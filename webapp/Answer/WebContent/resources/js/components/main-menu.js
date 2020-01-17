//Main left side menu. main-template includes this component
Vue.component('main-menu', {
	props: {
		baseUrl: { type: String, default: webAppRoot },
		width: { type: Number, default: 200 }
	},
	template: `<v-navigation-drawer app permanent :width="width" :mini-variant.sync="isMinied" mini-variant-width="50" >
	<v-toolbar flat :extended="isMinied ? false : true" style="height:128px" class="menu-image" disable-resize-watcher>
		<img alt="answer logo beta" v-if="isBetaVersion()" :src="getFinalLogoUrl()"  width="100%" :class="['pl-2', 'pr-2', 'pt-5 mt-2']"/>
		<img alt="answer logo" v-if="isVersionOne()" :src="getFinalLogoUrl()" width="100%" :class="['pl-2', 'pr-2', 'pt-5 mt-2']"/>
	</v-toolbar>
	<v-divider></v-divider>
	<v-list dense class="pt-0" :class="isMinied ? 'minied': ''">
		<v-list-tile v-if="displayMenuItem(menuItem)" @click="menuItem.isButton ? menuItem.action : ''" 
		v-for="menuItem in menuItems" :disabled="isMinied" :key="menuItem.title" :to="menuItem.skipRoute ? '' : { name: menuItem.name, params: {id: menuItem.id}}"
		>
			<v-list-tile-action v-if="menuItem.iconBefore && !menuItem.isButton" >
				<v-icon :class="getIconBeforeClass(menuItem)">{{ menuItem.iconBefore }}</v-icon>
			</v-list-tile-action>

			<v-list-tile-action v-if="menuItem.iconBefore && menuItem.isButton">
					<v-icon :class="getIconBeforeClass(menuItem)" @click.stop="menuItem.action">{{ menuItem.iconBefore }}</v-icon>
			</v-list-tile-action>

			<!-- Main menu item -->
			<v-list-tile-content v-if="!menuItem.isButton">
				<v-list-tile-title :class="['subheading', menuItem.activeColor]" >
					<span :class="menuItem.activeColor">{{ menuItem.title }}</span>
				</v-list-tile-title>
			</v-list-tile-content>

			<v-list-tile-content v-if="menuItem.isButton" @click.stop="menuItem.action">
			<v-list-tile-title class="subheading">
				{{ menuItem.title }}
			</v-list-tile-title>
		</v-list-tile-content>

			<v-list-tile-action v-if="menuItem.iconAfter">
				<!-- case search bar -->
				<v-menu offset-x :close-on-content-click="false" @input="updateActiveState($event, menuItem)" v-model="isMenuOpen[menuItem.title]">
					<v-btn flat icon slot="activator" :color="isMenuOpen[menuItem.title] ? 'warning' : 'primary'" v-show="!isMinied">
						<v-icon>{{ menuItem.iconAfter }}</v-icon>
					</v-btn>

					<v-card v-if="menuItem.caseSearch">
						<v-card-text class="pl-2 pr-2">
						<v-switch v-model="allCases" color="primary" label="See all cases" @change="populateCases"></v-switch>
						<v-autocomplete hide-details v-on:input="loadOpenCase" v-bind:items="cases" v-model="caseItemSelected" item-text="name" clearable
						 item-value="value" label="Case ID" single-line  @change="closeOpenMenu(menuItem)"
						 no-data-text="No active cases assigned to you">
						<template v-slot:item="data" >
							<v-icon class="pb-2 pr-3">{{ data.item.iconAvatar }}</v-icon>
							<span>{{ data.item.name }}</span>
						</template>
						</v-autocomplete>
						</v-card-text>
					</v-card>

					<v-card v-if="menuItem.caseReportSearch">
							<v-card-text class="pl-2 pr-2">
								<v-switch v-model="allReports" color="primary" label="See all reports" @change="populateCasesWithReport"></v-switch>
								<v-autocomplete hide-details v-on:input="loadOpenReport" v-bind:items="casesWithReport" v-model="caseReportItemSelected" item-text="name" clearable
								 item-value="value" label="Case ID" single-line  @change="closeOpenMenu(menuItem)"
								 no-data-text="No active reports assigned to you">
								 <template v-slot:item="data">
							<v-icon class="pb-2 pr-3">{{ data.item.iconAvatar }}</v-icon>
							<span>{{ data.item.name }}</span>
						</template>
								 </v-autocomplete>
							</v-card-text>
				</v-card>

				</v-menu>
			</v-list-tile-action>

			
		</v-list-tile>
		<v-slide-x-transition>
		<v-list-tile :class="[statusVisible ? 'menu-status-above' : 'menu-status', 'grey--text']" v-if="userRankVisible">
		<v-list-tile-action v-if="userLeaderBoardInfo.color && userLeaderBoardInfo.icon">
				<v-icon :color="userLeaderBoardInfo.color">{{ userLeaderBoardInfo.icon }}</v-icon>
			</v-list-tile-action>
		<v-list-tile-content>
			<v-list-tile-title class="subheading">
				{{ userLeaderBoardInfo.rankTitle }}
			</v-list-tile-title>
		</v-list-tile-content>
		</v-list-tile>
		</v-slide-x-transition>
		<v-slide-x-transition>
		<v-list-tile class="menu-status grey--text" v-if="statusVisible">
		<v-list-tile-content>
			<v-list-tile-title class="subheading">
				{{ statusMessage }}
			</v-list-tile-title>
		</v-list-tile-content>
		</v-list-tile>
		</v-slide-x-transition>

				<v-btn icon v-if="showXmas()" flat @click="activateXMas">
					<v-icon color="#c54245">mdi-snowman</v-icon>
				</v-btn>

	</v-list>
</v-navigation-drawer>`,
	data() {
		return {
			menuItems: [
				{ title: 'Hide Menu', name: 'HideMenu', regularItem: true, action: this.toggleMinied, skipRoute:true, iconBefore: 'eject', isButton: true, miniedRotation: true },
				{ title: 'Home', iconBefore: 'home', name: 'Home', regularItem: true },
				{ title: 'Open Case', skipRoute: true, regularItem: true, iconAfter: 'keyboard_arrow_right', caseSearch: true, activeColor:"" },
				{ title: 'Open Report', skipRoute: true, regularItem: true, iconAfter: 'keyboard_arrow_right', caseReportSearch: true, activeColor:""  },
				{ title: 'Annotations', name: 'AnnotationBrowser', regularItem: true, iconBefore: 'mdi-message-bulleted' }, // NOT READY YET
				{ title: 'Admin', name: 'Admin', regularItem: true, adminOnly: true, iconBefore: 'settings' },
				{ title: 'Preferences', name: 'UserPrefs', regularItem: true, iconBefore: 'account_circle' },
				{ title: 'Help', name: 'Help', regularItem: true, action: this.openHelp, skipRoute:true, iconBefore: 'mdi-lifebuoy', isButton: true },
				{ title: 'Logout', name: 'LogOut', iconBefore: 'mdi-logout', regularItem: true }
			],
			caseItemSelected: null,
			caseReportItemSelected: null,
			cases: [],
			casesWithReport: [],
			caseUpdateUrl: webAppRoot + "/getCaseItems",
			caseReportUpdateUrl: webAppRoot + "/getCaseReportItems",
			isMinied: false,
			statusVisible: false,
			statusMessage: "",
			userLeaderBoardInfo: {},
			userRankVisible: false,
			versionName: "1.0",
			allCases: false,
			allReports: false,
			isMenuOpen: { 'Open Case': false, 'Open Report': false} //to control open/closing a menu
		}

	},
	methods: {
		populateCases() {
			axios.get(this.caseUpdateUrl, {
				params: {
					allCases: this.allCases
				}
			})
				.then(response => {
					if (response.data.isAllowed) {
						this.cases = response.data.items;
					}
					else {
						this.handleDialogs(response.data, this.populateCases);
					}
				})
				.catch(error => {
					alert(error);
				});
		},
		populateCasesWithReport() {
			axios.get(this.caseReportUpdateUrl, {
				params: {
					allReports: this.allReports
				}
			})
				.then(response => {
					if (response.data.isAllowed) {
						this.casesWithReport = response.data.items;
					}
					else {
						this.handleDialogs(response.data, this.populateCasesWithReport);
					}
				})
				.catch(error => {
					alert(error);
				});
		},
		isUserAssignedToCase() {
			return new Promise((resolve, reject) => {
				axios({
					method: 'get',
					url: webAppRoot + "/isUserAssignedToCase",
					params: {
						caseId: this.caseItemSelected,
					},
				}).then(response => {
					if (response.data.success) {
						resolve({
							isAssigned: response.data.isAllowed,
							caseId: response.data.payload
						});
					}
					else {
						this.handleDialogs(response.data, this.isUserAssignedToCase);
					}
				}).catch(error => {
					this.handleAxiosError(error);
				});
			});
		},
		loadOpenCase() {
			if (this.caseItemSelected) {
				if (this.allCases) {
					//need to check first if user is assigned to case
					this.isUserAssignedToCase()
					.then(response => {
						var route = "OpenCaseReadOnly";
						if (response.isAssigned) {
							route = "OpenCase";
						}
						this.$router.push({ name: route, params: { id: response.caseId } });
					}).catch(error => {
						this.handleAxiosError(error);
					}); 
				}
				else {
					this.$router.push({ name: "OpenCase", params: { id: this.caseItemSelected } });
				}
			}
			this.caseItemSelected = null;
			this.caseReportItemSelected = null;
		},
		loadOpenReport() {
			if (this.caseReportItemSelected) {
				if (permissions.canReview) {
					this.$router.push({ name: "OpenReport", params: { id: this.caseReportItemSelected } });
				}
				else {
					this.$router.push({ name: "OpenReportReadOnly", params: { id: this.caseReportItemSelected } });
				}
			}
			this.caseItemSelected = null;
			this.caseReportItemSelected = null;
		},
		handleDialogs(response, callback) {
			// // alert(response.data.reason);
			// if (response.data.isXss) {
			// 	console.log("xss detected:" + response.data.reason);
			// 	this.showErrorDialog = true;
			// }
			// else {
			// 	this.showLoginDialog = true;
			// }
			if (response == "not-allowed") {
                bus.$emit("not-allowed", [this.response]);
            }
            if (response.isXss) {
                bus.$emit("xss-error",
                    [this, response.reason]);
            }
            else if (response.isLogin) {
                bus.$emit("login-needed", [this, callback])
            }
            else if (response.success === false) {
                bus.$emit("some-error", [this, response.message]);
            }
		},
		displayMenuItem(menuItem) {
			var isVisible = menuItem.regularItem;
			isVisible = true; // don't use regular Item for display purpose anymore
			if (menuItem.adminOnly != null) {
				return isVisible && menuItem.adminOnly === isAdmin;
			}
			return isVisible;
		},
		emitMenuChanged() {
			console.log("changed", this.isMinied);
//			if (this.isMinied) {
//				bus.$emit('menu-shrinked', [this, null]);
//			}
//			else {
//				bus.$emit('menu-expanded', [this, null]);
//			}
		},
		getIconBeforeClass(menuItem) {
			var classArray = [];
			if (menuItem.isButton && menuItem.miniedRotation) {
				if (this.isMinied) {
					classArray.push('rotate90');
				}
				else {
					classArray.push('rotate270');
				}
			}
			return classArray;
		},
		openHelp() {
			window.open(webAppRoot + "/help/index.html", "_blank");
		},
		toggleMinied() {
			// this.isMinied = !this.isMinied;
			this.$nextTick(() => this.isMinied = true);
		},
		// getUserLeaderBoardInfo() {
        //     axios.get(
        //         webAppRoot + "/getUserLeaderBoardInfo",
        //         {
        //             params: {
        //             }
        //         })
        //         .then(response => {
        //             if (response.data.isAllowed) {
		// 				this.userLeaderBoardInfo = response.data;
        //             }
        //             else {
        //                 this.handleDialogs(response.data, this.getUserLeaderBoardInfo);
        //             }
        //         }).catch(error => {
        //             this.handleAxiosError(error);
        //         });
		// },
		handleAxiosError(error) {
            console.log(error);
            bus.$emit("some-error", [this, error]);
		},
		isBetaVersion() {
            return this.versionName == "beta";
        },
        isVersionOne() {
            return this.versionName == "1.0";
		},
		getVersion() {
			axios.get(webAppRoot + "/getCurrentVersion", {
				params: {}
			})
				.then(response => {
					if (response.data.isAllowed) {
						this.versionName = response.data.payload;
					}
					else {
						this.handleDialogs(response);
					}
				})
				.catch(error => {
					alert(error);
				});
		},
		getFinalLogoUrl() {
			if (this.isVersionOne()) {
				if (this.isMinied) {
					return this.baseUrl + '/resources/images/answer-logo-vertical-medium.png';
				}
				return this.baseUrl + '/resources/images/answer-logo-medium.png';
			}
			else {
				if (this.isMinied) {
					return this.baseUrl + '/resources/images/answer-logo-vertical-medium.png';
				}
				return this.baseUrl + '/resources/images/answer-logo-medium-beta.png';
			}
		},
		updateActiveState(event, menuItem) {
			menuItem.activeColor = event ? "warning--text" : this.getRouteColor(menuItem);
		},
		closeOpenMenu(menuItem) {
			this.isMenuOpen[menuItem.title] = false;
			menuItem.activeColor = this.getRouteColor(menuItem);
		},
		getRouteColor(menuItem) {
			if (this.$route.name.indexOf(menuItem.title.replace(" ", "")) > -1) {
				return "primary--text";
			}
			return "";
		},
		handleRouteChanged() {
			for (let i = 0; i < this.menuItems.length; i++) {
				let menuItem = this.menuItems[i];
				if (menuItem.skipRoute) {
					menuItem.activeColor = this.getRouteColor(menuItem);
				}
			}
		},
		//remove after Xmas
		activateXMas() {
			// this.$vuetify.theme.primary = "#c54245";
			// document.getElementById("main-app").style.background = "#0F8A5F";
			// document.getElementsByClassName("v-navigation-drawer v-navigation-drawer--fixed v-navigation-drawer--open theme--light")[0].style.background = "#0F8A5F";
			// makeItSnow();
		},
		showXmas() {
			return false;
            if (userFullName == "Guillaume Jimenez"
            || userFullName == "Brandi Cantarel"
            || userFullName == "Benjamin Wakeland"
            || userFullName == "Jeffrey Gagan"
            || userFullName == "Erika Villa") {
                return true;
            }
            return false;
        }
	},
	mounted() {
		// this.getUserLeaderBoardInfo();
	},
	created: function () {
		this.populateCases();
		this.populateCasesWithReport();
//		bus.$on('shrink-menu', () => {
//			this.isMinied = true;
//		});
//		bus.$on('expand-menu', () => {
//			this.isMinied = false;
//		});
		bus.$on('clear-item-selected', args => {
			this.caseItemSelected = "";
		});
		//might not be needed anymore with vuetify 1.5.16
		bus.$on('need-layout-resize', args => {
			//resize the main content because it does not happen automatically
			this.$nextTick(function () {
				var content = document.getElementsByClassName("v-content");
				if (content) {
					content[0].style.paddingLeft = this.width + "px";
					var titlebars = document.getElementsByClassName("v-toolbar v-toolbar--fixed");
					for (var i = 0; i < titlebars.length; i++) {
						titlebars[i].style.paddingLeft = this.width + "px";
					}
				}

			});
		});
		bus.$on('update-status', args => {
			this.statusVisible = true;
			this.statusMessage = args[0];
		});
		bus.$on('update-status-off', args => {
			this.statusVisible = false;
			this.statusMessage = "";
		});
		this.getVersion();
	},
	destroyed: function () {
//		bus.$off('shrink-menu');
//		bus.$off('expand-menu');
		bus.$off('clear-item-selected');
		bus.$off('need-layout-resize');
		bus.$off('update-status');
		bus.$off('update-status-off');
	},
	watch: {
		// isMinied: 'emitMenuChanged'
		'$route': 'handleRouteChanged'
	}


});