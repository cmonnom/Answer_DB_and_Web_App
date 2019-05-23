Vue.component('variant-details', {
    props: {
        noEdit: { default: true, type: Boolean },
        variantDataTables: { default: [], type: Array },
        linkTable: { default: () => [], type: Array },
        widthClass: { default: "", type: String },
        currentVariant: { default:() =>  {}, type: Object },
        color: { default: "primary", type: String },
        variantType: { default: "snp", type: String },
        cnvPlotId: { default: "cnvPlot", type: String },
        type: { default: "snp", type: String },
        cnvChromList: { default: () => [], type: Array}
    },
    template: ` <v-card>

    <v-dialog ref="chartHelpDialog" v-model="chartHelpVisible" hide-overlay max-width="600px" persistent content-class="top-right-dialog">
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
    <span >
    - Darker points are a likely copy number change (copy number equals 2)<br/>
    &nbsp;&nbsp;or represent selected genes<br/>
    - Click and Drag the mouse to zoom in.<br/>
    - Right-Click on the chart to display more actions<br/>
    - Click on the legend to show/hide series<br/>
    - Mouse over a data point to get more information (tooltip)<br/>
    - The chart with ALL chromosomes doesn't have tooltips for faster loading.<br/>
    - You can highlight specific genes by clicking on the list in the CNV Variant Details panel.</span>
    </v-card-text>
    </v-card>
    </v-dialog>

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
      <span v-text="getVariantTypeTitle()"></span>
          Variant Details
      </v-toolbar-title>

      <v-spacer></v-spacer>
      <v-tooltip bottom>
          <v-btn icon @click="hidePanel()" slot="activator">
              <v-icon>close</v-icon>
          </v-btn>
          <span>Close Details</span>
      </v-tooltip>

  </v-toolbar>
  <v-container grid-list-md fluid>
      <v-layout row wrap>
          <v-flex :class="getTableFlexClass(table.name)" v-for="table in variantDataTables" :key="table.name">
              <v-card flat>
                  <v-card-text>
                      <v-list class="dense-tiles">
                          <v-list-tile v-for="item in table.items" :key="item.label">
                              <v-list-tile-content class="pb-2">
                                  <v-layout class="full-width">
                                      <v-flex xs12 :class="[item.type == 'link' ? 'pb-0' : '', 'text-xs-left', 'grow']">
                                          <span v-if="isRegularVariantDetailsLabel(item.type)" class="selectable">{{ item.label }}:</span>
                                          <span v-if="!item.type || item.type == 'link'" v-html="item.value" class="selectable text-xs-right grow blue-grey--text text--lighten-1"></span>
                                          <v-tooltip bottom>
                                          <v-btn slot="activator" v-if="item.type == 'chip'" flat icon color="primary" @click="toggleAllGenes" :loading="toggleAllLoading">
                                          <v-icon>done_all</v-icon>
                                          </v-btn>
                                          <span>Toggle all genes for CNV Plot</span>
                                          </v-tooltip>
                                          <!-- Notation (label + textfield + link) -->
                                          <v-layout row wrap v-if="item.type == 'notation'">
                                            <v-flex xs class="pl-0 pt-2"> 
                                            <span class="selectable">{{ item.label }}:</span>
                                            </v-flex>
                                            <v-flex xs6 > 
                                            <v-text-field hide-details class="no-height-select"
                                            v-model="currentVariant[item.fieldName]"
                                            :value="currentVariant[item.fieldName]"
                                            :disabled="noEdit"
                                             @input="variantDetailsChanged">
                                            </v-text-field>
                                            </v-flex>
                                            <v-flex xs> 
                                            <v-tooltip bottom>
                                                <v-btn slot="activator" color="primary" icon flat @click="openUrl(item)" class="mt-0 mb-0">
                                                    <v-icon>{{ item.linkIcon }}</v-icon>
                                                </v-btn>
                                                <span>{{ item.tooltip }}</span>
                                            </v-tooltip>  
                                            </v-flex>
                                          </v-layout>
                                          <span v-if="item.type == 'chip'">
                                          <span class="subheading warning--text" v-show="cnvPlotNeedsReload" >Click on <span v-text="getChrButtonName()"></span> to refresh the CNV Plot.</span>
                                                <v-btn-toggle v-model="genesSelected" multiple class="elevation-0" @change="handleGeneSelectionChanged">
                                                <v-layout row wrap>
                                                    <v-flex pl-0 pr-0 pt-0 pb-0 v-for="chip in item.value" :key="chip.name">
                                                        <v-btn flat class="selectable">
                                                        {{ chip.name }}
                                                        </v-btn>
                                                    </v-flex>
                                                </v-layout>
                                             
                                              </v-btn-toggle>
                                              
                                          </span>
                                          <v-tooltip bottom v-if="item.type == 'link'">
                                              <v-btn slot="activator" color="primary" icon flat @click="openUrl(item)" class="mt-0 mb-0">
                                                <v-icon>{{ item.linkIcon }}</v-icon>
                                              </v-btn>
                                              <span>{{ item.tooltip }}</span>
                                          </v-tooltip>  
                                          <span v-if="item.type == 'array'" class="selectable text-xs-right grow blue-grey--text text--lighten-1">
                                            <span v-for="v in item.value" :key="v">{{ v }}<br/></span>
                                          </span>  
                                          <v-data-table v-if="item.type == 'callSet'" :items="item.value" hide-actions hide-headers >
                                              <template slot="items" slot-scope="props">
                                                  <td class="normal-word-break">
                                                      {{ props.item.label }}
                                                  </td>
                                                  <td v-for="i in item.columns" :key="i" class="normal-word-break">{{ props.item["caller" + (i - 1)] }}</td>
                                              </template>
                                          </v-data-table>
                                          <v-layout v-if="item.type == 'select'" class="full-width">
                                          <v-flex class="selectable pt-2 pl-0">{{ item.label }}:</v-flex>
                                          <v-flex class="max300 xs4" >
                                            <v-tooltip right>
                                            <v-select slot="activator" clearable :value="currentVariant[item.fieldName]" :items="item.items" v-model="currentVariant[item.fieldName]"
                                                :label="item.tooltip" single-line hide-details
                                                class="no-height-select" @input="variantDetailsChanged"
                                                :disabled="noEdit"></v-select>
                                                <span v-if="item.helpMessage" v-html="item.helpMessage"></span>
                                                <span v-else="item.tooltip" v-html="item.tooltip"></span>
                                            </v-tooltip>
                                          </v-flex>
                                          </v-layout>

                                          <v-tooltip bottom v-for="(icon, index) in item.value" :key="index" v-if="item.type == 'flag'">
                                              <v-chip v-if="icon.chip" slot="activator" :color="icon.color" text-color="white" label small disabled>
                                                  {{ icon.iconName }}
                                              </v-chip>
                                              <v-icon v-else slot="activator" :color="icon.color">
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
                                      <v-flex xs12 md12 lg12 xl12 v-if="variantType == 'cnv'">
                                      <v-layout row wrap>
                                        <v-flex xs12 md12 lg6>
                                        <v-layout align-center>
                                          <v-flex xs class="pt-2 mt-1">
                                          <span class="subheading">Load CNV Plot: </span>
                                          <v-tooltip bottom v-if="currentVariant._id">
                                            <v-btn slot="activator" @click="handleCNVCurrentChromPlot()" :loading="cnvPlotLoadingCurrentChrom" :disabled="cnvPlotLoadingCurrentChrom"
                                            :class="[cnvPlotNeedsReload ? 'amber accent-2' : '']"
                                            ><span v-text="formatChrom(currentVariant.chrom)"></span>
                                            <v-icon right dark>refresh</v-icon>
                                            </v-btn>
                                            <span>Plot only <span v-text="formatChrom(currentVariant.chrom)"></span> (faster)</span>
                                          </v-tooltip>
                                          </v-flex>
  
                                          <v-flex xs2 pt-3>
                                          <v-tooltip right>
                                          <v-select slot="activator" :items="cnvChromList" v-model="selectedCNVChrom" :disabled="cnvPlotLoadingOtherChrom"
                                              :loading="cnvPlotLoadingOtherChrom"
                                              label="Other CHR" single-line hide-details
                                              item-text="name" clearable item-value="value"
                                              class="no-height-select" @input="otherCNVChromChanged()"
                                              ></v-select>
                                              <span>Display Another Chromosome</span>
                                          </v-tooltip>
                                          </v-flex>
  
                                          <v-flex xs>
                                          <v-tooltip bottom>
                                            <v-btn slot="activator" @click="handleCNVAllChromPlot()" :loading="cnvPlotLoadingAllChrom" :disabled="cnvPlotLoadingAllChrom">All</v-btn>
                                            <span>Plot all chromosomes (slower)</span>
                                          </v-tooltip>
                                          <v-tooltip bottom>
                                            <v-btn slot="activator" @click="resetZoom()" :disabled="cnvPlotReady()">
                                            <v-icon>zoom_out</v-icon>
                                            </v-btn>
                                            <span>Reset Zoom Level</span>
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
                                        class="subheading elevation-1 mb-4" >
                                        <v-flex xs12 pl-2><b>Genes visible at this zoom level:</b></v-flex>
                                        <v-flex xs12>
                                              <v-tooltip bottom>
                                              <v-btn slot="activator" @click="openNewCNVForm" 
                                              :loading="toggleAllLoading || cnvPlotLoadingCurrentChrom"
                                              :disabled="createCNVDisabled">
                                                  Create New CNV
                                              </v-btn>
                                              <span v-show="!createCNVDisabled">Create a new CNV from genes visible on the chart</span>
                                              <span v-show="createCNVDisabled">No genes visible at this zoom level</span>
                                              </v-tooltip>
                                        </v-flex>
                                        <v-flex xs12 md12 lg12 pl-3 class="cnv-list-height">
                                              <span v-text="genesVisibleTopLabel"></span>
                                              <span class="blue-grey--text text--lighten-1">{{ visibleGenesCN2OrSelected }}</span><br/>
                                              <span v-text="genesVisibleBottomLabel"></span>
                                              <span class="blue-grey--text text--lighten-1">{{ visibleGenesOther }}</span>
                                        </v-flex>
                                        </v-layout>
                                        </v-flex>
                                      </v-layout>


                                          <div :style="cnvPlotDataConfig ? fullSizeChart : ''">
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
                                              <span>{{ createLinkTooltip(id) }}</span>
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
            cnvPlotLoadingCurrentChrom: false,
            cnvPlotLoadingAllChrom: false,
            cnvPlotLoadingOtherChrom: false,
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
            selectedCNVChrom: null,
            visibleGenesCN2OrSelected: "",
            visibleGenesOther: "",
            currentListOfVisibleGenes: [],
            createCNVDisabled: false,
            genesVisibleTopLabel: "CN=2: ",
            genesVisibleBottomLabel: "Others: ",
            chartHelpVisible: false
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
            zingchart.exec(this.cnvPlotId, 'destroy'); //kill the chart if variant details is closed
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
                    link = "https://cancer.sanger.ac.uk/cosmic/mutation/overview?id=" + id.value.replace("COSM", "");
                }
                else if (id.value.indexOf('COSN') == 0) {
                    link = "https://cancer.sanger.ac.uk/cosmic/ncv/overview?id=" + id.value.replace("COSN", "");
                }
                else if (!isNaN(id.value)) { //Clinvar
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
        createLinkTooltip(id) {
            if (id.type == "various") {
                if (id.value.indexOf('rs') == 0) {
                    return "Open in dbSNP";
                }
                else if (id.value.indexOf('COSM') == 0 || id.value.indexOf('COSN') == 0) {
                    return "Open in COSMIC";
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
            return !type || type == 'chip' || type == 'callSet' || type == 'flag' || type == 'link' || type == 'array';
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
            var genesParam = [];
            this.visibleGenesCN2OrSelected = "Counting genes...";
            this.visibleGenesOther = "Counting genes...";
            this.currentListOfVisibleGenes = [];
            this.createCNVDisabled = true;
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
                        // startTime = new Date();
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
                                        angle: this.cnvPlotLoadingAllChrom ? 270 : 0, //only rotate for ALL chromosomes
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
                                    maxTrackers: response.data.dataPointCount + 100, //need amount bigger than nb of data points
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
                                        text: "%data-labels"
                                        // rules: [
                                        //     {
                                        //         rule: "'%data-labels' != %v",
                                        //         text: "Gene: %data-labels Copy Ratio: %v"

                                        //     },
                                        //     {
                                        //         rule: "'%data-labels' == %v",
                                        //         text: "Copy Ratio: %v %data-labels"

                                        //     }
                                        // ]
                                    },
                                    highlightMarker: {
                                        size: 4,
                                        backgroundColor: this.cividisColors.yellow25
                                    },
                                    highlightState: {
                                        lineWidth: 4,
                                        lineColor: this.cividisColors.blue100
                                    }
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

                            zingchart.zoom = this.handleChartZoom;

                            this.cnvPlotLoadingCurrentChrom = false;
                            this.cnvPlotLoadingAllChrom = false;
                            this.cnvPlotLoadingOtherChrom = false;
                            // var endTime = new Date();
                            // var timeDiff = endTime - startTime;
                            // console.log((timeDiff / 1000) + "s");
                            this.cnvPlotNeedsReload = false;
                            this.toggleAllLoading = false;
                            var kmax = zingchart.exec(this.cnvPlotId, 'getobjectinfo', {
                                object : 'scale',
                                name : 'scale-x'
                            }).maxValue;
                            if (this.genesSelected.length > 0) {
                                this.genesVisibleTopLabel =  "Gene" + (this.genesSelected.length == 1 ? "" : "s") + " selected:";
                                this.genesVisibleBottomLabel =  "Others: ";
                            }
                            else {
                                this.genesVisibleTopLabel =  "CN=2: ";
                                this.genesVisibleBottomLabel =  "Others: ";
                            }
                            this.handleChartZoom({kmin: 0, kmax});
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
                kmin = 0;
                kmax = zingchart.exec(this.cnvPlotId, 'getobjectinfo', {
                    object : 'scale',
                    name : 'scale-x'
                }).maxValue;
            }
            var seriesCN2OrSelected = [];
            var labelsCN2OrSelected = [];
            var seriesOther = [];
            var labelsOther = [];
            if (this.genesSelected.length > 0) {
                for (var i = 1; i <= this.genesSelected.length; i++) {
                    seriesCN2OrSelected = seriesCN2OrSelected.concat(zingchart.exec(this.cnvPlotId, 'getseriesvalues')[i]);
                    labelsCN2OrSelected = labelsCN2OrSelected.concat(zingchart.exec(this.cnvPlotId, 'getseriesdata')[i]["data-labels"]);
                }
                seriesOther = zingchart.exec(this.cnvPlotId, 'getseriesvalues')[0];
                labelsOther = zingchart.exec(this.cnvPlotId, 'getseriesdata')[0]["data-labels"];
            }
            else {
                seriesCN2OrSelected = zingchart.exec(this.cnvPlotId, 'getseriesvalues')[0];
                labelsCN2OrSelected = zingchart.exec(this.cnvPlotId, 'getseriesdata')[0]["data-labels"];
                seriesOther = zingchart.exec(this.cnvPlotId, 'getseriesvalues')[1];
                labelsOther = zingchart.exec(this.cnvPlotId, 'getseriesdata')[1]["data-labels"];
            }
            // var seriesCN2 = zingchart.exec(this.cnvPlotId, 'getseriesvalues')[0];
            // var labelsCN2 = zingchart.exec(this.cnvPlotId, 'getseriesdata')[0]["data-labels"]; //just item 0 for testing for now
            // var seriesOther = zingchart.exec(this.cnvPlotId, 'getseriesvalues')[1];
            // var labelsOther = zingchart.exec(this.cnvPlotId, 'getseriesdata')[1]["data-labels"]; //just item 0 for testing for now
            var visibleGenesCN2OrSelected = this.getUniqueLabelsFromSeries(kmin, kmax, seriesCN2OrSelected, labelsCN2OrSelected);
            var visibleGenesOther = this.getUniqueLabelsFromSeries(kmin, kmax, seriesOther, labelsOther);
            for (var i = 0; i < visibleGenesCN2OrSelected.length; i++) {
                this.currentListOfVisibleGenes.push(visibleGenesCN2OrSelected[i]);
            }
            for (var i = 0; i < visibleGenesOther.length; i++) {
                this.currentListOfVisibleGenes.push(visibleGenesOther[i]);
            }
            this.createCNVDisabled = visibleGenesCN2OrSelected.length == 0 && visibleGenesOther.length == 0;
            this.visibleGenesCN2OrSelected = this.getVisibleGeneStringFromUniqueList(visibleGenesCN2OrSelected);
            this.visibleGenesOther = this.getVisibleGeneStringFromUniqueList(visibleGenesOther);
            
        },
        getUniqueLabelsFromSeries(kmin, kmax, series, labels) {
            var visibleGenes = [];
            for (var i = 0; i < series.length; i++) {
                var value = series[i][0];
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
                "margin-top":"8%",
                "overflow": "scroll",
                "max-items":15,
                layout: "15x1",
                adjustLayout: true,
                highlightPlot: true,
                
            }
        },
        createCnvPlotTitle() {
            var title = 'CNV Plot for ';
            if (this.cnvPlotLoadingAllChrom) {
                title += 'all Chromosomes (CN=<b>2</b> VS Others)';
            }
            else if (this.genesSelected.length > 0) {
                title += formatChrom(this.currentVariant.chrom) + " (selected genes in dark)";
            }
            else {
                var chrName = this.currentVariant.chrom;
                if (this.selectedCNVChrom) {
                    chrName = this.selectedCNVChrom;
                }
                title += formatChrom(chrName) + " (CN=2 VS Others)";
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
            this.cnvPlotLoadingCurrentChrom = true;
            this.updateCNVPlot(this.currentVariant.chrom);
        },
        handleCNVCurrentChromPlot() {
            // this.currentCNVPlotType = "CURRENT";
            this.cnvPlotLoadingCurrentChrom = true;
            this.selectedCNVChrom = null;
            this.updateCNVPlot(this.currentVariant.chrom);
        },
        handleCNVAllChromPlot() {
            // this.currentCNVPlotType = "ALL";
            this.cnvPlotLoadingAllChrom = true;
            this.selectedCNVChrom = null;
            this.updateCNVPlot();
        },
        getChrButtonName() {
            return formatChrom(this.currentVariant.chrom);
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
        otherCNVChromChanged() {
            if (this.selectedCNVChrom) {
                this.currentCNVPlotType = "OTHER";
                this.cnvPlotLoadingOtherChrom = true;
                this.updateCNVPlot(this.selectedCNVChrom);
            }
        },
        openNewCNVForm() {
            bus.$emit("create-new-cnv", this.currentListOfVisibleGenes);
        },
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