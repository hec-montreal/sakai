/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/DataAccessDriver.java $ 
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
package org.etudes.jforum.dao.generic;

//import net.jforum.dao.generic.security.GenericGroupSecurityDAO;
//import net.jforum.dao.generic.security.GenericUserSecurityDAO;
import org.etudes.jforum.dao.CourseImportDAO;
import org.etudes.jforum.dao.CourseTimeDAO;
import org.etudes.jforum.dao.EvaluationDAO;
import org.etudes.jforum.dao.GradeDAO;
import org.etudes.jforum.dao.SpecialAccessDAO;
import org.etudes.jforum.dao.TopicMarkTimeDAO;

//Mallika's import end
/**
 * @author Rafael Steil
 */
/** 8/10/05 - Mallika - added method for CourseCategoryDAO
 * 8/17/05 - Mallika - adding method for CourseGroupDAO
 * 8/24/05 - Mallika - adding method for CourseInitDAO
 * 9/19/05 - Mallika - adding method for CoursePrivateMessageDAO
 * 10/17/05 - Mallika - adding method for CourseTimeDAO
 * 11/15/06 - Mallika - adding method for MarkTopicTime DAO
 */
public class DataAccessDriver extends org.etudes.jforum.dao.DataAccessDriver 
{
	//private static GenericGroupDAO groupDao = new GenericGroupDAO();
	private static GenericPostDAO postDao = new GenericPostDAO();
	private static GenericRankingDAO rankingDao = new GenericRankingDAO();
	private static GenericTopicModelDAO topicDao = new GenericTopicModelDAO();
	private static GenericUserDAO userDao = new GenericUserDAO();
	private static GenericTreeGroupDAO treeGroupDao = new GenericTreeGroupDAO();
	private static GenericSmilieDAO smilieDao = new GenericSmilieDAO();
	private static GenericSearchDAO searchDao = new GenericSearchDAO();
	//private static GenericUserSecurityDAO userSecurityDao = new GenericUserSecurityDAO();
	//private static GenericGroupSecurityDAO groupSecurityDao = new GenericGroupSecurityDAO();
	private static GenericPrivateMessageDAO privateMessageDao = new GenericPrivateMessageDAO();
	private static GenericUserSessionDAO userSessionDao = new GenericUserSessionDAO();
	private static GenericKarmaDAO karmaDao = new GenericKarmaDAO();
	private static GenericBookmarkDAO bookmarkDao = new GenericBookmarkDAO();
	private static GenericAttachmentDAO attachmentDao = new GenericAttachmentDAO();
	private static GenericModerationDAO moderationDao = new GenericModerationDAO();
	private static GenericForumDAO forumDao = new GenericForumDAO();
	private static GenericCategoryDAO categoryDao = new GenericCategoryDAO();
	private static GenericConfigDAO configDao = new GenericConfigDAO();
	private static GenericScheduledSearchIndexerDAO ssiDao = new GenericScheduledSearchIndexerDAO();
    private static GenericBannerDAO bannerDao = new GenericBannerDAO();

	//Mallika's new code beg
	private static CourseCategoryDAO ccDao = new CourseCategoryDAO();
	private static CourseGroupDAO cgDao = new CourseGroupDAO();
	private static CourseInitDAO ciDao = new CourseInitDAO();
	private static CoursePrivateMessageDAO cpmDao = new CoursePrivateMessageDAO();
	private static CourseTimeDAO ctimeDao = new GenericCourseTimeDAO();
	private static TopicMarkTimeDAO tmtimeDao = new GenericTopicMarkTimeDAO();
	
	//Mallika's new code end
	
	private static CourseImportDAO courseImportDao = new GenericCourseImportDAO();
	private static GenericGradeDAO gradeDao = new GenericGradeDAO();
	private static EvaluationDAO evaluationDAO = new GenericEvaluationDAO ();
	private static SpecialAccessDAO specialAccessDAO = new GenericSpecialAccessDAO();
	
	/**
	 * @see org.etudes.jforum.dao.DataAccessDriver#getForumModel()
	 */
	public org.etudes.jforum.dao.ForumDAO newForumDAO() 
	{
		return forumDao;	
	}

	/**
	 * @see net.jforum.dao.DataAccessDriver#getGroupModel()
	 */
	/*public net.jforum.dao.GroupDAO newGroupDAO() 
	{
		return groupDao;
	}*/

	/**
	 * @see org.etudes.jforum.dao.DataAccessDriver#getPostModel()
	 */
	public org.etudes.jforum.dao.PostDAO newPostDAO() 
	{
		return postDao;
	}

	/**
	 * @see org.etudes.jforum.dao.DataAccessDriver#getRankingModel()
	 */
	public org.etudes.jforum.dao.RankingDAO newRankingDAO() 
	{	
		return rankingDao;
	}

	/**
	 * @see org.etudes.jforum.dao.DataAccessDriver#getTopicModel()
	 */
	public org.etudes.jforum.dao.TopicDAO newTopicDAO() 
	{
		return topicDao;
	}

	/**
	 * @see org.etudes.jforum.dao.DataAccessDriver#getUserModel()
	 */
	public org.etudes.jforum.dao.UserDAO newUserDAO() 
	{
		return userDao;
	}

	/**
	 * @see org.etudes.jforum.dao.DataAccessDriver#newCategoryDAO()
	 */
	public org.etudes.jforum.dao.CategoryDAO newCategoryDAO() 
	{
		return categoryDao;
	}

	/**
	 * @see org.etudes.jforum.dao.DataAccessDriver#newTreeGroupDAO()
	 */
	public org.etudes.jforum.dao.TreeGroupDAO newTreeGroupDAO() 
	{
		return treeGroupDao;
	}
	
	/** 
	 * @see org.etudes.jforum.dao.DataAccessDriver#newSmilieDAO()
	 */
	public org.etudes.jforum.dao.SmilieDAO newSmilieDAO() 
	{
		return smilieDao;
	}
	
	/** 
	 * @see org.etudes.jforum.dao.DataAccessDriver#newSearchDAO()
	 */
	public org.etudes.jforum.dao.SearchDAO newSearchDAO() 
	{
		return searchDao;
	}
	
	/** 
	 * @see org.etudes.jforum.dao.DataAccessDriver#newSearchIndexerDAO()
	 */
	public org.etudes.jforum.dao.SearchIndexerDAO newSearchIndexerDAO() 
	{
		return new GenericSearchIndexerDAO();
	}
	
	/** 
	 * @see net.jforum.dao.DataAccessDriver#newGroupSecurityDAO()
	 */
	/*public net.jforum.dao.security.GroupSecurityDAO newGroupSecurityDAO() 
	{
		return groupSecurityDao;
	}*/

	/** 
	 * @see net.jforum.dao.DataAccessDriver#newUserSecurityDAO()
	 */
	/*public net.jforum.dao.security.UserSecurityDAO newUserSecurityDAO() 
	{
		return userSecurityDao;
	}*/

	/** 
	 * @see org.etudes.jforum.dao.DataAccessDriver#newUserSecurityDAO()
	 */
	public org.etudes.jforum.dao.PrivateMessageDAO newPrivateMessageDAO() 
	{
		return privateMessageDao;
	}
	
	/** 
	 * @see org.etudes.jforum.dao.DataAccessDriver#newUserSessionDAO()
	 */
	public org.etudes.jforum.dao.UserSessionDAO newUserSessionDAO()
	{
		return userSessionDao;
	}
	
	/** 
	 * @see org.etudes.jforum.dao.DataAccessDriver#newConfigDAO()
	 */
	public org.etudes.jforum.dao.ConfigDAO newConfigDAO()
	{
		return configDao;
	}
	
	/** 
	 * @see org.etudes.jforum.dao.DataAccessDriver#newKarmaDAO()
	 */
	public org.etudes.jforum.dao.KarmaDAO newKarmaDAO()
	{
		return karmaDao;
	}
	
	/** 
	 * @see org.etudes.jforum.dao.DataAccessDriver#newBookmarkDAO()
	 */
	public org.etudes.jforum.dao.BookmarkDAO newBookmarkDAO()
	{
		return bookmarkDao;
	}
	
	/** 
	 * @see org.etudes.jforum.dao.DataAccessDriver#newAttachmentDAO()
	 */
	public org.etudes.jforum.dao.AttachmentDAO newAttachmentDAO()
	{
		return attachmentDao;
	}
	
	/** 
	 * @see org.etudes.jforum.dao.DataAccessDriver#newModerationDAO()
	 */
	public org.etudes.jforum.dao.ModerationDAO newModerationDAO()
	{
		return moderationDao;
	}
	
	/**
	 * @see org.etudes.jforum.dao.DataAccessDriver#newScheduledSearchIndexerDAO()
	 */
	public org.etudes.jforum.dao.ScheduledSearchIndexerDAO newScheduledSearchIndexerDAO()
	{
		return ssiDao;
	}

	public org.etudes.jforum.dao.BannerDAO newBannerDAO()
	{
		return bannerDao;
	}
	
	//Mallika's new code beg
	public org.etudes.jforum.dao.generic.CourseCategoryDAO newCourseCategoryDAO()
	{
		return ccDao;
	}	
	public org.etudes.jforum.dao.generic.CourseGroupDAO newCourseGroupDAO()
	{
		return cgDao;
	}	
	public org.etudes.jforum.dao.generic.CourseInitDAO newCourseInitDAO()
	{
		return ciDao;
	}
	public org.etudes.jforum.dao.generic.CoursePrivateMessageDAO newCoursePrivateMessageDAO()
	{
		return cpmDao;
	}		
	public org.etudes.jforum.dao.CourseTimeDAO newCourseTimeDAO()
	{
		return ctimeDao;
	}	
	public org.etudes.jforum.dao.TopicMarkTimeDAO newTopicMarkTimeDAO()
	{
		return tmtimeDao;
	}		
	//Mallika's new code end

	@Override
	public CourseImportDAO newCourseImportDAO() {
		return courseImportDao;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GradeDAO newGradeDAO()
	{
		return gradeDao;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EvaluationDAO newEvaluationDAO()
	{
		return evaluationDAO;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public SpecialAccessDAO newSpecialAccessDAO()
	{
		return specialAccessDAO;
	}
}
