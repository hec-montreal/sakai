<!--
/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/www/website/help.jsp $
 * $Id: help.jsp 55780 2008-12-06 00:00:21Z murthy@etudes.org $ 
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
<title>JForum - Help</title>
<link href="jforum.css" rel="stylesheet" type="text/css" />
</head>
<body>

<jsp:include page="header.htm"/>

<table width="792" align="center" border="0">
	<tr height="10">
		<td colspan="2">&nbsp;</td>
	</tr>

	<tr>
		<td valign="top" rowspan="3" width="12%">
		<img src="dot.gif"> <a href="index.htm">Main page</a><br/>
		<img src="dot.gif"> <a href="features.htm">Features</a><br/>
		<img src="dot.gif"> <a href="download.htm">Download</a>
		</td>
	</tr>

	<tr>
		<td colspan="2" valign="top">
			<font style="font-family: verdana; font-size: 22px;"><b>Help / FAQ</b></font><br/>
			<img src="h_bar.gif" width="100%" height="2">
		</td>
	</tr>

	<tr>
		<td valign="top">
			<p>
			<br/>
			<b>For more help and support, please access the <i>Community</i> forum. <a href="community.htm">Click here</a></b>
			</p>

			<a name="top"></a>

			<!-- Installation -->
			<img src="dot.gif"> <font style="font-family: verdana; font-size: 18px;"><b>Installation</b></font><br/>
			<li><a href="#install_store_files">Where do JForum store my personall settings?</a><br/>
			<li><a href="#install_write_error">I'm getting an error that says that I need write access to some directories</a><br/>

			<br/>
			<!-- Board Settings -->
			<img src="dot.gif"> <font style="font-family: verdana; font-size: 18px;"><b>Board / Forum Settings</b></font><br/>
			<li><a href="#admin_config">How do I configure the Forum settings?</a><br/>
			<li><a href="#email_notification">How do I configure email notifications?</a><br/>
			<li><a href="#encoding">How to I set a different Character Encoding?</a><br/>
			<li><a href="#restricted_forums">Can I restict the access to some forums or categories?</a><br/>

			<br/>
			<!-- Funcionalities -->
			<img src="dot.gif"> <font style="font-family: verdana; font-size: 18px;"><b>JForum Funcionalities</b></font><br/>
			<li><a href="#databases">What database does JForum support? <br/></a>
			<li><a href="#languages">Which are the languages currently supported in JForum<br/>
			<li><a href="#sticky">What does "Announce" and "Sticky" mean, other than a special icon? </a><br/>
			<li><a href="#license">Which open source license is JForum under?</a><br/>

			<br/>
			<!-- Database -->
			<img src="dot.gif"> <font style="font-family: verdana; font-size: 18px;"><b>Database Issues</b></font><br/>
			<li><a href="#mysql_4_1">When using MySQL 4.1, I get an error like "<i>Illegal mix of collations</i>". What is that and how to solve? <br/></a>
			

			<br/>
			<!-- Users -->
			<img src="dot.gif"> <font style="font-family: verdana; font-size: 18px;"><b>Users</b></font><br/>
			<li><a href="#linux_no_x">Avatar upload is not working. What's wrong?</a><br/>

			<p>&nbsp</p>

			<!-- Installation Anwsers -->
			<img src="dot.gif"> <font style="font-family: verdana; font-size: 18px;"><b>Installation Answers</b></font><br/>
			<p>
			<a name="install_store_files"></a><b>How do I configure the Forum settings?</b><br/>
			All dinamically manipulled settings are stored in a special configuration file in the <i>WEB-INF/config</i> directory.
			The filename is system dependant, according to the user which is running the webserver, in the form 
			<b>&lt;user.name&gt;.conf</b>. So, if the system' user is "Webserver", all custom settings will be written to the file
			<i>WEB-INF/config/Webserver.conf</i>.<br/> 
			Obviously, it should have write access to this directory. <a href="#top">Top</a>
			</p>

			<p>
			<a name="install_write_error"></a><b>I'm getting an error that says that I need write access to some directories</b><br/>
			The error "<i>Please give write access for the user who is running the webserver to the file 'index.htm' 
			and for the directory 'WEB-INF/config' and its subdirectories before continuing</i>" means that, in order to JForum be able
			to store its configurations, it needs write access to these directories / files. <br/>
			It occurs generally in shared environments, like ISPs for example, where the user who owns the webserver process is different
			from your ftp user. To solve this issue, simply give write access to the respective user. For more information, please contact
			your System Administrator / Support. <a href="#top">Top</a>
			</p>

			<!-- Board Settings Anwsers -->
			<img src="dot.gif"> <font style="font-family: verdana; font-size: 18px;"><b>Board / Forum Settings Answers</b></font><br/>
			<p>
			<a name="admin_config"></a><b>How do I configure the Forum settings?</b><br/>
			All administrative settings are done in the "Admin Panel", that can be accessed through the link "Admin Panel" available
			in the end of the forum page. You must be logged as administrator, either by using the <i>Admin</i> account or any other 
			user who has administrative privileges. <a href="#top">Top</a>
			</p>

			<p>
			<a name="email_notification"></a> <b>How do I configure email notifications?</b><br/>
			You can change this option in the admin panel, Configurations section. <a href="#top">Top</a>
			</p>

			<p>
			<a name="encoding"></a><b>How to I set a different Character Encoding?</b><br/>
			Go to the Admin Panel, section "Configurations". Set the character encoding you want in the "Character Encoding" field. <a href="#top">Top</a>
			</p>

			<p>
			<a name="restricted_forums"></a><b>Can I restict the access to some forums or categories?</b><br/>
			Yes. Restricting access to forums or categories will make them do not be shown to users who doesn't have access
			rights. To acquire that, go to the <a href="#admin_config">Administration Panel</a> -&gt; Groups -&gt; Permissions
			and then choose the forums and / or categories you want to restrict in the section "Restricted Forums" and
			"Restricted Categories".  <a href="#top">Top</a>
			</p>

			<!-- JForum Functionalities Answers -->
			<img src="dot.gif"> <font style="font-family: verdana; font-size: 18px;"><b>JForum Funcionalities Answers</b></font><br/>
			<p>
			<a name="databases"></a><b>What database does JForum support?</b><br/>
			As of version 2.0, JForum is bundled with support to MySQL, PostgreSQL and HSQLDB. Other databases are known to work, like
			Oracle, SQLServer and Firebird. <a href="#top">Top</a>
			</p>

			<p>
			<a name="#sticky"></a><b>What does "Announce" and "Sticky" mean, other than a special icon? </b><br/>
			Announces are always shown first. Sticky are shown first of other topics as well, but after Announces. <a href="#top">Top</a>
			</p>

			<p>
			<a name="languages"></a><b>Which are the languages currently supported in JForum?</b><br/>
			As of version 2.0, the languages supported are English, Chinese Simplified, French, German, Dutch, Portugu&ecirc;s do Brasil, and Russian. 
			<a href="#top">Top</a>
			</p> 

			<p>
			<a name="#license"></a> <b>Which open source license is JForum under?</b><br/>
			JForum is licensed under the BSD License.  <a href="#top">Top</a>
			</p>

			<!-- Database Answers -->
			<img src="dot.gif"> <font style="font-family: verdana; font-size: 18px; "><b>Database Issues Answers</b></font><br/>
			<p>
			<a name="mysql_4_1"></a><b>When using MySQL 4.1, I get an error like "<i>Illegal mix of collations</i>". What is that and how to solve?</b> <br/>
			When using MySQL 4.1 or newer, JForum will raise an error when trying to do some options. The error raised generally is like "<i>Illegal mix of collations" 
			(utf8_general_ci,IMPLICIT) and (latin1_swedish_ci,COERCIBLE)</i>". <br/>
			Until we find a real solution, a workaround is described at <a href="http://www.jforum.net/posts/list/455.page">http://www.jforum.net/posts/list/455.page</a>.
			 <a href="#top">Top</a>
			</p>

			<!-- User Anwsers -->
			<img src="dot.gif"> <font style="font-family: verdana; font-size: 18px;"><b>User Answers</b></font><br/>
			<p>
			<a name="linux_no_x"></a><b>Avatar upload is not working. What's wrong?</b><br/>
			If you are using Linux, or other Unix like machine, it is necessary that you have installed the X server libraries. 
			Another solution is to run the Servlet Container setting up JAVA_OPTS's parameter <i>java.awt.headless</i> to "true". 
			For more information, see <a href="http://www.jforum.net/posts/list/219.page">http://www.jforum.net/posts/list/219.page</a>
			 <a href="#top">Top</a>
			</p>
		</td>
	</tr>
</table>

<jsp:include page="bottom.htm"/>

</body>
</html>
