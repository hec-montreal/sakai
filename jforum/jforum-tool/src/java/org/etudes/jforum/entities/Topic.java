/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/entities/Topic.java $ 
 * $Id: Topic.java 55483 2008-12-01 21:10:54Z murthy@etudes.org $ 
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
package org.etudes.jforum.entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents every topic in the forum.
 * 
 * @author Rafael Steil
* Mallika - 9/27/06 - eliminating views column
* 11/17/06 - Murthy - changed the values for topic types
 */
public class Topic implements Serializable
{
	//<<< 11/17/06 - Murthy - changed the values for topic types
	public static final int TYPE_NORMAL = 0;
	//public static final int TYPE_TASK = 1;
	public static final int TYPE_STICKY = 2;
	public static final int TYPE_ANNOUNCE = 3;
	//>>> 11/17/06 - Murthy - changed the values for topic types

	public static final int STATUS_UNLOCKED = 0;
	public static final int STATUS_LOCKED = 1;
	
	public static final int GRADE_NO = 0;
	public static final int GRADE_YES = 1;
	
	public static final int EXPORT_No = 0;
	public static final int EXPORT_YES = 1;
	
	
	
	private int id;
	private int forumId;
	private boolean read = true;
	private String title;
	private Date time;
	private Date lastPostTimeInMillis;
	private int totalReplies;
	private int status;
	private boolean vote;
	private int type;
	private int firstPostId;
	private String firstPostTime;
	private int lastPostId;	
	private String lastPostTime;
	private boolean moderated;
	private boolean paginate;
	private Double totalPages;
	private User postedBy;
	private User lastPostBy;
	private boolean isHot;
	private boolean hasAttach;
	private boolean gradeTopic;
	private boolean exportTopic;
	
	public Topic() {}
		
	/**
	 * Returns the ID of the firts topic
	 * 
	 * @return int value with the ID
	 */
	public int getFirstPostId() {
		return this.firstPostId;
	}

	/**
	 * Returns the ID of the topic
	 * 
	 * @return int value with the ID
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Returns the ID of the forum this topic belongs to
	 * 
	 * @return int value with the ID
	 */
	public int getForumId() {
		return this.forumId;
	}

	/**
	 * Teturns the ID of the last post in the topic
	 * 
	 * @return int value with the ID
	 */
	public int getLastPostId() {
		return this.lastPostId;
	}

	/**
	 * Returns the status 
	 * 
	 * @return int value with the status
	 */
	public int getStatus() {
		return this.status;
	}

	/**
	 * Returns the time the topic was posted
	 * 
	 * @return int value representing the time
	 */
	public Date getTime() {
		return this.time;
	}
	
	public void setFirstPostTime(String d) {
		this.firstPostTime = d;
	}
	
	public void setLastPostTime(String d) {
		this.lastPostTime = d;
	}

	/**
	 * Returns the title of the topci
	 * 
	 * @return String with the topic title
	 */
	public String getTitle() {
		return (this.title == null ? "" : this.title);
	}

	/**
	 * Returns the total number of replies
	 * 
	 * @return int value with the total
	 */
	public int getTotalReplies() {
		return this.totalReplies;
	}



	public User getPostedBy() {
		return this.postedBy;
	}
	
	public User getLastPostBy() {
		return this.lastPostBy;
	}

	/**
	 * Returns the type
	 * 
	 * @return int value representing the type
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Is a votation topic?
	 * 
	 * @return boolean value
	 */
	public boolean isVote() {
		return this.vote;
	}

	/**
	 * Sets the id of the firts post in the topic
	 * 
	 * @param firstPostId The post id 
	 */
	public void setFirstPostId(int firstPostId) {
		this.firstPostId = firstPostId;
	}

	/**
	 * Sets the id to the topic
	 * 
	 * @param id The id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the id of the forum associeted with this topic
	 * 
	 * @param idForum The id of the forum to set
	 */
	public void setForumId(int idForum) {
		this.forumId = idForum;
	}

	/**
	 * Sets the id of the last post in the topic
	 * 
	 * @param lastPostId The post id
	 */
	public void setLastPostId(int lastPostId) {
		this.lastPostId = lastPostId;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status The status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Sets the time.
	 * 
	 * @param time The time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	/**
	 * Sets the title.
	 * 
	 * @param title The title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Sets the totalReplies.
	 * 
	 * @param totalReplies The totalReplies to set
	 */
	public void setTotalReplies(int totalReplies) {
		this.totalReplies = totalReplies;
	}


	/**
	 * Sets the type.
	 * 
	 * @param type The type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * Sets the vote.
	 * 
	 * @param vote The vote to set
	 */
	public void setVote(boolean vote) {
		this.vote = vote;
	}
	/**
	 * @return
	 */
	public boolean isModerated() {
		return this.moderated;
	}

	/**
	 * @param b
	 */
	public void setModerated(boolean b) {
		this.moderated = b;
	}
	
	public void setPostedBy(User u) {
		this.postedBy = u;
	}
	
	public void setLastPostBy(User u) {
		this.lastPostBy = u;
	}
	
	public String getFirstPostTime() {
		return this.firstPostTime;
	}
	
	public String getLastPostTime() {
		return this.lastPostTime;
	}

	public void setRead(boolean read) {
		this.read = read;
	}
	
	public boolean getRead() {
		return this.read;
	}
	
	public void setLastPostTimeInMillis(Date t) {
		this.lastPostTimeInMillis = t;
	}
	
	public Date getLastPostTimeInMillis() {
		return this.lastPostTimeInMillis;
	}
	
	public void setPaginate(boolean paginate) {
		this.paginate = paginate;
	}
	
	public boolean getPaginate() {
		return this.paginate;
	}
	
	public void setTotalPages(Double total) {
		this.totalPages = total;
	}
	
	public Double getTotalPages() {
		return this.totalPages;
	}
	
	public void setHot(boolean hot) {
		this.isHot = hot;
	}
	
	public boolean isHot() {
		return this.isHot;
	}
	
	public void setHasAttach(boolean b)
	{
		this.hasAttach = b;
	}
	
	public boolean hasAttach()
	{
		return this.hasAttach;
	}
	
	/** 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		if (!(o instanceof Topic)) {
			return false;
		}
		
		return (((Topic)o).getId() == this.id);
	}
	/** 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return this.id;
	}

	/**
	 * @return the gradeTopic
	 */
	public boolean isGradeTopic()
	{
		return this.gradeTopic;
	}

	/**
	 * @param gradeTopic the gradeTopic to set
	 */
	public void setGradeTopic(boolean gradeTopic)
	{
		this.gradeTopic = gradeTopic;
	}

	/**
	 * @return the exportTopic
	 */
	public boolean isExportTopic()
	{
		return this.exportTopic;
	}

	/**
	 * @param exportTopic the exportTopic to set
	 */
	public void setExportTopic(boolean exportTopic)
	{
		this.exportTopic = exportTopic;
	}
}
