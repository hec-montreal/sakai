/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/GenericConfigDAO.java $ 
 * $Id: GenericConfigDAO.java 55493 2008-12-01 22:55:48Z murthy@etudes.org $ 
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.etudes.jforum.JForum;
import org.etudes.jforum.entities.Config;
import org.etudes.jforum.util.preferences.SystemGlobals;


/**
 * @author Rafael Steil
 */
public class GenericConfigDAO implements org.etudes.jforum.dao.ConfigDAO
{
	/**
	 * @see org.etudes.jforum.dao.ConfigDAO#insert(org.etudes.jforum.entities.Config)
	 */
	public void insert(Config config) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ConfigModel.insert"));
		p.setString(1, config.getName());
		p.setString(2, config.getValue());
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.ConfigDAO#update(org.etudes.jforum.entities.Config)
	 */
	public void update(Config config) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ConfigModel.update"));
		p.setString(1, config.getValue());
		p.setString(2, config.getName());
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.ConfigDAO#delete(org.etudes.jforum.entities.Config)
	 */
	public void delete(Config config) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ConfigModel.delete"));
		p.setInt(1, config.getId());
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.ConfigDAO#selectAll()
	 */
	public List selectAll() throws Exception
	{
		List l = new ArrayList();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ConfigModel.selectAll"));
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			l.add(this.makeConfig(rs));
		}
		
		rs.close();
		p.close();
		
		return l;
	}

	/**
	 * @see org.etudes.jforum.dao.ConfigDAO#selectByName(java.lang.String)
	 */
	public Config selectByName(String name) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("ConfigModel.selectByName"));
		p.setString(1, name);
		ResultSet rs = p.executeQuery();
		Config c = null;
		
		if (rs.next()) {
			c = this.makeConfig(rs);
		}
		
		rs.close();
		p.close();
		
		return c;
	}
	
	protected Config makeConfig(ResultSet rs) throws Exception
	{
		Config c = new Config();
		c.setId(rs.getInt("config_id"));
		c.setName(rs.getString("config_name"));
		c.setValue(rs.getString("config_value"));
		
		return c;
	}
}
