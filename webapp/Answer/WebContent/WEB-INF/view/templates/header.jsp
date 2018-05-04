<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<script>
        const webAppRoot = "${pageContext.request.contextPath}";
        const isAdmin = "${isAdmin}" === 'true';
</script>

<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Open+Sans:400,400i,600,700">
<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
<link rel="stylesheet" href="https://cdn.materialdesignicons.com/2.1.19/css/materialdesignicons.min.css">
<link href="https://unpkg.com/vuetify/dist/vuetify.min.css" rel="stylesheet">

<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/main.css?timestamp=${timestamp}">


<link rel="shortcut icon" type="image/x-icon" href="https://www.utsouthwestern.net/favicon.ico" />


<script src="https://unpkg.com/vue/dist/vue.js"></script>

<script src="//cdnjs.cloudflare.com/ajax/libs/Sortable/1.6.0/Sortable.min.js"></script> 
<script src="//cdnjs.cloudflare.com/ajax/libs/Vue.Draggable/2.14.1/vuedraggable.min.js"></script>
<script src="https://unpkg.com/vue-router/dist/vue-router.js"></script>

<script src="https://unpkg.com/vuetify/dist/vuetify.min.js"></script>

<script src="https://unpkg.com/axios/dist/axios.min.js"></script>
<script src="https://unpkg.com/vue-upload-component"></script>

 <script
  src="https://code.jquery.com/jquery-3.2.1.min.js"
  integrity="sha256-hwg4gsxgFZhOsEEamdOYGBf13FyQuiTwlAQgxVSNgt4="
  crossorigin="anonymous"></script>

<script src="${pageContext.request.contextPath}/resources/js/header.js?timestamp=${timestamp}"></script>
<script src="${pageContext.request.contextPath}/resources/js/components/login.js?timestamp=${timestamp}"></script>
<%-- <script src="${pageContext.request.contextPath}/resources/js/components/data-table.js"></script> --%>

<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.7.1/Chart.min.js"></script>
<script src="https://unpkg.com/vue-chartjs/dist/vue-chartjs.min.js"></script>

<script src="https://cdn.zingchart.com/zingchart.min.js"></script>

<c:forEach var = "componentFile" items="${componentFiles}">
		<script src="${pageContext.request.contextPath}/resources/js/components/${componentFile}?timestamp=${timestamp}"></script>
</c:forEach>
<c:forEach var = "jsFile" items="${jsFiles}">
		<script src="${pageContext.request.contextPath}/resources/js/${jsFile}?timestamp=${timestamp}"></script>
</c:forEach>


