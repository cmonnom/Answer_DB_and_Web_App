const Admin = {
  template:
    `<div>

  <v-snackbar :timeout="4000" :bottom="true" v-model="snackBarVisible">
    {{ snackBarMessage }}
    <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
  </v-snackbar>

  <!-- edit user dialog -->
  <v-dialog v-model="editUserDialogVisible" fullscreen transition="dialog-bottom-transition" :overlay="false" scrollable>
    <v-card class="soft-grey-background">
      <v-toolbar dense dark color="primary">
        <v-toolbar-title class="white--text">
          {{ editAdd }} User: {{ currentEditUserFullName }}
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
            <v-flex xs3>
              <v-card class="pl-2 pr-2">
                <v-card-title>
                  <div class="title">Name:</div>
                </v-card-title>
                <v-card-text>
                  <v-text-field ref="editUsername" label="User ID"></v-text-field>
                  <v-text-field ref="editFirstName" label="First Name"></v-text-field>
                  <v-text-field ref="editLastName" label="Last Name"></v-text-field>
                  <v-text-field ref="editEmail" label="Email"></v-text-field>
                  <!-- <div class="grey--text lighten-1">Email:</div><div class="pb-4">{{ userEmail }}</div> -->
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs4>
              <v-card class="pl-2 pr-2">
                <v-card-title>
                  <div class="title">Permissions:</div>
                </v-card-title>
                <v-card-text>
                  <v-switch :label="'Can View: ' + (editView ? 'Yes' : 'No')" v-model="editView"></v-switch>
                  <v-switch :label="'Can Annotate: ' + (editAnnotate ? 'Yes' : 'No')" v-model="editAnnotate"></v-switch>
                  <v-switch :label="'Can Select Variants: ' + (editSelect ? 'Yes' : 'No')" v-model="editSelect"></v-switch>
                  <v-switch :label="'Can Assign Cases: ' + (editAssign ? 'Yes' : 'No')" v-model="editAssign"></v-switch>
                  <v-switch :label="'Is Admin: ' + (editAdmin ? 'Yes' : 'No')" v-model="editAdmin"></v-switch>
                </v-card-text>
              </v-card>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-btn color="success" @click="saveEdits()">Save
          <v-icon right dark>save</v-icon>
        </v-btn>
        <v-btn color="error" @click="cancelEdits()">Cancel
          <v-icon right dark>cancel</v-icon>
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- edit gene set dialog -->
  <v-dialog v-model="editGeneSetDialogVisible" fullscreen transition="dialog-bottom-transition" :overlay="false" scrollable>
    <v-card class="soft-grey-background">
      <v-toolbar dense dark color="primary">
        <v-toolbar-title class="white--text">
          {{ editAdd }} Gene Set: {{ currentEditGeneSetGroupName }}
        </v-toolbar-title>
        <v-spacer></v-spacer>
        <v-tooltip bottom>
          <v-btn icon @click="cancelEditsGeneSet()" slot="activator">
            <v-icon>close</v-icon>
          </v-btn>
          <span>Cancel</span>
        </v-tooltip>
      </v-toolbar>
      <v-card-text :style="getDialogMaxHeight()">
        <v-container grid-list-md fluid class="pt-2">
          <v-layout row wrap>
            <v-flex xs5>
              <v-card class="pl-2 pr-2">
                <v-card-title>
                  <div class="title">Gene Set Details:</div>
                </v-card-title>
                <v-card-text>
                  <v-text-field ref="editGroupName" label="Name"></v-text-field>
                  <v-text-field ref="editDescription" label="Description" multi-line></v-text-field>
                  <v-text-field ref="editReference" label="Link to More Info"></v-text-field>
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs7>
              <v-card class="pl-2 pr-2">
                <v-card-title>
                  <div class="title">Genes:</div>
                </v-card-title>
                <v-card-text>
                <v-text-field :textarea="true" v-model="editGenes" label="Add genes here (separated by space or comma)">

                </v-text-field>
                </v-card-text>
              </v-card>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-btn color="success" @click="saveEditsGeneSet()" :disabled="saveGeneSetDisabled">Save
          <v-icon right dark>save</v-icon>
        </v-btn>
        <v-btn color="error" @click="cancelEditsGeneSet()">Cancel
          <v-icon right dark>cancel</v-icon>
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <v-dialog v-model="deleteGeneSetDialogVisible" width="400px">
    <v-card class="soft-grey-background">
      <v-card-title class="subheading">Are you sure?</v-card-title>
      <v-card-text class="pl-2 pr-2">
      You are about to delete <b>{{ currentEditGeneSetGroupName }}</b>.<br/>
      Click Delete to permanently delete this gene set.<br/>
      Click Cancel to keep the panel.<br/>
      </v-card-text>
      <v-card-actions>
        <v-btn color="warning" @click="deleteReportGroup()" :disabled="deleteGeneSetDisabled">Delete
          <v-icon right dark>delete</v-icon>
        </v-btn>
        <v-btn color="error" @click="deleteGeneSetDialogVisible = false">Cancel
          <v-icon right dark>cancel</v-icon>
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <v-toolbar dense dark color="primary" fixed app>
    <v-toolbar-title class="white--text">
      Manage Application
    </v-toolbar-title>
    <v-spacer></v-spacer>
  </v-toolbar>

  <data-table ref="userTable" :fixed="false" :fetch-on-created="true" table-title="Users" :initial-sort="'fullName'" no-data-text="No Data"
    data-url="./getAllUsers" >
    <v-fade-transition slot="action1">
      <v-tooltip bottom>
        <v-btn flat icon @click="addUser" slot="activator">
          <v-icon dark>supervisor_account</v-icon>
        </v-btn>
        <span>Add New User</span>
      </v-tooltip>
    </v-fade-transition>
    <v-list-tile avatar @click="addUser" slot="action1MenuItem">
      <v-list-tile-avatar>
        <v-icon>supervisor_account</v-icon>
      </v-list-tile-avatar>
      <v-list-tile-content>
        <v-list-tile-title>Add New User</v-list-tile-title>
      </v-list-tile-content>
    </v-list-tile>
  </data-table>

  <!-- Create new Gene sets -->
  <data-table ref="geneSetTable" :fixed="false" :fetch-on-created="true" table-title="Gene Sets" :initial-sort="'groupName'" no-data-text="No Data"
  class="pt-3"
    data-url="./getAllReportGroups" >
    <v-fade-transition slot="action1">
      <v-tooltip bottom>
        <v-btn flat icon @click="createGeneSet" slot="activator">
          <v-icon dark>playlist_add</v-icon>
        </v-btn>
        <span>Create a New Gene Set</span>
      </v-tooltip>
    </v-fade-transition>
    <v-list-tile avatar @click="createGeneSet" slot="action1MenuItem">
      <v-list-tile-avatar>
        <v-icon>playlist_add</v-icon>
      </v-list-tile-avatar>
      <v-list-tile-content>
        <v-list-tile-title>Create a New Gene Set</v-list-tile-title>
      </v-list-tile-content>
    </v-list-tile>
  </data-table>

</div>`,
  data() {
    return {
      editUserDialogVisible: false,
      currentEditUserFullName: "",
      currentEditUserId: null,
      editView: false,
      editAnnotate: false,
      editSelect: false,
      editAssign: false,
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
      deleteGeneSetDialogVisible: false

    }
  },
  methods: {
    editUser(userId) {
      this.editAdd = "Edit";
      var user = this.$refs.userTable.items.filter(item => item.userId == userId)[0];
      this.currentEditUserId = user.userId;
      this.$refs.editFirstName.inputValue = user.firstName;
      this.$refs.editLastName.inputValue = user.lastName;
      this.$refs.editUsername.inputValue = user.userName;
      this.$refs.editEmail.inputValue = user.email;
      this.currentEditUserFullName = user.fullName;
      this.editView = user.viewValue.pass;
      this.editAnnotate = user.annotateValue.pass;
      this.editSelect = user.selectValue.pass;
      this.editAssign = user.assignValue.pass;
      this.editAdmin = user.adminValue.pass;
      this.editUserDialogVisible = true;
    },
    blockUser(userId) {
      console.log("blocking user " + userId);
      this.editView = false;
      this.editAnnotate = false;
      this.editSelect = false;
      this.editAssign = false;
      this.editAdmin = false;
      var user = this.$refs.userTable.items.filter(item => item.userId == userId)[0];
      this.currentEditUserId = userId;
      this.$refs.editFirstName.inputValue = user.firstName;
      this.$refs.editLastName.inputValue = user.lastName;
      this.$refs.editUsername.inputValue = user.userName;
      this.$refs.editEmail.inputValue = user.email;
      this.saveEdits();
    },
    saveEdits() {
      this.editUserDialogVisible = false;
      this.snackBarMessage = this.currentEditUserId ? 'User saved successfully' : 'User Added successfully';
      axios.get("./saveUser", {
        params: {
          userId: this.currentEditUserId,
          username: this.$refs.editUsername.inputValue,
          first: this.$refs.editFirstName.inputValue,
          last: this.$refs.editLastName.inputValue,
          email: this.$refs.editEmail.inputValue,
          canView: this.editView,
          canSelect: this.editSelect,
          canAnnotate: this.editAnnotate,
          canAssign: this.editAssign,
          admin: this.editAdmin
        }
      })
        .then(response => {
          if (response.data.isAllowed && response.data.success) {
            this.$refs.userTable.getAjaxData();
            this.snackBarVisible = true;
          }
          else {
            this.handleDialogs(response.data, this.saveEdits);
          }
        })
        .catch(error => {
          alert(error);
        });
    },
    cancelEdits() {
      this.editUserDialogVisible = false;
    },
    addUser() {
      this.editAdd = "Add";
      this.currentEditUserId = null;
      this.$refs.editFirstName.inputValue = "";
      this.$refs.editLastName.inputValue = "";
      this.$refs.editUsername.inputValue = "";
      this.$refs.editEmail.inputValue = "";
      this.currentEditUserFullName = "";
      this.editView = false;
      this.editSelect = false;
      this.editAnnotate = false;
      this.editAssign = false;
      this.editAdmin = false;
      this.editUserDialogVisible = true;

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

    createGeneSet() {
      this.editAdd = "Add";
      this.currentEditGeneSetReportGroupId = null;
      this.$refs.editGroupName.inputValue = "";
      this.$refs.editDescription.inputValue = "";
      this.$refs.editReference.inputValue = "";
      this.editGenes = "";
      this.editGeneSetDialogVisible = true;
    },
    editReportGroup(reportGroupId) {
      this.editAdd = "Edit";
      var reportGroup = this.$refs.geneSetTable.items.filter(item => item.reportGroupId == reportGroupId)[0];
      this.currentEditGeneSetReportGroupId = reportGroup.reportGroupId;
      this.currentEditGeneSetGroupName = reportGroup.groupName;
      this.$refs.editGroupName.inputValue = reportGroup.groupName;
      this.$refs.editDescription.inputValue = reportGroup.description;
      this.$refs.editReference.inputValue = reportGroup.referenceUrl;
      this.editGenes = reportGroup.genes;
      this.editGeneSetDialogVisible = true;
    },
    confirmDeleteReportGroup(reportGroupId) {
      var reportGroup = this.$refs.geneSetTable.items.filter(item => item.reportGroupId == reportGroupId)[0];
      this.currentEditGeneSetReportGroupId = reportGroup.reportGroupId;
      this.currentEditGeneSetGroupName = reportGroup.groupName;
      this.deleteGeneSetDialogVisible = true;
    },
    deleteReportGroup(reportGroupId) {
      this.snackBarMessage = 'Gene Set deleted successfully';
      this.deleteGeneSetDisabled = true;
      axios.get("./deleteReportGroup", {
        params: {
          reportGroupId: this.currentEditGeneSetReportGroupId
        }
      })
        .then(response => {
          if (response.data.isAllowed && response.data.success) {
            this.$refs.geneSetTable.getAjaxData();
            this.snackBarVisible = true;
            this.deleteGeneSetDialogVisible = false;
          }
          else {
            this.handleDialogs(response.data, this.saveEdits);
          }
          this.deleteGeneSetDisabled = false;
        })
        .catch(error => {
          alert(error);
          this.deleteGeneSetDisabled = false;
        });
    },
    cancelEditsGeneSet() {
      this.editGeneSetDialogVisible = false;
    },
    saveEditsGeneSet() {
      this.snackBarMessage = this.currentEditGeneSetReportGroupId ? 'Gene Set saved successfully' : 'Gene Set Added successfully';
      this.saveGeneSetDisabled = true;
      axios({
        method: 'post',
        url: "./saveReportGroup",
        params: {
          reportGroupId: this.currentEditGeneSetReportGroupId,
        },
        data: {
          groupName: this.$refs.editGroupName.inputValue,
          description: this.$refs.editDescription.inputValue,
          referenceUrl: this.$refs.editReference.inputValue,
          genes: this.editGenes
        }
      })
        .then(response => {
          if (response.data.isAllowed && response.data.success) {
            this.$refs.geneSetTable.getAjaxData();
            this.snackBarVisible = true;
            this.editGeneSetDialogVisible = false;
          }
          else {
            this.handleDialogs(response.data, this.saveEditsGeneSet);
          }
          this.saveGeneSetDisabled = false;
        })
        .catch(error => {
          alert(error);
          this.saveGeneSetDisabled = false;
        });
    }

  },
  mounted: function () {
  },
  destroyed: function () {
    bus.$off('editUser');
    bus.$off('blockUser');

    bus.$off('editReportGroup');
    bus.$off('deleteReportGroup');
  },
  created: function () {
    bus.$on('editUser', (item) => {
      this.editUser(item.userId);
    });
    bus.$on('blockUser', (item) => {
      this.blockUser(item.userId);
    });

    bus.$on('editReportGroup', (item) => {
      this.editReportGroup(item.reportGroupId);
    });
    bus.$on('deleteReportGroup', (item) => {
      this.confirmDeleteReportGroup(item.reportGroupId);
    });
  },
  computed: {
  },
  watch: {
  }
};

