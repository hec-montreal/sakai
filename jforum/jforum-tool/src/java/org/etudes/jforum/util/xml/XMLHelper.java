/*********************************************************************************** 
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/xml/XMLHelper.java $ 
 * $Id: XMLHelper.java 55474 2008-12-01 18:58:24Z murthy@etudes.org $ 
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
 **********************************************************************************/ 

package org.etudes.jforum.util.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Foothill College
 */

public class XMLHelper {
	
	protected static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";
	/** Validation feature id (http://xml.org/sax/features/validation). */
    protected static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";
    /** Schema validation feature id (http://apache.org/xml/features/validation/schema). */
    protected static final String SCHEMA_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/schema";
    /** Dynamic validation feature id (http://apache.org/xml/features/validation/dynamic). */
    protected static final String DYNAMIC_VALIDATION_FEATURE_ID = "http://apache.org/xml/features/validation/dynamic";
    /** Namespace prefixes feature id (http://xml.org/sax/features/namespace-prefixes). */
    protected static final String NAMESPACE_PREFIXES_FEATURE_ID = "http://xml.org/sax/features/namespace-prefixes";
    
	
	/**
	 * Creates xml docuemnt and adds element
	 * @param element
	 */
    static public Document createXMLDocument(Element element) {
		Document document = DocumentHelper.createDocument();
		document.add(element);
		
		return document;
	}
	
	/**
	 * Reads a Document from the given File 
	 * @param xmlFile - is the File to read from
	 * @throws Exception
	 */
	static public Document parseFile(File xmlFile) throws Exception {
		try {
			Document document = getSaxReader().read(xmlFile);
			return document;
		} catch (DocumentException de) {
			throw de;
		} catch (SAXException se) {
			throw se;
		}catch (Exception e) {
			throw e;
		}
		
    }
	
	/**
	 * creates the SAXReader
	 * @return - the SAXReader instance 
	 * @throws SAXException
	 */
	static public SAXReader getSaxReader()throws SAXException{
		XMLReader xmlreader = null;
		try {
			xmlreader = getXMLReader();
		} catch (SAXException se) {
			throw se;
		}
		
		return new SAXReader(xmlreader);
	}
	
	
	/**
	 * creates the xml reader and sets the features
	 * @return - returns the XMLreader
	 * @throws SAXException
	 */
	static protected XMLReader getXMLReader() throws SAXException {
		XMLReader xmlreader;
		xmlreader = XMLReaderFactory.createXMLReader(DEFAULT_PARSER_NAME);
		//request XML Schema validation
		xmlreader.setFeature(SCHEMA_VALIDATION_FEATURE_ID, true);
		xmlreader.setFeature(VALIDATION_FEATURE_ID, true);
		xmlreader.setFeature(DYNAMIC_VALIDATION_FEATURE_ID, true);
		
		return xmlreader;
	}
	
	/**
	 * creates xml file for the give path
	 * @param url - path to create xml file
	 * @throws IOException
	 */
	static public void generateXMLFile(String url, Document document)throws Exception{
		XMLWriter writer = null;
		FileOutputStream out = null;
		try {
			if (document == null)
				document = DocumentHelper.createDocument();
			
			out = new FileOutputStream(new File(url));
			OutputFormat outformat = OutputFormat.createPrettyPrint();
			//outformat.setEncoding(aEncodingScheme);
			writer = new XMLWriter(out, outformat);
			writer.write(document);
			writer.flush();
		} catch (IOException e1) {
			throw e1;
		} catch (Exception e){
			throw e;
		}finally{
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e2) {
			}
			
			try {
				if (out != null)
					out.close();
			} catch (IOException e3) {
			}
		}
	}

}
