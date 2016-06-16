/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/search/quartz/QuartzSearchIndexerJob.java $ 
 * $Id: QuartzSearchIndexerJob.java 62396 2009-08-07 17:40:09Z murthy@etudes.org $ 
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
package org.etudes.jforum.util.search.quartz;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.Properties;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.DBConnection;
import org.etudes.jforum.cache.CacheEngine;
import org.etudes.jforum.cache.Cacheable;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.ScheduledSearchIndexerDAO;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author Rafael Steil
 */
public class QuartzSearchIndexerJob implements Job, Cacheable
{
	private static final String FQN = "quartz";
	private static final String INDEXING = "indexing";
	private static Log logger = LogFactory.getLog(QuartzSearchIndexerJob.class);
	private static CacheEngine cache;
	
	/**
	 * @see org.etudes.jforum.cache.Cacheable#setCacheEngine(org.etudes.jforum.cache.CacheEngine)
	 */
	public void setCacheEngine(CacheEngine engine)
	{
		cache = engine;
	}
	
	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext context) throws JobExecutionException
	{
		if ("1".equals(cache.get(FQN, INDEXING))) {
			logger.info("Indexing is already running. Going home...");
			return;
		}
		
		Properties p = this.loadConfig();
		
		if (p == null) {
			return;
		}
		
		Connection connStatus = null;
		// if status is started return from processing the indexing. Update database the status of the indexing to started 
		try
		{
			if (logger.isDebugEnabled()) logger.debug("Getting the index status");
				
			connStatus = DBConnection.getImplementation().getConnection();
			boolean statusStarted = DataAccessDriver.getInstance().newScheduledSearchIndexerDAO().indexingStatus(connStatus);
			
			if (logger.isDebugEnabled()) logger.debug("The index status is "+ statusStarted);
			
			if (statusStarted)
			{
				if (connStatus != null) {
					DBConnection.getImplementation().releaseConnection(connStatus);
				}
				return;
			}
			else
			{
				DataAccessDriver.getInstance().newScheduledSearchIndexerDAO().addStatus(connStatus);
			}
				
		}
		catch (Exception e1)
		{
			if (logger.isErrorEnabled()) 
				logger.error("Error while getting the status." + e1, new Throwable(e1));
			
			if (connStatus != null) {
				DBConnection.getImplementation().releaseConnection(connStatus);
			}
			
			return;
		}
		
		int step = Integer.parseInt(p.getProperty(ConfigKeys.QUARTZ_CONTEXT + ConfigKeys.SEARCH_INDEXER_STEP));
		
		Connection conn = null;
		boolean autoCommit = false;
		
		try {
			conn = DBConnection.getImplementation().getConnection();
			autoCommit = conn.getAutoCommit();
			conn.setAutoCommit(true);
			
			cache.add(FQN, INDEXING, "1");
			
			ScheduledSearchIndexerDAO dao = DataAccessDriver.getInstance().newScheduledSearchIndexerDAO();
			dao.index(step, conn);
		}
		catch (Exception e) {
			logger.error("Error while trying to index messagez. Cannot proceed. " + e);
			e.printStackTrace();
		}
		finally {
			cache.remove(FQN, INDEXING);
			
			if (conn != null) {
				try { conn.setAutoCommit(autoCommit); } catch (Exception e) {}
				DBConnection.getImplementation().releaseConnection(conn);
			}
			
			// status related
			try
			{
				DataAccessDriver.getInstance().newScheduledSearchIndexerDAO().deleteStatus(connStatus);
			}
			catch (Exception e)
			{
				if (logger.isErrorEnabled()) 
					logger.error("Error while deleting the status." + e, new Throwable(e));
			}
			finally
			{
				if (connStatus != null) {
					DBConnection.getImplementation().releaseConnection(connStatus);
				}
			}
		}
	}
	
	private Properties loadConfig()
	{
		String filename = SystemGlobals.getValue(ConfigKeys.SEARCH_INDEXER_QUARTZ_CONFIG);
		
		try {
			Properties p = new Properties();
			p.load(new FileInputStream(filename));

			return p;
		}
		catch (Exception e) {
			logger.warn("Failed to load " + filename + ": " + e, e);
			return null;
		}
	}
}
