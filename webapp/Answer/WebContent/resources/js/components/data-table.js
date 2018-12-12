

Vue.component('data-table', {
    props: {
        "table-title": { default: "Table", type: String },
        "data-url": "",
        "expanded-data-url": "",
        "expanded-data-url2": "",
        "initial-sort": { default: "id", type: String },
        "sort-descending": { default: false, type: Boolean },
        "toolbar-visible": { default: true, type: Boolean },
        "fetch-on-created": { default: true, type: Boolean },
        "no-data-text": { default: "Fetching data...", type: String },
        "fixed": { default: true, type: Boolean },
        "advance-filtering": { default: false, type: Boolean },
        "export-enabled": { default: false, type: Boolean },
        "enable-selection": { default: false, type: Boolean },
        "enable-select-all": { default: false, type: Boolean },
        "action1-param": { default: "", type: String },
        "show-pagination": { default: true, type: Boolean },
        "show-row-count": { default: false, type: Boolean },
        "title-icon": { default: null, type: String },
        "show-left-menu": { default: true, type: Boolean },
        "color": { default: "primary", type: String },
        "disable-sticky-header": {default: false, type: Boolean},
        highlights: { default: () => {}, type: Object },

    },
    template: `<div>
    <!-- Comment above and uncomment below to use the buttons on hover feature -->
     <!-- <div @mouseover="toggleShowButtons(true)" @mouseleave="toggleShowButtons(false)"> -->
  <!-- Top tool bar with menu options -->
  <v-toolbar dense dark :color="color" :class="fixed ? '' : 'elevation-0'" :fixed="fixed" :app="fixed" v-show="toolbarVisible">
    <!-- icon with no function -->
    <v-icon v-if="titleIcon && !showLeftMenu" color="amber accent-2">{{ titleIcon }}</v-icon>
    <!-- menu with same functions as left side icons -->
    <v-tooltip class="ml-0" bottom v-if="showLeftMenu">
      <v-menu offset-y offset-x slot="activator" class="ml-0">
        <v-btn slot="activator" flat icon dark>
          <v-icon v-if="!titleIcon">more_vert</v-icon>
          <v-icon v-if="titleIcon" color="amber accent-2">{{ titleIcon }}</v-icon>
        </v-btn>
        <v-list>

          <slot name="action1MenuItem"></slot>
          <slot name="action2MenuItem"></slot>
          <slot name="action3MenuItem"></slot>

          <v-list-tile avatar @click="toggleSearchBar">
            <v-list-tile-avatar>
              <v-icon>search</v-icon>
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title>Quick Filter</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>

          <v-list-tile avatar v-if="advanceFiltering" @click="toggleFilters">
            <v-list-tile-avatar>
              <v-icon>filter_list</v-icon>
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title>Filter Menu</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>

          <v-list-tile avatar v-if="exportEnabled" @click="exportToCSV">
            <v-list-tile-avatar>
              <v-icon>file_download</v-icon>
            </v-list-tile-avatar>
            <v-list-tile-content></v-list-tile-content>
            <v-list-tile-title>Export to CSV</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>

          <v-list-tile avatar @click="showDraggableHeader=!showDraggableHeader">
            <v-list-tile-avatar>
              <v-icon>swap_horiz</v-icon>
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title>Move Columns</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>

          <v-list-tile avatar @click="handleRefresh()">
            <v-list-tile-avatar>
              <v-icon>refresh</v-icon>
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title>Refresh</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>

        </v-list>
      </v-menu>
      <span>Table Menu</span>
    </v-tooltip>

    <v-toolbar-title class="white--text ml-0">{{ tableTitle }}
      <span v-if="showRowCount" v-html="getRowCount()"></span>
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <v-fade-transition>
      <v-layout justify-end v-show="showButtons">
        <v-flex class="text-xs-right">
          <v-container>
            <slot name="title"></slot>
          </v-container>
        </v-flex>
        <v-flex xs7 class="text-xs-right" v-show="showPagination">
          <div class="title white--text pr-1 pt-4 mt-1">Rows:</div>
        </v-flex>
        <v-flex xs3 v-show="showPagination" class="data-table-row-input">
          <v-container>
            <v-text-field solo single-line hide-details light v-model="pagination.rowsPerPage"></v-text-field>
          </v-container>
        </v-flex>
      </v-layout>
    </v-fade-transition>

      <slot name="action1"></slot>
      <slot name="action2"></slot>
      <slot name="action3"></slot>

    <v-fade-transition>
      <v-tooltip bottom v-show="showButtons">
        <v-btn flat icon @click="toggleSearchBar" slot="activator" :color="showSearchBar ? 'amber accent-2' : 'white'">
          <v-icon>search</v-icon>
        </v-btn>
        <span>Quick Filter</span>
      </v-tooltip>
    </v-fade-transition>

    <v-fade-transition>
      <v-tooltip bottom v-if="advanceFiltering" v-show="showButtons">
        <v-btn flat icon @click="toggleFilters" slot="activator" :color="showDrawer ? 'amber accent-2' : 'white'">
          <v-icon>filter_list</v-icon>
        </v-btn>
        <span>Filter Menu</span>
      </v-tooltip>
    </v-fade-transition>

    <v-fade-transition>
      <v-tooltip bottom v-if="exportEnabled" v-show="showButtons">
        <v-btn flat icon @click="exportToCSV" slot="activator">
          <v-icon>file_download</v-icon>
        </v-btn>
        <span>Export to CSV
          <br/>(works with Filter Menu)</span>
      </v-tooltip>
    </v-fade-transition>

    <v-fade-transition>
      <v-tooltip bottom v-show="showButtons">
        <v-btn flat icon @click="showDraggableHeader=!showDraggableHeader" slot="activator" :color="showDraggableHeader ? 'amber accent-2' : 'white'">
          <v-icon>swap_horiz</v-icon>
        </v-btn>
        <span>Move Columns</span>
      </v-tooltip>
    </v-fade-transition>

    <v-fade-transition>
      <v-tooltip bottom v-show="showButtons">
        <v-btn flat icon @click="handleRefresh()" slot="activator">
          <v-icon>refresh</v-icon>
        </v-btn>
        <span>Refresh</span>
      </v-tooltip>
    </v-fade-transition>
  </v-toolbar>

  <!-- Search Bar -->
  <v-slide-y-transition>
    <v-toolbar dense light class="mt-1" v-show="showSearchBar">
      <v-toolbar-title class="subheading">
        <v-btn icon @click="toggleSearchBar" slot="activator">
          <v-icon>keyboard_arrow_up</v-icon>
        </v-btn>
        Quick Filter
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-text-field clearable ref="search" append-icon="search" label="Search" single-line hide-details v-model="search"></v-text-field>
    </v-toolbar>
  </v-slide-y-transition>

  <!-- Draggable Header -->
  <v-slide-y-transition>
    <div v-show="showDraggableHeader">
      <v-toolbar dense light class="mt-1">
        <v-toolbar-title class="subheading">
          <v-btn icon @click="showDraggableHeader=!showDraggableHeader" slot="activator">
            <v-icon>keyboard_arrow_up</v-icon>
          </v-btn>
          Move Columns</v-toolbar-title>
        <v-spacer></v-spacer>

      </v-toolbar>
      <v-card>
        <v-layout>
          <v-flex>
            <v-tooltip bottom>
              <v-btn slot="activator" icon flat :color="color" small @click="toggleHeaderHidden()">
                <v-icon>visibility</v-icon>
              </v-btn>
              <span>Show/Hide all</span>
            </v-tooltip><br/>
            <v-tooltip bottom>
            <v-btn slot="activator" icon flat :color="color" small
            :loading="savingHeaderConfig" :disabled="!saveHeaderConfigNeeded" @click="saveHeaderConfig()">
              <v-icon>save</v-icon>
            </v-btn>
            <span>Save Headers Configuration (position, visibility)</span>
          </v-tooltip>
          </v-flex>
          <v-flex>
            <draggable :list="headerOrder" @start="draggingStarted" @end="draggingEnded" class="draggable">
              <v-chip label v-for="header in headerOrder" :key="header" :color="color" text-color="white" :class="[{'is-dragging':isDragging(header)}, 'elevation-1', 'draggable']"
                :id="header">
                <span class="draggable">{{ getHeaderByValue(header) }}</span>
                <v-btn :color="getHeaderButtonColor(header)" icon flat small @click="toggleHeaderHidden(header)">
                  <v-icon>visibility</v-icon>
                </v-btn>
              </v-chip>
            </draggable>
          </v-flex>
        </v-layout>
      </v-card>
    </div>
  </v-slide-y-transition>

  <!-- filter bar -->
  <div v-if="showDrawer">
    <v-navigation-drawer app width="500" class="elevation-5">
      <v-toolbar>
        <v-icon>filter_list</v-icon>
        <div class="title pl-2">
          Filters
        </div>
        <v-spacer></v-spacer>
        <v-btn :color="color" @click="clearFilters">
          Clear
        </v-btn>
        <v-btn :color="color" @click="filterData">
          Refresh
        </v-btn>
        <v-tooltip bottom>
          <v-btn icon @click="toggleFilters" slot="activator">
            <v-icon>close</v-icon>
          </v-btn>
          <span>Close Filter Menu</span>
        </v-tooltip>
      </v-toolbar>
    </v-navigation-drawer>
    <v-navigation-drawer app width="500" class="mt-6" height="calc(100% - 64px)">
      <v-divider></v-divider>
      <v-container grid-list-md>
        <v-layout row v-for="filter in filters" :key="filter.headerText" class="pl-3 pr-3">
          <v-flex xs12 v-if="filter.type == 'String'">
            <v-text-field class="no-height" :name="filter.headerValue" :label="filter.headerText" v-model="filter.value"></v-text-field>
          </v-flex>

          <v-flex xs12 v-if="filter.type == 'Number'">
            <v-layout row>
              <v-flex xs4 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
              <v-flex xs4>
                <v-text-field class="no-height" :name="filter.headerValue + '-min'" label="Min" v-model="filter.minValue"></v-text-field>
              </v-flex>
              <v-flex xs4>
                <v-text-field class="no-height" :name="filter.headerValue + '-max'" label="Max" v-model="filter.maxValue"></v-text-field>
              </v-flex>
            </v-layout>
          </v-flex>

          <v-flex xs12 v-if="filter.type == 'Date'">
            <v-layout row>
              <v-flex xs4 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
              <v-flex xs4>
                <v-menu lazy :v-model="false" transition="scale-transition" offset-y full-width :nudge-right="40" max-width="290px" min-width="290px"
                  :close-on-content-click="true">
                  <v-text-field class="no-height" slot="activator" label="From" v-model="filter.minValue" prepend-icon="event" readonly></v-text-field>
                  <v-date-picker v-model="filter.minValue" no-title scrollable>
                    <v-spacer></v-spacer>
                  </v-date-picker>
                </v-menu>
              </v-flex>
              <v-flex xs4>
                <v-menu lazy :v-model="false" transition="scale-transition" offset-y full-width :nudge-right="40" max-width="290px" min-width="290px"
                  :close-on-content-click="true">
                  <v-text-field class="no-height" slot="activator" label="To" v-model="filter.maxValue" prepend-icon="event" readonly></v-text-field>
                  <v-date-picker v-model="filter.maxValue" no-title scrollable>
                    <v-spacer></v-spacer>
                  </v-date-picker>
                </v-menu>
              </v-flex>
            </v-layout>
          </v-flex>

        </v-layout>
      </v-container>
    </v-navigation-drawer>
  </div>


  <!-- Data Table -->
  <v-divider></v-divider>
  <v-data-table :id="tableId" v-model="selected" v-bind:headers="headers" v-bind:items="items" v-bind:search="search" hide-actions
    v-bind:pagination.sync="pagination" :item-key="uniqueIdField" :no-data-text="noDataText" :loading="loading" :class="['elevation-1', toolbarVisible ? 'mt-0' : '']"
    :custom-sort="customSort" ref="dataTable">
    <template slot="headers" slot-scope="props">
      <tr>
        <th v-if="enableSelection" :class="[color, 'white--text']" style="width:50px">
          <v-checkbox v-if="enableSelectAll" hide-details @click.native="toggleAll" :input-value="props.all" :indeterminate="props.indeterminate"></v-checkbox>
        </th>
        <th v-if="expandedDataUrl" :class="color">
        </th>
        <th v-for="header in getSortedHeaders" :key="header.text" :class="[color, 'white--text', 'subheading', header.sortable ? 'column sortable' : '', pagination.descending ? 'desc' : 'asc', header.value === pagination.sortBy ? 'active' : '']"
          @click="changeSort(header)" :width="header.width" :style="'min-width:' + header.width">
          <v-icon v-if="header.sortable" class="table-sorting-icon">arrow_upward</v-icon>
          <v-tooltip bottom v-if="header.toolTip">
            <span slot="activator" v-html="formattedHeader(header)">
            </span>
            <span v-html="header.toolTip.text">
            </span>
          </v-tooltip>
          <span v-if="!header.toolTip" v-html="formattedHeader(header)">
          </span>
        </th>
      </tr>

      <tr v-show="showDraggableHeader">
        <th v-if="enableSelection" :class="[color, 'white--text']" style="width:50px"></th>
        <th v-if="expandedDataUrl" :class="color"></th>
        <th v-for="header in getSortedHeaders" :key="header.text" :class="[color, 'white--text', 'subheading']" :width="header.width"
          :style="'min-width:' + header.width">
          <!-- <v-btn flat icon small @click="decreaseHeaderWidth(header)">
            <v-icon>mdi-unfold-less-vertical</v-icon>
          </v-btn>
          <v-btn flat icon small @click="increaseHeaderWidth(header)">
            <v-icon>mdi-unfold-more-vertical</v-icon>
          </v-btn> -->
          <v-slider color="red" step="10" style="max-width:100px" max="500" v-model="header.widthValue" @input="updateHeaderWidth($event, header)"></v-slider>
        </th>
      </tr>

    </template>

    <template slot="items" slot-scope="props">
      <tr :active="props.selected">
        <td v-if="enableSelection" style="width:50px" :class="[isHighlighted(props.item[uniqueIdField]) ? 'row-highlight' : '']">
          <v-checkbox :color="color" hide-details :input-value="props.selected" @click="handleSelectionChange(props)" v-model="props.item.isSelected"
            :ripple="false" :disabled="props.item.readonly"></v-checkbox>
        </td>
        <td v-if="expandedDataUrl" class="pl-0 pr-0" :class="[isHighlighted(props.item[uniqueIdField]) ? 'row-highlight' : '']" @click="expandRow(props.item[uniqueIdField], props)">
          <v-btn flat icon small>
            <v-icon :class="[props.expanded ? 'rotate180' : 'rotate90']">expand_less</v-icon>
          </v-btn>
        </td>
        <td v-for="header in getSortedHeaders" :class="[alignHeader(header), isHighlighted(props.item[uniqueIdField]) ? 'row-highlight' : '']">


          <v-tooltip bottom v-if="props.item.tooltips && props.item.tooltips[header.value]" max-width="500px">
            <span slot="activator" v-html="formattedItem(header, props.item[header.value])">
            </span>
            <span v-html="props.item.tooltips[header.value]">
            </span>
          </v-tooltip>
          <span v-if="!(props.item.tooltips && props.item.tooltips[header.value])" v-html="formattedItem(header, props.item[header.value])"></span>

          <span v-if="props.item[header.value] && props.item[header.value].iconFlags">
            <v-tooltip bottom v-for="(icon, index) in props.item[header.value].iconFlags" :key="index" v-if="header.isFlag">
                <v-chip v-if="icon.chip" slot="activator" :color="icon.color"
                text-color="white" label small disabled>
                {{ icon.iconName }}
                </v-chip>
                <v-icon v-if="!icon.chip" slot="activator" :color="icon.color">
                {{ icon.iconName }}
              </v-icon>
              <span> {{ icon.tooltip }}</span>
            </v-tooltip>
          </span>

          <v-icon color="green" v-if="showPassFlag(header, props.item)">check_circle</v-icon>
          <v-icon color="red" v-if="showFailFlag(header, props.item)">cancel</v-icon>
          <!-- <v-icon color="green" v-if="header.isActionable === true && props.item[header.value] && props.item[header.value].pass">check_circle</v-icon> -->

          <!-- action button in after  cell content  -->
          <v-tooltip bottom v-if="header.itemAction && !props.item.readonly">
            <v-btn :ripple="false" slot="activator" flat small icon @click="header['itemAction'](props.item)" class="mt-0 mb-0 ml-0 mr-0">
              <v-icon v-if="!header.actionIcon">keyboard_arrow_right</v-icon>
              <v-icon v-if="header.actionIcon"> {{ header.actionIcon }}</v-icon>
            </v-btn>
            <span>{{ header.actionTooltip }}</span>
          </v-tooltip>

          <v-tooltip bottom v-if="header.buttons" v-for="(button, index) in props.item.buttons" :key="index">
            <v-btn class="table-btn" icon flat @click="handleButtonTriggered(button.action, props.item)" slot="activator" :color="button.color">
              <v-icon>{{ button.icon }}</v-icon>
            </v-btn>
            <span v-html="button.tooltip"></span>
          </v-tooltip>

          <v-tooltip bottom v-if="header.isLink">
            <a :href="props.item[header.value]" slot="activator" target="_blank" rel="noreferrer">{{ props.item[header.value] }}</a>
            <span>Open Link in New Tab</span>
          </v-tooltip>

        </td>
      </tr>
    </template>
    <!-- expanded row dynamically loaded when user clicks on row -->
    <template v-if="expandedDataUrl" slot="expand" slot-scope="props">
      <v-data-table id="expandedTableId" ref="expandedTableId" v-model="selected" v-bind:headers="expandedHeaders" v-bind:items="expandedItems"
        hide-actions :item-key="uniqueIdField" no-data-text="No Data Found" :loading="loading" :class="['pl-5', 'pb-1', 'elevation-1', toolbarVisible ? 'mt-1' : '']"
        :custom-sort="customSort">
        <template slot="headers" slot-scope="props">
          <tr>
            <th v-for="header in expandedHeaders" :key="header.text" :class="['cyan white--text', 'subheading', 'column sortable']" :width="header.width">
              <v-tooltip bottom v-if="header.toolTip">
                <span slot="activator" v-html="formattedHeader(header)">
                </span>
                <span>
                  {{ header.toolTip.text }}
                </span>
              </v-tooltip>
              <span v-if="!header.toolTip" v-html="formattedHeader(header)">
              </span>
            </th>
          </tr>
        </template>

        <template slot="items" slot-scope="props">
          <tr @click="expandRow(props.item[uniqueIdField], props)">
            <td v-for="header in expandedHeaders" :class="alignHeader(header)">
              <span v-html="formattedItem(header, props.item[header.value])"></span>

              <v-icon color="green" v-if="showPassFlag(header, props.item)">check_circle</v-icon>
              <v-icon color="red" v-if="showFailFlag(header, props.item)">cancel</v-icon>
            </td>
          </tr>
        </template>

      </v-data-table>

    </template>

  </v-data-table>
  <!-- external pagination -->
  <div class="text-xs-center pt-3" v-show="showPagination">
    <v-pagination v-model="pagination.page" :length="pages" :total-visible="10"></v-pagination>
  </div>


</div>`,
    data() {
        return {
            selected: [],
            toUnselect: [],
            search: '',
            headers: [],
            headerOrder: [],
            uniqueIdField: "",
            isLoadingColor: "warning",
            loading: this.isLoadingColor,
            items: [],
            itemDragging: '',
            showSearchBar: false,
            showDraggableHeader: false,
            mustSort: true,
            pagination: {},
            stickyHeader: null,
            previousPageNb: null,
            tableId: this.createTableId,
            showDrawer: false,
            expandedUniqueId: "",
            expandedItems: [],
            expandedHeaders: [],
            expandedHeaderOrder: [],
            filters: [],
            highlight: null, //use this to change the style of a row should have the value of item.[uniqueIdField]
            doExport: false, //if true, the table will be exported as a CSV
            csvContent: "",
            headerOptionsVisible: false, //work in progress
            showButtons: true,
            saveHeaderConfigNeeded: false,
            savingHeaderConfig: false
        }
    },
    methods: {
        toggleShowButtons(doShow) {
            this.showButtons = doShow;
            this.$emit("showing-buttons", doShow);
        },
        toggleAll() {
            if (this.selected.length) this.selected = []
            else this.selected = this.items.slice()
        },
        changeSort(header) {
            if (!header.sortable) {
                return;
            }
            var column = header.value;
            if (this.pagination.sortBy === column) {
                this.pagination.descending = !this.pagination.descending
            } else {
                this.pagination.sortBy = column
                this.pagination.descending = false
            }
        },
        calcExpandedWidth() {
            var width = 0;
            for (var i = 0; i < this.expandedHeaders.length; i++) {
                //add all the header widths plus some room for padding (?)
                //not sure about that one
                width += parseInt(this.expandedHeaders[i].width.replace("px", "")) + 24;
            }
            if (this.$refs.expandedTableId) {
                this.$refs.expandedTableId.$el.style.width = width + "px";
            }
        },
        // Send an Ajax request to dataUrl and updates
        // headers and items so that the table updates itself
        getAjaxData() {
            if (!this.dataUrl) {
                this.$emit("need-manual-data");
                return;
            }
            this.startLoading();
            axios({
                method: 'post',
                url: this.dataUrl,
                params: {
                    action1Param: this.action1Param,
                    doExport: this.doExport
                },
                data: {
                    filters: this.filters
                }
            }).then(response => {
                if (response.data.isAllowed) {
                    if (this.headers.length != response.data.headers.length) {
                        //just a refresh. Keep the headerOrder in place
                        //in case the user modified the column order
                        this.headerOrder = response.data.headerOrder;
                    }
                    this.headers = response.data.headers;
                    // for (var i = 0; i < this.headers.length; i++) {
                    //     this.headers[i].width = "400px";
                    // }
                    this.items = response.data.items;
                    this.pagination.totalItems = this.items.length;
                    this.uniqueIdField = response.data.uniqueIdField;
                    this.createFilters(response.data.dataFiltersMap)
                    if (this.doExport) {
                        this.csvContent = response.data.csvContent;
                        this.createCSVFile();
                    }
                }
                else {
                    this.handleDialogs(response, this.getAjaxData);
                }
                this.stopLoading();
                this.doExport = false;
            })
                .catch(error => {
                    this.stopLoading();
                    alert(error);
                });
        },
        handleRefresh() {
            this.$emit("refresh-requested");
            this.getAjaxData();
        },
        createFilters(previousFilters) {
            this.filters = [];
            for (var i = 0; i < this.headers.length; i++) {
                var header = this.headers[i];
                var headerText = null;
                if (header.text) {
                    headerText = header.text;
                }
                else {
                    var multipartHeader = header.textPart1;
                    if (header.textPart2) {
                        headerText = multipartHeader + " " + header.textPart2;
                    }
                }
                var filter = previousFilters ? previousFilters[header.value] : null;
                if (header.unit) { //number: need min and max
                    var type = null;
                    var minValue = null;
                    var maxValue = null;
                    if (header.unit.value == "Date") {
                        type = "Date";
                        if (filter) {
                            minValue = filter.minDateValue;
                            maxValue = filter.maxDateValue;
                        }
                    }
                    else {
                        type = "Number";
                        if (filter) {
                            minValue = filter.minValue ? filter.minValue + "" : ""; //change into a String to avoid parsing issues with minNode.textValue() on the backend
                            maxValue = filter.maxValue ? filter.maxValue + "" : "";
                        }
                    }
                    this.filters.push({
                        headerText: headerText,
                        headerValue: header.value,
                        isDna: header.isDna,
                        isRna: header.isRna,
                        type: type,
                        minValue: minValue,
                        maxValue: maxValue,
                        unit: header.unit
                    });
                }
                else { //just a string
                    this.filters.push({
                        headerText: headerText,
                        headerValue: header.value,
                        isDna: header.isDna,
                        isRna: header.isRna,
                        value: filter ? filter.value : null,
                        type: "String",
                    });
                }
            }
            return this.filters;
        },
        filterData() {
            // for (var i = 0; i < this.filters.length; i++) {
            //     var filter = this.filters[i];
            // }
            this.getAjaxData();
        },
        clearFilters() {
            for (var i = 0; i < this.filters.length; i++) {
                var filter = this.filters[i];
                filter.value = null;
                filter.minValue = null;
                filter.maxValue = null;
            }
            this.getAjaxData();
        },
        stopLoading() {
            this.loading = false;
        },
        //In most cases, the response contains data for only one table
        //use this method to manually update the data
        manualData(response) {
            if (this.headers.length != response.data.headers.length) {
                //just a refresh. Keep the headerOrder in place
                //in case the user modified the column order
                this.headerOrder = response.data.headerOrder;
            }
            this.headers = response.data.headers;
            this.items = response.data.items;
            this.pagination.totalItems = this.items.length;
            this.uniqueIdField = response.data.uniqueIdField;
            this.stopLoading();
        },
        //If the response contains data for multiple tables
        //use this method to only pass the data for the current table
        manualDataFiltered(data) {
            if (this.headers.length != data.headers.length) {
                //just a refresh. Keep the headerOrder in place
                //in case the user modified the column order
                this.headerOrder = data.headerOrder;
            }
            this.headers = data.headers;
            this.items = data.items;
            this.pagination.totalItems = this.items.length;
            this.uniqueIdField = data.uniqueIdField;
            this.stopLoading();
        },
        manualDataError() {
            this.stopLoading();
        },
        startLoading() {
            //since the ajax call could be done externally, call this method
            //to set the proper messages and loading states
            this.loading = this.isLoadingColor;
        },
        draggingStarted(evt) {
            var item = evt.item;
            this.itemDragging = item.id;
        },
        draggingEnded(evt) {
            this.itemDragging = '';
            this.saveHeaderConfigNeeded = true;
        },
        // Retrieve the human readable form by Java field name
        // Used by draggable
        getHeaderByValue(header) {
            var text = "";
            this.headers.forEach(item => {
                if (item.value === header) {
                    text = item.text ? item.text : item.textPart1 + " " + item.textPart2;
                }
            })
            return text;
        },
        getHeaderObjectByValue(header) {
            for (var i = 0; i < this.headers.length; i++) {
                if (this.headers[i].value === header) {
                    return this.headers[i];
                }
            }
            return null;
        },
        getHeaderButtonColor(header, isObjectHeader) {
            var headerObject = null;
            if (!isObjectHeader) {
                headerObject = this.getHeaderObjectByValue(header);
            }
            else {
                headerObject = header;
            }
            if (headerObject) {
                return headerObject.isHidden ? 'white' : 'amber accent-2';
            }
            return '';
        },
        toggleHeaderHidden(header, isObjectHeader) {
            if (!header) { //toggle all
                //check first one
                var alreadyHidden = this.headers[0].isHidden;
                for (var i = 0; i < this.headers.length; i++) {
                    this.headers[i].isHidden = !alreadyHidden;
                }
            }
            var headerObject = null;
            if (!isObjectHeader) {
                headerObject = this.getHeaderObjectByValue(header);
            }
            else {
                headerObject = header;
            }
            if (headerObject) {
                headerObject.isHidden = !headerObject.isHidden;
            }
        },
        isDragging(header) {
            return header === this.itemDragging;
        },
        toggleSearchBar() {
            this.showSearchBar = !this.showSearchBar;
            if (this.showSearchBar) {
                this.$nextTick(this.$refs.search.focus);
            }
        },
        toggleFilters() {
            this.showDrawer = !this.showDrawer;
            if (!this.showDrawer) {
                bus.$emit("need-layout-resize", this);
            }
        },
        handleScroll(event) {
            if (this.disableStickyHeader) {
                return;
            }
            if (!this.stickyHeader) {
                this.stickyHeader = $('table').stickyTableHeaders({ fixedOffset: 48 }); //need to use the html element rather than the element id
            }
            this.$nextTick(function () {
                $(window).trigger('resize.stickyTableHeaders');
            });

        },
        handleDialogs(response, callback) {
            if (response.data.isXss) {
                bus.$emit("xss-error", [this, response.data.reason]);
            }
            else {
                bus.$emit("login-needed", [this, callback])
            }
        },
        //function taken from vuetify.js directly to be used in customSort
        clean(t, e) {
            if (e && e.constructor === String) {
                e = e.replace(/\[(\w+)\]/g, ".$1"),
                    e = e.replace(/^\./, "");
                for (var i = e.split("."), n = 0, s = i.length; n < s; ++n) {
                    var r = i[n];
                    if (!(t instanceof Object && r in t))
                        return;
                    t = t[r]
                }
                return t
            }
        },
        //customSort (most code is from vuetify.js)
        //can sort string, integers (comma formatted if needed) and dates
        customSort(t, e, i) {
            return null === e ? t : t.sort(((t, n) => {
                var s = this.clean(t, e)
                    , r = this.clean(n, e);
                if (i) {
                    var o = [r, s];
                    s = o[0],
                        r = o[1]
                }
                if (null == s) {
                    if (null == r) {
                        return 0;
                    }
                    return 1;
                }
                if (null != s && null == r) {
                    return -1;
                }
                if (null == s && null == r)
                    return 0;
                if (s.field) { //passable field, transform to extract the actual value
                    s = s.value;
                }
                if (r.field) { //passable field, transform to extract the actual value
                    r = r.value;
                }
                if (s.iconFlags && s.iconFlags.length > 0) {
                        s = s.iconFlags[0].tooltip;
                }
                if (r.iconFlags && r.iconFlags.length > 0) {
                    r = r.iconFlags[0].tooltip;
                }
                //test again if s or r are now null
                if (null == s) {
                    if (null == r) {
                        return 0;
                    }
                    return 1;
                }
                if (null != s && null == r) {
                    return -1;
                }
                if (null == s && null == r)
                    return 0;
                if (!isNaN(s) && !isNaN(r))
                    return s - r;
                if (s.indexOf(",") > -1 || r.indexOf(",") > -1) { //one of them could be a formatted number
                    sNb = s.replace(/,/g, '');
                    rNb = r.replace(/,/g, '');
                    if (!isNaN(sNb) && !isNaN(rNb))
                        return parseInt(sNb) - parseInt(rNb);
                }
                if (s.indexOf("/") > -1 && r.indexOf("/") > -1) { //tallys like count/total
                    return parseInt(s.split("/")[0]) - parseInt(r.split("/")[0]);
                }
                if (dateRegex.test(s) && dateRegex.test(r)) {
                    var sDateArray = s.split("/");
                    var rDateArray = r.split("/");
                    //date compare
                    var sDate = new Date();
                    sDate.setFullYear(sDateArray[2], sDateArray[0] - 1, sDateArray[1]);
                    var rDate = new Date();
                    rDate.setFullYear(rDateArray[2], rDateArray[0] - 1, rDateArray[1]);
                    if (sDate > rDate) {
                        return 1;
                    }
                    if (sDate < rDate) {
                        return -1;
                    }
                    return 0;
                }
                return s.localeCompare(r);
            }
            ))
        },
        alignHeader(header) {
            //go with the align property first
            if (header.align != null) {
                if (header.align == "left") {
                    return "text-xs-left";
                }
                if (header.align == "center") {
                    return "text-xs-center";
                }
                if (header.align == "right") {
                    return "text-xs-right";
                }
            }
            if (header.unit != null) {
                if (header.unit.value == 'Date') {
                    return "text-xs-center";
                }
                return "text-xs-right"; //it's a number, could be formatted
            }
            return "text-xs-center"; //it's just text
        },
        formattedHeader(header) {
            if (header.text) {
                return header.text;
            }
            var multipartHeader = header.textPart1;
            if (header.textPart2) {
                return multipartHeader + "<br/>" + header.textPart2;
            }
        },
        formattedItem(header, item) {
            if (item == null) {
                return null;
            }
            var itemString = null;
            if (header.isPassable === true || header.isActionable === true) {
                itemString = item.value;
            }
            else if (header.isFlag || header.isLink) {
                return "";
            }
            else if (header.buttons) {
                itemString = "";
                for (var i = 0; i < header.buttons.length; i++) {
                    itemString += buttons
                }
            }
            else {
                itemString = item;
            }
            if (header.unit && header.unit.value != "Date") {
                itemString += " " + header.unit.value;
            }
            if (header.canHighlight) {
                var toHighlight = this.highlights ? this.highlights[header.value] : null; //array of items to highlight
                if (toHighlight) {
                    var items = itemString.split(" ");
                    for (var i = 0; i < items.length; i++) {
                        if (toHighlight.includes(items[i])) {
                            items[i] = "<b>" + items[i] + "</b>";
                        }
                    }
                    itemString = items.join(" ");
                }
            }
            return itemString;
        },
        isHighlighted(itemUniqueField) {
            return this.highlight === itemUniqueField;
        },
        createTableId() {
            // Need to create a unique unique for each table
            //This, though not perfect, quickly generates a random id
            //tableId is used by the sticky header
            var id = "table";
            var counter = Math.round(Math.random() * 10000);
            return id + counter;
        },
        expandRow(idItem, props) {
            if (!this.expandedDataUrl) {
                return;
            }
            props.expanded = !props.expanded;
            this.expandedUniqueId = idItem;
            this.startLoading();
            axios.get(this.expandedDataUrl, {
                params: {
                    'uniqueId': this.expandedUniqueId + ""
                }
            })
                .then(response => {
                    if (response.data.isAllowed) {
                        if (this.expandedHeaders.length != response.data.headers.length) {
                            //just a refresh. Keep the headerOrder in place
                            //in case the user modified the column order
                            this.expandedHeaderOrder = response.data.headerOrder;

                        }
                        this.expandedHeaders = response.data.headers;
                        this.expandedItems = response.data.items;
                        this.calcExpandedWidth();
                    }
                    else {
                        this.handleDialogs(response, this.getAjaxData);
                    }
                    this.stopLoading();
                })
                .catch(error => {
                    this.stopLoading();
                    alert(error);
                });
        },
        handleSelectionChange(props) {
            //if previous rows had been selected, the props.selected is out of sync
            //with props.item.isSelected.
            //Make sure to record which rows are unselected
            // if ((props.item.isSelected === true && props.selected === undefined)
            //     || props.selected === true) {
            //     //check if already exist
            //     var found = false;
            //     for (var i = 0; i < this.toUnselect.length; i++) {
            //         if (this.toUnselect[i].uniqueIdField == props.item.uniqueIdField) {
            //             found = true
            //             break;
            //         }
            //     }
            //     if (!found) {
            //         this.toUnselect.push(props.item);
            //     }
            // }
            // else if (props.item.isSelected === false) {//about to be selected. Remove from unselected list
            //     //remove from unselected
            //     var indexToRemove = -1;
            //     for (var i = 0; i < this.toUnselect.length; i++) {
            //         if (this.toUnselect[i].uniqueIdField == props.item.uniqueIdField) {
            //             indexToRemove = i;
            //             break;
            //         }
            //     }
            //     if (indexToRemove > -1) {
            //         this.toUnselect.splice(indexToRemove, 1);
            //     }
            // }
            props.item.isSelected = !props.item.isSelected;
            props.selected = props.item.isSelected;
        },
        addToSelection(item) {
            // item.isSelected = true;
            // var rowItem = null;
            // for (var i = 0; i < this.items.length; i++) {
            //     if (this.items[i].oid == item.oid) {
            //         rowItem = this.items[i];
            //         break;
            //     }
            // }
            this.handleSelectionChange({ item: item });
        },
        removeFromSelection(item) {
            // item.isSelected = false;
            this.handleSelectionChange({ item: item });
        },
        showPassFlag(header, item) {
            return header.isPassable === true
                && item[header.value]
                && item[header.value].pass === true;
        },
        showFailFlag(header, item) {
            return header.isPassable === true
                && item[header.value]
                && item[header.value].pass === false;
        },
        handleButtonTriggered(action, item) {
            bus.$emit(action, item);
        },
        exportToCSV() {
            this.doExport = true;
            this.getAjaxData();
        },
        createCSVFile() {
            var hiddenElement = document.createElement('a');
            hiddenElement.href = 'data:text/csv;charset=utf-8,' + encodeURI(this.csvContent);
            //hiddenElement.target = '_blank';
            hiddenElement.download = this.tableTitle.replace(" ", "_") + '_data.csv';
            document.body.appendChild(hiddenElement);
            hiddenElement.click();
            document.body.removeChild(hiddenElement);

        },
        getRowCount() {
            if (this.$refs.dataTable) {
                var rowCount = this.$refs.dataTable.itemsLength;
                return "(" + rowCount + " row" + (rowCount > 1 ? "s" : "") + ")";
            }
            return null;
        },
        selectionChanged() {
            this.$emit('datatable-selection-changed', this.selected.length);
        },
        getFilteredItems() {
            return this.$refs.dataTable.filteredItems;
        },
        getCurrentItemIdex(currentUniqueId) {
            var length = this.getFilteredItems().length;
            for (var i = 0; i < length; i++) {
                var item = this.getFilteredItems()[i];
                if (item[this.uniqueIdField] == currentUniqueId) {
                    return i;
                }
            }
            return -1;
        },
        isFirstItem(currentIndex) {
            return currentIndex == 0 && this.pagination.page == 1;
        },
        isLastItem(currentIndex) {
            var lastPage = this.pagination.page * this.pagination.rowsPerPage >= this.pagination.totalItems;
            return currentIndex == this.getFilteredItems().length - 1
                && lastPage;
        },
        getPreviousItem(currentRow) {
            if (currentRow[this.uniqueIdField] != null) {
                var currentIndex = this.getCurrentItemIdex(currentRow[this.uniqueIdField]);
                if (currentIndex > -1) { //found the current item
                    if (currentIndex == 0 && this.pagination.page > 1) { //could need to load the previous table page
                        this.pagination.page = this.pagination.page - 1;
                        return this.getFilteredItems()[this.getFilteredItems().length - 1]; //select the last item from the previous page
                    }
                    return this.getFilteredItems()[Math.max(0, currentIndex - 1)];
                }
            }
            return null;
        },
        getNextItem(currentRow) {
            if (currentRow[this.uniqueIdField] != null) {
                var currentIndex = this.getCurrentItemIdex(currentRow[this.uniqueIdField]);
                if (currentIndex > -1) { //found the current item
                    var lastPage = this.pagination.page * this.pagination.rowsPerPage >= this.pagination.totalItems;
                    if (currentIndex == this.getFilteredItems().length - 1 && !lastPage) { //could need to load the next table page
                        this.pagination.page++;
                        return this.getFilteredItems()[0]; //select the first item from the next page
                    }
                    return this.getFilteredItems()[Math.min(this.getFilteredItems().length - 1, currentIndex + 1)];
                }
            }
            return null;
        },
        decreaseHeaderWidth(header) {
            var value = 0;
            if (header.width) {
                value = parseInt(header.width.replace("px", ""));
            }
            value -= value >= 0 ? 10 : 0;
            header.width = value + "px";
        },
        increaseHeaderWidth(header) {
            var value = 0;
            if (header.width) {
                value = parseInt(header.width.replace("px", ""));
            }
            value += 10;
            header.width = value + "px";
        },
        updateHeaderWidth(event, header) {
            if (header.widthValue) {
                header.width = header.widthValue + "px";
            }
        },
        saveHeaderConfig() {
            this.savingHeaderConfig = true;
            var simpleHeaders = [];
            for (var i = 0; i < this.headerOrder.length; i++) {
                var fullHeader = this.getHeaderObjectByValue(this.headerOrder[i]);
                var text = "";
                if (fullHeader.text) {
                    text = fullHeader.text;
                }
                else if (fullHeader.textPart2 != null) {
                    text = fullHeader.textPart1 + " " + fullHeader.textPart2;
                }
                else {
                    text = fullHeader.textPart1;
                }
                simpleHeaders.push({
                    value: fullHeader.value,
                    isHidden: fullHeader.isHidden,
                    text: text
                })
            }
            axios({
                method: 'post',
                url: webAppRoot + "/saveHeaderConfig",
                params: {
                },
                data: {
                    headers: simpleHeaders,
                    tableTitle: this.tableTitle
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.saveHeaderConfigNeeded = false;
                    } else {
                        this.handleDialogs(response.data, this.saveHeaderConfig.bind(null, headers, tableTitle));
                    }
                    this.savingHeaderConfig = false;
                })
                .catch(error => {
                    this.handleAxiosError(error);
                    this.savingHeaderConfig = false;
                });
        }
    },
    computed: {
        getSortedHeaders() {
            var sortedHeaders = [];
            this.headerOrder.forEach(sortedHeader => {
                this.headers.forEach(header => {
                    if (sortedHeader === header.value && !header.isHidden) {
                        sortedHeaders.push(header);
                    }
                })
            });
            return sortedHeaders;
        },
        pages() {
            //return 0;
            if (this.pagination.rowsPerPage == 0) {
                return 0;
            }
            var pageNb = this.pagination.rowsPerPage && this.pagination.totalItems ?
                Math.ceil(this.pagination.totalItems / this.pagination.rowsPerPage) : 0;
            if (!this.previousPageNb) {
                this.previousPageNb = pageNb;
            }
            else if (this.previousPageNb > pageNb) {
                this.pagination.page = 1;
            }
            this.previousPageNb = pageNb;
            return pageNb;
        }




    },
    created: function () {
        this.tableId = this.createTableId();
        this.pagination = {
            sortBy: this.initialSort,
            descending: this.sortDescending,
            rowsPerPage: 10
        }


    },
    destroyed: function () {
        window.removeEventListener('scroll', this.handleScroll);
        $('#' + this.tableId).stickyTableHeaders('destroy');
        //make sure the layout is refreshed for other views
        bus.$emit("need-layout-resize", this);
    },
    mounted: function () {
        window.addEventListener('scroll', this.handleScroll);
        this.$el.querySelector('#' + this.tableId).firstElementChild.addEventListener('scroll', this.handleScroll);

        if (this.fetchOnCreated) {
            this.getAjaxData();
        } else {
            this.loading = false;
        }
    },
    watch: {
        "selected": 'selectionChanged'
    }



});

var dateRegex = /[0-9]{2}\/[0-9]{2}\/[0-9]{4}/;
