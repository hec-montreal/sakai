/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/UserDAO.java $ 
 * $Id: UserDAO.java 69087 2010-07-07 17:17:00Z murthy@etudes.org $ 
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

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.etudes.jforum.entities.User;


/**
  * Model interface for {@link net.jforum.User}.
 * This interface defines methods which are expected to be
 * implementd by a specific data access driver. The intention is
 * to provide all functionality needed to update, insert, delete and
 * select some specific data.
 * 
 * @author Rafael Steil
 * 10/6/05 - Mallika - adding method signature	
 * 	10/11/05 - rashmi - add a method to get the stat# of messages posted by a user
 * 11/03/05 - Murthy - Added addNewUser() method to add a new user but not to the group
 * 07/17/06 - Murthy - added method to get user based on sakai user id
 * 10/3/06 - Mallika - added method for selectUserById
 */
public interface UserDAO 
{
	/**
	 * Gets a specific <code>User</code>.
	 * 
	 * @param userId The User ID to search
	 * @return <code>User</code>object containing all the information
	 * @throws Exception
	 * @see #selectAll
	 */
	public User selectById(int userId) throws Exception;
	
	//New method for member listing
	public User selectUserById(int userId) throws Exception; 
	/**
	 * Gets a specific <code>User</code>.
	 * 
	 * @param username The User name to search
	 * @return <code>User</code> object containing all the information
	 * or <code>null</code> if no data was found. 
	 * @throws Exception
	 * @see #selectAll
	 */
	public User selectByName(String username) throws Exception;
	
	/**
	 * Gets a specific <code>User</code>.
	 * 
	 * @param sakUserId The sakai User Id to search
	 * @return <code>User</code> object containing all the information
	 * or <code>null</code> if no data was found. 
	 * @throws Exception
	 * @see #selectAll
	 */
	public User selectBySakaiUserId(String sakUserId) throws Exception;
	
	/**
	 * Gets all users
	 * 
	 * @return <code>ArrayList</code> with the users. Each entry is an <code>User</code> object
	 * @throws Exception
	 */
	public List selectAll() throws Exception;
	
	/**
	 * Gets all users with your Karma.
	 * 
	 * @return <code>List</code> with the users. Each entry is an <code>User</code> object
	 * (with the KarmaStatus populated).
	 * @throws Exception
	 */
	public List selectAllWithKarma() throws Exception;
	
	/**
	 * Gets all users from a specific group.
	 * 
	 * @param groupId The group id
	 * @param start The index position to start fetching
	 * @param count The total number of records to fetch
	 * @return <code>List</code> with the users. Each entry is an <code>User</code> object
	 * @throws Exception
	 */
	//public List selectAllByGroup(int groupId, int start, int count) throws Exception; 
	
	/**
	 * Gets all users
	 *
	 * @param startFrom Index to start fetching from
	 * @param count Number of records to retrieve
	 * @return <code>ArrayList</code> with the users. Each entry is an <code>User</code> object
	 * (with the KarmaStatus populated).
	 * @throws Exception
	 */
	public List selectAllWithKarma(int startFrom, int count) throws Exception;
	
	/**
	 * Finds an user by matching an input string. 
	 * 
	 * @param input The username to search. May be part of the username. 
	 * The method will match all users who have the input string as 
	 * part of their usernames.
	 * @param exactMath Set to <code>true</code> to get the user data related to 
	 * the username passed as argument, and set it to <code>false</code> to 
	 * search all users who match the criteria. 
	 * @return <code>List</code> with the found users. Each entry is an 
	 * <code>User</code> object, where only the <i>id</i> and <i>username</i>
	 * members are filled.
	 * @throws Exception exact
	 */
	public List findByName(String input, boolean exactMath) throws Exception;

	//Mallika's new code beg
	public List findByFlName(String input, boolean exactMatch) throws Exception;
	//Mallika's new code end
	
	/**
	 * Gets all users
	 *
	 * @param startFrom Index to start fetching from
	 * @param count Number of records to retrieve
	 * @return <code>ArrayList</code> with the users. Each entry is an <code>User</code> object
	 * @throws Exception
	 */
	public List selectAll(int startFrom, int count) throws Exception;
	
	/**
	 * Deletes an user.
	 * 
	 * @param userId The user ID to delete
	 * @throws Exception
	 * @see #undelete(int)
	 */
	public void delete(int userId) throws Exception;
	
	/**
	 * Undeletes an user.
	 * The system allows user undeletation because when you 
	 * call {@link #delete(int)} the user isn't fisically deleted of the
	 * database, but just marked as deleted. This is done to ensure
	 * data integrity.
	 * 
	 * @param userId The user ID to undelete
	 * @throws Exception
	 * @see #delete(int)
	 */
	public void undelete(int userId) throws Exception;
	
	/**
	 * Updates a user.
	 * 
	 * @param user Reference to a <code>User</code> object to update
	 * @throws Exception
	 * @see #update(int)
	 */
	public void update(User user) throws Exception;
	
	/**
	 * Updates a user with sakai account details
	 * 
	 * @param user Reference to a <code>User</code> object to update
	 * @throws Exception
	 */
	public void updateSakaiAccountDetails(User user) throws Exception;
	
	/**
	 * Adds a new User.
	 * After successfuly persisting the data, this method
	 * <b>shoud</b> call <code>user.setId(theNewId);</code>, as well
	 * return the new user id. 
	 * @param user Reference to a valid and configured <code>User</code> object
	 * @return The new user id
	 * @throws Exception
	 */
	public int addNew(User user) throws Exception;
	
	/**
	 * Adds a new user but not to the group
	 * @param user user Reference to a valid and configured <code>User</code> object
	 * @return The new user id 
	 * @throws Exception
	 */
	public int addNewUser(User user) throws Exception;

	/**
	 * Adds a new user with a predefined user id
	 * 
	 * (added by Pieter for external login support)
	 * @param user Reference to a valid and configured <code>User</code> object, with the user id already set
	 * @throws Exception
	 */
	public void addNewWithId(User user) throws Exception ;
	
	/**
	 * Set the active status.
	 * An user with the active status equals to false cannot be considered
	 * a "oficial", "fully registered" user until its status is set to true. This is
	 * interesting when you want to request user confirmation about registrations,
	 * for example
	 * 
	 * @param userId The user ID to change the status
	 * @param active <code>true</code> or <code>false</code>
	 * @throws Exception
	 */
	public void setActive(int userId, boolean active) throws Exception;
	
	/**
	 * Sets the ranking.
	 * 
	 * @param userId The user ID
	 * @throws Exception
	 */
	public void setRanking(int userId, int rankingId) throws Exception;
	
	/**
	 * Increments the number of posts of the user.
	 * 
	 * @param userId The user ID to increment the number of posts
	 * @throws Exception
	 */
	public void incrementPosts(int userId) throws Exception;
	
	/**
	 * Decrement the number of posts of some user.
	 * 
	 * @param userId The user ID do decrement the number of posts.
	 * @throws Exception
	 */
	public void decrementPosts(int userId) throws Exception;
	
	/**
	 * Gest some piece of information of the last user registered
	 * 
	 * @return <code>HashMap</code> containing the information. The map
	 * has two keys:<br>
	 * <li><b>userName</b>: The username
	 * <li><b>userId</b>: The user's ID 
	 * @throws Exception
	 */
	public User getLastUserInfo() throws Exception;
	
	/**
	 * Gets the total number of users
	 * 
	 * @return The total number of users
	 * @throws Exception
	 */
	public int getTotalUsers() throws Exception;
	
	/**
	 * Gets the total number of users of some group.
	 * 
	 * @param groupId The group id
	 * @return The total number of users
	 * @throws Exception
	 */
	//public int getTotalUsersByGroup(int groupId) throws Exception;
	
	/**
	 * whether the user is locked or not.
	 * 
	 * @return boolean
	 * @throws Exception
	 */	
	public boolean isDeleted(int user_id) throws Exception;
	
	/***
	 * Checks the existence of some username.
	 * This method is used to ensure that will not be two equal usernames in the database.
	 * 
	 * @param username The username to verify
	 * @return <code>true</code> or <code>false</code>, if the user was found or not, respectively
	 * @throws Exception
	 */
	public boolean isUsernameRegistered(String username) throws Exception;
	
	/**
	 * Validates the user login.
	 * 
	 * @param username The username
	 * @param password The password
	 * @return The user object if the provided information was corret, <code>null</code> if the information was invalid 
	 * @throws Exception
	 */
	public User validateLogin(String username, String password) throws NoSuchAlgorithmException, Exception;
	
	/**
	 * Associate the user to the group
	 * 
	 * @param userId The user id 
	 * @param groupId The group id to associate to
	 * @throws Exception
	 */
	//public void addToGroup(int userId, int[] groupId) throws Exception;
	
	/**
	 * Remove the user from the group
	 * 
	 * @param userId The user id
	 * @param groupId The group id to remove the user from
	 */
	//public void removeFromGroup(int userId, int[] groupId) throws Exception;
	
	/**
	 * Add the user to the site
	 * @param site_id Site id
	 * @param userId The user id 
	 * @throws Exception
	 */
	public void addToSite(int userId) throws Exception;
	
	/**
	 * Remove the user to the site
	 * @param site_id Site id
	 * @param userId The user id 
	 * @throws Exception
	 */
	public void removeFromSite(int userId) throws Exception;
	
	/**
	 * Gets all site users
	 * 
	 * @return <code>ArrayList</code> with the users. Each entry is an <code>User</code> object
	 * @throws Exception
	 */
	public List selectAllSiteUsers() throws Exception;
	
	/**
	 * Gets all site users
	 *
	 * @param startFrom Index to start fetching from
	 * @param count Number of records to retrieve
	 * @return <code>ArrayList</code> with the users. Each entry is an <code>User</code> object
	 * @throws Exception
	 */
	public List selectAllSiteUsers(int startFrom, int count, boolean getTotalPosts) throws Exception;
	
	/**
	 * Gets all site users with messages posted count 
	 * 
	 * @return <code>ArrayList</code> with the users. Each entry is an <code>User</code> object
	 * @throws Exception
	 */
	public List selectAllSiteUsersWithTotalPosts() throws Exception;
	
	/**
	 * Stores the "lost password" security hash, that was generated
	 * when the user asked the system to get a reminder of his password. 
	 * This hash is used to ensure the information supplied.  
	 * 
	 * @param email The user email
	 * @param hash The hash to store.
	 * @throws Exception
	 */
	public void writeLostPasswordHash(String email, String hash) throws Exception;
	
	/**
	 * Validate the provided security hash against the data stored in our system.
	 * 
	 * @param email The user email
	 * @param hash The supplied security hash
	 * @return <code>true</code> if the data matches ok, of <code>false</code> if it is invalid
	 * @throws Exception
	 */
	public boolean validateLostPasswordHash(String email, String hash) throws Exception;
	
	/**
	 * Writes a new password for the user. 
	 * 
	 * @param password The new password
	 * @param email The user email
	 * @throws Exception
	 */
	public void saveNewPassword(String password, String email) throws Exception;
	
	/**
	 * Gets the username related to the email
	 * 
	 * @param email The email to search for the username
	 * @return The username, if found, or an empty <code>String</code>
	 * @throws Exception
	 */
	public String getUsernameByEmail(String email) throws Exception;
	
	/**
	 * Validate if the activated key matches the one in the database
	 * 
	 * @param userId Which user to validate the activation key?
	 * @param hash The activation key
	 * @return <code>true</code> if the data matches ok, of <code>false</code> if it is invalid
	 * @throws Exception
	 */
	public boolean validateActivationKeyHash(int userId , String hash) throws Exception;

	/**
	 * Set user account to active
	 * 
	 * @param userId Which user account to set active?
	 * @throws Exception
	 */	
	public void writeUserActive(int userId) throws Exception;
	
	/**
	 * Updates only the username. 
	 * This method generally will be used in implementations
	 * of <code>net.jforum.drivers.external.LoginAuthenticator</code> to 
	 * update usernames which changed in the external source and therefore
	 * should be updated in jforum's users table. 
	 * 
	 * @param userId The user's id related to the username to update
	 * @param username The new username to write
	 * @throws Exception
	 */
	public void updateUsername(int userId, String username) throws Exception;
	
	/**
	 * Check if the username passed as argument is different of
	 * the username existent in the database. 
	 * 
	 * @param userId The user's id to work with
	 * @param usernameToCheck The username to compare with the existing
	 * one in <i>jforum_users</i>
	 * @return <code>true</code> if the usernames are different.
	 * @throws Exception
	 */
	public boolean hasUsernameChanged(int userId, String usernameToCheck) throws Exception;
	
	/*
	 * get the stat# of messages posted by a user at a specific site 
	 */
	public int getNumberOfMessages(int userId) throws Exception;
	
}
