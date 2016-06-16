//grading
<#if c?exists && c.gradeCategory>
	$("#gradingToggle1").hide();
	$("#gradingToggle2").show();
	
	// mark grading not enabled checked
	var gradingenabled = document.getElementsByName("grading_enabled");	
	gradingenabled[0].checked = true;
	
	// mark grading By Topic checked
	var gradingtype = document.getElementsByName("grading_type");
	gradingtype[0].checked = true;
	
	// change points to blank and disable it
	var points = document.getElementById("points");	
	points.value = "";
	points.disabled = true;
	
	// if sentogradebook is existing disable it
	var senttogradebook = document.getElementById('send_to_grade_book');

	if (senttogradebook)
	{
		senttogradebook.disabled = true;
	}
	
	// hide grading options
	$("#gradingTable").hide();
<#else>
	$("#gradingToggle1").show();
	$("#gradingToggle2").hide();
</#if>

// dates
<#if (c?exists && (c.startDate?exists || c.endDate?exists))>
	$("#datesToggle1").hide();
	$("#datesToggle2").show();

<#else>
	
	$("#datesToggle1").show();
	$("#datesToggle2").hide();
</#if>