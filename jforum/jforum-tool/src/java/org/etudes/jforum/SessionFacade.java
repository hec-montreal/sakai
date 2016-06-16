/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/SessionFacade.java $ 
 * $Id: SessionFacade.java 55368 2008-11-26 21:46:52Z murthy@etudes.org $ 
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

//import net.jforum.repository.SecurityRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.cache.CacheEngine;
import org.etudes.jforum.cache.Cacheable;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.entities.UserSession;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 */
public class SessionFacade implements Cacheable
{
	private static final Log logger = LogFactory.getLog(SessionFacade.class);
	
	private static final String FQN = "sessions";
	private static final String FQN_LOGGED = "logged";
	private static final String FQN_COUNT = "count";
	private static final String ANONYMOUS_COUNT = "anonymousCount";
	private static final String LOGGED_COUNT = "loggedCount";
	
	private static CacheEngine cache;

	/**
	 * @see org.etudes.jforum.cache.Cacheable#setCacheEngine(org.etudes.jforum.cache.CacheEngine)
	 */
	public void setCacheEngine(CacheEngine engine)
	{
		cache = engine;
	}
	
	/**
	 * Add a new <code>UserSession</code> entry to the session.
	 * This method will make a call to <code>JForum.getRequest.getSession().getId()</code>
	 * to retrieve the session's id
	 * 
	 * @param us The user session objetc to add
	 * @see #add(UserSession, String)
	 */
	public static void add(UserSession us)
	{
		add(us, JForum.getRequest().getSession().getId());
	}

	/**
	 * Registers a new {@link UserSession}.
	 * <p>
	 * If a call to {@link UserSession#getUserId()} return a value different 
	 * of <code>SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)</code>, then 
	 * the user will be registered as "logged". Otherwise it will enter as anonymous.
	 * </p>
	 * 
	 * <p>
	 * Please note that, in order to keep the number of guest and logged users correct, 
	 * it's caller's responsability to {@link #remove(String)} the record before adding it
	 * again if the current session is currently represented as "guest". 
	 * </p>
	 *  
	 * @param us the UserSession to add
	 * @param sessionId the user's session id
	 */
	public static void add(UserSession us, String sessionId)
	{
		if (us.getSessionId() == null || us.getSessionId().equals("")) {
			us.setSessionId(sessionId);
		}
		
		synchronized (FQN) {
			cache.add(FQN, us.getSessionId(), us);
			
			if (!us.isBot()) {
				if (us.getUserId() != SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)) {
					changeUserCount(LOGGED_COUNT, true);
					cache.add(FQN_LOGGED, us.getSessionId(), us);
				}
				else {
					// TODO: check the anonymous IP constraint
					changeUserCount(ANONYMOUS_COUNT, true);
				}
			}
		}
	}
	
	private static void changeUserCount(String cacheEntryName, boolean increment)
	{
		Integer count = (Integer)cache.get(FQN_COUNT, cacheEntryName);
		
		if (count == null) {
			count = new Integer(0);
		}
		
		if (increment) {
			count = new Integer(count.intValue() + 1);
		}
		else if (count.intValue() > 0) {
			count = new Integer(count.intValue() - 1);
		}
		
		cache.add(FQN_COUNT, cacheEntryName, count);
	}
	
	/**
	 * Add a new entry to the user's session
	 * 
	 * @param name The attribute name
	 * @param value The attribute value
	 */
	public static void setAttribute(String name, Object value)
	{
		JForum.getRequest().getSession().setAttribute(name, value);
	}
	
	/**
	 * Removes an attribute from the session
	 * 
	 * @param name The key associated to the the attribute to remove
	 */
	public static void removeAttribute(String name)
	{
		JForum.getRequest().getSession().removeAttribute(name);
	}
	
	/**
	 * Gets an attribute value given its name
	 * 
	 * @param name The attribute name to retrieve the value
	 * @return The value as an Object, or null if no entry was found
	 */
	public static Object getAttribute(String name)
	{
		return JForum.getRequest().getSession().getAttribute(name);
	}

	/**
	 * Remove an entry fro the session map
	 * 
	 * @param sessionId The session id to remove
	 */
	public static void remove(String sessionId)
	{
		if (logger.isDebugEnabled()) logger.debug("Removing session " + sessionId);
		
		synchronized (FQN) {
			UserSession us = getUserSession(sessionId);
			
			if (us != null) {
				cache.remove(FQN_LOGGED, sessionId);
				
				if (us.getUserId() != SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)) {
					changeUserCount(LOGGED_COUNT, false);
				}
				else {
					changeUserCount(ANONYMOUS_COUNT, false);
				}
			}
			
			cache.remove(FQN, sessionId);
		}
	}
	
	/**
	 * Get all registered sessions
	 * 
	 * @return <code>ArrayList</code> with the sessions. Each entry
	 * is an <code>UserSession</code> object.
	 */
	public static List getAllSessions()
	{
		synchronized (FQN) {
			return new ArrayList(cache.getValues(FQN));
		}
	}
	
	/**
	 * Gets the {@link UserSession} instance of all logged users
	 * @return A list with the user sessions
	 */
	public static List getLoggedSessions()
	{
		synchronized (FQN) {
			return new ArrayList(cache.getValues(FQN_LOGGED));
		}
	}
	
	/**
	 * Get the number of logged users
	 * @return the number of logged users
	 */
	public static int registeredSize()
	{
		Integer count = (Integer)cache.get(FQN_COUNT, LOGGED_COUNT);

		return (count == null ? 0 : count.intValue());
	}
	
	/**
	 * Get the number of anonymous users
	 * @return the nuber of anonymous users
	 */
	public static int anonymousSize()
	{
		Integer count = (Integer)cache.get(FQN_COUNT, ANONYMOUS_COUNT);

		return (count == null ? 0 : count.intValue());
	}
	
	public static void clear()
	{
		synchronized (FQN) {
			cache.remove(FQN);
			cache.add(FQN, new HashMap());
			
			cache.remove(FQN_COUNT, LOGGED_COUNT);
			cache.add(FQN_COUNT, LOGGED_COUNT, new Integer(0));
			
			cache.remove(FQN_COUNT, ANONYMOUS_COUNT);
			cache.add(FQN_COUNT, ANONYMOUS_COUNT, new Integer(0));
		}
	}
	
	/**
	 * Gets the user's <code>UserSession</code> object
	 * 
	 * @return The <code>UserSession</code> associated to the user's session
	 */
	public static UserSession getUserSession()
	{
		return getUserSession(JForum.getRequest().getSession().getId());
	}
	
	/**
	 * Gets an {@link UserSession} by the session id.
	 * 
	 * @param sessionId the session's id
	 * @return an <b>immutable</b> UserSession, or <code>null</code> if no entry found
	 */
	public static UserSession getUserSession(String sessionId)
	{
		UserSession us = (UserSession)cache.get(FQN, sessionId);
		return us;
	}

	/**
	 * Gets the number of session elements.
	 * 
	 * @return The number of session elements currently online (without bots)
	 */
	public static int size()
	{
		//return (anonymousSize() + registeredSize());
		return (getAllSessions().size());
	}
	
	/**
	 * Verify if the user in already loaded
	 * 
	 * @param username The username to check
	 * @return The session id if the user is already registered into the session, 
	 * or <code>null</code> if it is not.
	 */
	public static String isUserInSession(String username)
	{
		int aid = SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID);
		
		synchronized (FQN) {
			for (Iterator iter = cache.getValues(FQN).iterator(); iter.hasNext(); ) {
				UserSession us = (UserSession)iter.next();
				String thisUsername = us.getUsername();
				
				if (thisUsername == null) {
					continue;
				}
				
				if (us.getUserId() != aid && thisUsername.equals(username)) {
					return us.getSessionId();
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Verify if there is an user in the session with the 
	 * user id passed as parameter.
	 * 
	 * @param userId The user id to check for existance in the session
	 * @return The session id if the user is already registered into the session, 
	 * or <code>null</code> if it is not.
	 */
	public static String isUserInSession(int userId)
	{
		int aid = SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID);
		
		synchronized (FQN) {
			for (Iterator iter = cache.getValues(FQN).iterator(); iter.hasNext(); ) {
				UserSession us = (UserSession)iter.next();
				
				if (us.getUserId() != aid && us.getUserId() == userId) {
					return us.getSessionId();
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Verify is the user is logged in.
	 * 
	 * @return <code>true</code> if the user is logged, or <code>false</code> if is 
	 * an anonymous user.
	 */
	public static boolean isLogged()
	{
		return "1".equals(SessionFacade.getAttribute("logged"));
	}

	/**
	 * Persists user session information.
	 * This method will get a <code>Connection</code> making a call to
	 * <code>DBConnection.getImplementation().getConnection()</code>, and
	 * then releasing the connection after the method is processed.   
	 * 
	 * @param sessionId The session which we're going to persist information
	 * @throws Exception
	 * @see #storeSessionData(String, Connection)
	 */
	public static void storeSessionData(String sessionId) throws Exception
	{
		Connection conn = null;
		try {
			conn = DBConnection.getImplementation().getConnection();
			SessionFacade.storeSessionData(sessionId, conn);
		}
		finally {
			if (conn != null) {
				try {
					DBConnection.getImplementation().releaseConnection(conn);
				}
				catch (Exception e) {
					logger.warn("Error while releasing a connection: " + e);
				}
			}
		}
	}

	/**
	 * Persists user session information.
	 * 
	 * @param sessionId The session which we're going to persist
	 * @param conn A <code>Connection</code> to be used to connect to
	 * the database. 
	 * @throws Exception
	 * @see #storeSessionData(String)
	 */
	public static void storeSessionData(String sessionId, Connection conn) throws Exception
	{
		UserSession us = SessionFacade.getUserSession(sessionId);
		if (us != null) {
			try {
				if (us.getUserId() != SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)) {
					DataAccessDriver.getInstance().newUserSessionDAO().update(us, conn);
				}
				
				//SecurityRepository.remove(us.getUserId());
			}
			catch (Exception e) {
				logger.warn("Error storing user session data: " + e, e);
			}
		}
	}
}
