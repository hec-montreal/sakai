/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/tool/RFC1123Date.java $
 * $Id: RFC1123Date.java 45722 2008-02-19 11:56:15Z ian@caret.cam.ac.uk $
 ***********************************************************************************
 *
 * Copyright (c) 2008 Timefields Ltd
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/

package org.sakaiproject.sdata.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * TODO Javadoc
 * 
 * @author ieb
 */
public class RFC1123Date
{

	private static final Date DEFAULT_TWO_DIGIT_YEAR_START;

	static
	{
		Calendar calendar = Calendar.getInstance();
		calendar.set(2000, Calendar.JANUARY, 1, 0, 0);
		DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
	}

	private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

	/**
	 * Date format pattern used to parse HTTP date headers in RFC 1123 format.
	 */
	public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HHmmss zzz";

	/**
	 * TODO Javadoc
	 * 
	 * @param dateValue
	 * @return
	 * @throws ParseException
	 */
	public static Date parseDate(String dateValue) throws ParseException
	{

		if (dateValue == null)
		{
			throw new IllegalArgumentException("dateValue is null");
		}
		Date startDate = DEFAULT_TWO_DIGIT_YEAR_START;
		// trim single quotes around date if present
		// see issue #5279
		if (dateValue.length() > 1 && dateValue.startsWith("'")
				&& dateValue.endsWith("'"))
		{
			dateValue = dateValue.substring(1, dateValue.length() - 1);
		}

		SimpleDateFormat dateParser = null;
		dateParser = new SimpleDateFormat(PATTERN_RFC1123, Locale.US);
		dateParser.setTimeZone(TimeZone.getTimeZone("GMT"));
		dateParser.set2DigitYearStart(startDate);
		return dateParser.parse(dateValue);
	}

	/**
	 * TODO Javadoc
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(Date date)
	{
		if (date == null) throw new IllegalArgumentException("date is null");

		SimpleDateFormat formatter = new SimpleDateFormat(PATTERN_RFC1123, Locale.US);
		formatter.setTimeZone(GMT);
		return formatter.format(date);
	}

}
