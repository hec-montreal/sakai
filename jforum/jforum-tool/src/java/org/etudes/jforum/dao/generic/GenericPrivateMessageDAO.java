/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/GenericPrivateMessageDAO.java $ 
 * $Id: GenericPrivateMessageDAO.java 69032 2010-07-02 23:28:56Z murthy@etudes.org $ 
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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.JForum;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.UserDAO;
import org.etudes.jforum.entities.Post;
import org.etudes.jforum.entities.PrivateMessage;
import org.etudes.jforum.entities.PrivateMessageType;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.util.HtmlHelper;
import org.sakaiproject.tool.cover.ToolManager;

/**
 * @author Rafael Steil
 * 9/19/05 - Mallika - adding code to only show messages from this course
 * 9/19/05 - Mallika - adding code to add and delete from jforum sakai message table
 * 9/26/05 - Mallika - adding code to get count of messages
 * 10/13/05 - Mallika - adding code to get first name and last name
 *
 * 5/23/06 - Howie - revised selectFromInbox() and selectFromSent() methods to make sure no null (could be empty though) firstname and lastname for jf user.
 *
 */
public class GenericPrivateMessageDAO extends AutoKeys implements org.etudes.jforum.dao.PrivateMessageDAO
{
	private static Log logger = LogFactory.getLog(GenericPrivateMessageDAO.class);
	/** 
	 * @see org.etudes.jforum.dao.PrivateMessageDAO#send(org.etudes.jforum.entities.PrivateMessage)
	 */
	public void send(PrivateMessage pm) throws Exception
	{
		// We should store 2 copies: one for the sendee's sent box
		// and another for the target user's inbox.
		PreparedStatement p = this.getStatementForAutoKeys("PrivateMessageModel.add");

		// Sendee's sent box
		this.addPm(pm, p);
		this.addPmText(pm);
		
		// Target user's inbox
		p.setInt(1, PrivateMessageType.NEW);
		
		//Mallika's comments beg
		//pm.setId(this.executeAutoKeysQuery(p));
		//Mallika's comments end

		//Mallika's new code beg
		int msgId = this.executeAutoKeysQuery(p);
		pm.setId(msgId);
		CoursePrivateMessage cpm = new CoursePrivateMessage();
		cpm.setCourseId(ToolManager.getCurrentPlacement().getContext());
		cpm.setPrivmsgsId(msgId);
		CoursePrivateMessageDAO cpmDao = DataAccessDriver.getInstance().newCoursePrivateMessageDAO();
		cpmDao.addNew(cpm);
		//Mallika's new code end
		
		this.addPmText(pm);
		
		p.close();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void saveMessage(PrivateMessage pm, int[] attachmentIds) throws Exception
	{
		saveInUserSentbox(pm, attachmentIds);
		saveInToUserInbox(pm, attachmentIds);
	}
	
	/**
	 * save private message in to user sent box
	 * @param pm - private message
	 * @param attachmentIds - attachment Id's
	 * @throws Exception
	 */
	protected void saveInUserSentbox(PrivateMessage pm, int attachmentIds[]) throws Exception
	{
		// We should store 2 copies: one for the sendee's sent box
		// and another for the target user's inbox.
		PreparedStatement p = this.getStatementForAutoKeys("PrivateMessageModel.add");

		// Sendee's sent box
		this.addPm(pm, p);
		this.addPmText(pm);
		p.close();

		if (attachmentIds != null && attachmentIds.length > 0)
		{
			for (int i = 0; i < attachmentIds.length; i++)
			{
				addPMAttachmentsInfo(pm, attachmentIds[i]);
			}
			this.updatePM(pm.getId(), attachmentIds.length);
		}
	}
	
	
	/**
	 * save private message in to user in box
	 * @param pm - private message
	 * @param attachmentIds - attachment Id's
	 * @throws Exception
	 */
	protected void saveInToUserInbox(PrivateMessage pm, int attachmentIds[]) throws Exception
	{
		
		PreparedStatement p = this.getStatementForAutoKeys("PrivateMessageModel.add");

		this.addPrivateMessage(pm, p, PrivateMessageType.NEW);
		this.addPmText(pm);

		p.close();
		
		if (attachmentIds != null && attachmentIds.length > 0)
		{
			for (int i = 0; i < attachmentIds.length; i++)
			{
				addPMAttachmentsInfo(pm, attachmentIds[i]);
			}
			this.updatePM(pm.getId(), attachmentIds.length);
		}
	}
	
	protected void addPMAttachmentsInfo(PrivateMessage pm, int attachmentId) throws SQLException
	{
		PreparedStatement pstmt = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PrivateMessagesModel.addAttachmentInfo"));
		pstmt.setInt(1, attachmentId);
		pstmt.setInt(2, pm.getId());
		pstmt.execute();
		pstmt.close();
	}
	
	protected void updatePM(int PrivmsgsId, int count) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("AttachmentModel.updatePM"));
		p.setInt(1, count);
		p.setInt(2, PrivmsgsId);
		p.executeUpdate();
		p.close();
	}
	
	
	protected void addPrivateMessage(PrivateMessage pm, PreparedStatement p, int msgType) throws Exception
	{
		p.setInt(1, msgType);
		p.setString(2, pm.getPost().getSubject());
		p.setInt(3, pm.getFromUser().getId());
		p.setInt(4, pm.getToUser().getId());
		p.setTimestamp(5, new Timestamp(pm.getPost().getTime().getTime()));
		p.setInt(6, pm.getPost().isBbCodeEnabled() ? 1 : 0);
		p.setInt(7, pm.getPost().isHtmlEnabled() ? 1 : 0);
		p.setInt(8, pm.getPost().isSmiliesEnabled() ? 1 : 0);
		p.setInt(9, pm.getPost().isSignatureEnabled() ? 1 : 0);
		p.setInt(10, pm.getPriority());

		this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("PrivateMessagesModel.lastGeneratedPmId"));

		// Mallika's comments beg
		// pm.setId(this.executeAutoKeysQuery(p));
		// Mallika's comments end

		// Mallika's new code beg
		int msgId = this.executeAutoKeysQuery(p);
		pm.setId(msgId);
		CoursePrivateMessage cpm = new CoursePrivateMessage();
		cpm.setCourseId(ToolManager.getCurrentPlacement().getContext());
		cpm.setPrivmsgsId(msgId);
		CoursePrivateMessageDAO cpmDao = DataAccessDriver.getInstance().newCoursePrivateMessageDAO();
		cpmDao.addNew(cpm);
		// Mallika's new code end
	}
	
	
	protected void addPmText(PrivateMessage pm) throws Exception
	{
		PreparedStatement text = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("PrivateMessagesModel.addText"));
		
		text.setInt(1, pm.getId());
		text.setString(2, pm.getPost().getText());
		text.executeUpdate();
		
		text.close();
	}
	
	protected void addPm(PrivateMessage pm, PreparedStatement p) throws Exception
	{
		p.setInt(1, PrivateMessageType.SENT);
		p.setString(2, pm.getPost().getSubject());
		p.setInt(3, pm.getFromUser().getId());
		p.setInt(4, pm.getToUser().getId());
		p.setTimestamp(5, new Timestamp(pm.getPost().getTime().getTime()));
		p.setInt(6, pm.getPost().isBbCodeEnabled() ? 1 : 0);
		p.setInt(7, pm.getPost().isHtmlEnabled() ? 1 : 0);
		p.setInt(8, pm.getPost().isSmiliesEnabled() ? 1 : 0);
		p.setInt(9, pm.getPost().isSignatureEnabled() ? 1 : 0);
		p.setInt(10, pm.getPriority());
		
		this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("PrivateMessagesModel.lastGeneratedPmId"));
		
		//Mallika's comments beg
		//pm.setId(this.executeAutoKeysQuery(p));
		//Mallika's comments end
		
		//Mallika's new code beg
		int msgId = this.executeAutoKeysQuery(p);
		pm.setId(msgId);
		CoursePrivateMessage cpm = new CoursePrivateMessage();
		cpm.setCourseId(ToolManager.getCurrentPlacement().getContext());
		cpm.setPrivmsgsId(msgId);
		CoursePrivateMessageDAO cpmDao = DataAccessDriver.getInstance().newCoursePrivateMessageDAO();
		cpmDao.addNew(cpm);
		//Mallika's new code end
	}
	
	/** 
	 * @see org.etudes.jforum.dao.PrivateMessageDAO#delete(org.etudes.jforum.entities.PrivateMessage[])
	 */
	public void delete(PrivateMessage[] pm) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PrivateMessageModel.delete"));
		p.setInt(2, pm[0].getFromUser().getId());
		p.setInt(3, pm[0].getFromUser().getId());

		PreparedStatement delete = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PrivateMessagesModel.deleteText"));
		
		PreparedStatement deleteAttachInfo = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PrivateMessagesModel.deleteAttachmentInfo"));

		for (int i = 0; i < pm.length; i++)
		{
			p.setInt(1, pm[i].getId());
			p.executeUpdate();

			if (p.getUpdateCount() > 0)
			{
				delete.setInt(1, pm[i].getId());
				delete.executeUpdate();
				
				deleteAttachInfo.setInt(1, pm[i].getId());
				deleteAttachInfo.executeUpdate();
			}
		}

		delete.close();
		deleteAttachInfo.close();
		p.close();

		// Mallika's new code beg
		CoursePrivateMessageDAO cpmDao = DataAccessDriver.getInstance().newCoursePrivateMessageDAO();

		for (int i = 0; i < pm.length; i++)
		{
			cpmDao.delete(pm[i].getId());
		}
		// Mallika's new code end
	}

	/**
	 * @see org.etudes.jforum.dao.PrivateMessageDAO#selectFromInbox(org.etudes.jforum.entities.User)
	 */
	public List selectFromInbox(User user) throws Exception
	{
		String query = SystemGlobals.getSql("PrivateMessageModel.baseListing");
		query = query.replaceAll("#FILTER#", SystemGlobals.getSql("PrivateMessageModel.inbox"));
		
		PreparedStatement p = JForum.getConnection().prepareStatement(query);
		if (logger.isDebugEnabled()) logger.debug("Query inbox is "+query);
		
		p.setInt(1, user.getId());
		
        //Mallika's new code beg
		p.setString(2, ToolManager.getCurrentPlacement().getContext());
		//Mallika's new code end	
		
		List pmList = new ArrayList();

		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			PrivateMessage pm = this.getPm(rs, false);
			
			User fromUser = new User();
			fromUser.setId(rs.getInt("user_id"));
			fromUser.setUsername(rs.getString("username"));
			
			//Mallika's new code beg
			// <<<< 5/23/06 Howie - avoid to assign null value
			fromUser.setFirstName(rs.getString("user_fname")==null ? "" : rs.getString("user_fname"));
			fromUser.setLastName(rs.getString("user_lname")==null ? "" : rs.getString("user_lname"));
			// >>>> 5/23/06 Howie
			//Mallika's new code end
			
			pm.setFromUser(fromUser);
			
			pmList.add(pm);
		}
		
		if (logger.isDebugEnabled()) logger.debug("Pmlist size is "+pmList.size());
		rs.close();
		p.close();
		
		return pmList;
	}

	/** 
	 * @see org.etudes.jforum.dao.PrivateMessageDAO#selectFromSent(org.etudes.jforum.entities.User)
	 */
	public List selectFromSent(User user) throws Exception
	{
		String query = SystemGlobals.getSql("PrivateMessageModel.baseListing");
		query = query.replaceAll("#FILTER#", SystemGlobals.getSql("PrivateMessageModel.sent"));
		
		PreparedStatement p = JForum.getConnection().prepareStatement(query);
		//if (logger.isInfoEnabled()) logger.info("Query sent is "+query);
		p.setInt(1, user.getId());
		
        //Mallika's new code beg
		p.setString(2, ToolManager.getCurrentPlacement().getContext());
		//Mallika's new code end	
				
		List pmList = new ArrayList();

		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			PrivateMessage pm = this.getPm(rs, false);
			
			User toUser = new User();
			toUser.setId(rs.getInt("user_id"));
			toUser.setUsername(rs.getString("username"));
			
			//Mallika's new code beg
			// <<<< 5/23/06 Howie - avoid to assign null value
			toUser.setFirstName(rs.getString("user_fname")==null ? "" : rs.getString("user_fname"));
			toUser.setLastName(rs.getString("user_lname")==null ? "" : rs.getString("user_lname"));
			// >>>> 5/23/06 Howie
			//Mallika's new code end
			
			pm.setToUser(toUser);
			
			pmList.add(pm);
		}
		
		rs.close();
		p.close();
		
		return pmList;
	}
	
	protected PrivateMessage getPm(ResultSet rs) throws Exception
	{
		return this.getPm(rs, true);
	}
	
	protected PrivateMessage getPm(ResultSet rs, boolean full) throws Exception
	{
		PrivateMessage pm = new PrivateMessage();
		Post p = new Post();

		pm.setId(rs.getInt("privmsgs_id"));
		pm.setType(rs.getInt("privmsgs_type"));
		p.setTime(rs.getTimestamp("privmsgs_date"));
		p.setSubject(rs.getString("privmsgs_subject"));
		
		// SimpleDateFormat df = new SimpleDateFormat(SystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
		SimpleDateFormat df = new SimpleDateFormat(SakaiSystemGlobals.getValue(ConfigKeys.DATE_TIME_FORMAT));
		pm.setFormatedDate(df.format(p.getTime()));
		
		if (full) {
			UserDAO um = DataAccessDriver.getInstance().newUserDAO();
			pm.setFromUser(um.selectById(rs.getInt("privmsgs_from_userid")));
			pm.setToUser(um.selectById(rs.getInt("privmsgs_to_userid")));
			
			p.setBbCodeEnabled(rs.getInt("privmsgs_enable_bbcode") == 1);
			p.setSignatureEnabled(rs.getInt("privmsgs_attach_sig") == 1);
			p.setHtmlEnabled(rs.getInt("privmsgs_enable_html") == 1);
			p.setSmiliesEnabled(rs.getInt("privmsgs_enable_smilies") == 1);
			p.setText(HtmlHelper.stripComments(this.getPmText(rs)));
			p.hasAttachments(rs.getInt("privmsgs_attachments") > 0);
		}
		
		pm.setFlagToFollowup(rs.getInt("privmsgs_flag_to_follow") > 0);
		pm.setReplied(rs.getInt("privmsgs_replied") > 0);
		pm.setPriority(rs.getInt("privmsgs_priority"));		
		
		pm.setPost(p);

		return pm;
	}
	
	protected String getPmText(ResultSet rs) throws Exception
	{
		return rs.getString("privmsgs_text");
	}

	/** 
	 * @see org.etudes.jforum.dao.PrivateMessageDAO#selectById(org.etudes.jforum.entities.PrivateMessage)
	 */
	public PrivateMessage selectById(PrivateMessage pm) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PrivateMessageModel.selectById"));
		p.setInt(1, pm.getId());
		
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			pm = this.getPm(rs);
		}
		
		rs.close();
		p.close();
		
		return pm;
	}

	/** 
	 * @see org.etudes.jforum.dao.PrivateMessageDAO#updateType(org.etudes.jforum.entities.PrivateMessage)
	 */
	public void updateType(PrivateMessage pm) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PrivateMessageModel.updateType"));
		p.setInt(1,pm.getType());
		p.setInt(2, pm.getId());
		p.executeUpdate();
		p.close();
	}
	
	//Mallika's new code beg
	public int selectUnreadCount(int userId) throws Exception 
	{
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PrivateMessageModel.selectReadCount"));
		p.setInt(1,userId);
		p.setInt(2,1);
		p.setString(3,ToolManager.getCurrentPlacement().getContext());
		
		ResultSet rs = p.executeQuery();
	
		
		int total = 0;
		if (rs.next()) {
			total = rs.getInt("total");
			
		}
		
		rs.close();
		p.close();

		return total;
	}		
	//Mallika's new code end
	
	/**
	 * @see org.etudes.jforum.dao.PrivateMessageDAO#updateFlagToFollowup(org.etudes.jforum.entities.PrivateMessage)
	 */
	public void updateFlagToFollowup(PrivateMessage pm) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PrivateMessageModel.updateFlagToFollowup"));
		p.setInt(1,pm.isFlagToFollowup() ?  1 : 0);
		p.setInt(2, pm.getId());
		p.executeUpdate();
		p.close();
		
	}
	
	/**
	 * @see org.etudes.jforum.dao.PrivateMessageDAO#updateRepliedStatus(org.etudes.jforum.entities.PrivateMessage)
	 */
	public void updateRepliedStatus(PrivateMessage pm) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("PrivateMessageModel.updateRepliedStatus"));
		p.setInt(1,pm.isReplied() ?  1 : 0);
		p.setInt(2, pm.getId());
		p.executeUpdate();
		p.close();
		
	}
}
