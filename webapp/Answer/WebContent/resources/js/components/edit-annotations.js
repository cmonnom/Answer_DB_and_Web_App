Vue.component('edit-annotations', {
    props: {
        title: { default: "", type: String },
        type: { default: "snp", type: String },
        limitScopeCase: { default: false, type: Boolean}, //not used yet
        limitScopeGene: { default: false, type: Boolean}, //not used yet
        limitScopeVariant: { default: false, type: Boolean}, //not used yet
        limitScopeChromosome: { default: false, type: Boolean}, //used by CNV to limit the breadth choices
        hideScope: {default: false, type: Boolean},
        color: {default: "primary", type: String},
        breadcrumbs: { default: () => [], type: Array },
        annotationCategories: {default: () => [], type: Array},
        annotationBreadth: {default: () => [], type: Array},
        annotationClassifications: {default:() => [], type: Array},
        annotationTiers: {default: () => [], type: Array},
        annotationCategoriesCNV: {default: () => [], type: Array},
        annotationPhases: {default: () => [], type: Array},
        currentVariant: {default: () => {}, type: Object},
        annotationVariantDetailsVisible: {default: true, type: Boolean},
        backColor: {default: "orange lighten-4", type: String},
        caseIcon: {default: "", type: String},
        caseType: {default: "", type: String},
        userAnnotations: { default: () => [], type: Array },
        single: {default: false, type: Boolean}, //only one annotation at a time (for editing outside of a case)
        outsideACase: {default: true, type: Boolean} //to display the variant details or not
    },
    template: /*html*/`<div>
    <!-- annotation dialog -->
    <v-dialog v-model="annotationDialogVisible" fullscreen transition="dialog-bottom-transition" :overlay="false" scrollable>
        <v-card ref="annotationDialog" class="soft-grey-background">
            <v-toolbar dense dark :color="color">
                <v-tooltip class="ml-0" bottom>
                    <v-menu offset-y offset-x slot="activator" class="ml-0">
                        <v-btn slot="activator" flat icon dark>
                            <v-icon>more_vert</v-icon>
                        </v-btn>
                        <v-list>
                        <v-list-tile avatar @click="togglePanel()" v-if="!outsideACase">
                            <v-list-tile-avatar>
                                <v-icon>zoom_in</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Show/Hide Variant Details</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                            <v-list-tile avatar @click="addCustomAnnotation()" :disabled="single">
                                <v-list-tile-avatar>
                                    <v-icon>playlist_add</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title>Create a New Annotation</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>

                            <v-list-tile avatar @click="addCustomTrial()" :disabled="single">
                            <v-list-tile-avatar>
                                <v-icon>assignment</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Add a New Trial</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>

                            <v-list-tile avatar @click="saveAnnotations()" :disabled="saveIsDisabled()">
                                <v-list-tile-avatar>
                                    <v-icon>save</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title>Save/Update Annotations</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>

                            <v-list-tile avatar @click="cancelAnnotations()">
                                <v-list-tile-avatar>
                                    <v-icon>cancel</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title>Discard Changes</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>
                        </v-list>
                    </v-menu>
                    <span>Annotation Menu</span>
                </v-tooltip>
                <v-toolbar-title><span v-text="createTitle()"></span>
                <v-tooltip bottom>
                <v-icon slot="activator" size="20" class="pb-1 pl-1"> {{ caseIcon }} </v-icon>
                <span>{{ caseType }} case</span>
                </v-tooltip>
                </v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon flat :color="annotationVariantDetailsVisible ? 'amber accent-2' : ''" @click="togglePanel()"
                    v-if="!outsideACase"
                    slot="activator">
                        <v-icon>zoom_in</v-icon>
                    </v-btn>
                    <span>Show/Hide Variant Details</span>
                 </v-tooltip>
                <v-tooltip bottom >
                    <v-btn icon slot="activator" @click="addCustomAnnotation()" :disabled="single">
                        <v-icon>playlist_add</v-icon>
                    </v-btn>
                    <span>Create a new annotation</span>
                </v-tooltip>
                <v-tooltip bottom >
                    <v-btn icon slot="activator" @click="saveAnnotations()" :disabled="saveIsDisabled()">
                        <v-icon>save</v-icon>
                    </v-btn>
                    <span v-if="!saveIsDisabled()">Save/Update Annotations</span>
                    <span v-else v-html="saveDisabledReasons"></span>
                </v-tooltip>
                <v-tooltip bottom >
                    <v-btn icon @click="cancelAnnotations()" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close and Discard Changes</span>
                </v-tooltip>
            </v-toolbar>
            <v-card-text :style="getDialogMaxHeight(130)" :class="['pl-3', 'pr-3', backColor, 'smooth-scroll']" ref="scrollableEditContent">
                <v-breadcrumbs class="pt-2">
                <v-icon slot="divider">forward</v-icon>
                    <v-breadcrumbs-item v-for="(item, index) in breadcrumbs" :key="item.text" :disabled="disableBreadCrumbItem(item, index)"  @click.native="breadcrumbNavigation(index)">
                    {{ item.text }}
                    </v-breadcrumbs-item>
                 </v-breadcrumbs>
                <v-card v-if="userEditingAnnotations.length == 0" class="mb-3">
                    <v-card-text>
                        Click on
                        <v-btn color="primary" @click="addCustomAnnotation()" :disabled="single">Add Annotation
                            <v-icon right dark>playlist_add</v-icon>
                        </v-btn> to create a new annotation.
                        Click on
                        <v-btn color="primary" @click="addCustomTrial()" :disabled="single">Add Trial
                            <v-icon right dark>assignment</v-icon>
                        </v-btn> to create a new trial.
                    </v-card-text>
                </v-card>

                <!-- variant details information -->
                <slot name="variantDetails"></slot>

                <v-card class="mb-4 soft-grey-background" v-if="userEditingAnnotations.length > 0" v-for="(annotation, index) in userEditingAnnotations" :key="index"
                    :color="annotation.markedForDeletion ? 'blue-grey lighten-4' : ''">
                    <v-toolbar class="elevation-0" dense dark :color="color">
                        <v-tooltip bottom>
                            <v-btn slot="activator" :color="annotation.isVisible ? 'amber accent-2' : ''" icon flat @click="annotation.isVisible = !annotation.isVisible">
                                <v-icon v-show="!annotation.isVisible">visibility_off</v-icon>
                                <v-icon v-show="annotation.isVisible">visibility</v-icon>
                            </v-btn>
                            <span>Show/Hide Annotation</span>
                        </v-tooltip>
                        <v-tooltip bottom>
                            <v-btn slot="activator" :color="annotation.markedForDeletion ? 'amber accent-2' : ''" icon flat @click="deleteAnnotation(annotation, index)">
                                <v-icon>delete</v-icon>
                            </v-btn>
                            <span>Delete Annotation</span>
                        </v-tooltip>
                        <span v-show="!annotation.markedForDeletion" v-text="truncateAnnotation(annotation)">
                        </span>
                        <span v-show="annotation.markedForDeletion">This annotation will be deleted on SAVE. Click
                            <v-icon>delete</v-icon> to cancel.
                        </span>
                    </v-toolbar>
                    <v-slide-y-transition>
                        <v-card-text v-show="annotation.isVisible">
                            <v-layout color="primary" row wrap>
                                <v-flex xs12>
                                    <v-form>
                                        <v-container grid-list-md fluid pl-2 pr-2 pt-2 pb-2>
                                            <v-layout row wrap>
                                                <!-- Scope -->
                                                <v-flex xs12 sm6 md4>
                                                    <v-card :color="annotation.markedForDeletion ? 'blue-grey lighten-4' : ''">
                                                    <v-card-text class="pl-3 pr-3 pt-3 pb-3" v-if="isSNP() && hideScope">
                                                        The scope has been preselected based on your annotation search.
                                                    </v-card-text>
                                                        <!-- SNP -->
                                                        <v-card-text class="pl-3 pr-3 pt-3 pb-3" v-if="isSNP() && !hideScope">
                                                            <div class="subheading pt-1">
                                                                The
                                                                <span :class="noLevelSelected(annotation) ? 'warning--text' : ''">scope</span> determines if this annotation applies to other
                                                                cases/genes/variants:
                                                            </div>
                                                            <v-tooltip bottom>
                                                                <v-switch hide-details slot="activator" class="no-height mt-0" :disabled="annotation.markedForDeletion || noLevelSelected(annotation) || outsideACase" label="Case Specific"
                                                                    v-model="annotation.isCaseSpecific" @change="selectBreadth(annotation)"></v-switch>
                                                                <span>Select if this annotation only applies to this case
                                                                    <br/>(need to select Gene or Variant Specific first)</span>
                                                            </v-tooltip>
                                                            <v-layout row wrap>
                                                            <v-flex :class="[outsideACase ? 'xs6' : 'xs12', 'pl-0']">
                                                            <v-tooltip bottom>
                                                                <v-switch hide-details slot="activator" class="no-height" :disabled="annotation.markedForDeletion" label="Gene Specific" v-model="annotation.isGeneSpecific"
                                                                    @change="selectBreadth(annotation, 'Gene Function')"></v-switch>
                                                                <span v-if="!annotation.isVariantSpecific">Select either Gene or Variant Specific or both</span>
                                                                <span v-if="annotation.isGeneSpecific && annotation.isVariantSpecific">Uncheck Variant Specific first</span>
                                                            </v-tooltip>
                                                            </v-flex>
                                                            <v-flex xs6 v-if="outsideACase">
                                                                <v-tooltip right>
                                                                <v-autocomplete slot="activator" clearable :value="annotation.geneId" :items="allGenes" v-model="annotation.geneId"
                                                                    label="Gene Symbol" single-line hide-details clearable
                                                                    item-text="name" item-value="value"
                                                                    class="no-height-select" @change="getVariantsForGene(annotation.geneId,annotation)"
                                                                    :disabled="annotation.markedForDeletion || !annotation.isGeneSpecific"></v-autocomplete>
                                                                    <span>Select a gene</span>
                                                                </v-tooltip>
                                                            </v-flex>
                                                            </v-layout>
                                                            <v-layout row wrap>
                                                            <v-flex :class="[outsideACase ? 'xs6' : 'xs12', 'pl-0']">
                                                            <v-tooltip bottom>
                                                                <v-switch hide-details slot="activator" class="no-height" :disabled="annotation.markedForDeletion || outsideACase" label="Variant Specific" v-model="annotation.isVariantSpecific"
                                                                    @change="selectBreadth(annotation, 'Variant Function')"></v-switch>
                                                                <span>Select either Gene or Variant Specific or both</span>
                                                            </v-tooltip>
                                                            </v-flex>
                                                            <v-flex xs6 v-if="outsideACase">
                                                                <v-tooltip right>
                                                                <v-autocomplete slot="activator" clearable :value="annotation.geneId" :items="annotation.variantItems" v-model="annotation.variantId"
                                                                    label="Variant Notation" single-line hide-details clearable
                                                                    item-text="name" item-value="value"
                                                                    class="no-height-select"
                                                                    :disabled="annotation.markedForDeletion || !annotation.isVariantSpecific || outsideACase"></v-autocomplete>
                                                                    <span v-if="annotation.variantItems">Select a variant</span>
                                                                    <span v-else>Select a gene first</span>
                                                                </v-tooltip>
                                                            </v-flex>
                                                            </v-layout>
                                                            <v-switch hide-details class="no-height" :disabled="annotation.markedForDeletion || outsideACase" 
                                                            label="Diagnosis Specific" v-model="annotation.isTumorSpecific"
                                                            ></v-switch>
                                                        </v-card-text>
                                                        <!-- CNV -->
                                                        <v-card-text class="pl-3 pr-3 pt-3 pb-3" v-if="isCNV()  && !hideScope">
                                                            <div class="subheading pb-2">
                                                                The
                                                                <span :class="noLevelSelected(annotation) ? 'warning--text' : ''">scope</span> determines if this annotation applies to other
                                                                cases:
                                                            </div>
                                                            <v-tooltip bottom>
                                                                <v-switch hide-details slot="activator" class="no-height" :disabled="annotation.markedForDeletion || noLevelSelected(annotation)" label="Case Specific"
                                                                    v-model="annotation.isCaseSpecific" @change="selectBreadth(annotation)"></v-switch>
                                                                <span>Select if this annotation applies to this case only</span>
                                                            </v-tooltip>
                                                            <v-switch hide-details class="no-height" :disabled="annotation.markedForDeletion" label="Diagnosis Specific" v-model="annotation.isTumorSpecific"></v-switch>
                                                        </v-card-text>
                                                        <!-- Translocation -->
                                                        <v-card-text class="pl-3 pr-3 pt-3 pb-3" v-if="isTranslocation()  && !hideScope">
                                                        <div class="subheading pb-2">
                                                            The
                                                            <span :class="noLevelSelected(annotation) ? 'warning--text' : ''">scope</span> determines if this annotation applies to other
                                                            cases or genes:
                                                        </div>
                                                        <v-tooltip bottom>
                                                            <v-switch hide-details slot="activator" class="no-height" :disabled="annotation.markedForDeletion || noLevelSelected(annotation)" label="Case Specific"
                                                                v-model="annotation.isCaseSpecific" @change="selectBreadth(annotation)"></v-switch>
                                                            <span>Select if this annotation applies to this case only</span>
                                                        </v-tooltip>
                                                        <v-switch hide-details class="no-height" :disabled="annotation.markedForDeletion" label="Diagnosis Specific" v-model="annotation.isTumorSpecific"></v-switch>
                                                        <v-tooltip bottom>
                                                            <v-switch hide-details slot="activator" class="no-height" :disabled="annotation.markedForDeletion || noLevelSelected(annotation)" :label="'Left Gene Specific: ' + annotation.leftGene"
                                                                v-model="annotation.isLeftSpecific"></v-switch>
                                                            <span>Select if this annotation applies to the left gene</span>
                                                        </v-tooltip>
                                                        <v-tooltip bottom>
                                                        <v-switch hide-details slot="activator" class="no-height" :disabled="annotation.markedForDeletion || noLevelSelected(annotation)" :label="'Right Gene Specific: ' + annotation.rightGene"
                                                            v-model="annotation.isRightSpecific"></v-switch>
                                                        <span>Select if this annotation applies to the right gene</span>
                                                    </v-tooltip>
                                                    </v-card-text>
                                                     <!-- Virus -->
                                                    <v-card-text class="pl-3 pr-3 pt-3 pb-3" v-if="isVirus()  && !hideScope">
                                                    <div class="subheading pb-2">
                                                        The
                                                        <span :class="noLevelSelected(annotation) ? 'warning--text' : ''">scope</span> determines if this annotation applies to other
                                                        cases or diagnoses:
                                                    </div>
                                                    <v-tooltip bottom>
                                                        <v-switch hide-details slot="activator" class="no-height" :disabled="annotation.markedForDeletion || noLevelSelected(annotation)" label="Case Specific"
                                                            v-model="annotation.isCaseSpecific" @change="selectBreadth(annotation)"></v-switch>
                                                        <span>Select if this annotation applies to this case only</span>
                                                    </v-tooltip>
                                                    <v-switch hide-details class="no-height" :disabled="annotation.markedForDeletion" label="Diagnosis Specific" v-model="annotation.isTumorSpecific"></v-switch>
                                                </v-card-text>


                                                    </v-card>
                                                </v-flex>
                                                <v-flex xs12 sm6 md4>
                                                    <v-card :color="annotation.markedForDeletion ? 'blue-grey lighten-4' : ''">
                                                        <!-- SNP  or fusion or virus-->
                                                        <v-card-text v-if="(isSNP() || isTranslocation() || isVirus()) && annotation.category != 'Clinical Trial'" class="pl-3 pr-3 pt-3 pb-3 subheading">
                                                            <v-layout row wrap>
                                                                <v-flex xs5 class="mt-2">
                                                                    Annotation Category:
                                                                </v-flex>
                                                                <v-flex xs7>
                                                                    <v-select hide-details  clearable :value="annotation.category" :disabled="annotation.markedForDeletion" :items="annotationCategories" v-model="annotation.category"
                                                                        label="Select a Category" single-line class="no-height no-height-select" max-height="400"></v-select>
                                                                </v-flex>
                                                            </v-layout>
                                                            <v-layout row wrap>
                                                                <v-flex xs5 class="mt-2">
                                                                    Classification:
                                                                </v-flex>
                                                                <v-flex xs7>
                                                                    <v-select hide-details  clearable :value="annotation.classification" :disabled="annotation.markedForDeletion" :items="annotationClassifications"
                                                                        v-model="annotation.classification" label="Select a Classification"
                                                                        single-line class="no-height no-height-select"></v-select>
                                                                </v-flex>
                                                            </v-layout>
                                                            <v-layout row wrap>
                                                                <v-flex xs5 class="mt-2">
                                                                    Tier:
                                                                </v-flex>
                                                                <v-flex xs7>
                                                                    <v-select hide-details  clearable :value="annotation.tier" :disabled="annotation.markedForDeletion" :items="annotationTiers" v-model="annotation.tier"
                                                                        label="Select a Tier" single-line class="no-height no-height-select"></v-select>
                                                                </v-flex>
                                                            </v-layout>
                                                        </v-card-text>
                                                        <!-- CNV -->
                                                        <v-card-text v-if="isCNV() && annotation.category != 'Clinical Trial'" class="pl-3 pr-3 pb-3 pt-3 subheading">
                                                            <v-layout row wrap>
                                                                <v-flex xs5 class="mt-2">
                                                                    Annotation Category:
                                                                </v-flex>
                                                                <v-flex xs7>
                                                                    <v-select hide-details  clearable :value="annotation.category" :disabled="annotation.markedForDeletion" :items="annotationCategoriesCNV" v-model="annotation.category"
                                                                        label="Select a Category" single-line class="no-height no-height-select"></v-select>
                                                                </v-flex>
                                                            </v-layout>
                                                            <v-layout row wrap>
                                                                <v-flex xs5 class="mt-2">
                                                                    Annotation Breadth:
                                                                </v-flex>
                                                                <v-flex xs7>
                                                                    <v-select hide-details  clearable required :value="annotation.breadth" :error="!annotation.breadth" :disabled="annotation.markedForDeletion" :items="annotationBreadth"
                                                                        v-model="annotation.breadth" label="Select Chrom vs Focal"
                                                                        single-line class="no-height no-height-select"></v-select>
                                                                </v-flex>
                                                            </v-layout>
                                                            <v-layout row wrap v-show="isCNV()" >
                                                                <v-flex xs5 class="mt-2 pt-4">
                                                                    Genes:
                                                                </v-flex>
                                                                <v-flex xs7>
                                                                    <v-select hide-details  clearable :value="annotation.cnvGenes" :disabled="annotation.markedForDeletion || annotation.breadth != 'Focal'" :items="cnvGeneItems" v-model="annotation.cnvGenes"
                                                                        label="Select Gene(s)" chips deletable-chips multiple
                                                                        single-line hide-details></v-select>
                                                                </v-flex>
                                                            </v-layout>
                                                            <v-layout row wrap>
                                                                <v-flex xs5 class="mt-2">
                                                                    Classification:
                                                                </v-flex>
                                                                <v-flex xs7>
                                                                    <v-select hide-details  clearable :value="annotation.classification" :disabled="annotation.markedForDeletion" :items="annotationClassifications"
                                                                        v-model="annotation.classification" label="Select a Classification"
                                                                        single-line class="no-height no-height-select"></v-select>
                                                                </v-flex>
                                                            </v-layout>
                                                            <v-layout row wrap>
                                                                <v-flex xs5 class="mt-2">
                                                                    Tier:
                                                                </v-flex>
                                                                <v-flex xs7>
                                                                    <v-select hide-details  clearable :value="annotation.tier" :disabled="annotation.markedForDeletion" :items="annotationTiers" v-model="annotation.tier"
                                                                        label="Select a Tier" single-line class="no-height no-height-select"></v-select>
                                                                </v-flex>
                                                            </v-layout>
                                                        </v-card-text>
                                                        <!-- Clincal Trial-->
                                                        <v-card-text v-if="annotation.category == 'Clinical Trial'" class="pl-3 pr-3 pt-3 pb-3 subheading">
                                                            <v-layout row wrap>
                                                                <v-flex xs5 class="mt-1">
                                                                    NCT ID:
                                                                </v-flex>
                                                                <v-flex xs5>
                                                                    <v-text-field v-model="annotation.trial.nctId" label="eg. NCT123456" single-line  :rules="nctRules"
                                                                    class="no-top-text-field"></v-text-field>
                                                                </v-flex>
                                                                <v-flex xs2>
                                                                <v-tooltip bottom>
                                                                    <v-btn color="primary" slot="activator" flat icon @click="fetchNCTData(annotation)" 
                                                                    :disabled="annotation.trial.nctId && !isNCTNumberList(annotation.trial.nctId)"
                                                                    :lodaing="loadingNCTData">
                                                                        <v-icon>cloud_download</v-icon>
                                                                    </v-btn>
                                                                    <span>Auto populate Clinical Trial from NCT ID</span>
                                                                </v-tooltip>
                                                                </v-flex>
                                                            </v-layout>
                                                            <v-layout row wrap>
                                                                <v-flex xs5 class="mt-1">
                                                                    Biomarker(s):
                                                                </v-flex>
                                                                <v-flex xs7>
                                                                    <v-text-field v-model="annotation.trial.biomarker" label="Biomarker(s)" single-line hide-details class="no-top-text-field"></v-text-field>
                                                                </v-flex>
                                                            </v-layout>
                                                            <v-layout row wrap>
                                                                <v-flex xs5>
                                                                    Phase:
                                                                </v-flex>
                                                                <v-flex xs7>
                                                                    <span class="blue-grey--text text--lighten-1">{{ annotation.trial.phase }} </span>
                                                                </v-flex>
                                                            </v-layout>
                                                        </v-card-text>
                                                    </v-card>
                                                </v-flex>
                                                <v-flex xs12 sm6 md4>
                                                    <v-card :color="annotation.markedForDeletion ? 'blue-grey lighten-4' : ''">
                                                        <v-card-text class="pl-3 pr-3 pt-3 pb-3 subheading">
                                                            <div v-show="noLevelSelected(annotation)" class="warning--text">You need to select an annotation's scope (Gene or Variant Specific
                                                                or both).</div>
                                                            <div v-if="annotation.createdDate">
                                                                <b>Created on: </b>
                                                                <span v-text="parseDate(annotation.createdDate)"></span>
                                                            </div>
                                                            <div v-if="annotation.modifiedDate">
                                                                <b>Modified on: </b>
                                                                <span v-text="parseDate(annotation.modifiedDate)"></span>
                                                            </div>
                                                            <div v-show="!noLevelSelected(annotation)" v-text="createLevelInformation(annotation)"></div>
                                                            <div v-show="annotation.isTumorSpecific">This annotation is tumor specific.</div>
                                                            <div v-show="!annotation.isTumorSpecific">This annotation is tumor agnostic.</div>
                                                        </v-card-text>
                                                    </v-card>
                                                </v-flex>
                                                <v-flex xs12 class="pt-2" v-if="annotation.category != 'Clinical Trial'">
                                                    <v-textarea v-show="annotation.isVisible" ref="editAnnotation" v-model="annotation.text" class="mr-2 no-height"
                                                        :disabled="annotation.markedForDeletion" label="Write your comments here">
                                                    </v-textarea>
                                                </v-flex>
                                                <v-flex xs12 lg7 v-if="annotation.category == 'Therapy'">
                                                    <v-layout>
                                                        <v-flex class="mt-3 pt-2 subheading">Drugs:</v-flex>
                                                        <v-flex xs7>
                                                            <v-text-field :disabled="annotation.markedForDeletion" label="(comma separated)" v-model="annotation.drugs"></v-text-field>
                                                        </v-flex>
                                                        <v-flex xs3 class="mt-3 pt-2 pl-2">
                                                            <v-tooltip bottom>
                                                            <v-switch hide-details slot="activator" class="no-height" :disabled="annotation.markedForDeletion" label="Drug Resistant" v-model="annotation.drugResistant"></v-switch>
                                                                <span>Activate this button to make the variant resistant to the drugs listed.</span>
                                                            </v-tooltip>
                                                        </v-flex>
                                                    </v-layout>
                                                </v-flex>
                                                <v-flex xs12 lg5>
                                                    <v-layout>
                                                        <v-flex class="mt-3 pt-2 subheading">PubMed Ids:</v-flex>
                                                        <v-flex xs8>
                                                            <v-text-field :disabled="annotation.markedForDeletion" label="(comma separated)" v-model="annotation.pmids" :rules="numberRules"></v-text-field>
                                                        </v-flex>
                                                    </v-layout>
                                                </v-flex>
                                                <v-flex xs12 sm12 md8 v-if="annotation.category == 'Clinical Trial'">
                                                <v-card :color="annotation.markedForDeletion ? 'blue-grey lighten-4' : ''">
                                                    <v-card-text class="pl-3 pr-3 pt-3 pb-3 subheading">
                                                        <v-layout row wrap>
                                                            <v-flex xs2 class="subheading">Title:</v-flex>
                                                            <v-flex xs10>
                                                                <span class="blue-grey--text text--lighten-1">{{ annotation.trial.title }} </span>
                                                            </v-flex>
                                                        </v-layout>
                                                        <v-layout row wrap>
                                                            <v-flex xs2 class="subheading pt-2">Drugs:</v-flex>
                                                            <v-flex xs10>
                                                                <v-text-field single-line :disabled="annotation.markedForDeletion" label="Drugs" v-model="annotation.trial.drugs"
                                                                class="no-top-text-field"
                                                                    ></v-text-field>
                                                            </v-flex>
                                                            <!--
                                                            <v-flex xs4>
                                                            <v-tooltip bottom>
                                                            <v-switch hide-details slot="activator" class="no-height" :disabled="annotation.markedForDeletion" label="Drug Resistant" v-model="annotation.drugResistant"></v-switch>
                                                                <span>Activate this button to make the variant resistant to the drugs listed.</span>
                                                            </v-tooltip>
                                                        </v-flex>
                                                        -->
                                                        </v-layout>
                                                    </v-card-text>
                                                </v-card>
                                                </v-flex>
                                                <v-flex xs12 sm8 md4 v-if="annotation.category == 'Clinical Trial'">
                                                <v-card :color="annotation.markedForDeletion ? 'blue-grey lighten-4' : ''">
                                                    <v-card-text class="pl-3 pr-3 pt-3 pb-3 subheading">
                                                        <v-layout row wrap>
                                                            <v-flex class="subheading">Contact:</v-flex>
                                                            <v-flex xs10>
                                                            <span class="blue-grey--text text--lighten-1" v-html="annotation.trial.contact"></span>
                                                            </v-flex>
                                                        </v-layout>
                                                        <v-layout row wrap>
                                                            <v-flex class="subheading mt-2">Location:</v-flex>
                                                            <v-flex xs10>
                                                            <v-text-field single-line class="no-top-text-field" :disabled="annotation.markedForDeletion" label="City, State" v-model="annotation.trial.location"
                                                            ></v-text-field>
                                                            </v-flex>
                                                        </v-layout>
                                                    </v-card-text>
                                                </v-card>
                                                </v-flex>
                                            </v-layout>
                                        </v-container>
                                    </v-form>
                                </v-flex>
                            </v-layout>
                        </v-card-text>
                    </v-slide-y-transition>
                </v-card>
            </v-card-text>
            <v-card-actions :class="['card-actions-bottom', backColor]">
                <v-tooltip top class="pr-2">
                    <v-btn slot="activator" color="primary" @click="addCustomAnnotation()" :disabled="single">Add Annotation
                        <v-icon right dark>playlist_add</v-icon>
                    </v-btn>
                    <span>Create a new annotation</span>
                </v-tooltip>
                <v-tooltip top class="pr-2">
                <v-btn slot="activator" color="primary" @click="addCustomTrial()" :disabled="single">Add Trial
                    <v-icon right dark>assignment</v-icon>
                </v-btn>
                <span>Create a new annotation</span>
            </v-tooltip>
                <v-tooltip top class="pr-2">
                    <v-btn slot="activator" color="success" @click="saveAnnotations()" :disabled="saveIsDisabled()">Save / Update
                        <v-icon right dark>save</v-icon>
                    </v-btn>
                    <span v-if="!saveIsDisabled()">Save/Update Annotations</span>
                    <span v-else v-html="saveDisabledReasons"></span>
                </v-tooltip>
                <v-tooltip top class="pr-2">
                    <v-btn slot="activator" color="error" @click="cancelAnnotations()">Cancel
                        <v-icon right dark>cancel</v-icon>
                    </v-btn>
                    <span>Discard changes</span>
                </v-tooltip>
            </v-card-actions>
        </v-card>
    </v-dialog>

</div>`
    , data() {
        return {
            // breadcrumbs: [],
            annotationDialogVisible: false,
            userEditingAnnotations: [],
            numberRules: [(v) => { return this.isNumberList(v) || 'Only numbers, separated by comma' }],
            nctRules: [(v) => { return this.isNCTNumberList(v) || 'Must start with NCT + number. If more than one, use a comma' }],
           
            cnvGeneItems: [],
            saving: false,
            loadingNCTData: false,
            saveDisabledReasons: "",
            allGenes: [],
        }

    },
    methods: {
        getDialogMaxHeight(offset) {
            getDialogMaxHeight(offset);
        },
        startUserAnnotations() {
            //first make a copy of annotations for editing 
            //this will allow to cancel without modifying the existing annotations 
            this.userEditingAnnotations = [];
            for (var i = 0; i < this.userAnnotations.length; i++) {
                //make a hard copy of the annotation
                var tempAnnotation = JSON.parse(JSON.stringify(this.userAnnotations[i]));
                //need to convert pmid arrays into strings and remove possible dups
                var tempSet = new Set();
                if (tempAnnotation.pmids) {
                    for (var s = 0; s < tempAnnotation.pmids.length; s++) {
                        tempSet.add(tempAnnotation.pmids[s]);
                    }
                }
                tempAnnotation.pmids = tempSet.size != 0 ? Array.from(tempSet).join(',') : null;
                // tempSet = new Set();
                // if (tempAnnotation.nctIds) {
                //     for (var s = 0; s < tempAnnotation.nctIds.length; s++) {
                //         tempSet.add(tempAnnotation.nctIds[s]);
                //     }
                // }
                // tempAnnotation.nctIds = tempSet.size != 0 ? Array.from(tempSet).join(',') : null;
                tempAnnotation.isVisible = true;
                this.userEditingAnnotations.push(tempAnnotation);
            }
            // if (this.userEditingAnnotations.length == 0) {
            //     this.addCustomAnnotation();
            // }
            this.annotationDialogVisible = true;
        },
        addCustomAnnotation() {
            if (this.single)  {
                return;
            }
            for (var i = 0; i < this.userEditingAnnotations.length; i++) {
                this.userEditingAnnotations[i].isVisible = false;
            }
            for (var i = 0; i < this.userAnnotations.length; i++) {
                this.userAnnotations[i].isVisible = false;
            }
            this.userEditingAnnotations.push({
                origin: "UTSW",
                text: "",
                markedForDeletion: false,
                isVisible: true,
                geneId: null,
                caseId: null,
                pmids: "",
                isTumorSpecific: false,
                userId: null,
                variantId: null,
                isGeneSpecific: this.limitScopeGene || (this.isCNV() || this.isTranslocation() ? true : false),
                isVariantSpecific: this.isCNV() || this.isTranslocation() || this.isVirus() ? true : false,
                isCaseSpecific: false,
                isLeftSpecific: false,
                isRightSpecific: false,
                category: null,
                createdDate: null,
                modifiedDate: null,
                _id: null,
                classification: null,
                tier: null,
                // nctIds: "",
                type: this.type,
                cnvGenes: [],
                leftGene: this.currentVariant.leftGene,
                rightGene: this.currentVariant.rightGene,
                trial: null,
                drugs: "",
                warningLevel: 0,
                drugResistant: false,
                breadth: this.isCNV() ? "Chromosomal" : null

            });
          this.scrollToBottom();
        },
        addCustomTrial() {
            if (this.single) {
                return;
            }
            this.addCustomAnnotation();
            var annotation = this.userEditingAnnotations[this.userEditingAnnotations.length - 1];
            annotation.trial = {
                    nctId: "",
                    title: "",
                    phase: "",
                    biomarker: "",
                    drugs: "",
                    contact: "",
                    location: ""
            }
            annotation.category = "Clinical Trial";
            annotation.isGeneSpecific = this.isSNP(); //not gene specific for 
            this.scrollToBottom();    
        },
        scrollToBottom() {
            //scroll to show new annotation
            setTimeout(() => {
                this.$nextTick( () => {
                    var height = this.$refs.scrollableEditContent.scrollHeight;
                     this.$refs.scrollableEditContent.scrollTo(0,height);
                });
            }, 500);
        },
        saveAnnotations() {
             // There is a bug in vuetify 1.0.19 where a disabled menu still activates the click action.
            // Use a flag to disable the action in the meantime
            if (this.saveIsDisabled()) {
                return;
            }
            this.saving = true;
            // this.annotationDialogVisible = false;
            this.userAnnotations.length = 0;
            for (var i = 0; i < this.userEditingAnnotations.length; i++) {
                var annotation = JSON.parse(JSON.stringify(this.userEditingAnnotations[i]));
                //make sure ids are unique
                var tempSet = new Set();
                if (annotation.pmids) {
                    var pmidsArray = annotation.pmids.split(",");
                    for (var s = 0; s < pmidsArray.length; s++) {
                        tempSet.add(pmidsArray[s]);
                    }
                }
                annotation.pmids = tempSet.size != 0 ? Array.from(tempSet) : null;
                if (annotation.breadth == 'Chromosomal') {
                    annotation.cnvGenes = [];
                }
                if (annotation.category == "Clinical Trial") {
                    annotation.text = annotation.trial.nctId;
                }
                if (annotation.isVariantSpecific) {
                    annotation.isGeneSpecific = true;
                }
                this.userAnnotations.push(annotation);
            }
            this.$emit("saving-annotations", this.userAnnotations);
        },
        cancelAnnotations() {
            this.saving = false;
            this.annotationDialogVisible = false;
        },
        isNumberList(v) {
            var valid = !isNaN(v);
            if (!valid) {
                valid = true;
                //check if separated by comma
                var items = v.split(",");
                for (var i = 0; i < items.length; i++) {
                    valid = valid && !isNaN(items[i].trim());
                }
            }
            return valid;
        },
        isNCTNumberList(v) {
            if (!v) {
                return true;
            }
            //check if separated by comma
            var valid = true;
            var items = v.split(",");
            for (var i = 0; i < items.length; i++) {
                var item = items[i].trim();
                if (item != "") {
                    valid = valid && (item.indexOf('NCT') == 0);
                    valid = valid && !isNaN(item.replace('NCT', ''));
                }
            }
            return valid;
        },
        saveIsDisabled() {
            var scopeSelected = true;
            for (var i = 0; i < this.userEditingAnnotations.length; i++) {
                var annotation = this.userEditingAnnotations[i];
                scopeSelected = scopeSelected && !this.noLevelSelected(annotation);
            }
            var breadthSelected = true;
            if (this.isCNV()) {
                for (var i = 0; i < this.userEditingAnnotations.length; i++) {
                    var annotation = this.userEditingAnnotations[i];
                    breadthSelected = breadthSelected && annotation.breadth; //need to select breadth
                }
            }
            var trialsHaveNCTID = true;
            var caseAgnosticHasGeneSymbol = true;
            var caseAgnosticHasVariant = true;
            var length = this.userEditingAnnotations.length;
            for (var i = 0; i < length; i++) {
                if (this.userEditingAnnotations[i].trial) {
                    if (!this.userEditingAnnotations[i].trial.nctId || !this.isNCTNumberList(this.userEditingAnnotations[i].trial.nctId)) {
                        trialsHaveNCTID = false;
                    }
                    else if (!this.userEditingAnnotations[i].trial.phase) {
                        trialsHaveNCTID = false;
                    }
                    else if (!this.userEditingAnnotations[i].trial.biomarker) {
                        trialsHaveNCTID = false;
                    }
                    else if (!this.userEditingAnnotations[i].trial.title) {
                        trialsHaveNCTID = false;
                    }
                    else if (!this.userEditingAnnotations[i].trial.drugs) {
                        trialsHaveNCTID = false;
                    }
                    else if (!this.userEditingAnnotations[i].trial.contact) {
                        trialsHaveNCTID = false;
                    }
                    else if (!this.userEditingAnnotations[i].trial.location) {
                        trialsHaveNCTID = false;
                    }
                }
                if (this.userEditingAnnotations[i].isGeneSpecific && 
                    this.outsideACase && !this.userEditingAnnotations[i].geneId) { //need to select a gene if outside a case
                        caseAgnosticHasGeneSymbol = false
                }
                if (this.userEditingAnnotations[i].isVariantSpecific && 
                    this.outsideACase && !this.userEditingAnnotations[i].variantId) { //need to select a gene if outside a case
                        caseAgnosticHasVariant = false
                }
            }
            var saveDisabledReasons = [];
            if (!scopeSelected) {
                saveDisabledReasons.push("Some annotations don't have a scope");
            }
            if (length == 0) {
                saveDisabledReasons.push("No annotations to edit or save");
            }
            if (this.saving) {
                saveDisabledReasons.push("Currently saving. Please wait");
            }
            if (!trialsHaveNCTID) {
                saveDisabledReasons.push("Some Clinical Trials are incomplete (all fields are required)");
            }
            if (!caseAgnosticHasGeneSymbol) {
                saveDisabledReasons.push("Select a Gene Symbol for Gene Specific annotations");
            }
            if (!caseAgnosticHasVariant) {
                saveDisabledReasons.push("Select a Variant Notation for Variant Specific annotations");
            }
            if (!breadthSelected) {
                saveDisabledReasons.push("All CNVs must have a breadth selected");
            }
            this.saveDisabledReasons = saveDisabledReasons.join("<br/>");
            return !scopeSelected || length == 0 || this.saving 
            || !trialsHaveNCTID || !caseAgnosticHasGeneSymbol || !caseAgnosticHasVariant || !breadthSelected;
        },
        //at least one level needs to be selected
        //can't only be case specific: needs either gene or variant
        noLevelSelected(annotation) {
            return !annotation.isGeneSpecific
                && !annotation.isVariantSpecific;
        },
        createLevelInformation(annotation) {
            var text = "This annotation's scope is limited to ";
            var commaNeeded = false;
            if (annotation.isCaseSpecific) {
                text = text + "this case";
                commaNeeded = true;
            }
            if (annotation.isGeneSpecific && !this.isVirus()) {
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
        fetchNCTData(annotation) {
            this.loadingNCTData = true;
            axios.get(
                webAppRoot + "/fetchNCTData",
                {
                    params: {
                        nctId: annotation.trial.nctId,
                    }
                })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        var trial = response.data;
                        for (var i = 0; i < this.userEditingAnnotations.length; i++) {
                            var an = this.userEditingAnnotations[i];
                            if (an.trial && an.trial.nctId == trial.nctId) {
                                an.trial.phase = trial.phase;
                                an.trial.title = trial.title;
                                an.trial.contact = trial.contact;
                                var biomarker = "";
                                if (this.isSNP()) {
                                    biomarker = this.currentVariant.geneName;
                                    if (an.isVariantSpecific) {
                                        biomarker += " " + this.currentVariant.notation;
                                    }
                                }
                                else if (this.isCNV()) {
                                    biomarker = this.currentVariant.chrom;
                                }
                                else {
                                    biomarker = this.currentVariant.leftGene + "--" + this.currentVariant.rightGene;
                                }
                                an.trial.biomarker = biomarker;
                            }
                        }
                    }
                    else {
                        this.handleDialogs(response.data, this.fetchNCTData.bind(this, annotation));
                    }
                    this.loadingNCTData = false;
                }).catch(error => {
                    this.handleAxiosError(error);
                    this.loadingNCTData = false;
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
        },
        handleAxiosError(error) {
            console.log(error);
            bus.$emit("some-error", [this, error]);
        },
        selectBreadth(annotation, breadth) {
            if (breadth && !annotation.selectedBreadth) {
                annotation.selectedBreadth = this.annotationCategories.filter(item => item == breadth)[0];
            }
            if (annotation.isVariantSpecific) {
                annotation.isGeneSpecific = true && !this.isVirus();
                if (annotation.geneId && this.single) {
                    this.getVariantsForGene(annotation.geneId, annotation);
                }
            }
            if (annotation.isGeneSpecific) {
                this.$emit("get-variant-for-gene", annotation.geneId);
            }
            if (!annotation.isGeneSpecific) {
                annotation.genedId = null;
            }
        },
        deleteAnnotation(annotation, index) {
            if (annotation._id) {
                annotation.markedForDeletion = !annotation.markedForDeletion;
            }
            else { //remove the new, unsaved annotation
                this.userEditingAnnotations.splice(index, 1);

            }
        },
        parseDate(dateWithTimeZone) {
            if (dateWithTimeZone) {
                return dateWithTimeZone.split("T")[0];
            }
        },
        truncateAnnotation(annotation) {
            if (!annotation.text) {
                if (annotation.modifiedDate) {
                    return "Saved on " + this.parseDate(annotation.modifiedDate);
                }
                if (annotation.category == "Clinical Trial") {
                    return "New Clinical Trial";
                }
                return "New Annotation";
            }
            if (annotation.text.length > 30) {
                return annotation.text.substring(0, 30) + "...";
            }
            return annotation.text;
        },
        isSNP() {
            return this.type == "snp";
        },
        isCNV() {
            return this.type == "cnv";
        },
        isTranslocation() {
            return this.type == "translocation";
        },
        isVirus() {
            return this.type == "virus" || this.type == "VIR";
        },
        createTitle() {
            if (this.outsideACase && !this.single) {
                return "Create Annotations";
            }
            if (this.outsideACase && this.single) {
                return "Edit Annotation";
            }
            if (this.limitScopeGene) {
                return "Create/Edit Annotations for gene: " + this.title;
            }
            else {
                var typeTitle = "";
                if (this.isSNP()) {
                    typeTitle = "SNP";
                }
                else if (this.isCNV()) {
                    typeTitle = "CNV";
                }
                else if (this.isTranslocation()) {
                    typeTitle = "FTL";
                }
                else if (this.isVirus()) {
                    return "Create/Edit Annotations for " +
                " Virus: " + this.title;
                }
                return "Create/Edit Annotations for " + typeTitle +
                " Variant: " + this.title;
            }
            
        },
        togglePanel() {
            this.$emit("toggle-panel", this);
        },
        disableBreadCrumbItem(item, index) {
            return (item.disabled || index == this.breadcrumbs.length - 1);
        },
        breadcrumbNavigation(index) {
            this.$emit("breadcrumb-navigation", index);
        },
        getAllGenes() { 
            axios.get(webAppRoot + "/getGenesInPanel", {
              params: {
              }
            })
              .then(response => {
                if (response.data.isAllowed) {
                  this.allGenes = response.data.items;
                }
                else {
                  this.handleDialogs(response.data, this.getGenesInPanel);
                }
              })
              .catch(error => {
                alert(error);
              });
          },
          getVariantsForGene(geneId, annotation) { //TODO
            if (!geneId) {
                return;
            }
            axios.get(webAppRoot + "/getVariantsForGene", {
              params: {
                geneId: geneId,
                annotationId: annotation._id.$oid
              }
            })
              .then(response => {
                if (response.data.isAllowed && response.data.success) {
                    for (var i = 0; i < this.userAnnotations.length; i++) {
                        if (this.userAnnotations[i]._id.$oid == response.data.payload.annotationId) {
                            this.userAnnotations[i].variantItems = response.data.payload;
                            break;
                        }
                    }
                }
                else {
                  this.handleDialogs(response.data, this.getVariantsForGene.bind(null, geneId, annotation));
                }
              })
              .catch(error => {
                alert(error);
              });
          },
    },
    created: function () {
    },
    destroyed: function () {
    },
    mounted() {
        this.getAllGenes();
    },
    computed: {
    },
    watch: {
        annotationDialogVisible: function () {
            this.$emit("annotation-dialog-changed", this.annotationDialogVisible);
        }
    }


});