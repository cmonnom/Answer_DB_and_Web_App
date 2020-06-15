const LookupTool = {
    template:
        /*html*/`<div>
  
  <v-toolbar dense dark color="primary" fixed app>
  <v-menu offset-y offset-x class="ml-0">
  <v-btn slot="activator" flat icon dark>
  <v-icon color="amber accent-2">mdi-dna</v-icon>
          </v-btn>
          <v-list>
          <v-list-tile avatar v-for="button in buttons" :key="button.label" 
          @click="changeTab(button.label)">
                <v-list-tile-avatar>
                    <v-icon>{{ button.icon }}</v-icon>
                </v-list-tile-avatar>
                <v-list-tile-content>
                    <v-list-tile-title>{{ button.label }} </v-list-tile-title>
                </v-list-tile-content>
            </v-list-tile>
          </v-list>
</v-menu>
    <v-toolbar-title class="white--text ml-0">
    Lookup Tool <span v-text="getCurrentTabLabel()"></span>
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <v-tooltip bottom v-for="button in buttons" :key="button.label">
    <v-btn icon flat dar slot="activator" @click="changeTab(button.label)"
     >
        <v-icon :color="button.label == getCurrentTab() ? 'amber accent-2': ''">{{ button.icon }}</v-icon>
    </v-btn>
    <span>{{ button.label }} </span>
</v-tooltip>
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
            oncotree: [],
            buttons: []
        }
    },
    methods: {
        loadFromParams() {
            if (this.$refs.lookup) {
            this.$refs.lookup.currentGene = this.$route.query.gene;
            this.$refs.lookup.currentVariant = this.$route.query.variant;
            this.$refs.lookup.currentlyActive = this.$route.query.button;
            this.$refs.lookup.currentAmpDel = this.$route.query.ampDel;
            this.$refs.lookup.currentFive = this.$route.query.five;
            this.$refs.lookup.currentThree = this.$route.query.three;
            
            var oncotreeItems = this.oncotree.filter(o => o.text == this.$route.query.oncotree);
            var oncotree = null;
            if (oncotreeItems && oncotreeItems[0]) {
                oncotree = oncotreeItems[0];
            }
            this.$refs.lookup.currentOncotreeCode = oncotree;
            if (this.$refs.lookup.isFormValid()) {
                    this.$refs.lookup.submitForm();
                }
            }
        },
        changeTab(button) {
            if (this.$refs.lookup) {
                this.$refs.lookup.currentlyActive = button;
            }
        },
        collectOncoTreeDiagnosis() {
            this.oncotree = oncotree;
        },
        getCurrentTabLabel() {
            var label =  this.getCurrentTab();
            if (label) {
                return "(" + label + ")";
            }
            return "";
        },
        getCurrentTab() {
            if (this.$refs.lookup) {
                return this.$refs.lookup.currentlyActive;
            }
            return "";
        },
        updateButtons() {
            if (this.$refs.lookup) {
                this.buttons = this.$refs.lookup.buttons;
            }
            return [];
        }
    },
    mounted: function () {
        this.loadFromParams();
        this.updateButtons();

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

