/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/forum/RecentTopicsAction.java $ 
 * $Id: RecentTopicsAction.java 71014 2010-10-26 20:49:34Z murthy@etudes.org $ 
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
package org.etudes.jforum.view.forum;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.etudes.jforum.Command;
import org.etudes.jforum.JForum;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.entities.Category;
import org.etudes.jforum.entities.Forum;
import org.etudes.jforum.entities.Topic;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.repository.ForumRepository;
import org.etudes.jforum.repository.TopicRepository;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.TemplateKeys;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.etudes.jforum.view.forum.common.TopicsCommon;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.user.cover.UserDirectoryService;


/**
 * Display a list of recent Topics
 * 
 * @author James Yong
 * @author Rafael Steil
 * 11/03/05 Murthy - Updated topics() to show recent topics page and to avoid message
 *				     "Oooops. You don't have sufficient privileges to access this topic" 
 */
public class RecentTopicsAction extends Command 
{
	private List forums;
	private List categories;

	public void list() throws Exception
	{
		int postsPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		
		this.context.put("postsPerPage", new Integer(postsPerPage));
		this.context.put("topics", this.topics());
		this.context.put("forums", this.forums);
		this.context.put("categories", this.categories);
		this.setTemplateName(TemplateKeys.RECENT_LIST);
		
		TopicsCommon.topicListingBase();
	}
	
	List topics() throws Exception
	{
		int postsPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		List tmpTopics = TopicRepository.getRecentTopics();
		
		this.forums = new ArrayList(postsPerPage);
		this.categories = new ArrayList(postsPerPage);
		
		String sakaiUserId = UserDirectoryService.getCurrentUser().getId();
		User user = DataAccessDriver.getInstance().newUserDAO().selectBySakaiUserId(sakaiUserId);

		for (Iterator iter = tmpTopics.iterator(); iter.hasNext(); ) {
			Topic t = (Topic)iter.next();
			if (TopicsCommon.isTopicAccessible(t.getForumId())) {
				
				// Get name of forum that the topic refers to
				Forum f = ForumRepository.getForum(t.getForumId());
				Category c = ForumRepository.getCategory(f.getCategoryId());
				
				if(!ForumRepository.isCategoryAccessible(c, user.getId()))
				{
					iter.remove();
					continue;
				}
				
				forums.add(f);
								
				categories.add(c);
			}
			else {
				iter.remove();
				//11/03/05 Murthy - Added to show recent topics page and to avoid message
				//"Oooops. You don't have sufficient privileges to access this topic"
				JForum.getRequest().setAttribute("template", null);
			}
		}
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		this.context.put("facilitator", isfacilitator);
		
		return TopicsCommon.prepareTopics(tmpTopics);
	}
}
