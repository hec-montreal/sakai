/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/GenericKarmaDAO.java $ 
 * $Id: GenericKarmaDAO.java 55493 2008-12-01 22:55:48Z murthy@etudes.org $ 
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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.etudes.jforum.JForum;
import org.etudes.jforum.entities.Karma;
import org.etudes.jforum.entities.KarmaStatus;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.util.preferences.SystemGlobals;


/**
 * @author Rafael Steil
 */
public class GenericKarmaDAO implements org.etudes.jforum.dao.KarmaDAO
{
	/**
	 * @see org.etudes.jforum.dao.KarmaDAO#addKarma(org.etudes.jforum.entities.Karma)
	 */
	public void addKarma(Karma karma) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("KarmaModel.add"));
		p.setInt(1, karma.getPostId());
		p.setInt(2, karma.getPostUserId());
		p.setInt(3, karma.getFromUserId());
		p.setInt(4, karma.getPoints());
		p.setInt(5, karma.getTopicId());
		p.setTimestamp(6, new Timestamp((new Date()).getTime()));
		p.executeUpdate();
		p.close();

		this.updateUserKarma(karma.getPostUserId());
	}

	/**
	 * @see org.etudes.jforum.dao.KarmaDAO#selectKarmaStatus(int)
	 */
	public KarmaStatus getUserKarma(int userId) throws Exception
	{
		KarmaStatus status = new KarmaStatus();

		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("KarmaModel.getUserKarma"));
		p.setInt(1, userId);

		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			status.setKarmaPoints(Math.round(rs.getDouble("user_karma")));
		}

		rs.close();
		p.close();

		return status;
	}

	/**
	 * @see org.etudes.jforum.dao.KarmaDAO#updateUserKarma(int)
	 */
	public void updateUserKarma(int userId) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("KarmaModel.getUserKarmaPoints"));
		p.setInt(1, userId);

		int totalRecords = 0;
		double totalPoints = 0;
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			int points = rs.getInt("points");
			int votes = rs.getInt("votes");

			totalPoints += ((double) points / votes);
			totalRecords++;
		}

		rs.close();
		p.close();

		p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("KarmaModel.updateUserKarma"));
		p.setDouble(1, (double)totalPoints / totalRecords);
		p.setInt(2, userId);
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.KarmaDAO#update(org.etudes.jforum.entities.Karma)
	 */
	public void update(Karma karma) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("KarmaModel.update"));
		p.setInt(1, karma.getPoints());
		p.setInt(2, karma.getId());
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.KarmaDAO#getPostKarma(int)
	 */
	public KarmaStatus getPostKarma(int postId) throws Exception
	{
		KarmaStatus karma = new KarmaStatus();

		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("KarmaModel.getPostKarma"));
		p.setInt(1, postId);

		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			karma.setKarmaPoints(Math.round(rs.getDouble(1)));
		}

		rs.close();
		p.close();

		return karma;
	}

	/**
	 * @see org.etudes.jforum.dao.KarmaDAO#userCanAddKarma(int, int)
	 */
	public boolean userCanAddKarma(int userId, int postId) throws Exception
	{
		boolean status = true;

		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("KarmaModel.userCanAddKarma"));
		p.setInt(1, postId);
		p.setInt(2, userId);

		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			status = rs.getInt(1) < 1;
		}

		rs.close();
		p.close();

		return status;
	}

	/**
	 * @see org.etudes.jforum.dao.KarmaDAO#getUserVotes(int, int)
	 */
	public Map getUserVotes(int topicId, int userId) throws Exception
	{
		Map m = new HashMap();

		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("KarmaModel.getUserVotes"));
		p.setInt(1, topicId);
		p.setInt(2, userId);

		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			m.put(new Integer(rs.getInt("post_id")), new Integer(rs.getInt("points")));
		}

		rs.close();
		p.close();

		return m;
	}

	public void getUserTotalKarma(User user) throws SQLException
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("KarmaModel.getUserTotalVotes"));
		p.setInt(1, user.getId());

		ResultSet rs = p.executeQuery();

		user.setKarma(new KarmaStatus());

		if (rs.next()) {
			user.getKarma().setTotalPoints(rs.getInt("points"));
			user.getKarma().setVotesReceived(rs.getInt("votes"));
		}

		if (user.getKarma().getVotesReceived() != 0)// prevetns division by
			// zero.
			user.getKarma().setKarmaPoints(user.getKarma().getTotalPoints() / user.getKarma().getVotesReceived());

		this.getVotesGiven(user);

		rs.close();
		p.close();
	}

	private void getVotesGiven(User user) throws SQLException
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("KarmaModel.getUserGivenVotes"));
		p.setInt(1, user.getId());

		ResultSet rs = p.executeQuery();

		if (rs.next()) {
			user.getKarma().setVotesGiven(rs.getInt("votes"));
		}

		rs.close();
		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.KarmaDAO#getMostRatedUserByPeriod(java.util.Date, java.util.Date)
	 */
	public List getMostRatedUserByPeriod(int start, Date firstPeriod, Date lastPeriod, String orderField)
			throws Exception
	{
		String sql = SystemGlobals.getSql("KarmaModel.getMostRatedUserByPeriod");
		String orderby = " ORDER BY " + orderField + " DESC";
		sql += orderby;
		
		return this.getMostRatedUserByPeriod(sql, start, firstPeriod, lastPeriod, orderField);
	}

	/**
	 * 
	 * @param sql
	 * @param start
	 * @param firstPeriod
	 * @param lastPeriod
	 * @param orderField
	 * @return
	 * @throws Exception
	 */
	protected List getMostRatedUserByPeriod(String sql, int start, Date firstPeriod, Date lastPeriod, String orderField)
			throws Exception
	{
		if (firstPeriod.after(lastPeriod)) {
			throw new Exception("First Date needs to be before the Last Date");
		}
		
		PreparedStatement p = JForum.getConnection().prepareStatement(sql);
		p.setTimestamp(1, new Timestamp(firstPeriod.getTime()));
		p.setTimestamp(2, new Timestamp(lastPeriod.getTime()));

		ResultSet rs = p.executeQuery();
		List usersAndPoints = this.fillUser(rs);
		rs.close();
		p.close();

		return usersAndPoints;
	}

	protected List fillUser(ResultSet rs) throws SQLException
	{
		List usersAndPoints = new ArrayList();
		User user = null;
		KarmaStatus karma = null;
		while (rs.next()) {
			user = new User();
			karma = new KarmaStatus();
			karma.setTotalPoints(rs.getInt("total"));
			karma.setVotesReceived(rs.getInt("votes_received"));
			karma.setKarmaPoints(rs.getDouble("user_karma"));
			karma.setVotesGiven(rs.getInt("votes_given"));
			user.setUsername(rs.getString("username"));
			user.setId(rs.getInt("user_id"));
			user.setKarma(karma);
			usersAndPoints.add(user);
		}
		return usersAndPoints;
	}
}