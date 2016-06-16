/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/Command.java $ 
 * $Id: Command.java 55370 2008-11-26 21:57:23Z murthy@etudes.org $ 
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


import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.exceptions.TemplateNotFoundException;
import org.etudes.jforum.repository.Tpl;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.preferences.TemplateKeys;

import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 * <code>Command</code> Pattern implementation.
 * All View Helper classes, which are intead to configure and processs
 * presentation actions must extend this class. 
 * 
 * @author Rafael Steil
 */
public abstract class Command 
{
	private static final Log logger = LogFactory.getLog(Command.class);
	
	private boolean ignoreAction;
	protected String templateName;
	protected ActionServletRequest request;
	protected HttpServletResponse response;
	protected SimpleHash context;
	
	protected void setTemplateName(String templateName)
	{
		this.templateName = Tpl.name(templateName);
	}
	
	protected void ignoreAction()
	{
		this.ignoreAction = true;
	}
	
	/**
	 * Base method for listings. 
	 * May be used as general listing or as helper
	 * to another specialized type of listing. Subclasses
	 * must implement it to the cases where some invalid
	 * action is called ( which means that the exception will
	 * be caught and the general listing will be used )
	 * 
	 * @throws Exception  
	 */
	public abstract void list() throws Exception;
	
	/**
	 * Process and manipulate a requisition.
	 * @param context TODO
	 * @throws Exception
	 * @return <code>Template</code> reference
	 */
	public Template process(ActionServletRequest request, 
			HttpServletResponse response,
			SimpleHash context) throws Exception 
	{
		this.request = request;
		this.response = response;
		this.context = context;
		
		String action = this.request.getAction();
		
		if(logger.isDebugEnabled()) logger.debug("Action = " + action);

		if (!this.ignoreAction) {
			try {
				Object obj[] = null;
				Class clazz[] = null;
				if(logger.isDebugEnabled()) logger.debug("Running command on " + this.getClass().getName() + " with method " + action);
				//Class.forName(this.getClass().getName()).getMethod(action, null).invoke(this, null);			
				Class.forName(this.getClass().getName()).getMethod(action, clazz).invoke(this, obj);			
			}
			catch (NoSuchMethodException e) {		
				this.list();		
			}
			catch (Exception e) {
				throw e;
			}
		}
		
		if (JForum.getRedirect() != null) {
			this.setTemplateName(TemplateKeys.EMPTY);
		}
		else if (request.getAttribute("template") != null) {
			this.setTemplateName((String)request.getAttribute("template"));
		}
		
		if (JForum.isBinaryContent()) {
			return null;
		}
		
		if (this.templateName == null) {
			throw new TemplateNotFoundException("Template for action " + action + " is not defined");
		}
		
		return Configuration.getDefaultConfiguration().getTemplate(SystemGlobals.getValue(ConfigKeys.TEMPLATE_DIR)
				+ "/" 
				+ this.templateName);
	}
}
