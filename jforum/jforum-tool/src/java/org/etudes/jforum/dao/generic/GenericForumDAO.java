/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/GenericForumDAO.java $ 
 * $Id: GenericForumDAO.java 71044 2010-10-29 17:37:24Z murthy@etudes.org $ 
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.JForum;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.TopicDAO;
import org.etudes.jforum.entities.Forum;
import org.etudes.jforum.entities.LastPostInfo;
import org.etudes.jforum.entities.SpecialAccess;
import org.etudes.jforum.entities.Topic;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.sakaiproject.tool.cover.ToolManager;

/**
 * @author Rafael Steil
 * @author Vanessa Sabino
 * revised by rashmi - 09-14-05 getLastPostInfo() to include firstname and lastname
 * Mallika - 1/5/06 - to check if a forum can be deleted
 * Mallika - 1/6/06 - getting dates for forum
 * Mallika - 1/9/06 - adding dates to add and update forum
 * Mallika - 1/12/06 - changing code to handle null dates (add and update)
 * Mallika - 1/24/06 - changing code because either start or end date can be null
 * 
 * 5/23/06 - Howie - revised getLastPostInfo() method to make sure no null (could be empty though) firstname and lastname for jf user.
 *
 * 5/23/06 - Howie - revised fillForum() method to make sure no null (could be empty though) description for a forum.
 * 
 * Murthy - 06/27/06 - updated addNew and update methods for storing the date time in the database
 * Murthy - 06/29/06 - updated fillForum for start and end dates
 */
public class GenericForumDAO extends AutoKeys implements org.etudes.jforum.dao.ForumDAO 
{
	private static Log logger = LogFactory.getLog(GenericForumDAO.class);
	/**
	 * @see org.etudes.jforum.dao.ForumDAO#selectById(int)
	 */
	public Forum selectById(int forumId) throws Exception {
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.selectById"));
		p.setInt(1, forumId);

		ResultSet rs = p.executeQuery();

		Forum f = new Forum();

		if (rs.next()) {
			f = this.fillForum(rs);
		}

		rs.close();
		p.close();
		//<<< 03/15/2007 - Murthy - get groups associated with forums
		if (f.getAccessType() == Forum.ACCESS_GROUPS){
			PreparedStatement frmGrpPrepStmt = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.getForumGroups"));
			frmGrpPrepStmt.setInt(1, forumId);
			ResultSet rsFrmGrps = frmGrpPrepStmt.executeQuery();
			List selectedGrpList = new ArrayList();
			while (rsFrmGrps.next())
				selectedGrpList.add(rsFrmGrps.getString("sakai_group_id"));

			rsFrmGrps.close();
			frmGrpPrepStmt.close();
			
			f.setGroups(selectedGrpList);
		}else
			f.setGroups(new ArrayList());
		//>>> 03/15/2007 - Murthy - get groups associated with forums
		return f;
	}

	protected Forum fillForum(ResultSet rs) throws Exception {
		Forum f = new Forum();

		f.setId(rs.getInt("forum_id"));
		f.setIdCategories(rs.getInt("categories_id"));
		f.setName(rs.getString("forum_name"));
		// <<<< 5/23/06 Howie - avoid to assign null value
		f.setDescription(rs.getString("forum_desc")==null ? "" : rs.getString("forum_desc"));
		// >>>> 5/23/06 Howie
		f.setOrder(rs.getInt("forum_order"));
		f.setTotalTopics(rs.getInt("forum_topics"));
		f.setLastPostId(rs.getInt("forum_last_post_id"));
		f.setModerated(rs.getInt("moderated") > 0);
		f.setType(rs.getInt("forum_type"));
		f.setAccessType(rs.getInt("forum_access_type"));
		f.setGradeType(rs.getInt("forum_grade_type"));
		f.setTotalPosts(rs.getInt("total_posts"));

		//Mallika- 1/6/06 - new code beg
		if (rs.getDate("start_date") != null)
		{
		  Timestamp startDate = rs.getTimestamp("start_date");
		  f.setStartDate(startDate);
		  // SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
		  SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
		  f.setStartDateFormatted(df.format(startDate));
	    }
	    else
	    {
		  f.setStartDate(null);
	    }
	    if (rs.getDate("end_date") != null)
	    {
	      Timestamp endDate = rs.getTimestamp("end_date");
		  f.setEndDate(endDate);
		  // SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
		  SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
		  f.setEndDateFormatted(df.format(endDate));
		  
		  f.setLockForum(rs.getInt("lock_end_date") > 0);
	    }
	    else
	    {
			f.setEndDate(null);
	    }
		//Mallika - 1/6/06 - new code end

		return f;
	}

	/**
	 * @see org.etudes.jforum.dao.ForumDAO#selectAll()
	 */
	public List selectAll() throws Exception {
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.selectAll"));
		List l = new ArrayList();

		ResultSet rs = p.executeQuery();

		while (rs.next()) {
			l.add(this.fillForum(rs));
		}

		rs.close();
		p.close();

		return l;
	}
	
	/**
	 * @see org.etudes.jforum.dao.ForumDAO#selectAllByCourse()
	 */
	public List selectAllByCourse() throws Exception {
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.selectAllByCourseId"));
		p.setString(1, ToolManager.getCurrentPlacement().getContext());
		
		List l = new ArrayList();

		ResultSet rs = p.executeQuery();
		Forum f = new Forum();
		while (rs.next()) {
			f = this.fillForum(rs);
			
			//<<< 03/21/2007 - Murthy - get groups associated with forums
			if (f.getAccessType() == Forum.ACCESS_GROUPS){
				PreparedStatement frmGrpPrepStmt = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.getForumGroups"));
				frmGrpPrepStmt.setInt(1, f.getId());
				ResultSet rsFrmGrps = frmGrpPrepStmt.executeQuery();
				List selectedGrpList = new ArrayList();
				while (rsFrmGrps.next())
					selectedGrpList.add(rsFrmGrps.getString("sakai_group_id"));

				rsFrmGrps.close();
				frmGrpPrepStmt.close();
				
				f.setGroups(selectedGrpList);
			}else
				f.setGroups(new ArrayList());
			
			l.add(f);
		}

		rs.close();
		p.close();
		
		

		return l;
	}

	/**
	 * @see org.etudes.jforum.dao.ForumDAO#setOrderUp(Forum, Forum)
	 */
	public Forum setOrderUp(Forum forum, Forum related) throws Exception 
	{
		return this.changeForumOrder(forum, related, true);
	}
	
	/**
	 * @see org.etudes.jforum.dao.ForumDAO#setOrderDown(Forum, Forum)
	 */
	public Forum setOrderDown(Forum forum, Forum related) throws Exception {
		return this.changeForumOrder(forum, related, false);
	}
	
	private Forum changeForumOrder(Forum forum, Forum related, boolean up) throws Exception
	{
		int tmpOrder = related.getOrder();
		related.setOrder(forum.getOrder());
		forum.setOrder(tmpOrder);

		// ******** 
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.setOrderById"));
		p.setInt(1, forum.getOrder());
		p.setInt(2, forum.getId());
		p.executeUpdate();
		p.close();
		
		// ********
		p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.setOrderById"));
		p.setInt(1, related.getOrder());
		p.setInt(2, related.getId());
		p.executeUpdate();
		p.close();
		
		return this.selectById(forum.getId());
	}

	//Mallika-new code beg
	public boolean canDelete(int forumId) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.canDelete"));
		p.setInt(1, forumId);

		ResultSet rs = p.executeQuery();
		if (!rs.next() || rs.getInt("total") < 1) {
			
			rs.close();
			p.close();
			
			return true;
		}

		rs.close();
		p.close();

		return false;
	}
	//Mallika-new code end

	/**
	 * @see org.etudes.jforum.dao.ForumDAO#delete(int)
	 */
	public void delete(int forumId) throws Exception {
		
		//delete Forum Groups
		deleteForumGroups(forumId);
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.delete"));
		p.setInt(1, forumId);

		p.executeUpdate();

		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.ForumDAO#update(net.jforum.Forum)
	 */
	public void update(Forum forum) throws Exception {
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.update"));

		p.setInt(1, forum.getCategoryId());
		p.setString(2, forum.getName());
		p.setString(3, forum.getDescription());
        //Mallika - new code beg
		if (forum.getStartDate() == null)
		{
		  p.setTimestamp(4, null);
		}
		else
		{
		  p.setTimestamp(4, new Timestamp(forum.getStartDate().getTime()));
		}
		if (forum.getEndDate() == null)
		{
		  p.setTimestamp(5, null);
		  p.setInt(6, 0);
		}
		else
		{
		  p.setTimestamp(5, new Timestamp(forum.getEndDate().getTime()));
		  p.setInt(6, forum.isLockForum() ? 1 : 0);
		}		
		//Mallika - new code end

		p.setInt(7, forum.isModerated() ? 1 : 0);
		p.setInt(8, forum.getType());
		p.setInt(9, forum.getAccessType());
		p.setInt(10, forum.getGradeType());
		p.setInt(11, forum.getId());		
		
		//edit groups
		editForumGroups(forum);
		
		// Order, TotalTopics and LastPostId must be updated using the
		// respective methods
		p.executeUpdate();
		p.close();
	}
	
	
	/**
	 * @see org.etudes.jforum.dao.ForumDAO#updatedates(net.jforum.Forum)
	 */
	public void updateDates(Forum forum) throws Exception {
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.updateDates"));

		if (forum.getStartDate() == null)
		{
		  p.setTimestamp(1, null);
		}
		else
		{
		  p.setTimestamp(1, new Timestamp(forum.getStartDate().getTime()));
		}
		
		if (forum.getEndDate() == null)
		{
		  p.setTimestamp(2, null);
		  p.setInt(3, 0);
		}
		else
		{
		  p.setTimestamp(2, new Timestamp(forum.getEndDate().getTime()));
		  p.setInt(3, forum.isLockForum() ? 1 : 0);
		}		
		
		p.setInt(4, forum.getId());
		
		p.executeUpdate();
		p.close();
	}

	/**
	 * edit forum groups
	 * @param forum
	 */
	private void editForumGroups(Forum forum)
	{
		//remove existing groups if any
		try 
		{
			deleteForumGroups(forum.getId());
		} 
		catch (Exception e1) 
		{
			if (logger.isErrorEnabled()) logger.error(e1.toString(), e1);
		}
		
		//add groups
		if (forum.getAccessType() == Forum.ACCESS_GROUPS)
		{
			try 
			{
				//add forum groups
				addForumGroups(forum);
			} 
			catch (Exception e) 
			{
				if (logger.isErrorEnabled()) logger.error(e.toString(), e);
			}
		}
	}

	/**
	 * delete forum groups
	 * @param forumId
	 */
	private void deleteForumGroups(int forumId) throws Exception {
		if (logger.isDebugEnabled()) logger.debug(this.getClass().getName()+ ":deleteForumGroups - Entering....");
		PreparedStatement frmGrpPrepStmt = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.getForumGroups"));
		frmGrpPrepStmt.setInt(1, forumId);
		ResultSet rsFrmGrps = frmGrpPrepStmt.executeQuery();
		while (rsFrmGrps.next()){
			PreparedStatement frmDelGrpPrepStmt = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.deleteForumGroup"));
			frmDelGrpPrepStmt.setInt(1, forumId);
			frmDelGrpPrepStmt.setString(2, rsFrmGrps.getString("sakai_group_id"));
			frmDelGrpPrepStmt.executeUpdate();
			frmDelGrpPrepStmt.close();
		}

		rsFrmGrps.close();
		frmGrpPrepStmt.close();
		if (logger.isDebugEnabled()) logger.debug(this.getClass().getName()+ ":deleteForumGroups - Exiting....");		
	}

	/**
	 * @see org.etudes.jforum.dao.ForumDAO#addNew(net.jforum.Forum)
	 */
	public int addNew(Forum forum) throws Exception {
		// Gets the higher order
		PreparedStatement pOrder = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.getMaxOrder"));
		ResultSet rs = pOrder.executeQuery();

		if (rs.next()) {
			forum.setOrder(rs.getInt(1) + 1);
		}

		rs.close();
		pOrder.close();

		// Updates the order
		PreparedStatement p = this.getStatementForAutoKeys("ForumModel.addNew");

		p.setInt(1, forum.getCategoryId());
		p.setString(2, forum.getName());
		p.setString(3, forum.getDescription());
		//Mallika - new code beg
		
		if (forum.getStartDate() == null)
		{
		  p.setTimestamp(4, null);
		}
		else
		{
		  p.setTimestamp(4, new Timestamp(forum.getStartDate().getTime()));
		}
		
		if (forum.getEndDate() == null)
		{
		  p.setTimestamp(5, null);
		  p.setInt(6, 0);
		}
		else
		{
		  p.setTimestamp(5, new Timestamp(forum.getEndDate().getTime()));		  
		  p.setInt(6, forum.isLockForum() ? 1 : 0);
		}		
		//Mallika - new code end

		p.setInt(7, forum.getOrder());
		p.setInt(8, forum.isModerated() ? 1 : 0);
		p.setInt(9, forum.getType());
		p.setInt(10, forum.getAccessType());
		p.setInt(11, forum.getGradeType());

		this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("ForumModel.lastGeneratedForumId"));
		int forumId = this.executeAutoKeysQuery(p);

		p.close();
		
		forum.setId(forumId);
		if (forum.getAccessType() == Forum.ACCESS_GROUPS) {
			try {
				//add forum groups
				addForumGroups(forum);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) logger.error(e.toString());
			}
		}
		
		return forumId;
	}

	/**
	 * add or edit forum groups
	 * @param forumId - forum id
	 */
	private void addForumGroups(Forum forum)throws Exception {
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.addForumGroups"));
		
		if (forum != null){
			List groups = forum.getGroups();
			
			if (groups != null && groups.size() > 0){
				for (Iterator iter = groups.iterator(); iter.hasNext();) {
					String groupId = (String)iter.next();
					p.setInt(1, forum.getId());
					p.setString(2, groupId);
					
					p.executeUpdate();
				}
			}

		}
		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.ForumDAO#setLastPost(int, int)
	 */
	public void setLastPost(int forumId, int postId) throws Exception {
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.updateLastPost"));

		p.setInt(1, postId);
		p.setInt(2, forumId);

		p.executeUpdate();

		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.ForumDAO#setTotalTopics(int)
	 */
	public void incrementTotalTopics(int forumId, int count) throws Exception {
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.incrementTotalTopics"));
		p.setInt(1, count);
		p.setInt(2, forumId);
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.ForumDAO#setTotalTopics(int)
	 */
	public void decrementTotalTopics(int forumId, int count) throws Exception {
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.decrementTotalTopics"));
		p.setInt(1, count);
		p.setInt(2, forumId);
		p.executeUpdate();
		p.close();

		// If there are no more topics, then clean the
		// last post id information
		int totalTopics = this.getTotalTopics(forumId);
		if (totalTopics < 1) {
			this.setLastPost(forumId, 0);
		}
	}
	
	private LastPostInfo getLastPostInfo(int forumId, boolean tryFix) throws Exception
	{
		LastPostInfo lpi = new LastPostInfo();

		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.lastPostInfo"));
		p.setInt(1, forumId);

		ResultSet rs = p.executeQuery();

		if (rs.next()) {
			lpi.setUsername(rs.getString("username"));
			lpi.setUserId(rs.getInt("user_id"));
			//rashmi add
			// <<<< 5/23/06 Howie - avoid to assign null value
			lpi.setFirstName(rs.getString("user_fname")==null ? "" : rs.getString("user_fname"));
			lpi.setLastName(rs.getString("user_lname")==null ? "" : rs.getString("user_lname"));
			// >>>> 5/23/06 Howie
			//rashmi end
			// SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
			SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
			lpi.setPostDate(df.format(rs.getTimestamp("post_time")));
			lpi.setPostId(rs.getInt("post_id"));
			lpi.setTopicId(rs.getInt("topic_id"));
			lpi.setPostTimeMillis(rs.getTimestamp("post_time").getTime());
			lpi.setTopicReplies(rs.getInt("topic_replies"));

			lpi.setHasInfo(true);
			
			// Check if the topic is consistent
			TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
			Topic t = tm.selectById(lpi.getTopicId());
			
			if (t.getId() == 0) {
				// Hm, that's not good. Try to fix it
				tm.fixFirstLastPostId(lpi.getTopicId());
			}
			
			tryFix = false;
		}
		else if (tryFix) {
			p.close();
			rs.close();
			
			int postId = this.getMaxPostId(forumId);
			
			p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.latestTopicIdForfix"));
			p.setInt(1, forumId);
			rs = p.executeQuery();
			
			int topicId = -1;
			
			if (rs.next()) {
				topicId = rs.getInt("topic_id");
				
				rs.close();
				p.close();
				
				// Topic
				p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.fixLatestPostData"));
				p.setInt(1, postId);
				p.setInt(2, topicId);
				p.executeUpdate();
				p.close();
				
				// Forum
				p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.fixForumLatestPostData"));
				p.setInt(1, postId);
				p.setInt(2, forumId);
				p.executeUpdate();
			}
		}

		p.close();
		rs.close();
		
		return (tryFix ? this.getLastPostInfo(forumId, false) : lpi);
	}

	/**
	 * @see org.etudes.jforum.dao.ForumDAO#getLastPostInfo(int)
	 */
	public LastPostInfo getLastPostInfo(int forumId) throws Exception 
	{
		return this.getLastPostInfo(forumId, true);
	}

	/**
	 * @see org.etudes.jforum.dao.ForumDAO#getTotalMessages()
	 */
	public int getTotalMessages() throws Exception {
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.totalMessages"));
		ResultSet rs = p.executeQuery();
		rs.next();

		int total = rs.getInt("total_messages");
		p.close();
		rs.close();

		return total;
	}

	/**
	 * @see org.etudes.jforum.dao.ForumDAO#getTotalTopics(int)
	 */
	public int getTotalTopics(int forumId) throws Exception {
		int total = 0;
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.getTotalTopics"));
		p.setInt(1, forumId);
		ResultSet rs = p.executeQuery();

		if (rs.next()) {
			total = rs.getInt(1);
		}

		p.close();
		rs.close();

		return total;
	}

	/**
	 * @see org.etudes.jforum.dao.ForumDAO#getMaxPostId(int)
	 */
	public int getMaxPostId(int forumId) throws Exception {
		int id = -1;

		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.getMaxPostId"));
		p.setInt(1, forumId);

		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			id = rs.getInt("post_id");
		}

		rs.close();
		p.close();

		return id;
	}

	/**
	 * @see org.etudes.jforum.dao.ForumDAO#moveTopics(java.lang.String[], int, int)
	 */
	public void moveTopics(String[] topics, int fromForumId, int toForumId) throws Exception {
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.moveTopics"));
		PreparedStatement t = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PostModel.setForumByTopic"));
		
		p.setInt(1, toForumId);
		t.setInt(1, toForumId);
		
		TopicDAO tdao = DataAccessDriver.getInstance().newTopicDAO();
		
		Forum f = this.selectById(toForumId);

		for (int i = 0; i < topics.length; i++) {
			int topicId = Integer.parseInt(topics[i]);
			p.setInt(2, topicId);
			t.setInt(2, topicId);
			
			p.executeUpdate();
			t.executeUpdate();
			
			tdao.setModerationStatusByTopic(topicId, f.isModerated());
		}

		this.decrementTotalTopics(fromForumId, topics.length);
		this.incrementTotalTopics(toForumId, topics.length);
		
		this.setLastPost(fromForumId, this.getMaxPostId(fromForumId));
		this.setLastPost(toForumId, this.getMaxPostId(toForumId));

		p.close();
	}
	
	/**
	 * @see org.etudes.jforum.dao.ForumDAO#hasUnreadTopics(int, long)
	 */
	public List checkUnreadTopics(int forumId, long lastVisit) throws Exception
	{
		List l = new ArrayList();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.checkUnreadTopics"));
		p.setInt(1, forumId);
		p.setTimestamp(2, new Timestamp(lastVisit));
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			Topic t = new Topic();
			t.setId(rs.getInt("topic_id"));
			t.setTime(new Date(rs.getTimestamp(1).getTime()));
			
			l.add(t);
		}
		
		rs.close();
		p.close();
		
		return l;
	}
	
	/**
	 * @see org.etudes.jforum.dao.ForumDAO#setModerated(int, boolean)
	 */
	public void setModerated(int categoryId, boolean status) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.setModerated"));
		p.setInt(1, status ? 1 : 0);
		p.setInt(2, categoryId);
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.ForumDAO#selectByCategoryId(int)
	 */
	public List<Forum> selectByCategoryId(int categoryId) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.selectByCategoryId"));
		List l = new ArrayList();
		
		p.setInt(1, categoryId);
		ResultSet rs = p.executeQuery();

		while (rs.next()) {
			l.add(this.fillForum(rs));
		}

		rs.close();
		p.close();

		return l;
	}

	/**
	 * @see org.etudes.jforum.dao.ForumDAO#getForumDatesCount(int)
	 */
	public int getForumDatesCount(int categoryId) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ForumModel.selectForumCountWithDatesByCategoryId"));
		p.setInt(1, categoryId);

		ResultSet rs = p.executeQuery();

		if (rs.next()) 
		{
			return (rs.getInt("forums_with_dates"));
		}
		
		return 0;
	}
}
