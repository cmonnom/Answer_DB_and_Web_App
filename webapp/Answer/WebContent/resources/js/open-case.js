const OpenCase = {
    props: {
        "readonly": { default: true, type: Boolean }

    },
    template: `<div>
    <v-dialog v-model="confirmationDialogVisible" max-width="300px">
        <v-card>
            <v-card-text v-html="confirmationMessage" class="pl-2 pr-2 subheading">

            </v-card-text>
            <v-card-actions class="card-actions-bottom">
                <v-btn color="primary" @click="proceedWithConfirmation" slot="activator">{{ confirmationProceedButton }}
                </v-btn>
                <v-btn color="error" @click="cancelConfirmation" slot="activator">{{ confirmationCancelButton }}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
    <v-snackbar :timeout="4000" :bottom="true" v-model="snackBarVisible">
        {{ snackBarMessage }}
        <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
    </v-snackbar>
    <advanced-filter ref="advancedFilter" @refresh-data="filterData" @save-filters="saveCurrentFilters" @delete-filter="deleteFilterSet"></advanced-filter>
    <v-dialog v-model="saveDialogVisible" scrollable fullscreen hide-overlay transition="dialog-bottom-transition">
        <v-card class="soft-grey-background">
            <v-toolbar dense dark :color="colors.saveReview">
                <v-menu offset-y offset-x class="ml-0">
                    <v-btn slot="activator" flat icon dark>
                        <v-icon>more_vert</v-icon>
                    </v-btn>
                    <v-list>

                    <v-list-tile avatar @click="saveSelection()" :disabled="saveVariantDisabled || saveLoading">
                    <v-list-tile-avatar>
                        <v-icon>save</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Save Selected Variants</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>

                        <v-list-tile avatar @click="exportSelectedVariants()" :disabled="saveVariantDisabled || exportLoading">
                            <v-list-tile-avatar>
                                <v-icon>file_download</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Export to Excel</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile avatar @click="sendToMDA()" :disabled="saveVariantDisabled || sendToMDALoading">
                            <v-list-tile-avatar>
                                <v-icon>send</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Send to MD Anderson</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                       

                        <v-list-tile avatar @click="closeSaveDialog(true)">
                            <v-list-tile-avatar>
                                <v-icon>close</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Close</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>
                    </v-list>
                </v-menu>
                <v-toolbar-title>Review Selected Variants for {{ caseName }}
                </v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon :disabled="saveVariantDisabled" @click="saveSelection()" slot="activator" :loading="saveLoading">
                        <v-icon>save</v-icon>
                    </v-btn>
                    <span>Save Selected Variants</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <v-btn icon :disabled="saveVariantDisabled" @click="exportSelectedVariants()" slot="activator" :loading="exportLoading">
                        <v-icon>file_download</v-icon>
                    </v-btn>
                    <span>Export to Excel</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <v-btn icon :disabled="saveVariantDisabled" @click="sendToMDA()" slot="activator" :loading="sendToMDALoading">
                        <v-icon>send</v-icon>
                    </v-btn>
                    <span>Send To MD Anderson</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <v-btn icon @click="closeSaveDialog(true)" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close</span>
                </v-tooltip>
            </v-toolbar>
            <v-card-text :style="getDialogMaxHeight(120)">
                <v-card v-show="!areReportableGeneSelected()" class="mt-2 mb-2">
                    <v-card-text>
                        The following genes should be included in the report if pathogenic or likely pathogenic :
                        <v-tooltip bottom v-for="(reportGroup, index1) in reportGroups" :key="index1">
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
                    initial-sort="chromPos" no-data-text="No Data" :show-row-count="true" class="pb-3"
                    :color="colors.saveReview">
                </data-table>
                <data-table ref="cnvVariantsSelected" :fixed="false" :fetch-on-created="false" table-title="Selected CNVs" initial-sort="chrom"
                    no-data-text="No Data" :show-row-count="true" class="pb-3"
                    :color="colors.saveReview">
                </data-table>
                <data-table ref="translocationVariantsSelected" :fixed="false" :fetch-on-created="false" table-title="Selected Translocations"
                    initial-sort="fusionName" no-data-text="No Data" :show-row-count="true" class="pb-3"
                    :color="colors.saveReview">
                </data-table>
            </v-card-text>
            <v-card-actions class="card-actions-bottom">
                <v-tooltip top>
                    <v-btn color="success" :disabled="saveVariantDisabled" @click="saveSelection()" slot="activator" :loading="saveLoading">Save
                        <v-icon right dark>save</v-icon>
                    </v-btn>
                    <span>Save Selected Variants</span>
                </v-tooltip>
                <v-tooltip top>
                    <v-btn color="primary" :disabled="saveVariantDisabled" @click="exportSelectedVariants()" slot="activator" :loading="exportLoading">Excel
                        <v-icon right dark>file_download</v-icon>
                    </v-btn>
                    <span>Export to Excel</span>
                </v-tooltip>
                <v-tooltip top>
                    <v-btn color="primary" :disabled="saveVariantDisabled" @click="sendToMDA()" slot="activator" :loading="sendToMDALoading">Send to MDA
                        <v-icon right dark>send</v-icon>
                    </v-btn>
                    <span>Send to MD Anderson</span>
                </v-tooltip>
                <v-btn color="error" @click="closeSaveDialog(true)" slot="activator">Cancel
                    <v-icon right dark>cancel</v-icon>
                </v-btn>
                <!-- <breadcrumbs>
                </breadcrumbs> -->
            </v-card-actions>
        </v-card>
    </v-dialog>

    <!-- annotation dialog -->
    <edit-annotations type="snp" @saving-annotations="commitAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs" :color="colors.editAnnotation"
        ref="annotationDialog" :title="currentVariant.geneName + ' ' + currentVariant.notation + ' -- ' + caseName + ' --'"></edit-annotations>

    <edit-annotations type="cnv" @saving-annotations="commitAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs" :color="colors.editAnnotation"
        ref="cnvAnnotationDialog" :title="currentVariant.chrom  + ' -- ' + caseName + ' --'"" @toggle-panel="handlePanelVisibility()">
        <v-slide-y-transition slot="variantDetails">
            <v-flex xs12 md12 lg11 xl10 mb-2 v-show="editAnnotationVariantDetailsVisible">
                <variant-details :no-edit="true" :variant-data-tables="variantDataTables" :link-table="linkTable"
                :widthClass="getWidthClassForVariantDetails()" :current-variant="currentVariant" @hide-panel="handlePanelVisibility(false)"
                @show-panel="handlePanelVisibility(true)" @toggle-panel="handlePanelVisibility()"
                @revert-variant="revertVariant" @save-variant="saveVariant" :color="colors.editAnnotation"
                ref="cnvVariantDetailsPanel">

                </variant-details>
            </v-flex>
        </v-slide-y-transition>
                       
        </edit-annotations>

    <edit-annotations type="translocation" @saving-annotations="commitAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs" :color="colors.editAnnotation"
        ref="translocationAnnotationDialog" :title="currentVariant.chrom  + ' -- ' + caseName + ' --'""></edit-annotations>

    <!-- variant details dialog -->
    <v-dialog v-model="variantDetailsVisible" scrollable fullscreen hide-overlay transition="dialog-bottom-transition">
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
                                <v-icon>note_add</v-icon>
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

                        <v-list-tile avatar @click="closeVariantDetails(true)">
                            <v-list-tile-avatar>
                                <v-icon>cancel</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Close Variant</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>


                    </v-list>
                </v-menu>
                <v-toolbar-title class="ml-0">Annotations for Variant:
                    <span v-if="isSNP()">{{ currentVariant.geneName }} {{ currentVariant.notation }}</span>
                    <span v-if="isCNV()">{{ currentVariant.chrom }}</span>
                    <span v-if="isTranslocation()">{{ currentVariant.fusionName }}</span>
                    <span> -- {{ caseName }} -- </span>
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
                    <v-btn icon @click="closeVariantDetails(true)" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close Variant</span>
                </v-tooltip>

            </v-toolbar>
            <v-card-text :style="getDialogMaxHeight(120)">
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
                            <v-flex xs12 md12 lg11 xl10 v-show="annotationVariantDetailsVisible">

                            <variant-details :no-edit="false" :variant-data-tables="variantDataTables" :link-table="linkTable"
                            :widthClass="getWidthClassForVariantDetails()" :current-variant="currentVariant" @hide-panel="handlePanelVisibility(false)"
                            @show-panel="handlePanelVisibility(true)" @toggle-panel="handlePanelVisibility()"
                            @revert-variant="revertVariant" @save-variant="saveVariant" :color="colors.variantDetails"
                            ref="variantDetailsPanel">
                            </variant-details>

                            </v-flex>
                        </v-slide-y-transition>
                        <v-slide-y-transition>
                            <v-flex xs12 sm12 md9 lg7 xl5 v-show="isRelatedVariantsVisible()">
                                <div>
                                    <data-table ref="relatedVariantAnnotation" :fixed="false" :fetch-on-created="false" table-title="Related Variants" initial-sort="geneId"
                                        no-data-text="No Data" :show-pagination="false" title-icon="link"
                                        :color="colors.variantDetails">
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
                                    no-data-text="No Data" :show-row-count="true" title-icon="mdi-table-search"
                                    :color="colors.variantDetails">
                                </data-table>
                            </v-flex>
                        </v-slide-y-transition>
                        <!-- MDA Annotation card -->
                        <v-slide-y-transition>
                            <v-flex xs12 v-show="mdaAnnotationsVisible && mdaAnnotationsExists()">
                                <v-card>
                                    <v-toolbar dense dark :color="colors.variantDetails">
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
                                    <v-card-text v-for="(annotationCategory, index) in mdaAnnotations.annotationCategories" :key="index">
                                        <v-card flat v-if="annotationCategory">
                                            <v-card-title class="subheading">{{ annotationCategory.title }}:</v-card-title>
                                            <v-card-text class="pl-2 pr-2">{{ annotationCategory.text }} </v-card-text>
                                        </v-card>
                                    </v-card-text>
                                </v-card>
                            </v-flex>
                        </v-slide-y-transition>
                        <v-slide-y-transition>
                            <v-flex xs12 v-show="utswAnnotationsVisible && utswAnnotationsExists()">
                                <v-card class="soft-grey-background">
                                    <v-toolbar dense dark :color="colors.variantDetails">
                                        <v-toolbar-title>
                                            <v-icon color="amber accent-2">mdi-message-bulleted</v-icon>
                                            UTSW Annotations
                                        </v-toolbar-title>
                                        <v-spacer></v-spacer>
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
                                                <v-flex xs12 sm12 md6 lg6 xl4 v-for="(annotation, index) in utswAnnotationsFormatted" :key="index">
                                                    <utsw-annotation-card :annotation="annotation" :variant-type="currentVariantType"></utsw-annotation-card>
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
                        <v-icon right dark>note_add</v-icon>
                    </v-btn>
                    <span>Create/Edit Your Annotations</span>
                </v-tooltip>
                <v-btn v-if="!currentRow.isSelected" :disabled="saveDialogVisible || !canProceed('canSelect') || readonly" color="success" @click="selectVariantForReport()" slot="activator">Select Variant
                    <v-icon right dark>done</v-icon>
                </v-btn>
                <v-btn v-if="currentRow.isSelected" :disabled="saveDialogVisible || !canProceed('canSelect') || readonly" color="warning" @click="removeVariantFromReport()" slot="activator">Deselect Variant
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
                <v-btn color="error" @click="closeVariantDetails(true)">Close
                    <v-icon right dark>cancel</v-icon>
                </v-btn>
                <!-- <breadcrumbs>
                </breadcrumbs> -->
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

                    <v-list-tile avatar @click="openSaveDialog()">
                        <v-list-tile-avatar>
                            <v-icon>mdi-clipboard-check</v-icon>
                        </v-list-tile-avatar>
                        <v-list-tile-content>
                            <v-list-tile-title>Review Variants Selected</v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>
                </v-list>
            </v-menu>
            <span>Case Menu</span>
        </v-tooltip>
        <v-toolbar-title class="white--text ml-0">
            Working on case: {{ caseName }}
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
        <v-badge color="red" right bottom overlap v-model="variantUnSaved" class="mini-badge">
            <v-icon slot="badge"></v-icon>
            <v-tooltip bottom>
                <v-btn flat icon @click="openSaveDialog()" slot="activator" :color="saveDialogVisible ? 'amber accent-2' : ''">
                    <v-icon>mdi-clipboard-check</v-icon>
                </v-btn>
                <span>Review Variants Selected</span>
            </v-tooltip>
        </v-badge>
        <v-progress-linear class="ml-4 mr-4" :slot="loadingVariantDetails ? 'extension' : ''" v-show="!caseName || loadingVariantDetails"
            :indeterminate="true" color="white"></v-progress-linear>
    </v-toolbar>

    <v-slide-y-transition>
        <v-layout v-if="patientDetailsVisible">
            <v-flex xs12 md12 lg10 xl9>
                <div class="text-xs-center pb-3">
                    <v-card>
                        <v-toolbar dense dark :color="colors.openCase">
                            <!-- <v-icon>perm_identity</v-icon> -->
                            <v-menu offset-y offset-x class="ml-0">
                                            <v-btn slot="activator" flat icon dark>
                                                <v-icon color="amber accent-2">assignment_ind</v-icon>
                                            </v-btn>
                                            <v-list>
                                                <v-list-tile avatar @click="savePatientDetails()" :disabled="!canProceed('canAnnotate') || readonly">
                                                    <v-list-tile-avatar>
                                                        <v-icon>save</v-icon>
                                                    </v-list-tile-avatar>
                                                    <v-list-tile-content>
                                                        <v-list-tile-title>Save Patient Details</v-list-tile-title>
                                                    </v-list-tile-content>
                                                </v-list-tile>

                                                <v-list-tile avatar @click="getPatientDetails()" :disabled="!canProceed('canAnnotate') || readonly">
                                                    <v-list-tile-avatar>
                                                        <v-icon>settings_backup_restore</v-icon>
                                                    </v-list-tile-avatar>
                                                    <v-list-tile-content>
                                                        <v-list-tile-title>Restore From Last Saved</v-list-tile-title>
                                                    </v-list-tile-content>
                                                </v-list-tile>

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
                                        <v-badge color="red" right bottom overlap v-model="patientDetailsUnSaved" class="mini-badge">
                                            <v-icon slot="badge"></v-icon>
                                            <v-tooltip bottom>
                                                <v-btn flat icon @click="savePatientDetails()" slot="activator" :loading="savingPatientDetails" :disabled="!canProceed('canAnnotate')  || readonly">
                                                    <v-icon>save</v-icon>
                                                </v-btn>
                                                <span>Save Patient Details</span>
                                            </v-tooltip>
                                        </v-badge>
                                        <v-tooltip bottom>
                                            <v-btn flat icon @click="getPatientDetails()" slot="activator" :disabled="!canProceed('canAnnotate') || readonly">
                                                <v-icon>settings_backup_restore</v-icon>
                                            </v-btn>
                                            <span>Restore Last Saved Patient Details</span>
                                        </v-tooltip>
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
                                                        <v-layout class="full-width">
                                                            <v-flex xs5 class="text-xs-left grow">
                                                                <span class="selectable">{{ item.label }}:</span>
                                                            </v-flex>
                                                            <v-flex :class="[item.type ? 'xs5' : 'xs7','text-xs-right', 'grow', 'blue-grey--text', 'text--lighten-1']">
                                                                <span v-if="item.type == null" class="selectable">{{ item.value }}</span>
                                                                <v-text-field :disabled="!canProceed('canAnnotate') || readonly" v-if="item.type == 'text'" class="pt-2"
                                                                value="patientDetailsOncoTreeDiagnosis" v-model="patientDetailsOncoTreeDiagnosis" hide-details
                                                                @input="patientDetailsUnSaved = true">
                                                                </v-text-field>
                                                            </v-flex>
                                                            <v-flex xs2 v-if="item.type == 'text'">
                                                            <v-tooltip bottom>
                                                            <v-btn flat color="primary" icon @click="openOncoTree()" slot="activator">
                                                                <v-icon> open_in_new</v-icon>
                                                            </v-btn>
                                                            <span>Open OncoTree in New Tab</span>
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
            <v-flex xs12 class="pb-3">
                <v-card>
                    <v-toolbar dense dark :color="colors.openCase">
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
                    <v-card-text>
                        <v-text-field :textarea="true" :readonly="!canProceed('canAnnotate') || readonly" :disabled="!canProceed('canAnnotate') || readonly" v-model="caseAnnotation.caseAnnotation" class="mr-2 no-height" label="Write your comments here">
                        </v-text-field>
                    </v-card-text>
                    <v-card-actions class="card-actions-bottom">
                        <v-tooltip bottom>
                            <v-btn :disabled="!canProceed('canAnnotate') || loadingVariantDetails || isCaseAnnotationUnchanged() || readonly" slot="activator" color="success" @click="saveCaseAnnotations()">Save
                                <v-icon right dark>save</v-icon>
                            </v-btn>
                            <span>Save/Update Annotation</span>
                        </v-tooltip>
                        <v-tooltip bottom>
                            <v-btn :disabled="!canProceed('canAnnotate') || loadingVariantDetails || isCaseAnnotationUnchanged() || readonly" slot="activator" color="error" @click="loadCaseAnnotations()">Discard Changes
                                <v-icon right dark>cancel</v-icon>
                            </v-btn>
                            <span>Revert to previous annotation</span>
                        </v-tooltip>
                    </v-card-actions>
                </v-card>
            </v-flex>
        </v-layout>
    </v-slide-y-transition>

    <v-slide-y-transition>
        <v-tabs slot="extension" dark slider-color="warning" color="primary darken-1" fixed-tabs v-model="variantTabActive">
            <v-tab href="#tab-snp">
                SNP / Indel
            </v-tab>
            <v-tab href="#tab-cnv">
                CNV
            </v-tab>
            <v-tab href="#tab-translocation">
                Fusion / Translocation
            </v-tab>
            <v-tabs-items v-model="variantTabActive">
                <!-- SNP / Indel table -->
                <v-tab-item id="tab-snp">
                    <data-table ref="geneVariantDetails" :fixed="false" :fetch-on-created="false" table-title="SNP/Indel Variants" initial-sort="chromPos"
                        no-data-text="No Data" :enable-selection="canProceed('canSelect') && !readonly" :show-row-count="true" @refresh-requested="handleRefresh()"
                        :show-left-menu="true" @showing-buttons="toggleGeneVariantDetailsButtons" @datatable-selection-changed="handleSelectionChanged"
                        :color="colors.openCase">
                        <v-fade-transition slot="action1">
                            <v-tooltip bottom v-show="geneVariantDetailsTableHovering">
                                <v-btn slot="activator" flat icon @click="toggleFilters" :color="isAdvancedFilteringVisible() ? 'amber accent-2' : 'white'">
                                    <v-icon>filter_list</v-icon>
                                </v-btn>
                                <span>Advanced Filtering</span>
                            </v-tooltip>
                        </v-fade-transition>
                        <v-list-tile avatar @click="toggleFilters" slot="action1MenuItem">
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
                <v-tab-item id="tab-cnv">
                    <data-table ref="cnvDetails" :fixed="false" :fetch-on-created="false" table-title="CNVs" initial-sort="chrom" no-data-text="No Data"
                        :enable-selection="canProceed('canSelect')" :show-row-count="true" @refresh-requested="handleRefresh()" :show-left-menu="true"
                        @datatable-selection-changed="handleSelectionChanged"
                        :color="colors.openCase">
                    </data-table>
                </v-tab-item>
                <!--  Fusion / Translocation table -->
                <v-tab-item id="tab-translocation">
                    <data-table ref="translocationDetails" :fixed="false" :fetch-on-created="false" table-title="Fusions / Translocations" initial-sort="fusionName"
                        no-data-text="No Data" :enable-selection="canProceed('canSelect')" :show-row-count="true" @refresh-requested="handleRefresh()"
                        :show-left-menu="true" @datatable-selection-changed="handleSelectionChanged"
                        :color="colors.openCase">
                    </data-table>
                </v-tab-item>
            </v-tabs-items>
        </v-tabs>
    </v-slide-y-transition>

</div>`, data() {
        return {
            firstTimeLoading: true,
            loading: true,
            loadingVariantDetails: false,
            // breadcrumbs: [{text: "You are here:  Case", disabled: true}],
            breadcrumbItemVariantDetails: { text: "Variant Details", disabled: true, closingFunction: "closeVariantDetails" },
            breadcrumbItemReview: { text: "Review Selected Variants", disabled: true, closingFunction: "closeSaveDialog" },
            breadcrumbItemEditAnnotation: { text: "Add / Edit Annotation", disabled: true, closingFunction: "cancelAnnotations" },
            patientTables: [],
            patientDetailsVisible: false,
            caseAnnotationsVisible: false,
            variantTabsVisible: false,
            caseName: "",
            caseId: "",
            variantDetailsVisible: false,
            currentVariant: {},
            currentRow: {},
            currentVariantFlags: [],
            variantDataTables: [],
            linkTable: [],
            saveDialogVisible: false,
            annotationVariantDetailsVisible: true,
            annotationVariantRelatedVisible: true,
            annotationVariantCanonicalVisible: true,
            annotationVariantOtherVisible: false,
            saveVariantDisabled: false,
            variantUnSaved: false,
            // annotationDialogVisible: false,
            userAnnotations: [],
            snackBarMessage: "",
            snackBarVisible: false,
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
            variantTabActive: "tab-snp",
            wasAdvancedFilteringVisibleBeforeTabChange: false,
            currentVariantHasRelatedVariants: false,
            isFirstVariant: false,
            isLastVariant: false,
            variantTiers: [
                '1A',
                '1B',
                '2A',
                '2B',
                '3',
                '4',
                '5'],
            variantDetailsUnSaved: false,
            patientDetailsUnSaved: false,
            savingVariantDetails: false,
            savingPatientDetails: false,
            tempSelectedSNPVariants: [],
            tempSelectedCNVs: [],
            tempSelectedTranslocations: [],
            topMostDialog: "",
            patientDetailsOncoTreeDiagnosis: "",
            qcUrl: "",
            editAnnotationVariantDetailsVisible: true,
            urlQuery: {
                showReview: false,
                variantId: null,
                variantType: null,
                edit: false
            },
            colors: {
                openCase: "primary",
                variantDetails: "teal lighten-1",
                saveReview: "teal",
                editAnnotation: "teal darken-1"
            }
        }
    }, methods: {
        canProceed(field) {
            if (isAdmin) {
                return true;
            }
            switch(field) {
                case "canAnnotate": return permissions.canAnnotate;
                case "canSelect": return permissions.canSelect;
                case "canView": return permissions.canView;
                default: return false;
            }
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
                bus.$emit("not-allowed", [this.reponse]);
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
                    this.patientTables = response.data.patientInfo.patientTables;
                    this.caseName = response.data.caseName + " (" + this.patientTables[0].items[0].value + ")"; //careful when swapping item positions
                    this.patientDetailsOncoTreeDiagnosis = this.patientTables[2].items[0].value; //careful when swapping item positions
                    this.caseId = response.data.caseId;
                    this.qcUrl = response.data.qcUrl + this.caseId + "?isLimsId=true";
                    this.addCustomWarningFlags(response.data.snpIndelVariantSummary);
                    this.$refs.geneVariantDetails.manualDataFiltered(response.data.snpIndelVariantSummary);
                    this.$refs.cnvDetails.manualDataFiltered(response.data.cnvSummary);
                    this.$refs.translocationDetails.manualDataFiltered(response.data.translocationSummary);
                    this.$refs.advancedFilter.effects = response.data.effects;
                    this.userId = response.data.userId;
                    this.$refs.advancedFilter.populateCheckBoxes();
                    this.$refs.advancedFilter.filterNeedsReload = false;
                    this.addSNPIndelHeaderAction(response.data.snpIndelVariantSummary.headers);
                    this.addCNVHeaderAction(response.data.cnvSummary.headers);
                    this.addFusionHeaderAction(response.data.translocationSummary.headers);
                    this.reportGroups = response.data.reportGroups;
                    this.$refs.advancedFilter.reportGroups = this.reportGroups;
                    //only show hidden elements if it's the 1st time the page
                    //loads
                    //otherwise keep user's preference
                    if (this.firstTimeLoading) {
                        this.firstTimeLoading = false;
                        this.patientDetailsVisible = true;
                        setTimeout(() => {
                            this.caseAnnotationsVisible = true;
                        }, 500);
                        setTimeout(() => {
                            this.variantTabsVisible = true;
                        }, 1000);
                        
                        setTimeout(() => {
                            this.loadFromParams();
                        }, 500);
                    }
                }
                else {
                    this.handleDialogs(response.data, this.getAjaxData);
                }
                this.loadingVariantDetails = false;
                this.$refs.advancedFilter.loading = false;
                this.$emit("get-case-details-done");
            }
            ).catch(error => {
                this.loadingVariantDetails = false;
                this.$refs.advancedFilter.loading = false;
                console.log(error);
                bus.$emit("some-error", [this, error]);
            }
            );
        },
        loadFromParams() {
            this.urlQuery.variantId = this.$route.query.variantId;
            this.urlQuery.variantType = this.$route.query.variantType;
            this.urlQuery.showReview = this.$route.query.showReview === true || this.$route.query.showReview === "true";
            this.urlQuery.edit = this.$route.query.edit === true;

            if (!this.urlQuery.showReview) { //close save/review dialog
                this.closeSaveDialog();
            }
            if (!this.urlQuery.variantId) { //close variant details
                this.closeVariantDetails();
            }
            if (!this.urlQuery.edit) { //close edit anntation
                if (this.isSNP()) {
                    this.$refs.annotationDialog.cancelAnnotations();
                }
                else if (this.isCNV()) {
                    this.$refs.cnvAnnotationDialog.cancelAnnotations();
                }
                else if (this.isTranslocation()) {
                    this.$refs.translocationAnnotationDialog.cancelAnnotations();
                }
            }

            //first open save/review dialog
            if (this.urlQuery.showReview) {
                this.openSaveDialog();
            }
            //then open variant details
            if (this.urlQuery.variantId && this.urlQuery.variantType) {
                //find item
                if (this.urlQuery.variantType == 'snp') {
                    for (var i = 0; i < this.$refs.geneVariantDetails.items.length; i++) {
                        if (this.$refs.geneVariantDetails.items[i].oid == this.urlQuery.variantId) {
                            this.getVariantDetails(this.$refs.geneVariantDetails.items[i]);
                            break;
                        }
                    }
                }
                else if (this.urlQuery.variantType == 'cnv') {
                    for (var i = 0; i < this.$refs.cnvDetails.items.length; i++) {
                        if (this.$refs.cnvDetails.items[i].oid == this.urlQuery.variantId) {
                            this.getCNVDetails(this.$refs.cnvDetails.items[i]);
                            break;
                        }
                    }
                }
                else if (this.urlQuery.variantType == 'translocation') {
                    for (var i = 0; i < this.$refs.translocationDetails.items.length; i++) {
                        if (this.$refs.translocationDetails.items[i].oid == this.urlQuery.variantId) {
                            this.getTranslocationDetails(this.$refs.translocationDetails.items[i]);
                            break;
                        }
                    }
                }
            }
            //finally, open edit annotation
            if (this.urlQuery.edit === "true") {
                this.startUserAnnotations();
            }
        },
        addCustomWarningFlags(snpIndelVariantSummary) {
            for (var i = 0; i < snpIndelVariantSummary.items.length; i++) {
                var item = snpIndelVariantSummary.items[i];
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
                    console.log(error);
                    bus.$emit("some-error", [this, error]);
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
            if (this.variantTabActive != "tab-snp") { //remember if filter was visible or not before the change
                // this.wasAdvancedFilteringVisibleBeforeTabChange = this.$refs.advancedFilter.advancedFilteringVisible;
                // if (this.wasAdvancedFilteringVisibleBeforeTabChange) {
                //     this.$refs.advancedFilter.toggleFilters(); //hide filtering because of tab change
                // }
                this.$refs.advancedFilter.disableFiltering = true;
            }
            if (this.$refs.advancedFilter && this.variantTabActive == "tab-snp") { //restore the previous visibility of the filter
                // this.$refs.advancedFilter.toggleFilters(); //show filtering because it was previsously visible
                this.$refs.advancedFilter.disableFiltering = false;
            }
        },
        filterData() {
            this.getAjaxData();
        },
        toggleFilters() {
            this.$refs.advancedFilter.toggleFilters();
        },
        addSNPIndelHeaderAction(headers) {
            for (var i = 0; i < headers.length; i++) {
                if (headers[i].value == "chromPos") {
                    headers[i].itemAction = this.openVariant;
                    headers[i].actionIcon = "zoom_in";
                    headers[i].actionTooltip = "Variant Details";
                    break;
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
        getVariantDetails(item) {
            this.currentVariantType = "snp";
           
            this.currentVariantFlags = item.iconFlags.iconFlags;
            this.currentRow = item;
            var table; //could be the selected variant table or the regular one
            if (this.saveDialogVisible) {
                table = this.$refs.snpVariantsSelected;
            }
            else {
                table = this.$refs.geneVariantDetails;
            }
            var currentIndex = table.getCurrentItemIdex(this.currentRow.oid);
            this.isFirstVariant = table.isFirstItem(currentIndex);
            this.isLastVariant = table.isLastItem(currentIndex);


            this.loadingVariantDetails = true;
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
                                        + this.currentVariant.pos
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
                                    label: "Tier",
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
                        this.mdaAnnotations = this.currentVariant.mdaAnnotation ? this.currentVariant.mdaAnnotation : "";
                        this.formatAnnotations();
                        this.loadingVariantDetails = false;
                        this.toggleHTMLOverlay(true);
                        this.variantDetailsVisible = true;
                    } else {
                        this.loadingVariantDetails = false;
                        this.handleDialogs(response.data, this.getVariantDetails.bind(null,
                            item));
                    }
                }).catch(error => {
                    this.loadingVariantDetails = false;
                    console.log(error);
                    bus.$emit("some-error", [this, error]);
                });
        },
        getCNVDetails(item) {
            this.currentVariantType = "cnv";
            this.currentVariantFlags = item.iconFlags.iconFlags;
            this.currentRow = item;
            this.loadingVariantDetails = true;

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
                        var infoTable = {
                            name: "infoTable",
                            items: [
                                {
                                    label: "Chromosome", value: this.currentVariant.chrom
                                },
                                {
                                    label: "Genes", type: "chip", value: this.currentVariant.genes.sort()
                                },
                                {
                                    label: "Start", value: this.currentVariant.startFormatted
                                },
                                {
                                    label: "End", value: this.currentVariant.endFormatted
                                },
                                {
                                    label: "Aberration Type", value: this.currentVariant.aberrationType
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

                        this.linkTable = [];

                        this.userAnnotations = this.currentVariant.referenceCnv.utswAnnotations.filter(a => a.userId == this.userId);
                        this.$refs.cnvAnnotationDialog.userAnnotations = this.userAnnotations;
                        this.utswAnnotations = this.currentVariant.referenceCnv.utswAnnotations;
                        this.formatCNVAnnotations();
                        this.loadingVariantDetails = false;
                        this.toggleHTMLOverlay(true);
                        this.variantDetailsVisible = true;
                    } else {
                        this.loadingVariantDetails = false;
                        this.handleDialogs(response.data, this.getCNVDetails.bind(null,
                            item));
                    }
                }).catch(error => {
                    this.loadingVariantDetails = false;
                    console.log(error);
                    bus.$emit("some-error", [this, error]);
                });
        },
        getTranslocationDetails(item) {
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
                        this.$refs.translocationAnnotationDialog.userAnnotations = this.userAnnotations;
                        this.utswAnnotations = this.currentVariant.referenceTranslocation.utswAnnotations;
                        this.formatTranslocationAnnotations();
                        this.loadingVariantDetails = false;
                        this.toggleHTMLOverlay(true);
                        this.variantDetailsVisible = true;
                    } else {
                        this.loadingVariantDetails = false;
                        this.handleDialogs(response.data, this.getTranslocationDetails.bind(null,
                            item));
                    }
                }).catch(error => {
                    this.loadingVariantDetails = false;
                    console.log(error);
                    bus.$emit("some-error", [this, error]);
                });
        },
        //determines if the regular variant details label should be used
        //like Gene, Notation Nb.Cases Seen etc.
        //so that it behaves like a regular "label: string" combo
        isRegularVariantDetailsLabel(type) {
            return !type || type == 'chip' || type == 'callSet' || type == 'flag';
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
        updateVariantVcfAnnotationTable() {
            var items = this.currentVariant.vcfAnnotations;
            var headers = this.vcfAnnotationHeaders;
            var headerOrder = this.vcfAnnotationHeadersOrder;
            this.$refs.snpVariantsSelected.manualDataFiltered(
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
        openVariant(item) {
            this.getVariantDetails(item);
        },
        openCNV(item) {
            this.getCNVDetails(item);
        },
        openTranslocation(item) {
            this.getTranslocationDetails(item);
        },
        handleIdLink(id) {
            var link = "";
            if (id.type == "various") {
                if (id.value.indexOf('rs') == 0) {
                    link = "https://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=" + id.value;
                }
                else if (id.value.indexOf('COSM') == 0) {
                    link = "https://cancer.sanger.ac.uk/cosmic/ncv/overview?id=" + id.value.replace("COSM", "");
                }
                else if (id.value.indexOf('COSN') == 0) {
                    link = "https://cancer.sanger.ac.uk/cosmic/ncv/overview?id=" + id.value.replace("COSN", "");
                }
                else { //Clinvar
                    link = "https://www.ncbi.nlm.nih.gov/clinvar/variation/" + id.value;
                }
            }
            else if (id.type == "oncoKB") {
                if (id.subtype == "gene") {
                    link = "http://oncokb.org/#/gene/" + this.currentVariant.oncokbGeneName;
                }
                else if (id.subtype == "variant") {
                    link = "http://oncokb.org/#/gene/" + this.currentVariant.oncokbGeneName + "/variant/" + this.currentVariant.oncokbVariantName;
                }
            }
            window.open(link, "_blank");
        },
        // handlePubMedIdLink(id) {
        //     var link = "https://www.ncbi.nlm.nih.gov/pubmed/?term=" + id;
        //     window.open(link, "_blank");
        // },
        // handleNCTIdLink(id) {
        //     var link = "https://clinicaltrials.gov/ct2/show/" + id;
        //     window.open(link, "_blank");
        // },
        handleOncoKBGeneLink(id) {

        },
        openLink(link) {
            window.open(link, "_blank");
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
                return (Math.round(parseFloat(value) * 100000) / 1000) + "%";
            } return "";
        },
        formatLocalAnnotations(annotations, showUser) {
            var formatted = [];
            for (var i = 0; i < annotations.length; i++) {
                var annotation = {
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
                    classification: ""
                };
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
                annotation.scopeTooltip = this.$refs.annotationDialog.createLevelInformation(annotations[i]);
                annotation.tier = annotations[i].tier;
                annotation.classification = annotations[i].classification;
                formatted.push(annotation);
            }
            return formatted;
        },
        formatLocalCNVAnnotations(annotations, showUser) {
            var formatted = [];
            for (var i = 0; i < annotations.length; i++) {
                var annotation = {
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
                    cnvGenes: ""
                };
                if (showUser) {
                    annotation.fullName = annotations[i].fullName;
                }
                annotation.text = annotations[i].text.replace(/\n/g, "<br/>");
                annotation.scopes = [annotations[i].isCaseSpecific, annotations[i].isVariantSpecific, annotations[i].isTumorSpecific];
                annotation.scopeLevels = ["Case " + (annotations[i].isCaseSpecific ? annotations[i].caseId : ''),
                "Variant " + (annotations[i].isVariantSpecific ? this.currentVariant.chrom : ''),
                    "Tumor"];
                annotation.category = annotations[i].category;
                annotation.cnvGenes = annotations[i].cnvGenes ? annotations[i].cnvGenes.join(" ") : "";
                annotation.createdDate = annotations[i].createdDate;
                annotation.createdSince = annotations[i].createdSince;
                annotation.modifiedDate = annotations[i].modifiedDate;
                annotation.modifiedSince = annotations[i].modifiedSince;
                annotation.scopeTooltip = this.$refs.cnvAnnotationDialog.createLevelInformation(annotations[i]);
                formatted.push(annotation);
            }
            return formatted;
        },
        formatLocalTranslocationAnnotations(annotations, showUser) {
            var formatted = [];
            for (var i = 0; i < annotations.length; i++) {
                var annotation = {
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
                };
                if (showUser) {
                    annotation.fullName = annotations[i].fullName;
                }
                annotation.text = annotations[i].text.replace(/\n/g, "<br/>");
                annotation.scopes = [annotations[i].isCaseSpecific, annotations[i].isVariantSpecific, annotations[i].isTumorSpecific];
                annotation.scopeLevels = ["Case " + (annotations[i].isCaseSpecific ? annotations[i].caseId : ''),
                "Variant " + (annotations[i].isVariantSpecific ? this.currentVariant.fusionName : ''),
                    "Tumor"];
                annotation.category = annotations[i].category;
                annotation.createdDate = annotations[i].createdDate;
                annotation.createdSince = annotations[i].createdSince;
                annotation.modifiedDate = annotations[i].modifiedDate;
                annotation.modifiedSince = annotations[i].modifiedSince;
                annotation.scopeTooltip = this.$refs.translocationAnnotationDialog.createLevelInformation(annotations[i]);
                formatted.push(annotation);
            }
            return formatted;
        },
        startUserAnnotations() {
            if (!this.canProceed('canSelect') || this.readOnly) {
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
        },
        formatAnnotations() {
            this.utswAnnotationsFormatted = this.formatLocalAnnotations(this.utswAnnotations, true);
            // this.userAnnotationsFormatted = this.formatLocalAnnotations(this.userAnnotations, false);
        },
        formatCNVAnnotations() {
            this.utswAnnotationsFormatted = this.formatLocalCNVAnnotations(this.utswAnnotations, true);
        },
        formatTranslocationAnnotations() {
            this.utswAnnotationsFormatted = this.formatLocalTranslocationAnnotations(this.utswAnnotations, true);
        },
        commitAnnotations(userAnnotations) {
            this.userAnnotations = userAnnotations;
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
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        if (this.isSNP()) {
                            this.getVariantDetails(this.currentRow);
                        }
                        else if (this.isCNV()) {
                            this.getCNVDetails(this.currentRow);
                        }
                        else if (this.isTranslocation()) {
                            this.getTranslocationDetails(this.currentRow);
                        }
                        this.snackBarMessage = "Annotation(s) Saved";
                        this.snackBarVisible = true;

                        //keep track of the selected variants and refresh
                        var selectedIds = this.getSelectedVariantIds();
                        if (selectedIds) {
                            this.tempSelectedSNPVariants = selectedIds.selectedSNPVariantIds;
                            this.tempSelectedCNVs = selectedIds.selectedCNVIds;
                            this.tempSelectedTranslocations = selectedIds.selectedTranslocationIds;
    
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
                        //refresh
                        this.getAjaxData();

                    } else {
                        this.handleDialogs(response.data, this.commitAnnotations);
                    }
                })
                .catch(error => {
                    console.log(error);
                    bus.$emit("some-error", [this, error]);
                });
        },
        selectVariantForReport() {
            if (!this.canProceed('canSelect') || this.readOnly) {
                return;
            }
            this.$refs.geneVariantDetails.addToSelection(this.currentRow);
        },
        removeVariantFromReport() {
            if (!this.canProceed('canSelect') || this.readOnly) {
                return;
            }
            this.$refs.geneVariantDetails.removeFromSelection(this.currentRow);
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
        openSaveDialog() {
            this.updateSelectedVariantTable();
            this.toggleHTMLOverlay(true);
            this.saveDialogVisible = true;
        },
        closeSaveDialog(goBack) { //goBack if coming from a button, not from the navigation
            this.saveDialogVisible = false;
            this.toggleHTMLOverlay(false);
            if (goBack) {
                router.back();
            }
        },
        saveSelection() {
            // There is a bug in vuetify 1.0.19 where a disabled menu still activates the click action.
            // Use a flag to disable the action in the meantime
            if (this.saveVariantDisabled) {
                return;
            }
            this.saveLoading = true;
            var selectedIds = this.getSelectedVariantIds();
            axios({
                method: 'post',
                url: webAppRoot + "/saveVariantSelection",
                params: {
                    caseId: this.$route.params.id
                },
                data: {
                    selectedSNPVariantIds: selectedIds.selectedSNPVariantIds,
                    selectedCNVIds: selectedIds.selectedCNVIds,
                    selectedTranslocationIds: selectedIds.selectedTranslocationIds
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    this.saveDialogVisible = false;
                    this.snackBarMessage = "Variant Selection Saved";
                    this.snackBarVisible = true;
                    this.getAjaxData();
                    this.variantUnSaved = false;
                    this.saveLoading = false;
                    this.closeSaveDialog(true);
                }
                else {
                    this.saveLoading = false;
                    this.handleDialogs(response.data, this.saveSelection);
                }
            }).catch(error => {
                this.saveLoading = false;
                console.log(error);
                bus.$emit("some-error", [this, error]);
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
        toggleRelatedVariants() {
            if (this.currentVariantHasRelatedVariants) {
                this.annotationVariantRelatedVisible = !this.annotationVariantRelatedVisible;
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
                    this.snackBarVisible = true;
                }
                else {
                    this.handleDialogs(response.data, this.saveCurrentFilters);
                }
                this.$refs.advancedFilter.loading = false;
            }
            ).catch(error => {
                this.$refs.advancedFilter.loading = false;
                console.log(error);
                bus.$emit("some-error", [this, error]);
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
                    this.snackBarVisible = true;
                }
                else {
                    this.handleDialogs(response.data, this.deleteFilterSet.bind(null, filterSetId));
                }
            }
            ).catch(error => {
                console.log(error);
                bus.$emit("some-error", [this, error]);
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
                    console.log(error);
                    bus.$emit("some-error", [this, error]);
                });
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
        closeVariantDetails(goBack) {
            this.variantDetailsVisible = false;
            this.toggleHTMLOverlay(false);
            if (goBack) {
                router.back();
            }
        },
        toggleHTMLOverlay(hideScrollBar) {
            var html = document.querySelector("html");
            if (hideScrollBar) {
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
        getSelectedVariantIds() {
            var selectedSNPVariantIds = this.$refs.geneVariantDetails.items.filter(item => item.isSelected).map(item => item.oid);
            var selectedCNVIds = this.$refs.cnvDetails.items.filter(item => item.isSelected).map(item => item.oid);
            var selectedTranslocationIds = this.$refs.translocationDetails.items.filter(item => item.isSelected).map(item => item.oid);
            return {
                selectedSNPVariantIds: selectedSNPVariantIds,
                selectedCNVIds: selectedCNVIds,
                selectedTranslocationIds: selectedTranslocationIds
            }
        },
        exportSelectedVariants() {
            // There is a bug in vuetify 1.0.19 where a disabled menu still activates the click action.
            // Use a flag to disable the action in the meantime
            if (this.saveVariantDisabled) {
                return;
            }
            this.exportLoading = true;
            var selectedIds = this.getSelectedVariantIds();
            axios({
                method: 'post',
                responseType: 'blob',
                url: webAppRoot + "/exportSelection",
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
                this.createExcelFile(response.data);
                this.exportLoading = false;
            }
            ).catch(error => {
                if (error.response.status == 403) { //need to relogin
                    bus.$emit("login-needed", [this, this.exportSelectedVariants]);
                }
                else {
                    console.log(error);
                    bus.$emit("some-error", [this, error]);
                }
                this.exportLoading = false;
            }
            );
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
                    this.snackBarMessage = "Variants sent to MD Anderson";
                    this.snackBarVisible = true;
                }
                else {
                    this.handleDialogs(response.data, this.sendToMDA);
                }
            }).catch(error => {
                this.sendToMDALoading = false;
                console.log(error);
                bus.$emit("some-error", [this, error]);
            });
        },
        // parseDate(annotation) {
        //     if (annotation.modifiedDate) {
        //         return annotation.modifiedSince + " (" + annotation.modifiedDate.split("T")[0] + ")";
        //     }
        // },
        createExcelFile(content) {
            var url = window.URL.createObjectURL(new Blob([content]));
            var hiddenElement = document.createElement('a');
            hiddenElement.download = this.$route.params.id + '_variants.xlsx';
            hiddenElement.href = url;
            document.body.appendChild(hiddenElement);
            hiddenElement.click();
            document.body.removeChild(hiddenElement);
        },
        createCSVFile(content) {
            var hiddenElement = document.createElement('a');
            hiddenElement.href = 'data:text/csv;charset=utf-8,' + encodeURI(content);
            //hiddenElement.target = '_blank';
            hiddenElement.download = this.$route.params.id + '_data.csv';
            document.body.appendChild(hiddenElement);
            hiddenElement.click();
            document.body.removeChild(hiddenElement);

        },
        updateVariantDetailsBreadCrumbs(visible) {
            if (visible) {
            this.urlQuery.variantId = this.currentVariant._id.$oid;
            this.urlQuery.variantType = this.currentVariantType;
            router.push({query: this.urlQuery});
            }
            else {
            // router.back();
            }
        },
        updateSaveDialogBreadCrumbs(visible) {
            if (visible) {
                this.urlQuery.showReview = true;
                router.push({query: this.urlQuery});
            }
            else {
                // router.back();
            }
        },
        updateEditAnnotationBreadcrumbs(visible) {
            if (visible) {
                this.urlQuery.edit = true;
                router.push({query: this.urlQuery});
            }
            else {
                router.back();
            }
        },
        saveCaseAnnotations() {
            axios({
                method: 'post',
                url: webAppRoot + "/saveCaseAnnotations",
                params: {
                    caseId: this.$route.params.id
                },
                data: {
                    annotation: [this.caseAnnotation]
                }
            }).then(response => {
                if (response.data.isAllowed) {
                    this.loadCaseAnnotations();
                    this.snackBarMessage = "Annotation Saved";
                    this.snackBarVisible = true;
                    this.caseAnnotationOriginalText = this.caseAnnotation.caseAnnotation; //to reset the isCaseAnnotationUnchanged
                }
                else {
                    this.handleDialogs(response.data, this.saveCaseAnnotations);
                }
            }).catch(error => {
                console.log(error);
                bus.$emit("some-error", [this, error]);
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
                    console.log(error);
                    bus.$emit("some-error", [this, error]);
                });
        },
        handleSelectionChanged(selectedSize) {
            this.variantUnSaved = true;
        },
        isCaseAnnotationUnchanged() {
            return this.caseAnnotation.caseAnnotation == this.caseAnnotationOriginalText;
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
                var genesToReport = this.reportGroups[i].genesToReport;
                for (var j = 0; j < genesToReport.length; j++) {
                    geneNamesToReport.push(genesToReport[j]);
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
            return false; pat
        },
        //TODO
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
                    table = this.$refs.snpVariantsSelected;
                }
                else {
                    table = this.$refs.geneVariantDetails;
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
        saveVariant() {
            if (!this.canProceed('canAnnotate')) {
                return;
            }
            this.savingVariantDetails = true;
            var lightVariant = {};
            lightVariant["_id"] = this.currentVariant._id;
            lightVariant["tier"] = this.currentVariant.tier;
            axios({
                method: 'post',
                url: webAppRoot + "/saveVariant",
                params: {
                    variantType: this.currentVariantType,
                    caseId: this.$route.params.id,
                },
                data: {
                    // filters: this.$refs.advancedFilter.filters,
                    variant: lightVariant
                }
            }).then(response => {
                if (response.data.isAllowed) {
                    this.revertVariant();
                    this.snackBarMessage = "Variant Saved";
                    this.snackBarVisible = true;
                    this.$refs.variantDetailsPanel.variantDetailsUnSaved = false; //update badge on save button
                }
                else {
                    this.handleDialogs(response.data, this.saveVariant);
                }
                this.savingVariantDetails = false;
            }).catch(error => {
                this.savingVariantDetails = false;
                console.log(error);
                bus.$emit("some-error", [this, error]);
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
                        this.patientDetailsOncoTreeDiagnosis = this.patientTables[2].items[0].value; //careful when swapping item positions
                    }
                    else {
                        this.handleDialogs(response.data, this.getPatientDetails);
                    }
                }).catch(error => {
                    console.log(error);
                    bus.$emit("some-error", [this, error]);
                });
        },
        savePatientDetails() {
            if (!this.canProceed('canAnnotate')) {
                return;
            }
            this.savingPatientDetails = true;
            axios({
                method: 'post',
                url: webAppRoot + "/savePatientDetails",
                params: {
                    oncotreeDiagnosis: this.patientDetailsOncoTreeDiagnosis,
                    caseId: this.$route.params.id,
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    this.getPatientDetails();
                    this.snackBarMessage = "Patient Details Saved";
                    this.snackBarVisible = true;
                }
                else {
                    this.handleDialogs(response.data, this.savePatientDetails);
                }
                this.savingPatientDetails = false;
            }).catch(error => {
                this.savingPatientDetails = false;
                console.log(error);
                bus.$emit("some-error", [this, error]);
            });
        },
        openOncoTree() {
            window.open("http://oncotree.mskcc.org", "_blank");
        },
        handleRouteChanged(newRoute, oldRoute) {
            if (newRoute.path != oldRoute.path) { //prevent reloading data if only changing the query router.push({query: {test:"hello3"}})
                this.getAjaxData();
            }
            else { //look at the query
                // console.log(newRoute.query, oldRoute.query);
                var newRouteQuery = JSON.stringify(newRoute.query);
                if (newRouteQuery != JSON.stringify(oldRoute.query)) { //some params changed
                    this.loadFromParams();
                }
            }
    },
    handlePanelVisibility(visible) {
        if (visible == null) {
            this.editAnnotationVariantDetailsVisible = !this.editAnnotationVariantDetailsVisible;
        }
        else {
            this.editAnnotationVariantDetailsVisible = visible;
        }
    }
    },
    mounted() {
        this.getAjaxData();
        this.loadUserFilterSets();
        bus.$emit("clear-item-selected", [this]);
        this.getVariantFilters();
        this.loadCaseAnnotations();
        bus.$on('bam-viewer-closed', () => {
            this.externalWindowOpen = false;
        });
        bus.$on('saving-annotations', (annotations) => {
            this.commitAnnotations(annotations);
        });
        // bus.$on('breadcrumb-level-down', (visibleFlag) => {
        //     this.topMostDialog = visibleFlag;
        // });
        // router.beforeEach((to, from, next) => {
        //     //if any dialog is open don't navigate
        //     if (this.topMostDialog) { //null if level 1
        //         next(false);
        //         this[this.topMostDialog](); //hide the dialog
        //     }
        //     else {
        //         next();
        //     }
        // });

    },
    destroyed: function () {
        bus.$off('bam-viewer-closed');
        bus.$off('saving-annotations');
        // bus.$off('breadcrumb-level-down');
    },
    watch: {
        '$route': 'handleRouteChanged',
        variantDetailsVisible: "updateVariantDetailsBreadCrumbs",
        saveDialogVisible: "updateSaveDialogBreadCrumbs",
        variantTabActive: "handleTabChanged",
        // saveDialogVisible: "handleSaveDialogRouteChanged"
        // breadcrumbs: function() {
        //     this.$refs.annotationDialog.breadcrumbs = this.breadcrumbs;
        // }
    }

};