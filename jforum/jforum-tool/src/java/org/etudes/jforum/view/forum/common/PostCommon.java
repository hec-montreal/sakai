/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/forum/common/PostCommon.java $ 
 * $Id: PostCommon.java 67147 2010-04-13 21:25:01Z murthy@etudes.org $ 
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

package org.etudes.jforum.view.forum.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.etudes.jforum.JForum;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.PostDAO;
import org.etudes.jforum.dao.UserDAO;
import org.etudes.jforum.entities.Post;
import org.etudes.jforum.entities.Smilie;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.repository.BBCodeRepository;
import org.etudes.jforum.repository.PostRepository;
import org.etudes.jforum.repository.SmiliesRepository;
import org.etudes.jforum.security.SecurityConstants;
import org.etudes.jforum.util.SafeHtml;
import org.etudes.jforum.util.bbcode.BBCode;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.etudes.util.HtmlHelper;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.authz.api.AuthzGroupService;

// import net.jforum.repository.SecurityRepository;

/**
 * @author Rafael Steil
 *          revised UserForDisplay() 03/20/06 - rashmi - turn off jforum smilies 03/21/06 - rashmi - change in fillPostFromRequest() and
 *          preparePostForDisplay() to properly render FCK options 03/27/06 - rashmi - revised preparePostForDisplay() to remove extra lines from
 *          message 07/02/06 - rashmi - revised preparePostForDisplay() for quick reply revised by rashmi on 9/21/06 to remove extra lines from quick
 *          reply and editor message by putting check on quick reply only and not on editor text.
 */
public class PostCommon
{
	private static PostCommon instance = new PostCommon();

	/**
	 * Gets the instance. This method only exists to situations where an instance is needed in the template context, so we don't need to create a new
	 * instance every time.
	 * 
	 * @return
	 */
	public static PostCommon getInstance()
	{
		return instance;
	}

	public static Post preparePostForDisplay(Post p)
	{
		if (p.getText() == null)
		{
			return p;
		}

		// rashmi 03/21/06 changed if condition
		// if (!p.isHtmlEnabled()) {
		if (p.isHtmlEnabled())
		{
			p.setText(p.getText().replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
		}

		// DO NOT remove the trailing blank space
		// comment by rashmi 03/27/06
		// p.setText(p.getText().replaceAll("\n<p>","<p>"));
		// p.setText(p.getText().replaceAll("\n", "<br> "));
		// comment by rashmi 03/21/06
		// uncoment by rashmi 04/14/06 to turn on bbcode
		p.setText(alwaysProcess(p.getText(), BBCodeRepository.getBBCollection().getAlwaysProcessList()));

		// Then, search for bb codes
		if (p.isBbCodeEnabled())
		{
			p.setText(PostCommon.processText(p.getText()));
		}

		// Smilies...
		// rashmi comment to turn jforum smilies off 03/20/06
		// if (p.isSmiliesEnabled()) {
		// p.setText(processSmilies(p.getText(), SmiliesRepository.getSmilies()));
		// }

		return p;
	}

	private static String alwaysProcess(String text, Collection bbList)
	{
		for (Iterator iter = bbList.iterator(); iter.hasNext();)
		{
			BBCode bb = (BBCode) iter.next();
			text = text.replaceAll(bb.getRegex(), bb.getReplace());
		}

		return text;
	}

	public static String processText(String text)
	{
		if (text == null)
		{
			return null;
		}

		if (text.indexOf('[') > -1 && text.indexOf(']') > -1)
		{
			int openQuotes = 0;
			Iterator tmpIter = BBCodeRepository.getBBCollection().getBbList().iterator();

			while (tmpIter.hasNext())
			{
				BBCode bb = (BBCode) tmpIter.next();

				// Another hack for the quotes
				if (bb.getTagName().equals("openQuote") || bb.getTagName().equals("openSimpleQuote"))
				{
					Matcher matcher = Pattern.compile(bb.getRegex()).matcher(text);

					while (matcher.find())
					{
						openQuotes++;

						text = text.replaceFirst(bb.getRegex(), bb.getReplace());
					}
				}
				else if (bb.getTagName().equals("closeQuote"))
				{
					if (openQuotes > 0)
					{
						Matcher matcher = Pattern.compile(bb.getRegex()).matcher(text);

						while (matcher.find() && openQuotes-- > 0)
						{
							text = text.replaceFirst(bb.getRegex(), bb.getReplace());
						}
					}
				}
				else if (bb.getTagName().equals("code"))
				{
					Matcher matcher = Pattern.compile(bb.getRegex()).matcher(text);
					StringBuffer sb = new StringBuffer(text);

					while (matcher.find())
					{
						String contents = matcher.group(1);

						// Firefox seems to interpret <br> inside <pre>,
						// so we need this bizarre workaround
						contents = contents.replaceAll("<br>", "\n");

						// Do not allow other bb tags inside "code"
						contents = contents.replaceAll("\\[", "&#91;").replaceAll("\\]", "&#93;");

						// Try to bypass smilies interpretation
						contents = contents.replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;");

						// XML-like tags
						contents = contents.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

						StringBuffer replace = new StringBuffer(bb.getReplace());
						int index = replace.indexOf("$1");
						if (index > -1)
						{
							replace.replace(index, index + 2, contents);
						}

						index = sb.indexOf("[code]");
						int lastIndex = sb.indexOf("[/code]") + "[/code]".length();

						if (lastIndex > index)
						{
							sb.replace(index, lastIndex, replace.toString());
							text = sb.toString();
						}
					}
				}
				else
				{
					text = text.replaceAll(bb.getRegex(), bb.getReplace());
				}
			}

			if (openQuotes > 0)
			{
				BBCode closeQuote = BBCodeRepository.findByName("closeQuote");

				for (int i = 0; i < openQuotes; i++)
				{
					text = text + closeQuote.getReplace();
				}
			}
		}

		return text;
	}

	public static String processSmilies(String text, List smilies)
	{
		if (text == null || text.equals(""))
		{
			return text;
		}

		Iterator iter = smilies.iterator();
		while (iter.hasNext())
		{
			Smilie s = (Smilie) iter.next();

			int index = text.indexOf(s.getCode());
			if (index > -1)
			{
				text = text.replaceAll("\\Q" + s.getCode() + "\\E", s.getUrl());
			}
		}

		return text;
	}

	public static Post fillPostFromRequest() throws Exception
	{
		Post p = new Post();
		p.setTime(new Date());

		return fillPostFromRequest(p, false);
	}

	public static Post fillPostFromRequest(Post p, boolean isEdit) throws Exception
	{
		p.setSubject(SafeHtml.escapeScriptTagWithSpace(JForum.getRequest().getParameter("subject")));
		p.setBbCodeEnabled(JForum.getRequest().getParameter("disable_bbcode") != null ? false : true);
		p.setSmiliesEnabled(JForum.getRequest().getParameter("disable_smilies") != null ? false : true);
		p.setSignatureEnabled(JForum.getRequest().getParameter("attach_sig") != null ? true : false);

		if (!isEdit)
		{
			p.setUserIp(JForum.getRequest().getRemoteAddr());
			p.setUserId(SessionFacade.getUserSession().getUserId());
		}

		/*
		 * boolean htmlEnabled = SecurityRepository.canAccess(SecurityConstants.PERM_HTML_DISABLED, JForum.getRequest() .getParameter("forum_id"));
		 */
		boolean htmlEnabled = true;
		// changed by rashmi on 3/21/06 to show html codes
		// p.setHtmlEnabled(htmlEnabled && JForum.getRequest().getParameter("disable_html") == null);
		p.setHtmlEnabled(htmlEnabled && JForum.getRequest().getParameter("disable_html") != null);

		// revised by rashmi on 9/21/06 to remove extra lines
		// added branden patch on 02/23/09 to scrub invalid HTML with script tags from messages
		String message = "";
		if(JForum.getRequest().getParameter("message") != null) 
		{
			message = SafeHtml.escapeJavascript(JForum.getRequest().getParameter("message"));
			
			// strip html comments
			message = HtmlHelper.stripComments(message);
			
			// strip excess spaces
			message = SafeHtml.removeExcessSpaces(message);
			
		} else if (JForum.getRequest().getParameter("quickmessage") !=null) 
		{
			message = JForum.getRequest().getParameter("quickmessage");
			// strip html comments
			message = HtmlHelper.stripComments(message);
			
			// strip excess spaces
			message = SafeHtml.removeExcessSpaces(message);
			
			//scrub the raw user content of invalid HTML...
			message = SafeHtml.makeSafe( JForum.getRequest().getParameter("quickmessage"));
							
			//...then augment the plain-text to safe HTML
			message = message.replaceAll("\n<p>", "<p>");
			message = message.replaceAll("\n", "<br>");
			p.setHtmlEnabled(false);
		}
		
		if (p.isHtmlEnabled())
		{
			p.setText(SafeHtml.makeSafe(message));
		}
		else
		{
			p.setText(message);
		}

		// System.out.println("message as in p object:" + p.getText());
		return p;
	}

	public static void addToTopicPosters(int userId, Map usersMap, UserDAO um) throws Exception
	{
		Integer posterId = new Integer(userId);
		if (!usersMap.containsKey(posterId))
		{
			usersMap.put(posterId, PostCommon.getUserForDisplay(userId));
		}
	}
	
	public static void addToTopicFacilitators(int userId, List facilitatorsList, List participantsList) throws Exception
	{
		if (!facilitatorsList.contains(userId))
		{
			if (!participantsList.contains(userId))
			{
				User u = DataAccessDriver.getInstance().newUserDAO().selectById(userId);
				boolean facilitator = JForumUserUtil.isJForumFacilitator(u.getSakaiUserId());
				if (facilitator)
					facilitatorsList.add(userId);
				else 
					participantsList.add(userId);
			}
		}
	}

	public static User getUserForDisplay(int userId) throws Exception
	{
		User u = DataAccessDriver.getInstance().newUserDAO().selectById(userId);

		u.setTotalPosts(DataAccessDriver.getInstance().newUserDAO().getNumberOfMessages(userId));

		// <<< 01/10/2008 - Murthy - added the below code to hide email for inactive user
		if (u.isViewEmailEnabled() && (u.getEmail() != null && u.getEmail().trim().length() > 0))
		{
			AuthzGroupService authz = ComponentManager.get(AuthzGroupService.class);
			String role = authz.getUserRole(u.getSakaiUserId(), "/site/" + ToolManager.getCurrentPlacement().getContext());
			if (role == null) u.setEmail("");
		}
		// >>> 01/10/2008 - Murthy

		if (u.getSignature() != null)
		{
			u.setSignature(u.getSignature().replaceAll("\n", "<br>"));
			u.setSignature(PostCommon.processText(u.getSignature()));
			u.setSignature(PostCommon.processSmilies(u.getSignature(), SmiliesRepository.getSmilies()));
		}

		return u;
	}

	public static List topicPosts(PostDAO pm, UserDAO um, Map usersMap, boolean canEdit, int userId, int topicId, int start, int count)
			throws Exception
	{
		List posts = null;
		boolean needPrepare = true;

		if (SystemGlobals.getBoolValue(ConfigKeys.POSTS_CACHE_ENABLED))
		{
			posts = PostRepository.selectAllByTopicByLimit(topicId, start, count);
			needPrepare = false;
		}
		else
		{
			posts = pm.selectAllByTopicByLimit(topicId, start, count);
		}

		List helperList = new ArrayList();

		int anonymousUser = SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID);

		for (Iterator iter = posts.iterator(); iter.hasNext();)
		{
			Post p;

			if (needPrepare)
			{
				p = (Post) iter.next();
			}
			else
			{
				p = new Post((Post) iter.next());
			}

			if (canEdit || (p.getUserId() != anonymousUser && p.getUserId() == userId))
			{
				p.setCanEdit(true);
			}

			PostCommon.addToTopicPosters(p.getUserId(), usersMap, um);

			helperList.add(needPrepare ? PostCommon.preparePostForDisplay(p) : p);
		}

		return helperList;
	}
	
	public static List topicPosts(PostDAO pm, UserDAO um, Map usersMap, List facilitatorsList, List participantsList, boolean canEdit, int userId,
			int topicId, int start, int count) throws Exception
	{
		List posts = null;
		boolean needPrepare = true;

		if (SystemGlobals.getBoolValue(ConfigKeys.POSTS_CACHE_ENABLED))
		{
			posts = PostRepository.selectAllByTopicByLimit(topicId, start, count);
			needPrepare = false;
		}
		else
		{
			posts = pm.selectAllByTopicByLimit(topicId, start, count);
		}

		List helperList = new ArrayList();

		int anonymousUser = SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID);

		for (Iterator iter = posts.iterator(); iter.hasNext();)
		{
			Post p;

			if (needPrepare)
			{
				p = (Post) iter.next();
			}
			else
			{
				p = new Post((Post) iter.next());
			}

			if (canEdit || (p.getUserId() != anonymousUser && p.getUserId() == userId))
			{
				p.setCanEdit(true);
			}

			PostCommon.addToTopicPosters(p.getUserId(), usersMap, um);
			PostCommon.addToTopicFacilitators(p.getUserId(), facilitatorsList, participantsList);

			helperList.add(needPrepare ? PostCommon.preparePostForDisplay(p) : p);
		}

		return helperList;
	}

	/*
	 * get forum posts for a user
	 */
	public static List forumPosts(PostDAO pm, UserDAO um, Map usersMap, boolean canEdit, int userId, int forumId)
			throws Exception
	{
		List posts = null;
		
		posts = pm.selectAllByForumByUser(forumId, userId);
		
		List helperList = new ArrayList();

		int anonymousUser = SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID);

		for (Iterator iter = posts.iterator(); iter.hasNext();)
		{
			Post p;

			p = (Post) iter.next();
			
			if (canEdit || (p.getUserId() != anonymousUser && p.getUserId() == userId))
			{
				p.setCanEdit(true);
			}

			PostCommon.addToTopicPosters(p.getUserId(), usersMap, um);

			helperList.add(PostCommon.preparePostForDisplay(p));
		}

		return helperList;
	}
	
	/*
	 * get forum posts for a user
	 */
	public static List forumTopicPosts(PostDAO pm, UserDAO um, Map usersMap, boolean canEdit, int userId, int topicId)
			throws Exception
	{
		List posts = null;
		
		posts = pm.selectAllByTopicByUser(topicId, userId);
		
		List helperList = new ArrayList();

		int anonymousUser = SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID);

		for (Iterator iter = posts.iterator(); iter.hasNext();)
		{
			Post p;

			p = (Post) iter.next();
			
			if (canEdit || (p.getUserId() != anonymousUser && p.getUserId() == userId))
			{
				p.setCanEdit(true);
			}

			PostCommon.addToTopicPosters(p.getUserId(), usersMap, um);

			helperList.add(PostCommon.preparePostForDisplay(p));
		}

		return helperList;
	}
	
	/*
	 * get category posts for a user
	 */
	public static List categoryPosts(PostDAO pm, UserDAO um, Map usersMap, boolean canEdit, int userId, int categoryId)
			throws Exception
	{
		List posts = null;
		
		posts = pm.selectAllByCategoryByUser(categoryId, userId);
		
		List helperList = new ArrayList();

		int anonymousUser = SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID);

		for (Iterator iter = posts.iterator(); iter.hasNext();)
		{
			Post p;

			p = (Post) iter.next();
			
			if (canEdit || (p.getUserId() != anonymousUser && p.getUserId() == userId))
			{
				p.setCanEdit(true);
			}

			PostCommon.addToTopicPosters(p.getUserId(), usersMap, um);

			helperList.add(PostCommon.preparePostForDisplay(p));
		}

		return helperList;
	}
}
