Vue.component('variant-details', {
    props: {
        noEdit: { default: true, type: Boolean },
        variantDataTables: { default: [], type: Array },
        linkTable: { default: () => [], type: Array },
        widthClass: { default: "", type: String },
        currentVariant: { default: () => { }, type: Object },
        color: { default: "primary", type: String },
        variantType: { default: "snp", type: String },
        cnvPlotId: { default: "cnvPlot", type: String },
        type: { default: "snp", type: String },
        cnvChromList: { default: () => [], type: Array },
        loadingVariant: { default: false, type: Boolean },
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
    - You can highlight specific genes by clicking on the list in the CNV Variant Details panel.</span>
    <img width="100%" :src="getCNVPlotImage()"></img>
    </v-card-text>
    </v-card>
    </v-dialog>

  <v-toolbar class="elevation-0" dense dark :color="loadingVariant ? loadingVariantColor : color">
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
  <v-container grid-list-md fluid pl-2 pr-2 pt-2 pb-2>
      <v-layout row wrap>
          <v-flex :class="getTableFlexClass(table.name)" v-for="table in variantDataTables" :key="table.name">
              <v-card flat >
                  <v-card-text>
                      <v-list class="dense-tiles">
                          <v-list-tile v-for="item in table.items" :key="item.label" class="pl-0 pr-0 pb-2 no-tile-padding">
                              <v-list-tile-content class="pl-0 pr-0">
                                  <v-layout class="full-width">
                                      <v-flex xs12 :class="[item.type == 'link' ? 'pb-0' : '', 'text-xs-left', 'grow']">
                                          <span v-if="isRegularVariantDetailsLabel(item.type)" :class="[loadingVariant ? loadingVariantTextColor : '','selectable']">{{ item.label }}:</span>
                                          <span v-if="!item.type || item.type == 'link'" v-html="item.value" :class="['selectable text-xs-right grow blue-grey--text', loadingVariant ? 'text--lighten-4': 'text--lighten-1']"></span>
                                          <v-tooltip bottom>
                                          <v-btn slot="activator" v-if="item.type == 'chip'" flat icon color="primary" @click="toggleAllGenes" :loading="toggleAllLoading">
                                          <v-icon>done_all</v-icon>
                                          </v-btn>
                                          <span>Toggle all genes for CNV Plot</span>
                                          </v-tooltip>

                                          <!-- Notation (label + textfield + link)  or Fusion Name / Left Gene / Right Gene
                                          TODO textfield disabled for now until Ben is ready on the backend -->
                                          <v-layout row wrap v-if="item.type == 'notation' || item.type == 'textfield'">
                                            <v-flex xs  class="pt-2 pl-0 mt-1">
                                            <span :class="[loadingVariant ? loadingVariantTextColor : '','selectable']">{{ item.label }}:</span>
                                            </v-flex>
                                            <v-flex xs6 > 
                                            <v-text-field hide-details :class="['no-height-select', 'pb-1']"
                                            @change="checkForErrors(item)"
                                            :error="item.isValid === false"
                                            v-model="currentVariant[item.fieldName]"
                                            :value="currentVariant[item.fieldName]"
                                            :disabled="noEdit"
                                             @input="variantDetailsChanged">
                                            </v-text-field>
                                            </v-flex>
                                            <v-flex xs v-if="item.url"> 
                                            <v-tooltip bottom>
                                                <v-btn slot="activator" color="primary" icon flat @click="openUrl(item)" class="mt-0 mb-0">
                                                    <v-icon>{{ item.linkIcon }}</v-icon>
                                                </v-btn>
                                                <span>{{ item.tooltip }}</span>
                                            </v-tooltip>  
                                            </v-flex>
                                            <v-flex xs v-if="item.isValid === false" align-self-center> 
                                            <v-tooltip bottom>
                                                <v-icon color="error" slot="activator">mdi-cancel</v-icon>
                                                <span>{{ item.tooltipInvalid }}</span>
                                            </v-tooltip>  
                                            </v-flex>

                                          </v-layout>
                                          
                                          <span v-if="item.type == 'chip'">
                                          <span class="subheading warning--text" v-show="cnvPlotNeedsReload" >Click on <span v-text="getChrButtonName()"></span> to refresh the CNV Plot.</span>
                                                <v-btn-toggle v-model="genesSelected" multiple class="elevation-0" @change="handleGeneSelectionChanged">
                                                <v-layout row wrap>
                                                    <v-flex pl-0 pr-0 pt-0 pb-0 v-for="(chip, index) in item.value" :key="index">
                                                         <v-btn flat class="selectable-pointer">
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
                                          <v-menu v-if="item.type == 'menu-link'" offset-x>
                                            <v-tooltip slot="activator" bottom>
                                            <v-btn slot="activator" color="primary" icon flat class="mt-0 mb-0">
                                            <v-icon>{{ item.linkIcon }}</v-icon>
                                            </v-btn>
                                            <span>Click to select a gene</span>
                                            </v-tooltip>
                                            <v-card>
                                                <v-card-text>
                                                <v-layout row wrap>
                                                <v-flex pl-0 pr-0 pt-0 pb-0 v-for="chip in item.items" :key="chip.name">
                                                     <v-btn flat class="pl-0 pr-0 ml-0 mb-0 mr-0 mt-0 selectable-pointer" @click="openLookupLink(chip.name)">
                                                    {{ chip.name }}
                                                    </v-btn>
                                                </v-flex>
                                                </v-layout>
                                                </v-card-text>
                                            </v-card>
                                          </v-menu>
                                          <v-data-table v-if="item.type == 'callSet'" :items="item.value" hide-actions hide-headers >
                                              <template slot="items" slot-scope="props">
                                                  <td class="normal-word-break">
                                                      {{ props.item.label }}
                                                  </td>
                                                  <td v-for="i in item.columns" :key="i" class="normal-word-break">{{ props.item["caller" + (i - 1)] }}</td>
                                              </template>
                                          </v-data-table>
                                          <v-layout v-if="item.type == 'select'" class="full-width">
                                          <v-flex :class="[loadingVariant ? loadingVariantTextColor : '','selectable', 'pt-2', 'pl-0', 'mt-1']">{{ item.label }}:</v-flex>
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
                                              <v-chip v-if="icon.chip" slot="activator" :color="icon.color" text-color="white" label small disabled style="vertical-align: bottom">
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
                                        <v-flex xs12 pl-2><b>Genes visible at this zoom level ({{ currentPlotTitle }}):</b>
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
                                              <span class="blue-grey--text text--lighten-1">{{ visibleGenesCN2OrSelected }}</span>
                                             <!-- experimental code. do not implement
                                              <v-tooltip bottom>
                                              <v-btn icon flat slot="activator" @click="toggleGeneLabels" class="mt-0 mb-0 ml-0 mr-0"
                                              :disabled="currentListOfVisibleGenes.length >= maxGenesForLabels">
                                              <v-icon color="primary" v-if="showingGeneLabels">mdi-eye-off</v-icon>
                                              <v-icon color="primary" v-else>mdi-eye</v-icon>
                                              </v-btn>
                                              <span>Show/Hide Chromosome labels
                                              <br/>(if less than {{ maxGenesForLabels }} genes)</span>
                                              </v-tooltip>
                                             --> 
                                              <br/>
                                              <span v-text="genesVisibleBottomLabel"></span>
                                              <span class="blue-grey--text text--lighten-1">{{ visibleGenesOther }}</span>
                                        </v-flex>

                                        <v-flex xs6 lg4 pl-2>
                                        <v-autocomplete multiple chips deletable-chips hide-details v-bind:items="currentListOfVisibleGenes" clearable v-model="searchForGene"
                                        label="Find any genes in this plot" @input="highlightDots"
                                        auto clearable :disabled="createCNVDisabled"></v-autocomplete>
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
      <v-layout row wrap>
          <v-flex xs12 v-for="table in linkTable" :key="table.name">
              <v-card flat>
                  <v-card-text>
                      <v-list class="dense-tiles">
                          <v-list-tile v-for="item in table.items" :key="item.label">
                              <v-list-tile-content class="pb-2">
                                  <v-layout class="full-width">
                                      <v-flex xs12 class="text-xs-left grow">
                                          <span :class="[loadingVariant ? loadingVariantTextColor : '','selectable']">{{ item.label }}:</span>
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
            fieldsWithGeneRules: ["leftGene", "rightGene"],
            searchForGene: null
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
                else if (id.value.indexOf('COSV') == 0) {
                    return;
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
                    size: size ? size : 4,
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
                if (chrNumber % 2 != 0) {
                    color = "white";
                }
            }
            var chrTraceTop = this.createAChromGeneTrace(x, yTop, color, opacity, isbottomSubPlot);
            var chrTraceBottom = this.createAChromGeneTrace(x, yBottom, color, opacity, isbottomSubPlot);
            return [chrTraceTop, chrTraceBottom];
        },
        updateCNVPlot(chrom) {
            var genesParam = [];
            this.visibleGenesCN2OrSelected = "Counting genes...";
            this.visibleGenesOther = "Counting genes...";
            this.currentListOfVisibleGenes = [];
            this.createCNVDisabled = true;
            this.showingGeneLabels = false;
            for (var i = 0; i < this.genesSelected.length; i++) {
                if (this.currentVariant.geneChips[this.genesSelected[i]]) {
                    genesParam.push(this.currentVariant.geneChips[this.genesSelected[i]].name);
                }
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
                        var outlierSymbol = "triangle-up";
                        var outlierSize = 8;
                        var cnr2Trace = this.createScatterTrace(response.data.cnr2, "#00204d");
                        var cnrOthersTrace = this.createScatterTrace(response.data.cnrOthers, "#a39a76");
                        var cnr2OutliersTrace = this.createScatterTrace(response.data.cnr2Outliers, "#00204d", outlierSymbol, outlierSize);
                        var cnrOthersOutliersTrace = this.createScatterTrace(response.data.cnrOtherOutliers, "#a39a76", outlierSymbol, outlierSize);
                        var showVAF = response.data.bAlleles.x.length > 0;
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
                        for (var i = 0; i < response.data.genes.start.length; i++) {
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
                            "chrAnnotations": chrLabelsAnnotations,
                            "cnr2Outliers": cnr2OutliersTrace,
                            "cnrOthersOutliers": cnrOthersOutliersTrace
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
                                domain: [0, 0.3],
                                fixedrange: true,
                                range: [-0.1, 1.1]
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
                            height: showVAF ? 700 : 400,
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
                            if (this.searchForGene) {
                                this.highlightDots();
                            }
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
        /**
         * Makes the highlighted dots "flash" in a regular interval
         */
        highlightDots() {
            this.clearHighlight();
            var newData = {x: [], y:[], labels:[]};
            var traces = [this.cnvData.cnr2, this.cnvData.cnrOthers, this.cnvData.cnr2Outliers, this.cnvData.cnrOthersOutliers];
            for (var j = 0; j < traces.length; j++) {
                for (var i = 0; i < traces[j].text.length; i++) {
                    var text = traces[j].text[i];
                    for (var k = 0; k < this.searchForGene.length; k++) {
                        if (text.indexOf(" " + this.searchForGene[k] + " ") > -1) {
                            newData.x.push(traces[j].x[i]);
                            newData.y.push(traces[j].y[i]);
                            newData.labels.push(text);
                        }
                    }
                }
            }
            var newTrace = this.createScatterTrace(newData, "#00204d");
            newTrace.marker.size = 10;
            newTrace.marker.color = "red";
            newTrace.name = this.searchForGene.join(" ");
            newTrace.canDelete = true;
            this.doHighlightDots(newTrace);
            
        },
        doHighlightDots(newTrace) {
            Plotly.addTraces(this.cnvPlotId, newTrace);
        },
        clearHighlight() {
            var indicesToDelete = [];
            var graph = document.getElementById(this.cnvPlotId);
            for (var i = 0; i < graph.data.length; i++) {
                if (graph.data[i].canDelete) {
                    indicesToDelete.push(i);
                }
            }
            Plotly.deleteTraces(this.cnvPlotId, indicesToDelete);
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
                title += formatChrom(this.currentVariant.chrom) + " (selected genes in dark blue)";
            }
            else {
                var chrName = this.currentVariant.chrom;
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
        toggleAllGenes() {
            this.toggleAllLoading = true;
            for (var i = 0; i < this.variantDataTables.length; i++) {
                var items = this.variantDataTables[i].items;
                for (var j = 0; j < items.length; j++) {
                    var item = items[j];
                    if (item.type == "chip") {
                        this.genesSelected.length = 0;
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
        verifyGeneInput(input) {
            return this.verifyGeneNames(input).then(success => {
                console.log(success);
            }
            );
        },
        checkForErrors(item) {
            if (this.fieldsWithGeneRules.indexOf(item.fieldName) > -1) {
                this.verifyGeneNamesAPI(this.currentVariant[item.fieldName]).then(response => {
                    item.isValid = response.success;
                });
            }
        },
        verifyGeneNamesAPI(input) {
            return new Promise((resolve, reject) => {
                if (input == "Intergenic") {
                    resolve({
                        success: true
                    });
                }
                else {
                    axios({
                        method: 'post',
                        url: "https://rest.genenames.org/search/symbol/" + input,
                        headers: {
                            'Content-Type': 'application/json; charset=utf-8'
                        },
                        params: {
                        },
                    })
                        .then(response => {
                            resolve({
                                success: response.data.response.numFound > 0
                            })
                        })
                        .catch(error => {
                            console.log(error);
                            bus.$emit("some-error", [this, error]);
                        });

                }

            })
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
                height: "900px"
                // height: window.innerHeight - 120 + "px"
            }
        },
        // genesSelected: {
        //     get: function() {
        //         return this.getGenesSelected();
        //     },
        //     set: function(newValue) {
        //        this.genesSelectedPerVariantId[this.currentVariant._id.$oid] = newValue;
        //     }
        // }
    },
    watch: {
        //   variantDetailsUnSaved: this.handleVariantDetailsChanged()
        currentVariant: "updateGenesSelected"
    }


});