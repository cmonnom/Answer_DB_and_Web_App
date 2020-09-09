Vue.config.devtools = true;
// Vue.use(Vuetable);
Vue.use(VueRouter)
Vue.use(Vuetify)
Vue.use(Vuex)

/**
 * This variable can be use for behavior specific to index.jsp
 * and not applicable to the rest of the website.
 */
var isHomepage = false;

var bus = new Vue();