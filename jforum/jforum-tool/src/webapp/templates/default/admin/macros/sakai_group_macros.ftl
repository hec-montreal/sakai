<#-- ************************************************** -->
<#-- Create a checkbox HTML field with the sakai groups -->
<#-- ************************************************** -->
<#macro checkboxSelectedSakaiGroups name groups forum>
	<#assign len = groups.size() - 1>
	<#assign selectedSakaiGroupsList = forum.groups>
	<#if (len > -1)>
	<#list 0..len as i>
		<#assign group = groups.get(i)>
		<tr>
			<td headers="selectAllGroups" class="" valign="top" align="center">
				<input  type="checkbox"  name="${name}" id="group-${group.id}" title="${group.title}" value="${group.id}" <#if selectedSakaiGroupsList.contains(group.id)>checked</#if>/>
			</td>
			<td headers="groupname" nowrap="nowrap" valign="top">
				<span class="gen">${group.title}</span>
			</td>
			<td headers="groupdescription">
				<span class="gen">${group.description?default('')}</span> 							 
			</td>
		</tr>
	</#list>
	</#if>
</#macro>

<#-- *********************************************************** -->
<#-- Create a checkbox HTML field with the selected sakai groups -->
<#-- *********************************************************** -->
<#macro checkboxSakaiGroups name groups>
	<#assign len = groups.size() - 1>
	<#if (len > -1)>
	<#list 0..len as i>
		<#assign group = groups.get(i)>
		<tr>
			<td headers="selectAllGroups" class="" valign="top" align="center">
				<input  type="checkbox"  name="${name}" id="group-${group.id}" title="${group.title}" value="${group.id}"/>
			</td>
			<td headers="groupname" nowrap="nowrap" valign="top">
				<span class="gen">${group.title}</span>
			</td>
			<td headers="groupdescription">
				<span class="gen">${group.description?default('')}</span> 							 
			</td>
		</tr>
	</#list>
	</#if>
</#macro>