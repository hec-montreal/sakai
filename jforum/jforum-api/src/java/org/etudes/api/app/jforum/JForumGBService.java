/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-api/src/java/org/etudes/api/app/jforum/JForumGBService.java $ 
 * $Id: JForumGBService.java 65268 2009-12-15 22:39:38Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008, 2009 Etudes, Inc. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 ***********************************************************************************/

package org.etudes.api.app.jforum;

import java.util.Date;
import java.util.Map;

public interface JForumGBService
{
	/**
	 * Checks the gradebook with the given uid.
	 * @param context 	The gradebook's unique identifier
	 * @return true 	if exists
	 * 		   false 	if there is no gradebook with the give uid	
	 */
	public Boolean isGradebookDefined(String context);
	
	/**
	 * Check to see if an assignment with the given external id already exists in the given gradebook.
	 * @param gradebookUid	The gradebook's unique identifier
	 * @param externalId	The assessment's external identifier
	 * @return 	true 		if exists
	 * 		   	false 		if there is no gradebook with the give external id	
	 */
	public Boolean isExternalAssignmentDefined(String gradebookUid, String externalId);
	
	/**
	 * Checks the gradebook with the given uid and title
	 * @param gradebookUid		Gradebook unique identifier
	 * @param assignmentTitle	Title
	 * @return	true 		if exists
	 * 		   	false 		if there is no gradebook with the given uid and title
	 */
	public Boolean isAssignmentDefined(String gradebookUid, String assignmentTitle);
	
	/**
	 * Add grades to gradebook
	 * @param gradebookUid	Gradebook unique identifier
	 * @param externalId	External id
	 * @param externalUrl	External url
	 * @param title			Title
	 * @param points		Points
	 * @param dueDate		Due date
	 * @param externalServiceDescription	Description
	 * @return	true 		if add is success
	 * 		   	false 		if there are errors
	 */
	public Boolean addExternalAssessment(String gradebookUid, String externalId, String externalUrl, String title, double points, Date dueDate, 
													String externalServiceDescription);
	/**
	 * Update grades in gradebook
	 * @param gradebookUid	Gradebook unique identifier
	 * @param externalId	External id
	 * @param externalUrl	External url
	 * @param title			Title
	 * @param points		Points
	 * @param dueDate		Due date
	 * @return	true 		if update is success
	 * 		   	false 		if there are errors
	 */
	public Boolean updateExternalAssessment(String gradebookUid, String externalId, String externalUrl,
			String title, double points, Date dueDate);
	
	/**
	 * Remove grades from gradebook
	 * @param gradebookUid	Gradebook unique identifier
	 * @param externalId	External id
	 * @return	true 		if remove is success
	 * 		   	false 		if there are errors
	 */
	public Boolean removeExternalAssessment(String gradebookUid, String externalId);
	
	/**
	 * update gradebook scores
	 * @param gradebookUid	Gradebook unique identifier
	 * @param externalId	External id
	 * @param studentUidsToScores	Student id and scores map
	 * @return	true 		if update is success
	 * 		   	false 		if there are errors
	 */
	public Boolean updateExternalAssessmentScores(String gradebookUid, String externalId, Map studentUidsToScores);
	
	/**
	 * update gradebook score
	 * @param gradebookUid	Gradebook unique identifier
	 * @param externalId	External id
	 * @param studentUid	Student id
	 * @param points		points
	 * @return	true 		if update is success
	 * 		   	false 		if there are errors
	 */
	public Boolean updateExternalAssessmentScore(String gradebookUid, String externalId, String studentUid, Double points);

}
