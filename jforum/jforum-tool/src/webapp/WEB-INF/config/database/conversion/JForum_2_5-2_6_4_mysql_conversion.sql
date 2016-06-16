---------------------------------------------------------------------------------- 
-- $URL: $ 
-- $Id: $ 
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
-- This is the MySQL JForum 2.5 to JForum 2.6.4 conversion script
-----------------------------------------------------------------------------------------------------
--change the column data type from TEXT to LONGTEXT to avoid Data truncation error
ALTER TABLE jforum_posts_text MODIFY COLUMN post_text LONGTEXT DEFAULT NULL;

--change the column data type from TEXT to LONGTEXT to avoid Data truncation error
ALTER TABLE jforum_privmsgs_text MODIFY COLUMN privmsgs_text LONGTEXT DEFAULT NULL;

--add privmsgs_flag_to_follow to jforum_privmsgs
ALTER TABLE jforum_privmsgs ADD COLUMN privmsgs_flag_to_follow TINYINT(1) NOT NULL DEFAULT 0 AFTER privmsgs_attachments;

--add privmsgs_replied to jforum_privmsgs
ALTER TABLE jforum_privmsgs ADD COLUMN privmsgs_replied TINYINT(1) NOT NULL DEFAULT 0 AFTER privmsgs_flag_to_follow;

