/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/GenericTopicModelDAO.java $ 
 * $Id: GenericTopicModelDAO.java 68979 2010-06-29 22:17:25Z murthy@etudes.org $ 
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
package org.etudes.jforum.dao.generic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.JForum;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.ForumDAO;
import org.etudes.jforum.dao.PostDAO;
import org.etudes.jforum.entities.Topic;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.sakaiproject.tool.cover.ToolManager;
//import end by Mallika
/**
 * @author Rafael Steil
 * 09/14/05 - rashmi - firstname and lastname for posted by and last posted by 
 * instead of username
 * 10/19/05 - Mallika - adding course ID to the equation
 *
 * 5/23/06 - Howie - revised notifyUsers() method to make sure no null (could be empty though) email for jf user.
 * Mallika - 9/27/06 - eliminating views column
 */
public class GenericTopicModelDAO extends AutoKeys implements org.etudes.jforum.dao.TopicDAO 
{
	private int DUPLICATE_KEY = 1062;
	
	private static Log logger = LogFactory.getLog(GenericTopicModelDAO.class);
	
	/**
	 * @see org.etudes.jforum.dao.TopicDAO#fixFirstLastPostId(int)
	 */
	public void fixFirstLastPostId(int topicId) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.getFirstLastPostId"));
		p.setInt(1, topicId);
		
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			int first = rs.getInt("first_post_id");
			int last = rs.getInt("last_post_id");
			
			rs.close();
			p.close();
			
			p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.fixFirstLastPostId"));
			p.setInt(1, first);
			p.setInt(2, last);
			p.setInt(3, topicId);
			p.executeUpdate();
		}
		
		rs.close();
		p.close();
	}

	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#selectById(int)
	 */
	public Topic selectById(int topicId) throws Exception 
	{
		if(logger.isDebugEnabled()) logger.debug("selecting a topic on id=" + topicId);
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.selectById"));
		p.setInt(1, topicId);
		
		Topic t = new Topic();
		ResultSet rs = p.executeQuery();
		List l = this.fillTopicsData(rs);
		if (l.size() > 0) {
			t = (Topic)l.get(0);
		}

		if(l.size() > 1) {
			logger.error(l.size() + " topics exist with id=" + topicId);
		}
		rs.close();
		p.close();
		return t;
	}
	
	/**
	 * @see org.etudes.jforum.dao.TopicDAO#selectRaw(int)
	 */
	public Topic selectRaw(int topicId) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.selectRaw"));
		p.setInt(1, topicId);
		
		Topic t = new Topic();
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			t = this.getBaseTopicData(rs);
		}
		
		rs.close();
		p.close();
		return t;
	}

	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#delete(int)
	 */
	public void delete(final Topic topic) throws Exception 
	{
		this.deleteTopics(new ArrayList() {{ add(topic); }});
	}
	
	public void deleteTopics(List topics) throws Exception
	{
		// Topic
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.delete"));
		ForumDAO fm = DataAccessDriver.getInstance().newForumDAO();
		
		PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
		
		for (Iterator iter = topics.iterator(); iter.hasNext(); ) {
			Topic topic = (Topic)iter.next();

			// Remove watches
			this.removeSubscriptionByTopic(topic.getId());

			// Remove the messages
			pm.deleteByTopic(topic.getId());
			
			p.setInt(1, topic.getId());
			p.executeUpdate();
			
			// Update forum stats
			fm.decrementTotalTopics(topic.getForumId(), 1);
		}
		
		p.close();
	}
	
	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#deleteByForum(int)
	 */
	public void deleteByForum(int forumId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.deleteByForum"));
		p.setInt(1, forumId);
		
		ResultSet rs = p.executeQuery();
		List topics = new ArrayList();
		while (rs.next()) {
			Topic t = new Topic();
			t.setId(rs.getInt("topic_id"));
			
			topics.add(t);
		}
		
		rs.close();
		p.close();
		
		this.deleteTopics(topics);
	}

	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#update(net.jforum.Topic)
	 */
	public void update(Topic topic) throws Exception 
	{
		long methodStartTime = System.currentTimeMillis();
		if(logger.isDebugEnabled()) logger.debug("starting  updateTopic(" + topic + ")");

		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.update"));
		
		p.setString(1, topic.getTitle());
		p.setInt(2, topic.getLastPostId());
		p.setInt(3, topic.getFirstPostId());
		p.setInt(4, topic.getType());
		p.setInt(5, topic.isModerated() ? 1 : 0);
		p.setInt(6, topic.isGradeTopic() ? 1 : 0);
		p.setInt(7, topic.isExportTopic() ? 1 : 0);
		p.setInt(8, topic.getId());
		p.executeUpdate();
		
		p.close();

		if(logger.isDebugEnabled()) logger.debug("finished  updateTopic(" + topic + ").  Took " + (System.currentTimeMillis()-methodStartTime) + " ms");
	}

	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#addNew(net.jforum.Topic)
	 */
	public int addNew(Topic topic) throws Exception 
	{
		long methodStartTime = System.currentTimeMillis();
		if(logger.isDebugEnabled()) logger.debug("starting  addNewTopic(" + topic + ")");

		PreparedStatement p = this.getStatementForAutoKeys("TopicModel.addNew");
		
		p.setInt(1, topic.getForumId());
		p.setString(2, topic.getTitle());
		p.setInt(3, topic.getPostedBy().getId());
		p.setTimestamp(4, new Timestamp(topic.getTime().getTime()));
		p.setInt(5, topic.getFirstPostId());
		p.setInt(6, topic.getLastPostId());
		p.setInt(7, topic.getType());
		p.setInt(8, topic.isModerated() ? 1 : 0);
		p.setInt(9, topic.isGradeTopic() ? 1 : 0);
		p.setInt(10, topic.isExportTopic() ? 1 : 0);
		
		this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("TopicModel.lastGeneratedTopicId"));
		int topicId = this.executeAutoKeysQuery(p);

		p.close();

		if(logger.isDebugEnabled()) logger.debug("finished  addNewTopic(" + topic + ").  Took " + (System.currentTimeMillis()-methodStartTime) + " ms");

		return topicId;
	}


	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#incrementTotalReplies(int)
	 */
	public void incrementTotalReplies(int topicId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.incrementTotalReplies"));
		p.setInt(1, topicId);
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#decrementTotalReplies(int)
	 */
	public void decrementTotalReplies(int topicId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.decrementTotalReplies"));
		p.setInt(1, topicId);
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#setLastPostId(int, int)
	 */
	public void setLastPostId(int topicId, int postId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.setLastPostId"));
		p.setInt(1, postId);
		p.setInt(2, topicId);
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#selectAllByForum(int)
	 */
	public List selectAllByForum(int forumId) throws Exception 
	{
		return this.selectAllByForumByLimit(forumId, 0, Integer.MAX_VALUE);
	}
	
	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#selectAllByForumByLimit(int, int, int)
	 */
	public List selectAllByForumByLimit(
		int forumId,
		int startFrom,
		int count)
		throws Exception 
	{
		List l = new ArrayList();

		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.selectAllByForumByLimit"));
		p.setInt(1, forumId);
		p.setInt(2, startFrom);
		p.setInt(3, count);
		
		ResultSet rs = p.executeQuery();
		
		l = this.fillTopicsData(rs);
		
		rs.close();
		p.close();
		return l;
	}
	
	protected Topic getBaseTopicData(ResultSet rs) throws Exception
	{
		int id = rs.getInt("topic_id");
		if(logger.isDebugEnabled()) logger.debug("creating an in-memory topic object to model topic id=" + id);
		
		Topic t = new Topic();
		
		t.setTitle(rs.getString("topic_title"));
		t.setId(id);
		t.setTime(rs.getTimestamp("topic_time"));
		t.setStatus(rs.getInt("topic_status"));
		t.setTotalReplies(rs.getInt("topic_replies"));
		t.setFirstPostId(rs.getInt("topic_first_post_id"));
		t.setLastPostId(rs.getInt("topic_last_post_id"));
		/*10/15/2008 Murthy This should be changed with next release or later. This is added 
		 * as task topic type is changed to reuse/export topic*/
		if (rs.getInt("topic_type") == 1)
			t.setType(Topic.TYPE_NORMAL);
		else
			t.setType(rs.getInt("topic_type"));
		t.setForumId(rs.getInt("forum_id"));
		t.setModerated(rs.getInt("moderated") == 1);
		t.setGradeTopic(rs.getInt("topic_grade") == 1);
		/*10/16/2008 Murthy This should be changed with next release or later. This is added 
		 * as task topic type is changed to reuse/export topic*/
		if (rs.getInt("topic_type") == 1 || rs.getInt("topic_export") == 1)
			t.setExportTopic(true);
		else
			t.setExportTopic(false);
		
		return t;
	}
	
	public List fillTopicsData(ResultSet rs) throws Exception
	{
		// SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
		SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
		List l = new ArrayList();
		
		while (rs.next()) {
			Topic t = this.getBaseTopicData(rs);
			t.setHasAttach(rs.getInt("attach") > 0);

			// First Post Time
			t.setFirstPostTime(df.format(rs.getTimestamp("topic_time")));
			
			// Last Post Time
			t.setLastPostTime(df.format(rs.getTimestamp("post_time")));
			t.setLastPostTimeInMillis(rs.getTimestamp("post_time"));

			// Created by
			User u = new User();
			u.setId(rs.getInt("posted_by_id"));
			u.setUsername(rs.getString("posted_by_username"));
			//add by rashmi
			u.setFirstName(rs.getString("posted_by_fname"));
			u.setLastName(rs.getString("posted_by_lname"));
			//add end
			t.setPostedBy(u);
			
			// Last post by
			u = new User();
			u.setId(rs.getInt("last_post_by_id"));
			u.setUsername(rs.getString("last_post_by_username"));
//			add by rashmi
			u.setFirstName(rs.getString("last_post_by_fname"));
			u.setLastName(rs.getString("last_post_by_lname"));
			//add end
			t.setLastPostBy(u);
			
			l.add(t);
		}
		
		return l;
	}

	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#autoSetLastPostId(int)
	 */
	public int getMaxPostId(int topicId) throws Exception 
	{
		int id = -1;
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.getMaxPostId"));
		p.setInt(1, topicId);
		
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			id = rs.getInt("post_id");
		}
		
		rs.close();
		p.close();
		
		return id;
	}

	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#getTotalPosts(int)
	 */
	public int getTotalPosts(int topicId) throws Exception 
	{
		int total = 0;
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.getTotalPosts"));
		p.setInt(1, topicId);
		
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			total = rs.getInt("total");
		}
		
		rs.close();
		p.close();
		
		return total;
	}

	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#selectLastN(int)
	 */
	public List selectLastN(int count) throws Exception 
	{
		List topics = new ArrayList();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.selectLastN"));
		p.setInt(1, count);
		
		ResultSet rs = p.executeQuery();
		
		// If you want more fields here, just put the code. At the time
		// this code was written, these were the only needed fields ;)
		while (rs.next()) {
			Topic t = new Topic();
			
			t.setTitle(rs.getString("topic_title"));
			t.setId(rs.getInt("topic_id"));
			t.setTime(rs.getTimestamp("topic_time"));
			t.setType(rs.getInt("topic_type"));
			
			topics.add(t);
		}
		
		rs.close();
		p.close();
		
		return topics;
	}
	
	/**
 	 * @see org.etudes.jforum.dao.TopicDAO#notifyUsers(int)
 	 */
	public List notifyUsers(Topic topic) throws Exception 
	{ 
		int posterId = SessionFacade.getUserSession().getUserId();
		int anonUser = SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID);
		
		PreparedStatement stmt = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.notifyUsers"));		
		ResultSet rs = null;

		stmt.setInt(1, topic.getId());
		stmt.setInt(2, posterId); //don't notify the poster
		stmt.setInt(3, anonUser); //don't notify the anonimous user
				
		rs = stmt.executeQuery();
		
		List users = new ArrayList();
		while(rs.next()) {
			User user = new User();

			user.setId(rs.getInt("user_id"));
			// <<<< 5/23/06 Howie - avoid to assign null value
			user.setEmail(rs.getString("user_email")==null ? "" : rs.getString("user_email"));
			// >>>> 5/23/06 Howie
			user.setUsername(rs.getString("username"));
			user.setLang(rs.getString("user_lang"));
			user.setSakaiUserId(rs.getString("sakai_user_id"));
			
			users.add(user);
		}
		
		// Set read status to false
		stmt = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.markAllAsUnread"));
		stmt.setInt(1, topic.getId());
		stmt.setInt(2, posterId); //don't notify the poster
		stmt.setInt(3, anonUser); //don't notify the anonimous user
		
		stmt.executeUpdate();
			
		rs.close();
		stmt.close();
		
		return users;
	}
	
	/**
	 * @see org.etudes.jforum.dao.TopicDAO#subscribeUser(int, int)
	 */
	public void subscribeUser(int topicId, int userId) throws Exception 
	{
		PreparedStatement stmt = JForum.getConnection(). prepareStatement( SystemGlobals.getSql("TopicModel.subscribeUser"));
		
		try {
			stmt.setInt(1, topicId);
			stmt.setInt(2, userId);
			
			stmt.executeUpdate();
		} catch(SQLException e) {
			// Ignore duplicate key warnings
			if(e.getErrorCode() != DUPLICATE_KEY) {
				throw e;			
			}
		}
		finally {
			if(stmt != null) {
				stmt.close();
			}
		}		
	}
	
	/**
	 * @see org.etudes.jforum.dao.TopicDAO#isUserSubscribing(int, int)
	 */
	public boolean isUserSubscribed(int topicId, int userId) throws Exception 
	{
		PreparedStatement stmt = JForum.getConnection(). prepareStatement( SystemGlobals.getSql("TopicModel.isUserSubscribed"));
		ResultSet rs = null;
		
		stmt.setInt(1, topicId);
		stmt.setInt(2, userId);
		
		rs = stmt.executeQuery();
		boolean status = rs.next();
		
		rs.close();
		stmt.close();
				
		return status;
	}
	
	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#removeSubscription(int, int)
	 */
	public void removeSubscription(int topicId, int userId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.removeSubscription"));
		p.setInt(1, topicId);
		p.setInt(2, userId);
		
		p.executeUpdate();
		p.close();
	}
	
	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#removeSubscriptionByTopic(int)
	 */
	public void removeSubscriptionByTopic(int topicId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.removeSubscriptionByTopic"));
		p.setInt(1, topicId);
		
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#updateReadStatus(int, int, boolean)
	 */
	public void updateReadStatus(int topicId, int userId, boolean read) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.updateReadStatus"));
		p.setInt(1, read ? 1 : 0);
		p.setInt(2, topicId);
		p.setInt(3, userId);
		
		p.executeUpdate();
		p.close();
	}
	
	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#lockUnlock(int, int)
	 */
	public void lockUnlock(int[] topicId, int status) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.lockUnlock"));
		p.setInt(1, status);
		
		for (int i = 0; i < topicId.length; i++) {
			p.setInt(2, topicId[i]);
			p.executeUpdate();
		}
		p.close();
	}
	
	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#selectRecentTopics(int)
	 */	
	public List selectRecentTopics (int limit) throws Exception
	{
		List l = new ArrayList();

		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.selectRecentTopicsByLimit"));
        //Mallika - new code beg
		p.setString(1, ToolManager.getCurrentPlacement().getContext());
		//Mallika - new code end, changed below from 1 to 2
		
		p.setInt(2, limit);
		
		
		
		ResultSet rs = p.executeQuery();
		
		l = this.fillTopicsData(rs);
		
		rs.close();
		p.close();
		return l;		
	}
	
	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#setFirstPostId(int, int)
	 */
	public void setFirstPostId(int topicId, int postId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.setFirstPostId"));
		p.setInt(1, postId);
		p.setInt(2, topicId);
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see org.etudes.jforum.dao.TopicDAO#getMinPostId(int)
	 */
	public int getMinPostId(int topicId) throws Exception 
	{
		int id = -1;
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.getMinPostId"));
		p.setInt(1, topicId);
		
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			id = rs.getInt("post_id");
		}
		
		rs.close();
		p.close();
		
		return id;
	}
	
	/**
	 * @see org.etudes.jforum.dao.TopicDAO#setModerationStatus(int, boolean)
	 */
	public void setModerationStatus(int forumId, boolean status) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicModel.setModerationStatus"));
		p.setInt(1, status ? 1 : 0);
		p.setInt(2, forumId);
		p.executeUpdate();
		p.close();
	}
	
	/**
	 * @see org.etudes.jforum.dao.TopicDAO#setModerationStatusByTopic(int, boolean)
	 */
	public void setModerationStatusByTopic(int topicId, boolean status) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("TopicModel.setModerationStatusByTopic"));
		p.setInt(1, status ? 1 : 0);
		p.setInt(2, topicId);
		p.executeUpdate();
		p.close();
	}
	
	/**
	 * @see org.etudes.jforum.dao.TopicDAO#selectTopicTitlesByIds(java.util.Collection)
	 */
	public List selectTopicTitlesByIds(Collection idList) throws Exception
	{
		List l = new ArrayList();
		String sql = SystemGlobals.getSql("TopicModel.selectTopicTitlesByIds");
		
		StringBuffer sb = new StringBuffer(idList.size() * 2);
		for (Iterator iter = idList.iterator(); iter.hasNext(); ) {
			sb.append(iter.next()).append(",");
		}
		
		int len = sb.length();
		sql = sql.replaceAll(":ids:", len > 0 ? sb.toString().substring(0, len - 1) : "0");
		PreparedStatement p = JForum.getConnection().prepareStatement(sql);
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			Map m = new HashMap();
			m.put("id", new Integer(rs.getInt("topic_id")));
			m.put("title", rs.getString("topic_title"));
			
			l.add(m);
		}
		
		rs.close();
		p.close();
		
		return l;
	}
}
