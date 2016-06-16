SAKAI-JFORUM 2.3.m1 RELEASE - Discussion and Private Messages Tool
For Sakai 2.3 and above
------------------------------------------------------------------
UPGRADE INSTRUCTIONS from SAKAI-JForum 2.3 to SAKAI-JForum 2.3.m1 
These instructions are for the existing Sakai-JForum users.
-----------------------------------------------------------------

1.  Packaging path settings for the export/import
	
    The dependency files for the export/import process are in the 
	var/jforum/packagefiles directory in the JForum-Sakai source code. 
	Specify the absolute path for export/import process in 
	WEB-INF/config/SystemGlobals.properties for the variable "packaging.path"
	Copy the jforum directory and its contents into this directory. 
	Ensure that this directory and its children have read and write 
	permissions. 
	
	Eg. if your packaging directory is /var/jforum/packagefiles, specify 
	this in the following manner in WEB-INF/config/SystemGlobals.properties 
	and copy the jforum directory and its contents into this directory
    
    packaging.path=/var/jforum/packagefiles
    
    If you are on unix/linux copy the jforum directory and its contents 
    into /var directory
    
    For windows based system copy the jforum directory and its contents 
    into C:\var directory
    
2.  Build and Deploy JForum-Sakai
    Follow the step # 6 from readme.txt for build and deploy
