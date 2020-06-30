Vue.component('lookup-panel', {
    props: {
        title: {default: "Lookup Tool", type: String},
        standalone: {default: true, type:Boolean},
        oncotreeItems: {default: () => [], type:Array},
        originalVariant: {default: "", type:String},
        originalChr: {default: "", type:String},
        originalPos: {default: -1, type:Number},
        originalPosOldBuild: {default: -1, type:Number},
    },
    template: /*html*/`<div ref="lookupPanel">

    <v-card v-if="panelVisible || standalone" :class="standalone ? [] : ['lookup-panel', 'mr-2','ml-2']"
        :height="calcMaxHeight()" :width="calcMaxWidth()" :flat="standalone" :color="standalone ? 'rgba(0,0,0,0)' : ''">
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

                        <v-list-tile avatar @click="relayout()">
                            <v-list-tile-avatar>
                                <v-icon>search</v-icon>
                            </v-list-tile-avatar>
                            <v-list-tile-content>
                                <v-list-tile-title>RElayout</v-list-tile-title>
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
        <v-card flat class="html-scroll-auto lookup-card-height" :flat="standalone"
            :color="standalone ? 'rgba(0,0,0,0)' : ''">
            <v-container grid-list-md fluid pa-1>
                <v-layout row wrap justify-center>
                    <v-flex xs12 class="centered error--text">NOT QUITE PRODUCTION READY</v-flex>
                    <v-tooltip bottom v-for="button in buttons" :key="button.label">
                        <v-btn slot="activator" @click="handleButton(button)"
                            :class="button.label == currentlyActive ? 'amber accent-2': ''">
                            <v-icon left dark>{{ button.icon }}</v-icon>
                            {{ button.label}}
                        </v-btn>
                        <span>{{ button.tooltip }} </span>
                    </v-tooltip>
                    <v-flex xs12 class="centered">
                        <v-icon class="error--text">mdi-alert-circle</v-icon>
                        The data provided here is purely for research.<br />You are responsible for the content of
                        annotations you create.
                    </v-flex>

                </v-layout>
                <v-layout row wrap>
                    <v-flex xs12>
                        <v-layout row wrap>

                            <!-- Gene/Variant/CNV Input -->
                            <v-flex v-if="currentlyActive == 'Gene' || currentlyActive == 'Variant' || currentlyActive == 'CNV'"
                                :class="['pl-2','pr-2', ...getFlexClasses()]">
                                <v-text-field hide-details v-model="currentGene" :value="currentGene"
                                    label="Gene Symbol" placeholder="Eg. BRCA1" :error-messages="geneSymbolErrorMessage"
                                    @input="geneSymbolErrorMessage = null" @keyup.enter="submitForm"
                                    clearable>
                                </v-text-field>
                            </v-flex>

                            <!-- Variant Input -->
                            <v-flex v-if="currentlyActive == 'Variant'" :class="['pl-2','pr-2', ...getFlexClasses()]">
                                <v-text-field hide-details v-model="currentVariant" :value="currentVariant"
                                    label="HGVS Variant:" placeholder="Eg. p.His1791Gln" @keyup.enter="submitForm"
                                    clearable>
                                </v-text-field>
                            </v-flex>

                            <!-- Fusion Input -->
                            <v-flex v-if="currentlyActive == 'Fusion'"
                                :class="['pl-2','pr-2', ...getFlexClasses()]">
                                <v-text-field hide-details v-model="currentFive" :value="currentFive"
                                    label="5' Gene Symbol" placeholder="Eg. EML4" :error-messages="geneFiveErrorMessage"
                                    @input="geneFiveErrorMessage = null" @keyup.enter="submitForm"
                                    clearable>
                                </v-text-field>
                            </v-flex>

                             <!-- Fusion Input -->
                             <v-flex v-if="currentlyActive == 'Fusion'"
                             :class="['pl-2','pr-2', ...getFlexClasses()]">
                             <v-text-field hide-details v-model="currentThree" :value="currentThree"
                                 label="3' Gene Symbol" placeholder="Eg. ALK" :error-messages="geneThreeErrorMessage"
                                 @input="geneThreeErrorMessage = null" @keyup.enter="submitForm"
                                 clearable>
                             </v-text-field>
                         </v-flex>


                            <!-- Cancer Input/CNV -->
                            <v-flex 
                            v-if="currentlyActive == 'Cancer' || currentlyActive == 'Variant' || currentlyActive == 'CNV' || currentlyActive == 'Fusion'"
                                :class="['pl-2','pr-2', ...getCancerFlexClasses()]">
                                <v-autocomplete hide-details value="currentOncotreeCode" v-model="currentOncotreeCode"
                                    :items="oncotreeItems" return-object label="Oncotree Code" placeholder="Eg. AML"
                                    clearable :filter="oncotreeFilter" open-on-clear @change="submitForm">
                                    <template v-slot:selection="data">
                                        <span v-if="data && data.item">{{ data.item.text}}: {{ data.item.label}}</span>
                                    </template>
                                    <template v-slot:item="data">
                                        <span v-if="data && data.item">{{ data.item.text}}: {{ data.item.label}}</span>
                                    </template>
                                </v-autocomplete>
                            </v-flex>

                            <!-- CNV Input -->
                            <v-flex v-if="currentlyActive == 'CNV'" :class="['pl-2','pr-2', ...getFlexClasses()]">
                            <v-radio-group v-model="currentAmpDel" row  @change="submitForm" hide-details>
                            <v-radio label="Amplification" value="amp" color="primary"></v-radio>
                            <v-radio label="Deletion" value="del" color="primary"></v-radio>
                            </v-radio-group>
                            </v-flex>

                        </v-layout>

                        <v-btn v-show="currentlyActive" @click="submitForm"
                            :class="needsReloading(currentlyActive) ? 'amber accent-2' : 'primary'"
                            :disabled="!isFormValid()" :loading="isLoadingOverall()">Submit</v-btn>
                    </v-flex>


                    <!-- Gene Results -->
                    <v-flex xs12 v-show="currentlyActive == 'Gene'">
                        <lookup-panel-gene  ref="genePanel" :standalone="standalone"
                            :current-gene="currentGene" @handleDialogs="handleDialogs"
                            :last-gene="lastGene" :uniq-id="uniqId">
                        </lookup-panel-gene>
                    </v-flex>

                    <!-- Cancer Results -->
                    <v-flex :class="getGeneFlexClasses()" v-show="currentlyActive == 'Cancer'">
                        <v-card>
                            <v-toolbar dense class="elevation-0" dark color="primary">
                                <div class="title ml-0">NCI</div>
                                <v-spacer></v-spacer>
                                </v-tooltip>
                            </v-toolbar>
                            <v-card-text class="pa-3">
                                <span class="body-2"><b>NCI Thesaurus</b></span>
                                <v-tooltip bottom
                                    v-if="oncotree.externalReferences && oncotree.externalReferences.NCI && currentOncotreeCode">
                                    <v-btn icon slot="activator"
                                        :href="'https://ncit.nci.nih.gov/ncitbrowser/ConceptReport.jsp?dictionary=NCI_Thesaurus&code=' + oncotree.externalReferences.NCI[0]"
                                        target="_blank">
                                        <v-icon class="primary--text">mdi-open-in-new</v-icon>
                                    </v-btn>
                                    <span>Open {{ lastOncotreeCode }} in the
                                        NCI Thesaurus</span>
                                </v-tooltip>
                                <span
                                    v-if="oncotree.externalReferences && !oncotree.externalReferences.NCI && currentOncotreeCode">
                                    No NCI Thesaurus entry for {{ lastOncotreeCode }}<br />
                                    You can try a manual search directly in the NCI Thesaurus here:
                                    <v-btn slot="activator" icon
                                        href="https://ncit.nci.nih.gov/ncitbrowser/pages/home.jsf" target="_blank">
                                        <v-icon class="primary--text">mdi-open-in-new</v-icon>
                                    </v-btn>
                                </span>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                    <v-flex :class="getGeneFlexClasses()" v-show="currentlyActive == 'Cancer'">
                        <v-card>
                            <v-toolbar dense class="elevation-0" dark color="primary">
                                <div class="title ml-0">Genie</div>
                                <v-spacer></v-spacer>
                                </v-tooltip>
                            </v-toolbar>
                            <v-card-text class="pa-3">
                                <div :class="[cancerPlotLoading ? 'alpha-54' : '']">
                                    <div v-if="cancerPlotError && !cancerPlotLoading">No Genie Data could be found for
                                        oncotree {{ lastOncotreeCode }}</div>
                                    <div v-show="!cancerPlotError">
                                        <div :id="'mutatedGenesCancerPlot' + uniqId"></div>
                                    </div>
                                </div>

                            </v-card-text>
                        </v-card>
                    </v-flex>

                    <!-- Variant Results -->
                    <v-flex xs12 v-show="currentlyActive == 'Variant'" >
                        <lookup-panel-variant ref="variantPanel"
                            :standalone="standalone" :original-variant="originalVariant"
                            :original-chr="originalChr" :original-pos="originalPos"
                            :original-pos-old-build="originalPosOldBuild"
                            :current-oncotree-code="currentOncotreeCode" :current-variant="currentVariant"
                            :current-gene="currentGene" @handleDialogs="handleDialogs"
                            :last-oncotree-code="lastOncotreeCode" :last-variant="lastVariant"
                            :last-gene="lastGene"
                            :uniq-id="uniqId">
                        </lookup-panel-variant>
                    </v-flex>

                       <!-- CNV Results -->
                       <v-flex xs12 v-show="currentlyActive == 'CNV'">
                       <lookup-panel-cna  ref="cnvPanel"
                           :standalone="standalone"
                           :current-oncotree-code="currentOncotreeCode" :current-amp-del="currentAmpDel"
                           :current-gene="currentGene" @handleDialogs="handleDialogs"
                           :last-oncotree-code="lastOncotreeCode"
                           :last-gene="lastGene" :last-amp-del="lastAmpDel"
                           :uniq-id="uniqId">
                       </lookup-panel-cna>
                   </v-flex>

                    <!-- Fusion Results -->
                    <v-flex xs12  v-show="currentlyActive == 'Fusion'">
                    <lookup-panel-fusion ref="fusionPanel"
                        :standalone="standalone"
                        :current-oncotree-code="currentOncotreeCode" :current-five="currentFive"
                        :current-three="currentThree" @handleDialogs="handleDialogs"
                        :last-oncotree-code="lastOncotreeCode"
                        :last-five="lastFive" :last-three="lastThree"
                        :uniq-id="uniqId">
                    </lookup-panel-fusion>
                </v-flex>

                </v-layout>
            </v-container>
        </v-card>
    </v-card>
    <lookup-panel-plot-utils ref="plotUtils"  @handle-dialogs="handleDialogs"
    :standalone="standalone"></lookup-panel-plot-utils>
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
            loading: false,
            currentOncotreeCode: {},
            oncotree: {},
            currentVariant: "",
            geneSymbolErrorMessage: null,
           
            variantSymbolErrorMessage: null,
            reactomeItems: [],
            reactomeItemsOpen: [],
            reactomeRegex: /stId":"(id[0-9]+)"/gm,
            reactomeContentDetailUrl: null,
            
            cancerPlotLoading: false,
            cancerPlotError: false,
          
            lastGene: null,
            lastOncotreeCode: null,
            lastVariant: null,

            currentAmpDel: "",
            lastAmpDel: null,

            currentFive: "",
            currentThree: "",
            lastFive: null,
            lastThree: null,
            geneFiveErrorMessage:null,
            geneThreeErrorMessage: null,
            uniqId: "uniq"
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
            + "&ampDel=" +  (this.currentAmpDel ? this.currentAmpDel : "")
            + "&five=" +  (this.currentFive ? this.currentFive : "")
            + "&three=" +  (this.currentThree ? this.currentThree : "")
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
                bus.$emit("login-needed", [this, callback]);
                this.lastGene = null;
                this.lastOncotreeCode = null;
                this.lastVariant = null;
                this.lastAmpDel = null;
            }
            else if (response.success === false) {
                this.splashProgress = 100; //should dismiss the splash dialog
                bus.$emit("some-error", [this, response.message]);
            }
        },
        handleButton(button) {
            this.currentlyActive = button.label;
            this.updateRoute();
            this.$nextTick(() => {
                window.dispatchEvent(new Event('resize'));
            });
            // if (this.currentlyActive == "Cancer" && !this.isLoadingOverall()) {
            //     this.fetchCancerPlot();
            // }
        },
        updateRoute() {
            if (!this.standalone) {
                return;
            }
            var query = {
                gene: this.currentGene,
                variant: this.currentVariant,
                oncotree: this.currentOncotreeCode ? this.currentOncotreeCode.text : null,
                ampDel: this.currentAmpDel,
                five: this.currentFive,
                three: this.currentThree,
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
           this.currentAmpDel = this.$route.query.ampDel;
           this.currentFive = this.$route.query.five;
           this.currentThree = this.$route.query.three;
           var oncotree = null;
           if (oncotreeItems && oncotreeItems[0]) {
               oncotree = oncotreeItems[0];
           }
           this.currentOncotreeCode = oncotree;
           this.currentlyActive = this.$route.query.button;
        },
        submitForm() {
            setStart();
            this.updateRoute();
            var timeout = 500; //prioritize current button
            if (this.isFormValid("Gene") && this.needsReloading("Gene")) {
            setTimeout(() => {
                    this.$nextTick(() => {
                        this.$refs.genePanel.reload();
                        this.lastGene = this.currentGene;
                    });
                }, this.currentlyActive == "Gene" ? 0 : timeout);
            }

            if (this.isFormValid("Cancer") && this.needsReloading("Cancer")) {
            setTimeout(() => {
                    this.getOncotree();
                    this.fetchCancerPlot();
                    this.lastOncotreeCode = this.currentOncotreeCode.text;
                }, this.currentlyActive == "Cancer" ? 0 : timeout);
            }

            if (this.isFormValid("Variant") && this.needsReloading("Variant")) {
            setTimeout(() => {
                    this.$nextTick(() => {
                        this.$refs.variantPanel.reload();
                        this.lastGene = this.currentGene;
                        this.lastVariant = this.currentVariant;
                        this.lastOncotreeCode = this.currentOncotreeCode.text;
                    });
                }, this.currentlyActive == "Variant" ? 0 : timeout);
            }

            
            if (this.isFormValid("CNV") && this.needsReloading("CNV")) {
                setTimeout(() => {
                        this.$nextTick(() => {
                            this.$refs.cnvPanel.reload();
                            this.lastGene = this.currentGene;
                            this.lastAmpDel = this.currentAmpDel;
                            this.lastOncotreeCode = this.currentOncotreeCode.text;
                        });
                    }, this.currentlyActive == "CNV" ? 0 : timeout);
            }

            if (this.isFormValid("Fusion") && this.needsReloading("Fusion")) {
                setTimeout(() => {
                        this.$nextTick(() => {
                            this.$refs.fusionPanel.reload();
                            this.lastFive = this.currentFive;
                            this.lastThree = this.currentThree;
                            this.lastOncotreeCode = this.currentOncotreeCode.text;
                        });
                    }, this.currentlyActive == "Fusion" ? 0 : timeout);
            }

          
           
          
        },
        needsReloading(button) {
            if (button == "Gene") {
                return this.lastGene != this.currentGene;
            }
            if (button == "Cancer") {
                return this.lastOncotreeCode != (this.currentOncotreeCode ? this.currentOncotreeCode.text : null);
            }
            if (button == "Variant") {
                return this.lastGene != this.currentGene  || this.lastVariant != this.currentVariant 
                || this.lastOncotreeCode != (this.currentOncotreeCode ? this.currentOncotreeCode.text : null);
            }
            if (button == "CNV") {
                return this.lastGene != this.currentGene  || this.lastAmpDel != this.currentAmpDel 
                || this.lastOncotreeCode != (this.currentOncotreeCode ? this.currentOncotreeCode.text : null);
            }
            if (button == "Fusion") {
                return this.lastFive != this.currentFive  || this.lastThree != this.currentThree 
                || this.lastOncotreeCode != (this.currentOncotreeCode ? this.currentOncotreeCode.text : null);
            }
        },
        calcMaxHeight() {
            if (this.standalone) {
                return null;
            }
            return getDialogMaxHeightNumber(90);
        },
        isFormValid(button) {
            if (!button) {
                button = this.currentlyActive;
            }
            var geneValid = this.currentGene && this.currentGene.length > 0;
            var fiveValid = this.currentFive && this.currentFive.length > 0;
            var threeValid = this.currentThree && this.currentThree.length > 0;
            var oncotreeValid = this.currentOncotreeCode != null && this.currentOncotreeCode.text != null;
            var variantValid = this.currentVariant && this.currentVariant.length > 0;
            var cnvValid = this.currentAmpDel == "amp" || this.currentAmpDel == "del";
            switch(button) {
                case "Gene":
                    return geneValid;
                case "Cancer":
                    return oncotreeValid;
                case "Variant":
                    return geneValid && variantValid && oncotreeValid;
                case "CNV":
                    return geneValid && cnvValid && oncotreeValid;
                case "Fusion":
                    return fiveValid && threeValid && oncotreeValid;
            }
            return false;
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
        isLoadingOverall() {
            switch(this.currentlyActive) {
                case "Gene":
                    return this.$refs.genePanel && this.$refs.genePanel.isLoading();
                case "Cancer":
                    return this.loading;
                case "Variant":
                    return this.$refs.variantPanel && this.$refs.variantPanel.isLoading();
                case "CNV":
                    return this.$refs.cnvPanel && this.$refs.cnvPanel.isLoading();
                case "Fusion":
                    return this.$refs.fusionPanel && this.$refs.fusionPanel.isLoading();    
            }
            return this.loading;
        },
        getFlexClasses() {
            switch(this.currentlyActive) {
                case "Gene":
                    if (this.standalone) {
                        return ["xs12","md6","lg4","xl3"];
                    }
                    else {
                        return ["xs12"];
                    }
                case "Cancer":
                    return ["xs12"];
                case "Variant":
                    if (this.standalone) {
                        return ["xs12","md6","lg4","xl3"];
                    }
                    else {
                        return ["xs12","md6","lg6","xl6"];
                    }
                case "CNV":
                    if (this.standalone) {
                        return ["xs12","md6","lg4","xl3"];
                    }
                    else {
                        return ["xs12","md6","lg6","xl6"];
                    }
                case "Fusion":
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
                return "xs12 md12 lg6";
            }
            else {
                return "xs12";
            }
        },
        getGeneFlexClassesHalf() {
            if (this.standalone) {
                return "xs12 md6 lg3";
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
        reloadValues() {
            this.$emit("reload-values");
        },
        fetchCancerPlot() {
            if (this.currentOncotreeCode) {
                this.cancerPlotLoading = true;
                this.cancerPlotError = false;
                var promise1 = this.$refs.plotUtils.updateBarPlot("/getMutatedGeneByCancer", {
                    oncotreeCode: this.currentOncotreeCode.text,
                    plotId: "mutatedGenesCancerPlot" + this.uniqId,
                }, true);
                Promise.all([promise1]).then(values => {
                    this.cancerPlotLoading = false;
                    if (values.filter(v => v.success).length != values.length) {
                        console.log("Some plots did not finish properly");
                        this.cancerPlotError = true;
                    }
                    else {
                        this.cancerPlotError = false;
                    }
                })
            }
        },
        relayout() {
            console.log("relayouting");
        },
        calcMaxWidth() {
            if (this.standalone) {
                return null;
            }
            // var content = document.getElementsByClassName("v-content");
			// 	if (content) {
                    return this.$el.parentElement.parentElement.clientWidth / 3 - 10;
                    // return (content[0].clientWidth - parseInt(content[0].style.paddingLeft.replace("px", ""))) / 3 - 10;
				// }
            // return null; 
        }
    },
    mounted() {
        this.uniqId = "_uniq" + Math.round(Math.random() * 10000);
    },
    created: function () {
    },
    destroyed: function () {
    },
    watch: {
        '$route': "handleRouteChanged"
    }


});