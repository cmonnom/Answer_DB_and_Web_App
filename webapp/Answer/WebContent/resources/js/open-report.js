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

    <v-dialog v-model="confirmationSaveDialogVisible" max-width="500px" >
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

    <report-tier-warning
    :variants-missing-tier="variantsMissingTier"
    @get-report-details="getReportDetails">
    </report-tier-warning>

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
        <v-layout v-show="existingReportsVisible" row wrap pb-3>
            <v-flex xs12>
            <existing-reports ref="existingReports"
            @close-existing-reports="existingReportsVisible = false"
            @get-report-details="getReportDetails"
            @loading-report-details="updateLoadingReportDetails"
            :current-report-id="currentReportId"
            :readonly="readonly"
            :report-unsaved="reportUnsaved"
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
                    :color="colors.openReport" :disable-sticky-header="true"
                    :enable-selection="canProceed('canReview') && !readonly"
                    @datatable-selection-changed="handleSelectionChanged">
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
            existingReportsVisible: true,
            confirmationSaveDialogVisible: false,
            currentReportId: "",
            variantsMissingTier: [],
            urlQuery: {
                reportId: null
            },
            finalizeReportExists: false

        }
    }, methods: {
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
            return !this.canProceed('canReview') || this.readonly || this.savingReport || !this.fullReport.reportName;
        },
        updateLoadingReportDetails(isLoading) {
            this.loadingReportDetails = isLoading;
        },
        getReportDetails(reportId) {
            this.loadingReportDetails = true;
            axios.get(
                webAppRoot + "/getReportDetails",
                {
                    params: {
                        reportId: reportId,
                        caseId: this.$route.params.id
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
            this.reportUnsaved = !this.isSaveDisabled();
        },
        updateRoute() {
            router.push({ query: this.urlQuery });
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
                        this.urlQuery.reportId = response.data.message;
                        this.updateRoute();
                        this.$refs.existingReports.getExistingReports();
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
            if (this.$refs.existingReports) {
                for (var i = 0; i < this.$refs.existingReports.existingReports.length; i++) {
                    if (this.fullReport.reportName == this.$refs.existingReports.existingReports[i].reportName) {
                        return "Update";
                    }
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