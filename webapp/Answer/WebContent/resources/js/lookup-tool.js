const LookupTool = {
    template:
        `<div>
  
  <v-toolbar dense dark color="primary" fixed app>
  <v-menu offset-y offset-x class="ml-0">
  <v-btn slot="activator" flat icon dark>
  <v-icon color="amber accent-2">mdi-dna</v-icon>
          </v-btn>
          <v-list>
          </v-list>
</v-menu>
    <v-toolbar-title class="white--text ml-0">
    Lookup Tool
    </v-toolbar-title>
    <v-spacer></v-spacer>
</v-tooltip>
   

  </v-toolbar>
  <v-container grid-list-md fluid class="pl-0 pr-0 pt-0">
    <v-layout row wrap>
        <v-flex xs12>
        <lookup-panel ref="lookup" :standalone="true"
        :oncotree-items="oncotree"></lookup-panel>

        </v-flex>
    </v-layout>
    </v-container>
</div>`,
    data() {
        return {
            oncotree: []
        }
    },
    methods: {
        loadFromParams() {
           this.$refs.lookup.currentGene = this.$route.query.gene;
           this.$refs.lookup.currentVariant = this.$route.query.variant;
           this.$refs.lookup.currentlyActive = this.$route.query.button;
           
           var oncotreeItems = this.oncotree.filter(o => o.text == this.$route.query.oncotree);
           var oncotree = null;
           if (oncotreeItems && oncotreeItems[0]) {
               oncotree = oncotreeItems[0];
           }
           this.$refs.lookup.currentOncotreeCode = oncotree;
           if (this.$refs.lookup.isFormValid()) {
                this.$refs.lookup.submitForm();
            }
        },
        collectOncoTreeDiagnosis() {
            this.oncotree = oncotree;
        },
    },
    mounted: function () {
        this.loadFromParams();

    },
    destroyed: function () {
       
    },
    created: function () {
        this.collectOncoTreeDiagnosis();
    },
    computed: {
    },
    watch: {
    }
};

