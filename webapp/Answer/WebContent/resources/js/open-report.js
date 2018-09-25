const OpenReport = {
    props: {
        "readonly": { default: true, type: Boolean }

    },
    template: `<div>


    <div>
        <v-dialog v-model="confirmationDialogVisible" max-width="300px">
            <v-card>
                <v-card-text v-html="confirmationMessage" class="pl-2 pr-2 subheading">

                </v-card-text>
                <v-card-actions class="card-actions-bottom">
                    <v-btn color="primary" @click="proceedWithConfirmation" slot="activator">{{
                        confirmationProceedButton }}
                    </v-btn>
                    <v-btn color="error" @click="cancelConfirmation" slot="activator">{{ confirmationCancelButton }}
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
        <v-snackbar :timeout="4000" :bottom="true" v-model="snackBarVisible">
            {{ snackBarMessage }}
            <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
        </v-snackbar>

        <v-toolbar dense dark :color="colors.openReport" fixed app :extended="loadingReportDetails">
            <v-tooltip class="ml-0" bottom>
                <v-menu offset-y offset-x slot="activator" class="ml-0">
                    <v-btn slot="activator" flat icon dark>
                        <v-icon>more_vert</v-icon>
                    </v-btn>
                    <v-list>
                        <v-list-tile avatar @click="patientDetailsVisible = !patientDetailsVisible">
                            <v-list-tile-avatar>
                                <v-icon>assignment_ind</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Show/Hide Patient Details</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>


                    </v-list>
                </v-menu>
                <span>Case Menu</span>
            </v-tooltip>
            <v-toolbar-title class="white--text ml-0">
                Creating Report for case: {{ caseName }}
                <v-tooltip bottom>
                    <v-icon slot="activator" size="20" class="pb-1"> {{ caseTypeIcon }} </v-icon>
                    <span>{{caseType}} case</span>
                </v-tooltip>
                <save-badge :show-save-needed-badge="isSaveNeededBadgeVisible()" :tooltip="createSaveTooltip()"></save-badge>
            </v-toolbar-title>
            <v-spacer></v-spacer>
            <v-tooltip bottom>
                <v-btn flat icon @click="patientDetailsVisible = !patientDetailsVisible" slot="activator" :color="patientDetailsVisible ? 'amber accent-2' : ''">
                    <!-- <v-icon>perm_identity</v-icon> -->
                    <v-icon>assignment_ind</v-icon>
                </v-btn>
                <span>Show/Hide Patient Details</span>
            </v-tooltip>
            <v-progress-linear class="ml-4 mr-4" :slot="loadingReportDetails ? 'extension' : ''" v-show="loadingReportDetails"
                :indeterminate="true" color="white"></v-progress-linear>
        </v-toolbar>

        Coming soon...                


    </div>
</div>`, data() {
        return {
            reportUnSaved: false,
            loadingReportDetails: false,
            patientDetailsVisible: true,
            caseName: "",
            caseType: "",
            caseTypeIcon: "",
            colors: {
                openReport: "primary"
            },
            snackBarMessage: "",
            snackBarVisible: false,
            confirmationDialogVisible: false,
            confirmationMessage: "Unsaved report changes will be discarded.<br/>Are you sure?",
            confirmationProceedButton: "Proceed",
            confirmationCancelButton: "Cancel",
            
        }
    }, methods: {
        handleRouteChanged(newRoute, oldRoute) {
            if (newRoute.path != oldRoute.path) { //prevent reloading data if only changing the query router.push({query: {test:"hello3"}})
                // this.getAjaxData();
            }
            else { //look at the query
                // console.log(newRoute.query, oldRoute.query);
                var newRouteQuery = JSON.stringify(newRoute.query);
                var oldRouteQuery = JSON.stringify(oldRoute.query);
                if (newRouteQuery != oldRouteQuery) { //some params changed
                    this.loadFromParams(newRoute.query, oldRoute.query);
                }
            }
        },
        loadFromParams(newRouteQuery, oldRouteQuery) {
            this.urlQuery.reportId = this.$route.query.reportId ? this.$route.query.reportId : null;
        },
        isSaveNeededBadgeVisible() {
            return this.annotationSelectionUnSaved || this.variantUnSaved || this.patientDetailsUnSaved || (this.$refs.variantDetailsPanel ? this.$refs.variantDetailsPanel.variantDetailsUnSaved : false);
        },
        createSaveTooltip() {
            var tooltip = ["Some edits have not been saved yet:"];
            if (this.annotationSelectionUnSaved) {
                tooltip.push("- Report");
            }
            if (tooltip.length > 1) {
                return tooltip.join("<br/>");
            }
            return "";
        },
        proceedWithConfirmation() {
            this.confirmationDialogVisible = false;
            //TODO
        },
        cancelConfirmation() {
            this.confirmationDialogVisible = false;
        },
    },
    mounted() {
        this.snackBarMessage = this.readonly ? "View Only Mode: some actions have been disabled" : "",
        this.snackBarVisible = this.readonly;
        if (this.readonly) {
            setTimeout(() => {
                bus.$emit("update-status", ["VIEW ONLY MODE"]);
            }, 4200); //show after snackbar is dismissed
        }
    },
    created() {
    },
    computed: {
    },
    destroyed: function () {
    },
    watch: {
        '$route': 'handleRouteChanged',
    }

};