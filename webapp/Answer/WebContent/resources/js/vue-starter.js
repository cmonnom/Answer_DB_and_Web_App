var router = new VueRouter({
	mode: 'history',
	routes: [
		{
			path: webAppRoot + '/login',
			name: 'Login',
			// component: Login,
			meta: {
				title: 'Login'
			}
		},
		{
			path: webAppRoot + '/home',
			name: 'Home',
			component: Home,
			meta: {
				title: 'Home'
			}
		},
		{
			path: webAppRoot + '/openCase/:id',
			name: 'OpenCase',
			component: OpenCase,
			meta: {
				title: 'OpenCase Id: '
			},
			props: { readonly: false}
		},
		{
			path: webAppRoot + '/openCaseReadOnly/:id',
			name: 'OpenCase',
			component: OpenCase,
			meta: {
				title: 'OpenCase Id: '
			},
			props: { readonly: true}
		},
		{
			path: webAppRoot + '/openReport/:id',
			name: 'OpenReport',
			component: OpenReport,
			meta: {
				title: 'OpenReport Id: '
			},
			props: { readonly: false}
		},
		{
			path: webAppRoot + '/openReportReadOnly/:id',
			name: 'OpenReport',
			component: OpenReport,
			meta: {
				title: 'OpenReport Id: '
			},
			props: { readonly: true}
		},
		{
			path: webAppRoot + '/annotationBrowser',
			name: 'AnnotationBrowser',
			component: AnnotationBrowser,
			meta: {
				title: 'Annotation Browser'
			}
		},
		{
			path: webAppRoot + '/admin',
			name: 'Admin',
			component: Admin,
			meta: {
				title: 'Admin'
			}
		},
		{
			path: webAppRoot + '/logout',
			name: 'LogOut',
			component: LogOut,
			meta: {
				title: 'Log Out'
			}
		}
		]
});

router.beforeEach((to, from, next) => {
		document.title = 'Answer ' + to.meta.title + (to.params.id ? to.params.id : '');
		next();
});


new Vue({
	router,
	el: '#app',
	mounted() {
		this.$vuetify.theme.primary = '#4db6ac'; //set the theme here
	}
})

