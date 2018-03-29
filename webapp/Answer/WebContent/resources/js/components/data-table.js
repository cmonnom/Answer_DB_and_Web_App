

Vue.component('data-table', {
    props: {
        "table-title": { default: "Table", type: String },
        "data-url": "",
        "expanded-data-url": "",
        "initial-sort": { default: "id", type: String },
        "sort-descending": { default: false, type: Boolean },
        "toolbar-visible": { default: true, type: Boolean },
        "fetch-on-created": { default: true, type: Boolean },
        "no-data-text": { default: "Fetching data...", type: String },
        "fixed": { default: true, type: Boolean },
        "advance-filtering": { default: false, type: Boolean },
        "enable-selection": {default: false, type: Boolean},
        "action1-param": {default: "", type: String}

    },
    template: `<div>

  <!-- Top tool bar with menu options -->
  <v-toolbar dark color="primary" :fixed="fixed" :app="fixed" v-show="toolbarVisible">
    <v-toolbar-title class="white--text">{{ tableTitle }}</v-toolbar-title>
    <v-spacer></v-spacer>
    <v-layout justify-end>
      <v-flex class="text-xs-right">
        <v-container>
          <slot name="title"></slot>
        </v-container>
      </v-flex>
      <v-flex xs7 class="text-xs-right">
        <div class="title white--text pr-1 pt-4 mt-1">Rows:</div>
      </v-flex>
      <v-flex xs3>
        <v-container>
          <v-text-field solo single-line hide-details light v-model="pagination.rowsPerPage"></v-text-field>
        </v-container>
      </v-flex>
    </v-layout>

    <slot name="action1"></slot>
    <slot name="action2"></slot>
    <slot name="action3"></slot>

    <v-tooltip bottom>
      <v-btn icon @click="toggleSearchBar" slot="activator">
        <v-icon>search</v-icon>
      </v-btn>
      <span>Filter Results</span>
    </v-tooltip>

    <v-tooltip bottom v-if="advanceFiltering">
      <v-btn icon @click="toggleFilters" slot="activator">
        <v-icon>filter_list</v-icon>
      </v-btn>
      <span>Filter Menu</span>
    </v-tooltip>

    <v-tooltip bottom>
      <v-btn icon @click="showDraggableHeader=!showDraggableHeader" slot="activator">
        <v-icon>swap_horiz</v-icon>
      </v-btn>
      <span>Move Columns</span>
    </v-tooltip>

    <v-tooltip bottom>
      <v-btn icon @click="getAjaxData()" slot="activator">
        <v-icon>refresh</v-icon>
      </v-btn>
      <span>Refresh</span>
    </v-tooltip>

  </v-toolbar>

  <!-- Search Bar -->
  <v-slide-y-transition>
    <v-toolbar light class="mt-1" v-show="showSearchBar">
      <v-toolbar-title class="subheading">
        <v-btn icon @click="toggleSearchBar" slot="activator">
          <v-icon>keyboard_arrow_up</v-icon>
        </v-btn>
        Filter Results
      </v-toolbar-title>
      <v-spacer></v-spacer>
      <v-text-field ref="search" append-icon="search" label="Search" single-line hide-details v-model="search"></v-text-field>
    </v-toolbar>
  </v-slide-y-transition>

  <!-- Draggable Header -->
  <v-slide-y-transition>
    <div v-show="showDraggableHeader">
      <v-toolbar light class="mt-1">
        <v-toolbar-title class="subheading">
          <v-btn icon @click="showDraggableHeader=!showDraggableHeader" slot="activator">
            <v-icon>keyboard_arrow_up</v-icon>
          </v-btn>
          Move Columns</v-toolbar-title>
        <v-spacer></v-spacer>

      </v-toolbar>
      <v-card>
        <draggable :list="headerOrder" @start="draggingStarted" @end="itemDragging=''">
          <v-chip label v-for="header in headerOrder" :key="header" color="primary" text-color="white" :class="[{'is-dragging':isDragging(header)}, 'elevation-1']"
            :id="header">
            <v-avatar>
              <v-icon>swap_horiz</v-icon>
            </v-avatar>
            {{ getHeaderByValue(header) }}
          </v-chip>

        </draggable>
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
        <v-btn color="primary" @click="clearFilters">
        Clear
        </v-btn>
        <v-btn color="primary" @click="filterData">
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
            <v-text-field :name="filter.headerValue" :label="filter.headerText" v-model="filter.value"></v-text-field>
          </v-flex>

          <v-flex xs12 v-if="filter.type == 'Number'">
            <v-layout row>
              <v-flex xs4 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
              <v-flex xs4>
                <v-text-field :name="filter.headerValue + '-min'" label="Min" v-model="filter.minValue"></v-text-field>
              </v-flex>
              <v-flex xs4>
                <v-text-field name="filter.headerValue + '-max'" label="Max" v-model="filter.maxValue"></v-text-field>
              </v-flex>
            </v-layout>
          </v-flex>

          <v-flex xs12 v-if="filter.type == 'Date'">
            <v-layout row>
              <v-flex xs4 class="subheading mt-4" v-html="filter.headerText + ':'"></v-flex>
              <v-flex xs4>
                <v-menu lazy :v-model="false" transition="scale-transition" offset-y full-width :nudge-right="40" max-width="290px" min-width="290px"
                  :close-on-content-click="true">
                  <v-text-field slot="activator" label="From" v-model="filter.minValue" prepend-icon="event" readonly></v-text-field>
                  <v-date-picker v-model="filter.minValue" no-title scrollable>
                    <v-spacer></v-spacer>
                  </v-date-picker>
                </v-menu>
              </v-flex>
              <v-flex xs4>
                <v-menu lazy :v-model="false" transition="scale-transition" offset-y full-width :nudge-right="40" max-width="290px" min-width="290px"
                  :close-on-content-click="true">
                  <v-text-field slot="activator" label="To" v-model="filter.maxValue" prepend-icon="event" readonly></v-text-field>
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

  <v-data-table :id="tableId" v-model="selected" v-bind:headers="headers" v-bind:items="items" v-bind:search="search" hide-actions
    v-bind:pagination.sync="pagination" :item-key="uniqueIdField" :no-data-text="noDataText" :loading="loading" :class="['elevation-1', toolbarVisible ? 'mt-1' : '']"
    :custom-sort="customSort">
    <template slot="headers" slot-scope="props">
      <tr>
          <th v-if="enableSelection" class="primary white--text" style="width:50px">
              <v-checkbox
                hide-details
                @click.native="toggleAll"
                :input-value="props.all"
                :indeterminate="props.indeterminate"
              ></v-checkbox>
            </th>
          <th v-if="expandedDataUrl" class="primary">
          </th>
        <th v-for="header in getSortedHeaders" :key="header.text" :class="['primary white--text', 'subheading', 'column sortable', pagination.descending ? 'desc' : 'asc', header.value === pagination.sortBy ? 'active' : '']"
          @click="changeSort(header.value)" :width="header.width" :style="'min-width:' + header.width">
          <v-icon class="table-sorting-icon">arrow_upward</v-icon>
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
      <tr :active="props.selected" >
          <td v-if="enableSelection" style="width:50px"
          :class="[isHighlighted(props.item[uniqueIdField]) ? 'row-highlight' : '']">
              <v-checkbox
                primary
                hide-details
                :input-value="props.selected"
                @click="handleSelectionChange(props)"
                v-model="props.item.isSelected"
                :ripple="false"
              ></v-checkbox>
            </td>
        <td v-if="expandedDataUrl" class="pl-0 pr-0" :class="[isHighlighted(props.item[uniqueIdField]) ? 'row-highlight' : '']"
        @click="expandRow(props.item[uniqueIdField], props)">
          <v-btn flat icon small><v-icon :class="[props.expanded ? 'rotate180' : 'rotate90']">expand_less</v-icon>
          </v-btn>
        </td>
        <td v-for="header in getSortedHeaders" :class="[alignHeader(header), isHighlighted(props.item[uniqueIdField]) ? 'row-highlight' : '']" >
            <v-tooltip bottom v-if="props.item.tooltips && props.item.tooltips[header.value]"
            max-width="500px">
                <span slot="activator">
                  {{ formattedItem(header, props.item[header.value]) }}
                </span>
                <span v-html="props.item.tooltips[header.value]">
                </span>
            </v-tooltip>
            <span v-if="!(props.item.tooltips && props.item.tooltips[header.value])"
            v-html="formattedItem(header, props.item[header.value])"></span>
          <v-icon color="green" v-if="header.isPassable === true && props.item[header.value] && props.item[header.value].pass">check_circle</v-icon>
          <v-icon color="red" v-if="header.isPassable === true && props.item[header.value] && !props.item[header.value].pass">cancel</v-icon>
          <v-icon color="green" v-if="header.isActionable === true && props.item[header.value] && props.item[header.value].pass">check_circle</v-icon>
          <v-tooltip bottom>
          <v-btn :ripple="false" slot="activator" icon @click="header['itemAction'](props.item)" v-if="header.itemAction" >
              <v-icon >keyboard_arrow_right</v-icon>
          </v-btn>
          <span>{{ header.actionTooltip }}</span>
          </v-tooltip>
        </td>
      </tr>
    </template>
<!-- expanded row dynamically loaded when user clicks on row -->
    <template v-if="expandedDataUrl" slot="expand" slot-scope="props" >
      <v-data-table id="expandedTableId" ref="expandedTableId" v-model="selected" v-bind:headers="expandedHeaders" v-bind:items="expandedItems" hide-actions
        :item-key="uniqueIdField" no-data-text="No Data Found" :loading="loading" :class="['pl-5', 'pb-1', 'elevation-1', toolbarVisible ? 'mt-1' : '']"
        :custom-sort="customSort">
        <template slot="headers" slot-scope="props">
          <tr>
            <th v-for="header in expandedHeaders" :key="header.text" :class="['cyan white--text', 'subheading', 'column sortable']"
             :width="header.width">
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
              <span>{{ formattedItem(header, props.item[header.value]) }}</span>
              <v-icon color="green" v-if="header.isPassable === true && props.item[header.value] && props.item[header.value].pass">check_circle</v-icon>
              <v-icon color="red" v-if="header.isPassable === true && props.item[header.value] && !props.item[header.value].pass">cancel</v-icon>
            </td>
          </tr>
        </template>

      </v-data-table>
    </template>

  </v-data-table>
  <!-- external pagination -->
  <div class="text-xs-center pt-3">
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
            expandedHeaderOrder:[],
            filters: [],
            highlight: null //use this to change the style of a row should have the value of item.[uniqueIdField]
        }
    },
    methods: {
        toggleAll() {
            if (this.selected.length) this.selected = []
            else this.selected = this.items.slice()
        },
        changeSort(column) {
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
                    'filters': JSON.stringify(this.filters),
                    action1Param: this.action1Param
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
                            maxValue = filter.maxValue ? filter.maxValue + "": "";
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
        manualData(response) {
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
            if (!this.stickyHeader) {
                this.stickyHeader = $('table').stickyTableHeaders({ fixedOffset: 64 }); //need to use the html element rather than the element id
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
                if (!isNaN(s) && !isNaN(r))
                    return s - r;
                if (s.indexOf(",") > -1 || r.indexOf(",") > -1) { //one of them could be a formatted number
                    sNb = s.replace(/,/g, '');
                    rNb = r.replace(/,/g, '');
                    if (!isNaN(sNb) && !isNaN(rNb))
                        return parseInt(sNb) - parseInt(rNb);
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
            if (header.unit) {
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
            if (!item) {
                return null;
            }
            var itemString = null;
            if (header.isPassable === true || header.isActionable === true) {
                itemString = item.value;
            }
            else {
                itemString = item;
            }
            if (header.unit && header.unit.value != "Date") {
                itemString += " " + header.unit.value;
            }
            return itemString;
        },
        isHighlighted(itemUniqueField) {
            return this.highlight == itemUniqueField;
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
                    'sampleId': this.expandedUniqueId
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
                        // for (var i = 0; i < this.headers.length; i++) {
                        //     this.headers[i].width = "400px";
                        // }
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
            console.log(props.item.isSelected, props.selected);
            //if previous rows had been selected, the props.selected is out of sync
            //with props.item.isSelected.
            //Make sure to record which rows are unselected
            if ((props.item.isSelected === true && props.selected === undefined)
                || props.selected === true) {
                console.log("remove from variant selected");
                //check if already exist
                var found = false;
                for (var i = 0; i < this.toUnselect.length; i++) {
                    if (this.toUnselect[i].uniqueIdField == props.item.uniqueIdField) {
                        found = true
                        break;
                    }
                }
                if (!found) {
                    this.toUnselect.push(props.item);
                }
            }
            else if (props.item.isSelected === false){//about to be selected. Remove from unselected list
                //remove from unselected
                var indexToRemove = -1;
                for (var i = 0; i < this.toUnselect.length; i++) {
                    if (this.toUnselect[i].uniqueIdField == props.item.uniqueIdField) {
                        indexToRemove = i;
                        break;
                    }
                }
                if (indexToRemove > -1) {
                    this.toUnselect.splice(indexToRemove, 1);
                }
            }
            props.item.isSelected = !props.item.isSelected;
            props.selected = props.item.isSelected;
        }
    },
    computed: {
        getSortedHeaders: function () {
            var sortedHeaders = [];
            this.headerOrder.forEach(sortedHeader => {
                this.headers.forEach(header => {
                    if (sortedHeader === header.value) {
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
    }



});

var dateRegex = /[0-9]{2}\/[0-9]{2}\/[0-9]{4}/;
