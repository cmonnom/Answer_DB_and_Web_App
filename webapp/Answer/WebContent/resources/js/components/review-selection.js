

Vue.component('review-selection', {
    props: {
        caseName: {default: "", type: String},
        readonly: {default: true, type: Boolean},
        isSaveNeededBadgeVisible: {default: false, type: Boolean},
        breadcrumbs: {default: () => [], type: Array},

    },
    template: `
    <v-card class="soft-grey-background">
        <v-toolbar dense dark :color="primary">
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

                    <v-list-tile avatar @click="markAsReadyForReview()" :disabled="saveVariantDisabled" v-if="canProceed('canAnnotate')">
                        <v-list-tile-avatar>
                            <v-icon>how_to_reg</v-icon>
                        </v-list-tile-avatar>
                        <v-list-tile-content>
                            <v-list-tile-title>Ready for Review</v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>

                    <v-list-tile avatar @click="markAsReadyForReport()" :disabled="saveVariantDisabled" v-if="canProceed('canReview')">
                        <v-list-tile-avatar>
                            <v-icon>assignment</v-icon>
                        </v-list-tile-avatar>
                        <v-list-tile-content>
                            <v-list-tile-title>Ready for Report</v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>

                    <v-list-tile avatar @click="handleSaveAll()" :disabled="!isSaveNeededBadgeVisible">
                    <v-list-tile-avatar>
                        <v-icon>save</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Save Current Work</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>


                    <v-list-tile avatar @click="closeReviewSelectionDialog()">
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
            <v-btn icon :disabled="saveVariantDisabled" @click="markAsReadyForReview()" slot="activator">
                <v-icon>how_to_reg</v-icon>
            </v-btn>
            <span>Mark as Ready for Review. Save Selected Variants and Email reviewer(s)</span>
        </v-tooltip>
        <v-tooltip bottom v-if="canProceed('canReview')">
            <v-btn icon :disabled="saveVariantDisabled" @click="markAsReadyForReport()" slot="activator">
                <v-icon>assignment</v-icon>
            </v-btn>
            <span>Mark as Ready for Report. Save Selected Variants</span>
        </v-tooltip>
        <v-badge color="red" right bottom overlap v-model="isSaveNeededBadgeVisible" class="mini-badge">
        <v-icon slot="badge"></v-icon>
        <v-tooltip bottom offset-overflow>
            <v-btn flat icon @click="handleSaveAll()" slot="activator" :disabled="!isSaveNeededBadgeVisible"
            :loading="waitingForAjaxActive">
                <v-icon>save</v-icon>
            </v-btn>
            <span v-html="createSaveTooltip()"></span>
        </v-tooltip>
        </v-badge>
        <v-tooltip bottom>
            <v-btn icon @click="closeSaveDialog()" slot="activator">
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
                initial-sort="chromPos" no-data-text="No Data" :show-row-count="true" class="pb-3" :color="colors.saveReview">
            </data-table>
            <data-table ref="cnvVariantsSelected" :fixed="false" :fetch-on-created="false" table-title="Selected CNVs" initial-sort="chrom"
                no-data-text="No Data" :show-row-count="true" class="pb-3" :color="colors.saveReview">
            </data-table>
            <data-table ref="translocationVariantsSelected" :fixed="false" :fetch-on-created="false" table-title="Selected Translocations"
                initial-sort="fusionName" no-data-text="No Data" :show-row-count="true" class="pb-3" :color="colors.saveReview">
            </data-table>
        </v-card-text>
        <v-card-actions class="card-actions-bottom">
            <v-tooltip top>
                <v-btn color="success" :disabled="!isSaveNeededBadgeVisible" @click="handleSaveAll()" slot="activator" :loading="waitingForAjaxActive">Save Work
                    <v-icon right dark>save</v-icon>
                </v-btn>
                <span>Save Current Work</span>
            </v-tooltip>
            <v-tooltip top>
                <v-btn color="primary" :disabled="saveVariantDisabled" @click="exportSelectedVariants()" slot="activator" :loading="exportLoading">Excel
                    <v-icon right dark>mdi-file-excel</v-icon>
                </v-btn>
                <span>Export to Excel</span>
            </v-tooltip>
            <v-tooltip top>
                <v-btn color="primary" :disabled="saveVariantDisabled" @click="sendToMDA()" slot="activator" :loading="sendToMDALoading">Moclia File
                </v-btn>
                <span>Download Moclia File</span>
            </v-tooltip>
            <v-tooltip top v-if="canProceed('canAnnotate')">
                <v-btn color="primary" :disabled="saveVariantDisabled" @click="markAsReadyForReview()" slot="activator" >
                    Ready for Review
                <v-icon right dark>how_to_reg</v-icon>
                </v-btn>
                <span>Mark as Ready for Review. Save Current Work and Email reviewer(s)</span>
            </v-tooltip>
            <v-tooltip top v-if="canProceed('canReview')">
                <v-btn color="primary" :disabled="saveVariantDisabled" @click="markAsReadyForReport()" slot="activator" >
                    Ready for Report
                <v-icon right dark>assignment</v-icon>
                </v-btn>
                <span>Mark as Ready for Report. Save Current Work</span>
            </v-tooltip>
            <v-btn color="error" @click="closeSaveDialog()" slot="activator">Cancel
                <v-icon right dark>cancel</v-icon>
            </v-btn>
        </v-card-actions>
    </v-card>`,
    data() {
        return {
            saveVariantDisabled: false
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
        sendToMDA() {
            this.$emit("send-to-mda");
        },
        markAsReadyForReview() {
            this.$emit("mark-as-ready-for-review");
        },
        markAsReadyForReport() {
            this.$emit("mark-as-ready-for-report");
        },
        handleSaveAll() {
            this.$emit("save-all");
        },
        closeReviewSelectionDialog() {
            this.$emit("close-review-selection-dialog");
        },
        handleSaveAll() {
            this.$emit("save-all");
        },
        updateSelectedVariantTable() {
            var selectedSNPVariants = this.$refs.geneVariantDetails.items.filter(item => item.isSelected);
            var selectedCNVs = this.$refs.cnvDetails.items.filter(item => item.isSelected);
            var selectedTranslocations = this.$refs.translocationDetails.items.filter(item => item.isSelected);
            this.saveVariantDisabled = (selectedSNPVariants.length == 0 && selectedCNVs.length == 0 && selectedTranslocations.length == 0) || !this.canProceed('canAnnotate') || this.readonly;

            var snpHeaders = this.$refs.geneVariantDetails.headers;
            var snpHeaderOrder = this.$refs.geneVariantDetails.headerOrder;
            this.$refs.snpVariantsSelected.manualDataFiltered(
                { items: selectedSNPVariants, headers: snpHeaders, uniqueIdField: "oid", headerOrder: snpHeaderOrder });

            var cnvHeaders = this.$refs.cnvDetails.headers;
            var cnvHeaderOrder = this.$refs.cnvDetails.headerOrder;
            this.$refs.cnvVariantsSelected.manualDataFiltered(
                { items: selectedCNVs, headers: cnvHeaders, uniqueIdField: "oid", headerOrder: cnvHeaderOrder });

            var snpHeaders = this.$refs.translocationDetails.headers;
            var snpHeaderOrder = this.$refs.translocationDetails.headerOrder;
            this.$refs.translocationVariantsSelected.manualDataFiltered(
                { items: selectedTranslocations, headers: snpHeaders, uniqueIdField: "oid", headerOrder: snpHeaderOrder });
        },
        sendToMDA() {
            // There is a bug in vuetify 1.0.19 where a disabled menu still activates the click action.
            // Use a flag to disable the action in the meantime
            if (this.saveVariantDisabled) {
                return;
            }
            this.sendToMDALoading = true;
            var selectedIds = this.getSelectedVariantIds();
            axios({
                method: 'post',
                url: webAppRoot + "/sendToMDA",
                params: {
                    caseId: this.$route.params.id
                },
                data: {
                    filters: [],
                    selectedSNPVariantIds: selectedIds.selectedSNPVariantIds,
                    selectedCNVIds: selectedIds.selectedCNVIds,
                    selectedTranslocationIds: selectedIds.selectedTranslocationIds
                }
            }).then(response => {
                this.sendToMDALoading = false;
                if (response.data.isAllowed && response.data.success) {
                    this.createCSVFile(response.data.message);
                    // this.snackBarMessage = "Variants sent to MD Anderson";
                    // this.snackBarLink = "";
                    // this.snackBarVisible = true;
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
            // this.saveSelection(false, true);
            this.handleSaveAll();
            axios.get(webAppRoot + "/readyForReview", {
                params: {
                    caseId: this.$route.params.id
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.snackBarMessage = "The reviewer has been notified.";
                        this.snackBarLink = "";
                        this.snackBarVisible = true;
                    }
                    else {
                        this.handleDialogs(response.data, this.markAsReadyForReview);
                    }
                })
                .catch(error => {
                    this.handleAxiosError(error);
                });
        },
        markAsReadyForReport() {
            // this.saveSelection(false, true);
            this.handleSaveAll();
            axios.get(webAppRoot + "/readyForReport", {
                params: {
                    caseId: this.$route.params.id
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.snackBarMessage = "View/Edit Report";
                        this.snackBarLink = webAppRoot + "/openReport/" + this.$route.params.id;
                        this.snackBarLinkIcon = "assignment";
                        this.snackBarVisible = true;
                    }
                    else {
                        this.handleDialogs(response.data, this.markAsReadyForReport);
                    }
                })
                .catch(error => {
                    this.handleAxiosError(error);
                });
        },
        handleAxiosError(error) {
            console.log(error);
            bus.$emit("some-error", [this, error]);
        },
        breadcrumbNavigation(index) {
            this.$emit("breadcrumb-navigation", index);
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

