/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/entities/Forum.java $ 
 * $Id: Forum.java 70223 2010-09-10 16:26:06Z murthy@etudes.org $ 
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
package org.etudes.jforum.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a specific forum.
 * 
 * @author Rafael Steil
 * 1/6/06 - Mallika - Adding startdate and enddate
 */
public class Forum implements Serializable
{
	public static final int TYPE_NORMAL = 0;
	public static final int TYPE_REPLY_ONLY = 1;
	public static final int TYPE_READ_ONLY = 2;
	public static final int ACCESS_SITE = 0;
	public static final int ACCESS_DENY = 1;
	public static final int ACCESS_GROUPS = 2;
	public static final int GRADE_DISABLED = 0;
	public static final int GRADE_BY_TOPIC = 1;
	public static final int GRADE_BY_FORUM = 2;
	public static final int GRADE_BY_CATEGORY = 3;
	
	private int id;
	private int idCategories;
	private String name;
	private String description;
	private int order;
	private int totalTopics;
	private int totalPosts;
	private int lastPostId;
	private boolean moderated;
	private boolean unread;
	private Date startDate;
	private Date endDate;
	private boolean lockForum;
	private LastPostInfo lpi;
	private String startDateFormatted;
	private String endDateFormatted;
	private int type;
	private int accessType;
	private List groups;
	private int gradeType;
	private List<SpecialAccess> specialAccessList = new ArrayList<SpecialAccess>();
	
	public Forum() { }
	
	public Forum(int forumId) {
		this.id = forumId;
	}
	
	public Forum(Forum f)
	{
		this.description = f.getDescription();
		this.id = f.getId();
		this.idCategories = f.getCategoryId();
		this.lastPostId = f.getLastPostId();
		this.moderated = f.isModerated();
		this.name = f.getName();
		this.order = f.getOrder();
		this.totalPosts = f.getTotalPosts();
		this.totalTopics = f.getTotalTopics();
		this.unread = f.getUnread();
		this.startDate = f.getStartDate();		
		this.endDate = f.getEndDate();
		this.lockForum = f.isLockForum();
		this.lpi = f.getLastPostInfo();
		this.startDateFormatted = f.getStartDateFormatted();
		this.endDateFormatted = f.getEndDateFormatted();
		this.type = f.getType();
		this.accessType = f.getAccessType();
		this.groups = f.getGroups();
		this.gradeType = f.getGradeType();
		
		for(SpecialAccess specialAccess : f.getSpecialAccessList())
		{
			this.specialAccessList.add(new SpecialAccess(specialAccess));
		}
	}
	
	public void setLastPostInfo(LastPostInfo lpi) {
		this.lpi = lpi;
	}
	
	public LastPostInfo getLastPostInfo() {
		return this.lpi;
	}
	
	/**
	 * Gets the forum's description
	 * 
	 * @return String with the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Gets the forum's ID
	 * 
	 * @return int value representing the ID
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Gets the category which the forum belongs to
	 * 
	 * @return int value representing the ID of the category 
	 */
	public int getCategoryId() {
		return this.idCategories;
	}

	/**
	 * Gets the ID of the last post
	 * 
	 * @return int value representing the ID of the post
	 */
	public int getLastPostId() {
		return this.lastPostId;
	}

	/**
	 * Checks if is a moderated forum
	 * 
	 * @return boolean value. <code>true</code> if the forum is moderated, <code>false</code> if not.
	 */
	public boolean isModerated() {
		return this.moderated;
	}

	/**
	 * Gets the name of the forum
	 * 
	 * @return String with the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the order
	 * 
	 * @return int value representing the order of the forum
	 */
	public int getOrder() {
		return this.order;
	}

	/**
	 * Gets the total number of topics posted in the forum
	 * 
	 * @return int value with the total number of the topics
	 */
	public int getTotalTopics() {
		return this.totalTopics;
	}
	
	public boolean getUnread() {
		return this.unread;
	}
	public Date getStartDate() {
		return this.startDate;
	}
	public Date getEndDate() {
		return this.endDate;
	}	
	/**
	 * Sets the description.
	 * 
	 * @param description The description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id The id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the category id
	 * 
	 * @param idCategories The ID of the category  to set to the forum
	 */
	public void setIdCategories(int idCategories) {
		this.idCategories = idCategories;
	}

	/**
	 * Sets the ID of the last post
	 * 
	 * @param lastPostId The post id
	 */
	public void setLastPostId(int lastPostId) {
		this.lastPostId = lastPostId;
	}

	/**
	 * Sets the moderated flag to the forum
	 * 
	 * @param moderated <code>true</code> or <code>false</code>
	 */
	public void setModerated(boolean moderated) {
		this.moderated = moderated;
	}

	/**
	 * Sets the name of the forum
	 * 
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the order.
	 * 
	 * @param order The order to set
	 */
	public void setOrder(int order) {
		this.order = order;
	}
	
	public void setUnread(boolean status) {
		this.unread = status;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return the lockForum
	 */
	public boolean isLockForum()
	{
		return lockForum;
	}

	/**
	 * @param lockForum the lockForum to set
	 */
	public void setLockForum(boolean lockForum)
	{
		this.lockForum = lockForum;
	}

	/**
	 * Sets the total number of topics
	 * 
	 * @param totalTopics int value with the total number of topics
	 */
	public void setTotalTopics(int totalTopics) {
		this.totalTopics = totalTopics;
	}
	
	public int getTotalPosts() {
		return this.totalPosts;
	}
	
	public void setTotalPosts(int totalPosts) {
		this.totalPosts = totalPosts;
	}
	
	/** 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) 
	{
		return ((o instanceof Forum) && (((Forum)o).getId() == this.id));
	}

	/** 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() 
	{
		return this.id;
	}
	
	/** 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[" + this.name + ", id=" + this.id + ", order=" + this.order + "]";
	}

	public String getEndDateFormatted() {
		return endDateFormatted;
	}

	public void setEndDateFormatted(String endDateFormatted) {
		this.endDateFormatted = endDateFormatted;
	}

	public String getStartDateFormatted() {
		return startDateFormatted;
	}

	public void setStartDateFormatted(String startDateFormatted) {
		this.startDateFormatted = startDateFormatted;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getAccessType() {
		return accessType;
	}

	public void setAccessType(int accessType) {
		this.accessType = accessType;
	}

	public List getGroups() {
		return groups;
	}

	public void setGroups(List groups) {
		this.groups = groups;
	}

	public int getGradeType() {
		return gradeType;
	}

	public void setGradeType(int gradeType) {
		this.gradeType = gradeType;
	}

	/**
	 * @return the specialAccessList
	 */
	public List<SpecialAccess> getSpecialAccessList()
	{
		return specialAccessList;
	}

	/**
	 * @param specialAccessList the specialAccessList to set
	 */
	public void setSpecialAccessList(List<SpecialAccess> specialAccessList)
	{
		this.specialAccessList = specialAccessList;
	}	
}
