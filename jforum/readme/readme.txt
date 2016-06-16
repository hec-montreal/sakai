Etudes Jforum 2.8.1 RELEASE - Discussion and Private Messages Tool
For Sakai 2.7.1
-----------------------------------------------------
SETUP INSTRUCTIONS

1. Database Information
	Etudes Jforum 2.8.1 works with MySQL 4.1+, MySQL 5.0+ and Oracle databases. 
		
2. Dependency on etudes-util
	Build etudes-util before building Etudes Jforum. 
	
	Get etudes-util from https://source.sakaiproject.org/contrib/etudes/etudes-util/tags/1.0.8
		
	Note:
		a) Add etudes-util version to master/pom.xml to "properties" element as below
			<!--Etudes-->
        	<etudes.util.version>1.0.8</etudes.util.version>
		
		b) Add etudes-util dependencies to "dependencies" element under "dependencyManagement" element
			<!--Etudes-->
			<dependency>
			    <groupId>org.etudes</groupId>
			    <artifactId>etudes-util-api</artifactId>
			    <version>${etudes.util.version}</version>
			    <scope>provided</scope>
			 </dependency>
		
			<dependency>
			    <groupId>org.etudes</groupId>
			    <artifactId>etudes-util-util</artifactId>
			    <version>${etudes.util.version}</version>
			 </dependency>
			 
	Install etudes-util using : mvn clean install
	
3. Add the jforum configurations to sakai.properties and customize
	Refer to readme/jforum_config_properties.txt for the properties
	
4. Build and Deploy Etudes Jforum
	a. Add Etudes Jforum version to master/pom.xml to "properties" element as below
			<!--Etudes-->
        	<etudes.jforum.version>2.8.1</etudes.jforum.version>
		
	   Add etudes-JForum dependency to "dependencies" element under "dependencyManagement" element
			<!--Etudes-->
			<dependency>
			    <groupId>org.etudes</groupId>
			    <artifactId>etudes-jforum-api</artifactId>
			    <version>${etudes.jforum.version}</version>
			    <scope>provided</scope>
			 </dependency>
			 
	b. Install the below artifacts to the local maven repository from sakai_src/etudes-jforum
	
  		mvn install:install-file -DgroupId=jboss -DartifactId=jboss-cache -Dversion=1.3.0.SP2 -Dpackaging=jar -Dfile=lib/jboss-cache-1.3.0.SP2.jar
		mvn install:install-file -DgroupId=htmlparser -DartifactId=htmlparser -Dversion=1.5 -Dpackaging=jar -Dfile=lib/htmlparser-1.5.jar

	c. 	Run Maven Commands
		Note: 
			a) The sample maven goals are 
	    
	    -	mvn clean - remove any prior build

		-	mvn install - compile and package Etudes jforum

		-	mvn sakai:deploy - install the needed files to the local Servlet container
		
		Note : Make sure under tomcat(components, shared/lib, webapps) there are no other versions of jforum files than 2.8.1


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

7. Adding jforum icon to sakai sites' left menu
 
     If you are using sakai's default cascading style sheet create the icons folder under tomcat/webapps/library/skin/default/
     and copy the image jforum-menu.png from jforum-tool/src/webapp/images/sakai-menu to
     tomcat/webapps/library/skin/default/icons. 
 
     Add the below line to tomcat/webapps/library/skin/default/portal.css
        
     .icon-sakai-jforum-tool{
     	background-image: url(icons/jforum-menu.png);
     }