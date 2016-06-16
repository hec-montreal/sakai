/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/entities/Karma.java $ 
 * $Id: Karma.java 55483 2008-12-01 21:10:54Z murthy@etudes.org $ 
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
 * @author Rafael Steil
 */
public class Karma implements Serializable
{
	private int id;
	private int postId;
	private int topicId;
	private int postUserId;
	private int fromUserId;
	private int points;
	private Date rateDate;
	
	/**
	 * @return Returns the topicId.
	 */
	public int getTopicId()
	{
		return this.topicId;
	}
	
	/**
	 * @param topicId The topicId to set.
	 */
	public void setTopicId(int topicId)
	{
		this.topicId = topicId;
	}
	
	/**
	 * @return Returns the fromUserId.
	 */
	public int getFromUserId()
	{
		return this.fromUserId;
	}
	
	/**
	 * @param fromUserId The fromUserId to set.
	 */
	public void setFromUserId(int fromUserId)
	{
		this.fromUserId = fromUserId;
	}
	
	/**
	 * @return Returns the id.
	 */
	public int getId()
	{
		return this.id;
	}
	
	/**
	 * @param id The id to set.
	 */
	public void setId(int id)
	{
		this.id = id;
	}
	
	/**
	 * @return Returns the userId.
	 */
	public int getPostUserId()
	{
		return this.postUserId;
	}
	
	/**
	 * @param userId The userId to set.
	 */
	public void setPostUserId(int userId)
	{
		this.postUserId = userId;
	}
	
	/**
	 * @return Returns the points.
	 */
	public int getPoints()
	{
		return this.points;
	}
	
	/**
	 * @param points The points to set.
	 */
	public void setPoints(int points)
	{
		this.points = points;
	}
	
	/**
	 * @return Returns the postId.
	 */
	public int getPostId()
	{
		return this.postId;
	}
	
	/**
	 * @param postId The postId to set.
	 */
	public void setPostId(int postId)
	{
		this.postId = postId;
	}
	
	/**
	 * @return Returns the date of the vote.
	 */
    public Date getRateDate() 
    {
        return rateDate;
    }
    
    /**
	 * @param dateDate The date of the vote.
	 */
    public void setRateDate(Date rateDate) 
    {
        this.rateDate = rateDate;
    }
}
