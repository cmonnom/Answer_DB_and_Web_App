Vue.component('variant-details', {
  props: {
    noEdit: { default: true, type: Boolean },
    variantDataTables: { default: [], type: Array },
    linkTable: { default: [], type: Array },
    widthClass: { default: "", type: String },
    currentVariant: { default: {}, type: Object }
  },
  template: ` <v-card>
  <v-toolbar dense dark color="primary">
      <v-menu offset-y offset-x class="ml-0">
          <v-btn slot="activator" flat icon dark>
              <v-icon color="amber accent-2">zoom_in</v-icon>
          </v-btn>
          <v-list>
              <v-list-tile v-if="!noEdit" avatar @click="saveVariant()" :disabled="noEdit">
                  <v-list-tile-avatar>
                      <v-icon>save</v-icon>
                  </v-list-tile-avatar>
                  <v-list-tile-content>
                      <v-list-tile-title>Save Variant Details</v-list-tile-title>
                  </v-list-tile-content>
              </v-list-tile>

              <v-list-tile v-if="!noEdit" avatar @click="revertVariant()">
                  <v-list-tile-avatar>
                      <v-icon>settings_backup_restore</v-icon>
                  </v-list-tile-avatar>
                  <v-list-tile-content>
                      <v-list-tile-title>Restore From Last Saved</v-list-tile-title>
                  </v-list-tile-content>
              </v-list-tile>

              <v-list-tile avatar @click="hidePanel()">
                  <v-list-tile-avatar>
                      <v-icon>cancel</v-icon>
                  </v-list-tile-avatar>
                  <v-list-tile-content>
                      <v-list-tile-title>Close Details</v-list-tile-title>
                  </v-list-tile-content>
              </v-list-tile>
          </v-list>
      </v-menu>
      <v-toolbar-title>
          Variant Details
      </v-toolbar-title>

      <v-spacer></v-spacer>
      <v-badge color="red" v-if="!noEdit" right bottom overlap v-model="variantDetailsUnSaved" class="mini-badge">
          <v-icon slot="badge"></v-icon>
          <v-tooltip bottom>
              <v-btn flat icon @click="saveVariant()" slot="activator" :loading="savingVariantDetails" :disabled="noEdit">
                  <v-icon>save</v-icon>
              </v-btn>
              <span>Save Variant Details</span>
          </v-tooltip>
      </v-badge>
      <v-tooltip bottom>
          <v-btn flat icon v-if="!noEdit" @click="revertVariant()" slot="activator">
              <v-icon>settings_backup_restore</v-icon>
          </v-btn>
          <span>Restore Last Saved Variant Details</span>
      </v-tooltip>
      <v-tooltip bottom>
          <v-btn icon @click="hidePanel()" slot="activator">
              <v-icon>close</v-icon>
          </v-btn>
          <span>Close Details</span>
      </v-tooltip>

  </v-toolbar>
  <v-container grid-list-md fluid>
      <v-layout row wrap>
          <v-flex :class="widthClass" v-for="table in variantDataTables" :key="table.name">
              <v-card flat>
                  <v-card-text>
                      <v-list class="dense-tiles">
                          <v-list-tile v-for="item in table.items" :key="item.label">
                              <v-list-tile-content class="pb-2">
                                  <v-layout class="full-width">
                                      <v-flex xs12 class="text-xs-left grow">
                                          <span v-if="isRegularVariantDetailsLabel(item.type)" class="selectable">{{ item.label }}:</span>
                                          <span v-if="!item.type" v-html="item.value" class="selectable text-xs-right grow blue-grey--text text--lighten-1"></span>
                                          <span v-if="item.type == 'chip'">
                                              <v-chip disabled class="selectable" v-for="chip in item.value" :key="chip">
                                                  {{ chip }}
                                              </v-chip>
                                          </span>
                                          <v-data-table v-if="item.type == 'callSet'" :items="item.value" hide-actions hide-headers>
                                              <template slot="items" slot-scope="props">
                                                  <td>
                                                      {{ props.item.label }}
                                                  </td>
                                                  <td v-for="i in item.columns" :key="i">{{ props.item["caller" + (i - 1)] }}</td>
                                              </template>
                                          </v-data-table>
                                          <v-layout v-if="item.type == 'select'" class="full-width">
                                              <v-flex xs2 class="selectable">{{ item.label }}:</v-flex>
                                              <v-flex xs6>
                                                  <v-select clearable :value="currentVariant[item.fieldName]" :items="item.items" v-model="currentVariant[item.fieldName]"
                                                      :label="item.tooltip" single-line hide-details
                                                      class="no-height-select" @input="variantDetailsUnSaved = true"
                                                      :disabled="noEdit"></v-select>
                                              </v-flex>
                                          </v-layout>

                                          <v-tooltip bottom v-for="(icon, index) in item.value" :key="index" v-if="item.type == 'flag'">
                                              <v-chip v-if="icon.chip" slot="activator" :color="icon.color" text-color="white" label small disabled>
                                                  {{ icon.iconName }}
                                              </v-chip>
                                              <v-icon v-if="!icon.chip" slot="activator" :color="icon.color">
                                                  {{ icon.iconName }}
                                              </v-icon>
                                              <span> {{ icon.tooltip }}</span>
                                          </v-tooltip>

                                      </v-flex>
                                  </v-layout>
                              </v-list-tile-content>
                          </v-list-tile>
                      </v-list>
                  </v-card-text>
              </v-card>
          </v-flex>
      </v-layout>
      <v-layout row wrap>
          <v-flex xs12 v-for="table in linkTable" :key="table.name">
              <v-card flat>
                  <v-card-text>
                      <v-list class="dense-tiles">
                          <v-list-tile v-for="item in table.items" :key="item.label">
                              <v-list-tile-content class="pb-2">
                                  <v-layout class="full-width">
                                      <v-flex xs12 class="text-xs-left grow">
                                          <span class="selectable">{{ item.label }}:</span>
                                          <v-tooltip v-if="item.links && id.value !== null" bottom v-for="(id, index) in item.ids" :key="index">
                                              <v-btn @click="handleIdLink(id)" slot="activator" v-html="id.label">
                                              </v-btn>
                                              <span>Open in new tab</span>
                                          </v-tooltip>
                                      </v-flex>
                                  </v-layout>
                              </v-list-tile-content>
                          </v-list-tile>
                      </v-list>
                  </v-card-text>
              </v-card>
          </v-flex>
      </v-layout>
  </v-container>
</v-card>`,
  data() {
    return {
      savingVariantDetails: false,
      variantDetailsUnSaved: false
    }

  },
  methods: {
    revertVariant() {
      this.$emit("revert-variant", this);
    },
    saveVariant() {
      this.$emit("save-variant", this);
    },
    showPanel() {
      this.$emit("show-panel", this);
    },
    hidePanel() {
      this.$emit("hide-panel", this);
    },
    togglePanel() {
      this.$emit("toggle-panel", this);
    },
    handleIdLink(id) {
      var link = "";
      if (id.type == "various") {
        if (id.value.indexOf('rs') == 0) {
          link = "https://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs=" + id.value;
        }
        else if (id.value.indexOf('COSM') == 0) {
          link = "https://cancer.sanger.ac.uk/cosmic/ncv/overview?id=" + id.value.replace("COSM", "");
        }
        else if (id.value.indexOf('COSN') == 0) {
          link = "https://cancer.sanger.ac.uk/cosmic/ncv/overview?id=" + id.value.replace("COSN", "");
        }
        else { //Clinvar
          link = "https://www.ncbi.nlm.nih.gov/clinvar/variation/" + id.value;
        }
      }
      else if (id.type == "oncoKB") {
        if (id.subtype == "gene") {
          link = "http://oncokb.org/#/gene/" + this.currentVariant.oncokbGeneName;
        }
        else if (id.subtype == "variant") {
          link = "http://oncokb.org/#/gene/" + this.currentVariant.oncokbGeneName + "/variant/" + this.currentVariant.oncokbVariantName;
        }
      }
      window.open(link, "_blank");
    },
    //determines if the regular variant details label should be used
    //like Gene, Notation Nb.Cases Seen etc.
    //so that it behaves like a regular "label: string" combo
    isRegularVariantDetailsLabel(type) {
      return !type || type == 'chip' || type == 'callSet' || type == 'flag';
    },
  },
  mounted: function () {
  },
  created: function () {
  },
  destroyed: function () {

  },
  watch: {
  }


});