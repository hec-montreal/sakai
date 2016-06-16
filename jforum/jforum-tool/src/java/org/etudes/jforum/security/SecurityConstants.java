/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/security/SecurityConstants.java $ 
 * $Id: SecurityConstants.java 55478 2008-12-01 19:21:44Z murthy@etudes.org $ 
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
package org.etudes.jforum.security;

/**
 * @author Rafael Steil
 * 08/08/06 - Murthy - added constant for task topics
 */
public class SecurityConstants 
{
	private SecurityConstants() {}
	
	public static final String PERM_ADMINISTRATION = "perm_administration";
	public static final String PERM_CATEGORY = "perm_category";
	public static final String PERM_FORUM = "perm_forum";
	public static final String PERM_ANONYMOUS_POST = "perm_anonymous_post";
	public static final String PERM_MODERATION = "perm_moderation";
	public static final String PERM_MODERATION_APPROVE_MESSAGES = "perm_moderation_approve_messages";
	public static final String PERM_MODERATION_FORUMS = "perm_moderation_forums";
	public static final String PERM_MODERATION_POST_REMOVE = "perm_moderation_post_remove";
	public static final String PERM_MODERATION_POST_EDIT = "perm_moderation_post_edit";
	public static final String PERM_MODERATION_TOPIC_MOVE = "perm_moderation_topic_move";
	public static final String PERM_MODERATION_TOPIC_LOCK_UNLOCK = "perm_moderation_topic_lockUnlock";
	public static final String PERM_CREATE_STICKY_ANNOUNCEMENT_TOPICS = "perm_create_sticky_announcement_topics";
	public static final String PERM_READ_ONLY_FORUMS = "perm_read_only_forums";
	public static final String PERM_HTML_DISABLED = "perm_html_disabled";
	public static final String PERM_REPLY_ONLY = "perm_reply_only";
	public static final String PERM_KARMA_ENABLED = "perm_karma_enabled";
	public static final String PERM_BOOKMARKS_ENABLED = "perm_bookmarks_enabled";
	public static final String PERM_ATTACHMENTS_ENABLED = "perm_attachments_enabled";
	public static final String PERM_ATTACHMENTS_DOWNLOAD = "perm_attachments_download";
	//08/08/06 Murthy - added for task
	public static final String PERM_CREATE_TASK_TOPICS = "perm_create_task_topics";
}
