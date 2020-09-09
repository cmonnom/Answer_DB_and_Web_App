Vue.component('variant-details-dialog', {
    props: {
        colors: {default: () => {}, type: Object},
        isSaveNeededOverall: {default: false, type: Boolean},
        saveTooltip: {default: "", type: String},
        loadingColor: {default: "", type: String},
        caseName: {default: "", type: String},
        caseTypeIcon: {default: "", type: String},
        caseType: {default: "", type: String},
        isSaveLoading: {default: false, type: Boolean}, //TODO handle save loading
        breadcrumbs: {default: () =>[], type: Array},
        readonly: {default: false, type: Boolean},
        cnvChromList: {default: () =>[], type: Array},
        variantDetailsVisible: {default: false, type: Boolean},
        urlQuery: {default: () => {}, type: Object},
        variantTiers: {default: () => [
            '1A',
            '1B',
            '2C',
            '2D',
            '3',
            '4'
            ], type: Array},
        annotationCategories: {default: () => [
            'Gene Function',
            'Epidemiology',
            'Variant Function',
            'Prognosis',
            'Diagnosis',
            'Therapy',
            'Likely Artifact'], type: Array},
        annotationCategoriesCNV: {default: () => [
            'Epidemiology',
            'Prognosis',
            'Diagnosis',
            'Therapy'], type: Array},
        annotationBreadth: {default: () => [
            'Chromosomal',
            'Focal'], type: Array},
        annotationClassifications: {default: () => [
            'VUS',
            'Benign',
            'Likely benign',
            'Likely pathogenic',
            'Pathogenic'], type: Array},
        annotationPhases: {default:() => ["Phase 1", "Phase 2", "Phase 3", "Phase 4"], type: Array},
        scopesSNP: {default: () => [
            'Case', 'Gene', 'Variant', 'Tumor'
        ], type: Array},
        scopesCNV: {default: () => [
            'Case', 'Tumor'
        ], type: Array},
        scopesTranslocation: {default: () => [
            'Case', 'Tumor'
        ], type: Array},
        scopesVirus: {default: () => [
            'Case', 'Gene', 'Variant', 'Tumor'
        ], type: Array},
        aberrationTypes: {default: () => [
            'amplification',
            'gain',
            'hemizygous loss',
            'homozygous loss',
            'ITD'
        ], type: Array},
        oncotree: {default: () => [], type: Array},
    },
    template: /*html*/`<div>

    <!-- add CNV dialog -->
    <v-dialog v-model="addCNVDialogVisible" max-width="500px" scrollable>
      <add-cnv  @hide-add-cnv-panel="closeAddCNVDialog"
      :no-edit="!canProceed('canAnnotate') || readonly"
    :aberration-types="aberrationTypes"
    :cnv-chrom-list="cnvChromList"
    @refresh-cnv-table="refreshVariantTables"
    :current-gene-list="currentListOfCNVVisibleGenes"></add-cnv>
    </v-dialog>

    <!-- annotation dialog -->
    <edit-annotations type="snp" @saving-annotations="commitAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs"
        :color="colors.editAnnotation" ref="annotationDialog" :title="createVariantName() + ' -- ' + caseName + ' --'"
        :caseIcon="caseTypeIcon" :caseType="caseType" :outsideACase="false"
        :breadcrumbs="breadcrumbs" :annotation-categories="annotationCategories" :annotation-tiers="variantTiers" :annotation-classifications="annotationClassifications"
        :annotation-phases="annotationPhases"
        :userAnnotations="userAnnotations"
        @toggle-panel="handlePanelVisibility()" :annotation-variant-details-visible="editAnnotationVariantDetailsVisible"
        @breadcrumb-navigation="breadcrumbNavigation"
        :current-variant="currentVariant"
        :oncotree="oncotree"
        @reload-values="reloadLookupValues">
        <v-slide-y-transition slot="variantDetails">
            <v-flex xs12 md12 lg12 xl11 mb-2 v-show="editAnnotationVariantDetailsVisible">
            <variant-details :no-edit="true" :variant-data-tables="variantDataTables" :link-table="linkTable" :type="currentVariantType" 
                :width-class="getWidthClassForVariantDetails()" :current-variant="currentVariant" @hide-panel="handlePanelVisibility(false)"
                @show-panel="handlePanelVisibility(true)" @toggle-panel="handlePanelVisibility()" @revert-variant="revertVariant"
                @save-all-variants="saveAllVariants" :color="colors.variantDetails"
                :variant-type="currentVariantType" cnv-plot-id="cnvPlotEditUnused"
                :loading-variant="loadingVariant"
                @open-lookup-link="openLookupLink"
                @toggle-lookup-tool="toggleLookupTool"
                @toggle-lookup-tool-variant="toggleLookupToolVariant"
                @toggle-lookup-tool-gene="toggleLookupToolGene">
            </variant-details>
            </v-flex>
        </v-slide-y-transition>
        </edit-annotations>

    <edit-annotations type="cnv" @saving-annotations="commitAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs"
        :color="colors.editAnnotation" ref="cnvAnnotationDialog" :title="formatChrom(currentVariant.chrom)  + ' -- ' + caseName + ' --'" 
        :caseIcon="caseTypeIcon" :caseType="caseType" @toggle-panel="handlePanelVisibility()" :outsideACase="false"
        :breadcrumbs="breadcrumbs" @breadcrumb-navigation="breadcrumbNavigation" :annotation-categories-c-n-v="annotationCategoriesCNV" :annotation-breadth="annotationBreadth"
        :annotation-phases="annotationPhases"
        :userAnnotations="userAnnotations"
        @reload-values="reloadLookupValues"
        :oncotree="oncotree"
        :annotation-tiers="variantTiers" :annotation-classifications="annotationClassifications"
        :current-variant="currentVariant" :annotation-variant-details-visible="editAnnotationVariantDetailsVisible">
        <v-slide-y-transition slot="variantDetails">
            <v-flex xs12 md12 lg12 xl11 mb-2 v-show="editAnnotationVariantDetailsVisible">
                <variant-details :no-edit="true" :variant-data-tables="variantDataTables" :link-table="linkTable" :width-class="getWidthClassForVariantDetails()" :type="currentVariantType"
                    :current-variant="currentVariant" @hide-panel="handlePanelVisibility(false)" @show-panel="handlePanelVisibility(true)"
                    @toggle-panel="handlePanelVisibility()" @revert-variant="revertVariant" :color="colors.editAnnotation"
                    ref="cnvVariantDetailsPanel" cnv-plot-id="cnvPlotEdit" :cnv-chrom-list="cnvChromList"
                    :variant-type="currentVariantType"
                    :loading-variant="loadingVariant"
                    @open-lookup-link="openLookupLink"
                    @toggle-lookup-tool="toggleLookupTool"
                    >

                </variant-details>
            </v-flex>
        </v-slide-y-transition>

    </edit-annotations>

    <edit-annotations type="translocation" @saving-annotations="commitAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs"
        :color="colors.editAnnotation" ref="translocationAnnotationDialog" :title="currentVariant.fusionName  + ' -- ' + caseName + ' --'"
        :caseIcon="caseTypeIcon" :caseType="caseType" :outsideACase="false"
        :breadcrumbs="breadcrumbs" @breadcrumb-navigation="breadcrumbNavigation" :annotation-categories="annotationCategories"
        :annotation-tiers="variantTiers" :annotation-classifications="annotationClassifications" :annotation-variant-details-visible="editAnnotationVariantDetailsVisible"
        :annotation-phases="annotationPhases"
        :userAnnotations="userAnnotations"
        :current-variant="currentVariant" @toggle-panel="handlePanelVisibility()"
        :oncotree="oncotree"
        @reload-values="reloadLookupValues">
        <v-slide-y-transition slot="variantDetails">
            <v-flex xs12 md12 lg12 xl11 mb-2 v-show="editAnnotationVariantDetailsVisible">
                    <variant-details :no-edit="true" :variant-data-tables="variantDataTables" :link-table="linkTable" :width-class="getWidthClassForVariantDetails()" :type="currentVariantType"
                        :current-variant="currentVariant" @hide-panel="handlePanelVisibility(false)" @show-panel="handlePanelVisibility(true)"
                        @toggle-panel="handlePanelVisibility()" @revert-variant="revertVariant" :color="colors.editAnnotation"
                        cnv-plot-id="cnvPlotEditUnusedFTL"
                        :variant-type="currentVariantType"
                        :loading-variant="loadingVariant"
                        @open-lookup-link="openLookupLink"
                        @toggle-lookup-tool="toggleLookupTool">
                    </variant-details>
            </v-flex>
            </v-slide-y-transition>
        </edit-annotations>

        <edit-annotations type="virus" @saving-annotations="commitAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs"
        :color="colors.editAnnotation" ref="virusAnnotationDialog" :title="currentVariant.VirusName"
        :caseIcon="caseTypeIcon" :caseType="caseType" :outsideACase="false"
        :breadcrumbs="breadcrumbs" @breadcrumb-navigation="breadcrumbNavigation" :annotation-categories="annotationCategories"
        :annotation-tiers="variantTiers" :annotation-classifications="annotationClassifications" :annotation-variant-details-visible="editAnnotationVariantDetailsVisible"
        :annotation-phases="annotationPhases"
        :userAnnotations="userAnnotations"
        :current-variant="currentVariant" @toggle-panel="handlePanelVisibility()">
        <v-slide-y-transition slot="variantDetails">
            <v-flex xs12 md12 lg12 xl11 mb-2 v-show="editAnnotationVariantDetailsVisible">
                    <variant-details :no-edit="true" :variant-data-tables="variantDataTables" :link-table="linkTable" :width-class="getWidthClassForVariantDetails()" :type="currentVariantType"
                        :current-variant="currentVariant" @hide-panel="handlePanelVisibility(false)" @show-panel="handlePanelVisibility(true)"
                        @toggle-panel="handlePanelVisibility()" @revert-variant="revertVariant" :color="colors.editAnnotation"
                        cnv-plot-id="cnvPlotEditUnusedVIR"
                        :variant-type="currentVariantType"
                        :loading-variant="loadingVariant">

                    </variant-details>
            </v-flex>
            </v-slide-y-transition>
        </edit-annotations>

        <v-dialog v-model="variantDetailsVisible" scrollable fullscreen persistent hide-overlay transition="dialog-bottom-transition" content-class="variantDetailsDialog">
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
                                        <v-menu offset-y offset-x close-delay="500" open-on-hover>
                                            <span slot="activator">
                                                <v-icon class="pl-2 pr-4">keyboard_arrow_right</v-icon>Show / Hide
                                                <!-- This is a hack to extend the menu active area because the title is much shorter than other items -->
                                                <span v-for="i in 30" :key="i">&nbsp;</span>
                                            </span>
                                            <v-list>
                                                <v-list-tile :class="!annotationVariantDetailsVisible ? 'grey--text' : ''" avatar @click="annotationVariantDetailsVisible = !annotationVariantDetailsVisible">
                                                    <v-list-tile-avatar>
                                                        <v-icon>zoom_in</v-icon>
                                                    </v-list-tile-avatar>
                                                    <v-list-tile-content>
                                                        <v-list-tile-title>Variant Details</v-list-tile-title>
                                                    </v-list-tile-content>
                                                </v-list-tile>
    
                                                <v-list-tile v-if="isSNP()" :class="!currentVariantHasRelatedVariants ? 'grey--text' : ''" avatar :disabled="!currentVariantHasRelatedVariants" @click="toggleRelatedVariants()">
                                                    <v-list-tile-avatar>
                                                        <v-icon>link</v-icon>
                                                    </v-list-tile-avatar>
                                                    <v-list-tile-content>
                                                        <v-list-tile-title>Related Variants</v-list-tile-title>
                                                    </v-list-tile-content>
                                                </v-list-tile>
    
                                                <v-list-tile v-if="isSNP()" :class="!currentVariantHasRelatedCNV ? 'grey--text' : ''" avatar :disabled="!currentVariantHasRelatedCNV" @click="toggleRelatedCNV()">
                                                <v-list-tile-avatar>
                                                    <v-icon>link</v-icon>
                                                </v-list-tile-avatar>
                                                <v-list-tile-content>
                                                    <v-list-tile-title>Related CNV</v-list-tile-title>
                                                </v-list-tile-content>
                                            </v-list-tile>
    
                                                <v-list-tile v-if="isSNP()" :class="!annotationVariantCanonicalVisible ? 'grey--text' : ''" avatar @click="annotationVariantCanonicalVisible = !annotationVariantCanonicalVisible">
                                                    <v-list-tile-avatar>
                                                        <v-icon>mdi-table-search</v-icon>
                                                    </v-list-tile-avatar>
                                                    <v-list-tile-content>
                                                        <v-list-tile-title> Canonical VCF Annotations</v-list-tile-title>
                                                    </v-list-tile-content>
                                                </v-list-tile>
    
                                                <v-list-tile v-if="isSNP()" :class="!annotationVariantOtherVisible ? 'grey--text' : ''" avatar @click="annotationVariantOtherVisible = !annotationVariantOtherVisible">
                                                    <v-list-tile-avatar>
                                                        <v-icon>mdi-table-search</v-icon>
                                                    </v-list-tile-avatar>
                                                    <v-list-tile-content>
                                                        <v-list-tile-title>Other VCF Annotations</v-list-tile-title>
                                                    </v-list-tile-content>
                                                </v-list-tile>
    
                                                <!--
                                                <v-list-tile v-if="isSNP() || isCNV()" :class="!mdaAnnotationsVisible ? 'grey--text' : ''" avatar @click="mdaAnnotationsVisible = !mdaAnnotationsVisible" :disabled="!mdaAnnotationsExists()">
                                                    <v-list-tile-avatar>
                                                        <v-icon v-if="mdaAnnotationsExists()">mdi-message-bulleted</v-icon>
                                                        <v-icon v-else>mdi-message-bulleted-off</v-icon>
                                                    </v-list-tile-avatar>
                                                    <v-list-tile-content>
                                                        <v-list-tile-title v-if="mdaAnnotationsExists()">MDA Annotations</v-list-tile-title>
                                                        <v-list-tile-title v-else>No MDA Annotations</v-list-tile-title>
                                                    </v-list-tile-content>
                                                </v-list-tile>
                                                -->
    
                                                <v-list-tile avatar :class="!utswAnnotationsVisible ? 'grey--text' : ''" avatar @click="utswAnnotationsVisible = !utswAnnotationsVisible" :disabled="!utswAnnotationsExists()">
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
    
                            <v-list-tile class="list-menu" v-if="isSNP()">
                                <v-list-tile-content>
                                    <v-list-tile-title>
                                        <v-menu offset-y offset-x close-delay="500" open-on-hover>
                                            <span slot="activator">
                                                <v-icon class="pl-2 pr-4">keyboard_arrow_right</v-icon>IGV
                                                <!-- This is a hack to extend the menu active area because the title is much shorter than other items -->
                                                <span v-for="i in 30" :key="i">&nbsp;</span>
                                            </span>
                                            <v-list>
                                                <v-list-tile  avatar @click="openBamViewerLinkWeb()">
                                                    <v-list-tile-avatar>
                                                        <v-icon>mdi-web</v-icon>
                                                    </v-list-tile-avatar>
                                                    <v-list-tile-content class="mb-2">
                                                        <v-list-tile-title>Open IGV (web)</v-list-tile-title>
                                                    </v-list-tile-content>
                                                </v-list-tile>
    
                                                <v-list-tile avatar @click="downloadIGVFile('jnlp')">
                                                <v-list-tile-avatar>
                                                <v-icon>mdi-desktop-mac-dashboard</v-icon>
                                                </v-list-tile-avatar>
                                                <v-list-tile-content  class="mb-2">
                                                    <v-list-tile-title>Open IGV (desktop)</v-list-tile-title>
                                                </v-list-tile-content>
                                                </v-list-tile>
    
                                                <v-list-tile  avatar @click="downloadIGVFile('session')">
                                                <v-list-tile-avatar>
                                                    <v-icon>mdi-file-code</v-icon>
                                                </v-list-tile-avatar>
                                                <v-list-tile-content  class="mb-2">
                                                    <v-list-tile-title>Download IGV Session</v-list-tile-title>
                                                </v-list-tile-content>
                                                </v-list-tile>
    
                                                <v-list-tile avatar @click="getIGVSessionLink()">
                                                <v-list-tile-avatar>
                                                    <v-icon>mdi-link-variant</v-icon>
                                                </v-list-tile-avatar>
                                                <v-list-tile-content  class="mb-2">
                                                    <v-list-tile-title>Show IGV Session Link</v-list-tile-title>
                                                </v-list-tile-content>
                                                </v-list-tile>
    
                                    </v-list>
                                    </v-menu>
                                </v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>
    
                            <v-list-tile avatar @click="toggleLookupTool()">
                            <v-list-tile-avatar>
                                <v-icon>mdi-dna</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Open Lookup Tool (Beta)</v-list-tile-title>
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
    
                            <v-list-tile avatar @click="selectVariantForReport()" v-if="!currentlySelected" :disabled="!canProceed('canSelect')">
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
              <span v-else-if="isVirus()">Virus</span>
              <span v-if="!isVirus()">Variant:</span>
                  <v-tooltip bottom v-if="variantNameIsTooLong() && isSNP()">
                  <span slot="activator" v-text="createVariantName()"></span>
                  <span>{{ currentVariant.geneName }} {{ currentVariant.notation }}</span>
                  </v-tooltip>  
                  <span v-if="!variantNameIsTooLong() && isSNP()">{{ currentVariant.geneName }} {{ currentVariant.notation }}</span>
                  <span v-else-if="isCNV()" v-text="formatChrom(currentVariant.chrom)"></span>
                  <span v-else-if="isTranslocation()">{{ currentVariant.fusionName }}</span>
                  <span v-else-if="isVirus()">{{ currentVariant.VirusName }}</span>
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
                    <!--
                    <v-tooltip bottom v-if="isSNP || isCNV()">
                        <v-btn :disabled="!mdaAnnotationsExists()" icon flat :color="(mdaAnnotationsVisible && mdaAnnotationsExists()) ? 'amber accent-2' : ''"
                            @click="mdaAnnotationsVisible = !mdaAnnotationsVisible" slot="activator">
                            <v-icon v-if="mdaAnnotationsExists()">mdi-message-bulleted</v-icon>
                            <v-icon v-else>mdi-message-bulleted-off</v-icon>
                        </v-btn>
                        <span v-if="mdaAnnotationsExists()">Show/Hide MDA Annotations</span>
                        <span v-else>No MDA Annotations</span>
                    </v-tooltip>
                    -->
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
                        <v-btn  dark icon flat slot="activator" @click="downloadIGVFile('jnlp')">
                            <v-icon>mdi-desktop-mac-dashboard</v-icon>
                        </v-btn>
                        <span>Open IGV (desktop)</span>
                    </v-tooltip>
                    <br/>
                    <v-tooltip bottom v-if="isSNP()">
                        <v-btn dark icon flat slot="activator" @click="downloadIGVFile('session')">
                            <v-icon>mdi-file-code</v-icon>
                        </v-btn>
                        <span>Download IGV Session</span>
                    </v-tooltip>
                    <br/>
                    <v-tooltip bottom v-if="isSNP()">
                    <v-btn dark icon flat slot="activator" @click="getIGVSessionLink()">
                        <v-icon>mdi-link-variant</v-icon>
                    </v-btn>
                    <span>Show IGV Session Link</span>
                    </v-tooltip>
                    </v-card>
                    </v-menu>
    
                    <v-badge color="red" right bottom overlap v-model="isSaveNeededBadgeVisible()" class="mini-badge">
                    <v-icon slot="badge"></v-icon>
                    <v-tooltip bottom offset-overflow nudge-left="100px" min-width="200px">
                    <v-btn flat icon @click="handleSaveAll()" slot="activator" :loading="isSaveLoading" :disabled="!isSaveNeededBadgeVisible()">
                        <v-icon>save</v-icon>
                    </v-btn>
                    <span v-html="saveTooltip"></span>
                </v-tooltip>
                </v-badge>
    
                    <v-tooltip bottom>
                        <v-btn icon @click="closeVariantDetails()" slot="activator">
                            <v-icon>close</v-icon>
                        </v-btn>
                        <span>Close Variant</span>
                        <!--
                        <span v-if="!isSaveNeededBadgeVisible()">Save & Close Variant</span>
                        <span v-else>Save & Close Variant</span>
                        -->
                    </v-tooltip>
    
                </v-toolbar>
                <v-card-text :style="getDialogMaxHeight(120)" class="pl-2 pr-2">
                <v-layout row wrap>
                <v-flex :class="isLookupVisible() ? 'xs8' : 'xs12'">
    
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
                                        @save-all-variants="saveAllVariants" :color="colors.variantDetails" ref="variantDetailsPanel" @variant-details-changed=""
                                        :variant-type="currentVariantType" cnv-plot-id="cnvPlotDetails" :cnv-chrom-list="cnvChromList"
                                        :loading-variant="loadingVariant"
                                        @open-lookup-link="openLookupLink"
                                        @toggle-lookup-tool="toggleLookupTool"
                                        @toggle-lookup-tool-variant="toggleLookupToolVariant"
                                        @toggle-lookup-tool-gene="toggleLookupToolGene"
                                        >
                                    </variant-details>
    
                                </v-flex>
                            </v-slide-y-transition>
                            <v-slide-y-transition>
                                <v-flex :class="isLookupVisible() ? ['xs12','sm12','md12','lg9','xl7'] : ['xs12','sm12','md9','lg7','xl5']" v-show="isRelatedVariantsVisible()">
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
                            <!--
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
                            -->
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
                                                                        <v-select v-if="isSNP() || isTranslocation() || isVirus()" clearable :value="searchAnnotationCategory" :items="annotationCategories" v-model="searchAnnotationCategory"
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
                                                                        <v-select v-else-if="isVirus()" clearable :value="searchAnnotationScope" :items="scopesVirus" v-model="searchAnnotationScope"
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
                                                        :can-hide="canProceed('canHide') && !readonly && !loadingVariant"
                                                        @annotation-selection-changed="handleAnnotationSelectionChanged()"
                                                        @copy-annotation="copyAnnotation"
                                                        @hide-annotation="hideAnnotation"
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
                    </v-flex>
                    <v-flex v-show="isLookupVisible()" class="xs4">
                        <!-- lookup tools-->
                    <lookup-panel ref="lookupTool" :standalone="false"
                    :original-variant="currentVariant.notation"
                    :original-chr="currentVariant.chrom"
                    :original-pos="currentVariant.pos"
                    :original-pos-old-build="getHG19Pos()"
                    :oncotree-items="oncotree"
                    @reload-values="reloadLookupValues"
                    ></lookup-panel>
                    </v-flex>
                </v-layout>
                </v-card-text>
                <v-card-actions class="card-actions-bottom">
                    <v-tooltip top class="pr-2">
                        <v-btn color="primary" @click="startUserAnnotations()" slot="activator" :disabled="!canProceed('canAnnotate') || readonly">Add/Edit
                            <v-icon right dark>create</v-icon>
                        </v-btn>
                        <span>Create/Edit Your Annotations</span>
                    </v-tooltip>
                    <v-btn class="mr-2" v-if="!currentlySelected" :disabled="!canProceed('canSelect') || readonly" color="success"
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
                    <v-btn color="success" @click="handleSaveAll()" slot="activator" :loading="isSaveLoading" :disabled="!isSaveNeededBadgeVisible()">
                        Save Work
                    <v-icon right dark>save</v-icon>
                    </v-btn>
                    <span>Save Current Work</span>
                </v-tooltip class="pr-2">
                    <v-btn color="error" @click="closeVariantDetails()">
                    <span>Close</span>
                    <!--
                    <span v-if="!isSaveNeededBadgeVisible()">Close</span>
                    <span v-else>Save & Close</span>
                    -->
                        <v-icon right dark>cancel</v-icon>
                    </v-btn>
                </v-card-actions>
            </v-card>

        
        </v-dialog>
    </div>
`,
    data() {
        return {
            annotationVariantDetailsVisible: true,
            annotationVariantRelatedVisible: true,
            annotationCNVRelatedVisible: true,
            annotationVariantCanonicalVisible: true,
            annotationVariantOtherVisible: true,
            utswAnnotations: [],
            utswAnnotationsFormatted: [],
            mdaAnnotations: "",
            mdaAnnotationsFormatted: [],
            mdaAnnotationsVisible: true,
            utswAnnotationsVisible: true,
            currentVariantHasRelatedVariants: false,
            currentVariantHasRelatedCNV: false,
            currentVariant: {},
            variantDataTables: [],
            linkTable: [],
            loadingVariant: false,
            searchAnnotationsVisible: false,
            searchAnnotations: "",
            searchAnnotationsVisible: false,
            searchAnnotationClassification: [],
            searchAnnotationCategory: [],
            searchAnnotationBreadth: [],
            searchAnnotationTier: [],
            searchAnnotationScope: [],
            currentVariantFlags: [],
            highlightLatestAnnotation: false,
            // annotationIdsForReporting: [], //save the state of the selection in case the user close/open another page
            canCopyAnnotation: true,
            currentItem: {},
            userAnnotations: [],
            editAnnotationVariantDetailsVisible: true,
            userId: null,
            savingAnnotationSelection: false,
            isFirstVariant: true,
            isLastVariant: true,
            currentVariantType: "snp",
            patientDetailsOncoTreeDiagnosis: {},
            currentlySelected: false,
            currentListOfCNVVisibleGenes: [],
            addCNVDialogVisible: false,
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
                case "canHide": return permissions.canHide;
                default: return false;
            }
        },
        mdaAnnotationsExists() {
            return this.mdaAnnotations != '';
        },
        utswAnnotationsExists() {
            return this.utswAnnotationsFormatted.length > 0;
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
        isVirus() {
            return this.currentVariantType == "virus";
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
        resetCNVChart() {
            if (this.$refs.variantDetailsPanel) {
                this.$refs.variantDetailsPanel.resetCNVChart();
            }
            
        },
        downloadIGVFile(igvType) {
            this.$emit("download-igv-file", igvType, this.getIGVRange());
        },
        getIGVSessionLink() {
            this.$emit("download-igv-file", "sessionLink", this.getIGVRange());
        },
        getIGVRange() {
            let igvRange = this.currentVariant.chrom + ":";
            igvRange += this.currentVariant.pos - 100;
            igvRange += "-";
            igvRange += this.currentVariant.pos + 99;
            return igvRange;
        },
        openBamViewerLinkWeb() {
            this.$refs.bamViewerLink.$el.click();
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
        isSaveNeededBadgeVisible() {
            return this.isSaveNeededOverall;
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
        getDialogMaxHeight(offset) {
            getDialogMaxHeight(offset);
        },
        annotationAllHidden() {
            return !this.annotationVariantDetailsVisible
                && !this.annotationVariantCanonicalVisible
                && !(this.mdaAnnotationsVisible && this.mdaAnnotationsExists())
                && !this.annotationVariantOtherVisible
                && !(this.utswAnnotationsVisible && this.utswAnnotationsExists());
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
            if (this.isVirus()) {
                return 'xs12';
            }
        },
        revertVariant(keepSaveState) {
            if (this.isSNP()) {
                this.getVariantDetails(this.$route.query.variantId, this.isFirstVariant, this.isLastVariant);
            }
            else if (this.isCNV()) {
                this.getCNVDetails(this.$route.query.variantId, this.isFirstVariant, this.isLastVariant);
            }
            else if (this.isTranslocation()) {
                this.getTranslocationDetails(this.$route.query.variantId, this.isFirstVariant, this.isLastVariant);
            }
            else if (this.isVirus()) {
                this.getVirusDetails(this.$route.query.variantId, this.isFirstVariant, this.isLastVariant);
            }
        },
        syncCurrentVariant() {
            var lightVariant = this.$store.getters["variantStore/getLightVariant"](this.currentVariant._id["$oid"]);
            if (lightVariant) {
                if (this.isSNP()) {
                    this.currentVariant.tier = lightVariant.tier;
                    this.currentVariant.notation = lightVariant.notation;
                    this.currentVariant.geneName = lightVariant.geneName;
                }
                else if (this.isCNV()) {
                    this.currentVariant.tier = lightVariant.tier;
                    this.currentVariant.aberrationType = lightVariant.aberrationType;
                }
                else if (this.isTranslocation()) {
                    this.currentVariant.tier = lightVariant.tier;
                    this.currentVariant.fusionName = lightVariant.fusionName;
                    this.currentVariant.leftGene = lightVariant.leftGene;
                    this.currentVariant.rightGene = lightVariant.rightGene;
                }
                else if (this.isVirus()) {
                }
            }
        },
        getVariantDetails(variantId, isFirstVariant, isLastVariant) {
            return new Promise((resolve, reject) => {
                this.loadingVariant = true;
                this.currentVariantFlags = [];
                this.isFirstVariant = isFirstVariant;
                this.isLastVariant = isLastVariant;
                this.currentVariantType = "snp";

                //put panels in loading state
                this.$refs.relatedVariantAnnotation.startLoading();
                this.$refs.relatedCNVAnnotation.startLoading();
                this.$refs.canonicalVariantAnnotation.startLoading();
                this.$refs.otherVariantAnnotations.startLoading();

            axios.get(
                webAppRoot + "/getVariantDetails",
                {
                    params: {
                        variantId: variantId,
                        caseId: this.$route.params.id

                    }
                }).then(response => {
                    if (response.data.isAllowed) {
                        this.userId = response.data.userId;
                        this.currentVariant = response.data.variantDetails;
                        this.syncCurrentVariant();
                        this.currentItem = response.data.item;
                        let value = response.data.patientDetailsOncoTreeDiagnosis ? response.data.patientDetailsOncoTreeDiagnosis.value : "";
                        this.patientDetailsOncoTreeDiagnosis = { text: value, label: "" };
                        this.addCustomWarningFlagsForItem(this.currentItem);

                        this.variantDataTables = [];
                        var infoTable = {
                            name: "infoTable",
                            items: [
                                {
                                    label: "Flags",
                                    value: this.currentItem.iconFlags.iconFlags,
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
                                    value: this.createOldBuildList(),
                                },
                                {
                                    label: "Gene",
                                    type: "button",
                                    fieldName: "gene",
                                    linkIcon: "mdi-dna",
                                    handler: "toggle-lookup-tool-gene",
                                    value: this.currentVariant.geneName,
                                    tooltip: "Open Lookup Tool (Gene)"
                                },
                                {
                                    label: "Notation",
                                    type: "notation",
                                    fieldName: "notation",
                                    linkIcon: "mdi-dna",
                                    handler: "toggle-lookup-tool-variant",
                                    value: this.currentVariant.notation,
                                    tooltip: "Open Lookup Tool (Variant)"
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
                                    tooltip: "Open in gnomAD (data may differ from version on gnomAD website)"
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
                        this.utswAnnotations = this.currentVariant.referenceVariant.utswAnnotations;
                        // this.reloadPreviousSelectedState();
                        this.mdaAnnotations = this.currentVariant.mdaAnnotation ? this.currentVariant.mdaAnnotation : "";
                        this.formatAnnotations();
                        this.loadingVariant = false;

                        //finally, open edit annotation //TODO should be handle in load params
                        // this.handleEditAnnotationOpening();
                        resolve({success: true});
                    } else {
                        reject(response);
                        this.loadingVariant = false;
                        this.handleDialogs(response.data, this.getVariantDetails.bind(null,
                            variantId));
                    }
                }).catch(error => {
                    this.loadingVariant = false;
                    this.handleAxiosError(error);
                    reject(error);
                });
            });
        },
        getCNVDetails(variantId, isFirstVariant, isLastVariant) {
            //TODO
            return new Promise((resolve, reject) => {
                this.loadingVariant = true;
                this.currentVariantFlags = [];
                this.isFirstVariant = isFirstVariant;
                this.isLastVariant = isLastVariant;
                this.currentVariantType = "cnv";

            //put panels in loading state

            axios.get(
                webAppRoot + "/getCNVDetails",
                {
                    params: {
                        variantId: variantId,
                        caseId: this.$route.params.id
                    }
                }).then(response => {
                    if (response.data.isAllowed) {
                        this.userId = response.data.userId;
                        this.currentVariant = response.data.variantDetails;
                        this.syncCurrentVariant();
                        this.currentItem = response.data.item;
                        this.patientDetailsOncoTreeDiagnosis = { text: response.data.patientDetailsOncoTreeDiagnosis.value, label: response.data.patientDetailsOncoTreeDiagnosis.text };
                        this.addCustomWarningFlagsForItem(this.currentItem);
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
                        this.utswAnnotations = this.currentVariant.referenceCnv.utswAnnotations;
                        this.mdaAnnotations = this.currentVariant.mdaAnnotation ? this.currentVariant.mdaAnnotation : "";
                        // this.reloadPreviousSelectedState();
                        this.formatCNVAnnotations();
                        this.loadingVariant = false;
                        // this.updateVariantDetails();
                        //finally, open edit annotation
                        // this.handleEditAnnotationOpening();

                        resolve({success: true});
                    } else {
                        this.loadingVariant = false;
                        reject(response);
                        this.handleDialogs(response.data, this.getCNVDetails.bind(null,
                            variantId));
                    }
                }).catch(error => {
                    this.loadingVariant = false;
                    this.handleAxiosError(error);
                    reject(error);
                });
            });
        },
        getCNVDetailsNoVariant() {
            return new Promise((resolve, reject) => {
                this.currentVariantType = "cnv";
                this.currentVariantFlags = [];
                this.loadingVariant = true;

                this.isFirstVariant = true;
                this.isLastVariant = true;

                this.currentVariant = {};
                this.variantDataTables = [];
                this.linkTable = [];

                this.userAnnotations = [];
                this.utswAnnotations = [];
                this.utswAnnotationsFormatted = [];
                this.mdaAnnotations = "";
                this.mdaAnnotationsFormatted = [];
                this.loadingVariant = false;
                this.urlQuery.variantId = "notreal";
                this.urlQuery.variantType = this.currentVariantType;
                resolve({success: true});
            });
        },
        buildAberrationTypeHelp() {
            var message = "amplification: High level copy number gain</br>"
                + "gain: Low level copy number gain</br>"
                + "homozygous loss: Two copy loss</br>"
                + "hemizygous loss: Single Copy Loss with remaining allele WT</br>";
            return message;
        },
        getTranslocationDetails(variantId, isFirstVariant, isLastVariant) {
            return new Promise((resolve, reject) => {
            this.currentVariantType = "translocation";
            this.currentVariantFlags = [];
            this.loadingVariantDetails = true;
            this.loadingVariant = true;

            this.isFirstVariant = isFirstVariant;
            this.isLastVariant = isLastVariant;


            axios.get(
                webAppRoot + "/getTranslocationDetails",
                {
                    params: {
                        variantId: variantId,
                        caseId: this.$route.params.id
                    }
                }).then(response => {
                    if (response.data.isAllowed) {
                        this.userId = response.data.userId;
                        this.currentVariant = response.data.variantDetails;
                        this.syncCurrentVariant();
                        this.currentItem = response.data.item;
                        this.addCustomWarningFlagsForItem(this.currentItem);
                        this.patientDetailsOncoTreeDiagnosis = { text: response.data.patientDetailsOncoTreeDiagnosis.value, label: response.data.patientDetailsOncoTreeDiagnosis.text };
                        this.variantDataTables = [];
                        var infoTable = {
                            name: "infoTable",
                            items: [
                            {
                                label: "Flags",
                                value: this.currentItem.iconFlags.iconFlags,
                                type: "flag"
                            },
                            {
                                label: "Fusion Name",
                                type: "textfield",
                                fieldName: "fusionName",
                                value: this.currentVariant.fusionName,
                                tooltip: "Edit the Fusion Name"
                            },
                            {
                                label: "QC Tags", value: this.currentVariant.filtersFormatted
                            },
                            {
                                label: "Left Gene",
                                type: "textfield",
                                fieldName: "leftGene",
                                value: this.currentVariant.leftGene,
                                tooltip: "Edit Left Gene",
                                needsValidation: true,
                                isValid: true,
                                tooltipInvalid: "Not a valid gene"
                            },
                            {
                                label: "Right Gene",
                                type: "textfield",
                                fieldName: "rightGene",
                                value: this.currentVariant.rightGene,
                                tooltip: "Edit Right Gene",
                                needsValidation: true,
                                isValid: true,
                                tooltipInvalid: "Not a valid gene"
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
                            // {
                            //     label: "Open Lookup Portal (Fusion)",
                            //     type: "link",
                            //     linkIcon: "mdi-dna",
                            //     url: this.createOncoKBGeniePortalFusion(),
                            //     value: "",
                            //     tooltip: "Open Lookup Portal (Fusion) in new tab"
                            // },
                            {
                                label: "Open Lookup Tool (Fusion)",
                                type: "button",
                                linkIcon: "mdi-dna",
                                handler: "toggle-lookup-tool",
                                value: "",
                                tooltip: "Open Lookup Tool Panel (Fusion)"
                            }]
                        };
                        this.variantDataTables.push(infoTable3);

                        this.linkTable = [];

                        this.userAnnotations = this.currentVariant.referenceTranslocation.utswAnnotations.filter(a => a.userId == this.userId);
                        this.utswAnnotations = this.currentVariant.referenceTranslocation.utswAnnotations;
                        this.mdaAnnotations = "";
                        // this.reloadPreviousSelectedState();
                        this.formatTranslocationAnnotations();
                        this.loadingVariant = false;
                        resolve({success: true});

                    } else {
                        this.loadingVariant = false;
                        reject(response)
                        this.handleDialogs(response.data, this.getTranslocationDetails.bind(null,
                            variantId));
                    }
                }).catch(error => {
                    this.loadingVariant = false;
                    this.handleAxiosError(error);
                });
            });
        },
        getVirusDetails(variantId, isFirstVariant, isLastVariant) {
            return new Promise((resolve, reject) => {
            this.currentVariantType = "virus";
            this.currentVariantFlags = [];
            this.loadingVariantDetails = true;
            this.loadingVariant = true;

            this.isFirstVariant = isFirstVariant;
            this.isLastVariant = isLastVariant;


            axios.get(
                webAppRoot + "/getVirusDetails",
                {
                    params: {
                        variantId: variantId,
                        caseId: this.$route.params.id
                    }
                }).then(response => {
                    if (response.data.isAllowed) {
                        this.userId = response.data.userId;
                        this.currentVariant = response.data.variantDetails;
                        this.syncCurrentVariant();
                        this.currentItem = response.data.item;
                        this.addCustomWarningFlagsForItem(this.currentItem);
                        this.patientDetailsOncoTreeDiagnosis = { text: response.data.patientDetailsOncoTreeDiagnosis.value, label: response.data.patientDetailsOncoTreeDiagnosis.text };
                        this.variantDataTables = [];
                        var infoTable = {
                            name: "infoTable",
                            items: [
                                {
                                    label: "Virus Name", value: this.currentVariant.VirusName
                                },
                                {
                                    label: "Virus Description", value: this.currentVariant.VirusDescription
                                },
                               
                           
                            ]
                        };
                        this.variantDataTables.push(infoTable);

                        var infoTable2 = {
                            name: "infoTable2",
                            items: [
                                {
                                    label: "Sample ID", value: this.currentVariant.SampleId
                                },
                                {
                                    label: "Accession ID", value: this.currentVariant.VirusAcc
                                },
                                {
                                    label: "Read Count", value: this.currentVariant.VirusReadCount
                                },
                                {
                                    label: "Nb. Cases Seen", value: this.currentVariant.numCasesSeen
                                },
                           ]
                        };
                        this.variantDataTables.push(infoTable2);

                        var infoTable3 = {
                            name: "infoTable3",
                            items: [
                               ]
                        };
                        this.variantDataTables.push(infoTable3);

                        this.linkTable = [];

                        this.userAnnotations = this.currentVariant.referenceVirus.utswAnnotations.filter(a => a.userId == this.userId);
                        this.utswAnnotations = this.currentVariant.referenceVirus.utswAnnotations;
                        this.mdaAnnotations = "";
                        // this.reloadPreviousSelectedState();
                        this.formatVirusAnnotations();
                        this.loadingVariant = false;
                        resolve({success: true});

                    } else {
                        this.loadingVariant = false;
                        reject(response)
                        this.handleDialogs(response.data, this.getVirusDetails.bind(null,
                            variantId));
                    }
                }).catch(error => {
                    this.loadingVariant = false;
                    this.handleAxiosError(error);
                });
            });
        },
        createOncoKBGeniePortalFusion() {
            return oncoKBGeniePortalUrl + "Fusion/?gene1=" + this.currentVariant.leftGene
            + "&gene2=" + this.currentVariant.rightGene + "&Oncotree=" + this.patientDetailsOncoTreeDiagnosis.text;
        },
        // reloadPreviousSelectedState() {
        //     for (var i = 0; i < this.annotationIdsForReporting.length; i++) {
        //         for (var j = 0; j < this.utswAnnotations.length; j++) {
        //             if (this.annotationIdsForReporting[i].$oid == this.utswAnnotations[j]._id.$oid) {
        //                 this.utswAnnotations[j].isSelected = true; //only set if true, do not unset if false
        //             }
        //         }
        //     }
        // },
        loadPrevVariant() {
            this.$emit("load-prev-variant");
        },
        loadNextVariant() {
            this.$emit("load-next-variant");
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
        formatVirusAnnotations() {
            this.highlightLatestAnnotation = true;
            this.utswAnnotationsFormatted = this.formatLocalVirusAnnotations(this.utswAnnotations, true);
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
        },
        saveAllVariants(skipSnackBar) {
            if (!this.canProceed('canAnnotate')) {
                return;
            }
            return new Promise((resolve, reject) => {
                this.$store.commit("variantStore/updateUnsavedVariantValues");
                axios({
                    method: 'post',
                    url: webAppRoot + "/saveAllVariants",
                    params: {
                        skipSnackBar: skipSnackBar
                    },
                    data: {
                        // filters: this.$refs.advancedFilter.filters,
                        variants: this.$store.getters["variantStore/getVariantsToSave"]
                    }
                }).then(response => {
                    this.waitingForAjaxCount--;
                    if (response.data.isAllowed) {
                        if (!response.data.skipSnackBar) {
                            this.revertVariant();
                            this.showSnackBarMessage("Variants Saved");
                        }
                        this.$store.commit("variantStore/clearAfterSaving");
                        resolve({success: true});
                       
                    }
                    else {
                        reject(response);
                        this.handleDialogs(response.data, this.saveAllVariants.bind(null, response.data.skipSnackBar));
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                    reject(error);
                });
            });
        },
        showSnackBarMessage(message) {
            this.$emit("show-snackbar", message);
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
            this.toggleLookupTool(geneName);
            // var link = this.createOncoKBGeniePortalCNV(geneName);
            // window.open(link, "_blank");
        },
        startUserAnnotations() {
            if (!this.canProceed('canAnnotate') || this.readonly) {
                return;
            }
            if (this.isSNP() && this.$refs.annotationDialog) {
                this.$refs.annotationDialog.startUserAnnotations();
            }
            else if (this.isCNV() && this.$refs.cnvAnnotationDialog) {
                this.$refs.cnvAnnotationDialog.cnvGeneItems = this.currentItem.genes.split(" ");
                this.$refs.cnvAnnotationDialog.startUserAnnotations();
            }
            else if (this.isTranslocation() && this.$refs.translocationAnnotationDialog) {
                this.$refs.translocationAnnotationDialog.startUserAnnotations();
            }
            else if (this.isVirus() && this.$refs.virusAnnotationDialog) {
                this.$refs.virusAnnotationDialog.startUserAnnotations();
            }
        },
        commitAnnotations() {
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
                    annotationIdsForReporting: this.$store.getters["annotationStore/getAnnotationIdsForReporting"](this.currentVariant._id.$oid)
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
                            this.getVariantDetails(this.$route.query.variantId, this.isFirstVariant, this.isLastVariant);
                        }
                        else if (this.isCNV()) {
                            this.getCNVDetails(this.$route.query.variantId, this.isFirstVariant, this.isLastVariant);
                        }
                        else if (this.isTranslocation()) {
                            this.getTranslocationDetails(this.$route.query.variantId, this.isFirstVariant, this.isLastVariant);
                        }
                        else if (this.isVirus()) {
                            this.getVirusDetails(this.$route.query.variantId, this.isFirstVariant, this.isLastVariant);
                        }
                        this.$nextTick(() => {
                            this.cancelAnnotations();
                            this.canCopyAnnotation = true;
                            this.snackBarTimeout = 4000;
                            setTimeout(() => {
                                this.snackBarVisible = true;
                            }, 2000);
                            
                        });

                        // //keep track of the selected variants and refresh
                        // this.getSelectedVariantIds(true)
                        // .then(response => {
                        //     this.currentSelectedVariantIds = response;
                        //     if (this.currentSelectedVariantIds) {
                        //         this.tempSelectedSNPVariants = this.currentSelectedVariantIds.selectedSNPVariantIds;
                        //         this.tempSelectedCNVs = this.currentSelectedVariantIds.selectedCNVIds;
                        //         this.tempSelectedTranslocations = this.currentSelectedVariantIds.selectedTranslocationIds;
    
                        //         //once refreshed, reselect rows that were selected but not saved yet
                        //         this.$once('get-case-details-done', (annotations) => {
                        //             for (var i = 0; i < this.$refs.geneVariantDetails.items.length; i++) {
                        //                 var row = this.$refs.geneVariantDetails.items[i];
                        //                 if (this.tempSelectedSNPVariants.includes(row.oid)) {
                        //                     row.isSelected = true;
                        //                 }
                        //             }
                        //             for (var i = 0; i < this.$refs.cnvDetails.items.length; i++) {
                        //                 var row = this.$refs.cnvDetails.items[i];
                        //                 if (this.tempSelectedCNVs.includes(row.oid)) {
                        //                     row.isSelected = true;
                        //                 }
                        //             }
                        //             for (var i = 0; i < this.$refs.translocationDetails.items.length; i++) {
                        //                 var row = this.$refs.translocationDetails.items[i];
                        //                 if (this.tempSelectedTranslocations.includes(row.oid)) {
                        //                     row.isSelected = true;
                        //                 }
                        //             }
                        //             this.updateSelectedVariantTable();
                        //         });
                        //     }
                        // }).catch(error => {
                        //     this.handleDialogs(error.data, this.commitAnnotations.bind(null, this.userAnnotations));
                        //     this.$refs.annotationDialog.saving = false; 
                        //     this.$refs.cnvAnnotationDialog.saving = false;
                        //     this.$refs.translocationAnnotationDialog.saving = false;
                        // });
                    } else {
                        this.handleDialogs(response.data, this.commitAnnotations.bind(null, this.userAnnotations));
                        this.$refs.annotationDialog.saving = false; 
                        this.$refs.cnvAnnotationDialog.saving = false;
                        this.$refs.translocationAnnotationDialog.saving = false;
                        this.$refs.virusAnnotationDialog.saving = false;
                    }
                })
                .catch(error => {
                    this.handleAxiosError(error);
                });
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
            else if (this.isVirus()) {
                this.$refs.virusAnnotationDialog.cancelAnnotations();
            }
        },
        setSelected() {
            if (this.isSNP() && this.urlQuery.variantId && this.$store.getters["snpStore/getAllVariantItemsMap"][this.urlQuery.variantId]) {
                this.currentlySelected = this.$store.getters["snpStore/getAllVariantItemsMap"][this.urlQuery.variantId].isSelected;
            }
            if (this.isCNV() && this.urlQuery.variantId && this.$store.getters["cnvStore/getAllVariantItemsMap"][this.urlQuery.variantId]) {
                this.currentlySelected = this.$store.getters["cnvStore/getAllVariantItemsMap"][this.urlQuery.variantId].isSelected;
            }
            if (this.isTranslocation() && this.urlQuery.variantId && this.$store.getters["ftlStore/getAllVariantItemsMap"][this.urlQuery.variantId]) {
                this.currentlySelected = this.$store.getters["ftlStore/getAllVariantItemsMap"][this.urlQuery.variantId].isSelected;
            }
            if (this.isVirus() && this.urlQuery.variantId && this.$store.getters["virStore/getAllVariantItemsMap"][this.urlQuery.variantId]) {
                this.currentlySelected = this.$store.getters["virStore/getAllVariantItemsMap"][this.urlQuery.variantId].isSelected;
            }
        },
        handleAnnotationSelectionChanged(keepSaveState) {
            // this.annotationSelectionUnSaved = true;
            // this.annotationIdsForReporting = [];
            // for (var j = 0; j < this.utswAnnotationsFormatted.length; j++) {
            //     if (this.utswAnnotationsFormatted[j].isSelected) {
            //         this.annotationIdsForReporting.push(this.utswAnnotationsFormatted[j]._id);
            //     }
            // }
            // this.$emit("annotation-selection-changed", this.annotationSelectionUnSaved);
            this.$store.commit("annotationStore/updateVariantAnnotationSelection", 
            {id: this.currentVariant._id, 
                caseId: this.$route.params.id,
                formattedAnnotations: this.utswAnnotationsFormatted, 
                type: this.currentVariantType,
                keepSaveState: keepSaveState
            } );
        },
        createVariantName() {
            var text = this.currentVariant.geneName + " " + this.currentVariant.notation;
            if (text.length > 18) {
                return text.substring(0, 18) + "...";
            } 
            return text;
        },
        // saveAnnotationSelection(skipSnackBar) {
        //     if (!this.canProceed('canAnnotate')) {
        //         return;
        //     }
        //     this.savingAnnotationSelection = true;
        //     var lightVariant = {};
        //     lightVariant["_id"] = this.currentVariant._id;
        //     lightVariant["annotationIdsForReporting"] = [];
        //     for (var i = 0; i < this.utswAnnotationsFormatted.length; i++) {
        //         if (this.utswAnnotationsFormatted[i].isSelected) {
        //             lightVariant["annotationIdsForReporting"].push(this.utswAnnotationsFormatted[i]._id);
        //         }
        //     }
        //     return new Promise((resolve, reject) => {
        //     axios({
        //         method: 'post',
        //         url: webAppRoot + "/saveSelectedAnnotationsForVariant",
        //         params: {
        //             variantType: this.currentVariantType,
        //             caseId: this.$route.params.id,
        //             skipSnackBar: skipSnackBar
        //         },
        //         data: {
        //             // filters: this.$refs.advancedFilter.filters,
        //             variant: lightVariant
        //         }
        //     }).then(response => {
        //         this.waitingForAjaxCount--;
        //         if (response.data.isAllowed) {
        //             this.revertAnnotationSelection();
        //             if (!response.data.skipSnackBar) {
        //                 this.snackBarMessage = "Annotation Selection Saved";
        //                 this.snackBarLink = "";
        //                 this.snackBarVisible = true;
        //                 // this.annotationSelectionUnSaved = false;
        //             }
        //             resolve({success: true});
        //         }
        //         else {
        //             this.handleDialogs(response.data, this.saveAnnotationSelection.bind(null, response.data.skipSnackBar));
        //         }
        //         this.savingAnnotationSelection = false;
        //         this.$emit("annotation-selection-saved");
        //     }).catch(error => {
        //         this.savingAnnotationSelection = false;
        //         this.handleAxiosError(error);
        //         reject(error);
        //     });
        // });
        // },
        saveAllAnnotationSelections(skipSnackBar) {
            if (!this.canProceed('canAnnotate')) {
                return;
            }
            this.savingAnnotationSelection = true;
            return new Promise((resolve, reject) => {
            axios({
                method: 'post',
                url: webAppRoot + "/saveAllSelectedAnnotations",
                params: {
                    skipSnackBar: skipSnackBar
                },
                data: {
                    // filters: this.$refs.advancedFilter.filters,
                    variants: this.$store.getters["annotationStore/getAnnotationSelectionToSave"]
                }
            }).then(response => {
                this.waitingForAjaxCount--;
                if (response.data.isAllowed) {
                    // this.revertAnnotationSelection();
                    if (!response.data.skipSnackBar) {
                        this.snackBarMessage = "Annotation Selection Saved";
                        this.snackBarLink = "";
                        this.snackBarVisible = true;
                        // this.annotationSelectionUnSaved = false;
                    }
                    this.$store.commit("annotationStore/clearAfterSaving");
                    resolve({success: true});
                }
                else {
                    this.handleDialogs(response.data, this.saveAllAnnotationSelections.bind(null, response.data.skipSnackBar));
                }
                this.savingAnnotationSelection = false;
                this.$emit("annotation-selection-saved");
            }).catch(error => {
                this.savingAnnotationSelection = false;
                this.handleAxiosError(error);
                reject(error);
            });
        });
        },
        // revertAnnotationSelection() {
            // this.annotationIdsForReporting = []; //reset the unsaved list of ids
            // this.annotationSelectionUnSaved = false; //update badge on save button
            // this.$emit("annotation-selection-changed", this.annotationSelectionUnSaved);
        // },
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
        closeVariantDetails() {
            this.$emit("close-variant-details");
            this.$refs.lookupTool.panelVisible = false;
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
        createOldBuildList() {
            var builds = [];
            for (var key in this.currentVariant.oldBuilds) {
                builds.push(this.currentVariant.oldBuilds[key].chrom + ":" + this.currentVariant.oldBuilds[key].pos + " (" + key + ")");
            }
            return builds;
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
                annotation.scopeTooltip = this.$refs.annotationDialog ? this.$refs.annotationDialog.createLevelInformation(annotations[i]) : "";
                annotation.tier = annotations[i].tier;
                annotation.classification = annotations[i].classification;
                annotation.isSelected = annotations[i].isSelected;
                annotation.trial = annotations[i].trial;
                annotation.drugs = annotations[i].drugs;
                annotation.warningLevel = annotations[i].warningLevel;
                annotation.drugResistant = annotations[i].drugResistant;
                // annotation.visible = annotations[i].isVisible;
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
                // annotation.visible = annotations[i].isVisible;
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
                // annotation.visible = annotations[i].isVisible;
                formatted.push(annotation);
            }
            return formatted;
        },
        formatLocalVirusAnnotations(annotations, showUser) {
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
                annotation.scopeTooltip = this.$refs.annotationDialog ? this.$refs.annotationDialog.createLevelInformation(annotations[i]) : "";
                annotation.tier = annotations[i].tier;
                annotation.classification = annotations[i].classification;
                annotation.isSelected = annotations[i].isSelected;
                annotation.trial = annotations[i].trial;
                annotation.drugs = annotations[i].drugs;
                annotation.warningLevel = annotations[i].warningLevel;
                annotation.drugResistant = annotations[i].drugResistant;
                // annotation.visible = annotations[i].isVisible;
                formatted.push(annotation);
            }
            return formatted;
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
            else if (newAnnotation.type == "virus") {
                newAnnotation.isGeneSpecific = false;
                newAnnotation.isVariantSpecific = false;
                newAnnotation.isTumorSpecific = newAnnotation.scopes[2];
            }
            this.userAnnotations.push(newAnnotation);
            this.commitAnnotations(this.userAnnotations);
        },
        hideAnnotation(annotation) {
            if (!annotation._id || !annotation._id["$oid"]) {
                return; //new annotation can't modify it yet
            }
            axios.get(
                webAppRoot + "/hideAnnotations",
                {
                    params: {
                        variantId: this.currentVariant._id.$oid,
                        caseId: this.$route.params.id,
                        annotationId: annotation._id["$oid"],
                        variantType: this.currentVariantType
                    }
                }).then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        console.log("success");
                        if (this.isSNP()) {
                            this.getVariantDetails(this.$route.query.variantId, this.isFirstVariant, this.isLastVariant);
                        }
                        else if (this.isCNV()) {
                            this.getCNVDetails(this.$route.query.variantId, this.isFirstVariant, this.isLastVariant);
                        }
                        else if (this.isTranslocation()) {
                            this.getTranslocationDetails(this.$route.query.variantId, this.isFirstVariant, this.isLastVariant);
                        }
                        else if (this.isVirus()) {
                            this.getVirusDetails(this.$route.query.variantId, this.isFirstVariant, this.isLastVariant);
                        }
                    } else {
                        this.handleDialogs(response.data, this.hideAnnotation.bind(null,
                            annotation));
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                });
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
                        this.revertVariant(true);
                        // var variantId = this.currentVariant._id["$oid"];
                        // if (this.isSNP()) {
                        //     for (var i = 0; i < this.$refs.geneVariantDetails.items.length; i++) {
                        //         if (this.$refs.geneVariantDetails.items[i].oid == variantId) {
                        //                 this.$nextTick(this.getVariantDetails(this.$refs.geneVariantDetails.items[i]));
                        //                 break;
                        //         }
                        //     }
                        // }
                        // else if (this.isCNV()) {
                        //     for (var i = 0; i < this.$refs.cnvDetails.items.length; i++) {
                        //         if (this.$refs.cnvDetails.items[i].oid == variantId) {
                        //                 this.$nextTick(this.getCNVDetails(this.$refs.cnvDetails.items[i]));
                        //                 break;
                        //         }
                        //     }
                        // }
                        // else if (this.isTranslocation()) {
                        //     for (var i = 0; i < this.$refs.translocationDetails.items.length; i++) {
                        //         if (this.$refs.translocationDetails.items[i].oid == variantId) {
                        //                 this.$nextTick(this.getTranslocationDetails(this.$refs.translocationDetails.items[i]));
                        //                 break;
                        //         }
                        //     }
                        // }
                        // this.showSnackBarMessageWithParams(response.data.message, null, null, 4000);
                        this.showSnackBarMessage(response.data.message);
                    }
                    else {
                        this.handleDialogs(response.data, this.setDefaultTranscript.bind(null, item));
                    }
                })
                .catch(error => {
                    alert(error);
                });
        },
        handleSaveAll() {
            this.$emit("handle-save-all");
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
        updateEditAnnotationBreadcrumbs(visible) {
            this.$emit("updateEditAnnotationBreadcrumbs", visible);
        },
        breadcrumbNavigation(index) {
            this.$emit("breadcrumb-navigation", index);
        },
        formatChrom(chrom) { //needed to call the global function from v-text
            return formatChrom(chrom);
        },
        disableBreadCrumbItem(item, index) {
            return (item.disabled || index == this.breadcrumbs.length - 1);
        },
        selectVariantForReport() {
            let idType = "SNP";
            if (this.isCNV()) {
                idType = "CNV";
            }
            else if (this.isTranslocation()) {
                idType = "FTL";
            }
            else if (this.isVirus()) {
                idType = "VIR";
            }
            this.currentlySelected = true;
            this.$emit("selection-changed", this.urlQuery.variantId, idType, this.currentlySelected);
        },
        removeVariantFromReport() {
            let idType = "SNP";
            if (this.isCNV()) {
                idType = "CNV";
            }
            else if (this.isTranslocation()) {
                idType = "FTL";
            }
            else if (this.isVirus()) {
                idType = "VIR";
            }
            this.currentlySelected = false;
            this.$emit("selection-changed", this.urlQuery.variantId, idType, this.currentlySelected);
        },
        openAddCNVDialog(currentListOfCNVVisibleGenes) {
            this.currentListOfCNVVisibleGenes = currentListOfCNVVisibleGenes;
            this.addCNVDialogVisible = true;
        },
        closeAddCNVDialog() {
            this.addCNVDialogVisible = false;
        },
        refreshVariantTables() {
            this.$emit("refresh-variant-tables");
        },
        getLookupRef() {
            var ref = this.$refs.lookupTool;
            if (this.urlQuery.edit && this.isSNP()) {
                ref = this.$refs.annotationDialog.$refs.lookupTool;
            }
            else if (this.urlQuery.edit && this.isCNV()) {
                ref = this.$refs.cnvAnnotationDialog.$refs.lookupTool;
            }
            else if (this.urlQuery.edit && this.isTranslocation()) {
                ref = this.$refs.translocationAnnotationDialog.$refs.lookupTool;
            }
            return ref;
        },
        toggleLookupTool(cnvGeneName) {
            var ref = this.getLookupRef();
            this.reloadLookupValues(ref, cnvGeneName);
            ref.panelVisible = true;
        },
        toggleLookupToolVariant() {
            var ref = this.getLookupRef();
            this.reloadLookupValues(ref, null, "Variant");
            ref.panelVisible = true;
        },
        toggleLookupToolGene() {
            var ref = this.getLookupRef();
            this.reloadLookupValues(ref, null, "Gene");
            ref.panelVisible = true;
        },
        isLookupVisible() {
            return this.$refs.lookupTool && this.$refs.lookupTool.panelVisible;
        },
        reloadLookupValues(ref, cnvGeneName, activeButton) {
            if (!ref) {
                ref = this.getLookupRef();
            }
            ref.currentGene = this.currentVariant.geneName;
            ref.currentVariant = this.currentVariant.notation;
            if (ref.currentlyActive && !activeButton) {
                activeButton = ref.currentlyActive;
            }
            if (this.isCNV()) {
                ref.currentGene = cnvGeneName;
                var ampDel = "";
                if (this.currentVariant.aberrationType == "amplification") {
                    ampDel = "amp";
                }
                else if (this.currentVariant.aberrationType == "homozygous loss") {
                    ampDel = "del";
                }
                ref.currentAmpDel = ampDel;
                ref.currentlyActive = "CNV";
            }
            else if (this.isSNP()) {
                ref.currentlyActive = activeButton ? activeButton : "Variant";
            }
            else if (this.isTranslocation()) {
                ref.currentFive = this.currentVariant.leftGene;
                ref.currentThree = this.currentVariant.rightGene;
                ref.currentlyActive = "Fusion";
            }
            ref.oncokbVariantName = this.currentVariant.oncokbVariantName;
            
            var oncotreeItems = this.oncotree.filter(o => o.text == this.patientDetailsOncoTreeDiagnosis.text);
            var oncotree = null;
            if (oncotreeItems && oncotreeItems[0]) {
                oncotree = oncotreeItems[0];
            }
            ref.currentOncotreeCode = oncotree;
            ref.submitForm();
        },
        getHG19Pos() {
            if (this.currentVariant.oldBuilds && this.currentVariant.oldBuilds.hg19) {
                return this.currentVariant.oldBuilds.hg19.pos;
            }
            return -1;
        }
        
    },
    computed: {
    },
    created() {
        bus.$on('create-new-cnv', (genes) => {
            this.openAddCNVDialog(genes);
        });
        bus.$on('setDefaultTranscript', (item) => {
            this.setDefaultTranscript(item);
        });
    },
    mounted() {
        this.setSelected();
    },
    destroyed() {
        bus.$off('create-new-cnv');
        bus.$off('setDefaultTranscript');
    },
    watch: {
    }


});