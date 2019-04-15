/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/edu-services/tags/edu-services-1.0.6/cm-service/cm-impl/hibernate-impl/hibernate/src/java/org/sakaiproject/coursemanagement/impl/AcademicSessionCmImpl.java $
 * $Id: AcademicSessionCmImpl.java 59674 2009-04-03 23:05:58Z arwhyte@umich.edu $
 ***********************************************************************************
 *
 * Copyright (c) 2006, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.coursemanagement.impl;

import java.io.Serializable;

import org.sakaiproject.coursemanagement.api.AcademicCareer;

public class AcademicCareerCmImpl implements AcademicCareer, Serializable {
	
	private static final long serialVersionUID = 1L;

	private String eid;
	private String description;
	private String description_fr_ca;
	
	public AcademicCareerCmImpl(){
	    
	}
	
	public AcademicCareerCmImpl(String eId, String description, String description_fr_CA){
	    this.eid = eId;
	    this.description = description;
	    this.description_fr_ca = description_fr_CA;
	}
	
	
	public String getEid() {
	    return eid;
	}

	public void setEid(String eId) {
	    this.eid = eId;
	}

	public String getDescription() {
	    return description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	public String getDescription_fr_ca() {
	    return description_fr_ca;
	}

	public void setDescription_fr_ca(String description_fr_CA) {
	    this.description_fr_ca = description_fr_CA;
	}

}
