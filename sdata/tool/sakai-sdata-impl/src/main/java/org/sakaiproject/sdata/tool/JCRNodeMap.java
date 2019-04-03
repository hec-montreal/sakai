/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/tool/JCRNodeMap.java $
 * $Id: JCRNodeMap.java 47053 2008-03-21 01:14:16Z ian@caret.cam.ac.uk $
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

package org.sakaiproject.sdata.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;

import org.sakaiproject.jcr.api.JCRConstants;
import org.sakaiproject.sdata.tool.api.ResourceDefinition;

/**
 * @author ieb
 */
public class JCRNodeMap extends HashMap<String, Object>
{
	/**
	 * @throws RepositoryException
	 */
	public JCRNodeMap(Node n, int depth, ResourceDefinition rp)
			throws RepositoryException
	{
		depth--;
		put("primaryNodeType", n.getPrimaryNodeType().getName());
		put("mixinNodeType", getMixinTypes(n));
		put("properties", getProperties(n));
		put("name", n.getName());
		if (rp != null)
		{
			put("path", rp.getExternalPath(n.getPath()));
		}

		NodeType nt = n.getPrimaryNodeType();

		if (JCRConstants.NT_FILE.equals(nt.getName()))
		{
			addFile(n);
		}
		else
		{
			if (depth >= 0)
			{

				Map<String, Object> nodes = new HashMap<String, Object>();
				NodeIterator ni = n.getNodes();
				int i = 0;
				while (ni.hasNext())
				{
					Node cn = ni.nextNode();
					Map<String, Object> m = new JCRNodeMap(cn, depth, rp);
					m.put("position", String.valueOf(i));
					nodes.put(cn.getName(), m);
					i++;
				}
				put("nitems", nodes.size());
				put("items", nodes);
			}
		}
	}

	/**
	 * @param n
	 * @throws RepositoryException
	 */
	private void addFile(Node n) throws RepositoryException
	{
		Node resource = n.getNode(JCRConstants.JCR_CONTENT);
		Property lastModified = resource.getProperty(JCRConstants.JCR_LASTMODIFIED);
		Property content = resource.getProperty(JCRConstants.JCR_DATA);
		put("lastModified", lastModified.getDate().getTime());
		put("mimeType", resource.getProperty(JCRConstants.JCR_MIMETYPE).getString());
		if (resource.hasProperty(JCRConstants.JCR_ENCODING))
		{
			put("encoding", resource.getProperty(JCRConstants.JCR_ENCODING).getString());
		}
		put("length", String.valueOf(content.getLength()));
	}

	/**
	 * @param n
	 * @return
	 * @throws RepositoryException
	 */
	private Map<String, Object> getProperties(Node n) throws RepositoryException
	{
		Map<String, Object> m = new HashMap<String, Object>();
		for (PropertyIterator pi = n.getProperties(); pi.hasNext();)
		{
			Property p = pi.nextProperty();
			String name = p.getName();
			boolean multiValue = p.getDefinition().isMultiple();
			if (multiValue)
			{
				Value[] v = p.getValues();
				Object[] o = new String[v.length];
				for (int i = 0; i < o.length; i++)
				{
					o[i] = formatType(v[i]);
				}
				m.put(name, o);
			}
			else
			{
				Value v = p.getValue();
				m.put(name, formatType(v));

			}
		}
		return m;
	}

	/**
	 * @param value
	 * @return
	 * @throws RepositoryException
	 */
	private Object formatType(Value value) throws RepositoryException
	{
		switch (value.getType())
		{
			case PropertyType.BOOLEAN:
				return String.valueOf(value.getBoolean());
			case PropertyType.BINARY:
				return "--binary--";
			case PropertyType.DATE:
				return value.getDate().getTime();
			case PropertyType.DOUBLE:
				return String.valueOf(value.getDouble());
			case PropertyType.LONG:
				return String.valueOf(value.getLong());
			case PropertyType.NAME:
			case PropertyType.PATH:
			case PropertyType.REFERENCE:
			case PropertyType.STRING:
				return value.getString();
			default:
				return "--undefined--";
		}
	}

	/**
	 * @param n
	 * @return
	 * @throws RepositoryException
	 */
	private String[] getMixinTypes(Node n) throws RepositoryException
	{
		List<String> mixins = new ArrayList<String>();
		for (NodeType nt : n.getMixinNodeTypes())
		{
			mixins.add(nt.getName());
		}
		return mixins.toArray(new String[0]);
	}

}
