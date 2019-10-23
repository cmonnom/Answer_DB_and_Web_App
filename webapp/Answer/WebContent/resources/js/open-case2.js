const OpenCase2 = {
    props: {
        "readonly": { default: true, type: Boolean },
        loadingColor: {default:"blue-grey lighten-4", type: String},
        flt3ITDLocus: {default:"chr13:28,033,867-28,034,235", type: String},
        confirmationMessage: {default:"Unsaved selected variants will be discarded.<br/>Are you sure?", type: String},
        confirmationProceedButton: {default:"Proceed", type: String},
        confirmationCancelButton: {default:"Cancel", type: String},

    },
    template: `<div>

    <!-- splash screen dialog -->
    <splash-screen ref="splashScreen" :splash-dialog="splashDialog" ></splash-screen>


    <v-dialog v-model="confirmationDialogVisible" max-width="500px">
        <v-card>
            <v-card-text v-html="confirmationMessage" class="pl-2 pr-2 subheading">

            </v-card-text>
            <v-card-actions class="card-actions-bottom">
                <v-btn class="mr-2" color="primary" @click="proceedWithConfirmation" slot="activator">{{ confirmationProceedButton }}
                </v-btn>
                <v-btn class="mr-2" color="error" @click="cancelConfirmation" slot="activator">{{ confirmationCancelButton }}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>

    <!-- create ITD dialog -->
    <v-dialog v-model="itdDialogVisible" max-width="300px" v-if="canProceed('canSelect') && !readonly" persistent>
    <create-itd
    @hide-create-itd="itdDialogVisible = false"
    @refresh-variants="getAjaxData"
    @show-snackbar="showSnackBarMessageWithParams"></create-itd>
    </v-dialog>

    <!-- variant details dialog -->
        <variant-details-dialog :colors="colors" ref="variantDetailsDialog" 
        :isSaveNeededOverall="saveAllNeeded"
        :saveTooltip="saveTooltip"
        :caseName="caseName"
        :caseTypeIcon="caseTypeIcon"
        :caseType="caseType"
        :isSaveLoading="waitingForAjaxActive"
        :readonly="readonly"
        :cnvChromList="cnvChromList"
        @show-snackbar="showSnackBarMessage"
        @close-variant-details="closeVariantDetailsExternally"
        :loadingColor="loadingColor"
        @updateEditAnnotationBreadcrumbs="updateEditAnnotationBreadcrumbs"
        @breadcrumb-navigation="breadcrumbNavigation"
        :breadcrumbs="breadcrumbs"
        :variantDetailsVisible=variantDetailsVisible
        :urlQuery="urlQuery"
        @annotation-selection-changed="handleAnnotationSelectionChanged"
        @handle-save-all="handleSaveAll"
        @annotation-selection-saved="handleAnnotationSelectionSaved"
        @load-prev-variant="loadPrevVariant"
        @load-next-variant="loadNextVariant"
        @selection-changed="handleSelectionChangeFromVariantDetails"
        :unfilteredSNPsDict="unfilteredSNPsDict"
        :unfilteredCNVsDict="unfilteredCNVsDict"
        :unfilteredFTLsDict="unfilteredFTLsDict"
        @download-igv-file="downloadIGVFile"
        @refresh-variant-tables="getAjaxData"
        >
        </variant-details-dialog>

   
    <!-- FPKM plot drawer -->
    <v-menu
        v-model="fpkmVisible"
        :close-on-content-click="false"
        :close-on-click="false"
        :position-x="fpkmPositionx"  :position-y="fpkmPositiony"
        absolute
      >
      <fpkm-plot ref="fpkmPlot"
      :can-plot="patientDetailsOncoTreeDiagnosis.text != null"
      :oncotree="oncotree"
      @hide-fpkm-plot="closeFPKMChart"
      ></fpkm-plot>
      </v-menu>  
   
    <v-snackbar :timeout="snackBarTimeout" :bottom="true" right v-model="snackBarVisible">
        {{ snackBarMessage }}
        <v-tooltip top>
        <a slot="activator" :href="snackBarLink"><v-icon dark>{{ snackBarLinkIcon }}</v-icon></a>
        <span>Open Link</span>
        </v-tooltip>
        <v-btn flat color="primary" @click="snackBarVisible = false">Close</v-btn>
    </v-snackbar>

    <!-- advanced filter-->
    <advanced-filter ref="advancedFilter" @refresh-data="filterData"
        :type="currentFilterType"
        @update-highlight="updateHighlights"></advanced-filter>

    <!-- review selection dialog -->
    <v-dialog v-model="reviewDialogVisible" scrollable fullscreen hide-overlay persistent transition="dialog-bottom-transition">
        <review-selection ref="reviewDialog"
        @open-report="openReport"
        :report-ready="reportReady"
        :breadcrumbs="breadcrumbs"
        :case-name="caseName" :case-type="caseType" :case-type-icon="caseTypeIcon"
        @save-selection="saveSelection" @close-review-dialog="closeReviewDialog"
        :is-save-badge-visible="isSaveNeededBadgeVisible()" :save-variant-disabled="saveVariantDisabled"
        @save-all="handleSaveAll" :waiting-for-ajax-active="waitingForAjaxActive" @show-snackbar="showSnackBarMessageWithParams"
        :save-tooltip="createSaveTooltip()"
        :case-owner-id="caseOwnerId"
        :case-owner-name="caseOwnerName"
        :user-id="userId + ''"
        @review-selection-refresh="updateSelectedVariantTable()"
        :unfilteredSNPsDict="unfilteredSNPsDict"
        :unfilteredCNVsDict="unfilteredCNVsDict"
        :unfilteredFTLsDict="unfilteredFTLsDict"
        @accept-selection-from="addOtherAnnotatorSelection"></review-selection>
    </v-dialog>


    <v-toolbar dense dark :color="colors.openCase" fixed app :extended="loadingVariantDetails">
    <v-tooltip class="ml-0" bottom>
        <v-menu offset-y offset-x slot="activator" class="ml-0">
            <v-btn slot="activator" flat icon dark>
                <v-icon>more_vert</v-icon>
            </v-btn>
            <v-list>
                <v-list-tile avatar @click="patientDetailsVisible = !patientDetailsVisible" :disabled="patientTables.length == 0">
                    <v-list-tile-avatar>
                        <v-icon>assignment_ind</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Show/Hide Patient Details</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>

                <v-list-tile avatar @click="caseAnnotationsVisible = !caseAnnotationsVisible" :disabled="patientTables.length == 0">
                    <v-list-tile-avatar>
                        <v-icon>mdi-message-bulleted</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Show/Hide Case Notes</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>

                <v-list-tile avatar v-if="qcUrl" @click="openLink(qcUrl)" :disabled="!qcUrl">
                    <v-list-tile-avatar>
                        QC
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Open QC in NuCLIA</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>

                <v-list-tile avatar v-if="mutationalSignatureUrl" @click="openLink(mutationalSignatureUrl)" :disabled="!mutationalSignatureUrl">
                <v-list-tile-avatar>
                <v-icon>mdi-chart-histogram</v-icon>
                </v-list-tile-avatar>
                <v-list-tile-content>
                    <v-list-tile-title>Open Mutational Signature (MuSiCa)</v-list-tile-title>
                </v-list-tile-content>
                </v-list-tile>

                <v-list-tile avatar @click="openFPKMChart">
                <v-list-tile-avatar>
                <img alt="boxplot icon" class="alpha-54" :src="webAppRoot + '/resources/images/boxplot_icon_black.png'" width="16px" />
                </v-list-tile-avatar>
                <v-list-tile-content>
                    <v-list-tile-title>Open FPKM Plot</v-list-tile-title>
                </v-list-tile-content>
                </v-list-tile>

                <v-list-tile class="list-menu">
                        <v-list-tile-content>
                            <v-list-tile-title>
                                <v-menu offset-y offset-x close-delay="500" open-on-hover>
                                        <span slot="activator">
                                            <v-icon class="pl-2 pr-4">keyboard_arrow_right</v-icon>IGV
                                            <!-- This is a hack to extend the menu active area because the title is much shorter than other items -->
                                            <span v-for="i in 30" :key="i">&nbsp;</span>
                                        </span>
                                        <v-list>
                                <v-list-tile avatar @click="openBamViewerLinkWebFLT3()">
                                        <v-list-tile-avatar>
                                            <v-icon>mdi-web</v-icon>
                                        </v-list-tile-avatar>
                                        <v-list-tile-content class="mb-2">
                                            <v-list-tile-title>Open FLT3 locus (web)</v-list-tile-title>
                                        </v-list-tile-content>
                                    </v-list-tile>

                                    <v-list-tile avatar @click="downloadIGVFile('jnlp', flt3ITDLocus)">
                                    <v-list-tile-avatar>
                                        <v-icon>mdi-desktop-mac-dashboard</v-icon>
                                    </v-list-tile-avatar>
                                    <v-list-tile-content  class="mb-2">
                                        <v-list-tile-title>Open FLT3 locus (desktop)</v-list-tile-title>
                                    </v-list-tile-content>
                                    </v-list-tile>

                                    <v-list-tile avatar @click="downloadIGVFile('session', flt3ITDLocus)">
                                    <v-list-tile-avatar>
                                        <v-icon>mdi-file-xml</v-icon>
                                    </v-list-tile-avatar>
                                    <v-list-tile-content  class="mb-2">
                                        <v-list-tile-title>Download IGV Session for FLT3 locus</v-list-tile-title>
                                    </v-list-tile-content>
                                    </v-list-tile>

                                    <v-list-tile v-if="isSNP()" avatar @click="downloadIGVFile('sessionLink', flt3ITDLocus)">
                                    <v-list-tile-avatar>
                                        IGV
                                    </v-list-tile-avatar>
                                    <v-list-tile-content  class="mb-2">
                                        <v-list-tile-title>Show IGV Session Link <v-icon>mdi-link-variant</v-icon></v-list-tile-title>
                                    </v-list-tile-content>
                                    </v-list-tile>

                                </v-list>
                            </v-menu> 
                        </v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>   

                <v-list-tile avatar @click="openReviewSelectionDialog()">
                    <v-list-tile-avatar>
                        <v-icon>mdi-clipboard-check</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Review Variants Selected</v-list-tile-title>
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

                <v-list-tile avatar @click="handleSaveAll()" :disabled="!isSaveNeededBadgeVisible()">
                <v-list-tile-avatar>
                    <v-icon>save</v-icon>
                </v-list-tile-avatar>
                <v-list-tile-content>
                    <v-list-tile-title>Save Current Work</v-list-tile-title>
                </v-list-tile-content>
            </v-list-tile>
            </v-list>
        </v-menu>
        <span>Case Menu</span>
    </v-tooltip>
    <v-toolbar-title class="white--text ml-0">
    Working on case: {{ caseName }} 
    <v-tooltip bottom>
          <v-icon slot="activator" :size="caseTypeIconSize" class="icon-align-cancel"> {{ caseTypeIcon }} </v-icon>
        <span>{{caseType}} case</span>  
        </v-tooltip>
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <v-tooltip bottom>
        <v-btn :disabled="patientTables.length == 0" flat icon @click="patientDetailsVisible = !patientDetailsVisible" slot="activator"
            :color="patientDetailsVisible ? 'amber accent-2' : ''">
            <!-- <v-icon>perm_identity</v-icon> -->
            <v-icon>assignment_ind</v-icon>
        </v-btn>
        <span>Show/Hide Patient Details</span>
    </v-tooltip>
    <v-tooltip bottom>
        <v-btn flat icon @click="caseAnnotationsVisible = !caseAnnotationsVisible" :color="caseAnnotationsVisible ? 'amber accent-2' : ''"
            slot="activator">
            <v-icon>mdi-message-bulleted</v-icon>
        </v-btn>
        <span>Show/Hide Case Notes</span>
    </v-tooltip>
    <v-tooltip bottom>
        <v-btn icon flat slot="activator" :href="qcUrl" target="_blank" rel="noreferrer" :disabled="!qcUrl">
            QC
        </v-btn>
        <span>Open QC in NuCLIA</span>
    </v-tooltip>
    <v-tooltip bottom>
    <v-btn icon flat slot="activator" :href="mutationalSignatureUrl" target="_blank" rel="noreferrer" :disabled="!mutationalSignatureUrl">
    <v-icon>mdi-chart-histogram</v-icon>
    </v-btn>
    <span>Open Mutational Signature (MuSiCa)</span>
</v-tooltip>
<v-tooltip bottom>
    <v-btn icon flat slot="activator" @click="openFPKMChart">
    <img alt="boxplot icon" :src="webAppRoot + '/resources/images/boxplot_icon_white.png'" width="36px" />
    </v-btn>
    <span>Open FPKM Plot</span>
</v-tooltip>

<v-menu origin="center center" transition="slide-y-transition" bottom open-on-hover offset-y >
<v-btn icon flat slot="activator">IGV
</v-btn>
<v-card color="primary">
<v-tooltip bottom >
    <v-btn ref="bamViewerLinkFLT3" dark icon flat slot="activator" :href="createBamViewerLinkFLT3()" target="_blank" rel="noreferrer">
        <v-icon>mdi-web</v-icon> 
    </v-btn>
    <span>Open FLT3 locus (web)</span>
</v-tooltip>
<br/>
<v-tooltip bottom >
    <v-btn  dark icon flat slot="activator" @click="downloadIGVFile('jnlp', flt3ITDLocus)">
        <v-icon>mdi-desktop-mac-dashboard</v-icon>
    </v-btn>
    <span>Open FLT3 locus (desktop)</span>
</v-tooltip>
<br/>
<v-tooltip bottom >
    <v-btn dark icon flat slot="activator" @click="downloadIGVFile('session', flt3ITDLocus)">
        <v-icon>mdi-file-xml</v-icon>
    </v-btn>
    <span>Download IGV Session for FLT3 locus</span>
</v-tooltip>
<br/>
<v-tooltip bottom >
    <v-btn dark icon flat slot="activator" @click="downloadIGVFile('sessionLink', flt3ITDLocus)">
        <v-icon>mdi-link-variant</v-icon>
    </v-btn>
    <span>Show IGV Session Link for FLT3 locus</span>
</v-tooltip>
</v-card>
</v-menu>

        <v-tooltip bottom>
            <v-btn flat icon @click="openReviewSelectionDialog()" slot="activator" :color="reviewDialogVisible ? 'amber accent-2' : ''">
                <v-icon>mdi-clipboard-check</v-icon>
            </v-btn>
            <span>Review Variants Selected</span>
        </v-tooltip>
    <v-badge color="red" right bottom overlap v-model="isSaveNeededBadgeVisible()" class="mini-badge">
    <v-icon slot="badge"></v-icon>
    <v-tooltip bottom offset-overflow nudge-left="100px" min-width="200px">
        <v-btn flat icon @click="handleSaveAll()" slot="activator" :disabled="!isSaveNeededBadgeVisible()">
            <v-icon>save</v-icon>
        </v-btn>
        <span v-html="saveTooltip"></span>
    </v-tooltip>
    </v-badge>
    <v-progress-linear class="ml-4 mr-4" :slot="loadingVariantDetails ? 'extension' : ''" v-show="(!caseName || loadingVariantDetails) && !splashDialog"
        :indeterminate="true" color="white"></v-progress-linear>
</v-toolbar>

<v-breadcrumbs class="pt-2">
    <v-icon slot="divider">forward</v-icon>
    <v-breadcrumbs-item v-for="(item, index) in breadcrumbs" :key="item.text" :disabled="disableBreadCrumbItem(item, index)"
        @click.native="breadcrumbNavigation(index)">
        {{ item.text }}
    </v-breadcrumbs-item>
</v-breadcrumbs>

<v-slide-y-transition>
    <v-layout v-if="patientDetailsVisible">
        <v-flex xs12 md12 lg12 xl10>
            <div class="text-xs-center pb-3">
                <v-card>
                    <v-toolbar class="elevation-0" dense dark :color="colors.openCase">
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
                        <v-toolbar-title>Patient Details</v-toolbar-title>
                        <v-spacer></v-spacer>
                        <v-tooltip bottom>
                            <v-btn flat icon @click="patientDetailsVisible = false" slot="activator">
                                <v-icon>close</v-icon>
                            </v-btn>
                            <span>Close Details</span>
                        </v-tooltip>
                    </v-toolbar>
                    <v-container grid-list-md fluid pl-2 pr-2 pt-2 pb-2>
                        <v-layout row wrap>
                            <v-flex xs4 v-for="table in patientTables" :key="table.name">
                                <v-card flat>
                                    <v-card-text>
                                        <v-list class="dense-tiles">
                                            <v-list-tile v-for="item in table.items" :key="item.label" class="pl-0 pr-0 pb-2 no-tile-padding">
                                                <v-list-tile-content class="pl-0 pr-0">
                                                    <v-layout class="full-width " justify-space-between align-top>
                                                        <v-flex :class="[getPatientDetailsMarginClass(item), 'text-xs-left', 'xs']">
                                                            <span :class="[item.type == 'text' ? 'pt-4' : '', 'selectable']">{{ item.label }}:</span>
                                                        </v-flex>
                                                        <v-flex :class="[getPatientDetailsFlexClass(item),'text-xs-right', '', 'blue-grey--text', 'text--lighten-1']">
                                                            <span v-if="item.type == null" class="selectable">{{ item.value }}</span>
                                                            <v-tooltip bottom>
                                                            <v-autocomplete class="pt-0" slot="activator" :disabled="!canProceed('canAnnotate') || readonly" v-if="item.type == 'text' && item.field == 'oncotree'" 
                                                            v-model="patientDetailsOncoTreeDiagnosis" :items="oncotree" single-line return-object
                                                            item-text="text" item-value="text" hide-details @input="patientDetailsUnSaved = true">
                                                            </v-autocomplete>
                                                            <span> {{ patientDetailsOncoTreeDiagnosis.label }}</span>
                                                            </v-tooltip>
                                                            <v-textarea class="align-input-right pt-0 mt-0" :disabled="!canProceed('canAnnotate') || readonly" v-if="item.type == 'text-field' && item.field == 'tumorTissueType'" v-model="patientDetailsTumorTissue"
                                                            label="Tumor Tissue Type" single-line @input="patientDetailsUnSaved = true" hide-details rows="1" auto-grow>
                                                            </v-textarea>
                                                            <v-textarea class="align-input-right pt-0 mt-0" :disabled="!canProceed('canAnnotate') || readonly" v-if="item.type == 'text-field' && item.field == 'icd10'" v-model="patientDetailsICD10"
                                                            label="ICD10 Code" single-line @input="patientDetailsUnSaved = true" hide-details rows="1" auto-grow>
                                                            </v-textarea>
                                                            <v-text-field class="no-top-text-field align-input-right" :disabled="!canProceed('canAnnotate') || readonly" v-if="item.type == 'text-field' && item.field == 'dedupPctOver100X'" v-model="patientDetailsDedupPctOver100X"
                                                            label="Numbers Only" :rules="numberRules" single-line @input="patientDetailsUnSaved = true" hide-details>
                                                            </v-text-field>
                                                            <v-text-field class="no-top-text-field align-input-right" :disabled="!canProceed('canAnnotate') || readonly" v-if="item.type == 'text-field' && item.field == 'dedupAvgDepth'" v-model="patientDetailsDedupAvgDepth"
                                                            label="Numbers Only" :rules="numberRules" single-line @input="patientDetailsUnSaved = true" hide-details>
                                                            </v-text-field>
                                                            <v-text-field class="no-top-text-field align-input-right" :disabled="!canProceed('canAnnotate') || readonly" v-if="item.type == 'text-field' && item.field == 'tumorPercent'" v-model="patientDetailsTumorPercent"
                                                            label="Numbers Only" :rules="numberRules" single-line @input="patientDetailsUnSaved = true" hide-details>
                                                            </v-text-field>
                                                        </v-flex>
                                                        <v-flex xs4 v-if="item.type == 'text' && item.field == 'oncotree'" class="align-flex-right">
                                                            <v-tooltip bottom>
                                                                <v-btn flat color="primary" icon @click="openOncoTree()" slot="activator" class="mr-0 ml-0 mt-0 mb-0">
                                                                    <img alt="oncotree icon" :src="oncotreeIconUrl" width="24px"></img>
                                                                </v-btn>
                                                                <span>Open OncoTree in New Tab</span>
                                                            </v-tooltip>
                                                            <v-tooltip bottom>
                                                            <v-btn flat color="primary" icon @click="openOncoKBGeniePortalCancer()" slot="activator" class="mr-0 ml-0 mt-0 mb-0">
                                                                <v-icon>mdi-dna</v-icon>
                                                            </v-btn>
                                                            <span>Open Lookup Portal in New Tab</span>
                                                            </v-tooltip>
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

<v-slide-y-transition>
    <v-layout v-if="caseAnnotationsVisible">
        <v-flex xs12 md12 lg12 xl10 class="pb-3">
            <v-card>
                <v-toolbar class="elevation-0" dense dark :color="colors.openCase">
                    <!-- <v-icon>perm_identity</v-icon> -->
                    <v-icon :color="caseAnnotationsVisible ? 'amber accent-2' : ''">mdi-message-bulleted</v-icon>
                    <v-toolbar-title>Case Notes</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-tooltip bottom>
                        <v-btn flat icon @click="caseAnnotationsVisible = false" slot="activator">
                            <v-icon>close</v-icon>
                        </v-btn>
                        <span>Close Annotations</span>
                    </v-tooltip>
                </v-toolbar>
                <v-card-text class="pl-3">
                    <div v-if="labNotes" class="subheading selectable pl-1 pr-2 pt-2 pb-2">
                    <span>Lab Notes:</span>
                    <span class="blue-grey--text text--lighten-1">{{ labNotes }}</span>
                    </div>
                    <v-textarea :readonly="!canProceed('canAnnotate') || readonly" :disabled="!canProceed('canAnnotate') || readonly" hide-details
                        ref="caseNotes" v-model="caseAnnotation.caseAnnotation" @input="caseNotesChanged = true" class="mr-2 no-height" label="Write your comments here">
                    </v-textarea>
                </v-card-text>
            </v-card>
        </v-flex>
    </v-layout>
</v-slide-y-transition>

<v-slide-y-transition>
    <v-tabs slot="extension" dark slider-color="amber accent-2" color="primary darken-1" fixed-tabs v-model="variantTabActive" v-show="variantTabsVisible" hide-slider>
        <v-tab href="#tab-snp" :ripple="false" active-class="v-tabs__item--active primary">SNP / Indel</v-tab>
        <v-tab href="#tab-cnv" :ripple="false" active-class="v-tabs__item--active primary">CNV</v-tab>
        <v-tab href="#tab-translocation" :ripple="false" active-class="v-tabs__item--active primary">Fusion / Translocation</v-tab>
        <v-tabs-items>
            <!-- SNP / Indel table -->
            <v-tab-item value="tab-snp">
                <data-table ref="geneVariantDetails" :fixed="false" :fetch-on-created="false" table-title="SNP/Indel Variants" initial-sort="chromPos"
                    no-data-text="No Data" :enable-selection="canProceed('canSelect') && !readonly" :show-row-count="true"
                    @refresh-requested="handleRefresh()" :show-left-menu="true"
                    id-type="SNP"
                    @datatable-selection-changed="handleSelectionChanged" :color="colors.openCase" :external-filtering-active="isFilteringActiveForType('snp', 'geneVariantDetails')"
                    >
                    <v-fade-transition slot="action1">
                        <v-tooltip bottom >
                            <v-btn slot="activator" flat icon @click="toggleFilters('snp')" :color="isAdvancedFilteringVisible() ? 'amber accent-2' : 'white'">
                                <v-icon>filter_list</v-icon>
                            </v-btn>
                            <span>Advanced Filtering</span>
                        </v-tooltip>
                    </v-fade-transition>
                    <v-list-tile avatar @click="toggleFilters('snp')" slot="action1MenuItem">
                        <v-list-tile-avatar>
                            <v-icon>filter_list</v-icon>
                        </v-list-tile-avatar>
                        <v-list-tile-content>
                            <v-list-tile-title>Advanced Filtering</v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>

                </data-table>
            </v-tab-item>
            <!-- CNV table -->
            <v-tab-item value="tab-cnv">
                <data-table ref="cnvDetails" :fixed="false" :fetch-on-created="false" table-title="CNVs" initial-sort="chrom" no-data-text="No Data"
                    :enable-selection="canProceed('canSelect') && !readonly" :show-row-count="true" @refresh-requested="handleRefresh()"
                    :show-left-menu="true" @datatable-selection-changed="handleSelectionChanged" :color="colors.openCase"
                    id-type="CNV"
                    :highlights="highlights" :external-filtering-active="isFilteringActiveForType('cnv', 'cnvDetails')">
                    <v-fade-transition slot="action1">
                        <v-tooltip bottom >
                            <v-btn slot="activator" flat icon @click="toggleFilters('cnv')" :color="isAdvancedFilteringVisible() ? 'amber accent-2' : 'white'">
                                <v-icon>filter_list</v-icon>
                            </v-btn>
                            <span>Advanced Filtering</span>
                        </v-tooltip>
                    </v-fade-transition>
                    <v-list-tile avatar @click="toggleFilters('cnv')" slot="action1MenuItem">
                        <v-list-tile-avatar>
                            <v-icon>filter_list</v-icon>
                        </v-list-tile-avatar>
                        <v-list-tile-content>
                            <v-list-tile-title>Advanced Filtering</v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>
                    <v-fade-transition slot="action2">
                    <v-tooltip bottom >
                        <v-btn slot="activator" flat icon @click="getCNVDetailsNoVariant()">
                            <v-icon>zoom_in</v-icon>
                        </v-btn>
                        <span>Open CNV (no variant)</span>
                    </v-tooltip>
                    </v-fade-transition>
                    <v-list-tile avatar @click="getCNVDetailsNoVariant()" slot="action2MenuItem">
                        <v-list-tile-avatar>
                            <v-icon>zoom_in</v-icon>
                        </v-list-tile-avatar>
                        <v-list-tile-content>
                            <v-list-tile-title>Open CNV (no variant)</v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>
                    <v-list-tile avatar @click="openIDTCreationDialog()" slot="action3MenuItem">
                    <v-list-tile-avatar>
                        ITD
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Create a New ITD</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
                </data-table>
            </v-tab-item>
            <!--  Fusion / Translocation table -->
            <v-tab-item value="tab-translocation">
                <data-table ref="translocationDetails" :fixed="false" :fetch-on-created="false" table-title="Fusions / Translocations" initial-sort="fusionName"
                    no-data-text="No Data" :enable-selection="canProceed('canSelect') && !readonly" :show-row-count="true" @refresh-requested="handleRefresh()"
                    id-type="FTL"
                    :show-left-menu="true" @datatable-selection-changed="handleSelectionChanged" :color="colors.openCase" :external-filtering-active="isFilteringActiveForType('ftl', 'translocationDetails')">
                    <v-fade-transition slot="action1">
                    <v-tooltip bottom >
                        <v-btn slot="activator" flat icon @click="toggleFilters('ftl')" :color="isAdvancedFilteringVisible() ? 'amber accent-2' : 'white'">
                            <v-icon>filter_list</v-icon>
                        </v-btn>
                        <span>Advanced Filtering</span>
                    </v-tooltip>
                </v-fade-transition>
                <v-list-tile avatar @click="toggleFilters('ftl')" slot="action1MenuItem">
                    <v-list-tile-avatar>
                        <v-icon>filter_list</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Advanced Filtering</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
                </data-table>
            </v-tab-item>
        </v-tabs-items>
    </v-tabs>
</v-slide-y-transition>
</div>
`, data() {
        return this.initData();
    }, methods: {
        initData(fromCaseSwitch) {
            return {
                oncotreeIconUrl: oncotreeIconUrl,
                firstTimeLoading: !fromCaseSwitch,
                numberRules: [(v) => { return !isNaN(v) || 'Invalid value' }],
                colors: {
                    openCase: "primary",
                    variantDetails: "primary",
                    saveReview: "primary",
                    editAnnotation: "primary"
                },
                loadingVariantDetails: false,
                patientTables: [],
                qcUrl: "",
                mutationalSignatureUrl: "",
                reportReady: false,
                caseName: "",
                caseId: "",
                caseType: "",
                caseTypeIcon: "",
                caseTypeIconSize: 20,
                patientDetailsVisible: !fromCaseSwitch ? false : this.patientDetailsVisible,
                caseAnnotationsVisible: !fromCaseSwitch ? false : this.caseAnnotationsVisible,
                caseAnnotation: { caseAnnotation: "" },
                caseAnnotationOriginalText: "", //to verify if there has been a modification
                labNotes: null,
                reviewDialogVisible: false,
                caseNotesChanged: false,
                splashDialog: splashDialog,
                breadcrumbs: [],
                variantTabsVisible: true,
                variantTabActive: null,
                highlights: {
                    genes: []
                },
                oncotree: [],
                currentFilterType: "snp",
                confirmationDialogVisible: false,
                patientDetailsOncoTreeDiagnosis: {},
                patientDetailsTumorTissue: "",
                patientDetailsICD10: "",
                snackBarVisible: false,
                snackBarLinkIcon: "",
                snackBarLink: "",
                snackBarTimeout: 2000,
                snackBarMessage: "",
                fpkmVisible: false,
                fpkmPositionx: 0,
                fpkmPositiony: 0,
                unfilteredSNPsDict: {},
                unfilteredCNVsDict: {},
                unfilteredFTLsDict: {},
                isVariantOpening: false,
                urlQuery: {
                    showReview: false,
                    variantId: null,
                    variantType: null,
                    edit: false
                },
                variantDetailsVisible: false,
                cnvChromList: [],
                currentVariantType: "snp",
                currentItem: {},
                currentVariant: {},
                breadcrumbItemVariantDetails: { text: "Variant Details", disabled: false, params: ["variantId", "variantType"] },
                breadcrumbItemReview: { text: "Review", disabled: false, params: ["showReview"] },
                breadcrumbItemEditAnnotation: { text: "Add / Edit Annotation", disabled: false, params: ["edit"] },
                breadcrumbItemWorkOnCase: { text: "Case Overview", disabled: false, params: [] },
                userId: null,
                saveAllNeeded: false,
                variantUnSaved: false,
                patientDetailsUnSaved: false,
                waitingForAjaxActive: false,
                waitingForAjaxCount: 0,
                waitingForAjaxMessage: "",
                autoSaveInterval: null,
                savingVariantDetails: false,
                savingPatientDetails: false,
                annotationSelectionUnSaved: false,
                saveVariantDisabled: false,
                caseOwnerId: null,
                caseOwnerName: null,
                updatingSelectedVariantTable: false,
                snpIndelUnfilteredItems: null,
                cnvUnfilteredItems: null,
                ftlUnfilteredItems: null,
                itdDialogVisible: false,
            }
        },
        openIDTCreationDialog() {
            this.itdDialogVisible = true;
        },
        handleRefresh() {
            //issue a warning about unsaved selection
            this.confirmationDialogVisible = true;
        },
        proceedWithConfirmation() {
            this.confirmationDialogVisible = false;
            this.getAjaxData();
        },
        cancelConfirmation() {
            this.confirmationDialogVisible = false;
        },
        toggleFilters(type) {
            this.currentFilterType = type;
            this.$refs.advancedFilter.toggleFilters();
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
        getPatientDetails() {
            axios.get(
                webAppRoot + "/getPatientDetails",
                {
                    params: {
                        caseId: this.$route.params.id
                    }
                })
                .then(response => {
                    if (response.data.isAllowed) {
                        this.patientDetailsUnSaved = false;
                        this.patientTables = response.data.patientTables;
                        this.extractPatientDetailsInfo();

                    }
                    else {
                        this.handleDialogs(response.data, this.getPatientDetails);
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                });
        },
        openFPKMChart(event) {
            this.fpkmPositionx = event.clientX;
            this.fpkmPositiony = event.clientY;
            this.fpkmVisible = true;
        },
        closeFPKMChart() {
            this.fpkmVisible = false;
        },
        openBamViewerLinkWebFLT3() {
            this.$refs.bamViewerLinkFLT3.$el.click();
        },
        createBamViewerLinkFLT3() {
            var igvRange = "chr13:28,033,867-28,034,235";
            link = "../bamViewer?";
            link += "locus=" + igvRange;
            link += "&caseId=" + this.$route.params.id;
            return link;
        },
        downloadIGVFile(igvType, igvRangeBypass) {
            var igvRange = "";
            if (igvRangeBypass) {
                igvRange = igvRangeBypass;
            }
            else {
                igvRange = this.currentItem.chrom + ":";
                igvRange += this.currentItem.pos - 100;
                igvRange += "-";
                igvRange += this.currentItem.pos + 99;
            }
            axios.get(
                webAppRoot + "/downloadLocalIGVFile",
                {
                    params: {
                        locus: igvRange,
                        caseId: this.$route.params.id,
                        type: igvType
                    }
                })
                .then(response => {
                    if (response.data.isAllowed) {
                        if (response.data.payload.sessionType == "sessionLink") {
                            console.log(response.data.payload.link);
                        }
                        else {
                            var hiddenElement = document.createElement('a');
                            hiddenElement.href = webAppRoot + "/igv/" + response.data.payload;
                            hiddenElement.download = response.data.payload;
                            document.body.appendChild(hiddenElement);
                            hiddenElement.click();
                            document.body.removeChild(hiddenElement);
                        }
                    }
                    else {
                        this.handleDialogs(response.data, this.downloadIGVFile.bind(null, igvType, igvRangeBypass));
                    }
                }).catch(error => {
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
            // this.splashProgress = 100; //should dismiss the splash dialog
            // this.waitingForAjaxMessage = "There were some errors while saving";
            // this.waitingForAjaxActive = false; //stops spinning wheel if error
        },
        handleAxiosError(error) {
            console.log(error);
            // this.splashProgress = 100;
            bus.$emit("some-error", [this, error]);
            // this.waitingForAjaxMessage = "There were some errors while saving";
        },
        isSaveNeededBadgeVisible() {
            if (!this.saveAllNeeded) {
                this.saveAllNeeded = this.annotationSelectionUnSaved 
                || this.variantUnSaved 
                || this.patientDetailsUnSaved 
                || ((this.$refs.variantDetailsDialog && this.$refs.variantDetailsDialog.variantDetailsPanel) ? this.$refs.variantDetailsDialog.variantDetailsPanel.variantDetailsUnSaved : false)
                || this.isCaseAnnotationChanged();
            }
            return this.saveAllNeeded && !this.readonly;
        },
        handleAnnotationSelectionChanged(annotationSelectionUnSaved) {
            this.annotationSelectionUnSaved = annotationSelectionUnSaved;
        },
        handleAnnotationSelectionSaved() {
            this.waitingForAjaxCount--;
        },
        createSaveTooltip() {
            var tooltip = ["Some edits have not been saved yet:"];
            if (this.annotationSelectionUnSaved) {
                tooltip.push("- Annotation Selection");
            }
            if (this.variantUnSaved) {
                tooltip.push("- Variant Selection");
            }
            if (this.patientDetailsUnSaved) {
                tooltip.push("- Patient Details");
            }
            if (this.isCaseAnnotationChanged()) {
                tooltip.push("- Case Notes");
            }
            if (this.$refs.variantDetailsDialog && this.$refs.variantDetailsDialog.variantDetailsPanel && this.$refs.variantDetailsDialog.variantDetailsPanel.variantDetailsUnSaved) {
                tooltip.push("- Variant Details");
            }
            if (tooltip.length > 1) {
                return tooltip.join("<br/>");
            }
            return "Nothing to Save";
        },
        isCaseAnnotationChanged() {
            return this.caseNotesChanged;
        },
        isFilteringActiveForType(variantType, tableRef) {
            if (this.$refs.advancedFilter && this.$refs[tableRef]) {
                if (this.$refs.advancedFilter.filterNeedsReload) {
                    return this.$refs[tableRef].filteringActive;
                }
                else {
                    return this.$refs.advancedFilter.isAnyFilterUsedByType(variantType);
                }
            }
            return false;
        },
        handleSelectionChangeFromVariantDetails(oid, idType, isSelected) {
            //idType is used to know which table the event came from.
            let table = null;
            if (idType == "SNP") {
                this.unfilteredSNPsDict[oid] = {oid: oid, selected: isSelected}
                //update the SNP tables with the new selection
                table = this.$refs.geneVariantDetails;
            }
            else if (idType == "CNV") {
                this.unfilteredCNVsDict[oid] = {oid: oid, selected: isSelected}
                table = this.$refs.cnvDetails;
            }
            else if (idType == "FTL") {
                this.unfilteredFTLsDict[oid] = {oid: oid, selected: isSelected}
                table =  this.$refs.translocationDetails;
            }
            for (let i = 0; i < table.items.length; i++) {
                let item = table.items[i];
                if (item.oid == this.urlQuery.variantId) {
                    item.isSelected = isSelected;
                    if (!item.isSelected) {
                        delete item.selectionPerAnnotator[this.userId];
                        delete item["dateSince" + this.userId]; 
                    }
                    break; //item found. exit
                }
            }
            this.variantUnSaved = true;
        },
        handleSelectionChanged(selectedSize, item, idType) {
            //idType is used to know which table the event came from.
            if (idType == "SNP") {
                this.unfilteredSNPsDict[item.oid] = {oid: item.oid, selected: item.isSelected}
                
            }
            else if (idType == "CNV") {
                this.unfilteredCNVsDict[item.oid] = {oid: item.oid, selected: item.isSelected}
            }
            else if (idType == "FTL") {
                this.unfilteredFTLsDict[item.oid] = {oid: item.oid, selected: item.isSelected}
            }
            if (!item.isSelected) {
                delete item.selectionPerAnnotator[this.userId];
                delete item["dateSince" + this.userId]; 
            }
            this.variantUnSaved = true;

        },
        openOncoTree() {
            var oncotreeWindow = window.open("http://oncotree.mskcc.org", "_blank");
         },
         openOncoKBGeniePortalCancer() {
             var url = oncoKBGeniePortalUrl + "Cancer/?Oncotree=" + this.patientDetailsOncoTreeDiagnosis.text;
             window.open(url, "_blank");
         },
        isAdvancedFilteringVisible() {
            return this.$refs.advancedFilter && this.$refs.advancedFilter.advancedFilteringVisible;
        },
        updateHighlights(filter) {
            if (filter.fieldName == "cnvGeneName") {
                var items = null;
                if (filter.value) {
                    if (Array.isArray(filter.value)) {
                        items = filter.value;
                    }
                    else {
                        items = filter.value.split(",");
                    }
                    for (var i = 0; i < items.length; i++) {
                        items[i] = items[i].trim();
                     }
                }
                this.highlights.genes = items;
            }
        },
        filterData() {
            this.getAjaxData();
        },
        showSnackBarMessage(message) {
            this.snackBarMessage = message;
            this.snackBarLink = "";
            this.snackBarLinkIcon = "";
            this.snackBarTimeout = 2000;
            this.snackBarVisible = true;
        },
        showSnackBarMessageWithParams(snackBarMessage, snackBarLink, snackBarLinkIcon, snackBarTimeout) {
            this.snackBarMessage = snackBarMessage;
            this.snackBarLink = snackBarLink;
            this.snackBarLinkIcon = snackBarLinkIcon;
            this.snackBarTimeout = snackBarTimeout != null ? snackBarTimeout : 4000;
            this.snackBarVisible = true;
        },
        getAjaxData() {
            //TODO
            if (this.loadingVariantDetails) {
                return new Promise((resolve, reject) => {
                    resolve({success: true});
                });
            }
            this.loadingVariantDetails = true;
            this.$refs.advancedFilter.loading = true;
            return new Promise((resolve, reject) => {
                
                axios({
                    method: 'post',
                    url: webAppRoot + "/getCaseDetails",
                    params: {
                        caseId: this.$route.params.id,
                        readOnly: this.readonly
                    },
                    data: {
                        filters: this.$refs.advancedFilter.filters
                    }
                }).then(response => {
                    if (response.data.isAllowed) {
                        if (this.readonly) {
                            bus.$emit("update-status", ["VIEW ONLY MODE"]);
                        }
                        else {
                            bus.$emit("update-status-off");
                        }
                        this.patientTables = response.data.patientInfo.patientTables;
                        this.caseAssignedTo = response.data.assignedToIds;
                        this.caseType = response.data.type;
                        this.reportReady = response.data.reportReady;
                        this.caseOwnerId = response.data.caseOwnerId;
                        this.caseOwnerName = response.data.caseOwnerName;
                        if (this.caseType == "Clinical") {
                            this.caseTypeIcon = "fa-user-md";
                            this.caseTypeIconSize = 20;
                        }
                        else if (this.caseType == "Research" || this.caseType == "ClinicalResearch") {
                            this.caseTypeIcon = "fa-flask";
                            this.caseTypeIconSize = 18;
                        }
                        this.labNotes = response.data.labNotes;
                        this.extractPatientDetailsInfo(response.data.caseName);
                        this.caseId = response.data.caseId;
                        this.qcUrl = response.data.qcUrl + this.caseId + "?isLimsId=true&primary=true";
                        response.data.tumorVcf = "delete this";
                        this.mutationalSignatureUrl = response.data.tumorVcf ? webAppRoot + "/mutationalSignatureViewer?caseId=" + this.caseId : null;
                        this.addCustomWarningFlags(response.data.snpIndelVariantSummary);
                        this.addOtherAnnotatorsValues(response.data.snpIndelVariantSummary);
                        this.$refs.geneVariantDetails.manualDataFiltered(response.data.snpIndelVariantSummary); //this can freeze the UI in datatable this.items = data.items; Not sure how to speed it up
                        
                        this.addOtherAnnotatorsValues(response.data.cnvSummary);
                        this.$refs.cnvDetails.manualDataFiltered(response.data.cnvSummary);

                        this.addOtherAnnotatorsValues(response.data.translocationSummary);
                        this.$refs.translocationDetails.manualDataFiltered(response.data.translocationSummary);
                    
                        //keep track of the original items in order to select all the variants regardless of filtering
                        if (!this.snpIndelUnfilteredItems && !this.$refs.advancedFilter.isAnyFilterUsed()) {
                            this.snpIndelUnfilteredItems = response.data.snpIndelVariantSummary.items;
                        }
                        //keep track of the original items in order to select all the variants regardless of filtering
                        if (!this.cnvUnfilteredItems && !this.$refs.advancedFilter.isAnyFilterUsed()) {
                            this.cnvUnfilteredItems = response.data.cnvSummary.items;
                        }
                         //keep track of the original items in order to select all the variants regardless of filtering
                        if (!this.ftlUnfilteredItems && !this.$refs.advancedFilter.isAnyFilterUsed()) {
                            this.ftlUnfilteredItems = response.data.translocationSummary.items;
                        }


                        this.$refs.advancedFilter.effects = response.data.effects;
                        this.$refs.advancedFilter.failedFilters = response.data.failedFilters;
                        this.$refs.advancedFilter.checkBoxLabelsByValue = response.data.checkBoxLabelsByValue;
                        this.userId = response.data.userId;
                        this.$refs.advancedFilter.populateCheckBoxes();

                        this.$refs.advancedFilter.filterNeedsReload = false;
                        this.addSNPIndelHeaderAction(response.data.snpIndelVariantSummary.headers);
                        this.addCNVHeaderAction(response.data.cnvSummary.headers);
                        this.addFusionHeaderAction(response.data.translocationSummary.headers);
                        this.removeCurrentUserSelectionColumnFromHeaders(response.data.snpIndelVariantSummary.headerOrder, response.data.cnvSummary.headerOrder, response.data.translocationSummary.headerOrder)
                       
                        this.reportGroups = response.data.reportGroups;
                        if (this.$refs.reviewDialog) {
                            this.$refs.reviewDialog.requiredReportGroups = this.reportGroups.filter(r => r.required);
                        }
                        this.$refs.advancedFilter.reportGroups = this.reportGroups;

                       
                        
                        //create a dict of oids and their selected status here
                        for (let i=0; i < response.data.snpIndelVariantSummary.items.length; i++) {
                            let item = response.data.snpIndelVariantSummary.items[i];
                            if (item.oid in this.unfilteredSNPsDict) {
                                item.isSelected = this.unfilteredSNPsDict[item.oid].selected;
                            }
                            else {
                                this.unfilteredSNPsDict[item.oid] = {oid: item.oid, selected: item.isSelected}
                            }
                        }
                        for (let i=0; i < response.data.cnvSummary.items.length; i++) {
                            let item = response.data.cnvSummary.items[i];
                            if (item.oid in this.unfilteredCNVsDict) {
                                item.isSelected = this.unfilteredCNVsDict[item.oid].selected;
                            }
                            else {
                                this.unfilteredCNVsDict[item.oid] = {oid: item.oid, selected: item.isSelected}
                            }
                        }
                        for (let i=0; i < response.data.translocationSummary.items.length; i++) {
                            let item = response.data.translocationSummary.items[i];
                            if (item.oid in this.unfilteredFTLsDict) {
                                item.isSelected = this.unfilteredFTLsDict[item.oid].selected;
                            }
                            else {
                                this.unfilteredFTLsDict[item.oid] = {oid: item.oid, selected: item.isSelected}
                            }
                        }

                        //TODo might need to handle currentRow

                        //TOOD first time page load versus keeping user's prefs
                        if (this.firstTimeLoading) {
                            this.firstTimeLoading = false;
                            this.patientDetailsVisible = true;
                            this.caseAnnotationsVisible = true;
                        }
                        this.loadingVariantDetails = false;
                        this.$refs.advancedFilter.loading = false;
                        this.isVariantOpening = false;
                        resolve({
                            success: true
                        });
                    }
                    else {
                        this.loadingVariantDetails = false;
                        this.isVariantOpening = false;
                        reject(response);
                    }
                }).catch(error => {
                        this.loadingVariantDetails = false;
                        this.isVariantOpening = false;
                        if (this.$refs.advancedFilter) {
                            this.$refs.advancedFilter.loading = false;
                        }
                        this.handleAxiosError(error);
                    }
                    );    
            })
        },
        removeCurrentUserSelectionColumnFromHeaders(snpSummaryHeaderOrder, cnvSummaryHeaderOrder, ftlSummaryHeaderOrder) {
            var headerOrders = [snpSummaryHeaderOrder, cnvSummaryHeaderOrder, ftlSummaryHeaderOrder];
            for (var i = 0; i < headerOrders.length; i++) {
                for (var j = 0; j < headerOrders[i].length; j++) {
                    if (headerOrders[i][j] == "dateSince" + this.userId) {
                        headerOrders[i].splice(j, 1);
                        break;
                    }
                }
            }
        },
        addCurrentUserSelectionColumnToHeaders(snpSummaryHeaderOrder, cnvSummaryHeaderOrder, ftlSummaryHeaderOrder) {
            var headerOrders = [snpSummaryHeaderOrder, cnvSummaryHeaderOrder, ftlSummaryHeaderOrder];
            for (var i = 0; i < headerOrders.length; i++) {
                headerOrders[i].splice(0,0, "dateSince" + this.userId);
            }
        },
        addSNPIndelHeaderAction(headers) {
            for (var i = 0; i < headers.length; i++) {
                if (headers[i].value == "chromPos") {
                    headers[i].itemAction = this.openVariant;
                    headers[i].actionIcon = "zoom_in";
                    headers[i].actionTooltip = "Variant Details";
                }
                if (headers[i].value == "notation" && headers[i]["notationTooltipText"]) {
                    headers[i].itemAction = this.openVariant;
                    headers[i].actionIcon = "zoom_in";
                    headers[i].actionTooltip = "Variant Details";
                }
            }
        },
        addCNVHeaderAction(headers) {
            for (var i = 0; i < headers.length; i++) {
                if (headers[i].value == "chrom") {
                    headers[i].itemAction = this.openCNV;
                    headers[i].actionIcon = "zoom_in";
                    headers[i].actionTooltip = "CNV Details";
                    break;
                }
            }
        },
        addFusionHeaderAction(headers) {
            for (var i = 0; i < headers.length; i++) {
                if (headers[i].value == "fusionName") {
                    headers[i].itemAction = this.openTranslocation;
                    headers[i].actionIcon = "zoom_in";
                    headers[i].actionTooltip = "Translocation Details";
                    break;
                }
            }
        },
        updateRoute() {
            router.push({ query: this.urlQuery });
        },
        openVariant(item) {
            if (this.isVariantOpening) {
                return;
            }
            this.isVariantOpening = true;
            this.urlQuery.variantType = "snp";
            this.urlQuery.variantId = item.oid;
            item.loading = true;
            this.currentItem = item;
            this.$refs.variantDetailsDialog.currentlySelected = this.$refs.variantDetailsDialog.isSelected();
            this.updateRoute();
        },
        openCNV(item) {
            if (this.isVariantOpening) {
                return;
            }
            this.urlQuery.variantType = "cnv";
            this.urlQuery.variantId = item.oid;
            this.currentItem = item;
            item.loading = true;
            this.updateRoute();
        },
        openTranslocation(item) {
            if (this.isVariantOpening) {
                return;
            }
            this.urlQuery.variantType = "translocation";
            this.urlQuery.variantId = item.oid;
            this.currentItem = item;
            item.loading = true;
            this.updateRoute();
        },
        extractPatientDetailsInfo(caseName) {
            for (var i = 0; i < this.patientTables.length; i++) {
                for (var j = 0; j < this.patientTables[i].items.length; j++) {
                    var item = this.patientTables[i].items[j];
                    if (caseName && item.field == "caseName") {
                        this.caseName = caseName + " (" + item.value + ")";
                    }
                    else if (item.field == "oncotree") {
                        this.patientDetailsOncoTreeDiagnosis = { text: item.value, label: "" };
                    }
                    else if (item.field == "tumorTissueType") {
                        this.patientDetailsTumorTissue = item.value;
                    }
                    else if (item.field == "icd10") {
                        this.patientDetailsICD10 = item.value;
                    }
                    else if (item.field == "dedupPctOver100X") {
                        this.patientDetailsDedupPctOver100X = item.value;
                    }
                    else if (item.field == "dedupAvgDepth") {
                        this.patientDetailsDedupAvgDepth = item.value;
                    }
                    else if (item.field == "tumorPercent") {
                        this.patientDetailsTumorPercent = item.value;
                    }
                }
            }
            this.populateOncotreeLabel(); //update the label
        },
        openReviewSelectionDialog() {
            this.reviewDialogVisible = true;
            this.urlQuery.showReview = true;
            this.updateRoute();
            this.updateSelectedVariantTable();
        },
        closeReviewDialog() {
            this.reviewDialogVisible = false;
            this.urlQuery.showReview = false;
            this.updateRoute();
        },
        collectOncoTreeDiagnosis() {
            this.oncotree = oncotree;
            this.populateOncotreeLabel();
        },
        populateOncotreeLabel() {
            for (var i = 0; i < this.oncotree.length; i++) {
                if (this.oncotree[i].text == this.patientDetailsOncoTreeDiagnosis.text) {
                    this.patientDetailsOncoTreeDiagnosis.label = this.oncotree[i].label;
                    break;
                }
            }
        },
        saveCaseAnnotations(skipSnackBar) {
            axios({
                method: 'post',
                url: webAppRoot + "/saveCaseAnnotations",
                params: {
                    caseId: this.$route.params.id,
                    skipSnackBar: skipSnackBar
                },
                data: {
                    annotation: [this.caseAnnotation]
                }
            }).then(response => {
                this.waitingForAjaxCount--;
                if (response.data.isAllowed) {
                    this.loadCaseAnnotations();
                    if (!response.data.skipSnackBar) {
                        this.showSnackBarMessage("Annotation Saved");
                    }
                    this.caseAnnotationOriginalText = this.caseAnnotation.caseAnnotation; //to reset the isCaseAnnotationChanged
                    this.caseNotesChanged = false;
                }
                else {
                    this.handleDialogs(response.data, this.saveCaseAnnotations.bind(null, response.data.skipSnackBar));
                }
            }).catch(error => {
                this.handleAxiosError(error);
            });
        },
        loadCaseAnnotations() {
            axios.get(
                webAppRoot + "/loadCaseAnnotations",
                {
                    params: {
                        caseId: this.$route.params.id
                    }
                })
                .then(response => {
                    if (response.data.isAllowed) {
                        this.caseAnnotation = response.data;
                        this.caseAnnotationOriginalText = response.data.caseAnnotation;
                    }
                    else {
                        this.handleDialogs(response, this.loadCaseAnnotations);
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                });
        },
        mountComponent() {
            this.snackBarMessage = this.readonly ? "View Only Mode: some actions have been disabled" : "",
            this.snackBarLink = "";
            this.snackBarVisible = this.readonly;
            
            this.collectOncoTreeDiagnosis();
            this.getAjaxData().then(response => {
                if (this.urlQuery.showReview === true) {
                    this.$nextTick(this.openReviewSelectionDialog());
                    this.updateSelectedVariantTable();
                }
                this.closeSplashScreen();
            });
            if (this.$refs.advancedFilter) {
                this.$refs.advancedFilter.loadUserFilterSets();
                this.$refs.advancedFilter.getVariantFilters();
            }
            bus.$emit("clear-item-selected", [this]);
            this.loadCaseAnnotations();

            // this.$refs.geneVariantDetails.headerOptionsVisible = true;
            this.$refs.splashScreen.manageSplashScreen();
            this.getCNVChromList();
        },
        getCNVChromList() {
            axios.get(
                webAppRoot + "/getCNVChromList",
                {
                    params: {
                        caseId: this.$route.params.id

                    }
                }).then(response => {
                    if (response.data.isAllowed) {
                        this.cnvChromList = response.data.items;

                    } else {
                        this.handleDialogs(response.data, this.getCNVChromList);
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                });
        },
        addCustomWarningFlags(snpIndelVariantSummary) {
            for (var i = 0; i < snpIndelVariantSummary.items.length; i++) {
                var item = snpIndelVariantSummary.items[i];
                this.addCustomWarningFlagsForItem(item);
            }
        },
        addCustomWarningFlagsForItem(item) {
            item.iconFlags.iconFlags.forEach(f => {f.chip = false;});
            var iconFlags = item.iconFlags.iconFlags;
            var warnings = [];
            var tooltips = [];
            if (item.common) {
                warnings.push("C");
                tooltips.push("Common");
            }
            if (item.callsetInconsistent) {
                warnings.push("I");
                tooltips.push("Inconsistent calls");
            }
            if (item.lowComplexity) {
                warnings.push("LCR");
                tooltips.push("Low Complexity Region");
            }
            if (item.likelyArtifact) {
                warnings.push("A");
                tooltips.push("Likely Artifact");
            }
            if (item.isRepeat) {
                warnings.push("R");
                if (item.repeatTypes) {
                    tooltips.push("Repeat Types: " + item.repeatTypes.join(" "));
                }
                else {
                    tooltips.push("Repeats");
                }
            }
            if (warnings.length > 0) {
                iconFlags.push({
                    chip: true,
                    color: "warning",
                    iconName: warnings.join(),
                    tooltip: tooltips.join(", ")
                });
            }
        },
        addOtherAnnotatorsValues(itemSummary) {
            var headers = itemSummary.headers.filter(h => h.map);
            for (var i = 0; i < itemSummary.items.length; i++) {
                var item = itemSummary.items[i];
                var annotatorSelections = item.selectionPerAnnotator;
                for (var j = 0; j < headers.length; j++) {
                    var header = headers[j];
                    if (annotatorSelections[header.mapTo]) {
                        item[header.value] = annotatorSelections[header.mapTo].selectedSince;
                    }
                }
            }
        },
        handleRouteChanged(newRoute, oldRoute) {
            if (newRoute.path != oldRoute.path) { //prevent reloading data if only changing the query router.push({query: {test:"hello3"}})
                Object.assign(this.$data, this.initData(true));
                this.mountComponent();
            }
            else { //look at the query
                // console.log(newRoute.query, oldRoute.query);
                var newRouteQuery = JSON.stringify(newRoute.query);
                var oldRouteQuery = JSON.stringify(oldRoute.query);
                if (newRouteQuery != oldRouteQuery) { //some params changed
                    this.loadFromParams(newRoute.query, oldRoute.query);
                }
            }
            this.updateBreadcrumbs();
        },
        updateBreadcrumbs() {
            //build the breadcrumb trail
            this.breadcrumbs = [];
            this.breadcrumbs.push(this.breadcrumbItemWorkOnCase);
            if (this.urlQuery.showReview === true) {
                this.breadcrumbs.push(this.breadcrumbItemReview);
            }
            if (this.urlQuery.variantId && this.urlQuery.variantType) {
                this.breadcrumbs.push(this.breadcrumbItemVariantDetails);
            }
            if (this.urlQuery.edit === true) {
                this.breadcrumbs.push(this.breadcrumbItemEditAnnotation);
            }
        },
        loadFromParams(newRouteQuery, oldRouteQuery) {
            this.urlQuery.variantId = this.$route.query.variantId ? this.$route.query.variantId : null;
            this.urlQuery.variantType = this.$route.query.variantType ? this.$route.query.variantType : null;
            this.urlQuery.showReview = this.$route.query.showReview === true || this.$route.query.showReview === "true";
            this.urlQuery.edit = this.$route.query.edit === true || this.$route.query.edit === "true";

            if (!this.urlQuery.variantId) { //close variant details
                this.closeVariantDetails(true);
                this.toggleHTMLOverlay();
            }
            if (this.urlQuery.variantType) {
                this.variantTabActive = "tab-" + this.urlQuery.variantType;
            }
            if (!this.urlQuery.showReview) {
                this.closeReviewDialog();
            }
            //then open variant details
            if (this.urlQuery.variantId && this.urlQuery.variantType) {
                if (this.$route.query.variantId != 'notreal') {
                        var variantFound = false;
                        //find item
                        if (this.urlQuery.variantType == 'snp') {
                            if ((newRouteQuery && oldRouteQuery && (newRouteQuery.variantId != oldRouteQuery.variantId))
                                || !newRouteQuery) {
                                    variantFound = true;
                                this.getVariantDetails(this.urlQuery.variantId)
                                .then((response) => {
                                    if (response.success) {
                                        this.isVariantOpening = false;
                                        //open other dialogs if needed
                                        if (this.urlQuery.edit) {
                                            this.$refs.variantDetailsDialog.startUserAnnotations();
                                        }
                                    }
                                })
                                .catch(response => {
                                    this.handleDialogs(response.data, this.getVariantDetails.bind(null,
                                        this.urlQuery.variantId));
                                });
                            }
                        }
                        else if (this.urlQuery.variantType == 'cnv') {
                            if ((newRouteQuery && oldRouteQuery && (newRouteQuery.variantId != oldRouteQuery.variantId))
                                || !newRouteQuery) {
                                    variantFound = true;
                                this.getCNVDetails(this.urlQuery.variantId)
                                .then((response) => {
                                    if (response.success) {
                                        this.isVariantOpening = false;
                                        //open other dialogs if needed
                                        if (this.urlQuery.edit) {
                                            this.$refs.variantDetailsDialog.startUserAnnotations();
                                        }
                                    }
                                });
                            }
                        }
                        else if (this.urlQuery.variantType == 'translocation') {
                            if ((newRouteQuery && oldRouteQuery && (newRouteQuery.variantId != oldRouteQuery.variantId))
                                || !newRouteQuery) {
                                    variantFound = true;
                                this.getTranslocationDetails(this.urlQuery.variantId)
                                .then((response) => {
                                    if (response.success) {
                                        this.isVariantOpening = false;
                                        //open other dialogs if needed
                                        if (this.urlQuery.edit) {
                                            this.$refs.variantDetailsDialog.startUserAnnotations();
                                            this.closeSplashScreen();
                                        }
                                    }
                                });
                            }
                        }
                        if (!variantFound) {
                            //commented out because not working properly
                            // var response = {
                            //     success: false,
                            //     message: "The variant ID could not be found. The URL must be incorrect."
                            // }
                            // this.handleDialogs(response, null);
                            this.toggleHTMLOverlay();
                            // var html = document.querySelector("html");
                            // html.style.overflow = ""
                        }
                }
                else {
                    this.getCNVDetailsNoVariant();
                }

               
            }
            
        },
        getCNVDetailsNoVariant() {
            this.$refs.variantDetailsDialog.getCNVDetailsNoVariant()
            .then((response) => {
                if (response.success) {
                    this.isVariantOpening = false;
                    this.variantDetailsVisible = true;
                    this.updateRoute();
                    this.variantDetailsEndedLoading();
                    this.closeSplashScreen();
                }
            });
        },
        toggleHTMLOverlay() {
            var html = document.querySelector("html");
            if (this.urlQuery.variantId || this.urlQuery.showReview) {
                html.style.overflow = "hidden";
            }
            else {
                html.style.overflow = "";
            }
        },
        isSNP() {
            return this.currentVariantType == "snp";
        },
        isCNV() {
            return this.currentVariantType == "cnv";
        },
        isSNPCNV() {
            return this.isSNP() || this.isCNV();
        },
        isTranslocation() {
            return this.currentVariantType == "translocation";
        },
        closeSplashScreen() {
            setTimeout(() => {
                //make sure the dialogs are stacked up properly
                if (this.urlQuery.showReview && this.urlQuery.variantId) {
                    // var styleVariantDetails = this.$refs.variantDetailsDialog.$children[0].$el.parentElement.style;
                    let styleVariantDetails = document.getElementsByClassName("variantDetailsDialog")[0].parentElement.style;
                    var styleShowReview = this.$refs.reviewDialog.$parent.$parent.$children[0].$el.parentElement.style;
                    var zIndexVariantDetails = styleVariantDetails.zIndex == "" ? 200 : parseInt(styleVariantDetails.zIndex);
                    var zIndexShowReview = parseInt(styleShowReview.zIndex);
                    while (zIndexVariantDetails <= zIndexShowReview) {
                        zIndexVariantDetails++;
                    }
                    styleVariantDetails.zIndex = zIndexVariantDetails + "";
                }
                this.$nextTick( () => {
                    this.splashDialog = false;
                    splashDialog = false; //disable from now on
                    if (splashInterval) {
                        clearInterval(splashInterval);
                    }
                });
            }, 500);
          
        },
        getVariantDetails(variantId) {
            return new Promise((resolve, reject) => {
                this.currentVariantType = "snp";
                var table; //could be the selected variant table or the regular one
                if (this.reviewDialogVisible) {
                    table = this.$refs.reviewDialog.getSnpTable();
                }
                else {
                    table = this.$refs.geneVariantDetails;
                }
                let currentIndex = -1;
                let isFirstVariant = true;
                let isLastVariant = true;
                if (!this.splashDialog) { //don't allow prev/next when loading from a direct link
                    currentIndex = table.getCurrentItemIndex(this.urlQuery.variantId);
                    isFirstVariant = table.isFirstItem(currentIndex);
                    isLastVariant = table.isLastItem(currentIndex);
                }
    
                this.$refs.variantDetailsDialog.getVariantDetails(variantId, isFirstVariant, isLastVariant)
                .then(response => {
                    if (response.success) {
                        //open the variant details dialog
                        this.variantDetailsVisible = true;
                        this.variantDetailsEndedLoading();
                        resolve({success: true});
                    }
                })
                .catch(response => {
                    this.handleDialogs(response.data, this.getVariantDetails.bind(null,
                        this.urlQuery.variantId));
                });

            });

        },
        getCNVDetails(variantId) {
            return new Promise((resolve, reject) => {
                this.currentVariantType = "cnv";
                var table; //could be the selected variant table or the regular one
                if (this.reviewDialogVisible) {
                    table = this.$refs.reviewDialog.getCnvTable();
                }
                else {
                    table = this.$refs.cnvDetails;
                }
                let currentIndex = -1;
                let isFirstVariant = true;
                let isLastVariant = true;
                if (!this.splashDialog) { //don't allow prev/next when loading from a direct link
                    currentIndex = table.getCurrentItemIndex(this.urlQuery.variantId);
                    isFirstVariant = table.isFirstItem(currentIndex);
                    isLastVariant = table.isLastItem(currentIndex);
                }
    
                this.$refs.variantDetailsDialog.getCNVDetails(variantId, isFirstVariant, isLastVariant)
                .then(response => {
                    if (response.success) {
                        //open the variant details dialog
                        this.variantDetailsVisible = true;
                        this.variantDetailsEndedLoading();
                        resolve({success: true});
                    }
                })
                .catch(response => {
                    this.handleDialogs(response.data, this.getVariantDetails.bind(null,
                        this.urlQuery.variantId));
                });


            });

        },
        getTranslocationDetails(variantId) {
            return new Promise((resolve, reject) => {
                this.currentVariantType = "translocation";
                var table; //could be the selected variant table or the regular one
                if (this.reviewDialogVisible) {
                    table = this.$refs.reviewDialog.getFtlTable();
                }
                else {
                    table = this.$refs.translocationDetails;
                }
                let currentIndex = -1;
                let isFirstVariant = true;
                let isLastVariant = true;
                if (!this.splashDialog) { //don't allow prev/next when loading from a direct link
                    currentIndex = table.getCurrentItemIndex(this.urlQuery.variantId);
                    isFirstVariant = table.isFirstItem(currentIndex);
                    isLastVariant = table.isLastItem(currentIndex);
                }
    
                this.$refs.variantDetailsDialog.getTranslocationDetails(variantId, isFirstVariant, isLastVariant)
                .then(response => {
                    if (response.success) {
                        //open the variant details dialog
                        this.variantDetailsVisible = true;
                        this.variantDetailsEndedLoading();
                        resolve({success: true});
                    }
                })
                .catch(response => {
                    this.handleDialogs(response.data, this.getVariantDetails.bind(null,
                        this.urlQuery.variantId));
                });
;

            });

        },
        loadPrevVariant() {
            var table; //could be the selected table or the regular one
            if (this.isSNP()) {
                if (this.reviewDialogVisible) {
                    table = this.$refs.reviewDialog.getSnpTable();
                }
                else {
                    table = this.$refs.geneVariantDetails;
                }
                var prevVariant = table.getPreviousItem(this.urlQuery.variantId, true);
                if (prevVariant) {
                    // this.getVariantDetails(prevVariant);
                    this.openVariant(prevVariant);
                }
            }
            else if (this.isCNV()) {
                if (this.reviewDialogVisible) {
                    table = this.$refs.reviewDialog.getCnvTable();
                }
                else {
                    table = this.$refs.cnvDetails;
                }
                var prevVariant = table.getPreviousItem(this.urlQuery.variantId, true);
                if (prevVariant) {
                    this.openCNV(prevVariant);
                }
            }
            else if (this.isTranslocation()) {
                if (this.reviewDialogVisible) {
                    table = this.$refs.reviewDialog.getFtlTable();
                }
                else {
                    table = this.$refs.translocationDetails;
                }
                var prevVariant = table.getPreviousItem(this.urlQuery.variantId, true);
                if (prevVariant) {
                    this.openTranslocation(prevVariant);
                }
            }
        },
        isSelectionPerAnnotatorReal(selectionPerAnnotator) {
            //check if it's a real object or a html string (temp v-chip)
            return selectionPerAnnotator && selectionPerAnnotator.userId;
        },
        createTempSelectionPerAnnotator() {
            return "<span tabindex='-1' class='v-chip v-chip--disabled v-chip--label warning v-chip--small white--text'><span class='v-chip__content'><i aria-hidden='true' class='icon material-icons mdi mdi-checkbox-marked' style='font-size: 16px; vertical-align: bottom'></i><span class='pl-2'>latest</span></span></span>";
        },
        updateSelectedVariantTable() {
            if (this.updatingSelectedVariantTable) {
                return;
            }
            this.updatingSelectedVariantTable = true;
            this.$refs.reviewDialog.startLoading();

            //use the unfiltered tables
            var selectedSNPVariants = this.snpIndelUnfilteredItems.filter(item => this.unfilteredSNPsDict[item.oid].selected || Object.entries(item.selectionPerAnnotator).length > 0);
            var selectedSNPVariantsReviewer = this.snpIndelUnfilteredItems.filter(item => this.unfilteredSNPsDict[item.oid].selected || item.selectionPerAnnotator[this.caseOwnerId] != null);
            var selectedCNVs = this.cnvUnfilteredItems.filter(item => this.unfilteredCNVsDict[item.oid].selected  || Object.entries(item.selectionPerAnnotator).length > 0);
            var selectedCNVsReviewer = this.cnvUnfilteredItems.filter(item => this.unfilteredCNVsDict[item.oid].selected || item.selectionPerAnnotator[this.caseOwnerId] != null); 
            var selectedTranslocations = this.ftlUnfilteredItems.filter(item => this.unfilteredFTLsDict[item.oid].selected || Object.entries(item.selectionPerAnnotator).length > 0);
            var selectedFTLsReviewer = this.ftlUnfilteredItems.filter(item => this.unfilteredFTLsDict[item.oid].selected || item.selectionPerAnnotator[this.caseOwnerId] != null);
            this.saveVariantDisabled = (selectedSNPVariants.length == 0 && selectedCNVs.length == 0 && selectedTranslocations.length == 0) || !this.canProceed('canAnnotate') || this.readonly;
            
            let allVariantTables = [
                {items: selectedSNPVariants, dict: this.unfilteredSNPsDict},
                {items: selectedSNPVariantsReviewer, dict: this.unfilteredSNPsDict},
                {items: selectedCNVs, dict: this.unfilteredCNVsDict},
                {items: selectedCNVsReviewer, dict: this.unfilteredCNVsDict},
                {items: selectedTranslocations, dict: this.unfilteredFTLsDict}, 
                {items: selectedFTLsReviewer, dict: this.unfilteredFTLsDict}
                ]
                
            //populate the selected variants but not yet saved by creating fake selectionPerAnnotator objects
            for (let i=0; i < allVariantTables.length; i++) {
                let items = allVariantTables[i].items;
                let dict = allVariantTables[i].dict;
                for (var j= 0; j < items.length; j++) {
                    let item = items[j];
                    if (!this.isSelectionPerAnnotatorReal(item.selectionPerAnnotator[this.userId]) && dict[item.oid].selected) {
                        item["dateSince" + this.userId] = this.createTempSelectionPerAnnotator();
                    }
                }
            }    

            //add the current user column headerOrder but only to the all annotator table
            snpAllHeaderOrder =   this.$refs.geneVariantDetails.headerOrder.slice();
            cnvAllHeaderOrder =   this.$refs.cnvDetails.headerOrder.slice();
            ftlAllHeaderOrder =   this.$refs.translocationDetails.headerOrder.slice();

            this.addCurrentUserSelectionColumnToHeaders(snpAllHeaderOrder, cnvAllHeaderOrder, ftlAllHeaderOrder);

            this.$refs.reviewDialog.updateSelectedVariantTable(
            selectedSNPVariants, selectedSNPVariantsReviewer,
            this.$refs.geneVariantDetails.headers, snpAllHeaderOrder, this.$refs.geneVariantDetails.headerOrder, 
            selectedCNVs, selectedCNVsReviewer,
            this.$refs.cnvDetails.headers, cnvAllHeaderOrder, this.$refs.cnvDetails.headerOrder, 
            selectedTranslocations, selectedFTLsReviewer,
            this.$refs.translocationDetails.headers, ftlAllHeaderOrder, this.$refs.translocationDetails.headerOrder);
            this.updatingSelectedVariantTable = false;
        },
        loadNextVariant() {
            var table; //could be the selected table or the regular one
            if (this.isSNP()) {
                if (this.reviewDialogVisible) {
                    table = this.$refs.reviewDialog.getSnpTable();
                }
                else {
                    table = this.$refs.geneVariantDetails;
                }
                var nextVariant = table.getNextItem(this.urlQuery.variantId, true);
                if (nextVariant) {
                    this.openVariant(nextVariant);
                    // this.getVariantDetails(nextVariant);
                }
            }
            else if (this.isCNV()) {
                if (this.reviewDialogVisible) {
                    table = this.$refs.reviewDialog.getCnvTable();
                }
                else {
                    table = this.$refs.cnvDetails;
                }
                var nextVariant = table.getNextItem(this.urlQuery.variantId, true);
                if (nextVariant) {
                    this.openCNV(nextVariant);
                }
            }
            else if (this.isTranslocation()) {
                if (this.reviewDialogVisible) {
                    table = this.$refs.reviewDialog.getFtlTable();
                }
                else {
                    table = this.$refs.translocationDetails;
                }
                var nextVariant = table.getNextItem(this.urlQuery.variantId, true);
                if (nextVariant) {
                    this.openTranslocation(nextVariant);
                }
            }
        },
        closeVariantDetailsExternally(skipSave) {
            this.closeVariantDetails(skipSave);
            this.updateRoute();
        },
        closeVariantDetails(skipSave) {
            if (!skipSave) {
                this.handleSaveAll();
            }
            this.isVariantOpening = false;
            this.variantDetailsVisible = false;
            this.urlQuery.variantId = null;
            this.urlQuery.variantType = null;
            this.urlQuery.edit = false; //also close edit but it should have been done earlier
            zingchart.exec("cnvPlotDetails", 'destroy'); //kill the chart if variant details is closed
            this.$refs.variantDetailsDialog.resetCNVChart();
        },
        handleTabChanged(newValue, oldValue) {
            //SNP/Indel tab need to be active to allow filtering
            if (!this.$refs.advancedFilter) {
                return;
            }
            if (this.variantTabActive == "tab-snp") {
                this.currentFilterType = "snp";
                this.$refs.advancedFilter.disableFiltering = false;
                this.$refs.advancedFilter.checkboxExpansion = [true, true, true, true]; //controls the open state of each panel. Add more items here when creating new expandable panels
                this.$refs.advancedFilter.flagExpansion = [true];
            }
            else if (this.variantTabActive == "tab-cnv") {
                this.currentFilterType = "cnv";
                this.$refs.advancedFilter.disableFiltering = false;
            }
            else if (this.variantTabActive == "tab-translocation") {
                this.currentFilterType = "ftl";
                this.$refs.advancedFilter.flagExpansion = [true];
                this.$refs.advancedFilter.disableFiltering = false;
            }
            else { //no filter for translocation for now
                this.$refs.advancedFilter.disableFiltering = true;
            }
        },
        variantDetailsEndedLoading() {
            if (this.currentItem) {
                this.currentItem.loading = false;
            }
        },
        getPatientDetailsFlexClass(item) {
            if (item && (item.field == "tumorTissueType" || item.field == "icd10")) {
                return 'xs10 lg8 xl7';
            }
            if (item && item.type) {
                return 'xs5';
            }
            return 'xs';
        },
        getPatientDetailsMarginClass(item) {
            if (item && (item.field == "tumorTissueType" || item.field == "icd10")) {
                return "mt-1";
            }
            return "";
        },
        handleSaveAll(autoSave) {
            if (autoSave) {
            this.waitingForAjaxMessage = "Work auto saved"
            }
            else {
                this.waitingForAjaxMessage = "Work saved";
                clearInterval(this.autoSaveInterval);
                this.createAutoSaveInterval();
            }
            this.waitingForAjaxCount = 0;
            if (this.$refs.variantDetailsPanel && this.$refs.variantDetailsPanel.variantDetailsUnSaved) {
                this.waitingForAjaxCount++;
            }
            if (this.annotationSelectionUnSaved) {
                this.waitingForAjaxCount++;
            }
            if (this.variantUnSaved) {
                this.waitingForAjaxCount++;
            }
            if (this.patientDetailsUnSaved) {
                this.waitingForAjaxCount++;
            }
            if (this.isCaseAnnotationChanged()) {
                this.waitingForAjaxCount++;
            }

            if (this.waitingForAjaxCount > 0) {
                this.waitingForAjaxActive = true;
                if (this.$refs.variantDetailsPanel && this.$refs.variantDetailsPanel.variantDetailsUnSaved) {
                    this.saveVariant(true);
                }
                if (this.annotationSelectionUnSaved) {
                    this.$refs.variantDetailsDialog.saveAnnotationSelection(true);
                }
                if (this.variantUnSaved) {
                    this.saveSelection(false, true)
                }
                if (this.patientDetailsUnSaved) {
                    this.savePatientDetails(true);
                }
                if (this.isCaseAnnotationChanged()) {
                    this.saveCaseAnnotations(true);
                }
            }
        },
        saveSelection(closeAfter, skipSnackBar) {
            // There is a bug in vuetify 1.0.19 where a disabled menu still activates the click action.
            // Use a flag to disable the action in the meantime
            if (this.saveVariantDisabled) {
                return;
            }
            if (this.saveLoading) {
                return;
            }
            this.saveLoading = true;
            
            let selectedSNPVariantIds = Object.values(this.unfilteredSNPsDict).filter(i => i.selected).map(i => i.oid);
            let selectedCNVIds = Object.values(this.unfilteredCNVsDict).filter(i => i.selected).map(i => i.oid);
            let selectedTranslocationIds = Object.values(this.unfilteredFTLsDict).filter(i => i.selected).map(i => i.oid);
            axios({
                method: 'post',
                url: webAppRoot + "/saveVariantSelection",
                params: {
                    caseId: this.$route.params.id,
                    closeAfter: closeAfter, //pass this param along to proceed with closing the dialog
                    skipSnackBar: skipSnackBar //pass this param along to display snackbar after successful ajax call
                },
                data: {
                    selectedSNPVariantIds: selectedSNPVariantIds,
                    selectedCNVIds: selectedCNVIds,
                    selectedTranslocationIds: selectedTranslocationIds
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    if (!response.data.skipSnackBar) {
                        this.showSnackBarMessage("Variant Selection Saved");
                    }
                    // this.getAjaxData();
                    this.waitingForAjaxCount--;
                    this.variantUnSaved = false;
                    this.saveLoading = false;
                    if (response.data.uiProceed) {
                        this.closeReviewDialog(true);
                    }
                    this.updateSelectedVariantTable();
                }
                else {
                    this.saveLoading = false;
                    this.waitingForAjaxCount--;
                    this.handleDialogs(response.data, this.saveSelection.bind(null, response.data.uiProceed, response.data.skipSnackBar));
                }
            }).catch(error => {
                this.saveLoading = false;
                this.handleAxiosError(error);
            });
        },
        savePatientDetails(skipSnackBar) {
            if (!this.canProceed('canAnnotate')) {
                return;
            }
            this.savingPatientDetails = true;
            axios({
                method: 'post',
                url: webAppRoot + "/savePatientDetails",
                params: {
                    oncotreeDiagnosis: this.patientDetailsOncoTreeDiagnosis.text,
                    tumorTissue: this.patientDetailsTumorTissue,
                    icd10: this.patientDetailsICD10,
                    dedupAvgDepth: this.patientDetailsDedupAvgDepth,
                    dedupPctOver100X: this.patientDetailsDedupPctOver100X,
                    tumorPercent: this.patientDetailsTumorPercent,
                    caseId: this.$route.params.id,
                    skipSnackBar: skipSnackBar
                }
            }).then(response => {
                this.waitingForAjaxCount--;
                if (response.data.isAllowed && response.data.success) {
                    this.patientDetailsUnSaved = false;
                    this.getPatientDetails();
                    if (!this.skipSnackBar) {
                        this.showSnackBarMessage("Patient Details Saved");
                    }
                }
                else {
                    this.handleDialogs(response.data, this.savePatientDetails);
                }
                this.savingPatientDetails = false;
            }).catch(error => {
                this.savingPatientDetails = false;
                this.handleAxiosError(error);
            });
        },
        updateEditAnnotationBreadcrumbs(visible) {
            this.urlQuery.edit = visible;
            zingchart.exec("cnvPlotEdit", 'destroy'); //kill the chart if edit annotation is closed
            // console.log("killing cnv plot");
            this.updateRoute();
        },
        breadcrumbNavigation(index) {
            //change the urlQuery based on walking up the breadcrumbs
            var goBack = this.breadcrumbs.length - index;
            for (var i = 0; i < goBack; i++) {
                var currentBreadcrumb = this.breadcrumbs.pop();
                for (var j = 0; j < currentBreadcrumb.params.length; j++) {
                    this.urlQuery[currentBreadcrumb.params[j]] = null;
                }
            }
            this.updateRoute();
        },
        disableBreadCrumbItem(item, index) {
            return (item.disabled || index == this.breadcrumbs.length - 1);
        },
        handleWaitingForAjaxCount() {
            if (this.waitingForAjaxActive && this.waitingForAjaxCount <= 0) {
                this.waitingForAjaxActive = false;
                this.saveAllNeeded = false;
                this.showSnackBarMessage(this.waitingForAjaxMessage);
                clearInterval(this.autoSaveInterval);
                this.createAutoSaveInterval();
            }
        },
        createAutoSaveInterval() {
            this.autoSaveInterval = setInterval(() => {
                var editing = this.$route.query.edit === true || this.$route.query.edit === "true";
                if (!this.waitingForAjaxActive && !editing) {
                    this.handleSaveAll(true);
                }
            }, 120000);
        },
        openReport() {
            if (!this.reportReady) {
                return; //a disabled menu item will still call openReport. This should block it.
            }
            var path = webAppRoot + "/openReport";
            if (this.readonly) {
                path += "ReadOnly"
            }
            path += "/" + this.$route.params.id;
            router.push({path : path});
        },
        addOtherAnnotatorSelection(type, annotatorId, variantIds) {
            this.$refs.reviewDialog.startLoading();
            this.waitingForAjaxActive = true;
            //TODO
            //update dicts of selected from selectionPerAnnotator
            if (type == "snp") {
                for (let i=0; i < variantIds.length; i++) {
                    this.handleSelectionChangeFromVariantDetails(variantIds[i], type.toUpperCase(), true);
                    // this.unfilteredSNPsDict[variantIds[i]] =  {oid: variantIds[i], selected: true}
                }
            }
            if (type == "cnv") {
                for (let i=0; i < variantIds.length; i++) {
                    this.handleSelectionChangeFromVariantDetails(variantIds[i], type.toUpperCase(), true);
                    // this.unfilteredCNVsDict[variantIds[i]] =  {oid: variantIds[i], selected: true}
                }
            }
            if (type == "ftl") {
                for (let i=0; i < variantIds.length; i++) {
                    this.handleSelectionChangeFromVariantDetails(variantIds[i], type.toUpperCase(), true);
                    // this.unfilteredFTLsDict[variantIds[i]] =  {oid: variantIds[i], selected: true}
                }
            }
            this.getAjaxData().then(response => {
                this.updateSelectedVariantTable();
                this.variantUnSaved = true;
                this.waitingForAjaxActive = false;
            });
        }
    },
    mounted() {
        this.mountComponent();
        this.loadFromParams();
        this.updateBreadcrumbs();
        
    },
    created() {
        this.createAutoSaveInterval();
    },
    computed: {
        webAppRoot() {
            return webAppRoot;
        },
        saveTooltip() {
            return this.createSaveTooltip();
        }
    },
    destroyed: function () {
        clearInterval(this.autoSaveInterval);
    },
    watch: {
        '$route': "handleRouteChanged",
        variantTabActive: "handleTabChanged",
        waitingForAjaxCount: "handleWaitingForAjaxCount",
    }

};