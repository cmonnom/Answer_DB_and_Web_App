const OpenReport = {
    props: {
        "readonly": { default: true, type: Boolean }

    },
    template: `<div>
    <v-dialog v-model="confirmationDialogVisible" max-width="500px">
        <v-toolbar dense dark :color="colors.openReport">
            <v-toolbar-title>
                {{ currentEditTitle }}
            </v-toolbar-title>
        </v-toolbar>
        <v-card>
            <v-card-text class="pl-2 pr-2 subheading">
                <v-text-field :textarea="true" :readonly="!canProceed('canReview') || readonly" :disabled="!canProceed('canReview') || readonly"
                    v-model="currentEdit[currentEditField]" class="mr-2 no-height">
                </v-text-field>
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

    <v-dialog v-model="confirmationSaveDialogVisible" max-width="500px">
        <v-toolbar dense dark :color="colors.openReport">
            <v-toolbar-title>
                Save Current Report
            </v-toolbar-title>
        </v-toolbar>
        <v-card>
            <v-card-text class="pl-2 pr-2 subheading">
                <span class="subheading">Choose a name for this report. If the name already exists, the report will be updated.</span>
                <v-text-field
                    v-model="fullReport.reportName" class="mr-2 no-height" label="Report Name">
                </v-text-field>
            </v-card-text>
            <v-card-actions class="card-actions-bottom">
                <v-btn color="primary" @click="saveReport" slot="activator" :disabled="isSaveDisabled()">
                <span v-text="saveOrUpdateButtonName()"></span>
                </v-btn>
                <v-btn color="error" @click="cancelSaveConfirmation" slot="activator">Cancel
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
                    <v-list-tile class="list-menu">
                        <v-list-tile-content>
                            <v-list-tile-title>
                                <v-menu offset-y offset-x open-on-hover>
                                    <span slot="activator">
                                        <v-icon class="pl-2 pr-4">keyboard_arrow_right</v-icon>Show / Hide
                                        <!-- This is a hack to extend the menu active area because the title is much shorter than other items -->
                                        <span v-for="i in 30" :key="i">&nbsp;</span>
                                    </span>
                                    <v-list>
                                    <v-list-tile avatar @click="existingReportsVisible = !existingReportsVisible">
                                    <v-list-tile-avatar>
                                        <v-icon>assignment</v-icon>
                                        <v-icon class="multi-icon">assignment</v-icon>
                                    </v-list-tile-avatar>
                                    <v-list-tile-content>
                                        <v-list-tile-title>Existing Reports</v-list-tile-title>
                                    </v-list-tile-content>
                                    </v-list-tile>

                                        <v-list-tile avatar @click="patientDetailsVisible = !patientDetailsVisible">
                                            <v-list-tile-avatar>
                                                <v-icon>assignment_ind</v-icon>
                                            </v-list-tile-avatar>
                                            <v-list-tile-content>
                                                <v-list-tile-title>Patient Details</v-list-tile-title>
                                            </v-list-tile-content>
                                        </v-list-tile>

                                        <v-list-tile avatar @click="reportNotesVisible = !reportNotesVisible" :disabled="patientTables.length == 0">
                                            <v-list-tile-avatar>
                                                <v-icon>edit</v-icon>
                                            </v-list-tile-avatar>
                                            <v-list-tile-content>
                                                <v-list-tile-title>Report Notes</v-list-tile-title>
                                            </v-list-tile-content>
                                        </v-list-tile>

                                        <v-list-tile avatar @click="indicatedTherapiesVisible = !indicatedTherapiesVisible">
                                            <v-list-tile-avatar>
                                                <v-icon>mdi-pill</v-icon>
                                            </v-list-tile-avatar>
                                            <v-list-tile-content>
                                                <v-list-tile-title>Indicated Therapies</v-list-tile-title>
                                            </v-list-tile-content>
                                        </v-list-tile>

                                        <v-list-tile avatar @click="clinicalTrialsVisible = !clinicalTrialsVisible">
                                            <v-list-tile-avatar>
                                                <v-icon>assignment</v-icon>
                                            </v-list-tile-avatar>
                                            <v-list-tile-content>
                                                <v-list-tile-title>Clinical Trials</v-list-tile-title>
                                            </v-list-tile-content>
                                        </v-list-tile>

                                        <v-list-tile avatar @click="strongClinicalSignificanceVisible = !strongClinicalSignificanceVisible">
                                            <v-list-tile-avatar>
                                                <v-icon>mdi-message-bulleted</v-icon>
                                            </v-list-tile-avatar>
                                            <v-list-tile-content>
                                                <v-list-tile-title>Strong Clinical Significance</v-list-tile-title>
                                            </v-list-tile-content>
                                        </v-list-tile>

                                        <v-list-tile avatar @click="possibleClinicalSignificanceVisible = !possibleClinicalSignificanceVisible">
                                            <v-list-tile-avatar>
                                                <v-icon>mdi-message-bulleted</v-icon>
                                            </v-list-tile-avatar>
                                            <v-list-tile-content>
                                                <v-list-tile-title>Possible Clinical Significance</v-list-tile-title>
                                            </v-list-tile-content>
                                        </v-list-tile>

                                        <v-list-tile avatar @click="unknownClinicalSignificanceVisible = !unknownClinicalSignificanceVisible">
                                            <v-list-tile-avatar>
                                                <v-icon>mdi-message-bulleted</v-icon>
                                            </v-list-tile-avatar>
                                            <v-list-tile-content>
                                                <v-list-tile-title>Unknown Clinical Significance</v-list-tile-title>
                                            </v-list-tile-content>
                                        </v-list-tile>
                                    </v-list>
                                </v-menu>
                            </v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>

                    <v-list-tile avatar @click="previewReport()"  :disabled="isSaveDisabled()" :loading="savingReport">
                    <v-list-tile-avatar>
                        <v-icon>picture_as_pdf</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Preview Report as PDF</v-list-tile-title>
                    </v-list-tile-content>
                    </v-list-tile>

                    <v-list-tile avatar @click="openSaveConfirmation()"  :disabled="isSaveDisabled()">
                    <v-list-tile-avatar>
                        <v-icon>save</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Save Report</v-list-tile-title>
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
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip bottom>
            <v-btn flat icon @click="existingReportsVisible = !existingReportsVisible" slot="activator" :color="existingReportsVisible ? 'amber accent-2' : ''">
                <v-icon>assignment</v-icon>
                <v-icon color="amber accent-2" class="multi-icon">assignment</v-icon>
            </v-btn>
            <span>Show/Hide Existing Reports</span>
        </v-tooltip>

        <v-tooltip bottom>
        <v-btn flat icon @click="patientDetailsVisible = !patientDetailsVisible" slot="activator" :color="patientDetailsVisible ? 'amber accent-2' : ''">
            <v-icon>assignment_ind</v-icon>
        </v-btn>
        <span>Show/Hide Patient Details</span>
    </v-tooltip>

        <v-tooltip bottom>
        <v-btn flat icon @click="previewReport" slot="activator" :disabled="isSaveDisabled()" :loading="savingReport">
            <v-icon>picture_as_pdf</v-icon>
        </v-btn>
        <span>Preview Report as PDF</span>
       </v-tooltip>

        <v-badge color="red" right bottom overlap v-model="reportUnsaved" class="mini-badge">
        <v-icon slot="badge"></v-icon>
        <v-tooltip bottom>
        <v-btn flat icon @click="openSaveConfirmation" slot="activator" :disabled="isSaveDisabled()">
            <v-icon>save</v-icon>
        </v-btn>
        <span>Save Report</span>
       </v-tooltip>
       </v-badge>
 
        <v-progress-linear class="ml-4 mr-4" :slot="loadingReportDetails ? 'extension' : ''" v-show="loadingReportDetails"
            :indeterminate="true" color="white"></v-progress-linear>
    </v-toolbar>

    <!-- Existing Reports -->
    <v-slide-y-transition>
        <v-layout v-if="existingReportsVisible" row wrap pb-3>
            <v-flex xs12>
            <v-card class="soft-grey-background">
            <v-toolbar class="elevation-0" dense dark :color="colors.openReport">
                <v-menu offset-y offset-x class="ml-0">
                    <v-btn slot="activator" flat icon dark>
                        <v-icon color="amber accent-2">assignment</v-icon>
                        <v-icon color="amber accent-2" class="multi-icon">assignment</v-icon>
                    </v-btn>
                    <v-list>
                        <v-list-tile avatar @click="existingReportsVisible = false">
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
                    <v-btn flat icon @click="existingReportsVisible = false" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close Existing Reports</span>
                </v-tooltip>
            </v-toolbar>
            <v-container grid-list-md fluid>
            <v-layout row wrap>
            <v-flex xs>
                <v-btn @click="getReportDetails()">New Report</v-btn>
            </v-flex>
            <v-flex xs12 lg4 xl3 v-for="report in existingReports" :key="report.reportName">
                <v-card>
                    <v-card-text>
                    <v-list class="dense-tiles">
                        <v-list-tile>
                            <v-list-tile-content class="pb-2">
                                <v-layout class="full-width" justify-space-between>
                                    <v-flex class="text-xs-left xs">
                                        <span class="'selectable'">Report Name:</span>
                                    </v-flex>
                                    <v-flex class="text-xs-right blue-grey--text text--lighten-1">
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
                                <v-tooltip bottom>
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
                    </v-card-actions>

                </v-card>
            </v-flex>
            </v-layout>
            </v-container>
            </v-card>
            </v-flex>
        </v-layout>
    </v-slide-y-transition>

    <!-- Patient Details -->
    <v-slide-y-transition>
        <v-layout v-if="patientDetailsVisible">
            <v-flex xs12 md12 lg10 xl9>
                <div class="text-xs-center pb-3">
                    <v-card>
                        <v-toolbar class="elevation-0" dense dark :color="colors.openReport">
                            <v-menu offset-y offset-x class="ml-0">
                                <v-btn slot="activator" flat icon dark>
                                    <v-icon color="amber accent-2">assignment_ind</v-icon>
                                </v-btn>
                                <v-list>
                                    <v-list-tile avatar @click="patientDetailsVisible = false">
                                        <v-list-tile-avatar>
                                            <v-icon>cancel</v-icon>
                                        </v-list-tile-avatar>
                                        <v-list-tile-content>
                                            <v-list-tile-title>Close Patient Details</v-list-tile-title>
                                        </v-list-tile-content>
                                    </v-list-tile>
                                </v-list>
                            </v-menu>
                            <v-toolbar-title class="ml-0">Patient Details</v-toolbar-title>
                            <v-spacer></v-spacer>
                            <v-tooltip bottom>
                                <v-btn flat icon @click="patientDetailsVisible = false" slot="activator">
                                    <v-icon>close</v-icon>
                                </v-btn>
                                <span>Close Details</span>
                            </v-tooltip>
                        </v-toolbar>
                        <v-container grid-list-md fluid>
                            <v-layout row wrap>
                                <v-flex xs4 v-for="table in patientTables" :key="table.name">
                                    <v-card flat>
                                        <v-card-text>
                                            <v-list class="dense-tiles">
                                                <v-list-tile v-for="item in table.items" :key="item.label">
                                                    <v-list-tile-content class="pb-2">
                                                        <v-layout class="full-width " justify-space-between>
                                                            <v-flex class="text-xs-left xs">
                                                                <span :class="[item.type == 'text' ? 'pt-4' : '', 'selectable']">{{
                                                                    item.label }}:</span>
                                                            </v-flex>
                                                            <v-flex :class="[item.type ? 'xs5' : 'xs','text-xs-right', '', 'blue-grey--text', 'text--lighten-1']">
                                                                <span v-if="item.type == null" class="selectable">{{
                                                                    item.value }}</span>
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
            </v-flex>
        </v-layout>
    </v-slide-y-transition>

    <!-- Report Notes -->
    <v-slide-y-transition>
        <v-layout v-if="reportNotesVisible">
            <v-flex xs12 xl9 class="pb-3">
                <v-card>
                    <v-toolbar class="elevation-0" dense dark :color="colors.openReport">
                        <v-menu offset-y offset-x class="ml-0">
                        <v-btn slot="activator" flat icon dark>
                            <v-icon color="amber accent-2">edit</v-icon>
                        </v-btn>
                        <v-list>
                            <v-list-tile avatar @click="resetReportNotes()" :disabled="!canProceed('canReview') || readonly">
                                <v-list-tile-avatar>
                                    <v-icon>settings_backup_restore</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title>Restore From Last Saved</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>

                            <v-list-tile avatar @click="reportNotesVisible = false">
                                <v-list-tile-avatar>
                                    <v-icon>cancel</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title>Close Report Notes</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>
                        </v-list>
                    </v-menu>
                        
                        <v-toolbar-title  class="ml-0">Report Notes</v-toolbar-title>
                        <v-spacer></v-spacer>
                        <v-tooltip bottom>
                            <v-btn flat icon @click="resetReportNotes()" slot="activator" :disabled="!canProceed('canReview') || readonly">
                                <v-icon>settings_backup_restore</v-icon>
                            </v-btn>
                            <span>Restore Last Saved Report Notes</span>
                        </v-tooltip>
                        <v-tooltip bottom>
                            <v-btn flat icon @click="reportNotesVisible = false" slot="activator">
                                <v-icon>close</v-icon>
                            </v-btn>
                            <span>Close Report Notes</span>
                        </v-tooltip>
                    </v-toolbar>
                    <v-card-text>
                        <v-text-field :textarea="true" :readonly="!canProceed('canReview') || readonly" :disabled="!canProceed('canReview') || readonly"
                            v-model="fullReport.summary" class="mr-2 no-height" label="Write your comments here" @input="reportNeedsSaving()">
                        </v-text-field>
                    </v-card-text>
                </v-card>
            </v-flex>
        </v-layout>
    </v-slide-y-transition>

    <v-slide-y-transition>
        <v-flex xs12 xl9 v-show="indicatedTherapiesVisible" pb-3>
            <div>
                <data-table ref="indicatedTherapies" :fixed="false" :fetch-on-created="false" table-title="Indicated Therapies"
                    initial-sort="gene" no-data-text="No Data" :show-pagination="true" title-icon="mdi-pill" :color="colors.openReport"
                    :disable-sticky-header="true">
                </data-table>
            </div>
        </v-flex>
    </v-slide-y-transition>

    <v-slide-y-transition>
        <v-flex xs12 xl10 v-show="clinicalTrialsVisible">
            <div>
                <data-table ref="clinicalTrials" :fixed="false" :fetch-on-created="false" table-title="Clinical Trials"
                    initial-sort="biomarker" no-data-text="No Data" :show-pagination="true" title-icon="assignment"
                    :color="colors.openReport" :disable-sticky-header="true">
                </data-table>
            </div>
        </v-flex>
    </v-slide-y-transition>

    <v-container class="pt-3" grid-list-md fluid>
        <v-layout row wrap>
            <v-slide-y-transition>
                <v-flex :class="getClinicalSignificanceFlex()" v-show="strongClinicalSignificanceVisible">
                    <clinical-significance title="Variants of Strong Clinical Significance" :clinical-significance="strongClinicalSignificance"
                    @clinical-significance-changed="reportNeedsSaving"
                    :original-clinical-significance="originalStrongClinicalSignificance"
                        :disabled="!canProceed('canReview')" @close-panel="strongClinicalSignificanceVisible = false">
                    </clinical-significance>
                </v-flex>
            </v-slide-y-transition>

            <v-slide-y-transition>
                <v-flex :class="getClinicalSignificanceFlex()" v-show="possibleClinicalSignificanceVisible">
                    <clinical-significance title="Variants of Possible Clinical Significance" :clinical-significance="possibleClinicalSignificance"
                    @clinical-significance-changed="reportNeedsSaving"
                    :original-clinical-significance="originalPossibleClinicalSignificance"
                        :disabled="!canProceed('canReview')" @close-panel="possibleClinicalSignificanceVisible = false">
                    </clinical-significance>
                </v-flex>
            </v-slide-y-transition>

            <v-slide-y-transition>
                <v-flex :class="getClinicalSignificanceFlex()" v-show="unknownClinicalSignificanceVisible">
                    <clinical-significance title="Variants of Unknown Clinical Significance" :clinical-significance="unknownClinicalSignificance"
                    @clinical-significance-changed="reportNeedsSaving"
                    :original-clinical-significance="originalUnknownClinicalSignificance"
                        :disabled="!canProceed('canReview')" @close-panel="unknownClinicalSignificanceVisible = false">
                    </clinical-significance>
                </v-flex>
            </v-slide-y-transition>

        </v-layout>

    </v-container>

    <v-slide-y-transition>
        <v-flex xs12 xl9 v-show="copyNumberAlterationsVisible" pt-3>
            <div>
                <data-table ref="copyNumberAlterations" :fixed="false" :fetch-on-created="false" table-title="Copy Number Alterations"
                    initial-sort="gene" no-data-text="No Data" :show-pagination="true" title-icon="assignment" :color="colors.openReport"
                    :disable-sticky-header="true">
                </data-table>
            </div>
        </v-flex>
    </v-slide-y-transition>

    <v-slide-y-transition>
        <v-flex xs12 xl9 v-show="geneFusionVisible" pt-3>
            <div>
                <data-table ref="geneFusions" :fixed="false" :fetch-on-created="false" table-title="Gene Fusions"
                    initial-sort="fusionName" no-data-text="No Data" :show-pagination="true" title-icon="assignment"
                    :color="colors.openReport" :disable-sticky-header="true">
                </data-table>
            </div>
        </v-flex>
    </v-slide-y-transition>

</div>`,
    data() {
        return {
            reportUnsaved: false,
            loadingReportDetails: false,
            patientTables: [],
            patientDetailsVisible: false,
            caseName: "",
            caseType: "",
            caseTypeIcon: "",
            colors: {
                openReport: "primary"
            },
            snackBarMessage: "",
            snackBarVisible: false,
            confirmationDialogVisible: false,
            confirmationProceedButton: "Apply",
            confirmationCancelButton: "Cancel",
            indicatedTherapiesVisible: false,
            clinicalTrialsVisible: false,
            strongClinicalSignificanceVisible: false,
            possibleClinicalSignificanceVisible: false,
            unknownClinicalSignificanceVisible: false,
            strongClinicalSignificance: [],
            possibleClinicalSignificance: [],
            unknownClinicalSignificance: [],
            originalStrongClinicalSignificance: [],
            originalPossibleClinicalSignificance: [],
            originalUnknownClinicalSignificance: [],
            copyNumberAlterationsVisible: false,
            geneFusionVisible: false,
            reportNotesVisible: false,
            fullReport: {},
            currentEdit: {},
            currentEditField: "",
            currentEditTitle: "",
            currentEditTextBackup: "",
            savingReport: false,
            originalFullReportSummary: "",
            existingReports: [],
            existingReportsVisible: true,
            confirmationSaveDialogVisible: false

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
        createSaveTooltip() {
            var tooltip = ["Some edits have not been saved yet:"];
            if (tooltip.length > 1) {
                return tooltip.join("<br/>");
            }
            return "";
        },
        proceedWithConfirmation() {
            this.confirmationDialogVisible = false;
            var hasChanged = this.currentEditTextBackup != this.currentEdit[this.currentEditField];
            this.reportUnsaved = this.reportUnsaved || hasChanged;
        },
        cancelConfirmation() {
            this.currentEdit[this.currentEditField] = this.currentEditTextBackup;
            this.confirmationDialogVisible = false;
        },
        addNCTIDHeaderAction(headers) {
            for (var i = 0; i < headers.length; i++) {
                if (headers[i].value == "nctid") {
                    headers[i].itemAction = this.handleNCTIdLink;
                    headers[i].actionIcon = "open_in_new";
                    headers[i].actionTooltip = "Open Trial in new Tab";
                    break;
                }
            }
        },
        handleNCTIdLink(item) {
            var link = "https://clinicaltrials.gov/ct2/show/" + item.nctid;
            window.open(link, "_blank");
        },
        isSaveDisabled() {
            return !this.canProceed('canReview') || this.readonly || this.savingReport || !this.fullReport.reportName;
        },
        getReportDetails(reportId) {
            this.loadingReportDetails = true;
            axios.get(
                webAppRoot + "/getReportDetails",
                {
                    params: {
                        reportId: reportId
                    }
                })
                .then(response => {
                    if (response.data.isAllowed) {
                        this.reportUnsaved = false;
                        this.strongClinicalSignificance = [];
                        this.possibleClinicalSignificance = [];
                        this.unknownClinicalSignificance = [];
                        this.originalStrongClinicalSignificance = [];
                        this.originalPossibleClinicalSignificance = [];
                        this.originalUnknownClinicalSignificance = [];
                        this.fullReport = response.data;
                        console.log(this.fullReport);
                        this.originalFullReportSummary = this.fullReport.summary;
                        this.patientTables = this.fullReport.patientInfo.patientTables;
                        this.extractPatientDetailsInfo(this.fullReport.caseName);
                        this.$refs.indicatedTherapies.manualDataFiltered(this.fullReport.indicatedTherapySummary);
                        if (this.canProceed("canReview") && !this.readonly) {
                            this.addIndicatedTherapyHeaderAction(this.fullReport.indicatedTherapySummary.headers);
                        }

                        if (this.fullReport.clinicalTrialsSummary) {
                            this.$refs.clinicalTrials.manualDataFiltered(this.fullReport.clinicalTrialsSummary);
                            this.addNCTIDHeaderAction(this.fullReport.clinicalTrialsSummary.headers);
                        }

                        if (this.fullReport.cnvSummary) {
                            this.$refs.copyNumberAlterations.manualDataFiltered(this.fullReport.cnvSummary);
                            if (this.canProceed("canReview") && !this.readonly) {
                                this.addCNVHeaderAction(this.fullReport.cnvSummary.headers);
                            }
                        }

                        if (this.fullReport.translocationSummary) {
                            this.$refs.geneFusions.manualDataFiltered(this.fullReport.translocationSummary);
                            if (this.canProceed("canReview") && !this.readonly) {
                                this.addTranslocationHeaderAction(this.fullReport.translocationSummary.headers);
                            }
                        }

                        var strongLabels = Object.keys(this.fullReport.snpVariantsStrongClinicalSignificance);
                        if (strongLabels) {
                            for (var i = 0; i < strongLabels.length; i++) {
                                this.strongClinicalSignificance.push({
                                    label: this.fullReport.snpVariantsStrongClinicalSignificance[strongLabels[i]].geneVariant,
                                    text: this.fullReport.snpVariantsStrongClinicalSignificance[strongLabels[i]].annotation
                                });
                                this.originalStrongClinicalSignificance.push({
                                    label: this.fullReport.snpVariantsStrongClinicalSignificance[strongLabels[i]].geneVariant,
                                    text: this.fullReport.snpVariantsStrongClinicalSignificance[strongLabels[i]].annotation
                                });
                            }
                        }
                        var possibleLabels = Object.keys(this.fullReport.snpVariantsPossibleClinicalSignificance);
                        if (possibleLabels) {
                            for (var i = 0; i < possibleLabels.length; i++) {
                                this.possibleClinicalSignificance.push({
                                    label: this.fullReport.snpVariantsPossibleClinicalSignificance[possibleLabels[i]].geneVariant,
                                    text: this.fullReport.snpVariantsPossibleClinicalSignificance[possibleLabels[i]].annotation
                                });
                                this.originalPossibleClinicalSignificance.push({
                                    label: this.fullReport.snpVariantsPossibleClinicalSignificance[possibleLabels[i]].geneVariant,
                                    text: this.fullReport.snpVariantsPossibleClinicalSignificance[possibleLabels[i]].annotation
                                });
                            }
                        }
                        var unknownLabels = Object.keys(this.fullReport.snpVariantsUnknownClinicalSignificance);
                        if (unknownLabels) {
                            for (var i = 0; i < unknownLabels.length; i++) {
                                this.unknownClinicalSignificance.push({
                                    label: this.fullReport.snpVariantsUnknownClinicalSignificance[unknownLabels[i]].geneVariant,
                                    text: this.fullReport.snpVariantsUnknownClinicalSignificance[unknownLabels[i]].annotation
                                });
                                this.originalUnknownClinicalSignificance.push({
                                    label: this.fullReport.snpVariantsUnknownClinicalSignificance[unknownLabels[i]].geneVariant,
                                    text: this.fullReport.snpVariantsUnknownClinicalSignificance[unknownLabels[i]].annotation
                                });
                            }
                        }
                        this.loadingReportDetails = false;
                        this.patientDetailsVisible = true;
                        this.reportNotesVisible = true;
                        this.indicatedTherapiesVisible = true;
                        this.clinicalTrialsVisible = true;
                        this.strongClinicalSignificanceVisible = true;
                        this.possibleClinicalSignificanceVisible = true;
                        this.unknownClinicalSignificanceVisible = true;
                        this.copyNumberAlterationsVisible = true;
                        this.geneFusionVisible = true;
                    }
                    else {
                        this.handleDialogs(response, this.getReportDetails.bind(null, reportId));
                        this.loadingReportDetails = false;
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                    this.loadingReportDetails = false;
                });
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
                        console.log(response.data);
                        this.existingReports = response.data.reports;
                        if (this.existingReports.length == 0) {
                            this.getReportDetails();
                        }
                    }
                    else {
                        this.handleDialogs(response, this.getExistingReports);
                        this.loadingReportDetails = false;
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                    this.loadingReportDetails = false;
                });
        },
        handleAxiosError(error) {
            console.log(error);
            bus.$emit("some-error", [this, error]);
        },
        extractPatientDetailsInfo(caseName) {
            for (var i = 0; i < this.patientTables.length; i++) {
                for (var j = 0; j < this.patientTables[i].items.length; j++) {
                    var item = this.patientTables[i].items[j];
                    if (caseName && item.field == "caseName") {
                        this.caseName = caseName + " (" + item.value + ")";
                    }
                }
            }
        },
        canProceed(field) {
            if (isAdmin) {
                return true;
            }
            switch (field) {
                case "canAnnotate": return permissions.canAnnotate;
                case "canSelect": return permissions.canSelect;
                case "canView": return permissions.canView;
                case "canReview": return permissions.canReview;
                default: return false;
            }
        },
        getClinicalSignificanceFlex() {
            var counter = 0;
            if (this.strongClinicalSignificanceVisible) {
                counter++;
            }
            if (this.possibleClinicalSignificanceVisible) {
                counter++;
            }
            if (this.unknownClinicalSignificanceVisible) {
                counter++;
            }
            switch (counter) {
                case 0: return ['xs12'];
                case 1: return ['xs12'];
                case 2: return ['xs12', 'md12', 'lg6'];
                case 3: return ['xs12', 'md12', 'lg6', 'xl4'];
            }
        },
        addIndicatedTherapyHeaderAction(headers) {
            for (var i = 0; i < headers.length; i++) {
                if (headers[i].value == "indication") {
                    headers[i].itemAction = this.editIndicatedTherapy;
                    headers[i].actionIcon = "edit";
                    headers[i].actionTooltip = "Edit Indication";
                    break;
                }
            }
        },
        editIndicatedTherapy(item) {
            if (!this.canProceed("canReview") || this.readonly) {
                return;
            }
            this.currentEdit = item;
            this.currentEditField = "indication";
            this.currentEditTitle = "Edit Indication for " + this.currentEdit.gene + " " + this.currentEdit.variant;
            this.currentEditTextBackup = this.currentEdit[this.currentEditField];
            this.confirmationDialogVisible = true;
        },
        addCNVHeaderAction(headers) {
            for (var i = 0; i < headers.length; i++) {
                if (headers[i].value == "comment") {
                    headers[i].itemAction = this.editCNVTherapy;
                    headers[i].actionIcon = "edit";
                    headers[i].actionTooltip = "Edit Comment";
                    break;
                }
            }
        },
        editCNVTherapy(item) {
            if (!this.canProceed("canReview") || this.readonly) {
                return;
            }
            this.currentEdit = item;
            this.currentEditField = "comment";
            this.currentEditTitle = "Edit Comment for " + this.currentEdit.chrom;
            this.currentEditTextBackup = this.currentEdit[this.currentEditField];
            this.confirmationDialogVisible = true;
        },
        addTranslocationHeaderAction(headers) {
            for (var i = 0; i < headers.length; i++) {
                if (headers[i].value == "comment") {
                    headers[i].itemAction = this.editTranslocationTherapy;
                    headers[i].actionIcon = "edit";
                    headers[i].actionTooltip = "Edit Comment";
                    break;
                }
            }
        },
        editTranslocationTherapy(item) {
            if (!this.canProceed("canReview") || this.readonly) {
                return;
            }
            this.currentEdit = item;
            this.currentEditField = "comment";
            this.currentEditTitle = "Edit Comment for " + this.currentEdit.fusionName;
            this.currentEditTextBackup = this.currentEdit[this.currentEditField];
            this.confirmationDialogVisible = true;
        },
        resetReportNotes() {
            this.fullReport.summary = this.originalFullReportSummary;
        },
        reportNeedsSaving() {
            this.reportUnsaved = true;
        },
        updateFullReport() {
            var strongLabels = Object.keys(this.fullReport.snpVariantsStrongClinicalSignificance);
            for (var i = 0; i < strongLabels.length; i++) {
                for (var j = 0; j < this.strongClinicalSignificance.length; j++) {
                    if (this.strongClinicalSignificance[j].label.replace(".", "") == strongLabels[i]) {
                        this.fullReport.snpVariantsStrongClinicalSignificance[strongLabels[i]].annotation = this.strongClinicalSignificance[j].text;
                    }
                }
            }
            var possibleLabels = Object.keys(this.fullReport.snpVariantsPossibleClinicalSignificance);
            for (var i = 0; i < possibleLabels.length; i++) {
                for (var j = 0; j < this.possibleClinicalSignificance.length; j++) {
                    if (this.possibleClinicalSignificance[j].label.replace(".", "") == possibleLabels[i]) {
                        this.fullReport.snpVariantsPossibleClinicalSignificance[possibleLabels[i]].annotation = this.possibleClinicalSignificance[j].text;
                    }
                }
            }
            var unknownLabels = Object.keys(this.fullReport.snpVariantsUnknownClinicalSignificance);
            for (var i = 0; i < unknownLabels.length; i++) {
                for (var j = 0; j < this.unknownClinicalSignificance.length; j++) {
                    if (this.unknownClinicalSignificance[j].label.replace(".", "") == unknownLabels[i]) {
                        this.fullReport.snpVariantsUnknownClinicalSignificance[unknownLabels[i]].annotation = this.unknownClinicalSignificance[j].text;
                    }
                }
            }
        },
        saveReport() {
            if (this.isSaveDisabled()) {
                return;
            }
            this.savingReport = true;
            this.updateFullReport();

            console.log(this.fullReport);
            axios({
                method: 'post',
                url: webAppRoot + "/saveReport",
                params: {
                },
                data: {
                    report: this.fullReport,
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        snackBarMessage = "Report Saved";
                        snackBarVisible = true;
                        this.getExistingReports();
                        this.getReportDetails(response.data.message);
                        this.confirmationSaveDialogVisible = false;
                    } else {
                        this.handleDialogs(response.data, this.saveReport);
                        this.confirmationSaveDialogVisible = false;
                    }
                    this.savingReport = false;
                })
                .catch(error => {
                    this.savingReport = false;
                    this.confirmationSaveDialogVisible = false;
                    this.handleAxiosError(error);
                });
        },
        saveOrUpdateButtonName() {
            for (var i = 0; i < this.existingReports.length; i++) {
                if (this.fullReport.reportName == this.existingReports[i].reportName) {
                    return "Update";
                }
            }
            return "Save";
        },
        openSaveConfirmation() {
            this.confirmationSaveDialogVisible = true;
        },
        cancelSaveConfirmation() {
            this.confirmationSaveDialogVisible = false;
        },
        previewReport() {
            if (this.isSaveDisabled()) {
                return;
            }
            this.savingReport = true;
            this.updateFullReport();
            axios({
                method: 'post',
                url: webAppRoot + "/previewReport",
                params: {
                },
                data: {
                    report: this.fullReport,
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        // snackBarMessage = "Report Saved";
                        // snackBarVisible = true;
                        // this.getReportDetails();
                        console.log("done with preview"); //TODO
                        console.log(webAppRoot + "/pdfs/" + response.data.message);
                        window.open(webAppRoot + "/pdfs/" + response.data.message, "_blank");
                    } else {
                        this.handleDialogs(response.data, this.previewReport);
                    }
                    this.savingReport = false;
                })
                .catch(error => {
                    this.savingReport = false;
                    this.handleAxiosError(error);
                });
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
            this.splashProgress = 100; //should dismiss the splash dialog

        },
        parseDate(report) {
            if (report.dateModified) {
                return report.modifiedSince + " (" + report.dateModified.split("T")[0] + ")";
            }
        },
        truncatedNotes(report) {
            if (report.summary && report.summary.length > 50) {
                return report.summary.substring(0, 50) + "...";
            }
            return report.summary;
        }
    },
    mounted() {
        this.snackBarMessage = this.readonly ? "View Only Mode: some actions have been disabled" : "",
            this.snackBarVisible = this.readonly;
        if (this.readonly) {
            setTimeout(() => {
                bus.$emit("update-status", ["VIEW ONLY MODE"]);
            }, 4200); //show after snackbar is dismissed
        }
        this.getExistingReports();
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