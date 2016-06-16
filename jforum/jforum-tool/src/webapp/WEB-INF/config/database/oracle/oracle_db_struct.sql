---------------------------------------------------------------------------------- 
-- $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/WEB-INF/config/database/oracle/oracle_db_struct.sql $ 
-- $Id: oracle_db_struct.sql 71044 2010-10-29 17:37:24Z murthy@etudes.org $ 
----------------------------------------------------------------------------------- 
-- 
-- Copyright (c) 2008 Etudes, Inc. 
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
--
-- jforum_banlist
--
CREATE SEQUENCE jforum_banlist_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;
CREATE TABLE jforum_banlist (
    banlist_id NUMBER(10) NOT NULL,
    user_id NUMBER(10) DEFAULT 0,
    banlist_ip VARCHAR2(20) DEFAULT ' ' NOT NULL,
    banlist_email VARCHAR2(255)  NULL,
    PRIMARY KEY(banlist_id)
);
CREATE INDEX idx_banlist_user ON jforum_banlist(user_id);

--
-- Table structure for table 'jforum_categories'
--
CREATE SEQUENCE jforum_categories_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_categories (
  categories_id NUMBER(10) NOT NULL,
  title VARCHAR2(100) DEFAULT ' ' NOT NULL ,
  display_order NUMBER(10) DEFAULT 0 NOT NULL,
  moderated NUMBER(10) DEFAULT 0,
  PRIMARY KEY(categories_id)
);

--
-- Table structure for table 'jforum_config'
--
CREATE SEQUENCE jforum_config_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_config (
  config_name VARCHAR2(255)  DEFAULT ' ' NOT NULL,
  config_value VARCHAR2(255) DEFAULT ' ' NOT NULL,
  config_id NUMBER(10) NOT NULL,
  PRIMARY KEY(config_id)
);

--
-- Table structure for table 'jforum_forums'
--

CREATE SEQUENCE jforum_forums_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_forums (
  forum_id NUMBER(10) NOT NULL,
  categories_id NUMBER(10)  DEFAULT 1 NOT NULL,
  forum_name VARCHAR2(150) DEFAULT ' ' NOT NULL,
  forum_desc VARCHAR2(255) DEFAULT ' ',
  forum_order NUMBER(10) DEFAULT 1,
  forum_topics NUMBER(10) DEFAULT 0 NOT NULL,
  forum_last_post_id NUMBER(10)  DEFAULT 0 NOT NULL,
  moderated NUMBER(10) DEFAULT 0,
  PRIMARY KEY (forum_id)
);
CREATE INDEX idx_forums_categories_id ON jforum_forums(categories_id);

--
-- Table structure for table 'jforum_groups'
--

CREATE SEQUENCE jforum_groups_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_groups (
  group_id NUMBER(10) NOT NULL,
  group_name VARCHAR2(40) DEFAULT ' ' NOT NULL,
  group_description VARCHAR2(255) DEFAULT NULL,
  parent_id NUMBER(10) DEFAULT 0,
  PRIMARY KEY (group_id)
);

ALTER TABLE jforum_forums ADD CONSTRAINT fk_jforum_categories FOREIGN KEY(categories_id)
	REFERENCES jforum_categories(categories_id);


CREATE TABLE jforum_user_groups (
	group_id NUMBER(10) NOT NULL,
	user_id NUMBER(10) NOT NULL
);
CREATE INDEX idx_ug_group ON jforum_user_groups(group_id);
CREATE INDEX idx_ug_user ON jforum_user_groups(user_id);

--
-- Table structure for table 'jforum_roles'
--

CREATE SEQUENCE jforum_roles_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_roles (
  role_id NUMBER(10) NOT NULL,
  group_id NUMBER(10) DEFAULT 0,
  user_id NUMBER(10) DEFAULT 0,
  name VARCHAR2(255) NOT NULL,
  role_type NUMBER(10) DEFAULT 1,
  PRIMARY KEY (role_id)
);

CREATE INDEX idx_roles_group ON jforum_roles(group_id);
CREATE INDEX idx_roles_user ON jforum_roles(user_id);
CREATE INDEX idx_roles_name ON jforum_roles(name);

--
-- Table structure for table 'jforum_role_values'
--
CREATE TABLE jforum_role_values (
  role_id NUMBER(10) NOT NULL,
  role_value VARCHAR2(255),
  role_type NUMBER(10) DEFAULT 1
);
CREATE INDEX idx_rv_role ON jforum_role_values(role_id);

--
-- Table structure for table 'jforum_posts'
--

CREATE SEQUENCE jforum_posts_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_posts (
  post_id NUMBER(10) NOT NULL,
  topic_id NUMBER(10) DEFAULT 0 NOT NULL,
  forum_id NUMBER(10) DEFAULT 0 NOT NULL,
  user_id NUMBER(10) DEFAULT NULL,
  post_time DATE DEFAULT NULL,
  poster_ip VARCHAR2(15) DEFAULT NULL,
  enable_bbcode NUMBER(10) DEFAULT 1 NOT NULL,
  enable_html NUMBER(10) DEFAULT 1 NOT NULL,
  enable_smilies NUMBER(10) DEFAULT 1 NOT NULL,
  enable_sig NUMBER(10) DEFAULT 1 NOT NULL,
  post_edit_time DATE DEFAULT NULL,
  post_edit_count NUMBER(10) DEFAULT 0 NOT NULL,
  status NUMBER(10) DEFAULT 1,
  attach NUMBER(1) DEFAULT 0,
  need_moderate NUMBER(1) DEFAULT 0,
  PRIMARY KEY (post_id)
);

CREATE INDEX idx_posts_user ON jforum_posts(user_id);
CREATE INDEX idx_posts_topic ON jforum_posts(topic_id);
CREATE INDEX idx_posts_forum ON jforum_posts(forum_id);

--
-- Table structure for table 'jforum_posts_text'
--
CREATE TABLE jforum_posts_text (
	post_id NUMBER(10) NOT NULL,
	post_text BLOB,
	post_subject VARCHAR2(100) DEFAULT NULL,
	PRIMARY KEY (post_id)
);

--
-- Table structure for table 'jforum_privmsgs'
--

CREATE SEQUENCE jforum_privmsgs_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_privmsgs (
  privmsgs_id NUMBER(10) NOT NULL,
  privmsgs_type NUMBER(10) DEFAULT 0 NOT NULL,
  privmsgs_subject VARCHAR2(255) DEFAULT ' ' NOT NULL ,
  privmsgs_from_userid NUMBER(10) DEFAULT 0 NOT NULL,
  privmsgs_to_userid NUMBER(10) DEFAULT 0 NOT NULL,
  privmsgs_date DATE DEFAULT SYSDATE NOT NULL,
  privmsgs_ip VARCHAR2(8) DEFAULT ' ' NOT NULL,
  privmsgs_enable_bbcode NUMBER(10) DEFAULT 1 NOT NULL,
  privmsgs_enable_html NUMBER(10) DEFAULT 0 NOT NULL,
  privmsgs_enable_smilies NUMBER(10) DEFAULT 1 NOT NULL,
  privmsgs_attach_sig NUMBER(10) DEFAULT 1 NOT NULL,
  PRIMARY KEY  (privmsgs_id)
);

CREATE TABLE jforum_privmsgs_text (
	privmsgs_id NUMBER(10) NOT NULL,
	privmsgs_text BLOB
);
CREATE INDEX idx_pm_text_id ON jforum_privmsgs_text (privmsgs_id);

--
-- Table structure for table 'jforum_ranks'
--

CREATE SEQUENCE jforum_ranks_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_ranks (
  rank_id NUMBER(10) NOT NULL,
  rank_title VARCHAR2(50) DEFAULT ' ' NOT NULL,
  rank_min NUMBER(10) DEFAULT 0 NOT NULL,
  rank_special NUMBER(10) DEFAULT NULL,
  rank_image VARCHAR2(255) DEFAULT NULL,
  PRIMARY KEY (rank_id)
);

--
-- Table structure for table 'jforum_sessions'
--

CREATE TABLE jforum_sessions (
  session_id VARCHAR2(100) DEFAULT ' ' NOT NULL,
  session_user_id NUMBER(10) DEFAULT 0,
  session_start DATE DEFAULT SYSDATE NOT NULL,
  session_time NUMBER(10) DEFAULT 0 NOT NULL,
  session_ip VARCHAR2(8) DEFAULT ' ' NOT NULL,
  session_page NUMBER(10) DEFAULT 0 NOT NULL,
  session_logged_int NUMBER(10) DEFAULT NULL
);

--
-- Table structure for table 'jforum_smilies'
--

CREATE SEQUENCE jforum_smilies_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_smilies (
  smilie_id NUMBER(10) NOT NULL,
  code VARCHAR2(50) DEFAULT ' ' NOT NULL,
  url VARCHAR2(100) DEFAULT NULL,
  disk_name VARCHAR2(255),
  PRIMARY KEY (smilie_id)
);

--
-- Table structure for table 'jforum_themes'
--

CREATE SEQUENCE jforum_themes_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_themes (
  themes_id NUMBER(10) NOT NULL,
  template_name VARCHAR2(30) DEFAULT ' ' NOT NULL,
  style_name VARCHAR2(30) DEFAULT ' ' NOT NULL,
  PRIMARY KEY (themes_id)
);

--
-- Table structure for table 'jforum_topics'
--

CREATE SEQUENCE jforum_topics_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_topics (
  topic_id NUMBER(10) NOT NULL,
  forum_id NUMBER(10) DEFAULT 0 NOT NULL,
  topic_title VARCHAR2(100) DEFAULT ' ' NOT NULL,
  user_id NUMBER(10) DEFAULT 0 NOT NULL,
  topic_time DATE DEFAULT SYSDATE NOT NULL,
  topic_views NUMBER(10) DEFAULT 1,
  topic_replies NUMBER(10) DEFAULT 0,
  topic_status NUMBER(10) DEFAULT 0,
  topic_vote NUMBER(10) DEFAULT 0,
  topic_type NUMBER(10) DEFAULT 0,
  topic_first_post_id NUMBER(10) DEFAULT 0,
  topic_last_post_id NUMBER(10) DEFAULT 0 NOT NULL,
  moderated NUMBER(10) DEFAULT 0,
  PRIMARY KEY (topic_id)
);

CREATE INDEX idx_topics_forum ON jforum_topics(forum_id);
CREATE INDEX idx_topics_user ON jforum_topics(user_id);
CREATE INDEX idx_topics_fp ON jforum_topics(topic_first_post_id);
CREATE INDEX idx_topics_lp ON jforum_topics(topic_last_post_id);

--
-- Table structure for table 'jforum_topics_watch'
--

CREATE TABLE jforum_topics_watch (
  topic_id NUMBER(10) DEFAULT 0 NOT NULL,
  user_id NUMBER(10) DEFAULT 0 NOT NULL,
  is_read NUMBER(10) DEFAULT 0 NOT NULL
);
CREATE INDEX idx_tw_topic ON jforum_topics_watch(topic_id);
CREATE INDEX idx_tw_user ON jforum_topics_watch(user_id);

--
-- Table structure for table 'jforum_users'
--

CREATE SEQUENCE jforum_users_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_users (
  user_id NUMBER(10) NOT NULL,
  user_active NUMBER(10) DEFAULT NULL,
  username VARCHAR2(50) DEFAULT ' ' NOT NULL,
  user_password VARCHAR2(32) DEFAULT ' ' NOT NULL,
  user_session_time NUMBER(10) DEFAULT 0 NOT NULL,
  user_session_page NUMBER(10) DEFAULT 0 NOT NULL,
  user_lastvisit DATE DEFAULT SYSDATE NOT NULL,
  user_regdate DATE DEFAULT SYSDATE NOT NULL,
  user_level NUMBER(10) DEFAULT NULL,
  user_posts NUMBER(10) DEFAULT 0 NOT NULL,
  user_timezone VARCHAR2(5) DEFAULT ' ' NOT NULL,
  user_style NUMBER(10) DEFAULT NULL,
  user_lang VARCHAR2(255) DEFAULT NULL,
  user_dateformat VARCHAR2(30) DEFAULT '%d/%M/%Y %H:%i' NOT NULL,
  user_new_privmsg NUMBER(10) DEFAULT 0 NOT NULL,
  user_unread_privmsg NUMBER(10) DEFAULT 0 NOT NULL,
  user_last_privmsg DATE NULL,
  user_emailtime DATE DEFAULT NULL,
  user_viewemail NUMBER(10) DEFAULT 0,
  user_attachsig NUMBER(10) DEFAULT 1,
  user_allowhtml NUMBER(10) DEFAULT 0,
  user_allowbbcode NUMBER(10) DEFAULT 1,
  user_allowsmilies NUMBER(10) DEFAULT 1,
  user_allowavatar NUMBER(10) DEFAULT 1,
  user_allow_pm NUMBER(10) DEFAULT 1,
  user_allow_viewonline NUMBER(10) DEFAULT 1,
  user_notify NUMBER(10) DEFAULT 1,
  user_notify_pm NUMBER(10) DEFAULT 1,
  user_popup_pm NUMBER(10) DEFAULT 1,
  rank_id NUMBER(10) DEFAULT 1,
  user_avatar VARCHAR2(100) DEFAULT NULL,
  user_avatar_type NUMBER(10) DEFAULT 0 NOT NULL,
  user_email VARCHAR2(255) NULL,
  user_icq VARCHAR2(15) DEFAULT NULL,
  user_website VARCHAR2(100) DEFAULT NULL,
  user_from VARCHAR2(100) DEFAULT NULL,
  user_sig VARCHAR2(4000),
  user_sig_bbcode_uid VARCHAR2(10) DEFAULT NULL,
  user_aim VARCHAR2(255) DEFAULT NULL,
  user_yim VARCHAR2(255) DEFAULT NULL,
  user_msnm VARCHAR2(255) DEFAULT NULL,
  user_occ VARCHAR2(100) DEFAULT NULL,
  user_interests VARCHAR2(255) DEFAULT NULL,
  user_actkey VARCHAR2(32) DEFAULT NULL,
  gender CHAR(1) DEFAULT NULL,
  themes_id NUMBER(10) DEFAULT NULL,
  deleted NUMBER(10) DEFAULT NULL,
  user_viewonline NUMBER(10) DEFAULT 1,
  security_hash VARCHAR2(32),
  user_karma DECIMAL(10,2),
  PRIMARY KEY (user_id)
);


--
-- Table structure for table 'jforum_vote_desc'
--

CREATE SEQUENCE jforum_vote_desc_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_vote_desc (
  vote_id NUMBER(10) NOT NULL,
  topic_id NUMBER(10) DEFAULT 0 NOT NULL,
  vote_text BLOB,
  vote_start NUMBER(10) DEFAULT 0 NOT NULL,
  vote_length NUMBER(10) DEFAULT 0 NOT NULL,
  PRIMARY KEY  (vote_id)
);

--
-- Table structure for table 'jforum_vote_results'
--

CREATE TABLE jforum_vote_results (
  vote_id NUMBER(10) DEFAULT 0 NOT NULL,
  vote_option_id NUMBER(10) DEFAULT 0 NOT NULL,
  vote_option_text VARCHAR2(255) DEFAULT ' ' NOT NULL,
  vote_result NUMBER(10) DEFAULT 0 NOT NULL
);

--
-- Table structure for table 'jforum_vote_voters'
--

CREATE TABLE jforum_vote_voters (
  vote_id NUMBER(10) DEFAULT 0 NOT NULL,
  vote_user_id NUMBER(10) DEFAULT 0 NOT NULL,
  vote_user_ip CHAR(8) DEFAULT ' ' NOT NULL
);

--
-- Table structure for table 'jforum_words'
--

CREATE SEQUENCE jforum_words_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_words (
  word_id NUMBER(10) NOT NULL,
  word VARCHAR2(100) DEFAULT ' ' NOT NULL,
  replacement VARCHAR2(100) DEFAULT ' ' NOT NULL,
  PRIMARY KEY (word_id)
);

--
-- Table structure for table 'jforum_search_words'
--
CREATE SEQUENCE jforum_search_words_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_search_words (
  word_id NUMBER(10) NOT NULL,
  word VARCHAR2(100) NOT NULL,
  word_hash NUMBER(10),
  PRIMARY KEY (word_id)
);
CREATE INDEX idx_sw_word ON jforum_search_words(word);
CREATE INDEX idx_sw_hash ON jforum_search_words(word_hash);

-- 
-- Table structure for table 'jforum_search_wordmatch'
--
CREATE TABLE jforum_search_wordmatch (
  post_id NUMBER(10) NOT NULL,
  word_id NUMBER(10) NOT NULL,
  title_match NUMBER(10) DEFAULT 0
);
CREATE INDEX idx_swm_post ON jforum_search_wordmatch(post_id);
CREATE INDEX idx_swm_word ON jforum_search_wordmatch(word_id);
CREATE INDEX idx_swm_title ON jforum_search_wordmatch(title_match);

--
-- Table structure for table 'jforum_search_results'
--
CREATE TABLE jforum_search_results (
  topic_id NUMBER(10) NOT NULL,
  session_id VARCHAR2(100),
  search_time DATE
);
CREATE INDEX idx_sr_topic ON jforum_search_results(topic_id);


CREATE TABLE jforum_search_topics (
  topic_id NUMBER(10) NOT NULL,
  forum_id NUMBER(10) DEFAULT 0 NOT NULL,
  topic_title VARCHAR2(100) DEFAULT ' ' NOT NULL,
  user_id NUMBER(10) DEFAULT 0 NOT NULL,
  topic_time DATE DEFAULT SYSDATE NOT NULL,
  topic_views NUMBER(10) DEFAULT 1,
  topic_replies NUMBER(10) DEFAULT 0,
  topic_status NUMBER(10) DEFAULT 0,
  topic_vote NUMBER(10) DEFAULT 0,
  topic_type NUMBER(10) DEFAULT 0,
  topic_first_post_id NUMBER(10) DEFAULT 0,
  topic_last_post_id NUMBER(10) DEFAULT 0 NOT NULL,
  moderated NUMBER(10) DEFAULT 0,
  session_id VARCHAR2(100),
  search_time DATE
);
CREATE INDEX idx_st_topic ON jforum_search_topics(topic_id);
CREATE INDEX idx_st_forum ON jforum_search_topics(forum_id);
CREATE INDEX idx_st_user ON jforum_search_topics(user_id);
CREATE INDEX idx_st_fp ON jforum_search_topics(topic_first_post_id);
CREATE INDEX idx_st_lp ON jforum_search_topics(topic_last_post_id);

--
-- Table structure for table 'jforum_karma'
--
CREATE SEQUENCE jforum_karma_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_karma (
	karma_id NUMBER(10) NOT NULL,
	post_id NUMBER(10) NOT NULL,
	topic_id NUMBER(10) NOT NULL,
	post_user_id NUMBER(10) NOT NULL,
	from_user_id NUMBER(10) NOT NULL,
	points NUMBER(10) NOT NULL,
	rate_date DATE DEFAULT NULL,
	PRIMARY KEY(karma_id)
);

CREATE INDEX idx_krm_post ON jforum_karma(post_id);
CREATE INDEX idx_krm_topic ON jforum_karma(topic_id);
CREATE INDEX idx_krm_user ON jforum_karma(post_user_id);
CREATE INDEX idx_krm_from ON jforum_karma(from_user_id);

--
-- Table structure for table 'jforum_bookmark'
--
CREATE SEQUENCE jforum_bookmarks_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_bookmarks (
	bookmark_id NUMBER(10) NOT NULL,
	user_id NUMBER(10) NOT NULL,
	relation_id NUMBER(10) NOT NULL,
	relation_type NUMBER(10) NOT NULL,
	public_visible NUMBER(10) DEFAULT 1,
	title VARCHAR2(255),
	description VARCHAR2(255),
	PRIMARY KEY(bookmark_id)
);

CREATE INDEX idx_bok_user ON jforum_bookmarks(user_id);
CREATE INDEX idx_bok_rel ON jforum_bookmarks(relation_id);

-- 
-- Table structure for table 'jforum_quota_limit'
--
CREATE SEQUENCE jforum_quota_limit_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_quota_limit (
	quota_limit_id NUMBER(10) NOT NULL,
	quota_desc VARCHAR2(50) NOT NULL,
	quota_limit NUMBER(10) NOT NULL,
	quota_type NUMBER(1) DEFAULT 1,
	PRIMARY KEY(quota_limit_id)
);

--
-- Table structure for table 'jforum_extension_groups'
--
CREATE SEQUENCE jforum_extension_groups_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_extension_groups (
	extension_group_id NUMBER(10) NOT NULL,
	name VARCHAR2(100) NOT NULL,
	allow NUMBER(1) DEFAULT 1, 
	upload_icon VARCHAR2(100),
	download_mode NUMBER(1) DEFAULT 1,
	PRIMARY KEY(extension_group_id)
) ;

-- 
-- Table structure for table 'jforum_extensions'
--
CREATE SEQUENCE jforum_extensions_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_extensions (
	extension_id NUMBER(10) NOT NULL,
	extension_group_id NUMBER(10) NOT NULL,
	description VARCHAR2(100),
	upload_icon VARCHAR2(100),
	extension VARCHAR2(10),
	allow NUMBER(1) DEFAULT 1,
	PRIMARY KEY(extension_id)
);

CREATE INDEX idx_ext_group ON jforum_extensions(extension_group_id);
CREATE INDEX idx_ext_ext ON jforum_extensions(extension);

--
-- Table structure for table 'jforum_attach'
--
CREATE SEQUENCE jforum_attach_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_attach (
	attach_id NUMBER(10) NOT NULL,
	post_id NUMBER(10),
	privmsgs_id NUMBER(10),
	user_id NUMBER(10) NOT NULL,
	PRIMARY KEY(attach_id)
);

CREATE INDEX idx_att_post ON jforum_attach(post_id);
CREATE INDEX idx_att_priv ON jforum_attach(privmsgs_id);
CREATE INDEX idx_att_user ON jforum_attach(user_id);

-- 
-- Table structure for table 'jforum_attach_desc'
--
CREATE SEQUENCE jforum_attach_desc_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_attach_desc (
	attach_desc_id NUMBER(10) NOT NULL,
	attach_id NUMBER(10) NOT NULL,
	physical_filename VARCHAR2(255) NOT NULL,
	real_filename VARCHAR2(255) NOT NULL,
	download_count NUMBER(10),
	description VARCHAR2(255),
	mimetype VARCHAR2(50),
	filesize NUMBER(20),
	upload_time DATE,
	thumb NUMBER(1) DEFAULT 0,
	extension_id NUMBER(10)
);

CREATE INDEX idx_att_d_att ON jforum_attach_desc(attach_id);
CREATE INDEX idx_att_d_ext ON jforum_attach_desc(extension_id);

--
-- Table structure for table 'jforum_attach_quota'
--
CREATE SEQUENCE jforum_attach_quota_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_attach_quota (
	attach_quota_id NUMBER(10) NOT NULL,
	group_id NUMBER(10) NOT NULL,
	quota_limit_id NUMBER(10) NOT NULL,
	PRIMARY KEY(attach_quota_id)
);

CREATE INDEX idx_aq_group ON jforum_attach_quota(group_id);
CREATE INDEX idx_aq_ql ON jforum_attach_quota(quota_limit_id);

--
-- Table structure for table 'jforum_banner'
--
CREATE SEQUENCE jforum_banner_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

CREATE TABLE jforum_banner (
	banner_id NUMBER(10) NOT NULL,
	banner_name VARCHAR2(90),
	banner_placement NUMBER(1) DEFAULT 0 NOT NULL,
	banner_description VARCHAR2(250),
	banner_clicks NUMBER(8) DEFAULT 0 NOT NULL,
	banner_views NUMBER(8) DEFAULT 0 NOT NULL,
	banner_url VARCHAR2(250),
	banner_weight NUMBER(2) DEFAULT 50 NOT NULL,
	banner_active NUMBER(1) DEFAULT 0 NOT NULL,
	banner_comment VARCHAR2(250),
	banner_type NUMBER(5) DEFAULT 0 NOT NULL,
	banner_width NUMBER(5) DEFAULT 0 NOT NULL,
	banner_height NUMBER(5) DEFAULT 0 NOT NULL,
	PRIMARY KEY(banner_id)
);


--sakai integration related
ALTER TABLE jforum_users ADD user_fname VARCHAR2(255) NULL;
ALTER TABLE jforum_users ADD user_lname VARCHAR2(255) NULL;

CREATE TABLE jforum_sakai_course_categories (
	course_id VARCHAR2(99) NOT NULL,
	categories_id NUMBER(8) NOT NULL
);

CREATE TABLE jforum_sakai_course_groups (
	course_id VARCHAR2(99) NOT NULL,
	group_id NUMBER(11) NOT NULL
);

CREATE TABLE jforum_sakai_course_initvalues (
	course_id VARCHAR2(99) NOT NULL,
	init_status NUMBER(1) NOT NULL
);

CREATE TABLE jforum_sakai_course_privmsgs (
	course_id VARCHAR2(99) NOT NULL,
	privmsgs_id NUMBER(8) NOT NULL
);

ALTER TABLE jforum_users MODIFY user_notify NUMBER(1) default '0';
ALTER TABLE jforum_users MODIFY user_notify_pm NUMBER(1) default '0';

CREATE TABLE jforum_sakai_sessions (
	course_id VARCHAR2(99) NOT NULL,
	user_id NUMBER(8) NOT NULL,
	visit_time TIMESTAMP NOT NULL
);

ALTER TABLE jforum_forums ADD start_date TIMESTAMP NULL;
ALTER TABLE jforum_forums ADD end_date TIMESTAMP NULL;
ALTER TABLE jforum_users ADD sakai_user_id VARCHAR2(99) NOT NULL;

--drop topic_views column
ALTER TABLE jforum_topics DROP COLUMN topic_views;

--update for mark topics
ALTER TABLE jforum_sakai_sessions ADD markall_time TIMESTAMP NULL;

--table for mark topics
--CREATE TABLE jforum_topics_mark (
--topic_id NUMBER(8) NOT NULL default 0,
--user_id NUMBER(8) NOT NULL default 0,
--mark_time TIMESTAMP NOT NULL default '0000-00-00 00:00:00',
--PRIMARY KEY  (topic_id,user_id),
--CONSTRAINT fk_jf_topics FOREIGN KEY (topic_id) REFERENCES jforum_topics(topic_id) ON DELETE CASCADE ON UPDATE CASCADE);


CREATE TABLE jforum_topics_mark (
topic_id NUMBER(8) default 0 NOT NULL,
user_id NUMBER(8) default 0 NOT NULL,
mark_time TIMESTAMP not null,
constraint pk_jforum_topics_mark primary key (topic_id,user_id),
constraint fk_jf_topics forEIGN KEY (topic_id) REFERENCES jforum_topics(topic_id) 
ON DELETE CASCADE);

--This does not exists in Oracle we might need to write a trigger for it.
--ON UPDATE CASCADE;

--for forum type and access type
ALTER TABLE jforum_forums ADD forum_type NUMBER(1) DEFAULT 0 NOT NULL;
ALTER TABLE jforum_forums ADD forum_access_type NUMBER(1) DEFAULT 0 NOT NULL;

--Table for forum groups
CREATE TABLE jforum_forum_sakai_groups (
  forum_id NUMBER(10) NOT NULL,
  sakai_group_id VARCHAR2(99) NOT NULL,
  constraint pk_jforum_forum_sakai_groups PRIMARY KEY (forum_id, sakai_group_id)
) ;

--Table for site users
CREATE TABLE jforum_site_users (
  sakai_site_id VARCHAR2(99) NOT NULL,
  user_id NUMBER(10) NOT NULL,
  constraint pk_jforum_site_users PRIMARY KEY  (sakai_site_id, user_id)
);

--Table for import from site or duplicate site
CREATE TABLE jforum_import (
  sakai_site_id VARCHAR2(99) NOT NULL,
  imported NUMBER(1) DEFAULT 0 NOT NULL,
  constraint pk_jforum_import PRIMARY KEY  (sakai_site_id)
);

--add column to the table jforum_privmsgs for private attachments
ALTER TABLE jforum_privmsgs ADD privmsgs_attachments NUMBER(10) DEFAULT 0 NOT NULL;

--Table for private message attachments
CREATE TABLE jforum_privmsgs_attach (
  attach_id NUMBER(10) DEFAULT 0 NOT NULL,
  privmsgs_id NUMBER(10) DEFAULT 0 NOT NULL,
  constraint pk_jforum_privmsgs_attach PRIMARY KEY(attach_id, privmsgs_id)
);

--add column to the table jforum_forums forum_grade_type
ALTER TABLE jforum_forums ADD forum_grade_type NUMBER(1) DEFAULT 0 NOT NULL;

--sequence for jforum_grade
CREATE SEQUENCE jforum_grade_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;

--Table for grading
CREATE TABLE jforum_grade (
  grade_id NUMBER(10) NOT NULL,
  context VARCHAR2(99) DEFAULT '' NOT NULL,
  grade_type NUMBER(1) DEFAULT 0 NOT NULL,
  forum_id NUMBER(10) DEFAULT 0 NOT NULL,
  topic_id NUMBER(10) DEFAULT 0 NOT NULL,
  points FLOAT DEFAULT 0 NOT NULL,
  add_to_gradebook NUMBER(1) DEFAULT 0 NOT NULL,
  PRIMARY KEY(grade_id)
);

--create indexes for jforum_grade table columns
--CREATE INDEX idx_jforum_grade_forum_id_topic_id ON jforum_grade(forum_id ASC, topic_id ASC);
--renamed as name is long
CREATE INDEX idx_jf_grade_forum_topic_id ON jforum_grade(forum_id ASC, topic_id ASC);

--add column to the table jforum_topics for grading
ALTER TABLE jforum_topics ADD topic_grade NUMBER(1) DEFAULT 0 NOT NULL;
ALTER TABLE jforum_search_topics ADD topic_grade NUMBER(1) DEFAULT 0 NOT NULL;

--sequence for jforum_evaluations
CREATE SEQUENCE jforum_evaluations_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;
    
--Table for evaluations
CREATE TABLE jforum_evaluations (
  evaluation_id NUMBER(10) NOT NULL,
  grade_id NUMBER(10) DEFAULT 0 NOT NULL,
  user_id NUMBER(10) DEFAULT 0 NOT NULL,
  sakai_user_id VARCHAR2(99) NOT NULL,
  score FLOAT DEFAULT NULL,
  comments CLOB,
  evaluated_by NUMBER(10),
  evaluated_date DATE,
  PRIMARY KEY  (evaluation_id)
);

--CREATE INDEX idx_jforum_evaluations_grade_id ON jforum_evaluations(grade_id);
--renamed the above indes as name is too long
CREATE INDEX idx_jf_eval_grade_id ON jforum_evaluations(grade_id);
CREATE INDEX idx_jforum_evaluations_user_id ON jforum_evaluations(user_id);

--add archived column to jforum_categories
ALTER TABLE jforum_categories ADD archived NUMBER(1) DEFAULT 0 NOT NULL;

--add topic_export to jforum_topics
ALTER TABLE jforum_topics ADD topic_export NUMBER(1) DEFAULT 0 NOT NULL;
ALTER TABLE jforum_search_topics ADD topic_export NUMBER(1) DEFAULT 0 NOT NULL;

--increase size for mimetype column to support Office 2007 attachments
ALTER TABLE jforum_attach_desc MODIFY mimetype VARCHAR2(100) DEFAULT NULL;

--add privmsgs_flag_to_follow to jforum_privmsgs
ALTER TABLE jforum_privmsgs ADD privmsgs_flag_to_follow NUMBER(10) DEFAULT 0 NOT NULL;

--add privmsgs_replied to jforum_privmsgs
ALTER TABLE jforum_privmsgs ADD privmsgs_replied NUMBER(10) DEFAULT 0 NOT NULL;

--for quartz job
CREATE TABLE jforum_search_indexing (
  status NUMBER(1) DEFAULT 0 NOT NULL ,
  PRIMARY KEY  (status)
);

--add gradable to jforum_categories
ALTER TABLE jforum_categories ADD gradable NUMBER(1) DEFAULT 0 NOT NULL;

--add category id to jforum_grade
ALTER TABLE jforum_grade ADD categories_id NUMBER(10) DEFAULT 0 NOT NULL;

--add lock due date for jforum_forums
ALTER TABLE jforum_forums ADD lock_end_date NUMBER(1) DEFAULT 0 NOT NULL;

--add is_read to jforum_topics_mark to mark messages unread
ALTER TABLE jforum_topics_mark ADD is_read NUMBER(1) DEFAULT 0 NOT NULL;

--add released to jforum_evaluations to mark evaluations released/not released
ALTER TABLE jforum_evaluations ADD released NUMBER(1) DEFAULT 0;

--add user facebook, twitter accounts to jforum_users
ALTER TABLE jforum_users ADD (user_facebook_account VARCHAR2(255), user_twitter_account VARCHAR2(255));

--add priority to private messages
ALTER TABLE jforum_privmsgs ADD privmsgs_priority NUMBER(1) DEFAULT 0 NOT NULL;

--add start date, end date and lock end date to jforum_categories
ALTER TABLE jforum_categories ADD (start_date DATE , end_date DATE , lock_end_date NUMBER(1) DEFAULT 0);

--sequence for jforum_special_access
CREATE SEQUENCE jforum_special_access_seq
INCREMENT BY 1
    START WITH 1 MAXVALUE 2.0E9 MINVALUE 1 NOCYCLE
    CACHE 200 ORDER;
    
--table for jforum special access
CREATE TABLE jforum_special_access (
  special_access_id NUMBER(10) NOT NULL,
  forum_id NUMBER(10) DEFAULT 0 NOT NULL ,
  start_date DATE DEFAULT NULL,
  end_date DATE DEFAULT NULL,
  lock_end_date NUMBER(1) DEFAULT 0 NOT NULL,
  override_start_date NUMBER(1) DEFAULT 0 NOT NULL,
  override_end_date NUMBER(1) DEFAULT 0 NOT NULL,
  password VARCHAR(56) DEFAULT NULL,
  users CLOB,
  PRIMARY KEY(special_access_id)
);

CREATE INDEX idx_jf_sa_forum_id ON jforum_special_access(forum_id);