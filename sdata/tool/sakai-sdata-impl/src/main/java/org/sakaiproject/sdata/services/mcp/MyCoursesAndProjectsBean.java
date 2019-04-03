/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/trunk/sdata/sdata-tool/impl/src/java/org/sakaiproject/sdata/tool/JCRDumper.java $
 * $Id: JCRDumper.java 45207 2008-02-01 19:01:06Z ian@caret.cam.ac.uk $
 ***********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.sdata.services.mcp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.javax.PagingPosition;
import org.sakaiproject.sdata.services.mra.MyRecentChangesBean;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.api.SiteService.SelectionType;
import org.sakaiproject.site.api.SiteService.SortType;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;

/**
 * A bean where we construct a map object with all the sites a person is a
 * member of. The Map object structure is like:
 * <ul>
 * <li>site title</li>
 * <li>site id</li>
 * <li>site url</li>
 * </ul>
 * 
 * @author
 */
public class MyCoursesAndProjectsBean implements ServiceDefinition {

	private List<Site> mysites;

	private Session currentSession;

	@SuppressWarnings("unchecked")
	private List<Map> MyMappedSites = new ArrayList<Map>();

	private Map<String, Object> map2 = new HashMap<String, Object>();
	
	private static final Log log = LogFactory.getLog(MyCoursesAndProjectsBean.class);

	private boolean containstool(Site site, String tool){
		
		boolean containstool = false;
		
		List<SitePage> pages = (List<SitePage>) site.getOrderedPages();

		for (SitePage page : pages)
		{
			List<ToolConfiguration> lst = (List<ToolConfiguration>) page
					.getTools();
			for (ToolConfiguration conf : lst)
			{
				Tool t = conf.getTool();
				if (t != null && t.getId() != null && t.getId().equals(tool))
				{
					containstool = true;
				}				
			}
		}

		return containstool;
		
	}
	
	/**
	 * The MyCoursesAndProjectsBean constructor
	 * 
	 * @param sessionManager
	 * @param siteService
	 */
	@SuppressWarnings("unchecked")
	public MyCoursesAndProjectsBean(SessionManager sessionManager, SiteService siteService, HttpServletRequest request)
	{
		
		if (request.getMethod().toLowerCase().equals("get")){
		
			if (request.getParameter("action") == null){
		
				if (request.getParameter("rolestrict") == null){
				
					setCurrentSession(sessionManager.getCurrentSession());
					setMysites((List<Site>) siteService.getSites(SelectionType.ACCESS, null, null,
							null, SortType.TITLE_ASC, null));
			
					try
					{
						if (request.getParameter("tool") == null){
							mysites.add(0, (siteService.getSite(siteService.getUserSiteId(currentSession
									.getUserId()))));
						} else {
							if (containstool(siteService.getSite(siteService.getUserSiteId(currentSession
									.getUserId())), request.getParameter("tool"))){
								mysites.add(0, (siteService.getSite(siteService.getUserSiteId(currentSession
										.getUserId()))));
							}
						}
			
					}
					catch (IdUnusedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
					for (Site site : mysites)
					{
						if (request.getParameter("tool") == null){
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("title", site.getTitle());
							map.put("id", site.getId());
							map.put("url", site.getUrl());
							map.put("iconUrl", site.getIconUrl());
							map.put("owner", site.getCreatedBy().getDisplayName());
							map.put("creationDate", new SimpleDateFormat("dd-MM-yyyy").format(new Date(site.getCreatedTime().getTime())));
							map.put("members", site.getMembers().size());
							map.put("description", site.getDescription());
							map.put("siteType", site.getType());
							getMyMappedSites().add(map);
						} else {
							if (containstool(site, request.getParameter("tool"))){
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("title", site.getTitle());
								map.put("id", site.getId());
								map.put("url", site.getUrl());
								map.put("iconUrl", site.getIconUrl());
								map.put("owner", site.getCreatedBy().getDisplayName());
								map.put("creationDate", new SimpleDateFormat("dd-MM-yyyy").format(new Date(site.getCreatedTime().getTime())));
								map.put("members", site.getMembers().size());
								map.put("description", site.getDescription());
								map.put("siteType", site.getType());
								getMyMappedSites().add(map);
							}
						}
						
					}
					
					
			
					map2.put("items", getMyMappedSites());
					
				} else if (request.getParameter("rolestrict") != null){
					
					if (request.getParameter("tool") != null){
						
						setCurrentSession(sessionManager.getCurrentSession());
						setMysites((List<Site>) siteService.getSites(SelectionType.ACCESS, null, null,
								null, SortType.TITLE_ASC, null));
				
						for (Site site : mysites)
						{
							if (site.getUserRole(currentSession.getUserId()).getId().equals(site.getMaintainRole())){
								
								if (containstool(site, request.getParameter("tool"))){
									Map<String, Object> map = new HashMap<String, Object>();
									map.put("title", site.getTitle());
									map.put("id", site.getId());
									map.put("url", site.getUrl());
									map.put("iconUrl", site.getIconUrl());
									map.put("owner", site.getCreatedBy().getDisplayName());
									map.put("creationDate", new SimpleDateFormat("dd-MM-yyyy").format(new Date(site.getCreatedTime().getTime())));
									map.put("members", site.getMembers().size());
									map.put("description", site.getDescription());
									map.put("siteType", site.getType());
									getMyMappedSites().add(map);
								}
							}
						}
						
						map2.put("items", getMyMappedSites());
						
					} else {
						
						setCurrentSession(sessionManager.getCurrentSession());
						setMysites((List<Site>) siteService.getSites(SelectionType.ACCESS, null, null,
								null, SortType.TITLE_ASC, null));
				
						for (Site site : mysites)
						{
							if (site.getUserRole(currentSession.getUserId()).getId().equals(site.getMaintainRole())){
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("title", site.getTitle());
								map.put("id", site.getId());
								map.put("url", site.getUrl());
								map.put("iconUrl", site.getIconUrl());
								map.put("owner", site.getCreatedBy().getDisplayName());
								map.put("creationDate", new SimpleDateFormat("dd-MM-yyyy").format(new Date(site.getCreatedTime().getTime())));
								map.put("members", site.getMembers().size());
								map.put("description", site.getDescription());
								map.put("siteType", site.getType());
								getMyMappedSites().add(map);
							}
						}
						
						map2.put("items", getMyMappedSites());
						
					}
					
				}
			
			} else if (request.getParameter("action") != null || request.getParameter("action").equals("joinable")){
				
				int page = 1;
				int pagesize = 5;
				
				String search = "";
				
				if (request.getParameter("search") != null){
					try {
						search = request.getParameter("search");
					} catch (Exception ex){}
				}
				
				if (request.getParameter("page") != null){
					try {
						page = Integer.parseInt(request.getParameter("page"));
					} catch (Exception ex){}
				}
				
				PagingPosition pager = new PagingPosition((page - 1) * pagesize + 1, pagesize * (page - 1) + pagesize);
				
				List<Site> myJoinableSites = siteService.getSites(org.sakaiproject.site.api.SiteService.SelectionType.JOINABLE,
								null, search, null, org.sakaiproject.site.api.SiteService.SortType.TITLE_ASC, pager);
				
				int totalsites = siteService.countSites(org.sakaiproject.site.api.SiteService.SelectionType.JOINABLE, null, search, null);
				
				for (Site site : myJoinableSites)
				{
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("title", site.getTitle());
					map.put("id", site.getId());
					map.put("url", site.getUrl());
					map.put("iconUrl", site.getIconUrl());
					map.put("owner", site.getCreatedBy().getDisplayName());
					try {
						map.put("creationDate", new SimpleDateFormat("dd-MM-yyyy").format(new Date(site.getCreatedTime().getTime())));
					} catch (Exception ex){
						map.put("creationDate", "unknown");
					}
					map.put("members", site.getMembers().size());
					map.put("description", site.getDescription());
					map.put("siteType", site.getType());
					getMyMappedSites().add(map);
				}
				
				
				map2.put("total", totalsites);
				map2.put("items", getMyMappedSites());

			}
		
		} else if (request.getMethod().toLowerCase().equals("post")) {
		
			
			/*
			 *  Courses And Project actions (Join a Site - Unjoin a Site)
			 */
			
			String action = request.getParameter("action");
			
			if (action.equals("unjoin")){
				Session session = null;
				setCurrentSession(sessionManager.getCurrentSession());
				try
				{
					currentSession = sessionManager.getCurrentSession();
					session = sessionManager.startSession();
					session.setUserId("admin");
					sessionManager.setCurrentSession(session);
					
					Site s = siteService.getSite(request.getParameter("siteid"));
					s.removeMember(getCurrentSession().getUserId());
					siteService.save(s);
					
					log.info("User = " + getCurrentSession().getUserId() + " - " + s);
					map2.put("status", "success");
				}
				catch (IdUnusedException e)
				{
					// TODO Auto-generated catch block
					map2.put("status", "failed");
					e.printStackTrace();
				}
				catch (PermissionException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					map2.put("status", "permission denied");
					e.printStackTrace();
				}
				finally {
					sessionManager.setCurrentSession(currentSession);
					session.invalidate();
					session = null;
				}
				
			}  else if (action.equals("join")){
				Session session = null;
				setCurrentSession(sessionManager.getCurrentSession());
				try
				{
	
					currentSession = sessionManager.getCurrentSession();
					session = sessionManager.startSession();
					session.setUserId("admin");
					sessionManager.setCurrentSession(session);
							
					Site s = siteService.getSite(request.getParameter("siteid"));
					s.addMember(getCurrentSession().getUserId(), s.getJoinerRole(), true, false);
					siteService.save(s);
					map2.put("status", "success");
				}
				catch (IdUnusedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					map2.put("status", "failed");
				}
				catch (PermissionException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					map2.put("status", "failed");
					
				} 
				finally {
					sessionManager.setCurrentSession(currentSession);
					session.invalidate();
					session = null;
				}			
			}
			
		}

	}

	/**
	 * setter for mysites
	 * 
	 * @param mysites
	 */
	public void setMysites(List<Site> mysites) {
		this.mysites = mysites;
	}

	/**
	 * get mysites
	 * 
	 * @return all the sites the person is a member of.
	 */
	public List<Site> getMysites() {
		return mysites;
	}

	/**
	 * set the currentsession
	 * 
	 * @param currentSession
	 */
	public void setCurrentSession(Session currentSession) {
		this.currentSession = currentSession;
	}

	/**
	 * get currentsession
	 * 
	 * @return currentSession
	 */
	public Session getCurrentSession() {
		return currentSession;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ServiceDefinition#getResponseMap()
	 */
	public Map<String, Object> getResponseMap() {

		return map2;
	}

	/**
	 * set myMappedSites
	 * 
	 * @param myMappedSites
	 */
	public void setMyMappedSites(List<Map> myMappedSites) {
		MyMappedSites = myMappedSites;
	}

	/**
	 * get myMappedSites
	 * 
	 * @return Return a list of mapped sites
	 */
	public List<Map> getMyMappedSites() {
		return MyMappedSites;
	}

}
