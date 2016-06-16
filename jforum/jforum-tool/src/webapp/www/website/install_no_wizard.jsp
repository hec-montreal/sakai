<!--
/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/www/website/install_no_wizard.jsp $
 * $Id: install_no_wizard.jsp 55780 2008-12-06 00:00:21Z murthy@etudes.org $ 
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
			<font style="font-family: verdana; font-size: 22px;"><b>Installation & Configuration - Manual Install</b></font><br/>
			<img src="h_bar.gif" width="100%" height="2">
		</td>
	</tr>

	<tr>
		<td valign="top">
<p>
Here will be showed how to manually configure and install JForum. It is assumed that the you has some 
knowledge on how to install / configure a Java servlet Container ( or already has one up and running ), and the database is properly configured.
</p>

<p>
<br/>
<b>For automated installation, <a href="install.jsp">Click Here</a></b>
<br/></br>
</p>

<p class="note">Note: These instructions are for the installation of JForum, release version 2.1 Some of the steps here may not be valid for 
older versions, which are no longer supported.</p>
</p>

<!-- Options -->
<br><a href="#downloading">Downloading</a>
<br><a href="#installing">Installing</a>
<br><a href="#databaseConfig">Database Configuration</a>
<br><a href="#createTables">Creating the database tables</a>
<br><a href="#populating">Populating the tables</a>
<br><br><a href="#misc">Security Information and Considerations</a>


<!-- Downloadig -->
<p><img src="info.jpg" align="middle" border="0"> <a name="downloading"></a><span class="install_subtitle">Downloading JForum</span>
<p>
To get JForum, go to the <a href="download.htm">download page</a> and get the latest version.
</p>

<!-- Installing -->
<br><img src="info.jpg" align="middle" align="middle"> <a name="installing"></a><span class="install_subtitle">Installing</span>
<p>
After the download, unpack the .ZIP file into your webapp's directory (or anyplace you want to put it). A directory named
<br>
<br><i>JForum-&lt;release&gt;</i>
<br>
<br>will be created, where <i>&lt;release&gt;</i> is the version, which may be "2.0", "2.7.1" etc.. it is just to easily identify the version. You can rename the directory if you want. The next step you should do is register the JForum application within your Servlet Container, like Tomcat. This document will use the context name "jforum", but of course you can give the name you want.
</p>

<!-- Database Configuration -->
<br><a name="databaseConfig"></a><img src="info.jpg" align="middle" border="0"> <span class="install_subtitle">Database Configuration</span>
<p>
First of all, you must have <a href="http://www.mysql.com" target="_new">MySQL</a> or <a href="http://www.postgresql.org" target="_new">PostgreSQL</a>
installed and properly configured. <a href="http://hsqldb.sourceforge.net/">HSQLDB</a> is supported as well, and has built-in support, so you don't
need to download it (eg, it is an embedded database).
<br/><br/>
Open the file <i>WEB-INF/config/SystemGlobals.properties</i>. Now search for a key named <i>database.driver.name</i> and configure it according
to the following table: 

<br/><br/>
<table width="70%" align="center">
	<tr>
		<td>
			<table width="100%" bgcolor="#ff9900" cellspacing="2">
				<tr class="fields">
					<th>Database</th>
					<th>key Value</th>
				</tr>

				<tr class="fields">
					<td><b>MySQ</b>L</td>
					<td><i>mysql</i></td>
				</tr>

				<tr class="fields">
					<td><b>PostgeSQL</b></td>
					<td><i>postgresql</i></td>
				</tr>

				<tr class="fields">
					<td><b>HSQLDB</b></td>
					<td><i>hsqldb</i></td>
				</tr>
			</table>
		</td>
	</tr>

	<tr>
		<td align="center">Key: <i>database.driver.name</i></td>
	</tr>
</table>
<br/>

The default value is <i><b>mysql</b></i>, which means JForum will try to use MySQL. Note that the value should be in lowercase.

<br>
<br>Next, you can tell JForum whether to use a Connection Pool or not. A connection pool will increase the performance of your application, 
but there are some situations where the use of a connection pool is not recommended or even possible, so you can change it according to your
needs. 
</p>

<p>
<br>By default JForum <b>uses</b> a connection pool, option which is specified by the key <i>database.connection.implementation</i>.
The following table shows the possible values for this key:

<br/><br/>
<table width="70%" align="center">
	<tr>
		<td>
			<table width="100%" bgcolor="#ff9900" cellspacing="2">
				<tr class="fields">
					<th>Connection Storage Type</th>
					<th>key Value</th>
				</tr>

				<tr class="fields">
					<td><b>Pooled Connections</b></td>
					<td><i>net.jforum.PooledConnection</i></td>
				</tr>

				<tr class="fields">
					<td><b>Simple Connections</b></td>
					<td><i>net.jforum.SimpleConnection</i></td>
				</tr>

				<tr class="fields">
					<td><b>DataSource Connections</b></td>
					<td><i>net.jforum.DataSourceConnection</i></td>
				</tr>
			</table>
		</td>
	</tr>

	<tr>
		<td align="center">Key: <i>database.connection.implementation</i></td>
	</tr>
</table>
<br/>

<br> <img src="info.jpg" align="middle" border="0"> If you have chosen <i><b>net.jforum.DataSourceConnection</b></i>, then set the name of the datasource in key <i>database.datasource.name</i>, and ignore the table below. Otherwise, do the following steps:<br><br>
Edit the file <i>WEB-INF/config/database/&lt;<b>DBNAME</b>&gt;/&lt;<b>DBNAME</b>&gt;.properties</i>, 
where &lt;<b>DBNAME</b>&gt; is the database name you are using - for instance, <i>mysql</i>, <i>postgresql</i> or <i>hsqldb</i>. 
In this file there are some options you should change, according to the table below:

<br/><br/>
<table width="70%" align="center">
	<tr>
		<td>
			<table width="100%" bgcolor="#ff9900" cellspacing="2">
				<tr class="fields">
					<th>Key Name</th>
					<th>key Value description</th>
				</tr>

				<tr class="fields">
					<td><b>database.connection.username</b></td>
					<td>Database username</td>
				</tr>

				<tr class="fields">
					<td><b>database.connection.password</b></td>
					<td>Database password</td>
				</tr>

				<tr class="fields">
					<td><b>database.connection.host</b></td>
					<td>The host where the database is located</td>
				</tr>

				<tr class="fields">
					<td><b>dbname</b></td>
					<td>The database name. The default value is jforum. All JForum tables are preceded by "jforum_", 
					so you don't need to worry about conflicting table names.</td>
				</tr>
			</table>
		</td>
	</tr>

	<tr>
		<td align="center">File: <i>WEB-INF/config/database/&lt;DBNAME&gt;/&lt;DBNAME&gt;.properties</i></td>
	</tr>
</table>
<br/>

<br>The other properties you may leave with the default values if you don't know what to put. 

<!-- Note for mysql -->
<br><br></a><img src="info.jpg" align="middle" align="middle"> <font color="#ff0000"><b>Note for MySQL users</b></font><br>
If you're going to use MySQL 4.1 or newer, please pay attention to the fact that starting from this version (the mysql version, not JForum) many architectural changes were made. By default, now the system runs using the UTF-8 character set (previous versions lack support for it), and it may result in some problems depending the way you configured JForum to connect to MySQL. Regular installations will opt to use unicode and UTF-8 as character encoding, but, when using MySQL 4.1+, you <b>shoudl avoid id</b>. <br>
To do that, open the file <i>mysql.properties</i> and change the value of the keys <i>"mysql.encoding"</i> and <i>"mysql.unicode"</i> to empty (eg, <i>mysql.unicode=</i> ).
</p>

<!-- Creating Database Tables -->
<br><a name="createTables"></a><img src="info.jpg" align="middle" align="middle"> <span class="install_subtitle">Creating the database tables</span>
<p>The next step is to create the tables. To do that, use the import script named "&lt;<b>DBNAME</b>&gt;_db_struct.sql", placed at <i>WEB-INF/config/database/&lt;<b>DBNAME</b>&gt;</i>. This script will create all necessary tables to run JForum. The script were tested and should work with no problem at all. 
<br>Also, please keep in mind that if you are upgrading JForum you need to take a look to see if a migration script exists. Look in the file named "Readme.txt" in the root directory to see.
</p>

<p>
<a name="populating"></a><img src="info.jpg" align="middle" border="0"> <span class="install_subtitle"><b>Populating the tables</b></span><br>
 Now it is time to run the script to populate the database tables. To do that, use the script named "&lt;<b>DBNAME</b>&gt;_data_dump.sql", also located at <i>WEB-INF/config/database/&lt;<b>DBNAME</b>&gt;</i>. One more time, you should have no problems with this step. If you do, please remember to inform the error message, as well the database name and version you're using. 
</p>

<p>
<a name="populating"></a><img src="info.jpg" align="middle" border="0"> <span class="install_subtitle"><b>Renaming files</b></span><br>
Now, look for a file named <i>new_rename.htm</i> in the root directoy. If it exists, rename it to <i>index.htm</i> (delete the existing index.htm first). This will make browsers to be redirected to the forum when acessing the context path's root. 
</p>

<br>

<!-- Misc -->
<a name="misc"></a>
<img src="info.jpg" align="middle" border="0"> 
<span style="font-size: 16px; line-height: normal"><b><u>Security Information and Considerations</u></b></span>
<br><li><b><font color="#ff0000">Remove the line <font color="#006699"><i>"install = net.jforum.view.install.InstallAction"</i></font> from the file
<i>WEB-INF/config/modulesMapping.properties</i></font></b><br>

<br><li> JForum uses a servlet mapping to invoke the pages. This mapping is <b>*.page</b>, and is already properly configured at WEB-INF/web.xml. If you are running JForum on a ISPs which have Apache HTTPD in front of Tomcat, you may need to contact their Technical Support and ask them to explicity enable the mapping for you.

<br>
<br><li> The directory "images", "tmp" and "WEB-INF" ( e its sub-directories ) should have write permission 
to the user who runs the web server. You'll get nasty exceptions if there is no write permission. In the same way, if you're going to use the file attachments support, the directoy you'd chosen to store the files ("uploads" by default) should also be writable. 

<br>
<br><li> The administration interface is accessible via the link <i>Admin Control Panel</i>, located in the bottom of the main page. You will only see this link if you are logged as Administrator. See above the default password for the admin user: 
<br>
<br>The username is <i>Admin</i> and the password is <i>admin</i>
<br>
<br><li> This step is <b>HIGHLY</b> recommended: Open the file <i>WEB-INF/config/SystemGlobals.properties</i> and search for a key named <i>user.hash.sequence</i>. There is already a default value to the key, but is <b>VERY RECOMMENDED</b> that you change the value. It may be anything, and you won't need to remember the value. You can just change one or other char, insert some more... just type there some numbers and random characters, and then save the file. This value will be used to enhance the security of your JForum installation, and you will just need to do this step once.

		</td>
	</tr>
</table>

<jsp:include page="bottom.htm"/>

</body>
</html>
