<!--
/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/www/website/download.jsp $
 * $Id: download.jsp 55780 2008-12-06 00:00:21Z murthy@etudes.org $ 
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
<title>JForum - Download</title>
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
		<img src="dot.gif"> Download<br/>
		</td>
	</tr>

	<tr>
		<td colspan="2" valign="top">
			<font style="font-family: verdana; font-size: 22px;"><b>Download</b></font><br/>
			<img src="h_bar.gif" width="100%" height="2">
		</td>
	</tr>

	<tr>
		<td valign="top">
			<p>
			The latest stable JForum version available for download is <b>2.1</b>. The files are kindly hosted by <a href="http://sourceforge.net" target="_new">Source Forge</a>. 
			Use the link below to download: 
			<br>
			<br>
			<img src="download.jpg" align="middle"> <a href="http://www.jforum.net/download-latest.htm">Click here to download JForum</a>
			<br>
			<br>
			<font color="#ff0000"><b>Requirements:</b> Java 1.4 or more recent, and any Servlet Container (Tomcat, Jetty, JBoss etc)</font>
			<br><br>
			<font style="font-family: verdana; font-size: 16px;"><b>Developer / CVS Access</b></font><br/>
			If you're a developer or wants to get the latest development version, you can download the source code from CVS. Please see the 
			<a href="development.jsp">development</a> page for more information.
			</p>
		</td>
	</tr>
</table>

<jsp:include page="bottom.htm"/>

</body>
</html>
