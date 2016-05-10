/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/trunk/sdata/sdata-tool/impl/src/java/org/sakaiproject/sdata/tool/JCRDumper.java $
 * $Id: JCRDumper.java 45207 2008-02-01 19:01:06Z ian@caret.cam.ac.uk $
 ***********************************************************************************
 *
 * Copyright (c) 2008 The Sakai Foundation.
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
package org.sakaiproject.sdata.services.ci;

import java.util.List;
import java.util.Map;

/**
 * a simple POJO for citation request data wrapping
 * 
 */
public class CitationReq {
	private String id = null;
	private Map<String, List<String>> ciProperties = null;
	private String citationListName;

	public CitationReq() {
		// annonymous constructor
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the citation properties
	 */
	public Map<String, List<String>> getCiProperties() {
		return ciProperties;
	}

	/**
	 * @param citation
	 *            properties the properties to set
	 */
	public void setProperties(Map<String, List<String>> properties) {
		this.ciProperties = properties;
	}

	/**
	 * set citationListName
	 */
	public void setCitationListName(String citationListName) {
		this.citationListName = citationListName;
	}

	/**
	 * get citationListName
	 */
	public String getCitationListName() {
		return this.citationListName;
	}

	/* --------- help methods --------- */

	/**
	 * help method to return a non multivalued property if the value is a
	 * mutlivalue, the first occurence will be returned
	 * 
	 */
	public String getProperty(String key) {
		String res = null;
		List<String> vals = ciProperties.get(key);
		if (vals != null) {
			if (vals.size() > 0) {
				res = vals.get(0);
			}
		}
		return res;
	}

	/**
	 * help method to return a multivaluated list if the property is a non
	 * multivalued property, a List with a single value will be returned
	 */
	public List<String> getPropertyList(String key) {
		List<String> vals = ciProperties.get(key);
		return vals;
	}

	/** help method to return the type of the citation */
	public String getType() {
		return getProperty("sakai:mediatype");
	}

	/** help method to return the citation name */
	public String getCitationName() {
		return getProperty("sakai:displayname");
	}
	
	public String getCitationPreferredUrl(){
		return getProperty("preferredUrl");
	}
	
	public String getCitationPreferredUrlLabel(){
	    return getProperty("sakai:url_label");
	}
}
