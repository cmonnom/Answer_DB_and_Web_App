<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
	<%@ taglib prefix='c' uri='http://java.sun.com/jsp/jstl/core' %>
		<%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0);
//prevents caching at the proxy server
%>
			<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
			<html>
			<c:choose>
				<c:when test="${isAllowed}">

					<head>
						<title>Bam Viewer</title>
						<link rel="stylesheet" type="text/css" href="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/themes/smoothness/jquery-ui.css"
						/>
						<!-- Font Awesome CSS -->
						<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css"
						/>
						<link rel="stylesheet" type="text/css" href="https://igv.org/web/release/1.0.9/igv-1.0.9.css">
						<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
						<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.11.2/jquery-ui.min.js"></script>
						<script type="text/javascript" src="https://igv.org/web/release/1.0.9/igv-1.0.9.js"></script>
					</head>

					<body>
						<script>
							var urlRoot = "${pageContext.request.contextPath}" + "/resources/bams/";
							var locus = "${locus}";
							var bam = "${bam}";
							var bai = "${bai}";
							var label = "${label}";
						</script>
						<div id="igv-div">

						</div>
					</body>
				</c:when>
				<c:otherwise>
					<script>window.location = "${pageContext.request.contextPath}/login?urlRedirect=${urlRedirect}"</script>
				</c:otherwise>
			</c:choose>
			<script src="${pageContext.request.contextPath}/resources/js/bam-viewer.js?timestamp=${timestamp}"></script>

			</html>