const Home = {
    template:
        `<div>

        <v-snackbar :timeout="snackBarTimeout" :bottom="true" :value="snackBarVisible">
        {{ snackBarMessage }}
        <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
      </v-snackbar>

  <v-dialog v-model="assignDialogVisible" max-width="50%" scrollable>
    <v-card>
      <v-toolbar dense dark color="primary">
        <v-toolbar-title class="white--text">Assign case: {{ currentEpicOrderNumber }}</v-toolbar-title>
      </v-toolbar>
      <v-card-title>Pick who should work on this case:</v-card-title>
      <v-card-text height="50%">
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
            <v-switch slot="activator" label="Change Case Owner" v-model="currentCaseOwnerEnabled" color="error" hide-details class="no-margin-top-controls"></v-switch>
            <span>The case owner is responsible for deciding which variants go in the report.
            <br/>The new case owner's variant will be used to build new reports.
            <br/>Are you sure you want to change it? (this is not common)</span>
            </v-tooltip>
        </v-flex>
      </v-layout>

      </v-card-text>
      <v-card-actions>
      <v-tooltip bottom class="pr-2">
      <v-btn color="success" @click="assignToUser()" slot="activator" :disabled="assignToUserDisabled">Save
      <v-icon right dark>save</v-icon>
      </v-btn>
      <span>Answer will send an email notification<br/>to the selected users</span>
        </v-tooltip>
        <v-btn class="mr-2" color="error" @click="cancelAssign()">Cancel
          <v-icon right dark>cancel</v-icon>
        </v-btn>
        <v-checkbox v-model="receiveACopyOfEmail" label="Also send a notification to my email" hide-details class="no-margin-top-controls"></v-checkbox>
        </v-card-actions>
  


    </v-card>
  </v-dialog>

  <v-menu v-model="confirmSentToEpicDialogVisible" :position-x="positionx"  :position-y="positiony">
  <v-card>
  <v-toolbar color="primary" dark>
    <v-toolbar-title class="title">
    Report for Case: {{ currentEpicOrderNumber }} ({{currentPatientName}})
    </v-toolbar-title>
  </v-toolbar>
  <v-card-text class="pl-0 pr-0">
  <v-list>
    <v-list-tile @click="toggleSentToEpicStatusForCase(currentCaseId)">
    <v-list-tile-action>
    <v-icon  color="green">check_circle</v-icon>
    </v-list-tile-action>
    <v-list-tile-title>UPLOADED (The report was uploaded to Epic)</v-list-tile-title></v-list-tile>
    <v-list-tile @click="confirmSentToEpicDialogVisible = false">
    <v-list-tile-action>
    <v-icon color="red">cancel</v-icon>
    </v-list-tile-action>
    <v-list-tile-title>NOT READY (The report has not been uploaded yet)</v-list-tile-title></v-list-tile>
  </v-list>
  </v-card-text>
  </v-card>
</v-menu>

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
    <v-tooltip bottom class="pr-2">
      <v-btn color="success" @click="assignToGroup()" slot="activator">Save
        <v-icon right dark>save</v-icon>
      </v-btn>
      <span></span>
      </v-tooltip>
      <v-btn class="mr-2" color="error" @click="cancelAssignGroup()">Cancel
        <v-icon right dark>cancel</v-icon>
      </v-btn>
      
    </v-card-actions>
  </v-card>
</v-dialog>

  
  <v-toolbar dense dark color="primary" fixed app>
  <v-menu offset-y offset-x class="ml-0">
  <v-btn slot="activator" flat icon dark>
  <v-icon color="amber accent-2">mdi-home</v-icon>
          </v-btn>
          <v-list>
              <v-list-tile avatar @click="saveTabPreference()">
                  <v-list-tile-avatar>
                      <v-icon>bookmark</v-icon>
                  </v-list-tile-avatar>
                  <v-list-tile-content>
                      <v-list-tile-title>Bookmark Current Tab</v-list-tile-title>
                  </v-list-tile-content>
              </v-list-tile>
          </v-list>
</v-menu>
    <v-toolbar-title class="white--text ml-0" v-text="getWelcomeMessage()">
    </v-toolbar-title>
    <v-spacer></v-spacer>
    <v-tooltip bottom>
    <v-btn flat icon @click="saveTabPreference()" slot="activator" color="white">
        <v-icon>bookmark</v-icon>
    </v-btn>
    <span>Bookmark Current Tab</span>
</v-tooltip>
   

  </v-toolbar>
  <v-container grid-list-md fluid class="pl-0 pr-0 pt-0">

    <v-tabs dark slider-color="amber accent-2" color="primary darken-1" fixed-tabs v-model="activateTab" hide-slider>
        <v-tab href="#tab-userCases" :ripple="false" active-class="v-tabs__item--active primary">My Cases<v-icon class="pl-2" v-if="isDefaultTab('tab-userCases')" :key="forceRenderKey">bookmark</v-icon></v-tab>
        <v-tab href="#tab-allCases" :ripple="false" active-class="v-tabs__item--active primary">All Cases<v-icon class="pl-2" v-if="isDefaultTab('tab-allCases')" :key="forceRenderKey">bookmark</v-icon></v-tab>
        <v-tab href="#tab-finalizedCases" :ripple="false" active-class="v-tabs__item--active primary">Ready for Epic<v-icon class="pl-2" v-if="isDefaultTab('tab-finalizedCases')" :key="forceRenderKey">bookmark</v-icon></v-tab>

        <v-tabs-items>
            <v-tab-item value="tab-userCases"  class="pt-1">
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
    
            <v-tab-item value="tab-allCases"  class="pt-1">
                <v-layout row wrap>
                  <v-slide-x-transition>
                    <v-flex xs12>
                      <data-table ref="casesAllTable" :fixed="false" :fetch-on-created="false" table-title="All Cases" :initial-sort="'epicOrderDate'"
                      :sort-descending="true" no-data-text="No Data" :show-pagination="true" title-icon="mdi-table-search" @refresh-requested="handleRefresh()">
                      </data-table>
                    </v-flex>
                  </v-slide-x-transition>
                  </v-layout>
            </v-tab-item>
    
            <v-tab-item value="tab-finalizedCases"  class="pt-1">
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
            forceRenderKey: 0,
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
            currentCaseOwnerEnabled: false,
            firstTime: true,
            positionx: 0,
            positiony: 0,
            confirmSentToEpicDialogVisible: false
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
                        this.openDefaultTab();
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
        saveTabPreference() {
            axios({
                method: 'post',
                url: webAppRoot + "/saveTabPreference",
                params: {
                    tabid: this.activateTab
                },
                data: {
                }
            }).then(response => {
                if (response.data.isAllowed && response.data.success) {
                    defaultHomeTab = this.activateTab;
                    this.forceRenderKey++;
                }
                else {
                    //something is wrong. The user has been disconnected
                }
            }).catch(error => {
                this.handleAxiosError(error);
            });
        },
        handleRefresh() {
            this.getWorklists();
        },
        getWelcomeMessage() {
            var now = moment();
            var ampm = now.format("A");
            var isMorning = ampm == "AM";
            var isAfternoon = ampm == "PM";
            var isEvening = isAfternoon && now.hour() >= 18;
            var greetings = "Hello"
            if (isMorning) {
                greetings = "Good morning ";
            }
            else if (isEvening) {
                greetings = "Good evening ";
            }
            else if (isAfternoon) {
                greetings = "Good afternoon ";
            }
            return greetings + callingName;
        },
        openDefaultTab() {
            if (defaultHomeTab && this.firstTime) {
                this.firstTime = false;
                this.activateTab = defaultHomeTab;
            }
        },
        isDefaultTab(tabTitle) {
            return tabTitle == defaultHomeTab;
        },
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
        bus.$on('sent-to-epic', (item, event) => {
            this.currentCaseId = item.caseId;
            this.currentEpicOrderNumber = item.epicOrderNumber;
            this.currentPatientName = item.patientName;
            this.positionx = event.clientX;
            this.positiony = event.clientY;
            this.confirmSentToEpicDialogVisible = true;
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

