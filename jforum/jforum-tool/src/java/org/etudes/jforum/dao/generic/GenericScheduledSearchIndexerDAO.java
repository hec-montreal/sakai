/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/GenericScheduledSearchIndexerDAO.java $ 
 * $Id: GenericScheduledSearchIndexerDAO.java 62396 2009-08-07 17:40:09Z murthy@etudes.org $ 
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.SearchIndexerDAO;
import org.etudes.jforum.entities.Post;
import org.etudes.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 */
public class GenericScheduledSearchIndexerDAO implements org.etudes.jforum.dao.ScheduledSearchIndexerDAO
{
	private static Log logger = LogFactory.getLog(GenericScheduledSearchIndexerDAO.class);
	
	/**
	 * @see org.etudes.jforum.dao.ScheduledSearchIndexerDAO#index(int)
	 */
	public int index(int step, Connection conn) throws Exception
	{
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		
		// Get the last post id so far
		PreparedStatement p = conn.prepareStatement(SystemGlobals.getSql("SearchModel.maxPostIdUntilNow"));
		p.setTimestamp(1, timestamp);

		ResultSet rs = p.executeQuery();
		int maxPostId = -1;
		
		if (rs.next()) {
			maxPostId = rs.getInt(1);
		}
		
		// Get the latest indexed post
		rs.close();
		p.close();
		
		int latestIndexedPostId = -1;
		
		p = conn.prepareStatement(SystemGlobals.getSql("SearchModel.lastIndexedPostId"));
		rs = p.executeQuery();
		
		if (rs.next()) {
			latestIndexedPostId = rs.getInt(1);
		}
		
		rs.close();
		p.close();
		
		if (maxPostId == -1 || latestIndexedPostId == -1 || maxPostId <= latestIndexedPostId) {
			if (logger.isDebugEnabled()) logger.debug("No posts found to index. Leaving...");
			return 0;
		}
		
		if (logger.isDebugEnabled()) logger.debug("Going to index posts from " + latestIndexedPostId + " to " + maxPostId);
		
		// Count how many posts we have to index
		p = conn.prepareStatement(SystemGlobals.getSql("SearchModel.howManyToIndex"));
		p.setTimestamp(1, timestamp);
		p.setInt(2, latestIndexedPostId);
		
		rs = p.executeQuery();
		rs.next();
		
		int total = rs.getInt(1);
		
		rs.close();
		p.close();
		
		// Do the dirty job
		SearchIndexerDAO sim = DataAccessDriver.getInstance().newSearchIndexerDAO();
		sim.setConnection(conn);
		
		int start = 0;
		while (true) {
			List posts = this.getPosts(start, step, latestIndexedPostId, maxPostId, conn);
			
			if (posts.size() == 0) {
				break;
			}
			
			if (logger.isDebugEnabled()) logger.debug("Indexing range [" + start + ", " + (start + step) + "] from a total of " + total);
			
			sim.insertSearchWords(posts);
			
			start += step;
		}
		
		return maxPostId;
	}
	
	protected List getPosts(int start, int count, int minPostId, int maxPostId, Connection conn) throws Exception
	{
		List l = new ArrayList();
		
		PreparedStatement p = conn.prepareStatement(SystemGlobals.getSql("SearchModel.getPostsToIndex"));
		p.setInt(1, minPostId);
		p.setInt(2, maxPostId);
		p.setInt(3, start);
		p.setInt(4, count);
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			l.add(this.makePost(rs));
		}
		
		rs.close();
		p.close();
		
		return l;
	}
	
	protected String getPostTextFromResultSet(ResultSet rs) throws Exception
	{
		return rs.getString("post_text");
	}
	
	protected Post makePost(ResultSet rs) throws Exception
	{
		Post p = new Post();
		p.setId(rs.getInt("post_id"));
		p.setText(this.getPostTextFromResultSet(rs));
		p.setSubject("post_subject");
		
		return p;
	}

	public boolean indexingStatus(Connection conn) throws Exception
	{
		boolean status = false;
		
		PreparedStatement p = conn.prepareStatement(SystemGlobals.getSql("SearchModel.getIndexingStatus"));
					
		ResultSet rs = p.executeQuery();
		
		while (rs.next()){
			if (rs.getInt("status") > 1)
			 status = true;
		}
		
		rs.close();
		p.close();
		
		return status;
	}

	public void addStatus(Connection conn) throws Exception
	{
		PreparedStatement p = conn.prepareStatement(SystemGlobals.getSql("SearchModel.addIndexingStatus"));
		
		p.setInt(1, 1);
	
		
		p.execute();
		
		p.close();
		
	}

	public void deleteStatus(Connection conn) throws Exception
	{
		PreparedStatement p = conn.prepareStatement(SystemGlobals.getSql("SearchModel.deleteIndexingStatus"));
		
		p.execute();
		
		p.close();
		
	}
}
