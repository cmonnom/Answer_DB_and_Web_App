const Home = {
    template:
        `<div>

        <v-snackbar :timeout="snackBarTimeout" :bottom="true" v-model="snackBarVisible">
        {{ snackBarMessage }}
        <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
      </v-snackbar>

  <v-dialog v-model="assignDialogVisible" max-width="50%" scrollable>
    <v-card>
      <v-toolbar dense dark color="primary">
        <v-toolbar-title class="white--text">Assign case: {{ currentEpicOrderNumber }}</v-toolbar-title>
      </v-toolbar>
      <v-card-title>Pick who should work on this case:</v-card-title>
      <v-card-text>
        <v-layout row wrap class="pl-2">
          <v-flex xs12 lg6 v-for="(user, index) in allUsers" :key="index">
            <v-switch :label="createUserLabel(user)" v-model="usersAssignedToCase[index]"></v-switch>
          </v-flex>
        </v-layout>
      </v-card-text>
      <v-card-actions>
      <v-tooltip bottom>
        <v-btn color="success" @click="assignToUser()" slot="activator">Save
          <v-icon right dark>save</v-icon>
        </v-btn>
        <span>Answer will send an email notification<br/>to the selected users</span>
        </v-tooltip>
        <v-btn color="error" @click="cancelAssign()">Cancel
          <v-icon right dark>cancel</v-icon>
        </v-btn>
        <v-checkbox v-model="receiveACopyOfEmail" label="Also send a notification to my email" hide-details></v-checkbox>
        
      </v-card-actions>
    </v-card>
  </v-dialog>

  <v-dialog v-model="assignGroupDialogVisible" max-width="50%" scrollable>
  <v-card>
    <v-toolbar dense dark color="primary">
      <v-toolbar-title class="white--text">Assign group to case: {{ currentEpicOrderNumber }} ({{currentPatientName}})</v-toolbar-title>
    </v-toolbar>
    <v-card-title>Pick groups that can access this case:</v-card-title>
    <v-card-text>
      <v-layout row wrap class="pl-2">
        <v-flex xs12 lg6 v-for="(group, index) in allGroups" :key="index">
          <v-switch :label="createGroupLabel(group)" v-model="groupsAssignedToCase[index]"></v-switch>
        </v-flex>
      </v-layout>
    </v-card-text>
    <v-card-actions>
    <v-tooltip bottom>
      <v-btn color="success" @click="assignToGroup()" slot="activator">Save
        <v-icon right dark>save</v-icon>
      </v-btn>
      <span></span>
      </v-tooltip>
      <v-btn color="error" @click="cancelAssignGroup()">Cancel
        <v-icon right dark>cancel</v-icon>
      </v-btn>
      
    </v-card-actions>
  </v-card>
</v-dialog>

  
  <v-dialog v-model="signoutDialogVisible" max-width="500px" scrollable>
    <v-card>
      <v-toolbar dense dark color="primary">
        <v-toolbar-title class="white--text">Archive case {{ currentEpicOrderNumber }} ({{ currentPatientName }})?</v-toolbar-title>
      </v-toolbar>
      <v-card-text class="pb-3 pt-3 pr-3 pl-3">
        You are about to archive this case. Which means all work on this case is done.<br/>
        It will be removed from <b>My Cases</b> for all users assigned to the case.<br/>
        It will still be accessible in <b>Archived Cases</b> but greyed out.
      </v-card-text>
      <v-card-actions>
      <v-tooltip bottom>
        <v-btn color="success" @click="toggleArchivingStatusForCase(currentCaseId, true)" slot="activator">Archive
          <v-icon right dark>mdi-logout</v-icon>
        </v-btn>
        <span>Archive the case and remove it from <b>My Cases</b></span>
        </v-tooltip>
        <v-btn color="error" @click="signoutDialogVisible = false">Cancel
          <v-icon right dark>cancel</v-icon>
        </v-btn>
        
      </v-card-actions>
    </v-card>
  </v-dialog>

  <v-toolbar dense dark color="primary" fixed app>
    <v-tooltip class="ml-0" bottom>
      <v-menu offset-y offset-x slot="activator" class="ml-0">
        <v-btn slot="activator" flat icon dark>
          <v-icon>more_vert</v-icon>
        </v-btn>
        <v-list>
          <v-list-tile avatar @click="toggleTable('forUser')">
          <v-list-tile-avatar>
          <v-icon>mdi-account</v-icon>
        </v-list-tile-avatar>
          <v-list-tile-title>Show/Hide My Cases</v-list-tile-title>
        </v-list-tile>

          <v-list-tile avatar @click="toggleTable('all')">
          <v-list-tile-avatar>
          <v-icon>mdi-table-search</v-icon>
        </v-list-tile-avatar>
            <v-list-tile-title>Show/Hide All Cases Table</v-list-tile-title>
          </v-list-tile>

          <v-list-tile avatar @click="toggleTable('finalized')">
          <v-list-tile-avatar>
          <v-icon>mdi-check-all</v-icon>
        </v-list-tile-avatar>
          <v-list-tile-title>Show/Hide Finalized Cases</v-list-tile-title>
        </v-list-tile>

        <v-list-tile avatar @click="toggleTable('archived')">
        <v-list-tile-avatar>
        <v-icon>mdi-archive</v-icon>
      </v-list-tile-avatar>
        <v-list-tile-title>Show/Hide Archived Cases</v-list-tile-title>
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
        <v-icon :color="caseForUserTableVisible ? 'amber accent-2' : ''">mdi-account</v-icon>
      </v-btn>
      <span>Show/Hide My Cases</span>
    </v-tooltip>
    
    <v-tooltip bottom>
      <v-btn icon @click="toggleTable('all')" slot="activator">
        <v-icon :color="caseAllTableVisible ? 'amber accent-2' : ''">mdi-table-search</v-icon>
      </v-btn>
      <span>Show/Hide All Cases Table</span>
    </v-tooltip>

    <v-tooltip bottom>
    <v-btn icon @click="toggleTable('finalized')" slot="activator">
      <v-icon :color="caseFinalizedTableVisible ? 'amber accent-2' : ''">mdi-check-all</v-icon>
    </v-btn>
    <span>Show/Hide Finalized Cases</span>
  </v-tooltip>

  <v-tooltip bottom>
  <v-btn icon @click="toggleTable('archived')" slot="activator">
    <v-icon :color="caseArchivedTableVisible ? 'amber accent-2' : ''">mdi-archive</v-icon>
  </v-btn>
  <span>Show/Hide Archived Cases</span>
</v-tooltip>

  </v-toolbar>
  <v-container grid-list-md fluid class="pl-0 pr-0">
    <v-layout row wrap>
      <v-slide-x-transition>
        <v-flex xs12 v-show="caseForUserTableVisible" >
          <data-table ref="casesForUserTable" :fixed="false" :fetch-on-created="false" table-title="My Cases" :initial-sort="'epicOrderDate'"
            no-data-text="No Data" :show-pagination="true" title-icon="mdi-account">
          </data-table>
        </v-flex>
      </v-slide-x-transition>

      <v-slide-x-transition>
        <v-flex xs12 v-show="caseAllTableVisible" >
          <data-table ref="casesAllTable" :fixed="false" :fetch-on-created="false" table-title="All Cases" :initial-sort="'epicOrderDate'"
            no-data-text="No Data" :show-pagination="true" title-icon="mdi-table-search">
          </data-table>
        </v-flex>
      </v-slide-x-transition>

      <v-slide-x-transition>
      <v-flex xs12 v-show="caseFinalizedTableVisible" >
        <data-table ref="casesFinalizedTable" :fixed="false" :fetch-on-created="false" table-title="Cases Finalized" :initial-sort="'epicOrderDate'"
          no-data-text="No Data" :show-pagination="true" title-icon="mdi-check-all">
        </data-table>
      </v-flex>
    </v-slide-x-transition>

    <v-slide-x-transition>
    <v-flex xs12 v-show="caseArchivedTableVisible" >
      <data-table ref="casesArchivedTable" :fixed="false" :fetch-on-created="false" table-title="Cases Archived" :initial-sort="'epicOrderDate'"
        no-data-text="No Data" :show-pagination="true" title-icon="mdi-archive">
      </data-table>
    </v-flex>
  </v-slide-x-transition>

    </v-layout>
  </v-container>
</div>`,
    data() {
        return {
            assignDialogVisible: false,
            assignGroupDialogVisible: false,
            currentCaseId: null,
            allUsers: [],
            allGroups: [],
            usersAssignedToCase: [],
            groupsAssignedToCase: [],
            currentEpicOrderNumber: "",
            currentPatientName: "",
            caseForUserTableVisible: true,
            caseAllTableVisible: true,
            caseFinalizedTableVisible: true,
            caseArchivedTableVisible: false,
            tableFlex: 'xs4',
            creatingReport: false,
            snackBarVisible: false,
            snackBarMessage: "",
            signoutDialogVisible: false,
            snackBarTimeout: 0,
            receiveACopyOfEmail: false
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
                        this.$refs.casesAllTable.manualDataFiltered(response.data.casesAll);
                        this.$refs.casesForUserTable.manualDataFiltered(response.data.casesForUser);
                        this.$refs.casesFinalizedTable.manualDataFiltered(response.data.casesFinalized);
                        this.$refs.casesArchivedTable.manualDataFiltered(response.data.casesArchived);
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
        getAllGroups() {
            this.groupsAssignedToCase = [];
            axios.get("./getAllGroupsForUsers", {
                params: {
                }
            })
                .then(response => {
                    if (response.data.isAllowed) {
                        this.allGroups = response.data.items;
                        for (var i = 0; i < this.allGroups.length; i++) {
                            var group = this.allGroups[i];
                            this.groupsAssignedToCase.push(false);
                        }
                    }
                    else {
                        this.handleDialogs(response.data, this.getAllGroups);
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
                    userIdsParam: userIds.join(","),
                    receiveACopyOfEmail: this.receiveACopyOfEmail
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.assignDialogVisible = false;
                        this.getWorklists();
                    }
                    else {
                        this.handleDialogs(response.data, this.assignToUser.bind(null, this.currentCaseId));
                    }
                })
                .catch(error => {
                    alert(error);
                });
        },
        assignToGroup() {
            var groupIds = [];
            for (var i = 0; i < this.allUsers.length; i++) {
                if (this.groupsAssignedToCase[i] === true) {
                    groupIds.push(this.allGroups[i].value);
                }
            };
            axios.get("./assignToGroup", {
                params: {
                    caseId: this.currentCaseId,
                    groupIdsParam: groupIds.join(",")
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.assignGroupDialogVisible = false;
                        this.getWorklists();
                    }
                    else {
                        this.handleDialogs(response.data, this.assignToGroup.bind(null, this.currentCaseId));
                    }
                })
                .catch(error => {
                    alert(error);
                });
        },
        toggleArchivingStatusForCase(caseId, doArchive) {
            this.signoutDialogVisible = false;
            axios.get("./toggleArchivingStatusForCase", {
                params: {
                    caseId: caseId,
                    doArchive: doArchive
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.getWorklists();
                    }
                    else {
                        this.handleDialogs(response.data, this.toggleArchivingStatusForCase.bind(null, caseId));
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
        cancelAssignGroup() {
            this.assignGroupDialogVisible = false;
            this.groupsAssignedToCase = [];
        },
        //use this to make tables  resize when other are shown/hidden
        // setFlexClass() {
        //     var xs = 12;
        //     var nbPanelVisible = 0;
        //     if (this.caseAvailableTableVisible) {
        //         nbPanelVisible++;
        //     }
        //     if (this.caseForUserTableVisible) {
        //         nbPanelVisible++;
        //     }
        //     if (this.caseAssignedTableVisible) {
        //         nbPanelVisible++;
        //     }
        //     if (nbPanelVisible == 0) {
        //         this.tableFlex = "xs" + 12;
        //     }
        //     else {
        //         this.tableFlex = "xs" + (xs / nbPanelVisible);
        //     }
        // },
        toggleTable(tableName) {
            var restoring = false;
            if (tableName == 'all') {
                this.caseAllTableVisible = !this.caseAllTableVisible;
                restoring = this.caseAllTableVisible;
            }
            else if (tableName == 'forUser') {
                this.caseForUserTableVisible = !this.caseForUserTableVisible;
                restoring = this.caseForUserTableVisible;
            }
            else if (tableName == 'finalized') {
                this.caseFinalizedTableVisible = !this.caseFinalizedTableVisible;
                restoring = this.caseForUserTableVisible;
            }
            else if (tableName == 'archived') {
                this.caseArchivedTableVisible = !this.caseArchivedTableVisible;
                restoring = this.caseForUserTableVisible;
            }
            // else {
            //     this.caseAssignedTableVisible = !this.caseAssignedTableVisible;
            //     restoring = this.caseAssignedTableVisible;
            // }
            // if (restoring) {
            //     this.setFlexClass();
            // }
            // else {
            //     setTimeout(() => {
            //         this.setFlexClass();
            //     }, 400);
            // }
        },
        createUserLabel(user) {
            var title = user.canReview ? "(Reviewer)" : "";
            return user.name + " " + title;
        },
        createGroupLabel(group) {
            return group.name;
        },
        createPDFReport(reportId) {
            if (this.creatingReport) {
                return; //Already creating a report
            }
            this.creatingReport = true;
            this.snackBarMessage = "Downloading PDF Report...";
            this.snackBarTimeout = 0;
            this.snackBarVisible = true;
            axios.get("./createPDFReport", {
                params: {
                    reportId: reportId,
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        window.open(webAppRoot + "/pdfs/" + response.data.message, "_blank");
                    } else {
                        this.handleDialogs(response.data, this.createPDFReport.bind(null, reportId));
                    }
                    this.creatingReport = false;
                    this.snackBarVisible = false;
                })
                .catch(error => {
                    this.creatingReport = false;
                    this.snackBarVisible = false;
                    alert(error);
                });
        },
        showLastLoginAttempt() {
            if (showLastLogin) {
                this.snackBarMessage = "Last Login Attempt: " + lastLogin;
                this.snackBarTimeout = 8000;
                this.snackBarVisible = true;
                showLastLogin = false;
            }
        }
    },
    mounted: function () {
        this.getAllUsers();
        this.getAllGroups();
        this.getWorklists();
    },
    destroyed: function () {
        bus.$off('assignToUser');
        bus.$off('assignToGroup');
        bus.$off('open');
        bus.$off('open-read-only');
        bus.$off('edit-report');
        bus.$off('open-report-read-only');
        bus.$off('deactivate-case');
        bus.$off('downloadPDFReport');
       
    },
    created: function () {
        splashDialog = false; //disable splash screen if coming from Home
        bus.$on('assignToUser', (item) => {
            this.currentEpicOrderNumber = item.epicOrderNumber;
            this.currentPatientName = item.patientName;
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
        bus.$on('assignToGroup', (item) => {
            this.currentEpicOrderNumber = item.epicOrderNumber;
            this.currentPatientName = item.patientName;
            this.currentCaseId = item.caseId;
            this.groupsAssignedToCase = [];

            for (var i = 0; i < this.allGroups.length; i++) {
                var group = this.allGroups[i];
                var groupAssigned = false;
                if (item.groupIds) {
                    for (var j = 0; j < item.groupIds.length; j++) {
                        if (item.groupIds[j] == group.value) {
                            groupAssigned = true;
                            break;
                        }
                    }
                }
                this.groupsAssignedToCase.push(groupAssigned);
            }
            this.assignGroupDialogVisible = true;
        });
        bus.$on('open', (item) => {
            router.push("./openCase/" + item.caseId);
        });
        bus.$on('open-read-only', (item) => {
            router.push("./openCaseReadOnly/" + item.caseId);
        });
        bus.$on('edit-report', (item) => {
            router.push("./openReport/" + item.caseId);
        });
        bus.$on('open-report-read-only', (item) => {
            router.push("./openReportReadOnly/" + item.caseId);
        });
        bus.$on('deactivate-case', (item) => {
            this.currentCaseId = item.caseId;
            this.currentEpicOrderNumber = item.epicOrderNumber;
            this.currentPatientName = item.patientName;
            this.signoutDialogVisible = true;
        });
        //TODO
        bus.$on('downloadPDFReport', (item) => {
            this.createPDFReport(item.reportId);
        });
        this.showLastLoginAttempt();
    },
    computed: {
    },
    watch: {
    }
};

