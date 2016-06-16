/*********************************************************************************** 
 * $URL: https://source.sakaiproject.org/contrib/etudes/sakai-jforum/tags/2.8.1/jforum-tool/src/java/org/etudes/jforum/dao/generic/EvaluationUserOrderComparator.java $ 
 * $Id: EvaluationUserOrderComparator.java 55493 2008-12-01 22:55:48Z murthy@etudes.org $ 
 *********************************************************************************** 
 * 
 * Copyright (c) 2008 Etudes, Inc. 
 * 
 * Portions completed before September 1, 2008 Copyright (c) 2004, 2005, 2006, 2007 Foothill College, ETUDES Project 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you 
 * may not use this file except in compliance with the License. You may 
 * obtain a copy of the License at 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied. See the License for the specific language governing 
 * permissions and limitations under the License. 
 * 
 **********************************************************************************/ 
package org.etudes.jforum.dao.generic;

import java.io.Serializable;
import java.util.Comparator;

import org.etudes.jforum.entities.Evaluation;



/**
 * Compareevaluations user name
 * 
 * @author Murthy Tanniru
 * @version $Id: EvaluationUserOrderComparator.java 55493 2008-12-01 22:55:48Z murthy@etudes.org $
 */
public class EvaluationUserOrderComparator implements Comparator, Serializable
{
	/**
	 * {@inheritDoc}
	 */
	public int compare(Object o1, Object o2)
	{
		Evaluation e1 = (Evaluation) o1;
		Evaluation e2 = (Evaluation) o2;

		return e1.getUserLastName().toUpperCase().compareTo(e2.getUserLastName().toUpperCase());
	}

}
