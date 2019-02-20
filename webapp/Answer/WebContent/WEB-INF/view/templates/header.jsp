<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="Description" content="Answer is an Annotation Software for Electronic Reporting, 
Authors Dr. B. Cantarel, Dr J. Gagan, Benjamin Wakeland and Guillaume Jimenez
 at the University of Texas Southwestern Medical Center">
<script>
        const webAppRoot = "${pageContext.request.contextPath}";
        const permissions = {
        		canView: "${permissions.canView}" === 'true',
        		canAnnotate:  "${permissions.canAnnotate}" === 'true',
        		canSelect:  "${permissions.canSelect}" === 'true',
                        canAssign:  "${permissions.canAssign}" === 'true',
                        canReview: "${permissions.canReview}" === 'true',
        		admin: "${permissions.admin}" === 'true'
        };
        const isAdmin = permissions.admin === true;
        const isProduction = "${isProduction}";
        		
        const csrf = { paramName: "${_csrf.parameterName}",
        value: "${_csrf.token}",
        csrf:"${_csrf}" }

        const userFullName = "${userFullName}";

        const oncoKBGeniePortalUrl = "${oncoKBGeniePortalUrl}";
        const oncotreeIconUrl = webAppRoot + "/resources/images/oncotree.ico";

</script>

<link rel="stylesheet"  href="https://fonts.googleapis.com/css?family=Open+Sans:400,400i,600,700">
<link rel="stylesheet"  href="https://fonts.googleapis.com/icon?family=Material+Icons">
<link rel="stylesheet"  href="https://use.fontawesome.com/releases/v5.0.13/css/all.css" integrity="sha384-DNOHZ68U8hZfKXOrtjWvjxusGo9WQnrNx2sqG0tfsghAvtVlRW3tvkXWZh58N9jp" crossorigin="anonymous">
<link rel="stylesheet" href="https://cdn.materialdesignicons.com/2.4.85/css/materialdesignicons.min.css">
<link rel="stylesheet" href="https://unpkg.com/vuetify@1.0.19/dist/vuetify.min.css" >

<link rel="preload" as="style" onload="this.onload=null;this.rel='stylesheet'" href="${pageContext.request.contextPath}/resources/css/main.css?timestamp=${timestamp}">
<link rel="preload" as="style" onload="this.onload=null;this.rel='stylesheet'" href="${pageContext.request.contextPath}/resources/css/goodies.css?timestamp=${timestamp}">

<!-- <link rel="shortcut icon" type="image/x-icon" href="https://www.utsouthwestern.net/favicon.ico" /> -->
<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/images/answer-logo-icon-xsmall.png" />

<script src="${pageContext.request.contextPath}/resources/js/cssrelpreload.js?timestamp=${timestamp}"></script>

<c:choose>
<c:when test="${isProduction == 'false'}">
   <script src="https://unpkg.com/vue/dist/vue.js"></script>
</c:when>
<c:otherwise>
   <script src="https://unpkg.com/vue/dist/vue.min.js"></script>
</c:otherwise>
</c:choose>

<script src="https://cdnjs.cloudflare.com/ajax/libs/Sortable/1.6.0/Sortable.min.js"></script> 
<script src="https://cdnjs.cloudflare.com/ajax/libs/Vue.Draggable/2.14.1/vuedraggable.min.js"></script>
<script src="https://unpkg.com/vue-router/dist/vue-router.js"></script>

<script src="https://unpkg.com/vuetify@1.0.19/dist/vuetify.min.js"></script>

<script src="https://unpkg.com/axios/dist/axios.min.js"></script>
<!-- <script src="https://unpkg.com/vue-upload-component"></script> -->

 <script
  src="https://code.jquery.com/jquery-3.2.1.min.js"
  integrity="sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4="
  crossorigin="anonymous"></script>

<script src="${pageContext.request.contextPath}/resources/js/header.js?timestamp=${timestamp}"></script>
<script src="${pageContext.request.contextPath}/resources/js/components/login.js?timestamp=${timestamp}"></script>

<!-- <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.7.1/Chart.min.js"></script>
<script src="https://unpkg.com/vue-chartjs/dist/vue-chartjs.min.js"></script> -->

<script async src="https://cdn.zingchart.com/zingchart.min.js"></script>
<!-- <script src="//cdnjs.cloudflare.com/ajax/libs/dygraph/2.1.0/dygraph.min.js"></script>
<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/dygraph/2.1.0/dygraph.min.css" />
<script src="https://cdnjs.cloudflare.com/ajax/libs/vis/4.21.0/vis.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/vis/4.21.0/vis.min.css" /> -->

<c:forEach var = "goodieFile" items="${goodiesFiles}">
		<script async src="${pageContext.request.contextPath}/resources/js/goodies/${goodieFile}?timestamp=${timestamp}"></script>
</c:forEach>
<c:forEach var = "componentFile" items="${componentFiles}">
		<script src="${pageContext.request.contextPath}/resources/js/components/${componentFile}?timestamp=${timestamp}"></script>
</c:forEach>
<c:forEach var = "jsFile" items="${jsFiles}">
		<script src="${pageContext.request.contextPath}/resources/js/${jsFile}?timestamp=${timestamp}"></script>
</c:forEach>


