/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/entities/PrivateMessage.java $ 
 * $Id: PrivateMessage.java 69032 2010-07-02 23:28:56Z murthy@etudes.org $ 
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
package org.etudes.jforum.entities;

/**
 * @author Rafael Steil
 */
public class PrivateMessage 
{
	public static final int PRIORITY_GENERAL = 0;
	public static final int PRIORITY_HIGH = 1;
	
	private int id;
	private int type;
	private User fromUser;
	private User toUser;
	private Post post;
	private String formatedDate;
	private boolean hasAttachments;
	private boolean flagToFollowup;
	private boolean replied;
	private int priority;
	
	
	/**
	 * @return Returns the fromUser.
	 */
	public User getFromUser()
	{
		return fromUser;
	}
	
	/**
	 * @param fromUser The fromUser to set.
	 */
	public void setFromUser(User fromUser)
	{
		this.fromUser = fromUser;
	}
	
	/**
	 * @return Returns the toUser.
	 */
	public User getToUser()
	{
		return toUser;
	}
	
	/**
	 * @param toUser The toUser to set.
	 */
	public void setToUser(User toUser)
	{
		this.toUser = toUser;
	}
	
	/**
	 * @return Returns the type.
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * @param type The type to set.
	 */
	public void setType(int type)
	{
		this.type = type;
	}
	
	/**
	 * @return Returns the id.
	 */
	public int getId()
	{
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(int id)
	{
		this.id = id;
	}
	
	/**
	 * @return Returns the post.
	 */
	public Post getPost()
	{
		return post;
	}
	
	/**
	 * @param post The post to set.
	 */
	public void setPost(Post post)
	{
		this.post = post;
	}
	
	/**
	 * @return Returns the formatedDate.
	 */
	public String getFormatedDate()
	{
		return formatedDate;
	}
	
	/**
	 * @param formatedDate The formatedDate to set.
	 */
	public void setFormatedDate(String formatedDate)
	{
		this.formatedDate = formatedDate;
	}
	
	/**
	 * @return Returns the hasAttachments.
	 */
	public boolean hasAttachments()
	{
		return this.hasAttachments;
	}
	
	/**
	 * @param hasAttachments The hasAttachments to set.
	 */
	public void hasAttachments(boolean hasAttachments)
	{
		this.hasAttachments = hasAttachments;
	}
	
	/**
	 * @return Returns the flag to follow up
	 */
	public boolean isFlagToFollowup()
	{
		return flagToFollowup;
	}

	/**
	 * @param flagToFollowup Sets the flag to follow up
	 */
	public void setFlagToFollowup(boolean flagToFollowup)
	{
		this.flagToFollowup = flagToFollowup;
	}

	/**
	 * @return the replied
	 */
	public boolean isReplied()
	{
		return replied;
	}

	/**
	 * @param replied the replied to set
	 */
	public void setReplied(boolean replied)
	{
		this.replied = replied;
	}

	/**
	 * @return the priority
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}
}
