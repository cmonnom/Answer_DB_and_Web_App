

Vue.component('data-table', {
    props: {
        "table-title": { default: "Table", type: String },
        "data-url": "",
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
        // "disable-sticky-header": {default: false, type: Boolean},
        highlights: { default: () => {}, type: Object },
        "fixed-header": {default: false, type: Boolean}, //not working yet
        "add-row-button": {default: false, type: Boolean},
        "add-row-description": {default: "", type: String},
        "additional-headers": {default: () => [], type: Array}, //for situations where add a new row needs more fields than in headers
        "icon-color": {default: "", type: String},
        "icon-active-color": {default: "", type: String},
        "external-filtering-active": {default: false, type: Boolean}, //parent can control that the table is filtered externally (eg. advanced-filter)
        "id-type": {default: "", type: String}, //can be used to know which table an event came from. Should be unique to some extend
    },
    template: /*html*/`<div class="elevation-1">
    <!-- Comment above and uncomment below to use the buttons on hover feature -->
     <!-- <div @mouseover="toggleShowButtons(true)" @mouseleave="toggleShowButtons(false)"> -->
  <!-- Top tool bar with menu options -->
  <v-toolbar dense dark :color="loading ? headerLoadingColor : color" :class="fixed ? '' : 'elevation-0'" :fixed="fixed" :app="fixed" v-show="toolbarVisible">
    <!-- icon with no function -->
    <v-icon v-if="titleIcon && !showLeftMenu" :color="iconColor ? iconColor : 'amber accent-2'">{{ titleIcon }}</v-icon>
    <!-- menu with same functions as left side icons -->
    <v-tooltip class="ml-0" bottom v-if="showLeftMenu">
      <v-menu offset-y offset-x slot="activator" class="ml-0">
        <v-btn aria-label="Table Menu" slot="activator" flat icon dark>
          <v-icon v-if="!titleIcon">more_vert</v-icon>
          <v-icon v-if="titleIcon" :color="iconColor ? iconColor : 'amber accent-2'">{{ titleIcon }}</v-icon>
        </v-btn>
        <v-list>

          <slot name="action1MenuItem"></slot>
          <slot name="action2MenuItem"></slot>
          <slot name="action3MenuItem"></slot>

          <v-list-tile avatar @click="toggleAddRowBar" v-if="addRowButton">
            <v-list-tile-avatar>
              <v-icon>add</v-icon>
            </v-list-tile-avatar>
            <v-list-tile-content>
              <v-list-tile-title>Add New Row</v-list-tile-title>
            </v-list-tile-content>
          </v-list-tile>  

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
      <span v-if="showRowCount" v-text="getRowCount()"></span>
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <v-fade-transition>
      <v-layout justify-end align-center v-show="showButtons">
        <v-flex class="text-xs-right">
          <v-container>
            <slot name="title"></slot>
          </v-container>
        </v-flex>
        <v-flex xs7 class="text-xs-right" v-show="showPagination">
          <div class="title white--text pr-2">Rows:</div>
        </v-flex>
        <v-flex xs3 v-show="showPagination" class="data-table-row-input small-solo-input">
            <v-text-field solo flat single-line hide-details light v-model="pagination.rowsPerPage"></v-text-field>
        </v-flex>
      </v-layout>
    </v-fade-transition>

      <slot name="action1"></slot>
      <slot name="action2"></slot>
      <slot name="action3"></slot>

    <v-fade-transition>
      <v-tooltip bottom v-show="showButtons" v-if="addRowButton">
        <v-btn aria-label="Add New Row" flat icon @click="toggleAddRowBar" slot="activator"  :color="getButtonColor(showAddRowBar)">
          <v-icon>add</v-icon>
        </v-btn>
        <span>Add New Row</span>
      </v-tooltip>
    </v-fade-transition>

    <v-fade-transition>
      <v-tooltip bottom v-show="showButtons">
        <v-btn aria-label="Quick Filter" flat icon @click="toggleSearchBar" slot="activator" :color="getButtonColor(showSearchBar)">
          <v-icon>search</v-icon>
        </v-btn>
        <span>Quick Filter</span>
      </v-tooltip>
    </v-fade-transition>

    <v-fade-transition>
      <v-tooltip bottom v-if="advanceFiltering" v-show="showButtons">
        <v-btn aria-label="Filter Menu" flat icon @click="toggleFilters" slot="activator" :color="getButtonColor(showDrawer)">
          <v-icon>filter_list</v-icon>
        </v-btn>
        <span>Filter Menu</span>
      </v-tooltip>
    </v-fade-transition>

    <v-fade-transition>
      <v-tooltip bottom v-if="exportEnabled" v-show="showButtons">
        <v-btn aria-label="Export to CSV" flat icon @click="exportToCSV" slot="activator">
          <v-icon>file_download</v-icon>
        </v-btn>
        <span>Export to CSV
          <br/>(works with Filter Menu)</span>
      </v-tooltip>
    </v-fade-transition>

    <v-fade-transition>
      <v-tooltip bottom v-show="showButtons">
        <v-btn aria-label="Move Columns" flat icon @click="showDraggableHeader=!showDraggableHeader" slot="activator" :color="getButtonColor(showDraggableHeader)">
          <v-icon>swap_horiz</v-icon>
        </v-btn>
        <span>Move Columns</span>
      </v-tooltip>
    </v-fade-transition>

    <v-fade-transition>
      <v-tooltip bottom v-show="showButtons">
        <v-btn aria-label="Refresh" flat icon @click="handleRefresh()" slot="activator" :loading="loading">
          <v-icon>refresh</v-icon>
        </v-btn>
        <span>Refresh</span>
      </v-tooltip>
    </v-fade-transition>
  </v-toolbar>

  <!--Add Row Bar -->
  <v-slide-y-transition>
  <v-card v-show="showAddRowBar" class="mt-1 mb-1">
  <v-card-text>
      <v-layout>
        <v-flex xs12>
          <v-btn aria-label="Add a New Row" icon @click="toggleAddRowBar" slot="activator">
          <v-icon>keyboard_arrow_up</v-icon>
          </v-btn>
          <span class="subheading"> Add a new Row {{ addRowDescription }}</span>
        </v-flex>
      </v-layout>
      <v-layout row wrap align-end>
      <v-flex xs v-for="header in getSortedHeaders" :key="header.oneLineText" class="pl-2 pr-2" v-if="!header.isFlag">
         <v-textarea hide-details :label="header.oneLineText" v-model="newRow[header.value]" :style="'width:' + header.width" :color="color"></v-textarea>
      </v-flex>
      <v-flex xs v-for="header in additionalHeaders" :key="header.oneLineText" class="pl-2 pr-2" >
        <v-textarea hide-details :label="header.oneLineText" :hint="header.hint" v-model="newRow[header.value]" :style="'width:' + header.width" :color="color"></v-textarea>
      </v-flex>
      <v-flex>
      <v-btn aria-label="Add Row" :color="color" @click="addNewRow">Add Row</v-btn>
      </v-flex>
      </v-layout>
      </v-card-text>
    </v-card>  
  </v-slide-y-transition>

  <!-- Search Bar -->
  <v-slide-y-transition>

    <v-card v-show="showSearchBar" class="mt-1 mb-1">
      <v-card-text>
      <v-layout row wrap justify-space-between>
      <v-flex xs3>
        <v-btn aria-label="Toggle Quick Filter" icon @click="toggleSearchBar" slot="activator">
        <v-icon>keyboard_arrow_up</v-icon>
        </v-btn>
        <span class="subheading">Quick Filter</span>
      </v-flex>
      <v-flex xs6>
        <v-text-field clearable ref="search" append-icon="search" label="Search" single-line hide-details v-model="pendingSearch" class="pt-0 pb-0 pr-3" :color="color"></v-text-field>
      </v-flex>
      </v-layout>
      </v-card-text>
    </v-card>
  </v-slide-y-transition>

  <!-- Draggable Header -->
  <v-slide-y-transition>
      <v-card v-show="showDraggableHeader" class="mt-1 mb-1 pt-1 pb-1">
        <v-layout>
        <v-flex xs12>
        <v-btn aria-label="Toggle Move Columns" icon @click="showDraggableHeader=!showDraggableHeader" slot="activator">
            <v-icon>keyboard_arrow_up</v-icon>
          </v-btn>
        <span class="subheading">Move Columns</span>
      </v-flex>
        </v-layout>
        <v-layout>
          <v-flex>
            <v-tooltip bottom>
              <v-btn aria-label="Show/Hide all" slot="activator" icon flat :color="color" small @click="toggleHeaderHidden()">
                <v-icon>visibility</v-icon>
              </v-btn>
              <span>Show/Hide all</span>
            </v-tooltip><br/>
            <v-tooltip bottom>
            <v-btn aria-label="Save Header Configuration" slot="activator" icon flat :color="color" small
            :loading="savingHeaderConfig" :disabled="!saveHeaderConfigNeeded" @click="saveHeaderConfig()">
              <v-icon>save</v-icon>
            </v-btn>
            <span>Save Header Configuration (position, visibility)</span>
          </v-tooltip>
          </v-flex>
          <v-flex>
            <draggable :list="headerOrder" @start="draggingStarted" @end="draggingEnded" class="draggable">
              <v-chip label v-for="header in headerOrder" :key="header" :color="color" text-color="white" :class="[{'is-dragging':isDragging(header)}, 'elevation-1', 'draggable']"
                :id="header + tableTitle">
                <span class="draggable">{{ getHeaderByValue(header) }}</span>
                <v-btn aria-label="Show/Hide Header" :color="getHeaderButtonColor(header)" icon flat small @click="toggleHeaderHidden(header)">
                  <v-icon>visibility</v-icon>
                </v-btn>
              </v-chip>
            </draggable>
          </v-flex>
        </v-layout>
      </v-card>
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
        <v-btn aria-label="Clear Filters" :color="color" @click="clearFilters">
          Clear
        </v-btn>
        <v-btn aria-label="Refresh" :color="color" @click="filterData">
          Refresh
        </v-btn>
        <v-tooltip bottom>
          <v-btn aria-label="Close Filter Menu" icon @click="toggleFilters" slot="activator">
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
    <!-- <v-divider></v-divider>-->
  <v-data-table :id="tableId" v-model="selected" v-bind:headers="headers" v-bind:items="items" v-bind:search="search" hide-actions
    v-bind:pagination.sync="pagination" :item-key="uniqueIdField" :no-data-text="noDataText" :loading="loading ? isLoadingColor : false" :class="['elevation-0', toolbarVisible ? 'mt-0' : '', isHeaderFixed ? 'fixed-header' : '']"
    :custom-sort="customSort" ref="dataTable">
    <template slot="headers" slot-scope="props">
      <tr>
        <th v-if="enableSelection" :class="[loading ? headerLoadingColor : color, 'white--text']" style="width:50px">
        <v-tooltip bottom>
          <v-checkbox slot="activator" v-if="enableSelectAll" hide-details @click.native="toggleAll" :input-value="props.all" :indeterminate="areAllSelected()" dark color="white"></v-checkbox>
          <span>Select/Unselect All</span>
        </v-tooltip>
        </th>
        <th v-for="header in getSortedHeaders" :key="header.text" :class="[loading ? headerLoadingColor : color, 'white--text', 'subheading', header.sortable ? 'column sortable' : '', pagination.descending ? 'desc' : 'asc', header.value === pagination.sortBy ? 'active' : '']"
          @click="changeSort(header)" :width="header.width" :style="'min-width:' + header.width">
          <v-icon v-if="header.sortable" class="table-sorting-icon" color="white">mdi-menu-up</v-icon>
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
      <tr :active="props.selected" :class="props.item.active === false || loading ? 'blue-grey lighten-5 blue-grey--text' : ''">
        <td v-if="enableSelection" style="width:50px" :class="[isHighlighted(props.item[uniqueIdField]) ? 'row-highlight' : '']">
          <v-checkbox :color="color" hide-details :input-value="props.selected" @change="selectionChanged(props.item)" v-model="props.item.isSelected"
            :ripple="false" :disabled="props.item.readonly"></v-checkbox>
        </td>
        <td v-for="header in getSortedHeaders" :class="[alignHeader(header), isHighlighted(props.item[uniqueIdField]) ? 'row-highlight' : '']">


          <v-tooltip bottom v-if="props.item.tooltips && props.item.tooltips[header.value]" max-width="500px">
            <span v-if="header.isSafe || containsOnlyBR(props.item[header.value])" slot="activator" v-html="formattedItem(header, props.item[header.value])">
            <span v-if="!header.isSafe" slot="activator" v-text="formattedItem(header, props.item[header.value])"></span>
            </span>
            <span v-html="props.item.tooltips[header.value]">
            </span>
          </v-tooltip>
          <span v-if="!(props.item.tooltips && props.item.tooltips[header.value]) && header.isSafe" v-html="formattedItem(header, props.item[header.value])"></span>
          <span v-if="!(props.item.tooltips && props.item.tooltips[header.value]) && !header.isSafe" v-text="formattedItem(header, props.item[header.value])"></span>

          <span v-if="props.item[header.value] && props.item[header.value].iconFlags">
            <v-tooltip bottom v-for="(icon, index) in props.item[header.value].iconFlags" :key="index" v-if="header.isFlag">
                <v-chip v-if="icon.chip" slot="activator" :color="props.item.active === false ? 'blue-grey lighten-2' : icon.color"
                text-color="white" label small disabled style="vertical-align: bottom">
                {{ icon.iconName }}
                </v-chip>
                <v-icon v-if="!icon.chip" :size="icon.size ? icon.size : ''" slot="activator" :color="props.item.active === false ? 'blue-grey lighten-2' : icon.color">
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
            <v-btn :aria-label="header.actionTooltip" :ripple="false" slot="activator" flat small icon @click="header['itemAction'](props.item)" class="mt-0 mb-0 ml-0 mr-0" :loading="props.item.loading">
            <v-icon v-if="!header.actionIcon">keyboard_arrow_right</v-icon>
            <v-icon v-if="header.actionIcon"> {{ header.actionIcon }}</v-icon>
          </v-btn>
            <span>{{ header.actionTooltip }}</span>
          </v-tooltip>

          <v-tooltip bottom v-if="header.buttons" v-for="(button, index) in props.item.buttons" :key="index">
          <router-link v-if="button.link" slot="activator" :to="button.href">
            <v-btn  class="table-btn" icon flat :href="button.href"
            :color="props.item.active === false ? 'blue-grey lighten-2' : button.color">
            <v-icon>{{ button.icon }}</v-icon>
            </v-btn>
          </router-link>
                    
            <v-btn :aria-label="button.tooltip" v-else class="table-btn" icon flat @click="handleButtonTriggered(button.action, props.item, $event)" slot="activator"
             :color="props.item.active === false ? 'blue-grey lighten-2' : button.color">
              <v-icon>{{ button.icon }}</v-icon>
            </v-btn>
            <span v-html="button.tooltip"></span>
          </v-tooltip>

          <v-tooltip bottom v-if="header.isLink && header.urlForItem">
          <span slot="activator" >
          {{ props.item[header.value] }}
          <v-btn :aria-label="header.actionTooltip" :ripple="false" flat small icon :href="header.urlForItem(props.item)" target="_blank" rel="noreferrer" class="mt-0 mb-0 ml-0 mr-0">
          <v-icon v-if="!header.actionIcon">open_in_new</v-icon>
          <v-icon v-if="header.actionIcon"> {{ header.actionIcon }}</v-icon>
          </v-btn>
          </span>
            <span>{{ header.actionTooltip }}</span>
          </v-tooltip>

        </td>
      </tr>
    </template>
  </v-data-table>
  <!-- external pagination -->
  <div class="text-xs-center" v-show="showPagination && hasData()">
    <v-pagination v-model="pagination.page" :color="color" :length="pages" :total-visible="10"></v-pagination>
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
            isLoadingColor: "amber accent-2",
            loading: this.isLoadingColor ? true : false,
            items: [],
            itemDragging: '',
            showAddRowBar: false,
            showSearchBar: false,
            showDraggableHeader: false,
            mustSort: true,
            pagination: {},
            stickyHeader: null,
            previousPageNb: null,
            tableId: this.createTableId,
            showDrawer: false,
            filters: [],
            highlight: null, //use this to change the style of a row should have the value of item.[uniqueIdField]
            doExport: false, //if true, the table will be exported as a CSV
            csvContent: "",
            headerOptionsVisible: false, //work in progress
            showButtons: true,
            saveHeaderConfigNeeded: false,
            savingHeaderConfig: false,
            isHeaderFixed: false,
            newRow: {},
            headerLoadingColor: "blue-grey lighten-4",
            filteringActive: false, //preserves the previous value
            currentSeachTimeout: null,
            pendingSearch: null,
        }
    },
    methods: {
        toggleShowButtons(doShow) {
            this.showButtons = doShow;
            this.$emit("showing-buttons", doShow);
        },
        toggleAll() {
            var doSelect = false;
            var selectedLength = this.selected.length;
            if (!selectedLength || (selectedLength > 0 && selectedLength != this.items.length)) {
                this.selected = this.items.slice();
                doSelect = true;
            }
            else {
                this.selected = [];
                doSelect = false;
            }
            this.items.forEach(i => {
                if (!i.readonly) {
                    i.selected = doSelect; 
                    i.isSelected = doSelect;
                }
            });
        },
        areAllSelected() {
            var length = this.selected.length;
            return length > 0 && this.selected.length != this.items.length;
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
        getButtonColor(flag) {
          if (!flag) {
            return "white";
          }
          if (this.iconActiveColor) {
            return this.iconActiveColor;
          }
          return 'amber accent-2';
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
            this.$emit("refresh-requested", this);
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
        addNewRow() {
            this.$emit("adding-new-row", null, this.newRow);
        },
        //Some external validation might be required or extra fields added.
        //The parent component should call this method to finish adding this.newRow to the table
        confirmAddingANewRow(newRow) {
            this.newRow = newRow;
            this.items.push(this.newRow);
            this.newRow = {};
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
         //When using a Vuex store
        manualDataFilteredFromStore(summaryGetter, dataGetter) {
          if (this.headers.length != summaryGetter.headers.length) {
              //just a refresh. Keep the headerOrder in place
              //in case the user modified the column order
              this.headerOrder = summaryGetter.headerOrder;
          }
          this.headers = summaryGetter.headers;
          this.items = dataGetter;
          this.pagination.totalItems = this.items.length;
          this.uniqueIdField = summaryGetter.uniqueIdField;
          this.stopLoading();
      },
        manualDataError() {
            this.stopLoading();
        },
        startLoading() {
            //since the ajax call could be done externally, call this method
            //to set the proper messages and loading states
            this.loading = this.isLoadingColor ? true : false;
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
            return header + this.tableTitle === this.itemDragging;
        },
        toggleAddRowBar() {
            this.showAddRowBar = !this.showAddRowBar;
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
                bus.$emit("need-layout-resize");
            }
        },
        // handleScroll(event) {
        //     if (this.disableStickyHeader) {
        //         return;
        //     }
        //     if (!this.stickyHeader) {
        //         this.stickyHeader = $('table').stickyTableHeaders({ fixedOffset: 48 }); //need to use the html element rather than the element id
        //     }
        //     this.$nextTick(function () {
        //         $(window).trigger('resize.stickyTableHeaders');
        //     });

        // },
        handleDialogs(response, callback) {
            if (response.data.isXss) {
                bus.$emit("xss-error", [null, response.data.reason]);
            }
            else {
                bus.$emit("login-needed", [null, callback])
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
                if (s.indexOf("< ") > -1 || r.indexOf("< ") > -1) { //deal with special notations < 0.01%
                    sNb = s;
                    rNb = r;
                    if (s.indexOf("< ") > -1) {
                        sNb = s.replace("< ", "");
                        if (!isNaN(sNb)) {
                            sNb = parseFloat(sNb) / 10000;
                        }
                    }
                    if (r.indexOf("< ") > -1) {
                        rNb = r.replace("< ", "");
                        if (!isNaN(rNb)) {
                            rNb = parseFloat(rNb) / 10000;
                        }
                    }
                    return sNb - rNb;
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
        delaySearch() {
          clearTimeout(this.currentSeachTimeout);
          this.currentSeachTimeout = setTimeout(() => {
            this.search = this.pendingSearch;
          }, 501);
        },
        //returns the page number (base 0) for the highlighted row
        //Can be used to jump to the page the highlighted row is on
        //can freeze the UI for large tables. Use with care
        findPageForHiglightedItem() {
            var sortedItems = this.items.sort((a,b) => {return this.customSort(this.items, this.pagination.sortBy, this.pagination.descending)});
            var index = sortedItems.map(i => i[this.uniqueIdField]).indexOf(this.highlight);
            if (index > -1) {
                var pageNb = Math.floor(index / this.pagination.rowsPerPage);
                return pageNb;
            }
            return -1;
        },
        findPageForCurrentUniqueId(uniqueId) {
          var sortedItems = this.items.sort((a,b) => {return this.customSort(this.items, this.pagination.sortBy, this.pagination.descending)});
          var index = sortedItems.map(i => i[this.uniqueIdField]).indexOf(uniqueId);
          if (index > -1) {
              var pageNb = Math.floor(index / this.pagination.rowsPerPage);
              return pageNb;
          }
          return -1;
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
            if (header.isSafe) {
                return itemString;
            }
            //when not safe, at least remove the br tags for display
            if (typeof itemString == "string") {
                return itemString.replace("<br/>", " ");
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
            this.selectionChanged(props.item);
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
        handleButtonTriggered(action, item, event) {
            bus.$emit(action, item, event);
        },
        exportToCSV() {
            this.doExport = true;
            this.getAjaxData();
        },
        createCSVFile() {
        	 var blob = new Blob(
                     [this.csvContent],
                     {
                         type: "text/plain;charset=utf-8"
                     }
                 )
                 var downloadUrl = URL.createObjectURL( blob );
                 var hiddenElement = document.createElement('a');
                 hiddenElement.href = downloadUrl;
                 //hiddenElement.target = '_blank';
                 hiddenElement.download = this.tableTitle.replace(" ", "_") + '_data.csv';
                 document.body.appendChild(hiddenElement);
                 hiddenElement.click();
                 document.body.removeChild(hiddenElement);
                 URL.revokeObjectURL( downloadUrl );

        },
        getRowCount() {
            if (this.$refs.dataTable) {
                var rowCount = this.$refs.dataTable.itemsLength;
                this.filteringActive = this.search != "" || this.externalFilteringActive;
                return "(" + rowCount 
                + (this.filteringActive ? " filtered" : "") //adds "filtered" if a filter is used (quick search or advanced filtering)
                + " row" 
                + (rowCount > 1 ? "s" : "") //adds "s" if more than 1 row
                + ")";
            }
            return null;
        },
        selectionChanged(item) {
            this.$emit('datatable-selection-changed', this.selected.length, item, this.idType);
        },
        getFilteredItems() {
          if (this.$refs.dataTable) {
            return this.$refs.dataTable.filteredItems;
          }
          return null;
        },
        getCurrentItemIndex(currentUniqueId) {
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
        //could pass the currentRow or the currentId. usingUniqueId is true if currentRowOrId is a uniqueId
        //and false if it's currentRow
        getPreviousItem(currentRowOrId, usingUniqueId) {
            let uniqueId = usingUniqueId ? currentRowOrId : currentRowOrId[this.uniqueIdField];
            if (uniqueId != null) {
                var currentIndex = this.getCurrentItemIndex(uniqueId);
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
        getNextItem(currentRowOrId, usingUniqueId) {
          let uniqueId = usingUniqueId ? currentRowOrId : currentRowOrId[this.uniqueIdField];
          if (uniqueId != null) {
                var currentIndex = this.getCurrentItemIndex(uniqueId);
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
        },
        hasData() {
          return this.items.length > 0;
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
        // window.removeEventListener('scroll', this.handleScroll);
        // $('#' + this.tableId).stickyTableHeaders('destroy');
        //make sure the layout is refreshed for other views
        bus.$emit("need-layout-resize");
    },
    mounted: function () {
        // window.addEventListener('scroll', this.handleScroll);
        // this.$el.querySelector('#' + this.tableId).firstElementChild.addEventListener('scroll', this.handleScroll);

        if (this.fetchOnCreated) {
            this.getAjaxData();
        } else {
            this.loading = false;
        }
    },
    watch: {
        "selected": 'selectionChanged',
        pendingSearch: "delaySearch"
    }



});

var dateRegex = /[0-9]{2}\/[0-9]{2}\/[0-9]{4}/;
