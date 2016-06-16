/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/entities/LastPostInfo.java $ 
 * $Id: LastPostInfo.java 55483 2008-12-01 21:10:54Z murthy@etudes.org $ 
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

/**
 * @author Rafael Steil
 * revised by rashmi - 09-14-05 to add firstname and last name attribs and getter/setter methods
 */
public class LastPostInfo implements Serializable
{
	private long postTimeMillis;
	private int topicId;
	private int postId;
	private int userId;
	private int topicReplies;
	private String username;
	private String postDate;
	private boolean hasInfo;
	//add by rashmi
	private String firstName;
	private String lastName;
	// add end
	
	public void setHasInfo(boolean value) {
		this.hasInfo = value;
	}
	
	public boolean hasInfo() {
		return this.hasInfo;
	}
	
	
	/**
	 * @return Returns the postDate.
	 */
	public String getPostDate() {
		return this.postDate;
	}
	/**
	 * @param postDate The postDate to set.
	 */
	public void setPostDate(String postDate) {
		this.postDate = postDate;
	}
	/**
	 * @return Returns the postId.
	 */
	public int getPostId() {
		return this.postId;
	}
	/**
	 * @param postId The postId to set.
	 */
	public void setPostId(int postId) {
		this.postId = postId;
	}
	/**
	 * @return Returns the postTimeMillis.
	 */
	public long getPostTimeMillis() {
		return this.postTimeMillis;
	}
	/**
	 * @param postTimeMillis The postTimeMillis to set.
	 */
	public void setPostTimeMillis(long postTimeMillis) {
		this.postTimeMillis = postTimeMillis;
	}
	/**
	 * @return Returns the topicId.
	 */
	public int getTopicId() {
		return this.topicId;
	}
	/**
	 * @param topicId The topicId to set.
	 */
	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}
	/**
	 * @return Returns the topicReplies.
	 */
	public int getTopicReplies() {
		return this.topicReplies;
	}
	/**
	 * @param topicReplies The topicReplies to set.
	 */
	public void setTopicReplies(int topicReplies) {
		this.topicReplies = topicReplies;
	}
	/**
	 * @return Returns the userId.
	 */
	public int getUserId() {
		return this.userId;
	}
	/**
	 * @param userId The userId to set.
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}
	/**
	 * @return Returns the username.
	 */
	public String getUsername() {
		return this.username;
	}
	/**
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	// add by rashmi
	/**
	 * @return Returns the firstName.
	 */
	public String getFirstName() {
		return firstName;
	}
	/**
	 * @param firstName The firstName to set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	/**
	 * @return Returns the lastName.
	 */
	public String getLastName() {
		return lastName;
	}
	/**
	 * @param lastName The lastName to set.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	//add end
}
