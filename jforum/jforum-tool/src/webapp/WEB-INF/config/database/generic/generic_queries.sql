################################################################################## 
# $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/WEB-INF/config/database/generic/generic_queries.sql $ 
# $Id: generic_queries.sql 71044 2010-10-29 17:37:24Z murthy@etudes.org $ 
################################################################################### 
# 
# Copyright (c) 2008, 2009, 2010 Etudes, Inc. 
# 
# Licensed under the Apache License, Version 2.0 (the "License"); 
# you may not use this file except in compliance with the License. 
# You may obtain a copy of the License at 
# 
# http://www.apache.org/licenses/LICENSE-2.0 
# 
# Unless required by applicable law or agreed to in writing, software 
# distributed under the License is distributed on an "AS IS" BASIS, 
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
# See the License for the specific language governing permissions and 
# limitations under the License. 
# 
# Portions completed before July 1, 2004 Copyright (c) 2003, 2004 Rafael Steil, All rights reserved, licensed under the BSD license. 
# http://www.opensource.org/licenses/bsd-license.php 
# 
# Redistribution and use in source and binary forms, 
# with or without modification, are permitted provided 
# that the following conditions are met: 
# 
# 1) Redistributions of source code must retain the above 
# copyright notice, this list of conditions and the 
# following disclaimer. 
# 2) Redistributions in binary form must reproduce the 
# above copyright notice, this list of conditions and 
# the following disclaimer in the documentation and/or 
# other materials provided with the distribution. 
# 3) Neither the name of "Rafael Steil" nor 
# the names of its contributors may be used to endorse 
# or promote products derived from this software without 
# specific prior written permission. 
# 
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
# HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
# EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
# BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
# MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
# PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
# THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
# FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
# EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
# (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
# OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
# CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
# IN CONTRACT, STRICT LIABILITY, OR TORT 
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
# ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
# ADVISED OF THE POSSIBILITY OF SUCH DAMAGE 
##################################################################################
# ############
# GroupModel 
# ############
GroupModel.selectAll = SELECT group_id, group_name, parent_id, group_description FROM jforum_groups ORDER BY group_name
GroupModel.selectById = SELECT group_id, group_name, parent_id, group_description FROM jforum_groups WHERE group_id = ?
GroupModel.canDelete = SELECT COUNT(1) AS total FROM jforum_user_groups WHERE group_id = ?
GroupModel.delete = DELETE FROM jforum_groups WHERE group_id = ?
GroupModel.update = UPDATE jforum_groups SET group_name = ?, parent_id = ?, group_description = ? WHERE group_id = ?
GroupModel.addNew = INSERT INTO jforum_groups (group_name, group_description, parent_id) VALUES (?, ?, ?)
GroupModel.selectUsersIds = SELECT user_id FROM jforum_user_groups WHERE group_id = ?

#New statement by Mallika beg
GroupModel.lastGeneratedGroupId = SELECT MAX(group_id) from jforum_groups
GroupModel.selectGroupByUserId = SELECT COUNT(1) AS total FROM jforum_user_groups WHERE user_id = ? and group_id = ?
GroupModel.selectGroupByName = SELECT a.group_id, a.group_name, a.parent_id, a.group_description FROM jforum_groups a,jforum_sakai_course_groups b WHERE a.group_id = b.group_id and a.group_name = ? and b.course_id = ?
#Mallika end

# #############
# CategoryModel
# #############
CategoryModel.selectById = SELECT categories_id, title, display_order, moderated, archived, gradable, start_date, end_date, lock_end_date FROM jforum_categories WHERE categories_id = ? ORDER BY title 
CategoryModel.selectAll = SELECT categories_id, title, display_order, moderated, archived, gradable, start_date, end_date, lock_end_date FROM jforum_categories ORDER BY display_order
CategoryModel.canDelete = SELECT COUNT(1) AS total FROM jforum_forums WHERE categories_id = ?
CategoryModel.delete = DELETE FROM jforum_categories WHERE categories_id = ?
CategoryModel.update = UPDATE jforum_categories SET title = ?, moderated = ?, gradable = ?, start_date = ?, end_date = ?, lock_end_date = ? WHERE categories_id = ?
CategoryModel.addNew = INSERT INTO jforum_categories (title, display_order, moderated, archived, gradable, start_date, end_date, lock_end_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
CategoryModel.setOrderById = UPDATE jforum_categories SET display_order = ? WHERE categories_id = ?
CategoryModel.getMaxOrder = SELECT MAX(display_order) FROM jforum_categories
CategoryModel.updateDates = UPDATE jforum_categories SET start_date = ?, end_date = ?, lock_end_date = ? WHERE categories_id = ?

#CategoryModel.selectUserPostsCountByCategory = SELECT p.user_id, COUNT(p.post_id) AS user_posts_count, u.user_fname AS user_fname,u.user_lname AS user_lname, u.sakai_user_id \
#	FROM jforum_posts p, jforum_users u \
#	WHERE p.forum_id IN (SELECT forum_id FROM jforum_forums WHERE categories_id = ?) and p.user_id = u.user_id \
#	GROUP BY p.user_id
CategoryModel.selectUserPostsCountByCategory = SELECT p.user_id, COUNT(p.post_id) AS user_posts_count, u.user_fname AS user_fname,u.user_lname AS user_lname, u.sakai_user_id, u.username \
	FROM jforum_posts p, jforum_users u, jforum_forums f \
	WHERE p.forum_id = f.forum_id AND f.categories_id = ? AND p.user_id = u.user_id \
	GROUP BY p.user_id, u.user_fname, u.user_lname, u.sakai_user_id, u.username
	
#Not working on MySQL 5.x CategoryModel.selectAllUserPostsCountByCategory = SELECT s.user_id, COUNT(p.post_id) AS user_posts_count, u.user_fname AS user_fname,u.user_lname AS user_lname, u.sakai_user_id, u.username \
#	FROM  jforum_forums f, (SELECT user_id from jforum_site_users WHERE sakai_site_id = ?) s LEFT OUTER JOIN jforum_users u ON s.user_id = u.user_id \
#	LEFT OUTER JOIN jforum_posts p ON s.user_id = p.user_id \
#	AND f.forum_id = p.forum_id \
#	AND f.categories_id = ? \
#	GROUP BY s.user_id

CategoryModel.selectAllUserPostsCountByCategory = SELECT s.user_id, COUNT(p.post_id) AS user_posts_count, u.user_fname AS user_fname,u.user_lname AS user_lname, u.sakai_user_id, u.username \
	FROM  (SELECT user_id from jforum_site_users WHERE sakai_site_id = ?) s \
	LEFT OUTER JOIN jforum_users u ON (s.user_id = u.user_id) \
	LEFT OUTER JOIN jforum_posts p ON (s.user_id = p.user_id) \
	AND p.forum_id IN (SELECT forum_id FROM jforum_forums f where f.categories_id = ?) \
	GROUP BY s.user_id, u.user_fname, u.user_lname, u.sakai_user_id, u.username

#New statement by Mallika beg
CategoryModel.selectAllByCourseId = SELECT a.categories_id, a.title, a.display_order, a.moderated, a.archived, a.gradable, a.start_date, a.end_date, a.lock_end_date FROM jforum_categories a,jforum_sakai_course_categories b where a.categories_id = b.categories_id and b.course_id = ? ORDER BY a.display_order

CategoryModel.selectSiteArciveCategory = SELECT a.categories_id, a.title, a.display_order, a.moderated, a.archived, a.gradable, a.start_date, a.end_date, a.lock_end_date FROM jforum_categories a,jforum_sakai_course_categories b where a.categories_id = b.categories_id AND a.archived = 1 AND b.course_id = ? 
#Mallika end

# #############
# RankingModel
# #############
RankingModel.selectById = SELECT * FROM jforum_ranks WHERE rank_id = ?
RankingModel.selectAll = SELECT * FROM jforum_ranks ORDER BY rank_min
RankingModel.delete = DELETE FROM jforum_ranks WHERE rank_id = ?
RankingModel.update = UPDATE jforum_ranks SET rank_title = ?, rank_image = ?, rank_special = ?, rank_min = ? WHERE rank_id = ?
RankingModel.addNew = INSERT INTO jforum_ranks ( rank_title, rank_min ) VALUES ( ?, ? )

# #############
# ConfigModel
# #############
ConfigModel.insert = INSERT INTO jforum_config (config_name, config_value) VALUES (?, ?)
ConfigModel.selectById = SELECT config_name, config_value FROM jforum_config WHERE config_id = ?
ConfigModel.selectByName = SELECT config_name, config_value, config_id FROM jforum_config WHERE config_name = ?
ConfigModel.selectAll = SELECT config_name, config_value, config_id FROM jforum_config
ConfigModel.delete = DELETE FROM jforum_config WHERE config_id = ?
ConfigModel.update = UPDATE jforum_config SET config_value = ? WHERE config_name = ?

# ##########
# UserModel
# ##########
UserModel.selectById = SELECT COUNT(pm.privmsgs_to_userid) AS private_messages, u.* \
	FROM jforum_users u \
	LEFT JOIN jforum_privmsgs pm ON pm.privmsgs_type = 1 AND pm.privmsgs_to_userid = u.user_id \
	WHERE u.user_id = ? \
	GROUP BY pm.privmsgs_to_userid
#Mallika - added this 10/3/06 just for member listing
UserModel.selectUserById = SELECT u.* \
	FROM jforum_users u \
	WHERE u.user_id = ? \
	
#Mallika's code beg	

#UserModel.selectAll = SELECT a.user_email, a.user_id, a.user_posts, a.user_regdate, a.username, a.deleted, a.user_karma, a.user_from, a.user_website, a.user_viewemail, a.user_fname, a.user_lname FROM jforum_users a where a.user_id in (select distinct user_id from jforum_user_groups where group_id in (select group_id from jforum_sakai_course_groups where course_id = ?)) ORDER BY a.username
# revised by kkim4/22/06
#UserModel.selectAll = SELECT a.user_email, a.user_id, a.sakai_user_id, a.user_posts, a.user_regdate, a.username, a.deleted, a.user_karma, a.user_from, a.user_website, a.user_viewemail, a.user_fname, a.user_lname, a.user_allow_pm, a.user_notify FROM jforum_users a, jforum_user_groups ug, jforum_sakai_course_groups cg WHERE a.user_id=ug.user_id AND ug.group_id=cg.group_id AND cg.course_id= ? ORDER BY a.username
UserModel.selectAll = SELECT DISTINCT a.user_email, a.user_id, a.sakai_user_id, a.user_posts, a.user_regdate, a.username, a.deleted, a.user_karma, a.user_from, a.user_website, a.user_viewemail, a.user_fname, a.user_lname, a.user_allow_pm, a.user_notify FROM jforum_users a, jforum_user_groups ug, jforum_sakai_course_groups cg WHERE a.user_id=ug.user_id AND ug.group_id=cg.group_id AND cg.course_id= ? ORDER BY a.username
# revise end

#UserModel.selectAllByLimit = SELECT a.user_email, a.user_id, a.user_posts, a.user_regdate, a.username, a.deleted, a.user_karma, a.user_from, a.user_website, a.user_viewemail , a.user_fname, a.user_lname \
#	FROM jforum_users a WHERE a.user_id in (SELECT distinct user_id FROM jforum_user_groups  where group_id in (select group_id from jforum_sakai_course_groups where course_id = ?)) ORDER BY a.username LIMIT ?, ?		
# revised kkim 4/24/06
UserModel.selectAllByLimit = SELECT DISTINCT a.user_email, a.user_id, a.sakai_user_id, a.user_posts, a.user_regdate, a.username, a.deleted, a.user_karma, a.user_from, a.user_website, a.user_viewemail, a.user_fname, a.user_lname, a.user_allow_pm, a.user_notify \
	FROM jforum_users a, jforum_user_groups ug, jforum_sakai_course_groups cg \
	WHERE a.user_id=ug.user_id \
	AND ug.group_id=cg.group_id \
	AND cg.course_id= ?\
	ORDER BY a.username LIMIT ?, ?
#Mallika's code end

#revised by rashmi to get firstname and lastname 09-14-05
UserModel.selectAllByGroup = SELECT user_email, u.user_id, user_regdate, username, user_fname, user_lname \
	FROM jforum_users u, jforum_user_groups ug \
	WHERE u.user_id = ug.user_id \
	AND ug.group_id = ? \
	ORDER BY username LIMIT ?, ?
#end

UserModel.totalUsersByGroup = SELECT COUNT(1) FROM jforum_user_groups WHERE group_id = ?
UserModel.deletedStatus = UPDATE jforum_users SET deleted = ? WHERE user_id = ?
UserModel.isDeleted = SELECT deleted FROM jforum_users WHERE user_id = ?
UserModel.incrementPosts = UPDATE jforum_users SET user_posts = user_posts + 1 WHERE user_id = ?
UserModel.decrementPosts = UPDATE jforum_users SET user_posts = user_posts - 1 WHERE user_id = ?
UserModel.rankingId = UPDATE jforum_users SET rank_id = ? WHERE user_id = ?
UserModel.activeStatus = UPDATE jforum_users SET user_active = ? WHERE user_id = ?

#Statement revised by Rashmi to add first name and last name -beg
#07/17/2006 Murthy - added sakai_user_id
UserModel.addNew = INSERT INTO jforum_users (username, user_password, user_email, user_regdate, user_actkey, user_fname, user_lname, sakai_user_id) VALUES (?, ?, ?, ?, ?,?,?, ?)
#statement end

#Mallika's comments beg
#UserModel.findByName = SELECT user_id, username, user_email FROM jforum_users WHERE UPPER(username) LIKE UPPER(?)
#Mallika's comments end

#Mallika's code beg and revised by rashmi 09-14-05
#UserModel.findByName = SELECT a.user_id, a.username, a.user_email, a.user_fname, a.user_lname FROM jforum_users a, jforum_user_groups b WHERE UPPER(a.username) LIKE UPPER(?) and a.user_id = b.user_id and b.group_id in (select group_id from jforum_sakai_course_groups where course_id = ?)
#UserModel.findByFlName = SELECT a.user_id, a.username, a.user_email, a.user_fname, a.user_lname FROM jforum_users a, jforum_user_groups b WHERE (UPPER(a.user_fname) LIKE UPPER(?) or UPPER(a.user_lname) LIKE UPPER(?)) and a.user_id = b.user_id and b.group_id in (select group_id from jforum_sakai_course_groups where course_id = ?)
#revised by kkim
UserModel.findByFlName = SELECT a.user_id, a.username, a.user_email, a.user_fname, a.user_lname \
        FROM jforum_users a, jforum_user_groups b, jforum_sakai_course_groups c \
        WHERE (UPPER(a.user_fname) LIKE UPPER(?) or UPPER(a.user_lname) LIKE UPPER(?)) \
        and a.user_id = b.user_id and b.group_id = c.group_id and c.course_id = ? \
	ORDER BY a.user_lname
UserModel.findByName = SELECT a.user_id, a.username, a.user_email, a.user_fname, a.user_lname \
	FROM jforum_users a, jforum_user_groups b, jforum_sakai_course_groups c \
	WHERE (UPPER(a.user_fname) LIKE UPPER(?) or UPPER(a.user_lname) LIKE UPPER(?)) \
	and a.user_id = b.user_id and b.group_id = c.group_id and c.course_id = ?
	ORDER BY a.user_lname
# end kkim revision

#Mallika's code end
UserModel.selectByName = SELECT * FROM jforum_users WHERE username = ?
UserModel.addNewWithId = INSERT INTO jforum_users (username, user_password, user_email, user_regdate, user_actkey, user_id) VALUES (?, ?, ?, ?, ?, ?)
#07/17/2006 - Murthy - get user based on sakai user id
UserModel.selectBySakaiUserId = SELECT * FROM jforum_users WHERE sakai_user_id = ?

#Statement revised by Rashmi to add first name and last name -beg
UserModel.update = UPDATE jforum_users SET user_aim = ?, \
	user_avatar = ?,\
	gender = ?, \
	themes_id = ?,\
	user_allow_pm = ?, \
	user_allowavatar = ?, \
	user_allowbbcode = ?, \
	user_allowhtml = ?, \
	user_allowsmilies = ?, \
	user_email = ?, \
	user_from = ?, \
	user_icq = ?, \
	user_interests = ?, \
	user_occ = ?, \
	user_sig = ?, \
	user_website = ?, \
	user_yim = ?, \
	user_msnm = ?, \
	user_password = ?, \
	user_viewemail = ?, \
	user_viewonline = ?, \
	user_notify = ?, \
	user_attachsig = ?, \
	username = ?, \
	user_lang = ?, \
	user_notify_pm = ?, \
	user_fname = ?, \
	user_lname = ?, \
	user_facebook_account = ?, \
	user_twitter_account = ? \
	WHERE user_id = ?
# statement end
	
UserModel.lastUserRegistered = SELECT user_id, username FROM jforum_users ORDER BY user_regdate DESC LIMIT 1
#Mallika - 9/27/06 - changing query below to only get users in this course
#UserModel.totalUsers = SELECT COUNT(1) as total_users FROM jforum_users
UserModel.totalUsers = SELECT COUNT(DISTINCT ug.user_id) FROM jforum_user_groups ug,jforum_sakai_course_groups cg WHERE ug.group_id = cg.group_id AND cg.course_id = ?
UserModel.isUsernameRegistered = SELECT COUNT(1) as registered FROM jforum_users WHERE LOWER(username) = LOWER(?)
UserModel.login = SELECT user_id FROM jforum_users WHERE LOWER(username) = LOWER(?) AND user_password = ?
UserModel.addToGroup = INSERT INTO jforum_user_groups ( user_id, group_id ) VALUES ( ?, ? )
UserModel.removeFromGroup = DELETE FROM jforum_user_groups WHERE user_id = ? AND group_id = ?

UserModel.selectGroups = SELECT ug.group_id, g.group_name FROM jforum_user_groups ug, jforum_groups g \
	WHERE ug.group_id = g.group_id \
	AND ug.user_id = ?
#11/01/05 added by murthy to get user groups for a site
UserModel.selectUserGroups = SELECT ug.group_id, g.group_name FROM jforum_user_groups ug, jforum_groups g, \
	jforum_sakai_course_groups cg \
	WHERE ug.group_id = g.group_id \
	AND cg.group_id = g.group_id \
	AND ug.user_id = ? \
	AND cg.course_id = ? 

UserModel.saveNewPassword = UPDATE jforum_users SET user_password = ? WHERE user_email = ?
UserModel.validateLostPasswordHash = SELECT COUNT(1) AS valid FROM jforum_users WHERE security_hash = ? AND user_email = ?
UserModel.writeLostPasswordHash = UPDATE jforum_users SET security_hash = ? WHERE user_email = ?
UserModel.getUsernameByEmail = SELECT username FROM jforum_users WHERE user_email = ?
UserModel.validateActivationKeyHash = SELECT COUNT(1) AS valid FROM jforum_users WHERE user_actkey = ? AND user_id = ?
UserModel.writeUserActive = UPDATE jforum_users SET user_active = 1, user_actkey = NULL WHERE user_id = ?
UserModel.updateUsername = UPDATE jforum_users SET username = ? WHERE user_id = ?
UserModel.getUsernam = SELECT username FROM jforum_users WHERE user_id = ?

#Statement put in by Rashmi-beg(edited by Mallika)
UserModel.searchGroup = SELECT a.group_id FROM jforum_groups a,jforum_sakai_course_groups b WHERE a.group_id = b.group_id and a.group_name = ? and b.course_id = ?
#Statement put in by Rashmi-end
#Statement put in by Rashmi-beg
#Mallika - 1/23/06 -edited to add start date condn
#UserModel.getNumberOfMessages = SELECT COUNT(p.user_id) AS num_messages FROM jforum_users u, jforum_posts p \
#	WHERE u.user_id = ? \
#	AND u.user_id = p.user_id \
#	AND p.forum_id IN (select f1.forum_id from jforum_forums f1 where (f1.start_date is NULL OR f1.start_date <= curdate()) AND f1.categories_id IN (select categories_id from jforum_sakai_course_categories where course_id = ?)) \
#GROUP BY p.user_id
#revised by kkim 4/22/06
#UserModel.getNumberOfMessages = SELECT COUNT(p.user_id) AS num_messages FROM jforum_users u, jforum_posts p, jforum_forums f1, jforum_sakai_course_categories cc \
#	WHERE u.user_id = ? \
#	AND u.user_id = p.user_id \
#	AND p.forum_id=f1.forum_id \
#	AND f1.categories_id=cc.categories_id \
#	AND cc.course_id = ? \
#GROUP BY p.user_id
#revised again by Mallika 4/28/06 to add start_date condition to above query
UserModel.getNumberOfMessages = SELECT COUNT(p.user_id) AS num_messages FROM jforum_users u, jforum_posts p, jforum_forums f1, jforum_sakai_course_categories cc \
	WHERE u.user_id = ? \
	AND u.user_id = p.user_id \
	AND p.forum_id=f1.forum_id \
	AND (f1.start_date is NULL OR f1.start_date <= curdate()) \
	AND f1.categories_id=cc.categories_id \
	AND cc.course_id = ? \
GROUP BY p.user_id
#Statement put in by Rashmi-end
#<<<Murthy 4/05/07
#add user to site
UserModel.addToSite = INSERT INTO jforum_site_users (sakai_site_id, user_id) VALUES (?, ?)

#remove user from site
UserModel.removeFromSite = DELETE FROM jforum_site_users WHERE sakai_site_id = ? AND user_id = ?

#get all site users
UserModel.selectAllSiteUsers = SELECT DISTINCT a.user_email, a.user_id, a.sakai_user_id, a.user_posts, a.user_regdate, a.username, a.deleted, a.user_karma, a.user_from, a.user_website, a.user_viewemail, a.user_fname, a.user_lname, a.user_allow_pm, a.user_notify, a.user_password, a.user_lang \
	FROM jforum_users a, jforum_site_users b \
	WHERE a.user_id = b.user_id \
	AND b.sakai_site_id = ? \
	ORDER BY a.username

UserModel.selectAllSiteUsersByLimit = SELECT DISTINCT a.user_email, a.user_id, a.sakai_user_id, a.user_posts, a.user_regdate, a.username, a.deleted, a.user_karma, a.user_from, a.user_website, a.user_viewemail, a.user_fname, a.user_lname, a.user_allow_pm, a.user_notify, a.user_password, a.user_lang \
	FROM jforum_users a, jforum_site_users b \
	WHERE a.user_id = b.user_id \
	AND b.sakai_site_id = ? \
	ORDER BY a.username LIMIT ?, ?
#>>>Murthy 4/05/07

UserModel.updateSakaiAccountDetails = UPDATE jforum_users SET user_email = ?, username = ?, user_fname = ?, user_lname = ?, user_lang = ?, user_password = ? WHERE user_id = ?

# #############
# PostModel
# #############
PostModel.selectById = SELECT p.post_id, topic_id, forum_id, p.user_id, post_time, poster_ip, enable_bbcode, enable_html, \
	enable_smilies, enable_sig, post_edit_time, post_edit_count, status, pt.post_subject, pt.post_text, username, p.attach, p.need_moderate \
	FROM jforum_posts p, jforum_posts_text pt, jforum_users u \
	WHERE p.post_id = pt.post_id \
	AND p.post_id = ? \
	AND p.user_id = u.user_id

PostModel.deletePost = DELETE FROM jforum_posts WHERE post_id = ?
PostModel.deletePostText = DELETE FROM jforum_posts_text WHERE post_id = ?
PostModel.deleteWordMatch = DELETE FROM jforum_search_wordmatch WHERE post_id = ?

PostModel.updatePost = UPDATE jforum_posts SET topic_id = ?, forum_id = ?, enable_bbcode = ?, enable_html = ?, enable_smilies = ?, enable_sig = ?, post_edit_time = ?, post_edit_count = post_edit_count + 1, poster_ip = ? WHERE post_id = ?
PostModel.updatePostText = UPDATE jforum_posts_text SET post_text = ?, post_subject = ? WHERE post_id = ?

PostModel.addNewPost = INSERT INTO jforum_posts (topic_id, forum_id, user_id, post_time, poster_ip, enable_bbcode, enable_html, enable_smilies, enable_sig, post_edit_time, need_moderate) \
	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?)
PostModel.addNewPostText = INSERT INTO jforum_posts_text ( post_id, post_text, post_subject ) VALUES (?, ?, ?)

PostModel.selectAllByTopicByLimit = SELECT p.post_id, topic_id, forum_id, p.user_id, post_time, poster_ip, enable_bbcode, p.attach, \
	enable_html, enable_smilies, enable_sig, post_edit_time, post_edit_count, status, pt.post_subject, pt.post_text, username, p.need_moderate \
	FROM jforum_posts p, jforum_posts_text pt, jforum_users u \
	WHERE p.post_id = pt.post_id \
	AND topic_id = ? \
	AND p.user_id = u.user_id \
	AND p.need_moderate = 0 \
	ORDER BY post_time ASC \
	LIMIT ?, ?
	
PostModel.selectAllByTopicByUser = SELECT p.post_id, topic_id, forum_id, p.user_id, post_time, poster_ip, enable_bbcode, p.attach, \
	enable_html, enable_smilies, enable_sig, post_edit_time, post_edit_count, status, pt.post_subject, pt.post_text, username, p.need_moderate \
	FROM jforum_posts p, jforum_posts_text pt, jforum_users u \
	WHERE p.post_id = pt.post_id \
	AND p.topic_id = ? \
	AND p.user_id = ? \
	AND p.user_id = u.user_id \
	AND p.need_moderate = 0 \
	ORDER BY p.topic_id ASC, p.post_time ASC
	
PostModel.selectAllByForumByUser = SELECT p.post_id, topic_id, forum_id, p.user_id, post_time, poster_ip, enable_bbcode, p.attach, \
	enable_html, enable_smilies, enable_sig, post_edit_time, post_edit_count, status, pt.post_subject, pt.post_text, username, p.need_moderate \
	FROM jforum_posts p, jforum_posts_text pt, jforum_users u \
	WHERE p.post_id = pt.post_id \
	AND p.forum_id = ? \
	AND p.user_id = ? \
	AND p.user_id = u.user_id \
	AND p.need_moderate = 0 \
	ORDER BY p.post_time ASC
	
PostModel.selectAllByCategoryByUser = SELECT p.post_id, topic_id, p.forum_id, p.user_id, post_time, poster_ip, enable_bbcode, p.attach, \
	enable_html, enable_smilies, enable_sig, post_edit_time, post_edit_count, status, pt.post_subject, pt.post_text, username, p.need_moderate \
	FROM jforum_posts p, jforum_posts_text pt, jforum_users u, jforum_forums f \
	WHERE p.post_id = pt.post_id \
	AND p.forum_id = f.forum_id \
	AND f.categories_id = ? \
	AND p.user_id = ? \
	AND p.user_id = u.user_id \
	AND p.need_moderate = 0 \
	ORDER BY p.forum_id ASC, p.topic_id ASC, p.post_time ASC	
	
PostModel.setForumByTopic = UPDATE jforum_posts SET forum_id = ? WHERE topic_id = ?
PostModel.deleteByTopic = SELECT post_id, user_id FROM jforum_posts WHERE topic_id = ?

# #############
# ForumModel
# #############
ForumModel.selectById = SELECT f.*, COUNT(p.post_id) AS total_posts \
	FROM jforum_forums f \
	LEFT JOIN jforum_topics t ON t.forum_id = f.forum_id \
	LEFT JOIN jforum_posts p ON p.topic_id = t.topic_id \
	WHERE f.forum_id = ? \
	GROUP BY f.forum_id

ForumModel.selectAll = SELECT f.*, COUNT(p.post_id) AS total_posts \
	FROM jforum_forums f \
	LEFT JOIN jforum_topics t ON t.forum_id = f.forum_id \
	LEFT JOIN jforum_posts p ON p.topic_id = t.topic_id \
	GROUP BY f.categories_id, f.forum_order
	
ForumModel.selectByCategoryId = SELECT f.*, COUNT(p.post_id) AS total_posts \
	FROM jforum_forums f \
	LEFT JOIN jforum_topics t ON t.forum_id = f.forum_id \
	LEFT JOIN jforum_posts p ON p.topic_id = t.topic_id \
	WHERE f.categories_id = ? \
	GROUP BY f.categories_id, f.forum_order
	
ForumModel.selectForumCountWithDatesByCategoryId = SELECT count(forum_id) As forums_with_dates FROM jforum_forums \
	WHERE categories_id = ? \
	AND (start_date IS NOT NULL OR end_date IS NOT NULL)

# Mallika's code beg	
#ForumModel.selectAllByCourseId = SELECT f.*, COUNT(p.post_id) AS total_posts \
#	FROM jforum_forums f \
#	LEFT JOIN jforum_topics t ON t.forum_id = f.forum_id \
#	LEFT JOIN jforum_posts p ON p.topic_id = t.topic_id \
#	WHERE f.categories_id in (SELECT categories_id FROM jforum_sakai_course_categories WHERE course_id = ?) \
#	GROUP BY f.categories_id, f.forum_order	
#Mallika's code end

# revised by kkim 5/3/06
#ForumModel.selectAllByCourseId = SELECT f.*, COUNT(p.post_id) AS total_posts \
#	FROM jforum_forums f, jforum_sakai_course_categories c \
#	LEFT JOIN jforum_topics t ON t.forum_id = f.forum_id \
#	LEFT JOIN jforum_posts p ON p.topic_id = t.topic_id \
#	WHERE f.categories_id = c.categories_id \
#	AND c.course_id = ? \
#	GROUP BY f.categories_id, f.forum_order
# revision end
ForumModel.selectAllByCourseId = SELECT f.*, COUNT(p.post_id) AS total_posts \
	FROM jforum_sakai_course_categories c, jforum_forums f \
	LEFT JOIN jforum_topics t ON f.forum_id = t.forum_id \
	LEFT JOIN jforum_posts p ON p.topic_id = t.topic_id \
	WHERE f.categories_id = c.categories_id \
	AND c.course_id = ? \
	GROUP BY f.categories_id, f.forum_order
#Mallika - 9/26/06
ForumModel.selectIdByCourseId = SELECT f.forum_id \
    FROM jforum_forums f, jforum_sakai_course_categories c \
    WHERE f.categories_id = c.categories_id \
    AND c.course_id = ? \

ForumModel.canDelete = SELECT COUNT(1) AS total FROM jforum_topics WHERE forum_id = ?

#1/9/06 - Mallika - changing code below to add start_date and end_date
ForumModel.setModerated = UPDATE jforum_forums SET moderated = ? WHERE categories_id = ?
ForumModel.delete = DELETE FROM jforum_forums WHERE forum_id = ?
#03/13/07 - Murthy - Added the forum type, access type
ForumModel.update = UPDATE jforum_forums SET categories_id = ?, forum_name = ?, forum_desc = ?, start_date = ?,end_date = ?, lock_end_date = ?, moderated = ?, forum_type = ?, forum_access_type = ?, forum_grade_type = ? WHERE forum_id = ?
#03/13/07 - Murthy - Added the forum type, access type
ForumModel.updateDates = UPDATE jforum_forums SET start_date = ?, end_date = ?, lock_end_date = ? WHERE forum_id = ?
ForumModel.addNew = INSERT INTO jforum_forums (categories_id, forum_name, forum_desc, start_date, end_date, lock_end_date, forum_order, moderated, forum_type, forum_access_type, forum_grade_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
ForumModel.updateLastPost = UPDATE jforum_forums SET forum_last_post_id = ? WHERE forum_id = ?
ForumModel.incrementTotalTopics = UPDATE jforum_forums SET forum_topics = forum_topics + ? WHERE forum_id = ?
ForumModel.decrementTotalTopics = UPDATE jforum_forums SET forum_topics = forum_topics - ? WHERE forum_id = ?
ForumModel.decrementTotalPosts = UPDATE jforum_forums SET total_posts = total_posts - ? WHERE forum_id = ?
ForumModel.getTotalTopics = SELECT COUNT(topic_id) as total FROM jforum_topics WHERE forum_id = ?
ForumModel.setOrderById = UPDATE jforum_forums SET forum_order = ? WHERE forum_id = ? 
ForumModel.getMaxOrder = SELECT MAX(forum_order) FROM jforum_forums
ForumModel.addForumGroups = INSERT INTO jforum_forum_sakai_groups (forum_id, sakai_group_id) VALUES (?, ?)
ForumModel.getForumGroups = SELECT forum_id, sakai_group_id from jforum_forum_sakai_groups where forum_id = ?
ForumModel.deleteForumGroup = DELETE FROM jforum_forum_sakai_groups WHERE forum_id = ? and sakai_group_id = ?

#revised by rashmi
ForumModel.lastPostInfo = SELECT post_time, p.topic_id, t.topic_replies, post_id, u.user_id, username, u.user_fname, u.user_lname \
	FROM jforum_posts p, jforum_users u, jforum_topics t , jforum_forums f \
	WHERE t.forum_id = f.forum_id \
	AND t.topic_id = p.topic_id \
	AND f.forum_last_post_id = t.topic_last_post_id \
	AND t.topic_last_post_id = p.post_id \
	AND p.forum_id = ? \
	AND p.user_id = u.user_id
#revise end

ForumModel.totalMessages = SELECT COUNT(1) as total_messages FROM jforum_posts
ForumModel.getMaxPostId = SELECT MAX(post_id) AS post_id FROM jforum_posts WHERE forum_id = ?
ForumModel.moveTopics = UPDATE jforum_topics SET forum_id = ? WHERE topic_id = ?
ForumModel.checkUnreadTopics = SELECT MAX(post_time), topic_id FROM jforum_posts WHERE forum_id = ? AND post_time > ? GROUP BY topic_id
ForumModel.latestTopicIdForfix = SELECT MAX(topic_id) AS topic_id FROM jforum_posts WHERE forum_id = ?
ForumModel.fixLatestPostData = UPDATE jforum_topics SET topic_last_post_id = ? WHERE topic_id = ?
ForumModel.fixForumLatestPostData = UPDATE jforum_forums SET forum_last_post_id = ? WHERE forum_id = ?

ForumModel.getUnreadForums = SELECT t.forum_id, t.topic_id, p.post_time \
	FROM jforum_topics t, jforum_posts p \
	WHERE p.post_id = t.topic_last_post_id \
	AND p.post_time > ?
	
ForumModel.selectUserPostsCountByForum	= SELECT p.user_id, COUNT(p.post_id) AS user_posts_count, u.user_fname AS user_fname,u.user_lname AS user_lname, u.sakai_user_id, u.username \
	FROM jforum_posts p, jforum_users u \
	WHERE p.forum_id = ? and p.user_id = u.user_id \
	GROUP BY p.user_id, u.user_fname, u.user_lname, u.sakai_user_id, u.username
	
ForumModel.selectAllUserPostsCountByForum = SELECT s.user_id, COUNT(p.post_id) AS user_posts_count, u.user_fname AS user_fname,u.user_lname AS user_lname, u.sakai_user_id, u.username \
	FROM (SELECT user_id from jforum_site_users WHERE sakai_site_id = ?) s LEFT OUTER JOIN jforum_users u ON s.user_id = u.user_id \
	LEFT OUTER JOIN jforum_posts p ON s.user_id = p.user_id \
	AND p.forum_id = ? \
	GROUP BY s.user_id, u.user_fname, u.user_lname, u.sakai_user_id, u.username

ForumModel.selectUserNoPostsCountByCourse = SELECT u.user_id, 0 AS user_posts_count, u.user_fname AS user_fname,u.user_lname AS user_lname, u.sakai_user_id \
	FROM jforum_site_users s, jforum_users u \
	WHERE s.user_id = u.user_id \
	AND s.sakai_site_id = ?
	ORDER BY u.user_lname, u.user_fname
	
ForumModel.selectAllUserPostsCountByTopic = SELECT s.user_id, COUNT(p.post_id) AS user_posts_count, u.user_fname AS user_fname,u.user_lname AS user_lname, u.sakai_user_id, u.username \
	FROM (SELECT user_id from jforum_site_users WHERE sakai_site_id = ?) s LEFT OUTER JOIN jforum_users u ON s.user_id = u.user_id \
	LEFT OUTER JOIN jforum_posts p ON s.user_id = p.user_id \
	AND p.forum_id = ? AND p.topic_id = ? \
	GROUP BY s.user_id, u.user_fname, u.user_lname, u.sakai_user_id, u.username
	
ForumModel.selectUserPostsCountByTopic	= SELECT p.user_id, COUNT(p.post_id) AS user_posts_count, u.user_fname AS user_fname,u.user_lname AS user_lname, u.sakai_user_id, u.username \
	FROM jforum_posts p, jforum_users u \
	WHERE p.forum_id = ? and p.topic_id = ? and p.user_id = u.user_id \
	GROUP BY p.user_id, u.user_fname, u.user_lname, u.sakai_user_id, u.username 
	#ORDER BY u.user_lname, u.user_fname
# #############
# TopicModel
# #############
#revised by rashmi
TopicModel.selectById = SELECT t.*, u.username AS posted_by_username, u.user_id AS posted_by_id, u.user_fname AS posted_by_fname,u.user_lname AS posted_by_lname, u2.username AS last_post_by_username, u2.user_id AS last_post_by_id,u2.user_fname AS last_post_by_fname,u2.user_lname AS last_post_by_lname, p2.post_time, p.attach \
	FROM jforum_topics t, jforum_users u, jforum_posts p, jforum_posts p2, jforum_users u2 \
	WHERE t.topic_id = ? \
	AND t.user_id = u.user_id \
	AND p.post_id = t.topic_first_post_id \
	AND p2.post_id = t.topic_last_post_id \
	AND u2.user_id = p2.user_id
#end
	
TopicModel.selectRaw = SELECT topic_id, forum_id, topic_title, user_id, topic_replies, topic_status, topic_vote, topic_type, \
	topic_first_post_id, topic_last_post_id, moderated, topic_time, topic_grade, topic_export \
	FROM jforum_topics WHERE topic_id = ?

# revised by rashmi 09-14-05
TopicModel.selectAllByForumByLimit = SELECT t.*, u.username AS posted_by_username, u.user_id AS posted_by_id,u.user_fname AS posted_by_fname,u.user_lname AS posted_by_lname,u2.username AS last_post_by_username, u2.user_id AS last_post_by_id, u2.user_fname AS last_post_by_fname,u2.user_lname AS last_post_by_lname,p2.post_time, p.attach \
	FROM jforum_topics t, jforum_users u, jforum_posts p, jforum_posts p2, jforum_users u2 \
	WHERE t.forum_id = ? \
	AND t.user_id = u.user_id \
	AND p.post_id = t.topic_first_post_id \
	AND p2.post_id = t.topic_last_post_id \
	AND u2.user_id = p2.user_id \
	ORDER BY t.topic_export DESC, t.topic_type DESC, p2.post_time DESC, t.topic_last_post_id DESC \
	LIMIT ?, ?
#revise end

TopicModel.selectTopicTitlesByIds SELECT topic_id, topic_title FROM jforum_topics WHERE topic_id IN (:ids:)
TopicModel.selectLastN = SELECT topic_title, topic_time, topic_id, topic_type FROM jforum_topics ORDER BY topic_time DESC LIMIT ?
TopicModel.setModerationStatus = UPDATE jforum_topics SET moderated = ? WHERE forum_id = ?
TopicModel.setModerationStatusByTopic = UPDATE jforum_topics SET moderated = ? WHERE topic_id = ?
TopicModel.deleteByForum = SELECT topic_id FROM jforum_topics where forum_id = ?

TopicModel.delete = DELETE FROM jforum_topics WHERE topic_id = ?
TopicModel.deletePosts = DELETE FROM jforum_posts WHERE topic_id = ?
TopicModel.incrementTotalReplies = UPDATE jforum_topics SET topic_replies = topic_replies + 1 WHERE topic_id = ?
TopicModel.decrementTotalReplies = UPDATE jforum_topics SET topic_replies = topic_replies - 1 WHERE topic_id = ?
TopicModel.setLastPostId = UPDATE jforum_topics SET topic_last_post_id = ? WHERE topic_id = ?
TopicModel.setFirstPostId = UPDATE jforum_topics SET topic_first_post_id = ? WHERE topic_id = ?
TopicModel.getMinPostId = SELECT MIN(post_id) AS post_id FROM jforum_posts WHERE topic_id = ?

TopicModel.addNew = INSERT INTO jforum_topics (forum_id, topic_title, user_id, topic_time, topic_first_post_id, topic_last_post_id, topic_type, moderated, topic_grade, topic_export) \
	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)

TopicModel.update = UPDATE jforum_topics SET topic_title = ?, topic_last_post_id = ?, topic_first_post_id = ?, topic_type = ?, moderated =?, topic_grade = ?, topic_export = ? WHERE topic_id = ?

TopicModel.selectIdByCourseId = SELECT t.topic_id \
    FROM jforum_topics t, jforum_forums f, jforum_sakai_course_categories c \
    WHERE t.forum_id = f.forum_id \ 
    AND f.categories_id = c.categories_id \
    AND c.course_id = ? \

TopicModel.getMaxPostId = SELECT MAX(post_id) AS post_id FROM jforum_posts WHERE topic_id = ?
TopicModel.getTotalPosts = SELECT COUNT(1) AS total FROM jforum_posts WHERE topic_id = ?

TopicModel.subscribeUser = INSERT INTO jforum_topics_watch(topic_id, user_id, is_read) VALUES (?, ?, '1')
TopicModel.isUserSubscribed = SELECT user_id FROM jforum_topics_watch WHERE topic_id = ? AND user_id = ?
TopicModel.removeSubscription = DELETE FROM jforum_topics_watch WHERE topic_id = ? AND user_id = ?
TopicModel.removeSubscriptionByTopic = DELETE FROM jforum_topics_watch WHERE topic_id = ?
TopicModel.updateReadStatus = UPDATE jforum_topics_watch SET is_read = ? WHERE topic_id = ? AND user_id = ?

TopicModel.notifyUsers = SELECT u.user_id AS user_id, u.username AS username, \
	u.user_lang AS user_lang, u.user_email AS user_email, u.sakai_user_id \
	FROM jforum_topics_watch tw \
	INNER JOIN jforum_users u ON (tw.user_id = u.user_id) \
	WHERE tw.topic_id = ? \
	AND tw.is_read = 1 \
	AND u.user_id NOT IN ( ?, ? )
	
TopicModel.markAllAsUnread = UPDATE jforum_topics_watch SET is_read = '0' WHERE topic_id = ? AND user_id NOT IN (?, ?)
TopicModel.lockUnlock = UPDATE jforum_topics SET topic_status = ? WHERE topic_id = ?
#revised by rashmi 09-19-05
#revised by Mallika 10/19/05 t.forum_id line to include course 
#revised by Mallika to pull all topics including sticky and announcement
#revised by Mallika 1/23/06 to only select forums whose start date is after current date
#or start date is null
#TopicModel.selectRecentTopicsByLimit = SELECT t.*, u.username AS posted_by_username, u.user_id AS posted_by_id,u.user_fname AS posted_by_fname,u.user_lname AS posted_by_lname, u2.username AS last_post_by_username, u2.user_id AS last_post_by_id, u2.user_fname AS last_post_by_fname,u2.user_lname AS last_post_by_lname,p2.post_time, p.attach \
#	FROM jforum_topics t, jforum_users u, jforum_posts p, jforum_posts p2, jforum_users u2 \
#	WHERE t.forum_id IN (SELECT forum_id FROM jforum_forums WHERE categories_id IN (SELECT categories_id FROM jforum_sakai_course_categories WHERE course_id = ?)) \
#	AND t.user_id = u.user_id \
#	AND p.post_id = t.topic_first_post_id \
#	AND p2.post_id = t.topic_last_post_id \
#	AND u2.user_id = p2.user_id \
#	ORDER BY p2.post_time DESC, t.topic_last_post_id DESC \
#	LIMIT 0, ?

#revised kyong 4/22/06
TopicModel.selectRecentTopicsByLimit = SELECT t.*, u.username AS posted_by_username, u.user_id AS posted_by_id,u.user_fname AS posted_by_fname,u.user_lname AS posted_by_lname, u2.username AS last_post_by_username, u2.user_id AS last_post_by_id, u2.user_fname AS last_post_by_fname,u2.user_lname AS last_post_by_lname, p2.post_time, p.attach \
	FROM jforum_topics t, jforum_users u, jforum_posts p, jforum_posts p2, jforum_users u2, jforum_forums f, jforum_sakai_course_categories c, jforum_categories jc \
	WHERE t.user_id = u.user_id \
	AND p.post_id = t.topic_first_post_id \
	AND p2.post_id = t.topic_last_post_id \
	AND u2.user_id = p2.user_id \
	AND t.forum_id = f.forum_id \
	AND c.course_id = ? \
	AND f.categories_id = c.categories_id \
	AND c.categories_id = jc.categories_id \
	AND jc.archived = 0 \
	ORDER BY p2.post_time DESC, t.topic_last_post_id DESC \
	LIMIT 0, ?
#revise end

TopicModel.getFirstLastPostId = SELECT MIN(post_id) AS first_post_id, MAX(post_id) AS last_post_id FROM jforum_posts WHERE topic_id = ?
TopicModel.fixFirstLastPostId = UPDATE jforum_topics SET topic_first_post_id = ?, topic_last_post_id = ? WHERE topic_id = ?
#11/9/06 - added by Mallika
TopicMarkModel.addMarkTime = INSERT INTO jforum_topics_mark(topic_id, user_id, mark_time, is_read) VALUES (?, ?, ?, ?)
TopicMarkModel.selectMarkTime = SELECT topic_id, user_id, mark_time, is_read FROM jforum_topics_mark WHERE topic_id = ? AND user_id = ?
TopicMarkModel.updateMarkTime = UPDATE jforum_topics_mark SET mark_time = ?, is_read = ? WHERE topic_id = ? AND user_id = ?
TopicMarkModel.selectTopicTimes = SELECT m.topic_id,m.user_id,m.mark_time, m.is_read FROM jforum_topics_mark m,jforum_topics t WHERE m.topic_id = t.topic_id AND t.forum_id = ? AND m.user_id = ?
TopicMarkModel.selectCourseTopicTimes = SELECT m.topic_id,m.user_id,m.mark_time, m.is_read FROM jforum_topics_mark m,jforum_topics t,jforum_forums f,jforum_sakai_course_categories c WHERE m.topic_id = t.topic_id AND m.user_id = ? AND t.forum_id = f.forum_id AND f.categories_id = c.categories_id and c.course_id = ?
TopicMarkModel.deleteMarkTime = DELETE FROM jforum_topics_mark WHERE topic_id = ? AND user_id = ?
#TopicMarkModel.selectMarkedTopics = SELECT * FROM jforum_topics j, jforum_topics_mark m WHERE j.forum_id = ? AND m.topic_id = j.topic_id AND m.user_id = ?  AND m.is_read = ?
TopicMarkModel.selectCountMarkedTopicsByForum = SELECT count(j.topic_id) AS topics_count FROM jforum_topics j, jforum_topics_mark m WHERE j.forum_id = ? AND j.topic_id = m.topic_id AND m.user_id = ? AND m.is_read = ? 
TopicMarkModel.selectUserUnreadMarkedTopicsByCourse = SELECT tm.topic_id, tm.user_id, tm.mark_time, tm.is_read FROM jforum_sakai_course_categories scc, jforum_forums f, jforum_topics t, jforum_topics_mark tm WHERE scc.categories_id = f.categories_id AND f.forum_id = t.forum_id AND t.topic_id = tm.topic_id AND tm.user_id = ? AND tm.is_read = ? AND scc.course_id = ?


# ############
# SearchModel
# ############
#revised by rashmi 09-19-05
#Mallika - 1/24/06 - editing all queries below to add start_date condn
SearchModel.searchBase = SELECT t.*, u.username AS posted_by_username, u.user_id AS posted_by_id,u.user_fname AS posted_by_fname,u.user_lname AS posted_by_lname, u2.username AS last_post_by_username, u2.user_id AS last_post_by_id,u2.user_fname AS last_post_by_fname,u2.user_lname AS last_post_by_lname, p2.post_time, p.attach \
	FROM jforum_search_topics t, jforum_users u, jforum_posts p, jforum_posts p2, jforum_users u2, jforum_forums f, jforum_search_results sr \
	WHERE t.user_id = u.user_id \
	AND p.post_id = t.topic_first_post_id \
	AND p2.post_id = t.topic_last_post_id \
	AND u2.user_id = p2.user_id \
	AND f.forum_id = t.forum_id \
	AND (f.start_date is NULL OR f.start_date <= curdate()) \
	AND t.topic_id = sr.topic_id \
	AND sr.session = ? \
	AND t.session = ? \
	:criterias: \
	ORDER BY :orderByField: :orderBy:
	
SearchModel.insertWords = INSERT INTO jforum_search_words ( word_hash, word ) VALUES (?, ?)

#10/10/05 - Mallika - line below to include course id reference
#SearchModel.searchByWord = SELECT post_id FROM jforum_search_wordmatch wm, jforum_search_words w \
#	WHERE wm.word_id = w.word_id \
#	AND LOWER(w.word) = LOWER(?)

SearchModel.searchByWord = SELECT wm.post_id FROM jforum_search_wordmatch wm, jforum_search_words w, \
    jforum_posts jp,jforum_forums jf,jforum_sakai_course_categories jscc \
	WHERE jscc.course_id = ? AND jscc.categories_id = jf.categories_id AND \
	jf.forum_id = jp.forum_id AND (jf.start_date is NULL OR jf.start_date <= curdate()) and jp.post_id = wm.post_id AND \
	wm.word_id = w.word_id \
	AND LOWER(w.word) = LOWER(?)
	
#10/10/05 - Mallika - line below to include course id reference
#SearchModel.searchByLikeWord = SELECT post_id FROM jforum_search_wordmatch wm, jforum_search_words w \
#	WHERE wm.word_id = w.word_id \
#	AND LOWER(w.word) LIKE LOWER(?)
	
SearchModel.searchByLikeWord = SELECT wm.post_id FROM jforum_search_wordmatch wm, jforum_search_words w, \
    jforum_posts jp,jforum_forums jf,jforum_sakai_course_categories jscc \
	WHERE jscc.course_id = ? AND jscc.categories_id = jf.categories_id AND \
	jf.forum_id = jp.forum_id AND (jf.start_date is NULL OR jf.start_date <= curdate()) and jp.post_id = wm.post_id AND \
	WHERE wm.word_id = w.word_id \
	AND LOWER(w.word) LIKE LOWER(?)
	
SearchModel.insertTopicsIds = INSERT INTO jforum_search_results ( topic_id, session, search_time ) SELECT DISTINCT t.topic_id, ?, NOW() FROM jforum_topics t, jforum_posts p \
	WHERE t.topic_id = p.topic_id \
	AND p.post_id IN (:posts:)
	
SearchModel.selectTopicData = INSERT INTO jforum_search_topics (topic_id, forum_id, topic_title, user_id, topic_time, \
	topic_status, topic_replies, topic_vote, topic_type, topic_first_post_id, topic_last_post_id, moderated, topic_grade, topic_export, session, search_time) \
	SELECT t.topic_id, t.forum_id, t.topic_title, t.user_id, t.topic_time, \
	t.topic_status, t.topic_replies, t.topic_vote, t.topic_type, t.topic_first_post_id, t.topic_last_post_id, t.moderated, t.topic_grade, t.topic_export, ?, NOW() \
	FROM jforum_topics t, jforum_search_results s \
	WHERE t.topic_id = s.topic_id \
	AND s.session = ?
	
SearchModel.cleanSearchResults = DELETE FROM jforum_search_results WHERE session = ? OR search_time < DATE_SUB(NOW(), INTERVAL 1 HOUR)
SearchModel.cleanSearchTopics = DELETE FROM jforum_search_topics WHERE session = ? OR search_time < DATE_SUB(NOW(), INTERVAL 1 HOUR)
	
SearchModel.searchByTime = INSERT INTO jforum_search_results (topic_id, session, search_time) SELECT DISTINCT t.topic_id, ?, NOW() FROM jforum_topics t, jforum_posts p \
	WHERE t.topic_id = p.topic_id \
	AND p.post_time > ?

SearchModel.associateWordToPost = INSERT INTO jforum_search_wordmatch (post_id, word_id, title_match) VALUES (?, ?, ?)
SearchModel.searchExistingWord = SELECT w.word_id FROM jforum_search_words w WHERE w.word_hash = ?
SearchModel.searchExistingAssociation = SELECT post_id FROM jforum_search_wordmatch WHERE word_id = ? AND post_id = ?
SearchModel.maxPostIdUntilNow = SELECT MAX(post_id) FROM jforum_posts WHERE post_time < ?
SearchModel.lastIndexedPostId = SELECT MAX(post_id) FROM jforum_search_wordmatch
SearchModel.howManyToIndex = SELECT COUNT(1) FROM jforum_posts WHERE post_time < ? AND post_id > ?

SearchModel.getPostsToIndex = SELECT p.post_id, pt.post_text, pt.post_subject \
	FROM jforum_posts p, jforum_posts_text pt \
	WHERE p.post_id = pt.post_id \
	AND p.post_id BETWEEN ? AND ? \
	LIMIT ?, ?
	
SearchModel.getIndexingStatus = SELECT status FROM jforum_search_indexing

SearchModel.addIndexingStatus = INSERT INTO jforum_search_indexing(status) VALUES(?)

SearchModel.deleteIndexingStatus = DELETE FROM jforum_search_indexing

# ##########
# TreeGroup
# ##########
#Mallika's comments beg
#TreeGroup.selectGroup = SELECT group_id, group_name FROM jforum_groups WHERE parent_id = ? ORDER BY group_name
#Mallika's comments end

#Mallika's code beg
TreeGroup.selectGroup = SELECT a.group_id, a.group_name FROM jforum_groups a, jforum_sakai_course_groups b WHERE a.group_id = b.group_id and a.parent_id = ? and b.course_id = ? ORDER BY a.group_name
#Mallika's code end


# ################
# PermissionControl
# ################
PermissionControl.deleteAllRoleValues = DELETE FROM jforum_role_values WHERE role_id IN (SELECT role_id FROM jforum_roles WHERE group_id = ?)

PermissionControl.deleteAllUserRoles = DELETE FROM jforum_roles WHERE user_id = ?
PermissionControl.deleteAllGroupRoles = DELETE FROM jforum_roles WHERE group_id = ?
PermissionControl.deleteUserRole = DELETE from jforum_roles WHERE user_id = ? AND name = ?
PermissionControl.deleteGroupRole = DELETE FROM jforum_roles WHERE group_id = ? AND name = ?
PermissionControl.addGroupRole = INSERT INTO jforum_roles ( group_id, name, role_type ) VALUES (?, ?, ?)
PermissionControl.addUserRole = INSERT INTO jforum_roles ( user_id, name, role_type ) VALUES (?, ?, ?)
PermissionControl.addRoleValues = INSERT INTO jforum_role_values (role_id, role_value, role_type ) VALUES (?, ?, ?)
PermissionControl.getRoleIdByName = SELECT role_id FROM jforum_roles WHERE name = ? AND group_id = ?


PermissionControl.loadGroupRoles = SELECT r.role_id, r.name, rv.role_value, rv.role_type AS rv_type, r.role_type \
	FROM jforum_roles r \
	LEFT JOIN jforum_role_values rv ON rv.role_id = r.role_id \
	WHERE r.group_id = ? \ 
	AND user_id = 0 \
	ORDER BY r.role_id
	
PermissionControl.loadUserRoles = SELECT r.role_id, r.name, rv.role_value, rv.role_type AS rv_type, r.role_type \
	FROM jforum_roles r \
	LEFT JOIN jforum_role_values rv ON rv.role_id = r.role_id \
	WHERE r.user_id = ? \
	AND r.group_id = 0 \
	ORDER BY r.role_id
	
PermissionControl.deleteAllUserRoleValuesByGroup = DELETE FROM jforum_roles \
	where role_id in (select r.role_id from jforum_role_values rv, jforum_users u, jforum_user_groups ug \
	WHERE u.user_id = ug.user_id \
	AND ug.group_id = ? \
	AND r.user_id = u.user_id \
	AND r.role_name = ? )

PermissionControl.deleteUserRoleByGroup = DELETE FROM jforum_roles \
	where user_id in (select user_id from jforum_user_groups ug where  ug.group_id = ?) \
	and name = ? \
	
PermissionControl.deleteUserRoleValuesByRoleName = DELETE FROM jforum_role_values \
	where role_id in (select r.role_id from jforum_roles r, jforum_user_groups ug \
	WHERE ug.user_id = r.user_id \
	AND ug.group_id = ? \
	AND r.name = ? )

PermissionControl.deleteUserRoleValueByGroup = DELETE FROM jforum_role_values\
	where role_id in (select r.role_id from jforum_roles r, jforum_user_groups ug \
	WHERE ug.user_id = r.user_id \
	AND ug.group_id = ? \
	AND r.name = ? ) \
	AND rv.value = ?

# #############
# TopicListing
# #############
TopicListing.selectTopicData = SELECT topic_id, topic_title, topic_replies, topic_last_post_id, user_id FROM jforum_topics WHERE forum_id = ?

# #############
# SmiliesModel
# #############
SmiliesModel.addNew = INSERT INTO jforum_smilies ( code, url, disk_name) VALUES (?, ?, ?)
SmiliesModel.delete = DELETE FROM jforum_smilies WHERE smilie_id = ?
SmiliesModel.update = UPDATE jforum_smilies SET code = ?, url = ?, disk_name =? WHERE smilie_id = ?
SmiliesModel.selectAll = SELECT * FROM jforum_smilies ORDER BY smilie_id
SmiliesModel.selectById = SELECT * FROM jforum_smilies WHERE smilie_id = ?

# ####################
# PrivateMessageModel
# ####################
PrivateMessageModel.add = INSERT INTO jforum_privmsgs ( privmsgs_type, privmsgs_subject, privmsgs_from_userid, \
	privmsgs_to_userid, privmsgs_date, privmsgs_enable_bbcode, privmsgs_enable_html, privmsgs_enable_smilies, \
	privmsgs_attach_sig, privmsgs_priority ) \
	VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )
	
PrivateMessagesModel.addText = INSERT INTO jforum_privmsgs_text ( privmsgs_id, privmsgs_text ) VALUES (?, ?)

PrivateMessagesModel.addAttachmentInfo = INSERT INTO jforum_privmsgs_attach ( attach_id, privmsgs_id ) VALUES (?, ?)
	
PrivateMessageModel.delete = DELETE FROM jforum_privmsgs WHERE privmsgs_id = ? \
	AND ( \
	    (privmsgs_from_userid = ? AND privmsgs_type = 2) \
	    OR (privmsgs_to_userid = ? AND privmsgs_type IN(0, 1, 5)) \
	)
	
PrivateMessagesModel.deleteText = DELETE FROM jforum_privmsgs_text WHERE privmsgs_id = ?

PrivateMessagesModel.deleteAttachmentInfo = DELETE FROM jforum_privmsgs_attach WHERE privmsgs_id = ?
	
#Mallika's new code beg	
#PrivateMessageModel.baseListing = SELECT pm.privmsgs_type, pm.privmsgs_id, pm.privmsgs_date, pm.privmsgs_subject, u.user_id, u.username, u.user_fname, u.user_lname \
#	FROM jforum_privmsgs pm, jforum_users u \
#	#FILTER# \
#	ORDER BY pm.privmsgs_date DESC	
#Mallika's new code end	

	
#Mallika's new code beg	
#PrivateMessageModel.inbox = WHERE privmsgs_to_userid = ? \
#	AND u.user_id = pm.privmsgs_from_userid \
#	AND ( pm.privmsgs_type = 1 \
#	OR pm.privmsgs_type = 0 \
#	OR privmsgs_type = 5) \
#	AND privmsgs_id IN (SELECT distinct privmsgs_id FROM jforum_sakai_course_privmsgs where course_id = ?)
	
#PrivateMessageModel.sent = WHERE privmsgs_from_userid = ? \
#	AND u.user_id = pm.privmsgs_to_userid \
#	AND pm.privmsgs_type = 2 AND privmsgs_id IN (SELECT distinct privmsgs_id FROM jforum_sakai_course_privmsgs where course_id = ?)

#PrivateMessageModel.selectReadCount = SELECT count(*) AS total FROM \
#jforum_privmsgs where privmsgs_to_userid = ? AND privmsgs_type = ? AND privmsgs_id IN \
#(SELECT distinct privmsgs_id FROM jforum_sakai_course_privmsgs where course_id = ?)	
#Mallika's new code end

# revised by kkim 5/4/06
PrivateMessageModel.selectReadCount = SELECT count(*) AS total \
	FROM jforum_privmsgs p, jforum_sakai_course_privmsgs cp \
	WHERE p.privmsgs_to_userid = ? \
	AND p.privmsgs_type = ? \
	AND p.privmsgs_id = cp.privmsgs_id \
	AND cp.course_id = ?
# revision end kkim


# revised by kkim 4/22/06
PrivateMessageModel.baseListing = SELECT pm.privmsgs_type, pm.privmsgs_id, pm.privmsgs_date, pm.privmsgs_subject, pm.privmsgs_flag_to_follow, pm.privmsgs_replied, pm.privmsgs_priority ,u.user_id, u.username, u.user_fname, u.user_lname \
	FROM jforum_privmsgs pm, jforum_users u, jforum_sakai_course_privmsgs cp \
	#FILTER# \
	ORDER BY pm.privmsgs_date DESC
 
PrivateMessageModel.inbox = WHERE pm.privmsgs_to_userid = ? \
	AND u.user_id = pm.privmsgs_from_userid \
	AND ( pm.privmsgs_type = 1 \
	OR pm.privmsgs_type = 0 \
	OR privmsgs_type = 5) \
	AND pm.privmsgs_id = cp.privmsgs_id AND cp.course_id = ?

PrivateMessageModel.sent = WHERE pm.privmsgs_from_userid = ? \
	AND u.user_id = pm.privmsgs_to_userid \
	AND pm.privmsgs_type = 2 \
	AND pm.privmsgs_id = cp.privmsgs_id \
	AND cp.course_id = ?
#end revise

PrivateMessageModel.updateType = UPDATE jforum_privmsgs SET privmsgs_type = ? WHERE privmsgs_id = ?

PrivateMessageModel.updateFlagToFollowup = UPDATE jforum_privmsgs SET privmsgs_flag_to_follow = ? WHERE privmsgs_id = ?

PrivateMessageModel.updateRepliedStatus = UPDATE jforum_privmsgs SET privmsgs_replied = ? WHERE privmsgs_id = ?

PrivateMessageModel.selectById = SELECT p.*, pt.privmsgs_text \
	FROM jforum_privmsgs p, jforum_privmsgs_text pt \
	WHERE p.privmsgs_id = pt.privmsgs_id \
	AND p.privmsgs_id = ?


# #################
# UserSessionModel
# #################
UserSessionModel.add = INSERT INTO jforum_sessions ( session_id, session_user_id, session_start ) VALUES (?, ?, ?)
UserSessionModel.update = UPDATE jforum_sessions SET session_start = ?, session_time = ?, session_id = ? WHERE session_user_id = ?
UserSessionModel.delete = DELETE FROM jforum_sessions WHERE session_user_id = ?
UserSessionModel.selectById = SELECT session_time, session_start, session_id FROM jforum_sessions WHERE session_user_id = ?

# ###########
# KarmaModel
# ###########
KarmaModel.add = INSERT INTO jforum_karma (post_id, post_user_id, from_user_id, points, topic_id, rate_date) VALUES (?, ?, ?, ?, ?, ?)
KarmaModel.update = UPDATE jforum_karma SET points = ? WHERE karma_id = ?
KarmaModel.getUserKarma = SELECT user_karma FROM jforum_users WHERE user_id = ?
KarmaModel.updateUserKarma = UPDATE jforum_users SET user_karma = ? WHERE user_id = ?
KarmaModel.getPostKarma = SELECT SUM(points) / COUNT(post_id) AS points FROM jforum_karma WHERE post_id = ?
KarmaModel.userCanAddKarma = SELECT COUNT(1) FROM jforum_karma WHERE post_id = ? AND from_user_id = ?

KarmaModel.getUserKarmaPoints = SELECT SUM(points) AS points, COUNT(1) AS votes, from_user_id \
	FROM jforum_karma WHERE post_user_id = ? GROUP BY from_user_id
KarmaModel.getUserVotes = SELECT points, post_id FROM jforum_karma WHERE topic_id = ? AND from_user_id = ?	

#Frankiln Tests
KarmaModel.getUserGivenVotes = SELECT COUNT(post_id) AS votes FROM jforum_karma WHERE from_user_id = ?
KarmaModel.getUserTotalVotes = SELECT SUM(points) AS points, COUNT(post_id) AS votes FROM jforum_karma WHERE post_user_id = ?

KarmaModel.getMostRatedUserByPeriod = SELECT u.user_id, u.username, SUM(points) AS total, \
	  COUNT(post_user_id) AS votes_received, user_karma, \
	  -1 AS given \
	  FROM jforum_users u, jforum_karma k \
	  WHERE u.user_id = k.post_user_id \
	  AND k.rate_date BETWEEN ? AND ? \
	  GROUP BY u.user_id, u.username, user_karma

# ##############
# BookmarkModel
# ##############
BookmarkModel.add = INSERT INTO jforum_bookmarks (user_id, relation_id, relation_type, public_visible, title, description) VALUES (?, ?, ?, ?, ?, ?)
BookmarkModel.update = UPDATE jforum_bookmarks SET public_visible = ?, title = ?, description = ? WHERE bookmark_id = ?
BookmarkModel.remove = DELETE FROM jforum_bookmarks WHERE bookmark_id = ?

#Mallika - 1/24/06 - adding date condition
BookmarkModel.selectForumBookmarks = SELECT b.bookmark_id, b.user_id, b.relation_type, b.relation_id, b.public_visible, b.title, b.description, f.forum_name, f.forum_desc \
	FROM jforum_bookmarks b, jforum_forums f \
	WHERE b.relation_type = 1 \
	AND b.relation_id = f.forum_id \
	AND b.user_id = ? \
	AND (f.start_date is NULL OR f.start_date <= curdate()) \
	ORDER BY f.forum_name
	
BookmarkModel.selectTopicBookmarks = SELECT b.bookmark_id, b.user_id, b.relation_type, b.relation_id, b.public_visible, b.title, b.description, t.topic_title \
	FROM jforum_bookmarks b, jforum_topics t \
	WHERE b.relation_type = 2 \
	AND b.relation_id = t.topic_id \
	AND b.user_id = ? \
	ORDER BY t.topic_title
	
BookmarkModel.selectUserBookmarks = SELECT b.bookmark_id, b.user_id, b.relation_type, b.relation_id, b.public_visible, b.title, b.description, u.username \
	FROM jforum_bookmarks b, jforum_users u \
	WHERE b.relation_type = 3 \
	AND b.relation_id = u.user_id \
	AND b.user_id = ? \
	ORDER BY u.username
	
#Mallika -commenting line below and changing it to include ref to course id	
#BookmarkModel.selectAllFromUser = SELECT b.bookmark_id, b.user_id, b.relation_type, b.relation_id, b.public_visible, b.title, b.description \
#	FROM jforum_bookmarks b \
#	WHERE b.user_id = ? \
#	ORDER BY b.title

#Mallika - 1/23/06 - changing query to pull up forums by start date
BookmarkModel.selectAllFromUser = SELECT b.bookmark_id, b.user_id, b.relation_type, b.relation_id, b.public_visible, b.title, b.description \
	FROM jforum_bookmarks b \
	WHERE b.user_id = ?  and b.relation_type != 2 \
UNION \
    SELECT b.bookmark_id, b.user_id, b.relation_type, b.relation_id, b.public_visible, b.title, b.description \
	FROM jforum_bookmarks b \
	WHERE b.user_id = ?  and b.relation_type = 2 \
	and b.relation_id in (select topic_id from jforum_topics where forum_id in (select forum_id from jforum_forums where (start_date is NULL OR start_date <= curdate()) AND categories_id in (select categories_id from jforum_sakai_course_categories where course_id = ?)))\
	ORDER BY title 	
	
BookmarkModel.selectForUpdate = SELECT bookmark_id, relation_id, public_visible, relation_type, title, description, user_id \
	FROM jforum_bookmarks WHERE relation_id = ? AND relation_type = ? AND user_id = ?
	
BookmarkModel.selectById = SELECT bookmark_id, relation_id, public_visible, title, description, user_id, relation_type \
	FROM jforum_bookmarks WHERE bookmark_id = ?
	
# ################
# AttachmentModel
# ################
AttachmentModel.addQuotaLimit = INSERT INTO jforum_quota_limit (quota_desc, quota_limit, quota_type) VALUES (?, ?, ?)
AttachmentModel.updateQuotaLimit = UPDATE jforum_quota_limit SET quota_desc = ?, quota_limit = ?, quota_type = ? WHERE quota_limit_id = ?
AttachmentModel.removeQuotaLimit = DELETE FROM jforum_quota_limit WHERE quota_limit_id = ?

AttachmentModel.selectQuotaLimit = SELECT quota_limit_id, quota_desc, quota_limit, quota_type \
	FROM jforum_quota_limit ORDER BY quota_type, quota_limit
	
AttachmentModel.addExtensionGroup = INSERT INTO jforum_extension_groups (name, allow, upload_icon, download_mode) VALUES (?, ?, ?, ?)

AttachmentModel.updateExtensionGroups = UPDATE jforum_extension_groups SET name = ?, allow = ?, upload_icon = ?, download_mode = ? \
	WHERE extension_group_id = ?

AttachmentModel.removeExtensionGroups = DELETE FROM jforum_extension_groups WHERE extension_group_id = ?
AttachmentModel.selectExtensionGroups = SELECT extension_group_id, name, allow, upload_icon, download_mode FROM jforum_extension_groups ORDER BY name

AttachmentModel.addExtension = INSERT INTO jforum_extensions (extension_group_id, description, upload_icon, extension, allow) VALUES (?, ?, ?, ?, ?)

AttachmentModel.updateExtension = UPDATE jforum_extensions SET extension_group_id = ?, description = ?, upload_icon = ?, extension = ?, allow = ? \
	WHERE extension_id = ?

AttachmentModel.removeExtension = DELETE FROM jforum_extensions WHERE extension_id = ?
AttachmentModel.addAttachment = INSERT INTO jforum_attach (post_id, privmsgs_id, user_id) VALUES (?, ?, ?)

AttachmentModel.addAttachmentInfo = INSERT INTO jforum_attach_desc (attach_id, physical_filename, real_filename, description, \
	mimetype, filesize, upload_time, thumb, extension_id ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
	
AttachmentModel.updatePost = UPDATE jforum_posts SET attach = ? WHERE post_id = ?

AttachmentModel.updatePM = UPDATE jforum_privmsgs SET privmsgs_attachments = ? WHERE privmsgs_id = ?

AttachmentModel.selectExtensions = SELECT extension_id, extension_group_id, extension, description, upload_icon, allow, '' AS group_icon FROM jforum_extensions ORDER BY extension

AttachmentModel.selectExtension = SELECT e.extension_id, e.extension_group_id, e.extension, e.description, e.upload_icon, e.allow, g.upload_icon AS group_icon \
	FROM jforum_extensions e, jforum_extension_groups g \
	WHERE e.$field = ? \
	AND e.extension_group_id = g.extension_group_id

AttachmentModel.extensionsForSecurity = SELECT e.extension, e.allow, eg.allow AS group_allow \
	FROM jforum_extensions e, jforum_extension_groups eg \
	WHERE e.extension_group_id = eg.extension_group_id

AttachmentModel.isPhysicalDownloadMode = SELECT download_mode FROM jforum_extension_groups WHERE extension_group_id = ?

AttachmentModel.selectAttachments = SELECT a.attach_id, a.user_id, a.post_id, a.privmsgs_id, d.mimetype, d.physical_filename, d.real_filename, \
	d.download_count, d.description, d.filesize, d.upload_time, d.extension_id \
	FROM jforum_attach a, jforum_attach_desc d \
	WHERE a.post_id = ? \
	AND a.attach_id = d.attach_id
	
AttachmentModel.selectPMAttachments = SELECT a.attach_id, a.user_id, a.post_id, a.privmsgs_id, d.mimetype, d.physical_filename, d.real_filename, \
	d.download_count, d.description, d.filesize, d.upload_time, d.extension_id \
	FROM jforum_attach a, jforum_attach_desc d, jforum_privmsgs_attach p \
	WHERE a.privmsgs_id = 0 AND a.post_id = 0 \
	AND a.attach_id = d.attach_id \
	AND a.attach_id = p.attach_id \
	AND p.privmsgs_id = ?

AttachmentModel.selectAttachmentPrivatMessagesById = SELECT count(1) AS total FROM jforum_privmsgs_attach p \
	WHERE p.attach_id = ?	
	
AttachmentModel.selectAttachmentById = SELECT a.attach_id, a.user_id, a.post_id, a.privmsgs_id, d.mimetype, d.physical_filename, d.real_filename, \
	d.download_count, d.description, d.filesize, d.upload_time, d.extension_id \
	FROM jforum_attach a, jforum_attach_desc d \
	WHERE a.attach_id = ? \
	AND a.attach_id = d.attach_id
	
AttachmentModel.updateAttachment = UPDATE jforum_attach_desc SET description = ?, download_count = ? WHERE attach_id = ?
AttachmentModel.removeAttachment = DELETE FROM jforum_attach WHERE attach_id = ?
AttachmentModel.removeAttachmentInfo = DELETE FROM jforum_attach_desc WHERE attach_id = ?
AttachmentModel.countPostAttachments = SELECT COUNT(1) FROM jforum_attach WHERE post_id = ?
AttachmentModel.deleteGroupQuota = DELETE FROM jforum_attach_quota
AttachmentModel.setGroupQuota = INSERT INTO jforum_attach_quota (group_id, quota_limit_id) VALUES (?, ?)
AttachmentModel.selectGroupsQuotaLimits = SELECT group_id, quota_limit_id FROM jforum_attach_quota

AttachmentModel.selectQuotaLimitByGroup = SELECT ql.quota_limit_id, ql.quota_desc, ql.quota_limit, ql.quota_type \
	FROM jforum_quota_limit ql, jforum_attach_quota at \
	WHERE ql.quota_limit_id = at.quota_limit_id \
	AND at.group_id = ?
	
# ################
# ModerationModel
# ################
ModerationModel.aprovePost = UPDATE jforum_posts SET need_moderate = 0, post_time = ? WHERE post_id = ?
ModerationModel.categoryPendingModeration = SELECT c.categories_id, c.title, f.forum_id, f.forum_name, COUNT(p.post_id) AS total \
	FROM jforum_categories c, jforum_forums f, jforum_posts p \
	WHERE p.need_moderate = 1 \
	AND p.forum_id = f.forum_id \
	AND f.categories_id = c.categories_id \
	GROUP BY c.categories_id, c.title, f.forum_id, f.forum_name

# revised by rashmi
ModerationModel.topicsByForum = SELECT p.post_id, t.topic_id, t.topic_title, t.topic_replies, p.user_id, enable_bbcode, p.attach, \
	enable_html, enable_smilies, pt.post_subject, pt.post_text, username, u.user_fname, u.user_lname \
	FROM jforum_posts p, jforum_posts_text pt, jforum_users u, jforum_topics t \
	WHERE p.post_id = pt.post_id \
	AND p.topic_id = t.topic_id \
	AND t.forum_id = ? \
	AND p.user_id = u.user_id \
	AND p.need_moderate = 1 \
	ORDER BY t.topic_id, post_time ASC 

# #############
# BannerDAO
# #############
BannerDAO.selectById = SELECT banner_id, name, banner_placement, banner_description, banner_clicks, banner_views, \
	banner_url, banner_weight, banner_active, banner_comment, banner_type, banner_width, banner_height \
	FROM jforum_banner \
	WHERE banner_id = ?
	
BannerDAO.selectAll = SELECT banner_id, banner_name, banner_placement, banner_description, banner_clicks, banner_views, \
	banner_url, banner_weight, banner_active, banner_comment, banner_type, banner_width, banner_height \
	FROM jforum_banner \
	ORDER BY comment
	
BannerDAO.canDelete = SELECT COUNT(1) AS total FROM jforum_banner WHERE banner_id = ?
BannerDAO.delete = DELETE FROM jforum_banner WHERE banner_id = ?

BannerDAO.update = UPDATE jforum_banner SET banner_name = ?, banner_placement = ?, banner_description = ?, banner_clicks = ?, \
	banner_views = ?, banner_url = ?, banner_weight = ?, banner_active = ?, banner_comment = ?, banner_type = ?, \
	banner_width = ?, banner_height = ? \
	WHERE banner_id = ?

BannerDAO.addNew = INSERT INTO jforum_banner (banner_name, banner_placement, banner_description, banner_clicks, banner_views, banner_url, banner_weight, \
	banner_active, banner_comment, banner_type, banner_width, banner_height) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)

BannerDAO.selectActiveBannerByPlacement = SELECT banner_id, banner_name, banner_placement, banner_description, banner_clicks, \
	banner_views, banner_url, \
	banner_weight, banner_active, banner_comment, banner_type, banner_width, banner_height \
	FROM jforum_banner \
	WHERE banner_placement = ? \
	AND banner_active = 1 \
	ORDER BY banner_weight ASC

#Mallika changes beg	
# #############
# CourseCategoryModel
# #############
CourseCategoryModel.selectAll = SELECT categories_id, course_id FROM jforum_sakai_course_categories
CourseCategoryModel.delete = DELETE FROM jforum_sakai_course_categories WHERE categories_id = ?
CourseCategoryModel.addNew = INSERT INTO jforum_sakai_course_categories (course_id,categories_id) VALUES (?, ?)

# #############
# CourseGroupModel
# #############
CourseGroupModel.selectAll = SELECT course_id, group_id FROM jforum_sakai_course_groups where course_id = ?
CourseGroupModel.selectByGroupId = SELECT course_id, group_id FROM jforum_sakai_course_groups where course_id = ? and group_id = ?
CourseGroupModel.selectByGroupName = SELECT a.course_id,a.group_id FROM jforum_sakai_course_groups a,jforum_groups b where a.course_id = ? and a.group_id = b.group_id and b.group_name = ?
CourseGroupModel.addNew = INSERT INTO jforum_sakai_course_groups (course_id,group_id) VALUES (?, ?)
CourseGroupModel.delete = DELETE FROM jforum_sakai_course_groups WHERE group_id = ?

# ############
# CourseInitObjModel
# ############
CourseInitObjModel.selectByCourseId = SELECT course_id, init_status FROM jforum_sakai_course_initvalues where course_id = ?
CourseInitObjModel.addNew = INSERT INTO jforum_sakai_course_initvalues (course_id,init_status) VALUES (?, ?)

# #############
# CoursePrivateMessageModel
# #############
CoursePrivateMessageModel.selectAll = SELECT course_id, privmsgs_id FROM jforum_sakai_course_privmsgs where course_id = ?
CoursePrivateMessageModel.addNew = INSERT INTO jforum_sakai_course_privmsgs (course_id,privmsgs_id) VALUES (?, ?)
CoursePrivateMessageModel.delete = DELETE FROM jforum_sakai_course_privmsgs WHERE privmsgs_id = ?

# #############
# CourseTimeModel
# #############
CourseTimeModel.selectTime = SELECT visit_time FROM jforum_sakai_sessions where course_id = ? and user_id = ?
CourseTimeModel.addNew = INSERT INTO jforum_sakai_sessions (course_id,user_id,visit_time) VALUES (?, ?, ?)
CourseTimeModel.update = UPDATE jforum_sakai_sessions SET visit_time = ? WHERE course_id = ? AND user_id = ?
#Mallika - adding queries to get mark time
CourseTimeModel.selectMarkAllTime = SELECT markall_time FROM jforum_sakai_sessions where course_id = ? and user_id = ?
CourseTimeModel.addMarkAllNew = INSERT INTO jforum_sakai_sessions (course_id,user_id,markall_time) VALUES (?, ?, ?)
CourseTimeModel.updateMarkAllTime = UPDATE jforum_sakai_sessions SET markall_time = ? WHERE course_id = ? AND user_id = ?

#Mallika's changes end


# I think this method for finding newly inserted records is dangerous, and is going to cause problems.  Maybe not on mysql, but certainly on oracle.
# Autogenerated keys are nice, but they are not universally supported, so the code needs to be different for DBs that support it and those that don't

UserModel.lastGeneratedUserId = SELECT MAX(user_id) from jforum_users 
PostModel.lastGeneratedPostId = SELECT MAX(post_id) from jforum_posts
ForumModel.lastGeneratedForumId = SELECT MAX(forum_id) from jforum_forums
TopicModel.lastGeneratedTopicId = SELECT MAX(topic_id) FROM jforum_topics
PrivateMessagesModel.lastGeneratedPmId = SELECT MAX(privmsgs_id) FROM jforum_privmsgs
SearchModel.lastGeneratedWordId = SELECT MAX(word_id) FROM jforum_search_words
SmiliesModel.lastGeneratedSmilieId = SELECT MAX(smilie_id) FROM jforum_smilies
PermissionControl.lastGeneratedRoleId = SELECT MAX(role_id) FROM jforum_roles
CategoryModel.lastGeneratedCategoryId = SELECT MAX(categories_id) FROM jforum_categories
AttachmentModel.lastGeneratedAttachmentId = SELECT MAX(attach_id) FROM jforum_attach

# ############
# CourseImportModel
# ############
CourseImportModel.selectByCourseId = SELECT sakai_site_id, imported FROM jforum_import where sakai_site_id = ?
CourseImportModel.addNew = INSERT INTO jforum_import (sakai_site_id, imported) VALUES (?, ?)
CourseImportModel.update = UPDATE jforum_import set imported = ? where sakai_site_id = ?


# ############
# Grade Model
# ############
GradeModel.addNew = INSERT INTO jforum_grade(context, grade_type, forum_id, topic_id, categories_id, points, add_to_gradebook) VALUES (?, ?, ?, ?, ?, ?, ?)
GradeModel.lastGeneratedGradeId = SELECT MAX(grade_id) FROM jforum_grade
GradeModel.selectByForumTopicId = SELECT grade_id, context, grade_type, forum_id, topic_id, points, add_to_gradebook, categories_id FROM jforum_grade WHERE forum_id = ? and topic_id = ?
GradeModel.selectByForumTopicCategoryId = SELECT grade_id, context, grade_type, forum_id, topic_id, points, add_to_gradebook, categories_id FROM jforum_grade WHERE forum_id = ? and topic_id = ? and categories_id = ?
GradeModel.updateForumGrade = UPDATE jforum_grade SET points = ?,  grade_type = ?, add_to_gradebook = ? WHERE forum_id = ? AND topic_id = 0 
GradeModel.updateTopicGrade = UPDATE jforum_grade SET points = ?,  grade_type = ? WHERE forum_id = ? and topic_id = ?
GradeModel.updateCategoryGrade = UPDATE jforum_grade SET points = ? WHERE categories_id = ?
GradeModel.deleteById = DELETE FROM jforum_grade WHERE grade_id = ?
GradeModel.deleteByForumIdTopicId = DELETE FROM jforum_grade WHERE forum_id = ? AND topic_id = ?
GradeModel.selectForumTopicGradesByForumId = SELECT grade_id, context, grade_type, forum_id, topic_id, points, add_to_gradebook, categories_id FROM jforum_grade WHERE forum_id = ? and grade_type = ?
GradeModel.selectById = SELECT grade_id, context, grade_type, forum_id, topic_id, points, add_to_gradebook, categories_id FROM jforum_grade WHERE grade_id = ?
GradeModel.updateAddToGradeBookStatus = UPDATE jforum_grade SET add_to_gradebook = ? WHERE grade_id = ?
#GRADE_BY_TOPIC = 1, GRADE_BY_FORUM = 2
#GradeModel.selectGradableForumsByCategoryId = SELECT c.categories_id, f.forum_id FROM jforum_categories c, jforum_forums f WHERE c.categories_id = f.categories_id and c.categories_id = ? AND (f.forum_grade_type = 1 OR f.forum_grade_type = 2)
GradeModel.selectGradableForumsByCategoryId = SELECT c.categories_id, f.forum_id FROM jforum_categories c, jforum_forums f WHERE c.categories_id = f.categories_id and c.categories_id = ? AND f.forum_grade_type IN (1, 2)
# ############
# Evaluation Model
# ############
EvaluationModel.selectEvalutionsByGradeId = SELECT evaluation_id, grade_id, user_id, score, comments,  evaluated_by, evaluated_date, released  FROM jforum_evaluations WHERE grade_id = ?
EvaluationModel.addNew = INSERT INTO jforum_evaluations(grade_id, user_id, sakai_user_id, score, comments, evaluated_by, evaluated_date, released) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
EvaluationModel.lastGeneratedEvalId = SELECT MAX(evaluation_id) FROM jforum_evaluations
EvaluationModel.update = UPDATE jforum_evaluations SET score = ?, comments = ?, evaluated_by = ?, evaluated_date = ?, released = ? WHERE evaluation_id = ?
EvaluationModel.selectEvalutionByGradeIdUserId = SELECT evaluation_id, grade_id, user_id, sakai_user_id, score, comments,  evaluated_by, evaluated_date, released  FROM jforum_evaluations WHERE grade_id = ? AND user_id = ?
EvaluationModel.selectEvalutionsCountByGradeId = SELECT COUNT(1) AS eval_count FROM jforum_evaluations WHERE grade_id = ?
EvaluationModel.deleteById = DELETE FROM jforum_evaluations WHERE evaluation_id = ?
#EvaluationModel.selectEvalutionsCountByForumId = SELECT COUNT(1) AS eval_count FROM jforum_evaluations WHERE grade_id IN (SELECT grade_id FROM jforum_grade WHERE forum_id = ?)
#EvaluationModel.selectEvalutionsCountByForumId = SELECT evaluation_id FROM jforum_evaluations je, jforum_grade jg, jforum_forums jf WHERE je.grade_id = jg.grade_id AND jg.forum_id = jf.forum_id AND jg.forum_id = ?
EvaluationModel.selectEvalutionsCountByForumId = SELECT COUNT(je.evaluation_id) AS eval_count FROM jforum_evaluations je, jforum_grade jg, jforum_forums jf WHERE je.grade_id = jg.grade_id AND jg.forum_id = jf.forum_id AND jg.forum_id = ?
# ############
# Special Access
# ############
SpecialAccessModel.addNew = INSERT INTO jforum_special_access(forum_id, start_date, end_date, override_start_date, override_end_date, lock_end_date, users) VALUES (?, ?, ?, ?, ?, ?, ?)
SpecialAccessModel.lastGeneratedSpecilaAccessId = SELECT MAX(special_access_id) FROM jforum_special_access
SpecialAccessModel.selectSpecialAccessByForumId = SELECT special_access_id, forum_id, start_date, end_date, override_start_date, override_end_date, password, lock_end_date, users FROM jforum_special_access WHERE forum_id = ?
SpecialAccessModel.deleteById = DELETE FROM jforum_special_access WHERE special_access_id = ?
SpecialAccessModel.selectById = SELECT special_access_id, forum_id, start_date, end_date, override_start_date, override_end_date, password, lock_end_date, users FROM jforum_special_access WHERE special_access_id = ?
SpecialAccessModel.update = UPDATE jforum_special_access SET forum_id = ?, start_date = ?, end_date = ?, lock_end_date = ?, override_start_date = ?, override_end_date = ?, users = ? WHERE special_access_id = ?
SpecialAccessModel.selectForumSpecialAccessCountByForumId = SELECT COUNT(special_access_id) As special_access_count FROM jforum_special_access WHERE forum_id = ?
SpecialAccessModel.selectSpecialAccessByCourseId = SELECT  sa.special_access_id, sa.forum_id, sa.start_date, sa.end_date, sa.lock_end_date, sa.override_start_date, sa.override_end_date, sa.password, sa.users FROM jforum_special_access sa, jforum_sakai_course_categories cc, jforum_forums f WHERE cc.course_id = ? AND cc.categories_id = f.categories_id AND f.forum_id = sa.forum_id ORDER BY sa.forum_id