/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/admin/ModerationAction.java $ 
 * $Id: ModerationAction.java 61928 2009-07-21 22:29:16Z murthy@etudes.org $ 
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
package org.etudes.jforum.view.admin;

import org.etudes.jforum.ActionServletRequest;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.PostDAO;
import org.etudes.jforum.dao.TopicDAO;
import org.etudes.jforum.entities.Post;
import org.etudes.jforum.entities.Topic;
import org.etudes.jforum.repository.ForumRepository;
import org.etudes.jforum.repository.PostRepository;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.preferences.TemplateKeys;
import org.etudes.jforum.view.forum.common.AttachmentCommon;
import org.etudes.jforum.view.forum.common.PostCommon;
import org.etudes.jforum.view.forum.common.TopicsCommon;
import org.etudes.jforum.view.forum.common.ViewCommon;

import freemarker.template.SimpleHash;

/**
 * @author Rafael Steil
 */
public class ModerationAction extends AdminCommand
{
	public ModerationAction() {}
	
	public ModerationAction(SimpleHash context, ActionServletRequest request)
	{
		this.context = context;
		this.request = request;
	}
	
	/**
	 * @see org.etudes.jforum.Command#list()
	 */
	public void list() throws Exception
	{
		this.setTemplateName(TemplateKeys.MODERATION_ADMIN_LIST);
		this.context.put("infoList", DataAccessDriver.getInstance().newModerationDAO().categoryPendingModeration());
	}
	
	public void view() throws Exception
	{
		int forumId = this.request.getIntParameter("forum_id");
		
		int start = ViewCommon.getStartPage();
		// int count = SystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		int count = SakaiSystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		
		this.setTemplateName(TemplateKeys.MODERATION_ADMIN_VIEW);
		this.context.put("forum", ForumRepository.getForum(forumId));
		this.context.put("topics", DataAccessDriver.getInstance().newModerationDAO().topicsByForum(
				forumId));
	}
	
	public void doSave() throws Exception
	{
		String[] posts = this.request.getParameterValues("post_id");
		if (posts != null) {
			TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
			
			for (int i = 0; i < posts.length; i++) {
				int postId = Integer.parseInt(posts[i]);
				
				String status = this.request.getParameter("status_" + postId);
				if ("defer".startsWith(status)) {
					continue;
				}
				
				if ("aprove".startsWith(status)) {
					Post p = DataAccessDriver.getInstance().newPostDAO().selectById(postId);
					
					// Check is the post is in fact waiting moderation
					if (!p.isModerationNeeded()) {
						continue;
					}
					
					Topic t = tm.selectRaw(p.getTopicId());
					
					DataAccessDriver.getInstance().newModerationDAO().aprovePost(postId);
					TopicsCommon.updateBoardStatus(t, postId, t.getFirstPostId() == postId,
							tm, DataAccessDriver.getInstance().newForumDAO());
					
					
					DataAccessDriver.getInstance().newUserDAO().incrementPosts(p.getUserId());
					
					if (SystemGlobals.getBoolValue(ConfigKeys.POSTS_CACHE_ENABLED)) {
						PostRepository.append(p.getTopicId(), PostCommon.preparePostForDisplay(p));
					}
				}
				else {
					PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
					Post post = pm.selectById(postId);
					
					if (post == null || !post.isModerationNeeded()) {
						continue;
					}
					
					pm.delete(post);
					
					new AttachmentCommon(this.request, post.getForumId()).deleteAttachments(postId, post.getForumId());
					
					int totalPosts = tm.getTotalPosts(post.getTopicId());
					
					if (totalPosts == 0) {
						TopicsCommon.deleteTopic(post.getTopicId(), post.getForumId(), true);
					}
				}
			}
		}
	}
	
	public void save() throws Exception
	{
		this.doSave();
		this.view();
	}
}
