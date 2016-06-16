/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/forum/common/ViewCommon.java $ 
 * $Id: ViewCommon.java 55473 2008-12-01 18:49:13Z murthy@etudes.org $ 
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
package org.etudes.jforum.view.forum.common;

import java.util.Map;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.Command;
import org.etudes.jforum.JForum;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.exceptions.RequestEmptyException;
import org.etudes.jforum.repository.ModulesRepository;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.preferences.TemplateKeys;

import freemarker.template.SimpleHash;

/**
 * @author Rafael Steil
 */
public final class ViewCommon
{
	private static final Log logger = LogFactory.getLog(ViewCommon.class);
	
	/**
	 * Prepared the user context to use data pagination. 
	 * The following variables are set to the context:
	 * <p>
	 * 	<ul>
	 * 		<li> <i>totalPages</i> - total number of pages
	 * 		<li> <i>recordsPerPage</i> - how many records will be shown on each page
	 * 		<li> <i>totalRecords</i> - number of records fount
	 * 		<li> <i>thisPage</i> - the current page being shown
	 * 		<li> <i>start</i> - 
	 * 	</ul>
	 * </p>
	 * @param start
	 * @param totalRecords
	 * @param recordsPerPage
	 */
	public static void contextToPagination(int start, int totalRecords, int recordsPerPage)
	{
		SimpleHash context = JForum.getContext();
		
		context.put("totalPages", new Double(Math.ceil((double) totalRecords / (double) recordsPerPage)));
		context.put("recordsPerPage", new Integer(recordsPerPage));
		context.put("totalRecords", new Integer(totalRecords));
		context.put("thisPage", new Double(Math.ceil((double) (start + 1) / (double) recordsPerPage)));
		context.put("start", new Integer(start));
	}
	
	public static String contextToLogin() 
	{
		String uri = JForum.getRequest().getRequestURI();
		String query = JForum.getRequest().getQueryString();
		String path = query == null ? uri : uri + "?" + query;
		
		JForum.getContext().put("returnPath", path);
		
		if (ConfigKeys.TYPE_SSO.equals(SystemGlobals.getValue(ConfigKeys.AUTHENTICATION_TYPE))) {
			String redirect = SystemGlobals.getValue(ConfigKeys.SSO_REDIRECT);
			
			if (redirect != null && redirect.trim().length() > 0) {
				JForum.setRedirect(JForum.getRequest().getContextPath() + redirect.trim() + path);
			}
		}
		
		return TemplateKeys.USER_LOGIN;
	}
	
	/**
	 * Returns the initial page to start fetching records from.
	 *   
	 * @return The initial page number
	 */
	public static int getStartPage()
	{
		String s = JForum.getRequest().getParameter("start");
		int start = 0;
		
		if (s == null || s.trim().equals("")) {
			start = 0;
		}
		else {
			start = Integer.parseInt(s);
			
			if (start < 0) {
				start = 0;
			}
		}
		
		return start;
	}
	
	/**
	 * Gets the forum base link.
	 * The returned link has a trailing slash
	 * @return The forum link, with the trailing slash
	 */
	public static String getForumLink()
	{
		String forumLink = SystemGlobals.getValue(ConfigKeys.FORUM_LINK);
		if (!forumLink.endsWith("/")) {
			forumLink += "/";
		}
		
		return forumLink;
	}

	/**
	 * Checks if some request needs to be reprocessed. 
	 * This is likely to happen when @link net.jforum.ActionServletRequest#dumpRequest()
	 * is stored in the session. 
	 * 
	 * @return <code>true</code> of <code>false</code>, depending of the status.
	 */
	public static boolean needReprocessRequest()
	{
		return (SessionFacade.getAttribute(ConfigKeys.REQUEST_DUMP) != null);
	}
	
	/**
	 * Reprocess a request. 
	 * The request data should be in the session, held by the key
	 * <code>ConfigKeys.REQUEST_DUMP</code> and the value as
	 * a <code>java.util.Map</code>. Usual behaviour is to have the return
	 * of @link net.jforum.ActionServletRequest#dumpRequest().
	 * @throws Exception, RequestEmptyException
	 */
	public static void reprocessRequest() throws Exception
	{
		Map data = (Map)SessionFacade.getAttribute(ConfigKeys.REQUEST_DUMP);
		if (data == null) {
			throw new RequestEmptyException("A call to ViewCommon#reprocessRequest() was made, but no data found");
		}
		
		String module = (String)data.get("module");
		String action = (String)data.get("action");
		
		if (module == null || action == null) {
			throw new RequestEmptyException("A call to ViewCommon#reprocessRequest() was made, "
					+ "but no module or action name was found");
		}
		
		JForum.getRequest().restoreDump(data);
		SessionFacade.removeAttribute(ConfigKeys.REQUEST_DUMP);
		
		String moduleClass = ModulesRepository.getModuleClass(module);
		((Command)Class.forName(moduleClass).newInstance()).process(JForum.getRequest(),
				JForum.getResponse(), JForum.getContext());
	}
}
