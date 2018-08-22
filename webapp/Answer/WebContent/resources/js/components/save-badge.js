Vue.component('save-badge', {
  props: {
    "show-save-needed-badge": {default: false, type: Boolean},
    tooltip: {default: "", type: String}
  },
  template: `<v-tooltip right v-show="showSaveNeededBadge">
  <v-badge slot="activator" class="title-save-badge"
  color="red">
  <span slot="badge" ><v-icon>save</v-icon></span>
      </v-badge>
      <span v-html="tooltip"></span>
    </v-tooltip>`,
  data() {
    return {
     
      
    }

  },
  methods: {
  },
  mounted: function() {
   
  },
  created: function () {
  },
  destroyed: function () {

    
  },
  watch: {
  }


});