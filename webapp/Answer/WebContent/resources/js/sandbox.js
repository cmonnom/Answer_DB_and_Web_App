const Sandbox = {
    props: {
    },
    template: `<div>

    <v-toolbar dense dark color="primary" fixed app>
    <v-toolbar-title class="white--text ml-0">
    Sandbox (not for production use)
    </v-toolbar-title>
    </v-toolbar>

    <sandbox-cnv-plot>
    </sandbox-cnv-plot>
</div>
`, data() {
        return this.initData();
    }, methods: {
        initData() {
            return {
                numberRules: [(v) => { return !isNaN(v) || 'Invalid value' }],
                colors: {
                    openCase: "primary",
                    variantDetails: "primary",
                    saveReview: "primary",
                    editAnnotation: "primary"
                },
                snackBarVisible: false,
                snackBarLinkIcon: "",
                snackBarLink: "",
                snackBarTimeout: 4000,
                snackBarMessage: "",
                userId: null,
            }
        },
        canProceed(field) {
            if (isAdmin) {
                return true;
            }
            switch (field) {
                case "canAnnotate": return permissions.canAnnotate;
                case "canSelect": return permissions.canSelect;
                case "canView": return permissions.canView;
                case "canReview": return permissions.canReview;
                case "canHide": return permissions.canHide;
                default: return false;
            }
        },
        handleDialogs(response, callback) {
            if (response == "not-allowed") {
                bus.$emit("not-allowed", [this.response]);
            }
            if (response.isXss) {
                bus.$emit("xss-error", [this, response.reason]);
            }
            else if (response.isLogin) {
                bus.$emit("login-needed", [this, callback])
            }
            else if (response.success === false) {
                bus.$emit("some-error", [this, response.message]);
            }
            // this.splashProgress = 100; //should dismiss the splash dialog
            // this.waitingForAjaxMessage = "There were some errors while saving";
            // this.waitingForAjaxActive = false; //stops spinning wheel if error
        },
        handleAxiosError(error) {
            console.log(error);
            // this.splashProgress = 100;
            bus.$emit("some-error", [this, error]);
            // this.waitingForAjaxMessage = "There were some errors while saving";
        },
        showSnackBarMessage(message) {
            this.snackBarMessage = message;
            this.snackBarLink = "";
            this.snackBarLinkIcon = "";
            this.snackBarTimeout = 4000;
            this.snackBarVisible = true;
        },
        showSnackBarMessageWithParams(snackBarMessage, snackBarLink, snackBarLinkIcon, snackBarTimeout) {
            this.snackBarMessage = snackBarMessage;
            this.snackBarLink = snackBarLink;
            this.snackBarLinkIcon = snackBarLinkIcon;
            this.snackBarTimeout = snackBarTimeout != null ? snackBarTimeout : 4000;
            this.snackBarVisible = true;
        },
        mountComponent() {
            this.getCNVChromList();
        },
        openLink(link) {
            window.open(link, "_blank", 'noopener');
        },
    },
    mounted() {
    },
    created() {
    },
    computed: {
        webAppRoot() {
            return webAppRoot;
        },
    },
    destroyed: function () {
    },
    watch: {
    }

};
