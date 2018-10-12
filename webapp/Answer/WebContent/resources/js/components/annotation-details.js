

Vue.component('annotation-details', {
    props: {
        variantDetailsVisible: {default: false, type: Boolean},
        variantTiers: {default:() => [], type: Array},
        annotationCategories: {default:() => [], type: Array},
        annotationCategoriesCNV: {default:() => [], type: Array},
        annotationBreadth: {default:() => [], type: Array},
        annotationClassifications: {default:() => [], type: Array},
        isSaveNeededBadgeVisible: {default: false, type: Boolean},
        caseName: {default: "", type: String},
        caseTypeIcon: {default: "", type: String},
        caseType: {default: "", type: String},
        createSaveTooltip: {default: "", type: String},
        breadcrumbs: {default: () => [], type: Array},
        readonly: {default: false, type: Boolean},
        snpVariantsSelected: {default: () => {}, type: Object},
        geneVariantDetails: {default: () => {}, type: Object},
    },
    template: `<div>
    <v-dialog v-model="confirmationVariantDialogVisible"  max-width="500px">
    <v-card>
        <v-card-text class="pl-2 pr-2 subheading">
            You have unsaved work
        </v-card-text>
        <v-card-actions class="card-actions-bottom">
            <v-tooltip bottom>
            <v-btn color="primary" @click="closeVariantDetails()" slot="activator">Close Anyway
            </v-btn>
            <span>Close Annotation Panel without saving</span>
            </v-tooltip>
            <v-tooltip bottom>
            <v-btn color="primary" @click="closeAndSaveAllVariantDetailsConfirmationDialog()" slot="activator">Save & Close
            </v-btn>
            <span>Close Annotation Panel without saving</span>
            </v-tooltip>
            <v-tooltip bottom>
            <v-btn color="error" @click="confirmationVariantDialogVisible = false" slot="activator">Cancel
            </v-btn>
            <span>Close this warning. You work will not be lost.</span>
            </v-tooltip>
        </v-card-actions>
    </v-card>
</v-dialog>

    <v-dialog v-model="variantDetailsVisible" scrollable fullscreen hide-overlay transition="dialog-bottom-transition">
        <v-card class="soft-grey-background">
            <v-toolbar dense dark color="primary"">
                <v-menu offset-y offset-x class="ml-0">
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


                                            <v-list-tile v-if="isSNP()" avatar @click="mdaAnnotationsVisible = !mdaAnnotationsVisible" :disabled="!mdaAnnotationsExists()">
                                                <v-list-tile-avatar>
                                                    <v-icon v-if="mdaAnnotationsExists()">mdi-message-bulleted</v-icon>
                                                    <v-icon v-if="!mdaAnnotationsExists()">mdi-message-bulleted-off</v-icon>
                                                </v-list-tile-avatar>
                                                <v-list-tile-content>
                                                    <v-list-tile-title v-if="mdaAnnotationsExists()">MDA Annotations</v-list-tile-title>
                                                    <v-list-tile-title v-if="!mdaAnnotationsExists()">No MDA Annotations</v-list-tile-title>
                                                </v-list-tile-content>
                                            </v-list-tile>

                                            <v-list-tile avatar @click="utswAnnotationsVisible = !utswAnnotationsVisible" :disabled="!utswAnnotationsExists()">
                                                <v-list-tile-avatar>
                                                    <v-icon v-if="utswAnnotationsExists()">mdi-message-bulleted</v-icon>
                                                    <v-icon v-if="!utswAnnotationsExists()">mdi-message-bulleted-off</v-icon>
                                                </v-list-tile-avatar>
                                                <v-list-tile-content>
                                                    <v-list-tile-title v-if="utswAnnotationsExists()">UTSW Annotations</v-list-tile-title>
                                                    <v-list-tile-title v-if="!utswAnnotationsExists()">No UTSW Annotations</v-list-tile-title>
                                                </v-list-tile-content>
                                            </v-list-tile>

                                        </v-list>
                                    </v-menu>
                                </v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile v-if="isSNP()" avatar @click="openBamViewerLink()">
                            <v-list-tile-avatar>
                                IGV
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Open Bam Viewer in New Tab</v-list-tile-title>
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

                        <v-list-tile avatar @click="selectVariantForReport()" v-if="!currentRow.isSelected" :disabled="saveDialogVisible || !canProceed('canSelect')">
                            <v-list-tile-avatar>
                                <v-icon>done</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Select Variant</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile avatar @click="removeVariantFromReport()" v-if="currentRow.isSelected" :disabled="saveDialogVisible || !canProceed('canSelect')">
                            <v-list-tile-avatar>
                                <v-icon>done</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Deselect Variant</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile avatar @click="closingVariantDetailsConfirmationDialog()">
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
          <span v-if="isCNV()">CNV</span>
          <span v-if="isTranslocation()">FTL</span>
          Variant:
              <span v-if="isSNP()">{{ currentVariant.geneName }} {{ currentVariant.notation }}</span>
              <span v-if="isCNV()" v-text="formatChrom(currentVariant.chrom)"></span>
              <span v-if="isTranslocation()">{{ currentVariant.fusionName }}</span>
              <span> -- {{ caseName }} -- </span> 
            <v-tooltip bottom>
              <v-icon slot="activator" size="20" class="pb-1"> {{ caseTypeIcon }} </v-icon>
            <span>{{caseType}} case</span>  
            </v-tooltip>

            <save-badge :show-save-needed-badge="isSaveNeededBadgeVisible" :tooltip="createSaveTooltip"
            @save-all="handleSaveAll()"></save-badge>
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
                <v-tooltip bottom v-if="isSNP()">
                    <v-btn :disabled="!mdaAnnotationsExists()" icon flat :color="(mdaAnnotationsVisible && mdaAnnotationsExists()) ? 'amber accent-2' : ''"
                        @click="mdaAnnotationsVisible = !mdaAnnotationsVisible" slot="activator">
                        <v-icon v-if="mdaAnnotationsExists()">mdi-message-bulleted</v-icon>
                        <v-icon v-if="!mdaAnnotationsExists()">mdi-message-bulleted-off</v-icon>
                    </v-btn>
                    <span v-if="mdaAnnotationsExists()">Show/Hide MDA Annotations</span>
                    <span v-if="!mdaAnnotationsExists()">No MDA Annotations</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <v-btn :disabled="!utswAnnotationsExists()" icon flat :color="(utswAnnotationsVisible && utswAnnotationsExists()) ? 'amber accent-2' : ''"
                        @click="utswAnnotationsVisible = !utswAnnotationsVisible" slot="activator">
                        <v-icon v-if="utswAnnotationsExists()">mdi-message-bulleted</v-icon>
                        <v-icon v-if="!utswAnnotationsExists()">mdi-message-bulleted-off</v-icon>
                    </v-btn>
                    <span v-if="utswAnnotationsExists()">Show/Hide UTSW Annotations</span>
                    <span v-if="!utswAnnotationsExists()">No UTSW Annotations</span>
                </v-tooltip>
                <v-tooltip bottom v-if="isSNP()">
                    <v-btn ref="bamViewerLink" icon flat slot="activator" :href="createBamViewerLink()" target="_blank" rel="noreferrer">
                        IGV
                    </v-btn>
                    <span>Open Bam Viewer in New Tab</span>
                </v-tooltip>


                <v-tooltip bottom>
                    <v-btn icon @click="closingVariantDetailsConfirmationDialog()" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close Variant</span>
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

                <v-container grid-list-md fluid>
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
                                    @save-variant="saveVariant" color="primary"" ref="variantDetailsPanel"
                                    :variant-type="currentVariantType" cnv-plot-id="cnvPlotDetails">
                                </variant-details>

                            </v-flex>
                        </v-slide-y-transition>
                        <v-slide-y-transition>
                            <v-flex xs12 sm12 md9 lg7 xl5 v-show="isRelatedVariantsVisible()">
                                <div>
                                    <data-table ref="relatedVariantAnnotation" :fixed="false" :fetch-on-created="false" table-title="Related Variants" initial-sort="geneId"
                                        no-data-text="No Data" :show-pagination="false" title-icon="link" color="primary"">
                                    </data-table>
                                </div>
                            </v-flex>
                        </v-slide-y-transition>
                        <v-slide-y-transition>
                            <v-flex xs12 v-show="annotationVariantCanonicalVisible && isSNP()">
                                <div>
                                    <data-table ref="canonicalVariantAnnotation" :fixed="false" :fetch-on-created="false" table-title="Canonical VCF Annotations"
                                        initial-sort="geneId" no-data-text="No Data" :show-pagination="false" title-icon="mdi-table-search"
                                        color="primary"">
                                    </data-table>
                                </div>
                            </v-flex>
                        </v-slide-y-transition>
                        <v-slide-y-transition>
                            <v-flex xs12 v-show="annotationVariantOtherVisible  && isSNP()">
                                <data-table ref="otherVariantAnnotations" :fixed="false" :fetch-on-created="false" table-title="Other VCF Annotations" initial-sort="geneId"
                                    no-data-text="No Data" :show-row-count="true" title-icon="mdi-table-search" color="primary"">
                                </data-table>
                            </v-flex>
                        </v-slide-y-transition>
                        <!-- MDA Annotation card -->
                        <v-slide-y-transition>
                            <v-flex xs12 v-show="mdaAnnotationsVisible && mdaAnnotationsExists()">
                                <v-card class="soft-grey-background">
                                    <v-toolbar class="elevation-0" dense dark color="primary"">
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
                                        <v-container grid-list-md fluid>
                                    <v-layout row wrap>
                                    <v-flex xs12 sm12 md6 lg6 xl4 v-for="(annotation, index) in mdaAnnotationsFormatted" :key="index" v-show="annotation.visible">
                                        <mda-annotation-card :annotation="annotation" :variant-type="currentVariantType"
                                        :no-edit="true"
                                        @annotation-selection-changed="handleAnnotationSelectionChanged()"></mda-annotation-card>
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
                                    <v-toolbar class="elevation-0" dense dark color="primary"">
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

                                                <v-list-tile avatar v-if="canProceed('canAnnotate') && !readonly" @click="saveAnnotationSelection()" :loading="savingAnnotationSelection" :disabled="isAnnotationTierMissing()">
                                                    <v-list-tile-avatar>
                                                        <v-icon>save</v-icon>
                                                    </v-list-tile-avatar>
                                                    <v-list-tile-content>
                                                        <v-list-tile-title>Save Selection (all variant types)</v-list-tile-title>
                                                    </v-list-tile-content>
                                                </v-list-tile>

                                            <v-list-tile avatar v-if="canProceed('canAnnotate') && !readonly" @click="revertAnnotationSelection()">
                                                <v-list-tile-avatar>
                                                    <v-icon>settings_backup_restore</v-icon>
                                                </v-list-tile-avatar>
                                                <v-list-tile-content>
                                                    <v-list-tile-title>Restore Last Saved Selection</v-list-tile-title>
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
                                        <v-badge color="red" v-if="canProceed('canAnnotate') && !readonly" right bottom overlap v-model="annotationSelectionUnSaved" class="mini-badge">
                                            <v-icon slot="badge"></v-icon>
                                            <v-tooltip bottom>
                                                <v-btn flat icon @click="saveAnnotationSelection()" slot="activator" :disabled="isAnnotationTierMissing()">
                                                    <v-icon>save</v-icon>
                                                </v-btn>
                                                <span v-show="!isAnnotationTierMissing()">Save Selection (all variant types)</span>
                                                <span v-show="isAnnotationTierMissing()">All selected annotations need a tier to be in the report</span>
                                            </v-tooltip>
                                        </v-badge>
                                        <v-tooltip bottom>
                                            <v-btn flat icon v-if="canProceed('canAnnotate') && !readonly" @click="revertAnnotationSelection()" slot="activator">
                                                <v-icon>settings_backup_restore</v-icon>
                                            </v-btn>
                                            <span>Restore Last Saved Selection</span>
                                        </v-tooltip>
                                        <v-tooltip bottom>
                                            <v-btn icon @click="utswAnnotationsVisible = false" slot="activator">
                                                <v-icon>close</v-icon>
                                            </v-btn>
                                            <span>Close</span>
                                        </v-tooltip>
                                    </v-toolbar>
                                    <v-card-text>
                                        <v-container grid-list-md fluid>
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
                                                                    <v-select v-if="isCNV()" clearable :value="searchAnnotationCategory" :items="annotationCategoriesCNV" v-model="searchAnnotationCategory"
                                                                        label="Search by Category" multiple @input="matchAnnotationFilter"></v-select>
                                                                </v-flex>

                                                                <v-flex xs6 sm6 md3 lg3 xl2>
                                                                    <v-select v-if="isCNV()" clearable :value="searchAnnotationBreadth" :items="annotationBreadth" v-model="searchAnnotationBreadth"
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
                                                                    <v-select v-if="isCNV()" clearable :value="searchAnnotationScope" :items="scopesCNV" v-model="searchAnnotationScope" label="Search by Scope"
                                                                        multiple @input="matchAnnotationFilter"></v-select>
                                                                    <v-select v-if="isTranslocation()" clearable :value="searchAnnotationScope" :items="scopesTranslocation" v-model="searchAnnotationScope"
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
                                                    @annotation-selection-changed="handleAnnotationSelectionChanged()"></utsw-annotation-card>
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
                <v-tooltip top>
                    <v-btn color="primary" @click="startUserAnnotations()" slot="activator" :disabled="!canProceed('canAnnotate') || readonly">Add/Edit
                        <v-icon right dark>create</v-icon>
                    </v-btn>
                    <span>Create/Edit Your Annotations</span>
                </v-tooltip>
                <v-btn v-if="!currentRow.isSelected" :disabled="saveDialogVisible || !canProceed('canSelect') || readonly" color="success"
                    @click="selectVariantForReport()" slot="activator">Select Variant
                    <v-icon right dark>done</v-icon>
                </v-btn>
                <v-btn v-if="currentRow.isSelected" :disabled="saveDialogVisible || !canProceed('canSelect') || readonly" color="warning"
                    @click="removeVariantFromReport()" slot="activator">Deselect Variant
                    <v-icon right dark>done</v-icon>
                </v-btn>
                <v-tooltip top>
                    <v-btn :disabled="isFirstVariant" color="primary" @click="loadPrevVariant()" slot="activator">Prev. Variant
                        <v-icon right dark>chevron_left</v-icon>
                    </v-btn>
                    <span>Show Previous Variant</span>
                </v-tooltip>
                <v-tooltip top>
                    <v-btn :disabled="isLastVariant" color="primary" @click="loadNextVariant()" slot="activator">
                        <v-icon left dark>chevron_right</v-icon>
                        Next Variant
                    </v-btn>
                    <span>Show Next Variant</span>
                </v-tooltip>
                <v-btn color="error" @click="closingVariantDetailsConfirmationDialog()">Close
                    <v-icon right dark>cancel</v-icon>
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
    </div>`,
    data() {
        return {
            saveDialogVisible: false,
            annotationVariantDetailsVisible: true,
            annotationVariantRelatedVisible: true,
            annotationVariantCanonicalVisible: true,
            annotationVariantOtherVisible: false,
            currentVariantHasRelatedVariants: false,
            isFirstVariant: false,
            isLastVariant: false,
            utswAnnotations: [],
            utswAnnotationsFormatted: [],
            mdaAnnotations: "",
            mdaAnnotationsFormatted: [],
            mdaAnnotationsVisible: true,
            utswAnnotationsVisible: true,
            currentRow: {},
            currentVariant: {},
            confirmationVariantDialogVisible: false,
            currentVariantType: "snp",
            variantDataTables: [],
            linkTable: [],
            searchAnnotations: "",
            searchAnnotationsVisible: false,
            searchAnnotationClassification: [],
            searchAnnotationCategory: [],
            searchAnnotationBreadth: [],
            searchAnnotationTier: [],
            searchAnnotationScope: [],
            annotationSelectionUnSaved: false,
            savingAnnotationSelection: false,
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
                'homozygous loss'
            ],
        }
    },
    methods: {
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
        mdaAnnotationsExists() {
            return this.mdaAnnotations != '';
        },
        utswAnnotationsExists() {
            return this.utswAnnotationsFormatted.length > 0;
        },
        isRelatedVariantsVisible() {
            return this.annotationVariantRelatedVisible && this.isSNP() && this.currentVariantHasRelatedVariants;
        },
        toggleRelatedVariants() {
            if (this.currentVariantHasRelatedVariants) {
                this.annotationVariantRelatedVisible = !this.annotationVariantRelatedVisible;
            }
        },
        formatChrom(chrom) { //needed to call the global function from v-text
            return formatChrom(chrom);
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
        openBamViewerLink() {
            this.$refs.bamViewerLink.$el.click();
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
        getVariantDetails(item, resetSaveFlags) {
            this.currentVariantType = "snp";

            this.currentVariantFlags = item.iconFlags.iconFlags;
            this.currentRow = item;
            var table; //could be the selected variant table or the regular one
            if (this.saveDialogVisible) {
                table = this.snpVariantsSelected;
            }
            else {
                table = this.geneVariantDetails;
            }
            var currentIndex = table.getCurrentItemIdex(this.currentRow.oid);
            this.isFirstVariant = table.isFirstItem(currentIndex);
            this.isLastVariant = table.isLastItem(currentIndex);


            // this.loadingVariantDetails = true;
            axios.get(
                webAppRoot + "/getVariantDetails",
                {
                    params: {
                        variantId: item.oid

                    }
                }).then(response => {
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
                                    url: this.createUCSCLink(),
                                    tooltip: "Open Genome Browser"
                                },
                                {
                                    label: "Gene",
                                    value: this.currentVariant.geneName
                                },
                                {
                                    label: "Notation",
                                    value: this.currentVariant.notation
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
                                {
                                    label: "gnomAD Pop. Max. Allele Frequency",
                                    value: this.formatPercent(this.currentVariant.gnomadPopmaxAlleleFrequency)
                                },
                            ]
                        };
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
                        this.$refs.canonicalVariantAnnotation.manualDataFiltered(response.data.canonicalSummary);
                        this.$refs.otherVariantAnnotations.manualDataFiltered(response.data.otherSummary);
                        this.userAnnotations = this.currentVariant.referenceVariant.utswAnnotations.filter(a => a.userId == this.userId);
                        this.$refs.annotationDialog.userAnnotations = this.userAnnotations;
                        this.utswAnnotations = this.currentVariant.referenceVariant.utswAnnotations;
                        this.reloadPreviousSelectedState();
                        this.mdaAnnotations = this.currentVariant.mdaAnnotation ? this.currentVariant.mdaAnnotation : "";
                        this.formatAnnotations();
                        this.loadingVariantDetails = false;
                        if (resetSaveFlags) {
                            this.annotationSelectionUnSaved = false;
                        }
                        this.variantDetailsVisible = true;
                        this.updateVariantDetails();

                        //finally, open edit annotation
                        this.handleEditAnnotationOpening();

                        this.updateSplashProgress();

                    } else {
                        this.loadingVariantDetails = false;
                        this.handleDialogs(response.data, this.getVariantDetails.bind(null,
                            item));
                    }
                }).catch(error => {
                    this.loadingVariantDetails = false;
                    this.handleAxiosError(error);
                });
        },
        formatPercent(value) {
            if (value !== null && !isNaN(value)) {
                return (Math.round(parseFloat(value) * 100000) / 1000) + "%";
            } return "";
        },
        formatSNPCallers(callers) {
            var labels = ['Name:', 'Alt:', 'Tumor Total Depth:', 'Tumor Alt Percent:', 'Normal Total Depth:', 'Normal Alt Percent:'];
            var callerNames = callers.map(c => c.callerName);
            var alts = callers.map(c => c.alt);
            var tumorTotalDepths = callers.map(c => c.tumorTotalDepth);
            var tumorAlleleFrequencys = callers.map(c => c.tumorAlleleFrequencyFormatted + "%");
            var normalTotalDepths = callers.map(c => c.normalTotalDepth);
            var normalAlleleFrequencys = callers.map(c => c.normalAlleleFrequencyFormatted + "%");
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
        startUserAnnotations() {
            this.$emit("start-user-annotations");
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
                        for (var field in this.utswAnnotationsFormatted[i]) {
                            if (field == "scopeTooltip") {
                                continue;
                            }
                            else if (this.utswAnnotationsFormatted[i][field] && (this.utswAnnotationsFormatted[i][field] + "").indexOf(this.searchAnnotations) > -1) {
                                foundTextMatch = true;
                                break;
                            }
                        }
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
        disableBreadCrumbItem(item, index) {
            return (item.disabled || index == this.breadcrumbs.length - 1);
        },
        createUCSCLink() {
            return "https://genome.ucsc.edu/cgi-bin/hgTracks?db=hg38&position="
                + this.currentVariant.chrom + ":" + (this.currentVariant.pos - 50)
                + "-" + (this.currentVariant.pos + 50);
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
                    if (!this.utswAnnotationsFormatted[i].tier) {
                        return; //selected annotation without a tier. Should not happen normally
                    }
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
                if (response.data.isAllowed) {
                    this.revertAnnotationSelection();
                    this.waitingForAjaxCount--;
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
        },
        revertAnnotationSelection() {
            if (this.isSNP()) {
                this.getVariantDetails(this.currentRow, true);
            }
            else if (this.isCNV()) {
                this.getCNVDetails(this.currentRow, true);
            }
            else if (this.isTranslocation()) {
                this.getTranslocationDetails(this.currentRow, true);
            }
            this.annotationIdsForReporting = []; //rest the unsaved list of ids
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
        reloadPreviousSelectedState() {
            for (var i = 0; i < this.annotationIdsForReporting.length; i++) {
                for (var j = 0; j < this.utswAnnotations.length; j++) {
                    if (this.annotationIdsForReporting[i].$oid == this.utswAnnotations[j]._id.$oid) {
                        this.utswAnnotations[j].isSelected = true; //only set if true, do not unset if false
                    }
                }
            }
        },
        formatAnnotations() {
            this.utswAnnotationsFormatted = this.formatLocalAnnotations(this.utswAnnotations, true);
            if (this.mdaAnnotations) {
                this.mdaAnnotationsFormatted = this.formatMDAAnnotations(this.mdaAnnotations);
            }
            // this.userAnnotationsFormatted = this.formatLocalAnnotations(this.userAnnotations, false);
            this.matchAnnotationFilter();
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
                    tumorSpecific: "",
                    category: "",
                    createdDate: "",
                    createdSince: "",
                    modifiedDate: "",
                    modifiedSince: "",
                    pmids: [],
                    nctids: [],
                    tier: "",
                    classification: "",
                    visible: true,
                    isSelected: false
                };
                // annotation._id = annotations.annotationCategories[i]._id;
                // if (showUser) {
                //     annotation.fullName = annotations.annotationCategories[i].fullName;
                // }
                var geneSpecific = true;
                var variantSpecific =  annotations.annotationCategories[i].title == "Functional Annotation";
                var tumorSpecific = annotations.annotationCategories[i].title == "Tumor type-specific annotation";
                annotation.text = annotations.annotationCategories[i].text.replace(/\n/g, "<br/>").replace(/(PMID:)([0-9]*)/g, `<a href='https://www.ncbi.nlm.nih.gov/pubmed/?term=$2' target="_blank">PMID:$2</a>`);
                annotation.scopes = [geneSpecific ,variantSpecific, tumorSpecific];
                annotation.scopeLevels = [
                "Gene " + (geneSpecific ? this.mdaAnnotations.gene : ''),
                "Variant " + (variantSpecific ? this.currentVariant.notation : ''), "Tumor"];
                if (annotations.annotationCategories[i].title == "Biomarker Summary") {
                    annotation.category = "Gene Function";
                }
                else if (annotations.annotationCategories[i].title == "Functional Annotation") {
                    annotation.category = "Variant Function";
                }
                else if (annotations.annotationCategories[i].title == "Potential Therapeutic Implications") {
                    annotation.category = "Therapy";
                }
                else if (annotations.annotationCategories[i].title == "Tumor type-specific annotation") {
                    annotation.category = "Prognosis"; //TODO not sure about that one
                }
                //TOOD keep going

                annotation.createdDate = this.mdaAnnotations.reportDate;
                annotation.createdSince = this.mdaAnnotations.createdSince;
                // annotation.modifiedDate = annotations[i].modifiedDate;
                // annotation.modifiedSince = annotations[i].modifiedSince;
                // annotation.pmids = annotations[i].pmids;
                // annotation.nctids = annotations[i].nctids;
                annotation.scopeTooltip = this.createMDALevelInformation(geneSpecific, variantSpecific, tumorSpecific);
                // annotation.tier = annotations[i].tier;
                // annotation.classification = annotations[i].classification;
                annotation.isSelected = annotations.annotationCategories[i].isSelected;
                formatted.push(annotation);
            }
            return formatted;
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
                    nctids: [],
                    tier: "",
                    classification: "",
                    visible: true,
                    isSelected: false
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
                    "Tumor"];
                annotation.category = annotations[i].category;
                annotation.createdDate = annotations[i].createdDate;
                annotation.createdSince = annotations[i].createdSince;
                annotation.modifiedDate = annotations[i].modifiedDate;
                annotation.modifiedSince = annotations[i].modifiedSince;
                annotation.pmids = annotations[i].pmids;
                annotation.nctids = annotations[i].nctids;
                annotation.scopeTooltip = this.createLevelInformation(annotations[i]);
                annotation.tier = annotations[i].tier;
                annotation.classification = annotations[i].classification;
                annotation.isSelected = annotations[i].isSelected;
                formatted.push(annotation);
            }
            return formatted;
        },
        createLevelInformation(annotation) {
            var text = "This annotation's scope is limited to ";
            var commaNeeded = false;
            if (annotation.isCaseSpecific) {
                text = text + "this case";
                commaNeeded = true;
            }
            if (annotation.isGeneSpecific) {
                if (commaNeeded) {
                    text = text + ", ";
                }
                text = text + "this gene";
                commaNeeded = true;
            }
            if (annotation.isVariantSpecific) {
                if (commaNeeded) {
                    text = text + ", ";
                }
                text = text + "this variant";
                commaNeeded = true;
            }
            if (annotation.isLeftSpecific) {
                if (commaNeeded) {
                    text = text + ", ";
                }
                text = text + annotation.leftGene;
                commaNeeded = true;
            }
            if (annotation.isRightSpecific) {
                if (commaNeeded) {
                    text = text + ", ";
                }
                text = text + annotation.rightGene;
                commaNeeded = true;
            }
            text = text + ".";
            return text;
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
                    tier: "",
                    classification: "",
                    visible: true,
                    isSelected: false,
                    breadth: "",
                };
                annotation._id = annotations[i]._id;
                if (showUser) {
                    annotation.fullName = annotations[i].fullName;
                }
                annotation.text = annotations[i].text.replace(/\n/g, "<br/>");
                annotation.scopes = [annotations[i].isCaseSpecific, annotations[i].isVariantSpecific, annotations[i].isTumorSpecific];
                annotation.scopeLevels = ["Case " + (annotations[i].isCaseSpecific ? annotations[i].caseId : ''),
                "Variant " + (annotations[i].isVariantSpecific ? this.currentVariant.chrom : ''),
                    "Tumor"];
                annotation.category = annotations[i].category;
                annotation.category = annotations[i].breadth;
                annotation.cnvGenes = annotations[i].cnvGenes ? annotations[i].cnvGenes.join(" ") : "";
                annotation.createdDate = annotations[i].createdDate;
                annotation.createdSince = annotations[i].createdSince;
                annotation.modifiedDate = annotations[i].modifiedDate;
                annotation.modifiedSince = annotations[i].modifiedSince;
                annotation.scopeTooltip = this.createLevelInformation(annotations[i]);
                annotation.tier = annotations[i].tier;
                annotation.classification = annotations[i].classification;
                annotation.isSelected = annotations[i].isSelected;
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
                    classification: "",
                    visible: true,
                    isSelected: false,
                    leftGene: "",
                    rightGene: "",
                    isLeftSpecific: false,
                    isRightSpecific: false

                };
                annotation._id = annotations[i]._id;
                if (showUser) {
                    annotation.fullName = annotations[i].fullName;
                }
                annotation.text = annotations[i].text.replace(/\n/g, "<br/>");
                annotation.scopes = [annotations[i].isCaseSpecific, annotations[i].isVariantSpecific, annotations[i].isTumorSpecific, annotations[i].isLeftSpecific, annotations[i].isRightSpecific];
                annotation.scopeLevels = ["Case " + (annotations[i].isCaseSpecific ? annotations[i].caseId : ''),
                "Variant " + (annotations[i].isVariantSpecific ? this.currentVariant.fusionName : ''),
                    "Tumor", this.currentVariant.leftGene, this.currentVariant.rightGene];
                annotation.category = annotations[i].category;
                annotation.createdDate = annotations[i].createdDate;
                annotation.createdSince = annotations[i].createdSince;
                annotation.modifiedDate = annotations[i].modifiedDate;
                annotation.modifiedSince = annotations[i].modifiedSince;
                annotation.tier = annotations[i].tier;
                annotation.classification = annotations[i].classification;
                annotation.isSelected = annotations[i].isSelected;
                annotation.leftGene = annotations[i].leftGene;
                annotation.rightGene = annotations[i].rightGene;
                annotation.scopeTooltip = this.createLevelInformation(annotations[i]);
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
        getCNVDetails(item, resetSaveFlags) {
            this.currentVariantType = "cnv";
            this.currentVariantFlags = item.iconFlags.iconFlags;
            this.currentRow = item;
            // this.loadingVariantDetails = true;

            var table; //could be the selected variant table or the regular one
            if (this.saveDialogVisible) {
                table = this.$refs.cnvVariantsSelected;
            }
            else {
                table = this.$refs.cnvDetails;
            }
            var currentIndex = table.getCurrentItemIdex(this.currentRow.oid);
            this.isFirstVariant = table.isFirstItem(currentIndex);
            this.isLastVariant = table.isLastItem(currentIndex);

            axios.get(
                webAppRoot + "/getCNVDetails",
                {
                    params: {
                        variantId: item.oid
                    }
                }).then(response => {
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
                        this.currentVariant.geneChips = geneChips.sort();
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
                                }
                            ]
                        };
                        this.variantDataTables.push(infoTable);
                        this.variantDataTables.push(infoTable2);

                        this.linkTable = [];

                        this.userAnnotations = this.currentVariant.referenceCnv.utswAnnotations.filter(a => a.userId == this.userId);
                        this.updateCNVUserAnnotations();
                        this.utswAnnotations = this.currentVariant.referenceCnv.utswAnnotations;
                        this.mdaAnnotations = "";
                        this.reloadPreviousSelectedState();
                        this.formatCNVAnnotations();
                        this.loadingVariantDetails = false;
                        if (resetSaveFlags) {
                            this.annotationSelectionUnSaved = false;
                        }
                        this.variantDetailsVisible = true;
                        this.updateVariantDetails();
                        //finally, open edit annotation
                        this.handleEditAnnotationOpening();
                        // this.$refs.variantDetailsPanel.updateCNVPlot();
                        this.updateSplashProgress();
                    } else {
                        this.loadingVariantDetails = false;
                        this.handleDialogs(response.data, this.getCNVDetails.bind(null,
                            item));
                    }
                }).catch(error => {
                    this.loadingVariantDetails = false;
                    this.handleAxiosError(error);
                });
        },
        formatCNVAnnotations() {
            this.utswAnnotationsFormatted = this.formatLocalCNVAnnotations(this.utswAnnotations, true);
            this.matchAnnotationFilter();
        },
        formatTranslocationAnnotations() {
            this.utswAnnotationsFormatted = this.formatLocalTranslocationAnnotations(this.utswAnnotations, true);
            this.matchAnnotationFilter();
        },
        selectVariantForReport() {
            if (!this.canProceed('canSelect') || this.readonly) {
                return;
            }
            this.$refs.geneVariantDetails.addToSelection(this.currentRow);
            this.handleSelectionChanged();
        },
        removeVariantFromReport() {
            if (!this.canProceed('canSelect') || this.readonly) {
                return;
            }
            this.$refs.geneVariantDetails.removeFromSelection(this.currentRow);
            this.handleSelectionChanged();
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
        updateCNVUserAnnotations() {
            this.$emit("update-cnv-user-annotations", this.userAnnotations);
        },
        getTranslocationDetails(item, resetSaveFlags) {
            this.currentVariantType = "translocation";
            this.currentVariantFlags = item.iconFlags.iconFlags;
            this.currentRow = item;
            this.loadingVariantDetails = true;

            if (this.saveDialogVisible) {
                table = this.$refs.translocationVariantsSelected;
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
                        variantId: item.oid
                    }
                }).then(response => {
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
                                label: "Left Breakpoint", value: this.currentVariant.leftBreakpoint
                            },
                            {
                                label: "Right Breakpoint", value: this.currentVariant.rightBreakpoint
                            }
                            ]
                        };
                        this.variantDataTables.push(infoTable);

                        var infoTable2 = {
                            name: "infoTable2",
                            items: [{
                                label: "Left Strand", value: this.currentVariant.leftStrand
                            },
                            {
                                label: "Right Strand", value: this.currentVariant.rightStrand
                            },
                            {
                                label: "RNA Reads", value: this.currentVariant.rnaReads ? this.currentVariant.rnaReads + "" : ""
                            },
                            {
                                label: "DNA Reads", value: this.currentVariant.dnaReads ? this.currentVariant.dnaReads + "" : ""
                            }]
                        };
                        this.variantDataTables.push(infoTable2);

                        this.linkTable = [];

                        this.userAnnotations = this.currentVariant.referenceTranslocation.utswAnnotations.filter(a => a.userId == this.userId);
                        this.updateEditTranlocationAnnotationDialogUserAnnotation();
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
                        //finally, open edit annotation
                        this.handleEditAnnotationOpening();

                        this.updateSplashProgress();
                    } else {
                        this.loadingVariantDetails = false;
                        this.handleDialogs(response.data, this.getTranslocationDetails.bind(null,
                            item));
                    }
                }).catch(error => {
                    this.loadingVariantDetails = false;
                    this.handleAxiosError(error);
                });
        },
        updateEditSNPAnnotationDialogUserAnnotation() {
            this.$emit("update-edit-snp-user-annotation", this.userAnnotations);
        },
        updateEditCNVAnnotationDialogUserAnnotation() {
            this.$emit("update-edit-cnv-user-annotation", this.userAnnotations);
        },
        updateEditTranlocationAnnotationDialogUserAnnotation() {
            this.$emit("update-edit-ftl-user-annotation", this.userAnnotations);
        },
        isSNP() {
            return this.currentVariantType == "snp";
        },
        isCNV() {
            return this.currentVariantType == "cnv";
        },
        isTranslocation() {
            return this.currentVariantType == "translocation";
        },
        closingVariantDetailsConfirmationDialog() {
            if ((this.$refs.variantDetailsPanel && this.$refs.variantDetailsPanel.variantDetailsUnSaved) || this.annotationSelectionUnSaved) {
                this.confirmationVariantDialogVisible = true;
            }
            else {
                this.closeVariantDetails();
            }
        },
        closeAndSaveAllVariantDetailsConfirmationDialog() {
            this.handleSaveAll();
            this.closeVariantDetails();
        },
        closeVariantDetails() {
            this.confirmationVariantDialogVisible = false;
            this.variantDetailsVisible = false;
            this.urlQuery.variantId = null;
            this.urlQuery.variantType = null;
            this.urlQuery.edit = false; //also close edit but it should have been done earlier
            this.updateRoute();
        },
        handleSaveAll() {
            this.$emit("save-all");
        },
        loadPrevVariant() {
            var table; //could be the selected table or the regular one
            if (this.isSNP()) {
                if (this.saveDialogVisible) {
                    table = this.$refs.snpVariantsSelected;
                }
                else {
                    table = this.$refs.geneVariantDetails;
                }
                var prevVariant = table.getPreviousItem(this.currentRow);
                if (prevVariant) {
                    this.getVariantDetails(prevVariant);
                }
            }
            else if (this.isCNV()) {
                if (this.saveDialogVisible) {
                    table = this.$refs.cnvVariantsSelected;
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
                if (this.saveDialogVisible) {
                    table = this.$refs.translocationVariantsSelected;
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
                if (this.saveDialogVisible) {
                    table = this.snpVariantsSelected;
                }
                else {
                    table = this.geneVariantDetails;
                }
                var prevVariant = table.getNextItem(this.currentRow);
                if (prevVariant) {
                    this.getVariantDetails(prevVariant);
                }
            }
            else if (this.isCNV()) {
                if (this.saveDialogVisible) {
                    table = this.$refs.cnvVariantsSelected;
                }
                else {
                    table = this.$refs.cnvDetails;
                }
                var prevVariant = table.getNextItem(this.currentRow);
                if (prevVariant) {
                    this.getCNVDetails(prevVariant);
                }
            }
            else if (this.isTranslocation()) {
                if (this.saveDialogVisible) {
                    table = this.$refs.translocationVariantsSelected;
                }
                else {
                    table = this.$refs.translocationDetails;
                }
                var prevVariant = table.getNextItem(this.currentRow);
                if (prevVariant) {
                    this.getTranslocationDetails(prevVariant);
                }
            }
        },
        saveVariant(skipSnackBar) {
            this.$emit("save-variant", skipSnackBar);
        },
        handlePanelVisibility(visible) {
            this.$emit("handle-panel-visibility", visible);
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

