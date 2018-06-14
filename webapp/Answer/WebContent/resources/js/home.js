const Home = {
    template:
        `<div>
  <v-dialog v-model="assignDialogVisible" max-width="50%">
    <v-card>
      <v-toolbar dark color="primary">
        <v-toolbar-title class="white--text">Assign case: {{ currentEpicOrderNumber }}</v-toolbar-title>
      </v-toolbar>
      <v-card-title>Pick who should work on this case:</v-card-title>
      <v-card-text>
        <!-- <v-list>
          <v-list-tile class="dense-tiles" v-for="(user, index) in allUsers" :key="index">
            <v-switch :label="user.name" v-model="usersAssignedToCase[index]"></v-switch> 
          </v-list-tile>
        </v-list> -->
        <v-layout row wrap>
          <v-flex xs12 lg6 v-for="(user, index) in allUsers" :key="index">
            <v-switch :label="user.name" v-model="usersAssignedToCase[index]"></v-switch>
          </v-flex>
        </v-layout>
      </v-card-text>
      <v-card-actions>
        <v-btn color="success" @click="assignToUser()">Save
          <v-icon right dark>save</v-icon>
        </v-btn>
        <v-btn color="error" @click="cancelAssign()">Cancel
          <v-icon right dark>cancel</v-icon>
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <v-toolbar dark color="primary" fixed app>
    <v-tooltip class="ml-0" bottom>
      <v-menu offset-y offset-x slot="activator" class="ml-0">
        <v-btn slot="activator" flat icon dark>
          <v-icon>more_vert</v-icon>
        </v-btn>
        <v-list>
          <v-list-tile @click="toggleTable('forUser')">
          <v-list-tile-title>Show/Hide My Cases</v-list-tile-title>
        </v-list-tile>
          <v-list-tile @click="toggleTable('available')">
            <v-list-tile-title>Show/Hide Cases Available</v-list-tile-title>
          </v-list-tile>
        <v-list-tile @click="toggleTable('assigned')">
          <v-list-tile-title>Show/Hide Cases Assigned</v-list-tile-title>
        </v-list-tile>
      </v-list>
    </v-menu>
    <span>Worklist Menu</span>
  </v-tooltip>
    <v-toolbar-title class="white--text ml-0">
      Worklists
    </v-toolbar-title>
    <v-spacer></v-spacer>

    <v-tooltip bottom>
      <v-btn icon @click="toggleTable('forUser')" slot="activator">
        <v-icon :color="caseForUserTableVisible ? 'amber accent-2' : ''">table_chart</v-icon>
      </v-btn>
      <span>Show/Hide My Cases</span>
    </v-tooltip>
    
    <v-tooltip bottom>
      <v-btn icon @click="toggleTable('available')" slot="activator">
        <v-icon :color="caseAvailableTableVisible ? 'amber accent-2' : ''">table_chart</v-icon>
      </v-btn>
      <span>Show/Hide Cases Available</span>
    </v-tooltip>

    <v-tooltip bottom>
      <v-btn icon @click="toggleTable('assigned')" slot="activator">
        <v-icon :color="caseAssignedTableVisible ? 'amber accent-2' : ''">table_chart</v-icon>
      </v-btn>
      <span>Show/Hide Cases Assigned</span>
    </v-tooltip>

  </v-toolbar>
  <v-container grid-list-md fluid class="pl-0 pr-0">
    <v-layout row wrap>
      <v-slide-x-transition>
        <v-flex xs12 v-show="caseForUserTableVisible" >
          <data-table ref="casesForUserTable" :fixed="false" :fetch-on-created="false" table-title="My Cases" :initial-sort="'epicOrderDate'"
            no-data-text="No Data" :show-pagination="false" title-icon="table_chart">
          </data-table>
        </v-flex>
      </v-slide-x-transition>
      <v-slide-x-transition>
        <v-flex xs6 v-show="caseAvailableTableVisible" >
          <data-table ref="casesAvailableTable" :fixed="false" :fetch-on-created="false" table-title="Cases Available" :initial-sort="'epicOrderDate'"
            no-data-text="No Data" :show-pagination="false" title-icon="table_chart">
          </data-table>
        </v-flex>
      </v-slide-x-transition>
      <v-slide-x-transition>
        <v-flex xs6 v-show="caseAssignedTableVisible" >
          <data-table ref="casesAssignedTable" :fixed="false" :fetch-on-created="false" table-title="Cases Assigned" :initial-sort="'epicOrderDate'"
            no-data-text="No Data" :show-pagination="false" title-icon="table_chart">
          </data-table>
        </v-flex>
      </v-slide-x-transition>
    </v-layout>
  </v-container>
</div>`,
    data() {
        return {
            assignDialogVisible: false,
            currentCaseId: null,
            allUsers: [],
            usersAssignedToCase: [],
            currentEpicOrderNumber: "",
            caseAvailableTableVisible: true,
            caseForUserTableVisible: true,
            caseAssignedTableVisible: true,
            tableFlex: 'xs4',
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
        getWorklists() {
            axios.get("./getWorklists", {
                params: {
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.$refs.casesAvailableTable.manualDataFiltered(response.data.casesAvailable);
                        this.$refs.casesForUserTable.manualDataFiltered(response.data.casesForUser);
                        this.$refs.casesAssignedTable.manualDataFiltered(response.data.casesAssigned);
                    }
                    else {
                        this.handleDialogs(response.data, this.getWorklists);
                    }
                })
                .catch(error => {
                    alert(error);
                });
        },
        getAllUsers() {
            this.usersAssignedToCase = [];
            axios.get("./getAllUsersToAssign", {
                params: {
                }
            })
                .then(response => {
                    if (response.data.isAllowed) {
                        this.allUsers = response.data.items;
                        for (var i = 0; i < this.allUsers.length; i++) {
                            var user = this.allUsers[i];
                            this.usersAssignedToCase.push(false);
                        }
                    }
                    else {
                        this.handleDialogs(response.data, this.getAllUsers);
                    }
                })
                .catch(error => {
                    alert(error);
                });
        },
        assignToUser() {
            var userIds = [];
            for (var i = 0; i < this.allUsers.length; i++) {
                if (this.usersAssignedToCase[i] === true) {
                    userIds.push(this.allUsers[i].value);
                }
            };
            axios.get("./assignToUser", {
                params: {
                    caseId: this.currentCaseId,
                    userIdsParam: userIds.join("|")
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.assignDialogVisible = false;
                        this.getWorklists();
                    }
                    else {
                        this.handleDialogs(response.data, this.assignToUser.bind(null, caseId));
                    }
                })
                .catch(error => {
                    alert(error);
                });
        },
        cancelAssign() {
            this.assignDialogVisible = false;
            this.usersAssignedToCase = [];
        },
        setFlexClass() {
            var xs = 12;
            var nbPanelVisible = 0;
            if (this.caseAvailableTableVisible) {
                nbPanelVisible++;
            }
            if (this.caseForUserTableVisible) {
                nbPanelVisible++;
            }
            if (this.caseAssignedTableVisible) {
                nbPanelVisible++;
            }
            if (nbPanelVisible == 0) {
                this.tableFlex = "xs" + 12;
            }
            else {
                this.tableFlex = "xs" + (xs / nbPanelVisible);
            }
        },
        toggleTable(tableName) {
            var restoring = false;
            if (tableName == 'available') {
                this.caseAvailableTableVisible = !this.caseAvailableTableVisible;
                restoring = this.caseAvailableTableVisible;
            }
            else if (tableName == 'forUser') {
                this.caseForUserTableVisible = !this.caseForUserTableVisible;
                restoring = this.caseForUserTableVisible;
            }
            else {
                this.caseAssignedTableVisible = !this.caseAssignedTableVisible;
                restoring = this.caseAssignedTableVisible;
            }
            if (restoring) {
                this.setFlexClass();
            }
            else {
                setTimeout(() => {
                    this.setFlexClass();
                }, 400);
            }
        }

    },
    mounted: function () {
        this.getAllUsers();
        this.getWorklists();
    },
    destroyed: function () {
        bus.$off('assignToUser');
        bus.$off('open');
    },
    created: function () {
        bus.$on('assignToUser', (item) => {
            this.currentEpicOrderNumber = item.epicOrderNumber;
            this.currentCaseId = item.caseId;
            this.usersAssignedToCase = [];

            for (var i = 0; i < this.allUsers.length; i++) {
                var user = this.allUsers[i];
                var userAssigned = false;
                if (item.assignedToIds) {
                    for (var j = 0; j < item.assignedToIds.length; j++) {
                        if (item.assignedToIds[j] == user.value) {
                            userAssigned = true;
                            break;
                        }
                    }
                }
                this.usersAssignedToCase.push(userAssigned);
            }
            this.assignDialogVisible = true;
        });
        bus.$on('open', (item) => {
            router.push("./openCase/" + item.caseId);
        });
    },
    computed: {
    },
    watch: {
    }
};

