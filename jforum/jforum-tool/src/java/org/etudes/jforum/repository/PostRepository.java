/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/repository/PostRepository.java $ 
 * $Id: PostRepository.java 55479 2008-12-01 19:47:26Z murthy@etudes.org $ 
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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.cache.CacheEngine;
import org.etudes.jforum.cache.Cacheable;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.PostDAO;
import org.etudes.jforum.entities.Post;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.view.forum.common.PostCommon;

/**
 * Repository for the post in the top n topics for each forum.
 * 
 * @author Sean Mitchell
 * @author Rafael Steil
 */
public class PostRepository implements Cacheable
{
	private static Log logger = LogFactory.getLog(PostRepository.class);
	private static final int CACHE_SIZE = SystemGlobals.getIntValue(ConfigKeys.POSTS_CACHE_SIZE);
	public static final String FQN = "posts";
	private static CacheEngine cache;
	
	/**
	 * @see org.etudes.jforum.cache.Cacheable#setCacheEngine(org.etudes.jforum.cache.CacheEngine)
	 */
	public void setCacheEngine(CacheEngine engine)
	{
		cache = engine;
	}
	
	public static int size()
	{
		Map m = (Map)cache.get(FQN);
		return (m != null ? m.size() : 0);
	}
	
	public static int size(int topicId)
	{
		List posts = (List)cache.get(FQN, Integer.toString(topicId));
		return (posts == null ? 0 : posts.size());
	}
	
	public static Collection cachedTopics()
	{
		Map m = (Map)cache.get(FQN);
		if (m == null) {
			return new ArrayList();
		}
		
		return m.keySet();
	}
		
	public static List selectAllByTopicByLimit(int topicId, int start, int count) throws Exception 
	{
		String tid = Integer.toString(topicId);
		
		List posts = (List)cache.get(FQN, tid);
		if (posts == null || posts.size() == 0) {
			PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
			posts = pm.selectAllByTopic(topicId);
			
			for (Iterator iter = posts.iterator(); iter.hasNext(); ) {
				PostCommon.preparePostForDisplay((Post)iter.next());
			}
	
			Object obj = cache.get(FQN);
			if(obj == null) {
				if (logger.isDebugEnabled()) logger.debug("+++++++++++++++++ cache.get('posts') is null");
			} else {
				if (logger.isDebugEnabled()) logger.debug("+++++++++++++++++" + cache.get(FQN).getClass());
				if (logger.isDebugEnabled()) logger.debug("+++++++++++++++++" + cache.get(FQN).getClass().getName());
				if (logger.isDebugEnabled()) logger.debug("+++++++++++++++++" + cache.get(FQN));
			}
			Map topics = (Map)cache.get(FQN);
			if (topics == null || topics.size() == 0 || topics.size() < CACHE_SIZE) {
				cache.add(FQN, tid, posts);
			}
			else {
				if (!(topics instanceof LinkedHashMap)) {
					topics = new LinkedHashMap(topics) {
						protected boolean removeEldestEntry(java.util.Map.Entry eldest) {
							return this.size() > CACHE_SIZE;
						}
					};
				}
				
				topics.put(tid, posts);
				cache.add(FQN, topics);
			}
		}
		
		int size = posts.size();
		return posts.subList(start, (size < start + count) ? size : start + count);
   }
	
	public static void update(int topicId, Post p)
	{
		String tid = Integer.toString(topicId);
		List posts = (List)cache.get(FQN, tid);
		if (posts != null && posts.contains(p)) {
			posts.set(posts.indexOf(p), p);
			cache.add(FQN, tid, posts);
		}
	}
	
	public static void append(int topicId, Post p)
	{
		String tid = Integer.toString(topicId);
		List posts = (List)cache.get(FQN, tid);
		if (posts != null && !posts.contains(p)) {
			posts.add(p);
			cache.add(FQN, tid, posts);
		}
	}
	
	public static void clearCache(int topicId)
	{
		if (SystemGlobals.getBoolValue(ConfigKeys.POSTS_CACHE_ENABLED)) {
			cache.remove(FQN, Integer.toString(topicId));
		}
	}
}

