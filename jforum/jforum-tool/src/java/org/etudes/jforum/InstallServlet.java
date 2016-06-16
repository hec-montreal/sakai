/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/InstallServlet.java $ 
 * $Id: InstallServlet.java 61928 2009-07-21 22:29:16Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008 Etudes, Inc. 
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
 * 
 * Portions completed before July 1, 2004 Copyright (c) 2003, 2004 Rafael Steil, All rights reserved, licensed under the BSD license. 
 * http://www.opensource.org/licenses/bsd-license.php 
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met: 
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following disclaimer. 
 * 2) Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution. 
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE 
 ***********************************************************************************/
package org.etudes.jforum;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.etudes.jforum.exceptions.ExceptionWriter;
import org.etudes.jforum.repository.ModulesRepository;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;

import freemarker.template.Template;

/**
 * @author Rafael Steil
 */
public class InstallServlet extends JForumBaseServlet
{
	/** 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
	}
	
	/** 
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	public void service(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException
	{
		DataHolder dataHolder = new DataHolder();
		localData.set(dataHolder);
		
		// String encoding = SystemGlobals.getValue(ConfigKeys.ENCODING);
		String encoding = SakaiSystemGlobals.getValue(ConfigKeys.ENCODING);
		req.setCharacterEncoding(encoding);
		
		// Context
		InstallServlet.getContext().put("contextPath", req.getContextPath());
		InstallServlet.getContext().put("serverName", req.getServerName());
		InstallServlet.getContext().put("templateName", "default");
		InstallServlet.getContext().put("serverPort", Integer.toString(req.getServerPort()));
		InstallServlet.getContext().put("I18n", I18n.getInstance());
		InstallServlet.getContext().put("encoding", encoding);
		InstallServlet.getContext().put("extension", SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
		
		// Request
		ActionServletRequest request = new ActionServletRequest(req);
		request.setCharacterEncoding(encoding);

		dataHolder.setResponse(response);
		dataHolder.setRequest(request);

		// Assigns the information to user's thread 
		localData.set(dataHolder);
		
		if (SystemGlobals.getBoolValue(ConfigKeys.INSTALLED)) {
			InstallServlet.setRedirect(InstallServlet.getRequest().getContextPath() 
					+ "/forums/list.page");
		}
		else {		
			// Module and Action
			String moduleClass = ModulesRepository.getModuleClass(request.getModule());
			
			InstallServlet.getContext().put("moduleName", request.getModule());
			InstallServlet.getContext().put("action", InstallServlet.getRequest().getAction());
			
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), encoding));
			
			try {
				if (moduleClass != null) {
					// Here we go, baby
					Command c = (Command)Class.forName(moduleClass).newInstance();
					Template template = c.process(request, response, InstallServlet.getContext());
		
					if (((DataHolder)localData.get()).getRedirectTo() == null) {
						response.setContentType("text/html; charset=" + encoding);
		
						template.process(InstallServlet.getContext(), out);
						out.flush();
					}
				}
			}
			catch (Exception e) {
				response.setContentType("text/html");
				if (out != null) {
					new ExceptionWriter().handleExceptionData(e, out);
				}
				else {
					new ExceptionWriter().handleExceptionData(e, new BufferedWriter(new OutputStreamWriter(response.getOutputStream())));
				}
			}
		}
		
		String redirectTo = ((DataHolder)localData.get()).getRedirectTo();
		if (redirectTo != null) {
			InstallServlet.getResponse().sendRedirect(redirectTo);
		}
		
		localData.set(null);
	}
}
