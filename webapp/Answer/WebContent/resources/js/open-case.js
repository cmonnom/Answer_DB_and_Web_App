const OpenCase = {
    template: `<div>
    <!-- save selection dialog -->
    <v-dialog v-model="saveDialogVisible" scrollable fullscreen hide-overlay transition="dialog-bottom-transition">
        <v-card class="soft-grey-background">
            <v-toolbar dark color="primary">
                <v-toolbar-title>Review Selected Variants
                </v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon @click="saveDialogVisible = false" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close</span>
                </v-tooltip>
            </v-toolbar>
            <v-card-text :style="getDialogMaxHeight()">
                <data-table ref="variantsSelected" :fixed="false" :fetch-on-created="false" table-title="Selected Variants" initial-sort="chromPos"
                    no-data-text="No Data" :show-row-count="true">
                </data-table>
            </v-card-text>
            <v-card-actions>
                <v-tooltip bottom>
                    <v-btn color="primary" @click="saveSelection()" slot="activator">Save
                        <v-icon right dark>save</v-icon>
                    </v-btn>
                    <span>Save Variant</span>
                </v-tooltip>
                <v-btn color="error" @click="saveDialogVisible = false" slot="activator">Cancel
                    <v-icon right dark>cancel</v-icon>
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
    <!-- variant details dialog -->
    <v-dialog v-model="variantDetailsVisible" scrollable fullscreen hide-overlay transition="dialog-bottom-transition">
        <v-card class="soft-grey-background">
            <v-toolbar dark color="primary">
                <v-toolbar-title>Annotations for variant: {{ currentVariant.geneName }} {{ currentVariant.notation }}
                    <v-tooltip bottom v-for="(icon, index) in currentVariantFlags" :key="index">
                        <v-icon slot="activator">
                            {{ icon.iconName }}
                        </v-icon>
                        <span> {{ icon.tooltip }}</span>
                    </v-tooltip>

                </v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon flat :color="annotationVariantDetailsVisible ? 'amber accent-2' : ''" @click="annotationVariantDetailsVisible = !annotationVariantDetailsVisible"
                        slot="activator">
                        <v-icon>zoom_in</v-icon>
                    </v-btn>
                    <span>Show/Hide Variant Details</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <v-btn icon flat :color="annotationVariantCanonicalVisible ? 'amber accent-2' : ''" @click="annotationVariantCanonicalVisible = !annotationVariantCanonicalVisible"
                        slot="activator">
                        <v-icon>table_chart</v-icon>
                    </v-btn>
                    <span>Show/Hide Canonical VCF Annotations</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <v-btn icon flat :color="annotationVariantOtherVisible ? 'amber accent-2' : ''" @click="annotationVariantOtherVisible = !annotationVariantOtherVisible"
                        slot="activator">
                        <v-icon>table_chart</v-icon>
                    </v-btn>
                    <span>Show/Hide Other VCF Annotations</span>
                </v-tooltip>
                <v-tooltip bottom>
                    <v-btn icon @click="variantDetailsVisible = false" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close Variant</span>
                </v-tooltip>


            </v-toolbar>
            <v-card-text :style="getDialogMaxHeight()">
                <v-container grid-list-md fluid>
                    <v-layout row wrap>
                        <!-- card showing the same data as the summary row -->
                        <v-flex xs12 md12 lg9 xl7 v-show="annotationVariantDetailsVisible">
                            <v-card>
                                <v-toolbar dark color="primary">
                                    <v-toolbar-title>
                                        <v-icon>zoom_in</v-icon>
                                        Variant Details
                                    </v-toolbar-title>

                                    <v-spacer></v-spacer>
                                    <v-tooltip bottom>
                                        <v-btn icon @click="annotationVariantDetailsVisible = false" slot="activator">
                                            <v-icon>close</v-icon>
                                        </v-btn>
                                        <span>Close Details</span>
                                    </v-tooltip>

                                </v-toolbar>
                                <v-container grid-list-md fluid>
                                    <v-layout row wrap>
                                        <v-flex xs4 v-for="table in variantDataTables" :key="table.name">
                                            <v-card flat>
                                                <v-card-text>
                                                    <v-list class="dense-tiles">
                                                        <v-list-tile v-for="item in table.items" :key="item.label">
                                                            <v-list-tile-content class="pb-2">
                                                                <v-layout class="full-width">
                                                                    <v-flex xs12 class="text-xs-left grow">
                                                                        <span class="selectable">{{ item.label }}:</span>
                                                                        <span v-if="!item.links" v-html="item.value" class="selectable text-xs-right grow blue-grey--text text--lighten-1"></span>
                                                                        <v-tooltip v-if="item.links" bottom v-for="id in item.ids" :key="id">
                                                                            <v-btn @click="handleIdLink(id)" slot="activator" v-html="formatIdLinkLabel(id, item)">
                                                                            </v-btn>
                                                                            <span>Open in new tab</span>
                                                                        </v-tooltip>
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
                            </v-card>
                        </v-flex>
                        <v-flex xs12>
                            <div class="pb-4 pt-2" v-show="annotationVariantCanonicalVisible">
                                <data-table ref="canonicalVariantAnnotation" :fixed="false" :fetch-on-created="false" table-title="Canonical VCF Annotations"
                                    initial-sort="geneId" no-data-text="No Data" :show-pagination="false" title-icon="table_chart">
                                </data-table>
                            </div>
                            <data-table v-show="annotationVariantOtherVisible" ref="otherVariantAnnotations" :fixed="false" :fetch-on-created="false"
                                table-title="Other VCF Annotations" initial-sort="geneId" no-data-text="No Data" :show-row-count="true"
                                title-icon="table_chart">
                            </data-table>
                        </v-flex>
                    </v-layout>
                </v-container>
            </v-card-text>
            <v-card-actions>
                <v-tooltip bottom>
                    <v-btn color="primary" @click="addAnnotation()" slot="activator">Add
                        <v-icon right dark>note_add</v-icon>
                    </v-btn>
                    <span>Create a new UTSW Annotation</span>
                </v-tooltip>
                <v-btn v-if="!currentRow.isSelected" :disabled="saveDialogVisible" color="success" @click="selectVariantForReport()" slot="activator">Select Variant
                    <v-icon right dark>done</v-icon>
                </v-btn>
                <v-btn v-if="currentRow.isSelected" :disabled="saveDialogVisible" color="warning" @click="removeVariantFromReport()" slot="activator">Unselect Variant
                    <v-icon right dark>done</v-icon>
                </v-btn>
                <v-btn color="error" @click="variantDetailsVisible = false" slot="activator">Cancel
                    <v-icon right dark>cancel</v-icon>
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
    <!-- advanced filtering side drawer -->
    <div v-if="advanceFilteringVisible">
        <v-navigation-drawer app width="500" class="elevation-5">
            <v-toolbar>
                <v-icon>filter_list</v-icon>
                <div class="title pl-2">
                    Filters
                </div>
                <v-spacer></v-spacer>
                <v-btn color="primary" @click="clearFilters">
                    Clear
                </v-btn>
                <v-btn :color="filterNeedsReload ? 'warning' : 'primary'" @click="filterData" :disabled="!filtersValid">
                    Refresh
                </v-btn>
                <v-tooltip bottom>
                    <v-btn icon @click="toggleFilters()" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close Filter Menu</span>
                </v-tooltip>
            </v-toolbar>
        </v-navigation-drawer>
        <v-navigation-drawer app width="500" class="mt-6" height="calc(100% - 64px)">
            <!-- displays which filters are active -->
            <div class="pt-2 pb-2">
                <v-chip v-if="isFilterUsed(filter)" label v-for="filter in filters" :key="filter.fieldName" :color="isInputNumberValid(filter) ? 'primary' : 'error'"
                    text-color="white">
                    <span v-html="getFilterChip(filter)"></span>
                    <v-tooltip bottom>
                        <v-btn slot="activator" dark flat icon small @click="clearFilter(filter, true)">
                            <v-icon>close</v-icon>
                        </v-btn>
                        <span>Clear Filter</span>
                    </v-tooltip>
                </v-chip>
                <div v-if="filter.isCheckBox" v-for="filter in filters" :key="filter.fieldName">
                    <v-chip v-if="isCheckBoxFilterUsed(checkBox)" label v-for="checkBox in filter.checkBoxes" :key="checkBox.name" color="primary"
                        text-color="white">
                        <span v-html="getFilterCheckBoxChip(filter, checkBox)"></span>
                        <v-tooltip bottom>
                            <v-btn slot="activator" dark flat icon small @click="clearCheckBoxFilter(checkBox)">
                                <v-icon>close</v-icon>
                            </v-btn>
                            <span>Clear Filter</span>
                        </v-tooltip>
                    </v-chip>
                </div>
            </div>
            <v-divider></v-divider>
            <!-- list of possible filters -->
            <v-container grid-list-md>
                <v-form v-model="filtersValid">
                    <v-layout row v-for="filter in filters" :key="filter.fieldName" class="pl-3 pr-3">

                        <v-flex xs12 v-if="filter.isSelect">
                            <v-layout>
                                    <v-flex xs5 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
                                <v-flex xs7>
                                    <v-select class="no-height" v-bind:items="filter.selectItems" v-model="filter.value" item-text="name" item-value="value" :label="filter.headerText"
                                        @input="filterNeedsReload = true" auto autocomplete clearable></v-select>
                                </v-flex>
                            </v-layout>
                        </v-flex>

                        <v-flex xs12 v-if="filter.isBoolean">
                            <v-layout row class="pt-3">
                                <v-flex xs6>
                                    <v-switch class="no-height" color="primary" :label="filter.headerTextTrue" v-model="filter.valueTrue" @change="filterNeedsReload = true"></v-switch>
                                </v-flex>
                                <v-flex xs6>
                                    <v-switch class="no-height" color="primary" :label="filter.headerTextFalse" v-model="filter.valueFalse" @change="filterNeedsReload = true"></v-switch>
                                </v-flex>
                            </v-layout>
                        </v-flex>

                        <v-expansion-panel expand v-if="filter.isCheckBox" class="expandable-filter elevation-0">
                            <v-expansion-panel-content>
                                <div slot="header" class="subheading pl-1">{{ filter.headerText }}</div>
                                <v-layout row wrap>
                                    <v-flex xs12 lg6 v-for="(checkBox, index) in filter.checkBoxes" :key="index">
                                        <v-tooltip bottom>
                                            <v-checkbox color="primary" slot="activator" class="no-height" :label="checkBox.name" v-model="checkBox.value" @change="filterNeedsReload = true"></v-checkbox>
                                            <span>{{ checkBox.name }}</span>
                                        </v-tooltip>
                                    </v-flex>
                                </v-layout>
                            </v-expansion-panel-content>
                        </v-expansion-panel>

                        <v-flex xs12 v-if="filter.isString">
                            <v-layout>
                                    <v-flex xs5 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
                                <v-flex xs7>
                                    <v-text-field class="no-height" :name="filter.fieldName" :label="filter.headerText" v-model="filter.value" @input="filterNeedsReload = true"></v-text-field>
                                </v-flex>
                            </v-layout>
                        </v-flex>

                        <v-flex xs12 v-if="filter.isNumber">
                            <v-layout row>
                                <v-flex xs4 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
                                <v-flex xs4>
                                    <v-text-field class="no-height" :name="filter.fieldName + '-min'" label="Min" v-model="filter.minValue" :rules="numberRules"
                                        @input="filterNeedsReload = true"></v-text-field>
                                </v-flex>
                                <v-flex xs4>
                                    <v-text-field class="no-height" :name="filter.fieldName + '-max'" label="Max" v-model="filter.maxValue" :rules="numberRules"
                                        @input="filterNeedsReload = true"></v-text-field>
                                </v-flex>
                            </v-layout>
                        </v-flex>

                        <v-flex xs12 v-if="filter.isDate">
                            <v-layout row>
                                <v-flex xs4 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
                                <v-flex xs4>
                                    <v-menu lazy :v-model="false" transition="scale-transition" offset-y full-width :nudge-right="40" max-width="290px" min-width="290px"
                                        :close-on-content-click="true">
                                        <v-text-field class="no-height" slot="activator" label="From" v-model="filter.minValue" prepend-icon="event" readonly @input="filterNeedsReload = true"></v-text-field>
                                        <v-date-picker v-model="filter.minValue" no-title scrollable>
                                            <v-spacer></v-spacer>
                                        </v-date-picker>
                                    </v-menu>
                                </v-flex>
                                <v-flex xs4>
                                    <v-menu lazy :v-model="false" transition="scale-transition" offset-y full-width :nudge-right="40" max-width="290px" min-width="290px"
                                        :close-on-content-click="true">
                                        <v-text-field class="no-height" slot="activator" label="To" v-model="filter.maxValue" prepend-icon="event" readonly @input="filterNeedsReload = true"></v-text-field>
                                        <v-date-picker v-model="filter.maxValue" no-title scrollable>
                                            <v-spacer></v-spacer>
                                        </v-date-picker>
                                    </v-menu>
                                </v-flex>
                            </v-layout>
                        </v-flex>
                    </v-layout>
                </v-form>
            </v-container>
        </v-navigation-drawer>
    </div>
    <!-- advanced filtering side drawer -->

    <v-toolbar dark color="primary" fixed app>
        <v-toolbar-title class="white--text">
            Working on case: {{ caseName }}
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip bottom>
            <v-btn :disabled="patientTables.length == 0" flat icon @click="patientDetailsVisible = !patientDetailsVisible" slot="activator"
            :color="patientDetailsVisible ? 'amber accent-2' : ''">
            <!-- <v-icon>perm_identity</v-icon> -->
            <v-icon>assignment_ind</v-icon>
        </v-btn>
        <span>Show/Hide Patient Details</span>
    </v-tooltip>
        <v-tooltip bottom>
            <v-btn flat icon @click="openSaveDialog()" slot="activator" :color="saveDialogVisible ? 'amber accent-2' : ''">
                <v-icon>save</v-icon>
            </v-btn>
            <span>Review and Save Variants Selected</span>
        </v-tooltip>
    </v-toolbar>
    <v-progress-linear v-if="!caseName" :indeterminate="true"></v-progress-linear>
    
    <v-layout v-if="patientDetailsVisible">
        <v-flex xs12 md12 lg10 xl9>
            <div class="text-xs-center pb-3">
                <v-toolbar dark color="primary">
                    <!-- <v-icon>perm_identity</v-icon> -->
                    <v-icon>assignment_ind</v-icon>
                    <v-toolbar-title>Patient Details</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-tooltip bottom>
                        <v-btn flat icon @click="patientDetailsVisible = false" slot="activator">
                            <v-icon>close</v-icon>
                        </v-btn>
                        <span>Close Details</span>
                    </v-tooltip>
                </v-toolbar>
                <v-card>
                    <v-container grid-list-md fluid>
                        <v-layout row wrap>
                            <v-flex xs4 v-for="table in patientTables" :key="table.name">
                                <v-card flat>
                                    <v-card-text>
                                        <v-list class="dense-tiles">
                                            <v-list-tile v-for="item in table.items" :key="item.label">
                                                <v-list-tile-content class="pb-2">
                                                    <v-layout class="full-width">
                                                        <v-flex xs6 class="text-xs-left grow">
                                                            <span class="selectable">{{ item.label }}:</span>
                                                        </v-flex>
                                                        <v-flex xs6 class="text-xs-right grow blue-grey--text text--lighten-1">
                                                            <span class="selectable">{{ item.value }}</span>
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
                </v-card>
            </div>
        </v-flex>
    </v-layout>

    <data-table ref="geneVariantDetails" :fixed="false" :fetch-on-created="false" table-title="Variants" initial-sort="chromPos"
        no-data-text="No Data" :enable-selection="true" :show-row-count="true">
        <v-tooltip bottom slot="action1">
            <v-btn slot="activator" flat icon @click="toggleFilters" :color="advanceFilteringVisible ? 'amber accent-2' : 'white'">
                <v-icon>filter_list</v-icon>
            </v-btn>
            <span>Advanced Filtering</span>
        </v-tooltip>
    </data-table>
</div>` , data() { return { loading: true, patientTables: [], patientDetailsVisible: true, caseName: "",      caseId: "",
            advanceFilteringVisible: false,
            filters: [],
            effects: [],
            filtersValid: true,
            numberRules: [v => !isNaN(v) || 'Only numbers'],
            filterNeedsReload: true,
            variantDetailsVisible: false,
            currentVariant: {},
            currentRow: {},
            currentVariantFlags: [],
            variantDataTables: [],
            saveDialogVisible: false,
            annotationVariantDetailsVisible: true,
            annotationVariantCanonicalVisible: true,
            annotationVariantOtherVisible: true,
        }
    }, methods: {
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
        getDialogMaxHeight() {
            var height = window.innerHeight - 120;
            return "min-height:" + height + "px;max-height:" + height + "px; overflow-y: auto";
        },
        getAjaxData() {
            this.loading = true;
            axios({
                method: 'post',
                url: webAppRoot + "/getCaseDetails",
                params: {
                    caseId: this.$route.params.id
                },
                data: {
                    filters: this.filters
                }
            })
                .then(response => {
                    if (response.data.isAllowed) {
                        this.patientTables = response.data.patientInfo.patientTables;
                        this.caseName = response.data.caseName;
                        this.caseId = response.data.caseId;
                        this.$refs.geneVariantDetails.manualDataFiltered(response.data);
                        this.effects = response.data.effects;
                        this.populateCheckBoxes();
                        this.filterNeedsReload = false;
                        this.addHeaderAction(response);
                    }
                    else {
                        this.handleDialogs(response.data, this.getAjaxData);
                    }
                    this.loading = false;
                })
                .catch(error => {
                    this.loading = false;
                    console.log(error);
                });
        },
        getVariantFilters() {
            axios.get(webAppRoot + "/getVariantFilters", {
                params: {
                    caseId: this.$route.params.id
                }
            })
                .then(response => {
                    if (response.data.isAllowed) {
                        this.filters = response.data.filters;
                        this.populateCheckBoxes();
                    }
                    else {
                        this.handleDialogs(response, this.getVariantFilters);
                    }
                })
                .catch(error => {
                    console.log(error);
                });
        },
        populateCheckBoxes() {
            for (var i = 0; i < this.filters.length; i++) {
                var filter = this.filters[i];
                if (filter.isCheckBox) {
                    if (filter.fieldName == 'effects') {
                        if (filter.checkBoxes.length > 0) {
                            return;
                        }
                        filter.checkBoxes = [];
                        for (var j = 0; j < this.effects.length; j++) {
                            filter.checkBoxes.push({ name: this.effects[j], value: false });
                        }
                    }
                }
            }
        },
        filterData() {
            this.getAjaxData();
        },
        clearFilters() {
            for (var i = 0; i < this.filters.length; i++) {
                var filter = this.filters[i];
                this.clearFilter(filter);
            }
            this.getAjaxData();
        },
        clearFilter(filter, doRefresh) {
            filter.value = null;
            filter.minValue = null;
            filter.maxValue = null;
            filter.minDateValue = null;
            filter.maxDateValue = null;
            filter.valueTrue = false;
            filter.valueFalse = false;

            if (filter.isCheckBox) {
                for (var i = 0; i < filter.checkBoxes.length; i++) {
                    filter.checkBoxes[i].value = false;
                }
            }

            if (doRefresh) { //refresh only if closing a chip
                this.getAjaxData();
            }
        },
        clearCheckBoxFilter(checkBox) {
            checkBox.value = false;
            //TODO refresh
        },
        // display the content of the filter with the appropriate value min max etc
        getFilterChip(filter) {
            //TODO
            if (filter.isSelect) {
                return filter.headerText + ": <b>" + filter.value + "</b>";
            }
            if (filter.isBoolean) {
                return "Include " + filter.headerTextTrue + ": <b>" + (filter.valueTrue ? 'YES ' : 'NO ') + "</b><br/>"
                    + "Include " + filter.headerTextFalse + ": <b>" + (filter.valueFalse ? 'YES' : 'NO') + "</b>"
            }
            if (filter.isString) {
                return filter.headerText + " contains <b>" + filter.value + "</b>";
            }
            if (filter.isNumber) {
                // return (filter.minValue != null ? filter.minValue : '') + " <= " +  filter.headerText
                // + " <= " + (filter.maxValue != null ? filter.maxValue : '');
                return filter.headerText + ": <b>[ " + (filter.minValue != null ? filter.minValue : '') + ":"
                    + (filter.maxValue != null ? filter.maxValue : '') + " ]</b>";
            }
            //TODO dates and numbers
            return filter.headerText;
        },
        getFilterCheckBoxChip(filter, checkBox) {
            return filter.headerText + ": <b>" + checkBox.name + "</b>";
        },
        toggleFilters() {
            this.advanceFilteringVisible = !this.advanceFilteringVisible;
            if (!this.advanceFilteringVisible) {
                bus.$emit("need-layout-resize", this);
            }
        },
        isFilterUsed(filter) {
            if (filter.isCheckBox) {
                return false; //handled in a separate chip
            }
            if (filter.isNumber) {
                return (filter.minValue != null && filter.minValue !== "")
                    || (filter.maxValue != null && filter.maxValue !== "")
            }
            if (filter.isDate) {
                return filter.minDateValue != null || filter.maxDateValue != null;
            }
            if (filter.isBoolean) {
                return filter.valueTrue == true || filter.valueFalse == true;
            }
            return filter.value != null && filter.value !== "";
        },
        isCheckBoxFilterUsed(checkBox) {
            return checkBox.value == true;
        },
        isInputNumberValid(filter) {
            if (filter.isNumber) {
                var isValid = (filter.minValue === "" || !isNaN(filter.minValue))
                    && (filter.maxValue === "" || !isNaN(filter.maxValue));
                if (isValid) {
                    if (filter.minValue !== "" && filter.minValue != null) {
                        filter.minValue = parseFloat(filter.minValue);
                    }
                    if (filter.maxValue !== "" && filter.maxValue != null) {
                        filter.maxValue = parseFloat(filter.maxValue);
                    }
                }
                return isValid;
            }
            return true;
        },
        addHeaderAction(response) {
            for (var i = 0; i < response.data.headers.length; i++) {
                if (response.data.headers[i].value == "chromPos") {
                    response.data.headers[i].itemAction = this.openVariant;
                    response.data.headers[i].actionIcon = "zoom_in";
                    response.data.headers[i].actionTooltip = "Variant Details";
                    break;
                }
            }
        },
        getVariantDetails(item) {
            this.currentVariantFlags = item.iconFlags.iconFlags;
            this.currentRow = item;
            axios.get(webAppRoot + "/getVariantDetails", {
                params: {
                    caseId: this.$route.params.id,
                    chrom: item.chrom,
                    pos: item.pos,
                    alt: item.alt
                }
            })
                .then(response => {
                    if (response.data.isAllowed) {
                        this.currentVariant = response.data.variantDetails;
                        this.variantDataTables = [];
                        var infoTable = {
                            name: "infoTable",
                            items: [
                                {
                                    label: "Chromosome Position", value: this.currentVariant.chrom + ":" + this.currentVariant.pos
                                },
                                {
                                    label: "Gene", value: this.currentVariant.geneName
                                },
                                {
                                    label: "Notation", value: this.currentVariant.notation
                                },
                                {
                                    label: "Alt", value: this.currentVariant.alt
                                },
                                {
                                    label: "Type", value: this.currentVariant.type
                                },
                                {
                                    label: "IDs", ids: this.currentVariant.ids, cosmicPatients: this.currentVariant.cosmicPatients,
                                    links: true
                                }

                            ]
                        };
                        this.variantDataTables.push(infoTable);
                        var depthTable = {
                            name: "depthTable",
                            items: [
                                {
                                    label: "Tumor Total Depth", value: this.currentVariant.tumorTotalDepth
                                },
                                {
                                    label: "Tumor Alt Percent", value: this.formatPercent(this.currentVariant.tumorAltFrequency)
                                },
                                {
                                    label: "Normal Total Depth", value: this.currentVariant.normalTotalDepth
                                },
                                {
                                    label: "Normal Alt Percent", value: this.formatPercent(this.currentVariant.normalAltFrequency)
                                },
                                {
                                    label: "RNA Total Depth", value: this.currentVariant.rnaTotalDepth
                                },
                                {
                                    label: "RNA Alt Percent", value: this.formatPercent(this.currentVariant.rnaAltFrequency)
                                }
                            ]
                        };
                        this.variantDataTables.push(depthTable);
                        var dataTable = {
                            name: "dataTable",
                            items: [
                                {
                                    label: "Callers", value: this.currentVariant.callSet.join(", ")
                                },
                                {
                                    label: "Filters", value: this.currentVariant.filters.join(", ")
                                }
                            ]
                        };
                        this.variantDataTables.push(dataTable);

                        this.$refs.canonicalVariantAnnotation.manualDataFiltered(response.data.canonicalSummary);
                        this.$refs.otherVariantAnnotations.manualDataFiltered(response.data.otherSummary);

                        this.variantDetailsVisible = true;
                    }
                    else {
                        this.handleDialogs(response.data, this.getVariantDetails.bind(null, item));
                    }
                })
                .catch(error => {
                    console.log(error);
                });
        },
        updateVariantVcfAnnotationTable() {
            var items = this.currentVariant.vcfAnnotations;
            var headers = this.vcfAnnotationHeaders;
            var headerOrder = this.vcfAnnotationHeadersOrder;
            this.$refs.variantsSelected.manualDataFiltered(
                { items: items, 
                    headers: headers,
                    uniqueFieldId: "cdnaPosition",
                    headerOrder: headerOrder
             });
        },
        openVariant(item) {
            this.getVariantDetails(item);
        },
        handleIdLink(id) {
            var link = "";
            if (id.indexOf('rs') == 0) {
                link = "https://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?searchType=adhoc_search&type=rs&rs=" + id;
            }
            else if (id.indexOf('COSM') == 0) {
                link = "http://www.google.com?query=" + id;
            }
            window.open(link, "_blank");
        },
        formatIdLinkLabel(id, item) {
           var cosmicIds = item.ids.filter(id => id.indexOf('rs') != 0);
           var index = cosmicIds.indexOf(id);
            if (id.indexOf('rs') == 0) {
                return "&nbsp;" + id + "&nbsp;";
            }
            else if (id.indexOf('COSM') == 0) {
                return "&nbsp;" + id + " (" + item.cosmicPatients[index] + ")&nbsp;";
            }
        },
        formatPercent(value) {
            if (value !== null && !isNaN(value)) {
                return (Math.round(parseFloat(value) * 100000) / 1000) + "%";
            }
            return "";
        },
        addAnnotation() {
            //TODO
        },
        selectVariantForReport() {
            this.$refs.geneVariantDetails.addToSelection(this.currentRow);
            this.variantDetailsVisible = false;
        },
        removeVariantFromReport() {
            this.$refs.geneVariantDetails.removeFromSelection(this.currentRow);
            this.variantDetailsVisible = false;
        },
        updateSelectedVariantTable() {
            var selectedVariants = this.$refs.geneVariantDetails.items.filter(item => item.isSelected);
            var headers = this.$refs.geneVariantDetails.headers;
            var headerOrder = this.$refs.geneVariantDetails.headerOrder;
            this.$refs.variantsSelected.manualDataFiltered(
                { items: selectedVariants, 
                    headers: headers,
                    uniqueFieldId: "chromPos",
                    headerOrder: headerOrder
             });
        },
        openSaveDialog() {
           this.updateSelectedVariantTable();
            this.saveDialogVisible = true;
        },
        saveSelection() {
            //TODO
        }
    },
    mounted: function () {
        this.getAjaxData();
        bus.$emit("clear-item-selected", [this]);
        this.getVariantFilters();
    },
    destroyed: function () {
    },
    watch: {
        '$route': 'getAjaxData',
        'scrollPos': 'scrollWithBar'
    }
};