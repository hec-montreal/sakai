/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/GenericBookmarkDAO.java $ 
 * $Id: GenericBookmarkDAO.java 55493 2008-12-01 22:55:48Z murthy@etudes.org $ 
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
package org.etudes.jforum.dao.generic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


import org.etudes.jforum.JForum;
import org.etudes.jforum.entities.Bookmark;
import org.etudes.jforum.entities.BookmarkType;
import org.etudes.jforum.exceptions.InvalidBookmarkTypeException;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.sakaiproject.tool.cover.ToolManager;
//Mallika's import end

/**
 * @author Rafael Steil
 * 10/13/05 - Mallika - adding courseId to the list equation
 *
 * 5/23/06 - Howie - revised getForums() method to make sure no null (could be empty though) description for a forum.
 * 
 */
public class GenericBookmarkDAO implements org.etudes.jforum.dao.BookmarkDAO
{
	/**
	 * @see org.etudes.jforum.dao.BookmarkDAO#add(org.etudes.jforum.entities.Bookmark)
	 */
	public void add(Bookmark b) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("BookmarkModel.add"));
		p.setInt(1, b.getUserId());
		p.setInt(2, b.getRelationId());
		p.setInt(3, b.getRelationType());
		p.setInt(4, b.isPublicVisible() ? 1 : 0);
		p.setString(5, b.getTitle());
		p.setString(6, b.getDescription());
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.BookmarkDAO#update(org.etudes.jforum.entities.Bookmark)
	 */
	public void update(Bookmark b) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("BookmarkModel.update"));
		p.setInt(1, b.isPublicVisible() ? 1 : 0);
		p.setString(2, b.getTitle());
		p.setString(3, b.getDescription());
		p.setInt(4, b.getId());
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.BookmarkDAO#remove(int)
	 */
	public void remove(int bookmarkId) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("BookmarkModel.remove"));
		p.setInt(1, bookmarkId);
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.BookmarkDAO#selectByUser(int, int)
	 */
	public List selectByUser(int userId, int relationType) throws Exception
	{
		if (relationType == BookmarkType.FORUM) {
			return this.getForums(userId);
		}
		else if (relationType == BookmarkType.TOPIC) {
			return this.getTopics(userId);
		}
		else if (relationType == BookmarkType.USER) {
			return this.getUsers(userId);
		}
		else {
			throw new InvalidBookmarkTypeException("The type " + relationType 
					+ " is not a valid bookmark type");
		}
	}
	
	/**
	 * @see org.etudes.jforum.dao.BookmarkDAO#selectByUser(int)
	 */
	public List selectByUser(int userId) throws Exception
	{
		List l = new ArrayList();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("BookmarkModel.selectAllFromUser"));
		p.setInt(1, userId);
		
		//Mallika - new code beg
		p.setInt(2, userId);
		p.setString(3, ToolManager.getCurrentPlacement().getContext());
		//Mallika - new code end
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			l.add(this.getBookmark(rs));
		}
		
		rs.close();
		p.close();
		
		return l;
	}
	
	/**
	 * @see org.etudes.jforum.dao.BookmarkDAO#selectById(int)
	 */
	public Bookmark selectById(int bookmarkId) throws Exception
	{
		Bookmark b = null;
		
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("BookmarkModel.selectById"));
		p.setInt(1, bookmarkId);
		
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			b = this.getBookmark(rs);
		}
		
		rs.close();
		p.close();
		
		return b;
	}
	
	/**
	 * @see org.etudes.jforum.dao.BookmarkDAO#selectForUpdate(int, int, int)
	 */
	public Bookmark selectForUpdate(int relationId, int relationType, int userId) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("BookmarkModel.selectForUpdate"));
		p.setInt(1, relationId);
		p.setInt(2, relationType);
		p.setInt(3, userId);
		
		Bookmark b = null;
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			b = this.getBookmark(rs);
		}
		
		rs.close();
		p.close();
		
		return b;
	}
	
	protected List getUsers(int userId) throws Exception
	{
		List l = new ArrayList();
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("BookmarkModel.selectUserBookmarks"));
		p.setInt(1, userId);
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			Bookmark b = this.getBookmark(rs);
			
			if (b.getTitle() == null || "".equals(b.getTitle())) {
				b.setTitle(rs.getString("username"));
			}
			
			l.add(b);
		}
		
		rs.close();
		p.close();
		
		return l;
	}
	
	protected List getTopics(int userId) throws Exception
	{
		List l = new ArrayList();
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("BookmarkModel.selectTopicBookmarks"));
		p.setInt(1, userId);
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			Bookmark b = this.getBookmark(rs);
			
			if (b.getTitle() == null || "".equals(b.getTitle())) {
				b.setTitle(rs.getString("topic_title"));
			}
			
			l.add(b);
		}
		
		rs.close();
		p.close();
		
		return l;
	}
	
	protected List getForums(int userId) throws Exception
	{
		List l = new ArrayList();
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("BookmarkModel.selectForumBookmarks"));
		p.setInt(1, userId);
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			Bookmark b = this.getBookmark(rs);
			
			if (b.getTitle() == null || "".equals(b.getTitle())) {
				b.setTitle(rs.getString("forum_name"));
			}
			
			if (b.getDescription() == null || "".equals(b.getDescription())) {
				// <<<< 5/23/06 Howie - avoid to assign null value
				b.setDescription(rs.getString("forum_desc")==null ? "" : rs.getString("forum_desc"));
				// >>>> 5/23/06 Howie
			}
			
			l.add(b);
		}
		
		rs.close();
		p.close();
		
		return l;
	}
	
	protected Bookmark getBookmark(ResultSet rs) throws Exception
	{
		Bookmark b = new Bookmark();
		b.setId(rs.getInt("bookmark_id"));
		b.setDescription(rs.getString("description"));
		b.setPublicVisible(rs.getInt("public_visible") == 1 ? true : false);
		b.setRelationId(rs.getInt("relation_id"));
		b.setTitle(rs.getString("title"));
		b.setDescription(rs.getString("description"));
		b.setUserId(rs.getInt("user_id"));
		b.setRelationType(rs.getInt("relation_type"));

		return b;
	}
}
