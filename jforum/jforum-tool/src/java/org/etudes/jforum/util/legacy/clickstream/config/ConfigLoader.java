/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/legacy/clickstream/config/ConfigLoader.java $ 
 * $Id: ConfigLoader.java 55476 2008-12-01 19:16:20Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008 Etudes, Inc. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 * Portions completed before July 1, 2004 Copyright (c) 2003, 2004 Rafael Steil, All rights reserved, licensed under the BSD license. 
 * http://www.opensource.org/licenses/bsd-license.php 
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met: 
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following disclaimer. 
 * 2) Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution. 
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE 
 ***********************************************************************************/
package org.etudes.jforum.util.legacy.clickstream.config;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *Loads clickstream.xml for JForum.
 * 
 * @author <a href="plightbo@hotmail.com">Patrick Lightbody</a>
 * @author Rafael Steil (little hacks for JForum)
 */
public class ConfigLoader
{
	private static final Log log = LogFactory.getLog(ConfigLoader.class);

	private ClickstreamConfig config;

	private static ConfigLoader instance = new ConfigLoader();;

	public static ConfigLoader instance()
	{
		return instance;
	}

	private ConfigLoader() {}

	public ClickstreamConfig getConfig()
	{
		if (this.config != null) {
			return this.config;
		}

		this.config = new ClickstreamConfig();

		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

			String path = SystemGlobals.getValue(ConfigKeys.CLICKSTREAM_CONFIG);
			
			if (log.isInfoEnabled()) {
				log.info("Loading clickstream config from " + path);
			}
			
			File fileInput = new File(path);
			
			if (fileInput.exists()) {
				parser.parse(fileInput, new ConfigHandler());
			}
			else {
				parser.parse(new InputSource(path), new ConfigHandler());
			}
			
			return config;
		}
		catch (SAXException e) {
			log.error("Could not parse clickstream XML", e);
			throw new RuntimeException(e.getMessage());
		}
		catch (IOException e) {
			log.error("Could not read clickstream config from stream", e);
			throw new RuntimeException(e.getMessage());
		}
		catch (ParserConfigurationException e) {
			log.fatal("Could not obtain SAX parser", e);
			throw new RuntimeException(e.getMessage());
		}
		catch (RuntimeException e) {
			log.fatal("RuntimeException", e);
			throw e;
		}
		catch (Throwable e) {
			log.fatal("Exception", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * SAX Handler implementation for handling tags in config file and building config objects.
	 */
	private class ConfigHandler extends DefaultHandler
	{
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
		{
			if (qName.equals("bot-host")) {
				config.addBotHost(attributes.getValue("name"));
			}
			else if (qName.equals("bot-agent")) {
				config.addBotAgent(attributes.getValue("name"));
			}
		}
	}
}
