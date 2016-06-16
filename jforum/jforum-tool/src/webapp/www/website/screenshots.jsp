<!--
/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/www/website/screenshots.jsp $
 * $Id: screenshots.jsp 55780 2008-12-06 00:00:21Z murthy@etudes.org $ 
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
 --%>
<html>
<head>
<title>JForum - Powering Communities</title>
<link href="jforum.css" rel="stylesheet" type="text/css" />

<script language="javascript">
function show_big(image)
{
	window.open('screenshots/' + image, 'screenshot');
}
</script>
</head>
<body>

<jsp:include page="header.htm"/>

<table width="792" align="center" border="0">
	<tr height="10">
		<td colspan="2">&nbsp;</td>
	</tr>

	<tr>
		<td valign="top" rowspan="4" width="12%">
		<img src="dot.gif"> <a href="index.jsp">Main page</a><br/>
		<img src="dot.gif"> <a href="features.jsp">Features</a><br/>
		<img src="dot.gif"> <a href="download.jsp">Download</a><br/>
		</td>
	</tr>

	<tr>
		<td width="50%" valign="top" colspan="3">
			<font style="font-family: verdana; font-size: 22px;"><b>Screenshots</b></font><br/>
			<img src="h_bar.gif"><br>
			<p>
			Here are some screenhots of JForum. Click on the images to expand.
			</p>
		</td>
	</tr>

	<tr><td>&nbsp;</td></tr>

	<tr>
		<td valign="top" colspan="3">
			<table width="100%" cellspacing="10">
				<tr>
					<td width="33%" valign="top">
						<a href="#" onClick="show_big('forum_listing_b.jpg');"><img src="screenshots/forum_listing_s.jpg"></a><br/>
						Main forum page, listing all categories and forums available to the user, as well information about the last message of each forum
					</td>
					<td width="33%" valign="top">
						<a href="#" onClick="show_big('topic_listing_b.jpg');"><img src="screenshots/topic_listing_s.jpg"></a><br/>
						Topic listing page, showing the first page of topics of some forum
					</td>
					<td width="33%" valign="top">
						<a href="#" onClick="show_big('posting_messages_b.jpg');"><img src="screenshots/posting_messages_s.jpg"></a><br/>
						Posting a new message. Note the text formating options and the "Attach Files" button
					</td>
				</tr>

				<tr><td colspan="3">&nbsp;</td></tr>

				<tr>
					<td valign="top">
						<a href="#" onClick="show_big('reading_messages_b.jpg');"><img src="screenshots/reading_messages_s.jpg"></a><br/>
						Reading a topic
					</td>
					<td valign="top">
						<a href="#" onClick="show_big('config_form_b.jpg');"><img src="screenshots/config_form_s.jpg"></a><br/>
						Board configuration options in the Administration Panel
					</td>
					<td valign="top">
						<a href="#" onClick="show_big('online_users_b.jpg');"><img src="screenshots/online_users_s.jpg"></a><br/>
						Information about current online users
					</td>
				</tr>

				<tr><td colspan="3">&nbsp;</td></tr>

				<tr>
					<td valign="top">
						<a href="#" onClick="show_big('attaching_files_b.jpg');"><img src="screenshots/attaching_files_s.jpg"></a><br/>
						Attaching files to the message
					</td>
					<td valign="top">
						<a href="#" onClick="show_big('post_attach_b.jpg');"><img src="screenshots/post_attach_s.jpg"></a><br/>
						Message displaying the File Download box of an attached file
					</td>
					<td valign="top">
						<a href="#" onClick="show_big('member_listing_b.jpg');"><img src="screenshots/member_listing_s.jpg"></a><br/>
						Listing of registered members
					</td>
				</tr>
			</table>
		</td>
	</tr>

</table>

<jsp:include page="bottom.htm"/>

</body>
</html>