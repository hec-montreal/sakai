function verifyCheckedPMs()
{
	//var f = document.privmsg_list.id;
	var f = document.getElementsByName("id");
	
	if (f == undefined)	 {
		return false;
	}
	
	if (f.length == undefined)	 {
		if (f.checked) {
			return true;
		}
	}

	for (var i = 0; i < f.length; i++) {
		if (f[i].checked) {
			return true;
		}
	}
	
	alert("${I18n.getMessage("PrivateMessage.SelectTopics")}");
	return false;
}

function flagPMs(mode)
{
	if (verifyCheckedPMs())
	{
		var ele = document.getElementById('action');
		ele.value = mode;
		document.privmsg_list.submit();
	}
}