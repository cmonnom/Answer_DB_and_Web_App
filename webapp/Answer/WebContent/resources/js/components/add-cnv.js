Vue.component('add-cnv', {
    props: {
        noEdit: { default: true, type: Boolean },
        aberrationTypes: { default: () => [], type: Array},
        cnvChromList: { default: () => [], type: Array},
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
                        <v-flex pt-3 xs3>
                            <v-tooltip bottom>
                            <span slot="activator">Gene:<br/>(Gene Symbol)</span>
                            <span>Select a Gene <b>OR</b> a Range</span>
                            </v-tooltip>
                        </v-flex>
                        <v-flex xs12 md9>
                            <v-select :items="genesInPanel" label="Select a Gene for this CNV" v-model="genes"
                            chips deletable-chips :disabled="noEdit" autocomplete
                            item-value="value" item-text="name"></v-select>
                        </v-flex>
                    </v-layout>

                    <v-layout row wrap>
                        <v-flex pt-2 xs3>
                            Aberration Type:
                        </v-flex>
                        <v-flex xs9>
                            <v-select clearable :items="aberrationTypes" v-model="aberrationType" label="Select Aberration Type"
                                single-line hide-details class="no-height-select" :disabled="noEdit"></v-select>
                        </v-flex>
                    </v-layout>
                    <v-layout row wrap>
                        <v-flex xs3>
                            Copy Number:
                        </v-flex>
                        <v-flex xs9>
                            <v-text-field v-model="copyNumber" :rules="numberRules" class="no-top-text-field">
                            </v-text-field>
                        </v-flex>
                    </v-layout>
                </v-flex>
            </v-layout>
        </v-container>
    </v-card-text>
    <v-card-actions :class="['card-actions-bottom']">
        <v-tooltip top>
            <v-btn slot="activator" color="primary" @click="saveCNV()"
            :disabled="isDisabled()">Save
                <v-icon right dark>save</v-icon>
            </v-btn>
            <span v-if="!isDisabled()">Create a new CNV</span>
            <span v-if="isDisabled()">
            All fields are required.<br/>
            Choose between a Gene <b>OR</b> a Range (for multiple genes)</span>
        </v-tooltip>
        <v-tooltip top>
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
            chrom: null,
            genes: "",
            aberrationType: null,
            copyNumber: null,
            genesInPanel: [],
            savingCNV: false,
            start: null,
            end: null
        }

    },
    methods: {
        hidePanel() {
            this.$emit("hide-add-cnv-panel", this);
        },
        saveCNV() {
            this.savingCNV = true;
            
            axios({
                method: 'post',
                url: webAppRoot + "/saveCNV",
                params: {
                    caseId: this.$route.params.id,
                },
                data: {
                    chrom: this.chrom,
                    genes: [this.genes], //should be an array but we only select one gene, so it's a string instead
                    aberrationType: this.aberrationType,
                    copyNumber: this.copyNumber
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    this.hidePanel();
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
            var valid = this.genes && this.aberrationType && this.copyNumber && !isNaN(this.copyNumber);
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
        handleAxiosError(error) {
            console.log(error);
            bus.$emit("some-error", [this, error]);
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
    },
    computed: {
    },
    created() {
        this.getGenesInPanel();
    },
    watch: {
        
    }


});