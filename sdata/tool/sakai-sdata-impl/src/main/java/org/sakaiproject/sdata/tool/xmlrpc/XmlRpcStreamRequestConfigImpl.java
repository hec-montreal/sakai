/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/tool/xmlrpc/XmlRpcStreamRequestConfigImpl.java $
 * $Id: XmlRpcStreamRequestConfigImpl.java 45722 2008-02-19 11:56:15Z ian@caret.cam.ac.uk $
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

package org.sakaiproject.sdata.tool.xmlrpc;

import java.util.TimeZone;

import org.apache.xmlrpc.common.XmlRpcStreamRequestConfig;

/**
 * TODO Javadoc
 * 
 * @author ieb
 */
public class XmlRpcStreamRequestConfigImpl implements XmlRpcStreamRequestConfig
{

	private TimeZone tz = TimeZone.getTimeZone("GMT");

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.xmlrpc.common.XmlRpcStreamRequestConfig#isEnabledForExceptions()
	 */
	public boolean isEnabledForExceptions()
	{
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.xmlrpc.common.XmlRpcStreamRequestConfig#isGzipCompressing()
	 */
	public boolean isGzipCompressing()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.xmlrpc.common.XmlRpcStreamRequestConfig#isGzipRequesting()
	 */
	public boolean isGzipRequesting()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.xmlrpc.common.XmlRpcStreamConfig#getEncoding()
	 */
	public String getEncoding()
	{
		return "UTF-8";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.xmlrpc.XmlRpcConfig#getTimeZone()
	 */
	public TimeZone getTimeZone()
	{
		return tz;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.xmlrpc.XmlRpcConfig#isEnabledForExtensions()
	 */
	public boolean isEnabledForExtensions()
	{
		return false;
	}

}
