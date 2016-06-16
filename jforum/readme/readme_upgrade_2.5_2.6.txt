------------------------------------------------------------------
UPGRADE INSTRUCTIONS from SAKAI-JForum 2.5 to SAKAI-JForum 2.6 
These instructions are for existing Sakai-JForum users.
-----------------------------------------------------------------

1. Artifacts changed
	Artifact names are changed from sakai-jforum-xxx to etudes-jforum-xxx. The 
	earlier deployed versions prior to 2.6 have artifacts named sakai-jforum-xxx.
	Undeploy from previous source and make sure the previous versions deployed 
	artifacts from tomcat are removed from 
		tomcat/
				components/sakai-jforum-pack
				shared/lib/sakai-jforum-api-xxx.jar
				webapps/sakai-jforum-tool.war
				webapps/sakai-jforum-tool

    
2.  Build and Deploy JForum-Sakai
    Follow the step # 6 from readme.txt for build and deploy
