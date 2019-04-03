/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/test/java/org/sakaiproject/sdata/tool/test/MockResourceProperites.java $
 * $Id: MockResourceProperites.java 48759 2008-05-07 21:26:51Z ian@caret.cam.ac.uk $
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

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Stack;

import org.sakaiproject.entity.api.EntityPropertyNotDefinedException;
import org.sakaiproject.entity.api.EntityPropertyTypeException;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.time.api.Time;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;

/**
 * @author ieb
 *
 */
public class MockResourceProperites implements ResourcePropertiesEdit
{

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#addAll(org.sakaiproject.entity.api.ResourceProperties)
	 */
	public void addAll(ResourceProperties arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#addAll(java.util.Properties)
	 */
	public void addAll(Properties arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#addProperty(java.lang.String, java.lang.String)
	 */
	public void addProperty(String arg0, String arg1)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#addPropertyToList(java.lang.String, java.lang.String)
	 */
	public void addPropertyToList(String arg0, String arg1)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#clear()
	 */
	public void clear()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#get(java.lang.String)
	 */
	public Object get(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getBooleanProperty(java.lang.String)
	 */
	public boolean getBooleanProperty(String arg0)
			throws EntityPropertyNotDefinedException, EntityPropertyTypeException
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getContentHander()
	 */
	public ContentHandler getContentHander()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getLongProperty(java.lang.String)
	 */
	public long getLongProperty(String arg0) throws EntityPropertyNotDefinedException,
			EntityPropertyTypeException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropAssignmentDeleted()
	 */
	public String getNamePropAssignmentDeleted()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropCalendarLocation()
	 */
	public String getNamePropCalendarLocation()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropCalendarType()
	 */
	public String getNamePropCalendarType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropChatRoom()
	 */
	public String getNamePropChatRoom()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropCollectionBodyQuota()
	 */
	public String getNamePropCollectionBodyQuota()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropContentLength()
	 */
	public String getNamePropContentLength()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropContentType()
	 */
	public String getNamePropContentType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropCopyright()
	 */
	public String getNamePropCopyright()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropCopyrightAlert()
	 */
	public String getNamePropCopyrightAlert()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropCopyrightChoice()
	 */
	public String getNamePropCopyrightChoice()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropTypeResource()
	 */
	public String getNamePropTypeResource()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropCreationDate()
	 */
	public String getNamePropCreationDate()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropCreator()
	 */
	public String getNamePropCreator()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropDescription()
	 */
	public String getNamePropDescription()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropDisplayName()
	 */
	public String getNamePropDisplayName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropIsCollection()
	 */
	public String getNamePropIsCollection()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropModifiedBy()
	 */
	public String getNamePropModifiedBy()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropModifiedDate()
	 */
	public String getNamePropModifiedDate()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropNewAssignmentCheckAddDueDate()
	 */
	public String getNamePropNewAssignmentCheckAddDueDate()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropNewAssignmentCheckAutoAnnounce()
	 */
	public String getNamePropNewAssignmentCheckAutoAnnounce()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropReplyStyle()
	 */
	public String getNamePropReplyStyle()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropStructObjType()
	 */
	public String getNamePropStructObjType()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropSubmissionPreviousFeedbackComment()
	 */
	public String getNamePropSubmissionPreviousFeedbackComment()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropSubmissionPreviousFeedbackText()
	 */
	public String getNamePropSubmissionPreviousFeedbackText()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropSubmissionPreviousGrades()
	 */
	public String getNamePropSubmissionPreviousGrades()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropSubmissionScaledPreviousGrades()
	 */
	public String getNamePropSubmissionScaledPreviousGrades()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getNamePropTo()
	 */
	public String getNamePropTo()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getProperty(java.lang.String)
	 */
	public String getProperty(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getPropertyFormatted(java.lang.String)
	 */
	public String getPropertyFormatted(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getPropertyList(java.lang.String)
	 */
	public List getPropertyList(String arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getPropertyNames()
	 */
	public Iterator getPropertyNames()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getTimeProperty(java.lang.String)
	 */
	public Time getTimeProperty(String arg0) throws EntityPropertyNotDefinedException,
			EntityPropertyTypeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#getTypeUrl()
	 */
	public String getTypeUrl()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#isLiveProperty(java.lang.String)
	 */
	public boolean isLiveProperty(String arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#removeProperty(java.lang.String)
	 */
	public void removeProperty(String arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#set(org.sakaiproject.entity.api.ResourceProperties)
	 */
	public void set(ResourceProperties arg0)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.sakaiproject.entity.api.ResourceProperties#toXml(org.w3c.dom.Document, java.util.Stack)
	 */
	public Element toXml(Document arg0, Stack arg1)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
