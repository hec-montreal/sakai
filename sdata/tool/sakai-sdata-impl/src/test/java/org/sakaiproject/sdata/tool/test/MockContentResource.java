/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/test/java/org/sakaiproject/sdata/tool/test/MockContentResource.java $
 * $Id: MockContentResource.java 48759 2008-05-07 21:26:51Z ian@caret.cam.ac.uk $
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

import java.io.InputStream;
import java.util.Collection;
import java.util.Stack;

import org.sakaiproject.content.api.ContentCollection;
import org.sakaiproject.content.api.ContentEntity;
import org.sakaiproject.content.api.ContentHostingHandler;
import org.sakaiproject.content.api.ContentResourceEdit;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.time.api.Time;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author ieb
 *
 */
public class MockContentResource implements ContentResourceEdit
{

	private String id;

	/**
	 * @param string
	 */
	public MockContentResource(String id)
	{
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentResourceEdit#setContent(byte[])
	 */
	public void setContent(byte[] arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentResourceEdit#setContent(java.io.InputStream)
	 */
	public void setContent(InputStream arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentResourceEdit#setContentLength(int)
	 */
	public void setContentLength(int arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentResourceEdit#setContentType(java.lang.String)
	 */
	public void setContentType(String arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentResource#getContent()
	 */
	public byte[] getContent() throws ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentResource#getContentLength()
	 */
	public int getContentLength()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentResource#getContentType()
	 */
	public String getContentType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentResource#streamContent()
	 */
	public InputStream streamContent() throws ServerOverloadException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentEntity#getContainingCollection()
	 */
	public ContentCollection getContainingCollection()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentEntity#getContentHandler()
	 */
	public ContentHostingHandler getContentHandler()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentEntity#getMember(java.lang.String)
	 */
	public ContentEntity getMember(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentEntity#getResourceType()
	 */
	public String getResourceType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentEntity#getUrl(boolean)
	 */
	public String getUrl(boolean arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentEntity#getVirtualContentEntity()
	 */
	public ContentEntity getVirtualContentEntity()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentEntity#isCollection()
	 */
	public boolean isCollection()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentEntity#isResource()
	 */
	public boolean isResource()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentEntity#setContentHandler(org.sakaiproject.content.api.ContentHostingHandler)
	 */
	public void setContentHandler(ContentHostingHandler arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.ContentEntity#setVirtualContentEntity(org.sakaiproject.content.api.ContentEntity)
	 */
	public void setVirtualContentEntity(ContentEntity arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEntity#getAccess()
	 */
	public AccessMode getAccess()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEntity#getGroupObjects()
	 */
	public Collection getGroupObjects()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEntity#getGroups()
	 */
	public Collection getGroups()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEntity#getInheritedAccess()
	 */
	public AccessMode getInheritedAccess()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEntity#getInheritedGroupObjects()
	 */
	public Collection getInheritedGroupObjects()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEntity#getInheritedGroups()
	 */
	public Collection getInheritedGroups()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEntity#getReleaseDate()
	 */
	public Time getReleaseDate()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEntity#getRetractDate()
	 */
	public Time getRetractDate()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEntity#isAvailable()
	 */
	public boolean isAvailable()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEntity#isHidden()
	 */
	public boolean isHidden()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.Entity#getId()
	 */
	public String getId()
	{
		return id;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.Entity#getProperties()
	 */
	public ResourceProperties getProperties()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.Entity#getReference()
	 */
	public String getReference()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.Entity#getReference(java.lang.String)
	 */
	public String getReference(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.Entity#getUrl()
	 */
	public String getUrl()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.Entity#getUrl(java.lang.String)
	 */
	public String getUrl(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.Entity#toXml(org.w3c.dom.Document, java.util.Stack)
	 */
	public Element toXml(Document arg0, Stack arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.Edit#getPropertiesEdit()
	 */
	public ResourcePropertiesEdit getPropertiesEdit()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.Edit#isActiveEdit()
	 */
	public boolean isActiveEdit()
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEdit#clearGroupAccess()
	 */
	public void clearGroupAccess() throws InconsistentException, PermissionException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEdit#clearPublicAccess()
	 */
	public void clearPublicAccess() throws InconsistentException, PermissionException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEdit#setAvailability(boolean, org.sakaiproject.time.api.Time, org.sakaiproject.time.api.Time)
	 */
	public void setAvailability(boolean arg0, Time arg1, Time arg2)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEdit#setGroupAccess(java.util.Collection)
	 */
	public void setGroupAccess(Collection arg0) throws InconsistentException,
			PermissionException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEdit#setHidden()
	 */
	public void setHidden()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEdit#setPublicAccess()
	 */
	public void setPublicAccess() throws InconsistentException, PermissionException
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEdit#setReleaseDate(org.sakaiproject.time.api.Time)
	 */
	public void setReleaseDate(Time arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEdit#setResourceType(java.lang.String)
	 */
	public void setResourceType(String arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.content.api.GroupAwareEdit#setRetractDate(org.sakaiproject.time.api.Time)
	 */
	public void setRetractDate(Time arg0)
	{
		// TODO Auto-generated method stub

	}

}
