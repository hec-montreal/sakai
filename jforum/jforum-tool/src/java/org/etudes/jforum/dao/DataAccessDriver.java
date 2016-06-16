/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/DataAccessDriver.java $ 
 * $Id: DataAccessDriver.java 69818 2010-08-17 23:57:01Z murthy@etudes.org $ 
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
package org.etudes.jforum.dao;



/**
 * The class that every driver class must implement.
 * JForum implementation provides a simple and extremely
 * configurable way to use diferent database engines without
 * any modification to the core source code. 
 * <br>
 * For example, if you want to use the Database "XYZ" as
 * backend, all you need to do is to implement <code>DataAccessDriver</code>,
 * all *Model classes and a specific file with the SQL queries. 
 * <br>
 * The default implementation was written to support MySQL, so if you want a base code to
 * analise, look at <code>net.jforum.drivers.generic</code> package.
 * 
 * @author Rafael Steil
 */
/**
 * 8/10/05 - Mallika - adding instance method for CourseCategoryDAO 8/17/05 -
 * Mallika - adding instance method for CourseGroupDAO 8/24/05 - Mallika -
 * adding instance method for CourseIntDAO 9/19/05 - Mallika - adding method for
 * CoursePrivateMessageDAO 10/17/05 - Mallika - adding method for CourseTimeDAO
 * 11/16/06 - Mallika - adding method for MarkTopicTimeDAO
 */
public abstract class DataAccessDriver 
{
	private static DataAccessDriver driver;
	
	protected DataAccessDriver() {}
	
	/**
	 * Starts the engine.
	 * This method should be called when the system
	 * is starting. 
	 * 
	 * @param implementation The dao.driver implementation
	 */
	public static final void init(DataAccessDriver implementation)
	{
		driver = implementation;
	}
	
	/**
	 * Gets a driver implementation instance. 
	 * You MUST use this method when you want a instance
	 * of a valid <code>DataAccessDriver</code>. Never access
	 * the driver implementation directly.  
	 * 
	 * @return <code>DataAccessDriver</code> instance
	 */
	public final static DataAccessDriver getInstance()
	{
		return driver;
	}
	
	/**
	 * Gets a {@link org.etudes.jforum.dao.ForumDAO} instance. 
	 * 
	 * @return <code>net.jforum.model.ForumModel</code> instance
	 */
	public abstract org.etudes.jforum.dao.ForumDAO newForumDAO();
	
	/**
	 * Gets a {@link net.jforum.dao.GroupDAO} instance
	 * 
	 * @return <code>net.jforum.model.GroupModel</code> instance.
	 */
	//public abstract  net.jforum.dao.GroupDAO newGroupDAO();
	
	/**
	 * Gets a {@link org.etudes.jforum.dao.PostDAO} instance.
	 * 
	 * @return <code>net.jforum.model.PostModel</code> instance.
	 */
	public abstract  org.etudes.jforum.dao.PostDAO newPostDAO();
	
	/**
	 * Gets a {@link org.etudes.jforum.dao.RankingDAO} instance.
	 * 
	 * @return <code>net.jforum.model.RankingModel</code> instance
	 */
	public abstract  org.etudes.jforum.dao.RankingDAO newRankingDAO();
	
	/**
	 * Gets a {@link org.etudes.jforum.dao.TopicDAO} instance.
	 * 
	 * @return <code>net.jforum.model.TopicModel</code> instance.
	 */
	public abstract  org.etudes.jforum.dao.TopicDAO newTopicDAO();
	
	/**
	 * Gets an {@link org.etudes.jforum.dao.UserDAO} instance.
	 * 
	 * @return <code>net.jforum.model.UserModel</code> instance.
	 */
	public abstract  org.etudes.jforum.dao.UserDAO newUserDAO();
	
	/**
	 * Gets an {@link org.etudes.jforum.dao.CategoryDAO} instance.
	 * 
	 * @return <code>net.jforum.model.CategoryModel</code> instance.
	 */
	public abstract  org.etudes.jforum.dao.CategoryDAO newCategoryDAO();
	
	/**
	 * Gets an {@link org.etudes.jforum.dao.TreeGroupDAO} instance
	 * 
	 * @return <code>net.jforum.model.TreeGroupModel</code> instance.
	 */
	public abstract org.etudes.jforum.dao.TreeGroupDAO newTreeGroupDAO();

	/**
	 * Gets a {@link org.etudes.jforum.dao.SmilieDAO} instance
	 * 
	 * @return <code>net.jforum.model.SmilieModel</code> instance.
	 */
	public abstract org.etudes.jforum.dao.SmilieDAO newSmilieDAO();
	
	/**
	 * Gets a {@link org.etudes.jforum.dao.SearchDAO} instance
	 * 
	 * @return <code>net.jforum.model.SearchModel</code> instance
	 */
	public abstract org.etudes.jforum.dao.SearchDAO newSearchDAO();
	
	/**
	 * Gets a {@link org.etudes.jforum.dao.SearchIndexerDAO} instance
	 * 
	 * @return <code>net.jforum.model.SearchIndexerModel</code> instance
	 */
	public abstract org.etudes.jforum.dao.SearchIndexerDAO newSearchIndexerDAO();
	
	/**
	 * Gets a {@link net.jforum.dao.security.UserSecurityDAO} instance
	 * 
	 * @return <code>net.jforum.model.security.UserSecurityModel</code> instance
	 */
	//public abstract net.jforum.dao.security.UserSecurityDAO newUserSecurityDAO();
	
	/**
	 * Gets a {@link net.jforum.dao.security.GroupSecurityDAO} instance
	 * 
	 * @return <code>net.jforum.model.security.GroupSecurityModel</code> instance
	 */
	//public abstract net.jforum.dao.security.GroupSecurityDAO newGroupSecurityDAO();

	/**
	 * Gets a {@link net.jforum.model.security.PrivateMessageDAO} instance
	 * 
	 * @return <code>link net.jforum.model.security.PrivateMessageModel</code> instance
	 */
	public abstract org.etudes.jforum.dao.PrivateMessageDAO newPrivateMessageDAO();
	
	/**
	 * Gets a {@link org.etudes.jforum.dao.UserSessionDAO} instance
	 * 
	 * @return <code>link net.jforum.model.UserSessionModel</code> instance
	 */
	public abstract org.etudes.jforum.dao.UserSessionDAO newUserSessionDAO();
	
	/**
	 * Gets a {@link org.etudes.jforum.dao.ConfigDAO} instance
	 * 
	 * @return <code>link net.jforum.model.ConfigModel</code> instance
	 */
	public abstract org.etudes.jforum.dao.ConfigDAO newConfigDAO();
	/**
	 * Gets a {@link org.etudes.jforum.dao.KarmaDAO} instance
	 * 
	 * @return <code>link net.jforum.model.KarmaModel</code> instance
	 */
	public abstract org.etudes.jforum.dao.KarmaDAO newKarmaDAO();
	
	/**
	 * Gets a {@link org.etudes.jforum.dao.BookmarkDAO} instance
	 * 
	 * @return <code>link net.jforum.model.BookmarkModel</code> instance
	 */
	public abstract org.etudes.jforum.dao.BookmarkDAO newBookmarkDAO();
	
	/**
	 * Gets a {@link org.etudes.jforum.dao.AttachmentDAO} instance
	 * 
	 * @return <code>link net.jforum.model.AttachmentModel</code> instance
	 */
	public abstract org.etudes.jforum.dao.AttachmentDAO newAttachmentDAO();
	
	/**
	 * Gets a {@link org.etudes.jforum.dao.ModerationDAO} instance
	 * 
	 * @return <code>link net.jforum.model.ModerationModel</code> instance
	 */
	public abstract org.etudes.jforum.dao.ModerationDAO newModerationDAO();
	
	/**
	 * Gets a {@link org.etudes.jforum.dao.ScheduledSearchIndexerDAO} instance
	 * 
	 * @return <code>link net.jforum.model.ScheduledSearchIndexerModel</code> instance
	 */
	public abstract org.etudes.jforum.dao.ScheduledSearchIndexerDAO newScheduledSearchIndexerDAO();

	/**
	 * Gets an {@link org.etudes.jforum.dao.BannerDAO} instance.
	 *
	 * @return <code>net.jforum.dao.BannerDAO</code> instance.
	 */
	public abstract org.etudes.jforum.dao.BannerDAO newBannerDAO();

	//Mallika's new code beg
	public abstract org.etudes.jforum.dao.generic.CourseCategoryDAO newCourseCategoryDAO();
	public abstract org.etudes.jforum.dao.generic.CourseGroupDAO newCourseGroupDAO();
	public abstract org.etudes.jforum.dao.generic.CourseInitDAO newCourseInitDAO();
	public abstract org.etudes.jforum.dao.generic.CoursePrivateMessageDAO newCoursePrivateMessageDAO();
	public abstract org.etudes.jforum.dao.CourseTimeDAO newCourseTimeDAO();
	
	public abstract org.etudes.jforum.dao.TopicMarkTimeDAO newTopicMarkTimeDAO();
	
	//Mallika's new code end
	
	public abstract org.etudes.jforum.dao.CourseImportDAO newCourseImportDAO();
	
	public abstract org.etudes.jforum.dao.GradeDAO newGradeDAO();
	
	public abstract org.etudes.jforum.dao.EvaluationDAO newEvaluationDAO();
	
	public abstract org.etudes.jforum.dao.SpecialAccessDAO newSpecialAccessDAO();
}
