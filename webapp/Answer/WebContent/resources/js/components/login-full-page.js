

Vue.component('login-full-page', {
    props: {
        dataUrlRoot: {default: webAppRoot, type: String}
    },
    template: `<div :class="[magicClass, 'magic-transition', backgroundClass, ]">

    <v-snackbar :timeout="0" :bottom="true" :value="snackBarVisible">
    {{ snackBarMessage }}
    <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
  </v-snackbar>

    <v-dialog v-model="showResetPasswordDialog" max-width="50%" scrollable>
    <v-card>
      <v-toolbar dense dark color="teal lighten-2">
        <v-toolbar-title class="white--text">Reset Your Password</v-toolbar-title>
      </v-toolbar>
      <v-card-title>Send a reset link to this email address:</v-card-title>
      <v-card-text>
        <v-layout row wrap class="pl-2">
          <v-flex xs12 lg6 >
          <v-text-field label="email" 
          v-model="email"
          required></v-text-field>
          </v-flex>
        </v-layout>
      </v-card-text>
      <v-card-actions>
        <v-btn class="mr-2" color="success" @click="sendResetPasswordEmail()" slot="activator"
        :disabled="!email">Send
          <v-icon right dark>email</v-icon>
        </v-btn>
        <v-btn class="mr-2" color="error" @click="cancelReset()">Cancel
          <v-icon right dark>cancel</v-icon>
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

    <goodies2 ref="goodiesPanel" @end-goodies="showGoodiesPanel = false"></goodies2>
    <v-layout row justify-center>
        <v-flex :class="['text-xs-center', elevation]" xs12 md6 lg3 xl2>
        <div class="white">
        <img alt="utsw master logo" :src="dataUrlRoot + '/resources/images/utsw-master-logo-lg.png'" width="100%" class="ml-0">
        <img alt="answer logo beta" @click="handleVersionChange" v-if="isBetaVersion()" :src="dataUrlRoot + '/resources/images/answer-logo-large-beta.png'" width="100%" :class="[changingVersion? 'shake': '', 'pl-4', 'pr-4', 'pb-3']"/> 
        <img alt="answer logo" v-if="isVersionOne()"  :src="dataUrlRoot + '/resources/images/answer-logo-large.png'" width="100%" class="pl-4 pr-4 pb-3"/>     
        <div class="pb-2"><b>An</b>notation <b>S</b>oft<b>w</b>are for <b>E</b>lectronic <b>R</b>eporting</div>
        </div>
        </v-flex>
    </v-layout> 
    <v-layout row justify-center v-show="showLogin">
        <v-flex class="text-xs-center elevation-1 teal darken-2" xs12 md6 lg3 xl2>
            <login :message="message" :popup="false"></login>
        </v-flex>
    </v-layout>
    <v-layout row justify-center pt-3 v-if="allowResetPwd">
        <v-flex xs12 md6 log3 xl2 class="text-xs-center">
            <a @click="openResetPasswordDialog">Reset Password</a>
        </v-flex>
    </v-layout>


</div>`,
    data() {
        return {
            magicClass: "",
            elevation: "elevation-1",
            backgroundClass: 'plain-light-background',
            changingVersion: false,
            versionName: "1.0",
            message: authMessage,
            showResetPasswordDialog: false,
            email: "",
            snackBarVisible: false,
            snackBarMessage: "",
            allowResetPwd: false,
            showLogin: false
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
                        if (this.$route.query.urlRedirect) {
                            window.location = this.$route.query.urlRedirect;
                        }
                        else {
                            window.location = response.data.urlRedirect;
                        }
                    }
                    else {
                        this.showLogin = true;
                        this.message = this.message ? this.message : response.data.reason;
                        console.log(response.data.reason);
                        this.allowResetPwd = response.data.payload;
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
        },
        openResetPasswordDialog() {
            this.email = "";
            this.showResetPasswordDialog = true;
        },
        cancelReset() {
            this.showResetPasswordDialog = false;
        },
        sendResetPasswordEmail() {
            axios({
                method: 'post',
                url: webAppRoot + "/sendResetPasswordEmail",
                params: {
                    email: this.email,
                },
                data: {
                }
            }).then(response => {
                if (response.data.success) {
                    this.snackBarMessage = "The reset email was sent";
                    this.cancelReset();
                }
                else if (response.data.message) {
                    this.snackBarMessage = response.data.message;
                }
                else {
                    this.snackBarMessage = "Unknown email address or could not send email";
                }
                this.snackBarVisible = true;
            }
            ).catch(error => {
                alert(error);
            }
            );
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