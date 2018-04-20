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
			}
		},
		{
			path: webAppRoot + '/newCase',
			name: 'NewCase',
			component: NewCase,
			meta: {
				title: 'New Case'
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


//TODO test this
new Vue({
	router,
	el: '#app',
	mounted() {
		this.$vuetify.theme.primary = '#4db6ac'; //set the theme here
	}
})

