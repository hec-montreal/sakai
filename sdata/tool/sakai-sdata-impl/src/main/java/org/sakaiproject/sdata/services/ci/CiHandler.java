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

package org.sakaiproject.sdata.services.ci;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory;
import org.sakaiproject.sdata.tool.json.JSONServiceHandler;

/**
 * TODO Javadoc
 * 
 * @author
 */
public class CiHandler extends JSONServiceHandler {

	private static final Log log = LogFactory.getLog(CiHandler.class);

	/**
	 * TODO Javadoc
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.sakaiproject.sdata.tool.json.JSONServiceServlet#
	 * getServiceDefinitionFactory()
	 */
	@Override
	protected ServiceDefinitionFactory getServiceDefinitionFactory()
			throws ServletException {
		return new CiServiceDefinitionFactory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sakaiproject.sdata.tool.ServiceServlet#getServiceDefinitionFactory
	 * (javax.servlet.ServletConfig)
	 */
	@Override
	protected ServiceDefinitionFactory getServiceDefinitionFactory(
			Map<String, String> config) throws ServletException {
		return new CiServiceDefinitionFactory();
	}

	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		log.info("CiHandler doPut not implemented");
		// TODO: working with the current service def factory
		// try
		// {
		// ServiceDefinition serviceDefinition =
		// getServiceDefinitionFactory().getSpec(
		// request, response);
		//
		// Map<String, Object> responseMap = serviceDefinition.getResponseMap();
		//
		// sendMap(request, response, responseMap);
		// }
		// catch (Exception ex)
		// {
		// sendError(request, response, ex);
		// }
		// TODO Auto-generated method stub
		super.doPut(request, response);
	}

	@Override
	public void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		log.info("CiHandler doDelete not implemented");
		// TODO Auto-generated method stub
		super.doDelete(request, response);
	}
}
