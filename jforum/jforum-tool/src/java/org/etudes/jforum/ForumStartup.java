/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/ForumStartup.java $ 
 * $Id: ForumStartup.java 55370 2008-11-26 21:57:23Z murthy@etudes.org $ 
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
package org.etudes.jforum;

import java.sql.Connection;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//Mallika's import end
import org.etudes.jforum.dao.CategoryDAO;
import org.etudes.jforum.dao.ConfigDAO;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.ForumDAO;
import org.etudes.jforum.dao.generic.CourseCategoryDAO;
import org.etudes.jforum.exceptions.DatabaseException;
import org.etudes.jforum.exceptions.RepositoryStartupException;
import org.etudes.jforum.repository.ForumRepository;

/**
 * @author Rafael Steil
 * 8/4/05 - Mallika - Added method to start forum repository with course
 * 01/18/07 - Murthy - commented start repository code 
 */
public class ForumStartup 
{
	private static final Log logger = LogFactory.getLog(ForumStartup.class);
	
	/**
	 * Starts the database implementation
	 * @return <code>true</code> if everthing were ok
	 * @throws DatabaseException if something were wrong
	 */
	public static boolean startDatabase()
	{
		try {
			if (DBConnection.createInstance()) {
				DBConnection.getImplementation().init();
				
				// Check if we're in fact up and running
				Connection conn = DBConnection.getImplementation().getConnection();
				DBConnection.getImplementation().releaseConnection(conn);
			}
		}
		catch (Exception e) {
			throw new DatabaseException("Error while trying to start the database: " + e);
		}
		
		return true;
	}
	
	/**
	 * Starts the cache control for forums and categories.
	 * @throws RepositoryStartupException is something were wrong.
	 *//*
	public static void startForumRepository()
	{
		try {
			ForumDAO fm = DataAccessDriver.getInstance().newForumDAO();
			CategoryDAO cm = DataAccessDriver.getInstance().newCategoryDAO();
			ConfigDAO configModel = DataAccessDriver.getInstance().newConfigDAO();

			ForumRepository.start(fm, cm, configModel);
		}
		catch (Exception e) {
			throw new RepositoryStartupException("Error while trying to start ForumRepository: " + e);
		}
	}*/
	
	/**
	 * Method by Mallika
	 * Starts the cache control for forums and categories per course.
	 * @throws RepositoryStartupException is something were wrong.
	 *//*
	public static void startCourseForumRepository()
	{
		if (logger.isDebugEnabled()) logger.debug("StartCourseForumRep executing");
		try {
			ForumDAO fm = DataAccessDriver.getInstance().newForumDAO();
			CategoryDAO cm = DataAccessDriver.getInstance().newCategoryDAO();
			ConfigDAO configModel = DataAccessDriver.getInstance().newConfigDAO();
            CourseCategoryDAO ccm = DataAccessDriver.getInstance().newCourseCategoryDAO();
			
			ForumRepository.start(fm, cm, configModel, ccm);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RepositoryStartupException("Error while trying to start ForumRepository: ", e); // Rethrow the original exception -- JMH
		}
	}	
	//End code Mallika
*/}
