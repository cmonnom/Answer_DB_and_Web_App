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
      <v-switch :label="createUserLabel(user)" v-model="usersAssignedToCase[index]" @change="handleAssignToUserValid"></v-switch>
      </v-flex>
      <v-flex xs>
    </v-flex>
      </v-layout>

      <v-layout row wrap class="pl-2">
        <v-flex xs6 lg5 xl3>
        <v-select clearable :value="caseOwnerSelected" :items="allUsers" v-model="caseOwnerSelected" return-object item-text="name" item-value="value"
        :disabled="!currentCaseOwnerEnabled"
        label="Case Owner" ></v-select>
        </v-flex>
        <v-flex xs6 lg5 xl3 pt-3 pl-3>
            <v-tooltip bottom>
            <v-switch slot="activator" label="Change Case Owner" v-model="currentCaseOwnerEnabled" color="error"></v-switch>
            <span>The case owner is responsible for deciding which variants go in the report.
            <br/>The new case owner's variant will be used to build new reports.
            <br/>Are you sure you want to change it? (this is not common)</span>
            </v-tooltip>
        </v-flex>
      </v-layout>

      </v-card-text>
      <v-card-actions>
      <v-tooltip bottom>
      <v-btn color="success" @click="assignToUser()" slot="activator" :disabled="assignToUserDisabled">Save
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

  
  <v-toolbar dense dark color="primary" fixed app>
    <v-icon color="amber accent-2">mdi-home</v-icon>
  </v-tooltip>
    <v-toolbar-title class="white--text">
      Worklists
    </v-toolbar-title>
    <v-spacer></v-spacer>

   

  </v-toolbar>
  <v-container grid-list-md fluid class="pl-0 pr-0 pt-0">

    <v-tabs dark slider-color="warning" color="primary darken-1" fixed-tabs v-model="activateTab">
        <v-tab href="#tab-userCases" :ripple="false">My Cases</v-tab>
        <v-tab href="#tab-allCases" :ripple="false">All Cases</v-tab>
        <v-tab href="#tab-finalizedCases" :ripple="false">Ready for Epic</v-tab>

        <v-tabs-items>
            <v-tab-item id="tab-userCases"  class="pt-1">
            <v-layout row wrap>
            <v-slide-x-transition>
              <v-flex xs12 >
                <data-table ref="casesForUserTable" :fixed="false" :fetch-on-created="false" table-title="Active Cases" :initial-sort="'epicOrderDate'"
                  no-data-text="No Data" :show-pagination="true" title-icon="star" @refresh-requested="handleRefresh()">
                </data-table>
              </v-flex>
            </v-slide-x-transition>
    
            <v-slide-x-transition>
            <v-flex xs12 >
              <data-table ref="casesForUserCompletedTable" :fixed="false" :fetch-on-created="false" table-title="Completed Cases" :initial-sort="'epicOrderDate'"
                no-data-text="No Data" :show-pagination="true" title-icon="star" @refresh-requested="handleRefresh()">
              </data-table>
            </v-flex>
          </v-slide-x-transition>
    
            </v-layout>
            </v-tab-item>
    
            <v-tab-item id="tab-allCases"  class="pt-1">
                <v-layout row wrap>
                  <v-slide-x-transition>
                    <v-flex xs12>
                      <data-table ref="casesAllTable" :fixed="false" :fetch-on-created="false" table-title="All Cases" :initial-sort="'epicOrderDate'"
                        no-data-text="No Data" :show-pagination="true" title-icon="mdi-table-search" @refresh-requested="handleRefresh()">
                      </data-table>
                    </v-flex>
                  </v-slide-x-transition>
                  </v-layout>
            </v-tab-item>
    
            <v-tab-item id="tab-finalizedCases"  class="pt-1">
              <v-layout row wrap>
              <v-slide-x-transition>
              <v-flex xs12>
                <data-table ref="casesFinalizedTable" :fixed="false" :fetch-on-created="false" table-title="Cases Ready for Epic" :initial-sort="'epicOrderDate'"
                :sort-descending="true" no-data-text="No Data" :show-pagination="true" title-icon="mdi-check-all" @refresh-requested="handleRefresh()">
                </data-table>
              </v-flex>
            </v-slide-x-transition>
            </v-layout>
            </v-tab-item>
    
        </v-tabs-items>
    </v-tabs>


  </v-container>
</div>`,
    data() {
        return {
            activateTab: null,
            assignDialogVisible: false,
            assignGroupDialogVisible: false,
            currentCaseId: null,
            allUsers: [],
            allGroups: [],
            usersAssignedToCase: [],
            groupsAssignedToCase: [],
            currentEpicOrderNumber: "",
            currentPatientName: "",
            tableFlex: 'xs4',
            creatingReport: false,
            snackBarVisible: false,
            snackBarMessage: "",
            snackBarTimeout: 0,
            receiveACopyOfEmail: false,
            assignToUserDisabled: false,
            assignToSelectionValid: true,
            caseOwnerSelected: null,
            currentCaseOwnerEnabled: false
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
            this.$refs.casesAllTable.startLoading();
            this.$refs.casesForUserTable.startLoading();
            this.$refs.casesForUserCompletedTable.startLoading();
            this.$refs.casesFinalizedTable.startLoading();
            axios.get("./getWorklists", {
                params: {
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.$refs.casesAllTable.manualDataFiltered(response.data.casesAll);
                        this.$refs.casesForUserTable.manualDataFiltered(response.data.casesForUser);
                        this.$refs.casesForUserCompletedTable.manualDataFiltered(response.data.casesForUserCompleted);
                        this.$refs.casesFinalizedTable.manualDataFiltered(response.data.casesFinalized);
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
        handleAssignToUserValid() {
           this.assignToSelectionValid = this.isAssignToUserValid();
           this.assignToUserDisabled = !this.assignToSelectionValid;
        },
        isAssignToUserValid() {
            return this.allUsers.length > 0;
            // var reviewerCount = 0;
            // for (var i = 0; i < this.allUsers.length; i++) {
            //     if (this.usersAssignedToCase[i] === true) {
            //         if (this.allUsers[i].canReview) {
            //             reviewerCount++;
            //         }
            //     }
            // };
            // return reviewerCount <= 1;
        },
        assignToUser() {
            this.assignToUserDisabled = true;
            var userIds = [];
            for (var i = 0; i < this.allUsers.length; i++) {
                if (this.usersAssignedToCase[i] === true) {
                    userIds.push(this.allUsers[i].value);
                }
            };
            if (!this.isAssignToUserValid()) {
                this.assignToSelectionValid = false;
                return;
            }
            axios.get("./assignToUser", {
                params: {
                    caseId: this.currentCaseId,
                    userIdsParam: userIds.join(","),
                    caseOwnerId: this.caseOwnerSelected && this.caseOwnerSelected.value != "-1" ? this.caseOwnerSelected.value : null,
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
                    this.assignToUserDisabled = false;
                })
                .catch(error => {
                    alert(error);
                    this.assignToUserDisabled = false;
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
        toggleSentToEpicStatusForCase(caseId) {
            axios.get("./toggleSentToEpicStatusForCase", {
                params: {
                    caseId: caseId
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        this.getWorklists();
                    }
                    else {
                        this.handleDialogs(response.data, this.toggleSentToEpicStatusForCase.bind(null, caseId));
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
                this.updateLastLoginAttempt();
            }
        },
        updateLastLoginAttempt() {
            axios({
                method: 'post',
                url: webAppRoot + "/updateLastLogin",
                params: {
                },
                data: {
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                }
                else {
                    //something is wrong. The user has been disconnected
                }
            }).catch(error => {
                this.saveLoading = false;
                this.handleAxiosError(error);
            });
        },
        handleRefresh() {
            this.getWorklists();
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
        bus.$off('sent-to-epic');
        bus.$off('downloadPDFReport');
       
    },
    created: function () {
        splashDialog = false; //disable splash screen if coming from Home
        bus.$on('assignToUser', (item) => {
            this.currentEpicOrderNumber = item.epicOrderNumber;
            this.currentPatientName = item.patientName;
            this.currentCaseId = item.caseId;
            var caseOwnerId = item.caseOwnerId;
            this.currentCaseOwnerEnabled = true;
            this.caseOwnerSelected = null;
            if (caseOwnerId && caseOwnerId > -1) {
                this.currentCaseOwnerEnabled = false; //already a case owner. Disable the button
            }
            this.usersAssignedToCase = [];

            for (var i = 0; i < this.allUsers.length; i++) {
                var user = this.allUsers[i];
                var userAssigned = false;
                if (user.value == caseOwnerId) {
                    this.caseOwnerSelected = user;
                }
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
        bus.$on('sent-to-peic', (item) => {
            this.currentCaseId = item.caseId;
            this.currentEpicOrderNumber = item.epicOrderNumber;
            this.currentPatientName = item.patientName;
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

