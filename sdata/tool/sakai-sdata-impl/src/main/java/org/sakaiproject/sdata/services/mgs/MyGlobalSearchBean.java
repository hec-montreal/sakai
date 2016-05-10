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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.search.api.InvalidSearchQueryException;
import org.sakaiproject.search.api.SearchList;
import org.sakaiproject.search.api.SearchResult;
import org.sakaiproject.search.api.SearchService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.SiteService.SelectionType;
import org.sakaiproject.site.api.SiteService.SortType;
import org.sakaiproject.tool.api.SessionManager;

/**
 * TODO Javadoc
 * 
 * @author
 */
public class MyGlobalSearchBean implements ServiceDefinition
{
	private List<Map> searchList = new ArrayList<Map>();

	private Map<String, Object> map2 = new HashMap<String, Object>();

	private Integer resultsOnPage = 5;

	List<String> arl = new ArrayList<String>();

	List<String> arl2 = new ArrayList<String>();

	private Map<String, Object> map = new HashMap<String, Object>();

	private static final Log log = LogFactory.getLog(MyGlobalSearchBean.class);

	private Site currentSite = null;

	/**
	 * TODO Javadoc
	 * 
	 * @param sessionManager
	 * @param siteService
	 */
	@SuppressWarnings("unchecked")
	public MyGlobalSearchBean(SessionManager sessionManager, SiteService siteService,
			ContentHostingService contentHostingService, HttpServletResponse response,
			String page, String searchParam, Boolean empty, String cSite)
	{

		if (cSite.equals("all"))
		{

			List<Site> sites = (List<Site>) siteService.getSites(SelectionType.ACCESS,
					null, null, null, SortType.TITLE_ASC, null);

			try
			{
				sites.add(0, (siteService.getSite(siteService
						.getUserSiteId(sessionManager.getCurrentSession().getUserId()))));

			}
			catch (IdUnusedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (Site s : sites)
			{

				arl.add(s.getId());

			}

			if (!empty)
			{

				try
				{

					SearchService search = (SearchService) ComponentManager
							.get("org.sakaiproject.search.api.SearchService");
					int currentPage = 0;
					if (page != null)
					{

						currentPage = Integer.parseInt(page);
					}
					else
					{

						currentPage = 1;
					}

					SearchList res = null;

					res = search.search(searchParam, arl, (currentPage - 1)
							* resultsOnPage, (currentPage * resultsOnPage), null, null);

					List<SearchResult> resBis = new ArrayList<SearchResult>();

					int totalfilesshown = 0;
					Iterator<SearchResult> it = res.iterator();

					while (it.hasNext())
					{

						SearchResult bis = it.next();

						if (bis.getId() != null && !bis.getId().equals(""))
						{

							totalfilesshown += 1;
							Map<String, String> search_result = new HashMap<String, String>();
							search_result.put("title", bis.getTitle());
							search_result.put("reference", bis.getReference());
							search_result.put("url", bis.getUrl());
							search_result.put("tool", bis.getTool());
							search_result.put("searchResult", bis.getSearchResult());

							String context = "";
							String[] str = (String[]) bis.getValueMap().get("context");
							for (String s : str)
							{
								context += s;
							}
							Site resultsite = siteService.getSite(context);
							search_result.put("site", resultsite.getTitle());
							search_result.put("siteId",context);
							search_result.put("score", String.valueOf(bis.getScore()));
							searchList.add(search_result);

							resBis.add(bis);
						}

						else
						{
							totalfilesshown += 1;
							log.error("access denied to file");
							// access denied to file
							Map<String, String> search_result = new HashMap<String, String>();
							search_result
									.put(
											"title",
											"You do not have permission to view this search result, please contact the worksite administrator");
							search_result.put("reference", "#");
							search_result.put("url", "#");
							search_result.put("tool", "");
							search_result.put("searchResult", "");
							search_result.put("score", "");
							search_result.put("siteId","");
							search_result.put("site", "");
							searchList.add(search_result);
						}
					}

					if (resBis.size() <= 0)
					{

						map2.put("total", totalfilesshown);
						map2.put("items", searchList);

					}
					else
					{
						map2.put("searchString", searchParam);
						map2.put("total", totalfilesshown);
						map2.put("items", searchList);
						map2.put("status", "succes");
						map2.put("totalResults", res.getFullSize());
					}
				}
				catch (Exception e)
				{

					map2.put("status", "failed");
				}
			}
			else
			{

				map2.put("status", "failed");
			}

		}
		else
		{ // als men op currentSite heeft geklikt

			try
			{

				currentSite = siteService.getSite(cSite);

				arl2.add(currentSite.getId());

				SearchService search = (SearchService) ComponentManager
						.get("org.sakaiproject.search.api.SearchService");
				int currentPage = 0;

				if (page != null)
				{

					currentPage = Integer.parseInt(page);
				}
				else
				{

					currentPage = 1;
				}

				SearchList res = null;

				res = search.search(searchParam, arl2, (currentPage - 1) * resultsOnPage,
						(currentPage * resultsOnPage), null, null);

				List<SearchResult> resBis = new ArrayList<SearchResult>();

				int totalfilesshown = 0;
				Iterator<SearchResult> it = res.iterator();

				while (it.hasNext())
				{

					SearchResult bis = it.next();

					if (bis.getId() != null && !bis.getId().equals(""))
					{

						totalfilesshown += 1;
						Map<String, String> search_result = new HashMap<String, String>();
						search_result.put("title", bis.getTitle());
						search_result.put("reference", bis.getReference());
						search_result.put("url", bis.getUrl());
						search_result.put("tool", bis.getTool());
						search_result.put("searchResult", bis.getSearchResult());
						search_result.put("score", String.valueOf(bis.getScore()));
						
						String context = "";
						String[] str = (String[]) bis.getValueMap().get("context");
						for (String s : str)
						{
							context += s;
						}
						Site resultsite = siteService.getSite(context);
						search_result.put("site", resultsite.getTitle());
						searchList.add(search_result);
						search_result.put("siteId",context);
						resBis.add(bis);
					}
					else
					{
						totalfilesshown += 1;
						log.error("access denied to file");
						// access denied to file
						Map<String, String> search_result = new HashMap<String, String>();
						search_result
								.put(
										"title",
										"You do not have permission to view this search result, please contact the worksite administrator");
						search_result.put("reference", "#");
						search_result.put("url", "#");
						search_result.put("tool", "");
						search_result.put("searchResult", "");
						search_result.put("score", "");
						search_result.put("siteId","");
						search_result.put("site", "");

						searchList.add(search_result);
					}
				}

				if (resBis.size() <= 0)
				{

					map2.put("total", totalfilesshown);
					map2.put("items", searchList);

				}
				else
				{
					map2.put("searchString", searchParam);
					map2.put("total", totalfilesshown);
					map2.put("items", searchList);
					map2.put("status", "succes");
					map2.put("totalResults", res.getFullSize());
				}
			}
			catch (IdUnusedException e1)
			{
				map2.put("total", "0");
				map2.put("items", searchList);

				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch (InvalidSearchQueryException e1)
			{
				map2.put("total", "0");
				map2.put("items", searchList);

				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

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
