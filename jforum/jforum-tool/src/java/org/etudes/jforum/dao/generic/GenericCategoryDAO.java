/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/GenericCategoryDAO.java $ 
 * $Id: GenericCategoryDAO.java 69519 2010-07-30 21:09:43Z murthy@etudes.org $ 
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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.JForum;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.entities.Category;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.sakaiproject.tool.cover.ToolManager;

/**
 * @author Rafael Steil
*/
/**
 * 8/11/05 - Mallika - adding to code to insert course category object when
 * category object is inserted 8/17/05 - Mallika - adding method to pull all
 * categories per course, so these can be used to get just the forums for that
 * course
 */
public class GenericCategoryDAO extends AutoKeys implements org.etudes.jforum.dao.CategoryDAO 
{
	private static Log logger = LogFactory.getLog(GenericCategoryDAO.class);
	/**
	 * @see org.etudes.jforum.dao.CategoryDAO#selectById(int)
	 */
	public Category selectById(int categoryId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CategoryModel.selectById"));
		p.setInt(1, categoryId);
		
		ResultSet rs = p.executeQuery();
		
		Category c = new Category();
		if (rs.next()) {
			c = this.getCategory(rs);
		}
		
		rs.close();
		p.close();
		
		return c;
	}

	/** 
	 * @see org.etudes.jforum.dao.CategoryDAO#selectAll()
	 */
	public List selectAll() throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CategoryModel.selectAll"));
		
		
		List l = new ArrayList();
		//Mallika's comments
		if (logger.isDebugEnabled()) logger.debug("EXECUTING CATEGORYDAO-selectAll!!!!");
		ResultSet rs = p.executeQuery();		
		while (rs.next()) {
			l.add(this.getCategory(rs));		
		}
		
		rs.close();
		p.close();
			
		return l;
	}
	
	//New code Mallika - beg
	public List selectAllByCourse() throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CategoryModel.selectAllByCourseId"));
		p.setString(1, ToolManager.getCurrentPlacement().getContext());
		
		List l = new ArrayList();
		//Mallika's comments
		if (logger.isDebugEnabled()) logger.debug("EXECUTING CATEGORYDAO-selectAllByCourse!!!!");
		
		ResultSet rs = p.executeQuery();		
		while (rs.next()) {
			l.add(this.getCategory(rs));		
		}
		
		rs.close();
		p.close();
			
		return l;
	}	
	//New code Mallika - end
	
	protected Category getCategory(ResultSet rs) throws Exception
	{
		Category c = new Category();
		
		c.setId(rs.getInt("categories_id"));
		c.setName(rs.getString("title"));
		c.setOrder(rs.getInt("display_order"));	
		c.setModerated(rs.getInt("moderated") == 1);
		c.setArchived(rs.getInt("archived") == 1);
		c.setGradeCategory(rs.getInt("gradable") == 1);
		
		if (rs.getDate("start_date") != null)
		{
		  Timestamp startDate = rs.getTimestamp("start_date");
		  c.setStartDate(startDate);
		  SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
		  c.setStartDateFormatted(df.format(startDate));
	    }
	    else
	    {
		  c.setStartDate(null);
	    }
	    if (rs.getDate("end_date") != null)
	    {
	      Timestamp endDate = rs.getTimestamp("end_date");
		  c.setEndDate(endDate);
		  SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
		  c.setEndDateFormatted(df.format(endDate));
		  
		  c.setLockCategory(rs.getInt("lock_end_date") > 0);
	    }
	    else
	    {
			c.setEndDate(null);
	    }
		
		return c;
	}

	/** 
	 * @see org.etudes.jforum.dao.CategoryDAO#canDelete(int)
	 */
	public boolean canDelete(int categoryId) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CategoryModel.canDelete"));
		p.setInt(1, categoryId);
		
		ResultSet rs = p.executeQuery();
		if (!rs.next() || rs.getInt("total") < 1) {		
			return true;
		}
		
		rs.close();
		p.close();
		
		return false;
	}

	/**
	 * @see org.etudes.jforum.dao.CategoryDAO#delete(int)
	 */
	public void delete(int categoryId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CategoryModel.delete"));
		p.setInt(1, categoryId);
		p.executeUpdate();
		
		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.CategoryDAO#update(net.jforum.Category)
	 */
	public void update(Category category) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CategoryModel.update"));		
		p.setString(1, category.getName());
		p.setInt(2, category.isModerated() ? 1 : 0);
		p.setInt(3, category.isGradeCategory() ? 1 : 0);
		if (category.getStartDate() == null)
		{
			p.setTimestamp(4, null);
		} else
		{
			p.setTimestamp(4, new Timestamp(category.getStartDate().getTime()));
		}
		if (category.getEndDate() == null)
		{
			p.setTimestamp(5, null);
			p.setInt(6, 0);
		} else
		{
			p.setTimestamp(5, new Timestamp(category.getEndDate().getTime()));
			p.setInt(6, category.isLockCategory() ? 1 : 0);
		}
		p.setInt(7, category.getId());
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see org.etudes.jforum.dao.CategoryDAO#addNew(net.jforum.Category)
	 */
	public int addNew(Category category) throws Exception 
	{
		int order = 1;
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CategoryModel.getMaxOrder"));
		
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			order = rs.getInt(1) + 1;
		}
		rs.close();
		p.close();

		p = this.getStatementForAutoKeys("CategoryModel.addNew");
		p.setString(1, category.getName());
		p.setInt(2, order);
		p.setInt(3, category.isModerated() ? 1 : 0);
		p.setInt(4, category.isArchived() ? 1 : 0);
		p.setInt(5, category.isGradeCategory() ? 1 : 0);
		
		if (category.getStartDate() == null)
		{
			p.setTimestamp(6, null);
		} else
		{
			p.setTimestamp(6, new Timestamp(category.getStartDate().getTime()));
		}

		if (category.getEndDate() == null)
		{
			p.setTimestamp(7, null);
			p.setInt(8, 0);
		} else
		{
			p.setTimestamp(7, new Timestamp(category.getEndDate().getTime()));
			p.setInt(8, category.isLockCategory() ? 1 : 0);
		}
		
		this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("CategoryModel.lastGeneratedCategoryId"));
		int id = this.executeAutoKeysQuery(p);
		
		p.close();
		category.setId(id);
		category.setOrder(order);
		DataAccessDriver.getInstance().newCourseCategoryDAO().addNew(category);
		
		return id;
	}
	
	/**
	 * @see org.etudes.jforum.dao.CategoryDAO#setOrderUp(Category, Category)
	 */
	public void setOrderUp(Category category, Category relatedCategory) throws Exception 
	{
		this.setOrder(category, relatedCategory, true);
	}
	
	/**
	 * @see org.etudes.jforum.dao.CategoryDAO#setOrderDown(Category, Category)
	 */
	public void setOrderDown(Category category, Category relatedCategory) throws Exception 
	{
		this.setOrder(category, relatedCategory, false);
	}
	
	private void setOrder(Category category, Category otherCategory, boolean up) throws Exception
	{
		int tmpOrder = otherCategory.getOrder();
		otherCategory.setOrder(category.getOrder());
		category.setOrder(tmpOrder);

		// *********
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CategoryModel.setOrderById"));
		p.setInt(1, otherCategory.getOrder());
		p.setInt(2, otherCategory.getId());
		p.executeUpdate();
		p.close();

		// *********
		p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CategoryModel.setOrderById"));
		p.setInt(1, category.getOrder());
		p.setInt(2, category.getId());
		p.executeUpdate();
		p.close();
	}

	public Category selectArchiveCategory() throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CategoryModel.selectSiteArciveCategory"));
		p.setString(1, ToolManager.getCurrentPlacement().getContext());
		
		ResultSet rs = p.executeQuery();
		
		Category c = null;
		if (rs.next()) {
			c = this.getCategory(rs);
		}
		
		rs.close();
		p.close();
		
		return c;
	}

	/**
	 * @see org.etudes.jforum.dao.CategoryDAO#updateDates(Category)
	 */
	public void updateDates(Category category) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("CategoryModel.updateDates"));

		if (category.getStartDate() == null)
		{
		  p.setTimestamp(1, null);
		}
		else
		{
		  p.setTimestamp(1, new Timestamp(category.getStartDate().getTime()));
		}
		
		if (category.getEndDate() == null)
		{
		  p.setTimestamp(2, null);
		  p.setInt(3, 0);
		}
		else
		{
		  p.setTimestamp(2, new Timestamp(category.getEndDate().getTime()));
		  p.setInt(3, category.isLockCategory() ? 1 : 0);
		}		
		
		p.setInt(4, category.getId());
		
		p.executeUpdate();
		p.close();
		
	}
}
