

Vue.component('login-full-page2', {
    props: {
        dataUrlRoot: {default: webAppRoot, type: String}
    },
    template: /*html*/`<div>

  <v-toolbar fixed app flat extended class="hidden-sm-and-up" >
    <div class="toolbar-image">
    <img class="toolbar-image pt-1 pb-1" :src="dataUrlRoot + '/resources/images/answer-logo-icon-medium.png'" /> 
    </div>
    <v-toolbar-title class="headline">
      Answer
    </v-toolbar-title>
    <div class="toolbar-image margin-auto" slot="extension">
      <img class="toolbar-image pt-2 pb-2" alt="ngs logo"
        :src="dataUrlRoot + '/resources/images/screenshots/NGS_Lab_Color.png'">
      <img class="toolbar-image" alt="utsw master logo"
        :src="dataUrlRoot + '/resources/images/utsw-master-logo-lg.png'">
    </div>
    <v-spacer></v-spacer>
    <v-tooltip bottom>
    <v-btn slot="activator" flat @click="openGeneSearch()" dark class="teal lighten-2">Gene Search</v-btn>
    <span>Search and download<br/>genes available in our panels</span>
    </v-tooltip>
    <v-btn flat @click="doShowLoginDialog()" :disabled="!showLogin" dark class="teal lighten-2">Login</v-btn>
  </v-toolbar>

  <v-toolbar fixed app flat ref="regularToolbar" class="hidden-xs-only">
  <div class="toolbar-image">
    <img class="toolbar-image pt-1 pb-1" :src="dataUrlRoot + '/resources/images/answer-logo-icon-medium.png'" />
  </div>
  <v-toolbar-title class="headline">
    Answer
  </v-toolbar-title>
  <v-spacer></v-spacer>
  <div class="toolbar-image">
    <img class="toolbar-image pt-2 pb-2" alt="ngs logo"
      :src="dataUrlRoot + '/resources/images/screenshots/NGS_Lab_Color.png'">
    <img class="toolbar-image" alt="utsw master logo"
      :src="dataUrlRoot + '/resources/images/utsw-master-logo-lg.png'">
  </div>
  <v-tooltip bottom>
  <v-btn slot="activator" flat @click="openGeneSearch()" dark class="teal lighten-2">Gene Search</v-btn>
  <span>Search and download<br/>genes available in our panels</span>
  </v-tooltip>
  <v-btn flat @click="doShowLoginDialog()" :disabled="!showLogin" dark class="teal lighten-2">Login</v-btn>
</v-toolbar>

  <v-snackbar :timeout="0" :bottom="true" :value="snackBarVisible">
    {{ snackBarMessage }}
    <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
  </v-snackbar>

  <v-dialog v-model="showResetPasswordDialog" max-width="50%" scrollable>
    <v-card>
      <v-toolbar dense dark color="teal lighten-2">
        <v-toolbar-title class="white--text">Reset Your Password</v-toolbar-title>
      </v-toolbar>
      <v-card-title>Send a reset link to this email address:</v-card-title>
      <v-card-text>
        <v-layout row wrap class="pl-2">
          <v-flex xs12 lg6>
            <v-text-field label="email" v-model="email" required></v-text-field>
          </v-flex>
        </v-layout>
      </v-card-text>
      <v-card-actions>
        <v-btn class="mr-2" color="success" @click="sendResetPasswordEmail()" slot="activator" :disabled="!email">Send
          <v-icon right dark>email</v-icon>
        </v-btn>
        <v-btn class="mr-2" color="error" @click="cancelReset()">Cancel
          <v-icon right dark>cancel</v-icon>
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
  <v-container fluid grid-list-xl class="pt-0">
    <v-layout row wrap justify-center  pb-3 pt-0>
      <v-flex class="text-xs-center" xs12 pt-0>
      <v-img alt="answer title" max-height="150px"
      gradient="to top right, rgba(255,255,255,.75), rgba(255,255,255,.35)"
      :src="dataUrlRoot + '/resources/images/screenshots/banner.jpg'">
        <v-layout row wrap justify-center align-center fill-height mt-0>
            <v-flex class="title text-xs-center hidden-sm-and-up" xs12>
            {{ headerText }}
            </v-flex>
            <v-flex class="display-1 text-xs-center hidden-xs-only" xs8>
            {{ headerText }}
            </v-flex>
        </v-layout>
      </v-img>  
      </v-flex>
    </v-layout>
        
    <v-layout row wrap justify-center align-center pb-5>

    <v-flex xs12>
    <v-layout row wrap justify-center align-center pb-5>
      <v-flex xs12 >
          <v-card class="elevation-0 no-background pl-2 pr-2 pt-2 pb-2">
            <div class="title pb-3">
            <v-icon>mdi-play-box-outline </v-icon><span class="pl-2">Discover Answer's Features</span>
            </div>
            </v-card>
        </v-flex>

        <v-flex xs12 md11 lg9 xl7>
        <v-card>
        <v-card-text class="black">
        <video id='demo-video' class='video-js vjs-big-play-centered' controls preload='auto' 
        data-setup='{"fluid": true}' :poster="dataUrlRoot + '/resources/images/screenshots/answer-logo-large-background.png'">
          <source :src='currentVideoUrl' type='video/mp4'>
          <p class='vjs-no-js'>
            To view this video please enable JavaScript, and consider upgrading to a web browser that
            <a href='https://videojs.com/html5-video-support/' target='_blank'>supports HTML5 video</a>
          </p>
        </video>
         </v-card-text>
         </v-card>
        </v-flex>  

        <v-flex align-self-end>
        <v-menu slot="activator" offset-y>
          <template v-slot:activator="{ on }">
          <v-tooltip right>
            <v-btn slot="activator" icon flat v-on="on">
            <v-icon>copyright</v-icon>
            </v-btn>
            <span>Copyright Info</span>
            </v-tooltip>
            </template>
            <v-card class="pt-2 pb-2 pr-2 pl-2">
            <v-card-text>Music from <a href="https://filmmusic.io">https://filmmusic.io</a> <br/>
            "Climb" by Alexander Nakarada (<a href="https://www.serpentsoundstudios.com/">https://www.serpentsoundstudios.com/</a>) <br/>
            License: CC BY (<a href="http://creativecommons.org/licenses/by/4.0/">http://creativecommons.org/licenses/by/4.0/</a>) <br/>
            </v-card-text></v-card>
          </v-menu>
        </v-flex>  

    </v-layout>

    </v-flex>


    <v-flex xs12 v-for="(currentImgs, index) in allImages" :key="index" >

      <v-layout row wrap justify-center align-center pb-5>
        <v-flex xs12 :ref="refElements[index]">
          <v-card class="elevation-0 no-background pl-2 pr-2 pt-2 pb-2">
            <div class="title pb-3">
              <v-icon>{{ icons[index] }} </v-icon><span class="pl-2">{{ cards[index].text }}</span>
            </div>
            <div class="subheading" color="blue-grey lighten-2">
              {{ subTexts[index] }}
              <v-tooltip bottom v-if="cards[index].link" >
              <v-btn slot="activator" flat icon :href="links[index].url" class="table-btn pb-1">
                  <v-icon>{{ links[index].icon }}</v-icon>
                </v-btn>
              <span>{{ links[index].tooltip }} </span>  
              </v-tooltip>  
              </div>
            </v-card>
        </v-flex>

        <v-flex :class="flexClasses[index]" v-for="img in currentImgs" :key="img">
          <v-card >
              <v-img alt="snp filtering" :src="dataUrlRoot + '/resources/images/screenshots/' + img"></v-img>
            </v-card>
          </v-flex>
        </v-layout>
     </v-flex>
     </v-layout>   
  </v-container>

  <v-layout row justify-center pt-3 v-if="allowResetPwd">
    <v-flex xs12 md6 lg6 xl6 class="text-xs-center">
      <a @click="openResetPasswordDialog">Reset Password</a>
    </v-flex>
  </v-layout>
  <v-dialog v-model="showLoginDialog" max-width="300px">
    <v-card dark class="teal darken-2">
      <v-card-text class="text-xs-center">
        <login ref="loginBox" :message="message" :popup="false" :authType="authType"></login>
      </v-card-text>
    </v-card>
  </v-dialog>

  <v-btn fab bottom right fixed @click="scrollTo">
    <v-icon :class="scrollBarAtBottom ? 'rotate180' : ''">mdi-chevron-down</v-icon>
  </v-btn>

</div>`,
    data() {
        return {
            magicClass: "",
            elevation: "elevation-1",
            changingVersion: false,
            versionName: "1.0",
            message: authMessage,
            authType: authType,
            showResetPasswordDialog: false,
            email: "",
            snackBarVisible: false,
            snackBarMessage: "",
            allowResetPwd: false,
            showLogin: false,
            showLoginDialog: false,
            allImages: [
              ["filter1.png", "filter2.png", "filter3.png", "filter4.png"],
              ["igv.png"],
              ["snps.png", "cnv.png"],
              ["trials.png", "annotations.png"],
              ["report_html_1.png", "report_html_2.png", "report_pdf_1.png", "report_pdf_2.png"]
            ],
            flexClasses: [
              "xs12 md6 lg3 xl3",
              "xs12 md12 lg10 xl9",
              "xs12 md12 lg6 xl6",
              "xs12 md12 lg6 xl6",
              "xs12 md12 lg6 xl6",
            ],
            refElements: [
              "filters",
              "igv",
              "tables",
              "annotations",
              "reports"
            ],
            filterImgs: ["filter1.png", "filter2.png", "filter3.png", "filter4.png"],
            externalResoucesImgs: ["igv.png", "musica.png"],
            annotationImgs: ["trials.png", "annotations.png"],
            variantsCNVsImgs: ["snps.png", "cnv.png"],
            reportImgs: ["report_html_1.png", "report_html_2.png", "report_pdf_1.png", "report_pdf_2.png"],
            headerText: "Answer is a comprehensive tool to visualize and annotate variants for Clinical Reporting",
            cards:   [
                {text: "Filter thousands of variants", hover: false},
                {text: "Connect to outside resources", hover: false},
                {text: "Get detailed insights about each variant", hover: false},
                {text: "Annotations and Collaborations", hover: false},
                {text: "Meaningful Reports", hover: false, link: true},
            ],
            subTexts: [
                "Focus on impactful mutations with simple yet powerful filters.",
                "Open variants on external databases and visualizations such as the UCSC Genome Browser, ClinVar, COSMIC, IGV...",
                "Browse SNPs, Indels, CNS, Fusion and Translocations.",
                "Create, share, and browse annotations, and link mutations to existing clinical trials.",
                "Select variants and annotations to create meaningful reports that can be exported to PDFs."
            ],
            icons: [
                "mdi-magnify-plus-outline",
                "mdi-toolbox-outline",
                "mdi-eye",
                "mdi-message-reply-text",
                "mdi-pdf-box",

            ],
            links: [
              {},
              {},
              {},
              {},
              {url: this.dataUrlRoot + '/resources/files/PMF_Report_Redacted.pdf/',
               icon: "cloud_download", tooltip: "Download a report sample (PDF)"}
            ],
            carousel: [],
            currentIndex: -1,
            currentImgs: ["answer-logo-large.png"],
            currentFlex: "xs6 md6 lg3 xl3",
            currentVisibility: [true, true, true, true, true],
            timeoutId: null,
            showCarousel: true,
            hoverVideo: false,
            videoVisible: false,
            currentVideoUrl: "./media/demo.mp4",
            videoPlayer: null,
            scrollBarAtBottom: true
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
                        if (this.$route.query.urlRedirect) {
                            window.location = this.$route.query.urlRedirect;
                        }
                        else {
                            window.location = response.data.urlRedirect;
                        }
                    }
                    else {
                        this.showLogin = true;
                        this.message = this.message ? this.message : response.data.reason;
                        console.log(response.data.reason);
                        this.allowResetPwd = response.data.payload == "local";
                        this.authType = response.data.payload;
                    }
                }).catch(error => {
                });
        },
        switchToReleaseVersion() {
            this.magicClass = "magic-wand";
            this.changingVersion = true;
        },
        handleVersionChange() {
            if (this.changingVersion) {
                this.changingVersion = false;
                this.magicClass = "";
                if (this.$refs.goodiesPanel) {
                  this.$refs.goodiesPanel.createFireworks();
                  setTimeout(this.$refs.goodiesPanel.clearFireworks, 10000);
                }
                this.updateToVersion1();
            }
            else {
                if (Date.now() >= new Date("02/27/2019").getTime()) {
                    this.switchToReleaseVersion();
                }
            }
        },
        updateToVersion1() {
            axios.get(
                webAppRoot + "/updateVersion",
                {
                    params: {
                    }
                })
                .then(response => {
                    if (response.data.success) {
                        this.getVersion();
                    }
                    else {
                        console.log(response.data.reason);
                    }
                }).catch(error => {
                });
        },
        isBetaVersion() {
            return this.versionName == "beta";
        },
        isVersionOne() {
            return this.versionName == "1.0";
        },
        getVersion() {
			axios.get(webAppRoot + "/getCurrentVersion", {
				params: {}
			})
            .then(response => {
                if (response.data.isAllowed) {
                    this.versionName = response.data.payload;
                }
                else {
                    console.log(response.message);
                }
            })
            .catch(error => {
                alert(error);
            });
        },
        openResetPasswordDialog() {
            this.email = "";
            this.showResetPasswordDialog = true;
        },
        cancelReset() {
            this.showResetPasswordDialog = false;
        },
        sendResetPasswordEmail() {
            axios({
                method: 'post',
                url: webAppRoot + "/sendResetPasswordEmail",
                params: {
                    email: this.email,
                },
                data: {
                }
            }).then(response => {
                if (response.data.success) {
                    this.snackBarMessage = "The reset email was sent";
                    this.cancelReset();
                }
                else if (response.data.message) {
                    this.snackBarMessage = response.data.message;
                }
                else {
                    this.snackBarMessage = "Unknown email address or could not send email";
                }
                this.snackBarVisible = true;
            }
            ).catch(error => {
                alert(error);
            }
            );
        },
        playVideo() {
          this.videoVisible = true;
        },
        updateVideoLink() {
          if (!this.videoVisible) {
            this.videoPlayer.pause();
          }
          else {
            this.videoPlayer.play();
          }
        },
        onScroll(e) {
          if (this.timeoutId) {
            return;
          }
          this.scrollBarAtBottom = document.documentElement.scrollHeight <=  (document.documentElement.clientHeight + document.documentElement.scrollTop) * 1.10;
          // for (var i = 0; i < this.refElements.length; i++) {
          //   this.currentVisibility[i] = this.isElementInViewport(this.$refs[this.refElements[i]][0]);
          // }
          // console.log(this.currentVisibility);
        },
        // isElementInViewport(el) {
        //   var rect = el.getBoundingClientRect();
        //   return (
        //     rect.top >= 0 &&
        //     // rect.left >= 0 &&
        //     rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) 
        //     // && rect.right <= (window.innerWidth || document.documentElement.clientWidth)
        //   );
        // },
        scrollTo() {
          if (this.scrollBarAtBottom) {
            this.$vuetify.goTo(0);
          }
          else {
            this.$vuetify.goTo(9999);
          }
        },
        isXSWidth() {
          return window.outerWidth < 600;
        },
        doShowLoginDialog() {
          if (!this.showLogin) {
            return;
          }
          this.showLoginDialog = true;
          this.$nextTick(() => { this.$refs.loginBox.userNameFocus()});
        },
        openGeneSearch() {
          window.open("./search/index.html", "_blank");
        },
    },
    mounted: function () {
       window.HELP_IMPROVE_VIDEOJS = false;
       this.videoPlayer = videojs("demo-video", {}, function onPlayerReady() {
         console.log("ready");
       });
       if (this.$route.query.urlRedirect) {
        this.doShowLoginDialog();
       }
       
    },
    destroyed() {
      if (this.videoPlayer) {
          this.videoPlayer.dispose();
      }
      window.onscroll = null;
    },
    computed: {
      
    },
    created: function () {
        this.checkAlreadyLoggedIn();
        this.getVersion();
        window.onscroll = this.onScroll;
    },
    watch: {
      videoVisible: "updateVideoLink"
    }
})

