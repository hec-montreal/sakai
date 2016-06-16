################################################################################## 
# $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/WEB-INF/config/database/oracle/oracle.sql $ 
# $Id: oracle.sql 71138 2010-11-02 18:44:42Z murthy@etudes.org $ 
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
# #############
# GroupModel
# #############
GroupModel.addNew = INSERT INTO jforum_groups (group_id, group_name, group_description, parent_id) VALUES (jforum_groups_seq.nextval, ?, ?, ?)

# #############
# CategoryModel
# #############
CategoryModel.addNew = INSERT INTO jforum_categories (categories_id, title, display_order, moderated, archived, gradable, start_date, end_date, lock_end_date) VALUES (jforum_categories_seq.nextval, ?, ?, ?, ?, ?, ?, ?, ?)

CategoryModel.selectAllUserPostsCountByCategory = SELECT s.user_id, COUNT(p.post_id) AS user_posts_count, u.user_fname AS user_fname,u.user_lname AS user_lname, u.sakai_user_id, u.username \
	FROM  (SELECT user_id from jforum_site_users WHERE sakai_site_id = ?) s \
	LEFT OUTER JOIN jforum_users u ON (s.user_id = u.user_id) \
	LEFT OUTER JOIN jforum_posts p ON (s.user_id = p.user_id) \
	AND p.forum_id IN (SELECT forum_id FROM jforum_forums f where f.categories_id = ?) \
	GROUP BY s.user_id, u.user_fname, u.user_lname, u.sakai_user_id, u.username

CategoryModel.selectUserPostsCountByCategory = SELECT p.user_id, COUNT(p.post_id) AS user_posts_count, u.user_fname AS user_fname,u.user_lname AS user_lname, u.sakai_user_id, u.username \
	FROM jforum_posts p, jforum_users u, jforum_forums f \
	WHERE p.forum_id = f.forum_id AND f.categories_id = ? AND p.user_id = u.user_id \
	GROUP BY p.user_id, u.user_fname, u.user_lname, u.sakai_user_id, u.username


# #############
# RankingModel
# #############
RankingModel.addNew = INSERT INTO jforum_ranks (rank_id, rank_title, rank_min ) VALUES (jforum_ranks_seq.nextval, ?, ? )

# #############
# ConfigModel
# #############
ConfigModel.insert = INSERT INTO jforum_config (config_id, config_name, config_value) VALUES (jforum_config_seq.nextval, ?, ?)

# ##########
# UserModel
# ##########
# Added first and last names -- JMH
#UserModel.addNew = INSERT INTO jforum_users (user_id, username, user_password, user_email, user_regdate, user_actkey, user_fname, user_lname) VALUES (jforum_users_seq.nextval, ?, ?, ?, ?, ?, ?, ?)
UserModel.addNew = INSERT INTO jforum_users (user_id, username, user_password, user_email, user_regdate, user_actkey, user_fname, user_lname,sakai_user_id) VALUES (jforum_users_seq.nextval, ?, ?, ?, ?, ?, ?, ?, ?)

UserModel.selectAllByLimit = SELECT * FROM ( \
	SELECT a.user_email, a.user_id, a.user_posts, a.user_regdate, a.username, a.deleted, a.user_karma, a.user_from, a.user_website, \
		a.user_viewemail , a.user_fname, a.user_lname, ROW_NUMBER() OVER(ORDER BY username) LINENUM \
	FROM jforum_users a WHERE a.user_id in \
		(SELECT distinct user_id FROM jforum_user_groups  where \
			group_id in ( \
				select group_id from jforum_sakai_course_groups where course_id = ? \
			) \
		) ORDER BY a.username \
) WHERE LINENUM >= ? AND LINENUM <= ?

#UserModel.selectAllByLimit = SELECT * FROM ( \
#        SELECT user_email, user_id, user_posts, user_regdate, username, deleted, user_karma, user_from, user_website, user_viewemail, ROW_NUMBER() OVER(ORDER BY username) LINENUM  \
#        FROM jforum_users ORDER BY username \
#        ) \
#        WHERE LINENUM >= ? AND LINENUM <= ?

UserModel.lastGeneratedUserId = SELECT jforum_users_seq.currval FROM DUAL

UserModel.selectById = SELECT u.*, \
	(SELECT COUNT(1) FROM jforum_privmsgs pm \
	WHERE pm.privmsgs_to_userid = u.user_id \
	AND pm.privmsgs_type = 1) AS private_messages \
	FROM jforum_users u \
	WHERE u.user_id = ?


UserModel.lastUserRegistered = SELECT * FROM ( \
        SELECT user_id, username, ROW_NUMBER() OVER(ORDER BY user_regdate DESC) LINENUM FROM jforum_users \
        ORDER BY user_regdate DESC \
        ) \
        WHERE LINENUM <= 1
        
UserModel.selectAllByGroup = SELECT * FROM ( \
	SELECT user_email, u.user_id, user_regdate, username, ROW_NUMBER() OVER(ORDER BY username) LINENUM \
	FROM jforum_users u, jforum_user_groups ug \
	WHERE u.user_id = ug.user_id \
	AND ug.group_id = ? \
	ORDER BY username ) WHERE LINENUM >= ? AND LINENUM <= ?

UserModel.getNumberOfMessages = SELECT COUNT(p.user_id) AS num_messages FROM jforum_users u, jforum_posts p \
	WHERE u.user_id = ? \
	AND u.user_id = p.user_id \
	AND p.forum_id IN (select f1.forum_id from jforum_forums f1 where (f1.start_date is NULL OR f1.start_date <= SYSDATE) AND f1.categories_id IN (select categories_id from jforum_sakai_course_categories where course_id = ?)) \
GROUP BY p.user_id



# ################
# PermissionControl
# ################
PermissionControl.addGroupRole = INSERT INTO jforum_roles (role_id, group_id, name, role_type ) VALUES (jforum_roles_seq.nextval, ?, ?, ?)
PermissionControl.addUserRole = INSERT INTO jforum_roles (role_id, user_id, name, role_type ) VALUES (jforum_roles_seq.nextval, ?, ?, ?)

# #############
# PostModel
# #############
PostModel.addNewPost = INSERT INTO jforum_posts (post_id, topic_id, forum_id, user_id, post_time, poster_ip, enable_bbcode, enable_html, enable_smilies, enable_sig, post_edit_time, need_moderate) \
	VALUES (jforum_posts_seq.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, SYSDATE, ?)
PostModel.addNewPostText = INSERT INTO jforum_posts_text ( post_text, post_id, post_subject ) VALUES (EMPTY_BLOB(), ?, ?)
PostModel.addNewPostTextField = SELECT post_text from jforum_posts_text WHERE post_id = ? FOR UPDATE
PostModel.updatePostText = UPDATE jforum_posts_text SET post_subject = ? WHERE post_id = ?

PostModel

PostModel.lastGeneratedPostId = SELECT jforum_posts_seq.currval FROM DUAL

PostModel.selectAllByTopicByLimit = SELECT * FROM ( \
    SELECT p.post_id, topic_id, forum_id, p.user_id, post_time, poster_ip, enable_bbcode, p.attach, p.need_moderate, \
   	enable_html, enable_smilies, enable_sig, post_edit_time, post_edit_count, status, pt.post_subject, pt.post_text, username,  \
   	ROW_NUMBER() OVER(ORDER BY p.post_time ASC) LINENUM \
   	FROM jforum_posts p, jforum_posts_text pt, jforum_users u \
	WHERE p.post_id = pt.post_id  \
	AND topic_id = ? \
	AND p.user_id = u.user_id \
	AND p.need_moderate = 0 \
	ORDER BY post_time ASC \
) \
WHERE LINENUM BETWEEN ? AND ?


# #############
# ForumModel
# #############
ForumModel.addNew = INSERT INTO jforum_forums (forum_id, categories_id, forum_name, forum_desc, start_date, end_date, lock_end_date, forum_order, moderated, forum_type, forum_access_type, forum_grade_type) VALUES (jforum_forums_seq.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)

ForumModel.selectById = SELECT f.*, COUNT(p.post_id) AS total_posts \
	FROM jforum_forums f, jforum_topics t, jforum_posts p  \
	WHERE 	t.forum_id(+) = f.forum_id AND \
        	p.topic_id(+) = t.topic_id AND \
        	f.forum_id = ? \
	GROUP BY f.categories_id, f.forum_id, \
	      f.forum_name, f.forum_desc, f.forum_order, \
	      f.forum_topics, f.forum_last_post_id, f.moderated, f.start_date, f.end_date, f.forum_type, f.forum_access_type, f.forum_grade_type, f.lock_end_date

ForumModel.selectAll = SELECT f.*, COUNT(p.post_id) AS total_posts \
	FROM jforum_forums f, jforum_topics t, jforum_posts p \
	WHERE 	t.forum_id(+) = f.forum_id AND \
        	p.topic_id(+) = t.topic_id \
	GROUP BY f.categories_id, f.forum_id, \
	      f.forum_name, f.forum_desc, f.forum_order, \
	      f.forum_topics, f.forum_last_post_id, f.moderated, f.start_date, f.end_date, f.forum_type, f.forum_access_type, f.forum_grade_type, f.lock_end_date
	      
ForumModel.selectByCategoryId = SELECT f.*, COUNT(p.post_id) AS total_posts \
	FROM jforum_forums f \
	LEFT JOIN jforum_topics t ON t.forum_id = f.forum_id \
	LEFT JOIN jforum_posts p ON p.topic_id = t.topic_id \
	WHERE f.categories_id = ? \
	GROUP BY f.categories_id, f.forum_order, f.forum_id, \
	      f.forum_name, f.forum_desc, \
	      f.forum_topics, f.forum_last_post_id, f.moderated, f.start_date, f.end_date, f.forum_type, f.forum_access_type, f.forum_grade_type, f.lock_end_date
	
ForumModel.lastGeneratedForumId = SELECT jforum_forums_seq.currval FROM DUAL

# JMH
ForumModel.selectAllByCourseId = SELECT f.*, COUNT(p.post_id) AS total_posts \
	FROM jforum_forums f \
	LEFT JOIN jforum_topics t ON t.forum_id = f.forum_id \
	LEFT JOIN jforum_posts p ON p.topic_id = t.topic_id \
	WHERE f.categories_id in (SELECT categories_id FROM jforum_sakai_course_categories WHERE course_id = ?) \
	GROUP BY f.categories_id, f.forum_order, f.forum_name, f.forum_id, f.forum_desc, \
	      f.forum_topics, f.forum_last_post_id, f.moderated, f.start_date, f.end_date, f.forum_type, f.forum_access_type, f.forum_grade_type, f.lock_end_date

ForumModel.selectUserPostsCountByForum	= SELECT p.user_id, COUNT(p.post_id) AS user_posts_count, u.user_fname AS user_fname,u.user_lname AS user_lname, u.sakai_user_id, u.username \
	FROM jforum_posts p, jforum_users u \
	WHERE p.forum_id = ? and p.user_id = u.user_id \
	GROUP BY p.user_id, u.user_fname, u.user_lname, u.sakai_user_id, u.username	      

ForumModel.selectAllUserPostsCountByForum = SELECT s.user_id, COUNT(p.post_id) AS user_posts_count, u.user_fname AS user_fname,u.user_lname AS user_lname, u.sakai_user_id, u.username \
	FROM (SELECT user_id from jforum_site_users WHERE sakai_site_id = ?) s LEFT OUTER JOIN jforum_users u ON s.user_id = u.user_id \
	LEFT OUTER JOIN jforum_posts p ON s.user_id = p.user_id \
	AND p.forum_id = ? \
	GROUP BY s.user_id, u.user_fname, u.user_lname, u.sakai_user_id, u.username
	
ForumModel.selectAllUserPostsCountByTopic = SELECT s.user_id, COUNT(p.post_id) AS user_posts_count, u.user_fname AS user_fname,u.user_lname AS user_lname, u.sakai_user_id, u.username \
	FROM (SELECT user_id from jforum_site_users WHERE sakai_site_id = ?) s LEFT OUTER JOIN jforum_users u ON s.user_id = u.user_id \
	LEFT OUTER JOIN jforum_posts p ON s.user_id = p.user_id \
	AND p.forum_id = ? AND p.topic_id = ? \
	GROUP BY s.user_id, u.user_fname, u.user_lname, u.sakai_user_id, u.username
	
ForumModel.selectUserPostsCountByTopic	= SELECT p.user_id, COUNT(p.post_id) AS user_posts_count, u.user_fname AS user_fname,u.user_lname AS user_lname, u.sakai_user_id, u.username \
	FROM jforum_posts p, jforum_users u \
	WHERE p.forum_id = ? and p.topic_id = ? and p.user_id = u.user_id \
	GROUP BY p.user_id, u.user_fname, u.user_lname, u.sakai_user_id, u.username
	
# #############
# TopicModel
# #############
TopicModel.addNew = INSERT INTO jforum_topics (topic_id, forum_id, topic_title, user_id, topic_time, topic_first_post_id, topic_last_post_id, topic_type, moderated, topic_grade, topic_export) \
	VALUES (jforum_topics_seq.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)

TopicModel.selectAllByForumByLimit = SELECT * FROM ( \
    SELECT t.*, u.username AS posted_by_username, u.user_id AS posted_by_id, u.user_fname AS posted_by_fname, u.user_lname AS posted_by_lname, \
        u2.username AS last_post_by_username, u2.user_id AS last_post_by_id, u2.user_fname AS last_post_by_fname, u2.user_lname AS last_post_by_lname, p2.post_time, p.attach, \
        ROW_NUMBER() OVER(ORDER BY t.topic_type DESC, t.topic_time DESC, t.topic_last_post_id DESC) LINENUM \
	FROM jforum_topics t, jforum_users u, jforum_posts p, jforum_posts p2, jforum_users u2 \
	WHERE t.forum_id = ? \
	AND t.user_id = u.user_id \
	AND p.post_id = t.topic_first_post_id \
	AND p2.post_id = t.topic_last_post_id \
	AND u2.user_id = p2.user_id \
	ORDER BY t.topic_type DESC, p2.post_time DESC, t.topic_last_post_id DESC \
) \
WHERE LINENUM BETWEEN ? AND ?

# Modified -- JMH
#TopicModel.selectRecentTopicsByLimit = SELECT * FROM ( \
#    SELECT t.*, u.username AS posted_by_username, u.user_id AS posted_by_id,\
#    	u2.username AS last_post_by_username, u2.user_id AS last_post_by_id, p2.post_time, p.attach, \
#        ROW_NUMBER() OVER(ORDER BY p2.post_time DESC, t.topic_last_post_id DESC) LINENUM \
#	FROM jforum_topics t, jforum_users u, jforum_posts p, jforum_posts p2, jforum_users u2 \
#	WHERE t.user_id = u.user_id \
#	AND p.post_id = t.topic_first_post_id \
#	AND p2.post_id = t.topic_last_post_id \
#	AND u2.user_id = p2.user_id \
#	AND t.topic_type = 0 \
#	ORDER BY p2.post_time DESC, t.topic_last_post_id DESC \
#    ) \
#    WHERE LINENUM <= ?

TopicModel.selectRecentTopicsByLimit = SELECT * FROM (SELECT t.*, u.username AS posted_by_username, u.user_id AS posted_by_id, \
	u.user_fname AS posted_by_fname,u.user_lname AS posted_by_lname, u2.username AS last_post_by_username, u2.user_id AS last_post_by_id, \
	u2.user_fname AS last_post_by_fname,u2.user_lname AS last_post_by_lname,p2.post_time, p.attach \
	FROM jforum_topics t, jforum_users u, jforum_posts p, jforum_posts p2, jforum_users u2 \
	WHERE t.forum_id IN (SELECT forum_id FROM jforum_forums WHERE \
	categories_id IN (SELECT jscc.categories_id FROM jforum_sakai_course_categories jscc, jforum_categories jc WHERE jscc.course_id = ? AND jscc.categories_id = jc.categories_id AND jc.archived = 0)) \
	AND t.user_id = u.user_id \
	AND p.post_id = t.topic_first_post_id \
	AND p2.post_id = t.topic_last_post_id \
	AND u2.user_id = p2.user_id \
	ORDER BY p2.post_time DESC, t.topic_last_post_id DESC) \
    WHERE ROWNUM <= ?

TopicModel.notifyUsers = SELECT u.user_id AS user_id, u.username AS username, \
	u.user_lang AS user_lang, u.user_email AS user_email, u.sakai_user_id \
	FROM jforum_topics_watch tw, jforum_users u \
	WHERE   tw.user_id = u.user_id AND \
	        tw.topic_id = ? \
	AND tw.is_read = 1 \
	AND u.user_id NOT IN ( ?, ? )

TopicModel.lastGeneratedTopicId = SELECT jforum_topics_seq.currval FROM DUAL

TopicModel.selectLastN = SELECT * FROM ( \
    SELECT topic_title, topic_time, topic_id, topic_type, ROW_NUMBER() OVER(ORDER BY topic_time DESC) LINENUM \
    FROM jforum_topics \
    ORDER BY topic_time DESC \
    ) \
    WHERE LINENUM <= ?


# ####################
# PrivateMessageModel
# ####################
PrivateMessageModel.add = INSERT INTO jforum_privmsgs (privmsgs_id, privmsgs_type, privmsgs_subject, privmsgs_from_userid, \
	privmsgs_to_userid, privmsgs_date, privmsgs_enable_bbcode, privmsgs_enable_html, privmsgs_enable_smilies, \
	privmsgs_attach_sig, privmsgs_priority ) \
	VALUES (jforum_privmsgs_seq.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )

PrivateMessagesModel.addText = INSERT INTO jforum_privmsgs_text ( privmsgs_id, privmsgs_text ) VALUES ( ?, EMPTY_BLOB() )
PrivateMessagesModel.addTextField = SELECT privmsgs_text from jforum_privmsgs_text WHERE privmsgs_id = ? FOR UPDATE
PrivateMessagesModel.lastGeneratedPmId = SELECT jforum_privmsgs_seq.currval FROM DUAL

# ############
# SearchModel
# ############
SearchModel.insertWords = INSERT INTO jforum_search_words (word_id, word_hash, word ) VALUES (jforum_search_words_seq.nextval, ?, ?)

# JMH
#SearchModel.searchBase = SELECT t.*, u.username AS posted_by_username, u.user_id AS posted_by_id, u2.username AS last_post_by_username, u2.user_id AS last_post_by_id, p2.post_time, p.attach \
#	FROM jforum_search_topics t, jforum_users u, jforum_posts p, jforum_posts p2, jforum_users u2, jforum_forums f, jforum_search_results sr \
#	WHERE t.user_id = u.user_id \
#	AND p.post_id = t.topic_first_post_id \
#	AND p2.post_id = t.topic_last_post_id \
#	AND u2.user_id = p2.user_id \
#	AND f.forum_id = t.forum_id \
#	AND t.topic_id = sr.topic_id \
#	AND sr.session_id = ? \
#	AND t.session_id = ? \
#	:criterias: \
#	ORDER BY :orderByField: :orderBy:

SearchModel.searchBase = SELECT t.*, f.forum_id, f.categories_id, f.start_date, u.username AS posted_by_username, u.user_id AS posted_by_id,u.user_fname AS posted_by_fname, \
	u.user_lname AS posted_by_lname, u2.username AS last_post_by_username, u2.user_id AS last_post_by_id,u2.user_fname AS last_post_by_fname, \
	u2.user_lname AS last_post_by_lname, p2.post_time, p.attach, sr.session_id, sr.topic_id \
	FROM jforum_search_topics t, jforum_users u, jforum_posts p, jforum_posts p2, jforum_users u2, jforum_forums f, jforum_search_results sr \
	WHERE t.user_id = u.user_id \
	AND p.post_id = t.topic_first_post_id \
	AND p2.post_id = t.topic_last_post_id \
	AND u2.user_id = p2.user_id \
	AND f.forum_id = t.forum_id \
	AND (f.start_date is NULL OR f.start_date <= SYSDATE) \
	AND t.topic_id = sr.topic_id \
	AND sr.session_id = ? \
	AND t.session_id = ? \
	:criterias: \
	ORDER BY :orderByField: :orderBy:


SearchModel.insertTopicsIds = INSERT INTO jforum_search_results ( topic_id, session_id, search_time ) SELECT DISTINCT t.topic_id, ?, sysdate FROM jforum_topics t, jforum_posts p \
	WHERE t.topic_id = p.topic_id \
	AND p.post_id IN (:posts:)


SearchModel.selectTopicData = INSERT INTO jforum_search_topics (topic_id, forum_id, topic_title, user_id, topic_time, \
	topic_status, topic_replies, topic_vote, topic_type, topic_first_post_id, topic_last_post_id, moderated, session_id, search_time) \
	SELECT t.topic_id, t.forum_id, t.topic_title, t.user_id, t.topic_time, \
	t.topic_status, t.topic_replies, t.topic_vote, t.topic_type, t.topic_first_post_id, t.topic_last_post_id, t.moderated, ?, sysdate \
	FROM jforum_topics t, jforum_search_results s \
	WHERE t.topic_id = s.topic_id \
	AND s.session_id = ?

SearchModel.lastGeneratedWordId = SELECT jforum_search_words_seq.currval FROM DUAL

#
# The construction ((SYSDATE - time_field)*24) > 1.0 mean following:
# (SYSDATE - time_field) return days. E.q if delta is 20 minuts it return 0.0125. If multyply on 24, that it would be hours - 0.3
# So, ((SYSDATE - time_field)*24) > 1.0 totally mean 'delta' > 1 hour   
#
SearchModel.cleanSearchResults = DELETE FROM jforum_search_results WHERE session_id = ? OR ((SYSDATE - search_time)*24) > 1.0
SearchModel.cleanSearchTopics = DELETE FROM jforum_search_topics WHERE session_id = ? OR ((SYSDATE - search_time)*24) > 1.0

SearchModel.searchByTime = INSERT INTO jforum_search_results (topic_id, session_id, time_field) SELECT DISTINCT t.topic_id, ?, SYSDATE FROM jforum_topics t, jforum_posts p \
	WHERE t.topic_id = p.topic_id \
	AND p.post_time > ?

# JMH
SearchModel.searchByLikeWord = SELECT wm.post_id FROM jforum_search_wordmatch wm, jforum_search_words w, \
    jforum_posts jp,jforum_forums jf,jforum_sakai_course_categories jscc \
	WHERE jscc.course_id = ? AND jscc.categories_id = jf.categories_id AND \
	jf.forum_id = jp.forum_id AND (jf.start_date is NULL OR jf.start_date <= SYSDATE) and jp.post_id = wm.post_id\
	AND wm.word_id = w.word_id \
	AND LOWER(w.word) LIKE LOWER(?)

# JMH
SearchModel.searchByWord = SELECT wm.post_id FROM jforum_search_wordmatch wm, jforum_search_words w, \
    jforum_posts jp,jforum_forums jf,jforum_sakai_course_categories jscc \
	WHERE jscc.course_id = ? AND jscc.categories_id = jf.categories_id AND \
	jf.forum_id = jp.forum_id AND (jf.start_date is NULL OR jf.start_date <= SYSDATE) and jp.post_id = wm.post_id AND \
	wm.word_id = w.word_id \
	AND LOWER(w.word) = LOWER(?)

# #############
# SmiliesModel
# #############
SmiliesModel.addNew = INSERT INTO jforum_smilies (smilie_id, code, url, disk_name) VALUES (jforum_smilies_seq.nextval, ?, ?, ?)

SmiliesModel.lastGeneratedSmilieId = SELECT jforum_smilies_seq.currval FROM DUAL

# ##################
# PermissionControl
# ##################
PermissionControl.lastGeneratedRoleId = SELECT jforum_roles_seq.currval FROM DUAL

PermissionControl.loadGroupRoles = SELECT r.role_id, r.name, rv.role_value, rv.role_type AS rv_type, r.role_type \
	FROM jforum_roles r, jforum_role_values rv \
	WHERE rv.role_id(+) = r.role_id AND r.group_id = ? \
	AND user_id = 0

PermissionControl.loadUserRoles = SELECT r.role_id, r.name, rv.role_value, rv.role_type AS rv_type, r.role_type \
	FROM jforum_roles r, jforum_role_values rv \
	WHERE rv.role_id(+) = r.role_id AND r.user_id = ? \
	AND r.group_id = 0

# ##############
# CategoryModel
# ##############
CategoryModel.lastGeneratedCategoryId = SELECT jforum_categories_seq.currval  FROM DUAL

# ###########
# KarmaModel
# ###########
KarmaModel.add = INSERT INTO jforum_karma (karma_id, post_id, post_user_id, from_user_id, points, topic_id, rate_date) VALUES (jforum_karma_seq.nextval, ?, ?, ?, ?, ?, ?)

# ##############
# BookmarkModel
# ##############
BookmarkModel.add = INSERT INTO jforum_bookmarks (bookmark_id, user_id, relation_id, relation_type, public_visible, title, description) \
	VALUES (jforum_bookmarks_seq.nextval, ?, ?, ?, ?, ?, ?)

# JMH
BookmarkModel.selectAllFromUser = SELECT b.bookmark_id, b.user_id, b.relation_type, b.relation_id, b.public_visible, b.title, b.description \
	FROM jforum_bookmarks b \
	WHERE b.user_id = ?  and b.relation_type != 2 \
UNION \
    SELECT b.bookmark_id, b.user_id, b.relation_type, b.relation_id, b.public_visible, b.title, b.description \
	FROM jforum_bookmarks b \
	WHERE b.user_id = ?  and b.relation_type = 2 \
	and b.relation_id in (select topic_id from jforum_topics where forum_id in (select forum_id from jforum_forums where (start_date is NULL OR start_date <= SYSDATE) AND categories_id in (select categories_id from jforum_sakai_course_categories where course_id = ?)))\
	ORDER BY title 	

# ################
# AttachmentModel
# ################
AttachmentModel.addQuotaLimit = INSERT INTO jforum_quota_limit (quota_limit_id, quota_desc, quota_limit, quota_type) VALUES (jforum_quota_limit_seq.nextval, ?, ?, ?)
AttachmentModel.lastGeneratedAttachmentId = SELECT jforum_attach_seq.currval FROM dual

AttachmentModel.addExtensionGroup = INSERT INTO jforum_extension_groups (extension_group_id, name, allow, upload_icon, download_mode) \
	VALUES (jforum_extension_groups_seq.nextval, ?, ?, ?, ?)

AttachmentModel.addExtension = INSERT INTO jforum_extensions (extension_id, extension_group_id, description, upload_icon, extension, allow) \
	VALUES (jforum_extensions_seq.nextval, ?, ?, ?, ?, ?)

AttachmentModel.addAttachment = INSERT INTO jforum_attach (attach_id, post_id, privmsgs_id, user_id) VALUES (jforum_attach_seq.nextval, ?, ?, ?)

AttachmentModel.addAttachmentInfo = INSERT INTO jforum_attach_desc (attach_desc_id, attach_id, physical_filename, real_filename, description, \
	mimetype, filesize, upload_time, thumb, extension_id ) VALUES (jforum_attach_desc_seq.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?)
	
	
# ##############
# ModerationModel -- JMH
# ##############

ModerationModel.topicsByForum = SELECT p.post_id, t.topic_id, t.topic_title, t.topic_replies, p.user_id, p.enable_bbcode, p.attach, \
	p.enable_html, p.enable_smilies, pt.post_subject, pt.post_text, u.user_id, u.user_fname, u.user_lname \
	FROM jforum_posts p, jforum_posts_text pt, jforum_users u, jforum_topics t \
	WHERE p.post_id = pt.post_id \
	AND p.topic_id = t.topic_id \
	AND t.forum_id = ? \
	AND p.user_id = u.user_id \
	AND p.need_moderate = 1 \
	ORDER BY t.topic_id, post_time ASC

# #############
# TopicModel -- JMH
# #############
#commented as there is another query and this query is failing when topics size is reduced
#TopicModel.selectAllByForumByLimit = SELECT * FROM \
#(SELECT t.*, u.username AS posted_by_username, u.user_id AS posted_by_id, u.user_fname AS posted_by_fname, u.user_lname AS posted_by_lname, \
#					u2.username AS last_post_by_username, u2.user_id AS last_post_by_id, u2.user_fname AS last_post_by_fname, u2.user_lname AS last_post_by_lname, p2.post_time, p.attach \
#	FROM jforum_topics t, jforum_users u, jforum_posts p, jforum_posts p2, jforum_users u2 \
#	WHERE t.forum_id = ? \
#	AND t.user_id = u.user_id \
#	AND p.post_id = t.topic_first_post_id \
#	AND p2.post_id = t.topic_last_post_id \
#	AND u2.user_id = p2.user_id \
#	ORDER BY t.topic_type DESC, p2.post_time DESC, t.topic_last_post_id DESC \
#) WHERE ROWNUM >? and ROWNUM <=? \


# ############
# Grade Model
# ############
GradeModel.addNew = INSERT INTO jforum_grade(grade_id, context, grade_type, forum_id, topic_id, categories_id, points, add_to_gradebook) VALUES (jforum_evaluations_seq.nextval, ?, ?, ?, ?, ?, ?, ?)
GradeModel.lastGeneratedGradeId = SELECT jforum_evaluations_seq.currval  FROM DUAL

# ############
# Evaluation Model
# ############
EvaluationModel.addNew = INSERT INTO jforum_evaluations(evaluation_id, grade_id, user_id, sakai_user_id, score, comments, evaluated_by, evaluated_date, released) VALUES (jforum_evaluations_seq.nextval, ?, ?, ?, ?, EMPTY_CLOB(), ?, ?, ?)
EvaluationModel.lastGeneratedEvalId = SELECT jforum_evaluations_seq.currval FROM DUAL
EvaluationModel.addComments = SELECT comments FROM jforum_evaluations WHERE evaluation_id = ? FOR UPDATE
EvaluationModel.update = UPDATE jforum_evaluations SET score = ?, evaluated_by = ?, evaluated_date = ?, released = ? WHERE evaluation_id = ?

# ############
# Special Access
# ############
SpecialAccessModel.addNew = INSERT INTO jforum_special_access(special_access_id, forum_id, start_date, end_date, override_start_date, override_end_date, lock_end_date, users) VALUES (jforum_special_access_seq.nextval, ?, ?, ?, ?, ?, ?, EMPTY_CLOB())
SpecialAccessModel.lastGeneratedSpecilaAccessId = SELECT jforum_special_access_seq.currval FROM DUAL
SpecialAccessModel.addUsers = SELECT users FROM jforum_special_access WHERE special_access_id = ? FOR UPDATE
SpecialAccessModel.update = UPDATE jforum_special_access SET forum_id = ?, start_date = ?, end_date = ?, lock_end_date = ?, override_start_date = ?, override_end_date = ? WHERE special_access_id = ?