

Vue.component('report-pubmed-ids', {
    props: {
        pubmeds: {default: () => [], type: Array},
        color: {default: "primary", type: String}
    },
    template: `<v-card>
    <v-toolbar class="elevation-0" dense dark :color="color">
        <v-menu offset-y offset-x class="ml-0">
            <v-btn slot="activator" flat icon dark>
                <v-icon color="amber accent-2">mdi-book-open-page-variant</v-icon>
            </v-btn>
            <v-list>
                <v-list-tile avatar @click="closePanel()">
                    <v-list-tile-avatar>
                        <v-icon>cancel</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Close Panel</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
            </v-list>
        </v-menu>
        <v-toolbar-title>Pubmed References</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip bottom>
            <v-btn flat icon @click="closePanel()" slot="activator">
                <v-icon>close</v-icon>
            </v-btn>
            <span>Close Panel</span>
        </v-tooltip>
    </v-toolbar>
    <v-card-text>
    <v-container grid-list-md fluid>
    <v-layout row wrap>
    <v-flex xs12 v-for="item in pubmeds" :key="item.pmid">
        <div><b>{{ item.title }}</b></div>
        <div>{{ item.authors }}</div>
        <div>{{ item.description }}</div>
        <div>PMID: {{ item.pmid }} 
        <v-tooltip bottom>
        <v-btn small slot="activator" icon flat @click="openPMIDLink(item.pmid)" :color="color" class="mt-0 ml-0 mb-0 mr-0">
        <v-icon>open_in_new</v-icon>
        </v-btn>
        <span>Open in New Tab</span>
        </v-tooltip>
        </div>
    </v-flex>
    </v-layout> 

    </v-container>
    </v-card-text>
</v-card>
    `,
    data() {
        return {
           


        }
    },
    methods: {
        // getPubmedDetails() {
        //     axios({
        //         method: 'post',
        //         url: webAppRoot + "/getPubmedDetails",
        //         params: {
        //         },
        //         data: {
        //             pmids: ['26221189','29315417']
        //         }
        //     })
        //         .then(response => {
        //             if (response.data.isAllowed) {
        //                 console.log(response.data);
        //             } else {
        //                 this.handleDialogs(response.data, this.getPubmedDetails);
        //             }
        //         })
        //         .catch(error => {
        //             this.handleAxiosError(error);
        //         });
        // },
        closePanel() {
            this.$emit("close-pmid-panel");
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
        openPMIDLink(pmid) {
            window.open('https://www.ncbi.nlm.nih.gov/pubmed/?term=' + pmid, "_blank");
        },

    },
    computed: {

    },
    created() {

    },
    destroyed() {
    },
    mounted() {
    },
    watch: {
    }



});

