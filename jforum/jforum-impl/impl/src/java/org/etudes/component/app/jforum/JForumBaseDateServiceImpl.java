/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-impl/impl/src/java/org/etudes/component/app/jforum/JForumBaseDateServiceImpl.java $ 
 * $Id: JForumBaseDateServiceImpl.java 71249 2010-11-09 00:29:12Z mallika@etudes.org $ 
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
 ***********************************************************************************/
package org.etudes.component.app.jforum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.api.app.jforum.JForumBaseDateService;
import org.sakaiproject.db.api.SqlService;

public class JForumBaseDateServiceImpl implements JForumBaseDateService
{
	private static Log logger = LogFactory.getLog(JForumBaseDateServiceImpl.class);

	/** Dependency: SqlService */
	protected SqlService sqlService = null;
	
	public void init()
	{
		if (logger.isInfoEnabled())
			logger.info("init....");
	}

	public void destroy()
	{
		if (logger.isInfoEnabled())
			logger.info("destroy....");
	}

	/**
	 * @see org.etudes.api.app.jforum.JForumBaseDateService#applyBaseDateTx(String, int)
	 */
	public void applyBaseDateTx(String course_id, int days_diff)
	{
		if (course_id == null)
		{
			throw new IllegalArgumentException("applyBaseDateTx: course_id is null");
		}
		if (days_diff == 0)
		{
			return;
		}
		StringBuilder sql = new StringBuilder();
		sql.append("UPDATE JFORUM_FORUMS JF, JFORUM_SAKAI_COURSE_CATEGORIES JSCC SET");
		sql.append(" JF.START_DATE=DATE_ADD(JF.START_DATE,INTERVAL ? DAY), JF.END_DATE=DATE_ADD(JF.END_DATE,INTERVAL ? DAY)");
		sql.append(" WHERE JF.CATEGORIES_ID=JSCC.CATEGORIES_ID AND JSCC.COURSE_ID =?");

		Object[] fields = new Object[3];
		int i = 0;
		fields[i++] = days_diff;
		fields[i++] = days_diff;
		fields[i++] = course_id;

		if (!sqlService.dbWrite(sql.toString(), fields)) {
			throw new RuntimeException("applyBaseDate: db write failed");
		}
		
		sql = new StringBuilder();
		sql.append("UPDATE JFORUM_CATEGORIES JC, JFORUM_SAKAI_COURSE_CATEGORIES JSCC SET");
		sql.append(" JC.START_DATE=DATE_ADD(JC.START_DATE,INTERVAL ? DAY), JC.END_DATE=DATE_ADD(JC.END_DATE,INTERVAL ? DAY)");
		sql.append(" WHERE JC.CATEGORIES_ID=JSCC.CATEGORIES_ID AND JSCC.COURSE_ID =?");

		fields = new Object[3];
		i = 0;
		fields[i++] = days_diff;
		fields[i++] = days_diff;
		fields[i++] = course_id;

		if (!sqlService.dbWrite(sql.toString(), fields)) {
			throw new RuntimeException("applyBaseDate: db write failed");
		}
	}
	
	
	/**
	 * @see org.etudes.api.app.jforum.JForumBaseDateService#getMinStartDate(String)
	 */
	public Date getMinStartDate(String course_id)
	{
		if (course_id == null)
		{
			return null;
		}

		PreparedStatement p = null;
		ResultSet rs = null;

		Connection conn = null;

		Date startDate = null, endDate = null, minForumDate = null, catStartDate = null, catEndDate = null;

		try
		{
			conn = sqlService.borrowConnection();
			String selectMinStartDate = "SELECT MIN(f.start_date) AS start_date, MIN(f.end_date) AS end_date "
					+ "FROM jforum_sakai_course_categories cc, jforum_categories c, jforum_forums f WHERE cc.course_id = ? "
					+ "AND cc.categories_id = c.categories_id AND c.categories_id = f.categories_id";

			p = conn.prepareStatement(selectMinStartDate);
			p.setString(1, course_id);

			rs = p.executeQuery();

			if (rs.next())
			{
				startDate = rs.getTimestamp("start_date");
				endDate = rs.getTimestamp("end_date");
				minForumDate = getMin(startDate, endDate);
			}
			
			rs.close();
			p.close();
			
			p = null;
			rs = null;
			
			String selectCatMinStartDate = "SELECT MIN(c.start_date) AS start_date, MIN(c.end_date) AS end_date "
					+ "FROM jforum_categories c, jforum_sakai_course_categories jscc "
					+ "WHERE c.categories_id = jscc.categories_id AND jscc.course_id = ?";
			
			p = conn.prepareStatement(selectCatMinStartDate);
			p.setString(1, course_id);

			rs = p.executeQuery();

			if (rs.next())
			{
				catStartDate = rs.getTimestamp("start_date");
				catEndDate = rs.getTimestamp("end_date");
				if (minForumDate == null)
				{
					//Get the minimum of cat start and end date
					minForumDate = getMin(catStartDate, catEndDate);
				}
				else
				{
					//Get the min of cat start and end date
					//compare it with minForumDate
					Date minCatDate = getMin(catStartDate, catEndDate);
					minForumDate = getMin(minForumDate, minCatDate);
				}	
			}
		}
		catch (SQLException e)
		{
			if (logger.isWarnEnabled())
				logger.warn(this.getClass().getName() + ".getMinStartDate() : " + e.getMessage(), e);
		}
		finally
		{
			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (SQLException e)
				{
				}
			}

			if (p != null)
			{
				try
				{
					p.close();
				}
				catch (SQLException e)
				{
				}
			}

			if (conn != null)
			{
				sqlService.returnConnection(conn);
			}
		}

		return minForumDate;
	}
	
	private Date getMin(Date startDate, Date endDate)
	{
		Date minDate = null;
		if ((startDate == null)&&(endDate == null)) return minDate;
		if ((startDate == null)&&(endDate != null)) return endDate;
		if ((startDate != null)&&(endDate == null)) return startDate;
		if ((startDate != null)&&(endDate != null))
		{
			if (startDate.before(endDate))
			  minDate = startDate;
			else
			  minDate = endDate;	
		}
		return minDate;
	}

	/**
	 * @return the sqlService
	 */
	public SqlService getSqlService()
	{
		return sqlService;
	}

	/**
	 * @param sqlService
	 *            the sqlService to set
	 */
	public void setSqlService(SqlService sqlService)
	{
		this.sqlService = sqlService;
	}

}