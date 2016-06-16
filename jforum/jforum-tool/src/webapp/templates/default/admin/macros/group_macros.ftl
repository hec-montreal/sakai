<#-- ******************************* -->
<#-- Make the HTML for group listing -->
<#-- ******************************* -->
<#macro listGroups parent>
	<#assign total = parent.size() - 1>
	
	<#if (total >= 0 )>
		<#list 0..total as i>
			<#global level = level + 2>
			<#assign node = parent.getNode(i)>
			
			<tr bgcolor="#f4f4f4">
			<td><span class="gen"><#list 0..level as j>&nbsp;</#list>${node.name}</span></td>
			<td><span class="gen"><a href="${contextPath}/${moduleName}/edit/${node.id}${extension}">${I18n.getMessage("Groups.List.Edit")}</a></span></td>
			<td><input type="checkbox" name="group_id" value="${node.id}"></td>
			<td class="row2"><span class="gen"><a href="${contextPath}/${moduleName}/permissions/${node.id}.page">${I18n.getMessage("Permissions")}</a></span></td>
			</tr>

			<@listGroups node/>

		<#global level = level - 2>
		</#list>
	</#if>
</#macro>

<#-- ******************************************** -->
<#-- Create a <select> HTML field with the groups -->
<#-- ******************************************** -->
<#macro selectFieldGroups name groups parentId multiple selectedList>
	<select name="${name}" <#if multiple>multiple size="4"</#if>>
	
	<#if !multiple>
		<option value="0">Top level Group</option>
	</#if>
		
	<#assign len = groups.size() - 1>
	<#if (len > -1)>
	<#list 0..len as i>
		<#assign node = groups.get(i)>
		<#global level = 0>
		<option value="${node.id}"<#if parentId == node.id || selectedList.contains(node.id)> selected</#if>>${node.name}</option>
		<@selectOption node, parentId/>
	</#list>
	</#if>

	</select>	
</#macro>

<#-- *************************************************************** -->
<#-- Create the <option> fields related to "selectFieldGroups" macro -->
<#-- *************************************************************** -->
<#macro selectOption node parentId>
	<#assign len = node.size() - 1>
	
	<#if (len >= 0)>
		<#list 0..len as i>
			<#global level = level + 2>
			<#assign n = node.getNode(i)>
			<option value="${n.id}"<#if parentId == n.id || selectedList.contains(node.id)> selected</#if>><#list 0..level as j>&nbsp;</#list>${n.name}</option>
			<@selectOption n, parentId/>
			
			<#global level = level - 2>
		</#list>
	</#if>
</#macro>

<#-- Mallika's macro for select groups without facilitator beg 
11/01/05 Murthy - Participant condition removed
-->
<#macro selectFieldFacGroups name groups parentId multiple selectedList>
	<select name="${name}" <#if multiple>multiple size="4"</#if>>
	
	<#if !multiple>
		<option value="0">Top level Group</option>
	</#if>
		
	<#assign len = groups.size() - 1>
	<#if (len > -1)>
	<#list 0..len as i>
		<#assign node = groups.get(i)>
		<#global level = 0>
		<#if node.name != "Facilitator" && node.name != "Administration">
		<option value="${node.id}"<#if parentId == node.id || selectedList.contains(node.id)> selected</#if>>${node.name}</option>
		<@selectOption node, parentId/>
		</#if>
	</#list>
	</#if>

	</select>	
</#macro>
<#macro selectFieldFacNoDefGroups name groups parentId multiple selectedList>
	<select name="${name}" <#if multiple>multiple size="4"</#if>>
	
	<#if !multiple>
		<option value="0">Top level Group</option>
	</#if>
		
	<#assign len = groups.size() - 1>
	<#if (len > -1)>
	<#list 0..len as i>
		<#assign node = groups.get(i)>
		<#global level = 0>
		<#if node.name != "Facilitator" && node.name != "Administration">
		<option value="${node.id}"<#if parentId == node.id || selectedList.contains(node.id)> selected</#if>>${node.name}</option>
		<@selectOption node, parentId/>
		</#if>
	</#list>
	</#if>

	</select>	
</#macro>
<#-- Mallika's macro end -->