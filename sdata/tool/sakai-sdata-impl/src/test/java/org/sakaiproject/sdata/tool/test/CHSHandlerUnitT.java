/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/test/java/org/sakaiproject/sdata/tool/test/CHSHandlerUnitT.java $
 * $Id: CHSHandlerUnitT.java 48759 2008-05-07 21:26:51Z ian@caret.cam.ac.uk $
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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.TypeException;

/**
 * @author ieb
 *
 */
public class CHSHandlerUnitT extends TestCase
{

	protected static final Map<String, Object> componentMap = new HashMap<String, Object>();
	protected static final Log log = LogFactory.getLog(CHSHandlerUnitT.class);

	/**
	 * @param arg0
	 */
	public CHSHandlerUnitT(String arg0)
	{
		super(arg0);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception
	{
		super.setUp();
		ContentHostingService chs = new MockContentHostingService() {
			
			private Map<String, ContentCollection> ccMap = new HashMap<String, ContentCollection>();

			/* (non-Javadoc)
			 * @see org.sakaiproject.sdata.tool.test.MockContentHostingService#getCollection(java.lang.String)
			 */
			@Override
			
			public ContentCollection getCollection(String path) throws IdUnusedException, TypeException, PermissionException
			{
				ContentCollection cc = ccMap.get(path);
				if ( "/content/group/testcollection/".equals(path)) {
					log.info("Request for "+path+" was mockCC ");
					return new MockContentCollection(path);
				} else {
					log.info("Request for "+path+" was "+cc);
				}
				return cc;
			}
			
			/* (non-Javadoc)
			 * @see org.sakaiproject.sdata.tool.test.MockContentHostingService#addCollection(java.lang.String)
			 */
			@Override
			public ContentCollectionEdit addCollection(String path) throws IdUsedException, IdInvalidException, PermissionException, InconsistentException
			{
				ContentCollectionEdit cce = new MockContentCollection(path);
				ccMap.put(path,cce);
				return cce;
			}
			
		};
		componentMap.put(ContentHostingService.class.getName(), chs);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception
	{
		super.tearDown();
	}
	
	public void testGetFolder() {
		
		
		ConcreteCHSHandler chsH = new ConcreteCHSHandler(componentMap); 
		Map<String, String> m = new HashMap<String, String>();
		m.put("testmode", "testmode");
		chsH.init(m);
		ContentCollection cc = chsH.testGetFolder("/content/group/testcollection");
		assertNotNull(cc);
		cc = chsH.testGetFolder("/content/group/testcollection/");
		assertNotNull(cc);
		cc = chsH.testGetFolder("/content/group/testcollection/1/2/3");
		assertNotNull(cc);
		cc = chsH.testGetFolder("/content/group/testcollection/1/2/3/sdfsdf/sad/sdffsd/");
		assertNotNull(cc);
		
	
		
	}

	public void testGetName() {
		ConcreteCHSHandler chsH = new ConcreteCHSHandler(componentMap); 
		Map<String, String> m = new HashMap<String, String>();
		m.put("testmode", "testmode");
		chsH.init(m);
		
		String name = chsH.testGetName(new MockContentResource("/sdfsdf/sdfsdfsdf/rtert.pdf"));
		assertEquals("rtert.pdf",name);
		name = chsH.testGetName(new MockContentResource("/sdfsdf/sdfsdfsdf/rtert.pdf/"));
		assertEquals("rtert.pdf",name);
	}
}
