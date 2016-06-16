function savechanges(navto)
{
	try
	{
		var ele = document.getElementById("autosavenav");
		
		if (ele == undefined)
		{
			defaultnav(navto);
			return true;
		}
		
		if (ele.value == "forumaddedit")
		{
			if (document.form.forum_name.value.replace(/^\s*|\s*$/g, "").length == 0)
			{
				defaultnav(navto);
				return true;
			}
			else
			{
				return validatecatgforumforms(ele, navto);
			}
		}
		else if (ele.value == "catgaddedit")
		{
			if (document.form.category_name.value.replace(/^\s*|\s*$/g, "").length == 0)
			{
				defaultnav(navto);
				return true;
			}
			else
			{
				return validatecatgforumforms(ele, navto);
			}
		}
		else if (ele.value == "forumlist")
		{
			if (validateDates())
			{
				var ele1 = document.getElementById("saveForums");
				
				ele.value = navto;
				
				if (ele1 != undefined)
				{
					ele1.click();
				}
				else
				{
					defaultnav(navto);
				}
	
				return true;
			}
			return false;
		}
		else if (ele.value == "categorylist")
		{
			if (validateDates())
			{
				var ele1 = document.getElementById("saveCategories");
				
				ele.value = navto;
				
				if (ele1 != undefined)
				{
					ele1.click();
				}
				else
				{
					defaultnav(navto);
				}
				
				return true;
			}
			return false;
		}
		else
		{
			defaultnav(navto);
		}
	
		return false;
	}
	catch(err)
	{
		//alert(err.description);
		defaultnav(navto);
		return true;
	}
	finally
	{	
	}
}

function defaultnav(navto)
{
	if (navto == 'forums')
	{
		document.location = "${contextPath}/adminForums/list${extension}";
	}
	else if (navto == 'categories')
	{
		document.location = "${contextPath}/adminCategories/list${extension}";
	}
	else if (navto == 'discussionlist')
	{
		document.location = "${contextPath}/forums/list${extension}";
	}
	else if (navto == 'importexport')
	{
		document.location = "${contextPath}/adminImportExport/list${extension}";
	}
	else
	{
		document.location = "${contextPath}/adminForums/list${extension}";
	}
}

function validatecatgforumforms(ele, navto)
{
	if (checkInput())
	{
		ele.value = navto;
		var ele1 = document.getElementById("submit");
		
		if (ele1 != undefined)
		{
			ele1.click();
		}
		else
		{
			defaultnav(navto);
		}
		
		return true;
	}
	return false;
}