Vue.component('variant-details', {
    props: {
        noEdit: { default: true, type: Boolean },
        variantDataTables: { default: [], type: Array },
        linkTable: { default: [], type: Array },
        widthClass: { default: "", type: String },
        currentVariant: { default: {}, type: Object },
        color: { default: "primary", type: String },
        variantType: { default: "snp", type: String },
        cnvPlotId: { default: "cnvPlot", type: String }
    },
    template: ` <v-card>
  <v-toolbar class="elevation-0" dense dark :color="color">
      <v-menu offset-y offset-x class="ml-0">
          <v-btn slot="activator" flat icon dark>
              <v-icon color="amber accent-2">zoom_in</v-icon>
          </v-btn>
          <v-list>
              <v-list-tile v-if="!noEdit" avatar @click="saveVariant()" :disabled="noEdit">
                  <v-list-tile-avatar>
                      <v-icon>save</v-icon>
                  </v-list-tile-avatar>
                  <v-list-tile-content>
                      <v-list-tile-title>Save Variant Details</v-list-tile-title>
                  </v-list-tile-content>
              </v-list-tile>

              <v-list-tile v-if="!noEdit" avatar @click="revertVariant()">
                  <v-list-tile-avatar>
                      <v-icon>settings_backup_restore</v-icon>
                  </v-list-tile-avatar>
                  <v-list-tile-content>
                      <v-list-tile-title>Restore From Last Saved</v-list-tile-title>
                  </v-list-tile-content>
              </v-list-tile>

              <v-list-tile avatar @click="hidePanel()">
                  <v-list-tile-avatar>
                      <v-icon>cancel</v-icon>
                  </v-list-tile-avatar>
                  <v-list-tile-content>
                      <v-list-tile-title>Close Details</v-list-tile-title>
                  </v-list-tile-content>
              </v-list-tile>
          </v-list>
      </v-menu>
      <v-toolbar-title class="ml-0">
          Variant Details
      </v-toolbar-title>

      <v-spacer></v-spacer>
      <v-badge color="red" v-if="!noEdit" right bottom overlap v-model="variantDetailsUnSaved" class="mini-badge">
          <v-icon slot="badge"></v-icon>
          <v-tooltip bottom>
              <v-btn flat icon @click="saveVariant()" slot="activator" :loading="savingVariantDetails" :disabled="noEdit">
                  <v-icon>save</v-icon>
              </v-btn>
              <span>Save Variant Details</span>
          </v-tooltip>
      </v-badge>
      <v-tooltip bottom>
          <v-btn flat icon v-if="!noEdit" @click="revertVariant()" slot="activator">
              <v-icon>settings_backup_restore</v-icon>
          </v-btn>
          <span>Restore Last Saved Variant Details</span>
      </v-tooltip>
      <v-tooltip bottom>
          <v-btn icon @click="hidePanel()" slot="activator">
              <v-icon>close</v-icon>
          </v-btn>
          <span>Close Details</span>
      </v-tooltip>

  </v-toolbar>
  <v-container grid-list-md fluid>
      <v-layout row wrap>
          <v-flex :class="[variantType == 'cnv' ? 'md7' : '', widthClass]" v-for="table in variantDataTables" :key="table.name">
              <v-card flat>
                  <v-card-text>
                      <v-list class="dense-tiles">
                          <v-list-tile v-for="item in table.items" :key="item.label">
                              <v-list-tile-content class="pb-2">
                                  <v-layout class="full-width">
                                      <v-flex xs12 :class="[item.type == 'link' ? 'pb-0' : '', 'text-xs-left', 'grow']">
                                          <span v-if="isRegularVariantDetailsLabel(item.type)" class="selectable]">{{ item.label }}:</span>
                                          <span v-if="!item.type || item.type == 'link'" v-html="item.value" class="selectable text-xs-right grow blue-grey--text text--lighten-1"></span>
                                          <v-tooltip bottom>
                                          <v-btn slot="activator" v-if="item.type == 'chip'" flat icon color="primary" @click="toggleAllGenes" :loading="toggleAllLoading">
                                          <v-icon>done_all</v-icon>
                                          </v-btn>
                                          <span>Toggle all genes for CNV Plot</span>
                                          </v-tooltip>
                                          <span v-if="item.type == 'chip'">
                                          <span class="subheading warning--text" v-show="cnvPlotNeedsReload" >Click on <span v-text="getChrButtonName()"></span> to refresh the CNV Plot.</span>
                                                <v-btn-toggle v-model="genesSelected" multiple class="elevation-0" @change="handleGeneSelectionChanged">
                                                <v-layout row wrap>
                                                    <v-flex pl-0 pr-0 v-for="chip in item.value" :key="chip.name">
                                                        <v-btn flat class="selectable">
                                                        {{ chip.name }}
                                                        </v-btn>
                                                    </v-flex>
                                                </v-layout>
                                             
                                              </v-btn-toggle>
                                              
                                          </span>
                                          <v-tooltip bottom v-if="item.type == 'link'">
                                              <v-btn slot="activator" color="primary" icon flat @click="openUrl(item)" class="mt-0 mb-0">
                                                <v-icon>open_in_new</v-icon>
                                              </v-btn>
                                              <span>{{ item.tooltip }}</span>
                                          </v-tooltip>    
                                          <v-data-table v-if="item.type == 'callSet'" :items="item.value" hide-actions hide-headers>
                                              <template slot="items" slot-scope="props">
                                                  <td>
                                                      {{ props.item.label }}
                                                  </td>
                                                  <td v-for="i in item.columns" :key="i">{{ props.item["caller" + (i - 1)] }}</td>
                                              </template>
                                          </v-data-table>
                                          <v-layout v-if="item.type == 'select'" class="full-width">
                                          <v-flex class="selectable pt-2 pl-0">{{ item.label }}:</v-flex>
                                          <v-flex xs6 class="max300" >
                                          <v-tooltip right>
                                                  <v-select slot="activator" clearable :value="currentVariant[item.fieldName]" :items="item.items" v-model="currentVariant[item.fieldName]"
                                                      :label="item.tooltip" single-line hide-details
                                                      class="no-height-select" @input="variantDetailsChanged"
                                                      :disabled="noEdit"></v-select>
                                                      <span v-html="item.helpMessage"></span>
                                                      </v-tooltip>
                                              </v-flex>
                                          </v-layout>

                                          <v-tooltip bottom v-for="(icon, index) in item.value" :key="index" v-if="item.type == 'flag'">
                                              <v-chip v-if="icon.chip" slot="activator" :color="icon.color" text-color="white" label small disabled>
                                                  {{ icon.iconName }}
                                              </v-chip>
                                              <v-icon v-if="!icon.chip" slot="activator" :color="icon.color">
                                                  {{ icon.iconName }}
                                              </v-icon>
                                              <span> {{ icon.tooltip }}</span>
                                          </v-tooltip>

                                      </v-flex>
                                      </v-layout>
                                      </v-list-tile-content>
                                      </v-list-tile>
                                      </v-list>
                                      </v-card-text>
                                      </v-card>
                                      </v-flex>
                                      <v-flex xs5 v-if="variantType == 'cnv'">
                                      <span class="subheading">Load CNV Plot: </span>
                                      <v-tooltip bottom>
                                        <v-btn slot="activator" @click="updateCNVPlot(currentVariant.chrom)" :loading="cnvPlotLoadingChrom" :disabled="cnvPlotLoading"
                                        :class="[cnvPlotNeedsReload ? 'amber--text accent-2' : '']"
                                        ><span v-text="formatChrom(currentVariant.chrom)"></span></v-btn>
                                        <span>Plot only <span v-text="formatChrom(currentVariant.chrom)"></span> (faster)</span>
                                      </v-tooltip>
                                      <v-tooltip bottom>
                                        <v-btn slot="activator" @click="updateCNVPlot()" :loading="cnvPlotLoading" :disabled="cnvPlotLoadingChrom">All</v-btn>
                                        <span>Plot all chromosomes (slower)</span>
                                      </v-tooltip>
                                      <v-tooltip bottom>
                                        <v-btn slot="activator" @click="resetZoom()" :disabled="cnvPlotReady()">
                                        <v-icon>zoom_out</v-icon>
                                        </v-btn>
                                        <span>Reset Zoom Level</span>
                                        </v-tooltip>
                                        <v-tooltip top content-clas="subheading">
                                            <v-icon slot="activator" color="primary">help</v-icon>
                                        <span >
                                        - Darker points are a likely copy number change (copy number equals 2)<br/>
                                        &nbsp;&nbsp;or represent selected genes<br/>
                                        - Click and Drag the mouse to zoom in.<br/>
                                        - Right-Click on the chart to display more actions<br/>
                                        - Click on the legend to show/hide series<br/>
                                        - Mouse over a data point to get more information (tooltip)<br/>
                                        - The chart with ALL chromosomes doesn't have tooltips for faster loading.</span>
                                        </v-tooltip>  
                                      <div :style="fullSizeChart">
                                      <div :id="cnvPlotId" style="height: 100%"></div>
                                        </div>
                                      </v-flex>
      </v-layout>
      <v-layout row wrap>
          <v-flex xs12 v-for="table in linkTable" :key="table.name">
              <v-card flat>
                  <v-card-text>
                      <v-list class="dense-tiles">
                          <v-list-tile v-for="item in table.items" :key="item.label">
                              <v-list-tile-content class="pb-2">
                                  <v-layout class="full-width">
                                      <v-flex xs12 class="text-xs-left grow">
                                          <span class="selectable">{{ item.label }}:</span>
                                          <v-tooltip v-if="item.links && id.value !== null" bottom v-for="(id, index) in item.ids" :key="index">
                                              <v-btn @click="handleIdLink(id)" slot="activator" v-html="id.label">
                                              </v-btn>
                                              <span>Open in new tab</span>
                                          </v-tooltip>
                                      </v-flex>
                                  </v-layout>
                              </v-list-tile-content>
                          </v-list-tile>
                      </v-list>
                  </v-card-text>
              </v-card>
          </v-flex>
      </v-layout>
  </v-container>
</v-card>`,
    data() {
        return {
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
            cnvPlotLoading: false,
            cnvPlotLoadingChrom: false,
            cividisColors: {
                blue100: "#00204d",
                blue90: "#00306f",
                blue75: "#38486b",
                brown50: "#a39a76",
                yellow25: "#e4cf5b",
                yellow5: "#f9e04a"
            },
            genesSelected: [],
            cnvPlotNeedsReload: false,
            toggleAllLoading: false,
        }

    },
    methods: {
        revertVariant() {
            this.$emit("revert-variant", this);
        },
        saveVariant() {
            this.$emit("save-variant", this);
        },
        showPanel() {
            this.$emit("show-panel", this);
        },
        hidePanel() {
            this.$emit("hide-panel", this);
        },
        togglePanel() {
            this.$emit("toggle-panel", this);
        },
        handleIdLink(id) {
            var link = "";
            if (id.type == "various") {
                if (id.value.indexOf('rs') == 0) {
                    link = "https://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=" + id.value;
                }
                else if (id.value.indexOf('COSM') == 0) {
                    link = "https://cancer.sanger.ac.uk/cosmic/ncv/overview?id=" + id.value.replace("COSM", "");
                }
                else if (id.value.indexOf('COSN') == 0) {
                    link = "https://cancer.sanger.ac.uk/cosmic/ncv/overview?id=" + id.value.replace("COSN", "");
                }
                else { //Clinvar
                    link = "https://www.ncbi.nlm.nih.gov/clinvar/variation/" + id.value;
                }
            }
            else if (id.type == "oncoKB") {
                if (id.subtype == "gene") {
                    link = "http://oncokb.org/#/gene/" + this.currentVariant.oncokbGeneName;
                }
                else if (id.subtype == "variant") {
                    link = "http://oncokb.org/#/gene/" + this.currentVariant.oncokbGeneName + "/variant/" + this.currentVariant.oncokbVariantName;
                }
            }
            window.open(link, "_blank");
        },
        //determines if the regular variant details label should be used
        //like Gene, Notation Nb.Cases Seen etc.
        //so that it behaves like a regular "label: string" combo
        isRegularVariantDetailsLabel(type) {
            return !type || type == 'chip' || type == 'callSet' || type == 'flag' || type == 'link';
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
        applySeriesStyle(series) {
            series.forEach((serie, index) => {
                if (serie.type == "scatter") {
                    serie.marker = { backgroundColor: serie.color }
                    serie.hoverMarker = {
                        size: 4,
                        backgroundColor: this.cividisColors.blue90,
                        alpha: 1
                    }
                }
                else if (serie.type == "line") {
                    // serie.marker = { backgroundColor: this.cividisColors.blue90, lineColor: this.cividisColors.blue90 }
                    serie.marker = { backgroundColor: serie.color, lineColor: serie.color }
                    serie.lineColor = serie.color;
                    serie.hoverMarker = {
                        size: 6,
                        backgroundColor: this.cividisColors.blue90,
                        alpha: 1
                    }
                    // if (index != series.length - 1) {
                    //     serie.legendItem = {
                    //         visible:false // turn off legend item
                    //     }
                    // }
                }
            });
        },
        updateCNVPlot(chrom) {
            if (chrom) {
                this.cnvPlotLoadingChrom = true;
                this.cnvPlotLoading = false;
            }
            else {
                this.cnvPlotLoadingChrom = false;
                this.cnvPlotLoading = true;
            }
            var genesParam = [];
            for (var i = 0; i < this.genesSelected.length; i++) {
                genesParam.push(this.currentVariant.geneChips[this.genesSelected[i]].name);
            }
            axios.get(webAppRoot + "/getCNVChartData", {
                params: {
                    caseId: this.$route.params.id,
                    chrom: chrom,
                    genesParam: genesParam.join(",")

                }
            })
                .then(response => {
                    if (response.data.isAllowed) {
                        startTime = new Date();
                        this.applySeriesStyle(response.data.series);
                        var chrMarkers = [];
                        for (var i = 0; i < response.data.maxChroms.length; i++) {
                            // for (var i = 0; i < 1; i++) {
                            chrMarkers.push(
                                {
                                    type: "area",
                                    range: [i == 0 ? response.data.minChrom : response.data.maxChroms[i - 1], response.data.maxChroms[i]],
                                    backgroundColor: this.getMarkerColor(i, response.data.maxChroms.length),
                                    alpha: 0.2,
                                    label: {
                                        text: response.data.sortedChrs[i],
                                        angle: this.cnvPlotLoadingChrom ? 0 : 270, //only rotate for ALL chromosomes
                                        "offset-x": 0,
                                        "offset-y": -10,
                                        color: "black"
                                    },
                                    valueRange: true
                                });
                        }
                        // console.log(chrMarkers);
                        // console.log(response.data.series.length);
                        this.cnvPlotDataConfig = {
                            gui: this.gui,
                            graphset: [{
                                type: 'mixed',

                                plot: {
                                    mode: "fast",
                                    exact: false,
                                    smartSampling: true,
                                    maxNodes: 0,
                                    maxTrackers: this.cnvPlotLoadingChrom ? response.data.dataPointCount + 100 : 0, //need amount bigger than nb of data points
                                    lineWidth: 4,
                                    shadow: false,
                                    marker: {
                                        borderColor: "none",
                                        type: "circle",
                                        shadow: false,
                                        size: 2,
                                        alpha: 0.5,

                                    },
                                    tooltip: {
                                        visible: true,
                                        rules: [
                                            {
                                                rule: "'%data-labels' != %v",
                                                text: "Gene: %data-labels Copy Ratio: %v"

                                            },
                                            {
                                                rule: "'%data-labels' == %v",
                                                text: "Copy Ratio: %v"

                                            }
                                        ]
                                    },
                                },
                                plotArea: {
                                    adjustLayout: false,
                                    // "margin-left":"0%",
                                    // "margin-right":"0%",
                                    "margin-top": "0%",
                                    "margin-bottom": "0%",
                                },
                                series: response.data.series,
                                //// very slow preview at the moment. Try to fix this
                                // "preview":{

                                title: {
                                    text: this.createCnvPlotTitle(),
                                    fontSize: 12,
                                    adjustLayout: false,
                                    "margin-left": "0%",
                                    "margin-right": "0%",
                                    "margin-top": "0%",
                                    "margin-bottom": "0%",
                                },
                                legend: this.getLegend(),
                                scaleX: {
                                    zooming: true,
                                    // labels: response.data.labels,
                                    // label: {
                                    //     text: "Chromosomes"
                                    // },
                                    // minValue: 0,
                                    // maxValue: 10000,
                                    // step: 10,
                                    maxTicks: 0,
                                    maxItems: 0,
                                    markers: chrMarkers,
                                    tick: {
                                        visible: false
                                    },
                                    item: {
                                        visible: false
                                    }
                                },
                                scrollX: {

                                },
                                scaleY: {
                                    // zooming: true,
                                    label: {
                                        text: "Copy Ratio (log2)"
                                    },
                                    minValue: -5,
                                    maxValue: 5
                                },
                            }]
                        };
                        this.$nextTick(() => {
                            zingchart.render({
                                id: this.cnvPlotId,
                                data: this.cnvPlotDataConfig,
                                height: "90%",
                                output: 'canvas'
                            });
                            this.cnvPlotLoading = false;
                            this.cnvPlotLoadingChrom = false;
                            var endTime = new Date();
                            var timeDiff = endTime - startTime;
                            console.log((timeDiff / 1000) + "s");
                            this.cnvPlotNeedsReload = false;
                            this.toggleAllLoading = false;
                        });
                    }
                    else {
                        this.handleDialogs(response.data, this.updateCNVPlot.bind(null, chrom));
                        this.cnvPlotLoading = false;
                        this.cnvPlotLoadingChrom = false;
                        this.toggleAllLoading = false;
                    }
                })
                .catch(error => {
                    this.loading = false;
                    console.log(error);
                    this.cnvPlotLoading = false;
                    this.cnvPlotLoadingChrom = false;
                    this.toggleAllLoading = false;
                });
        },
        cnvPlotReady() {
            return !this.cnvPlotLoading && !this.cnvPlotLoadingChrom && !this.cnvPlotDataConfig;
        },
        getMarkerColor(index, size) {
            if (size == 1) {
                return ""; //default color because only one
            }
            // return index % 2 == 0 ? "" : "gray";
            return index % 2 == 0 ? this.cividisColors.yellow5 : "";
        },
        resetZoom() {
            zingchart.exec(this.cnvPlotId, 'viewall', {
                graphid: 0
            });
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
                bus.$emit("some-error", [this, response.message]);
            }
        },
        getLegend() {
            return {
                "layout": "1x4", //row x column
                "margin-left": "10%",
                // "margin-top":"8%",
                marginBottom: "0%",
                maxItems: 4,
                "overflow": "page",
                // align: "center",
            }
        },
        createCnvPlotTitle() {
            var title = 'CNV Plot for ';
            if (!this.cnvPlotLoadingChrom) {
                title += 'all Chromosomes (CN=2 VS Others)';
            }
            else if (this.genesSelected.length > 0) {
                title += formatChrom(this.currentVariant.chrom) + " (selected genes in dark)";
            }
            else {
                title += formatChrom(this.currentVariant.chrom) + " (CN=2 VS Others)";
            }
            return title;
        },
        handleGeneSelectionChanged() {
            this.cnvPlotNeedsReload = true;
        },
        toggleAllGenes() {
            this.toggleAllLoading = true;
            for (var i = 0; i < this.variantDataTables.length; i++) {
                var items = this.variantDataTables[i].items;
                for (var j = 0; j < items.length; j++) {
                    var item = items[j];
                    if (item.type == "chip") {
                        this.genesSelected = [];
                        var newSelectedState = !item.value[0].selected;
                        for (var k = 0; k < item.value.length; k++) {
                            item.value[k].selected = newSelectedState;
                            if (newSelectedState) {
                                this.genesSelected.push(k);
                            }
                        }
                    }
                }
            }
            this.updateCNVPlot(this.currentVariant.chrom);
        },
        getChrButtonName() {
            return formatChrom(this.currentVariant.chrom);
        },
        formatChrom(chrom) { //needed to call the global function from v-text
            return formatChrom(chrom);
        }
       
    },
    mounted: function () {

    },
    created: function () {
    },
    destroyed: function () {

    },
    computed: {
        fullSizeChart: function () {
            return {
                position: "relative",
                height: "500px"
                // height: window.innerHeight - 120 + "px"
            }
        }
    },
    watch: {
        //   variantDetailsUnSaved: this.handleVariantDetailsChanged()
    }


});