Vue.component('breadcrumbs', {
  props: {
  },
  template: `<v-breadcrumbs>
  <v-icon slot="divider">chevron_right</v-icon>
  <v-breadcrumbs-item v-for="(item, index) in breadcrumbs" :key="item.text" :disabled="item.disabled">
      <span :class="index == breadcrumbs.length -1 ? 'subheading' :''">{{ item.text }}</span>
  </v-breadcrumbs-item>
</v-breadcrumbs>`,
  data() {
    return {
      breadcrumbs: [{text: "You are here:  Case", disabled: true, closingFunction: null}]
      
    }

  },
  methods: {
    addLevelHandler(level) {
      this.breadcrumbs.push(level);
    },
    removeLevelHandler() {
      this.breadcrumbs.pop();
      // bus.$emit("breadcrumb-level-down", this.breadcrumbs[this.breadcrumbs.length -1].closingFunction);
    }
  },
  mounted: function() {
    // bus.$on('add-breadcrumb-level',this.addLevelHandler);
    // bus.$on('remove-breadcrumb-level', this.removeLevelHandler);
  },
  created: function () {
  },
  beforeDestroy() {
    // bus.$off('add-breadcrumb-level',this.addLevelHandler);
    // bus.$off('remove-breadcrumb-level', this.removeLevelHandler);
  },
  destroyed: function () {
    // bus.$off('add-breadcrumb-level');
    // bus.$off('remove-breadcrumb-level');
    
  },
  watch: {
  }


});