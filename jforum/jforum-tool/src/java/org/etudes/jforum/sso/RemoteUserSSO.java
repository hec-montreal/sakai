/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/sso/RemoteUserSSO.java $ 
 * $Id: RemoteUserSSO.java 55477 2008-12-01 19:20:25Z murthy@etudes.org $ 
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
package org.etudes.jforum.sso;

import javax.servlet.http.HttpServletRequest;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.ActionServletRequest;
import org.etudes.jforum.entities.UserSession;

/**
 * Simple SSO authenticator. 
 * This class will try to validate an user by simple
 * checking <code>request.getRemoteUser()</code> is not
 * null. 
 * @author Rafael Steil
 * @author Daniel Campagnoli
 */
public class RemoteUserSSO implements SSO
{
	private static Log logger = LogFactory.getLog(RemoteUserSSO.class);
	/**
	 * @see org.etudes.jforum.sso.SSO#authenticateUser(org.etudes.jforum.ActionServletRequest)
	 */
	public String authenticateUser(ActionServletRequest request)
	{
		return request.getRemoteUser();
	}

   
    public boolean isSessionValid(UserSession userSession, HttpServletRequest request) 
    {  
//    	if (logger.isInfoEnabled()) logger.info("****"+ this.getClass().getName() +".isSessionValid() - Entered...");
//		
//        String remoteUser = request.getRemoteUser();
//      
//        // user has since logged out
//        if(remoteUser == null && userSession.getUserId() != SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)) {
//        	if(logger.isInfoEnabled()) logger.info("User has logged out");
//            return false;
//      
//        // user has since logged in
//        } else if(remoteUser != null && userSession.getUserId() == SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)) {
//        	if(logger.isInfoEnabled()) logger.info("User has logged in");
//            return false;
//   
//        // user has changed user
//        } else if(remoteUser != null && !remoteUser.equals(userSession.getUsername())) {
//        	if(logger.isInfoEnabled()) logger.info("User has changed user");
//            return false;
//        }
//        
//        if (logger.isInfoEnabled()) logger.info("****"+ this.getClass().getName() +".isSessionValid() - Exiting...");
//    	if(logger.isInfoEnabled()) logger.info("User session is valid");
    	
    	// Sakai handles sessions for us -- JMH
        return true;
   }
}
