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

package org.sakaiproject.sdata.services.mgs;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;

/**
 * TODO Javadoc
 * 
 * @author
 */
public class MyGlobalSearchDefinitionFactory implements ServiceDefinitionFactory
{

	private SessionManager sessionManager;

	private SiteService siteService;

	private ComponentManager componentManager;

	private ContentHostingService contentHostingService;

	/**
	 * TODO Javadoc
	 */
	public MyGlobalSearchDefinitionFactory()
	{
		componentManager = org.sakaiproject.component.cover.ComponentManager
				.getInstance();
		siteService = (SiteService) componentManager.get(SiteService.class.getName());
		sessionManager = (SessionManager) componentManager.get(SessionManager.class
				.getName());
		contentHostingService = (ContentHostingService) componentManager
				.get(ContentHostingService.class.getName());
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
		// final Log log =
		// LogFactory.getLog(MyFileFinderDefinitionFactory.class);

		String searchParam = null;
		String page = request.getParameter("page");
		Boolean empty = false;
		String currentSiteSearch = null;

		if (request.getParameter("search") != null
				&& !request.getParameter("search").equals("")
				&& request.getParameter("siteId") != null)

		{

			if (!request.getParameter("siteId").equals("all"))
			{

				// do a local search action
				currentSiteSearch = request.getParameter("siteId");

				searchParam = request.getParameter("search");

				searchParam = searchParam.replaceAll("[\\!]", "");

				searchParam = searchParam.replaceAll("[\\^]", "");

				searchParam = searchParam.replaceAll("[\\(]", "");

				searchParam = searchParam.replaceAll("[\\{]", "");

				searchParam = searchParam.replaceAll("[\\}]", "");

				searchParam = searchParam.replaceAll("[\\[]", "");

				searchParam = searchParam.replaceAll("[\\]]", "");

				searchParam = searchParam.replaceAll("[\\%]", "");

			}
			else
			{

				searchParam = request.getParameter("search");

				searchParam = searchParam.replaceAll("[\\!]", "");

				searchParam = searchParam.replaceAll("[\\^]", "");

				searchParam = searchParam.replaceAll("[\\(]", "");

				searchParam = searchParam.replaceAll("[\\{]", "");

				searchParam = searchParam.replaceAll("[\\}]", "");

				searchParam = searchParam.replaceAll("[\\[]", "");

				searchParam = searchParam.replaceAll("[\\]]", "");

				searchParam = searchParam.replaceAll("[\\%]", "");

				empty = false;

				currentSiteSearch = "all";

			}

		}
		else
		{
			currentSiteSearch = "all";
			searchParam = "";
			empty = true;
		}

		return new MyGlobalSearchBean(sessionManager, siteService, contentHostingService,
				response, page, searchParam, empty, currentSiteSearch);
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
