/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/repository/RankingRepository.java $ 
 * $Id: RankingRepository.java 55479 2008-12-01 19:47:26Z murthy@etudes.org $ 
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
package org.etudes.jforum.repository;

import java.util.Iterator;
import java.util.List;

import org.etudes.jforum.cache.CacheEngine;
import org.etudes.jforum.cache.Cacheable;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.RankingDAO;
import org.etudes.jforum.entities.Ranking;
import org.etudes.jforum.exceptions.RankingLoadException;


/**
 * @author Rafael Steil
 */
public class RankingRepository implements Cacheable
{
	private static CacheEngine cache;
	private static final String FQN = "ranking";
	private static final String ENTRIES = "entries";

	/**
	 * @see org.etudes.jforum.cache.Cacheable#setCacheEngine(org.etudes.jforum.cache.CacheEngine)
	 */
	public void setCacheEngine(CacheEngine engine)
	{
		cache = engine;
	}
	
	public static void loadRanks()
	{
		try {
			RankingDAO rm = DataAccessDriver.getInstance().newRankingDAO();
			cache.add(FQN, ENTRIES, rm.selectAll());
		}
		catch (Exception e) {
			throw new RankingLoadException("Error while loading the rankings: " + e);
		}
	}
	
	public static int size()
	{
		if (cache.get(FQN, ENTRIES) == null)
			loadRanks();

		return ((List)cache.get(FQN, ENTRIES)).size();
	}
	
	/**
	 * Gets the title associated to total of messages the user have
	 * @param total Number of messages the user have. The ranking will be
	 * returned according to the range to which this total belongs to. 
	 * @return String with the ranking title. 
	 */	
	public static String getRankTitle(int total) 
	{
		Ranking lastRank = new Ranking();
		List entries = (List)cache.get(FQN, ENTRIES);
		
		if (entries == null)
			loadRanks();
		
		entries = (List)cache.get(FQN, ENTRIES);
		
		for (Iterator iter = entries.iterator(); iter.hasNext(); ) {
			Ranking r = (Ranking)iter.next();
			
			if (total == r.getMin()) {
				return r.getTitle();
			}
			else if (total > lastRank.getMin() && total < r.getMin()) {
				return lastRank.getTitle();
			}
			
			lastRank = r;
		}
		
		return lastRank.getTitle();
	}

}
