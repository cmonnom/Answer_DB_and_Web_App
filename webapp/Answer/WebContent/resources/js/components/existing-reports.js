

Vue.component('existing-reports', {
    props: {
        currentReportId: {default: "", type: String},
        readonly: {default: true, type: Boolean},
        reportUnsaved: {default: true, type: Boolean},
    },
    template: `<div>
    
    <v-dialog v-model="finalizeConfirmationVisible" max-width="700px">
    <v-toolbar dense dark color="primary">
        <v-toolbar-title>
            Finalize Report
        </v-toolbar-title>
    </v-toolbar>
    <v-card>
        <v-card-text class="pl-2 pr-2 subheading">
            Finalizing a report is <b>permanent</b>. No other report can be finalized.<br/>
            Only amendments and addenda will be allowed after this step.<br/>
            <br/>
            Please type your full name to confirm:
            <v-text-field
                v-model="fullNameField" class="mr-2 no-height" label="Full Name">
            </v-text-field>
        </v-card-text>
        <v-card-actions class="card-actions-bottom">
            <v-btn color="warning" @click="finalizeReport()" slot="activator" :disabled="!readyToFinalize()" :loading="finalizingReport">Finalize
            </v-btn>
            <v-btn color="error" @click="cancelFinalizeReport()" slot="activator" :disable="finalizingReport">Cancel
            </v-btn>
        </v-card-actions>
    </v-card>
</v-dialog>

    <v-card class="soft-grey-background">
    <v-toolbar class="elevation-0" dense dark color="primary">
        <v-menu offset-y offset-x class="ml-0">
            <v-btn slot="activator" flat icon dark>
                <v-icon color="amber accent-2">assignment</v-icon>
                <v-icon color="amber accent-2" class="multi-icon">assignment</v-icon>
            </v-btn>
            <v-list>
                <v-list-tile avatar @click="closeExistingReport()">
                    <v-list-tile-avatar>
                        <v-icon>cancel</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Close Existing Reports</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
            </v-list>
        </v-menu>
        <v-toolbar-title class="ml-0">Existing Reports</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip bottom>
            <v-btn flat icon @click="closeExistingReport()" slot="activator">
                <v-icon>close</v-icon>
            </v-btn>
            <span>Close Existing Reports</span>
        </v-tooltip>
    </v-toolbar>
    <v-container grid-list-md fluid>
    <v-layout row wrap>
    <v-flex xs12>
        <v-btn @click="getReportDetails()">New Report</v-btn>
    </v-flex>
    <v-flex xs12 lg4 xl3 v-for="report in existingReports" :key="report._id['$oid']">
        <v-card :color="isReportSelected(report) ? 'amber accent-2' : ''">
            <v-card-text>
            <v-list :class="['dense-tiles', isReportSelected(report) ? 'amber accent-2' : '']" :color="isReportSelected(report) ? 'amber accent-2' : ''">
            <v-list-tile v-if="report.finalized">
            <v-list-tile-content class="pb-2">
                <v-layout class="full-width" justify-space-between>
                    <v-flex class="text-xs-left xs">
                    <span class="'selectable'"><b>Finalized</b></span>
                    <v-icon color="primary" class="pb-1">check</v-icon>
                    </v-flex>
                </v-layout>
            </v-list-tile-content>
            </v-list-tile>
                <v-list-tile>
                    <v-list-tile-content class="pb-2">
                        <v-layout class="full-width" justify-space-between>
                            <v-flex class="text-xs-left xs">
                                <span class="'selectable'">Report Name:</span>
                            </v-flex>
                            <v-flex class="blue-grey--text text--lighten-1 text-xs-right">
                                <span class="selectable">{{ report.reportName }}</span>
                            </v-flex>
                        </v-layout>
                    </v-list-tile-content>
                </v-list-tile>
                <v-list-tile>
                <v-list-tile-content class="pb-2">
                    <v-layout class="full-width" justify-space-between>
                        <v-flex class="text-xs-left xs">
                            <span class="selectable">Date Modified:</span>
                        </v-flex>
                        <v-flex class="text-xs-right blue-grey--text text--lighten-1">
                            <span class="selectable" v-text="parseDate(report)"></span>
                        </v-flex>
                    </v-layout>
                </v-list-tile-content>
                </v-list-tile>
                <v-list-tile>
                <v-list-tile-content class="pb-2">
                    <v-layout class="full-width" justify-space-between>
                        <v-flex class="text-xs-left xs">
                            <span class="selectable">Modified By:</span>
                        </v-flex>
                        <v-flex class="text-xs-right blue-grey--text text--lighten-1">
                            <span class="selectable">{{ report.modifiedByName }}</span>
                        </v-flex>
                    </v-layout>
                </v-list-tile-content>
                </v-list-tile>
                <v-list-tile>
                <v-list-tile-content class="pb-2">
                    <v-layout class="full-width" justify-space-between>
                        <v-flex class="text-xs-left xs">
                            <span class="'selectable'">Notes:</span>
                        </v-flex>
                        <v-flex class="text-xs-right blue-grey--text text--lighten-1">
                        <v-tooltip bottom max-width="600px">
                            <span slot="activator" class="selectable" v-text="truncatedNotes(report)"></span>
                            <span>{{ report.summary }}</span>    
                        </v-tooltip>
                        </v-flex>
                    </v-layout>
                </v-list-tile-content>
                </v-list-tile>
            </v-list>
            </v-card-text>

            <v-card-actions class="card-actions-bottom">
                <v-btn @click="getReportDetails(report._id['$oid'])">
                Load Report
                </v-btn>
                <v-tooltip bottom>
                <v-btn slot="activator" @click="openFinalizeConfirmationDialog(report._id['$oid'])"
                :disabled="finalizeButtonDisabled(report)">
                Finalize
                </v-btn>
                <span v-show="finalizeButtonDisabled(report)">Load the report first. All edits must be saved.</span>
                <span v-show="!finalizeButtonDisabled(report)">Finalize the report. NO OTHER REPORT CAN BE FINALIZED AFTER THIS ONE</span>
                </v-tooltip>
            </v-card-actions>

        </v-card>
    </v-flex>
    </v-layout>
    </v-container>
    </v-card>
    </div>
    `,
    data() {
        return {
            existingReports: [],
            finalizeConfirmationVisible: false,
            reportIdToFinalize: null,
            fullNameField: "",
            canFinalize: false,
            finalizingReport: false

        }
    },
    methods: {
        closeExistingReport() {
            this.$emit("close-existing-reports");
        },
        getReportDetails(reportId) {
            this.$emit("get-report-details", reportId);
        },
        handleLoadingReportDetails(isLoading) {
            this.$emit("loading-report-details", isLoading);
        },
        getExistingReports() {
            axios.get(
                webAppRoot + "/getExistingReports",
                {
                    params: {
                        caseId: this.$route.params.id
                    }
                })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.existingReports = response.data.reports;
                        this.existingReports.forEach((r) => {console.log(r._id['$oid'])});
                        this.canFinalize = !this.finalizeReportExists();
                        this.getReportDetails(this.$route.query.reportId);
                    }
                    else {
                        this.handleDialogs(response, this.getExistingReports);
                        this.handleLoadingReportDetails(false);
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                    this.handleLoadingReportDetails(false);
                });
        },
        isReportSelected(report) {
            return report && report._id.$oid == this.currentReportId;
        },
        truncatedNotes(report) {
            if (report.summary && report.summary.length > 50) {
                return report.summary.substring(0, 50) + "...";
            }
            return report.summary;
        },
        parseDate(report) {
            if (report.dateModified) {
                return report.modifiedSince + " (" + report.dateModified.split("T")[0] + ")";
            }
        },
        handleDialogs(response, callback) {
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
        handleAxiosError(error) {
            console.log(error);
            bus.$emit("some-error", [this, error]);
        },
        readyToFinalize() {
            var nameMatch = this.getCurrentUserFullName().toUpperCase() == this.fullNameField.toUpperCase();
            return this.reportIdToFinalize && !this.readonly && nameMatch;
        },
        cancelFinalizeReport() {
            this.finalizeConfirmationVisible = false;
            this.fullNameField = "";
            this.reportIdToFinalize = null;
        },
        finalizeReport() {
            // return; //TODO remove after testing
            if (!this.readyToFinalize()) {
                return;
            }
            this.finalizingReport = true;
            axios({
                method: 'post',
                url: webAppRoot + "/finalizeReport",
                params: {
                    reportId: this.reportIdToFinalize,
                },
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        snackBarMessage = "Report Finalized";
                        snackBarVisible = true;
                        this.getExistingReports();
                        this.confirmationSaveDialogVisible = false;
                    } else {
                        this.handleDialogs(response.data, this.finalizeReport);
                        this.finalizeConfirmationVisible = false;
                    }
                    this.finalizingReport = false;
                })
                .catch(error => {
                    this.finalizingReport = false;
                    this.finalizeConfirmationVisible = false;
                    this.handleAxiosError(error);
                });
        },
        openFinalizeConfirmationDialog(reportId) {
            this.reportIdToFinalize = reportId;
            this.fullNameField = "";
            this.finalizeConfirmationVisible = true;
        },
        finalizeReportExists() {
            for (var i = 0; i < this.existingReports.length; i++) {
                var r = this.existingReports[i];
                if (r.finalized) {
                    return true;
                }
            }
            return false;
        },
        finalizeButtonDisabled(report) {
            return !this.canFinalize || (report._id['$oid'] != this.currentReportId) || this.reportUnsaved;
        },
        getCurrentUserFullName() {
            return userFullName; //global variable
        }

    },
    computed: {

    },
    created() {

    },
    destroyed() {
    },
    mounted() {
    },
    watch: {
    }



});

