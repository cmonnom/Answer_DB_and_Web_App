const OpenCase = {
    props: {
        "readonly": { default: true, type: Boolean }

    },
    template: `<div>
    <!-- goodies panel dialog -->
    <v-dialog v-model="showGoodiesPanel"  full-width hide-overlay fullscreen transition="fade-transition">
    <v-layout row wrap fill-height text-xs-center>
    <v-flex xs12>
    <goodies2 ref="goodiesPanel" @end-goodies="endGoodies()"></goodies2>
    </v-flex>
    <v-flex xs12 >
    <span class="display-1">Enjoy a quick break...</span><br/>
    <v-btn large color="warning" @click="endGoodies()">Back to work</v-btn>
    <v-tooltip bottom>
    <v-btn slot="activator" large color="error" @click="cancelBreaks()">No more breaks</v-btn>
    <span>Go to Preferences to reset breaks</span>
    </v-tooltip>
    </v-flex>
    </v-layout>
    </v-dialog>

    <!-- splash screen dialog -->
    <splash-screen ref="splashScreen" :splash-dialog="splashDialog" ></splash-screen>

  <!-- add CNV dialog -->
  <v-dialog v-model="addCNVDialogVisible" max-width="500px" scrollable>
    <add-cnv  @hide-add-cnv-panel="closeAddCNVDialog"
    :no-edit="!canProceed('canAnnotate') || readonly"
  :aberration-types="aberrationTypes"
  :cnv-chrom-list="cnvChromList"
  @refresh-cnv-table="getAjaxData"
  :current-gene-list="currentListOfCNVVisibleGenes"></add-cnv>
  </v-dialog>

  <!-- create ITD dialog -->
  <v-dialog v-model="itdDialogVisible" max-width="300px" v-if="canProceed('canSelect') && !readonly" persistent>
  <create-itd
  @hide-create-itd="itdDialogVisible = false"
  @refresh-variants="getAjaxData"
  @show-snackbar="showSnackBarMessageWithParams"></create-itd>
  </v-dialog>

    <div>
    <v-dialog v-model="confirmationDialogVisible" max-width="300px">
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
      @hide-fpkm-plot="closeFPKMChart"
      ></fpkm-plot>
      </v-menu>  
   
    <v-snackbar :timeout="snackBarTimeout" :bottom="true" v-model="snackBarVisible">
        {{ snackBarMessage }}
        <v-tooltip top>
        <a slot="activator" :href="snackBarLink"><v-icon dark>{{ snackBarLinkIcon }}</v-icon></a>
        <span>Open Link</span>
        </v-tooltip>
        <v-btn flat color="primary" @click="snackBarVisible = false">Close</v-btn>
    </v-snackbar>
    <advanced-filter ref="advancedFilter" @refresh-data="filterData" @save-filters="saveCurrentFilters" @delete-filter="deleteFilterSet"
        :type="currentFilterType"
        @update-highlight="updateHighlights" @filter-action-success="showSnackBarMessage"></advanced-filter>
    <v-dialog v-model="reviewDialogVisible" scrollable fullscreen hide-overlay persistent transition="dialog-bottom-transition">
        <review-selection ref="reviewDialog"
        @open-report="openReport"
        :report-ready="reportReady"
        :breadcrumbs="breadcrumbs"
        :case-name="caseName" :case-type="caseType" :case-type-icon="caseTypeIcon"
        :selected-ids="currentSelectedVariantIds"
        @save-selection="saveSelection" @close-review-dialog="closeReviewDialog"
        :is-save-badge-visible="isSaveNeededBadgeVisible()" :save-variant-disabled="saveVariantDisabled"
        @save-all="handleSaveAll" :waiting-for-ajax-active="waitingForAjaxActive" @show-snackbar="showSnackBarMessageWithParams"
        :save-tooltip="createSaveTooltip()"
        @review-selection-refresh="updateSelectedVariantTable()"></review-selection>
    </v-dialog>

    <!-- annotation dialog -->
    <edit-annotations type="snp" @saving-annotations="commitAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs"
        :color="colors.editAnnotation" ref="annotationDialog" :title="createVariantName() + ' -- ' + caseName + ' --'"
        :caseIcon="caseTypeIcon" :caseType="caseType"
        :breadcrumbs="breadcrumbs" :annotation-categories="annotationCategories" :annotation-tiers="variantTiers" :annotation-classifications="annotationClassifications"
        :annotation-phases="annotationPhases"
        @toggle-panel="handlePanelVisibility()" :annotation-variant-details-visible="editAnnotationVariantDetailsVisible"
        @breadcrumb-navigation="breadcrumbNavigation"
        :current-variant="currentVariant">
        <v-slide-y-transition slot="variantDetails">
            <v-flex xs12 md12 lg12 xl11 mb-2 v-show="editAnnotationVariantDetailsVisible">
            <variant-details :no-edit="true" :variant-data-tables="variantDataTables" :link-table="linkTable" :type="currentVariantType" 
                :width-class="getWidthClassForVariantDetails()" :current-variant="currentVariant" @hide-panel="handlePanelVisibility(false)"
                @show-panel="handlePanelVisibility(true)" @toggle-panel="handlePanelVisibility()" @revert-variant="revertVariant"
                @save-variant="saveVariant" :color="colors.variantDetails"
                :variant-type="currentVariantType" cnv-plot-id="cnvPlotEditUnused"
                :loading-variant="loadingVariant">
            </variant-details>
            </v-flex>
        </v-slide-y-transition>
        </edit-annotations>

    <edit-annotations type="cnv" @saving-annotations="commitAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs"
        :color="colors.editAnnotation" ref="cnvAnnotationDialog" :title="formatChrom(currentVariant.chrom)  + ' -- ' + caseName + ' --'" 
        :caseIcon="caseTypeIcon" :caseType="caseType" @toggle-panel="handlePanelVisibility()"
        :breadcrumbs="breadcrumbs" @breadcrumb-navigation="breadcrumbNavigation" :annotation-categories-c-n-v="annotationCategoriesCNV" :annotation-breadth="annotationBreadth"
        :annotation-phases="annotationPhases"
        :annotation-tiers="variantTiers" :annotation-classifications="annotationClassifications"
        :current-variant="currentVariant" :annotation-variant-details-visible="editAnnotationVariantDetailsVisible">
        <v-slide-y-transition slot="variantDetails">
            <v-flex xs12 md12 lg12 xl11 mb-2 v-show="editAnnotationVariantDetailsVisible">
                <variant-details :no-edit="true" :variant-data-tables="variantDataTables" :link-table="linkTable" :width-class="getWidthClassForVariantDetails()" :type="currentVariantType"
                    :current-variant="currentVariant" @hide-panel="handlePanelVisibility(false)" @show-panel="handlePanelVisibility(true)"
                    @toggle-panel="handlePanelVisibility()" @revert-variant="revertVariant" :color="colors.editAnnotation"
                    ref="cnvVariantDetailsPanel" cnv-plot-id="cnvPlotEdit" :cnv-chrom-list="cnvChromList"
                    :variant-type="currentVariantType"
                    :loading-variant="loadingVariant">

                </variant-details>
            </v-flex>
        </v-slide-y-transition>

    </edit-annotations>

    <edit-annotations type="translocation" @saving-annotations="commitAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs"
        :color="colors.editAnnotation" ref="translocationAnnotationDialog" :title="currentVariant.fusionName  + ' -- ' + caseName + ' --'"
        :caseIcon="caseTypeIcon" :caseType="caseType"
        :breadcrumbs="breadcrumbs" @breadcrumb-navigation="breadcrumbNavigation" :annotation-categories="annotationCategories"
        :annotation-tiers="variantTiers" :annotation-classifications="annotationClassifications" :annotation-variant-details-visible="editAnnotationVariantDetailsVisible"
        :annotation-phases="annotationPhases"
        :current-variant="currentVariant" @toggle-panel="handlePanelVisibility()">
        <v-slide-y-transition slot="variantDetails">
            <v-flex xs12 md12 lg12 xl11 mb-2 v-show="editAnnotationVariantDetailsVisible">
                    <variant-details :no-edit="true" :variant-data-tables="variantDataTables" :link-table="linkTable" :width-class="getWidthClassForVariantDetails()" :type="currentVariantType"
                        :current-variant="currentVariant" @hide-panel="handlePanelVisibility(false)" @show-panel="handlePanelVisibility(true)"
                        @toggle-panel="handlePanelVisibility()" @revert-variant="revertVariant" :color="colors.editAnnotation"
                        cnv-plot-id="cnvPlotEditUnusedFTL"
                        :variant-type="currentVariantType"
                        :loading-variant="loadingVariant">

                    </variant-details>
            </v-flex>
            </v-slide-y-transition>
        </edit-annotations>

    <!-- variant details dialog -->
    <v-dialog v-model="variantDetailsVisible" ref="variantDetailsDialog" scrollable fullscreen persistent hide-overlay transition="dialog-bottom-transition">
        <v-card class="soft-grey-background">
            <v-toolbar dense dark :color="colors.variantDetails">
                <v-menu offset-y offset-x class="ml-0">
                    <v-btn slot="activator" flat icon dark>
                        <v-icon>more_vert</v-icon>
                    </v-btn>
                    <v-list>
                        <v-list-tile class="list-menu">
                            <v-list-tile-content>
                                <v-list-tile-title>
                                    <v-menu offset-y offset-x close-delay="2000" open-on-hover>
                                        <span slot="activator">
                                            <v-icon class="pl-2 pr-4">keyboard_arrow_right</v-icon>Show / Hide
                                            <!-- This is a hack to extend the menu active area because the title is much shorter than other items -->
                                            <span v-for="i in 30" :key="i">&nbsp;</span>
                                        </span>
                                        <v-list>
                                            <v-list-tile avatar @click="annotationVariantDetailsVisible = !annotationVariantDetailsVisible">
                                                <v-list-tile-avatar>
                                                    <v-icon>zoom_in</v-icon>
                                                </v-list-tile-avatar>
                                                <v-list-tile-content>
                                                    <v-list-tile-title>Variant Details</v-list-tile-title>
                                                </v-list-tile-content>
                                            </v-list-tile>

                                            <v-list-tile v-if="isSNP()" avatar :disabled="!currentVariantHasRelatedVariants" @click="toggleRelatedVariants()">
                                                <v-list-tile-avatar>
                                                    <v-icon>link</v-icon>
                                                </v-list-tile-avatar>
                                                <v-list-tile-content>
                                                    <v-list-tile-title>Related Variants</v-list-tile-title>
                                                </v-list-tile-content>
                                            </v-list-tile>

                                            <v-list-tile v-if="isSNP()" avatar :disabled="!currentVariantHasRelatedCNV" @click="toggleRelatedCNV()">
                                            <v-list-tile-avatar>
                                                <v-icon>link</v-icon>
                                            </v-list-tile-avatar>
                                            <v-list-tile-content>
                                                <v-list-tile-title>Related CNV</v-list-tile-title>
                                            </v-list-tile-content>
                                        </v-list-tile>

                                            <v-list-tile v-if="isSNP()" avatar @click="annotationVariantCanonicalVisible = !annotationVariantCanonicalVisible">
                                                <v-list-tile-avatar>
                                                    <v-icon>mdi-table-search</v-icon>
                                                </v-list-tile-avatar>
                                                <v-list-tile-content>
                                                    <v-list-tile-title> Canonical VCF Annotations</v-list-tile-title>
                                                </v-list-tile-content>
                                            </v-list-tile>

                                            <v-list-tile v-if="isSNP()" avatar @click="annotationVariantOtherVisible = !annotationVariantOtherVisible">
                                                <v-list-tile-avatar>
                                                    <v-icon>mdi-table-search</v-icon>
                                                </v-list-tile-avatar>
                                                <v-list-tile-content>
                                                    <v-list-tile-title>Other VCF Annotations</v-list-tile-title>
                                                </v-list-tile-content>
                                            </v-list-tile>


                                            <v-list-tile v-if="isSNP() || isCNV()" avatar @click="mdaAnnotationsVisible = !mdaAnnotationsVisible" :disabled="!mdaAnnotationsExists()">
                                                <v-list-tile-avatar>
                                                    <v-icon v-if="mdaAnnotationsExists()">mdi-message-bulleted</v-icon>
                                                    <v-icon v-else>mdi-message-bulleted-off</v-icon>
                                                </v-list-tile-avatar>
                                                <v-list-tile-content>
                                                    <v-list-tile-title v-if="mdaAnnotationsExists()">MDA Annotations</v-list-tile-title>
                                                    <v-list-tile-title v-else>No MDA Annotations</v-list-tile-title>
                                                </v-list-tile-content>
                                            </v-list-tile>

                                            <v-list-tile avatar @click="utswAnnotationsVisible = !utswAnnotationsVisible" :disabled="!utswAnnotationsExists()">
                                                <v-list-tile-avatar>
                                                    <v-icon v-if="utswAnnotationsExists()">mdi-message-bulleted</v-icon>
                                                    <v-icon v-else>mdi-message-bulleted-off</v-icon>
                                                </v-list-tile-avatar>
                                                <v-list-tile-content>
                                                    <v-list-tile-title v-if="utswAnnotationsExists()">UTSW Annotations</v-list-tile-title>
                                                    <v-list-tile-title v-else>No UTSW Annotations</v-list-tile-title>
                                                </v-list-tile-content>
                                            </v-list-tile>

                                        </v-list>
                                    </v-menu>
                                </v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile v-if="isSNP()" avatar @click="openBamViewerLinkWeb()">
                            <v-list-tile-avatar>
                                IGV
                            </v-list-tile-avatar>
                            <v-list-tile-content class="mb-2">
                                <v-list-tile-title>Open IGV (web) <v-icon>mdi-web</v-icon></v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile v-if="isSNP()" avatar @click="downloadIGVFile('jnlp')">
                        <v-list-tile-avatar>
                            IGV
                        </v-list-tile-avatar>
                        <v-list-tile-content  class="mb-2">
                            <v-list-tile-title>Open IGV (desktop) <v-icon>mdi-desktop-mac-dashboard</v-icon></v-list-tile-title>
                        </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile v-if="isSNP()" avatar @click="downloadIGVFile('session')">
                        <v-list-tile-avatar>
                            IGV
                        </v-list-tile-avatar>
                        <v-list-tile-content  class="mb-2">
                            <v-list-tile-title>Download IGV Session <v-icon>mdi-file-xml</v-icon></v-list-tile-title>
                        </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile avatar @click="startUserAnnotations()" :disabled="!canProceed('canAnnotate')">
                            <v-list-tile-avatar>
                                <v-icon>create</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Create/Edit Your Annotations</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile avatar @click="selectVariantForReport()" v-if="!currentRow.isSelected" :disabled="!canProceed('canSelect')">
                            <v-list-tile-avatar>
                                <v-icon>done</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Select Variant</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile avatar @click="removeVariantFromReport()" v-else :disabled="!canProceed('canSelect')">
                            <v-list-tile-avatar>
                                <v-icon>done</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Deselect Variant</v-list-tile-title>
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

                        <v-list-tile avatar @click="closeVariantDetails()">
                            <v-list-tile-avatar>
                                <v-icon>cancel</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Close Variant</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>


                    </v-list>
                </v-menu>
                <v-toolbar-title class="ml-0">
          Annotations for 
          <span v-if="isSNP()">SNP</span>
          <span v-else-if="isCNV()">CNV</span>
          <span v-else-if="isTranslocation()">FTL</span>
          Variant:
              <v-tooltip bottom v-if="variantNameIsTooLong() && isSNP()">
              <span slot="activator" v-text="createVariantName()"></span>
              <span>{{ currentVariant.geneName }} {{ currentVariant.notation }}</span>
              </v-tooltip>  
              <span v-if="!variantNameIsTooLong() && isSNP()">{{ currentVariant.geneName }} {{ currentVariant.notation }}</span>
              <span v-else-if="isCNV()" v-text="formatChrom(currentVariant.chrom)"></span>
              <span v-else-if="isTranslocation()">{{ currentVariant.fusionName }}</span>
              <span> -- {{ caseName }} -- </span> 
            <v-tooltip bottom>
              <v-icon slot="activator" size="20" class="pb-1"> {{ caseTypeIcon }} </v-icon>
            <span>{{caseType}} case</span>  
            </v-tooltip>

                </v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon flat :color="annotationVariantDetailsVisible ? 'amber accent-2' : ''" @click="annotationVariantDetailsVisible = !annotationVariantDetailsVisible"
                        slot="activator">
                        <v-icon>zoom_in</v-icon>
                    </v-btn>
                    <span>Show/Hide Variant Details</span>
                </v-tooltip>
                <v-tooltip bottom v-if="isSNP()">
                    <v-btn icon flat :color="annotationVariantCanonicalVisible ? 'amber accent-2' : ''" @click="annotationVariantCanonicalVisible = !annotationVariantCanonicalVisible"
                        slot="activator">
                        <v-icon>mdi-table-search</v-icon>
                    </v-btn>
                    <span>Show/Hide Canonical VCF Annotations</span>
                </v-tooltip>
                <v-tooltip bottom v-if="isSNP || isCNV()">
                    <v-btn :disabled="!mdaAnnotationsExists()" icon flat :color="(mdaAnnotationsVisible && mdaAnnotationsExists()) ? 'amber accent-2' : ''"
                        @click="mdaAnnotationsVisible = !mdaAnnotationsVisible" slot="activator">
                        <v-icon v-if="mdaAnnotationsExists()">mdi-message-bulleted</v-icon>
                        <v-icon v-else>mdi-message-bulleted-off</v-icon>
                    </v-btn>
                    <span v-if="mdaAnnotationsExists()">Show/Hide MDA Annotations</span>
                    <span v-else>No MDA Annotations</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <v-btn :disabled="!utswAnnotationsExists()" icon flat :color="(utswAnnotationsVisible && utswAnnotationsExists()) ? 'amber accent-2' : ''"
                        @click="utswAnnotationsVisible = !utswAnnotationsVisible" slot="activator">
                        <v-icon v-if="utswAnnotationsExists()">mdi-message-bulleted</v-icon>
                        <v-icon v-else>mdi-message-bulleted-off</v-icon>
                    </v-btn>
                    <span v-if="utswAnnotationsExists()">Show/Hide UTSW Annotations</span>
                    <span v-else>No UTSW Annotations</span>
                </v-tooltip>
                <v-menu origin="center center" transition="slide-y-transition" bottom open-on-hover offset-y v-if="isSNP()">
                <v-btn icon flat slot="activator">IGV
                </v-btn>
                <v-card color="primary">
                <v-tooltip bottom v-if="isSNP()">
                    <v-btn ref="bamViewerLink" dark icon flat slot="activator" :href="createBamViewerLink()" target="_blank" rel="noreferrer">
                        <v-icon>mdi-web</v-icon> 
                    </v-btn>
                    <span>Open IGV (web)</span>
                </v-tooltip>
                <br/>
                <v-tooltip bottom v-if="isSNP()">
                    <v-btn ref="bamViewerLinkDesktop" dark icon flat slot="activator" @click="downloadIGVFile('jnlp')">
                        <v-icon>mdi-desktop-mac-dashboard</v-icon>
                    </v-btn>
                    <span>Open IGV (desktop)</span>
                </v-tooltip>
                <br/>
                <v-tooltip bottom v-if="isSNP()">
                    <v-btn ref="bamViewerLinkDesktop" dark icon flat slot="activator" @click="downloadIGVFile('session')">
                        <v-icon>mdi-file-xml</v-icon>
                    </v-btn>
                    <span>Download IGV Session</span>
                </v-tooltip>
                </v-card>
                </v-menu>

                <v-badge color="red" right bottom overlap v-model="isSaveNeededBadgeVisible()" class="mini-badge">
                <v-icon slot="badge"></v-icon>
                <v-tooltip bottom offset-overflow nudge-left="100px" min-width="200px">
                <v-btn flat icon @click="handleSaveAll()" slot="activator" :loading="waitingForAjaxActive" :disabled="!isSaveNeededBadgeVisible()">
                    <v-icon>save</v-icon>
                </v-btn>
                <span v-html="createSaveTooltip()"></span>
            </v-tooltip>
            </v-badge>

                <v-tooltip bottom>
                    <v-btn icon @click="closeVariantDetails()" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span v-if="!isSaveNeededBadgeVisible()">Save & Close Variant</span>
                    <span v-else>Save & Close Variant</span>
                </v-tooltip>

            </v-toolbar>

            <v-card-text :style="getDialogMaxHeight(120)" class="pl-2 pr-2">

                <v-breadcrumbs class="pt-2 pb-2">
                    <v-icon slot="divider">forward</v-icon>
                    <v-breadcrumbs-item v-for="(item, index) in breadcrumbs" :key="item.text" :disabled="disableBreadCrumbItem(item, index)"
                        @click.native="breadcrumbNavigation(index)">
                        {{ item.text }}
                    </v-breadcrumbs-item>
                </v-breadcrumbs>

                <v-container grid-list-md fluid pl-2 pr-2 pt-2 pb-2>
                    <v-layout row wrap>
                        <v-slide-y-transition>
                            <v-flex xs12 v-show="annotationAllHidden()">
                                <p class="text-xs-right subheading">
                                    <v-icon style="transform: rotate(90deg) translateX(-5px);">keyboard_return</v-icon>
                                    <span>Click on a icon to show variant details.</span>
                                </p>
                            </v-flex>
                        </v-slide-y-transition>
                        <!-- card showing the same data as the summary row -->
                        <v-slide-y-transition>
                            <v-flex xs12 md12 lg12 xl11 v-show="annotationVariantDetailsVisible">

                                <variant-details :no-edit="!canProceed('canAnnotate') || readonly" :variant-data-tables="variantDataTables" :link-table="linkTable" :type="currentVariantType"
                                    :width-class="getWidthClassForVariantDetails()" :current-variant="currentVariant" @hide-panel="handlePanelVisibility(false)"
                                    @show-panel="handlePanelVisibility(true)" @toggle-panel="handlePanelVisibility()" @revert-variant="revertVariant"
                                    @save-variant="saveVariant" :color="colors.variantDetails" ref="variantDetailsPanel" @variant-details-changed=""
                                    :variant-type="currentVariantType" cnv-plot-id="cnvPlotDetails" :cnv-chrom-list="cnvChromList"
                                    :loading-variant="loadingVariant"
                                    @open-lookup-link="openLookupLink">
                                </variant-details>

                            </v-flex>
                        </v-slide-y-transition>
                        <v-slide-y-transition>
                            <v-flex xs12 sm12 md9 lg7 xl5 v-show="isRelatedVariantsVisible()">
                                <div>
                                    <data-table ref="relatedVariantAnnotation" :fixed="false" :fetch-on-created="false" table-title="Related Variants" initial-sort="geneId"
                                        no-data-text="No Data" :show-pagination="false" title-icon="link" :color="colors.variantDetails">
                                    </data-table>
                                </div>
                            </v-flex>
                        </v-slide-y-transition>
                        <v-slide-y-transition>
                            <v-flex xs12 sm12 md9 lg7 xl5 v-show="isRelatedCNVVisible()">
                                <div>
                                    <data-table ref="relatedCNVAnnotation" :fixed="false" :fetch-on-created="false" table-title="Related CNV" initial-sort="geneId"
                                        no-data-text="No Data" :show-pagination="false" title-icon="link" :color="colors.variantDetails">
                                    </data-table>
                                </div>
                            </v-flex>
                        </v-slide-y-transition>
                        <v-slide-y-transition>
                            <v-flex xs12 v-show="annotationVariantCanonicalVisible && isSNP()">
                                <div>
                                    <data-table ref="canonicalVariantAnnotation" :fixed="false" :fetch-on-created="false" table-title="Canonical VCF Annotations"
                                        initial-sort="geneId" no-data-text="No Data" :show-pagination="false" title-icon="mdi-table-search"
                                        :color="colors.variantDetails">
                                    </data-table>
                                </div>
                            </v-flex>
                        </v-slide-y-transition>
                        <v-slide-y-transition>
                            <v-flex xs12 v-show="annotationVariantOtherVisible  && isSNP()">
                                <data-table ref="otherVariantAnnotations" :fixed="false" :fetch-on-created="false" table-title="Other VCF Annotations" initial-sort="geneId"
                                    no-data-text="No Data" :show-row-count="true" title-icon="mdi-table-search" :color="colors.variantDetails">
                                </data-table>
                            </v-flex>
                        </v-slide-y-transition>
                        <!-- MDA Annotation card -->
                        <v-slide-y-transition>
                            <v-flex xs12 v-show="mdaAnnotationsVisible && mdaAnnotationsExists()">
                                <v-card class="soft-grey-background">
                                    <v-toolbar class="elevation-0" dense dark :color="colors.variantDetails">
                                        <v-toolbar-title>
                                            <v-icon color="amber accent-2">mdi-message-bulleted</v-icon>
                                            MD Anderson Annotations
                                        </v-toolbar-title>
                                        <v-spacer></v-spacer>
                                        <v-tooltip bottom>
                                            <v-btn icon @click="mdaAnnotationsVisible = false" slot="activator">
                                                <v-icon>close</v-icon>
                                            </v-btn>
                                            <span>Close</span>
                                        </v-tooltip>
                                    </v-toolbar>
                                    <!-- <v-card-text v-for="(annotationCategory, index) in mdaAnnotations.annotationCategories" :key="index">
                                        <v-card flat v-if="annotationCategory">
                                            <v-card-title class="subheading">{{ annotationCategory.title }}:</v-card-title>
                                            <v-card-text class="pl-2 pr-2">{{ annotationCategory.text }} </v-card-text>
                                        </v-card>
                                    </v-card-text> -->
                                    <v-card-text>
                                        <v-container grid-list-md fluid pl-2 pr-2 pt-2 pb-2>
                                    <v-layout row wrap>
                                    <v-flex xs12 sm12 md6 lg6 xl4 v-for="(annotation, index) in mdaAnnotationsFormatted" :key="index" v-show="annotation.visible">
                                        <mda-annotation-card :annotation="annotation" :variant-type="currentVariantType"
                                        :no-edit="!canProceed('canAnnotate') || readonly"
                                        @copy-mda-annotation="copyMDAAnnotation"
                                        ></mda-annotation-card>
                                    </v-flex>
                                    </v-layout>
                                    </v-container>
                                    </v-card-text>
                                </v-card>
                            </v-flex>
                        </v-slide-y-transition>
                        <v-slide-y-transition>
                            <v-flex xs12 v-show="utswAnnotationsVisible && utswAnnotationsExists()">
                                <v-card class="soft-grey-background">
                                    <v-toolbar class="elevation-0" dense dark :color="loadingVariant ? loadingColor : colors.variantDetails">
                                    <v-menu offset-y offset-x class="ml-0">
                                    <v-btn slot="activator" flat icon dark>
                                                <v-icon color="amber accent-2">mdi-message-bulleted</v-icon>
                                            </v-btn>
                                            <v-list>
                                                <v-list-tile avatar @click="searchAnnotationsVisible = !searchAnnotationsVisible">
                                                    <v-list-tile-avatar>
                                                        <v-icon>search</v-icon>
                                                    </v-list-tile-avatar>
                                                    <v-list-tile-content>
                                                        <v-list-tile-title>Show/Hide Search Annotations</v-list-tile-title>
                                                    </v-list-tile-content>
                                                </v-list-tile>

                                                <v-list-tile avatar v-if="canProceed('canAnnotate') && !readonly" @click="startUserAnnotations">
                                                    <v-list-tile-avatar>
                                                        <v-icon>create</v-icon>
                                                    </v-list-tile-avatar>
                                                    <v-list-tile-content>
                                                        <v-list-tile-title>Create/Edit Your Annotations</v-list-tile-title>
                                                    </v-list-tile-content>
                                                </v-list-tile>

                                                <v-list-tile avatar @click="utswAnnotationsVisible = false">
                                                    <v-list-tile-avatar>
                                                        <v-icon>mdi-message-bulleted</v-icon>
                                                    </v-list-tile-avatar>
                                                    <v-list-tile-content>
                                                        <v-list-tile-title>Close UTSW Annotations</v-list-tile-title>
                                                    </v-list-tile-content>
                                                </v-list-tile>
                                            </v-list>
                                </v-menu>
                                        <v-toolbar-title class="ml-0">UTSW Annotations</v-toolbar-title>
                                        <v-spacer></v-spacer>
                                        <v-tooltip bottom>
                                            <v-btn flat icon @click="searchAnnotationsVisible = !searchAnnotationsVisible" slot="activator" :color="searchAnnotationsVisible ? 'amber accent-2' : ''">
                                                <v-icon>search</v-icon>
                                            </v-btn>
                                            <span>Show/Hide Search Annotations</span>
                                        </v-tooltip>
                                        <v-tooltip bottom>
                                        <v-btn flat icon v-if="canProceed('canAnnotate') && !readonly" @click="startUserAnnotations()" slot="activator">
                                            <v-icon>create</v-icon>
                                        </v-btn>
                                        <span>Create/Edit Your Annotations</span>
                                        </v-tooltip>

                                        <v-tooltip bottom>
                                            <v-btn icon @click="utswAnnotationsVisible = false" slot="activator">
                                                <v-icon>close</v-icon>
                                            </v-btn>
                                            <span>Close</span>
                                        </v-tooltip>
                                    </v-toolbar>
                                    <v-card-text>
                                        <v-container grid-list-md fluid pl-2 pr-2 pt-2 pb-2>
                                            <v-layout row wrap>
                                            <v-slide-y-transition>
                                                <v-flex xs12 v-show="searchAnnotationsVisible">
                                                    <v-card>
                                                        <v-card-title class="subheading">
                                                            Search Annotation Cards
                                                            <v-spacer></v-spacer>

                                                        </v-card-title>
                                                        <v-card-text class="pl-2 pr-2">
                                                            <v-layout row wrap>
                                                                <v-flex xs6 sm6 md3 lg3 xl2>
                                                                    <v-text-field clearable append-icon="search" label="Search any text" v-model="searchAnnotations" @input="matchAnnotationFilter"></v-text-field>
                                                                </v-flex>
                                                                <v-flex xs6 sm6 md3 lg3 xl2>
                                                                    <v-select v-if="isSNP() || isTranslocation()" clearable :value="searchAnnotationCategory" :items="annotationCategories" v-model="searchAnnotationCategory"
                                                                        label="Search by Category" multiple @input="matchAnnotationFilter"></v-select>
                                                                    <v-select v-else-if="isCNV()" clearable :value="searchAnnotationCategory" :items="annotationCategoriesCNV" v-model="searchAnnotationCategory"
                                                                        label="Search by Category" multiple @input="matchAnnotationFilter"></v-select>
                                                                </v-flex>

                                                                <v-flex xs6 sm6 md3 lg3 xl2 v-if="isCNV()">
                                                                    <v-select  clearable :value="searchAnnotationBreadth" :items="annotationBreadth" v-model="searchAnnotationBreadth"
                                                                        label="Search by Breadth" multiple @input="matchAnnotationFilter"></v-select>
                                                                </v-flex>

                                                                <v-flex xs6 sm6 md3 lg3 xl2>
                                                                    <v-select clearable :value="searchAnnotationClassification" :items="annotationClassifications" v-model="searchAnnotationClassification"
                                                                        label="Search by Classification" multiple @input="matchAnnotationFilter"></v-select>
                                                                </v-flex>

                                                                <v-flex xs6 sm6 md3 lg3 xl2>
                                                                    <v-select clearable :value="searchAnnotationTier" :items="variantTiers" v-model="searchAnnotationTier" label="Search by Tier"
                                                                        multiple @input="matchAnnotationFilter"></v-select>
                                                                </v-flex>

                                                                <v-flex xs6 sm6 md3 lg3 xl2>
                                                                    <v-select v-if="isSNP()" clearable :value="searchAnnotationScope" :items="scopesSNP" v-model="searchAnnotationScope" label="Search by Scope"
                                                                        multiple @input="matchAnnotationFilter"></v-select>
                                                                    <v-select v-else-if="isCNV()" clearable :value="searchAnnotationScope" :items="scopesCNV" v-model="searchAnnotationScope" label="Search by Scope"
                                                                        multiple @input="matchAnnotationFilter"></v-select>
                                                                    <v-select v-else-if="isTranslocation()" clearable :value="searchAnnotationScope" :items="scopesTranslocation" v-model="searchAnnotationScope"
                                                                        label="Search by Scope" multiple @input="matchAnnotationFilter"></v-select>
                                                                </v-flex>

                                                            </v-layout>
                                                        </v-card-text>
                                                    </v-card>
                                                </v-flex>
                                                </v-slide-y-transition>    
                                                <v-flex xs12 sm12 md6 lg6 xl4 v-for="(annotation, index) in utswAnnotationsFormatted" :key="index" v-show="annotation.visible">
                                                    <utsw-annotation-card :annotation="annotation" :variant-type="currentVariantType"
                                                    :no-edit="!canProceed('canAnnotate') || readonly"
                                                    :can-copy="canCopyAnnotation && canProceed('canAnnotate') && !readonly && !loadingVariant"
                                                    @annotation-selection-changed="handleAnnotationSelectionChanged()"
                                                    @copy-annotation="copyAnnotation"
                                                    :class="getHighlightClass(index)"></utsw-annotation-card>
                                                </v-flex>
                                            </v-layout>
                                        </v-container>
                                    </v-card-text>
                                </v-card>
                            </v-flex>
                        </v-slide-y-transition>
                    </v-layout>
                </v-container>
            </v-card-text>
            <v-card-actions class="card-actions-bottom">
                <v-tooltip top class="pr-2">
                    <v-btn color="primary" @click="startUserAnnotations()" slot="activator" :disabled="!canProceed('canAnnotate') || readonly">Add/Edit
                        <v-icon right dark>create</v-icon>
                    </v-btn>
                    <span>Create/Edit Your Annotations</span>
                </v-tooltip>
                <v-btn class="mr-2" v-if="!currentRow.isSelected" :disabled="!canProceed('canSelect') || readonly" color="success"
                    @click="selectVariantForReport()" slot="activator">Select Variant
                    <v-icon right dark>done</v-icon>
                </v-btn>
                <v-btn class="mr-2" v-else :disabled="!canProceed('canSelect') || readonly" color="warning"
                    @click="removeVariantFromReport()" slot="activator">Deselect Variant
                    <v-icon right dark>done</v-icon>
                </v-btn>
                <v-tooltip top class="pr-2">
                    <v-btn :disabled="isFirstVariant" color="primary" @click="loadPrevVariant()" :loading="loadingVariant" slot="activator">Prev. Variant
                        <v-icon right dark>chevron_left</v-icon>
                    </v-btn>
                    <span>Show Previous Variant</span>
                </v-tooltip>
                <v-tooltip top class="pr-2">
                    <v-btn :disabled="isLastVariant" color="primary" @click="loadNextVariant()" :loading="loadingVariant" slot="activator">
                        <v-icon left dark>chevron_right</v-icon>
                        Next Variant
                    </v-btn>
                    <span>Show Next Variant</span>
                </v-tooltip>
                <v-tooltip top class="pr-2">
                <v-btn color="success" @click="handleSaveAll()" slot="activator" :loading="waitingForAjaxActive" :disabled="!isSaveNeededBadgeVisible()">
                    Save Work
                <v-icon right dark>save</v-icon>
                </v-btn>
                <span>Save Current Work</span>
            </v-tooltip class="pr-2">
                <v-btn color="error" @click="closeVariantDetails()">
                <span v-if="!isSaveNeededBadgeVisible()">Close</span>
                <span v-else>Save & Close</span>
                    <v-icon right dark>cancel</v-icon>
                </v-btn>
            </v-card-actions>
        </v-card>
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
                                    <v-menu offset-y offset-x close-delay="2000" open-on-hover>
                                            <span slot="activator">
                                                <v-icon class="pl-2 pr-4">keyboard_arrow_right</v-icon>IGV
                                                <!-- This is a hack to extend the menu active area because the title is much shorter than other items -->
                                                <span v-for="i in 30" :key="i">&nbsp;</span>
                                            </span>
                                            <v-list>
                                    <v-list-tile v-if="isSNP()" avatar @click="openBamViewerLinkWebFLT3()">
                                            <v-list-tile-avatar>
                                                <v-icon>mdi-web</v-icon>
                                            </v-list-tile-avatar>
                                            <v-list-tile-content class="mb-2">
                                                <v-list-tile-title>Open FLT3 locus (web)</v-list-tile-title>
                                            </v-list-tile-content>
                                        </v-list-tile>

                                        <v-list-tile v-if="isSNP()" avatar @click="downloadIGVFile('jnlp', flt3ITDLocus)">
                                        <v-list-tile-avatar>
                                            <v-icon>mdi-desktop-mac-dashboard</v-icon>
                                        </v-list-tile-avatar>
                                        <v-list-tile-content  class="mb-2">
                                            <v-list-tile-title>Open FLT3 locus (desktop)</v-list-tile-title>
                                        </v-list-tile-content>
                                        </v-list-tile>

                                        <v-list-tile v-if="isSNP()" avatar @click="downloadIGVFile('session', flt3ITDLocus)">
                                        <v-list-tile-avatar>
                                            <v-icon>mdi-file-xml</v-icon>
                                        </v-list-tile-avatar>
                                        <v-list-tile-content  class="mb-2">
                                            <v-list-tile-title>Download IGV Session for FLT3 locus</v-list-tile-title>
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

    <v-menu origin="center center" transition="slide-y-transition" bottom open-on-hover offset-y v-if="isSNP()">
    <v-btn icon flat slot="activator">IGV
    </v-btn>
    <v-card color="primary">
    <v-tooltip bottom v-if="isSNP()">
        <v-btn ref="bamViewerLinkFLT3" dark icon flat slot="activator" :href="createBamViewerLinkFLT3()" target="_blank" rel="noreferrer">
            <v-icon>mdi-web</v-icon> 
        </v-btn>
        <span>Open FLT3 locus (web)</span>
    </v-tooltip>
    <br/>
    <v-tooltip bottom v-if="isSNP()">
        <v-btn ref="bamViewerLinkDesktop" dark icon flat slot="activator" @click="downloadIGVFile('jnlp', flt3ITDLocus)">
            <v-icon>mdi-desktop-mac-dashboard</v-icon>
        </v-btn>
        <span>Open FLT3 locus (desktop)</span>
    </v-tooltip>
    <br/>
    <v-tooltip bottom v-if="isSNP()">
        <v-btn ref="bamViewerLinkDesktop" dark icon flat slot="activator" @click="downloadIGVFile('session', flt3ITDLocus)">
            <v-icon>mdi-file-xml</v-icon>
        </v-btn>
        <span>Download IGV Session for FLT3 locus</span>
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
            <v-btn flat icon @click="handleSaveAll()" slot="activator" :loading="waitingForAjaxActive" :disabled="!isSaveNeededBadgeVisible()">
                <v-icon>save</v-icon>
            </v-btn>
            <span v-html="createSaveTooltip()"></span>
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
                        @refresh-requested="handleRefresh()" :show-left-menu="true" @showing-buttons="toggleGeneVariantDetailsButtons"
                        @datatable-selection-changed="handleSelectionChanged" :color="colors.openCase" :external-filtering-active="isFilteringActiveForType('snp', 'geneVariantDetails')"
                        >
                        <v-fade-transition slot="action1">
                            <v-tooltip bottom v-show="geneVariantDetailsTableHovering">
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
                        :highlights="highlights" :external-filtering-active="isFilteringActiveForType('cnv', 'cnvDetails')">
                        <v-fade-transition slot="action1">
                            <v-tooltip bottom v-show="geneVariantDetailsTableHovering">
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
                        <v-tooltip bottom v-show="geneVariantDetailsTableHovering">
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
                        :show-left-menu="true" @datatable-selection-changed="handleSelectionChanged" :color="colors.openCase" :external-filtering-active="isFilteringActiveForType('ftl', 'translocationDetails')">
                        <v-fade-transition slot="action1">
                        <v-tooltip bottom v-show="geneVariantDetailsTableHovering">
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
</div>`, data() {
        return {
            oncotreeIconUrl: oncotreeIconUrl,
            firstTimeLoading: true,
            loading: true,
            loadingVariantDetails: false,
            // breadcrumbs: [{text: "You are here:  Case", disabled: true}],
            breadcrumbItemVariantDetails: { text: "Variant Details", disabled: false, params: ["variantId", "variantType"] },
            breadcrumbItemReview: { text: "Review", disabled: false, params: ["showReview"] },
            breadcrumbItemEditAnnotation: { text: "Add / Edit Annotation", disabled: false, params: ["edit"] },
            breadcrumbItemWorkOnCase: { text: "Case Overview", disabled: false, params: [] },
            breadcrumbs: [],
            patientTables: [],
            patientDetailsVisible: false,
            caseAnnotationsVisible: false,
            variantTabsVisible: true,
            caseName: "",
            caseId: "",
            variantDetailsVisible: false,
            currentVariant: {},
            currentRow: {},
            currentVariantFlags: [],
            variantDataTables: [],
            linkTable: [],
            reviewDialogVisible: false,
            annotationVariantDetailsVisible: true,
            annotationVariantRelatedVisible: true,
            annotationCNVRelatedVisible: true,
            annotationVariantCanonicalVisible: true,
            annotationVariantOtherVisible: true,
            saveVariantDisabled: false,
            variantUnSaved: false,
            // annotationDialogVisible: false,
            userAnnotations: [],
            snackBarMessage: "",
            snackBarVisible: false,
            snackBarLink: "",
            snackBarLinkIcon: "",
            snackBarTimeout: 4000,
            utswAnnotations: [],
            utswAnnotationsFormatted: [],
            mdaAnnotations: "",
            mdaAnnotationsFormatted: [],
            mdaAnnotationsVisible: true,
            utswAnnotationsVisible: true,
            bamViewerVisible: false,
            externalWindow: null,
            externalWindowOpen: false,
            exportLoading: false,
            saveLoading: false,
            sendToMDALoading: false,
            caseAnnotation: { caseAnnotation: "" },
            caseAnnotationOriginalText: "", //to verify if there has been a modification
            currentVariantType: "snp",
            //confirmation dialog
            confirmationDialogVisible: false,
            confirmationMessage: "Unsaved selected variants will be discarded.<br/>Are you sure?",
            confirmationProceedButton: "Proceed",
            confirmationCancelButton: "Cancel",
            reportGroups: [],
            geneVariantDetailsTableHovering: true,
            variantTabActive: null,
            wasAdvancedFilteringVisibleBeforeTabChange: false,
            currentVariantHasRelatedVariants: false,
            currentVariantHasRelatedCNV: false,
            isFirstVariant: false,
            isLastVariant: false,
            variantTiers: [
                '1A',
                '1B',
                '2C',
                '2D',
                '3',
                '4'
                ],
            annotationCategories: [
                'Gene Function',
                'Epidemiology',
                'Variant Function',
                'Prognosis',
                'Diagnosis',
                'Therapy',
                'Likely Artifact'],
            annotationCategoriesCNV: [
                'Epidemiology',
                'Prognosis',
                'Diagnosis',
                'Therapy'],
            annotationBreadth: [
                'Chromosomal',
                'Focal'],
            annotationClassifications: [
                'VUS',
                'Benign',
                'Likely benign',
                'Likely pathogenic',
                'Pathogenic'],
            annotationPhases: ["Phase 1", "Phase 2", "Phase 3", "Phase 4"],
            scopesSNP: [
                'Case', 'Gene', 'Variant', 'Tumor'
            ],
            scopesCNV: [
                'Case', 'Tumor'
            ],
            scopesTranslocation: [
                'Case', 'Tumor'
            ],
            aberrationTypes: [
                'amplification',
                'gain',
                'hemizygous loss',
                'homozygous loss',
                'ITD'
            ],
            variantDetailsUnSaved: false,
            patientDetailsUnSaved: false,
            savingVariantDetails: false,
            savingPatientDetails: false,
            tempSelectedSNPVariants: [],
            tempSelectedCNVs: [],
            tempSelectedTranslocations: [],
            topMostDialog: "",
            patientDetailsOncoTreeDiagnosis: "",
            patientDetailsTumorTissue: "",
            patientDetailsICD10: "",
            qcUrl: "",
            mutationalSignatureUrl: "",
            editAnnotationVariantDetailsVisible: true,
            urlQuery: {
                showReview: false,
                variantId: null,
                variantType: null,
                edit: false
            },
            // colors: {
            //     openCase: "primary",
            //     variantDetails: "teal lighten-1",
            //     saveReview: "teal",
            //     editAnnotation: "teal darken-1"
            // },
            colors: {
                openCase: "primary",
                variantDetails: "primary",
                saveReview: "primary",
                editAnnotation: "primary"
            },
            searchAnnotations: "",
            searchAnnotationsVisible: false,
            searchAnnotationClassification: [],
            searchAnnotationCategory: [],
            searchAnnotationBreadth: [],
            searchAnnotationTier: [],
            searchAnnotationScope: [],
            annotationSelectionUnSaved: false,
            savingAnnotationSelection: false,
            splashDialog: splashDialog,
           
            splashProgress: 0,
            splashSteps: 0,
            splashTextVisible: true,
            annotationIdsForReporting: [], //save the state of the selection in case the user close/open another page
            currentFilterType: "snp",
            highlights: {
                genes: []
            },
            oncotree: [],
            waitingForGoodies: false,
            caseType: "",
            caseTypeIcon: "",
            caseTypeIconSize: 20,
            showNormalSnackBar: true,
            waitingForAjaxCount: 0, //use this variable to wait for other Ajax calls to return. Each ajax call should decrease the amount by one
            waitingForAjaxMessage: "",
            waitingForAjaxActive: false,
            saveAllNeeded: false,
            caseNotesChanged: false,
            autoSaveInterval: null,
            cnvChromList: [],
            addCNVDialogVisible: false,
            showGoodiesPanel: false,
            patientDetailsDedupPctOver100X: "",
            patientDetailsDedupAvgDepth: "",
            patientDetailsTumorPercent: "",
            numberRules: [(v) => { return !isNaN(v) || 'Invalid value' }],
            currentListOfCNVVisibleGenes: [],
            reportReady: false,
            labNotes: null,
            snpIndelUnfilteredItems: null,
            cnvUnfilteredItems: null,
            ftlUnfilteredItems: null,
            itdDialogVisible: false,
            loadingVariant: false,
            currentSelectedVariantIds: {},
            creatingOtherTissueTypes: false,
            updatingSelectedVariantTable: false,
            loadingColor: "blue-grey lighten-4",
            userId: null,
            caseOwnerId: null,
            currentItem: null,
            fpkmVisible: false,
            fpkmPositionx: 0,
            fpkmPositiony: 0,
            snpItemsTemp: [], //a temp list of selected variants
            flt3ITDLocus: "chr13:28,033,867-28,034,235",
            highlightLatestAnnotation: false,
            canCopyAnnotation: true
        }
    }, methods: {
        
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
        openAddCNVDialog(currentListOfCNVVisibleGenes) {
            this.currentListOfCNVVisibleGenes = currentListOfCNVVisibleGenes;
            this.addCNVDialogVisible = true;
        },
        closeAddCNVDialog() {
            this.addCNVDialogVisible = false;
        },
        toggleGeneVariantDetailsButtons(doShow) {
            this.geneVariantDetailsTableHovering = doShow;
        },
        proceedWithConfirmation() {
            this.confirmationDialogVisible = false;
            this.getAjaxData();
        },
        cancelConfirmation() {
            this.confirmationDialogVisible = false;
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
            this.waitingForAjaxMessage = "There were some errors while saving";
            this.waitingForAjaxActive = false; //stops spinning wheel if error
        },
        getAjaxData() {
            this.loadingVariantDetails = true;
            this.$refs.advancedFilter.loading = true;
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
                        setTimeout(() => {
                            if (this.readonly) {
                                bus.$emit("update-status", ["VIEW ONLY MODE"]);
                            }
                            else {
                                bus.$emit("update-status-off");
                            }
                        }, 4200); //show after snackbar is dismissed
                    // var start = new Date();
                    this.patientTables = response.data.patientInfo.patientTables;
                    this.caseAssignedTo = response.data.assignedToIds;
                    this.caseType = response.data.type;
                    this.reportReady = response.data.reportReady;
                    this.caseOwnerId = response.data.caseOwnerId;
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
                    // var step = new Date() - start;
                    // console.log(1, step); 
                    this.caseId = response.data.caseId;
                    this.qcUrl = response.data.qcUrl + this.caseId + "?isLimsId=true&primary=true";
                    response.data.tumorVcf = "delete this";
                    this.mutationalSignatureUrl = response.data.tumorVcf ? webAppRoot + "/mutationalSignatureViewer?caseId=" + this.caseId : null;
                    this.addCustomWarningFlags(response.data.snpIndelVariantSummary);
                    this.addOtherAnnotatorsValues(response.data.snpIndelVariantSummary);
                    // step = new Date() - start;
                    // console.log(2, step); 
                    this.$refs.geneVariantDetails.manualDataFiltered(response.data.snpIndelVariantSummary); //this can freeze the UI in datatable this.items = data.items; Not sure how to speed it up
                    //keep track of the original items in order to select all the variants regardless of filtering
                    if (!this.snpIndelUnfilteredItems && !this.$refs.advancedFilter.isAnyFilterUsed()) {
                        this.snpIndelUnfilteredItems = response.data.snpIndelVariantSummary.items;
                    }
                    //when the items are repopulated, we break the link with currentRow
                    //use the for loop to reset currentRow to the proper reference
                    if (this.currentRow && this.isSNP()) {
                        for (var i = 0; i < this.$refs.geneVariantDetails.items.length; i++) {
                            if (this.$refs.geneVariantDetails.items[i].oid == this.currentRow.oid) {
                                this.currentRow = this.$refs.geneVariantDetails.items[i];
                                break;
                            }
                        }
                    }
                    // step = new Date() - start;
                    // console.log(3, step); 
                    this.addOtherAnnotatorsValues(response.data.cnvSummary);
                    this.$refs.cnvDetails.manualDataFiltered(response.data.cnvSummary);
                    //keep track of the original items in order to select all the variants regardless of filtering
                    if (!this.cnvUnfilteredItems && !this.$refs.advancedFilter.isAnyFilterUsed()) {
                        this.cnvUnfilteredItems = response.data.cnvSummary.items;
                    }
                    if (this.currentRow && this.isCNV()) {
                        for (var i = 0; i < this.$refs.cnvDetails.items.length; i++) {
                            if (this.$refs.cnvDetails.items[i].oid == this.currentRow.oid) {
                                this.currentRow = this.$refs.cnvDetails.items[i];
                                break;
                            }
                        }
                    }
                    // step = new Date() - start;
                    // console.log(4, step); 
                    this.addOtherAnnotatorsValues(response.data.translocationSummary);
                    this.$refs.translocationDetails.manualDataFiltered(response.data.translocationSummary);
                    //keep track of the original items in order to select all the variants regardless of filtering
                    if (!this.ftlUnfilteredItems && !this.$refs.advancedFilter.isAnyFilterUsed()) {
                        this.ftlUnfilteredItems = response.data.translocationSummary.items;
                    }
                    if (this.currentRow && this.isTranslocation()) {
                        for (var i = 0; i < this.$refs.translocationDetails.items.length; i++) {
                            if (this.$refs.translocationDetails.items[i].oid == this.currentRow.oid) {
                                this.currentRow = this.$refs.translocationDetails.items[i];
                                break;
                            }
                        }
                    }
                    // step = new Date() - start;
                    // console.log(5, step); 
                    this.$refs.advancedFilter.effects = response.data.effects;
                    this.$refs.advancedFilter.failedFilters = response.data.failedFilters;
                    this.$refs.advancedFilter.checkBoxLabelsByValue = response.data.checkBoxLabelsByValue;
                    this.userId = response.data.userId;
                    this.$refs.advancedFilter.populateCheckBoxes();
                    // step = new Date() - start;
                    // console.log(6, step); 
                    this.$refs.advancedFilter.filterNeedsReload = false;
                    this.addSNPIndelHeaderAction(response.data.snpIndelVariantSummary.headers);
                    this.addCNVHeaderAction(response.data.cnvSummary.headers);
                    this.addFusionHeaderAction(response.data.translocationSummary.headers);
                    this.removeCurrentUserSelectionColumnFromHeaders(response.data.snpIndelVariantSummary.headerOrder, response.data.cnvSummary.headerOrder, response.data.translocationSummary.headerOrder)
                    // step = new Date() - start;
                    // console.log(7, step); 
                    this.reportGroups = response.data.reportGroups;
                    this.$refs.reviewDialog.requiredReportGroups = this.reportGroups.filter(r => r.required);
                    this.$refs.advancedFilter.reportGroups = this.reportGroups;
                    //only show hidden elements if it's the 1st time the page
                    //loads
                    //otherwise keep user's preference
                    if (this.firstTimeLoading) {
                        this.firstTimeLoading = false;
                        this.patientDetailsVisible = true;
                        setTimeout(() => {
                            this.caseAnnotationsVisible = true;
                        }, 200);
                        setTimeout(() => {
                            this.variantTabsVisible = true;
                        }, 400);

                        setTimeout(() => {
                            this.loadFromParams();
                        }, 500);
                    }
                    //calculate progress
                    this.splashSteps = 1;
                    if (this.$route.query.variantId ? this.$route.query.variantId : null) {
                        this.splashSteps++;
                    }
                    if (this.$route.query.showReview === true || this.$route.query.showReview === "true") {
                        this.splashSteps++;
                    }
                    if (this.$route.query.edit === true || this.$route.query.edit === "true") {
                        this.splashSteps++;
                    }
                    this.splashSteps == 1 ? this.splashProgress = 100 : this.splashProgress = 30;
                }
                else {
                    this.handleDialogs(response.data, this.getAjaxData);
                }
                this.loadingVariantDetails = false;
                this.$refs.advancedFilter.loading = false;
                this.$emit("get-case-details-done");
                 this.$refs.advancedFilter.isAnyFilterUsed() && !this.$refs.advancedFilter.filterNeedsReload
            }
            ).catch(error => {
                this.loadingVariantDetails = false;
                if (this.$refs.advancedFilter) {
                    this.$refs.advancedFilter.loading = false;
                }
                this.handleAxiosError(error);
            }
            );
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
        loadFromParams(newRouteQuery, oldRouteQuery) {
            this.urlQuery.variantId = this.$route.query.variantId ? this.$route.query.variantId : null;
            this.urlQuery.variantType = this.$route.query.variantType ? this.$route.query.variantType : null;
            this.urlQuery.showReview = this.$route.query.showReview === true || this.$route.query.showReview === "true";
            this.urlQuery.edit = this.$route.query.edit === true || this.$route.query.edit === "true";

            //calculate progress
            this.splashSteps = 1;
            if (this.urlQuery.variantId) {
                this.splashSteps++;
            }
            if (this.urlQuery.showReview) {
                this.splashSteps++;
            }
            if (this.urlQuery.edit) {
                this.splashSteps++;
            }

            if (!this.urlQuery.showReview) { //close save/review dialog
                this.closeReviewDialog();
            }
            if (!this.urlQuery.variantId) { //close variant details
                this.closeVariantDetails(true);
            }
            if (!this.urlQuery.edit) { //close edit annotation
                this.cancelAnnotations();
            }

            //first open save/review dialog
            if (this.urlQuery.showReview === true) {
                this.$nextTick(this.openReviewSelectionDialog());
                this.updateSelectedVariantTable();

            }
            if (this.urlQuery.variantType) {
                this.variantTabActive = "tab-" + this.urlQuery.variantType;
            }
            //then open variant details
            if (this.urlQuery.variantId && this.urlQuery.variantType) {
                if (this.$route.query.variantId != 'notreal') {
                    var delay = 0;
                    setTimeout(() => {
                        var variantFound = false;
                        //find item
                        if (this.urlQuery.variantType == 'snp') {
                            for (var i = 0; i < this.$refs.geneVariantDetails.items.length; i++) {
                                if (this.$refs.geneVariantDetails.items[i].oid == this.urlQuery.variantId) {
                                    if ((newRouteQuery && oldRouteQuery && (newRouteQuery.variantId != oldRouteQuery.variantId))
                                        || !newRouteQuery) {
                                            variantFound = true;
                                        this.$nextTick(this.getVariantDetails(this.$refs.geneVariantDetails.items[i]));
                                        break;
                                    }
                                }
                            }
                        }
                        else if (this.urlQuery.variantType == 'cnv') {
                            for (var i = 0; i < this.$refs.cnvDetails.items.length; i++) {
                                if (this.$refs.cnvDetails.items[i].oid == this.urlQuery.variantId) {
                                    if ((newRouteQuery && oldRouteQuery && (newRouteQuery.variantId != oldRouteQuery.variantId))
                                        || !newRouteQuery) {
                                            variantFound = true;
                                        this.$nextTick(this.getCNVDetails(this.$refs.cnvDetails.items[i]));
                                        break;
                                    }
                                }
                            }
                        }
                        else if (this.urlQuery.variantType == 'translocation') {
                            for (var i = 0; i < this.$refs.translocationDetails.items.length; i++) {
                                if (this.$refs.translocationDetails.items[i].oid == this.urlQuery.variantId) {
                                    if ((newRouteQuery && oldRouteQuery && (newRouteQuery.variantId != oldRouteQuery.variantId))
                                        || !newRouteQuery) {
                                            variantFound = true;
                                        this.$nextTick(this.getTranslocationDetails(this.$refs.translocationDetails.items[i]));
                                        break;
                                    }
                                }
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
                    }, delay);
                }
                else {
                    this.getCNVDetailsNoVariant();
                }
            }

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
            this.toggleHTMLOverlay();

        },
        updateSplashProgress() {
            this.splashProgress += Math.min(100, Math.floor(70 / (this.splashSteps - 1)));
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
        addCustomWarningFlags(snpIndelVariantSummary) {
            for (var i = 0; i < snpIndelVariantSummary.items.length; i++) {
                var item = snpIndelVariantSummary.items[i];
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
        handleRefresh() {
            //issue a warning about unsaved selection
            this.confirmationDialogVisible = true;
        },
        getVariantFilters() {
            axios.get(
                webAppRoot + "/getVariantFilters",
                {
                    params: {
                        caseId: this.$route.params.id
                    }
                })
                .then(response => {
                    if (response.data.isAllowed) {
                        this.$refs.advancedFilter.createFilters(response.data.filters);
                    }
                    else {
                        this.handleDialogs(response, this.getVariantFilters);
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                });
        },
        isAdvancedFilteringVisible() {
            return this.$refs.advancedFilter && this.$refs.advancedFilter.advancedFilteringVisible;
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
        filterData() {
            this.getAjaxData();
        },
        toggleFilters(type) {
            this.currentFilterType = type;
            this.$refs.advancedFilter.toggleFilters();
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
        getDialogMaxHeight(offset) {
            getDialogMaxHeight(offset);
        },
        getVariantDetails(item, resetSaveFlags) {
            this.loadingVariant = true;
            this.currentVariantType = "snp";

            this.currentVariantFlags = item.iconFlags.iconFlags;
            this.currentRow = item;
            var table; //could be the selected variant table or the regular one
            if (this.reviewDialogVisible) {
                table = this.$refs.reviewDialog.getSnpTable();
            }
            else {
                table = this.$refs.geneVariantDetails;
            }
            var currentIndex = table.getCurrentItemIdex(this.currentRow.oid);
            this.isFirstVariant = table.isFirstItem(currentIndex);
            this.isLastVariant = table.isLastItem(currentIndex);

            //put panels in loading state
            this.$refs.relatedVariantAnnotation.startLoading();
            this.$refs.relatedCNVAnnotation.startLoading();
            this.$refs.canonicalVariantAnnotation.startLoading();
            this.$refs.otherVariantAnnotations.startLoading();

            // this.loadingVariantDetails = true;
            axios.get(
                webAppRoot + "/getVariantDetails",
                {
                    params: {
                        variantId: item.oid,
                        caseId: this.$route.params.id

                    }
                }).then(response => {
                    this.variantDetailsEndedLoading();
                    if (response.data.isAllowed) {
                        this.variantDetailsUnSaved = false;
                        this.currentVariant = response.data.variantDetails;
                        this.variantDataTables = [];
                        var infoTable = {
                            name: "infoTable",
                            items: [
                                {
                                    label: "Flags",
                                    value: this.currentRow.iconFlags.iconFlags,
                                    type: "flag"
                                },
                                {
                                    label: "Chromosome Position",
                                    value: this.currentVariant.chrom + ":"
                                        + this.currentVariant.pos,
                                    type: "link",
                                    linkIcon: "open_in_new",
                                    url: this.createUCSCLink(),
                                    tooltip: "Open Genome Browser"
                                },
                                {
                                    label: "Previous Builds",
                                    type: "array",
                                    value: this.createOldBuildList(this.currentVariant.oldBuilds),
                                },
                                {
                                    label: "Gene",
                                    type: "link",
                                    linkIcon: "mdi-dna",
                                    url: this.createOncoKBGeniePortalGene(),
                                    value: this.currentVariant.geneName,
                                    tooltip: "Open Lookup Portal (Gene) in new tab"
                                },
                                {
                                    label: "Notation",
                                    type: "notation",
                                    fieldName: "notation",
                                    linkIcon: "mdi-dna",
                                    url: this.createOncoKBGeniePortalVariant(),
                                    value: this.currentVariant.notation,
                                    tooltip: "Open Lookup Portal (Variant) in new tab"
                                },
                                {
                                    label: "Reference Allele(s)",
                                    value: this.currentVariant.reference
                                },
                                {
                                    label: "Alternate Allele(s)",
                                    value: this.currentVariant.alt
                                },
                                {
                                    label: "Type",
                                    value: this.currentVariant.type
                                },
                                {
                                    label: "Nb. Cases Seen",
                                    value: this.currentVariant.numCasesSeen ? this.currentVariant.numCasesSeen + "" : ""
                                },
                                {
                                    label: "Somatic Status",
                                    value: this.currentVariant.somaticStatus
                                }]
                        };
                        this.variantDataTables.push(infoTable);

                        var oncoKBIds = [{
                            type: "oncoKB",
                            subtype: "gene",
                            value: this.currentVariant.oncokbGeneName,
                            label: "<span class='no-text-transform'>&nbsp;OncoKB Gene " + this.currentVariant.oncokbGeneName + "&nbsp;</span>",
                        },
                        {
                            type: "oncoKB",
                            subtype: "variant",
                            value: this.currentVariant.oncokbVariantName,
                            label: "<span class='no-text-transform'>&nbsp;OncoKB Variant " + this.currentVariant.oncokbVariantName + "&nbsp;</span>",
                        }
                        ];

                        var variousIds = [];
                        for (var i = 0; i < this.currentVariant.ids.length; i++) {
                            variousIds.push({
                                type: "various",
                                subtype: null,
                                value: this.currentVariant.ids[i],
                                label: this.formatIdLinkLabel(this.currentVariant.ids[i], this.currentVariant.ids, this.currentVariant.cosmicPatients),
                            });
                        }

                        this.linkTable = [{
                            name: "linkTable",
                            items: [
                                {
                                    label: "IDs",
                                    ids: variousIds.concat(oncoKBIds),
                                    cosmicPatients: this.currentVariant.cosmicPatients,
                                    links: true
                                },
                            ]
                        }];

                        var depthTable =
                        {
                            name: "depthTable",
                            items: [
                                {
                                    label: "Tumor Total Depth",
                                    value: this.currentVariant.tumorTotalDepth ? this.currentVariant.tumorTotalDepth + "" : ""
                                },
                                {
                                    label:
                                        "Tumor Alt Percent",
                                    value: this.currentVariant.tumorAltFrequencyFormatted ? this.currentVariant.tumorAltFrequencyFormatted + "%" : "" //already formatted as pct
                                },
                                {
                                    label: "Normal Total Depth",
                                    value: this.currentVariant.normalTotalDepth ? this.currentVariant.normalTotalDepth + "" : ""
                                },
                                {
                                    label: "Normal Alt Percent",
                                    value: this.currentVariant.normalAltFrequencyFormatted ? this.currentVariant.normalAltFrequencyFormatted + "%" : ""  //already formatted as pct
                                }, {
                                    label: "RNA Total Depth",
                                    value: this.currentVariant.rnaTotalDepth ? this.currentVariant.rnaTotalDepth + "" : ""
                                },
                                {
                                    label: "RNA Alt Percent",
                                    value: this.currentVariant.rnaAltFrequencyFormatted ? this.currentVariant.rnaAltFrequencyFormatted + "%" : ""  //already formatted as pct
                                },
                                {
                                    label: "Exac Allele Frequency",
                                    value: this.formatPercent(this.currentVariant.exacAlleleFrequency)
                                },
                               
                            ]
                        };
                        if (this.currentVariant.gnomadPopmaxAlleleFrequency !=  0) {
                            depthTable.items.push(
                                {
                                    label: "gnomAD Pop. Max. Allele Frequency",
                                    value: this.formatPercent(this.currentVariant.gnomadPopmaxAlleleFrequency),
                                    type: this.currentVariant.gnomadHg19Variant ? "link" : null,
                                    linkIcon: "open_in_new",
                                    url: this.createGnomadLink(),
                                    tooltip: "Open in gnomAD"
                                }
                            );
                        }
                        else {
                            depthTable.items.push(
                            {
                                label: "gnomAD Pop. Max. Allele Frequency",
                                value: this.formatPercent(this.currentVariant.gnomadPopmaxAlleleFrequency)
                            }
                            );
                        }
                        depthTable.items.push({
                           label: "gnomAD HOM",
                           value: this.currentVariant.gnomadHomozygotes ? this.currentVariant.gnomadHomozygotes[0] + "" : "" 
                        });
                        this.variantDataTables.push(depthTable);
                        var dataTable = {
                            name: "dataTable",
                            items: [
                                {
                                    label: "Reported Tier",
                                    type: "select",
                                    fieldName: "tier",
                                    tooltip: "Select a Tier",
                                    items: this.variantTiers
                                },
                                {
                                    label: "Callers",
                                    value: this.formatSNPCallers(this.currentVariant.callSet),
                                    type: "callSet",
                                    columns: this.currentVariant.callSet.length
                                },
                            ]
                        };
                        this.variantDataTables.push(dataTable);
                        if (response.data.relatedSummary) {
                            this.$refs.relatedVariantAnnotation.manualDataFiltered(response.data.relatedSummary);
                            if (response.data.relatedSummary.items.length > 0) {
                                this.currentVariantHasRelatedVariants = true;
                                this.annotationVariantRelatedVisible = true;
                            }
                        }
                        else {
                            this.currentVariantHasRelatedVariants = false;
                        }
                        if (response.data.cnvRelatedSummary) {
                            this.$refs.relatedCNVAnnotation.manualDataFiltered(response.data.cnvRelatedSummary);
                            if (response.data.cnvRelatedSummary.items.length > 0) {
                                this.currentVariantHasRelatedCNV = true;
                                this.annotationCNVRelatedVisible = true;
                            }
                        }
                        else {
                            this.currentVariantHasRelatedCNV = false;
                        }
                        this.$refs.canonicalVariantAnnotation.manualDataFiltered(response.data.canonicalSummary);
                        this.$refs.otherVariantAnnotations.manualDataFiltered(response.data.otherSummary);
                        this.userAnnotations = this.currentVariant.referenceVariant.utswAnnotations.filter(a => a.userId == this.userId);
                        this.$refs.annotationDialog.userAnnotations = this.userAnnotations;
                        this.utswAnnotations = this.currentVariant.referenceVariant.utswAnnotations;
                        this.reloadPreviousSelectedState();
                        this.mdaAnnotations = this.currentVariant.mdaAnnotation ? this.currentVariant.mdaAnnotation : "";
                        this.formatAnnotations();
                        this.loadingVariantDetails = false;
                        this.loadingVariant = false;
                        if (resetSaveFlags) {
                            this.annotationSelectionUnSaved = false;
                        }
                        this.variantDetailsVisible = true;
                        this.updateVariantDetails();

                        //finally, open edit annotation
                        this.handleEditAnnotationOpening();

                        this.updateSplashProgress();

                        this.tryGoodies();

                       

                    } else {
                        this.loadingVariantDetails = false;
                        this.loadingVariant = false;
                        this.handleDialogs(response.data, this.getVariantDetails.bind(null,
                            item));
                    }
                }).catch(error => {
                    this.loadingVariantDetails = false;
                    this.loadingVariant = false;
                    this.variantDetailsEndedLoading();
                    this.handleAxiosError(error);
                });
        },
        getCNVDetails(item, resetSaveFlags) {
            this.currentVariantType = "cnv";
            this.currentVariantFlags = item.iconFlags.iconFlags;
            this.currentRow = item;
            // this.loadingVariantDetails = true;
            this.loadingVariant = true;

            var table; //could be the selected variant table or the regular one
            if (this.reviewDialogVisible) {
                table = this.$refs.reviewDialog.getCnvTable();
            }
            else {
                table = this.$refs.cnvDetails;
            }
            var currentIndex = table.getCurrentItemIdex(this.currentRow.oid);
            this.isFirstVariant = table.isFirstItem(currentIndex);
            this.isLastVariant = table.isLastItem(currentIndex);

            //put panels in loading state



            axios.get(
                webAppRoot + "/getCNVDetails",
                {
                    params: {
                        variantId: item.oid,
                        caseId: this.$route.params.id
                    }
                }).then(response => {
                    this.variantDetailsEndedLoading();
                    if (response.data.isAllowed) {
                        this.variantDetailsUnSaved = false;
                        this.currentVariant = response.data;
                        this.variantDataTables = [];
                        var geneChips = [];
                        for (var i = 0; i < this.currentVariant.genes.length; i++) {
                            geneChips.push({
                                name: this.currentVariant.genes[i],
                                selected: false
                            })
                        }
                        this.currentVariant.geneChips = geneChips.sort((a, b) => {
                        	const nameA = a.name.toUpperCase();
                        	const nameB = b.name.toUpperCase();
                        	if (nameA > nameB) {
                        		return 1;
                        	}
                        	else if (nameA < nameB) {
                        		return -1;
                        	}
                        	return 0;
                        });
                        var infoTable = {
                            name: "infoTable",
                            items: [
                                {
                                    label: "Chromosome", value: formatChrom(this.currentVariant.chrom)
                                },
                                {
                                    label: "Genes", type: "chip", value: this.currentVariant.geneChips
                                },
                            ]
                        };
                        var infoTable2 = {
                            name: "infoTable2",
                            items: [
                                {
                                    label: "Start", value: this.currentVariant.startFormatted
                                },
                                {
                                    label: "End", value: this.currentVariant.endFormatted
                                },
                                {
                                    label: "Reported Tier",
                                    type: "select",
                                    fieldName: "tier",
                                    tooltip: "Select a Tier",
                                    items: this.variantTiers
                                },
                                {
                                    label: "Aberration Type",
                                    type: "select",
                                    fieldName: "aberrationType",
                                    tooltip: "Select Aberration Type",
                                    items: this.aberrationTypes,
                                    help: true,
                                    helpMessage: this.buildAberrationTypeHelp(),
                                    value: this.currentVariant.aberrationType
                                },
                                {
                                    label: "Copy Number", value: this.currentVariant.copyNumber ? this.currentVariant.copyNumber + "" : ""
                                },
                                {
                                    label: "Score", value: this.currentVariant.score ? this.currentVariant.score + "" : ""
                                },
                                {
                                    label: "Cytoband", value: this.currentVariant.cytoband
                                },
                                {
                                    label: "Open Lookup Portal (CNV)",
                                    type: "menu-link",
                                    linkIcon: "mdi-dna",
                                    items: this.currentVariant.geneChips,
                                    value: "",
                                    tooltip: "Open Lookup Portal (CNV) in new tab"
                                },
                               
                            ]
                        };
                        this.variantDataTables.push(infoTable);
                        this.variantDataTables.push(infoTable2);

                        this.linkTable = [];

                        this.userAnnotations = this.currentVariant.referenceCnv.utswAnnotations.filter(a => a.userId == this.userId);
                        this.$refs.cnvAnnotationDialog.userAnnotations = this.userAnnotations;
                        this.utswAnnotations = this.currentVariant.referenceCnv.utswAnnotations;
                        this.mdaAnnotations = this.currentVariant.mdaAnnotation ? this.currentVariant.mdaAnnotation : "";
                        this.reloadPreviousSelectedState();
                        this.formatCNVAnnotations();
                        this.loadingVariantDetails = false;
                        this.loadingVariant = false;
                        if (resetSaveFlags) {
                            this.annotationSelectionUnSaved = false;
                        }
                        this.variantDetailsVisible = true;
                        this.updateVariantDetails();
                        //finally, open edit annotation
                        this.handleEditAnnotationOpening();
                        // this.$refs.variantDetailsPanel.updateCNVPlot();
                        this.updateSplashProgress();
                        this.tryGoodies();
                    } else {
                        this.loadingVariantDetails = false;
                        this.loadingVariant = false;
                        this.handleDialogs(response.data, this.getCNVDetails.bind(null,
                            item));
                    }
                }).catch(error => {
                    this.loadingVariantDetails = false;
                    this.loadingVariant = false;
                    this.variantDetailsEndedLoading();
                    this.handleAxiosError(error);
                });
        },
        getCNVDetailsNoVariant(resetSaveFlags) {
            this.currentVariantType = "cnv";
            this.currentVariantFlags = [];
            this.currentRow = { isSelected: false};
            // this.loadingVariantDetails = true;
            this.loadingVariant = true;

            var table; //could be the selected variant table or the regular one
            if (this.reviewDialogVisible) {
                table = this.$refs.reviewDialog.getCnvTable();
            }
            else {
                table = this.$refs.cnvDetails;
            }
            var currentIndex = 0;
            this.isFirstVariant = true;
            this.isLastVariant = true;

            this.variantDetailsUnSaved = false;
            this.currentVariant = {};
            this.variantDataTables = [];
            this.linkTable = [];

            this.userAnnotations = [];
            this.$refs.cnvAnnotationDialog.userAnnotations = this.userAnnotations;
            this.utswAnnotations = [];
            this.utswAnnotationsFormatted = [];
            this.mdaAnnotations = "";
            this.mdaAnnotationsFormatted = [];
            this.reloadPreviousSelectedState();
            // this.formatCNVAnnotations();
            this.loadingVariantDetails = false;
            this.loadingVariant = false;
            if (resetSaveFlags) {
                this.annotationSelectionUnSaved = false;
            }
            this.variantDetailsVisible = true;
            // this.updateVariantDetails();
            this.urlQuery.variantId = "notreal";
            this.urlQuery.variantType = this.currentVariantType;
            this.updateSplashProgress(); //update progress as if getCNVDetails had been called
            this.updateRoute();
            //finally, open edit annotation
            // this.handleEditAnnotationOpening();
            // // this.$refs.variantDetailsPanel.updateCNVPlot();
            // this.updateSplashProgress();
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
        createOldBuildList(buildMap) {
            var builds = [];
            for (var key in this.currentVariant.oldBuilds) {
                builds.push(this.currentVariant.oldBuilds[key].chrom + ":" + this.currentVariant.oldBuilds[key].pos + " (" + key + ")");
            }
            return builds;
        },
        variantDetailsEndedLoading() {
            if (this.currentItem) {
                this.currentItem.loading = false;
            }
        },
        getTranslocationDetails(item, resetSaveFlags) {
            this.currentVariantType = "translocation";
            this.currentVariantFlags = item.iconFlags.iconFlags;
            this.currentRow = item;
            this.loadingVariantDetails = true;
            this.loadingVariant = true;

            if (this.reviewDialogVisible) {
                table = this.$refs.reviewDialog.getFtlTable();
            }
            else {
                table = this.$refs.translocationDetails;
            }
            var currentIndex = table.getCurrentItemIdex(this.currentRow.oid);
            this.isFirstVariant = table.isFirstItem(currentIndex);
            this.isLastVariant = table.isLastItem(currentIndex);


            axios.get(
                webAppRoot + "/getTranslocationDetails",
                {
                    params: {
                        variantId: item.oid,
                        caseId: this.$route.params.id
                    }
                }).then(response => {
                    this.variantDetailsEndedLoading();
                    if (response.data.isAllowed) {
                        this.variantDetailsUnSaved = false;
                        this.currentVariant = response.data;
                        this.variantDataTables = [];
                        var infoTable = {
                            name: "infoTable",
                            items: [{
                                label: "Fusion Name", value: this.currentVariant.fusionName
                            },
                            {
                                label: "Left Gene", value: this.currentVariant.leftGene
                            },
                            {
                                label: "Right Gene", value: this.currentVariant.rightGene
                            },
                            {
                                label: "Left Exons", value: this.currentVariant.leftExons
                            },
                            {
                                label: "Right Exons", value: this.currentVariant.rightExons
                            },
                           
                            ]
                        };
                        this.variantDataTables.push(infoTable);

                        var infoTable2 = {
                            name: "infoTable2",
                            items: [
                                {
                                    label: "Left Breakpoint", value: this.currentVariant.leftBreakpoint
                                },
                                {
                                    label: "Right Breakpoint", value: this.currentVariant.rightBreakpoint
                                },
                            {
                                label: "Left Strand", value: this.currentVariant.leftStrand
                            },
                            {
                                label: "Right Strand", value: this.currentVariant.rightStrand
                            },
                           ]
                        };
                        this.variantDataTables.push(infoTable2);

                        var infoTable3 = {
                            name: "infoTable3",
                            items: [
                                {
                                    label: "Reported Tier",
                                    type: "select",
                                    fieldName: "tier",
                                    tooltip: "Select a Tier",
                                    items: this.variantTiers
                                },
                            {
                                label: "RNA Reads", value: this.currentVariant.rnaReads ? this.currentVariant.rnaReads + "" : ""
                            },
                            {
                                label: "DNA Reads", value: this.currentVariant.dnaReads ? this.currentVariant.dnaReads + "" : ""
                            },
                            {
                                label: "Open Lookup Portal (Fusion)",
                                type: "link",
                                linkIcon: "mdi-dna",
                                url: this.createOncoKBGeniePortalFusion(),
                                value: "",
                                tooltip: "Open Lookup Portal (Fusion) in new tab"
                            }]
                        };
                        this.variantDataTables.push(infoTable3);

                        this.linkTable = [];

                        this.userAnnotations = this.currentVariant.referenceTranslocation.utswAnnotations.filter(a => a.userId == this.userId);
                        this.$refs.translocationAnnotationDialog.userAnnotations = this.userAnnotations;
                        this.utswAnnotations = this.currentVariant.referenceTranslocation.utswAnnotations;
                        this.mdaAnnotations = "";
                        this.reloadPreviousSelectedState();
                        this.formatTranslocationAnnotations();
                        this.loadingVariantDetails = false;
                        if (resetSaveFlags) {
                            this.annotationSelectionUnSaved = false;
                        }
                        this.variantDetailsVisible = true;
                        this.updateVariantDetails();
                        this.loadingVariant = false;
                        //finally, open edit annotation
                        this.handleEditAnnotationOpening();

                        this.updateSplashProgress();
                        this.tryGoodies();
                    } else {
                        this.loadingVariantDetails = false;
                        this.loadingVariant = false;
                        this.handleDialogs(response.data, this.getTranslocationDetails.bind(null,
                            item));
                    }
                }).catch(error => {
                    this.loadingVariantDetails = false;
                    this.loadingVariant = false;
                    this.variantDetailsEndedLoading();
                    this.handleAxiosError(error);
                });
        },
        reloadPreviousSelectedState() {
            for (var i = 0; i < this.annotationIdsForReporting.length; i++) {
                for (var j = 0; j < this.utswAnnotations.length; j++) {
                    if (this.annotationIdsForReporting[i].$oid == this.utswAnnotations[j]._id.$oid) {
                        this.utswAnnotations[j].isSelected = true; //only set if true, do not unset if false
                    }
                }
            }
        },
        createUCSCLink() {
            return "https://genome.ucsc.edu/cgi-bin/hgTracks?db=hg38&position="
                + this.currentVariant.chrom + ":" + (this.currentVariant.pos - 50)
                + "-" + (this.currentVariant.pos + 50);
        },
        createGnomadLink() {
            if (this.currentVariant.gnomadHg19Variant) {
                return "http://gnomad.broadinstitute.org/variant/" + this.currentVariant.gnomadHg19Variant;
            }
            return "";
        },
        handleEditAnnotationOpening() {
            if (this.urlQuery.edit === true) {
                setTimeout(() => {
                    this.startUserAnnotations();
                }, 1000);
            }
        },
        formatSNPCallers(callers) {
            var labels = ['Name:', 'Alt:', 'Tumor Total Depth:', 'Tumor Alt Percent:', 'Normal Total Depth:', 'Normal Alt Percent:'];
            var callerNames = callers.map(c => c.callerName);
            var alts = callers.map(c => c.alt);
            var tumorTotalDepths = callers.map(c => c.tumorTotalDepth);
            var tumorAlleleFrequencys = callers.map(c => c.tumorAlleleFrequencyFormatted ? c.tumorAlleleFrequencyFormatted + "%": null);
            var normalTotalDepths = callers.map(c => c.normalTotalDepth);
            var normalAlleleFrequencys = callers.map(c => c.normalAlleleFrequencyFormatted ? c.normalAlleleFrequencyFormatted + "%": null);
            var formattedRows = [];
            for (var i = 0; i < labels.length; i++) {
                formattedRows.push({
                    label: labels[i],
                })
            }
            for (var j = 0; j < callerNames.length; j++) {
                var i = 0;
                formattedRows[i++]["caller" + j] = callerNames[j];
                formattedRows[i++]["caller" + j] = alts[j];
                formattedRows[i++]["caller" + j] = tumorTotalDepths[j];
                formattedRows[i++]["caller" + j] = tumorAlleleFrequencys[j];
                formattedRows[i++]["caller" + j] = normalTotalDepths[j];
                formattedRows[i++]["caller" + j] = normalAlleleFrequencys[j];
            }

            return formattedRows;
        },
        updateVariantVcfAnnotationTable() {
            var items = this.currentVariant.vcfAnnotations;
            var headers = this.vcfAnnotationHeaders;
            var headerOrder = this.vcfAnnotationHeadersOrder;
            this.$refs.reviewDialog.getSnpTable().manualDataFiltered(
                {
                    items: items,
                    headers: headers,
                    uniqueIdField: "oid",
                    headerOrder: headerOrder
                }
            );
        },
        getWidthClassForVariantDetails() {
            if (this.isSNP()) {
                return 'xs4';
            }
            if (this.isCNV()) {
                return 'xs12';
            }
            if (this.isTranslocation()) {
                return 'xs6';
            }
        },
        isVariantOpening() {
            return (this.currentItem && this.currentItem.loading);
        },
        openVariant(item) {
            if (this.isVariantOpening()) {
                return;
            }
            this.urlQuery.variantType = "snp";
            this.urlQuery.variantId = item.oid;
            item.loading = true;
            this.currentItem = item;
            this.updateRoute();
        },
        openCNV(item) {
            if (this.isVariantOpening()) {
                return;
            }
            this.urlQuery.variantType = "cnv";
            this.urlQuery.variantId = item.oid;
            this.currentItem = item;
            this.updateRoute();
        },
        openTranslocation(item) {
            if (this.isVariantOpening()) {
                return;
            }
            this.urlQuery.variantType = "translocation";
            this.urlQuery.variantId = item.oid;
            this.currentItem = item;
            this.updateRoute();
        },
        openLink(link) {
            window.open(link, "_blank", 'noopener');
        },
        formatIdLinkLabel(id, ids, cosmicPatients) {
            if (id == null) {
                return;
            }
            var cosmicIds = ids.filter(id => id.indexOf('COSM') == 0);
            var index = cosmicIds.indexOf(id);
            if (id.indexOf('rs') == 0) {
                return "&nbsp;" + id + "&nbsp;";
            }
            else if (id.indexOf('COSM') == 0) {
                return "&nbsp;" + id + " (" + cosmicPatients[index] + ")&nbsp;";
            }
            else {
                return "&nbsp;" + id + "&nbsp;";
            }
        },
        formatPercent(value) {
            if (value !== null && !isNaN(value)) {
            	if (!isNaN(value)) {
            		return (Math.round(parseFloat(value) * 100000) / 1000) + "%";
            	}
            	else if (value.indexOf("<") > -1) {
            		return value + "%";
            	}
            } 
            return "";
        },
        formatLocalAnnotations(annotations, showUser) {
            var formatted = [];
            for (var i = 0; i < annotations.length; i++) {
                var annotation = {
                    _id: "",
                    fullName: "",
                    text: "",
                    scopes: [],
                    scopeLevels: [],
                    scopeTooltip: "",
                    tumorSpecific: "",
                    category: "",
                    createdDate: "",
                    createdSince: "",
                    modifiedDate: "",
                    modifiedSince: "",
                    pmids: [],
                    // nctIds: [],
                    tier: "",
                    classification: "",
                    visible: true,
                    isSelected: false,
                    trial: null,
                    drugs: "",
                    warningLevel: 0,
                    drugResistant: false
                };
                annotation._id = annotations[i]._id;
                if (showUser) {
                    annotation.fullName = annotations[i].fullName;
                }
                annotation.text = annotations[i].text.replace(/\n/g, "<br/>");
                annotation.scopes = [annotations[i].isCaseSpecific, annotations[i].isGeneSpecific, annotations[i].isVariantSpecific, annotations[i].isTumorSpecific];
                annotation.scopeLevels = ["Case " + (annotations[i].isCaseSpecific ? annotations[i].caseId : ''),
                "Gene " + (annotations[i].isGeneSpecific ? annotations[i].geneId : ''),
                "Variant " + (annotations[i].isVariantSpecific ? this.currentVariant.notation : ''),
                    "Diagnosis "+ (annotations[i].isTumorSpecific ? annotations[i].oncotreeDiagnosis : '')];
                annotation.category = annotations[i].category;
                annotation.createdDate = annotations[i].createdDate;
                annotation.createdSince = annotations[i].createdSince;
                annotation.modifiedDate = annotations[i].modifiedDate;
                annotation.modifiedSince = annotations[i].modifiedSince;
                annotation.pmids = annotations[i].pmids;
                // annotation.nctIds = annotations[i].nctIds;
                annotation.scopeTooltip = this.$refs.annotationDialog.createLevelInformation(annotations[i]);
                annotation.tier = annotations[i].tier;
                annotation.classification = annotations[i].classification;
                annotation.isSelected = annotations[i].isSelected;
                annotation.trial = annotations[i].trial;
                annotation.drugs = annotations[i].drugs;
                annotation.warningLevel = annotations[i].warningLevel;
                annotation.drugResistant = annotations[i].drugResistant;
                formatted.push(annotation);
            }
            return formatted;
        },
        formatLocalCNVAnnotations(annotations, showUser) {
            var formatted = [];
            for (var i = 0; i < annotations.length; i++) {
                var annotation = {
                    _id: "",
                    fullName: "",
                    text: "",
                    scopes: [],
                    scopeLevels: [],
                    scopeTooltip: "",
                    tumorSpecific: "",
                    category: "",
                    createdDate: "",
                    createdSince: "",
                    modifiedDate: "",
                    modifiedSince: "",
                    cnvGenes: "",
                    pmids: [],
                    tier: "",
                    classification: "",
                    visible: true,
                    isSelected: false,
                    breadth: "",
                    trial: null,
                    warningLevel: 0,
                    drugResistant: false
                };
                annotation._id = annotations[i]._id;
                if (showUser) {
                    annotation.fullName = annotations[i].fullName;
                }
                annotation.text = annotations[i].text.replace(/\n/g, "<br/>");
                annotation.scopes = [annotations[i].isCaseSpecific, annotations[i].isVariantSpecific, annotations[i].isTumorSpecific];
                annotation.scopeLevels = ["Case " + (annotations[i].isCaseSpecific ? annotations[i].caseId : ''),
                "Variant " + (annotations[i].isVariantSpecific ? this.currentVariant.chrom : ''),
                    "Diagnosis"];
                var categoryLabel = annotations[i].category ? annotations[i].category : "";
                var categoryLabel = categoryLabel + (annotations[i].breadth ? " " + annotations[i].breadth : "");
                annotation.category = categoryLabel;
                annotation.breadth = annotations[i].breadth;
                annotation.cnvGenes = annotations[i].cnvGenes ? annotations[i].cnvGenes.join(" ") : "";
                annotation.createdDate = annotations[i].createdDate;
                annotation.createdSince = annotations[i].createdSince;
                annotation.modifiedDate = annotations[i].modifiedDate;
                annotation.modifiedSince = annotations[i].modifiedSince;
                annotation.pmids = annotations[i].pmids;
                annotation.scopeTooltip = this.$refs.cnvAnnotationDialog.createLevelInformation(annotations[i]);
                annotation.tier = annotations[i].tier;
                annotation.classification = annotations[i].classification;
                annotation.isSelected = annotations[i].isSelected;
                annotation.trial = annotations[i].trial;
                annotation.warningLevel = annotations[i].warningLevel;
                annotation.drugResistant = annotations[i].drugResistant;
                formatted.push(annotation);
            }
            return formatted;
        },
        formatLocalTranslocationAnnotations(annotations, showUser) {
            var formatted = [];
            for (var i = 0; i < annotations.length; i++) {
                var annotation = {
                    _id: "",
                    fullName: "",
                    text: "",
                    scopes: [],
                    scopeLevels: [],
                    scopeTooltip: "",
                    tumorSpecific: "",
                    category: "",
                    createdDate: "",
                    createdSince: "",
                    modifiedDate: "",
                    modifiedSince: "",
                    tier: "",
                    pmids: [],
                    classification: "",
                    visible: true,
                    isSelected: false,
                    leftGene: "",
                    rightGene: "",
                    isLeftSpecific: false,
                    isRightSpecific: false,
                    trial: null,
                    warningLevel: 0,
                    drugResistant: false

                };
                annotation._id = annotations[i]._id;
                if (showUser) {
                    annotation.fullName = annotations[i].fullName;
                }
                annotation.text = annotations[i].text.replace(/\n/g, "<br/>");
                annotation.scopes = [annotations[i].isCaseSpecific, annotations[i].isVariantSpecific, annotations[i].isTumorSpecific, annotations[i].isLeftSpecific, annotations[i].isRightSpecific];
                annotation.scopeLevels = ["Case " + (annotations[i].isCaseSpecific ? annotations[i].caseId : ''),
                "Variant " + (annotations[i].isVariantSpecific ? this.currentVariant.fusionName : ''),
                    "Diagnosis", this.currentVariant.leftGene, this.currentVariant.rightGene];
                annotation.category = annotations[i].category;
                annotation.createdDate = annotations[i].createdDate;
                annotation.createdSince = annotations[i].createdSince;
                annotation.modifiedDate = annotations[i].modifiedDate;
                annotation.modifiedSince = annotations[i].modifiedSince;
                annotation.tier = annotations[i].tier;
                annotation.pmids = annotations[i].pmids;
                annotation.classification = annotations[i].classification;
                annotation.isSelected = annotations[i].isSelected;
                annotation.leftGene = annotations[i].leftGene;
                annotation.rightGene = annotations[i].rightGene;
                annotation.scopeTooltip = this.$refs.translocationAnnotationDialog.createLevelInformation(annotations[i]);
                annotation.trial = annotations[i].trial;
                annotation.warningLevel = annotations[i].warningLevel;
                annotation.drugResistant = annotations[i].drugResistant;
                formatted.push(annotation);
            }
            return formatted;
        },
        startUserAnnotations() {
            if (!this.canProceed('canAnnotate') || this.readonly) {
                return;
            }
            if (this.isSNP()) {
                this.$refs.annotationDialog.startUserAnnotations();
            }
            else if (this.isCNV()) {
                this.$refs.cnvAnnotationDialog.cnvGeneItems = this.currentVariant.genes;
                this.$refs.cnvAnnotationDialog.startUserAnnotations();
            }
            else if (this.isTranslocation()) {
                this.$refs.translocationAnnotationDialog.startUserAnnotations();
            }
            this.updateSplashProgress();
        },
        formatAnnotations() {
            this.highlightLatestAnnotation = true;
            this.utswAnnotationsFormatted = this.formatLocalAnnotations(this.utswAnnotations, true);
            if (this.mdaAnnotations) {
                this.mdaAnnotationsFormatted = this.formatMDAAnnotations(this.mdaAnnotations);
            }
            // this.userAnnotationsFormatted = this.formatLocalAnnotations(this.userAnnotations, false);
            this.matchAnnotationFilter();
            this.stopHighlightAnimation();
        },
        formatMDAAnnotations(annotations) {
            var formatted = [];
            for (var i = 0; i < annotations.annotationCategories.length; i++) {
                if (!annotations.annotationCategories[i]) {
                    continue;
                }
                var annotation = {
                    _id: "",
                    fullName: "Dianren Xia",
                    text: "",
                    scopes: [],
                    scopeLevels: [],
                    scopeTooltip: "",
                    tumorSpecific: false,
                    category: "",
                    createdDate: "",
                    createdSince: "",
                    modifiedDate: "",
                    modifiedSince: "",
                    pmids: [],
                    // nctIds: [],
                    tier: "",
                    classification: "",
                    visible: true,
                    isSelected: false,
                    variantSpecific: false,
                    geneSpecific: false,
                    tempId: i //to retrieve the unformatted annotation later
                };
                // annotation._id = annotations.annotationCategories[i]._id;
                // if (showUser) {
                //     annotation.fullName = annotations.annotationCategories[i].fullName;
                // }
                var geneSpecific = true;
                var variantSpecific =  annotations.annotationCategories[i].title == "Functional Annotation";
                var tumorSpecific = annotations.annotationCategories[i].title == "Tumor type-specific annotation";
                annotation.geneSpecific = geneSpecific;
                annotation.variantSpecific = variantSpecific;
                annotation.tumorSpecific = tumorSpecific;
                annotation.text = annotations.annotationCategories[i].text.replace(/\n/g, "<br/>").replace(/(PMID:)([0-9]*)/g, `<a href='https://www.ncbi.nlm.nih.gov/pubmed/?term=$2' target="_blank">PMID:$2</a>`);
                annotation.scopes = [geneSpecific ,variantSpecific, tumorSpecific];
                annotation.scopeLevels = [
                "Gene " + (geneSpecific ? this.mdaAnnotations.gene : ''),
                "Variant " + (variantSpecific ? this.currentVariant.notation : ''), "Diagnosis"];
                if (annotations.annotationCategories[i].title == "Biomarker Summary") {
                    annotation.category = "Gene Function";
                }
                else if (annotations.annotationCategories[i].title == "Functional Annotation") {
                    annotation.category = "Variant Function";
                }
                else if (annotations.annotationCategories[i].title == "Potential Therapeutic Implications") {
                    annotation.category = "Therapy";
                }
                else if (annotations.annotationCategories[i].title == "Diagnosis-specific annotation") {
                    annotation.category = "Prognosis"; //TODO not sure about that one
                }
                //TOOD keep going

                annotation.createdDate = this.mdaAnnotations.reportDate;
                annotation.createdSince = this.mdaAnnotations.createdSince;
                // annotation.modifiedDate = annotations[i].modifiedDate;
                // annotation.modifiedSince = annotations[i].modifiedSince;
                // annotation.pmids = annotations[i].pmids;
                // annotation.nctIds = annotations[i].nctIds;
                annotation.scopeTooltip = this.createMDALevelInformation(geneSpecific, variantSpecific, tumorSpecific);
                // annotation.tier = annotations[i].tier;
                // annotation.classification = annotations[i].classification;
                annotation.isSelected = annotations.annotationCategories[i].isSelected;
                formatted.push(annotation);
            }
            return formatted;
        },
        createMDALevelInformation(geneSpecific, variantSpecific, tumorSpecific) {
            var text = "This annotation's scope is limited to ";
            var commaNeeded = false;
            if (geneSpecific) {
                if (commaNeeded) {
                    text = text + ", ";
                }
                text = text + "this gene";
                commaNeeded = true;
            }
            if (variantSpecific) {
                if (commaNeeded) {
                    text = text + ", ";
                }
                text = text + "this variant";
                commaNeeded = true;
            }
            if (tumorSpecific) {
                if (commaNeeded) {
                    text = text + ", ";
                }
                text = text + "this tumor";
                commaNeeded = true;
            }
            text = text + ".";
            return text;
        },
        formatCNVAnnotations() {
            this.highlightLatestAnnotation = true;
            this.utswAnnotationsFormatted = this.formatLocalCNVAnnotations(this.utswAnnotations, true);
            if (this.mdaAnnotations) {
                this.mdaAnnotationsFormatted = this.formatMDAAnnotations(this.mdaAnnotations);
            }
            this.matchAnnotationFilter();
            this.stopHighlightAnimation();
        },
        formatTranslocationAnnotations() {
            this.highlightLatestAnnotation = true;
            this.utswAnnotationsFormatted = this.formatLocalTranslocationAnnotations(this.utswAnnotations, true);
            this.matchAnnotationFilter();
            this.stopHighlightAnimation();
        },
        commitAnnotations(userAnnotations) {
            this.userAnnotations = userAnnotations;
            this.handleAnnotationSelectionChanged();
            axios({
                method: 'post',
                url: webAppRoot + "/commitAnnotations",
                params: {
                    caseId: this.$route.params.id,
                    geneId: this.currentVariant.geneName ? this.currentVariant.geneName : "",
                    variantId: this.currentVariant._id.$oid
                },
                data: {
                    annotations: this.userAnnotations,
                    annotationIdsForReporting: this.annotationIdsForReporting
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        if (response.data.payload.annotationModified) {
                            if (response.data.userPrefs && response.data.userPrefs.showGoodies) {
                                this.waitingForGoodies = true;
                            }
                            this.snackBarMessage = "Annotation(s) Saved";
                            this.snackBarLink = "";
                        }
                        else {
                            this.snackBarMessage = "No Change Detected";
                            this.snackBarLink = "";
                        }
                        if (this.isSNP()) {
                            this.getVariantDetails(this.currentRow);
                        }
                        else if (this.isCNV()) {
                            this.getCNVDetails(this.currentRow);
                        }
                        else if (this.isTranslocation()) {
                            this.getTranslocationDetails(this.currentRow);
                        }
                        this.$nextTick(() => {
                            this.cancelAnnotations();
                            this.canCopyAnnotation = true;
                            this.snackBarTimeout = 4000;
                            setTimeout(() => {
                                this.snackBarVisible = true;
                            }, 2000);
                            
                        });

                        //keep track of the selected variants and refresh
                        this.getSelectedVariantIds(true)
                        .then(response => {
                            this.currentSelectedVariantIds = response;
                            if (this.currentSelectedVariantIds) {
                                this.tempSelectedSNPVariants = this.currentSelectedVariantIds.selectedSNPVariantIds;
                                this.tempSelectedCNVs = this.currentSelectedVariantIds.selectedCNVIds;
                                this.tempSelectedTranslocations = this.currentSelectedVariantIds.selectedTranslocationIds;
    
                                //once refreshed, reselect rows that were selected but not saved yet
                                this.$once('get-case-details-done', (annotations) => {
                                    for (var i = 0; i < this.$refs.geneVariantDetails.items.length; i++) {
                                        var row = this.$refs.geneVariantDetails.items[i];
                                        if (this.tempSelectedSNPVariants.includes(row.oid)) {
                                            row.isSelected = true;
                                        }
                                    }
                                    for (var i = 0; i < this.$refs.cnvDetails.items.length; i++) {
                                        var row = this.$refs.cnvDetails.items[i];
                                        if (this.tempSelectedCNVs.includes(row.oid)) {
                                            row.isSelected = true;
                                        }
                                    }
                                    for (var i = 0; i < this.$refs.translocationDetails.items.length; i++) {
                                        var row = this.$refs.translocationDetails.items[i];
                                        if (this.tempSelectedTranslocations.includes(row.oid)) {
                                            row.isSelected = true;
                                        }
                                    }
                                    this.updateSelectedVariantTable();
                                });
                            }
                        }).catch(error => {
                            this.handleDialogs(error.data, this.commitAnnotations.bind(null, userAnnotations));
                            this.$refs.annotationDialog.saving = false; 
                            this.$refs.cnvAnnotationDialog.saving = false;
                            this.$refs.translocationAnnotationDialog.saving = false;
                        });
                    } else {
                        this.handleDialogs(response.data, this.commitAnnotations.bind(null, userAnnotations));
                        this.$refs.annotationDialog.saving = false; 
                        this.$refs.cnvAnnotationDialog.saving = false;
                        this.$refs.translocationAnnotationDialog.saving = false;
                    }
                })
                .catch(error => {
                    this.handleAxiosError(error);
                });
        },
        selectVariantForReport() {
            if (!this.canProceed('canSelect') || this.readonly) {
                return;
            }
            var table = null;
            if (this.isSNP()) {
                table = this.$refs.geneVariantDetails;
            }
            else if (this.isCNV()) {
                table = this.$refs.cnvDetails;
            }
            else if (this.isTranslocation()) {
                table = this.$refs.translocationDetails;
            }
            table.addToSelection(this.currentRow);
            this.handleSelectionChanged();
        },
        removeVariantFromReport() {
            if (!this.canProceed('canSelect') || this.readonly) {
                return;
            }
            var table = null;
            if (this.isSNP()) {
                table = this.$refs.geneVariantDetails;
            }
            else if (this.isCNV()) {
                table = this.$refs.cnvDetails;
            }
            else if (this.isTranslocation()) {
                table = this.$refs.translocationDetails;
            }
            table.removeFromSelection(this.currentRow);
            this.handleSelectionChanged();
        },
        createTempSelectionPerAnnotator() {
            return "<span tabindex='-1' class='v-chip v-chip--disabled v-chip--label warning v-chip--small white--text'><span class='v-chip__content'><i aria-hidden='true' class='icon material-icons mdi mdi-checkbox-marked' style='font-size: 16px; vertical-align: bottom'></i><span class='pl-2'>latest</span></span></span>";
        },
        isVariantTempSelected(item) {
            return this.snpItemsTemp.filter(i => i.oid == item.oid && i.isSelected).length > 0;
        },
        isSelectionPerAnnotatorReal(selectionPerAnnotator) {
            //check if it's a real object or a html string (temp v-chip)
            return selectionPerAnnotator && selectionPerAnnotator.userId;
        },
        updateSelectedVariantTable() {
            if (this.updatingSelectedVariantTable) {
                return;
            }
            this.updatingSelectedVariantTable = true;
            this.$refs.reviewDialog.startLoading();

            this.getSelectedVariantIds(false).then(response => {
                if (response != null) {
                    this.currentSelectedVariantIds = response;
                    var selectedSNPVariants = this.snpIndelUnfilteredItems.filter(item => this.currentSelectedVariantIds.selectedSNPVariantIds.indexOf(item.oid) > -1);
                    var selectedSNPVariantsReviewer = this.snpIndelUnfilteredItems.filter(item => this.currentSelectedVariantIds.selectedSNPVariantIdsReviewer.indexOf(item.oid) > -1); //.filter(item => item.selectionPerAnnotator[this.caseOwnerId]);
                    var selectedCNVs = this.cnvUnfilteredItems.filter(item => this.currentSelectedVariantIds.selectedCNVIds.indexOf(item.oid) > -1);
                    var selectedCNVsReviewer = this.cnvUnfilteredItems.filter(item => this.currentSelectedVariantIds.selectedCNVIdsReviewer.indexOf(item.oid) > -1); //.filter(item => item.selectionPerAnnotator[this.caseOwnerId]);
                    var selectedTranslocations = this.ftlUnfilteredItems.filter(item => this.currentSelectedVariantIds.selectedTranslocationIds.indexOf(item.oid) > -1);
                    var selectedFTLsReviewer = this.ftlUnfilteredItems.filter(item => this.currentSelectedVariantIds.selectedTranslocationIdsReviewer.indexOf(item.oid) > -1); //.filter(item => item.selectionPerAnnotator[this.caseOwnerId]);
                    this.saveVariantDisabled = (selectedSNPVariants.length == 0 && selectedCNVs.length == 0 && selectedTranslocations.length == 0) || !this.canProceed('canAnnotate') || this.readonly;
                    //populate the selected variants but not yet saved by creating fake selectionPerAnnotator objects
                    for (var i= 0; i < selectedSNPVariants.length; i++) {
                        if (!this.isSelectionPerAnnotatorReal(selectedSNPVariants[i].selectionPerAnnotator[this.userId]) && this.isVariantTempSelected(selectedSNPVariants[i])) {
                            selectedSNPVariants[i]["dateSince" + this.userId] = this.createTempSelectionPerAnnotator();
                        }
                    }
                    
                    for (var i= 0; i < selectedSNPVariantsReviewer.length; i++) {
                        if (!selectedSNPVariantsReviewer[i].selectionPerAnnotator[this.userId]) {
                            selectedSNPVariantsReviewer[i].selectionPerAnnotator[this.userId] = this.createTempSelectionPerAnnotator();
                        }
                    }

                    
                    for (var i= 0; i < selectedCNVs.length; i++) {
                        if (!selectedCNVs[i].selectionPerAnnotator[this.userId]) {
                            selectedCNVs[i].selectionPerAnnotator[this.userId] = this.createTempSelectionPerAnnotator();
                        }
                    }
                    
                    for (var i= 0; i < selectedCNVsReviewer.length; i++) {
                        if (!selectedCNVsReviewer[i].selectionPerAnnotator[this.userId]) {
                            selectedCNVsReviewer[i].selectionPerAnnotator[this.userId] = this.createTempSelectionPerAnnotator();
                        }
                    }

                    
                    for (var i= 0; i < selectedTranslocations.length; i++) {
                        if (!selectedTranslocations[i].selectionPerAnnotator[this.userId]) {
                            selectedTranslocations[i].selectionPerAnnotator[this.userId] = this.createTempSelectionPerAnnotator();
                        }
                    }
                    
                    for (var i= 0; i < selectedFTLsReviewer.length; i++) {
                        if (!selectedFTLsReviewer[i].selectionPerAnnotator[this.userId]) {
                            selectedFTLsReviewer[i].selectionPerAnnotator[this.userId] = this.createTempSelectionPerAnnotator();
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
                    this.updateSplashProgress();
                }
            }).catch(error => {
                this.handleDialogs(error.data, this.updateSelectedVariantTable);
                this.updatingSelectedVariantTable = false;
            });
        },
        openReviewSelectionDialog() {
            this.reviewDialogVisible = true;
            this.urlQuery.showReview = true;
            this.updateRoute();
            // this.$nextTick(this.updateSelectedVariantTable());
        },
        closeReviewDialog() {
            this.reviewDialogVisible = false;
            this.urlQuery.showReview = false;
            this.updateRoute();
        },
        updateRoute() {
            router.push({ query: this.urlQuery });
        },
        saveSelection(closeAfter, skipSnackBar) {
            // There is a bug in vuetify 1.0.19 where a disabled menu still activates the click action.
            // Use a flag to disable the action in the meantime
            if (this.saveVariantDisabled) {
                return;
            }
            this.saveLoading = true;
            //TODO replace this by doing it on the server side instead
            this.getSelectedVariantIds(true).
            then(response => {
                this.currentSelectedVariantIds = response;
                axios({
                    method: 'post',
                    url: webAppRoot + "/saveVariantSelection",
                    params: {
                        caseId: this.$route.params.id,
                        closeAfter: closeAfter, //pass this param along to proceed with closing the dialog
                        skipSnackBar: skipSnackBar //pass this param along to display snackbar after successful ajax call
                    },
                    data: {
                        selectedSNPVariantIds: this.currentSelectedVariantIds.selectedSNPVariantIds,
                        selectedCNVIds: this.currentSelectedVariantIds.selectedCNVIds,
                        selectedTranslocationIds: this.currentSelectedVariantIds.selectedTranslocationIds
                    }
                }).then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        if (!response.data.skipSnackBar) {
                            this.showSnackBarMessage("Variant Selection Saved");
                        }
                        this.getAjaxData();
                        this.waitingForAjaxCount--;
                        this.variantUnSaved = false;
                        this.saveLoading = false;
                        if (response.data.uiProceed) {
                            this.closeReviewDialog(true);
                        }
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
            }).catch(error => {
                this.handleDialogs(error.data, this.saveSelection.bind(null, closeAfter, skipSnackBar));
                this.saveLoading = false;
            });
        },
        mdaAnnotationsExists() {
            return this.mdaAnnotations != '';
        },
        utswAnnotationsExists() {
            return this.utswAnnotationsFormatted.length > 0;
        },
        isRelatedVariantsVisible() {
            return this.annotationVariantRelatedVisible && this.isSNP() && this.currentVariantHasRelatedVariants;
        },
        isRelatedCNVVisible() {
            return this.annotationCNVRelatedVisible && this.isSNP() && this.currentVariantHasRelatedCNV;
        },
        toggleRelatedVariants() {
            if (this.currentVariantHasRelatedVariants) {
                this.annotationVariantRelatedVisible = !this.annotationVariantRelatedVisible;
            }
        },
        toggleRelatedCNV() {
            if (this.currentVariantHasRelatedCNV) {
                this.annotationCNVRelatedVisible = !this.annotationCNVRelatedVisible;
            }
        },
        saveCurrentFilters() {
            this.$refs.advancedFilter.loading = true;
            axios({
                method: 'post',
                url: webAppRoot + "/saveCurrentFilters",
                params: {
                    filterListId: this.$refs.advancedFilter.saveFilterSetId,
                    filterListName: this.$refs.advancedFilter.saveFilterSetName
                },
                data: {
                    filters: this.$refs.advancedFilter.filters
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    this.loadUserFilterSets();
                    this.$refs.advancedFilter.currentFilterSet = response.data.savedFilterSet;
                    this.$refs.advancedFilter.saveFilterSetDialogVisible = false;
                    this.snackBarMessage = "Filter Set Saved";
                    this.snackBarLink = "";
                    this.snackBarVisible = true;
                }
                else {
                    this.handleDialogs(response.data, this.saveCurrentFilters);
                }
                this.$refs.advancedFilter.loading = false;
            }
            ).catch(error => {
                this.$refs.advancedFilter.loading = false;
                this.handleAxiosError(error);
            }
            );
        },
        deleteFilterSet(filterSetId) {
            axios({
                method: 'post',
                url: webAppRoot + "/deleteFilterSet",
                params: {
                    filterSetId: filterSetId,
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    this.loadUserFilterSets();
                    this.$refs.advancedFilter.saveFilterSetDialogVisible = false;
                    this.snackBarMessage = "Filter Set Deleted";
                    this.snackBarLink = "";
                    this.snackBarVisible = true;
                }
                else {
                    this.handleDialogs(response.data, this.deleteFilterSet.bind(null, filterSetId));
                }
            }
            ).catch(error => {
                this.handleAxiosError(error);
            }
            );
        },
        loadUserFilterSets() {
            axios.get(
                webAppRoot + "/loadUserFilterSets",
                {
                    params: {
                    }
                })
                .then(response => {
                    if (response.data.isAllowed) {
                        this.$refs.advancedFilter.filterSets = response.data.filters;
                        this.$refs.advancedFilter.filterSetItems = response.data.items;
                    }
                    else {
                        this.handleDialogs(response, this.loadUserFilterSets);
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                });
        },
        openBamViewerLinkWeb() {
            this.$refs.bamViewerLink.$el.click();
        },
        openBamViewerLinkWebFLT3() {
            this.$refs.bamViewerLinkFLT3.$el.click();
        },
        createBamViewerLink() {
            var igvRange = this.currentVariant.chrom + ":";
            igvRange += this.currentVariant.pos - 100;
            igvRange += "-";
            igvRange += this.currentVariant.pos + 99;
            link = "../bamViewer?";
            link += "locus=" + igvRange;
            link += "&caseId=" + this.$route.params.id;
            return link;
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
                igvRange = this.currentVariant.chrom + ":";
                igvRange += this.currentVariant.pos - 100;
                igvRange += "-";
                igvRange += this.currentVariant.pos + 99;
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
                        var hiddenElement = document.createElement('a');
                        hiddenElement.href = webAppRoot + "/igv/" + response.data.payload;
                        hiddenElement.download = response.data.payload;
                        document.body.appendChild(hiddenElement);
                        hiddenElement.click();
                        document.body.removeChild(hiddenElement);
                    }
                    else {
                        this.handleDialogs(response.data, this.downloadIGVFile.bind(null, igvType));
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                });
        },
        closeVariantDetails(skipSave) {
            if (!skipSave) {
                this.handleSaveAll();
            }
            this.confirmationVariantDialogVisible = false;
            this.variantDetailsVisible = false;
            this.urlQuery.variantId = null;
            this.urlQuery.variantType = null;
            this.urlQuery.edit = false; //also close edit but it should have been done earlier
            zingchart.exec("cnvPlotDetails", 'destroy'); //kill the chart if variant details is closed
            this.updateRoute();
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
        annotationAllHidden() {
            return !this.annotationVariantDetailsVisible
                && !this.annotationVariantCanonicalVisible
                && !(this.mdaAnnotationsVisible && this.mdaAnnotationsExists())
                && !this.annotationVariantOtherVisible
                && !(this.utswAnnotationsVisible && this.utswAnnotationsExists());
        },
        selectedIdsPreFiltering(i, currentUserOnly) {
            // if (currentUserOnly) {
            //     return i.isSelected;
            // }
            // return i.selectionPerAnnotator && Object.keys(i.selectionPerAnnotator).length > 0;
            return i.isSelected || (i.selectionPerAnnotator && Object.keys(i.selectionPerAnnotator).length > 0);
        },
        selectedIdsPreFilteringTempOnly(i) {
            return (i.isSelected && !this.isSelectionPerAnnotatorReal(i.selectionPerAnnotator[this.userId]));
            // return (i.isSelected && (!i.selectionPerAnnotator || Object.keys(i.selectionPerAnnotator).length == 0));
        },
        simplifiedSelectedVariant(i) {
            return {"oid": i.oid, "isSelected": i.isSelected, "selectionPerAnnotator": this.removeSelectedSince(i.selectionPerAnnotator)};
        },
        removeSelectedSince(i) {
            for (var item in i) {
                if (i[item].selectedSince != undefined) {
                    i[item].selectedSince = ""; //handles regular cases
                }
                else {
                   console.log(i[item]); 
                   i[item] = ""; //handles the "latest" case
                }
                
            }
            return i;
        },
        getSelectedVariantIds(currentUserOnly) {
            return new Promise((resolve, reject) => {
                var filteredSNPItems = this.$refs.geneVariantDetails.items.filter(i => this.selectedIdsPreFiltering(i, currentUserOnly)).map(i => (this.simplifiedSelectedVariant(i)));
                var filteredSNPItemsTemp = this.$refs.geneVariantDetails.items.filter(i => this.selectedIdsPreFilteringTempOnly(i)).map(i => (this.simplifiedSelectedVariant(i)));
                var unfilteredSNPItems = this.snpIndelUnfilteredItems.filter(i => this.selectedIdsPreFiltering(i, currentUserOnly)).map(i => (this.simplifiedSelectedVariant(i)));
                var unfilteredSNPItemsTemp = this.snpIndelUnfilteredItems.filter(i => this.selectedIdsPreFilteringTempOnly(i)).map(i => (this.simplifiedSelectedVariant(i)));
                var filteredCNVItems = this.$refs.cnvDetails.items.filter(i => this.selectedIdsPreFiltering(i, currentUserOnly)).map(i => (this.simplifiedSelectedVariant(i)));
                var unfilteredCNVItems = this.cnvUnfilteredItems.filter(i => this.selectedIdsPreFiltering(i, currentUserOnly)).map(i => (this.simplifiedSelectedVariant(i)));
                var filteredFTLItems = this.$refs.translocationDetails.items.filter(i => this.selectedIdsPreFiltering(i, currentUserOnly)).map(i => (this.simplifiedSelectedVariant(i)));
                var unfilteredFTLItems = this.ftlUnfilteredItems.filter(i => this.selectedIdsPreFiltering(i, currentUserOnly)).map(i => (this.simplifiedSelectedVariant(i)));

                this.snpItemsTemp = filteredSNPItemsTemp.concat(unfilteredSNPItemsTemp);


                axios({
                    method: 'post',
                    url: webAppRoot + "/getSelectedVariantIds",
                    params: {
                        caseId: this.$route.params.id,
                        currentUserOnly: currentUserOnly
                    },
                    data: {
                        filteredSNPItems: filteredSNPItems, 
                        unfilteredSNPItems: unfilteredSNPItems,
                        filteredCNVItems: filteredCNVItems, 
                        unfilteredCNVItems: unfilteredCNVItems,
                        filteredFTLItems: filteredFTLItems, 
                        unfilteredFTLItems: unfilteredFTLItems
                    }
                }).then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        var selectedSNPVariantIds = [];
                        var selectedSNPVariantIdsReviewer = [];
                        var selectedCNVIds = [];
                        var selectedCNVIdsReviewer = [];
                        var selectedTranslocationIds = [];
                        var selectedTranslocationIdsReviewer = [];
                        // if (this.$refs.geneVariantDetails) {
                        //     selectedSNPVariantIds = this.getFilteredAndUnfilteredVariantIds(this.$refs.geneVariantDetails.items, this.snpIndelUnfilteredItems, true);
                        //     selectedSNPVariantIdsReviewer = this.getFilteredAndUnfilteredVariantIds(this.$refs.geneVariantDetails.items, this.snpIndelUnfilteredItems, false);
                        // }
                        // if (this.$refs.cnvDetails) {
                        //     selectedCNVIds = this.getFilteredAndUnfilteredVariantIds(this.$refs.cnvDetails.items, this.cnvUnfilteredItems, true);
                        // }
                        // if (this.$refs.translocationDetails) {
                        //     selectedTranslocationIds = this.getFilteredAndUnfilteredVariantIds(this.$refs.translocationDetails.items, this.ftlUnfilteredItems, true);
                        // }
                        if (this.$refs.geneVariantDetails) {
                            selectedSNPVariantIds = response.data.payload.snpIdsAll;
                            selectedSNPVariantIdsReviewer = response.data.payload.snpIdsReviewer;
                        }
                        if (this.$refs.cnvDetails) {
                            selectedCNVIds = response.data.payload.cnvIdsAll;
                            selectedCNVIdsReviewer = response.data.payload.cnvIdsReviewer;
                        }
                        if (this.$refs.translocationDetails) {
                            selectedTranslocationIds = response.data.payload.ftlIdsAll;
                            selectedTranslocationIdsReviewer = response.data.payload.ftlIdsReviewer;
                        }
                        resolve({
                            selectedSNPVariantIds: selectedSNPVariantIds ? selectedSNPVariantIds : null,
                            selectedSNPVariantIdsReviewer: selectedSNPVariantIdsReviewer ? selectedSNPVariantIdsReviewer : null,
                            selectedCNVIds: selectedCNVIds ? selectedCNVIds : null,
                            selectedCNVIdsReviewer: selectedCNVIdsReviewer ? selectedCNVIdsReviewer : null,
                            selectedTranslocationIds: selectedTranslocationIds  ? selectedTranslocationIds : null,
                            selectedTranslocationIdsReviewer: selectedTranslocationIdsReviewer ? selectedTranslocationIdsReviewer : null
                        });
                    }
                    else {
                        reject(response);
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                });
            });
            
        },
        updateVariantDetails() {
            this.urlQuery.variantId = this.currentVariant._id.$oid;
            this.urlQuery.variantType = this.currentVariantType;
            this.updateRoute();
        },
        updateEditAnnotationBreadcrumbs(visible) {
            this.urlQuery.edit = visible;
            zingchart.exec("cnvPlotEdit", 'destroy'); //kill the chart if edit annotation is closed
            // console.log("killing cnv plot");
            this.updateRoute();
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
                        this.handleDialogs(response, this.saveCaseAnnotations);
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                });
        },
        handleSelectionChanged(selectedSize, item) {
            this.variantUnSaved = true;
            if (item) {
                //update unfiltered items
                for (var i = 0; i < this.snpIndelUnfilteredItems.length; i++){
                    if (this.snpIndelUnfilteredItems[i].oid == item.oid) {
                        this.snpIndelUnfilteredItems[i].isSelected = item.isSelected;
                        if (!item.isSelected) {
                            delete this.snpIndelUnfilteredItems[i].selectionPerAnnotator[this.userId];
                            this.snpIndelUnfilteredItems[i]["dateSince" + this.userId] = "";
                        }
                        break;
                    }
                }
            }
        },
        isCaseAnnotationChanged() {
            return this.caseNotesChanged;
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
        loadPrevVariant() {
            var table; //could be the selected table or the regular one
            if (this.isSNP()) {
                if (this.reviewDialogVisible) {
                    table = this.$refs.reviewDialog.getSnpTable();
                }
                else {
                    table = this.$refs.geneVariantDetails;
                }
                var prevVariant = table.getPreviousItem(this.currentRow);
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
                var prevVariant = table.getPreviousItem(this.currentRow);
                if (prevVariant) {
                    this.getCNVDetails(prevVariant);
                }
            }
            else if (this.isTranslocation()) {
                if (this.reviewDialogVisible) {
                    table = this.$refs.reviewDialog.getFtlTable();
                }
                else {
                    table = this.$refs.translocationDetails;
                }
                var prevVariant = table.getPreviousItem(this.currentRow);
                if (prevVariant) {
                    this.getTranslocationDetails(prevVariant);
                }
            }
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
                var nextVariant = table.getNextItem(this.currentRow);
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
                var nextVariant = table.getNextItem(this.currentRow);
                if (nextVariant) {
                    this.getCNVDetails(nextVariant);
                }
            }
            else if (this.isTranslocation()) {
                if (this.reviewDialogVisible) {
                    table = this.$refs.reviewDialog.getFtlTable();
                }
                else {
                    table = this.$refs.translocationDetails;
                }
                var nextVariant = table.getNextItem(this.currentRow);
                if (nextVariant) {
                    this.getTranslocationDetails(nextVariant);
                }
            }
        },
        saveVariant(skipSnackBar) {
            if (!this.canProceed('canAnnotate')) {
                return;
            }
            this.savingVariantDetails = true;
            var lightVariant = {};
            lightVariant["_id"] = this.currentVariant._id;
            lightVariant["tier"] = this.currentVariant.tier;
            lightVariant["aberrationType"] = this.currentVariant.aberrationType;
            lightVariant["notation"] = this.currentVariant.notation;
            axios({
                method: 'post',
                url: webAppRoot + "/saveVariant",
                params: {
                    variantType: this.currentVariantType,
                    caseId: this.$route.params.id,
                    skipSnackBar: skipSnackBar
                },
                data: {
                    // filters: this.$refs.advancedFilter.filters,
                    variant: lightVariant
                }
            }).then(response => {
                this.waitingForAjaxCount--;
                if (response.data.isAllowed) {
                    if (!response.data.skipSnackBar) {
                        this.revertVariant();
                        this.showSnackBarMessage("Variant Saved");
                    }
                    this.$refs.variantDetailsPanel.variantDetailsUnSaved = false; //update badge on save button
                }
                else {
                    this.handleDialogs(response.data, this.saveVariant.bind(null, response.data.skipSnackBar));
                }
                this.savingVariantDetails = false;
            }).catch(error => {
                this.savingVariantDetails = false;
                this.handleAxiosError(error);
            });
        },
        revertVariant() {
            if (this.isSNP()) {
                this.getVariantDetails(this.currentRow);
            }
            else if (this.isCNV()) {
                this.getCNVDetails(this.currentRow);
            }
            else if (this.isTranslocation()) {
                this.getTranslocationDetails(this.currentRow);
            }
            this.$refs.variantDetailsPanel.variantDetailsUnSaved = false;  //update badge on save button
        },
        saveAnnotationSelection(skipSnackBar) {
            if (!this.canProceed('canAnnotate')) {
                return;
            }
            this.savingAnnotationSelection = true;
            var lightVariant = {};
            lightVariant["_id"] = this.currentVariant._id;
            lightVariant["annotationIdsForReporting"] = [];
            for (var i = 0; i < this.utswAnnotationsFormatted.length; i++) {
                if (this.utswAnnotationsFormatted[i].isSelected) {
                    lightVariant["annotationIdsForReporting"].push(this.utswAnnotationsFormatted[i]._id);
                }
            }
            axios({
                method: 'post',
                url: webAppRoot + "/saveSelectedAnnotationsForVariant",
                params: {
                    variantType: this.currentVariantType,
                    caseId: this.$route.params.id,
                    skipSnackBar: skipSnackBar
                },
                data: {
                    // filters: this.$refs.advancedFilter.filters,
                    variant: lightVariant
                }
            }).then(response => {
                this.waitingForAjaxCount--;
                if (response.data.isAllowed) {
                    this.revertAnnotationSelection();
                    if (!response.data.skipSnackBar) {
                        this.snackBarMessage = "Annotation Selection Saved";
                        this.snackBarLink = "";
                        this.snackBarVisible = true;
                        this.annotationSelectionUnSaved = false;
                    }
                }
                else {
                    this.handleDialogs(response.data, this.saveVasaveAnnotationSelectionriant.bind(null, response.data.skipSnackBar));
                }
                this.savingAnnotationSelection = false;
            }).catch(error => {
                this.savingAnnotationSelection = false;
                this.handleAxiosError(error);
            });
        },
        isAnnotationTierMissing() {
            for (var i = 0; i < this.utswAnnotationsFormatted.length; i++) {
                if (this.utswAnnotationsFormatted[i].isSelected && !this.utswAnnotationsFormatted[i].tier) {
                    return true;
                }
            }
            return false;
        },
        handleAxiosError(error) {
            console.log(error);
            this.splashProgress = 100;
            bus.$emit("some-error", [this, error]);
            this.waitingForAjaxMessage = "There were some errors while saving";
        },
        revertAnnotationSelection() {
            // if (this.isSNP()) {
            //     this.getVariantDetails(this.currentRow, true);
            // }
            // else if (this.isCNV()) {
            //     this.getCNVDetails(this.currentRow, true);
            // }
            // else if (this.isTranslocation()) {
            //     this.getTranslocationDetails(this.currentRow, true);
            // }
            this.annotationIdsForReporting = []; //reset the unsaved list of ids
            this.annotationSelectionUnSaved = false; //update badge on save button
        },
        handleAnnotationSelectionChanged() {
            this.annotationSelectionUnSaved = true;
            this.annotationIdsForReporting = [];
            for (var j = 0; j < this.utswAnnotationsFormatted.length; j++) {
                if (this.utswAnnotationsFormatted[j].isSelected) {
                    this.annotationIdsForReporting.push(this.utswAnnotationsFormatted[j]._id);
                }
            }
        },
        // Use this when need to close the annotation dialog from outside the edit-annotations component
        cancelAnnotations() {
            if (this.isSNP()) {
                this.$refs.annotationDialog.cancelAnnotations();
            }
            else if (this.isCNV()) {
                this.$refs.cnvAnnotationDialog.cancelAnnotations();
            }
            else if (this.isTranslocation()) {
                this.$refs.translocationAnnotationDialog.cancelAnnotations();
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
        createVariantName() {
            var text = this.currentVariant.geneName + " " + this.currentVariant.notation;
            if (text.length > 18) {
                return text.substring(0, 18) + "...";
            } 
            return text;
        },
        variantNameIsTooLong() {
            var text = this.currentVariant.geneName + " " + this.currentVariant.notation;
            return text.length > 18;
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
        openOncoTree() {
           var oncotreeWindow = window.open("http://oncotree.mskcc.org", "_blank");
        },
        openOncoKBGeniePortalCancer() {
            var url = oncoKBGeniePortalUrl + "Cancer/?Oncotree=" + this.patientDetailsOncoTreeDiagnosis.text;
            window.open(url, "_blank");
        },
        createOncoKBGeniePortalGene() {
            return oncoKBGeniePortalUrl + "Gene/?gene_name=" + this.currentVariant.geneName;
        },
        createOncoKBGeniePortalVariant() {
            return oncoKBGeniePortalUrl + "Variant/?gene_name=" + this.currentVariant.geneName
            + "&variant=" + this.currentVariant.notation + "&Oncotree=" + this.patientDetailsOncoTreeDiagnosis.text;
        },
        createOncoKBGeniePortalCNV(geneName) {
            var ampDel = "";
            var gene = "";
            if (this.currentVariant.aberrationType == "amplification") {
                ampDel = "Amplification";
            }
            else if (this.currentVariant.aberrationType == "homozygous loss" ) {
                ampDel = "Deletion";
            }
            if (geneName) {
                gene = geneName;
            }
            return oncoKBGeniePortalUrl + "CNA/?gene_name=" + gene + "&Amp_Del="
            + ampDel + "&Oncotree=" + this.patientDetailsOncoTreeDiagnosis.text;
        },
        openLookupLink(geneName) {
            var link = this.createOncoKBGeniePortalCNV(geneName);
            window.open(link, "_blank");
        },
        createOncoKBGeniePortalFusion() {
            return oncoKBGeniePortalUrl + "Fusion/?gene1=" + this.currentVariant.leftGene
            + "&gene2=" + this.currentVariant.rightGene + "&Oncotree=" + this.patientDetailsOncoTreeDiagnosis.text;
        },
        handleRouteChanged(newRoute, oldRoute) {
            if (newRoute.path != oldRoute.path) { //prevent reloading data if only changing the query router.push({query: {test:"hello3"}})
                this.getAjaxData();
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
        handlePanelVisibility(visible) {
            if (visible == null) {
                if (this.urlQuery.edit) {
                    this.editAnnotationVariantDetailsVisible = !this.editAnnotationVariantDetailsVisible;
                }
                else {
                    this.annotationVariantDetailsVisible = !this.annotationVariantDetailsVisible;
                }
            }
            else {
                if (this.urlQuery.edit) {
                    this.editAnnotationVariantDetailsVisible = visible;
                }
                else {
                    this.annotationVariantDetailsVisible = visible;
                }
            }
        },
        matchAnnotationFilter() {
            if (this.searchAnnotations || this.searchAnnotationCategory.length > 0
                || this.searchAnnotationClassification.length > 0 || this.searchAnnotationTier.length > 0
                || this.searchAnnotationScope.length > 0) {
                for (var i = 0; i < this.utswAnnotationsFormatted.length; i++) {
                    var foundTextMatch = false;
                    var foundCategoryMatch = false;
                    var foundBreadthMatch = false;
                    var foundClassificationMatch = false;
                    var foundTierMatch = false;
                    var foundScopeMatch = false;
                    if (this.searchAnnotations) {
                        var json = JSON.stringify(this.utswAnnotationsFormatted[i]);
                        if (json.indexOf(this.searchAnnotations) > -1) {
                          foundTextMatch = true;
                        }
                        // for (var field in this.utswAnnotationsFormatted[i]) {
                        //     if (field == "scopeTooltip") {
                        //         continue;
                        //     }
                        //     else if (this.utswAnnotationsFormatted[i][field] && (this.utswAnnotationsFormatted[i][field] + "").indexOf(this.searchAnnotations) > -1) {
                        //         foundTextMatch = true;
                        //         break;
                        //     }
                        // }
                    }
                    else {
                        foundTextMatch = true;
                    }
                    // continue search with Category
                    if (this.searchAnnotationCategory.length > 0) {
                        if (this.searchAnnotationCategory.includes(this.utswAnnotationsFormatted[i].category)) {
                            foundCategoryMatch = true;
                        }
                    }
                    else {
                        foundCategoryMatch = true;
                    }
                    // continue search with Breadth
                    if (this.searchAnnotationBreadth.length > 0) {
                        if (this.searchAnnotationBreadth.includes(this.utswAnnotationsFormatted[i].breadth)) {
                            foundBreadthMatch = true;
                        }
                    }
                    else {
                        foundBreadthMatch = true;
                    }
                    // continue search with Classification
                    if (this.searchAnnotationClassification.length > 0) {
                        if (this.searchAnnotationClassification.includes(this.utswAnnotationsFormatted[i].classification)) {
                            foundClassificationMatch = true;
                        }
                    }
                    else {
                        foundClassificationMatch = true;
                    }
                    // continue search with Tier
                    if (this.searchAnnotationTier.length > 0) {
                        if (this.searchAnnotationTier.includes(this.utswAnnotationsFormatted[i].tier)) {
                            foundTierMatch = true;
                        }
                    }
                    else {
                        foundTierMatch = true;
                    }
                    // continue search with Scope
                    //the select items need to match the scopeLevels array
                    //to find the corresponding scope flag
                    if (this.searchAnnotationScope.length > 0) {
                        for (var j = 0; j < this.searchAnnotationScope.length; j++) {
                            var scope = this.searchAnnotationScope[j];
                            var scopeIndex = -1;
                            for (var k = 0; k < this.utswAnnotationsFormatted[i].scopeLevels.length; k++) { //iterate through scopeLevels to find a match
                                if (this.utswAnnotationsFormatted[i].scopeLevels[k].indexOf(scope) > -1) {
                                    scopeIndex = k;
                                    break;
                                }
                            }
                            if (scopeIndex > -1 && this.utswAnnotationsFormatted[i].scopes[scopeIndex]) {
                                foundScopeMatch = true;
                                break;
                            }

                        }
                    }
                    else {
                        foundScopeMatch = true;
                    }
                    this.utswAnnotationsFormatted[i].visible = foundTextMatch
                        && foundCategoryMatch
                        && foundClassificationMatch
                        && foundTierMatch
                        && foundScopeMatch;
                }
            }
            else {
                for (var i = 0; i < this.utswAnnotationsFormatted.length; i++) {
                    this.utswAnnotationsFormatted[i].visible = true;
                }
            }
        },
        handleSplashVisibility() {
            if (this.splashProgress > 95) {
                setTimeout(() => {
                    this.splashDialog = false;
                    splashDialog = false; //disable from now on
                    if (splashInterval) {
                        clearInterval(splashInterval);
                    }
                }, 500);
                //make sure the dialogs are stacked up properly
                if (this.urlQuery.showReview && this.urlQuery.variantId) {
                    var styleVariantDetails = this.$refs.variantDetailsDialog.$children[0].$el.parentElement.style;
                    var styleShowReview = this.$refs.reviewDialog.$parent.$parent.$children[0].$el.parentElement.style;
                    var zIndexVariantDetails = parseInt(styleVariantDetails.zIndex);
                    var zIndexShowReview = parseInt(styleShowReview.zIndex);
                    while (zIndexVariantDetails <= zIndexShowReview) {
                        zIndexVariantDetails++;
                    }
                    styleVariantDetails.zIndex = zIndexVariantDetails + "";
                }
            }
            else {

            }
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
            if (this.$refs.variantDetailsPanel && this.$refs.variantDetailsPanel.variantDetailsUnSaved) {
                tooltip.push("- Variant Details");
            }
            if (tooltip.length > 1) {
                return tooltip.join("<br/>");
            }
            return "Nothing to Save";
        },
        isSaveNeededBadgeVisible() {
            if (!this.saveAllNeeded) {
                this.saveAllNeeded = this.annotationSelectionUnSaved 
                || this.variantUnSaved 
                || this.patientDetailsUnSaved 
                || (this.$refs.variantDetailsPanel ? this.$refs.variantDetailsPanel.variantDetailsUnSaved : false)
                || this.isCaseAnnotationChanged();
            }
            return this.saveAllNeeded && !this.readonly;
        },
        buildAberrationTypeHelp() {
            var message = "amplification: High level copy number gain</br>"
                + "gain: Low level copy number gain</br>"
                + "homozygous loss: Two copy loss</br>"
                + "hemizygous loss: Single Copy Loss with remaining allele WT</br>";
            return message;
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
        showSnackBarMessage(message) {
            this.snackBarMessage = message;
            this.snackBarLink = "";
            this.snackBarVisible = true;
        },
        showSnackBarMessageWithParams(snackBarMessage, snackBarLink, snackBarLinkIcon, snackBarTimeout) {
            this.snackBarMessage = snackBarMessage;
            this.snackBarLink = snackBarLink;
            this.snackBarLinkIcon = snackBarLinkIcon;
            this.snackBarTimeout = snackBarTimeout != null ? snackBarTimeout : 4000;
            this.snackBarVisible = true;
        },
        formatChrom(chrom) { //needed to call the global function from v-text
            return formatChrom(chrom);
        },
        populateOncotreeLabel() {
            for (var i = 0; i < this.oncotree.length; i++) {
                if (this.oncotree[i].text == this.patientDetailsOncoTreeDiagnosis.text) {
                    this.patientDetailsOncoTreeDiagnosis.label = this.oncotree[i].label;
                    break;
                }
            }
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
                    this.saveAnnotationSelection(true);
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
        handleWaitingForAjaxCount() {
            if (this.waitingForAjaxActive && this.waitingForAjaxCount <= 0) {
                this.waitingForAjaxActive = false;
                this.saveAllNeeded = false;
                this.showSnackBarMessage(this.waitingForAjaxMessage);
                clearInterval(this.autoSaveInterval);
                this.createAutoSaveInterval();
            }
        },
        collectOncoTreeDiagnosis() {
            this.oncotree = oncotree;
            this.populateOncotreeLabel();
        },
        createAutoSaveInterval() {
            this.autoSaveInterval = setInterval(() => {
                var editing = this.$route.query.edit === true || this.$route.query.edit === "true";
                if (!this.waitingForAjaxActive && !editing) {
                    this.handleSaveAll(true);
                }
            }, 120000);
        },
        copyMDAAnnotation(mdaAnnotation, variantType) {
            if (!this.canProceed('canAnnotate') || this.readonly) {
                return;
            }
            var regex = /(PMID:)([0-9]*)/g;
            var text = this.mdaAnnotations.annotationCategories[mdaAnnotation.tempId].text;
            var match = regex.exec(text);
            var pmids = [];
            while (match != null) {
                pmids.push(match[2]);
                match = regex.exec(text);
            }
            var tempSet = new Set();
            for (var s = 0; s < pmids.length; s++) {
                tempSet.add(pmids[s]);
            }
            pmids = tempSet.size != 0 ? Array.from(tempSet) : null;
            var variantId = null;
            if (mdaAnnotation.variantSpecific) {
                variantId = this.currentVariant._id.$oid;
            }
            var utswAnnotation = {
                origin: "UTSW",
                text: text,
                markedForDeletion: false,
                isVisible: true,
                geneId: this.currentVariant.geneName,
                caseId: null,
                pmids: pmids,
                // nctIds: null,
                isTumorSpecific: mdaAnnotation.tumorSpecific,
                userId: null,
                variantId: variantId,
                isGeneSpecific: true,
                isVariantSpecific: mdaAnnotation.variantSpecific,
                isCaseSpecific: false,
                isLeftSpecific: false,
                isRightSpecific: false,
                category: mdaAnnotation.category,
                createdDate: null,
                modifiedDate: null,
                _id: null,
                classification: null,
                tier: null,
                type: variantType,
                trial: null
            }
            this.userAnnotations.push(utswAnnotation);
            this.commitAnnotations(this.userAnnotations);
        },
        copyAnnotation(annotation, variantType) {
            if (!this.canProceed('canAnnotate') || this.readonly || !this.canCopyAnnotation || this.loadingVariant) {
                return;
            }
            this.canCopyAnnotation = false;
            var annotationString = JSON.stringify(annotation);
            var newAnnotation = JSON.parse(annotationString);
            newAnnotation._id = null;
            newAnnotation.text = "(copy) " + annotation.text;
            newAnnotation.origin = "UTSW";
            newAnnotation.createdDate = null;
            newAnnotation.modifiedDate = null;
            newAnnotation.userId = this.userId;
            newAnnotation.fullName = null;
            newAnnotation.type = variantType;
            newAnnotation.isCaseSpecific = newAnnotation.scopes[0];
            if (newAnnotation.type == "snp") {
            newAnnotation.isGeneSpecific = newAnnotation.scopes[1];
            newAnnotation.isVariantSpecific = newAnnotation.scopes[2];
            newAnnotation.isTumorSpecific = newAnnotation.scopes[3];
            }
            else if (newAnnotation.type == "cnv") {
                newAnnotation.cnvGenes = (annotation.cnvGenes == "") ? [] : annotation.cnvGenes;
                newAnnotation.isGeneSpecific = true;
                newAnnotation.isVariantSpecific = newAnnotation.scopes[1];
                newAnnotation.isTumorSpecific = newAnnotation.scopes[2];
                newAnnotation.category = annotation.category ? annotation.category.split(" ")[0] : null;
                newAnnotation.cnvGenes = annotation.cnvGenes ? annotation.category.split(" ") : [];
            }
            else if (newAnnotation.type == "translocation") {
                newAnnotation.isGeneSpecific = true;
                newAnnotation.isVariantSpecific = newAnnotation.scopes[1];
                newAnnotation.isTumorSpecific = newAnnotation.scopes[2];
                newAnnotation.isLeftSpecific = newAnnotation.scopes[3];
                newAnnotation.isRightSpecific = newAnnotation.scopes[4];
            }
            console.log(annotation);
            console.log(newAnnotation);
            this.userAnnotations.push(newAnnotation);
            this.commitAnnotations(this.userAnnotations);
        },
        setDefaultTranscript(item) {
            axios({
                method: 'post',
                url: webAppRoot + "/setDefaultTranscript",
                params: {
                    variantId: this.currentVariant._id["$oid"],
                },
                data: item
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        //reload the variant
                        // console.log("success");
                        var variantId = this.currentVariant._id["$oid"];
                        if (this.isSNP()) {
                            for (var i = 0; i < this.$refs.geneVariantDetails.items.length; i++) {
                                if (this.$refs.geneVariantDetails.items[i].oid == variantId) {
                                        this.$nextTick(this.getVariantDetails(this.$refs.geneVariantDetails.items[i]));
                                        break;
                                }
                            }
                        }
                        else if (this.isCNV()) {
                            for (var i = 0; i < this.$refs.cnvDetails.items.length; i++) {
                                if (this.$refs.cnvDetails.items[i].oid == variantId) {
                                        this.$nextTick(this.getCNVDetails(this.$refs.cnvDetails.items[i]));
                                        break;
                                }
                            }
                        }
                        else if (this.isTranslocation()) {
                            for (var i = 0; i < this.$refs.translocationDetails.items.length; i++) {
                                if (this.$refs.translocationDetails.items[i].oid == variantId) {
                                        this.$nextTick(this.getTranslocationDetails(this.$refs.translocationDetails.items[i]));
                                        break;
                                }
                            }
                        }
                        this.showSnackBarMessageWithParams(response.data.message, null, null, 4000);
                    }
                    else {
                        this.handleDialogs(response.data, this.setDefaultTranscript.bind(null, item));
                    }
                })
                .catch(error => {
                    alert(error);
                });
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
        openIDTCreationDialog() {
            this.itdDialogVisible = true;
        },
        cancelBreaks() {
            axios({
                method: 'post',
                url: webAppRoot + "/cancelBreaks",
                params: {
                },
                data: {
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    this.showSnackBarMessage("Breaks will no longer appear. Go to Preferences to reset.");
                    this.showGoodiesPanel = false;
                }
                else {
                    this.handleDialogs(response.data, this.cancelBreaks);
                }
            }).catch(error => {
                this.handleAxiosError(error);
            });
        },
        endGoodies() {
            this.showGoodiesPanel = false;
            if (this.$refs.goodiesPanel.demo) {
                this.$refs.goodiesPanel.demo.destroy();
            }
        },
        tryGoodies() {
            if (this.waitingForGoodies) {
                this.waitingForGoodies = false;
                this.showGoodiesPanel = true;
                this.$refs.goodiesPanel.activateGoodies();
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
        openFPKMChart(event) {
            this.fpkmPositionx = event.clientX;
            this.fpkmPositiony = event.clientY;
            this.fpkmVisible = true;
            // this.$nextTick(() => {
            //     this.$refs.fpkmPlot.updateFPKMPlot();
            // });
        },
        closeFPKMChart() {
            this.fpkmVisible = false;
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
        getHighlightClass(index) {
            return (this.highlightLatestAnnotation && index == 0 && !this.canCopyAnnotation) ? 'highlight-active' : '';
        },
        stopHighlightAnimation() {
            //stop animation of latest card
            if (this.highlightLatestAnnotation) {
               setTimeout(() => {
                   this.highlightLatestAnnotation = false;
               }, 2000);
           }
        }

    },
    mounted() {
        this.snackBarMessage = this.readonly ? "View Only Mode: some actions have been disabled" : "",
            this.snackBarLink = "";
        this.snackBarVisible = this.readonly;
        
        this.collectOncoTreeDiagnosis();
        this.getAjaxData();
        this.loadUserFilterSets();
        bus.$emit("clear-item-selected", [this]);
        this.getVariantFilters();
        this.loadCaseAnnotations();

        this.$refs.geneVariantDetails.headerOptionsVisible = true;
        this.$refs.splashScreen.manageSplashScreen();
        this.getCNVChromList();
    },
    created() {
        bus.$on('bam-viewer-closed', () => {
            this.externalWindowOpen = false;
        });
        this.createAutoSaveInterval();
        bus.$on('setDefaultTranscript', (item) => {
            this.setDefaultTranscript(item);
        });
        bus.$on('create-new-cnv', (genes) => {
            this.openAddCNVDialog(genes);
        });
        bus.$on('show-snackbar', (message, timeout) => {
            this.showSnackBarMessageWithParams(message, null, null, timeout);
        });
    },
    computed: {
        webAppRoot() {
            return webAppRoot;
        }
    },
    destroyed: function () {
        bus.$off('bam-viewer-closed');
        // bus.$off('saving-annotations');
        bus.$emit("update-status-off", this);
        clearInterval(this.autoSaveInterval);
        bus.$off('setDefaultTranscript');
        bus.$off('create-new-cnv');
        bus.$off('show-snackbar');
    },
    watch: {
        '$route': 'handleRouteChanged',
        variantTabActive: "handleTabChanged",
        splashProgress: "handleSplashVisibility",
        waitingForAjaxCount: "handleWaitingForAjaxCount"
    }

};