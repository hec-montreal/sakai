/**********************************************************************************
 * $URL$
 * $Id$
 ***********************************************************************************
 *
 * Copyright (c) 2006, 2007, 2008 The Sakai Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.opensource.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 **********************************************************************************/
package org.sakaiproject.coursemanagement.impl;

import org.sakaiproject.coursemanagement.api.*;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * A template to use when implementing CourseManagementService to provide
 * course and enrollment data in a federated CM configuration.  Extending this class
 * should be useful for institutions intending to federate external datasources
 * (via webservices, SIS APIs, etc) with Sakai's hibernate-based
 * CourseManagementService.
 * 
 * @author <a href="mailto:jholtzman@berkeley.edu">Josh Holtzman</a>
 *
 */
public class CourseManagementServiceSampleChainImpl implements CourseManagementService {

	public Set findCourseOfferings(String courseSetEid, String academicSessionEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseSetEid, CourseSet.class.getName());
	}

	public Set findCourseOfferingsByAcadCareerAndAcademicSession(String acadCareer, String academicSessionEid) throws IdNotFoundException {
		throw new IdNotFoundException(acadCareer, acadCareer);
	}

	public Set findCourseOfferingsByAcadCareer(String acadCareer) throws IdNotFoundException {
			throw new IdNotFoundException(acadCareer, acadCareer);
	}

	public List findCourseSets(String category) {
		return null;
	}

	public Set findCurrentlyEnrolledEnrollmentSets(String userId) {
		return null;
	}

	@Override
	public Set<String> findCurrentEnrollmentIds() {
		return null;
	}

	public Set findCurrentEnrollments() {
		return null;
	}

	public Set findCurrentlyInstructingEnrollmentSets(String userId) {
		return null;
	}

	public Enrollment findEnrollment(String userId, String eid) {
		return null;
	}

	public String findEnrollmentId(String userId, String eid) {
		return null;
	}

	public Set findInstructingSections(String userId) {
		return null;
	}

	public Set findInstructingSections(String userId, String academicSessionEid) throws IdNotFoundException {
		throw new IdNotFoundException(academicSessionEid, AcademicSession.class.getName());
	}

	public Set findSectionsByCategory(String category) throws IdNotFoundException {
		throw new IdNotFoundException(category, category);
	}

	public AcademicSession getAcademicSession(String academicSessionEid) throws IdNotFoundException {
		throw new IdNotFoundException(academicSessionEid, AcademicSession.class.getName());
	}

	public List getAcademicSessions() {
		return null;
	}

	public CanonicalCourse getCanonicalCourse(String canonicalCourseEid) throws IdNotFoundException {
		throw new IdNotFoundException(canonicalCourseEid, CanonicalCourse.class.getName());
	}

	public Set getCanonicalCourses(String courseSetEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseSetEid, CourseSet.class.getName());
	}

	public Set getChildCourseSets(String parentCourseSetEid) throws IdNotFoundException {
		throw new IdNotFoundException(parentCourseSetEid, CourseSet.class.getName());
	}

	public Set getChildSections(String parentSectionEid) throws IdNotFoundException {
		throw new IdNotFoundException(parentSectionEid, Section.class.getName());
	}

	public CourseOffering getCourseOffering(String courseOfferingEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseOfferingEid, CourseOffering.class.getName());
	}

	public Set getCourseOfferingMemberships(String courseOfferingEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseOfferingEid, CourseOffering.class.getName());
	}

	public Set getCourseOfferingsInCourseSet(String courseSetEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseSetEid, CourseSet.class.getName());
	}

	public CourseSet getCourseSet(String courseSetEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseSetEid, CourseSet.class.getName());
	}

	public Set getCourseSetMemberships(String courseSetEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseSetEid, CourseSet.class.getName());
	}

	public Set getCourseSets() {
		return null;
	}

	public List getCurrentAcademicSessions() {
		return null;
	}

	public EnrollmentSet getEnrollmentSet(String enrollmentSetEid) throws IdNotFoundException {
		throw new IdNotFoundException(enrollmentSetEid, EnrollmentSet.class.getName());
	}

	public Set getEnrollmentSets(String courseOfferingEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseOfferingEid, CourseOffering.class.getName());
	}

	public Set getEnrollments(String enrollmentSetEid) throws IdNotFoundException {
		throw new IdNotFoundException(enrollmentSetEid, EnrollmentSet.class.getName());
	}

	public Set getEquivalentCanonicalCourses(String canonicalCourseEid) throws IdNotFoundException {
		throw new IdNotFoundException(canonicalCourseEid, CanonicalCourse.class.getName());
	}

	public Set getEquivalentCourseOfferings(String courseOfferingEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseOfferingEid, CourseOffering.class.getName());
	}

	public Set getInstructorsOfRecordIds(String enrollmentSetEid) throws IdNotFoundException {
		throw new IdNotFoundException(enrollmentSetEid, EnrollmentSet.class.getName());
	}

	public Section getSection(String sectionEid) throws IdNotFoundException {
		throw new IdNotFoundException(sectionEid, Section.class.getName());
	}

	public Set getSectionMemberships(String sectionEid) throws IdNotFoundException {
		throw new IdNotFoundException(sectionEid, Section.class.getName());
	}

	public Set getSections(String courseOfferingEid) throws IdNotFoundException {
		throw new IdNotFoundException(courseOfferingEid, CourseOffering.class.getName());
	}

	public boolean isEmpty(String courseSetEid) {
		throw new UnsupportedOperationException();
	}

	public boolean isEnrolled(String userId, Set enrollmentSetEids) {
		throw new UnsupportedOperationException();
	}

	public boolean isEnrolled(String userId, String eid) {
		throw new UnsupportedOperationException();
	}

	public Set findEnrolledSections(String userId) {
		return null;
	}

	public Map findCourseOfferingRoles(String userEid) {
		return null;
	}

	public Map findCourseSetRoles(String userEid) {
		return null;
	}

	public Map findSectionRoles(String userEid) {
		return null;
	}

	public Set getCourseOfferingsInCanonicalCourse(String canonicalCourseEid) throws IdNotFoundException {
		throw new IdNotFoundException(canonicalCourseEid, CanonicalCourse.class.getName());
	}

	public boolean isAcademicSessionDefined(String eid) {
		throw new UnsupportedOperationException();
	}

	public boolean isCanonicalCourseDefined(String eid) {
		throw new UnsupportedOperationException();
	}

	public boolean isCourseOfferingDefined(String eid) {
		throw new UnsupportedOperationException();
	}

	public boolean isCourseSetDefined(String eid) {
		throw new UnsupportedOperationException();
	}

	public boolean isEnrollmentSetDefined(String eid) {
		throw new UnsupportedOperationException();
	}

	public boolean isSectionDefined(String eid) {
		throw new UnsupportedOperationException();
	}

	public List<String> getSectionCategories() {
		return null;
	}

	public String getSectionCategoryDescription(String categoryCode) {
		return null;
	}

	public Map<String, String> getEnrollmentStatusDescriptions(Locale locale) {
		return null;
	}

	public Map<String, String> getGradingSchemeDescriptions(Locale locale) {
		return null;
	}

	public Map<String, String> getMembershipStatusDescriptions(Locale locale) {
		return null;
	}

	public List<CourseOffering> findActiveCourseOfferingsInCanonicalCourse(
			String eid) {
		return null;
	}

	public List<AcademicCareer> getAcademicCareers() {
	    return null;
	}

	public AcademicCareer getAcademicCareer(String eid)
		throws IdNotFoundException {
	    return null;
	}

	public boolean isAcademicCareerDefined(String eid) {
	    return false;
	}

}
