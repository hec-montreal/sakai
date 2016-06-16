------------------------------------------------------------------
UPGRADE INSTRUCTIONS from SAKAI-JForum 2.3 to SAKAI-JForum 2.4 
These instructions are for existing Sakai-JForum users.
-----------------------------------------------------------------

1. Database conversion

	Sakai-JForum 2.4 works with Mysql4.1+, Oracle and HSQLDB databases. 
	JForum makes use of Sakai's database connection pool and database.
	
	1.1  It is a good idea to make a backup of the database. 
	
	------------------------
	Backup example For mysql
	------------------------
	Example: mysql -hlocalhost -usakaiuser -p sakai_db < jforum_2_2.dmp
	where sakai_db is sakai database
	jforum_2_2.dmp is the existing JForum database dump

	1.2  To upgrade from previous JForum mysql database, take the data dump of 
	the existing JForum database. Run the relavant conversion script 
	located at WEB-INF/config/database/conversion
	
	Run the script against the sakai database. 

    
2.  Build and Deploy JForum-Sakai
    Follow the step # 6 from readme.txt for build and deploy
