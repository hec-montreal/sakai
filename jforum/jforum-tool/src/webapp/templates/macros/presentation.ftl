<#-- ********************************************* -->
<#-- Displays the topic folder image by its status -->
<#-- ********************************************* -->
<#macro folderImage topic>
	<#if topic.read>
		<#if topic.status == STATUS_UNLOCKED>
			<#if topic.type == TOPIC_ANNOUNCE>
				<img src="${contextPath}/templates/${templateName}/images/folder_announce.gif" width="19" height="18">
			<#elseif topic.type == TOPIC_STICKY>
				<img src="${contextPath}/templates/${templateName}/images/folder_sticky.gif" width="19" height="18">
			<#elseif topic.isExportTopic()>
				<img src="${contextPath}/templates/${templateName}/images/folder_task.gif" width="19" height="18">
			<#else>
				<#if topic.isHot()>
					<img src="${contextPath}/templates/${templateName}/images/folder_hot.gif" width="19" height="18">
				<#else>
					<img src="${contextPath}/templates/${templateName}/images/folder.gif" width="19" height="18">
				</#if>
			</#if>
		<#else>
			<img src="${contextPath}/templates/${templateName}/images/folder_lock.gif" width="19" height="18">
		</#if>
	<#else>
		<#if topic.status == STATUS_UNLOCKED>
			<#if topic.type == TOPIC_ANNOUNCE>
				<img src="${contextPath}/templates/${templateName}/images/folder_announce_new.gif" width="19" height="18">
			<#elseif topic.type == TOPIC_STICKY>
				<img src="${contextPath}/templates/${templateName}/images/folder_sticky_new.gif" width="19" height="18">
			<#elseif topic.isExportTopic()>
					<img src="${contextPath}/templates/${templateName}/images/folder_new_task.gif" width="19" height="18">
			<#else>
				<#if topic.isHot()>
					<img src="${contextPath}/templates/${templateName}/images/folder_new_hot.gif" width="19" height="18">
				<#else>
					<img src="${contextPath}/templates/${templateName}/images/folder_new.gif" width="19" height="18">
				</#if>
			</#if>
		<#else>
			<img src="${contextPath}/templates/${templateName}/images/folder_lock_new.gif" width="19" height="18">
		</#if>
	</#if>
</#macro>

<#macro row1Class topic>
	<#if topic.type == TOPIC_ANNOUNCE>
		row1Announce
	<#elseif topic.type == TOPIC_STICKY>
		row1Sticky
	<#else>
		row1
	</#if>
</#macro>

<#macro row2Class topic>
	<#if topic.type == TOPIC_ANNOUNCE>
		row2Announce
	<#elseif topic.type == TOPIC_STICKY>
		row2Sticky
	<#else>
		row2
	</#if>
</#macro>

<#macro row3Class topic>
	<#if topic.type == TOPIC_ANNOUNCE>
		row3Announce
	<#elseif topic.type == TOPIC_STICKY>
		row3Sticky
	<#else>
		row3
	</#if>
</#macro>

<#-- ****************** -->
<#-- Moderation buttons -->
<#-- ****************** -->
<#macro moderationButtons>
	 <#if moderator  && openModeration?default(false)>
		<#if can_remove_posts?default(false)><input type="submit" name="topicRemove" value="&nbsp;&nbsp;${I18n.getMessage("Delete")}&nbsp;&nbsp;" class="liteoption" accesskey="${I18n.getMessage("delete-access")}" title="${I18n.getMessage("delete-access-description")}" onclick="return validateModerationDelete();"></#if>
		<#if can_move_topics?default(false)><input type="submit" name="topicMove" value="&nbsp;&nbsp;${I18n.getMessage("move")}&nbsp;&nbsp;" class="liteoption" onclick="return verifyModerationCheckedTopics();"></#if>
		<#if can_lockUnlock_topics?default(false)>
			<input type="submit" name="topicLock" value="&nbsp;&nbsp;${I18n.getMessage("Lock")}&nbsp;&nbsp;" class="liteoption" onclick="return verifyModerationCheckedTopics();">
			<input type="submit" name="topicUnlock" value="&nbsp;&nbsp;${I18n.getMessage("Unlock")}&nbsp;&nbsp;" class="liteoption" onclick="return verifyModerationCheckedTopics();">
		</#if>
		<#--if !archivedCategory>
			<input type="submit" name="topicArchive" value="&nbsp;&nbsp;${I18n.getMessage("Archive")}&nbsp;&nbsp;" class="liteoption" onclick="return verifyModerationCheckedTopics();">
		</#if-->	
		<span class="gensmall"><b><a class="gensmall" href="javascript:select_switch(true);">${I18n.getMessage("PrivateMessage.checkAll")}</a> :: 
									<a class="gensmall" href="javascript:select_switch(false);">${I18n.getMessage("PrivateMessage.uncheckAll")}</a></b></span>
	  <#else>
	  	<span class="gensmall"><b><a class="gensmall" href="javascript:select_switch(true);">${I18n.getMessage("PrivateMessage.checkAll")}</a> :: 
									<a class="gensmall" href="javascript:select_switch(false);">${I18n.getMessage("PrivateMessage.uncheckAll")}</a></b></span>
				
	 
	  </#if>
</#macro>

<#-- ********************** -->
<#-- Forum navigation combo -->
<#-- ********************** -->
<#macro forumsComboTable>
	<table cellspacing="0" cellpadding="0" width="100%" border="0">
		<tr>
			<td align="right">
				<table cellspacing="0" cellpadding="0" border="0">
					<tr>			  
						<td nowrap="nowrap">
							<form name="f" id="f" accept-charset="${encoding}">
								<span class="gensmall">${I18n.getMessage("ForumIndex.goTo")}:&nbsp;
								<select onchange="if(this.options[this.selectedIndex].value != -1){ document.location = '${contextPath}/forums/show/'+ this.options[this.selectedIndex].value +'${extension}'; }" name="select">
									<option value="-1" selected="selected">${I18n.getMessage("ForumIndex.goToSelectAForum")}</option>				
									
									<#list allCategories as category>
										<option value="-1">&nbsp;</option>
										<option value="-1">${category.name}</option>
										<option value="-1">-------------</option>
										
										<#list category.getForums() as forum>
											<option value="${forum.id}">${forum.name}</option>
										</#list>
									</#list>
								</select>
								&nbsp;
								<input class="liteoption" type="button" value="${I18n.getMessage("ForumIndex.goToGo")}" onClick="if(document.f.select.options[document.f.select.selectedIndex].value != -1){ document.location = '${contextPath}/forums/show/'+ document.f.select.options[document.f.select.selectedIndex].value +'${extension}'; }" />
							</form>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</#macro>