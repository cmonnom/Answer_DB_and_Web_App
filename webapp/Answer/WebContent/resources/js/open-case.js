const OpenCase = {
    template: `<div>
    <v-dialog v-model="confirmationDialogVisible" max-width="300px">
        <v-card>
            <v-card-text v-html="confirmationMessage" class="pl-2 pr-2 subheading">

            </v-card-text>
            <v-card-actions>
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
            <v-toolbar dark color="primary">
                <v-menu offset-y offset-x class="ml-0">
                    <v-btn slot="activator" flat icon dark>
                        <v-icon>more_vert</v-icon>
                    </v-btn>
                    <v-list>

                        <v-list-tile avatar @click="exportSelectedVariants()" :disabled="saveVariantDisabled || exportLoading">
                            <v-list-tile-avatar>
                                <v-icon>file_download</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Export to Excel</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile avatar @click="saveSelection()" :disabled="saveVariantDisabled || saveLoading">
                            <v-list-tile-avatar>
                                <v-icon>save</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Save Selected Variants</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile avatar @click="closeSaveDialog()">
                            <v-list-tile-avatar>
                                <v-icon>close</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Close</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>
                    </v-list>
                </v-menu>
                <v-toolbar-title>Review Selected Variants
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
                    <v-btn icon @click="closeSaveDialog" slot="activator">
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
                    initial-sort="chromPos" no-data-text="No Data" :show-row-count="true" class="pb-3">
                </data-table>
                <data-table ref="cnvVariantsSelected" :fixed="false" :fetch-on-created="false" table-title="Selected CNVs" initial-sort="chrom"
                    no-data-text="No Data" :show-row-count="true" class="pb-3">
                </data-table>
                <data-table ref="translocationVariantsSelected" :fixed="false" :fetch-on-created="false" table-title="Selected Translocations"
                    initial-sort="fusionName" no-data-text="No Data" :show-row-count="true" class="pb-3">
                </data-table>
            </v-card-text>
            <v-card-actions>
                <v-tooltip top>
                    <v-btn color="primary" :disabled="saveVariantDisabled" @click="saveSelection()" slot="activator" :loading="saveLoading">Save
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
                <v-btn color="error" @click="closeSaveDialog" slot="activator">Cancel
                    <v-icon right dark>cancel</v-icon>
                </v-btn>
                <breadcrumbs>
                </breadcrumbs>
            </v-card-actions>
        </v-card>
    </v-dialog>

    <!-- annotation dialog -->
    <edit-annotations type="snp" @saving-annotations="commitAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs"
        ref="annotationDialog" :title="currentVariant.geneName + ' ' + currentVariant.notation"></edit-annotations>

    <edit-annotations type="cnv" @saving-annotations="commitAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs"
        ref="cnvAnnotationDialog" :title="currentVariant.chrom"></edit-annotations>

    <edit-annotations type="translocation" @saving-annotations="commitAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs"
        ref="translocationAnnotationDialog" :title="currentVariant.chrom"></edit-annotations>
    <!-- variant details dialog -->
    <v-dialog v-model="variantDetailsVisible" scrollable fullscreen hide-overlay transition="dialog-bottom-transition">
        <v-card class="soft-grey-background">
            <v-toolbar dark color="primary">
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
                                            <v-icon class="pl-2 pr-4">keyboard_arrow_right</v-icon>Show / Hide </span>
                                        <v-list>
                                            <v-list-tile avatar @click="annotationVariantDetailsVisible = !annotationVariantDetailsVisible">
                                                <v-list-tile-avatar>
                                                    <v-icon>zoom_in</v-icon>
                                                </v-list-tile-avatar>
                                                <v-list-tile-content>
                                                    <v-list-tile-title>Variant Details</v-list-tile-title>
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
                <v-toolbar-title class="ml-0">Annotations for variant: {{ currentVariant.geneName }} {{ currentVariant.notation }}
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
                    <v-btn icon flat :color="annotationVariantOtherVisible ? 'amber accent-2' : ''" @click="annotationVariantOtherVisible = !annotationVariantOtherVisible"
                        slot="activator">
                        <v-icon>mdi-table-search</v-icon>
                    </v-btn>
                    <span>Show/Hide Other VCF Annotations</span>
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
                    <v-btn icon @click="closeVariantDetails()" slot="activator">
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
                            <v-flex xs12 md12 lg9 xl7 v-show="annotationVariantDetailsVisible">
                                <v-card>
                                    <v-toolbar dark color="primary">
                                        <v-toolbar-title>
                                            <v-icon color="amber accent-2">zoom_in</v-icon>
                                            Variant Details
                                        </v-toolbar-title>

                                        <v-spacer></v-spacer>
                                        <v-tooltip bottom>
                                            <v-btn icon @click="annotationVariantDetailsVisible = false" slot="activator">
                                                <v-icon>close</v-icon>
                                            </v-btn>
                                            <span>Close Details</span>
                                        </v-tooltip>

                                    </v-toolbar>
                                    <v-container grid-list-md fluid>
                                        <v-layout row wrap>
                                            <v-flex :class="isSNP() ? 'xs4' : 'xs6'" v-for="table in variantDataTables" :key="table.name">
                                                <v-card flat>
                                                    <v-card-text>
                                                        <v-list class="dense-tiles">
                                                            <v-list-tile v-for="item in table.items" :key="item.label">
                                                                <v-list-tile-content class="pb-2">
                                                                    <v-layout class="full-width">
                                                                        <v-flex xs12 class="text-xs-left grow">
                                                                            <span class="selectable">{{ item.label }}:</span>
                                                                            <span v-html="item.value" class="selectable text-xs-right grow blue-grey--text text--lighten-1"></span>
                                                                        </v-flex>
                                                                    </v-layout>
                                                                </v-list-tile-content>
                                                            </v-list-tile>
                                                        </v-list>
                                                    </v-card-text>
                                                </v-card>
                                            </v-flex>
                                        </v-layout>
                                        <v-layout row wrap>
                                            <v-flex xs12 v-for="table in linkTable" :key="table.name">
                                                <v-card flat>
                                                    <v-card-text>
                                                        <v-list class="dense-tiles">
                                                            <v-list-tile v-for="item in table.items" :key="item.label">
                                                                <v-list-tile-content class="pb-2">
                                                                    <v-layout class="full-width">
                                                                        <v-flex xs12 class="text-xs-left grow">
                                                                            <span class="selectable">{{ item.label }}:</span>
                                                                            <v-tooltip v-if="item.links && id !== null" bottom v-for="id in item.ids" :key="id">
                                                                                <v-btn @click="handleIdLink(id)" slot="activator" v-html="formatIdLinkLabel(id, item)">
                                                                                </v-btn>
                                                                                <span>Open in new tab</span>
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
                            </v-flex>
                        </v-slide-y-transition>
                        <v-flex xs12>
                            <v-slide-y-transition>
                                <div class="pb-4 pt-2" v-show="annotationVariantCanonicalVisible && isSNP()">
                                    <data-table ref="canonicalVariantAnnotation" :fixed="false" :fetch-on-created="false" table-title="Canonical VCF Annotations"
                                        initial-sort="geneId" no-data-text="No Data" :show-pagination="false" title-icon="mdi-table-search">
                                    </data-table>
                                </div>
                            </v-slide-y-transition>
                            <v-slide-y-transition>
                                <data-table v-show="annotationVariantOtherVisible" ref="otherVariantAnnotations" :fixed="false" :fetch-on-created="false"
                                    table-title="Other VCF Annotations" initial-sort="geneId" no-data-text="No Data" :show-row-count="true"
                                    title-icon="mdi-table-search">
                                </data-table>
                            </v-slide-y-transition>
                        </v-flex>
                        <!-- MDA Annotation card -->
                        <v-slide-y-transition>
                            <v-flex xs12 v-show="mdaAnnotationsVisible && mdaAnnotationsExists()">
                                <v-card>
                                    <v-toolbar dark color="primary">
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
                                    <v-toolbar dark color="primary">
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
                                        <v-card >
                                            <v-card-text class="subheading">
                                                <v-container grid-list-md fluid>
                                                    <v-layout row wrap>
                                                        <v-flex xs12>
                                                            From {{ annotation.fullName }}
                                                            <span v-text="parseDate(annotation)"></span>
                                                        </v-flex>
                                                        <v-flex xs12>
                                                            Scope:
                                                            <v-tooltip bottom v-for="(scope, index) in annotation.scopes" :key="index">
                                                                <v-chip disabled outline slot="activator">
                                                                    <span :class="scope ? 'green--text' : 'red--text'">{{ annotation.scopeLevels[index] }}</span>
                                                                    <v-icon right v-if="scope" color="green">check</v-icon>
                                                                    <v-icon right v-if="!scope" color="red">close</v-icon>
                                                                </v-chip>
                                                                <span v-html="annotation.scopeTooltip"> </span>
                                                            </v-tooltip>
                                                        </v-flex>
                                                        <v-flex xs12>
                                                            <span v-if="annotation.category" class="pr-1">
                                                                <b>{{ annotation.category }}:</b>
                                                            </span>
                                                            <span v-html="annotation.text"></span>
                                                        </v-flex>
                                                        <v-flex xs12>
                                                            <span v-if="annotation.tier" class="pr-1">
                                                                <b>Tier:</b>
                                                            </span>
                                                            <span v-html="annotation.tier"></span>
                                                        </v-flex>
                                                        <v-flex xs12>
                                                        <span v-if="annotation.classification" class="pr-1">
                                                            <b>Classification:</b>
                                                        </span>
                                                        <span v-html="annotation.classification"></span>
                                                    </v-flex>
                                                        <v-flex xs12 v-if="isCNV() && annotation.cnvGenes" class="pr-1">
                                                            Apply to genes: {{ annotation.cnvGenes }}
                                                        </v-flex>
                                                        <v-flex xs12>
                                                            <span v-if="annotation.pmids" class="selectable">PubMed Ids:</span>
                                                            <v-tooltip v-if="id" bottom v-for="id in annotation.pmids" :key="id">
                                                                <v-btn @click="handlePubMedIdLink(id)" slot="activator">
                                                                    {{ id }}
                                                                </v-btn>
                                                                <span>Open in new tab</span>
                                                            </v-tooltip>
                                                            <span v-if="annotation.nctids" class="selectable pl-2">Clinical Trials:</span>
                                                            <v-tooltip v-if="id" bottom v-for="id in annotation.nctids" :key="id">
                                                                <v-btn @click="handleNCTIdLink(id)" slot="activator">
                                                                    {{ id }}
                                                                </v-btn>
                                                                span>Open in new tab</span>
                                                            </v-tooltip>
                                                        </v-flex>
                                                    </v-layout>
                                                </v-container>
                                            </v-card-text>
                                        </v-card>
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
            <v-card-actions>
                <v-tooltip top>
                    <v-btn color="primary" @click="startUserAnnotations()" slot="activator">Add/Edit
                        <v-icon right dark>note_add</v-icon>
                    </v-btn>
                    <span>Create/Edit Your Annotations</span>
                </v-tooltip>
                <v-btn v-if="!currentRow.isSelected" :disabled="saveDialogVisible" color="success" @click="selectVariantForReport()" slot="activator">Select Variant
                    <v-icon right dark>done</v-icon>
                </v-btn>
                <v-btn v-if="currentRow.isSelected" :disabled="saveDialogVisible" color="warning" @click="removeVariantFromReport()" slot="activator">Deselect Variant
                    <v-icon right dark>done</v-icon>
                </v-btn>
                <v-btn color="error" @click="closeVariantDetails()" slot="activator">Close
                    <v-icon right dark>cancel</v-icon>
                </v-btn>
                <breadcrumbs>
                </breadcrumbs>
            </v-card-actions>
        </v-card>
    </v-dialog>



    <v-toolbar dark color="primary" fixed app :extended="loadingVariantDetails">
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
                            <v-list-tile-title>Show/Hide Case Annotations</v-list-tile-title>
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
            <span>Show/Hide Case Annotations</span>
        </v-tooltip>
        <v-badge color="red" right bottom overlap v-model="variantUnSaved" class="mini-badge">
            <v-icon slot="badge" ></v-icon>
            <v-tooltip bottom>
                <v-btn flat icon @click="openSaveDialog()" slot="activator" :color="saveDialogVisible ? 'amber accent-2' : ''">
                    <v-icon>mdi-clipboard-check</v-icon>
                </v-btn>
                <span>Review Variants Selected</span>
            </v-tooltip>
        </v-badge>
        <v-progress-linear class="ml-4 mr-4" :slot="loadingVariantDetails ? 'extension' : ''" v-show="!caseName || loadingVariantDetails" :indeterminate="true" color="white"></v-progress-linear>
    </v-toolbar>

    <v-slide-y-transition>
        <v-layout v-if="patientDetailsVisible">
            <v-flex xs12 md12 lg10 xl9>
                <div class="text-xs-center pb-3">
                    <v-toolbar dark color="primary">
                        <!-- <v-icon>perm_identity</v-icon> -->
                        <v-icon :color="patientDetailsVisible ? 'amber accent-2' : ''">assignment_ind</v-icon>
                        <v-toolbar-title>Patient Details</v-toolbar-title>
                        <v-spacer></v-spacer>
                        <v-tooltip bottom>
                            <v-btn flat icon @click="patientDetailsVisible = false" slot="activator">
                                <v-icon>close</v-icon>
                            </v-btn>
                            <span>Close Details</span>
                        </v-tooltip>
                    </v-toolbar>
                    <v-card>
                        <v-container grid-list-md fluid>
                            <v-layout row wrap>
                                <v-flex xs4 v-for="table in patientTables" :key="table.name">
                                    <v-card flat>
                                        <v-card-text>
                                            <v-list class="dense-tiles">
                                                <v-list-tile v-for="item in table.items" :key="item.label">
                                                    <v-list-tile-content class="pb-2">
                                                        <v-layout class="full-width">
                                                            <v-flex xs6 class="text-xs-left grow">
                                                                <span class="selectable">{{ item.label }}:</span>
                                                            </v-flex>
                                                            <v-flex xs6 class="text-xs-right grow blue-grey--text text--lighten-1">
                                                                <span class="selectable">{{ item.value }}</span>
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
                <v-toolbar dark color="primary">
                    <!-- <v-icon>perm_identity</v-icon> -->
                    <v-icon :color="caseAnnotationsVisible ? 'amber accent-2' : ''">mdi-message-bulleted</v-icon>
                    <v-toolbar-title>Case Annotations</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-tooltip bottom>
                        <v-btn flat icon @click="caseAnnotationsVisible = false" slot="activator">
                            <v-icon>close</v-icon>
                        </v-btn>
                        <span>Close Annotations</span>
                    </v-tooltip>
                </v-toolbar>
                <v-card>
                    <v-card-text>
                        <v-text-field :textarea="true" v-model="caseAnnotation.caseAnnotation" class="mr-2 no-height" label="Write your comments here">
                        </v-text-field>
                    </v-card-text>
                    <v-card-actions>
                        <v-tooltip bottom>
                            <v-btn :disabled="loadingVariantDetails || isCaseAnnotationUnchanged()" slot="activator" color="success" @click="saveCaseAnnotations()">Save
                                <v-icon right dark>save</v-icon>
                            </v-btn>
                            <span>Save/Update Annotation</span>
                        </v-tooltip>
                        <v-tooltip bottom>
                            <v-btn :disabled="loadingVariantDetails || isCaseAnnotationUnchanged()" slot="activator" color="error" @click="loadCaseAnnotations()">Discard Changes
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
        <v-tabs dark slider-color="warning" color="primary" 
        fixed-tabs v-show="variantTabsVisible"
        v-model="variantTabActive">
            <v-tab>
                SNP / Indel
            </v-tab>
            <v-tab>
                CNV
            </v-tab>
            <v-tab>
                Fusion / Translocation
            </v-tab>
            <!-- SNP / Indel table -->
            <v-tab-item>
                <data-table ref="geneVariantDetails" :fixed="false" :fetch-on-created="false" table-title="SNP/Indel Variants" initial-sort="chromPos"
                    no-data-text="No Data" :enable-selection="true" :show-row-count="true" @refresh-requested="handleRefresh()"
                    :show-left-menu="true" @showing-buttons="toggleGeneVariantDetailsButtons"
                   @datatable-selection-changed="handleSelectionChanged">
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
            <v-tab-item>
                <data-table ref="cnvDetails" :fixed="false" :fetch-on-created="false" table-title="CNVs" initial-sort="chrom" no-data-text="No Data"
                    :enable-selection="true" :show-row-count="true" @refresh-requested="handleRefresh()" :show-left-menu="true">
                </data-table>
            </v-tab-item>
            <!--  Fusion / Translocation table -->
            <v-tab-item>
                <data-table ref="translocationDetails" :fixed="false" :fetch-on-created="false" table-title="Fusions / Translocations" initial-sort="fusionName"
                    no-data-text="No Data" :enable-selection="true" :show-row-count="true" @refresh-requested="handleRefresh()"
                    :show-left-menu="true">
                </data-table>
            </v-tab-item>
        </v-tabs>
    </v-slide-y-transition>



</div>` , data() { return {      firstTimeLoading: true,
            loading: true,
            loadingVariantDetails: false,
            // breadcrumbs: [{text: "You are here:  Case", disabled: true}],
            breadcrumbItemVariantDetails: { text: "Variant Details", disabled: true },
            breadcrumbItemReview: { text: "Review Selected Variants", disabled: true },
            breadcrumbItemEditAnnotation: { text: "Add / Edit Annotation", disabled: true },
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
            caseAnnotation: { caseAnnotation: "" },
            caseAnnotationOriginalText: "", //to verify if there has been a modification
            currentVariantType: "snp",
            //confirmation dialog
            confirmationDialogVisible: false,
            confirmationMessage: "Unsaved selected variants will be discarded.<br/>Are you sure?",
            confirmationProceedButton: "Proceed",
            confirmationCancelButton: "Cancel",
            reportGroups: [],
            geneVariantDetailsTableHovering: false,
            variantTabActive: null,
            wasAdvancedFilteringVisibleBeforeTabChange: false
        }
    }, methods: {
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
                    caseId: this.$route.params.id
                },
                data: {
                    filters: this.$refs.advancedFilter.filters
                }
            }).then(response => {
                if (response.data.isAllowed) {
                    this.patientTables = response.data.patientInfo.patientTables;
                    this.caseName = response.data.caseName;
                    this.caseId = response.data.caseId;
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
                    }
                }
                else {
                    this.handleDialogs(response.data, this.getAjaxData);
                }
                this.loadingVariantDetails = false;
                this.$refs.advancedFilter.loading = false;
            }
            ).catch(error => {
                this.loadingVariantDetails = false;
                this.$refs.advancedFilter.loading = false;
                console.log(error);
                bus.$emit("some-error", [this, error]);
            }
            );
        },
        addCustomWarningFlags(snpIndelVariantSummary) {
            for (var i =0; i < snpIndelVariantSummary.items.length; i++) {
                var item = snpIndelVariantSummary.items[i];
                var iconFlags = item.iconFlags.iconFlags;
                var warnings = [];
                var tooltips = [];
                if (item.common) {
                    warnings.push("C");
                    tooltips.push("Common");
                }
                if (item.inconsistent) {
                    warnings.push("I");
                    tooltips.push("Inconsistent calls");
                }
                if (item.repeat) {
                    warnings.push("R");
                    tooltips.push("Repeat");
                }
                if (warnings.length > 0) {
                    iconFlags.push({
                        chip: true,
                        color: "warning",
                        iconName: warnings.join(),
                        tooltip: tooltips.join(",")
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
            if (this.variantTabActive != "0") { //remember if filter was visible or not before the change
                // this.wasAdvancedFilteringVisibleBeforeTabChange = this.$refs.advancedFilter.advancedFilteringVisible;
                // if (this.wasAdvancedFilteringVisibleBeforeTabChange) {
                //     this.$refs.advancedFilter.toggleFilters(); //hide filtering because of tab change
                // }
                this.$refs.advancedFilter.disableFiltering = true;
            }
            if (this.$refs.advancedFilter && this.variantTabActive == "0") { //restore the previous visibility of the filter
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
            this.loadingVariantDetails = true;
            axios.get(
                webAppRoot + "/getVariantDetails",
                {
                    params: {
                        variantId: item.oid

                    }
                }).then(response => {
                    if (response.data.isAllowed) {
                        this.currentVariant = response.data.variantDetails;
                        this.variantDataTables = [];
                        var infoTable = {
                            name: "infoTable",
                            items: [{
                                label: "Chromosome Position", value: this.currentVariant.chrom + ":"
                                    + this.currentVariant.pos
                            },
                            {
                                label: "Gene", value: this.currentVariant.geneName
                            },
                            {
                                label: "Notation", value:
                                    this.currentVariant.notation
                            },
                            {
                                label: "Alt", value: this.currentVariant.alt
                            },
                            {
                                label: "Type", value: this.currentVariant.type
                            },
                            {
                                label: "Nb. Cases Seen", value: this.currentVariant.numCasesSeen
                            },
                            {
                                label: "Somatic Status", value: this.currentVariant.somaticStatus
                            }]
                        };
                        this.variantDataTables.push(infoTable);

                        this.linkTable = [{
                            name: "linkTable", items: [
                                {
                                    label: "IDs", ids:
                                        this.currentVariant.ids, cosmicPatients: this.currentVariant.cosmicPatients, links: true
                                }
                            ]
                        }];

                        var depthTable =
                        {
                            name: "depthTable",
                            items: [
                                {
                                    label: "Tumor Total Depth",
                                    value: this.currentVariant.tumorTotalDepth
                                },
                                {
                                    label:
                                        "Tumor Alt Percent",
                                    value: this.formatPercent(this.currentVariant.tumorAltFrequency)
                                },
                                {
                                    label: "Normal Total Depth",
                                    value: this.currentVariant.normalTotalDepth
                                },
                                {
                                    label: "Normal Alt Percent",
                                    value: this.formatPercent(this.currentVariant.normalAltFrequency)
                                }, {
                                    label: "RNA Total Depth",
                                    value: this.currentVariant.rnaTotalDepth
                                },
                                {
                                    label: "RNA Alt Percent",
                                    value: this.formatPercent(this.currentVariant.rnaAltFrequency)
                                },
                                {
                                    label: "Exac Allele Frequency",
                                    value: this.formatPercent(this.currentVariant.exacAlleleFrequency)
                                },
                                {
                                    label: "gnomAD Pop. Max. Allele Frequency",
                                    value: this.formatPercent(this.currentVariant.gnomadPopmaxAlleleFrequency)
                                }
                            ]
                        };
                        this.variantDataTables.push(depthTable);
                        var dataTable = {
                            name: "dataTable", items: [{
                                label: "Callers",
                                value: this.currentVariant.callSet.join(", ")
                            }, {
                                label: "Filters",
                                value: this.currentVariant.filters.join(", ")
                            }]
                        };
                        this.variantDataTables.push(dataTable);
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
            axios.get(
                webAppRoot + "/getCNVDetails",
                {
                    params: {
                        variantId: item.oid
                    }
                }).then(response => {
                    if (response.data.isAllowed) {
                        this.currentVariant = response.data;
                        this.variantDataTables = [];
                        var infoTable = {
                            name: "infoTable",
                            items: [{
                                label: "Chromosome", value: this.currentVariant.chrom
                            },
                            {
                                label: "Genes", value: this.currentVariant.genes
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
                                label: "Copy Number", value: this.currentVariant.copyNumber
                            },
                            {
                                label: "Score", value: this.currentVariant.score
                            }]
                        };
                        this.variantDataTables.push(infoTable);

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
            axios.get(
                webAppRoot + "/getTranslocationDetails",
                {
                    params: {
                        variantId: item.oid
                    }
                }).then(response => {
                    if (response.data.isAllowed) {
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
                            items: [  {
                                label: "Left Strand", value: this.currentVariant.leftStrand
                            },
                            {
                                label: "Right Strand", value: this.currentVariant.rightStrand
                            },
                            {
                                label: "RNA Reads", value: this.currentVariant.rnaReads
                            },
                            {
                                label: "DNA Reads", value: this.currentVariant.dnaReads
                            }]
                        };
                        this.variantDataTables.push(infoTable2);

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
        updateVariantVcfAnnotationTable() {
            var items = this.currentVariant.vcfAnnotations;
            var headers = this.vcfAnnotationHeaders;
            var headerOrder = this.vcfAnnotationHeadersOrder;
            this.$refs.snpVariantsSelected.manualDataFiltered(
                {
                    items: items,
                    headers: headers,
                    uniqueFieldId: "cdnaPosition",
                    headerOrder: headerOrder
                }
            );
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
            if (id.indexOf('rs') == 0) {
                link = "https://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=" + id;
            }
            else if (id.indexOf('COSM') == 0) {
                link = "https://cancer.sanger.ac.uk/cosmic/ncv/overview?id=" + id.replace("COSM", "");
            }
            else if (id.indexIf('COSN') == 0) {
                link = "https://cancer.sanger.ac.uk/cosmic/ncv/overview?id=" + id.replace("COSN", "");
            }
            else { //Clinvar
                link = "https://www.ncbi.nlm.nih.gov/clinvar/variation/" + id;
            }
            window.open(link, "_blank");
        },
        handlePubMedIdLink(id) {
            var link = "https://www.ncbi.nlm.nih.gov/pubmed/?term=" + id;
            window.open(link, "_blank");
        },
        handleNCTIdLink(id) {
            var link = "https://clinicaltrials.gov/ct2/show/" + id;
            window.open(link, "_blank");
        },
        openLink(link) {
            window.open(link, "_blank");
        },
        formatIdLinkLabel(id, item) {
            var cosmicIds = item.ids.filter(id => id.indexOf('COSM') == 0);
            var index = cosmicIds.indexOf(id);
            if (id.indexOf('rs') == 0) {
                return "&nbsp;" + id + "&nbsp;";
            }
            else if (id.indexOf('COSM') == 0) {
                return "&nbsp;" + id + " (" + item.cosmicPatients[index] + ")&nbsp;";
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
                        //update the row to show that an UTSW annotation exists
                        //without refreshing the whole table to avoid losing
                        //unsaved selected data
                        var variantAnnotated = this.$refs.geneVariantDetails.items.filter(item => item.oid == this.currentVariant._id.$oid);
                        if (variantAnnotated.length == 0) {
                            variantAnnotated = this.$refs.cnvDetails.items.filter(item => item.oid == this.currentVariant._id.$oid);
                        }
                        if (variantAnnotated.length == 0) {
                            variantAnnotated = this.$refs.translocationDetails.items.filter(item => item.oid == this.currentVariant._id.$oid);
                        }
                        //CAREFUL IF UTSW icon is not in 2nd position!!!!
                        //update the correct row
                        for (var i = 0; i < variantAnnotated.length; i++) {
                            variantAnnotated[i].utswAnnotated = true;
                            variantAnnotated[i].iconFlags.iconFlags[2] = {
                                color: "indigo darken-4",
                                iconName: "mdi-message-bulleted",
                                tooltip: "UTSW Annotations"
                            }
                        }
                        this.updateSelectedVariantTable();
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
            this.$refs.geneVariantDetails.addToSelection(this.currentRow);
            this.closeVariantDetails();
        },
        removeVariantFromReport() {
            this.$refs.geneVariantDetails.removeFromSelection(this.currentRow);
            this.closeVariantDetails();
        },
        updateSelectedVariantTable() {
            var selectedSNPVariants = this.$refs.geneVariantDetails.items.filter(item => item.isSelected);
            var selectedCNVs = this.$refs.cnvDetails.items.filter(item => item.isSelected);
            var selectedTranslocations = this.$refs.translocationDetails.items.filter(item => item.isSelected);
            this.saveVariantDisabled = selectedSNPVariants.length == 0 && selectedCNVs.length == 0 && selectedTranslocations.length == 0;

            var snpHeaders = this.$refs.geneVariantDetails.headers;
            var snpHeaderOrder = this.$refs.geneVariantDetails.headerOrder;
            this.$refs.snpVariantsSelected.manualDataFiltered(
                { items: selectedSNPVariants, headers: snpHeaders, uniqueFieldId: "chromPos", headerOrder: snpHeaderOrder });

            var cnvHeaders = this.$refs.cnvDetails.headers;
            var cnvHeaderOrder = this.$refs.cnvDetails.headerOrder;
            this.$refs.cnvVariantsSelected.manualDataFiltered(
                { items: selectedCNVs, headers: cnvHeaders, uniqueFieldId: "chrom", headerOrder: cnvHeaderOrder });

            var snpHeaders = this.$refs.translocationDetails.headers;
            var snpHeaderOrder = this.$refs.translocationDetails.headerOrder;
            this.$refs.translocationVariantsSelected.manualDataFiltered(
                { items: selectedTranslocations, headers: snpHeaders, uniqueFieldId: "fusionName", headerOrder: snpHeaderOrder });
        },
        openSaveDialog() {
            this.updateSelectedVariantTable();
            this.toggleHTMLOverlay(true);
            this.saveDialogVisible = true;
        },
        closeSaveDialog() {
            this.saveDialogVisible = false;
            this.toggleHTMLOverlay(false);
        },
        saveSelection() {
            // There is a bug in vuetify 1.0.19 where a disabled menu still activates the click action.
            // Use a flag to disable the action in the meantime
            if (this.saveVariantDisabled) {
                return;
            }
            this.saveLoading = true;
            var selectedSNPVariantIds = this.$refs.geneVariantDetails.items.filter(item => item.isSelected).map(item => item.oid);
            var selectedCNVIds = this.$refs.cnvDetails.items.filter(item => item.isSelected).map(item => item.oid);
            var selectedTranslocationIds = this.$refs.translocationDetails.items.filter(item => item.isSelected).map(item => item.oid);
            axios({
                method: 'post',
                url: webAppRoot + "/saveVariantSelection",
                params: {
                    caseId: this.$route.params.id
                },
                data: {
                        selectedSNPVariantIds: selectedSNPVariantIds,
                        selectedCNVIds: selectedCNVIds,
                        selectedTranslocationIds: selectedTranslocationIds
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    this.saveDialogVisible = false;
                    this.snackBarMessage = "Variant Selection Saved";
                    this.snackBarVisible = true;
                    this.getAjaxData();
                    this.variantUnSaved = false;
                    this.saveLoading = false;
                    this.closeSaveDialog();
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
        closeVariantDetails() {
            this.variantDetailsVisible = false;
            this.toggleHTMLOverlay(false);
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
        exportSelectedVariants() {
            // There is a bug in vuetify 1.0.19 where a disabled menu still activates the click action.
            // Use a flag to disable the action in the meantime
            if (this.saveVariantDisabled) {
                return;
            }
            this.exportLoading = true;
            var selectedSNPVariantIds = this.$refs.geneVariantDetails.items.filter(item => item.isSelected).map(item => item.oid);
            var selectedCNVIds = this.$refs.cnvDetails.items.filter(item => item.isSelected).map(item => item.oid);
            var selectedTranslocationIds = this.$refs.translocationDetails.items.filter(item => item.isSelected).map(item => item.oid);
            axios({
                method: 'post',
                responseType: 'blob',
                url: webAppRoot + "/exportSelection",
                params: {
                    caseId: this.$route.params.id
                },
                data: {
                    filters: [],
                    selectedSNPVariantIds: selectedSNPVariantIds,
                    selectedCNVIds: selectedCNVIds,
                    selectedTranslocationIds: selectedTranslocationIds
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
        parseDate(annotation) {
            if (annotation.modifiedDate) {
                return annotation.modifiedSince + " (" + annotation.modifiedDate.split("T")[0] + ")";
            }
        },
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
                bus.$emit("add-breadcrumb-level", this.breadcrumbItemVariantDetails);
            }
            else {
                bus.$emit("remove-breadcrumb-level", this);
            }
        },
        updateSaveDialogBreadCrumbs(visible) {
            if (visible) {
                bus.$emit("add-breadcrumb-level", this.breadcrumbItemReview);
            }
            else {
                bus.$emit("remove-breadcrumb-level", this);
            }
        },
        updateEditAnnotationBreadcrumbs(visible) {
            if (visible) {
                bus.$emit("add-breadcrumb-level", this.breadcrumbItemEditAnnotation);
            }
            else {
                bus.$emit("remove-breadcrumb-level", this);
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
        }
    },
    mounted: function () {
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

    },
    destroyed: function () {
        bus.$off('bam-viewer-closed');
        bus.$off('saving-annotations');
    },
    watch: {
        '$route': 'getAjaxData',
        variantDetailsVisible: "updateVariantDetailsBreadCrumbs",
        saveDialogVisible: "updateSaveDialogBreadCrumbs",
        variantTabActive: "handleTabChanged"
        // breadcrumbs: function() {
        //     this.$refs.annotationDialog.breadcrumbs = this.breadcrumbs;
        // }
    }

};