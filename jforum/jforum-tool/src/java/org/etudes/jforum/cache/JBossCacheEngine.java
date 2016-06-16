/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/cache/JBossCacheEngine.java $ 
 * $Id: JBossCacheEngine.java 61928 2009-07-21 22:29:16Z murthy@etudes.org $ 
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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.exceptions.CacheException;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.jboss.cache.Fqn;
import org.jboss.cache.Node;
import org.jboss.cache.PropertyConfigurator;
import org.jboss.cache.TreeCache;

/**
 * @author Rafael Steil
 */
public class JBossCacheEngine implements CacheEngine
{
	private Log logger = LogFactory.getLog(JBossCacheEngine.class);
	private TreeCache cache;

	/**
	 * @see org.etudes.jforum.cache.CacheEngine#init()
	 */
	public void init()
	{
		try {
			if(logger.isDebugEnabled()) logger.debug("Creating a new TreeCache");
			this.cache = new TreeCache();
			PropertyConfigurator config = new PropertyConfigurator();
			
			// Read the config file from someplace on the filesystem that doesn't need to be on the classpath
			// File configFile = new File(SystemGlobals.getValue(ConfigKeys.LOCAL_CONFIG_DIR ) + File.separator + SystemGlobals.getValue(ConfigKeys.JBOSS_CACHE_PROPERTIES_FILE));
			File configFile = new File(SakaiSystemGlobals.getValue(ConfigKeys.LOCAL_CONFIG_DIR ) + File.separator + SakaiSystemGlobals.getValue(ConfigKeys.JBOSS_CACHE_PROPERTIES_FILE));
			if(!configFile.exists() || !configFile.canRead()) {
				// There's no overriding cache config location, so use the default location
				configFile = new File(SystemGlobals.getValue(ConfigKeys.CONFIG_DIR) + File.separator + SystemGlobals.getValue(ConfigKeys.DEFAULT_JBOSS_CACHE_PROPERTIES_FILE));
			}
			
			if(logger.isDebugEnabled()) logger.debug("configFile is "+ configFile);
			
			InputStream is = new FileInputStream(configFile);
			config.configure(this.cache, is);
			
			this.cache.startService();
			
			if(logger.isDebugEnabled()) logger.debug("JBossCache mode = " + cache.getCacheMode());
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new CacheException("Error while trying to configure jboss-cache: " + e);
		}
	}

	/**
	 * @see org.etudes.jforum.cache.CacheEngine#add(java.lang.String, java.lang.Object)
	 */
	public void add(String key, Object value)
	{
		this.add(CacheEngine.DUMMY_FQN, key, value);
	}
	
	/**
	 * @see org.etudes.jforum.cache.CacheEngine#add(java.lang.String, java.lang.Object)
	 */
	public void add(String fqn, Map data)
	{
		try {
			this.cache.put(Fqn.fromString(fqn), data);
		} catch (Exception e) {
			throw new CacheException("Error adding a new entry to the cache: " + e);
		}
	}

	/**
	 * @see org.etudes.jforum.cache.CacheEngine#add(java.lang.String, java.lang.String, java.lang.Object)
	 */
	public void add(String fqn, String key, Object value)
	{
		try {
			this.cache.put(Fqn.fromString(fqn), key, value);
		}
		catch (Exception e) {
			throw new CacheException("Error adding a new entry to the cache: " + e);
		}
	}

	/**
	 * @see org.etudes.jforum.cache.CacheEngine#get(java.lang.String, java.lang.String)
	 */
	public Object get(String fqn, String key)
	{
		try {
			return this.cache.get(Fqn.fromString(fqn), key);
		}
		catch (Exception e) {
			throw new CacheException("Error while trying to get an entry from the cache: " + e);
		}
	}

	/**
	 * @see org.etudes.jforum.cache.CacheEngine#get(java.lang.String)
	 */
	public Object get(String fqn)
	{
		try {
			Node node = this.cache.get(Fqn.fromString(fqn));
			if(node != null) {
				return node.getData();
			}
			return null;
		}
		catch (Exception e) {
			throw new CacheException("Error while trying to get an entry from the cache: " + e);
		}
	}
	
	/**
	 * @see org.etudes.jforum.cache.CacheEngine#getValues(java.lang.String)
	 */
	public Collection getValues(String fqn)
	{
		
		Node node = null;
		try {
			node = (Node)cache.get(Fqn.fromString(fqn));
		} catch (org.jboss.cache.CacheException ex) {
			logger.error(ex);
			ex.printStackTrace();
		}
		if (node == null) {
			return new ArrayList();
		}
		
		return node.getData().values();
	}

	/**
	 * @see org.etudes.jforum.cache.CacheEngine#remove(java.lang.String, java.lang.String)
	 */
	public void remove(String fqn, String key)
	{
		try {
			if (key == null) {
				this.cache.remove(Fqn.fromString(fqn));
			}
			else {
				this.cache.remove(Fqn.fromString(fqn), key);
			}
		}
		catch (Exception e) {
			logger.warn("Error while removing a FQN from the cache: " + e);
		}
	}

	/**
	 * @see org.etudes.jforum.cache.CacheEngine#remove(java.lang.String)
	 */
	public void remove(String fqn)
	{
		try {
			this.cache.remove(Fqn.fromString(fqn));
		}
		catch (Exception e) {
			logger.warn("Error while removing a FQN from the cache: " + e);
		}
	}

	/**
	 * @see org.etudes.jforum.cache.CacheEngine#getChildrenNames(java.lang.String)
	 */
	public Set getChildrenNames(String fqn) {
		try {
			return this.cache.getChildrenNames(Fqn.fromString(fqn));
		}
		catch (org.jboss.cache.CacheException e) {
			logger.warn("Error while getting Children Names for a FQN from the cache: " + e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @see org.etudes.jforum.cache.CacheEngine#stop()
	 */
	public void stop() {
		
		try {
			if (this.cache != null){
				this.cache.stopService();
			}
		} catch (RuntimeException e) {
			logger.warn("Error while stopping the jboss cache service: " + e);
			e.printStackTrace();
		}
	}

}
