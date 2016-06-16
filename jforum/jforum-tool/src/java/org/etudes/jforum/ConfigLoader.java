/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/ConfigLoader.java $ 
 * $Id: ConfigLoader.java 67148 2010-04-14 00:24:27Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008, 2009, 2010 Etudes, Inc. 
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
package org.etudes.jforum;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

//import net.jforum.repository.RolesRepository;
//import net.jforum.repository.SecurityRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.cache.CacheEngine;
import org.etudes.jforum.exceptions.CacheEngineStartupException;
import org.etudes.jforum.repository.BBCodeRepository;
import org.etudes.jforum.repository.ForumRepository;
import org.etudes.jforum.repository.ModulesRepository;
import org.etudes.jforum.repository.PostRepository;
import org.etudes.jforum.repository.RankingRepository;
import org.etudes.jforum.repository.SmiliesRepository;
import org.etudes.jforum.repository.TopicRepository;
import org.etudes.jforum.repository.Tpl;
import org.etudes.jforum.util.FileMonitor;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.QueriesFileListener;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobalsListener;
import org.etudes.jforum.util.search.SearchFacade;
import org.etudes.jforum.util.search.quartz.QuartzSearchIndexerJob;
;

/**
 * General utilities methods for loading configurations for JForum.
 * 
 * @author Rafael Steil
 */
public class ConfigLoader 
{
	private static final Log logger = LogFactory.getLog(ConfigLoader.class);
	private static CacheEngine cache;
	
	/**
	 * Start ( or restart ) <code>SystemGlobals</code>.
	 * This method loads all configuration keys set at
	 * <i>SystemGlobals.properties</i>, <i>&lt;user.name&gt;.properties</i>
	 * and database specific stuff.
	 * 
	 * @param appPath The application root's directory
	 * @throws Exception
	 */
	public static void startSystemglobals(String appPath) throws Exception
	{
		SystemGlobals.initGlobals(appPath, appPath + "/WEB-INF/config/SystemGlobals.properties");
		
		// No need to get DB connection info locally -- JMH
//		SystemGlobals.loadAdditionalDefaults(SystemGlobals.getValue(ConfigKeys.DATABASE_DRIVER_CONFIG));
		
//		if (new File(SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG)).exists()) {
//			SystemGlobals.loadAdditionalDefaults(SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG));
//		}
	}

	/**
	 * Loads module mappings for the system.
	 * 
	 * @param baseConfigDir The directory where the file <i>modulesMapping.properties</i> is.
	 * @return The <code>java.util.Properties</code> instance, with the loaded modules 
	 * @throws IOException
	 */
	public static Properties loadModulesMapping(String baseConfigDir) throws IOException
	{
		Properties modulesMapping = new Properties();
		FileInputStream fis = new FileInputStream(baseConfigDir + "/modulesMapping.properties");
		modulesMapping.load(fis);
		fis.close();
		return modulesMapping;
	}
	
	/**
	 * Load url patterns.
	 * The method tries to load url patterns from <i>WEB-INF/config/urlPattern.properties</i>
	 * 
	 * @throws IOException
	 */
	public static void loadUrlPatterns() throws IOException {
		Properties p = new Properties();
		p.load(new FileInputStream(SystemGlobals.getValue(ConfigKeys.CONFIG_DIR)
			+ "/urlPattern.properties"));

		Iterator iter = p.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();

			ActionServletRequest.addUrlPattern((String)entry.getKey(), (String)entry.getValue());
        }
    }
	
	/**
	 * Listen for changes in common configuration files.
	 * The watched files are: <i>generic_queries.sql</i>, 
	 * <i>&lt;database_name&gt;.sql</i>, <i>SystemGlobals.properties</i>
	 * and <i>&lt;user.name&gt;.properties</i>
	 */
	public static void listenForChanges()
	{
		int fileChangesDelay = SystemGlobals.getIntValue(ConfigKeys.FILECHANGES_DELAY);
		
		if (fileChangesDelay > 0) {
			// Queries
			FileMonitor.getInstance().addFileChangeListener(new QueriesFileListener(),
					SystemGlobals.getValue(ConfigKeys.SQL_QUERIES_GENERIC), fileChangesDelay);

			FileMonitor.getInstance().addFileChangeListener(new QueriesFileListener(),
					SystemGlobals.getValue(ConfigKeys.SQL_QUERIES_DRIVER), fileChangesDelay);

			// System Properties
			FileMonitor.getInstance().addFileChangeListener(new SystemGlobalsListener(),
					SystemGlobals.getValue(ConfigKeys.DEFAULT_CONFIG), fileChangesDelay);

			ConfigLoader.listenInstallationConfig();
        }
	}
	
	public static void listenInstallationConfig()
	{
		int fileChangesDelay = SystemGlobals.getIntValue(ConfigKeys.FILECHANGES_DELAY);
		
		if (fileChangesDelay > 0) {
			/*if (new File(SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG)).exists()) {
				FileMonitor.getInstance().addFileChangeListener(new SystemGlobalsListener(),
						SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG), fileChangesDelay);
			}*/
		}
	}
	
	public static void startCacheEngine()
	{
		try {
			//String cacheImplementation = SystemGlobals.getValue(ConfigKeys.CACHE_IMPLEMENTATION);
			String cacheImplementation = SakaiSystemGlobals.getValue(ConfigKeys.CACHE_IMPLEMENTATION);
			if(logger.isDebugEnabled()) logger.debug("Using cache engine: " + cacheImplementation);
			
			cache = (CacheEngine)Class.forName(cacheImplementation).newInstance();
			cache.init();
			
			new BBCodeRepository().setCacheEngine(cache);
			new ModulesRepository().setCacheEngine(cache);
			new RankingRepository().setCacheEngine(cache);
			new SmiliesRepository().setCacheEngine(cache);
			//new SecurityRepository().setCacheEngine(cache);
			new ForumRepository().setCacheEngine(cache);
			new TopicRepository().setCacheEngine(cache);
			new SessionFacade().setCacheEngine(cache);
			new PostRepository().setCacheEngine(cache);
			new QuartzSearchIndexerJob().setCacheEngine(cache);
			new Tpl().setCacheEngine(cache);
			//new RolesRepository().setCacheEngine(cache);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new CacheEngineStartupException("Error while starting the cache engine: " + e);
		}
	}
	
	public static void startSearchIndexer()
	{
		SearchFacade.init();
	}
	
	public static void stopCacheEngine()
	{
		if(logger.isInfoEnabled()) logger.info("stopping cache engine: "+ cache);
		try {
			if (cache != null){
				cache.stop();
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	}

	
}
