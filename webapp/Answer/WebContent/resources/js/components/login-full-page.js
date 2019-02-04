

Vue.component('login-full-page', {
    props: {
        message: { default: "Please log in using UTSW credentials", type: String },
        dataUrlRoot: {default: webAppRoot, type: String}
    },
    template: `<div :class="[magicClass, 'magic-transition', backgroundClass, ]">
    <goodies2 ref="goodiesPanel" @end-goodies="showGoodiesPanel = false"></goodies2>
    <v-layout row justify-center>
        <v-flex :class="['text-xs-center', elevation]" xs12 md6 lg3 xl2>
        <div class="plain-light-background">
        <img :src="dataUrlRoot + '/resources/images/utsw-master-logo-lg.png'" alt="UTSW" width="100%" class="ml-0">
        <img @click="handleVersionChange" v-if="isBetaVersion()" :src="dataUrlRoot + '/resources/images/answer-logo-large-beta.png'" width="100%" alt="Answer" :class="[changingVersion? 'shake': '', 'pl-4', 'pr-4', 'pb-3']"/> 
        <img v-if="isVersionOne()"  :src="dataUrlRoot + '/resources/images/answer-logo-large.png'" width="100%" alt="Answer" class="pl-4 pr-4 pb-3"/>     
        <b>An</b>notation <b>S</b>oft<b>w</b>are for <b>E</b>lectronic <b>R</b>eporting
        </div>
        </v-flex>
    </v-layout> 
    <v-layout row justify-center>
        <v-flex class="text-xs-center elevation-1 teal darken-2" xs12 md6 lg3 xl2>
            <login :message="message" :popup="false"></login>
        </v-flex>
    </v-layout>


</div>`,
    data() {
        return {
            magicClass: "",
            elevation: "elevation-1",
            backgroundClass: 'plain-light-background',
            changingVersion: false,
            versionName: "1.0"
        }
    },
    methods: {
        checkAlreadyLoggedIn() {
            axios.get(
                webAppRoot + "/checkAlreadyLoggedIn",
                {
                    params: {
                    }
                })
                .then(response => {
                    if (response.data.success) {
                        console.log(response.data.reason);
                        if (this.$route.query.urlRedirect) {
                            window.location = this.$route.query.urlRedirect;
                        }
                        else {
                            window.location = response.data.urlRedirect;
                        }
                    }
                    else {
                        console.log(response.data.reason);
                    }
                }).catch(error => {
                });
        },
        switchToReleaseVersion() {
            this.magicClass = "magic-wand";
            this.backgroundClass = "";
            this.changingVersion = true;
        },
        handleVersionChange() {
            if (this.changingVersion) {
                this.changingVersion = false;
                this.backgroundClass = 'plain-light-background';
                this.magicClass = "";
                this.$refs.goodiesPanel.createFireworks();
                this.updateToVersion1();
                setTimeout(this.$refs.goodiesPanel.clearFireworks, 10000);
            }
            else {
                if (Date.now() >= new Date("02/27/2019").getTime()) {
                    this.switchToReleaseVersion();
                }
            }
        },
        updateToVersion1() {
            axios.get(
                webAppRoot + "/updateVersion",
                {
                    params: {
                    }
                })
                .then(response => {
                    if (response.data.success) {
                        this.getVersion();
                    }
                    else {
                        console.log(response.data.reason);
                    }
                }).catch(error => {
                });
        },
        isBetaVersion() {
            return this.versionName == "beta";
        },
        isVersionOne() {
            return this.versionName == "1.0";
        },
        getVersion() {
			axios.get(webAppRoot + "/getCurrentVersion", {
				params: {}
			})
            .then(response => {
                if (response.data.isAllowed) {
                    this.versionName = response.data.payload;
                }
                else {
                    console.log(response.message);
                }
            })
            .catch(error => {
                alert(error);
            });
		}
    },
    mounted: function () {
       
    },
    computed: {

    },
    created: function () {
        this.checkAlreadyLoggedIn();
        this.getVersion();
    }



})