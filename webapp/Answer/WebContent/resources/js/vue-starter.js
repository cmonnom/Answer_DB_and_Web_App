const router = new VueRouter({
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
				resolve => resolve(OpenCase2)),
			// component: OpenCase,
			meta: {
				title: 'Open Case '
			},
			props: { readonly: false}
		},
		{
			path: webAppRoot + '/openCaseReadOnly/:id',
			name: 'OpenCaseReadOnly',
			component: () => new Promise(
				resolve => resolve(OpenCase2)),
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
			name: 'OpenReportReadOnly',
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
		},
		{
			path: webAppRoot + '/sandbox',
			name: 'Sandbox',
			component: () => new Promise(
				resolve => resolve(Sandbox)),
			meta: {
				title: 'Sandbox'
			}
		},
		{
			path: webAppRoot + '/discovar',
			name: 'LookupTool',
			component: () => new Promise(
				resolve => resolve(LookupTool)),
			meta: {
				title: 'LookupTool'
			}
		},
		]
});

router.beforeEach((to, from, next) => {
	var samepage = (to.name == from.name) && (to.params.id == from.params.id);
	if (samepage) {
		next();
	}
	else if (store.getters.isOpenCaseSaveNeeded && !samepage) {
		const answer = window.confirm('Some work has not been saved. Are you sure you want to leave?');
		if (answer) {
			store.commit("resetAll");
			updateTitle(to, from);
			next();
		}
		else {
			next(false);
		}    
	}
	else {
		store.commit("resetAll");
		updateTitle(to, from);
		next();
	}
});

function updateTitle(to, from) {
	document.title = 'Answer ' + to.meta.title + (to.params.id ? to.params.id : '');
	if (to.path.indexOf("discovar") > -1) {
		document.title = document.title + buildLookupParamsTitle(to.query);
	}
}

const store = new Vuex.Store( {
	modules: {
		snpStore: snpStoreModule,
		cnvStore: cnvStoreModule,
		ftlStore: ftlStoreModule,
		virStore: virStoreModule,
		annotationStore: annotationStoreModule,
		variantStore: variantStoreModule
	},
	state: {
		openCaseSaveNeeded: false
	},
	getters: {
		isOpenCaseSaveNeeded: (state) => {
			return state.openCaseSaveNeeded;
		}
	},
	mutations: {
		updateOpenCaseSaveNeeded: (state, isSaveNeeded) => {
			state.openCaseSaveNeeded = isSaveNeeded;
		},
		resetAll: (state, currentVariantId) => {
			state.openCaseSaveNeeded = false;
			store.commit("snpStore/resetAll");
			store.commit("cnvStore/resetAll");
			store.commit("ftlStore/resetAll");
			store.commit("virStore/resetAll");
			store.commit("annotationStore/clearAfterSaving", currentVariantId);
			store.commit("variantStore/clearAfterSaving");
		}
	}
})

const vueApp = new Vue({
	router,
	el: '#app',
	store: store,
	mounted() {
		this.$vuetify.theme.primary = '#4db6ac'; //set the theme here
		this.$vuetify.theme.warning = "#FFD740";
	}
})

function buildLookupParamsTitle(query) {
	switch(query.button) {
		case "Gene": return " Gene " + query.gene;
		case "Cancer": return " Cancer " + query.oncotree;
		case "Variant": return " Variant " + query.gene + " " + query.variant + " " + query.oncotree;
		case "CNV": var ampDel = query.ampDel ? "Amplification" : "Deletion"; return " CNV " + query.gene + " " + query.oncotree + " " + ampDel;
		case "Fusion": return " Fusion " + query.five + "-" + query.three + " " + query.oncotree;
		default: return "";
	}
}