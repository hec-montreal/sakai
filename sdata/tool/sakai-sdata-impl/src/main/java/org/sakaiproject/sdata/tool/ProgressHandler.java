/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/tool/ProgressHandler.java $
 * $Id: ProgressHandler.java 46605 2008-03-12 11:07:29Z ian@caret.cam.ac.uk $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007 The Sakai Foundation.
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

package org.sakaiproject.sdata.tool;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.sdata.tool.api.Handler;

/**
 * Gives acess to an uplaod progress map based on an id
 * 
 * @author ieb
 */
public abstract class ProgressHandler implements Handler
{

	private static Map<String, Map<String, Object>> progressStore = new ConcurrentHashMap<String, Map<String, Object>>();

	private static final String BASE_URL_INIT = "baseurl";

	private static final String DEFAULT_BASE_URL = "";

	private String baseUrl;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.Handler#doDelete(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.Handler#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		String progressID = request.getPathInfo();
		if (progressID.startsWith("/" + baseUrl))
		{
			progressID = progressID.substring(baseUrl.length() + 1);
		}
		if (progressID.startsWith("/"))
		{
			progressID = progressID.substring(1);
		}

		if (progressID != null)
		{
			Map m = progressStore.get(progressID);
			if (m != null)
			{
				m.put("servertime", System.currentTimeMillis());
				sendMap(request, response, m);
			}
			else
			{
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				response.getWriter().print(
						"{ 'status': 'not-found', 'error' :'Didnt locate progress information for id "
								+ progressID + "' }");
			}
		}
		else
		{
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response.getWriter().print(
					"{ 'status': 'not-found', 'error' :'Didnt locate progress information for id "
							+ progressID + "' }");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.Handler#doHead(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doHead(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.Handler#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.Handler#doPut(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.Handler#init(java.util.Map)
	 */
	public void init(Map<String, String> config) throws ServletException
	{
		baseUrl = config.get(BASE_URL_INIT);
		if (baseUrl == null)
		{
			baseUrl = DEFAULT_BASE_URL;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.Handler#setHandlerHeaders(javax.servlet.http.HttpServletResponse)
	 */
	public void setHandlerHeaders(HttpServletResponse response)
	{
		response.setHeader("x-sdata-handler", this.getClass().getName());
	}


	public static void setMap(String key, Map<String, Object> map)
	{
		if (key != null)
		{
			if (map == null)
			{
				progressStore.remove(key);
			}
			else
			{
				progressStore.put(key, map);
			}
		}
	}

	/**
	 * @param progressID
	 */
	public static void clearMap()
	{
		long now = System.currentTimeMillis();
		for ( String key : progressStore.keySet() ) {
			Object o = progressStore.get(key);
			if ( o instanceof Map )
			{
				Map<String, Object> m = (Map<String, Object>)o;
				if (m.get("all-completed-at") != null)
				{
					long t = Long.parseLong(String.valueOf(m.get("all-completed-at")));
					if ( (now - t) > 60000 ) {
						progressStore.remove(key);
					}
				}
				
			}
		}
	}
	public static void markComplete(Map<String, Object> progressMap) {
		progressMap.put("all-completed", "true");
		progressMap.put("all-completed-at", String.valueOf(System.currentTimeMillis()));
	}
	public static void clearComplete(Map<String, Object> progressMap) {
		progressMap.remove("all-completed");
		progressMap.remove("all-completed-at");
	}

	/**
	 * @param progressID
	 * @return
	 */
	public static Map<String, Object> getMap(String progressID)
	{
		if (progressID == null)
		{
			return null;
		}
		else
		{
			return progressStore.get(progressID);
		}
	}
}
