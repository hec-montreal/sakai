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

package org.sakaiproject.sdata.services.me;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SitePage;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

/**
 * TODO Javadoc
 * 
 * @author
 */
public class MeBean implements ServiceDefinition
{

	private Session currentSession;

	private Map<String, Object> map2 = new HashMap<String, Object>();;

	private Map<String, Object> map = new HashMap<String, Object>();

	/**
	 * TODO Javadoc
	 * 
	 * @param sessionManager
	 * @param siteService
	 */
	public MeBean(SiteService siteService, SessionManager sessionManager, 
			UserDirectoryService userDirectoryService, HttpServletResponse response)
	{
		User user = null;
		currentSession = sessionManager.getCurrentSession();

		try
		{

			user = userDirectoryService.getUser(currentSession.getUserId());
		}
		catch (UserNotDefinedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// serialize user object

		if (user == null)
		{

			try
			{
				response.sendError(401);
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else
		{

			try
			{

				Site myWorkSite = (siteService.getSite(siteService
						.getUserSiteId(currentSession.getUserId())));

				map.put("workspace", myWorkSite.getId());

				List<SitePage> pages = (List<SitePage>) myWorkSite.getOrderedPages();

				for (SitePage page : pages)
				{

					List<ToolConfiguration> lst = (List<ToolConfiguration>) page
							.getTools();

					for (ToolConfiguration conf : lst)
					{

						Tool t = conf.getTool();

						if (t != null && t.getId() != null)
						{
							if (t.getId().equals("sakai.membership")
									|| t.getId().equals("sakai.sites"))
							{
								map.put("cp", conf.getId());
							}
							else if (t.getId().equals("sakai.preferences")
									|| t.getId().equals("sakai.preference"))
							{
								map.put("pref", conf.getId());
							}
						}

					}

				}

			}
			catch (IdUnusedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			map.put("userid", user.getId());
			map.put("firstname", user.getFirstName());
			map.put("lastname", user.getLastName());
			map.put("displayId", user.getDisplayId());
			map.put("email", user.getEmail());
			try
			{
				map.put("createdBy", user.getCreatedBy().getDisplayName());
			}
			catch (NullPointerException npe)
			{
				map.put("createdTime", "na");
			}
			try
			{
				map.put("createdTime", user.getCreatedDate().toLocaleString());
			}
			catch (NullPointerException npe)
			{
				map.put("createdTime", "na");

			}
			map.put("userEid", user.getEid());
			
			
			Map<String, Object> properties = new HashMap<String, Object>();
			ResourceProperties p = user.getProperties();
			for (Iterator<String> i = p.getPropertyNames(); i.hasNext(); ) {
				
				String pname = i.next();
				List<String> l = p.getPropertyList(pname);
				if ( l.size() ==  1 ) {
					properties.put(pname, l.get(0));
				} else if ( l.size() > 1 ) {
					properties.put(pname, l);
				}
			}
			map.put("properties", properties);
			
			
			
			// map2.put("items", user);
			map2.put("items", map);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ServiceDefinition#getResponseMap()
	 */
	public Map<String, Object> getResponseMap()
	{

		return map2;
	}

}
