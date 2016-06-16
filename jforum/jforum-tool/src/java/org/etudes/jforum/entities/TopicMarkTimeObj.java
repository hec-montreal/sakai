/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/entities/TopicMarkTimeObj.java $ 
 * $Id: TopicMarkTimeObj.java 66202 2010-02-18 22:29:17Z murthy@etudes.org $ 
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
 * 
 * 
 * @author Mallika M Thoppay
 * 11/15/06 - created file
 */
public class TopicMarkTimeObj  implements Serializable
{
	private int topicId;
	private int userId;
	private Date markTime;
	boolean isRead = false;
    
	public TopicMarkTimeObj() {}
	
	public TopicMarkTimeObj(int topicId,int userId,Date markTime) {
		this.topicId = topicId;
		this.userId = userId;
		this.markTime = markTime;
		
	}
	
	public TopicMarkTimeObj(TopicMarkTimeObj c) {
		this.topicId = c.getTopicId();
		this.userId = c.getUserId();
		this.markTime = c.getMarkTime();
		
	}
	/**
	 * Sets the id.
	 * @param courseId The courseId to set
	 */
	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}
	/**
	 * @return String
	 */
	public int getTopicId() {
		return this.topicId;
	}
	
	/**
	 * @return int
	 */
	public int getUserId() {
		return this.userId;
	}
	/**
	 * Sets the id.
	 * @param msgId The msgId to set
	 */
	public void setUserId(int userId) {
		this.userId = userId;
	}		
	/**
	 * @return int
	 */
	public Date getMarkTime() {
		return this.markTime;
	}
	/**
	 * Sets the id.
	 * @param msgId The msgId to set
	 */
	public void setMarkTime(Date markTime) {
		this.markTime = markTime;
	}			

	
	
	
	/** 
	 * @see java.lang.Object#hashCode()
	 */
	/*public int hashCode() 
	{
		return this.msgId;
	}*/

	/** 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) 
	{
		return ((o instanceof TopicMarkTimeObj) && (((TopicMarkTimeObj)o).getUserId() == this.userId) && (((TopicMarkTimeObj)o).getTopicId() == this.topicId) && (((TopicMarkTimeObj)o).getMarkTime() == this.markTime));
	}
	
	/** 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[userId=" + this.userId + ", topicid=" + this.topicId  + ", markTime=" +this.markTime + "]"; 
	}

	/**
	 * @return the isRead
	 */
	public boolean isRead()
	{
		return isRead;
	}

	/**
	 * @param isRead the isRead to set
	 */
	public void setRead(boolean isRead)
	{
		this.isRead = isRead;
	}

}
