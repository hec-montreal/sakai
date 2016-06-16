/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/admin/AdminCommand.java $ 
 * $Id: AdminCommand.java 70688 2010-10-13 18:54:22Z murthy@etudes.org $ 
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
package org.etudes.jforum.view.admin;


import javax.servlet.http.HttpServletResponse;

import org.etudes.jforum.ActionServletRequest;
import org.etudes.jforum.Command;
import org.etudes.jforum.JForum;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;

import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 * @author Rafael Steil
 */
public abstract class AdminCommand extends Command
{
	public Template process(ActionServletRequest request, HttpServletResponse response, 
			SimpleHash context) throws Exception
	{
		//new AdminAction().checkAdmin();
		return super.process(request, response, context);
	}
	
	protected void autoSaveNavigation() throws Exception
	{
		String autoSaveNav = this.request.getParameter("autosavenav");
		
		if (autoSaveNav != null)
		{
			if (autoSaveNav.trim().equalsIgnoreCase("forums"))
			{
				String path = this.request.getContextPath() +"/adminForums/list"+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION);;
				JForum.setRedirect(path);
			}
			else if (autoSaveNav.trim().equalsIgnoreCase("categories"))
			{
				String path = this.request.getContextPath() +"/adminCategories/list"+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION);;
				JForum.setRedirect(path);
			}
			else if (autoSaveNav.trim().equalsIgnoreCase("discussionlist"))
			{
				String path = this.request.getContextPath() +"/forums/list"+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION);;
				JForum.setRedirect(path);
			}
			else if (autoSaveNav.trim().equalsIgnoreCase("importexport"))
			{
				String path = this.request.getContextPath() +"/adminImportExport/list"+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION);;
				JForum.setRedirect(path);
			}
			else
			{
				this.list();
			}
		}
		else
		{
			this.list();
		}
	}
}
