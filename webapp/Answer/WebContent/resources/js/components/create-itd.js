Vue.component('create-itd', {
    props: {
    },
    template: ` <!-- create ITD dialog -->
   
        <v-card>
        <v-toolbar class="elevation-0" dense dark color="primary">
        <v-toolbar-title>
            Create a New ITD
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip bottom>
            <v-btn icon @click="cancelCreateITD()" slot="activator">
                <v-icon>close</v-icon>
            </v-btn>
            <span>Cancel</span>
        </v-tooltip>

    </v-toolbar>
            <v-card-text class="pt-3 pl-3 pr-4 pb-3 subheading">
                Select a gene for this ITD:
                <v-select clearable :value="itdGene" :items="genes" v-model="itdGene"
                label="Select a Gene" single-line autocomplete
                item-text="name" item-value="value"></v-select>
            </v-card-text>
            <v-card-actions class="card-actions-bottom">
                <v-btn color="primary" @click="createITD()" slot="activator" :disabled="isDisabled()">Create ITD
                </v-btn>
                <v-btn color="error" @click="cancelCreateITD()" :disabled="isDisabled()" slot="activator">Cancel
                </v-btn>
            </v-card-actions>
        </v-card>

`,
    data() {
        return {
            itdGene: this.itdGene,
            itdButtonDisabled: false,
            genes: []
        }

    },
    methods: {
        cancelCreateITD() {
            this.$emit("hide-create-itd", this);
        },
        createITD() {
            this.itdButtonDisabled = true;
            axios.get(
                webAppRoot + "/createITD",
                {
                    params: {
                        caseId: this.$route.params.id,
                        gene: this.itdGene
                    }
                })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.itdDialogVisible = false;
                        this.itdButtonDisabled = true;
                        this.$emit("show-snackbar", "ITD created successfully", null, null, 2000);
                        this.$emit("refresh-variants");
                    }
                    else {
                        this.handleDialogs(response.data, this.createITD);
                    }
                    this.itdButtonDisabled = false;
                }).catch(error => {
                    this.handleAxiosError(error);
                    this.itdButtonDisabled = false;
                });
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
                        this.genes = response.data.items;

                    } else {
                        this.handleDialogs(response.data, this.getGenesInPanel);
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
        isDisabled() {
            return this.itdButtonDisabled || !this.itdGene;
        }
    },
    computed: {
    },
    created() {
        this.getGenesInPanel();
    },
    watch: {
    }


});