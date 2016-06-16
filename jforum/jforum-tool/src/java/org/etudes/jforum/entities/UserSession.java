/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/entities/UserSession.java $ 
 * $Id: UserSession.java 55483 2008-12-01 21:10:54Z murthy@etudes.org $ 
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
package org.etudes.jforum.entities;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import net.jforum.security.PermissionControl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.JForum;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.PrivateMessageDAO;
import org.etudes.jforum.dao.generic.SakaiUserDAO;
import org.etudes.jforum.security.SecurityConstants;
import org.etudes.jforum.util.Captcha;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.cover.UserDirectoryService;

import com.octo.captcha.image.ImageCaptcha;

/**
 * Stores information about user's session.
 * 
 * @author Rafael Steil
 * 08/09/05 -- rashmim - isFacilitator() method added to not display certain 
 * admin menu options for a facilitator like configuration, attachments etc 
 * 08/30/05 - rashmim - add course_id member and getter/setter to the userSession object
 * and modified dataToUser() to set course_id 
 * 9/26/05 - mallika - adding method to check if a user is a facilitator by supplying username
 * 9/26/05 - mallika - adding method to get private messages from db
 * 10/17/05 - mallika - adding code to save time to the db
 * 07/18/06 - Murthy - added sakai user id variable
 * 10/2/06 - Mallika - adding markAllTime
 * 01/10/06 - Murthy - addded isFacilitator(Object sakUserId) method
 */
public class UserSession implements Serializable
{
	static final long serialVersionUID = 0;
	private long sessionTime;
	
	private int userId;
	private int privateMessages;
	
	private Date startTime;
	private Date lastVisit;
	
	//Mallika - adding markAlltime
	private Date markAllTime;
	
	// This was transient (not sure why... it was breaking the cache) -- JMH
	private String sessionId;
	
	private String username;
	private String lang;
	
	private boolean autoLogin;
	
	private ImageCaptcha imageCaptcha = null;
	
	//added by rashmi
	private String sakaiUserType;
	private String course_id;
	private String firstName;
	private String lastName;
	// add end
	//07/18/2006 - Murthy
	private String sakaiUserId;
	
	private static Log logger = LogFactory.getLog(UserSession.class);	

	
	public UserSession() {}

	public UserSession(UserSession us)
	{
		if (us.getStartTime() != null) {
			this.startTime = new Date(us.getStartTime().getTime());
		}

		if (us.getLastVisit() != null) {
			this.lastVisit = new Date(us.getLastVisit().getTime());
		}
		
		if (us.getMarkAllTime() != null) {
			this.markAllTime = new Date(us.getMarkAllTime().getTime());
		}
		
		this.sessionTime = us.getSessionTime();
		this.userId = us.getUserId();
		this.sessionId = us.getSessionId();
		this.username = us.getUsername();
		this.autoLogin = us.getAutoLogin();
		this.lang = us.getLang();
		this.privateMessages = us.getPrivateMessages();
		this.imageCaptcha = us.imageCaptcha;
		//rashmi modified
		this.sakaiUserType = null;
		this.course_id=null;
	}

	/**
	 * Set session's start time.
	 * 
	 * @param startTime  Start time in miliseconds
	 */
	public void setStartTime(Date startTime)
	{
		this.startTime = startTime;
	}

	/**
	 * @return Returns the privateMessages.
	 */
	public int getPrivateMessages()
	{
		return this.privateMessages;
	}

	/**
	 * @param privateMessages The privateMessages to set.
	 */
	public void setPrivateMessages(int privateMessages)
	{
		this.privateMessages = privateMessages;
	}

	/**
	 * Set session last visit time.
	 * 
	 * @param lastVisit Time in miliseconds
	 */
	public void setLastVisit(Date lastVisit)
	{
		this.lastVisit = lastVisit;
	}
	
	/**
	 * Set session mark all time.
	 * 
	 * @param markAll Time in miliseconds
	 */
	public void setMarkAllTime(Date markAllTime)
	{
		this.markAllTime = markAllTime;
	}

	/**
	 * Set user's id
	 * 
	 * @param userId The user id
	 */
	public void setUserId(int userId)
	{
		this.userId = userId;
	}

	/**
	 * Set user's name
	 * 
	 * @param username The username
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}

	public void setSessionTime(long sessionTime)
	{
		this.sessionTime = sessionTime;
	}

	public void setLang(String lang)
	{
		this.lang = lang;
	}

	/**
	 * Update the session time.
	 */
	public void updateSessionTime()
	{
		this.sessionTime = System.currentTimeMillis() - this.startTime.getTime();
	}

	/**
	 * Enable or disable auto-login.
	 * 
	 * @param autoLogin  <code>true</code> or <code>false</code> to represent auto-login status
	 */
	public void setAutoLogin(boolean autoLogin)
	{
		this.autoLogin = autoLogin;
	}

	/**
	 * Gets user's session start time
	 * 
	 * @return Start time in miliseconds
	 */
	public Date getStartTime()
	{
		return this.startTime;
	}

	public String getLang()
	{
		return this.lang;
	}

	/**
	 * Gets user's last visit time
	 * 
	 * @return Time in miliseconds
	 */
	public Date getLastVisit()
	{	
		return this.lastVisit;
	}
	
	/**
	 * Gets user's mark all time
	 * 
	 * @return Time in miliseconds
	 */
	public Date getMarkAllTime()
	{	
		return this.markAllTime;
	}	
	

	/**
	 * Gets the session time.
	 * 
	 * @return The session time
	 */
	public long getSessionTime()
	{
		return this.sessionTime;
	}

	/**
	 * Gets user's id
	 * 
	 * @return The user id
	 */
	public int getUserId()
	{
		return this.userId;
	}

	/**
	 * Gets the username
	 * 
	 * @return The username
	 */
	public String getUsername()
	{
		return this.username;
	}

	/**
	 * Gets auto-login status
	 * 
	 * @return <code>true</code> if auto-login is enabled, or <code>false</code> if disabled.
	 */
	public boolean getAutoLogin()
	{
		return this.autoLogin;
	}

	/**
	 * Gets the session id related to this user session
	 * 
	 * @return A string with the session id
	 */
	public String getSessionId()
	{
		return this.sessionId;
	}

	/**
	 * Checks if the user is an administrator
	 * 
	 * @return <code>true</code> if the user is an administrator
	 */
	public boolean isAdmin() throws Exception
	{
		//return SecurityRepository.canAccess(this.userId, SecurityConstants.PERM_ADMINISTRATION);
		return SecurityService.isSuperUser();
	}

	/**
	 * Checks if the user is a moderator
	 * 
	 * @return <code>true</code> if the user has moderations rights
	 */
	public boolean isModerator() throws Exception
	{
		//return SecurityRepository.canAccess(this.userId, SecurityConstants.PERM_MODERATION);
		return (JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser());
	}
	
	/**
	 * Checks if the user can moderate a forum
	 * 
	 * @param forumId the forum's id to check for moderation rights
	 * @return <code>true</code> if the user has moderations rights
	 */
	public boolean isModerator(int forumId) throws Exception
	{
		/*PermissionControl pc = SecurityRepository.get(this.userId);
		
		return (pc.canAccess(SecurityConstants.PERM_MODERATION))
			&& (pc.canAccess(SecurityConstants.PERM_MODERATION_FORUMS, 
				Integer.toString(forumId)));*/
		return (JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser());
	}

	/**
	 * Makes the user's session "anoymous" - eg, the user. This method sets the session's start and
	 * last visit time to the current datetime, the user id to the return of a call to
	 * <code>SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)</code> and finally sets
	 * session attribute named "logged" to "0" will be considered a non-authenticated / anonymous
	 * user
	 */
	public void makeAnonymous()
	{
		this.setStartTime(new Date(System.currentTimeMillis()));
		if (logger.isDebugEnabled()) logger.debug("USERSESSION MAKEANYONYMOUS 409");
		//Mallika-commenting line below, only setting lastvisit when link is clicked in header
		//this.setLastVisit(new Date(System.currentTimeMillis()));
		this.setUserId(SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID));

		SessionFacade.setAttribute("logged", "0");
	}

	/**
	 * Sets a new user session information using information from an <code>User</code> instance.
	 * This method sets the user id, username, the number of private messages, the session's start
	 * time ( set to the current date and time ) and the language.
	 * 
	 * @param user The <code>User</code> instance to get data from
	 */
	public void dataToUser(User user)
	{
		this.setUserId(user.getId());
		this.setUsername(user.getUsername());
		this.setSakaiUserId(user.getSakaiUserId());
		this.setPrivateMessages(user.getPrivateMessagesCount());
		this.setStartTime(new Date(System.currentTimeMillis()));
		this.setLang(user.getLang());
		//added by rashmi 
		String context = ToolManager.getCurrentPlacement().getContext();
		this.setCourse_id(context);
		
		this.setLastVisit(user.getLastVisit());
		if (logger.isDebugEnabled()) logger.debug("usersession.dataToUser course_id is" +context);
	}	
	
	/**
	 * Get the captcha image to challenge the user
	 * 
	 * @return BufferedImage the captcha image to challenge the user
	 */
	public BufferedImage getCaptchaImage()
	{
		if (this.imageCaptcha == null) {
			return null;
		}
		
		return (BufferedImage)this.imageCaptcha.getChallenge();
	}

	/**
	 * Validate the captcha response of user
	 * 
	 * @param anwser String the captcha response from user
	 * @return boolean true if the answer is valid, otherwise return false
	 */
	public boolean validateCaptchaResponse(String userResponse)
	{
		if ((SystemGlobals.getBoolValue(ConfigKeys.CAPTCHA_REGISTRATION) 
				|| SystemGlobals.getBoolValue(ConfigKeys.CAPTCHA_POSTS))
				&& this.imageCaptcha != null) {
			boolean result =  this.imageCaptcha.validateResponse(userResponse).booleanValue();
			this.destroyCaptcha();
			return result;
		}
		
		return true;
	}

	/**
	 * create a new image captcha
	 * 
	 * @return void
	 */
	public void createNewCaptcha()
	{
		this.destroyCaptcha();
		this.imageCaptcha = Captcha.getInstance().getNextImageCaptcha();
	}

	/**
	 * Destroy the current captcha validation is done
	 * 
	 * @return void
	 */
	public void destroyCaptcha()
	{
		this.imageCaptcha = null;
	}
	
	/**
	 * Checks if it's a bot
	 * @return <code>true</code> if this user session is from any robot
	 */
	public boolean isBot()
	{
		return Boolean.TRUE.equals(JForum.getRequest().getAttribute(ConfigKeys.IS_BOT));
	}
	
	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		if (!(o instanceof UserSession)) {
			return false;
		}
		
		return this.sessionId.equals(((UserSession)o).getSessionId());
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return this.sessionId.hashCode();
	}
	
	/*
	 * added by rashmi to see if the user is a facilitator.
	 * If user is sakai jforum.admin return false.
	 */
	public boolean isFacilitator()
	{
		if(sakaiUserType != null )
			{
			if(sakaiUserType.equals("Facilitator"))
			return true;
			}
		else
			{
			try{
				//if (logger.isInfoEnabled()) logger.info("User Session isFacilitator method called !!! yipeee");
				SakaiUserDAO s = new SakaiUserDAO();
				if (s.isUserAdmin()) 
					{
					sakaiUserType = "SakaiAdmin";
					return false;
					}
				
				if(s.isUserFacilitator())
					{
					sakaiUserType = "Facilitator";
					return true;
					}
			} catch(Exception e)
			{
				logger.error(this.getClass().getName() + 
						".isFacilitator() : " + e.getMessage(), e);
			}
		}
		return false;
	}
	
	//New code beg-Mallika- to check if user is facilitator by username
	public boolean isFacilitator(String sakUserId)
	{
		try{
			if (logger.isDebugEnabled()) logger.debug("User Session isFacilitator(username) method called");
				SakaiUserDAO s = new SakaiUserDAO();
				if(s.isUserFacilitator(sakUserId))
				{
					return true;
				}
			} catch(Exception e)
			{
				logger.error(this.getClass().getName() + 
						".isFacilitator(String userName) : " + e.getMessage(), e);
			}
		
		return false;
	}
	

	public boolean isFacilitator(Object sakUserId)
	{
		try{
			if (logger.isDebugEnabled()) logger.debug("User Session isFacilitator(sakUserId) method called");
				SakaiUserDAO s = new SakaiUserDAO();
				if (sakUserId != null && (sakUserId instanceof String)) {
					if(s.isUserFacilitator((String)sakUserId))
					{
						return true;
					}
					
				}
				
			} catch(Exception e)
			{
				logger.error(this.getClass().getName() + 
						".isFacilitator(Object sakUserId) : " + e.getMessage(), e);
			}
		
		return false;
	}
	
	//To get private msg count from DB
	public int getPrivateMessageCount()
	{
		PrivateMessageDAO pm = DataAccessDriver.getInstance().newPrivateMessageDAO();
		int total = 0;
		try
		{
		  total = pm.selectUnreadCount(this.userId);
		}
		catch (Exception e)
		{
			//System.out.println("Exception while reading count ");
			logger.error(this.getClass().getName() + 
					".getPrivateMessageCount() : " + e.getMessage(), e);
		}
		return total;
	}
	//New code end-Mallika
	
	public boolean isSakaiAdmin()
	{
		if(sakaiUserType != null )
		{
		if(sakaiUserType.equals("SakaiAdmin"))
		return true;
		}
	else
		{
		try{
			SakaiUserDAO s = new SakaiUserDAO();
			if(s.isUserAdmin())
				{
				sakaiUserType = "SakaiAdmin";
				return true;
				}
		} catch(Exception e)
		{
			logger.error(this.getClass().getName() + 
					".isSakaiAdmin() : " + e.getMessage(), e);
		}
	}
	return false;
	}
	
	public List getCourseUsers()
	{
		ArrayList courseUsers = null;
		return courseUsers;
	}
	
	/**
	 * @return Returns the course_id.
	 */
	public String getCourse_id() {
		return course_id;
	}
	/**
	 * @param course_id The course_id to set.
	 */
	public void setCourse_id(String course_id) {
		this.course_id = course_id;
	}
	//rashmi add beg
	/**
	 * @return Returns the firstName.
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName The firstName to set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return Returns the lastName.
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName The lastName to set.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	//rashmi add end

	public String getSakaiUserId() {
		return sakaiUserId;
	}

	public void setSakaiUserId(String sakaiUserId) {
		this.sakaiUserId = sakaiUserId;
	}
}
