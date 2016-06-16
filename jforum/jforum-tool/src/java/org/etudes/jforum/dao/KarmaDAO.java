/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/KarmaDAO.java $ 
 * $Id: KarmaDAO.java 55486 2008-12-01 22:06:47Z murthy@etudes.org $ 
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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.etudes.jforum.entities.Karma;
import org.etudes.jforum.entities.KarmaStatus;
import org.etudes.jforum.entities.User;


/**
 * @author Rafael Steil
 */
public interface KarmaDAO
{
	/**
	 * Insert a new Karma.
	 * 
	 * @param karma The karma to add. The instance should at
	 * least have set the karma status, the user who is receiving
	 * the karma and the user which is setting the karme.
	 * @throws Exception
	 */
	public void addKarma(Karma karma) throws Exception;
	
	/**
	 * Gets the karma status of some user.
	 * 
	 * @param userId The user id to get the karma status
	 * @return A <code>net.jforum.entities.KarmaStatus</code> instance
	 */
	public KarmaStatus getUserKarma(int userId) throws Exception;
	
	/**
	 * Updates the karma status for some user. 
	 * This method will store the user's karme in the
	 * users table. 
	 * 
	 * @param userId The id of the user to update
	 * @throws Exception
	 */
	public void updateUserKarma(int userId) throws Exception;
	
	/**
	 * Checks if the user can add the karma.
	 * The method will search for existing entries in
	 * the karma table associated with the user id and post id
	 * passed as argument. If found, it means that the user 
	 * already has voted, so we cannot allow him to vote one
	 * more time.
	 * 
	 * @param userId The user id to check
	 * @param postId The post id to chekc
	 * @return <code>true</code> if the user hasn't voted on the
	 * post yet, or <code>false</code> otherwise. 
	 * @throws Exception
	 */
	public boolean userCanAddKarma(int userId, int postId) throws Exception;
	
	/**
	 * Gets the karma status of some post.
	 * 
	 * @param postId The post id to get the karma status
	 * @return A <code>net.jforum.entities.KarmaStatus</code> instance
	 * @throws Exception
	 */
	public KarmaStatus getPostKarma(int postId) throws Exception;
	
	/**
	 * Updates a karma
	 * @param karma The karma instance to update
	 */
	public void update(Karma karma) throws Exception;
	
	/**
	 * Gets the votes the user made on some topic.
	 * @param topicId The topic id.
	 * @param userId TODO
	 * 
	 * @return A <code>java.util.Map</code>, where the key is the post id and the
	 * value id the rate made by the user.
	 * @throws Exception
	 */
	public Map getUserVotes(int topicId, int userId) throws Exception;
	
	/**
	 * @param user
	 * @throws Exception
	 */
	public void getUserTotalKarma(User user) throws Exception;
	
	
	/**
	 * Total points received, grouped by user and filtered by a range of dates.
	 * 
	 * @param firstPeriod
	 * @param lastPeriod
	 * @return Returns a List of users ant your total votes.
	 * @throws Exception
	 */
	public List getMostRatedUserByPeriod(int start, Date firstPeriod, Date lastPeriod, String orderField) throws Exception;
}
