/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/trunk/sdata/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/tool/functions/JCRNodeMetadata.java $
 * $Id: JCRNodeMetadata.java 47053 2008-03-21 01:14:16Z ian@caret.cam.ac.uk $
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

package org.sakaiproject.sdata.tool.functions;

import java.io.IOException;

import javax.jcr.Node;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.component.api.ComponentManager;
import org.sakaiproject.jcr.support.api.JCRNodeFactoryService;
import org.sakaiproject.jcr.support.api.JCRNodeFactoryServiceException;
import org.sakaiproject.sdata.tool.JCRNodeMap;
import org.sakaiproject.sdata.tool.api.Handler;
import org.sakaiproject.sdata.tool.api.ResourceDefinition;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.api.SDataFunction;

/**
 * @author ieb
 */
public class JCRCreateFolder implements SDataFunction
{
	
	private ComponentManager componentManager;
	private JCRNodeFactoryService jcrNodeFactory;

	public JCRCreateFolder(){
		
		componentManager = org.sakaiproject.component.cover.ComponentManager.getInstance();

		jcrNodeFactory = (JCRNodeFactoryService) componentManager.get(JCRNodeFactoryService.class.getName());
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.SDataFunction#call(org.sakaiproject.sdata.tool.api.Handler,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Object)
	 */
	public void call(Handler handler, HttpServletRequest request,
			HttpServletResponse response, Object target, ResourceDefinition rp) throws SDataException
	{
		
		try {
			jcrNodeFactory.createFolder(rp.getRepositoryPath());
		} catch (JCRNodeFactoryServiceException e) {
			throw new SDataException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e
					.getMessage());
		}
		
		/*try
		{
			Node n = (Node) target;
			JCRNodeMap nm = new JCRNodeMap(n,rp.getDepth(),rp);
			handler.sendMap(request, response, nm);
		}
		catch (RepositoryException rex)
		{
			throw new SDataException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, rex
					.getMessage());
		}
		catch (IOException e)
		{
			throw new SDataException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e
					.getMessage());
		}*/

	}

}