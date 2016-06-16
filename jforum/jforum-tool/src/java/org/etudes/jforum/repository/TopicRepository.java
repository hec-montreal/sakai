/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/repository/TopicRepository.java $ 
 * $Id: TopicRepository.java 61928 2009-07-21 22:29:16Z murthy@etudes.org $ 
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
package org.etudes.jforum.repository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.etudes.jforum.cache.CacheEngine;
import org.etudes.jforum.cache.Cacheable;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.TopicDAO;
import org.etudes.jforum.entities.Topic;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;


/**
 * Repository for the last n topics for each forum.
 * 
 * @author Rafael Steil
 * @author James Yong
 */
public class TopicRepository implements Cacheable
{
	// private static int maxItems = SystemGlobals.getIntValue(ConfigKeys.TOPICS_PER_PAGE);
	private static int maxItems = SakaiSystemGlobals.getIntValue(ConfigKeys.TOPICS_PER_PAGE);
	public static final String FQN = "topics";
	private static final String RECENT = "recent";
	public static final String FQN_FORUM = FQN + "/byforum";
	private static CacheEngine cache;
	
	/**
	 * @see org.etudes.jforum.cache.Cacheable#setCacheEngine(org.etudes.jforum.cache.CacheEngine)
	 */
	public void setCacheEngine(CacheEngine engine)
	{
		cache = engine;
	}

	/**
	 * Add topic to the FIFO stack
	 * 
	 * @param topic The topic to add to stack
	 */
	public synchronized static void pushTopic(Topic topic) throws Exception
	{
		if (SystemGlobals.getBoolValue(ConfigKeys.TOPIC_CACHE_ENABLED)) {
			// int limit = SystemGlobals.getIntValue(ConfigKeys.RECENT_TOPICS);
			int limit = SakaiSystemGlobals.getIntValue(ConfigKeys.RECENT_TOPICS);
			
			LinkedList l = (LinkedList)cache.get(FQN, RECENT);
			if (l == null || l.size() == 0) {
				l = new LinkedList(loadMostRecentTopics());
			}
			
			l.remove(topic);
			l.addFirst(topic);
			
			while (l.size() > limit)
			{
				l.removeLast();
			}
			
			cache.add(FQN, RECENT, l);
		}
	}

	/**
	 * Remove topic to the FIFO stack
	 * 
	 * @param topic The topic to remove from stack
	 */
	public synchronized static void popTopic(Topic topic) throws Exception
	{
		if (SystemGlobals.getBoolValue(ConfigKeys.TOPIC_CACHE_ENABLED)) {
			List l = (List)cache.get(FQN, RECENT);
			if (l == null || l.size() == 0) {
				l = new LinkedList(loadMostRecentTopics());
			}
			
			l.remove(topic);
			cache.add(FQN, RECENT, l);
		}
	}	

	/**
	 * Get all cached recent topics. 
	 * 
	 */	
	public static List getRecentTopics() throws Exception
	{
		List l = (List)cache.get(FQN, RECENT);
		if (l == null || l.size() == 0
				|| !SystemGlobals.getBoolValue(ConfigKeys.TOPIC_CACHE_ENABLED)) {
			l = loadMostRecentTopics();
		}
		
		return new ArrayList(l);
	}	

	/**
	 * Add recent topics to the cache
	 */
	public static List loadMostRecentTopics() throws Exception
	{
		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
		// int limit = SystemGlobals.getIntValue(ConfigKeys.RECENT_TOPICS);
		int limit = SakaiSystemGlobals.getIntValue(ConfigKeys.RECENT_TOPICS);
		
		List l = tm.selectRecentTopics(limit);
		if (SystemGlobals.getBoolValue(ConfigKeys.TOPIC_CACHE_ENABLED)) {
			cache.add(FQN, RECENT, new LinkedList(l));
		}
		
		return l;
	}
	/**
	 * Add topics to the cache
	 * 
	 * @param forumId The forum id to which the topics are related
	 * @param topics The topics to add
	 */
	public synchronized static void addAll(int forumId, List topics)
	{
		cache.add(FQN_FORUM, Integer.toString(forumId), new LinkedList(topics));
	}
	
	/**
	 * Clears the cache
	 * 
	 * @param forumId The forum id to clear the cache
	 */
	public synchronized static void clearCache(int forumId) throws Exception
	{
		if (SystemGlobals.getBoolValue(ConfigKeys.TOPIC_CACHE_ENABLED)) {
			cache.add(FQN_FORUM, Integer.toString(forumId), new LinkedList());
		}
	}
	
	/**
	 * Adds a new topic to the cache
	 * 
	 * @param topic The topic to add
	 */
	public synchronized static void addTopic(Topic topic)
	{
		if (SystemGlobals.getBoolValue(ConfigKeys.TOPIC_CACHE_ENABLED)) {
			String forumId = Integer.toString(topic.getForumId());
			LinkedList list = (LinkedList)cache.get(FQN_FORUM, forumId);
			
			if (list == null) {
				list = new LinkedList();
				list.add(topic);
			}
			else {
				if (list.size() + 1 > maxItems) {
					list.removeLast();
				}
				
				list.addFirst(topic);
			}
			
			cache.add(FQN_FORUM, forumId, list);
		}
	}
	
	/**
	 * Updates a cached topic
	 * 
	 * @param topic The topic to update
	 */
	public synchronized static void updateTopic(Topic topic)
	{
		String forumId = Integer.toString(topic.getForumId());
		List l = (List)cache.get(FQN_FORUM, forumId);
		
		if (l != null) {
			int index = l.indexOf(topic);
			if (index > -1) {
				l.set(index, topic);
				cache.add(FQN_FORUM, forumId, l);
			}
		}
	}
	
	/**
	 * Checks if a topic is cached
	 * 
	 * @param topic The topic to verify
	 * @return <code>true</code> if the topic is cached, or <code>false</code> if not.
	 */
	public static boolean isTopicCached(Topic topic)
	{
		return ((List)cache.get(FQN_FORUM, Integer.toString(topic.getForumId()))).contains(topic);
	}
	
	/**
	 * Get all cached topics related to a forum. 
	 * 
	 * @param forumid The forum id 
	 * @return <code>ArrayList</code> with the topics.
	 */
	public static List getTopics(int forumid)
	{
		if (SystemGlobals.getBoolValue(ConfigKeys.TOPIC_CACHE_ENABLED)) {
			List returnList = (List)cache.get(FQN_FORUM, Integer.toString(forumid));
			if (returnList == null) {
				return new ArrayList();
			}
			
			return new ArrayList(returnList);
		}
		
		return new ArrayList();
	}
}
