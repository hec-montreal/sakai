/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/forum/common/BannerCommon.java $ 
 * $Id: BannerCommon.java 55473 2008-12-01 18:49:13Z murthy@etudes.org $ 
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
package org.etudes.jforum.view.forum.common;

import java.util.List;
import java.util.Random;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.dao.BannerDAO;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.entities.Banner;

/**
 * @author Samuel Yung
 */
public class BannerCommon
{
	private static Log logger = LogFactory.getLog(BannerCommon.class);

	private BannerDAO dao;
	private List banners;

	public BannerCommon()
	{
		this.dao = DataAccessDriver.getInstance().newBannerDAO();
	}

    /**
     * Check whether the banner will be displayed based on user rights and
     * banner filter settings.
     * @return boolean
     * @throws Exception
     */
    public boolean canBannerDisplay(int bannerId) throws Exception
	{
		boolean result = true;

                // todo

		return result;
	}

	/**
	 * Test whether any active banner exist at the placement indicated.
	 * @param placement int
	 * @return boolean
	 * @throws Exception
	 */
	public boolean isActiveBannerExist(int placement) throws Exception
	{
		banners = dao.selectActiveBannerByPlacement(placement);
		if (banners == null || banners.isEmpty())
		{
			return false;
		}
		
		return true;
	}

	/**
	 * Retrieves the correct banner based on weight. Before calling this
	 * function the isBannerExist(int placement) must be called. The total
	 * weight for all the same position banners should be equal to 99. If
	 * the total weight is smaller than 99 and the random number is larger
	 * than the total weight of all the same position banners, the highest
	 * weight's banner will be chosen. After a correct banner is found, its
	 * views variable will be incremented by 1.
	 *
	 * @param placement int
	 * @return Banner
	 * @throws Exception
	 * @see #isBannerExist(int)
	 */
	public Banner getBanner() throws Exception
	{
		Banner result = null;

		if (banners == null || banners.isEmpty())
		{
			return null;
		}

		// get correct banner based on weight
		int r = (new Random().nextInt(99));
		int weightFrom = 0;
		int weightTo = 0;
		for(int i = 0; i < banners.size(); i++)
		{
			result = (Banner)banners.get(i);
			weightTo += result.getWeight();
			if (r >= weightFrom && r < weightTo)
			{
				break;
			}
			weightFrom = weightTo;
		}

		// increment views by 1
		result.setViews(result.getViews() + 1);
		dao.update(result);

		return result;
	}
}
