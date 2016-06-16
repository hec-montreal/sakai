---------------------------------------------------------------------------------- 
-- $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/WEB-INF/config/database/generic/indices.sql $ 
-- $Id: indices.sql 55571 2008-12-02 19:26:02Z murthy@etudes.org $ 
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
CREATE INDEX JF_SCC_COURSE_ID_IDX ON jforum_sakai_course_categories(course_id);
CREATE INDEX JF_SCC_CATEGORIES_ID_IDX ON jforum_sakai_course_categories(categories_id);
CREATE INDEX JF_SCG_COURSE_ID_IDX ON jforum_sakai_course_groups(course_id);
CREATE INDEX JF_SCG_GROUP_ID_IDX ON jforum_sakai_course_groups(group_id);
CREATE INDEX JF_SCP_COURSE_ID_IDX ON jforum_sakai_course_privmsgs(course_id);
CREATE INDEX JF_SCP_PRIVMSGS_ID_IDX ON jforum_sakai_course_privmsgs(privmsgs_id);
CREATE INDEX JF_SCI_COURSE_ID_IDX ON jforum_sakai_course_initvalues(course_id);
CREATE INDEX JF_SS_USER_ID_IDX ON jforum_sakai_sessions(user_id); 
CREATE INDEX JF_SS_COURSE_ID_IDX ON jforum_sakai_sessions(course_id); 
CREATE INDEX JF_JS_SESSION_USER_ID ON jforum_sessions(session_user_id);
CREATE INDEX SAKAI_USER_ID_IDX ON jforum_users(sakai_user_id);
----Drop existing indexes on jforum_search_wordmatch table 
DROP INDEX post_id ON jforum_search_wordmatch;
DROP INDEX word_id ON jforum_search_wordmatch;
--create new index on jforum_search_wordmatch for columns post_id, word_id
CREATE INDEX POST_ID_WORD_ID ON jforum_search_wordmatch(post_id, word_id);

CREATE INDEX idx_jforum_topics_mark_user_id ON jforum_topics_mark(user_id);
CREATE INDEX idx_jforum_privmsgs_privmsgs_to_userid ON jforum_privmsgs (privmsgs_to_userid);


