
package ca.hec.logic;

import java.util.List;
import java.util.Observer;
import java.util.ArrayList;
import java.util.Arrays;

import ca.hec.listener.EventProcessor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.event.api.EventTrackingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.thread_local.api.ThreadLocalManager;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;

public class SakaiProxyImpl {

	public static final String CONFIG_DISABLE_HEC_EVENTPROCESSING = "hec.eventprocessing.disable";
	public static final String CONFIG_HEC_EVENTPROCESSING_LISTENERS = "hec.eventprocessing.listeners";
	public static final String CONFIG_HEC_INSTRUCTOR_ROLES_TO_ENROLE = "hec.eventprocessing.groupeventprocessor.instructor.roles";
	public static final String CONFIG_HEC_STUDENT_ROLES = "hec.eventprocessing.groupeventprocessor.student.roles";
	public static final String[] DEFAULT_CONFIG_HEC_INSTRUCTOR_ROLES_TO_ENROLE = {"Instructor","TA", "Teaching Assistant","maintain"};
	public static final String[] DEFAULT_CONFIG_HEC_STUDENT_ROLES = {"Student","access"};

	private static final Logger logger = Logger.getLogger(SakaiProxyImpl.class);

	public void addLocalEventListener(Observer observer) {
		this.eventTrackingService.addLocalObserver(observer);
	}

	public boolean getConfigParam(String param, boolean dflt) {
		return serverConfigurationService.getBoolean(param, dflt);
	}

	public String getConfigParam(String param, String dflt) {
		return serverConfigurationService.getString(param, dflt);
	}
	
	public String[] getConfigParams(String param) {
		String[] strings = serverConfigurationService.getStrings(param);
		if(strings != null) {
			return strings;
		}
		else {
			return new String[0];
		}
	}

	public String getServerId() {
		return this.serverConfigurationService.getServerId();
	}
	
	public String getPortalUrl(){
		return serverConfigurationService.getPortalUrl();
	}

	public Site getSite(String siteId) {
		Site site = null;
		try {
			site = this.siteService.getSite(siteId);
		} catch (IdUnusedException e) {
			logger.warn("Unable to get site for siteId: " + siteId);
		}
		return site;
	}

	public String getSiteReference(String siteId)
	{
		return siteService.siteReference(siteId);
	}

	public boolean isSitePublished(String siteId) {
		Site site = getSite(siteId);
		return site != null? site.isPublished(): false;
	}

	public Group getGroupByTitle(Site site, String groupTitle) {

		if(site == null || StringUtils.isEmpty(groupTitle)) {
			return null;
		}
		
		for(Group group : site.getGroups()) {
			if(groupTitle.equals(group.getTitle()) || groupTitle.equals(group.getDescription())) {
				return group;
			}
		}
		return null;
	}
	
	public boolean saveSiteMembership(Site site) {
		try {
			siteService.saveGroupMembership(site);
			return true;
			} catch (Exception e) {
				logger.error(String.format("Fatal error saving the group membership of the group %s", site.getId()));
				return false;
			}
		}

	public void pushSecurityAdvisor(SecurityAdvisor securityAdvisor) {
		this.securityService.pushAdvisor(securityAdvisor);

	}

	public void popSecurityAdvisor(SecurityAdvisor securityAdvisor) {
		this.securityService.popAdvisor(securityAdvisor);

	}

	public void startAdminSession() {
		logger.debug("Creating session: EventProcessor");
		Session session = this.sessionManager.startSession("EventProcessor");
		session.setUserId("admin");
		session.setUserEid("admin");
		this.sessionManager.setCurrentSession(session);
	}

	/**
	 * Remove from the thread-local cache all items bound to the current thread.
	 */
	public void clearThreadLocalCache() {
		this.threadLocalManager.clear();
	}

	/**
	 * check with the server configuration whether the HEC event process thread should be disabled or not
	 * @return
	 */
	public boolean isEventProcessingThreadDisabled()
	{
		return serverConfigurationService.getBoolean(CONFIG_DISABLE_HEC_EVENTPROCESSING, false);
	}
	
	public boolean isEventProcessingListenerEnabled(EventProcessor listener) {
		String listeners = serverConfigurationService.getString(CONFIG_HEC_EVENTPROCESSING_LISTENERS, "*");
		if("*".equals(listeners)) {
			return true;
		}
		if(StringUtils.isNotBlank(listeners)) {
			List<String> listenersList = new ArrayList(Arrays.asList(listeners.split(",")));
			return listenersList.contains(listener.getClass().getSimpleName());
		}
		return false;
	}

	/************************************************************************
	 * Spring-injected classes
	 ************************************************************************/

	@Setter private SessionManager sessionManager;

	@Setter private SecurityService securityService;
	
	@Setter private EventTrackingService eventTrackingService;

	@Setter private ServerConfigurationService serverConfigurationService;

	@Setter private SiteService siteService;

	@Setter protected ThreadLocalManager threadLocalManager;

	/************************************************************************
	 * init() and destroy()
	 ************************************************************************/

	/**
	 * init - perform any actions required here for when this bean starts up
	 */
	public void init() {
		logger.info("init");
	}

}
