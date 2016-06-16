/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/entities/CourseTimeObj.java $ 
 * $Id: CourseTimeObj.java 55483 2008-12-01 21:10:54Z murthy@etudes.org $ 
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
 * Represents a coursecategory in the System.
 * 
 * @author Mallika M Thoppay
 * 10/2/06 - adding fields and methods for markallTime
 */
public class CourseTimeObj  implements Serializable
{
	private String courseId;
	private int userId;
	private Date visitTime;
    private Date markAllTime;
		
	public CourseTimeObj() {}
	
	public CourseTimeObj(String courseId,int userId,Date visitTime,Date markAllTime) {
		this.courseId = courseId;
		this.userId = userId;
		this.visitTime = visitTime;
        this.markAllTime = markAllTime;
		
	}
	
	public CourseTimeObj(CourseTimeObj c) {
		this.courseId = c.getCourseId();
		this.userId = c.getUserId();
		this.visitTime = c.getVisitTime();
		this.markAllTime = c.getMarkAllTime();
		
	}
	/**
	 * Sets the id.
	 * @param courseId The courseId to set
	 */
	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}
	/**
	 * @return String
	 */
	public String getCourseId() {
		return this.courseId;
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
	public Date getVisitTime() {
		return this.visitTime;
	}
	/**
	 * Sets the id.
	 * @param msgId The msgId to set
	 */
	public void setVisitTime(Date visitTime) {
		this.visitTime = visitTime;
	}			

	/**
	 * @return int
	 */
	public Date getMarkAllTime() {
		return this.markAllTime;
	}
	/**
	 * Sets the id.
	 * @param msgId The msgId to set
	 */
	public void setMarkAllTime(Date markAllTime) {
		this.markAllTime = markAllTime;
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
		return ((o instanceof CourseTimeObj) && (((CourseTimeObj)o).getUserId() == this.userId) && (((CourseTimeObj)o).getCourseId() == this.courseId) && (((CourseTimeObj)o).getVisitTime() == this.visitTime) && (((CourseTimeObj)o).getMarkAllTime() == this.markAllTime));
	}
	
	/** 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "[userId=" + this.userId + ", courseid=" + this.courseId  + ", visitTime=" +this.visitTime + ", markAllTime=" +this.markAllTime+ "]"; 
	}

}
