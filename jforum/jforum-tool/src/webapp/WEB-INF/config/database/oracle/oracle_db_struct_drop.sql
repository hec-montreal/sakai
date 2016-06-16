---------------------------------------------------------------------------------- 
-- $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/WEB-INF/config/database/oracle/oracle_db_struct_drop.sql $ 
-- $Id: oracle_db_struct_drop.sql 55571 2008-12-02 19:26:02Z murthy@etudes.org $ 
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
-- Table structure for table 'jforum_attach_quota'
DROP SEQUENCE jforum_attach_quota_seq;
DROP TABLE jforum_attach_quota;

-- Table structure for table 'jforum_attach_desc'
DROP SEQUENCE jforum_attach_desc_seq;
DROP TABLE jforum_attach_desc;

-- Table structure for table 'jforum_attach'
DROP SEQUENCE jforum_attach_seq;
DROP TABLE jforum_attach;

-- Table structure for table 'jforum_extensions'
DROP SEQUENCE jforum_extensions_seq;
DROP TABLE jforum_extensions;

-- Table structure for table 'jforum_extension_groups'
DROP SEQUENCE jforum_extension_groups_seq;
DROP TABLE jforum_extension_groups;

-- Table structure for table 'jforum_quota_limit'
DROP SEQUENCE jforum_quota_limit_seq;
DROP TABLE jforum_quota_limit;

-- Table structure for table 'jforum_bookmark'
DROP SEQUENCE jforum_bookmarks_seq;
DROP TABLE jforum_bookmarks;

-- Table structure for table 'jforum_karma'
DROP SEQUENCE jforum_karma_seq;
DROP TABLE jforum_karma;

-- Table structure for table 'jforum_search_results'
DROP TABLE jforum_search_results;
DROP TABLE jforum_search_topics;

-- Table structure for table 'jforum_search_wordmatch'
DROP TABLE jforum_search_wordmatch;

-- Table structure for table 'jforum_search_words'
DROP SEQUENCE jforum_search_words_seq;
DROP TABLE jforum_search_words;

-- Table structure for table 'jforum_words'
DROP SEQUENCE jforum_words_seq;
DROP TABLE jforum_words;

-- Table structure for table 'jforum_vote_voters'
DROP TABLE jforum_vote_voters;

-- Table structure for table 'jforum_vote_results'
DROP TABLE jforum_vote_results;

-- Table structure for table 'jforum_vote_desc'
DROP SEQUENCE jforum_vote_desc_seq;
DROP TABLE jforum_vote_desc;

-- Table structure for table 'jforum_users'
DROP SEQUENCE jforum_users_seq;
DROP TABLE jforum_users;

-- Table structure for table 'jforum_topics_watch'
DROP TABLE jforum_topics_watch;

-- Table structure for table 'jforum_topics'
DROP SEQUENCE jforum_topics_seq;
DROP TABLE jforum_topics;

-- Table structure for table 'jforum_themes'
DROP SEQUENCE jforum_themes_seq;
DROP TABLE jforum_themes;

-- Table structure for table 'jforum_smilies'
DROP SEQUENCE jforum_smilies_seq;
DROP TABLE jforum_smilies;

-- Table structure for table 'jforum_sessions'
DROP TABLE jforum_sessions;

-- Table structure for table 'jforum_ranks'
DROP SEQUENCE jforum_ranks_seq;
DROP TABLE jforum_ranks;

-- Table structure for table 'jforum_privmsgs'
DROP SEQUENCE jforum_privmsgs_seq;
DROP TABLE jforum_privmsgs;
DROP TABLE jforum_privmsgs_text;

-- Table structure for table 'jforum_posts_text'
DROP TABLE jforum_posts_text;

-- Table structure for table 'jforum_posts'
DROP SEQUENCE jforum_posts_seq;
DROP TABLE jforum_posts;

-- Table structure for table 'jforum_role_values'
DROP TABLE jforum_role_values;
	
-- Table structure for table 'jforum_roles'
DROP SEQUENCE jforum_roles_seq;
DROP TABLE jforum_roles;

-- Table structure for table 'jforum_groups'
DROP SEQUENCE jforum_groups_seq;
DROP TABLE jforum_groups;
DROP TABLE jforum_user_groups;

-- Table structure for table 'jforum_forums'
DROP SEQUENCE jforum_forums_seq;
DROP TABLE jforum_forums;

-- Table structure for table 'jforum_config'
DROP SEQUENCE jforum_config_seq;
DROP TABLE jforum_config;

-- Table structure for table 'jforum_categories'
DROP SEQUENCE jforum_categories_seq;
DROP TABLE jforum_categories;

-- jforum_banlist
DROP SEQUENCE jforum_banlist_seq;
DROP TABLE jforum_banlist;

-- jforum_topics_mark
DROP TABLE jforum_topics_mark;

-- jforum_forum_sakai_groups
DROP TABLE jforum_forum_sakai_groups;

-- jforum_site_users
DROP TABLE jforum_site_users;

-- jforum_import
DROP TABLE jforum_import;

-- jforum_privmsgs_attach
DROP TABLE jforum_privmsgs_attach;