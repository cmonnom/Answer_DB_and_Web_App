Vue.component('sandbox-cnv-plot', {
    props: {
        noEdit: { default: true, type: Boolean },
        variantDataTables: { default: () => [], type: Array },
        linkTable: { default: () => [], type: Array },
        widthClass: { default: "", type: String },
        color: { default: "primary", type: String },
        variantType: { default: "cnv", type: String },
        cnvPlotId: { default: "cnvPlot", type: String },
        type: { default: "cnv", type: String },
        loadingVariant: { default: false, type: Boolean },
    },
    template: `<v-card>

    <v-dialog ref="chartHelpDialog" v-model="chartHelpVisible" hide-overlay max-width="600px" persistent
        content-class="top-right-dialog">
        <v-card class="subheading">
            <v-toolbar dense dark color="warning">
                <v-toolbar-title>
                    CNV Chart Help
                </v-toolbar-title>
                <v-spacer></v-spacer>
                <v-btn icon @click="chartHelpVisible = false">
                    <v-icon>close</v-icon>
                </v-btn>

            </v-toolbar>
            <v-card-text class="pl-2 pr-2 pt-2 pb-2">
                <span>
                    - Darker points are a likely copy number change (copy number equals 2)<br />
                    &nbsp;&nbsp;or represent selected genes<br />
                    - Click and Drag the mouse to zoom in.<br />
                    - Right-Click on the chart to display more actions<br />
                    - Click on the legend to show/hide series<br />
                    - Mouse over a data point to get more information (tooltip)<br />
                    - You can highlight specific genes by clicking on the list in the CNV Variant Details panel.</span>
                <img width="100%" :src="getCNVPlotImage()"></img>
            </v-card-text>
        </v-card>
    </v-dialog>

    <v-toolbar class="elevation-0" dense dark :color="loadingVariant ? loadingVariantColor : color">
        <v-toolbar-title class="ml-0">
            CNV Plot {{ caseId }}
        </v-toolbar-title>

    </v-toolbar>
    <v-container grid-list-md fluid pl-2 pr-2 pt-2 pb-2>
        <v-layout row wrap>
            <v-flex xs12 md12 lg12 xl12 v-if="variantType == 'cnv'">
                <v-layout row wrap>
                    <v-flex xs12 md12 lg6>
                        <v-layout align-center>
                            <v-flex xs class="pt-2 mt-1">
                                <span class="subheading">Load CNV Plot: </span>
                            </v-flex>

                            <v-flex xs2 pt-3>
                                <v-tooltip right>
                                    <v-select slot="activator" :items="cnvChromList" v-model="selectedCNVChrom"
                                        :disabled="cnvPlotLoadingOtherChrom" :loading="cnvPlotLoadingOtherChrom"
                                        label="Other CHR" single-line hide-details item-text="name" clearable
                                        item-value="value" class="no-height-select" @input="otherCNVChromChanged()">
                                    </v-select>
                                    <span>Display Another Chromosome</span>
                                </v-tooltip>
                            </v-flex>

                            <v-flex xs>
                                <v-tooltip bottom>
                                    <v-btn slot="activator" @click="handleCNVAllChromPlot()"
                                        :loading="cnvPlotLoadingAllChrom" :disabled="cnvPlotLoadingAllChrom">All</v-btn>
                                    <span>Plot all chromosomes (slower)</span>
                                </v-tooltip>
                                <v-tooltip top content-clas="subheading">
                                    <v-btn slot="activator" @click="chartHelpVisible = true" flat icon>
                                        <v-icon color="primary">help</v-icon>
                                    </v-btn>
                                    <span>
                                        Display Help for the CNV Chart
                                    </span>
                                </v-tooltip>
                            </v-flex>

                        </v-layout>
                    </v-flex>
                    <v-flex xs12 md12 lg12>
                        <v-layout row wrap v-if="visibleGenesCN2OrSelected || visibleGenesOther"
                            class="subheading elevation-1 mb-4">
                            <v-flex xs12 pl-2><b>Genes visible at this zoom level ({{ currentPlotTitle }}):</b>
                            </v-flex>

                            <v-flex xs12 md12 lg12 pl-3 class="cnv-list-height">
                                <span v-text="genesVisibleTopLabel"></span>
                                <span class="blue-grey--text text--lighten-1">{{ visibleGenesCN2OrSelected }}</span>
                                <br />
                                <span v-text="genesVisibleBottomLabel"></span>
                                <span class="blue-grey--text text--lighten-1">{{ visibleGenesOther }}</span>
                            </v-flex>
                            <v-flex xs12>
                                <div :style="cnvPlotDataConfig ? fullSizeChart : ''">
                                    <div :id="cnvPlotId" style="height: 100%"></div>
                                </div>
                            </v-flex>
                        </v-layout>
                    </v-flex>
                </v-layout>


            </v-flex>
        </v-layout>
    </v-container>
</v-card>`,
    data() {
        return {
            caseId: "",
            savingVariantDetails: false,
            variantDetailsUnSaved: false,
            cnvPlotDataConfig: null,
            gui: {
                behaviors: [
                    { id: 'DownloadSVG', enabled: 'none' },
                    { id: 'ViewSource', enabled: 'none' },
                    { id: 'HideGuide', enabled: 'none' },
                    { id: 'ShowGuide', enabled: 'none' }
                ],
                watermark: {
                    position: "tr" //br (default), bl, tr, tl
                }
            },
            cnvPlotLoadingCurrentChrom: false,
            cnvPlotLoadingAllChrom: false,
            cnvPlotLoadingOtherChrom: false,
            cividisColors: {
                blue100: "#00204d",
                blue90: "#00306f",
                blue75: "#38486b",
                brown50: "#a39a76",
                yellow25: "#e4cf5b",
                yellow5: "#f7edb2"
            },
            genesSelected: [],
            cnvPlotNeedsReload: false,
            toggleAllLoading: false,
            selectedCNVChrom: null,
            visibleGenesCN2OrSelected: "",
            visibleGenesOther: "",
            currentListOfVisibleGenes: [],
            createCNVDisabled: false,
            genesVisibleTopLabel: "CN=2: ",
            genesVisibleBottomLabel: "Others: ",
            chartHelpVisible: false,
            loadingVariantColor: "blue-grey lighten-4",
            loadingVariantTextColor: "blue-grey--text text--lighten-4",
            currentPlotTitle: "",
            plotlyConfig: {
                displayModeBar: true,
                displaylogo: false,
                modeBarButtonsToRemove: ['sendDataToCloud', 'editInChartStudio', 'select2d',
                    'lasso2d', 'hoverClosestCartesian', 'hoverCompareCartesian',
                    'toggleSpikelines', 'autoScale2d'],
                responsive: true,
                toImageButtonOptions: {
                    format: 'png', // one of png, svg, jpeg, webp
                    filename: 'cnv_plot',
                    height: 600,
                    width: 1600,
                    scale: 1 // Multiply title/legend/axis/canvas sizes by this factor
                  }
            },
            cnvData: {},
            lastKMin: null,
            lastKMax: null,
            showingGeneLabels: false,
            maxGenesForLabels: 150,
            cnvChromList: []
        }

    },
    methods: {
        revertVariant() {
            this.$emit("revert-variant", this);
        },
        saveVariant() {
            this.$emit("save-variant", false);
        },
        showPanel() {
            this.$emit("show-panel", this);
        },
        hidePanel() {
            this.$emit("hide-panel", this);
            if (document.getElementById(this.cnvPlotId)) {
                Plotly.purge(this.cnvPlotId);
            }
            this.resetCNVChart();
        },
        resetCNVChart() {
            //remove lists of visible genes
            this.visibleGenesCN2OrSelected = "";
            this.visibleGenesOther = "";
            this.cnvData = null;
            this.showingGeneLabels = false;
        },
        togglePanel() {
            this.$emit("toggle-panel", this);
        },
        openLookupLink(geneName) {
            this.$emit("open-lookup-link", geneName);
        },
        createLinkTooltip(id) {
            if (id.type == "various") {
                if (id.value.indexOf('rs') == 0) {
                    return "Open in dbSNP";
                }
                else if (id.value.indexOf('COSM') == 0 || id.value.indexOf('COSN') == 0) {
                    return "Open in COSMIC";
                }
                else if (id.value.indexOf('COSV') == 0) {
                    return "COSMIC hasn't implemented referencing COSV ids yet. Please use the COSM or COSN id";
                }
                else if (!isNaN(id.value)) { //Clinvar
                    return "Open in ClinVar";
                }
            }
            else if (id.type == "oncoKB") {
                return "Open in OncoKB";
            }
        },
        //determines if the regular variant details label should be used
        //like Gene, Notation Nb.Cases Seen etc.
        //so that it behaves like a regular "label: string" combo
        isRegularVariantDetailsLabel(type) {
            return !type || type == 'chip' || type == 'callSet' || type == 'flag' || type == 'link' || type == 'array' || type == 'menu-link';
        },
        getTableFlexClass(name) {
            if (this.variantType == "cnv") {
                return "md6";
            }
            if (this.variantType == "snp") {
                if (name == "infoTable") {
                    return ['xs4', 'md4', "pt-2", 'mt-1'];
                }
                if (name == "depthTable") {
                    return ['xs4', 'md3', "pt-2", 'mt-1'];
                }
                if (name == "dataTable") {
                    return ['xs4', 'md5'];
                }
            }
            if (this.isTranslocation()) {
                return ['xs4'];
            }
            return [this.widthClass];
        },
        openUrl(item) {
            window.open(item.url, "_blank");
        },
        // handleVariantDetailsChanged() {
        //     this.$emit("variant-details-changed");
        // }
        variantDetailsChanged() {
            this.variantDetailsUnSaved = true;
            // this.$emit("variant-details-changed");
        },
        updateGenesSelected() {
            this.genesSelected.length = 0;
        },
        createScatterTrace(data, color, symbol, size, isBottomPlot) {
            return {
                x: data.x,
                y: data.y,
                yaxis: isBottomPlot ? "y2" : "y",
                name: data.name,
                mode: "markers",
                type: "scattergl",
                marker: {
                    color: color,
                    opacity: symbol ? 1 : 0.5,
                    size: size ? size: 4,
                    symbol: symbol ? symbol : "circle"
                },
                text: data.labels,
                hovertemplate: "%{text}"
            }
        },
        getCNVPlotImage() {
            return webAppRoot + '/resources/images/screenshots/cnv-plot-details.png';
        },
        createAChromGeneTrace(x, y, color, opacity, isbottomSubPlot) {
            return {
                x: x,
                y: y,
                yaxis: isbottomSubPlot ? "y2" : "y",
                fill: 'tozeroy',
                connectgap: false,
                hoverinfo: 'skip',
                name: "",
                mode: "lines",
                type: "scattergl",
                line: {
                    color: color, //'#f7edb2',
                    width: 0
                },
                opacity: opacity,
                showlegend: false,
            }
        },
        createChromGeneTraces(start, end, color, opacity, minY, maxY, isbottomSubPlot) {
            let x = [];
            let yTop = [];
            let yBottom = [];
            for (let i = 0; i < start.length; i++) {
                if (i % 2 == 0) {
                    x.push(start[i]);
                    yTop.push(maxY);
                    yBottom.push(minY);

                    x.push(end[i]);
                    yTop.push(maxY);
                    yBottom.push(minY);

                    x.push(null);
                    yTop.push(null);
                    yBottom.push(null);
                }
                else {
                    x.push(end[i - 1]);
                    yTop.push(0);
                    yBottom.push(0);

                    x.push(start[i + 1]);
                    yTop.push(0);
                    yBottom.push(0);
                }

            }
            if (start.length == 1 && this.selectedCNVChrom) {
                var chrNumber = Number.parseInt(this.selectedCNVChrom.substring(3, this.selectedCNVChrom.length)) - 1;
                if (chrNumber % 2 != 0)  {
                    color = "white";
                }
            }
            var chrTraceTop = this.createAChromGeneTrace(x, yTop, color, opacity, isbottomSubPlot);
            var chrTraceBottom = this.createAChromGeneTrace(x, yBottom, color, opacity, isbottomSubPlot);
            return [chrTraceTop, chrTraceBottom];
        },
        updateCNVPlot(chrom) {
            this.visibleGenesCN2OrSelected = "Counting genes...";
            this.visibleGenesOther = "Counting genes...";
            this.currentListOfVisibleGenes = [];
            this.createCNVDisabled = true;
            this.showingGeneLabels = false;
            axios.get(webAppRoot + "/getCNVChartDataStatic", {
                params: {
                    chrom: chrom,
                }
            })
                .then(response => {
                    if (response.data.isAllowed) {
                        var outlierSymbol = "triangle-up";
                        var outlierSize = 8;
                        this.caseId = response.data.caseId;
                        var cnr2Trace = this.createScatterTrace(response.data.cnr2, "#00204d");
                        var cnrOthersTrace = this.createScatterTrace(response.data.cnrOthers, "#a39a76");
                        var cnr2OutliersTrace = this.createScatterTrace(response.data.cnr2Outliers, "#00204d", outlierSymbol, outlierSize);
                        var cnrOthersOutliersTrace = this.createScatterTrace(response.data.cnrOtherOutliers, "#a39a76", outlierSymbol, outlierSize);
                        var showVAF = response.data.bAlleles.x.length > 0 ;
                        var bAllelesTrace = showVAF ? this.createScatterTrace(response.data.bAlleles, "#00204d", null, null, true) : null;
                        var geneSelectedTraces = [];
                        for (var i = 0; i < response.data.genesSelected.length; i++) {
                            var trace = this.createScatterTrace(response.data.genesSelected[i], "#00204d");
                            geneSelectedTraces.push(trace);
                        }
                        var geneSelectedOutliersTraces = [];
                        for (var i = 0; i < response.data.genesSelectedOutliers.length; i++) {
                            var trace = this.createScatterTrace(response.data.genesSelected[i], "#00204d", outlierSymbol, outlierSize);
                            geneSelectedOutliersTraces.push(trace);
                        }

                        var cnsTraces = [];
                        for (var i = 0; i < response.data.cns.y.length; i++) {
                            var cnsTrace = {
                                x: response.data.cns.x[i],
                                y: response.data.cns.y[i],
                                hoveron: 'points+fills',
                                name: response.data.cnsTitles[i],
                                mode: "lines",
                                type: "scattergl",
                                text: [response.data.cns.labels[i], response.data.cns.labels[i]],
                                hovertemplate: "%{text}",
                                line: {
                                    color: '#f9e04a',
                                    width: 4
                                },
                                showlegend: false,
                            }
                            cnsTraces.push(cnsTrace);
                        }
                        
                        var chrGenesTraces = [];
                        //odd and even gene traces to have 2 shades of colors alternating    
                        var genesStartEven = [];
                        var genesStartOdd = [];
                        var genesEndEven = [];
                        var genesEndOdd = [];
                        for (var i = 0 ; i < response.data.genes.start.length; i++) {
                            if (i % 2 == 0) {
                                genesStartEven.push(response.data.genes.start[i]);
                                genesEndEven.push(response.data.genes.end[i]);
                            }
                            else {
                                genesStartOdd.push(response.data.genes.start[i]);
                                genesEndOdd.push(response.data.genes.end[i]);
                            }
                        }
                        var minY = -5.2;
                        var maxY = 5.2;
                        var chrTraceTop = this.createChromGeneTraces(
                            response.data.chr.start, response.data.chr.end, '#f9e04a', 0.4, minY, maxY, false);
                        var chrTraceBottom = this.createChromGeneTraces(
                            response.data.chr.start, response.data.chr.end, '#f9e04a', 0.4, 0, 1, true);
                        chrGenesTraces.push(...chrTraceTop);
                        chrGenesTraces.push(...this.createChromGeneTraces(
                            genesStartEven, genesEndEven, this.cividisColors.blue75, 0.25, minY, maxY));
                        chrGenesTraces.push(...this.createChromGeneTraces(
                            genesStartEven.slice(1), genesEndEven.slice(1), this.cividisColors.blue75, 0.25, minY, maxY)); 

                        chrGenesTraces.push(...this.createChromGeneTraces(
                            genesStartOdd, genesEndOdd, this.cividisColors.blue75, 0.4, minY, maxY));
                        chrGenesTraces.push(...this.createChromGeneTraces(
                            genesStartOdd.slice(1), genesEndOdd.slice(1), this.cividisColors.blue75, 0.1, minY, maxY));

                        var chrLabelsAnnotations = [];
                        for (let i = 0; i < response.data.chr.labels.length; i++) {
                            let annotation = {
                                showarrow: false,
                                text: response.data.chr.labels[i],
                                align: "right",
                                x: response.data.chr.start[i],
                                xanchor: "left",
                                y: -4.8,
                                yanchor: "bottom",
                                textangle: this.cnvPlotLoadingAllChrom ? 270 : 0
                            }
                            chrLabelsAnnotations.push(annotation);
                        }
                        
                        //experimental. don't implement for now
                        var geneLabelsAnnotations = [];
                        /**
                        for (let i = 0; i < response.data.genes.start.length; i++) {
                            let annotation = {
                                showarrow: false,
                                text: response.data.geneLabels[i],
                                align: "left",
                                x: response.data.genes.start[i],
                                xanchor: "left",
                                y: ((i % 8) * 0.22 ) + 3,
                                yanchor: "top",
                            }
                            geneLabelsAnnotations.push(annotation);
                        }
                         */

                        data = [...chrGenesTraces, cnr2Trace, cnrOthersTrace, cnr2OutliersTrace, cnrOthersOutliersTrace, ...geneSelectedTraces, ...cnsTraces];
                        if (showVAF) {
                            data.push(...chrTraceBottom);
                            data.push(bAllelesTrace);
                        }
                        this.cnvData = {
                            "cnr2": cnr2Trace,
                            "cnrOthers": cnrOthersTrace,
                            "cns": cnsTraces,
                            "genesSelected": geneSelectedTraces,
                            "geneAnnotations": geneLabelsAnnotations,
                            "chrAnnotations": chrLabelsAnnotations
                        };

                        var topSubPlotAxes = [];
                        for (var i = 0; i < data.length - 1; i++) {
                            topSubPlotAxes.push("xy");
                        }

                        var layout = {
                            title: this.createCnvPlotTitle(),
                            yaxis: {
                                title: "Copy Ratio (log2)",
                                zeroline: false,
                                range: [-5.2, 5.2],
                                fixedrange: true,
                                dtick: 1,
                                domain: [showVAF ? 0.3 : 0, 1]
                                
                            },
                            yaxis2: {
                                title: "VAF",
                                zeroline: false,
                                domain: [0,0.3],
                                fixedrange: true,
                                range: [-0.1,1.1]
                            },
                            xaxis: {
                                zeroline: false,
                                showticklabels: false,
                                showgrid: false,
                                range: [0, response.data.chr.end[response.data.chr.end.length - 1]],
                            },
                            margin: {
                                l: 40,
                                r: 20,
                                b: 20,
                                t: 30,
                                pad: 4
                            },
                            height: showVAF? 700 : 400,
                            hovermode: 'closest',
                            // shapes: chrs,
                            annotations: chrLabelsAnnotations,
                            legend: {
                                itemsizing: "constant"
                            }
                        }


                        this.$nextTick(() => {
                            Plotly.newPlot(this.cnvPlotId, data, layout, this.plotlyConfig);
                            document.getElementById(this.cnvPlotId).on('plotly_relayout', (eventdata) => {
                                this.handleChartZoom({
                                    kmin: eventdata["xaxis.range[0]"],
                                    kmax: eventdata["xaxis.range[1]"]
                                });
                            });
                            this.cnvPlotLoadingCurrentChrom = false;
                            this.cnvPlotLoadingAllChrom = false;
                            this.cnvPlotLoadingOtherChrom = false;
                            this.cnvPlotNeedsReload = false;
                            this.toggleAllLoading = false;
                            var kmax = response.data.chr.end[response.data.chr.end.length - 1];
                            if (this.genesSelected.length > 0) {
                                this.genesVisibleTopLabel = "Gene" + (this.genesSelected.length == 1 ? "" : "s") + " selected:";
                                this.genesVisibleBottomLabel = "Others: ";
                            }
                            else {
                                this.genesVisibleTopLabel = "CN=2: ";
                                this.genesVisibleBottomLabel = "Others: ";
                            }
                            this.handleChartZoom({ kmin: 0, kmax });
                        });
                    }
                    else {
                        this.handleDialogs(response.data, this.updateCNVPlot.bind(null, chrom));
                        this.cnvPlotLoadingCurrentChrom = false;
                        this.cnvPlotLoadingAllChrom = false;
                        this.cnvPlotLoadingOtherChrom = false;
                        this.toggleAllLoading = false;
                    }
                })
                .catch(error => {
                    this.loading = false;
                    console.log(error);
                    this.cnvPlotLoadingCurentChrom = false;
                    this.cnvPlotLoadingAllChrom = false;
                    this.cnvPlotLoadingOtherChrom = false;
                    this.toggleAllLoading = false;
                });
        },
        handleChartZoom(p) {
            this.currentListOfVisibleGenes = [];
            var kmin = p.kmin;
            var kmax = p.kmax;
            if (!kmin && !kmax) {
                kmin = this.lastKMin;
                kmax = this.lastKMax;
            }
            else {
                this.lastKMin = kmin;
                this.lastKMax = kmax;
            }
            var seriesCN2OrSelected = [];
            var labelsCN2OrSelected = [];
            var seriesOther = [];
            var labelsOther = [];
            if (this.genesSelected.length > 0 && this.cnvData["genesSelected"].length > 0) {
                for (var i = 0; i < this.genesSelected.length; i++) {
                    seriesCN2OrSelected = this.cnvData["genesSelected"][i].x;
                    labelsCN2OrSelected = this.cnvData["genesSelected"][i].text;
                }
            }
            else {
                seriesCN2OrSelected = this.cnvData["cnr2"].x;
                labelsCN2OrSelected = this.cnvData["cnr2"].text;
            }
            seriesOther = this.cnvData["cnrOthers"].x;
            labelsOther = this.cnvData["cnrOthers"].text;
            var visibleGenesCN2OrSelected = this.getUniqueLabelsFromSeries(kmin, kmax, seriesCN2OrSelected, labelsCN2OrSelected);
            var visibleGenesOther = this.getUniqueLabelsFromSeries(kmin, kmax, seriesOther, labelsOther);
            let genesSet = new Set();
            for (var i = 0; i < visibleGenesCN2OrSelected.length; i++) {
                genesSet.add(visibleGenesCN2OrSelected[i]);
            }
            for (var i = 0; i < visibleGenesOther.length; i++) {
                genesSet.add(visibleGenesOther[i]);
            }
            this.currentListOfVisibleGenes = [...genesSet];
            this.createCNVDisabled = visibleGenesCN2OrSelected.length == 0 && visibleGenesOther.length == 0;
            this.visibleGenesCN2OrSelected = this.getVisibleGeneStringFromUniqueList(visibleGenesCN2OrSelected);
            this.visibleGenesOther = this.getVisibleGeneStringFromUniqueList(visibleGenesOther);
        },
        toggleGeneLabels() {
            var update = {};
            if (!this.showingGeneLabels && this.currentListOfVisibleGenes.length < this.maxGenesForLabels) {
                var geneLabels = this.cnvData.geneAnnotations.filter(i => i.x >= this.lastKMin && i.x <= this.lastKMax);
                update = {
                    annotations: this.cnvData.chrAnnotations.concat(geneLabels)
                }
            }
            else {
                update = {
                    annotations: this.cnvData.chrAnnotations
                }
            }
            this.showingGeneLabels = !this.showingGeneLabels;
            Plotly.relayout(this.cnvPlotId, update);

        },
        getUniqueLabelsFromSeries(kmin, kmax, series, labels) {
            var visibleGenes = [];
            for (var i = 0; i < series.length; i++) {
                var value = series[i];
                if (value >= kmin && value <= kmax) {
                    var regex = /(Gene: )(.*)( Log2: [0-9.-]*)/g;
                    var match = regex.exec(labels[i]);
                    if (match && match[2]) {
                        visibleGenes.push(match[2]);
                    }
                }
            }
            var tempSet = new Set();
            for (var i = 0; i < visibleGenes.length; i++) {
                tempSet.add(visibleGenes[i]);
            }
            visibleGenes = tempSet.size != 0 ? Array.from(tempSet) : [];
            return visibleGenes.sort();
        },
        getVisibleGeneStringFromUniqueList(visibleGenes) {
            if (visibleGenes.length == 0) {
                return "No genes visible for that category";
            }
            else if (visibleGenes.length >= 100) {
                return visibleGenes.length + " gene" + (visibleGenes.length == 1 ? "" : "s") + " visible in that category";
            }
            else {
                return visibleGenes.join(", ") + " (" + visibleGenes.length + " gene" + (visibleGenes.length == 1 ? ")" : "s)");
            }
        },
        cnvPlotReady() {
            return !this.cnvPlotLoadingCurentChrom && !this.cnvPlotLoadingAllChrom && !this.cnvPlotDataConfig && !this.cnvPlotLoadingOtherChrom;
        },
        getMarkerColor(index, size, cnsData) {
            if (size == 1 && cnsData[0]) {
                var length = cnsData[0].name.length;
                var chrNumber = Number.parseInt(cnsData[0].name.substring(length - 1, length)) - 1;
                return chrNumber % 2 == 0 ? this.cividisColors.yellow5 : ""; //same color as all chrom plot
            }
            return index % 2 == 0 ? this.cividisColors.yellow5 : "";
        },
        getLegend() {
            return {
                "margin-top": "8%",
                "overflow": "scroll",
                "max-items": 15,
                layout: "15x1",
                adjustLayout: true,
                highlightPlot: true,

            }
        },
        createCnvPlotTitle() {
            var title = 'CNV Plot for ';
            if (this.cnvPlotLoadingAllChrom) {
                title += 'all Chromosomes (CN=2 VS Others)';
            }
            else if (this.genesSelected.length > 0) {
                title += formatChrom(this.selectedCNVChrom) + " (selected genes in dark blue)";
            }
            else {
                var chrName = this.selectedCNVChrom;
                if (this.selectedCNVChrom) {
                    chrName = this.selectedCNVChrom;
                }
                title += formatChrom(chrName) + " (CN=2 VS Others)";
            }
            this.currentPlotTitle = title;
            return title;
        },
        handleGeneSelectionChanged() {
            this.cnvPlotNeedsReload = true;
        },
        handleCNVCurrentChromPlot() {
            // this.currentCNVPlotType = "CURRENT";
            this.cnvPlotLoadingCurrentChrom = true;
            this.selectedCNVChrom = null;
            this.updateCNVPlot();
        },
        handleCNVAllChromPlot() {
            // this.currentCNVPlotType = "ALL";
            this.cnvPlotLoadingAllChrom = true;
            this.selectedCNVChrom = null;
            this.updateCNVPlot();
        },
        formatChrom(chrom) { //needed to call the global function from v-text
            return formatChrom(chrom);
        },
        getVariantTypeTitle() {
            if (this.isSNP()) {
                return "SNP";
            }
            else if (this.isCNV()) {
                return "CNV";
            }
            else if (this.isTranslocation()) {
                return "FTL";
            }
            else if (this.isVirus()) {
                return "VIR";
            }
        },
        isSNP() {
            return this.type == "snp";
        },
        isCNV() {
            return this.type == "cnv";
        },
        isTranslocation() {
            return this.type == "translocation";
        },
        isTranslocation() {
            return this.type == "virus" || this.type == "VIR";
        },
        otherCNVChromChanged() {
            if (this.selectedCNVChrom) {
                this.currentCNVPlotType = "OTHER";
                this.cnvPlotLoadingOtherChrom = true;
                this.updateCNVPlot(this.selectedCNVChrom);
            }
        },
        getCNVChromList() {
            axios.get(
                webAppRoot + "/getCNVChromListStatic",
                {
                    params: {
                    }
                }).then(response => {
                    if (response.data.isAllowed) {
                        this.cnvChromList = response.data.items;

                    } else {
                        this.handleDialogs(response.data, this.getCNVChromList);
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                });
        },
        handleDialogs(response, callback) {
            if (response == "not-allowed") {
                bus.$emit("not-allowed", [this.response]);
            }
            if (response.isXss) {
                bus.$emit("xss-error", [null, response.reason]);
            }
            else if (response.isLogin) {
                bus.$emit("login-needed", [null, callback])
            }
            else if (response.success === false) {
                bus.$emit("some-error", [null, response.message]);
            }
            // this.splashProgress = 100; //should dismiss the splash dialog
            // this.waitingForAjaxMessage = "There were some errors while saving";
            // this.waitingForAjaxActive = false; //stops spinning wheel if error
        },
        handleAxiosError(error) {
            console.log(error);
            bus.$emit("some-error", [null, error]);
        },
    },
    mounted: function () {
        this.getCNVChromList();
    },
    created: function () {

    },
    beforeDestroy() {
        if (document.getElementById(this.cnvPlotId)) {
            Plotly.purge(this.cnvPlotId);
        }
    },
    destroyed: function () {

    },
    computed: {
        fullSizeChart: function () {
            return {
                position: "relative",
                height: "900px"
            }
        },
    },
    watch: {
    }


});