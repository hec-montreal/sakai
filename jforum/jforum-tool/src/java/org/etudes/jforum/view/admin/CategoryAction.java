/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/admin/CategoryAction.java $ 
 * $Id: CategoryAction.java 71056 2010-10-29 22:24:12Z murthy@etudes.org $ 
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
import org.etudes.jforum.dao.GradeDAO;
import org.etudes.jforum.dao.generic.CourseCategoryDAO;
import org.etudes.jforum.entities.Category;
import org.etudes.jforum.entities.Evaluation;
import org.etudes.jforum.entities.Forum;
import org.etudes.jforum.entities.Grade;
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
import org.etudes.jforum.view.forum.common.ForumCommon;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.cover.UserDirectoryService;

/**
 * ViewHelper for category administration.
 * 
 * @author Rafael Steil
 */
/**
 * 8/10/05 - Mallika - adding code to delete from CourseCategory when category is deleted
 * 8/10/05 - Mallika - adding code so only categories for this course appear even on the admin panel
 * 9/13/05 - Mallika - adding code to get Facilitator group so all categories added would be accessible to them
 * 10/20/05 - Murthy - trim() added to category name in the insertSave()and editSave() methods,  
 * 						"moderated" corrected to "moderate" in the insertSave() method
 * 9/13/05 - Mallika - changed del msg to not show id
 * 1/5/06 - Mallika - added code to show warning for delete cat
 * 1/6/06 - Mallika - correcting bug in warning code, warning used to always show
 */
public class CategoryAction extends AdminCommand 
{
	private CategoryDAO cm = DataAccessDriver.getInstance().newCategoryDAO();
	private CourseCategoryDAO ccm = DataAccessDriver.getInstance().newCourseCategoryDAO();
	private GradeDAO gm = DataAccessDriver.getInstance().newGradeDAO();
	private EvaluationDAO evaldao = DataAccessDriver.getInstance().newEvaluationDAO();
	
	private static Log logger = LogFactory.getLog(CategoryAction.class);
	
	// Listing
	public void list() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}

		this.context.put("categories", ForumCommon.getAllCategoriesAndForums(true));
		
		this.context.put("repository", new ForumRepository());
		
		this.context.put("viewTitleManageCatg", true);
		
		context.put("calendarDateTimeFormat", SakaiSystemGlobals.getValue(ConfigKeys.CALENDAR_DATE_TIME_FORMAT));

		this.setTemplateName(TemplateKeys.CATEGORY_LIST);
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
		
		this.context.put("groups", new TreeGroup().getNodes());
		this.context.put("selectedList", new ArrayList());
		this.setTemplateName(TemplateKeys.CATEGORY_INSERT);
		this.context.put("action", "insertSave");
		this.context.put("viewTitleManageCatg", true);
		
		Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
		if (site.getToolForCommonId(SakaiSystemGlobals.getValue(ConfigKeys.GRADEBOOK_TOOL_ID)) != null)
		{
			this.context.put("enableGrading", Boolean.TRUE);
		}
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
		
		this.context.put("category", this.cm.selectById(this.request.getIntParameter("category_id")));
		this.context.put("grade", this.gm.selectByCategoryId(this.request.getIntParameter("category_id")));
		this.setTemplateName(TemplateKeys.CATEGORY_EDIT);
		this.context.put("action", "editSave");
		this.context.put("viewTitleManageCatg", true);
		
		int forumDatesCount = DataAccessDriver.getInstance().newForumDAO().getForumDatesCount(this.request.getIntParameter("category_id"));
		this.context.put("forumDates", ((forumDatesCount > 0) ? true : false));
		context.put("calendarDateTimeFormat", SakaiSystemGlobals.getValue(ConfigKeys.CALENDAR_DATE_TIME_FORMAT));
		
		Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
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
		
		Category c = new Category(ForumRepository.getCategory(
				this.request.getIntParameter("categories_id")), true);
		c.setName(SafeHtml.escapeJavascript(this.request.getParameter("category_name").trim()));
		c.setModerated("1".equals(this.request.getParameter("moderate")));
		c.setGradeCategory("1".equals(this.request.getParameter("grade_category")));
		
		// star and end dates
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
				this.request.addParameter("forum_id", String.valueOf(c.getId()));
				this.edit();
				return;
			}
			c.setStartDate(startDate);
			
			if (startDate != null){
				SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
				c.setStartDateFormatted(df.format(startDate));
			}
		}
		else
		{
			c.setStartDate(null);
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
				this.request.addParameter("forum_id", String.valueOf(c.getId()));
				this.edit();
				return;
			}
			c.setEndDate(endDate);
			
			String lockForum = this.request.getParameter("lock_category");
			if (lockForum!= null && "1".equals(lockForum)){
				c.setLockCategory(true);
			}
			else
			{
				c.setLockCategory(false);
			}
			
			if (endDate != null){
				SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
				c.setEndDateFormatted(df.format(endDate));
			}
		}
		else
		{
			c.setEndDate(null);
			c.setLockCategory(false);
		}
					
		if (c.isGradeCategory())
		{
			// check for category forums for grading enabled
			if (this.gm.isCategoryForumsGradable(c.getId()))
			{
				JForum.enableCancelCommit();
				this.context.put("errorMessage", I18n.getMessage("Category.Form.CannotEditGrade"));
				this.request.addParameter("category_id", String.valueOf(c.getId()));
				this.edit();
				return;
			}
			
			// update category
			this.cm.update(c);
			
			ForumRepository.reloadCourseCategory(c);
			
			new ModerationCommon().setForumsModerationStatus(c, c.isModerated());
						
			// existing grade
			Grade exisGrade = this.gm.selectByCategoryId(c.getId());
			
			if (exisGrade == null)
			{
				// create grade
				Grade grade = new Grade();
				
				grade.setContext(ToolManager.getCurrentPlacement().getContext());
				grade.setCategoryId(c.getId());
				try {
					Float points = Float.parseFloat(this.request.getParameter("point_value"));
					
					if (points.floatValue() < 0) points = Float.valueOf(0.0f);
					if (points.floatValue() > 1000) points = Float.valueOf(1000.0f);
					points = Float.valueOf(((float) Math.round(points.floatValue() * 100.0f)) / 100.0f);
					
					grade.setPoints(points);
				} catch (NumberFormatException ne) {
					grade.setPoints(0f);
				}			
				grade.setType(Forum.GRADE_BY_CATEGORY);
				
				int gradeId = this.gm.addNew(grade);
				
				String sendToGradebook = this.request.getParameter("send_to_grade_book");
				boolean addToGradeBook = false;
				if ((sendToGradebook != null) && (Integer.parseInt(sendToGradebook) == 1))
				{
					addToGradeBook = true;
				}
				grade.setAddToGradeBook(addToGradeBook);
				
				grade.setId(gradeId);
				
				//if add to grade book is true then add the grade to grade book
				if (addToGradeBook)
				{
					addToGradeBook = updateGradebook(grade);
				}
				this.gm.updateAddToGradeBookStatus(gradeId, addToGradeBook);
			}
			else
			{
				// update existing grade and update any entry in the gradebook
				exisGrade.setContext(ToolManager.getCurrentPlacement().getContext());
				exisGrade.setCategoryId(c.getId());
				try {
					Float points = Float.parseFloat(this.request.getParameter("point_value"));
					
					if (points.floatValue() < 0) points = Float.valueOf(0.0f);
					if (points.floatValue() > 1000) points = Float.valueOf(1000.0f);
					points = Float.valueOf(((float) Math.round(points.floatValue() * 100.0f)) / 100.0f);
					exisGrade.setPoints(points);
					
				} catch (NumberFormatException ne) {
					exisGrade.setPoints(0f);
				}			
				
				this.gm.updateCategoriesGrade(exisGrade);
				
				String sendToGradebook = this.request.getParameter("send_to_grade_book");
				boolean addToGradeBook = false;
				if ((sendToGradebook != null) && (Integer.parseInt(sendToGradebook) == 1))
				{
					addToGradeBook = true;
				}
								
				// update any entry in the grade book for points and any name changes.
				if (addToGradeBook)
				{
					exisGrade.setAddToGradeBook(addToGradeBook);
					addToGradeBook = updateGradebook(exisGrade);
				} 
				else
				{
					// remove any existing entry in the grade book
					removeEntryFromGradeBook(exisGrade);
				}
				exisGrade.setAddToGradeBook(addToGradeBook);
				this.gm.updateAddToGradeBookStatus(exisGrade.getId(), addToGradeBook);
			}
		}
		else
		{
			// delete any existing grade associated with category if not graded
			Grade exisGrade = this.gm.selectByCategoryId(c.getId());
			
			if (exisGrade != null)
			{
				// check for evaluations
				int evalCount = DataAccessDriver.getInstance().newEvaluationDAO().selectEvaluationsCountByGradeId(exisGrade.getId());
				
				if (evalCount > 0)
				{
					JForum.enableCancelCommit();
					this.context.put("errorMessage", I18n.getMessage("Category.Form.CannotEditGradedCategory"));
					this.request.addParameter("category_id", String.valueOf(c.getId()));
					this.edit();
					return;
				}
				else
				{
					if (exisGrade.isAddToGradeBook())
					{
						removeEntryFromGradeBook(exisGrade);
					}
					
					// delete the grade
					DataAccessDriver.getInstance().newGradeDAO().delete(exisGrade.getId());
				}
			}
			
			// update category
			this.cm.update(c);
			
			ForumRepository.reloadCourseCategory(c);
			
			new ModerationCommon().setForumsModerationStatus(c, c.isModerated());
			
		}
		
		// auto save navigation
		autoSaveNavigation();
	}
	
	public void delete() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		String ids[] = this.request.getParameterValues("categories_id");
		List errors = new ArrayList();
		StringBuffer catNameList = new StringBuffer();
		StringBuffer gradedCatNameList = new StringBuffer();
		boolean errFlag = false;
		boolean errGradedFlag = false;
		
		if (ids != null) {						
			for (int i = 0; i < ids.length; i++){
				if (this.cm.canDelete(Integer.parseInt(ids[i]))) {
					int id = Integer.parseInt(ids[i]);
					
					Category c = this.cm.selectById(id);
					
					//remove grades associated with category
					if (c.isGradeCategory())
					{
						Grade grade = this.gm.selectByCategoryId(c.getId());
						
						int evalCount = this.evaldao.selectEvaluationsCountByGradeId(grade.getId());
						
						if (evalCount > 0)
						{
							gradedCatNameList.append(c.getName());
							gradedCatNameList.append(",");
							errGradedFlag = true;
							continue;
						}
						
						//remove entry from gradebook
						if (grade.isAddToGradeBook())
						{
							removeEntryFromGradeBook(grade);
						}
						
						//delete grade
						this.gm.delete(grade.getId());
					}
					
					this.cm.delete(id);

					this.ccm.delete(id);
					
					ForumRepository.removeCourseCategory(c);
				}
				else {
					
					int id = Integer.parseInt(ids[i]);
					Category c = this.cm.selectById(id);
					catNameList.append(c.getName());
					catNameList.append(",");
					errFlag = true;
				}
			}
		}
		if (errFlag == true)
		{	
		  String catNameListStr = catNameList.toString();
		  if (catNameListStr.endsWith(","))
		  {
			catNameListStr = catNameListStr.substring(0,catNameListStr.length()-1);
		  }
		  errors.add(I18n.getMessage(I18n.CANNOT_DELETE_CATEGORY, new Object[]{catNameListStr}));
		}
		
		if (errGradedFlag == true)
		{	
		  String gradedCatNameListStr = gradedCatNameList.toString();
		  if (gradedCatNameListStr.endsWith(","))
		  {
			  gradedCatNameListStr = gradedCatNameListStr.substring(0,gradedCatNameListStr.length()-1);
		  }
		  errors.add(I18n.getMessage(I18n.CANNOT_DELETE_GRADED_CATEGORY, new Object[]{gradedCatNameListStr}));
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
		
		Category c = new Category();
		c.setName(SafeHtml.escapeJavascript(this.request.getParameter("category_name").trim()));
		c.setModerated("1".equals(this.request.getParameter("moderate")));
		c.setGradeCategory("1".equals(this.request.getParameter("grade_category")));
		
		// star and end dates
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
			c.setStartDate(startDate);
			
			if (startDate != null){
				SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
				c.setStartDateFormatted(df.format(startDate));
			}
		}
		else
		{
		  c.setStartDate(null);
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
			c.setEndDate(endDate);
			String lockCategory = this.request.getParameter("lock_category");
			if (lockCategory != null && "1".equals(lockCategory)){
				c.setLockCategory(true);
			}
			else
			{
				c.setLockCategory(false);
			}
			
			if (endDate != null){
				SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
				c.setEndDateFormatted(df.format(endDate));
			}
		}
		else
		{
		  c.setEndDate(null);
		  c.setLockCategory(false);
		}
			
		int categoryId = this.cm.addNew(c);
		c.setId(categoryId);
		
		if (c.isGradeCategory())
		{
			// create grade
			Grade grade = new Grade();
			
			grade.setContext(ToolManager.getCurrentPlacement().getContext());
			grade.setCategoryId(c.getId());
			try {
				Float points = Float.parseFloat(this.request.getParameter("point_value"));
				
				if (points.floatValue() < 0) points = Float.valueOf(0.0f);
				if (points.floatValue() > 1000) points = Float.valueOf(1000.0f);
				points = Float.valueOf(((float) Math.round(points.floatValue() * 100.0f)) / 100.0f);
				
				grade.setPoints(points);
			} catch (NumberFormatException ne) {
				grade.setPoints(0f);
			}			
			grade.setType(Forum.GRADE_BY_CATEGORY);
			
			int gradeId = this.gm.addNew(grade);
			grade.setId(gradeId);
			
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
				addToGradeBook = updateGradebook(grade);
			}
			this.gm.updateAddToGradeBookStatus(gradeId, addToGradeBook);
		}
		
		ForumRepository.addCourseCategoryToCache(c);
		
		// auto save navigation
		autoSaveNavigation();
	}
	
	public void up() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
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
		
		this.processOrdering(false);
	}
	
	private void processOrdering(boolean up) throws Exception
	{
		Category toChange = new Category(ForumRepository.getCategory(Integer.parseInt(
				this.request.getParameter("category_id"))), true);
		
		List categories = ForumRepository.getUserAllCourseCategories();
		
		int index = categories.indexOf(toChange);
		if (index == -1 || (up && index == 0) || (!up && index + 1 == categories.size())) {
			this.list();
			return;
		}
		
		if (up) {
			// Get the category which comes *before* the category we want to change
			Category otherCategory = new Category((Category)categories.get(index - 1), true);
			this.cm.setOrderUp(toChange, otherCategory);
		}
		else {
			// Get the category which comes *after* the category we want to change
			Category otherCategory = new Category((Category)categories.get(index + 1), true);
			this.cm.setOrderDown(toChange, otherCategory);
		}
		
		//ForumRepository.reloadCategory(toChange);
		ForumRepository.reloadCourseCategory(toChange);
		this.list();
	}
	
	/**
	 * Apply changes made to category list.
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
		
		if (JForum.getRequest().getParameter("deleteCategories") != null) 
		{
			this.delete();
		}
		else if (JForum.getRequest().getParameter("saveCategories") != null) 
		{
			this.saveCategoryDates();
			//this.list();
			autoSaveNavigation();
		}
	}
	
	/**
	 * save categiry start and end dates
	 */
	protected void saveCategoryDates() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		List errors = new ArrayList();
		boolean errFlag = false;
		StringBuffer categoryNameList = new StringBuffer();
		
		Enumeration<?> paramNames = this.request.getParameterNames();
		
		while (paramNames.hasMoreElements())
		{
			String paramName = (String) paramNames.nextElement();

			if (paramName.startsWith("startdate_"))
			{
				// paramName is in the format startdate_forumId
				String id[] = paramName.split("_");
				String categoryId = null;
				categoryId = id[1];
				Category existingCategory = DataAccessDriver.getInstance().newCategoryDAO().selectById(Integer.parseInt(categoryId));
				
				
				Category c = new Category(existingCategory);
				
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
						categoryNameList.append(c.getName());
						categoryNameList.append(",");
						continue;
					}
					c.setStartDate(startDate);
				}
				else
				{
				  c.setStartDate(null);
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
						categoryNameList.append(c.getName());
						categoryNameList.append(",");
						continue;
					}
					c.setEndDate(endDate);
					
					//lockforum_forumId
					String lockForum = this.request.getParameter("lockcategory_"+ id[1]);
					if (lockForum!= null && "1".equals(lockForum)){
						c.setLockCategory(true);
					}
					else
					{
						c.setLockCategory(false);
					}
				}
				else
				{
					c.setEndDate(null);
					c.setLockCategory(false);
				}
				
				// update if there are date changes
				boolean datesChanged = false;
				
				if (existingCategory.getStartDate() == null)
				{
					if (c.getStartDate() != null)
					{
						datesChanged = true;
					}
				}
				else
				{
					if (c.getStartDate() == null)
					{
						datesChanged = true;
					}
					else if (!c.getStartDate().equals(existingCategory.getStartDate()))
					{
						datesChanged = true;
					}
				}
				
				if (!datesChanged)
				{
					if (existingCategory.getEndDate() == null)
					{
						if (c.getEndDate() != null)
						{
							datesChanged = true;
						}
					}
					else
					{
						if (c.getEndDate() == null)
						{
							datesChanged = true;
						}
						else if (!c.getEndDate().equals(existingCategory.getStartDate()))
						{
							datesChanged = true;
						}
					}
				}
				
				if (datesChanged)
				{
					DataAccessDriver.getInstance().newCategoryDAO().updateDates(c);
					
					// update gradable categories with date changes in the gradebook
					if (c.isGradeCategory())
					{
						Grade catGrade = DataAccessDriver.getInstance().newGradeDAO().selectByCategoryId(c.getId());
						boolean addToGradeBook = false;
						
						if (catGrade != null && catGrade.isAddToGradeBook())
						{
							addToGradeBook = updateGradebook(catGrade);
							catGrade.setAddToGradeBook(addToGradeBook);
							DataAccessDriver.getInstance().newGradeDAO().updateAddToGradeBookStatus(catGrade.getId(), addToGradeBook);
						}
					}
				}
			}
		}		
		
		if (errFlag == true)
		{
		  String categoryNameListStr = categoryNameList.toString();
		  if (categoryNameListStr.endsWith(","))
		  {
			  categoryNameListStr = categoryNameListStr.substring(0,categoryNameListStr.length()-1);
		  }
		  errors.add(I18n.getMessage("Forums.List.CannotUpdateForumDates", new Object[]{categoryNameListStr}));
		}
		
		if (errors.size() > 0) {
			this.context.put("errorMessage", errors);
		}
	}

	
	/**
	 * update grade book
	 * @param grade		grade
	 * @param gradebookUid	gradebookUid
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
				
		if (grade.getType() != Forum.GRADE_BY_CATEGORY)
		{
			return false;
		}
		
		if (!jForumGBService.isGradebookDefined(gradebookUid))
		{
			return false;
		}
		
		Category category = DataAccessDriver.getInstance().newCategoryDAO().selectById(grade.getCategoryId());
		
		boolean entryExisInGradebook = false;
		
		if (jForumGBService.isExternalAssignmentDefined(gradebookUid, "discussions-" + String.valueOf(grade.getId())))
		{
			entryExisInGradebook = true;
		}
		
		//add or update to gradebook
		String url = null;
		
		if (entryExisInGradebook)
		{
			/*remove entry in the gradebook and add again if there is no entry with the same name in the gradebook.*/
			jForumGBService.removeExternalAssessment(gradebookUid, "discussions-" + String.valueOf(grade.getId()));
			
			
			if (jForumGBService.isAssignmentDefined(gradebookUid, category.getName()))
			{
				this.context.put("errorMessage", I18n.getMessage("Grade.AddEditCategoryGradeBookConflictingAssignmentNameException"));
				return false;
			}
			
			if (jForumGBService.addExternalAssessment(gradebookUid, "discussions-" + String.valueOf(grade.getId()), url, category.getName(), 
					grade.getPoints(), category.getEndDate(), I18n.getMessage("Grade.sendToGradebook.description")))
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
			if (jForumGBService.isAssignmentDefined(gradebookUid, category.getName()))
			{
				this.context.put("errorMessage", I18n.getMessage("Grade.AddEditCategoryGradeBookConflictingAssignmentNameException"));
				
				return false;
			}
			
			if (jForumGBService.addExternalAssessment(gradebookUid, "discussions-" + String.valueOf(grade.getId()), url, category.getName(), 
					grade.getPoints(), category.getEndDate(), I18n.getMessage("Grade.sendToGradebook.description")))
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
	 * remove entry from gradebook
	 * @param grade
	 */
	private void removeEntryFromGradeBook(Grade grade) throws Exception
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
			evaluations = DataAccessDriver.getInstance().newEvaluationDAO().selectCategoryEvaluations(grade.getCategoryId(), evalSort);
			
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
}
