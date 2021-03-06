Vue.component('splash-screen', {
    props: {
        splashDialog: { default: true, type: Boolean },
    },
    template: `
    <v-fade-transition >
    <div class="splash-screen" v-if="splashDialog">
    <v-fade-transition >
    <v-layout align-center justify-center row fill-height class="splash-screen-item"  v-show="showFab">
    <span class="subheading splash-text">{{ splashTextCurrent }}</span>
    <v-btn fixed dark fab top left flat color="primary" class="custom-loader"
    >
      <img
      :src="loadingImageUrl()" alt="loading page" width="100%"/>
    </v-btn>
    </v-layout>
    </v-fade-transition >
  </div>
  </v-fade-transition >`,
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
                "Reviewing Reviewers...",
                "Thinking...",
                "Loading Environment...",
                "Almost There..."
            ],
            showFab: false
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
            return getDialogMaxHeightOuter(-200) + "left: -200px;top: -96px";
        },
        loadingImageUrl() {
            return webAppRoot + "/resources/images/answer-logo-icon-small.png";
        }
    },
    computed: {
    },
    mounted() {
        this.showFab = true;
    },
    created() {
    },
    watch: {
    }


});