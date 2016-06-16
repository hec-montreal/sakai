---------------------------------------------------------------------------------- 
-- $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/trunk/jforum-tool/src/webapp/WEB-INF/config/database/conversion/jforum_2_6_4-2_7_mysql_conversion.sql $ 
-- $Id: jforum_2_6_4-2_7_mysql_conversion.sql 65331 2009-12-17 22:13:34Z murthy@etudes.org $ 
----------------------------------------------------------------------------------- 
-- 
-- Copyright (c) 2009 Etudes, Inc. 
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
-- This is for MySQL JForum 2.6.4 to JForum 2.7
--------------------------------------------------------------------------
--Note : Before running this script back up the jforum_categories, jforum_grade, jforum_forums tables

--for quartz job
CREATE TABLE jforum_search_indexing (
  status TINYINT(1) NOT NULL DEFAULT '0',
  PRIMARY KEY  (status)
) ENGINE=InnoDB;

--add gradable to jforum_categories
ALTER TABLE jforum_categories ADD COLUMN gradable TINYINT(1) NOT NULL DEFAULT 0 AFTER archived;

--add category id to jforum_grade
ALTER TABLE jforum_grade ADD COLUMN categories_id MEDIUMINT(8) NOT NULL DEFAULT 0 AFTER add_to_gradebook;

--add lock due date for jforum_forums
ALTER TABLE jforum_forums ADD COLUMN lock_end_date TINYINT(1) UNSIGNED DEFAULT 0 AFTER forum_grade_type;