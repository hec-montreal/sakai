$("#previewSubject").html("${postPreview.post.subject?html}");
$("#previewMessage").html("${postPreview.post.text}");
$("#previewTable").show();

var s = document.location.toString();
var index = s.indexOf("#preview");

if (index > -1) {
	s = s.substring(0, index);
}

document.location = s + "#preview";

