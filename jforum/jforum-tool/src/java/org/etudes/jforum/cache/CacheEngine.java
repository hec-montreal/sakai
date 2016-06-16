/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/cache/CacheEngine.java $ 
 * $Id: CacheEngine.java 55498 2008-12-01 23:54:49Z murthy@etudes.org $ 
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
package org.etudes.jforum.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Rafael Steil
 */
public interface CacheEngine
{
	public static final String DUMMY_FQN = "";
	public static final String NOTIFICATION = "notification";
	
	/**
	 * Inits the cache engine. 
	 */
	public void init();
	
	/**
	 * Adds a new object to the cache. 
	 * The fqn will be set as the value of {@link #DUMMY_FQN}
	 * 
	 * @param key The key to associate with the object. 
	 * @param value The object to cache
	 */
	public void add(String key, Object value);
	/**
	 * Adds a new object to the cache. 
	 * 
	 * @param fqn The fully qualified name of the new node
	 * @param data The new data
	 */
	public void add(String fqn, Map data);
	
	/**
	 * 
	 * Adds a new object to the cache.
	 * 
	 * @param fqn The fully qualified name of the cache. 
	 * @param key The key to associate with the object
	 * @param value The object to cache
	 */
	public void add(String fqn, String key, Object value);
	
	/**
	 * Gets some object from the cache.
	 * 
	 * @param fqn The fully qualified name associated with the key
	 * @param key The key to get
	 * @return The cached object, or <code>null</code> if no entry was found
	 */
	public Object get(String fqn, String key);
	
	/**
	 * Gets some object from the cache.
	 * 
	 * @param key The fqn tree to get
	 * @return The cached object, or <code>null</code> if no entry was found
	 */
	public Object get(String fqn);
	
	/**
	 * Gets all values from some given FQN.
	 * 
	 * @param fqn
	 * @return
	 */
	public Collection getValues(String fqn);
	
	/**
	 * Removes an entry from the cache.
	 * 
	 * @param fqn The fully qualified name associated with the key
	 * @param key The key to remove
	 */
	public void remove(String fqn, String key);
	
	/**
	 * Removes a complete node from the cache
	 * @param key The fqn to remove
	 */
	public void remove(String fqn);

	/**
	 * Set A list of child names (as Objects). Returns null of the parent node 
	 * was not found, or if there are no children 
	 * @param fqn The fully qualified name of the node 
	 * @return Returns all children of a given node
	 */
	public Set getChildrenNames(String fqn);
	
	/**
	 * stop the cache engine. 
	 */
	public void stop();
}
