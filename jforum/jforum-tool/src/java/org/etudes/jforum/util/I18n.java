/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/I18n.java $
 * $Id: I18n.java 66776 2010-03-23 18:50:51Z murthy@etudes.org $
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

import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;







import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.entities.UserSession;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.sakaiproject.tool.cover.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.PreferencesService;
import org.sakaiproject.user.cover.UserDirectoryService;

/**
 * I18n (Internationalization) class implementation. Does nothing of special, just loads the
 * messages into memory and provides a static method to acess them.
 *
 * @author Rafael Steil
 * @author James Yong
 * 1/5/06 - Mallika - adding msg for not deleting forum
 */
public class I18n
{
	private static I18n classInstance = new I18n();
	private static Map messagesMap = new HashMap();
	private static Properties localeNames = new Properties();
	private static String defaultName;
	private static String baseDir;
	private static List watching = new ArrayList();
	public static final String CANNOT_DELETE_GROUP = "CannotDeleteGroup";
	public static final String CANNOT_DELETE_CATEGORY = "CannotDeleteCategory";
	public static final String CANNOT_DELETE_BANNER = "CannotDeleteBanner";
	public static final String CANNOT_DELETE_GRADED_CATEGORY = "CannotDeleteGradedCategory";

	//Mallika's new code beg
	public static final String CANNOT_DELETE_FORUM = "CannotDeleteForum";
	//Mallika's new code end

	private static final Log logger = LogFactory.getLog(I18n.class);

	private I18n() {}

	/**
	 * Gets the singleton
	 *
	 * @return Instance of I18n class
	 */
	public static I18n getInstance()
	{
		return classInstance;
	}

	/**
	 * Load the default I18n file
	 *
	 * @throws IOException
	 */
	public static synchronized void load() throws IOException
	{
		baseDir = SystemGlobals.getApplicationResourceDir() + "/" + SystemGlobals.getValue(ConfigKeys.LOCALES_DIR);

		loadLocales();

		defaultName = SystemGlobals.getValue(ConfigKeys.I18N_DEFAULT_ADMIN);
		load(defaultName, null);

		// String custom = SystemGlobals.getValue(ConfigKeys.I18N_DEFAULT);
		String custom = SakaiSystemGlobals.getValue(ConfigKeys.I18N_DEFAULT);
		if (!custom.equals(defaultName)) {
			load(custom, defaultName);
			defaultName = custom;
		}
	}

	public static void changeBoardDefault(String newDefaultLanguage) throws Exception
	{
		load(newDefaultLanguage, SystemGlobals.getValue(ConfigKeys.I18N_DEFAULT_ADMIN));
		defaultName = newDefaultLanguage;
	}

	private static void loadLocales() throws IOException
	{
		localeNames.load(new FileInputStream(baseDir + SystemGlobals.getValue(ConfigKeys.LOCALES_NAMES)));
	}

	static void load(String localeName, String mergeWith) throws IOException
	{
		load(localeName, mergeWith, false);
	}

	static void load(String localeName, String mergeWith, boolean force) throws IOException
	{
		if (!force && (localeName == null || localeName.trim().equals("") || I18n.contains(localeName))) {
			return;
		}

		if (localeNames.size() == 0) {
			loadLocales();
		}

		Properties p = new Properties();

		if (mergeWith != null) {
			if (!I18n.contains(mergeWith)) {
				load(mergeWith, null);
			}

			p.putAll((Properties) messagesMap.get(mergeWith));
		}

		p.load(new FileInputStream(baseDir + localeNames.getProperty(localeName)));
		messagesMap.put(localeName, p);

		watchForChanges(localeName);
	}

	/**
	 * Loads a new locale. If <code>localeName</code> is either null or empty, or if the locale is
	 * already loaded, the method will return without executing any code.
	 *
	 * @param localeName
	 *            The locale name to load
	 * @throws IOException
	 */
	public static void load(String localeName) throws IOException
	{
		// load(localeName, SystemGlobals.getValue(ConfigKeys.I18N_DEFAULT));
		load(localeName, SakaiSystemGlobals.getValue(ConfigKeys.I18N_DEFAULT));
	}

	public static void reset()
	{
		messagesMap = new HashMap();
		localeNames = new Properties();
		defaultName = null;
	}

	private static void watchForChanges(final String localeName)
	{
		if (!watching.contains(localeName)) {
			watching.add(localeName);

			int fileChangesDelay = SystemGlobals.getIntValue(ConfigKeys.FILECHANGES_DELAY);

			if (fileChangesDelay > 0) {
				FileMonitor.getInstance().addFileChangeListener(new FileChangeListener() {
					/**
					 * @see org.etudes.jforum.util.FileChangeListener#fileChanged(java.lang.String)
					 */
					public void fileChanged(String filename)
					{
						logger.info("Reloading i18n for " + localeName);

						try {
							// I18n.load(localeName, SystemGlobals.getValue(ConfigKeys.I18N_DEFAULT), true);
							I18n.load(localeName, SakaiSystemGlobals.getValue(ConfigKeys.I18N_DEFAULT), true);
						}
						catch (IOException e) {
							logger.warn(e);
							e.printStackTrace();
						}
					}
				}, baseDir + localeNames.getProperty(localeName), fileChangesDelay);
			}
		}
	}

	/**
	 * Gets a I18N (internationalized) message.
	 *
	 * @param localeName
	 *            The locale name to retrieve the messages from
	 * @param messageName
	 *            The message name to retrieve. Must be a valid entry into the file specified by
	 *            <code>i18n.file</code> property.
	 * @param params
	 *            Parameters needed by some messages. The messages with extra parameters are
	 *            formated according to {@link java.text.MessageFormat}specification
	 * @return String With the message
	 */
	public static String getMessage(String localeName, String messageName, Object params[])
	{
		return MessageFormat.format(((Properties) messagesMap.get(localeName)).getProperty(messageName), params);
	}

	/**
	 * @see #getMessage(String, String, Object[])
	 */
	public static String getMessage(String messageName, Object params[])
	{
		String lang = "";
		UserSession us = SessionFacade.getUserSession();
		if (us != null && us.getLang() != null) {
			lang = us.getLang();
		}

		if ("".equals(lang)) {
			return getMessage(defaultName, messageName, params);
		}

		return getMessage(lang, messageName, params);
	}

	/**
	 * Gets an I18N (internationalization) message.
	 *
	 * @param m
	 *            The message name to retrieve. Must be a valid entry into the file specified by
	 *            <code>i18n.file</code> property.
	 * @return String With the message
	 */
	public static String getMessage(String localeName, String m)
	{
		if (!messagesMap.containsKey(localeName)) {
			try {
				load(localeName);
			}
			catch (IOException e) {
				logger.warn("Error loading locale " + localeName + ". " + e.getMessage());
				return null;
			}
		}

		return (((Properties) messagesMap.get(localeName)).getProperty(m));
	}

	public static String getMessage(String m)
	{
		return getMessage(m, SessionFacade.getUserSession());
	}

	public static String getMessage(String m, UserSession us)
	{
		if (us == null) {
			return getMessage(defaultName, m);
		}
		
		String sakaiUser = us.getSakaiUserId();
		
		
		return getMessage(getUserLocale(sakaiUser), m);
	}

	public static String getLang()
	{
		UserSession currentUser =  SessionFacade.getUserSession();
	
		if (currentUser == null)
			return SakaiSystemGlobals.getValue(ConfigKeys.I18N_IMAGES_DIR);
		
		return getUserLocale(currentUser.getSakaiUserId());

	}

	private static String getUserLocale( String userId){
		if (PreferencesService.getLocale(userId) == null)
			return "fr_CA";
		return PreferencesService.getLocale(userId).toString();
	}
	/**
	 * This is used in the synoptic tool on the Home page
	 * @param m Key of the property of the text
	 * @return The property value
	 */
	public static String getSynopticMessage(String messageName)
	{
		return getMessage(defaultName, messageName);
	}

	/**
	 * This is used in the synoptic tool on the Home page
	 * @param m Key of the property of the text
	 * @return The property value
	 */
	public static String getSynopticMessage(String messageName,  Object params[])
	{
		return getMessage(defaultName, messageName, params);
	}

	/**
	 * Check whether the language is loaded in i18n.
	 *
	 * @param language
	 * @return boolean
	 */
	public static boolean contains(String language)
	{
		return messagesMap.containsKey(language);
	}

	/**
	 * Check if the given language exist.
	 *
	 * @param language
	 *            The language to check
	 * @return <code>true</code> if the language is a valid and registered translation.
	 */
	public static boolean languageExists(String language)
	{
		return (localeNames.getProperty(language) != null);
	}

	/**
	 * Returns default locale
	 * @return default locale
	 */
	public static String getDefaultLocale()
	{
		return defaultName;
	}

	public static float parseFloat(String source) throws ParseException {
		String[] localeProps = I18n.getLang().split("_");
		Locale locale = new Locale(localeProps[0], localeProps[1]);
		return DecimalFormat.getInstance(locale).parse(source).floatValue();
	}
}