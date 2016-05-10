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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.citation.api.CitationService;
import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.tool.api.Tool;

/**
 * TODO Javadoc
 * 
 * @author
 */
public class CiServiceDefinitionFactory implements ServiceDefinitionFactory {

	private SessionManager sessionManager;

	private SiteService siteService;

	private ComponentManager componentManager;

	private CitationService citationService;

	private ContentHostingService contentHostingService;

	private static final Log log = LogFactory
			.getLog(CiServiceDefinitionFactory.class);

	/**
	 * TODO Javadoc
	 */
	public CiServiceDefinitionFactory() {
		componentManager = org.sakaiproject.component.cover.ComponentManager
				.getInstance();
		// siteService = (SiteService)
		// componentManager.get(SiteService.class.getName());
		sessionManager = (SessionManager) componentManager
				.get(SessionManager.class.getName());
		citationService = (CitationService) componentManager
				.get(CitationService.class.getName());
		siteService = (SiteService) componentManager.get(SiteService.class
				.getName());
		contentHostingService = (ContentHostingService) componentManager
				.get(ContentHostingService.class.getName());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory#getSpec(javax
	 * .servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ServiceDefinition getSpec(HttpServletRequest request,
			HttpServletResponse response) {
		request.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);

		// parsing entity path from request
		String path = request.getPathInfo();
		String entityPath = retrieveEntity(path);
		if (entityPath == null || entityPath.length() == 0) {
			try {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			} catch (IOException e) {
				log.error("Cannot send error on servlet response ", e);
			}
			log
					.info("CiServiceDefinition Bad request you probably have to point to an entity path="
							+ path);
			return null;
		}

		return new CiBean(siteService, sessionManager, citationService,
				contentHostingService, entityPath, request, response);
	}

	private String retrieveEntity(String path) {
		String entityPath = null;
		int startPos = path.indexOf("/", 1);
		if (startPos >= 0) {
			entityPath = path.substring(startPos);
		}
		return entityPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sakaiproject.sdata.tool.api.ServiceDefinitionFactory#init(java.util
	 * .Map)
	 */
	public void init(Map<String, String> config) {

	}

}
