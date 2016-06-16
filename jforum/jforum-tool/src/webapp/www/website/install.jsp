<!--
/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/www/website/install.jsp $
 * $Id: install.jsp 55780 2008-12-06 00:00:21Z murthy@etudes.org $ 
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
<title>JForum - Installation & Configuration</title>
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
		<img src="dot.gif"> <a href="index.jsp">Main page</a><br/>
		<img src="dot.gif"> <a href="features.jsp">Features</a><br/>
		<img src="dot.gif"> <a href="download.jsp">Download</a>
		</td>
	</tr>

	<tr>
		<td colspan="2" valign="top">
			<font style="font-family: verdana; font-size: 22px;"><b>Installation & Configuration - Wizard</b></font><br/>
			<img src="h_bar.gif" width="100%" height="2">
		</td>
	</tr>

	<tr>
		<td valign="top">
<p>
Here we will show how to configure and install JForum using the graphical 'wizard'. It is assumed that you have some 
knowledge on how to install/configure a Java servlet container (or already have one up and running), and the database is properly configured.
</p>

<p>
<br/>
<b>For manual installation instructions, <a href="install_no_wizard.jsp">Click Here</a></b>
<br/></br>
</p>

<p class="note">Note: These instructions are for the installation of JForum, release version 2.1. Some of the steps here described may not be valid for 
older versions, which are no longer supported.</p>
</p>

<!-- Options -->
<br><a href="#downloading">Downloading</a>
<br><a href="#installing">Unpacking</a>
<br><a href="#configuring">Configuring</a>
<br><a href="#administering">Administering the Forum</a>

<p>&nbsp;</p>

<!-- Downloadig -->
<p><img src="info.jpg" align="middle" border="0"> <a name="downloading"></a><span class="install_subtitle">Downloading JForum</span>
<p>
To get JForum, go to the <a href="download.htm">download page</a> and get the latest version.
</p>

<!-- Installing -->
<br><img src="info.jpg" align="middle" align="middle"> <a name="installing"></a><span class="install_subtitle">Unpacking</span>
<p>
After the download, unpack the .ZIP file into your webapp's directory (or anyplace you want to put it). A directory named
<br>
<br><i>JForum-&lt;release&gt;</i>
<br>
<br>will be created, where <i>&lt;release&gt;</i> is the version, which may be "2.0", "2.7.1" etc... this it just for easy version identification. 
You can rename the directory if you want. The next step you should do is register the JForum application within your Servlet Container, 
like Tomcat. This document will use the context name "jforum", but of course you can use any name you want.
</p>

<!-- Configuring -->
<br><a name="configuring"></a><img src="info.jpg" align="middle" border="0"> <span class="install_subtitle">Configuring</span>
<p>
Next, point your browser to the following address: <br/><br/>
<a href="http://localhost:8080/jforum/install/install.page?module=install&action=welcome">http://localhost:8080/<b>jforum</b>/install/install.page?module=install&action=welcome</a>
<br/><br/>
The bold text, "jforum", is the context's name. If you changed the context name you will need to change it here too. After the page loads, you should see the screen
shown by <i>Image 1</i>:<br/><br/>

<div align="center">
	<img src="screenshots/install_step_1.jpg"><br/>
	<i>Image 1 - Welcome Page</i>
</div>

</p>

<p>
Please read carefully the fields' tips, since they contain valuable information. Below is a little explanation of each field:

<table width="90%" align="center">
	<tr>
		<td>
			<table width="100%" bgcolor="#ff9900" cellspacing="2">
				<tr>
					<th width="30%">Field Name</th>
					<th>Required</th>
					<th>Description</th>
				</tr>

				<tr class="fields">
					<td>Default Board Language</td>
					<td align="center">Yes</td>
					<td>The language to display the messages in the forum. Note that, if the translation for some text is not available,
						an English text will be used</td>
				</tr>

				<tr class="fields">
					<td>Database Type</td>
					<td align="center">Yes</td>
					<td>The database server to use. If you don't know which one to choose, select "HSQLDB" from the list</td>
				</tr>

				<tr class="fields">
					<td>Installation Type</td>
					<td align="center">Yes</td>
					<td>Installation mode. Currently only "New Installation" is supported</td>
				</tr>

				<tr class="fields">
					<td>Connection type</td>
					<td align="center">Yes</td>
					<td>Which method to use to connect to database. <i>Native</i> will use regular connections, while <i>DataSource</i> will try
					to connect using the specified datasource name</td>
				</tr>

				<tr class="fields">
					<td>DataSource name</td>
					<td align="center">no</td>
					<td>If you chosen <i>DataSource</i> as Connection Type, then inform the name of the datasource</td>
				</tr>

				<tr class="fields">
					<td>Database Server Hostname</td>
					<td align="center">Yes</td>
					<td>The host where the database is located</td>
				</td>

				<tr class="fields">
					<td>Database Name</td>
					<td align="center">Yes</td>
					<td>Where the tables will be created. <b>Note that the database should already exist</b>. If you're going
					go use HSQLDB, no extra configuration is needed, since it is created on the fly</td>
				</tr>

				<tr class="fields">
					<td>Database username</td>
					<td align="center">No*</td>
					<td>The user of your database instance. *This field is required for databases other than HSQLDB</td>
				</tr>

				<tr class="fields">
					<td>Database Password</td>
					<td align="center">No</td>
					<td>The database password, if any. Please note that errors may occur when not using any password</td>
				</tr>

				<tr class="fields">
					<td>Database Encoding</td>
					<td align="center">No</td>
					<td>The text encoding for the database. You can specify it by hand in the "Other" field</td>
				</tr>

				<tr class="fields">
					<td>Use Connection Pool</td>
					<td align="center">Yes</td>
					<td>In case of doubt, choose <b>No</b></td>
				</tr>

				<tr class="fields">
					<td>Forum Link</td>
					<td align="center">Yes</td>
					<td>The link to the forum context. If you are hosting it under some "real" domain and the context path is "/", 
						the "Forum Link" will be the address of your site, in most cases. 
					</td>
				</tr>

				<tr class="fields">
					<td>Website Link</td>
					<td align="center">Yes</td>
					<td>The link to your website.</td>
				</tr>

				<tr class="fields">
					<td>Administrator Password</td>
					<td align="center">Yes</td>
					<td>Type the password of the administrator (<i>Admin</i> user). 
				</tr>
			</table>
		</td>
	</tr>
</table>
<br/>
Now you can click the "Next Step "button. You will see the page shown in <i>Image 2</i>, which that contains a summary of your choices.
</p>

<p align="center">
<img src="screenshots/install_step_2.jpg"><br/>
<i>Image 2 - Checking the configuration before installation begins</i>
</p>

<p>
If is everthing looks good, click on the "Begin Install" button. Note that if some warning message is shown, then there are some required steps
that should be made before proceding with the installation. 
</p>

<p>
Wait until the installation ends. If no errors occur, then you will see a page like the one shown in <i>Image 3</i>
</p>

<p align="center">
<img src="screenshots/install_step_3.jpg"><br/>
<i>Image 3 - Installation Finished</i>
</p>

<p>
<b>Congratulations!</b> You have finished your JForum Installation. Before accessing the forum, do the actions pointed by the arrows, click
on the check button and click on the button "Click Here to Access the Forum". 
</p>


<!-- Administration -->
<br><a name="administering"></a><img src="info.jpg" align="middle" border="0"> <span class="install_subtitle">Administering the Forum</span>
<p>
Now you can login as <b>Admin</b> / <i>&lt;the_password_you_set&gt;</i> and click in the link "Admin Control Panel", at the end of the page. 
There you will be able to create Categories, Forums, Groups, and Users. <br/>
Don't forget to give write access to the webserver's user to the directories "images" and "tmp" ( as well from its subdiretories, if any ).
</p>

		</td>
	</tr>
</table>

<jsp:include page="bottom.htm"/>

</body>
</html>
