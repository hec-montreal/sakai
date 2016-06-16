/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/CourseCategoryDAO.java $ 
 * $Id: CourseCategoryDAO.java 55493 2008-12-01 22:55:48Z murthy@etudes.org $ 
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


import org.etudes.jforum.JForum;
import org.etudes.jforum.entities.Category;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.sakaiproject.tool.cover.ToolManager;
//Mallika's import end
/**
 * @author Mallika M Thoppay
 * 8/9/05 - Mallika - selects from the Course Category table
 * 8/10/05 - Mallika - adding delete method that deletes from Course Category table
 * 8/11/05 - Mallika - adding code to add new coursecategory entry
 */
public class CourseCategoryDAO 
{
	/**
	 * @see 
	 */
	public List selectAll() throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CourseCategoryModel.selectAll"));
		
		List l = new ArrayList();
		ResultSet rs = p.executeQuery();
		
		CourseCategory c = new CourseCategory();
		while (rs.next()) {
			c = this.getCourseCategory(rs);
			l.add(c);
		}
		
		rs.close();
		p.close();
		
		return l;
	}
	
	protected CourseCategory getCourseCategory(ResultSet rs) throws Exception
	{
		CourseCategory c = new CourseCategory();
		
		c.setCategoryId(rs.getInt("categories_id"));
		c.setCourseId(rs.getString("course_id"));
		
		return c;
	}
	public void addNew(Category category) throws Exception 
	{
		int order = 1;
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CourseCategoryModel.addNew"));
		
		p.setString(1, ToolManager.getCurrentPlacement().getContext());
		p.setInt(2, category.getId());
		
		p.execute();
		
		p.close();
	
	}	
	/**
	 * @see 
	 */
	public void delete(int categoryId) throws Exception 
	{
		//if (logger.isInfoEnabled()) logger.info("Course Category delete working");
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CourseCategoryModel.delete"));
		p.setInt(1, categoryId);
		p.executeUpdate();
		
		p.close();
	}
	
}
