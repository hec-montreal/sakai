/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/GenericTreeGroupDAO.java $ 
 * $Id: GenericTreeGroupDAO.java 55493 2008-12-01 22:55:48Z murthy@etudes.org $ 
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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.JForum;
import org.etudes.jforum.util.GroupNode;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.sakaiproject.tool.cover.ToolManager;
//Mallika's import end

/**
 * @author Rafael Steil
 */
/*
 * 8/16/05 - Mallika - adding code to also include courseid in the equation
 * 9/15/05 - Mallika - changing code to add Participant first
 */
public class GenericTreeGroupDAO implements org.etudes.jforum.dao.TreeGroupDAO 
{
	private static Log logger = LogFactory.getLog(GenericTreeGroupDAO.class);
	
	/** 
	 * @see org.etudes.jforum.dao.TreeGroupDAO#selectGroups(int)
	 */
	public List selectGroups(int parentId) throws Exception 
	{
		List list = new ArrayList();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("TreeGroup.selectGroup"));
		p.setInt(1, parentId);
		//Mallika's new code beg
		p.setString(2, ToolManager.getCurrentPlacement().getContext());
		//Mallika's new code end
		
		if (logger.isDebugEnabled()) logger.debug("Executing the new select groups code");
		ResultSet rs = p.executeQuery();
		GroupNode partNode = null;
		while (rs.next()) {
				
			//Mallika's new code beg
			if (rs.getString("group_name").equals("Participant"))
			{
				partNode = new GroupNode();
				partNode.setName(rs.getString("group_name"));
				partNode.setId(rs.getInt("group_id"));
			}
			else
			{	
			  GroupNode n = new GroupNode();	
			  n.setName(rs.getString("group_name"));
			  n.setId(rs.getInt("group_id"));
			  if (logger.isDebugEnabled()) logger.debug("Group name is "+rs.getString("group_name"));
			  list.add(n);
			}  
			//Mallika's new code end
			
			//Mallika's comments beg
			/*  GroupNode n = new GroupNode();	
			  n.setName(rs.getString("group_name"));
			  n.setId(rs.getInt("group_id"));
			  if (logger.isInfoEnabled()) logger.info("Group name is "+rs.getString("group_name"));
			  list.add(n);*/
			//Mallika's comments end  
		}
		
		//Mallika's new code beg
		if (partNode != null)
		{
			list.add(0,partNode);
		}
		//Mallika's new code end	
			
		
		rs.close();
		p.close();
		
		return list;
	}
}
