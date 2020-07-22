Vue.component('tmb-plot', {
    props: {
        canPlot: { default: false, type: Boolean },
        oncotree: {default: () => [], type:Array },
        oncotreeCode: {default: null, type: String}
    },
    template: `
   
        <v-card width="500px" class="pt-3 pl-3 pr-3 pb-3">
            <v-card-actions>
            <v-layout row wrap justify-end>
                <v-flex class="pr-2">
                <v-btn @click="updateTMBPlot" color="primary"
                :disabled="!canPlot"
                :loading="loading">Refresh
                </v-btn>
                </v-flex>
                <v-flex>
                <v-btn @click="closeTMBPlot" color="error"
                >Close
                </v-btn>
                </v-flex>
                <v-flex xs12>
                    <v-layout row wrap justify-end>
                        <v-flex class="pl-2 pt-2 mt-1">
                            <v-switch color="primary" @change="updateTMBPlot" :disabled="!canPlot" class="no-margin-top-controls" hide-details v-model="useLog2" label=" Use Log2"></v-switch>
                        </v-flex>
                    </v-layout>
                </v-flex>
            </v-layout>
            </v-card-actions>
            <v-card-text class="pt-3 pl-3 pr-4 pb-3 subheading"
            style="min-height:550px">
            <div v-if="showNoDataMessage" class="centered subheading pb-2">
            {{ noDataMessage }}
            </div>
            <div id="tmbPlot" v-show="canPlot"></div>
            <div v-show="!canPlot" class="subheading">
                <v-layout fill-height align-center justify-center row>
                    <v-flex xs12>
                    This case needs an OncoTree Diagnosis<br/>
                    and a value for Tumor Mutation Burder<br/>
                    Please select an Oncotree Diagnosis and save your changes first.
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
            useLog2: true,
            openedOnce: false,
            noDataMessage: "No TMB Data could be found",
            showNoDataMessage: false,
        }

    },
    methods: {
        closeTMBPlot() {
            this.$emit("hide-tmb-plot", this);
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
        createPlotTitle(code, nbOfCases) {
            let caseString = nbOfCases ? nbOfCases : 0;
            if (nbOfCases > 1) {
                caseString += " cases";
            }
            else {
                caseString += " case";
            }
            return "TMB from " + this.getTissueFromOncotreeCode(code) +" tissues";
        },
        getTissueFromOncotreeCode(code) {
            return this.oncotree.filter( i => {return i.text === code;})[0].tissue;
        },
        updateTMBPlot() {
            this.loading = true;
            this.showNoDataMessage = false;
            axios.get(webAppRoot + "/getTMBChartData", {
                params: {
                    caseId: this.$route.params.id,
                    oncotreeCode: this.oncotreeCode,
                    showOtherPlots: this.showOtherPlots,
                    useLog2: this.useLog2
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        var chartData = response.data;
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
                        ymax = Math.max(1, chartData.max);
                        var layout = {
                            title: this.createPlotTitle(chartData.label, chartData.nbOfCases),
                            yaxis: {
                                title: "TMB (Mutations/MB)" + (this.useLog2 ? " (log2)": ""),
                                zeroline: false,
                                range: [0, ymax],
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
                            Plotly.newPlot('tmbPlot', data, layout, this.plotlyConfig);
                            });
                    }
                    else if (response.data.isAllowed && !response.data.success) {
                        this.showNoDataMessage = true;
                    }
                    else {
                        this.handleDialogs(response.data, this.updateTMBPlot);
                    }
                    this.loading = false;
                })
                .catch(error => {
                    this.loading = false;
                    console.log(error);
                });
        },
        loadDefaultTMBPlot() {
            if (this.openedOnce || !this.oncotreeCode) {
                return;
            }
            this.openedOnce = true;
            this.$nextTick(() => {
                this.updateTMBPlot();
                });
        }
    },
    computed: {
        webAppRoot() {
            return webAppRoot;
        }
    },
    mounted: function () {
        this.loadDefaultTMBPlot();
    },
    created() {
    },
    destroyed() {
        if (document.getElementById('tmbPlot')) {
            Plotly.purge('tmbPlot');
        }
    },
    watch: {
    }


});