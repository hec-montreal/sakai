/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/test/java/org/sakaiproject/sdata/tool/test/http/DeleteMethodWebRequest.java $
 * $Id: DeleteMethodWebRequest.java 45260 2008-02-05 11:03:42Z ian@caret.cam.ac.uk $
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

package org.sakaiproject.sdata.tool.test.http;

import java.net.URL;

import com.meterware.httpunit.GetMethodWebRequest;

/**
 * @author ieb
 */
public class DeleteMethodWebRequest extends GetMethodWebRequest
{

	/**
	 * @param arg0
	 */
	public DeleteMethodWebRequest(String arg0)
	{
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public DeleteMethodWebRequest(URL arg0, String arg1)
	{
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 */
	public DeleteMethodWebRequest(URL arg0, String arg1, String arg2)
	{
		super(arg0, arg1, arg2);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.meterware.httpunit.GetMethodWebRequest#getMethod()
	 */
	@Override
	public String getMethod()
	{
		return "DELETE";
	}

}
