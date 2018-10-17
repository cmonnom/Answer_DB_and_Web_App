

Vue.component('report-tier-warning', {
    props: {
        variantsMissingTier: {default: () => [], type: Array}
    },
    template: `<div v-show="variantsMissingTier.length > 0">
    <v-toolbar dense dark color="error">
    <v-toolbar-title>
        Some Annotations are Incomplete
    </v-toolbar-title>
</v-toolbar>
    <v-card  class="pl-2 pt-2 pr-2 pb-2 mb-3" >
    <v-card-text class="subheading">
       The following variants have been selected for this report but none of its annotation cards have tiers.<br/>
       <v-layout row wrap>
       <v-flex xs12 v-for="variant in variantsMissingTier" :key="variant.id">
       <v-icon color="error" class="pb-2">warning</v-icon>
       <span  v-if="variant.type == 'SNP'">{{variant.type}} {{ variant.chrom }}:{{ variant.name}} {{variant.notation }}</span>
       <span  v-if="variant.type == 'CNV'">{{variant.type}} {{ variant.chrom }}</span>
       <v-tooltip bottom>
       <v-btn icon flat slot="activator" :href="createVariantEditLink(variant)" target="_blank" rel="noreferrer">
       <v-icon>zoom_in</v-icon>
       </v-btn>
       <span>Open variant in a new tab</span>
       </v-tooltip>
       </v-flex>
       </v-layout>
       Each variant selected needs to have at least one annotation with a tier.<br/>
       Click on each variant to edit its annotations then click <v-btn @click="getReportDetails()">NEW REPORT</v-btn> to refresh the changes when done.
    </v-card-text>
    </v-card>
    </div>
    `,
    data() {
        return {
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

