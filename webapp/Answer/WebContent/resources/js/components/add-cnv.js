Vue.component('add-cnv', {
    props: {
        noEdit: { default: true, type: Boolean },
        aberrationTypes: { default: () => [], type: Array},
        cnvChromList: { default: () => [], type: Array},
        currentGeneList: { default: () => [], type: Array},
    },
    template: `<v-card>
    <v-toolbar class="elevation-0" dense dark color="primary">
        <v-toolbar-title>
            Add a New CNV
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip bottom>
            <v-btn icon @click="hidePanel()" slot="activator">
                <v-icon>close</v-icon>
            </v-btn>
            <span>Cancel</span>
        </v-tooltip>

    </v-toolbar>
    <v-card-text class="pl-2 pr-2 subheading">
        <v-container grid-list-md fluid>
            <v-layout row wrap>
                <v-flex xs12 pb-3>
                    <b>Fill all information below to add a new CNV:</b>
                </v-flex>
                <v-flex xs12>
                    
                    <v-layout row wrap>
                        <v-flex pt-3 xs12>
                            <span>Orignal Genes:</span>
                            <span v-text="getGeneList()" class="blue-grey--text text--lighten-1"></span>
                        </v-flex>
                    </v-layout>

                    <v-layout row wrap>
                    <v-flex pt-3 xs12>
                            <v-tooltip bottom>
                                <span slot="activator">Genes To Keep</span>
                                <span>You can remove some genes from this CNV.
                                <br/>The final list of CNV will be determined by 
                                <br/>the left-most and right-most chromosomal positions 
                                <br/>of genes in the list,regardless of the genes removed.</span>
                            </v-tooltip>
                            <v-tooltip bottom>
                            <v-btn slot="activator" :disabled="canRefreshGeneChips()" @click="createGeneChips()" flat icon 
                            class="ml-0 mr-0 mt-0 mb-0"><v-icon>refresh</v-icon></v-btn>
                            <span>Reset</span>
                            </v-tooltip>
                            <span>:</span>
                            <v-chip v-model="chip.visible" v-for="chip in geneChipList" :key="chip.text" close >{{ chip.text }}</v-chip>
                        </v-flex>
                    </v-layout>

                    <v-layout row wrap>
                        <v-flex pt-4 xs4>
                            Aberration Type:
                        </v-flex>
                        <v-flex xs8>
                            <v-select clearable :items="aberrationTypes" v-model="aberrationType" label="Select Aberration Type"
                                single-line hide-details  :disabled="noEdit"></v-select>
                        </v-flex>
                    </v-layout>
                    <v-layout row wrap>
                        <v-flex xs4 pt-4>
                            Copy Number:
                        </v-flex>
                        <v-flex xs8>
                            <v-text-field v-model="copyNumber" :rules="numberRules">
                            </v-text-field>
                        </v-flex>
                    </v-layout>
                </v-flex>
            </v-layout>
        </v-container>
    </v-card-text>
    <v-card-actions :class="['card-actions-bottom']">
        <v-tooltip top class="pr-2">
            <v-btn slot="activator" color="primary" @click="saveCNV()"
            :disabled="isDisabled()">Save
                <v-icon right dark>save</v-icon>
            </v-btn>
            <span v-if="!isDisabled()">Create a new CNV</span>
            <span v-if="isDisabled()">
            All fields are required.<br/>
            Choose between a Gene <b>OR</b> a Range (for multiple genes)</span>
        </v-tooltip>
        <v-tooltip top class="pr-2">
            <v-btn slot="activator" color="error" @click="hidePanel()"
            :disabled="savingCNV">Cancel
                <v-icon right dark>cancel</v-icon>
            </v-btn>
            <span>Discard changes</span>
        </v-tooltip>
    </v-card-actions>
</v-card>`,
    data() {
        return {
            numberRules: [(v) => { return !isNaN(v) || 'Only one number' }],
            // chrom: null,
            genes: "",
            aberrationType: null,
            copyNumber: null,
            genesInPanel: [],
            savingCNV: false,
            start: null,
            end: null,
            geneChipList: []
        }

    },
    methods: {
        hidePanel() {
            this.$emit("hide-add-cnv-panel", this);
        },
        saveCNV() {
            this.savingCNV = true;
            var finalGeneList = [];
            for (var i =0; i < this.geneChipList.length; i++) {
                if (this.geneChipList[i].visible) {
                    finalGeneList.push(this.geneChipList[i].text);
                }
            }
            axios({
                method: 'post',
                url: webAppRoot + "/saveCNV",
                params: {
                    caseId: this.$route.params.id,
                },
                data: {
                    // chrom: this.chrom,
                    genes: finalGeneList,
                    aberrationType: this.aberrationType,
                    copyNumber: this.copyNumber
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    this.hidePanel();
                    bus.$emit("show-snackbar", "CNV Created Successfuly", 4000);
                    this.$emit("refresh-cnv-table");
                }
                else {
                    this.handleDialogs(response.data, this.saveCNV);
                }
                this.savingCNV = false;
            }
            ).catch(error => {
                this.savingCNV = false;
                this.handleAxiosError(error);
            }
            );
        },
        isDisabled() {
            var valid = this.aberrationType && this.copyNumber && !isNaN(this.copyNumber);
            // if (this.genes) { 
            //     valid &= !this.start && !this.end & !this.chrom; //if genes is set, range should not
            // }
            // else if (this.start && this.end && this.chrom) { //if genes is not set, range should
            //     valid &= !isNaN(this.start) && !isNaN(this.end);
            // }
            // else {
            //     valid = false;
            // }
            // if (!this.genes && !this.start && !this.end && !this.chrom) { //none of them is set
            //     valid = false;
            // }
            return !valid;

        },
        getGenesInPanel() {
            axios.get(
                webAppRoot + "/getGenesInPanel",
                {
                    params: {
                        caseId: this.$route.params.id

                    }
                }).then(response => {
                    if (response.data.isAllowed) {
                        this.genesInPanel = response.data.items;

                    } else {
                        this.handleDialogs(response.data, this.getGenesInPanel);
                    }
                }).catch(error => {
                    this.handleAxiosError(error);
                });
        },
        getGeneList() {
           
            return this.currentGeneList.join(", ") + " (" + this.currentGeneList.length + " gene" + (this.currentGeneList.length == 1 ? ")" : "s)");
        },
        createGeneChips() {
            this.geneChipList = [];
            for (var i = 0; i < this.currentGeneList.length; i++) {
                this.geneChipList.push({
                    text: this.currentGeneList[i],
                    visible: true
                });
            }
        },
        canRefreshGeneChips() {
            if (this.geneChipList.length == 0) {
                return false;
            }
            return this.geneChipList.filter(c => !c.visible).length == 0;
        },
        handleAxiosError(error) {
            console.log(error);
            bus.$emit("some-error", [null, error]);
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
        },
    },
    computed: {
    },
    created() {
        // this.getGenesInPanel();
    },
    watch: {
        currentGeneList: "createGeneChips"
    }


});