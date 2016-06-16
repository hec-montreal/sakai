//Depends on jquery.js
function previewPM()
{
	var f = document.post;
	
	var p = { 
			text:f.fckcontent.value, 
			subject:f.subject.value, 
			html:f.disable_html.checked
	};

	$.ajax({
		type:"POST",
		url:CONTEXTPATH + "/jforum" + SERVLET_EXTENSION + "?module=ajax&action=previewPM",
		data:p,
		dataType:"script",
		global:false
	});
}