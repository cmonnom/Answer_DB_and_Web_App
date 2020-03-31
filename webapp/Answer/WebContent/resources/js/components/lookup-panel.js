Vue.component('lookup-panel', {
    props: {
        title: {default: "Lookup Tool", type: String},
    },
    template: `<div>

    <div v-if="panelVisible">
        <v-navigation-drawer app permanent right width="500" class="elevation-5">
            <v-toolbar dense>
                <v-tooltip class="ml-0" bottom>
                    <v-menu offset-y offset-x slot="activator" class="ml-0">
                        <v-btn slot="activator" flat icon dark>
                            <v-icon color="amber accent-2">filter_list</v-icon>
                        </v-btn>
                        <v-list>

                            <v-list-tile avatar @click="togglePanel()">
                                <v-list-tile-avatar>
                                    <v-icon>close</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title>Close Lookup Tool</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>


                        </v-list>
                    </v-menu>
                    <span>Filter Menu</span>
                </v-tooltip>
                <div class="title ml-0">
                    {{ title }}
                </div>
                <v-spacer></v-spacer>

                <v-tooltip bottom>
                    <v-btn icon @click="togglePanel()" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close Lookup Tool</span>
                </v-tooltip>
            </v-toolbar>
        </v-navigation-drawer>
        <v-navigation-drawer app permanent right width="500" class="mt-5" height="calc(100% - 48px)">
            <!-- list of possible filters -->
            <v-container grid-list-md pl-1 pr-1 pt-1 pb-1>
            </v-container>
        </v-navigation-drawer>
    </div>

    <v-snackbar :timeout="snackBarTimeout" :bottom="true" v-model="snackBarVisible">
    {{ snackBarMessage }}
    <v-tooltip top>
    <a slot="activator" :href="snackBarLink"><v-icon dark>{{ snackBarLinkIcon }}</v-icon></a>
    <span>Open Link</span>
    </v-tooltip>
    <v-btn flat color="primary" @click="snackBarVisible = false">Close</v-btn>
</v-snackbar>
</div>`, data() {
        return {
            panelVisible: false,
            snackBarVisible: false,
            snackBarLinkIcon: "",
            snackBarLink: "",
            snackBarTimeout: 2000,
            snackBarMessage: "",
        }

    },
    methods: {
        togglePanel() {
            this.panelVisible = !this.panelVisible;
            if (!this.panelVisible) {
                bus.$emit("need-layout-resize", this);
            }
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
                this.splashProgress = 100; //should dismiss the splash dialog
                bus.$emit("some-error", [this, response.message]);
            }
        },
        showSnackBarMessage(message) {
            this.snackBarMessage = message;
            this.snackBarLink = "";
            this.snackBarVisible = true;
        },
    },
    created: function () {
    },
    destroyed: function () {
    },
    watch: {
    }


});