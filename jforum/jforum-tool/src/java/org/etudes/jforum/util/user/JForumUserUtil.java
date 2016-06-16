/*********************************************************************************** 
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/user/JForumUserUtil.java $ 
 * $Id: JForumUserUtil.java 70458 2010-09-29 00:21:05Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008 Etudes, Inc. 
 * 
 * Portions completed before September 1, 2008 Copyright (c) 2004, 2005, 2006, 2007 Foothill College, ETUDES Project 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License. 
 **********************************************************************************/ 

package org.etudes.jforum.util.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.UserDAO;
import org.etudes.jforum.dao.generic.SakaiUserDAO;
import org.etudes.jforum.dao.generic.UserOrderComparator;
import org.etudes.jforum.entities.KarmaStatus;
import org.etudes.jforum.entities.User;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.api.UserNotDefinedException;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.component.cover.ComponentManager;

/**
 * <p>
 * SakaiJForumUtils provides utility functions for sakai-JForum
 * </p>
 *
 * @author murthyt
 * 1/12/06 - Mallika - adding code to sort users list
 * 5/10/06 - Howie - updated updateJFUser() to set password (dummy) for the jforum user
 * 07/17/06 - Murthy - updated for sakai's Eid and user Id
* 9/12/06 - Mallika - commenting updateFacilitator and updateParticipant to see if it improves performance
 * This means roles will NOT get updated when the user clicks on Member listing
 * 9/12/06 - Mallika - adding new method so that method would be invoked from members
 * 10/3/06 - Mallika - changing selectById to selectUserById
 * 10/5/06 - Mallika - adding some code to further optimize first name check
 * 01/11/07 - Murthy - updated case for the condition in the method dropUsers() 
 */
/**
 * @author Murthy Tanniru
 * @version $Id: JForumUserUtil.java 70458 2010-09-29 00:21:05Z murthy@etudes.org $
 */
public class JForumUserUtil {
	private static Log logger = LogFactory.getLog(JForumUserUtil.class);
	public static final String ROLE_FACILITATOR="jforum.manage";
	public static final String ROLE_ADMIN="jforum.admin";
	public static final String ROLE_PARTICIPANT="jforum.member";
	private static final int MAX_USERS_CREATE = 100;
	private static Object obj = new Object();
	

	/**
	 * synchronizes the sakai site info with jforum user info
	 *
	 * @throws Exception
	 */
	public static List updateUsersInfo() throws Exception {
		/*Get all current users*/
		List users = DataAccessDriver.getInstance().newUserDAO().selectAll(0, 0);

		/*Step 1: find site users through sakai api's*/
		String currCourse_id = ToolManager.getCurrentPlacement().getContext();
		String realmId = "/site/" + currCourse_id;
		List allsiteusers = new ArrayList();
		//allsiteusers.addAll(RealmService.getRealm(realmId).getUsers());
		Site sakSite;
		try {
			sakSite = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
		} catch (IdUnusedException e) {
			logger.error(e.getMessage(), e);
		}
		//Set sakUsers = sakSite.getUsers();

		//<<<Murthy 07/13/2006
		Set sakUsers = null;
		AuthzGroup realm;
	    try
	    {
	    	AuthzGroupService authz = ComponentManager.get(AuthzGroupService.class);
	      realm = authz.getAuthzGroup("/site/" + ToolManager.getCurrentPlacement().getContext());
	      //UserDirectoryService.getUsers(realm.getUsers());
	      sakUsers = realm.getUsers();

	      //remove users that are delete in sakai
	      Iterator sakUserIterator = sakUsers.iterator();
	      while (sakUserIterator.hasNext()){
	        String userId = (String) sakUserIterator.next();
	        try{
	        	org.sakaiproject.user.api.User user = UserDirectoryService.getUser(userId);
	        }catch (UserNotDefinedException e){
	        	/*logger.warn("updateUsersInfo() : The " +
	        			"user with id : "+ userId +" is not in sakai");*/
	        	sakUserIterator.remove();
	        }
	      }

	    }catch (GroupNotDefinedException e){
	    	logger.error(e.getMessage(), e);
	    }
	    //>>>Murthy 07/13/2006

		allsiteusers.addAll(sakUsers);
		List copyallsiteusers = new ArrayList();
		Iterator copyiter = allsiteusers.iterator();
		while (copyiter.hasNext()) {
			String copyname = ((String) copyiter.next()).toLowerCase();
			copyallsiteusers.add(copyname);
		}

		/*for existing site users*/
		List exissiteusers = new ArrayList();
		/*add new members
		  Step 2: find new users*/
		Iterator iter = users.iterator();
		while (iter.hasNext()) {
			User checkusr = (User) iter.next();

			//if (allsiteusers.contains(checkusr.getUsername().toLowerCase())) {
			//07/17/2006 Murthy
			if (allsiteusers.contains(checkusr.getSakaiUserId().toLowerCase())) {
				/*2a. remove from allsiteusers as user is existing*/
				allsiteusers.remove(checkusr.getSakaiUserId().toLowerCase());

				/*add existing site users*/
				exissiteusers.add(checkusr);
			}
		}

		/*create new users*/
		//createNewUsers(users, allsiteusers);
		// create 100 users maximum at a time
		if (allsiteusers.size() > MAX_USERS_CREATE)
		{
			allsiteusers = allsiteusers.subList(0, MAX_USERS_CREATE);
		}
		List newUsers = createNewUsers(allsiteusers);
		
		users.addAll(newUsers);

		/*update existing site users to the current sakai role*/
		updateExistingUsers(exissiteusers);

     	/*dropped students remove from users and delete their association with
		groups*/
		User adminuser = dropUsers(users, copyallsiteusers);

		if (adminuser != null) users.remove(adminuser);
		//Mallika - new code beg
        Collections.sort(users,new UserOrderComparator());


		//Mallika - new code end
		return users;
	}
	
	public static List updateMembersInfo() throws Exception {
		return updateMembersInfo(false);
	}

    public static List updateMembersInfo(boolean getTotalPosts) throws Exception {
		/*Get all current users*/
				//List users = DataAccessDriver.getInstance().newUserDAO().selectAll(0, 0);
    			List users = null;
    			
    			if (getTotalPosts)
    				users = DataAccessDriver.getInstance().newUserDAO().selectAllSiteUsersWithTotalPosts();
    			else
    				users = DataAccessDriver.getInstance().newUserDAO().selectAllSiteUsers();

				/*Step 1: find site users through sakai api's*/
				String currCourse_id = ToolManager.getCurrentPlacement().getContext();
				String realmId = "/site/" + currCourse_id;
				List allsiteusers = new ArrayList();
				//allsiteusers.addAll(RealmService.getRealm(realmId).getUsers());
				Site sakSite;
				try {
					sakSite = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
				} catch (IdUnusedException e) {
					logger.error(e.getMessage(), e);
				}
				//Set sakUsers = sakSite.getUsers();

				//<<<Murthy 07/13/2006
				Set sakUsers = null;
				AuthzGroup realm;
			    try
			    {
			    	AuthzGroupService authz = ComponentManager.get(AuthzGroupService.class);
			      realm = authz.getAuthzGroup("/site/" + ToolManager.getCurrentPlacement().getContext());
			      //UserDirectoryService.getUsers(realm.getUsers());
			      sakUsers = realm.getUsers();

			      //remove users that are delete in sakai
			      Iterator sakUserIterator = sakUsers.iterator();
			      while (sakUserIterator.hasNext()){
			        String userId = (String) sakUserIterator.next();
			        try{
			        	org.sakaiproject.user.api.User user = UserDirectoryService.getUser(userId);
			        }catch (UserNotDefinedException e){
			        	/*logger.warn("updateUsersInfo() : The " +
			        			"user with id : "+ userId +" is not in sakai");*/
			        	sakUserIterator.remove();
			        }
			      }

			    }catch (GroupNotDefinedException e){
			    	logger.error(e.getMessage(), e);
			    }
			    //>>>Murthy 07/13/2006

				allsiteusers.addAll(sakUsers);
				List copyallsiteusers = new ArrayList();
				Iterator copyiter = allsiteusers.iterator();
				while (copyiter.hasNext()) {
					String copyname = ((String) copyiter.next()).toLowerCase();
					copyallsiteusers.add(copyname);
				}

				/*for existing site users*/
				List exissiteusers = new ArrayList();
				/*add new members
				  Step 2: find new users*/
				Iterator iter = users.iterator();
				while (iter.hasNext()) {
					User checkusr = (User) iter.next();

					//if (allsiteusers.contains(checkusr.getUsername().toLowerCase())) {
					//07/17/2006 Murthy
					if (allsiteusers.contains(checkusr.getSakaiUserId().toLowerCase())) {
						/*2a. remove from allsiteusers as user is existing*/
						allsiteusers.remove(checkusr.getSakaiUserId().toLowerCase());

						/*add existing site users*/
						exissiteusers.add(checkusr);
					}
				}

				/*create new users*/
				//createNewUsers(users, allsiteusers);
				// create 100 users maximum at a time
				if (allsiteusers.size() > MAX_USERS_CREATE)
				{
					allsiteusers = allsiteusers.subList(0, MAX_USERS_CREATE);
				}
				List newUsers = createNewUsers(allsiteusers);
				users.addAll(newUsers);

				/*update existing site users to the current sakai role*/
				updateExistingMembers(exissiteusers);

		     	/*dropped students remove from users and delete their association with
				groups*/
				User adminuser = dropUsers(users, copyallsiteusers);

				if (adminuser != null) users.remove(adminuser);
				//Mallika - new code beg
		        Collections.sort(users,new UserOrderComparator());


				//Mallika - new code end
		return users;
	}

	/**
	 * update JForum user
	 * @throws Exception
	 */
	public static void updateJFUser(User jfuser)throws Exception{
		try {
			//get JForum user name
			//String username = jfuser.getUsername();

			//org.sakaiproject.user.api.User sakUser = UserDirectoryService.getUser(username);
			org.sakaiproject.user.api.User sakUser = UserDirectoryService.getUser(jfuser.getSakaiUserId());
			//logger.info("updateUser() :"+ sakUser.toString());
			if (sakUser != null){
				String sakUsrEmail = sakUser.getEmail();
				String sakUsrFname = sakUser.getFirstName();
				String sakUsrLname = sakUser.getLastName();

				String jforumUsrEmail = jfuser.getEmail();
				String jforumUsrFname = jfuser.getFirstName();
				String jforumUsrLname = jfuser.getLastName();

				boolean changed = false, fnameblank = false;
				//if sakai Eid is changed
				if(!sakUser.getEid().equalsIgnoreCase(jfuser.getUsername())){
					jfuser.setUsername(sakUser.getEid());
					changed = true;
				}

				/*if sakai user first name and last name are blank then change the
				jforum user last name as Guest*/
				//first name
				if (sakUsrFname != null && sakUsrFname.trim().length() > 0) {
					if (jforumUsrFname != null){
						//compare first names
						if (!jforumUsrFname.equals(sakUsrFname)){
							jfuser.setFirstName(sakUsrFname);
							changed = true;
						}
					}else{
						jfuser.setFirstName(sakUsrFname);
						changed = true;
					}
				}else{
					  fnameblank = true;
					  jfuser.setFirstName("");
					  changed = true;
				}

				//last name
				if (sakUsrLname != null && sakUsrLname.trim().length() > 0) {
					if (jforumUsrLname != null){
						//compare last names
						if (!jforumUsrLname.equals(sakUsrLname)){
							jfuser.setLastName(sakUsrLname);
							changed = true;
						}
					}else{
						jfuser.setLastName(sakUsrLname);
						changed = true;
					}
				}else{
					if (fnameblank){
						jfuser.setLastName("Guest User");
					}else{
						jfuser.setLastName("");
					}
					changed = true;
				}

				//email
				if (sakUsrEmail != null && sakUsrEmail.trim().length() > 0) {
					if (jforumUsrEmail != null){
						if (!jforumUsrEmail.equals(sakUsrEmail)){
							jfuser.setEmail(sakUsrEmail);
							changed = true;
						}
					}else{
						jfuser.setEmail(sakUsrEmail);
						changed = true;
					}
				}else{
					if (jforumUsrEmail != null && jforumUsrEmail.trim().length() != 0){
						jfuser.setEmail("");
						changed = true;
					}
				}

				if (changed){
					// <<<< 5/10/06 Howie - set with dummy password if null
					if (jfuser.getLang() == null || jfuser.getLang().trim().length() == 0) {
						//logger.warn("Locale is null! " + sakUser.getEid());
						jfuser.setLang("");
					}

					//password
					String jforumUsrPassword = jfuser.getPassword();
					if (jforumUsrPassword == null || jforumUsrPassword.trim().length() < 1) {
						jfuser.setPassword("passwd");
					}

					// >>>> 5/10/06 Howie

					UserDAO dao = DataAccessDriver.getInstance().newUserDAO();
					dao.updateSakaiAccountDetails(jfuser);
				}
			}
		} catch (UserNotDefinedException e) {
			//if (logger.isWarnEnabled()) logger.warn(e, e);
		}
	}

	/**
	 * drops the users
	 * @param users
	 * @param copyallsiteusers
	 * @return
	 * @throws Exception
	 */
	private static User dropUsers(List users, List copyallsiteusers) throws Exception {
		UserDAO newusrdao = DataAccessDriver.getInstance().newUserDAO();

		Iterator dropiter = users.iterator();
		User adminuser = null;
		while (dropiter.hasNext()) {
			User dropusr = (User) dropiter.next();
			//String dropusername = dropusr.getUsername().toLowerCase();
			String dropSakUsrId = dropusr.getSakaiUserId();
			if (dropSakUsrId.equalsIgnoreCase("admin"))
				adminuser = dropusr;
			if (!copyallsiteusers.contains(dropSakUsrId.toLowerCase())) {
				/*remove all groups associated with this user*/
				/*User dropthisuser = newusrdao.selectUserById(dropusr.getId());
				List usrParGrps = dropthisuser.getGroupsList();
				Iterator dropUsrIter = usrParGrps.iterator();
				while (dropUsrIter.hasNext()) {
					Group usrGrp = (Group) dropUsrIter.next();
					newusrdao.removeFromGroup(dropusr.getId(),
							new int[] { usrGrp.getId() });
				}*/
				dropiter.remove();
			}
		}
		return adminuser;
	}

	/**
	 * updates user groups
	 * @param realmId
	 * @param exissiteusers
	 * @throws UserNotDefinedException
	 * @throws Exception
	 */
	private static void updateExistingUsers(List exissiteusers) throws Exception {
		String currCourse_id = ToolManager.getCurrentPlacement().getContext();
		String realmId = "/site/" + currCourse_id;

		Iterator iterExisUsers = exissiteusers.iterator();
		//UserDAO newusrdao = DataAccessDriver.getInstance().newUserDAO();
		//SakaiUserDAO sakaiUserDAO = new SakaiUserDAO();
		while (iterExisUsers.hasNext()) {
			User exisusr = (User) iterExisUsers.next();

			//Role role = RealmService.getRealm(realmId).getUserRole(
			//		exisusr.getUsername().toLowerCase());
			updateJFUser(exisusr);

			//if user is admin he should belong to Facilitator group
			/*if (isSakaiAdmin(exisusr.getSakaiUserId())){
				updateFacilitator(exisusr);
				continue;
			}

			//if (role.contains("jforum.manage")) {
			if (isJForumFacilitator(exisusr.getSakaiUserId())) {
				 remove all exisitng groups if user belongs to other groups as
				 * Facilitator belongs to only one group(Facilitator group) and
				 * add the Facilitator group
				 
				updateFacilitator(exisusr);
			} //else if (role.contains("jforum.member")) {
			else if (isJForumParticipant(exisusr.getSakaiUserId())) {
				
				 * If user belongs to jforum.member get the exisitng user groups
				 * and if don't belong to any group add to Paticipant else check
				 * the groups if the user belongs to Facilitator if belongs
				 * remove the group leave the other groups(realistically if user
				 * belongs to Facilitator he should not have any other groups)
				 
				updateParticipant(exisusr);
			}*/
		}
	}

private static void updateExistingMembers(List exissiteusers) throws Exception {
		String currCourse_id = ToolManager.getCurrentPlacement().getContext();
		String realmId = "/site/" + currCourse_id;

		Iterator iterExisUsers = exissiteusers.iterator();
		//UserDAO newusrdao = DataAccessDriver.getInstance().newUserDAO();
		//SakaiUserDAO sakaiUserDAO = new SakaiUserDAO();
		while (iterExisUsers.hasNext()) {
			User exisusr = (User) iterExisUsers.next();

			//Role role = RealmService.getRealm(realmId).getUserRole(
			//		exisusr.getUsername().toLowerCase());
			updateJFUser(exisusr);

			//Mallika - 9/12/06 - commenting code below to improve
						//performance
			/*

			//if user is admin he should belong to Facilitator group
			if (isSakaiAdmin(exisusr.getSakaiUserId())){
				updateFacilitator(exisusr);
				continue;
			}

			//if (role.contains("jforum.manage")) {
			if (isJForumFacilitator(exisusr.getSakaiUserId())) {
				// remove all exisitng groups if user belongs to other groups as
				//  Facilitator belongs to only one group(Facilitator group) and
				// add the Facilitator group
				//updateFacilitator(exisusr);
			} //else if (role.contains("jforum.member")) {
			else if (isJForumParticipant(exisusr.getSakaiUserId())) {
				// If user belongs to jforum.member get the exisitng user groups
				// and if don't belong to any group add to Paticipant else check
				// the groups if the user belongs to Facilitator if belongs
				// remove the group leave the other groups(realistically if user
				// belongs to Facilitator he should not have any other groups)
				updateParticipant(exisusr);
			}*/
		}
	}




	/**
	 * @param newusrdao
	 * @param sakaiUserDAO
	 * @param exisusr
	 * @throws Exception
	 */
	/*private static void updateParticipant(User exisusr) throws Exception {
		UserDAO newusrdao = DataAccessDriver.getInstance().newUserDAO();
		SakaiUserDAO sakaiUserDAO = new SakaiUserDAO();
		int participantGrpId = sakaiUserDAO.findGroupId("Participant");
		int facilitatorGrpId = sakaiUserDAO.findGroupId("Facilitator");

		User existinguser = newusrdao.selectUserById(exisusr.getId());
		List usrExisGrps = existinguser.getGroupsList();
		Iterator usrExisGrpsIter = usrExisGrps.iterator();

		boolean participantGrp = false;
		while (usrExisGrpsIter.hasNext()) {
			Group usrGrp = (Group) usrExisGrpsIter.next();

			if (facilitatorGrpId == usrGrp.getId()) {
				remove facilitator group
				newusrdao.removeFromGroup(exisusr.getId(),
						new int[] { usrGrp.getId() });
			} else if (participantGrpId == usrGrp.getId()) {
				participant group existing
				participantGrp = true;
			}
		}

		User existingParuser = newusrdao.selectUserById(exisusr.getId());
		List usrParGrps = existingParuser.getGroupsList();
		if already in Participant group
		if (participantGrp) {
			do nothing and keep the exisitng groups as is
		} else {
			add Participant group
			if (usrParGrps.size() == 0)
				newusrdao.addToGroup(exisusr.getId(), new int[] { sakaiUserDAO
						.findGroupId("Participant") });
		}
	}*/

	/**
	 * @param newusrdao
	 * @param sakaiUserDAO
	 * @param exisusr
	 * @throws Exception
	 */
	/*private static void updateFacilitator(User exisusr) throws Exception {
		UserDAO newusrdao = DataAccessDriver.getInstance().newUserDAO();
		SakaiUserDAO sakaiUserDAO = new SakaiUserDAO();
		User existinguser = newusrdao.selectUserById(exisusr.getId());
		List usrExisGrps = existinguser.getGroupsList();
		Iterator usrExisGrpsIter = usrExisGrps.iterator();
		int facilitatorGrpId = sakaiUserDAO.findGroupId("Facilitator");
		boolean facGrp = false;
		while (usrExisGrpsIter.hasNext()) {
			Group usrGrp = (Group) usrExisGrpsIter.next();

			if (facilitatorGrpId != usrGrp.getId()) {
				newusrdao.removeFromGroup(exisusr.getId(),
						new int[] { usrGrp.getId() });
			} else
				facGrp = true;
		}

		if already in Facilitator group
		if (facGrp) {
			User existingfacuser = newusrdao
					.selectUserById(exisusr.getId());
			List usrFacGrps = existingfacuser.getGroupsList();

			if (usrFacGrps != null && usrFacGrps.size() > 1) {
				keep only one facilitator group
				Iterator usrFacGrpsIter = usrFacGrps.iterator();
				for (int i = 0; i < usrFacGrps.size() - 1; i++) {
					Group usrFacGrp = (Group) usrFacGrps.get(i);
					newusrdao.removeFromGroup(existingfacuser.getId(),
							new int[] { usrFacGrp.getId() });
				}
			}
		} else {
			add Facilitator group
			newusrdao.addToGroup(exisusr.getId(), new int[] { sakaiUserDAO
					.findGroupId("Facilitator") });
		}
	}*/

	/**
	 * creates new user if not existing in jforum and adds use to group
	 * @param users
	 * @param realmId
	 * @param allsiteusers
	 * @throws Exception
	 * @throws UserNotDefinedException
	 */
	//private void createNewUsers(List users, List allsiteusers) throws Exception, UserNotDefinedException {
	private static List createNewUsers(List allsiteusers){
		String currCourse_id = ToolManager.getCurrentPlacement().getContext();
		String realmId = "/site/" + currCourse_id;
		
		ArrayList users = new ArrayList();

		Iterator iter;
		/* Step 3 : create new users in jforum and add to users list*/
		if (logger.isDebugEnabled()) logger.debug("createNewUsers() : left out users"
				+ allsiteusers.toString());

		UserDAO newusrdao = DataAccessDriver.getInstance().newUserDAO();
		SakaiUserDAO sakaiUserDAO = new SakaiUserDAO();

		iter = allsiteusers.iterator();
		synchronized(obj){
			while (iter.hasNext()) {
				//String newusername = (String) iter.next();
				String newSakaiUserId = (String) iter.next();
				if (logger.isDebugEnabled()) logger.debug("createNewUsers() : creating jforum user for " + newSakaiUserId);
				//if user exists in jforum then associate user with group of this course
				//User jfusr = newusrdao.selectByName(newusername);
				try {
					User jfusr = newusrdao.selectBySakaiUserId(newSakaiUserId);
					if (jfusr != null) {
						/*associate with jforum group based on sakai site role*/
						//Role role = RealmService.getRealm(realmId).getUserRole(
						//newusername);
						//if (role.contains("jforum.manage")) {
						/*if (isJForumFacilitator(newSakaiUserId)) {
							newusrdao.addToGroup(jfusr.getId(), new int[] { sakaiUserDAO.findGroupId("Facilitator") });
							users.add(jfusr);
						} //else if (role.contains("jforum.member")) {
						else if (isJForumParticipant(newSakaiUserId)) {
							newusrdao.addToGroup(jfusr.getId(), new int[] { sakaiUserDAO.findGroupId("Participant") });
							users.add(jfusr);
						}*/
						//add as site user
						newusrdao.addToSite(jfusr.getId());
						//as the user is added to site the total posts should be zero
						//jfusr.setTotalPosts(0);
						users.add(jfusr);
					} else {
						/* 	else create new jforum user get sakai user attribs*/
						try {
							org.sakaiproject.user.api.User sakaiusr = UserDirectoryService
									.getUser(newSakaiUserId);
	
							/*create new jforum user*/
							User newusr = new User();
							newusr.setUsername(sakaiusr.getEid());
							newusr.setPassword("password");
							newusr.setEmail(sakaiusr.getEmail());
							newusr.setActive(1);
							if (sakaiusr.getFirstName() == null
									|| sakaiusr.getFirstName().length() == 0)
								newusr.setFirstName(" ");
							else
								newusr.setFirstName(sakaiusr.getFirstName());
							if (sakaiusr.getLastName() == null
									|| sakaiusr.getLastName().length() == 0)
								newusr.setLastName("Guest");
							else
								newusr.setLastName(sakaiusr.getLastName());
							newusr.setRegistrationDate(new Date());
							newusr.setTotalPosts(0);
							newusr.setKarma(new KarmaStatus());
							//07/17/2006 Murthy
							newusr.setSakaiUserId(sakaiusr.getId());
							//	newusr.setKarma(new Karma);
							newusrdao.addNewUser(newusr);
							//Role role = RealmService.getRealm(realmId).getUserRole(
							//		newusername);
	
							//if (role.contains("jforum.manage")) {
							/*if (isJForumFacilitator(newSakaiUserId)) {
								newusrdao.addToGroup(newusr.getId(), new int[] { sakaiUserDAO
										.findGroupId("Facilitator") });
							} //else if (role.contains("jforum.member")) {
							else if (isJForumParticipant(newSakaiUserId)) {
								newusrdao.addToGroup(newusr.getId(), new int[] { sakaiUserDAO
										.findGroupId("Participant") });
							}*/
							//add as site user
							newusrdao.addToSite(newusr.getId());
							users.add(newusr);
						} catch (UserNotDefinedException e) {
								//logger.error(".createNewUsers() "	+ e.toString());
						} 
					}
				} catch (Exception e) {
					logger.error(".createNewUsers() "	+ e.toString());
				}
			}
		}
		return users;
	}


	/**
	 * checks if user is sakai/jforum admin
	 * @param sakaiUserId
	 * @return
	 */
	public static boolean isSakaiAdmin(String sakaiUserId) {

			try {
				org.sakaiproject.user.api.User sakUser = UserDirectoryService.getUser(sakaiUserId);
				String thisSiteId = ToolManager.getCurrentPlacement().getContext();
				String realmName = "/site/" + thisSiteId;
				return SecurityService.unlock(sakUser, ROLE_ADMIN, realmName);
			} catch (UserNotDefinedException e) {
				return false;
			}


	}

	/**
	 * checks if user is jforum facilitator
	 * @param sakaiUserId
	 * @return true if jforum facilitator
	 * 		   false if not jforum facilitator
	 */
	public static boolean isJForumFacilitator(String sakaiUserId) {
		try {
			org.sakaiproject.user.api.User sakUser = UserDirectoryService.getUser(sakaiUserId);
			String thisSiteId = ToolManager.getCurrentPlacement().getContext();
			String realmName = "/site/" + thisSiteId;
		    return SecurityService.unlock(sakUser, ROLE_FACILITATOR, realmName);

		} catch (UserNotDefinedException e) {
			return false;
		}
	}

	/**
	 * checks if user is jforum participant
	 * @param sakaiUserId
	 * @return - true if jforum particpant
	 * 			 false if not jforum particpant
	 */
	public static boolean isJForumParticipant(String sakaiUserId) {
		try {
			org.sakaiproject.user.api.User sakUser = UserDirectoryService.getUser(sakaiUserId);
			String thisSiteId = ToolManager.getCurrentPlacement().getContext();
			String realmName = "/site/" + thisSiteId;
		    return SecurityService.unlock(sakUser, ROLE_PARTICIPANT, realmName);

		} catch (UserNotDefinedException e) {
			return false;
		}
	}
	
	/**
	 * checks if user is active in site
	 * @param userId
	 * @return - true if user is active
	 * 			 false if usr is not active 
	 */
	public static boolean isUserActive(String sakaiUserId){
		if (sakaiUserId != null && sakaiUserId.trim().length() > 0) {
			/*String role = AuthzGroupService.getUserRole(sakaiUserId.trim(), "/site/" + ToolManager.getCurrentPlacement().getContext());
			//user have no role for inactive and removed from site
			if (role == null)
				return false;
			else
				return true;*/
			if (isUserInSakai(sakaiUserId)) {
				try {
			    	AuthzGroupService authz = ComponentManager.get(AuthzGroupService.class);
			    	AuthzGroup authzGroup = authz.getAuthzGroup("/site/" + ToolManager.getCurrentPlacement().getContext());
					Member member = authzGroup.getMember(sakaiUserId);
					if (member != null)
						return member.isActive();
				} catch (GroupNotDefinedException e) {
					if (logger.isErrorEnabled()) logger.error(".isUserActive() "	+ e.toString());
				}
			}
			
		}
		return false;
	}
	
	/**
	 * checks if user is in sakai
	 * @param userId
	 * @return - true if user is in sakai
	 * 			 false if user is removed or not in sakai 
	 */
	public static boolean isUserInSakai(String sakaiUserId){
		try {
			UserDirectoryService.getUser(sakaiUserId);
			
			return true;
		} catch (UserNotDefinedException e) {
			return false;
		}
	}
	
	/**
	 * get sakai user
	 * @param sakaiUserId		sakai user id
	 * @return sakai user object
	 */
	public static org.sakaiproject.user.api.User getSakaiUser(String sakaiUserId){
		try {
			return UserDirectoryService.getUser(sakaiUserId);

		} catch (UserNotDefinedException e) {
			return null;
		}
	}
	
	/**
	 * checks if user is memeber of the site
	 * @param userId
	 * @return - true if user is in site
	 * 			 false if user is removed or not in site 
	 */
	public static boolean isUserInSite(String sakaiUserId) {
		try {
	    	AuthzGroupService authz = ComponentManager.get(AuthzGroupService.class);
	    	AuthzGroup authzGroup = authz.getAuthzGroup("/site/" + ToolManager.getCurrentPlacement().getContext());
			Member member = authzGroup.getMember(sakaiUserId);
			if (member != null)
				return true;
		} catch (GroupNotDefinedException e) {
			if (logger.isErrorEnabled()) logger.error(".isUserInSite() "	+ e.toString());
		}
		return false;
	}
}
