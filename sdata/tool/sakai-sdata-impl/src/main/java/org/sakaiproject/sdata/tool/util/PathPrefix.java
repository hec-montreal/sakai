/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/tfd/branches/sdata-ct25x/tool/sakai-sdata-impl/src/main/java/org/sakaiproject/sdata/tool/util/PathPrefix.java $
 * $Id: PathPrefix.java 45722 2008-02-19 11:56:15Z ian@caret.cam.ac.uk $
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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO Javadoc
 * @author ieb
 */
public class PathPrefix
{

	private static final Log log = LogFactory.getLog(PathPrefix.class);

	/**
 * TODO Javadoc
	 * @param user
	 * @return
	 */
	public static String getPrefix(String user)
	{
		MessageDigest md;
		if (user != null)
		{
			try
			{
				md = MessageDigest.getInstance("SHA-1");
				byte[] userHash = md.digest(user.getBytes("UTF-8"));

				if (user.length() == 0)
				{
					user = "anon";
				}

				char[] chars = new char[8 + user.length()];
				byte current = userHash[0];
				int hi = (current & 0xF0) >> 4;
				int lo = current & 0x0F;
				chars[0] = '/';
				chars[1] = (char) (hi < 10 ? ('0' + hi) : ('A' + hi - 10));
				chars[2] = (char) (lo < 10 ? ('0' + lo) : ('A' + lo - 10));
				current = userHash[1];
				hi = (current & 0xF0) >> 4;
				lo = current & 0x0F;
				chars[3] = '/';
				chars[4] = (char) (hi < 10 ? ('0' + hi) : ('A' + hi - 10));
				chars[5] = (char) (lo < 10 ? ('0' + lo) : ('A' + lo - 10));
				chars[6] = '/';
				for (int i = 0; i < user.length(); i++)
				{
					char c = user.charAt(i);
					if (!Character.isLetterOrDigit(c))
					{
						c = '_';
					}
					chars[i + 7] = c;
				}
				chars[7 + user.length()] = '/';
				return new String(chars);
			}
			catch (NoSuchAlgorithmException e)
			{
				log.error(e);
			}
			catch (UnsupportedEncodingException e)
			{
				log.error(e);
			}
			if (user.length() == 0)
			{
				user = "anon";
			}

			char[] chars = new char[2 + user.length()];
			chars[0] = '/';
			for (int i = 0; i < user.length(); i++)
			{
				char c = user.charAt(i);
				if (!Character.isLetterOrDigit(c))
				{
					c = '_';
				}
				chars[i + 1] = c;
			}
			chars[1 + user.length()] = '/';
			return new String(chars);
		}
		return null;
	}
}
