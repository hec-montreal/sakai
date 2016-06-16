//Depends on jquery.js
function validateCategoryGrading()
{
	var f = document.form;
	
	if ((f.categories_id.selectedIndex == -1) || (f.categories_id.selectedIndex == 0))
		return;

	var p = { 
			categories_id:f.categories_id.value
	};

	$.ajax({
		type:"POST",
		url:CONTEXTPATH + "/jforum" + SERVLET_EXTENSION + "?module=ajax&action=validateCategoryGrading",
		data:p,
		dataType:"script",
		global:false
	});
}