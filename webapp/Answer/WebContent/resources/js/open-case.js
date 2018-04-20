const OpenCase = {
  template: `<div>
    <v-toolbar dark color="primary" fixed app>
        <v-toolbar-title class="white--text">
            Working on case: {{ caseName }}
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip bottom>
            <v-btn :disabled="patientTables.length == 0" flat icon @click="patientDetailsVisible = !patientDetailsVisible" slot="activator"
            :color="patientDetailsVisible ? 'warning' : ''">
                <v-icon>perm_identity</v-icon>
            </v-btn>
            <span>Patient Details</span>
        </v-tooltip>
    </v-toolbar>

    <div class="text-xs-center pb-3" v-if="patientDetailsVisible">
        <v-toolbar dark color="primary">
            <v-icon>perm_identity</v-icon>
            <v-toolbar-title>Patient Details</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-tooltip bottom>
                <v-btn flat icon @click="patientDetailsVisible = false" slot="activator"
                >
                    <v-icon>close</v-icon>
                </v-btn>
                <span>Close Details</span>
            </v-tooltip>
        </v-toolbar>
        <v-card>
        <v-container grid-list-md fluid>
            <v-layout row wrap>
                <v-flex xs4 v-for="table in patientTables" :key="table.name">
                    <v-card flat>
                        <v-card-text>
                            <v-list class="dense-tiles">
                                <v-list-tile v-for="item in table.items" :key="item.label">
                                    <v-list-tile-content class="pb-2">
                                        <v-layout class="full-width">
                                            <v-flex xs6 class="text-xs-left grow">
                                                <span>{{ item.label }}:</span>
                                            </v-flex>
                                            <v-flex xs6 class="text-xs-right grow blue-grey--text text--lighten-1">
                                                <span>{{ item.value }}</span>
                                            </v-flex>
                                        </v-layout>
                                    </v-list-tile-content>
                                </v-list-tile>
                            </v-list>
                        </v-card-text>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
        </v-card>
    </div>

    <data-table ref="geneVariantDetails" :fixed="false" :fetch-on-created="false" table-title="Variants" initial-sort="chrom"
        no-data-text="No Data" :enable-selection="true">
    </data-table>
</div>`
, data() {
		return {
      loading: true,
      patientTables: [],
      patientDetailsVisible: true,
      caseName: "",
      caseId: ""
		}
	},
	methods: {
		getAjaxData() {
			this.loading = true;
			axios.get(webAppRoot + "/getCaseDetails", {
				params: {
					caseId: this.$route.params.id
				}
			})
				.then(response => {
					if (response.data.isAllowed) {
            this.patientTables = response.data.patientInfo.patientTables;
            this.caseName = response.data.caseName;
            this.caseId = response.data.caseId;
            this.$refs.geneVariantDetails.manualDataFiltered(response.data);
					}
					else {
						this.handleDialogs(response, this.getAjaxData);
					}
					this.loading = false;
				})
				.catch(error => {
					this.loading = false;
					console.log(error);
				});
		},
		test() {
			this.getAjaxData();
		},

	},
	mounted: function () {
		this.getAjaxData();
		bus.$emit("clear-item-selected", [this]);
	},
	watch: {
		'$route': 'test',
		'scrollPos': 'scrollWithBar'
	}
};