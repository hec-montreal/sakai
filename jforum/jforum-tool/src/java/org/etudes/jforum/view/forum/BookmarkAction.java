/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/forum/BookmarkAction.java $ 
 * $Id: BookmarkAction.java 67503 2010-05-05 00:10:26Z murthy@etudes.org $ 
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


import javax.servlet.http.HttpServletResponse;

import org.etudes.jforum.ActionServletRequest;
import org.etudes.jforum.Command;
import org.etudes.jforum.JForum;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.dao.BookmarkDAO;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.entities.Bookmark;
import org.etudes.jforum.entities.BookmarkType;
import org.etudes.jforum.entities.Forum;
import org.etudes.jforum.entities.Topic;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.repository.ForumRepository;
import org.etudes.jforum.security.SecurityConstants;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.preferences.TemplateKeys;

//import net.jforum.repository.SecurityRepository;
import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 * @author Rafael Steil
 */
public class BookmarkAction extends Command
{
	public void insert() throws Exception
	{
		int type = this.request.getIntParameter("relation_type");
		if (type == BookmarkType.FORUM) {
			this.addForum();
		}
		else if (type == BookmarkType.TOPIC) {
			this.addTopic();
		}
		else if (type == BookmarkType.USER) {
			this.addUser();
		}
		else {
			this.error("Bookmarks.invalidType");
		}
	}
	
	private void addForum() throws Exception
	{
		Forum f = ForumRepository.getForum(this.request.getIntParameter("relation_id"));
		String title = f.getName();
		String description = f.getDescription();
		
		Bookmark b = DataAccessDriver.getInstance().newBookmarkDAO().selectForUpdate(
				f.getId(), BookmarkType.FORUM, SessionFacade.getUserSession().getUserId());
		if (b != null) {
			if (b.getTitle() != null) {
				title = b.getTitle();
			}
			
			if (b.getDescription() != null) {
				description = b.getDescription();
			}

			this.context.put("bookmark", b);
		}
		
		this.setTemplateName(TemplateKeys.BOOKMARKS_ADD_FORUM);
		this.context.put("title", title);
		this.context.put("description", description);
		this.context.put("relationType", new Integer(BookmarkType.FORUM));
		this.context.put("relationId", new Integer(f.getId()));
	}
	
	private void addTopic() throws Exception
	{
		Topic t = DataAccessDriver.getInstance().newTopicDAO().selectById(
				this.request.getIntParameter("relation_id"));
		String title = t.getTitle();
		
		Bookmark b = DataAccessDriver.getInstance().newBookmarkDAO().selectForUpdate(
				t.getId(), BookmarkType.TOPIC, SessionFacade.getUserSession().getUserId());
		if (b != null) {
			if (b.getTitle() != null) {
				title = b.getTitle();
			}
			
			this.context.put("description", b.getDescription());
			this.context.put("bookmark", b);
		}
		
		this.setTemplateName(TemplateKeys.BOOKMARS_ADD_TOPIC);
		this.context.put("title", title);
		this.context.put("relationType", new Integer(BookmarkType.TOPIC));
		this.context.put("relationId", new Integer(t.getId()));
	}
	
	private void addUser() throws Exception
	{
		User u = DataAccessDriver.getInstance().newUserDAO().selectById(
				this.request.getIntParameter("relation_id"));
		String title = u.getUsername();
		
		Bookmark b = DataAccessDriver.getInstance().newBookmarkDAO().selectForUpdate(
				u.getId(), BookmarkType.USER, SessionFacade.getUserSession().getUserId());
		if (b != null) {
			if (b.getTitle() != null) {
				title = b.getTitle();
			}
			
			this.context.put("description", b.getDescription());
			this.context.put("bookmark", b);
		}
		
		this.setTemplateName(TemplateKeys.BOOKMARKS_ADD_USER);
		this.context.put("title", title);
		this.context.put("relationType", new Integer(BookmarkType.USER));
		this.context.put("relationId", new Integer(u.getId()));
	}
	
	public void insertSave() throws Exception
	{
		Bookmark b = new Bookmark();
		b.setDescription(this.request.getParameter("description"));
		b.setTitle(this.request.getParameter("title"));
		b.setPublicVisible(this.request.getParameter("visible") != null);
		b.setRelationId(this.request.getIntParameter("relation_id"));
		b.setRelationType(this.request.getIntParameter("relation_type"));
		b.setUserId(SessionFacade.getUserSession().getUserId());
		
		DataAccessDriver.getInstance().newBookmarkDAO().add(b);
		this.setTemplateName(TemplateKeys.BOOKMARKS_INSERT_SAVE);
	}
	
	public void updateSave() throws Exception
	{
		int id = this.request.getIntParameter("bookmark_id");
		BookmarkDAO bm = DataAccessDriver.getInstance().newBookmarkDAO();
		Bookmark b = bm.selectById(id);
		
		if (!this.sanityCheck(b)) {
			return;
		}
		
		b.setTitle(this.request.getParameter("title"));
		b.setDescription(this.request.getParameter("description"));
		
		String visible = this.request.getParameter("visible");
		b.setPublicVisible(visible != null && !"".equals(visible) ? true : false);
		
		bm.update(b);
		this.setTemplateName(TemplateKeys.BOOKMARKS_UPDATE_SAVE);
		
		this.context.put("updatesuccess", true);
	}
	
	public void edit() throws Exception
	{
		int id = this.request.getIntParameter("bookmark_id");
		BookmarkDAO bm = DataAccessDriver.getInstance().newBookmarkDAO();
		Bookmark b = bm.selectById(id);
		
		if (!this.sanityCheck(b)) {
			return;
		}
		
		this.setTemplateName(TemplateKeys.BOOKMARKS_EDIT);
		this.context.put("bookmark", b);
	}
	
	public void delete() throws Exception
	{
		int id = this.request.getIntParameter("bookmark_id");
		BookmarkDAO bm = DataAccessDriver.getInstance().newBookmarkDAO();
		Bookmark b = bm.selectById(id);
		
		if (!this.sanityCheck(b)) {
			return;
		}
		
		bm.remove(id);
		
		JForum.setRedirect(this.request.getContextPath() + "/bookmarks/list/" + b.getUserId()
				+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
	}
	
	private boolean sanityCheck(Bookmark b)
	{
		if (b == null) {
			this.error("Bookmarks.notFound");
			return false;
		}
		
		if (b.getUserId() != SessionFacade.getUserSession().getUserId()) {
			this.error("Bookmarks.notOwner");
			return false;
		}
		
		return true;
	}
	
	private void error(String message)
	{
		this.setTemplateName(TemplateKeys.BOOKMARKS_ERROR);
		this.context.put("message", I18n.getMessage(message));
	}
	
	public void disabled()
	{
		this.error("Bookmarks.featureDisabled");
	}
	
	public void anonymousIsDenied()
	{
		this.error("Bookmarks.anonymousIsDenied");
	}

	/**
	 * @see org.etudes.jforum.Command#list()
	 */
	public void list() throws Exception
	{
		int userId = this.request.getIntParameter("user_id");
		
		this.setTemplateName(TemplateKeys.BOOKMARKS_LIST);
		this.context.put("bookmarks", DataAccessDriver.getInstance().newBookmarkDAO().selectByUser(userId));
		this.context.put("forumType", new Integer(BookmarkType.FORUM));
		this.context.put("userType", new Integer(BookmarkType.USER));
		this.context.put("topicType", new Integer(BookmarkType.TOPIC));
		this.context.put("user", DataAccessDriver.getInstance().newUserDAO().selectById(userId));
		this.context.put("loggedUserId", new Integer(SessionFacade.getUserSession().getUserId()));
	}
	
	/**
	 * @see org.etudes.jforum.Command#process(org.etudes.jforum.ActionServletRequest, javax.servlet.http.HttpServletResponse, freemarker.template.SimpleHash)
	 */
	public Template process(ActionServletRequest request, HttpServletResponse response, SimpleHash context) throws Exception
	{
		if (SessionFacade.getUserSession().getUserId() == SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)
				&& !request.getAction().equals("list")) {
			request.addParameter("action", "anonymousIsDenied");
		}
		/*else if (!SecurityRepository.canAccess(SecurityConstants.PERM_BOOKMARKS_ENABLED)) {
			request.addParameter("action", "disabled");
		}*/

		return super.process(request, response, context);
	}
}
