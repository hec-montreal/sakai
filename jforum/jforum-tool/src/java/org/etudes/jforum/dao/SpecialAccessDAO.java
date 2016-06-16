/*********************************************************************************** 
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/SpecialAccessDAO.java $ 
 * $Id: SpecialAccessDAO.java 71014 2010-10-26 20:49:34Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008, 2009, 2010 Etudes, Inc. 
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
 **********************************************************************************/ 

package org.etudes.jforum.dao;

import java.util.List;

import org.etudes.jforum.entities.SpecialAccess;

/**
 * Model interface for {@link org.etudes.jforum.entities.SpecialAccess}. This
 * interface defines methods which are expected to be implemented by a specific
 * data access driver. The intention is to provide all functionality needed to
 * update, insert, delete and select some specific data.
 * 
 * @author Murthy Tanniru
 */
public interface SpecialAccessDAO
{
	/**
	 * Selects special access by id
	 * 
	 * @param specialAccessId	Special access id
	 * @return					Special access
	 * @throws Exception
	 */
	public SpecialAccess selectById(int specialAccessId) throws Exception;
	
	/**
	 * Selects the special access by forum id
	 * @param forumId			Forum id
	 * @return					Special access
	 * @throws Exception
	 */
	public List<SpecialAccess> selectByForumId(int forumId) throws Exception;
	
	/**
	 * Selectest the special access for a site or course
	 * @return					Special access
	 * @throws Exception
	 */
	public List<SpecialAccess> selectBySite() throws Exception;
	
	/**
	 * Gets the forum special access count
	 * @param forumId			Forum id
	 * @return					Count of the special access for the forum 
	 * @throws Exception
	 */
	public int selectForumSpecialAccessCount(int forumId) throws Exception;
	
	/**
	 * add new special access
	 * @param specialAccess		Special access
	 * @return					Special access id
	 * @throws Exception
	 */
	public int addNew(SpecialAccess specialAccess) throws Exception;
	
	/**
	 * Update the existing special access
	 * @param specialAccess		Special access
	 * @throws Exception
	 */
	public void update(SpecialAccess specialAccess) throws Exception;
	
	/**
	 * Deletes the special access
	 * @param specialAccessId	Special access id
	 * @throws Exception
	 */
	public void delete(int specialAccessId) throws Exception;
}
