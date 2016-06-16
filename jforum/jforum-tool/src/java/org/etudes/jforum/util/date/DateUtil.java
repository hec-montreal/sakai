/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/date/DateUtil.java $ 
 * $Id: DateUtil.java 69315 2010-07-20 18:33:35Z murthy@etudes.org $ 
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

package org.etudes.jforum.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.user.JForumUserUtil;

public class DateUtil
{
	private static Log logger = LogFactory.getLog(JForumUserUtil.class);
	
	private DateUtil()
	{
	}

	/**
	 * parse the date string to date
	 * 
	 * @param date
	 * @return the date from the parsed date string
	 * @throws ParseException
	 */
	static public Date getDateFromString(String date) throws ParseException
	{
		Date endDate;
		try
		{
			if ((date == null) || (date.trim().length() == 0))
				return null;
			
			SimpleDateFormat sdf = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.CALENDAR_DATE_TIME_FORMAT));
			endDate = sdf.parse(date);
		} 
		catch (ParseException e)
		{
			if (logger.isWarnEnabled())
			{
				logger.warn("getDateFromString() : " + "Error occurred while parsing the date for '" + date + "'", e);
			}
			throw e;
		}
		return endDate;
	}

}
