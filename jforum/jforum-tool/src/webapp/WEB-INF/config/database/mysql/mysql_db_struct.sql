---------------------------------------------------------------------------------- 
-- $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/WEB-INF/config/database/mysql/mysql_db_struct.sql $ 
-- $Id: mysql_db_struct.sql 71044 2010-10-29 17:37:24Z murthy@etudes.org $ 
----------------------------------------------------------------------------------- 
-- 
-- Copyright (c) 2008, 2009, 2010 Etudes, Inc. 
-- 
-- Licensed under the Apache License, Version 2.0 (the "License"); 
-- you may not use this file except in compliance with the License. 
-- You may obtain a copy of the License at 
-- 
-- http://www.apache.org/licenses/LICENSE-2.0 
-- 
-- Unless required by applicable law or agreed to in writing, software 
-- distributed under the License is distributed on an "AS IS" BASIS, 
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
-- See the License for the specific language governing permissions and 
-- limitations under the License. 
-- 
-- Portions completed before July 1, 2004 Copyright (c) 2003, 2004 Rafael Steil, All rights reserved, licensed under the BSD license. 
-- http://www.opensource.org/licenses/bsd-license.php 
-- 
-- Redistribution and use in source and binary forms, 
-- with or without modification, are permitted provided 
-- that the following conditions are met: 
-- 
-- 1) Redistributions of source code must retain the above 
-- copyright notice, this list of conditions and the 
-- following disclaimer. 
-- 2) Redistributions in binary form must reproduce the 
-- above copyright notice, this list of conditions and 
-- the following disclaimer in the documentation and/or 
-- other materials provided with the distribution. 
-- 3) Neither the name of "Rafael Steil" nor 
-- the names of its contributors may be used to endorse 
-- or promote products derived from this software without 
-- specific prior written permission. 
-- 
-- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
-- HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
-- EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
-- BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
-- MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
-- PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
-- THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
-- FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
-- EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
-- (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
-- SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
-- OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
-- CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
-- IN CONTRACT, STRICT LIABILITY, OR TORT 
-- (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
-- ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
-- ADVISED OF THE POSSIBILITY OF SUCH DAMAGE 
----------------------------------------------------------------------------------
-- MySQL dump 9.07
--
-- Host: localhost    Database: jforum
---------------------------------------------------------
-- Server version	4.0.12-nt

--
-- Table structure for table 'jforum_banlist'
--

--DROP TABLE IF EXISTS jforum_banlist;
CREATE TABLE jforum_banlist (
  banlist_id mediumint(8) NOT NULL auto_increment,
  user_id mediumint(8) NOT NULL default '0',
  banlist_ip varchar(8) NOT NULL default '',
  banlist_email varchar(255) default NULL,
  PRIMARY KEY  (banlist_id),
  INDEX idx_user (user_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_categories'
--

--DROP TABLE IF EXISTS jforum_categories;
CREATE TABLE jforum_categories (
  categories_id mediumint(8) NOT NULL auto_increment,
  title varchar(100) NOT NULL default '',
  display_order mediumint(8) NOT NULL default '0',
  moderated TINYINT(1) DEFAULT '0',
  PRIMARY KEY  (categories_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_config'
--

--DROP TABLE IF EXISTS jforum_config;
CREATE TABLE jforum_config (
  config_name varchar(255) NOT NULL default '',
  config_value varchar(255) NOT NULL default '',
  config_id int not null auto_increment,
  PRIMARY KEY(config_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_forums'
--

--DROP TABLE IF EXISTS jforum_forums;
CREATE TABLE jforum_forums (
  forum_id mediumint(8) UNSIGNED NOT NULL auto_increment,
  categories_id mediumint(8) NOT NULL default '1',
  forum_name varchar(150) NOT NULL default '',
  forum_desc varchar(255) default NULL,
  forum_order mediumint(8) default '1',
  forum_topics mediumint(8) NOT NULL default '0',
  forum_last_post_id mediumint(8) NOT NULL default '0',
  moderated TINYINT(1) DEFAULT '0',
  PRIMARY KEY  (forum_id),
  KEY (categories_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_groups'
--

--DROP TABLE IF EXISTS jforum_groups;
CREATE TABLE jforum_groups (
  group_id mediumint(8) NOT NULL auto_increment,
  group_name varchar(40) NOT NULL default '',
  group_description varchar(255) default NULL,
  parent_id mediumint(8) default '0',
  PRIMARY KEY  (group_id)
) TYPE=InnoDB;


--DROP TABLE IF EXISTS jforum_user_groups;
CREATE TABLE jforum_user_groups (
	group_id INT NOT NULL,
	user_id INT NOT NULL,
	INDEX idx_group (group_id),
	INDEX idx_user (user_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_roles'
--

--DROP TABLE IF EXISTS jforum_roles;
CREATE TABLE jforum_roles (
  role_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  group_id mediumint(8) default '0',
  user_id mediumint(8) default '0',
  name varchar(255) NOT NULL,
  role_type TINYINT(1) DEFAULT 1,
  INDEX idx_group (group_id),
  INDEX idx_user (user_id),
  INDEX idx_name (name)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_role_values'
--
--DROP TABLE IF EXISTS jforum_role_values;
CREATE TABLE jforum_role_values (
  role_id INT NOT NULL,
  role_value VARCHAR(255),
  role_type TINYINT(1) DEFAULT 1,
  INDEX idx_role(role_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_posts'
--

--DROP TABLE IF EXISTS jforum_posts;
CREATE TABLE jforum_posts (
  post_id mediumint(8) NOT NULL auto_increment,
  topic_id mediumint(8) NOT NULL default '0',
  forum_id mediumint(8) UNSIGNED NOT NULL default '0',
  user_id mediumint(8) default NULL,
  post_time datetime default NULL,
  poster_ip varchar(15) default NULL,
  enable_bbcode tinyint(1) NOT NULL default '1',
  enable_html tinyint(1) NOT NULL default '1',
  enable_smilies tinyint(1) NOT NULL default '1',
  enable_sig tinyint(1) NOT NULL default '1',
  post_edit_time datetime default NULL,
  post_edit_count smallint(5) NOT NULL default '0',
  status tinyint(1) default '1',
  attach TINYINT(1) DEFAULT '0',
  need_moderate TINYINT(1) DEFAULT '0',
  PRIMARY KEY  (post_id),
  KEY (user_id),
  KEY (topic_id),
  KEY (forum_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_posts_text'
--
--DROP TABLE IF EXISTS jforum_posts_text;
CREATE TABLE jforum_posts_text (
	post_id MEDIUMINT(8) NOT NULL PRIMARY KEY,
	post_text TEXT,
	post_subject VARCHAR(100)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_privmsgs'
--

--DROP TABLE IF EXISTS jforum_privmsgs;
CREATE TABLE jforum_privmsgs (
  privmsgs_id mediumint(8) NOT NULL auto_increment,
  privmsgs_type tinyint(4) NOT NULL default '0',
  privmsgs_subject varchar(255) NOT NULL default '',
  privmsgs_from_userid mediumint(8) NOT NULL default '0',
  privmsgs_to_userid mediumint(8) NOT NULL default '0',
  privmsgs_date datetime default null,
  privmsgs_ip varchar(8) NOT NULL default '',
  privmsgs_enable_bbcode tinyint(1) NOT NULL default '1',
  privmsgs_enable_html tinyint(1) NOT NULL default '0',
  privmsgs_enable_smilies tinyint(1) NOT NULL default '1',
  privmsgs_attach_sig tinyint(1) NOT NULL default '1',
  PRIMARY KEY  (privmsgs_id)
) TYPE=InnoDB;

CREATE INDEX idx_jforum_privmsgs_privmsgs_to_userid ON jforum_privmsgs (privmsgs_to_userid);

--DROP TABLE IF EXISTS jforum_privmsgs_text;
CREATE TABLE jforum_privmsgs_text (
	privmsgs_id MEDIUMINT(8) NOT NULL,
	privmsgs_text TEXT,
	PRIMARY KEY ( privmsgs_id )
) Type=InnoDB;

--
-- Table structure for table 'jforum_ranks'
--

--DROP TABLE IF EXISTS jforum_ranks;
CREATE TABLE jforum_ranks (
  rank_id smallint(5) NOT NULL auto_increment,
  rank_title varchar(50) NOT NULL default '',
  rank_min mediumint(8) NOT NULL default '0',
  rank_special tinyint(1) default NULL,
  rank_image varchar(255) default NULL,
  PRIMARY KEY  (rank_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_sessions'
--

--DROP TABLE IF EXISTS jforum_sessions;
CREATE TABLE jforum_sessions (
  session_id varchar(50) NOT NULL default '',
  session_user_id mediumint(8) NOT NULL default '0',
  session_start datetime default null,
  session_time bigint default '0',
  session_ip varchar(8) NOT NULL default '',
  session_page int(11) NOT NULL default '0',
  session_logged_int tinyint(1) default NULL
) TYPE=InnoDB;

CREATE INDEX jf_js_session_user_id ON jforum_sessions(session_user_id);

--
-- Table structure for table 'jforum_smilies'
--

--DROP TABLE IF EXISTS jforum_smilies;
CREATE TABLE jforum_smilies (
  smilie_id smallint(5) NOT NULL auto_increment,
  code varchar(50) NOT NULL default '',
  url varchar(100) default NULL,
  disk_name varchar(255),
  PRIMARY KEY  (smilie_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_themes'
--

--DROP TABLE IF EXISTS jforum_themes;
CREATE TABLE jforum_themes (
  themes_id mediumint(8) NOT NULL auto_increment,
  template_name varchar(30) NOT NULL default '',
  style_name varchar(30) NOT NULL default '',
  PRIMARY KEY  (themes_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_topics'
--

--DROP TABLE IF EXISTS jforum_topics;
CREATE TABLE jforum_topics (
  topic_id mediumint(8) NOT NULL auto_increment,
  forum_id mediumint(8) UNSIGNED NOT NULL default '0',
  topic_title varchar(100) NOT NULL default '',
  user_id mediumint(8) NOT NULL default '0',
  topic_time datetime default null,
  topic_views mediumint(8) default '1',
  topic_replies mediumint(8) default '0',
  topic_status tinyint(3) default '0',
  topic_vote tinyint(1) default '0',
  topic_type tinyint(3) default '0',
  topic_first_post_id mediumint(8) default '0',
  topic_last_post_id mediumint(8) NOT NULL default '0',
  moderated TINYINT(1) DEFAULT '0',
  PRIMARY KEY  (topic_id),
  KEY (forum_id),
  KEY(user_id),
  KEY(topic_first_post_id),
  KEY(topic_last_post_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_topics_watch'
--

--DROP TABLE IF EXISTS jforum_topics_watch;
CREATE TABLE jforum_topics_watch (
  topic_id mediumint(8) NOT NULL default '0',
  user_id mediumint(8) NOT NULL default '0',
  is_read tinyint(1) NOT NULL DEFAULT '0',
  INDEX idx_topic (topic_id),
  INDEX idx_user (user_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_users'
--

--DROP TABLE IF EXISTS jforum_users;
CREATE TABLE jforum_users (
  user_id mediumint(8) NOT NULL auto_increment,
  user_active tinyint(1) default NULL,
  username varchar(50) NOT NULL default '',
  user_password varchar(32) NOT NULL default '',
  user_session_time bigint default 0,
  user_session_page smallint(5) NOT NULL default '0',
  user_lastvisit datetime default null,
  user_regdate datetime default null,
  user_level tinyint(4) default NULL,
  user_posts mediumint(8) NOT NULL default '0',
  user_timezone varchar(5) NOT NULL default '',
  user_style tinyint(4) default NULL,
  user_lang varchar(255) NOT NULL default '',
  user_dateformat varchar(20) NOT NULL default '%d/%M/%Y %H:%i',
  user_new_privmsg smallint(5) NOT NULL default '0',
  user_unread_privmsg smallint(5) NOT NULL default '0',
  user_last_privmsg datetime NULL,
  user_emailtime datetime default NULL,
  user_viewemail tinyint(1) default '0',
  user_attachsig tinyint(1) default '1',
  user_allowhtml tinyint(1) default '0',
  user_allowbbcode tinyint(1) default '1',
  user_allowsmilies tinyint(1) default '1',
  user_allowavatar tinyint(1) default '1',
  user_allow_pm tinyint(1) default '1',
  user_allow_viewonline tinyint(1) default '1',
  user_notify tinyint(1) default '1',
  user_notify_pm tinyint(1) default '1',
  user_popup_pm tinyint(1) default '1',
  rank_id smallint(5) default '1',
  user_avatar varchar(100) default NULL,
  user_avatar_type tinyint(4) NOT NULL default '0',
  user_email varchar(255) NOT NULL default '',
  user_icq varchar(15) default NULL,
  user_website varchar(100) default NULL,
  user_from varchar(100) default NULL,
  user_sig text,
  user_sig_bbcode_uid varchar(10) default NULL,
  user_aim varchar(255) default NULL,
  user_yim varchar(255) default NULL,
  user_msnm varchar(255) default NULL,
  user_occ varchar(100) default NULL,
  user_interests varchar(255) default NULL,
  user_actkey varchar(32) default NULL,
  gender char(1) default NULL,
  themes_id mediumint(8) default NULL,
  deleted tinyint(1) default NULL,
  user_viewonline tinyint(1) default '1',
  security_hash varchar(32),
  user_karma DECIMAL(10,2),
  PRIMARY KEY  (user_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_vote_desc'
--

--DROP TABLE IF EXISTS jforum_vote_desc;
CREATE TABLE jforum_vote_desc (
  vote_id mediumint(8) NOT NULL auto_increment,
  topic_id mediumint(8) NOT NULL default '0',
  vote_text text NOT NULL,
  vote_start int(11) NOT NULL default '0',
  vote_length int(11) NOT NULL default '0',
  PRIMARY KEY  (vote_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_vote_results'
--

--DROP TABLE IF EXISTS jforum_vote_results;
CREATE TABLE jforum_vote_results (
  vote_id mediumint(8) NOT NULL default '0',
  vote_option_id tinyint(4) NOT NULL default '0',
  vote_option_text varchar(255) NOT NULL default '',
  vote_result int(11) NOT NULL default '0'
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_vote_voters'
--

--DROP TABLE IF EXISTS jforum_vote_voters;
CREATE TABLE jforum_vote_voters (
  vote_id mediumint(8) NOT NULL default '0',
  vote_user_id mediumint(8) NOT NULL default '0',
  vote_user_ip char(8) NOT NULL default ''
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_words'
--

--DROP TABLE IF EXISTS jforum_words;
CREATE TABLE jforum_words (
  word_id mediumint(8) NOT NULL auto_increment,
  word varchar(100) NOT NULL default '',
  replacement varchar(100) NOT NULL default '',
  PRIMARY KEY  (word_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_search_words'
--
--DROP TABLE IF EXISTS jforum_search_words;
CREATE TABLE jforum_search_words (
  word_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  word VARCHAR(100) NOT NULL,
  word_hash INT,
  KEY(word),
  KEY(word_hash)
) TYPE=InnoDB;

-- 
-- Table structure for table 'jforum_search_wordmatch'
--
--DROP TABLE IF EXISTS jforum_search_wordmatch;
CREATE TABLE jforum_search_wordmatch (
  post_id INT NOT NULL,
  word_id INT NOT NULL,
  title_match TINYINT(1) DEFAULT '0',
  KEY(post_id),
  KEY(word_id),
  KEY(title_match)
) TYPE=InnoDB;

CREATE INDEX post_id_word_id ON jforum_search_wordmatch(post_id, word_id);

--
-- Table structure for table 'jforum_search_results'
--
--DROP TABLE IF EXISTS jforum_search_results;
CREATE TABLE jforum_search_results (
  topic_id INT NOT NULL,
  session VARCHAR(50),
  search_time DATETIME,
  KEY (topic_id)
) TYPE=InnoDB;


--DROP TABLE IF EXISTS jforum_search_topics;
CREATE TABLE jforum_search_topics (
  topic_id mediumint(8) NOT NULL,
  forum_id mediumint(8) UNSIGNED NOT NULL default '0',
  topic_title varchar(100) NOT NULL default '',
  user_id mediumint(8) NOT NULL default '0',
  topic_time datetime default null,
  topic_views mediumint(8) default '1',
  topic_replies mediumint(8) default '0',
  topic_status tinyint(3) default '0',
  topic_vote tinyint(1) default '0',
  topic_type tinyint(3) default '0',
  topic_first_post_id mediumint(8) default '0',
  topic_last_post_id mediumint(8) NOT NULL default '0',
  moderated smallint(1) default '0',
  session varchar(50),
  search_time datetime,
  KEY  (topic_id),
  KEY (forum_id),
  KEY(user_id),
  KEY(topic_first_post_id),
  KEY(topic_last_post_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_karma'
--
--DROP TABLE IF EXISTS jforum_karma;
CREATE TABLE jforum_karma (
	karma_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	post_id INT NOT NULL,
	topic_id INT NOT NULL,
	post_user_id INT NOT NULL,
	from_user_id INT NOT NULL,
	points INT NOT NULL,
	rate_date datetime NULL,
	KEY(post_id),
	KEY(topic_id),
	KEY(post_user_id),
	KEY(from_user_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_bookmark'
--
--DROP TABLE IF EXISTS jforum_bookmarks;
CREATE TABLE jforum_bookmarks (
	bookmark_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	user_id INT NOT NULL,
	relation_id INT NOT NULL,
	relation_type INT NOT NULL,
	public_visible INT DEFAULT '1',
	title varchar(255),
	description varchar(255),
	INDEX book_idx_relation (relation_id),
	KEY(user_id)
) TYPE=InnoDB;
-- 
-- Table structure for table 'jforum_quota_limit'
--
--DROP TABLE IF EXISTS jforum_quota_limit;
CREATE TABLE jforum_quota_limit (
	quota_limit_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	quota_desc VARCHAR(50) NOT NULL,
	quota_limit INT NOT NULL,
	quota_type TINYINT(1) DEFAULT '1'
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_extension_groups'
--
--DROP TABLE IF EXISTS jforum_extension_groups;
CREATE TABLE jforum_extension_groups (
	extension_group_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	name VARCHAR(100) NOT NULL,
	allow TINYINT(1) DEFAULT '1', 
	upload_icon VARCHAR(100),
	download_mode TINYINT(1) DEFAULT '1'
) TYPE=InnoDB;

-- 
-- Table structure for table 'jforum_extensions'
--
--DROP TABLE IF EXISTS jforum_extensions;
CREATE TABLE jforum_extensions (
	extension_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	extension_group_id INT NOT NULL,
	description VARCHAR(100),
	upload_icon VARCHAR(100),
	extension VARCHAR(10),
	allow TINYINT(1) DEFAULT '1',
	KEY(extension_group_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_attach'
--
--DROP TABLE IF EXISTS jforum_attach;
CREATE TABLE jforum_attach (
	attach_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	post_id INT,
	privmsgs_id INT,
	user_id INT NOT NULL,
	INDEX idx_att_post(post_id),
	INDEX idx_att_priv(privmsgs_id),
	INDEX idx_att_user(user_id)
) TYPE=InnoDB;

-- 
-- Table structure for table 'jforum_attach_desc'
--
--DROP TABLE IF EXISTS jforum_attach_desc;
CREATE TABLE jforum_attach_desc (
	attach_desc_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	attach_id INT NOT NULL,
	physical_filename VARCHAR(255) NOT NULL,
	real_filename VARCHAR(255) NOT NULL,
	download_count INT,
	description VARCHAR(255),
	mimetype VARCHAR(50),
	filesize INT,
	upload_time DATETIME,
	thumb TINYINT(1) DEFAULT '0',
	extension_id INT,
	INDEX idx_att_d_att(attach_id),
	INDEX idx_att_d_ext(extension_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_attach_quota'
--
--DROP TABLE IF EXISTS jforum_attach_quota;
CREATE TABLE jforum_attach_quota (
	attach_quota_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	group_id INT NOT NULL,
	quota_limit_id INT NOT NULL,
	KEY(group_id)
) TYPE=InnoDB;

--
-- Table structure for table 'jforum_banner'
--
--DROP TABLE IF EXISTS jforum_banner;
CREATE TABLE jforum_banner (
	banner_id MEDIUMINT(8) NOT NULL PRIMARY KEY AUTO_INCREMENT,
	banner_name VARCHAR(90),
	banner_placement SMALLINT(1) NOT NULL DEFAULT '0',
	banner_description VARCHAR(250),
	banner_clicks MEDIUMINT(8) NOT NULL DEFAULT '0',
	banner_views MEDIUMINT(8) NOT NULL DEFAULT '0',
	banner_url VARCHAR(250),
	banner_weight TINYINT(1) NOT NULL DEFAULT '50',
	banner_active TINYINT(1) NOT NULL DEFAULT '0',
	banner_comment VARCHAR(250),
	banner_type MEDIUMINT(5) NOT NULL DEFAULT '0',
	banner_width MEDIUMINT(5) NOT NULL DEFAULT '0',
	banner_height MEDIUMINT(5) NOT NULL DEFAULT '0',
	KEY(banner_id)
) TYPE=InnoDB;

--sakai integration related
ALTER TABLE jforum_users ADD COLUMN user_fname VARCHAR(255);
ALTER TABLE jforum_users ADD COLUMN user_lname VARCHAR(255);

CREATE TABLE jforum_sakai_course_categories (
	course_id VARCHAR(99) NOT NULL,
	categories_id MEDIUMINT(8) NOT NULL
)TYPE=InnoDB;

CREATE INDEX jf_scc_course_id_idx ON jforum_sakai_course_categories(course_id);
CREATE INDEX jf_scc_categories_id_idx ON jforum_sakai_course_categories(categories_id);

CREATE TABLE jforum_sakai_course_groups (
	course_id VARCHAR(99) NOT NULL,
	group_id INT(11) NOT NULL
)TYPE=InnoDB;

CREATE INDEX jf_scg_course_id_idx ON jforum_sakai_course_groups(course_id);
CREATE INDEX jf_scg_group_id_idx ON jforum_sakai_course_groups(group_id);

CREATE TABLE jforum_sakai_course_initvalues (
	course_id VARCHAR(99) NOT NULL,
	init_status TINYINT(1) NOT NULL
)TYPE=InnoDB;

CREATE INDEX jf_sci_course_id_idx ON jforum_sakai_course_initvalues(course_id);

CREATE TABLE jforum_sakai_course_privmsgs (
	course_id VARCHAR(99) NOT NULL,
	privmsgs_id MEDIUMINT(8) NOT NULL
)TYPE=InnoDB;

CREATE INDEX jf_scp_course_id_idx ON jforum_sakai_course_privmsgs(course_id);
CREATE INDEX jf_scp_privmsgs_id_idx ON jforum_sakai_course_privmsgs(privmsgs_id);

ALTER TABLE jforum_users MODIFY user_notify tinyint(1) default '0';
ALTER TABLE jforum_users MODIFY user_notify_pm tinyint(1) default '0';

CREATE TABLE jforum_sakai_sessions (
	course_id varchar(99) NOT NULL,
	user_id mediumint(8) NOT NULL,
	visit_time datetime NOT NULL
)TYPE=InnoDB;

ALTER TABLE jforum_forums ADD start_date datetime;
ALTER TABLE jforum_forums ADD end_date datetime;

ALTER TABLE jforum_users ADD sakai_user_id varchar(99) NOT NULL;

CREATE INDEX sakai_user_id_idx ON jforum_users(sakai_user_id);

ALTER TABLE jforum_topics drop column topic_views;
ALTER TABLE jforum_sakai_sessions add column markall_time datetime;

CREATE INDEX jf_ss_user_id_idx ON jforum_sakai_sessions(user_id); 
CREATE INDEX jf_ss_course_id_idx ON jforum_sakai_sessions(course_id);

CREATE TABLE jforum_topics_mark (
topic_id mediumint(8) NOT NULL default '0',
user_id mediumint(8) NOT NULL default '0',
mark_time datetime NOT NULL default '0000-00-00 00:00:00',
PRIMARY KEY  (topic_id,user_id),
CONSTRAINT fk_jf_topics FOREIGN KEY (topic_id) REFERENCES jforum_topics(topic_id) ON DELETE CASCADE ON UPDATE CASCADE)Type=InnoDB;

CREATE INDEX idx_jforum_topics_mark_user_id ON jforum_topics_mark(user_id);

--for forum type and access type
ALTER TABLE jforum_forums ADD COLUMN forum_type SMALLINT UNSIGNED NOT NULL DEFAULT 0 AFTER end_date,
 ADD COLUMN forum_access_type SMALLINT UNSIGNED NOT NULL DEFAULT 0 AFTER forum_type;

--Table for forum groups
CREATE TABLE jforum_forum_sakai_groups (
  forum_id mediumint(8) UNSIGNED NOT NULL,
  sakai_group_id varchar(99) NOT NULL,
  PRIMARY KEY  (forum_id, sakai_group_id)
) TYPE=InnoDB;

--Table for site users
CREATE TABLE jforum_site_users (
  sakai_site_id varchar(99) NOT NULL,
  user_id mediumint(8) unsigned NOT NULL,
  PRIMARY KEY  (sakai_site_id, user_id)
) TYPE=InnoDB;

--Table for import from site or duplicate site
CREATE TABLE jforum_import (
  sakai_site_id varchar(99) NOT NULL,
  imported tinyint(1) unsigned NOT NULL default '0',
  PRIMARY KEY  (sakai_site_id)
) TYPE=InnoDB;

--add column to the table jforum_privmsgs for private attachments
ALTER TABLE jforum_privmsgs ADD COLUMN privmsgs_attachments TINYINT(1) NOT NULL DEFAULT 0 AFTER privmsgs_attach_sig;

--Table for private message attachments
CREATE TABLE jforum_privmsgs_attach (
  attach_id INTEGER NOT NULL DEFAULT 0,
  privmsgs_id INTEGER NOT NULL DEFAULT 0,
  PRIMARY KEY(attach_id, privmsgs_id)
)TYPE=InnoDB;

--add column to the table jforum_forums forum_grade_type
ALTER TABLE jforum_forums ADD COLUMN forum_grade_type SMALLINT UNSIGNED NOT NULL DEFAULT 0 AFTER forum_access_type;

--Table for grading
CREATE TABLE jforum_grade (
  grade_id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
  context VARCHAR(99) NOT NULL DEFAULT '',
  grade_type SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  forum_id MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
  topic_id MEDIUMINT NOT NULL DEFAULT 0,
  points FLOAT NOT NULL DEFAULT 0,
  add_to_gradebook TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY(grade_id)
)TYPE=InnoDB;

--create indexes for jforum_grade table columns
CREATE INDEX idx_jforum_grade_forum_id_topic_id ON jforum_grade(forum_id ASC, topic_id ASC);

--add column to the table jforum_topics for grading
ALTER TABLE jforum_topics ADD COLUMN topic_grade TINYINT(1) NOT NULL DEFAULT 0 AFTER moderated;
ALTER TABLE jforum_search_topics ADD COLUMN topic_grade TINYINT(1) NOT NULL DEFAULT 0 AFTER moderated;

--Table for evaluations
CREATE TABLE jforum_evaluations (
  evaluation_id MEDIUMINT UNSIGNED NOT NULL auto_increment,
  grade_id MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
  user_id MEDIUMINT NOT NULL DEFAULT 0,
  sakai_user_id varchar(99) NOT NULL,
  score FLOAT DEFAULT NULL,
  comments TEXT DEFAULT NULL,
  evaluated_by MEDIUMINT DEFAULT NULL,
  evaluated_date DATETIME DEFAULT NULL,
  PRIMARY KEY  (evaluation_id)
) TYPE=InnoDB;

CREATE INDEX idx_jforum_evaluations_grade_id ON jforum_evaluations(grade_id);
CREATE INDEX idx_jforum_evaluations_user_id ON jforum_evaluations(user_id);

--add archived column to jforum_categories
ALTER TABLE jforum_categories ADD COLUMN archived TINYINT(1) NOT NULL DEFAULT 0 AFTER moderated;

--add topic_export to jforum_topics
ALTER TABLE jforum_topics ADD COLUMN topic_export TINYINT(1) DEFAULT 0 AFTER topic_grade;
ALTER TABLE jforum_search_topics ADD COLUMN topic_export TINYINT(1) DEFAULT 0 AFTER topic_grade;

--increase size for mimetype column to support Office 2007 attachments
ALTER TABLE jforum_attach_desc MODIFY COLUMN mimetype VARCHAR(100) DEFAULT NULL;

--change the column data type from TEXT to LONGTEXT to avoid Data truncation error
ALTER TABLE jforum_posts_text MODIFY COLUMN post_text LONGTEXT DEFAULT NULL;

--change the column data type from TEXT to LONGTEXT to avoid Data truncation error
ALTER TABLE jforum_privmsgs_text MODIFY COLUMN privmsgs_text LONGTEXT DEFAULT NULL;

--add privmsgs_flag_to_follow to jforum_privmsgs
ALTER TABLE jforum_privmsgs ADD COLUMN privmsgs_flag_to_follow TINYINT(1) NOT NULL DEFAULT 0 AFTER privmsgs_attachments;

--add privmsgs_replied to jforum_privmsgs
ALTER TABLE jforum_privmsgs ADD COLUMN privmsgs_replied TINYINT(1) NOT NULL DEFAULT 0 AFTER privmsgs_flag_to_follow;

--for quartz job
CREATE TABLE jforum_search_indexing (
  status TINYINT(1) NOT NULL DEFAULT '0',
  PRIMARY KEY  (status)
) ENGINE=InnoDB;

--add gradable to jforum_categories
ALTER TABLE jforum_categories ADD COLUMN gradable TINYINT(1) NOT NULL DEFAULT 0 AFTER archived;

--add category id to jforum_grade
ALTER TABLE jforum_grade ADD COLUMN categories_id MEDIUMINT(8) NOT NULL DEFAULT 0 AFTER add_to_gradebook;

--add unique index on jforum_grade on forum_id, topic_id and categories_id
--ALTER TABLE jforum_grade ADD UNIQUE INDEX idx_jforum_grade_forum_id_topic_id_categories_id(forum_id, topic_id, categories_id);

--add lock due date for jforum_forums
ALTER TABLE jforum_forums ADD COLUMN lock_end_date TINYINT(1) UNSIGNED DEFAULT 0 AFTER forum_grade_type;

--add is_read to jforum_topics_mark to mark messages unread
ALTER TABLE jforum_topics_mark ADD COLUMN is_read TINYINT(1) NOT NULL DEFAULT 0 AFTER mark_time;

--add released to jforum_evaluations to mark evaluations released/not released
ALTER TABLE jforum_evaluations ADD COLUMN released TINYINT(1) DEFAULT 0 AFTER evaluated_date;

--add user facebook, twitter accounts to jforum_users
ALTER TABLE jforum_users ADD COLUMN user_facebook_account VARCHAR(255) AFTER sakai_user_id, ADD COLUMN user_twitter_account VARCHAR(255) AFTER user_facebook_account;

--add priority to private messages
ALTER TABLE jforum_privmsgs ADD COLUMN privmsgs_priority TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 AFTER privmsgs_replied;

--add start date, end date and lock end date to jforum_categories
ALTER TABLE jforum_categories ADD COLUMN start_date DATETIME AFTER gradable, ADD COLUMN end_date DATETIME AFTER start_date, ADD COLUMN lock_end_date TINYINT(1) UNSIGNED DEFAULT 0 AFTER end_date;

--table for jforum special access
CREATE TABLE jforum_special_access (
  special_access_id INT UNSIGNED NOT NULL auto_increment,
  forum_id MEDIUMINT(8) UNSIGNED NOT NULL DEFAULT 0,
  start_date DATETIME DEFAULT NULL,
  end_date DATETIME DEFAULT NULL,
  lock_end_date TINYINT(1) UNSIGNED NOT NULL DEFAULT 0,
  override_start_date TINYINT(1) UNSIGNED NOT NULL DEFAULT 0,
  override_end_date TINYINT(1) UNSIGNED NOT NULL DEFAULT 0,
  password VARCHAR(56) DEFAULT NULL,
  users LONGTEXT,
  PRIMARY KEY(special_access_id)
) ENGINE=InnoDB;

CREATE INDEX idx_jforum_special_access_forum_id ON jforum_special_access(forum_id);
