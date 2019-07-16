

Vue.component('login', {
    props: {
        message: { default: "Please log in using UTSW credentials", type: String },
        authType: { default: "ldap", type: String },
        popup: { default: true, type: Boolean },
        dataUrlRoot: { default: webAppRoot, type: String }
    },
    template: `<div>
            <v-container pl-3 pr-3 pt-3 pb-3>
                <div v-if="isCloudLogin()">
                <v-btn block dark color="teal" @click="azureSignIn" :disabled="loading">Sign in with Azure</v-btn>
                <v-btn block dark color="teal" @click="azureSignOut" :disabled="loading">Switch Account</v-btn>
                <v-alert class="text-xs-center" v-show="showError" color="error" icon="warning" value="true">
                {{ errorReason }}
                 </v-alert>
                </div>
                <v-form :value="formValid" @submit.prevent ref="form" lazy @submit="submitForm" v-show="!isCloudLogin()">
                        <div class="text-xs-center white--text">
                            {{ message }}
                        </div>

                    <div class="text-xs-center">
                        <v-text-field label="Username" 
                        v-model="username"
                        required
                        autofocus
                        dark color="white"></v-text-field>
                        <v-text-field label="Password"
                        v-model="password" 
                        required
                        dark
                        color="white"
                        :append-icon="showPasswordIcon ? 'visibility' : 'visibility_off'" 
                        @click:append="() => (showPasswordIcon = !showPasswordIcon)"
                        :type="showPasswordIcon ? 'password' : 'text'"
                        ></v-text-field>
                    </div>
                    <v-alert class="text-xs-center" v-show="showError" color="error" icon="warning" value="true">
                        {{ errorReason }}
                    </v-alert>
                    <div class="text-xs-center">
                        <v-btn type="submit"
                        :loading="loading"
                        :disabled="loading || !formValid">
                            Submit
                        </v-btn>
                        <v-btn @click="clear">Clear</v-btn>
                    </div>
                </v-form>
            </v-container>
</div>`,
    data() {
        return {
            formValid: true,
            username: '',
            password: '',
            showPasswordIcon: true, //the icon/button shows a view password icon if true (therefore the password is hidden if true)
            errorReason: '',
            showError: false,
            loading: false,

            //Azure
            msalConfig: {
                auth: {
                    clientId: "730b20e4-c009-4a44-a27e-fc8ff0672cc0",
                    authority: "https://login.microsoftonline.com/3843407e-c407-4775-ad35-b5ab96ebb930",
                    navigateToLoginRequestUrl: false,
                    redirectUri: window.location.origin + "/Answer/"
                },
                cache: {
                    cacheLocation: "localStorage",
                    storeAuthStateInCookie: false
                }
            },
            graphConfig: {
                graphMeEndpoint: "https://graph.microsoft.com/v1.0/me"
            },
            requestObj: {
                scopes: ["user.read"],
                prompt: "select_account"
            },
            myMSALObj: null
        }
    },
    methods: {
        isCloudLogin() {
            return this.authType == "azure_oauth";
        },
        submitForm() {
            this.showError = false;
            this.loading = true;
            if (this.$refs.form.validate()) {
                var ajax = axios({
                    method: 'post',
                    url: webAppRoot + "/validateUser",
                    params: {
                    },
                    data: {
                        username: this.username,
                        password: this.password
                    }
                });
                ajax.then(response => {
                    var data = response.data;
                    if (data.success) {
                        this.clear();
                        if (this.popup) {
                            bus.$emit("login-success");
                        }
                        else {
                            if (this.$route.query.urlRedirect) {
                                window.location = "./" + this.$route.query.urlRedirect;
                            }
                            else {
                                window.location = "./home";
                            }
                        }
                    }
                    else {
                        this.showErrorMessage(data.reason);
                        this.loading = false;
                    }
                })
                    .catch(error => {
                        this.loading = false;
                        console.log(error);
                    });
            }
        },
        submitToken(data) {
            this.loading = true;
            var ajax = axios({
                method: 'post',
                url: webAppRoot + "/validateUser",
                params: {
                },
                data: {
                    username: data.account.userName,
                    password: data.accessToken
                }
            });
            ajax.then(response => {
                var data = response.data;
                if (data.success) {
                    this.clear();
                    if (this.popup) {
                        bus.$emit("login-success");
                    }
                    else {
                        if (this.$route.query.urlRedirect) {
                            window.location = "./" + this.$route.query.urlRedirect;
                        }
                        else {
                            window.location = "./home";
                        }
                    }
                }
                else {
                    this.showErrorMessage(data.reason);
                    this.loading = false;
                }
            })
                .catch(error => {
                    this.loading = false;
                    console.log(error);
                });
        },
        clear() {
            this.$refs.form.reset()
            this.showError = false;
            this.loading = false;
        },
        showErrorMessage(reason) {
            this.errorReason = reason;
            this.showError = true;
        },
        handleDialogs(response) {
            // alert(response.data.reason);
            if (response.data.isXss) {
                console.log("xss detected:" + response.data.reason);
            }
            else {
                this.showErrorMessage(data.reason);
                this.loading = false;
            }
        },
        ///Azure///
        azureSignIn() {
            this.loading = true;
            this.myMSALObj.loginPopup(this.requestObj).then((loginResponse) => {
                //Successful login
                //Get the token to send to MS Graph
                this.acquireTokenForcePopup();
            }).catch(function (error) {
                //Please check the console for errors
                console.log(error);
            });
        },
        azureSignOut() {
            //clear Answer session and then disconnect from Azure
            this.loading = true;
            var ajax = axios({
                method: 'post',
                url: webAppRoot + "/logoutAjax",
                params: {
                },
                data: {
                }
            });
            ajax.then(response => {
                this.myMSALObj.logout();
                this.loading = false;
            })
                .catch(error => {
                    this.myMSALObj.logout();
                    this.loading = false;
                    console.log(error);
                });
        },
        acquireTokenForcePopup() {
            this.myMSALObj.acquireTokenPopup(this.requestObj).then((tokenResponse) => {
                this.submitToken(tokenResponse);
            }).catch(function (error) {
                console.log(error);
            });
        }
        ///Azure///

    },
    computed: {
        getUrlRedirect() {
            return urlRedirect;
        }
    },
    mounted() {
        this.myMSALObj = new Msal.UserAgentApplication(this.msalConfig);
    },
    created: function () {
    },
    destroyed: function () {
    }


})