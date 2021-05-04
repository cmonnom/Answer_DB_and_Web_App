Vue.component('lookup-panel-gene', {
    props: {
        standalone: { default: true, type: Boolean },
        currentGene: { default: "", type: String },
        lastGene: { default: "", type: String },
        uniqId: {default: "uniqcna", type:String}
    },
    template: /*html*/`<v-layout row wrap>

    <!-- Gene Results -->
    <v-flex :class="getGeneFlexClasses()">
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
                            <div>{{ item }} <span v-show="!geneSummaries[item].summary && !geneSummaryLoading">(no
                                    summary found)</span>
                                <v-tooltip bottom v-show="geneSummaries[item].url && !geneSummaryLoading">
                                    <v-btn slot="activator" icon :href="geneSummaries[item].url" target="_blank" rel="noreferrer"
                                        @click.stop class="primary--text">
                                        <v-icon>mdi-open-in-new</v-icon>
                                    </v-btn>
                                    <span>Open {{ lastGene }} in {{ item }} (new tab).</span>
                                </v-tooltip>
                                <v-tooltip bottom v-show="geneSummaries[item].url2 && !geneSummaryLoading">
                                    <v-btn slot="activator" icon :href="geneSummaries[item].url2" target="_blank" rel="noreferrer"
                                        @click.stop class="primary--text">
                                        <v-icon>mdi-lock</v-icon>
                                    </v-btn>
                                    <span>Open {{ lastGene }} in {{ item }} (need paid subscription).</span>
                                </v-tooltip>
                            </div>

                        </template>
                        <v-card class="pl-2 pr-2 pb-2 pt-0">
                            <v-card-text class="pl-2 pr-2 pt-0">
                                <span v-for="(item, index) in geneSummaries[item].summary" :key="index">
                                    <span v-if="item.type == 'text'">{{ item.text }}</span>
                                    <a v-else :href="'https://www.ncbi.nlm.nih.gov/pubmed/?term=' + item.text"
                                        target="_blank" rel="noreferrer">{{ item.text }}</a>
                                </span>
                            </v-card-text>
                        </v-card>
                    </v-expansion-panel-content>
                </v-expansion-panel>
            </v-card-text>
        </v-card>
    </v-flex>
    <v-flex :class="getGeneFlexClasses()">
        <v-layout row wrap>

            <!-- GDC -->
            <v-flex xs12>
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
                    <v-card-text class="pa-3">
                        <span class="body-2"><b>GDC Data Portal</b>
                        </span>
                        <v-tooltip bottom v-if="ensemblId">
                            <v-btn icon slot="activator" :href="'https://portal.gdc.cancer.gov/genes/' + ensemblId"
                                target="_blank" rel="noreferrer">
                                <v-icon class="primary--text">mdi-open-in-new</v-icon>
                            </v-btn>
                            <span>Open {{ lastGene }} on the NIH GDC Data Portal</span>
                        </v-tooltip>
                        <span v-show="!geneLolliplotPlotLoading">Click on a cancer type to show it in the lollipop plot.</span>

                        <div :class="[genePlotLoading ? 'alpha-54' : '']">
                            <div v-if="genePlotError && !genePlotLoading">No Genie Data could be found for gene
                                {{ lastGene }}</div>
                            <div v-if="!genePlotError" v-show="showGeneGeniePanel" width="100%">
                                <div :class="[geneLolliplotPlotLoading ? 'alpha-54' : '']">
                                    <div :id="'genieLollipopPlot' + uniqId"></div>
                                </div>
                                <div :id="'alterationByCancerPlot' + uniqId"></div>
                                <div :id="'cancerByPercentPlot' + uniqId"></div>
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
                            <div class="pa-1 body-2"><b>{{ lastGene }} locations</b>
                                <v-tooltip bottom>
                                    <v-btn slot="activator" icon :href="reactomeContentDetailUrl" target="_blank" rel="noreferrer"
                                        @click.stop class="primary--text">
                                        <v-icon>mdi-open-in-new</v-icon>
                                    </v-btn>
                                    <span>Open Reactome PathwayBrowser Locations (new tab).</span>
                                </v-tooltip>
                            </div>
                            <v-treeview open-on-click :items="reactomeItems" item-key="stId" :open-all="false"
                                ref="reactome" :open.sync="reactomeItemsOpen" hoverable style="max-width:100%">
                                <!---
                    <template v-slot:prepend="{ item }">
                    <v-icon>mdi-alert-circle-outline</v-icon>
                    </template>
                    -->
                                <template v-slot:label="{ item }">
                                    <v-tooltip bottom>
                                        <a @click.stop class="body-1" slot="activator" v-if="item.url" :href="item.url"
                                            target="_blank" rel="noreferrer">{{ item.name }}</a>
                                        <span>Open Reactome's PathwayBrowser</span>
                                    </v-tooltip>
                                    <span v-if="!item.rootLevel && !item.url" class="body-1"
                                        slot="activator">{{ item.name }}</span>
                                    <span v-if="item.rootLevel" class="font-weight-bold body-1"
                                        slot="activator">{{ item.name }}</span>
                                    <v-tooltip bottom v-if="item.rootLevel">
                                        <v-btn icon small @click.stop="openReactomeRoot(item)" slot="activator"
                                            class="primary--text">
                                            <v-icon class="pr-0">mdi-arrow-expand-vertical</v-icon>
                                        </v-btn>
                                        <span>Expand Branch</span>
                                    </v-tooltip>
                                </template>
                            </v-treeview>
                        </v-card-text>
                        <v-card-text class="pa-2" v-show="reactomeError && !reactomeLoading">
                            {{ reactomeError }}
                        </v-card-text>
                    </v-card>
                </v-card>
            </v-flex>


        </v-layout>
        <lookup-panel-plot-utils ref="plotUtils"
        :standalone="standalone" @handle-dialogs="handleDialogs"
        @fetchGene-lollipop-plot-only="fetchGeneLollipopPlotOnly"></lookup-panel-plot-utils>
    </v-flex>

</v-layout>`, data() {
        return {
            panelVisible: false,
            genePanelTitles: ["RefSeq", "OncoKB", "UniProt", "Jackson Labs", "Civic DB"],
            geneSummaries: {
                "RefSeq": { summary: null, url: null, url2: null, loading: false },
                "OncoKB": { summary: null, url: null }, url2: null, loading: false,
                "UniProt": { summary: null, url: null, url2: null, loading: false },
                "Jackson Labs": { summary: null, url: null, url2: null, loading: false },
                "Civic DB": { summary: null, url: null, loading: false },
            },
            loading: false,
            geneSummaryLoading: false,
            reactomeLoading: false,
            genePanel: [],
            variantPanel: [],
            ensemblId: null,
            oncotree: {},
            currentVariant: "",
            geneSymbolErrorMessage: null,
            variantSymbolErrorMessage: null,
            reactomeItems: [],
            reactomeItemsOpen: [],
            reactomeRegex: /stId":"(id[0-9]+)"/gm,
            reactomeContentDetailUrl: null,
            oncokbVariantName: null,
            genePlotLoading: false,
            genePlotError: false,
            showGeneGeniePanel: true,
            reactomeError: null,
            cancerPlotLoading: false,
            cancerPlotError: false,
            geneLolliplotPlotLoading: false,
            geneLollipopPlotError: false,
            cividisColors: {
                "blue75": "#38486b", //blue75
                "Deletion": "#38486b", //blue75
                "Amplification": "#a39a76", //brown50
                "SNP/Indel": "#e4cf5b", //yellow25
                "DEL": "#e4cf5b", //yellow25
            },
        }

    },
    methods: {
        handleDialogs(response, callback) {
            this.$emit("handle-dialogs", [null, response, callback]);
        },
        reload() {
            this.toggleAllGenePanels(false);
            this.geneAjaxSummary(this.genePanelTitles);
            this.getReactomeLocations("Cell Cycle");
            this.fetchGenePlot();
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
                            var summary = payload && payload.summary ? formatPubMedLinks(payload.summary) : null;
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
                            var summary = formatPubMedLinks("Nothing found in " + database);
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
        getReactomeLocations(levels) {
            this.reactomeLoading = true;
            this.reactomeContentDetailUrl = null;
            this.reactomeError = null;
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
                        this.reactomeError = this.currentGene + " could not be found in Reactome."
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
            if (this.$refs.reactome) {
                this.$refs.reactome.updateAll(doOpen);
            }
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
        isLoading() {
            return this.geneSummaryLoading || this.reactomeLoading ||  this.genePlotLoading;
        },
        getGeneFlexClasses() {
            if (this.standalone) {
                return "md12 lg6";
            }
            else {
                return "xs12";
            }
        },
        fetchGenePlot() {
            if (this.currentGene && this.$refs.plotUtils) {
                this.genePlotLoading = true;
                this.geneLolliplotPlotLoading = true;
                this.genePlotError = false;
                var promise1 = this.$refs.plotUtils.updateBarPlot("/getAlterationByCancer", {
                    hugoSymbol: this.currentGene,
                    plotId: "alterationByCancerPlot" + this.uniqId,
                });
                var promise2 = this.$refs.plotUtils.updateBarPlot("/getCancerbyPercent", {
                    hugoSymbol: this.currentGene,
                    plotId: "cancerByPercentPlot" + this.uniqId,
                });
                var promise3 = this.$refs.plotUtils.updateLolliplotPlot("genieLollipopPlot" + this.uniqId, "/getGenieGeneLollipop", this.currentGene);
                Promise.all([promise1, promise2, promise3]).then(values => {
                    this.genePlotLoading = false;
                    this.geneLolliplotPlotLoading = false;
                    if (values.filter(v => v.success).length != values.length) {
                        console.log("Some plots did not finish properly");
                        this.genePlotError = true;
                    }
                    else {
                        this.genePlotError = false;
                    }
                })
            }
        },
        // call back function from a horizontal bar chart with a cancer name
        fetchGeneLollipopPlotOnly(cancerType) {
            if (this.currentGene && this.$refs.plotUtils) {
                this.geneLolliplotPlotLoading = true;
                this.geneLollipopPlotError = false;
                var promise3 = this.$refs.plotUtils.updateLolliplotPlot("genieLollipopPlot" + this.uniqId, "/getGenieGeneLollipop", this.currentGene, cancerType);
                Promise.all([promise3]).then(values => {
                    this.geneLolliplotPlotLoading = false;
                    if (values.filter(v => v.success).length != values.length) {
                        console.log("Some plots did not finish properly");
                        this.geneLollipopPlotError = true;
                    }
                    else {
                        this.geneLollipopPlotError = false;
                    }
                });
                this.updateSelectedBarPlot(cancerType);
            }
        },
        updateSelectedBarPlot(cancerType) {
            var plotIds = ['alterationByCancerPlot' + this.uniqId, 'cancerByPercentPlot' + this.uniqId,]
            for (var i = 0; i < plotIds.length; i++) {
                var thePlot = document.getElementById(plotIds[i]);
                var data = thePlot.data[0];
                var colors= data.marker.color;
                for (var j =0; j < colors.length; j++) {
                colors[j] = this.cividisColors.blue75; //reset original colors
            }
                var pointClicked = data.y.indexOf(cancerType);
                if (pointClicked != -1) {
                    colors[pointClicked] = this.cividisColors.DEL
                }
                var update = {'marker':{color: colors}};
                Plotly.restyle(thePlot, update);
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
    }


});