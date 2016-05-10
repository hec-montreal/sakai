/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/test/java/org/sakaiproject/sdata/tool/test/ResourceDefinitionFactoryUnitT.java $
 * $Id: ResourceDefinitionFactoryUnitT.java 46605 2008-03-12 11:07:29Z ian@caret.cam.ac.uk $
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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.sdata.tool.api.ResourceDefinition;
import org.sakaiproject.sdata.tool.api.ResourceDefinitionFactory;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.util.ResourceDefinitionFactoryImpl;

/**
 * @author ieb
 */
public class ResourceDefinitionFactoryUnitT extends TestCase
{

	private static final Log log = LogFactory.getLog(ResourceDefinitionFactoryUnitT.class);

	private String[] basePaths = { "/", "/sakai", "/sakai/", null, "" };

	private String[] testPaths = { "sdfsdfsdf", "sdfsdf/", "/",
			"/sdfsdf/sdfsdf/sdfsdf/sdfssdf/12321", "sdfsdfs/sdfsd/sdfsdf/sdfsdf/sdf/", "" };

	/**
	 * @param arg0
	 */
	public ResourceDefinitionFactoryUnitT(String arg0)
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
	public void testCreation()
	{
		for (String basePath : basePaths)
		{
			Map<String, String> config = new HashMap<String, String>();
			config.put("testmode", "testmode");
			ResourceDefinitionFactory rdf = new ResourceDefinitionFactoryImpl(config,"",basePath);
			for (String testPath : testPaths)
			{
				MockResourceDefinitionRequest request = new MockResourceDefinitionRequest(
						testPath);
				try
				{
					ResourceDefinition rd = rdf.getSpec(request);
					/*
					 * System.err.println(basePath + ":" + testPath +
					 * ":getExternalPath():" + rd.getExternalPath(testPath));
					 * System.err.println(basePath + ":" + testPath +
					 * ":getRepositoryPath():" + rd.getRepositoryPath());
					 * System.err.println(basePath + ":" + testPath +
					 * ":getRepositoryPath(extra):" +
					 * rd.getRepositoryPath("extra"));
					 */
					String rp = rd.getRepositoryPath();

					assertTrue("Repository Paths must not be null ", rp != null);
					assertTrue("Repository Paths must be absolute ", rp.startsWith("/"));
					assertTrue("Repository Paths must not end in /, except when root ",
							rp.equals("/") || !rp.endsWith("/"));
					assertTrue(
							"Repository Paths must not have white space at either end ",
							rp.length() == rp.trim().length());
					assertTrue("Repository Paths must no have // ", rp.indexOf("//") < 0);
					String[] elements = rp.split("/");
					if (elements.length != 0)
					{
						char c = elements[elements.length - 1].charAt(0);
					}
					rp = rd.getExternalPath(testPath);
					assertTrue("External Paths must not be null ", rp != null);
					// assertTrue("External Paths must not end in /, except when
					// root ",rp.equals("/") || !rp.endsWith("/"));
					assertTrue("External Paths must not have white space at either end ",
							rp.length() == rp.trim().length());
					assertTrue("External Patsh must no have // ", rp.indexOf("//") < 0);
					rp = rd.getRepositoryPath("extra");
					assertTrue("Extra Repository Paths must not be null ", rp != null);
					assertTrue("Extra Repository Paths must be absolute ", rp
							.startsWith("/"));
					assertTrue(
							"Extra Repository Paths must not end in /, except when root ",
							rp.equals("/") || !rp.endsWith("/"));
					assertTrue(
							"Extra Repository Paths must not have white space at either end ",
							rp.length() == rp.trim().length());
					assertTrue("Extra Repository Patsh must no have // ", rp
							.indexOf("//") < 0);
				}
				catch (SDataException sde)
				{
					log.info("Failed ",sde);
					fail("Problem with dispatcher " + sde.getMessage());
				}

			}
		}

	}

}
