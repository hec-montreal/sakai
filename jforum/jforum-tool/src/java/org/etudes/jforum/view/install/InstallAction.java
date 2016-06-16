/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/install/InstallAction.java $ 
 * $Id: InstallAction.java 61928 2009-07-21 22:29:16Z murthy@etudes.org $ 
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
package org.etudes.jforum.view.install;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.ActionServletRequest;
import org.etudes.jforum.Command;
import org.etudes.jforum.ConfigLoader;
import org.etudes.jforum.DBConnection;
import org.etudes.jforum.DataSourceConnection;
import org.etudes.jforum.InstallServlet;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.entities.UserSession;
import org.etudes.jforum.util.FileMonitor;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.MD5;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobalsListener;
import org.sakaiproject.db.cover.SqlService;

import freemarker.template.SimpleHash;
import freemarker.template.Template;

/**
 * @author Rafael Steil
 */
public class InstallAction extends Command
{
	private static Log logger = LogFactory.getLog(InstallAction.class);
	
	public void welcome() throws Exception
	{
		this.checkLanguage();
		
		this.context.put("language", this.getFromSession("language"));
		this.context.put("database", this.getFromSession("database"));
		this.context.put("dbhost", this.getFromSession("dbHost"));
		this.context.put("dbuser", this.getFromSession("dbUser"));
		this.context.put("dbname", this.getFromSession("dbName"));
		this.context.put("dbpasswd", this.getFromSession("dbPassword"));
		this.context.put("dbencoding", this.getFromSession("dbEncoding"));
		this.context.put("use_pool", this.getFromSession("usePool"));
		this.context.put("forumLink", this.getFromSession("forumLink"));
		this.context.put("siteLink", this.getFromSession("siteLink"));
		this.context.put("dbdatasource", this.getFromSession("dbdatasource"));
		
		this.context.put("moduleAction", "install.htm");
	}
	
	private void checkLanguage() throws IOException
	{
		String lang = this.request.getParameter("l");
		if (lang == null || !I18n.languageExists(lang)) {
			return;
		}
		
		I18n.load(lang);
		
		UserSession us = new UserSession();
		us.setLang(lang);
		
		SessionFacade.add(us);
		this.addToSessionAndContext("language", lang);
	}
	
	private String getFromSession(String key)
	{
		return (String)this.request.getSession().getAttribute(key);
	}
	
	private void error()
	{
		this.context.put("moduleAction", "install_error.htm");
	}
	
	public void doInstall() throws Exception
	{
		Connection conn = null;
		
		if (!this.checkForWritableDir()) {
			return;
		}
		
		this.removeUserConfig();
		
		if (!"passed".equals(this.getFromSession("configureDatabase"))) {
			logger.info("Going to configure the database...");
			conn = this.configureDatabase();
			if (conn == null) {
				this.context.put("message", I18n.getMessage("Install.databaseError"));
				this.error();
				return;
			}
		}
		
		logger.info("Database configuration ok");

		// Database Configuration is ok
		this.addToSessionAndContext("configureDatabase", "passed");
		
		// Sakai never uses this kind of connection -- JMH
//		DBConnection simpleConnection = new SimpleConnection();
//		if (conn == null) {
//			conn = simpleConnection.getConnection();
//		}
		
		// Sakai uses a datasource
		DBConnection dsConnection = new DataSourceConnection();
		
		if (!"passed".equals(this.getFromSession("createTables")) && !this.createTables(conn)) {
			this.context.put("message", I18n.getMessage("Install.createTablesError"));
			dsConnection.releaseConnection(conn);
			this.error();
			return;
		}
		
		// Create tables is ok
		this.addToSessionAndContext("createTables", "passed");
		logger.info("Table creation is ok");
		
		if (!"passed".equals(this.getFromSession("importTablesData")) && !this.importTablesData(conn)) {
			this.context.put("message", I18n.getMessage("Install.importTablesDataError"));
			dsConnection.releaseConnection(conn);
			this.error();
			return;
		}
		
		// Dump is ok
		this.addToSessionAndContext("importTablesData", "passed");
		
		if (!this.updateAdminPassword(conn)) {
			this.context.put("message", I18n.getMessage("Install.updateAdminError"));
			dsConnection.releaseConnection(conn);
			this.error();
			return;
		}
		
		dsConnection.releaseConnection(conn);

		InstallServlet.setRedirect(this.request.getContextPath() + "/install/install"
				+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION)
				+ "?module=install&action=finished");
	}
	
	private void removeUserConfig()
	{
		/*File f = new File(SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG));
		if (f.exists() && f.canWrite()) {
			try {
				f.delete();
			}
			catch (Exception e) {
				logger.info(e.toString());
			}
		}*/
	}
	
	public void finished() throws Exception
	{
		this.context.put("clickHere", I18n.getMessage("Install.clickHere"));
		this.context.put("forumLink", this.getFromSession("forumLink"));
		this.context.put("moduleAction", "install_finished.htm");
		
		String lang = this.getFromSession("language");
		if (lang == null) {
			lang = "en_US";
		}
		
		this.context.put("lang", lang);
		
		this.doFinalSteps();
		this.configureSystemGlobals();

		SystemGlobals.loadQueries(SystemGlobals.getValue(ConfigKeys.SQL_QUERIES_GENERIC));
        SystemGlobals.loadQueries(SystemGlobals.getValue(ConfigKeys.SQL_QUERIES_DRIVER));
        
        SessionFacade.remove(this.request.getSession().getId());
	}
	
	private void doFinalSteps()
	{
		try {
			// Modules Mapping
			String modulesMapping = SystemGlobals.getValue(ConfigKeys.CONFIG_DIR) + "/modulesMapping.properties";
			if (new File(modulesMapping).canWrite()) {
				Properties p = new Properties();
				p.load(new FileInputStream(modulesMapping));
				
				if (p.containsKey("install")) {
					p.remove("install");
					
					p.store(new FileOutputStream(modulesMapping), "Modified by JForum Installer");
					
					this.addToSessionAndContext("mappingFixed", "true");
					ConfigLoader.loadModulesMapping(SystemGlobals.getValue(ConfigKeys.CONFIG_DIR));
				}
			}
		}
		catch (Exception e) {
			logger.warn("Error while working on modulesMapping.properties: " + e);
		}
		
		
		try {
			// Index renaming
			String index = SystemGlobals.getApplicationPath() + "/index.htm";
			File indexFile = new File(index);
			if (indexFile.canWrite()) {
				String newIndex = SystemGlobals.getApplicationPath() + "/new_rename.htm";
				File newIndexFile = new File(newIndex);
				if (newIndexFile.exists()) {
					indexFile.delete();
					newIndexFile.renameTo(indexFile);
					
					this.addToSessionAndContext("indexFixed", "true");
				}
			}
		}
		catch (Exception e) {
			logger.warn("Error while renaming index.htm: " + e);
		}
	}
	
	private void configureSystemGlobals() throws Exception
	{
		SystemGlobals.setValue(ConfigKeys.USER_HASH_SEQUENCE, MD5.crypt(this.getFromSession("dbPassword")
				+ System.currentTimeMillis()));

		SystemGlobals.setValue(ConfigKeys.FORUM_LINK, this.getFromSession("forumLink"));
		SystemGlobals.setValue(ConfigKeys.HOMEPAGE_LINK, this.getFromSession("siteLink"));
		// SystemGlobals.setValue(ConfigKeys.I18N_DEFAULT, this.getFromSession("language"));
		SystemGlobals.setValue(ConfigKeys.INSTALLED, "true");
		SystemGlobals.saveInstallation();
		
		this.restartSystemGlobals();
	}
	
	private boolean importTablesData(Connection conn) throws Exception
	{
		boolean status = true;
		boolean autoCommit = conn.getAutoCommit();
		conn.setAutoCommit(false);
		
		String dbType = this.getFromSession("database");
		
		if (dbType.startsWith("mysql")) {
			dbType = "mysql";
		}
		
		List statements = this.readFromDat(SystemGlobals.getApplicationResourceDir() 
				+ "/setup-files/" 
				+ dbType + "_dump.dat");
		for (Iterator iter = statements.iterator(); iter.hasNext();) {
			String query = (String)iter.next();
			if (query == null || "".equals(query.trim())) {
				continue;
			}
			
			query = query.trim();
			
			Statement s = conn.createStatement();
			
			try {
				if (query.startsWith("UPDATE") || query.startsWith("INSERT")
						|| query.startsWith("SET")) {
					s.executeUpdate(query);
				}
				else if (query.startsWith("SELECT")) {
					s.executeQuery(query);
				}
				else {
					throw new Exception("Invalid query: " + query);
				}
			}
			catch (SQLException ex) {
				status = false;
				conn.rollback();
				logger.error("Error importing data for " + query + ": " + ex);
				this.context.put("exceptionMessage", ex.getMessage() + "\n" + query);
				break;
			}
			finally {
				s.close();
			}
		}
		
		conn.setAutoCommit(autoCommit);
		return status;
	}
		
	private boolean createTables(Connection conn) throws Exception
	{
		logger.info("Going to create tables...");
		String dbType = this.getFromSession("database");
		
		if ("postgresql".equals(dbType)) {
			this.dropPostgresqlTables(conn);
		}
		else if (dbType.startsWith("mysql")) {
			dbType = "mysql";
		}
		
		boolean status = true;
		List statements = this.readFromDat(SystemGlobals.getApplicationPath() + "/install/" + dbType + "_structure.dat");
		for (Iterator iter = statements.iterator(); iter.hasNext(); ) {
			String query = (String)iter.next();
			if (query == null || "".equals(query.trim())) {
				continue;
			}
			
			Statement s = conn.createStatement();
			
			try {
				s.executeUpdate(query);
			}
			catch (SQLException ex) {
				status = false;

				logger.error("Error executing query: " + query + ": " + ex);
				this.context.put("exceptionMessage", ex.getMessage() + "\n" + query);
				
				break;
			}
			finally {
				s.close();
			}
		}
		
		return status;
	}
	
	private boolean checkForWritableDir()
	{
		boolean canWriteToWebInf = this.canWriteToWebInf();
		boolean canWriteToIndex = this.canWriteToIndex();
		
		if (!canWriteToWebInf || !canWriteToIndex) {
			this.context.put("message", I18n.getMessage("Install.noWritePermission"));
			this.context.put("tryAgain", true);
			this.error();
			return false;
		}

		return true;
	}
	
	private boolean canWriteToWebInf()
	{
		return new File(SystemGlobals.getValue(ConfigKeys.CONFIG_DIR) + "/modulesMapping.properties").canWrite();
	}
	
	
	private boolean canWriteToIndex()
	{
		return new File(SystemGlobals.getApplicationPath() + "/index.htm").canWrite();
	}
	

	
	private void copyFile(String from, String to) throws Exception
	{
		FileChannel source = new FileInputStream(new File(from)).getChannel();
		FileChannel dest =  new FileOutputStream(new File(to)).getChannel();
		
		source.transferTo(0, source.size(), dest);
		source.close();
		dest.close();
	}
	
	private Connection configureDatabase() throws Exception
	{
		// Respect the DB vendor defined in sakai -- JMH
		String database =SqlService.getVendor();
		
		if (database.startsWith("mysql")) {
			if ("mysql41".equals(database)) {
				String path = SystemGlobals.getValue(ConfigKeys.CONFIG_DIR) + "/database/mysql";
				
				this.copyFile(path + "/mysql_41.sql", path + "/mysql_41-bkp.sql");
				
				new File(path + "/mysql.sql").delete();
				new File(path + "/mysql_41.sql").renameTo(new File(path + "/mysql.sql"));
				
				this.copyFile(path + "/mysql_41-bkp.sql", path + "/mysql_41.sql");
			}
			
			database = "mysql";
		}
		
		// These values are never read -- JMH
//		SystemGlobals.setValue(ConfigKeys.DATABASE_CONNECTION_IMPLEMENTATION, implementation);
//		SystemGlobals.setValue(ConfigKeys.DATABASE_DRIVER_NAME, database);
		
		SystemGlobals.saveInstallation();
		this.restartSystemGlobals();
		
		int fileChangesDelay = SystemGlobals.getIntValue(ConfigKeys.FILECHANGES_DELAY);
		if (fileChangesDelay > 0) {
			/*FileMonitor.getInstance().addFileChangeListener(new SystemGlobalsListener(),
					SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG), fileChangesDelay);*/
		}
		
		Connection conn = null;
		
		try {
			// Always use the datasource provided by sakai -- JMH
			DBConnection s =  new DataSourceConnection();
			s.init();
			
			conn = s.getConnection();
		}
		catch (Exception e) {
			logger.warn("Error while trying to get a connection: " + e);
			this.context.put("exceptionMessage", e.getMessage());
			return null;
		}
		
		return conn;
	}
	
	private void restartSystemGlobals() throws Exception
	{
		String appPath = SystemGlobals.getApplicationPath();
		SystemGlobals.initGlobals(appPath, appPath + "/WEB-INF/config/SystemGlobals.properties");
		
		// There's no need for hot-swap DB config, since it isn't supported in Sakai -- JMH
        //SystemGlobals.loadAdditionalDefaults(SystemGlobals.getValue(ConfigKeys.DATABASE_DRIVER_CONFIG));
        
        /*if (new File(SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG)).exists()) {
            SystemGlobals.loadAdditionalDefaults(SystemGlobals.getValue(ConfigKeys.INSTALLATION_CONFIG));
        }*/
	}
	
	private boolean updateAdminPassword(Connection conn) throws Exception
	{
		logger.info("Going to update the administrator's password");
		
		boolean status = false;
		
		try {
			PreparedStatement p = conn.prepareStatement("UPDATE jforum_users SET user_password = ? WHERE username = 'Admin'");
			p.setString(1, MD5.crypt(this.getFromSession("adminPassword")));
			p.executeUpdate();
			p.close();
			
			status = true;
		}
		catch (Exception e) {
			logger.warn("Error while trying to update the administrator's password: " + e);
			this.context.put("exceptionMessage", e.getMessage());
		}
		
		return status;
	}
	
	public void checkInformation() throws Exception
	{
		String language = this.request.getParameter("language");
		String database = this.request.getParameter("database");
		String dbHost = this.request.getParameter("dbhost");
		String dbUser = this.request.getParameter("dbuser");
		String dbName = this.request.getParameter("dbname");
		String dbPassword = this.request.getParameter("dbpasswd");
		String dbEncoding = this.request.getParameter("dbencoding");
		String dbEncodingOther = this.request.getParameter("dbencoding_other");
		String usePool = this.request.getParameter("use_pool");
		String forumLink = this.request.getParameter("forum_link");
		String adminPassword = this.request.getParameter("admin_pass1");
		
		dbHost = this.notNullDefault(dbHost, "localhost");
		dbEncodingOther = this.notNullDefault(dbEncodingOther, "utf-8");
		dbEncoding = this.notNullDefault(dbEncoding, dbEncodingOther);
		forumLink = this.notNullDefault(forumLink, "http://localhost");
		dbName = this.notNullDefault(dbName, "jforum");
		
		if ("hsqldb".equals(database)) {
			dbUser = this.notNullDefault(dbUser, "sa");
		}
		
		this.addToSessionAndContext("language", language);
		this.addToSessionAndContext("database", database);
		this.addToSessionAndContext("dbHost", dbHost);
		this.addToSessionAndContext("dbUser", dbUser);
		this.addToSessionAndContext("dbName", dbName);
		this.addToSessionAndContext("dbPassword", dbPassword);
		this.addToSessionAndContext("dbEncoding", dbEncoding);
		this.addToSessionAndContext("usePool", usePool);
		this.addToSessionAndContext("forumLink", forumLink);
		this.addToSessionAndContext("siteLink", this.request.getParameter("site_link"));
		this.addToSessionAndContext("adminPassword", adminPassword);
		this.addToSessionAndContext("dbdatasource", this.request.getParameter("dbdatasource"));
		this.addToSessionAndContext("db_connection_type", this.request.getParameter("db_connection_type"));
		
		this.addToSessionAndContext("configureDatabase", null);
		this.addToSessionAndContext("createTables", null);
		this.addToSessionAndContext("importTablesData", null);
		
		this.context.put("canWriteToWebInf", this.canWriteToWebInf());
		this.context.put("canWriteToIndex", this.canWriteToIndex());
		
		this.context.put("moduleAction", "install_check_info.htm");
	}
	
	private List readFromDat(String filename) throws Exception
	{
		List l = new ArrayList();
		
		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream in = new ObjectInputStream(fis);
		l = (ArrayList)in.readObject();
		in.close();
		
		return l;
	}
	
	private void dropPostgresqlTables(Connection conn) throws Exception
	{
		String[] tables = { "jforum_banlist", "jforum_banlist_seq", "jforum_categories", 
				"jforum_categories_order_seq", "jforum_categories_seq", "jforum_config",
				"jforum_config_seq", "jforum_forums", "jforum_forums_seq", "jforum_groups",
				"jforum_groups_seq", "jforum_posts", "jforum_posts_seq", "jforum_posts_text",
				"jforum_privmsgs", "jforum_privmsgs_seq", "jforum_privmsgs_text",
				"jforum_ranks", "jforum_ranks_seq", "jforum_role_values", "jforum_roles",
				"jforum_roles_seq", "jforum_search_results", "jforum_search_topics",
				"jforum_search_wordmatch", "jforum_search_words", "jforum_search_words_seq", "jforum_sessions",
				"jforum_smilies", "jforum_smilies_seq", "jforum_themes", "jforum_themes_seq",
				"jforum_topics", "jforum_topics_seq", "jforum_topics_watch", "jforum_user_groups",
				"jforum_users", "jforum_users_seq", "jforum_vote_desc", "jforum_vote_desc_seq",
				"jforum_vote_results", "jforum_vote_voters", "jforum_words", "jforum_words_seq",
				"jforum_karma_seq", "jforum_karma", "jforum_bookmarks_seq", "jforum_bookmarks", 
				"jforum_quota_limit", "jforum_quota_limit_seq", "jforum_extension_groups_seq", 
				"jforum_extension_groups", "jforum_extensions_seq", "jforum_extensions", 
				"jforum_attach_seq", "jforum_attach", "jforum_attach_desc_seq", "jforum_attach_desc",
				"jforum_attach_quota_seq", "jforum_attach_quota" };

		for (int i = 0; i < tables.length; i++) {
			Statement s = conn.createStatement();
			String query = tables[i].endsWith("_seq") ? "DROP SEQUENCE " : "DROP TABLE ";
			query += tables[i];
			
			try {
				s.executeUpdate(query);
			}
			catch (SQLException e) {
				logger.info("IGNORE: " + e.getMessage());
			}
			
			s.close();
		}
	}
	
	private void addToSessionAndContext(String key, String value)
	{
		this.request.getSession().setAttribute(key, value);
		this.context.put(key, value);
	}
	
	private String notNullDefault(String value, String useDefault)
	{
		if (value == null || value.trim().equals("")) {
			return useDefault;
		}
		
		return value;
	}
	
	/** 
	 * @see org.etudes.jforum.Command#list()
	 */
	public void list() throws Exception
	{
		this.welcome();
	}
	
	/** 
	 * @see org.etudes.jforum.Command#process()
	 */
	public Template process(ActionServletRequest request, 
			HttpServletResponse response, 
			SimpleHash context) throws Exception 
	{
		this.setTemplateName("default/empty.htm");
		return super.process(request, response, context);
	}
}
