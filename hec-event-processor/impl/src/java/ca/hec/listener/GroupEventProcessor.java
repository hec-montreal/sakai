package ca.hec.listener;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ca.hec.logic.EventProcessorLogic;
import ca.hec.logic.SakaiProxyImpl;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.sakaiproject.authz.api.Member;
import org.sakaiproject.event.api.Event;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;

public class GroupEventProcessor implements EventProcessor {

	private static Logger logger = Logger.getLogger(GroupEventProcessor.class);

	private static final String EVENT_ID = "site.upd.grp.mbrshp";

	@Setter protected EventProcessorLogic eventProcessorLogic;

	@Setter protected SakaiProxyImpl sakaiProxy;

	public String getEventIdentifer() {
		return EVENT_ID;
	}

	synchronized public void processEvent(Event event) {
		
		if(logger.isDebugEnabled()) {
			logger.debug("\n\n\n=============================================================\n" + event  
					+ "\n=============================================================\n\n\n");
		}

		boolean groupHasChanges = false;
		String groupId = event.getResource();
		String siteId = event.getContext();
		
		Site site = sakaiProxy.getSite(siteId);

		if(site == null) {
			logger.warn(String.format("The site %s not exists.", siteId));
			return;
		}
		Group group = site.getGroup(groupId);

		if(group == null) {
			logger.warn(String.format("The group %s not exists or is not in the site %s.", groupId, siteId));
			return;
		}
		
		logger.debug(String.format("The site %s and the group %s are valid, adding instructors to the group automatically", site.getId(), group.getId()));
		
		if(group.getMembers().isEmpty()) {
			logger.debug(String.format("The group %s is empty, nothing to do", group.getId()));
			return;
		}else {
			//Get the instructor roles to be added automatically
			List<String> instructorRoles = Arrays.asList(sakaiProxy.getConfigParams(sakaiProxy.CONFIG_HEC_INSTRUCTOR_ROLES_TO_ENROLE));
			if(instructorRoles.isEmpty()) {
				instructorRoles = Arrays.asList(sakaiProxy.DEFAULT_CONFIG_HEC_INSTRUCTOR_ROLES_TO_ENROLE);
			}

			//Get the student roles to identify the first student
			List<String> studentRoles = Arrays.asList(sakaiProxy.getConfigParams(sakaiProxy.CONFIG_HEC_STUDENT_ROLES));
			if(studentRoles.isEmpty()) {
				studentRoles = Arrays.asList(sakaiProxy.DEFAULT_CONFIG_HEC_STUDENT_ROLES);
			}

			Member firstStudent = null;
			logger.debug("Finding the first member with role student");

			for(Member member : group.getMembers()) {
				if(studentRoles.contains(member.getRole().getId())) {
					firstStudent = member;
					break;
				}
			}

			//If there are no students, nothing to do
			if(firstStudent == null) {
				logger.debug("Not found any student in the group, nothing to do");
				return;
			}else {
				logger.debug(String.format("Found a student %s in the group", firstStudent.getUserEid()));
				//Get all the groups/sections of the first student
				Collection<Group> groupsWithMember = site.getGroupsWithMember(firstStudent.getUserId());
				logger.debug("Getting groups with this student");
				for(Group g : groupsWithMember) {
					//Exclude the own group
					if(g.getId().equals(group.getId())) {
						continue;
					}
					//Exclude groups, only consider sections
					if(g.getProperties() != null && StringUtils.isEmpty(g.getProperties().getProperty("sections_eid"))){
						continue;
					}
					
					logger.debug(String.format("Identified a section %s of the student, getting instructors", g.getTitle()));
					
					for(Member member : g.getMembers()) {
						if(instructorRoles.contains(member.getRole().getId())) {
							logger.debug(String.format("Found a instructor %s in the section %s, adding him to the group", member.getUserEid(), g.getTitle()));
							group.addMember(member.getUserId(), member.getRole().getId(), true, false);
							groupHasChanges=true;
						}
					}
					
				}
			}
			//Only save the group if there is any change
			if(groupHasChanges){
				logger.debug(String.format("Saving the group of the site %s", site.getId()));
				if(sakaiProxy.saveSiteMembership(site)) {
					logger.info(String.format("Group %s updated successfully with the instructors of the sections", group.getId()));
				}else {
					logger.error(String.format("Error updating the group %s automatically with the instructors", group.getId()));
				}
			}
		}
	}

	public void init() {
		this.eventProcessorLogic.registerEventProcessor(EVENT_ID, this);
	}
}
