/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/forum/PrivateMessageAction.java $ 
 * $Id: PrivateMessageAction.java 69032 2010-07-02 23:28:56Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008, 2009 Etudes, Inc. 
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
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.Command;
import org.etudes.jforum.JForum;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.entities.Post;
import org.etudes.jforum.entities.PrivateMessage;
import org.etudes.jforum.entities.PrivateMessageType;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.entities.UserSession;
import org.etudes.jforum.repository.SmiliesRepository;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.concurrent.executor.QueuedExecutor;
import org.etudes.jforum.util.mail.EmailSenderTask;
import org.etudes.jforum.util.mail.PrivateMessageSpammer;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.preferences.TemplateKeys;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.etudes.jforum.view.forum.common.AttachmentCommon;
import org.etudes.jforum.view.forum.common.PostCommon;
import org.etudes.jforum.view.forum.common.ViewCommon;

/**
 * @author Rafael Steil
 * 10/4/05 - Mallika - Changing check so emails are sent only if user wants them
 * 10/6/05 - Mallika - adding method to search by user's first or last name
 * 10/13/05 - Mallika - adding line of code to set first and last name
 * 10/14/05 - Mallika - getting isnotifyprivatemessageenabled in all situations
 * 07/10/06 - Murthy - updated quote() method to set quoteUser to User
 * 11/13/06 - Murthy - modified to show users in drop down box
 * 11/15/06 - Murthy - modified sendSave method to support sending PM to multiple users
 * 11/23/06 - Mallika - adding code to strip off first Re
 * 11/27/06 - Mallika - adding same code to quote method
 * 12/04/06 - Murthy - updated send method for Maximum number of users allowed 
 * 						to copy in Private Messages
 */
public class PrivateMessageAction extends Command
{
	private String templateName;

	private static Log logger = LogFactory.getLog(PrivateMessageAction.class);

	public void inbox() throws Exception
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}

		User user = new User();
		user.setId(SessionFacade.getUserSession().getUserId());

		List pmList = DataAccessDriver.getInstance().newPrivateMessageDAO().selectFromInbox(user);

		this.context.put("inbox", true);
		this.context.put("pmList", pmList);
		this.setTemplateName(TemplateKeys.PM_INBOX);
		this.putTypes();
	}

	public void sentbox() throws Exception
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}

		User user = new User();
		user.setId(SessionFacade.getUserSession().getUserId());

		List pmList = DataAccessDriver.getInstance().newPrivateMessageDAO().selectFromSent(user);

		this.context.put("sentbox", true);
		this.context.put("pmList", pmList);
		this.setTemplateName(TemplateKeys.PM_SENTBOX);
		this.putTypes();
	}

	private void putTypes()
	{
		this.context.put("NEW", new Integer(PrivateMessageType.NEW));
		this.context.put("READ", new Integer(PrivateMessageType.READ));
		this.context.put("UNREAD", new Integer(PrivateMessageType.UNREAD));
		this.context.put("PRIORITY_HIGH", new Integer(PrivateMessage.PRIORITY_HIGH));
	}

	public void send() throws Exception
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}

		User user = DataAccessDriver.getInstance().newUserDAO().selectById(
						SessionFacade.getUserSession().getUserId());
		user.setSignature(PostCommon.processText(user.getSignature()));
		user.setSignature(PostCommon.processSmilies(user.getSignature(), SmiliesRepository.getSmilies()));

		this.sendFormCommon(user);

		//11/13/2006 - Murthy - updated to show user in drop down box
		//SakaiJForumUtils sakaiJForumUtils = new SakaiJForumUtils();
		List users = null;
		try {
			users = JForumUserUtil.updateMembersInfo();
			this.context.put("users", users);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) logger.error(e.toString());
		}
		//12/04/06 - Murthy - Maximum number of users allowed to copy in Private Messages
		//this.context.put("maxPMToUsers",  new Integer(SystemGlobals.getIntValue(ConfigKeys.MAX_PM_TOUSERS)));
		this.context.put("maxPMToUsers",  new Integer(SakaiSystemGlobals.getIntValue(ConfigKeys.MAX_PM_TOUSERS)));
	}
	
	
	public void sendTo() throws Exception
	{
		if (!SessionFacade.isLogged()) 
		{
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}

		User user = DataAccessDriver.getInstance().newUserDAO().selectById(
				SessionFacade.getUserSession().getUserId());

		int userId = this.request.getIntParameter("user_id");
		
		if (userId > 0)
		{
			User user1 = DataAccessDriver.getInstance().newUserDAO().selectById(userId);
			
			if (!JForumUserUtil.isUserActive(user1.getSakaiUserId()))
			{
				this.setTemplateName(TemplateKeys.PM_SENDSAVE_USER_NOTFOUND);
				this.context.put("message", I18n.getMessage("PrivateMessage.userInactive"));
				return;
			}

			
			this.context.put("pmRecipient", user1);
			this.context.put("toUserId", String.valueOf(user1.getId()));

            //Mallika's comments beg
			//this.context.put("toUsername", user1.getUsername());
			//Mallika's comments end

			//Mallika's new code beg
			this.context.put("toUsername", user1.getFirstName()+" "+user1.getLastName());
			//Mallika's new code end

			this.context.put("toUserEmail", user1.getEmail());
			
			this.context.put("pageTitle", SystemGlobals.getValue(ConfigKeys.FORUM_NAME) + " - " + I18n.getMessage("PrivateMessage.title"));
		}
		else
		{
			this.setTemplateName(TemplateKeys.PM_SENDSAVE_USER_NOTFOUND);
			this.context.put("message", I18n.getMessage("PrivateMessage.PrivateMessage.userNotFound"));
			return;
		}

		this.sendFormCommon(user);
		
		this.context.put("action", "sendPMSave");
		this.setTemplateName(TemplateKeys.PM_SENDTO);
	}

	private void sendFormCommon(User user)
	{
		this.context.put("user", user);
		this.context.put("moduleName", "pm");
		this.context.put("action", "sendSave");
		this.setTemplateName(TemplateKeys.PM_SENDFORM);
		this.context.put("htmlAllowed", true);
		// this.context.put("maxAttachments", SystemGlobals.getValue(ConfigKeys.ATTACHMENTS_MAX_POST));
		this.context.put("maxAttachments", SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_MAX_POST));
		//12/11/2007 Murthy - attachments enabled
		this.context.put("attachmentsEnabled", true);
		this.context.put("maxAttachmentsSize", new Integer(0));
		this.context.put("pmPost", true);
	}

	public void sendSave() throws Exception
	{
		if (logger.isDebugEnabled()) logger.debug("PrivateMessageAction - sendSave");
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}

		String sId = this.request.getParameter("toUserId");
		String toUsername = this.request.getParameter("toUsername");
		String userEmail = this.request.getParameter("toUserEmail");

		boolean pmEmailEnabled = false;
		
		PrivateMessage pm = new PrivateMessage();
		pm.setPost(PostCommon.fillPostFromRequest());
		
		if ((this.request.getParameter("high_priority_pm") != null) && (this.request.getIntParameter("high_priority_pm") == 1))
		{
			pm.setPriority(PrivateMessage.PRIORITY_HIGH);
		}
		else
		{
			pm.setPriority(PrivateMessage.PRIORITY_GENERAL);
		}
		
		User fromUser = DataAccessDriver.getInstance().newUserDAO().selectById(SessionFacade.getUserSession().getUserId());
		pm.setFromUser(fromUser);
		
		
		boolean preview = (this.request.getParameter("preview") != null && this.request.getParameter("preview").trim().length() > 0);
		
		if (preview) {
			if (sId == null || sId.trim().equals("")) {

				String toUserIds[] = (String[])this.request.getObjectParameter("toUsername"+"ParamValues");
				if (toUserIds != null){
					List toUsers = new ArrayList();
								
					for (int i = 0; i < toUserIds.length; i++) {
						if (toUserIds[i] != null && toUserIds[i].trim().length() > 0) {
							
							int toUserId = Integer.parseInt(toUserIds[i]);
							User toUser = DataAccessDriver.getInstance().newUserDAO().selectById(toUserId);
							// pm.setToUser(toUser);
							toUsers.add(toUser);
						}
					}
					this.context.put("toUsers", toUsers);
				}
				else
				{
					List toUsers = new ArrayList();
					
					int toUserId = Integer.parseInt(toUsername);
					User toUser = DataAccessDriver.getInstance().newUserDAO().selectById(toUserId);
					//pm.setToUser(toUser);
					toUsers.add(toUser);
					this.context.put("toUsers", toUsers);
				}
			} else {
				// PM quote reply
				int toUserId = Integer.parseInt(sId);
				User usr = DataAccessDriver.getInstance().newUserDAO().selectById(toUserId);
				toUsername = usr.getFirstName()+ " "+usr.getLastName();
				pmEmailEnabled = usr.isNotifyPrivateMessagesEnabled();
				
				String exisPmId = this.request.getParameter("exisPmId");
				
				if (exisPmId != null && exisPmId.trim().length() > 0)
				{
					PrivateMessage exisPm = new PrivateMessage();
					exisPm.setId(Integer.parseInt(exisPmId));

					exisPm = DataAccessDriver.getInstance().newPrivateMessageDAO().selectById(exisPm);
					
					pm.setFromUser(exisPm.getFromUser());
					pm.setToUser(exisPm.getToUser());
					pm.setId(exisPm.getId());
				}
			}
			this.context.put("preview", true);
			this.context.put("post", pm.getPost());

			Post postPreview = new Post(pm.getPost());
			this.context.put("postPreview", PostCommon.preparePostForDisplay(postPreview));
			this.context.put("pm", pm);
			this.context.put("maxPMToUsers",  new Integer(SakaiSystemGlobals.getIntValue(ConfigKeys.MAX_PM_TOUSERS)));
			
			List users = null;
			try {
				users = JForumUserUtil.updateMembersInfo();
				this.context.put("users", users);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) logger.error(e.toString());
			}

			this.send();
			return;
		}
		
		//save PM attachments only once
		int attachmentIds[] = null;
		if (!preview)
		{
			try
			{
				attachmentIds = addPMAttachments();
			}
			catch (Exception e)
			{
				this.context.put("post", pm.getPost());
				this.context.put("pm", pm);
				this.send();
				return;
			}			
		}
		
		int toUserId = -1;
		if (sId == null || sId.trim().equals("")) {

			String toUserIds[] = (String[])this.request.getObjectParameter("toUsername"+"ParamValues");
			if (toUserIds != null){
							
				for (int i = 0; i < toUserIds.length; i++) {
					if (toUserIds[i] != null && toUserIds[i].trim().length() > 0) {
						toUserId = Integer.parseInt(toUserIds[i]);
						User usr = DataAccessDriver.getInstance().newUserDAO().selectById(toUserId);
						toUserId = usr.getId();
						userEmail = usr.getEmail();
						toUsername = usr.getFirstName()+ " "+usr.getLastName();
						pmEmailEnabled = usr.isNotifyPrivateMessagesEnabled();
						sendPrivateMessage(pm, toUsername, userEmail, pmEmailEnabled, toUserId, attachmentIds);
					}
				}
			}else{
				toUserId = Integer.parseInt(toUsername);
				User usr = DataAccessDriver.getInstance().newUserDAO().selectById(toUserId);
				toUserId = usr.getId();
				userEmail = usr.getEmail();
				toUsername = usr.getFirstName()+ " "+usr.getLastName();
				pmEmailEnabled = usr.isNotifyPrivateMessagesEnabled();
				sendPrivateMessage(pm, toUsername, userEmail, pmEmailEnabled, toUserId, attachmentIds);
			}
		}
		else {
			toUserId = Integer.parseInt(sId);
            //Mallika - new code beg
			//Needed to do this because otherwise emailEnabled is not used
			User usr = DataAccessDriver.getInstance().newUserDAO().selectById(toUserId);
			toUsername = usr.getFirstName()+ " "+usr.getLastName();
			pmEmailEnabled = usr.isNotifyPrivateMessagesEnabled();
			//Mallika - new code end

			sendPrivateMessage(pm, toUsername, userEmail, pmEmailEnabled, toUserId, attachmentIds);
		}
		
		// update private message status to replied
		if (!preview) {
			String exisPmId = this.request.getParameter("exisPmId");
			
			if (exisPmId != null && exisPmId.trim().length() > 0)
			{
				PrivateMessage exisPm = new PrivateMessage();
				exisPm.setId(Integer.parseInt(exisPmId));

				exisPm = DataAccessDriver.getInstance().newPrivateMessageDAO().selectById(exisPm);
				exisPm.setReplied(true);
				DataAccessDriver.getInstance().newPrivateMessageDAO().updateRepliedStatus(exisPm);
			}				
		}

		//boolean preview = (this.request.getParameter("preview") != null);
		//if (logger.isDebugEnabled()) logger.debug("Preview is "+preview);
		if (preview) {
			this.context.put("preview", true);
			this.context.put("post", pm.getPost());

			Post postPreview = new Post(pm.getPost());
			this.context.put("postPreview", PostCommon.preparePostForDisplay(postPreview));
			this.context.put("pm", pm);

			this.send();
		}else{
			this.setTemplateName(TemplateKeys.PM_SENDSAVE);
			
			this.context.put("message", I18n.getMessage("PrivateMessage.messageSent",
							new String[] { this.request.getContextPath() +"/pm/inbox"
											+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION)}));
		}
	}
	
	
	/**
	 * save private message sent to individual user from pop up window
	 * @throws Exception
	 */
	public void sendPMSave() throws Exception
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}

		String sId = this.request.getParameter("toUserId");
		String toUsername = this.request.getParameter("toUsername");
		String userEmail = this.request.getParameter("toUserEmail");
		
		boolean preview = (this.request.getParameter("preview") != null && this.request.getParameter("preview").trim().length() > 0);
				
		boolean pmEmailEnabled = false;
		
		PrivateMessage pm = new PrivateMessage();
		pm.setPost(PostCommon.fillPostFromRequest());

		if ((this.request.getParameter("high_priority_pm") != null) && (this.request.getIntParameter("high_priority_pm") == 1))
		{
			pm.setPriority(PrivateMessage.PRIORITY_HIGH);
		}
		else
		{
			pm.setPriority(PrivateMessage.PRIORITY_GENERAL);
		}
		
		if (preview) 
		{
			this.context.put("postPreview", pm);
			this.context.put("preview", true);
			this.request.addParameter("user_id", sId);
			this.sendTo();
			return;
		}
		
		int attachmentIds[] = null;
		try
		{
			attachmentIds = addPMAttachments();
		}
		catch (Exception e)
		{
			this.request.addParameter("user_id", sId);
			this.sendTo();
			return;
		}			
		int toUserId = Integer.parseInt(sId);
       	User usr = DataAccessDriver.getInstance().newUserDAO().selectById(toUserId);
		toUsername = usr.getFirstName()+ " "+usr.getLastName();
		pmEmailEnabled = usr.isNotifyPrivateMessagesEnabled();
		
		sendPrivateMessage(pm, toUsername, userEmail, pmEmailEnabled, toUserId, attachmentIds);

		this.context.put("savesucess", true);
		this.setTemplateName(TemplateKeys.PM_SENDTOSAVE);
	}
	
	/**
	 * save attachments
	 * @return attachment Id's
	 * @throws Exception
	 */
	private int[] addPMAttachments() throws Exception
	{
		AttachmentCommon attachments = new AttachmentCommon(this.request);

		try
		{
			attachments.preProcess();
		}
		catch (Exception e)
		{
			JForum.enableCancelCommit();
			this.context.put("errorMessage", e.getMessage());
			throw e;
		}

		return attachments.insertPMAttachments();
	}

	/**
	 * send private message
	 * @param pm - PrivateMessage
	 * @param toUsername - to user name
	 * @param userEmail - user email
	 * @param pmEmailEnabled - is pm enabled
	 * @param toUserId - to user id
	 * @param attachmentIds - attachment Id's
	 * @throws Exception
	 */
	private void sendPrivateMessage(PrivateMessage pm, String toUsername, String userEmail, boolean pmEmailEnabled, int toUserId, int attachmentIds[]) throws Exception {
		if (toUserId == -1) {
			this.setTemplateName(TemplateKeys.PM_SENDSAVE_USER_NOTFOUND);
			this.context.put("message", I18n.getMessage("PrivateMessage.userIdNotFound"));
			return;
		}

		User fromUser = DataAccessDriver.getInstance().newUserDAO().selectById(SessionFacade.getUserSession().getUserId());
		pm.setFromUser(fromUser);

		User toUser = new User();
		toUser.setId(toUserId);
		toUser.setUsername(toUsername);
		toUser.setEmail(userEmail);
		pm.setToUser(toUser);

		boolean preview = (this.request.getParameter("preview") != null);

		if (!preview) {
			//DataAccessDriver.getInstance().newPrivateMessageDAO().send(pm);
			DataAccessDriver.getInstance().newPrivateMessageDAO().saveMessage(pm, attachmentIds);

			/*this.setTemplateName(TemplateKeys.PM_SENDSAVE);
			this.context.put("message", I18n.getMessage("PrivateMessage.messageSent",
							new String[] { this.request.getContextPath() +"/pm/inbox"
											+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION)}));*/

			// If the target user if in the forum, then increments its
			// private messate count
			String sid = SessionFacade.isUserInSession(toUserId);
			if (sid != null) {
				UserSession us = SessionFacade.getUserSession(sid);
				us.setPrivateMessages(us.getPrivateMessages() + 1);
			}
			
			if (logger.isDebugEnabled()) logger.debug("Before userEmail");
			if (userEmail != null && userEmail.trim().length() > 0) {
				if (logger.isDebugEnabled()) logger.debug("Useremail is not null "+pmEmailEnabled);
				//Mallika-commenting line below and going based off of settings instead
				/*if (SystemGlobals.getBoolValue(ConfigKeys.MAIL_NOTIFY_ANSWERS)) {*/
				if ((pmEmailEnabled == true) || (pm.getPriority() == PrivateMessage.PRIORITY_HIGH)) {
					if (logger.isDebugEnabled()) logger.debug("Sending email");
					
					try	{
						new InternetAddress(toUser.getEmail());
					} catch (AddressException e) {
						if (logger.isWarnEnabled()) logger.warn("sendPrivateMessage(...) : "+ toUser.getEmail() + " is invalid. And exception is : "+ e);
						return;
					}
					
					//get attachments
					List attachments = DataAccessDriver.getInstance().newAttachmentDAO().selectPMAttachments(pm.getId());
					try {
						if (attachments != null && attachments.size() > 0)
						{
							QueuedExecutor.getInstance().execute(new EmailSenderTask(new PrivateMessageSpammer(toUser, pm, attachments)));
						}
						else
						{
							QueuedExecutor.getInstance().execute(new EmailSenderTask(new PrivateMessageSpammer(toUser, pm)));
						}
					}
					catch (Exception e) {
						logger.error(this.getClass().getName() +
								".sendSave() : " + e.getMessage(), e);
					}
				}
			}
		}
	}
	
	public void findUser() throws Exception
	{
		boolean showResult = false;
		String username = this.request.getParameter("username");

		if (username != null && !username.equals("")) {
			List namesList = DataAccessDriver.getInstance().newUserDAO().findByName(username, false);
			this.context.put("namesList", namesList);
			showResult = true;
		}

		this.setTemplateName(TemplateKeys.PM_FIND_USER);

		this.context.put("username", username);
		this.context.put("showResult", showResult);
	}

	//Mallika's new code beg
	public void findFluser() throws Exception
	{
		boolean showResult = false;
		String username = this.request.getParameter("username");

		if (username != null && !username.equals("")) {
			List namesList = DataAccessDriver.getInstance().newUserDAO().findByFlName(username, false);
			this.context.put("namesList", namesList);
			showResult = true;
		}

		this.setTemplateName(TemplateKeys.PM_FIND_USER);

		this.context.put("username", username);
		this.context.put("showResult", showResult);
	}
	//Mallika's new code end

	public void read() throws Exception
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}

		int id = this.request.getIntParameter("id");

		PrivateMessage pm = new PrivateMessage();
		pm.setId(id);

		pm = DataAccessDriver.getInstance().newPrivateMessageDAO().selectById(pm);

		// Don't allow the read of messages that don't belongs
		// to the current user
		UserSession us = SessionFacade.getUserSession();
		int userId = us.getUserId();
		if (pm.getToUser().getId() == userId || pm.getFromUser().getId() == userId) {
			pm.getPost().setText(PostCommon.preparePostForDisplay(pm.getPost()).getText());

			// Update the message status, if needed
			if (pm.getType() == PrivateMessageType.NEW) {
				pm.setType(PrivateMessageType.READ);
				DataAccessDriver.getInstance().newPrivateMessageDAO().updateType(pm);
				us.setPrivateMessages(us.getPrivateMessages() - 1);
			}

			User u = pm.getFromUser();
			u.setSignature(PostCommon.processText(u.getSignature()));
            u.setSignature(PostCommon.processSmilies(u.getSignature(),
            		SmiliesRepository.getSmilies()));

			this.context.put("pm", pm);
			this.setTemplateName(TemplateKeys.PM_READ);
			
			this.context.put("am", new AttachmentCommon(this.request));
		}
		else {
			this.setTemplateName(TemplateKeys.PM_READ_DENIED);
			this.context.put("message", I18n.getMessage("PrivateMessage.readDenied"));
		}
	}

	public void review() throws Exception
	{
		this.read();
		this.setTemplateName(TemplateKeys.PM_READ_REVIEW);
	}

	public void delete() throws Exception
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}

		String ids[] = this.request.getParameterValues("id");

		if (ids != null && ids.length > 0) {
			PrivateMessage[] pm = new PrivateMessage[ids.length];
			User u = new User();
			u.setId(SessionFacade.getUserSession().getUserId());

			for (int i = 0; i < ids.length; i++) {
				pm[i] = new PrivateMessage();
				pm[i].setFromUser(u);
				pm[i].setId(Integer.parseInt(ids[i]));
			}
			
			//delete attachments if any
			if (pm != null)
			{
				for (int i = 0; i < pm.length; i++)
				{
					new AttachmentCommon(this.request).deletePMAttachments(pm[i].getId());
				}
			}

			DataAccessDriver.getInstance().newPrivateMessageDAO().delete(pm);
		}

		this.setTemplateName(TemplateKeys.PM_DELETE);
		this.context.put("message", I18n.getMessage("PrivateMessage.deleteDone",
						new String[] { this.request.getContextPath() + "/pm/inbox"
										+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) }));
	}
	
	public void reply() throws Exception
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}

		int id = this.request.getIntParameter("id");

		PrivateMessage pm = new PrivateMessage();
		pm.setId(id);
		pm = DataAccessDriver.getInstance().newPrivateMessageDAO().selectById(pm);

		int userId = SessionFacade.getUserSession().getUserId();

		if (pm.getToUser().getId() != userId && pm.getFromUser().getId() != userId) {
			this.setTemplateName(TemplateKeys.PM_READ_DENIED);
			this.context.put("message", I18n.getMessage("PrivateMessage.readDenied"));
			return;
		}
		//Mallika - adding this if to strip out additional Res
		//Only add Re: at beginning if this is the first reply
		if (pm.getPost().getSubject().length() >= 3)
		{
		  if (!(pm.getPost().getSubject().substring(0,3).equals("Re:")))
		  {
		    pm.getPost().setSubject(pm.getPost().getSubject());
		  }
	    }
		else
		{
			 pm.getPost().setSubject(pm.getPost().getSubject());
			 
		}


		this.context.put("pm", pm);
		this.context.put("pmReply", true);

		this.sendFormCommon(DataAccessDriver.getInstance().newUserDAO().selectById(
				SessionFacade.getUserSession().getUserId()));
	}

	public void quote() throws Exception
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}

		int id = this.request.getIntParameter("id");

		PrivateMessage pm = new PrivateMessage();
		pm.setId(id);
		pm = DataAccessDriver.getInstance().newPrivateMessageDAO().selectById(pm);

		int userId = SessionFacade.getUserSession().getUserId();

		if (pm.getToUser().getId() != userId && pm.getFromUser().getId() != userId) {
			this.setTemplateName(TemplateKeys.PM_READ_DENIED);
			this.context.put("message", I18n.getMessage("PrivateMessage.readDenied"));
			return;
		}
       
		//Mallika - adding this if to strip out additional Res
		//Only add Re: at beginning if this is the first reply
		if (pm.getPost().getSubject().length() >= 3)
		{
		  if (!(pm.getPost().getSubject().substring(0,3).equals("Re:")))
		  {
		    pm.getPost().setSubject(pm.getPost().getSubject());
		  }
	    }
		else
		{
			 pm.getPost().setSubject(pm.getPost().getSubject());
			 
		}
		
		this.sendFormCommon(DataAccessDriver.getInstance().newUserDAO().selectById(userId));

		this.context.put("quote", "true");
		this.context.put("quoteUser", pm.getFromUser());
		this.context.put("post", pm.getPost());
		this.context.put("pm", pm);
	}
	
	/**
	 * flag or clear the flag of the PM to follow up
	 * @throws Exception
	 */
	public void flag() throws Exception
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}

		int id = this.request.getIntParameter("id");

		PrivateMessage pm = new PrivateMessage();
		pm.setId(id);

		pm = DataAccessDriver.getInstance().newPrivateMessageDAO().selectById(pm);

		// Don't allow the flag the message that don't belongs
		// to the current user
		UserSession us = SessionFacade.getUserSession();
		int userId = us.getUserId();
		if (pm.getToUser().getId() == userId || pm.getFromUser().getId() == userId) {
			
			pm.getPost().setText(PostCommon.preparePostForDisplay(pm.getPost()).getText());
			
			// Update the flag
			pm.setFlagToFollowup(!pm.isFlagToFollowup());
			DataAccessDriver.getInstance().newPrivateMessageDAO().updateFlagToFollowup(pm);
		}
		else 
		{
			this.setTemplateName(TemplateKeys.PM_FLAG_FOLLOWUP_DENIED);
			this.context.put("message", I18n.getMessage("PrivateMessage.flagToFollowUpDenied"));
		}
		this.read();
	}
	
	/**
	 * add the flag of the PM to follow up
	 * @throws Exception
	 */
	public void addFlag() throws Exception
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}

		String ids[] = this.request.getParameterValues("id");
		UserSession us = SessionFacade.getUserSession();
		int userId = us.getUserId();
		
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				PrivateMessage pm = new PrivateMessage();
				pm.setId(Integer.parseInt(ids[i]));
	
				pm = DataAccessDriver.getInstance().newPrivateMessageDAO().selectById(pm);
				
				if (pm == null)
					continue;
				
				if (pm.getToUser().getId() == userId || pm.getFromUser().getId() == userId) {
				
					// Update the flag
					pm.setFlagToFollowup(true);
					DataAccessDriver.getInstance().newPrivateMessageDAO().updateFlagToFollowup(pm);
				}
			}
		}

		this.setTemplateName(TemplateKeys.PM_FLAGGED);
		this.context.put("message", I18n.getMessage("PrivateMessage.flaggingDone",
						new String[] { this.request.getContextPath() + "/pm/inbox"
										+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) }));
	}
	
	/**
	 * clear the flag of the PM to follow up
	 * @throws Exception
	 */
	public void clearFlag() throws Exception
	{
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());
			return;
		}

		String ids[] = this.request.getParameterValues("id");
		UserSession us = SessionFacade.getUserSession();
		int userId = us.getUserId();
		
		if (ids != null && ids.length > 0) {
			for (int i = 0; i < ids.length; i++) {
				PrivateMessage pm = new PrivateMessage();
				pm.setId(Integer.parseInt(ids[i]));
	
				pm = DataAccessDriver.getInstance().newPrivateMessageDAO().selectById(pm);
				
				if (pm == null)
					continue;
				
				if (pm.getToUser().getId() == userId || pm.getFromUser().getId() == userId) {
				
					// Update the flag
					pm.setFlagToFollowup(false);
					DataAccessDriver.getInstance().newPrivateMessageDAO().updateFlagToFollowup(pm);
				}
			}
		}

		this.setTemplateName(TemplateKeys.PM_FLAGGED);
		this.context.put("message", I18n.getMessage("PrivateMessage.flaggingDone",
						new String[] { this.request.getContextPath() + "/pm/inbox"
										+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) }));
	}
	

	/**
	 * @see org.etudes.jforum.Command#list()
	 */
	public void list() throws Exception
	{
		this.inbox();
	}
}
