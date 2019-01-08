

Vue.component('utsw-annotation-card', {
    props: {
        "annotation": Object,
        "variantType": {default: "snp", type: String},
        noEdit: { default: true, type: Boolean },
        caseAgnostic: { default: false, type: Boolean },
        currentUserId: { default: null, type: Number}
    },
    template: `<div>
    <v-card>
    <v-card-text :class="['subheading', !annotation.canEdit && caseAgnostic ? 'blue-grey lighten-3' : '']">
        <v-container grid-list-md fluid class="white">
            <v-layout row wrap>
                <v-flex xs11>
                    From {{ annotation.fullName }}
                    <span v-text="parseDate(annotation)" :class="annotation.warningLevel == 2 ? 'error--text font-weight-bold' : ''"></span>
                </v-flex>
                <v-flex xs1>
                <v-tooltip bottom v-if="!caseAgnostic">
                    <v-switch color="primary" slot="activator" class="no-height" :disabled="noEdit"
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
                        <span v-html="annotation.scopeTooltip"> </span>
                    </v-tooltip>
                </v-flex>
                <v-flex xs12>
                    <span class="pr-1" v-if="annotation.category">
                        <b>{{ annotation.category }}:</b>
                    </span>
                    <span v-html="annotation.text"></span>
                </v-flex>
                <v-flex xs12 v-if="annotation.classification">
                    <span  class="pr-1">
                        <b>Classification:</b>
                    </span>
                    <span v-html="annotation.classification"></span>
                </v-flex>
                <v-flex xs12 v-if="annotation.tier" >
                    <span class="pr-1">
                        <b>Tier:</b>
                    </span>
                    <span v-html="annotation.tier"></span>
                </v-flex>
                <v-flex xs12 v-if="isCNV() && annotation.cnvGenes" class="pr-1">
                    Applies to genes: {{ annotation.cnvGenes }}
                </v-flex>
                <v-flex xs12 v-if="annotation.trial" class="pr-1">
                    <span class="pr-1">
                        <b>Phase:</b>
                    </span>
                    <span v-html="annotation.trial.phase"></span>
                </v-flex>
                <v-flex xs12 v-if="annotation.trial" class="pr-1">
                    <span class="pr-1">
                        <b>Biomarker(s):</b>
                    </span>
                    <span v-html="annotation.trial.biomarker"></span>
                </v-flex>
                <v-flex xs12 v-if="annotation.category == 'Clinical Trial'" class="pr-1">
                    <span class="pr-1">
                        <b>Title:</b>
                    </span>
                    <span v-html="annotation.trial.title"></span>
                </v-flex>
                <v-flex xs12 v-if="annotation.drugs" class="pr-1">
                    <span class="pr-1">
                        <b v-if="!annotation.drugResistant">Drugs:</b>
                        <b v-if="annotation.drugResistant">Drug Resistant:</b>
                    </span>
                    <span v-html="annotation.drugs"></span>
                </v-flex>
                <v-flex xs12>
                    <span v-if="annotation.pmids" class="selectable">PubMed Ids:</span>
                    <v-tooltip v-if="id" bottom v-for="id in annotation.pmids" :key="id">
                        <v-btn @click="handlePubMedIdLink(id)" slot="activator">
                            {{ id }}
                        </v-btn>
                        <span>Open in new tab</span>
                    </v-tooltip>
                    <span v-if="annotation.trial && annotation.trial.nctId" class="selectable">Open Trial:</span>
                    <v-tooltip v-if="annotation.trial" bottom>
                        <v-btn @click="handleNCTIdLink(annotation.trial.nctId)" slot="activator">
                            {{ annotation.trial.nctId }}
                        </v-btn>
                        <span>Open in new tab</span>
                    </v-tooltip>
                </v-flex>
            </v-layout>
        </v-container>
    </v-card-text>
</v-card>

</div>`,
    data() {
        return {
        }
    },
    methods: {
        handlePubMedIdLink(id) {
            var link = "https://www.ncbi.nlm.nih.gov/pubmed/?term=" + id;
            window.open(link, "_blank");
        },
        handleNCTIdLink(id) {
            var link = "https://clinicaltrials.gov/ct2/show/" + id;
            window.open(link, "_blank");
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
        annotationSelectionChanged() {
            this.$emit("annotation-selection-changed");
        },
        startUserAnnotation(annotation) {
            this.$emit("start-user-annotation", annotation);
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

