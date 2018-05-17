Vue.component('advanced-filter', {
  props: {
  },
  template: `<div>
  <!-- save filter set dialog -->
    <v-dialog v-model="saveFilterSetDialogVisible" max-width="500px">
        <v-card class="soft-grey-background">
            <v-toolbar dark color="primary">
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
                        <v-text-field v-model="saveFilterSetName" label="Filter Set Name">
                        </v-text-field>
                    </v-flex>
                    <!-- show active filters -->
                    <v-flex xs12>
                        <v-divider></v-divider>
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
                    </v-flex>
                </v-layout>
            </v-card-text>
            <v-card-actions>
                <v-tooltip top>
                    <v-btn color="primary" :disabled="!saveFilterSetName" @click="saveCurrentFilters()" slot="activator">
                        <span v-if="saveFilterSetId == -1">Create</span>
                        <span v-if="saveFilterSetId != -1">Update</span>
                        <v-icon right dark>save</v-icon>
                    </v-btn>
                    <span>Save Filter Set</span>
                </v-tooltip>
                <v-tooltip top>
                    <v-btn color="warning" :disabled="saveFilterSetId == -1" @click="deleteFilterSet()" slot="activator">
                        Delete
                        <v-icon right dark>delete</v-icon>
                    </v-btn>
                    <span>Delete Filter Set</span>
                </v-tooltip>
                <v-btn color="error" @click="saveFilterSetDialogVisible = false" slot="activator">Cancel
                    <v-icon right dark>cancel</v-icon>
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
    <!-- save selection dialog -->
  
  <div v-if="advanceFilteringVisible">
      <v-navigation-drawer app width="500" class="elevation-5">
          <v-toolbar>
              <v-tooltip class="ml-0" bottom>
                  <v-menu offset-y offset-x slot="activator" class="ml-0">
                      <v-btn slot="activator" flat icon dark>
                          <v-icon color="amber accent-2">filter_list</v-icon>
                      </v-btn>
                      <v-list>

                          <v-list-tile avatar @click="clearFilters">
                              <v-list-tile-avatar>
                                  <v-icon>mdi-filter-remove-outline</v-icon>
                              </v-list-tile-avatar>
                              <v-list-tile-content>
                                  <v-list-tile-title>Clear Filters</v-list-tile-title>
                              </v-list-tile-content>
                          </v-list-tile>

                          <v-list-tile avatar :disabled="filterSets.length == 0">
                              <v-list-tile-avatar>
                                  <v-icon>mdi-filter-outline</v-icon>
                              </v-list-tile-avatar>
                              <v-list-tile-content>
                                  Load Filter Set
                              </v-list-tile-content>
                              <v-list-tile-action>
                                  <v-menu open-on-hover offset-x :close-on-content-click="true">
                                      <v-btn flat icon slot="activator">
                                          <v-icon>keyboard_arrow_right</v-icon>
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
                              </v-list-tile-action>
                          </v-list-tile>

                          <v-list-tile avatar @click="openSaveFiltersDialog()" :disabled="!filtersValid">
                              <v-list-tile-avatar>
                                  <v-icon>save</v-icon>
                              </v-list-tile-avatar>
                              <v-list-tile-content>
                                  <v-list-tile-title>Edit/Save Current Filter Set</v-list-tile-title>
                              </v-list-tile-content>
                          </v-list-tile>

                          <v-list-tile avatar @click="filterData" :disabled="!filtersValid">
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
                  Filters
              </div>
              <v-spacer></v-spacer>
              <v-tooltip bottom>
                  <v-btn slot="activator" flat icon color="primary" @click="clearFilters">
                      <v-icon>mdi-filter-remove-outline</v-icon>
                  </v-btn>
                  <span>Clear Filters</span>
              </v-tooltip>

              <v-tooltip right>
                  <v-menu slot="activator" open-on-hover offset-y :close-on-content-click="true" :disabled="filterSets.length == 0">
                      <v-btn flat icon slot="activator" color="primary" :disabled="filterSets.length == 0">
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
                  <v-btn slot="activator" flat icon @click="openSaveFiltersDialog()" :disabled="!filtersValid" color="primary">
                      <v-icon>save</v-icon>
                  </v-btn>
                  <span>Edit/Save Current Filter Set</span>
              </v-tooltip>

              <v-tooltip bottom>
                  <v-btn slot="activator" flat icon :loading="loading" :color="filterNeedsReload ? 'warning' : 'primary'" @click="filterData"
                      :disabled="!filtersValid">
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
      <v-navigation-drawer app width="500" class="mt-6" height="calc(100% - 64px)">
          <!-- displays which filters are active -->
          <div v-if="currentFilterSet" class="pl-2 pt-2 subheading">Current Filter Set: {{ currentFilterSet.listName }}</div>
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
                                  <v-select class="no-height" v-bind:items="filter.selectItems" v-model="filter.value" item-text="name" item-value="value"
                                      :label="filter.headerText" @input="updateFilterNeedsReload(true)" auto autocomplete clearable></v-select>
                              </v-flex>
                          </v-layout>
                      </v-flex>

                      <v-flex xs12 v-if="filter.isBoolean">
                          <v-layout row class="pt-3">
                              <v-flex xs6>
                                  <v-switch class="no-height" color="primary" :label="filter.headerTextTrue" v-model="filter.valueTrue" @change="updateFilterNeedsReload(true)"></v-switch>
                              </v-flex>
                              <v-flex xs6>
                                  <v-switch class="no-height" color="primary" :label="filter.headerTextFalse" v-model="filter.valueFalse" @change="updateFilterNeedsReload(true)"></v-switch>
                              </v-flex>
                          </v-layout>
                      </v-flex>

                      <v-expansion-panel expand v-if="filter.isCheckBox" class="expandable-filter elevation-0">
                          <v-expansion-panel-content :value="true">
                              <div slot="header" class="subheading pl-1">{{ filter.headerText }}</div>
                              <v-layout row wrap>
                                  <v-flex xs12 lg6 v-for="(checkBox, index) in filter.checkBoxes" :key="index">
                                      <v-tooltip bottom>
                                          <v-checkbox color="primary" slot="activator" class="no-height" :label="checkBox.name" v-model="checkBox.value" @change="updateFilterNeedsReload(true)"></v-checkbox>
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
                                  <v-text-field class="no-height" :name="filter.fieldName" :label="filter.headerText" v-model="filter.value" @input="updateFilterNeedsReload(true)"></v-text-field>
                              </v-flex>
                          </v-layout>
                      </v-flex>

                      <v-flex xs12 v-if="filter.isNumber">
                          <v-layout row>
                              <v-flex xs4 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
                              <v-flex xs4>
                                  <v-text-field class="no-height" :name="filter.fieldName + '-min'" label="Min" v-model="filter.minValue" :rules="numberRules"
                                      @input="updateFilterNeedsReload(true)"></v-text-field>
                              </v-flex>
                              <v-flex xs4>
                                  <v-text-field class="no-height" :name="filter.fieldName + '-max'" label="Max" v-model="filter.maxValue" :rules="numberRules"
                                      @input="updateFilterNeedsReload(true)"></v-text-field>
                              </v-flex>
                          </v-layout>
                      </v-flex>

                      <v-flex xs12 v-if="filter.isDate">
                          <v-layout row>
                              <v-flex xs4 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
                              <v-flex xs4>
                                  <v-menu lazy :v-model="false" transition="scale-transition" offset-y full-width :nudge-right="40" max-width="290px" min-width="290px"
                                      :close-on-content-click="true">
                                      <v-text-field class="no-height" slot="activator" label="From" v-model="filter.minValue" prepend-icon="event" readonly @input="updateFilterNeedsReload(true)"></v-text-field>
                                      <v-date-picker v-model="filter.minValue" no-title scrollable>
                                          <v-spacer></v-spacer>
                                      </v-date-picker>
                                  </v-menu>
                              </v-flex>
                              <v-flex xs4>
                                  <v-menu lazy :v-model="false" transition="scale-transition" offset-y full-width :nudge-right="40" max-width="290px" min-width="290px"
                                      :close-on-content-click="true">
                                      <v-text-field class="no-height" slot="activator" label="To" v-model="filter.maxValue" prepend-icon="event" readonly @input="updateFilterNeedsReload(true)"></v-text-field>
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
  </div>`,
  data() {
    return {
      filters: [],
      effects: [],
      filtersValid: true,
      numberRules: [v => !isNaN(v) || 'Only numbers'],
      filterNeedsReload: true,
      currentFilterSet: "",
      filterSets: [],
      filterSetItems: [],
      saveFilterSetName: "",
      saveFilterSetId: -1,
      saveFilterSetDialogVisible: false,
      advanceFilteringVisible: false,
      loading: false
    }

  },
  methods: {
    createFilters(filters) {
      this.filters = filters;
      this.populateCheckBoxes();
    },
    populateCheckBoxes() {
      for (var i = 0; i
        < this.filters.length; i++) {
        var filter = this.filters[i]; if (filter.isCheckBox) {
          if (filter.fieldName == 'effects') {
            if
            (filter.checkBoxes.length > 0) { return; } filter.checkBoxes = []; for (var j = 0; j
              < this.effects.length; j++) { filter.checkBoxes.push({ name: this.effects[j], value: false }); }
          }
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
        this.currentFilterSet = "";
        this.filterData();
      }
    },
    clearCheckBoxFilter(checkBox) {
      checkBox.value = false;
      this.currentFilterSet = "";
      this.filterData();
    },
    getFilterChip(filter) { //TODO 
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
        return filter.headerText + " contains <b>" + filter.value + "</b>";
      } if (filter.isNumber) { // 
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
    isCheckBoxFilterUsed(checkBox) { return checkBox.value == true; }, isInputNumberValid(filter) {
      if (filter.isNumber) {
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
    saveCurrentFilters() {
      this.$emit("save-filters", null);
    },
    deleteFilterSet(filterSetId) {
      if (this.currentFilterSet && filterSetId == this.currentFilterSet.variantFilterListId) {
        this.currentFilterSet = "";
      }
      this.$emit("delete-filter", filterSetId);
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
      filterToPopulate.value = loadedFilter.value;
      filterToPopulate.minValue = loadedFilter.minValue * multiplier;
      filterToPopulate.maxValue = loadedFilter.maxValue * multiplier;
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
    updateFilterNeedsReload(needsReload) {
      this.filterNeedsReload = needsReload;
      if (needsReload) {
        this.currentFilterSet = "";
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