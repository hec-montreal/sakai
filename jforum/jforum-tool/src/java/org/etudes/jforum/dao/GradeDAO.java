/*********************************************************************************** 
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/GradeDAO.java $ 
 * $Id: GradeDAO.java 64878 2009-11-24 01:17:32Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008, 2009 Etudes, Inc. 
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

import org.etudes.jforum.entities.Grade;


/**
 * Model interface for {@link net.jforum.Grade}. This interface defines methods which are expected to be implementd by a specific data access driver.
 * The intention is to provide all functionality needed to update, insert, delete and select some specific data.
 * 
 * @author Murthy Tanniru
 */
public interface GradeDAO
{
	/**
	 * Adds a new grade
	 * @param grade Reference to a valid and configured <code>Grade</code> object
	 * @return the grade id
	 * @throws Exception
	 */
	public int addNew(Grade grade) throws Exception;
	
	/**
	 * updates the existing grade
	 * @param grade Reference to a valid and configured <code>Grade</code> object
	 * @throws Exception
	 */
	public void updateForumGrade(Grade grade) throws Exception;
	
	
	/**
	 * updates the existing topic grade
	 * @param grade Reference to a valid and configured <code>Grade</code> object
	 * @throws Exception
	 */
	public void updateTopicGrade(Grade grade) throws Exception;
	
	
	/**
	 * updates the existing category grade
	 * @param grade Reference to a valid and configured <code>Grade</code> object
	 * @throws Exception
	 */
	public void updateCategoriesGrade(Grade grade) throws Exception;
	
	
	/**
	 * updates the existing topic grade
	 * @param grade Reference to a valid and configured <code>Grade</code> object
	 * @return the grade id
	 * @throws Exception
	 */
	public void updateAddToGradeBookStatus(int gradeId, boolean addToGradeBook) throws Exception;
	
	/**
	 * Gets a specific <code>Grade</code> for the given grade id.
	 * @param gradeId Id of the Grade
	 * @return <code>Grade</code>object containing all the information
	 * @throws Exception
	 */
	public Grade selectById(int gradeId) throws Exception;

	/**
	 * Gets a specific <code>Grade</code> for the given forum id.
	 * @param forumId Forum id of the Grade
	 * @return <code>Grade</code>object containing all the information
	 * @throws Exception
	 */
	public Grade selectByForumId(int forumId) throws Exception;
	
	/**
	 * Gets a specific <code>Grade</code> for the given category id.
	 * @param categoryId Category id of the Grade
	 * @return <code>Grade</code>object containing all the information
	 * @throws Exception
	 */
	public Grade selectByCategoryId(int categoryId) throws Exception;
	
	/**
	 * Gets a specific <code>Grade</code> for the given forum id and topic id.
	 * @param forumId Forum id of the Grade
	 * @param topicId Topic id of the Grade
	 * @return <code>Grade</code>object containing all the information
	 * @throws Exception
	 */
	public Grade selectByForumTopicId(int forumId, int topicId) throws Exception;
	
	/**
	 * Gets a specific <code>Grade</code> for the given forum id and topic id.
	 * @param forumId Forum id of the Grade
	 * @param topicId Topic id of the Grade
	 * @param categoryId Category id of the Grade
	 * @return <code>Grade</code>object containing all the information
	 * @throws Exception
	 */
	public Grade selectByForumTopicCategoryId(int forumId, int topicId, int categoryId) throws Exception;
	
	
	/**
	 * Gets the list of grades associated with forum topics.
	 * @param forumId Forum id
	 * @return List of grades associated with forum topics
	 * @throws Exception
	 */
	public List<Grade> selectForumTopicGradesByForumId(int forumId) throws Exception;
	
	
	/**
	 * Deletes a grade.
	 * @param gradeId The grade id to delete
	 * @throws Exception
	 */
	public void delete(int gradeId) throws Exception;
	
	
	/**
	 * deletes a grade
	 * @param forumId forum Id
	 * @param topicId topic id
	 * @throws Exception
	 */
	public void delete(int forumId, int topicId) throws Exception;
	
	/**
	 * Check to see category forums are gradable
	 * @param 	categoryId category id
	 * @return 	true = if forum(s) of the category are of gradable 
	 * 			false = if forum(s) of the category are of not gradable 
	 * @throws Exception
	 */
	public boolean isCategoryForumsGradable(int categoryId) throws Exception;

}
