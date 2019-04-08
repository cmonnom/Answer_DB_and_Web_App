

Vue.component('review-selection', {
    props: {
        breadcrumbs: {default: () => [], type: Array},
        selectedIds: {default: () => {}, type: Object},
        isSaveBadgeVisible: {default: false, type: Boolean},
        saveVariantDisabled: {default: false, type: Boolean},
        waitingForAjaxActive: {default: false, type: Boolean},
        caseName: {default: "", type: String},
        caseType: {default: "", type: String},
        caseTypeIcon: {default: "", type: String},
        saveTooltip: {default: "", type: String},
        reportReady: {default: false, type: Boolean},
    },
    template: `<v-card class="soft-grey-background">
    <v-toolbar dense dark color="primary">
        <v-menu offset-y offset-x class="ml-0">
            <v-btn slot="activator" flat icon dark>
                <v-icon>more_vert</v-icon>
            </v-btn>
            <v-list>

                <v-list-tile avatar @click="sendToMDA()" :disabled="saveVariantDisabled || sendToMDALoading">
                    <v-list-tile-avatar>
                        MDA
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Download Moclia File</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>

                <v-list-tile avatar @click="markAsReadyForReview()"  v-if="canProceed('canAnnotate')">
                    <v-list-tile-avatar>
                        <v-icon>how_to_reg</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Ready for Review</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>

                <v-list-tile avatar @click="markAsReadyForReport()" v-if="canProceed('canReview')">
                    <v-list-tile-avatar>
                        <v-icon>assignment</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Ready for Report</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>

                <v-list-tile avatar @click="handleSaveAll()" :disabled="!isSaveBadgeVisible">
                <v-list-tile-avatar>
                    <v-icon>save</v-icon>
                </v-list-tile-avatar>
                <v-list-tile-content>
                    <v-list-tile-title>Save Current Work</v-list-tile-title>
                </v-list-tile-content>
                </v-list-tile>

                <v-list-tile avatar @click="openReport()" :disabled="!reportReady">
                <v-list-tile-avatar>
                    <v-icon>assignment</v-icon>
                </v-list-tile-avatar>
                <v-list-tile-content>
                    <v-list-tile-title>Open Report</v-list-tile-title>
                </v-list-tile-content>
                </v-list-tile>

                <v-list-tile avatar @click="closeReviewDialog()">
                    <v-list-tile-avatar>
                        <v-icon>close</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Close</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
            </v-list>
        </v-menu>
        <v-toolbar-title class="ml-0">
            Review Selected Variants for {{ caseName }} 
            <v-tooltip bottom>
            <v-icon slot="activator" size="20" class="pb-1"> {{ caseTypeIcon }} </v-icon>
          <span>{{caseType}} case</span>  
          </v-tooltip>
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip bottom>
            <v-btn icon :disabled="saveVariantDisabled" @click="sendToMDA()" slot="activator" :loading="sendToMDALoading">
                MDA
            </v-btn>
            <span>Download Moclia File</span>
        </v-tooltip>
        <v-tooltip bottom v-if="canProceed('canAnnotate')">
        <v-btn icon  @click="markAsReadyForReview()" slot="activator" :loading="markingForReview">
            <v-icon>how_to_reg</v-icon>
        </v-btn>
        <span>Mark as Ready for Review. Save Selected Variants and Email reviewer(s)</span>
    </v-tooltip>
    <v-tooltip bottom v-if="canProceed('canReview')">
        <v-btn icon @click="markAsReadyForReport()" slot="activator" :loading="markingForReport">
            <v-icon>assignment</v-icon>
        </v-btn>
        <span>Mark as Ready for Report. Save Selected Variants</span>
    </v-tooltip>
    <v-badge color="red" right bottom overlap v-model="isSaveBadgeVisible" class="mini-badge">
<v-icon slot="badge"></v-icon>
<v-tooltip bottom offset-overflow nudge-left="100px" min-width="200px">
    <v-btn flat icon @click="handleSaveAll()" slot="activator" :loading="waitingForAjaxActive" :disabled="!isSaveBadgeVisible">
        <v-icon>save</v-icon>
    </v-btn>
    <span v-html="saveTooltip"></span>
</v-tooltip>
</v-badge>
        <v-tooltip bottom>
            <v-btn icon @click="closeReviewDialog()" slot="activator">
                <v-icon>close</v-icon>
            </v-btn>
            <span>Close</span>
        </v-tooltip>
    </v-toolbar>
    <v-card-text :style="getDialogMaxHeight(120)" class="pl-3 pr-3">

        <v-breadcrumbs class="pt-2">
            <v-icon slot="divider">forward</v-icon>
            <v-breadcrumbs-item v-for="(item, index) in breadcrumbs" :key="item.text" :disabled="disableBreadCrumbItem(item, index)"
                @click.native="breadcrumbNavigation(index)">
                {{ item.text }}
            </v-breadcrumbs-item>
        </v-breadcrumbs>

        <v-card v-show="!areReportableGeneSelected()" class="mt-2 mb-2">
            <v-card-text>
                The following genes should be included in the report if pathogenic or likely pathogenic :
                <v-tooltip bottom v-for="(reportGroup, index1) in requiredReportGroups" :key="index1">
                    <v-menu slot="activator" max-width="500px" offset-x>
                        <v-btn slot="activator">
                            {{ reportGroup.groupName}}
                        </v-btn>
                        <v-card>
                            <v-card-title>
                                <div class="subheading">
                                    {{ reportGroup.description }}
                                    <v-tooltip bottom>
                                        <v-btn slot="activator" flat icon @click="openLink(reportGroup.link)" color="primary">
                                            <v-icon>open_in_new</v-icon>
                                        </v-btn>
                                        <span>Open Link in New Tab</span>
                                    </v-tooltip>
                                </div>
                            </v-card-title>
                            <v-card-text>
                                <v-chip :color="isReportableGeneSelected(gene) ? 'primary' : ''" disabled v-for="(gene, index2) in reportGroup.genesToReport"
                                    :key="index2">
                                    {{ gene }}
                                </v-chip>
                            </v-card-text>
                        </v-card>
                    </v-menu>
                    <span>{{ reportGroup.description }}</span>
                </v-tooltip>

            </v-card-text>
        </v-card>
        <data-table ref="snpVariantsSelected" :fixed="false" :fetch-on-created="false" table-title="Selected SNP/Indel Variants"
            initial-sort="chromPos" no-data-text="No Data" :show-row-count="true" class="pb-3" color="primary">
        </data-table>
        <data-table ref="cnvVariantsSelected" :fixed="false" :fetch-on-created="false" table-title="Selected CNVs" initial-sort="chrom"
            no-data-text="No Data" :show-row-count="true" class="pb-3" color="primary">
        </data-table>
        <data-table ref="translocationVariantsSelected" :fixed="false" :fetch-on-created="false" table-title="Selected Translocations"
            initial-sort="fusionName" no-data-text="No Data" :show-row-count="true" class="pb-3" color="primary">
        </data-table>
    </v-card-text>
    <v-card-actions class="card-actions-bottom">
        <v-tooltip top>
            <v-btn color="primary" :disabled="saveVariantDisabled" @click="sendToMDA()" slot="activator" :loading="sendToMDALoading">Moclia File
            </v-btn>
            <span>Download Moclia File</span>
        </v-tooltip>
        <v-tooltip top v-if="canProceed('canAnnotate')">
            <v-btn color="primary" @click="markAsReadyForReview()" slot="activator"  :loading="markingForReview">
                Ready for Review
            <v-icon right dark>how_to_reg</v-icon>
            </v-btn>
            <span>Mark as Ready for Review. Save Selected Variants and Email reviewer(s)</span>
        </v-tooltip>
        <v-tooltip top v-if="canProceed('canReview')">
            <v-btn color="primary" @click="markAsReadyForReport()" slot="activator"  :loading="markingForReport">
                Ready for Report
            <v-icon right dark>assignment</v-icon>
            </v-btn>
            <span>Mark as Ready for Report. Save Selected Variants</span>
        </v-tooltip>
        <v-tooltip top>
            <v-btn color="success" @click="handleSaveAll()" slot="activator" :loading="waitingForAjaxActive" :disabled="!isSaveBadgeVisible">
                Save Work
            <v-icon right dark>save</v-icon>
            </v-btn>
            <span>Save Current Work</span>
        </v-tooltip>
        <v-btn color="error" @click="closeReviewDialog()" slot="activator">Close
            <v-icon right dark>cancel</v-icon>
        </v-btn>
    </v-card-actions>
</v-card>
   `,
    data() {
        return {
            sendToMDALoading: false,
            requiredReportGroups: [],
            markingForReview: false,
            markingForReport: false,
        }
    },
    methods: {
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
        handleAxiosError(error) {
            console.log(error);
            bus.$emit("some-error", [this, error]);
        },
        breadcrumbNavigation(index) {
            this.$emit("breadcrumb-navigation", index);
        },
        sendToMDA() {
            // There is a bug in vuetify 1.0.19 where a disabled menu still activates the click action.
            // Use a flag to disable the action in the meantime
            if (this.saveVariantDisabled) {
                return;
            }
            this.sendToMDALoading = true;
            axios({
                method: 'post',
                url: webAppRoot + "/sendToMDA",
                params: {
                    caseId: this.$route.params.id
                },
                data: {
                    filters: [],
                    selectedSNPVariantIds: this.selectedIds.selectedSNPVariantIds,
                    selectedCNVIds: this.selectedIds.selectedCNVIds,
                    selectedTranslocationIds: this.selectedIds.selectedTranslocationIds
                }
            }).then(response => {
                this.sendToMDALoading = false;
                if (response.data.isAllowed && response.data.success) {
                    this.createCSVFile(response.data.message);
                }
                else {
                    this.handleDialogs(response.data, this.sendToMDA);
                }
            }).catch(error => {
                this.sendToMDALoading = false;
                this.handleAxiosError(error);
            });
        },
        createCSVFile(csvContent) {
            var hiddenElement = document.createElement('a');
            hiddenElement.href = 'data:text/csv;charset=utf-8,' + encodeURI(csvContent);
            //hiddenElement.target = '_blank';
            hiddenElement.download = this.$route.params.id + moment().format("_YYYY_MM_DD") + '_moclia.csv';
            document.body.appendChild(hiddenElement);
            hiddenElement.click();
            document.body.removeChild(hiddenElement);

        },
        markAsReadyForReview() {
            this.markingForReview = true;
            this.saveSelection(false, true);
            axios.get(webAppRoot + "/readyForReview", {
                params: {
                    caseId: this.$route.params.id
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        var snackBarMessage = "The reviewer(s) have been notified.";
                        var snackBarLink = "";
                        this.$emit("show-snackbar", snackBarMessage, snackBarLink);
                    }
                    else {
                        this.handleDialogs(response.data, this.markAsReadyForReview);
                    }
                    this.markingForReview = false;
                })
                .catch(error => {
                    alert(error);
                    this.markingForReview = false;
                });
        },
        markAsReadyForReport() {
            this.markingForReport = true;
            this.saveSelection(false, true);
            axios.get(webAppRoot + "/readyForReport", {
                params: {
                    caseId: this.$route.params.id
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        var snackBarMessage = "View/Edit Report:";
                        var snackBarLink = webAppRoot + "/openReport/" + this.$route.params.id;
                        var snackBarLinkIcon = "assignment";
                        this.$emit("show-snackbar", snackBarMessage, snackBarLink, snackBarLinkIcon, 0);
                    }
                    else {
                        this.handleDialogs(response.data, this.markAsReadyForReport);
                    }
                    this.markingForReport = false;
                })
                .catch(error => {
                    alert(error);
                    this.markingForReport = false;
                });
        },
        saveSelection(closeAfter, skipSnackBar) {
            this.$emit("save-selection", closeAfter, skipSnackBar);
        },
        handleSaveAll() {
            this.$emit("save-all");
        },
        closeReviewDialog() {
            this.$emit("close-review-dialog");
        } ,
        //to show hide the warning in the review dialog
        areReportableGeneSelected() {
            if (!this.$refs.snpVariantsSelected || !this.reportGroups) {
                return false;
            }
            var selectedGenes = [];
            for (var i = 0; i < this.$refs.snpVariantsSelected.items.length; i++) {
                var item = this.$refs.snpVariantsSelected.items[i];
                selectedGenes.push(item.geneName);
            }
            var geneNamesToReport = [];
            for (var i = 0; i < this.reportGroups.length; i++) {
                if (this.reportGroups[i].required) {
                    var genesToReport = this.reportGroups[i].genesToReport;
                    for (var j = 0; j < genesToReport.length; j++) {
                        geneNamesToReport.push(genesToReport[j]);
                    }
                }
            }
            for (var i = 0; i < geneNamesToReport.length; i++) {
                var geneNameToReport = geneNamesToReport[i];
                if (!selectedGenes.includes(geneNameToReport)) {
                    return false;
                }
            }
            return true;
        },
        //to color the reportGroup gene if already selected
        isReportableGeneSelected(gene) {
            for (var i = 0; i < this.$refs.snpVariantsSelected.items.length; i++) {
                var item = this.$refs.snpVariantsSelected.items[i].geneName;
                if (item == gene) {
                    return true;
                }
            }
            return false;
        },
        disableBreadCrumbItem(item, index) {
            return (item.disabled || index == this.breadcrumbs.length - 1);
        },
        getDialogMaxHeight(offset) {
            getDialogMaxHeight(offset);
        },
        updateSelectedVariantTable(selectedSNPVariants, snpHeaders, snpHeaderOrder, 
            selectedCNVs, cnvHeaders, cnvHeaderOrder, 
            selectedTranslocations, ftlHeaders, ftlHeaderOrder) {
            this.$refs.snpVariantsSelected.manualDataFiltered(
                { items: selectedSNPVariants, headers: snpHeaders, uniqueIdField: "oid", headerOrder: snpHeaderOrder });

            this.$refs.cnvVariantsSelected.manualDataFiltered(
                { items: selectedCNVs, headers: cnvHeaders, uniqueIdField: "oid", headerOrder: cnvHeaderOrder });

            this.$refs.translocationVariantsSelected.manualDataFiltered(
                { items: selectedTranslocations, headers: ftlHeaders, uniqueIdField: "oid", headerOrder: ftlHeaderOrder });
        },
        getSnpTable() {
            return this.$refs.snpVariantsSelected;
        },
        getCnvTable() {
            return this.$refs.cnvVariantsSelected;
        },
        getFtlTable() {
            return this.$refs.translocationVariantsSelected;
        },
        openReport() {
            this.$emit("open-report");
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

