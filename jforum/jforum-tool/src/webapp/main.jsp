<%--
/***********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/webapp/main.jsp $
 * $Id: main.jsp 55780 2008-12-06 00:00:21Z murthy@etudes.org $
 ***********************************************************************************
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
 ***********************************************************************************/
 --%>
<%@ page import="org.sakaiproject.tool.jforum.JforumSiteAndUserInfo"%>

<html>
<head>
<title>JForum</title>
<%= request.getAttribute("sakai.html.head.js") %>
<%= request.getAttribute("sakai.html.head.css.skin") %>
</head>
<body onload="<%=request.getAttribute("sakai.html.body.onload")%>">

<%
final javax.faces.context.FacesContext facesContext = javax.faces.context.FacesContext.getCurrentInstance();

final JforumSiteAndUserInfo jforumSiteAndUserInfo = (JforumSiteAndUserInfo)facesContext.getApplication().getVariableResolver().resolveVariable(facesContext, "jforumSiteAndUserInfo");


String usersakaiemail = jforumSiteAndUserInfo.getCurrentUser().getEmail();
String userFirstName = jforumSiteAndUserInfo.getCurrentUser().getFirstName();
String userLastName = jforumSiteAndUserInfo.getCurrentUser().getLastName();
String courseId = jforumSiteAndUserInfo.getCurrentSiteId();

request.getSession().setAttribute("sso.email.attribute", usersakaiemail);
request.getSession().setAttribute("sso.sakaiuser.fname", userFirstName);
request.getSession().setAttribute("sso.sakaiuser.lname", userLastName);
//System.out.println("Course ID is "+courseId);
request.getSession().setAttribute("courseid", courseId);

//response.sendRedirect("index.htm");

%>
<jsp:forward page="index.htm" />
<br>
<body>
</html> 

