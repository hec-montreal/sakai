/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/tool/util/ResourceDefinitionFactoryImpl.java $
 * $Id: ResourceDefinitionFactoryImpl.java 46615 2008-03-12 15:14:11Z ian@caret.cam.ac.uk $
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

package org.sakaiproject.sdata.tool.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.sdata.tool.api.ResourceDefinition;
import org.sakaiproject.sdata.tool.api.ResourceDefinitionFactory;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.api.SecurityAssertion;
import org.sakaiproject.tool.api.Tool;

/**
 * TODO Javadoc
 * 
 * @author ieb
 */
public class ResourceDefinitionFactoryImpl implements ResourceDefinitionFactory
{

	private static final Log log = LogFactory.getLog(ResourceDefinitionFactoryImpl.class);

	private String basePath;

	private String baseUrl;

	private SecurityAssertion pathSecurityAssertion;

	/**
	 * construct a resource definition factory with a base URL and a base Path.
	 * @param config 
	 * 
	 * @param basePath
	 * @param basePath2
	 */
	public ResourceDefinitionFactoryImpl(Map<String, String> config, String baseUrl, String basePath)
	{
		this.basePath = basePath;
		this.baseUrl = baseUrl;
		pathSecurityAssertion = new PathSecurityAssertion(config);
		log.info("Definition Factory Created with base path as " + basePath);
	}


	/**
	 * TODO Javadoc
	 * 
	 * @param path
	 * @return
	 * @throws SDataException 
	 */
	public ResourceDefinition getSpec(final HttpServletRequest request) throws SDataException
	{

		String chilSiteId = request.getParameter("child");
		pathSecurityAssertion.setChildSiteId(chilSiteId);
		pathSecurityAssertion.setInheritedAccess(false);

		request.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);

		String path = request.getPathInfo();
		path = path.substring(baseUrl.length());

		if (path.endsWith("/"))
		{
			path = path.substring(0, path.length() - 1);
		}
		int lastSlash = path.lastIndexOf("/");
		int leng = path.length();
		String lastElement = path.substring(lastSlash + 1);

		String v = request.getParameter("v"); // version
		int version = -1;
		if (  v != null && v.trim().length() > 0 ) {
			version = Integer.parseInt(v);
		}
		String f = request.getParameter("f"); // function
		String d = request.getParameter("d"); // function
		int depth = 1;
		if ( d != null && d.trim().length() > 0 ) {
			depth = Integer.parseInt(d);
		}
		String[] fp = request.getParameterValues("fp"); // function parameters
		String[] fv = request.getParameterValues("fv"); // function parameters values
		ResourceDefinition rd = new ResourceDefinitionImpl(request.getMethod(), f, depth, basePath, path, version, pathSecurityAssertion);
		rd.setInheritedAccess(pathSecurityAssertion.inheritedAccess());
		rd.setFunctionParameters(fp);
		rd.setFunctionParameterValues(fv);
		
		return rd;
	}

}
