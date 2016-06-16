/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/UserSessionDAO.java $ 
 * $Id: UserSessionDAO.java 55486 2008-12-01 22:06:47Z murthy@etudes.org $ 
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
package org.etudes.jforum.dao;

import java.sql.Connection;

import org.etudes.jforum.entities.UserSession;


/**
 * @author Rafael Steil
 */
public interface UserSessionDAO
{
	/**
	 * Writes a new <code>UserSession</code> to the database.
	 * 
	 * @param us The <code>UserSession</code> to store
	 * @param conn The {@link java.sql.Connection} object to use. 
	 * As many times user session management will be done in places where 
	 * a valid request is not available, we cannot try to retrieve the 
	 * conneciton from the thread local implementation. <br>
	 * If any driver implementation of this method will not use a database
	 * ( eg, where a <code>Connection</code> is not required ), when just
	 * pass <code>null</code> as argument.
	 * @throws Exception
	 */
	public void add(UserSession us, Connection conn) throws Exception;
	
	/**
	 * Updates an <code>UserSession</code> 
	 * 
	 * @param us The <code>UserSession</code> to update
	 * @param conn The {@link java.sql.Connection} object to use. 
	 * As many times user session management will be done in places where 
	 * a valid request is not available, we cannot try to retrieve the 
	 * conneciton from the thread local implementation. <br>
	 * If any driver implementation of this method will not use a database
	 * ( eg, where a <code>Connection</code> is not required ), when just
	 * pass <code>null</code> as argument.

	 * @throws Exception
	 */
	public void update(UserSession us, Connection conn) throws Exception;
	
	/**
	 * Gets an <code>UserSession</code> from the database.
	 * The object passed as argument should at least have the user id 
	 * in order to find the correct register. 
	 * 
	 * @param us The complete <code>UserSession</code> object data
	 * @param conn The {@link java.sql.Connection} object to use. 
	 * As many times user session management will be done in places where 
	 * a valid request is not available, we cannot try to retrieve the 
	 * conneciton from the thread local implementation. <br>
	 * If any driver implementation of this method will not use a database
	 * ( eg, where a <code>Connection</code> is not required ), when just
	 * pass <code>null</code> as argument.
	 * 
	 * @return 
	 */
	public UserSession selectById(UserSession us, Connection conn) throws Exception;
}
