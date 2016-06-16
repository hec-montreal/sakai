/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/rss/ForumRSS.java $ 
 * $Id: ForumRSS.java 61928 2009-07-21 22:29:16Z murthy@etudes.org $ 
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
package org.etudes.jforum.util.rss;


import java.util.Iterator;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.entities.Category;
import org.etudes.jforum.entities.Forum;
import org.etudes.jforum.entities.LastPostInfo;
import org.etudes.jforum.repository.ForumRepository;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.view.forum.common.ViewCommon;

/**
 * @author Rafael Steil
 */
public class ForumRSS extends GenericRSS 
{
	private static final Log logger = LogFactory.getLog(ForumRSS.class);
	
	private List categories;
	private RSS rss;
	private String forumLink;
	
	public ForumRSS(String title, String description, List categories)
	{
		this.categories = categories;
		this.forumLink = ViewCommon.getForumLink();
		
		this.rss = new RSS(title, description, 
				// SystemGlobals.getValue(ConfigKeys.ENCODING ),
				SakaiSystemGlobals.getValue(ConfigKeys.ENCODING ),
				this.forumLink);
		
		this.prepareRSS();
	}
	
	private void prepareRSS()
	{
		try {
			for (Iterator iter = this.categories.iterator(); iter.hasNext(); ) {
				Category category = (Category)iter.next();
				
				for (Iterator fIter = category.getForums().iterator(); fIter.hasNext(); ) {
					Forum forum = (Forum)fIter.next();
					
					LastPostInfo info = ForumRepository.getLastPostInfo(forum.getId());
					
					RSSItem item = new RSSItem();
					item.addCategory(category.getName());
					item.setTitle(forum.getName());
					item.setDescription(forum.getDescription());
					item.setContentType(RSSAware.CONTENT_HTML);
					item.setLink(this.forumLink
							+ "forums/show/" + forum.getId()
							+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
					
					String author = info.getUsername();
					
					item.setAuthor(author != null ? author : I18n.getMessage("Guest"));
					
					String date = info.getPostDate();
					item.setPublishDate(date != null ? RSSUtils.formatDate(date) : "");
					
					this.rss.addItem(item);
				}
			}
		}
		catch (Exception e) {
			logger.warn("Error while generating rss for forums: " + e);
			e.printStackTrace();
		}
		
		super.setRSS(this.rss);
	}
}
