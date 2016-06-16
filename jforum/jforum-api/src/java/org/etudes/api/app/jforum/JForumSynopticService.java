/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-api/src/java/org/etudes/api/app/jforum/JForumSynopticService.java $ 
 * $Id: JForumSynopticService.java 66776 2010-03-23 18:50:51Z murthy@etudes.org $ 
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
 ***********************************************************************************/
package org.etudes.api.app.jforum;

public interface JForumSynopticService
{
	/**
	 * Gets the current logged in user's new private messages count in the site
	 * @return The new private messages count for the user in the site
	 */
	public int getUserNewPrivateMessageCount();
	
	/**
	 * Checks for the unread posts
	 * @return true - if user has unread posts
	 * 		   false - if user has no unread posts
	 */
	public boolean isUserHasUnreadTopicsAndReplies();
}
