Vue.component('edit-annotations', {
    props: {
        title: { default: "", type: String },
        type: { default: "snp", type: String }
    },
    template: `<div>
    <!-- annotation dialog -->
    <v-dialog v-model="annotationDialogVisible" fullscreen transition="dialog-bottom-transition" :overlay="false" scrollable>
        <v-card ref="annotationDialog" class="soft-grey-background">
            <v-toolbar dense dark color="primary" class="mb-2">
                <v-tooltip class="ml-0" bottom>
                    <v-menu offset-y offset-x slot="activator" class="ml-0">
                        <v-btn slot="activator" flat icon dark>
                            <v-icon>more_vert</v-icon>
                        </v-btn>
                        <v-list>
                            <v-list-tile avatar @click="addCustomAnnotation()">
                                <v-list-tile-avatar>
                                    <v-icon>playlist_add</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title>Create a New Annotation</v-list-tile-title>
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
                <v-toolbar-title>Your Annotations for variant: {{ title }}</v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom >
                    <v-btn icon slot="activator" @click="addCustomAnnotation()">
                        <v-icon>playlist_add</v-icon>
                    </v-btn>
                    <span>Create a new annotation</span>
                </v-tooltip>
                <v-tooltip bottom >
                    <v-btn icon slot="activator" @click="saveAnnotations()" :disabled="saveIsDisabled()">
                        <v-icon>save</v-icon>
                    </v-btn>
                    <span>Save/Update Annotations</span>
                </v-tooltip>
                <v-tooltip bottom >
                    <v-btn icon @click="cancelAnnotations()" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close and Discard Changes</span>
                </v-tooltip>
            </v-toolbar>
            <v-card-text :style="getDialogMaxHeight(130)">
                <v-card v-if="userEditingAnnotations.length == 0">
                    <v-card-text>
                        Click on
                        <v-btn color="primary" @click="addCustomAnnotation()">Add
                            <v-icon right dark>playlist_add</v-icon>
                        </v-btn> to create a new annotation.
                    </v-card-text>
                </v-card>
                <v-card class="mb-4" v-if="userEditingAnnotations.length > 0" v-for="(annotation, index) in userEditingAnnotations" :key="index"
                    :color="annotation.markedForDeletion ? 'blue-grey lighten-4' : ''">
                    <v-toolbar dense dark color="primary">
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
                                        <v-container grid-list-md fluid>
                                            <v-layout row wrap>
                                                <v-flex xs12 sm6 md4>
                                                    <v-card :color="annotation.markedForDeletion ? 'blue-grey lighten-4' : ''">
                                                        <!-- SNP -->
                                                        <v-card-text class="card__text_default" v-if="isSNP()">
                                                            <div class="subheading pb-2">
                                                                The
                                                                <span :class="noLevelSelected(annotation) ? 'warning--text' : ''">scope</span> determines if this annotation applies to other
                                                                cases/genes/variants:
                                                            </div>
                                                            <v-tooltip bottom>
                                                                <v-switch slot="activator" class="no-height" :disabled="annotation.markedForDeletion || noLevelSelected(annotation)" label="Case Specific"
                                                                    v-model="annotation.isCaseSpecific" @change="selectCategory(annotation)"></v-switch>
                                                                <span>Select if this annotation only applies to this case
                                                                    <br/>(need to select Gene or Variant Specific first)</span>
                                                            </v-tooltip>
                                                            <v-tooltip bottom>
                                                                <v-switch slot="activator" class="no-height" :disabled="annotation.markedForDeletion" label="Gene Specific" v-model="annotation.isGeneSpecific"
                                                                    @change="selectCategory(annotation, 'Gene Function')"></v-switch>
                                                                <span>Select either Gene or Variant Specific or both</span>
                                                            </v-tooltip>
                                                            <v-tooltip bottom>
                                                                <v-switch slot="activator" class="no-height" :disabled="annotation.markedForDeletion" label="Variant Specific" v-model="annotation.isVariantSpecific"
                                                                    @change="selectCategory(annotation, 'Variant Function')"></v-switch>
                                                                <span>Select either Gene or Variant Specific or both</span>
                                                            </v-tooltip>
                                                            <v-switch class="no-height" :disabled="annotation.markedForDeletion" label="Tumor Specific" v-model="annotation.isTumorSpecific"></v-switch>
                                                        </v-card-text>
                                                        <!-- CNV and Translocation -->
                                                        <v-card-text class="card__text_default" v-if="isCNV() || isTranslocation()">
                                                            <div class="subheading pb-2">
                                                                The
                                                                <span :class="noLevelSelected(annotation) ? 'warning--text' : ''">scope</span> determines if this annotation applies to other
                                                                cases:
                                                            </div>
                                                            <v-tooltip bottom>
                                                                <v-switch slot="activator" class="no-height" :disabled="annotation.markedForDeletion || noLevelSelected(annotation)" label="Case Specific"
                                                                    v-model="annotation.isCaseSpecific" @change="selectCategory(annotation)"></v-switch>
                                                                <span>Select if this annotation only applies to this case only</span>
                                                            </v-tooltip>
                                                            <v-switch class="no-height" :disabled="annotation.markedForDeletion" label="Tumor Specific" v-model="annotation.isTumorSpecific"></v-switch>
                                                        </v-card-text>
                                                    </v-card>
                                                </v-flex>
                                                <v-flex xs12 sm6 md4 v-if="!isTranslocation()">
                                                    <v-card :color="annotation.markedForDeletion ? 'blue-grey lighten-4' : ''">
                                                        <!-- SNP -->
                                                        <v-card-text v-if="isSNP()" class="card__text_default subheading">
                                                            <v-layout row wrap>
                                                                <v-flex xs5 class="mt-1">
                                                                    Annotation Category:
                                                                </v-flex>
                                                                <v-flex xs7>
                                                                    <v-select clearable :value="annotation.category" :disabled="annotation.markedForDeletion" :items="annotationCategories" v-model="annotation.category"
                                                                        label="Select a Category" single-line class="no-height no-height-select"></v-select>
                                                                </v-flex>
                                                            </v-layout>
                                                            <v-layout row wrap>
                                                                <v-flex xs5 class="mt-1">
                                                                    Classification:
                                                                </v-flex>
                                                                <v-flex xs7>
                                                                    <v-select clearable :value="annotation.classification" :disabled="annotation.markedForDeletion" :items="annotationClassifications"
                                                                        v-model="annotation.classification" label="Select a Classification"
                                                                        single-line class="no-height no-height-select"></v-select>
                                                                </v-flex>
                                                            </v-layout>
                                                            <v-layout row wrap>
                                                                <v-flex xs5 class="mt-1">
                                                                    Tier:
                                                                </v-flex>
                                                                <v-flex xs7>
                                                                    <v-select clearable :value="annotation.tier" :disabled="annotation.markedForDeletion" :items="annotationTiers" v-model="annotation.tier"
                                                                        label="Select a Tier" single-line class="no-height no-height-select"></v-select>
                                                                </v-flex>
                                                            </v-layout>
                                                        </v-card-text>
                                                        <!-- CNV -->
                                                        <v-card-text v-if="isCNV()" class="card__text_default subheading">
                                                            <v-layout row wrap>
                                                                <v-flex xs5 class="mt-1">
                                                                    Annotation Category:
                                                                </v-flex>
                                                                <v-flex xs7>
                                                                    <v-select clearable :value="annotation.category" :disabled="annotation.markedForDeletion" :items="annotationCategoriesCNV"
                                                                        v-model="annotation.category" label="Select a Category"
                                                                        single-line class="no-height no-height-select"></v-select>
                                                                </v-flex>
                                                            </v-layout>
                                                            <v-layout row wrap v-show="isCNV() && annotation.category == 'Focal'">
                                                                <v-flex xs5 class="mt-1">
                                                                    Genes:
                                                                </v-flex>
                                                                <v-flex xs7>
                                                                    <v-select clearable :value="annotation.cnvGenes" :disabled="annotation.markedForDeletion" :items="cnvGeneItems" v-model="annotation.cnvGenes"
                                                                        label="Select Gene(s)" chips deletable-chips multiple
                                                                        single-line hide-details></v-select>
                                                                </v-flex>
                                                            </v-layout>
                                                        </v-card-text>
                                                    </v-card>
                                                </v-flex>
                                                <v-flex xs12 sm6 md4>
                                                    <v-card :color="annotation.markedForDeletion ? 'blue-grey lighten-4' : ''">
                                                        <v-card-text class="card__text_default subheading">
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
                                                <v-flex xs12 class="pt-2">
                                                    <v-text-field v-show="annotation.isVisible" ref="editAnnotation" :textarea="true" v-model="annotation.text" class="mr-2 no-height"
                                                        :disabled="annotation.markedForDeletion" label="Write your comments here">
                                                    </v-text-field>
                                                </v-flex>
                                                <v-flex xs12 v-if="isSNP()">
                                                    <v-layout>
                                                        <v-flex class="mt-4 subheading">PubMed Ids:</v-flex>
                                                        <v-flex xs4>
                                                            <v-text-field :disabled="annotation.markedForDeletion" label="(comma separated)" v-model="annotation.pmids" :rules="numberRules"></v-text-field>
                                                        </v-flex>
                                                        <v-flex class="mt-4 subheading">NCT Ids:</v-flex>
                                                        <v-flex xs4>
                                                            <v-text-field :disabled="annotation.markedForDeletion" label="Clinical Trials (eg. NCT123456, comma separated)" v-model="annotation.nctids"
                                                                :rules="nctRules"></v-text-field>
                                                        </v-flex>
                                                    </v-layout>
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
            <v-card-actions class="card-actions-bottom">
                <v-tooltip top>
                    <v-btn slot="activator" color="primary" @click="addCustomAnnotation()">Add
                        <v-icon right dark>playlist_add</v-icon>
                    </v-btn>
                    <span>Create a new annotation</span>
                </v-tooltip>
                <v-tooltip top>
                    <v-btn slot="activator" color="success" @click="saveAnnotations()" :disabled="saveIsDisabled()">Save / Update
                        <v-icon right dark>save</v-icon>
                    </v-btn>
                    <span>Save/Update Annotations</span>
                </v-tooltip>
                <v-tooltip top>
                    <v-btn slot="activator" color="error" @click="cancelAnnotations()">Cancel
                        <v-icon right dark>cancel</v-icon>
                    </v-btn>
                    <span>Discard changes</span>
                </v-tooltip>
                <breadcrumbs>
                </breadcrumbs>
            </v-card-actions>
        </v-card>
    </v-dialog>

</div>`
    , data() {
        return {
            // breadcrumbs: [],
            annotationDialogVisible: false,
            userAnnotations: [],
            userEditingAnnotations: [],
            numberRules: [(v) => { return this.isNumberList(v) || 'Only numbers, separated by comma' }],
            nctRules: [(v) => { return this.isNCTNumberList(v) || 'Must start with NCT + number. If more than one, use a comma' }],
            annotationCategories: [
                'Gene Function',
                'Variant Function',
                'Therapy'],
            annotationCategoriesCNV: [
                'Chromosomal',
                'Focal'],
            annotationClassifications: [
                'VUS',
                'Benign',
                'Likely benign',
                'Likely pathogenic',
                'Pathogenic'],
            annotationTiers: [
                '1A',
                '1B',
                '2A',
                '2B',
                '3',
                '4',
                '5'],
            cnvGeneItems: []    
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
                //need to convert pmid arrays into strings
                tempAnnotation.pmids = tempAnnotation.pmids ? tempAnnotation.pmids.join(",") : null;
                tempAnnotation.nctids = tempAnnotation.nctids ? tempAnnotation.nctids.join(",") : null;
                // tempAnnotation.selectedCategory = tempAnnotation.category;
                // tempAnnotation.selectedClassification = tempAnnotation.classification;
                // tempAnnotation.selectedTier = tempAnnotation.tier;
                tempAnnotation.isVisible = true;
                this.userEditingAnnotations.push(tempAnnotation);
            }
            this.annotationDialogVisible = true;
        },
        addCustomAnnotation() {
            //TODO 
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
                isGeneSpecific: false,
                isVariantSpecific: this.isCNV() || this.isTranslocation() ? true : false,
                isCaseSpecific: false,
                category: null,
                // selectedCategory: null,
                createdDate: null,
                modifiedDate: null,
                _id: null,
                classification: null,
                tier: null,
                // selectedTier: null,
                // selectedClassification: null,
                nctids: "",
                type: this.type,
                cnvGenes: []
            });
            // this.$nextTick(function () {
            //     this.$refs.editAnnotation[this.$refs.editAnnotation.length - 1].focus(); this.$vuetify.goTo(
            //         "textarea:last-child");
            // });
        },
        saveAnnotations() {
             // There is a bug in vuetify 1.0.19 where a disabled menu still activates the click action.
            // Use a flag to disable the action in the meantime
            if (this.saveIsDisabled()) {
                return;
            }
            this.annotationDialogVisible = false;
            this.userAnnotations = [];
            for (var i = 0; i < this.userEditingAnnotations.length; i++) {
                var annotation = JSON.parse(JSON.stringify(this.userEditingAnnotations[i]));
                annotation.pmids = annotation.pmids ? annotation.pmids.split(",") : null;
                annotation.nctids = annotation.nctids ? annotation.nctids.split(",") : null;
                // annotation.category = annotation.selectedCategory ? annotation.selectedCategory : null;
                // annotation.classification = annotation.selectedClassification ? annotation.selectedClassification : null;
                // annotation.tier = annotation.selectedTier ? annotation.selectedTier : null;
                this.userAnnotations.push(annotation);
            }
            this.$emit("saving-annotations", this.userAnnotations);
        },
        cancelAnnotations() {
            this.annotationDialogVisible = false;
            this.$nextTick(function () { //wait until dialog is closed 
                this.userEditingAnnotations = [];
                this.$emit("cancel-annotations", null);
            });
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
            return !scopeSelected || this.userEditingAnnotations.length == 0;
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
                commaNeeded = true;
                text = text + "this case";
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
            }
            text = text + ".";
            return text;
        },
        selectCategory(annotation, category) {
            if (category && !annotation.selectedCategory) {
                annotation.selectedCategory = this.annotationCategories.filter(item => item == category)[0];
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

    },
    created: function () {
    },
    destroyed: function () {
    },
    computed: {
    },
    watch: {
        annotationDialogVisible: function () {
            this.$emit("annotation-dialog-changed", this.annotationDialogVisible);
        }
    }


});