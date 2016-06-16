/*********************************************************************************** 
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/TopicMarkTimeDAO.java $ 
 * $Id: TopicMarkTimeDAO.java 66237 2010-02-19 23:48:58Z murthy@etudes.org $ 
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

import java.util.Date;
import java.util.List;

import org.etudes.jforum.entities.TopicMarkTimeObj;

public interface TopicMarkTimeDAO
{

	/**
	 * Add topic mark time for the user
	 * @param topicId 	-	topic id
	 * @param userId 	-	user id
	 * @param markTime	-	mark time 
	 * @param isRead	-	is topic read
	 * @throws Exception
	 */
	public void addMarkTime(int topicId, int userId, Date markTime, boolean isRead) throws Exception;

	/**
	 * Select topic marked time for the user
	 * @param topicId	-	topic id
	 * @param userId	-	user id
	 * @return	The mark time of the topic
	 * @throws Exception
	 */
	public Date selectMarkTime(int topicId, int userId) throws Exception;
	
	/**
	 * Select topic mark time details for the user
	 * @param topicId	-	topic id
	 * @param userId	-	user id
	 * @return			Details of the marked time for the topic and for the user
	 * @throws Exception
	 */
	public TopicMarkTimeObj selectTopicMarkTime(int topicId, int userId) throws Exception;

	/**
	 * Select the list of the topic mark times for the forum and user
	 * @param forumId	- forum id
	 * @param userId	- user id
	 * @return	The list of the topic mark times
	 * @throws Exception
	 */
	public List selectTopicMarkTimes(int forumId, int userId) throws Exception;

	/**
	 * Select the topic mark times for the user for the course
	 * @param userId	-	 user id
	 * @return	The list of the mark times for the user
	 * @throws Exception
	 */
	public List selectCourseTopicMarkTimes(int userId) throws Exception;

	/**
	 * Update the topic mark time for the user
	 * @param topicId	- topic id
	 * @param userId	- user id
	 * @param markTime	- mark time
	 * @param isRead	- is read
	 * @throws Exception
	 */
	public void updateMarkTime(int topicId, int userId, Date markTime, boolean isRead) throws Exception;
	
	/**
	 * Delete topic mark time for the user
	 * @param topicId	- topic id
	 * @param userId	- user id
	 * @throws Exception
	 */
	public void deleteMarkTime(int topicId, int userId) throws Exception;
	
	/**
	 * Mark topic as unread
	 * @param topicId	- topic id
	 * @param userId	- user id
	 * @param markTime	- mark time
	 * @throws Exception
	 */
	public void markTopicUnread(int topicId, int userId, Date markTime) throws Exception;
	
	/**
	 * Select the unread marked topics count for the user
	 * @param forumId	- forum id
	 * @param userId	- user id
	 * @return	The count of the unread topics count for the user
	 * @throws Exception
	 */
	public int selectUnreadMarkedTopicsCount(int forumId, int userId) throws Exception;
	
	/**
	 * Select the user course unread marked topics
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public List<TopicMarkTimeObj> selectUserCourseUnreadMarkedTopics(int userId) throws Exception; 

}