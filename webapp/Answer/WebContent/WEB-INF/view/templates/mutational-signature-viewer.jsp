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
			<c:choose>
				<c:when test="${isAllowed}">

					<head>
						<title>Mutational Signature Viewer</title>
						<meta name="viewport" content="width=device-width, initial-scale=1">
						<meta name="Description" content="Answer is an Annotation Software for Electronic Reporting, 
Authors Dr. B. Cantarel, Dr J. Gagan, Benjamin Wakeland and Guillaume Jimenez
 at the University of Texas Southwestern Medical Center">
						
					</head>

					<body>
						<script>
							var tumorVcf = "${tumorVcf}";
							var url = "${mutationalSignatureUrl}";
							var sampleName = "${sampleName}";
							window.location=url + "?vcfFile=" + tumorVcf + "&name=" + sampleName;
						</script>
					</body>
				</c:when>
				<c:otherwise>
					<script>window.location = "${pageContext.request.contextPath}/login?urlRedirect=${urlRedirect}"</script>
				</c:otherwise>
			</c:choose>
			</html>