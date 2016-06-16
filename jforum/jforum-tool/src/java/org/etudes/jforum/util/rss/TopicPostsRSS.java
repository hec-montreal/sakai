/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/rss/TopicPostsRSS.java $ 
 * $Id: TopicPostsRSS.java 61928 2009-07-21 22:29:16Z murthy@etudes.org $ 
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

import org.etudes.jforum.entities.Post;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.view.forum.common.PostCommon;
import org.etudes.jforum.view.forum.common.ViewCommon;


/**
 * RSS for the messages of some topic
 * 
 * @author Rafael Steil
 */
public class TopicPostsRSS extends GenericRSS 
{
	private List posts;
	protected RSS rss;
	protected String forumLink;
	
	public TopicPostsRSS(String title, String description, int topicId, List posts)
	{
		this.forumLink = ViewCommon.getForumLink();
		
		this.posts = posts;
		this.rss = new RSS(title, description, 
				// SystemGlobals.getValue(ConfigKeys.ENCODING),
				SakaiSystemGlobals.getValue(ConfigKeys.ENCODING),
				this.forumLink + "posts/list/" + topicId 
				+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
		this.prepareRSS();
	}
	
	private void prepareRSS()
	{
		for (Iterator iter = this.posts.iterator(); iter.hasNext(); ) {
			Post p = (Post)iter.next();
			
			RSSItem item = new RSSItem();
			item.setAuthor(p.getPostUsername());
			item.setContentType(RSSAware.CONTENT_HTML);
			item.setDescription(PostCommon.processText(p.getText()));
			item.setPublishDate(RSSUtils.formatDate(p.getTime()));
			item.setTitle(p.getSubject());
			item.setLink(this.forumLink 
					+ "posts/list/" + p.getTopicId()
					+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION)
					+ "#" + p.getId());

			this.rss.addItem(item);
		}
		
		super.setRSS(this.rss);
	}
}
