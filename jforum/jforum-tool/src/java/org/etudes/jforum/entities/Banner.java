/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/entities/Banner.java $ 
 * $Id: Banner.java 55483 2008-12-01 21:10:54Z murthy@etudes.org $ 
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
 * Represents a banner in the System.
 *
 * @author Samuel Yung
 */
public class Banner implements Serializable
{
	private int id;
	private String comment;
	private boolean active;
	private int type;
	private String name;
	private String description;
	private int width;
	private int height;
	private int views;
	private int clicks;
	private String url;
	private int placement;
	private int weight;

	public Banner()
	{}

	public Banner(int id)
	{
		this.id = id;
	}

	public Banner(Banner c)
	{
		this.id = c.getId();
		this.comment = c.getComment();
		this.active = c.isActive();
		this.type = c.getType();
		this.name = c.getName();
		this.description = c.getDescription();
		this.width = c.getWeight();
		this.height = c.getHeight();
		this.views = c.getViews();
		this.clicks = c.getClicks();
		this.url = c.getUrl();
		this.placement = c.getPlacement();
		this.weight = c.getWeight();
	}

	/**
	 * @return int
	 */
	public int getId()
	{
		return this.id;
	}

	public String getComment()
	{
		return comment;
	}

	public boolean isActive()
	{
		return active;
	}

	public int getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public String getDescription()
	{
		return description;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public int getViews()
	{
		return views;
	}

	public int getClicks()
	{
		return clicks;
	}

	public String getUrl()
	{
		return url;
	}

	public int getPlacement()
	{
		return placement;
	}

	public int getWeight()
	{
		return weight;
	}

	/**
	 * Sets the id.
	 * @param id The id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}

	public void setComment(String comment)
	{
		this.comment = comment;
	}

	public void setActive(boolean active)
	{
		this.active = active;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setWidth(int width)
	{
		this.width = width;
	}

	public void setHeight(int height)
	{
		this.height = height;
	}

	public void setViews(int views)
	{
		this.views = views;
	}

	public void setClicks(int clicks)
	{
		this.clicks = clicks;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public void setPlacement(int placement)
	{
		this.placement = placement;
	}

	public void setWeight(int weight)
	{
		this.weight = weight;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode()
	{
		return this.id;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o)
	{
		return((o instanceof Banner) && (((Banner)o).getId() == this.id));
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return "[comment=" + this.comment + ", id=" + this.id +
			", type=" + this.type + ", name=" + this.name +
			", description=" + this.description + ", active=" +
			this.active + "]";
	}

}
