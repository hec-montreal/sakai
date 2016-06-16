/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/mail/TopicSpammer.java $ 
 * $Id: TopicSpammer.java 64745 2009-11-17 22:37:52Z murthy@etudes.org $ 
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.entities.Post;
import org.etudes.jforum.entities.Topic;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
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
 * 10/5/05 - Mallika - adding course information to email instead of link
 * 10/5/05 - Murthy  - updated constructor for message file
 */
public class TopicSpammer extends Spammer 
{
	private static Log logger = LogFactory.getLog(TopicSpammer.class);
	public TopicSpammer(Topic topic, List users, String messageFile, Post post)
	{
		// Prepare the users. In this current version, the email
		// is not personalized, so then we'll just use his address
		List recipients = new ArrayList();
		for (Iterator iter = users.iterator(); iter.hasNext(); ) {
			User u = (User)iter.next();
			try
			{
				new InternetAddress(u.getEmail());
			} 
			catch (AddressException e)
			{
				if (logger.isWarnEnabled()) logger.warn("TopicSpammer(...) : "+ u.getEmail() + " is invalid. And exception is : "+ e);
				continue;
			}
			recipients.add(u.getEmail());
		}
		
		// Make the topic url
		String page = "";
		// int postsPerPage = SystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		int postsPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		if (topic.getTotalReplies() > postsPerPage) {
			page += (((topic.getTotalReplies() / postsPerPage)) * postsPerPage) + "/";
		}
		
        //Mallika-comments beg
		/*String forumLink = SystemGlobals.getValue(ConfigKeys.FORUM_LINK);
		if (!forumLink.endsWith("/")) {
			forumLink += "/";
		}

		String path = forumLink + "posts/list/" + page + topic.getId() 
			+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) + "#" + topic.getLastPostId();
		
		String unwatch = forumLink + "posts/unwatch/" + topic.getId()
			+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION);
		*/
		//Mallika-comments end
		
		SimpleHash params = new SimpleHash();
		if (logger.isDebugEnabled()) logger.debug("TopicSpammer- this is the CODE!");
        //Mallika - new code beg
		String path = null;
		Site currentSite = null;
		try
		{
		  currentSite = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
		  path = currentSite.getTitle();
				  
		}
		catch (Exception e)
		{
			//System.out.println("Exception thrown "+e.toString());
			logger.error(this.getClass().getName() + 
					".TopicSpammer() : " + e.getMessage(), e);
		}
		//Mallika - new code end
		
		params.put("topic", topic);
		params.put("post", post);
		params.put("path", path);
		
		String postfrom = "";
		if (post.getFirstName() != null)
		{
			postfrom = post.getFirstName();		
		}
		
		if (post.getLastName() != null)
		{
			postfrom = postfrom +" "+ post.getLastName();
		}
		params.put("postfrom", postfrom);

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
		params.put("serverurl", siteNavUrl);
		
		//Mallika-comments beg
		/*params.put("forumLink", forumLink);
		params.put("unwatch", unwatch);*/
		//Mallika-comments end
		
		//Mallika-code below changed to add course id
		Object obj[] = new String[] { topic.getTitle()};
		/*super.prepareMessage(recipients, params,
				"[" + path + "] " + MessageFormat.format(SystemGlobals.getValue(ConfigKeys.MAIL_NEW_ANSWER_SUBJECT), new String[] { topic.getTitle() }),
			SystemGlobals.getValue(ConfigKeys.MAIL_NEW_ANSWER_MESSAGE_FILE));*/
		/*super.prepareMessage(recipients, params,
				"[" + path + "] " + MessageFormat.format(SystemGlobals.getValue(ConfigKeys.MAIL_NEW_ANSWER_SUBJECT), obj),
			SystemGlobals.getValue(ConfigKeys.MAIL_NEW_ANSWER_MESSAGE_FILE));*/
		super.prepareMessage(recipients, params,
				"[" + path + "] " + MessageFormat.format(SystemGlobals.getValue(ConfigKeys.MAIL_NEW_ANSWER_SUBJECT), obj),
			SystemGlobals.getValue(messageFile));
	}
	
	public TopicSpammer(Topic topic, List users, String messageFile, Post post, List attachments)
	{
		// Prepare the users. In this current version, the email
		// is not personalized, so then we'll just use his address
		List recipients = new ArrayList();
		for (Iterator iter = users.iterator(); iter.hasNext(); ) {
			User u = (User)iter.next();
			try
			{
				new InternetAddress(u.getEmail());
			} 
			catch (AddressException e)
			{
				if (logger.isWarnEnabled()) logger.warn("TopicSpammer(...) : "+ u.getEmail() + " is invalid. And exception is : "+ e);
				continue;
			}
			recipients.add(u.getEmail());
		}
		
		// Make the topic url
		String page = "";
		// int postsPerPage = SystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		int postsPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		if (topic.getTotalReplies() > postsPerPage) {
			page += (((topic.getTotalReplies() / postsPerPage)) * postsPerPage) + "/";
		}

		SimpleHash params = new SimpleHash();
		if (logger.isDebugEnabled()) logger.debug("TopicSpammer- this is the CODE!");

		String path = null;
		Site currentSite = null;
		try
		{
		  currentSite = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
		  path = currentSite.getTitle();
				  
		}
		catch (Exception e)
		{
			logger.error(this.getClass().getName() + 
					".TopicSpammer() : " + e.getMessage(), e);
		}
		
		params.put("topic", topic);
		params.put("post", post);
		params.put("path", path);
		
		String postfrom = "";
		if (post.getFirstName() != null)
		{
			postfrom = post.getFirstName();		
		}
		
		if (post.getLastName() != null)
		{
			postfrom = postfrom +" "+ post.getLastName();
		}
		params.put("postfrom", postfrom);

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
		params.put("serverurl", siteNavUrl);
		
		Object obj[] = new String[] { topic.getTitle()};
		
		if (attachments != null && attachments.size() > 0)
		{
			super.prepareAttachmentMessage(recipients, params,
					"[" + path + "] " + MessageFormat.format(SystemGlobals.getValue(ConfigKeys.MAIL_NEW_ANSWER_SUBJECT), obj),
				SystemGlobals.getValue(messageFile), attachments);
		}
		else
		{
			super.prepareMessage(recipients, params,
					"[" + path + "] " + MessageFormat.format(SystemGlobals.getValue(ConfigKeys.MAIL_NEW_ANSWER_SUBJECT), obj),
				SystemGlobals.getValue(messageFile));
		}
	}
	
	public TopicSpammer(Topic topic, Post post, List users, String messageFile)
	{
		// Prepare the users. In this current version, the email
		// is not personalized, so then we'll just use his address
		List recipients = new ArrayList();
		for (Iterator iter = users.iterator(); iter.hasNext(); ) {
			User u = (User)iter.next();
			try
			{
				new InternetAddress(u.getEmail());
			} 
			catch (AddressException e)
			{
				if (logger.isWarnEnabled()) logger.warn("TopicSpammer(...) : "+ u.getEmail() + " is invalid. And exception is : "+ e);
				continue;
			}
			recipients.add(u.getEmail());
		}
		
		// Make the topic url
		String page = "";
		// int postsPerPage = SystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		int postsPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		if (topic.getTotalReplies() > postsPerPage) {
			page += (((topic.getTotalReplies() / postsPerPage)) * postsPerPage) + "/";
		}
		
        //Mallika-comments beg
		/*String forumLink = SystemGlobals.getValue(ConfigKeys.FORUM_LINK);
		if (!forumLink.endsWith("/")) {
			forumLink += "/";
		}

		String path = forumLink + "posts/list/" + page + topic.getId() 
			+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION) + "#" + topic.getLastPostId();
		
		String unwatch = forumLink + "posts/unwatch/" + topic.getId()
			+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION);
		*/
		//Mallika-comments end
		
		SimpleHash params = new SimpleHash();
		if (logger.isDebugEnabled()) logger.debug("TopicSpammer- this is the CODE!");
        //Mallika - new code beg
		String path = null;
		Site currentSite = null;
		try
		{
		  currentSite = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
		  path = currentSite.getTitle();
				  
		}
		catch (Exception e)
		{
			//System.out.println("Exception thrown "+e.toString());
			logger.error(this.getClass().getName() + 
					".TopicSpammer() : " + e.getMessage(), e);
		}
		//Mallika - new code end
		
		params.put("topic", topic);
		params.put("post", post);
		params.put("path", path);
		
		String topicFrom = "";
		if (topic.getPostedBy() != null)
		{
			topicFrom = topic.getPostedBy().getFirstName() +" "+ topic.getPostedBy().getLastName();		
		}
		params.put("topicfrom", topicFrom);

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
		params.put("serverurl", siteNavUrl);
		
		//Mallika-comments beg
		/*params.put("forumLink", forumLink);
		params.put("unwatch", unwatch);*/
		//Mallika-comments end
		
		//Mallika-code below changed to add course id
		Object obj[] = new String[] { topic.getTitle()};
		/*super.prepareMessage(recipients, params,
				"[" + path + "] " + MessageFormat.format(SystemGlobals.getValue(ConfigKeys.MAIL_NEW_ANSWER_SUBJECT), new String[] { topic.getTitle() }),
			SystemGlobals.getValue(ConfigKeys.MAIL_NEW_ANSWER_MESSAGE_FILE));*/
		/*super.prepareMessage(recipients, params,
				"[" + path + "] " + MessageFormat.format(SystemGlobals.getValue(ConfigKeys.MAIL_NEW_ANSWER_SUBJECT), obj),
			SystemGlobals.getValue(ConfigKeys.MAIL_NEW_ANSWER_MESSAGE_FILE));*/
		super.prepareMessage(recipients, params,
				"[" + path + "] " + MessageFormat.format(SystemGlobals.getValue(ConfigKeys.MAIL_NEW_ANSWER_SUBJECT), obj),
			SystemGlobals.getValue(messageFile));
	}
	
	public TopicSpammer(Topic topic, Post post, List users, String messageFile, List attachments)
	{
		// Prepare the users. In this current version, the email
		// is not personalized, so then we'll just use his address
		List recipients = new ArrayList();
		for (Iterator iter = users.iterator(); iter.hasNext(); ) {
			User u = (User)iter.next();
			try
			{
				new InternetAddress(u.getEmail());
			} 
			catch (AddressException e)
			{
				if (logger.isWarnEnabled()) logger.warn("TopicSpammer(...) : "+ u.getEmail() + " is invalid. And exception is : "+ e);
				continue;
			}
			recipients.add(u.getEmail());
		}
		
		// Make the topic url
		String page = "";
		// int postsPerPage = SystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		int postsPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.POST_PER_PAGE);
		if (topic.getTotalReplies() > postsPerPage) {
			page += (((topic.getTotalReplies() / postsPerPage)) * postsPerPage) + "/";
		}
		
        SimpleHash params = new SimpleHash();
		if (logger.isDebugEnabled()) logger.debug("TopicSpammer- this is the CODE!");

		String path = null;
		Site currentSite = null;
		try
		{
		  currentSite = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
		  path = currentSite.getTitle();
				  
		}
		catch (Exception e)
		{
			logger.error(this.getClass().getName() + 
					".TopicSpammer() : " + e.getMessage(), e);
		}
		
		params.put("topic", topic);
		params.put("post", post);
		params.put("path", path);
		
		String topicFrom = "";
		if (topic.getPostedBy() != null)
		{
			topicFrom = topic.getPostedBy().getFirstName() +" "+ topic.getPostedBy().getLastName();		
		}
		params.put("topicfrom", topicFrom);

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
		params.put("serverurl", siteNavUrl);
		
		Object obj[] = new String[] { topic.getTitle()};
		
		if (attachments != null && attachments.size() > 0)
		{
			super.prepareAttachmentMessage(recipients, params,
					"[" + path + "] " + MessageFormat.format(SystemGlobals.getValue(ConfigKeys.MAIL_NEW_ANSWER_SUBJECT), obj),
				SystemGlobals.getValue(messageFile), attachments);
		}
		else
		{
			super.prepareMessage(recipients, params,
					"[" + path + "] " + MessageFormat.format(SystemGlobals.getValue(ConfigKeys.MAIL_NEW_ANSWER_SUBJECT), obj),
				SystemGlobals.getValue(messageFile));
		}
	}
	
}
