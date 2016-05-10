/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/tool/util/UserResourceDefinitionFactory.java $
 * $Id: UserResourceDefinitionFactory.java 48702 2008-05-07 00:13:47Z ian@caret.cam.ac.uk $
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

package org.sakaiproject.sdata.tool.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sakaiproject.sdata.tool.api.ResourceDefinition;
import org.sakaiproject.sdata.tool.api.ResourceDefinitionFactory;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.api.SecurityAssertion;
import org.sakaiproject.tool.api.Tool;

/**
 * <p>
 * A UserResourceDefinitionFactory generates ResourceDefinition based on the
 * logged in User. The Resource path is a structured path that ensures that
 * there will not be too many users in one directory. Currently we use a path
 * that is 2 deep, Each level containing a maximum of 256 entries, giving 65K
 * directories in which to store the user preferences. We use the first 4
 * characters of a base16 encoded SHA-1 of the username to generate the path to
 * the users folder, and then append a path normalized folder of the username.
 * </p>
 * <p>
 * Taking this approach ensures that we dont have problems with too many users
 * in each directory which would adversly effect performance.
 * </p>
 * 
 * @author ieb
 */
public class UserResourceDefinitionFactory implements ResourceDefinitionFactory
{

	private String basePath;
	private SecurityAssertion nullSecurityAssertion = new NullSecurityAssertion();

	/**
	 * TODO Javadoc
	 * 
	 * @param basePath
	 */
	public UserResourceDefinitionFactory(String basePath)
	{
		this.basePath = basePath;
	}

	/**
	 * TODO Javadoc
	 * 
	 * @param path
	 * @return
	 * @throws SDataException
	 */
	public ResourceDefinition getSpec(HttpServletRequest request) throws SDataException
	{
		request.setAttribute(Tool.NATIVE_URL, Tool.NATIVE_URL);

		String path = request.getPathInfo();

		if (path.endsWith("/"))
		{
			path = path.substring(0, path.length() - 1);
		}
		int lastSlash = path.lastIndexOf("/");
		int leng = path.length();
		String lastElement = path.substring(lastSlash + 1);

		int version = -1;
		if (lastElement.length() > 0)
		{
			char c = lastElement.charAt(0);
			if (Character.isDigit(c))
			{
				version = Integer.parseInt(lastElement);
				path = path.substring(0, lastSlash);
			}
		}

		String user = request.getRemoteUser();
		if (user == null || user.trim().length() == 0)
		{
			throw new SDataException(HttpServletResponse.SC_UNAUTHORIZED,
					"User must be logged in to use preference service ");
		}

		String pathPrefix = PathPrefix.getPrefix(user);

		path = pathPrefix + path;
		
		String f = request.getParameter("f"); // function
		String d = request.getParameter("d"); // function
		int depth = 1;
		if ( d != null && d.trim().length() > 0 ) {
			depth = Integer.parseInt(d);
		}

		return new ResourceDefinitionImpl( request.getMethod(),f, depth, basePath, path, version, nullSecurityAssertion  );
	}

}
