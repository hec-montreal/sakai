/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/GenericPostDAO.java $ 
 * $Id: GenericPostDAO.java 66267 2010-02-23 21:39:07Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008, 2009 Etudes, Inc. 
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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.JForum;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.entities.Post;
import org.etudes.jforum.entities.UserSession;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.search.SearchFacade;
import org.etudes.util.HtmlHelper;

import java.util.Date;

/**
 * @author Rafael Steil
 * @author Vanessa Sabino
 * Mallika - 10/2/06 - Adding markAllTime condition
 */
public class GenericPostDAO extends AutoKeys implements org.etudes.jforum.dao.PostDAO 
{
	private static final Log logger = LogFactory.getLog(GenericPostDAO.class);
	
	/**
	 * @see org.etudes.jforum.dao.PostDAO#selectById(int)
	 */
	public Post selectById(int postId) throws Exception 
	{
		long methodStartTime = System.currentTimeMillis();
		if(logger.isDebugEnabled()) logger.debug("starting  selectById(" + postId + ")");

		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PostModel.selectById"));		
		p.setInt(1, postId);
		
		ResultSet rs = p.executeQuery();
		
		Post post = new Post();
		
		if (rs.next()) {
			post = this.makePost(rs);
		}
		
		rs.close();
		p.close();
		
		if(logger.isDebugEnabled()) logger.debug("finished  selectById(" + postId + ").  Took " + (System.currentTimeMillis()-methodStartTime) + " ms");

		return post;
	}
	
	protected Post makePost(ResultSet rs) throws Exception
	{
		Post post = new Post();
		post.setId(rs.getInt("post_id"));
		post.setTopicId(rs.getInt("topic_id"));
		post.setForumId(rs.getInt("forum_id"));
		post.setUserId(rs.getInt("user_id"));
		Timestamp postTime = rs.getTimestamp("post_time");
		post.setTime(postTime);
		post.setUserIp(rs.getString("poster_ip"));
		post.setBbCodeEnabled(rs.getInt("enable_bbcode") > 0);
		post.setHtmlEnabled(rs.getInt("enable_html") > 0);
		post.setSmiliesEnabled(rs.getInt("enable_smilies") > 0);
		post.setSignatureEnabled(rs.getInt("enable_sig") > 0);
		post.setEditTime(rs.getTimestamp("post_edit_time"));
		post.setEditCount(rs.getInt("post_edit_count"));
		post.setSubject(rs.getString("post_subject"));
		post.setText(HtmlHelper.stripComments(this.getPostTextFromResultSet(rs)));
		post.setPostUsername(rs.getString("username"));
		post.hasAttachments(rs.getInt("attach") > 0);
		post.setModerate(rs.getInt("need_moderate") == 1);
		
		// SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
		SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
		post.setFormatedTime(df.format(postTime));
		
		post.setKarma(DataAccessDriver.getInstance().newKarmaDAO().getPostKarma(post.getId()));
		
		//dave setting whether post was read...
		UserSession userSession = SessionFacade.getUserSession();

		long lastVisit = userSession.getLastVisit().getTime();
		
		Date markDate = userSession.getMarkAllTime();
		
		
		boolean checkUnread = (userSession.getUserId() 
				!= SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID));
		
		/*if(checkUnread)
		{
			long timeToCompare;
			if (markDate == null)
			{
				System.out.println("LastVisit setting to false,markDate is null ");
				timeToCompare = lastVisit;
			}
			else
			{
				if (lastVisit > markDate.getTime())
				{
					System.out.println("LastVisit setting to false ");
					timeToCompare = lastVisit;
				}
				else
				{
					System.out.println("markAll setting to false ");	
					timeToCompare = markDate.getTime();
				}
			}
			if (post.getTime().getTime() > timeToCompare)
			{
			  	
			  post.setRead(false);
			}
		
		}*/
		return post;
	}
	
	/**
	 * Utility method to read the post text fromt the result set.
	 * This method may be useful when using some "non-standart" way
	 * to store text, like oracle does when using (c|b)lob
	 * 
	 * @param rs The resultset to fetch data from
	 * @return The post text string
	 * @throws Exception
	 */
	protected String getPostTextFromResultSet(ResultSet rs) throws Exception
	{
		return rs.getString("post_text");
	}

	/**
	 * @see org.etudes.jforum.dao.PostDAO#delete(Post)
	 */
	public void delete(final Post post) throws Exception 
	{
		this.removePosts(new ArrayList() {{ add(post); }});
	}
	
	private void removePosts(List posts) throws Exception
	{
		PreparedStatement post = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PostModel.deletePost"));
		PreparedStatement text = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PostModel.deletePostText"));
		PreparedStatement search = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PostModel.deleteWordMatch"));
		
		for (Iterator iter = posts.iterator(); iter.hasNext(); ) {
			Post p = (Post)iter.next();

			search.setInt(1, p.getId());
			text.setInt(1, p.getId());
			post.setInt(1, p.getId());	
			
			search.executeUpdate();
			text.executeUpdate();
			post.executeUpdate();
		}
		
		search.close();
		text.close();
		post.close();
	}
	
	/**
	 * @set net.jforum.model.PostModel#deleteByTopic(int) 
	 */
	public void deleteByTopic(int topicId) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PostModel.deleteByTopic"));
		p.setInt(1, topicId);
		ResultSet rs = p.executeQuery();
		
		List posts = new ArrayList();
		while (rs.next()) {
			Post post = new Post();
			post.setId(rs.getInt("post_id"));
			post.setUserId(rs.getInt("user_id"));
			
			posts.add(post);
		}
		
		rs.close();
		p.close();
		
		this.removePosts(posts);
	}
	
	/**
	 * @see org.etudes.jforum.dao.PostDAO#update(net.jforum.Post)
	 */
	public void update(Post post) throws Exception 
	{
		this.updatePostsTable(post);
		this.updatePostsTextTable(post);
	}
	
	protected void updatePostsTextTable(Post post) throws Exception
	{
		long methodStartTime = System.currentTimeMillis();
		if(logger.isDebugEnabled()) logger.debug("starting  updatePostsTextTable(" + post + ")");
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PostModel.updatePostText"));
		p.setString(1, post.getText());
		p.setString(2, post.getSubject());
		p.setInt(3, post.getId());
		
		p.executeUpdate();
		p.close();

		if(logger.isDebugEnabled()) logger.debug("finished  updatePostsTextTable(" + post + ").  Took " + (System.currentTimeMillis()-methodStartTime) + " ms");
	}
	
	protected void updatePostsTable(Post post) throws Exception
	{
		long methodStartTime = System.currentTimeMillis();
		if(logger.isDebugEnabled()) logger.debug("starting  updatePostsTable(" + post + ")");

		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PostModel.updatePost"));
		p.setInt(1, post.getTopicId());
		p.setInt(2, post.getForumId());
		p.setInt(3, post.isBbCodeEnabled() ? 1 : 0);
		p.setInt(4, post.isHtmlEnabled() ? 1 : 0);
		p.setInt(5, post.isSmiliesEnabled() ? 1 : 0);
		p.setInt(6, post.isSignatureEnabled() ? 1 : 0);
		p.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
		p.setString(8, post.getUserIp());
		p.setInt(9, post.getId());
		
		p.executeUpdate();
		p.close();

		if(logger.isDebugEnabled()) logger.debug("finished  updatePostsTable(" + post + ").  Took " + (System.currentTimeMillis()-methodStartTime) + " ms");
	}

	/**
	 * @see org.etudes.jforum.dao.PostDAO#addNew(net.jforum.Post)
	 */
	public int addNew(Post post) throws Exception 
	{
		this.addNewPost(post);
		this.addNewPostText(post);
		
		// Search
		SearchFacade.index(post);
		
		return post.getId();
	}
	
	protected void addNewPostText(Post post) throws Exception
	{
		long methodStartTime = System.currentTimeMillis();
		if(logger.isDebugEnabled()) logger.debug("starting  addNewPostText(" + post + ")");

		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PostModel.addNewPostText"));
		p.setInt(1, post.getId());
		p.setString(2, post.getText());
		p.setString(3, post.getSubject());
		p.executeUpdate();
		p.close();

		if(logger.isDebugEnabled()) logger.debug("finished  addNewPostText(" + post + ").  Took " + (System.currentTimeMillis()-methodStartTime) + " ms");
	}
	
	protected void addNewPost(Post post) throws Exception
	{
		long methodStartTime = System.currentTimeMillis();
		if(logger.isDebugEnabled()) logger.debug("starting  addNewPost(" + post + ")");

		PreparedStatement p = this.getStatementForAutoKeys("PostModel.addNewPost");
		
		p.setInt(1, post.getTopicId());
		p.setInt(2, post.getForumId());
		p.setLong(3, post.getUserId());
		p.setTimestamp(4, new Timestamp(post.getTime().getTime()));
		p.setString(5, post.getUserIp());
		p.setInt(6, post.isBbCodeEnabled() ? 1 : 0);
		p.setInt(7, post.isHtmlEnabled() ? 1 : 0);
		p.setInt(8, post.isSmiliesEnabled() ? 1 : 0);
		p.setInt(9, post.isSignatureEnabled() ? 1 : 0);
		p.setInt(10, post.isModerationNeeded() ? 1 : 0);
		
		this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("PostModel.lastGeneratedPostId"));
		int postId = this.executeAutoKeysQuery(p);
		post.setId(postId);
		
		p.close();

		if(logger.isDebugEnabled()) logger.debug("finished  addNewPost(" + post + ").  Took " + (System.currentTimeMillis()-methodStartTime) + " ms");
	}
	
	/**
	 * @see org.etudes.jforum.dao.PostDAO#selectAllBytTopic(int)
	 */
	public List selectAllByTopic(int topicId) throws Exception
	{
		return this.selectAllByTopicByLimit(topicId, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * @see org.etudes.jforum.dao.PostDAO#selectAllBytTopicByLimit(int, int, int)
	 */
	public List selectAllByTopicByLimit(int topicId, int startFrom, int count) throws Exception
	{
		long methodStartTime = System.currentTimeMillis();
		if(logger.isDebugEnabled()) logger.debug("starting  selectAllByTopicByLimit(" + topicId + ", " + startFrom + ", " + count + ")");
		
		List l = new ArrayList();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PostModel.selectAllByTopicByLimit"));
		p.setInt(1, topicId);
		p.setInt(2, startFrom);
		p.setInt(3, count);
		
		ResultSet rs = p.executeQuery();
						
		while (rs.next()) {
			l.add(this.makePost(rs));			
		}
		
		rs.close();
		p.close();
				
		if(logger.isDebugEnabled()) logger.debug("finished  selectAllByTopicByLimit(" + topicId + ", " + startFrom + ", " + count + ").  Took " + (System.currentTimeMillis()-methodStartTime) + " ms");

		return l;
	}

	/**
	 * {@inheritDoc}
	 */
	public List selectAllByForumByUser(int forumId, int userId) throws Exception
	{
		List l = new ArrayList();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PostModel.selectAllByForumByUser"));
		p.setInt(1, forumId);
		p.setInt(2, userId);
		
		ResultSet rs = p.executeQuery();
						
		while (rs.next()) {
			l.add(this.makePost(rs));			
		}
		
		rs.close();
		p.close();
				
		return l;
	}

	/**
	 * {@inheritDoc}
	 */
	public List selectAllByTopicByUser(int topicId, int userId) throws Exception
	{
		List l = new ArrayList();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PostModel.selectAllByTopicByUser"));
		p.setInt(1, topicId);
		p.setInt(2, userId);
		
		ResultSet rs = p.executeQuery();
						
		while (rs.next()) {
			l.add(this.makePost(rs));			
		}
		
		rs.close();
		p.close();
				
		return l;
	}

	/**
	 * {@inheritDoc}
	 */
	public List selectAllByCategoryByUser(int categoryId, int userId) throws Exception
	{
		List l = new ArrayList();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PostModel.selectAllByCategoryByUser"));
		p.setInt(1, categoryId);
		p.setInt(2, userId);
		
		ResultSet rs = p.executeQuery();
						
		while (rs.next()) {
			l.add(this.makePost(rs));			
		}
		
		rs.close();
		p.close();
				
		return l;
	}
}
