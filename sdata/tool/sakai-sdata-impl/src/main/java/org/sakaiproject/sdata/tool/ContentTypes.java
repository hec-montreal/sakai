/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/tool/ContentTypes.java $
 * $Id: ContentTypes.java 46513 2008-03-10 10:46:18Z ian@caret.cam.ac.uk $
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

package org.sakaiproject.sdata.tool;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author ieb
 */
public class ContentTypes
{

	private static final Log log = LogFactory.getLog(ContentTypes.class);

	private static Map<String, String> contentTypes = new HashMap<String, String>();

	static
	{
		Properties p = new Properties();
		InputStream in = ContentTypes.class
				.getResourceAsStream("ContentTypes.properties");
		try
		{
			p.load(in);
			in.close();
		}
		catch (IOException e)
		{
			log.error("Failed to load Content Types ", e);
		}
		for (Object k : p.keySet())
		{
			for ( String ext : p.getProperty(String.valueOf(k)).split(" ") ) {
				contentTypes.put(ext,String.valueOf(k));				
			}
		}

	}

	/**
	 * @param name
	 * @param contentType
	 * @return
	 */
	public static String getContentType(String name, String contentType)
	{
		if (contentType == null || "application/octet-stream".equals(contentType))
		{
			if (name == null || name.endsWith("."))
			{
				return "application/octet-stream";
			}
			int lastDot = name.lastIndexOf(".");
			String ext = name.substring(lastDot + 1).toLowerCase();
			String ct = contentTypes.get(ext);
			if (ct == null)
			{
				return "application/octet-stream";
			}
			else
			{
				return ct;
			}

		}
		else
		{
			return contentType;
		}
	}

}
