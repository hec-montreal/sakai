/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/forum/AjaxAction.java $ 
 * $Id: AjaxAction.java 65037 2009-12-03 22:44:06Z murthy@etudes.org $ 
 ***********************************************************************************
 * Code changed after November 18, 2009 Copyright (c) 2008, 2009 Etudes, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 
 * Portions completed before November 18, 2009 (c) JForum Team
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met:
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following  disclaimer.
 * 2)  Redistributions in binary form must reproduce the 
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
 * 
 * Created on 09/08/2007 09:31:17
 * 
 * The JForum Project
 * http://www.jforum.net
 */
package org.etudes.jforum.view.forum;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.Command;
import org.etudes.jforum.dao.CategoryDAO;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.entities.Category;
import org.etudes.jforum.entities.Post;
import org.etudes.jforum.entities.PrivateMessage;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.SafeHtml;
import org.etudes.jforum.util.preferences.TemplateKeys;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.etudes.jforum.view.admin.CategoryAction;
import org.etudes.jforum.view.forum.common.PostCommon;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.user.cover.UserDirectoryService;


/**
 * @author Rafael Steil
 */
public class AjaxAction extends Command
{
	private static Log logger = LogFactory.getLog(CategoryAction.class);
	
	/**
	 * Sends a test message
	 * @param sender The sender's email address
	 * @param host the smtp host
	 * @param auth if need authorization or not
	 * @param username the smtp server username, if auth is needed
	 * @param password the smtp server password, if auth is needed
	 * @param to the recipient
	 * @return The status message
	 *//*
	public void sendTestMail()
	{
		String sender = this.request.getParameter("sender");
		String host = this.request.getParameter("host");
		String port = this.request.getParameter("port");
		String auth = this.request.getParameter("auth");
		String ssl = this.request.getParameter("ssl");
		String username = this.request.getParameter("username");
		String password = this.request.getParameter("password");
		String to = this.request.getParameter("to");
		
		// Save the current values
		String originalHost = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_HOST);
		String originalAuth = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_AUTH);
		String originalUsername = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_USERNAME);
		String originalPassword = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_PASSWORD);
		String originalSender = SystemGlobals.getValue(ConfigKeys.MAIL_SENDER);
		String originalSSL = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_SSL);
		String originalPort = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_PORT);
		
		// Now put the new ones
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_HOST, host);
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_AUTH, auth);
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_USERNAME, username);
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_PASSWORD, password);
		SystemGlobals.setValue(ConfigKeys.MAIL_SENDER, sender);
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_SSL, ssl);
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_PORT, port);
		
		String status = "OK";
		
		// Send the test mail
		class TestSpammer extends Spammer {
			public TestSpammer(String to) {
				List l = new ArrayList();
				
				User user = new User();
				user.setEmail(to);
				
				l.add(user);
				
				this.setUsers(l);
				
				this.setTemplateParams(new SimpleHash());
				this.prepareMessage("JForum Test Mail", null);
			}
			
			protected String processTemplate() throws Exception {
				return ("Test mail from JForum Admin Panel. Sent at " + new Date());
			}
			
			protected void createTemplate(String messageFile) throws Exception {}
		}
		
		Spammer s = new TestSpammer(to);
		
		try {
			s.dispatchMessages();
		}
		catch (Exception e) {
			status = StringEscapeUtils.escapeJavaScript(e.toString());
			logger.error(e.toString(), e);
		}
		finally {
			// Restore the original values
			SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_HOST, originalHost);
			SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_AUTH, originalAuth);
			SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_USERNAME, originalUsername);
			SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_PASSWORD, originalPassword);
			SystemGlobals.setValue(ConfigKeys.MAIL_SENDER, originalSender);
			SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_SSL, originalSSL);
			SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_PORT, originalPort);
		}
		
		this.setTemplateName(TemplateKeys.AJAX_TEST_MAIL);
		this.context.put("status", status);
	}
	
	public void isPostIndexed()
	{
		int postId = this.request.getIntParameter("post_id");

		this.setTemplateName(TemplateKeys.AJAX_IS_POST_INDEXED);
		
		LuceneManager manager = (LuceneManager)SearchFacade.manager();
		Document doc = manager.luceneSearch().findDocumentByPostId(postId);
		
		this.context.put("doc", doc);
	}
	
	public void loadPostContents()
	{
		int postId = this.request.getIntParameter("id");
		PostDAO dao = DataAccessDriver.getInstance().newPostDAO();
		Post post = dao.selectById(postId);
		this.setTemplateName(TemplateKeys.AJAX_LOAD_POST);
		this.context.put("post", post);
	}
	
	public void savePost()
	{
		PostDAO postDao = DataAccessDriver.getInstance().newPostDAO();
		Post post = postDao.selectById(this.request.getIntParameter("id"));
		
		String originalMessage = post.getText();
		
		if (!PostCommon.canEditPost(post)) {
			post = PostCommon.preparePostForDisplay(post);
		}
		else {
			post.setText(this.request.getParameter("value"));
			postDao.update(post);
			SearchFacade.update(post);
			post = PostCommon.preparePostForDisplay(post);
		}
		
		boolean isModerator = SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION_POST_EDIT);
		
		if (SystemGlobals.getBoolValue(ConfigKeys.MODERATION_LOGGING_ENABLED)
				&& isModerator && post.getUserId() != SessionFacade.getUserSession().getUserId()) {
			ModerationHelper helper = new ModerationHelper();
			
			this.request.addParameter("log_original_message", originalMessage);
			this.request.addParameter("post_id", String.valueOf(post.getId()));
			this.request.addParameter("topic_id", String.valueOf(post.getTopicId()));
			
			ModerationLog log = helper.buildModerationLogFromRequest();
			log.getPosterUser().setId(post.getUserId());
			
			helper.saveModerationLog(log);
		}
		
		if (SystemGlobals.getBoolValue(ConfigKeys.POSTS_CACHE_ENABLED)) {
			PostRepository.update(post.getTopicId(), PostCommon.preparePostForDisplay(post));
		}
		
		this.setTemplateName(TemplateKeys.AJAX_LOAD_POST);
		this.context.put("post", post);
	}*/
	
	public void previewPost()
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		boolean isParticipant = JForumUserUtil.isJForumParticipant(UserDirectoryService.getCurrentUser().getId());
		
		if (!isParticipant) {
			if (!isfacilitator )
			{
				Post post = new Post();
				
				post.setText("");
				post.setSubject("");
				return;
			}
		}
		
		Post post = new Post();
		
		post.setText(this.request.getParameter("text"));
		post.setSubject(this.request.getParameter("subject"));
		post.setHtmlEnabled("true".equals(this.request.getParameter("html")));
		//post.setBbCodeEnabled("true".equals(this.request.getParameter("bbcode")));
		//post.setSmiliesEnabled("true".equals(this.request.getParameter("smilies")));
		
		if (post.isHtmlEnabled()) {
			post.setText(new SafeHtml().makeSafe(post.getText()));
		}
		
		post = PostCommon.preparePostForDisplay(post);
		post.setSubject(StringEscapeUtils.escapeJavaScript(post.getSubject()));
		post.setText(StringEscapeUtils.escapeJavaScript(post.getText()));

		this.setTemplateName(TemplateKeys.AJAX_PREVIEW_POST);
		this.context.put("post", post);
	}
	
	/**
	 * @see net.jforum.Command#list()
	 */
	public void list()
	{
		this.ignoreAction();
	}
	
	/**
	 * validate category grading option
	 */
	public void validateCategoryGrading()
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		this.setTemplateName(TemplateKeys.AJAX_GRADING_CATEGORY_VALIDATION);
		
		if (!isfacilitator) {
			this.context.put("errorValidation", I18n.getMessage("User.NotAuthorized"));
			return;
		}
		
		int categoryId = this.request.getIntParameter("categories_id");
		
		CategoryDAO cm = DataAccessDriver.getInstance().newCategoryDAO();
		
		try
		{
			Category c = cm.selectById(categoryId);
			this.context.put("c", c);
		}
		catch (Exception e)
		{
			if (logger.isErrorEnabled()) logger.warn(e);
		}		
	}
	
	/**
	 * preview PM
	 */
	public void previewPM()
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		boolean isParticipant = JForumUserUtil.isJForumParticipant(UserDirectoryService.getCurrentUser().getId());
		
		if (!isParticipant) {
			if (!isfacilitator )
			{
				PrivateMessage pm = new PrivateMessage();
				Post post = new Post();
				post.setText("");
				post.setSubject("");
				
				pm.setPost(post);
				return;
			}
		}
		
		PrivateMessage pm = new PrivateMessage();
		Post post = new Post();
		
		post.setText(this.request.getParameter("text"));
		post.setSubject(this.request.getParameter("subject"));
		post.setHtmlEnabled("true".equals(this.request.getParameter("html")));
		//post.setBbCodeEnabled("true".equals(this.request.getParameter("bbcode")));
		//post.setSmiliesEnabled("true".equals(this.request.getParameter("smilies")));
		
		if (post.isHtmlEnabled()) {
			post.setText(new SafeHtml().makeSafe(post.getText()));
		}
		
		post = PostCommon.preparePostForDisplay(post);
		post.setSubject(StringEscapeUtils.escapeJavaScript(post.getSubject()));
		post.setText(StringEscapeUtils.escapeJavaScript(post.getText()));
		pm.setPost(post);
		this.setTemplateName(TemplateKeys.AJAX_PREVIEW_PM);
		this.context.put("postPreview", pm);
	}
}
