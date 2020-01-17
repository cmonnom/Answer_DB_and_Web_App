

Vue.component('error-handler', {
    props: {
        
    },
    template: `<div>
    <v-dialog v-model="showDialog" max-width="500px">
    <v-card>
    <v-card-text class="pt-3 pb-3 pl-3 pr-3">{{ errorMessage }}</v-card-text>
    </v-card>
    </v-dialog>
    <v-dialog v-model="showLoginDialog" max-width="300px">
        <login-ajax></login-ajax>
    </v-dialog>
</div>`,
    data() {
        return {
            showDialog: false,
            errorMessage: null,
            showLoginDialog: false,
            callBacks: [],
            busy: false
        }
    },
    methods: {
        handleError(args) {
            if (args == null || args[1] == null) {
                this.errorMessage = "Something went wrong";
            }
            else {
                this.errorMessage = args[1].message ? args[1].message : args[1];
            }
            this.showDialog = true;
        },
        handleLoginNeeded(args) {
            this.callBacks.push(args[1]);
            this.showLoginDialog = true;
        },
        doCallBacks() {
            this.busy = true; //prevents other events
            this.showLoginDialog = false;
            // console.log(this.callBacks);
            for (var i = 0; i < this.callBacks.length; i++) {
                var callBack = this.callBacks[i];
                if (callBack) {
                    callBack();
                }
            }
            this.callBacks = [];
            busy = false; 
        }
    },
    computed: {

    },
    created: function () {
        //just a test example. Do no display a popup when user enters invalid characters
        bus.$on('xss-error', args => {
            if (args) {
                this.handleError(args);
            }
        });
        bus.$on('some-error', args => {
            if (args) {
                this.handleError(args);
            }
        });
        bus.$on('login-needed', args => {
            if (args) {
                this.handleLoginNeeded(args);
            }
        });
        bus.$on('login-success', () => {
            this.doCallBacks();
        });
        bus.$on('not-allowed', () => {
            this.handleError([null,"You are not allowed to perform this action"]);
        });
    }



})