/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/GenericModerationDAO.java $ 
 * $Id: GenericModerationDAO.java 55493 2008-12-01 22:55:48Z murthy@etudes.org $ 
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
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.etudes.jforum.JForum;
import org.etudes.jforum.dao.ModerationDAO;
import org.etudes.jforum.entities.ModerationPendingInfo;
import org.etudes.jforum.entities.Post;
import org.etudes.jforum.entities.TopicModerationInfo;
import org.etudes.jforum.util.preferences.SystemGlobals;


/**
 * @author Rafael Steil
 * 09/14/05 - Rashmi - add firstname and lastname to the query
 *
 * 5/23/06 - Howie - revised getPost() method to make sure no null (could be empty though) firstname and lastname for jf user.
 *
 */
public class GenericModerationDAO implements ModerationDAO
{
	/**
	 * @see org.etudes.jforum.dao.ModerationDAO#aprovePost(int)
	 */
	public void aprovePost(int postId) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("ModerationModel.aprovePost"));
		p.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
		p.setInt(2, postId);
		p.executeUpdate();
		p.close();
	}
	
	/**
	 * @see org.etudes.jforum.dao.ModerationDAO#topicsByForum(int)
	 */
	public Map topicsByForum(int forumId) throws Exception
	{
		Map m = new HashMap();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("ModerationModel.topicsByForum"));
		p.setInt(1, forumId);
		
		int lastId = 0;
		TopicModerationInfo info = null;
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			int id = rs.getInt("topic_id");
			if (id != lastId) {
				lastId = id;
				
				if (info != null) {
					m.put(new Integer(info.getTopicId()), info);
				}
				
				info = new TopicModerationInfo();
				info.setTopicId(id);
				info.setTopicReplies(rs.getInt("topic_replies"));
				info.setTopicTitle(rs.getString("topic_title"));
			}
			
			info.addPost(this.getPost(rs));
		}
		
		if (info != null) {
			m.put(new Integer(info.getTopicId()), info);
		}
		
		rs.close();
		p.close();
		
		return m;
	}
	
	protected Post getPost(ResultSet rs) throws Exception
	{
		Post p = new Post();
		p.setPostUsername(rs.getString("username"));
		p.setId(rs.getInt("post_id"));
		p.setUserId(rs.getInt("user_id"));
		p.setBbCodeEnabled(rs.getInt("enable_bbcode") == 1);
		p.setHtmlEnabled(rs.getInt("enable_html") == 1);
		p.setSmiliesEnabled(rs.getInt("enable_smilies") == 1);
		p.setSubject(rs.getString("post_subject"));
		p.setText(this.getPostTextFromResultSet(rs));
		// aded by rashmi
		// <<<< 5/23/06 Howie - avoid to assign null value
		p.setFirstName(rs.getString("user_fname")==null ? "" : rs.getString("user_fname"));
		p.setLastName(rs.getString("user_lname")==null ? "" : rs.getString("user_lname"));
		// >>>> 5/23/06 Howie
		// add end
		return p;
	}
	
	protected String getPostTextFromResultSet(ResultSet rs) throws Exception
	{
		return rs.getString("post_text");
	}
	
	/**
	 * @see org.etudes.jforum.dao.ModerationDAO#categoryPendingModeration()
	 */
	public List categoryPendingModeration() throws Exception
	{
		List l = new ArrayList();
		int lastId = 0;
		ModerationPendingInfo info = null;
		Statement s = JForum.getConnection().createStatement();
		
		ResultSet rs = s.executeQuery(SystemGlobals.getSql("ModerationModel.categoryPendingModeration"));
		while (rs.next()) {
			int id = rs.getInt("categories_id");
			if (id != lastId) {
				lastId = id;
				
				if (info != null) {
					l.add(info);
				}
				
				info = new ModerationPendingInfo();
				info.setCategoryName(rs.getString("title"));
				info.setCategoryId(id);
			}
			
			info.addInfo(rs.getString("forum_name"), 
					rs.getInt("forum_id"), 
					rs.getInt("total"));
		}
		
		if (info != null) {
			l.add(info);
		}
		
		rs.close();
		s.close();
		
		return l;
	}
	
}
