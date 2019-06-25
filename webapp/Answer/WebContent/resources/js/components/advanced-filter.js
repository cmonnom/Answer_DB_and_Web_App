Vue.component('advanced-filter', {
    props: {
        title: {default: "Filters", type: String},
        type: {default: "snp", type: String}
    },
    template: `<div>
    <!-- error message dialog -->
    <v-dialog v-model="messageDialogVisible" max-width="50%">
    <v-card>
        <v-card-text v-html="message" class="pl-2 pr-2 subheading">
        </v-card-text>
    </v-card>
</v-dialog>
    <!-- save filter set dialog -->
    <v-dialog v-model="saveFilterSetDialogVisible" max-width="500px">
        <v-card class="soft-grey-background">
            <v-toolbar dense dark color="primary" class="elevation-0">
                <v-toolbar-title>Save Current Filter Set
                </v-toolbar-title>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn icon @click="saveFilterSetDialogVisible = false" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close</span>
                </v-tooltip>
            </v-toolbar>
            <v-card-text>
                <v-layout row wrap class="pt-2 pl-2 pr-2">
                    <v-flex xs4 v-if="saveFilterSetId == -1" class="subheading mt-4">Filter Set Name:</v-flex>
                    <v-flex xs4 v-if="saveFilterSetId != -1" class="subheading mt-4">Modify Name:</v-flex>
                    <v-flex xs8>
                        <v-text-field v-model="saveFilterSetName" label="Filter Set Name" :rules="filterNameRules">
                        </v-text-field>
                    </v-flex>
                    <!-- show active filters -->
                    <v-flex xs12>
                        <v-divider></v-divider>
                        <div class="pt-2 pb-2">
                            <v-chip v-if="isFilterUsed(filter)" label v-for="filter in filters" :key="filter.fieldName" :color="isInputNumberValid(filter) ? 'primary' : 'error'"
                                text-color="white">
                                <v-avatar class="" v-text="getFormattedType(filter.type)"></v-avatar>
                                <span v-html="getFilterChip(filter)"></span>
                                <v-tooltip bottom>
                                    <v-btn slot="activator" dark flat icon small @click="clearFilter(filter, true)">
                                        <v-icon>close</v-icon>
                                    </v-btn>
                                    <span>Clear Filter</span>
                                </v-tooltip>
                            </v-chip>
                            <div v-if="filter.isCheckBox" v-for="filter in filters" :key="filter.headerText">
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
                    </v-flex>
                </v-layout>
            </v-card-text>
            <v-card-actions class="card-actions-bottom">
                <v-tooltip top class="pr-2">
                    <v-btn color="primary" :disabled="!isNameValid()" @click="saveCurrentFilters()" slot="activator">
                        <span v-if="saveFilterSetId == -1">Create</span>
                        <span v-if="saveFilterSetId != -1">Update</span>
                        <v-icon right dark>save</v-icon>
                    </v-btn>
                    <span>Save Filter Set</span>
                </v-tooltip>
                <v-tooltip top class="pr-2">
                    <v-btn color="warning" :disabled="saveFilterSetId == -1" @click="deleteFilterSet()" slot="activator">
                        Delete
                        <v-icon right dark>delete</v-icon>
                    </v-btn>
                    <span>Delete Filter Set</span>
                </v-tooltip>
                <v-btn class="mr-2" color="error" @click="saveFilterSetDialogVisible = false" slot="activator">Cancel
                    <v-icon right dark>cancel</v-icon>
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
    <!-- save selection dialog -->

    <div v-if="advancedFilteringVisible">
        <v-navigation-drawer app width="500" class="elevation-5">
            <v-toolbar dense>
                <v-tooltip class="ml-0" bottom>
                    <v-menu offset-y offset-x slot="activator" class="ml-0">
                        <v-btn slot="activator" flat icon dark>
                            <v-icon color="amber accent-2">filter_list</v-icon>
                        </v-btn>
                        <v-list>

                            <v-list-tile avatar @click="clearFilters" :disabled="disableFiltering">
                                <v-list-tile-avatar>
                                    <v-icon>mdi-filter-remove-outline</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title>Clear Filters</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>

                            <v-list-tile class="list-menu" :disabled="filterSets.length == 0 || disableFiltering">
                                <v-list-tile-content>
                                    <v-list-tile-title>
                                        <v-menu open-on-hover offset-x :close-on-content-click="true">
                                            <span slot="activator" class="pr-4">
                                                <v-icon class="pl-2 pr-4">keyboard_arrow_right</v-icon>Load Filter Set
                                            </span>
                                            <v-list>
                                                <v-list-tile v-for="(filterSet, index) in filterSetItems" :key="index" @click="loadSelectedFilterSet(filterSet)">
                                                    <v-list-tile-content class="pr-4">
                                                        {{ filterSet.name }}
                                                    </v-list-tile-content>
                                                    <v-list-tile-action>
                                                        <v-tooltip bottom>
                                                            <v-btn slot="activator" flat icon color="primary" @click.stop="deleteFilterSet(filterSet.value)">
                                                                <v-icon>delete</v-icon>
                                                            </v-btn>
                                                            <span>Delete Filter Set</span>
                                                        </v-tooltip>
                                                    </v-list-tile-action>
                                                </v-list-tile>
                                            </v-list>
                                        </v-menu>
                                    </v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>

                            <v-list-tile class="list-menu" :disabled="reportGroups.length == 0 || disableFiltering">
                                <v-list-tile-content>
                                    <v-menu open-on-hover offset-x :close-on-content-click="true">
                                        <span slot="activator" class="pr-4">
                                            <v-icon class="pl-2 pr-4">keyboard_arrow_right</v-icon>Load Gene Set
                                        </span>
                                        <v-list>
                                            <v-list-tile v-for="(reportGroup, index) in reportGroups" :key="index" @click="loadReportGroup(reportGroup)">
                                                <v-list-tile-content class="pr-4">
                                                    {{ reportGroup.groupName }}
                                                </v-list-tile-content>
                                            </v-list-tile>
                                        </v-list>
                                    </v-menu>
                                </v-list-tile-content>
                            </v-list-tile>

                            <v-list-tile avatar @click="openSaveFiltersDialog()" :disabled="!filtersValid || disableFiltering">
                                <v-list-tile-avatar>
                                    <v-icon>save</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title>Edit/Save Current Filter Set</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>

                            <v-list-tile avatar @click="filterData" :disabled="!filtersValid || disableFiltering">
                                <v-list-tile-avatar>
                                    <v-icon>refresh</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title>Refresh</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>

                            <v-list-tile avatar @click="toggleFilters()">
                                <v-list-tile-avatar>
                                    <v-icon>close</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title>Close Filter Menu</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>


                        </v-list>
                    </v-menu>
                    <span>Filter Menu</span>
                </v-tooltip>
                <div class="title ml-0">
                    {{ title }}
                </div>
                <v-spacer></v-spacer>
                <v-tooltip bottom>
                    <v-btn slot="activator" flat icon color="primary" @click="clearFilters" :disabled="disableFiltering">
                        <v-icon>mdi-filter-remove-outline</v-icon>
                    </v-btn>
                    <span>Clear Filters</span>
                </v-tooltip>

                <v-tooltip right>
                    <v-menu slot="activator" open-on-hover offset-y :close-on-content-click="true" :disabled="filterSets.length == 0 || disableFiltering">
                        <v-btn flat icon slot="activator" color="primary" :disabled="filterSets.length == 0 || disableFiltering">
                            <v-icon>mdi-filter-outline</v-icon>
                        </v-btn>
                        <v-list>
                            <v-list-tile v-for="(filterSet, index) in filterSetItems" :key="index" @click="loadSelectedFilterSet(filterSet)">
                                <v-list-tile-content class="pr-4">
                                    {{ filterSet.name }}
                                </v-list-tile-content>
                                <v-list-tile-action>
                                    <v-tooltip bottom>
                                        <v-btn slot="activator" flat icon color="primary" @click.stop="deleteFilterSet(filterSet.value)">
                                            <v-icon>delete</v-icon>
                                        </v-btn>
                                        <span>Delete Filter Set</span>
                                    </v-tooltip>
                                </v-list-tile-action>
                            </v-list-tile>
                        </v-list>
                    </v-menu>
                    <span>Load Filter Set</span>
                </v-tooltip>

                <v-tooltip bottom>
                    <v-btn slot="activator" flat icon @click="openSaveFiltersDialog()" :disabled="!filtersValid || disableFiltering" color="primary">
                        <v-icon>save</v-icon>
                    </v-btn>
                    <span>Edit/Save Current Filter Set</span>
                </v-tooltip>

                <v-tooltip bottom>
                    <v-btn slot="activator" flat icon :loading="loading" :color="filterNeedsReload ? 'warning' : 'primary'" @click="filterData"
                        :disabled="!filtersValid || disableFiltering">
                        <v-icon>refresh</v-icon>
                    </v-btn>
                    <span>Refresh</span>
                </v-tooltip>

                <v-tooltip bottom>
                    <v-btn icon @click="toggleFilters()" slot="activator">
                        <v-icon>close</v-icon>
                    </v-btn>
                    <span>Close Filter Menu</span>
                </v-tooltip>
            </v-toolbar>
        </v-navigation-drawer>
        <v-navigation-drawer app width="500" class="mt-5" height="calc(100% - 48px)">
            <!-- displays which filters are active -->
            <div v-if="currentFilterSet" class="pl-2 pt-2 subheading">Current Filter Set: {{ currentFilterSet.listName }}</div>
            <div class="pt-2 pb-2">
                <v-chip v-if="isFilterUsed(filter)" label v-for="(filter, index1) in filters" :key="index1" :color="getChipFilterColor(filter)"
                    text-color="white">
                    <v-avatar class="" v-text="getFormattedType(filter.type)"></v-avatar>
                    <span v-html="getFilterChip(filter)"></span>
                    <v-tooltip bottom>
                        <v-btn slot="activator" dark flat icon small @click="clearFilter(filter, true)" :disabled="disableFiltering">
                            <v-icon>close</v-icon>
                        </v-btn>
                        <span>Clear Filter</span>
                    </v-tooltip>
                </v-chip>
                <div v-if="filter.isCheckBox" v-for="(filter, index2) in filters" :key="index2">
                    <v-chip v-if="isCheckBoxFilterUsed(checkBox)" label v-for="checkBox in filter.checkBoxes" :key="checkBox.name" :color="getChipFilterColor(filter)"
                        text-color="white">
                        <span v-html="getFilterCheckBoxChip(filter, checkBox)"></span>
                        <v-tooltip bottom>
                            <v-btn slot="activator" dark flat icon small @click="clearCheckBoxFilter(checkBox)" :disabled="disableFiltering">
                                <v-icon>close</v-icon>
                            </v-btn>
                            <span>Clear Filter</span>
                        </v-tooltip>
                    </v-chip>
                </div>
            </div>
            <v-divider></v-divider>
            <!-- list of possible filters -->
            <v-container grid-list-md pl-1 pr-1 pt-1 pb-1>
                <v-form :value="filtersValid" :class="[disableFiltering ? 'grey--text lighten-1' : '']">
                    <v-layout row v-for="(filter, index3) in getFiltersByType(filters)" :key="index3" :class="[filter.button? '' : 'pr-3', 'pl-3']">

                        <v-flex xs12 v-if="filter.isSelect">
                            <v-layout>
                                <v-flex xs5 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
                                <v-flex xs7>
                                    <v-autocomplete multiple chips deletable-chips hide-details v-bind:items="filter.selectItems" clearable v-model="filter.value"
                                        item-text="name" item-value="value" :label="filter.headerText" @input="updateFilterNeedsReload(true)"
                                        auto clearable :disabled="disableFiltering"></v-autocomplete>
                                </v-flex>
                            </v-layout>
                        </v-flex>


                        <v-flex xs12 v-if="filter.isString">
                            <v-layout>
                                <v-flex xs5 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
                                <v-flex xs6>
                                    <v-tooltip right>
                                        <v-text-field slot="activator" autocomplete="off" clearable :ref="'filter' + filter.fieldName" hide-details :name="filter.fieldName"
                                            :disabled="disableFiltering" :label="filter.headerText" v-model="filter.value" @input="updateFilterNeedsReload(true, filter)"></v-text-field>
                                        <span v-html="createLongTooltip(filter)">{{ filter.tooltip }}</span>
                                    </v-tooltip>
                                    </v-flex>
                                    <v-flex xs mt-2>
                                    <v-tooltip right v-if="filter.button">
                                        <v-btn slot="activator" icon flat small @click="handleFilterAction(filter.button.action)" :color="filter.button.color" :disabled="disableFiltering || !filter.value">
                                            <v-icon> {{ filter.button.icon }} </v-icon>
                                        </v-btn>
                                        <span>{{ filter.button.tooltip }}</span>
                                    </v-tooltip>
                                </v-flex>
                            </v-layout>
                        </v-flex>

                        <v-flex xs12 v-if="filter.isDate">
                            <v-layout row>
                                <v-flex xs4 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
                                <v-flex xs4>
                                    <v-menu lazy :v-model="false" transition="scale-transition" offset-y full-width :nudge-right="40" max-width="290px" min-width="290px"
                                        :close-on-content-click="true">
                                        <v-text-field hide-details clearable slot="activator" label="From" v-model="filter.minValue" prepend-icon="event" readonly
                                            :disabled="disableFiltering" @input="updateFilterNeedsReload(true)"></v-text-field>
                                        <v-date-picker v-model="filter.minValue" no-title scrollable>
                                            <v-spacer></v-spacer>
                                        </v-date-picker>
                                    </v-menu>
                                </v-flex>
                                <v-flex xs4>
                                    <v-menu lazy :v-model="false" transition="scale-transition" offset-y full-width :nudge-right="40" max-width="290px" min-width="290px"
                                        :close-on-content-click="true">
                                        <v-text-field hide-details slot="activator" clearable label="To" v-model="filter.maxValue" prepend-icon="event" readonly
                                            :disabled="disableFiltering" @input="updateFilterNeedsReload(true)"></v-text-field>
                                        <v-date-picker v-model="filter.maxValue" no-title scrollable>
                                            <v-spacer></v-spacer>
                                        </v-date-picker>
                                    </v-menu>
                                </v-flex>
                            </v-layout>
                        </v-flex>
                    </v-layout>



                    <!-- filter flags -->
                    <v-layout row :class="[disableFiltering ? 'grey' : 'primary', 'pl-3', 'pr-3']" v-if="getFiltersByType(flagFilters).length > 0">
                        <v-expansion-panel expand class="expandable-filter elevation-0"  :value="flagExpansion">
                            <v-expansion-panel-content>
                                <div slot="header" :class="[disableFiltering ? 'grey--text lighten-1' : '', 'subheading', 'pl-1']">Flags</div>
                                <v-layout row class="pt-3" v-for="filter in getFiltersByType(flagFilters)" :key="filter.fieldName" align-end>
                                    <v-flex xs6>
                                        <v-switch :disabled="disableFiltering" hide-details color="primary" :label="filter.headerTextTrue" v-model="filter.valueTrue"
                                            @change="updateFilterNeedsReload(true)"
                                            class="no-height mt-0"></v-switch>
                                    </v-flex>
                                    <v-flex xs5>
                                        <v-switch :disabled="disableFiltering" hide-details color="primary" :label="filter.headerTextFalse" v-model="filter.valueFalse"
                                            @change="updateFilterNeedsReload(true)"
                                            class="no-height mt-0"></v-switch>
                                    </v-flex>
                                    <v-flex>
                                        <v-tooltip right>
                                            <v-icon :color="disableFiltering ? 'grey--text lighten-1' : 'primary'" slot="activator">help</v-icon>
                                            <div>
                                                <span v-show="isBooleanFilterAllOrNone(filter)">
                                                    Current Filtering Criteria:
                                                    <br/> Include both
                                                    <b>{{ filter.headerTextTrue }}</b> and
                                                    <b> {{ filter.headerTextFalse }}</b>
                                                </span>
                                                <span v-show="!isBooleanFilterAllOrNone(filter)">
                                                    Current Filtering Criteria:
                                                    <br/> Include only
                                                    <span v-show="filter.valueTrue">
                                                        <b>{{ filter.headerTextTrue }} </b>
                                                    </span>
                                                    <span v-show="filter.valueFalse">
                                                        <b> {{ filter.headerTextFalse }} </b>
                                                    </span>
                                                    (
                                                    <span v-show="!filter.valueFalse">
                                                        <b>{{ filter.headerTextFalse }}</b>
                                                    </span>
                                                    <span v-show="!filter.valueTrue">
                                                        <b>{{ filter.headerTextTrue }}</b>
                                                    </span> would be filtered out)
                                                </span>
                                            </div>
                                        </v-tooltip>
                                    </v-flex>
                                </v-layout>
                            </v-expansion-panel-content>
                        </v-expansion-panel>
                    </v-layout>

                    <!-- filter numbers -->
                    <v-layout row v-for="filter in getFiltersByType(numberFilters)" :key="filter.fieldName" class="pl-3 pr-3">
                        <v-flex xs12 v-if="filter.isNumber" pt-0 pb-0>
                            <v-layout row>
                                <v-flex xs4 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
                                <v-flex xs4>
                                    <v-text-field :disabled="disableFiltering" clearable hide-details :name="filter.fieldName + '-min'" label="Min" v-model="filter.minValue"
                                        :rules="numberRules" @input="updateFilterNeedsReload(true)"></v-text-field>
                                </v-flex>
                                <v-flex xs4>
                                    <v-text-field :disabled="disableFiltering" clearable hide-details :name="filter.fieldName + '-max'" label="Max" v-model="filter.maxValue"
                                        :rules="numberRules" @input="updateFilterNeedsReload(true)"></v-text-field>
                                </v-flex>
                            </v-layout>
                        </v-flex>
                        <v-flex xs12 v-if="filter.isReverseNumber" pt-0 pb-0>
                            <v-layout row>
                                <v-flex xs4 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
                                <v-flex xs4>
                                    <v-text-field :disabled="disableFiltering" clearable hide-details :name="filter.fieldName + '-min'" label="Less Than" v-model="filter.minValue"
                                        :rules="numberRules" @input="updateFilterNeedsReload(true)"></v-text-field>
                                </v-flex>
                                <v-flex xs4>
                                    <v-text-field :disabled="disableFiltering" clearable hide-details :name="filter.fieldName + '-max'" label="More Than" v-model="filter.maxValue"
                                        :rules="numberRules" @input="updateFilterNeedsReload(true)"></v-text-field>
                                </v-flex>
                            </v-layout>
                        </v-flex>
                    </v-layout>

                    <!-- checkbox -->
                    <v-layout row v-for="filter in getFiltersByType(checkboxFilters)" :key="filter.headerText" :class="[disableFiltering ? 'grey' : 'primary', 'pl-3', 'pr-3', 'mt-2']">
                        <v-expansion-panel expand v-if="filter.isCheckBox" class="expandable-filter elevation-0" :value="checkboxExpansion">
                            <v-expansion-panel-content >
                                <div slot="header" :class="[disableFiltering ? 'grey--text lighten-1' : '', 'subheading', 'pl-1']">
                                    {{ filter.headerText }}
                                    <v-tooltip right>
                                    <v-btn small slot="activator" :disabled="disableFiltering" flat @click.stop="toggleAllCheckBoxes(filter)" icon>
                                        <v-icon>done_all</v-icon>
                                    </v-btn>
                                    <span>Select/Unselect All</span>
                                </v-tooltip>
                                </div>
                                <v-layout row wrap>
                                    <v-flex xs12 lg6 v-for="(checkBox, index) in filter.checkBoxes" :key="index">
                                        <v-tooltip bottom>
                                            <v-checkbox class="mt-0" :disabled="disableFiltering" color="primary" slot="activator" hide-details :label="checkBox.name" v-model="checkBox.value"
                                                @change="updateFilterNeedsReload(true)"></v-checkbox>
                                            <span>{{ checkBox.name }}</span>
                                        </v-tooltip>
                                    </v-flex>
                                </v-layout>
                            </v-expansion-panel-content>
                        </v-expansion-panel>
                    </v-layout>

                </v-form>
            </v-container>
        </v-navigation-drawer>
    </div>
</div>`, data() {
        return {
            filters: [],
            effects: null,
            failedFilters: null,
            flagFilters: [],
            checkboxFilters: [],
            numberFilters: [],
            filtersValid: true,
            numberRules: [v => !isNaN(v) || 'Only numbers'],
            filterNeedsReload: true,
            currentFilterSet: "",
            filterSets: [],
            filterSetItems: [],
            saveFilterSetName: "",
            saveFilterSetId: -1,
            saveFilterSetDialogVisible: false,
            advancedFilteringVisible: false,
            loading: false,
            reportGroups: [],
            disableFiltering: false,
            filterNameRules: [v => { return /^[a-zA-Z0-9_. -]*$/.test(v) || "Only Letters and Numbers" }],
            messageDialogVisible: false,
            message: "",
            checkboxExpansion: [true, true, true, true], //controls the open state of each panel. Add more items here when creating new expandable panels
            flagExpansion: [true]
        }

    },
    methods: {
        createFilters(filters) {
            this.filters = filters;
            this.populateCheckBoxes();
            this.populateFlagFilter();
            this.populateNumberFilter();
        },
        populateCheckBoxes() {
            this.checkboxFilters = [];
            for (var i = 0; i < this.filters.length; i++) {
                var filter = this.filters[i];
                if (filter.isCheckBox) {
                    this.checkboxFilters.push(filter);
                    if (filter.fieldName == 'effects') {
                        if (filter.checkBoxes.length > 0) {
                            continue;
                        }
                        filter.checkBoxes = [];
                        //impacts can be HIGH MODERATE LOW MODIFIER
                        //each "effect" filter should have a category with the labels above
                        //compare the filter category to the effect category to populate the correct filter checkboxes
                        for (var impact in this.effects) {
                            if (impact == filter.category && this.effects && this.effects[impact]) {
                                for (var j = 0; j < this.effects[impact].length; j++) {
                                    filter.checkBoxes.push({ name: this.effects[impact][j], value: false});
                                }
                            }
                        }
                    }
                    else if (filter.fieldName == 'filters') {
                        if (filter.checkBoxes.length > 0) {
                            continue;
                        }
                        filter.checkBoxes = [];
                        if (this.failedFilters) {
                            for (var j = 0; j < this.failedFilters.length; j++) {
                                filter.checkBoxes.push({ name: this.failedFilters[j], value: false});
                            }
                        }
                    }
                }
            }
        },
        toggleAllCheckBoxes(filter) {
            var selectAll = filter.checkBoxes[0].value;
            for (var i = 0; i < filter.checkBoxes.length; i++) {
                filter.checkBoxes[i].value = !selectAll;
            }
            this.updateFilterNeedsReload(true);
        },
        populateFlagFilter() {
            this.flagFilters = [];
            for (var i = 0; i < this.filters.length; i++) {
                var filter = this.filters[i];
                if (filter.isBoolean) {
                    this.flagFilters.push(filter);
                }
            }
        },
        populateNumberFilter() {
            this.numberFilters = [];
            for (var i = 0; i < this.filters.length; i++) {
                var filter = this.filters[i];
                if (filter.isNumber || filter.isReverseNumber) {
                    this.numberFilters.push(filter);
                }
            }
        },
        filterData() {
            this.$emit("refresh-data", null);
        },
        clearFilters() {
            for (var i = 0; i < this.filters.length; i++) {
                var filter = this.filters[i];
                this.clearFilter(filter);
            }
            this.currentFilterSet = "";
            this.filterData();
        },
        clearFilter(filter, doRefresh) {
            filter.value = [];
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
                this.currentFilterSet = "";
                this.filterData();
            }
            this.$emit("update-highlight", filter);
        },
        clearCheckBoxFilter(checkBox) {
            checkBox.value = false;
            this.currentFilterSet = "";
            this.filterData();
        },
        getFilterChip(filter) {
            if (filter.isSelect) {
                return filter.headerText + ": <b>" + filter.value + "</b>";
            }
            if (filter.isBoolean) {
                return "Include " + filter.headerTextTrue
                    + ": <b>" + (filter.valueTrue ? 'YES ' : 'NO ')
                    + "</b><br/>" + "Include "
                    + filter.headerTextFalse + ": <b>"
                    + (filter.valueFalse ? 'YES' : 'NO') + "</b>";
            } if (filter.isString) {
                var finalItems = [];
                var geneItems = filter.value.split(",");
                var geneItemsUnique = new Set();
                for (var i = 0; i < geneItems.length; i++) {
                    geneItemsUnique.add(geneItems[i].trim());
                }
                for (var i = 0; i < this.reportGroups.length; i++) {
                    var foundReportGroup = true;
                    var genesToReport = this.reportGroups[i].genesToReport;
                    if (geneItemsUnique.size >= genesToReport.length) {
                        //could be a reportGroup. Need to compare genes
                        for (var j = 0; j < genesToReport.length; j++) {
                            if (!geneItemsUnique.has(genesToReport[j])) {
                                //not a full report group
                                foundReportGroup = false;
                                break;
                            }
                        }
                        if (foundReportGroup) {
                            //delete all genes from reportGroup
                            for (var j = 0; j < genesToReport.length; j++) {
                                geneItemsUnique.delete(genesToReport[j]);
                            }
                            finalItems.push(this.reportGroups[i].groupName);
                        }
                    }
                }
                var iterator = geneItemsUnique.values();
                for (var i = 0; i < geneItemsUnique.size; i++) { //in case some genes are left
                    finalItems.push(iterator.next().value);
                }
                var displayValue = finalItems.join(", ");
                if (displayValue.length > 30) {
                    return filter.headerText + " contains <b>" + displayValue.substring(0, Math.min(30, displayValue.length)) + "...</b>";
                }
                else {
                    return filter.headerText + " contains <b>" + displayValue + "</b>";
                }
            } if (filter.isNumber  || filter.isReverseNumber) { // 
                return filter.headerText + ": <b>[ "
                    + (filter.minValue != null ? filter.minValue : '') + ":"
                    + (filter.maxValue != null ? filter.maxValue : '') + " ]</b>";
            } //TODO dates and numbers
            return filter.headerText;
        },
        getFilterCheckBoxChip(filter, checkBox) {
            return filter.headerText + ": <b>" + checkBox.name
                + "</b>";
        },
        toggleFilters() {
            this.advancedFilteringVisible = !this.advancedFilteringVisible;
            if (!this.advancedFilteringVisible) {
                bus.$emit("need-layout-resize", this);
            }
        },
        isFilterUsed(filter) {
            if (filter.isCheckBox) {
                return false; //handled in a separate chip
            }
            if (filter.isNumber  || filter.isReverseNumber) {
                return (filter.minValue != null && filter.minValue !== "")
                    || (filter.maxValue != null && filter.maxValue !== "")
            }
            if (filter.isDate) {
                return filter.minDateValue != null || filter.maxDateValue != null;
            }
            if (filter.isBoolean) {
                return filter.valueTrue == true || filter.valueFalse == true;
            }
            return filter.value != null && filter.value.length > 0;
        },
        isCheckBoxFilterUsed(checkBox) { return checkBox.value == true; }, isInputNumberValid(filter) {
            if (filter.isNumber  || filter.isReverseNumber) {
                var isValid = (filter.minValue === "" || !isNaN(filter.minValue)) && (filter.maxValue === "" || !isNaN(filter.maxValue));
                if (isValid) {
                    if (filter.minValue !== "" && filter.minValue != null) {
                        filter.minValue = parseFloat(filter.minValue);
                    }
                    if (filter.maxValue !== "" && filter.maxValue != null) {
                        filter.maxValue = parseFloat(filter.maxValue);
                    }
                } return isValid;
            } return true;
        },
        isAnyFilterUsed() {
            for (var i = 0; i < this.filters.length; i++) {
                if (this.isFilterUsed(this.filters[i])) {
                    return true;
                }
            }
            return false;
        },
        openSaveFiltersDialog() {
            if (this.currentFilterSet) {
                this.saveFilterSetId = this.currentFilterSet.variantFilterListId;
                this.saveFilterSetName = this.currentFilterSet.listName;
            }
            else { //this is a new filter set
                this.saveFilterSetId = -1;
                this.saveFilterSetName = "";
            }
            this.saveFilterSetDialogVisible = true;
        },
        isNameValid() {
            return this.saveFilterSetName && this.filterNameRules[0](this.saveFilterSetName) === true;
        },
        saveCurrentFilters() {
            this.$emit("save-filters", null);
        },
        deleteFilterSet(filterSetId) {
            if (this.currentFilterSet && filterSetId == this.currentFilterSet.variantFilterListId) {
                this.currentFilterSet = "";
            }
            this.$emit("delete-filter", filterSetId);
        },
        // used in the filter tooltip to inform user of the implication
        //of the selected criteria
        isBooleanFilterAllOrNone(filter) {
            if (filter.valueTrue == null && filter.valueFalse == null) {
                return true;
            }
            if (filter.valueTrue == null && filter.valueFalse == false) {
                return true;
            }
            if (filter.valueFalse == null && filter.valueTrue == false) {
                return true;
            }
            return filter.valueTrue == filter.valueFalse;
        },
        // find the filterset loaded by the user and populates all the relevant form fields
        loadSelectedFilterSet(filterSet) {
            this.clearFilters();
            this.currentFilterSet = this.filterSets.filter(f => f.variantFilterListId == filterSet.value)[0];
            for (var i = 0; i < this.currentFilterSet.filters.length; i++) {
                var filter = this.currentFilterSet.filters[i];
                var filterToPopulate = this.filters.filter(f => f.fieldName == filter.field)[0];
                this.populateFilter(filterToPopulate, filter);
            }
            this.$emit("refresh-data", null);
        },
        populateFilter(filterToPopulate, loadedFilter) {
            var multiplier = 1;
            if (filterToPopulate.fieldName.includes("Frequency")) {
                multiplier = 100;
            }
            if (filterToPopulate.isSelect && loadedFilter.value) {
                filterToPopulate.value = loadedFilter.value.split(",");
            }
            else {
                filterToPopulate.value = loadedFilter.value;
            }
            filterToPopulate.minValue = loadedFilter.minValue != null ? (loadedFilter.minValue * multiplier).toFixed(5) : null;
            filterToPopulate.maxValue = loadedFilter.maxValue != null ? (loadedFilter.maxValue * multiplier).toFixed(5) : null;
            filterToPopulate.minDateValue = null;
            filterToPopulate.maxDateValue = null;
            filterToPopulate.valueTrue = loadedFilter.valueTrue;
            filterToPopulate.valueFalse = loadedFilter.valueFalse;
            if (filterToPopulate.isCheckBox) {
                for (var i = 0; i < filterToPopulate.checkBoxes.length; i++) {
                    var field = filterToPopulate.checkBoxes[i].name.replace(/ /g, "_").toLowerCase();
                    var checkboxHasValue = false;
                    for (var j = 0; j < loadedFilter.stringValues.length; j++) {
                        var currentCheckBoxValue = loadedFilter.stringValues[j].filterString;
                        if (currentCheckBoxValue == field) {
                            checkboxHasValue = true;
                        }
                    }
                    filterToPopulate.checkBoxes[i].value = checkboxHasValue;
                }
            }
        },
        updateFilterNeedsReload(needsReload, filter) {
            this.filterNeedsReload = needsReload;
            if (needsReload) {
                this.currentFilterSet = "";
            }
            if (filter && filter.fieldName == "cnvGeneName") {
                //update table to highlight genes
                this.$emit("update-highlight", filter);
            }
        },
        //add the list of genes from the given reportGroup
        //to the gene name filter
        loadReportGroup(reportGroup) {
            var geneNameField = "geneName";
            if (this.type == "cnv") {
                geneNameField = "cnvGeneName";
            }
            for (var i = 0; i < this.filters.length; i++) {
                var filter = this.filters[i];
                if (filter.fieldName == geneNameField) {
                    var geneNames = reportGroup.genesToReport.join(", ");
                    filter.value = geneNames;
                    this.updateFilterNeedsReload(true, filter);
                    break;

                }
            }
        },
        getChipFilterColor(filter) {
            if (this.disableFiltering) {
                return "grey";
            }
            return this.isInputNumberValid(filter) ? 'primary' : 'error';
        },
        getFiltersByType(filters) {
            return filters.filter(f => f.type == this.type);
        },
        getFormattedType(type) {
            if (type == "snp" || type == "cnv") {
                return type.toUpperCase();
            }
            else if (type == "translocation") {
                return "FTL"; //place holder. Find a better way to handle this
            }
        },
        createLongTooltip(filter) {
            var tooltip = "Separate Gene Names by comma.";
            var items = null;
            if (filter.value) {
                tooltip = "";
                if (Array.isArray(filter.value)) {
                    items = filter.value;
                }
                else {
                    items = filter.value.split(",");
                }
                for (var i = 0; i < items.length; i++) {
                    var item = items[i].trim();
                    tooltip += item;
                    if (i > 0 && i % 5 == 0) {
                        tooltip += "<br/>";
                    }
                    else {
                        tooltip += " ";
                    }
                }
            }
            return tooltip;
        },
        handleFilterAction(action) {
            switch(action) {
               case "verifyGeneNames": this.verifyGeneNames("snp"); break;
               case "verifyCNVGeneNames": this.verifyGeneNames("cnv"); break;

            }
        },
        verifyGeneNames(type) {
            var genesFilter = null;
            if (type == "snp") {
                genesFilter = this.filters.filter(f => f.fieldName == "geneName")[0].value;
            }
            else {
                genesFilter = this.filters.filter(f => f.fieldName == "cnvGeneName")[0].value;
            }
            if (!genesFilter || genesFilter.length == 0) {
                return;
            }
            axios({
                method: 'post',
                url: webAppRoot + "/verifyGeneNames",
                params: {
                    type: type,
                    genesParam: genesFilter
                },
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        //update geneName or cnvGeneName and inform user if any gene was removed
                        if (response.data.message) {
                            this.message = response.data.message;
                            this.messageDialogVisible = true;
                        }
                        else {
                            this.$emit("filter-action-success", "All genes are valid.");
                        }
                    } else {
                        this.handleDialogs(response.data, this.verifyGeneNames.bind(null, type));
                    }
                })
                .catch(error => {
                    console.log(error);
                    bus.$emit("some-error", [this, error]);
                });
        },
        handleDialogs(response, callback) {
            if (response == "not-allowed") {
                bus.$emit("not-allowed", [this.reponse]);
            }
            if (response.isXss) {
                bus.$emit("xss-error",
                    [this, response.reason]);
            }
            else if (response.isLogin) {
                bus.$emit("login-needed", [this, callback])
            }
            else if (response.success === false) {
                this.splashProgress = 100; //should dismiss the splash dialog
                bus.$emit("some-error", [this, response.message]);
            }
        },
    },
    created: function () {
    },
    destroyed: function () {
    },
    watch: {
    }


});