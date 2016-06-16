/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/admin/ImportExportAction.java $ 
 * $Id: ImportExportAction.java 69377 2010-07-23 23:24:46Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008, 2009 Etudes, Inc. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 * 
 * Portions completed before July 1, 2004 Copyright (c) 2003, 2004 Rafael Steil, All rights reserved, licensed under the BSD license. 
 * http://www.opensource.org/licenses/bsd-license.php 
 * 
 * Redistribution and use in source and binary forms, 
 * with or without modification, are permitted provided 
 * that the following conditions are met: 
 * 
 * 1) Redistributions of source code must retain the above 
 * copyright notice, this list of conditions and the 
 * following disclaimer. 
 * 2) Redistributions in binary form must reproduce the 
 * above copyright notice, this list of conditions and 
 * the following disclaimer in the documentation and/or 
 * other materials provided with the distribution. 
 * 3) Neither the name of "Rafael Steil" nor 
 * the names of its contributors may be used to endorse 
 * or promote products derived from this software without 
 * specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT 
 * HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL 
 * THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE 
 ***********************************************************************************/
package org.etudes.jforum.view.admin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.XPath;
import org.etudes.jforum.JForum;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.dao.AttachmentDAO;
import org.etudes.jforum.dao.CategoryDAO;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.ForumDAO;
import org.etudes.jforum.dao.PostDAO;
import org.etudes.jforum.dao.TopicDAO;
import org.etudes.jforum.entities.Attachment;
import org.etudes.jforum.entities.AttachmentExtension;
import org.etudes.jforum.entities.AttachmentInfo;
import org.etudes.jforum.entities.Category;
import org.etudes.jforum.entities.Forum;
import org.etudes.jforum.entities.Grade;
import org.etudes.jforum.entities.Post;
import org.etudes.jforum.entities.Topic;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.entities.UserSession;
import org.etudes.jforum.exceptions.AttachmentSizeTooBigException;
import org.etudes.jforum.exceptions.BadExtensionException;
import org.etudes.jforum.repository.ForumRepository;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.MD5;
import org.etudes.jforum.util.SafeHtml;
import org.etudes.jforum.util.legacy.commons.fileupload.FileItem;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.preferences.TemplateKeys;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.etudes.jforum.util.xml.XMLHelper;
import org.etudes.jforum.view.forum.common.ForumCommon;
import org.etudes.jforum.view.forum.common.TopicsCommon;
import org.etudes.jforum.view.forum.common.UploadUtils;
import org.etudes.util.XrefHelper;
import org.sakaiproject.authz.api.SecurityAdvisor;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.component.cover.ServerConfigurationService;
import org.sakaiproject.content.api.ContentResource;
import org.sakaiproject.content.cover.ContentHostingService;
import org.sakaiproject.entity.api.ResourceProperties;
import org.sakaiproject.entity.api.ResourcePropertiesEdit;
import org.sakaiproject.exception.IdInvalidException;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.IdUsedException;
import org.sakaiproject.exception.InconsistentException;
import org.sakaiproject.exception.OverQuotaException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.exception.ServerOverloadException;
import org.sakaiproject.exception.TypeException;
import org.sakaiproject.id.cover.IdManager;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.cover.SiteService;
import org.sakaiproject.tool.cover.ToolManager;
import org.sakaiproject.user.cover.UserDirectoryService;
import org.sakaiproject.util.Validator;
import org.xml.sax.SAXException;

public class ImportExportAction extends AdminCommand
{
	private static Log logger = LogFactory.getLog(ImportExportAction.class);
	/** default namespace and metadata namespace */
	protected String DEFAULT_NAMESPACE_URI = "http://www.imsglobal.org/xsd/imscp_v1p1";
	protected String IMSMD_NAMESPACE_URI = "http://www.imsglobal.org/xsd/imsmd_v1p2";

	final static int BUFFER = 2048;

	/**
	 * list the import export functions
	 */
	public void list() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}

		this.context.put("viewTitleImportExport", true);
		this.setTemplateName(TemplateKeys.IMPORT_EXPORT_LIST);
	}

	/**
	 * export the topics
	 * 
	 */
	public void exportTopics() throws Exception
	{
		if (logger.isDebugEnabled())
			logger.debug(this.getClass().getName() + ".exportTopics() : Entering....");

		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		// export the task topics
		try
		{
			exportTaskTopics();
		}
		catch (Exception e)
		{
			this.context.put("errormessage", I18n.getMessage("Export.Error"));
		}

		this.setTemplateName(TemplateKeys.IMPORT_EXPORT_LIST);

		if (logger.isDebugEnabled())
			logger.debug(this.getClass().getName() + ".exportTopics() : Exiting....");
	}

	/**
	 * import the topics
	 */
	public void importTopics() throws Exception
	{
		if (logger.isDebugEnabled())
			logger.debug(this.getClass().getName() + ".importTopics() : Entering....");
		
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		// import the task topics
		try
		{
			importTaskTopics();
		}
		catch (Exception e)
		{
			this.context.put("errormessage", I18n.getMessage("Import.Error"));
			if (logger.isErrorEnabled()) 
				logger.debug(this.getClass().getName() + ".importTopics() : "+ e.getMessage(), e);
		}
		this.setTemplateName(TemplateKeys.IMPORT_EXPORT_LIST);

		if (logger.isDebugEnabled())
			logger.debug(this.getClass().getName() + ".importTopics() : Exiting....");
	}

	/**
	 * Export the task topics according to imsglobal content packaging specs
	 * 1.1.4
	 * 
	 */
	private void exportTaskTopics() throws Exception
	{
		if (logger.isDebugEnabled())
			logger.debug(this.getClass().getName() + ".exportTaskTopics() : Entering....");

		try
		{
			// String packagingdirpath = SystemGlobals.getValue(ConfigKeys.PACKAGING_DIR);
			String packagingdirpath = SakaiSystemGlobals.getValue(ConfigKeys.PACKAGING_DIR);
			String user_id = SessionFacade.getUserSession().getUsername();
			// logger.info("ToolManager.getCurrentPlacement().getContext() : "+
			// ToolManager.getCurrentPlacement().getContext());
			// logger.info("ToolManager.getCurrentPlacement().getId() : "+
			// ToolManager.getCurrentPlacement().getId());
			String courseId = ToolManager.getCurrentPlacement().getContext();

			// get categories, forums with topics
			List catgForums = ForumCommon.getAllCategoriesAndForums(true);

			if (catgForums != null && catgForums.size() > 0)
			{
				File basePackDir = new File(packagingdirpath);
				if (!basePackDir.exists())
					basePackDir.mkdirs();

				String exisXmlFile = basePackDir.getAbsolutePath() + File.separator + "imsmanifest.xml";
				File manifestFile = new File(exisXmlFile);
				File packagedir = null;

				try
				{
					Element manifest = createManifestMetadata(manifestFile);

					String title = SiteService.getSite(ToolManager.getCurrentPlacement().getContext()).getTitle();

					packagedir = new File(basePackDir.getAbsolutePath() + File.separator 
							+ title.replaceAll("\\s+", "_"));
					// delete exisiting files
					deleteFiles(packagedir);

					if (!packagedir.exists())
						packagedir.mkdirs();

					// copy the schema files
					File schemaFilesDir = basePackDir;
					String imscp_v1p1 = schemaFilesDir.getAbsolutePath() + File.separator + "imscp_v1p1.xsd";
					File imscp_v1p1_File = new File(imscp_v1p1);
					createFile(imscp_v1p1_File.getAbsolutePath(), packagedir.getAbsolutePath() + File.separator + "imscp_v1p1.xsd");

					String imsmd_v1p2 = schemaFilesDir.getAbsolutePath() + File.separator + "imsmd_v1p2.xsd";
					File imsmd_v1p2_File = new File(imsmd_v1p2);
					createFile(imsmd_v1p2_File.getAbsolutePath(), packagedir.getAbsolutePath() + File.separator + "imsmd_v1p2.xsd");

					String imsxml = schemaFilesDir.getAbsolutePath() + File.separator + "xml.xsd";
					File xml_File = new File(imsxml);
					createFile(xml_File.getAbsolutePath(), packagedir.getAbsolutePath() + File.separator + "xml.xsd");

					List orgResElements = generateOrganizationResourceItems(catgForums, packagedir);
					Element manElement = null;
					if (orgResElements != null && orgResElements.size() > 0)
					{
						Iterator iter = orgResElements.iterator();
						while (iter.hasNext())
						{
							manElement = (Element) iter.next();
							manifest.add(manElement);
						}
					}

					// create xml document and add element
					Document document = XMLHelper.createXMLDocument(manifest);

					String newXmlFile = packagedir.getAbsolutePath() + File.separator + "imsmanifest.xml";
					// generate xml file
					XMLHelper.generateXMLFile(newXmlFile, document);

					// validate new manifest xml
					XMLHelper.parseFile(new File(newXmlFile));

					String outputfilename = packagedir.getParentFile().getAbsolutePath() + File.separator + title.replaceAll("\\s+", "_") + "_FORUMS"
							+ ".zip";
					File zipfile = new File(outputfilename);
					// create zip
					createZip(packagedir, zipfile);

					// download zip
					download(new File(zipfile.getAbsolutePath()));

					if (packagedir != null && packagedir.exists())
						deleteFiles(packagedir);

				}
				catch (Exception e)
				{
					throw e;
				}
				finally
				{
					// delete the files - Directory courseid_userid and
					// it's child
					/*
					 * if (packagedir != null && packagedir.exists())
					 * deleteFiles(packagedir .getParentFile());
					 */
				}
			}
			else
			{
				// add message for no task topics
			}
		}
		catch (Exception e)
		{
			if (logger.isErrorEnabled())
				logger.error(this.getClass().getName() + " : " + e.toString(), e);

			throw e;
		}

		if (logger.isDebugEnabled())
			logger.debug(this.getClass().getName() + ".exportTaskTopics() : Exiting....");
	}

	/**
	 * Import task topics to site's JForum tool
	 * 
	 * @throws Exception
	 */
	private void importTaskTopics() throws Exception
	{
		if (logger.isDebugEnabled())
			logger.debug("Entering importTaskTopics....");

		FileItem fileItem = (FileItem) this.request.getObjectParameter("fileimporttopics");

		if (fileItem == null)
		{
			return;
		}

		if (fileItem.getName().indexOf('\000') > -1)
		{
			if (logger.isWarnEnabled())
				logger.warn("Possible bad attachment (null char): " + fileItem.getName() + " - user_id: "
						+ SessionFacade.getUserSession().getUserId());
			return;
		}

		if (fileItem != null)
		{
			String uploadedFileName = fileItem.getName();
			if (uploadedFileName != null && uploadedFileName.trim().length() > 0)
			{
				if (uploadedFileName.lastIndexOf('.') != -1
						&& uploadedFileName.substring(uploadedFileName.lastIndexOf('.') + 1).equalsIgnoreCase("zip"))
				{

					File unpackagedir = null;
					try
					{
						//String packagingdirpath = SystemGlobals.getValue(ConfigKeys.PACKAGING_DIR);
						String packagingdirpath = SakaiSystemGlobals.getValue(ConfigKeys.PACKAGING_DIR);

						String user_id = SessionFacade.getUserSession().getUsername();
						String courseId = ToolManager.getCurrentPlacement().getContext();

						File basePackDir = new File(packagingdirpath);
						if (!basePackDir.exists())
							basePackDir.mkdirs();
						String title = SiteService.getSite(ToolManager.getCurrentPlacement().getContext()).getTitle();

						unpackagedir = new File(basePackDir.getAbsolutePath() + File.separator + "import" + File.separator + courseId + "_" + user_id
								+ File.separator + title.replace(' ', '_'));
						// delete exisiting files
						deleteFiles(unpackagedir);

						if (!unpackagedir.exists())
							unpackagedir.mkdirs();

						String actFileName;
						// write the uploaded zip file to disk
						if (uploadedFileName.indexOf('\\') != -1)
							actFileName = uploadedFileName.substring(uploadedFileName.lastIndexOf('\\') + 1);
						else
							actFileName = uploadedFileName.substring(uploadedFileName.lastIndexOf('/') + 1);
						// write file to disk
						File outputFile = new File(unpackagedir + File.separator + actFileName);
						writeFileToDisk(fileItem, outputFile);

						// unzipping dir
						File unzippeddir = new File(unpackagedir.getAbsolutePath() + File.separator
								+ actFileName.substring(0, actFileName.lastIndexOf('.') + 1));
						if (!unzippeddir.exists())
							unzippeddir.mkdirs();
						String unzippedDirpath = unzippeddir.getAbsolutePath();
						// unzip files
						unZipFile(outputFile, unzippedDirpath);

						File imsmanifest = new File(unzippedDirpath + File.separator + "imsmanifest.xml");
						// validatemanifest(imsmanifest);

						Document document = XMLHelper.parseFile(imsmanifest);

						parseAndCreateTopics(document, unzippeddir.getAbsolutePath());
					}
					catch (Exception e)
					{
						if (logger.isErrorEnabled())
							logger.error(this.getClass().getName() + ": Error while importing...:" + e.toString());
						throw e;
					}
					finally
					{
						// delete the files - Directory courseid_userid and
						// it's child
						if (unpackagedir != null && unpackagedir.exists())
							deleteFiles(unpackagedir.getParentFile());
					}
					// success message
					this.context.put("successmessage", I18n.getMessage("Import.success"));

				}
				else
				{
					// add message as file is not a zip file
					if (logger.isWarnEnabled())
						logger.warn("Not a zip file...");
					this.context.put("errormessage", I18n.getMessage("Import.CorrectFileFormat"));
				}

			}
			else
			{
				if (logger.isWarnEnabled())
					logger.warn("Possible bad attachment (null char): " + fileItem.getName() + " - user_id: "
							+ SessionFacade.getUserSession().getUserId());
			}
		}

		if (logger.isDebugEnabled())
			logger.debug("Exiting importTaskTopics....");
	}

	/**
	 * Generate organization and resuore items
	 * 
	 * @param catgForums
	 *            - category list
	 * @param packagedir
	 *            - packaging directory
	 * @return list of organization and resource items
	 * @throws Exception
	 */
	private List generateOrganizationResourceItems(List catgForums, File packagedir) throws Exception
	{

		try
		{
			String packagedirpath = packagedir.getAbsolutePath();
			String resourcespath = packagedirpath + File.separator + "resources";
			File resoucesDir = new File(resourcespath);
			if (!resoucesDir.exists())
				resoucesDir.mkdir();

			Element organizations = createOrganizations();
			Element resources = createResources();

			Element organization = addOrganization(organizations);

			organizations.addAttribute("default", organization.attributeValue("identifier"));

			Iterator catgForumsIter = catgForums.iterator();
			Category category = null;
			int i = 0, j = 0, k = 0;
			// create item for each category and items under the category item
			// for
			// forums and items for task topics under forum items
			while (catgForumsIter.hasNext())
			{
				category = (Category) catgForumsIter.next();

				if (category.isArchived())
					continue;

				// add category element
				Element catMainItem = organization.addElement("item");
				catMainItem.addAttribute("identifier", "MF01_ORG1_JFORUM_CATG" + ++i);
				
				// add category grade if category is gradable
				StringBuffer categoryParameters = new StringBuffer();
				
				if (category.isGradeCategory())
				{
					categoryParameters.append("&gradetype=");
					categoryParameters.append(Forum.GRADE_BY_CATEGORY);
	
					Grade catGrade = DataAccessDriver.getInstance().newGradeDAO().selectByCategoryId(category.getId());
					categoryParameters.append("&gradepoints=");
					categoryParameters.append(catGrade.getPoints().toString());
				}
				
				if (category.getStartDate() != null) {
					categoryParameters.append("&startdate=");
					SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.CALENDAR_DATE_TIME_FORMAT));
					String startDateStr = df.format(category.getStartDate());
					  
					categoryParameters.append(startDateStr);
				}
				
				if (category.getEndDate() != null) {
					categoryParameters.append("&enddate=");
					
					SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.CALENDAR_DATE_TIME_FORMAT));
					String endDateStr = df.format(category.getEndDate());
					
					categoryParameters.append(endDateStr);
				}
				
				if (categoryParameters.length() > 0)
				{
					String catgParams = categoryParameters.toString();
					catMainItem.addAttribute("parameters", catgParams.substring(1));
				}
				

				Element title = catMainItem.addElement("title");
				if (category.getName() != null && category.getName().trim().length() > 0)
					title.setText(category.getName());
				
				Collection forums = (Collection) category.getForums();
				if (forums != null && forums.size() > 0)
				{
					Iterator forumIter = forums.iterator();
					Forum forum = null;
					// create items and resources for forum and it's task topics
					while (forumIter.hasNext())
					{
						forum = (Forum) forumIter.next();

						// add forum element
						Element forumElement = null;
						forumElement = catMainItem.addElement("item");
						forumElement.addAttribute("identifier", "FORUMITEM" + ++k);
						// add attribute "parameters" for forum type and access
						// type
						StringBuffer forumParameters = new StringBuffer();
						
						forumParameters.append("forumtype=");
						forumParameters.append(forum.getType());
						forumParameters.append("&accesstype=");
						if (forum.getAccessType() == Forum.ACCESS_GROUPS)
							forumParameters.append("0");
						else
							forumParameters.append(forum.getAccessType());

						if (forum.getGradeType() == Forum.GRADE_BY_FORUM)
						{
							Grade grade = DataAccessDriver.getInstance().newGradeDAO().selectByForumId(forum.getId());

							if (grade != null)
							{
								forumParameters.append("&gradetype=");
								forumParameters.append(forum.getGradeType());

								forumParameters.append("&gradepoints=");
								forumParameters.append(grade.getPoints().toString());
							}
						}
						else if (forum.getGradeType() == Forum.GRADE_BY_TOPIC)
						{
							forumParameters.append("&gradetype=");
							forumParameters.append(forum.getGradeType());
						}
						
						if (forum.getStartDate() != null) {
							forumParameters.append("&startdate=");
							SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.CALENDAR_DATE_TIME_FORMAT));
							String startDateStr = df.format(forum.getStartDate());
							  
							forumParameters.append(startDateStr);
						}
						
						if (forum.getEndDate() != null) {
							forumParameters.append("&enddate=");
							
							SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.CALENDAR_DATE_TIME_FORMAT));
							String endDateStr = df.format(forum.getEndDate());
							
							forumParameters.append(endDateStr);
						}

						forumElement.addAttribute("parameters", forumParameters.toString());
						Element forumTitleEle = forumElement.addElement("title");
						forumTitleEle.setText(forum.getName());

						TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
						List topics = tm.selectAllByForum(forum.getId());

						/* topics */
						Topic topic = null;
						if (topics != null && topics.size() > 0)
						{
							Iterator topidIter = topics.iterator();

							// create items and resources for task topics
							while (topidIter.hasNext())
							{
								topic = (Topic) topidIter.next();
								/*
								 * 10/16/08 Murthy Topic type 1 was task topic.
								 * As task topic is changed to export topic task
								 * topic support code is added add topic element
								 * for exportable topic.
								 */
								if (topic.isExportTopic() || topic.getType() == 1)
								{
									if (forumElement != null)
									{
										Element topicElement = forumElement.addElement("item");
										topicElement.addAttribute("identifier", "TOPICITEM" + ++k);

										// params
										StringBuffer topicParameters = new StringBuffer();
										topicParameters.append("topictype=");
										/*
										 * 10/16/08 Murthy Topic type 1 was task
										 * topic. As task topic is changed to
										 * export topic task topic support code
										 * is added
										 */
										if (topic.getType() == 1)
											topicParameters.append(0);
										else
											topicParameters.append(topic.getType());

										if (forum.getGradeType() == Forum.GRADE_BY_TOPIC && topic.isGradeTopic())
										{
											Grade topicGrade = DataAccessDriver.getInstance().newGradeDAO().selectByForumTopicId(forum.getId(),
													topic.getId());

											if (topicGrade != null)
											{
												topicParameters.append("&gradetopic=");
												topicParameters.append(Topic.GRADE_YES);

												topicParameters.append("&gradepoints=");
												topicParameters.append(topicGrade.getPoints().toString());
											}
										}

										topicElement.addAttribute("parameters", topicParameters.toString());

										Element topicTitleEle = topicElement.addElement("title");
										topicTitleEle.setText(topic.getTitle());

										int firstPostId = topic.getFirstPostId();
										PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
										Post p = pm.selectById(firstPostId);

										/* add topic resource */
										Element topicResource = resources.addElement("resource");
										topicResource.addAttribute("identifier", "RESOURCE" + ++j);
										topicResource.addAttribute("type ", "webcontent");

										topicElement.addAttribute("identifierref", topicResource.attributeValue("identifier"));

										// create the file for first post text
										File resfile = new File(resoucesDir + "/post_" + p.getId() + ".html");

										// process cross references. harvest post text embedded references
										Set<String> refs = XrefHelper.harvestEmbeddedReferences(p.getText(), null);

										if (logger.isDebugEnabled())
											logger.debug("processTopic(): embed references found:" + refs.toString());
										
										String modifiedText = null;
										
										if (refs != null && refs.size() > 0)
										{
											modifiedText = updateEmbeddedReferenceUrls(p.getText(), refs, resoucesDir);
											
											createFileFromContent(modifiedText, resfile.getAbsolutePath());
										}
										else
											createFileFromContent(p.getText(), resfile.getAbsolutePath());
										
										Element file = topicResource.addElement("file");
										file.addAttribute("href", "resources/post_" + p.getId() + ".html");
										topicResource.addAttribute("href", "resources/post_" + p.getId() + ".html");

										// create file for attachments if any
										// with first post
										if (p.hasAttachments())
										{
											List attachments = DataAccessDriver.getInstance().newAttachmentDAO().selectAttachments(p.getId());

											if (attachments != null && attachments.size() > 0)
											{
												for (Iterator iter = attachments.iterator(); iter.hasNext();)
												{
													Attachment attachment = (Attachment) iter.next();
													String realFileName = attachment.getInfo().getRealFilename().replaceAll("\\s+", "_");
													File resAttachfile = new File(resoucesDir + "/post_" + p.getId() + "_" + realFileName);

													// String fileLoc = SystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR) + "/"
													String fileLoc = SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR) + "/"
															+ attachment.getInfo().getPhysicalFilename();

													if (!new File(fileLoc).exists())
													{
														if (logger.isErrorEnabled())
															logger.error(this.getClass().getName() + ". generateOrganizationResourceItems() : "
																	+ fileLoc + " doesn't exist.");
														continue;
													}
													createFile(fileLoc, resAttachfile.getAbsolutePath());

													Element attchFileEle = topicResource.addElement("file");

													// file description
													String attchmentComment = attachment.getInfo().getComment();
													if (attchmentComment != null && attchmentComment.trim().length() > 0)
													{
														Element imsmdlom = createLOMElement("imsmd:lom", "lom");
														Element imsmdgeneral = imsmdlom.addElement("imsmd:general");
														imsmdgeneral.add(createMetadataDescription(attchmentComment.trim()));
														attchFileEle.add(imsmdlom);
													}

													attchFileEle.addAttribute("href", "resources/post_" + p.getId() + "_" + realFileName);
												}
											}
										}

										/*
										 * add meta data to topic resource
										 * element - for topic subject - as
										 * metadata title - subject is same as
										 * topic title
										 */
										if (p.getSubject() != null && p.getSubject().trim().length() > 0)
										{
											Element imsmdlom = createLOMElement("imsmd:lom", "lom");
											Element imsmdgeneral = imsmdlom.addElement("imsmd:general");
											imsmdgeneral.add(createMetadataTitle(p.getSubject()));
											topicResource.add(imsmdlom);
										}
									}
								}
							}
						}

						// metadata to capture forum description
						if (forum.getDescription() != null && forum.getDescription().trim().length() > 0)
						{
							Element imsmdlom = createLOMElement("imsmd:lom", "lom");
							Element imsmdgeneral = imsmdlom.addElement("imsmd:general");
							imsmdgeneral.add(createMetadataDescription(forum.getDescription().trim()));
							forumElement.add(imsmdlom);
						}
					}

				}
			}
			ArrayList manElements = new ArrayList();
			manElements.add(organizations);
			manElements.add(resources);

			return manElements;

		}
		catch (Exception e)
		{
			throw e;
		}
	}

	/**
	 * update embedded reference urls
	 * 
	 * @param postText
	 *            - post text
	 * @param refs
	 *            - references
	 * @param resoucesDir
	 *            - resource directory
	 * @return modified post text
	 */
	private String updateEmbeddedReferenceUrls(String postText, Set<String> refs, File resoucesDir)
	{
		for (String ref : refs)
		{
			String refPathWithFolders;
			String siteId;

			// handle urls that has /content/group/
			if (ref.startsWith("/content/group/"))
			{
				refPathWithFolders = ref.substring("/content/group/".length());

				siteId = refPathWithFolders.substring(0, refPathWithFolders.indexOf("/"));
				refPathWithFolders = refPathWithFolders.substring(refPathWithFolders.indexOf("/"));
				
				if (logger.isDebugEnabled()) logger.debug("updateEmbeddedReferenceUrls() : refPathWithFolders : "+ refPathWithFolders);
				
				postText = parseAndReplacePostTextUrls(postText, ref);

				// html file - save the file as per updated relative reference path(embeded_jf_content/content/group + refPathWithFolders)
				try
				{

					ContentResource resource;

					// get the resource
					String id = ref.substring("/content".length());
					resource = ContentHostingService.getResource(id);

					if (resource != null)
					{
						String type = resource.getContentType();

						// create the file for the reference
						if (type.equals("text/html"))
						{
							String resourceId = resource.getId();
							String resourcePath = resourceId.substring(resourceId.indexOf(siteId) + siteId.length());
							
							// create folders
							// String refPath = refPathWithFolders.substring(0, refPathWithFolders.lastIndexOf("/"));
							String refPath = resourcePath.substring(0, resourcePath.lastIndexOf("/"));
							if (logger.isDebugEnabled()) logger.debug("updateEmbeddedReferenceUrls() : refPath : "+ refPath);
							
							File refPathFolder = new File(resoucesDir + "/embeded_jf_content/content/group" + refPath);
							if (!refPathFolder.exists())
								refPathFolder.mkdirs();

							// get file name
							// String fileName = refPathWithFolders.substring(refPathWithFolders.lastIndexOf("/") + 1);
							String fileName = resourceId.substring(resourceId.lastIndexOf("/") + 1);

							File resfile = new File(refPathFolder + "/" + fileName);

							// change the path of embedded refs
							String bodyString = new String(resource.getContent(), "UTF-8");
							String modifiedBody = updateReferencePaths(bodyString, resoucesDir, refPath);
							
							createFileFromContent(modifiedBody, resfile.getAbsolutePath());
						}
						else
						{
							String resourceId = resource.getId();
							String resourcePath = resourceId.substring(resourceId.indexOf(siteId) + siteId.length());
							
							// create folders
							// String refPath = refPathWithFolders.substring(0, refPathWithFolders.lastIndexOf("/"));
							String refPath = resourcePath.substring(0, resourcePath.lastIndexOf("/"));
							if (logger.isDebugEnabled()) logger.debug("updateEmbeddedReferenceUrls() : refPath : "+ refPath);
							
							File refPathFolder = new File(resoucesDir + "/embeded_jf_content/content/group" + refPath);
							if (!refPathFolder.exists())
								refPathFolder.mkdirs();

							// get file name
							// String fileName = refPathWithFolders.substring(refPathWithFolders.lastIndexOf("/") + 1);
							
							String fileName = resourceId.substring(resourceId.lastIndexOf("/") + 1);
							
							if (logger.isDebugEnabled()) logger.debug("updateEmbeddedReferenceUrls() : fileName : "+ fileName);

							File resfile = new File(refPathFolder + "/" + fileName);

							createFileFromContent(resource.getContent(), resfile.getAbsolutePath());
						}
					}
				}
				catch (PermissionException e)
				{
					if (logger.isWarnEnabled())
						logger.warn("updateEmbeddedReferenceUrls: " + e.toString());
				}
				catch (IdUnusedException e)
				{
					if (logger.isWarnEnabled())
						logger.warn("updateEmbeddedReferenceUrls: " + e.toString());
				}
				catch (TypeException e)
				{
					if (logger.isWarnEnabled())
						logger.warn("updateEmbeddedReferenceUrls: " + e.toString());
				}
				catch (ServerOverloadException e)
				{
					if (logger.isWarnEnabled())
						logger.warn("updateEmbeddedReferenceUrls: " + e.toString());
				}
				catch (Exception e)
				{
					if (logger.isWarnEnabled())
						logger.warn("updateEmbeddedReferenceUrls: " + e.toString());
				}
			}
		} 
		
		return postText;
	}

	/**
	 * creates manifest metadata - course title and description
	 * 
	 * @param manifestFile
	 *            - manifest file
	 * @return - the manifest element
	 * @throws Exception
	 */
	private Element createManifestMetadata(File manifestFile) throws Exception
	{
		try
		{

			Element manifest = getManifest(manifestFile);

			// create manifest metadata
			Element manifestMetadata = createManifestMetadata();

			// imsmd:lom
			Element imsmdlom = createLOMElement("imsmd:lom", "lom");
			// imsmd:general
			Element imsmdgeneral = createLOMElement("imsmd:general", "general");

			// title
			Site sakSite = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
			String title = sakSite.getTitle();

			if (title != null)
			{
				Element metadataTitle = createMetadataTitle(title);
				imsmdgeneral.add(metadataTitle);
			}

			// description
			String description = sakSite.getDescription();
			if (description != null)
			{
				Element metadataDesc = createMetadataDescription(description);
				imsmdgeneral.add(metadataDesc);
			}

			imsmdlom.add(imsmdgeneral);
			manifestMetadata.add(imsmdlom);

			manifest.add(manifestMetadata);

			return manifest;

		}
		catch (Exception e)
		{
			throw e;
		}
	}

	/**
	 * creates document root element "manifest" from the default manifest file
	 * and adds the namespaces
	 * 
	 * @param xmlFile
	 *            - Default manifest file
	 * @return returns the manifest element
	 * @throws Exception
	 */
	private Element getManifest(File xmlFile) throws Exception
	{
		try
		{
			Document document = XMLHelper.getSaxReader().read(xmlFile);
			Element root = document.getRootElement();
			Element rootnew = root.createCopy();
			List childEleList = rootnew.elements();
			childEleList.clear();

			this.DEFAULT_NAMESPACE_URI = rootnew.getNamespaceURI();

			List nslist = rootnew.declaredNamespaces();

			for (int i = 0; i < nslist.size(); i++)
			{
				if (((Namespace) nslist.get(i)).getPrefix().equals("imsmd"))
				{
					this.IMSMD_NAMESPACE_URI = ((Namespace) nslist.get(i)).getURI();
					break;
				}
			}
			rootnew.addAttribute("identifier", "Manifest-" + IdManager.createUuid());
			return rootnew;
		}
		catch (DocumentException de)
		{
			throw de;
		}
		catch (SAXException se)
		{
			throw se;
		}
		catch (Exception e)
		{
			throw e;
		}
	}

	/**
	 * create manifest metadata element with schema and schemaversion elements
	 * 
	 * @return - returns metadata element
	 */
	private Element createManifestMetadata()
	{
		Element metadata = createDefaultNSElement("metadata", "metadata");

		// schema element
		Element schema = createDefaultNSElement("schema", "schema");
		schema.setText("IMS Content");
		metadata.add(schema);

		// schema version element
		Element schemaVersion = createDefaultNSElement("schemaversion", "schemaversion");
		schemaVersion.setText("1.1.4");
		metadata.add(schemaVersion);

		return metadata;
	}

	/**
	 * creates the default namespace element
	 * 
	 * @param elename
	 *            - element name
	 * @param qname
	 *            - qualified name
	 * @return - returns the default namespace element
	 */
	private Element createDefaultNSElement(String elename, String qname)
	{
		Element metadata = DocumentHelper.createElement(elename);
		metadata.setQName(new QName(qname, new Namespace(null, DEFAULT_NAMESPACE_URI)));
		return metadata;
	}

	/**
	 * creates the LOM metadata element
	 * 
	 * @param elename
	 *            - element name
	 * @param qname
	 *            - qualified name
	 * @return - returns the metadata element
	 */
	private Element createLOMElement(String elename, String qname)
	{

		Element imsmdlom = DocumentHelper.createElement(elename);
		imsmdlom.setQName(new QName(qname, new Namespace("imsmd", IMSMD_NAMESPACE_URI)));

		return imsmdlom;
	}

	/**
	 * creates metadata title element
	 * 
	 * @param title
	 *            - title
	 * @return - returns the title element
	 */
	private Element createMetadataTitle(String title)
	{
		// imsmd:title
		Element imsmdtitle = createLOMElement("imsmd:title", "title");

		// imsmd:langstring
		Element imsmdlangstring = createLOMElement("imsmd:langstring", "langstring");
		// imsmdlangstring.addAttribute("xml:lang", "en-US");
		imsmdlangstring.setText(title);

		imsmdtitle.add(imsmdlangstring);

		return imsmdtitle;
	}

	/**
	 * creates metadata description element
	 * 
	 * @param description
	 *            - description
	 * @return - returns the metadata description element
	 */
	public Element createMetadataDescription(String description)
	{
		// imsmd:description
		Element mdDesc = createLOMElement("imsmd:description", "description");

		// imsmd:langstring
		Element mdLangString = createLOMElement("imsmd:langstring", "langstring");
		// mdLangString.addAttribute("xml:lang", "en-US");
		mdLangString.setText(description);

		mdDesc.add(mdLangString);

		return mdDesc;
	}

	/**
	 * deletes the file and its children
	 * 
	 * @param delfile
	 *            - file to be deleted
	 */
	private void deleteFiles(File delfile)
	{

		if (delfile.isDirectory())
		{
			File files[] = delfile.listFiles();
			int i = files.length;
			while (i > 0)
				deleteFiles(files[--i]);

			delfile.delete();
		}
		else
			delfile.delete();

	}

	/**
	 * creates file from input path to output path
	 * 
	 * @param inputpath
	 *            - input path for file
	 * @param outputpath
	 *            - output path for file
	 * @throws Exception
	 */
	public void createFile(String inputurl, String outputurl) throws Exception
	{
		FileInputStream in = null;
		FileOutputStream out = null;
		try
		{
			File inputFile = new File(inputurl);
			File outputFile = new File(outputurl);
			in = new FileInputStream(inputFile);
			out = new FileOutputStream(outputFile);
			int len;
			byte buf[] = new byte[102400];
			while ((len = in.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
		}
		catch (FileNotFoundException e)
		{
			if (logger.isErrorEnabled())
				logger.error(e.toString());
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				if (in != null)
					in.close();
			}
			catch (IOException e1)
			{
			}
			try
			{
				if (out != null)
					out.close();
			}
			catch (IOException e2)
			{
			}
		}
	}

	/**
	 * creates organizations element
	 * 
	 * @return returns organizations element
	 */
	private Element createOrganizations()
	{
		return createDefaultNSElement("organizations", "organizations");
	}

	/**
	 * creates resources element
	 * 
	 * @return returns resources element
	 */
	private Element createResources()
	{

		return createDefaultNSElement("resources", "resources");
	}

	/**
	 * add organization for JForum
	 * 
	 * @param organizations
	 *            - organizations element
	 */
	private Element addOrganization(Element organizations)
	{
		Element organization = organizations.addElement("organization");
		organization.addAttribute("identifier", "JF01_ORG1_JFORUM");
		organization.addAttribute("structure", "hierarchical");

		return organization;
	}

	/**
	 * creates zip file
	 * 
	 * @param inputFile
	 *            - input directory that is to be zipped
	 * @param outputfile
	 *            - out zip file
	 * @throws Exception
	 */
	private void createZip(File inputFile, File outputfile) throws Exception
	{
		ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(outputfile));
		writeToZip(inputFile, zout, inputFile.getName());
		zout.close();
	}

	/**
	 * @param inputFile
	 *            - file to zip
	 * @param zout
	 *            - zip output stream
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void writeToZip(File inputFile, ZipOutputStream zout, String baseFileName) throws FileNotFoundException, IOException
	{
		File files[] = inputFile.listFiles();
		for (int i = 0; i < files.length; i++)
		{
			if (files[i].isFile())
			{
				FileInputStream in = new FileInputStream(files[i]);

				String name = null;
				String filepath = files[i].getAbsolutePath();
				name = filepath.substring(filepath.indexOf(baseFileName) + baseFileName.length() + 1);
				zout.putNextEntry(new ZipEntry(name));

				// Transfer bytes from the file to the ZIP file
				int len;
				byte buf[] = new byte[102400];
				while ((len = in.read(buf)) > 0)
				{
					zout.write(buf, 0, len);
				}
				zout.closeEntry();
				in.close();
			}
			else
			{
				writeToZip(files[i], zout, baseFileName);
			}
		}
	}

	/**
	 * writes the zip file to browser
	 * 
	 * @param file
	 *            - zip file to download
	 * @throws Exception
	 */
	private void download(File file) throws Exception
	{
		FileInputStream fis = null;
		ServletOutputStream out = null;
		try
		{
			String disposition = "attachment; filename=\"" + file.getName() + "\"";
			fis = new FileInputStream(file);

			response.setContentType("application/zip"); // application/zip
			response.addHeader("Content-Disposition", disposition);

			out = response.getOutputStream();

			int len;
			byte buf[] = new byte[102400];
			while ((len = fis.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}

			out.flush();
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				if (out != null)
					out.close();
			}
			catch (IOException e1)
			{
			}

			try
			{
				if (fis != null)
					fis.close();
			}
			catch (IOException e2)
			{
			}
		}
	}

	/**
	 * creates file from input path to output path
	 * 
	 * @param inputpath
	 *            - input path for file
	 * @param outputpath
	 *            - output path for file
	 * @throws Exception
	 */
	public void createFileFromContent(String content, String outputurl) throws Exception
	{
		PrintWriter out = null;
		try
		{
			out = new PrintWriter(new BufferedWriter(new FileWriter(outputurl)));
			out.write(content);
			out.flush();
		}
		catch (IOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (out != null)
				out.close();
		}
	}

	/**
	 * creates file from input path to output path
	 * 
	 * @param inputpath
	 *            - input path for file
	 * @param outputpath
	 *            - output path for file
	 * @throws Exception
	 */
	public void createFileFromContent(byte[] content, String outputurl) throws Exception
	{
		FileOutputStream fout = new FileOutputStream(new File(outputurl));
		try
		{
			fout.write(content);
			fout.flush();
		}
		catch (IOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw e;
		}
		finally
		{
			if (fout != null)
				fout.close();
		}
	}

	/**
	 * writes the file to disk
	 * 
	 * @param fileItem
	 *            Fileitem
	 * @param outputFile
	 *            out put file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void writeFileToDisk(FileItem fileItem, File outputFile) throws FileNotFoundException, IOException
	{
		BufferedInputStream inputStream = null;
		FileOutputStream out = null;
		try
		{
			inputStream = new BufferedInputStream(fileItem.getInputStream());
			out = new FileOutputStream(outputFile);

			int len;
			byte buf[] = new byte[102400];
			while ((len = inputStream.read(buf)) > 0)
			{
				out.write(buf, 0, len);
			}
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				if (inputStream != null)
					inputStream.close();
			}
			catch (IOException e1)
			{
			}
			try
			{
				if (out != null)
					out.close();
			}
			catch (IOException e2)
			{
			}
		}
	}

	/**
	 * unzip the file and write to disk
	 * 
	 * @param zipfile
	 * @param dirpath
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void unZipFile(File zipfile, String dirpath) throws Exception
	{
		FileInputStream fis = new FileInputStream(zipfile);
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		ZipEntry entry;
		while ((entry = zis.getNextEntry()) != null)
		{
			if (entry.isDirectory())
			{

			}
			else if (entry.getName().lastIndexOf('\\') != -1)
			{
				String filenameincpath = entry.getName();

				String actFileNameIncPath = dirpath;

				while (filenameincpath.indexOf('\\') != -1)
				{
					String subFolName = filenameincpath.substring(0, filenameincpath.indexOf('\\'));

					File subfol = new File(actFileNameIncPath + File.separator + subFolName);
					if (!subfol.exists())
						subfol.mkdirs();

					actFileNameIncPath = actFileNameIncPath + File.separator + subFolName;

					filenameincpath = filenameincpath.substring(filenameincpath.indexOf('\\') + 1);
				}

				String filename = entry.getName().substring(entry.getName().lastIndexOf('\\') + 1);
				unzip(zis, actFileNameIncPath + File.separator + filename);
			}
			else if (entry.getName().lastIndexOf('/') != -1)
			{
				String filenameincpath = entry.getName();

				String actFileNameIncPath = dirpath;

				while (filenameincpath.indexOf('/') != -1)
				{
					String subFolName = filenameincpath.substring(0, filenameincpath.indexOf('/'));
					File subfol = new File(actFileNameIncPath + File.separator + subFolName);
					if (!subfol.exists())
						subfol.mkdirs();

					actFileNameIncPath = actFileNameIncPath + File.separator + subFolName;

					filenameincpath = filenameincpath.substring(filenameincpath.indexOf('/') + 1);
				}

				String filename = entry.getName().substring(entry.getName().lastIndexOf('/') + 1);
				unzip(zis, actFileNameIncPath + File.separator + filename);
			}
			else
				unzip(zis, dirpath + File.separator + entry.getName());
		}
		zis.close();
	}

	/**
	 * write zip file to disk
	 * 
	 * @param zis
	 * @param name
	 * @throws IOException
	 */
	private void unzip(ZipInputStream zis, String name) throws Exception
	{
		BufferedOutputStream dest = null;
		int count;
		byte data[] = new byte[BUFFER];
		try
		{
			// write the files to the disk
			FileOutputStream fos = new FileOutputStream(name);
			dest = new BufferedOutputStream(fos, BUFFER);
			while ((count = zis.read(data, 0, BUFFER)) != -1)
			{
				dest.write(data, 0, count);
			}
			dest.flush();
		}
		catch (FileNotFoundException e)
		{
			throw e;
		}
		catch (IOException e)
		{
			throw e;
		}
		finally
		{
			try
			{
				if (dest != null)
					dest.close();
			}
			catch (IOException e1)
			{
			}
		}
	}

	/**
	 * Parses the manifest and build topics
	 * 
	 * @param document
	 *            document
	 * @param unZippedDirPath
	 *            unZipped files Directory Path
	 * @exception throws exception
	 */
	public void parseAndCreateTopics(Document document, String unZippedDirPath) throws Exception
	{
		if (logger.isDebugEnabled())
			logger.debug("Entering parseAndCreateTopics");

		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		// get current site
		Site site = SiteService.getSite(ToolManager.getCurrentPlacement().getContext());
		Collection sakaiSiteGroups = site.getGroups();

		Map uris = new HashMap();
		uris.put("imscp", DEFAULT_NAMESPACE_URI);
		uris.put("imsmd", IMSMD_NAMESPACE_URI);

		// organizations
		XPath xpath = document.createXPath("/imscp:manifest/imscp:organizations/imscp:organization");
		xpath.setNamespaceURIs(uris);

		Element eleOrg = (Element) xpath.selectSingleNode(document);

		// create category, forum if not existing and then add task topics of
		// the forum
		// loop thru organization elements - item elements(category)
		List elements = eleOrg.elements();
		for (Iterator iter = elements.iterator(); iter.hasNext();)
		{
			Element element = (Element) iter.next();
			if (element.getName().equalsIgnoreCase("item"))
			{
				processXML(element, document, unZippedDirPath);
			}

		}

		if (logger.isDebugEnabled())
			logger.debug("Exiting parseAndCreateTopics");
	}

	/**
	 * create category, forum if not existing and then add task topics of the
	 * forum
	 * 
	 * @param element
	 * @param document
	 */
	private void processXML(Element eleCatItem, Document document, String unZippedDirPath) throws Exception
	{
		if (logger.isDebugEnabled())
			logger.debug("Entering processXML...");

		List eleCatTitles = (List) eleCatItem.elements("title");
		if (eleCatTitles == null || eleCatTitles.size() == 0)
			return;
		// If category is not existing create the category
		Element titleEle = (Element) eleCatItem.elements("title").get(0);

		// get existing categories, forums with topics
		List exisCatgForums = ForumCommon.getAllCategoriesAndForums(true);
		Category category = null;
		boolean blnCategoryExisting = false;

		for (Iterator catIter = exisCatgForums.iterator(); catIter.hasNext();)
		{
			category = (Category) catIter.next();

			/*
			 * create category if not existing - select the first topic if
			 * mutiple topics exist with same name/title
			 */
			if (titleEle.getText() != null && titleEle.getText().trim().length() > 0)
				if (titleEle.getText().trim().equalsIgnoreCase(category.getName()))
				{
					blnCategoryExisting = true;
					break;
				}
		}

		if (blnCategoryExisting)
		{
			// forums
			Collection forums = (Collection) category.getManageForums();
			if (forums != null && forums.size() > 0)
			{
				Map ExisForumMap = new HashMap();

				// existing forums
				for (Iterator forumIter = forums.iterator(); forumIter.hasNext();)
				{
					Forum forum = (Forum) forumIter.next();
					// if forum with same name exists the last one is added to
					// the map
					ExisForumMap.put(forum.getName(), forum);
				}

				// process forum elements
				List forumItems = eleCatItem.elements("item");
				for (Iterator iter = forumItems.iterator(); iter.hasNext();)
				{
					Element forumEle = (Element) iter.next();

					Element forumTitleEle = (Element) forumEle.elements("title").get(0);
					if (forumTitleEle.getText() != null && forumTitleEle.getText().trim().length() > 0)
					{
						if (ExisForumMap.containsKey(forumTitleEle.getText()))
						{
							// forum is existing. create not existing task
							// topics for the forum
							Forum exisForum = (Forum) ExisForumMap.get(forumTitleEle.getText());
							TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
							List topics = tm.selectAllByForum(exisForum.getId());
							if (topics != null && topics.size() > 0)
							{
								Map ExisTopicMap = new HashMap();
								for (Iterator topicIter = topics.iterator(); topicIter.hasNext();)
								{
									Topic topic = (Topic) topicIter.next();
									ExisTopicMap.put(topic.getTitle(), topic);
								}

								List topicItems = forumEle.elements("item");
								for (Iterator topicIter = topicItems.iterator(); topicIter.hasNext();)
								{
									Element topicEle = (Element) topicIter.next();
									Element topicTitleEle = (Element) topicEle.elements("title").get(0);
									if (topicTitleEle.getText() != null && topicTitleEle.getText().trim().length() > 0)
									{
										if (!ExisTopicMap.containsKey(topicTitleEle.getText()))
										{
											try
											{
												createTopic(topicEle, exisForum, document, unZippedDirPath);
											}
											catch (Exception e)
											{
												if (logger.isErrorEnabled())
													logger.warn("site : " + ToolManager.getCurrentPlacement().getContext() + " : " + e.toString());
												e.printStackTrace();
											}
										}
									}
								}
							}
							else
							{
								// no topics are existing. create topics
								List topicItems = forumEle.elements("item");
								for (Iterator topicIter = topicItems.iterator(); topicIter.hasNext();)
								{
									Element topicEle = (Element) topicIter.next();
									try
									{
										createTopic(topicEle, exisForum, document, unZippedDirPath);
									}
									catch (Exception e)
									{
										if (logger.isErrorEnabled())
											logger.warn("site : " + ToolManager.getCurrentPlacement().getContext() + " : " + e.toString());
										e.printStackTrace();
									}
								}
							}
						}
						else
						{
							// this forum is not existing.create forum and its
							// topics
							Forum forum = createForum(forumEle, category);
							if (forum == null)
								continue;
							/* create task topics for each forum */
							List topicItems = forumEle.elements("item");
							for (Iterator topicIter = topicItems.iterator(); topicIter.hasNext();)
							{
								Element topicEle = (Element) topicIter.next();
								try
								{
									createTopic(topicEle, forum, document, unZippedDirPath);
								}
								catch (Exception e)
								{
									if (logger.isErrorEnabled())
										logger.warn("site : " + ToolManager.getCurrentPlacement().getContext() + " : " + e.toString());
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
			else
			{
				// no existing forums - create forums and topics for forums
				createForumsForCategory(eleCatItem, document, unZippedDirPath, category);
			}
		}
		else
		{
			// create category
			category = createCategory(eleCatItem, titleEle);

			/* create forums */
			createForumsForCategory(eleCatItem, document, unZippedDirPath, category);
		}

		if (logger.isDebugEnabled())
			logger.debug("Exiting processXML...");
	}

	/**
	 * 
	 * create forums for category and topics for forums
	 * 
	 * @param eleCatItem
	 * @param document
	 * @param unZippedDirPath
	 * @param category
	 * @throws Exception
	 */
	private void createForumsForCategory(Element eleCatItem, Document document, String unZippedDirPath, Category category) throws Exception
	{
		List forumItems = eleCatItem.elements("item");
		// if (logger.isDebugEnabled()) logger.debug("forumItems size is " +
		// forumItems.size());
		for (Iterator iter = forumItems.iterator(); iter.hasNext();)
		{
			Element forumEle = (Element) iter.next();
			Forum forum = createForum(forumEle, category);
			if (forum == null)
				continue;
			/* create task topics for each forum */
			List topicItems = forumEle.elements("item");
			for (Iterator topicIter = topicItems.iterator(); topicIter.hasNext();)
			{
				Element topicEle = (Element) topicIter.next();
				createTopic(topicEle, forum, document, unZippedDirPath);
			}
		}
	}

	/**
	 * creates forum category
	 * 
	 * @param titleEle
	 * @throws Exception
	 */
	private Category createCategory(Element eleCatItem, Element titleEle) throws Exception
	{
		// create category, forums and topics
		if (logger.isDebugEnabled())
			logger.debug("Creating category......");
		/* create category */
		CategoryDAO cm = DataAccessDriver.getInstance().newCategoryDAO();
		Category c = new Category();
		c.setName(titleEle.getText());
		c.setModerated(false);
	
		// grade category
		int catGradeType = 0;
		float gradePoints = 0f;
		
		Date startDate = null, endDate = null;

		String parameters = eleCatItem.attributeValue("parameters");
		if (parameters != null && parameters.trim().length() > 0)
		{
			String param[] = parameters.split("&");
			for (int i = 0; i < param.length; i++)
			{
				if (param[i].startsWith("gradetype"))
				{
					try
					{
						if (param[i].indexOf('=') != -1)
							catGradeType = Integer.parseInt(param[i].substring(param[i].indexOf('=') + 1));
					}
					catch (NumberFormatException e)
					{
						if (logger.isErrorEnabled())
							logger.error(e);
					}
				}
				else if (param[i].startsWith("gradepoints"))
				{
					try
					{
						if (param[i].indexOf('=') != -1)
							gradePoints = Float.parseFloat(param[i].substring(param[i].indexOf('=') + 1));
					}
					catch (NumberFormatException e)
					{
						if (logger.isErrorEnabled())
							logger.error(e);
					}
				}
				else if (param[i].startsWith("startdate"))
				{
					try
					{
						if (param[i].indexOf('=') != -1)
						{
							
							try
							{
								startDate = getDateFromString(param[i].substring(param[i].indexOf('=') + 1));
							} catch (ParseException e)
							{
							}
						}
					}
					catch (NumberFormatException e)
					{
						if (logger.isErrorEnabled())
							logger.error(e);
					}
				}
				else if (param[i].startsWith("enddate"))
				{
					try
					{
						if (param[i].indexOf('=') != -1)
						{
							
							try
							{
								endDate = getDateFromString(param[i].substring(param[i].indexOf('=') + 1));
							} catch (ParseException e)
							{
							}
						}
					}
					catch (NumberFormatException e)
					{
						if (logger.isErrorEnabled())
							logger.error(e);
					}
				}
			}
		}
			
		if (catGradeType == Forum.GRADE_BY_CATEGORY)
		{
			c.setGradeCategory(true);
		}
		
		c.setStartDate(startDate);
		c.setEndDate(endDate);
		
		int categoryId = cm.addNew(c);
		c.setId(categoryId);
		// add to cache
		// ForumRepository.addCategory(c);
		ForumRepository.addCourseCategoryToCache(c);
				
		if (catGradeType == Forum.GRADE_BY_CATEGORY)
		{
			Grade grade = new Grade();
			
			grade.setContext(ToolManager.getCurrentPlacement().getContext());
			grade.setCategoryId(c.getId());
			try
			{
				grade.setPoints(gradePoints);
			}
			catch (NumberFormatException ne)
			{
				grade.setPoints(0f);
			}
			grade.setType(Forum.GRADE_BY_CATEGORY);
	
			int gradeId = DataAccessDriver.getInstance().newGradeDAO().addNew(grade);
		}

		/*
		 * associate category with groups(Facilitator, Participant)
		 * //Facilitator GroupSecurityDAO gmodel =
		 * DataAccessDriver.getInstance().newGroupSecurityDAO();
		 * PermissionControl pc = new PermissionControl();
		 * pc.setSecurityModel(gmodel); Role role = new Role();
		 * role.setName(SecurityConstants.PERM_CATEGORY); GroupDAO gm =
		 * DataAccessDriver.getInstance().newGroupDAO(); Group facGroup =
		 * gm.selectGroupByName("Facilitator"); int groupId = facGroup.getId();
		 * RoleValueCollection roleValues = new RoleValueCollection();
		 * 
		 * RoleValue rv = new RoleValue();
		 * rv.setType(PermissionControl.ROLE_ALLOW);
		 * rv.setValue(Integer.toString(categoryId));
		 * 
		 * roleValues.add(rv);
		 * 
		 * pc.addRoleValue(groupId, role, roleValues);
		 * 
		 * //Participant Group parGroup = gm.selectGroupByName("Participant");
		 * int parGroupId = parGroup.getId(); RoleValueCollection parRoleValues
		 * = new RoleValueCollection();
		 * 
		 * RoleValue parrv = new RoleValue();
		 * parrv.setType(PermissionControl.ROLE_ALLOW);
		 * parrv.setValue(Integer.toString(categoryId));
		 * 
		 * parRoleValues.add(rv);
		 * 
		 * pc.addRoleValue(parGroupId, role, parRoleValues);
		 * 
		 * SecurityRepository.clean();
		 */
		return c;
	}

	/**
	 * creates forum for a category
	 * 
	 * @param forumEle
	 */
	private Forum createForum(Element forumEle, Category category) throws Exception
	{

		if (logger.isDebugEnabled())
			logger.debug("Entering createForum......");
		List eleForumTitles = (List) forumEle.elements("title");
		if (eleForumTitles == null || eleForumTitles.size() == 0)
			return null;

		// create the forum
		Element titleEle = (Element) forumEle.elements("title").get(0);
		int forumType = 0, forumAccessType = 0, forumGradeType = 0;
		float gradePoints = 0f;
		Date startDate = null, endDate = null;

		String parameters = forumEle.attributeValue("parameters");
		if (parameters != null && parameters.trim().length() > 0)
		{
			String param[] = parameters.split("&");
			for (int i = 0; i < param.length; i++)
			{
				if (param[i].startsWith("forumtype"))
				{
					try
					{
						if (param[i].indexOf('=') != -1)
							forumType = Integer.parseInt(param[i].substring(param[i].indexOf('=') + 1));
					}
					catch (NumberFormatException e)
					{
						if (logger.isErrorEnabled())
							logger.error(e);
					}
				}
				else if (param[i].startsWith("accesstype"))
				{
					try
					{
						if (param[i].indexOf('=') != -1)
							forumAccessType = Integer.parseInt(param[i].substring(param[i].indexOf('=') + 1));
					}
					catch (NumberFormatException e)
					{
						if (logger.isErrorEnabled())
							logger.error(e);
					}
				}
				else if (param[i].startsWith("gradetype"))
				{
					try
					{
						if (param[i].indexOf('=') != -1)
							forumGradeType = Integer.parseInt(param[i].substring(param[i].indexOf('=') + 1));
					}
					catch (NumberFormatException e)
					{
						if (logger.isErrorEnabled())
							logger.error(e);
					}
				}
				else if (param[i].startsWith("gradepoints"))
				{
					try
					{
						if (param[i].indexOf('=') != -1)
							gradePoints = Float.parseFloat(param[i].substring(param[i].indexOf('=') + 1));
					}
					catch (NumberFormatException e)
					{
						if (logger.isErrorEnabled())
							logger.error(e);
					}
				}
				else if (param[i].startsWith("startdate"))
				{
					try
					{
						if (param[i].indexOf('=') != -1)
						{
							
							try
							{
								startDate = getDateFromString(param[i].substring(param[i].indexOf('=') + 1));
							} catch (ParseException e)
							{
							}
						}
					}
					catch (NumberFormatException e)
					{
						if (logger.isErrorEnabled())
							logger.error(e);
					}
				}
				else if (param[i].startsWith("enddate"))
				{
					try
					{
						if (param[i].indexOf('=') != -1)
						{
							
							try
							{
								endDate = getDateFromString(param[i].substring(param[i].indexOf('=') + 1));
							} catch (ParseException e)
							{
							}
						}
					}
					catch (NumberFormatException e)
					{
						if (logger.isErrorEnabled())
							logger.error(e);
					}
				}
			}
		}

		// forum types are 0,1,2 and access types are 0,1,2
		if (forumType > 2)
			forumType = Forum.TYPE_NORMAL;
		if (forumAccessType > 2)
			forumAccessType = Forum.ACCESS_SITE;

		Forum f = new Forum();
		
		if ((category.getStartDate()) == null && (category.getEndDate() == null))
		{
			f.setStartDate(startDate);
			f.setEndDate(endDate);
		}
		else
		{
			f.setStartDate(null);
			f.setEndDate(null);
		}
		f.setIdCategories(category.getId());
		f.setName(titleEle.getTextTrim());
		f.setModerated(false);
		f.setType(forumType);
		f.setAccessType(forumAccessType);

		if (forumGradeType == Forum.GRADE_BY_FORUM || forumGradeType == Forum.GRADE_BY_TOPIC)
			f.setGradeType(forumGradeType);
		else
			f.setGradeType(Forum.GRADE_DISABLED);

		// description
		List genElements = forumEle.selectNodes("./imsmd:lom/imsmd:general");
		if (genElements != null && genElements.size() > 0)
		{
			Element generalElement = (Element) genElements.get(0);
			Element descElement = generalElement.element("description");
			String description = descElement.selectSingleNode(".//imsmd:langstring").getText();
			if (description != null)
				f.setDescription(description.trim());
		}

		int forumId = DataAccessDriver.getInstance().newForumDAO().addNew(f);
		f.setId(forumId);

		ForumRepository.addForum(f);

		// create grade if forum is grade by forum
		if (f.getGradeType() == Forum.GRADE_BY_FORUM)
		{
			Grade grade = new Grade();

			grade.setContext(ToolManager.getCurrentPlacement().getContext());
			grade.setForumId(forumId);
			try
			{
				grade.setPoints(gradePoints);
			}
			catch (NumberFormatException ne)
			{
				grade.setPoints(0f);
			}
			grade.setType(Forum.GRADE_BY_FORUM);

			int gradeId = DataAccessDriver.getInstance().newGradeDAO().addNew(grade);
		}

		/*
		 * //Process permissions List groups = new TreeGroup().getNodes();
		 * String[] allGroups = new String[groups.size()];
		 * 
		 * int i = 0; for (Iterator iter = groups.iterator(); iter.hasNext();) {
		 * GroupNode n = (GroupNode)iter.next(); allGroups[i++] =
		 * String.valueOf(n.getId()); }
		 * 
		 * GroupSecurityDAO gmodel =
		 * DataAccessDriver.getInstance().newGroupSecurityDAO();
		 * PermissionControl pc = new PermissionControl();
		 * pc.setSecurityModel(gmodel); //Access addRole(pc,
		 * SecurityConstants.PERM_FORUM, f.getId(), allGroups, true);
		 * //Anonymous posts addRole(pc, SecurityConstants.PERM_ANONYMOUS_POST,
		 * f.getId(), allGroups, true); //Read-only addRole(pc,
		 * SecurityConstants.PERM_READ_ONLY_FORUMS, f.getId(), allGroups, true);
		 * //Reply-only addRole(pc, SecurityConstants.PERM_REPLY_ONLY,
		 * f.getId(), allGroups, true); //HTML addRole(pc,
		 * SecurityConstants.PERM_HTML_DISABLED, f.getId(), allGroups, true);
		 * //Attachemtns addRole(pc, SecurityConstants.PERM_ATTACHMENTS_ENABLED,
		 * f.getId(), allGroups, true); SecurityRepository.clean();
		 */

		if (logger.isDebugEnabled())
			logger.debug("Exiting createForum......");

		return f;
	}

	/**
	 * add forum role
	 * 
	 * @param pc
	 * @param roleName
	 * @param forumId
	 * @param groups
	 * @param allow
	 * @throws Exception
	 */
	/*
	 * private void addRole(PermissionControl pc, String roleName, int forumId,
	 * String[] groups, boolean allow) throws Exception{ Role role = new Role();
	 * role.setName(roleName); for (int i = 0; i < groups.length; i++) { int
	 * groupId = Integer.parseInt(groups[i]); RoleValueCollection roleValues =
	 * new RoleValueCollection();
	 * 
	 * RoleValue rv = new RoleValue(); rv.setType(allow ?
	 * PermissionControl.ROLE_ALLOW : PermissionControl.ROLE_DENY);
	 * rv.setValue(Integer.toString(forumId)); roleValues.add(rv);
	 * 
	 * pc.addRoleValue(groupId, role, roleValues); } }
	 */

	/**
	 * create topic and first post
	 * 
	 * @param topicEle
	 * @param forum
	 */
	private void createTopic(Element topicEle, Forum forum, Document document, String unZippedDirPath) throws Exception
	{
		if (logger.isDebugEnabled())
			logger.debug("Entering createTopic......");

		List eleTopicTitles = (List) topicEle.elements("title");
		if (eleTopicTitles == null || eleTopicTitles.size() == 0)
			return;

		Element titleEle = (Element) topicEle.elements("title").get(0);

		int topicGrade = 0;
		float gradePoints = 0f;
		int topicType = 0;
		String parameters = topicEle.attributeValue("parameters");
		if (parameters != null && parameters.trim().length() > 0)
		{
			String param[] = parameters.split("&");
			for (int i = 0; i < param.length; i++)
			{
				if (param[i].startsWith("topictype"))
				{
					try
					{
						if (param[i].indexOf('=') != -1)
							topicType = Integer.parseInt(param[i].substring(param[i].indexOf('=') + 1));
					}
					catch (NumberFormatException e)
					{
						if (logger.isErrorEnabled())
							logger.error(e);
					}
				}
				else if (param[i].startsWith("gradetopic"))
				{
					try
					{
						if (param[i].indexOf('=') != -1)
							topicGrade = Integer.parseInt(param[i].substring(param[i].indexOf('=') + 1));
					}
					catch (NumberFormatException e)
					{
						if (logger.isErrorEnabled())
							logger.error(e);
					}
				}
				else if (param[i].startsWith("gradepoints"))
				{
					try
					{
						if (param[i].indexOf('=') != -1)
							gradePoints = Float.parseFloat(param[i].substring(param[i].indexOf('=') + 1));
					}
					catch (NumberFormatException e)
					{
						if (logger.isErrorEnabled())
							logger.error(e);
					}
				}
			}
		}

		/* create new topic and it is a first post */
		Topic t = new Topic();
		t.setId(-1);
		t.setForumId(forum.getId());
		if (topicType == Topic.TYPE_NORMAL || topicType == Topic.TYPE_ANNOUNCE || topicType == Topic.TYPE_STICKY)
			t.setType(topicType);
		else
			t.setType(Topic.TYPE_NORMAL);

		t.setGradeTopic((topicGrade == 1) ? true : false);
		t.setExportTopic(true);

		UserSession us = SessionFacade.getUserSession();
		User u = new User();
		u.setId(us.getUserId());
		u.setUsername(us.getUsername());

		t.setPostedBy(u);

		// Set the Post
		Post p = new Post();
		p.setTime(new Date());
		p.setForumId(forum.getId());
		p.setSubject(titleEle.getText());
		p.setBbCodeEnabled(false);
		p.setSmiliesEnabled(true);
		p.setSignatureEnabled(true);
		p.setUserIp(JForum.getRequest().getRemoteAddr());
		p.setUserId(SessionFacade.getUserSession().getUserId());

		/*
		 * boolean htmlEnabled =
		 * SecurityRepository.canAccess(SecurityConstants.PERM_HTML_DISABLED,
		 * String.valueOf(forum.getId()));
		 */
		p.setHtmlEnabled(false);

		// get text
		Attribute identifierref = topicEle.attribute("identifierref");
		Element eleRes = getResource(identifierref.getValue(), document);
		String hrefVal = eleRes.attributeValue("href");
		String message = null;
		;
		try
		{
			message = readFromFile(new File(unZippedDirPath + File.separator + hrefVal));
		}
		catch (Exception e1)
		{
			if (logger.isWarnEnabled())
				logger.warn("site : " + ToolManager.getCurrentPlacement().getContext() + " : " + e1.toString());
			e1.printStackTrace();
			return;
		}
		
		// parse and update embedded references path and save the embedded references if not in the resource tool
		message = parseAndUpdateImportUrls(message, unZippedDirPath);
		

		if (p.isHtmlEnabled())
		{
			p.setText(SafeHtml.makeSafe(message));
		}
		else
			p.setText(message);

		TopicDAO tm = DataAccessDriver.getInstance().newTopicDAO();
		PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
		ForumDAO fm = DataAccessDriver.getInstance().newForumDAO();

		// If topic_id is -1, then is the first post
		if (t.getId() == -1)
		{
			t.setTime(new Date());
			t.setTitle(titleEle.getText());
			t.setModerated(false);

			try
			{
				t.setId(tm.addNew(t));
			}
			catch (Exception e)
			{
				if (logger.isWarnEnabled())
					logger.warn("site : " + ToolManager.getCurrentPlacement().getContext() + " : " + e.toString());
				e.printStackTrace();
				return;
			}
		}
		p.setTopicId(t.getId());
		p.setModerate(false);
		int postId = pm.addNew(p);
		t.setFirstPostId(postId);

		tm.update(t);
		/*
		 * //attachments Map filesToSave = attachmentProcess(); if
		 * (filesToSave.size() > 0) insertAttachments(p.getId(), filesToSave);
		 */

		//
		try
		{
			processAttachments(eleRes, forum, document, unZippedDirPath, postId);
		}
		catch (Exception e)
		{
			if (logger.isWarnEnabled())
				logger.warn("site : " + ToolManager.getCurrentPlacement().getContext() + " : " + e.toString());
			e.printStackTrace();
		}

		DataAccessDriver.getInstance().newUserDAO().incrementPosts(p.getUserId());
		TopicsCommon.updateBoardStatus(t, postId, true, tm, fm);

		// create grade if forum is grade by topic and topic is grade topic
		if (forum.getGradeType() == Forum.GRADE_BY_TOPIC && t.isGradeTopic())
		{
			Grade grade = new Grade();

			grade.setContext(ToolManager.getCurrentPlacement().getContext());
			grade.setForumId(forum.getId());
			grade.setTopicId(t.getId());
			try
			{
				grade.setPoints(gradePoints);
			}
			catch (NumberFormatException ne)
			{
				grade.setPoints(0f);
			}
			grade.setType(Forum.GRADE_BY_TOPIC);

			int gradeId = DataAccessDriver.getInstance().newGradeDAO().addNew(grade);
		}

		if (logger.isDebugEnabled())
			logger.debug("Exiting createTopic......");
	}

	/**
	 * process attachments
	 * 
	 * @param eleRes
	 * @param forum
	 * @param document
	 * @param unZippedDirPath
	 * @param postId
	 * @throws Exception
	 */
	private void processAttachments(Element eleRes, Forum forum, Document document, String unZippedDirPath, int postId) throws Exception
	{
		int total_files = 0;

		long totalSize = 0;
		int userId = SessionFacade.getUserSession().getUserId();
		AttachmentDAO am = DataAccessDriver.getInstance().newAttachmentDAO();
		Map extensions = am.extensionsForSecurity();
		// resource elements - <file>
		List resElements = eleRes.elements();
		Attribute resHrefAttr = eleRes.attribute("href");
		for (Iterator iter = resElements.iterator(); iter.hasNext();)
		{
			Element element = (Element) iter.next();
			if (element.getQualifiedName().equalsIgnoreCase("file"))
			{
				Attribute hrefAttr = element.attribute("href");
				// if (total_files >= SystemGlobals.getIntValue(ConfigKeys.ATTACHMENTS_MAX_POST))
				if (total_files >= SakaiSystemGlobals.getIntValue(ConfigKeys.ATTACHMENTS_MAX_POST))
				{
					if (logger.isWarnEnabled())
						logger.warn("Total number of files attached exceeding maximum " + "allowed.... for site : "
								+ ToolManager.getCurrentPlacement().getContext() + " - and for forum : " + forum.getName());
					return;
				}
				String fileext = hrefAttr.getValue().trim().substring(hrefAttr.getValue().trim().lastIndexOf('.') + 1);
				// if (!fileext.equalsIgnoreCase("html")){
				if (!hrefAttr.getValue().trim().equalsIgnoreCase(resHrefAttr.getValue().trim()))
				{
					// create as attachment
					try
					{
						// if (logger.isDebugEnabled())
						// logger.debug("extensions.entrySet() : "+
						// extensions.entrySet());
						// Check if the extension is allowed
						if (extensions.containsKey(fileext))
						{
							if (!((Boolean) extensions.get(fileext)).booleanValue())
							{
								throw new BadExtensionException(I18n.getMessage("Attachments.badExtension", new String[] { fileext }));
							}
						}
						else
						{
							if (logger.isWarnEnabled())
								logger.warn("site - " + ToolManager.getCurrentPlacement().getContext() + " - Attachment with this extension '"
										+ fileext + "' is not allowed ");
							continue;
						}
					}
					catch (BadExtensionException e)
					{
						if (logger.isWarnEnabled())
							logger.warn("site - " + ToolManager.getCurrentPlacement().getContext() + " - " + e.toString());
						continue;
					}
					Attachment a = new Attachment();
					a.setUserId(userId);

					AttachmentInfo info = new AttachmentInfo();
					File attachmentFile = new File(unZippedDirPath + File.separator + hrefAttr.getValue());
					if (!attachmentFile.exists())
					{
						if (logger.isErrorEnabled())
							logger.error("Error while import for site - " + ToolManager.getCurrentPlacement().getContext() + " - Attachment : "
									+ attachmentFile.getAbsolutePath() + " doesn't exist.");
						continue;
					}

					try
					{
						// Check for total attachments size limit
						// long quotaSize = SystemGlobals.getIntValue(ConfigKeys.ATTACHMENTS_QUOTA_LIMIT) * 1024 * 1024;
						long quotaSize = SakaiSystemGlobals.getIntValue(ConfigKeys.ATTACHMENTS_QUOTA_LIMIT) * 1024 * 1024;
						totalSize = totalSize + attachmentFile.length();
						if (quotaSize < totalSize)
						{
							throw new AttachmentSizeTooBigException(I18n.getMessage("Attachments.tooBig", new Integer[] {
									new Integer((int) quotaSize / 1024), new Integer((int) totalSize / 1024) }));
						}
					}
					catch (AttachmentSizeTooBigException e)
					{
						if (logger.isWarnEnabled())
							logger.warn("site - " + ToolManager.getCurrentPlacement().getContext() + " - " + e.toString());
						break;
					}

					info.setFilesize(attachmentFile.length());
					info.setComment("");
					info.setMimetype("");
					// description
					List genElements = element.selectNodes("./imsmd:lom/imsmd:general");
					if (genElements != null && genElements.size() > 0)
					{
						Element generalElement = (Element) genElements.get(0);
						Element descElement = generalElement.element("description");
						String description = descElement.selectSingleNode(".//imsmd:langstring").getText();
						if (description != null)
							info.setComment(description.trim());
					}

					String filename = null;
					String hrefVal = hrefAttr.getValue().trim();
					if (hrefVal.lastIndexOf('/') != -1)
						filename = hrefVal.substring(hrefVal.lastIndexOf('/') + 1);
					else if (hrefVal.lastIndexOf('\\') != -1)
						filename = hrefVal.substring(hrefVal.lastIndexOf('\\') + 1);

					if (filename.startsWith("post_"))
						filename = filename.substring(filename.indexOf("_", 5) + 1);

					info.setRealFilename(filename);
					info.setUploadTimeInMillis(System.currentTimeMillis());

					AttachmentExtension ext = am.selectExtension(fileext);

					info.setExtension(ext);
					String savePath = makeStoreFilename(info);
					info.setPhysicalFilename(savePath);
					a.setInfo(info);
					a.setPostId(postId);
					// String path = SystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR) + "/" + a.getInfo().getPhysicalFilename();
					String path = SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR) + "/" + a.getInfo().getPhysicalFilename();
					am.addAttachment(a);

					saveAttachmentFile(path, attachmentFile);

					total_files++;
				}
			}
		}
	}

	/**
	 * 
	 * @param a
	 * @return
	 */
	private String makeStoreFilename(AttachmentInfo a)
	{
		Calendar c = new GregorianCalendar();
		c.setTimeInMillis(System.currentTimeMillis());
		c.get(Calendar.YEAR);
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

		String dir = "" + year + "/" + month + "/" + day + "/";
		//new File(SystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR) + "/" + dir).mkdirs();
		new File(SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR) + "/" + dir).mkdirs();

		return dir + MD5.crypt(a.getRealFilename() + a.getUploadTime()) + "_" + SessionFacade.getUserSession().getUserId() + "."
				+ a.getExtension().getExtension();
	}

	/**
	 * 
	 * @param postId
	 * @param filesToSave
	 * @throws Exception
	 */
	public void saveAttachments(int postId, Map filesToSave) throws Exception
	{
		for (Iterator iter = filesToSave.entrySet().iterator(); iter.hasNext();)
		{
			Map.Entry entry = (Map.Entry) iter.next();
			Attachment a = (Attachment) entry.getValue();
			a.setPostId(postId);
			// String path = SystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR) + "/" + a.getInfo().getPhysicalFilename();
			String path = SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR) + "/" + a.getInfo().getPhysicalFilename();
			AttachmentDAO am = DataAccessDriver.getInstance().newAttachmentDAO();
			am.addAttachment(a);
			((UploadUtils) entry.getKey()).saveUploadedFile(path);
		}
	}

	/**
	 * gets the resource element
	 * 
	 * @param resName
	 *            resource name
	 * @param document
	 *            document
	 * @return resource element
	 * @throws Exception
	 */
	private Element getResource(String resName, Document document) throws Exception
	{
		if (logger.isDebugEnabled())
			logger.debug("Entering getResource...");

		Map uris = new HashMap();
		uris.put("imscp", DEFAULT_NAMESPACE_URI);
		uris.put("imsmd", IMSMD_NAMESPACE_URI);

		// resource
		XPath xpath = document.createXPath("/imscp:manifest/imscp:resources/imscp:resource[@identifier = '" + resName + "']");
		xpath.setNamespaceURIs(uris);

		Element eleRes = (Element) xpath.selectSingleNode(document);

		if (logger.isDebugEnabled())
			logger.debug("Exiting getResource...");

		return eleRes;
	}

	/**
	 * reading characters from file
	 * 
	 * @param contentfile
	 *            - file to read
	 * @return content of the file
	 */
	private String readFromFile(File contentfile) throws Exception
	{
		StringBuffer lines = new StringBuffer();
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(contentfile);
			int len;

			byte buf[] = new byte[1];
			while ((len = fis.read(buf)) > 0)
			{
				String temp = new String(buf);
				lines.append(temp);
			}
			return lines.toString();
		}
		catch (Exception ex)
		{
			throw ex;
		}
		finally
		{
			if (fis != null)
				fis.close();
		}
	}
	
	
	
	private byte[] readFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

	

	/**
	 * save attachment file
	 * 
	 * @param filename
	 * @param contentfile
	 * @throws Exception
	 */
	private void saveAttachmentFile(String filename, File contentfile) throws Exception
	{
		FileInputStream fis = null;
		FileOutputStream outputStream = null;

		try
		{
			fis = new FileInputStream(contentfile);
			outputStream = new FileOutputStream(filename);

			int c = 0;
			byte[] b = new byte[4096];
			while ((c = fis.read(b)) != -1)
			{
				outputStream.write(b, 0, c);
			}
		}
		catch (Exception e)
		{
			if (logger.isWarnEnabled())
				logger.warn("site : " + ToolManager.getCurrentPlacement().getContext() + " : " + e.toString());
			throw e;
		}
		finally
		{
			if (outputStream != null)
			{
				outputStream.flush();
				outputStream.close();
			}

			if (fis != null)
			{
				fis.close();
			}
		}
	}

	/**
	 * update embedded reference paths
	 * 
	 * @param content
	 *            - content
	 * @param resoucesDir
	 *            - resouce packaging directory
	 * @param parentRefPathFolder
	 *            - parent reference path folder
	 * @return modifed content
	 */
	private String updateReferencePaths(String content, File resoucesDir, String parentRefPath)
	{
		Set<String> refs = XrefHelper.harvestEmbeddedReferences(content, null);
		
		if (refs != null && refs.size() > 0)
		{

			for (String ref : refs)
			{
				String refPathWithFolders;
				String siteId;

				// handle urls that has /content/group/
				if (ref.startsWith("/content/group/"))
				{
					try
					{
						ContentResource resource;

						// get the resource
						String id = ref.substring("/content".length());
						resource = ContentHostingService.getResource(id);

						if (resource != null)
						{
							String resourceId = resource.getId();
							String resourcePath = resourceId.substring(resourceId.indexOf("group") + "group".length() + 1);
							resourcePath = resourcePath.substring(resourcePath.indexOf("/"));
														
							refPathWithFolders = ref.substring("/content/group/".length());

							siteId = refPathWithFolders.substring(0, refPathWithFolders.indexOf("/"));
							refPathWithFolders = refPathWithFolders.substring(refPathWithFolders.indexOf("/"));

							String type = resource.getContentType();

							// create the file for the reference
							// String refPath = null;
							File refPathFolder = null;

							// create folders
							// refPath = refPathWithFolders.substring(0, refPathWithFolders.lastIndexOf("/"));
							String refPath = resourcePath.substring(0, resourcePath.lastIndexOf("/"));
							
							refPathFolder = new File(resoucesDir + "/embeded_jf_content/content/group" + refPath);
							if (!refPathFolder.exists())
								refPathFolder.mkdirs();

							// get file name
							// fileName = refPathWithFolders.substring(refPathWithFolders.lastIndexOf("/") + 1);
							String fileName = resourceId.substring(resourceId.lastIndexOf("/") + 1);

							File resfile = new File(refPathFolder + "/" + fileName);

							if (!(ref.trim().endsWith(".html") || ref.trim().endsWith(".htm")))
							{
								createFileFromContent(resource.getContent(), resfile.getAbsolutePath());
							}
							
							content = parseExportContentResourceUrls(content, ref, parentRefPath);
						}
					}
					catch (PermissionException e)
					{
						if (logger.isWarnEnabled())
							logger.warn("updateReferencePaths: " + e.toString());
					}
					catch (IdUnusedException e)
					{
						if (logger.isWarnEnabled())
							logger.warn("updateReferencePaths: " + e.toString());
					}
					catch (TypeException e)
					{
						if (logger.isWarnEnabled())
							logger.warn("updateReferencePaths: " + e.toString());
					}
					catch (ServerOverloadException e)
					{
						if (logger.isWarnEnabled())
							logger.warn("updateReferencePaths: " + e.toString());
					}
					catch (Exception e)
					{
						if (logger.isWarnEnabled())
							logger.warn("updateReferencePaths: " + e.toString());
					}
				}
			}
		}

		return content;
	}
	
	/**
	 * parse and update import urls of the embedded references	
	 * @param message
	 * 				- message
	 * @param unZippedDirPath
	 * 				- unzipped directory path
	 * @return
	 * 				- modified message
	 */
	private String parseAndUpdateImportUrls(String message, String unZippedDirPath)
	{
		Set<String> embeddedRefs = new LinkedHashSet<String>();
		
		message = parseContentResourceUrls(message, embeddedRefs);
		
		if (embeddedRefs.size() > 0)
		{
			//  save the embeded references if not existing. Modify the embeded resources for any references before saving.
			parseAndSaveEmbeddedRefs(embeddedRefs, unZippedDirPath);
		}
		
		return message;		
	}

	/**
	 * parse content resource urls
	 * @param message
	 * 			- message
	 * @param embeddedRefs
	 * 			- embedded references
	 * @return
	 * 			- modified content
	 */
	private String parseContentResourceUrls(String message, Set<String> embeddedRefs)
	{
		StringBuffer sb = new StringBuffer();

		Pattern p = getContentResourcePattern();
			
		Matcher m = p.matcher(message);
		while (m.find())
		{
			if (m.groupCount() == 2)
			{
				String ref = m.group(2);
				
				embeddedRefs.add("embeded_jf_content/content/group"+ ref);
				
				String siteId = ToolManager.getCurrentPlacement().getContext();
				m.appendReplacement(sb, Matcher.quoteReplacement(m.group(1)+ "=\""+ ServerConfigurationService.getServerUrl()+"/access/content/group/"+ siteId + Validator.escapeUrl(ref) + "\""));

			}
		}
		m.appendTail(sb);
		
		return sb.toString();
	}
	
	/**
	 * parse and replace emebeded references
	 * @param message - message
	 * @param embeddedRef - embedded reference
	 * @return - modified message
	 */
	private String parseAndReplacePostTextUrls(String message, String embeddedRef)
	{
		StringBuffer sb = new StringBuffer();

		Pattern p = getExportContentResourcePattern();
			
		Matcher m = p.matcher(message);
		while (m.find())
		{
			if (m.groupCount() == 3)
			{
				String ref = m.group(3);
				
				String refPathWithFolders = ref.substring(ref.indexOf("/"));
				
				String embeddedRefPathWithFolders = embeddedRef.substring("/content/group/".length());
				embeddedRefPathWithFolders = embeddedRefPathWithFolders.substring(embeddedRefPathWithFolders.indexOf("/"));
				
				String siteId = ToolManager.getCurrentPlacement().getContext();
				
				try
				{
					refPathWithFolders = URLDecoder.decode(refPathWithFolders, "UTF-8");
					//This is alreaded docoded
					//embeddedRefPathWithFolders = URLDecoder.decode(embeddedRefPathWithFolders, "UTF-8");
				}
				catch (UnsupportedEncodingException e)
				{
					if (logger.isWarnEnabled()) logger.warn("parseAndReplacePostTextUrls: " + e);
				}
				
				if (refPathWithFolders.equalsIgnoreCase(embeddedRefPathWithFolders)) 
				{					
					m.appendReplacement(sb, Matcher.quoteReplacement(m.group(1)+ "=\""+ "embeded_jf_content/content/group"+ refPathWithFolders + "\""));
				}

			}
		}
		m.appendTail(sb);
		
		return sb.toString();
	}
	
	/**
	 * parse export content resource reference urls
	 * @param message
	 * 				- messge	
	 * @param ref
	 * 				- reference
	 * @param parentPath
	 * 				- parent path
	 * @return
	 * 				- modified content
	 */
	private String parseExportContentResourceUrls(String message, String ref, String parentPath)
	{
		ref = Validator.escapeUrl(ref);
		
		// file name with spaces doesn't have %20 for spaces
		// get file name
		/*This may not be needed as spaces have %20
		 String fileName = ref.substring(ref.lastIndexOf("/") + 1);
		
		try
		{
			fileName = URLDecoder.decode(fileName, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			if (logger.isWarnEnabled()) logger.warn("parseExportContentResourceUrls: " + e);
		}*/
		
		//ref = ref.substring(0, ref.lastIndexOf("/") + 1) + fileName;
		
		parentPath = Validator.escapeUrl(parentPath);
		
		StringBuffer sb = new StringBuffer();

		Pattern p = Pattern.compile("(src|href)[\\s]*=[\\s]*[\\\"'](/access"+ ref +")[\\\"']", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNICODE_CASE);
			
		Matcher m = p.matcher(message);
		while (m.find())
		{
			if (m.groupCount() == 2)
			{
				String refMatch = m.group(2);
				if (parentPath == null || parentPath.trim().length() == 0)
				{
					String siteId = ToolManager.getCurrentPlacement().getContext();
					refMatch = refMatch.substring(("/access/content/group/"+ siteId).length() + 1);
				}
				else
				{
				
					if (refMatch.indexOf(parentPath) ==  -1)
					{
						String siteId = ToolManager.getCurrentPlacement().getContext();
						refMatch = refMatch.substring(("/access/content/group/"+ siteId).length() + 1);
						
						String pathRef[] = parentPath.split("/");
												
						StringBuilder refPath = new StringBuilder();
						
						for (int i=0; i<(pathRef.length-1); i++)
						{
							refPath.append("../"); 
						}
						refMatch = refPath.toString() + refMatch;
					}
					else
					{
						int index = refMatch.indexOf(parentPath);
						refMatch = refMatch.substring(index + parentPath.length() + 1);
					}
				}
				
				/*String fileName1 = null;
				boolean escapeFilePath = false;
				
				try
				{
					if (logger.isDebugEnabled()) logger.debug("parseExportContentResourceUrls: refMatch :"+ refMatch);
										
					if (refMatch.lastIndexOf("/") != -1)
					{
						fileName1 = refMatch.substring(refMatch.lastIndexOf("/")+1);
						refMatch = refMatch.substring(0, refMatch.lastIndexOf("/")+1);
						
						if (logger.isDebugEnabled()) logger.debug("parseExportContentResourceUrls: refMatch sub string :"+ refMatch);
						
						fileName1 = URLDecoder.decode(fileName1, "UTF-8");
						escapeFilePath = true;
					}
				}
				catch (UnsupportedEncodingException e)
				{
					if (logger.isWarnEnabled()) logger.warn("parseExportContentResourceUrls: " + e);
				}
				
				if (escapeFilePath)
				{
					m.appendReplacement(sb, Matcher.quoteReplacement(m.group(1)+ "=\""+ refMatch + fileName1 +"\""));
				}
				else
					m.appendReplacement(sb, Matcher.quoteReplacement(m.group(1)+ "=\""+ refMatch + "\""));*/
				m.appendReplacement(sb, Matcher.quoteReplacement(m.group(1)+ "=\""+ refMatch + "\""));

			}
		}
		m.appendTail(sb);
		
		return sb.toString();
	}
	
	/**
	 * Create the embedded reference detection pattern. It creates three groups: 0 - the entire matc, 1- src|href, 2-server url up to access/content/...., 3-siteid/refwithfolders.
	 * 
	 * @return The Pattern.
	 */
	private Pattern getExportContentResourcePattern()
	{
		return Pattern.compile("(src|href)[\\s]*=[\\s]*[\\\"'](.*?)/access/content/group/([^\"]*)[\\\"']", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	}
	
	
	/**
	 * Create the embedded reference detection pattern. It creates three groups: 0 - the entire matc, 1- src|href, 2-the reference.
	 * 
	 * @return The Pattern.
	 */
	private Pattern getContentResourcePattern()
	{
		return Pattern.compile("(src|href)[\\s]*=[\\s]*[\\\"']embeded_jf_content/content/group([^\"]*)[\\\"']", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	}
	
	
	/**
	 * Create the embedded reference detection pattern. It creates three groups: 0 - the entire match, 1- src|href, 2-the reference.
	 * 
	 * @return The Pattern.
	 */
	private Pattern getEmbeddedContentResourcePattern()
	{
		return Pattern.compile("(src|href)[\\s]*=[\\s]*[\\\"'](?!http|www|file)([^\"]*)[\\\"']", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	}
	
	/**
	 * Create the embedded reference detection pattern. It creates three groups: 0 - the entire matc, 1- src|href, 2-the reference.
	 * 
	 * @return The Pattern.
	 */
	private Pattern getMeleteResourcePattern()
	{
		return Pattern.compile("(src|href)[\\s]*=[\\s]*[\\\"']embeded_jf_content/meleteDocs/content/private/meleteDocs([^\"]*)[\\\"']", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
	}
	
	/**
	 * parse and save embedded references	
	 * @param embeddedRefs
	 * 			- embedded references
	 * @param unZippedDirPath
	 * 			- unzipped directory path
	 */
	private void parseAndSaveEmbeddedRefs(Set<String> embeddedRefs, String unZippedDirPath)
	{
		Set<String> allEmbeddedRefs = new LinkedHashSet<String>();
		
		// actual emebeded refs
		for (String ref : embeddedRefs)
		{
			if (ref.startsWith("embeded_jf_content/content/group"))
			{
				String id = ref.substring("embeded_jf_content/content/group".length());
				allEmbeddedRefs.add(id);
			}
		}
		
		Set<String> embededRefsToSave = new LinkedHashSet<String>();
		
		for (String ref : allEmbeddedRefs)
		{
			// if html file read and process the embedded refs else save if the resource is not existing
			if (ref.trim().endsWith(".html") || ref.trim().endsWith(".htm"))
			{
				// first level embedded refs with entire ref path with folders
				if (!isResourceAvailble(ref))
				{
					String refId = null;
					
					refId = ref;
					
					String parentPath = refId.substring(0, refId.lastIndexOf("/"));
					
					/* update embedded refs url in this file and save this file.
					 * All embedded refs in this html file should be saved with the parent path 
					 * ("/group/" + siteId + parentPath + id) as these urls are relative unless if the embedded 
					 * refs have .. in their urls
					 */
					
					// parseEmbeddedRef returns the embededed ref
					// save files other than html
					// parse the embedded html files 
					Set<String> process = new LinkedHashSet<String>();
					
					Set<String> rv = new LinkedHashSet<String>();
					rv = parseEmbeddedUrlsAndSaveFile(unZippedDirPath, ref, parentPath, embededRefsToSave);
					process.addAll(rv);
					
					if (logger.isDebugEnabled()) logger.debug("rv is "+ rv);
					
					while (!process.isEmpty())
					{
						Set<String> secondary = new LinkedHashSet<String>();
						for (String ref1 : process)
						{
							// check for any html
							if (ref1.endsWith(".html") || (ref1.endsWith(".htm")))
							{	
								// parentPath = refId.substring(0, refId.lastIndexOf("/"));
								if (ref1.lastIndexOf("/") != -1)
								{
									parentPath = ref1.substring(0, ref1.lastIndexOf("/"));
								}
								else
									parentPath = "";
								
								secondary.addAll(parseEmbeddedUrlsAndSaveFile(unZippedDirPath, ref1, parentPath, embededRefsToSave));
							}
							else
							{
								embededRefsToSave.add(ref1);
							}
						}

						// ignore any secondary we already have
						secondary.removeAll(rv);

						// collect the secondary
						rv.addAll(secondary);

						// process the secondary
						process.clear();
						process.addAll(secondary);
					}
							
				}
			}
			else
			{
				if (logger.isDebugEnabled()) logger.debug("parseAndSaveEmbeddedRefs(Set<String>, String) : ref is "+ ref);
				checkAndSaveResource(ref, unZippedDirPath);
			}
		}
		
		if (logger.isDebugEnabled()) logger.debug("parseAndSaveEmbeddedRefs(Set<String>, String) : embededRefsToSave is "+ embededRefsToSave);
		//if (logger.isDebugEnabled()) logger.debug("embededRefsToSave is "+ embededRefsToSave);
		
		//save embededRefsToSave
		for (String ref : embededRefsToSave)
		{
			try
			{
				ref = URLDecoder.decode(ref, "UTF-8");
			}
			catch (UnsupportedEncodingException e)
			{
				if (logger.isWarnEnabled()) logger.warn("parseAndSaveEmbeddedRefs: " + e);
			}
			checkAndSaveResource(ref, unZippedDirPath);
		}
		
	}

	/**
	 * parse embedded urls and save the files
	 * 
	 * @param unZippedDirPath
	 * 			- unzipped directory path
	 * @param ref
	 * 			- embedded refereces
	 * @param parentPath
	 * 			- parent path
	 * @param allEmbeddedRefs
	 * 			- all embedded refs
	 * @return
	 * 			- embedded references
	 */
	private Set<String> parseEmbeddedUrlsAndSaveFile(String unZippedDirPath, String ref, String parentPath, Set<String> allEmbeddedRefs)
	{
		Set<String> embeddedRefs = new LinkedHashSet<String>();
		
		/*try
		{
			ref = URLDecoder.decode(ref, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			if (logger.isWarnEnabled()) logger.warn("parseEmbeddedUrlsAndSaveFile: " + e);
			return embeddedRefs;
		}*/
		
		if (!ref.startsWith("/"))
			ref = "/"+ ref;
			
		File file = new File(unZippedDirPath + File.separator +"resources"+ File.separator + "embeded_jf_content/content/group" + ref);
		
		String refHtml = null;
		try
		{
			refHtml = readFromFile(file);
		}
		catch (Exception e)
		{
			if (logger.isErrorEnabled()) logger.error("parseEmbeddedUrlsAndSaveFile: " + e);
			e.printStackTrace();
			return embeddedRefs; 
		}
		
		refHtml = parseEmbeddedContentResourceUrls(refHtml, parentPath, embeddedRefs);
		
		//save the resource
		checkAndSaveResource(ref, unZippedDirPath, refHtml);
		
		if (embeddedRefs.size() > 0)
		{
			for (String ref1 : embeddedRefs)
			{
				// check for any html
				if (ref1.trim().endsWith(".html") || (ref1.trim().endsWith(".htm")))
				{
					
				}
				else
				{
					allEmbeddedRefs.add(ref1.trim());
				}
			}
			
		}
		
		return embeddedRefs;		
	}

	/**
	 * check and save the resource is not existing
	 * 
	 * @param refId
	 * 			- reference id
	 * @param unZippedDirPath
	 * 			- unzipped directory path
	 */
	private void checkAndSaveResource(String refId, String unZippedDirPath)
	{
		if (refId ==  null || refId.trim().length() == 0)
			return;
		
		/*try
		{
			refId = URLDecoder.decode(refId, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			if (logger.isWarnEnabled()) logger.warn("checkAndSaveResource: " + e);
		}*/
		
		if (!refId.startsWith("/"))
			refId = "/"+ refId;
		
		String siteId = ToolManager.getCurrentPlacement().getContext();
		
		boolean exisResource = false;
		
		// get the resource
		try
		{
			// bypass security when reading the resource to copy
			SecurityService.pushAdvisor(new SecurityAdvisor()
			{
				public SecurityAdvice isAllowed(String userId, String function, String reference)
				{
					return SecurityAdvice.ALLOWED;
				}
			});
			
			//ContentResource resource = ContentHostingService.getResource("/group/" + siteId + refId);
			ContentHostingService.checkResource("/group/" + siteId + refId);
			
			exisResource = true;

			
		}
		catch (PermissionException e)
		{
			if (logger.isWarnEnabled())
				logger.warn("checkAndSaveResource: " + e.toString());
			
			return;
		}
		catch (IdUnusedException e)
		{
			/*if (logger.isWarnEnabled())
				logger.warn("checkAndSaveResource: " + e.toString());*/
		}
		catch (TypeException e)
		{
			if (logger.isWarnEnabled())
				logger.warn("checkAndSaveResource(String , String): " + e.toString());
			
			return;
		}
		finally
		{
			SecurityService.popAdvisor();
		}
		
		
		if (logger.isDebugEnabled()) logger.debug("Resource is "+ exisResource);
		
		if (!exisResource)
		{
			// bypass security when reading the resource to copy
			SecurityService.pushAdvisor(new SecurityAdvisor()
			{
				public SecurityAdvice isAllowed(String userId, String function, String reference)
				{
					return SecurityAdvice.ALLOWED;
				}
			});
			
			// the new resource collection and name
			String destinationCollection = "/group/" + siteId + refId;
			String displayName = refId.substring(refId.lastIndexOf("/")+1);

			// make sure to remove the reference root sakai:reference-root
			ResourcePropertiesEdit props = ContentHostingService.newResourceProperties();
			props.removeProperty("sakai:reference-root");
			props.addProperty(ResourceProperties.PROP_DISPLAY_NAME, displayName); 
			
			File file = new File(unZippedDirPath + File.separator +"resources"+ File.separator + "embeded_jf_content/content/group/" + refId);
			byte[] body;
			try
			{
				body = readFile(file);
			}
			catch (IOException e)
			{
				if (logger.isWarnEnabled())
					logger.warn("checkAndSaveResource(String , String): " + e.toString());
				
				return;
			}
			String type = new MimetypesFileTypeMap().getContentType(file);
			
			if (logger.isDebugEnabled()) logger.debug("checkAndSaveResource: type : " + type);
			
			// create url/link resource if the file has no extentions and has valid url in the file
			if (displayName.indexOf(".") == -1)
			{
				// this is a link if the file has one line and valid url
				type = "text/url";
			}
			
			ContentResource importedResource = null;
			try
			{
				//String destinationPath = destinationCollection + destinationName;
				String destinationPath = destinationCollection;
				//importedResource = ContentHostingService.addResource(destinationName, destinationCollection, 255, type, body, props, 0);
				importedResource = ContentHostingService.addResource(destinationPath, type, body, props, 0);
			}
			catch (IdInvalidException e)
			{
				if (logger.isWarnEnabled())
					logger.warn("checkAndSaveResource(String , String): " + e.toString());
			}
			catch (InconsistentException e)
			{
				if (logger.isWarnEnabled())
					logger.warn("checkAndSaveResource(String , String): " + e.toString());
			}
			catch (OverQuotaException e)
			{
				if (logger.isWarnEnabled())
					logger.warn("checkAndSaveResource(String , String): " + e.toString());
			}
			catch (ServerOverloadException e)
			{
				if (logger.isWarnEnabled())
					logger.warn("checkAndSaveResource(String , String): " + e.toString());
			}
			catch (PermissionException e)
			{
				if (logger.isWarnEnabled())
					logger.warn("checkAndSaveResource(String , String): " + e.toString());
			}
			catch (IdUsedException e)
			{
				if (logger.isWarnEnabled())
					logger.warn("checkAndSaveResource(String , String): " + e.toString());
			}
			finally
			{
				SecurityService.popAdvisor();
			}

		}
	}
	
	/**
	 * check and save resource
	 * @param refId
	 * 			- reference id
	 * @param unZippedDirPath
	 * 			- unzipped directory path
	 * @param htmlText
	 * 			- html text
	 */
	private void checkAndSaveResource(String refId, String unZippedDirPath, String htmlText)
	{
		if (refId ==  null || refId.trim().length() == 0)
			return;
		
		String siteId = ToolManager.getCurrentPlacement().getContext();
		
		boolean exisResource = false;
		
		// get the resource
		try
		{
			// bypass security when reading the resource to copy
			SecurityService.pushAdvisor(new SecurityAdvisor()
			{
				public SecurityAdvice isAllowed(String userId, String function, String reference)
				{
					return SecurityAdvice.ALLOWED;
				}
			});
			
			//ContentResource resource = ContentHostingService.getResource("/group/" + siteId + refId);
			ContentHostingService.checkResource("/group/" + siteId + refId);
			
			exisResource = true;

			
		}
		catch (PermissionException e)
		{
			if (logger.isWarnEnabled())
				logger.warn("checkAndSaveResource: " + e.toString());
			
			return;
		}
		catch (IdUnusedException e)
		{
			/*if (logger.isWarnEnabled())
				logger.warn("checkAndSaveResource: " + e.toString());*/
		}
		catch (TypeException e)
		{
			if (logger.isWarnEnabled())
				logger.warn("checkAndSaveResource: " + e.toString());
			
			return;
		}
		finally
		{
			SecurityService.popAdvisor();
		}
		
		if (logger.isDebugEnabled()) logger.debug("Resource is "+ exisResource);
		
		if (!exisResource)
		{
			// bypass security when reading the resource to copy
			SecurityService.pushAdvisor(new SecurityAdvisor()
			{
				public SecurityAdvice isAllowed(String userId, String function, String reference)
				{
					return SecurityAdvice.ALLOWED;
				}
			});
			
			// the new resource collection and name
			String destinationCollection = "/group/" + siteId + refId;
			String displayName = refId.substring(refId.lastIndexOf("/")+1);

			// make sure to remove the reference root sakai:reference-root
			ResourcePropertiesEdit props = ContentHostingService.newResourceProperties();
			props.removeProperty("sakai:reference-root");
			props.addProperty(ResourceProperties.PROP_DISPLAY_NAME, displayName); 
			
			File file = new File(unZippedDirPath + File.separator +"resources"+ File.separator + "embeded_jf_content/content/group/" + refId);
			byte[] body;
			
			body =  new String(htmlText).getBytes();
			String type = new MimetypesFileTypeMap().getContentType(file);
			
			ContentResource importedResource = null;
			try
			{
				String destinationPath = destinationCollection;
				importedResource = ContentHostingService.addResource(destinationPath, type, body, props, 0);
			}
			catch (IdInvalidException e)
			{
				if (logger.isErrorEnabled()) logger.error("Error while adding resource : "+ refId);
				return;
			}
			catch (InconsistentException e)
			{
				if (logger.isWarnEnabled())
					logger.warn("checkAndSaveResource: " + e.toString());
			}
			catch (OverQuotaException e)
			{
				if (logger.isWarnEnabled())
					logger.warn("checkAndSaveResource: " + e.toString());
			}
			catch (ServerOverloadException e)
			{
				if (logger.isWarnEnabled())
					logger.warn("checkAndSaveResource: " + e.toString());
			}
			catch (PermissionException e)
			{
				if (logger.isWarnEnabled())
					logger.warn("checkAndSaveResource: " + e.toString());
			}
			catch (IdUsedException e)
			{
				if (logger.isWarnEnabled())
					logger.warn("checkAndSaveResource: " + e.toString());
			}
			finally
			{
				SecurityService.popAdvisor();
			}

		}
		
	}
	
	/**
	 * check if resource available
	 * 	
	 * @param ref
	 * 			- reference
	 * @return
	 * 			true - if existing
	 * 			false - if resource is not existing
	 */
	private boolean isResourceAvailble(String ref)
	{
		String id = null;
		
		id = ref;
		
		if (id == null)
			throw new IllegalArgumentException("ref id is not valid");
		
		// get the resource
		try
		{
			// bypass security when reading the resource to copy
			SecurityService.pushAdvisor(new SecurityAdvisor()
			{
				public SecurityAdvice isAllowed(String userId, String function, String reference)
				{
					return SecurityAdvice.ALLOWED;
				}
			});
			
			String siteId = ToolManager.getCurrentPlacement().getContext();
			ContentResource resource = ContentHostingService.getResource("/group/" + siteId + id);
			
			return true;

			
		}
		catch (PermissionException e)
		{
			if (logger.isWarnEnabled())
				logger.warn("isResourceAvailble: " + e.toString());
		}
		catch (IdUnusedException e)
		{
			/*if (logger.isWarnEnabled())
				logger.warn("checkAndSaveResource: " + e.toString());*/
		}
		catch (TypeException e)
		{
			if (logger.isWarnEnabled())
				logger.warn("isResourceAvailble: " + e.toString());
		}
		finally
		{
			SecurityService.popAdvisor();
		}
		
		return false;
	}
	
	/**
	 * parse embedded content resource urls
	 * 
	 * @param message
	 * 			- message
	 * @param parentPath
	 * 			- parent path
	 * @param embeddedRefs
	 * 			- embedded references
	 * @return
	 * 			- modifed content
	 */
	private String parseEmbeddedContentResourceUrls(String message, String parentPath, Set<String> embeddedRefs)
	{
		/*try
		{
			parentPath = URLDecoder.decode(parentPath, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			if (logger.isWarnEnabled()) logger.warn("parseEmbeddedContentResourceUrls: " + e);
		}*/
		
		StringBuffer sb = new StringBuffer();

		Pattern p = getEmbeddedContentResourcePattern();
			
		Matcher m = p.matcher(message);
		while (m.find())
		{
			if (m.groupCount() == 2)
			{
				String ref = m.group(2);
				
				if (logger.isDebugEnabled()) logger.debug("parseEmbeddedContentResourceUrls Before : ref : " + ref);
				
				//check for the resource in the unzipped file before changing the url
				// embeddedRefs.add(ref);
				
				boolean excludeParentPath = false;
				
				// for relative paths
				if (ref.startsWith(".."))
				{
					ref = ref.substring(ref.lastIndexOf("..")+ "..".length() + 1);
					excludeParentPath = true;
				}
				
				if (logger.isDebugEnabled()) logger.debug("parseEmbeddedContentResourceUrls After : ref : " + ref);
				
				String siteId = ToolManager.getCurrentPlacement().getContext();
				
				if (excludeParentPath)
				{
					m.appendReplacement(sb, Matcher.quoteReplacement(m.group(1)+ "=\"/access/content/group/"+ siteId + "/" + ref + "\""));
					embeddedRefs.add(ref);
				}
				else
				{
					m.appendReplacement(sb, Matcher.quoteReplacement(m.group(1)+ "=\"/access/content/group/"+ siteId + Validator.escapeUrl(parentPath) + "/" +ref + "\""));
					embeddedRefs.add(parentPath +"/"+ ref);
				}
			}
		}
		m.appendTail(sb);
		
		return sb.toString();
	}
	
	/**
	 * parse the date string to date
	 * @param date
	 * @return the date from the parsed date string
	 * @throws ParseException
	 */
	private Date getDateFromString(String date) throws ParseException{
		Date parsedDate;
		try {
			//SimpleDateFormat sdf = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.CALENDAR_DATE_TIME_FORMAT));
			SimpleDateFormat sdf = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.CALENDAR_DATE_TIME_FORMAT));
			parsedDate = sdf.parse(date);
		} catch (ParseException e) {
			if (logger.isWarnEnabled()){
				logger.warn(this.getClass().getName()+ ".getDateFromString() : " +
						"Error occurred while parsing the date for '"+ date +"'");
				logger.warn(e.toString());
			}
			throw e;
		}
		return parsedDate;
	}
}
