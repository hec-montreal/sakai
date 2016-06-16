/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/GenericUserDAO.java $ 
 * $Id: GenericUserDAO.java 69087 2010-07-07 17:17:00Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008, 2009, 2010 Etudes, Inc. 
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

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//import net.jforum.entities.Group;
//import net.jforum.sakaiproject.SakaiUserDAO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.jforum.JForum;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.entities.KarmaStatus;
import org.etudes.jforum.entities.User;
import org.etudes.jforum.sso.LoginAuthenticator;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.sakaiproject.tool.cover.ToolManager;
//Mallika's import end

/**
 * @author Rafael Steil
 * 08/04/05 - rashmim- modified processSelectAll() to add firstname and lastname to 
 * display in post_show_user_inc.htm page 
 * 08/03/05 -rashmim- modified update(), fillUserFromResultSet(), InitUser() to 
 * add firstname and lastname 
 * rashmim - modified addNew() to associate newly registered user to the right group
 * based on sakai jforum permisssions
 * 8/16/05 - Mallika - adding code to also include courseid in the equation
 * 9/12/05 - Mallika - adding code to find by name for a specific course
 * 9/14/05 - Rashmi - revised findByName() to include firstname and lastname for searching users
 * 9/14/05 - Rashmi - revised selectAllByGroup() to include firstname and lastname for searching users
 * 10/6/05 - Mallika - adding method to find users by first or last name
 * 10/11/05 - Rashmi - add method getNumberOfMessages() to get number of replies posted by a user
 * 10/12/05 - Rashmi - revised selectAll() to get right number of messages
 * 11/01/05 - Murthy - revised selectById() to get user groups for a site
 * 11/03/05 - Murthy - Added addNewUser() method to add a new user but not to the group
 *
 * 5/23/06 - Howie - revised fillUserFromResultSet(),processSelectAll(),selectAllByGroup(),findByName(), and findByFlName() methods to make sure no null (could be empty though) firstname or lastname for jf user.
 *
 * 5/23/06 - Howie - revised fillUserFromResultSet(),processSelectAll(),findByName(), and findByFlName() methods to make sure no null (could be empty though) email for jf user.
 * 07/17/06 - Murthy - updated for sakai's Eid and user Id
 * 10/3/06 - Mallika - added method for selectUserById
 * 09/27/06 - Mallika - getting total users for this course
 * 11/13/06 - Murthy - updated findByName method for sql parameter
 * 11/20/06 - Murthy - updated processSelectAll method to add user object 
 *                     for user_notify value from database
 *
 */
public class GenericUserDAO extends AutoKeys implements org.etudes.jforum.dao.UserDAO 
{
	private static LoginAuthenticator loginAuthenticator;
	
	private static Log logger = LogFactory.getLog(GenericUserDAO.class);
	
	public GenericUserDAO()
	{
		String className = SystemGlobals.getValue(ConfigKeys.LOGIN_AUTHENTICATOR);

		try {
			loginAuthenticator = (LoginAuthenticator)Class.forName(className).newInstance();
			loginAuthenticator.setUserModel(this);
		}
		catch (Exception e) {
			throw new RuntimeException("Error while trying to instantiate a "
					+ "login.authenticator instance (" + className + "): " + e);
		}
	}
	
	/** 
	 * @see org.etudes.jforum.dao.UserDAO#selectById(int)
	 */
	public User selectById(int userId) throws Exception 
	{
	    String q = SystemGlobals.getSql("UserModel.selectById");
		PreparedStatement p = JForum.getConnection().prepareStatement(q);
		p.setInt(1, userId);
		
		ResultSet rs = p.executeQuery();
		User u = new User();
		
		if (rs.next()) {
			this.fillUserFromResultSet(u, rs);
			u.setPrivateMessagesCount(rs.getInt("private_messages"));
			
			// p.close();
			// rs.close();

			// User groups
			//p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectGroups"));
			//11/01/05 Murthy - updated to get user groups for a site
			/*p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectUserGroups"));
			p.setInt(1, userId);
			p.setString(2, ToolManager.getCurrentPlacement().getContext());
			
			rs = p.executeQuery();
			while (rs.next()) {
				Group g = new Group();
				g.setName(rs.getString("group_name"));
				g.setId(rs.getInt("group_id"));

				u.getGroupsList().add(g);
			}*/
		}
		
		rs.close();
		p.close();

		return u;
	}
	
	//Mallika - new method for member listing
	public User selectUserById(int userId) throws Exception 
	{
	    String q = SystemGlobals.getSql("UserModel.selectUserById");
		PreparedStatement p = JForum.getConnection().prepareStatement(q);
		p.setInt(1, userId);
		
		ResultSet rs = p.executeQuery();
		User u = new User();
		
		if (rs.next()) {
			this.fillUserFromResultSet(u, rs);
			//SETTING THIS TO ZERO SINCE WE DON'T NEED IT ON THE MEMBER LISTING
			//PAGE
			u.setPrivateMessagesCount(0);
			
			//p.close();
			//rs.close();

			// User groups
			//p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectGroups"));
			//11/01/05 Murthy - updated to get user groups for a site
			/*p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectUserGroups"));
			p.setInt(1, userId);
			p.setString(2, ToolManager.getCurrentPlacement().getContext());
			
			rs = p.executeQuery();
			while (rs.next()) {
				Group g = new Group();
				g.setName(rs.getString("group_name"));
				g.setId(rs.getInt("group_id"));

				u.getGroupsList().add(g);
			}*/
		}
		
		rs.close();
		p.close();

		return u;
	}	
	
	public User selectByName(String username) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectByName"));
		p.setString(1, username);
		
		ResultSet rs = p.executeQuery();
		User u = null;
		
		if (rs.next()) {
			u = new User();
			fillUserFromResultSet(u, rs);
		}
		
		rs.close();
		p.close();

		return u;
	}
	
	public User selectBySakaiUserId(String sakUserId) throws Exception{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectBySakaiUserId"));
		p.setString(1, sakUserId);
		
		ResultSet rs = p.executeQuery();
		User u = null;
		
		if (rs.next()) {
			u = new User();
			fillUserFromResultSet(u, rs);
		}
		
		rs.close();
		p.close();

		return u;
	}

	protected void fillUserFromResultSet(User u, ResultSet rs) throws Exception 
	{
		u.setAim(rs.getString("user_aim"));
		u.setAvatar(rs.getString("user_avatar"));
		u.setGender(rs.getString("gender"));
		u.setRankId(rs.getInt("rank_id"));
		u.setThemeId(rs.getInt("themes_id"));
		u.setPrivateMessagesEnabled(rs.getInt("user_allow_pm") == 1);
		u.setNotifyOnMessagesEnabled(rs.getInt("user_notify") == 1);
		u.setViewOnlineEnabled(rs.getInt("user_viewonline") == 1);
		u.setPassword(rs.getString("user_password"));
		u.setViewEmailEnabled(rs.getInt("user_viewemail") == 1);
		u.setViewOnlineEnabled(rs.getInt("user_allow_viewonline") == 1);
		u.setAvatarEnabled(rs.getInt("user_allowavatar") == 1);
		u.setBbCodeEnabled(rs.getInt("user_allowbbcode") == 1);
		u.setHtmlEnabled(rs.getInt("user_allowhtml") == 1);
		u.setSmiliesEnabled(rs.getInt("user_allowsmilies") == 1);
		// <<<< 5/23/06 Howie - avoid to assign null value
		u.setEmail(rs.getString("user_email")==null ? "" : rs.getString("user_email"));
		// >>>> 5/23/06 Howie
		u.setFrom(rs.getString("user_from"));
		u.setIcq(rs.getString("user_icq"));
		u.setId(rs.getInt("user_id"));
		u.setInterests(rs.getString("user_interests"));
		u.setLastVisit(rs.getTimestamp("user_lastvisit"));
		u.setOccupation(rs.getString("user_occ"));
		u.setTotalPosts(rs.getInt("user_posts"));
		u.setRegistrationDate(rs.getTimestamp("user_regdate"));
		u.setSignature(rs.getString("user_sig"));
		u.setWebSite(rs.getString("user_website"));
		u.setYim(rs.getString("user_yim"));
		u.setUsername(rs.getString("username"));
		u.setAttachSignatureEnabled(rs.getInt("user_attachsig") == 1);
		u.setMsnm(rs.getString("user_msnm"));
		u.setFaceBookAccount(rs.getString("user_facebook_account"));
		u.setTwitterAccount(rs.getString("user_twitter_account"));
		u.setLang(rs.getString("user_lang"));
		u.setActive(rs.getInt("user_active"));
		u.setKarma(new KarmaStatus(u.getId(), rs.getDouble("user_karma")));
		u.setNotifyPrivateMessagesEnabled(rs.getInt("user_notify_pm") == 1); 
		u.setDeleted(rs.getInt("deleted"));
		
		String actkey = rs.getString("user_actkey");
		u.setActivationKey(actkey == null || "".equals(actkey) ? null : actkey);
		
		//added by rashmi
		// <<<< 5/23/06 Howie - avoid to assign null value
		u.setFirstName(rs.getString("user_fname")==null ? "" : rs.getString("user_fname"));
		u.setLastName(rs.getString("user_lname")==null ? "" : rs.getString("user_lname"));
		// >>>> 5/23/06 Howie
		//07/17/06 Murthy
		u.setSakaiUserId(rs.getString("sakai_user_id"));
	}

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#delete(int)
	 */
	public void delete(int userId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.deletedStatus"));
		p.setInt(1, 1);
		p.setInt(2, userId);
		
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#update(net.jforum.User)
	 */
	public void update(User user) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.update"));
		
		p.setString(1, user.getAim());
		p.setString(2, user.getAvatar());
		p.setString(3, user.getGender());
		p.setInt(4, user.getThemeId());
		p.setInt(5, user.isPrivateMessagesEnabled() ? 1 : 0);
		p.setInt(6, user.isAvatarEnabled() ? 1 : 0);
		p.setInt(7, user.isBbCodeEnabled() ? 1 : 0);
		p.setInt(8, user.isHtmlEnabled() ? 1 : 0);
		p.setInt(9, user.isSmiliesEnabled() ? 1 : 0);
		p.setString(10, user.getEmail());
		p.setString(11, user.getFrom());
		p.setString(12, user.getIcq());		
		p.setString(13, user.getInterests());
		p.setString(14, user.getOccupation());
		p.setString(15, user.getSignature());
		p.setString(16, user.getWebSite());
		p.setString(17, user.getYim());
		p.setString(18, user.getMsnm());
		p.setString(19, user.getPassword());
		p.setInt(20, user.isViewEmailEnabled() ? 1 : 0);
		p.setInt(21, user.isViewOnlineEnabled() ? 1 : 0);
		p.setInt(22, user.isNotifyOnMessagesEnabled() ? 1 : 0);
		p.setInt(23, user.getAttachSignatureEnabled() ? 1 : 0);
		p.setString(24, user.getUsername());
		if (user.getLang() == null)
			p.setString(25, "");
        else
           	p.setString(25, user.getLang());		
		p.setInt(26, user.isNotifyPrivateMessagesEnabled() ? 1 : 0);
		//added by rashmi
		p.setString(27, user.getFirstName());
		p.setString(28, user.getLastName());
		p.setString(29, user.getFaceBookAccount());
		p.setString(30, user.getTwitterAccount());
		p.setInt(31, user.getId());
		
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#addNew(net.jforum.User)
	 */
	public int addNew(User user) throws Exception 
	{
		if (logger.isDebugEnabled()) logger.debug("genericUserDAO user fname and lname = " + user.getFirstName() + " " + user.getLastName());
		PreparedStatement p = this.getStatementForAutoKeys("UserModel.addNew");
		
		this.initNewUser(user, p);
		
		this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("UserModel.lastGeneratedUserId"));
		int id = this.executeAutoKeysQuery(p);
		p.close();
		
		//this.addToGroup(id, new int[] { SystemGlobals.getIntValue(ConfigKeys.DEFAULT_USER_GROUP) });
		
		// added by rashmi -get group based on role permissions
		/*try{
			SakaiUserDAO s = new SakaiUserDAO();
			this.addToGroup(id, new int[]{s.getGroupId()});
			} catch(Exception e)
			{
				logger.error(this.getClass().getName() + 
						".addNew() : " + e.getMessage(), e);
				throw e;
			}*/
		// end of new add
		user.setId(id);
		return id;
	}
	
	/**
	 * @see org.etudes.jforum.dao.UserDAO#addNewUser(net.jforum.User)
	 */
	public int addNewUser(User user) throws Exception 
	{
		PreparedStatement p = this.getStatementForAutoKeys("UserModel.addNew");
		
		this.initNewUser(user, p);
		
		this.setAutoGeneratedKeysQuery(SystemGlobals.getSql("UserModel.lastGeneratedUserId"));
		int id = this.executeAutoKeysQuery(p);
		p.close();
		
		user.setId(id);
		return id;
	}

	protected void initNewUser(User user, PreparedStatement p) throws Exception 
	{
		p.setString(1, user.getUsername());
		p.setString(2, user.getPassword());
		p.setString(3, user.getEmail());
		p.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
		p.setString(5, user.getActivationKey());
		//rashmi added 
		p.setString(6,user.getFirstName());
		p.setString(7,user.getLastName());
		//07/17/06 - Murthy
		p.setString(8,user.getSakaiUserId());
	}

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#addNewWithId(net.jforum.User)
	 */
	public void addNewWithId(User user) throws Exception 
	{
		PreparedStatement p = this.getStatementForAutoKeys("UserModel.addNewWithId");
		
		this.initNewUser(user, p);
		p.setInt(6, user.getId());
		
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#decrementPosts(int)
	 */
	public void decrementPosts(int userId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.decrementPosts"));
		p.setInt(1, userId);
		
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#incrementPosts(int)
	 */
	public void incrementPosts(int userId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.incrementPosts"));
		p.setInt(1, userId);
		
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#incrementRanking(int)
	 */
	public void setRanking(int userId, int rankingId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.rankingId"));
		p.setInt(1, rankingId);
		p.setInt(2, userId);
		
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#setActive(int, boolean)
	 */
	public void setActive(int userId, boolean active) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.activeStatus"));
		p.setInt(1, active ? 1 : 0);
		p.setInt(2, userId);
		
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#undelete(int)
	 */
	public void undelete(int userId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.deletedStatus"));
		p.setInt(1, 0);
		p.setInt(2, userId);
		
		p.executeUpdate();
		p.close();
	}

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#selectAll()
	 */
	public List selectAll() throws Exception 
	{
		return selectAll(0, 0);
	}

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#selectAll(int, int)
	 */
	public List selectAll(int startFrom, int count) throws Exception 
	{
		PreparedStatement p;
		
		if (logger.isDebugEnabled()) logger.debug("GenericUserDAO-getting all users for course");
		
		if (count > 0) {
			if (logger.isDebugEnabled()) logger.debug("GenericUserDAO-selectAllby limit called");
			p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectAllByLimit"));
			
			//Mallika's comments beg
			//p.setInt(1, startFrom);
			//p.setInt(2, count);
			//Mallika's comments end
			
            //Mallika's new code beg
			p.setString(1, ToolManager.getCurrentPlacement().getContext());
			p.setInt(2, startFrom);
			p.setInt(3, count);			
			//Mallika's new code end
		}
		else {
		
			if (logger.isDebugEnabled()) logger.debug("GenericUserDAO-selectAll called");
			p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectAll"));
            //Mallika's new code beg
			p.setString(1, ToolManager.getCurrentPlacement().getContext());
			
			//Mallika's new code end
		}
		
		ResultSet rs = p.executeQuery();
		List list = this.processSelectAll(rs);
		
		Iterator iter = list.iterator();
		while(iter.hasNext())
		{
			User u = (User)iter.next();
			if (logger.isDebugEnabled()) logger.debug("user info " + u.getUsername());
			u.setTotalPosts(getNumberOfMessages(u.getId()));		
		}
		rs.close();
		p.close();
		
		return list;
	}
	
	
	
	/** 
	 * @see org.etudes.jforum.dao.UserDAO#selectAllBySakUserId(int, int)
	 */
	public List selectAllBySakUserId(int startFrom, int count) throws Exception 
	{
		PreparedStatement p;
		
		if (logger.isDebugEnabled()) logger.debug("GenericUserDAO-getting all users for course");
		
		if (count > 0) {
			if (logger.isDebugEnabled()) logger.debug("GenericUserDAO-selectAllby limit called");
			p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectAllByLimit"));
			
			//Mallika's comments beg
			//p.setInt(1, startFrom);
			//p.setInt(2, count);
			//Mallika's comments end
			
            //Mallika's new code beg
			p.setString(1, ToolManager.getCurrentPlacement().getContext());
			p.setInt(2, startFrom);
			p.setInt(3, count);			
			//Mallika's new code end
		}
		else {
		
			if (logger.isDebugEnabled()) logger.debug("GenericUserDAO-selectAll called");
			p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectAll"));
            //Mallika's new code beg
			p.setString(1, ToolManager.getCurrentPlacement().getContext());
			
			//Mallika's new code end
		}
		
		ResultSet rs = p.executeQuery();
		List list = this.processSelectAll(rs);
		
		Iterator iter = list.iterator();
		while(iter.hasNext())
		{
			User u = (User)iter.next();
			if (logger.isDebugEnabled()) logger.debug("user info " + u.getUsername());
			u.setTotalPosts(getNumberOfMessages(u.getId()));		
		}
		rs.close();
		p.close();
		
		return list;
	}
	
	
	
	/** 
	 * @see org.etudes.jforum.dao.UserDAO#selectAllWithKarma()
	 */
	public List selectAllWithKarma() throws Exception 
	{
		return this.selectAllWithKarma(0, 0);
	}	
	
	/** 
	 * @see org.etudes.jforum.dao.UserDAO#selectAllWithKarma(int, int)
	 */
	public List selectAllWithKarma(int startFrom, int count) throws Exception 
	{
	    return this.loadKarma( this.selectAll(startFrom, count) );
	}

	
	protected List processSelectAll(ResultSet rs) throws Exception
	{
		List list = new ArrayList();
		while (rs.next()) {
			User u = new User();
			
			// <<<< 5/23/06 Howie - avoid to assign null value
			u.setEmail(rs.getString("user_email")==null ? "" : rs.getString("user_email"));
			// >>>> 5/23/06 Howie
			u.setId(rs.getInt("user_id"));
			u.setTotalPosts(rs.getInt("user_posts"));
			u.setRegistrationDate(rs.getTimestamp("user_regdate"));
			u.setUsername(rs.getString("username"));
			u.setDeleted(rs.getInt("deleted"));
			KarmaStatus karma = new KarmaStatus();
			karma.setKarmaPoints(rs.getInt("user_karma"));
			u.setKarma( karma );
			u.setFrom(rs.getString("user_from"));
			u.setWebSite(rs.getString("user_website"));
			u.setViewEmailEnabled(rs.getInt("user_viewemail") == 1);
			// added by rashmi
			// <<<< 5/23/06 Howie - avoid to assign null value
			u.setFirstName(rs.getString("user_fname")==null ? "" : rs.getString("user_fname"));
			u.setLastName(rs.getString("user_lname")==null ? "" : rs.getString("user_lname"));
			// >>>> 5/23/06 Howie
			// end of add
			
			//07/17/06 Murthy
			u.setSakaiUserId(rs.getString("sakai_user_id"));
			//<<< 11/20/06 Murthy - added the below lines
			u.setPrivateMessagesEnabled(rs.getInt("user_allow_pm") == 1);
			u.setNotifyOnMessagesEnabled(rs.getInt("user_notify") == 1);
			//>>> 11/20/06 Murthy
			list.add(u);
		}
		
		return list;
	}
	
	/**
	 * @see net.jforum.dao.UserDAO#selectAllByGroup(int, int, int)
	 */
	/*public List selectAllByGroup(int groupId, int start, int count) throws Exception
	{
		List l = new ArrayList();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectAllByGroup"));
		p.setInt(1, groupId);
		p.setInt(2, start);
		p.setInt(3, count);
		
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			User u = new User();
			u.setUsername(rs.getString("username"));
			u.setId(rs.getInt("user_id"));
			u.setRegistrationDate(rs.getTimestamp("user_regdate"));
			// add by rashmi
			// <<<< 5/23/06 Howie - avoid to assign null value
			u.setFirstName(rs.getString("user_fname")==null ? "" : rs.getString("user_fname"));
			u.setLastName(rs.getString("user_lname")==null ? "" : rs.getString("user_lname"));
			// >>>> 5/23/06 Howie
			// add end
			l.add(u);
		}
		
		return l;
	}*/

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#getLastUserInfo()
	 */
	public User getLastUserInfo() throws Exception 
	{
		User u = new User();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.lastUserRegistered"));
		ResultSet rs = p.executeQuery();
		rs.next();
		
		u.setUsername(rs.getString("username"));
		u.setId(rs.getInt("user_id"));
		
		rs.close();
		p.close();
		
		return u;
	}

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#getTotalUsers()
	 */
	public int getTotalUsers() throws Exception
	{
		//Mallika - This code changed to get total users per course
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("UserModel.totalUsers"));
		p.setString(1, ToolManager.getCurrentPlacement().getContext());
		return this.getTotalUsersCommon(p);
	}
	
	/**
	 * @see org.etudes.jforum.dao.UserDAO#getTotalUsersByGroup(int)
	 */
	/*public int getTotalUsersByGroup(int groupId) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(
				SystemGlobals.getSql("UserModel.totalUsersByGroup"));
		p.setInt(1, groupId);
		
		return this.getTotalUsersCommon(p);
	}*/
	
	protected int getTotalUsersCommon(PreparedStatement p) throws Exception
	{
		ResultSet rs = p.executeQuery();
		rs.next();
		
		int total = rs.getInt(1);
		
		rs.close();
		p.close();
		
		return total;
	}

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#isDeleted(int user_id)
	 */
	public boolean isDeleted(int userId) throws Exception 
	{	
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.isDeleted"));
		p.setInt(1, userId);
		
		int deleted = 0;
		
		ResultSet rs = p.executeQuery();
		if (rs.next()) {	
			deleted = rs.getInt("deleted");
		}
		
		rs.close();
		p.close();
		
		return deleted == 1;
	}
	
	/** 
	 * @see org.etudes.jforum.dao.UserDAO#isUsernameRegistered(java.lang.String)
	 */
	public boolean isUsernameRegistered(String username) throws Exception 
	{
		boolean status = false;
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.isUsernameRegistered"));
		p.setString(1, username);
		
		ResultSet rs = p.executeQuery();
		if (rs.next() && rs.getInt("registered") > 0) {
			status = true;
		}
		
		rs.close();
		p.close();
		
		return status;
	}

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#validateLogin(java.lang.String, java.lang.String)
	 */
	public User validateLogin(String username, String password) throws NoSuchAlgorithmException, Exception
	{
		return loginAuthenticator.validateLogin(username, password, null);
	}

	/** 
	 * @see net.jforum.dao.UserDAO#addToGroup(int, int[])
	 */
	/*public void addToGroup(int userId, int[] groupId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.addToGroup"));
		p.setInt(1, userId);
		
		for (int i = 0; i < groupId.length; i++) {
			p.setInt(2, groupId[i]);
			p.executeUpdate();
		}
		
		p.close();
	}*/

	/** 
	 * @see net.jforum.dao.UserDAO#removeFromGroup(int, int[])
	 */
	/*public void removeFromGroup(int userId, int[] groupId) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.removeFromGroup"));
		p.setInt(1, userId);
		
		for (int i = 0; i < groupId.length; i++) {
			p.setInt(2, groupId[i]);
			p.executeUpdate();
		}
		
		p.close();
	}*/

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#saveNewPassword(java.lang.String, java.lang.String)
	 */
	public void saveNewPassword(String password, String email) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.saveNewPassword"));
		p.setString(1, password);
		p.setString(2, email);
		p.executeUpdate();
		p.close();
	}
	
	/** 
	 * @see org.etudes.jforum.dao.UserDAO#validateLostPasswordHash(java.lang.String, java.lang.String)
	 */
	public boolean validateLostPasswordHash(String email, String hash) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.validateLostPasswordHash"));
		p.setString(1, hash);
		p.setString(2, email);
		
		boolean status = false;
		
		ResultSet rs = p.executeQuery();
		if (rs.next() && rs.getInt("valid") == 1) {
			status = true;
			
			this.writeLostPasswordHash(email, "");
		}
		
		rs.close();
		p.close();
		
		return status;		
	}
	
	/** 
	 * @see org.etudes.jforum.dao.UserDAO#writeLostPasswordHash(java.lang.String, java.lang.String)
	 */
	public void writeLostPasswordHash(String email, String hash) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.writeLostPasswordHash"));
		p.setString(1, hash);
		p.setString(2, email);
		p.executeUpdate();
		p.close();
	}
	
	/** 
	 * @see org.etudes.jforum.dao.UserDAO#getUsernameByEmail(java.lang.String)
	 */
	public String getUsernameByEmail(String email) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.getUsernameByEmail"));
		p.setString(1, email);
		
		String username = "";
		
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			username = rs.getString("username");
		}
		
		rs.close();
		p.close();
		
		return username;
	}

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#findByName(java.lang.String, boolean)
	 */
	public List findByName(String input, boolean exactMatch) throws Exception
	{
		List namesList = new ArrayList();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.findByName"));
		//Mallika's comments beg
		//p.setString(1, exactMatch ? input : "%" + input + "%");
		//Mallika's comments end
		
		//Mallika's new code beg
		p.setString(1, exactMatch ? input : "%" + input + "%");
		p.setString(2, exactMatch ? input : "%" + input + "%");
		p.setString(3, ToolManager.getCurrentPlacement().getContext());
		//Mallika's new code end
	
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			User u = new User();
			u.setId(rs.getInt("user_id"));
			u.setUsername(rs.getString("username"));
			// <<<< 5/23/06 Howie - avoid to assign null value
			u.setEmail(rs.getString("user_email")==null ? "" : rs.getString("user_email"));
			// >>>> 5/23/06 Howie
			// add by rashmi
			// <<<< 5/23/06 Howie - avoid to assign null value
			u.setFirstName(rs.getString("user_fname")==null ? "" : rs.getString("user_fname"));
			u.setLastName(rs.getString("user_lname")==null ? "" : rs.getString("user_lname"));
			// >>>> 5/23/06 Howie
			// add end
			namesList.add(u);
		}
		
		rs.close();
		p.close();
		
		return namesList;
	}
	
	//Mallika's new code beg
	public List findByFlName(String input, boolean exactMatch) throws Exception
	{
		List namesList = new ArrayList();
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.findByFlName"));
		
		p.setString(1, exactMatch ? input : "%" + input + "%");
		p.setString(2, exactMatch ? input : "%" + input + "%");
		p.setString(3, ToolManager.getCurrentPlacement().getContext());
	
	
		ResultSet rs = p.executeQuery();
		while (rs.next()) {
			User u = new User();
			u.setId(rs.getInt("user_id"));
			u.setUsername(rs.getString("username"));
			// <<<< 5/23/06 Howie - avoid to assign null value
			u.setEmail(rs.getString("user_email")==null ? "" : rs.getString("user_email"));
			// >>>> 5/23/06 Howie
			// add by rashmi
			// <<<< 5/23/06 Howie - avoid to assign null value
			u.setFirstName(rs.getString("user_fname")==null ? "" : rs.getString("user_fname"));
			u.setLastName(rs.getString("user_lname")==null ? "" : rs.getString("user_lname"));
			// >>>> 5/23/06 Howie
			// add end
			namesList.add(u);
		}
		
		rs.close();
		p.close();
		
		return namesList;
	}
	//Mallika's new code end
	
	
	/** 
	 * @see org.etudes.jforum.dao.UserDAO#validateActivationKeyHash(int, java.lang.String)
	 */
	public boolean validateActivationKeyHash(int userId , String hash) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.validateActivationKeyHash"));
		p.setString(1, hash);
		p.setInt(2, userId);

		boolean status = false;

		ResultSet rs = p.executeQuery();
		if (rs.next() && rs.getInt("valid") == 1) {
			status = true;
		}

		rs.close();
		p.close();

		return status;
	}

	/** 
	 * @see org.etudes.jforum.dao.UserDAO#writeUserActive(int)
	 */
	public void writeUserActive(int userId) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.writeUserActive"));
		p.setInt(1, userId);
		p.executeUpdate();
		p.close();
	}
	
	/** 
	 * @see org.etudes.jforum.dao.UserDAO#updateUsername(int, String)
	 */
	public void updateUsername(int userId, String username) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.updateUsername"));
		p.setString(1, username);
		p.setInt(2, userId);
		p.executeUpdate();
		p.close();
	}
	
	/**
	 * @see org.etudes.jforum.dao.UserDAO#hasUsernameChanged(int, java.lang.String)
	 */
	public boolean hasUsernameChanged(int userId, String usernameToCheck) throws Exception
	{
		boolean status = false;
		
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.getUsername"));
		p.setString(1, usernameToCheck);
		p.setInt(2, userId);
		
		String dbUsername = null;
		
		ResultSet rs = p.executeQuery();
		if (rs.next()) {
			dbUsername = rs.getString("username");
		}
		
		if (!usernameToCheck.equals(dbUsername)) {
			status = true;
		}
		
		rs.close();
		p.close();
		
		return status;
	}
	
	/**
	 * Load KarmaStatus from a list of users.
	 * @param users 
	 * @return
	 * @throws Exception
	 */
	protected List loadKarma(List users) throws Exception{
	    List result = new ArrayList(users.size());
	    
	    User user = null;
		Iterator iter = users.iterator(); 
		while (iter.hasNext()) {		    
		    user = (User) iter.next();
		    //load Karma
		    DataAccessDriver.getInstance().newKarmaDAO().getUserTotalKarma(user);
		    result.add(user);
        }		
		return result;
	}
	/*
	 * add by rashmi to get number of messages posted by a user
	 * and to make it site aware 
	 * @see net.jforum.dao.UserDAO#getNumberOfMessages(int)
	 */
	public int getNumberOfMessages(int userId) throws Exception
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.getNumberOfMessages"));
		p.setInt(1, userId);
		p.setString(2,ToolManager.getCurrentPlacement().getContext());
		int noOfMsgs = 0;
		
		ResultSet rs = p.executeQuery();
		if (rs !=null && rs.next()) {
			noOfMsgs = rs.getInt("num_messages");
		}
		
		rs.close();
		p.close();
		
		return noOfMsgs;
	}

	/**
	 * @inheritDoc
	 */
	public void addToSite(int userId) throws Exception {
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.addToSite"));
		p.setString(1, ToolManager.getCurrentPlacement().getContext());
		p.setInt(2, userId);
		
		p.executeUpdate();
	
		p.close();
		
	}

	/**
	 * @inheritDoc
	 */
	public void removeFromSite(int userId) throws Exception {
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.removeFromSite"));
		p.setString(1, ToolManager.getCurrentPlacement().getContext());
		p.setInt(1, userId);
		
		p.executeUpdate();
	
		p.close();
		
	}

	/**
	 * @inheritDoc
	 */
	public List selectAllSiteUsers() throws Exception {
		return selectAllSiteUsers(0, 0, false);
	}
	
	/**
	 * @inheritDoc
	 */
	public List selectAllSiteUsersWithTotalPosts() throws Exception {
		return selectAllSiteUsers(0, 0, true);
	}	

	/**
	 * @inheritDoc
	 */
	public List selectAllSiteUsers(int startFrom, int count, boolean getTotalPosts) throws Exception {
		PreparedStatement p;
		
		if (logger.isDebugEnabled()) logger.debug("GenericUserDAO-getting all users for course");
		
		if (count > 0) {
			if (logger.isDebugEnabled()) logger.debug("processsing UserModel.selectAllSiteUsersByLimit");
			p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectAllSiteUsersByLimit"));
		
			p.setString(1, ToolManager.getCurrentPlacement().getContext());
			p.setInt(2, startFrom);
			p.setInt(3, count);			
		}
		else {
		
			if (logger.isDebugEnabled()) logger.debug("processsing UserModel.selectAllSiteUsers");
			p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.selectAllSiteUsers"));
			p.setString(1, ToolManager.getCurrentPlacement().getContext());
		}
		
		ResultSet rs = p.executeQuery();
		List list = this.processSelectAll(rs);
	
		if (getTotalPosts){
			Iterator iter = list.iterator();
			while(iter.hasNext())
			{
				User u = (User)iter.next();
				u.setTotalPosts(getNumberOfMessages(u.getId()));		
			}
		}
		
		rs.close();
		p.close();
		
		return list;
	}

	/**
	 * @inheritDoc
	 */
	public void updateSakaiAccountDetails(User user) throws Exception 
	{
		PreparedStatement p = JForum.getConnection().prepareStatement(SystemGlobals.getSql("UserModel.updateSakaiAccountDetails"));
		
		p.setString(1, user.getEmail());
		p.setString(2, user.getUsername());
		p.setString(3, user.getFirstName());
		p.setString(4, user.getLastName());
		if (user.getLang() == null)
			p.setString(5, "");
        else
           	p.setString(5, user.getLang());		
		p.setString(6, user.getPassword());
		p.setInt(7, user.getId());
		
		p.executeUpdate();
		p.close();
		
	}
}
