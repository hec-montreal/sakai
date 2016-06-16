------------------------------------------------------------------
UPGRADE INSTRUCTIONS from SAKAI-JForum 2.4 to SAKAI-JForum 2.4.1 
These instructions are for existing Sakai-JForum mysql database users.
-----------------------------------------------------------------

1. Database conversion

 	1.1 This is for MySQL forum_id data type change from smallint to mediumint

	Note : Before running this script back up the jforum_forums, jforum_posts, jforum_topics,
    		jforum_search_topics, jforum_forum_sakai_groups tables

	1.2  Run the conversion script located at 
			WEB-INF/config/database/conversion/JForum_mysql_forum_id_data_type_change.sql
	
	Run the script against the sakai database. 
    
2.  Build and Deploy JForum-Sakai
    Follow the step # 6 from readme.txt for build and deploy
