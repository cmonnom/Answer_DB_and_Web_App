const OpenCase = {
    template: `<div>

    <v-snackbar :timeout="4000" :bottom="true" v-model="snackBarVisible">
        {{ snackBarMessage }}
        <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
    </v-snackbar>
    <advanced-filter ref="advanceFilter" @refresh-data="filterData" @save-filters="saveCurrentFilters" @delete-filter="deleteFilterSet"></advanced-filter>
    <v-dialog v-model="saveDialogVisible" scrollable fullscreen hide-overlay transition="dialog-bottom-transition">
        <v-card class="soft-grey-background">
            <v-toolbar dark color="primary">
                <v-toolbar-title>Review Selected Variants
                </v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon @click="exportSelectedVariants" slot="activator">
                        <v-icon>file_download</v-icon>
                    </v-btn>
                    <span>Export to CSV</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <v-btn icon @click="saveDialogVisible = false" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close</span>
                </v-tooltip>
            </v-toolbar>
            <v-card-text :style="getDialogMaxHeight()">
                <data-table ref="variantsSelected" :fixed="false" :fetch-on-created="false" table-title="Selected Variants" initial-sort="chromPos"
                    no-data-text="No Data" :show-row-count="true">
                </data-table>
            </v-card-text>
            <v-card-actions>
                <v-tooltip top>
                    <v-btn color="primary" :disabled="saveVariantDisabled" @click="saveSelection()" slot="activator">Save
                        <v-icon right dark>save</v-icon>
                    </v-btn>
                    <span>Save Variant</span>
                </v-tooltip>
                <v-btn color="error" @click="saveDialogVisible = false" slot="activator">Cancel
                    <v-icon right dark>cancel</v-icon>
                </v-btn>
                <v-breadcrumbs>
                    <v-icon slot="divider">chevron_right</v-icon>
                    <v-breadcrumbs-item
                      v-for="item in breadcrumbs"
                      :key="item.text"
                      :disabled="item.disabled"
                    >
                      {{ item.text }}
                    </v-breadcrumbs-item>
                  </v-breadcrumbs>
            </v-card-actions>
        </v-card>
    </v-dialog>

    <!-- annotation dialog -->
    <edit-annotations @saving-annotations="commitAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs" ref="annotationDialog" :title="currentVariant.geneName + ' ' + currentVariant.notation"></edit-annotations>

    <!-- variant details dialog -->
    <v-dialog v-model="variantDetailsVisible" scrollable fullscreen hide-overlay transition="dialog-bottom-transition">
        <v-card class="soft-grey-background">
            <v-toolbar dark color="primary">
                <v-menu offset-y offset-x class="ml-0">
                    <v-btn slot="activator" flat icon dark>
                        <v-icon>more_vert</v-icon>
                    </v-btn>
                    <v-list>

                        <v-list-tile avatar @click="annotationVariantDetailsVisible = !annotationVariantDetailsVisible">
                            <v-list-tile-avatar>
                                <v-icon>zoom_in</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Show/Hide Variant Details</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile avatar @click="annotationVariantCanonicalVisible = !annotationVariantCanonicalVisible">
                            <v-list-tile-avatar>
                                <v-icon>table_chart</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Show/Hide Canonical VCF Annotations</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile avatar @click="annotationVariantOtherVisible = !annotationVariantOtherVisible">
                            <v-list-tile-avatar>
                                <v-icon>table_chart</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Show/Hide Other VCF Annotations</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>


                        <v-list-tile avatar @click="mdaAnnotationsVisible = !mdaAnnotationsVisible" :disabled="!mdaAnnotationsExists()">
                            <v-list-tile-avatar>
                                <v-icon v-if="mdaAnnotationsExists()">mdi-message-bulleted</v-icon>
                                <v-icon v-if="!mdaAnnotationsExists()">mdi-message-bulleted-off</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title v-if="mdaAnnotationsExists()">Show/Hide MDA Annotations</v-list-tile-title>
                                <v-list-tile-title v-if="!mdaAnnotationsExists()">No MDA Annotations</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile avatar @click="utswAnnotationsVisible = !utswAnnotationsVisible" :disabled="!utswAnnotationsExists()">
                            <v-list-tile-avatar>
                                <v-icon v-if="utswAnnotationsExists()">mdi-message-bulleted</v-icon>
                                <v-icon v-if="!utswAnnotationsExists()">mdi-message-bulleted-off</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title v-if="utswAnnotationsExists()">Show/Hide UTSW Annotations</v-list-tile-title>
                                <v-list-tile-title v-if="!utswAnnotationsExists()">No UTSW Annotations</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile avatar @click="openBamViewerLink()">
                            <v-list-tile-avatar>
                                IGV
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Open Bam Viewer in New Tab</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                        <v-list-tile avatar @click="closeVariantDetails()">
                            <v-list-tile-avatar>
                                <v-icon>close</v-icon>
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
                <v-tooltip bottom>
                    <v-btn icon flat :color="annotationVariantCanonicalVisible ? 'amber accent-2' : ''" @click="annotationVariantCanonicalVisible = !annotationVariantCanonicalVisible"
                        slot="activator">
                        <v-icon>table_chart</v-icon>
                    </v-btn>
                    <span>Show/Hide Canonical VCF Annotations</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <v-btn icon flat :color="annotationVariantOtherVisible ? 'amber accent-2' : ''" @click="annotationVariantOtherVisible = !annotationVariantOtherVisible"
                        slot="activator">
                        <v-icon>table_chart</v-icon>
                    </v-btn>
                    <span>Show/Hide Other VCF Annotations</span>
                </v-tooltip>
                <v-tooltip bottom>
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
                <v-tooltip bottom>
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
            <v-card-text :style="getDialogMaxHeight()">
                <v-container grid-list-md fluid>
                    <v-layout row wrap>
                        <v-flex xs12 v-show="annotationAllHidden()">
                            <p class="text-xs-right subheading">
                                <v-icon style="transform: rotate(90deg) translateX(-5px);">keyboard_return</v-icon>
                                <span>Click on a icon to show variant details.</span>
                            </p>
                        </v-flex>
                        <!-- card showing the same data as the summary row -->
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
                                        <v-flex xs4 v-for="table in variantDataTables" :key="table.name">
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
                        <v-flex xs12>
                            <div class="pb-4 pt-2" v-show="annotationVariantCanonicalVisible">
                                <data-table ref="canonicalVariantAnnotation" :fixed="false" :fetch-on-created="false" table-title="Canonical VCF Annotations"
                                    initial-sort="geneId" no-data-text="No Data" :show-pagination="false" title-icon="table_chart">
                                </data-table>
                            </div>
                            <data-table v-show="annotationVariantOtherVisible" ref="otherVariantAnnotations" :fixed="false" :fetch-on-created="false"
                                table-title="Other VCF Annotations" initial-sort="geneId" no-data-text="No Data" :show-row-count="true"
                                title-icon="table_chart">
                            </data-table>
                        </v-flex>
                        <!-- MDA Annotation card -->
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
                                    <v-card class="mb-2" v-for="(annotation, index) in utswAnnotationsFormatted" :key="index">
                                        <v-card-text class="subheading">
                                            <v-container grid-list-md fluid>
                                                <v-layout row wrap>
                                                    <v-flex xs12>
                                                        From {{ annotation.fullName }} on
                                                        <span v-text="parseDate(annotation.modifiedDate)"></span>
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
                                                            <span>Open in new tab</span>
                                                        </v-tooltip>
                                                    </v-flex>
                                                </v-layout>
                                            </v-container>
                                        </v-card-text>
                                    </v-card>
                                </v-card-text>
                            </v-card>
                        </v-flex>
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
                <v-btn v-if="currentRow.isSelected" :disabled="saveDialogVisible" color="warning" @click="removeVariantFromReport()" slot="activator">Unselect Variant
                    <v-icon right dark>done</v-icon>
                </v-btn>
                <v-btn color="error" @click="closeVariantDetails()" slot="activator">Cancel
                    <v-icon right dark>cancel</v-icon>
                </v-btn>
                <v-breadcrumbs>
                    <v-icon slot="divider">chevron_right</v-icon>
                    <v-breadcrumbs-item
                      v-for="item in breadcrumbs"
                      :key="item.text"
                      :disabled="item.disabled"
                    >
                      {{ item.text }}
                    </v-breadcrumbs-item>
                  </v-breadcrumbs>
            </v-card-actions>
        </v-card>
    </v-dialog>



    <v-toolbar dark color="primary" fixed app>
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

                    <v-list-tile avatar @click="openSaveDialog()">
                        <v-list-tile-avatar>
                            <v-icon>save</v-icon>
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
            <v-btn flat icon @click="openSaveDialog()" slot="activator" :color="saveDialogVisible ? 'amber accent-2' : ''">
                <v-icon>save</v-icon>
            </v-btn>
            <span>Review Variants Selected</span>
        </v-tooltip>
    </v-toolbar>
    <v-progress-linear v-if="!caseName" :indeterminate="true"></v-progress-linear>

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

    <data-table ref="geneVariantDetails" :fixed="false" :fetch-on-created="false" table-title="Variants" initial-sort="chromPos"
        no-data-text="No Data" :enable-selection="true" :show-row-count="true">
        <v-tooltip bottom slot="action1">
            <v-btn slot="activator" flat icon @click="toggleFilters" :color="isAdvanceFilteringVisible() ? 'amber accent-2' : 'white'">
                <v-icon>filter_list</v-icon>
            </v-btn>
            <span>Advanced Filtering</span>
        </v-tooltip>
        <v-list-tile avatar @click="toggleFilters" slot="action1MenuItem">
            <v-list-tile-avatar>
                <v-icon>filter_list</v-icon>
            </v-list-tile-avatar>
            <v-list-tile-content>
                <v-list-tile-title>Advanced Filtering</v-list-tile-title>
            </v-list-tile-content>
        </v-list-tile>
    </data-table>



</div>` , data() {
        return {
            loading: true,
            breadcrumbs: [{text: "You are here: Case Overview", disabled: true}],
            breadcrumbItemVariantDetails: {text:"Variant Details", disabled: true},
            breadcrumbItemReview:  {text:"Review Selected Variants", disabled: true},
            breadcrumbItemEditAnnotation: {text:"Add / Edit Annotation", disabled: true},
            patientTables: [],
            patientDetailsVisible: true,
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
            annotationVariantOtherVisible: true,
            saveVariantDisabled: true,
            // annotationDialogVisible: false,
            userAnnotations: [],
            // userEditingAnnotations: [],
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
            externalWindowOpen: false
        }
    }, methods: {
        handleDialogs(response, callback) {
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
        getDialogMaxHeight() {
            var height = window.innerHeight - 120;
            return "min-height:" + height + "px;max-height:" + height + "px; overflow-y: auto";
        },
        getAjaxData() {
            this.loading = true;
            this.$refs.advanceFilter.loading = true;
            axios({
                method: 'post',
                url: webAppRoot + "/getCaseDetails",
                params: {
                    caseId: this.$route.params.id
                },
                data: {
                    filters: this.$refs.advanceFilter.filters
                }
            }).then(response => {
                if (response.data.isAllowed) {
                    this.patientTables = response.data.patientInfo.patientTables;
                    this.caseName = response.data.caseName;
                    this.caseId = response.data.caseId;
                    this.$refs.geneVariantDetails.manualDataFiltered(response.data);
                    this.$refs.advanceFilter.effects = response.data.effects;
                    this.userId = response.data.userId;
                    this.$refs.advanceFilter.populateCheckBoxes();
                    this.$refs.advanceFilter.filterNeedsReload = false;
                    this.addHeaderAction(response);
                }
                else {
                    this.handleDialogs(response.data, this.getAjaxData);
                }
                this.loading = false;
                this.$refs.advanceFilter.loading = false;
            }
            ).catch(error => {
                this.loading = false;
                this.$refs.advanceFilter.loading = false;
                console.log(error);
                bus.$emit("some-error", [this, error]);
            }
            );
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
                        this.$refs.advanceFilter.createFilters(response.data.filters);
                    }
                    else {
                        this.handleDialogs(response, this.getVariantFilters);
                    }
                }).catch(error => {
                    console.log(error);
                    bus.$emit("some-error", [this, error]);
                });
        },
        isAdvanceFilteringVisible() {
            return this.$refs.advanceFilter && this.$refs.advanceFilter.advanceFilteringVisible;
        },
        filterData() {
            this.getAjaxData();
        },
        toggleFilters() {
            this.$refs.advanceFilter.toggleFilters();
        },
        addHeaderAction(response) {
            for (var i = 0; i < response.data.headers.length; i++) {
                if (response.data.headers[i].value == "chromPos") {
                    response.data.headers[i].itemAction = this.openVariant;
                    response.data.headers[i].actionIcon = "zoom_in";
                    response.data.headers[i].actionTooltip = "Variant Details";
                    break;
                }
            }
        }, getVariantDetails(item) {
            this.currentVariantFlags = item.iconFlags.iconFlags;
            this.currentRow = item;
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
                        this.toggleHTMLOverlay(true);
                        this.variantDetailsVisible = true;
                    } else {
                        this.handleDialogs(response.data, this.getVariantDetails.bind(null,
                            item));
                    }
                }).catch(error => {
                    console.log(error);
                    bus.$emit("some-error", [this, error]);
                });
        },
        updateVariantVcfAnnotationTable() {
            var items = this.currentVariant.vcfAnnotations;
            var headers = this.vcfAnnotationHeaders;
            var headerOrder = this.vcfAnnotationHeadersOrder;
            this.$refs.variantsSelected.manualDataFiltered(
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
        handleIdLink(id) {
            var link = "";
            if (id.indexOf('rs') == 0) {
                link = "https://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=" + id;
            }
            else if (id.indexOf('COSM') == 0) {
                link = "http://www.google.com?search?q=" + id;
            }
            else {
                link = "https://www.bing.com/search?q=" + id;
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
                    modifiedDate: "",
                    pmids: [],
                    nctids: [],
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
                annotation.modifiedDate = annotations[i].modifiedDate;
                annotation.pmids = annotations[i].pmids;
                annotation.nctids = annotations[i].nctids;
                annotation.scopeTooltip = this.$refs.annotationDialog.createLevelInformation(annotations[i]);
                formatted.push(annotation);
            }
            return formatted;
        },
        startUserAnnotations() {
            this.$refs.annotationDialog.startUserAnnotations();
        },
        formatAnnotations() {
            this.utswAnnotationsFormatted = this.formatLocalAnnotations(this.utswAnnotations, true);
            // this.userAnnotationsFormatted = this.formatLocalAnnotations(this.userAnnotations, false);
        },
        commitAnnotations(userAnnotations) {
            this.userAnnotations = userAnnotations;
            axios({
                method: 'post',
                url: webAppRoot + "/commitAnnotations",
                params: {
                    caseId: this.$route.params.id,
                    geneId: this.currentVariant.geneName,
                    variantId: this.currentVariant._id.$oid
                },
                data: {
                    annotations: this.userAnnotations,
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.getVariantDetails(this.currentRow);
                        this.snackBarMessage = "Annotation(s) Saved";
                        this.snackBarVisible = true;
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
            var selectedVariants = this.$refs.geneVariantDetails.items.filter(item => item.isSelected);
            this.saveVariantDisabled = selectedVariants.length == 0;
            var headers = this.$refs.geneVariantDetails.headers;
            var headerOrder = this.$refs.geneVariantDetails.headerOrder; this.$refs.variantsSelected.manualDataFiltered(
                { items: selectedVariants, headers: headers, uniqueFieldId: "chromPos", headerOrder: headerOrder });
        },
        openSaveDialog() {
            this.updateSelectedVariantTable(); this.saveDialogVisible = true;
        },
        saveSelection() {
            var selectedVariantIds = this.$refs.geneVariantDetails.items.filter(item => item.isSelected).map(item => item.oid);
            axios({
                method:
                    'post',
                url: webAppRoot + "/saveVariantSelection",
                params: {
                    caseId: this.$route.params.id
                }, data: {
                    selectedVariantIds:
                        selectedVariantIds
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    this.saveDialogVisible = false;
                    this.snackBarMessage = "Variant Selection Saved";
                    this.snackBarVisible = true;
                    this.getAjaxData();
                }
                else {
                    this.handleDialogs(response.data, this.saveSelection);
                }
                this.loading = false;
            }).catch(error => {
                this.loading = false;
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
            $refs.advanceFilter.loading = true;
            axios({
                method: 'post',
                url: webAppRoot + "/saveCurrentFilters",
                params: {
                    filterListId: this.$refs.advanceFilter.saveFilterSetId,
                    filterListName: this.$refs.advanceFilter.saveFilterSetName
                },
                data: {
                    filters: this.$refs.advanceFilter.filters
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    this.loadUserFilterSets();
                    this.$refs.advanceFilter.currentFilterSet = response.data.savedFilterSet;
                    this.$refs.advanceFilter.saveFilterSetDialogVisible = false;
                    this.snackBarMessage = "Filter Set Saved";
                    this.snackBarVisible = true;
                }
                else {
                    this.handleDialogs(response.data, this.saveCurrentFilters);
                }
                this.$refs.advanceFilter.loading = false;
            }
            ).catch(error => {
                this.$refs.advanceFilter.loading = false;
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
                    this.$refs.advanceFilter.saveFilterSetDialogVisible = false;
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
                        this.$refs.advanceFilter.filterSets = response.data.filters;
                        this.$refs.advanceFilter.filterSetItems = response.data.items;

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
            var bam = "SHI710-27-6271_T_DNA_panel1385v2-1.final.bam";
            var bai = "SHI710-27-6271_T_DNA_panel1385v2-1.final.bai";
            var label = "test";
            link = "../bamViewer?";
            link += "locus=" + igvRange;
            link += "&bam=" + bam;
            link += "&bai=" + bai;
            link += "&label=" + label;
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
            axios({
                method: 'post',
                url: webAppRoot + "/exportSelection",
                params: {
                    caseId: this.$route.params.id
                },
                data: {
                    filters: []
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    console.log(response.data);
                    this.createCSVFile(response.data.message);
                }
                else {
                    this.handleDialogs(response.data, this.exportSelectedVariants);
                }
            }
            ).catch(error => {
                console.log(error);
                bus.$emit("some-error", [this, error]);
            }
            );
        },
        parseDate(dateWithTimeZone) {
            if (dateWithTimeZone) {
                return dateWithTimeZone.split("T")[0];
            }
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
            visible ? this.breadcrumbs.push(this.breadcrumbItemVariantDetails) : this.breadcrumbs.pop();
        },
        updateSaveDialogBreadCrumbs(visible) {
            visible ? this.breadcrumbs.push(this.breadcrumbItemReview) : this.breadcrumbs.pop();
        },
        updateEditAnnotationBreadcrumbs(visible) {
            visible ? this.breadcrumbs.push(this.breadcrumbItemEditAnnotation) : this.breadcrumbs.pop();
        }
    },
    mounted: function () {
        this.getAjaxData();
        this.loadUserFilterSets();
        bus.$emit("clear-item-selected", [this]); this.getVariantFilters();
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
        breadcrumbs: function() {
            this.$refs.annotationDialog.breadcrumbs = this.breadcrumbs;
        }
    }

};