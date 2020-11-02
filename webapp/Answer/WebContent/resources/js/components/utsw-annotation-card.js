

Vue.component('utsw-annotation-card', {
    props: {
        "annotation": Object,
        "variantType": {default: "snp", type: String},
        noEdit: { default: true, type: Boolean },
        caseAgnostic: { default: false, type: Boolean },
        currentUserId: { default: null, type: Number},
        canCopy: { default: false, type: Boolean },
        canHide: { default: false, type: Boolean }
    },
    template: /*html*/`<div>
    <v-card>
    <v-card-text :class="['subheading', !annotation.canEdit && caseAgnostic ? 'blue-grey lighten-3' : '', aboutToHide ? 'alpha-54' : '']">
        <v-container grid-list-md fluid class="white" pl-2 pr-2 pt-2 pb-2>
            <v-layout row wrap>
                <v-flex :class="[canHide? 'xs9' : 'xs10', 'pt-2']">
                    From {{ annotation.fullName }}
                    <span slot="activator" v-if="!annotation.warningLevel || annotation.warningLevel == 0"
                    v-text="parseDate(annotation)"></span>
                    <v-tooltip bottom v-if="annotation.warningLevel == 2">
                    <span slot="activator" v-text="parseDate(annotation)" class="orange--text text--darken-1 font-weight-bold"></span>
                    <span>This Therapy card is fairly old.<br/>
                    You may want to update its information before using it in a report.</span>
                    </v-tooltip>
                    <v-tooltip bottom v-if="annotation.warningLevel == 3">
                    <span slot="activator">
                    <span v-text="parseDate(annotation)" 
                    class="error--text font-weight-bold"></span>
                    <v-icon color="error">mdi-alert-decagram</v-icon>
                    </span>
                    <span>This Therapy card is very old.<br/>
                    You probalby should not use it in a report and create a new one<br/>
                    with updated information.</span>
                    </v-tooltip>
                </v-flex>
                <v-flex xs1 class="pt-0" v-if="canHide">
                <v-tooltip bottom>
                <v-btn color="primary" slot="activator" @click="aboutToHide = true" flat icon
                :disabled="noEdit || aboutToHide" class="mr-0">
                <v-icon>mdi-delete</v-icon>
                </v-btn>
                    <span>Delete this annotation permanently, for all users</span>
                    </v-tooltip>
                </v-flex>
                <v-flex xs1 class="pt-0">
                <!-- TODO disable copying for CNV annotations for now. Need to handle Chomosomal vs focal -->
                <v-tooltip bottom>
                <v-btn color="primary" slot="activator" @click="copyAnnotation" flat icon
                :disabled="noEdit || !canCopy" class="ml-0">
                <v-icon>mdi-content-copy</v-icon>
                </v-btn>
                    <span>Create a copy of this annotation</span>
                    </v-tooltip>
                </v-flex>
                <v-flex xs1>
                <v-tooltip bottom v-if="!caseAgnostic">
                    <v-switch color="primary" slot="activator"  hide-details class="no-height mt-0" :disabled="noEdit"
                    v-model="annotation.isSelected" @change="annotationSelectionChanged"></v-switch>
                    <span>Select/Unselect for Report</span>
                    </v-tooltip>
                    <v-tooltip bottom v-if="caseAgnostic">
                        <v-btn flat icon v-if="!noEdit" color="primary" :disabled="!annotation.canEdit"
                        @click="startUserAnnotation(annotation)" slot="activator">
                            <v-icon>create</v-icon>
                        </v-btn>
                        <span v-if="annotation.canEdit">Edit this annotation</span>
                        <span v-if="!annotation.canEdit">You cannot edit someone else's annotation</span>
                    </v-tooltip>
                </v-flex>
                <v-flex xs12>
                    Scope:
                    <v-tooltip bottom v-for="(scope, index) in annotation.scopes" :key="index">
                        <v-chip disabled outline slot="activator">
                            <span :class="scope ? 'green--text' : 'red--text'">{{ annotation.scopeLevels[index] }}</span>
                            <v-icon right v-if="scope" color="green">check</v-icon>
                            <v-icon right v-if="!scope" color="red">close</v-icon>
                        </v-chip>
                        <span v-text="annotation.scopeTooltip"> </span>
                    </v-tooltip>
                </v-flex>
                <v-flex xs12>
                    <span class="pr-1" v-if="annotation.category">
                        <b>{{ annotation.category }}:</b>
                    </span>
                    <span v-text="annotation.text"></span>
                </v-flex>
                <v-flex xs12 v-if="annotation.classification">
                    <span  class="pr-1">
                        <b>Classification:</b>
                    </span>
                    <span v-text="annotation.classification"></span>
                </v-flex>
                <v-flex xs12 v-if="annotation.tier" >
                    <span class="pr-1">
                        <b>Tier:</b>
                    </span>
                    <span v-text="annotation.tier"></span>
                </v-flex>
                <v-flex xs12 v-if="isCNV() && annotation.cnvGenes" class="pr-1">
                    Applies to genes: {{ annotation.cnvGenes }}
                </v-flex>
                <v-flex xs12 v-if="annotation.trial" class="pr-1">
                    <span class="pr-1">
                        <b>Phase:</b>
                    </span>
                    <span v-text="annotation.trial.phase"></span>
                </v-flex>
                <v-flex xs12 v-if="annotation.trial" class="pr-1">
                    <span class="pr-1">
                        <b>Biomarker(s):</b>
                    </span>
                    <span v-text="annotation.trial.biomarker"></span>
                </v-flex>
                <v-flex xs12 v-if="annotation.category == 'Clinical Trial'" class="pr-1">
                    <span class="pr-1">
                        <b>Title:</b>
                    </span>
                    <span v-text="annotation.trial.title"></span>
                </v-flex>
                <v-flex xs12 v-if="annotation.drugs" class="pr-1">
                    <span class="pr-1">
                        <b v-if="!annotation.drugResistant">Drugs:</b>
                        <b v-if="annotation.drugResistant">Drug Resistant:</b>
                    </span>
                    <span v-text="annotation.drugs"></span>
                </v-flex>
                <v-flex xs12>
                    <span v-if="annotation.pmids" class="selectable">PubMed Ids:</span>
                    <v-tooltip v-if="id" bottom v-for="id in annotation.pmids" :key="id">
                        <v-btn :href="getPubMedIdLink(id)" target="_blank" rel="noreferrer" slot="activator">
                            {{ id }}
                        </v-btn>
                        <span>Open in new tab</span>
                    </v-tooltip>
                    <span v-if="annotation.trial && annotation.trial.nctId" class="selectable">Open Trial:</span>
                    <v-tooltip v-if="annotation.trial" bottom>
                        <v-btn :href="getNCTIDLink(annotation.trial.nctId)" target="_blank" rel="noreferrer" slot="activator">
                            {{ annotation.trial.nctId }}
                        </v-btn>
                        <span>Open in new tab</span>
                    </v-tooltip>
                </v-flex>
            </v-layout>
        </v-container>
    </v-card-text>
    <v-card-actions v-if="aboutToHide">
        <span class="pr-2">This card will be deleted permanently, are you sure?</span>
        <v-btn @click="hideAnnotation" class="success">
            Yes, Delete
        </v-btn>
        <v-btn @click="aboutToHide = false" class="error">
            No, Cancel
        </v-btn>
    </v-card-actions>
</v-card>

</div>`,
    data() {
        return {
            aboutToHide: false
        }
    },
    methods: {
        // handlePubMedIdLink(id) {
        //     var link = "https://www.ncbi.nlm.nih.gov/pubmed/?term=" + id;
        //     window.open(link, "_blank");
        // },
        getPubMedIdLink(id) {
            return "https://www.ncbi.nlm.nih.gov/pubmed/?term=" + id;
        },
        // handleNCTIdLink(id) {
        //     var link = "https://clinicaltrials.gov/ct2/show/" + id;
        //     window.open(link, "_blank");
        // },
        getNCTIDLink(id) {
            return "https://clinicaltrials.gov/ct2/show/" + id;
        },
        parseDate(annotation) {
            if (annotation.modifiedDate) {
                return annotation.modifiedSince + " (" + annotation.modifiedDate.split("T")[0] + ")";
            }
        },
        isSNP() {
            return this.variantType == "snp";
        },
        isCNV() {
            return this.variantType == "cnv";
        },
        isTranslocation() {
            return this.variantType == "translocation";
        },
        isVirus() {
            return this.variantType == "virus";
        },
        annotationSelectionChanged() {
            this.$emit("annotation-selection-changed");
        },
        startUserAnnotation(annotation) {
            this.$emit("start-user-annotation", annotation);
        },
        copyAnnotation() {
            if (this.canCopy) {
                this.$emit("copy-annotation", this.annotation, this.variantType);
            }
        },
        hideAnnotation() {
            if (this.canHide) {
                this.aboutToHide = false;
                this.$emit("hide-annotation", this.annotation, this.variantType);
            }
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

