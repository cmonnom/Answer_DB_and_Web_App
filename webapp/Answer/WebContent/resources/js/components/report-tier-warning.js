

Vue.component('report-tier-warning', {
    props: {
        variantsMissingTier: {default: () => [], type: Array},
        commitingAnnotations: {default: false, type: Boolean}
    },
    template: /*html*/`<div v-show="variantsMissingTier.length > 0">
    <v-toolbar dense dark color="error">
    <v-toolbar-title>
        Some Annotations are Incomplete
    </v-toolbar-title>
</v-toolbar>
<v-card  class="pl-2 pt-2 pr-2 pb-2 mb-3" >
<v-card-text class="subheading">
        <v-layout row wrap>
        <v-flex xs12 md6 class="pt-2 mt-1">
        The following variants have been selected for this report but their annotations cards are missing something.<br/>
        <div v-if="anyCNV()" class="pt-2">
            <v-btn slot="activator" @click="bypassAllCNVWarnings()" :disabled="commitingAnnotations">
            Include all <span class="pl-1 pr-1" v-text="missingCNVCount()"></span> CNV annotations below <v-icon class="pl-2">mdi-auto-fix</v-icon>
            </v-btn>
        </div>
        <v-layout row wrap align-end>
        <v-flex xs12 v-for="variant in variantsMissingTier" :key="variant.id">
        <v-icon color="error">warning</v-icon>
        <span  v-if="variant.type == 'SNP'">{{variant.type}} {{ variant.chrom }}:{{ variant.name}} {{variant.notation }}</span>
        <span  v-if="variant.type == 'CNV'">{{variant.type}} {{ variant.chrom }}</span>
        <span  v-if="variant.type == 'TRANSLOCATION'">FTL {{ variant.name }}</span>
        <v-tooltip bottom>
        <v-btn class="mr-0" icon flat slot="activator" :href="createVariantEditLink(variant)" target="_blank" rel="noreferrer">
        <v-icon>zoom_in</v-icon>
        </v-btn>
        <span>Open variant in a new tab</span>
        </v-tooltip>
        <v-tooltip bottom v-if="variant.type == 'CNV'">
        <v-btn icon flat slot="activator" @click="bypassCNVWarning(variant)" class="ml-0 mt-0">
        <v-icon class="rotate270">mdi-auto-fix</v-icon>
        </v-btn>
        <span>Include this CNV anyway by creating a tier 3 chromosomal annotation</span>
        </v-tooltip>
        </v-flex>
        </v-layout>
        Click on each variant to edit its annotations then click <v-btn @click="getReportDetails()">NEW REPORT</v-btn> to refresh the changes when done.
        </v-flex>
    
        <v-flex xs12 md6>
            <v-expansion-panel class="elevation-0">
                <v-expansion-panel-content>
                    <div slot="header"><span class="pr-2">Why am I seeing this?</span></div>
                    Annotations have specific criteria to be included in a report.<br/><br/>
                    Apply the following rules for each variant:<br/>
                    <v-list>
                        <template v-for="(rule, index) in annotationRules" >
                            <v-list-tile avatar :key="rule.text">
                            <span><v-icon>mdi-book-open-variant</v-icon><span class="pl-2">{{ rule.text }}</span></span>
                            </v-list-tile>
                            <v-divider :key="index" v-if="rule.divider"></v-divider>
                        </template>
                    </v-list>
                </v-expansion-panel-content>
                
            </v-expansion-panel>
        </v-flex>
    
        </v-layout>
    </v-card-text>
    </v-card>
    
    </div>
    `,
    data() {
        return {
            annotationRules: [
                {text: "Make sure annotation cards are selected/toggled (Selecting a variant is not enough)", divider: true},
                {text: "At least one annotation per variant must have a tier", divider: true},
                {text: "A variant needs more than a Therapy card (eg. a Variant Function card) ", divider: false},
            ]
        }
    },
    methods: {
        getReportDetails() {
            this.$emit("get-report-details");
        },
        createVariantEditLink(variant) {
            var link = "../openCase/" + this.$route.params.id + "?showReview=false&variantId="
            + variant.id + "&variantType=" + variant.type.toLowerCase() + "&edit=false";
            return link;
        },
        bypassCNVWarning(variant) {
            this.$emit("bypass-cnv-warning", variant.id);
        },
        bypassAllCNVWarnings() {
            this.$emit("bypass-cnv-all");
        },
        anyCNV() {
            return this.missingCNVCount() > 0;
        },
        missingCNVCount() {
            return this.variantsMissingTier.filter(i => { return i.type === "CNV";}).length;
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

