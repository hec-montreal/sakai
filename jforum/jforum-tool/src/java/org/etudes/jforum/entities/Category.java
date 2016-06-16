/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/entities/Category.java $ 
 * $Id: Category.java 71044 2010-10-29 17:37:24Z murthy@etudes.org $ 
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
package org.etudes.jforum.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.exceptions.ForumOrderChangedException;
import org.etudes.jforum.util.ForumOrderComparator;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.cover.UserDirectoryService;

/**
 * Represents a category in the System.
 * Each category holds a reference to all its forums, which 
 * can be retrieved by calling either @link #getForums(), 
 * @link #getForum(int) and related methods. 
 * 
 * <br/>
 * 
 * This class also controls the access to its forums, so a call
 * to @link #getForums() will only return the forums accessible
 * to the user who make the call tho the method. 
 * 
 * @author Rafael Steil
 * 1/21/06 - Mallika - adding method to get forums without permission check
 * 1/23/06 - Mallika - getting current time and comparing to restrict forum
 */
public class Category  implements Serializable
{
	private int id;
	private int order;
	private boolean moderated;
	private String name;
	private boolean archived;
	private Map forumsIdMap = new HashMap();
	private Set forums = new TreeSet(new ForumOrderComparator());
	private boolean gradeCategory;
	private Date startDate;
	private Date endDate;
	private boolean lockCategory;
	private String startDateFormatted;
	private String endDateFormatted;
			
	public Category() {}
	
	public Category(int id) {
		this.id = id;
	}
	
	public Category(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	/**
	 * create category and add  user accessible forums
	 */
	public Category(Category c) {
		this.name = c.getName();
		this.id = c.getId();
		this.order = c.getOrder();
		this.moderated = c.isModerated();
		this.archived = c.isArchived();
		this.gradeCategory = c.isGradeCategory();
		this.startDate = c.getStartDate();
		this.endDate = c.getEndDate();
		this.lockCategory = c.isLockCategory();
		this.startDateFormatted = c.getStartDateFormatted();
		this.endDateFormatted = c.getEndDateFormatted();
		
		for (Iterator iter = c.getUserForums().iterator(); iter.hasNext(); ) {
			this.addForum(new Forum((Forum)iter.next()));
		}
	}
	
	public Category(Category c, boolean allForums) {
		this.name = c.getName();
		this.id = c.getId();
		this.order = c.getOrder();
		this.moderated = c.isModerated();
		this.archived = c.isArchived();
		this.gradeCategory = c.isGradeCategory();
		this.startDate = c.getStartDate();
		this.endDate = c.getEndDate();
		this.lockCategory = c.isLockCategory();
		this.startDateFormatted = c.getStartDateFormatted();
		this.endDateFormatted = c.getEndDateFormatted();
		
		if (allForums){
			for (Iterator iter = c.getManageForums().iterator(); iter.hasNext(); ) {
				this.addForum(new Forum((Forum)iter.next()));
			}
		}
	}
	
	
	public void setModerated(boolean status)
	{
		this.moderated = status;
	}
	
	public boolean isModerated() 
	{
		return this.moderated;
	}
	
	/**
	 * @return int
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @return String
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @return int
	 */
	public int getOrder() {
		return this.order;
	}

	/**
	 * Sets the id.
	 * @param id The id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the order.
	 * @param order The order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}
	
	/**
	 * Adds a forum to this category
	 * 
	 * @param forum
	 */
	public void addForum(Forum forum) {
		this.forumsIdMap.put(new Integer(forum.getId()), forum);
		this.forums.add(forum);
	}
	
	/**
	 * Reloads a forum.
	 * The forum should already be in the cache and <b>SHOULD NOT</b>
	 * have its order changed. If the forum's order was changed, 
	 * then you <b>MUST CALL</b> @link #changeForumOrder(Forum) <b>BEFORE</b>
	 * calling this method.
	 * 
	 * @param forum The forum to reload its information
	 * @throws ForumChangedException if the forum given as parameter
	 * has a modified display order
	 * @throws Exception
	 * @see #changeForumOrder(Forum)
	 */
	public void reloadForum(Forum forum) {
		Forum currentForum = this.getForum(forum.getId());
		
		if (forum.getOrder() != currentForum.getOrder()) {
			throw new ForumOrderChangedException("Forum #" + forum.getId() + " cannot be reloaded, since its "
					+ "display order was changed. You must call Category#changeForumOrder(Forum)"
					+ "first");
		}
		
		Set tmpSet = new TreeSet(new ForumOrderComparator());
		tmpSet.addAll(this.forums);
		tmpSet.remove(currentForum);
		tmpSet.add(forum);
		this.forumsIdMap.put(new Integer(forum.getId()), forum);
		
		this.forums = tmpSet;
	}
	
	/**
	 * Changes a forum's display order. 
	 * This method changes the position of the
	 * forum in the current display order of the
	 * forum instance passed as argument, if applicable.
	 * 
	 * @param forum The forum to change
	 */
	public void changeForumOrder(Forum forum)
	{
		Forum current = this.getForum(forum.getId());
		Forum currentAtOrder = this.findByOrder(forum.getOrder());
		
		Set tmpSet = new TreeSet(new ForumOrderComparator());
		tmpSet.addAll(this.forums);
		
		// Remove the forum in the current order
		// where the changed forum will need to be
		if (currentAtOrder != null) {
			tmpSet.remove(currentAtOrder);
		}
		
		tmpSet.add(forum);
		this.forumsIdMap.put(new Integer(forum.getId()), forum);
		
		// Remove the forum in the position occupied
		// by the changed forum before its modification,
		// so then we can add the another forum into 
		// its position
		if (currentAtOrder != null) {
			tmpSet.remove(current);
			currentAtOrder.setOrder(current.getOrder());
			tmpSet.add(currentAtOrder);

			this.forumsIdMap.put(new Integer(currentAtOrder.getId()), currentAtOrder);
		}
		
		this.forums = tmpSet;
	}
	
	private Forum findByOrder(int order)
	{
		for (Iterator iter = this.forums.iterator(); iter.hasNext(); ) {
			Forum f = (Forum)iter.next();
			if (f.getOrder() == order) {
				return f;
			}
		}
		
		return null;
	}
	
	/**
	 * Removes a forum from the list.
	 * @param forumId
	 */
	public void removeForum(int forumId) {
		this.forums.remove(this.getForum(forumId));
		this.forumsIdMap.remove(new Integer(forumId));
	}

	/**
	 * Gets a forum.
	 * 
	 * @param userId The user's id who is trying to see the forum
	 * @param forumId The id of the forum to get
	 * @return The <code>Forum</code> instance if found, or <code>null</code>
	 * otherwhise.
	 * @see #getForum(int)
	 */
	public Forum getForum(int userId, int forumId)
	{
		return (Forum)this.forumsIdMap.get(new Integer(forumId));
	}

	/**
	 * Gets a forum.
	 * 
	 * @param forumId The forum's id 
	 * @return The requested forum, if found, or <code>null</code> if
	 * the forum does not exists or access to it is denied.
	 * @see #getForum(int, int)
	 */
	public Forum getForum(int forumId)
	{
		return this.getForum(SessionFacade.getUserSession().getUserId(), forumId);
	}

	/**
	 * Get all forums from this category.
	 * 
	 * @return All forums, regardless it is accessible 
	 * to the user or not.
	 */
	public Collection getForums()
	{
		if (this.forums.size() == 0) {
			return this.forums;
		}

		return this.getForums(SessionFacade.getUserSession().getUserId());
	}
	
	/**
	 * Get  forums from this category for the user
	 * 
	 * @return 
	 */
	public Collection getUserForums()
	{
		if (this.forums.size() == 0) {
			return this.forums;
		}

		return this.getGroupAccessibleForums();
	}
	
	/**
	 * get user accessible forums
	 * @return list of user accessible forums
	 */
	private Collection getGroupAccessibleForums() {
		List forums = new ArrayList();
		//get user forums
		try {
			if (JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser())
			{
				forums.addAll(this.forums);
				return forums;
			}
			
			if (JForumUserUtil.isJForumParticipant(UserDirectoryService.getCurrentUser().getId()))
			{
				Calendar rightNow = Calendar.getInstance();
				int userId = SessionFacade.getUserSession().getUserId();
				
				Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());

				Collection sakaiSiteGroups = site.getGroupsWithMember(UserDirectoryService.getCurrentUser().getId());
				
				for (Iterator iter = this.forums.iterator(); iter.hasNext(); ) 
				{
					Forum f = (Forum)iter.next();
					
					if (f.getAccessType() == Forum.ACCESS_GROUPS)
					{
						if (f.getGroups() != null)
						{
							for (Iterator usrGrpIter = sakaiSiteGroups.iterator(); usrGrpIter.hasNext(); ) 
							{
								org.sakaiproject.site.api.Group grp = (org.sakaiproject.site.api.Group)usrGrpIter.next();
							
								if (f.getGroups().contains(grp.getId()))
								{
									// check for forum group special access
									List<SpecialAccess> specialAccessList = f.getSpecialAccessList();
								
									boolean specialAccessUser = false, specialAccessUserAccess = false;
									for (SpecialAccess sa : specialAccessList)
									{
										if ((sa.getUserIds().contains(new Integer(userId))))
										{
											specialAccessUser = true;
											
											if (sa.getStartDate() == null)
											{
												specialAccessUserAccess = true;
											}
											else if (rightNow.getTime().after(sa.getStartDate()))
											{
												specialAccessUserAccess = true;
											}
										
											break;
										}
									}
								
									if (specialAccessUser)
									{
										if (specialAccessUserAccess)
										{
											forums.add(f);
										}
									}
									else
									{
										forums.add(f);
									}
								}
							}							
						}
						else
						{
							
							// check for forum special access
						}
					}
					else if (f.getAccessType() == Forum.ACCESS_DENY)
					{
						
					}
					else 
					{
						// check for forum special access
						List<SpecialAccess> specialAccessList = f.getSpecialAccessList();
						
						boolean specialAccessUser = false, specialAccessUserAccess = false;
						for (SpecialAccess sa : specialAccessList)
						{
							if (sa.getUserIds().contains(new Integer(userId)))
							{
								specialAccessUser = true;
								
								if (sa.getStartDate() == null)
								{
									specialAccessUserAccess = true;
								}
								else if (rightNow.getTime().after(sa.getStartDate()))
								{
									specialAccessUserAccess = true;
								}
								break;
							}
						}
						
						if (specialAccessUser)
						{
							if (specialAccessUserAccess)
							{
								forums.add(f);
							}
						}
						else
						{
							forums.add(f);
						}
					}	
				}
			}
			
		} catch (IdUnusedException e) {
			e.printStackTrace();
		}
		return forums;
	}

	public Collection getManageForums()
	{
		if (this.forums.size() == 0) {
			return this.forums;
		}

		return this.getManageForums(SessionFacade.getUserSession().getUserId());
	}	

	/**
	 * Gets all forums from this category.
	 * 
	 * @return The forums available to the user who make the call
	 * @see #getForums() 
	 */
	public Collection getForums(int userId) 
	{
		if (JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser()){
			return this.forums;
		}

		List forums = new ArrayList();
		
		Calendar rightNow = Calendar.getInstance();

		for (Iterator iter = this.forums.iterator(); iter.hasNext(); ) 
		{
			Forum f = (Forum)iter.next();
			
			// check for forum special access
			List<SpecialAccess> specialAccessList = f.getSpecialAccessList();
			
			boolean specialAccessUser = false, specialAccessUserAccess = false;
			for (SpecialAccess sa : specialAccessList)
			{
				if (sa.getUserIds().contains(new Integer(userId)))
				{
					specialAccessUser = true;
					
					if (sa.getStartDate() == null)
					{
						specialAccessUserAccess = true;
					}
					else if (rightNow.getTime().after(sa.getStartDate()))
					{
						specialAccessUserAccess = true;
					}
					break;
				}
			}
			
			if (specialAccessUser)
			{
				if (specialAccessUserAccess)
				{
					forums.add(f);
				}
				continue;
			}
		
			// don't add category forum with later category start date
			if (this.getStartDate() != null && this.getStartDate().after(rightNow.getTime()))
			{
				continue;
			}
			
			//If start date is null or if its before the current date
			//keep the forum visible, else do not add the forum
			if (f.getStartDate() == null)
			{					
				forums.add(f);
			}
			else
			{	
			  if ((f.getStartDate().before(rightNow.getTime())))
			  {				  		
			      forums.add(f);
			  }
			}
		}
		
		return forums;
	}
	
	public Collection getManageForums(int userId) 
	{
		List forums = new ArrayList();

		
		for (Iterator iter = this.forums.iterator(); iter.hasNext(); ) {
			Forum f = (Forum)iter.next();
 		
			forums.add(f);
	
		}
		
		return forums;
	}

	/** 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() 
	{
		return this.id;
	}

	/** 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) 
	{
		return ((o instanceof Category) && (((Category)o).getId() == this.id));
	}
	
	/** 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[" + this.name + ", id=" + this.id + ", order=" + this.order + "]"; 
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	/**
	 * @return the gradeCategory
	 */
	public boolean isGradeCategory()
	{
		return gradeCategory;
	}

	/**
	 * @param gradeCategory the gradeCategory to set
	 */
	public void setGradeCategory(boolean gradeCategory)
	{
		this.gradeCategory = gradeCategory;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the lockCategory
	 */
	public boolean isLockCategory() {
		return lockCategory;
	}

	/**
	 * @param lockCategory the lockCategory to set
	 */
	public void setLockCategory(boolean lockCategory) {
		this.lockCategory = lockCategory;
	}

	/**
	 * @return the startDateFormatted
	 */
	public String getStartDateFormatted()
	{
		return startDateFormatted;
	}

	/**
	 * @param startDateFormatted the startDateFormatted to set
	 */
	public void setStartDateFormatted(String startDateFormatted)
	{
		this.startDateFormatted = startDateFormatted;
	}

	/**
	 * @return the endDateFormatted
	 */
	public String getEndDateFormatted()
	{
		return endDateFormatted;
	}

	/**
	 * @param endDateFormatted the endDateFormatted to set
	 */
	public void setEndDateFormatted(String endDateFormatted)
	{
		this.endDateFormatted = endDateFormatted;
	}
}
