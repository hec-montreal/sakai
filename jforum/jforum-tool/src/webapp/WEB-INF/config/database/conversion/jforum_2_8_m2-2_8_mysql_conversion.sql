---------------------------------------------------------------------------------- 
-- $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/WEB-INF/config/database/conversion/jforum_2_8_m2-2_8_mysql_conversion.sql $ 
-- $Id: jforum_2_8_m2-2_8_mysql_conversion.sql 71044 2010-10-29 17:37:24Z murthy@etudes.org $ 
----------------------------------------------------------------------------------- 
-- 
-- Copyright (c) 2010 Etudes, Inc. 
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
-- This is for MySQL, JForum 2.8.m2 to JForum 2.8
--------------------------------------------------------------------------
--Note : Before running this script back up the updated tables

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