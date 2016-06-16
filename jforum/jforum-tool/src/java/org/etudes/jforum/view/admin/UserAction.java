/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/admin/UserAction.java $ 
 * $Id: UserAction.java 61928 2009-07-21 22:29:16Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008 Etudes, Inc. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 * Portions completed before July 1, 2004 Copyright (c) 2003, 2004 Rafael Steil, All rights reserved, licensed under the BSD license. 
 * http://www.opensource.org/licenses/bsd-license.php 
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met: 
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following disclaimer. 
 * 2) Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution. 
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE 
 ***********************************************************************************/
package org.etudes.jforum.view.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

//import net.jforum.dao.GroupDAO;
//import net.jforum.dao.security.UserSecurityDAO;
//import net.jforum.repository.SecurityRepository;
///import net.jforum.security.PermissionControl;
//import net.jforum.security.XMLPermissionControl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.UserDAO;
import org.etudes.jforum.dao.generic.CourseGroup;
import org.etudes.jforum.dao.generic.CourseGroupDAO;
import org.etudes.jforum.dao.generic.GenericUserDAO;
import org.etudes.jforum.dao.generic.SakaiUserDAO;
import org.etudes.jforum.entities.Group;
import org.etudes.jforum.entities.KarmaStatus;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.TreeGroup;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.preferences.TemplateKeys;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.etudes.jforum.view.forum.common.UserCommon;
import org.etudes.jforum.view.forum.common.ViewCommon;
import org.sakaiproject.authz.api.AuthzGroup;
import org.sakaiproject.authz.api.GroupNotDefinedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.cover.UserDirectoryService;


/**
 * @author Rafael Steil
 * @version $Id: UserAction.java 61928 2009-07-21 22:29:16Z murthy@etudes.org $
 * 10/6/05 - Mallika - adding method to search by user's first or last name
 * 11/01/05 - Murthy - updated groupsSave() to get only groups of a site while
 * 						updating the groups for a user
 * 11/08/05 - Murthy - added updateUsersInfo() to synchronize the jforum users with 
 * 						sakai users and roles
 */
public class UserAction extends AdminCommand 
{
	private static Log logger = LogFactory.getLog(UserAction.class);
	// Listing
	public void list() throws Exception
	{
		if (logger.isDebugEnabled()) logger.debug("UserAction - list");
		//11/08/05 - Murthy synchronize the jforum users with sakai users and roles
		//this.updateUsersInfo();
		//SakaiJForumUtils sakaiJForumUtils = new SakaiJForumUtils();
		JForumUserUtil.updateUsersInfo();
		
		int start = this.preparePagination(DataAccessDriver.getInstance().newUserDAO().getTotalUsers());
		// int usersPerPage = SystemGlobals.getIntValue(ConfigKeys.USERS_PER_PAGE);
		int usersPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.USERS_PER_PAGE);
		
		this.context.put("users", DataAccessDriver.getInstance().newUserDAO().selectAll(start ,usersPerPage));
		
	
		this.commonData();
	}
	
	private int preparePagination(int totalUsers)
	{
		int start = ViewCommon.getStartPage();
		// int usersPerPage = SystemGlobals.getIntValue(ConfigKeys.USERS_PER_PAGE);
		int usersPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.USERS_PER_PAGE);
		
		this.context.put("totalPages", new Double(Math.ceil( (double)totalUsers / usersPerPage )));
		this.context.put("recordsPerPage", new Integer(usersPerPage));
		this.context.put("totalRecords", new Integer(totalUsers));
		this.context.put("thisPage", new Double(Math.ceil( (double)(start + 1) / usersPerPage )));
		this.context.put("start", new Integer(start));
		
		return start;
	}
	
	private void commonData() throws Exception
	{
		this.context.put("selectedList", new ArrayList());
		this.context.put("groups", new TreeGroup().getNodes());
		this.setTemplateName(TemplateKeys.USER_ADMIN_COMMON);
		this.context.put("searchAction", "list");
		this.context.put("searchId", new Integer(-1));
	}
	
	/*public void groupSearch() throws Exception
	{
		final int groupId = this.request.getIntParameter("group_id");
		if (groupId == 0) {
			this.list();
			return;
		}
		
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();
		
		int start = this.preparePagination(um.getTotalUsersByGroup(groupId));
		int usersPerPage = SystemGlobals.getIntValue(ConfigKeys.USERS_PER_PAGE);
		
		this.commonData();
		
		this.context.put("selectedList", new ArrayList() {{ add(new Integer(groupId)); }});
		this.context.put("searchAction", "groupSearch");
		this.context.put("users", um.selectAllByGroup(groupId, start, usersPerPage));
		this.context.put("searchId", new Integer(groupId));
	}*/
	
	/*public void search() throws Exception
	{
		List users = new ArrayList();
		String search = this.request.getParameter("username");
		String group = this.request.getParameter("group");
		
		if (search != null && !"".equals(search)) {
			users = DataAccessDriver.getInstance().newUserDAO().findByName(search, false);
			
			this.commonData();
			
			this.context.put("users", users);
			this.context.put("search", search);
			this.context.put("start", new Integer(1));
		}
		else if (!"0".equals(group)) {
			this.groupSearch();
			return;
		}
		else {
			this.list();
			return;
		}
	}*/
	
	//Mallika's new code beg
	/*public void searchFlname() throws Exception
	{
		List users = new ArrayList();
		String search = this.request.getParameter("username");
		String group = this.request.getParameter("group");
		
		if (search != null && !"".equals(search)) {
			users = DataAccessDriver.getInstance().newUserDAO().findByFlName(search, false);
			
			this.commonData();
			
			this.context.put("users", users);
			this.context.put("search", search);
			this.context.put("start", new Integer(1));
		}
		else if (!"0".equals(group)) {
			this.groupSearch();
			return;
		}
		else {
			this.list();
			return;
		}
	}*/
	//Mallika's new code end
	
	// Permissions
	/*public void permissions() throws Exception
	{
		int id = this.request.getIntParameter("id");
		
		User user = DataAccessDriver.getInstance().newUserDAO().selectById(id);
		
		UserSecurityDAO umodel = DataAccessDriver.getInstance().newUserSecurityDAO();
		PermissionControl pc = new PermissionControl();
		pc.setSecurityModel(umodel);
		pc.setRoles(umodel.loadRoles(user));
		
		List sections = new XMLPermissionControl(pc).loadConfigurations(
				SystemGlobals.getValue(ConfigKeys.CONFIG_DIR) + "/permissions.xml");
		this.context.put("sections", sections);
		this.context.put("user", user);
		this.setTemplateName(TemplateKeys.USER_ADMIN_PERMISSIONS);
	}*/
	
	// Permissions Save
	/*public void permissionsSave() throws Exception
	{
		int id = this.request.getIntParameter("id");
		User user = DataAccessDriver.getInstance().newUserDAO().selectById(id);
		
		UserSecurityDAO umodel = DataAccessDriver.getInstance().newUserSecurityDAO();
		PermissionControl pc = new PermissionControl();
		pc.setSecurityModel(umodel);

		new PermissionProcessHelper(pc, user.getId()).processData();
		
		// Reload it
		pc.setRoles(umodel.loadRoles(user));

		// Update Security Repository
		SecurityRepository.remove(user.getId());
		SecurityRepository.add(user.getId(), pc);
		
		this.list();
	}*/
	
	public void edit() throws Exception
	{
		int userId = this.request.getIntParameter("id");	
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();
		User u = um.selectById(userId);
		
		this.context.put("u", u);
		this.context.put("action", "editSave");		
		this.setTemplateName(TemplateKeys.USER_ADMIN_EDIT);
		this.context.put("admin", true);
	}
	
	public void editSave() throws Exception
	{
		int userId = this.request.getIntParameter("user_id");
		UserCommon.saveUser(userId);

		this.list();
	}

	// Delete
	public void delete() throws Exception
	{
		String ids[] = this.request.getParameterValues("user_id");
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();
		
		if (ids != null) {
			for (int i = 0; i < ids.length; i++) {
				
				int user = Integer.parseInt(ids[i]);
				if (um.isDeleted(user)){
					um.undelete(user);
				} 
				else {
					um.delete(user);
				}
			}
		}
		
		this.list();
	}
	
	// Groups
	/*public void groups() throws Exception
	{
		int userId = this.request.getIntParameter("id");
		
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();
		GroupDAO gm = DataAccessDriver.getInstance().newGroupDAO();
		
		User u = um.selectById(userId);
		
		List selectedList = new ArrayList();
		for (Iterator iter = u.getGroupsList().iterator(); iter.hasNext(); ) {
			selectedList.add(new Integer(((Group)iter.next()).getId()));
		}
		
		this.context.put("selectedList", selectedList);
		this.context.put("groups", new TreeGroup().getNodes());
		this.context.put("user", u);
		this.context.put("userId", new Integer(userId));
		this.setTemplateName(TemplateKeys.USER_ADMIN_GROUPS);
		this.context.put("groupFor", I18n.getMessage("User.GroupsFor", new String[] { u.getUsername() }));
	}*/
	
	// Groups Save
	/*public void groupsSave() throws Exception
	{
		int userId = this.request.getIntParameter("user_id");
		
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();
		//GroupDAO gm = DataAccessDriver.getInstance().newGroupDAO();
		//10/01/05 Murthy - updated to get only groups of a site while
		//					updating the groups for a user
		CourseGroupDAO cgDAO = DataAccessDriver.getInstance().newCourseGroupDAO();
		
		// Remove the old groups
		//List allGroupsList = gm.selectAll();
		List allGroupsList = cgDAO.selectAll();
		int[] allGroups = new int[allGroupsList.size()];
		
		int counter = 0;
		for (Iterator iter = allGroupsList.iterator(); iter.hasNext(); counter++) {
			//Group g = (Group)iter.next();
			CourseGroup cg = (CourseGroup) iter.next();
			
			//allGroups[counter] = g.getId();
			allGroups[counter] = cg.getGroupId();
		}
		
		um.removeFromGroup(userId, allGroups);
		
		// Associate the user to the selected groups
		String[] selectedGroups = this.request.getParameterValues("groups");
		int[] newGroups = new int[selectedGroups.length];
		
		for (int i = 0; i < selectedGroups.length; i++) {
			newGroups[i] = Integer.parseInt(selectedGroups[i]);
		}
		
		um.addToGroup(userId, newGroups);
		SecurityRepository.remove(userId);
		
		this.list();
	}*/
	
	/**
	 * synchronizes the sakai site info with jforum user info
	 * @throws Exception
	 */
	/*private void updateUsersInfo() throws Exception
	{
		//int start = this.preparePagination(DataAccessDriver.getInstance().newUserDAO().getTotalUsers());
		//int usersPerPage = SystemGlobals.getIntValue(ConfigKeys.USERS_PER_PAGE);

		//List users = DataAccessDriver.getInstance().newUserDAO().selectAll(start ,usersPerPage);
		//11/07/05 Murthy - Get all current users
		List users = DataAccessDriver.getInstance().newUserDAO().selectAll(0 , 0);

		//added by rashmi 09-15-05
		//Step 1: find site users through sakai api's
		String currCourse_id= ToolManager.getCurrentPlacement().getContext();
		//String realmId = "/site/"+currCourse_id;
		List allsiteusers = new ArrayList();
		//allsiteusers.addAll(RealmService.getRealm(realmId).getUsers());
		Site sakSite = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
		//Set sakUsers = sakSite.getUsers();
		
		//<<<Murthy 07/13/2006
		Set sakUsers = null;
		AuthzGroup realm;
	    try
	    {
	      realm = AuthzGroupService.getAuthzGroup("/site/" + ToolManager.getCurrentPlacement().getContext());
	      //UserDirectoryService.getUsers(realm.getUsers());
	      sakUsers = realm.getUsers();
	    }catch (GroupNotDefinedException e){
	    	logger.error(e.getMessage(), e);
	    }
	    //>>>Murthy 07/13/2006
	    
		allsiteusers.addAll(sakUsers);
		
		if (logger.isDebugEnabled()) logger.debug("member listing all sakai course members" + allsiteusers.size() + allsiteusers.toString()+allsiteusers.get(0).getClass());
		List copyallsiteusers = new ArrayList();
		Iterator copyiter = allsiteusers.iterator();
		while(copyiter.hasNext())
		{
			String copyname = ((String)copyiter.next()).toLowerCase();
			copyallsiteusers.add(copyname);
		}

		//11/07/05 Murthy - for existing site users
		List exissiteusers = new ArrayList();
	//		 add new members added
		// Step 2: find new users
		Iterator iter =users.iterator();
		while(iter.hasNext())
		{
			User checkusr = (User)iter.next();

			if (allsiteusers.contains(checkusr.getUsername().toLowerCase()))
			{
				//2a. remove from allsiteusers as user is existing
				allsiteusers.remove(checkusr.getUsername().toLowerCase());

				////11/07/05 Murthy - add existing site users
				exissiteusers.add(checkusr);
			}
		}
		// Step 3 : create new users in jforum and add to users list
		if (logger.isDebugEnabled()) logger.debug("left out users" + allsiteusers.toString());
		//iter = allsiteusers.iterator();
//		UserDAO newusrdao = DataAccessDriver.getInstance().newUserDAO();
//		SakaiUserDAO s = new SakaiUserDAO();
		//SakaiJForumUtils sakaiJForumUtils = new SakaiJForumUtils();
		createNewUsers(users, allsiteusers);

		//<<<11/07/05 Murthy - step 4: update existing site users to the current sakai role
		updateExisUsersGroup(exissiteusers);
		//>>>11/07/05 Murthy

	//		 dropped students remove from users and delete their association with groups
		User adminuser = dropUsers(users, copyallsiteusers);
		if (adminuser !=null) users.remove(adminuser);
		//this.context.put("users", users);
		//this.setTemplateName(TemplateKeys.USER_LIST);
	}*/

	/**
	 * @param users
	 * @param copyallsiteusers
	 * @return
	 * @throws Exception
	 *//*
	private User dropUsers(List users, List copyallsiteusers) throws Exception {
		UserDAO newusrdao = DataAccessDriver.getInstance().newUserDAO();
		Iterator dropiter =users.iterator();
		User adminuser = null;
		while(dropiter.hasNext())
		{
			User dropusr = (User)dropiter.next();
			String dropusername = dropusr.getUsername().toLowerCase();
			if(dropusername.equals("admin"))
				adminuser = dropusr;
			if (!copyallsiteusers.contains(dropusername))
			{
				if (logger.isDebugEnabled()) logger.debug("dropusr.getUsername().toLowerCase() : "+dropusername);
				//<<<11/07/05 Murthy updated to remove all groups associated with this user 
				User dropthisuser = newusrdao.selectById(dropusr.getId());
				List usrParGrps = dropthisuser.getGroupsList();
				Iterator dropUsrIter = usrParGrps.iterator();

				while (dropUsrIter.hasNext()){
					Group usrGrp = (Group)dropUsrIter.next();
					newusrdao.removeFromGroup(dropusr.getId(),new int[]{usrGrp.getId()});
				}
				//>>>11/07/05 Murthy updated to remove all groups associated with this user 
				//newusrdao.removeFromGroup(dropusr.getId(),new int[]{s.getGroupId(dropusername)});
				dropiter.remove();
			}

		}
		return adminuser;
	}*/

	/**
	 * @param exissiteusers
	 * @throws Exception
	 */
	/*private void updateExisUsersGroup(List exissiteusers) throws Exception {
		Iterator iterExisUsers = exissiteusers.iterator();
		SakaiJForumUtils sakaiJForumUtils = new SakaiJForumUtils();
		UserDAO newusrdao = DataAccessDriver.getInstance().newUserDAO();
		SakaiUserDAO s = new SakaiUserDAO();
		
		while(iterExisUsers.hasNext()){
			User exisusr = (User)iterExisUsers.next();

//			Role role = RealmService.getRealm(realmId).getUserRole(exisusr.getUsername().toLowerCase());
//			System.out.println("role for existing user "+ role.getId() + role.contains("jforum.manage") + " , " 
//									+ role.contains("jforum.admin") + "," 
//									+ role.contains("jforum.member"));

			//if(role.contains("jforum.manage"))
			if (sakaiJForumUtils.isJForumFacilitator(exisusr.getUsername().toLowerCase()))
			{	
				remove all exisitng groups if user belongs to other groups
				 as Facilitator belong to only one group(Facilitator group)
				 and add the Facilitator group
				
				//newusrdao.addToGroup(exisusr.getId(), new int[]{s.findGroupId("Facilitator")});
				//newusrdao.removeFromGroup
				User existinguser = newusrdao.selectById(exisusr.getId());
				//List usrExisGrps = exisusr.getGroupsList();
				List usrExisGrps = existinguser.getGroupsList();
				Iterator usrExisGrpsIter = usrExisGrps.iterator();
				//int facilitatorGrpId = s.getGroupId(exisusr.getUsername());
				int facilitatorGrpId = s.findGroupId("Facilitator");
				boolean facGrp = false;
				while (usrExisGrpsIter.hasNext()){
					Group usrGrp = (Group)usrExisGrpsIter.next();

					if (facilitatorGrpId != usrGrp.getId()){
						newusrdao.removeFromGroup(exisusr.getId(),new int[]{usrGrp.getId()});
					}else
						facGrp = true;
				}

				//if already in Facilitator group
				if (facGrp){
					User existingfacuser = newusrdao.selectById(exisusr.getId());
					List usrFacGrps = existingfacuser.getGroupsList();

					if (usrFacGrps !=null && usrFacGrps.size() > 1){
						//keep only one facilitator group
						Iterator usrFacGrpsIter = usrFacGrps.iterator();
						for(int i=0; i <usrFacGrps.size()-1; i++){
							//Group usrFacGrp = (Group)usrFacGrpsIter.next();
							Group usrFacGrp = (Group)usrFacGrps.get(i);
							newusrdao.removeFromGroup(existingfacuser.getId(),new int[]{usrFacGrp.getId()});
						}
					}
				}else{
					//add Facilitator group
					newusrdao.addToGroup(exisusr.getId(), new int[]{s.findGroupId("Facilitator")});
				}
			}
			//else if(role.contains("jforum.member"))
			else if (sakaiJForumUtils.isJForumParticipant(exisusr.getUsername().toLowerCase()))
			{
				
				If user belongs to jforum.member get the exisitng user groups
				 and if don't belong to any group add to Paticipant else check
				 the groups if the user belongs to Facilitator if belongs remove the 
				 group leave the other groups(realistically if user belongs to Facilitator he should not
				 have any other groups)
				 
				//newusrdao.addToGroup(exisusr.getId(), new int[]{s.findGroupId("Participant")});
				int participantGrpId = s.findGroupId("Participant");
				int facilitatorGrpId = s.findGroupId("Facilitator");

				User existinguser = newusrdao.selectById(exisusr.getId());
				List usrExisGrps = existinguser.getGroupsList();
				Iterator usrExisGrpsIter = usrExisGrps.iterator();

				boolean participantGrp = false;
				while (usrExisGrpsIter.hasNext()){
					Group usrGrp = (Group)usrExisGrpsIter.next();

					if (facilitatorGrpId == usrGrp.getId()){
						//remove facilitator group
						newusrdao.removeFromGroup(exisusr.getId(),new int[]{usrGrp.getId()});
					}else if (participantGrpId == usrGrp.getId()){
						//participant group existing
						participantGrp = true;
					}
				}

				User existingParuser = newusrdao.selectById(exisusr.getId());
				List usrParGrps = existingParuser.getGroupsList();
				//if already in Participant group
				if (participantGrp){
					//do nothing and keep the exisitng groups as is
				}else{
					//add Participant group
					if (usrParGrps.size() == 0)
						newusrdao.addToGroup(exisusr.getId(), new int[]{s.findGroupId("Participant")});
				}
			}
		}
	}*/

	/**
	 * @param users
	 * @param iter
	 * @param newusrdao
	 * @param s
	 * @param sakaiJForumUtils
	 * @throws Exception
	 */
	/*private void createNewUsers(List users, List allsiteusers) throws Exception {
		SakaiJForumUtils sakaiJForumUtils = new SakaiJForumUtils();
		UserDAO newusrdao = DataAccessDriver.getInstance().newUserDAO();
		SakaiUserDAO s = new SakaiUserDAO();
		Iterator iter = allsiteusers.iterator();
		while(iter.hasNext())
		{
			String newusername = (String)iter.next();
			if (logger.isDebugEnabled()) logger.debug("creating jforum user for " + newusername);
			//if user exists in jforum then associate user with group of this course
			User jfusr = newusrdao.selectByName(newusername);
			if( jfusr !=null)
			{
				//associate with jforum group based on sakai site role
//				Role role = RealmService.getRealm(realmId).getUserRole(newusername);
//				System.out.println(" role found "+ role.getId() + role.contains("jforum.manage") + role.contains("jforum.admin") + role.contains("jforum.member"));

				//if(role.contains("jforum.manage"))
				if (sakaiJForumUtils.isJForumFacilitator(newusername))
				{
				newusrdao.addToGroup(jfusr.getId(), new int[]{s.findGroupId("Facilitator")});
				users.add(jfusr);
				}//else if(role.contains("jforum.member"))
				if (sakaiJForumUtils.isJForumParticipant(newusername))
				{
				newusrdao.addToGroup(jfusr.getId(), new int[]{s.findGroupId("Participant")});
				users.add(jfusr);
				}
			}
			else
			{
				// 	else create new jforum user
				// get sakai user attribs
				try
				{
					org.sakaiproject.user.api.User sakaiusr = UserDirectoryService.getUser(newusername);

					//create new jforum user
					User newusr = new User();
					newusr.setUsername(newusername);
					newusr.setPassword("password");
					newusr.setEmail(sakaiusr.getEmail());
					newusr.setActive(1);
					if(sakaiusr.getFirstName() == null || sakaiusr.getFirstName().length() == 0)
						newusr.setFirstName(" ");
					else newusr.setFirstName(sakaiusr.getFirstName());
					if(sakaiusr.getLastName() == null || sakaiusr.getLastName().length() == 0)
						newusr.setLastName("Guest");
					else newusr.setLastName(sakaiusr.getLastName());
					newusr.setRegistrationDate(new Date());
					newusr.setTotalPosts(0);
					newusr.setKarma(new KarmaStatus());
				//	newusr.setKarma(new Karma);
					//11/03/05 Murthy - Commented as addNew() adds the group also
					//newusrdao.addNew(newusr);
					newusrdao.addNewUser(newusr);
//					Role role = RealmService.getRealm(realmId).getUserRole(newusername);
//					System.out.println(" role found "+ role.getId() + role.contains("jforum.manage") + role.contains("jforum.admin") + role.contains("jforum.member"));
					
					//if(role.contains("jforum.manage"))
					if (sakaiJForumUtils.isJForumFacilitator(newusername))
					{
						newusrdao.addToGroup(newusr.getId(), new int[]{s.findGroupId("Facilitator")});
					}
					//else if(role.contains("jforum.member"))
					else if (sakaiJForumUtils.isJForumParticipant(newusername))
					{
					newusrdao.addToGroup(newusr.getId(), new int[]{s.findGroupId("Participant")});
					}
					users.add(newusr);
				}catch(Exception e)
				{
					logger.error(this.getClass().getName() + 
							".createNewUsers() : " + e.getMessage(), e);
				}
			}

		}
	}
*/
}
