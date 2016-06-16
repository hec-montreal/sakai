/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/SafeHtml.java $ 
 * $Id: SafeHtml.java 67147 2010-04-13 21:25:01Z murthy@etudes.org $ 
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
package org.etudes.jforum.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.htmlparser.Attribute;
import org.htmlparser.Node;
import org.htmlparser.Tag;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.nodes.TextNode;

/**
 * Process text with html and remove possible
 * malicious tags and attributes.
 * 
 * @author Rafael Steil
 */
public class SafeHtml
{
	private static final Log logger = LogFactory.getLog(SafeHtml.class);
	private Set welcomeTags;
	
	public SafeHtml()
	{
		this.welcomeTags = new HashSet();
		// String[] tags = SystemGlobals.getValue(ConfigKeys.HTML_TAGS_WELCOME).toUpperCase().split(",");
		String[] tags = SakaiSystemGlobals.getValue(ConfigKeys.HTML_TAGS_WELCOME).toUpperCase().split(",");

		for (int i = 0; i < tags.length; i++) {
			this.welcomeTags.add(tags[i].trim());
		}
	}
	
	private String processAllNodes(String contents) throws Exception
	{
		StringBuffer sb = new StringBuffer(512);
		Lexer lexer = new Lexer(contents);
		Node node;
		while ((node = lexer.nextNode()) != null) {
			if (this.isTagWelcome(node)) {
				sb.append(node.toHtml());
			}
			else {
				sb.append(node.toHtml().replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
			}
		}
		
		return sb.toString();
	}
	
	private boolean isTagWelcome(Node node)
	{
		if (node instanceof TextNode) {
			return true;
		}
		
		Tag tag = (Tag)node;
		if (!this.welcomeTags.contains(tag.getTagName())) {
			return false;
		}
		
		this.checkAndValidateAttributes(tag);
		
		return true;
	}
	
	private void checkAndValidateAttributes(Tag tag)
	{
		Vector newAttributes = new Vector();
		for (Iterator iter = tag.getAttributesEx().iterator(); iter.hasNext(); ) {
			Attribute a = (Attribute)iter.next();

			String name = a.getName();
			if (name != null) {
				name = name.toLowerCase();
				if (("href".equals(name) || "src".equals(name)) && a.getValue() != null) {
					if (a.getValue().toLowerCase().indexOf("javascript:") > -1) {
						a.setValue("#");
					}
					else if (a.getValue().indexOf("&#") > -1) {
						a.setValue(a.getValue().replaceAll("&#", "&amp;#"));
					}
					
					newAttributes.add(a);
				}
				else if (!name.startsWith("on") && !name.startsWith("style")) {
					newAttributes.add(a);
				}
			}
			else {
				newAttributes.add(a);
			}
		}
		
		tag.setAttributesEx(newAttributes);
	}

	public static String makeSafe(String contents)
	{
		if (contents == null || contents.trim().length() == 0) {
			return contents;
		}
		
		try {
			contents = new SafeHtml().processAllNodes(contents);
		}
		catch (Exception e) {
			logger.warn("Problems while parsing the html: " + e);
			; // we don't care
		}
		
		return contents;
	}
	
	/**
	 * Replace the characters < and > with &lt; and &gt; associated with text "script"
	 * @param contents string that is to be modified
	 * @return modifeed string
	 */
	public static String escapeJavascript(String contents) {
		if (contents == null)
			return null;
		
		String result = null;
		result = contents.replaceAll("<([\\/]*[sS][cC][rR][iI][pP][tT])>*?>", "&lt;$1&gt;");
		result = result.replaceAll("<([sS][cC][rR][iI][pP][tT])[\\s+][^\\<]*>*?>", "&lt;$1&gt;");
	
		return result;
	}
	
	/**
	 * Replace the characters < and > with space associated with  text "script"  
	 * @param contents string that is to be modified
	 * @return modifeed string
	 */
	public static String escapeScriptTagWithSpace(String contents) {
		if (contents == null)
			return null;
		
		String result = null;
		result = contents.replaceAll("<\\/*([sS][cC][rR][iI][pP][tT])>*?>", " $1 ");
		result = result.replaceAll("<([sS][cC][rR][iI][pP][tT])[\\s+][^\\<]*>*?>", " $1 ");
	
		return result;
	}
	
	
	/**
	 * regular expression that tests for the existence of malicious characters and replaces them with a space. 
	 */
	public static String escapeWithSpace(String contents) {
		if (contents == null)
			return null;
		
		String filterPattern="[<>{}\\[\\];\\&]";
		String result = contents.replaceAll(filterPattern," ");
		
		return result;
	}
	
	/**
	 * remove excess spaces from the string
	 * @param contents		post content
	 * @return				modified content
	 */
	public static String removeExcessSpaces(String contents) {
		if (contents == null)
			return null;
		
		String filterPattern="(&amp;nbsp;|&nbsp;){10,}+";
		String result = contents.replaceAll(filterPattern," ");
		
		return result;
	}
}
