/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/admin/AttachmentsAction.java $ 
 * $Id: AttachmentsAction.java 66496 2010-03-11 00:25:25Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008 Etudes, Inc. 
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.etudes.jforum.dao.AttachmentDAO;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.entities.AttachmentExtension;
import org.etudes.jforum.entities.AttachmentExtensionGroup;
import org.etudes.jforum.entities.QuotaLimit;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.TreeGroup;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.preferences.TemplateKeys;
import org.etudes.jforum.util.user.JForumUserUtil;
import org.sakaiproject.authz.cover.SecurityService;
import org.sakaiproject.user.cover.UserDirectoryService;


/**
 * @author Rafael Steil
 * 1/31/06 - Mallika - adding code for quota limit
 */
public class AttachmentsAction extends AdminCommand
{
	public void configurations() throws Exception
	{
		this.context.put("icon", SystemGlobals.getValue(ConfigKeys.ATTACHMENTS_ICON));
		// this.context.put("createThumb", SystemGlobals.getBoolValue(ConfigKeys.ATTACHMENTS_IMAGES_CREATE_THUMB));
		this.context.put("createThumb", SakaiSystemGlobals.getBoolValue(ConfigKeys.ATTACHMENTS_IMAGES_CREATE_THUMB));
		// this.context.put("thumbH", SystemGlobals.getValue(ConfigKeys.ATTACHMENTS_IMAGES_MIN_THUMB_H));
		this.context.put("thumbH", SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_IMAGES_MIN_THUMB_H));
		// this.context.put("thumbW", SystemGlobals.getValue(ConfigKeys.ATTACHMENTS_IMAGES_MIN_THUMB_W));
		this.context.put("thumbW", SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_IMAGES_MIN_THUMB_W));
		// this.context.put("maxPost", SystemGlobals.getValue(ConfigKeys.ATTACHMENTS_MAX_POST));
		this.context.put("maxPost", SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_MAX_POST));
		// this.context.put("quotaLimit", SystemGlobals.getValue(ConfigKeys.ATTACHMENTS_QUOTA_LIMIT));
		this.context.put("quotaLimit", SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_QUOTA_LIMIT));

		this.setTemplateName(TemplateKeys.ATTACHMENTS_CONFIG);
	}
	
	public void configurationsSave() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		ConfigAction ca = new ConfigAction(this.request, this.response, this.context);
		ca.updateData(ca.getConfig());
		
		this.configurations();
	}
	
	public void quotaLimit() throws Exception
	{
		AttachmentDAO am = DataAccessDriver.getInstance().newAttachmentDAO();
		
		this.context.put("quotas", am.selectQuotaLimit());
		this.setTemplateName(TemplateKeys.ATTACHMENTS_QUOTA_LIMIT);
		this.context.put("groups", new TreeGroup().getNodes());
		this.context.put("selectedList", new ArrayList());
		this.context.put("groupQuotas", am.selectGroupsQuotaLimits());
	}
	
	public void quotaLimitSave() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		QuotaLimit ql = new QuotaLimit();
		ql.setDescription(this.request.getParameter("quota_description"));
		ql.setSize(this.request.getIntParameter("max_filesize"));
		ql.setType(this.request.getIntParameter("type"));
		
		DataAccessDriver.getInstance().newAttachmentDAO().addQuotaLimit(ql);
		this.quotaLimit();
	}
	
	public void quotaLimitUpdate() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		AttachmentDAO am = DataAccessDriver.getInstance().newAttachmentDAO();
		
		// First check if we should delete some entry
		String[] delete = this.request.getParameterValues("delete");
		List deleteList = new ArrayList();
		if (delete != null) {
			deleteList = Arrays.asList(delete);
			am.removeQuotaLimit(delete);
		}
		
		// Now update the remaining
		int total = this.request.getIntParameter("total_records");
		for (int i = 0; i < total; i++) {
			if (deleteList.contains(this.request.getParameter("id_" + i))) {
				continue;
			}
			
			QuotaLimit ql = new QuotaLimit();
			ql.setId(this.request.getIntParameter("id_" + i));
			ql.setDescription(this.request.getParameter("quota_desc_" + i));
			ql.setSize(this.request.getIntParameter("max_filesize_" + i));
			ql.setType(this.request.getIntParameter("type_" + i));
			
			am.updateQuotaLimit(ql);
		}
		
		this.quotaLimit();
	}
	
	public void extensionGroups() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		this.setTemplateName(TemplateKeys.ATTACHMENTS_EXTENSION_GROUPS);
		this.context.put("groups", DataAccessDriver.getInstance().newAttachmentDAO().selectExtensionGroups());
		this.context.put("viewTitleManageAttachExtensionGroups", true);
	}
	
	public void extensionGroupsSave() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		AttachmentExtensionGroup g = new AttachmentExtensionGroup();		
		g.setAllow(this.request.getParameter("allow") != null);
		g.setDownloadMode(this.request.getIntParameter("download_mode"));
		g.setName(this.request.getParameter("name"));
		g.setUploadIcon(this.request.getParameter("upload_icon"));
		
		DataAccessDriver.getInstance().newAttachmentDAO().addExtensionGroup(g);
		this.extensionGroups();
	}
	
	public void extensionGroupsUpdate() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		AttachmentDAO am = DataAccessDriver.getInstance().newAttachmentDAO();
		
		// Check if there are records to remove
		String[] delete = this.request.getParameterValues("delete");
		List deleteList = new ArrayList();
		if (delete != null) {
			deleteList = Arrays.asList(delete);
			am.removeExtensionGroups(delete);
		}
		
		// Update
		int total = this.request.getIntParameter("total_records");
		for (int i = 0; i < total; i++) {
			if (deleteList.contains(this.request.getParameter("id_" + i))) {
				continue;
			}
			
			AttachmentExtensionGroup g = new AttachmentExtensionGroup();
			g.setId(this.request.getIntParameter("id_" + i));
			g.setAllow(this.request.getParameter("allow_" + i) != null);
			g.setDownloadMode(this.request.getIntParameter("download_mode_" + i));
			g.setName(this.request.getParameter("name_" + i));
			g.setUploadIcon(this.request.getParameter("upload_icon_" + i));
			
			am.updateExtensionGroup(g);
		}
		
		this.extensionGroups();
	}
	
	public void extensions() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		AttachmentDAO am = DataAccessDriver.getInstance().newAttachmentDAO();
		
		this.setTemplateName(TemplateKeys.ATTACHMENTS_EXTENSIONS);
		this.context.put("extensions", am.selectExtensions());
		this.context.put("groups", am.selectExtensionGroups());
		this.context.put("viewTitleManageAttachExtensions", true);
	}
	
	public void extensionsSave() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		AttachmentExtension e = new AttachmentExtension();
		e.setAllow(this.request.getParameter("allow") != null);
		e.setComment(this.request.getParameter("comment"));
		e.setExtension(this.request.getParameter("extension"));
		e.setUploadIcon(this.request.getParameter("upload_icon"));
		e.setExtensionGroupId(this.request.getIntParameter("extension_group"));
		
		if (e.getExtension().startsWith(".")) {
			e.setExtension(e.getExtension().substring(1));
		}
		
		DataAccessDriver.getInstance().newAttachmentDAO().addExtension(e);
		this.extensions();
	}
	
	public void extensionsUpdate() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		AttachmentDAO am = DataAccessDriver.getInstance().newAttachmentDAO();
		
		// Check for records to delete
		String[] delete = this.request.getParameterValues("delete");
		List deleteList = new ArrayList();
		if (delete != null) {
			deleteList = Arrays.asList(delete);
			am.removeExtensions(delete);
		}
		
		int total = this.request.getIntParameter("total_records");
		for (int i = 0; i < total; i++) {
			if (deleteList.contains(this.request.getParameter("id_" + i))) {
				continue;
			}
			
			AttachmentExtension e = new AttachmentExtension();
			e.setAllow(this.request.getParameter("allow_" + i) != null);
			e.setComment(this.request.getParameter("comment_" + i));
			e.setExtension(this.request.getParameter("extension_" + i));
			e.setExtensionGroupId(this.request.getIntParameter("extension_group_" + i));
			e.setId(this.request.getIntParameter("id_" + i));
			e.setUploadIcon(this.request.getParameter("upload_icon_" + i));
			
			am.updateExtension(e);
		}
		
		this.extensions();
	}
	
	public void quotaGroupsSave() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		int total = this.request.getIntParameter("total_groups");
		AttachmentDAO am = DataAccessDriver.getInstance().newAttachmentDAO();
		am.cleanGroupQuota();
		
		for (int i = 0; i < total; i++) {
			String l = this.request.getParameter("limit_" + i);
			if (l == null || l.equals("")) {
				continue;
			}
			
			int limitId = Integer.parseInt(l);
			int groupId = this.request.getIntParameter("group_" + i);
			
			if (groupId > 0) {
				am.setGroupQuota(groupId, limitId);
			}
		}
		
		this.quotaLimit();
	}
	
	/**
	 * @see org.etudes.jforum.Command#list()
	 */
	public void list() throws Exception
	{
		boolean isfacilitator = JForumUserUtil.isJForumFacilitator(UserDirectoryService.getCurrentUser().getId()) || SecurityService.isSuperUser();
		
		if (!isfacilitator) {
			this.context.put("message", I18n.getMessage("User.NotAuthorizedToManage"));
			this.setTemplateName(TemplateKeys.MANAGE_NOT_AUTHORIZED);
			return;
		}
		
		this.configurations();
	}
}
