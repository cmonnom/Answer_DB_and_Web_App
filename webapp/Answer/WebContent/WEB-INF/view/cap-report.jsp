<script src="${pageContext.request.contextPath}/resources/js/subjects.js"></script>
</head>

<div id="app">
	<nav class="navbar" role="navigation" aria-label="main navigation">
		<div class="navbar-brand">
			<a class="navbar-item" href="https://bulma.io">
				<img src="https://bulma.io/images/bulma-logo.png" alt="Bulma: a modern CSS framework based on Flexbox" width="112" height="28">
			</a>

			
		</div>
	</nav>

	<div class="ui">
		<h2 class="title">
			Cap Report
		</h2>

		<div class="columns">
			<label class="column is-2 v-centered">Move columns:</label>
			<div class="column">
				<draggable :list="fields" @end="onMoveEnd">

					<span v-for="header in fields" class="button ">
						{{ header.title }}
					</span>
				</draggable>
			</div>
		</div>

		<vuetable ref="vuetable" api-url="./getSubjectsAjax" :fields="fields" :css="css.table" pagination-path="pagination" :per-page="10"
		 :sort-order="sortOrder" @vuetable:pagination-data="onPaginationData" :multi-sort="true" @vuetable:loading="onLoading" @vuetable:loaded="onLoaded">
		</vuetable>

		<div class="vuetable-pagination ui basic segment grid">
			<vuetable-pagination-info class="" ref="paginationInfoBottom"></vuetable-pagination-info>
			<vuetable-pagination class="v-centered" ref="paginationBottom" :css="css.pagination" @vuetable-pagination:change-page="onChangePage"></vuetable-pagination>
		</div>
	</div>

</div>