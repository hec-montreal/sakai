/*********************************************************************************** 
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/EvaluationDAO.java $ 
 * $Id: EvaluationDAO.java 70471 2010-09-29 22:13:03Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008, 2009, 2010 Etudes, Inc. 
 * 
 * Portions completed before September 1, 2008 Copyright (c) 2004, 2005, 2006, 2007 Foothill College, ETUDES Project 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License. 
 * 
 **********************************************************************************/ 

package org.etudes.jforum.dao;

import java.util.List;

import org.etudes.jforum.entities.Evaluation;


/**
 * Model interface for {@link net.jforum.Evaluation}. This interface defines methods which are expected to be implementd by a specific data access
 * driver. The intention is to provide all functionality needed to update, insert, delete and select some specific data.
 * 
 * @author Murthy Tanniru
 */
public interface EvaluationDAO
{
	/**
	 * Sort options for evaluations
	 */
	enum EvaluationsSort
	{
		last_name_a, last_name_d, total_posts_a, total_posts_d, scores_a, scores_d, group_title_a, group_title_d
	}

	/**
	 * Adds a new evaluation
	 * 
	 * @param evaluation
	 *        Reference to a valid and configured <code>Evaluation</code> object
	 * @return the evaluation id
	 * @throws Exception
	 */
	public int addNew(Evaluation evaluation) throws Exception;
	
	/**
	 * select forum evaluations
	 * 
	 * @param forumId forum id
	 * @param evalSort evalSort
	 * @return evaluations list
	 * @throws Exception
	 */
	public List<Evaluation> selectForumEvaluations(int forumId, EvaluationsSort evalSort) throws Exception;
	
	/**
	 * select category evaluations
	 * 
	 * @param categoryId category id
	 * @param evalSort evalSort
	 * @return evaluations list
	 * @throws Exception
	 */
	public List<Evaluation> selectCategoryEvaluations(int categoryId, EvaluationsSort evalSort) throws Exception;
	
	/**
	 * select category evaluations only with posts
	 * 
	 * @param categoryId category id
	 * @return evaluations list
	 * @throws Exception
	 */
	public List<Evaluation> selectCategoryEvaluationsWithPosts(int categoryId) throws Exception;
	
	/**
	 * select forum evaluations only with posts
	 * 
	 * @param forumId forum id
	 * @return evaluations list
	 * @throws Exception
	 */
	public List<Evaluation> selectForumEvaluationsWithPosts(int forumId) throws Exception;
	
	/**
	 * select forum evaluations count
	 * @param forumId
	 * @return count of evaluations for the forum
	 * @throws Exception
	 */
	public int selectForumEvaluationsCount(int forumId) throws Exception;
	
	/**
	 * select evaluations count for grade
	 * @param gradeId
	 * @return count of evaluations for the grade
	 * @throws Exception
	 */
	public int selectEvaluationsCountByGradeId(int gradeId) throws Exception;
	
	/**
	 * select forum topic evaluations count
	 * 
	 * @param forumId
	 * @return
	 * @throws Exception
	 */
	public int selectForumTopicEvaluationsCount(int forumId) throws Exception;
	
	/**
	 * select topic evaluations count
	 * @param postId
	 * @return count of evaluations for the topic
	 * @throws Exception
	 */
	public int selectForumTopicEvaluationsCountById(int topicId) throws Exception;
	
	/**
	 * select topic evaluations
	 * 
	 * @param forumId forum id
	 * @param topicId topic id
	 * @return evaluations list
	 * @throws Exception
	 */
	public List<Evaluation> selectTopicEvaluations(int forumId, int topicId, EvaluationsSort evalSort) throws Exception;
	
	/**
	 * select topic evaluations with posts
	 * 
	 * @param forumId forum id
	 * @param topicId topic id
	 * @return evaluations list
	 * @throws Exception
	 */
	public List<Evaluation> selectTopicEvaluationsWithPosts(int forumId, int topicId) throws Exception;
		
	/**
	 * select forum evaluation for a forumId and userid
	 * 
	 * @param forumId forum id
	 * @param userId user id
	 * @return evaluation for the forum and user
	 * @throws Exception
	 */
	public Evaluation selectEvaluationByForumIdUserId(int forumId, int userId) throws Exception;

	/**
	 * @param forumId
	 * @param topicId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public Evaluation selectEvaluationByForumIdTopicIdUserId (int forumId, int topicId, int userId) throws Exception;
	
	/**
	 * select forum evaluation for a categoryId and userid
	 * 
	 * @param categoryId category  id
	 * @param userId user id
	 * @return evaluation for the category and user
	 * @throws Exception
	 */
	public Evaluation selectEvaluationByCategoryIdUserId(int categoryId, int userId) throws Exception;
	
	/**
	 * Updates a evaluation.
	 * 
	 * @param forum
	 *        Reference to a <code>Evaluation</code> object to update
	 * @throws Exception
	 */
	public void update(Evaluation evaluation) throws Exception;
	
	/**
	 * Deletes a evaluation.
	 * @param evaluationId The evaluationId id to delete
	 * @throws Exception
	 */
	public void delete(int evaluationId) throws Exception;

}
