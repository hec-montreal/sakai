/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/forum/ModerationHelper.java $ 
 * $Id: ModerationHelper.java 65265 2009-12-15 19:11:12Z murthy@etudes.org $ 
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.api.app.jforum.JForumGBService;
import org.etudes.jforum.JForum;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.EvaluationDAO;
import org.etudes.jforum.dao.ForumDAO;
import org.etudes.jforum.dao.GradeDAO;
import org.etudes.jforum.dao.TopicDAO;
import org.etudes.jforum.entities.Category;
import org.etudes.jforum.entities.Forum;
import org.etudes.jforum.entities.Grade;
import org.etudes.jforum.entities.Topic;
import org.etudes.jforum.repository.ForumRepository;
import org.etudes.jforum.repository.PostRepository;
import org.etudes.jforum.repository.TopicRepository;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.preferences.TemplateKeys;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.etudes.jforum.view.forum.common.ForumCommon;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.cover.UserDirectoryService;

/**
 * @author Rafael Steil
 */
public class ModerationHelper 
{
	private static Log logger = LogFactory.getLog(ModerationHelper.class);
	
	public static final int SUCCESS = 1;
	public static final int FAILURE = 2;
	public static final int IGNORE = 3;
	public static final int GRADED = 4;
	
	public int doModeration(String successUrl) throws Exception
	{
		int status = FAILURE;

		/*if (SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION)) {
			// Deleting topics
			if (JForum.getRequest().getParameter("topicRemove") != null) {
				if (SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION_POST_REMOVE)) {
					this.removeTopics();
					
					status = SUCCESS;
				}
			}
			else if (JForum.getRequest().getParameter("topicMove") != null) {
				if (SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION_TOPIC_MOVE)) {
					this.moveTopics();
					
					status = IGNORE;
				}
			}
			else if (JForum.getRequest().getParameter("topicLock") != null) {
				if (SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION_TOPIC_LOCK_UNLOCK)) {
					this.lockUnlockTopics(Topic.STATUS_LOCKED);
					
					status = SUCCESS;
				}
			}
			else if (JForum.getRequest().getParameter("topicUnlock") != null) {
				if (SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION_TOPIC_LOCK_UNLOCK)) {
					this.lockUnlockTopics(Topic.STATUS_UNLOCKED);
					
					status = SUCCESS;
				}
			}
		}*/
		if (JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser()) {
			// Deleting topics
			if (JForum.getRequest().getParameter("topicRemove") != null) {
				//if (SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION_POST_REMOVE)) {
					//this.removeTopics();
					if (this.removeTopics())	
						status = SUCCESS;
					else
						status = GRADED;
				//}
			}
			else if (JForum.getRequest().getParameter("topicMove") != null) {
				//if (SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION_TOPIC_MOVE)) {
					if (this.moveTopics())	
						status = IGNORE;
					else
						status = GRADED;
					
				//}
			}
			else if (JForum.getRequest().getParameter("topicLock") != null) {
				//if (SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION_TOPIC_LOCK_UNLOCK)) {
					this.lockUnlockTopics(Topic.STATUS_LOCKED);
					
					status = SUCCESS;
				//}
			}
			else if (JForum.getRequest().getParameter("topicUnlock") != null) {
				//if (SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION_TOPIC_LOCK_UNLOCK)) {
					this.lockUnlockTopics(Topic.STATUS_UNLOCKED);
					
					status = SUCCESS;
				//}
			}else if (JForum.getRequest().getParameter("topicArchive") != null) {
				//archive topics
				this.archiveTopics();
				
				status = SUCCESS;
			}
		}

		if (status == ModerationHelper.FAILURE) {
			this.denied();
		}
		else if (status == ModerationHelper.SUCCESS && successUrl != null) {
			JForum.setRedirect(successUrl);
		}else if (status == ModerationHelper.GRADED) {
		}
		
		return status;
	}
	
	public int doModeration() throws Exception
	{
		return this.doModeration(null);
	}
	
	private boolean removeTopics() throws Exception
	{
		String[] topics = JForum.getRequest().getParameterValues("topic_id");
		
		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
		
		List<Integer> forumsList = new ArrayList<Integer>();
		
		List<Topic> topicsToDelete = new ArrayList<Topic>();
		StringBuffer topicTitleList = new StringBuffer();
		
		List<Integer> forumsGradedList = new ArrayList<Integer>();
		StringBuffer forumNameList = new StringBuffer();
		
		boolean topicsGraded = false, forumGraded = false;
		
		if (topics != null && topics.length > 0) {
			for (int i = 0; i < topics.length; i++) {
				Topic t = tm.selectById(Integer.parseInt(topics[i]));
				
				//check if the forum is gradable forum and the forum is graded
				Forum forum  = ForumRepository.getForum(t.getForumId());

				//graded topics cannot be deleted
				if (forum.getGradeType() == Forum.GRADE_BY_TOPIC) {
					if (t.isGradeTopic()) {
						EvaluationDAO evaldao = DataAccessDriver.getInstance().newEvaluationDAO();
						int evalCount = evaldao.selectForumTopicEvaluationsCountById(t.getId());
						
						if (evalCount > 0) {
							if (t.getTitle().length() > 20) {
								topicTitleList.append(t.getTitle().substring(0, 20));
								topicTitleList.append("...");
							} else {
								topicTitleList.append(t.getTitle());
							}
							
							topicTitleList.append(",");
							topicsGraded = true;
							continue;
						}
					}
				} else if (forum.getGradeType() == Forum.GRADE_BY_FORUM) {
					//check if the forum is graded
					if (!forumsGradedList.contains(new Integer(forum.getId()))) {
						forumsGradedList.add(new Integer(forum.getId()));
						
						EvaluationDAO evaldao = DataAccessDriver.getInstance().newEvaluationDAO();
						int evalCount = evaldao.selectForumEvaluationsCount(forum.getId());
						if (evalCount > 0) {
							
							if (forum.getName().length() > 20) {
								forumNameList.append(forum.getName().substring(0, 20));
								forumNameList.append("...");
							} else {
								forumNameList.append(forum.getName());
							}
							forumNameList.append(",");
							forumGraded = true;

							continue;
						}
					}else {
						continue;
					}
				}
				
				if (!forumsList.contains(new Integer(t.getForumId()))) {
					forumsList.add(new Integer(t.getForumId()));
				}
				
				topicsToDelete.add(t);
				PostRepository.clearCache(t.getId());
			}
			
			
			//delete any associated grades that are not evaluated before deleting topics
			if (topicsToDelete.size() > 0) {
				for (Iterator<Topic> iter = topicsToDelete.iterator(); iter.hasNext(); ) {
					
					Topic t = iter.next();
					if (t.isGradeTopic()) {
						GradeDAO gradeDao = DataAccessDriver.getInstance().newGradeDAO();
						Grade grade = gradeDao.selectByForumTopicId(t.getForumId(), t.getId());
						
						//remove entry from gradebook
						if (grade != null && grade.isAddToGradeBook()) {
							
							removeEntryFromGradeBook(grade);
						}
						if (grade != null) {
							gradeDao.delete(grade.getId());
						}
					}
				}
			}
			
			//delete topics
			tm.deleteTopics(topicsToDelete);			
			
			TopicRepository.loadMostRecentTopics();
			ForumDAO fm = DataAccessDriver.getInstance().newForumDAO();
			
			// Reload changed forums
			for (Iterator iter = forumsList.iterator(); iter.hasNext(); ) {
				int forumId = ((Integer)iter.next()).intValue();

				TopicRepository.clearCache(forumId);
				int postId = fm.getMaxPostId(forumId);
				if (postId > -1) {
					fm.setLastPost(forumId, postId);
				}
				else {
					logger.warn("Could not find last post id for forum " + forumId);
				}
				
				ForumRepository.reloadForum(forumId);
			}
			if (topicsGraded && forumGraded) {
				String topicTitleListStr = topicTitleList.toString();
				if (topicTitleListStr.endsWith(","))
				{
					topicTitleListStr = topicTitleListStr.substring(0,topicTitleListStr.length()-1);
				}
				
				String forumNameListStr = forumNameList.toString();
				
				if (forumNameListStr.endsWith(","))
				{
					forumNameListStr = forumNameListStr.substring(0,forumNameListStr.length()-1);
				}
				
				JForum.getContext().put("errorMessage", I18n.getMessage("Forums.List.CannotDeleteGradedTopics", new Object[]{forumNameListStr, topicTitleListStr}));
				return false;
			}else if (topicsGraded) {
				String topicTitleListStr = topicTitleList.toString();
				if (topicTitleListStr.endsWith(","))
				{
					topicTitleListStr = topicTitleListStr.substring(0,topicTitleListStr.length()-1);
				}
				JForum.getContext().put("errorMessage", I18n.getMessage("Forums.List.CannotDeleteTopics", new Object[]{topicTitleListStr}));
				return false;
			} else if (forumGraded) {
				
				String forumNameListStr = forumNameList.toString();
				
				if (forumNameListStr.endsWith(","))
				{
					forumNameListStr = forumNameListStr.substring(0,forumNameListStr.length()-1);
				}
				
				JForum.getContext().put("errorMessage", I18n.getMessage("Forums.List.CannotDeleteForumTopics", new Object[]{forumNameListStr}));
				return false;
			}
		}
		return true;
	}
	
	private void lockUnlockTopics(int status) throws Exception
	{
		String[] topics = JForum.getRequest().getParameterValues("topic_id");
		
		if (topics != null && topics.length > 0) {
			int[] ids = new int[topics.length];

			for (int i = 0; i < topics.length; i++) {
				ids[i] = Integer.parseInt(topics[i]);
			}
			
			DataAccessDriver.getInstance().newTopicDAO().lockUnlock(ids, status);
			
			// Clear the cache
			Topic t = DataAccessDriver.getInstance().newTopicDAO().selectById(ids[0]);
			TopicRepository.clearCache(t.getForumId());
		}
	}
	
	private boolean moveTopics() throws Exception
	{
		JForum.getContext().put("persistData", JForum.getRequest().getParameter("persistData"));
		JForum.getContext().put("allCategories", ForumCommon.getAllCategoriesAndForums(false));
		
		String[] topics = JForum.getRequest().getParameterValues("topic_id");
		if (topics.length > 0) {
			// If forum_id is null, get from the database
			String forumId = JForum.getRequest().getParameter("forum_id");
			if (forumId == null) {
				forumId = Integer.toString(DataAccessDriver.getInstance().newTopicDAO().selectById(
						Integer.parseInt(topics[0])).getForumId());
			}
			
			JForum.getContext().put("forum_id", forumId);
			
			Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(Integer.parseInt(forumId));
			if (forum.getGradeType() == Forum.GRADE_BY_FORUM) {
				JForum.getContext().put("errorMessage", I18n.getMessage("Forums.List.CannotMoveGradableForumTopics", new Object[]{forum.getName()}));
				return false;
			}
			
			TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
			StringBuffer topicTitleList = new StringBuffer();
			boolean topicsGradable = false;
			
			StringBuffer sb = new StringBuffer(128);
			for (int i = 0; i < topics.length; i++) {
				Topic t = tm.selectById(Integer.parseInt(topics[i]));
				
				//gradable topics cannot be moved
				if (t.isGradeTopic()) {
					if (t.getTitle().length() > 20) {
						topicTitleList.append(t.getTitle().substring(0, 20));
						topicTitleList.append("...");
					} else {
						topicTitleList.append(t.getTitle());
					}
					
					topicTitleList.append(", ");
					topicsGradable = true;
					continue;
				}
				sb.append(topics[i]).append(",");
			}
			
			String sbStr = sb.toString().trim();
			if (sbStr.endsWith(","))
				sbStr = sbStr.substring(0,sbStr.length()-1);
			
			JForum.getContext().put("topics", sbStr);
			
			if (topicsGradable) {
				String topicTitleListStr = topicTitleList.toString().trim();
				  if (topicTitleListStr.endsWith(","))
				  {
					  topicTitleListStr = topicTitleListStr.substring(0,topicTitleListStr.length()-1);
				  }
				  JForum.getContext().put("errorMessage", I18n.getMessage("Forums.List.CannotMoveGradableTopics", new Object[]{topicTitleListStr}));
			}
			return true;
		}
		
		return true;
	}
	
	
	/**
	 * archive topics
	 */
	private void archiveTopics() throws Exception
	{
		JForum.getContext().put("persistData", JForum.getRequest().getParameter("persistData"));
		
		String[] topics = JForum.getRequest().getParameterValues("topic_id");
		if (topics.length > 0) {
			// If forum_id is null, get from the database
			String forumId = JForum.getRequest().getParameter("forum_id");
			if (forumId == null) {
				forumId = Integer.toString(DataAccessDriver.getInstance().newTopicDAO().selectById(
						Integer.parseInt(topics[0])).getForumId());
			}
			
			JForum.getContext().put("forum_id", forumId);
			
			//get archive category for the site
			Category archivedCategory = DataAccessDriver.getInstance().newCategoryDAO().selectArchiveCategory();
			
			if (archivedCategory == null) {
				//create archive category for the site
				archivedCategory = new Category();
				archivedCategory.setName(I18n.getMessage("Forums.List.Category.ArchivedTopics"));
				archivedCategory.setModerated(false);
				archivedCategory.setArchived(true);
				int categoryId = DataAccessDriver.getInstance().newCategoryDAO().addNew(archivedCategory);
				archivedCategory.setId(categoryId);
				
				ForumRepository.addCourseCategoryToCache(archivedCategory);
				
				//create forum under archive category
				Forum f = new Forum();
				f.setDescription(I18n.getMessage("Forums.List.Forum.ArchivedTopics.Description"));
				f.setStartDate(null);
				f.setEndDate(null);
				f.setIdCategories(archivedCategory.getId());
				f.setName(I18n.getMessage("Forums.List.Forum.ArchivedTopics"));
				f.setModerated(false);
				f.setType(Forum.TYPE_NORMAL);
				f.setAccessType(Forum.ACCESS_SITE);
				f.setGradeType(Forum.GRADE_DISABLED);
				
				int archivedForumId = DataAccessDriver.getInstance().newForumDAO().addNew(f);
				f.setId(archivedForumId);
				
				ForumRepository.addForum(f);
				
				moveTopicsToArchive(topics, f);
			} else {
				//archived category and forum is existing
				Category c = ForumRepository.getCategory(archivedCategory.getId());
				
				Collection<Forum> forums = c.getForums();
				//there should be only one forum with in archived category
				if (forums.size() == 1) {
					for (Iterator<Forum> iter = forums.iterator(); iter.hasNext(); ) {
						
						Forum f = iter.next();
						//archive topics
						moveTopicsToArchive(topics, f);
					}
				}
			}
		}
	}

	/**
	 * @param topics
	 * @param f
	 * @throws NumberFormatException
	 * @throws Exception
	 */
	private void moveTopicsToArchive(String[] topics, Forum f) throws NumberFormatException, Exception
	{
		if (topics != null) {
			int fromForumId = Integer.parseInt(JForum.getRequest().getParameter("forum_id"));
			int toForumId = f.getId();
			
			DataAccessDriver.getInstance().newForumDAO().moveTopics(topics, fromForumId, toForumId);
			
			ForumRepository.reloadForum(fromForumId);
			ForumRepository.reloadForum(toForumId);
			
			TopicRepository.clearCache(fromForumId);
			TopicRepository.clearCache(toForumId);
			
			TopicRepository.loadMostRecentTopics();
		}
	}
	
	public int moveTopicsSave(String successUrl) throws Exception
	{
		int status = SUCCESS;
		//if (!SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION_TOPIC_MOVE)) {
		if (!(JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser())){
			status = FAILURE;
		}
		else {
			String topics = JForum.getRequest().getParameter("topics");
			if (topics != null) {
				int fromForumId = Integer.parseInt(JForum.getRequest().getParameter("forum_id"));
				int toForumId = Integer.parseInt(JForum.getRequest().getParameter("to_forum"));
				
				DataAccessDriver.getInstance().newForumDAO().moveTopics(topics.split(","), fromForumId, toForumId);
				
				ForumRepository.reloadForum(fromForumId);
				ForumRepository.reloadForum(toForumId);
				
				TopicRepository.clearCache(fromForumId);
				TopicRepository.clearCache(toForumId);
				
				TopicRepository.loadMostRecentTopics();
			}
		}
		
		if (status == FAILURE) {
			this.denied();
		}
		else {
			this.moderationDone(successUrl);
		}
		
		return status;
	}
	
	public String moderationDone(String redirectUrl)
	{
		JForum.getRequest().setAttribute("template", TemplateKeys.MODERATION_DONE);
		JForum.getContext().put("message", I18n.getMessage("Moderation.ModerationDone", new String[] { redirectUrl }));
		
		return TemplateKeys.MODERATION_DONE;
	}
	
	public void denied()
	{
		this.denied(I18n.getMessage("Moderation.Denied"));
	}
	
	public void denied(String message)
	{
		JForum.getRequest().setAttribute("template", TemplateKeys.MODERATION_DENIED);
		JForum.getContext().put("message", message);		
	}
	
	/**
	 * remove entry from gradebook
	 * @param grade
	 */
	protected void removeEntryFromGradeBook(Grade grade) throws Exception
	{
		//remove entry from gradebook
		if (grade != null && grade.isAddToGradeBook()) 
		{
			
			JForumGBService jForumGBService = null;
			jForumGBService = (JForumGBService)ComponentManager.get("org.etudes.api.app.jforum.JForumGBService");
			
			if (jForumGBService == null)
				return;
			
			String gradebookUid = ToolManager.getInstance().getCurrentPlacement().getContext();
			
			jForumGBService.removeExternalAssessment(gradebookUid, "discussions-" + String.valueOf(grade.getId()));
		}
	}
}
