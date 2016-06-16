/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/sso/SSOUtils.java $ 
 * $Id: SSOUtils.java 55477 2008-12-01 19:20:25Z murthy@etudes.org $ 
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
package org.etudes.jforum.sso;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.UserDAO;
import org.etudes.jforum.entities.User;

/**
 * General utilities to use with SSO.
 * 
 * @author Rafael Steil
 * rashmim - another register() with 4 args on 08/03/05
 * rashmim - added updateUser() to update user info if different than sakai
 *
 * 8/30/05 - Mallika - Adding method to add new user group
 * 07/17/06 - Murthy - updated for sakai's Eid and user id
 */
public class SSOUtils
{
	private String username;
	private boolean exists = true;
	private User user;
	private UserDAO dao;
	private String sakaiUserId;
		
	private static Log logger = LogFactory.getLog(SSOUtils.class);
	
	/**
	 * Checks if an user exists in the database
	 * 
	 * @param username The username to check
	 * @return <code>true</code> if the user exists. If <code>false</code> is
	 * returned, then you can insert the user by calling {@link #register(String, String)}
	 * @see #register(String, String)
	 * @see #getUser()
	 * @throws Exception
	 */
	public boolean userExists(String username) throws Exception
	{
		this.username = username;
		this.dao = DataAccessDriver.getInstance().newUserDAO();

		this.user = this.dao.selectByName(username);

		this.exists = this.user != null;
		
		return this.exists;
	}
	
	/**
	 * Checks if an user exists in the database
	 * 
	 * @param username The username to check
	 * @return <code>true</code> if the user exists. If <code>false</code> is
	 * returned, then you can insert the user by calling {@link #register(String, String)}
	 * @see #register(String, String)
	 * @see #getUser()
	 * @throws Exception
	 */
	public boolean sakaiUserExists(String sakUserId) throws Exception
	{
		this.sakaiUserId = sakUserId;
		this.dao = DataAccessDriver.getInstance().newUserDAO();

		this.user = this.dao.selectBySakaiUserId(sakUserId);

		this.exists = this.user != null;
		
		return this.exists;
	}
	
	/**
	 * Registers a new user. 
	 * This method should be used together with {@link #userExists(String)}. 
	 * 
	 * @param password the user's password. It <em>should</em> be the real / final 
	 * password. In other words, the data passed as password is the data that'll be
	 * written to the database
	 * @param email the user's email
	 * @see #getUser()
	 * @throws Exception
	 */
	public void register(String password, String email) throws Exception
	{
		if (this.exists) {
			return;
		}
		
		// Is a new user for us. Register him
		this.user = new User();
		user.setUsername(this.username);
		user.setPassword(password);
		user.setEmail(email);
		user.setActive(1);
		user.setSakaiUserId(this.sakaiUserId);
		this.dao.addNew(user);
	}
	
	/**
	 * added by rashmi to add firstname and lname of user
	 * 
	 * FIXME The rampant use of non-threadsafe object instances is confusing and dangerous. Notice that, if userExists is not called on this object before this method, it will return the wrong result.   Refactor to use thread-safe singletons.
	 * 
	 */
	public void register(String password, String email, String fname, String lname) throws Exception
	{
//		The only place this is called is when the user doesn't exist.
//		if (this.exists) {
//			return;
//		}
		
		// Is a new user for us. Register him
		this.user = new User();
		user.setUsername(this.username);
		user.setPassword(password);
		user.setEmail(email);
		user.setActive(1);
		user.setFirstName(fname);
		user.setLastName(lname);
		user.setSakaiUserId(this.sakaiUserId);
		this.dao.addNew(user);
	}
	
	/**
	*reister the new user
	*/
	public void register(User user) throws Exception
	{
		this.user = user;
		this.dao.addNew(user);
	}
	
	/**
	 * Gets the user associated to this class instance.
	 * 
	 * @return the user
	 */
	public User getUser()
	{
		return this.user;
	}
	
	//added by rashmi to update user info if different than sakai
	public void UpdateUser(User usr) throws Exception
	{
		this.user = usr;
		this.dao.update(user);	
	//	System.out.println("update user in SSOUtils" + user.getEmail());
	}
	
	//New code beg - Mallika
	/*public void addToGroup(int userId, int groupId) throws Exception
	{
		try{
			this.dao.addToGroup(userId, new int[]{groupId});
		} 
		catch(Exception e)
		{
			//System.out.println("ERROR!!! error in adding user to a group based on sakai role permissions");
			logger.error(this.getClass().getName() + 
					".addToGroup() : " + e.getMessage(), e);
			throw e;
		}
	}*/
	//New code end - Mallika
}
