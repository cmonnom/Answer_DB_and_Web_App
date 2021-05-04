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
								<title>Bam Viewer</title>
								<meta name="viewport" content="width=device-width, initial-scale=1">
								<meta name="Description" content="Answer is an Annotation Software for Electronic Reporting, 
		Authors Dr. B. Cantarel, Dr J. Gagan, Benjamin Wakeland and Guillaume Jimenez
		 at the University of Texas Southwestern Medical Center">
								<!-- Font Awesome CSS -->
								<link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.2.0/css/font-awesome.min.css"
								/>
								
								<!-- <script type="text/javascript" src="https://cdn.jsdelivr.net/npm/igv@2.2.5/dist/igv.min.js"></script> -->
								<!-- <script src="https://cdn.jsdelivr.net/npm/igv@2.4.0/dist/igv.min.js" integrity="sha384-BzBUWTCtRcjC4nWXiyKFbrzTtsl2VbdZQneyaQtiRKOaeycZbQKYixzQccsVGAOm" crossorigin="anonymous"></script> -->
								<script src="https://cdn.jsdelivr.net/npm/igv@2.7.9/dist/igv.min.js" integrity="sha384-dBf2VcAVtlmmfDF2dPZG5KUSh0M80qQetkbNWg+Tw04Ffjbn3yTvMXNXgLKGF6Lf" crossorigin="anonymous"></script>
							</head>
		
							<body>
								<script>
									var urlRoot = "${pageContext.request.contextPath}" + "/bams/";
									if ("${storageType}" != "local") {
										urlRoot = ""; //url is fully contained in the filename for cloud storage
									}
									var locus = "${locus}";
									var caseId = "${caseId}";
									var normalBam = "${normalBam}";
									var normalBai = "${normalBai}";
									var normalLabel = "${normalLabel}";
									var tumorBam = "${tumorBam}";
									var tumorBai = "${tumorBai}";
									var tumorLabel = "${tumorLabel}";
									var rnaBam = "${rnaBam}";
									var rnaBai = "${rnaBai}";
									var rnaLabel = "${rnaLabel}";
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