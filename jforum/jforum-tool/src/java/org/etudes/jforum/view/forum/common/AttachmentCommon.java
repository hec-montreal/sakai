/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/forum/common/AttachmentCommon.java $ 
 * $Id: AttachmentCommon.java 61928 2009-07-21 22:29:16Z murthy@etudes.org $ 
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
package org.etudes.jforum.view.forum.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import net.jforum.repository.SecurityRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.ActionServletRequest;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.dao.AttachmentDAO;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.entities.Attachment;
import org.etudes.jforum.entities.AttachmentExtension;
import org.etudes.jforum.entities.AttachmentInfo;
import org.etudes.jforum.entities.Group;
import org.etudes.jforum.entities.QuotaLimit;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.exceptions.AttachmentSizeTooBigException;
import org.etudes.jforum.exceptions.BadExtensionException;
import org.etudes.jforum.security.SecurityConstants;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.MD5;
import org.etudes.jforum.util.legacy.commons.fileupload.FileItem;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.sakaiproject.util.Validator;

/**
 * @author Rafael Steil
 * 1/31/06 - Mallika - upload size change
 */
public class AttachmentCommon
{
	private static Log logger = LogFactory.getLog(AttachmentCommon.class);
	
	private ActionServletRequest request;
	private AttachmentDAO am;
	private boolean canProceed;
	private Map filesToSave = new HashMap();
	
	public AttachmentCommon(ActionServletRequest request, int forumId)
	{
		this.request = request;
		this.am = DataAccessDriver.getInstance().newAttachmentDAO();
		
		/*this.canProceed = SecurityRepository.canAccess(SecurityConstants.PERM_ATTACHMENTS_ENABLED, 
				Integer.toString(forumId));*/
		this.canProceed = true;
		
		if (!this.canProceed) {
			return;
		}
	}
	
	public AttachmentCommon(ActionServletRequest request) {
		this.request = request;
		this.am = DataAccessDriver.getInstance().newAttachmentDAO();

		this.canProceed = true;

		if (!this.canProceed) {
			return;
		}
	}
	
	public void preProcess() throws Exception
	{
		if (!this.canProceed) {
			return;
		}
		
		String t = this.request.getParameter("total_files");
		
		if (t == null || "".equals(t)) {
			return;
		}
		
		int total = Integer.parseInt(t);
		
		if (total < 1) {
			return;
		}
		
		// if (total > SystemGlobals.getIntValue(ConfigKeys.ATTACHMENTS_MAX_POST)) {
		if (total > SakaiSystemGlobals.getIntValue(ConfigKeys.ATTACHMENTS_MAX_POST)) {
			total = SystemGlobals.getIntValue(ConfigKeys.ATTACHMENTS_MAX_POST);
		}

		long totalSize = 0;
		int userId = SessionFacade.getUserSession().getUserId();
		Map extensions = this.am.extensionsForSecurity();
		
		for (int i = 0; i < total; i++) {
			FileItem item = (FileItem)this.request.getObjectParameter("file_" + i);
			if (item == null) {
				continue;
			}

			if (item.getName().indexOf('\000') > -1) {
				logger.warn("Possible bad attachment (null char): " + item.getName()
						+ " - user_id: " + SessionFacade.getUserSession().getUserId());
				continue;
			}
			
			UploadUtils uploadUtils = new UploadUtils(item);
			
			// Check if the extension is allowed
			if (extensions.containsKey(uploadUtils.getExtension())) {
				if (!((Boolean)extensions.get(uploadUtils.getExtension())).booleanValue()) {
					throw new BadExtensionException(I18n.getMessage("Attachments.badExtension", 
							new String[] { uploadUtils.getExtension() }));
				}
			}
			
			Attachment a = new Attachment();
			a.setUserId(userId);
			
			AttachmentInfo info = new AttachmentInfo();
			info.setFilesize(item.getSize());
			info.setComment(this.request.getParameter("comment_" + i));
			info.setMimetype(item.getContentType());
			
			// Get only the filename, without the path (IE does that)
			String realName = item.getName();
			String separator = "/";
			int index = realName.indexOf(separator);
			
			if (index == -1) {
				separator = "\\";
				index = realName.indexOf(separator);
			}
			
			if (index > -1) {
				if (separator.equals("\\")) {
					separator = "\\\\";
				}
				
				String[] p = realName.split(separator);
				realName = p[p.length - 1];
			}
			
			String escRealName = Validator.escapeResourceName(realName);
			info.setRealFilename(escRealName);
			info.setUploadTimeInMillis(System.currentTimeMillis());
			
			AttachmentExtension ext = this.am.selectExtension(uploadUtils.getExtension().toLowerCase());
			if (ext.isUnknown()) {
				ext.setExtension(uploadUtils.getExtension());
			}
			
			info.setExtension(ext);
			String savePath = this.makeStoreFilename(info);
			info.setPhysicalFilename(savePath);
			
			a.setInfo(info);
			filesToSave.put(uploadUtils, a);
			
			totalSize += item.getSize();
		}
		
		// Check upload limits
		//Mallika - comments beg
		/*QuotaLimit ql = this.getQuotaLimit(userId);
		if (ql != null) {
			if (ql.exceedsQuota(totalSize)) {
				throw new AttachmentSizeTooBigException(I18n.getMessage("Attachments.tooBig", 
						new Integer[] { new Integer(ql.getSizeInBytes() / 1024), 
							new Integer((int)totalSize / 1024) }));
			}
		}*/
		//Mallika - comments end
		//Mallika - new code beg
		//long quotaSize = SystemGlobals.getIntValue(ConfigKeys.ATTACHMENTS_QUOTA_LIMIT) * 1024 * 1024;
		long quotaSize = SakaiSystemGlobals.getIntValue(ConfigKeys.ATTACHMENTS_QUOTA_LIMIT) * 1024 * 1024;
		if (quotaSize < totalSize) {
				throw new AttachmentSizeTooBigException(I18n.getMessage("Attachments.tooBig", 
						new Integer[] { new Integer((int)quotaSize / 1024), 
							new Integer((int)totalSize / 1024) }));
			}
		//Mallika - new code end
	}
	
	public void insertAttachments(int postId) throws Exception
	{
		if (!this.canProceed) {
			return;
		}
		
		for (Iterator iter = this.filesToSave.entrySet().iterator(); iter.hasNext(); ) {
			Map.Entry entry = (Map.Entry)iter.next();
			Attachment a = (Attachment)entry.getValue();
			a.setPostId(postId);
			// String path = SystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR) 
			String path = SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR)
				+ "/" 
				+ a.getInfo().getPhysicalFilename();
			
			this.am.addAttachment(a);
			((UploadUtils)entry.getKey()).saveUploadedFile(path);
		}
	}
	
	public QuotaLimit getQuotaLimit(int userId) throws Exception
	{
		QuotaLimit ql = new QuotaLimit();
		User u = DataAccessDriver.getInstance().newUserDAO().selectById(userId);
		
		for (Iterator iter = u.getGroupsList().iterator(); iter.hasNext();) {
			QuotaLimit l = this.am.selectQuotaLimitByGroup(((Group)iter.next()).getId());
			if (l == null) {
				continue;
			}
			
			if (l.getSizeInBytes() > ql.getSizeInBytes()) {
				ql = l;
			}
		}
		
		if (ql.getSize() == 0) {
			return null;
		}
		
		return ql;
	}
	
	public void editAttachments(int postId, int forumId) throws Exception
	{
		// Allow removing the attachments at least
		AttachmentDAO am = DataAccessDriver.getInstance().newAttachmentDAO();
		
		// Check for attachments to remove
		List deleteList = new ArrayList();
		String[] delete = null;
		String s = this.request.getParameter("delete_attach");
		
		if (s != null) {
			delete = s.split(",");
		}
		
		if (delete != null) {
			for (int i = 0; i < delete.length; i++) {
				if (delete[i] != null && !delete[i].equals("")) {
					int id = Integer.parseInt(delete[i]);
					Attachment a = am.selectAttachmentById(id);
					
					am.removeAttachment(id, postId);
					
					// File f = new File(SystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR)
					File f = new File(SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR)
							+ "/" + a.getInfo().getPhysicalFilename());
					if (f.exists()) {
						f.delete();
					}
				}
			}
			
			deleteList = Arrays.asList(delete);
		}
		
		/*if (!SecurityRepository.canAccess(SecurityConstants.PERM_ATTACHMENTS_ENABLED, 
				Integer.toString(forumId))
				&& !SecurityRepository.canAccess(SecurityConstants.PERM_ATTACHMENTS_DOWNLOAD)) {
			return;
		}*/
		
		// Update
		String[] attachIds = null;
		s = this.request.getParameter("edit_attach_ids");
		if (s != null) {
			attachIds = s.split(",");
		}
		
		if (attachIds != null) {
			for (int i = 0; i < attachIds.length; i++) {
				if (deleteList.contains(attachIds[i]) 
						|| attachIds[i] == null || attachIds[i].equals("")) {
					continue;
				}
				
				int id = Integer.parseInt(attachIds[i]);
				Attachment a = am.selectAttachmentById(id);
				a.getInfo().setComment(this.request.getParameter("edit_comment_" + id));

				am.updateAttachment(a);
			}
		}
	}
	
	public int[] insertPMAttachments() throws Exception {
		if (!this.canProceed) {
			return null;
		}

		int attachIds[] = new int[filesToSave.size()];
		int count = 0;

		for (Iterator iter = this.filesToSave.entrySet().iterator(); iter
				.hasNext();) {
			Map.Entry entry = (Map.Entry) iter.next();
			Attachment a = (Attachment) entry.getValue();
			a.setPrivmsgsId(0);
			//String path = SystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR)
			String path = SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR)
					+ "/" + a.getInfo().getPhysicalFilename();

			attachIds[count++] = this.am.addPMAttachment(a);
			((UploadUtils) entry.getKey()).saveUploadedFile(path);
		}

		return attachIds;
	}
	
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
		
		return dir
			+ MD5.crypt(a.getRealFilename() + a.getUploadTimeInMillis()) 
			+ "_" + SessionFacade.getUserSession().getUserId()
			+ "." + a.getExtension().getExtension();
	}
	
	public List getAttachments(int postId, int forumId) throws Exception
	{
		/*if (!SecurityRepository.canAccess(SecurityConstants.PERM_ATTACHMENTS_DOWNLOAD)
				&& !SecurityRepository.canAccess(SecurityConstants.PERM_ATTACHMENTS_ENABLED, 
						Integer.toString(forumId))) {
			return new ArrayList();
		}*/
		
		return this.am.selectAttachments(postId);
	}

    public boolean isPhysicalDownloadMode(int extensionGroupId) throws Exception
    {
            return this.am.isPhysicalDownloadMode(extensionGroupId);
    }
    
    public List getPMAttachments(int privMsgId) throws Exception {

		return this.am.selectPMAttachments(privMsgId);
	}

	public void deleteAttachments(int postId, int forumId) throws Exception
	{
		// Attachments
		List attachments = DataAccessDriver.getInstance().newAttachmentDAO().selectAttachments(postId);
		String attachIds = "";
		for (Iterator iter = attachments.iterator(); iter.hasNext(); ) {
			Attachment a = (Attachment)iter.next();
			attachIds += a.getId() + ",";
		}
		
		this.request.addParameter("delete_attach", attachIds);
		this.editAttachments(postId, forumId);
	}
	
	public void deletePMAttachments(int privMsgId) throws Exception {
		//Attachments
		List attachments = DataAccessDriver.getInstance().newAttachmentDAO().selectPMAttachments(privMsgId);

		if (attachments == null || attachments.size() == 0)
			return;

		String attachIds = "";
		boolean deleteFlag = false;
		for (Iterator iter = attachments.iterator(); iter.hasNext();) {
			Attachment a = (Attachment) iter.next();
			// check if this attachment is only used for one private message
			int count = DataAccessDriver.getInstance().newAttachmentDAO().selectAttachmentPrivateMessages(a.getId());

			if (count == 1) {
				attachIds += a.getId() + ",";
				deleteFlag = true;
			}
		}

		if (deleteFlag) {
			this.request.addParameter("delete_attach", attachIds);
			this.editPMAttachments(privMsgId);
		}
	}
	
	public void editPMAttachments(int privMsgId) throws Exception {
		// Allow removing the attachments at least
		AttachmentDAO am = DataAccessDriver.getInstance().newAttachmentDAO();

		// Check for attachments to remove
		List deleteList = new ArrayList();
		String[] delete = null;
		String s = this.request.getParameter("delete_attach");

		if (s != null) {
			delete = s.split(",");
		}

		if (delete != null) {
			for (int i = 0; i < delete.length; i++) {
				if (delete[i] != null && !delete[i].equals("")) {
					int id = Integer.parseInt(delete[i]);
					Attachment a = am.selectAttachmentById(id);
					am.removePMAttachment(id);

					// File f = new File(SystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR)
					File f = new File(SakaiSystemGlobals.getValue(ConfigKeys.ATTACHMENTS_STORE_DIR)
							+ "/" + a.getInfo().getPhysicalFilename());
					if (f.exists()) {
						f.delete();
					}
				}
			}

			deleteList = Arrays.asList(delete);
		}
	}
}
