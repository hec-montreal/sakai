/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/JForumContext.java $ 
 * $Id: JForumContext.java 55370 2008-11-26 21:57:23Z murthy@etudes.org $ 
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
package org.etudes.jforum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Marc Wick
 */
public class JForumContext {

    private String contextPath;

    private String servletExtension;

    private HttpServletResponse response;

    private boolean isEncodingDisabled = false;
    
    private HttpServletRequest request;

    public JForumContext(String contextPath, String servletExtension, HttpServletRequest req,
            HttpServletResponse response) {
        this.contextPath = contextPath;
        this.servletExtension = servletExtension;
        this.response = response;
        this.request =  req;

        if (req != null) {
            String userAgent = req.getHeader("user-agent");
            if (userAgent != null) {
                // search engine robots are not using cookies but don't like sessionid in url
                // we are nice with the robots and don't encode the session id for them
                if (userAgent.toLowerCase().indexOf("bot") > -1) {
                    isEncodingDisabled = true;
                } else if (userAgent.toLowerCase().indexOf("slurp") > -1) {
                    isEncodingDisabled = true;
                } else if (userAgent.toLowerCase().indexOf("crawler") > -1) {
                    isEncodingDisabled = true;
                }
            }
        }
    }

    public String encodeURL(String url) {
        if (isEncodingDisabled) {
            return contextPath + url + servletExtension;
        }
        return response.encodeURL(contextPath + url + servletExtension);
    }

    public String encodeURL(String url, String extension) {
        if (isEncodingDisabled) {
            return contextPath + url + extension;
        }
        return response.encodeURL(contextPath + url + extension);
    }
    
    public String sakaiHeadJSScriptTag() {
    	return (String)request.getAttribute("sakai.html.head.js");
    }
    public String sakaiWinHeightJSFunction() {
    	return (String)request.getAttribute("sakai.html.body.onload");
    }
}