/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/sqlserver/SqlServerPostDAO.java $ 
 * $Id: SqlServerPostDAO.java 55488 2008-12-01 22:19:38Z murthy@etudes.org $ 
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
package org.etudes.jforum.dao.sqlserver;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.JForum;
import org.etudes.jforum.entities.Post;
import org.etudes.jforum.util.preferences.SystemGlobals;

/**
 * @author Andre de Andrade da Silva - andre.de.andrade@gmail.com
 * @version $Id: SqlServerPostDAO.java 55488 2008-12-01 22:19:38Z murthy@etudes.org $
 */
public class SqlServerPostDAO extends org.etudes.jforum.dao.generic.GenericPostDAO
{
	private static Log logger = LogFactory.getLog(SqlServerPostDAO.class);
	/**
	 * @see org.etudes.jforum.dao.PostDAO#selectById(int)
	 */
	public Post selectById(int postId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PostModel.selectById"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		p.setInt(1, postId);
		
		ResultSet rs = p.executeQuery();
		
		Post post = new Post();
		
		if (rs.next()) {
			post = this.makePost(rs);
		}
		
		rs.close();
		p.close();
		
		return post;
	}
	
	/** 
	 * @see org.etudes.jforum.dao.PostDAO#selectAllByTopicByLimit(int, int, int)
	 */
	public List selectAllByTopicByLimit(int topicId, int startFrom, int count) throws Exception
	{
		List l = new ArrayList();

		String top = SystemGlobals.getSql("GenericModel.selectByLimit");
        
        PreparedStatement p = JForum.getConnection().prepareStatement(top + " " + count + " " + SystemGlobals.getSql("PostModel.selectAllByTopicByLimit1")  + " " + top + " " + startFrom + " " + SystemGlobals.getSql("PostModel.selectAllByTopicByLimit2"), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        if (logger.isDebugEnabled()) logger.debug(top + " " + count + " " + SystemGlobals.getSql("PostModel.selectAllByTopicByLimit1")  + " " + top + " " + startFrom + " " + SystemGlobals.getSql("PostModel.selectAllByTopicByLimit2"));
        p.setInt(1, topicId);
        p.setInt(2, topicId);        

		ResultSet rs = p.executeQuery();

		while (rs.next()) {
			l.add(this.makePost(rs));
		}
		
		rs.close();
		p.close();
				
		return l;
	}
	
}
