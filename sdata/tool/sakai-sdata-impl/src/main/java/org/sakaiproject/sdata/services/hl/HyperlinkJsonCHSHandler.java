/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/services/hl/HyperlinkJsonCHSHandler.java $
 * $Id: HyperlinkJsonCHSHandler.java 46605 2008-03-12 11:07:29Z ian@caret.cam.ac.uk $
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

package org.sakaiproject.sdata.services.hl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.sdata.tool.json.JsonCHSHandler;
import org.sakaiproject.sdata.tool.api.ResourceDefinition;
import org.sakaiproject.util.Validator;

/**
 * A JCRServlet that manages hyper link ressources and 
 * serializes responses for using JSON
 *
 * @author <a href="mailto:katharina.bauer-oppinger@crim.ca">Katharina Bauer-Oppinger</a>
 */
public class HyperlinkJsonCHSHandler extends JsonCHSHandler
{
	private static final Log log = LogFactory.getLog(HyperlinkJsonCHSHandler.class);
	
    /**
     * Constructor.
     */
	public HyperlinkJsonCHSHandler()
	{
		super();
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see org.sakaiproject.sdata.tool.api.Handler#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doGet(final HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		ctFilter = "url";
		super.doGet(request, response);
	}
	
	/*
	 * (non-Javadoc)
	 *
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		snoopRequest(request);
		try
		{
			ResourceDefinition rp = resourceDefinitionFactory.getSpec(request);
			String collectionPath = rp.getRepositoryPath();
			
			String link = request.getParameter("link");
			if (link == null) {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST,
						"Not able to create or update hyper link, parameter 'link' is missing.");
				return;
			}
			if (!collectionPath.endsWith("/") ) {
				collectionPath = collectionPath + "/";
			}
			String resourceName = collectionPath.concat(link.replace("/", "_"));
			
			String name = link;
			if (request.getParameter("name") != null)
				name = request.getParameter("name");
			String description = null;
			if (request.getParameter("descr") != null)
				description = request.getParameter("descr");
			
			List<String> errors = new ArrayList<String>();
			Map<String, Object> responseMap = new HashMap<String, Object>();			
			
			try {
				ContentCollection e = getFolder(collectionPath);
				if (e == null) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST,
							"Unable to save hyperlink to location " + rp.getRepositoryPath());
					return;
				}
			}
			catch (Exception ex) {
				sendError(request, response, ex);
				snoopRequest(request);
				log.error("Failed service Request ", ex);
				return;
			}		
			
			ContentEntity ce = getEntity(resourceName);
			ContentResourceEdit target = null;
			if (ce != null)
			{
				target = contentHostingService.editResource(resourceName);
				log.info("Hyperlink exists already, it is updated: " + link);
			}
			if (target == null)
			{
				target = contentHostingService.addResource(resourceName);
				log.info("Hyperlink is created: " + link);
			}
			target.getPropertiesEdit().addProperty(ResourceProperties.PROP_DISPLAY_NAME,
					name);
			target.getPropertiesEdit().addProperty(ResourceProperties.PROP_DESCRIPTION,
					description);
			target.getPropertiesEdit().addProperty(ResourceProperties.PROP_CONTENT_TYPE, 
					ResourceProperties.TYPE_URL);
			GregorianCalendar lastModified = new GregorianCalendar();
			lastModified.setTime(new Date());
			
			target.setContentType(ResourceProperties.TYPE_URL);
			setLastModified(target, lastModified.getTime());
			target.setContent(link.getBytes());
			contentHostingService.commitResource(target);
			
			responseMap.put("success", true);
			responseMap.put("errors", errors.toArray(new String[1]));
			responseMap.put("uploads", link);
			sendMap(request, response, responseMap);
			log.info("Hyper link complete saved to " + rp.getRepositoryPath());
		}
		catch (Exception ex)
		{
			sendError(request, response, ex);
		}
	}
}
