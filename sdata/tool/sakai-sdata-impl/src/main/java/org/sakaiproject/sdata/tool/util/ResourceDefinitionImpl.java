/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/tool/util/ResourceDefinitionImpl.java $
 * $Id: ResourceDefinitionImpl.java 48702 2008-05-07 00:13:47Z ian@caret.cam.ac.uk $
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

package org.sakaiproject.sdata.tool.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.sdata.tool.api.ResourceDefinition;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.sakaiproject.sdata.tool.api.SecurityAssertion;

/**
 * TODO Javadoc
 * 
 * @author ieb
 */
public class ResourceDefinitionImpl implements ResourceDefinition
{

	private static final Log log = LogFactory.getLog(ResourceDefinitionImpl.class);

	protected String path;

	protected int version;

	protected String basePath;

	protected String repoPath;

	protected String function;
	
	protected String[] functionParameters;
	
	protected String[] functionParameterValues;
	
	protected String contentTypeFilter;

	protected int depth;

	protected SecurityAssertion assertion;

	protected String method;
	
	protected boolean inheritedAccess;

	/**
	 * TODO Javadoc
	 * 
	 * @param request
	 * @param inbasePath
	 *        the base path of the resource in the repository
	 * @param inpath
	 *        the path reference in the request
	 * @param f
	 *        the function bein applied	       
	 * @param depth
	 * @param version
	 *        the version being requested.
	 * @throws SDataException
	 */
	public ResourceDefinitionImpl( String method, String f, int depth, String inbasePath,
			String inpath, int inversion, SecurityAssertion assertion)
			throws SDataException
	{
		if (log.isDebugEnabled())
		{
			log.debug("ResourceDef: Base:" + inbasePath + ": path:" + inpath
					+ ": version:" + inversion);
		}
		this.path = inpath;
		this.version = inversion;
		this.basePath = String.valueOf(inbasePath);
		this.contentTypeFilter = null;
		
		this.function = f;
		this.depth = depth;
		this.method = method;
		this.assertion = assertion;
		path = checkContentTypeFilter(path);
		
		if ( basePath.endsWith("/") ) {
			repoPath = basePath + path;
		} else {
			repoPath = basePath + "/" + path;			
		}
		repoPath = cleanPath(repoPath);
		repoPath = repoPath.replaceAll("//", "/");
		if (repoPath.length() > 1 && repoPath.endsWith("/"))
		{
			repoPath = repoPath.substring(0, repoPath.length() - 1);
		}
		if (!repoPath.startsWith("/"))
		{
			repoPath = "/" + repoPath;
		}
		
		assertion.check( method, repoPath);
	}

	/**
	 * TODO Javadoc
	 * 
	 * @param repoPath2
	 * @return
	 */
	protected String cleanPath(String p)
	{
		p = p.replaceAll("//", "/");
		if (p.length() > 1 && p.endsWith("/"))
		{
			p = p.substring(0, p.length() - 1);
		}
		if (!p.startsWith("/"))
		{
			p = "/" + p;
		}
		return p;

	}
	
	/**
	 * checks if path contains a parameter for a content
	 * type filter, returns the path without filter parameter
	 * 
	 * @param path
	 * @return String path without filter
	 */
	protected String checkContentTypeFilter(String p)
	{
		if (p.toLowerCase().startsWith("/ct/")) {
			p = p.substring(4);
			int i = p.indexOf("/");
			this.contentTypeFilter = p.substring(0,i);
			p = p.substring(i);
			if (log.isDebugEnabled())
			{
				log.debug("Filter for Content Type: " + contentTypeFilter);
			}
		}
		return p;
	}

	/**
	 * TODO Javadoc
	 * 
	 * @return
	 */
	public String getRepositoryPath()
	{
		return repoPath;
	}

	/**
	 * TODO Javadoc
	 * 
	 * @param path2
	 * @return
	 */
	public String getExternalPath(String path)
	{
		if (path == null)
		{
			return null;
		}
		if (path.startsWith(basePath))
		{
			return cleanPath(path.substring(basePath.length()));
		}
		return path;
	}

	/**
	 * TODO Javadoc
	 * 
	 * @param name
	 * @return
	 */
	public String getRepositoryPath(String name)
	{
		return cleanPath(repoPath + "/" + name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ResourceDefinition#isPrivate()
	 */
	public boolean isPrivate()
	{
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ResourceDefinition#getFunctionDefinition()
	 */
	public String getFunctionDefinition()
	{
		return function;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ResourceDefinition#getFunctionParameters()
	 */
	public String[] getFunctionParameters() {
		return functionParameters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ResourceDefinition#setFunctionParameters()
	 */
	public void setFunctionParameters(String[] fp) {
		this.functionParameters = fp;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ResourceDefinition#getFunctionParameterValues()
	 */
	public String[] getFunctionParameterValues() {
		return functionParameterValues;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ResourceDefinition#setFunctionParameterValues()
	 */
	public void setFunctionParameterValues(String[] fv) {
		this.functionParameterValues = fv;
	}		
	
	public int getDepth()
	{
		return depth;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ResourceDefinition#getContentTypeFilter()
	 */
	public String getContentTypeFilter()
	{
		return contentTypeFilter;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.api.ResourceDefinition#setContentTypeFilter()
	 */
	public void setContentTypeFilter(String contentTypeFilter)
	{
		this.contentTypeFilter = contentTypeFilter;
	}

	public boolean inheritedAccess() {
		return inheritedAccess;
	}

	public void setInheritedAccess(boolean inherited) {
		inheritedAccess = inherited;
		
	}

}
