/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/test/java/org/sakaiproject/sdata/tool/test/MockSecurityService.java $
 * $Id: MockSecurityService.java 46391 2008-03-06 16:23:31Z ian@caret.cam.ac.uk $
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

import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.user.api.User;

/**
 * @author ieb
 */
public class MockSecurityService implements SecurityService
{

	private static final Log log = LogFactory.getLog(MockSecurityService.class);

	private boolean pass;

	private String lastResource;

	private String lastLock;

	public void clearAdvisors()
	{
	}

	public boolean hasAdvisors()
	{
		return false;
	}

	public boolean isSuperUser()
	{
		return false;
	}

	public boolean isSuperUser(String arg0)
	{
		return false;
	}

	public SecurityAdvisor popAdvisor()
	{
		return null;
	}

	public void pushAdvisor(SecurityAdvisor arg0)
	{
	}

	public boolean unlock(String arg0, String arg1)
	{
		if ( arg0 == null || arg1 == null ) {
			log.debug("unlock("+arg0+","+arg1+") is bad ");
			return false;
		}
		lastResource = arg0;
		lastLock = arg1;
		return pass;
	}

	public boolean unlock(User arg0, String arg1, String arg2)
	{
		if ( arg0 == null || arg1 == null ) {
			log.debug("unlock("+arg0+","+arg1+","+arg2+") is bad ");
			return false;
		}
		lastResource = arg1;
		lastLock = arg2;
		log.debug("unlock("+arg0+","+arg1+","+arg2+") is "+pass);
		return pass;
	}

	public boolean unlock(String arg0, String arg1, String arg2)
	{
		if ( arg0 == null || arg1 == null ) {
			log.debug("unlock("+arg0+","+arg1+","+arg2+") is bad ");
			return false;
		}
		lastResource = arg0;
		lastLock = arg1;
		return pass;
	}

	public boolean unlock(String arg0, String arg1, String arg2, Collection arg3)
	{
		if ( arg0 == null || arg1 == null ) {
			log.debug("unlock("+arg0+","+arg1+","+arg2+","+arg3+") is bad ");
			return false;
		}
		lastResource = arg0;
		lastLock = arg1;
		log.debug("unlock("+arg0+","+arg1+","+arg2+","+arg3+") is "+pass);
		return pass;
	}

	public List unlockUsers(String arg0, String arg1)
	{
		if ( arg0 == null || arg1 == null ) {
			log.debug("unlockUsers("+arg0+","+arg1+") is bad ");
			return null;
		}
		log.debug("unlockUsers("+arg0+","+arg1+") is "+pass);
		lastResource = arg0;
		lastLock = arg1;
		return null;
	}

	/**
	 * @return the lastLock
	 */
	public String getLastLock()
	{
		return lastLock;
	}

	/**
	 * @param lastLock the lastLock to set
	 */
	public void setLastLock(String lastLock)
	{
		this.lastLock = lastLock;
	}

	/**
	 * @return the lastResource
	 */
	public String getLastResource()
	{
		return lastResource;
	}

	/**
	 * @param lastResource the lastResource to set
	 */
	public void setLastResource(String lastResource)
	{
		this.lastResource = lastResource;
	}

	/**
	 * @return the pass
	 */
	public boolean isPass()
	{
		return pass;
	}

	/**
	 * @param pass the pass to set
	 */
	public void setPass(boolean pass)
	{
		this.pass = pass;
	}

	public void clearUserEffectiveRole(String arg0) {
	}

	public void clearUserEffectiveRoles() {
	}

	public String getUserEffectiveRole(String arg0) {
	    return null;
	}

	public boolean setUserEffectiveRole(String arg0, String arg1) {
	    return false;
	}

}
