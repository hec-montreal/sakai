/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/GenericUserSessionDAO.java $ 
 * $Id: GenericUserSessionDAO.java 55493 2008-12-01 22:55:48Z murthy@etudes.org $ 
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
package org.etudes.jforum.dao.generic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import org.etudes.jforum.entities.UserSession;
import org.etudes.jforum.util.preferences.SystemGlobals;


/**
 * @author Rafael Steil
 */
public class GenericUserSessionDAO implements org.etudes.jforum.dao.UserSessionDAO
{
	/** 
	 * @see org.etudes.jforum.dao.UserSessionDAO#add(org.etudes.jforum.entities.UserSession, java.sql.Connection)
	 */
	public void add(UserSession us, Connection conn) throws Exception
	{
		this.add(us, conn, false);
	}
	
	private void add(UserSession us, Connection conn, boolean checked) throws Exception
	{
		if (!checked && this.selectById(us, conn) != null) {
			return;
		}
		
		PreparedStatement p = conn.prepareStatement(SystemGlobals.getSql("UserSessionModel.add"));
		p.setString(1, us.getSessionId());
		p.setInt(2, us.getUserId());
		p.setTimestamp(3, new Timestamp(us.getStartTime().getTime()));
		
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see org.etudes.jforum.dao.UserSessionDAO#update(org.etudes.jforum.entities.UserSession, java.sql.Connection)
	 */
	public void update(UserSession us, Connection conn) throws Exception
	{
		if (this.selectById(us, conn) == null) {
			this.add(us, conn, true);
			return;
		}
		
		PreparedStatement p = conn.prepareStatement(SystemGlobals.getSql("UserSessionModel.update"));
		p.setTimestamp(1, new Timestamp(us.getStartTime().getTime()));
		p.setLong(2, us.getSessionTime());
		p.setString(3, us.getSessionId());
		p.setInt(4, us.getUserId());
		
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see org.etudes.jforum.dao.UserSessionDAO#selectById(org.etudes.jforum.entities.UserSession, java.sql.Connection)
	 */
	public UserSession selectById(UserSession us, Connection conn) throws Exception
	{
		PreparedStatement p = conn.prepareStatement(SystemGlobals.getSql("UserSessionModel.selectById"));
		p.setInt(1, us.getUserId());
		
		ResultSet rs = p.executeQuery();
		boolean found = false;
		
		UserSession returnUs = new UserSession(us);
		
		if (rs.next()) {
			returnUs.setSessionTime(rs.getLong("session_time"));
			returnUs.setStartTime(rs.getTimestamp("session_start"));
			found = true;
		}
		
		rs.close();
		p.close();
		
		return (found ? returnUs : null);
	}

}
