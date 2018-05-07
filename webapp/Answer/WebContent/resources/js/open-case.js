const OpenCase = {
    template: `<div>
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
                <v-chip v-if="isFilterUsed(filter)" label v-for="filter in filters" :key="filter.fieldName" :color="isInputNumberValid(filter) ? 'primary' : 'error'" text-color="white">
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
                        <v-select v-bind:items="filter.selectItems" v-model="filter.value" item-text="name" item-value="value" :label="filter.headerText"
                        @input="filterNeedsReload = true"
                            auto autocomplete clearable></v-select>
                    </v-flex>

                    <v-flex xs12 v-if="filter.isBoolean">
                        <v-layout row>
                            <v-flex xs6>
                                <v-switch class="no-height" color="primary" :label="filter.headerTextTrue" v-model="filter.valueTrue"
                                @change="filterNeedsReload = true"></v-switch>
                            </v-flex>
                            <v-flex xs6>
                                <v-switch class="no-height" color="primary" :label="filter.headerTextFalse" v-model="filter.valueFalse"
                                @change="filterNeedsReload = true"></v-switch>
                            </v-flex>
                        </v-layout>
                    </v-flex>

                    <v-expansion-panel expand v-if="filter.isCheckBox" class="expandable-filter elevation-0">
                        <v-expansion-panel-content>
                            <div slot="header" class="subheading pl-1">{{ filter.headerText }}</div>
                            <v-layout row wrap>
                                <v-flex xs12 lg6 v-for="(checkBox, index) in filter.checkBoxes" :key="index">
                                    <v-tooltip bottom>
                                    <v-checkbox color="primary" slot="activator" class="no-height" :label="checkBox.name" v-model="checkBox.value"
                                    @change="filterNeedsReload = true"></v-checkbox>
                                    <span>{{ checkBox.name }}</span>
                                </v-tooltip>
                                </v-flex>
                            </v-layout>
                        </v-expansion-panel-content>
                    </v-expansion-panel>

                    <v-flex xs12 v-if="filter.isString">
                        <v-text-field class="no-height" :name="filter.fieldName" :label="filter.headerText" v-model="filter.value"
                        @input="filterNeedsReload = true"></v-text-field>
                    </v-flex>

                    <v-flex xs12 v-if="filter.isNumber">
                        <v-layout row>
                            <v-flex xs4 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
                            <v-flex xs4>
                                <v-text-field class="no-height" :name="filter.fieldName + '-min'" label="Min" v-model="filter.minValue" :rules="numberRules" @input="filterNeedsReload = true"></v-text-field>
                            </v-flex>
                            <v-flex xs4>
                                <v-text-field class="no-height" :name="filter.fieldName + '-max'" label="Max" v-model="filter.maxValue" :rules="numberRules" @input="filterNeedsReload = true"></v-text-field>
                            </v-flex>
                        </v-layout>
                    </v-flex>

                    <v-flex xs12 v-if="filter.isDate">
                        <v-layout row>
                            <v-flex xs4 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
                            <v-flex xs4>
                                <v-menu lazy :v-model="false" transition="scale-transition" offset-y full-width :nudge-right="40" max-width="290px" min-width="290px"
                                    :close-on-content-click="true">
                                    <v-text-field class="no-height" slot="activator" label="From" v-model="filter.minValue" prepend-icon="event" readonly
                                    @input="filterNeedsReload = true"></v-text-field>
                                    <v-date-picker v-model="filter.minValue" no-title scrollable>
                                        <v-spacer></v-spacer>
                                    </v-date-picker>
                                </v-menu>
                            </v-flex>
                            <v-flex xs4>
                                <v-menu lazy :v-model="false" transition="scale-transition" offset-y full-width :nudge-right="40" max-width="290px" min-width="290px"
                                    :close-on-content-click="true">
                                    <v-text-field class="no-height" slot="activator" label="To" v-model="filter.maxValue" prepend-icon="event" readonly
                                    @input="filterNeedsReload = true"></v-text-field>
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
                :color="patientDetailsVisible ? 'lime accent-2' : ''">
                <v-icon>perm_identity</v-icon>
            </v-btn>
            <span>Patient Details</span>
        </v-tooltip>
    </v-toolbar>

    <v-layout v-if="patientDetailsVisible">
        <v-flex xs12 md12 lg10 xl9>
            <div class="text-xs-center pb-3">
                <v-toolbar dark color="primary">
                    <v-icon>perm_identity</v-icon>
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
                </v-card>
            </div>
        </v-flex>
    </v-layout>

    <data-table ref="geneVariantDetails" :fixed="false" :fetch-on-created="false" table-title="Variants" initial-sort="chromPos"
        no-data-text="No Data" :enable-selection="true">
        <v-tooltip bottom slot="action1">
            <v-btn slot="activator" flat icon @click="toggleFilters" :color="advanceFilteringVisible ? 'lime accent-2' : 'white'">
                <v-icon>filter_list</v-icon>
            </v-btn>
            <span>Advanced Filtering</span>
        </v-tooltip>
    </data-table>
</div>` , data() {
        return {
            loading: true,
            patientTables: [],
            patientDetailsVisible: true,
            caseName: "",
            caseId: "",
            advanceFilteringVisible: false,
            filters: [],
            effects: [],
            filtersValid: true,
            numberRules: [v => !isNaN(v) || 'Only numbers'],
            filterNeedsReload: true
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