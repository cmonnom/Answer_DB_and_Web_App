Vue.component('gene-sets-edit', {
  template:
    `<div>

  <v-snackbar :timeout="4000" :bottom="true" :value="snackBarVisible">
    {{ snackBarMessage }}
    <v-btn flat color="primary" @click.native="snackBarVisible = false">Close</v-btn>
  </v-snackbar>

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
                  <v-text-field v-model="editGroupName" label="Name"></v-text-field>
                  <v-textarea v-model="editDescription" label="Description"></v-textarea>
                  <v-text-field v-model="editReference" label="Link to More Info"></v-text-field>
                </v-card-text>
              </v-card>
            </v-flex>
            <v-flex xs7>
              <v-card class="pl-2 pr-2">
                <v-card-title>
                  <div class="title">Genes:</div>
                </v-card-title>
                <v-card-text>
                <v-textarea v-model="editGenes" label="Add genes here (separated by space or comma)">

                </v-textarea>
                </v-card-text>
              </v-card>
            </v-flex>
          </v-layout>
        </v-container>
      </v-card-text>
      <v-card-actions>
        <v-btn class="mr-2" color="success" @click="saveEditsGeneSet()" :disabled="saveGeneSetDisabled">Save
          <v-icon right dark>save</v-icon>
        </v-btn>
        <v-btn class="mr-2" color="error" @click="cancelEditsGeneSet()">Cancel
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
        <v-btn class="mr-2" color="warning" @click="deleteReportGroup()" :disabled="deleteGeneSetDisabled">Delete
          <v-icon right dark>delete</v-icon>
        </v-btn>
        <v-btn class="mr-2" color="error" @click="deleteGeneSetDialogVisible = false">Cancel
          <v-icon right dark>cancel</v-icon>
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>


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
      editGroupName: "",
      editDescription: "",
      editReference: ""
    }
  },
  methods: {
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
      this.editGroupName = "";
      this.editDescription = "";
      this.editReference = "";
      this.editGenes = "";
      this.editGeneSetDialogVisible = true;
    },
    editReportGroup(reportGroupId) {
      this.editAdd = "Edit";
      var reportGroup = this.$refs.geneSetTable.items.filter(item => item.reportGroupId == reportGroupId)[0];
      this.currentEditGeneSetReportGroupId = reportGroup.reportGroupId;
      this.currentEditGeneSetGroupName = reportGroup.groupName;
      this.editGroupName = reportGroup.groupName;
      this.editDescription = reportGroup.description;
      this.editReference = reportGroup.referenceUrl;
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
          groupName: this.editGroupName,
          description: this.editDescription,
          referenceUrl: this.editReference,
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
    bus.$off('editReportGroup');
    bus.$off('deleteReportGroup');
  },
  created: function () {
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
});

