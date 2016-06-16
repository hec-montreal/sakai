/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/GenericAttachmentDAO.java $ 
 * $Id: GenericAttachmentDAO.java 62519 2009-08-11 18:21:38Z murthy@etudes.org $ 
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
package org.etudes.jforum.dao.generic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.JForum;
import org.etudes.jforum.entities.Attachment;
import org.etudes.jforum.entities.AttachmentExtension;
import org.etudes.jforum.entities.AttachmentExtensionGroup;
import org.etudes.jforum.entities.AttachmentInfo;
import org.etudes.jforum.entities.QuotaLimit;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;

/**
 * @author Rafael Steil
 */
public class GenericAttachmentDAO extends AutoKeys implements org.etudes.jforum.dao.AttachmentDAO
{
	private static final Log logger = LogFactory.getLog(GenericAttachmentDAO.class);
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#addQuotaLimit(org.etudes.jforum.entities.QuotaLimit)
	 */
	public void addQuotaLimit(QuotaLimit limit) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.addQuotaLimit"));
		p.setString(1, limit.getDescription());
		p.setInt(2, limit.getSize());
		p.setInt(3, limit.getType());
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#updateQuotaLimit(org.etudes.jforum.entities.QuotaLimit)
	 */
	public void updateQuotaLimit(QuotaLimit limit) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.updateQuotaLimit"));
		p.setString(1, limit.getDescription());
		p.setInt(2, limit.getSize());
		p.setInt(3, limit.getType());
		p.setInt(4, limit.getId());
		p.executeUpdate();
		p.close();
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#cleanGroupQuota()
	 */
	public void cleanGroupQuota() throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.deleteGroupQuota"));
		p.executeUpdate();
		p.close();
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#setGroupQuota(int, int)
	 */
	public void setGroupQuota(int groupId, int quotaId) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.setGroupQuota"));
		p.setInt(1, groupId);
		p.setInt(2, quotaId);
		p.executeUpdate();
		p.close();
	}

	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#removeQuotaLimit(int)
	 */
	public void removeQuotaLimit(int id) throws Exception
	{
		this.removeQuotaLimit(new String[] { Integer.toString(id) });
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#removeQuotaLimit(java.lang.String[])
	 */
	public void removeQuotaLimit(String[] ids) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.removeQuotaLimit"));
		
		for (int i = 0; i < ids.length; i++) {
			p.setInt(1, Integer.parseInt(ids[i]));
			p.executeUpdate();
		}
		
		p.close();
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#selectQuotaLimit()
	 */
	public List selectQuotaLimit() throws Exception
	{
		List l = new ArrayList();
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.selectQuotaLimit"));
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			l.add(this.getQuotaLimit(rs));
		}
		
		rs.close();
		p.close();
		
		return l;
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#selectQuotaLimit()
	 */
	public QuotaLimit selectQuotaLimitByGroup(int groupId) throws Exception
	{
		QuotaLimit ql = null;
		
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.selectQuotaLimitByGroup"));
		p.setInt(1, groupId);
		
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			ql = this.getQuotaLimit(rs);
		}
		
		rs.close();
		p.close();
		
		return ql;
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#selectGroupsQuotaLimits()
	 */
	public Map selectGroupsQuotaLimits() throws Exception
	{
		Map m = new HashMap();
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.selectGroupsQuotaLimits"));
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			m.put(new Integer(rs.getInt("group_id")), new Integer(rs.getInt("quota_limit_id")));
		}
		
		return m;
	}
	
	protected QuotaLimit getQuotaLimit(ResultSet rs) throws Exception
	{
		QuotaLimit ql = new QuotaLimit();
		ql.setDescription(rs.getString("quota_desc"));
		ql.setId(rs.getInt("quota_limit_id"));
		ql.setSize(rs.getInt("quota_limit"));
		ql.setType(rs.getInt("quota_type"));
		
		return ql;
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#addExtensionGroup(org.etudes.jforum.entities.AttachmentExtensionGroup)
	 */
	public void addExtensionGroup(AttachmentExtensionGroup g) throws Exception
	{
		if (logger.isDebugEnabled()) logger.debug("AttachmentExtensionGroup name = " + g.getName());
		if (logger.isDebugEnabled()) logger.debug("AttachmentExtensionGroup up icon = " + g.getUploadIcon());
		if (logger.isDebugEnabled()) logger.debug("AttachmentExtensionGroup down mode = " + g.getDownloadMode());
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.addExtensionGroup"));
		p.setString(1, g.getName());
		p.setInt(2, g.isAllow() ? 1 : 0);
		p.setString(3, g.getUploadIcon());
		p.setInt(4, g.getDownloadMode());
		p.executeUpdate();
		p.close();
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#removeExtensionGroups(java.lang.String[])
	 */
	public void removeExtensionGroups(String[] ids) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.removeExtensionGroups"));
		
		for (int i = 0; i < ids.length; i++) {
			p.setInt(1, Integer.parseInt(ids[i]));
			p.executeUpdate();
		}
		
		p.close();
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#selectExtensionGroups()
	 */
	public List selectExtensionGroups() throws Exception
	{
		List l = new ArrayList();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.selectExtensionGroups"));
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			l.add(this.getExtensionGroup(rs));
		}
		
		rs.close();
		p.close();
		
		return l;
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#extensionsForSecurity()
	 */
	public Map extensionsForSecurity() throws Exception
	{
		Map m = new HashMap();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.extensionsForSecurity"));
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			int allow = rs.getInt("group_allow");
			if (allow == 1) {
				allow = rs.getInt("allow");
			}
			
			m.put(rs.getString("extension"), new Boolean(allow == 1));
		}
		
		rs.close();
		p.close();
		
		return m;
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#updateExtensionGroup(org.etudes.jforum.entities.AttachmentExtensionGroup)
	 */
	public void updateExtensionGroup(AttachmentExtensionGroup g) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.updateExtensionGroups"));
		p.setString(1, g.getName());
		p.setInt(2, g.isAllow() ? 1 : 0);
		p.setString(3, g.getUploadIcon());
		p.setInt(4, g.getDownloadMode());
		p.setInt(5, g.getId());
		p.executeUpdate();
		p.close();
	}
	
	protected AttachmentExtensionGroup getExtensionGroup(ResultSet rs) throws Exception
	{
		AttachmentExtensionGroup g = new AttachmentExtensionGroup();
		g.setId(rs.getInt("extension_group_id"));
		g.setName(rs.getString("name"));
		g.setUploadIcon(rs.getString("upload_icon"));
		g.setAllow(rs.getInt("allow") == 1);
		g.setDownloadMode(rs.getInt("download_mode"));
		
		return g;
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#addExtension(org.etudes.jforum.entities.AttachmentExtension)
	 */
	public void addExtension(AttachmentExtension e) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.addExtension"));
		p.setInt(1, e.getExtensionGroupId());
		p.setString(2, e.getComment());
		p.setString(3, e.getUploadIcon());
		p.setString(4, e.getExtension().toLowerCase());
		p.setInt(5, e.isAllow() ? 1 : 0);
		p.executeUpdate();
		p.close();
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#removeExtensions(java.lang.String[])
	 */
	public void removeExtensions(String[] ids) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.removeExtension"));
		for (int i = 0; i < ids.length; i++) {
			p.setInt(1, Integer.parseInt(ids[i]));
			p.executeUpdate();
		}
		p.close();
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#selectExtensions()
	 */
	public List selectExtensions() throws Exception
	{
		List l = new ArrayList();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.selectExtensions"));
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			l.add(this.getExtension(rs));
		}
		
		rs.close();
		p.close();
		
		return l;
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#updateExtension(org.etudes.jforum.entities.AttachmentExtension)
	 */
	public void updateExtension(AttachmentExtension e) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.updateExtension"));
		p.setInt(1, e.getExtensionGroupId());
		p.setString(2, e.getComment());
		p.setString(3, e.getUploadIcon());
		p.setString(4, e.getExtension().toLowerCase());
		p.setInt(5, e.isAllow() ? 1 : 0);
		p.setInt(6, e.getId());
		p.executeUpdate();
		p.close();
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#selectExtension(java.lang.String)
	 */
	public AttachmentExtension selectExtension(String extension) throws Exception
	{
		return this.searchExtension(SystemGlobals.getValue(ConfigKeys.EXTENSION_FIELD), 
				extension);
	}
	
	private AttachmentExtension selectExtension(int extensionId) throws Exception
	{
		return this.searchExtension("extension_id", new Integer(extensionId));
	}
	
	private AttachmentExtension searchExtension(String paramName, Object paramValue) throws Exception
	{
		String sql = SystemGlobals.getSql("AttachmentModel.selectExtension");
		sql = sql.replaceAll("\\$field", paramName);
		
		PreparedStatement p = JForum.getConnection().prepareStatement(sql);
		p.setObject(1, paramValue);
		
		AttachmentExtension e = new AttachmentExtension();
		
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			e = this.getExtension(rs);
		}
		else {
			e.setUnknown(true);
		}
		
		rs.close();
		p.close();
		
		return e;
	}
	
	protected AttachmentExtension getExtension(ResultSet rs) throws Exception
	{
		AttachmentExtension e = new AttachmentExtension();
		e.setAllow(rs.getInt("allow") == 1);
		e.setComment(rs.getString("description"));
		e.setExtension(rs.getString("extension"));
		e.setExtensionGroupId(rs.getInt("extension_group_id"));
		e.setId(rs.getInt("extension_id"));
		
		String icon = rs.getString("upload_icon");
		if (icon == null || icon.equals("")) {
			icon = rs.getString("group_icon");
		}
		
		e.setUploadIcon(icon);
		
		return e;
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#addAttachment(org.etudes.jforum.entities.Attachment)
	 */
	public void addAttachment(Attachment a) throws Exception
	{
		PreparedStatement p = this.getStatementForAutoKeys("AttachmentModel.addAttachment");
		p.setInt(1, a.getPostId());
		p.setInt(2, a.getPrivmsgsId());
		p.setInt(3, a.getUserId());
		
		this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("AttachmentModel.lastGeneratedAttachmentId"));
		int id = this.executeAutoKeysQuery(p);
		p.close();
		
		p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.addAttachmentInfo"));
		p.setInt(1, id);
		p.setString(2, a.getInfo().getPhysicalFilename());
		p.setString(3, a.getInfo().getRealFilename());
		p.setString(4, a.getInfo().getComment());
		p.setString(5, a.getInfo().getMimetype());
		p.setLong(6, a.getInfo().getFilesize());
		p.setTimestamp(7, new Timestamp(a.getInfo().getUploadTimeInMillis()));
		p.setInt(8, 0);
		p.setInt(9, a.getInfo().getExtension().getId());
		p.executeUpdate();
		p.close();
		
		this.updatePost(a.getPostId(), 1);
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#addPMAttachment(org.etudes.jforum.entities.Attachment)
	 */
	public int addPMAttachment(Attachment a) throws Exception
	{
		PreparedStatement p = this.getStatementForAutoKeys("AttachmentModel.addAttachment");
		p.setInt(1, a.getPostId());
		p.setInt(2, a.getPrivmsgsId());
		p.setInt(3, a.getUserId());
		
		this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("AttachmentModel.lastGeneratedAttachmentId"));
		int id = this.executeAutoKeysQuery(p);
		p.close();
		
		p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.addAttachmentInfo"));
		p.setInt(1, id);
		p.setString(2, a.getInfo().getPhysicalFilename());
		p.setString(3, a.getInfo().getRealFilename());
		p.setString(4, a.getInfo().getComment());
		p.setString(5, a.getInfo().getMimetype());
		p.setLong(6, a.getInfo().getFilesize());
		p.setTimestamp(7, new Timestamp(a.getInfo().getUploadTimeInMillis()));
		p.setInt(8, 0);
		p.setInt(9, a.getInfo().getExtension().getId());
		p.executeUpdate();
		p.close();
		
		return id;
	}
	
	protected void updatePost(int postId, int count) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.updatePost"));
		p.setInt(1, count);
		p.setInt(2, postId);
		p.executeUpdate();
		p.close();
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#removeAttachment(int, int)
	 */
	public void removeAttachment(int id, int postId) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.removeAttachmentInfo"));
		p.setInt(1, id);
		p.executeUpdate();
		p.close();
		
		p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.removeAttachment"));
		p.setInt(1, id);
		p.executeUpdate();
		p.close();
		
		p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.countPostAttachments"));
		p.setInt(1, postId);

		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			this.updatePost(postId, rs.getInt(1));
		}
		
		rs.close();
		p.close();
	}
	
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#removePMAttachment(int)
	 */
	public void removePMAttachment(int id) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.removeAttachmentInfo"));
		p.setInt(1, id);
		p.executeUpdate();
		p.close();
		
		p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.removeAttachment"));
		p.setInt(1, id);
		p.executeUpdate();
		p.close();
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#updateAttachment(org.etudes.jforum.entities.Attachment)
	 */
	public void updateAttachment(Attachment a) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.updateAttachment"));
		p.setString(1, a.getInfo().getComment());
		p.setInt(2, a.getInfo().getDownloadCount());
		p.setInt(3, a.getId());
		p.executeUpdate();
		p.close();
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#selectAttachments(int)
	 */
	public List selectAttachments(int postId) throws Exception
	{
		List l = new ArrayList();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.selectAttachments"));
		p.setInt(1, postId);
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			l.add(this.getAttachment(rs));
		}
		
		rs.close();
		p.close();
		
		return l;
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#selectPMAttachments(int)
	 */
	public List selectPMAttachments(int privMsgsId) throws Exception
	{
		List l = new ArrayList();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.selectPMAttachments"));
		p.setInt(1, privMsgsId);
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			l.add(this.getAttachment(rs));
		}
		
		rs.close();
		p.close();
		
		return l;
	}
	
	protected Attachment getAttachment(ResultSet rs) throws Exception
	{
		Attachment a = new Attachment();
		a.setId(rs.getInt("attach_id"));
		a.setPostId(rs.getInt("post_id"));
		a.setPrivmsgsId(rs.getInt("privmsgs_id"));
		
		AttachmentInfo ai = new AttachmentInfo();
		ai.setComment(rs.getString("description"));
		ai.setDownloadCount(rs.getInt("download_count"));
		ai.setFilesize(rs.getLong("filesize"));
		ai.setMimetype(rs.getString("mimetype"));
		ai.setPhysicalFilename(rs.getString("physical_filename"));
		ai.setRealFilename(rs.getString("real_filename"));
		ai.setUploadTime(rs.getTimestamp("upload_time"));
		ai.setExtension(this.selectExtension(rs.getInt("extension_id")));
		
		a.setInfo(ai);
		
		return a;
	}
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#selectAttachmentById(int)
	 */
	public Attachment selectAttachmentById(int attachId) throws Exception
	{
		Attachment e = null;
		
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.selectAttachmentById"));
		p.setInt(1, attachId);
		
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			e = this.getAttachment(rs);
		}
		
		rs.close();
		p.close();
		
		return e;
	}
	
	
	/**
	 * @see org.etudes.jforum.dao.AttachmentDAO#selectAttachmentPrivateMessages(int)
	 */
	public int selectAttachmentPrivateMessages(int attachId) throws Exception
	{
		int count = 0;
		
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.selectAttachmentPrivatMessagesById"));
		p.setInt(1, attachId);
		
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			count = rs.getInt("total");
		}
		
		rs.close();
		p.close();
		
		return count;
	}
	
	public boolean isPhysicalDownloadMode(int extensionGroupId) throws Exception
	{
		boolean result = true;
		
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.isPhysicalDownloadMode"));
		
		p.setInt(1, extensionGroupId);
		
		ResultSet rs = p.executeQuery();
		if (rs.next())
		{
			result = (rs.getInt("download_mode") == 2);
		}
		
		rs.close();
		p.close();
		
		return result;
	}
	
}
