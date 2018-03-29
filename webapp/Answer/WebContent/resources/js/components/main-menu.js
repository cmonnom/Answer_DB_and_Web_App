//Main left side menu. main-template includes this component
Vue.component('main-menu', {
	props: {
		baseUrl: {type: String, default: webAppRoot}
	},
	template: `<v-navigation-drawer permanent app width="200" :mini-variant.sync="isMinied">
	<v-toolbar flat dense>
	<span :class="['text-xs-center', isMinied ? 'caption' : 'headline', isMinied ? '' : 'pl-4', 'grey--text', 'text--darken-2']">Clear NGS</span>
	</v-toolbar> 
	<v-toolbar flat>
		<img :src="baseUrl + '/resources/images/utsw-master-logo-lg.png'" alt="UTSW" width="100%" class="ml-0">
		<br/>
		<div>Answer</br></div>
	</v-toolbar>
	<v-divider></v-divider>
	<v-list dense class="pt-0">
		<v-list-tile>
			<v-list-tile-action>
				<v-btn flat icon @click.native.stop="isMinied = !isMinied" active>
					<v-icon :class="isMinied ? 'rotate90': 'rotate270'">eject</v-icon>
				</v-btn>
			</v-list-tile-action>
			<v-list-tile-content>
				<v-list-tile-title class="subheading">
					Hide Menu
				</v-list-tile-title>
			</v-list-tile-content>
		</v-list-tile>
		<v-list-tile v-for="menuItem in menuItems" :disabled="isMinied" :key="menuItem.title" :to="menuItem.skipRoute ? '' : { name: menuItem.name, params: {id: menuItem.id}}">
			<v-list-tile-action v-if="menuItem.iconBefore">
				<v-icon>{{ menuItem.iconBefore }}</v-icon>
			</v-list-tile-action>

			<!-- Main menu item -->
			<v-list-tile-content v-if="menuItem.regularItem">
				<v-list-tile-title class="subheading">
					{{ menuItem.title }}
				</v-list-tile-title>
			</v-list-tile-content>

			<v-list-tile-action v-if="menuItem.iconAfter">
				<!-- order search bar -->
				<v-menu offset-x :close-on-content-click="false">
					<v-btn flat icon slot="activator" color="blue lighten-2" v-show="!isMinied">
						<v-icon>{{ menuItem.iconAfter }}</v-icon>
					</v-btn>

					<!-- <v-card v-if="menuItem.projectOrderSearch">
						<v-select v-on:input="loadProjectOrderDetails" v-bind:items="projectOrders" v-model="projectOrderItemSelected" item-text="name"
						 item-value="value" label="Order Name" single-line solo autocomplete></v-select>
					</v-card> -->

				</v-menu>
			</v-list-tile-action>
		</v-list-tile>

	</v-list>
</v-navigation-drawer>`,
	data() {
		return {
			menuItems: [
				{ title: 'Home', iconBefore: 'home', name: 'Home', regularItem: true  },
				{ title: 'New Case', name: 'NewCase', regularItem: true  },
				// { title: 'Order Details', skipRoute: true, regularItem: true, iconAfter: 'keyboard_arrow_right', projectOrderSearch: true },
				// { title: 'Sample Coverage', name: 'SampleCoverageNoSampleNoChrom', regularItem: true },
				// { title: 'Low Coverage', name: 'LowCoverageBrowser', regularItem: true },
				// { title: 'Seq Run Details', name: 'SeqRunDetailsTable', regularItem: true  },
				// { title: 'Sample Details', name: 'SampleDetailsTable', regularItem: true  },
				// { title: 'Subjects', name: 'Subjects', regularItem: true },
				{ title: 'Logout', name: 'LogOut', iconBefore: 'exit_to_app', regularItem: true }
			],
			// projectOrderItemSelected: null,
			// projectOrders: [],
			// projectOrderUpdateUrl: webAppRoot + "/getProjectOrderItems",
			// sampleUpdateUrl: webAppRoot + "/getSampleItems",
			isMinied: false
		}

	},
	methods: {
		// populateProjectOrders() {
		// 	axios.get(this.projectOrderUpdateUrl, {
        //         params: {}
        //     })
        //         .then(response => {
        //             if (response.data.isAllowed) {
        //                 this.projectOrders = response.data.items;
        //             }
        //             else {
        //                 this.handleDialogs(response);
        //             }
        //         })
        //         .catch(error => {
        //             alert(error);
        //         });
		// },
		// loadProjectOrderDetails() {
		// 	this.$router.push({name:"OrderDetails", params:{id: this.projectOrderItemSelected}});
		// },
        handleDialogs(response) {
            // alert(response.data.reason);
            if (response.data.isXss) {
               console.log("xss detected:" + response.data.reason);
               this.showErrorDialog = true;
           }
           else {
               this.showLoginDialog = true;
           }
       }
	},
	updated: function() {
		//this.populateProjectOrders();
	},
	created: function() {
		// this.populateProjectOrders();
		bus.$on('shrink-menu', () => {
            this.isMinied = true;
        });
	}

	
});