Vue.component('fpkm-plot', {
    props: {
        canPlot: { default: false, type: Boolean },
        oncotree: {default: () => [], type:Array },
        oncotreeCode: {default: null, type: String}
    },
    template: `
   
        <v-card width="500px" class="pt-3 pl-3 pr-3 pb-3">
            <v-card-actions>
            <v-layout row wrap justify-space-between>
                <v-flex>
                    <v-autocomplete
                    clearable v-model="currentGene"
                    placeholder="Start typing a gene name"
                    class="no-height-select"
                    :search-input.sync="search"
                    hide-no-data
                    label="Gene Name"
                    hide-details
                    item-text="name" item-value="value"
                    return-object
                    :items=items
                    :disabled="!canPlot"
                    @input="updateFPKMPlot"
                    >
                    </v-autocomplete>
                </v-flex>

                <v-flex>
                <v-btn @click="updateFPKMPlot" color="primary"
                :disabled="!currentGene || !canPlot"
                :loading="loading">Refresh
                </v-btn>
                </v-flex>
                <v-flex>
                <v-btn @click="closeFPKMPlot" color="error"
                >Close
                </v-btn>
                </v-flex>
                <v-flex xs12>
                    <v-layout row wrap justify-end>
                        <v-flex class="pl-2">
                            <v-switch color="primary" @change="updateFPKMPlot" :disabled="!currentGene || !canPlot" class="no-margin-top-controls" hide-details v-model="useLog2" label=" Use Log2"></v-switch>
                        </v-flex>
                    </v-layout>
                </v-flex>
            </v-layout>
            </v-card-actions>
            <v-card-text class="pt-3 pl-3 pr-4 pb-3 subheading"
            style="min-height:550px">
            <!--
            <div v-if="currentGene && canPlot" class="centered title pb-2">
            FPKM for cases diagnosed with {{ currentOncotreeCode }}
            <br/> on gene {{ getCurrentGeneName() }}</div> 
            -->
            <div v-if="showNoDataMessage" class="centered subheading pb-2">
            {{ noDataMessage }}
            </div>
            <div id="fpkmPlot" v-show="canPlot"></div>
            <div v-show="!canPlot" class="subheading">
                <v-layout fill-height align-center justify-center row>
                    <v-flex xs12>
                    This case doesn't have an OncoTree Diagnosis yet.<br/>
                    Please select one and save your changes first.
                    </v-flex>
                </v-layout>
            </div>
            </div>
            </v-card-text>
        </v-card>

`,
    data() {
        return {
            cividisColors: {
                blue100: "#00204d",
                blue90: "#00306f",
                blue75: "#38486b",
                brown50: "#a39a76",
                yellow25: "#e4cf5b",
                yellow5: "#f9e04a"
            }, 
            currentGene: null,
            hardCodedItems: [{name: "CRLF2", value: "CRLF2"},
                {name: "BCL2", value: "BCL2"},
                {name: "BCL6", value: "BCL6"},
                {name: "MYC", value: "MYC"},
            ],
            plotlyConfig: {
                displayModeBar: true,
                displaylogo: false,
                modeBarButtonsToRemove: ['sendDataToCloud', 'editInChartStudio', 'select2d',
                'lasso2d', 'hoverClosestCartesian', 'hoverCompareCartesian',
                'toggleSpikelines', 'autoScale2d'],
                responsive: true
            },
            items: [],
            search: null,
            showOtherPlots: false,
            loading: false,
            currentOncotreeCode: null,
            useLog2: true,
            openedOnce: false,
            noDataMessage: "No FPKM Data could be found",
            showNoDataMessage: false,
            oncotreeAMLCodes: ["AML",
            "AMLNOS",
            "AMKL",
            "AMML",
            "APMF",
            "AWM",
            "AMOL",
            "PERL",
            "ABL",
            "AM",
            "AMLMD",
            "TMN",
            "TAML",
            "TMDS",
            "MPRDS",
            "TAM",
            "MLADS",
            "AMLMRC",
            "MS",
            "AMLRGA",
            "AMLGATA2MECOM",
            "AMLBCRABL1",
            "AMLCBFBMYH11",
            "AMLRBM15MKL1",
            "AMLRUNX1",
            "AMLMLLT3KMT2A",
            "AMLDEKNUP214",
            "AMLNPM1",
            "AMLRUNX1RUNX1T1",
            "AMLCEBPA",
            "APLPMLRARA"],
            oncotreeBLLCodes: ["BLL",
            "BLLRGA",
            "BLLBCRABL1L",
            "BLLKMT2A",
            "BLLTCF3PBX1",
            "BLLIL3IGH",
            "BLLIAMP21",
            "BLLHYPER",
            "BLLHYPO",
            "BLLBCRABL1",
            "BLLETV6RUNX1",
            "BLLNOS",
            "AMLNOS",
            "AMKL",
            "AMML",
            "APMF",
            "AWM",
            "AMOL",
            "PERL",
            "ABL",
            "AM",
            "AMLMD",
            "TMN",
            "TAML",
            "TMDS",
            "MPRDS",
            "TAM",
            "MLADS",
            "AMLMRC",
            "MS",
            "AMLRGA",
            "AMLGATA2MECOM",
            "AMLBCRABL1",
            "AMLCBFBMYH11",
            "AMLRBM15MKL1",
            "AMLRUNX1",
            "AMLMLLT3KMT2A",
            "AMLDEKNUP214",
            "AMLNPM1",
            "AMLRUNX1RUNX1T1",
            "AMLCEBPA",
            "APLPMLRARA"]

        }

    },
    methods: {
        closeFPKMPlot() {
            this.$emit("hide-fpkm-plot", this);
        },
        handleDialogs(response, callback) {
            if (response == "not-allowed") {
                bus.$emit("not-allowed", [this.response]);
            }
            if (response.isXss) {
                bus.$emit("xss-error",
                    [this, response.reason]);
            }
            else if (response.isLogin) {
                bus.$emit("login-needed", [this, callback])
            }
            else if (response.success === false) {
                bus.$emit("some-error", [this, response.message]);
            }
        },
        handleAxiosError(error) {
            console.log(error);
            bus.$emit("some-error", [this, error]);
        },
        getTissueFromOncotreeCode(code) {
            return this.oncotree.filter( i => {return i.text === code;})[0].tissue;
        },
        createPlotTitle(code, nbOfCases) {
            let caseString = nbOfCases ? nbOfCases : 0;
            if (nbOfCases > 1) {
                caseString += " cases";
            }
            else {
                caseString += " case";
            }
            return "FPKM from " + this.getTissueFromOncotreeCode(code) +" tissue<br> on gene " + this.getCurrentGeneName() + " (" + caseString + ")";
        },
        updateFPKMPlot() {

            if (!this.currentGene) {
                return;
            }
            this.loading = true;
            this.showNoDataMessage = false;
            axios.get(webAppRoot + "/getFPKMChartData", {
                params: {
                    caseId: this.$route.params.id,
                    geneParam: this.currentGene.value.trim(),
                    showOtherPlots: this.showOtherPlots,
                    useLog2: this.useLog2
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        var chartData = response.data;
                        this.currentOncotreeCode = chartData.label;
                        var allDataTrace = {
                            y: chartData.boxData,
                            type: 'box',
                              name: "All Cases",
                              marker: {
                                  color: this.cividisColors.brown50
                              },
                              
                          };
                        var currentCaseTrace = {
                            y: [chartData.currentCaseData],
                            x: ["All Cases"],
                            name: "Current Case",
                            mode: "markers",
                            type: "scatter",
                            text: [chartData.currentCaseLabel],
                            marker: {
                                color: this.cividisColors.blue100
                            },
                            hovertemplate: "%{text}: %{y}"
                        }  
                        var outliersTrace = {
                            y: chartData.outliersData,
                            x: chartData.outliersData.map(i => "All Cases"),
                            name: "Outliers",
                            mode: "markers",
                            type: "scatter",
                            text: chartData.outliersLabels,
                            marker: {
                                color: this.cividisColors.brown50
                            },
                            hovertemplate: "%{text}: %{y}"
                        }
                        var data = [allDataTrace, currentCaseTrace, outliersTrace];
                        var layout = {
                            title: this.createPlotTitle(chartData.label, chartData.nbOfCases),
                            yaxis: {
                                title: this.getCurrentGeneName() + ": FPKM" + (this.useLog2 ? " (log2)": ""),
                                zeroline: false,
                                range: [0, chartData.max],
                            },
                            xaxis: {
                                showticklabels: false,
                            },
                            hovermode: 'closest',
                            font: {
                                family: 'Roboto,sans-serif',
                            }
                        }
                        this.$nextTick(() => {
                            Plotly.newPlot('fpkmPlot', data, layout, this.plotlyConfig);
                            });
                    }
                    else if (response.data.isAllowed && !response.data.success) {
                        this.showNoDataMessage = true;
                    }
                    else {
                        this.handleDialogs(response.data, this.updateFPKMPlot);
                    }
                    this.loading = false;
                })
                .catch(error => {
                    this.loading = false;
                    console.log(error);
                });
        },
        searchGene(value) {
            if (!value || (value.length < 3 && value != "AR")) {
                return;
            }
            axios.get(webAppRoot + "/searchGenesInPanels", {
                params: {
                    geneSearch: value
                }
            })
            .then(response => {
                if (response.data.isAllowed) {
                    var tempList = response.data.items;
                    for (var i = 0; i < this.hardCodedItems.length; i++) {
                        tempList.push(this.hardCodedItems[i]);
                    }
                    tempList.sort((a,b) => { return this.geneItemCompare(a,b)});
                    this.items = tempList;
                }
                else {
                    this.handleDialogs(response.data, this.searchGene);
                }
            })
            .catch(error => {
                console.log(error);
            });
        },
        getCurrentGeneName() {
            if (this.currentGene) {
                return this.currentGene.name;
            }
            else {
                return "";
            }
        },
        geneItemCompare(a, b) {
            if (a.name < b.name) {
                return  -1;
            }
            if (a.name > b.name) {
                return 1;
            }
            return 0;
        },
        populateItems() {
            this.items = [];
            for (var i = 0; i < this.hardCodedItems.length; i++) {
                this.items.push(this.hardCodedItems[i]);
            }
            this.items.sort((a,b) => { return this.geneItemCompare(a,b)});
        },
        loadDefaultFPKMPlot() {
            if (this.openedOnce) {
                return;
            }
            if (this.oncotreeAMLCodes.indexOf(this.oncotreeCode) > -1
            || this.oncotreeBLLCodes.indexOf(this.oncotreeCode) > -1) {
                this.currentGene = {"name":"CRLF2","value":"CRLF2"};
                this.openedOnce = true;
                this.$nextTick(() => {
                    this.updateFPKMPlot();
                    });
            }
        }
    },
    computed: {
        webAppRoot() {
            return webAppRoot;
        }
    },
    mounted: function () {
        this.loadDefaultFPKMPlot();
    },
    created() {
        this.populateItems();
    },
    destroyed() {
        if (document.getElementById('fpkmPlot')) {
            Plotly.purge('fpkmPlot');
        }
    },
    watch: {
        search(value) {
            this.searchGene(value);
        } 
    }


});