const Admin = {
    template:
        `<div>

    <v-snackbar :timeout="4000" :bottom="true" v-model="snackBarVisible">
        {{ snackBarMessage }}
        <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
    </v-snackbar>

  <!-- edit user dialog -->
  <v-dialog v-model="editUserDialogVisible" fullscreen transition="dialog-bottom-transition" :overlay="false" scrollable>
      <v-toolbar dark color="primary" fixed>
          <v-toolbar-title class="white--text">
              {{ editAdd }} User: {{ currentEditUserFullName }}
          </v-toolbar-title>
          <v-spacer></v-spacer>
        </v-toolbar>
    <v-card>
      <v-card-text :style="getDialogMaxHeight()" class="pt-6">
        <v-container grid-list-md fluid class="pt-2">
        <v-layout row wrap>
          <v-flex xs3>
            <div class="title">Name:</div>
            <v-text-field ref="editUsername" label="User ID"></v-text-field>
            <v-text-field ref="editFirstName" label="First Name"></v-text-field>
            <v-text-field ref="editLastName" label="Last Name"></v-text-field>
          </v-flex>
        </v-layout>
        <v-layout row wrap>
          <v-flex xs4>
            <div class="title pb-4">Permissions:</div>
            <v-switch :label="'Can View: ' + (editView ? 'Yes' : 'No')" v-model="editView"></v-switch>
            <v-switch :label="'Can Edit: ' + (editEdit ? 'Yes' : 'No')" v-model="editEdit"></v-switch>
            <v-switch :label="'Can Finalize: ' + (editFinalize ? 'Yes' : 'No')" v-model="editFinalize"></v-switch>
            <v-switch :label="'Is Admin: ' + (editAdmin ? 'Yes' : 'No')" v-model="editAdmin"></v-switch>
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

  <v-toolbar dark color="primary" fixed app>
    <v-toolbar-title class="white--text">
      Manage Application
    </v-toolbar-title>
    <v-spacer></v-spacer>
  </v-toolbar>

  <data-table ref="userTable" :fixed="false" :fetch-on-created="true" table-title="Users" :initial-sort="'fullName'" no-data-text="No Data"
    data-url="./getAllUsers">
    <div slot="action1">
      <v-tooltip bottom>
        <v-btn flat icon @click="addUser" slot="activator">
          <v-icon dark>supervisor_account</v-icon>
        </v-btn>
        <span>Add New User</span>
      </v-tooltip>
    </div>
  </data-table>

</div>`,
    data() {
        return {
            editUserDialogVisible: false,
            currentEditUserFullName: "",
            currentEditUserId: null,
            editView: false,
            editEdit: false,
            editFinalize: false,
            editAdmin: false,
            editAdd: "Add",
            snackBarVisible: false,
            snackBarMessage: ""

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
            this.currentEditUserFullName = user.fullName;
            this.editView = user.viewValue.pass;
            this.editEdit = user.editValue.pass;
            this.editFinalize = user.finalizeValue.pass;
            this.editAdmin = user.adminValue.pass;
            this.editUserDialogVisible = true;
            
        },
        blockUser(userId) {
            console.log("blocking user " + userId );
            this.editView = false;
            this.editEdit = false;
            this.editFinalize = false;
            this.editAdmin = false;
            var user = this.$refs.userTable.items.filter(item => item.userId == userId)[0];
            this.currentEditUserId = userId;
            this.$refs.editFirstName.inputValue = user.firstName;
            this.$refs.editLastName.inputValue = user.lastName;
            this.$refs.editUsername.inputValue = user.userName;
            this.saveEdits();
        },
        saveEdits() {
            //TODO
            console.log("saving edits");
            this.editUserDialogVisible = false;
            this.snackBarMessage = this.currentEditUserId ? 'User saved successfully' : 'User Added successfully';
            axios.get("./saveUser", {
                params: {
                    userId: this.currentEditUserId,
                    username: this.$refs.editUsername.inputValue,
                    first: this.$refs.editFirstName.inputValue,
                    last: this.$refs.editLastName.inputValue,
                    view: this.editView,
                    edit: this.editEdit,
                    finalize: this.editFinalize,
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
            this.currentEditUserFullName = "";
            this.editView = false;
            this.editEdit = false;
            this.editFinalize = false;
            this.editAdmin = false;
            this.editUserDialogVisible = true;
            
        },
        getDialogMaxHeight() {
            return "max-height:" + window.innerHeight - 100 + "px; overflow-y: auto";
        },
        handleAdminChanged() {
            if (this.editAdmin) {
                this.editView = true;
                this.editEdit = true;
                this.editFinalize = true;
            }
        },
        handleFinalizeChanged() {
            if (this.editFinalize) {
                this.editView = true;
                this.editEdit = true;
            }
            else {
                this.editAdmin = false;
            }
        },
        handleEditChanged() {
            if (this.editEdit) {
                this.editView = true;
            }
            else {
                this.editFinalize = false;
                this.editAdmin = false;
            }
        },
        handleViewChanged() {
            if (!this.editView) {
                this.editEdit = false;
                this.editFinalize = false;
                this.editAdmin = false;
            }
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

    },
    mounted: function () {
    },
    destroyed: function () {
        bus.$off('editUser');
        bus.$off('blockUser');
    },
    created: function () {
        bus.$on('editUser', (item) => {
            this.editUser(item.userId);
        });
        bus.$on('blockUser', (item) => {
            this.blockUser(item.userId);
        });
    },
    computed: {
    },
    watch: {
        editAdmin: 'handleAdminChanged',
        editFinalize: 'handleFinalizeChanged',
        editEdit: 'handleEditChanged',
        editView: 'handleViewChanged'
    }
};

