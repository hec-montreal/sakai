<!--
/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/www/website/index.jsp $
 * $Id: index.jsp 55780 2008-12-06 00:00:21Z murthy@etudes.org $ 
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
		<td width="50%" valign="top">
			<font style="font-family: verdana; font-size: 22px;"><b>What is JForum?</b></font><br/>
			<img src="h_bar.gif"><br>
			<p>JForum is a powerful and robust discussion board system implemented in Java<sup>tm</sup>. It provides an attractive interface, an efficient forum engine, an easy to use administrative panel, an advanced permission control system and much more. 
			</p>
      			<p>Built from the ground up around a MVC framework, it can be deployed on any servlet container or Application Server, such as Tomcat,  Resin and JBoss. Its clean design and implementation make JForum easy to customize and extend.
			</p>
			<p>Best of all, JForum is freely available under the BSD Open Source license.</p>	

			<p>
			If you or your company are searching for a serious and robust Forum Software, JForum is the right choice.
			</p>
		</td>
		<td width="50%" align="right">
			<img src="ss_main.jpg"><br/>
			<a href="download.jsp"><img src="download_now.jpg"></a>
		</td>
	</tr>
</table>

<table width="792" align="center" border="0">
	<tr>
		<td colspan="2">
		<font style="font-family: verdana; font-size: 22px;"><b>Key Features</b></font><br/>
		<img src="h_bar.gif" width="792" height="2">
		</td>
	</tr>

	<tr>
		<td width="50%" valign="jpg">
		<p><img src="speed.jpg" align="middle">&nbsp;<a href="features.jsp">Ultra fast</a></p>
		<p><img src="graphics.jpg" align="middle">&nbsp;<a href="features.jsp">Fully customizable user interface</a></p>
		<p><img src="padlock.jpg" align="middle">&nbsp;<a href="features.jsp">Secure and highly customizable permission control</a></p>
		</td>

		<td width="50%" valign="top">
		<p><img src="language.jpg" align="middle">&nbsp;<a href="features.jsp">Multi-language support</a></p>
		<p><img src="db.jpg" align="middle">&nbsp;<a href="features.jsp">Multi-database support</a></p>
		<p><img src="opensource.jpg" align="middle">&nbsp;<a href="features.jsp">Open Source</a></p>
		<td>
	</tr>

	<tr>
		<td colspan="2" align="center" style="font-family: verdana; font-size: 12px;">
		<br/>
		<img src="box_open.jpg" align="middle">&nbsp; <a href="features.jsp">Full feature list</a>
		</td>
	</tr>
</table>

<jsp:include page="bottom.htm"/>

</body>
</html>
