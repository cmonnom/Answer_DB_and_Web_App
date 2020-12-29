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
                        canHide: "${permissions.canHide}" === 'true',
        		admin: "${permissions.admin}" === 'true'
        };
        const isAdmin = permissions.admin === true;
        const isProduction = "${isProduction}";
        		
        const csrf = { paramName: "${_csrf.parameterName}",
        value: "${_csrf.token}",
        csrf:"${_csrf}" }

        const userFullName = "${userFullName}";
        const callingName = (permissions.canReview ? "Dr. " + "${lastName}" : "${firstName}");

        const oncoKBGeniePortalUrl = "${oncoKBGeniePortalUrl}";
        const oncotreeIconUrl = webAppRoot + "/resources/images/oncotree_256.ico";
        const lastLogin = "${lastLogin}";
        var showLastLogin = "${showLastLogin}" === 'true';

        var authMessage = "${authMessage}";
        var authType = "${authType}";

        var defaultHomeTab = "${prefs.homeTab}";

        var version = "${version}";

</script>

<link rel="stylesheet" rel="preload" as="style" onload="this.onload=null;this.rel='stylesheet'" href="https://fonts.googleapis.com/css?family=Open+Sans:400,400i,600,700">
<link rel="stylesheet" rel="preload" as="style" onload="this.onload=null;this.rel='stylesheet'" href="https://fonts.googleapis.com/icon?family=Material+Icons">
<link rel="stylesheet" rel="preload" as="style" onload="this.onload=null;this.rel='stylesheet'" href="https://use.fontawesome.com/releases/v5.0.13/css/all.css" integrity="sha384-DNOHZ68U8hZfKXOrtjWvjxusGo9WQnrNx2sqG0tfsghAvtVlRW3tvkXWZh58N9jp" crossorigin="anonymous">
<link rel="stylesheet" rel="preload" as="style" onload="this.onload=null;this.rel='stylesheet'" href="https://cdnjs.cloudflare.com/ajax/libs/MaterialDesign-Webfont/5.8.55/css/materialdesignicons.min.css" integrity="sha512-x96qcyADhiw/CZY7QLOo7dB8i/REOEHZDhNfoDuJlyQ+yZzhdy91eAa4EkO7g3egt8obvLeJPoUKEKu5C5JYjA=="  crossorigin="anonymous">
<!-- <link rel="stylesheet" href="https://unpkg.com/vuetify@1.0.19/dist/vuetify.min.css" > -->
<link href="https://unpkg.com/vuetify@1.5.17/dist/vuetify.min.css" rel="stylesheet">

<link rel="preload" as="style" onload="this.onload=null;this.rel='stylesheet'" href="${pageContext.request.contextPath}/resources/css/main.css?timestamp=${timestamp}">
<link rel="preload" as="style" onload="this.onload=null;this.rel='stylesheet'" href="${pageContext.request.contextPath}/resources/css/goodies.css?timestamp=${timestamp}">

<!-- <link rel="shortcut icon" type="image/x-icon" href="https://www.utsouthwestern.net/favicon.ico" /> -->
<link rel="shortcut icon" type="image/x-icon" href="${pageContext.request.contextPath}/resources/images/answer-logo-icon-xsmall.png" />

<script src="${pageContext.request.contextPath}/resources/js/cssrelpreload.js?timestamp=${timestamp}"></script>

<c:choose>
<c:when test="${isProduction == 'false'}">
         <script src="https://unpkg.com/vue@2.6.8/dist/vue.js" integrity="sha384-YzY3KQhnjdhKuuhBSraSy+kHLJuhBO3BGqmWfr6JXg98Ns4bAdTkHmtiNZT4mT1S" crossorigin="anonymous"></script>
         <script src="https://unpkg.com/vuex@3.5.1/dist/vuex.js" integrity="sha384-+O5ZSPkN6TGXt9PxjU2EBQjmscj6Qt5ki7uytTSfvQ5rbd2ZNjHGFX+Zj+GmsCBZ" crossorigin="anonymous"></script>
</c:when>
<c:otherwise>
        <script src="https://unpkg.com/vue@2.6.8/dist/vue.min.js" integrity="sha384-tJs0vlPyuG7XcysSkZ0VtWsM/uztJkUtpLwb6HzV5NSgSwGqdW61IBOBJDhbUf+K" crossorigin="anonymous"></script>
        <script src="https://unpkg.com/vuex@3.5.1/dist/vuex.min.js" integrity="sha384-LiIJNOpyvdXELIx0yBiazgrvDGSE2p8aHU27i55lTzWC42cNiFqDNWIlU8ocTyWI" crossorigin="anonymous"></script>
</c:otherwise>
</c:choose>

<script src="https://cdnjs.cloudflare.com/ajax/libs/Sortable/1.8.4/Sortable.min.js" integrity="sha256-yEySJXdfoPg1V6xPh7TjRM0MRZnJCnIxsoBEp50u0as=" crossorigin="anonymous"></script> 
<script src="https://cdnjs.cloudflare.com/ajax/libs/Vue.Draggable/15.0.0/vuedraggable.min.js" integrity="sha256-kZmY4LUwE53ceTqZXPto8gMELhExZWBrmzOCZgzyY0Y=" crossorigin="anonymous"></script>
<script src="https://unpkg.com/vue-router@3.0.2/dist/vue-router.min.js" integrity="sha384-EnzMJi5PirMz1dgUr9a4DUHwg7e71+fxk3jLLfJn1vljGydxX8c6bdDYdPDE8224" crossorigin="anonymous"></script>

<!-- <script src="https://unpkg.com/vuetify@1.0.19/dist/vuetify.min.js" integrity="sha384-/tuDalDXfn0/mqH+c+VTK46EiVxKo0Vs3dSRyRLD7jaC1E1t+fhXsB3sB0m+jT5V" crossorigin="anonymous"></script> -->
<!-- <script src="https://unpkg.com/vuetify@1.5.16/dist/vuetify.min.js" integrity="sha384-EYihXFVPjgT0rPZrN9wqg1D2+yJdATdXk8Wt9qVURsaUIEWSYO9beSiTJY9okdEZ" crossorigin="anonymous"></script> -->
<script src="https://unpkg.com/vuetify@1.5.17/dist/vuetify.min.js" integrity="sha384-a/ZvnkJ3DbfDdfKPYp3euckkAtJXQBVIhyafc4J8/eVD3nBrkX4XVIwrXPsnr4t3" crossorigin="anonymous"></script>

<script src="https://unpkg.com/axios@0.18.0/dist/axios.min.js" integrity="sha384-U/+EF1mNzvy5eahP9DeB32duTkAmXrePwnRWtuSh1C/bHHhyR1KZCr/aGZBkctpY" crossorigin="anonymous"></script>
<!-- <script src="https://unpkg.com/vue-upload-component"></script> -->
<!-- <script src="https://code.jquery.com/jquery-3.5.1.min.js" integrity="sha384-ZvpUoO/+PpLXR1lu4jmpXWu80pZlYUAfxl5NsBMWOEPSjUn/6Z/hRTt8+pR6L4N2" crossorigin="anonymous"></script> -->
<script src="${pageContext.request.contextPath}/resources/js/header.js?timestamp=${timestamp}"></script>
<script src="${pageContext.request.contextPath}/resources/js/components/login.js?timestamp=${timestamp}"></script>
<c:choose>
<c:when test="${authType == 'azure_oauth'}">
        <script src="https://secure.aadcdn.microsoftonline-p.com/lib/1.0.2/js/msal.js"></script>

</c:when>          
</c:choose>


<!-- <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/3.1.9-1/crypto-js.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/3.1.9-1/hmac-sha256.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/3.1.9-1/enc-base64.min.js"></script> -->

<!-- <script src="//cdnjs.cloudflare.com/ajax/libs/dygraph/2.1.0/dygraph.min.js"></script>
<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/dygraph/2.1.0/dygraph.min.css" />
<script src="https://cdnjs.cloudflare.com/ajax/libs/vis/4.21.0/vis.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/vis/4.21.0/vis.min.css" /> -->

<c:forEach var = "storeFile" items="${storesFiles}">
	<script src="${pageContext.request.contextPath}/resources/js/stores/${storeFile}?timestamp=${timestamp}"></script>
</c:forEach>

<c:forEach var = "goodieFile" items="${goodiesFiles}">
	<script async src="${pageContext.request.contextPath}/resources/js/goodies/${goodieFile}?timestamp=${timestamp}"></script>
</c:forEach>
<c:forEach var = "componentFile" items="${componentFiles}">
	<script src="${pageContext.request.contextPath}/resources/js/components/${componentFile}?timestamp=${timestamp}"></script>
</c:forEach>
<c:forEach var = "jsFile" items="${jsFiles}">
	<script src="${pageContext.request.contextPath}/resources/js/${jsFile}?timestamp=${timestamp}"></script>
</c:forEach>


