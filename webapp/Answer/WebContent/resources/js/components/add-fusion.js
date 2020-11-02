Vue.component('add-fusion', {
    props: {
        noEdit: { default: true, type: Boolean },
    },
    template: /*html*/`<v-card>
    <v-toolbar class="elevation-0" dense dark color="primary">
        <v-toolbar-title>
            Add a New Fusion
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
                    <b>Fill all information below to add a new Fusion:</b>
                </v-flex>
                <v-flex xs12>
                    <v-layout row wrap>
                        <v-flex xs4 pt-4>
                            Left Gene:
                        </v-flex>
                        <v-flex xs8>
                            <v-text-field v-model="leftGene" placeholder="eg. EML4"
                            @change="capitalizeLeftGene">
                            </v-text-field>
                        </v-flex>
                    </v-layout>
                </v-flex>
                <v-flex xs12>
                    <v-layout row wrap>
                        <v-flex xs4 pt-4>
                            Left Exons:
                        </v-flex>
                        <v-flex xs8>
                            <v-text-field v-model="leftExon" placeholder="eg. 1-12">
                            </v-text-field>
                        </v-flex>
                    </v-layout>
                </v-flex>
                <v-flex xs12>
                    <v-layout row wrap>
                        <v-flex xs4 pt-4>
                            Right Gene:
                        </v-flex>
                        <v-flex xs8>
                            <v-text-field v-model="rightGene" placeholder="eg. MLK"
                            @change="capitalizeRightGene">
                            </v-text-field>
                        </v-flex>
                    </v-layout>
                </v-flex>
                <v-flex xs12>
                    <v-layout row wrap>
                        <v-flex xs4 pt-4>
                            Right Exons:
                        </v-flex>
                        <v-flex xs8>
                            <v-text-field v-model="rightExon"  placeholder="eg. 1-12">
                            </v-text-field>
                        </v-flex>
                    </v-layout>
                </v-flex>
                <v-flex xs12 pb-3>
                <b>Fusion Name: </b><span v-text="generateFusionName()"></span>
            </v-flex>
            </v-layout>
        </v-container>
    </v-card-text>
    <v-card-actions :class="['card-actions-bottom']">
        <v-tooltip top class="pr-2">
            <v-btn slot="activator" color="primary" @click="saveFusion()"
            :disabled="isDisabled()">Save
                <v-icon right dark>save</v-icon>
            </v-btn>
            <span v-if="!isDisabled()">Create a new Fusion</span>
            <span v-if="isDisabled()">
            All fields are required</span>
        </v-tooltip>
        <v-tooltip top class="pr-2">
            <v-btn slot="activator" color="error" @click="hidePanel()"
            :disabled="savingFusion">Cancel
                <v-icon right dark>cancel</v-icon>
            </v-btn>
            <span>Discard changes</span>
        </v-tooltip>
    </v-card-actions>
</v-card>`,
    data() {
        return {
            numberRules: [(v) => { return !isNaN(v) || 'Only one number' }],
            leftGene: null,
            rightGene: null,
            leftExon: null,
            rightExon: null,
            savingFusion: false,
            start: null,
            end: null,
        }

    },
    methods: {
        hidePanel() {
            this.$emit("hide-add-fusion-panel", this);
        },
        saveFusion() {
            this.savingFusion = true;
            axios({
                method: 'post',
                url: webAppRoot + "/saveFusion",
                params: {
                    caseId: this.$route.params.id,
                },
                data: {
                    leftGene: this.leftGene,
                    rightGene: this.rightGene,
                    leftExons: this.leftExon,
                    rightExons: this.rightExon
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    this.hidePanel();
                    bus.$emit("show-snackbar", "Fusion Created Successfuly", 4000);
                    this.$emit("refresh-fusion-table");
                }
                else {
                    this.handleDialogs(response.data, this.saveFusion);
                }
                this.savingFusion = false;
            }
            ).catch(error => {
                this.savingFusion = false;
                this.handleAxiosError(error);
            }
            );
        },
        isDisabled() {
            var valid = !this.noEdit && this.leftGene && this.rightGene && this.leftExon
            && this.rightExon;
            return !valid;

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
        generateFusionName() {
            if (!this.leftGene || !this.rightGene) {
                return "";
            }
            return (this.leftGene + "--" + this.rightGene).toUpperCase();
        },
        capitalizeLeftGene() {
            this.leftGene = this.leftGene.toUpperCase();
        }
        ,
        capitalizeRightGene() {
            this.rightGene = this.rightGene.toUpperCase();
        }
    },
    computed: {
    },
    created() {
    },
    watch: {
    }


});