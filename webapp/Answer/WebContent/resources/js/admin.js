const Admin = {
  template:
    /*html*/`<div>

  <v-snackbar :timeout="4000" :bottom="true" :value="snackBarVisible">
    {{ snackBarMessage }}
    <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
  </v-snackbar>

  <!-- edit user dialog -->
  <v-dialog v-model="editUserDialogVisible" fullscreen transition="dialog-bottom-transition" :overlay="false" scrollable>
    <v-card class="soft-grey-background">
      <v-toolbar dense dark color="primary">
        <v-toolbar-title class="white--text">
          {{ editAdd }} User {{ currentEditUserFullName }}
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip bottom>
          <v-btn icon @click="cancelEdits()" slot="activator">
            <v-icon>close</v-icon>
          </v-btn>
          <span>Cancel</span>
        </v-tooltip>
      </v-toolbar>
      <v-card-text :style="getDialogMaxHeight()">
        <v-container grid-list-md fluid class="pt-2">
          <v-layout row wrap>
            <v-flex xs5 md4 lg3 xl2>
              <v-card class="pl-2 pr-2">
                <v-card-title>
                  <div class="title">Identification</div>
                </v-card-title>
                <v-card-text>
                  <v-text-field v-model="editUsername" label="User ID"></v-text-field>
                  <v-text-field v-model="editFirstName" label="First Name"></v-text-field>
                  <v-text-field v-model="editLastName" label="Last Name"></v-text-field>
                  <v-text-field v-model="editEmail" label="Email"></v-text-field>
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs7 md4 lg3 xl2>
              <v-card class="pl-2 pr-2">
                <v-card-title>
                  <div class="title">Permissions</div>
                </v-card-title>
                <v-card-text>
                  <v-switch :label="'Can View: ' + (editView ? 'Yes' : 'No')" v-model="editView"></v-switch>
                  <v-switch :label="'Can Annotate: ' + (editAnnotate ? 'Yes' : 'No')" v-model="editAnnotate"></v-switch>
                  <v-switch :label="'Can Select Variants: ' + (editSelect ? 'Yes' : 'No')" v-model="editSelect"></v-switch>
                  <v-switch :label="'Can Assign Cases: ' + (editAssign ? 'Yes' : 'No')" v-model="editAssign"></v-switch>
                  <v-switch :label="'Can Review Cases: ' + (editReview ? 'Yes' : 'No')" v-model="editReview"></v-switch>
                  <v-switch :label="'Can Hide Annotations: ' + (editHide ? 'Yes' : 'No')" v-model="editHide"></v-switch>
                  <v-switch :label="'Receive All Notifications: ' + (editNotification ? 'Yes' : 'No')" v-model="editNotification"></v-switch>
                  <v-switch :label="'Is Admin: ' + (editAdmin ? 'Yes' : 'No')" v-model="editAdmin"></v-switch>
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs7 md4 lg3 xl2>
              <v-card class="pl-2 pr-2">
                <v-card-title>
                  <div class="title">Groups</div>
                </v-card-title>
                <v-card-text>
                <v-select clearable chips :value="currentEditGroupsInUser" :items="groupsSelectItems" v-model="currentEditGroupsInUser" item-text="name" item-value="value"
                label="Select Groups" multiple ></v-select>
                </v-card-text>
              </v-card>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-btn class="mr-2" color="success" @click="saveEdits()" :disabled="editUserDialogSaveDisabled">Save
          <v-icon right dark>save</v-icon>
        </v-btn>
        <v-btn class="mr-2" color="error" @click="cancelEdits()">Cancel
          <v-icon right dark>cancel</v-icon>
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- edit group dialog -->
  <v-dialog v-model="editGroupDialogVisible" fullscreen transition="dialog-bottom-transition" :overlay="false" scrollable>
    <v-card class="soft-grey-background">
      <v-toolbar dense dark color="primary">
        <v-toolbar-title class="white--text">
          {{ editAdd }} Group: {{ currentEditGroupName }}
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip bottom>
          <v-btn icon @click="cancelGroupEdits()" slot="activator">
            <v-icon>close</v-icon>
          </v-btn>
          <span>Cancel</span>
        </v-tooltip>
      </v-toolbar>
      <v-card-text :style="getDialogMaxHeight()">
        <v-container grid-list-md fluid class="pt-2">
          <v-layout row wrap>
            <v-flex xs5 md4 lg3 xl2>
              <v-card class="pl-2 pr-2">
                <v-card-title>
                  <div class="title">Name:</div>
                </v-card-title>
                <v-card-text>
                  <v-text-field v-model="editGroupName" label="Group Name"></v-text-field>
                  <v-text-field v-model="editDescription" label="Description"></v-text-field>
                  <v-select clearable chips :value="currentEditUsersInGroup" :items="usersSelectItems" v-model="currentEditUsersInGroup" item-text="name" item-value="value"
                label="Select Users" multiple ></v-select>
                </v-card-text>
              </v-card>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-btn class="mr-2" color="success" @click="saveGroupEdits()" :disabled="editGroupDialogSaveDisabled">Save
          <v-icon right dark>save</v-icon>
        </v-btn>
        <v-btn class="mr-2" color="error" @click="cancelGroupEdits()">Cancel
          <v-icon right dark>cancel</v-icon>
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <v-toolbar dense dark color="primary" fixed app>
    <v-toolbar-title class="white--text">
      Manage Application <span v-text="getAppVersion()"></span>
    </v-toolbar-title>
    <v-spacer></v-spacer>
  </v-toolbar>

  <data-table ref="userTable" :fixed="false" :fetch-on-created="true" table-title="Users" :initial-sort="'fullName'" no-data-text="No Data"
    data-url="./getAllUsers" >
    <v-fade-transition slot="action1">
      <v-tooltip bottom>
        <v-btn flat icon @click="addUser" slot="activator">
          <v-icon dark>mdi-account-plus</v-icon>
        </v-btn>
        <span>Add New User</span>
      </v-tooltip>
    </v-fade-transition>
    <v-list-tile avatar @click="addUser" slot="action1MenuItem">
      <v-list-tile-avatar>
        <v-icon>mdi-account-plus</v-icon>
      </v-list-tile-avatar>
      <v-list-tile-content>
        <v-list-tile-title>Add New User</v-list-tile-title>
      </v-list-tile-content>
    </v-list-tile>
  </data-table>

  <data-table ref="groupTable" :fixed="false" :fetch-on-created="true" table-title="Groups" :initial-sort="'name'" no-data-text="No Data"
    data-url="./getAllGroups" >
    <v-fade-transition slot="action1">
      <v-tooltip bottom>
        <v-btn flat icon @click="addGroup" slot="activator">
          <v-icon dark>mdi-account-multiple-plus</v-icon>
        </v-btn>
        <span>Add New Group</span>
      </v-tooltip>
    </v-fade-transition>
    <v-list-tile avatar @click="addGroup" slot="action1MenuItem">
      <v-list-tile-avatar>
        <v-icon>mdi-account-multiple-plus</v-icon>
      </v-list-tile-avatar>
      <v-list-tile-content>
        <v-list-tile-title>Add New Group</v-list-tile-title>
      </v-list-tile-content>
    </v-list-tile>
  </data-table>

  <gene-sets-edit></gene-sets-edit>

</div>`,
  data() {
    return {
      editUserDialogVisible: false,
      editUserDialogSaveDisabled: false,
      currentEditUserFullName: "",
      currentEditUserId: null,
      editView: false,
      editAnnotate: false,
      editSelect: false,
      editAssign: false,
      editReview: false,
      editHide: false,
      editNotification: false,
      editAdmin: false,
      editAdd: "Add",
      snackBarVisible: false,
      snackBarMessage: "",
      editGeneSetDialogVisible: false,
      currentEditGeneSetGroupName: "",
      currentEditGeneSetReportGroupId: null,
      editGenes: "",
      saveGeneSetDisabled: false,
      deleteGeneSetDisabled: false,
      deleteGeneSetDialogVisible: false,
      editGroupDialogVisible: false,
      editGroupDialogSaveDisabled: false,
      currentEditGroupName: "",
      currentEditGroupId: null,
      groupsSelectItems: [],
      usersSelectItems: [],
      currentEditUsersInGroup: [],
      currentEditGroupsInUser: [],
      editUsername: "",
      editFirstName: "",
      editLastName: "",
      editEmail: "",
      currentEditUserFullName: "",
      editGroupName: "",
      editDescription: "",
    }
  },
  methods: {
    editUser(userId) {
      this.editAdd = "Edit";
      var user = this.$refs.userTable.items.filter(item => item.userId == userId)[0];
      this.currentEditUserId = user.userId;
      this.editFirstName = user.firstName;
      this.editLastName = user.lastName;
      this.editUsername = user.userName;
      this.editEmail = user.email;
      this.currentEditUserFullName = user.fullName;
      this.editView = user.viewValue.pass;
      this.editAnnotate = user.annotateValue.pass;
      this.editSelect = user.selectValue.pass;
      this.editAssign = user.assignValue.pass;
      this.editReview = user.reviewValue.pass;
      this.editHide = user.hideValue.pass;
      this.editNotification = user.notificationValue.pass;
      this.editAdmin = user.adminValue.pass;
      this.currentEditGroupsInUser = user.groupIds;
      this.editUserDialogSaveDisabled = false;
      this.editUserDialogVisible = true;
    },
    editGroup(groupId) {
      this.editAdd = "Edit";
      var group = this.$refs.groupTable.items.filter(item => item.groupId == groupId)[0];
      this.currentEditGroupId = group.groupId;
      this.editGroupName = group.name;
      this.editDescription = group.description;
      this.currentEditUsersInGroup = group.userIds;
      this.editGroupDialogVisible = true;
    },
    blockUser(userId) {
      console.log("blocking user " + userId);
      this.editView = false;
      this.editAnnotate = false;
      this.editSelect = false;
      this.editAssign = false;
      this.editReview = false;
      this.editHide = false;
      this.editNotification = false;
      this.editAdmin = false;
      var user = this.$refs.userTable.items.filter(item => item.userId == userId)[0];
      this.currentEditUserId = userId;
      this.editFirstName = user.firstName;
      this.editLastName = user.lastName;
      this.editUsername = user.userName;
      this.editEmail = user.email;
      this.saveEdits();
    },
    saveEdits() {
      this.editUserDialogSaveDisabled = true;
      this.snackBarMessage = this.currentEditUserId ? 'User saved successfully' : 'User Added successfully';
      axios({
        method: "post",
        url: "./saveUser", 
        params: {
          userId: this.currentEditUserId,
          groups: this.currentEditGroupsInUser.join(",")
        },
        data: {
          username: this.editUsername,
          first: this.editFirstName,
          last: this.editLastName,
          email: this.editEmail,
          individualPermission: {
            canView: this.editView,
            canSelect: this.editSelect,
            canAnnotate: this.editAnnotate,
            canAssign: this.editAssign,
            canReview: this.editReview,
            canHide: this.editHide,
            receiveAllNotifications: this.editNotification,
            admin: this.editAdmin,
          }
        }
      })
        .then(response => {
          if (response.data.isAllowed && response.data.success) {
            this.$refs.userTable.getAjaxData();
            this.$refs.groupTable.getAjaxData();
            this.cancelEdits();
            this.snackBarVisible = true;
          }
          else {
            this.handleDialogs(response.data, this.saveEdits);
          }
          this.editUserDialogSaveDisabled = false;
        })
        .catch(error => {
          alert(error);
          this.editUserDialogSaveDisabled = false;
        });
    },
    cancelEdits() {
      this.editUserDialogVisible = false;
    },
    saveGroupEdits() {
      this.editGroupDialogVisible = false;
      this.snackBarMessage = this.currentEditGroupId ? 'Group saved successfully' : 'Group Added successfully';
      axios({
        method: "post",
        url: "./saveGroup", 
        params: {
          groupId: this.currentEditGroupId,
          name: this.editGroupName,
          description: this.editDescription,
          users: this.currentEditUsersInGroup.join(",")
        }
      })
        .then(response => {
          if (response.data.isAllowed && response.data.success) {
            this.$refs.groupTable.getAjaxData();
            this.$refs.userTable.getAjaxData();
            this.cancelGroupEdits();
            this.snackBarVisible = true;
          }
          else {
            this.handleDialogs(response.data, this.saveGroupEdits);
          }
          this.editGroupDialogSaveDisabled = false;
        })
        .catch(error => {
          alert(error);
        });
    },
    cancelGroupEdits() {
      this.editGroupDialogVisible = false;
    },
    addUser() {
      this.editAdd = "Add";
      this.currentEditUserId = null;
      this.editFirstName = "";
      this.editLastName = "";
      this.editUsername = "";
      this.editEmail = "";
      this.currentEditUserFullName = "";
      
      this.editView = false;
      this.editSelect = false;
      this.editAnnotate = false;
      this.editAssign = false;
      this.editReview = false;
      this.editHide = false;
      this.editNotification = false;
      this.editAdmin = false;
      this.editUserDialogVisible = true;
    },
    addGroup() {
      this.editAdd = "Add";
      this.currentEditGroupId = null;
      this.editGroupName = "";
      this.editDescription = "";
      this.currentEditUsersInGroup = [];
      this.editGroupDialogVisible = true;

    },
    getDialogMaxHeight() {
      var height = window.innerHeight - 120;
      return "min-height:" + height + "px;max-height:" + height + "px; overflow-y: auto";
    },
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
    getAllUsersForGroups() {
      axios.get("./getAllUsersForGroups", {
        params: {
        }
      })
        .then(response => {
          if (response.data.isAllowed) {
            this.usersSelectItems = response.data.items;
          }
          else {
            this.handleDialogs(response.data, this.getAllUsersForGroups);
          }
        })
        .catch(error => {
          alert(error);
        });
    },
    getAllGroupsForUsers() {
      axios.get("./getAllGroupsForUsers", {
        params: {
        }
      })
        .then(response => {
          if (response.data.isAllowed) {
            this.groupsSelectItems = response.data.items;
          }
          else {
            this.handleDialogs(response.data, this.getAllGroupsForUsers);
          }
        })
        .catch(error => {
          alert(error);
        });
    },
    getAppVersion() {
      return "(version: " + version + ")";
    }
  },
  mounted: function () {
    this.getAllUsersForGroups();
    this.getAllGroupsForUsers();
  },
  destroyed: function () {
    bus.$off('editUser');
    bus.$off('editGroup');
    bus.$off('blockUser');

  },
  created: function () {
    bus.$on('editUser', (item) => {
      this.editUser(item.userId);
    });
    bus.$on('editGroup', (item) => {
      this.editGroup(item.groupId);
    });
    bus.$on('blockUser', (item) => {
      this.blockUser(item.userId);
    });

  },
  computed: {
  },
  watch: {
  }
};

