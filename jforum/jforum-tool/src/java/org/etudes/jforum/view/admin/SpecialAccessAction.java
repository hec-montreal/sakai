/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/admin/SpecialAccessAction.java $ 
 * $Id: SpecialAccessAction.java 71144 2010-11-02 23:34:28Z murthy@etudes.org $ 
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
package org.etudes.jforum.view.admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.JForum;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.generic.UserOrderComparator;
import org.etudes.jforum.entities.Category;
import org.etudes.jforum.entities.Forum;
import org.etudes.jforum.entities.SpecialAccess;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.date.DateUtil;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.preferences.TemplateKeys;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.site.api.Group;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.cover.UserDirectoryService;

public class SpecialAccessAction extends AdminCommand
{

	private static Log logger = LogFactory.getLog(SpecialAccessAction.class);
	
	@Override
	public void list() throws Exception
	{
	}
	
	/**
	 * Show forum special access list
	 * @throws Exception
	 */
	public void showForumList() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		this.context.put("viewTitleManageForums", true);
		int forumId = this.request.getIntParameter("forum_id");
		
		Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(forumId);
		this.context.put("forum", forum);
		
		Category category = DataAccessDriver.getInstance().newCategoryDAO().selectById(forum.getCategoryId());
		this.context.put("category", category);
		
		List<SpecialAccess> forumSpecialAccessList = DataAccessDriver.getInstance().newSpecialAccessDAO().selectByForumId(forumId);
			
		this.context.put("forumspecialAccessList", forumSpecialAccessList);
		
		this.context.put("action", "delete");
		this.setTemplateName(TemplateKeys.SPECIAL_ACCESS_FORUM_LIST);
	}
	
	/**
	 * Insert forum special access
	 * @throws Exception
	 */
	public void insertForum() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) 
		{
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		int forumId = this.request.getIntParameter("forum_id");
		
		Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(forumId);
		this.context.put("forum", forum);
		
		getForumUsers(forum);
		
		Category category = DataAccessDriver.getInstance().newCategoryDAO().selectById(forum.getCategoryId());
		this.context.put("category", category);
		
		this.context.put("viewTitleManageForums", true);
		this.context.put("action", "insertForumSave");
		context.put("calendarDateTimeFormat", SakaiSystemGlobals.getValue(ConfigKeys.CALENDAR_DATE_TIME_FORMAT));
		
		this.setTemplateName(TemplateKeys.SPECIAL_ACCESS_FORUM_FORM);
	}

	/**
	 * Edit forum special access
	 * @throws Exception
	 */
	public void editForum() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) 
		{
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		int specialAccessId = this.request.getIntParameter("special_access_id");
		SpecialAccess specialAccess = DataAccessDriver.getInstance().newSpecialAccessDAO().selectById(specialAccessId);
		this.context.put("specialAccess", specialAccess);
				
		Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(specialAccess.getForumId());
		this.context.put("forum", forum);
		
		//getForumSpecialAccess(specialAccess, forum);
		getForumUsers(forum);
		
		this.context.put("viewTitleManageForums", true);
		this.context.put("action", "editForumSave");
		context.put("calendarDateTimeFormat", SakaiSystemGlobals.getValue(ConfigKeys.CALENDAR_DATE_TIME_FORMAT));
		
		this.setTemplateName(TemplateKeys.SPECIAL_ACCESS_FORUM_FORM);
	}
	
	/**
	 * Save new forum special access
	 * @throws Exception
	 */
	public void insertForumSave() throws Exception
	{
		
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		String forumSpecialAccess = this.request.getParameter("forum_special_access");
		String forumGroupSpecialAccess = this.request.getParameter("forum_group_special_access");
		
		int forumId = this.request.getIntParameter("forum_id");
		Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(forumId);
		
		if (forumSpecialAccess != null)
		{
			addForumSpecialAccess(forum);
		}
		else if (forumGroupSpecialAccess != null)
		{
			addForumGroupSpecialAccess(forum);
		}
		
		this.context.put("forum", forum);		
			
		this.showForumList();
	}
	
	/**
	 * Save existing forum special access
	 * @throws Exception
	 */
	public void editForumSave() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		String forumSpecialAccess = this.request.getParameter("forum_special_access");
		String forumGroupSpecialAccess = this.request.getParameter("forum_group_special_access");
		
		int forumId = this.request.getIntParameter("forum_id");
		Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(forumId);
		
		if (forumSpecialAccess != null)
		{
			editForumSpecialAccess(forum);
		}
		/*else if (forumGroupSpecialAccess != null)
		{
			editForumGroupSpecialAccess(forum);
		}*/
			
		this.showForumList();
	}
	
	/**
	 * Delete special access
	 * @throws Exception
	 */
	public void delete() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		String ids[] = this.request.getParameterValues("special_access_id");
		
		if (ids != null) 
		{						
			for (int i = 0; i < ids.length; i++)
			{
				int id = Integer.parseInt(ids[i]);
				
				DataAccessDriver.getInstance().newSpecialAccessDAO().delete(id);
			}
		}
		
		String mode = this.request.getParameter("mode");
		
		if (mode.equals("forum_sa"))
		{
			this.showForumList();
		}
	}
	
	/**
	 * Add special access to forum user
	 * @throws Exception
	 */
	public void addForumUser() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}

		int forumId = this.request.getIntParameter("forum_id");
		int userId = this.request.getIntParameter("user_id");
		
		Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(forumId);
		this.context.put("forum", forum);
		
		Category category = DataAccessDriver.getInstance().newCategoryDAO().selectById(forum.getCategoryId());
		this.context.put("category", category);
		
		User user = DataAccessDriver.getInstance().newUserDAO().selectById(userId);
		this.context.put("user", user);
		
		// get forum user groups
		getForumUserGroups(forum, user);
			
		context.put("calendarDateTimeFormat", SakaiSystemGlobals.getValue(ConfigKeys.CALENDAR_DATE_TIME_FORMAT));
		
		this.context.put("action", "addForumUserSave");
		
		this.setTemplateName(TemplateKeys.SPECIAL_ACCESS_FORUM_ADD_EDIT_USER);
	}
	
	/**
	 * Save the new special access to forum user
	 * @throws Exception
	 */
	public void addForumUserSave() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		int forumId = this.request.getIntParameter("forum_id");
		int userId = this.request.getIntParameter("user_id");
		
		Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(forumId); 
		
		SpecialAccess specialAccess = new SpecialAccess();
		specialAccess.setForumId(forumId);		
		
		this.context.put("forum", forum);
		
		Category category = DataAccessDriver.getInstance().newCategoryDAO().selectById(forum.getCategoryId());
		
		// forum groups user_group
		if ((forum.getGroups() != null) && (forum.getGroups().size() > 0))
		{
			
		}
		
		String startDateParam = this.request.getParameter("start_date");
		if (startDateParam != null && startDateParam.trim().length() > 0)
		{
			Date startDate;
			try
			{
				startDate = DateUtil.getDateFromString(startDateParam.trim());
				specialAccess.setStartDate(startDate);
				
				SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
				specialAccess.setStartDateFormatted(df.format(startDate));
				
				// check with forum and category dates
				if ((forum.getStartDate() == null) && (forum.getEndDate() == null))
				{
					if ((category.getStartDate() == null) || (!startDate.equals(category.getStartDate())))
					{
						specialAccess.setOverrideStartDate(true);					
					}
				}
				else if ((forum.getStartDate() == null) || (!startDate.equals(forum.getStartDate())))
				{
					specialAccess.setOverrideStartDate(true);					
				}
			} 
			catch (ParseException e)
			{
				this.context.put("errorMessage", I18n.getMessage("Forums.Forum.DateParseError"));
				this.addForumUser();
				return;
			}
		}
		else
		{
			specialAccess.setStartDate(null);
		}
		
		String endDateParam = this.request.getParameter("end_date");
		if (endDateParam != null && endDateParam.trim().length() > 0)
		{
			Date endDate;
			try
			{
				endDate = DateUtil.getDateFromString(endDateParam.trim());
				specialAccess.setEndDate(endDate);
				
				SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
				specialAccess.setEndDateFormatted(df.format(endDate));
				
				if ((forum.getStartDate() == null) && (forum.getEndDate() == null))
				{
					if ((category.getEndDate() == null) || (!endDate.equals(category.getEndDate())))
					{
						specialAccess.setOverrideStartDate(true);					
					}
				}
				else if ((forum.getEndDate() == null ) || (!endDate.equals(forum.getEndDate())))
				{
					specialAccess.setOverrideEndDate(true);
				}
				
				String lockOnDue = this.request.getParameter("lock_on_due");
				if ((specialAccess.getEndDate() != null) && (lockOnDue != null && "1".equals(lockOnDue)))
				{
					specialAccess.setLockOnEndDate(true);
				}
			} catch (ParseException e)
			{
				this.context.put("errorMessage", I18n.getMessage("Forums.Forum.DateParseError"));
				this.addForumUser();
				return;
			}
		}
		else
		{
			specialAccess.setEndDate(null);
			specialAccess.setLockOnEndDate(false);
		}
		List<Integer> specialAccessUser = new ArrayList<Integer>();
		specialAccessUser.add(new Integer(userId));
		
		specialAccess.setUserIds(specialAccessUser);
		
		if (((specialAccess.isOverrideStartDate()) || (specialAccess.isOverrideEndDate())) && (specialAccess.getUserIds().size() > 0))
		{
			int newSpecialAccessId  = DataAccessDriver.getInstance().newSpecialAccessDAO().addNew(specialAccess);
			specialAccess.setId(newSpecialAccessId);
		}
		
		JForum.setRedirect(this.request.getContextPath() +"/gradeForum/evalForumList/"+ forumId +"/name/a" + SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
		
	}
	
	/**
	 * Edit the existing special access to Forum user
	 * @throws Exception
	 */
	public void editForumUser() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		int specialAccessId = this.request.getIntParameter("special_access_id");
		int forumId = this.request.getIntParameter("forum_id");
		int userId = this.request.getIntParameter("user_id");
		
		SpecialAccess specialAccess = DataAccessDriver.getInstance().newSpecialAccessDAO().selectById(specialAccessId);
		this.context.put("specialAccess", specialAccess);
		
		Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(forumId); 
		this.context.put("forum", forum);
		
		User user = DataAccessDriver.getInstance().newUserDAO().selectById(userId);
		this.context.put("user", user);
		
		// get forum user groups
		getForumUserGroups(forum, user);
		
		context.put("calendarDateTimeFormat", SakaiSystemGlobals.getValue(ConfigKeys.CALENDAR_DATE_TIME_FORMAT));
		
		this.context.put("action", "editForumUserSave");
		
		this.setTemplateName(TemplateKeys.SPECIAL_ACCESS_FORUM_ADD_EDIT_USER);
	}
	
	/**
	 * Save the updated special access to forum user
	 * @throws Exception
	 */
	public void editForumUserSave() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		int specialAccessId = this.request.getIntParameter("special_access_id");
		int forumId = this.request.getIntParameter("forum_id");
		int userId = this.request.getIntParameter("user_id");
		
		Forum forum = DataAccessDriver.getInstance().newForumDAO().selectById(forumId); 
		
		SpecialAccess specialAccess = new SpecialAccess();
		specialAccess.setForumId(forum.getId());
		
		this.context.put("forum", forum);
		
		// forum groups user_group
		if ((forum.getGroups() != null) && (forum.getGroups().size() > 0))
		{
		}
		
		Category category = DataAccessDriver.getInstance().newCategoryDAO().selectById(forum.getCategoryId());
		
		String startDateParam = this.request.getParameter("start_date");
		if (startDateParam != null && startDateParam.trim().length() > 0)
		{
			Date startDate;
			try
			{
				startDate = DateUtil.getDateFromString(startDateParam.trim());
				specialAccess.setStartDate(startDate);
				
				SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
				specialAccess.setStartDateFormatted(df.format(startDate));
				
				// check with forum and category dates
				if ((forum.getStartDate() == null) && (forum.getEndDate() == null))
				{
					if ((category.getStartDate() == null) || (!startDate.equals(category.getStartDate())))
					{
						specialAccess.setOverrideStartDate(true);					
					}
				}
				else if ((forum.getStartDate() == null) || (!startDate.equals(forum.getStartDate())))
				{
					specialAccess.setOverrideStartDate(true);					
				}
				else
				{
					specialAccess.setOverrideStartDate(false);
				}
			} catch (ParseException e)
			{
				this.context.put("errorMessage", I18n.getMessage("Forums.Forum.DateParseError"));
				this.editForumUser();
				return;
			}
		}
		else
		{
			specialAccess.setStartDate(null);
		}
		
		String endDateParam = this.request.getParameter("end_date");
		if (endDateParam != null && endDateParam.trim().length() > 0)
		{
			Date endDate;
			try
			{
				endDate = DateUtil.getDateFromString(endDateParam.trim());
				specialAccess.setEndDate(endDate);
				
				SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
				specialAccess.setEndDateFormatted(df.format(endDate));
				
				if ((forum.getStartDate() == null) && (forum.getEndDate() == null))
				{
					if ((category.getEndDate() == null) || (!endDate.equals(category.getEndDate())))
					{
						specialAccess.setOverrideStartDate(true);					
					}
				}
				else if ((forum.getEndDate() == null ) || (!endDate.equals(forum.getEndDate())))
				{
					specialAccess.setOverrideEndDate(true);
				}
				else if ((category.getEndDate() == null ) || (!endDate.equals(category.getEndDate())))
				{
					specialAccess.setOverrideEndDate(true);
				}
				else
				{
					specialAccess.setOverrideEndDate(false);
				}
				
				String lockOnDue = this.request.getParameter("lock_on_due");
				if ((specialAccess.getEndDate() != null) && (lockOnDue != null && "1".equals(lockOnDue)))
				{
					specialAccess.setLockOnEndDate(true);
				}
				else
				{
					specialAccess.setLockOnEndDate(false);
				}
			} catch (ParseException e)
			{
				this.context.put("errorMessage", I18n.getMessage("Forums.Forum.DateParseError"));
				this.editForumUser();
				return;
			}
		}
		else
		{
			specialAccess.setEndDate(null);
			specialAccess.setLockOnEndDate(false);
		}
		
		List<Integer> specialAccessUser = new ArrayList<Integer>();
		specialAccessUser.add(new Integer(userId));
		
		// delete existing special access for the selected user
		SpecialAccess exisSpecialAccess = DataAccessDriver.getInstance().newSpecialAccessDAO().selectById(specialAccessId);
		List<Integer>  exisUserIds = exisSpecialAccess.getUserIds();
		if (exisUserIds.removeAll(specialAccessUser))
		{
			if (exisUserIds.size() > 0)
			{	
				exisSpecialAccess.setUserIds(exisUserIds);
				DataAccessDriver.getInstance().newSpecialAccessDAO().update(exisSpecialAccess);
			}
			else
			{
				DataAccessDriver.getInstance().newSpecialAccessDAO().delete(exisSpecialAccess.getId());
			}
		}
		
		specialAccess.setUserIds(specialAccessUser);
		if (((specialAccess.isOverrideStartDate()) || (specialAccess.isOverrideEndDate())) && (specialAccess.getUserIds().size() > 0))
		{
			int newSpecialAccessId  = DataAccessDriver.getInstance().newSpecialAccessDAO().addNew(specialAccess);
			specialAccess.setId(newSpecialAccessId);
		}
		
		JForum.setRedirect(this.request.getContextPath() +"/gradeForum/evalForumList/"+ forumId +"/name/a" + SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
	}
	
	
	/**
	 * Gets the forum users with groups or with out groups
	 * @param forum
	 * @throws IdUnusedException
	 */
	protected void getForumUsers(Forum forum) throws IdUnusedException
	{
		// check for groups
		if ((forum.getGroups() != null) && forum.getGroups().size() > 0)
		{
			List users = null;
			Map<String, User> usersMap = new HashMap<String, User>();
			try 
			{
				users = JForumUserUtil.updateMembersInfo();
				Iterator userIterator = users.iterator();
				while (userIterator.hasNext())
				{
					User user = (User) userIterator.next();
					if (JForumUserUtil.isJForumFacilitator(user.getSakaiUserId()))
					{
						userIterator.remove();
					}
					else
					{
						usersMap.put(user.getSakaiUserId(), user);
					}
				}
			}
			catch (Exception e) 
			{
				if (logger.isErrorEnabled()) 
				{
					logger.error(e.toString(), e);
				}
			}
					
			List forumGroupsIds = forum.getGroups();
			this.context.put("groupsExist", true);
			
			//show the selected groups for the forum and group users
			Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
			Collection sakaiSiteGroups = site.getGroups();
			//this.context.put("sakaigroups", sakaiSiteGroups);
			
			List sakaiSiteGroupsUsed = new ArrayList();
			Map forumGroupsMap = new HashMap();
			List<User> forumGroupMembers = new ArrayList<User>();
			
			for (Iterator i = sakaiSiteGroups.iterator(); i.hasNext();)
			{
				Group group = (Group) i.next();
				
				// get forum groups
				/*if (forumGroupsIds.contains(group.getId()))
				{
					sakaiSiteGroupsUsed.add(group);
					
					List<User> groupMembers = new ArrayList<User>();
					
					Set members = group.getMembers();
					for (Iterator iter = members.iterator(); iter.hasNext();)
					{
						Member member = (Member) iter.next();
						
						if (usersMap.containsKey(member.getUserId()))
						{
							groupMembers.add(usersMap.get(member.getUserId()));
						}
					}
					forumGroupsMap.put(group.getId(), groupMembers);
				}*/
				
				
				//get froum group users
				if (forumGroupsIds.contains(group.getId()))
				{
					//sakaiSiteGroupsUsed.add(group);
					
					//List<User> groupMembers = new ArrayList<User>();
					
					Set members = group.getMembers();
					for (Iterator iter = members.iterator(); iter.hasNext();)
					{
						Member member = (Member) iter.next();
						
						if (usersMap.containsKey(member.getUserId()))
						{
							if (!forumGroupMembers.contains(usersMap.get(member.getUserId())))
							{
								forumGroupMembers.add(usersMap.get(member.getUserId()));
							}
						}
					}
					//forumGroupsMap.put(group.getId(), groupMembers);
				}
			}
			
			//this.context.put("forumGroupsMap", forumGroupsMap);
			//this.context.put("sakaiSiteGroupsUsed", sakaiSiteGroupsUsed);
			
			// this.context.put("forumGroupMembers", forumGroupMembers);
			users.clear();
			users.addAll(forumGroupMembers);
			
			Collections.sort(users,new UserOrderComparator());
			
			this.context.put("users", users);
		}
		else
		{
			List users = null;
			try 
			{
				users = JForumUserUtil.updateMembersInfo();
				Iterator userIterator = users.iterator();
				while (userIterator.hasNext())
				{
					User user = (User) userIterator.next();
					if (JForumUserUtil.isJForumFacilitator(user.getSakaiUserId()))
					{
						userIterator.remove();
					}
				}
			}
			catch (Exception e) 
			{
				if (logger.isErrorEnabled()) 
				{
					logger.error(e.toString(), e);
				}
			}
			
			this.context.put("groupsExist", false);
			this.context.put("users", users);
		}
	}
	
	/**
	 * gets the user's sakai groups
	 * @param forum			Forum	
	 * @param user			User
	 * @throws IdUnusedException
	 */
	protected void getForumUserGroups(Forum forum, User user)throws IdUnusedException
	{
		// check for groups
		if ((forum.getGroups() != null) && forum.getGroups().size() > 0)
		{
			List forumGroupsIds = forum.getGroups();
			this.context.put("groupsExist", true);
			
			Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
			
			List userSiteGroupsUsed = new ArrayList();
		
			Collection userGroups = site.getGroupsWithMember(user.getSakaiUserId());
			for (Iterator usrGrpIter = userGroups.iterator(); usrGrpIter.hasNext(); ) 
			{
				org.sakaiproject.site.api.Group group = (org.sakaiproject.site.api.Group)usrGrpIter.next();
			
				if (forum.getGroups().contains(group.getId()))
				{
					userSiteGroupsUsed.add(group);
				}
			}
			
			//this.context.put("userSiteGroupsUsed", userSiteGroupsUsed);
		}
		else
		{
			this.context.put("groupsExist", false);
		}
	
		
	}
	
	/**
	 * Edit forum with out group special access
	 * @param forum		Forum
	 * @throws Exception
	 */
	protected void editForumSpecialAccess(Forum forum) throws Exception
	{
		Category category = DataAccessDriver.getInstance().newCategoryDAO().selectById(forum.getCategoryId());
		
		int specialAccessId = this.request.getIntParameter("special_access_id");
		SpecialAccess specialAccess = DataAccessDriver.getInstance().newSpecialAccessDAO().selectById(specialAccessId);
		
		String startDateParam = this.request.getParameter("start_date");
		if (startDateParam != null && startDateParam.trim().length() > 0)
		{
			Date startDate;
			try
			{
				startDate = DateUtil.getDateFromString(startDateParam.trim());
				specialAccess.setStartDate(startDate);
				
				SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
				specialAccess.setStartDateFormatted(df.format(startDate));
				
				// check with forum and category dates
				if ((forum.getStartDate() == null) && (forum.getEndDate() == null))
				{
					if ((category.getStartDate() == null) || (!startDate.equals(category.getStartDate())))
					{
						specialAccess.setOverrideStartDate(true);					
					}
				}
				else if ((forum.getStartDate() == null) || (!startDate.equals(forum.getStartDate())))
				{
					specialAccess.setOverrideStartDate(true);					
				}
				else
				{
					specialAccess.setOverrideStartDate(false);
				}
			} catch (ParseException e)
			{
				this.context.put("errorMessage", I18n.getMessage("Forums.Forum.DateParseError"));
				this.editForum();
				return;
			}
			
		}
		else
		{
			specialAccess.setOverrideStartDate(false);
			specialAccess.setStartDate(null);
		}
		
		String endDateParam = this.request.getParameter("end_date");
		if (endDateParam != null && endDateParam.trim().length() > 0)
		{
			Date endDate;
			try
			{
				endDate = DateUtil.getDateFromString(endDateParam.trim());
				specialAccess.setEndDate(endDate);
				
				SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
				specialAccess.setEndDateFormatted(df.format(endDate));
				
				// check with forum and category dates
				if ((forum.getStartDate() == null) && (forum.getEndDate() == null))
				{
					if ((category.getEndDate() == null) || (!endDate.equals(category.getEndDate())))
					{
						specialAccess.setOverrideStartDate(true);					
					}
				}
				else if ((forum.getEndDate() == null ) || (!endDate.equals(forum.getEndDate())))
				{
					specialAccess.setOverrideEndDate(true);
				}
				else
				{
					specialAccess.setOverrideEndDate(false);
				}
				
				String lockOnDue = this.request.getParameter("lock_on_due");
				if ((specialAccess.getEndDate() != null) && (lockOnDue != null && "1".equals(lockOnDue)))
				{
					specialAccess.setLockOnEndDate(true);
				}
				else
				{
					specialAccess.setLockOnEndDate(false);
				}
			} catch (ParseException e)
			{
				this.context.put("errorMessage", I18n.getMessage("Forums.Forum.DateParseError"));
				this.editForum();
				return;
			}
		}
		else
		{
			specialAccess.setEndDate(null);
			specialAccess.setOverrideEndDate(false);
			specialAccess.setLockOnEndDate(false);
		}
		
		List<Integer> users = new ArrayList<Integer>();
		String userIds[] = (String[])this.request.getObjectParameter("toUsername"+"ParamValues");
		if (userIds != null){
						
			for (int i = 0; i < userIds.length; i++) 
			{
				if (userIds[i] != null && userIds[i].trim().length() > 0) 
				{
					int userId;
					try
					{
						userId = Integer.parseInt(userIds[i]);
						users.add(new Integer(userId));
					} 
					catch (NumberFormatException e)
					{
						if (logger.isWarnEnabled()) logger.warn(this.getClass().getName()+".editForumSpecialAccess() : Error while parsing the userId.", e);
					}
				}
			}
		}
		else
		{
			try
			{
				int userId = Integer.parseInt(this.request.getParameter("toUsername"));
				users.add(new Integer(userId));
			} 
			catch (NumberFormatException e)
			{
				if (logger.isWarnEnabled()) logger.warn(this.getClass().getName()+".editForumSpecialAccess() : Error while parsing the userId.", e);
			}
		}
		
		specialAccess.setUserIds(users);
		
		// delete any existing special access for the selected users
		List<SpecialAccess> forumSpecialAccessList = DataAccessDriver.getInstance().newSpecialAccessDAO().selectByForumId(specialAccess.getForumId());
		for (SpecialAccess exiSpecialAccess : forumSpecialAccessList)
		{
			if (exiSpecialAccess.getId() == specialAccess.getId())
				continue;
			
			List<Integer>  exisUserIds = exiSpecialAccess.getUserIds();
			if (exisUserIds.removeAll(users))
			{
				if (exisUserIds.size() > 0)
				{	
					exiSpecialAccess.setUserIds(exisUserIds);
					DataAccessDriver.getInstance().newSpecialAccessDAO().update(exiSpecialAccess);
				}
				else
				{
					DataAccessDriver.getInstance().newSpecialAccessDAO().delete(exiSpecialAccess.getId());
				}
					
			}
		}
		
		if (((specialAccess.isOverrideStartDate()) || (specialAccess.isOverrideEndDate())) && (users.size() > 0))
		{
			DataAccessDriver.getInstance().newSpecialAccessDAO().update(specialAccess);
		}
		else
		{
			DataAccessDriver.getInstance().newSpecialAccessDAO().delete(specialAccess.getId());
		}
		
	}

	/**
	 * Add new special access to forum
	 * @param forum
	 * @throws Exception
	 */
	protected void addForumSpecialAccess(Forum forum) throws Exception
	{
		Category category = DataAccessDriver.getInstance().newCategoryDAO().selectById(forum.getCategoryId());
		
		SpecialAccess specialAccess = new SpecialAccess();
		specialAccess.setForumId(forum.getId());
		
		String startDateParam = this.request.getParameter("start_date");
		if (startDateParam != null && startDateParam.trim().length() > 0)
		{
			Date startDate;
			try
			{
				startDate = DateUtil.getDateFromString(startDateParam.trim());
				
				specialAccess.setStartDate(startDate);
				
				SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
				specialAccess.setStartDateFormatted(df.format(startDate));
				
				// check with forum and category dates
				if ((forum.getStartDate() == null) && (forum.getEndDate() == null))
				{
					if ((category.getStartDate() == null) || (!startDate.equals(category.getStartDate())))
					{
						specialAccess.setOverrideStartDate(true);					
					}
				}
				else if ((forum.getStartDate() == null) || (!startDate.equals(forum.getStartDate())))
				{
					specialAccess.setOverrideStartDate(true);					
				}
			} 
			catch (ParseException e)
			{
				this.context.put("errorMessage", I18n.getMessage("Forums.Forum.DateParseError"));
				this.insertForum();
				return;
			}
		}
		else
		{
			specialAccess.setStartDate(null);
		}
		
		String endDateParam = this.request.getParameter("end_date");
		if (endDateParam != null && endDateParam.trim().length() > 0)
		{
			Date endDate;
			try
			{
				endDate = DateUtil.getDateFromString(endDateParam.trim());
				
				specialAccess.setEndDate(endDate);
				
				SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
				specialAccess.setEndDateFormatted(df.format(endDate));
				
				// check with forum and category dates
				if ((forum.getStartDate() == null) && (forum.getEndDate() == null))
				{
					if ((category.getEndDate() == null) || (!endDate.equals(category.getEndDate())))
					{
						specialAccess.setOverrideStartDate(true);					
					}
				}
				else if ((forum.getEndDate() == null ) || (!endDate.equals(forum.getEndDate())))
				{
					specialAccess.setOverrideEndDate(true);
				}
				
				String lockOnDue = this.request.getParameter("lock_on_due");
				if ((specialAccess.getEndDate() != null) && (lockOnDue != null && "1".equals(lockOnDue)))
				{
					specialAccess.setLockOnEndDate(true);
				}
			} catch (ParseException e)
			{
				this.context.put("errorMessage", I18n.getMessage("Forums.Forum.DateParseError"));
				this.insertForum();
				return;
			}
			
		}
		else
		{
			specialAccess.setEndDate(null);
			specialAccess.setLockOnEndDate(false);
		}
		
		List<Integer> users = new ArrayList<Integer>();
		String userIds[] = (String[])this.request.getObjectParameter("toUsername"+"ParamValues");
		if (userIds != null){
						
			for (int i = 0; i < userIds.length; i++) 
			{
				if (userIds[i] != null && userIds[i].trim().length() > 0) 
				{
					int userId;
					try
					{
						userId = Integer.parseInt(userIds[i]);
						users.add(new Integer(userId));
					} 
					catch (NumberFormatException e)
					{
						if (logger.isWarnEnabled()) logger.warn(this.getClass().getName()+".addForumSpecialAccess() : Error while parsing the userId.", e);
					}
				}
			}
		}
		else
		{
			try
			{
				int userId = Integer.parseInt(this.request.getParameter("toUsername"));
				users.add(new Integer(userId));
			} 
			catch (NumberFormatException e)
			{
				if (logger.isWarnEnabled()) logger.warn(this.getClass().getName()+".addForumSpecialAccess() : Error while parsing the userId.", e);
			}
		}
		
		specialAccess.setUserIds(users);
		
		// delete any existing special access for the selected users
		List<SpecialAccess> categorySpecialAccessList = DataAccessDriver.getInstance().newSpecialAccessDAO().selectByForumId(forum.getId());
		for (SpecialAccess exiSpecialAccess : categorySpecialAccessList)
		{
			List<Integer>  exisUserIds = exiSpecialAccess.getUserIds();
			if (exisUserIds.removeAll(users))
			{
				if (exisUserIds.size() > 0)
				{	
					exiSpecialAccess.setUserIds(exisUserIds);
					DataAccessDriver.getInstance().newSpecialAccessDAO().update(exiSpecialAccess);
				}
				else
				{
					DataAccessDriver.getInstance().newSpecialAccessDAO().delete(exiSpecialAccess.getId());
				}
					
			}
		}
		if (((specialAccess.isOverrideStartDate()) || (specialAccess.isOverrideEndDate())) && (users.size() > 0))
		{
			DataAccessDriver.getInstance().newSpecialAccessDAO().addNew(specialAccess);
		}
	}
	
	/**
	 * Add new special access to forum with group
	 * @param forum
	 * @throws Exception
	 */
	protected void addForumGroupSpecialAccess(Forum forum) throws Exception
	{
		SpecialAccess specialAccess = new SpecialAccess();
		specialAccess.setForumId(forum.getId());
		
		String groupId = this.request.getParameter("group_id");
		
		if ((groupId == null) || (groupId.trim().length() == 0))
		{
			this.showForumList();
			return;
		}
		
		String startDateParam = this.request.getParameter("startdate_"+ groupId);
		if (startDateParam != null && startDateParam.trim().length() > 0)
		{
			Date startDate;
			try
			{
				startDate = DateUtil.getDateFromString(startDateParam.trim());
				
				specialAccess.setStartDate(startDate);
				
				SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
				specialAccess.setStartDateFormatted(df.format(startDate));
				
				if ((forum.getStartDate() == null) || (!startDate.equals(forum.getStartDate())))
				{
					specialAccess.setOverrideStartDate(true);					
				}
			} catch (ParseException e)
			{
				this.context.put("errorMessage", I18n.getMessage("Forums.Forum.DateParseError"));
				this.insertForum();
				return;
			}
		}
		else
		{
			specialAccess.setStartDate(null);
		}
		
		String endDateParam = this.request.getParameter("enddate_"+ groupId);
		if (endDateParam != null && endDateParam.trim().length() > 0)
		{
			Date endDate;
			try
			{
				endDate = DateUtil.getDateFromString(endDateParam.trim());
				
				specialAccess.setEndDate(endDate);
				
				SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
				specialAccess.setEndDateFormatted(df.format(endDate));
				
				if ((forum.getEndDate() == null ) || (!endDate.equals(forum.getEndDate())))
				{
					specialAccess.setOverrideEndDate(true);
				}
				
				String lockOnDue = this.request.getParameter("lock_on_due");
				if ((specialAccess.getEndDate() != null) && (lockOnDue != null && "1".equals(lockOnDue)))
				{
					specialAccess.setLockOnEndDate(true);
				}
			} catch (ParseException e)
			{
				this.context.put("errorMessage", I18n.getMessage("Forums.Forum.DateParseError"));
				this.insertForum();
				return;
			}
			
		}
		else
		{
			specialAccess.setEndDate(null);
			specialAccess.setLockOnEndDate(false);
		}
		
		List<Integer> users = new ArrayList<Integer>();
		String userIds[] = (String[])this.request.getObjectParameter("toUsername"+"ParamValues");
		if (userIds != null){
						
			for (int i = 0; i < userIds.length; i++) 
			{
				if (userIds[i] != null && userIds[i].trim().length() > 0) 
				{
					int userId;
					try
					{
						userId = Integer.parseInt(userIds[i]);
						users.add(new Integer(userId));
					} 
					catch (NumberFormatException e)
					{
						if (logger.isWarnEnabled()) logger.warn(this.getClass().getName()+".addForumGroupSpecialAccess() : Error while parsing the userId.", e);
					}
				}
			}
		}
		else
		{
			try
			{
				int userId = Integer.parseInt(this.request.getParameter("toUsername"));
				users.add(new Integer(userId));
			} 
			catch (NumberFormatException e)
			{
				if (logger.isWarnEnabled()) logger.warn(this.getClass().getName()+".addForumGroupSpecialAccess() : Error while parsing the userId.", e);
			}
		}
		
		specialAccess.setUserIds(users);
		
		// delete any existing special access for the selected users
		List<SpecialAccess> categorySpecialAccessList = DataAccessDriver.getInstance().newSpecialAccessDAO().selectByForumId(forum.getId());
		for (SpecialAccess exiSpecialAccess : categorySpecialAccessList)
		{
			List<Integer>  exisUserIds = exiSpecialAccess.getUserIds();
			if (exisUserIds.removeAll(users))
			{
				if (exisUserIds.size() > 0)
				{	
					exiSpecialAccess.setUserIds(exisUserIds);
					DataAccessDriver.getInstance().newSpecialAccessDAO().update(exiSpecialAccess);
				}
				else
				{
					DataAccessDriver.getInstance().newSpecialAccessDAO().delete(exiSpecialAccess.getId());
				}
					
			}
		}
		if (((specialAccess.isOverrideStartDate()) || (specialAccess.isOverrideEndDate())) && (users.size() > 0))
		{
			DataAccessDriver.getInstance().newSpecialAccessDAO().addNew(specialAccess);
		}
	}
	
}
