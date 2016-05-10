/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/tool/xmlrpc/XmlRpcJcrHandler.java $
 * $Id: XmlRpcJcrHandler.java 47053 2008-03-21 01:14:16Z ian@caret.cam.ac.uk $
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

package org.sakaiproject.sdata.tool.xmlrpc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.common.TypeFactory;
import org.apache.xmlrpc.common.XmlRpcStreamRequestConfig;
import org.apache.xmlrpc.serializer.DefaultXMLWriterFactory;
import org.apache.xmlrpc.serializer.XmlRpcWriter;
import org.apache.xmlrpc.serializer.XmlWriterFactory;
import org.sakaiproject.sdata.tool.JCRHandler;
import org.sakaiproject.sdata.tool.api.SDataException;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * A JCRServlet that serializes responses using JSON
 * 
 * @author ieb
 */
public class XmlRpcJcrHandler extends JCRHandler
{

	private static final Log log = LogFactory.getLog(XmlRpcJcrHandler.class);

	private XmlWriterFactory writerFactory = new DefaultXMLWriterFactory();

	private XmlRpcStreamRequestConfig pConfig = new XmlRpcStreamRequestConfigImpl();

	private TypeFactory typeFactory = new XmlRpcTypeFactory(pConfig);

	/**
	 * TODO Javadoc
	 */
	public XmlRpcJcrHandler()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.JCRServlet#sendMap(java.util.Map)
	 */
	public void sendMap(HttpServletRequest request, HttpServletResponse response,
			Map<String, Object> contentMap) throws IOException
	{

		XmlRpcWriter xw;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			xw = getXmlRpcWriter(pConfig, baos);
		}
		catch (XmlRpcException e)
		{
			log.error("Failed to get RpcWriter ", e);
			throw new IOException("Failed to get RpcWriter  " + e.getMessage());
		}
		try
		{
			xw.write(pConfig, contentMap);
			baos.flush();
			byte[] out = baos.toByteArray();
			baos.close();

			response.setContentLength(out.length);
			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");
			response.getOutputStream().write(out);
		}
		catch (SAXException e)
		{
			StringBuilder sb = new StringBuilder();
			dumpMap(contentMap,sb);
			log.error("Map is  "+sb.toString());
			log.error("Failed to write response ", e);
			throw new IOException("Failed to write response " + e.getMessage());
		}

	}

	/**
	 * @param contentMap
	 */
	private void dumpMap(Map<String, Object> contentMap, StringBuilder sb)
	{
		for ( String k : contentMap.keySet()) {
			Object o = contentMap.get(k);
			if ( o instanceof Map ) {
				sb.append(k).append(":{");
				dumpMap((Map)o,sb);
				sb.append("}\n");
			} else if ( o instanceof List ) {
				sb.append(k).append(":[");
				dumpList((List)o,sb);
				sb.append("]\n");
			} else {
				sb.append(k).append(":").append(o).append("\n");
			}
		}
		
	}

	/**
	 * @param list
	 * @param sb
	 */
	private void dumpList(List list, StringBuilder sb)
	{
		for ( Object o : list) { 
			if ( o instanceof Map ) {
				sb.append(o).append(":{");
				dumpMap((Map)o,sb);
				sb.append("}\n");
			} else if ( o instanceof List ) {
				sb.append(o).append(":[");
				dumpList((List)o,sb);
				sb.append("]\n");
			} else {
				sb.append(o).append("\n");
			}			
		}
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sakaiproject.sdata.tool.JCRServlet#sendError(java.lang.Throwable)
	 */
	public void sendError(HttpServletRequest request, HttpServletResponse response,
			Throwable ex) throws IOException
	{
		if (ex instanceof SDataException)
		{

			SDataException sde = (SDataException) ex;
			response.reset();
			response.sendError(sde.getCode(), sde.getMessage());
		}
		else
		{
			XmlRpcWriter xw;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try
			{
				xw = getXmlRpcWriter(pConfig, baos);
			}
			catch (XmlRpcException e)
			{
				log.error("Failed to get RpcWriter ", e);
				throw new IOException("Failed to get RpcWriter  " + e.getMessage());
			}
			try
			{
				xw.write(pConfig, 500, ex.getMessage(), ex);
				baos.flush();
				byte[] out = baos.toByteArray();
				baos.close();
				response.setContentLength(out.length);
				response.setContentType("text/xml");
				response.setCharacterEncoding("UTF-8");
				response.getOutputStream().write(out);
			}
			catch (SAXException e)
			{
				log.error("Failed to write response ", e);
				throw new IOException("Failed to write response " + e.getMessage());
			}
		}
	}

	/**
	 * TODO Javadoc
	 * 
	 * @param pConfig
	 * @param pStream
	 * @return
	 * @throws XmlRpcException
	 */
	protected XmlRpcWriter getXmlRpcWriter(XmlRpcStreamRequestConfig pConfig,
			OutputStream pStream) throws XmlRpcException
	{
		ContentHandler w = getXMLWriterFactory().getXmlWriter(pConfig, pStream);
		return new XmlRpcWriter(pConfig, w, getTypeFactory());
	}

	/**
	 * TODO Javadoc
	 * 
	 * @return
	 */
	protected XmlWriterFactory getXMLWriterFactory()
	{
		return writerFactory;
	}

	/**
	 * TODO Javadoc
	 * 
	 * @return
	 */
	protected TypeFactory getTypeFactory()
	{
		return typeFactory;
	}

}
