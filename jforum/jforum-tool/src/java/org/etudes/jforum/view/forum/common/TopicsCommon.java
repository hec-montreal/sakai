/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/forum/common/TopicsCommon.java $ 
 * $Id: TopicsCommon.java 69344 2010-07-21 23:47:21Z murthy@etudes.org $ 
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
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.JForum;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.ForumDAO;
import org.etudes.jforum.dao.TopicDAO;
import org.etudes.jforum.dao.TopicMarkTimeDAO;
import org.etudes.jforum.entities.Forum;
import org.etudes.jforum.entities.Post;
import org.etudes.jforum.entities.Topic;
import org.etudes.jforum.entities.TopicMarkTimeObj;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.entities.UserSession;
import org.etudes.jforum.repository.ForumRepository;
import org.etudes.jforum.repository.TopicRepository;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.concurrent.executor.QueuedExecutor;
import org.etudes.jforum.util.mail.EmailSenderTask;
import org.etudes.jforum.util.mail.TopicSpammer;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.etudes.jforum.view.forum.ModerationHelper;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.cover.UserDirectoryService;

import freemarker.template.SimpleHash;

/**
 * General utilities methods for topic manipulation.
 * 
 * @author Rafael Steil
 * Murthy - 08/14/2006 - Added Task type to topicListingBase
 * Mallika - 9/27/06 - eliminating views column
 * Mallika - 11/13/06  - Changing compareDate to markAllDate
 * Mallika - 11/15/06 - Added code for topicMarkTimes
 * Mallika - 11/23/06 - Added method for topicCourseMarkTimes
 * Murthy - 11/20/06 - added new method notifyNewTopicToUsers
 */
public class TopicsCommon 
{
	private static Log logger = LogFactory.getLog(TopicsCommon.class);
	
	/**
	 * List all first 'n' topics of a given forum.
	 * This method returns no more than <code>ConfigKeys.TOPICS_PER_PAGE</code>
	 * topics for the forum. 
	 * 
	 * @param forumId The forum id to which the topics belongs to
	 * @param start The start fetching index
	 * @return <code>java.util.List</code> containing the topics found.
	 * @throws Exception
	 */
	public static List topicsByForum(int forumId, int start) throws Exception
	{
		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
		// int topicsPerPage = SystemGlobals.getIntValue(ConfigKeys.TOPICS_PER_PAGE);
		int topicsPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.TOPICS_PER_PAGE);
		List topics = null;
		
		// Try to get the first's page of topics from the cache
		if (start == 0 && SystemGlobals.getBoolValue(ConfigKeys.TOPIC_CACHE_ENABLED)) {
			topics = TopicRepository.getTopics(forumId);

			if (topics.size() == 0) {
				topics = tm.selectAllByForumByLimit(forumId, start, topicsPerPage);
				TopicRepository.addAll(forumId, topics);
			}
		}
		else {
			topics = tm.selectAllByForumByLimit(forumId, start, topicsPerPage);
		}
		
		return topics;
	}
	
	/**
	 * Prepare the topics for listing.
	 * This method does some preparation for a set ot <code>net.jforum.entities.Topic</code>
	 * instances for the current user, like verification if the user already
	 * read the topic, if pagination is a need and so on.
	 * 
	 * @param topics The topics to process
	 * @return The post-processed topics.
	 */
	public static List prepareTopics(List topics) throws Exception
	{
		UserSession userSession = SessionFacade.getUserSession();

		long lastVisit = userSession.getLastVisit().getTime();
		// int hotBegin = SystemGlobals.getIntValue(ConfigKeys.HOT_TOPIC_BEGIN);
		int hotBegin = SakaiSystemGlobals.getIntValue(ConfigKeys.HOT_TOPIC_BEGIN);

		// int postsPerPage = SystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		int postsPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		Map topicsTracking = (HashMap)SessionFacade.getAttribute(ConfigKeys.TOPICS_TRACKING);
		List newTopics = new ArrayList(topics.size());
		
		boolean checkUnread = (userSession.getUserId() 
			!= SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID));
		
		List topicMarkTimes = null;
		
		Date markAllDate = null;
	
		markAllDate = userSession.getMarkAllTime();
		if (markAllDate == null)
		{
			//First date mentioned in Java API
			GregorianCalendar gc = new GregorianCalendar(1970,0,1);
			markAllDate = gc.getTime();
		}
	    /*Date lastVisitDate = userSession.getLastVisit();
	  
	    Date compareDate;
		if (markAllDate == null)
		{
			System.out.println("LastVisit setting to false,markDate is null ");
			compareDate = lastVisitDate;
		}
		else
		{
			if (lastVisitDate.getTime() > markAllDate.getTime())
			{
				System.out.println("LastVisit setting to false ");
				compareDate = lastVisitDate;
			}
			else
			{
				System.out.println("markAll setting to false ");	
				compareDate = markAllDate;
			}
		}*/				
		int prevTopicForumId = -1;
		
		for (Iterator iter = topics.iterator(); iter.hasNext(); ) {
			boolean read = false;
			boolean markedNotRead = false;
			
			Topic t = (Topic)iter.next();
						
			Date markTime = null;
			Date compareDate = null;
			
			if (prevTopicForumId != t.getForumId())
			{
				topicMarkTimes = null;
				topicMarkTimes = DataAccessDriver.getInstance().newTopicMarkTimeDAO().selectTopicMarkTimes(t.getForumId(), userSession.getUserId());
			}
			
			//Check if topicId is in mark read list
			for (Iterator mIter = topicMarkTimes.iterator(); mIter.hasNext(); )
			{
				TopicMarkTimeObj tmObj = (TopicMarkTimeObj) mIter.next();
				if (tmObj.getTopicId() == t.getId())
				{
					markTime = tmObj.getMarkTime();
					if (!tmObj.isRead())
					{
						markedNotRead = true;
					}
					break;
				}
				
			}			
			if (markTime == null)
			{
				GregorianCalendar gc = new GregorianCalendar(1970,0,1);
				markTime = gc.getTime();
			}
			
			if (markAllDate.getTime() > markTime.getTime())
			{
				compareDate = markAllDate;
			}
			else
			{
				compareDate = markTime;
			}
			//System.out.println("For topic id "+t.getId()+" compareDate is "+compareDate.toString());
			 //Mallika - changing line below from lastVisit to compareDate.getTime()			
			if (checkUnread && t.getLastPostTimeInMillis().getTime() > compareDate.getTime()) {
				if (topicsTracking.containsKey(new Integer(t.getId()))) {
					read = (t.getLastPostTimeInMillis().getTime() == ((Long)topicsTracking.get(new Integer(t.getId()))).longValue());
				}
				
				if (markedNotRead)
				{
					read = false;
				}
			}
			else {
				if (markedNotRead)
				{
					read = false;
				}
				else
				{
					read = true;
				}
			}
			
			if (t.getTotalReplies() + 1 > postsPerPage) {
				t.setPaginate(true);
				t.setTotalPages(new Double(Math.floor(t.getTotalReplies() / postsPerPage)));
			}
			else {
				t.setPaginate(false);
				t.setTotalPages(new Double(0));
			}
			
			// Check if this is a hot topic
			t.setHot(t.getTotalReplies() >= hotBegin);
			
			t.setRead(read);
			prevTopicForumId = t.getForumId();
			newTopics.add(t);			
		}
		
		return newTopics;
	}

	//This method is used by RecentTopicsAction and SearchAction
	//because both of these return topics associated with different
	//forums, this method needs to execute a query per topic
	//to get the mark read time
	public static List prepareDiffTopics(List topics) throws Exception
	{
		UserSession userSession = SessionFacade.getUserSession();

		long lastVisit = userSession.getLastVisit().getTime();
		// int hotBegin = SystemGlobals.getIntValue(ConfigKeys.HOT_TOPIC_BEGIN);
		int hotBegin = SakaiSystemGlobals.getIntValue(ConfigKeys.HOT_TOPIC_BEGIN);

		// int postsPerPage = SystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		int postsPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		Map topicsTracking = (HashMap)SessionFacade.getAttribute(ConfigKeys.TOPICS_TRACKING);
		List newTopics = new ArrayList(topics.size());
		
		boolean checkUnread = (userSession.getUserId() 
			!= SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID));
		
		List topicMarkTimes = null;
		if (topics.size() > 0)
		{	
		  topicMarkTimes = DataAccessDriver.getInstance().newTopicMarkTimeDAO().selectCourseTopicMarkTimes(
				userSession.getUserId());
		}  
				
		
		Date markAllDate = null;
	
		markAllDate = userSession.getMarkAllTime();
		if (markAllDate == null)
		{
			//First date mentioned in Java API
			GregorianCalendar gc = new GregorianCalendar(1970,0,1);
			markAllDate = gc.getTime();
		}
	    /*Date lastVisitDate = userSession.getLastVisit();
	  
	    Date compareDate;
		if (markAllDate == null)
		{
			System.out.println("LastVisit setting to false,markDate is null ");
			compareDate = lastVisitDate;
		}
		else
		{
			if (lastVisitDate.getTime() > markAllDate.getTime())
			{
				System.out.println("LastVisit setting to false ");
				compareDate = lastVisitDate;
			}
			else
			{
				System.out.println("markAll setting to false ");	
				compareDate = markAllDate;
			}
		}*/				
		TopicMarkTimeDAO tmark = DataAccessDriver.getInstance().newTopicMarkTimeDAO();

		for (Iterator iter = topics.iterator(); iter.hasNext(); ) {
			boolean read = false;
			Topic t = (Topic)iter.next();
			
			Date markTime = null;
			Date compareDate = null;
			
			//markTime = tmark.selectMarkTime(t.getId(), SessionFacade.getUserSession().getUserId());

			for (Iterator mIter = topicMarkTimes.iterator(); mIter.hasNext(); )
			{
				TopicMarkTimeObj tmObj = (TopicMarkTimeObj) mIter.next();
				if (tmObj.getTopicId() == t.getId())
				{
					markTime = tmObj.getMarkTime();
					break;
				}
				
			}				
			if (markTime == null)
			{
					GregorianCalendar gc = new GregorianCalendar(1970,0,1);
					markTime = gc.getTime();
			}
		
			if (markAllDate.getTime() > markTime.getTime())
			{
				compareDate = markAllDate;
			}
			else
			{
				compareDate = markTime;
			}
			System.out.println("For topic id "+t.getId()+" compareDate is "+compareDate.toString());
			 //Mallika - changing line below from lastVisit to compareDate.getTime()			
			if (checkUnread && t.getLastPostTimeInMillis().getTime() > compareDate.getTime()) {
				if (topicsTracking.containsKey(new Integer(t.getId()))) {
					read = (t.getLastPostTimeInMillis().getTime() == ((Long)topicsTracking.get(new Integer(t.getId()))).longValue());
				}
			}
			else {
				read = true;
			}
			
			if (t.getTotalReplies() + 1 > postsPerPage) {
				t.setPaginate(true);
				t.setTotalPages(new Double(Math.floor(t.getTotalReplies() / postsPerPage)));
			}
			else {
				t.setPaginate(false);
				t.setTotalPages(new Double(0));
			}
			
			// Check if this is a hot topic
			t.setHot(t.getTotalReplies() >= hotBegin);
			
			t.setRead(read);
			newTopics.add(t);
		}
		
		return newTopics;
	}	
	
	/**
	 * Common properties to be used when showing topic data
	 */
	public static void topicListingBase() throws Exception
	{
		SimpleHash context = JForum.getContext();
		// Topic Types
		context.put("TOPIC_ANNOUNCE", new Integer(Topic.TYPE_ANNOUNCE));
		context.put("TOPIC_STICKY", new Integer(Topic.TYPE_STICKY));
		context.put("TOPIC_NORMAL", new Integer(Topic.TYPE_NORMAL));
		context.put("EXPORT_YES", new Integer(Topic.EXPORT_YES));
		// Topic Status
		context.put("STATUS_LOCKED", new Integer(Topic.STATUS_LOCKED));
		context.put("STATUS_UNLOCKED", new Integer(Topic.STATUS_UNLOCKED));
		
		// Moderation
		/*context.put("moderator", SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION));
		context.put("can_remove_posts", SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION_POST_REMOVE));
		context.put("can_move_topics", SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION_TOPIC_MOVE));
		context.put("can_lockUnlock_topics", SecurityRepository.canAccess(SecurityConstants.PERM_MODERATION_TOPIC_LOCK_UNLOCK));*/
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		context.put("moderator",  isfacilitator);
		context.put("can_remove_posts", isfacilitator);
		context.put("can_move_topics", isfacilitator);
		context.put("can_lockUnlock_topics", isfacilitator);
		context.put("rssEnabled", SystemGlobals.getBoolValue(ConfigKeys.RSS_ENABLED));
		
	}
	
	/**
	 * Checks if the user is allowed to view the topic
	 * 
	 * @param forumId The forum id to which the topics belongs to
	 * @return <code>true</code> if the topic is accessible, <code>false</code> otherwise
	 * @throws Exception
	 */
	public static boolean isTopicAccessible(int forumId) throws Exception 
	{
		Forum f = ForumRepository.getForum(forumId);
		
		//if (f == null || !ForumRepository.isCategoryAccessible(f.getCategoryId())) {
		if (f == null || !ForumRepository.isForumAccessibleToUser(f)) {
			new ModerationHelper().denied(I18n.getMessage("PostShow.denied"));
			return false;
		}
		
		return true;
	}
	
	/**
	 * Sends a "new post" notification message to all users watching the topic.
	 * 
	 * @param t The changed topic
	 * @param tm A TopicModel instance
	 * @throws Exception
	 */
	public static void notifyUsers(Topic t, Post p, TopicDAO tm, List attachments) throws Exception
	{
		if (SystemGlobals.getBoolValue(ConfigKeys.MAIL_NOTIFY_ANSWERS)) {
			try {
				List usersToNotify = tm.notifyUsers(t);
				
				// we only have to send an email if there are users
				// subscribed to the topic
				if (usersToNotify != null && usersToNotify.size() > 0) 
				{
					// Remove in active users from the list
					for (Iterator iter = usersToNotify.iterator(); iter.hasNext();) 
					{
						User usr = (User) iter.next();
						
						if (!JForumUserUtil.isUserActive(usr.getSakaiUserId()))
						{
							iter.remove();
							continue;
						}
					}
					
					if (attachments != null && attachments.size() > 0) 
					{
						QueuedExecutor.getInstance().execute(
								new EmailSenderTask(new TopicSpammer(t, usersToNotify, ConfigKeys.MAIL_NEW_ANSWER_MESSAGE_FILE, p, attachments)));
					}
					else
					{
						QueuedExecutor.getInstance().execute(
								new EmailSenderTask(new TopicSpammer(t, usersToNotify, ConfigKeys.MAIL_NEW_ANSWER_MESSAGE_FILE, p)));
					}
				}
			}
			catch (Exception e) {
				logger.warn("Error while sending notification emails: " + e);
			}
		}
	}
	

	
	
	/**
	 * Sends a "new post" notification message to all users 
	 * whose profile has "Notify me when new topic is posted" selected 
	 * @param t The changed topic
	 * @throws Exception
	 */
	public static void notifyNewTopicToUsers(Topic t, Post p, List attachments) throws Exception
	{
		if (SystemGlobals.getBoolValue(ConfigKeys.MAIL_NOTIFY_ANSWERS)) {
			
			if (t != null) {
				List usersToNotify = null;
				try {
					usersToNotify = JForumUserUtil.updateMembersInfo();
					
					//notify only to the users who belong to the group(s) associated with forum
					Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(t.getForumId());
					boolean sendToGroups = false;
					Site site = null;
					if ((forum.getAccessType() == Forum.ACCESS_GROUPS) && (forum.getGroups() != null && forum.getGroups().size() > 0)) 
					{
						sendToGroups = true;
						site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
					}
					
					//remove the user who posted the message and anonymous user
					int posterId = SessionFacade.getUserSession().getUserId();
					int anonUser = SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID);
					
					for (Iterator iter = usersToNotify.iterator(); iter.hasNext();) {
						User usr = (User) iter.next();
						
						if (usr == null) {
							if (logger.isWarnEnabled()) logger.warn("A 'null' user turned up in the membership of Forum : " + t.getForumId());
							continue;
						}
							
						if (usr.getId() == posterId || usr.getId() == anonUser){
							iter.remove();
							continue;
						}
						
						//notify only to the users who belong to the group(s) associated with forum
						if (sendToGroups && !JForumUserUtil.isJForumFacilitator(usr.getSakaiUserId())) 
						{
							boolean userInGroup = false;
							
							Collection sakaiSiteGroups = site.getGroupsWithMember(usr.getSakaiUserId());
							
							for (Iterator usrGrpIter = sakaiSiteGroups.iterator(); usrGrpIter.hasNext(); ) {
								org.sakaiproject.site.api.Group grp = (org.sakaiproject.site.api.Group)usrGrpIter.next();
								if (forum.getGroups().contains(grp.getId())) {
									userInGroup = true;
									break;
								}
							}
							
							if (!userInGroup)
							{
								iter.remove();
								continue;
							}
						}
						
						if (!usr.isNotifyOnMessagesEnabled() || 
								(usr.getEmail() == null || usr.getEmail().trim().length() == 0) || (!JForumUserUtil.isUserActive(usr.getSakaiUserId()))){
							iter.remove();
							continue;
						}
						
						try
						{
							new InternetAddress(usr.getEmail());
						} 
						catch (AddressException e)
						{
							if (logger.isWarnEnabled()) logger.warn("notifyNewTopicToUsers(...) : "+ usr.getEmail() + " is invalid. And exception is : "+ e);
							iter.remove();
							continue;
						}
					}
					
					/* send an email only to users whose profile has "Notify me when new topic is posted" selected*/ 
					if (usersToNotify != null && usersToNotify.size() > 0) 
					{
						if (attachments != null && attachments.size() > 0) 
						{
							QueuedExecutor.getInstance().execute(
									new EmailSenderTask(new TopicSpammer(t, p, usersToNotify, ConfigKeys.MAIL_NEW_TOPIC_MESSAGE_FILE, attachments)));
						}
						else 
						{
							
							QueuedExecutor.getInstance().execute(
								new EmailSenderTask(new TopicSpammer(t, p, usersToNotify, ConfigKeys.MAIL_NEW_TOPIC_MESSAGE_FILE)));
						}
					}
				} catch (Exception e) {
					if (logger.isWarnEnabled()) logger.warn("Error while sending notification emails in notifyNewTopicToUsers() : " + e);
				}
			} else {
				if (logger.isWarnEnabled()) logger.warn("Attempt to notify users that a 'null' topic was created.");
				throw new IllegalArgumentException("Attempt to notify users that a 'null' topic was created.");
 			}
		}
	}
	
	
	
	
	/**
	 * Updates the board status after a new post is inserted.
	 * This method is used in conjunct with moderation manipulation. 
	 * It will increase by 1 the number of replies of the tpoic, set the
	 * last post id for the topic and the forum and refresh the cache. 
	 * 
	 * @param t The topic to update
	 * @param lastPostId The id of the last post
	 * @param tm A TopicModel instance
	 * @param fm A ForumModel instance
	 * @throws Exception
	 */
	public static void updateBoardStatus(Topic t, int lastPostId, boolean firstPost, TopicDAO tm, ForumDAO fm) throws Exception
	{
		t.setLastPostId(lastPostId);
		tm.update(t);
		
		fm.setLastPost(t.getForumId(), lastPostId);
		
		if (!firstPost) {
			tm.incrementTotalReplies(t.getId());
		}
		else {
			fm.incrementTotalTopics(t.getForumId(), 1);
		}


		ForumRepository.reloadForum(t.getForumId());
		TopicRepository.clearCache(t.getForumId());

		// Updates cache for latest topic
		TopicRepository.pushTopic(tm.selectById(t.getId()));
	}
	
	/**
	 * Deletes a topic.
	 * This method will remove the topic from the database,
	 * clear the entry frm the cache and update the last 
	 * post info for the associated forum.
	 * @param topicId The topic id to remove
	 * @param fromModeration TODO
	 * 
	 * @throws Exception
	 */
	public static void deleteTopic(int topicId, int forumId, boolean fromModeration) throws Exception
	{
		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
		ForumDAO fm = DataAccessDriver.getInstance().newForumDAO();
		
		Topic topic = new Topic();
		topic.setId(topicId);
		tm.delete(topic);

		if (!fromModeration) {
			// Updates the Recent Topics if it contains this topic
			TopicRepository.popTopic(topic);
			TopicRepository.loadMostRecentTopics();
	
			tm.removeSubscriptionByTopic(topicId);
			fm.decrementTotalTopics(forumId, 1);
		}
	}
}
