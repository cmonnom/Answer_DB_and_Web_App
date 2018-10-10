Vue.component('save-badge', {
  props: {
    "show-save-needed-badge": {default: false, type: Boolean},
    tooltip: {default: "", type: String}
  },
  template: `
  <v-menu slot="activator" offset-x open-on-hover left open-delay="500" v-show="showSaveNeededBadge">
    <v-badge slot="activator" class="title-save-badge"
    color="red">
    <span slot="badge" ><v-icon>save</v-icon></span>
    </v-badge>
    <v-card>
    <v-card-text class="pl-2 pt-2 pr-2 pb-2">
        <div v-html="tooltip"></div>
          <v-btn color="primary" @click='saveAll()'>Save All</v-btn>
    </v-card-text>
    </v-card>
  </v-menu>
    `,
    
  data() {
    return {
     
      
    }

  },
  methods: {
    saveAll() {
      this.$emit("save-all");
    }
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