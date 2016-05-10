/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/trunk/sdata/sdata-tool/impl/src/java/org/sakaiproject/sdata/tool/StreamRequestFilter.java $
 * $Id: StreamRequestFilter.java 45207 2008-02-01 19:01:06Z ian@caret.cam.ac.uk $
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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.sdata.tool.api.Handler;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory;

/**
 * TODO Javadoc
 * 
 * @author ieb
 */
public abstract class ServiceHandler implements Handler
{

	private ServiceDefinitionFactory serviceDefinitionFactory = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(Map<String, String> config) throws ServletException
	{
		serviceDefinitionFactory = getServiceDefinitionFactory(config);
	}

	/**
	 * TODO Javadoc
	 * 
	 * @return
	 * @throws ServletException
	 */
	protected abstract ServiceDefinitionFactory getServiceDefinitionFactory()
			throws ServletException;

	/**
	 * TODO Javadoc
	 * 
	 * @param config
	 * @return
	 * @throws ServletException
	 */
	@SuppressWarnings("unchecked")
	protected ServiceDefinitionFactory getServiceDefinitionFactory(
			Map<String, String> config) throws ServletException
	{
		try
		{
			String factoryName = config.get("factory-name");

			Class<ServiceDefinitionFactory> c = (Class<ServiceDefinitionFactory>) this
					.getClass().getClassLoader().loadClass(factoryName);
			ServiceDefinitionFactory o = c.newInstance();
			o.init(config);
			return o;
		}
		catch (InstantiationException e)
		{
			throw new ServletException(e);
		}
		catch (IllegalAccessException e)
		{
			throw new ServletException(e);
		}
		catch (ClassNotFoundException e)
		{
			throw new ServletException(e);
		}
	}

	/**
	 * Respond to an HTTP GET request.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{

		try
		{
			ServiceDefinition serviceDefinition = serviceDefinitionFactory.getSpec(
					request, response);

			Map<String, Object> responseMap = serviceDefinition.getResponseMap();

			sendMap(request, response, responseMap);
		}
		catch (Exception ex)
		{
			sendError(request, response, ex);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		// process(request,response);
		//response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		try
		{
			ServiceDefinition serviceDefinition = serviceDefinitionFactory.getSpec(
					request, response);

			Map<String, Object> responseMap = serviceDefinition.getResponseMap();

			sendMap(request, response, responseMap);
		}
		catch (Exception ex)
		{
			sendError(request, response, ex);
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
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
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
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
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
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
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
	
	
}
