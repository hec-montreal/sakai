<!--
/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/www/website/development.jsp $
 * $Id: development.jsp 55780 2008-12-06 00:00:21Z murthy@etudes.org $ 
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
</head>
<body>

<jsp:include page="header.htm"/>

<table width="792" align="center" border="0">
	<tr height="10">
		<td colspan="2">&nbsp;</td>
	</tr>

	<tr>
		<td valign="top" rowspan="2" width="12%">
		<img src="dot.gif"> <a href="index.jsp">Main page</a><br/>
		<img src="dot.gif"> <a href="features.jsp">Features</a><br/>
		<img src="dot.gif"> <a href="download.jsp">Download</a>
		</td>
	</tr>

	<tr>
		<td valign="top" rowspan="3">
			<a href="http://www.atlassian.com/c/NPOS/10160"><img src="jira_tile_150wx300h.gif" align="right"></a><br>

			<font style="font-family: verdana; font-size: 22px;"><b>Development</b></font><br/>
			<img src="h_bar.gif"><br>
			<p>
			As an Open-Source software, you are invited to contribute to JForum. There are many areas where you can help, like
			making documentation, new styles, buttons, mods and even helping to develop the core source-code. <br/>We'll have more
			information in this page soon, but a good start point is the forums. You can also send an email to JForum's project
			leader, Rafael Steil, at <b>rafael _[at]_ insanecorp.com</b>.
			</p>

			<p>
			JForum does not uses JSP for content display, but a template engine, which offer much more ease of development and
			maintaince. The template engine used is <a href="http://freemarker.sourceforge.net/">FreeMarker</a>.
			</p>

			<br/>

			<p>
			<img src="info.jpg" align="middle"> <b>Contributors</b><br/>
			A project only evolves with feedback of users and contribution - be it programming, documenting, making graphics and
			so other many areas - of so many valuable people. Here are some of who have helped to improve JForum:

			<p>&nbsp</p>

			<table cellspacing="0" width="50%" align="center" bgcolor="#ff9900">
				<tr>
					<td>
						<table width="100%" bgcolor="#ffffff">
							<tr>
								<td><b>James Yong</b></td>
								<td><b>Marc Wick<b></td>
							</tr>

							<tr>
								<td><b>Sean Mitchell</b></td>
								<td><b>Pieter Olivier</b></td>
							</tr>

							<tr>
								<td><b>Sérgio Umlauf</b></td>
								<td><b>Pablo Marutto</b></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			</p>

			<p>
			People come and go all the time. Many times we receive help because an user needs some feature more quickly, or then
			he / she got some free time to contribute for a while, and is that kind of contribution which makes the project grow up.
			<b>You are welcome to join us.</b> 
			</p>

			<br/>

			<p>
			<img src="info.jpg" align="middle"> <b>Bug and Issue Tracker</b><br/>
			JForum issues are tracked by <a href="http://www.atlassian.com/c/NPOS/10160" target="_new">JIRA</a>, by Atlassian. You can see
			the current issues, ask for new features and submit bug reports at <a href="http://www.jforum.net/jira">http://www.jforum.net/jira</a>
			</p>

			<br/>

			<p>
				<img src="info.jpg" align="middle"> <b>Getting the source code from CVS</b> <br/>
				The CVS repository is hosted by <a href="http://java.net" target="_new">Java.net</a>. Below is listed the host access options:
			</p>

			<table cellspacing="0" width="50%" align="center" bgcolor="#ff9900">
				<tr>
					<td>
						<table width="100%" bgcolor="#ffffff">
							<tr>
								<td><b>Host</b></td>
								<td><i>cvs.dev.java.net</i></td>
							</tr>

							<tr>
								<td><b>Path</b></td>
								<td><i>/cvs</i></td>
							</tr>

							<tr>
								<td><b>Server Type</b></td>
								<td><i>pserver</i></td>
							</tr>

							<tr>
								<td><b>Username</b></td>
								<td><i>guest</i></td>
							</tr>

							<tr>
								<td><b>Module</b></td>
								<td><i>jforum</i></td>
							</tr>
						</table>
					</td>
				</tr>
			</table>

			<br/>

			<p>
			<img src="info.jpg" align="middle"> <b>Setting up your development environment</b><br>
			First of all, create an empty file named <i>jforum-custon.conf</i> in the directory <i>WEB-INF/config</i> .This file is necessary when working on
			teams, since you should not change <i>SystemGlobals.properties</i> to put your own configuration data, like passwords and 
			forum address. After creating the file, just add it to <i>.cvsignore</i>. 
			</p>

			<p>
			You can follow the install instructions <a href="install.jsp">clicking here</a>
			</p>

			<p>
			The best start point is <i>src/net/jforum/JForum.java</i>, which is the Front Controller implementation. 
			<i>src/net/jforum/entities</i> contains all entities used in the system, <i>src/net/jforum/model</i> have the
			interfaces used to persist and retrieve data and <i>src/net/jforum/view</i> contains all view related actions. 
			</p>

			<br/>

			<p>
			<img src="info.jpg" align="middle"> <b>Getting help</b><br/>
			The best place to ask for help is in the forums / Community section. <a href="community.htm">Click here</a> to go there. 
			</p>
		</td>
	</tr>

</table>

<jsp:include page="bottom.htm"/>

</body>
</html>
