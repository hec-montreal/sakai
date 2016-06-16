/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/admin/ForumAction.java $ 
 * $Id: ForumAction.java 71055 2010-10-29 22:07:19Z murthy@etudes.org $ 
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
package org.etudes.jforum.view.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.api.app.jforum.JForumGBService;
import org.etudes.jforum.JForum;
import org.etudes.jforum.dao.CategoryDAO;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.EvaluationDAO;
import org.etudes.jforum.dao.ForumDAO;
import org.etudes.jforum.dao.GradeDAO;
import org.etudes.jforum.dao.TopicDAO;
import org.etudes.jforum.entities.Category;
import org.etudes.jforum.entities.Evaluation;
import org.etudes.jforum.entities.Forum;
import org.etudes.jforum.entities.Grade;
import org.etudes.jforum.entities.SpecialAccess;
import org.etudes.jforum.entities.Topic;
import org.etudes.jforum.repository.ForumRepository;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.SafeHtml;
import org.etudes.jforum.util.TreeGroup;
import org.etudes.jforum.util.date.DateUtil;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.preferences.TemplateKeys;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.etudes.jforum.view.admin.common.ModerationCommon;
import org.etudes.jforum.view.forum.ModerationHelper;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.cover.UserDirectoryService;

/**
 * @author Rafael Steil
 */
/*
 * 8/17/05 - Mallika - changing pulling up categories to select by course
 * 9/7/05 - Mallika - adding code to selectallbycourse even for insert method
 * 9/9/05 - Mallika - adding code to selectallbycourse to edit method
 * 9/27/05 - Mallika - adding code to make forums attachments enabled
 * 1/5/06 - Mallika - adding code to show forums warning
 * 1/6/06 - Mallika - correcting bug in warning code, warning used to always show
 * 1/9/06 - Mallika - adding code to handle dates
 * 1/24/06 - Mallika - adding code to insert and edit to set forums to read only if needed
 * 06/27/06 - Murthy - updated the code for start and stop dates 
 * 				in editSave and insertSave methods
 * 07/03/06 - Murthy - updated processOrdering to get forums as category manageForums
 *  */
public class ForumAction extends AdminCommand 
{
	private static Log logger = LogFactory.getLog(ForumAction.class);
	// Listing
	public void list() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		this.context.put("categories", ForumRepository.getAllManageCategories());

		context.put("calendarDateTimeFormat", SakaiSystemGlobals.getValue(ConfigKeys.CALENDAR_DATE_TIME_FORMAT));
		this.context.put("viewTitleManageForums", true);
		
		this.context.put("repository", new ForumRepository());
		this.setTemplateName(TemplateKeys.FORUM_ADMIN_LIST);
	}
	
	// One more, one more
	public void insert() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		CategoryDAO cm = DataAccessDriver.getInstance().newCategoryDAO();
		
		addGradeTypesToContext();
		
		this.context.put("groups", new TreeGroup().getNodes());
		this.context.put("selectedList", new ArrayList());
		this.setTemplateName(TemplateKeys.FORUM_ADMIN_INSERT);
				
		this.context.put("categories",cm.selectAllByCourse());
		
		//get current site
		Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
		context.put("site", site);

		Collection sakaiSiteGroups = site.getGroups();
		this.context.put("sakaigroups", sakaiSiteGroups);
		
		if (site.getToolForCommonId(SakaiSystemGlobals.getValue(ConfigKeys.GRADEBOOK_TOOL_ID)) != null)
		{
			this.context.put("enableGrading", Boolean.TRUE);
		}

		this.context.put("action", "insertSave");
		this.context.put("viewTitleManageForums", true);
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

	// Edit
	public void edit() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		CategoryDAO cm = DataAccessDriver.getInstance().newCategoryDAO();
		int forumId = this.request.getIntParameter("forum_id");
		
		addGradeTypesToContext();

		Forum forum = ForumRepository.getForum(forumId);
		if (forum == null) {
			new ModerationHelper().denied(I18n.getMessage("ForumListing.denied"));
			return;
		}
		
		Forum forumFromDB = DataAccessDriver.getInstance().newForumDAO().selectById(this.request.getIntParameter("forum_id"));
		
		this.context.put("forum", forumFromDB);
		
		Category forumCategory = DataAccessDriver.getInstance().newCategoryDAO().selectById(forumFromDB.getCategoryId());
		this.context.put("forumCategory", forumCategory);
		
		if (forumFromDB.getGradeType() == Forum.GRADE_BY_FORUM){
			Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByForumId(this.request.getIntParameter("forum_id"));
			if (grade != null)
			this.context.put("grade", grade);
		} else if (forumFromDB.getGradeType() == Forum.GRADE_DISABLED){
			
		}
		
        this.context.put("categories",cm.selectAllByCourse());
		
		//get current site
		Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
		context.put("site", site);
		Collection sakaiSiteGroups = site.getGroups();
		this.context.put("sakaigroups", sakaiSiteGroups);
				
		this.setTemplateName(TemplateKeys.FORUM_ADMIN_EDIT);
		this.context.put("action", "editSave");
		this.context.put("viewTitleManageForums", true);
		context.put("calendarDateTimeFormat", SakaiSystemGlobals.getValue(ConfigKeys.CALENDAR_DATE_TIME_FORMAT));
		
		if (site.getToolForCommonId(SakaiSystemGlobals.getValue(ConfigKeys.GRADEBOOK_TOOL_ID)) != null)
		{
			this.context.put("enableGrading", Boolean.TRUE);
		}
	}
	
	//  Save information
	public void editSave() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		addGradeTypesToContext();
		
		Forum f = new Forum(ForumRepository.getForum(this.request.getIntParameter("forum_id")));
		boolean moderated = f.isModerated();
		int categoryId = f.getCategoryId();
		
		f.setDescription(SafeHtml.escapeJavascript(this.request.getParameter("description")));
		
		Category c = DataAccessDriver.getInstance().newCategoryDAO().selectById(this.request.getIntParameter("categories_id"));
		if (c.getStartDate() == null  && c.getEndDate() == null)
		{
			String startDateParam = this.request.getParameter("start_date");
			if (startDateParam != null && startDateParam.trim().length() > 0)
			{
				Date startDate;
				try
				{
					startDate = DateUtil.getDateFromString(startDateParam.trim());
				} catch (ParseException e)
				{
					this.context.put("errorMessage", I18n.getMessage("Forums.Forum.DateParseError"));
					this.request.addParameter("forum_id", String.valueOf(f.getId()));
					this.edit();
					return;
				}
				f.setStartDate(startDate);
				
				if (startDate != null){
					SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
					f.setStartDateFormatted(df.format(startDate));
				}
			}
			else
			{
			  f.setStartDate(null);
			}
			
			String endDateParam = this.request.getParameter("end_date");
			if (endDateParam != null && endDateParam.trim().length() > 0)
			{
				Date endDate;
				try
				{
					endDate = DateUtil.getDateFromString(endDateParam.trim());
				} catch (ParseException e)
				{
					this.context.put("errorMessage", I18n.getMessage("Forums.Forum.DateParseError"));
					this.request.addParameter("forum_id", String.valueOf(f.getId()));
					this.edit();
					return;
				}
				f.setEndDate(endDate);
				
				String lockForum = this.request.getParameter("lock_forum");
				if (lockForum!= null && "1".equals(lockForum)){
					f.setLockForum(true);
				}
				else
				{
					f.setLockForum(false);
				}
				
				if (endDate != null){
					SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
					f.setEndDateFormatted(df.format(endDate));
				}
			}
			else
			{
			  f.setEndDate(null);
			  f.setLockForum(false);
			}
		}
		else
		{
			f.setStartDate(null);
			f.setEndDate(null);
			f.setLockForum(false);
		}
		
		// check if grading is enabled for forum category
		boolean forumCategoryChangedToGradeCategory = false;
		if ((f.getCategoryId() != this.request.getIntParameter("categories_id")) && (c.isGradeCategory()))
		{
			forumCategoryChangedToGradeCategory = true;	
		}
		
		// check category special access 
		boolean forumCategoryChanged = false;
		if (f.getCategoryId() != this.request.getIntParameter("categories_id"))
		{
			forumCategoryChanged = true;
		}
		
		f.setIdCategories(this.request.getIntParameter("categories_id"));
		f.setName(SafeHtml.escapeJavascript(this.request.getParameter("forum_name")));
		f.setModerated("1".equals(this.request.getParameter("moderate")));
		f.setType(Integer.parseInt(this.request.getParameter("forum_type")));
		f.setAccessType(Integer.parseInt(this.request.getParameter("access_type")));
		
		if (f.getAccessType() == Forum.ACCESS_GROUPS)
		{
			String selectedGroups[] = (String[])this.request.getParameterValues("selectedGroups");
			if (logger.isDebugEnabled()) logger.debug("selectedGroups[] "+ selectedGroups);
			List selectedGroupsList = new ArrayList();
			
			if (selectedGroups != null && selectedGroups.length > 0){
				for (int i = 0; i < selectedGroups.length; i++)
					selectedGroupsList.add(selectedGroups[i]);
			}
			f.setGroups(selectedGroupsList);
		}
		
		
		// check if grading is enabled for forum category
		if (c.isGradeCategory() && (this.request.getParameter("grading_enabled") != null && 
				Integer.parseInt(this.request.getParameter("grading_enabled")) == 1))
		{
			JForum.enableCancelCommit();
			this.context.put("errorMessage", I18n.getMessage("Forums.Forum.CannotEditForumGrading"));
			this.request.addParameter("forum_id", String.valueOf(f.getId()));
			this.edit();
			return;
		}
		
		// grading - Once the forum is graded the forum "Grading" cannot be changed to Not Enabled or Enabled - By Topic
		if ((f.getGradeType() == Forum.GRADE_BY_TOPIC) || (f.getGradeType() == Forum.GRADE_BY_FORUM)) 
		{
			boolean editForum = true;
			EvaluationDAO evaldao = DataAccessDriver.getInstance().newEvaluationDAO();
			int evalCount = 0;
			
			if (f.getGradeType() == Forum.GRADE_BY_TOPIC)
			{
				evalCount = evaldao.selectForumTopicEvaluationsCount(f.getId());
			}
			else if (f.getGradeType() == Forum.GRADE_BY_FORUM)
			{
				evalCount = evaldao.selectForumEvaluationsCount(f.getId());
			}
			
			if (this.request.getParameter("grading_enabled") == null) {
				if (evalCount > 0)
				{
					editForum = false;
				}
			} else {
				if (evalCount > 0) {
					if ((f.getGradeType() == Forum.GRADE_BY_TOPIC && 
							(Integer.parseInt(this.request.getParameter("grading_type")) == Forum.GRADE_BY_FORUM))
						|| (f.getGradeType() == Forum.GRADE_BY_FORUM && 
							(Integer.parseInt(this.request.getParameter("grading_type")) == Forum.GRADE_BY_TOPIC))
						|| ((f.getGradeType() == Forum.GRADE_BY_FORUM || f.getGradeType() == Forum.GRADE_BY_TOPIC) && 
						(Integer.parseInt(this.request.getParameter("grading_enabled")) == Forum.GRADE_DISABLED))) {
						editForum = false;
					}
				}
			}
			
			if (!editForum){
				
				if (forumCategoryChangedToGradeCategory)
				{
					this.context.put("errorMessage", I18n.getMessage("Forums.Forum.CannotEditForumToGradableCategory"));
				}
				else
				{
					this.context.put("errorMessage", I18n.getMessage("Forums.Forum.CannotEditForum"));
				}
				
				JForum.enableCancelCommit();
				this.request.addParameter("forum_id", String.valueOf(f.getId()));
				this.edit();
				return;
			}
		}
		
		if (this.request.getParameter("grading_enabled") != null && Integer.parseInt(this.request.getParameter("grading_enabled")) == 1)
		{
			if (this.request.getParameter("grading_type") != null)
			{
				f.setGradeType(Integer.parseInt(this.request.getParameter("grading_type")));
			}
			else
			{
				f.setGradeType(Forum.GRADE_BY_TOPIC);
			}
		}else {
			f.setGradeType(Forum.GRADE_DISABLED);
		}
		
		// if category is changed delete forum special access.
		if (forumCategoryChanged)
		{
			List<SpecialAccess> forumSpecialAccessList = DataAccessDriver.getInstance().newSpecialAccessDAO().selectByForumId(f.getId());
			for (SpecialAccess exiSpecialAccess : forumSpecialAccessList)
			{
				DataAccessDriver.getInstance().newSpecialAccessDAO().delete(exiSpecialAccess.getId());
			}
		}
		
		DataAccessDriver.getInstance().newForumDAO().update(f);

		if (moderated != f.isModerated()) {
			new ModerationCommon().setTopicModerationStatus(f.getId(), f.isModerated());
		}
		
		//if grading type is forum update grade
		if (f.getGradeType() == Forum.GRADE_BY_FORUM) {
			//remove existing topic grades if any
			List<Grade> grades = DataAccessDriver.getInstance().newGradeDAO().selectForumTopicGradesByForumId(f.getId());
			for (Grade grade : grades)	{
				Topic topic = DataAccessDriver.getInstance().newTopicDAO().selectById(grade.getTopicId());
				topic.setGradeTopic(false);
				DataAccessDriver.getInstance().newTopicDAO().update(topic);
				removeEntryFromGradeBook(grade);
				DataAccessDriver.getInstance().newGradeDAO().delete(grade.getId());
			}
			Grade exisGrade = DataAccessDriver.getInstance().newGradeDAO().selectByForumId(f.getId());

			//update existing forum grade or create grade
			if (exisGrade == null)
			{
				Grade grade = new Grade();
				
				grade.setContext(ToolManager.getCurrentPlacement().getContext());
				grade.setForumId(f.getId());
				try {
					Float points = Float.parseFloat(this.request.getParameter("points"));
					
					if (points.floatValue() < 0) points = Float.valueOf(0.0f);
					if (points.floatValue() > 1000) points = Float.valueOf(1000.0f);
					points = Float.valueOf(((float) Math.round(points.floatValue() * 100.0f)) / 100.0f);
					
					grade.setPoints(points);
				} catch (NumberFormatException ne) {
					grade.setPoints(0f);
				}			
				grade.setType(Forum.GRADE_BY_FORUM);
				
				int gradeId = DataAccessDriver.getInstance().newGradeDAO().addNew(grade);
				grade.setId(gradeId);
				
				String sendToGradebook = this.request.getParameter("send_to_grade_book");
				boolean addToGradeBook = false;
				if ((sendToGradebook != null) && (Integer.parseInt(sendToGradebook) == 1))
				{
					addToGradeBook = true;
				}
								
				//if add to grade book is true then add the grade to grade book
				if (addToGradeBook)
				{
					grade.setAddToGradeBook(addToGradeBook);
					if (f.getStartDate() != null)
					{
						Calendar calendar = Calendar.getInstance();
						
						Date startDate = f.getStartDate();
						
						Date nowDate = calendar.getTime();
						
						if (nowDate.before(startDate))
						{
							addToGradeBook = false;
						}
						else
						{
							addToGradeBook = updateGradebook(grade);
						}
					}
					else
					{
						addToGradeBook = updateGradebook(grade);
					}
				}
				grade.setAddToGradeBook(addToGradeBook);
				DataAccessDriver.getInstance().newGradeDAO().updateAddToGradeBookStatus(gradeId, addToGradeBook);
			}
			else 
			{
				try 
				{
					Float points = Float.parseFloat(this.request.getParameter("points"));
					
					if (points.floatValue() < 0) points = Float.valueOf(0.0f);
					if (points.floatValue() > 1000) points = Float.valueOf(1000.0f);
					points = Float.valueOf(((float) Math.round(points.floatValue() * 100.0f)) / 100.0f);
					exisGrade.setPoints(points);
				} catch (NumberFormatException ne) {
				}			
				exisGrade.setType(Forum.GRADE_BY_FORUM);
				
				String sendToGradebook = this.request.getParameter("send_to_grade_book");
				boolean addToGradeBook = false;
				if ((sendToGradebook != null) && (Integer.parseInt(sendToGradebook) == 1))
				{
					addToGradeBook = true;
				}
						
				DataAccessDriver.getInstance().newGradeDAO().updateForumGrade(exisGrade);
				
				exisGrade.setAddToGradeBook(addToGradeBook);
				
				//update title in gradebook if grades are released to gradebook
				
				if (addToGradeBook)
				{
					if (f.getStartDate() != null)
					{
						Calendar calendar = Calendar.getInstance();

						Date startDate = f.getStartDate();

						Date nowDate = calendar.getTime();

						if (nowDate.before(startDate))
						{
							// remove any existing entry in the grade book
							removeEntryFromGradeBook(exisGrade);
							addToGradeBook = false;
						}
						else
						{
							addToGradeBook = updateGradebook(exisGrade);
						}
					}
					else
					{
						addToGradeBook = updateGradebook(exisGrade);
					}
				} 
				else
				{
					// remove any existing entry in the grade book
					removeEntryFromGradeBook(exisGrade);
				}
				exisGrade.setAddToGradeBook(addToGradeBook);
				DataAccessDriver.getInstance().newGradeDAO().updateAddToGradeBookStatus(exisGrade.getId(), addToGradeBook);
			}
		}
		else if (f.getGradeType() == Forum.GRADE_BY_TOPIC) 
		{
			updateGradebookTopics(f);
		} else{
			//delete forum or topic grades if existing
			List<Grade> grades = DataAccessDriver.getInstance().newGradeDAO().selectForumTopicGradesByForumId(f.getId());
			for (Grade grade : grades)	{
				Topic topic = DataAccessDriver.getInstance().newTopicDAO().selectById(grade.getTopicId());
				topic.setGradeTopic(false);
				DataAccessDriver.getInstance().newTopicDAO().update(topic);
				
				removeEntryFromGradeBook(grade);
				
				DataAccessDriver.getInstance().newGradeDAO().delete(grade.getId());
			}
			
			Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByForumId(f.getId());
			
			removeEntryFromGradeBook(grade);
			
			if (grade != null)
				DataAccessDriver.getInstance().newGradeDAO().delete(grade.getId());
		}
		
		if (categoryId != f.getCategoryId()) {
			f.setIdCategories(categoryId);
			ForumRepository.removeForum(f);
			
			f.setIdCategories(this.request.getIntParameter("categories_id"));
			ForumRepository.addForum(f);
		}
		else {
			ForumRepository.reloadForum(f.getId());
		}
		
		// auto save navigation
		autoSaveNavigation();
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

	public void up() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		this.saveForumDates();
		this.processOrdering(true);
	}
	
	public void down() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		this.saveForumDates();
		this.processOrdering(false);
	}
	
	private void processOrdering(boolean up) throws Exception
	{
		Forum toChange = new Forum(ForumRepository.getForum(Integer.parseInt(
				this.request.getParameter("forum_id_1"))));
		
		Category category = ForumRepository.getCategory(toChange.getCategoryId());
		//List forums = new ArrayList(category.getForums());
		List forums = new ArrayList(category.getManageForums());
		int index = forums.indexOf(toChange);
		
		if (index == -1 || (up && index == 0) || (!up && index + 1 == forums.size())) {
			this.list();
			return;
		}
		
		ForumDAO fm = DataAccessDriver.getInstance().newForumDAO();
		
		if (up) {
			// Get the forum which comes *before* the forum we're changing
			Forum otherForum = new Forum((Forum)forums.get(index - 1));
			fm.setOrderUp(toChange, otherForum);
		}
		else {
			// Get the forum which comes *after* the forum we're changing
			Forum otherForum = new Forum((Forum)forums.get(index + 1));
			fm.setOrderDown(toChange, otherForum);
		}
		
		category.changeForumOrder(toChange);
		ForumRepository.refreshCategory(category);
		
		this.list();
	}
	
	
	public void delete() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		String ids[] = this.request.getParameterValues("forum_id");
		List errors = new ArrayList();
		StringBuffer forumNameList = new StringBuffer();
		boolean errFlag = false;

		ForumDAO fm = DataAccessDriver.getInstance().newForumDAO();
		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
		EvaluationDAO evaldao = DataAccessDriver.getInstance().newEvaluationDAO();
		GradeDAO gradedao = DataAccessDriver.getInstance().newGradeDAO();

		if (ids != null) {
			for (int i = 0; i < ids.length; i++) {
				int forumId = Integer.parseInt(ids[i]);
				if (fm.canDelete(forumId))
				{
					// check the grade type and check if any evaluations are existing
					Forum f = new Forum(ForumRepository.getForum(forumId));
					if (f.getGradeType() == Forum.GRADE_BY_FORUM)
					{
						int evalCout = evaldao.selectForumEvaluationsCount(f.getId());
						if (evalCout > 0)
						{
							forumNameList.append(f.getName());
							forumNameList.append(",");
							errFlag = true;
							continue;
						} else {
							Grade grade = gradedao.selectByForumId(forumId);
							
							//remove entry from gradebook
							removeEntryFromGradeBook(grade);
							
							if (grade != null)
								gradedao.delete(grade.getId());
							else {
								if (logger.isWarnEnabled()) logger.warn("delete() : The grade for forum id = "
										+ f.getId() +" is grade by forum but grade doesn't exist");
							}
						}
					}
					// delete special access
					List<SpecialAccess> forumSpecialAccessList = DataAccessDriver.getInstance().newSpecialAccessDAO().selectByForumId(forumId);
					for (SpecialAccess exiSpecialAccess : forumSpecialAccessList)
					{
						DataAccessDriver.getInstance().newSpecialAccessDAO().delete(exiSpecialAccess.getId());
					}
					
					tm.deleteByForum(forumId);
					fm.delete(forumId);

					ForumRepository.removeForum(f);
				}
				else
				{
					int id = Integer.parseInt(ids[i]);
					Forum f = new Forum(ForumRepository.getForum(id));
					forumNameList.append(f.getName());
					forumNameList.append(",");
					errFlag = true;
				}
			}
		}
		if (errFlag == true)
		{
		  String forumNameListStr = forumNameList.toString();
		  if (forumNameListStr.endsWith(","))
		  {
			forumNameListStr = forumNameListStr.substring(0,forumNameListStr.length()-1);
		  }
		  errors.add(I18n.getMessage(I18n.CANNOT_DELETE_FORUM, new Object[]{forumNameListStr}));
		}

		if (errors.size() > 0) {
			this.context.put("errorMessage", errors);
		}
		this.list();
	}
	
	// A new one
	public void insertSave() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		if (this.request.getIntParameter("categories_id") == -1)
		{
			this.context.put("errorMessage", I18n.getMessage("Forums.Forum.SelectCategory"));
			this.insert();
			return;
		}
		
		addGradeTypesToContext();
		
		// check if grading is enabled for forum category
		Category c = DataAccessDriver.getInstance().newCategoryDAO().selectById(this.request.getIntParameter("categories_id"));
		
		if (c.isGradeCategory() && (this.request.getParameter("grading_enabled") != null && 
				Integer.parseInt(this.request.getParameter("grading_enabled")) == 1))
		{
			JForum.enableCancelCommit();
			this.context.put("errorMessage", I18n.getMessage("Forums.Forum.CannotAddForumGrading"));
			this.insert();
			return;
		}
		
		Forum f = new Forum();
		f.setDescription(SafeHtml.escapeJavascript(this.request.getParameter("description")));

		if (c.getStartDate() == null  && c.getEndDate() == null)
		{
			String startDateParam = this.request.getParameter("start_date");
			if (startDateParam != null && startDateParam.trim().length() > 0)
			{
				Date startDate;
				try
				{
					startDate = DateUtil.getDateFromString(startDateParam.trim());
				} catch (ParseException e)
				{
					this.context.put("errorMessage", I18n.getMessage("Forums.Forum.DateParseError"));
					this.insert();
					return;
				}
				f.setStartDate(startDate);
				
				if (startDate != null){
					SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
					f.setStartDateFormatted(df.format(startDate));
				}
			}
			else
			{
			  f.setStartDate(null);
			}
			
			String endDateParam = this.request.getParameter("end_date");
			if (endDateParam != null && endDateParam.trim().length() > 0)
			{
				Date endDate;
				try
				{
					endDate = DateUtil.getDateFromString(endDateParam.trim());
				} catch (ParseException e)
				{
					this.context.put("errorMessage", I18n.getMessage("Forums.Forum.DateParseError"));
					this.insert();
					return;
				}
				f.setEndDate(endDate);
				String lockForum = this.request.getParameter("lock_forum");
				if (lockForum!= null && "1".equals(lockForum)){
					f.setLockForum(true);
				}
				else
				{
					f.setLockForum(false);
				}
				
				if (endDate != null){
					SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
					f.setEndDateFormatted(df.format(endDate));
				}
			}
			else
			{
			  f.setEndDate(null);
			  f.setLockForum(false);
			}
		}
		else
		{
			f.setStartDate(null);
			f.setEndDate(null);
			f.setLockForum(false);
		}
			

		f.setIdCategories(this.request.getIntParameter("categories_id"));
		f.setName(SafeHtml.escapeJavascript(this.request.getParameter("forum_name")));	
		f.setModerated("1".equals(this.request.getParameter("moderate")));
		f.setType(Integer.parseInt(this.request.getParameter("forum_type")));
		f.setAccessType(Integer.parseInt(this.request.getParameter("access_type")));
		if (f.getAccessType() == Forum.ACCESS_GROUPS){
			String selectedGroups[] = (String[])this.request.getParameterValues("selectedGroups");
			if (logger.isDebugEnabled()) logger.debug("selectedGroups[] "+ selectedGroups);
			
			List selectedGroupsList = new ArrayList();
			
			if (selectedGroups != null && selectedGroups.length > 0){
				for (int i = 0; i < selectedGroups.length; i++)
					selectedGroupsList.add(selectedGroups[i]);
			}
			f.setGroups(selectedGroupsList);
		}
		if (this.request.getParameter("grading_enabled") != null && 
				Integer.parseInt(this.request.getParameter("grading_enabled")) == 1)
			f.setGradeType(Integer.parseInt(this.request.getParameter("grading_type")));
		else 
			f.setGradeType(Forum.GRADE_DISABLED);
		
		int forumId = DataAccessDriver.getInstance().newForumDAO().addNew(f);
		f.setId(forumId);
		
		ForumRepository.addForum(f);
		
		//if grading type is forum create grading
		if (f.getGradeType() == Forum.GRADE_BY_FORUM) {
			Grade grade = new Grade();
			
			grade.setContext(ToolManager.getCurrentPlacement().getContext());
			grade.setForumId(forumId);
			try {
				Float points = Float.parseFloat(this.request.getParameter("points"));
				
				if (points.floatValue() < 0) points = Float.valueOf(0.0f);
				if (points.floatValue() > 1000) points = Float.valueOf(10000.0f);
				points = Float.valueOf(((float) Math.round(points.floatValue() * 100.0f)) / 100.0f);
				
				grade.setPoints(points);
			} catch (NumberFormatException ne) {
				grade.setPoints(0f);
			}			
			grade.setType(Forum.GRADE_BY_FORUM);
			
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
				if (f.getStartDate() != null)
				{
					Calendar calendar = Calendar.getInstance();
					
					Date startDate = f.getStartDate();
					
					Date nowDate = calendar.getTime();
					
					if (nowDate.before(startDate))
					{
						addToGradeBook = false;
					}
					else
					{
						addToGradeBook = updateGradebook(grade);
					}

				}
				else
				{
					addToGradeBook = updateGradebook(grade);
				}
			}
			grade.setAddToGradeBook(addToGradeBook);
			DataAccessDriver.getInstance().newGradeDAO().updateAddToGradeBookStatus(gradeId, addToGradeBook);
		}
		
		// auto save navigation
		autoSaveNavigation();
	}
	
	/**
	 * update grade book
	 * @param grade		grade
	 * @return true - if updated in gradebook
	 * 		   false - if not added or updated in gradebook 
	 * @throws Exception
	 */
	protected boolean updateGradebook(Grade grade) throws Exception 
	{
		String gradebookUid = ToolManager.getInstance().getCurrentPlacement().getContext();
		
		JForumGBService jForumGBService = null;
		jForumGBService = (JForumGBService)ComponentManager.get("org.etudes.api.app.jforum.JForumGBService");
		
		if (jForumGBService == null)
			return false;
				
		if (grade.getType() != Forum.GRADE_BY_FORUM)
		{
			return false;
		}
		
		if (!jForumGBService.isGradebookDefined(gradebookUid))
		{
			return false;
		}
		
		Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(grade.getForumId());
		
		boolean entryExisInGradebook = false;
		
		if (jForumGBService.isExternalAssignmentDefined(gradebookUid, "discussions-" + String.valueOf(grade.getId())))
		{
			entryExisInGradebook = true;
		}
		
		// if entry in gradebook exists remove it
		if (forum.getAccessType() == Forum.ACCESS_DENY)
		{
			if (entryExisInGradebook)
			{
				jForumGBService.removeExternalAssessment(gradebookUid, "discussions-" + String.valueOf(grade.getId()));

			}
			this.context.put("errorMessage", I18n.getMessage("Grade.AddEditForumDenyAccessGradesToGradebook"));
			
			return false;
		}
		
		//add or update to gradebook
		String url = null;
		
		if (entryExisInGradebook)
		{
			/*remove entry in the gradebook and add again if there is no entry with the same name in the gradebook.*/
			jForumGBService.removeExternalAssessment(gradebookUid, "discussions-" + String.valueOf(grade.getId()));
			
			
			if (jForumGBService.isAssignmentDefined(gradebookUid, forum.getName()))
			{
				this.context.put("errorMessage", I18n.getMessage("Grade.AddEditForumGradeBookConflictingAssignmentNameException"));
				return false;
			}
			
			if (jForumGBService.addExternalAssessment(gradebookUid, "discussions-" + String.valueOf(grade.getId()), url, forum.getName(), 
					grade.getPoints(), forum.getEndDate(), I18n.getMessage("Grade.sendToGradebook.description")))
			{
				try
				{
					sendGradesToGradebook(grade, gradebookUid, jForumGBService);
				} 
				catch (Exception e)
				{
					if (logger.isErrorEnabled())
					{
						logger.error(this.getClass().getName()+".updateGradebook() : "+ e.toString(), e);
					}
				}
				return true;
			}
			
			return false;
		}
		else
		{
			if (jForumGBService.isAssignmentDefined(gradebookUid, forum.getName()))
			{
				this.context.put("errorMessage", I18n.getMessage("Grade.AddEditForumGradeBookConflictingAssignmentNameException"));
				
				return false;
			}
			
			if (jForumGBService.addExternalAssessment(gradebookUid, "discussions-" + String.valueOf(grade.getId()), url, forum.getName(), 
					grade.getPoints(), forum.getEndDate(), I18n.getMessage("Grade.sendToGradebook.description")))
			{
				try
				{
					sendGradesToGradebook(grade, gradebookUid, jForumGBService);
				} 
				catch (Exception e)
				{
					if (logger.isErrorEnabled())
					{
						logger.error(this.getClass().getName()+".updateGradebook() : "+ e.toString(), e);
					}
				}
				
				return true;
			}
			
			return false;			
		}

	}

	/**
	 * Apply changes made to forum's list.
	 * @throws Exception
	 */
	public void applyChanges() throws Exception
	{
		
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		if (JForum.getRequest().getParameter("deleteForums") != null) 
		{
			this.delete();
		}
		else if (JForum.getRequest().getParameter("saveForums") != null) 
		{
			this.saveForumDates();
			
			//this.list();
			autoSaveNavigation();
		}
	}

	/**
	 * save forum start and end dates
	 */
	protected void saveForumDates() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		List errors = new ArrayList();
		boolean errFlag = false;
		StringBuffer forumNameList = new StringBuffer();
		
		Enumeration<?> paramNames = this.request.getParameterNames();
		
		while (paramNames.hasMoreElements())
		{
			String paramName = (String) paramNames.nextElement();

			if (paramName.startsWith("startdate_"))
			{
				// paramName is in the format startdate_forumId
				String id[] = paramName.split("_");
				String forumId = null;
				forumId = id[1];
				
				Forum existingForum = ForumRepository.getForum(Integer.parseInt(forumId));
				
				Forum f = new Forum(existingForum);
				
				//startdate_forumId
				String startDateParam = null;				
				startDateParam = this.request.getParameter("startdate_"+ id[1]);
				if (startDateParam != null && startDateParam.trim().length() > 0)
				{
					Date startDate;
					try
					{
						startDate = DateUtil.getDateFromString(startDateParam.trim());
					} catch (ParseException e)
					{
						errFlag = true;
						forumNameList.append(f.getName());
						forumNameList.append(",");
						continue;
					}
					f.setStartDate(startDate);
				}
				else
				{
				  f.setStartDate(null);
				}
				
				//enddate_forumId
				String endDateParam = null;
				endDateParam = this.request.getParameter("enddate_"+ id[1]);
				
				if (endDateParam != null && endDateParam.trim().length() > 0)
				{
					Date endDate;
					try
					{
						endDate = DateUtil.getDateFromString(endDateParam.trim());
					} catch (ParseException e)
					{
						errFlag = true;
						forumNameList.append(f.getName());
						forumNameList.append(",");
						continue;
					}
					f.setEndDate(endDate);
					
					//lockforum_forumId
					String lockForum = this.request.getParameter("lockforum_"+ id[1]);
					if (lockForum!= null && "1".equals(lockForum)){
						f.setLockForum(true);
					}
					else
					{
						f.setLockForum(false);
					}
				}
				else
				{
				  f.setEndDate(null);
				  f.setLockForum(false);
				}
				
				// update if there are date changes
				boolean datesChanged = false;
				
				if (existingForum.getStartDate() == null)
				{
					if (f.getStartDate() != null)
					{
						datesChanged = true;
					}
				}
				else
				{
					if (f.getStartDate() == null)
					{
						datesChanged = true;
					}
					else if (!f.getStartDate().equals(existingForum.getStartDate()))
					{
						datesChanged = true;
					}
				}
				
				if (!datesChanged)
				{
					if (existingForum.getEndDate() == null)
					{
						if (f.getEndDate() != null)
						{
							datesChanged = true;
						}
					}
					else
					{
						if (f.getEndDate() == null)
						{
							datesChanged = true;
						}
						else if (!f.getEndDate().equals(existingForum.getStartDate()))
						{
							datesChanged = true;
						}
					}
				}
								
				if (datesChanged)
				{
					DataAccessDriver.getInstance().newForumDAO().updateDates(f);
				
					// update gradable forums and topics with date changes in the gradebook
					if (f.getGradeType() == Forum.GRADE_BY_FORUM)
					{
						Grade forumGrade = DataAccessDriver.getInstance().newGradeDAO().selectByForumId(f.getId());
						boolean addToGradeBook = false;
						
						if (forumGrade != null && forumGrade.isAddToGradeBook())
						{
							addToGradeBook = updateGradebook(forumGrade);
							forumGrade.setAddToGradeBook(addToGradeBook);
							DataAccessDriver.getInstance().newGradeDAO().updateAddToGradeBookStatus(forumGrade.getId(), addToGradeBook);
						}
					}
					else if (f.getGradeType() == Forum.GRADE_BY_TOPIC)
					{
						updateGradebookTopics(f);
					}
				}				
			}
		}
		
		
		if (errFlag == true)
		{
		  String forumNameListStr = forumNameList.toString();
		  if (forumNameListStr.endsWith(","))
		  {
			forumNameListStr = forumNameListStr.substring(0,forumNameListStr.length()-1);
		  }
		  errors.add(I18n.getMessage("Forums.List.CannotUpdateForumDates", new Object[]{forumNameListStr}));
		}
		
		if (errors.size() > 0) {
			this.context.put("errorMessage", errors);
		}
	}
	
	/**
	 * send grades to gradebook
	 * @param grade			grade
	 * @param gradebookUid	gradebookuid
	 * @param jForumGBService	jforum gradebook service
	 * @throws Exception
	 */
	protected void sendGradesToGradebook(Grade grade, String gradebookUid, JForumGBService jForumGBService) throws Exception
	{
		if (grade.isAddToGradeBook())
		{
			// send grades to gradebook
			List<Evaluation> evaluations = null;
			EvaluationDAO.EvaluationsSort evalSort = EvaluationDAO.EvaluationsSort.last_name_a;
			evaluations = DataAccessDriver.getInstance().newEvaluationDAO().selectForumEvaluations(grade.getForumId(), evalSort);
			
			Map<String, Double> scores = new HashMap<String, Double>();
			for(Evaluation eval: evaluations) 
			{
				if (eval.isReleased())
				{
					String key = eval.getSakaiUserId();
					Float userScore = eval.getScore();
					scores.put(key, (userScore == null) ? null : Double.valueOf(userScore.doubleValue()));
				}
			}
			jForumGBService.updateExternalAssessmentScores(gradebookUid, "discussions-"+ String.valueOf(grade.getId()), scores);
		}
	}
	
	/**
	 * update topics in the gradebook
	 * @param f			Forum
	 * @throws Exception
	 */
	protected void updateGradebookTopics(Forum f) throws Exception
	{
		//delete forum grade if existing 
		Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByForumId(f.getId());
		
		if (grade != null)
		{
			//remove entry from gradebook
			removeEntryFromGradeBook(grade);
			
			DataAccessDriver.getInstance().newGradeDAO().delete(grade.getId());	
		}
		
		String gradebookUid = ToolManager.getInstance().getCurrentPlacement().getContext();
		
		//updated existing entries in gradebook for gradable topics for date if any
		List<Grade> grades = DataAccessDriver.getInstance().newGradeDAO().selectForumTopicGradesByForumId(f.getId());
		for (Grade topicGrade : grades)	{
			if (topicGrade.isAddToGradeBook()) {
				// remove grades from gradebook if forum access type is deny access
				if (f.getAccessType() == Forum.ACCESS_DENY)
				{
					removeEntryFromGradeBook(topicGrade);
					DataAccessDriver.getInstance().newGradeDAO().updateAddToGradeBookStatus(topicGrade.getId(), false);
					topicGrade.setAddToGradeBook(false);
					this.context.put("errorMessage", I18n.getMessage("Grade.AddEditForumDenyAccessGradesToGradebook"));
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
							// remove any existing entry in the grade book
							removeEntryFromGradeBook(topicGrade);
							// this.context.put("errorMessage", I18n.getMessage("Grade.AddEditForumNotVisibleGradesToGradebook"));
							DataAccessDriver.getInstance().newGradeDAO().updateAddToGradeBookStatus(topicGrade.getId(), false);
							continue;
						}
					}
											
					Topic topic = DataAccessDriver.getInstance().newTopicDAO().selectById(topicGrade.getTopicId());
					String title = topic.getTitle();
					
					JForumGBService jForumGBService = null;
					jForumGBService = (JForumGBService)ComponentManager.get("org.etudes.api.app.jforum.JForumGBService");
					
					// update end date
					if (jForumGBService != null)
					{
						jForumGBService.updateExternalAssessment(gradebookUid, "discussions-" + String.valueOf(topicGrade.getId()), null, 
									title, topicGrade.getPoints(), f.getEndDate());
					}
				}
			}
		}
	}
}
