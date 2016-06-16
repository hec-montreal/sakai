---------------------------------------------------------------------------------- 
-- $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/WEB-INF/config/database/conversion/JForum_2_4-2_5_oracle_conversion.sql $ 
-- $Id: JForum_2_4-2_5_oracle_conversion.sql 69632 2010-08-06 18:08:22Z murthy@etudes.org $ 
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
-- This is the Oracle JForum 2.4 to JForum 2.5 conversion script
-----------------------------------------------------------------------------------------------------
--For grading and change in task topics to reuse topics
--Back up jforum_privmsgs, jforum_forums, jforum_topics, jforum_search_topics, jforum_categories tables
--before running this script
-----------------------------------------------------------------------------------------------------
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
CREATE INDEX idx_jforum_grade_forum_id_topic_id ON jforum_grade(forum_id ASC, topic_id ASC);

--add column to the table jforum_topics for grading
ALTER TABLE jforum_topics ADD topic_grade NUMBER(1) DEFAULT 0 NOT NULL;
ALTER TABLE jforum_search_topics ADD COLUMN topic_grade NUMBER(1) DEFAULT 0 NOT NULL;

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

CREATE INDEX idx_jforum_evaluations_grade_id ON jforum_evaluations(grade_id);
CREATE INDEX idx_jforum_evaluations_user_id ON jforum_evaluations(user_id);

--add archived column to jforum_categories
ALTER TABLE jforum_categories ADD archived NUMBER(1) DEFAULT 0 NOT NULL;

--add topic_export to jforum_topics
ALTER TABLE jforum_topics ADD topic_export NUMBER(1) DEFAULT 0 NOT NULL;
ALTER TABLE jforum_search_topics ADD COLUMN topic_export NUMBER(1) DEFAULT 0 NOT NULL;

--increase size for mimetype column to support Office 2007 attachments
ALTER TABLE jforum_attach_desc MODIFY mimetype VARCHAR2(100) DEFAULT NULL;

--back up jforum_topics before running this statement
--modify task topic type to reuse/export topic
--Run the below statement to update task topic type to reuse/export topic
--UPDATE jforum_topics SET topic_type = 0, topic_export = 1 WHERE topic_type = 1;