################################################################################## 
# $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/WEB-INF/config/database/sqlserver/sqlserver.sql $ 
# $Id: sqlserver.sql 55571 2008-12-02 19:26:02Z murthy@etudes.org $ 
################################################################################### 
# 
# Copyright (c) 2008 Etudes, Inc. 
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
# #############
# GenericModel
# #############

GenericModel.selectByLimit = SELECT TOP



# #############
# UserModel
# #############

UserModel.selectById = SELECT COUNT(pm.privmsgs_to_userid) AS private_messages, u.user_id, u.user_active, u.username, u.user_password, u.user_session_time, \
								u.user_session_page, u.user_lastvisit, u.user_regdate, u.user_level, u.user_posts, u.user_timezone, u.user_style, \
								u.user_lang, u.user_dateformat, u.user_new_privmsg, u.user_unread_privmsg, u.user_last_privmsg, u.user_emailtime, \
								u.user_viewemail, u.user_attachsig, u.user_allowhtml, u.user_allowbbcode, u.user_allowsmilies, u.user_allowavatar, \
								u.user_allow_pm, u.user_allow_viewonline, u.user_notify, u.user_notify_pm, u.user_popup_pm, u.rank_id, u.user_avatar, \
								u.user_avatar_type, u.user_email, u.user_icq, u.user_website, u.user_from, CAST(u.user_sig as varchar) as user_sig , u.user_sig_bbcode_uid, \
								u.user_aim, u.user_yim, u.user_msnm, u.user_occ, u.user_interests, u.user_actkey, u.gender, u.themes_id, u.deleted, \
								u.user_viewonline, u.security_hash, u.user_karma \
								FROM jforum_users u \
								LEFT JOIN jforum_privmsgs pm ON pm.privmsgs_type = 1 AND pm.privmsgs_to_userid = u.user_id \
								WHERE u.user_id = ? \
								GROUP BY pm.privmsgs_to_userid, u.user_id, u.user_active, u.username, u.user_password, u.user_session_time, \
								u.user_session_page, u.user_lastvisit, u.user_regdate, u.user_level, u.user_posts, u.user_timezone, u.user_style, \
								u.user_lang, u.user_dateformat, u.user_new_privmsg, u.user_unread_privmsg, u.user_last_privmsg, u.user_emailtime, \
								u.user_viewemail, u.user_attachsig, u.user_allowhtml, u.user_allowbbcode, u.user_allowsmilies, u.user_allowavatar, \
								u.user_allow_pm, u.user_allow_viewonline, u.user_notify, u.user_notify_pm, u.user_popup_pm, u.rank_id, u.user_avatar, \
								u.user_avatar_type, u.user_email, u.user_icq, u.user_website, u.user_from, CAST(u.user_sig as varchar), u.user_sig_bbcode_uid, \
								u.user_aim, u.user_yim, u.user_msnm, u.user_occ, u.user_interests, u.user_actkey, u.gender, u.themes_id, u.deleted, \
								u.user_viewonline, u.security_hash, u.user_karma
								
UserModel.lastUserRegistered = SELECT TOP 1 user_id, username FROM jforum_users ORDER BY user_regdate DESC
UserModel.lastGeneratedUserId = SELECT IDENT_CURRENT('jforum_users') AS user_id
UserModel.selectAllByLimit = user_email, user_id, user_posts, user_regdate, username, deleted, user_karma, user_from, user_website, user_viewemail FROM jforum_users ORDER BY username

# #############
# GroupModel
# #############

GroupModel.lastGeneratedGroupId = SELECT IDENT_CURRENT('jforum_groups') AS group_id


# #############
# ForumModel
# #############

ForumModel.selectAll =  SELECT f.forum_id, f.categories_id, f.forum_name, f.forum_desc, f.forum_order, f.forum_topics, \
							  f.forum_last_post_id, f.moderated, COUNT(p.post_id) AS total_posts \
						FROM jforum_forums f \
						LEFT JOIN jforum_topics t \
						ON t.forum_id = f.forum_id \
						LEFT JOIN jforum_posts p \
						ON p.topic_id = t.topic_id \
						GROUP BY f.forum_id, f.categories_id, f.forum_name, f.forum_desc, f.forum_order, f.forum_topics, f.forum_last_post_id, f.moderated 

						
# #############
# CategoryModel
# #############
CategoryModel.lastGeneratedCategoryId = SELECT IDENT_CURRENT('jforum_categories') AS categories_id 




# #############
# PostModel
# #############

PostModel.lastGeneratedPostId = SELECT IDENT_CURRENT('jforum_posts') AS post_id

PostModel.addNewPost = INSERT INTO jforum_posts (topic_id, forum_id, user_id, post_time, poster_ip, enable_bbcode, enable_html, enable_smilies, enable_sig, post_edit_time, need_moderate) \
	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), ?)

PostModel.selectAllByTopicByLimit1 = p.post_id, topic_id, forum_id, p.user_id, post_time, poster_ip, enable_bbcode, \
	enable_html, enable_smilies, enable_sig, post_edit_time, post_edit_count, status, user_karma, pt.post_subject, pt.post_text, username, attach \
	FROM jforum_posts p, jforum_posts_text pt, jforum_users u \
	WHERE p.post_id = pt.post_id \
	AND topic_id = ? \
	AND p.user_id = u.user_id \
	AND p.need_moderate = 0 \
	AND p.post_id not in (  

PostModel.selectAllByTopicByLimit2 = p.post_id \
	FROM jforum_posts p, jforum_posts_text pt, jforum_users u \
	WHERE p.post_id = pt.post_id \
	AND topic_id = ? \
	AND p.user_id = u.user_id ) \
	ORDER BY post_time ASC	
	
# #############
# ForumModel
# #############

ForumModel.selectById = SELECT f.*, COUNT(p.post_id) AS total_posts \
	FROM jforum_forums f \
	LEFT JOIN jforum_topics t ON t.forum_id = f.forum_id \
	LEFT JOIN jforum_posts p ON p.topic_id = t.topic_id \
	WHERE f.forum_id = ? \
	GROUP BY f.categories_id, f.forum_id, \
	      f.forum_name, f.forum_desc, f.forum_order, \
	      f.forum_topics, f.forum_last_post_id, f.moderated


ForumModel.lastGeneratedForumId = SELECT IDENT_CURRENT('jforum_forums') AS forum_id



# #############
# TopicModel
# #############
TopicModel.selectAllByForumByLimit1 = t.*, p.attach, u.username AS posted_by_username, u.user_id AS posted_by_id, u2.username AS last_post_by_username, u2.user_id AS last_post_by_id, p2.post_time \
	FROM jforum_topics t, jforum_users u, jforum_posts p, jforum_posts p2, jforum_users u2 \
	WHERE t.forum_id = ? \
	AND t.user_id = u.user_id \
	AND p.post_id = t.topic_first_post_id \
	AND p2.post_id = t.topic_last_post_id \
	AND u2.user_id = p2.user_id \
	AND t.topic_id not in ( 
	
TopicModel.selectAllByForumByLimit2 = t.topic_id \
	FROM jforum_topics t, jforum_users u, jforum_posts p, jforum_posts p2, jforum_users u2 \
	WHERE t.forum_id = ? \
	AND t.user_id = u.user_id \
	AND p.post_id = t.topic_first_post_id \
	AND p2.post_id = t.topic_last_post_id \
	AND u2.user_id = p2.user_id)\	
	ORDER BY t.topic_type DESC, t.topic_time DESC, t.topic_last_post_id DESC	

TopicModel.selectRecentTopicsByLimit = t.*, u.username AS posted_by_username, u.user_id AS posted_by_id, u2.username AS last_post_by_username, u2.user_id AS last_post_by_id, p2.post_time, p2.attach \
	FROM jforum_topics t, jforum_users u, jforum_posts p, jforum_posts p2, jforum_users u2 \
	WHERE t.user_id = u.user_id \
	AND p.post_id = t.topic_first_post_id \
	AND p2.post_id = t.topic_last_post_id \
	AND u2.user_id = p2.user_id \
	AND t.topic_type = 0 \
	ORDER BY p2.post_time DESC, t.topic_last_post_id DESC
	
TopicModel.lastGeneratedTopicId = SELECT IDENT_CURRENT('jforum_topics') AS topic_id 

# #############
# PrivateMessagesModel
# #############
PrivateMessagesModel.lastGeneratedPmId = SELECT IDENT_CURRENT('jforum_privmsgs') AS privmsgs_id 

PrivateMessageModel.selectById = SELECT p.privmsgs_id, p.privmsgs_type, p.privmsgs_subject, p.privmsgs_from_userid, p.privmsgs_to_userid, \
									p.privmsgs_date, p.privmsgs_ip, p.privmsgs_enable_bbcode, p.privmsgs_enable_html, p.privmsgs_enable_smilies, \
									p.privmsgs_attach_sig, pt.privmsgs_text FROM jforum_privmsgs p, jforum_privmsgs_text pt \
									WHERE p.privmsgs_id = pt.privmsgs_id AND p.privmsgs_id = ?

# #############
# SearchModel
# #############
SearchModel.lastGeneratedWordId = SELECT IDENT_CURRENT('jforum_search_words') AS word_id 

SearchModel.cleanSearchResults = DELETE FROM jforum_search_results WHERE session = ? OR search_time < DATEADD(HOUR, -1, getdate())
SearchModel.cleanSearchTopics = DELETE FROM jforum_search_topics WHERE session = ? OR search_time < DATEADD(HOUR, -1, getdate())


SearchModel.insertTopicsIds = INSERT INTO jforum_search_results ( topic_id, session, search_time ) \
									SELECT DISTINCT t.topic_id, ?, GETDATE() FROM jforum_topics t, jforum_posts p \
									WHERE t.topic_id = p.topic_id \
									AND p.post_id IN (:posts:)


SearchModel.searchByTime = INSERT INTO jforum_search_results (topic_id, session, search_time) SELECT DISTINCT t.topic_id, ?, GETDATE() FROM jforum_topics t, jforum_posts p \
	WHERE t.topic_id = p.topic_id \
	AND p.post_time > ?
	
SearchModel.selectTopicData = INSERT INTO jforum_search_topics (topic_id, forum_id, topic_title, user_id, topic_time, \
	topic_views, topic_status, topic_replies, topic_vote, topic_type, topic_first_post_id, topic_last_post_id, moderated, session, search_time) \
	SELECT t.topic_id, t.forum_id, t.topic_title, t.user_id, t.topic_time, \
	t.topic_views, t.topic_status, t.topic_replies, t.topic_vote, t.topic_type, t.topic_first_post_id, t.topic_last_post_id, t.moderated, ?, GETDATE() \
	FROM jforum_topics t, jforum_search_results s \
	WHERE t.topic_id = s.topic_id \
	AND s.session = ?

# #############
# SmiliesModel
# #############

SmiliesModel.lastGeneratedSmilieId = SELECT IDENT_CURRENT('jforum_smilies') AS smilie_id 


# #############
# PermissionControl
# #############

PermissionControl.lastGeneratedRoleId = SELECT IDENT_CURRENT('jforum_roles') AS role_id 

# #############
# PublishUserModel
# #############

PublishUserModel.selectById = SELECT mmpublish_user_uuid FROM jforum_mmpublish_users WHERE jforum_user_id = ?
PublishUserModel.deleteFromId = DELETE FROM jforum_mmpublish_users WHERE jforum_user_id = ?
PublishUserModel.deleteFromUuid = DELETE FROM jforum_mmpublish_users WHERE mmpublish_user_uuid = ?
PublishUserModel.update = UPDATE jforum_mmpublish_users SET mmpublish_user_uuid = ? WHERE jforum_user_id = ?
PublishUserModel.addNew = INSERT INTO jforum_mmpublish_users (jforum_user_id, mmpublish_user_uuid) VALUES(?,?)

# #############
# KarmaModel
# #############

KarmaModel.getMostRatedUserByPeriod = u.user_id, u.username, SUM(points) AS total, \
	COUNT(post_user_id) AS votes_received, user_karma, \
	(SELECT COUNT(from_user_id) AS votes_given \
		FROM jforum_karma as k2 \
		WHERE k2.from_user_id = u.user_id) AS votes_given \
	FROM jforum_users u, jforum_karma k \
	WHERE u.user_id = k.post_user_id \
	AND k.rate_date BETWEEN ? AND ? \
	GROUP BY u.user_id, u.username, user_karma
									  
KarmaModel.getMostRaterUserByPeriod = NO

