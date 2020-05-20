Vue.component('lookup-panel', {
    props: {
        title: {default: "Lookup Tool (Beta)", type: String},
        standalone: {default: true, type:Boolean},
        oncotreeItems: {default: () => [], type:Array},
        originalVariant: {default: "", type:String}
    },
    template: /*html*/`<div>

    <v-card v-if="panelVisible || standalone" :class="standalone ? [] : ['lookup-panel', 'mr-2','ml-2']" :height="calcMaxHeight()" :flat="standalone"
    :color="standalone ? 'rgba(0,0,0,0)' : ''">
            <v-toolbar dense class="elevation-0" dark color="primary" v-show="!standalone">
                <v-tooltip class="ml-0" bottom>
                    <v-menu offset-y offset-x slot="activator" class="ml-0">
                        <v-btn slot="activator" flat icon dark>
                            <v-icon color="amber accent-2">mdi-dna</v-icon>
                        </v-btn>
                        <v-list>

                        <v-list-tile avatar @click="openInNewWindow()">
                        <v-list-tile-avatar>
                            <v-icon>mdi-open-in-new</v-icon>
                        </v-list-tile-avatar>
                        <v-list-tile-content>
                            <v-list-tile-title>Open in a separate window</v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>

                            <v-list-tile avatar @click="reloadValues()">
                                <v-list-tile-avatar>
                                    <v-icon>refresh</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title>Reload Current Variant Values</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>

                            <v-list-tile avatar @click="togglePanel()">
                            <v-list-tile-avatar>
                                <v-icon>close</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>Close Lookup Tool</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>


                   


                        </v-list>
                    </v-menu>
                    <span>Lookup Menu</span>
                </v-tooltip>
                <div class="title ml-0">
                    {{ title }}
                </div>
                <v-spacer></v-spacer>

                <v-tooltip bottom>
                <v-btn icon @click="openInNewWindow()" slot="activator">
                    <v-icon>mdi-open-in-new</v-icon>
                </v-btn>
                <span>Open in a separate window</span>
            </v-tooltip>

                <v-tooltip bottom>
                    <v-btn icon @click="reloadValues()" slot="activator">
                        <v-icon>refresh</v-icon>
                    </v-btn>
                    <span>Reload Current Variant Values</span>
                </v-tooltip>

                <v-tooltip bottom>
                <v-btn icon @click="togglePanel()" slot="activator">
                    <v-icon>close</v-icon>
                </v-btn>
                <span>Close Lookup Tool</span>
            </v-tooltip>
    
            </v-toolbar>
            <v-card flat class="html-scroll-auto lookup-card-height" :flat="standalone" :color="standalone ? 'rgba(0,0,0,0)' : ''">
                <v-container grid-list-md fluid pa-1>
                    <v-layout row wrap justify-center>
                    <v-flex xs12 class="centered error--text">NOT PRODUCTION READY</v-flex>
                        <v-tooltip bottom v-for="button in buttons" :key="button.label">
                        <v-btn slot="activator" @click="handleButton(button)" :class="button.label == currentlyActive ? 'amber accent-2': ''">
                        <v-icon left dark>{{ button.icon }}</v-icon>
                        {{ button.label}}
                        </v-btn>
                        <span>{{ button.tooltip }} </span>
                        </v-tooltip>
                        <v-flex xs12  class="centered">
                        <v-icon class="error--text">mdi-alert-circle</v-icon>
                        The data provided here is purely for research.<br/>You are responsible for the content of annotations you create.
                        </v-flex>

                        </v-layout>
                        <v-layout row wrap>
                        <v-flex xs12>
                            <v-layout row wrap>
                            
                                <!-- Gene/Variant Input -->
                                <v-flex v-if="currentlyActive == 'Gene' || currentlyActive == 'Variant'" :class="['pl-2','pr-2', ...getFlexClasses()]">
                                    <v-text-field hide-details
                                    v-model="currentGene"
                                    :value="currentGene"
                                    label="Gene Symbol"
                                    placeholder="Eg. BRCA1"
                                    :error-messages="geneSymbolErrorMessage"
                                    @input="geneSymbolErrorMessage = null"
                                    @keyup.enter="submitForm">
                                    </v-text-field>
                                </v-flex>

                                <!-- Variant Input -->
                                <v-flex v-if="currentlyActive == 'Variant'" :class="['pl-2','pr-2', ...getFlexClasses()]">
                                    <v-text-field hide-details 
                                    v-model="currentVariant"
                                    :value="currentVariant"
                                    label="HGVS Variant:"
                                    placeholder="Eg. p.His1791Gln"
                                    @keyup.enter="submitForm">
                                    </v-text-field>
                                </v-flex>

                                <!-- Cancer Input -->
                                <v-flex v-if="currentlyActive == 'Cancer' || currentlyActive == 'Variant'" :class="['pl-2','pr-2', ...getCancerFlexClasses()]">
                                    <v-autocomplete hide-details
                                    value="currentOncotreeCode"  v-model="currentOncotreeCode" :items="oncotreeItems" return-object
                                    label="Oncotree Code"
                                    placeholder="Eg. AML"
                                    clearable
                                    :filter="oncotreeFilter"
                                    open-on-clear
                                      @change="submitForm">
                                    <template v-slot:selection="data">
                                    <span v-if="data && data.item">{{ data.item.text}}: {{ data.item.label}}</span>
                                    </template>
                                    <template v-slot:item="data">
                                    <span v-if="data && data.item">{{ data.item.text}}: {{ data.item.label}}</span>
                                    </template>
                                    </v-autocomplete>
                                </v-flex>

                            </v-layout>

                        <v-btn v-show="currentlyActive" @click="submitForm" class="primary"
                        :disabled="!isFormValid()"
                        :loading="isLoadingOverall()">Submit</v-btn>
                        </v-flex>

                       
                        <!-- Gene Results -->
                        <v-flex :class="getGeneFlexClasses()" v-show="currentlyActive == 'Gene'">
                        <v-card flat>
                            <v-toolbar dense class="elevation-0" dark color="primary">
                                <div class="title ml-0">Summary</div>
                                <v-spacer></v-spacer>
                
                                <v-tooltip bottom>
                                <v-btn icon @click="toggleAllGenePanels(true)" slot="activator">
                                    <v-icon>mdi-arrow-expand-vertical</v-icon>
                                </v-btn>
                                <span>Expand All</span>
                            </v-tooltip>
                
                                <v-tooltip bottom>
                                    <v-btn icon @click="toggleAllGenePanels(false)" slot="activator">
                                        <v-icon>mdi-arrow-collapse-vertical</v-icon>
                                    </v-btn>
                                    <span>Collapse All</span>
                                </v-tooltip>
                            </v-toolbar>
                            <v-card-text class="pa-0">
                                <v-expansion-panel v-model="genePanel" expand>
                                <v-expansion-panel-content v-for="item in genePanelTitles" :key="item">
                                <template v-slot:header>
                                    <div>{{ item }} <span v-show="!geneSummaries[item].summary && !geneSummaryLoading">(no summary found)</span>
                                    <v-tooltip bottom v-show="geneSummaries[item].url && !geneSummaryLoading">
                                    <v-btn slot="activator" icon :href="geneSummaries[item].url" target="_blank" @click.stop class="primary--text"><v-icon>mdi-open-in-new</v-icon></v-btn>
                                    <span>Open {{ currentGene }} in {{ item }} (new tab).</span>
                                    </v-tooltip>
                                    <v-tooltip bottom v-show="geneSummaries[item].url2 && !geneSummaryLoading">
                                    <v-btn slot="activator" icon :href="geneSummaries[item].url2" target="_blank" @click.stop class="primary--text"><v-icon>mdi-lock</v-icon></v-btn>
                                    <span>Open {{ currentGene }} in {{ item }} (need paid subscription).</span>
                                    </v-tooltip>
                                    </div>

                                </template>
                                <v-card class="pl-2 pr-2 pb-2 pt-0">
                                <v-card-text class="pl-2 pr-2 pt-0">
                                    <span v-for="(item, index) in geneSummaries[item].summary" :key="index">
                                        <span v-if="item.type == 'text'">{{ item.text }}</span>
                                        <a v-else :href="'https://www.ncbi.nlm.nih.gov/pubmed/?term=' + item.text" target="_blank">{{ item.text }}</a>
                                    </span>
                                </v-card-text>
                                </v-card>
                                </v-expansion-panel-content>
                                </v-expansion-panel>
                            </v-card-text>
                        </v-card>    
                        </v-flex>
                        <v-flex :class="getGeneFlexClasses()" v-show="currentlyActive == 'Gene'">
                            <v-layout row wrap>

                             <!-- GDC -->
                             <v-flex xs12 v-show="currentlyActive == 'Gene'">
                             <v-card>
                                 <v-toolbar dense class="elevation-0" dark color="primary">
                                     <div class="title ml-0">Genie</div>
                                     <v-spacer></v-spacer>
                                     <v-tooltip bottom>
                                     <v-btn icon @click="showGeneGeniePanel = true" slot="activator">
                                         <v-icon>mdi-arrow-expand-vertical</v-icon>
                                     </v-btn>
                                     <span>Expand All</span>
                                 </v-tooltip>
                     
                                     <v-tooltip bottom>
                                         <v-btn icon @click="showGeneGeniePanel = false" slot="activator">
                                             <v-icon>mdi-arrow-collapse-vertical</v-icon>
                                         </v-btn>
                                         <span>Collapse All</span>
                                     </v-tooltip>
                                 </v-toolbar>
                                 </v-toolbar>
                                 <v-card-text class="pa-3">
                                 <span class="body-2"><b>GDC Data Portal</b></span>
                                 <v-tooltip bottom v-if="ensemblId">
                                 <v-btn icon slot="activator" :href="'https://portal.gdc.cancer.gov/genes/' + ensemblId" target="_blank">
                                 <v-icon class="primary--text">mdi-open-in-new</v-icon></v-btn>
                                 <span>Open {{ currentGene }} on the NIH GDC Data Portal</span>
                                 </v-tooltip>

                                 <div :class="[genePlotLoading ? 'alpha-54' : '']">
                                 <div v-if="genePlotError && !genePlotLoading">No Genie Data could be found for gene {{ currentGene }}</div>
                                 <div v-if="!genePlotError" v-show="showGeneGeniePanel">
                                    <div id="genieLollipopPlot"></div>
                                    <div id="alterationByCancerPlot"></div>
                                    <div id="cancerByPercentPlot"></div>
                                </div>
                                </div>

                                 </v-card-text>
                             </v-card>    
                             </v-flex>

                            <!-- Reactome Pathways -->
                            <v-flex xs12>
                            <v-card>
                                <v-toolbar dense class="elevation-0" dark color="primary">
                                    <div class="title ml-0">Reactome Locations</div>
                                    <v-spacer></v-spacer>
                                    <v-tooltip bottom>
                                    <v-btn icon @click="openCloseAllReactome(true)" slot="activator">
                                        <v-icon>mdi-arrow-expand-vertical</v-icon>
                                    </v-btn>
                                    <span>Expand All</span>
                                </v-tooltip>
                    
                                    <v-tooltip bottom>
                                        <v-btn icon @click="openCloseAllReactome(false)" slot="activator">
                                            <v-icon>mdi-arrow-collapse-vertical</v-icon>
                                        </v-btn>
                                        <span>Collapse All</span>
                                    </v-tooltip>
                                </v-toolbar>
                                <v-card>
                                    <v-card-text class="pa-2" v-show="reactomeContentDetailUrl">
                                    <div class="pa-1 body-2"><b>{{ currentGene }} locations</b>
                                    <v-tooltip bottom>
                                    <v-btn slot="activator" icon :href="reactomeContentDetailUrl" target="_blank" @click.stop class="primary--text"><v-icon>mdi-open-in-new</v-icon></v-btn>
                                    <span>Open Reactome PathwayBrowser Locations (new tab).</span>
                                    </v-tooltip>
                                    </div>
                                        <v-treeview open-on-click :items="reactomeItems" item-key="stId" :open-all="false" ref="reactome" :open.sync="reactomeItemsOpen"
                                        hoverable style="max-width:100%">
                                        <!---
                                        <template v-slot:prepend="{ item }">
                                        <v-icon>mdi-alert-circle-outline</v-icon>
                                      </template>
                                      -->
                                      <template v-slot:label="{ item }" >
                                             <v-tooltip bottom>
                                             <a @click.stop class="body-1" slot="activator" v-if="item.url" :href="item.url" target="_blank">{{ item.name }}</a>
                                             <span>Open Reactome's PathwayBrowser</span>
                                             </v-tooltip>
                                             <span v-if="!item.rootLevel && !item.url" class="body-1" slot="activator">{{ item.name }}</span>
                                             <span v-if="item.rootLevel" class="font-weight-bold body-1" slot="activator">{{ item.name }}</span>
                                             <v-tooltip bottom  v-if="item.rootLevel">
                                             <v-btn icon small @click.stop="openReactomeRoot(item)" slot="activator" class="primary--text">
                                                 <v-icon class="pr-0">mdi-arrow-expand-vertical</v-icon>
                                             </v-btn>
                                             <span>Expand Branch</span>
                                            </v-tooltip>
                                      </template>
                                        </v-treeview>
                                    </v-card-text>
                                </v-card>
                            </v-card>    
                            </v-flex>
    
                           
                            </v-layout>
                        </v-flex>
                        
                        <!-- Cancer Results -->
                        <v-flex :class="getGeneFlexClasses()" v-show="currentlyActive == 'Cancer'">
                            <v-tooltip bottom v-if="oncotree.externalReferences && oncotree.externalReferences.NCI && currentOncotreeCode">
                            <v-btn slot="activator" 
                            :href="'https://ncit.nci.nih.gov/ncitbrowser/ConceptReport.jsp?dictionary=NCI_Thesaurus&code=' + oncotree.externalReferences.NCI[0]"
                            target="_blank">
                                NCI Thesaurus
                                <v-icon right class="primary--text">mdi-open-in-new</v-icon>
                            </v-btn>
                            <span>Open {{ currentOncotreeCode.label }} ({{ currentOncotreeCode.text }}) in the NCI Thesaurus</span>
                            </v-tooltip>
                            <span v-if="oncotree.externalReferences && !oncotree.externalReferences.NCI && currentOncotreeCode">
                                No NCI Thesaurus entry for {{ currentOncotreeCode.text}}: {{ currentOncotreeCode.label}}<br/>
                                You can try a manual search directly in the NCI Thesaurus here: 
                                <v-btn slot="activator" icon
                                href="https://ncit.nci.nih.gov/ncitbrowser/pages/home.jsf"
                                target="_blank"> <v-icon class="primary--text">mdi-open-in-new</v-icon>
                                </v-btn>
                            </span>
                        </v-flex>

                        <!-- Variant Results -->
                        <v-flex :class="getGeneFlexClasses()" v-show="currentlyActive == 'Variant'">
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
                                    <v-btn slot="activator" icon :href="variantSummaries[item].url" target="_blank" @click.stop class="primary--text"><v-icon>mdi-open-in-new</v-icon></v-btn>
                                    <span>Open {{ currentVariant }} in {{ item }} (new tab).</span>
                                    </v-tooltip>
                                    <v-tooltip bottom v-show="variantSummaries[item].url2 && !loading">
                                    <v-btn slot="activator" icon :href="variantSummaries[item].url2" target="_blank" @click.stop class="primary--text"><v-icon>mdi-lock</v-icon></v-btn>
                                    <span>Open {{ currentVariant }} in {{ item }} (need paid subscription).</span>
                                    </v-tooltip>
                                    </div>

                                </template>
                                <v-card class="pl-2 pr-2 pb-2 pt-0">
                                <v-card-text class="pl-2 pr-2 pt-0">
                                    <span v-for="(item, index) in variantSummaries[item].summary" :key="index">
                                        <span v-if="item.type == 'text'">{{ item.text }}</span>
                                        <a v-else :href="'https://www.ncbi.nlm.nih.gov/pubmed/?term=' + item.text" target="_blank">{{ item.text }}</a>
                                    </span>
                                </v-card-text>
                                </v-card>
                                </v-expansion-panel-content>
                                </v-expansion-panel>
                            </v-card-text>
                        </v-card>    
                        </v-flex>


                        </v-layout>
            </v-container>
            </v-card>
    </v-card>

</div>`, data() {
        return {
            panelVisible: false,
            currentlyActive: "",
            buttons: [ 
                {label: "Gene", tooltip: "What does this gene do?", icon: "mdi-dna"},
                {label: "Cancer", tooltip: "What are the commonly mutated genes in this cancer?", icon: "mdi-zodiac-cancer"},
                {label: "Variant", tooltip: "Is this variant functional?", icon: "mdi-clipboard-text-outline"},
                {label: "CNV", tooltip: "Is this amplification or deletion functional?", icon: "mdi-poll"},
                {label: "Fusion", tooltip: "Is this gene fusion important?", icon: "mdi-unfold-less-vertical"},
            ],
            currentGene: "",
            genePanelTitles: ["RefSeq", "OncoKB", "UniProt", "Jackson Labs", "Civic DB"],
            geneSummaries: {
                "RefSeq": {summary: null, url: null, url2: null, loading: false},
                "OncoKB": {summary: null, url: null}, url2: null, loading: false,
                "UniProt": {summary: null, url: null, url2: null, loading: false},
                "Jackson Labs" : {summary: null, url: null, url2: null, loading: false},
                "Civic DB" : {summary: null, url: null, loading: false},
            },
            variantPanelTitles: ["Jackson Labs", "Civic DB"],
            variantSummaries: {
                "Jackson Labs" : {summary: null, url: null, url2: null, loading: false},
                "Civic DB" : {summary: null, url: null, url2: null, loading: false},
            },
            loading: false,
            geneSummaryLoading: false,
            reactomeLoading: false,
            genePanel: [],
            variantPanel: [],
            ensemblId: null,
            currentOncotreeCode: {},
            oncotree: {},
            currentVariant: "",
            geneSymbolErrorMessage: null,
            variantSymbolErrorMessage: null,
            reactomeItems: [],
            reactomeItemsOpen: [],
            reactomeRegex: /stId":"(id[0-9]+)"/gm,
            reactomeContentDetailUrl: null,
            oncokbVariantName: null,
            plotlyConfig: {
                displayModeBar: true,
                displaylogo: false,
                modeBarButtonsToRemove: ['sendDataToCloud', 'editInChartStudio', 'select2d',
                'lasso2d', 'hoverClosestCartesian', 'hoverCompareCartesian',
                'toggleSpikelines', 'autoScale2d'],
                responsive: true
            },
            genePlotLoading: false,
            genePlotError: false,
            showGeneGeniePanel: true
        }

    },
    methods: {
        togglePanel() {
            this.panelVisible = !this.panelVisible;
            if (!this.panelVisible) {
                bus.$emit("need-layout-resize", this);
            }
        },
        openInNewWindow() {
            var params = "gene=" + (this.currentGene ? this.currentGene : "") + "&variant=" +  (this.currentVariant ? this.currentVariant : "")
            + "&oncotree=" +  (this.currentOncotreeCode ? this.currentOncotreeCode.text : "")
            + "&button=" +  (this.currentlyActive ? this.currentlyActive : "")
            window.open(webAppRoot + "/discovar?" + params, "_blank");
            this.panelVisible = false;
        },
        handleDialogs(response, callback) {
            if (response == "not-allowed") {
                bus.$emit("not-allowed", [this.reponse]);
            }
            if (response.isXss) {
                bus.$emit("xss-error",
                    [this, response.reason]);
            }
            else if (response.isLogin) {
                bus.$emit("login-needed", [this, callback])
            }
            else if (response.success === false) {
                this.splashProgress = 100; //should dismiss the splash dialog
                bus.$emit("some-error", [this, response.message]);
            }
        },
        handleButton(button) {
            this.currentlyActive = button.label;
            this.updateRoute();
        },
        updateRoute() {
            if (!this.standalone) {
                return;
            }
            var query = {
                gene: this.currentGene,
                variant: this.currentVariant,
                oncotree: this.currentOncotreeCode ? this.currentOncotreeCode.text : null,
                button: this.currentlyActive
            }
            this.$router.push({ query: query });
        },
        handleRouteChanged() {
            if (!this.standalone) {
                return;
            }
           this.currentGene = this.$route.query.gene;
           this.currentVariant = this.$route.query.variant;
           var oncotreeItems = this.oncotreeItems.filter(o => o.text == this.$route.query.oncotree);
           var oncotree = null;
           if (oncotreeItems && oncotreeItems[0]) {
               oncotree = oncotreeItems[0];
           }
           this.currentOncotreeCode = oncotree;
           this.currentlyActive = this.$route.query.button;
        },
        submitForm() {
            this.updateRoute();
            if (this.isFormValid()) {
                switch(this.currentlyActive) {
                    case "Gene":
                        this.toggleAllGenePanels(false);
                        this.geneAjaxSummary(this.genePanelTitles);
                        this.getReactomeLocations("Cell Cycle");
                        this.fetchGenePlot();
                        break;
                    case "Cancer":
                        this.getOncotree();
                        break;
                    case "Variant":
                        this.getVariantSummary();
                        break;    
                }
            }
        },
        geneAjaxSummary(databases) {
            this.geneSummaryLoading = true;
            this.geneSymbolErrorMessage = null;
            this.ensemblId = null;
            for (var i = 0; i < databases.length; i++) {
                // this.geneSummaries[databases[i]].loading = true;
                this.geneSummaries[databases[i]].summary = "";
                this.geneSummaries[databases[i]].url = null;
                this.geneSummaries[databases[i]].url2 = null;
            }
            axios.get(webAppRoot + "/getGeneSummary", {
                params: {
                    geneTerm: this.currentGene,
                    databases: databases.join(","),

                }
            })
            .then(response => {
                var responseDatabases = response.data.payload ? response.data.payload.databases : "";
                if (response.data.isAllowed && response.data.success) {
                    var panelsToOpen = [];
                   for (var i = 0; i < responseDatabases.length; i++) {
                       var database = responseDatabases[i];
                       var payload = response.data.payload.summaries[database];
                       var summary = payload && payload.summary ? this.formatPubMedLinks(payload.summary) : null;
                       panelsToOpen.push(summary ? true : false);
                       this.geneSummaries[database].summary = summary;
                       this.geneSummaries[database].url = payload && payload.moreInfoUrl ? payload.moreInfoUrl : "";
                       this.geneSummaries[database].url2 = payload && payload.moreInfoUrl2 ? payload.moreInfoUrl2 : "";
                    //    this.geneSummaries[database].loading = false;
                    }
                    this.ensemblId = response.data.payload && response.data.payload.ensembl ? response.data.payload.ensembl.ensemblId : null;
                    this.geneSymbolErrorMessage = this.ensemblId ? null : "This HUGO symbol doesn't exist.";
                    this.toggleAllGenePanels(true, panelsToOpen);
                    this.geneSummaryLoading = false;
                }
                else if (response.data.isAllowed && !response.data.success) {
                    for (var i = 0; i < responseDatabases.length; i++) {
                        var summary = this.formatPubMedLinks("Nothing found in " + database);
                        this.geneSummaries[database].summary = summary;
                        this.geneSummaries[database].url = null;
                        this.geneSummaries[database].url2 = null;
                        // this.geneSummaries[database].loading = false;
                    }
                    this.toggleAllGenePanels(true);
                    this.geneSummaryLoading = false;
                }
                else {
                    this.geneSummaryLoading = false;
                    this.handleDialogs(response.data, this.geneAjaxSummary.bind(null, databases));
                }
            })
            .catch(error => {
                this.geneSummaryLoading = false;
                alert(error);
            });
        },
        toggleAllGenePanels(doOpen, panelsToOpen) {
            if (panelsToOpen) {
                this.genePanel = panelsToOpen;
            }
            else {
                this.genePanel = this.genePanelTitles.map(i => doOpen);
            }
        },
        toggleAllVariantPanels(doOpen) {
            this.variantPanel = this.variantPanelTitles.map(i => doOpen);
        },
        calcMaxHeight() {
            if (this.standalone) {
                return null;
            }
            return getDialogMaxHeightNumber(90);
        },
        isFormValid() {
            var geneValid = this.currentGene && this.currentGene.length > 0;
            var oncotreeValid = this.currentOncotreeCode != null && this.currentOncotreeCode.text != null;
            var variantValid = this.currentVariant && this.currentVariant.length > 0;
            switch(this.currentlyActive) {
                case "Gene":
                    return geneValid;
                case "Cancer":
                    return oncotreeValid;
                case "Variant":
                    return geneValid && variantValid && oncotreeValid;
            }
            return false;
        },
        formatPubMedLinks(text) {
            var regex = /([0-9]{6,})/gm;
            var results = text.split(regex);
            var summary = [];
            for (var i = 0; i < results.length; i++) {
                var item = results[i];
                if (isNaN(item)) {
                    summary.push({text: item, type: "text"});
                }
                else {
                    summary.push({text: item, type: "link"});
                }
            }
            return summary;
        },
        getOncotree() {
            this.loading = true;
            this.oncotree = {};
            axios.get(webAppRoot + "/getOncotreeTumorType", {
                params: {
                    oncotreeCode: this.currentOncotreeCode.text
                }
            })
            .then(response => {
                if (response.data.isAllowed && response.data.success) {
                    this.oncotree = response.data.payload;
                }
                else if (response.data.isAllowed && !response.data.success) {
                    this.oncotree = {}; //either the oncotree doesn't exist or the website is down.
                }
                else {
                    this.handleDialogs(response.data, this.getOncotree);
                }
                this.loading = false;
            })
            .catch(error => {
                this.loading = false;
                alert(error);
            });
        },
        getReactomeLocations(levels) {
            this.reactomeLoading = true;
            this.reactomeContentDetailUrl = null;
            this.reactomeItems = [];
            axios.get(webAppRoot + "/getReactomeLocations", {
                params: {
                    geneTerm: this.currentGene,
                    levels: levels
                }
            })
            .then(response => {
                if (response.data.isAllowed && response.data.success) {
                    // console.log(response.data.payload);
                    this.reactomeItems = response.data.payload.items;
                    this.reactomeContentDetailUrl = response.data.payload.mainPageUrl;
                    this.$nextTick(() => {
                        this.openCloseAllReactome(false);
                    });

                }
                else if (response.data.isAllowed && !response.data.success) {
                    console.log(response.data.message);
                }
                else {
                    this.handleDialogs(response.data, this.getReactomeLocations.bind(this, levels));
                }
                this.reactomeLoading = false;
            })
            .catch(error => {
                this.reactomeLoading = false;
                alert(error);
            });
        },
        openCloseAllReactome(doOpen) {
            this.$refs.reactome.updateAll(doOpen);
        },
        openReactomeRoot(item) {
            var textToParse = JSON.stringify(item).match(this.reactomeRegex);
            var m;
            while ((m = this.reactomeRegex.exec(textToParse)) !== null) {
                // This is necessary to avoid infinite loops with zero-width matches
                if (m.index === this.reactomeRegex.lastIndex) {
                    this.reactomeRegex.lastIndex++;
                }
                m.forEach((match, groupIndex) => {
                    if (groupIndex == 1) {
                        this.reactomeItemsOpen.push(match);
                    }
                });
            }
        },
        isLoadingOverall() {
            switch(this.currentlyActive) {
                case "Gene":
                    return this.geneSummaryLoading || this.reactomeLoading;
                case "Cancer":
                    return this.loading;
                case "Variant":
                    return this.loading;
            }
            return this.loading;
        },
        getFlexClasses() {
            switch(this.currentlyActive) {
                case "Gene":
                    return ["xs12"]
                case "Cancer":
                    return ["xs12"]
                case "Variant":
                    if (this.standalone) {
                        return ["xs12","md6","lg4","xl3"];
                    }
                    else {
                        return ["xs12","md6","lg6","xl6"];
                    }
            }
        },
        getGeneFlexClasses() {
            if (this.standalone) {
                return "md12 lg6";
            }
            else {
                return "xs12";
            }
        },
        getCancerFlexClasses() {
            if (this.standalone) {
                return "xs12 md6 lg3";
            }
            else {
                return "xs12";
            }
        },
        oncotreeFilter(item, queryText, itemText) {
            const textOne = item.text.toLowerCase()
            const textTwo = item.label.toLowerCase()
            const searchText = queryText.toLowerCase()
            return textOne.indexOf(searchText) > -1 ||
              textTwo.indexOf(searchText) > -1
          },
        getVariantSummary() {
            this.loading = true;
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
                    oncokbVariantName: this.oncokbVariantName,
                    databases: this.variantPanelTitles.join(","),
                }
            })
            .then(response => {
                var responseDatabases = response.data.payload ? response.data.payload.databases : "";
                if (response.data.isAllowed && response.data.success) {
                   for (var i = 0; i < responseDatabases.length; i++) {
                       var database = responseDatabases[i];
                       var payload = response.data.payload.summaries[database];
                       var summary = this.formatPubMedLinks(payload && payload.summary ? payload.summary : "No summary found in " + database);
                       this.variantSummaries[database].summary = summary;
                       this.variantSummaries[database].url = payload && payload.moreInfoUrl ? payload.moreInfoUrl : null;
                       this.variantSummaries[database].url2 = payload && payload.moreInfoUrl2 ? payload.moreInfoUrl2 : null;
                    }
                    // this.variantSymbolErrorMessage = this.ensemblId ? null : "No entry could be found.";
                    this.toggleAllVariantPanels(true);
                    this.loading = false;
                }
                else if (response.data.isAllowed && !response.data.success) {
                    for (var i = 0; i < responseDatabases.length; i++) {
                        var summary = this.formatPubMedLinks("Nothing found in " + database);
                        this.variantSummaries[database].summary = summary;
                        this.variantSummaries[database].url = null;
                        this.variantSummaries[database].url2 = null;
                    }
                    this.toggleAllVariantPanels(true);
                    this.loading = false;
                }
                else {
                    this.loading = false;
                    this.handleDialogs(response.data, this.getVariantSummary);
                }
            })
            .catch(error => {
                this.loading = false;
                alert(error);
            });
            //also search gene and oncotree
            this.geneAjaxSummary(this.genePanelTitles);
            this.getReactomeLocations("Cell Cycle");
            this.getOncotree();
        },  
        reloadValues() {
            this.$emit("reload-values");
        },
        fetchGenePlot() {
            this.genePlotLoading = true;
            this.genePlotError = false;
            var promise1 = this.updateBarPlot("alterationByCancerPlot", "/getAlterationByCancer");
            var promise2 = this.updateBarPlot("cancerByPercentPlot", "/getCancerbyPercent");
            var promise2 = this.updateLolliplotPlot("genieLollipopPlot", "/getGenieGeneLollipop");
            Promise.all([promise1, promise2]).then(values => {
                this.genePlotLoading = false;
                if (values.filter(v => v.success).length != values.length) {
                    console.log("Some plots did not finish properly");
                    this.genePlotError = true;
                }
                else {
                    this.genePlotError = false;
                }
            })
        },
        updateBarPlot(plotId, url) {
            if (!this.currentGene) {
                return;
            }
            return new Promise((resolve, reject) => {
            axios.get(webAppRoot + url, {
                params: {
                    hugoSymbol: this.currentGene,
                    plotId: plotId,
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        var chartData = response.data;
                        var trace = chartData.trace;
                        trace.type = "bar";
                        trace.orientation = "h";
                        trace.text = trace.labels;
                        // trace.width=trace.y.map( i=> 0.5);
                        trace.hovertemplate = "%{text}<extra></extra>"
                        var data = [trace];
                        var layout = {
                            title: this.createPlotTitle(chartData.plotId),
                            hovermode: 'closest',
                            font: {
                                family: 'Roboto,sans-serif',
                            },
                            xaxis: {automargin: true, title: this.createXAxisTitle(chartData.plotId)},
                            yaxis: {automargin: true, title: {text: this.createYAxisTitle(chartData.plotId), standoff: 10}},
                            margin: {
                                t: this.getPlotTopMargin()
                            },
                            height: 350
                        }
                        this.$nextTick(() => {
                            Plotly.newPlot(chartData.plotId, data, layout, this.plotlyConfig);
                        });
                        resolve({
                            success: true
                        });
                    }
                    else if (response.data.isAllowed && !response.data.success) {
                        resolve({
                            success: false
                        });
                    }
                    else {
                        this.handleDialogs(response.data, this.updateBarPlot.bind(null, plotId, url));
                    }
                })
                .catch(error => {
                    console.log(error);
                    reject({
                        success: false
                    });
                });
            });
        },
        updateLolliplotPlot(plotId, url) {
            if (!this.currentGene) {
                return;
            }
            return new Promise((resolve, reject) => {
            axios.get(webAppRoot + url, {
                params: {
                    hugoSymbol: this.currentGene,
                    plotId: plotId,
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        var chartData = response.data.payload;
                        var trace = chartData.trace;
                        trace.type = "scattergl";
                        trace.mode = 'markers';
                        trace.marker = { size: 9};
                        trace.text = trace.labels;
                        trace.yaxis = "y";
                        trace.hovertemplate = "%{text}<extra></extra>"
                        var data = [trace];
                        var shapes = trace.x.map((x,index) => ({
                            type: 'line',
                            xref: 'x',
                            yref: 'y',
                            x0: x,
                            y0: 0,
                            x1: x,
                            y1: trace.y[index],
                            line: {
                                color:'grey',
                                width: 1
                            },
                            layer: 'below'
                        }
                            )
                        );
                        var annotations = [];
                        var y1Even = chartData.maxY * -0.1;
                        var y0Even = y1Even * 0.1;
                        var height = Math.abs(y1Even - y0Even);
                        var y1Odd = y1Even - height;
                        var y0Odd = y1Odd + height;
                        for (var i =0; i < chartData.underlineTraces[0].x.length; i++) {
                            var shape = {
                                type: 'rect',
                            xref: 'x',
                            yref: 'y',
                            x0: chartData.underlineTraces[0].start[i],
                            y0: i % 2 == 0 ? y0Even : y0Odd,
                            x1: chartData.underlineTraces[0].end[i],
                            y1: i % 2 == 0 ? y1Even : y1Odd,
                            fillcolor: '#f9e04a',
                            opacity: 0.7,
                            line: {
                                width: 0
                            },
                            };
                            shapes.push(shape);

                            let annotation = {
                                showarrow: false,
                                text: chartData.annotations[i],
                                align: "center",
                                x: chartData.underlineTraces[0].x[i],
                                xanchor: "center",
                                y: i % 2 == 0 ? y0Even : y0Odd,
                                yanchor: "top",
                                font: {
                                    // color: "white"
                                }
                            }
                            annotations.push(annotation);
                        }
                        var bottomTrace = chartData.underlineTraces[0];
                        bottomTrace.type = "scatter";
                        bottomTrace.yaxis = "y";
                        bottomTrace.y = bottomTrace.x.map((p, index) => index % 2 == 0 ? y0Even - height / 2 : y0Odd - height / 2);
                        bottomTrace.mode = "markers";
                        bottomTrace.text = bottomTrace.labels;
                        bottomTrace.hovertemplate = "%{text}<extra></extra>";
                        bottomTrace.marker = {
                            color: '#f9e04a'
                        }
                        bottomTrace.opacity = 0;
                        data.push(bottomTrace);

                        var layout = {
                            title: this.createPlotTitle(chartData.plotId),
                            hovermode: 'closest',
                            font: {
                                family: 'Roboto,sans-serif',
                            },
                            xaxis: {
                                title: this.createXAxisTitle(chartData.plotId)
                            },
                            yaxis: {
                                title: {text: this.createYAxisTitle(chartData.plotId)},
                            },
                            margin: {
                                t: this.getPlotTopMargin()
                            },
                            height: 350,
                            showlegend: false,
                            shapes: shapes,
                            annotations: annotations,
                               
                        }
                        this.$nextTick(() => {
                            Plotly.newPlot(chartData.plotId, data, layout, this.plotlyConfig);
                            //need to hide tick labels below 0. Use d3
                            var axisTickLabels = Plotly.d3.selectAll("#genieLollipopPlot .yaxislayer-above").selectAll('text')[0];
                            for (var i = 0; i < axisTickLabels.length; i++) {
                                if (isNaN(axisTickLabels[i].innerHTML.substring(0, 1))) {
                                    axisTickLabels[i].innerHTML = " ";
                                }
                            }
                        });
                        resolve({
                            success: true
                        });
                    }
                    else if (response.data.isAllowed && !response.data.success) {
                        resolve({
                            success: false
                        });
                    }
                    else {
                        this.handleDialogs(response.data, this.updateBarPlot.bind(null, plotId, url));
                    }
                })
                .catch(error => {
                    console.log(error);
                    reject({
                        success: false
                    });
                });
            });
        },
        
        getPlotTopMargin() {
            return this.standalone ? 30 : null;
        },

        createPlotTitle(plotId) {
            switch(plotId) {
                case "alterationByCancerPlot": return this.currentGene + " Alterations by Cancer";
                case "cancerByPercentPlot": return "Cancers with most " + this.currentGene + " Alterations by Percent";
                case "genieLollipopPlot": return "Lollipop Plot of Genie " + this.currentGene + " Variants";

            }
        },
        createXAxisTitle(plotId) {
            switch(plotId) {
                case "alterationByCancerPlot": return "Number of Variants";
                case "cancerByPercentPlot": return "Percent of cases";
                case "genieLollipopPlot": return "Amino Acid Position";

            }
        },
        createYAxisTitle(plotId) {
            switch(plotId) {
                case "alterationByCancerPlot": return "Cancer Type";
                case "cancerByPercentPlot": return "Cancer Type";
                case "genieLollipopPlot": return "Variant Count";

            }
        }
    },
    mounted() {
        this.toggleAllGenePanels(true);
    },
    created: function () {
    },
    destroyed: function () {
    },
    watch: {
        '$route': "handleRouteChanged"
    }


});