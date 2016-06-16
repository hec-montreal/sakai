/*********************************************************************************** 
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/entities/Grade.java $ 
 * $Id: Grade.java 63031 2009-09-04 00:09:18Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008, 2009 Etudes, Inc. 
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

/**
 * Represents grade for forum or topic
 * 
* @author Murthy Tanniru
 */
public class Grade implements Serializable
{
	private int id;
	private String context;
	private int type;
	private int forumId;
	private int topicId;
	private int categoryId;
	private Float points;
	private boolean addToGradeBook;
	
	public Grade() {}
	
	public Grade(Grade grade)
	{
		this.id = grade.getId();
		this.context = grade.getContext();
		this.type = grade.getType();
		this.forumId = grade.getForumId();
		this.topicId = grade.getTopicId();
		this.points = grade.getPoints();
		this.addToGradeBook = grade.isAddToGradeBook();
	}

	/**
	 * @return the context
	 */
	public String getContext()
	{
		return this.context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(String context)
	{
		this.context = context;
	}

	/**
	 * @return the forumId
	 */
	public int getForumId()
	{
		return this.forumId;
	}

	/**
	 * @param forumId the forumId to set
	 */
	public void setForumId(int forumId)
	{
		this.forumId = forumId;
	}

	/**
	 * @return the tyep
	 */
	public int getType()
	{
		return this.type;
	}

	/**
	 * @param gradeType the gradeType to set
	 */
	public void setType(int type)
	{
		this.type = type;
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
	 * @return the points
	 */
	public Float getPoints()
	{
		return this.points;
	}

	/**
	 * @param points the points to set
	 */
	public void setPoints(Float points)
	{
		this.points = points;
	}

	/**
	 * @return the topicId
	 */
	public int getTopicId()
	{
		return this.topicId;
	}

	/**
	 * @param topicId the topicId to set
	 */
	public void setTopicId(int topicId)
	{
		this.topicId = topicId;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Grade)) {
			return false;
		}
		
		return (((Grade)o).getId() == this.id);
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
	 * @return the addToGradeBook
	 */
	public boolean isAddToGradeBook()
	{
		return this.addToGradeBook;
	}

	/**
	 * @param addToGradeBook the addToGradeBook to set
	 */
	public void setAddToGradeBook(boolean addToGradeBook)
	{
		this.addToGradeBook = addToGradeBook;
	}
	
	/**
	 * @return the categoryId
	 */
	public int getCategoryId()
	{
		return categoryId;
	}

	/**
	 * @param categoryId the categoryId to set
	 */
	public void setCategoryId(int categoryId)
	{
		this.categoryId = categoryId;
	}
	

}
