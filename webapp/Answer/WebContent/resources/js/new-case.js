const NewCase = {
    template:
        `<div>
    <v-dialog v-model="helpVisible" max-width="50%">
        <v-card>
            <v-card-title primary-title>
                <span class="headline">New Case</span>
            </v-card-title>
            <v-card-text>
                <p>
                    Start by importing the report created by MD Anderson.
                </p>
                <p>
                    You can then select which variants will go into the final report.
                    <br/>
                    <b>Gene Details:</b>
                    <br/>
                    <v-icon color="green">check_circle</v-icon>: Actionable gene
                    <b>and</b> variant.
                </p>
                <p>
                    Once your selection and annotations are ready for review, click the
                    <i>Finalize</i>
                    button to submit the report for approval.
                </p>
            </v-card-text>
        </v-card>
    </v-dialog>

    <v-snackbar :timeout="2000" :bottom="true" v-model="snackBarVisible">
        {{ snackBarMessage }}
        <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
    </v-snackbar>

    <!-- annotation dialog -->
    <v-dialog v-model="annotationDialogVisible" fullscreen transition="dialog-bottom-transition" :overlay="false" scrollable>
        <v-card ref="annotationDialog" color="primary">
            <v-card-title>
                <span class="headline">Your Annotations for {{ variantAnnotated }}</span>
            </v-card-title>
            <v-card-text :style="getDialogMaxHeight()">
                <v-card v-if="userEditingAnnotations.length == 0">
                    <v-card-text>
                        Click on
                        <v-btn color="primary" @click="addAnnotation()">Add
                            <v-icon right dark>playlist_add</v-icon>
                        </v-btn> to create a new annotation.
                    </v-card-text>
                </v-card>
                <v-layout color="primary" row wrap v-for="(annotation, index) in userEditingAnnotations" :key="index">
                    <v-flex xs12>
                        <v-card class="mb-3">
                            <v-card-text>
                                <v-layout row wrap>
                                    <v-flex xs12>
                                        <span v-if="annotation.createdDateFormatted">
                                            <b>Created on:</b> {{ annotation.createdDateFormatted }}</span>
                                        <span v-if="annotation.modifiedDateFormatted" class="pl-4">
                                            <b>Modified on:</b> {{ annotation.modifiedDateFormatted }}</span>
                                        <v-tooltip bottom>
                                            <v-btn slot="activator" icon flat @click="annotation.isVisible = !annotation.isVisible">
                                                <v-icon v-show="!annotation.isVisible">visibility</v-icon>
                                                <v-icon v-show="annotation.isVisible">visibility_off</v-icon>
                                            </v-btn>
                                            <span>Show/Hide Annotation</span>
                                        </v-tooltip>
                                        <v-tooltip bottom>
                                            <v-btn slot="activator" icon flat @click="annotation.markedForDeletion = !annotation.markedForDeletion">
                                                <v-icon>delete</v-icon>
                                            </v-btn>
                                            <span>Delete Annotation</span>
                                        </v-tooltip>
                                        <span class="pl-4" v-show="annotation.markedForDeletion">This annotation will be deleted on SAVE. Click CANCEL or
                                            <v-icon>delete</v-icon> to cancel.
                                        </span>
                                    </v-flex>
                                    <v-flex xs12>
                                        <v-text-field v-show="annotation.isVisible" :textarea="true" ref="editAnnotation" :value="annotation.text" class="mr-2" :disabled="annotation.markedForDeletion">
                                        </v-text-field>
                                    </v-flex>
                                </v-layout>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                </v-layout>
            </v-card-text>
            <v-card-actions>
                <v-btn color="primary" @click="addAnnotation()">Add
                    <v-icon right dark>playlist_add</v-icon>
                </v-btn>
                <v-btn color="success" @click="saveAnnotations()">Save
                    <v-icon right dark>save</v-icon>
                </v-btn>
                <v-btn color="error" @click="cancelAnnotations()">Cancel
                    <v-icon right dark>cancel</v-icon>
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>

    <v-toolbar dark color="primary" fixed app>
        <v-toolbar-title class="white--text">
            <span v-if="!patient">Import a New Case</span>
            <span v-if="patient">Current Case</span>
            <span v-if="patient" class="pl-4">MRN: {{ patient.mrn }}

            </span>
            <span v-if="patient" class="pl-4"> {{ patient.firstName }} {{ patient.lastName }}
            </span>
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip bottom>
            <v-btn icon @click="importVisible = !importVisible" slot="activator">
                <v-icon>cloud_upload</v-icon>
            </v-btn>
            <span>Import MDA File</span>
        </v-tooltip>
        <v-tooltip bottom>
            <v-btn :disabled="patientTables.length == 0" icon @click="patientDetailsVisible = !patientDetailsVisible" slot="activator">
                <v-icon>perm_identity</v-icon>
            </v-btn>
            <span>Patient Details</span>
        </v-tooltip>
        <v-tooltip bottom>
            <v-btn :disabled="patientTables.length == 0" icon @click="saveCase" slot="activator">
                <v-icon>save</v-icon>
            </v-btn>
            <span>Save Selected Variants</span>
        </v-tooltip>
        <v-tooltip bottom>
            <v-btn :disabled="patientTables.length == 0" icon @click="finalizeCase" slot="activator">
                <v-icon>playlist_add_check</v-icon>
            </v-btn>
            <span>Save Case and Finalize</span>
        </v-tooltip>
        <v-btn icon @click="helpVisible = true">
            <v-icon>help</v-icon>
        </v-btn>
    </v-toolbar>

    <div>
        <v-text-field :textarea="true" ref="emailContent">

        </v-text-field>
        <div>
            {{ parsedResponse }}
        </div>
    </div>

    <v-layout row wrap v-if="importVisible">
        <v-flex>
            <v-card flat class="gray-background">
                <v-card-text>
                    <file-upload v-model="files" :post-action="uploadUrl" ref="upload" extensions="html" :multiple="false" @input-file="inputFile"
                        :headers="{enctype:'multipart/form-data'}">
                        <div class="btn primary div-button">
                            <span class="btn__content">IMPORT MDA FILE
                                <v-icon right>folder_open</v-icon>
                            </span>
                        </div>
                    </file-upload>
                </v-card-text>
            </v-card>
        </v-flex>
        <v-flex>
            <v-card flat class="gray-background">
                <v-card-text>
                    <span>File Selected:</span>
                    <strong v-if="files.length == 0">&nbsp;None</strong>
                    <strong v-if="files.length > 0">{{ files[0].name }} {{ Math.round(files[0].size / 1024) }}KB</strong>
                    <v-btn @click="handleFileUploadClick()" color="primary" :disabled="files.length == 0" :loading="loading">Upload
                        <v-icon right dark>cloud_upload</v-icon>
                    </v-btn>
                </v-card-text>
            </v-card>
        </v-flex>

    </v-layout>

    <div v-show="patientTables.length > 0">
        <div class="pb-2">MD Anderson file imported as:
            <span> {{ currentFile }}</span>
        </div>
        <!-- Patient details -->
        <div class="text-xs-center" v-if="patientDetailsVisible">
            <v-toolbar flat>
                <v-toolbar-title>Patient Details</v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn flat icon @click="patientDetailsVisible = false" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close Details</span>
                </v-tooltip>
            </v-toolbar>

            <v-container grid-list-md fluid>
                <v-layout row wrap>
                    <v-flex xs4 v-for="table in patientTables" :key="table.name">
                        <v-card>
                            <v-card-text>
                                <v-list class="dense-tiles">
                                    <v-list-tile v-for="item in table.items" :key="item.label">
                                        <v-list-tile-content class="pb-2">
                                            <v-layout class="full-width">
                                                <v-flex xs6 class="text-xs-left grow">
                                                    <span>{{ item.label }}:</span>
                                                </v-flex>
                                                <v-flex xs6 class="text-xs-right grow blue-grey--text text--lighten-1">
                                                    <span>{{ item.value }}</span>
                                                </v-flex>
                                            </v-layout>
                                        </v-list-tile-content>
                                    </v-list-tile>
                                </v-list>
                            </v-card-text>
                        </v-card>
                    </v-flex>
                </v-layout>
            </v-container>
        </div>

        <v-container grid-list-md fluid>
            <div>
                <v-layout row wrap>
                    <v-flex :class="[annotationVisible ? 'xs6':'xs12']">
                        <!-- table -->
                        <data-table ref="geneVariantDetails" :fixed="false" :fetch-on-created="false" table-title="Gene Details" :initial-sort="'geneDetails'"
                            no-data-text="No Data" :enable-selection="true">
                        </data-table>
                    </v-flex>
                    <v-flex xs6 v-if="annotationVisible">
                        <!-- annotations -->
                        <v-toolbar color="primary">
                            <v-toolbar-title class="white--text">Annotations for {{ variantAnnotated }}</v-toolbar-title>
                            <v-spacer></v-spacer>
                            <v-tooltip bottom>
                                <v-btn flat icon dark @click="annotationVisible = false" slot="activator">
                                    <v-icon dark>close</v-icon>
                                </v-btn>
                                <span>Close Annotations</span>
                            </v-tooltip>
                        </v-toolbar>
                        <v-card color="primary">
                            <v-container fluid grid-list-lg>
                                <v-layout row wrap>
                                    <v-flex xs12>
                                        <v-card>
                                            <v-card-title class="subheading">MD Anderson Annotations</v-card-title>
                                            <v-card-text v-html="mdaAnnotation">

                                            </v-card-text>
                                        </v-card>
                                    </v-flex>

                                    <v-flex xs12>
                                        <v-card>
                                            <v-card-title class="subheading">UTSW Annotations</v-card-title>
                                            <v-card-text v-for="(annotation, index) in utswAnnotationsFormatted" :key="index" v-html="annotation">

                                            </v-card-text>
                                        </v-card>
                                    </v-flex>

                                    <v-flex xs12>
                                        <v-card>
                                            <v-card-title class="subheading">Personnal Annotations
                                                <v-tooltip bottom>
                                                    <v-btn slot="activator" flat icon @click="startUserAnnotations">
                                                        <v-icon>mode_edit</v-icon>
                                                    </v-btn>
                                                    <span>Edit/Add Annotation(s)</span>
                                                </v-tooltip>
                                            </v-card-title>
                                            <v-card-text v-for="(annotation, index) in userAnnotationsFormatted" :key="index" v-html="annotation">

                                            </v-card-text>
                                        </v-card>
                                    </v-flex>
                                </v-layout>
                            </v-container>
                        </v-card>
                    </v-flex>
                </v-layout>
            </div>
        </v-container>


    </div>
</div>`, data() {
        return {
            files: [], uploadUrl: webAppRoot + "/importMDAFile", uploading: false, patientTables: [], caseId: null,
            loading: false, helpVisible: false,
            importVisible: true,
            currentFile: null,
            patientDetailsVisible: false,
            mdaAnnotation: "",
            utswAnnotations: [],
            userAnnotations: [],
            userEditingAnnotations: [],
            userEditingGeneId: -1,
            utswAnnotationsFormatted: [],
            userAnnotationsFormatted: [],
            annotationVisible: false,
            variantAnnotated: "",
            annotationDialogVisible: false,
            snackBarVisible: false,
            snackBarMessage: "",
            patient: null,
            parsedResponse: ""
        }
    },
    methods: {
        inputFile: function (newFile, oldFile) {
            if (newFile && oldFile && !newFile.active && oldFile.active) {
                var response = JSON.parse(newFile.response);
                if (response.isAllowed && response.success) {
                    this.patientTables = response.patientTables;
                    this.patient = response.patient;
                    this.caseId = response.caseId;
                    this.importVisible = false;
                    this.currentFile = response.currentFile;
                    // this.tableData = response.data;
                    this.addHeaderAction(response)
                    this.$refs.geneVariantDetails.manualData(response);
                    this.patientDetailsVisible = true;
                }
                else {
                    this.handleDialogs(response);
                }
                this.loading = false;
                this.files = [];
            }
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
        handleFileUploadClick() {
            this.$refs.upload.active = true;
            this.loading = true;
            this.fileUploaded = true;
        },
        addHeaderAction(response) {
            for (var i = 0; i < response.data.headers.length; i++) {
                if (response.data.headers[i].value == "geneDetailsValue") {
                    response.data.headers[i].itemAction = this.handleToggleAnnotations;
                    response.data.headers[i].actionTooltip = "Annotations";
                    break;
                }
            }
        },
        handleToggleAnnotations(item) {
            this.annotationVisible = true;
            this.variantAnnotated = item.geneDetails;
            bus.$emit("shrink-menu");
            this.mdaAnnotation = item.mdaAnnotations;
            this.getLocalAnnotations(item.gene);
        },
        getLocalAnnotations(gene) {
            this.utswAnnotations = [];
            this.userAnnotations = [];
            axios.get("./getLocalAnnotations", {
                params: {
                    'geneName': gene,
                    geneId: this.userEditingGeneId
                }
            })
                .then(response => {
                    if (response.data.isAllowed) {
                        this.utswAnnotations = response.data.utswAnnotations;
                        this.userAnnotations = response.data.userAnnotations;
                        this.formatAnnotations();
                    }
                    else {
                        this.handleDialogs(response.data, this.getLocalAnnotations.bind(null, gene, this.getLocalAnnotations));
                    }
                })
                .catch(error => {
                    alert(error);
                });
        },
        commitAnnotations() {
            axios.get("./commitAnnotations", {
                params: {
                    annotations: JSON.stringify(this.userAnnotations),
                    geneId: this.userEditingGeneId
                }
            })
                .then(response => {
                    if (response.data.isAllowed) {
                        this.snackBarMessage = "Annotation(s) Saved";
                        this.snackBarVisible = true;
                        this.getLocalAnnotations("");
                    }
                    else {
                        this.handleDialogs(response.data, this.commitAnnotations);
                    }
                })
                .catch(error => {
                    alert(error);
                });
        },
        formatLocalAnnotations(annotations, showUser) {
            var formatted = [];
            for (var i = 0; i < annotations.length; i++) {
                var annotation = "";
                if (showUser) {
                    annotation = "<b>" + annotations[i].user.first + " "
                        + annotations[i].user.last + "</b>: ";
                }
                annotation += annotations[i].text.replace(/\n/g, "<br/>") + "<br/>";
                formatted.push(annotation);
            }
            return formatted;
        },
        startUserAnnotations() {
            //first make a copy of annotations for editing
            //this will allow to cancel without modifying the existing annotations
            this.userEditingAnnotations = [];
            for (var i = 0; i < this.userAnnotations.length; i++) {
                this.userEditingAnnotations.push(JSON.parse(JSON.stringify(this.userAnnotations[i])));
                this.userEditingGeneId = this.userAnnotations[i].gene.geneId;
            }
            this.annotationDialogVisible = true;
        },
        cancelAnnotations() {
            this.annotationDialogVisible = false;
            this.$nextTick(function () {
                //wait until dialog is closed
                this.userEditingAnnotations = [];
            });
        },
        saveAnnotations() {

            this.annotationDialogVisible = false;
            //copy edits to original annotations
            // setTimeout(function() {
            //     this.userAnnotations = this.userEditingAnnotations;
            // }, 2000);
            var editedAnnotations = this.$refs.editAnnotation;
            for (var i = 0; i < editedAnnotations.length; i++) {
                if (!this.userAnnotations[i]) {
                    this.userAnnotations.push({
                        annotationId: -1
                    });
                }
                this.userAnnotations[i].text = editedAnnotations[i].inputValue;
                this.userAnnotations[i].markedForDeletion = this.userEditingAnnotations[i].markedForDeletion;
            }
            this.commitAnnotations();
        },
        addAnnotation() {
            this.userEditingAnnotations.push({
                text: "",
                markedForDeletion: false,
                isVisible: true
            });
            this.$nextTick(function () {
                this.$refs.editAnnotation[this.$refs.editAnnotation.length - 1].focus();
                $vuetify.goTo("textarea:last-child");
            });
        },
        formatAnnotations() {
            this.utswAnnotationsFormatted = this.formatLocalAnnotations(this.utswAnnotations, true);
            this.userAnnotationsFormatted = this.formatLocalAnnotations(this.userAnnotations, false);
        },
        getDialogMaxHeight() {
            return "max-height:" + window.innerHeight - 200 + "px; overflow-y: auto";
        },
        isAnnotationVisible(annotation) {
            return annotation.visible;
        },
        saveCase() {
            axios.get("./saveCase", {
                params: {
                    selectedVariants: JSON.stringify(this.$refs.geneVariantDetails.selected),
                    toUnselect: JSON.stringify(this.$refs.geneVariantDetails.toUnselect),
                    curatorComments: ""
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.snackBarMessage = response.data.message;
                        this.snackBarVisible = true;
                    }
                    else {
                        this.handleDialogs(response.data, this.commitAnnotations);
                    }
                })
                .catch(error => {
                    alert(error);
                });
        },
        finalizeCase() {
            axios.get("./finalizeCase", {
                params: {
                    selectedVariants: JSON.stringify(this.$refs.geneVariantDetails.selected),
                    toUnselect: JSON.stringify(this.$refs.geneVariantDetails.toUnselect),
                    curatorComments: ""
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.snackBarMessage = response.data.message;
                        this.snackBarVisible = true;
                    }
                    else {
                        this.handleDialogs(response.data, this.commitAnnotations);
                    }
                })
                .catch(error => {
                    alert(error);
                });
        },
        parseMDAEmail() {
            axios({
                method: 'post',
                url: webAppRoot + "/parseMDAEmail",
                params: {
                    emailContent: JSON.stringify(this.$refs.emailContent.inputValue),
                    token: "G13qIPyP3mLO1wrYLao0RhIm8gqQmQk4qsBxsL5fnW72YwYeVj6Lkr7ivx7dAHyjod9gAjBdGuSF43kQRsZkgcYFNB19Pjk5Gd0Jz5F81DPr500VjMJrq9vth0tu3w7H"
                }
            })
                .then(response => {
                    this.parsedResponse = response.data;
                })
                .catch(error => {
                    alert(error);
                });
        }
    },
    mounted: function () {
        // this.getMonthlySampleSummary();
        // this.getTATChartData();
    },
    destroyed: function () {
        // zingchart.exec(this.chartId, 'destroy');
    },
    created: function () {
    },
    computed: {
    },
    watch: {
    }
};

