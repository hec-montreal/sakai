/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/mail/PrivateMessageSpammer.java $ 
 * $Id: PrivateMessageSpammer.java 64033 2009-10-15 21:42:02Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008, 2009 Etudes, Inc. 
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
package org.etudes.jforum.util.mail;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.entities.PrivateMessage;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.ToolConfiguration;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.util.Web;

import freemarker.template.SimpleHash;

/**
 * @author Rafael Steil
 * 10/3/05 - Mallika - adding course information to email instead of link
 */
public class PrivateMessageSpammer extends Spammer
{
	private static Log logger = LogFactory.getLog(PrivateMessageSpammer.class);
	public PrivateMessageSpammer(User user)
	{
		if (user.getEmail() == null || user.getEmail().trim().equals("")) {
			return;
		}
		
		//Mallika-comments beg
		/*String forumLink = SystemGlobals.getValue(ConfigKeys.FORUM_LINK);
		if (!forumLink.endsWith("/")) {
			forumLink += "/";
		}
		
		forumLink += "pm/inbox" + SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION);*/
		//Mallika - comments end
		
		SimpleHash params = new SimpleHash();
		if (logger.isDebugEnabled()) logger.debug("PrivateMessageSpammer- this is the CODE!");
		//Mallika - new code beg
		String forumLink = null;
		Site currentSite = null;
		try
		{
		  currentSite = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
		  forumLink = currentSite.getTitle();
				  
		}
		catch (Exception e)
		{
			logger.error(this.getClass().getName() + 
					".PrivateMessageSpammer() : " + e.getMessage(), e);
		}
		//Mallika - new code end
		
		params.put("path", forumLink);
		params.put("user", user);
		String portalUrl = ServerConfigurationService.getPortalUrl();
		String currToolId = ToolManager.getCurrentPlacement().getId();
		ToolConfiguration toolConfiguration = currentSite.getTool(currToolId);
		
		String siteNavUrl = portalUrl + "/"+ "site" + "/"+ Web.escapeUrl(currentSite.getId());
		
		if (toolConfiguration != null)
			siteNavUrl = siteNavUrl + "/" + "page" + "/"+ toolConfiguration.getPageId();
		
		params.put("serverurl", siteNavUrl);
		
		List recipients = new ArrayList();
		
		try
		{
			new InternetAddress(user.getEmail());
		} 
		catch (AddressException e)
		{
			if (logger.isWarnEnabled()) logger.warn("PrivateMessageSpammer(...) : "+ user.getEmail() + " is invalid. And exception is : "+ e);
			return;
		}
		
		recipients.add(user.getEmail());
				
        //Mallika-code below changed to add course id
		super.prepareMessage(recipients, params, 
				"[" + forumLink + "] " + SystemGlobals.getValue(ConfigKeys.MAIL_NEW_PM_SUBJECT),
				SystemGlobals.getValue(ConfigKeys.MAIL_NEW_PM_MESSAGE_FILE));
	}
	
	public PrivateMessageSpammer(User user, PrivateMessage pm)
	{
		if (user.getEmail() == null || user.getEmail().trim().equals("")) {
			return;
		}
		
		//Mallika-comments beg
		/*String forumLink = SystemGlobals.getValue(ConfigKeys.FORUM_LINK);
		if (!forumLink.endsWith("/")) {
			forumLink += "/";
		}
		
		forumLink += "pm/inbox" + SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION);*/
		//Mallika - comments end
		
		SimpleHash params = new SimpleHash();
		if (logger.isDebugEnabled()) logger.debug("PrivateMessageSpammer- this is the CODE!");
		//Mallika - new code beg
		String forumLink = null;
		Site currentSite = null;
		try
		{
		  currentSite = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
		  forumLink = currentSite.getTitle();
				  
		}
		catch (Exception e)
		{
			logger.error(this.getClass().getName() + 
					".PrivateMessageSpammer() : " + e.getMessage(), e);
		}
		//Mallika - new code end
		
		params.put("path", forumLink);
		params.put("user", user);
		
		StringBuilder siteNavUrl = new StringBuilder();
		
		String portalUrl = ServerConfigurationService.getPortalUrl();
		siteNavUrl.append(portalUrl);
		
		String currToolId = ToolManager.getCurrentPlacement().getId();
		ToolConfiguration toolConfiguration = currentSite.getTool(currToolId);
		
		siteNavUrl.append("/");
		siteNavUrl.append("site");
		siteNavUrl.append("/");
		siteNavUrl.append(Web.escapeUrl(currentSite.getId()));
		
		if (toolConfiguration != null)
		{
			siteNavUrl.append("/"); 
			siteNavUrl.append("page");
			siteNavUrl.append("/");
			siteNavUrl.append(Web.escapeUrl(toolConfiguration.getPageId()));
		}
		
		params.put("serverurl", siteNavUrl.toString());
		
		String pmFrom = pm.getFromUser().getFirstName() +" "+ pm.getFromUser().getLastName();
		
		params.put("pmfrom", pmFrom);
		
		params.put("pmsubject", pm.getPost().getSubject());
		
		params.put("pmtext", pm.getPost().getText());
		
		List recipients = new ArrayList();
		
		try
		{
			new InternetAddress(user.getEmail());
		} 
		catch (AddressException e)
		{
			if (logger.isWarnEnabled()) logger.warn("PrivateMessageSpammer(...) : "+ user.getEmail() + " is invalid. And exception is : "+ e);
			return;
		}
		
		recipients.add(user.getEmail());
				
        //Mallika-code below changed to add course id
		super.prepareMessage(recipients, params, 
				"[" + forumLink + "] " + SystemGlobals.getValue(ConfigKeys.MAIL_NEW_PM_SUBJECT),
				SystemGlobals.getValue(ConfigKeys.MAIL_NEW_PM_MESSAGE_FILE));
	}
	
	public PrivateMessageSpammer(User user, PrivateMessage pm, List attachments)
	{
		if (user.getEmail() == null || user.getEmail().trim().equals("")) {
			return;
		}
		
		SimpleHash params = new SimpleHash();
		if (logger.isDebugEnabled()) logger.debug("PrivateMessageSpammer with attachments .....");

		String forumLink = null;
		Site currentSite = null;
		try
		{
		  currentSite = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
		  forumLink = currentSite.getTitle();
				  
		}
		catch (Exception e)
		{
			logger.error(this.getClass().getName() + 
					".PrivateMessageSpammer() : " + e.getMessage(), e);
		}
		
		params.put("path", forumLink);
		params.put("user", user);
		
		StringBuilder siteNavUrl = new StringBuilder();
		
		String portalUrl = ServerConfigurationService.getPortalUrl();
		siteNavUrl.append(portalUrl);
		
		String currToolId = ToolManager.getCurrentPlacement().getId();
		ToolConfiguration toolConfiguration = currentSite.getTool(currToolId);
		
		siteNavUrl.append("/");
		siteNavUrl.append("site");
		siteNavUrl.append("/");
		siteNavUrl.append(Web.escapeUrl(currentSite.getId()));
		
		if (toolConfiguration != null)
		{
			siteNavUrl.append("/"); 
			siteNavUrl.append("page");
			siteNavUrl.append("/");
			siteNavUrl.append(Web.escapeUrl(toolConfiguration.getPageId()));
		}
		
		params.put("serverurl", siteNavUrl.toString());
		
		String pmFrom = pm.getFromUser().getFirstName() +" "+ pm.getFromUser().getLastName();
		
		params.put("pmfrom", pmFrom);
		
		params.put("pmsubject", pm.getPost().getSubject());
		
		params.put("pmtext", pm.getPost().getText());
		
		List recipients = new ArrayList();
		
		try
		{
			new InternetAddress(user.getEmail());
		} 
		catch (AddressException e)
		{
			if (logger.isWarnEnabled()) logger.warn("PrivateMessageSpammer with attachments(...) : "+ user.getEmail() + " is invalid. And exception is : "+ e);
			return;
		}
		
		recipients.add(user.getEmail());
				
		if (attachments != null && attachments.size() > 0)
		{
			super.prepareAttachmentMessage(recipients, params, 
					"[" + forumLink + "] " + SystemGlobals.getValue(ConfigKeys.MAIL_NEW_PM_SUBJECT),
					SystemGlobals.getValue(ConfigKeys.MAIL_NEW_PM_MESSAGE_FILE), attachments);
		}
		else
		{
			super.prepareMessage(recipients, params, 
					"[" + forumLink + "] " + SystemGlobals.getValue(ConfigKeys.MAIL_NEW_PM_SUBJECT),
					SystemGlobals.getValue(ConfigKeys.MAIL_NEW_PM_MESSAGE_FILE));
		}
	}
}
