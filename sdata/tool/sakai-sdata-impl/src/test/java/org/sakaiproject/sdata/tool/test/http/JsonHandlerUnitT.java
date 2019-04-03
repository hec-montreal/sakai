/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/test/java/org/sakaiproject/sdata/tool/test/http/JsonHandlerUnitT.java $
 * $Id: JsonHandlerUnitT.java 47053 2008-03-21 01:14:16Z ian@caret.cam.ac.uk $
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

import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

/**
 * @author ieb
 */

public abstract class JsonHandlerUnitT extends TestCase
{
	private static final Log log = LogFactory.getLog(JsonHandlerUnitT.class);

	private static final String LOGIN_BASE_URL = "http://localhost:8080/portal/relogin";

	private static final String USERNAME = "admin";

	private static final String PASSWORD = "admin";

	protected HttpClient client;

	protected boolean enabled = true;

	private byte[] buffer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception
	{
		try
		{
			client = new HttpClient();
			client.getState().setCredentials(
					new AuthScope("localhost", 8080, "LocalSakaiName"),
					new UsernamePasswordCredentials("admin", "admin"));
			GetMethod method = new GetMethod(getBaseUrl() + "checkRunning");
			method.setDoAuthentication(false);
			method.setFollowRedirects(true);
			method.setRequestHeader("x-testdata-size", "2048");
			client.executeMethod(method);
			if (method.getStatusCode() == 200)
			{
				buffer = method.getResponseBody();
				enabled = true;
			}
			else
			{
				enabled = false;
			}
		}
		catch (HttpException he)
		{
			enabled = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	/**
	 * @return
	 */
	protected abstract String getBaseDataUrl();

	/**
	 * @return
	 */
	protected abstract String getBaseUrl();

	/**
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws SAXException
	 */
	protected void login() throws MalformedURLException, IOException
	{
		PostMethod postMethod = new PostMethod(LOGIN_BASE_URL);
		postMethod.setParameter("eid", USERNAME);
		postMethod.setParameter("pw", PASSWORD);
		postMethod.setParameter("submit", "Login");
		client.executeMethod(postMethod);
		postMethod.getStatusCode();

		log.info("Login " + postMethod.getURI() + " to Said "
				+ postMethod.getStatusCode() + " " + postMethod.getStatusText() + " "
				+ postMethod.getResponseBodyAsString());
	}

	/**
	 * 
	 */
	public void testSinglePage()
	{
		if (enabled)
		{
			log.info("Single Page Test Enabled");
		}
		else
		{
			log.info("Tests Disabled, please start tomcat with sdata installed");
		}
	}

	/**
	 * @throws Exception
	 */
	public void testGet404() throws Exception
	{
		if (enabled)
		{
			login();
			GetMethod method = new GetMethod(getBaseDataUrl() + "testpage");
			client.executeMethod(method);
			assertEquals(404,method.getStatusCode());
		}
		else
		{
			log.info("Tests Disabled, please start tomcat with sdata installed");
		}
	}

	/**
	 * @throws Exception
	 */
	public void testUpload() throws Exception
	{
		if (enabled)
		{
			login();
			PutMethod method = new PutMethod(getBaseDataUrl() + "putUpload");
			method.setRequestHeader("Content-Type", "text/html");
			method.setRequestHeader("Content-Encoding", "UTF-8");
			method.setRequestEntity(new ByteArrayRequestEntity(buffer, "text/html"));
			client.executeMethod(method);
			checkHandler(method);
			int code = method.getStatusCode();
			assertTrue("Should have been a 201 or 204 ", (code == 201) || (code == 204));
		}
		else
		{
			log.info("Tests Disabled, please start tomcat with sdata installed");
		}
	}

	/**
	 * @throws Exception
	 */
	public void testUploadDownload() throws Exception
	{
		if (enabled)
		{
			login();
			PutMethod method = new PutMethod(getBaseDataUrl() + "putUpload");
			method.setRequestHeader("Content-Type", "text/html");
			method.setRequestHeader("Content-Encoding", "UTF-8");
			method.setRequestEntity(new ByteArrayRequestEntity(buffer, "text/html"));
			client.executeMethod(method);
			checkHandler(method);
			int code = method.getStatusCode();
			assertTrue("Should have been a 201 or 204 ", (code == 201) || (code == 204));

			GetMethod gmethod = new GetMethod(getBaseDataUrl() + "putUpload");
			client.executeMethod(gmethod);
			checkHandler(gmethod);
			code = gmethod.getStatusCode();
			assertTrue("Should have been a 201 or 204 ", (code == 200));
			int contentL = (int) gmethod.getResponseContentLength();
			log.info("Got " + contentL + " bytes ");
			DataInputStream in = new DataInputStream(gmethod.getResponseBodyAsStream());
			byte[] buffer2 = new byte[contentL];
			in.readFully(buffer2);
			assertEquals("Upload Size is not the same as download size ", buffer.length,
					buffer2.length);
			for (int i = 0; i < buffer2.length; i++)
			{
				assertEquals("Byte at Offset " + i + " corrupted ", buffer[i], buffer2[i]);
			}
		}
		else
		{
			log.info("Tests Disabled, please start tomcat with sdata installed");
		}
	}

	/**
	 * @throws Exception
	 */
	public void testUploadDownloadCache() throws Exception
	{
		if (enabled)
		{
			login();
			PutMethod method = new PutMethod(getBaseDataUrl() + "putUpload");
			method.setRequestHeader("Content-Type", "text/html");
			method.setRequestHeader("Content-Encoding", "UTF-8");
			method.setRequestEntity(new ByteArrayRequestEntity(buffer, "text/html"));
			client.executeMethod(method);
			checkHandler(method);
			int code = method.getStatusCode();
			assertTrue("Should have been a 201 or 204 ", (code == 201) || (code == 204));

			GetMethod gmethod = new GetMethod(getBaseDataUrl() + "putUpload");
			client.executeMethod(gmethod);
			checkHandler(gmethod);

			dumpHeaders(gmethod);
			code = gmethod.getStatusCode();
			assertEquals("Should have been a 200 ", 200, code);
			int contentL = (int) gmethod.getResponseContentLength();
			log.info("Got " + contentL + " bytes ");
			DataInputStream in = new DataInputStream(gmethod.getResponseBodyAsStream());
			byte[] buffer2 = new byte[contentL];
			in.readFully(buffer2);
			assertEquals("Upload Size is not the same as download size ", buffer.length,
					buffer2.length);
			for (int i = 0; i < buffer2.length; i++)
			{
				assertEquals("Byte at Offset " + i + " corrupted ", buffer[i], buffer2[i]);
			}

			String dateheader = gmethod.getResponseHeader("last-modified").getValue();
			// Date date =
			// RFC1123Date.parseDate(resp.getHeaderField("date"));

			// now test the 304 response
			gmethod = new GetMethod(getBaseDataUrl() + "putUpload");
			gmethod.setRequestHeader("if-modified-since", dateheader);
			client.executeMethod(gmethod);
			checkHandler(gmethod);

			code = gmethod.getStatusCode();
			dumpHeaders(gmethod);
			assertEquals("Should have been a 304 ", 304, code);

		}
		else
		{
			log.info("Tests Disabled, please start tomcat with sdata installed");
		}
	}

	/**
	 * @param resp
	 */
	private void dumpHeaders(HttpMethod resp)
	{
		StringBuilder sb = new StringBuilder();
		for (Header header : resp.getResponseHeaders())
		{
			for (HeaderElement h : header.getElements())
			{
				sb.append("\n\t").append(h.getName()).append(": ").append(h.getValue());
			}
		}
		log.info("Headers " + sb.toString());
	}

	/**
	 * @throws Exception
	 */
	public void testDirectory() throws Exception
	{
		if (enabled)
		{
			login();
			for (int i = 0; i < 20; i++)
			{
				PutMethod method = new PutMethod(getBaseDataUrl() + "dirlist/file" + i);
				method.setRequestHeader("Content-Type", "text/html");
				method.setRequestHeader("Content-Encoding", "UTF-8");
				method.setRequestEntity(new ByteArrayRequestEntity(buffer, "text/html"));
				client.executeMethod(method);
				checkHandler(method);
				int code = method.getStatusCode();
				assertTrue("Should have been a 201 or 204 ", (code == 201)
						|| (code == 204));

			}
			long start = System.currentTimeMillis();
			GetMethod gmethod = new GetMethod(getBaseDataUrl() + "dirlist");
			client.executeMethod(gmethod);
			checkHandler(gmethod);

			int code = gmethod.getStatusCode();
			log.info("Dir Method took:" + (System.currentTimeMillis() - start));

			assertEquals("Should have been a 200 ", 200, code);
			int contentL = (int) gmethod.getResponseContentLength();
			log.info("Got " + contentL + " bytes ");
			DataInputStream in = new DataInputStream(gmethod.getResponseBodyAsStream());
			byte[] buffer2 = new byte[contentL];
			in.readFully(buffer2);
			String contentEncoding = gmethod.getResponseCharSet();
			String content = new String(buffer2, contentEncoding);
			log.info("Content\n" + content);
		}
		else
		{
			log.info("Tests Disabled, please start tomcat with sdata installed");
		}
	}

	/**
	 * @throws Exception
	 */
	public void testDeleteOneByOne() throws Exception
	{
		if (enabled)
		{
			login();
			testDirectory();
			for (int i = 0; i < 20; i++)
			{
				DeleteMethod method = new DeleteMethod(getBaseDataUrl() + "dirlist/file"
						+ i);
				client.executeMethod(method);
				checkHandler(method);

				int code = method.getStatusCode();
				assertEquals("Should have been a 204 ", 204, code);
			}
			{
				DeleteMethod method = new DeleteMethod(getBaseDataUrl() + "dirlist");
				client.executeMethod(method);
				checkHandler(method);

				int code = method.getStatusCode();
				assertEquals("Should have been a 204 ", 204, code);
			}
			GetMethod method = new GetMethod(getBaseDataUrl() + "dirlist");
			client.executeMethod(method);
			checkHandler(method);

			int code = method.getStatusCode();
			assertEquals("Should have been a 404 ", 404, code);

		}
		else
		{
			log.info("Tests Disabled, please start tomcat with sdata installed");
		}
	}

	/**
	 * @throws Exception
	 */
	public void testDeleteAllAtOnce() throws Exception
	{
		if (enabled)
		{
			login();
			testDirectory();
			{
				DeleteMethod method = new DeleteMethod(getBaseDataUrl() + "dirlist");
				client.executeMethod(method);
				checkHandler(method);

				int code = method.getStatusCode();
				assertEquals("Should have been a 204 ", 204, code);
			}
			GetMethod method = new GetMethod(getBaseDataUrl() + "dirlist");
			client.executeMethod(method);
			checkHandler(method);

			int code = method.getStatusCode();
			assertEquals("Should have been a 404 ", 404, code);
		}
		else
		{
			log.info("Tests Disabled, please start tomcat with sdata installed");
		}
	}

	public void testMultipartUpload() throws Exception
	{
		if (enabled)
		{
			login();
			
			PostMethod method = new PostMethod(getBaseDataUrl() + "dirlist");

			Part[] parts = new Part[20];
			for (int i = 0; i < parts.length; i++)
			{
				parts[i] = new FilePart("multifile" + i, new ByteArrayPartSource(
						"multifile" + i, buffer));
			}
			method
					.setRequestEntity(new MultipartRequestEntity(parts, method
							.getParams()));

			client.executeMethod(method);
			log.info("Got "+method.getURI()+" "+method.getResponseBodyAsString());
			checkHandler(method);

			int code = method.getStatusCode();

			assertTrue("Should have been a 200 ", (code == 200));

			String content = method.getResponseBodyAsString();
			log.info("Content\n" + content);

			for (int i = 0; i < 20; i++)
			{
				GetMethod gmethod = new GetMethod(getBaseDataUrl() + "dirlist/multifile"
						+ i);
				log.info("Trying " + "dirlist/multifile" + i);
				client.executeMethod(gmethod);
				checkHandler(gmethod);

				int rcode = gmethod.getStatusCode();
				assertEquals("Expected a 200 response ", 200, rcode);
				assertEquals("Content Lenght does not match  ", buffer.length, gmethod
						.getResponseContentLength());
				int contentL = (int) gmethod.getResponseContentLength();
				log.info("Got " + contentL + " bytes ");
				DataInputStream in = new DataInputStream(gmethod
						.getResponseBodyAsStream());
				byte[] buffer2 = new byte[contentL];
				in.readFully(buffer2);
				assertEquals("Upload Size is not the same as download size ",
						buffer.length, buffer2.length);
				for (int j = 0; j < buffer2.length; j++)
				{
					assertEquals("Byte at Offset " + j + " corrupted ", buffer[j],
							buffer2[j]);
				}
			}
		}
		else
		{
			log.info("Tests Disabled, please start tomcat with sdata installed");
		}

	}

	/**
	 * @param resp
	 */
	protected void checkHandler(HttpMethod resp)
	{
		String className = this.getClass().getName();
		className = className.substring(className.lastIndexOf('.'));
		className = className.substring(0, className.length() - "UnitT".length());
		Header h = resp.getResponseHeader("x-sdata-handler");
		assertNotNull("Handler Not found ", h);
		String handler = h.getValue();
		assertTrue("Handler Not found (no value)", handler.trim().length() > 0);
		handler = handler.substring(handler.lastIndexOf('.'));
		assertEquals("Not the expected Handler Class", className, handler);
	}

}
