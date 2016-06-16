/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/repository/ForumRepository.java $ 
 * $Id: ForumRepository.java 71138 2010-11-02 18:44:42Z murthy@etudes.org $ 
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
package org.etudes.jforum.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.cache.CacheEngine;
import org.etudes.jforum.cache.Cacheable;
import org.etudes.jforum.dao.CategoryDAO;
import org.etudes.jforum.dao.ConfigDAO;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.ForumDAO;
import org.etudes.jforum.dao.SpecialAccessDAO;
import org.etudes.jforum.dao.UserDAO;
import org.etudes.jforum.dao.generic.CourseGroup;
import org.etudes.jforum.dao.generic.CourseGroupDAO;
import org.etudes.jforum.dao.generic.CourseInitDAO;
import org.etudes.jforum.entities.Category;
import org.etudes.jforum.entities.Config;
import org.etudes.jforum.entities.CourseInitObj;
import org.etudes.jforum.entities.Forum;
import org.etudes.jforum.entities.LastPostInfo;
import org.etudes.jforum.entities.MostUsersEverOnline;
import org.etudes.jforum.entities.SiteInfo;
import org.etudes.jforum.entities.SpecialAccess;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.entities.UserSession;
import org.etudes.jforum.exceptions.CategoryNotFoundException;
import org.etudes.jforum.util.CategoryOrderComparator;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.cover.UserDirectoryService;


/**
 * Repository for the forums of the System.
 * This repository acts like a cache system, to avoid repetitive and unnecessary SQL queries
 * every time we need some info about the forums. 
 * To start the repository, call the method <code>start(ForumModel, CategoryModel)</code>
 * 
 * @author Rafael Steil
 * 8/4/05 - Mallika - New start method to support courseId
 * 8/9/05 - Mallika - Adding method to get all course categories
 * 8/10/05 - Mallika - Decided to make code changes within here for course, instead of new methods
 * 8/11/05 - Mallika - Adding code to add to coursecategory cache
 * 8/24/05 - Mallika - Adding code to create default groups for course
 * 8/29/05 - Mallika - Adding isCourseGroupsInitialized function that returns true if groups have been added
 * 9/7/05 - Mallika - Adding loadCourseGroupRoles method to set permissions for facilitator role
 * 9/13/05 - Mallika - Changing above method name and adding another one for participant roles
 * 9/27/05 - Mallika - Adding code to make attachments enabled by default
 * 10/14/05 - Mallika - Adding karma and bookmarks to facilitators
 * 10/21/05 - Mallika - Adding post sticky announcement deny permission to participants
 * 10/21/05 - Mallika - Adding permissions as per bug 135
 * 01/19/06 - Murthy - addForum() and deleteForum() updated for cache
 * 01/19/06 - Murthy - moved the instantiating of the instance variable to outside the block
 * 01/24/06 - Murthy -  Updated the call getTotalTopics(int forumId)to get total topics from database
 * 01/25/06 - Murthy - Updated reloadForum() method to update cateogy in cache
 * 02/02/06 - Murthy - added reLoadCategories() method to reload categories in cache after forums are loaded
 * 1/21/06 - Mallika - Adding method to get categories without perm check
 * 03/31/06 - Murthy - updated the default karma role for Participant and Facilitator 
 * 						to Deny - to set default to "no"
 * 08/08/06 - Murthy - updated loadFacGroupRoles and loadPartGroupRoles methods
 * 						to add permissions code related to task topics
 * 01/18/07 - Murthy - changed the cache design - categories and forums are added to cache when
 * 						the site is accessed
 */
public class ForumRepository implements Cacheable
{
	private static CacheEngine cache;
	public static final String FQN = "categories";
	private static final String RELATION = "relationForums";
	public static final String FQN_TOTAL_TOPICS = FQN + "/totalTopics";
	private static final String MOST_USERS_ONLINE = "mostUsersEverOnline";
	private static final String LAST_USER = "lastUser";
	private static final String TOTAL_USERS = "totalUsers";
	private static final String FQN_CC = "courseCategories";
	
	private static final ForumRepository instance = new ForumRepository();
	
	private static Log logger = LogFactory.getLog(ForumRepository.class);
	
	/**
	 * @see org.etudes.jforum.cache.Cacheable#setCacheEngine(org.etudes.jforum.cache.CacheEngine)
	 */
	public void setCacheEngine(CacheEngine engine)
	{
		cache = engine;
	}
	
	/**
	 * Gets a category by its id.
	 * A call to @link #getCategory(int, int) is made, using the
	 * return of <code>SessionFacade.getUserSession().getUserId()</code>
	 * as argument for the "userId" parameter.
	 * 
	 * @param categoryId The id of the category to check
	 * @return <code>null</code> if the category is either not
	 * found or access is denied.
	 * @see #getCategory(int, int)
	 */
	/*public static Category getCategory(int categoryId)
	{
		return getCategory(SessionFacade.getUserSession().getUserId(), categoryId);
	}*/

	/**
	 * get manage category
	 * @param categoryId
	 * @return
	 */
	public static Category getManageCategory(int categoryId)
	{
		return getManageCategory(SessionFacade.getUserSession().getUserId(), categoryId);
	}

	/**
	 * Gets a category by its id.
	 *  
	 * @param userId The user id who is requesting the category
	 * @param categoryId The id of the category to get
	 * @return <code>null</code> if the category is either not
	 * found or access is denied.
	 * @see #getCategory(int)
	 */
	public static Category getCategory(int userId, int categoryId)
	{
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			checkAndAddCourseCategoriesForumsToCache();
			String courseId = ToolManager.getCurrentPlacement().getContext();
			return (Category)cache.get(FQN_CC +"/"+ courseId, Integer.toString(categoryId));
		}
		
		return null;
	}

	/**
	 * check and add the course categories and forums to cache
	 */
	private static void checkAndAddCourseCategoriesForumsToCache() {
		
		String courseId = ToolManager.getCurrentPlacement().getContext();
		boolean imported = false;
		try {
			//check for import from other sites
			List list = DataAccessDriver.getInstance().newCourseImportDAO().selectByCourseId(courseId);
			if (list.size() > 0){
				SiteInfo siteInfo = (SiteInfo)list.get(0);
				if (siteInfo.isImportedFromSite()){
					//reload cache and update the database for imported
					imported = true;
					DataAccessDriver.getInstance().newCourseImportDAO().update(courseId, false);
				}
			}
		} catch (Exception e1) {
			if (logger.isErrorEnabled()) logger.error("checkAndAddCourseCategoriesForumsToCache() : Error while fetching Course Import information : "+ e1.toString(), new Throwable(e1));
		}
		
		Map courseCats = (Map)cache.get(FQN_CC +"/"+ courseId);
		if (courseCats != null && !imported){
			if(logger.isDebugEnabled()) logger.debug("checkAndAddCourseCategoriesForumsToCache() : course categories are in cache..... : ");
		}else{
			//add course categories and forums to cache
			try {
				loadCourseCategory();
				loadCourseForums();
			} catch (Exception e) {
				if (logger.isErrorEnabled()) logger.error("checkAndAddCourseCategoriesForumsToCache() : Error while loading course category : "+ e.toString(), new Throwable(e));
			}
		}
	}
	
	/**
	 * get manage category
	 * @param userId
	 * @param categoryId
	 * @return
	 */
	public static Category getManageCategory(int userId, int categoryId)
	{
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			checkAndAddCourseCategoriesForumsToCache();
			String courseId = ToolManager.getCurrentPlacement().getContext();
			return (Category)cache.get(FQN_CC +"/"+ courseId, Integer.toString(categoryId));
		}
		else
		{
			try
			{
				Category c = DataAccessDriver.getInstance().newCategoryDAO().selectById(categoryId);
				List<Forum> forums = DataAccessDriver.getInstance().newForumDAO().selectByCategoryId(categoryId);
				
				for (Forum forum : forums){
					c.addForum(forum);
				}
				
				return c;
				
			}
			catch (Exception e)
			{
				if (logger.isErrorEnabled()) logger.error("getManageCategory() : Error while fetching category forums.", new Throwable(e));
			}
		}
		
		return null;
	}


	public static Category getCategory(int categoryId)
	{
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			checkAndAddCourseCategoriesForumsToCache();
			String courseId = ToolManager.getCurrentPlacement().getContext();
			return (Category)cache.get(FQN_CC +"/"+ courseId, Integer.toString(categoryId));
		}
		else
		{
			try
			{	
				Category c = DataAccessDriver.getInstance().newCategoryDAO().selectById(categoryId);
								
				List<Forum> forums = DataAccessDriver.getInstance().newForumDAO().selectByCategoryId(categoryId);
				
				for (Forum forum : forums){
					
					List<SpecialAccess> forumSpecialAccessList = DataAccessDriver.getInstance().newSpecialAccessDAO().selectByForumId(forum.getId());
					forum.setSpecialAccessList(forumSpecialAccessList);
					
					c.addForum(forum);
				}
				
				return c;
			}
			catch (Exception e)
			{
				if (logger.isErrorEnabled()) logger.error("getCategory(int categoryId) : Error while getting course categories or forums : "+ e.toString(), new Throwable(e));
			}
		}
		
		return null;
	}
	
	/**
	 * Check if forum is accessible.
	 * 
	 * @param 	forum	forum
	 * @return 	true	if access to the forum is allowed. 
	 */
	public static boolean isForumAccessibleToUser(Forum forum)
	{
		if (forum == null)
			throw new IllegalArgumentException();
		
		if (JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser()){
			return true;
		}
		
		if (JForumUserUtil.isJForumParticipant(UserDirectoryService.getCurrentUser().getId())){
			try {
				Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
								
				//get site groups - gets org.sakaiproject.site.api.Group(org.sakaiproject.site.impl.BaseGroup);
				Collection sakaiSiteGroups = site.getGroupsWithMember(UserDirectoryService.getCurrentUser().getId());
				
				GregorianCalendar gc = new GregorianCalendar();
				Date nowDate = gc.getTime();
				
				UserSession currentUser = SessionFacade.getUserSession();
				
				if (forum.getAccessType() == Forum.ACCESS_GROUPS)
				{
					for (Iterator usrGrpIter = sakaiSiteGroups.iterator(); usrGrpIter.hasNext(); ) 
					{
						org.sakaiproject.site.api.Group grp = (org.sakaiproject.site.api.Group)usrGrpIter.next();
						if (forum.getGroups() != null)
						{
							if (forum.getGroups().contains(grp.getId()))
							{
								// check for forum group special access
								List<SpecialAccess> specialAccessList = forum.getSpecialAccessList();
							
								boolean specialAccessUser = false, specialAccessUserAccess = false;
								for (SpecialAccess sa : specialAccessList)
								{
									if ((sa.getUserIds().contains(currentUser.getUserId())))
									{
										specialAccessUser = true;
										
										if (sa.getStartDate() == null)
										{
											specialAccessUserAccess = true;
										}
										else if (nowDate.getTime() > sa.getStartDate().getTime())
										{
											specialAccessUserAccess = true;
										}
										
										break;
									}
									
								}
							
								if (specialAccessUser)
								{
									return specialAccessUserAccess;
								}
								else
								{
									if (forum.getStartDate() != null)
									{
										if (nowDate.getTime() < forum.getStartDate().getTime())
										{
											return false;
										}
									}
								}
								
								return true;
							}
						} 
						else 
						{
							return false;
						}
					}
				}
				else if (forum.getAccessType() == Forum.ACCESS_DENY)
				{
					return false;					
				}
				else 
				{
					
					// check forum special access
					List<SpecialAccess> forumSpecialAccessList = forum.getSpecialAccessList();
					boolean specialAccessUser = false, specialAccessUserAccess = false;
										
					if (forumSpecialAccessList != null)
					{
						for (SpecialAccess specialAccess : forumSpecialAccessList)
						{
							if (specialAccess.getUserIds().contains(currentUser.getUserId()))
							{
								specialAccessUser = true;
								if ((specialAccess.getStartDate() != null) && ( nowDate.getTime() < specialAccess.getStartDate().getTime()))
								{
									specialAccessUserAccess = false;
								}
								else
								{
									specialAccessUserAccess = true;
								}
								break;
							}
						}
					}
					
					if (specialAccessUser)
					{
						return specialAccessUserAccess;
					}
					else
					{
						if (forum.getStartDate() != null)
						{
							
							if (nowDate.getTime() < forum.getStartDate().getTime())
							{
								return false;
							}
						}
					}
					
					return true;
				}
			} catch (IdUnusedException e) {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Check if category is accessible.
	 * 
	 * @param category	category
	 * @return 	true	if access to the category is allowed. 
	 */
	public static boolean isCategoryAccessible(Category category, int userId)
	{
		if (category == null)
			throw new IllegalArgumentException();
		
		if (JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser()){
			return true;
		}
		
		GregorianCalendar gc = new GregorianCalendar();
		Date nowDate = gc.getTime();
		
		// category access dates check 
		if (category.getStartDate() != null)
		{
			if (nowDate.getTime() < category.getStartDate().getTime())
			{
				// check forum(s) special access for the user
				if (category.getForums().size() > 0)
				{
					return true;
				}
				
				return false;
			}
		}
		
		return true;
	}
		
	/**
	 * Gets all categories from the cache. 
	 * 
	 * @return <code>List</code> with the categories. Each entry is a <code>Category</code> object.
	 */
	public static List getAllCourseCategoriesForUser(int userId)
	{
		if (logger.isDebugEnabled()) logger.debug("getAllCourseCategories() Entering....");

		List l = new ArrayList();
				
		String courseId = ToolManager.getCurrentPlacement().getContext();
		
			
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			checkAndAddCourseCategoriesForumsToCache();
			Map courseCats = (Map)cache.get(FQN_CC +"/"+ courseId);
			
			if (courseCats != null){
				Iterator iter = courseCats.keySet().iterator();
				
				while (iter.hasNext()) {
					String catId = (String) iter.next();
					Category c = (Category)cache.get(FQN_CC +"/"+ courseId, catId);
					
					Category userCategory = new Category(c);
					
					l.add(c);
				}
				Collections.sort(l, new CategoryOrderComparator());
			}
		}
		else
		{
			try
			{
				Map courseCats = getCourseCategory();
				
				if (courseCats != null){
					Iterator iter = courseCats.keySet().iterator();
					
					while (iter.hasNext()) {
						Integer catId = (Integer) iter.next();
						Category c = (Category) courseCats.get(catId);
						Category userCategory = new Category(c);
						l.add(c);
					}
					Collections.sort(l, new CategoryOrderComparator());
				}
			}
			catch (Exception e)
			{
				if (logger.isErrorEnabled()) logger.error("getAllCourseCategories() : Error while fetching the categories" +
						" for site : "+ courseId, e);
			}
		}
		
		
		if (logger.isDebugEnabled()) logger.debug("getAllCourseCategories() : Exiting");
		return l;
	}

	/**
	 * get all manage categories
	 * @param userId
	 * @return
	 */
	public static List getAllManageCategories(int userId)
	{
		if (logger.isDebugEnabled()) logger.debug("ForumRepository-getAllManageCategories: Entering");

		List l = new ArrayList();
		String courseId = ToolManager.getCurrentPlacement().getContext();
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			checkAndAddCourseCategoriesForumsToCache();
			Map courseCats = (Map)cache.get(FQN_CC +"/"+ courseId);
			if (courseCats != null){
				Iterator iter = courseCats.keySet().iterator();
				while (iter.hasNext()) {
					String catId = (String) iter.next();
					Category c = (Category)cache.get(FQN_CC +"/"+ courseId, catId);
					l.add(c);
				}
				Collections.sort(l, new CategoryOrderComparator());
			}
		}
		else
		{

			try
			{
				Map courseCats = getCourseCategory();
				if (courseCats != null)
				{
					Iterator iter = courseCats.keySet().iterator();

					while (iter.hasNext())
					{
						Integer catId = (Integer) iter.next();
						Category c = (Category) courseCats.get(catId);
						Category userCategory = new Category(c);
						
						// get forums
						List<Forum> forums = DataAccessDriver.getInstance().newForumDAO().selectByCategoryId(c.getId());
						
						for (Forum forum : forums){
							c.addForum(forum);
						}
						l.add(c);
					}
					Collections.sort(l, new CategoryOrderComparator());
				}
			}
			catch (Exception e)
			{
				if (logger.isErrorEnabled()) logger.error("getAllManageCategories(int userId) : Error while getting course categories or forums : "+ e.toString(), new Throwable(e));
			}
		}
		if (logger.isDebugEnabled()) logger.debug("ForumRepository-getAllManageCategories: Exiting");
		return l;
	}
	
	/**
	 * Get use all course categories.
	 * A call to @link #getAllCategories(int) is made, passing
	 * the return of <code>SessionFacade.getUserSession().getUserId()</code> 
	 * as the value for the "userId" argument.
	 * 
	 * @return <code>List</code> with the categories. Each entry is a <code>Category</code> object.
	 * @see #getAllCategories(int)
	 */
	public static List getUserAllCourseCategories()
	{
		return getAllCourseCategoriesForUser(SessionFacade.getUserSession().getUserId());
	}
	
	/**
	 * get all manage categories
	 * @return
	 */
	public static List getAllManageCategories()
	{
		return getAllManageCategories(SessionFacade.getUserSession().getUserId());
	}

	/**
	 * find course category by order
	 * @param order
	 * @return
	 */
	private static Category findCourseCategoryByOrder(int order)
	{
		String courseId = ToolManager.getCurrentPlacement().getContext();
		Map courseCats = (Map)cache.get(FQN_CC +"/"+ courseId);
		if (courseCats != null){
			Iterator iter = courseCats.keySet().iterator();
			
			while (iter.hasNext()) {
				String catId = (String) iter.next();
				Category c = (Category)cache.get(FQN_CC +"/"+ courseId, catId);
				
				if (c.getOrder() == order)
					return c;
			}
		}
		return null;
	}

	/**
	 * Updates course category.
	 * This method only updated the "name" and "order" fields. 
	 *  
	 * @param c The category to update. The method will search for a category
	 * with the same id and update its data.
	 */
	public synchronized static void reloadCourseCategory(Category c)
	{
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			String courseId = ToolManager.getCurrentPlacement().getContext();
			Category current = (Category) cache.get(FQN_CC +"/"+ courseId, Integer.toString(c.getId()));
			
			Category currentAtOrder = findCourseCategoryByOrder(c.getOrder());
			if (currentAtOrder != null){
				cache.add(FQN_CC +"/"+ courseId, Integer.toString(c.getId()), c);	
				if (currentAtOrder != null && c.getId() != currentAtOrder.getId()) {
					//change the order of the other category
					currentAtOrder.setOrder(current.getOrder());
					cache.add(FQN_CC +"/"+ courseId, Integer.toString(currentAtOrder.getId()), currentAtOrder);
				}
			}
		}
	}
	
	/**
	 * Refreshes a category entry in the cache.
	 * 
	 * @param c The category to refresh
	 */
	public static void refreshCategory(Category c)
	{
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			String courseId = ToolManager.getCurrentPlacement().getContext();
			cache.add(FQN_CC +"/"+ courseId, Integer.toString(c.getId()), c);
		}
	}
	
	/**
	 * Remove a course category from the cache
	 * @param c The category to remove. The instance should have the 
	 * category id at least
	 */
	public synchronized static void removeCourseCategory(Category c)
	{
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			String courseId = ToolManager.getCurrentPlacement().getContext();
			cache.remove(FQN_CC +"/"+ courseId, Integer.toString(c.getId()));
		}
	}
	
	/**
	 * Adds a new category to the cache.
	 * @param c The category instance to insert in the cache.
	 */
	public synchronized static void addCourseCategoryToCache(Category c)
	{
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			String courseId = ToolManager.getCurrentPlacement().getContext();
			cache.add(FQN_CC +"/"+ courseId, Integer.toString(c.getId()), c);
		}
	}
	
	/**
	 * Gets a specific forum from the cache.	 
	 * 
	 * @param forumId The forum's ID to get
	 * @return <code>net.jforum.Forum</code> object instance or <code>null</code>
	 * if the forum was not found or is not accessible to the user.
	 */
	public static Forum getForum(int forumId)
	{
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			checkAndAddCourseCategoriesForumsToCache();
			String courseId = ToolManager.getCurrentPlacement().getContext();
			String categoryId = getCategoryIdFromCacheForForumId(forumId);
			
			if (categoryId != null) {
				Category c = (Category)cache.get(FQN_CC +"/"+ courseId, categoryId);
				return c.getForum(forumId);
			}
		}
		else
		{
			try
			{
				Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(forumId);
				List<SpecialAccess> forumSpecialAccessList = DataAccessDriver.getInstance().newSpecialAccessDAO().selectByForumId(forum.getId());
				forum.setSpecialAccessList(forumSpecialAccessList);
				
				return forum;
			}
			catch (Exception e)
			{
				if (logger.isErrorEnabled()) logger.error("getForum(int forumId) : Error while getting Forum : "+ e.toString(), new Throwable(e));
			}
		}
		
		return null;
	}
	
	/**
	 * Gets a specific forum from the cache.
	 * 
	 * @param forumId			The forum's ID to get
	 * @param getSpecialAccess	true - with special access
	 * 							false - with out special access
	 * @return					<code>net.jforum.Forum</code> object instance or <code>null</code>
	 */
	public static Forum getForum(int forumId, boolean getSpecialAccess)
	{
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			checkAndAddCourseCategoriesForumsToCache();
			String courseId = ToolManager.getCurrentPlacement().getContext();
			String categoryId = getCategoryIdFromCacheForForumId(forumId);
			
			if (categoryId != null) {
				Category c = (Category)cache.get(FQN_CC +"/"+ courseId, categoryId);
				return c.getForum(forumId);
			}
		}
		else
		{
			try
			{
				Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(forumId);
				
				if (getSpecialAccess)
				{
					List<SpecialAccess> forumSpecialAccessList = DataAccessDriver.getInstance().newSpecialAccessDAO().selectByForumId(forum.getId());
					forum.setSpecialAccessList(forumSpecialAccessList);
				}
				
				return forum;
			}
			catch (Exception e)
			{
				if (logger.isErrorEnabled()) logger.error("getForum(int forumId) : Error while getting Forum : "+ e.toString(), new Throwable(e));
			}
		}
		
		return null;
	}

	/**
	 * get category from cache for forumId
	 * @param forumId
	 * @return
	 */
	private static String getCategoryIdFromCacheForForumId(int forumId) {
		String categoryId = null;
		Map relationForums = (Map)cache.get(FQN, RELATION);
		if (relationForums != null){
			categoryId = (String)((Map)cache.get(FQN, RELATION)).get(Integer.toString(forumId));
			if (categoryId == null || categoryId.trim().length() == 0){
				try {
					loadCourseForums();
					categoryId = (String)((Map)cache.get(FQN, RELATION)).get(Integer.toString(forumId));
				} catch (Exception e) {
					if (logger.isErrorEnabled()) logger.error("getCategoryFromCacheForForumId(int forumId) : Error while loading course Forums : "+ e.toString());
				}
			}
		}
		else{
			try {
				loadCourseForums();
				categoryId = (String)((Map)cache.get(FQN, RELATION)).get(Integer.toString(forumId));
			} catch (Exception e) {
				if (logger.isErrorEnabled()) logger.error("getCategoryFromCacheForForumId(int forumId) : Error while loading course Forums : "+ e.toString());
			}
		}
		return categoryId;
	}
	
	public static boolean isForumAccessible(int forumId)
	{
		return isForumAccessible(SessionFacade.getUserSession().getUserId(), forumId);
	}
	
	public static boolean isForumAccessible(int userId, int forumId)
	{
		int categoryId = -1;
		
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			categoryId = Integer.parseInt((String)((Map)cache.get(FQN, RELATION)).get(Integer.toString(forumId)));
		}
		else
		{
			try
			{
				Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(forumId);
				categoryId = forum.getCategoryId();
			}
			catch (Exception e)
			{
				if (logger.isErrorEnabled()) logger.error("isForumAccessible(int userId, int forumId) : Error while fetching forum : "+ e.toString(), new Throwable(e));
			}
		}
		return isForumAccessible(userId, categoryId, forumId);
	}
	
	public static boolean isForumAccessible(int userId, int categoryId, int forumId)
	{
		Category category = null;
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			String courseId = ToolManager.getCurrentPlacement().getContext();
			category = (Category)cache.get(FQN_CC +"/"+ courseId, Integer.toString(categoryId));
		}
		else
		{
			try
			{
				category = DataAccessDriver.getInstance().newCategoryDAO().selectById(categoryId);
			}
			catch (Exception e)
			{
				if (logger.isErrorEnabled()) logger.error("isForumAccessible(int userId, int categoryId, int forumId) : Error while fetching category : "+ e.toString(), new Throwable(e));
			}
		}
		return category.getForum(userId, forumId) != null;
	}
	
	/**
	 * Adds a new forum to the cache repository.	 
	 * 
	 * @param forum The forum to add
	 */
	public synchronized static void addForum(Forum forum)
	{
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			//logger.info("ForumRepository-addForum");
			String categoryId = Integer.toString(forum.getCategoryId());
				
			String courseId = ToolManager.getCurrentPlacement().getContext();
			Category c = (Category)cache.get(FQN_CC +"/"+ courseId, categoryId);
			c.addForum(forum);
			
			cache.add(FQN_CC +"/"+ courseId, Integer.toString(c.getId()), c);
			
			Map m = (Map)cache.get(FQN, RELATION);
			if (m == null) {
				m = new HashMap();
			}
			m.put(Integer.toString(forum.getId()), categoryId);
			cache.add(FQN, RELATION, m);
		}
	}
	
	/**
	 * Removes a forum from the cache.
	 * 
	 * @param forum The forum instance to remove.
	 */
	public synchronized static void removeForum(Forum forum)
	{
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			String id = Integer.toString(forum.getId());
			Map m = (Map)cache.get(FQN, RELATION);
			m.remove(id);
			cache.add(FQN, RELATION, m);
	
			id = Integer.toString(forum.getCategoryId());
			
			String courseId = ToolManager.getCurrentPlacement().getContext();
			Category c = (Category)cache.get(FQN_CC +"/"+ courseId, id);
			c.removeForum(forum.getId());
			cache.add(FQN_CC +"/"+ courseId, Integer.toString(c.getId()), c);
		}
	}
	
	/**
	 * Reloads a forum.
	 * The forum should already be in the cache and <b>SHOULD NOT</b>
	 * have its order changed. If the forum's order was changed, 
	 * then you <b>MUST CALL</b> @link Category#changeForumOrder(Forum) <b>BEFORE</b>
	 * calling this method.
	 * 
	 * @param forum The forum to reload its information
	 * @throws Exception
	 */
	public static synchronized void reloadForum(int forumId) throws Exception
	{
		Forum f = DataAccessDriver.getInstance().newForumDAO().selectById(forumId);
		
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			if (((Map)cache.get(FQN, RELATION)).containsKey(Integer.toString(forumId))) {
				String id = Integer.toString(f.getCategoryId());
				//Category c = (Category)cache.get(FQN, id);
				String courseId = ToolManager.getCurrentPlacement().getContext();
				Category c = (Category)cache.get(FQN_CC +"/"+ courseId, id);
				
				f.setLastPostInfo(null);
				f.setLastPostInfo(ForumRepository.getLastPostInfo(f));
				c.reloadForum(f);
				
				if (c != null)
					cache.add(FQN_CC +"/"+ courseId, Integer.toString(c.getId()), c);
			}
		}
		//getTotalMessages(true);
	}
	
	/**
	 * Gets information about the last message posted in some forum.
	 * @param forum The forum to retrieve information
	 * @return 
	 */
	public static LastPostInfo getLastPostInfo(Forum forum) throws Exception
	{
		LastPostInfo lpi = forum.getLastPostInfo();
		
		if (lpi == null || !forum.getLastPostInfo().hasInfo()) {
			lpi = DataAccessDriver.getInstance().newForumDAO().getLastPostInfo(forum.getId());
			forum.setLastPostInfo(lpi);
		}
		
		return lpi;
	}
	
	/**
	 * Gets information about the last message posted in some forum.
	 * 
	 * @param forumId The forum's id to retrieve information
	 * @return
	 * @throws Exception
	 */
	public static LastPostInfo getLastPostInfo(int forumId) throws Exception
	{
		return getLastPostInfo(getForum(forumId, false));
	}
	
	public static User lastRegisteredUser()
	{
		return (User)cache.get(FQN, LAST_USER);
	}
	
	public static void setLastRegisteredUser(User user)
	{
		cache.add(FQN, LAST_USER, user);
	}
	
	public static Integer totalUsers()
	{
		return (Integer)cache.get(FQN, TOTAL_USERS);
	}
	
	public static void incrementTotalUsers()
	{
		Integer i = (Integer)cache.get(FQN, TOTAL_USERS);
		cache.add(FQN,TOTAL_USERS, new Integer(i.intValue() + 1));
	}
	
	/**
	 * Gets the number of topics in some forum.
	 * 
	 * @param forumId The forum's id to retrieve the number of topics
	 * @param fromDb If <code>true</code>, a query to the database will be made 
	 * to get the number of topics. If <code>false</code>, the cached information
	 * will be returned
	 * @return The number of topics
	 * @throws Exception
	 * @see #getTotalTopics(int)
	 */
	public static int getTotalTopics(int forumId, boolean fromDb) throws Exception
	{
		Integer i = (Integer)cache.get(FQN_TOTAL_TOPICS, Integer.toString(forumId));
		int total = (i != null ? i.intValue() : 0);
		
		if (fromDb || total == -1) {
			total = DataAccessDriver.getInstance().newForumDAO().getTotalTopics(forumId);
			cache.add(FQN_TOTAL_TOPICS, Integer.toString(forumId), new Integer(total));
		}
		
		return total;
	}
	
	/**
	 * Gets the number of topics in some forum.
	 * @param forumId The forum's id to retrieve the number of topics
	 * @return The number of topics
	 * @throws Exception
	 * @see #getTotalTopics(int, boolean)
	 */
	public static int getTotalTopics(int forumId) throws Exception
	{
		return ForumRepository.getTotalTopics(forumId, true); 
	}
	
	/**
	 * Gets the number of messages in the entire board.
	 * @return 
	 * @throws Exception
	 * @see #getTotalMessages(boolean)
	 */
	public static int getTotalMessages() throws Exception
	{
		return getTotalMessages(false);
	}

	/**
	 * Gets the number of messags in the entire board.
	 * 
	 * @param fromDb If <code>true</code>, a query to the database will
	 * be made, to retrieve the desired information. If <code>false</code>, the
	 * data will be fetched from the cache.
	 * @return The number of messages posted in the board.
	 * @throws Exception
	 * @see #getTotalMessages()
	 */
	public static int getTotalMessages(boolean fromDb) throws Exception
	{
		int total = 0;
		
		return total;
	}
	
	/**
	 * Gets the number of most online users ever
	 * @return
	 */
	public static MostUsersEverOnline getMostUsersEverOnline()
	{
		return (MostUsersEverOnline)cache.get(FQN, MOST_USERS_ONLINE);
	}
	
	/**
	 * Update the value of most online users ever.
	 * 
	 * @param newValue The new value to store. Generally it
	 * will be a bigger one.
	 * @throws Exception
	 */
	public static void updateMostUsersEverOnline(MostUsersEverOnline m) throws Exception
	{
		ConfigDAO cm = DataAccessDriver.getInstance().newConfigDAO();
		Config config = cm.selectByName(ConfigKeys.MOST_USERS_EVER_ONLINE);
		if (config == null) {
			// Total
			config = new Config();
			config.setName(ConfigKeys.MOST_USERS_EVER_ONLINE);
			config.setValue(Integer.toString(m.getTotal()));
			
			cm.insert(config);
			
			// Date
			config.setName(ConfigKeys.MOST_USER_EVER_ONLINE_DATE);
			config.setValue(Long.toString(m.getTimeInMillis()));
			
			cm.insert(config);
		}
		else {
			// Total
			config.setValue(Integer.toString(m.getTotal()));
			cm.update(config);

			// Date
			config.setName(ConfigKeys.MOST_USER_EVER_ONLINE_DATE);
			config.setValue(Long.toString(m.getTimeInMillis()));
			cm.update(config);
		}
		
		cache.add(FQN, MOST_USERS_ONLINE, m);
	}
	
	private void loadUsersInfo() throws Exception
	{
		UserDAO udao = DataAccessDriver.getInstance().newUserDAO();
		cache.add(FQN, LAST_USER, udao.getLastUserInfo());
		cache.add(FQN, TOTAL_USERS, new Integer(udao.getTotalUsers()));
	}

	private void loadMostUsersEverOnline(ConfigDAO cm) throws Exception
	{
		Config config = cm.selectByName(ConfigKeys.MOST_USERS_EVER_ONLINE);
		MostUsersEverOnline mostUsersEverOnline = new MostUsersEverOnline();
		
		if (config != null) {
			mostUsersEverOnline.setTotal(Integer.parseInt(config.getValue()));
			
			// We're assuming that, if we have one key, the another one
			// will always exist
			config = cm.selectByName(ConfigKeys.MOST_USER_EVER_ONLINE_DATE);
			mostUsersEverOnline.setTimeInMillis(Long.parseLong(config.getValue()));
		}
		
		cache.add(FQN, MOST_USERS_ONLINE, mostUsersEverOnline);
	}
		
	/**
	 * check for course initialization
	 * @return
	 * @throws Exception
	 */
	public static boolean isCourseInitialized() throws Exception
	{
		CourseInitDAO cio = DataAccessDriver.getInstance().newCourseInitDAO();
		String courseId = ToolManager.getCurrentPlacement().getContext();
		return cio.selectByCourseId(courseId) != null;
	}
	
	/**
	 * load course info
	 * @throws Exception
	 */
	public static void loadCourseInit() throws Exception
	{
		CourseInitDAO cio = DataAccessDriver.getInstance().newCourseInitDAO();
		
		String courseId = ToolManager.getCurrentPlacement().getContext();
		
		CourseInitObj ciObj = new CourseInitObj();
		ciObj.setCourseId(courseId);
		ciObj.setInitStatus(1);
		cio.addNew(ciObj);
		
	}
	
	/**
	 * check for course group initialization
	 * @return
	 * @throws Exception
	 */
	public static boolean isCourseGroupsInitialized() throws Exception
	{
		CourseGroupDAO cgm = DataAccessDriver.getInstance().newCourseGroupDAO();
	    CourseGroup cg_part = cgm.selectByGroupName(ToolManager.getCurrentPlacement().getContext(),"Participant");
		CourseGroup cg_fac = cgm.selectByGroupName(ToolManager.getCurrentPlacement().getContext(),"Facilitator");
	    
		if ((cg_part == null)&&(cg_fac == null)) return false;
		else return true;
	}	
	
	public static void loadDefaultCategoriesAndForums() throws Exception
	{
		CategoryDAO cm = DataAccessDriver.getInstance().newCategoryDAO();
						
		Category c = new Category();
		c.setName("Discussions");
		//Would this be set to false?
		c.setModerated(false);
		int categoryId = cm.addNew(c);

		c.setId(categoryId);
		addCourseCategoryToCache(c);
	}
	
	/**
	 * Loads course category to cache
	 * @throws Exception 
	 */
	private synchronized static void loadCourseCategory() throws Exception 
	{
		if (logger.isDebugEnabled()) logger.debug(" : loadCourseCategory() Entering....");
		List categories = DataAccessDriver.getInstance().newCategoryDAO().selectAllByCourse();
		
		String courseId = ToolManager.getCurrentPlacement().getContext();
		String siteTitle = SiteService.getSite(ToolManager.getCurrentPlacement().getContext()).getTitle();
		if (logger.isDebugEnabled()) logger.debug(" : loading CourseCategories from DB to " +
				"cache for site : '"+ courseId +" Title : "+ siteTitle +"'");
		
		for (Iterator iter = categories.iterator(); iter.hasNext(); ) {
			Category c = (Category)iter.next();
			cache.add(FQN_CC +"/"+ courseId, Integer.toString(c.getId()), c);
		}
		if (logger.isDebugEnabled()) logger.debug(" : loadCourseCategory() Exiting....");
	}
	
	/**
	 * Loads course category to cache
	 * @throws Exception 
	 */
	private static Map getCourseCategory() throws Exception 
	{
		if (logger.isDebugEnabled()) logger.debug(" : getCourseCategory() Entering....");
		List<Category> categories = DataAccessDriver.getInstance().newCategoryDAO().selectAllByCourse();
		
		String courseId = ToolManager.getCurrentPlacement().getContext();
		String siteTitle = SiteService.getSite(ToolManager.getCurrentPlacement().getContext()).getTitle();
		if (logger.isDebugEnabled()) logger.debug(" : getCourseCategory from DB to " +
				"cache for site : '"+ courseId +" Title : "+ siteTitle +"'"+ " , Site Id : " +ToolManager.getCurrentPlacement().getContext());

		SpecialAccessDAO specialAccessDAO = DataAccessDriver.getInstance().newSpecialAccessDAO();
		
		//get special access for the site
		List<SpecialAccess> siteSpecialAccessList = specialAccessDAO.selectBySite();
		
		Map<Integer, List<SpecialAccess>> forumSpecialAccessMap = new HashMap<Integer, List<SpecialAccess>>();
		
		for (SpecialAccess specialAccess : siteSpecialAccessList)
		{
			if (specialAccess.getForumId() > 0)
			{
				if (forumSpecialAccessMap.get(specialAccess.getForumId()) == null)
				{
					List<SpecialAccess> forumSpecialAccessList = new ArrayList<SpecialAccess>();
					forumSpecialAccessList.add(specialAccess);
					forumSpecialAccessMap.put(specialAccess.getForumId(), forumSpecialAccessList);
				}
				else
				{
					List<SpecialAccess> forumSpecialAccessList = forumSpecialAccessMap.get(specialAccess.getForumId());
					forumSpecialAccessList.add(specialAccess);
					forumSpecialAccessMap.put(specialAccess.getForumId(), forumSpecialAccessList);
				}
			}
		}
		
		Map catMap = new HashMap();
		for (Category category : categories)
		{
			catMap.put(category.getId(), category);
			
		}
			
		// get forums
		List l = DataAccessDriver.getInstance().newForumDAO().selectAllByCourse();
		int lastId = 0;
		Category c = null;
		String catId = null;
		
		for (Iterator iter = l.iterator(); iter.hasNext(); ) {
			Forum f = (Forum)iter.next();

			List<SpecialAccess> forumSpecialAccessList = forumSpecialAccessMap.get(f.getId());
			
			if (forumSpecialAccessList == null)
			{
				forumSpecialAccessList = new ArrayList<SpecialAccess>();
			}
			
			f.setSpecialAccessList(forumSpecialAccessList);
			
			if (f.getCategoryId() != lastId) {
				
				lastId = f.getCategoryId();
				catId = Integer.toString(f.getCategoryId());

				c = (Category)catMap.get(f.getCategoryId());
			}
			
			if (c == null) {
				throw new CategoryNotFoundException("Category for forum #" + f.getId() + " not found");
			}
			
			String forumId = Integer.toString(f.getId());
			
			if (forumSpecialAccessMap.get(f.getId()) != null)
			{
				f.setSpecialAccessList(forumSpecialAccessMap.get(f.getId()));
			}
			c.addForum(f);
		}
		
		
		if (logger.isDebugEnabled()) logger.debug(" : getCourseCategory() Exiting....");
		
		return catMap;
	}
	
	/**
	 * Loads course forums to cache and adds to related categories
	 * @throws Exception 
	 */
	private synchronized static void loadCourseForums() throws Exception
	{
		
		if (logger.isDebugEnabled()) logger.debug(" : loadCourseForums() Entering....");
		
		List l = DataAccessDriver.getInstance().newForumDAO().selectAllByCourse();
		
		Map m = (Map)cache.get(FQN, RELATION);
		if (m == null) {
			m = new HashMap();
		}
		
		int lastId = 0;
		Category c = null;
		String catId = null;
		String courseId = ToolManager.getCurrentPlacement().getContext();
		String siteTitle = SiteService.getSite(ToolManager.getCurrentPlacement().getContext()).getTitle();
		if (logger.isDebugEnabled()) logger.debug(" : loading CourseForums from DB to " +
				"cache for site : '"+ courseId +" Title : "+ siteTitle +"'");
		
		for (Iterator iter = l.iterator(); iter.hasNext(); ) {
			Forum f = (Forum)iter.next();
			
			if (f.getCategoryId() != lastId) {
				if (c != null)
					cache.add(FQN_CC +"/"+ courseId, Integer.toString(c.getId()), c);
				
				lastId = f.getCategoryId();
				catId = Integer.toString(f.getCategoryId());
				c = (Category)cache.get(FQN_CC +"/"+ courseId, catId);
			}
			
			if (c == null) {
				throw new CategoryNotFoundException("Category for forum #" + f.getId() + " not found");
			}
			
			String forumId = Integer.toString(f.getId());
			c.addForum(f);
			m.put(forumId, catId);
			cache.add(FQN_TOTAL_TOPICS, forumId, new Integer(-1));
		}
		if (c != null)
			cache.add(FQN_CC +"/"+ courseId, Integer.toString(c.getId()), c);
		
		cache.add(FQN, RELATION, m);
		
		if (logger.isDebugEnabled()) logger.debug(" : loadCourseForums() Exiting....");
		
	}
	
	/**
	 * gets the number of course categories in cache
	 * @return
	 */
	public static int courseCategoriesSize()
	{
		Set m = (Set)cache.getChildrenNames(FQN_CC);
		return (m != null ? m.size() : 0);
	}
	
	/**
	 * remove all course categories from cache
	 * @throws Exception
	 */
	public synchronized static void clearCourseCategoriesFromCache()throws Exception
	{
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			cache.remove(FQN_CC);
		}
	}
	
	/**
	 * remove course category from cache
	 * @param courseId
	 * @throws Exception
	 */
	public synchronized static void removeCourseCategyFromCache(String courseId)throws Exception
	{
		if (SakaiSystemGlobals.getBoolValue(ConfigKeys.CATEGORY_CACHE_ENABLED))
		{
			Object obj = cache.get(FQN_CC +"/"+ courseId);
			
			if (obj != null){
				cache.remove(FQN_CC +"/"+ courseId);
				if (logger.isDebugEnabled()) logger.debug("The Course category removed from cache is : "+ FQN_CC +"/"+ courseId);
			}
		}
	}
	
	/**
	 * check the forum dates for a category
	 * @param catgoryId		category id
	 * @return				true	if category forums has a start or end date
	 * 						false	if category forums has no start or end date
	 * @throws Exception
	 */
	public static boolean isCategoryForumDates(int catgoryId)throws Exception
	{
		int forumDatesCount = DataAccessDriver.getInstance().newForumDAO().getForumDatesCount(catgoryId);
		
		return ((forumDatesCount > 0) ? true : false);
	}
	
	
	public static boolean checkForumSpecialAccess(int forumId)throws Exception
	{
		int forumSpecialAccessCount = DataAccessDriver.getInstance().newSpecialAccessDAO().selectForumSpecialAccessCount(forumId);
		
		return ((forumSpecialAccessCount > 0) ? true : false);
	}
	
	public static boolean isSpecialAccessUser(Forum forum)throws Exception
	{

		if (forum == null)
			throw new IllegalArgumentException();
		
		if (JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser()){
			return false;
		}
		
		if (JForumUserUtil.isJForumParticipant(UserDirectoryService.getCurrentUser().getId()))
		{
			try 
			{
				Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());

				Collection sakaiSiteGroups = site.getGroupsWithMember(UserDirectoryService.getCurrentUser().getId());
				
				GregorianCalendar gc = new GregorianCalendar();
				Date nowDate = gc.getTime();
				
				UserSession currentUser = SessionFacade.getUserSession();
				
				if (forum.getAccessType() == Forum.ACCESS_GROUPS)
				{
					for (Iterator usrGrpIter = sakaiSiteGroups.iterator(); usrGrpIter.hasNext(); ) 
					{
						org.sakaiproject.site.api.Group grp = (org.sakaiproject.site.api.Group)usrGrpIter.next();
						if (forum.getGroups() != null)
						{
							if (forum.getGroups().contains(grp.getId()))
							{
								// check for forum group special access
								List<SpecialAccess> specialAccessList = forum.getSpecialAccessList();
							
								boolean specialAccessUser = false, specialAccessUserAccess = false;
								for (SpecialAccess sa : specialAccessList)
								{
									if ((sa.getUserIds().contains(currentUser.getUserId())))
									{
										specialAccessUser = true;
										
										if (sa.getStartDate() == null)
										{
											specialAccessUserAccess = true;
										}
										else if (nowDate.getTime() > sa.getStartDate().getTime())
										{
											specialAccessUserAccess = true;
										}
										
										break;
									}
									
								}
							
								if (specialAccessUser)
								{
									return specialAccessUserAccess;
								}
								
								return false;
							}
						} 
						else 
						{
							return false;
						}
					}
				}
				else if (forum.getAccessType() == Forum.ACCESS_DENY)
				{
					return false;					
				}
				else 
				{
					
					// check forum special access
					List<SpecialAccess> forumSpecialAccessList = forum.getSpecialAccessList();
					boolean specialAccessUser = false, specialAccessUserAccess = false;
										
					if (forumSpecialAccessList != null)
					{
						for (SpecialAccess specialAccess : forumSpecialAccessList)
						{
							if (specialAccess.getUserIds().contains(currentUser.getUserId()))
							{
								specialAccessUser = true;
								if ((specialAccess.getStartDate() != null) && ( nowDate.getTime() < specialAccess.getStartDate().getTime()))
								{
									specialAccessUserAccess = false;
								}
								else
								{
									specialAccessUserAccess = true;
								}
								break;
							}
						}
					}
					
					if (specialAccessUser)
					{
						return specialAccessUserAccess;
					}
					else
					{ 
						return false;
					}
				}
			} 
			catch (IdUnusedException e) 
			{
				return false;
			}
		}
		return false;
	
	}
}
