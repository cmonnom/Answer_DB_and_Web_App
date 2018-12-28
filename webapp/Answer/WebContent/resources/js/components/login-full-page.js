

Vue.component('login-full-page', {
    props: {
        message: { default: "Please log in using UTSW credentials", type: String },
        dataUrlRoot: {default: webAppRoot, type: String}
    },
    template: `<div>
    <v-layout row justify-center>
        <v-flex class="text-xs-center elevation-1" xs12 md6 lg3 xl2>
        <div>
        <img :src="dataUrlRoot + '/resources/images/utsw-master-logo-lg.png'" alt="UTSW" width="100%" class="ml-0">
        <!-- <img :src="dataUrlRoot + '/resources/images/answer-logo-large-beta.png'" width="100%" alt="Answer" class="pl-4 pr-4 pb-3"/>   -->
        <img :src="dataUrlRoot + '/resources/images/answer-logo-large-beta.png'" width="100%" alt="Answer" class="pl-4 pr-4 pb-3"/>   
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
        }

    },
    mounted: function () {
       
    },
    computed: {

    },
    created: function () {
        this.checkAlreadyLoggedIn();
    }



})