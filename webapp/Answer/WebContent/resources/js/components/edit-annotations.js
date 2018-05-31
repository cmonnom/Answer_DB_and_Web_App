Vue.component('edit-annotations', {
    props: {
        title: { default: "", type: String },
    },
    template: `<div>
    <!-- annotation dialog -->
    <v-dialog v-model="annotationDialogVisible" fullscreen transition="dialog-bottom-transition" :overlay="false" scrollable>
        <v-card ref="annotationDialog" class="soft-grey-background">
            <v-toolbar dark color="primary" class="mb-2">
                <v-toolbar-title>Your Annotations for variant: {{ title }}</v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon @click="annotationDialogVisible = false" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close Annotations</span>
                </v-tooltip>
            </v-toolbar>
            <v-card-text :style="getDialogMaxHeight()">
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
                    <v-toolbar dark color="primary">
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
                            <v-flex xs12 >
                                <v-form>
                                    <v-container grid-list-md fluid>
                                        <v-layout row wrap>
                                            <v-flex xs12 sm6 md4>
                                                <v-card :color="annotation.markedForDeletion ? 'blue-grey lighten-4' : ''">
                                                    <v-card-text class="card__text_default">
                                                        <div class="subheading pb-2">
                                                            The
                                                            <span :class="noLevelSelected(annotation) ? 'warning--text' : ''">scope</span> determines if this annotation applies to other cases/genes/variants:
                                                        </div>
                                                        <v-switch class="no-height" :disabled="annotation.markedForDeletion" label="Case Specific" v-model="annotation.isCaseSpecific"
                                                            @change="selectCategory(annotation)"></v-switch>
                                                        <v-switch class="no-height" :disabled="annotation.markedForDeletion" label="Gene Specific" v-model="annotation.isGeneSpecific"
                                                            @change="selectCategory(annotation, 'Gene Function')"></v-switch>
                                                        <v-switch class="no-height" :disabled="annotation.markedForDeletion" label="Variant Specific" v-model="annotation.isVariantSpecific"
                                                            @change="selectCategory(annotation, 'Variant Function')"></v-switch>
                                                        <v-switch class="no-height" :disabled="annotation.markedForDeletion" label="Tumor Specific" v-model="annotation.isTumorSpecific"></v-switch>
                                                    </v-card-text>
                                                </v-card>
                                            </v-flex>
                                            <v-flex xs12 sm6 md4>
                                                <v-card :color="annotation.markedForDeletion ? 'blue-grey lighten-4' : ''">
                                                    <v-card-text class="card__text_default subheading">
                                                        <v-layout row wrap>
                                                            <v-flex xs5 class="mt-1">
                                                                Annotation Category:
                                                            </v-flex>
                                                            <v-flex xs7>
                                                                <v-select clearable 
                                                                item-value="text"
                                                                item-text="text" 
                                                                :value="annotation.selectedCategory" 
                                                                :disabled="annotation.markedForDeletion" :items="annotationCategories" v-model="annotation.selectedCategory" label="Select a Category"
                                                                    single-line class="no-height no-height-select"></v-select>
                                                            </v-flex>
                                                        </v-layout>
                                                        <v-layout row wrap>
                                                            <v-flex xs5 class="mt-1">
                                                                Classification:
                                                            </v-flex>
                                                            <v-flex xs7>
                                                                <v-select clearable 
                                                                item-value="text"
                                                                item-text="text" 
                                                                :value="annotation.selectedClassification" 
                                                                :disabled="annotation.markedForDeletion" :items="annotationClassifications" v-model="annotation.selectedClassification" label="Select a Classification"
                                                                    single-line class="no-height no-height-select"></v-select>
                                                            </v-flex>
                                                        </v-layout>
                                                        <v-layout row wrap>
                                                            <v-flex xs5 class="mt-1">
                                                                Tier:
                                                            </v-flex>
                                                            <v-flex xs7>
                                                                <v-select clearable 
                                                                item-value="text"
                                                                item-text="text" 
                                                                :value="annotation.selectedTier" 
                                                                :disabled="annotation.markedForDeletion" :items="annotationTiers" v-model="annotation.selectedTier" label="Select a Tier"
                                                                    single-line class="no-height no-height-select"></v-select>
                                                            </v-flex>
                                                        </v-layout>
                                                    </v-card-text>
                                                </v-card>
                                            </v-flex>
                                            <v-flex xs12 sm6 md4>
                                                <v-card :color="annotation.markedForDeletion ? 'blue-grey lighten-4' : ''">
                                                    <v-card-text class="card__text_default subheading">
                                                        <div v-show="noLevelSelected(annotation)" class="warning--text">You need to select an annotation's scope.</div>
                                                        <div v-if="annotation.createdDate">
                                                            <b>Created on: </b><span v-text="parseDate(annotation.createdDate)"></span></div>
                                                        <div v-if="annotation.modifiedDate">
                                                            <b>Modified on: </b><span v-text="parseDate(annotation.modifiedDate)"></span></div>
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
                                            <v-flex xs12>
                                                <v-layout>
                                                    <v-flex class="mt-4 subheading">PubMed Ids:</v-flex>
                                                    <v-flex xs4>
                                                        <v-text-field :disabled="annotation.markedForDeletion"  label="(comma separated)"
                                                            v-model="annotation.pmids" :rules="numberRules"></v-text-field>
                                                    </v-flex>
                                                    <v-flex class="mt-4 subheading">NCT Ids:</v-flex>
                                                    <v-flex xs4>
                                                        <v-text-field :disabled="annotation.markedForDeletion"  label="Clinical Trials (eg. NCT123456, comma separated)"
                                                            v-model="annotation.nctids" :rules="nctRules"></v-text-field>
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
            <v-card-actions>
                <v-btn color="primary" @click="addCustomAnnotation()">Add
                    <v-icon right dark>playlist_add</v-icon>
                </v-btn>
                <v-btn color="success" @click="saveAnnotations()" :disabled="saveIsDisabled()">Save
                    <v-icon right dark>save</v-icon>
                </v-btn>
                <v-btn color="error" @click="cancelAnnotations()">Cancel
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

</div>`
    , data() {
        return {
            breadcrumbs: [],
            annotationDialogVisible: false,
            userAnnotations: [],
            userEditingAnnotations: [],
            numberRules: [(v) => { return this.isNumberList(v) || 'Only numbers, separated by comma' }],
            nctRules: [(v) => { return this.isNCTNumberList(v) || 'Must start with NCT + number. If more than one, use a comma' }],
            annotationCategories: [
                { text: 'Gene Function' },
                { text: 'Variant Function' },
                { text: 'Therapy' }],
            annotationClassifications: [
                { text: 'VUS' },
                { text: 'Benign' },
                { text: 'Likely benign' },
                { text: 'Likely pathogenic' },
                { text: 'Pathogenic' }],
            annotationTiers: [
                { text: '1A' },
                { text: '2A' },
                { text: '2B' },
                { text: '3' },
                { text: '4' },
                { text: '5' }],
        }

    },
    methods: {
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
                tempAnnotation.selectedCategory = { "text": tempAnnotation.category };
                tempAnnotation.selectedClassification = { "text": tempAnnotation.classification };
                tempAnnotation.selectedTier = { "text": tempAnnotation.tier };
                tempAnnotation.isVisible = true;
                this.userEditingAnnotations.push(tempAnnotation);
            }
            this.annotationDialogVisible = true;
        },
        addCustomAnnotation() {
            //TODO 
            for(var i = 0; i < this.userEditingAnnotations.length; i++) {
                this.userEditingAnnotations[i].isVisible = false;
            }
            for(var i = 0; i < this.userAnnotations.length; i++) {
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
                isVariantSpecific: false,
                isCaseSpecific: false,
                category: null,
                selectedCategory: null,
                createdDate: null,
                modifiedDate: null,
                _id: null,
                classification: null,
                tier: null,
                selectedTier: null,
                selectedClassification: null,
                nctids: ""
            });
            // this.$nextTick(function () {
            //     this.$refs.editAnnotation[this.$refs.editAnnotation.length - 1].focus(); this.$vuetify.goTo(
            //         "textarea:last-child");
            // });
        },
        saveAnnotations() {
            this.annotationDialogVisible = false;
            this.userAnnotations = [];
            for (var i = 0; i < this.userEditingAnnotations.length; i++) {
                var annotation = JSON.parse(JSON.stringify(this.userEditingAnnotations[i]));
                annotation.pmids = annotation.pmids ? annotation.pmids.split(",") : null;
                annotation.nctids = annotation.nctids ? annotation.nctids.split(",") : null;
                annotation.category = annotation.selectedCategory ? annotation.selectedCategory.text : null;
                annotation.classification = annotation.selectedClassification ? annotation.selectedClassification.text : null;
                annotation.tier = annotation.selectedTier ? annotation.selectedTier.text : null;
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
        getDialogMaxHeight() {
            var height = window.innerHeight - 130;
            return "min-height:" + height + "px;max-height:" + height + "px; overflow-y: auto";
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
        noLevelSelected(annotation) {
            return !annotation.isCaseSpecific
                && !annotation.isGeneSpecific
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
                annotation.selectedCategory = this.annotationCategories.filter(item => item.text == category)[0];
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
                    return "Saved on " + annotation.modifiedDate;
                }
                return "New Annotation";
            }
            if (annotation.text.length > 20) {
                return annotation.text.substring(0,20) + "...";
            }
            return annotation.text;
        }

    },
    created: function () {
    },
    destroyed: function () {
    },
    computed: {
    },
    watch: {
        annotationDialogVisible: function() {
            this.$emit("annotation-dialog-changed", this.annotationDialogVisible);
        }
    }


});