Vue.component('fpkm-plot', {
    props: {
        canPlot: { default: false, type: Boolean },
        oncotree: {default: () => [], type:Array }
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
            </v-layout>
            </v-card-actions>
            <v-card-text class="pt-3 pl-3 pr-4 pb-3 subheading"
            style="min-height:550px">
            <!--
            <div v-if="currentGene && canPlot" class="centered title pb-2">
            FPKM for cases diagnosed with {{ currentOncotreeCode }}
            <br/> on gene {{ getCurrentGeneName() }}</div> 
            -->
            <div id="fpkmPlot" v-if="canPlot"></div>
            <div v-else class="subheading">
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
            fpkmPlotDataConfig: null,
            gui: {
                behaviors: [
                    { id: 'DownloadSVG', enabled: 'none' },
                    { id: 'ViewSource', enabled: 'none' },
                    { id: 'HideGuide', enabled: 'none' },
                    { id: 'ShowGuide', enabled: 'none' }
                ],
                watermark: {
                    position: "br" //br (default), bl, tr, tl
                },
                cividisColors: {
                    blue100: "#00204d",
                    blue90: "#00306f",
                    blue75: "#38486b",
                    brown50: "#a39a76",
                    yellow25: "#e4cf5b",
                    yellow5: "#f9e04a"
                }, 
            },
            currentGene: null,
            hardCodedItems: [{name: "CRLF2", value: "CRLF2"},
                {name: "BCL2", value: "BCL2"},
                {name: "BCL6", value: "BCL6"},
                {name: "MYC", value: "MYC"},
            ],
            items: [],
            search: null,
            showOtherPlots: false,
            loading: false,
            currentOncotreeCode: null

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
        createPlotTitle(code) {
            return "FPKM from " + this.getTissueFromOncotreeCode(code) +" tissue<br/> on gene " + this.getCurrentGeneName();
        },
        updateFPKMPlot() {
            if (!this.currentGene) {
                return;
            }
            this.loading = true;
            axios.get(webAppRoot + "/getFPKMChartData", {
                params: {
                    caseId: this.$route.params.id,
                    geneParam: this.currentGene.value.trim(),
                    showOtherPlots: this.showOtherPlots
                }
            })
                .then(response => {
                    if (response.data.isAllowed) {
                        var chartData = response.data;
                        this.currentOncotreeCode = chartData.oncotreeCode;
                        if (this.fpkmPlotDataConfig) {
                            this.refreshPlot(chartData);
                        }
                        else {
                            this.fpkmPlotDataConfig = {
                                gui: this.gui,
                                graphset: [
                                    {
                                        "type":"mixed",
                                        "title":{
                                        //   "text":"FPKM for cases diagnosed with " + chartData.oncotreeCode +"<br/> on gene " + this.getCurrentGeneName()
                                        //   + "<br/>(Demo only, not real data)",
                                        "text": this.createPlotTitle(chartData.oncotreeCode)
                                        },
                                        plotarea: {
                                            marginTop: "100px",
                                            // backgroundImage: webAppRoot + "/resources/images/draft-watermark.png",
                                            // backgroundFit: "xy"
                                        }, 
                                        "scale-x": {
                                          "values": "-0.5:0.5:1",
                                          "visible": false,
                                        },
                                        "scale-y": {
                                            maxValue: chartData.maxValue,
                                            minValue: 0,
                                            progression: "log",
                                            'log-base': 2,
                                            zooming: true,
                                        },
                                        legend: {
                                            offsetY: "90px"
                                        },
                                        scrollY: {

                                        },
                                        "series":this.createAllSeries(chartData)
                                    }
                                ]
                            };
                            this.$nextTick(() => {
                                zingchart.render({
                                    id: "fpkmPlot",
                                    data: this.fpkmPlotDataConfig,
                                    height: "60%",
                                    // output: 'canvas'
                                });
    
                            });
                        }
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
        createAllSeries(chartData) {
            var series = [];
            var scatter = this.createScatterSeries(chartData);
            var outlier = this.createOutliersSeries(chartData);
            var currentCase = this.createCurrentCaseSeries(chartData);
            var boxplot = this.createBoxPlotSeries(chartData);
            if (scatter) {
                series.push(scatter);
            }
            if (outlier) {
                series.push(outlier);
            }
            if (boxplot) {
                series.push(boxplot[0]);
                series.push(boxplot[1]);
            }
            if (currentCase) {
                series.push(currentCase);
            }
            return series;
        },
        createScatterSeries(chartData) {
            if (chartData.scatterSerie) {
                return {
                    type: "scatter",
                    "data-labels": chartData.scatterSerie["data-labels"],
                    "marker": {
                        "background-color": this.gui.cividisColors.brown50
                    },
                    values: chartData.scatterSerie.values,
                      "tooltip": {
                        "text": "%data-labels: %v",
                        "background-color": this.gui.cividisColors.brown50,
                        alpha: 0.75
                    }
                  };
            }
            return null;
        },
        createOutliersSeries(chartData) {
            if (chartData.outliersSerie) {
                return {
                    type: "scatter",
                    text: chartData.outliersSerie.text,
                    "data-labels": chartData.outliersSerie["data-labels"],
                    "marker": {
                        "background-color": this.gui.cividisColors.brown50
                    },
                    values: chartData.outliersSerie.values,
                      "tooltip": {
                        "text": "%data-labels: %v",
                        "background-color": this.gui.cividisColors.brown50,
                        alpha: 0.75
                    },
                    "legend-marker": {
                        "background-color": this.gui.cividisColors.brown50,
                        type: "circle",
                        size: 5
                    }
                  };
            }
            return null;
        },
        createCurrentCaseSeries(chartData) {
            if (chartData.currentCaseSerie) {
                return {
                    type: "scatter",
                    text: chartData.currentCaseSerie.text,
                    "data-labels": chartData.currentCaseSerie["data-labels"],
                    "marker": {
                        "background-color": this.gui.cividisColors.blue100
                    },
                    values: chartData.currentCaseSerie.values,
                    "tooltip": {
                        "text": "%data-labels: %v",
                        "background-color": this.gui.cividisColors.blue100,
                        alpha: 0.75
                    },
                    "legend-marker": {
                        "background-color": this.gui.cividisColors.blue100,
                        type: "circle",
                        size: 5
                    }
                }
            } 
              return null;
        },
        createBoxPlotSeries(chartData) {
            var series = [];
            if (chartData.stockSerie) {
                series.push({
                type: "stock",
                text: chartData.stockSerie.text,
                backgroundColor: "none",
                "line-color": this.gui.cividisColors.brown50,
                "border-color": this.gui.cividisColors.brown50,
                "border-width": 2,
                "line-width": 2,
                "values":[
                  //[Q1, Max, Min, Q3]
                  [0,chartData.stockSerie.values]
                ],
                "tooltip": {
                  "text": chartData.boxPlotTooltip,
                  "background-color": this.gui.cividisColors.brown50,
                  alpha: 0.75
                },
                "legend-marker": {
                    visible: false
                },
                "legend-item": {
                    visible: false
                }
              });
            }
            if (chartData.medianSerie) {
                series.push({
                //median
                type: "line",
                text: chartData.medianSerie.text,
                marker: {
                    "visible": false
                },
                values: chartData.medianSerie.values,
                "line-color": this.gui.cividisColors.brown50,
                "line-width": 2,
                //hardcoded tooltip. Need to create it on back-end
                "tooltip": { 
                    "text": chartData.boxPlotTooltip,
                    "background-color": this.gui.cividisColors.brown50,
                    alpha: 0.75
                },
                "legend-marker": {
                    visible: false
                },
                "legend-item": {
                    visible: false
                }
                });
            }
            return (series.length > 0) ? series : null;
        },
        refreshPlot(chartData) {
            zingchart.exec('fpkmPlot', 'setseriesdata', {
                data : this.createAllSeries(chartData)
            });
            zingchart.exec('fpkmPlot', 'modify', {
                data: {
                    title: {
                        "text": this.createPlotTitle(chartData.oncotreeCode)
                    },
                    scaleY: {
                        minValue: 0,
                        maxValue: chartData.maxValue,
                        progression: "log",
                        'log-base': 2,
                        zooming: true,
                    }
                }
            });
        },
        searchGene(value) {
            if (!value || value.length < 3) {
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
        }
    },
    computed: {
        webAppRoot() {
            return webAppRoot;
        }
    },
    created() {
        this.populateItems();
    },
    watch: {
        search(value) {
            this.searchGene(value);
        } 
    }


});