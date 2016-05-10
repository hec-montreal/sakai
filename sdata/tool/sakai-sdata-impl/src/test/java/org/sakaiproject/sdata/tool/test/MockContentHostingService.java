/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/test/java/org/sakaiproject/sdata/tool/test/MockContentHostingService.java $
 * $Id: MockContentHostingService.java 48759 2008-05-07 21:26:51Z ian@caret.cam.ac.uk $
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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentCollectionEdit;
import org.sakaiproject.content.api.ContentHostingService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entity.api.Entity;
import org.sakaiproject.entity.api.HttpAccess;
import org.sakaiproject.entity.api.Reference;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdLengthException;
import org.sakaiproject.exception.IdUniquenessException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InUseException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.time.api.Time;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author ieb
 *
 */
public class MockContentHostingService implements ContentHostingService
{

	/**
	 * 
	 */
	public MockContentHostingService()
	{
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#addAttachmentResource(java.lang.String)
	 */
	public ContentResourceEdit addAttachmentResource(String arg0)
			throws IdInvalidException, InconsistentException, IdUsedException,
			PermissionException, ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#addAttachmentResource(java.lang.String, java.lang.String, byte[], org.sakaiproject.entity.api.ResourceProperties)
	 */
	public ContentResource addAttachmentResource(String arg0, String arg1, byte[] arg2,
			ResourceProperties arg3) throws IdInvalidException, InconsistentException,
			IdUsedException, PermissionException, OverQuotaException,
			ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#addAttachmentResource(java.lang.String, java.lang.String, java.lang.String, java.lang.String, byte[], org.sakaiproject.entity.api.ResourceProperties)
	 */
	public ContentResource addAttachmentResource(String arg0, String arg1, String arg2,
			String arg3, byte[] arg4, ResourceProperties arg5) throws IdInvalidException,
			InconsistentException, IdUsedException, PermissionException,
			OverQuotaException, ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#addCollection(java.lang.String)
	 */
	public ContentCollectionEdit addCollection(String arg0) throws IdUsedException,
			IdInvalidException, PermissionException, InconsistentException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#addCollection(java.lang.String, org.sakaiproject.entity.api.ResourceProperties)
	 */
	public ContentCollection addCollection(String arg0, ResourceProperties arg1)
			throws IdUsedException, IdInvalidException, PermissionException,
			InconsistentException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#addCollection(java.lang.String, java.lang.String)
	 */
	public ContentCollectionEdit addCollection(String arg0, String arg1)
			throws PermissionException, IdUnusedException, IdUsedException,
			IdLengthException, IdInvalidException, TypeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#addCollection(java.lang.String, org.sakaiproject.entity.api.ResourceProperties, java.util.Collection)
	 */
	public ContentCollection addCollection(String arg0, ResourceProperties arg1,
			Collection arg2) throws IdUsedException, IdInvalidException,
			PermissionException, InconsistentException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#addCollection(java.lang.String, org.sakaiproject.entity.api.ResourceProperties, java.util.Collection, boolean, org.sakaiproject.time.api.Time, org.sakaiproject.time.api.Time)
	 */
	public ContentCollection addCollection(String arg0, ResourceProperties arg1,
			Collection arg2, boolean arg3, Time arg4, Time arg5) throws IdUsedException,
			IdInvalidException, PermissionException, InconsistentException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#addProperty(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ResourceProperties addProperty(String arg0, String arg1, String arg2)
			throws PermissionException, IdUnusedException, TypeException, InUseException,
			ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#addResource(java.lang.String)
	 */
	public ContentResourceEdit addResource(String arg0) throws PermissionException,
			IdUsedException, IdInvalidException, InconsistentException,
			ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#addResource(java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public ContentResourceEdit addResource(String arg0, String arg1, String arg2, int arg3)
			throws PermissionException, IdUniquenessException, IdLengthException,
			IdInvalidException, IdUnusedException, OverQuotaException,
			ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#addResource(java.lang.String, java.lang.String, byte[], org.sakaiproject.entity.api.ResourceProperties, int)
	 */
	public ContentResource addResource(String arg0, String arg1, byte[] arg2,
			ResourceProperties arg3, int arg4) throws PermissionException,
			IdUsedException, IdInvalidException, InconsistentException,
			OverQuotaException, ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#addResource(java.lang.String, java.lang.String, byte[], org.sakaiproject.entity.api.ResourceProperties, java.util.Collection, int)
	 */
	public ContentResource addResource(String arg0, String arg1, byte[] arg2,
			ResourceProperties arg3, Collection arg4, int arg5)
			throws PermissionException, IdUsedException, IdInvalidException,
			InconsistentException, OverQuotaException, ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#addResource(java.lang.String, java.lang.String, int, java.lang.String, byte[], org.sakaiproject.entity.api.ResourceProperties, int)
	 */
	public ContentResource addResource(String arg0, String arg1, int arg2, String arg3,
			byte[] arg4, ResourceProperties arg5, int arg6) throws PermissionException,
			IdUniquenessException, IdLengthException, IdInvalidException,
			InconsistentException, IdLengthException, OverQuotaException,
			ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#addResource(java.lang.String, java.lang.String, int, java.lang.String, byte[], org.sakaiproject.entity.api.ResourceProperties, java.util.Collection, int)
	 */
	public ContentResource addResource(String arg0, String arg1, int arg2, String arg3,
			byte[] arg4, ResourceProperties arg5, Collection arg6, int arg7)
			throws PermissionException, IdUniquenessException, IdLengthException,
			IdInvalidException, InconsistentException, IdLengthException,
			OverQuotaException, ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#addResource(java.lang.String, java.lang.String, int, java.lang.String, byte[], org.sakaiproject.entity.api.ResourceProperties, java.util.Collection, boolean, org.sakaiproject.time.api.Time, org.sakaiproject.time.api.Time, int)
	 */
	public ContentResource addResource(String arg0, String arg1, int arg2, String arg3,
			byte[] arg4, ResourceProperties arg5, Collection arg6, boolean arg7,
			Time arg8, Time arg9, int arg10) throws PermissionException,
			IdUniquenessException, IdLengthException, IdInvalidException,
			InconsistentException, IdLengthException, OverQuotaException,
			ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#allowAddAttachmentResource()
	 */
	public boolean allowAddAttachmentResource()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#allowAddCollection(java.lang.String)
	 */
	public boolean allowAddCollection(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#allowAddProperty(java.lang.String)
	 */
	public boolean allowAddProperty(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#allowAddResource(java.lang.String)
	 */
	public boolean allowAddResource(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#allowCopy(java.lang.String, java.lang.String)
	 */
	public boolean allowCopy(String arg0, String arg1)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#allowGetCollection(java.lang.String)
	 */
	public boolean allowGetCollection(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#allowGetProperties(java.lang.String)
	 */
	public boolean allowGetProperties(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#allowGetResource(java.lang.String)
	 */
	public boolean allowGetResource(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#allowRemoveCollection(java.lang.String)
	 */
	public boolean allowRemoveCollection(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#allowRemoveProperty(java.lang.String)
	 */
	public boolean allowRemoveProperty(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#allowRemoveResource(java.lang.String)
	 */
	public boolean allowRemoveResource(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#allowRename(java.lang.String, java.lang.String)
	 */
	public boolean allowRename(String arg0, String arg1)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#allowUpdateCollection(java.lang.String)
	 */
	public boolean allowUpdateCollection(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#allowUpdateResource(java.lang.String)
	 */
	public boolean allowUpdateResource(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#archiveResources(java.util.List, org.w3c.dom.Document, java.util.Stack, java.lang.String)
	 */
	public String archiveResources(List arg0, Document arg1, Stack arg2, String arg3)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#cancelCollection(org.sakaiproject.content.api.ContentCollectionEdit)
	 */
	public void cancelCollection(ContentCollectionEdit arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#cancelResource(org.sakaiproject.content.api.ContentResourceEdit)
	 */
	public void cancelResource(ContentResourceEdit arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#checkCollection(java.lang.String)
	 */
	public void checkCollection(String arg0) throws IdUnusedException, TypeException,
			PermissionException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#checkResource(java.lang.String)
	 */
	public void checkResource(String arg0) throws PermissionException, IdUnusedException,
			TypeException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#commitCollection(org.sakaiproject.content.api.ContentCollectionEdit)
	 */
	public void commitCollection(ContentCollectionEdit arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#commitResource(org.sakaiproject.content.api.ContentResourceEdit)
	 */
	public void commitResource(ContentResourceEdit arg0) throws OverQuotaException,
			ServerOverloadException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#commitResource(org.sakaiproject.content.api.ContentResourceEdit, int)
	 */
	public void commitResource(ContentResourceEdit arg0, int arg1)
			throws OverQuotaException, ServerOverloadException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#containsLockedNode(java.lang.String)
	 */
	public boolean containsLockedNode(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#copy(java.lang.String, java.lang.String)
	 */
	public String copy(String arg0, String arg1) throws PermissionException,
			IdUnusedException, TypeException, InUseException, OverQuotaException,
			IdUsedException, ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#copyIntoFolder(java.lang.String, java.lang.String)
	 */
	public String copyIntoFolder(String arg0, String arg1) throws PermissionException,
			IdUnusedException, TypeException, InUseException, OverQuotaException,
			IdUsedException, ServerOverloadException, InconsistentException,
			IdLengthException, IdUniquenessException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#createDropboxCollection()
	 */
	public void createDropboxCollection()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#createDropboxCollection(java.lang.String)
	 */
	public void createDropboxCollection(String arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#createIndividualDropbox(java.lang.String)
	 */
	public void createIndividualDropbox(String arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#editCollection(java.lang.String)
	 */
	public ContentCollectionEdit editCollection(String arg0) throws IdUnusedException,
			TypeException, PermissionException, InUseException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#editResource(java.lang.String)
	 */
	public ContentResourceEdit editResource(String arg0) throws PermissionException,
			IdUnusedException, TypeException, InUseException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#eliminateDuplicates(java.util.Collection)
	 */
	public void eliminateDuplicates(Collection arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#findResources(java.lang.String, java.lang.String, java.lang.String)
	 */
	public List findResources(String arg0, String arg1, String arg2)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getAllEntities(java.lang.String)
	 */
	public List getAllEntities(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getAllResources(java.lang.String)
	 */
	public List getAllResources(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getCollection(java.lang.String)
	 */
	public ContentCollection getCollection(String arg0) throws IdUnusedException,
			TypeException, PermissionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getCollectionMap()
	 */
	public Map getCollectionMap()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getCollectionSize(java.lang.String)
	 */
	public int getCollectionSize(String arg0) throws IdUnusedException, TypeException,
			PermissionException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getContainingCollectionId(java.lang.String)
	 */
	public String getContainingCollectionId(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getDepth(java.lang.String, java.lang.String)
	 */
	public int getDepth(String arg0, String arg1)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getDropboxCollection()
	 */
	public String getDropboxCollection()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getDropboxCollection(java.lang.String)
	 */
	public String getDropboxCollection(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getDropboxDisplayName()
	 */
	public String getDropboxDisplayName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getDropboxDisplayName(java.lang.String)
	 */
	public String getDropboxDisplayName(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getGroupsWithAddPermission(java.lang.String)
	 */
	public Collection getGroupsWithAddPermission(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getGroupsWithReadAccess(java.lang.String)
	 */
	public Collection getGroupsWithReadAccess(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getGroupsWithRemovePermission(java.lang.String)
	 */
	public Collection getGroupsWithRemovePermission(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getIndividualDropboxId(java.lang.String)
	 */
	public String getIndividualDropboxId(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getLocks(java.lang.String)
	 */
	public Collection getLocks(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getProperties(java.lang.String)
	 */
	public ResourceProperties getProperties(String arg0) throws PermissionException,
			IdUnusedException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getQuota(org.sakaiproject.content.api.ContentCollection)
	 */
	public long getQuota(ContentCollection arg0)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getReference(java.lang.String)
	 */
	public String getReference(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getResource(java.lang.String)
	 */
	public ContentResource getResource(String arg0) throws PermissionException,
			IdUnusedException, TypeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getResourcesOfType(java.lang.String, int, int)
	 */
	public Collection<ContentResource> getResourcesOfType(String arg0, int arg1, int arg2)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getSiteCollection(java.lang.String)
	 */
	public String getSiteCollection(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getUrl(java.lang.String)
	 */
	public String getUrl(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getUrl(java.lang.String, java.lang.String)
	 */
	public String getUrl(String arg0, String arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#getUuid(java.lang.String)
	 */
	public String getUuid(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#isAttachmentResource(java.lang.String)
	 */
	public boolean isAttachmentResource(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#isAvailabilityEnabled()
	 */
	public boolean isAvailabilityEnabled()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#isAvailable(java.lang.String)
	 */
	public boolean isAvailable(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#isCollection(java.lang.String)
	 */
	public boolean isCollection(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#isContentHostingHandlersEnabled()
	 */
	public boolean isContentHostingHandlersEnabled()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#isDropboxMaintainer()
	 */
	public boolean isDropboxMaintainer()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#isDropboxMaintainer(java.lang.String)
	 */
	public boolean isDropboxMaintainer(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#isInDropbox(java.lang.String)
	 */
	public boolean isInDropbox(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#isInheritingPubView(java.lang.String)
	 */
	public boolean isInheritingPubView(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#isLocked(java.lang.String)
	 */
	public boolean isLocked(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#isPubView(java.lang.String)
	 */
	public boolean isPubView(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#isRootCollection(java.lang.String)
	 */
	public boolean isRootCollection(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#isShortRefs()
	 */
	public boolean isShortRefs()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#isSortByPriorityEnabled()
	 */
	public boolean isSortByPriorityEnabled()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#lockObject(java.lang.String, java.lang.String, java.lang.String, boolean)
	 */
	public void lockObject(String arg0, String arg1, String arg2, boolean arg3)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#moveIntoFolder(java.lang.String, java.lang.String)
	 */
	public String moveIntoFolder(String arg0, String arg1) throws PermissionException,
			IdUnusedException, TypeException, InUseException, OverQuotaException,
			IdUsedException, InconsistentException, ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#newContentHostingComparator(java.lang.String, boolean)
	 */
	public Comparator newContentHostingComparator(String arg0, boolean arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#newResourceProperties()
	 */
	public ResourcePropertiesEdit newResourceProperties()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#removeAllLocks(java.lang.String)
	 */
	public void removeAllLocks(String arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#removeCollection(java.lang.String)
	 */
	public void removeCollection(String arg0) throws IdUnusedException, TypeException,
			PermissionException, InUseException, ServerOverloadException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#removeCollection(org.sakaiproject.content.api.ContentCollectionEdit)
	 */
	public void removeCollection(ContentCollectionEdit arg0) throws TypeException,
			PermissionException, InconsistentException, ServerOverloadException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#removeLock(java.lang.String, java.lang.String)
	 */
	public void removeLock(String arg0, String arg1)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#removeProperty(java.lang.String, java.lang.String)
	 */
	public ResourceProperties removeProperty(String arg0, String arg1)
			throws PermissionException, IdUnusedException, TypeException, InUseException,
			ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#removeResource(java.lang.String)
	 */
	public void removeResource(String arg0) throws PermissionException,
			IdUnusedException, TypeException, InUseException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#removeResource(org.sakaiproject.content.api.ContentResourceEdit)
	 */
	public void removeResource(ContentResourceEdit arg0) throws PermissionException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#rename(java.lang.String, java.lang.String)
	 */
	public String rename(String arg0, String arg1) throws PermissionException,
			IdUnusedException, TypeException, InUseException, OverQuotaException,
			InconsistentException, IdUsedException, ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#resolveUuid(java.lang.String)
	 */
	public String resolveUuid(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#setPubView(java.lang.String, boolean)
	 */
	public void setPubView(String arg0, boolean arg1)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#setUuid(java.lang.String, java.lang.String)
	 */
	public void setUuid(String arg0, String arg1) throws IdInvalidException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#updateResource(java.lang.String, java.lang.String, byte[])
	 */
	public ContentResource updateResource(String arg0, String arg1, byte[] arg2)
			throws PermissionException, IdUnusedException, TypeException, InUseException,
			OverQuotaException, ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentHostingService#usingResourceTypeRegistry()
	 */
	public boolean usingResourceTypeRegistry()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.EntityProducer#archive(java.lang.String, org.w3c.dom.Document, java.util.Stack, java.lang.String, java.util.List)
	 */
	public String archive(String arg0, Document arg1, Stack arg2, String arg3, List arg4)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.EntityProducer#getEntity(org.sakaiproject.entity.api.Reference)
	 */
	public Entity getEntity(Reference arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.EntityProducer#getEntityAuthzGroups(org.sakaiproject.entity.api.Reference, java.lang.String)
	 */
	public Collection getEntityAuthzGroups(Reference arg0, String arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.EntityProducer#getEntityDescription(org.sakaiproject.entity.api.Reference)
	 */
	public String getEntityDescription(Reference arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.EntityProducer#getEntityResourceProperties(org.sakaiproject.entity.api.Reference)
	 */
	public ResourceProperties getEntityResourceProperties(Reference arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.EntityProducer#getEntityUrl(org.sakaiproject.entity.api.Reference)
	 */
	public String getEntityUrl(Reference arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.EntityProducer#getHttpAccess()
	 */
	public HttpAccess getHttpAccess()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.EntityProducer#getLabel()
	 */
	public String getLabel()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.EntityProducer#merge(java.lang.String, org.w3c.dom.Element, java.lang.String, java.lang.String, java.util.Map, java.util.Map, java.util.Set)
	 */
	public String merge(String arg0, Element arg1, String arg2, String arg3, Map arg4,
			Map arg5, Set arg6)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.EntityProducer#parseEntityReference(java.lang.String, org.sakaiproject.entity.api.Reference)
	 */
	public boolean parseEntityReference(String arg0, Reference arg1)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.EntityProducer#willArchiveMerge()
	 */
	public boolean willArchiveMerge()
	{
		// TODO Auto-generated method stub
		return false;
	}

	public Collection<ContentResource> getContextResourcesOfType(
		String arg0, Set<String> arg1) {
	    return null;
	}

}
