/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/EtudesJForumBaseServlet.java $ 
 * $Id: EtudesJForumBaseServlet.java 55884 2008-12-09 20:53:19Z murthy@etudes.org $ 
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
package org.etudes.jforum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.hsqldb.HsqldbDataAccessDriver;
import org.etudes.jforum.dao.mysql.MysqlDataAccessDriver;
import org.etudes.jforum.dao.oracle.OracleDataAccessDriver;
import org.etudes.jforum.exceptions.ForumStartupException;
import org.etudes.jforum.repository.BBCodeRepository;
import org.etudes.jforum.repository.ModulesRepository;
import org.etudes.jforum.repository.Tpl;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.bbcode.BBCodeHandler;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.sakaiproject.authz.cover.FunctionManager;
import org.sakaiproject.db.cover.SqlService;

import freemarker.template.Configuration;

/**
 * <p>
 * SakaiJForumBaseServlet initialize configurations and creates JForum database tables on servlet startup
 * </p>
 * 
 * @author Foothill College
 */
public class EtudesJForumBaseServlet extends HttpServlet {
	protected boolean debug;
	//Logger instance named "SakaiJForumBaseServlet".
	private static Log logger = LogFactory.getLog(EtudesJForumBaseServlet.class);

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		//initialize configurations
		try {
        	String appPath = config.getServletContext().getRealPath("");
            debug = "true".equals(config.getInitParameter("development"));

            // Load system default values
            ConfigLoader.startSystemglobals(appPath);
            
            ConfigLoader.startCacheEngine();

            // Configure the template engine
            Configuration templateCfg = new Configuration();
            templateCfg.setDirectoryForTemplateLoading(new File(SystemGlobals.getApplicationPath()
                    + "/templates"));
            templateCfg.setTemplateUpdateDelay(2);
			templateCfg.setSetting("number_format", "#");

            ModulesRepository.init(SystemGlobals.getValue(ConfigKeys.CONFIG_DIR));

            SystemGlobals.loadQueries(SystemGlobals.getValue(ConfigKeys.SQL_QUERIES_GENERIC));
            SystemGlobals.loadQueries(SystemGlobals.getValue(ConfigKeys.SQL_QUERIES_DRIVER));
            
            // Start the dao.driver implementation
            DataAccessDriver daoImpl = null;
            String vendor = SqlService.getVendor();
            if(vendor.equals("oracle")) {
            	daoImpl = new OracleDataAccessDriver();
            } else if (vendor.equals("mysql")) {
            	daoImpl = new MysqlDataAccessDriver();
            } else if (vendor.equals("hsqldb")) {
            	daoImpl = new HsqldbDataAccessDriver();
            }else {
            	logger.error("Unable to find an appropriate DataAccessDriver for DB vendor " + vendor);
            }
            DataAccessDriver.init(daoImpl);

            this.loadConfigStuff();

            if (!this.debug) {
                templateCfg.setTemplateUpdateDelay(3600);
            }

            ConfigLoader.listenForChanges();
			ConfigLoader.startSearchIndexer();

            Configuration.setDefaultConfiguration(templateCfg);
        } catch (Exception e) {
            throw new ForumStartupException("Error while starting jforum", e);
        }

		//create database tables
		boolean m_autoDdl = config.getInitParameter("autoddl").equals("true");
		
		try {
			// if autoDdl is true create the tables
			if (m_autoDdl) {
				Connection conn = null;

				//Start database
				boolean isDatabaseUp = ForumStartup.startDatabase();
				if (isDatabaseUp) {
					conn = DBConnection.getImplementation().getConnection();

					//create tables and data only one time
					if (!createTablesAndData(conn))
						if (logger.isDebugEnabled()) logger.debug(this.getClass().getName()+".init() : JForum Table's and data already existing in the database");
					else
						if (logger.isDebugEnabled()) logger.debug(this.getClass().getName()+".init() : JForum Table's and data created in the database");
				}

				//Finalize
				if (conn != null) {
					try {
						DBConnection.getImplementation().releaseConnection(conn);
					} catch (Exception e) {
						throw e;
					}
				}
			}
			//register sakai-JForum functions
			registerJForumFunctions();
			
		} catch (Exception e) {
			logger.error(this.getClass().getName() + ".init() : " + e.toString(), e);
		}
	}
	
	protected void loadConfigStuff() throws Exception {
        ConfigLoader.loadUrlPatterns();
        I18n.load();
		Tpl.load(SystemGlobals.getValue(ConfigKeys.TEMPLATES_MAPPING));

        // BB Code
        BBCodeRepository.setBBCollection(new BBCodeHandler().parse());
    }

	/**
	 * register sakai-JForum functions
	 */
	private void registerJForumFunctions() {
		//register JForum functions
		FunctionManager.registerFunction(JForumUserUtil.ROLE_ADMIN);
		FunctionManager.registerFunction(JForumUserUtil.ROLE_FACILITATOR);
		FunctionManager.registerFunction(JForumUserUtil.ROLE_PARTICIPANT);
	}

	/**
	 * creates tables
	 * 
	 * @param conn connection
	 * @return true if tables are created false if tables already in database
	 */
	private boolean createTablesAndData(Connection conn) throws Exception {
		String database = SqlService.getVendor();
		String path = null;
		
		if (database != null && database.trim().length() != 0) {
			if (database.trim().equals("mysql"))
				path = SystemGlobals.getValue(ConfigKeys.CONFIG_DIR)
						+ "/database/mysql";
			else if (database.trim().equals("oracle"))
				path = SystemGlobals.getValue(ConfigKeys.CONFIG_DIR)
						+ "/database/oracle";
			else if (database.trim().equalsIgnoreCase("hsqldb"))
				path = SystemGlobals.getValue(ConfigKeys.CONFIG_DIR)
						+ "/database/HSQLDB";
				
			String tablesql = database + "_db_struct.sql";
			String datasql = database + "_data_dump.sql";
			//create tables
			if (ddl(conn, path + File.separator + tablesql)){
				//create data is tables are created for the first time
				ddl(conn, path + File.separator + datasql);
			}else
				return false;
		}
		return true;
	}

	/**
	 * reads the .sql file and runs the sql statements
	 * @param conn connection
	 * @param resource .sql file
	 * @return true if 
	 */
	public boolean ddl(Connection conn, String resource)throws Exception {
		
		InputStream in = null;
		try {
			//get the resource
			in = new FileInputStream(new File(resource));
			if (in == null) {
				if (logger.isWarnEnabled()) logger.warn(this.getClass().getName()+".ddl() : missing resource: " + resource);
				return false;
			}

			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			try {
				// read the first line, skipping any '--' comment lines
				boolean firstLine = true;
				StringBuffer buf = new StringBuffer();
				for (String line = r.readLine(); line != null; line = r
						.readLine()) {
					line = line.trim();
					if (line.startsWith("--"))
						continue;
					if (line.length() == 0)
						continue;

					// add the line to the buffer
					buf.append(' ');
					buf.append(line);

					// process if the line ends with a ';'
					boolean process = line.endsWith(";");

					if (!process)
						continue;

					// remove trailing ';'
					buf.setLength(buf.length() - 1);

					// run the first sql statement as the test - if it fails, 
					// we can assume tables are existing in the database
					if (firstLine) {
						firstLine = false;
						if (!dbWrite(conn, buf.toString())) {
							if (logger.isDebugEnabled()) logger.debug(this.getClass().getName()+":ddl() JForum Table's are already existing in the database");
							return false;
						}
					}
					// run other lines, until done - any one can fail (we will
					// report it)
					else {
						dbWrite(conn, buf.toString());
					}

					// clear the buffer for next
					buf.setLength(0);
				}
			} catch (IOException ioe) {
				if (logger.isWarnEnabled()) logger.warn(this.getClass().getName()+".ddl() : resource: "+ resource +" : "+ ioe);
				throw ioe;
			} finally {
				try {
					r.close();
				} catch (IOException ioe) {
					if (logger.isWarnEnabled()) logger.warn(this.getClass().getName()+".ddl() : resource: " + resource + " : " + ioe);
					throw ioe;
				}
			}
		} catch (FileNotFoundException e) {
			if (logger.isWarnEnabled()) logger.warn(e);
			throw e;
		} finally {
			try {
				in.close();
			} catch (IOException ioe) {
				if (logger.isWarnEnabled()) logger.warn(this.getClass().getName()+".ddl() : resource: " + resource + " : " + ioe);
				throw ioe;
			}
		}
		return true;
	}

	/**
	 * write to database
	 * 
	 * @param conn
	 *            connection
	 * @param sql
	 *            sql
	 * @return true if database write is success false if database write fails
	 */
	private boolean dbWrite(Connection conn, String sql) {
		if(logger.isInfoEnabled()) if (logger.isDebugEnabled()) logger.debug("JForum issuing SQL: " + sql);
		
		PreparedStatement pstmt = null;
		boolean autoCommit = false;
		boolean resetAutoCommit = false;

		boolean success = false;

		try {
			// make sure we do not have auto commit - will change and reset if
			// needed
			autoCommit = conn.getAutoCommit();
			if (autoCommit) {
				conn.setAutoCommit(false);
				resetAutoCommit = true;
			}

			pstmt = conn.prepareStatement(sql);

			pstmt.executeUpdate();

			if (conn != null) {
				conn.commit();
			}

			// indicate success
			success = true;
		} catch (Exception e) {
			//if (logger.isWarnEnabled()) logger.warn(this.getClass().getName()+".dbWrite(): " + e);
			//e.printStackTrace();
			return false;
		} finally {
			try {
				if (null != pstmt)
					pstmt.close();
				if ((conn != null)) {
					// rollback on failure
					if (!success)
						conn.rollback();

					// reset the autocommit
					if (resetAutoCommit)
						conn.setAutoCommit(autoCommit);
				}
			} catch (Exception e) {
				if (logger.isWarnEnabled()) logger.warn(this.getClass().getName()+".dbWrite(): " + e);
			}
		}
		return true;
	}
}