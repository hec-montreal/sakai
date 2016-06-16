/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/rss/RSS.java $ 
 * $Id: RSS.java 55476 2008-12-01 19:16:20Z murthy@etudes.org $ 
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
package org.etudes.jforum.util.rss;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a RSS document
 * 
 * @author Rafael Steil
 */
public class RSS 
{
	private List itens;
	private String title;
	private String description;
	private String encoding;
	private String link;
	
	/**
	 * Creates a new RSS document.
	 * 
	 * @param title The document title
	 * @param description The document description
	 * @param encoding The character encoding
	 * @param link The main document link
	 */
	public RSS(String title, String description, String encoding, String link)
	{
		this.itens = new ArrayList();
		this.title = title;
		this.description = description;
		this.encoding = encoding;
		this.link = link;
	}
	
	/**
	 * Gets the main document link
	 * @return The document link
	 */
	public String getLink()
	{
		return this.link;
	}
	
	/**
	 * Gets he document title 
	 * @return The document title
	 */
	public String getTitle()
	{
		return this.title;
	}
	
	/**
	 * Gets the document description
	 * @return The document description
	 */
	public String getDescription()
	{
		return this.description;
	}
	
	/**
	 * Gets the document character encoding
	 * @return The encoding
	 */
	public String getEncoding()
	{
		return this.encoding;
	}
	
	/**
	 * Gets all <code>RSSItem</code> instances related
	 * to this RSS document.
	 * 
	 * @return <code>java.util.List</code> with the entries
	 */
	public List getItens()
	{
		return this.itens;
	}
	
	/**
	 * Add a new item to the RSS document
	 * 
	 * @param entry <code>RSSItem</code> object containing the item information 
	 */
	public void addItem(RSSItem item)
	{
		this.itens.add(item);
	}
}
