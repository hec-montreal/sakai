/**
 * Copyright (c) 2003-2017 The Apereo Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://opensource.org/licenses/ecl2
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sakaiproject.gradebookng.tool.panels;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.io.Serializable;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.StringHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.validation.IValidationError;
import org.sakaiproject.gradebookng.business.GbCategoryType;
import org.sakaiproject.gradebookng.business.model.GbGroup;
import org.sakaiproject.gradebookng.business.util.FormatHelper;
import org.sakaiproject.gradebookng.tool.model.UiMode;
import org.sakaiproject.portal.util.PortalUtils;
import org.sakaiproject.rubrics.logic.RubricsConstants;
import org.sakaiproject.service.gradebook.shared.Assignment;
import org.sakaiproject.service.gradebook.shared.CategoryDefinition;
import org.sakaiproject.service.gradebook.shared.GradebookService;
import org.sakaiproject.service.gradebook.shared.GradingType;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.gradebook.Gradebook;
import org.sakaiproject.util.DateFormatterUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * The panel for the add grade item window
 *
 * @author Steve Swinsburg (steve.swinsburg@gmail.com)
 *
 */
@Slf4j
public class AddOrEditGradeItemPanelContent extends BasePanel {

	private static final long serialVersionUID = 1L;

	private CheckBox counted;
	private CheckBox released;

	private boolean categoriesEnabled;

	private static final String DATEPICKER_FORMAT = "yyyy-MM-dd";

	private Double existingPoints = null;
	private boolean scaleGradesTriggered = false;
	private WebMarkupContainer scaleGradesContainer;

	public AddOrEditGradeItemPanelContent(final String id, final Model<Assignment> assignmentModel, final UiMode mode) {
		super(id, assignmentModel);

		final Gradebook gradebook = this.businessService.getGradebook();
		final GradingType gradingType = GradingType.valueOf(gradebook.getGrade_type());

		final Assignment assignment = assignmentModel.getObject();

		String dueDateString = "";
		if (assignment.getDueDate() != null) {
			dueDateString = DateFormatterUtil.format(assignment.getDueDate(), DATEPICKER_FORMAT, getSession().getLocale());
		}

		this.categoriesEnabled = true;
		if (gradebook.getCategory_type() == GbCategoryType.NO_CATEGORY.getValue()) {
			this.categoriesEnabled = false;
		}

		// get existing points. Will be null for a new assignment
		this.existingPoints = assignmentModel.getObject().getPoints();

		// title
		final TextField<String> title = new TextField<String>("title",
				new PropertyModel<String>(assignmentModel, "name")) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !assignment.isExternallyMaintained();
			}

			@Override
			public boolean isRequired() {
				return !assignment.isExternallyMaintained();
			}

			@Override
			public void error(final IValidationError error) {
				// Use our fancy error message for all validation errors
				error(getString("error.addgradeitem.title"));
			}
		};
		add(title);

		// points
		final Label pointsLabel = new Label("pointsLabel");
		if (gradingType == GradingType.PERCENTAGE) {
			pointsLabel.setDefaultModel(new ResourceModel("label.addgradeitem.percentage"));
		} else {
			pointsLabel.setDefaultModel(new ResourceModel("label.addgradeitem.points"));
		}
		add(pointsLabel);
		final TextField<Double> points = new TextField<Double>("points",
				new PropertyModel<Double>(assignmentModel, "points")) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !assignment.isExternallyMaintained();
			}

			@Override
			public boolean isRequired() {
				return !assignment.isExternallyMaintained();
			}

			@Override
			public void error(final IValidationError error) {
				// Use our fancy error message for all validation errors
				error(getString("error.addgradeitem.points"));
			}
		};

		// onchange, might want to scale
		points.add(new OnChangeAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {

				// conditional option to scale
				if (gradingType == GradingType.POINTS) {

					final Double existing = AddOrEditGradeItemPanelContent.this.existingPoints;
					final Double current = points.getModelObject();

					log.debug("existingPoints: " + existing);
					log.debug("currentPoints: " + current);

					AddOrEditGradeItemPanelContent.this.scaleGradesTriggered = false;
					if (existing != null && existing != current) {
						AddOrEditGradeItemPanelContent.this.scaleGradesTriggered = true;
					}

					log.debug("scaleGradesTriggered: " + AddOrEditGradeItemPanelContent.this.scaleGradesTriggered);

					target.add(AddOrEditGradeItemPanelContent.this.scaleGradesContainer);
				}
			}
		});

		add(points);

		// scale grades option
		this.scaleGradesContainer = new WebMarkupContainer("scaleGradesContainer") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return AddOrEditGradeItemPanelContent.this.scaleGradesTriggered;
			}
		};
		this.scaleGradesContainer.setOutputMarkupPlaceholderTag(true);
		this.scaleGradesContainer.add(new CheckBox("scaleGrades", new PropertyModel<Boolean>(assignmentModel, "scaleGrades")));
		add(this.scaleGradesContainer);

		// map group names to ref
		final LinkedHashMap<String, String> groupsMap = new LinkedHashMap<String, String>();

		Optional<Site> site = this.businessService.getCurrentSite();
		Boolean canUserGradeAll = this.businessService.canUserGradeAll(site.get().getReference());
		final String siteRef = site.get().getReference();

		if ((canUserGradeAll || (assignment.getExternalAssignedGroups() != null && assignment.getExternalAssignedGroups().contains(siteRef)))) {
			groupsMap.put(siteRef, getString("label.addgradeitem.wholesite"));
		}

		List<GbGroup> siteGroups = this.businessService.getSiteSectionsAndGroups();
		for (GbGroup group : siteGroups) {
			groupsMap.put(group.getReference(), group.getTitle());
		}

		final ListMultipleChoice<String> sectionsList = new ListMultipleChoice<String>("sections",
			// not sure why PropertyModel isn't working so I had to do this
			new Model() {
				private static final long serialVersionUID = 1L;

				@Override
				public Serializable getObject() {
					Set<String> definedGroups = assignment.getExternalAssignedGroups();
					//if we don't return a new hashset, wicket returns an arraylist :/
					return definedGroups != null ? new HashSet<String>(definedGroups) : new HashSet<String>();
				}

				@Override
				public void setObject(Serializable object) {
					Set<String> selectedSections = (HashSet<String>) object;
					assignment.setExternalAssignedGroups(
						selectedSections.contains(siteRef) ? Stream.of(siteRef).collect(Collectors.toSet()) : selectedSections);
				}
			},
			new ArrayList<String>(groupsMap.keySet()),
			new IChoiceRenderer<String>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Object getDisplayValue(String object) {
					return groupsMap.get(object);
				}

				@Override
				public String getIdValue(String object, int index) {
					return object;
				}

			}
		) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !assignment.isExternallyMaintained();
			}

			@Override
			public boolean isRequired() {
				return !assignment.isExternallyMaintained();
			}

			@Override
			public void error(final IValidationError error) {
				// Use our fancy error message for all validation errors
				error(getString("error.addgradeitem.sections"));
			}
		};

		// display 5 rows
		sectionsList.setMaxRows(5);
		add(sectionsList);

		// due date
		// TODO date format needs to come from i18n
		final TextField dueDate = new TextField("duedate", Model.of(dueDateString)) {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isEnabled() {
				return !assignment.isExternallyMaintained();
			}
		};
		add(dueDate);

		// category
		final List<CategoryDefinition> categories = new ArrayList<>();
		final Map<Long, CategoryDefinition> categoryMap = new LinkedHashMap<>();

		if (this.categoriesEnabled) {
			categories.addAll(this.businessService.getGradebookCategories());

			for (final CategoryDefinition category : categories) {
				categoryMap.put(category.getId(), category);
			}
		}

		// wrapper for category section. It doesnt get shown at all if
		// categories are not enabled.
		final WebMarkupContainer categoryWrap = new WebMarkupContainer("categoryWrap");
		categoryWrap.setVisible(this.categoriesEnabled);

		final DropDownChoice<Long> categoryDropDown = new DropDownChoice<Long>("category",
				new PropertyModel<Long>(assignmentModel, "categoryId"), new ArrayList<Long>(categoryMap.keySet()),
				new IChoiceRenderer<Long>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Object getDisplayValue(final Long value) {
						final CategoryDefinition category = categoryMap.get(value);
						if (GradebookService.CATEGORY_TYPE_WEIGHTED_CATEGORY == gradebook.getCategory_type()) {
							final String weight = FormatHelper.formatDoubleAsPercentage(category.getWeight() * 100);
							return MessageFormat.format(getString("label.addgradeitem.categorywithweight"),
									category.getName(), weight);
						} else {
							return category.getName();
						}
					}

					@Override
					public String getIdValue(final Long object, final int index) {
						return object.toString();
					}
				}) {
			private static final long serialVersionUID = 1L;

			@Override
			protected String getNullValidDisplayValue() {
				return getString("gradebookpage.uncategorised");
			}
		};

		// always allow an assignment to be set as uncategorized
		categoryDropDown.setNullValid(true);
		categoryDropDown.setVisible(!categories.isEmpty());
		categoryWrap.add(categoryDropDown);

		categoryWrap.add(new WebMarkupContainer("noCategoriesMessage") {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return categories.isEmpty();
			}
		});

		add(categoryWrap);

		// extra credit
		// if an extra credit category is selected, this will be unchecked and
		// disabled
		final AjaxCheckBox extraCredit = new AjaxCheckBox("extraCredit",
				new PropertyModel<Boolean>(assignmentModel, "extraCredit")) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {
				// nothing required
			}
		};
		extraCredit.setOutputMarkupId(true);
		extraCredit.setEnabled(!assignment.isCategoryExtraCredit());
		add(extraCredit);

		final WebMarkupContainer sakaiRubricAssociation = new WebMarkupContainer("sakai-rubric-association");
		sakaiRubricAssociation.add(AttributeModifier.append("dont-associate-label", new ResourceModel("rubrics.dont_associate_label")));
		sakaiRubricAssociation.add(AttributeModifier.append("dont-associate-value", "0"));
		sakaiRubricAssociation.add(AttributeModifier.append("associate-label", new ResourceModel("rubrics.associate_label")));
		sakaiRubricAssociation.add(AttributeModifier.append("associate-value", "1"));
		sakaiRubricAssociation.add(AttributeModifier.append("fine-tune-points", new ResourceModel("rubrics.option_pointsoverride")));
		sakaiRubricAssociation.add(AttributeModifier.append("hide-student-preview", new ResourceModel("rubrics.option_studentpreview")));
		sakaiRubricAssociation.add(AttributeModifier.append("tool-id", RubricsConstants.RBCS_TOOL_GRADEBOOKNG));
		sakaiRubricAssociation.add(AttributeModifier.append("token", rubricsService.generateJsonWebToken(RubricsConstants.RBCS_TOOL_GRADEBOOKNG)));

		if (assignment.getId() != null) {
			sakaiRubricAssociation.add(AttributeModifier.append("entity-id", assignment.getId()));
		}
		add(sakaiRubricAssociation);

		// released
		this.released = new CheckBox("released", new PropertyModel<Boolean>(assignmentModel, "released"));
		this.released.setOutputMarkupId(true);
		add(this.released);

		// counted
		this.counted = new CheckBox("counted", new PropertyModel<Boolean>(assignmentModel, "counted"));
		this.counted.setOutputMarkupId(true);
		if (this.businessService.categoriesAreEnabled()) {
			this.counted.setEnabled(assignment.getCategoryId() != null);
			if (assignment.getCategoryId() == null) {
				this.counted.setModelObject(false);
			}
		}

		add(this.counted);

		// behaviour for when a category is chosen. If the category is extra
		// credit, deselect and disable extra credit checkbox
		categoryDropDown.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(final AjaxRequestTarget target) {

				final Long selected = categoryDropDown.getModelObject();

				// if extra credit, deselect and disable the 'extraCredit'
				// checkbox
				final CategoryDefinition category = categoryMap.get(selected);

				if (category != null && category.getExtraCredit()) {
					extraCredit.setModelObject(false);
					extraCredit.setEnabled(false);
				} else {
					extraCredit.setEnabled(true);
				}
				target.add(extraCredit);

				if (AddOrEditGradeItemPanelContent.this.businessService.categoriesAreEnabled()) {
					if (category == null) {
						AddOrEditGradeItemPanelContent.this.counted.setEnabled(false);
						AddOrEditGradeItemPanelContent.this.counted.setModelObject(false);
					} else {
						AddOrEditGradeItemPanelContent.this.counted.setEnabled(true);
						AddOrEditGradeItemPanelContent.this.counted.setModelObject(true);
						//AddOrEditGradeItemPanelContent.this.released.setModelObject(true);
						//HEC ZCII-4704
						AddOrEditGradeItemPanelContent.this.released.setModelObject(false);
					}

					target.add(AddOrEditGradeItemPanelContent.this.counted);
					target.add(AddOrEditGradeItemPanelContent.this.released);
				}
			}
		});

		if (assignment.isExternallyMaintained()) {
			warn(MessageFormat.format(getString("info.edit_assignment_external_items"), assignment.getExternalAppName()));
		}
	}

	public void renderHead(final IHeaderResponse response) {

		final String version = PortalUtils.getCDNQuery();
		response.render(StringHeaderItem.forString(
			"<script type=\"module\" src=\"/rubrics-service/webcomponents/rubric-association-requirements.js" + version + "\"></script>"));
	}
}
