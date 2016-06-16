/**********************************************************************************
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-impl/impl/src/java/org/etudes/component/app/jforum/JForumGBServiceImpl.java $ 
 * $Id: JForumGBServiceImpl.java 65268 2009-12-15 22:39:38Z murthy@etudes.org $ 
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
package org.etudes.component.app.jforum;

import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etudes.api.app.jforum.JForumGBService;
import org.sakaiproject.service.gradebook.shared.AssessmentNotFoundException;
import org.sakaiproject.service.gradebook.shared.AssignmentHasIllegalPointsException;
import org.sakaiproject.service.gradebook.shared.ConflictingAssignmentNameException;
import org.sakaiproject.service.gradebook.shared.ConflictingExternalIdException;
import org.sakaiproject.service.gradebook.shared.ExternalAssignmentProvider;
import org.sakaiproject.service.gradebook.shared.GradebookExternalAssessmentService;
import org.sakaiproject.service.gradebook.shared.GradebookNotFoundException;
import org.sakaiproject.service.gradebook.shared.GradebookService;

public class JForumGBServiceImpl implements JForumGBService
{
	private static Log logger = LogFactory.getLog(JForumGBServiceImpl.class);
	
	protected GradebookService gradebookService = null;
	
	public GradebookExternalAssessmentService getGradebookExternalAssessmentService() {
		return gradebookExternalAssessmentService;
	}

	public void setGradebookExternalAssessmentService(
			GradebookExternalAssessmentService gradebookExternalAssessmentService) {
		this.gradebookExternalAssessmentService = gradebookExternalAssessmentService;
	}

	protected GradebookExternalAssessmentService gradebookExternalAssessmentService = null;
	
	public void init(){
		if (logger.isInfoEnabled()) logger.info("init....");
	}
	
	public void destroy(){
		if (logger.isInfoEnabled()) logger.info("destroy....");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Boolean isGradebookDefined(String context)
	{
		boolean hasGradebook = gradebookService.isGradebookDefined(context);
		return Boolean.valueOf(hasGradebook);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Boolean isExternalAssignmentDefined(String gradebookUid, String externalId)
	{
		boolean hasExternalAssignment = false;
		
		try
		{
			//Migration Sakai 11
			hasExternalAssignment = gradebookService.isAssignmentDefined(gradebookUid, externalId);
		}
		catch (GradebookNotFoundException e)
		{
			if (logger.isWarnEnabled()) logger.warn("isExternalAssignmentDefined:" + e.toString());
		}

		return Boolean.valueOf(hasExternalAssignment);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Boolean isAssignmentDefined(String gradebookUid, String assignmentTitle)
	{
		boolean assignmentDefined = false;
		
		try
		{
			assignmentDefined = gradebookService.isAssignmentDefined(gradebookUid, assignmentTitle);
		}
		catch (GradebookNotFoundException e)
		{
			if (logger.isWarnEnabled()) logger.warn("isAssignmentDefined:" + e.toString());
		}
		return Boolean.valueOf(assignmentDefined);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Boolean addExternalAssessment(String gradebookUid, String externalId, String externalUrl, String title, double points, Date dueDate, 
			String externalServiceDescription)
	{
		boolean addSuccess = false;
		
		try
		{
			gradebookExternalAssessmentService.addExternalAssessment(gradebookUid, externalId, externalUrl, title, points, dueDate, 
					externalServiceDescription);
			
			addSuccess = true;
		}
		catch (GradebookNotFoundException e)
		{
			if(logger.isWarnEnabled()) logger.warn("addExternalAssessment : "+ e.toString());
		}
		catch (ConflictingAssignmentNameException e)
		{
			if(logger.isWarnEnabled()) logger.warn("addExternalAssessment : "+ e.toString());
		}
		catch (ConflictingExternalIdException e)
		{
			if(logger.isWarnEnabled()) logger.warn("addExternalAssessment : "+ e.toString());
		}
		catch (AssignmentHasIllegalPointsException e)
		{
			if(logger.isWarnEnabled()) logger.warn("addExternalAssessment : "+ e.toString());
		}
		
		return Boolean.valueOf(addSuccess);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Boolean removeExternalAssessment(String gradebookUid, String externalId)
	{
		boolean removeSuccess = false;
		try
		{
			gradebookExternalAssessmentService.removeExternalAssessment(gradebookUid, externalId);
			removeSuccess = true;
		}
		catch (GradebookNotFoundException e)
		{
			if(logger.isWarnEnabled()) logger.warn("removeExternalAssessment : "+ e.toString());
		}
		catch (AssessmentNotFoundException e)
		{
			if(logger.isWarnEnabled()) logger.warn("removeExternalAssessment : "+ e.toString());
		}
		
		return Boolean.valueOf(removeSuccess);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Boolean updateExternalAssessment(String gradebookUid, String externalId, String externalUrl,
			String title, double points, Date dueDate)
	{
		boolean updateAssessment = false;
		try
		{
			gradebookExternalAssessmentService.updateExternalAssessment(gradebookUid, externalId, externalUrl, title, points, dueDate);
			updateAssessment = true;
		}
		catch (GradebookNotFoundException e)
		{
			if(logger.isWarnEnabled()) logger.warn("updateExternalAssessment : "+ e.toString());
		}
		catch (AssessmentNotFoundException e)
		{
			if(logger.isWarnEnabled()) logger.warn("updateExternalAssessment : "+ e.toString());
		}
		catch (ConflictingAssignmentNameException e)
		{
			if(logger.isWarnEnabled()) logger.warn("updateExternalAssessment : "+ e.toString());
		}
		catch (AssignmentHasIllegalPointsException e)
		{
			if(logger.isWarnEnabled()) logger.warn("updateExternalAssessment : "+ e.toString());
		}
		return Boolean.valueOf(updateAssessment);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Boolean updateExternalAssessmentScores(String gradebookUid, String externalId, Map studentUidsToScores)
	{
		boolean updateScoresSuccess = false;
		try
		{
			gradebookExternalAssessmentService.updateExternalAssessmentScores(gradebookUid, externalId, studentUidsToScores);
			updateScoresSuccess = true;
		}
		catch (GradebookNotFoundException e)
		{
			if(logger.isWarnEnabled()) logger.warn("updateExternalAssessmentScores : "+ e.toString());
		}
		catch (AssessmentNotFoundException e)
		{
			if(logger.isWarnEnabled()) logger.warn("updateExternalAssessmentScores : "+ e.toString());
		}
		
		return Boolean.valueOf(updateScoresSuccess);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public Boolean updateExternalAssessmentScore(String gradebookUid, String externalId, String studentUid, Double points)
	{
		boolean updateScoreSuccess = false;
		try
		{
			gradebookExternalAssessmentService.updateExternalAssessmentScore(gradebookUid, externalId, studentUid, points+"");
			updateScoreSuccess = true;
		}
		catch (GradebookNotFoundException e)
		{
			if(logger.isWarnEnabled()) logger.warn("updateExternalAssessmentScore : "+ e.toString());
		}
		catch (AssessmentNotFoundException e)
		{
			if(logger.isWarnEnabled()) logger.warn("updateExternalAssessmentScore : "+ e.toString());
		}
		
		return Boolean.valueOf(updateScoreSuccess);
		
	}
	
	/**
	 * @return the gradebookService
	 */
	public GradebookService getGradebookService()
	{
		return gradebookService;
	}

	/**
	 * @param gradebookService the gradebookService to set
	 */
	public void setGradebookService(GradebookService gradebookService)
	{
		this.gradebookService = gradebookService;
	}

}
