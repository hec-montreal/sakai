---------------------------------------------------------------------------------- 
-- $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/WEB-INF/config/database/conversion/jforum_2_7_1-2_8_1_mysql_conversion.sql $ 
-- $Id: jforum_2_7_1-2_8_1_mysql_conversion.sql 72068 2011-01-14 23:12:45Z murthy@etudes.org $ 
----------------------------------------------------------------------------------- 
-- 
-- Copyright (c) 2009, 2010, 2011 Etudes, Inc. 
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
-- This is for MySQL, JForum 2.7.1 to JForum 2.8.1
--------------------------------------------------------------------------
--Note : Before running this script back up the updated tables

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