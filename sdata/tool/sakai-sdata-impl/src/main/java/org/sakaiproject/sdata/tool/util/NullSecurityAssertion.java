/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/tool/util/NullSecurityAssertion.java $
 * $Id: NullSecurityAssertion.java 46391 2008-03-06 16:23:31Z ian@caret.cam.ac.uk $
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

package org.sakaiproject.sdata.tool.util;

import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.api.SecurityAssertion;

/**
 * @author ieb
 *
 */
public class NullSecurityAssertion implements SecurityAssertion {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.sakaiproject.sdata.tool.api.SecurityAssertion#check(java.lang.String)
	 */
	public void check(String method, String resourceLocation)
			throws SDataException {
	}

	public void setChildSiteId(String childSiteId) {

	}

	public String getChildSiteId() {
		return null;
	}

	public boolean inheritedAccess() {
		return false;
	}
	
	public void setInheritedAccess (boolean access){
	    
	}
}
