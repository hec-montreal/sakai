/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/test/java/org/sakaiproject/sdata/tool/test/http/JsonUserStorageHandlerUnitT.java $
 * $Id: JsonUserStorageHandlerUnitT.java 46691 2008-03-13 15:34:40Z ian@caret.cam.ac.uk $
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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;


/**
 * @author ieb
 */

public abstract class JsonUserStorageHandlerUnitT extends JsonHandlerUnitT
{
	private static final Log log = LogFactory.getLog(JsonUserStorageHandlerUnitT.class);



	/**
	 * @throws Exception
	 */
	public void testGet401() throws Exception
	{
		if (enabled)
		{
				GetMethod method = new GetMethod(getBaseDataUrl() + "testpage");
				method.setDoAuthentication(false);
				client.executeMethod(method);

				assertEquals(401,method.getStatusCode());
		}
		else
		{
			log.info("Tests Disabled, please start tomcat with sdata installed");
		}
	}



}
