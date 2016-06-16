################################################################################## 
# $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/WEB-INF/config/database/postgresql/postgresql.sql $ 
# $Id: postgresql.sql 55571 2008-12-02 19:26:02Z murthy@etudes.org $ 
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
# ##########
# UserModel
# ##########
UserModel.selectAllByLimit = SELECT user_email, user_id, user_posts, user_regdate, username, deleted, user_karma, user_from, user_website, user_viewemail \
	FROM jforum_users ORDER BY username LIMIT ? OFFSET ?

UserModel.lastGeneratedUserId = SELECT CURRVAL('jforum_users_seq')

UserModel.selectById = SELECT u.*, \
	(SELECT COUNT(1) FROM jforum_privmsgs pm \
	WHERE pm.privmsgs_to_userid = u.user_id \
	AND pm.privmsgs_type = 1) AS private_messages \
	FROM jforum_users u \
	WHERE u.user_id = ?
	
UserModel.selectAllByGroup = SELECT user_email, u.user_id, user_regdate, username \
	FROM jforum_users u, jforum_user_groups ug \
	WHERE u.user_id = ug.user_id \
	AND ug.group_id = ? \
	ORDER BY username \
	LIMIT ? OFFSET ?

# #############
# PostModel
# #############
PostModel.lastGeneratedPostId = SELECT CURRVAL('jforum_posts_seq')

PostModel.selectAllByTopicByLimit = SELECT p.post_id, topic_id, forum_id, p.user_id, post_time, poster_ip, enable_bbcode, p.attach, \
	enable_html, enable_smilies, enable_sig, post_edit_time, post_edit_count, status, pt.post_subject, pt.post_text, username, p.need_moderate \
	FROM jforum_posts p, jforum_posts_text pt, jforum_users u \
	WHERE p.post_id = pt.post_id \
	AND topic_id = ? \
	AND p.user_id = u.user_id \
	AND p.need_moderate = 0 \
	ORDER BY post_time ASC \
	LIMIT ? OFFSET ?
	
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

ForumModel.selectAll = SELECT f.*, COUNT(p.post_id) AS total_posts \
	FROM jforum_forums f \
	LEFT JOIN jforum_topics t ON t.forum_id = f.forum_id \
	LEFT JOIN jforum_posts p ON p.topic_id = t.topic_id \
	GROUP BY f.categories_id, f.forum_order, f.forum_id, \
	      f.forum_name, f.forum_desc, \
	      f.forum_topics, f.forum_last_post_id, f.moderated

ForumModel.lastGeneratedForumId = SELECT CURRVAL('jforum_forums_seq');

# #############
# TopicModel
# #############
TopicModel.selectAllByForumByLimit = SELECT t.*, u.username AS posted_by_username, u.user_id AS posted_by_id, u2.username AS last_post_by_username, u2.user_id AS last_post_by_id, p2.post_time, p.attach \
	FROM jforum_topics t, jforum_users u, jforum_posts p, jforum_posts p2, jforum_users u2 \
	WHERE t.forum_id = ? \
	AND t.user_id = u.user_id \
	AND p.post_id = t.topic_first_post_id \
	AND p2.post_id = t.topic_last_post_id \
	AND u2.user_id = p2.user_id \
	ORDER BY t.topic_type DESC, p2.post_time DESC, t.topic_last_post_id DESC \
	LIMIT ? OFFSET ?
	
TopicModel.selectRecentTopicsByLimit = SELECT t.*, u.username AS posted_by_username, u.user_id AS posted_by_id, u2.username AS last_post_by_username, u2.user_id AS last_post_by_id, p2.post_time, p.attach \
	FROM jforum_topics t, jforum_users u, jforum_posts p, jforum_posts p2, jforum_users u2 \
	WHERE t.user_id = u.user_id \
	AND p.post_id = t.topic_first_post_id \
	AND p2.post_id = t.topic_last_post_id \
	AND u2.user_id = p2.user_id \
	AND t.topic_type = 0 \
	ORDER BY p2.post_time DESC, t.topic_last_post_id DESC \
	LIMIT ?
	
TopicModel.lastGeneratedTopicId = SELECT CURRVAL('jforum_topics_seq')

# #####################
# PrivateMessagesModel
# #####################
PrivateMessagesModel.lastGeneratedPmId = SELECT CURRVAL('jforum_privmsgs_seq')

# ############
# SearchModel
# ############
SearchModel.lastGeneratedWordId = SELECT CURRVAL('jforum_search_words_seq')

SearchModel.cleanSearchResults = DELETE FROM jforum_search_results WHERE session = ? OR search_time < (NOW() - INTERVAL '1 HOUR')
SearchModel.cleanSearchTopics = DELETE FROM jforum_search_topics WHERE session = ? OR search_time < (NOW() - INTERVAL '1 HOUR')

SearchModel.insertTopicsIds = INSERT INTO jforum_search_results ( topic_id, session, search_time ) SELECT DISTINCT t.topic_id, ?::varchar, NOW() FROM jforum_topics t, jforum_posts p \
	WHERE t.topic_id = p.topic_id \
	AND p.post_id IN (:posts:)
	
SearchModel.getPostsToIndex = SELECT p.post_id, pt.post_text, pt.post_subject \
	FROM jforum_posts p, jforum_posts_text pt \
	WHERE p.post_id = pt.post_id \
	AND p.post_id BETWEEN ? AND ? \
	LIMIT ? OFFSET ?

# #############
# SmiliesModel
# #############
SmiliesModel.lastGeneratedSmilieId = SELECT CURRVAL('jforum_smilies_seq')

# ##################
# PermissionControl
# ##################
PermissionControl.lastGeneratedRoleId = SELECT CURRVAL('jforum_roles_seq')

# ##############
# CategoryModel
# ##############
CategoryModel.lastGeneratedCategoryId = SELECT CURRVAL('jforum_categories_seq')

# ################
# AttachmentModel
# ################
AttachmentModel.lastGeneratedAttachmentId = SELECT CURRVAL('jforum_attach_seq')

# ##########
# UserModel
# ##########
UserModel.login = SELECT user_id FROM jforum_users WHERE lower(username) = lower(?) AND user_password = ?
