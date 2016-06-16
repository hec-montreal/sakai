/*********************************************************************************** 
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-impl/impl/src/java/org/etudes/component/app/jforum/JforumDataServiceImpl.java $ 
 * $Id: JforumDataServiceImpl.java 69355 2010-07-22 21:20:56Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008, 2009, 2010 Etudes, Inc. 
 * 
 * Portions completed before September 1, 2008 Copyright (c) 2004, 2005, 2006, 2007 Foothill College, ETUDES Project 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License. 
 * 
 **********************************************************************************/
package org.etudes.component.app.jforum;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.api.app.jforum.JforumDataService;
import org.etudes.util.XrefHelper;
import org.etudes.util.api.Translation;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.db.api.SqlService;
import org.sakaiproject.event.api.UsageSession;
import org.sakaiproject.event.cover.UsageSessionService;
import org.sakaiproject.id.cover.IdManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.cover.UserDirectoryService;

public class JforumDataServiceImpl implements JforumDataService{
	/** logger*/
	private static Log logger = LogFactory.getLog(JforumDataServiceImpl.class);
	
	public static final int GRADE_DISABLED = 0;
	public static final int GRADE_BY_TOPIC = 1;
	public static final int GRADE_BY_FORUM = 2;
	public static final int GRADE_BY_CATEGORY = 3;
	
	public static final int GRADE_NO = 0;
	public static final int GRADE_YES = 1;
	
	public static final int EXPORT_No = 0;
	public static final int EXPORT_YES = 1;
	
	public static final String ATTACHMENTS_STORE_DIR = "etudes.jforum.attachments.store.dir";
	
	/** Dependency: SqlService */
	protected SqlService sqlService = null;


	public void setSqlService(SqlService service){
		this.sqlService = service;
	}


	public void init(){
		if (logger.isDebugEnabled())
			logger.debug("Entering init....");

		if (logger.isDebugEnabled())
			logger.debug("Exiting init....");
	}
	
	public void destroy(){
	}
	
	/* (non-Javadoc)
	 * @see org.sakaiproject.component.app.jforum.JforumDataService#creatTaskTopics(java.lang.String, java.lang.String)
	 */
	public void createTaskTopicsInNewSite(String fromContextId, String toContextId){
		logger.info("createTaskTopicsInNewSite : fromContextId  - "+ fromContextId +" - : toContextId"+ toContextId);
		//get a connection
		Connection connection = null;
		boolean defaultCommit = false;
		try {
			boolean imported = false;
			//not setting the autocommit assuming sakai autocommit is set correctly
			connection = sqlService.borrowConnection();
			defaultCommit = connection.getAutoCommit();
			connection.setAutoCommit(false);
			
			//get tosite
			Site site = SiteService.getSite(toContextId);
			Collection sakaiSiteGroups = site.getGroups();
			
			String getJforumUserId = "SELECT user_id, username, username FROM jforum_users WHERE sakai_user_id = ?";
			PreparedStatement jforumUserIdPrepStmnt = connection.prepareStatement(getJforumUserId);
			jforumUserIdPrepStmnt.setString(1, UserDirectoryService.getCurrentUser().getId());
			ResultSet rsJforumUserId = jforumUserIdPrepStmnt.executeQuery();
			int jforumUserId = 0; 
			if (rsJforumUserId.next()){
				jforumUserId = rsJforumUserId.getInt("user_id");
			}else{
				//create user in jforum table
				try {
					User sakaiusr = UserDirectoryService.getCurrentUser();
					jforumUserId = createJforumUser(connection, sakaiusr.getEid(), sakaiusr.getEmail(), 
							sakaiusr.getFirstName(), sakaiusr.getLastName(), sakaiusr.getId() );
				} catch (SQLException e) {
					return;
				}
			}
			rsJforumUserId.close();
			jforumUserIdPrepStmnt.close();
			/*after creating categories, forums etc mark the course to initialized if not 
			marked as initialized in datbase*/
			Map dataMap = new HashMap();
			
			try {
				//get categories, forums and topics
				dataMap.putAll(loadExistingSiteData(toContextId, connection));
			} catch (Exception e) {
				if (logger.isDebugEnabled()) logger.debug("createTaskTopicsInNewSite() :Exception " +
						"while loading existing Jforum data :"+ e.toString());
			}
			//get categories from fromContextId
			String categoryModelSelectAllByCourseId = "SELECT a.categories_id, a.title, a.display_order, " +
			"a.moderated, a.gradable, a.start_date, a.end_date FROM jforum_categories a,jforum_sakai_course_categories b where " +
			"a.categories_id = b.categories_id and b.course_id = ? and a.archived = 0 ORDER BY a.display_order";
	
			PreparedStatement catgPrepStmnt = connection.prepareStatement(categoryModelSelectAllByCourseId);
			catgPrepStmnt.setString(1, fromContextId);
			
			ResultSet rsCatg = catgPrepStmnt.executeQuery();	
			boolean categoryNewCreated = false;
			while (rsCatg.next()) {
				categoryNewCreated = false;
				int categoryId = rsCatg.getInt("categories_id");
				String categoryTitle = rsCatg.getString("title");
				//int displayOrder = rsCatg.getInt("display_order");
				int moderated = rsCatg.getInt("moderated");
				int gradable = rsCatg.getInt("gradable");
				Date catStartDate = rsCatg.getTimestamp("start_date");
				Date catEndDate = rsCatg.getTimestamp("end_date");
			
				//check this category if existing
				Map toSiteCatgDataMap = (Map) dataMap.get(toContextId);
				Set toexisCatgTitles = (Set)toSiteCatgDataMap.get("existingcategorytitles");
				
				int createdCategoryId = -1;
				if((toexisCatgTitles.size() == 0) || (!toexisCatgTitles.contains(categoryTitle.trim()))){
					try {
						//create category 
						createdCategoryId = createCategory(connection,
								toContextId, categoryTitle, moderated, gradable, catStartDate, catEndDate, categoryId);
					} catch (Exception e) {
						continue;
					}					
					imported = true;
					categoryNewCreated = true;									
				}
				
				//get category forums
				String forumModelSelectAllByCategoryId = "SELECT f.forum_id, "+
				 "f.categories_id, f.forum_name, f.forum_desc, f.forum_order, " +
				 "f.forum_topics, f.forum_last_post_id, f.moderated, f.start_date, " +
				 "f.end_date, f.forum_type, f.forum_access_type, f.forum_grade_type " +
				 "FROM jforum_forums f WHERE f.categories_id = ? ORDER BY f.forum_order";
				
				PreparedStatement catgForumPrepStmnt = connection.prepareStatement(forumModelSelectAllByCategoryId);
				catgForumPrepStmnt.setInt(1, categoryId);
				
				ResultSet rsCatgForums = catgForumPrepStmnt.executeQuery();
				boolean categoryNewForum = false;
				while (rsCatgForums.next()) {
					categoryNewForum = false;
					int forumId = rsCatgForums.getInt("forum_id");
					//int catgId = rsCatgForums.getInt("categories_id");
					String forumName = rsCatgForums.getString("forum_name");
					String forumDescription = rsCatgForums.getString("forum_desc");
					//int forumOrder = rsCatgForums.getInt("forum_order");
					boolean forumModerated = (rsCatgForums.getInt("moderated") > 0);
					int forumType = rsCatgForums.getInt("forum_type");
					int accessType = rsCatgForums.getInt("forum_access_type");
					int gradeType = rsCatgForums.getInt("forum_grade_type");
					Date startDate = rsCatgForums.getTimestamp("start_date");
					Date endDate = rsCatgForums.getTimestamp("end_date");

					int createdForumId = -1;
					if (categoryNewCreated){
						try {
							//insert all forums
							createdForumId = createForum(connection, forumName,
									createdCategoryId, forumDescription,
									forumType, accessType, forumModerated, gradeType, startDate, endDate);
						} catch (Exception e) {
							continue;
						}						
						imported = true;
						categoryNewForum = true;
					}else{
						//category is existing and insert only non existing forums 
						int exisCatId = 0;
						Map toexisCategoryMap = (Map)toSiteCatgDataMap.get(categoryTitle.trim());
						exisCatId = ((Integer)toexisCategoryMap.get("id")).intValue();
						//create forum if not existing
						Set existingForumTitles = (Set) toexisCategoryMap.get("existingforumtitles");
						
						if (!existingForumTitles.contains(forumName.trim())){
							try {
								createdForumId = createForum(connection,
										forumName, exisCatId, forumDescription,
										forumType, accessType, forumModerated, gradeType, startDate, endDate);
								
								} catch (Exception e) {
								continue;
							}							
							imported = true;
							categoryNewForum = true;
						}
					}
					
					//for new forum create grade if forum grade type is 2
					if (categoryNewForum  && gradeType == GRADE_BY_FORUM){
						
						String gradeModelSelectByForumTopicId = "SELECT grade_id, context, grade_type, " +
								"forum_id, topic_id, points, add_to_gradebook, categories_id FROM jforum_grade WHERE " +
								"forum_id = ? and topic_id = 0";
												
						PreparedStatement gradePrepStmnt = connection.prepareStatement(gradeModelSelectByForumTopicId);
						gradePrepStmnt.setInt(1, forumId);
						
						ResultSet rsGrade = gradePrepStmnt.executeQuery();
						
						float gradePoints = 0f;
						
						if (rsGrade.next()) {
							gradePoints = rsGrade.getFloat("points");
						}
						rsGrade.close();
						gradePrepStmnt.close();
						
						createGrade(connection, toContextId, GRADE_BY_FORUM, createdForumId, 0, 0, gradePoints);
					}
					
					//get forum task topics
					String topicModelSelectAllByForum = "SELECT topic_id, forum_id, topic_title, " +
							"user_id, topic_time, topic_replies, topic_status, topic_vote, topic_type, " +
							"topic_first_post_id, topic_last_post_id, moderated, topic_grade, topic_export FROM jforum_topics " +
							"WHERE forum_id = ? AND (topic_export = ? OR topic_type = ?)";
					
					PreparedStatement topicsPrepStmnt = connection.prepareStatement(topicModelSelectAllByForum);
					topicsPrepStmnt.setInt(1, forumId);
					topicsPrepStmnt.setInt(2, EXPORT_YES);
					/*10/17/2008 Murthy Added the below line to support task topic type 1 and this is changed to
					topic_export.*/
					topicsPrepStmnt.setInt(3, 1);
					
					ResultSet rsTopics = topicsPrepStmnt.executeQuery();
					while (rsTopics.next()) {
						int fromTopicId = rsTopics.getInt("topic_id");
						String topicTitle = rsTopics.getString("topic_title"); 
						int topicType = rsTopics.getInt("topic_type");
						int topicFirstPostId = rsTopics.getInt("topic_first_post_id");
						int topicGrade = rsTopics.getInt("topic_grade");
						
						if (categoryNewForum ){
							//new forum - create all topics
							try {
								processTopic(connection, fromContextId, toContextId, jforumUserId, forumId, fromTopicId, 
										createdForumId, topicTitle, topicType, topicGrade, topicFirstPostId);
								imported = true;
							} catch (Exception e) {
								e.printStackTrace();
							}
														
						}else{
							//check if task topic is existing
							Map toexisCategoryMap = (Map)toSiteCatgDataMap.get(categoryTitle.trim());

							//create topic if not existing
							Map existingForumMap = (Map) toexisCategoryMap.get(forumName.trim());
							int exisForumId = ((Integer)existingForumMap.get("id")).intValue();
							Set existingTopicTitles = (Set) existingForumMap.get("existingtopictitles");
							
							if (!existingTopicTitles.contains(topicTitle.trim())){
								try {
									processTopic(connection, fromContextId, toContextId, jforumUserId, forumId, fromTopicId, 
											exisForumId, topicTitle, topicType, topicGrade, topicFirstPostId);
									imported = true;
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
							
						}
					}
					rsTopics.close();
					topicsPrepStmnt.close();
				}
				rsCatgForums.close();
				catgForumPrepStmnt.close();
			}
			rsCatg.close();
			catgPrepStmnt.close();

			//update database field for import success
			if (imported){
				String courseImportSelectByCourseId = "SELECT sakai_site_id, imported FROM jforum_import where sakai_site_id = ?";
				String courseImportAddNew = "INSERT INTO jforum_import (sakai_site_id, imported) VALUES (?, ?)";
				String courseImportUpdate = "UPDATE jforum_import set imported = ? where sakai_site_id = ?";
				
				PreparedStatement prepStatement = connection.prepareStatement(courseImportSelectByCourseId);
				prepStatement.setString(1, toContextId);
				ResultSet rs = prepStatement.executeQuery();
				boolean initialized = false;
				if (rs.next()) {
					initialized = true;
				}
				rs.close();
				prepStatement.close();
								
				if (initialized){
					prepStatement = connection.prepareStatement(courseImportUpdate);
					prepStatement.setInt(1, 1);
					prepStatement.setString(2, toContextId);					
					
					prepStatement.executeUpdate();
					prepStatement.close();
				}else{
					prepStatement = connection.prepareStatement(courseImportAddNew);
					prepStatement.setString(1, toContextId);
					prepStatement.setInt(2, 1);
					
					prepStatement.executeUpdate();
					prepStatement.close();
				}
				
				//mark course initialized
				String isCourseIntialized = "SELECT course_id, init_status FROM " +
										"jforum_sakai_course_initvalues where course_id = ?";
				prepStatement = connection.prepareStatement(isCourseIntialized);
				prepStatement.setString(1, toContextId);
				
				rs = prepStatement.executeQuery();

				if (!rs.next()) {
					String markCourseInitialized = "INSERT INTO jforum_sakai_course_initvalues " +
					"(course_id, init_status) VALUES (?, ?)";
					PreparedStatement p = connection.prepareStatement(markCourseInitialized);
					p .setString(1, toContextId);
					p .setInt(2, 1);
					p .execute();
					p .close();
				}
				rs.close();
				prepStatement.close();
				
				//if user is not in site add to site
				String getUserFromSiteSql = "SELECT sakai_site_id, user_id FROM jforum_site_users WHERE sakai_site_id = ? AND user_id = ?";
				prepStatement = connection.prepareStatement(getUserFromSiteSql);
				prepStatement.setString(1, toContextId);
				prepStatement.setInt(2, jforumUserId);
				rs = prepStatement.executeQuery();

				if (!rs.next()) {
					String addUserToSite = "INSERT INTO jforum_site_users (sakai_site_id, user_id) VALUES (?, ?)";
					PreparedStatement p = connection.prepareStatement(addUserToSite);
					p .setString(1, toContextId);
					p .setInt(2, jforumUserId);
					p .execute();
					p .close();
				}
				rs.close();
				prepStatement.close();
				connection.commit();
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) logger.error("createTaskTopicsInNewSite() : Error while createTaskTopicsInNewSite : "+ e);
			e.printStackTrace();
		}finally{
			try{
				if (connection != null) {
					connection.setAutoCommit(defaultCommit);
					sqlService.returnConnection(connection);
				}
			}catch (Exception e1){
				if (logger.isErrorEnabled()) logger.error(e1);
			}
		}
	}


	
	/**
	 * load existing site categories, forums, topics etc
	 * @param toContextId site id
	 * @param connection connection
	 * @throws SQLException exception
	 */
	private Map loadExistingSiteData(String toContextId, Connection connection) throws SQLException {
		
		String categoryModelSelectAllByCourseIdExis = "SELECT a.categories_id, a.title, a.display_order, " +
				"a.moderated FROM jforum_categories a,jforum_sakai_course_categories b where " +
				"a.categories_id = b.categories_id and b.course_id = ? and a.archived = 0 ORDER BY a.display_order";
		
		PreparedStatement exisCatgPrepStmnt = connection.prepareStatement(categoryModelSelectAllByCourseIdExis, Statement.RETURN_GENERATED_KEYS);
				
		//get existing categories from toContextId site
		exisCatgPrepStmnt.setString(1, toContextId);
		ResultSet rsExisCatg = exisCatgPrepStmnt.executeQuery();
		
		/*if categories have same name then the forums are imported into those categories*/
		Map siteCatgDataMap = new HashMap();
		Set exisCatgTitles = new HashSet();
		while (rsExisCatg.next()) {
			String exisCategoryTitle = rsExisCatg.getString("title");
			int categoryId = rsExisCatg.getInt("categories_id");
			
			//if duplicate catg titles are existing then ignore one
			if (!exisCatgTitles.contains(exisCategoryTitle.trim())){
				exisCatgTitles.add(exisCategoryTitle.trim());
				Map exisCatgMap = new HashMap();
				
				//get forums for this category
				String forumModelSelectAllByCategoryId = "SELECT f.forum_id, "+
				 "f.categories_id, f.forum_name, f.forum_desc, f.forum_order, " +
				 "f.forum_topics, f.forum_last_post_id, f.moderated, f.start_date, " +
				 "f.end_date, f.forum_type, f.forum_access_type " +
				 "FROM jforum_forums f WHERE f.categories_id = ? ORDER BY f.forum_order";
				
				PreparedStatement exisCatgForumPrepStmnt = connection.prepareStatement(forumModelSelectAllByCategoryId);
				exisCatgForumPrepStmnt.setInt(1, categoryId);
				ResultSet rsCatgForumsExis = exisCatgForumPrepStmnt.executeQuery();
				Set exisForumTitles = new HashSet();
				while (rsCatgForumsExis.next()) {
					Map exisForumMap = new HashMap();
					int forumId = rsCatgForumsExis.getInt("forum_id");
					String forumName = rsCatgForumsExis.getString("forum_name");
					exisForumTitles.add(forumName.trim());
					
					//get existing category forum task topics
					String topicModelSelectAllByForumExis = "SELECT topic_id, forum_id, topic_title, " +
							"user_id, topic_time, topic_replies, topic_status, topic_vote, topic_type, " +
							"topic_first_post_id, topic_last_post_id, moderated FROM jforum_topics " +
							"WHERE forum_id = ? and (topic_export = ? OR topic_type = ?)";
					
					PreparedStatement exisTopicsPrepStmnt = connection.prepareStatement(topicModelSelectAllByForumExis);
					exisTopicsPrepStmnt.setInt(1, forumId);
					exisTopicsPrepStmnt.setInt(2, EXPORT_YES);
					/*10/17/2008 Murthy Added the below line to support task topic type 1 and this is changed to
					topic_export.*/
					exisTopicsPrepStmnt.setInt(3, 1);
					
					ResultSet rsTopicsExis = exisTopicsPrepStmnt.executeQuery();
					Set exisTopicTitles = new HashSet();
					while (rsTopicsExis.next()) {
						String topicTitle = rsTopicsExis.getString("topic_title"); 
						int topicType = rsTopicsExis.getInt("topic_type");
						
						//add only task topics - moved to query
						//if (topicType == TYPE_TASK){
							exisTopicTitles.add(topicTitle.trim());
						//}
					}
					exisForumMap.put("existingtopictitles", exisTopicTitles);
					exisForumMap.put("id", forumId);

					exisCatgMap.put(forumName.trim(), exisForumMap );
				}
				rsCatgForumsExis.close();
				exisCatgForumPrepStmnt.close();
				
				exisCatgMap.put("existingforumtitles", exisForumTitles);
				exisCatgMap.put("id", new Integer(categoryId));
				
				siteCatgDataMap.put(exisCategoryTitle.trim(), exisCatgMap);
			}
		}
		rsExisCatg.close();
		exisCatgPrepStmnt.close();
		
		siteCatgDataMap.put("existingcategorytitles", exisCatgTitles);
		
		Map dataMap = new HashMap();
		dataMap.put(toContextId, siteCatgDataMap);
		
		return dataMap;
	}


	/**
	 * process topic
	 * @param connection connection
	 * @param jforumUserId jforum user id
	 * @param createdForumId forum id
	 * @param topicTitle topic title
	 * @param topicType topic type
	 * @param topicFirstPostId topic's first post id
	 * @throws SQLException
	 */
	private void processTopic(Connection connection, String fromContext, String toContextId, int jforumUserId, 
								int fromForumId, int fromTopicId, int createdForumId, String topicTitle, 
								int topicType, int topicGrade, int topicFirstPostId)
																	throws SQLException {
		int createdTopicId, createdPostId;
		/* create new topic then add new post. After adding post assign the post id to topic and
		update the topic*/
		createdTopicId = createTopic(connection, toContextId, fromForumId, fromTopicId, createdForumId, topicTitle, 
											jforumUserId, topicType, topicGrade, topicFirstPostId);
		
		//create post and post text
		createdPostId = createPost(connection, topicFirstPostId, createdTopicId, 
														createdForumId, jforumUserId);
		String postTextSql = "select post_id, post_text, post_subject from jforum_posts_text where post_id = ?";
		PreparedStatement postTextStmnt = connection.prepareStatement(postTextSql);
		postTextStmnt.setInt(1, topicFirstPostId);
		ResultSet rsPostText = postTextStmnt.executeQuery();
		
		if(rsPostText.next()){
			String postText = rsPostText.getString("post_text");
			String postSubject = rsPostText.getString("post_subject");
			
			// harvest post text embedded references
			Set<String> refs = XrefHelper.harvestEmbeddedReferences(postText, null);
			
			if (logger.isDebugEnabled()) logger.debug("processTopic(): embed references found:" + refs.toString());
			
			if(!refs.isEmpty())
			{
				List<Translation>translations = XrefHelper.importTranslateResources(refs, toContextId, "Jforum");
				postText = XrefHelper.translateEmbeddedReferences(postText, translations, toContextId);
			}
			
			addPostText(connection, createdPostId, postText, postSubject);
		}
		rsPostText.close();
		postTextStmnt.close();
		
		//update topic for first post id
		updateTopic(connection, createdTopicId, createdPostId);
		
		//process attachments
		processAttachments(connection, topicFirstPostId, createdPostId, jforumUserId);
		
		//increment user posts
		String userIncrementPosts = "UPDATE jforum_users SET user_posts = user_posts + 1 WHERE user_id = ?";
		PreparedStatement usrIncPostsStmnt = connection.prepareStatement(userIncrementPosts);
		usrIncPostsStmnt.setInt(1, jforumUserId);
		usrIncPostsStmnt.executeUpdate();
		usrIncPostsStmnt.close();
		
		//updateBoardStatus
		String forumUpdateLastPostSql = "UPDATE jforum_forums SET forum_last_post_id = ? WHERE forum_id = ?";
		PreparedStatement forumLastPostStmnt = connection.prepareStatement(forumUpdateLastPostSql);
		forumLastPostStmnt.setInt(1, createdPostId);
		forumLastPostStmnt.setInt(2, createdForumId);
		forumLastPostStmnt.executeUpdate();
		forumLastPostStmnt.close();
		
		//increase topic's count
		String forumIncrementTotalTopics = "UPDATE jforum_forums SET forum_topics = forum_topics + 1 WHERE forum_id = ?";
		PreparedStatement p = connection.prepareStatement(forumIncrementTotalTopics);
		p.setInt(1, createdForumId);
		p.executeUpdate();
		p.close();
	}

	
	/**
	 * process post attachments
	 * @param connection connection	
	 * @param fromPostId from postid
	 * @param toPostId to post id
	 * @param jforumUserId jforum user id
	 */
	private void processAttachments(Connection connection, int fromPostId, int toPostId, 
																		int jforumUserId) {
		//get attachments from fromPostId and make copy for toPostId
		String getAttachementsSql = "SELECT attach_id, post_id, privmsgs_id, user_id " +
													"FROM jforum_attach WHERE post_id = ?";
		try {
			PreparedStatement p = connection.prepareStatement(getAttachementsSql);
			p.setInt(1, fromPostId);
			ResultSet rs = p.executeQuery();
			
			while(rs.next()){
				int attachId = rs.getInt("attach_id");
				//int postId = rs.getInt("post_id");
				
				//create attachment for toPostId
				String addAttachmentSql = null;
				PreparedStatement createAttachmentStmnt = null;
				ResultSet rsAttachment = null;
				int createdAttachId = -1;
				if (sqlService.getVendor().equals("oracle")){
					addAttachmentSql = "INSERT INTO jforum_attach (attach_id, post_id, privmsgs_id, user_id) " +
							"VALUES (jforum_attach_seq.nextval, ?, ?, ?)";
									
					createAttachmentStmnt = connection.prepareStatement(addAttachmentSql);
					createAttachmentStmnt.setInt(1, toPostId);
					createAttachmentStmnt.setInt(2, 0);
					createAttachmentStmnt.setInt(3, jforumUserId);
					createAttachmentStmnt.executeUpdate();

					createAttachmentStmnt.close();
					
					String forumLastGeneratedTopicId = "SELECT jforum_attach_seq.currval FROM DUAL";
					createAttachmentStmnt = connection.prepareStatement(addAttachmentSql);
					rsAttachment = createAttachmentStmnt.executeQuery();
					
					if (rsAttachment.next()) {
						createdAttachId = rsAttachment.getInt(1);
					}
					
					rsAttachment.close();
					createAttachmentStmnt.close();
				}else if (sqlService.getVendor().equalsIgnoreCase("mysql")){
					addAttachmentSql = "INSERT INTO jforum_attach (post_id, privmsgs_id, user_id) " +
							"VALUES (?, ?, ?)";
					
					createAttachmentStmnt = connection.prepareStatement(addAttachmentSql, Statement.RETURN_GENERATED_KEYS);
					createAttachmentStmnt.setInt(1, toPostId);
					createAttachmentStmnt.setInt(2, 0);
					createAttachmentStmnt.setInt(3, jforumUserId);
					createAttachmentStmnt.executeUpdate();
					
					rsAttachment = createAttachmentStmnt.getGeneratedKeys();
					if (rsAttachment.next()) {
						createdAttachId = rsAttachment.getInt(1);
					}
					rsAttachment.close();
					createAttachmentStmnt.close();
				}
				
				String attachmentDetailSql = "SELECT attach_desc_id, attach_id, physical_filename, " +
						"real_filename, description, mimetype, filesize, thumb, extension_id " +
						" FROM jforum_attach_desc WHERE attach_id = ?";
				
				PreparedStatement p1 = connection.prepareStatement(attachmentDetailSql);
				p1.setInt(1, attachId);
				ResultSet rs1 = p1.executeQuery();
				if(rs1.next()){
					int attachDescId = rs1.getInt("attach_desc_id");
					//int attachId = rs1.getInt("attach_id");
					String physicalFilename = rs1.getString("physical_filename");
					String realFilename = rs1.getString("real_filename");
					String description = rs1.getString("description");
					String mimetype = rs1.getString("mimetype");
					int filesize = rs1.getInt("filesize");
					int thumb = rs1.getInt("thumb");
					int extensionId = rs1.getInt("extension_id");
					
					try {
						String attachmentsStoreDir = ServerConfigurationService.getString(ATTACHMENTS_STORE_DIR);
						
						if (attachmentsStoreDir == null || attachmentsStoreDir.trim().length() == 0)
						{
							//to get the file path is needed
							String tomcatPath = getCatalina();
							//if (logger.isInfoEnabled()) logger.info("Tomcat path is : "+ tomcatPath);
							//load jforums SystemGlobal.properties and get attachments store directory
							String propFile = tomcatPath +"/webapps/etudes-jforum-tool/WEB-INF/config/SystemGlobals.properties";
							Properties props = new Properties();
							props.load(new FileInputStream(propFile));
							/*attachment file path is the value of System global 
							 variable attachments.store.dir + physical_filename*/
							attachmentsStoreDir = (String)props.get(ATTACHMENTS_STORE_DIR);

							//String attachmentsUploadDir = (String)props.get("attachments.upload.dir");
							File attSrorePath = new File(attachmentsStoreDir);
							if (!attSrorePath.exists()){
								//assuming default path
								attachmentsStoreDir = tomcatPath +"/webapps/etudes-jforum-tool/upload/";
							}
						}

						String addAttachmentInfo = null;
						//create attachment file
						Calendar c = new GregorianCalendar();
						c.setTimeInMillis(System.currentTimeMillis());
						c.get(Calendar.YEAR);
						int year = Calendar.getInstance().get(Calendar.YEAR);
						int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
						int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
						
						String dir = "" + year + "/" + month + "/" + day + "/";
						new File(attachmentsStoreDir + "/" + dir).mkdirs();
						String fileext =  realFilename.trim().substring(realFilename.trim().lastIndexOf('.')+1);
						String physicalFilenameActpath = dir + IdManager.createUuid() + "_" + jforumUserId +"." + fileext;
						try {
							saveAttachmentFile(attachmentsStoreDir+ File.separator+ physicalFilenameActpath, 
									new File(attachmentsStoreDir+ File.separator+ physicalFilename));
						} catch (Exception e) {
							if (logger.isErrorEnabled()) logger.error("processAttachments(): Error while saving attachemnt file");
							e.printStackTrace();
						}
						
						PreparedStatement createAttachmentInfoStmnt = null;
						ResultSet rsAttachmentInfo = null;
						int createdAttachInfoId = -1;
						if (sqlService.getVendor().equals("oracle")){
							addAttachmentInfo = "INSERT INTO jforum_attach_desc (attach_desc_id, attach_id, physical_filename, " +
							"real_filename, description, mimetype, filesize, upload_time, thumb, extension_id ) " +
							"VALUES (jforum_attach_desc_seq.nextval, ?, ?, ?, ?, ?, ?, SYSDATE, ?, ?)";
							
							createAttachmentInfoStmnt = connection.prepareStatement(addAttachmentSql);
							createAttachmentInfoStmnt.setInt(1, createdAttachId);
							createAttachmentInfoStmnt.setString(2, physicalFilenameActpath);
							createAttachmentInfoStmnt.setString(3, realFilename);
							createAttachmentInfoStmnt.setString(4, description);
							createAttachmentInfoStmnt.setString(5, mimetype);
							createAttachmentInfoStmnt.setInt(6, filesize);
							createAttachmentInfoStmnt.setInt(7, thumb);
							createAttachmentInfoStmnt.setInt(8, extensionId);
							
							createAttachmentInfoStmnt.close();
							
							String lastGeneratedAttachInfoId = "SELECT jforum_attach_desc_seq.currval FROM DUAL";
							createAttachmentInfoStmnt = connection.prepareStatement(lastGeneratedAttachInfoId);
							rsAttachmentInfo = createAttachmentInfoStmnt.executeQuery();
							
							if (rsAttachmentInfo.next()) {
								createdAttachInfoId = rsAttachmentInfo.getInt(1);
							}
							rsAttachmentInfo.close();
							createAttachmentInfoStmnt.close();							
						}else if (sqlService.getVendor().equalsIgnoreCase("mysql")){
							addAttachmentInfo = "INSERT INTO jforum_attach_desc (attach_id, physical_filename, " +
							"real_filename, description, mimetype, filesize, upload_time, thumb, extension_id ) " +
							"VALUES (?, ?, ?, ?, ?, ?, NOW(), ?, ?)";
							
							createAttachmentInfoStmnt = connection.prepareStatement(addAttachmentInfo, Statement.RETURN_GENERATED_KEYS);
							createAttachmentInfoStmnt.setInt(1, createdAttachId);
							createAttachmentInfoStmnt.setString(2, physicalFilenameActpath);
							createAttachmentInfoStmnt.setString(3, realFilename);
							createAttachmentInfoStmnt.setString(4, description);
							createAttachmentInfoStmnt.setString(5, mimetype);
							createAttachmentInfoStmnt.setInt(6, filesize);
							createAttachmentInfoStmnt.setInt(7, thumb);
							createAttachmentInfoStmnt.setInt(8, extensionId);
							
							createAttachmentInfoStmnt.executeUpdate();
							
							rsAttachmentInfo = createAttachmentInfoStmnt.getGeneratedKeys();
							if (rsAttachmentInfo.next()) {
								createdAttachInfoId = rsAttachmentInfo.getInt(1);
							}
							rsAttachmentInfo.close();
							createAttachmentInfoStmnt.close();
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				rs1.close();
				p1.close();
				
			}
			rs.close();
			p.close();
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) logger.error("processAttachments():Error while processing attachments : "+ e.toString());
			e.printStackTrace();
		}
		
	}


	/**
	 * create post text
	 * @param connection connection	
	 * @param postId post id
	 * @param postText post text
	 * @param postSubject post subject
	 */
	private void addPostText(Connection connection, int postId, String postText, String postSubject) {
		String addNewPostText = "INSERT INTO jforum_posts_text ( post_id, post_text, post_subject ) " +
																				"VALUES (?, ?, ?)";
		try {
			PreparedStatement p = connection.prepareStatement(addNewPostText);
			p.setInt(1, postId);
			p.setString(2, postText);
			p.setString(3, postSubject);
			p.executeUpdate();
			p.close();
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) logger.error("addPostText():Error while creating post text : "+ e.toString());
			e.printStackTrace();
		}
		
	}

	/**
	 * update topic
	 * @param connection connection
	 * @param createdTopicId topic id
	 * @param createdPostId post id
	 */
	private void updateTopic(Connection connection, int topicId, int postId) {
		String topicUpdate = "UPDATE jforum_topics SET topic_first_post_id = ?, topic_last_post_id = ? WHERE topic_id = ?";
		try {
			PreparedStatement p = connection.prepareStatement(topicUpdate);
				
			p.setInt(1, postId);
			p.setInt(2, postId);
			p.setInt(3, topicId);
			p.executeUpdate();

			p.close();
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) logger.error("updateTopic():Error while updating post : "+ e.toString());
			e.printStackTrace();
		}
		
	}

	
	/**
	 * create topic
	 * @param connection connection
	 * @param forumId forum id
	 * @param topicTitle topic title
	 * @param userId user id
	 * @param topicType topic type
	 * @param firstPostId first post id
	 * @return
	 */
	private int createTopic(Connection connection, String toContextId, int fromForumId, int fromTopicId, int forumId, 
								String topicTitle, int userId, int topicType, int topicGrade, int firstPostId) {
		if (logger.isDebugEnabled()) logger.debug("creating topic with topicName :"+ topicTitle 
				+" for forum_id : "+ forumId);
		if (logger.isDebugEnabled()) logger.debug("Entering createTopic......");
		
		int topicId = -1;
		try {
			PreparedStatement p = null;
			ResultSet rs = null;
			
			String topicAddNew = null;
			if (sqlService.getVendor().equals("oracle")){
				topicAddNew = "INSERT INTO jforum_topics (topic_id, forum_id, topic_title, " +
						"user_id, topic_time, topic_first_post_id, topic_last_post_id, " +
						"topic_type, moderated, topic_grade, topic_export)" +
						"VALUES (jforum_topics_seq.nextval, ?, ?, ?, ?, SYSDATE, ?, ?, ?, ?, ?)";
				
				p = connection.prepareStatement(topicAddNew);
				p.setInt(1, forumId);
				p.setString(2, topicTitle);
				p.setInt(3, userId);
				//p.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
				p.setInt(4, 0);
				p.setInt(5, 0);
				p.setInt(6, topicType);
				p.setInt(7, 0);
				if (topicGrade == GRADE_YES)
					p.setInt(8, topicGrade);
				else
					p.setInt(8, GRADE_NO);
				
				p.setInt(9, EXPORT_YES);
				p.executeUpdate();

				p.close();
				
				String forumLastGeneratedTopicId = "SELECT jforum_topics_seq.currval FROM DUAL";
				p = connection.prepareStatement(forumLastGeneratedTopicId);
				rs = p.executeQuery();
				
				if (rs.next()) {
					topicId = rs.getInt(1);
				}
				
				rs.close();
				p.close();
			}else if (sqlService.getVendor().equalsIgnoreCase("mysql")){
				topicAddNew = "INSERT INTO jforum_topics (forum_id, topic_title, user_id, " +
				"topic_time, topic_first_post_id, topic_last_post_id, topic_type, moderated, topic_grade, topic_export)" +
				"VALUES (?, ?, ?, NOW(), ?, ?, ?, ?, ?, ?)";
				
				p = connection.prepareStatement(topicAddNew, Statement.RETURN_GENERATED_KEYS);
				p.setInt(1, forumId);
				p.setString(2, topicTitle);
				p.setInt(3, userId);
				//p.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
				p.setInt(4, 0);
				p.setInt(5, 0);
				p.setInt(6, topicType);
				p.setInt(7, 0);
				if (topicGrade == GRADE_YES)
					p.setInt(8, topicGrade);
				else
					p.setInt(8, GRADE_NO);
				p.setInt(9, EXPORT_YES);
				p.executeUpdate();
				
				rs = p.getGeneratedKeys();
				if (rs.next()) {
					topicId = rs.getInt(1);
					
				rs.close();
				p.close();
				}
			}
			
			//create grade for grade topic
			if (topicGrade == GRADE_YES) {
				
				String gradeModelSelectByForumTopicId = "SELECT grade_id, context, grade_type, " +
				"forum_id, topic_id, points, add_to_gradebook FROM jforum_grade WHERE " +
				"forum_id = ? and topic_id = ?";
								
				PreparedStatement gradePrepStmnt = connection.prepareStatement(gradeModelSelectByForumTopicId);
				gradePrepStmnt.setInt(1, fromForumId);
				gradePrepStmnt.setInt(2, fromTopicId);
				
				ResultSet rsGrade = gradePrepStmnt.executeQuery();
				
				float gradePoints = 0f;
				
				if (rsGrade.next()) {
					gradePoints = rsGrade.getFloat("points");
				}
				rsGrade.close();
				gradePrepStmnt.close();
				
				createGrade(connection, toContextId, GRADE_BY_TOPIC, forumId, topicId, 0, gradePoints);
				
			}
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) logger.error("createTopic():Error while creating topic : "+ e.toString());
			e.printStackTrace();
		}
		
		if (logger.isDebugEnabled()) logger.debug("Exiting createTopic......");
		return topicId;
	}


	/**
	 * create post
	 * @param connection
	 * @param exisPostId
	 * @param topicId
	 * @param forumId
	 * @param userId
	 * @return
	 */
	private int createPost(Connection connection, int exisPostId, int topicId, int forumId, int userId) {
		if (logger.isDebugEnabled())logger.debug("Entering createPost......");
		//use exisPostId for post properties
		
		String addNewPost = null;
		PreparedStatement p = null;
		ResultSet rs = null;
		int postId = -1;
		
		try {
			String exisPostDetails = "SELECT post_id, topic_id, forum_id, user_id, post_time, " +
					"enable_bbcode, enable_html, enable_smilies, enable_sig," +
					"attach , need_moderate FROM jforum_posts WHERE post_id = ?";
			PreparedStatement exisPostDetailsStmnt = connection.prepareStatement(exisPostDetails);
			exisPostDetailsStmnt.setInt(1, exisPostId);
			ResultSet rsexisPostDetails = exisPostDetailsStmnt.executeQuery();
						
			int enableBbcode = 0;
			int enableHtml = 0;
			int enableSmilies = 0;
			int enableSig = 0;
			int attach = 0;
			int needModerate = 0;
			
			if (rsexisPostDetails.next()){
				enableBbcode = rsexisPostDetails.getInt("enable_bbcode");
				enableHtml = rsexisPostDetails.getInt("enable_html");
				enableSmilies = rsexisPostDetails.getInt("enable_smilies");
				enableSig = rsexisPostDetails.getInt("enable_sig");
				attach = rsexisPostDetails.getInt("attach");
				needModerate = rsexisPostDetails.getInt("need_moderate");
			}
			exisPostDetailsStmnt.close();
			rsexisPostDetails.close();
			
			if (sqlService.getVendor().equals("oracle")){
				addNewPost = "INSERT INTO jforum_posts (post_id, topic_id, forum_id, user_id, " +
						"post_time, poster_ip, enable_bbcode, enable_html, enable_smilies, " +
						"enable_sig, post_edit_time, attach, need_moderate) " +
						"VALUES (jforum_posts_seq.nextval, ?, ?, ?, SYSDATE, ?, ?, ?, ?,?, SYSDATE, ?, ?)";
				
				p = connection.prepareStatement(addNewPost);
				p.setInt(1, topicId);
				p.setInt(2, forumId);
				p.setLong(3, userId);
				//p.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
				UsageSession usageSession = UsageSessionService.getSession();
				p.setString(4, usageSession.getIpAddress());
				p.setInt(5, enableBbcode);
				p.setInt(6, enableHtml);
				p.setInt(7, enableSmilies);
				p.setInt(8, enableSig);
				p.setInt(9, attach);
				p.setInt(10, needModerate);
				p.executeUpdate();

				p.close();
				
				String lastGeneratedTopidId = "SELECT jforum_posts_seq.currval FROM DUAL";
				p = connection.prepareStatement(lastGeneratedTopidId);
				rs = p.executeQuery();
				
				if (rs.next()) {
					postId = rs.getInt(1);
				}
				
				rs.close();
				p.close();
			}else if (sqlService.getVendor().equalsIgnoreCase("mysql")){
				addNewPost = "INSERT INTO jforum_posts (topic_id, forum_id, user_id, " +
				"post_time, poster_ip, enable_bbcode, enable_html, enable_smilies, " +
				"enable_sig, post_edit_time, attach, need_moderate) " +
				"VALUES (?, ?, ?, NOW(), ?, ?, ?, ?, ?, NOW(), ?, ?)";
				p = connection.prepareStatement(addNewPost, Statement.RETURN_GENERATED_KEYS);
				p.setInt(1, topicId);
				p.setInt(2, forumId);
				p.setLong(3, userId);
				//p.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
				UsageSession usageSession = UsageSessionService.getSession();
				p.setString(4, usageSession.getIpAddress());
				p.setInt(5, enableBbcode);
				p.setInt(6, enableHtml);
				p.setInt(7, enableSmilies);
				p.setInt(8, enableSig);
				p.setInt(9, attach);
				p.setInt(10, needModerate);
				p.executeUpdate();
				
				rs = p.getGeneratedKeys();
				if (rs.next()) {
					postId = rs.getInt(1);
					
				rs.close();
				p.close();
				}
			}
			
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) logger.error("createPost():Error while creating post : "+ e.toString());
			e.printStackTrace();
		}
		if (logger.isDebugEnabled())logger.debug("Exiting createPost......");
		
		return postId;		
	}


	private int createForum(Connection connection, String forumName, int catgId, String forumDescription, 
							int forumType, int accessType, boolean forumModerated, int gradeType, Date startDate, Date endDate) throws SQLException {
		if (logger.isDebugEnabled()) logger.debug("creating forum with forumName :"+ forumName 
				+" for categoryId : "+ catgId);
		if (logger.isDebugEnabled())logger.debug("Entering createForum......");
		
		int forumId = -1;
		
		try {
			String forumGetMaxOrder = "SELECT MAX(forum_order) FROM jforum_forums";
			PreparedStatement p = connection.prepareStatement(forumGetMaxOrder);
			ResultSet rs = p.executeQuery();
			int order = 1;
			if (rs.next()) {
				order = rs.getInt(1) + 1;
			}

			rs.close();
			p.close();
			//if (logger.isInfoEnabled()) logger.info("forum order : "+ order);
			String forumAddNew = null;
		
			if (sqlService.getVendor().equals("oracle")){
				forumAddNew = "INSERT INTO jforum_forums (forum_id, categories_id, forum_name, " +
						"forum_desc, forum_order, forum_type, forum_access_type, forum_grade_type, start_date, end_date) " +
						"VALUES (jforum_forums_seq.nextval, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				p = connection.prepareStatement(forumAddNew);
				p.setInt(1, catgId);
				p.setString(2, forumName);
				p.setString(3, forumDescription);
				p.setInt(4, order);
				p.setInt(5, forumType);
				p.setInt(6, accessType);
				if (gradeType == GRADE_BY_FORUM || gradeType == GRADE_BY_TOPIC)
					p.setInt(7, gradeType);
				else
					p.setInt(7, GRADE_DISABLED);
				
				if (startDate == null) {
					p.setTimestamp(8, null);
				} else {
					p.setTimestamp(8, new Timestamp(startDate.getTime()));
				}
				
				if (endDate == null) {
					p.setTimestamp(9, null);
				} else {
					p.setTimestamp(9, new Timestamp(endDate.getTime()));
				}
				
				p.executeUpdate();

				p.close();
				
				String forumLastGeneratedForumId = "SELECT jforum_forums_seq.currval FROM DUAL";
				p = connection.prepareStatement(forumLastGeneratedForumId);
				rs = p.executeQuery();
				
				if (rs.next()) {
					forumId = rs.getInt(1);
				}
				
				rs.close();
				p.close();
			}else if (sqlService.getVendor().equalsIgnoreCase("mysql")){
				forumAddNew = "INSERT INTO jforum_forums (categories_id, forum_name, forum_desc, " +
				"forum_order, forum_type, forum_access_type, forum_grade_type, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
				p = connection.prepareStatement(forumAddNew, Statement.RETURN_GENERATED_KEYS);
				p.setInt(1, catgId);
				p.setString(2, forumName);
				p.setString(3, forumDescription);
				p.setInt(4, order);
				p.setInt(5, forumType);
				p.setInt(6, accessType);
				if (gradeType == GRADE_BY_FORUM || gradeType == GRADE_BY_TOPIC)
					p.setInt(7, gradeType);
				else
					p.setInt(7, GRADE_DISABLED);
				
				if (startDate == null) {
					p.setTimestamp(8, null);
				} else {
					p.setTimestamp(8, new Timestamp(startDate.getTime()));
				}
				
				if (endDate == null) {
					p.setTimestamp(9, null);
				} else {
					p.setTimestamp(9, new Timestamp(endDate.getTime()));
				}
				
				p.executeUpdate();
				
				rs = p.getGeneratedKeys();
				if (rs.next()) {
					forumId = rs.getInt(1);
					
				rs.close();
				p.close();
				}
			}
			
			if (logger.isDebugEnabled())logger.debug("Exiting createForum......");
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) logger.error("createForum():Error while creating forum : "+ e.toString());
			e.printStackTrace();
			throw e;
		}
		return forumId;
	}
	
	
	private void createGrade(Connection connection, String toContextId, int gradeType, int forumId, int topicId, int categoryId, float gradePoints) throws SQLException{
		
		String gradeModelAddNewsql = "INSERT INTO jforum_grade(context, grade_type, forum_id, topic_id, categories_id, points) " +
									"VALUES (?, ?, ?, ?, ?, ?)";
		
		PreparedStatement p = connection.prepareStatement(gradeModelAddNewsql);
		p.setString(1, toContextId);
		p.setInt(2, gradeType);
		p.setInt(3, forumId);
		p.setInt(4, topicId);
		p.setInt(5, categoryId);
		p.setFloat(6, gradePoints);
		
		p.executeUpdate();
		p.close();		
	}


	/**
	 * creates category
	 * @param connection
	 * @param toContextId
	 * @param catName
	 * @param moderated
	 * @return categoy Id
	 */
	private int createCategory(Connection connection, String toContextId, String catName, 
															int moderated, int gradable, Date startDate, Date endDate, int exisCatgeoryId) throws Exception {
			if (logger.isDebugEnabled()) logger.debug("creating category with title :"+ catName 
													+" for site : "+ toContextId);
			int categoryId = -1;
			try {
				int order = 1;
				String categoryGetMaxOrderSql = "SELECT MAX(display_order) FROM jforum_categories";
				PreparedStatement p = connection.prepareStatement(categoryGetMaxOrderSql);
				
				ResultSet rs = p.executeQuery();
				if (rs.next()) {
					order = rs.getInt(1) + 1;
				}
				rs.close();
				p.close();
				
				String categoryAddNewSql = null;
				if (sqlService.getVendor().equals("oracle")){
					categoryAddNewSql = "INSERT INTO jforum_categories (categories_id, title, display_order, moderated, gradable, start_date, end_date) " +
							"VALUES (jforum_categories_seq.nextval, ?, ?, ?, ?, ?, ?)";
					p = connection.prepareStatement(categoryAddNewSql);
					p.setString(1, catName);
					p.setInt(2, order);
					p.setInt(3, moderated);
					p.setInt(4, gradable);
					
					if (startDate == null) {
						p.setTimestamp(5, null);
					} else {
						p.setTimestamp(5, new Timestamp(startDate.getTime()));
					}
					
					if (endDate == null) {
						p.setTimestamp(6, null);
					} else {
						p.setTimestamp(6, new Timestamp(endDate.getTime()));
					}
					
					p.executeUpdate();

					p.close();
					
					String categoryLastGeneratedCategoryId = "SELECT jforum_categories_seq.currval  FROM DUAL";
					p = connection.prepareStatement(categoryLastGeneratedCategoryId);
					rs = p.executeQuery();
					
					if (rs.next()) {
						categoryId = rs.getInt(1);
					}
					
					rs.close();
					p.close();
				}else if (sqlService.getVendor().equalsIgnoreCase("mysql")){
					categoryAddNewSql = "INSERT INTO jforum_categories (title, display_order, moderated, gradable, start_date, end_date) VALUES (?, ?, ?, ?, ?, ?)";
					p = connection.prepareStatement(categoryAddNewSql, Statement.RETURN_GENERATED_KEYS);
					p.setString(1, catName);
					p.setInt(2, order);
					p.setInt(3, moderated);
					p.setInt(4, gradable);
					
					if (startDate == null) {
						p.setTimestamp(5, null);
					} else {
						p.setTimestamp(5, new Timestamp(startDate.getTime()));
					}
					
					if (endDate == null) {
						p.setTimestamp(6, null);
					} else {
						p.setTimestamp(6, new Timestamp(endDate.getTime()));
					}
					
					p.executeUpdate();
										
					rs = p.getGeneratedKeys();
					if (rs.next()) {
						categoryId = rs.getInt(1);
					}
					rs.close();
					p.close();
				}
				
				String courseCategoryAddNewSql = "INSERT INTO jforum_sakai_course_categories (course_id,categories_id) VALUES (?, ?)";
				p = connection.prepareStatement(courseCategoryAddNewSql);
				
				p.setString(1, toContextId);
				p.setInt(2, categoryId);
				
				p.execute();
				p.close();
				
				// create grade if category is gradable
				if (gradable == 1)
				{
					String gradeModelSelectByCategoryId = "SELECT grade_id, context, grade_type, " +
							"forum_id, topic_id, points, add_to_gradebook, categories_id FROM jforum_grade " +
							"WHERE forum_id = 0 and topic_id = 0 and categories_id = ?";
									
					PreparedStatement gradePrepStmnt = connection.prepareStatement(gradeModelSelectByCategoryId);
					gradePrepStmnt.setInt(1, exisCatgeoryId);
					
					ResultSet rsGrade = gradePrepStmnt.executeQuery();
					
					float gradePoints = 0f;
					
					if (rsGrade.next()) {
						gradePoints = rsGrade.getFloat("points");
					}
					rsGrade.close();
					gradePrepStmnt.close();
					
					createGrade(connection, toContextId, GRADE_BY_CATEGORY, 0, 0, categoryId, gradePoints);
				}
				
				
			} catch (SQLException e) {
				if (logger.isErrorEnabled()) logger.error("createCategory():Error occurred while creating category with title : "+ catName);
				e.printStackTrace();
				throw e;
			} 	
			
			return categoryId;
		
	}
	
	
	/**
	 * create jforum user
	 * @param connection
	 * @param username
	 * @param email
	 * @param firstName
	 * @param lastName
	 * @param sakaiUserId
	 * @return
	 * @throws Exception
	 */
	private int createJforumUser(Connection connection, String username, String email, 
							String firstName, String lastName, String sakaiUserId) throws SQLException {
		int jforumUserId = -1;
		
		try {
			PreparedStatement p = null;
			ResultSet rs = null;
			
			String addNewUser = null;
			if (sqlService.getVendor().equals("oracle")){
				addNewUser = "INSERT INTO jforum_users (user_id, username, user_password, " +
						"user_email, user_regdate, user_fname, user_lname, sakai_user_id) VALUES " +
						"(jforum_users_seq.nextval, ?, ?, ?,  SYSDATE, ?, ?, ?)";
				
				p = connection.prepareStatement(addNewUser);
				p.setString(1, username);
				p.setString(2, "password");
				p.setString(3, email);
				p.setString(4, firstName);
				p.setString(5, lastName);
				p.setString(6, sakaiUserId);
				
				p.executeUpdate();

				p.close();
				
				String categoryLastGeneratedCategoryId = "SELECT jforum_users_seq.currval  FROM DUAL";
				p = connection.prepareStatement(categoryLastGeneratedCategoryId);
				rs = p.executeQuery();
				
				if (rs.next()) {
					jforumUserId = rs.getInt(1);
				}
				
				rs.close();
				p.close();
			}else if (sqlService.getVendor().equalsIgnoreCase("mysql")){
				addNewUser = "INSERT INTO jforum_users (username, user_password, " +
				"user_email, user_regdate, user_fname, user_lname, " +
				"sakai_user_id) VALUES (?, ?, ?, NOW(), ?, ?, ?)";
				
				p = connection.prepareStatement(addNewUser, Statement.RETURN_GENERATED_KEYS);
				p.setString(1, username);
				p.setString(2, "password");
				p.setString(3, email);
				p.setString(4, firstName);
				p.setString(5, lastName);
				p.setString(6, sakaiUserId);
				
				p.executeUpdate();
									
				rs = p.getGeneratedKeys();
				if (rs.next()) {
					jforumUserId = rs.getInt(1);
				}
				rs.close();
				p.close();
			}
		} catch (SQLException e) {
			if (logger.isErrorEnabled()) logger.error("createJforumUser():Error occurred while creating user with username : "+ username);
			e.printStackTrace();
			throw e;
		} 	
		return jforumUserId;
	}
	
	
	/**
	 * Check the environment for catalina's base or home directory.
	 * 
	 * @return Catalina's base or home directory.
	 */
	protected String getCatalina(){
		String catalina = System.getProperty("catalina.base");
		if (catalina == null){
			catalina = System.getProperty("catalina.home");
		}

		return catalina;
	}
	
	/**
	 * save attachment file
	 * @param filename
	 * @param contentfile
	 * @throws Exception
	 */
	private void saveAttachmentFile(String filename, File contentfile) throws Exception
	{
		FileInputStream fis = null;
		FileOutputStream outputStream = null;
		
		try {
			fis = new FileInputStream(contentfile);
			outputStream = new FileOutputStream(filename);
			
			int c = 0;
			byte[] b = new byte[4096];
			while ((c = fis.read(b)) != -1) {
				outputStream.write(b, 0, c);
			}
		}catch (Exception e){
			if (logger.isWarnEnabled()) logger.warn(" JForum saveAttachmentFile : "+ e.toString());
			throw e;
		}
		finally {
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
			}
			
			if (fis != null) {
				fis.close();
			}
		}
	}
}