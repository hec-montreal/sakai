/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/test/java/org/sakaiproject/sdata/tool/test/ConcreteCHSHandler.java $
 * $Id: ConcreteCHSHandler.java 48759 2008-05-07 21:26:51Z ian@caret.cam.ac.uk $
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

package org.sakaiproject.sdata.tool.test;

import static org.sakaiproject.sdata.tool.test.CHSHandlerUnitT.log;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.sdata.tool.CHSHandler;

/**
 * @author ieb
 */
public class ConcreteCHSHandler extends CHSHandler
{

	private Map<String, Object> componentMap;

	public ConcreteCHSHandler(Map<String, Object> componentMap)
	{
		this.componentMap = componentMap;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.CHSHandler#init(java.util.Map)
	 */
	@Override
	public void init(Map<String, String> config)
	{
		testmode = true;
		componentManager = new MockComponentManager(componentMap);
		super.init(config);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.Handler#sendError(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.lang.Throwable)
	 */
	public void sendError(HttpServletRequest request, HttpServletResponse response,
			Throwable ex) throws IOException
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.Handler#sendMap(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.util.Map)
	 */
	public void sendMap(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> contetMap) throws IOException
	{
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.CHSHandler#getFolder(java.lang.String)
	 */
	@Override
	public ContentCollection getFolder(String path)
	{
		log.debug("Checking path " + path);
		return super.getFolder(path);
	}

	/**
	 * @param string
	 * @return
	 */
	public ContentCollection testGetFolder(String path)
	{
		return getFolder(path);
	}

	/**
	 * @param contentResource 
	 * 
	 */
	public String testGetName(ContentResourceEdit contentResource)
	{
		return getName(contentResource);
	}

}
