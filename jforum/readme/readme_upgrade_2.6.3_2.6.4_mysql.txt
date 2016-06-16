------------------------------------------------------------------
UPGRADE INSTRUCTIONS from SAKAI-JForum 2.6.3 to SAKAI-JForum 2.6.4
These instructions are for existing Sakai-JForum mysql database users.
-----------------------------------------------------------------

1. Database conversion

 	1.1 This is for database conversion for existing JForum users for private message flagging and for data
 		type change from TEXT to LONGTEXT for post_text column of jforum_posts_text table and for privmsgs_text
 		column of jforum_privmsgs_text table	
 		
 		
	1.2  Run the conversion script located at 
			jforum-tool/src/webapp/WEB-INF/config/database/conversion/JForum_2_5-2_6_4_mysql_conversion.sql
	
	Run the script against the sakai database.
	    
2.  Build and Deploy JForum-Sakai
    Follow step # 6 from readme_sak_2.5.x_above.txt for build and deploy
    
3. Login as admin and under manage->configurations, change "Message format" to "HTML" for messages sent from JForum
