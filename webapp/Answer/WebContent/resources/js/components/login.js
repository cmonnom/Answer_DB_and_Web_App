

Vue.component('login', {
    props: {
        message: { default: "Please log in using UTSW credentials", type: String },
        popup: { default: true, type: Boolean },
        dataUrlRoot: { default: webAppRoot, type: String }
    },
    template: `<div>
            <v-container>
                <v-form :value="formValid" @submit.prevent ref="form" lazy @submit="submitForm">
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
            loading: false
        }
    },
    methods: {
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
        }

    },
    computed: {
        getUrlRedirect() {
            return urlRedirect;
        }
    },
    created: function () {

    }



})