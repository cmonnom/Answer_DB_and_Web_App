Vue.component('splash-screen', {
    props: {
        splashDialog: { default: true, type: Boolean },
    },
    template: `<div class="splash-screen" v-if="splashDialog">
    <v-layout align-center justify-center row fill-height class="splash-screen-item">
	<span class="subheading">{{ splashTextCurrent }}</span>
  </v-layout>
  </div>`,
    data() {
        return {
            splashTextCurrent: "Warming Up...",
            splashTextItems: [
                "Acquiring Patient Data...",
                "Spellchecking Annotations...",
                "Formatting Input...",
                "Initializing User Permissions...",
                "Rendering Page...",
                "Coloring Icons...",
                "Loading User Preferences...",
                "Dusting Off Variants",
                "Translocating Translocations",
                "Reviewing Reviewers..."
            ],
        }

    },
    methods: {
        createSplashText() {
            var newText = "";
            while (newText == "" || this.splashTextCurrent == newText) {
                newText = this.splashTextItems[Math.floor(Math.random() * this.splashTextItems.length)]
            }
            this.splashTextCurrent = newText;
        },
        manageSplashScreen() {
            if (this.splashDialog) {
                splashInterval = setInterval(() => {
                    this.splashTextVisible = !this.splashTextVisible;
                    if (this.splashTextVisible) {
                        this.createSplashText();
                    }
                }
                    , 750);
                document.querySelector(".splash-screen").style = this.getSplashScreenMaxHeightTopLeft();
            }
        },
        getSplashScreenMaxHeightTopLeft() {
            return getDialogMaxHeightOuter(0) + "left: -200px;top: -96px";
        },
    },
    computed: {
    },
    created() {
    },
    watch: {
    }


});