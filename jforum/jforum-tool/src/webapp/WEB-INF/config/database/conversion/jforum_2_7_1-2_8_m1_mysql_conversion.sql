---------------------------------------------------------------------------------- 
-- $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/WEB-INF/config/database/conversion/%20jforum_2_7_1-2_8_m1_mysql_conversion.sql $ 
-- $Id:  jforum_2_7_1-2_8_m1_mysql_conversion.sql 67307 2010-04-23 18:53:14Z murthy@etudes.org $ 
----------------------------------------------------------------------------------- 
-- 
-- Copyright (c) 2009, 2010 Etudes, Inc. 
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
----------------------------------------------------------------------------------
--------------------------------------------------------------------------
-- This is for MySQL JForum 2.7.1 to JForum 2.8
--------------------------------------------------------------------------
--Note : Before running this script back up the jforum_topics_mark tables

--add is_read to jforum_topics_mark to mark messages unread
ALTER TABLE jforum_topics_mark ADD COLUMN is_read TINYINT(1) NOT NULL DEFAULT 0 AFTER mark_time;

--add released to jforum_evaluations to mark evaluations released/not released
ALTER TABLE jforum_evaluations ADD COLUMN released TINYINT(1) DEFAULT 0 AFTER evaluated_date;

--add user facebook, twitter accounts to jforum_users
ALTER TABLE jforum_users ADD COLUMN user_facebook_account VARCHAR(255) AFTER sakai_user_id, ADD COLUMN user_twitter_account VARCHAR(255) AFTER user_facebook_account;