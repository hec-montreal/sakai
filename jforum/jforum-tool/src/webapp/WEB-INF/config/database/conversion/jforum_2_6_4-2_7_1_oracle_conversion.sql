---------------------------------------------------------------------------------- 
-- $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/trunk/jforum-tool/src/webapp/WEB-INF/config/database/conversion/jforum_2_6_4-2_7_oracle_conversion.sql $ 
-- $Id: jforum_2_6_4-2_7_oracle_conversion.sql 65333 2009-12-17 22:22:32Z murthy@etudes.org $ 
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
-- This is for Oracle JForum 2.6.4 to JForum 2.7
--------------------------------------------------------------------------
--Note : Before running this script back up the jforum_categories, jforum_grade, jforum_forums tables

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