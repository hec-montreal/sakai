/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/GenericTopicMarkTimeDAO.java $ 
 * $Id: GenericTopicMarkTimeDAO.java 66237 2010-02-19 23:48:58Z murthy@etudes.org $ 
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.JForum;
import org.etudes.jforum.dao.TopicMarkTimeDAO;
import org.etudes.jforum.entities.TopicMarkTimeObj;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.sakaiproject.tool.cover.ToolManager;
//Mallika's import end
/**
 * @author Mallika M Thoppay
 * @version $Id: GenericTopicMarkTimeDAO.java 66237 2010-02-19 23:48:58Z murthy@etudes.org $
 * 11/15/06 - New DAO class for topic mark times
*/
public class GenericTopicMarkTimeDAO implements TopicMarkTimeDAO 
{
	private int DUPLICATE_KEY = 1062;
	
	private static Log logger = LogFactory.getLog(GenericTopicMarkTimeDAO.class);
	
	/**
	 * @see org.etudes.jforum.dao.TopicMarkTimeDAO#addMarkTime(int, int, Date, boolean)
	 */
	public void addMarkTime(int topicId, int userId, Date markTime, boolean isRead) throws Exception
	{
		//System.out.println("Adding mark time topicId:"+topicId+" userId:"+userId);
      PreparedStatement stmt = JForum.getConnection(). prepareStatement( SystemGlobals.getSql("TopicMarkModel.addMarkTime"));

		try {
			stmt.setInt(1, topicId);
			stmt.setInt(2, userId);
			stmt.setTimestamp(3, new java.sql.Timestamp(markTime.getTime()));
			stmt.setInt(4, isRead ? 0 : 1);

			stmt.executeUpdate();
			//System.out.println("After insert");
		} catch(SQLException e) {
			System.out.println("Exceptio "+e.toString());
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
	 * @see org.etudes.jforum.dao.TopicMarkTimeDAO#selectMarkTime(int, int)
	 */
	public Date selectMarkTime(int topicId, int userId) throws Exception
	{
		PreparedStatement stmt = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicMarkModel.selectMarkTime"));
		stmt.setInt(1, topicId);
		stmt.setInt(2, userId);

	    ResultSet rs = stmt.executeQuery();

	    Date markTime = null;
		while (rs.next()) {
			markTime = rs.getTimestamp("mark_time");
		}

		rs.close();
		stmt.close();

		return markTime;
	}

	/**
	 * @see org.etudes.jforum.dao.TopicMarkTimeDAO#selectTopicMarkTimes(int, int)
	 */
	public List selectTopicMarkTimes(int forumId, int userId) throws Exception
	{
		List l = new ArrayList();

		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicMarkModel.selectTopicTimes"));
		p.setInt(1, forumId);
		p.setInt(2, userId);
		
		ResultSet rs = p.executeQuery();
		
		l = this.fillTimeData(rs);
		
		rs.close();
		p.close();
		return l;		
		
	}
	
	/**
	 * @see org.etudes.jforum.dao.TopicMarkTimeDAO#selectCourseTopicMarkTimes(int)
	 */
	public List selectCourseTopicMarkTimes(int userId) throws Exception
	{
		List l = new ArrayList();

		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicMarkModel.selectCourseTopicTimes"));
		p.setInt(1, userId);
		p.setString(2, ToolManager.getCurrentPlacement().getContext());
		
		ResultSet rs = p.executeQuery();
		
		l = this.fillTimeData(rs);
		
		rs.close();
		p.close();
		return l;		
		
	}	
	
	protected List<TopicMarkTimeObj> fillTimeData(ResultSet rs) throws Exception
	{
      List<TopicMarkTimeObj> l = new ArrayList<TopicMarkTimeObj>();
		
		while (rs.next()) {
			TopicMarkTimeObj t = new TopicMarkTimeObj(); 
			t.setTopicId(rs.getInt("topic_id"));
			t.setUserId(rs.getInt("user_id"));
			t.setMarkTime(rs.getTimestamp("mark_time"));
			if (rs.getInt("is_read") == 1)
				t.setRead(false);
			else
				t.setRead(true);
			
			l.add(t);
		}
		
		return l;		
	}
	
	/**
	 * @see org.etudes.jforum.dao.TopicMarkTimeDAO#updateMarkTime(int, int, Date, boolean)
	 */
	public void updateMarkTime(int topicId, int userId, Date markTime, boolean isRead) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicMarkModel.updateMarkTime"));
		p.setTimestamp(1, new java.sql.Timestamp(markTime.getTime()));
		p.setInt(2, isRead ? 0 : 1);
		p.setInt(3, topicId);
		p.setInt(4, userId);		

		p.executeUpdate();
		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.TopicMarkTimeDAO#deleteMarkTime(int, int)
	 */
	public void deleteMarkTime(int topicId, int userId) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicMarkModel.deleteMarkTime"));

		p.setInt(1, topicId);
		p.setInt(2, userId);

		p.executeUpdate();
		p.close();
		
	}

	/**
	 * @see org.etudes.jforum.dao.TopicMarkTimeDAO#markTopicUnread(int, int, Date)
	 */
	public void markTopicUnread(int topicId, int userId, Date markTime) throws Exception
	{
		updateMarkTime(topicId, userId, markTime, false);
		
	}

	/**
	 * @see org.etudes.jforum.dao.TopicMarkTimeDAO#selectUnreadMarkedTopicsCount(int, int)
	 */
	public int selectUnreadMarkedTopicsCount(int forumId, int userId) throws Exception
	{
		int count = 0;
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicMarkModel.selectCountMarkedTopicsByForum"));
		p.setInt(1, forumId);
		p.setInt(2, userId);
		p.setInt(3, 1);
		
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			count = rs.getInt("topics_count");
		}
		
		rs.close();
		p.close();
		
		return count;
	}

	/**
	 * @see org.etudes.jforum.dao.TopicMarkTimeDAO#selectTopicMarkTime(int, int)
	 */
	public TopicMarkTimeObj selectTopicMarkTime(int topicId, int userId) throws Exception
	{
		TopicMarkTimeObj topicMarkTimeObj = null;
		
		PreparedStatement stmt = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicMarkModel.selectMarkTime"));
		stmt.setInt(1, topicId);
		stmt.setInt(2, userId);

	    ResultSet rs = stmt.executeQuery();

	    List<TopicMarkTimeObj> l = this.fillTimeData(rs);
		if (l.size() > 0) {
			topicMarkTimeObj = l.get(0);
		}

		rs.close();
		stmt.close();

		return topicMarkTimeObj;
	}

	/**
	 * @see org.etudes.jforum.dao.TopicMarkTimeDAO#selectUserCourseUnreadMarkedTopics(int)
	 */
	public List<TopicMarkTimeObj> selectUserCourseUnreadMarkedTopics(int userId) throws Exception
	{
		PreparedStatement stmt = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TopicMarkModel.selectUserUnreadMarkedTopicsByCourse"));
		stmt.setInt(1, userId);
		stmt.setInt(2, 1);
		stmt.setString(3, ToolManager.getCurrentPlacement().getContext());		

	    ResultSet rs = stmt.executeQuery();

	    List<TopicMarkTimeObj> l = this.fillTimeData(rs);

	    rs.close();
		stmt.close();

		return l;
	}

}
