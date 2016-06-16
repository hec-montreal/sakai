/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-api/src/java/org/etudes/api/app/jforum/JForumBaseDateService.java $ 
 * $Id: JForumBaseDateService.java 70664 2010-10-12 17:18:15Z mallika@etudes.org $ 
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
 ***********************************************************************************/
package org.etudes.api.app.jforum;

import java.util.Date;

public interface JForumBaseDateService
{
	/**
	 * Gets the earliest start date of all the forums 
	 * @param course_id		Course id
	 * @return If start date exists for the forums gets the earliest start date of all the forum else returns null
	 */
	Date getMinStartDate(String course_id);
	
	/**
	 * Apply base date to all forum dates
	 * @param course_id		Course id
	 * @param days_diff		Time difference in days
	 */
	void applyBaseDateTx(String course_id, int days_diff);
}
