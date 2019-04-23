

Vue.component('reset-password', {
    props: {
        dataUrlRoot: {default: webAppRoot, type: String}
    },
    template: `<div :class="[backgroundClass ]">
    <v-layout row justify-center>
    <v-flex :class="['text-xs-center', 'elevation-1']" xs12 md6 lg3 xl2>
    <div class="plain-light-background">
    <img alt="utsw master logo" :src="dataUrlRoot + '/resources/images/utsw-master-logo-lg.png'" width="100%" class="ml-0">
    <img alt="answer logo" :src="dataUrlRoot + '/resources/images/answer-logo-large.png'" width="100%" class="pl-4 pr-4 pb-3"/>     
    <b>An</b>notation <b>S</b>oft<b>w</b>are for <b>E</b>lectronic <b>R</b>eporting
    </div>
    </v-flex>
</v-layout> 
<v-layout row justify-center>
    <v-flex class="text-xs-center elevation-1 teal darken-2" xs12 md6 lg3 xl2>
    <v-container>
        <v-form v-model="formValid" @submit.prevent ref="form" lazy @submit="submitForm">
                <div class="text-xs-center white--text">
                    {{ message }}
                </div>

            <div class="text-xs-center">
            <v-text-field label="Email" 
            v-model="email" required autofocus
            dark color="white"></v-text-field>
            <v-tooltip top>
                <v-text-field label="New password" slot="activator"
                v-model="password" 
                required
                :rules="passwordRules"
                dark
                color="white"
                :append-icon="showPasswordIcon ? 'visibility' : 'visibility_off'" 
                :append-icon-cb="() => (showPasswordIcon = !showPasswordIcon)"
                :type="showPasswordIcon ? 'password' : 'text'"
                ></v-text-field>
             <span v-html="helpPasswordRules()"></span>
             </v-tooltip>   
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
    </v-flex>
    </v-layout>

</div>`,
    data() {
        return {
            message: "Enter your current email and new password",
            formValid: true,
            backgroundClass: 'plain-light-background',
            loading: false,
            errorReason: '',
            showError: false,
            email: "",
            showPasswordIcon: true,
            password: "",
            passwordRules: [(v) => { return this.validatePassword(v) || "Invalid Password" }],
            passedRules: {
                length: false,
                upper: false,
                lower: false,
                digit: false,
                special: false
            }
        }
    },
    methods: {
        submitForm() {
            this.showError = false;
            this.loading = true;
            if (this.$refs.form.validate()) {
                var ajax = axios({
                    method: 'post',
                    url: webAppRoot + "/updatePassword",
                    params: {
                        token: this.$route.query.token
                    },
                    data: {
                        username: this.email,
                        password: this.password
                    }
                });
                ajax.then(response => {
                    var data = response.data;
                    if (data.success) {
                        this.clear();
                        window.location = "./home";
                    }
                    else {
                        this.showErrorMessage(data.message);
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
        },
        validatePassword(v) {
            return true;
            this.passedRules.length = v.length >= 8;
            this.passedRules.upper = v.match(/[A-Z]+/g) != null;
            this.passedRules.lower = v.match(/[a-z]+/g) != null;
            this.passedRules.digit = v.match(/[0-9]+/g) != null;
            this.passedRules.special = v.match(/[!@#$%^&*()+=_]+/g) != null;
            var count = this.countPassedRules();
            var valid = this.passedRules.length && count >= 3;
            return valid;
        },
        countPassedRules() {
            var count = 0;
            count += this.passedRules.upper ? 1 : 0;
            count += this.passedRules.lower ? 1 : 0;
            count += this.passedRules.digit ? 1 : 0;
            count += this.passedRules.special ? 1 : 0;
            return count;
        },
        helpPasswordRules() {
            var count = this.countPassedRules();
            return "<ul class='pl-2'>" 
            + "<li class='" + (this.passedRules.length ? "strike" : "") + "'>Password must be a minimum of eight (8) characters in length</li>"
            + "<li class='" + (count >= 3 ? "strike" : "") + "'>AND Contain at least one character from 3 out of 4 of the following categories:</li>"
            + "<ul class='pl-3'>"
            + "<li class='" + (this.passedRules.upper ? "strike" : "") + "'>Uppercase letter (A-Z)</li>"
            + "<li class='" + (this.passedRules.lower ? "strike" : "") + "'>Lowercase letter (a-z)</li>"
            + "<li class='" + (this.passedRules.digit ? "strike" : "") + "'>Digit (0-9)</li>"
            + "<li class='" + (this.passedRules.special ? "strike" : "") + "'>Special character (!@#$%^&*()+=_)</li></ul>";
        }

    },
    mounted: function () {
       
    },
    computed: {
    },
    created: function () {
    }



})