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

import java.util.ArrayList;
import java.util.List;

import org.sakaiproject.citation.api.Schema;

/**
 * Editable properties for citation
 * 
 */
public class CiEditableProperties {
	private static List<String> list = new ArrayList<String>();
	static {
		list.add(Schema.TITLE);
		list.add(Schema.ISN);
		list.add(Schema.CREATOR);
//		list.add(Schema.RESOURCE_TYPE);
		list.add(Schema.EDITOR);
		list.add(Schema.VOLUME);
		list.add(Schema.ISSUE);
		list.add(Schema.PAGES);
		list.add(Schema.PUBLISHER);
		list.add(Schema.YEAR);
		list.add(Schema.SOURCE_TITLE);
		list.add(Schema.DATE);
		list.add("publicationLocation");
		list.add("doi"); // no schema entry for DOI
		// added mediatype to properties, otherwise new type
		// is not returned with JSON
		list.add("sakai:mediatype");
		//Used when the user decides to enter a url
		list.add("url");
		list.add("preferredUrl");
		list.add("bookstoreUrl");
		list.add("noUrl");
		list.add("sakai:url_label");
	}

	public static List<String> getList() {
		return list;
	}
}
