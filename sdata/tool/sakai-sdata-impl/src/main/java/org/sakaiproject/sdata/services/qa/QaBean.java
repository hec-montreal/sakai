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

package org.sakaiproject.sdata.services.qa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.announcement.api.AnnouncementMessage;
import org.sakaiproject.announcement.api.AnnouncementService;
import org.sakaiproject.message.api.Message;
import org.sakaiproject.message.api.MessageService;
import org.sakaiproject.sdata.tool.api.ServiceDefinition;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.time.api.TimeService;
import org.sakaiproject.tool.api.SessionManager;

/**
 * TODO Javadoc
 * 
 * @author
 */
public class QaBean implements ServiceDefinition
{
	private List<Map> searchList = new ArrayList<Map>();

	private Map<String, Object> map2 = new HashMap<String, Object>();;

	private List<String> arl = new ArrayList<String>();

	private ArrayList<Message> Mes = new ArrayList<Message>();

	private Map<String, Object> map = new HashMap<String, Object>();

	private static final Log log = LogFactory.getLog(QaBean.class);

	private List<Map> MyMotds = new ArrayList<Map>();

	/**
	 * TODO Javadoc
	 * 
	 * @param sessionManager
	 * @param siteService
	 */
	public QaBean(SessionManager sessionManager,
			MessageService messageservice, TimeService timeService,
			SiteService siteService, AnnouncementService announcementService, HttpServletRequest request, HttpServletResponse response)
	{

		try
		{
			
			String siteId = request.getParameter("siteid");
			String subject = request.getParameter("subject");
			String body = request.getParameter("body");
			
			AnnouncementMessage edit = announcementService.getAnnouncementChannel("/announcement/channel/" + siteId + "/main").addAnnouncementMessage(subject, false, null, body);
			//announcementService.getAnnouncementChannel("/announcement/channel/" + siteId + "/main").commitMessage(edit);
			
			map2.put("status", "success");
			
		} catch (Exception ex){
			
			map2.put("status", "failed");
			
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ServiceDefinition#getResponseMap()
	 */
	public Map<String, Object> getResponseMap()
	{

		return map2;
	}

}
