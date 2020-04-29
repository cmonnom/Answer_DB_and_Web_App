

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
        caseOwnerId: { default: "-1", type: String},
        caseOwnerName: {default: "", type: String},
        userId: { default: "-2", type: String},
        unfilteredSNPsDict: {default: () => {}, type: Object},
        unfilteredCNVsDict: {default: () => {}, type: Object},
        unfilteredFTLsDict: {default: () => {}, type: Object},
        unfilteredVIRsDict: {default: () => {}, type: Object},
        readonly: {default: false, type: Boolean},
        openReportUrl: {default: "", type: String}
    },
    template: /*html*/`<v-card class="soft-grey-background">
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

                <v-list-tile avatar :to="openReportUrl" :disabled="!reportReady">
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

        <v-breadcrumbs class="pt-2 pb-2">
            <v-icon slot="divider">forward</v-icon>
            <v-breadcrumbs-item v-for="(item, index) in breadcrumbs" :key="item.text" :disabled="disableBreadCrumbItem(item, index)"
                @click.native="breadcrumbNavigation(index)">
                {{ item.text }}
            </v-breadcrumbs-item>
        </v-breadcrumbs>

        <v-card v-show="!areReportableGeneSelected()" class="mt-1 mb-2">
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

        <v-tabs slot="extension" dark slider-color="warning" color="primary darken-1" fixed-tabs v-model="variantTabActive" hide-slider>
            <v-tab href="#tab-selected-snp" :ripple="false" active-class="v-tabs__item--active primary">
               Selected SNP / Indel
            </v-tab>
            <v-tab href="#tab-selected-cnv" :ripple="false" active-class="v-tabs__item--active primary">
               Selected CNV
            </v-tab>
            <v-tab href="#tab-selected-translocation" :ripple="false" active-class="v-tabs__item--active primary">
               Selected Fusion / Translocation
            </v-tab>
            <v-tab href="#tab-selected-virus" :ripple="false" active-class="v-tabs__item--active primary">
               Selected Virus
            </v-tab>
            <v-tabs-items v-model="variantTabActive">
            <!-- SNP / Indel table -->
        <v-tab-item value="tab-selected-snp" class="pt-1">
        
            <data-table disable-sticky-header ref="snpVariantsSelectedReviewer" :fixed="false" :fetch-on-created="false" :table-title="'SNP/Indel Variants from Case Owner ' + caseOwnerName"
            initial-sort="chromPos" no-data-text="No Data" :show-row-count="true" class="pb-3" color="primary"
            @refresh-requested="handleRefresh()">
            </data-table>  

            <v-tooltip top v-for="(otherAnnotator, index) in otherAnnotatorSNPs" :key="otherAnnotator.userId" class="pr-2" v-if="userId == caseOwnerId">
            <v-btn @click="acceptSelectionFrom('snp', otherAnnotator.userId)" slot="activator" class="mr-0 ml-0"
            :disabled="noNewVariantFromAnnotator('snp', otherAnnotator) || waitingForAjaxActive">
                Add Selection From {{ otherAnnotator.userFullName }}
            <v-icon right dark>mdi-check-all</v-icon>
            </v-btn>
            <span v-if="!noNewVariantFromAnnotator('snp', otherAnnotator)">Add {{ otherAnnotator.userFullName }}'s SNP selection to yours ({{ calcSelectionDiff('snp', otherAnnotator.userId).length }} more)</span>
            <span v-else>You have already selected all of {{ otherAnnotator.userFullName }}'s SNPs</span>
            </v-tooltip>

            <data-table disable-sticky-header ref="snpVariantsSelected" :fixed="false" :fetch-on-created="false" table-title="SNP/Indel Variants from All Annotators"
                initial-sort="chromPos" no-data-text="No Data" :show-row-count="true" class="pb-3" color="primary"
                @refresh-requested="handleRefresh()">
            </data-table>


        </v-tab-item>
        <!-- CNV table -->
        <v-tab-item value="tab-selected-cnv"  class="pt-1">
            <data-table disable-sticky-header ref="cnvVariantsSelectedReviewer" :fixed="false" :fetch-on-created="false" :table-title="'CNVs from Case Owner ' + caseOwnerName" initial-sort="chrom"
            no-data-text="No Data" :show-row-count="true" class="pb-3" color="primary"
            @refresh-requested="handleRefresh()">
            </data-table>

            <v-tooltip top v-for="(otherAnnotator, index) in otherAnnotatorCNVs" :key="otherAnnotator.userId" class="pr-2" v-if="userId == caseOwnerId">
            <v-btn @click="acceptSelectionFrom('cnv', otherAnnotator.userId)" slot="activator" class="mr-0 ml-0"
            :disabled="noNewVariantFromAnnotator('cnv', otherAnnotator)  || waitingForAjaxActive">
                Add Selection From {{ otherAnnotator.userFullName }}
            <v-icon right dark>mdi-check-all</v-icon>
            </v-btn>
            <span v-if="!noNewVariantFromAnnotator('cnv', otherAnnotator)">Add {{ otherAnnotator.userFullName }}'s CNV selection to yours ({{ calcSelectionDiff('cnv', otherAnnotator.userId).length }} more)</span>
            <span v-else>You have already selected all of {{ otherAnnotator.userFullName }}'s CNVs</span>
            </v-tooltip>

            <data-table disable-sticky-header ref="cnvVariantsSelected" :fixed="false" :fetch-on-created="false" table-title="CNVs from All Annotators" initial-sort="chrom"
                no-data-text="No Data" :show-row-count="true" class="pb-3" color="primary"
                @refresh-requested="handleRefresh()">
             </data-table>
        </v-tab-item>
        <!--  Fusion / Translocation table -->
        <v-tab-item value="tab-selected-translocation" class="pt-1">
            <data-table disable-sticky-header ref="translocationVariantsSelectedReviewer" :fixed="false" :fetch-on-created="false" :table-title="'Fusions / Translocations from Case Owner '  + caseOwnerName"
            initial-sort="fusionName" no-data-text="No Data" :show-row-count="true" class="pb-3" color="primary"
            @refresh-requested="handleRefresh()">
            </data-table>

            <v-tooltip top v-for="(otherAnnotator, index) in otherAnnotatorFTLs" :key="otherAnnotator.userId" class="pr-2" v-if="userId == caseOwnerId">
            <v-btn @click="acceptSelectionFrom('ftl', otherAnnotator.userId)" slot="activator" class="mr-0 ml-0"
            :disabled="noNewVariantFromAnnotator('ftl', otherAnnotator)  || waitingForAjaxActive">
                Add Selection From {{ otherAnnotator.userFullName }}
            <v-icon right dark>mdi-check-all</v-icon>
            </v-btn>
            <span v-if="!noNewVariantFromAnnotator('ftl', otherAnnotator)">Add {{ otherAnnotator.userFullName }}'s Fusion/Translocation selection to yours ({{ calcSelectionDiff('ftl', otherAnnotator.userId).length }} more)</span>
            <span v-else>You have already selected all of {{ otherAnnotator.userFullName }}'s Fusions/Translocations</span>
            </v-tooltip>

            <data-table disable-sticky-header ref="translocationVariantsSelected" :fixed="false" :fetch-on-created="false" table-title="Fusions / Translocations from All Annotators"
                initial-sort="fusionName" no-data-text="No Data" :show-row-count="true" class="pb-3" color="primary"
                @refresh-requested="handleRefresh()">
            </data-table>
        </v-tab-item>

        <!--  Virus table -->
        <v-tab-item value="tab-selected-virus" class="pt-1">
            <data-table disable-sticky-header ref="virusVariantsSelectedReviewer" :fixed="false" :fetch-on-created="false" :table-title="'Virus from Case Owner '  + caseOwnerName"
            initial-sort="virusName" no-data-text="No Data" :show-row-count="true" class="pb-3" color="primary"
            @refresh-requested="handleRefresh()">
            </data-table>

            <v-tooltip top v-for="(otherAnnotator, index) in otherAnnotatorVIRs" :key="otherAnnotator.userId" class="pr-2" v-if="userId == caseOwnerId">
            <v-btn @click="acceptSelectionFrom('vir', otherAnnotator.userId)" slot="activator" class="mr-0 ml-0"
            :disabled="noNewVariantFromAnnotator('vir', otherAnnotator)  || waitingForAjaxActive">
                Add Selection From {{ otherAnnotator.userFullName }}
            <v-icon right dark>mdi-check-all</v-icon>
            </v-btn>
            <span v-if="!noNewVariantFromAnnotator('vir', otherAnnotator)">Add {{ otherAnnotator.userFullName }}'s Virus selection to yours ({{ calcSelectionDiff('vir', otherAnnotator.userId).length }} more)</span>
            <span v-else>You have already selected all of {{ otherAnnotator.userFullName }}'s Viruses</span>
            </v-tooltip>

            <data-table disable-sticky-header ref="virusVariantsSelected" :fixed="false" :fetch-on-created="false" table-title="Viruses from All Annotators"
                initial-sort="fusionName" no-data-text="No Data" :show-row-count="true" class="pb-3" color="primary"
                @refresh-requested="handleRefresh()">
            </data-table>
        </v-tab-item>
        </v-tabs-items>
    </v-tabs>

    </v-card-text>
    <v-card-actions class="card-actions-bottom">
        <v-tooltip top class="pr-2">
            <v-btn color="primary" :disabled="saveVariantDisabled" @click="sendToMDA()" slot="activator" :loading="sendToMDALoading">Moclia File
            </v-btn>
            <span>Download Moclia File</span>
        </v-tooltip>
        <v-tooltip top v-if="canProceed('canAnnotate')" class="pr-2">
            <v-btn color="primary" @click="markAsReadyForReview()" slot="activator"  :loading="markingForReview">
                Ready for Review
            <v-icon right dark>how_to_reg</v-icon>
            </v-btn>
            <span>Mark as Ready for Review. Save Selected Variants and Email reviewer(s)</span>
        </v-tooltip>
        <v-tooltip top v-if="canProceed('canReview')" class="pr-2">
            <v-btn color="primary" @click="markAsReadyForReport()" slot="activator"  :loading="markingForReport">
                Ready for Report
            <v-icon right dark>assignment</v-icon>
            </v-btn>
            <span>Mark as Ready for Report. Save Selected Variants</span>
        </v-tooltip>
        <v-tooltip top class="pr-2">
            <v-btn color="success" @click="handleSaveAll()" slot="activator" :loading="waitingForAjaxActive" :disabled="!isSaveBadgeVisible">
                Save Work
            <v-icon right dark>save</v-icon>
            </v-btn>
            <span>Save Current Work</span>
        </v-tooltip>
        <v-btn class="mr-2" color="error" @click="closeReviewDialog()" slot="activator">Close
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
            variantTabActive: null,
            otherAnnotatorSNPs: [],
            variantIdSNPPerAnnotator: {},
            variantIdsSNPForCaseOwner: {},
            otherAnnotatorCNVs: [],
            variantIdCNVPerAnnotator: {},
            variantIdsCNVForCaseOwner: {},
            otherAnnotatorFTLs: [],
            variantIdFTLPerAnnotator: {},
            variantIdsFTLForCaseOwner: {},
            otherAnnotatorVIRs: [],
            variantIdVIRPerAnnotator: {},
            variantIdsVIRForCaseOwner: {}
        }
    },
    methods: {
        canProceed(field) {
            if (isAdmin) {
                return true && !this.readonly;
            }
            switch (field) {
                case "canAnnotate": return permissions.canAnnotate && !this.readonly;
                case "canSelect": return permissions.canSelect && !this.readonly;
                case "canView": return permissions.canView;
                case "canReview": return permissions.canReview && !this.readonly;
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
                    selectedTranslocationIds: this.selectedIds.selectedTranslocationIds,
                    selectedVirusIds: this.selectedIds.selectedVirusIds
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
        	 var blob = new Blob(
                     [csvContent],
                     {
                         type: "text/plain;charset=utf-8"
                     }
                 )
                 var downloadUrl = URL.createObjectURL( blob );
                 var hiddenElement = document.createElement('a');
                 hiddenElement.href = downloadUrl;
                 //hiddenElement.target = '_blank';
                 hiddenElement.download = this.$route.params.id + moment().format("_YYYY_MM_DD") + '_moclia.csv';
                 document.body.appendChild(hiddenElement);
                 hiddenElement.click();
                 document.body.removeChild(hiddenElement);
                 URL.revokeObjectURL( downloadUrl );
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
            //TODO use reviewer table
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
        updateSelectedVariantTable(selectedSNPVariants, selectedSNPVariantsReviewer, snpHeaders, snpHeaderOrderAll, snpHeaderOrderReviewer,
            selectedCNVs, selectedCNVsReviewer, cnvHeaders, cnvHeaderOrderAll, cnvHeaderOrderReviewer,
            selectedTranslocations, selectedTranslocationsReviewer, ftlHeaders, ftlHeaderOrderAll, ftlHeaderOrderReviewer,
            selectedViruses, selectedVirusesReviewer, virHeaders, virHeaderOrderAll, virHeaderOrderReviewer) {
            this.$refs.snpVariantsSelected.manualDataFiltered(
                { items: selectedSNPVariants, headers: snpHeaders, uniqueIdField: "oid", headerOrder: snpHeaderOrderAll });
            this.$refs.snpVariantsSelectedReviewer.manualDataFiltered(
                { items: selectedSNPVariantsReviewer, headers: snpHeaders, uniqueIdField: "oid", headerOrder: snpHeaderOrderReviewer });    

            this.$refs.cnvVariantsSelected.manualDataFiltered(
                { items: selectedCNVs, headers: cnvHeaders, uniqueIdField: "oid", headerOrder: cnvHeaderOrderAll });
            this.$refs.cnvVariantsSelectedReviewer.manualDataFiltered(
                { items: selectedCNVsReviewer, headers: cnvHeaders, uniqueIdField: "oid", headerOrder: cnvHeaderOrderReviewer });
    
            this.$refs.translocationVariantsSelected.manualDataFiltered(
                { items: selectedTranslocations, headers: ftlHeaders, uniqueIdField: "oid", headerOrder: ftlHeaderOrderAll });
            this.$refs.translocationVariantsSelectedReviewer.manualDataFiltered(
                { items: selectedTranslocationsReviewer, headers: ftlHeaders, uniqueIdField: "oid", headerOrder: ftlHeaderOrderReviewer });

            this.$refs.virusVariantsSelected.manualDataFiltered(
                { items: selectedViruses, headers: virHeaders, uniqueIdField: "oid", headerOrder: virHeaderOrderAll });
            this.$refs.virusVariantsSelectedReviewer.manualDataFiltered(
                { items: selectedVirusesReviewer, headers: virHeaders, uniqueIdField: "oid", headerOrder: virHeaderOrderReviewer });
            this.populateOtherAnnotators('snp', selectedSNPVariants); 
            this.populateOtherAnnotators('cnv', selectedCNVs); 
            this.populateOtherAnnotators('ftl', selectedTranslocations);  
            this.populateOtherAnnotators('vir', selectedViruses);   
            this.stopLoading();    
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
        getVirTable() {
            return this.$refs.virusVariantsSelected;
        },
        openReport() {
            this.$emit("open-report");
        },
        startLoading() {
            this.$refs.snpVariantsSelected.startLoading();
            this.$refs.snpVariantsSelectedReviewer.startLoading();
            this.$refs.cnvVariantsSelected.startLoading();
            this.$refs.cnvVariantsSelectedReviewer.startLoading();
            this.$refs.translocationVariantsSelected.startLoading();
            this.$refs.translocationVariantsSelectedReviewer.startLoading();
            this.$refs.virusVariantsSelected.startLoading();
            this.$refs.virusVariantsSelectedReviewer.startLoading();
        },
        stopLoading() {
            this.$refs.snpVariantsSelected.stopLoading();
            this.$refs.snpVariantsSelectedReviewer.stopLoading();
            this.$refs.cnvVariantsSelected.stopLoading();
            this.$refs.cnvVariantsSelectedReviewer.stopLoading();
            this.$refs.translocationVariantsSelected.stopLoading();
            this.$refs.translocationVariantsSelectedReviewer.stopLoading();
            this.$refs.virusVariantsSelected.stopLoading();
            this.$refs.virusVariantsSelectedReviewer.stopLoading();
        },
        handleRefresh() {
            this.$emit("review-selection-refresh");
        },
        acceptSelectionFrom(type, userId) {
            // let diffIds = this.calcSelectionDiff(type, userId);
            let variantIds = [];
            if (type == "snp") {
                variantIds = this.variantIdSNPPerAnnotator[userId + "_"];
            }
            else if (type == "cnv") {
                variantIds = this.variantIdCNVPerAnnotator[userId + "_"];
            }
            else if (type == "ftl") {
                variantIds = this.variantIdFTLPerAnnotator[userId + "_"];
            }
            else if (type == "vir") {
                variantIds = this.variantIdVIRPerAnnotator[userId + "_"];
            }
            this.$emit("accept-selection-from", type, userId, variantIds);
        },
        calcSelectionDiff(type, userId) {
            //remove ids in variantIds if they are in ownerVariantIds
            let diffIds = [];
            let variantIds = [];
            let caseOwnerVariantIds = [];
            let dict = {};
            if (type == "snp") {
                variantIds = this.variantIdSNPPerAnnotator[userId + "_"];
                caseOwnerVariantIds = this.variantIdsSNPForCaseOwner;
                dict = this.unfilteredSNPsDict;
            }
            else if (type == "cnv") {
                variantIds = this.variantIdCNVPerAnnotator[userId + "_"];
                caseOwnerVariantIds = this.variantIdsCNVForCaseOwner;
                dict = this.unfilteredCNVsDict;
            }
            else if (type == "ftl") {
                variantIds = this.variantIdFTLPerAnnotator[userId + "_"];
                caseOwnerVariantIds = this.variantIdsFTLForCaseOwner;
                dict = this.unfilteredFTLsDict;
            }
            else if (type == "vir") {
                variantIds = this.variantIdVIRPerAnnotator[userId + "_"];
                caseOwnerVariantIds = this.variantIdsVIRForCaseOwner;
                dict = this.unfilteredVIRsDict;
            }
            for (let i =0; i < variantIds.length; i++) {
                if (caseOwnerVariantIds.indexOf(variantIds[i]) == -1 && !dict[variantIds[i]].selected) {
                    diffIds.push(variantIds[i]);
                }
            }
            return diffIds;
        },
        populateOtherAnnotators(type, selectedVariants) {
            let variantIdPerAnnotator = {};
            let variantIdsForCaseOwner = [];
            let otherAnnotators = [];
            for (let i=0; i < selectedVariants.length; i++) {
                selectionPerAnnotator = selectedVariants[i].selectionPerAnnotator;
                let currentSelections = Object.entries(selectionPerAnnotator);
                for (let j=0; j < currentSelections.length; j++) {
                    let userId = currentSelections[j][0] + "_";
                    if (currentSelections[j][0] != (this.caseOwnerId + "")) {
                        let ids = variantIdPerAnnotator[userId];
                        if (!ids) {
                            ids = [];
                        }
                        ids.push(selectedVariants[i].oid)
                        variantIdPerAnnotator[userId] = ids;
                        if (otherAnnotators.filter(a => a.userId == currentSelections[j][0]).length == 0) {
                            otherAnnotators.push({userId: currentSelections[j][1].userId, userFullName: currentSelections[j][1].userFullName});
                        }
                    }
                    else {
                        variantIdsForCaseOwner.push(selectedVariants[i].oid);
                    }
                }
            }
            if (type == "snp") {
                this.variantIdSNPPerAnnotator = variantIdPerAnnotator;
                this.variantIdsSNPForCaseOwner = variantIdsForCaseOwner;
                this.otherAnnotatorSNPs = otherAnnotators;
            }
            else if (type == "cnv") {
                this.variantIdCNVPerAnnotator = variantIdPerAnnotator;
                this.variantIdsCNVForCaseOwner = variantIdsForCaseOwner;
                this.otherAnnotatorCNVs = otherAnnotators;
            }
            else if (type == "ftl") {
                this.variantIdFTLPerAnnotator = variantIdPerAnnotator;
                this.variantIdsFTLForCaseOwner = variantIdsForCaseOwner;
                this.otherAnnotatorFTLs = otherAnnotators;
            }
            else if (type == "vir") {
                this.variantIdVIRPerAnnotator = variantIdPerAnnotator;
                this.variantIdsVIRForCaseOwner = variantIdsForCaseOwner;
                this.otherAnnotatorVIRs = otherAnnotators;
            }
        },
        noNewVariantFromAnnotator(type, otherAnnotator) {
            let diffIds = this.calcSelectionDiff(type, otherAnnotator.userId);
            return diffIds.length == 0;
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

