/*********************************************************************************** 
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-impl/impl/src/java/org/etudes/component/app/jforum/JforumServiceImpl.java $ 
 * $Id: JforumServiceImpl.java 60570 2009-05-19 20:03:10Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008 Etudes, Inc. 
 * 
 * Portions completed before September 1, 2008 Copyright (c) 2004, 2005, 2006, 2007 Foothill College, ETUDES Project 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License. 
 * 
 **********************************************************************************/ 
package org.etudes.component.app.jforum;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.api.app.jforum.JforumDataService;
import org.etudes.api.app.jforum.JforumService;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.EntityTransferrer;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.cover.EntityManager;
import org.sakaiproject.site.cover.SiteService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class JforumServiceImpl implements JforumService, EntityTransferrer{
	
	/** Dependency: a logger component. */
	private static Log logger = LogFactory.getLog(JforumServiceImpl.class);
	
	private JforumDataService jforumDataService;
	
	public void init() {
		if (logger.isInfoEnabled())
			logger.info("Entering init....");
		EntityManager.registerEntityProducer(this, REFERENCE_ROOT);
		if (logger.isInfoEnabled())
			logger.info("Exiting init....");
	}
	
	public void destroy(){
	}

	public String getLabel() {
		return "jforum";
	}

	public boolean willArchiveMerge() {
		return false;
	}

	public String archive(String siteId, Document doc, Stack stack, String archivePath, List attachments) {
		if (logger.isInfoEnabled()) logger.info("Entering archive.....");

		StringBuffer results = new StringBuffer();

		results.append("archiving " + getLabel() + " context " + Entity.SEPARATOR + siteId + Entity.SEPARATOR
				+ SiteService.MAIN_CONTAINER + ".\n");
		
		if (logger.isInfoEnabled()) logger.info("Exiting archive.....");
		return results.toString();
	}

	public String merge(String siteId, Element root, String archivePath, String fromSiteId, Map attachmentNames, Map userIdTrans, Set userListAllowImport) {
		if (logger.isInfoEnabled()) logger.info("Entering megre.....");
		
		StringBuffer results = new StringBuffer();
		
		if (logger.isInfoEnabled()) logger.info("Exiting megre.....");
		return results.toString();
	}

	public boolean parseEntityReference(String reference, Reference ref) {
		// TODO Auto-generated method stub
		return false;
	}

	public String getEntityDescription(Reference ref) {
		return null;
	}

	public ResourceProperties getEntityResourceProperties(Reference ref) {
		return null;
	}

	public Entity getEntity(Reference ref) {
		return null;
	}

	public String getEntityUrl(Reference ref) {
		return null;
	}

	public Collection getEntityAuthzGroups(Reference ref, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	public HttpAccess getHttpAccess() {
		return null;
	}

	public void transferCopyEntities(String fromContext, String toContext, List ids) {
		if (logger.isInfoEnabled()) logger.info("Entering transferCopyEntities.....");
		
		jforumDataService.createTaskTopicsInNewSite(fromContext, toContext);
		
		if (logger.isInfoEnabled()) logger.info("Exiting transferCopyEntities.....");
	}
	
	public void transferCopyEntities(String fromContext, String toContext, List ids, boolean cleanup)
	{
		// TODO: implement cleanup?
		transferCopyEntities(fromContext, toContext, ids);
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] myToolIds() {
		String[] toolIds = { "sakai.jforum.tool" };
		return toolIds;
	}

	public void setJforumDataService(JforumDataService jforumDataService) {
		this.jforumDataService = jforumDataService;
	}
 
	
}
