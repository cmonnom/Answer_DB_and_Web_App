const UserPrefs = {
  template:
    `<div>

  <v-snackbar :timeout="2000" :bottom="true" v-model="snackBarVisible">
    {{ snackBarMessage }}
    <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
  </v-snackbar>

  <v-toolbar dense dark color="primary" fixed app>
    <v-toolbar-title class="white--text">
      User Preferences
    </v-toolbar-title>
    <v-spacer></v-spacer>
  </v-toolbar>

  <v-card>
    <v-card-text class="subheading">
    <v-container grid-list-md fluid>
        <v-layout row wrap>
          <v-flex xs12>
          In Answer, you have been granted the following permissions (with a check mark):
          </v-flex>
          <v-flex xs12>
          <v-icon color="green" v-if="permissions.canView">check_circle</v-icon>
          <v-icon color="red" v-if="!permissions.canView">cancel</v-icon>
            <b>View:</b> you can browse cases and reports
          </v-flex>
          <v-flex xs12>
          <v-icon color="green" v-if="permissions.canAnnotate">check_circle</v-icon>
          <v-icon color="red" v-if="!permissions.canAnnotate">cancel</v-icon>
          <b>Annotate:</b> you can edit cases and create annotation cards
          </v-flex>
          <v-flex xs12>
          <v-icon color="green" v-if="permissions.canSelect">check_circle</v-icon>
          <v-icon color="red" v-if="!permissions.canSelect">cancel</v-icon>
          <b>Select Variants:</b> decide which variants go into a report
          </v-flex>
          <v-flex xs12>
          <v-icon color="green" v-if="permissions.canAssign">check_circle</v-icon>
          <v-icon color="red" v-if="!permissions.canAssign">cancel</v-icon>
          <b>Assign Cases to Users:</b> assign cases to users
          </v-flex>
          <v-flex xs12>
          <v-icon color="green" v-if="permissions.canReview">check_circle</v-icon>
          <v-icon color="red" v-if="!permissions.canReview">cancel</v-icon>
          <b>Review:</b> a reviewer who can edit reports
          </v-flex>
          <v-flex xs12>
          <v-icon color="green" v-if="permissions.admin">check_circle</v-icon>
          <v-icon color="red" v-if="!permissions.admin">cancel</v-icon>
          <b>Admin:</b> admistrator and can do all of the above. Be careful.
          </v-flex>
          <v-flex xs12>
            In you need to enable more access, contact an Answer administrator ({{ admins }}).
          </v-flex>
        </v-layout>
     </v-container>   
    </v-card-text>
  </v-card>

  <v-card v-if="userPrefs" class="mt-3">
    <v-card-text class="subheading pl-3 pt-3">
      <span pb-2>You can customize the following settings:<br/></span>
      <v-switch :label="'Show rewards after annotations: ' + (userPrefs.showGoodies ? 'Yes' : 'No')" v-model="userPrefs.showGoodies"
      @change="saveUserPrefs"></v-switch>

    </v-card-text>
  </v-card>

</div>`,
  data() {
    return {
      snackBarVisible: false,
      snackBarMessage: "",
      userPrefs: null,
      permissions: permissions, //needed to use the global variable in header.jsp
      admins: ""
    }
  },
  methods: {
    getAdmins() {
      axios.get("./getAdmins", {
        params: {
        }
      })
        .then(response => {
          if (response.data.isAllowed && response.data.success) {
            this.admins = response.data.message;
          }
          else {
            this.handleDialogs(response.data, this.getAdmins);
          }
        })
        .catch(error => {
          alert(error);
        });
    },
    getUserPrefs() {
      axios.get("./getUserPrefs", {
        params: {
        }
      })
        .then(response => {
          if (response.data.isAllowed && response.data.success) {
            this.userPrefs = response.data;
          }
          else {
            this.handleDialogs(response.data, this.getUserPrefs);
          }
        })
        .catch(error => {
          alert(error);
        });
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

    saveUserPrefs() {
      if (this.notLoadedYet) {
        return;
      }
      this.snackBarMessage = 'Saved successfully';
      axios({
        method: 'post',
        url: "./saveUserPrefs",
        params: {
        },
        data: this.userPrefs
      })
        .then(response => {
          if (response.data.isAllowed && response.data.success) {
            this.getUserPrefs();
            this.snackBarVisible = true;
          }
          else {
            this.handleDialogs(response.data, this.saveUserPrefs);
          }
        })
        .catch(error => {
          alert(error);
        });
    }

  },
  mounted: function () {
    this.getAdmins();
    this.getUserPrefs();
  },
  destroyed: function () {
  },
  created: function () {
  },
  computed: {
  },
  watch: {
  }
};

