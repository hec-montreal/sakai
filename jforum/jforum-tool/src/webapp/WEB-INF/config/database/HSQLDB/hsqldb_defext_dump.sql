---------------------------------------------------------------------------------- 
-- $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/WEB-INF/config/database/HSQLDB/hsqldb_defext_dump.sql $ 
-- $Id: hsqldb_defext_dump.sql 55571 2008-12-02 19:26:02Z murthy@etudes.org $ 
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
--1/6/06 - Mallika - adding insert stmts for default extensions
insert into jforum_extension_groups(name,allow,download_mode) values('Images',1,1);
insert into jforum_extension_groups(name,allow,download_mode) values('Documents',1,2);
insert into jforum_extension_groups(name,allow,download_mode) values('Media',1,2);
insert into jforum_extension_groups(name,allow,download_mode) values('Utility',1,2);
insert into jforum_extension_groups(name,allow,download_mode) values('Banned',0,2);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Images'),'bmp',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Images'),'jpg',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Images'),'jpeg',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Images'),'gif',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Images'),'png',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Images'),'psd',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Images'),'tiff',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Images'),'swf',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Documents'),'doc',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Documents'),'pdf',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Documents'),'xls',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Documents'),'ppt',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Documents'),'pps',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Documents'),'html',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Documents'),'htm',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Media'),'midi',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Media'),'mov',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Media'),'wav',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Media'),'mp3',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Media'),'acc',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Utility'),'gz',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Utility'),'tar',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Utility'),'zip',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Utility'),'sit',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Utility'),'sitx',1);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Banned'),'exe',0);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Banned'),'bat',0);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Banned'),'pif',0);
insert into jforum_extensions(extension_group_id,extension,allow) values((select extension_group_id from jforum_extension_groups where name='Banned'),'bin',0);
