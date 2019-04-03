/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/services/col/ColCHSServiceDefinitionFactory.java $
 * $Id: ColCHSServiceDefinitionFactory.java 47053 2008-03-21 01:14:16Z ian@caret.cam.ac.uk $
 ***********************************************************************************
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2007 The Sakai Foundation.
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

package org.sakaiproject.sdata.services.col;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.api.SecurityAssertion;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory;
import org.sakaiproject.sdata.tool.util.PathSecurityAssertion;
import org.sakaiproject.tool.api.Tool;

/**
 * @author ieb
 */
public class ColCHSServiceDefinitionFactory implements ServiceDefinitionFactory
{

	private static final String BASE_PATH_INIT = "basepath";

	private static final String DEFAULT_BASE_PATH = "/private/sdata";

	private static final Log log = LogFactory
			.getLog(ColCHSServiceDefinitionFactory.class);

	private String basePath;

	private SecurityAssertion pathSecurityAssertion;

	private ContentHostingService contentHostingService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory#getSpec(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ServiceDefinition getSpec(HttpServletRequest request,
			HttpServletResponse response) throws SDataException
	{
		String[] uris = request.getParameterValues("uri");

		log.info("GOT URIS as " + uris);
		for (String u : uris)
		{
			log.info("URIS " + u);
		}
		request.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);

		String v = request.getParameter("v"); // version
		int version = -1;
		if (v != null && v.trim().length() > 0)
		{
			version = Integer.parseInt(v);
		}
		String f = request.getParameter("f"); // function
		String d = request.getParameter("d"); // function
		int depth = 1;
		if (d != null && d.trim().length() > 0)
		{
			depth = Integer.parseInt(d);
		}

		return new ColCHSBean(uris, depth, basePath, pathSecurityAssertion,
				contentHostingService);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory#init(java.util.Map)
	 */
	public void init(Map<String, String> config)
	{

		contentHostingService = (ContentHostingService) ComponentManager
				.get(ContentHostingService.class.getName());
		basePath = config.get(BASE_PATH_INIT);
		if (basePath == null)
		{
			basePath = DEFAULT_BASE_PATH;
		}
		pathSecurityAssertion = new PathSecurityAssertion(config);
	}
}