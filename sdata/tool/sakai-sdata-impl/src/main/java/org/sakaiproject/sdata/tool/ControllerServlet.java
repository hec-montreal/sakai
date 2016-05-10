/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/tool/ControllerServlet.java $
 * $Id: ControllerServlet.java 47053 2008-03-21 01:14:16Z ian@caret.cam.ac.uk $
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.sdata.tool.api.Handler;
import org.sakaiproject.tool.api.Tool;

/**
 * <p>
 * Loads the a list of handlers as specified in in the init params. These may be
 * off the 2 forms. Either all the properties can be specified in a single init
 * property in web.xml. The name values, seperated by = and the properties
 * seperated by ;.
 * </p>
 * <p>
 * 
 * <pre>
 *       	 &lt;init-param&gt;
 *       &lt;param-name&gt;config&lt;/param-name&gt;
 *       &lt;param-value&gt; 
 *       1.classname=org.sakaiproject.sdata.tool.JCRDumper;
 *       1.baseurl=d;
 *       2.classname=org.sakaiproject.sdata.tool.json.JsonCHSHandler;
 *       2.baseurl=c;
 *       3.classname=org.sakaiproject.sdata.tool.json.JsonJcrHandler;
 *       3.baseurl=f;
 *       4.classname=org.sakaiproject.sdata.tool.json.JsonJcrUserStorageHandler;
 *       4.baseurl=p;
 *       5.classname=org.sakaiproject.sdata.tool.json.JsonCHSUserStorageHandler;
 *       5.baseurl=cp;
 *       6.classname=org.sakaiproject.sdata.tool.xmlrpc.XmlRpcCHSHandler;
 *       6.baseurl=xc;
 *       7.classname=org.sakaiproject.sdata.tool.xmlrpc.XmlRpcJcrHandler;
 *       7.baseurl=xf;
 *       8.classname=org.sakaiproject.sdata.tool.xmlrpc.XmlRpcJcrUserStorageHandler;
 *       8.baseurl=xp;
 *       9.classname=org.sakaiproject.sdata.tool.xmlrpc.XmlRpcCHSUserStorageHandler;
 *       9.baseurl=xcp;
 *       10.classname=org.sakaiproject.sdata.services.mcp.MyCoursesAndProjectsHandler;
 *       10.baseurl=mcp;
 *       11.classname=org.sakaiproject.sdata.services.mra.MyRecentChangesHandler;
 *       11.baseurl=mra;
 *       12.classname=org.sakaiproject.sdata.services.me.MeHandler;
 *       12.baseurl=me;
 *       13.classname=org.sakaiproject.sdata.services.mff.MyFileFinderHandler;
 *       13.baseurl=mff;
 *       14.classname=org.sakaiproject.sdata.services.motd.MessageOfTheDayHandler;
 *       14.baseurl=motd;
 *       15.classname=org.sakaiproject.sdata.services.mgs.MyGlobalSearchHandler;
 *       15.baseurl=mgs;
 *       16.classname=org.sakaiproject.sdata.services.site.SiteHandler;
 *       16.baseurl=site;
 *       &lt;/param-value&gt;
 *       &lt;/init-param&gt;
 *      
 * </pre>
 * 
 * </p>
 * <p>
 * or as individual properties starting with handler.
 * </p>
 * <p>
 * 
 * <pre>
 *      	 &lt;init-param&gt;
 *                    &lt;param-name&gt;handler.1.classname&lt;/param-name&gt;
 *                    &lt;param-value&gt;org.sakaiproject.sdata.tool.JCRDumper&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 *     	 &lt;init-param&gt;
 *                    &lt;param-name&gt;handler.1.baseurl&lt;/param-name&gt;
 *                    &lt;param-value&gt;d&lt;/param-value&gt;
 *         &lt;/init-param&gt;
 * </pre>
 * 
 * </p>
 * <p>
 * Obviously the former is more compact.
 * </p>
 * <p>
 * When the servlet inits, it will create instances of the classes names in the
 * classname property and then register those against the baseurl property. When
 * processing a request, the path info will be examined and the first element of
 * the path will be used to match a selected handler on baseurl. The handler
 * will then be invoked for the method in question. If no handler is found then
 * a 404 will be sent back to the user.
 * </p>
 * <p>
 * There is an additional url /checkRunning that will respond with some sample
 * random data. This is used for unit testing. The size of the block can be set
 * with a x-testdata-size header in the request. This is limited to 4K maximum.
 * </p>
 * 
 * @author ieb
 */
public class ControllerServlet extends HttpServlet
{

	private static final Log log = LogFactory.getLog(ControllerServlet.class);

	private Map<String, Handler> handlerRegister;

	/**
	 * TODO Javadoc
	 */
	private Handler nullHandler = new /**
										 * @author ieb
										 */
	Handler()
	{

		private Random r = new Random(System.currentTimeMillis());

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.sakaiproject.sdata.tool.api.Handler#doDelete(javax.servlet.http.HttpServletRequest,
		 *      javax.servlet.http.HttpServletResponse)
		 */
		public void doDelete(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException
		{
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
			int size = 1024;
			try
			{
				size = Integer.parseInt(request.getHeader("x-testdata-size"));
			}
			catch (Exception ex)
			{

			}
			size = Math.min(4096, size);
			byte[] b = new byte[size];
			r.nextBytes(b);
			response.setContentType("application/octet-stream");
			response.setContentLength(b.length);
			response.setStatus(HttpServletResponse.SC_OK);
			response.getOutputStream().write(b);
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

		public void sendError(HttpServletRequest request, HttpServletResponse response, Throwable ex) throws IOException
		{
			// TODO Auto-generated method stub
			
		}

		public void sendMap(HttpServletRequest request, HttpServletResponse response, Map<String, Object> contetMap) throws IOException
		{
			// TODO Auto-generated method stub
			
		}

	};

	/**
	 * TODO Javadoc
	 */
	public ControllerServlet()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException
	{
		String handlerName = null;
		try
		{
			super.init(config);
			handlerRegister = new HashMap<String, Handler>();

			String configData = config.getInitParameter("config");
			Map<String, Map<String, String>> configMap = null;
			if (configData != null && configData.trim().length() > 0)
			{
				configMap = loadConfigMap(configData);
			}
			else
			{
				configMap = loadConfigMap(config);
			}
			for (String handler : configMap.keySet())
			{
				handlerName = handler;
				Map<String, String> handlerConfig = configMap.get(handler);
				Class c = Class.forName(handlerConfig.get("classname"));
				Handler h = (Handler) c.newInstance();
				h.init(handlerConfig);
				handlerRegister.put(handlerConfig.get("baseurl"), h);
			}
		}
		catch (ClassNotFoundException e)
		{
			throw new ServletException("Failed to instance handler " + handlerName, e);
		}
		catch (InstantiationException e)
		{
			throw new ServletException("Failed to instance handler " + handlerName, e);
		}
		catch (IllegalAccessException e)
		{
			throw new ServletException("Failed to instance handler " + handlerName, e);
		}
	}

	/**
	 * TODO Javadoc
	 * 
	 * @param config
	 * @return
	 */

	private Map<String, Map<String, String>> loadConfigMap(ServletConfig config)
	{

		Map<String, Map<String, String>> configMap = new HashMap<String, Map<String, String>>();
		for (Enumeration<String> e = config.getInitParameterNames(); e.hasMoreElements();)
		{
			String name = e.nextElement();
			if (name.startsWith("handler."))
			{
				int handlerLength = "handler.".length();
				int endkey = name.indexOf(".", handlerLength + 1);

				String key = name.substring(handlerLength, endkey);
				String valuekey = name.substring(endkey + 1);
				String value = config.getInitParameter(name);
				if (log.isDebugEnabled())
				{
					log.debug("Adding Key[" + key + "] [" + handlerLength + "-" + endkey
							+ "] keyValue[" + valuekey + "] Value[" + value + "]");
				}
				Map<String, String> handlerConfig = configMap.get(key);
				if (handlerConfig == null)
				{
					handlerConfig = new HashMap<String, String>();
					configMap.put(key, handlerConfig);
				}
				handlerConfig.put(valuekey, value);
			}
		}
		return configMap;
	}

	/**
	 * TODO Javadoc
	 * 
	 * @param config
	 * @return
	 */
	private Map<String, Map<String, String>> loadConfigMap(String config)
	{

		Map<String, Map<String, String>> configMap = new HashMap<String, Map<String, String>>();
		String[] pairs = config.trim().split(";");
		for (String pair : pairs)
		{
			String[] nv = pair.trim().split("=", 2);
			String name = nv[0].trim();
			String value = nv[1].trim();
			int endkey = name.indexOf(".");

			String key = name.substring(0, endkey);
			String valuekey = name.substring(endkey + 1);
			if (log.isDebugEnabled())
			{
				log.debug("Config Data Adding Key[" + key + "] [" + endkey
						+ "] keyValue[" + valuekey + "] Value[" + value + "]");
			}
			Map<String, String> handlerConfig = configMap.get(key);
			if (handlerConfig == null)
			{
				handlerConfig = new HashMap<String, String>();
				configMap.put(key, handlerConfig);
			}
			handlerConfig.put(valuekey, value);
		}
		return configMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		Handler h = getHandler(request);
		if (h != null)
		{
			h.setHandlerHeaders(response);
			h.doDelete(request, response);
		}
		else
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No Handler Found");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		Handler h = getHandler(request);
		if (h != null)
		{
			h.setHandlerHeaders(response);
			h.doGet(request, response);
		}
		else
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No Handler Found");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		Handler h = getHandler(request);
		if (h != null)
		{
			h.setHandlerHeaders(response);
			h.doHead(request, response);
		}
		else
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No Handler Found");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		Handler h = getHandler(request);
		if (h != null)
		{
			h.setHandlerHeaders(response);
			h.doPost(request, response);
		}
		else
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No Handler Found");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		Handler h = getHandler(request);
		if (h != null)
		{
			h.setHandlerHeaders(response);
			h.doPut(request, response);
		}
		else
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No Handler Found");
		}
	}

	/**
	 * TODO Javadoc
	 * 
	 * @param request
	 * @return
	 */
	public Handler getHandler(HttpServletRequest request)
	{
		request.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);
		String pathInfo = request.getPathInfo();
		if (log.isDebugEnabled())
		{
			log.debug("Path is " + pathInfo);
		}
		if ("/checkRunning".equals(pathInfo))
		{
			return nullHandler;
		}
		if (pathInfo == null) return null;

		char[] path = request.getPathInfo().trim().toCharArray();
		if (path.length < 1) return null;
		int start = 0;
		if (path[0] == '/')
		{
			start = 1;
		}
		int end = start;
		for (; end < path.length && path[end] != '/'; end++);
		String key = new String(path, start, end - start);
		return handlerRegister.get("/"+key);
	}
	
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		long start = System.currentTimeMillis();
		super.service(req, resp);
		log.info((System.currentTimeMillis()-start)+" ms "+req.getMethod()+":"+req.getRequestURL());
	}

}
