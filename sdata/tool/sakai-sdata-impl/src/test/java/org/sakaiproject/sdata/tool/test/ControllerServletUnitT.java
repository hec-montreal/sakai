/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/test/java/org/sakaiproject/sdata/tool/test/ControllerServletUnitT.java $
 * $Id: ControllerServletUnitT.java 47053 2008-03-21 01:14:16Z ian@caret.cam.ac.uk $
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

import javax.servlet.ServletException;

import junit.framework.TestCase;

import org.sakaiproject.sdata.tool.ControllerServlet;
import org.sakaiproject.sdata.tool.api.Handler;

/**
 * @author ieb
 */
public class ControllerServletUnitT extends TestCase
{

	/**
	 * @param arg0
	 */
	public ControllerServletUnitT(String arg0)
	{
		super(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}

	public void testInit() throws ServletException
	{
		ControllerServlet cs = new ControllerServlet();
		MockServletConfig config = new MockServletConfig();

		config.setInitParameters("handler.1.classname", MockHandler.class.getName());
		config.setInitParameters("handler.1.baseurl", "/c");
		config.setInitParameters("handler.1.name", "1");

		config.setInitParameters("handler.2.classname", MockHandler.class.getName());
		config.setInitParameters("handler.2.baseurl", "/f");
		config.setInitParameters("handler.2.name", "2");
		config.setInitParameters("handler.3.classname", MockHandler.class.getName());
		config.setInitParameters("handler.3.baseurl", "/xc");
		config.setInitParameters("handler.3.name", "3");
		config.setInitParameters("handler.4.classname", MockHandler.class.getName());
		config.setInitParameters("handler.4.baseurl", "/xf");
		config.setInitParameters("handler.4.name", "4");
		config.setInitParameters("handler.5.classname", MockHandler.class.getName());
		config.setInitParameters("handler.5.baseurl", "/cp");
		config.setInitParameters("handler.5.name", "5");
		config.setInitParameters("handler.6.classname", MockHandler.class.getName());
		config.setInitParameters("handler.6.baseurl", "/p");
		config.setInitParameters("handler.6.name", "6");
		cs.init(config);
		Handler h = cs.getHandler(new MockRequest("/c"));
		assertEquals("1", String.valueOf(h));
		h = cs.getHandler(new MockRequest("/c/sdfsdf"));
		assertEquals("1", String.valueOf(h));
		h = cs.getHandler(new MockRequest("/f"));
		assertEquals("2", String.valueOf(h));
		h = cs.getHandler(new MockRequest("/f/sdfsdf"));
		assertEquals("2", String.valueOf(h));
		h = cs.getHandler(new MockRequest("/xf"));
		assertEquals("4", String.valueOf(h));
		h = cs.getHandler(new MockRequest("/xf/sdfsdf"));
		assertEquals("4", String.valueOf(h));
		h = cs.getHandler(new MockRequest("/"));
		assertNull(h);
		h = cs.getHandler(new MockRequest(""));
		assertNull(h);
		h = cs.getHandler(new MockRequest(null));
		assertNull(h);

	}

	public void testInitConfigData() throws ServletException
	{
		ControllerServlet cs = new ControllerServlet();
		MockServletConfig config = new MockServletConfig();

		StringBuilder sb = new StringBuilder();
		sb.append("1.classname=").append(MockHandler.class.getName()).append(";");

		sb.append("1.baseurl=").append("/c").append(";");
		sb.append("1.name=").append("1").append(";");

		sb.append("2.classname=").append(MockHandler.class.getName()).append(";");
		sb.append("2.baseurl=").append("/f").append(";");
		sb.append("2.name=").append("2").append(";");
		sb.append("3.classname=").append(MockHandler.class.getName()).append(";");
		sb.append("3.baseurl=").append("/xc").append(";");
		sb.append("3.name=").append("3").append(";");
		sb.append("4.classname=").append(MockHandler.class.getName()).append(";");
		sb.append("4.baseurl=").append("/xf").append(";");
		sb.append("4.name=").append("4").append(";");
		sb.append("5.classname=").append(MockHandler.class.getName()).append(";");
		sb.append("5.baseurl=").append("/cp").append(";");
		sb.append("5.name=").append("5").append(";");
		sb.append("6.classname=").append(MockHandler.class.getName()).append(";");
		sb.append("6.baseurl=").append("/p").append(";");
		sb.append("6.name=").append("6").append(";");
		config.setInitParameters("config", sb.toString());
		cs.init(config);
		Handler h = cs.getHandler(new MockRequest("/c"));
		assertEquals("1", String.valueOf(h));
		h = cs.getHandler(new MockRequest("/c/sdfsdf"));
		assertEquals("1", String.valueOf(h));
		h = cs.getHandler(new MockRequest("/f"));
		assertEquals("2", String.valueOf(h));
		h = cs.getHandler(new MockRequest("/f/sdfsdf"));
		assertEquals("2", String.valueOf(h));
		h = cs.getHandler(new MockRequest("/xf"));
		assertEquals("4", String.valueOf(h));
		h = cs.getHandler(new MockRequest("/xf/sdfsdf"));
		assertEquals("4", String.valueOf(h));
		h = cs.getHandler(new MockRequest("/"));
		assertNull(h);
		h = cs.getHandler(new MockRequest(""));
		assertNull(h);
		h = cs.getHandler(new MockRequest(null));
		assertNull(h);

	}
}
