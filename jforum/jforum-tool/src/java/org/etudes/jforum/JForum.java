/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/JForum.java $ 
 * $Id: JForum.java 62519 2009-08-11 18:21:38Z murthy@etudes.org $ 
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
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import net.jforum.repository.SecurityRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.exceptions.ExceptionWriter;
import org.etudes.jforum.exceptions.ForumStartupException;
import org.etudes.jforum.repository.ModulesRepository;
import org.etudes.jforum.repository.RankingRepository;
import org.etudes.jforum.repository.SmiliesRepository;
import org.etudes.jforum.util.MD5;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;

import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 * Front Controller.
 * 
 * @author Rafael Steil
 * 8/4/05 - Mallika - Adding courseId assocation
 * 8/30/05 - Rashmi - removed extra import refrence- portalservice 
 */
public class JForum extends JForumBaseServlet 
{
	private static boolean isDatabaseUp;
	private static final Log logger = LogFactory.getLog(JForum.class);
	
	/**
	 * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		if (logger.isDebugEnabled()) logger.debug("INIT OF JFORUM RUNNING!!!");
		// Start database
		isDatabaseUp = ForumStartup.startDatabase();
		
		// Configure ThreadLocal
		DataHolder dh = new DataHolder();
		Connection conn;
		
		try {
			conn = DBConnection.getImplementation().getConnection();
		}
		catch (Exception e) {
			throw new ForumStartupException("Error while starting jforum", e);
		}
		
		dh.setConnection(conn);
		JForum.setThreadLocalData(dh);

			
		// Init general forum stuff
		//Mallika's comments beg
	    //ForumStartup.startForumRepository();
		//Mallika's comments end
		
		//Mallika's code to start forum repository with course beg
		//12/07/2006 Murthy - Commented 
		//ForumStartup.startCourseForumRepository();
		//Mallika's code end
			
		try
		{
			RankingRepository.loadRanks();
			SmiliesRepository.loadSmilies();
		}
		catch (Exception e)
		{
			if (logger.isErrorEnabled()) logger.error("Error while loading ranks and smilies: " + e, new Throwable(e));
		}
		
		// Finalize
		if (conn != null) {
			try {
				DBConnection.getImplementation().releaseConnection(conn);
			}
			catch (Exception e) {
				logger.error("Can not release DB connection: " + e);
			}
		}
		
		JForum.setThreadLocalData(null);
	}
	
	/**
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void service(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException
	{
		if(logger.isDebugEnabled()) logger.debug("JForum request cycle starting for thread " + Thread.currentThread().hashCode());

		Writer out = null;
		try {
			// Initializes thread local data
			DataHolder dataHolder = new DataHolder();
			localData.set(dataHolder);

			// String encoding = SystemGlobals.getValue(ConfigKeys.ENCODING);
			String encoding = SakaiSystemGlobals.getValue(ConfigKeys.ENCODING);

			// Request
			ActionServletRequest request = new ActionServletRequest(req);

			dataHolder.setResponse(response);
			dataHolder.setRequest(request);
			
			if (!isDatabaseUp) {
				ForumStartup.startDatabase();
			}
			
			// Why is this here again?  --JMH
			localData.set(dataHolder);
			
			// Setup stuff
			SimpleHash context = JForum.getContext();
			
			ControllerUtils utils = new ControllerUtils();
			utils.refreshSession();
			utils.prepareTemplateContext(context);
			
			boolean logged = "1".equals(SessionFacade.getAttribute("logged"));
			
			context.put("logged", logged);
			
			// Process security data
			/*if (logger.isDebugEnabled()) logger.debug("Processing security");
			//SecurityRepository.load(SessionFacade.getUserSession().getUserId());
			if (logger.isDebugEnabled()) logger.debug("Processed security");*/
		

			String module = request.getModule();
			
			// Gets the module class name
			if (logger.isDebugEnabled()) logger.debug("Getting module class for " + module);
			String moduleClass = ModulesRepository.getModuleClass(module);
			if (logger.isDebugEnabled()) logger.debug("Got module class " + moduleClass);
			
			context.put("moduleName", module);
			context.put("action", request.getAction());
			if (logger.isDebugEnabled()) logger.debug("Module is "+module);
			
			context.put("securityHash", MD5.crypt(request.getSession().getId()));
			context.put("session", SessionFacade.getUserSession());
		
			if (moduleClass != null) {
				// Here we go, baby
				if(logger.isDebugEnabled()) logger.debug("Entering command processing");
				Command c = (Command)Class.forName(moduleClass).newInstance();
				Template template = c.process(request, response, context);
				if(logger.isDebugEnabled()) logger.debug("Exiting command processing");

				DataHolder dh = (DataHolder)localData.get();
				
				if (dh.getRedirectTo() == null) {
					String contentType = dh.getContentType();
					
					if (contentType == null) {
						contentType = "text/html; charset=" + encoding;
					}
					
					response.setContentType(contentType);
					
					// Binary content are expected to be fully 
					// handled in the action, including outputstream
					// manipulation
					if (!dh.isBinaryContent()) {
						if(logger.isDebugEnabled()) logger.debug("Getting output stream");
						out = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), encoding));
						if(logger.isDebugEnabled()) logger.debug("Output stream ready for writing");
						
						if(logger.isDebugEnabled()) logger.debug("Entering template processing");
						template.process(JForum.getContext(), out);
						if(logger.isDebugEnabled()) logger.debug("Exiting template processing");

						if(logger.isDebugEnabled()) logger.debug("Flushing output stream");
						out.flush();
						if(logger.isDebugEnabled()) logger.debug("Output stream flushed");
					}
				}
			}
			else {
				logger.error("Cannot find the module class name for "
						+ "[module=" + module + ", "
						+ "action=" + request.getAction() + "]");
			}
		}
		catch (Exception e) {
			logger.error("an error occured in JForum.service(): " + e);
			e.printStackTrace();
			
			JForum.enableCancelCommit();
			
			if (e.toString().indexOf("ClientAbortException") == -1) {
				response.setContentType("text/html");
				if (out != null) {
					new ExceptionWriter().handleExceptionData(e, out);
				}
				else {
					new ExceptionWriter().handleExceptionData(e, new BufferedWriter(new OutputStreamWriter(response.getOutputStream())));
				}
			}
		}
		finally {
			this.releaseConnection();
			
			DataHolder dh = (DataHolder)localData.get();
			
			if (dh != null) {
				String redirectTo = dh.getRedirectTo();
				
				if (redirectTo != null) {
					response.sendRedirect(redirectTo);
				}
			}
			
			localData.set(null);

			if(logger.isDebugEnabled()) logger.debug("JForum request cycle finished for thread "  + Thread.currentThread().hashCode());
		}		
	}
	
	private void releaseConnection()
	{
		Connection conn = JForum.getConnection(false);
		
		if (conn != null) {
			if (JForum.cancelCommit()) {
				try {
					conn.rollback();
				}
				catch (Exception e) {
					logger.error("Error while rolling back a transaction", e);
				}
			}
			else {
				try {
					conn.commit();
				}
				catch (Exception e) {
					logger.error("Error while commiting a transaction", e);
				}
			}
				
			DBConnection.getImplementation().releaseConnection(conn);
		}
	}
	
	/** 
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	public void destroy() {
		super.destroy();
		if (logger.isInfoEnabled()) logger.info("Destroying JForum...");
		
		try {
			DBConnection.getImplementation().realReleaseAllConnections();
		}
		catch (Exception e) {}
	}
}
