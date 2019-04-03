/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/test/java/org/sakaiproject/sdata/tool/test/RFC1123DateUnitT.java $
 * $Id: RFC1123DateUnitT.java 46605 2008-03-12 11:07:29Z ian@caret.cam.ac.uk $
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

package org.sakaiproject.sdata.tool.test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.sdata.tool.RFC1123Date;

/**
 * @author ieb
 */
public class RFC1123DateUnitT extends TestCase
{

	private static final Log log = LogFactory.getLog(RFC1123DateUnitT.class);

	/**
	 * @param arg0
	 */
	public RFC1123DateUnitT(String arg0)
	{
		super(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * @throws ParseException
	 */
	public void testDate() throws ParseException
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2007, 5, 5, 5, 5, 0);
		cal.set(Calendar.MILLISECOND, 0);
		for (int i = 0; i < 100; i++)
		{
			cal.add(Calendar.SECOND, i * 3656 * 24);
			String dateString = RFC1123Date.formatDate(cal.getTime());
			Date parsed = RFC1123Date.parseDate(dateString);
			assertEquals("Dates didnt match ", cal.getTimeInMillis(), parsed.getTime());
		}
	}
}
