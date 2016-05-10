/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/test/java/org/sakaiproject/sdata/tool/test/PathPrefixUnitT.java $
 * $Id: PathPrefixUnitT.java 45260 2008-02-05 11:03:42Z ian@caret.cam.ac.uk $
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

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.sdata.tool.util.PathPrefix;

/**
 * @author ieb
 */
public class PathPrefixUnitT extends TestCase
{

	private static final String[] testUsers = new String[] { null, "", "~test", "ieb236" };

	private static final Log log = LogFactory.getLog(PathPrefixUnitT.class);

	/**
	 * @param arg0
	 */
	public PathPrefixUnitT(String arg0)
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
	 * 
	 */
	public void testGetPath()
	{

		for (String user : testUsers)
		{
			log.info("User:" + user + ":" + PathPrefix.getPrefix(user));
		}
	}

}
