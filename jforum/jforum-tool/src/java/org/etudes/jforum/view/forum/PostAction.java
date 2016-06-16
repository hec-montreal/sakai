/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/forum/PostAction.java $ 
 * $Id: PostAction.java 71044 2010-10-29 17:37:24Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008, 2009, 2010 Etudes, Inc. 
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

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.api.app.jforum.JForumGBService;
import org.etudes.jforum.Command;
import org.etudes.jforum.JForum;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.dao.AttachmentDAO;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.EvaluationDAO;
import org.etudes.jforum.dao.ForumDAO;
import org.etudes.jforum.dao.GradeDAO;
import org.etudes.jforum.dao.PostDAO;
import org.etudes.jforum.dao.TopicDAO;
import org.etudes.jforum.dao.TopicMarkTimeDAO;
import org.etudes.jforum.dao.UserDAO;
import org.etudes.jforum.entities.Attachment;
import org.etudes.jforum.entities.Category;
import org.etudes.jforum.entities.Forum;
import org.etudes.jforum.entities.Grade;
import org.etudes.jforum.entities.Post;
import org.etudes.jforum.entities.SpecialAccess;
import org.etudes.jforum.entities.Topic;
import org.etudes.jforum.entities.TopicMarkTimeObj;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.entities.UserSession;
import org.etudes.jforum.exceptions.AttachmentException;
import org.etudes.jforum.exceptions.AttachmentSizeTooBigException;
import org.etudes.jforum.repository.ForumRepository;
import org.etudes.jforum.repository.PostRepository;
import org.etudes.jforum.repository.RankingRepository;
import org.etudes.jforum.repository.SmiliesRepository;
import org.etudes.jforum.repository.TopicRepository;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.preferences.TemplateKeys;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.etudes.jforum.view.forum.common.AttachmentCommon;
import org.etudes.jforum.view.forum.common.ForumCommon;
import org.etudes.jforum.view.forum.common.PostCommon;
import org.etudes.jforum.view.forum.common.TopicsCommon;
import org.etudes.jforum.view.forum.common.ViewCommon;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.cover.UserDirectoryService;


/**
 * @author Rafael Steil
 * 01/12/2006 Murthy - updated downloadAttach() method writing the response
 * 1/31/06 - Mallika - making changes for quotalimit
 * 1/25/06 - Mallika - adding nowDate variable
 * 08/08/06 - Murthy - updated for task topic
 * Mallika - 9/27/06 - eliminating views column
 * Mallika - 10/2/06 - Adding markAllTime from session to page
 * 11/09/06 - Murthy - commented the updateAttachment code in downloadAttach method
* Mallika - 11/9/06 - Adding updateTopicReadFlags method
 * Mallika - 11/13/06  - Changing compareDate to markAllDate
 * Mallika - 11/15/06 - Adding code for markTime
 * 11/20/06 - Murthy - code added to notify users whose preference under my profile, 
 *                 "Notify me when new topic is posted" is selected
 */
/**
 * @author Murthy Tanniru
 * @version $Id: PostAction.java 71044 2010-10-29 17:37:24Z murthy@etudes.org $
 */
public class PostAction extends Command {
	private static final Log logger = LogFactory.getLog(PostAction.class);

	public void list() throws Exception {
		PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();
		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();

		UserSession us = SessionFacade.getUserSession();
		int anonymousUser = SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID);

		int topicId = this.request.getIntParameter("topic_id");
		if(logger.isDebugEnabled()) logger.debug("listing posts for user " + us.getUsername() + " where topicId is " + topicId);
		Topic topic = tm.selectById(topicId);

		// The topic exists?
		if (topic.getId() == 0) {
			logger.error("topic " + topic + " has id=0");
			this.topicNotFound();
			return;
		}

		// Shall we proceed?
		if (!SessionFacade.isLogged()) {
			Forum f = ForumRepository.getForum(topic.getForumId());

			if (f == null || !ForumRepository.isForumAccessibleToUser(f)) {
				this.setTemplateName(ViewCommon.contextToLogin());
				return;
			}
		}
		else if (!TopicsCommon.isTopicAccessible(topic.getForumId())) {
			return;
		}

		int count = SakaiSystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		int start = ViewCommon.getStartPage();

		boolean canEdit = false;
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		if (isfacilitator)
			canEdit = true;
		
		Forum forum = ForumRepository.getForum(topic.getForumId());	
		boolean gradeForum = (forum.getGradeType() == Forum.GRADE_BY_FORUM);
		this.context.put("gradeForum", gradeForum);
		
		boolean gradeTopic = (forum.getGradeType() == Forum.GRADE_BY_TOPIC) && topic.isGradeTopic();
		this.context.put("gradeTopic", gradeTopic);
		
		Category category = DataAccessDriver.getInstance().newCategoryDAO().selectById(forum.getCategoryId());
		boolean gradeCategory = category.isGradeCategory();
		this.context.put("category", category);
		this.context.put("gradeCategory", gradeCategory);
		
		SpecialAccess specialAccess = getUserSpecialAccess(forum);
		boolean specialAccessUser = ((specialAccess != null)? true : false);
		
		if ((!specialAccessUser) && (!this.isForumOpen(forum) || !this.isCategoryOpen(category)))
		{
			this.context.put("errorMessage", I18n.getMessage("User.NotAuthorized"));
			this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED);
			return;
		}
		
		if (specialAccessUser)
		{
			boolean specialAccessUserAccess = false;
		
			Date currentTime = Calendar.getInstance().getTime();
			if ((specialAccess.getStartDate() != null) && (specialAccess.getStartDate().after(currentTime)))
			{
				this.context.put("errorMessage", I18n.getMessage("User.NotAuthorized"));
				this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED);
				return;
			}
			
			if (((specialAccess.getEndDate() != null) && (specialAccess.getEndDate().before(currentTime))) && specialAccess.isLockOnEndDate())
			{
				specialAccessUserAccess = false;
			}
			else
			{
				specialAccessUserAccess = true;
			}
			
			this.context.put("specialAccessUserAccess", specialAccessUserAccess);
		}
			
		List helperList = null;

		Map usersMap = new HashMap();
		List facilitatorsList = null;
		if (gradeForum || gradeTopic || gradeCategory) {
			facilitatorsList = new ArrayList();
			List participantsList = new ArrayList();
			helperList = PostCommon.topicPosts(pm, um, usersMap, facilitatorsList, participantsList, canEdit, us.getUserId(), topic.getId(), start, count);
		} else {
			helperList = PostCommon.topicPosts(pm, um, usersMap, canEdit, us.getUserId(), topic.getId(), start, count);
		}

		// Ugly assumption:
		// Is moderation pending for the topic?
		if (topic.isModerated() && helperList.size() == 0) {
			this.notModeratedYet();
			return;
		}
		
		if (gradeForum || gradeTopic || gradeCategory) {
			this.context.put("facilitators", facilitatorsList);
		}

		// Set the topic status as read
		tm.updateReadStatus(topic.getId(), us.getUserId(), true);

		this.context.put("attachmentsEnabled", true);
		this.context.put("canDownloadAttachments", true);
		this.context.put("am", new AttachmentCommon(this.request, topic.getForumId()));
		this.context.put("karmaVotes", DataAccessDriver.getInstance().newKarmaDAO().getUserVotes(topic.getId(), us.getUserId()));
		this.context.put("rssEnabled", SystemGlobals.getBoolValue(ConfigKeys.RSS_ENABLED));
		this.context.put("canRemove", isfacilitator);
		this.context.put("canEdit", canEdit);
		this.setTemplateName(TemplateKeys.POSTS_LIST);
		this.context.put("allCategories", ForumCommon.getAllCategoriesAndForums(false));
		this.context.put("topic", topic);
		this.context.put("rank", new RankingRepository());
		this.context.put("posts", helperList);
		this.context.put("forum", forum) ;
		this.context.put("users", usersMap);
		this.context.put("sju", new JForumUserUtil());
		this.context.put("topicId", new Integer(topicId));
		this.context.put("anonymousPosts", false);
		this.context.put("watching", tm.isUserSubscribed(topicId, SessionFacade.getUserSession().getUserId()));
		this.context.put("pageTitle", SystemGlobals.getValue(ConfigKeys.FORUM_NAME) + " - " + topic.getTitle());
		this.context.put("isAdmin", isfacilitator);
		this.context.put("readonly", this.isForumTypeReadonly(forum));
		this.context.put("replyOnly", this.isForumTypeReplyOnly(forum));
		this.context.put("isModerator", us.isModerator(topic.getForumId()));

		// Topic Status
		this.context.put("STATUS_LOCKED", new Integer(Topic.STATUS_LOCKED));
		this.context.put("STATUS_UNLOCKED", new Integer(Topic.STATUS_UNLOCKED));

		// Pagination
		int totalPosts = tm.getTotalPosts(topic.getId());
		
		GregorianCalendar gc = new GregorianCalendar();
		this.context.put("nowDate", gc.getTime());
		Date lastVisitDate = SessionFacade.getUserSession().getLastVisit();
	    this.context.put("lastVisit", lastVisitDate);
	    Date markAllDate = SessionFacade.getUserSession().getMarkAllTime();

        TopicMarkTimeDAO tmark = DataAccessDriver.getInstance().newTopicMarkTimeDAO();

		Date markTime = null;
		Date compareDate = null;
		TopicMarkTimeObj topicMarkTimeObj = null;
		
		try
		{
			topicMarkTimeObj = tmark.selectTopicMarkTime(topic.getId(), SessionFacade.getUserSession().getUserId());
			if (topicMarkTimeObj != null)
			{
				markTime = topicMarkTimeObj.getMarkTime();
			}
		}
		catch (Exception e)
		{
			logger.error(this.getClass().getName() +
					".updateTopicReadFlags() : " + e.getMessage(), e);
		}
	    if ((markAllDate == null) && (markTime == null))
		{
			//First date mentioned in Java API
			gc = new GregorianCalendar(1970,0,1);
			compareDate = gc.getTime();
		}
		else
		{
	      if (markAllDate == null)
	      {
			  compareDate = markTime;
	      }
	      if (markTime == null)
	      {
			  compareDate = markAllDate;
	      }
	      if ((markAllDate != null)&&(markTime != null))
	      {
			  if (markAllDate.getTime() > markTime.getTime())
			  {
				  compareDate = markAllDate;
		      }
		      else
		      {
				  compareDate = markTime;
		      }
	      }

	    }

	    this.context.put("compareDate", compareDate);
		 
		/* Mark topic as read when topic is visited but add only if the message is on the page where user is viewing.
		 	count is records per page to be displayed.*/
	    Double totalPages = Math.ceil((totalPosts * 1.0)/count);
		long totalPagesRounded = Math.round(totalPages);
		
		int currentPage = (start+count)/count;
		
	  	if ((totalPosts <= count) || (currentPage == totalPagesRounded))		//only one page posts && other last page posts. mark only on the last page as latest posts are on the last page
		{
			if (markTime != null)
			{
				if ((topic.getLastPostTimeInMillis().compareTo(markTime) > 0) || (!topicMarkTimeObj.isRead()))
				{
					tmark.updateMarkTime(topicId, us.getUserId(), new Date(System.currentTimeMillis()), true);
				}
			}
			else
			{
				tmark.addMarkTime(topicId, us.getUserId(), new Date(System.currentTimeMillis()), true);
			}
			
			if (us.getUserId() != anonymousUser) 
			{
				((Map) SessionFacade.getAttribute(ConfigKeys.TOPICS_TRACKING)).put(new Integer(topic.getId()),
						new Long(topic.getLastPostTimeInMillis().getTime()));
			}
		}
	  			 
		ViewCommon.contextToPagination(start, totalPosts, count);
	}

	/**
	 * review
	 * 
	 * @throws Exception
	 */
	public void review() throws Exception {
		PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();
		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();

		int userId = SessionFacade.getUserSession().getUserId();
		int topicId = this.request.getIntParameter("topic_id");
		Topic topic = tm.selectById(topicId);

		if (!TopicsCommon.isTopicAccessible(topic.getForumId())) {
			return;
		}

		int count = SakaiSystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		int start = ViewCommon.getStartPage();

		Map usersMap = new HashMap();
		List helperList = PostCommon.topicPosts(pm, um, usersMap, false, userId, topic.getId(), start, count);
		Collections.reverse(helperList);

		this.setTemplateName(SystemGlobals.getValue(ConfigKeys.TEMPLATE_DIR) + "/empty.htm");

		this.setTemplateName(TemplateKeys.POSTS_REVIEW);
		this.context.put("posts", helperList);
		this.context.put("users", usersMap);
	}

	/**
	 * topic not found message 
	 */
	private void topicNotFound() {
		this.setTemplateName(TemplateKeys.POSTS_TOPIC_NOT_FOUND);
		this.context.put("message", I18n.getMessage("PostShow.TopicNotFound"));
	}

	/**
	 * posts not found
	 */
	private void postNotFound() {
		this.setTemplateName(TemplateKeys.POSTS_POST_NOT_FOUND);
		this.context.put("message", I18n.getMessage("PostShow.PostNotFound"));
	}

	/**
	 * reply only
	 */
	private void replyOnly()
	{
		this.setTemplateName(TemplateKeys.POSTS_REPLY_ONLY);
		this.context.put("message", I18n.getMessage("PostShow.replyOnly"));
	}

	/**
	 * insert
	 * 
	 * @throws Exception
	 */
	public void insert() throws Exception
	{
		if(logger.isDebugEnabled()) logger.debug("starting insert()");
		int forumId = this.request.getIntParameter("forum_id");

		if (!TopicsCommon.isTopicAccessible(forumId)) {
			return;
		}

		Forum forum = ForumRepository.getForum(forumId);
		
		if (this.isForumTypeReadonly(forum))
		{
				return;
		}
		Category category = DataAccessDriver.getInstance().newCategoryDAO().selectById(forum.getCategoryId());
		
		SpecialAccess specialAccess = getUserSpecialAccess(forum);
		boolean specialAccessUser = ((specialAccess != null)? true : false);
				
		if ((!specialAccessUser) && (this.isForumLocked(forum) || !this.isForumOpen(forum) || this.isCategoryLocked(category) || !this.isCategoryOpen(category)))
		{
			this.context.put("errorMessage", I18n.getMessage("User.NotAuthorized"));
			this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED);
			return;
		}
		
		// special access user - verify access
		if (specialAccessUser)
		{
			boolean specialAccessUserAccess = false;
		
			Date currentTime = Calendar.getInstance().getTime();
			if ((specialAccess.getStartDate() != null) && (specialAccess.getStartDate().after(currentTime)))
			{
				this.context.put("errorMessage", I18n.getMessage("User.NotAuthorized"));
				this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED);
				return;
			}
			
			if (((specialAccess.getEndDate() != null) && (specialAccess.getEndDate().before(currentTime))) && specialAccess.isLockOnEndDate())
			{
				this.context.put("errorMessage", I18n.getMessage("User.NotAuthorized"));
				this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED);
				return;
			}
		}
		
		boolean facilitator = false;
		//facilitator can add all type of topics 
		if (JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser())
			facilitator = true;
		
		addGradeTypesToContext();
		
		if (this.request.getParameter("topic_id") != null) {
			int topicId = this.request.getIntParameter("topic_id");
			Topic t = DataAccessDriver.getInstance().newTopicDAO().selectRaw(topicId);

			if (!facilitator){
				if (t.getStatus() == Topic.STATUS_LOCKED) {
					this.topicLocked();
					return;
				}
			}

			this.context.put("topic", t);
			this.context.put("setType", false);
		}
		else {

			this.context.put("setType", true);
			if (this.isForumGradeByTopic(forum)) 
			{
				this.context.put("canAddEditGrade", true);
				Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
				if (site.getToolForCommonId(SakaiSystemGlobals.getValue(ConfigKeys.GRADEBOOK_TOOL_ID)) != null)
				{
					this.context.put("enableGrading", Boolean.TRUE);
				}
			}
		}

		int userId = SessionFacade.getUserSession().getUserId();

		this.setTemplateName(TemplateKeys.POSTS_INSERT);

		boolean attachmentsEnabled = true;
		this.context.put("attachmentsEnabled", attachmentsEnabled);

		if (attachmentsEnabled) {
			
			this.context.put("maxAttachmentsSize", new Long(SakaiSystemGlobals.getIntValue(ConfigKeys.ATTACHMENTS_QUOTA_LIMIT) * 1024 * 1024));
			
			this.context.put("maxAttachments", SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_MAX_POST));
		}

		boolean needCaptcha = SystemGlobals.getBoolValue(ConfigKeys.CAPTCHA_POSTS);

		if (needCaptcha) {
			SessionFacade.getUserSession().createNewCaptcha();
		}

		this.context.put("forum", forum);
		this.context.put("category", ForumRepository.getCategory(forum.getCategoryId()));
		this.context.put("action", "insertSave");
		this.context.put("start", this.request.getParameter("start"));
		this.context.put("isNewPost", true);
		this.context.put("needCaptcha", needCaptcha);
		this.context.put("htmlAllowed", true);
		this.context.put("canCreateStickyOrAnnouncementTopics", facilitator);
		this.context.put("canCreateTaskTopics", facilitator);
		
		User user = DataAccessDriver.getInstance().newUserDAO().selectById(userId);
		user.setSignature(PostCommon.processText(user.getSignature()));
		user.setSignature(PostCommon.processSmilies(user.getSignature(), SmiliesRepository.getSmilies()));

		if (this.request.getParameter("preview") != null) {
			user.setNotifyOnMessagesEnabled(this.request.getParameter("notify") != null);
		}

		this.context.put("user", user);
	}

	/**
	 * edit the post
	 * @throws Exception
	 */
	public void edit() throws Exception {
		this.edit(false, null);
	}

	/**
	 * edit the post
	 * @param preview
	 * @param p
	 * @throws Exception
	 */
	private void edit(boolean preview, Post p) throws Exception {
		int userId = SessionFacade.getUserSession().getUserId();
		int aId = SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID);
		boolean canAccess = false;

		if (!preview) {
			PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
			p = pm.selectById(this.request.getIntParameter("post_id"));

			// The post exist?
			if (p.getId() == 0) {
				this.postNotFound();
				return;
			}
		}

		boolean isModerator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		canAccess = (isModerator || p.getUserId() == userId);

		if ((userId != aId) && canAccess) {
			Topic topic = DataAccessDriver.getInstance().newTopicDAO().selectById(p.getTopicId());

			if (!TopicsCommon.isTopicAccessible(topic.getForumId())) {
				return;
			}

			if (topic.getStatus() == Topic.STATUS_LOCKED && !isModerator) {
				this.topicLocked();
				return;
			}
			
			Forum forum = ForumRepository.getForum(topic.getForumId());
			Category category = DataAccessDriver.getInstance().newCategoryDAO().selectById(forum.getCategoryId());
			
			SpecialAccess specialAccess = getUserSpecialAccess(forum);
			boolean specialAccessUser = ((specialAccess != null)? true : false);
			
			if ((!specialAccessUser) && (this.isForumLocked(forum) || !this.isForumOpen(forum) || this.isCategoryLocked(category) || !this.isCategoryOpen(category)))
			{
				this.context.put("errorMessage", I18n.getMessage("Forum.Locked"));
				this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED);
				return;
			}
			
			// special access user - verify access
			if (specialAccessUser)
			{
				boolean specialAccessUserAccess = false;
			
				Date currentTime = Calendar.getInstance().getTime();
				if ((specialAccess.getStartDate() != null) && (specialAccess.getStartDate().after(currentTime)))
				{
					this.context.put("errorMessage", I18n.getMessage("User.NotAuthorized"));
					this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED);
					return;
				}
				
				if (((specialAccess.getEndDate() != null) && (specialAccess.getEndDate().before(currentTime))) && specialAccess.isLockOnEndDate())
				{
					this.context.put("errorMessage", I18n.getMessage("User.NotAuthorized"));
					this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED);
					return;
				}
			}

			if (preview && this.request.getParameter("topic_type") != null) {
				topic.setType(this.request.getIntParameter("topic_type"));
			}

			if (p.hasAttachments()) {
				this.context.put("attachments",
						DataAccessDriver.getInstance().newAttachmentDAO().selectAttachments(p.getId()));
			}
			this.context.put("attachmentsEnabled", true);
			
			this.context.put("maxAttachmentsSize", new Long(SakaiSystemGlobals.getIntValue(ConfigKeys.ATTACHMENTS_QUOTA_LIMIT) * 1024 * 1024));
			this.context.put("maxAttachments", SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_MAX_POST));
			this.context.put("forum", forum);
			this.context.put("category", category);
			this.context.put("action", "editSave");
			this.context.put("post", p);
			this.context.put("setType", p.getId() == topic.getFirstPostId());
			this.context.put("editPost", true);
			
			addGradeTypesToContext();
			
			if ((p.getId() == topic.getFirstPostId()) && this.isForumGradeByTopic(forum)) {
				this.context.put("canAddEditGrade", true);
				
				if (topic.isGradeTopic()) {
					Grade exisGrade = DataAccessDriver.getInstance().newGradeDAO().selectByForumTopicId(topic.getForumId(), topic.getId());
					this.context.put("grade", exisGrade);
				}
				Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
				if (site.getToolForCommonId(SakaiSystemGlobals.getValue(ConfigKeys.GRADEBOOK_TOOL_ID)) != null)
				{
					this.context.put("enableGrading", Boolean.TRUE);
				}
			}
			
			this.context.put("topic", topic);
			this.setTemplateName(TemplateKeys.POSTS_EDIT);
			this.context.put("start", this.request.getParameter("start"));
			this.context.put("htmlAllowed", true);
			
			this.context.put("canCreateStickyOrAnnouncementTopics",
								JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser());
			
			this.context.put("canCreateTaskTopics",
					JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser());
		}
		else {
			this.setTemplateName(TemplateKeys.POSTS_EDIT_CANNOTEDIT);
			this.context.put("message", I18n.getMessage("CannotEditPost"));
		}

		User u = PostCommon.getUserForDisplay(userId);

		if (preview) {
			u.setNotifyOnMessagesEnabled(this.request.getParameter("notify") != null);

			if (u.getId() != p.getUserId()) {
				// Probably a moderator is editing the message
				this.context.put("previewUser", PostCommon.getUserForDisplay(p.getUserId()));
			}
		}

		this.context.put("user", u);
	}

	/**
	 * quote
	 * 
	 * @throws Exception
	 */
	public void quote() throws Exception {
		PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
		Post p = pm.selectById(this.request.getIntParameter("post_id"));
		
		boolean facilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();

		if (!this.anonymousPost(p.getForumId())) {
			if (!facilitator)
				return;
		}

		Topic t = DataAccessDriver.getInstance().newTopicDAO().selectRaw(p.getTopicId());

		if (!TopicsCommon.isTopicAccessible(t.getForumId())) {
			if (!facilitator)
				return;
		}

		if (t.getStatus() == Topic.STATUS_LOCKED) {
			if (!facilitator){
				this.topicLocked();
				return;
			}
		}

		if (p.getId() == 0) {
			this.postNotFound();
			return;
		}

		if (p.isModerationNeeded()) {
			if (!facilitator){
				this.notModeratedYet();
				return;
			}
		}
		
		Forum forum = ForumRepository.getForum(t.getForumId());
		Category category = DataAccessDriver.getInstance().newCategoryDAO().selectById(forum.getCategoryId());
		SpecialAccess specialAccess = getUserSpecialAccess(forum);
		boolean specialAccessUser = ((specialAccess != null)? true : false);
		
		if ((!specialAccessUser) && (this.isForumLocked(forum) || !this.isForumOpen(forum) || this.isCategoryLocked(category) || !this.isCategoryOpen(category)))
		{
			this.context.put("errorMessage", I18n.getMessage("Forum.Locked"));
			this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED);
			return;
		}
		
		// special access user - verify access
		if (specialAccessUser)
		{
			boolean specialAccessUserAccess = false;
		
			Date currentTime = Calendar.getInstance().getTime();
			if ((specialAccess.getStartDate() != null) && (specialAccess.getStartDate().after(currentTime)))
			{
				this.context.put("errorMessage", I18n.getMessage("User.NotAuthorized"));
				this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED);
				return;
			}
			
			if (((specialAccess.getEndDate() != null) && (specialAccess.getEndDate().before(currentTime))) && specialAccess.isLockOnEndDate())
			{
				this.context.put("errorMessage", I18n.getMessage("User.NotAuthorized"));
				this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED);
				return;
			}
		}

		this.context.put("forum", ForumRepository.getForum(p.getForumId()));
		this.context.put("action", "insertSave");
		this.context.put("post", p);

		UserDAO um = DataAccessDriver.getInstance().newUserDAO();
		User u = um.selectById(p.getUserId());

		Topic topic = DataAccessDriver.getInstance().newTopicDAO().selectById(p.getTopicId());
		int userId = SessionFacade.getUserSession().getUserId();
		this.context.put("attachmentsEnabled", true);
		this.context.put("maxAttachmentsSize", new Long(SakaiSystemGlobals.getIntValue(ConfigKeys.ATTACHMENTS_QUOTA_LIMIT) * 1024 * 1024));
		this.context.put("maxAttachments", SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_MAX_POST));
		this.context.put("isNewPost", true);
		this.context.put("topic", topic);
		this.context.put("quote", "true");
		this.context.put("quoteUser", u);
		this.setTemplateName(TemplateKeys.POSTS_QUOTE);
		this.context.put("setType", false);
		this.context.put("htmlAllowed", true);
		this.context.put("start", this.request.getParameter("start"));
		this.context.put("user", DataAccessDriver.getInstance().newUserDAO().selectById(userId));
	}
	
	/**
	 * save the edited post
	 * @throws Exception
	 */
	public void editSave() throws Exception {
		PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();

		Post p = pm.selectById(this.request.getIntParameter("post_id"));
		p = PostCommon.fillPostFromRequest(p, true);

		// The user wants to preview the message before posting it?
		if (this.request.getParameter("preview") != null) {
			this.context.put("preview", true);

			Post postPreview = new Post(p);
			this.context.put("postPreview", PostCommon.preparePostForDisplay(postPreview));

			this.edit(true, p);
		}
		else {
			AttachmentCommon attachments = new AttachmentCommon(this.request, p.getForumId());

			try {
				attachments.preProcess();
			}
			catch (AttachmentException e) {
				JForum.enableCancelCommit();
				p.setText(this.request.getParameter("message"));
				this.context.put("errorMessage", e.getMessage());
				this.context.put("post", p);
				this.edit(false, p);
				return;
			}

			Topic t = tm.selectById(p.getTopicId());

			if (!TopicsCommon.isTopicAccessible(t.getForumId())) {
				return;
			}
			
			Forum forum = ForumRepository.getForum(t.getForumId());
			Category category = DataAccessDriver.getInstance().newCategoryDAO().selectById(forum.getCategoryId());
			SpecialAccess specialAccess = getUserSpecialAccess(forum);
			boolean specialAccessUser = ((specialAccess != null)? true : false);
			
			if ((!specialAccessUser) && (this.isForumLocked(forum) || !this.isForumOpen(forum) || this.isCategoryLocked(category) || !this.isCategoryOpen(category)))
			{
				this.context.put("errorMessage", I18n.getMessage("Forum.Locked"));
				this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED);
				return;
			}
			
			// special access user - verify access
			if (specialAccessUser)
			{
				boolean specialAccessUserAccess = false;
			
				Date currentTime = Calendar.getInstance().getTime();
				if ((specialAccess.getStartDate() != null) && (specialAccess.getStartDate().after(currentTime)))
				{
					this.context.put("errorMessage", I18n.getMessage("User.NotAuthorized"));
					this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED);
					return;
				}
				
				if (((specialAccess.getEndDate() != null) && (specialAccess.getEndDate().before(currentTime))) && specialAccess.isLockOnEndDate())
				{
					this.context.put("errorMessage", I18n.getMessage("User.NotAuthorized"));
					this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED);
					return;
				}
			}
			
			boolean facilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();

			if (t.getStatus() == Topic.STATUS_LOCKED
					&& !facilitator) {
				this.topicLocked();
				return;
			}

			pm.update(p);
			
			// Attachments
			attachments.editAttachments(p.getId(), p.getForumId());
			attachments.insertAttachments(p.getId());

			// Updates the topic title
			if (t.getFirstPostId() == p.getId()) {
				t.setTitle(p.getSubject());

				if (facilitator) {
					t.setType(this.request.getIntParameter("topic_type"));
				}
				
				//topic - mark for export
				if (facilitator) {
					if (this.request.getParameter("topic_export") != null) {
						t.setExportTopic(true);
					} else {
						t.setExportTopic(false);
					}
				}
				
				//grade topic
				if (this.request.getParameter("grade_topic") != null) {
					if (this.request.getIntParameter("grade_topic") == Topic.GRADE_NO) {
						//check if there are any existing evaluations if the grading type before was grade topic
						if (t.isGradeTopic()) {
							EvaluationDAO evaldao = DataAccessDriver.getInstance().newEvaluationDAO();
							int evalCount = evaldao.selectForumTopicEvaluationsCountById(t.getId());
							
							if (evalCount > 0) {
								JForum.enableCancelCommit();
								this.context.put("errorMessage", I18n.getMessage("PostShow.CannotEditTopic"));
								this.context.put("post", p);
								this.edit(false, p);
								return;
							}
						}
						t.setGradeTopic(false);
					} else if (this.request.getIntParameter("grade_topic") == Topic.GRADE_YES)
						t.setGradeTopic(true);
				}

				tm.update(t);
				
				//update grading if grade topic is yes
				if (t.isGradeTopic()) {
					
					Grade exisGrade = DataAccessDriver.getInstance().newGradeDAO().selectByForumTopicId(t.getForumId(), t.getId());
					
					//add grading if not existing
					if (exisGrade == null) {
						Grade grade = new Grade();
						
						grade.setContext(ToolManager.getCurrentPlacement().getContext());
						grade.setForumId(t.getForumId());
						grade.setTopicId(t.getId());
						try {
							Float points = Float.parseFloat(this.request.getParameter("point_value"));
							
							if (points.floatValue() < 0) points = Float.valueOf(0.0f);
							if (points.floatValue() > 1000) points = Float.valueOf(1000.0f);
							points = Float.valueOf(((float) Math.round(points.floatValue() * 100.0f)) / 100.0f);
							
							grade.setPoints(points);
						} catch (NumberFormatException ne) {
							grade.setPoints(0f);
						}			
						grade.setType(Forum.GRADE_BY_TOPIC);
						
						int gradeId = DataAccessDriver.getInstance().newGradeDAO().addNew(grade);
						grade.setId(gradeId);
						
						/*if add to gradebook is checked add the topic grade to gradebook if there no entry
						  in the gradebook if the same title*/
						String sendToGradebook = this.request.getParameter("send_to_grade_book");
						boolean addToGradeBook = false;
						if ((sendToGradebook != null) && (Integer.parseInt(sendToGradebook) == 1))
						{
							addToGradeBook = true;
						}
						
						grade.setAddToGradeBook(addToGradeBook);		
						
						//if add to grade book is true then add the grade to grade book
						if (addToGradeBook)
						{
							Forum f = DataAccessDriver.getInstance().newForumDAO().selectById(t.getForumId());
							if (f.getAccessType() == Forum.ACCESS_DENY)
							{
								// remove any existing entry in the grade book
								removeEntryFromGradeBook(grade);
								addToGradeBook = false;
							}
							else
							{
								if (f.getStartDate() != null)
								{
									Calendar calendar = Calendar.getInstance();
									
									Date startDate = f.getStartDate();
									
									Date nowDate = calendar.getTime();
									
									if (nowDate.before(startDate))
									{
										addToGradeBook = false;
										this.context.put("errorMessage", I18n.getMessage("Grade.AddEditForumNotVisibleGradesToGradebook"));
									}
									else
									{
										addToGradeBook = updateGradebook(t, grade);
									}
	
								}
								else
								{
									addToGradeBook = updateGradebook(t, grade);
								}
							}
						}
						grade.setAddToGradeBook(addToGradeBook);
						DataAccessDriver.getInstance().newGradeDAO().updateAddToGradeBookStatus(gradeId, addToGradeBook);
					} else {
						//update existing grading
						Grade grade = new Grade();
						
						grade.setContext(ToolManager.getCurrentPlacement().getContext());
						grade.setForumId(t.getForumId());
						grade.setTopicId(t.getId());
						try {
							Float points = Float.parseFloat(this.request.getParameter("point_value"));
							
							if (points.floatValue() < 0) points = Float.valueOf(0.0f);
							if (points.floatValue() > 1000) points = Float.valueOf(10000.0f);
							points = Float.valueOf(((float) Math.round(points.floatValue() * 100.0f)) / 100.0f);
							
							grade.setPoints(points);
						} catch (NumberFormatException ne) {
							grade.setPoints(0f);
						}			
						grade.setType(Forum.GRADE_BY_TOPIC);
						grade.setId(exisGrade.getId());
						
						DataAccessDriver.getInstance().newGradeDAO().updateTopicGrade(grade);
						
						String sendToGradebook = this.request.getParameter("send_to_grade_book");
						boolean addToGradeBook = false;
						if ((sendToGradebook != null) && (Integer.parseInt(sendToGradebook) == 1))
						{
							addToGradeBook = true;
						}
						grade.setAddToGradeBook(addToGradeBook);
						
						// update any entry in the grade book for points and any name changes.
						if (addToGradeBook)
						{
							Forum f = DataAccessDriver.getInstance().newForumDAO().selectById(t.getForumId());
							if (f.getAccessType() == Forum.ACCESS_DENY)
							{
								// remove any existing entry in the grade book
								removeEntryFromGradeBook(exisGrade);
								addToGradeBook = false;
							}
							else
							{
								if (f.getStartDate() != null)
								{
									Calendar calendar = Calendar.getInstance();
									
									Date startDate = f.getStartDate();
									
									Date nowDate = calendar.getTime();
									
									if (nowDate.before(startDate))
									{
										addToGradeBook = false;
										this.context.put("errorMessage", I18n.getMessage("Grade.AddEditForumNotVisibleGradesToGradebook"));
									}
									else
									{
										addToGradeBook = updateGradebook(t, grade);
									}
	
								}
								else
								{
									addToGradeBook = updateGradebook(t, grade);
								}
							}
						} 
						else
						{
							// remove any existing entry in the grade book
							removeEntryFromGradeBook(exisGrade);
						}
						grade.setAddToGradeBook(addToGradeBook);	
						DataAccessDriver.getInstance().newGradeDAO().updateAddToGradeBookStatus(grade.getId(), addToGradeBook);
					}
				} 
				else 
				{
					//remove existing grade and entry in the gradebook
					Grade exisGrade = DataAccessDriver.getInstance().newGradeDAO().selectByForumTopicId(t.getForumId(), t.getId());
					if (exisGrade != null && exisGrade.isAddToGradeBook()) {
						
						removeEntryFromGradeBook(exisGrade);
					}
				}


				ForumRepository.reloadForum(t.getForumId());
				TopicRepository.clearCache(t.getForumId());
			}

			if (this.request.getParameter("notify") == null) {
				tm.removeSubscription(p.getTopicId(), SessionFacade.getUserSession().getUserId());
			}

			String path = this.request.getContextPath() + "/posts/list/";
			String start = this.request.getParameter("start");
			if (start != null && !start.equals("0")) {
				path += start + "/";
			}

			path += p.getTopicId() + SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) + "#" + p.getId();
			JForum.setRedirect(path);

			if (SystemGlobals.getBoolValue(ConfigKeys.POSTS_CACHE_ENABLED)) {
				PostRepository.update(p.getTopicId(), PostCommon.preparePostForDisplay(p));
			}
		}
	}

	/**
	 * waiting moderation
	 */
	public void waitingModeration()
	{
		this.setTemplateName(TemplateKeys.POSTS_WAITING);

		int topicId = this.request.getIntParameter("topic_id");
		String path = this.request.getContextPath();

		if (topicId == 0) {
			path += "/forums/show/" + this.request.getParameter("forum_id");
		}
		else {
			path += "/posts/list/" + topicId;
		}

		this.context.put("message", I18n.getMessage("PostShow.waitingModeration",
				new String[] { path + SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) }));
	}

	/**
	 * not moderated yet
	 */
	private void notModeratedYet()
	{
		this.setTemplateName(TemplateKeys.POSTS_NOT_MODERATED);
		this.context.put("message", I18n.getMessage("PostShow.notModeratedYet"));
	}

	/**
	 * save the post
	 * 
	 * @throws Exception
	 */
	public void insertSave() throws Exception
	{
		if(logger.isDebugEnabled()) logger.debug("starting insertSave()");

		int forumId = this.request.getIntParameter("forum_id");
		boolean firstPost = false;

		if (!this.anonymousPost(forumId)) {
			SessionFacade.setAttribute(ConfigKeys.REQUEST_DUMP, this.request.dumpRequest());
			return;
		}

		Topic t = new Topic();
		t.setId(-1);
		t.setForumId(forumId);

		Forum forum = ForumRepository.getForum(forumId);
		
		if (!TopicsCommon.isTopicAccessible(t.getForumId())
				|| this.isForumTypeReadonly(forum)) {
			return;
		}
		Category category = DataAccessDriver.getInstance().newCategoryDAO().selectById(forum.getCategoryId());
		
		SpecialAccess specialAccess = getUserSpecialAccess(forum);
		
		boolean specialAccessUser = ((specialAccess != null)? true : false);
		
		if ((!specialAccessUser) && (this.isForumLocked(forum) || !this.isForumOpen(forum) || this.isCategoryLocked(category) || !this.isCategoryOpen(category)))
		{
			this.context.put("errorMessage", I18n.getMessage("Forum.Locked"));
			this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED);
			return;
		}
		
		// special access user - verify access
		if (specialAccessUser)
		{
			boolean specialAccessUserAccess = false;
		
			Date currentTime = Calendar.getInstance().getTime();
			if ((specialAccess.getStartDate() != null) && (specialAccess.getStartDate().after(currentTime)))
			{
				this.context.put("errorMessage", I18n.getMessage("User.NotAuthorized"));
				this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED);
				return;
			}
			
			if (((specialAccess.getEndDate() != null) && (specialAccess.getEndDate().before(currentTime))) && specialAccess.isLockOnEndDate())
			{
				this.context.put("errorMessage", I18n.getMessage("User.NotAuthorized"));
				this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED);
				return;
			}
		}
		
		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
		PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
		ForumDAO fm = DataAccessDriver.getInstance().newForumDAO();

		if (this.request.getParameter("topic_id") != null) {
			t = tm.selectById(this.request.getIntParameter("topic_id"));

			if (!TopicsCommon.isTopicAccessible(t.getForumId())) {
				return;
			}
			boolean facilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
			// Cannot insert new messages on locked topics
			if (t.getStatus() == Topic.STATUS_LOCKED && !facilitator) {
				this.topicLocked();
				return;
			}
		}
		else {

			if (this.isForumTypeReplyOnly(forum)) {
				this.replyOnly();
				return;
			}
		}

		if (this.request.getParameter("topic_type") != null) {
			t.setType(this.request.getIntParameter("topic_type"));
		}

		UserSession us = SessionFacade.getUserSession();
		User u = new User();
		u.setId(us.getUserId());
		u.setUsername(us.getUsername());
		
		User user = DataAccessDriver.getInstance().newUserDAO().selectById(us.getUserId());
		
		u.setFirstName(user.getFirstName());
		u.setLastName(user.getLastName());

		t.setPostedBy(u);

		// Set the Post
		Post p = PostCommon.fillPostFromRequest();

		// Check the elapsed time since the last post from the user
		int delay = SystemGlobals.getIntValue(ConfigKeys.POSTS_NEW_DELAY);

		if (delay > 0) {
			Long lastPostTime = (Long)SessionFacade.getAttribute(ConfigKeys.LAST_POST_TIME);

			if (lastPostTime != null) {
				if (System.currentTimeMillis() < (lastPostTime.longValue() + delay)) {
					this.context.put("post", p);
					this.context.put("start", this.request.getParameter("start"));
					this.context.put("error", I18n.getMessage("PostForm.tooSoon"));
					this.insert();
					return;
				}
			}
		}

		p.setForumId(this.request.getIntParameter("forum_id"));

		if (p.getSubject() == null || p.getSubject().trim() == "") {
			p.setSubject(t.getTitle());
		}

		boolean needCaptcha = SystemGlobals.getBoolValue(ConfigKeys.CAPTCHA_POSTS);

		if (needCaptcha) {
			if (!us.validateCaptchaResponse(this.request.getParameter("captcha_anwser"))) {
				this.context.put("post", p);
				this.context.put("start", this.request.getParameter("start"));
				this.context.put("error", I18n.getMessage("CaptchaResponseFails"));

				this.insert();

				return;
			}
		}

		boolean preview = (this.request.getParameter("preview") != null);
		boolean moderate = false;
		if (!preview) {
			AttachmentCommon attachments = new AttachmentCommon(this.request, forumId);

			try {
				attachments.preProcess();
			}
			catch (AttachmentSizeTooBigException e) {
				JForum.enableCancelCommit();
				p.setText(this.request.getParameter("message"));
				p.setId(0);
				this.context.put("errorMessage", e.getMessage());
				this.context.put("post", p);
				this.insert();
				return;
			}

			// If topic_id is -1, then is the first post
			if (t.getId() == -1) {
				t.setTime(new Date());
				t.setTitle(this.request.getParameter("subject"));
				t.setModerated(ForumRepository.getForum(forumId).isModerated());
				//grade topic
				if (this.request.getParameter("grade_topic") != null) {
					if (this.request.getIntParameter("grade_topic") == Topic.GRADE_NO)
						t.setGradeTopic(false);
					else if (this.request.getIntParameter("grade_topic") == Topic.GRADE_YES)
						t.setGradeTopic(true);
				}
				
				//topic - mark for export
				if (this.request.getParameter("topic_export") != null) {
					t.setExportTopic(true);
				}

				t.setId(tm.addNew(t));
				
				//create grading if grade topic is yes
				if (t.isGradeTopic()) {
					Grade grade = new Grade();
					
					grade.setContext(ToolManager.getCurrentPlacement().getContext());
					grade.setForumId(forumId);
					grade.setTopicId(t.getId());
					try {
						Float points = Float.parseFloat(this.request.getParameter("point_value"));
						
						if (points.floatValue() < 0) points = Float.valueOf(0.0f);
						if (points.floatValue() > 1000) points = Float.valueOf(1000.0f);
						points = Float.valueOf(((float) Math.round(points.floatValue() * 100.0f)) / 100.0f);
						grade.setPoints(points);
					} catch (NumberFormatException ne) {
						grade.setPoints(0f);
					}			
					grade.setType(Forum.GRADE_BY_TOPIC);
					
					/*if add to gradebook is checked add the topic grade to gradebook if there no entry
					  in the gradebook if the same title*/
					String sendToGradebook = this.request.getParameter("send_to_grade_book");
					boolean addToGradeBook = false;
					if ((sendToGradebook != null) && (Integer.parseInt(sendToGradebook) == 1))
					{
						addToGradeBook = true;
					}
					
					int gradeId = DataAccessDriver.getInstance().newGradeDAO().addNew(grade);
					grade.setId(gradeId);
					
					grade.setAddToGradeBook(addToGradeBook);
					
					//if add to grade book is true then add the grade to grade book
					if (addToGradeBook)
					{
						Forum f = fm.selectById(t.getForumId());
						if (f.getAccessType() == Forum.ACCESS_DENY)
						{
							// remove any existing entry in the grade book
							removeEntryFromGradeBook(grade);
							addToGradeBook = false;
						}
						else
						{
							if (f.getStartDate() != null)
							{
								Calendar calendar = Calendar.getInstance();
								
								Date startDate = f.getStartDate();
								
								Date nowDate = calendar.getTime();
								
								if (nowDate.before(startDate))
								{
									addToGradeBook = false;
									this.context.put("errorMessage", I18n.getMessage("Grade.AddEditForumNotVisibleGradesToGradebook"));
								}
								else
								{
									addToGradeBook = updateGradebook(t, grade);
								}
	
							}
							else
							{
								addToGradeBook = updateGradebook(t, grade);
							}
						}
						
					}
					grade.setAddToGradeBook(addToGradeBook);
					DataAccessDriver.getInstance().newGradeDAO().updateAddToGradeBookStatus(gradeId, addToGradeBook);
				}
				
				firstPost = true;
			}

			// Moderators and admins don't need to have their messages moderated
			moderate = (t.isModerated()
					&& JForumUserUtil.isJForumParticipant(UserDirectoryService.getCurrentUser().getId()));
			

			// Topic watch
			if (this.request.getParameter("notify") != null) {
				this.watch(tm, t.getId(), u.getId());
			}

			p.setTopicId(t.getId());

			// Save the remaining stuff
			p.setModerate(moderate);
			int postId = pm.addNew(p);

			if (this.request.getParameter("topic_id") == null) {
				t.setFirstPostId(postId);
			}

			tm.update(t);

			attachments.insertAttachments(postId);

			if (!moderate) {
				DataAccessDriver.getInstance().newUserDAO().incrementPosts(p.getUserId());
				TopicsCommon.updateBoardStatus(t, postId, firstPost, tm, fm);
				Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());

				boolean notifyTopicToUsers = true;
				
				if (forum.getAccessType() == Forum.ACCESS_DENY)
					notifyTopicToUsers = false;
				
				// check for forum start and end dates
				if (notifyTopicToUsers)
				{
					GregorianCalendar gc = new GregorianCalendar();
					Date nowDate = gc.getTime();
					
					if (forum.getStartDate() != null){
						if (nowDate.getTime() < forum.getStartDate().getTime())
							notifyTopicToUsers = false;
					}
					
					if (forum.getEndDate() != null && notifyTopicToUsers){
						if (nowDate.getTime() > forum.getEndDate().getTime())
							notifyTopicToUsers = false;
					}
				}
				
				//get attachments
				List postAttachments = DataAccessDriver.getInstance().newAttachmentDAO().selectAttachments(p.getId());
				
				if (site.isPublished() && notifyTopicToUsers) {
					if (firstPost) {
						TopicsCommon.notifyNewTopicToUsers(t, p, postAttachments);
					} else {
						
						User postUser = DataAccessDriver.getInstance().newUserDAO().selectById(us.getUserId());
						
						p.setFirstName(postUser.getFirstName());
						p.setLastName(postUser.getLastName());
						 
						TopicsCommon.notifyUsers(t, p, tm, postAttachments);
					}
				}
				
				String path = this.request.getContextPath() + "/posts/list/";
				int start = ViewCommon.getStartPage();

				path += this.startPage(t, start) + "/";
				path += t.getId() + SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) + "#" + postId;

				JForum.setRedirect(path);

				int anonymousUser = SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID);
				if (u.getId() != anonymousUser) {
					((Map) SessionFacade.getAttribute(ConfigKeys.TOPICS_TRACKING)).put(new Integer(t.getId()),
							new Long(p.getTime().getTime()));
				}

				if (SystemGlobals.getBoolValue(ConfigKeys.POSTS_CACHE_ENABLED)) {
					// SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
					SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
					p.setFormatedTime(df.format(p.getTime()));

					PostRepository.append(p.getTopicId(), PostCommon.preparePostForDisplay(p));
				}
			}
			else {
				JForum.setRedirect(this.request.getContextPath() + "/posts/waitingModeration/" + (firstPost ? 0 : t.getId())
						+ "/" + t.getForumId()
						+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
			}

			if (delay > 0) {
				SessionFacade.setAttribute(ConfigKeys.LAST_POST_TIME, new Long(System.currentTimeMillis()));
			}
		}
		else {
			this.context.put("preview", true);
			this.context.put("post", p);
			this.context.put("start", this.request.getParameter("start"));

			Post postPreview = new Post(p);
			this.context.put("postPreview", PostCommon.preparePostForDisplay(postPreview));

			this.insert();
		}
	}

	/**
	 * start page
	 * 
	 * @param t
	 * @param currentStart
	 * @return
	 */
	private int startPage(Topic t, int currentStart) {
		int postsPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);

		int newStart = (t.getTotalReplies() + 1) / postsPerPage * postsPerPage;
		if (newStart > currentStart) {
			return newStart;
		}

		return currentStart;
	}

	/**
	 * delete the post
	 * 
	 * @throws Exception
	 */
	public void delete() throws Exception {

		if (!(JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) 
				|| SecurityService.isSuperUser())){
			this.setTemplateName(TemplateKeys.POSTS_CANNOT_DELETE);
			this.context.put("message", I18n.getMessage("CannotRemovePost"));

			return;
		}

		// Post
		PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
		Post p = pm.selectById(this.request.getIntParameter("post_id"));

		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
		Topic t = tm.selectById(p.getTopicId());

		if (!TopicsCommon.isTopicAccessible(t.getForumId())) {
			return;
		}

		if (p.getId() == 0) {
			this.postNotFound();
			return;
		}
		
		/*
		 * If the post is first post check the grade type of the topic. If the topic's grade type
		 * is yes check for existing evaluations. If the topic's grade has evaluations, the topic cannot
		 * be deleted
		 * If the forum has grades the top level topics cannot be deleted
		 */
		ForumDAO fm = DataAccessDriver.getInstance().newForumDAO();
		Forum forum = fm.selectById(t.getForumId());
		if ((t.getFirstPostId() == p.getId()) && (forum.getGradeType() == Forum.GRADE_BY_TOPIC) && (t.isGradeTopic())) {
			EvaluationDAO evaldao = DataAccessDriver.getInstance().newEvaluationDAO();
			int evalCount = evaldao.selectForumTopicEvaluationsCountById(t.getId());
			
			if (evalCount > 0) {
				this.context.put("errorMessage", I18n.getMessage("PostShow.CannotDeleteTopic", new Object[]{t.getTitle()}));
				this.request.addParameter("topic_id", String.valueOf(t.getId()));
				this.list();
				return;
			}
			
			//remove entry from gradebook
			GradeDAO gradedao = DataAccessDriver.getInstance().newGradeDAO();
			Grade grade = gradedao.selectByForumTopicId(t.getForumId(), t.getId());
			if (grade != null && grade.isAddToGradeBook()) {
				
				removeEntryFromGradeBook(grade);
			}
			
			
		} else if ((t.getFirstPostId() == p.getId()) && (forum.getGradeType() == Forum.GRADE_BY_FORUM)) {
			EvaluationDAO evaldao = DataAccessDriver.getInstance().newEvaluationDAO();
			int evalCount = evaldao.selectForumEvaluationsCount(forum.getId());
			
			if (evalCount > 0) {
				this.context.put("errorMessage", I18n.getMessage("PostShow.CannotDeleteForumTopic", new Object[]{t.getTitle()}));
				this.request.addParameter("topic_id", String.valueOf(t.getId()));
				this.list();
				return;
			}
		}
		
		pm.delete(p);
		DataAccessDriver.getInstance().newUserDAO().decrementPosts(p.getUserId());

		// Attachments
		new AttachmentCommon(this.request, p.getForumId()).deleteAttachments(p.getId(), p.getForumId());

		// Topic
		tm.decrementTotalReplies(p.getTopicId());

		int maxPostId = tm.getMaxPostId(p.getTopicId());
		if (maxPostId > -1) {
			tm.setLastPostId(p.getTopicId(), maxPostId);
		}

		int minPostId = tm.getMinPostId(p.getTopicId());
		if (minPostId > -1) {
		  tm.setFirstPostId(p.getTopicId(), minPostId);
		}

		// Forum

		maxPostId = fm.getMaxPostId(p.getForumId());
		if (maxPostId > -1) {
			fm.setLastPost(p.getForumId(), maxPostId);
		}

		// It was the last remaining post in the topic?
		int totalPosts = tm.getTotalPosts(p.getTopicId());
		if (totalPosts > 0) {
			String page = this.request.getParameter("start");
			String returnPath = this.request.getContextPath() + "/posts/list/";

			if (page != null && !page.equals("") && !page.equals("0")) {

				int postsPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
				int newPage = Integer.parseInt(page);

				if (totalPosts % postsPerPage == 0) {
					newPage -= postsPerPage;
				}

				returnPath += newPage + "/";
			}

			JForum.setRedirect(returnPath + p.getTopicId() + SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
		}
		else {
			// Ok, all posts were removed. Time to say goodbye
			TopicsCommon.deleteTopic(p.getTopicId(), p.getForumId(), false);

			JForum.setRedirect(this.request.getContextPath() + "/forums/show/" + p.getForumId()
					+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
		}

		ForumRepository.reloadForum(p.getForumId());
		TopicRepository.clearCache(p.getForumId());
		PostRepository.clearCache(p.getTopicId());
	}

	/**
	 * update topic read flags
	 * @throws Exception
	 */
	public void updateTopicReadFlags() throws Exception
	{
		TopicMarkTimeDAO tm = DataAccessDriver.getInstance().newTopicMarkTimeDAO();
		int topicId = this.request.getIntParameter("topic_id");
		UserSession us = SessionFacade.getUserSession();
		
		Topic topic = DataAccessDriver.getInstance().newTopicDAO().selectById(topicId);

		Date markTime = null;
		try
		{
		  markTime = tm.selectMarkTime(topicId, us.getUserId());

		}
		catch (Exception e)
		{
			logger.error(this.getClass().getName() +
					".updateTopicReadFlags() : " + e.getMessage(), e);
		}
		if (markTime == null)
		{
		  tm.addMarkTime(topicId, us.getUserId(), new Date(System.currentTimeMillis()), false);
		}
		else
		{
		  tm.updateMarkTime(topicId, us.getUserId(), new Date(System.currentTimeMillis()), false);
		}

		JForum.setRedirect(this.request.getContextPath() + "/forums/show/"+ topic.getForumId() + SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
	}

	/**
	 * watch the topic
	 * @param tm
	 * @param topicId
	 * @param userId
	 * @throws Exception
	 */
	private void watch(TopicDAO tm, int topicId, int userId) throws Exception {
		if (!tm.isUserSubscribed(topicId, userId)) {
			tm.subscribeUser(topicId, userId);
		}
	}

	/**
	 * wat the topic
	 * @throws Exception
	 */
	public void watch() throws Exception {
		int topicId = this.request.getIntParameter("topic_id");
		int userId = SessionFacade.getUserSession().getUserId();

		this.watch(DataAccessDriver.getInstance().newTopicDAO(), topicId, userId);
		this.list();
	}

	/**
	 * unwatch the topic
	 * 
	 * @throws Exception
	 */
	public void unwatch() throws Exception {
		if (SessionFacade.isLogged()) {
			int topicId = this.request.getIntParameter("topic_id");
			int userId = SessionFacade.getUserSession().getUserId();
			String start = this.request.getParameter("start");

			DataAccessDriver.getInstance().newTopicDAO().removeSubscription(topicId, userId);

			String returnPath = this.request.getContextPath() + "/posts/list/";
			if (start != null && !start.equals("")) {
				returnPath += start + "/";
			}

			returnPath += topicId + SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION);

			this.setTemplateName(TemplateKeys.POSTS_UNWATCH);
			this.context.put("message", I18n.getMessage("ForumBase.unwatched", new String[] { returnPath }));
		}
		else {
			this.setTemplateName(ViewCommon.contextToLogin());
		}
	}

	/**
	 * download attachment
	 * 
	 * @throws Exception
	 */
	public void downloadAttach() throws Exception
	{

		int id = this.request.getIntParameter("attach_id");

		AttachmentDAO am = DataAccessDriver.getInstance().newAttachmentDAO();
		Attachment a = am.selectAttachmentById(id);

		String filename = SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR)
			+ "/"
			+ a.getInfo().getPhysicalFilename();

		if (!new File(filename).exists()) {
			this.setTemplateName(TemplateKeys.POSTS_ATTACH_NOTFOUND);
			this.context.put("message", I18n.getMessage("Attachments.notFound"));
			return;
		}

		FileInputStream fis = new FileInputStream(filename);
		OutputStream os = response.getOutputStream();

		if(am.isPhysicalDownloadMode(a.getInfo().getExtension().getExtensionGroupId()))
		{
			this.response.setContentType("application/octet-stream");
		}
		else
		{
			this.response.setContentType(a.getInfo().getMimetype());
		}

		this.response.setHeader("Content-Disposition", "attachment; filename=\"" + a.getInfo().getRealFilename() + "\";");
		this.response.setContentLength((int)a.getInfo().getFilesize());

		byte[] b = new byte[4096];
		int c = 0;
		while ((c = fis.read(b)) != -1) {
			os.write(b, 0, c);
		}
		os.flush();

		fis.close();
		os.close();

		JForum.enableBinaryContent(true);
	}

	/**
	 * 
	 */
	private void topicLocked() {
		this.setTemplateName(TemplateKeys.POSTS_TOPIC_LOCKED);
		this.context.put("message", I18n.getMessage("PostShow.topicLocked"));
	}

	/**
	 * 
	 */
	public void listSmilies()
	{
		this.setTemplateName(SystemGlobals.getValue(ConfigKeys.TEMPLATE_DIR) + "/empty.htm");
		this.setTemplateName(TemplateKeys.POSTS_LIST_SMILIES);
		this.context.put("smilies", SmiliesRepository.getSmilies());
	}


	/**
	 * 
	 * @param forumId
	 * @return
	 * @throws Exception
	 */
	private boolean anonymousPost(int forumId) throws Exception {
		// Check if anonymous posts are allowed
		
		if (!SessionFacade.isLogged()) {
			this.setTemplateName(ViewCommon.contextToLogin());

			return false;
		}

		return true;
	}
	
	/**
	 * check to see if forum is read only
	 * 
	 * @param forum		forum
	 * @return
	 * @throws Exception
	 */
	private boolean isForumTypeReadonly(Forum forum) throws Exception {
		if (JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser())
			return false;
		
		if (forum.getType() == Forum.TYPE_READ_ONLY){
			return true;
		}

		return false;
	}
	
	/**
	 * Check to see of forum type is reply only
	 * 
	 * @param forum
	 * @return
	 * @throws Exception
	 */
	private boolean isForumTypeReplyOnly(Forum forum) throws Exception {
		
		if (JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser())
			return false;
				
		if (forum.getType() == Forum.TYPE_REPLY_ONLY) {
			return true;
		}

		return false;
	}
	
	/**
	 * check to see if forum is grade by topid
	 * @param forum
	 * @return
	 * @throws Exception
	 */
	private boolean isForumGradeByTopic(Forum forum) throws Exception
	{
		if (JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) 
				|| SecurityService.isSuperUser()) {

			if (forum.getGradeType() == Forum.GRADE_BY_TOPIC) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Checks to see if the forum is locked after due date
	 * 
	 * @param forumId	forum
	 * @return	true 	if forum is not locked after the due date or the user is admin or facilitator
	 * 			false 	if forum is locked after the due date and the user is not admin or facilitator
	 * @throws Exception
	 */
	private boolean isForumLocked(Forum forum) throws Exception 
	{
		if (JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser())
			return false;
		
		if ((forum.getEndDate() == null) || (!forum.isLockForum())) 
			return false;
		
		GregorianCalendar gc = new GregorianCalendar();
		Date nowDate = gc.getTime();
		
		if (nowDate.getTime() > forum.getEndDate().getTime())
		{
				return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Check to see if forum is open
	 * 
	 * @param forum		forum
	 * @return	true 	if forum start date is before the current date/time or the user is admin or facilitator
	 * 			false 	if forum start date is after the current date/time or and the user is not admin or facilitator
	 * @throws Exception
	 */
	private boolean isForumOpen(Forum forum) throws Exception 
	{
		if (JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser())
			return true;
		
		if (forum.getStartDate() == null) 
			return true;
		
		GregorianCalendar gc = new GregorianCalendar();
		Date nowDate = gc.getTime();
		
		if (nowDate.getTime() > forum.getStartDate().getTime())
		{
			return true;
		}
		
		return false;
	}
	
	/**
	 * Checks to see if the category is locked after due date
	 * 
	 * @param category		category
	 * @return	true 	if category is not locked after the due date or the user is admin or facilitator
	 * 			false 	if category is locked after the due date and the user is not admin or facilitator
	 * @throws Exception
	 */
	private boolean isCategoryLocked(Category category) throws Exception 
	{
		if (JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser())
			return false;
				
		if ((category.getEndDate() == null) || (!category.isLockCategory())) 
			return false;
		
		GregorianCalendar gc = new GregorianCalendar();
		Date nowDate = gc.getTime();
		
		if (nowDate.getTime() > category.getEndDate().getTime())
		{
				return true;
		}
		
		return false;
	}
	
	/**
	 * Check to see if category is open
	 * 
	 * @param category		category
	 * @return	true 	if category start date is before the current date/time or the user is admin or facilitator
	 * 			false 	if start date is after the current date/time or and the user is not admin or facilitator
	 * @throws Exception
	 */
	private boolean isCategoryOpen(Category category) throws Exception 
	{
		if (JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser())
			return true;
		
		if (category.getStartDate() == null) 
			return true;
		
		GregorianCalendar gc = new GregorianCalendar();
		Date nowDate = gc.getTime();
						
		if (nowDate.getTime() > category.getStartDate().getTime())
		{
				return true;
		}
		
		return false;
	}
	
	private boolean isCategoryAccessibleToSpecialAccessUser (SpecialAccess specialAccess) throws Exception 
	{
		if (JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser())
			return true;
		
		GregorianCalendar gc = new GregorianCalendar();
		Date nowDate = gc.getTime();
		
		/* 
		 	check for category special access. If category special access is available
		   	verify the special access start date with current time. If special access is not
		   	available verify forum start dates with current time.
	    */
		boolean specialAccessUser = false, specialAccessUserAccess = false;
				
		if (specialAccess.getStartDate() != null)
		{
			if (specialAccess.getStartDate().getTime() > nowDate.getTime())
			{
				specialAccessUserAccess = false;
			}
			if (specialAccess.getEndDate() != null)
			{
				if ((specialAccess.getEndDate().getTime() > nowDate.getTime()))
				{
					specialAccessUserAccess = false;
				}
			}
			specialAccessUserAccess = true;
		}
		else
		{
			specialAccessUserAccess = true;
		}
		
		
		this.context.put("specialAccessUserAccess", specialAccessUserAccess);
				
		return specialAccessUserAccess;
	}
	
	private SpecialAccess getUserSpecialAccess (Forum forum) throws Exception 
	{
		// check forum special access
		List<SpecialAccess> forumSpecialAccessList = forum.getSpecialAccessList();
		
		List<SpecialAccess> specialAccessList = null;
		
		if ((forumSpecialAccessList != null) && (forumSpecialAccessList.size() > 0))
		{
			specialAccessList = forumSpecialAccessList;
		}
				
		SpecialAccess userSpecialAccess = null;
		boolean specialAccessUser = false;
		
		if (specialAccessList != null)
		{
			for (SpecialAccess specialAccess : specialAccessList)
			{
				UserSession currentUser = SessionFacade.getUserSession();
				if (specialAccess.getUserIds().contains(currentUser.getUserId()))
				{
					if (!ForumRepository.isForumAccessibleToUser(forum))
					{
						break;
					}
					
					specialAccessUser = true;
					userSpecialAccess = specialAccess;
					
					break;
				}
			}
		}	
			
		this.context.put("specialAccessUser", specialAccessUser);
		this.context.put("specialAccess", userSpecialAccess);
		
		return userSpecialAccess;
	}
	
	/**
	 * add forum grade type to context
	 */
	private void addGradeTypesToContext()
	{
		this.context.put("gradeDisabled", Forum.GRADE_DISABLED);
		this.context.put("gradeForum", Forum.GRADE_BY_FORUM);
		this.context.put("gradeTopic", Forum.GRADE_BY_TOPIC);
		this.context.put("gradeCategory", Forum.GRADE_BY_CATEGORY);		
	}
	
		
	/**
	 * update grade book
	 * @param grade		grade
	 * @param gradebookUid	gradebookUid
	 * @return true - if updated in gradebook
	 * 		   false - if not added or updated in gradebook 
	 * @throws Exception
	 */
	protected boolean updateGradebook(Topic topic, Grade grade) throws Exception 
	{
		String gradebookUid = ToolManager.getInstance().getCurrentPlacement().getContext();
		
		JForumGBService jForumGBService = null;
		jForumGBService = (JForumGBService)ComponentManager.get("org.etudes.api.app.jforum.JForumGBService");
		
		if (jForumGBService == null)
			return false;
				
		if (grade.getType() != Forum.GRADE_BY_TOPIC)
		{
			return false;
		}
		
		if (!jForumGBService.isGradebookDefined(gradebookUid))
		{
			return false;
		}
		
		boolean entryExisInGradebook = false;
		
		if (jForumGBService.isExternalAssignmentDefined(gradebookUid, "discussions-" + String.valueOf(grade.getId())))
		{
			entryExisInGradebook = true;
		}
		
		//add or update to gradebook
		String url = null;
		
		if (entryExisInGradebook)
		{
			/*remove entry in the gradebook and add again if there is no entry with the same name in the gradebook.
			Then publish the scores from grading page.*/
			jForumGBService.removeExternalAssessment(gradebookUid, "discussions-" + String.valueOf(grade.getId()));
			
			
			if (jForumGBService.isAssignmentDefined(gradebookUid, topic.getTitle()))
			{
				this.context.put("errorMessage", I18n.getMessage("Grade.AddEditTopicGradeBookConflictingAssignmentNameException"));
				return false;
			}
			
			if (jForumGBService.addExternalAssessment(gradebookUid, "discussions-" + String.valueOf(grade.getId()), url,  topic.getTitle(), 
					grade.getPoints(), null, I18n.getMessage("Grade.sendToGradebook.description")))
			{
				return true;
			}
			
			return false;
		}
		else
		{
			if (jForumGBService.isAssignmentDefined(gradebookUid, topic.getTitle()))
			{
				this.context.put("errorMessage", I18n.getMessage("Grade.AddEditTopicGradeBookConflictingAssignmentNameException"));
				
				return false;
			}
			
			if (jForumGBService.addExternalAssessment(gradebookUid, "discussions-" + String.valueOf(grade.getId()), url, topic.getTitle(), 
					grade.getPoints(), null, I18n.getMessage("Grade.sendToGradebook.description")))
			{
				return true;
			}
			
			return false;			
		}

	}
	
	/**
	 * remove entry from gradebook
	 * @param grade
	 */
	protected void removeEntryFromGradeBook(Grade grade) throws Exception
	{
		//remove entry from gradebook
		if (grade != null) 
		{
			
			JForumGBService jForumGBService = null;
			jForumGBService = (JForumGBService)ComponentManager.get("org.etudes.api.app.jforum.JForumGBService");
			
			if (jForumGBService == null)
				return;
			
			String gradebookUid = ToolManager.getInstance().getCurrentPlacement().getContext();
			if (jForumGBService.isExternalAssignmentDefined(gradebookUid, "discussions-" + String.valueOf(grade.getId())))
			{
				jForumGBService.removeExternalAssessment(gradebookUid, "discussions-" + String.valueOf(grade.getId()));
			}
		}
	}
}