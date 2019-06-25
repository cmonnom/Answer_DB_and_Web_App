const AnnotationBrowser = {
  template:
    `<div>

    <!-- goodies panel dialog -->
    <v-dialog v-model="showGoodiesPanel" content-class="no-transition" full-width hide-overlay fullscreen>
    <v-layout row wrap fill-height text-xs-center>
    <v-flex xs12>
    <goodies2 ref="goodiesPanel" @end-goodies="showGoodiesPanel = false"></goodies2>
    </v-flex>
    <v-flex xs12 >
    <span class="display-1">Enjoy a quick break...</span><br/>
    <v-btn large color="warning" @click="showGoodiesPanel = false">Back to work</v-btn>
    </v-flex>
    </v-layout>
    </v-dialog>

    <v-snackbar :timeout="4000" :bottom="true" :value="snackBarVisible">
        {{ snackBarMessage }}
        <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
    </v-snackbar>

    <!-- annotation dialog -->
    <edit-annotations type="snp" @saving-annotations="commitCaseAgnosticAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs"
        color="primary" ref="annotationDialog" title=""
        @toggle-panel="handlePanelVisibility()" :annotation-variant-details-visible="editAnnotationVariantDetailsVisible"
        :current-variant="currentVariant"
        :single="singleAnnotation"        
        :annotation-categories="annotationCategories" :annotation-tiers="variantTiers" :annotation-classifications="annotationClassifications"
        :annotation-phases="annotationPhases">
        </edit-annotations>

    <v-toolbar dense dark color="primary" fixed app>
        <v-menu offset-y offset-x class="ml-0">
            <v-btn slot="activator" flat icon dark>
                <v-icon color="amber accent-2">mdi-message-bulleted</v-icon>
            </v-btn>
            <v-list>
                <v-list-tile avatar @click="startUserAnnotations()">
                    <v-list-tile-avatar>
                        <v-icon>search</v-icon>
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title>Create New Annotations</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>

            </v-list>
        </v-menu>
        <v-toolbar-title class="ml-0">
        <v-layout row align-center>
          <v-flex xs12>
            UTSW Annotations
          </v-flex>
          <v-flex xs12 offset-xs2 offset-md3 offset-lg5 offset-xl8>
            <v-tabs grow slot="extension" dark slider-color="warning" color="primary" fixed-tabs v-model="annotationTabActive">
            <v-tab href="#tab-trial" :ripple="false">
                Clinical Trials
            </v-tab>
            <v-tab href="#tab-snp" :ripple="false">
                SNP / Indel
            </v-tab>
            <v-tab href="#tab-genes" :ripple="false">
                Gene Sets
            </v-tab>
            </v-tabs>
          </v-flex>
        </v-layout>
        </v-toolbar-title>
       
        <v-spacer></v-spacer>

        <v-tooltip bottom>
            <v-btn flat icon @click="startUserAnnotations()" slot="activator">
                <v-icon>playlist_add</v-icon>
            </v-btn>
            <span>Create New Annotations</span>
        </v-tooltip>
    </v-toolbar>
    
            <v-tabs-items v-model="annotationTabActive">
            <!-- Trials -->
                <v-tab-item value="tab-trial">
            <v-container grid-list-md fluid pt-0 pl-1 pr-1>
                <v-layout row wrap>
                    <v-slide-y-transition>
                        <v-flex xs12 v-show="searchTrialAnnotationsVisible">
                            <v-card>
                                <v-card-text class="pl-2 pr-2 pb-3 subheading">
                                    <v-layout row wrap>
                                        <v-flex xs12>
                                        Clinical Trial Annotations: <span v-text="formatTrialCount()"></span>
                                        </v-flex>
                                        <v-flex xs pr-3 pt-4>
                                        Search Clinical Trial Cards:
                                        </v-flex>
                                        <v-flex xs6 sm6 md3 lg3 xl2>
                                            <v-text-field clearable append-icon="search" label="Search any text"
                                            autofocus ref="annotationTextSearch" hide-details
                                                v-model="searchTrialAnnotations" @input="matchTrialAnnotationFilter"></v-text-field>
                                        </v-flex>
                                    </v-layout>
                                </v-card-text>
                            </v-card>
                        </v-flex>
                    </v-slide-y-transition>
                    <v-flex xs12 sm12 md6 lg6 xl4 v-for="(annotation, index) in trialAnnotationsFormatted" :key="index"
                        v-show="annotation.visible">
                        <utsw-annotation-card :annotation="annotation" :no-edit="!canProceed('canAnnotate')"
                            :case-agnostic="true" 
                            @start-user-annotation="startUserAnnotations"></utsw-annotation-card>
                    </v-flex>
                </v-layout>
            </v-container>
            </v-tab-item>
            <!-- SNPs -->
            <v-tab-item value="tab-snp">
            <v-container grid-list-md fluid pt-0 pl-1 pr-1>
                <v-layout row wrap>
                    <v-slide-y-transition>
                        <v-flex xs12 v-show="searchSNPAnnotationsVisible">
                            <v-card>
                                <v-card-text class="pl-2 pr-2 pb-3 subheading">
                                    <v-layout row wrap>
                                    <v-flex xs12>
                                    <span v-show="snpGeneSearch">SNP Annotations for <b>{{ snpGeneSearch }}</b>: <span v-text="formatSNPCount()"></span></span>
                                    <span v-show="!snpGeneSearch">Select a gene to show its annotations</b></span>
                                    </v-flex>
                                    <v-flex xs12 md6 pt-3 mt-1>
                                    <v-layout row wrap>
                                        <v-flex xs pr-3 mt-1>
                                        Narrow by Gene:
                                        </v-flex>
                                        <v-flex xs6>
                                        <v-tooltip right>
                                        <v-autocomplete slot="activator" clearable :value="snpGeneSearch" :items="allGenes" v-model="snpGeneSearch"
                                            label="Gene Symbol" hide-details clearable
                                            item-text="name" item-value="value" autofocus
                                            class="no-height-select" @input="getAllSNPsForGene(snpGeneSearch)"
                                            ></v-autocomplete>
                                            <span>Select a gene</span>
                                        </v-tooltip>
                                        </v-flex>
                                    </v-layout>
                                    </v-flex>
                                      <v-flex xs12 md6>
                                      <v-layout row wrap>
                                          <v-flex xs pr-3 pt-4>
                                          Search SNPs/Indel Cards:
                                          </v-flex>
                                          <v-flex xs6>
                                              <v-text-field clearable append-icon="search" label="Search any text"
                                              ref="annotationSNPTextSearch" hide-details
                                                  v-model="searchSNPAnnotations" @input="matchSNPAnnotationFilter"></v-text-field>
                                          </v-flex>
                                      </v-layout>
                                      </v-flex>
                                    </v-layout>
                                </v-card-text>
                            </v-card>
                        </v-flex>
                    </v-slide-y-transition>
                    <v-flex xs12 sm12 md6 lg6 xl4 v-for="(annotation, index) in snpAnnotationsFormatted" :key="index"
                        v-show="annotation.visible">
                        <utsw-annotation-card :annotation="annotation" :no-edit="!canProceed('canAnnotate')"
                            :case-agnostic="true" 
                            @start-user-annotation="startUserAnnotations"></utsw-annotation-card>
                    </v-flex>
                </v-layout>
            </v-container>
            </v-tab-item>
            <!-- Gene sets -->
            <v-tab-item value="tab-genes">
              <gene-sets-edit></gene-sets-edit>
            </v-tab-item>
            </v-tabs-items>

</div>`,
  data() {
    return {
      snackBarVisible: false,
      snackBarMessage: "",
      searchTrialAnnotationsVisible: true,
      searchSNPAnnotationsVisible: true,
      trialAnnotations: [],
      trialAnnotationsFormatted: [],
      snpAnnotations: [],
      snpAnnotationsFormatted: [],
      searchTrialAnnotations: "",
      searchSNPAnnotations: "",
      currentVariant: {},
      editAnnotationVariantDetailsVisible: false,
      urlQuery: {
        annotationId: null,
        variantType: null,
        edit: false
      },
      singleAnnotation: false,
      currentAnnotation: {},
      waitingForGoodies: false,
      showGoodiesPanel: false,
      allGenes: [],
      variantsForGene: [],
      annotationTabActive: "tab-trial",
      allGenes: [],
      snpGeneSearch: null,
      variantTiers: [
        '1A',
        '1B',
        '2C',
        '2D',
        '3',
        '4'
        ],
    annotationCategories: [
        'Gene Function',
        'Epidemiology',
        'Variant Function',
        'Prognosis',
        'Diagnosis',
        'Therapy'],
    annotationClassifications: [
        'VUS',
        'Benign',
        'Likely benign',
        'Likely pathogenic',
        'Pathogenic'],
    annotationPhases: ["Phase 1", "Phase 2", "Phase 3", "Phase 4"],
    scopesSNP: [
        'Case', 'Gene', 'Variant', 'Tumor'
    ],
    }
  },
  methods: {
    getAllGenes() { 
      axios.get("./getGenesInPanel", {
        params: {
        }
      })
        .then(response => {
          if (response.data.isAllowed) {
            this.allGenes = response.data.items;
          }
          else {
            this.handleDialogs(response.data, this.getGenesInPanel);
          }
        })
        .catch(error => {
          alert(error);
        });
    },
    getAllTrials() {
      axios.get("./getAllClinicalTrials", {
        params: {
        }
      })
        .then(response => {
          if (response.data.isAllowed && response.data.success) {
            this.trialAnnotations = response.data.payload;
            this.formatTrialAnnotations();
            this.loadFromParams();
          }
          else {
            this.handleDialogs(response.data, this.getAdmins);
          }
        })
        .catch(error => {
          alert(error);
        });
    },
    getAllSNPsForGene() {
      if (!this.snpGeneSearch) {
        this.snpAnnotations = [];
        this.formatSNPAnnotations();
        return;
      }
      axios.get("./getAllSNPsForGene", {
        params: {
          geneId: this.snpGeneSearch
        }
      })
        .then(response => {
          if (response.data.isAllowed && response.data.success) {
            this.snpAnnotations = response.data.payload;
            this.formatSNPAnnotations();
            this.loadFromParams();
          }
          else {
            this.handleDialogs(response.data, this.getAdmins);
          }
        })
        .catch(error => {
          alert(error);
        });
    },
   
    handleDialogs(response, callback) {
      if (response.isXss) {
        bus.$emit("xss-error", [this, response.reason]);
      }
      else if (response.isLogin) {
        bus.$emit("login-needed", [this, callback])
      }
      else if (response.success === false) {
        bus.$emit("some-error", [this, response.message]);
      }
    },
    matchTrialAnnotationFilter() {
      if (this.searchTrialAnnotations) { //only one search field for now. Add more here
        for (var i = 0; i < this.trialAnnotationsFormatted.length; i++) {
          var foundTextMatch = false;
          if (this.searchTrialAnnotations) { //handle each field in a separate if
            var json = JSON.stringify(this.trialAnnotationsFormatted[i]).toLowerCase();
            if (json.indexOf(this.searchTrialAnnotations.toLowerCase()) > -1) {
              foundTextMatch = true;
            }
          }
          else {
            foundTextMatch = true;
          }
          this.trialAnnotationsFormatted[i].visible = foundTextMatch;
        }
      }
      else {
        for (var i = 0; i < this.trialAnnotationsFormatted.length; i++) {
          this.trialAnnotationsFormatted[i].visible = true;
        }
      }
    },
    formatTrialAnnotations() {
      this.trialAnnotationsFormatted = [];
      this.trialAnnotationsFormatted = this.trialAnnotationsFormatted.concat(this.formatLocalAnnotations(this.trialAnnotations));
    },
    matchSNPAnnotationFilter() {
      if (this.searchSNPAnnotations) { //only one search field for now. Add more here
        for (var i = 0; i < this.snpAnnotationsFormatted.length; i++) {
          var foundTextMatch = false;
          if (this.searchSNPAnnotations) { //handle each field in a separate if
            var json = JSON.stringify(this.snpAnnotationsFormatted[i]);
            if (json.indexOf(this.searchSNPAnnotations) > -1) {
              foundTextMatch = true;
            }
          }
          else {
            foundTextMatch = true;
          }
          this.snpAnnotationsFormatted[i].visible = foundTextMatch;
        }
      }
      else {
        for (var i = 0; i < this.snpAnnotationsFormatted.length; i++) {
          this.snpAnnotationsFormatted[i].visible = true;
        }
      }
    },
    formatSNPAnnotations() {
      this.snpAnnotationsFormatted = [];
      this.snpAnnotationsFormatted = this.snpAnnotationsFormatted.concat(this.formatLocalAnnotations(this.snpAnnotations));
    },
    formatLocalAnnotations(annotations) {
      var formatted = [];
      for (var i = 0; i < annotations.length; i++) {
        var annotation = {
          _id: "",
          fullName: "",
          text: "",
          scopes: [],
          scopeLevels: [],
          scopeTooltip: "",
          tumorSpecific: "",
          category: "",
          createdDate: "",
          createdSince: "",
          modifiedDate: "",
          modifiedSince: "",
          pmids: [],
          // nctIds: [],
          tier: "",
          classification: "",
          visible: true,
          isSelected: false,
          trial: null,
          drugs: "",
          canEdit: false,
          variantItems: [],
          warningLevel: 0,
          drugResistant: false
        };
        annotation._id = annotations[i]._id;
        annotation.fullName = annotations[i].fullName;
        annotation.text = annotations[i].text.replace(/\n/g, "<br/>");
        annotation.scopes = [annotations[i].isCaseSpecific, annotations[i].isGeneSpecific, annotations[i].isVariantSpecific, annotations[i].isTumorSpecific];
        annotation.scopeLevels = ["Case " + (annotations[i].isCaseSpecific ? annotations[i].caseId : ''),
        "Gene " + (annotations[i].isGeneSpecific ? annotations[i].geneId : ''),
        "Variant " + (annotations[i].isVariantSpecific ? annotations[i].notation : ''),
          "Diagnosis"];
        annotation.category = annotations[i].category;
        annotation.createdDate = annotations[i].createdDate;
        annotation.createdSince = annotations[i].createdSince;
        annotation.modifiedDate = annotations[i].modifiedDate;
        annotation.modifiedSince = annotations[i].modifiedSince;
        annotation.pmids = annotations[i].pmids;
        // annotation.nctIds = annotations[i].nctIds;
        annotation.scopeTooltip = this.$refs.annotationDialog.createLevelInformation(annotations[i]);
        annotation.tier = annotations[i].tier;
        annotation.classification = annotations[i].classification;
        annotation.isSelected = annotations[i].isSelected;
        annotation.trial = annotations[i].trial;
        annotation.drugs = annotations[i].drugs;
        annotation.canEdit = annotations[i].canEdit;
        annotation.warningLevel = annotations[i].warningLevel;
        annotation.drugResistant = annotations[i].drugResistant;
        formatted.push(annotation);
      }
      return formatted;
    },
    startUserAnnotations(annotation) {
      if (!this.canProceed('canAnnotate')) {
        return;
      }
      //get current variant from annotation if available
      this.initEditAnnotation(annotation);
    },
    handlePanelVisibility(visible) {
      if (visible == null && this.urlQuery.edit) {
        this.editAnnotationVariantDetailsVisible = !this.editAnnotationVariantDetailsVisible;
      }
      else if (this.urlQuery.edit) {
        this.editAnnotationVariantDetailsVisible = visible;
      }
    },
    updateEditAnnotationBreadcrumbs(visible) {
      this.urlQuery.edit = visible;
      this.updateRoute();
    },
    updateRoute() {
      router.push({ query: this.urlQuery });
    },
    commitCaseAgnosticAnnotations(userAnnotations) {
            axios({
                method: 'post',
                url: webAppRoot + "/commitCaseAgnosticAnnotations",
                params: {
                },
                data: {
                    annotations: userAnnotations,
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        if (response.data.payload.annotationModified) {
                            if (response.data.userPrefs && response.data.userPrefs.showGoodies) {
                                this.waitingForGoodies = true;
                            }
                            this.snackBarMessage = "Annotation(s) Saved";
                            this.snackBarLink = "";
                        }
                        else {
                            this.snackBarMessage = "No Change Detected";
                            this.snackBarLink = "";
                        }
                        this.getAllTrials();
                        this.$nextTick(() => {
                            this.$refs.annotationDialog.cancelAnnotations();
                            this.snackBarVisible = true;
                            if (this.waitingForGoodies) {
                                this.waitingForGoodies = false;
                                this.snackBarTimeout = 4000;
                                setTimeout(() => {
                                    this.showGoodiesPanel = true;
                                    this.$refs.goodiesPanel.activateGoodies();
                                }, this.snackBarTimeout); //4sec might help with having the proper top/left calculated
                            }
                        });
                    } else {
                        this.handleDialogs(response.data, this.commitCaseAgnosticAnnotations.bind(null, userAnnotations));
                    }
                })
                .catch(error => {
                    this.handleAxiosError(error);
                });
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
        default: return false;
      }
    },
    handleRouteChanged(newRoute, oldRoute) {
      if (newRoute.path != oldRoute.path) { //prevent reloading data if only changing the query router.push({query: {test:"hello3"}})
        // this.getAjaxData();
      }
      else { //look at the query
        // console.log(newRoute.query, oldRoute.query);
        var newRouteQuery = JSON.stringify(newRoute.query);
        var oldRouteQuery = JSON.stringify(oldRoute.query);
        if (newRouteQuery != oldRouteQuery) { //some params changed
          this.loadFromParams(newRoute.query, oldRoute.query);
        }
      }
    },
    loadFromParams(newRouteQuery, oldRouteQuery) {
      this.urlQuery.annotationId = this.$route.query.annotationId ? this.$route.query.annotationId : null;
      this.urlQuery.variantType = 'snp'
      this.urlQuery.edit = this.$route.query.edit === true || this.$route.query.edit === "true";

      if (!this.urlQuery.edit) { //close edit annotation
        this.$refs.annotationDialog.cancelAnnotations();
      }
      else if (this.urlQuery.edit && this.urlQuery.annotationId) {
        this.getAnnotationDetails(this.urlQuery.annotationId);
        //finally, open edit annotation
      }
      this.handleEditAnnotationOpening();
      this.toggleHTMLOverlay();
    },
    getAnnotationDetails(annotationId) {
      var found = false;
      for (var i = 0; i < this.trialAnnotations.length; i++) {
        if (this.trialAnnotations[i]._id.$oid == annotationId) {
          found = true;
          this.initEditAnnotation(this.trialAnnotations[i]);
          break;
        }
      }
      for (var i = 0; i < this.snpAnnotations.length; i++) {
        if (this.snpAnnotations[i]._id.$oid == annotationId) {
          found = true;
          this.initEditAnnotation(this.snpAnnotations[i]);
          break;
        }
      }
      
    },
    initEditAnnotation(annotation) {
      this.currentAnnotation = annotation;
      if (annotation) {
        this.$refs.annotationDialog.userAnnotations = [this.currentAnnotation];
        if (annotation.isVariantSpecific) {
          this.$refs.annotationDialog.getVariantsForGene(annotation.geneId, annotation);
        }
        this.annotationId = this.currentAnnotation._id.$oid;
        this.singleAnnotation = true;
        
      }
      else {
        this.$refs.annotationDialog.userAnnotations = [];
        this.annotationId = "";
        this.singleAnnotation = false;
      }
      this.urlQuery.annotationId =  this.annotationId;
      this.urlQuery.edit = true;
      this.updateRoute();
    },
    handleAxiosError(error) {
      console.log(error);
      bus.$emit("some-error", [this, error]);
    },
    handleEditAnnotationOpening() {
      if (this.urlQuery.edit === true) {
        setTimeout(() => {
          this.$refs.annotationDialog.startUserAnnotations();
        }, 1000);
      }
    },
    toggleHTMLOverlay() {
      var html = document.querySelector("html");
      if (this.urlQuery.edit) {
          html.style.overflow = "hidden";
      }
      else {
          html.style.overflow = "";
      }
  },
  handleSearchFocus() {
    if (this.searchTrialAnnotationsVisible) {
      this.$nextTick(this.$refs.annotationTextSearch.focus);
    }
  },
  countVisible(annotations) {
    if (annotations) {
      return annotations.filter(a => a.visible).length;
    }
    return 0;
  },
  formatSNPCount() {
    var count = this.countVisible(this.snpAnnotationsFormatted);
    return this.formatCount(count, this.snpAnnotationsFormatted);
  },
  formatTrialCount() {
    var count = this.countVisible(this.trialAnnotationsFormatted);
    return this.formatCount(count, this.trialAnnotationsFormatted);
  },
  formatCount(count, annotations) {
    var length = annotations && annotations.length;
    if (length && count == 0) {
      return "No cards visible (" + length + " found)";
    }
    if (length && count == 1) {
      return "1 card visible (" + length + " found)";
    }
    if (length && count > 1) {
      return count + " cards visible (" + length + " found)";
    }
    return "No annotations found";
  }

  },
  mounted: function () {
    this.getAllTrials();
    this.getAllGenes();
  },
  destroyed: function () {
  },
  created: function () {
  },
  computed: {
  },
  watch: {
    '$route': 'handleRouteChanged',
    searchTrialAnnotationsVisible: 'handleSearchFocus'
  }
};

