/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/tool/json/JsonCHSUserStorageHandler.java $
 * $Id: JsonCHSUserStorageHandler.java 46605 2008-03-12 11:07:29Z ian@caret.cam.ac.uk $
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

package org.sakaiproject.sdata.tool.json;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.sakaiproject.sdata.tool.CHSUserStorageHandler;
import org.sakaiproject.sdata.tool.api.SDataException;

/**
 * Serializes the output of a UserStorageSevlet as json
 * 
 * @author ieb
 */
public class JsonCHSUserStorageHandler extends CHSUserStorageHandler
{

	/**
 * TODO Javadoc
	 * 
	 */
	public JsonCHSUserStorageHandler()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.JCRServlet#sendMap(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, java.util.Map)
	 */
	public void sendMap(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> contetMap) throws IOException
	{
		JSONObject jsonObject = JSONObject.fromObject(contetMap);
		byte[] b = jsonObject.toString().getBytes("UTF-8");
		//response.setContentType("text/javascript");
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.setContentLength(b.length);
		response.getOutputStream().write(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.JCRServlet#sendError(java.lang.Throwable)
	 */
	public void sendError(HttpServletRequest request, HttpServletResponse response,
			Throwable ex) throws IOException
	{
		if (ex instanceof SDataException)
		{
			SDataException sde = (SDataException) ex;
			response.reset();
			response.sendError(sde.getCode(), sde.getMessage());
		}
		else
		{
			response.reset();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Failed with " + ex.getMessage());
		}
	}

}
