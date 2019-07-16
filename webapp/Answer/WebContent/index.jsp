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
<title>Answer</title>
<%@include file="/WEB-INF/view/templates/header.jsp" %>
<script src="${pageContext.request.contextPath}/resources/js/components/login-full-page.js"></script>

<!-- Just for the launch of the final version -->
<script src="${pageContext.request.contextPath}/resources/js/goodies/sketch.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/components/goodies2.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/goodies/fireworks.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/components/azure-oauth.js"></script>

</head>
<body>
<div id="app">
<v-app >
		<main>
			<v-content>
				<v-container>
					<!-- 		ADDING PAGE SPECIFIC CONTENT HERE BY POPULATING ${content} with the name of the jsp file
	from the controller-->
					<login-full-page :popup="false"></login-full-page>
				</v-container>	
			</v-content>
		</main>
	</v-app>
</div>
</body>
<script>
	var router = new VueRouter({
    mode: 'history',
    routes: [
    ]
});
	new Vue({
		router,
 	el: '#app',

  
})

</script>
</html>