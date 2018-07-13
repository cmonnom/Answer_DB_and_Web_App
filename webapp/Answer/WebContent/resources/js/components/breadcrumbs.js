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
  },
  mounted: function() {
    bus.$on('add-breadcrumb-level', (level) => {
        this.breadcrumbs.push(level);
    });
    bus.$on('remove-breadcrumb-level', () => {
        this.breadcrumbs.pop();
        bus.$emit("breadcrumb-level-down", this.breadcrumbs[this.breadcrumbs.length -1].closingFunction);
    });
  },
  created: function () {
  },
  destroyed: function () {
    bus.$off('add-breadcrumb-level');
    bus.$off('remove-breadcrumb-level');
    
  },
  watch: {
  }


});