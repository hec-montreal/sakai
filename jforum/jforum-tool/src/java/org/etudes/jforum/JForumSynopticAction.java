/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/JForumSynopticAction.java $ 
 * $Id: JForumSynopticAction.java 68052 2010-06-07 21:46:11Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008, 2009, 2010 Etudes, Inc. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 ***********************************************************************************/
package org.etudes.jforum;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.api.app.jforum.JForumSynopticService;
import org.etudes.jforum.repository.Tpl;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.preferences.TemplateKeys;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;

import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@SuppressWarnings("serial")
public class JForumSynopticAction extends HttpServlet
{
	private static final Log logger = LogFactory.getLog(JForumSynopticAction.class);
	
	@Override
	public void init() throws ServletException
	{
		super.init();
		if (logger.isInfoEnabled())
			logger.info(this.getClass().getName() + ".init()....");
	}
	
	@Override
	public void destroy()
	{
		super.destroy();
		if (logger.isInfoEnabled())
			logger.info(this.getClass().getName() + ".destroy()....");
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String requestType = (request.getMethod()).toUpperCase();
		String pathInfo = request.getPathInfo();
		if ((!("GET").equals(requestType)) || (!pathInfo.endsWith(SystemGlobals.getValue(ConfigKeys.SYNOPTIC_SERVLET_EXTENSION)))
					|| (!pathInfo.startsWith("/newpmrecenttopics")))
		{
			return;
		}
		
		SimpleHash context = new SimpleHash(ObjectWrapper.BEANS_WRAPPER);
		context.put("I18n", I18n.getInstance());
		context.put("templateName", SystemGlobals.getValue(ConfigKeys.TEMPLATE_DIR));
		context.put("JForumContext", new JForumContext(request.getContextPath(),
				SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION), request, response));
		
		Template template = Configuration.getDefaultConfiguration().getTemplate(SystemGlobals.getValue(ConfigKeys.TEMPLATE_DIR)
				+ "/" 
				+ Tpl.name(TemplateKeys.SYNOPTIC_JFORUM_MAIN));
		
		JForumSynopticService jForumSynopticService = null;
		jForumSynopticService = (JForumSynopticService)ComponentManager.get("org.etudes.api.app.jforum.JForumSynopticService");
		if (jForumSynopticService == null) return;
		
		String contextPath = request.getContextPath();
		context.put("contextPath", contextPath);
		
		// PM count
		int newPMcount = 0;
		newPMcount = jForumSynopticService.getUserNewPrivateMessageCount();
		context.put("newPrivateMessageCount", newPMcount);
		
		// new posts
		boolean hasUnreadTopicsReplies = jForumSynopticService.isUserHasUnreadTopicsAndReplies();
		context.put("hasUnreadTopicsReplies", hasUnreadTopicsReplies);
		
		Site currentSite = null;
		try
		{
		  currentSite = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
				  
		}
		catch (Exception e)
		{
			if (logger.isWarnEnabled()) logger.error(this.getClass().getName() + ".service() : " + e.getMessage(), e);
		}
		
		if (currentSite != null)
		{
			StringBuilder siteNavUrl = new StringBuilder();
			
			String portalUrl = ServerConfigurationService.getPortalUrl();
			siteNavUrl.append(portalUrl);
			siteNavUrl.append("/directtool/");
			
			ToolConfiguration jforumToolConfiguration = currentSite.getToolForCommonId(SakaiSystemGlobals.getValue(ConfigKeys.JFORUM_TOOL_ID));
			
			if (jforumToolConfiguration != null)
			{
				siteNavUrl.append(jforumToolConfiguration.getId());
				context.put("pmUrl", siteNavUrl +"/pm/inbox"+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
				context.put("newTopicsReplies", I18n.getSynopticMessage("Synoptic.newTopicsReplies", new Object[] {siteNavUrl + "/recentTopics/list"+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION)}));
			}
		}
		String encoding = SakaiSystemGlobals.getValue(ConfigKeys.ENCODING);
		String contentType = "text/html; charset=" + encoding;
		response.setContentType(contentType);		
		Writer out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), encoding));
		try
		{
			template.process(context, out);
		}
		catch (TemplateException e)
		{
			if (logger.isWarnEnabled()) 
				logger.warn(this.getClass().getName() +".service(HttpServletRequest, HttpServletResponse) : "+ e.getMessage(), e);
		}
	}

}
