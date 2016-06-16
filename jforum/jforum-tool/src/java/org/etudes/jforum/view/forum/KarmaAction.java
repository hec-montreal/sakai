/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/forum/KarmaAction.java $ 
 * $Id: KarmaAction.java 61928 2009-07-21 22:29:16Z murthy@etudes.org $ 
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
package org.etudes.jforum.view.forum;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.etudes.jforum.Command;
import org.etudes.jforum.JForum;
import org.etudes.jforum.SessionFacade;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.dao.KarmaDAO;
import org.etudes.jforum.dao.PostDAO;
import org.etudes.jforum.entities.Karma;
import org.etudes.jforum.entities.Post;
import org.etudes.jforum.security.SecurityConstants;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.preferences.ConfigKeys;
import org.etudes.jforum.util.preferences.SakaiSystemGlobals;
import org.etudes.jforum.util.preferences.SystemGlobals;
import org.etudes.jforum.util.preferences.TemplateKeys;
import org.etudes.jforum.view.forum.common.ViewCommon;

//import net.jforum.repository.SecurityRepository;

/**
 * @author Rafael Steil
 */
public class KarmaAction extends Command
{
	public void insert() throws Exception
	{
		/*if (!SecurityRepository.canAccess(SecurityConstants.PERM_KARMA_ENABLED)) {
			this.error("Karma.featureDisabled", null);
			return;
		}*/

		int postId = this.request.getIntParameter("post_id");
		int fromUserId = SessionFacade.getUserSession().getUserId();

		PostDAO pm = DataAccessDriver.getInstance().newPostDAO();
		Post p = pm.selectById(postId);

		if (fromUserId == SystemGlobals.getIntValue(ConfigKeys.ANONYMOUS_USER_ID)) {
			this.error("Karma.anonymousIsDenied", p);
			return;
		}

		if (p.getUserId() == fromUserId) {
			this.error("Karma.cannotSelfVote", p);
			return;
		}

		KarmaDAO km = DataAccessDriver.getInstance().newKarmaDAO();
		
		if (!km.userCanAddKarma(fromUserId, postId)) {
			this.error("Karma.alreadyVoted", p);
			return;
		}
		
		// Check range
		int points = this.request.getIntParameter("points");
		
		if (points < SystemGlobals.getIntValue(ConfigKeys.KARMA_MIN_POINTS)
				|| points > SystemGlobals.getIntValue(ConfigKeys.KARMA_MAX_POINTS)) {
			this.error("Karma.invalidRange", p);
			return;
		}

		Karma karma = new Karma();
		karma.setFromUserId(fromUserId);
		karma.setPostUserId(p.getUserId());
		karma.setPostId(postId);
		karma.setTopicId(p.getTopicId());
		karma.setPoints(points);

		km.addKarma(karma);

		JForum.setRedirect(this.urlToTopic(p));
	}

	private void error(String message, Post p)
	{
		this.setTemplateName(TemplateKeys.KARMA_ERROR);

		if (p != null) {
			this.context.put("message", I18n.getMessage(message, new String[] { this.urlToTopic(p) }));
		}
		else {
			this.context.put("message", I18n.getMessage(message));
		}
	}

	private String urlToTopic(Post p)
	{
		return JForum.getRequest().getContextPath() + "/posts/list/" 
			+ ViewCommon.getStartPage()
			+ "/" + p.getTopicId()
			+ SystemGlobals.getValue(ConfigKeys.SERVLET_EXTENSION)
			+ "#" + p.getId();
	}

	/**
	 * @see org.etudes.jforum.Command#list()
	 */
	public void list() throws Exception
	{
		this.setTemplateName(TemplateKeys.KARMA_LIST);
		this.context.put("message", I18n.getMessage("invalidAction"));
	}

	/**
	 * TODO: Make dynamic data format TODO: refactoring here to remove the duplicated code with the
	 * method above. Performs a search over the users votes between two dates.
	 * 
	 * @throws Exception
	 */
	public void searchByPeriod() throws Exception
	{
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy");
		Date firstPeriod, lastPeriod;
		if ("".equals(this.request.getParameter("first_date"))) {
			firstPeriod = formater.parse("01/01/1970");// extreme date
		}
		else {
			firstPeriod = formater.parse(this.request.getParameter("first_date"));
		}
		if ("".equals(this.request.getParameter("last_date"))) {
			lastPeriod = new Date();// now
		}
		else {
			lastPeriod = formater.parse(this.request.getParameter("last_date"));
		}

		String orderField;
		if ("".equals(this.request.getParameter("order_by"))) {
			orderField = "total";
		}
		else {
			orderField = this.request.getParameter("order_by");
		}

		int start = this.preparePagination(DataAccessDriver.getInstance().newUserDAO().getTotalUsers());
		// int usersPerPage = SystemGlobals.getIntValue(ConfigKeys.USERS_PER_PAGE);
		int usersPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.USERS_PER_PAGE);
		// Load all users with your karma
		List users = DataAccessDriver.getInstance().newKarmaDAO().getMostRatedUserByPeriod(usersPerPage, firstPeriod,
				lastPeriod, orderField);
		this.context.put("users", users);
		this.setTemplateName(TemplateKeys.KARMA_SEARCH_BYPERIOD);
	}

	/**
	 * FIXME: The date format is not dynamic.
	 * 
	 * Performs a search over the users votes in a specific month.
	 * 
	 * @throws Exception
	 */
	public void searchByMonth() throws Exception
	{
		SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		int month = Integer.parseInt(request.getParameter("month"));
		int year = Integer.parseInt(request.getParameter("year"));

		Calendar c = Calendar.getInstance();
		Date firstPeriod, lastPeriod;
		firstPeriod = formater.parse("01/" + month + "/" + year+ " 00:00:00");
		// set the Calendar with the first day of the month
		c.setTime(firstPeriod);
		// Now get the last day of this month.
		lastPeriod = formater.parse(c.getActualMaximum(Calendar.DAY_OF_MONTH) + "/" + month + "/" + year +" 23:59:59");

		String orderField;
		if ("".equals(request.getParameter("order_by")) || request.getParameter("order_by") == null) {
			orderField = "total";
		}
		else {
			orderField = this.request.getParameter("order_by");
		}

		int start = this.preparePagination(DataAccessDriver.getInstance().newUserDAO().getTotalUsers());
		// int usersPerPage = SystemGlobals.getIntValue(ConfigKeys.USERS_PER_PAGE);
		int usersPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.USERS_PER_PAGE);
		// Load all users with your karma
		List users = DataAccessDriver.getInstance().newKarmaDAO().getMostRatedUserByPeriod(usersPerPage, firstPeriod,
				lastPeriod, orderField);
		this.context.put("users", users);
		this.setTemplateName(TemplateKeys.KARMA_SEARCH_BYMONTH);
	}

	private int preparePagination(int totalUsers)
	{
		int start = ViewCommon.getStartPage();
		// int usersPerPage = SystemGlobals.getIntValue(ConfigKeys.USERS_PER_PAGE);
		int usersPerPage = SakaiSystemGlobals.getIntValue(ConfigKeys.USERS_PER_PAGE);

		this.context.put("totalPages", new Double(Math.ceil((double) totalUsers / usersPerPage)));
		this.context.put("recordsPerPage", new Integer(usersPerPage));
		this.context.put("totalRecords", new Integer(totalUsers));
		this.context.put("thisPage", new Double(Math.ceil((double) (start + 1) / usersPerPage)));
		this.context.put("start", new Integer(start));

		return start;
	}
}
