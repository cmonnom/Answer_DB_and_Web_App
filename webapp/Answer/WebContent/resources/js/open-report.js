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
                    :label="currentEditLabel"
                    v-model="currentEdit[currentEditField]">
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

    <!--
    <v-dialog v-model="confirmationSaveDialogVisible" max-width="500px" >
        <v-toolbar dense dark :color="colors.openReport">
            <v-toolbar-title>
                Save Current Report
            </v-toolbar-title>
        </v-toolbar>
        <v-card>
            <v-card-text class="pl-2 pr-2 subheading">
                <span class="subheading">Choose a name for this report. Change the name to create a new report.</span>
                <v-text-field
                    v-model="currentReportName" class="mr-2 no-height" label="Report Name">
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
    -->

    <v-snackbar :timeout="4000" :bottom="true" v-model="snackBarVisible">
        {{ snackBarMessage }}
        <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
    </v-snackbar>

    <v-slide-y-transition>
        <report-tier-warning v-show="variantsMissingTier.length > 0"
        :variants-missing-tier="variantsMissingTier"
        @get-report-details="getReportDetails"
        @bypass-cnv-warning="bypassCNVWarning">
        </report-tier-warning>
    </v-slide-y-transition>

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

                                        <v-list-tile avatar @click="reportNotesVisible = !reportNotesVisible">
                                            <v-list-tile-avatar>
                                                <v-icon>edit</v-icon>
                                            </v-list-tile-avatar>
                                            <v-list-tile-content>
                                                <v-list-tile-title>Case Summary</v-list-tile-title>
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

                                        <v-list-tile avatar @click="copyNumberAlterationsVisible = !copyNumberAlterationsVisible">
                                            <v-list-tile-avatar>
                                                <v-icon>assignment</v-icon>
                                            </v-list-tile-avatar>
                                            <v-list-tile-content>
                                                <v-list-tile-title>Copy Number Alterations</v-list-tile-title>
                                            </v-list-tile-content>
                                        </v-list-tile>

                                        <v-list-tile avatar @click="geneFusionVisible = !geneFusionVisible">
                                            <v-list-tile-avatar>
                                                <v-icon>assignment</v-icon>
                                            </v-list-tile-avatar>
                                            <v-list-tile-content>
                                                <v-list-tile-title>Gene Fusions</v-list-tile-title>
                                            </v-list-tile-content>
                                        </v-list-tile>

                                        <v-list-tile avatar @click="pmidPanelVisible = !pmidPanelVisible">
                                        <v-list-tile-avatar>
                                            <v-icon>mdi-book-open-page-variant</v-icon>
                                        </v-list-tile-avatar>
                                        <v-list-tile-content>
                                            <v-list-tile-title>PubMed References</v-list-tile-title>
                                        </v-list-tile-content>
                                    </v-list-tile>
                                    </v-list>
                                </v-menu>
                            </v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>

                    <v-list-tile avatar @click="previewReport()"  :disabled="isPreviewDisabled()" :loading="savingReport">
                    <v-list-tile-avatar>
                        <v-icon>picture_as_pdf</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Preview Report as PDF</v-list-tile-title>
                    </v-list-tile-content>
                    </v-list-tile>

                    <v-list-tile avatar @click="openCase()">
                    <v-list-tile-avatar>
                        <v-icon>assignment_ind</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Open Case</v-list-tile-title>
                    </v-list-tile-content>
                    </v-list-tile>

                    <v-list-tile avatar @click="saveReport()"  :disabled="isSaveDisabled()">
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
            <span v-show="currentReportId">Modifying</span>
            <span v-show="!currentReportId">Creating</span>
             Report for case: {{ caseName }}
            <v-tooltip bottom>
                <v-icon slot="activator" size="20" class="pb-1"> {{ caseTypeIcon }} </v-icon>
                <span>{{caseType}} case</span>
            </v-tooltip>
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip bottom>
            <v-btn flat icon @click="existingReportsVisible = !existingReportsVisible" slot="activator" :color="existingReportsVisible ? 'amber accent-2' : ''">
                <v-icon>assignment</v-icon>
                <v-icon class="multi-icon">assignment</v-icon>
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
        <v-btn flat icon @click="previewReport" slot="activator" :disabled="isPreviewDisabled()" :loading="savingReport">
            <v-icon>picture_as_pdf</v-icon>
        </v-btn>
        <span>Preview Report as PDF</span>
       </v-tooltip>

        <v-badge color="red" right bottom overlap v-model="reportUnsaved" class="mini-badge">
        <v-icon slot="badge"></v-icon>
        <v-tooltip bottom>
        <v-btn flat icon @click="saveReport()" slot="activator" :disabled="isSaveDisabled()">
            <v-icon>save</v-icon>
        </v-btn>
        <span v-if="!isSaveDisabled()">Save Report</span>
        <span v-else>You cannot make changes to this report.<br/>It's probably finalized.</span>
       </v-tooltip>
       </v-badge>
 
        <v-progress-linear class="ml-4 mr-4" :slot="loadingReportDetails ? 'extension' : ''" v-show="loadingReportDetails"
            :indeterminate="true" color="white"></v-progress-linear>
    </v-toolbar>

    <!-- Existing Reports -->
    <v-slide-y-transition>
        <v-layout v-show="existingReportsVisible" row wrap pb-3>
            <v-flex xs12>
            <existing-reports ref="existingReports"
            @close-existing-reports="existingReportsVisible = false"
            @get-report-details="getReportDetails"
            @loading-report-details="updateLoadingReportDetails"
            :current-report-id="currentReportId"
            :readonly="readonly"
            :report-unsaved="reportUnsaved"
            @amend-report="amendReport"
            @addend-report="addendReport"
           >
            </existing-reports>
            </v-flex>
        </v-layout>
    </v-slide-y-transition>

    <!-- Patient Details -->
    <v-slide-y-transition>
        <v-layout v-show="patientDetailsVisible">
            <v-flex xs12 md12 lg10 xl9>
                <report-patient-details ref="patientDetails"
                @close-patient-details="patientDetailsVisible = false"
                @update-case-name="updateCaseName">
                </report-patient-details>
            </v-flex>
        </v-layout>
    </v-slide-y-transition>

    <!-- Case Summary -->
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
                            <v-list-tile avatar @click="resetReportNotes()" :disabled="readOnlyReportNotes()">
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
                                    <v-list-tile-title>Close Case Summary</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>
                        </v-list>
                    </v-menu>
                        
                        <v-toolbar-title  class="ml-0">Case Summary</v-toolbar-title>
                        <v-spacer></v-spacer>
                        <v-tooltip bottom>
                            <v-btn flat icon @click="resetReportNotes()" slot="activator" :disabled="readOnlyReportNotes()">
                                <v-icon>settings_backup_restore</v-icon>
                            </v-btn>
                            <span>Restore Last Saved Case Summary</span>
                        </v-tooltip>
                        <v-tooltip bottom>
                            <v-btn flat icon @click="reportNotesVisible = false" slot="activator">
                                <v-icon>close</v-icon>
                            </v-btn>
                            <span>Close Case Summary</span>
                        </v-tooltip>
                    </v-toolbar>
                    <v-card-text>
                        <v-text-field :textarea="true" v-if="!readOnlyReportNotes()"
                            v-model="fullReport.summary" class="mr-2 no-height" label="Write your comments here" @input="reportNeedsSaving()">
                        </v-text-field>
                        <div v-else>{{ fullReport.summary }}</div>
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
                    :disable-sticky-header="true" :add-row-button="true"
                    add-row-description="(for this report only. Unsaved changes will be discarded)"
                    @adding-new-row="handleNewIndicatedTherapyRow">
                </data-table>
            </div>
        </v-flex>
    </v-slide-y-transition>

    <v-slide-y-transition>
        <v-flex xs12 xl10 v-show="clinicalTrialsVisible" pb-3>
            <div>
                <data-table ref="clinicalTrials" :fixed="false" :fetch-on-created="false" table-title="Clinical Trials"
                :enable-select-all="true"
                    initial-sort="biomarker" no-data-text="No Data" :show-pagination="true" title-icon="assignment"
                    :color="colors.trials" :disable-sticky-header="true" :add-row-button="true"
                    :enable-selection="canProceed('canReview') && !readonly"
                    @datatable-selection-changed="handleSelectionChanged"
                    add-row-description="(for this report only. Unsaved changes will be discarded)"
                    @adding-new-row="handleNewTrialRow">
                </data-table>
            </div>
        </v-flex>
    </v-slide-y-transition>

    <v-slide-y-transition>
    <v-flex xs12 xl9 v-show="strongClinicalSignificanceVisible" pb-3>
        <div>
            <data-table ref="strongCS" :fixed="false" :fetch-on-created="false" table-title="Variants of Strong Clinical Significance"
                initial-sort="geneVariant" no-data-text="No Data" :show-pagination="true" title-icon="mdi-message-bulleted" :color="colors.variants"
                :disable-sticky-header="true" :add-row-button="true"
                add-row-description="(for this report only. Unsaved changes will be discarded)"
                @adding-new-row="handleNewStrongCSRow"
                :additional-headers="additionalCSHeaders">
            </data-table>
        </div>
    </v-flex>
</v-slide-y-transition>

<v-slide-y-transition>
<v-flex xs12 xl9 v-show="possibleClinicalSignificanceVisible" pb-3>
    <div>
        <data-table ref="possibleCS" :fixed="false" :fetch-on-created="false" table-title="Variants of Possible Clinical Significance"
            initial-sort="geneVariant" no-data-text="No Data" :show-pagination="true" title-icon="mdi-message-bulleted" :color="colors.variants"
            :disable-sticky-header="true" :add-row-button="true"
            add-row-description="(for this report only. Unsaved changes will be discarded)"
            @adding-new-row="handleNewPossibleCSRow"
            :additional-headers="additionalCSHeaders">
        </data-table>
    </div>
</v-flex>
</v-slide-y-transition>

<v-slide-y-transition>
<v-flex xs12 xl9 v-show="unknownClinicalSignificanceVisible" pb-3>
    <div>
        <data-table ref="unknownCS" :fixed="false" :fetch-on-created="false" table-title="Variants of Unknown Clinical Significance"
            initial-sort="geneVariant" no-data-text="No Data" :show-pagination="true" title-icon="mdi-message-bulleted" :color="colors.variants"
            :disable-sticky-header="true" :add-row-button="true"
            add-row-description="(for this report only. Unsaved changes will be discarded)"
            @adding-new-row="handleNewUnknownCSRow">
        </data-table>
    </div>
</v-flex>
</v-slide-y-transition>

    <v-slide-y-transition>
        <v-flex xs12 xl9 v-show="copyNumberAlterationsVisible" pt-3>
            <div>
                <data-table ref="copyNumberAlterations" :fixed="false" :fetch-on-created="false" table-title="Copy Number Alterations"
                    initial-sort="gene" no-data-text="No Data" :show-pagination="true" title-icon="assignment" :color="colors.cnvs"
                    :disable-sticky-header="true" :add-row-button="true"
                    add-row-description="(for this report only. Unsaved changes will be discarded)"
                    @adding-new-row="handleNewCNVRow"             >
                </data-table>
            </div>
        </v-flex>
    </v-slide-y-transition>

    <v-slide-y-transition>
        <v-flex xs12 xl9 v-show="geneFusionVisible" pt-3>
            <div>
                <data-table ref="geneFusions" :fixed="false" :fetch-on-created="false" table-title="Gene Fusions"
                    initial-sort="fusionName" no-data-text="No Data" :show-pagination="true" title-icon="assignment"
                    :color="colors.fusions" :disable-sticky-header="true" :add-row-button="true"
                    add-row-description="(for this report only. Unsaved changes will be discarded)"
                    @adding-new-row="handleNewFTLRow">
                </data-table>
            </div>
        </v-flex>
    </v-slide-y-transition>

    <v-slide-y-transition>
    <v-flex xs12 xl9 v-show="pmidPanelVisible" pt-3>
        <report-pubmed-ids :color="colors.pubmeds" ref="pubmedTable"
        :pubmeds="fullReport.pubmeds"
        @close-pmid-panel="pmidPanelVisible = false"
        @add-pubmed-reference="addPubMedReference"></report-pubmed-ids>
    </v-flex>
</v-slide-y-transition>

</div>`,
    data() {
        return {
            reportUnsaved: false,
            loadingReportDetails: false,
            patientDetailsVisible: false,
            caseName: "",
            caseType: "",
            caseTypeIcon: "",
            colors: {
                openReport: "primary",
                cnvs: "green",
                // variants: "indigo",
                variants: "pink lighten-3",
                trials: "warning",
                // fusions: "blue",
                fusions: "purple lighten-3",
                pubmeds: "indigo darken-2"
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
            currentEditLabel: "",
            currentEditTextBackup: "",
            savingReport: false,
            originalFullReportSummary: "",
            existingReportsVisible: true,
            confirmationSaveDialogVisible: false,
            currentReportId: "",
            variantsMissingTier: [],
            urlQuery: {
                reportId: null,
                test: ""
            },
            finalizeReportExists: false,
            currentReportName: "",
            pmidPanelVisible: false,
            additionalCSHeaders: [
                {
                    oneLineText: "Position",
                    value: "position",
                    width: "100px",
                    hint: ""
                },
                {
                    oneLineText: "ENST (SNP)",
                    value: "enst",
                    width: "200px",
                    hint: ""
                },
                {
                    oneLineText: "VAF (SNP)",
                    value: "vaf",
                    width: "200px",
                    hint: ""
                },
                {
                    oneLineText: "Depth (SNP)",
                    value: "depth",
                    width: "200px",
                    hint: ""
                },
                {
                    oneLineText: "Copy Number (CNV)",
                    value: "copyNumber",
                    width: "200px",
                    hint: ""
                },
                {
                    oneLineText: "Aberration Type (CNV)",
                    value: "aberrationType",
                    width: "200px",
                    hint: ""
                },]

        }
    }, methods: {
        readOnlyReportNotes() {
            return !this.canProceed('canReview') || this.readonly || this.fullReport.finalized || this.fullReport.addendum;
        },
        updateCaseName(fullCaseName) {
            this.caseName = fullCaseName;
        },
        handleRouteChanged(newRoute, oldRoute) {
            if (newRoute.path != oldRoute.path) { //prevent reloading data if only changing the query router.push({query: {test:"hello3"}})
                this.$refs.existingReports.getExistingReports();
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
            this.urlQuery.test = this.$route.query.test ? this.$route.query.test : null;
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
            this.reportUnsaved = (this.reportUnsaved || hasChanged) && !this.isSaveDisabled();
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
            return !this.canProceed('canReview') 
            || this.readonly 
            || this.savingReport 
            || !this.fullReport.reportName
            || this.fullReport.amended
            || this.fullReport.finalized;
        },
        isPreviewDisabled() {
            return this.savingReport 
            || !this.fullReport.reportName
            ;
        },
        updateLoadingReportDetails(isLoading) {
            this.loadingReportDetails = isLoading;
        },
        getReportDetails(reportId, test) {
            this.loadingReportDetails = true;
            axios.get(
                webAppRoot + "/getReportDetails",
                {
                    params: {
                        reportId: reportId,
                        caseId: this.$route.params.id,
                        test: test
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
                        this.currentReportId = this.fullReport._id ? this.fullReport._id.$oid : "";
                        this.urlQuery.reportId = this.currentReportId;
                        this.urlQuery.test = this.$route.query.test;
                        this.currentReportName = this.fullReport.reportName;
                        this.updateRoute();
                        this.variantsMissingTier = [];
                        for (var i = 0; i < this.fullReport.missingTierVariants.length; i++) {
                            var variant = this.fullReport.missingTierVariants[i];
                            this.variantsMissingTier.push({
                                id: variant._id.$oid,
                                type: variant.type.toUpperCase(),
                                chrom: formatChrom(variant.chrom),
                                notation: variant.notation,
                                name: variant.geneName
                            });
                        }
                        for (var i = 0; i < this.fullReport.missingTierCNVs.length; i++) {
                            var variant = this.fullReport.missingTierCNVs[i];
                            this.variantsMissingTier.push({
                                id: variant._id.$oid,
                                type: variant.type.toUpperCase(),
                                chrom: formatChrom(variant.chrom),
                            });
                        }
                        this.originalFullReportSummary = this.fullReport.summary;
                        this.$refs.patientDetails.patientTables = this.fullReport.patientInfo.patientTables;
                        this.$refs.patientDetails.extractPatientDetailsInfo(this.fullReport.caseName);
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

                        if (this.fullReport.snpVariantsStrongClinicalSignificanceSummary) {
                            this.$refs.strongCS.manualDataFiltered(this.fullReport.snpVariantsStrongClinicalSignificanceSummary);
                            if (this.canProceed("canReview") && !this.readonly) {
                                this.addCSHeaderAction(this.fullReport.snpVariantsStrongClinicalSignificanceSummary.headers);
                            }
                        }

                        if (this.fullReport.snpVariantsPossibleClinicalSignificanceSummary) {
                            this.$refs.possibleCS.manualDataFiltered(this.fullReport.snpVariantsPossibleClinicalSignificanceSummary);
                            if (this.canProceed("canReview") && !this.readonly) {
                                this.addCSHeaderAction(this.fullReport.snpVariantsPossibleClinicalSignificanceSummary.headers);
                            }
                        }

                        if (this.fullReport.snpVariantsUnknownClinicalSignificanceSummary) {
                            this.$refs.unknownCS.manualDataFiltered(this.fullReport.snpVariantsUnknownClinicalSignificanceSummary);
                            if (this.canProceed("canReview") && !this.readonly) {
                                this.addCSHeaderAction(this.fullReport.snpVariantsUnknownClinicalSignificanceSummary.headers);
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
                        this.pmidPanelVisible = true;
                    }
                    else {
                        this.handleDialogs(response.data, this.getReportDetails.bind(null, reportId));
                        this.loadingReportDetails = false;
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                    this.loadingReportDetails = false;
                });
        },
        handleSelectionChanged(selectedSize) {
            this.reportNeedsSaving();
        },
        handleAxiosError(error) {
            console.log(error);
            bus.$emit("some-error", [this, error]);
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
            if (!this.canProceed("canReview") || this.readonly || this.fullReport.addendum) {
                return;
            }
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
            if (!this.canProceed("canReview") || this.readonly || this.fullReport.addendum) {
                return;
            }
            this.currentEdit = item;
            this.currentEditField = "indication";
            this.currentEditTitle = "Edit Indication for " + this.currentEdit.gene + " " + this.currentEdit.variant;
            this.currentEditLabel = "";
            this.currentEditTextBackup = this.currentEdit[this.currentEditField];
            this.confirmationDialogVisible = true;
        },
        addCNVHeaderAction(headers) {
            if (!this.canProceed("canReview") || this.readonly || this.fullReport.addendum) {
                return;
            }
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
            if (!this.canProceed("canReview") || this.readonly || this.fullReport.addendum) {
                return;
            }
            this.currentEdit = item;
            this.currentEditField = "comment";
            this.currentEditTitle = "Edit Comment for " + this.currentEdit.chrom;
            this.currentEditLabel = "";
            this.currentEditTextBackup = this.currentEdit[this.currentEditField];
            this.confirmationDialogVisible = true;
        },
        addTranslocationHeaderAction(headers) {
            if (!this.canProceed("canReview") || this.readonly || this.fullReport.addendum) {
                return;
            }
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
            if (!this.canProceed("canReview") || this.readonly || this.fullReport.addendum) {
                return;
            }
            this.currentEdit = item;
            this.currentEditField = "comment";
            this.currentEditTitle = "Edit Comment for " + this.currentEdit.fusionName;
            this.currentEditLabel = "";
            this.currentEditTextBackup = this.currentEdit[this.currentEditField];
            this.confirmationDialogVisible = true;
        },
        addCSHeaderAction(headers) {
            if (!this.canProceed("canReview") || this.readonly || this.fullReport.addendum) {
                return;
            }
            for (var i = 0; i < headers.length; i++) {
                if (headers[i].value == "annotation") {
                    headers[i].itemAction = this.editCS;
                    headers[i].actionIcon = "edit";
                    headers[i].actionTooltip = "Edit Annotation";
                    break;
                }
            }
        },
        editCS(item) {
            if (!this.canProceed("canReview") || this.readonly || this.fullReport.addendum) {
                return;
            }
            this.currentEdit = item;
            this.currentEditField = "annotation";
            this.currentEditTitle = "Edit Annotation for " + this.currentEdit.geneVariant;
            this.currentEditLabel = this.currentEdit.category;
            this.currentEditTextBackup = this.currentEdit[this.currentEditField];
            this.confirmationDialogVisible = true;
        },
        resetReportNotes() {
            this.fullReport.summary = this.originalFullReportSummary;
        },
        reportNeedsSaving() {
            this.reportUnsaved = !this.isSaveDisabled();
        },
        updateRoute() {
            router.push({ query: this.urlQuery });
        },
        //populate all the fields from various parts of the form
        updateFullReport() {
            // var strongLabels = Object.keys(this.fullReport.snpVariantsStrongClinicalSignificance);
            // for (var i = 0; i < strongLabels.length; i++) {
            //     for (var j = 0; j < this.strongClinicalSignificance.length; j++) {
            //         if (this.strongClinicalSignificance[j].label.replace(".", "") == strongLabels[i]) {
            //             this.fullReport.snpVariantsStrongClinicalSignificance[strongLabels[i]].annotation = this.strongClinicalSignificance[j].text;
            //         }
            //     }
            // }
            // var possibleLabels = Object.keys(this.fullReport.snpVariantsPossibleClinicalSignificance);
            // for (var i = 0; i < possibleLabels.length; i++) {
            //     for (var j = 0; j < this.possibleClinicalSignificance.length; j++) {
            //         if (this.possibleClinicalSignificance[j].label.replace(".", "") == possibleLabels[i]) {
            //             this.fullReport.snpVariantsPossibleClinicalSignificance[possibleLabels[i]].annotation = this.possibleClinicalSignificance[j].text;
            //         }
            //     }
            // }
            // var unknownLabels = Object.keys(this.fullReport.snpVariantsUnknownClinicalSignificance);
            // for (var i = 0; i < unknownLabels.length; i++) {
            //     for (var j = 0; j < this.unknownClinicalSignificance.length; j++) {
            //         if (this.unknownClinicalSignificance[j].label.replace(".", "") == unknownLabels[i]) {
            //             this.fullReport.snpVariantsUnknownClinicalSignificance[unknownLabels[i]].annotation = this.unknownClinicalSignificance[j].text;
            //         }
            //     }
            // }
            this.fullReport.reportName = this.currentReportName;
            for (var i = 0; i < this.fullReport.patientInfo.patientTables.length; i++) {
                for (var j = 0; j < this.fullReport.patientInfo.patientTables[i].items.length; j++) {
                    var item = this.fullReport.patientInfo.patientTables[i].items[j];
                    if (item.field == "dedupPctOver100X") {
                        item.value = parseFloat((item.value + "").replace("%", ""));
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
                        this.snackBarMessage = "Report Saved";
                        this.snackBarVisible = true;
                        this.urlQuery.reportId = response.data.message;
                        this.updateRoute();
                        this.$refs.existingReports.getExistingReports();
                        this.getReportDetails(response.data.message);
                        // this.confirmationSaveDialogVisible = false;
                    } else {
                        this.handleDialogs(response.data, this.saveReport);
                        // this.confirmationSaveDialogVisible = false;
                    }
                    this.savingReport = false;
                })
                .catch(error => {
                    this.savingReport = false;
                    // this.confirmationSaveDialogVisible = false;
                    this.handleAxiosError(error);
                });
        },
        saveOrUpdateButtonName() {
            // if (this.$refs.existingReports) {
            //     for (var i = 0; i < this.$refs.existingReports.existingReports.length; i++) {
            //         if (this.fullReport.reportName == this.$refs.existingReports.existingReports[i].reportName) {
            //             return "Update";
            //         }
            //     }
            // }
            if (this.fullReport.reportName == this.currentReportName) {
                return "  Save  ";
            }
            return "Save New";
        },
        // openSaveConfirmation() {
        //     this.confirmationSaveDialogVisible = true;
        // },
        // cancelSaveConfirmation() {
        //     this.confirmationSaveDialogVisible = false;
        // },
        previewReport() {
            if (this.isPreviewDisabled()) {
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
        amendReport(reportId) {
            if (this.readonly) {
                return;
            }
            // console.log("Amending Report") + reportId;
            this.$refs.existingReports.amendingReportLoading = true;
            axios({
                method: 'post',
                url: webAppRoot + "/amendReport",
                params: {
                    reportId: reportId,
                },
                data: {
                    reason: this.$refs.existingReports.amendmentReason
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.snackBarMessage = "Report amended.";
                        this.snackBarVisible = true;
                        this.urlQuery.reportId = "";
                        this.updateRoute();
                        this.$refs.existingReports.getExistingReports();
                        this.$refs.existingReports.amendmentDialogVisible = false;
                    } else {
                        this.handleDialogs(response.data, this.amendReport.bind(this, reportId));
                    }
                    this.$refs.existingReports.amendingReportLoading = false;
                })
                .catch(error => {
                    this.$refs.existingReports.amendingReportLoading = false;
                    this.handleAxiosError(error);
                });
            this.getReportDetails();
        },
        addendReport(reportId) {
            //TODO
            if (this.readonly) {
                return;
            }
            // console.log("Addending Report") + reportId;
            this.$refs.existingReports.addendingReportLoading = true;
            axios({
                method: 'post',
                url: webAppRoot + "/addendReport",
                params: {
                    reportId: reportId,
                },
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.snackBarMessage = "Report addended.";
                        this.snackBarVisible = true;
                        this.urlQuery.reportId = response.data.message;
                        this.updateRoute();
                        this.$refs.existingReports.getExistingReports();
                        this.$refs.existingReports.addendumDialogVisible = false;
                    } else {
                        this.handleDialogs(response.data, this.addendReport.bind(this, reportId));
                    }
                    this.$refs.existingReports.addendingReportLoading = false;
                })
                .catch(error => {
                    this.$refs.existingReports.addendingReportLoading = false;
                    this.handleAxiosError(error);
                });
            this.getReportDetails();
        },
        //To create a tier 3 annotation without going back to the variant
        bypassCNVWarning(variantId) {
            var annotations = [];
            annotations.push({
                origin: "UTSW",
                text: "Uncertain Clinical Significance",
                markedForDeletion: false,
                isVisible: true,
                geneId: null,
                caseId: null,
                pmids: null,
                isTumorSpecific: false,
                userId: null,
                variantId: variantId,
                isGeneSpecific: true,
                isVariantSpecific: true,
                isCaseSpecific: false,
                isLeftSpecific: false,
                isRightSpecific: false,
                category: null,
                createdDate: null,
                modifiedDate: null,
                _id: null,
                classification: null,
                tier: "3",
                // nctIds: "",
                type: "cnv",
                cnvGenes: [],
                leftGene: null,
                rightGene: null,
                trial: null,
                drugs: "",
                warningLevel: 0,
                drugResistant: false,
                breadth: "Chromosomal",
                isSelected: true
            });
            axios({
                method: 'post',
                url: webAppRoot + "/commitAnnotations",
                params: {
                    caseId: this.$route.params.id,
                    geneId: "",
                    variantId: variantId,
                    skipAutoSelect: true
                },
                data: {
                    annotations: annotations
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.saveBypassCNVSelection(response.data.message);
                    } else {
                        this.handleDialogs(response.data, this.bypassCNVWarning.bind(null, variantId));
                    }
                })
                .catch(error => {
                    this.handleAxiosError(error);
                });
        },
        //the cnv annotation bypassing the warning needs to be selected here
        saveBypassCNVSelection(variantId) {
            axios({
                method: 'post',
                url: webAppRoot + "/selectByPassCNVWarningAnnotation",
                params: {
                    variantId: variantId,
                    caseId: this.$route.params.id,
                },
            }).then(response => {
                if (response.data.isAllowed) {
                    this.getReportDetails();
                }
                else {
                    this.handleDialogs(response.data, this.saveBypassCNVSelection.bind(null, variantId));
                }
            }).catch(error => {
                this.handleAxiosError(error);
            });
        },
        openCase() {
            var path = webAppRoot + "/openCase";
            if (this.readonly) {
                path += "ReadOnly"
            }
            path += "/" + this.$route.params.id;
            router.push({path : path});
        },
        handleNewIndicatedTherapyRow(table, newRow) {
            table.confirmAddingANewRow(newRow);
        },
        handleNewStrongCSRow(table, newRow) {
            newRow.geneVariantAsKey = newRow.geneVariant.replace(/\./g, "");
            newRow.additionalRow = true;
            newRow.csType = "strong";
            table.confirmAddingANewRow(newRow);
        },
        handleNewPossibleCSRow(table, newRow) {
            newRow.geneVariantAsKey = newRow.geneVariant.replace(/\./g, "");
            newRow.additionalRow = true;
            newRow.csType = "possible";
            table.confirmAddingANewRow(newRow);
        },
        handleNewUnknownCSRow(table, newRow) {
            newRow.geneVariantAsKey = newRow.geneVariant.replace(/\./g, "");
            newRow.additionalRow = true;
            newRow.csType = "unknown";
            table.confirmAddingANewRow(newRow);
        },
        handleNewTrialRow(table, newRow) {
            table.confirmAddingANewRow(newRow);
        },
        handleNewFTLRow(table, newRow) {
            table.confirmAddingANewRow(newRow);
        },
        handleNewCNVRow(table, newRow) {
            var isValid = true;
            //check that loci is formatted correctly
            const regex = /(chr[0-9XY]+):([0-9]+)-([0-9]+)/gmi;
            var m = regex.exec(newRow.loci);
            isValid = m != null;
            if (isValid) {
                //split loci in chr start end
                newRow.chrom = m[1];
                newRow.start = parseInt(m[2]);
                newRow.startFormatted = newRow.start.toLocaleString();
                newRow.end = parseInt(m[3]);
                newRow.endFormatted = newRow.end.toLocaleString();
                table.confirmAddingANewRow(newRow);
            }
            else {
                this.handleAxiosError("Loci should be formatted as chrXX:123456-456789");
            }
        },
        addPubMedReference(pubmedIds) {
            axios.get(
                webAppRoot + "/createPubMedFromId",
                {
                    params: {
                        pubmedIds: pubmedIds
                    }
                })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        var pubmeds = response.data.payload;
                        for (var i = 0; i < pubmeds.length; i++) {
                            this.fullReport.pubmeds.push(pubmeds[i]);
                        }
                    }
                    else {
                        this.handleDialogs(response.data, this.addPubMedReference.bind(null, pubmedIds));
                        this.loadingReportDetails = false;
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                });
        },
        test() {
            this.$refs.indicatedTherapies.newRow.drugs = "some drugs";
            this.$refs.indicatedTherapies.newRow.variant = "some variant";
            this.$refs.indicatedTherapies.newRow.level = "some level";
            this.$refs.indicatedTherapies.newRow.indication = "some indication";
            this.$refs.indicatedTherapies.addNewRow();

            this.$refs.clinicalTrials.newRow.biomarkers = "some biomarkers";
            this.$refs.clinicalTrials.newRow.drugs = "some drugs";
            this.$refs.clinicalTrials.newRow.title = "some title";
            this.$refs.clinicalTrials.newRow.phase = "some phase";
            this.$refs.clinicalTrials.newRow.contact = "some contact";
            this.$refs.clinicalTrials.newRow.location = "some location";
            this.$refs.clinicalTrials.addNewRow();

            this.$refs.strongCS.newRow.geneVariant = "BRCA1 TET2"; 
            this.$refs.strongCS.newRow.category = "My Category";
            this.$refs.strongCS.newRow.annotation = "Some annotation"; 
            this.$refs.strongCS.newRow.position = "chr1:123456"; 
            this.$refs.strongCS.newRow.copyNumber = "4";
            this.$refs.strongCS.newRow.aberrationType = "Gain";
            this.$refs.strongCS.addNewRow();

            this.$refs.possibleCS.newRow.geneVariant = "BRCA1 TET2"; 
            this.$refs.possibleCS.newRow.category = "My Category";
            this.$refs.possibleCS.newRow.annotation = "Some annotation"; 
            this.$refs.possibleCS.newRow.position = "chr1:123456"; 
            this.$refs.possibleCS.newRow.enst = "ENST123456";
            this.$refs.possibleCS.newRow.vaf = "0.14";
            this.$refs.possibleCS.newRow.depth = "1600";
            this.$refs.possibleCS.addNewRow();

            this.$refs.unknownCS.newRow.geneVariant = "BRCA1 TET2"; 
            this.$refs.unknownCS.newRow.category = "My Category";
            this.$refs.unknownCS.newRow.annotation = "Some annotation"; 
            this.$refs.unknownCS.addNewRow();

            this.$refs.copyNumberAlterations.newRow.loci = "chr12:123456-123458"; 
            this.$refs.copyNumberAlterations.newRow.copyNumber = "1"; 
            this.$refs.copyNumberAlterations.newRow.genes = "SOME GENES HERE"; 
            this.$refs.copyNumberAlterations.newRow.cytoband = "q1.2-35"; 
            this.$refs.copyNumberAlterations.newRow.comment = "some comments"; 
            this.$refs.copyNumberAlterations.addNewRow();

            this.$refs.geneFusions.newRow.fusionName = "fusion";
            this.$refs.geneFusions.newRow.leftGene = "GENE1";
            this.$refs.geneFusions.newRow.lastExon = "5";
            this.$refs.geneFusions.newRow.rightGene = "GENE2";
            this.$refs.geneFusions.newRow.firstExon = "1";
            this.$refs.geneFusions.newRow.comment = "Some Comment";
            this.$refs.geneFusions.addNewRow();

            this.$refs.pubmedTable.currentPubMedIds = "23411347,12345678";
            this.$refs.pubmedTable.addNewPubMed();


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
        this.$refs.existingReports.getExistingReports();
        splashDialog = false; //prevent splash dialog when user navigate to open case for the 1st time
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