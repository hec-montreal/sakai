/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/view/admin/BannerAction.java $ 
 * $Id: BannerAction.java 55473 2008-12-01 18:49:13Z murthy@etudes.org $ 
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

import org.etudes.jforum.dao.BannerDAO;
import org.etudes.jforum.dao.DataAccessDriver;
import org.etudes.jforum.entities.Banner;
import org.etudes.jforum.util.I18n;
import org.etudes.jforum.util.preferences.TemplateKeys;

/**
 * ViewHelper class for banner administration.
 *
 * @author Samuel Yung
 */
public class BannerAction extends AdminCommand
{
	// Listing
	public void list() throws Exception
	{
		this.context.put("banners",
			DataAccessDriver.getInstance().newBannerDAO().selectAll());
		this.setTemplateName(TemplateKeys.BANNER_LIST);
	}

	// Insert
	public void insert() throws Exception
	{
		this.context.put("action", "insertSave");
		this.setTemplateName(TemplateKeys.BANNER_INSERT);
	}

	// Saves a new banner
	public void insertSave() throws Exception
	{
		BannerDAO dao = DataAccessDriver.getInstance().newBannerDAO();

		dao.addNew(getBanner());

		this.list();
	}

	// Edit a banner
	public void edit() throws Exception
	{
		int bannerId = this.request.getIntParameter("banner_id");
		BannerDAO dao = DataAccessDriver.getInstance().newBannerDAO();

		this.context.put("banner", dao.selectById(bannerId));
		this.setTemplateName(TemplateKeys.BANNER_EDIT);
		this.context.put("action", "editSave");
	}

	// Save information for an existing banner
	public void editSave() throws Exception
	{
		int bannerId = this.request.getIntParameter("banner_id");

		Banner banner = getBanner();
		banner.setId(bannerId);

		DataAccessDriver.getInstance().newBannerDAO().update(banner);

		this.list();
	}

	// Delete a banner
	public void delete() throws Exception
	{
		String bannerId = this.request.getParameter("banner_id");
		if(bannerId == null)
		{
			this.list();
			return;
		}

		BannerDAO dao = DataAccessDriver.getInstance().newBannerDAO();

		int id = Integer.parseInt(bannerId);
		if(dao.canDelete(id))
		{
			dao.delete(id);
		}
		else
		{
			this.context.put("errorMessage",
				I18n.getMessage(I18n.CANNOT_DELETE_BANNER));
		}

		this.list();
	}

	protected Banner getBanner()
	{
		Banner b = new Banner();
		b.setComment(request.getParameter("comment"));
		b.setActive(request.getIntParameter("active") == 1);
		b.setType(Integer.parseInt(request.getParameter("type")));
		b.setName(request.getParameter("name"));
		b.setDescription(request.getParameter("description"));
		b.setWidth(Integer.parseInt(request.getParameter("width")));
		b.setHeight(Integer.parseInt(request.getParameter("height")));
		b.setUrl(request.getParameter("url"));
		b.setPlacement(Integer.parseInt(request.getParameter(
			"placement")));
		b.setWeight(Integer.parseInt(request.getParameter("weight")));
		b.setViews(Integer.parseInt(request.getParameter("views")));
		b.setClicks(Integer.parseInt(request.getParameter("clicks")));

		return b;
	}
}
