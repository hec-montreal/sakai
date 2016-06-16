/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/PrivateMessageDAO.java $ 
 * $Id: PrivateMessageDAO.java 60357 2009-05-11 21:24:02Z murthy@etudes.org $ 
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

import java.util.List;

import org.etudes.jforum.entities.PrivateMessage;
import org.etudes.jforum.entities.User;


/**
 * @author Rafael Steil
 * 9/26/05 - Mallika - adding method to get count of new messages 
 */
public interface PrivateMessageDAO
{
	/**
	 * Send a new <code>PrivateMessage</code>
	 * 
	 * @param pm The pm to add
	 * @throws Exception
	 */
	public void send(PrivateMessage pm) throws Exception;
	
	/**
	 * save a new <code>PrivateMessage</code> in user sent box and user in box
	 * 
	 * @param pm The pm to add
	 * @param attachmentIds[] attachment Id's
	 * @throws Exception
	 */
	public void saveMessage(PrivateMessage pm, int attachmentIds[]) throws Exception;
		
	/**
	 * Deletes a collection of private messages.
	 * Each instance should at least have the private message
	 * id and the owner user id.
	 * 
	 * @param pm
	 * @throws Exception
	 */
	public void delete(PrivateMessage[] pm) throws Exception;
	
	/**
	 * Update the type of some private message.
	 * You should pass as argument a <code>PrivateMessage</code> instance
	 * with the pm's id and the new message status. There is no need to
	 * fill the other members.
	 * 
	 * @param pm The instance to update 
	 * @throws Exception
	 */
	public void updateType(PrivateMessage pm) throws Exception;
	
	/**
	 * Update the Flag To Follow-up of some private message.
	 * You should pass as argument a <code>PrivateMessage</code> instance
	 * with the pm's id and the new follow up status. There is no need to
	 * fill the other members.
	 * 
	 * @param pm The instance to update 
	 * @throws Exception
	 */
	public void updateFlagToFollowup(PrivateMessage pm) throws Exception;
	
	/**
	 * Update the replied status of some private message.
	 * You should pass as argument a <code>PrivateMessage</code> instance
	 * with the pm's id and the replied status. There is no need to
	 * fill the other members.
	 * 
	 * @param pm The instance to update 
	 * @throws Exception
	 */
	public void updateRepliedStatus(PrivateMessage pm) throws Exception;	
	
	/**
	 * Selects all messages from the user's inbox. 
	 * 
	 * @param user The user to fetch the messages
	 * @return A <code>List</code> with all messages found. Each 
	 * entry is a <code>PrivateMessage</code> entry.
	 * @throws Exception
	 */
	public List selectFromInbox(User user) throws Exception;
	
	/**
	 * Selects all messages from the user's sent box. 
	 * 
	 * @param user The user to fetch the messages
	 * @return A <code>List</code> with all messages found. Each 
	 * entry is a <code>PrivateMessage</code> entry.
	 * @throws Exception
	 */
	public List selectFromSent(User user) throws Exception;
	
	/**
	 * Gets a <code>PrivateMessage</code> by its id.
	 * 
	 * @param pm A <code>PrivateMessage</code> instance containing the pm's id
	 * to retrieve
	 * @return The pm contents
	 * @throws Exception
	 */
	public PrivateMessage selectById(PrivateMessage pm) throws Exception;
	
	//Mallika new code beg
	public int selectUnreadCount(int userId) throws Exception;
	//Mallika new code end
}
