

Vue.component('login-ajax', {
    props: {
        message: { default: "Please log in using UTSW credentials", type: String },
        dataUrlRoot: {default: webAppRoot, type: String}
    },
    template: `<div>
    <v-layout row justify-center>
        <v-flex class="text-xs-center elevation-1 indigo" xs12 teal darken-2>
            <login :message="message" v-on:login-success="loginSuccess"></login>
        </v-flex>
    </v-layout>


</div>`,
    data() {
        return {

        }
    },
    methods: {
        loginSuccess() {
            this.$emit("login-success");
        }

    },
    computed: {

    },
    created: function () {

    }



})