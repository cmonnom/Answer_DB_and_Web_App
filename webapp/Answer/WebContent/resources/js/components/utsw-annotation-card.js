

Vue.component('utsw-annotation-card', {
    props: {
        "annotation": Object,
        "variantType": {default: "snp", type: String},
        noEdit: { default: true, type: Boolean },
    },
    template: `<div>
    <v-card>
    <v-card-text class="subheading">
        <v-container grid-list-md fluid>
            <v-layout row wrap>
                <v-flex xs11>
                    From {{ annotation.fullName }}
                    <span v-text="parseDate(annotation)"></span>
                </v-flex>
                <v-flex xs1>
                <v-tooltip bottom>
                    <v-switch slot="activator" class="no-height" :disabled="noEdit"
                    v-model="annotation.isSelected" @change="annotationSelectionChanged"></v-switch>
                    <span>Select/Unselect for Report</span>
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

