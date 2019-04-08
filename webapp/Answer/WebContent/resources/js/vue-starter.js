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
			component: () => new Promise(
				resolve => resolve(Home)),
			meta: {
				title: 'Home'
			}
		},
		{
			path: webAppRoot + '/openCase/:id',
			name: 'OpenCase',
			component: () => new Promise(
				resolve => resolve(OpenCase)),
			// component: OpenCase,
			meta: {
				title: 'Open Case '
			},
			props: { readonly: false}
		},
		{
			path: webAppRoot + '/openCaseReadOnly/:id',
			name: 'OpenCase',
			component: () => new Promise(
				resolve => resolve(OpenCase)),
			// component: OpenCase,
			meta: {
				title: 'Open Case '
			},
			props: { readonly: true}
		},
		{
			path: webAppRoot + '/openReport/:id',
			name: 'OpenReport',
			component: () => new Promise(
				resolve => resolve(OpenReport)),
			// component: OpenReport,
			meta: {
				title: 'Open Report '
			},
			props: { readonly: false}
		},
		{
			path: webAppRoot + '/openReportReadOnly/:id',
			name: 'OpenReport',
			component: () => new Promise(
				resolve => resolve(OpenReport)),
			// component: OpenReport,
			meta: {
				title: 'Open Report '
			},
			props: { readonly: true}
		},
		{
			path: webAppRoot + '/annotationBrowser',
			name: 'AnnotationBrowser',
			component: () => new Promise(
				resolve => resolve(AnnotationBrowser)),
			// component: AnnotationBrowser,
			meta: {
				title: 'Annotation Browser'
			}
		},
		{
			path: webAppRoot + '/admin',
			name: 'Admin',
			component: () => new Promise(
				resolve => resolve(Admin)),
			// component: Admin,
			meta: {
				title: 'Admin'
			}
		},
		{
			path: webAppRoot + '/userPrefs',
			name: 'UserPrefs',
			component: () => new Promise(
				resolve => resolve(UserPrefs)),
			// component: UserPrefs,
			meta: {
				title: 'User Preferences'
			}
		},
		{
			path: webAppRoot + '/logout',
			name: 'LogOut',
			component: () => new Promise(
				resolve => resolve(LogOut)),
			// component: LogOut,
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

