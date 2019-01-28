

Vue.component('mda-annotation-card', {
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
                <!-- TODO disable copying for CNV annotations for now. Need to handle Chomosomal vs focal -->
                <v-tooltip bottom v-if="isSNP()">
                <v-btn color="primary" slot="activator" @click="copyMDAAnnotation()" flat icon
                :disabled="noEdit">
                <v-icon>mdi-content-copy</v-icon>
                </v-btn>
                    <span>Create a UTSW annotation from this card</span>
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
                <v-flex xs12 v-if="isCNV() && annotation.cnvGenes" class="pr-1">
                    Applies to genes: {{ annotation.cnvGenes }}
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
        handleNCTIdLink(id) {
            var link = "https://clinicaltrials.gov/ct2/show/" + id;
            window.open(link, "_blank");
        },
        parseDate(annotation) {
            if (annotation.createdDate) {
                return annotation.createdSince + " (" + annotation.createdDate + ")";
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
        copyMDAAnnotation() {
            this.$emit("copy-mda-annotation", this.annotation, this.variantType);
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

