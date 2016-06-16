/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/preferences/SakaiSystemGlobals.java $ 
 * $Id: SakaiSystemGlobals.java 62351 2009-08-06 17:38:09Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2009 Etudes, Inc. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * ***********************************************************************************/
package org.etudes.jforum.util.preferences;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.cover.ServerConfigurationService;

/**
 * Read configurations from sakai.properties.   
 *
 */
public class SakaiSystemGlobals
{
	private static final Log logger = LogFactory.getLog(SystemGlobals.class);
	
	private SakaiSystemGlobals() {}
	
	/**
	 * get the integer value of the property
	 * 
	 * @param field 
	 * 			The property name
	 * @return 
	 * 			The property value
	 */
	public static int getIntValue(String field)
	{
		String value = getPropertyValue(field);
		
		return Integer.parseInt(value);
	}

		
	/**
	 * get the boolean value of the property
	 * 
	 * @param field 
	 * 			The property name
	 * @return 
	 * 			The property value
	 */
	public static boolean getBoolValue(String field)
	{
		String value = getPropertyValue(field);
		return "true".equals(value);
	}
	
	/**
	 * get the value of the property
	 * 
	 * @param field	
	 * 			The property name
	 * @return	
	 * 			The property value
	 */
	public static String getValue(String field)
	{
		String value = getPropertyValue(field);
		return value;
	}
	
	/**
	 * get the property value
	 * 
	 * @param field
	 * 			The property name
	 * @return
	 * 			The property value
	 */
	private static String getPropertyValue(String field)
	{
		String value = ServerConfigurationService.getString(field);
		
		if (value == null || value.trim().length() == 0)
		{
			value = SystemGlobals.getValue(field);
		}
		return value;
	}
	
}
