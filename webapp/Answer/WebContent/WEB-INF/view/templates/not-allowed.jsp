<!-- This is the main template from which all pages should be build upon. -->
<!-- The controller for the page should add "content" to the model with the name -->
<!-- of the jsp file (no extension) that has the actual page content. -->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
pageEncoding="ISO-8859-1"%>
<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>
<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0);
//prevents caching at the proxy server
%>    
<!DOCTYPE html>
<html lang="en">
<head>
<title>Answer Error</title>
<%@include file="/WEB-INF/view/templates/header.jsp"%>

</head>
<body>
<div id="app">
<v-app >
		<main>
			<v-content>
				<v-container>
					<!-- 		ADDING PAGE SPECIFIC CONTENT HERE BY POPULATING ${content} with the name of the jsp file
	from the controller-->
					You are not allowed to access this page.
					<c:choose>
						<c:when test="${not empty redirectReadOnlyUrl}">
					<span><br/>You will be redirected to the read only page in 3 seconds instead.</span>
				</c:when>
			</c:choose>
				</v-container>	
			</v-content>
		</main>
	</v-app>
</div>
</body>
<!-- Start the Vue application -->
<script src="${pageContext.request.contextPath}/resources/js/vue-starter.js"></script>

<script>
	const redirectReadOnlyUrl = "${redirectReadOnlyUrl}";
	if (redirectReadOnlyUrl) {
		setTimeout(() => {window.location = "${pageContext.request.contextPath}/login?urlRedirect=${redirectReadOnlyUrl}"}, 3000);
	}
</script>

</html>