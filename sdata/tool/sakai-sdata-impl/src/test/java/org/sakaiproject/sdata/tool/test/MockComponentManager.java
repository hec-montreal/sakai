/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/test/java/org/sakaiproject/sdata/tool/test/MockComponentManager.java $
 * $Id: MockComponentManager.java 48759 2008-05-07 21:26:51Z ian@caret.cam.ac.uk $
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

import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.sakaiproject.component.api.ComponentManager;

/**
 * @author ieb
 *
 */
public class MockComponentManager implements ComponentManager
{
	

	private Map<String, Object> componentMap;

	/**
	 * @param componentMap
	 */
	public MockComponentManager(Map<String, Object> componentMap)
	{
		this.componentMap = componentMap;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.component.api.ComponentManager#close()
	 */
	public void close()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.component.api.ComponentManager#contains(java.lang.Class)
	 */
	public boolean contains(Class arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.component.api.ComponentManager#contains(java.lang.String)
	 */
	public boolean contains(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.component.api.ComponentManager#get(java.lang.Class)
	 */
	public Object get(Class arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.component.api.ComponentManager#get(java.lang.String)
	 */
	public Object get(String key)
	{
		return componentMap.get(key);
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.component.api.ComponentManager#getConfig()
	 */
	public Properties getConfig()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.component.api.ComponentManager#getRegisteredInterfaces()
	 */
	public Set getRegisteredInterfaces()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.component.api.ComponentManager#hasBeenClosed()
	 */
	public boolean hasBeenClosed()
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.component.api.ComponentManager#loadComponent(java.lang.Class, java.lang.Object)
	 */
	public void loadComponent(Class arg0, Object arg1)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.component.api.ComponentManager#loadComponent(java.lang.String, java.lang.Object)
	 */
	public void loadComponent(String arg0, Object arg1)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.component.api.ComponentManager#waitTillConfigured()
	 */
	public void waitTillConfigured()
	{
		// TODO Auto-generated method stub

	}

}
