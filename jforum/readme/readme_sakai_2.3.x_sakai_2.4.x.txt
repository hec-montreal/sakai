Etudes Jforum  2.7.1 RELEASE - Discussion and Private Messages Tool
For Sakai 2.3.x or Sakai 2.4.x
-----------------------------------------------------
SETUP INSTRUCTIONS

1. Database Information
	Etudes Jforum 2.7.1 works with Mysql4.1+ and Oracle databases. 
	JForum makes use of Sakai's database connection pool. And Etudes JForum 
	tables are created in the sakai database.
	
2. Dependency on etudes-util
	Get etudes-util from https://source.sakaiproject.org/contrib/etudes/etudes-util/tags/1.0.2
	and build it before building Etudes Jforum.
	
3. Add the jforum configurations to sakai.properties and customize
	Refer to readme/jforum_config_properties.txt for the properties
	
4. Build and Deploy Etudes Jforum
	Run Maven Commands

    Use the sakai maven commands to build and deploy the JForum-Sakai. Refer to 
    sakaisource/reference/docs/architecturesakai_maven.doc
    
    Note: Undeploy any previous versions from the previous version JFORUM source before deploying
    
    The sample maven goals are 
    
    -	sakai:clean - remove any prior build

	-	sakai:build - compile and package Sakai

	-	sakai:deploy - install the needed files to the local Servlet container

	-	sakai:undeploy - remove the installed files from the local Servlet container

	-	sakai:clean_build - clean then build

	-	sakai:clean_build_deploy - clean then build then deploy	 


5. Update Sakai Roles (under realms) to include JForum permissions

	5.1 Check appropriate JForum permissions under the roles in !site.template.course. 
	
		* Check jforum.manage for teacher, instructor, faculty types of roles (maintain).
		* Check jforum.member for student types of custom roles that you have (access).
		
	5.2. If you have project sites and related roles in !site.template.project), appropriate 
		permissions (jforum.manage or jforum.member) need to be checked as defined above.
		

   CAUTION: 
   
		a. IF YOU FAIL TO CHECK THE JFORUM.MEMBER AND JFORUM.MANAGE PERMISSIONS FOR YOUR
			ROLES, JFORUM-SAKAI MAY NOT WORK PROPERLY. 
		b. IF YOU ADD JFORUM-SAKAI TO AN INSTALLATION WITH _EXISTING SITES_, USERS WILL 
			NOT HAVE THE JFORUM PERMISSIONS THAT YOU CHECKED TO EXISTING SITES. YOU WILL 
			NEED TO USE !SITE.HELPER OR OTHER SCRIPT TO PROPAGAGE THE CHANGES. 

6. Mask the image icon in FCK Editor

	This is a necessary step for masking the image icon in the toolbar of the 
    editor (JForum handles embedded images through the 'Attach files' process). 

    If you omit this step, you will get a toolbar error!!!

	Under sakai source/reference/library/src/webapp/editor/FCKeditor/config.js 
	file, include this statement
	FCKConfig.ToolbarSets["JforumDefault"] = [
    ['Source','-','Preview'],
    ['Cut','Copy','Paste','PasteText','PasteWord','-','SpellCheck'],
    ['Undo','Redo','-','Find','Replace','-','SelectAll','RemoveFormat'],
    ['Bold','Italic','Underline','StrikeThrough','-','Subscript','Superscript'],
    ['OrderedList','UnorderedList','-','Outdent','Indent'],
    ['JustifyLeft','JustifyCenter','JustifyRight','JustifyFull'],
    ['Link','Unlink','Anchor'],
	['Smiley'],
    ['SpecialChar'], ['Style'], 
    '/',
    ['FontFormat','FontName','FontSize'],
    ['TextColor','BGColor'],
    ['About']
     ] ;
   	and compile and deploy sakai again. 
	
	NOTE: If you have already deployed sakai and don't want to redeploy then in 
	tomcat/webapps/library/editor/FCKeditor/config.js you can add the above statement.
