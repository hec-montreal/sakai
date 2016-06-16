/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/ForumDAO.java $ 
 * $Id: ForumDAO.java 69251 2010-07-15 21:14:57Z murthy@etudes.org $ 
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
package org.etudes.jforum.dao;

import java.util.List;

import org.etudes.jforum.entities.Forum;
import org.etudes.jforum.entities.LastPostInfo;


/**
* Model interface for {@link net.jforum.Forum}.
 * This interface defines methods which are expected to be
 * implementd by a specific data access driver. The intention is
 * to provide all functionality needed to update, insert, delete and
 * select some specific data.
 * 
 * @author Rafael Steil
 * Mallika - 1/5/06 - Adding method to check if forum can be deleted
 */
public interface ForumDAO 
{
	/**
	 * Gets a specific <code>Forum</code>.
	 * 
	 * @param forumId The ForumID to search
	 * @return <code>Forum</code>object containing all the information
	 * @throws Exception
	 * @see #selectAll
	 */
	public Forum selectById(int forumId) throws Exception;
	
	/**
	 * Gets the list of forums for a catogory id.
	 * @param categoryId
	 * @return list of forums associated with category
	 * @throws Exception
	 */
	public List<Forum> selectByCategoryId(int categoryId) throws Exception;
	
	/**
	 * Selects all forums data from the database.
	 * 
	 * @return ArrayList with the forums found 
	 * @throws Exception
	 * @see #selectById
	 */
	public List selectAll() throws Exception;
	
	/**
	 * Selects all forums data from the database for the course.
	 * 
	 * @return ArrayList with the forums found 
	 * @throws Exception
	 * @see #selectAllByCourse
	 */
	public List selectAllByCourse() throws Exception;
	
	/**
	 * Sets the forum's order one level up.
	 * When you call this method on a specific forum, the forum that 
	 * is one level up will be sent down one level, and the forum which
	 * you are sending up wil take the order position of the forum which
	 * was sent down.
	 * 
	 * @param forum The forum to change its order
	 * @param related The forum which comes before the forum we want to change
	 * @throws Exception
	 * @return The changed forum, with the new order set
	 */
	public Forum setOrderUp(Forum forum, Forum related) throws Exception;
	
	/**
	 * Sets the forum's order one level down.
	 * For more information, take a look at @link #setOrderUp method. 
	 * The only different between both is that this method sends the 
	 * forum order down.
	 * 
	 * @param forum The forum to change its order
	 * @param related The forum which comes after the forum we want to change
	 * @throws Exception
	 * @return The changed forum, with the new order set
	 */
	public Forum setOrderDown(Forum forum, Forum related) throws Exception;
	
	//Mallika-new code beg
	public boolean canDelete(int forumId) throws Exception;
	//Mallika-new code end
	
	/**
	 * Delete a forum.
	 * 
	 * @param forumId The forum ID to delete
	 * @throws Exception
	 * @see #canDelete(int)
	 */
	public void delete(int forumId) throws Exception;
		
	/**
	 * Updates a Forum.
	 * 
	 * @param forum Reference to a <code>Forum</code> object to update
	 * @throws Exception
	 * @see #update(int)
	 */
	public void update(Forum forum) throws Exception;
	
	/**
	 * Updates dates of a Forum.
	 * 
	 * @param forum Reference to a <code>Forum</code> object to update
	 * @throws Exception
	 */
	public void updateDates(Forum forum) throws Exception;
	
	/**
	 * Adds a new Forum.
	 * 
	 * @param forum Reference to a valid and configured <code>Forum</code> object
	 * @return The forum's ID
	 * @throws Exception
	 */
	public int addNew(Forum forum) throws Exception;
	
	/**
	 * Sets the last topic of a forum
	 * 
	 * @param forumId The forum ID to update
	 * @param postId Last post ID
	 * @throws Exception
	 */
	public void setLastPost(int forumId, int postId) throws Exception;

	/**
	 * Increments the total number of topics of a forum
	 * 
	 * @param forumId The forum ID to update
	 * @param count Increment a total of <code>count</code> elements
	 * @throws Exception
	 */
	public void incrementTotalTopics(int forumId, int count) throws Exception;
	
	/**
	 * Decrements the total number of topics of a forum
	 * 
	 * @param forumId The forum ID to update
	 * @param count Decrement a total of <code>count</code> elements 
	 * @throws Exception
	 */
	public void decrementTotalTopics(int forumId, int count) throws Exception;

	public LastPostInfo getLastPostInfo(int forumId) throws Exception;
	
	/**
	 * Gets the total number of messages of a forum
	 * 
	 * @param forumId The forum ID
	 * @throws Exception
	 */
	public int getTotalMessages() throws Exception; 
	
	/**
	 * Gets the total number os topics of some forum
	 * 
	 * @return Total of topics
	 * @throws Exception
	 */
	public int getTotalTopics(int forumId) throws Exception;

	
	/**
	 * Gets the last post id associated to the forum
	 * 
	 * @param forumId The forum id 
	 * @throws Exception
	 */
	public int getMaxPostId(int forumId) throws Exception;
	
	/**
	 * Move the topics to a new forum
	 * 
	 * @param topics The topics id array
	 * @param fromForumId The original forum id
	 * @param toForumId The destination forum id
	 * @throws Exception
	 */
	public void moveTopics(String[] topics, int fromForumId, int toForumId) throws Exception;
	
	/**
	 * Check if the forum has unread topics.
	 * 
	 * @param forumId The forum's id to check
	 * @param lastVisit The last visit time the user has seen the forum
	 * @return An <code>java.util.List</code> instance, where each entry is a
	 * <code>net.jforum.entities.Topic</code> instance. 
	 * @throws Exception
	 */
	public List checkUnreadTopics(int forumId, long lastVisit) throws Exception;
	
	/**
	 * Enable or disabled moderation for the forum.
	 * 
	 * @param categoryId The main category for the forum
	 * @param status a boolean value representing the desired status
	 * @throws Exception
	 */
	public void setModerated(int categoryId, boolean status) throws Exception;
	
	/**
	 * Get the count of the forums with start or end date for a category
	 * 
	 * @param categoryId	category id
	 * @return	Count of the forums that has either start or end date or both
	 * @throws Exception
	 */
	public int getForumDatesCount(int categoryId) throws Exception;
}
