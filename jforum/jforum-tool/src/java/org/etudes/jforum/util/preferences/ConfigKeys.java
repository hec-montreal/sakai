/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/util/preferences/ConfigKeys.java $ 
 * $Id: ConfigKeys.java 66776 2010-03-23 18:50:51Z murthy@etudes.org $ 
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
package org.etudes.jforum.util.preferences;

/**
* Encapsulate all configuration keys in constants. This is more typesafe and provides
* a nice overview of all configuration keys. Last but not least this lets us autocomplete
* configuration keys under eclipse ;-)
* 
* @author Pieter Olivier
* 1/31/06 - Mallika - Adding quota limit variable - this is different from the template 
* mapping variable that also has same name but will not be use by us since we are
* moving the functionality 
* 02/22/2006 - Murthy - added variables for avatar path
* 06/30/2006 - Murthy - added CALENDAR_DATE_TIME_FORMAT for calendar datetime format
* 12/04/2006 - Murthy - added variable for maximum number of users allowed to 
* 						copy in Private Messages
*/

public class ConfigKeys {

	// Configuration values for the keys in this section are set by the web-application itself
	public static final String APPLICATION_PATH = "application.path";
	public static final String INSTALLATION = "installation";
	public static final String INSTALLED = "installed";

	// The installation config file contains keys that are installation specific and differ from
	// the default config.
	public static final String INSTALLATION_CONFIG = "installation.config";
	
	public static final String FILECHANGES_DELAY = "file.changes.delay";
//	public static final String DATABASE_PING_DELAY = "database.ping.delay";
//	public static final String DATABASE_CONNECTION_IMPLEMENTATION = "database.connection.implementation";
//	public static final String DATABASE_DRIVER_NAME = "database.driver.name";
//	public static final String DATABASE_DRIVER_CONFIG = "database.driver.config";
//	public static final String DATABASE_CONNECTION_HOST = "database.connection.host";
//	public static final String DATABASE_CONNECTION_USERNAME = "database.connection.username";
//	public static final String DATABASE_CONNECTION_PASSWORD = "database.connection.password";
//	public static final String DATABASE_CONNECTION_DBNAME = "dbname";
//	public static final String DATABASE_CONNECTION_ENCODING = "dbencoding";
//	public static final String DATABASE_CONNECTION_DRIVER = "database.connection.driver";
//	public static final String DATABASE_CONNECTION_STRING = "database.connection.string";
//	public static final String DATABASE_POOL_MIN = "database.connection.pool.min";
//	public static final String DATABASE_POOL_MAX = "database.connection.pool.max";
//	public static final String DATABASE_USE_TRANSACTIONS = "database.use.transactions";
//	public static final String DATABASE_DATASOURCE_NAME = "database.datasource.name";
	public static final String DATABASE_ERROR_PAGE = "database.error.page";
	public static final String DATABASE_MYSQL_UNICODE = "mysql.unicode";
	public static final String DATABASE_MYSQL_ENCODING = "mysql.encoding";
//	public static final String DATABASE_AUTO_KEYS = "database.support.autokeys";
	
	public static final String AUTHENTICATION_TYPE = "authentication.type";
	public static final String SSO_IMPLEMENTATION = "sso.implementation";
	public static final String LOGIN_AUTHENTICATOR = "login.authenticator";
	public static final String TYPE_DEFAULT = "default";
	public static final String TYPE_SSO = "sso";
	public static final String AUTO_LOGIN_ENABLED = "auto.login.enabled";
	
	public static final String SSO_PASSWORD_ATTRIBUTE = "sso.password.attribute";
	public static final String SSO_EMAIL_ATTRIBUTE = "sso.email.attribute";
	public static final String SSO_DEFAULT_PASSWORD = "sso.default.password";
	public static final String SSO_DEFAULT_EMAIL = "sso.default.email";
	public static final String SSO_REDIRECT = "sso.redirect";
	
	public static final String RESOURCE_DIR = "resource.dir";
	public static final String CONFIG_DIR = "config.dir";
	public static final String DATABASE_PROPERTIES = "database.properties";
	public static final String DATABASE_DRIVER_PROPERTIES = "database.driver.properties";
	public static final String SQL_QUERIES_GENERIC = "sql.queries.generic";
	public static final String SQL_QUERIES_DRIVER = "sql.queries.driver";

	public static final String TEMPLATES_MAPPING = "templates.mapping";
	public static final String TEMPLATE_DIR = "template.dir";
	// public static final String ENCODING = "encoding";
	public static final String ENCODING = "etudes.jforum.encoding";
	public static final String DEFAULT_CONTAINER_ENCODING = "default.container.encoding";
	public static final String SERVLET_NAME = "servlet.name";
	public static final String DEFAULT_CONFIG = "default.config";
	public static final String CONTEXT_NAME = "context.name";
	public static final String SERVLET_EXTENSION = "servlet.extension";
	public static final String SYNOPTIC_SERVLET_EXTENSION = "synoptic.servlet.extension";
	public static final String COOKIE_NAME_DATA = "cookie.name.data";
	public static final String COOKIE_NAME_USER = "cookie.name.user";
	public static final String COOKIE_AUTO_LOGIN = "cookie.name.autologin";
	public static final String COOKIE_USER_HASH = "cookie.name.userHash";
		
	public static final String ANONYMOUS_USER_ID = "anonymous.userId";
	public static final String DEFAULT_USER_GROUP = "defaultUserGroup";
	public static final String USER_HASH_SEQUENCE = "user.hash.sequence";
	public static final String TOPICS_TRACKING = "topics.tracking";
	
	public static final String TOPIC_CACHE_ENABLED = "topic.cache.enabled";
	public static final String SECURITY_CACHE_ENABLED = "security.cache.enabled";
	public static final String FORUM_CACHE_ENABLED = "forum.cache.enabled";
	//public static final String CATEGORY_CACHE_ENABLED = "category.cache.enabled";
	public static final String CATEGORY_CACHE_ENABLED = "etudes.category.cache.enabled";

	public static final String VERSION = "version";
	public static final String BACKGROUND_TASKS = "background.tasks";
	public static final String REQUEST_DUMP = "request.dump";

	public static final String FORUM_LINK = "forum.link";
	public static final String HOMEPAGE_LINK = "homepage.link";
	public static final String FORUM_NAME = "forum.name";
	public static final String FORUM_PAGE_TITLE = "forum.page.title";
	public static final String FORUM_PAGE_METATAG_KEYWORDS = "forum.page.metatag.keywords";
	public static final String FORUM_PAGE_METATAG_DESCRIPTION = "forum.page.metatag.description";

	public static final String TMP_DIR = "tmp.dir";
	// public static final String CACHE_DIR = "cache.dir";

//	public static final String DAO_DRIVER = "dao.driver";

	// public static final String DATE_TIME_FORMAT = "dateTime.format";
	public static final String DATE_TIME_FORMAT = "etudes.jforum.dateTime.format";
	//06/30/2006 Murthy - added for calendar date format
	//public static final String CALENDAR_DATE_TIME_FORMAT = "calendar.dateTime.format";
	public static final String CALENDAR_DATE_TIME_FORMAT = "etudes.jforum.calendar.dateTime.format";
	
	public static final String RSS_DATE_TIME_FORMAT = "rss.datetime.format";
	public static final String RSS_ENABLED = "rss.enabled";

	// public static final String HOT_TOPIC_BEGIN = "hot.topic.begin";
	public static final String HOT_TOPIC_BEGIN = "etudes.jforum.hot.topic.begin";
	// public static final String TOPICS_PER_PAGE = "topicsPerPage";
	public static final String TOPICS_PER_PAGE = "etudes.jforum.topicsPerPage";
	// public static final String POST_PER_PAGE = "postsPerPage";
	public static final String POST_PER_PAGE = "etudes.jforum.postsPerPage";
	// public static final String USERS_PER_PAGE = "usersPerPage";
	public static final String USERS_PER_PAGE = "etudes.jforum.usersPerPage";
	// public static final String RECENT_TOPICS = "topic.recent";
	public static final String RECENT_TOPICS = "etudes.jforum.topic.recent";
	public static final String POSTS_CACHE_SIZE = "posts.cache.size";
	public static final String POSTS_CACHE_ENABLED = "posts.cache.enabled";

	public static final String CAPTCHA_REGISTRATION = "captcha.registration";
	public static final String CAPTCHA_POSTS = "captcha.posts";
	public static final String CAPTCHA_WIDTH = "captcha.width";
	public static final String CAPTCHA_HEIGHT = "captcha.height";
	public static final String CAPTCHA_MIN_FONT_SIZE = "captcha.min.font.size";
	public static final String CAPTCHA_MAX_FONT_SIZE = "captcha.max.font.size";
	public static final String CAPTCHA_MIN_WORDS = "captcha.min.words";
	public static final String CAPTCHA_MAX_WORDS = "captcha.max.words";
	
	// public static final String I18N_DEFAULT = "i18n.board.default";
	public static final String I18N_DEFAULT = "etudes.jforum.i18n.board.default";
	public static final String I18N_DEFAULT_ADMIN = "i18n.internal";
	// public static final String I18N_IMAGES_DIR = "i18n.images.dir";
	public static final String I18N_IMAGES_DIR = "etudes.jforum.i18n.images.dir";
	public static final String LOCALES_DIR = "locales.dir";
	public static final String LOCALES_NAMES = "locales.names";

	public static final String MAIL_LOST_PASSWORD_MESSAGE_FILE = "mail.lostPassword.messageFile";
	public static final String MAIL_LOST_PASSWORD_SUBJECT = "mail.lostPassword.subject";
	public static final String MAIL_NOTIFY_ANSWERS = "mail.notify.answers";
	// public static final String MAIL_SENDER = "mail.sender";
	public static final String MAIL_CHARSET = "mail.charset";
	public static final String MAIL_TEMPLATE_ENCODING = "mail.template.encoding";
	public static final String MAIL_NEW_ANSWER_MESSAGE_FILE = "mail.newAnswer.messageFile";
	public static final String MAIL_NEW_ANSWER_SUBJECT = "mail.newAnswer.subject";
	public static final String MAIL_NEW_PM_SUBJECT = "mail.newPm.subject";
	public static final String MAIL_NEW_PM_MESSAGE_FILE = "mail.newPm.messageFile";
	//12/04/2006 - Murthy - Maximum number of users allowed to copy in Private Messages
	//public static final String MAX_PM_TOUSERS="maxPMToUsers";
	public static final String MAX_PM_TOUSERS="etudes.jforum.maxPMToUsers";
	public static final String MAIL_MESSSAGE_FORMAT = "mail.messageFormat";
	// public static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
	// public static final String MAIL_SMTP_HOST = "mail.smtp.host";
	public static final String MAIL_SMTP_HOST = "etudes.jforum.mail.smtp.host";
	// public static final String MAIL_SMTP_LOCALHOST = "mail.smtp.localhost";
	// public static final String MAIL_SMTP_USERNAME = "mail.smtp.username";
	// public static final String MAIL_SMTP_PASSWORD = "mail.smtp.password";
	// public static final String MAIL_USER_EMAIL_AUTH = "mail.user.email.auth";
	public static final String MAIL_ACTIVATION_KEY_MESSAGE_FILE = "mail.activationKey.messageFile";
	public static final String MAIL_ACTIVATION_KEY_SUBJECT = "mail.activationKey.subject";
	//11/21/2006 - Email message for new topic
	public static final String MAIL_NEW_TOPIC_MESSAGE_FILE = "mail.newTopic.messageFile";
	
	// public static final String HTML_TAGS_WELCOME = "html.tags.welcome";
	public static final String HTML_TAGS_WELCOME = "etudes.jforum.html.tags.welcome";

	public static final String SMILIE_IMAGE_DIR = "smilie.image.dir";
	public static final String SMILIE_IMAGE_PATTERN = "smilie.image.pattern";

	// public static final String AVATAR_MAX_WIDTH = "avatar.maxWidth";
	public static final String AVATAR_MAX_WIDTH = "etudes.jforum.avatar.maxWidth";
	// public static final String AVATAR_MAX_HEIGHT = "avatar.maxHeight";
	public static final String AVATAR_MAX_HEIGHT = "etudes.jforum.avatar.maxHeight";
	
	//<<<02/22/2006 - Murthy - avatar path
	public static final String AVATAR_CLUSTERED = "etudes.jforum.avatar.clustered";
	public static final String AVATAR_PATH = "etudes.jforum.avatar.path";
	public static final String AVATAR_CONTEXT = "etudes.jforum.avatar.context";
	//>>>02/22/2006 - Murthy
	//<<<11/03/2006 - Murthy - packagin path
	// public static final String PACKAGING_DIR = "packaging.path";
	public static final String PACKAGING_DIR = "etudes.jforum.packaging.path";

	public static final String MOST_USERS_EVER_ONLINE = "most.users.ever.online";
	public static final String MOST_USER_EVER_ONLINE_DATE = "most.users.ever.online.date";
	
	// public static final String JBOSS_CACHE_PROPERTIES_FILE = "jboss.cache.properties.file";
	public static final String DEFAULT_JBOSS_CACHE_PROPERTIES_FILE = "jboss.cache.properties.file";
	public static final String JBOSS_CACHE_PROPERTIES_FILE = "etudes.jforum.jboss.cache.properties.file";
	
	//public static final String CACHE_IMPLEMENTATION = "cache.engine.implementation";
	public static final String CACHE_IMPLEMENTATION = "etudes.jforum.cache.engine.implementation";
	
	// public static final String ATTACHMENTS_MAX_POST = "attachments.max.post";
	public static final String ATTACHMENTS_MAX_POST = "etudes.jforum.attachments.max.post";
	// public static final String ATTACHMENTS_IMAGES_CREATE_THUMB = "attachments.images.createthumb";
	public static final String ATTACHMENTS_IMAGES_CREATE_THUMB = "etudes.jforum.attachments.images.createthumb";
	// public static final String ATTACHMENTS_IMAGES_MIN_THUMB_W = "attachments.images.thumb.minsize.w";
	public static final String ATTACHMENTS_IMAGES_MIN_THUMB_W = "etudes.jforum.attachments.images.thumb.minsize.w";
	// public static final String ATTACHMENTS_IMAGES_MIN_THUMB_H = "attachments.images.thumb.minsize.h";
	public static final String ATTACHMENTS_IMAGES_MIN_THUMB_H = "etudes.jforum.attachments.images.thumb.minsize.h";
	public static final String ATTACHMENTS_ICON = "attachments.icon";
	public static final String ATTACHMENTS_DOWNLOAD_MODE = "attachments.download.mode";
	//public static final String ATTACHMENTS_STORE_DIR = "attachments.store.dir";
	public static final String ATTACHMENTS_STORE_DIR = "etudes.jforum.attachments.store.dir";
	public static final String ATTACHMENTS_UPLOAD_DIR = "attachments.upload.dir";
	//Mallika - new code beg
	// public static final String ATTACHMENTS_QUOTA_LIMIT = "attachments.quota.limit";	
	public static final String ATTACHMENTS_QUOTA_LIMIT = "etudes.jforum.attachments.quota.limit";
	//Mallika - new code end
	
	// public static final String REGISTRATION_ENABLED = "registration.enabled";
	public static final String USERNAME_MAX_LENGTH = "username.max.length";

	public static final String QUARTZ_CONTEXT = "org.quartz.context.";
	public static final String SEARCH_INDEXING_ENABLED = "search.indexing.enabled";
	public static final String SEARCH_INDEXER_IMPLEMENTATION = "search.indexer.implementation";
	public static final String SEARCH_INDEXER_QUARTZ_CONFIG = "search.indexer.quartz.config";
	public static final String SEARCH_INDEXER_CRON_EXPRESSON = "indexer.cron.expression";
	public static final String SEARCH_LAST_POST_ID = "last.post.id";
	public static final String SEARCH_INDEXER_STEP = "indexer.step";
	public static final String SEARCH_MIN_WORD_SIZE = "search.min.word.size";
	public static final String SEARCH_WORD_MATCHING = "search.word.matching";
	
	public static final String TOPIC_TIME_FIELD = "topic.time.field";
	public static final String EXTENSION_FIELD = "extension.field";
	
	public static final String LDAP_SECURITY_PROTOCOL = "ldap.security.protocol";
	public static final String LDAP_AUTHENTICATION = "ldap.authentication";
	public static final String LDAP_FACTORY = "ldap.factory";
	public static final String LDAP_USER_PREFIX = "ldap.user.prefix";
	public static final String LDAP_ORGANIZATION_PREFIX = "ldap.organization.prefix";
	public static final String LDAP_SERVER_URL = "ldap.server.url";
	public static final String LDAP_FIELD_EMAIL = "ldap.field.email";
	
	public static final String CLICKSTREAM_CONFIG = "clickstream.config";
	public static final String IS_BOT = "clickstream.is.bot";

	public static final String POSTS_NEW_DELAY = "posts.new.delay";
	public static final String LAST_POST_TIME = "last.post.time";

	public static final String KARMA_MIN_POINTS = "karma.min.points";
	public static final String KARMA_MAX_POINTS = "karma.max.points";
	
	// Added to support cluster config -- JMH
	// public static final String LOCAL_CONFIG_DIR = "local.config.dir";
	public static final String LOCAL_CONFIG_DIR = "etudes.jforum.local.config.dir";
	
	//For forum grading
	// public static final String GRADEBOOK_TOOL_ID = "sakai.gradebook.tool.id";
	public static final String GRADEBOOK_TOOL_ID = "etudes.jforum.sakai.gradebook.tool.id";
	public static final String JFORUM_TOOL_ID = "etudes.jforum.sakai.jforum.tool.id";
	
	private ConfigKeys() {}
}
