const AnnotationBrowser = {
    template:
        `<div>
  
    <!-- annotation dialog -->
    <edit-annotations type="snp" @saving-annotations="commitAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs"
        ref="annotationDialog" :title="geneName + ' ' + variantName"></edit-annotations>

    <edit-annotations type="cnv" @saving-annotations="commitAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs"
        ref="cnvAnnotationDialog" :title="chrom"></edit-annotations>

    <edit-annotations type="translocation" @saving-annotations="commitAnnotations" @annotation-dialog-changed="updateEditAnnotationBreadcrumbs"
        ref="translocationAnnotationDialog" :title="leftGene + '--'  + rightGene"></edit-annotations>

        <v-snackbar :timeout="4000" :bottom="true" v-model="snackBarVisible">
            {{ snackBarMessage }}
            <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
        </v-snackbar>
        
  <v-toolbar dense dark color="primary" fixed app>
    <v-tooltip class="ml-0" bottom>
      <v-menu offset-y offset-x slot="activator" class="ml-0">
        <v-btn slot="activator" flat icon dark>
          <v-icon>more_vert</v-icon>
        </v-btn>
        <v-list>
          <v-list-tile @click="utswAnnotationsVisible = !utswAnnotationsVisible">
            <v-list-tile-title>Show/Hide UTSW Annotations</v-list-tile-title>
          </v-list-tile>
          <v-list-tile @click="mdaAnnotationsVisible = !mdaAnnotationsVisible">
            <v-list-tile-title>Show/Hide MDA Annotations</v-list-tile-title>
          </v-list-tile>
          <v-list-tile @click="startUserAnnotations()">
            <v-list-tile-title>Create/Edit Your Annotations</v-list-tile-title>
          </v-list-tile>
        </v-list>
      </v-menu>
      <span>Annotation Browser Menu</span>
    </v-tooltip>
    <v-toolbar-title class="white--text ml-0">
      Annotation Browser
    </v-toolbar-title>
    <v-spacer></v-spacer>

    <v-tooltip bottom>
      <v-btn icon @click="utswAnnotationsVisible = !utswAnnotationsVisible" slot="activator">
        <v-icon :color="utswAnnotationsVisible ? 'amber accent-2' : ''">mdi-table-search</v-icon>
      </v-btn>
      <span>Show/Hide UTSW Annotations</span>
    </v-tooltip>

    <v-tooltip bottom>
      <v-btn icon @click="mdaAnnotationsVisible = !mdaAnnotationsVisible" slot="activator">
        <v-icon :color="mdaAnnotationsVisible ? 'amber accent-2' : ''">mdi-table-search</v-icon>
      </v-btn>
      <span>Show/Hide MDA Annotations</span>
    </v-tooltip>

    <v-tooltip bottom>
      <v-btn icon @click="startUserAnnotations()" slot="activator">
        <v-icon :color="editAnnotationVisible ? 'amber accent-2' : ''">note_add</v-icon>
      </v-btn>
      <span>Create/Edit Your Annotations</span>
    </v-tooltip>

  </v-toolbar>
  <v-container grid-list-md fluid class="pl-0 pr-0">
    <v-flex xs7>
    <v-card class="mb-3 pl-2">
      <v-card-title class="subheading">
        Search for the genes or variants you would like to annotate:
      </v-card-title>
      <v-card-text>
        <v-layout row wrap>
          <v-flex xs5>
            <v-radio-group v-model="variantType" column>
              <v-radio label="SNP/Indel" value="snp"></v-radio>
              <v-radio label="CNV" value="cnv"></v-radio>
              <v-radio label="Fusion/Translocation" value="translocation"></v-radio>
            </v-radio-group>
          </v-flex>
          <v-flex xs7 v-show="variantType == 'snp'">
            <v-container grid-list-md class="mt-3">
              <v-layout row wrap>
                <v-flex xs12 class="subheading">Search SNP Annotations by Gene/Variant:</v-flex>
                <v-flex xs5>
                  <v-text-field slot="activator" autocomplete="off" clearable label="Gene Name" v-model="searchGene" hide-details></v-text-field>
                </v-flex>
                <v-flex xs5>
                  <v-text-field slot="activator" autocomplete="off" clearable label="Variant" v-model="searchVariant" hide-details></v-text-field>
                </v-flex>
              </v-layout>
            </v-container>
          </v-flex>

          <v-flex xs7 v-show="variantType == 'cnv'">
            <v-container grid-list-md class="mt-3">
              <v-layout row wrap>
                <v-flex xs12 class="subheading">Search CNV Annotations by Chrom/Gene:</v-flex>
                <v-flex xs5>
                  <v-text-field slot="activator" autocomplete="off" clearable label="Chromosome" v-model="chrom" hide-details></v-text-field>
                </v-flex>
                <v-flex xs5>
                  <v-text-field slot="activator" autocomplete="off" clearable label="Gene Name" v-model="searchGene" hide-details></v-text-field>
                </v-flex>
              </v-layout>
            </v-container>
          </v-flex>

          <v-flex xs7 v-show="variantType == 'translocation'">
            <v-container grid-list-md class="mt-3">
              <v-layout row wrap>
                <v-flex xs12 class="subheading">Search Fusion Annotations by Genes:</v-flex>
                <v-flex xs5>
                  <v-text-field slot="activator" autocomplete="off" clearable label="Left Gene" v-model="leftGene" hide-details></v-text-field>
                </v-flex>
                <v-flex xs5>
                  <v-text-field slot="activator" autocomplete="off" clearable label="Right Gene" v-model="rightGene" hide-details></v-text-field>
                </v-flex>
              </v-layout>
            </v-container>
          </v-flex>

        </v-layout>
      </v-card-text>
      <v-card-actions class="card-actions-bottom">
        <v-btn @click="searchForAnnotations()">Search</v-btn>
      </v-card-actions>
    </v-card>
  </v-flex>

    <v-card class="soft-grey-background">
      <v-toolbar dense dark color="primary">
        <v-toolbar-title>
          <v-icon color="amber accent-2">mdi-message-bulleted</v-icon>
          UTSW Annotations
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip bottom>
          <v-btn icon @click="utswAnnotationsVisible = false" slot="activator">
            <v-icon>close</v-icon>
          </v-btn>
          <span>Close</span>
        </v-tooltip>
      </v-toolbar>
      <v-card-text>
        <v-container grid-list-md fluid>
          <v-layout row wrap>
            <v-flex xs12 sm12 md6 lg6 xl4 v-for="(annotation, index) in utswAnnotationsFormatted" :key="index">
              <utsw-annotation-card :annotation="annotation" :variant-type="currentVariantType"></utsw-annotation-card>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>
      <v-card-actions class="card-actions-bottom">
        <v-tooltip top>
          <v-btn color="primary" @click="startUserAnnotations()" slot="activator">Add/Edit
            <v-icon right dark>note_add</v-icon>
          </v-btn>
          <span>Create/Edit Your Annotations</span>
        </v-tooltip>
      </v-card-actions>
    </v-card>

  </v-container>
</div>`
, data() {
        return {
            searchGene: null,
            searchVariant: null,
            searchChrom: null,
            searchLeftGene: null,
            searchRightGene: null,
            utswAnnotations: [],
            mdaAnnotations: [],
            currentVariant: null,
            utswAnnotationsVisible: true,
            mdaAnnotationsVisible: true,
            editAnnotationVisible: false,
            utswAnnotationsFormatted: [],
            geneName: null,
            variantName: null,
            chrom: null,
            leftGene: null,
            rightGene: null,
            variantType: 'snp',
            topMostDialog: "",
            userAnnotations: [],
            snackBarVisible: false,
            snackBarMessage: "",
            variantId: null

        }
    },
    methods: {
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
        searchForAnnotations() {
            this.utswAnnotations = [];
            this.mdaAnnotations = [];
            this.geneName = this.searchGene;
            this.variantName = this.searchVariant;
            this.chrom = this.searchChrom;
            this.leftGene = this.searchLeftGene;
            this.rightGene = this.searchRightGene;
            this.variantType = this.variantType;
            this.variantId = null; //reset this field in case the search is not variant specific
            axios.get("./searchForAnnotations", {
                params: {
                    gene: this.geneName,
                    variant: this.variantName,
                    chrom: this.chrom,
                    leftGene: this.leftGene,
                    rightGene: this.rightGene,
                    variantType: this.variantType,
                     
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.utswAnnotations = response.data.utswAnnotations;
                        this.mdaAnnotations = response.data.mdaAnnotations;
                        this.geneName = response.data.geneName;
                        this.variantName = response.data.variantName;
                        this.chrom = response.data.chrom;
                        this.leftGene = response.data.leftGene;
                        this.rightGene = response.data.rightGene;
                        this.variantType = response.data.variantType;
                        this.variantId = response.data.variantId;
                    }
                    else {
                        this.handleDialogs(response.data, this.searchForAnnotations);
                    }
                })
                .catch(error => {
                    alert(error);
                });
        },
        formatAnnotations() {
            this.utswAnnotationsFormatted = this.formatLocalAnnotations(this.utswAnnotations, true);
            // this.userAnnotationsFormatted = this.formatLocalAnnotations(this.userAnnotations, false);
        },
        formatCNVAnnotations() {
            this.utswAnnotationsFormatted = this.formatLocalCNVAnnotations(this.utswAnnotations, true);
        },
        formatTranslocationAnnotations() {
            this.utswAnnotationsFormatted = this.formatLocalTranslocationAnnotations(this.utswAnnotations, true);
        },
        formatLocalAnnotations(annotations, showUser) {
            var formatted = [];
            for (var i = 0; i < annotations.length; i++) {
                var annotation = {
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
                    nctids: [],
                    tier: "",
                    classification: ""
                };
                if (showUser) {
                    annotation.fullName = annotations[i].fullName;
                }
                annotation.text = annotations[i].text.replace(/\n/g, "<br/>");
                annotation.scopes = [annotations[i].isCaseSpecific, annotations[i].isGeneSpecific, annotations[i].isVariantSpecific, annotations[i].isTumorSpecific];
                annotation.scopeLevels = ["Case " + (annotations[i].isCaseSpecific ? annotations[i].caseId : ''),
                "Gene " + (annotations[i].isGeneSpecific ? annotations[i].geneId : ''),
                "Variant " + (annotations[i].isVariantSpecific ? currentVariant.notation : ''),
                    "Tumor"];
                annotation.category = annotations[i].category;
                annotation.createdDate = annotations[i].createdDate;
                annotation.createdSince = annotations[i].createdSince;
                annotation.modifiedDate = annotations[i].modifiedDate;
                annotation.modifiedSince = annotations[i].modifiedSince;
                annotation.pmids = annotations[i].pmids;
                annotation.nctids = annotations[i].nctids;
                annotation.scopeTooltip = this.$refs.annotationDialog.createLevelInformation(annotations[i]);
                annotation.tier = annotations[i].tier;
                annotation.classification = annotations[i].classification;
                formatted.push(annotation);
            }
            return formatted;
        },
        formatLocalCNVAnnotations(annotations, showUser) {
            var formatted = [];
            for (var i = 0; i < annotations.length; i++) {
                var annotation = {
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
                    cnvGenes: ""
                };
                if (showUser) {
                    annotation.fullName = annotations[i].fullName;
                }
                annotation.text = annotations[i].text.replace(/\n/g, "<br/>");
                annotation.scopes = [annotations[i].isCaseSpecific, annotations[i].isVariantSpecific, annotations[i].isTumorSpecific];
                annotation.scopeLevels = ["Case " + (annotations[i].isCaseSpecific ? annotations[i].caseId : ''),
                "Variant " + (annotations[i].isVariantSpecific ? this.currentVariant.chrom : ''),
                    "Tumor"];
                annotation.category = annotations[i].category;
                annotation.cnvGenes = annotations[i].cnvGenes ? annotations[i].cnvGenes.join(" ") : "";
                annotation.createdDate = annotations[i].createdDate;
                annotation.createdSince = annotations[i].createdSince;
                annotation.modifiedDate = annotations[i].modifiedDate;
                annotation.modifiedSince = annotations[i].modifiedSince;
                annotation.scopeTooltip = this.$refs.cnvAnnotationDialog.createLevelInformation(annotations[i]);
                formatted.push(annotation);
            }
            return formatted;
        },
        formatLocalTranslocationAnnotations(annotations, showUser) {
            var formatted = [];
            for (var i = 0; i < annotations.length; i++) {
                var annotation = {
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
                };
                if (showUser) {
                    annotation.fullName = annotations[i].fullName;
                }
                annotation.text = annotations[i].text.replace(/\n/g, "<br/>");
                annotation.scopes = [annotations[i].isCaseSpecific, annotations[i].isVariantSpecific, annotations[i].isTumorSpecific];
                annotation.scopeLevels = ["Case " + (annotations[i].isCaseSpecific ? annotations[i].caseId : ''),
                "Variant " + (annotations[i].isVariantSpecific ? this.currentVariant.fusionName : ''),
                    "Tumor"];
                annotation.category = annotations[i].category;
                annotation.createdDate = annotations[i].createdDate;
                annotation.createdSince = annotations[i].createdSince;
                annotation.modifiedDate = annotations[i].modifiedDate;
                annotation.modifiedSince = annotations[i].modifiedSince;
                annotation.scopeTooltip = this.$refs.translocationAnnotationDialog.createLevelInformation(annotations[i]);
                formatted.push(annotation);
            }
            return formatted;
        },
        updateEditAnnotationBreadcrumbs(visible) {
          if (visible) {
              bus.$emit("add-breadcrumb-level", this.breadcrumbItemEditAnnotation);
              this.topMostDialog = this.breadcrumbItemEditAnnotation.closingFunction;
          }
          else {
              bus.$emit("remove-breadcrumb-level", this);
          }
      },
        startUserAnnotations() {

        },
        commitAnnotations(userAnnotations) {
          this.userAnnotations = userAnnotations;
          axios({
              method: 'post',
              url: webAppRoot + "/commitAnnotations",
              params: {
                  caseId: "",
                  geneId: this.geneName ? this.geneName : "",
                  variantId: this.variantId ? this.variantId : ""
              },
              data: {
                  annotations: this.userAnnotations,
              }
          })
              .then(response => {
                  if (response.data.isAllowed && response.data.success) {
                      this.snackBarMessage = "Annotation(s) Saved";
                      this.snackBarVisible = true;
                      //refresh
                      this.searchForAnnotations();

                  } else {
                      this.handleDialogs(response.data, this.commitAnnotations);
                  }
              })
              .catch(error => {
                  console.log(error);
                  bus.$emit("some-error", [this, error]);
              });
      },

    },
    mounted: function () {

    },
    destroyed: function () {

    },
    created: function () {

    },
    computed: {
    },
    watch: {
    }
};

