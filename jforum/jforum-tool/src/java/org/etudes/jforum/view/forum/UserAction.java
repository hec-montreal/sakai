/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/forum/UserAction.java $ 
 * $Id: UserAction.java 69124 2010-07-09 23:16:31Z murthy@etudes.org $ 
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
package org.etudes.jforum.view.forum;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.Command;
import org.etudes.jforum.ControllerUtils;
import org.etudes.jforum.JForum;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.dao.CourseTimeDAO;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.UserDAO;
import org.etudes.jforum.dao.UserSessionDAO;
import org.etudes.jforum.entities.CourseTimeObj;
import org.etudes.jforum.entities.TopicMarkTimeObj;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.entities.UserSession;
import org.etudes.jforum.repository.RankingRepository;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.MD5;
import org.etudes.jforum.util.concurrent.executor.QueuedExecutor;
import org.etudes.jforum.util.mail.EmailException;
import org.etudes.jforum.util.mail.EmailSenderTask;
import org.etudes.jforum.util.mail.LostPasswordSpammer;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.preferences.TemplateKeys;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.etudes.jforum.view.forum.common.UserCommon;
import org.etudes.jforum.view.forum.common.ViewCommon;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.authz.api.AuthzGroupService;

//Mallika's import end
/**
 * @author Rafael Steil
 * Rashmi - 09/15/05 - include all sakai users listed in site info in member listing
 * Rashmi - 10/05/05 - add if conditions for firstname and lastname of a guest
 * Rashmi - 10/11/05 - revised profile() to get number of posts for a site
 * Mallika -10/18/05 - Adding method to set last visit time
 * Murthy - 11/05/05 - updated for clicking on Mark all as read, the forum's 
 * 					   launch page is displayed
 * Murthy - 11/03/05 - Commented call to addNew() in list() method as this adds the 
 * 						group also and added call to addNewUser() to create only
 * 						user
 * Murthy - 11/07/05 - updated list() method to synchronize the sakai user's info with 
 * 						jforum
 * Murthy - 11/07/05 - updated list() to get all current users
 * Murthy - 11/10/05 - commented the code in list()and moved to SakaiJForumUtils
 * Murthy - 11/15/05 - code added in the edit method to update user info
 * Mallika - 9/12/06 - changing code to calls updateMembersInfo
 * Mallika - 9/25/06 - changed setlastvisittime code
 * Mallika - 10/2/06 - adding code to setmarkalltime
 */
public class UserAction extends Command
{
	private static final Log logger = LogFactory.getLog(UserAction.class);

	private boolean canEdit() throws Exception
	{
		int tmpId = SessionFacade.getUserSession().getUserId();
		boolean canEdit = SessionFacade.isLogged() && tmpId == this.request.getIntParameter("user_id");

		if (!canEdit) {
			this.profile();
		}

		return canEdit;
	}

	public void edit() throws Exception
	{
		if (this.canEdit()) {
			int userId = this.request.getIntParameter("user_id");
			UserDAO um = DataAccessDriver.getInstance().newUserDAO();
			User u = um.selectById(userId);
			
			//<<<start 11/15/05 Murthy - update user info
			//SakaiJForumUtils sakaiJForumUtils =  new SakaiJForumUtils();
			JForumUserUtil.updateJFUser(u);
			//>>>end update user info
			this.context.put("u", u);
			this.context.put("action", "editSave");
			this.setTemplateName(TemplateKeys.USER_EDIT);
		}
	}

	public void editDone() throws Exception
	{
		this.context.put("editDone", true);
		this.edit();
	}

	public void editSave() throws Exception
	{
		if (this.canEdit()) {
			int userId = this.request.getIntParameter("user_id");
			List warns = UserCommon.saveUser(userId);

			if (warns.size() > 0) {
				this.context.put("warns", warns);
				this.edit();
			}
			else {
				JForum.setRedirect(this.request.getContextPath()
						+ "/user/editDone/" + userId
						+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
			}
		}
	}

	private void registrationDisabled()
	{
		this.setTemplateName(TemplateKeys.USER_REGISTRATION_DISABLED);
		this.context.put("message", I18n.getMessage("User.registrationDisabled"));
	}

	/*public void insert()
	{
		if (!SystemGlobals.getBoolValue(ConfigKeys.REGISTRATION_ENABLED)
				|| ConfigKeys.TYPE_SSO.equals(SystemGlobals.getValue(ConfigKeys.AUTHENTICATION_TYPE))) {
			this.registrationDisabled();
			return;
		}

		this.setTemplateName(TemplateKeys.USER_INSERT);
		this.context.put("action", "insertSave");
		this.context.put("username", this.request.getParameter("username"));
		this.context.put("email", this.request.getParameter("email"));

		if (SystemGlobals.getBoolValue(ConfigKeys.CAPTCHA_REGISTRATION)){
			// Create a new image captcha
			SessionFacade.getUserSession().createNewCaptcha();
			this.context.put("captcha_reg", true);
		}
	}

	public void insertSave() throws Exception
	{
		if (!SystemGlobals.getBoolValue(ConfigKeys.REGISTRATION_ENABLED)
				|| ConfigKeys.TYPE_SSO.equals(SystemGlobals.getValue(ConfigKeys.AUTHENTICATION_TYPE))) {
			this.registrationDisabled();
			return;
		}

		User u = new User();
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();

		String username = this.request.getParameter("username");
		String password = this.request.getParameter("password");
		String captchaResponse = this.request.getParameter("captchaResponse");

		boolean error = false;
		if (username == null || username.trim().equals("")
				|| password == null || password.trim().equals("")) {
			this.context.put("error", I18n.getMessage("UsernamePasswordCannotBeNull"));
			error = true;
		}

		if (!error && username.length() > SystemGlobals.getIntValue(ConfigKeys.USERNAME_MAX_LENGTH)) {
			this.context.put("error", I18n.getMessage("User.usernameTooBig"));
			error = true;
		}

		if (!error && username.indexOf('<') > -1 || username.indexOf('>') > -1) {
			this.context.put("error", I18n.getMessage("User.usernameInvalidChars"));
			error = true;
		}

		if (!error && um.isUsernameRegistered(this.request.getParameter("username"))) {
			this.context.put("error", I18n.getMessage("UsernameExists"));
			error = true;
		}

		if (!error && !SessionFacade.getUserSession().validateCaptchaResponse(captchaResponse)){
			this.context.put("error", I18n.getMessage("CaptchaResponseFails"));
			error = true;
		}

		if (error) {
			this.insert();

			return;
		}

		u.setUsername(username);
		u.setPassword(MD5.crypt(password));
		u.setEmail(this.request.getParameter("email"));

		if (SystemGlobals.getBoolValue(ConfigKeys.MAIL_USER_EMAIL_AUTH)) {
			u.setActivationKey(MD5.crypt(username + System.currentTimeMillis()));
		}

		int userId = um.addNew(u);

		if (SystemGlobals.getBoolValue(ConfigKeys.MAIL_USER_EMAIL_AUTH)) {
			try {
				//Send an email to new user
				QueuedExecutor.getInstance().execute(
						new EmailSenderTask(new ActivationKeySpammer(u)));
			}
			catch (Exception e) {
				logger.warn("Error while trying to send an email: " + e);
				e.printStackTrace();
			}

			this.setTemplateName(TemplateKeys.USER_INSERT_ACTIVATE_MAIL);
			this.context.put("message", I18n.getMessage("User.GoActivateAccountMessage"));
		}
		else {
			this.logNewRegisteredUserIn(userId, u);
		}
	}*/

	public void activateAccount() throws Exception
	{
		String hash = this.request.getParameter("hash");
		int userId = (new Integer(this.request.getParameter("user_id"))).intValue();

		String message = "";

		UserDAO um = DataAccessDriver.getInstance().newUserDAO();
		User u = um.selectById(userId);

		boolean isOk = um.validateActivationKeyHash(userId, hash);
		if (isOk) {
			// make account active
			um.writeUserActive(userId);
			this.logNewRegisteredUserIn(userId, u);
		}
		else {
			message = I18n.getMessage("User.invalidActivationKey");
			this.setTemplateName(TemplateKeys.USER_INVALID_ACTIVATION);
			this.context.put("message", message);
		}

	}

	private void logNewRegisteredUserIn(int userId, User u)
	{
		SessionFacade.setAttribute("logged", "1");

		UserSession userSession = new UserSession();
		userSession.setAutoLogin(true);
		userSession.setUserId(userId);
		userSession.setUsername(u.getUsername());
		if (logger.isDebugEnabled()) logger.debug("USERACTION LOGNEWREGUSER 277");
		userSession.setLastVisit(new Date(System.currentTimeMillis()));
		userSession.setStartTime(new Date(System.currentTimeMillis()));

		SessionFacade.add(userSession);

		// Finalizing.. show to user the congrats page
		JForum.setRedirect(this.request.getContextPath()
				+ "/user/registrationComplete"
				+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
	}

	public void registrationComplete() throws Exception
	{
		int userId = SessionFacade.getUserSession().getUserId();

		String profilePage = JForum.encodeUrlWithPathAndExtension("/user/edit/" + userId);
		String homePage = JForum.encodeUrlWithPathAndExtension("/forums/list");

		String message = I18n.getMessage("User.RegistrationCompleteMessage",
				new Object[] { profilePage, homePage });
		this.context.put("message", message);
		this.setTemplateName(TemplateKeys.USER_REGISTRATION_COMPLETE);
	}

	public void validateLogin() throws Exception
	{
		boolean validInfo = false;

		String password = this.request.getParameter("password");

		if (password.length() > 0) {
			User user = this.validateLogin(this.request.getParameter("username"), password);

			if (user != null) {
				JForum.setRedirect(this.request.getContextPath()
						+ "/forums/list"
						+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));

				SessionFacade.setAttribute("logged", "1");

				UserSession tmpUs = null;
				String sessionId = SessionFacade.isUserInSession(user.getId());

				UserSession userSession = new UserSession(SessionFacade.getUserSession());

				// Remove the "guest" session
				SessionFacade.remove(userSession.getSessionId());

				userSession.dataToUser(user);

				// Check if the user is returning to the system
				// before its last session has expired ( hypothesis )
				if (sessionId != null) {
					// Write its old session data
					SessionFacade.storeSessionData(sessionId, JForum.getConnection());
					tmpUs = new UserSession(SessionFacade.getUserSession(sessionId));
					SessionFacade.remove(sessionId);
				}
				else {
					UserSessionDAO sm = DataAccessDriver.getInstance().newUserSessionDAO();
					tmpUs = sm.selectById(userSession, JForum.getConnection());
				}

				I18n.load(user.getLang());

				// Autologin
				if (this.request.getParameter("autologin") != null
						&& SystemGlobals.getBoolValue(ConfigKeys.AUTO_LOGIN_ENABLED)) {
					userSession.setAutoLogin(true);
					ControllerUtils.addCookie(SystemGlobals.getValue(ConfigKeys.COOKIE_AUTO_LOGIN), "1");
					ControllerUtils.addCookie(SystemGlobals.getValue(ConfigKeys.COOKIE_USER_HASH),
							MD5.crypt(SystemGlobals.getValue(ConfigKeys.USER_HASH_SEQUENCE) + user.getId()));
				}
				else {
					// Remove cookies for safety
					ControllerUtils.addCookie(SystemGlobals.getValue(ConfigKeys.COOKIE_USER_HASH), null);
					ControllerUtils.addCookie(SystemGlobals.getValue(ConfigKeys.COOKIE_AUTO_LOGIN), null);
				}

				if (tmpUs == null) {
					if (logger.isDebugEnabled()) logger.debug("USERACTION VALIDATELOGIN 357");
					userSession.setLastVisit(new Date(System.currentTimeMillis()));
				}
				else {
					// Update last visit and session start time
					if (logger.isDebugEnabled()) logger.debug("USERACTION VALIDATELOGIN 361");
					userSession.setLastVisit(new Date(tmpUs.getStartTime().getTime() + tmpUs.getSessionTime()));
				}

				SessionFacade.add(userSession);

				SessionFacade.setAttribute(ConfigKeys.TOPICS_TRACKING, new HashMap());

				ControllerUtils.addCookie(SystemGlobals.getValue(ConfigKeys.COOKIE_NAME_DATA),
						Integer.toString(user.getId()));

				//SecurityRepository.load(user.getId(), true);
				validInfo = true;
			}
		}

		// Invalid login
		if (validInfo == false) {
			this.context.put("invalidLogin", "1");
			this.setTemplateName(TemplateKeys.USER_VALIDATE_LOGIN);

			if (this.request.getParameter("returnPath") != null) {
				this.context.put("returnPath",
						this.request.getParameter("returnPath"));
			}
		}
		else if (ViewCommon.needReprocessRequest()) {
			ViewCommon.reprocessRequest();
		}
		else if (this.request.getParameter("returnPath") != null) {
			JForum.setRedirect(this.request.getParameter("returnPath"));
		}
	}

	private User validateLogin(String name, String password) throws Exception
	{
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();
		User user = um.validateLogin(name, password);
		return user;
	}

	public void profile() throws Exception
	{
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();

		User u = um.selectById(this.request.getIntParameter("user_id"));
		if (u.getId() == 0) {
			this.userNotFound();
		}
		else {
			this.setTemplateName(TemplateKeys.USER_PROFILE);
			//this.context.put("karmaEnabled", SecurityRepository.canAccess(SecurityConstants.PERM_KARMA_ENABLED));
			this.context.put("karmaEnabled", false);
			this.context.put("rank", new RankingRepository());
			// add by rashmi to make number of posts site aware
			u.setTotalPosts(um.getNumberOfMessages(this.request.getIntParameter("user_id")));
			//add end
			//<<< 01/14/2008 Murthy - remove email address to display if user is inactive
	    	AuthzGroupService authz = ComponentManager.get(AuthzGroupService.class);
	    	String role = authz.getUserRole(u.getSakaiUserId(), "/site/" + ToolManager.getCurrentPlacement().getContext());
			if (role == null){
				u.setEmail("");
			}
			//>>> 01/14/2008 Murthy
			
			this.context.put("u", u);
		}
	}

	private void userNotFound()
	{
		this.context.put("message", I18n.getMessage("User.notFound"));
		this.setTemplateName(TemplateKeys.USER_NOT_FOUND);
	}

	public void logout() throws Exception
	{
		JForum.setRedirect(this.request.getContextPath()
				+ "/forums/list"
				+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));

		UserSession userSession = SessionFacade.getUserSession();
		SessionFacade.storeSessionData(userSession.getSessionId(), JForum.getConnection());

		SessionFacade.remove(userSession.getSessionId());

		// Disable auto login
		userSession.setAutoLogin(false);
		userSession.setUserId(SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID));

		SessionFacade.setAttribute("logged", "0");
		SessionFacade.add(userSession);

		ControllerUtils.addCookie(SystemGlobals.getValue(ConfigKeys.COOKIE_AUTO_LOGIN), null);
		ControllerUtils.addCookie(SystemGlobals.getValue(ConfigKeys.COOKIE_NAME_DATA),
				SystemGlobals.getValue(ConfigKeys.ANONYMOUS_USER_ID));
	}

	public void login() throws Exception
	{
		if (ConfigKeys.TYPE_SSO.equals(SystemGlobals.getValue(ConfigKeys.AUTHENTICATION_TYPE))) {
			this.registrationDisabled();
			return;
		}

		if (this.request.getParameter("returnPath") != null) {
			this.context.put("returnPath", this.request.getParameter("returnPath"));
		}

		this.setTemplateName(TemplateKeys.USER_LOGIN);
	}

	// Lost password form
	public void lostPassword()
	{
		this.setTemplateName(TemplateKeys.USER_LOSTPASSWORD);
	}

	public User prepareLostPassword(String username, String email) throws Exception
	{
		User user = null;
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();

		if (email != null && !email.trim().equals("")) {
			username = um.getUsernameByEmail(email);
		}

		if (username != null && !username.trim().equals("")) {
			List l = um.findByName(username, true);
			if (l.size() > 0) {
				user = (User)l.get(0);
			}
		}

		if (user == null) {
			return null;
		}

		String hash = MD5.crypt(user.getEmail() + System.currentTimeMillis());
		um.writeLostPasswordHash(user.getEmail(), hash);

		user.setActivationKey(hash);

		return user;
	}

	// Send lost password email
	public void lostPasswordSend() throws Exception
	{
		String email = this.request.getParameter("email");
		String username = this.request.getParameter("username");

		User user = this.prepareLostPassword(username, email);
		if (user == null) {
			// user could not be found
			this.context.put("message",
					I18n.getMessage("PasswordRecovery.invalidUserEmail"));
			this.lostPassword();
			return;
		}

		try {
			QueuedExecutor.getInstance().execute(
					new EmailSenderTask(new LostPasswordSpammer(user,
							SystemGlobals.getValue(ConfigKeys.MAIL_LOST_PASSWORD_SUBJECT))));
		}
		catch (EmailException e) {
			logger.warn("Error while sending email: " + e);
		}

		this.setTemplateName(TemplateKeys.USER_LOSTPASSWORD_SEND);
		this.context.put("message", I18n.getMessage(
										"PasswordRecovery.emailSent",
										new String[] {
												this.request.getContextPath()
												+ "/user/login"
												+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION)
											}));
	}

	// Recover user password ( aka, ask him a new one )
	public void recoverPassword() throws Exception
	{
		String hash = this.request.getParameter("hash");

		this.setTemplateName(TemplateKeys.USER_RECOVERPASSWORD);
		this.context.put("recoverHash", hash);
	}

	public void recoverPasswordValidate() throws Exception
	{
		String hash = this.request.getParameter("recoverHash");
		String email = this.request.getParameter("email");

		String message = "";

		boolean isOk = DataAccessDriver.getInstance().newUserDAO().validateLostPasswordHash(email, hash);
		if (isOk) {
			String password = this.request.getParameter("newPassword");
			DataAccessDriver.getInstance().newUserDAO().saveNewPassword(MD5.crypt(password), email);

			message = I18n.getMessage("PasswordRecovery.ok",
					new String[] { this.request.getContextPath()
							+ "/user/login"
							+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) });
		}
		else {
			message = I18n.getMessage("PasswordRecovery.invalidData");
		}

		this.setTemplateName(TemplateKeys.USER_RECOVERPASSWORD_VALIDATE);
		this.context.put("message", message);
	}


	public void list() throws Exception
	{
		/*11/10/05 Murthy - commented and moved the code to SakaiJForumUtils*/
//		//int start = this.preparePagination(DataAccessDriver.getInstance().newUserDAO().getTotalUsers());
//		//int usersPerPage = SystemGlobals.getIntValue(ConfigKeys.USERS_PER_PAGE);
//
//		//List users = DataAccessDriver.getInstance().newUserDAO().selectAll(start ,usersPerPage);
//		//11/07/05 Murthy - Get all current users
//		List users = DataAccessDriver.getInstance().newUserDAO().selectAll(0 , 0);
//
//		//added by rashmi 09-15-05
//		//Step 1: find site users through sakai api's
//		String currCourse_id= ToolManager.getCurrentPlacement().getContext();
//		String realmId = "/site/"+currCourse_id;
//		List allsiteusers = new ArrayList();
//		allsiteusers.addAll(RealmService.getRealm(realmId).getUsers());
//		System.out.println("member listing all sakai course members" + allsiteusers.size() + allsiteusers.toString()+allsiteusers.get(0).getClass());
//		List copyallsiteusers = new ArrayList();
//		Iterator copyiter = allsiteusers.iterator();
//		while(copyiter.hasNext())
//		{
//			String copyname = ((String)copyiter.next()).toLowerCase();
//			copyallsiteusers.add(copyname);
//		}
//		
//		//11/07/05 Murthy - for existing site users
//		List exissiteusers = new ArrayList();
////		 add new members added
//		// Step 2: find new users
//		Iterator iter =users.iterator();
//		while(iter.hasNext())
//		{
//			User checkusr = (User)iter.next();
//
//			if (allsiteusers.contains(checkusr.getUsername().toLowerCase()))
//			{
//				//2a. remove from allsiteusers as user is existing
//				allsiteusers.remove(checkusr.getUsername().toLowerCase());
//				
//				////11/07/05 Murthy - add existing site users
//				exissiteusers.add(checkusr);
//			}
//		}
//		// Step 3 : create new users in jforum and add to users list
//		System.out.println("left out users" + allsiteusers.toString());
//		iter = allsiteusers.iterator();
//		UserDAO newusrdao = DataAccessDriver.getInstance().newUserDAO();
//		SakaiUserDAO s = new SakaiUserDAO();
//		while(iter.hasNext())
//		{
//			String newusername = (String)iter.next();
//			System.out.println("creating jforum user for " + newusername);
//			//if user exists in jforum then associate user with group of this course
//			User jfusr = newusrdao.selectByName(newusername);
//			if( jfusr !=null)
//			{
//				//associate with jforum group based on sakai site role
//				Role role = RealmService.getRealm(realmId).getUserRole(newusername);
//				System.out.println(" role found "+ role.getId() + role.contains("jforum.manage") + role.contains("jforum.admin") + role.contains("jforum.member"));
//
//				if(role.contains("jforum.manage"))
//				{
//				newusrdao.addToGroup(jfusr.getId(), new int[]{s.findGroupId("Facilitator")});
//				users.add(jfusr);
//				}else if(role.contains("jforum.member"))
//				{
//				newusrdao.addToGroup(jfusr.getId(), new int[]{s.findGroupId("Participant")});
//				users.add(jfusr);
//				}
//			}
//			else
//			{
//				// 	else create new jforum user
//				// get sakai user attribs
//				try
//				{
//					org.sakaiproject.service.legacy.user.User sakaiusr = UserDirectoryService.getUser(newusername);
//	
//					//create new jforum user
//					User newusr = new User();
//					newusr.setUsername(newusername);
//					newusr.setPassword("password");
//					newusr.setEmail(sakaiusr.getEmail());
//					newusr.setActive(1);
//					if(sakaiusr.getFirstName() == null || sakaiusr.getFirstName().length() == 0)
//						newusr.setFirstName(" ");
//					else newusr.setFirstName(sakaiusr.getFirstName());
//					if(sakaiusr.getLastName() == null || sakaiusr.getLastName().length() == 0)
//						newusr.setLastName("Guest");
//					else newusr.setLastName(sakaiusr.getLastName());
//					newusr.setRegistrationDate(new Date());
//					newusr.setTotalPosts(0);
//					newusr.setKarma(new KarmaStatus());
//				//	newusr.setKarma(new Karma);
//					//11/03/05 Murthy - Commented as addNew() adds the group also
//					//newusrdao.addNew(newusr);
//					newusrdao.addNewUser(newusr);
//					Role role = RealmService.getRealm(realmId).getUserRole(newusername);
//					System.out.println(" role found "+ role.getId() + role.contains("jforum.manage") + role.contains("jforum.admin") + role.contains("jforum.member"));
//	
//					if(role.contains("jforum.manage"))
//					{
//						newusrdao.addToGroup(newusr.getId(), new int[]{s.findGroupId("Facilitator")});
//					}
//					else if(role.contains("jforum.member"))
//					{
//					newusrdao.addToGroup(newusr.getId(), new int[]{s.findGroupId("Participant")});
//					}
//					users.add(newusr);
//				}catch(Exception e)
//				{
//				System.out.println("error while adding this user in member listing" + e.toString());
//				}
//			}
//
//		}
//		
//		//<<<11/07/05 Murthy - step 4: update existing site users to the current sakai role
//		Iterator iterExisUsers = exissiteusers.iterator();
//		while(iterExisUsers.hasNext()){
//			User exisusr = (User)iterExisUsers.next();
//			
//			Role role = RealmService.getRealm(realmId).getUserRole(exisusr.getUsername().toLowerCase());
//			System.out.println("role for existing user "+ role.getId() + role.contains("jforum.manage") + " , " 
//									+ role.contains("jforum.admin") + "," 
//									+ role.contains("jforum.member"));
//
//			if(role.contains("jforum.manage"))
//			{	
//				/*remove all exisitng groups if user belongs to other groups
//				 as Facilitator belong to only one group(Facilitator group)
//				 and add the Facilitator group
//				*/
//				//newusrdao.addToGroup(exisusr.getId(), new int[]{s.findGroupId("Facilitator")});
//				//newusrdao.removeFromGroup
//				User existinguser = newusrdao.selectById(exisusr.getId());
//				//List usrExisGrps = exisusr.getGroupsList();
//				List usrExisGrps = existinguser.getGroupsList();
//				Iterator usrExisGrpsIter = usrExisGrps.iterator();
//				//int facilitatorGrpId = s.getGroupId(exisusr.getUsername());
//				int facilitatorGrpId = s.findGroupId("Facilitator");
//				boolean facGrp = false;
//				while (usrExisGrpsIter.hasNext()){
//					Group usrGrp = (Group)usrExisGrpsIter.next();
//					
//					if (facilitatorGrpId != usrGrp.getId()){
//						newusrdao.removeFromGroup(exisusr.getId(),new int[]{usrGrp.getId()});
//					}else
//						facGrp = true;
//				}
//				
//				//if already in Facilitator group
//				if (facGrp){
//					User existingfacuser = newusrdao.selectById(exisusr.getId());
//					List usrFacGrps = existingfacuser.getGroupsList();
//					
//					if (usrFacGrps !=null && usrFacGrps.size() > 1){
//						//keep only one facilitator group
//						Iterator usrFacGrpsIter = usrFacGrps.iterator();
//						for(int i=0; i <usrFacGrps.size()-1; i++){
//							//Group usrFacGrp = (Group)usrFacGrpsIter.next();
//							Group usrFacGrp = (Group)usrFacGrps.get(i);
//							newusrdao.removeFromGroup(existingfacuser.getId(),new int[]{usrFacGrp.getId()});
//						}
//					}
//				}else{
//					//add Facilitator group
//					newusrdao.addToGroup(exisusr.getId(), new int[]{s.findGroupId("Facilitator")});
//				}
//			}
//			else if(role.contains("jforum.member"))
//			{
//				/*
//				If user belongs to jforum.member get the exisitng user groups
//				 and if don't belong to any group add to Paticipant else check
//				 the groups if the user belongs to Facilitator if belongs remove the 
//				 group leave the other groups(realistically if user belongs to Facilitator he should not
//				 have any other groups)
//				 */
//				//newusrdao.addToGroup(exisusr.getId(), new int[]{s.findGroupId("Participant")});
//				int participantGrpId = s.findGroupId("Participant");
//				int facilitatorGrpId = s.findGroupId("Facilitator");
//				
//				User existinguser = newusrdao.selectById(exisusr.getId());
//				List usrExisGrps = existinguser.getGroupsList();
//				Iterator usrExisGrpsIter = usrExisGrps.iterator();
//				
//				boolean participantGrp = false;
//				while (usrExisGrpsIter.hasNext()){
//					Group usrGrp = (Group)usrExisGrpsIter.next();
//					
//					if (facilitatorGrpId == usrGrp.getId()){
//						//remove facilitator group
//						newusrdao.removeFromGroup(exisusr.getId(),new int[]{usrGrp.getId()});
//					}else if (participantGrpId == usrGrp.getId()){
//						//participant group existing
//						participantGrp = true;
//					}
//				}
//				
//				User existingParuser = newusrdao.selectById(exisusr.getId());
//				List usrParGrps = existingParuser.getGroupsList();
//				//if already in Participant group
//				if (participantGrp){
//					//do nothing and keep the exisitng groups as is
//				}else{
//					//add Participant group
//					if (usrParGrps.size() == 0)
//						newusrdao.addToGroup(exisusr.getId(), new int[]{s.findGroupId("Participant")});
//				}
//			}
//		}
//		//>>>11/07/05 Murthy
//		
////		 dropped students remove from users and delete their association with groups
//		Iterator dropiter =users.iterator();
//		User adminuser = null;
//		while(dropiter.hasNext())
//		{
//			User dropusr = (User)dropiter.next();
//			String dropusername = dropusr.getUsername().toLowerCase();
//			if(dropusername.equals("admin"))
//				adminuser = dropusr;
//			if (!copyallsiteusers.contains(dropusername))
//			{
//				System.out.println("dropusr.getUsername().toLowerCase() : "+dropusername);
//				//<<<11/07/05 Murthy updated to remove all groups associated with this user 
//				User dropthisuser = newusrdao.selectById(dropusr.getId());
//				List usrParGrps = dropthisuser.getGroupsList();
//				Iterator dropUsrIter = usrParGrps.iterator();
//				
//				while (dropUsrIter.hasNext()){
//					Group usrGrp = (Group)dropUsrIter.next();
//					newusrdao.removeFromGroup(dropusr.getId(),new int[]{usrGrp.getId()});
//				}
//				//>>>11/07/05 Murthy updated to remove all groups associated with this user 
//				//newusrdao.removeFromGroup(dropusr.getId(),new int[]{s.getGroupId(dropusername)});
//				dropiter.remove();
//			}
//
//		}
//		if (adminuser !=null) users.remove(adminuser);
		//SakaiJForumUtils sakaiJForumUtils = new SakaiJForumUtils();
		
		int startPage = ViewCommon.getStartPage();
		int totalUsers = 0;
		
		List users = JForumUserUtil.updateMembersInfo(false);
		totalUsers = users.size();		
		
		int start = this.preparePagination(totalUsers);
		
		// int usersPerPage = SystemGlobals.getIntValue(ConfigKeys.USERS_PER_PAGE);
		int usersPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.USERS_PER_PAGE);
				
		int userUpperCount = 0;
		if (users.size() > start + usersPerPage)
			userUpperCount = start + usersPerPage;
		else
			userUpperCount = users.size();
		
		List listPageUsers = new ArrayList();
		for (int i=start; i < userUpperCount; i++ ) {
			User user = (User) users.get(i);
			int totalPosts = DataAccessDriver.getInstance().newUserDAO().getNumberOfMessages(user.getId());
			user.setTotalPosts(totalPosts);
			
			listPageUsers.add(user);
		}
		this.context.put("users", listPageUsers);
		this.setTemplateName(TemplateKeys.USER_LIST);
	}

	/**
	 * @deprecated probably will be removed. Use KarmaAction to load Karma
	 * @throws Exception
	 */
	public void searchKarma() throws Exception
	{
		int start = this.preparePagination(DataAccessDriver.getInstance().newUserDAO().getTotalUsers());
		// int usersPerPage = SystemGlobals.getIntValue(ConfigKeys.USERS_PER_PAGE);
		int usersPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.USERS_PER_PAGE);

		//Load all users with your karma
		List users = DataAccessDriver.getInstance().newUserDAO().selectAllWithKarma(start ,usersPerPage);
		this.context.put("users", users);
		this.setTemplateName(TemplateKeys.USER_SEARCH_KARMA);
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

	public boolean isFacilitator()
	{
		if (logger.isDebugEnabled()) logger.debug("User Action isFacilitator method called !!! yipeee");
		return true;
	}

	//Mallika's new code beg
	public void setLastVisitTime()
	{
	  CourseTimeDAO ctimeDao = DataAccessDriver.getInstance().newCourseTimeDAO();
	  UserSession userSession = SessionFacade.getUserSession();
	  try
		{
		  CourseTimeObj ctimeObj = ctimeDao.selectVisitTime(userSession.getUserId());
		  if (ctimeObj == null)
		  {
		  	if (logger.isDebugEnabled()) logger.debug("MALLIKA SETLASTVISITTIME-TIME OBJECT DOES NOT EXIST");
		  	userSession.setLastVisit(new Date(System.currentTimeMillis()));
		  	CourseTimeObj newCtimeObj = new CourseTimeObj();
		  	newCtimeObj.setCourseId(ToolManager.getCurrentPlacement().getContext());
		  	newCtimeObj.setUserId(userSession.getUserId());
		  	newCtimeObj.setVisitTime(new Date(System.currentTimeMillis()));
		  	ctimeDao.addNew(newCtimeObj);
		  }
		  else
		  {
		  	if (logger.isDebugEnabled()) logger.debug("MALLIKA SETLASTVISITTIME-TIME OBJECT EXISTS");
		  	userSession.setLastVisit(ctimeObj.getVisitTime());
		  	CourseTimeObj newCtimeObj = new CourseTimeObj();
		  	newCtimeObj.setCourseId(ToolManager.getCurrentPlacement().getContext());
		  	newCtimeObj.setUserId(userSession.getUserId());
		  	newCtimeObj.setVisitTime(new Date(System.currentTimeMillis()));
		  	ctimeDao.update(newCtimeObj);
		  }
		}
		catch (Exception e)
		{
			logger.error(this.getClass().getName() + 
					".setLastVisitTime() : " + e.getMessage(), e);
		}
		JForum.setRedirect(this.makeRedirect("list"));
		//this.setTemplateName(TemplateKeys.USER_LAST_VISIT);
	}
	
	public void setMarkAllTime()
	{
	  CourseTimeDAO ctimeDao = DataAccessDriver.getInstance().newCourseTimeDAO();
	  UserSession userSession = SessionFacade.getUserSession();
	  try
		{
		  CourseTimeObj ctimeObj = ctimeDao.selectMarkAllTime(userSession.getUserId());
		  if (ctimeObj == null)
		  {
		  	if (logger.isDebugEnabled()) logger.debug("SETMARKALLTIME-TIME OBJECT DOES NOT EXIST");
		  	userSession.setMarkAllTime(new Date(System.currentTimeMillis()));
		  	CourseTimeObj newCtimeObj = new CourseTimeObj();
		  	newCtimeObj.setCourseId(ToolManager.getCurrentPlacement().getContext());
		  	newCtimeObj.setUserId(userSession.getUserId());
		  	newCtimeObj.setMarkAllTime(new Date(System.currentTimeMillis()));
		  	ctimeDao.addMarkAllNew(newCtimeObj);
		  }
		  else
		  {
		  	if (logger.isDebugEnabled()) logger.debug("SETMARKALLTIME-TIME OBJECT EXISTS");
		  	userSession.setMarkAllTime(new Date(System.currentTimeMillis()));
		  	CourseTimeObj newCtimeObj = new CourseTimeObj();
		  	newCtimeObj.setCourseId(ToolManager.getCurrentPlacement().getContext());
		  	newCtimeObj.setUserId(userSession.getUserId());
		  	newCtimeObj.setMarkAllTime(new Date(System.currentTimeMillis()));
		  	ctimeDao.updateMarkAllTime(newCtimeObj);
		  }
		  
		  // mark all marked topic's as unread to read
		  	List<TopicMarkTimeObj> unreadMarkedTopicMarkTimes = DataAccessDriver.getInstance().newTopicMarkTimeDAO()
					.selectUserCourseUnreadMarkedTopics(SessionFacade.getUserSession().getUserId());
		  	
			for (TopicMarkTimeObj topicMarkTimeObj : unreadMarkedTopicMarkTimes)
			{
				DataAccessDriver.getInstance().newTopicMarkTimeDAO().updateMarkTime(topicMarkTimeObj.getTopicId(), topicMarkTimeObj.getUserId(), new Date(System.currentTimeMillis()), true);
			}
			  
		}
		catch (Exception e)
		{
			logger.error(this.getClass().getName() +
					".setMarkAllTime() : " + e.getMessage(), e);
		}
		JForum.setRedirect(this.makeRedirect("list"));
		//this.setTemplateName(TemplateKeys.USER_LAST_VISIT);
	}	
	//Mallika's new code end
	
	//<<<start - 11/01/05 Murthy - click on Mark all as read, the forum's launch page is displayed  
	/**
	 * gets the redirect path
	 */
	private String makeRedirect(String action)
	{
		String path = this.request.getContextPath() +"/forums/"+ action;
		
		path += SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION);
		
		return path;
	}
	//>>>end
	
}
