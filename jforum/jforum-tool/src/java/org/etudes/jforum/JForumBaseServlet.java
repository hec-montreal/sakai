/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/JForumBaseServlet.java $ 
 * $Id: JForumBaseServlet.java 55698 2008-12-05 01:21:10Z murthy@etudes.org $ 
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

import java.sql.Connection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;

import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;

/**
 * @author Rafael Steil
 */
public class JForumBaseServlet extends HttpServlet {
    //protected boolean debug;
    private static Log logger = LogFactory.getLog(DataHolder.class);

    // Thread local implementation
    protected static ThreadLocal localData = new ThreadLocal() {
        public Object initialValue() {
            return new DataHolder();
        }
    };

    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        /*try {
        	String appPath = config.getServletContext().getRealPath("");
            debug = "true".equals(config.getInitParameter("development"));

            // Use Sakai's logging configuration -- JMH
//            DOMConfigurator.configure(appPath + "/WEB-INF/log4j.xml");

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
            // Use the SqlService vendor to decide which impl to use -- JMH
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
        }*/
    }

    /*protected void loadConfigStuff() throws Exception {
        ConfigLoader.loadUrlPatterns();
        I18n.load();
		Tpl.load(SystemGlobals.getValue(ConfigKeys.TEMPLATES_MAPPING));

        // BB Code
        BBCodeRepository.setBBCollection(new BBCodeHandler().parse());
    }*/

    /**
     * Sets the <code>Connection</code>, <code>HttpServletRequest</code>
     * and <code>HttpServletResponse</code> for the incoming requisition.
     * As JForum relies on <code>ThreadLocal</code> data, it is necessary,
     * before of the processing of some request, to set the necessary
     * data, so the core classes may have access to request, response
     * and database connections. 
     * 
     * @param dataHolder The filled <code>DataHolder</code> class. 
     */
    public static void setThreadLocalData(DataHolder dataHolder)
    {
    	localData.set(dataHolder);
    }
    
    /**
     * Request information data holder. Stores information/data like the user request and response,
     * his database connection and any other kind of data needed.
     */
    public static class DataHolder {
    	private Connection conn;
        private ActionServletRequest request;
        private HttpServletResponse response;
        private SimpleHash context = new SimpleHash(ObjectWrapper.BEANS_WRAPPER);
        private String redirectTo;
        private String contentType;
        private boolean isBinaryContent;
        private boolean cancelCommit;
        
        public boolean cancelCommit()
        {
        	return this.cancelCommit;
        }
        
        public void enableCancelcommit()
        {
        	this.cancelCommit = true;
        }
        
        // Setters
        public void setConnection(Connection conn) {
            this.conn = conn;
        }

        public void setRequest(ActionServletRequest request) {
            this.request = request;
        }

        public void setResponse(HttpServletResponse response) {
            this.response = response;
        }

        public void setContext(SimpleHash context) {
            this.context = context;
        }

        public void setRedirectTo(String redirectTo) {
            this.redirectTo = redirectTo;
        }
        
        public void setContentType(String contentType) {
        	this.contentType = contentType;
        }
        
        public void enableBinaryContent(boolean enable) {
        	this.isBinaryContent = enable;
        }

        // Getters
        public boolean isBinaryContent() {
        	return this.isBinaryContent;
        }
        
        public String getContentType() {
        	return this.contentType;
        }
        
        public Connection getConnection() {
            return this.conn;
        }

        public ActionServletRequest getRequest() {
            return this.request;
        }

        public HttpServletResponse getResponse() {
            return this.response;
        }

        public SimpleHash getContext() {
            return this.context;
        }

        public String getRedirectTo() {
            return this.redirectTo;
        }
    }

    /**
     * Gets the current thread's connection
     * 
     * @return
     */
    public static Connection getConnection() 
	{
		return getConnection(true);
	}
	
	public static Connection getConnection(boolean validate)
	{
		Connection c =  ((DataHolder)localData.get()).getConnection();
		
		if (validate && c == null) {
			c = DBConnection.getImplementation().getConnection();
			
			// Autocommit is set properly by sakai -- JMH
//			try {
//				c.setAutoCommit(!SystemGlobals.getBoolValue(ConfigKeys.DATABASE_USE_TRANSACTIONS));
//			}
//			catch (Exception e) {}
			
			((DataHolder)localData.get()).setConnection(c);
		}
	    
		return c; 
	}

    /**
     * Gets the current thread's request
     * 
     * @return
     */
    public static ActionServletRequest getRequest() {
        return ((DataHolder) localData.get()).getRequest();
    }

    /**
     * Gets the current thread's response
     * 
     * @return
     */
    public static HttpServletResponse getResponse() {
        return ((DataHolder) localData.get()).getResponse();
    }

    /**
     * Gets the current thread's template context
     * 
     * @return
     */
    public static SimpleHash getContext() {
        return ((DataHolder) localData.get()).getContext();
    }

    /**
     * Gets the current thread's <code>DataHolder</code> instance
     * 
     * @return
     */
    public static void setRedirect(String redirect) {
        ((DataHolder) localData.get()).setRedirectTo(getResponse().encodeRedirectURL(redirect));
    }
	
	public static String getRedirect()
	{
		return ((DataHolder)localData.get()).getRedirectTo();
	}

    /**
     * Sets the content type for the current http response.
     * 
     * @param contentType
     */
    public static void setContentType(String contentType) {
    	((DataHolder)localData.get()).setContentType(contentType);
    }
    
    public static void enableBinaryContent(boolean enable) {
    	((DataHolder)localData.get()).enableBinaryContent(enable);
    }
    
    public static void enableCancelCommit() {
    	((DataHolder)localData.get()).enableCancelcommit();
    }
	
	public static boolean isBinaryContent() {
		return ((DataHolder)localData.get()).isBinaryContent();
	}
    
    public static boolean cancelCommit() {
    	return ((DataHolder)localData.get()).cancelCommit();
    }

    /**
     * prepend the path, append the extension and encode the url
     * 
     * @return 
     */
    public static String encodeUrlWithPathAndExtension(String url) {
        DataHolder dataHolder = (DataHolder) localData.get();
        return dataHolder.getResponse().encodeURL(getRequest().getContextPath() 
					+ url
					+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION));
       	}
    
   
    public void destroy() {
    	ConfigLoader.stopCacheEngine();
    	
    	super.destroy();
    	if (logger.isInfoEnabled()) logger.info("Destroying JForumBaseServlet...");
    }
}