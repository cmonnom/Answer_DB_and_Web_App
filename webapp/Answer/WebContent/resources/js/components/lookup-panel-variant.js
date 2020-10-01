Vue.component('lookup-panel-variant', {
    props: {
        standalone: {default: true, type:Boolean},
        originalVariant: {default: "", type:String},
        currentOncotreeCode: {default:  () =>{}, type:Object},
        currentGene: {default: "", type:String},
        currentVariant: {default: "", type:String},
        lastOncotreeCode: {default: "", type:String},
        lastOncotreeCodeLabel: {default: "", type:String},
        lastGene: {default: "", type:String},
        lastVariant: {default: "", type:String},
        uniqId: {default: "uniqcna", type:String},
        originalChr: {default: "", type:String},
        originalPos: {default: -1, type:Number},
        originalPosOldBuild: {default: -1, type:Number},
    },
    template: /*html*/`<v-layout row wrap>
    <!-- Variant Results -->

    <!--  Variant & Tumor Type Description -->
    <v-flex :class="getGeneFlexClasses()" >
    <v-card>
        <v-toolbar dense class="elevation-0" dark color="primary">
            <div class="title ml-0">Variant & Tumor Type Description</div>
            <v-spacer></v-spacer>
        </v-toolbar>
        <v-card-text class="pa-3">
            <div v-if="oncoKBVariantError || (oncoKBVariantSummary && !oncoKBVariantSummary.clinicalImplications)">
            {{ lastGene }} {{ lastVariant }} {{ lastOncotreeCode}}: {{lastOncotreeCodeLabel}} has no clinical implication data in OncoKB.
            </div>
            <div v-if="oncoKBVariantSummary && oncoKBVariantSummary.clinicalImplications">
                <div class="font-weight-bold body-1 pb-2">Clinical Implications:</div>
                <div>{{ oncoKBVariantSummary.clinicalImplications }}</div>
            </div>
        </v-card-text>
    </v-card>    
    </v-flex>

<!--  Database Descriptions  -->
    <v-flex :class="getGeneFlexClasses()" >
    <v-card flat>
        <v-toolbar dense class="elevation-0" dark color="primary">
            <div class="title ml-0">Summary</div>
            <v-spacer></v-spacer>

            <v-tooltip bottom>
            <v-btn icon @click="toggleAllVariantPanels(true)" slot="activator">
                <v-icon>mdi-arrow-expand-vertical</v-icon>
            </v-btn>
            <span>Expand All</span>
        </v-tooltip>

            <v-tooltip bottom>
                <v-btn icon @click="toggleAllVariantPanels(false)" slot="activator">
                    <v-icon>mdi-arrow-collapse-vertical</v-icon>
                </v-btn>
                <span>Collapse All</span>
            </v-tooltip>
        </v-toolbar>
        <v-card-text class="pa-0">
            <v-expansion-panel v-model="variantPanel" expand>
            <v-expansion-panel-content v-for="item in variantPanelTitles" :key="item">
            <template v-slot:header>
                <div>{{ item }}
                <v-tooltip bottom v-show="variantSummaries[item].url && !loading">
                <v-btn slot="activator" icon :href="variantSummaries[item].url" target="_blank" rel="noreferrer" @click.stop class="primary--text"><v-icon>mdi-open-in-new</v-icon></v-btn>
                <span>Open {{ lastVariant }} in {{ item }} (new tab).</span>
                </v-tooltip>
                <v-tooltip bottom v-show="variantSummaries[item].url2 && !loading">
                <v-btn slot="activator" icon :href="variantSummaries[item].url2" target="_blank" rel="noreferrer" @click.stop class="primary--text"><v-icon>mdi-lock</v-icon></v-btn>
                <span>Open {{ lastVariant }} in {{ item }} (need paid subscription).</span>
                </v-tooltip>
                </div>

            </template>
            <v-card class="pl-2 pr-2 pb-2 pt-0">
            <v-card-text class="pl-2 pr-2 pt-0">
                <span v-for="(item, index) in variantSummaries[item].summary" :key="index">
                    <span v-if="item.type == 'text'">{{ item.text }}</span>
                    <a v-else :href="'https://www.ncbi.nlm.nih.gov/pubmed/?term=' + item.text" target="_blank" rel="noreferrer">{{ item.text }}</a>
                </span>
            </v-card-text>
            </v-card>
            </v-expansion-panel-content>
            </v-expansion-panel>
        </v-card-text>
    </v-card>    
    </v-flex>

    <v-flex :class="getGeneFlexClasses()" >
        <v-layout row wrap>
          <!--  Standard of Care Indications  -->
    <v-flex class="xs12" >
    <v-card>
        <v-toolbar dense class="elevation-0" dark color="primary">
            <div class="title ml-0">Standard of Care Indications</div>
            <v-spacer></v-spacer>
            <v-tooltip bottom>
            <v-btn icon @click="showStandardOfCare = true" slot="activator">
                <v-icon>mdi-arrow-expand-vertical</v-icon>
            </v-btn>
            <span>Expand All</span>
        </v-tooltip>

        <v-tooltip bottom>
            <v-btn icon @click="showStandardOfCare = false" slot="activator">
                <v-icon>mdi-arrow-collapse-vertical</v-icon>
            </v-btn>
            <span>Collapse All</span>
        </v-tooltip>
        </v-toolbar>
        <v-slide-y-transition>
        <v-card-text class="pl-2 pr-2 pt-1" v-show="showStandardOfCare">
            <div v-if="oncoKBVariantError" class="pl-2 pt-2 mt-1 pb-2 mb-1 pr-2">
                {{ lastGene }} {{ lastVariant }} has no result in OncoKB.
            </div>
            <div v-if="oncoKBVariantSummary">
                <span class="font-weight-bold body-1 pl-2">OncoKB</span>
                <v-tooltip bottom>
                <v-btn slot="activator" icon :href="oncoKBVariantSummary.oncokbVariantUrl" 
                target="_blank" rel="noreferrer" @click.stop class="primary--text ma-0"><v-icon>mdi-open-in-new</v-icon></v-btn>
                <span>Open Variant in OncoKB</span>
                </v-tooltip>
                <div class="pa-2" v-for="indication in oncoKBVariantSummary.indications" :key="indication.drugs">
                <v-divider class="pb-3"></v-divider>
                    <div><span class="font-weight-bold">Indicated Drugs: </span><span>{{ indication.drugs }}</span></div>
                    <div><span class="font-weight-bold">Indication: </span><span v-for="(text, index) in indication.indications" v-text="text"></span></div>
                    <div>
                    <span class="font-weight-bold">Pubmed:</span>
                    <v-tooltip bottom>
                    <v-btn slot="activator" icon :href="indication.pubmedUrl" target="_blank" rel="noreferrer" @click.stop class="primary--text"><v-icon>mdi-open-in-new</v-icon></v-btn>
                    <span>Open Pubmed Article(s)</span>
                    </v-tooltip>
                    </div>
                </div>
                <div class="pa-2" v-show="oncoKBVariantSummary.indications.length == 0">
                    No indications were found but the variant exists in their database.
                </div>    
                    
            </div>
        </v-card-text>
        </v-slide-y-transition>
    </v-card>    
    </v-flex>

         <!--  Investigational of Care Indications  -->
         <v-flex class="xs12" >
         <v-card>
             <v-toolbar dense class="elevation-0" dark color="primary">
                 <div class="title ml-0">Investigational Indications</div>
                 <v-spacer></v-spacer>
             </v-toolbar>
             <v-card-text class="pl-2 pr-2 pt-1">
                 <div v-if="oncoKBVariantError" class="pl-2 pt-2 mt-1 pb-2 mb-1 pr-2">
                     {{ lastGene }} {{ lastVariant }} has no result in OncoKB.
                 </div>
                 <div v-if="oncoKBVariantSummary">
                     <span class="font-weight-bold body-1 pl-2">OncoKB</span>
                     <v-tooltip bottom>
                     <v-btn slot="activator" icon :href="oncoKBVariantSummary.oncokbVariantUrl" 
                     target="_blank" rel="noreferrer" @click.stop class="primary--text ma-0"><v-icon>mdi-open-in-new</v-icon></v-btn>
                     <span>Open Variant in OncoKB</span>
                     </v-tooltip>
                     <div class="pa-2" v-for="indication in oncoKBVariantSummary.investigationalIndications" :key="indication.drugs">
                     <v-divider class="pb-3"></v-divider>
                         <div><span class="font-weight-bold">Investigational Drugs: </span><span>{{ indication.drugs }}</span></div>
                         <div><span class="font-weight-bold">OncoKB Level: </span><span> {{ indication.level }}</span></div>
                         <div>
                         <span class="font-weight-bold">Pubmed:</span>
                         <v-tooltip bottom>
                         <v-btn slot="activator" icon :href="indication.pubmedUrl" target="_blank" rel="noreferrer" @click.stop class="primary--text ma-0"><v-icon>mdi-open-in-new</v-icon></v-btn>
                         <span>Open Pubmed Article(s)</span>
                         </v-tooltip>
                         </div>
                     </div>
                     <div class="pa-2" v-show="oncoKBVariantSummary.indications.length == 0">
                        No indications were found but the variant exists in their database.
                     </div>        
                         
                 </div>
             </v-card-text>
         </v-card>    
         </v-flex>
        </v-layout>
    </v-flex>
      

        <!--  Standard of Care Resistance  -->
        <v-flex :class="getGeneFlexClasses()" >
        <v-card>
            <v-toolbar dense class="elevation-0" dark color="primary">
                <div class="title ml-0">Standard of Care Resistance</div>
                <v-spacer></v-spacer>
            </v-toolbar>
            <v-card-text class="pa-3">
                <div v-if="oncoKBVariantError" class="">
                    {{ lastGene }} {{ lastVariant }} has no result in OncoKB.
                </div>
                <div v-if="oncoKBVariantSummary">
                    <span class="font-weight-bold body-1">OncoKB</span>
                    <v-tooltip bottom>
                <v-btn slot="activator" icon :href="oncoKBVariantSummary.oncokbVariantUrl" 
                target="_blank" rel="noreferrer" @click.stop class="primary--text ma-0"><v-icon>mdi-open-in-new</v-icon></v-btn>
                <span>Open Variant in OncoKB</span>
                </v-tooltip>
                        <div><span class="font-weight-bold">Drug resistance: </span><span v-if="oncoKBVariantSummary.drugResistance">{{ oncoKBVariantSummary.drugResistance }}</span>
                        <span v-else>None found.</span></div>
                </div>
            </v-card-text>
        </v-card>    
        </v-flex>

              <!--  Clinical Trials  -->
    <v-flex :class="getGeneFlexClasses()" >
    <v-card>
        <v-toolbar dense class="elevation-0" dark color="primary">
            <div class="title ml-0">Clinical Trials</div>
            <v-spacer></v-spacer>
            <v-tooltip bottom>
            <v-btn icon @click="showClinicalTrials = true" slot="activator">
                <v-icon>mdi-arrow-expand-vertical</v-icon>
            </v-btn>
            <span>Expand All</span>
        </v-tooltip>

        <v-tooltip bottom>
            <v-btn icon @click="showClinicalTrials = false" slot="activator">
                <v-icon>mdi-arrow-collapse-vertical</v-icon>
            </v-btn>
            <span>Collapse All</span>
        </v-tooltip>
        </v-toolbar>
        <v-slide-y-transition>
        <v-card-text class="pl-2 pr-2 pt-1" v-show="showClinicalTrials">
            <div v-if="clinicalTrialsError" class="pl-2 pt-2 mt-1 pb-2 mb-1 pr-2">
                {{ lastGene }} {{ lastVariant }} {{ lastOncotreeCode }}: {{ lastOncotreeCodeLabel}} has no result in clinicaltrials.gov.
            </div>
            <div v-if="clinicalTrials">
                <div class="pt-2 pl-2 pr-2 pb-0" v-for="(trial, index) in clinicalTrials" :key="index">
                <v-divider class="pb-3"></v-divider>
                    <div><span class="font-weight-bold">Investigational Drugs: </span><span  v-text="formatTrialArray(trial.InterventionName)"></span></div>
                    <div><span class="font-weight-bold">Description: </span><span v-text="formatTrialArray(trial.BriefTitle)"></span></div>
                    <div><span class="font-weight-bold">Conditions: </span><span v-text="formatTrialArray(trial.Condition)"></span></div>
                    <div>
                    <span class="font-weight-bold">{{ trial.NCTId[0] }}:</span>
                    <v-tooltip bottom>
                    <v-btn slot="activator" icon :href="clinicalTrialStudyUrl + trial.NCTId[0]" target="_blank" rel="noreferrer" @click.stop class="primary--text ma-0"><v-icon>mdi-open-in-new</v-icon></v-btn>
                    <span>Open Study page on clinicaltrials.gov</span>
                    </v-tooltip>
                    </div>
                </div>    
                    
            </div>
        </v-card-text>
        </v-slide-y-transition>
    </v-card>    
    </v-flex>

    <v-flex :class="getGeneFlexClasses()">
    <v-layout row wrap>
<!--   Functional Annotations  -->
<v-flex class="xs6" >
<v-card>
    <v-toolbar dense class="elevation-0" dark color="primary">
        <div class="title ml-0">Functional Annotations</div>
        <v-spacer></v-spacer>
    </v-toolbar>
    <v-card-text class="pl-3 pr-3 pt-1">
        <div v-if="oncoKBVariantError" class="pt-2 mt-1 pb-2 mb-1">
            {{ lastGene }} {{ lastVariant }} has no result in OncoKB.
        </div>
        <div v-if="oncoKBVariantSummary && !fasmicLoading && !uniProtVariantLoading">
            <table class="no-border-table">
            <tbody>
                <tr>
                    <td class="font-weight-bold pa-2">OncoKB Oncogenic</td><td class="pa-2">{{ oncoKBVariantSummary.oncogenic }} </td>
                </tr>
                <tr >
                    <td class="font-weight-bold pa-2">OncoKB Function</td><td class="pa-2">{{ oncoKBVariantSummary.mutationEffect }} </td>
                </tr>
                <tr >
                    <td class="font-weight-bold pl-2 pr-2">OncoKB Mutation Effect</td><td class="pl-2 pr-2">Pubmed: 
                    <v-tooltip bottom>
                    <v-btn slot="activator" icon :href="oncoKBVariantSummary.mutationEffectPubMedUrl" target="_blank" rel="noreferrer" @click.stop class="primary--text mt-0"><v-icon>mdi-open-in-new</v-icon></v-btn>
                    <span>Open Mutation Effect Related PubMed Article(s)</span>
                    </v-tooltip>
                    </td>
                </tr>
                <tr>
                    <td :class="[fasmicUrl ? 'pl-2 pr-2' : 'pa-2', 'font-weight-bold', 'pt-0' ]">Fasmic</td><td :class="[fasmicUrl ? 'pl-2 pr-2' : 'pa-2', 'pt-0']">
                    <span v-if="fasmicSummary">{{ fasmicSummary }}</span>
                    <v-tooltip bottom v-if="fasmicUrl">
                    <v-btn slot="activator" icon :href="fasmicUrl" target="_blank" rel="noreferrer" @click.stop class="primary--text mt-0"><v-icon>mdi-open-in-new</v-icon></v-btn>
                    <span>Open MD Anderson Fasmic Website</span>
                    </v-tooltip>
                    <span v-else>No entry in Fasmic</span>
                    </td>
                </tr>
                <tr>
                    <td :class="[uniProtVariantUrl ? 'pl-2 pr-2' : 'pa-2', 'font-weight-bold', 'pt-0' ]">UniprotKB</td><td :class="[uniProtVariantUrl ? 'pl-2 pr-2' : 'pa-2', 'pt-0']">
                    <span v-if="uniProtVariantUrl">Present</span>
                    <v-tooltip bottom v-if="uniProtVariantUrl">
                    <v-btn slot="activator" icon :href="uniProtVariantUrl" target="_blank" rel="noreferrer" @click.stop class="primary--text mt-0"><v-icon>mdi-open-in-new</v-icon></v-btn>
                    <span>Open MD Anderson Fasmic Website</span>
                    </v-tooltip>
                    <span v-else>No entry in UniprotKB</span>
                    </td>
                </tr>
                </tbody>
            </table>
    </div>
    </v-card-text>
</v-card>    
</v-flex>

<!--   Hot Spots  -->
<v-flex class="xs6">
<v-card>
    <v-toolbar dense class="elevation-0" dark color="primary">
        <div class="title ml-0">Hotspot Annotations</div>
        <v-spacer></v-spacer>
    </v-toolbar>
    <v-card-text class="pl-3 pr-3 pt-1">
        <div v-if="hotspotVariantError"  class="pt-2 mt-1 pb-2 mb-1">
            {{ lastGene }} {{ lastVariant }} has no result in MSK Hotspot.
        </div>
        <div v-if="hotspotSummary && !hotspotLoading">
                <table class="no-border-table">
                <tbody>
                <tr>
                    <td class="font-weight-bold pa-2">Hotspot</td><td class="pa-2">
                    <div>
                    <span v-if="hotspot.isHotspot">Yes</span>
                    <span v-else>No</span>
                    <v-tooltip bottom>
                    <v-btn slot="activator" icon :href="hotspotSummary.moreInfoUrl" target="_blank" rel="noreferrer" @click.stop class="primary--text mt-0"><v-icon>mdi-open-in-new</v-icon></v-btn>
                    <span>Open MSK Cancer Hotspot Table</span>
                    </v-tooltip>
                    </div> 
                    </td>
                </tr>
                <tr>
                <td class="font-weight-bold pa-2">3D Hotspot</td><td class="pa-2">
                <div>
                <span v-if="hotspot.isThreeD">Yes</span>
                <span v-else>No</span>
                <v-tooltip bottom>
                <v-btn slot="activator" icon :href="hotspotSummary.moreInfoUrl2" target="_blank" rel="noreferrer" @click.stop class="primary--text mt-0"><v-icon>mdi-open-in-new</v-icon></v-btn>
                <span>Open MSK Cancer 3D Hotspot Table</span>
                </v-tooltip>
                </div> 
                </td>
            </tr>
                </tbody>
            </table>
        </div>
    </v-card-text>
</v-card>    
</v-flex>
<!-- Codon Disease Distribution -->
<v-flex class="xs12">
<lookup-panel-plot-utils ref="plotUtils"
:standalone="standalone" @handle-dialogs="handleDialogs"></lookup-panel-plot-utils>
        <v-card>
            <v-toolbar dense class="elevation-0" dark color="primary">
                <div class="title ml-0">Codon Disease Distribution</div>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon @click="showCodonPanel = true" slot="activator">
                        <v-icon>mdi-arrow-expand-vertical</v-icon>
                    </v-btn>
                    <span>Expand All</span>
                </v-tooltip>

                <v-tooltip bottom>
                    <v-btn icon @click="showCodonPanel = false" slot="activator">
                        <v-icon>mdi-arrow-collapse-vertical</v-icon>
                    </v-btn>
                    <span>Collapse All</span>
                </v-tooltip>
            </v-toolbar>
            <v-card-text class="pa-3">
                <div :class="[codonPlotLoading ? 'alpha-54' : '']">
                    <div v-if="codonPlotError && !codonPlotLoading">No Genie Data could be found for variant
                        {{ lastGene }} {{ lastVariant }}</div>
                    <div v-if="!codonPlotError" v-show="showCodonPanel">
                        <div class="pb-2">This graph shows the disease distribution of this codon. Sometimes diseases will be enriched for a given mutant amino acid, and this can be useful for cancers that lack a definitive diagnosis.</div>
                        <div :id="'codonPlot' + uniqId"></div>
                    </div>
                </div>

            </v-card-text>
        </v-card>
    </v-flex>
    </v-layout>
    </v-flex>
        



</v-layout>`, data() {
        return {
            variantPanelTitles: ["Jackson Labs", "Civic DB"],
            variantSummaries: {
                "Jackson Labs" : {summary: null, url: null, url2: null, loading: false},
                "Civic DB" : {summary: null, url: null, url2: null, loading: false},
            },
            loading: false,
            variantPanel: [],
            ensemblId: null,
            oncoKBVariantSummary: null,
            oncoKBVariantError: false,
            fasmicSummary: null,
            fasmicUrl: null,
            fasmicLoading: false,
            uniProtVariantUrl: null,
            uniProtVariantLoading: false,
            hotspotLoading: false,
            hotspotSummary: null,
            hotspotVariantError: false,
            hotspot: null,
            showCodonPanel: true,
            codonPlotLoading: false,
            codonPlotError: false,
            clinicalTrials: [],
            clinicalTrialsError: false,
            clinicalTrialsLoading: false,
            clinicalTrialStudyUrl: "",
            showClinicalTrials: true,
            showStandardOfCare: true
        }

    },
    methods: {
        handleDialogs(response, callback) {
            this.$emit("handle-dialogs", [this, response, callback]);
        },
        toggleAllVariantPanels(doOpen) {
            this.variantPanel = this.variantPanelTitles.map(i => doOpen);
        },
        getGeneFlexClasses() {
            if (this.standalone) {
                return "md12 lg6";
            }
            else {
                return "xs12";
            }
        },
        getGeneFlexClassesHalf() {
            if (this.standalone) {
                return "md6 lg3";
            }
            else {
                return "xs12";
            }
        },
        getVariantSummary() {
            this.oncoKBVariantSummary = null;
            this.oncoKBVariantError = false;
            this.fasmicUrl = null;
            this.fasmicSummary = null;
            this.fasmicLoading = true;
            this.uniProtVariantUrl = null;
            this.uniProtVariantLoading = true;
            this.hotspotLoading = true;
            this.loading = true;
            this.clinicalTrialsLoading = true;
            this.clinicalTrials = [];
            this.hotspotVariantError = false;
            this.hotspot = null;
            this.hotspotSummary = null;
            for (var i = 0; i < this.variantPanelTitles.length; i++) {
                this.variantSummaries[this.variantPanelTitles[i]].summary = "";
                this.variantSummaries[this.variantPanelTitles[i]].url = null;
                this.variantSummaries[this.variantPanelTitles[i]].url2 = null;
            }
            axios.get(webAppRoot + "/getVariantSummary", {
                params: {
                    geneTerm: this.currentGene,
                    oncotreeCode: this.currentOncotreeCode.text,
                    hgvs: this.currentVariant,
                    originalVariant: this.originalVariant,
                    databases: this.variantPanelTitles.join(","),
                    chr: this.originalChr,
                    posGRC38: this.originalPos,
                    posHG19: this.originalPosOldBuild
                }
            })
            .then(response => {
                //summary
                var responseDatabases = response.data.payload.variantSummaries ? response.data.payload.variantSummaries.databases : "";
                if (response.data.isAllowed && response.data.success) {
                    processDatabaseSummaries(responseDatabases, response, this.variantSummaries);
                    this.toggleAllVariantPanels(true);
                    
                    //OncoKB
                    this.oncoKBVariantSummary = response.data.payload.oncoKBSummary;
                    if (!this.oncoKBVariantSummary) {
                        this.oncoKBVariantError = true;
                    }
                    else {
                        this.oncoKBVariantSummary.clinicalImplications = this.formatClinicalImplications();
                    }

                    //Fasmic
                    var fasmicSummary = response.data.payload.fasmicSummary;
                    if (fasmicSummary) {
                        this.fasmicUrl = fasmicSummary.moreInfoUrl;
                        this.fasmicSummary = fasmicSummary.summary;
                    }

                    //Uniprot
                    var uniprotSummary = response.data.payload.uniprotSummary;
                    if (uniprotSummary) {
                        this.uniProtVariantUrl = uniprotSummary.moreInfoUrl;
                    }

                    //Hotspot
                    var hotspotSummary = response.data.payload.hotspotSummary;
                    if (hotspotSummary) {
                        this.hotspotSummary = hotspotSummary;
                        this.hotspot = response.data.payload.hotspot;
                    }
                    else {
                        this.hotspotVariantError = true;
                    }

                    //Clinical Trials
                    var clinicalTrials = response.data.payload.clinicalTrials;
                    if (clinicalTrials) {
                        this.clinicalTrials = clinicalTrials;
                        this.clinicalTrialStudyUrl = response.data.payload.clinicalTrialStudyUrl;
                    }
                    else {
                        this.clinicalTrialsError = true;
                    }
                    this.stopLoading();
                }
                else if (response.data.isAllowed && !response.data.success) {
                    //summary
                    for (var i = 0; i < responseDatabases.length; i++) {
                        var summary = formatPubMedLinks("Nothing found in " + database);
                        this.variantSummaries[database].summary = summary;
                        this.variantSummaries[database].url = null;
                        this.variantSummaries[database].url2 = null;
                    }
                    this.setAllInErrorState();
                    this.toggleAllVariantPanels(true);
                    this.stopLoading();
                }
                else {
                    this.stopLoading();
                    this.handleDialogs(response.data, this.getVariantSummary);
                }
            })
            .catch(error => {
                this.stopLoading();
                alert(error);
            });
        },
        stopLoading() {
            this.loading = false;
            //Fasmic
            this.fasmicLoading = false;
            //Uniprot
            this.uniProtVariantLoading = false;
             //Hotspot
            this.hotspotLoading = false;
            //Clinical Trials
            this.clinicalTrialsLoading = false;
        }, 
        setAllInErrorState() {
            this.hotspotVariantError = true;
            this.clinicalTrialsError = true;
            this.oncoKBVariantError = true;
        },
        reload() {
            this.getVariantSummary();
            this.fetchCodonPlot();
        },
        isLoading() {
            return this.uniProtVariantLoading || this.fasmicLoading 
            || this.hotspotLoading || this.loading
            || this.codonPlotLoading;
        },
        fetchCodonPlot() {
            this.codonPlotLoading = true;
            this.codonPlotError = false;
            var promise1 = this.$refs.plotUtils.updateBarPlot("/getCodonDiseaseDistributionData", {
                geneTerm: this.currentGene,
                oncotreeCode: this.currentOncotreeCode.text,
                hgvs: this.currentVariant,
                originalVariant: this.originalVariant,
                plotId: "codonPlot" + this.uniqId,
                posGRC38: this.originalPos,
                posHG19: this.originalPosOldBuild,
                chr: this.originalChr

            }, true);
            Promise.all([promise1]).then(values => {
                this.codonPlotLoading = false;
                if (values.filter(v => v.success).length != values.length) {
                    console.log("Some plots did not finish properly");
                    this.codonPlotError = true;
                }
                else {
                    this.codonPlotError = false;
                }
            })
        },
        formatTrialArray(items) {
            if (items) {
                return items.join(" | ");
            }
            return "None";
        },
        formatClinicalImplications() {
            if (this.oncoKBVariantSummary && this.oncoKBVariantSummary.clinicalImplications) {
                var description = this.oncoKBVariantSummary.clinicalImplications;
                description = description.replace(/\[\[gene\]\]/g, this.lastGene);
                description = description.replace(/\[\[mutation\]\]/g, this.lastVariant);
                description = description.replace(/\[\[variant\]\]/g, this.lastVariant);
                description = description.replace(/\[\[\[mutant\]\]\]/g, this.lastOncotreeCode);
                description = description.replace(/\[\[mutant\]\]/g, this.lastOncotreeCode);
                description = description.replace(/\[\[tumor type\]\]/g, this.lastOncotreeCode);
                return description;
            }
            return "";
        }  
    },
    mounted() {
    },
    created: function () {
    },
    destroyed: function () {
    },
    watch: {
    }


});