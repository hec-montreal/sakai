/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/tool/SnoopHandler.java $
 * $Id: SnoopHandler.java 46605 2008-03-12 11:07:29Z ian@caret.cam.ac.uk $
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
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.sdata.tool.api.Handler;

/**
 * @author ieb
 */
public class SnoopHandler implements Handler
{

	private static final Log log = LogFactory.getLog(SnoopHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.Handler#doDelete(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		snoopRequest(request);
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
		snoopRequest(request);
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
		snoopRequest(request);
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
		snoopRequest(request);
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
		snoopRequest(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.Handler#init(java.util.Map)
	 */
	public void init(Map<String, String> config) throws ServletException
	{
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

	/**
	 * @param request
	 */
	private void snoopRequest(HttpServletRequest request)
	{
		StringBuilder sb = new StringBuilder("SData Request :");
		sb.append("\n\tRequest Path :").append(request.getPathInfo());
		sb.append("\n\tMethod :").append(request.getMethod());
		for (Enumeration<String> hnames = request.getHeaderNames(); hnames
				.hasMoreElements();)
		{
			String name = hnames.nextElement();
			sb.append("\n\tHeader :").append(name).append("=[").append(
					request.getHeader(name)).append("]");
		}
		for (Enumeration<String> hnames = request.getParameterNames(); hnames
				.hasMoreElements();)
		{
			String name = hnames.nextElement();
			sb.append("\n\tParameter :").append(name).append("=[").append(
					request.getParameter(name)).append("]");
		}
		if (request.getCookies() != null)
		{
			for (Cookie c : request.getCookies())
			{
				sb.append("\n\tCookie:");
				sb.append("name[").append(c.getName());
				sb.append("]path[").append(c.getPath());
				sb.append("]value[").append(c.getValue());
			}
		}
		sb.append("]");
		log.info(sb.toString());
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.sdata.tool.api.Handler#sendError(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Throwable)
	 */
	public void sendError(HttpServletRequest request, HttpServletResponse response, Throwable ex) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.sdata.tool.api.Handler#sendMap(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.util.Map)
	 */
	public void sendMap(HttpServletRequest request, HttpServletResponse response, Map<String, Object> contetMap) throws IOException
	{
		// TODO Auto-generated method stub
		
	}

}
