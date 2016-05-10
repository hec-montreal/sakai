/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/tool/JCRDumper.java $
 * $Id: JCRDumper.java 46691 2008-03-13 15:34:40Z ian@caret.cam.ac.uk $
 ***********************************************************************************
 *
 * Copyright (c) 2008 Timefields Ltd
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

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.jcr.api.JCRService;
import org.sakaiproject.sdata.tool.api.Handler;
import org.sakaiproject.tool.api.Tool;

/**
 * TODO Javadoc
 * 
 * @author ieb
 */
public class JCRDumper implements Handler
{

	private static final Log log = LogFactory.getLog(JCRDumper.class);

	private ComponentManager componentManager;

	private JCRService jcrService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.Handler#init(java.util.Map)
	 */
	public void init(Map<String, String> config) throws ServletException
	{

		componentManager = org.sakaiproject.component.cover.ComponentManager
				.getInstance();

		jcrService = (JCRService) componentManager.get(JCRService.class.getName());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		request.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);
		String path = request.getPathInfo();
		if (path == null || path.length() == 0)
		{
			path = "/";
		}

		response.setContentType("text/xml");
		try
		{
			if (path.startsWith("/sys"))
			{
				path = path.substring("/sys".length());
				jcrService.getSession().exportSystemView(path,
						response.getOutputStream(), true, false);
			}
			else
			{
				jcrService.getSession().exportDocumentView(path,
						response.getOutputStream(), true, false);

			}
		}
		catch (RepositoryException e)
		{
			log.error("Failed", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.Handler#doDelete(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.Handler#sendError(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Throwable)
	 */
	public void sendError(HttpServletRequest request, HttpServletResponse response,
			Throwable ex) throws IOException
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.Handler#sendMap(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.util.Map)
	 */
	public void sendMap(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> contetMap) throws IOException
	{
	}

}
