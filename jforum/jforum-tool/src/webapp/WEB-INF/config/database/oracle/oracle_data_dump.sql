---------------------------------------------------------------------------------- 
-- $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/WEB-INF/config/database/oracle/oracle_data_dump.sql $ 
-- $Id: oracle_data_dump.sql 55571 2008-12-02 19:26:02Z murthy@etudes.org $ 
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
-- 
-- General Grop
INSERT INTO jforum_groups ( group_id, group_name, group_description ) VALUES (jforum_groups_seq.nextval, 'General', 'General Users');



INSERT INTO jforum_roles (role_id, group_id, name, role_type) VALUES (jforum_roles_seq.nextval, jforum_groups_seq.currval, 'perm_administration', 0);

INSERT INTO jforum_roles (role_id, group_id, name, role_type) VALUES (jforum_roles_seq.nextval, jforum_groups_seq.currval, 'perm_moderation', 0);

INSERT INTO jforum_roles (role_id, group_id, name, role_type) VALUES (jforum_roles_seq.nextval, jforum_groups_seq.currval, 'perm_moderation_post_remove', 0);

INSERT INTO jforum_roles (role_id, group_id, name, role_type) VALUES (jforum_roles_seq.nextval, jforum_groups_seq.currval, 'perm_moderation_post_edit', 0);

INSERT INTO jforum_roles (role_id, group_id, name, role_type) VALUES (jforum_roles_seq.nextval, jforum_groups_seq.currval, 'perm_moderation_topic_move', 0);

INSERT INTO jforum_roles (role_id, group_id, name, role_type) VALUES (jforum_roles_seq.nextval, jforum_groups_seq.currval, 'perm_moderation_topic_lockUnlock', 0);



--
--  Users from General Group
INSERT INTO jforum_users ( user_id, username, user_password, user_regdate, sakai_user_id ) VALUES ( jforum_users_seq.nextval, 'Anonymous', 'nopass', SYSDATE, 'Anonymous');

INSERT INTO jforum_user_groups (group_id, user_id) VALUES (jforum_groups_seq.currval, jforum_users_seq.currval);

-- 
--  Admin Group

INSERT INTO jforum_groups ( group_id, group_name, group_description ) VALUES (jforum_groups_seq.nextval, 'Administration', 'Admin Users');


INSERT INTO jforum_roles (role_id, group_id, name, role_type) VALUES (jforum_roles_seq.nextval, jforum_groups_seq.currval, 'perm_administration', 1);

INSERT INTO jforum_roles (role_id, group_id, name, role_type) VALUES (jforum_roles_seq.nextval, jforum_groups_seq.currval, 'perm_moderation', 1);

INSERT INTO jforum_roles (role_id, group_id, name, role_type) VALUES (jforum_roles_seq.nextval, jforum_groups_seq.currval, 'perm_moderation_post_remove', 1);

INSERT INTO jforum_roles (role_id, group_id, name, role_type) VALUES (jforum_roles_seq.nextval, jforum_groups_seq.currval, 'perm_moderation_post_edit', 1);

INSERT INTO jforum_roles (role_id, group_id, name, role_type) VALUES (jforum_roles_seq.nextval, jforum_groups_seq.currval, 'perm_moderation_topic_move', 1);

INSERT INTO jforum_roles (role_id, group_id, name, role_type) VALUES (jforum_roles_seq.nextval, jforum_groups_seq.currval, 'perm_moderation_topic_lockUnlock', 1);

INSERT INTO jforum_roles (role_id, group_id, name, role_type) VALUES (jforum_roles_seq.nextval, jforum_groups_seq.currval, 'perm_create_sticky_announcement_topics', 1);


-- 
--  Users from Admin Group
--

INSERT INTO jforum_users ( user_id, username, user_password, user_regdate, user_posts, user_fname, user_lname, sakai_user_id ) VALUES (jforum_users_seq.nextval, 'admin', '21232f297a57a5a743894a0e4a801fc3', SYSDATE, 1, 'Sakai', 'Administrator', 'admin');

INSERT INTO jforum_user_groups (group_id, user_id) VALUES (jforum_groups_seq.currval, jforum_users_seq.currval );



-- 
--  Smilies
--

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':)','<img src=\"#CONTEXT#/images/smilies/3b63d1616c5dfcf29f8a7a031aaa7cad.gif\" border=\"0\">','3b63d1616c5dfcf29f8a7a031aaa7cad.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':-)','<img src=\"#CONTEXT#/images/smilies/3b63d1616c5dfcf29f8a7a031aaa7cad.gif\" border=\"0\">','3b63d1616c5dfcf29f8a7a031aaa7cad.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':D','<img src=\"#CONTEXT#/images/smilies/283a16da79f3aa23fe1025c96295f04f.gif\" border=\"0\">','283a16da79f3aa23fe1025c96295f04f.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':-D','<img src=\"#CONTEXT#/images/smilies/283a16da79f3aa23fe1025c96295f04f.gif\" border=\"0\">','283a16da79f3aa23fe1025c96295f04f.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':(','<img src=\"#CONTEXT#/images/smilies/9d71f0541cff0a302a0309c5079e8dee.gif\" border=\"0\">','9d71f0541cff0a302a0309c5079e8dee.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':-(','<img src=\"#CONTEXT#/images/smilies/9d71f0541cff0a302a0309c5079e8dee.gif\" border=\"0\">','9d71f0541cff0a302a0309c5079e8dee.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':-o','<img src=\"#CONTEXT#/images/smilies/47941865eb7bbc2a777305b46cc059a2.gif\" border=\"0\">','47941865eb7bbc2a777305b46cc059a2.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':shock:','<img src=\"#CONTEXT#/images/smilies/385970365b8ed7503b4294502a458efa.gif\" border=\"0\">','385970365b8ed7503b4294502a458efa.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':?:','<img src=\"#CONTEXT#/images/smilies/136dd33cba83140c7ce38db096d05aed.gif\" border=\"0\">','136dd33cba83140c7ce38db096d05aed.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, '8)','<img src=\"#CONTEXT#/images/smilies/b2eb59423fbf5fa39342041237025880.gif\" border=\"0\">','b2eb59423fbf5fa39342041237025880.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':lol:','<img src=\"#CONTEXT#/images/smilies/97ada74b88049a6d50a6ed40898a03d7.gif\" border=\"0\">','97ada74b88049a6d50a6ed40898a03d7.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':x','<img src=\"#CONTEXT#/images/smilies/1069449046bcd664c21db15b1dfedaee.gif\" border=\"0\">','1069449046bcd664c21db15b1dfedaee.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':P','<img src=\"#CONTEXT#/images/smilies/69934afc394145350659cd7add244ca9.gif\" border=\"0\">','69934afc394145350659cd7add244ca9.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':-P','<img src=\"#CONTEXT#/images/smilies/69934afc394145350659cd7add244ca9.gif\" border=\"0\">','69934afc394145350659cd7add244ca9.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':oops:','<img src=\"#CONTEXT#/images/smilies/499fd50bc713bfcdf2ab5a23c00c2d62.gif\" border=\"0\">','499fd50bc713bfcdf2ab5a23c00c2d62.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':cry:','<img src=\"#CONTEXT#/images/smilies/c30b4198e0907b23b8246bdd52aa1c3c.gif\" border=\"0\">','c30b4198e0907b23b8246bdd52aa1c3c.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':evil:','<img src=\"#CONTEXT#/images/smilies/2e207fad049d4d292f60607f80f05768.gif\" border=\"0\">','2e207fad049d4d292f60607f80f05768.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':twisted:','<img src=\"#CONTEXT#/images/smilies/908627bbe5e9f6a080977db8c365caff.gif\" border=\"0\">','908627bbe5e9f6a080977db8c365caff.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':roll:','<img src=\"#CONTEXT#/images/smilies/2786c5c8e1a8be796fb2f726cca5a0fe.gif\" border=\"0\">','2786c5c8e1a8be796fb2f726cca5a0fe.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':wink:','<img src=\"#CONTEXT#/images/smilies/8a80c6485cd926be453217d59a84a888.gif\" border=\"0\">','8a80c6485cd926be453217d59a84a888.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ';)','<img src=\"#CONTEXT#/images/smilies/8a80c6485cd926be453217d59a84a888.gif\" border=\"0\">','8a80c6485cd926be453217d59a84a888.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ';-)','<img src=\"#CONTEXT#/images/smilies/8a80c6485cd926be453217d59a84a888.gif\" border=\"0\">','8a80c6485cd926be453217d59a84a888.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':!:','<img src=\"#CONTEXT#/images/smilies/9293feeb0183c67ea1ea8c52f0dbaf8c.gif\" border=\"0\">','9293feeb0183c67ea1ea8c52f0dbaf8c.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':?','<img src=\"#CONTEXT#/images/smilies/0a4d7238daa496a758252d0a2b1a1384.gif\" border=\"0\">','0a4d7238daa496a758252d0a2b1a1384.gif');
INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':idea:','<img src=\"#CONTEXT#/images/smilies/8f7fb9dd46fb8ef86f81154a4feaada9.gif\" border=\"0\">','8f7fb9dd46fb8ef86f81154a4feaada9.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':arrow:','<img src=\"#CONTEXT#/images/smilies/d6741711aa045b812616853b5507fd2a.gif\" border=\"0\">','d6741711aa045b812616853b5507fd2a.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval, ':mrgreen:','<img src=\"#CONTEXT#/images/smilies/ed515dbff23a0ee3241dcc0a601c9ed6.gif\" border=\"0\">','ed515dbff23a0ee3241dcc0a601c9ed6.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval,':hunf:','<img src=\"#CONTEXT#/images/smilies/0320a00cb4bb5629ab9fc2bc1fcc4e9e.gif\" border=\"0\">','0320a00cb4bb5629ab9fc2bc1fcc4e9e.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval,':XD:','<img src=\"#CONTEXT#/images/smilies/49869fe8223507d7223db3451e5321aa.gif\" border=\"0\">','49869fe8223507d7223db3451e5321aa.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval,':thumbup:','<img src=\"#CONTEXT#/images/smilies/e8a506dc4ad763aca51bec4ca7dc8560.gif\" border=\"0\">','e8a506dc4ad763aca51bec4ca7dc8560.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval,':thumbdown:','<img src=\"#CONTEXT#/images/smilies/e78feac27fa924c4d0ad6cf5819f3554.gif\" border=\"0\">','e78feac27fa924c4d0ad6cf5819f3554.gif');

INSERT INTO jforum_smilies VALUES ( jforum_smilies_seq.nextval,':|','<img src=\"#CONTEXT#/images/smilies/1cfd6e2a9a2c0cf8e74b49b35e2e46c7.gif\" border=\"0\">','1cfd6e2a9a2c0cf8e74b49b35e2e46c7.gif');

--

--
-- Demonstration Forum
--
--INSERT INTO jforum_categories VALUES (jforum_categories_seq.nextval,'Category Test',1,0);
--INSERT INTO jforum_forums VALUES (jforum_forums_seq.nextval,jforum_categories_seq.currval,'Test Forum','This is a test forum',1,1,1,0, null, null);
--INSERT INTO jforum_topics VALUES (jforum_topics_seq.nextval,jforum_forums_seq.currval,'Welcome to JForum',jforum_users_seq.currval,SYSDATE,0,0,0,0,1,1,0);
--INSERT INTO jforum_posts VALUES (jforum_posts_seq.nextval,jforum_topics_seq.currval,jforum_forums_seq.currval,jforum_users_seq.currval,SYSDATE,'127.0.0.1',1,0,1,1,SYSDATE,2,1,0,0);
--INSERT INTO jforum_posts_text VALUES (1,EMPTY_BLOB(),'Welcome to JForum');

--
-- View Forum
--
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (jforum_roles_seq.nextval, 'perm_forum', 1, 0);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (jforum_roles_seq.currval, '1', 1);

INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (jforum_roles_seq.nextval, 'perm_forum', 2, 0);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (15, '1', 1);

--
-- Anonymous posts
--
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (jforum_roles_seq.nextval, 'perm_anonymous_post', 1, 0);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (jforum_roles_seq.currval, '1', 1);

INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (jforum_roles_seq.nextval, 'perm_anonymous_post', 2, 0);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (jforum_roles_seq.currval, '1', 1);

--
-- View Category
--
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (jforum_roles_seq.nextval, 'perm_category', 1, 0);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (jforum_roles_seq.currval, '1', 1);

INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (jforum_roles_seq.nextval, 'perm_category', 2, 0);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (jforum_roles_seq.currval, '1', 1);

--
-- Sticky / Announcements
--
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (jforum_roles_seq.nextval, 'perm_create_sticky_announcement_topics', 1, 1);
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (jforum_roles_seq.nextval, 'perm_create_sticky_announcement_topics', 2, 1);

--
-- Create / Reply to topics
--
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (jforum_roles_seq.nextval, 'perm_read_only_forums', 1, 0);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (jforum_roles_seq.currval, '1', 1);

INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (jforum_roles_seq.nextval, 'perm_read_only_forums', 2, 0);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (jforum_roles_seq.currval, '1', 1);

-- 
-- Enable HTML
--
INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (jforum_roles_seq.nextval, 'perm_html_disabled', 1, 0);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (jforum_roles_seq.currval, '1', 1);

INSERT INTO jforum_roles (role_id, name, group_id, role_type ) VALUES (jforum_roles_seq.nextval, 'perm_html_disabled', 2, 0);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (jforum_roles_seq.currval, '1', 1);

--
-- Attachments
--
INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (jforum_roles_seq.nextval, 'perm_attachments_enabled', 1, 0);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (jforum_roles_seq.currval, '1', 1);

INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (jforum_roles_seq.nextval, 'perm_attachments_enabled', 2, 0);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (jforum_roles_seq.currval, '1', 1);

INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (jforum_roles_seq.nextval, 'perm_attachments_download', 1, 1);
INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (jforum_roles_seq.nextval, 'perm_attachments_download', 2, 1);


--
-- Bookmarks
--
INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (jforum_roles_seq.nextval, 'perm_bookmarks_enabled', 1, 1);
INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (jforum_roles_seq.nextval, 'perm_bookmarks_enabled', 2, 1);

--
-- Karma
--
INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (jforum_roles_seq.nextval, 'perm_karma_enabled', 1, 1);
INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (jforum_roles_seq.nextval, 'perm_karma_enabled', 2, 1);

--
-- Reply only
--
INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (jforum_roles_seq.nextval, 'perm_reply_only', 1, 0);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (jforum_roles_seq.currval, '1', 1);

INSERT INTO jforum_roles (role_id, name, group_id, role_type) VALUES (jforum_roles_seq.nextval, 'perm_reply_only', 2, 0);
INSERT INTO jforum_role_values ( role_id, role_value, role_type ) VALUES (jforum_roles_seq.currval, '1', 1);

--1/6/06 - Mallika - adding insert stmts for default extensions
-- JMH Added sequence IDs to insert statements

insert into jforum_extension_groups(extension_group_id, name,allow,download_mode) values(jforum_extension_groups_seq.nextval, 'Images',1,1);
insert into jforum_extension_groups(extension_group_id, name,allow,download_mode) values(jforum_extension_groups_seq.nextval, 'Documents',1,2);
insert into jforum_extension_groups(extension_group_id, name,allow,download_mode) values(jforum_extension_groups_seq.nextval, 'Media',1,2);
insert into jforum_extension_groups(extension_group_id, name,allow,download_mode) values(jforum_extension_groups_seq.nextval, 'Utility',1,2);
insert into jforum_extension_groups(extension_group_id, name,allow,download_mode) values(jforum_extension_groups_seq.nextval, 'Banned',0,2);


insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Images'),'bmp',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Images'),'jpg',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Images'),'jpeg',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Images'),'gif',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Images'),'png',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Images'),'psd',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Images'),'tiff',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Images'),'swf',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Documents'),'doc',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Documents'),'pdf',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Documents'),'xls',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Documents'),'ppt',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Documents'),'pps',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Documents'),'html',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Documents'),'htm',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Media'),'midi',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Media'),'mov',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Media'),'wav',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Media'),'mp3',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Media'),'acc',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Utility'),'gz',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Utility'),'tar',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Utility'),'zip',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Utility'),'sit',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Utility'),'sitx',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Banned'),'exe',0);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Banned'),'bat',0);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Banned'),'pif',0);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Banned'),'bin',0);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Documents'),'pptx',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Documents'),'docx',1);
insert into jforum_extensions(extension_id, extension_group_id,extension,allow) values(jforum_extensions_seq.nextval, (select extension_group_id from jforum_extension_groups where name='Documents'),'xlsx',1);
