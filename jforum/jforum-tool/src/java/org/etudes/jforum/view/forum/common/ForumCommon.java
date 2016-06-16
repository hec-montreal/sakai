/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/forum/common/ForumCommon.java $ 
 * $Id: ForumCommon.java 71014 2010-10-26 20:49:34Z murthy@etudes.org $ 
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
package org.etudes.jforum.view.forum.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.entities.Category;
import org.etudes.jforum.entities.Forum;
import org.etudes.jforum.entities.SpecialAccess;
import org.etudes.jforum.entities.Topic;
import org.etudes.jforum.entities.TopicMarkTimeObj;
import org.etudes.jforum.entities.UserSession;
import org.etudes.jforum.repository.ForumRepository;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.user.cover.UserDirectoryService;


/**
 * @author Rafael Steil
 * 8/9/05 - Mallika - Adding method for course categories
 * Mallika - 10/4/06 - Adding code for mark all as read logic
 * Mallika - 11/11/06 - Sending markAll date
 * Mallika - 11/13/06  - Changing compareDate to markAllDate
 * Mallika - 11/15/06 - added code for topicMarkTimes
 */
public class ForumCommon 
{
	/**
	 * Check if some forum has unread messages.
	 * @param forum The forum to search for unread messages 
	 * @param tracking Tracking of the topics read by the user
	 * @param lastVisit The last visit time of the current usre
	 * 
	 * @return The same <code>Forum</code> instance passed as argument, 
	 * which then a call to "getUnread()" will return the "read" status
	 * for this forum
	 */
	public static Forum checkUnreadPosts(Forum forum, Map tracking, long lastVisit) throws Exception
	{
		int unReadTopicsCount = DataAccessDriver.getInstance().newTopicMarkTimeDAO().selectUnreadMarkedTopicsCount(forum.getId(), SessionFacade.getUserSession().getUserId());
		
		if (unReadTopicsCount > 0)
		{
			forum.setUnread(true);
			return forum;
		}
		
		//System.out.println("User id "+userId+" Forum id "+forum.getId()+" lastVisit "+new java.sql.Date(lastVisit).toString());
		List unreadTopics = DataAccessDriver.getInstance().newForumDAO().checkUnreadTopics(
				forum.getId(), lastVisit);
		
		List topicMarkTimes = DataAccessDriver.getInstance().newTopicMarkTimeDAO().selectTopicMarkTimes(
				forum.getId(), SessionFacade.getUserSession().getUserId());
		
		if (unreadTopics.size() == 0) {
			//System.out.println("Size is zero");
			return forum;
		}
		
		for (Iterator iter = unreadTopics.iterator(); iter.hasNext();)
		{
			Topic t = (Topic) iter.next();

			Integer topicId = new Integer(t.getId());
			Date markTime = null;
			// Check if topicId is in mark read list
			for (Iterator mIter = topicMarkTimes.iterator(); mIter.hasNext();)
			{
				TopicMarkTimeObj tmObj = (TopicMarkTimeObj) mIter.next();
				if (tmObj.getTopicId() == t.getId())
				{
					markTime = tmObj.getMarkTime();
					break;
				}
				else
				{
					markTime = null;
				}
			}
			// System.out.println("Topicid is "+topicId);
			if (t.getTime().compareTo(new Date(lastVisit)) > 0)
			{
				if ((markTime == null) || ((markTime.compareTo(new Date(lastVisit)) > 0) && (t.getTime().compareTo(markTime) > 0))
						|| ((markTime.compareTo(new Date(lastVisit)) < 0) && (t.getTime().compareTo(new Date(lastVisit)) > 0)))
				{
					if (tracking != null && tracking.containsKey(topicId))
					{
						long readTime = ((Long) tracking.get(topicId)).longValue();
						if (t.getTime().compareTo(new Date(readTime)) > 0)
						{
							// System.out.println("Unread after tracking");
							forum.setUnread(true);
						}
					}
					else
					{
						// System.out.println("Unread in else");
						forum.setUnread(true);
					}

					if (forum.getUnread())
					{
						break;
					}
				}
			}
		}
		
		return forum;
	}
	
	/**
	 * Gets all forums available to the user.
	 * 
	 * @param us An <code>UserSession</code> instance with user information
	 * @param anonymousUserId The id which represents the anonymous user
	 * @param tracking <code>Map</code> instance with information 
	 * about the topics read by the user
	 * @param checkUnreadPosts <code>true</code> if is to search for unread topics inside the forums, 
	 * or <code>false</code> if this action is not needed. 
	 * @return A <code>List</code> instance where each record is an instance of a <code>Category</code>
	 * object
	 * @throws Exception
	 */
	public static List getAllCategoriesAndForums(UserSession us, int anonymousUserId, 
			Map tracking, boolean checkUnreadPosts) throws Exception
	{
		long lastVisit = 0;
		int userId = anonymousUserId;
		
		if (us != null) {
 		    lastVisit = us.getLastVisit().getTime();
			
			userId = us.getUserId();
		}

		Date markAllDate = us.getMarkAllTime();
		
		if (markAllDate == null)
		{
			//First date mentioned in Java API
			GregorianCalendar gc = new GregorianCalendar(1970,0,1);
			markAllDate = gc.getTime();
		}
				
		// Do not check for unread posts if the user is not logged in
		checkUnreadPosts = checkUnreadPosts && (us.getUserId() != anonymousUserId);
		//get course categories for the user
		List categories = ForumRepository.getAllCourseCategoriesForUser(userId);
		
		
		Date currentTime = Calendar.getInstance().getTime();
		
		List returnCategories = new ArrayList();
		for (Iterator iter = categories.iterator(); iter.hasNext(); ) 
		{
			Category c = new Category((Category)iter.next());
			
			// Don't show the category if start date is a later date
			if (!(JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser()))
			{
				/* Ignore category start date for forum special access user. 
					Show forums that have special access to the user.*/
				boolean specialAccessUser = false, specialAccessUserAccess = false;
				
				for (Iterator forumIter = c.getForums().iterator(); forumIter.hasNext(); ) 
				{
					specialAccessUser = false;
					specialAccessUserAccess = false;
					
					Forum f = (Forum)forumIter.next();
					
					// check for forum special access
					List<SpecialAccess> specialAccessList = f.getSpecialAccessList();

					for (SpecialAccess sa : specialAccessList)
					{
						if (sa.getUserIds().contains(new Integer(userId)))
						{
							specialAccessUser = true;
							
							if (sa.getStartDate() == null)
							{
								specialAccessUserAccess = true;
							}
							else if (currentTime.after(sa.getStartDate()))
							{
								specialAccessUserAccess = true;
							}
							break;
						}
					}
					
					if (specialAccessUser)
					{
						if (specialAccessUserAccess)
						{
							// forums.add(f);
							break;
						}
					}
				}
				
				// check category start date
				if (specialAccessUser)
				{
					if (!specialAccessUserAccess)
					{
						continue;
					}					
				}
				else if ((c.getStartDate() != null) && (c.getStartDate().after(currentTime)))
				{
					
				}
			}
			
			if (c.getForums().size() == 0)
				continue;
			
			
			if (checkUnreadPosts)
			{
				for (Iterator tmpIterator = c.getForums().iterator(); tmpIterator.hasNext(); ) {
					Forum f = (Forum)tmpIterator.next();

					f = ForumCommon.checkUnreadPosts(f, tracking, markAllDate.getTime());
				}
			}
			
			returnCategories.add(c);
		}
		
		return returnCategories;
	}
	
	/**
	 * @see #getAllCategoriesAndForums(UserSession, int, Map, boolean)
	 */
	public static List getAllCategoriesAndForums(boolean checkUnreadPosts) throws Exception
	{
		return getAllCategoriesAndForums(SessionFacade.getUserSession(), 
				SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID), 
				(HashMap)SessionFacade.getAttribute(ConfigKeys.TOPICS_TRACKING), 
				checkUnreadPosts);
	}
	
	/**
	 * @see #getAllCategoriesAndForums(boolean)
	 */
	public static List getAllCategoriesAndForums() throws Exception
	{
		UserSession us = SessionFacade.getUserSession();
		boolean checkUnread = (us != null && us.getUserId() 
				!= SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID));
		return getAllCategoriesAndForums(checkUnread);
	}
}
