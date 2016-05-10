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

package org.sakaiproject.sdata.services.mra;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.announcement.api.AnnouncementService;
import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.entity.api.EntityManager;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory;
import org.sakaiproject.search.api.SearchService;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;

/**
 * TODO Javadoc
 * 
 * @author
 */
public class MyRecentChangesServiceDefinitionFactory implements ServiceDefinitionFactory
{

	private SessionManager sessionManager;

	private SiteService siteService;

	private ComponentManager componentManager;

	private SqlService sqlService;

	private ContentHostingService contentHostingService;

	private AnnouncementService announcementService;

	private EntityManager entityManager;

	private SearchService searchService;

	/**
	 * TODO Javadoc
	 */
	public MyRecentChangesServiceDefinitionFactory()
	{
		componentManager = org.sakaiproject.component.cover.ComponentManager
				.getInstance();

		searchService = (SearchService) componentManager
				.get("org.sakaiproject.search.api.SearchService");
		contentHostingService = (ContentHostingService) componentManager
				.get("org.sakaiproject.content.api.ContentHostingService");
		announcementService = (AnnouncementService) componentManager
				.get("org.sakaiproject.announcement.api.AnnouncementService");
		entityManager = (EntityManager) componentManager
				.get("org.sakaiproject.entity.api.EntityManager");

		siteService = (SiteService) componentManager.get(SiteService.class.getName());
		sessionManager = (SessionManager) componentManager.get(SessionManager.class
				.getName());
		sqlService = (SqlService) componentManager.get(SqlService.class.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory#getSpec(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ServiceDefinition getSpec(HttpServletRequest request,
			HttpServletResponse response)
	{
		int paging = 1;
		if (request.getParameter("page") != null)
		{
			paging = Integer.parseInt(request.getParameter("page"));
		}
		return new MyRecentChangesBean(sessionManager, siteService, componentManager,
				sqlService, searchService, contentHostingService, announcementService,
				entityManager, paging);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory#init(java.util.Map)
	 */
	public void init(Map<String, String> config)
	{
	}

}
