/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/ajax/AjaxUtils.java $ 
 * $Id: AjaxUtils.java 61815 2009-07-15 17:48:14Z murthy@etudes.org $ 
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
package org.etudes.jforum.util.ajax;

import java.util.ArrayList;
import java.util.Date;

import org.etudes.jforum.entities.Post;
import org.etudes.jforum.util.mail.Spammer;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.view.forum.common.PostCommon;

import freemarker.template.SimpleHash;

/**
 * General AJAX utility methods. 
 * 
 * @author Rafael Steil
 */
public class AjaxUtils
{
	/**
	 * Sends a test message
	 * @param sender The sender's email address
	 * @param host the smtp host
	 * @param auth if need authorization or not
	 * @param username the smtp server username, if auth is needed
	 * @param password the smtp server password, if auth is needed
	 * @param to the recipient
	 * @return The status message
	 */
	public static String sendTestMail(String sender, String host, String auth, 
			String  username, String password, String to)
	{
		/*// Save the current values
		String originalHost = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_HOST);
		String originalAuth = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_AUTH);
		String originalUsername = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_USERNAME);
		String originalPassword = SystemGlobals.getValue(ConfigKeys.MAIL_SMTP_PASSWORD);
		String originalSender = SystemGlobals.getValue(ConfigKeys.MAIL_SENDER);
		
		// Now put the new ones
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_HOST, host);
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_AUTH, auth);
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_USERNAME, username);
		SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_PASSWORD, password);
		SystemGlobals.setValue(ConfigKeys.MAIL_SENDER, sender);*/
		
		// Send the test mail
		class TestSpammer extends Spammer {
			public TestSpammer(final String to) {
				super.prepareMessage(new ArrayList() {{ add(to); }}, null, "JForum Test Mail", null);
			}
			
			protected String getMessageText(SimpleHash params, String messageFile) throws Exception {
				return ("Test mail from JForum Admin Panel. Sent at " + new Date());
			}
		}
		
		Spammer s = new TestSpammer(to);
		
		try {
			s.dispatchMessages();
		}
		catch (Exception e) {
			return e.toString();
		}
		finally {
			// Restore the original values
			/*SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_HOST, originalHost);
			SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_AUTH, originalAuth);
			SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_USERNAME, originalUsername);
			SystemGlobals.setValue(ConfigKeys.MAIL_SMTP_PASSWORD, originalPassword);
			SystemGlobals.setValue(ConfigKeys.MAIL_SENDER, originalSender);*/
		}
		
		return "OK";
	}
	
	/**
	 * Prepares a message for previwing
	 * @param p the post to preview
	 * @return the formatted post
	 */
	public static Post previewPost(Post p)
	{
		return PostCommon.preparePostForDisplay(p);
	}
}
