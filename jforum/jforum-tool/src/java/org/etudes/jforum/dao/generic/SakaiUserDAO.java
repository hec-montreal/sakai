/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/SakaiUserDAO.java $ 
 * $Id: SakaiUserDAO.java 55493 2008-12-01 22:55:48Z murthy@etudes.org $ 
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
/*
 * Created on Jul 27, 2005
 * This class is a bridge in between JForum package and SakaiProject package.
 *  It passes the sakai user info to Jforum.
 */
package org.etudes.jforum.dao.generic;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.JForum;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.cover.UserDirectoryService;


/**
 * @author Rashmi@Foothill College 
 */
/*
* 8/17/05 - Mallika - added code to set course id also when getting group id
* 9/18/05 - Rashmi - add new functions to check any user's role isUserAdmin(), isUserFacilitator() etc  
* 9/20/05 - Rashmi - revised isFacilitator and isParticipant methods to check a users 
* role for that site rather than at user permission level. 
 */
public class SakaiUserDAO {
	private static Log logger = LogFactory.getLog(SakaiUserDAO.class);
	
	public SakaiUserDAO()
	{
	}
	
	/*
	 * Finds user role permissions at sakai level and based on that find the group
	 * where user will belong to. Right now JForum adds every user to General group.
	 */
	public int getGroupId() throws Exception
	{
		String groupName;
		try
		{
		 try{
		 	if (isUserAdmin())
		 		{
		 		// 	set groupname for administrator
				groupName="Administration";
		 		}
		 		else if(isUserFacilitator())
		 			{
		 			groupName="Facilitator";
		 			}else
		 			{
		 				groupName="Participant";
		 			}
		   	}
		 	catch(Exception e)
			{
				//e.printStackTrace();
				logger.error(this.getClass().getName() + 
						".getGroupId() : " + e.getMessage(), e);
				//System.out.println("ERROR!!!error in getting user's sakai permissions");
				groupName="Participant";
			}
		 	return findGroupId(groupName);
						
		}catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
	
	/*
	 * group id for a user
	 */
	
	public int getGroupId(String findusername) throws Exception
	{
		String groupName;
		try
		{
		 try{
		 	if (isUserAdmin(findusername))
		 		{
		 		// 	set groupname for administrator
				groupName="Administration";
		 		}
		 		else if(isUserFacilitator(findusername))
		 			{
		 			groupName="Facilitator";
		 			}else
		 			{
		 				groupName="Participant";
		 			}
		   	}
		 	catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("ERROR!!!error in getting user's sakai permissions");
				groupName="Participant";
			}
		 	return findGroupId(groupName);
						
		}catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
	
	/*
	 * Rashmi
	 * seggregate in 2 functions as this method is called from other java class 
	 */
	public int findGroupId(String groupName) throws Exception
	{
		
		//query to get the group_id
		try{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.searchGroup"));
		p.setString(1, groupName);
		p.setString(2, ToolManager.getCurrentPlacement().getContext());
		ResultSet rs = p.executeQuery();
		
		// This query doesn't always return a result -- JMH
		int groupId;
		if(rs.next()) {
			if(logger.isDebugEnabled()) logger.debug("findGroupId has another record");
			groupId = rs.getInt(1);
		} else {
			groupId = 1;
		}		
		rs.close();
		p.close();
		
		return groupId;
		}catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}
	
	/**
	 * Check if the current user has permission as author.
	 * @return true if the current user has permission to perform this action, false if not.
	 */
	public boolean isUserFacilitator() {

			org.sakaiproject.user.api.User sakUser = UserDirectoryService.getCurrentUser();
			String thisSiteId = ToolManager.getCurrentPlacement().getContext();
			String realmName = "/site/" + thisSiteId;
		    return SecurityService.unlock(sakUser, JForumUserUtil.ROLE_FACILITATOR, realmName);
//			String currCourse_id= ToolManager.getCurrentPlacement().getContext();
//			String realmId = "/site/"+currCourse_id;
//			String newusername=UserDirectoryService.getCurrentUser().getId();
//			Role role = RealmService.getRealm(realmId).getUserRole(newusername);
//			if(role !=null && role.contains(ROLE_FACILITATOR))
//				return true;
//			else return false;
		//	return SecurityService.unlock(ROLE_FACILITATOR, relativeAccessPoint + ToolManager.getCurrentPlacement().getContext());
			
	}


//	/**
//	 * Check if the current user has permission as admin.
//	 * @return true if the current user has permission to perform this action, false if not.
//	 */
//
//	public boolean isUserAdmin()throws Exception{
//
//		try {
//				return SecurityService.unlock(ROLE_ADMIN, relativeAccessPoint + ToolManager.getCurrentPlacement().getContext());
//			
//		} catch (Exception e) {
//			throw e;
//		}
//	}
	
	/**
	 * checks if user is sakai/jforum admin
	 * @param username
	 * @return
	 */
	public boolean isUserAdmin() {
		try {
			org.sakaiproject.user.api.User sakUser = UserDirectoryService.getCurrentUser();
			String thisSiteId = ToolManager.getCurrentPlacement().getContext();
			String realmName = "/site/" + thisSiteId;
		    return SecurityService.unlock(sakUser, JForumUserUtil.ROLE_ADMIN, realmName);	
			
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Check if the current user has permission as student.
	 * @return true if the current user has permission to perform this action, false if not.
	 */
	public boolean isUserParticipant()throws Exception{
		try {
//			String currCourse_id= ToolManager.getCurrentPlacement().getContext();
//			String realmId = "/site/"+currCourse_id;
//			String newusername=UserDirectoryService.getCurrentUser().getId();
//			Role role = RealmService.getRealm(realmId).getUserRole(newusername);
//			if(role!=null && role.contains(ROLE_PARTICIPANT))
//				return true;
//			else return false;
			org.sakaiproject.user.api.User sakUser = UserDirectoryService.getCurrentUser();
			String thisSiteId = ToolManager.getCurrentPlacement().getContext();
			String realmName = "/site/" + thisSiteId;
		    return SecurityService.unlock(sakUser, JForumUserUtil.ROLE_PARTICIPANT, realmName);
			//return SecurityService.unlock(ROLE_PARTICIPANT, relativeAccessPoint + ToolManager.getCurrentPlacement().getContext());
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * Check if the  user has permission as author.
	 * @return true if the current user has permission to perform this action, false if not.
	 */
	public boolean isUserFacilitator(String sakUserId)throws Exception{

		try {
//			String currCourse_id= ToolManager.getCurrentPlacement().getContext();
//			String realmId = "/site/"+currCourse_id;
//			String newusername=UserDirectoryService.getUser(u).getId();
//			Role role = RealmService.getRealm(realmId).getUserRole(newusername);
//			if(role !=null && role.contains(ROLE_FACILITATOR))
//				return true;
//			else return false;
			org.sakaiproject.user.api.User sakUser = UserDirectoryService.getUser(sakUserId);
			String thisSiteId = ToolManager.getCurrentPlacement().getContext();
			String realmName = "/site/" + thisSiteId;
		    return SecurityService.unlock(sakUser, JForumUserUtil.ROLE_FACILITATOR, realmName);
			
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Check if the  user has permission as admin.
	 * @return true if the current user has permission to perform this action, false if not.
	 */
	public boolean isUserAdmin(String sakUserId)throws Exception{

		try {
//			String currCourse_id= ToolManager.getCurrentPlacement().getContext();
//			String realmId = "/site/"+currCourse_id;
//			String newusername=UserDirectoryService.getUser(u).getId();
//			Role role = RealmService.getRealm(realmId).getUserRole(newusername);
//			if(role !=null && role.contains(ROLE_ADMIN))
//				return true;
//			else return false;
			org.sakaiproject.user.api.User sakUser = UserDirectoryService.getUser(sakUserId);
			String thisSiteId = ToolManager.getCurrentPlacement().getContext();
			String realmName = "/site/" + thisSiteId;
		    return SecurityService.unlock(sakUser, JForumUserUtil.ROLE_ADMIN, realmName);
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * Check if the  user has permission as student.
	 * @return true if the current user has permission to perform this action, false if not.
	 */
	public boolean isUserParticipant(String sakUserId)throws Exception{
		try {
//			String currCourse_id= ToolManager.getCurrentPlacement().getContext();
//			String realmId = "/site/"+currCourse_id;
//			String newusername=UserDirectoryService.getUser(u).getId();
//			Role role = RealmService.getRealm(realmId).getUserRole(newusername);
//			if(role !=null && role.contains(ROLE_PARTICIPANT))
//				return true;
//			else return false;
			org.sakaiproject.user.api.User sakUser = UserDirectoryService.getUser(sakUserId);
			String thisSiteId = ToolManager.getCurrentPlacement().getContext();
			String realmName = "/site/" + thisSiteId;
		    return SecurityService.unlock(sakUser, JForumUserUtil.ROLE_PARTICIPANT, realmName);
			
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	public void updateSakaiUserInfo(String username, String newemail) throws Exception
	{
		try{
			boolean status = false;
			
			PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.isUsernameRegistered"));
			// PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.findByName"));
			p.setString(1, username);
			
			ResultSet rs = p.executeQuery();
//			 1. find if user is registered
			if (rs.next() && rs.getInt("registered") > 0) {
				status = true;
				System.out.println("user is there");
			} else return;
			
			//2.if yes then get user and compare emailaddress
			
			rs.close();
			p.close();
			}catch(Exception e)
			{
				throw e;
			}
	}
}
