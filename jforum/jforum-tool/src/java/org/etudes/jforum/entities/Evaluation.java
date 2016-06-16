/*********************************************************************************** 
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/entities/Evaluation.java $ 
 * $Id: Evaluation.java 70471 2010-09-29 22:13:03Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008 Etudes, Inc. 
 * 
 * Portions completed before September 1, 2008 Copyright (c) 2004, 2005, 2006, 2007 Foothill College, ETUDES Project 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License. 
 * 
 **********************************************************************************/ 
package org.etudes.jforum.entities;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents evaluation for forum or topic
 * 
 * @author Murthy Tanniru
 */
public class Evaluation implements Serializable
{
	private int id;
	private Float score;
	private String comments;
	private int evaluatedBy;
	private Date evaluatedDate;
	private int gradeId;
	private int userId;
	private String userFirstName;
	private String userLastName;
	private String sakaiUserId;
	private String sakaiDisplayId;
	private int totalPosts;
	private boolean released;
	private String username;
	private String userSakaiGroupName;
	
	public Evaluation() {}

	/**
	 * @return the comments
	 */
	public String getComments()
	{
		return this.comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments)
	{
		this.comments = comments;
	}

	/**
	 * @return the evaluatedBy
	 */
	public int getEvaluatedBy()
	{
		return this.evaluatedBy;
	}

	/**
	 * @param evaluatedBy the evaluatedBy to set
	 */
	public void setEvaluatedBy(int evaluatedBy)
	{
		this.evaluatedBy = evaluatedBy;
	}

	/**
	 * @return the evaluationDate
	 */
	public Date getEvaluatedDate()
	{
		return this.evaluatedDate;
	}

	/**
	 * @param evaluationDate the evaluationDate to set
	 */
	public void setEvaluatedDate(Date evaluatedDate)
	{
		this.evaluatedDate = evaluatedDate;
	}

	/**
	 * @return the firstName
	 */
	public String getUserFirstName()
	{
		return this.userFirstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setUserFirstName(String userFirstName)
	{
		this.userFirstName = userFirstName;
	}

	/**
	 * @return the id
	 */
	public int getId()
	{
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	/**
	 * @return the lastName
	 */
	public String getUserLastName()
	{
		return this.userLastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setUserLastName(String userLastName)
	{
		this.userLastName = userLastName;
	}

	/**
	 * @return the score
	 */
	public Float getScore()
	{
		return this.score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(Float score)
	{
		this.score = score;
	}

	/**
	 * @return the totalPosts
	 */
	public int getTotalPosts()
	{
		return this.totalPosts;
	}

	/**
	 * @param totalPosts the totalPosts to set
	 */
	public void setTotalPosts(int totalPosts)
	{
		this.totalPosts = totalPosts;
	}

	/**
	 * @return the userId
	 */
	public int getUserId()
	{
		return this.userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(int userId)
	{
		this.userId = userId;
	}
	
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Evaluation)) {
			return false;
		}
		
		return (((Evaluation)o).getId() == this.id);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		return this.id;
	}

	/**
	 * @return the gradeId
	 */
	public int getGradeId()
	{
		return this.gradeId;
	}

	/**
	 * @param gradeId the gradeId to set
	 */
	public void setGradeId(int gradeId)
	{
		this.gradeId = gradeId;
	}

	/**
	 * @return the sakaiUserId
	 */
	public String getSakaiUserId()
	{
		return this.sakaiUserId;
	}

	/**
	 * @param sakaiUserId the sakaiUserId to set
	 */
	public void setSakaiUserId(String sakaiUserId)
	{
		this.sakaiUserId = sakaiUserId;
	}

	/**
	 * @return the sakaiDisplayId
	 */
	public String getSakaiDisplayId()
	{
		return sakaiDisplayId;
	}

	/**
	 * @param sakaiDisplayId the sakaiDisplayId to set
	 */
	public void setSakaiDisplayId(String sakaiDisplayId)
	{
		this.sakaiDisplayId = sakaiDisplayId;
	}

	/**
	 * @param released the released to set
	 */
	public void setReleased(boolean released)
	{
		this.released = released;
	}

	/**
	 * @return the released
	 */
	public boolean isReleased()
	{
		return released;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username)
	{
		this.username = username;
	}

	/**
	 * @return the username
	 */
	public String getUsername()
	{
		return username;
	}

	/**
	 * @return the userSakaiGroupName
	 */
	public String getUserSakaiGroupName()
	{
		return userSakaiGroupName;
	}

	/**
	 * @param userSakaiGroupName the userSakaiGroupName to set
	 */
	public void setUserSakaiGroupName(String userSakaiGroupName)
	{
		this.userSakaiGroupName = userSakaiGroupName;
	}

}
