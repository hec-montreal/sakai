/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/GenericCourseTimeDAO.java $ 
 * $Id: GenericCourseTimeDAO.java 66005 2010-02-04 22:40:50Z murthy@etudes.org $ 
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

import org.etudes.jforum.JForum;
import org.etudes.jforum.dao.CourseTimeDAO;
import org.etudes.jforum.entities.CourseTimeObj;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.sakaiproject.tool.cover.ToolManager;
//Mallika's import end
/**
 * @author Mallika M Thoppay
 * 8/17/05 - Mallika - file created
 * 10/2/06 - Mallika - adding methods to set mark all time
*/
public class GenericCourseTimeDAO implements CourseTimeDAO 
{
	/* (non-Javadoc)
	 * @see org.etudes.jforum.dao.generic.CourseTimeDAO#selectVisitTime(int)
	 */
	public CourseTimeObj selectVisitTime(int userId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CourseTimeModel.selectTime"));
		p.setString(1,ToolManager.getCurrentPlacement().getContext());
		p.setInt(2,userId);
		
	
		ResultSet rs = p.executeQuery();
		
		CourseTimeObj cto = null;
		
	
		while (rs.next()) {
			cto = new CourseTimeObj();
			cto.setVisitTime(rs.getTimestamp("visit_time"));
			cto.setCourseId(ToolManager.getCurrentPlacement().getContext());
			cto.setUserId(userId);
		}
		
		rs.close();
		p.close();
		
		return cto;
	}
	
	/* (non-Javadoc)
	 * @see org.etudes.jforum.dao.generic.CourseTimeDAO#addNew(org.etudes.jforum.entities.CourseTimeObj)
	 */
	public void addNew(CourseTimeObj cto) throws Exception 
	{
		//System.out.println("CourseTimedao before prepare");
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CourseTimeModel.addNew"));
		//System.out.println("IN courseTimeDAO "+cto.getCourseId());
		p.setString(1, cto.getCourseId());
		p.setInt(2, cto.getUserId());
		p.setTimestamp(3, new java.sql.Timestamp(cto.getVisitTime().getTime()));
		
		p.execute();
		
		p.close();
	
	}	
	/* (non-Javadoc)
	 * @see org.etudes.jforum.dao.generic.CourseTimeDAO#update(org.etudes.jforum.entities.CourseTimeObj)
	 */
	public void update(CourseTimeObj cto) throws Exception 
	{
		//System.out.println("Course Group delete working");
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CourseTimeModel.update"));
		p.setTimestamp(1, new java.sql.Timestamp(cto.getVisitTime().getTime()));
		p.setString(2, cto.getCourseId());
		p.setInt(3, cto.getUserId());
		p.executeUpdate();
		
		p.close();
	}
	
	/* (non-Javadoc)
	 * @see org.etudes.jforum.dao.generic.CourseTimeDAO#selectMarkAllTime(int)
	 */
	public CourseTimeObj selectMarkAllTime(int userId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CourseTimeModel.selectMarkAllTime"));
		p.setString(1,ToolManager.getCurrentPlacement().getContext());
		p.setInt(2,userId);
		
	
		ResultSet rs = p.executeQuery();
		
		CourseTimeObj cto = null;
		
	
		while (rs.next()) {
			cto = new CourseTimeObj();
			cto.setMarkAllTime(rs.getTimestamp("markall_time"));
			cto.setCourseId(ToolManager.getCurrentPlacement().getContext());
			cto.setUserId(userId);
		}
		
		rs.close();
		p.close();
		
		return cto;
	}
	
	/* (non-Javadoc)
	 * @see org.etudes.jforum.dao.generic.CourseTimeDAO#addMarkAllNew(org.etudes.jforum.entities.CourseTimeObj)
	 */
	public void addMarkAllNew(CourseTimeObj cto) throws Exception 
	{
		//System.out.println("CourseTimedao before prepare");
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CourseTimeModel.addMarkAllNew"));
		//System.out.println("IN courseTimeDAO "+cto.getCourseId());
		p.setString(1, cto.getCourseId());
		p.setInt(2, cto.getUserId());
		p.setTimestamp(3, new java.sql.Timestamp(cto.getMarkAllTime().getTime()));
		
		p.execute();
		
		p.close();
	
	}	
	/* (non-Javadoc)
	 * @see org.etudes.jforum.dao.generic.CourseTimeDAO#updateMarkAllTime(org.etudes.jforum.entities.CourseTimeObj)
	 */
	public void updateMarkAllTime(CourseTimeObj cto) throws Exception 
	{
		//System.out.println("Course Group delete working");
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CourseTimeModel.updateMarkAllTime"));
		p.setTimestamp(1, new java.sql.Timestamp(cto.getMarkAllTime().getTime()));
		p.setString(2, cto.getCourseId());
		p.setInt(3, cto.getUserId());
		p.executeUpdate();
		
		p.close();
	}
		
	
}
