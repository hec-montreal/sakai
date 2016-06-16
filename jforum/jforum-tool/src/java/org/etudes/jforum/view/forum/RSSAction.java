/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/forum/RSSAction.java $ 
 * $Id: RSSAction.java 55473 2008-12-01 18:49:13Z murthy@etudes.org $ 
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
package org.etudes.jforum.view.forum;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.etudes.jforum.ActionServletRequest;
import org.etudes.jforum.Command;
import org.etudes.jforum.JForum;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.PostDAO;
import org.etudes.jforum.dao.TopicDAO;
import org.etudes.jforum.entities.Forum;
import org.etudes.jforum.entities.Topic;
import org.etudes.jforum.repository.ForumRepository;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.preferences.TemplateKeys;
import org.etudes.jforum.util.rss.ForumRSS;
import org.etudes.jforum.util.rss.RSSAware;
import org.etudes.jforum.util.rss.RecentTopicsRSS;
import org.etudes.jforum.util.rss.TopicPostsRSS;
import org.etudes.jforum.util.rss.TopicRSS;
import org.etudes.jforum.view.forum.common.ForumCommon;
import org.etudes.jforum.view.forum.common.TopicsCommon;

import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 * @author Rafael Steil
 * Mallika - 9/27/06 - eliminating views column
 */
public class RSSAction extends Command 
{
	/**
	 * RSS for all forums.
	 * Show rss syndication containing information about
	 * all available forums
	 * @throws Exception
	 */
	public void forums() throws Exception
	{
		String title = I18n.getMessage("RSS.Forums.title");
		String description = I18n.getMessage("RSS.Forums.description");
		
		RSSAware rss = new ForumRSS(title, description, ForumCommon.getAllCategoriesAndForums(false));
		this.context.put("rssContents", rss.createRSS());
	}
	
	/**
	 * RSS for all N first topics for some given forum
	 * @throws Exception
	 */
	public void forumTopics() throws Exception
	{
		int forumId = this.request.getIntParameter("forum_id"); 
		if (!TopicsCommon.isTopicAccessible(forumId)) {
			return;
		}
		
		List topics = TopicsCommon.topicsByForum(forumId, 0);
		Forum forum = ForumRepository.getForum(forumId);
		
		String[] p = { forum.getName() };
		
		RSSAware rss = new TopicRSS(I18n.getMessage("RSS.ForumTopics.title", p),
				I18n.getMessage("RSS.ForumTopics.description", p),
				forumId, 
				topics);
		this.context.put("rssContents", rss.createRSS());
	}
	
	/**
	 * RSS for all N first posts for some given topic
	 * @throws Exception
	 */
	public void topicPosts() throws Exception
	{
		int topicId = this.request.getIntParameter("topic_id");

		PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
		
		Topic topic = tm.selectById(topicId);
		
		if (!TopicsCommon.isTopicAccessible(topic.getForumId()) || topic.getId() == 0) {
			return;
		}
		
		
		List posts = pm.selectAllByTopic(topicId);
		
		String[] p = { topic.getTitle() };
		
		String title = I18n.getMessage("RSS.TopicPosts.title", p);
		String description = I18n.getMessage("RSS.TopicPosts.description", p);

		RSSAware rss = new TopicPostsRSS(title, description, topic.getForumId(), posts);
		this.context.put("rssContents", rss.createRSS());
	}
	
	public void recentTopics() throws Exception
	{
		String title = I18n.getMessage("RSS.RecentTopics.title");
		String description = I18n.getMessage("RSS.RecentTopics.description");

		RSSAware rss = new RecentTopicsRSS(title, description, 
				new RecentTopicsAction().topics());
		this.context.put("rssContents", rss.createRSS());
	}
	
	/** 
	 * @see org.etudes.jforum.Command#list()
	 */
	public void list() throws Exception 
	{
		this.forums();
	}
	
	/** 
	 * @see org.etudes.jforum.Command#process()
	 */
	public Template process(ActionServletRequest request, 
			HttpServletResponse response, 
			SimpleHash context) throws Exception 
	{
		JForum.setContentType("text/xml");
		super.setTemplateName(TemplateKeys.RSS);

		return super.process(request, response, context);
	}

}
