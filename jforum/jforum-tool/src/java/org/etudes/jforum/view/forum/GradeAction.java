/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/forum/GradeAction.java $
 * $Id: GradeAction.java 71014 2010-10-26 20:49:34Z murthy@etudes.org $
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
 ***********************************************************************************/

package org.etudes.jforum.view.forum;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.api.app.jforum.JForumGBService;
import org.etudes.jforum.Command;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.dao.CategoryDAO;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.EvaluationDAO;
import org.etudes.jforum.dao.PostDAO;
import org.etudes.jforum.dao.TopicDAO;
import org.etudes.jforum.dao.UserDAO;
import org.etudes.jforum.dao.EvaluationDAO.EvaluationsSort;
import org.etudes.jforum.dao.generic.EvaluationUserOrderComparator;
import org.etudes.jforum.entities.Category;
import org.etudes.jforum.entities.Evaluation;
import org.etudes.jforum.entities.Forum;
import org.etudes.jforum.entities.Grade;
import org.etudes.jforum.entities.Topic;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.entities.UserSession;
import org.etudes.jforum.repository.ForumRepository;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.preferences.TemplateKeys;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.etudes.jforum.view.forum.common.AttachmentCommon;
import org.etudes.jforum.view.forum.common.PostCommon;
import org.etudes.jforum.view.forum.common.ViewCommon;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.cover.UserDirectoryService;

/**
 * @author Murthy Tanniru
 */
public class GradeAction extends Command
{

	private static Log logger = LogFactory.getLog(GradeAction.class);

	/**
	 *
	 *
	 */
	public void list() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();

		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToGrade"));
			this.setTemplateName(TemplateKeys.GRADE_EVAL_NOT_AUTHORIZED);
			return;
		}

		//this.setTemplateName(TemplateKeys.GRADE_EVAL_FORUM);
	}

	/**
	 * get forum evaluations
	 * @throws Exception
	 */
	public void evalForumList() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();

		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToGrade"));
			this.setTemplateName(TemplateKeys.GRADE_EVAL_NOT_AUTHORIZED);
			return;
		}

		//update members info
		JForumUserUtil.updateMembersInfo(false);

		int forumId = this.request.getIntParameter("forum_id");
		String sortColumn = this.request.getParameter("sort_column");
		String sortDirection = this.request.getParameter("sort_direction");

		EvaluationDAO.EvaluationsSort evalSort = EvaluationDAO.EvaluationsSort.last_name_a;

		if (sortColumn != null && sortDirection != null)
		{
			if (sortColumn.equals("name") && sortDirection.equals("a")) {
				evalSort = EvaluationDAO.EvaluationsSort.last_name_a;
			} else if (sortColumn.equals("name") && sortDirection.equals("d")) {
				evalSort = EvaluationDAO.EvaluationsSort.last_name_d;
			}else if (sortColumn.equals("posts") && sortDirection.equals("a")) {
				evalSort = EvaluationDAO.EvaluationsSort.total_posts_a;
			} else if (sortColumn.equals("posts") && sortDirection.equals("d")) {
				evalSort = EvaluationDAO.EvaluationsSort.total_posts_d;
			}else if (sortColumn.equals("scores") && sortDirection.equals("a")) {
				evalSort = EvaluationDAO.EvaluationsSort.scores_a;
			} else if (sortColumn.equals("scores") && sortDirection.equals("d")) {
				evalSort = EvaluationDAO.EvaluationsSort.scores_d;
			}else if (sortColumn.equals("grouptitle") && sortDirection.equals("a")) {
				evalSort = EvaluationDAO.EvaluationsSort.group_title_a;
			} else if (sortColumn.equals("grouptitle") && sortDirection.equals("d")) {
				evalSort = EvaluationDAO.EvaluationsSort.group_title_d;
			}

		} else {
			sortColumn = "name";
			sortDirection = "a";
		}

		Forum forum = ForumRepository.getForum(forumId);
		if (forum == null) return;

		this.context.put("forum", forum);

		Category category = ForumRepository.getCategory(forum.getCategoryId());
		this.context.put("category", category);
		this.context.put("facilitator", isfacilitator);

		Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByForumId(forum.getId());

		this.context.put("grade", grade);

		Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());

		if (site.getToolForCommonId(SakaiSystemGlobals.getValue(ConfigKeys.GRADEBOOK_TOOL_ID)) != null)
			this.context.put("addToGradebook", true);

		//get evaluations
		EvaluationDAO evaluationDao = DataAccessDriver.getInstance().newEvaluationDAO();
		List<Evaluation> evaluations = evaluationDao.selectForumEvaluations(forum.getId(), evalSort);

		// sort based on group title
		if (forum.getAccessType() == Forum.ACCESS_GROUPS)
		{
			getForumGroupUsers(forum, evaluations);

			sortEvaluationGroups(evalSort, evaluations);
		}

		this.context.put("sort_column", sortColumn);
		this.context.put("sort_direction", sortDirection);
		this.context.put("evaluations", evaluations);

		//int catSpecialAccessCount = DataAccessDriver.getInstance().newSpecialAccessDAO().selectCategorySpecialAccessCount(category.getId());
		//this.context.put("catSpecialAccessCount", catSpecialAccessCount);

		this.setTemplateName(TemplateKeys.GRADE_EVAL_FORUM);
	}


	/**
	 * get topic evaluations
	 * @throws Exception
	 */
	public void evalTopicList() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();

		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToGrade"));
			this.setTemplateName(TemplateKeys.GRADE_EVAL_NOT_AUTHORIZED);
			return;
		}

		//update memebers info
		JForumUserUtil.updateMembersInfo(false);

		int topicId = this.request.getIntParameter("topic_id");
		String sortColumn = this.request.getParameter("sort_column");
		String sortDirection = this.request.getParameter("sort_direction");

		EvaluationDAO.EvaluationsSort evalSort = EvaluationDAO.EvaluationsSort.last_name_a;

		if (sortColumn != null && sortDirection != null)
		{
			if (sortColumn.equals("name") && sortDirection.equals("a")) {
				evalSort = EvaluationDAO.EvaluationsSort.last_name_a;
			} else if (sortColumn.equals("name") && sortDirection.equals("d")) {
				evalSort = EvaluationDAO.EvaluationsSort.last_name_d;
			}else if (sortColumn.equals("posts") && sortDirection.equals("a")) {
				evalSort = EvaluationDAO.EvaluationsSort.total_posts_a;
			} else if (sortColumn.equals("posts") && sortDirection.equals("d")) {
				evalSort = EvaluationDAO.EvaluationsSort.total_posts_d;
			}else if (sortColumn.equals("scores") && sortDirection.equals("a")) {
				evalSort = EvaluationDAO.EvaluationsSort.scores_a;
			} else if (sortColumn.equals("scores") && sortDirection.equals("d")) {
				evalSort = EvaluationDAO.EvaluationsSort.scores_d;
			}
		} else {
			sortColumn = "name";
			sortDirection = "a";
		}

		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
		Topic topic = tm.selectById(topicId);

		// The topic exists?
		if (topic.getId() == 0) {
			if (logger.isErrorEnabled()) logger.error("topic " + topic + " has id=0");
			this.topicNotFound();
			return;
		}

		Forum forum = ForumRepository.getForum(topic.getForumId());
		if (forum == null) return;

		this.context.put("forum", forum);

		Category category = ForumRepository.getCategory(forum.getCategoryId());
		this.context.put("category", category);

		this.context.put("topic", topic);

		this.context.put("facilitator", isfacilitator);

		Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByForumTopicId(topic.getForumId(), topicId);

		this.context.put("grade", grade);
		Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());

		if (site.getToolForCommonId(SakaiSystemGlobals.getValue(ConfigKeys.GRADEBOOK_TOOL_ID)) != null)
			this.context.put("addToGradebook", true);

		//get evaluations
		EvaluationDAO evaluationDao = DataAccessDriver.getInstance().newEvaluationDAO();
		List<Evaluation> evaluations = evaluationDao.selectTopicEvaluations(forum.getId(), topicId, evalSort);

		if (forum.getAccessType() == Forum.ACCESS_GROUPS)
		{
			getForumGroupUsers(forum, evaluations);

			sortEvaluationGroups(evalSort, evaluations);
		}

		this.context.put("sort_column", sortColumn);
		this.context.put("sort_direction", sortDirection);
		this.context.put("evaluations", evaluations);
		this.setTemplateName(TemplateKeys.GRADE_EVAL_TOPIC);
	}

	/**
	 * get category evaluations
	 * @throws Exception
	 */
	public void evalCategoryList() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();

		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToGrade"));
			this.setTemplateName(TemplateKeys.GRADE_EVAL_NOT_AUTHORIZED);
			return;
		}

		//update memebers info
		JForumUserUtil.updateMembersInfo(false);

		int categoryId = this.request.getIntParameter("category_id");
		String sortColumn = this.request.getParameter("sort_column");
		String sortDirection = this.request.getParameter("sort_direction");

		EvaluationDAO.EvaluationsSort evalSort = EvaluationDAO.EvaluationsSort.last_name_a;

		if (sortColumn != null && sortDirection != null)
		{
			if (sortColumn.equals("name") && sortDirection.equals("a")) {
				evalSort = EvaluationDAO.EvaluationsSort.last_name_a;
			} else if (sortColumn.equals("name") && sortDirection.equals("d")) {
				evalSort = EvaluationDAO.EvaluationsSort.last_name_d;
			}else if (sortColumn.equals("posts") && sortDirection.equals("a")) {
				evalSort = EvaluationDAO.EvaluationsSort.total_posts_a;
			} else if (sortColumn.equals("posts") && sortDirection.equals("d")) {
				evalSort = EvaluationDAO.EvaluationsSort.total_posts_d;
			}else if (sortColumn.equals("scores") && sortDirection.equals("a")) {
				evalSort = EvaluationDAO.EvaluationsSort.scores_a;
			} else if (sortColumn.equals("scores") && sortDirection.equals("d")) {
				evalSort = EvaluationDAO.EvaluationsSort.scores_d;
			}
		} else {
			sortColumn = "name";
			sortDirection = "a";
		}

		Category category = ForumRepository.getCategory(categoryId);
		if (category == null) return;

		this.context.put("category", category);

		this.context.put("facilitator", isfacilitator);

		Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByCategoryId(categoryId);

		this.context.put("grade", grade);

		Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());

		if (site.getToolForCommonId(SakaiSystemGlobals.getValue(ConfigKeys.GRADEBOOK_TOOL_ID)) != null)
			this.context.put("addToGradebook", true);

		//get evaluations
		EvaluationDAO evaluationDao = DataAccessDriver.getInstance().newEvaluationDAO();
		List<Evaluation> evaluations = evaluationDao.selectCategoryEvaluations(categoryId, evalSort);

		this.context.put("sort_column", sortColumn);
		this.context.put("sort_direction", sortDirection);
		this.context.put("evaluations", evaluations);

		//int forumSpecialAccessCount = DataAccessDriver.getInstance().newSpecialAccessDAO().selectCategoryForumSpecialAccessCount(category.getId());
		//this.context.put("forumSpecialAccessCount", forumSpecialAccessCount);

		this.setTemplateName(TemplateKeys.GRADE_EVAL_CATEGORY);

	}

	/**
	 * evaluate forum
	 *
	 * @throws Exception
	 */
	public void evaluateForum() throws Exception
	{
		evaluateUsers();

		this.evalForumList();
	}

	/**
	 * evaluate topic
	 *
	 * @throws Exception
	 */
	public void evaluateTopic() throws Exception
	{
		evaluateUsers();

		this.evalTopicList();
	}


	/**
	 * evaluate category
	 *
	 * @throws Exception
	 */
	public void evaluateCategory() throws Exception
	{
		evaluateUsers();

		this.evalCategoryList();
	}

	/**
	 * evaluate users
	 * @throws Exception
	 * @throws NumberFormatException
	 */
	private void evaluateUsers() throws NumberFormatException, Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();

		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToGrade"));
			this.setTemplateName(TemplateKeys.GRADE_EVAL_NOT_AUTHORIZED);
			return;
		}
		/*adjust scores for all evaluations who have scores or if there are replies / posts for a user*/
		String adjustScores = this.request.getParameter("adjust_scores");
		Float adjustScore = null;
		if (adjustScores != null && adjustScores.trim().length() > 0) {
			try {
				adjustScore = I18n.parseFloat(adjustScores);

				if (adjustScore.floatValue() > 1000) adjustScore = Float.valueOf(1000.0f);

				adjustScore = Float.valueOf(((float) Math.round(adjustScore.floatValue() * 100.0f)) / 100.0f);
			} catch (ParseException e) {
				if (logger.isWarnEnabled()) logger.warn("evaluateUsers(): adjust scores: " + e);
			}
		}

		/*adjust comments will be added to all users/evaluations, if there are replies / posts for a user
		 * and if a user doesn't have any posts but has a score (manually added by the teacher),
		 * add/append comments*/
		String adjustComments = this.request.getParameter("adjust_comments");

		Enumeration<?> paramNames = this.request.getParameterNames();

		String sakuserId = UserDirectoryService.getCurrentUser().getId();

		int evaluatedBy = (DataAccessDriver.getInstance().newUserDAO().selectBySakaiUserId(sakuserId)).getId();

		String releaseAllEvaluated = this.request.getParameter("releaseall");
		boolean releaseEvaluatedScore = false;
		if ((releaseAllEvaluated != null) && (Integer.parseInt(releaseAllEvaluated) == 1))
		{
			releaseEvaluatedScore = true;
		}

		while (paramNames.hasMoreElements())
		{
			String paramName = (String) paramNames.nextElement();
			boolean noScore = false;

			if (paramName.endsWith("_score"))
			{
				String paramScore = this.request.getParameter(paramName);
				// paramName is in the format gradeId_evalutionId_jforumUserId_score
				String ids[] = paramName.split("_");
				String sakUserId = null;
				//sakUserId_userId
				sakUserId = this.request.getParameter("sakUserId_"+ ids[2]);

				Float score = null;
				int totalPostCount = 0;
				try
				{
					String paramTotalPosts = paramName.replace("score", "totalposts");
					String userTotalPosts = this.request.getParameter(paramTotalPosts);

					if ((userTotalPosts != null) && (userTotalPosts.trim().length() > 0)) {
						totalPostCount = Integer.parseInt(userTotalPosts.trim());
					}

					if ((paramScore != null) && (paramScore.trim().length() > 0)) {
						score = I18n.parseFloat(this.request.getParameter(paramName));

						//if (score.floatValue() < 0) score = Float.valueOf(0.0f);
						if (score.floatValue() > 1000) score = Float.valueOf(1000.0f);
						score = Float.valueOf(((float) Math.round(score.floatValue() * 100.0f)) / 100.0f);
						if (adjustScore != null && JForumUserUtil.isUserActive(sakUserId))
						{
							score = score + adjustScore;
							score = Float.valueOf(((float) Math.round(score.floatValue() * 100.0f)) / 100.0f);
						}
					} else {
						if (totalPostCount > 0 && adjustScore != null && JForumUserUtil.isUserActive(sakUserId)) {
							score = adjustScore.floatValue();
							score = Float.valueOf(((float) Math.round(score.floatValue() * 100.0f)) / 100.0f);
						}else
							noScore = true;
					}
				}
				catch (ParseException e)
				{
					if (logger.isWarnEnabled()) logger.warn("evaluateUsers() : " + e);
					continue;
				}
				String paramComments = paramName.replace("score", "comments");
				String comments = this.request.getParameter(paramComments);
				if (adjustComments != null && adjustComments.trim().length() > 0)
				{
					if ((totalPostCount > 0 || !noScore)&& JForumUserUtil.isUserActive(sakUserId)) {
						StringBuffer strBufComments = new StringBuffer();
						strBufComments.append(comments);
						strBufComments.append("\n");
						strBufComments.append(adjustComments);

						comments = strBufComments.toString();
					}
				}

				boolean releaseScore = false;
				if (releaseEvaluatedScore)
				{
					releaseScore = true;
				}
				else
				{
					String paramRelease = paramName.replace("score", "release");
					String released = this.request.getParameter(paramRelease);

					if ((released != null) && (Integer.parseInt(released) == 1))
					{
						releaseScore = true;
					}
				}

				/*
				 * create evaluation if evaluation id is -1 and has valid grade id
				 * update evaluation if evaluation is valid id and has valid grade id
				 */
				Evaluation evaluation = new Evaluation();

				if (Integer.parseInt(ids[1]) == -1)
				{
					if (noScore) {
						if (comments == null || comments.trim().length() == 0) {
							continue;
						}
					}
					evaluation.setGradeId(Integer.parseInt(ids[0]));
					evaluation.setUserId(Integer.parseInt(ids[2]));
					evaluation.setSakaiUserId(sakUserId);
					evaluation.setScore(score);
					evaluation.setComments(comments.trim());
					evaluation.setEvaluatedBy(evaluatedBy);
					evaluation.setReleased(releaseScore);

					int evaluationId = DataAccessDriver.getInstance().newEvaluationDAO().addNew(evaluation);
					evaluation.setId(evaluationId);
				}
				else if (Integer.parseInt(ids[1]) > 0)
				{
					if (noScore) {
						int evaluationId = Integer.parseInt(ids[1]);
						if (comments != null && comments.trim().length() == 0) {
							DataAccessDriver.getInstance().newEvaluationDAO().delete(evaluationId);
						} else {
							evaluation.setId(evaluationId);
							evaluation.setGradeId(Integer.parseInt(ids[0]));
							evaluation.setUserId(Integer.parseInt(ids[2]));
							evaluation.setSakaiUserId(sakUserId);
							evaluation.setScore(score);
							evaluation.setComments(comments.trim());
							evaluation.setEvaluatedBy(evaluatedBy);
							evaluation.setReleased(releaseScore);

							DataAccessDriver.getInstance().newEvaluationDAO().update(evaluation);
						}
					} else {
						evaluation.setId(Integer.parseInt(ids[1]));
						evaluation.setGradeId(Integer.parseInt(ids[0]));
						evaluation.setUserId(Integer.parseInt(ids[2]));
						evaluation.setSakaiUserId(sakUserId);
						evaluation.setScore(score);
						evaluation.setComments(comments.trim());
						evaluation.setEvaluatedBy(evaluatedBy);
						evaluation.setReleased(releaseScore);

						DataAccessDriver.getInstance().newEvaluationDAO().update(evaluation);
					}
				}
				/*if (!evaluated)
					evaluated = true;*/
			}
		}

		/*	If evaluated and checked "Send to Gradebook", update grade "add to gradebook" to true
		 *  and send the grades to grade book
		 *  If "Send to Gradebook" is unchecked, update grade "add to gradebook" to false
		 *  Remove grades from grade book if grade "add to gradebook" is true before
		 */
		String sendToGradeBook = this.request.getParameter("send_to_grade_book");
		boolean addToGradeBook = false;
		if ((sendToGradeBook != null) && (Integer.parseInt(sendToGradeBook) == 1))
		{
			addToGradeBook = true;
		}

		int gradeId = Integer.parseInt(this.request.getParameter("grade_id"));
		Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectById(gradeId);

		JForumGBService jForumGBService = null;
		jForumGBService = (JForumGBService)ComponentManager.get("org.etudes.api.app.jforum.JForumGBService");

		if (jForumGBService == null) return;

		//Check if gradebook is added to the site
		String gradebookUid = ToolManager.getInstance().getCurrentPlacement().getContext();
		boolean hasGradebook = jForumGBService.isGradebookDefined(gradebookUid);

		Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(grade.getForumId());

		if (addToGradeBook)
		{
			if (!grade.isAddToGradeBook())
			{
				if (hasGradebook) {
					if (forum.getAccessType() == Forum.ACCESS_DENY)
					{
						this.context.put("errorMessage", I18n.getMessage("Grade.CannotSentToGradebook"));
					}
					else
					{
						grade.setAddToGradeBook(true);
						DataAccessDriver.getInstance().newGradeDAO().updateAddToGradeBookStatus(grade.getId(), true);

						updateGradebook(grade);
					}
				}
			}
			else
			{
				if (hasGradebook)
				{
					if (forum.getAccessType() == Forum.ACCESS_DENY)
					{
						this.context.put("errorMessage", I18n.getMessage("Grade.CannotSentToGradebook"));
					}
					else
					{
						updateGradebook(grade);

					}
				}

			}
		} else {
			if (grade.isAddToGradeBook())
			{
				grade.setAddToGradeBook(false);
				DataAccessDriver.getInstance().newGradeDAO().updateAddToGradeBookStatus(grade.getId(), false);

				//remove from gradebook if existing
				boolean existing = false;

				existing = jForumGBService.isExternalAssignmentDefined(gradebookUid, "discussions-" + String.valueOf(grade.getId()));


				if (existing)
				{
					jForumGBService.removeExternalAssessment(gradebookUid, "discussions-" + String.valueOf(grade.getId()));
				}
			}
		}
	}


	/**
	 * update grade book
	 * @param grade grade
	 * @throws Exception
	 */
	private void updateGradebook(Grade grade) throws Exception
	{
		String gradebookUid = ToolManager.getInstance().getCurrentPlacement().getContext();
		List<Evaluation> evaluations = null;

		JForumGBService jForumGBService = null;
		jForumGBService = (JForumGBService)ComponentManager.get("org.etudes.api.app.jforum.JForumGBService");

		if (jForumGBService == null)
			return;

		// remove from gradebook and add again
		removeEntryFromGradeBook(grade);

		if ((grade.getType() == Forum.GRADE_BY_FORUM) || (grade.getType() == Forum.GRADE_BY_TOPIC))
		{

			Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(grade.getForumId());

			if (forum.getAccessType() == Forum.ACCESS_DENY)
			{
				return;
			}

			if (forum.getStartDate() != null)
			{
				Calendar calendar = Calendar.getInstance();

				Date startDate = forum.getStartDate();

				Date nowDate = calendar.getTime();

				if (nowDate.before(startDate))
				{
					this.context.put("errorMessage", I18n.getMessage("Grade.CannotSentToGradebookForumLaterStartDate"));
					DataAccessDriver.getInstance().newGradeDAO().updateAddToGradeBookStatus(grade.getId(), false);
					grade.setAddToGradeBook(false);
					return;
				}
			}

			//add to gradebook
			String url = null;
			if (grade.getType() == Forum.GRADE_BY_FORUM)
			{

				if (!jForumGBService.isExternalAssignmentDefined(gradebookUid, "discussions-" + String.valueOf(grade.getId())))
				{
					if (jForumGBService.isAssignmentDefined(gradebookUid, forum.getName()))
					{
						this.context.put("errorMessage", I18n.getMessage("Grade.GradeBookConflictingAssignmentNameException"));
						DataAccessDriver.getInstance().newGradeDAO().updateAddToGradeBookStatus(grade.getId(), false);
						grade.setAddToGradeBook(false);
						return;
					}

					if (!jForumGBService.addExternalAssessment(gradebookUid, "discussions-" + String.valueOf(grade.getId()), url, forum.getName(), grade
							.getPoints(), forum.getEndDate(), I18n.getMessage("Grade.sendToGradebook.description")))
					{
						return;
					}
				}

				EvaluationDAO.EvaluationsSort evalSort = EvaluationDAO.EvaluationsSort.last_name_a;

				evaluations = DataAccessDriver.getInstance().newEvaluationDAO().selectForumEvaluations(grade.getForumId(), evalSort);
			}
			else if (grade.getType() == Forum.GRADE_BY_TOPIC)
			{
				Topic topic = DataAccessDriver.getInstance().newTopicDAO().selectById(grade.getTopicId());
				String title = topic.getTitle();

				if (!jForumGBService.isExternalAssignmentDefined(gradebookUid, "discussions-" + String.valueOf(grade.getId())))
				{
					if (jForumGBService.isAssignmentDefined(gradebookUid, title))
					{
						this.context.put("errorMessage", I18n.getMessage("Grade.GradeBookConflictingAssignmentNameException"));
						DataAccessDriver.getInstance().newGradeDAO().updateAddToGradeBookStatus(grade.getId(), false);
						grade.setAddToGradeBook(false);
						return;
					}

					//String title = ((topic.getTitle().length() < 50)? topic.getTitle(): topic.getTitle().substring(0,50)+ "...");
					if (!jForumGBService.addExternalAssessment(gradebookUid, "discussions-" + String.valueOf(grade.getId()), url, title, grade
							.getPoints(), forum.getEndDate(), I18n.getMessage("Grade.sendToGradebook.description")))
					{
						return;
					}
				}

				EvaluationDAO.EvaluationsSort evalSort = EvaluationDAO.EvaluationsSort.last_name_a;

				evaluations = DataAccessDriver.getInstance().newEvaluationDAO().selectTopicEvaluations(topic.getForumId(), topic.getId(), evalSort);
			}
		}
		else if (grade.getType() == Forum.GRADE_BY_CATEGORY)
		{
			String url = null;
			Category category = DataAccessDriver.getInstance().newCategoryDAO().selectById(grade.getCategoryId());

			if (!jForumGBService.isExternalAssignmentDefined(gradebookUid, "discussions-" + String.valueOf(grade.getId())))
			{
				if (jForumGBService.isAssignmentDefined(gradebookUid, category.getName()))
				{
					this.context.put("errorMessage", I18n.getMessage("Grade.GradeBookConflictingAssignmentNameException"));
					DataAccessDriver.getInstance().newGradeDAO().updateAddToGradeBookStatus(grade.getId(), false);
					grade.setAddToGradeBook(false);
					return;
				}

				if (!jForumGBService.addExternalAssessment(gradebookUid, "discussions-" + String.valueOf(grade.getId()), url, category.getName(), grade
						.getPoints(), category.getEndDate(), I18n.getMessage("Grade.sendToGradebook.description")))
				{
					return;
				}
			}

			EvaluationDAO.EvaluationsSort evalSort = EvaluationDAO.EvaluationsSort.last_name_a;

			evaluations = DataAccessDriver.getInstance().newEvaluationDAO().selectCategoryEvaluations(grade.getCategoryId(), evalSort);
		}

		//points double datatype for gradebook
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

		//remove and update scores
		jForumGBService.updateExternalAssessmentScores(gradebookUid, "discussions-"+ String.valueOf(grade.getId()), scores);
	}



	/**
	 * update grade book
	 * @param grade grade
	 * @param gradebookService gradebook service
	 * @param gradebookUid gradebook uid
	 * @param evaluation evaluation
	 * @throws Exception
	 */
	private void updateGradebookForUser(Grade grade, Evaluation evaluation) throws Exception
	{
		if (!grade.isAddToGradeBook()) return;

		String gradebookUid = ToolManager.getInstance().getCurrentPlacement().getContext();

		JForumGBService jForumGBService = null;
		jForumGBService = (JForumGBService)ComponentManager.get("org.etudes.api.app.jforum.JForumGBService");

		if (jForumGBService == null)
			return;

		Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(grade.getForumId());

		if (forum.getAccessType() == Forum.ACCESS_DENY)
		{
			if (jForumGBService.isExternalAssignmentDefined(gradebookUid, "discussions-" + String.valueOf(grade.getId())))
			{
				jForumGBService.removeExternalAssessment(gradebookUid, "discussions-" + String.valueOf(grade.getId()));
				DataAccessDriver.getInstance().newGradeDAO().updateAddToGradeBookStatus(grade.getId(), false);
				grade.setAddToGradeBook(false);
			}

			return;
		}

		if ((evaluation.isReleased()) && evaluation.getScore() != null)
			jForumGBService.updateExternalAssessmentScore(gradebookUid, "discussions-"+ String.valueOf(grade.getId()), evaluation.getSakaiUserId(), Double.valueOf(evaluation.getScore()));
		else
			jForumGBService.updateExternalAssessmentScore(gradebookUid, "discussions-"+ String.valueOf(grade.getId()), evaluation.getSakaiUserId(), null);
	}


	/**
	 * show user forum evaluation
	 *
	 * @throws Exception
	 */
	public void showEvalForumUser() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		if (!isfacilitator) {
			this.context.put("errorMessage", I18n.getMessage("User.NotAuthorizedToGrade"));
			this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED_POP_UP);
			return;
		}

		int forumId = this.request.getIntParameter("forum_id");
		int userId = this.request.getIntParameter("user_id");

		Forum forum = ForumRepository.getForum(forumId);
		if (forum == null) return;

		this.context.put("forum", forum);
		this.context.put("userId", userId);

		User user = DataAccessDriver.getInstance().newUserDAO().selectById(userId);
		this.context.put("user", user);
		if (!JForumUserUtil.isUserActive(user.getSakaiUserId()))
		{
			this.context.put("message", I18n.getMessage("Evaluation.userInactive"));
			this.setTemplateName(TemplateKeys.GRADE_EVAL_NOT_AUTHORIZED);
			return;
		}

		Evaluation evaluation = DataAccessDriver.getInstance().newEvaluationDAO().selectEvaluationByForumIdUserId(forumId, userId);

		if (evaluation !=  null)
			this.context.put("evaluation", evaluation);
		else
			this.context.put("evaluation", new Evaluation());


		Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByForumId(forum.getId());
		this.context.put("grade", grade);

		Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());

		if (site.getToolForCommonId(SakaiSystemGlobals.getValue(ConfigKeys.GRADEBOOK_TOOL_ID)) != null)
			this.context.put("addToGradebook", true);

		this.context.put("pageTitle", SystemGlobals.getValue(ConfigKeys.FORUM_NAME) + " - " + I18n.getMessage("Grade.gradeForumUser"));

		this.setTemplateName(TemplateKeys.GRADE_EVAL_FORUM_USER);
	}


	/**
	 * show user forum evaluation
	 *
	 * @throws Exception
	 */
	public void showEvalTopicUser() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		if (!isfacilitator) {
			this.context.put("errorMessage", I18n.getMessage("User.NotAuthorizedToGrade"));
			this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED_POP_UP);
			return;
		}

		int topicId = this.request.getIntParameter("topic_id");
		int userId = this.request.getIntParameter("user_id");

		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
		Topic topic = tm.selectById(topicId);

		this.context.put("topic", topic);
		this.context.put("userId", userId);

		User user = DataAccessDriver.getInstance().newUserDAO().selectById(userId);
		this.context.put("user", user);
		if (!JForumUserUtil.isUserActive(user.getSakaiUserId()))
		{
			this.context.put("message", I18n.getMessage("Evaluation.userInactive"));
			this.setTemplateName(TemplateKeys.GRADE_EVAL_NOT_AUTHORIZED);
			return;
		}

		Forum forum = ForumRepository.getForum(topic.getForumId());
		this.context.put("forum", forum);

		Evaluation evaluation = DataAccessDriver.getInstance().newEvaluationDAO().selectEvaluationByForumIdTopicIdUserId(topic.getForumId(), topicId, userId);

		if (evaluation !=  null)
			this.context.put("evaluation", evaluation);
		else
			this.context.put("evaluation", new Evaluation());


		Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByForumTopicId(topic.getForumId(), topicId);
		this.context.put("grade", grade);

		Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());

		if (site.getToolForCommonId(SakaiSystemGlobals.getValue(ConfigKeys.GRADEBOOK_TOOL_ID)) != null)
			this.context.put("addToGradebook", true);

		this.context.put("pageTitle", SystemGlobals.getValue(ConfigKeys.FORUM_NAME) + " - " + I18n.getMessage("Grade.gradeTopicUser"));

		this.setTemplateName(TemplateKeys.GRADE_EVAL_TOPIC_USER);
	}

	/**
	 * show user forum evaluation
	 *
	 * @throws Exception
	 */
	public void showEvalCategoryUser() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		if (!isfacilitator) {
			this.context.put("errorMessage", I18n.getMessage("User.NotAuthorizedToGrade"));
			this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED_POP_UP);
			return;
		}

		int categoryId = this.request.getIntParameter("category_id");
		int userId = this.request.getIntParameter("user_id");

		CategoryDAO cm = DataAccessDriver.getInstance().newCategoryDAO();
		Category category = cm.selectById(categoryId);

		this.context.put("category", category);
		this.context.put("userId", userId);

		User user = DataAccessDriver.getInstance().newUserDAO().selectById(userId);
		this.context.put("user", user);
		if (!JForumUserUtil.isUserActive(user.getSakaiUserId()))
		{
			this.context.put("message", I18n.getMessage("Evaluation.userInactive"));
			this.setTemplateName(TemplateKeys.GRADE_EVAL_NOT_AUTHORIZED);
			return;
		}

		Evaluation evaluation = DataAccessDriver.getInstance().newEvaluationDAO().selectEvaluationByCategoryIdUserId(categoryId, userId);

		if (evaluation !=  null)
			this.context.put("evaluation", evaluation);
		else
			this.context.put("evaluation", new Evaluation());


		Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByCategoryId(categoryId);
		this.context.put("grade", grade);

		Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());

		if (site.getToolForCommonId(SakaiSystemGlobals.getValue(ConfigKeys.GRADEBOOK_TOOL_ID)) != null)
			this.context.put("addToGradebook", true);

		this.context.put("pageTitle", SystemGlobals.getValue(ConfigKeys.FORUM_NAME) + " - " + I18n.getMessage("Grade.gradeCategoryUser"));

		this.setTemplateName(TemplateKeys.GRADE_EVAL_CATEGORY_USER);
	}


	/**
	 * evaluate user forum
	 *
	 * @throws Exception
	 */
	public void evaluateForumUser() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();

		if (!isfacilitator) {
			this.context.put("errorMessage", I18n.getMessage("User.NotAuthorizedToGrade"));
			this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED_POP_UP);
			return;
		}

		int forumId = this.request.getIntParameter("forum_id");
		int userId = this.request.getIntParameter("user_id");

		Forum forum = ForumRepository.getForum(forumId);
		if (forum == null) return;

		this.context.put("forum", forum);
		this.context.put("userId", userId);
		String release = this.request.getParameter("release");
		String sendToGradeBook = this.request.getParameter("send_to_grade_book");
		boolean releaseGrade = false, evalSuccess = false;

		if ((release != null) && (Integer.parseInt(release) == 1))
		{
			releaseGrade = true;
		}

		String sakuserId = UserDirectoryService.getCurrentUser().getId();
		int evaluatedBy = (DataAccessDriver.getInstance().newUserDAO().selectBySakaiUserId(sakuserId)).getId();

		Evaluation evaluation = DataAccessDriver.getInstance().newEvaluationDAO().selectEvaluationByForumIdUserId(forumId, userId);

		if (evaluation ==  null)
		{
			Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByForumId(forum.getId());
			evaluation = new Evaluation();
			evaluation.setGradeId(grade.getId());
			evaluation.setUserId(userId);
			evaluation.setReleased(releaseGrade);
			String userSakaiUserId = (DataAccessDriver.getInstance().newUserDAO().selectById(userId)).getSakaiUserId();
			evaluation.setSakaiUserId(userSakaiUserId);

			Float score = null;
			boolean evaluated = false;
			try
			{
				if ((this.request.getParameter("score") != null) &&
						(this.request.getParameter("score").trim().length() > 0))
				{
					score = I18n.parseFloat(this.request.getParameter("score"));
					//if (score.floatValue() < 0) score = Float.valueOf(0.0f);
					if (score.floatValue() > 1000) score = Float.valueOf(1000.0f);
					score = Float.valueOf(((float) Math.round(score.floatValue() * 100.0f)) / 100.0f);

					evaluated = true;
				}
				else
				{
					String comments = this.request.getParameter("comments");
					if (comments != null && comments.trim().length() > 0) {
						evaluated = true;
					}
				}

				if (evaluated)
				{
					evaluation.setReleased(releaseGrade);
					evaluation.setScore(score);
					evaluation.setComments(this.request.getParameter("comments"));
					evaluation.setEvaluatedBy(evaluatedBy);

					int evaluationId = DataAccessDriver.getInstance().newEvaluationDAO().addNew(evaluation);
					evaluation.setId(evaluationId);

					evalSuccess = true;
				}
			}
			catch (ParseException e)
			{
				if (logger.isWarnEnabled()) logger.warn("evaluateForum() : " + e);
			}
		}
		else
		{
			Float score = null;
			boolean evaluated = false;
			try
			{
				if ((this.request.getParameter("score") != null) &&
						(this.request.getParameter("score").trim().length() > 0)) {
					score = I18n.parseFloat(this.request.getParameter("score"));
					//if (score.floatValue() < 0) score = Float.valueOf(0.0f);
					if (score.floatValue() > 1000) score = Float.valueOf(1000.0f);
					score = Float.valueOf(((float) Math.round(score.floatValue() * 100.0f)) / 100.0f);

					evaluated = true;
				}
				else
				{
					String comments = this.request.getParameter("comments");
					if (comments != null && comments.trim().length() > 0) {
						evaluated = true;
					}
				}

				if (evaluated)
				{
					evaluation.setReleased(releaseGrade);
					evaluation.setScore(score);
					evaluation.setComments(this.request.getParameter("comments"));
					evaluation.setEvaluatedBy(evaluatedBy);

					DataAccessDriver.getInstance().newEvaluationDAO().update(evaluation);

					evalSuccess = true;
				}
			}
			catch (ParseException e)
			{
				if (logger.isWarnEnabled()) logger.warn("evaluateForum() : " + e);
			}
		}

		//send grade to gradebook
		if (evalSuccess) {
			Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByForumId(forumId);

			if (forum.getAccessType() == Forum.ACCESS_DENY)
			{
				this.context.put("errorMessage", I18n.getMessage("Grade.CannotSentToGradebook"));
			}
			else
			{
				sendToGradebook(evaluation, grade);
			}
		}

		this.context.put("updatesucess", true);

		this.showEvalForumUser();
	}

	/**
	 * @param evaluation
	 * @param grade
	 * @throws Exception
	 */
	private void sendToGradebook(Evaluation evaluation, Grade grade) throws Exception
	{
		if (!grade.isAddToGradeBook()) return;

		String gradebookUid = ToolManager.getInstance().getCurrentPlacement().getContext();

		JForumGBService jForumGBService = null;
		jForumGBService = (JForumGBService)ComponentManager.get("org.etudes.api.app.jforum.JForumGBService");

		if (jForumGBService == null)
			return;

		updateGradebookForUser(grade, evaluation);
	}

	/**
	 * evaluate user topic
	 * @throws Exception
	 */
	public void evaluateTopicUser() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();

		if (!isfacilitator) {
			this.context.put("errorMessage", I18n.getMessage("User.NotAuthorizedToGrade"));
			this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED_POP_UP);
			return;
		}

		int topicId = this.request.getIntParameter("topic_id");
		int userId = this.request.getIntParameter("user_id");
		String release = this.request.getParameter("release");
		boolean releaseGrade = false, evalSuccess = false;

		if ((release != null) && (Integer.parseInt(release) == 1))
		{
			releaseGrade = true;
		}

		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
		Topic topic = tm.selectById(topicId);

		this.context.put("topic", topic);
		this.context.put("userId", userId);

		Forum forum = ForumRepository.getForum(topic.getForumId());
		this.context.put("forum", forum);

		Evaluation evaluation = DataAccessDriver.getInstance().newEvaluationDAO().selectEvaluationByForumIdTopicIdUserId(topic.getForumId(), topicId, userId);

		String sakuserId = UserDirectoryService.getCurrentUser().getId();
		int evaluatedBy = (DataAccessDriver.getInstance().newUserDAO().selectBySakaiUserId(sakuserId)).getId();

		if (evaluation ==  null)
		{
			Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByForumTopicId(topic.getForumId(), topicId);
			evaluation = new Evaluation();
			evaluation.setGradeId(grade.getId());
			evaluation.setUserId(userId);
			evaluation.setReleased(releaseGrade);
			String userSakaiUserId = (DataAccessDriver.getInstance().newUserDAO().selectById(userId)).getSakaiUserId();
			evaluation.setSakaiUserId(userSakaiUserId);

			Float score = null;
			boolean evaluated = false;

			try
			{
				if ((this.request.getParameter("score") != null) &&
						(this.request.getParameter("score").trim().length() > 0))
				{
					score = I18n.parseFloat(this.request.getParameter("score"));
					//if (score.floatValue() < 0) score = Float.valueOf(0.0f);
					if (score.floatValue() > 1000) score = Float.valueOf(1000.0f);
					score = Float.valueOf(((float) Math.round(score.floatValue() * 100.0f)) / 100.0f);

					evaluated = true;
				}
				else
				{
					String comments = this.request.getParameter("comments");
					if (comments != null && comments.trim().length() > 0) {
						evaluated = true;
					}
				}

				if (evaluated)
				{
					evaluation.setScore(score);
					evaluation.setComments(this.request.getParameter("comments"));
					evaluation.setEvaluatedBy(evaluatedBy);

					int evaluationId = DataAccessDriver.getInstance().newEvaluationDAO().addNew(evaluation);
					evaluation.setId(evaluationId);

					evalSuccess = true;
				}
			}
			catch (ParseException e)
			{
				if (logger.isWarnEnabled()) logger.warn("evaluateTopic() : " + e);
			}
		}
		else
		{
			Float score = null;
			boolean evaluated = false;
			try
			{
				if ((this.request.getParameter("score") != null) &&
						(this.request.getParameter("score").trim().length() > 0)) {
					score = I18n.parseFloat(this.request.getParameter("score"));
					//if (score.floatValue() < 0) score = Float.valueOf(0.0f);
					if (score.floatValue() > 1000) score = Float.valueOf(1000.0f);
					score = Float.valueOf(((float) Math.round(score.floatValue() * 100.0f)) / 100.0f);

					evaluated = true;
				}
				else
				{
					String comments = this.request.getParameter("comments");
					if (comments != null && comments.trim().length() > 0) {
						evaluated = true;
					}
				}

				if (evaluated)
				{
					evaluation.setReleased(releaseGrade);
					evaluation.setScore(score);
					evaluation.setComments(this.request.getParameter("comments"));
					evaluation.setEvaluatedBy(evaluatedBy);

					DataAccessDriver.getInstance().newEvaluationDAO().update(evaluation);

					evalSuccess = true;
				}
			}
			catch (ParseException e)
			{
				if (logger.isWarnEnabled()) logger.warn("evaluateForum() : " + e);
			}
		}

		//send grade to gradebook
		if (evalSuccess) {
			Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByForumTopicId(topic.getForumId(), topic.getId());

			if (forum.getAccessType() == Forum.ACCESS_DENY)
			{
				this.context.put("errorMessage", I18n.getMessage("Grade.CannotSentToGradebook"));
			}
			else
			{
				sendToGradebook(evaluation, grade);
			}
		}

		this.context.put("updatesucess", true);

		this.showEvalTopicUser();
	}

	/**
	 * evaluate user category
	 * @throws Exception
	 */
	public void evaluateCategoryUser() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();

		if (!isfacilitator) {
			this.context.put("errorMessage", I18n.getMessage("User.NotAuthorizedToGrade"));
			this.setTemplateName(TemplateKeys.USER_NOT_AUTHORIZED_POP_UP);
			return;
		}

		int categoryId = this.request.getIntParameter("category_id");
		int userId = this.request.getIntParameter("user_id");
		String release = this.request.getParameter("release");
		boolean releaseGrade = false, evalSuccess = false;

		if ((release != null) && (Integer.parseInt(release) == 1))
		{
			releaseGrade = true;
		}

		CategoryDAO cm = DataAccessDriver.getInstance().newCategoryDAO();
		Category category = cm.selectById(categoryId);

		this.context.put("category", category);
		this.context.put("userId", userId);

		//Forum forum = ForumRepository.getForum(topic.getForumId());
		//this.context.put("forum", forum);

		Evaluation evaluation = DataAccessDriver.getInstance().newEvaluationDAO().selectEvaluationByCategoryIdUserId(categoryId, userId);

		String sakuserId = UserDirectoryService.getCurrentUser().getId();
		int evaluatedBy = (DataAccessDriver.getInstance().newUserDAO().selectBySakaiUserId(sakuserId)).getId();

		if (evaluation ==  null)
		{
			Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByCategoryId(categoryId);
			evaluation = new Evaluation();
			evaluation.setGradeId(grade.getId());
			evaluation.setUserId(userId);
			evaluation.setReleased(releaseGrade);
			String userSakaiUserId = (DataAccessDriver.getInstance().newUserDAO().selectById(userId)).getSakaiUserId();
			evaluation.setSakaiUserId(userSakaiUserId);

			Float score = null;
			boolean evaluated = false;

			try
			{
				if ((this.request.getParameter("score") != null) &&
						(this.request.getParameter("score").trim().length() > 0))
				{
					score = I18n.parseFloat(this.request.getParameter("score"));
					//if (score.floatValue() < 0) score = Float.valueOf(0.0f);
					if (score.floatValue() > 1000) score = Float.valueOf(1000.0f);
					score = Float.valueOf(((float) Math.round(score.floatValue() * 100.0f)) / 100.0f);

					evaluated = true;
				}
				else
				{
					String comments = this.request.getParameter("comments");
					if (comments != null && comments.trim().length() > 0) {
						evaluated = true;
					}
				}

				if (evaluated)
				{
					evaluation.setScore(score);
					evaluation.setComments(this.request.getParameter("comments"));
					evaluation.setEvaluatedBy(evaluatedBy);

					int evaluationId = DataAccessDriver.getInstance().newEvaluationDAO().addNew(evaluation);
					evaluation.setId(evaluationId);

					evalSuccess = true;
				}
			}
			catch (ParseException e)
			{
				if (logger.isWarnEnabled()) logger.warn("evaluateTopic() : " + e);
			}
		}
		else
		{
			Float score = null;
			boolean evaluated = false;
			try
			{
				if ((this.request.getParameter("score") != null) &&
						(this.request.getParameter("score").trim().length() > 0)) {
					score = I18n.parseFloat(this.request.getParameter("score"));
					//if (score.floatValue() < 0) score = Float.valueOf(0.0f);
					if (score.floatValue() > 1000) score = Float.valueOf(1000.0f);
					score = Float.valueOf(((float) Math.round(score.floatValue() * 100.0f)) / 100.0f);

					evaluated = true;
				}
				else
				{
					String comments = this.request.getParameter("comments");
					if (comments != null && comments.trim().length() > 0) {
						evaluated = true;
					}
				}

				if (evaluated)
				{
					evaluation.setReleased(releaseGrade);
					evaluation.setScore(score);
					evaluation.setComments(this.request.getParameter("comments"));
					evaluation.setEvaluatedBy(evaluatedBy);

					DataAccessDriver.getInstance().newEvaluationDAO().update(evaluation);

					evalSuccess = true;
				}
			}
			catch (ParseException e)
			{
				if (logger.isWarnEnabled()) logger.warn("evaluateForum() : " + e);
			}
		}

		//send grade to gradebook
		if (evalSuccess) {
			Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByCategoryId(categoryId);

			if (grade.isAddToGradeBook())
			{
				sendToGradebook(evaluation, grade);
			}
		}

		this.context.put("updatesucess", true);

		this.showEvalCategoryUser();
	}

	/**
	 * view forum grade
	 *
	 * @throws Exception
	 */
	public void viewForumGrade() throws Exception
	{
		int forumId = this.request.getIntParameter("forum_id");

		Forum forum = ForumRepository.getForum(forumId);

		if (forum == null) return;
		this.context.put("forum", forum);

		Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByForumId(forum.getId());
		this.context.put("grade", grade);

		String sakuserId = UserDirectoryService.getCurrentUser().getId();
		int currentUserId = (DataAccessDriver.getInstance().newUserDAO().selectBySakaiUserId(sakuserId)).getId();

		Evaluation evaluation = DataAccessDriver.getInstance().newEvaluationDAO().selectEvaluationByForumIdUserId(forumId, currentUserId);
		this.context.put("evaluation", evaluation);

		if (evaluation == null || !evaluation.isReleased()) {
			this.setTemplateName(TemplateKeys.EVALUATION_NOT_AVAILABLE);
			this.context.put("message", I18n.getMessage("Evaluation.forum.notDone",
					new String[] { this.request.getContextPath() + "/forums/list"
									+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) }));
		} else {
			this.setTemplateName(TemplateKeys.GRADE_VIEW_FORUM);
			this.context.put("message", I18n.getMessage("Evaluation.done",
					new String[] { this.request.getContextPath() + "/forums/list"
									+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) }));
		}
	}


	/**
	 * view topic grade
	 * @throws Exception
	 */
	public void viewTopicGrade() throws Exception
	{
		int topicId = this.request.getIntParameter("topic_id");

		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
		Topic topic = tm.selectById(topicId);

		Forum forum = ForumRepository.getForum(topic.getForumId());

		if (forum == null) return;
		this.context.put("forum", forum);

		this.context.put("topic", topic);

		Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByForumTopicId(topic.getForumId(), topicId);
		this.context.put("grade", grade);

		String sakuserId = UserDirectoryService.getCurrentUser().getId();
		int currentUserId = (DataAccessDriver.getInstance().newUserDAO().selectBySakaiUserId(sakuserId)).getId();

		Evaluation evaluation = DataAccessDriver.getInstance().newEvaluationDAO().selectEvaluationByForumIdTopicIdUserId(topic.getForumId(), topicId, currentUserId);
		this.context.put("evaluation", evaluation);

		if (evaluation == null || !evaluation.isReleased()) {
			this.setTemplateName(TemplateKeys.EVALUATION_NOT_AVAILABLE);
			this.context.put("message", I18n.getMessage("Evaluation.topic.notDone",
					new String[] { this.request.getContextPath() + "/forums/show/"+ forum.getId()
									+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) }));
		} else {
			this.setTemplateName(TemplateKeys.GRADE_VIEW_TOPIC);
			this.context.put("message", I18n.getMessage("Evaluation.done",
					new String[] { this.request.getContextPath() + "/forums/show/"+ forum.getId()
									+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) }));
		}
	}

	/**
	 * view category grade
	 *
	 * @throws Exception
	 */
	public void viewCategoryGrade() throws Exception
	{
		int categoryId = this.request.getIntParameter("category_id");

		Category category = ForumRepository.getCategory(categoryId);

		if (category == null) return;
		this.context.put("category", category);

		Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByCategoryId(categoryId);
		this.context.put("grade", grade);

		String sakuserId = UserDirectoryService.getCurrentUser().getId();
		int currentUserId = (DataAccessDriver.getInstance().newUserDAO().selectBySakaiUserId(sakuserId)).getId();

		Evaluation evaluation = DataAccessDriver.getInstance().newEvaluationDAO().selectEvaluationByCategoryIdUserId(categoryId, currentUserId);
		this.context.put("evaluation", evaluation);

		if (evaluation == null || !evaluation.isReleased()) {
			this.setTemplateName(TemplateKeys.EVALUATION_NOT_AVAILABLE);
			this.context.put("message", I18n.getMessage("Evaluation.category.notDone",
					new String[] { this.request.getContextPath() + "/forums/list"
									+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) }));
		} else {
			this.setTemplateName(TemplateKeys.GRADE_VIEW_CATEGORY);
			this.context.put("message", I18n.getMessage("Evaluation.done",
					new String[] { this.request.getContextPath() + "/forums/list"
									+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) }));
		}
	}

	/**
	 * show user replies for the forum topics
	 */
	public void showUserForumReplies() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();

		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToGrade"));
			this.setTemplateName(TemplateKeys.GRADE_EVAL_NOT_AUTHORIZED);
			return;
		}

		int forumId = this.request.getIntParameter("forum_id");
		int userId = this.request.getIntParameter("user_id");

		PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();

		// Shall we proceed?
		if (!SessionFacade.isLogged()) {
			Forum f = ForumRepository.getForum(forumId);

			if (f == null || !ForumRepository.isForumAccessibleToUser(f)) {
				this.setTemplateName(ViewCommon.contextToLogin());
				return;
			}
		}
		//pagination
		Forum forum = ForumRepository.getForum(forumId);
		EvaluationDAO evaluationDao = DataAccessDriver.getInstance().newEvaluationDAO();
		List<Evaluation> evaluations = evaluationDao.selectForumEvaluationsWithPosts(forum.getId());
		Collections.sort(evaluations, new EvaluationUserOrderComparator());

		int start = 0;
		String s = this.request.getParameter("start");
		if (s == null || s.trim().equals("")) {
			int indexCount = 0;
			boolean foundIndex = false;
		    for(Evaluation presEval: evaluations) {
		    	indexCount++;
		    	if (presEval.getUserId() == userId) {
		    		//start = evaluations.indexOf(presEval);
		    		indexCount--;
		    		foundIndex = true;
		    		break;
		    	}
		    }
		    if (foundIndex)
		    	start = indexCount;

			this.context.put("presUserId", userId);
		} else {
			start = ViewCommon.getStartPage();
			Evaluation eval = evaluations.get(start);
			this.context.put("presUserId", eval.getUserId());
			userId = eval.getUserId();
		}
		ViewCommon.contextToPagination(start, evaluations.size(), 1);

		boolean canEdit = false;
		if (isfacilitator)
			canEdit = true;

		Map usersMap = new HashMap();
		List helperList = PostCommon.forumPosts(pm, um, usersMap, canEdit, userId, forumId);

		this.context.put("attachmentsEnabled", true);
		this.context.put("canDownloadAttachments", true);
		this.context.put("am", new AttachmentCommon(this.request, forumId));
		this.context.put("posts", helperList);
		this.context.put("forum", ForumRepository.getForum(forumId));
		this.context.put("users", usersMap);

		User u = (User)usersMap.get(userId);
		if (!JForumUserUtil.isUserActive(u.getSakaiUserId()))
		{
			this.context.put("message", I18n.getMessage("Evaluation.userInactive"));
			this.setTemplateName(TemplateKeys.GRADE_EVAL_NOT_AUTHORIZED);
			return;
		}

		this.setTemplateName(TemplateKeys.GRADE_VIEW_USER_FORUM_REPLIES);
	}


	/**
	 * show user replies for the forum topics
	 */
	public void showUserTopicReplies() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();

		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToGrade"));
			this.setTemplateName(TemplateKeys.GRADE_EVAL_NOT_AUTHORIZED);
			return;
		}

		int topicId = this.request.getIntParameter("topic_id");
		int userId = this.request.getIntParameter("user_id");

		PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();

		UserSession us = SessionFacade.getUserSession();

		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
		Topic topic = tm.selectById(topicId);

		// Shall we proceed?
		if (!SessionFacade.isLogged()) {
			 //The topic exists?
			if (topic.getId() == 0) {
				if (logger.isErrorEnabled()) logger.error("topic " + topic + " has id=0");
				this.topicNotFound();
				return;
			}
		}

		Forum forum = ForumRepository.getForum(topic.getForumId());

		//pagination
		EvaluationDAO evaluationDao = DataAccessDriver.getInstance().newEvaluationDAO();
		List<Evaluation> evaluations = evaluationDao.selectTopicEvaluationsWithPosts(topic.getForumId(), topicId);
		Collections.sort(evaluations, new EvaluationUserOrderComparator());

		int start = 0;
		String s = this.request.getParameter("start");
		if (s == null || s.trim().equals("")) {
			int indexCount = 0;
			boolean foundIndex = false;
		    for(Evaluation presEval: evaluations) {
		    	indexCount++;
		    	if (presEval.getUserId() == userId) {
		    		indexCount--;
		    		foundIndex = true;
		    		break;
		    	}
		    }
		    if (foundIndex)
		    	start = indexCount;

			this.context.put("presUserId", userId);
		} else {
			start = ViewCommon.getStartPage();
			Evaluation eval = evaluations.get(start);
			this.context.put("presUserId", eval.getUserId());
			userId = eval.getUserId();
		}
		ViewCommon.contextToPagination(start, evaluations.size(), 1);

		boolean canEdit = false;
		if (isfacilitator)
			canEdit = true;

		Map usersMap = new HashMap();
		List helperList = PostCommon.forumTopicPosts(pm, um, usersMap, canEdit, userId, topicId);

		this.context.put("attachmentsEnabled", true);
		this.context.put("canDownloadAttachments", true);
		this.context.put("am", new AttachmentCommon(this.request, topic.getForumId()));
		this.context.put("posts", helperList);
		this.context.put("topic", topic);
		this.context.put("forum", forum);
		this.context.put("users", usersMap);

		User u = (User)usersMap.get(userId);
		if (!JForumUserUtil.isUserActive(u.getSakaiUserId()))
		{
			this.context.put("message", I18n.getMessage("Evaluation.userInactive"));
			this.setTemplateName(TemplateKeys.GRADE_EVAL_NOT_AUTHORIZED);
			return;
		}

		this.setTemplateName(TemplateKeys.GRADE_VIEW_USER_TOPIC_REPLIES);
	}

	/**
	 * show user replies for the category
	 */
	public void showUserCategoryReplies() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();

		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToGrade"));
			this.setTemplateName(TemplateKeys.GRADE_EVAL_NOT_AUTHORIZED);
			return;
		}

		int categoryId = this.request.getIntParameter("category_id");
		int userId = this.request.getIntParameter("user_id");

		PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
		UserDAO um = DataAccessDriver.getInstance().newUserDAO();
		Category category = ForumRepository.getCategory(categoryId);

		// Shall we proceed?
		if (!SessionFacade.isLogged()) {

			/*Forum f = ForumRepository.getForum(forumId);

			if (f == null || !ForumRepository.isForumAccessibleToUser(forumId)) {
				this.setTemplateName(ViewCommon.contextToLogin());
				return;
			}*/
		}
		//pagination
		//Forum forum = ForumRepository.getForum(forumId);
		EvaluationDAO evaluationDao = DataAccessDriver.getInstance().newEvaluationDAO();
		List<Evaluation> evaluations = evaluationDao.selectCategoryEvaluationsWithPosts(categoryId);
		Collections.sort(evaluations, new EvaluationUserOrderComparator());

		int start = 0;
		String s = this.request.getParameter("start");
		if (s == null || s.trim().equals("")) {
			int indexCount = 0;
			boolean foundIndex = false;
		    for(Evaluation presEval: evaluations) {
		    	indexCount++;
		    	if (presEval.getUserId() == userId) {
		    		//start = evaluations.indexOf(presEval);
		    		indexCount--;
		    		foundIndex = true;
		    		break;
		    	}
		    }
		    if (foundIndex)
		    	start = indexCount;

			this.context.put("presUserId", userId);
		} else {
			start = ViewCommon.getStartPage();
			Evaluation eval = evaluations.get(start);
			this.context.put("presUserId", eval.getUserId());
			userId = eval.getUserId();
		}
		ViewCommon.contextToPagination(start, evaluations.size(), 1);

		boolean canEdit = false;
		if (isfacilitator)
			canEdit = true;

		Map usersMap = new HashMap();
		List helperList = PostCommon.categoryPosts(pm, um, usersMap, canEdit, userId, categoryId);

		this.context.put("attachmentsEnabled", true);
		this.context.put("canDownloadAttachments", true);
		this.context.put("am", new AttachmentCommon(this.request));
		this.context.put("posts", helperList);
		this.context.put("users", usersMap);
		this.context.put("category", category);

		User u = (User)usersMap.get(userId);
		if (!JForumUserUtil.isUserActive(u.getSakaiUserId()))
		{
			this.context.put("message", I18n.getMessage("Evaluation.userInactive"));
			this.setTemplateName(TemplateKeys.GRADE_EVAL_NOT_AUTHORIZED);
			return;
		}
		this.setTemplateName(TemplateKeys.GRADE_VIEW_USER_CATEGORY_REPLIES);
	}

	/**
	 * show message topic not found
	 */
	private void topicNotFound() {
		this.setTemplateName(TemplateKeys.POSTS_TOPIC_NOT_FOUND);
		this.context.put("message", I18n.getMessage("PostShow.TopicNotFound"));
	}

	/**
	 * remove entry from the gradebook
	 * @param grade
	 * @throws Exception
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

	/**
	 * get forum group users
	 * @param forum			Forum
	 * @param site			Site
	 * @param evaluationDao	evaluationDao
	 * @param evaluations	evaluations
	 * @throws Exception
	 */
	protected void getForumGroupUsers(Forum forum, List<Evaluation> evaluations) throws Exception
	{
		if (forum.getAccessType() == Forum.ACCESS_GROUPS)
		{
			if ((forum.getGroups() != null) && (forum.getGroups().size() > 0))
			{
				EvaluationDAO evaluationDao = DataAccessDriver.getInstance().newEvaluationDAO();
				Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());

				// get forum group users
				Collection sakaiSiteGroups = site.getGroups();
				List forumGroupsIds = forum.getGroups();
				List<String> sakaiSiteGroupUserIds = new ArrayList<String>();
				Map<String, Group> sakaiUserGroups = new HashMap<String, Group>();
				for (Iterator i = sakaiSiteGroups.iterator(); i.hasNext();)
				{
					Group group = (Group) i.next();

					if (forumGroupsIds.contains(group.getId()))
					{
						Set members = group.getMembers();
						for (Iterator iter = members.iterator(); iter.hasNext();)
						{
							Member member = (Member) iter.next();
							sakaiSiteGroupUserIds.add(member.getUserId());

							sakaiUserGroups.put(member.getUserId(), group);
						}
					}
				}

				// show users belong to the forum group
				for (Iterator<Evaluation> i = evaluations.iterator(); i.hasNext();)
				{
					Evaluation evaluation = i.next();
					if (!sakaiSiteGroupUserIds.contains(evaluation.getSakaiUserId()))
					{
						if (evaluation.getId() > 0)
						{
							evaluationDao.delete(evaluation.getId());
						}
						i.remove();
					}
					else
					{
						Group group = sakaiUserGroups.get(evaluation.getSakaiUserId());
						evaluation.setUserSakaiGroupName(group.getTitle());
					}
				}


				this.context.put("sakaiSiteGroupUserIds", sakaiSiteGroupUserIds);
			}
			else
			{
				this.context.put("sakaiSiteGroupUserIds", new ArrayList<String>());
			}
		}
	}

	/**
	 * sort evaluations for group titles
	 * @param evalSort		evalution sort
	 * @param evaluations	evalutions
	 */
	protected void sortEvaluationGroups(EvaluationDAO.EvaluationsSort evalSort, List<Evaluation> evaluations)
	{
		if (evalSort == EvaluationsSort.group_title_a)
		{
			Collections.sort(evaluations, new Comparator<Evaluation>()
			{
				public int compare(Evaluation eval1, Evaluation eval2)
				{
					return eval1.getUserSakaiGroupName().compareTo(eval2.getUserSakaiGroupName());
				}
			});
		}
		else if (evalSort == EvaluationsSort.group_title_d)
		{
			Collections.sort(evaluations, new Comparator<Evaluation>()
			{
				public int compare(Evaluation eval1, Evaluation eval2)
				{
					return -1 * eval1.getUserSakaiGroupName().compareTo(eval2.getUserSakaiGroupName());
				}
			});
		}
	}

}
