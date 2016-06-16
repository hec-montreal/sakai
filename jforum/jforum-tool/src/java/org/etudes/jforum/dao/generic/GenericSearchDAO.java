/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/GenericSearchDAO.java $ 
 * $Id: GenericSearchDAO.java 55493 2008-12-01 22:55:48Z murthy@etudes.org $ 
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.JForum;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.dao.SearchData;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.sakaiproject.tool.cover.ToolManager;
//Mallika's import end

/**
 * @author Rafael Steil
 * 10/10/05 - Mallika - adding courseId to the search by word statement
 */
public class GenericSearchDAO implements org.etudes.jforum.dao.SearchDAO	
{
	private static final Log logger = LogFactory.getLog(GenericSearchDAO.class);
	
	/** 
	 * @see org.etudes.jforum.dao.SearchDAO#search(org.etudes.jforum.dao.SearchData)
	 */
	public List search(SearchData sd) throws Exception 
	{
		List l = new ArrayList();
		List topics = new ArrayList();
		
		// Check for the search cache
		if (!sd.getSearchStarted()) {
			if (sd.getTime() == null) {
				this.topicsByKeyword(sd);
			}
			else {
				this.topicsByTime(sd);
			}
		}
		
		StringBuffer criterias = new StringBuffer(256);
		if (sd.getForumId() != 0) {
			criterias.append(" AND t.forum_id = "+ sd.getForumId());
		}
		
		if (sd.getCategoryId() != 0) {
			criterias.append(" AND f.categories_id = "+ sd.getCategoryId());
		}
		
		if (sd.getOrderByField() == null || sd.getOrderByField().equals("")) {
			sd.setOrderByField("p.post_time");
		}
		
		String sql = SystemGlobals.getSql("SearchModel.searchBase");
		// Prepare the query
		sql = sql.replaceAll(":orderByField:", sd.getOrderByField());
		sql = sql.replaceAll(":orderBy:", sd.getOrderBy());
		sql = sql.replaceAll(":criterias:", criterias.toString());
		
		if (logger.isDebugEnabled()) logger.debug("Search SQL = " + sql);
		
		PreparedStatement p = JForum.getConnection().prepareStatement(sql);
		p.setString(1, SessionFacade.getUserSession().getSessionId());
		p.setString(2, SessionFacade.getUserSession().getSessionId());

		ResultSet rs = p.executeQuery();
		
		l = new GenericTopicModelDAO().fillTopicsData(rs);
		
		rs.close();
		p.close();
		
		return l;
	}
	
	// Find topics by time
	private void topicsByTime(SearchData sd) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("SearchModel.searchByTime"));
		p.setString(1, SessionFacade.getUserSession().getSessionId());
		p.setTimestamp(2, new Timestamp(sd.getTime().getTime()));
		p.executeUpdate();
		p.close();
		
		this.selectTopicData();
	}
	
	// Given a set of keywords, find the topics
	private void topicsByKeyword(SearchData sd) throws Exception
	{
		boolean isLike = "like".equals(SystemGlobals.getValue(ConfigKeys.SEARCH_WORD_MATCHING).trim());
		
		String sql = isLike 
			? SystemGlobals.getSql("SearchModel.searchByLikeWord")
			: SystemGlobals.getSql("SearchModel.searchByWord");
		
		PreparedStatement p = JForum.getConnection().prepareStatement(sql);

		Map eachWordMap = new HashMap();
		
		
		String context = ToolManager.getCurrentPlacement().getContext();
		
		// Get the post ids to which the words are associated to
		for (int i = 0; i < sd.getKeywords().length; i++) {
			if (isLike) {
				 //Mallika's new code beg
				p.setString(1, context);
				//Mallika's new code end, line below changes from 1 to 2				
				p.setString(2, "%" + sd.getKeywords()[i].toLowerCase() + "%");
			}
			else {
				 //Mallika's new code beg
				p.setString(1, context);
				//Mallika's new code end, line below changes from 1 to 2
				p.setString(2, sd.getKeywords()[i].toLowerCase());
			}
			
			Set postsIds = new HashSet();
			ResultSet rs = p.executeQuery();
			
			while (rs.next()) {
				postsIds.add(new Integer(rs.getInt("post_id")));
			}
			
			if (postsIds.size() > 0) {
				eachWordMap.put(sd.getKeywords()[i], postsIds);
			}
		}
		
		// [wordName] = { each, post, id }
		
		// If seach type is OR, then get all words
		// If it is AND, then we want only the ids common to all words
		Set postsIds = null;
		
		if (sd.getUseAllWords()) {
			for (Iterator iter = eachWordMap.values().iterator(); iter.hasNext(); ) {
				if (postsIds == null) {
					postsIds = new HashSet(eachWordMap.values().size());
					postsIds.addAll((HashSet)iter.next());
				}
				else {
					postsIds.retainAll((HashSet)iter.next());
				}
			}
		}
		else {
			postsIds = new HashSet();
			
			for (Iterator iter = eachWordMap.values().iterator(); iter.hasNext(); ) {
				postsIds.addAll((HashSet)iter.next());
			}
		}
		
		if (postsIds == null || postsIds.size() == 0) {
			return;
		}
		
		// Time to get ready to search for the topics ids 
		StringBuffer sb = new StringBuffer(1024);
		for (Iterator iter = postsIds.iterator(); iter.hasNext(); ) {
			sb.append(iter.next()).append(",");
		}
		sb.delete(sb.length() - 1, sb.length());

		// Search for the ids, inserting them in the helper table 
		sql = SystemGlobals.getSql("SearchModel.insertTopicsIds");
		sql = sql.replaceAll(":posts:", sb.toString());
		p = JForum.getConnection().prepareStatement(sql);
		p.setString(1, SessionFacade.getUserSession().getSessionId());
		p.executeUpdate();
		
		// Now that we have the topics ids, it's time to make a copy from the 
		// topics table, to make the search faster ( damn, next version I'll 
		// remove the search functionality. Look for this code's size )
		this.selectTopicData();
		
		p.close();
	}
	
	private void selectTopicData() throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("SearchModel.selectTopicData"));
		p.setString(1, SessionFacade.getUserSession().getSessionId());
		p.setString(2, SessionFacade.getUserSession().getSessionId());
		p.executeUpdate();
		
		p.close();
	}
	

	/** 
	 * @see org.etudes.jforum.dao.SearchDAO#cleanSearch()
	 */
	public void cleanSearch() throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("SearchModel.cleanSearchTopics"));
		p.setString(1, SessionFacade.getUserSession().getSessionId());
		p.executeUpdate();
		
		p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("SearchModel.cleanSearchResults"));
		p.setString(1, SessionFacade.getUserSession().getSessionId());
		p.executeUpdate();
		p.close();
	}
}
