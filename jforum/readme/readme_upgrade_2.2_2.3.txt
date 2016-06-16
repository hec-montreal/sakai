SAKAI-JFORUM 2.3 RELEASE - Discussion and Private Messages Tool
For Sakai 2.2 and above
------------------------------------------------------------------
UPGRADE INSTRUCTIONS from SAKAI-JForum 2.2 to SAKAI-JForum 2.3. 
These instructions are for the existing Sakai-JForum users.
-----------------------------------------------------------------

1. Database conversion

	Sakai-JForum 2.3 works with Mysql4.1+, Oracle, HSQLDB and PostgreSQL 
	databases. JForum makes use of Sakai's database connection pool.
	
	1.1  It is a good idea to make a backup of the database. 
	
	---------
	For mysql
	---------
	Example: mysql -hlocalhost -usakaiuser -p sakai_db < jforum_2_2.dmp
	where sakai_db is sakai database
	jforum_2_2.dmp is the existing JForum database dump

	1.2  To upgrade from previous JForum mysql database, take the data dump of 
	the existing JForum mysql database. Run the relavant conversion script 
	located at WEB-INF/config/database/conversion
	
	Run the script against the sakai database. 
