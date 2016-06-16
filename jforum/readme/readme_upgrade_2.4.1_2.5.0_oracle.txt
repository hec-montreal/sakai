------------------------------------------------------------------
UPGRADE INSTRUCTIONS from SAKAI-JForum 2.4.1 to SAKAI-JForum 2.5.0 
These instructions are for existing Sakai-JForum oracle database users.
-----------------------------------------------------------------

1. Database conversion

 	1.1 This is for database conversion for existing JForum users. For private message attachments, 
 		grading and change in task topics to reuse topics

	Note : 1. Before running this script back up jforum_privmsgs, jforum_forums, jforum_topics, jforum_search_topics, jforum_categories, 
			jforum_attach_desc tables. 
			
		   2. The last statement related to update topic type is commented in the script. You can run this 
		      statement after running the conversion script or along with conversion script by uncommenting 
		      the statement
    
	1.2  Run the conversion script located at 
			jforum-tool/src/webapp/WEB-INF/config/database/conversion/JForum_2_4-2_5_oracle_conversion.sql
	
	Run the script against the sakai database. 
2.  There is an addtional property in SystemGlobals.properties. If you are using existing SystemGlobals.properties
	add the below to SystemGlobals.properties.
	
	# #############
	# FORUM GRADING 
	# #############
	sakai.gradebook.tool.id = sakai.gradebook.tool
	    
2.  Build and Deploy JForum-Sakai
    Follow the step # 6 from readme_sak_2.5.x_above.txt for build and deploy
