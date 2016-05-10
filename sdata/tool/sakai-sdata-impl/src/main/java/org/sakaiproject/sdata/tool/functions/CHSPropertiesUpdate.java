/******************************************************************************
 * $Id: $
 ******************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation, The Sakai Quebec Team.
 *
 * Licensed under the Educational Community License, Version 1.0
 * (the "License"); you may not use this file except in compliance with the
 * License.
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
 *****************************************************************************/

package org.sakaiproject.sdata.tool.functions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.event.cover.NotificationService;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.sdata.tool.api.Handler;
import org.sakaiproject.sdata.tool.api.ResourceDefinition;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.api.SDataFunction;

/**
 * Special function for updating properties of Content Resource
 */
public class CHSPropertiesUpdate implements SDataFunction
{

	private ContentHostingService contentHostingService;
	private static final Log log = LogFactory.getLog(CHSPropertiesUpdate.class);

	public CHSPropertiesUpdate()
	{
		ComponentManager componentManager = org.sakaiproject.component.cover.ComponentManager
				.getInstance();

		contentHostingService = (ContentHostingService) componentManager
				.get(ContentHostingService.class.getName());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.SDataFunction#call(org.sakaiproject.sdata.tool.api.Handler,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	public void call(Handler handler, HttpServletRequest request,
			HttpServletResponse response, Object target, ResourceDefinition rp)
			throws SDataException
	{
			
			try {
				// updating a property  for ex the "CHEF:description"
				String[] fp = rp.getFunctionParameters();
				String[] fv = rp.getFunctionParameterValues();
				// here we assume a map of properties to update:
				if(fp != null && fv != null){
					if(fp.length == fv.length){
						
						ContentResourceEdit cre = null;
						
						cre = contentHostingService.editResource(rp.getRepositoryPath());
						if (cre == null)
						{
							String message = "Failed to edit node at "
								+ rp.getRepositoryPath() + " type ContentResource ";
							log.error(message);
							throw new RuntimeException(message);
						}
						
						for(int i=0 ; i < fp.length ; i++){
							String propName = fp[i];
							String propValue = fv[i];
							cre.getPropertiesEdit().addProperty(propName, propValue);
						}

						contentHostingService.commitResource(cre, NotificationService.NOTI_NONE);						
					}
				}
				
				
			} catch (PermissionException e) {
				e.printStackTrace();
				log.error(e);
				throw new SDataException(HttpServletResponse.SC_FORBIDDEN, e
						.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
				throw new SDataException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e
						.getMessage());	
			} 
		
	}

}
