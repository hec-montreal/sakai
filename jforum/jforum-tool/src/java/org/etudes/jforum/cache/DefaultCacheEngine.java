/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/cache/DefaultCacheEngine.java $ 
 * $Id: DefaultCacheEngine.java 55498 2008-12-01 23:54:49Z murthy@etudes.org $ 
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Rafael Steil
 */
public class DefaultCacheEngine implements CacheEngine
{
	private Map cache = new HashMap();
	
	/**
	 * @see org.etudes.jforum.cache.CacheEngine#add(java.lang.String, java.lang.Object)
	 */
	public void add(String key, Object value)
	{
		this.cache.put(key, value);
	}
	
	/**
	 * @see org.etudes.jforum.cache.CacheEngine#add(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void add(String fqn, String key, Object value)
	{
		Map m = (Map)this.cache.get(fqn);
		if (m == null) {
			m = new HashMap();
		}

		m.put(key, value);
		this.cache.put(fqn, m);
	}
	
	/**
	 * @see org.etudes.jforum.cache.CacheEngine#get(java.lang.String, java.lang.String)
	 */
	public Object get(String fqn, String key)
	{
		Map m = (Map)this.cache.get(fqn);
		if (m == null) {
			return null;
		}
		
		return m.get(key);
	}
	
	/**
	 * @see org.etudes.jforum.cache.CacheEngine#get(java.lang.String)
	 */
	public Object get(String fqn)
	{
		return this.cache.get(fqn);
	}
	
	/**
	 * @see org.etudes.jforum.cache.CacheEngine#getValues(java.lang.String)
	 */
	public Collection getValues(String fqn)
	{
		Map m = (Map)this.cache.get(fqn);
		if (m == null) {
			return new ArrayList();
		}
		
		return m.values();
	}
	
	/**
	 * @see org.etudes.jforum.cache.CacheEngine#init()
	 */
	public void init()
	{
		this.cache = new HashMap();
	}
	
	/**
	 * @see org.etudes.jforum.cache.CacheEngine#remove(java.lang.String, java.lang.String)
	 */
	public void remove(String fqn, String key)
	{
		Map m = (Map)this.cache.get(fqn);
		if (m != null) {
			m.remove(key);
		}
	}
	
	/**
	 * @see org.etudes.jforum.cache.CacheEngine#remove(java.lang.String)
	 */
	public void remove(String fqn)
	{
		this.cache.remove(fqn);
	}

	public void add(String fqn, Map data) {
		
		this.cache.put(fqn, data);	
	}

	/**
	 * @see org.etudes.jforum.cache.CacheEngine#getChildrenNames(java.lang.String)
	 */
	public Set getChildrenNames(String fqn) {
		Map m = (Map)this.cache.get(fqn);
		if (m != null)
			return m.keySet();
		
		return null;
	}

	public void stop() {
		this.cache = null;
	}
}
