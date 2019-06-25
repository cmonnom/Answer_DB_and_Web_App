const UserPrefs = {
  template:
    `<div>

  <v-snackbar :timeout="2000" :bottom="true" :value="snackBarVisible">
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
            If you need to enable more permissions, contact an Answer administrator ({{ admins }}).
          </v-flex>
        </v-layout>
        <v-layout row wrap>
        <v-flex xs12>
          You belong to the following {{ groupSingPlural }}: 
          <v-chip disabled v-for="group in userGroups" :key="group.groupId">{{ group.name }}</v-chip>
        </v-flex>
        </v-layout>
     </v-container>   
    </v-card-text>
  </v-card>

  <v-card v-if="userPrefs" class="mt-3">
    <v-card-text class="subheading pl-3 pt-3">
      <span pb-2>You can customize the following settings:<br/></span>
      <v-layout row wrap>
      <v-flex xs12 md8 lg5>
      <v-switch :label="'Show rewards after annotations: ' + (userPrefs.showGoodies ? 'Yes' : 'No')" v-model="userPrefs.showGoodies"
      @change="saveUserPrefs"></v-switch>
      </v-flex> 
      </v-layout>
    </v-card-text>
  </v-card>

  <v-card v-if="headerConfigs && headerConfigs.length > 0" class="mt-3">
  <v-card-text class="subheading pl-3 pt-3">
    <span pb-3>You customized the following table headers:<br/></span>
    <v-layout row wrap pt-1>
    <v-flex xs12 pb-3 v-for="(config, index) in headerConfigs" :key="index">
    <v-tooltip bottom>
    <v-btn slot="activator" @click="deleteHeaderConfig(config)" icon flat>
    <v-icon color="primary">delete</v-icon>
    </v-btn>
    <span>Delete this header preference (resets to the normal header)</span>
    </v-tooltip>
      <span>{{ config.tableTitle }}:</span>
      <v-chip label v-for="header in config.headerOrders" 
      :key="header.value + config.tableTitle" color="primary" text-color="white"
      disabled>
        <span class="pr-2"> {{ header.text }} </span>
          <v-icon :color="header.hidden ? 'white' : 'amber accent-2'">visibility</v-icon>
      </v-chip>
    </v-flex>
    </v-layout>
  </v-card-text>
</v-card>

</div>`,
  data() {
    return {
      snackBarVisible: false,
      snackBarMessage: "",
      userPrefs: null,
      permissions: permissions, //needed to use the global variable in header.jsp
      admins: "",
      headerConfigs: null,
      userGroups: []
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
    getHeaderPrefs() {
      axios.get("./getHeaderPrefs", {
        params: {
        }
      })
        .then(response => {
          if (response.data.isAllowed) {
            this.headerConfigs = response.data.summaries;
          }
          else {
            this.handleDialogs(response.data, this.getHeaderPrefs);
          }
        })
        .catch(error => {
          alert(error);
        });
    },
    getUserGroups() {
      axios.get("./getUserGroups", {
        params: {
        }
      })
        .then(response => {
          if (response.data.isAllowed) {
            this.userGroups = response.data.payload;
          }
          else {
            this.handleDialogs(response.data, this.getUserGroups);
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
    },

    deleteHeaderConfig(headerConfig) {
      if (this.notLoadedYet) {
        return;
      }
      this.snackBarMessage = 'Deleted successfully';
      axios.get("./deleteHeaderConfig", {
        params: {
          tableTitle: headerConfig.tableTitle
        }
      })
        .then(response => {
          if (response.data.isAllowed && response.data.success) {
            this.getHeaderPrefs();
            this.snackBarVisible = true;
          }
          else {
            this.handleDialogs(response.data, this.getHeaderPrefs);
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
    this.getHeaderPrefs();
    this.getUserGroups();
  },
  destroyed: function () {
  },
  created: function () {
  },
  computed: {
    groupSingPlural() {
      var groupLabel = "group";
      if (this.userGroups && this.userGroups.length > 1) {
        groupLabel += "s";
      }
      return groupLabel;
    }
  },
  watch: {
  }
};

