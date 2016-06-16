<#macro littlePostPagination topicId postsPerPage totalReplies>
	[ <img src="${contextPath}/templates/${templateName}/images/icon_latest_reply.gif"> ${I18n.getMessage("goToPage")}: 

	<#assign totalPostPages = ((totalReplies+1)/postsPerPage)?int />
	<#if (((totalReplies+1)%postsPerPage) > 0)>
		<#assign totalPostPages = (totalPostPages +1)/>
	</#if>

	<#list 1 .. totalPostPages as page>
		<#assign start = postsPerPage * (page - 1)/>

		<a href="${contextPath}/posts/list/${start}/${topicId}${extension}">${page}</a>	
		<#if (page < totalPostPages) >,</#if>
	</#list>

	]
</#macro>

<#macro doPagination action id=-1>
	<#if (totalRecords > recordsPerPage)>
		<span class="gensmall"><b>
		${I18n.getMessage("goToPage")}:
		
		<#assign numberOfActiveLinks = 7/>
		<#assign numberOfSideLinks = 3/>
		
		<#if (thisPage > 1)>
			<#assign start = (thisPage - 2) * recordsPerPage/>
			<a href="${contextPath}/${moduleName}/${action}/${start}<#if (id > -1)>/${id}</#if>${extension}">${I18n.getMessage("previous")}</a>&nbsp;
		</#if>

		<#if (totalPages > numberOfActiveLinks)>
			<#if (thisPage <= numberOfSideLinks)>
				<#assign startPageAt = 1/>
				<#assign stopPageAt = numberOfActiveLinks/>
			<#else>
				<#assign startPageAt = (thisPage - numberOfSideLinks) >
				<#if (thisPage >= (totalPages - numberOfSideLinks)) >
					<#assign startPageAt = (totalPages - (numberOfActiveLinks - 1))/>
				</#if>
				
				<#assign stopPageAt = startPageAt + (numberOfActiveLinks - 1)/>
			</#if>

			<#if (startPageAt > 1) >
				...
			</#if>
						
			<#list startPageAt .. stopPageAt as page >
				<#assign start = recordsPerPage * (page-1) >

				<#if thisPage == page >
					${page}
				<#else>
					<a href="${contextPath}/${moduleName}/${action}/${start}<#if (id > -1)>/${id}</#if>${extension}">${page}</a>
				</#if>
				<#if (page < totalPages) >,</#if>
			</#list>
			
			<#if (stopPageAt < totalPages)>
				...
			</#if>
		<#else>
			<#list 1 .. totalPages as page >
				<#assign start = recordsPerPage * (page - 1)/>

				<#if thisPage == page>
					${page}
				<#else>
					<a href="${contextPath}/${moduleName}/${action}/${start}<#if (id > -1)>/${id}</#if>${extension}">${page}</a>
				</#if>
				<#if (page < totalPages) >,</#if>
			</#list>
		</#if>

		<#if thisPage < totalPages >
			<#assign start = thisPage * recordsPerPage/>
			&nbsp;<a href="${contextPath}/${moduleName}/${action}/${start}<#if (id > -1)>/${id}</#if>${extension}">${I18n.getMessage("next")}</a>
		</#if>
		</b>
	</span>
	</#if>
</#macro>

<#macro doGradeUserPagination action presUserId id=-1>
	<#if (totalRecords > recordsPerPage)>
		<span class="gensmall"><b>
		${I18n.getMessage("goToPage")}:
		
		<#assign numberOfActiveLinks = 7/>
		<#assign numberOfSideLinks = 3/>
		
		<#if (thisPage > 1)>
			<#assign start = (thisPage - 2) * recordsPerPage/>
			<a href="${contextPath}/${moduleName}/${action}/<#if (id > -1)>${id}</#if>/${presUserId}/${start}${extension}">${I18n.getMessage("previousUser")}</a>&nbsp;
		</#if>
		
		<#if (totalPages > numberOfActiveLinks)>
			<#if (thisPage <= numberOfSideLinks)>
				<#assign startPageAt = 1/>
				<#assign stopPageAt = numberOfActiveLinks/>
			<#else>
				<#assign startPageAt = (thisPage - numberOfSideLinks) >
				<#if (thisPage >= (totalPages - numberOfSideLinks)) >
					<#assign startPageAt = (totalPages - (numberOfActiveLinks - 1))/>
				</#if>
				
				<#assign stopPageAt = startPageAt + (numberOfActiveLinks - 1)/>
			</#if>

			<#if (startPageAt > 1) >
				...
			</#if>
			
			<#list startPageAt .. stopPageAt as page >
				<#assign start = recordsPerPage * (page-1) >

				<#if thisPage == page >
					${page}
				<#else>
					<a href="${contextPath}/${moduleName}/${action}/<#if (id > -1)>${id}</#if>/${presUserId}/${start}${extension}">${page}</a>
				</#if>
				<#if (page < totalPages) >,</#if>
			</#list>
			
			<#if (stopPageAt < totalPages)>
				...
			</#if>
		<#else>
			<#list 1 .. totalPages as page >
				<#assign start = recordsPerPage * (page - 1)/>

				<#if thisPage == page>
					${page}
				<#else>
					<a href="${contextPath}/${moduleName}/${action}/<#if (id > -1)>${id}</#if>/${presUserId}/${start}${extension}">${page}</a>
				</#if>
				<#if (page < totalPages) >,</#if>
			</#list>
		</#if>
		
		<#if thisPage < totalPages >
			<#assign start = thisPage * recordsPerPage/>
			&nbsp;<a href="${contextPath}/${moduleName}/${action}/<#if (id > -1)>${id}</#if>/${presUserId}/${start}${extension}">${I18n.getMessage("nextUser")}</a>
		</#if>
		</b>
	</#if>
</#macro>