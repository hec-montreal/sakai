SAKAI-JFORUM 2.2 RELEASE - Discussion and Private Messages Tool
For Sakai 2.2
------------------------------------------------------------------
UPGRADE INSTRUCTIONS from SAKAI-JForum 2.1.1 to SAKAI-JForum 2.2. 
These instructions are for the existing Sakai-JForum users.
-----------------------------------------------------------------

1. Database conversion

	Sakai-JForum 2.2 works with Mysql4.1+, Oracle, HSQLDB and PostgreSQL 
	databases. Oracle support is the latest addition to the supported databases. 
	JForum now makes use of Sakai's database connection pool.
	
	To upgrade from previous JForum mysql database, take the data dump of the 
	existing JForum mysql database and run this against the sakai database that
	creates the tables and dumps the data from the existing JForum database. 
	---------
	For mysql
	---------
	Example: mysql -hlocalhost -usakaiuser -p sakai_db < jforum_2_1_0.dmp
	where sakai_db is sakai database
		  jforum_2_1_0.dmp is the existing JForum database dump

	Run the below statements to update the jforum tables
	----------
	For mysql
	----------
	Note: If you are already running JForum 2.2 for Sakai 2.1.2, you need NOT run
	the first two statements below (you've done it already). Just run the third one.
	
	a) ALTER TABLE jforum_forums ADD start_date datetime;
	b) ALTER TABLE jforum_forums ADD end_date datetime;
	c) ALTER TABLE jforum_users ADD sakai_user_id varchar(99) NOT NULL;
	
	--assuming sakai conversion script is run
	UPDATE jforum_users 
		SET sakai_user_id = username
	WHERE 
		sakai_user_id IS NULL OR RTRIM(LTRIM(sakai_user_id)) = ''
	AND
		username in 
		(SELECT EID FROM SAKAI_USER_ID_MAP WHERE EID = USER_ID);
	----------
	For Oracle
	----------
	Note: If you are already running JForum 2.2 for Sakai 2.1.2, you need NOT run
	the first two statements below (you've done it already). Just run the third one.
	
	a) ALTER TABLE jforum_forums ADD start_date TIMESTAMP NULL;
	b) ALTER TABLE jforum_forums ADD end_date TIMESTAMP NULL;
	c) ALTER TABLE jforum_users ADD sakai_user_id VARCHAR2(99) NOT NULL;
	
	--assuming sakai conversion script is run
	UPDATE jforum_users 
		SET sakai_user_id = username
	WHERE 
		sakai_user_id IS NULL OR RTRIM(LTRIM(sakai_user_id)) = ''
	AND
		username in 
		(SELECT EID FROM SAKAI_USER_ID_MAP WHERE EID = USER_ID);
	
	
2. Cache set up

 	The WEB-INF/config/jforum-cache-cluster.xml file must be modified to specify
	the IP address of the cluster node.  You may therefore wish to configure the cache
	engine in a file outside the webapp itself.  This is often preferable to modifying
	the a file inside a war, since the customizations will be lost on each redeploy.
	To use an external file to override the jforum-cache-cluster.xml settings,
	specify a 'local.config.dir' property in SystemGlobals.properties and place
	your custom jforum-cache-cluster.xml file there on each server in the cluster.
	
3. Build and Deploy JForum-Sakai

   As few dependencies are not in maven repository, follow the steps below to include them.

	a.	Copy htmlparser-1.5.jar to your local maven repository from WEB-INF/lib as 	
		.maven/repository/htmlparser/jars/htmlparser-1.5.jar 
	
	b.	Copy ojdbc14_g.jar to your local maven repository from WEB-INF/lib as 		
		.maven/repository/oracle/jars/ojdbc14_g.jar 
	
	c.	Copy commons-fileupload-1.1-dev.jar to your local maven repository from 
		WEB-INF/lib under	.maven/repository/commons-fileupload/jars 

	d.	Copy commons-io-1.1-dev.jar to your local maven repository from WEB-INF/lib 
		under .maven/repository/commons-io/jars
		
	e.  Copy freemarker-2.3-rc3.jar to your local maven repository from WEB-INF/lib 
		under .maven/repository/freemarker/jars

	f.  Copy dwr-0.8.1-jforum-1.0.jar to your local maven repository from WEB-INF/lib 
		as .maven/repository/dwr/jars/dwr-0.8.1-jforum-1.0.jar
		
	g. Copy jboss-cache-1.3.0.SP2.jar to your local maven repository from WEB-INF/lib 
		as .maven/repository/jboss/jars/jboss-cache-1.3.0.SP2.jar

		NOTE: No changes in Project.xml <currentversion> are needed for users of 
		Sakai 2.2
		
	h.	Run Maven Commands
	
	    Use the maven commands below to build and deploy the JForum-Sakai:
	   
	    maven jforum:build - compile and package jforum, copies war artifact to local repository 
		maven jforum:deploy - deploy war to the server 
		maven jforum:clean - remove the prior build files 
		maven jforum:undeploy - undeploy war from the server
		maven jforum - clean, build and deploy 



		
