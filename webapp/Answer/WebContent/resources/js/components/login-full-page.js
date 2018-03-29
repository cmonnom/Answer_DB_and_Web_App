

Vue.component('login-full-page', {
    props: {
        message: { default: "Please log in using UTSW credentials", type: String },
        dataUrlRoot: {default: webAppRoot, type: String}
    },
    template: `<div>
    <v-layout row justify-center>
        <v-flex class="text-xs-center elevation-1" xs12 sm8 md4 lg3 xl2>
        <img :src="dataUrlRoot + '/resources/images/utsw-master-logo-lg.png'" alt="UTSW" width="100%" class="ml-0">
        <div class="title grey--text text--darken-2 pb-3"><strong>Answer NGS</strong><br/>
        <b>CL</b>inical <b>e</b>lectronic <b>A</b>nnotation <b>R</b>eporting Tool<br/>
        for <b>N</b>ext <b>G</b>en. <b>S</b>equencing</div>
        </v-flex>
    </v-layout> 
    <v-layout row justify-center>
        <v-flex class="text-xs-center elevation-1 teal darken-2" xs12 sm8 md4 lg3 xl2>
            <login :message="message" :popup="false"></login>
        </v-flex>
    </v-layout>


</div>`,
    data() {
        return {

        }
    },
    methods: {
        

    },
    computed: {

    },
    created: function () {

    }



})