/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/trunk/sdata/sdata-tool/impl/src/java/org/sakaiproject/sdata/tool/JCRDumper.java $
 * $Id: JCRDumper.java 45207 2008-02-01 19:01:06Z ian@caret.cam.ac.uk $
 ***********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation.
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

package org.sakaiproject.sdata.services.mcp;

import java.util.Map;

import javax.servlet.ServletException;

import org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory;
import org.sakaiproject.sdata.tool.json.JSONServiceHandler;

/**
 * The mycoursesandprojects handler extends JSONServiceHandler
 * 
 * @author
 */
public class MyCoursesAndProjectsHandler extends JSONServiceHandler
{

	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.json.JSONServiceServlet#getServiceDefinitionFactory()
	 */
	@Override
	protected ServiceDefinitionFactory getServiceDefinitionFactory()
			throws ServletException
	{
		return new MyCoursesAndProjectsServiceDefinitionFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.ServiceServlet#getServiceDefinitionFactory(javax.servlet.ServletConfig)
	 */
	@Override
	protected ServiceDefinitionFactory getServiceDefinitionFactory(
			Map<String, String> config) throws ServletException
	{
		return new MyCoursesAndProjectsServiceDefinitionFactory();
	}

}
