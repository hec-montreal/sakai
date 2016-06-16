
---------------------------------------------------------------------------------- 
-- $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/WEB-INF/config/database/conversion/jforum_2_7_1-2_8_1_oracle_conversion.sql $ 
-- $Id: jforum_2_7_1-2_8_1_oracle_conversion.sql 72068 2011-01-14 23:12:45Z murthy@etudes.org $ 
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
-- This is for Oracle, JForum 2.7.1 to JForum 2.8.1
--------------------------------------------------------------------------
--Note : Before running this script back up the updated tables

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