/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/DBConnection.java $ 
 * $Id: DBConnection.java 55370 2008-11-26 21:57:23Z murthy@etudes.org $ 
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Base class for all database connection implementations that
 * may be used with JForum.
 * Default implementations are <code>PooledConnection</code>, which
 * is the defeault connection pool implementation, and <code>SimpleConnection</code>,
 * which opens a new connection on every request.  
 * 
 * @author Rafael Steil
 */
public abstract class DBConnection 
{
	private static final Log logger = LogFactory.getLog(DBConnection.class);
	protected boolean isDatabaseUp;
	
	private static DBConnection instance;

	/**
	 * Creates an instance of some <code>DBConnection </code>implementation. 
	 * 
	 * @return <code>true</code> if the instance was successfully created, 
	 * or <code>false</code> if some exception was thrown.
	 */
	public static final boolean createInstance()
	{
		try {
			// Always use the datasource connection, since it's provided by sakai -- JMH
			instance = new DataSourceConnection();
			instance.init();
		}
		catch (Exception e) {
			 logger.warn("Error connecting to sakai's datasource. " + e);
			 e.printStackTrace();
			 return false;
		}
		
		return true;
	}
	
	/**
	 * Gets the current <code>DBConnection</code> implementation's instance
	 * 
	 * @return
	 */
	public static DBConnection getImplementation()
	{
		return instance;
	}
	
	/**
	 * Checks if database connection is up.
	 *  
	 * @return <code>true</code> if a connection to the database
	 * was successfully created, or <code>false</code> if not.
	 */
	public boolean isDatabaseUp()
	{
		return this.isDatabaseUp;
	}
	
	/**
	 * Inits the implementation. 
	 * Connection pools may use this method to init the connections from the
	 * database, while non-pooled implementation can provide an empty method
	 * block if no other initialization is necessary.
	 * <br>
	 * Please note that this method will be called just once, at system startup. 
	 * 
	 * @throws Exception
	 */
	public abstract void init() throws Exception;
	
	/**
	 * Gets a connection.
	 * Connection pools' normal behaviour will be to once connection
	 * from the pool, while non-pooled implementations will want to
	 * go to the database and get the connection in time the method
	 * is called.
	 * 
	 * @return
	 * @throws Exception
	 */
	public abstract Connection getConnection();
	
	/**
	 * Releases a connection.
	 * Connection pools will want to put the connection back to the pool list,
	 * while non-pooled implementations should call <code>close()</code> directly
	 * in the connection object.
	 * 
	 * @param conn The connection to release
	 * @throws Exception
	 */
	public abstract void releaseConnection(Connection conn);
	
	/**
	 * Close all open connections.
	 * 
	 * @throws Exception
	 */
	public abstract void realReleaseAllConnections() throws Exception;
}
