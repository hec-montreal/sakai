/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/legacy/clickstream/BotChecker.java $ 
 * $Id: BotChecker.java 55476 2008-12-01 19:16:20Z murthy@etudes.org $ 
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
package org.etudes.jforum.util.legacy.clickstream;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.etudes.jforum.util.legacy.clickstream.config.ConfigLoader;


/**
 * Determines if a request is actually a bot or spider.
 * 
 * @author <a href="plightbo@hotmail.com">Patrick Lightbody</a>
 * @author Rafael Steil (little hacks for JForum)
 * @version $Id: BotChecker.java 55476 2008-12-01 19:16:20Z murthy@etudes.org $
 */
public class BotChecker
{
	/**
	 * Checks if we have a bot
	 * @param request the request
	 * @return <code>null</code> if there is no bots in the current request, 
	 * or the bot's name otherwise
	 */
	public static String isBot(HttpServletRequest request)
	{
		if (request.getRequestURI().indexOf("robots.txt") != -1) {
			// there is a specific request for the robots.txt file, so we assume
			// it must be a robot (only robots request robots.txt)
			return "Unknown (asked for robots.txt)";
		}
		
		String userAgent = request.getHeader("User-Agent");
		
		if (userAgent != null) {
			List agents = ConfigLoader.instance().getConfig().getBotAgents();
			
			userAgent = userAgent.toLowerCase();
			
			for (Iterator iterator = agents.iterator(); iterator.hasNext(); ) {
				String agent = (String) iterator.next();
				
				if (userAgent.indexOf(agent) != -1) {
					return userAgent;
				}
			}
		}
		
		String remoteHost = request.getRemoteHost(); // requires a DNS lookup
		
		if (remoteHost != null && remoteHost.length() > 0 && remoteHost.charAt(remoteHost.length() - 1) > 64) {
			List hosts = ConfigLoader.instance().getConfig().getBotHosts();
			
			remoteHost = remoteHost.toLowerCase();
			
			for (Iterator iterator = hosts.iterator(); iterator.hasNext(); ) {
				String host = (String) iterator.next();
				
				if (remoteHost.indexOf(host) != -1) {
					return remoteHost;
				}
			}
		}

		return null;
	}
}