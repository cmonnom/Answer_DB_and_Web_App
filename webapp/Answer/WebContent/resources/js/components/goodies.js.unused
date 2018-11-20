Vue.component('goodies', {
  props: {
  },
  template: `  <v-slide-x-transition>
  <div v-if="showGoodiesPanel" class="goodies" :style="goodiesPosition">
      <img :src="getGoodieUrl()" alt="Goodies" :width="getGoodieWidth()" />
  </div>
  </v-slide-x-transition>`,
  data() {
    return {
      showGoodiesPanel: false,
      currentGoodie: {},
      goodiesImgs: [
        {
          name: 'fireworks.gif',
          width: 300,
          timeout: 3000
        },
        {
        name: 'nyancat.gif',
        width: 175,
        timeout: 3000
      },
      {
        name: 'minion-02.gif',
        width: 125,
        timeout: 2000
      },
      {
        name: 'mario.gif',
        width: 125,
        timeout: 3000
      }
        ,
      {
         name: 'stitch.gif',
      width: 150,
         timeout: 3000
      },
      {
        name: 'little_pony-small.gif',
        width: 150,
        timeout: 3000
      },
      {
        name: 'pusheen.gif',
        width: 200,
        timeout: 3000
      },
      {
        name: 'birds-2.gif',
        width: 300,
        timeout: 3000
      },
      {
        name: 'dna-preloader.gif',
        width: 200,
        timeout: 3000
      },
      {
        name: 'starwars.gif',
        width: 350,
        timeout: 3000
      },
      {
        name: 'molang.gif',
        width: 150,
        timeout: 3000
      }
      ],
      testItem: 0
    }

  },
  methods: {
    showGoodiesTest() {
      var currentTimeout = 0;
      for (var i= 0; i < this.goodiesImgs.length; i++) {
        this.currentGoodie = this.getGoodiesImg(i);
        var fadeIn = currentTimeout;
        var fadeOut = fadeIn + this.currentGoodie.timeout;
        currentTimeout = 500 + fadeOut;
        console.log(fadeIn, fadeOut);
        setTimeout(() => {
          this.currentGoodie = this.getGoodiesImg(this.testItem);
          this.goodiesPosition = this.generateGoodiesPosition();
          this.showGoodiesPanel = true;

          console.log("launching " + this.currentGoodie.name + " " + this.goodiesPosition + "height: " + window.innerHeight + " width:" + window.innerWidth);
        }, fadeIn);
        setTimeout(() => {
          this.showGoodiesPanel = false;
          this.testItem++;
          if (this.testItem >= this.goodiesImgs.length) {
            this.testItem = 0;
          }
        }, fadeOut);

      }
    },
    showGoodies() {
      this.currentGoodie = this.getGoodiesImg();
      this.goodiesPosition = this.generateGoodiesPosition();
      this.showGoodiesPanel = true;
      console.log("launching " + this.currentGoodie.name + " " + this.goodiesPosition + "height: " + window.innerHeight + " width:" + window.innerWidth);
      setTimeout(() => {
        this.showGoodiesPanel = false;
      }, this.currentGoodie.timeout);
    },
    getGoodiesImg(index) {
      if (index == null) {
        var index = Math.floor(Math.random() * (this.goodiesImgs.length));
      }
      return this.goodiesImgs[index];
    },
    generateGoodiesPosition() {
      var left = Math.floor(Math.random() * (window.innerWidth - this.currentGoodie.width));
      var top = Math.floor(Math.random() * (window.innerHeight - this.currentGoodie.width));
      return "left:" + left + "px; top:" + top + "px;";
    },
    getGoodieUrl() {
      return webAppRoot + "/resources/images/goodies/" + this.currentGoodie.name;
    },
    getGoodieWidth() {
      return this.currentGoodie ? this.currentGoodie.width + 'px' : '150px';
    }
  },
  mounted: function () {

  },
  created: function () {
    bus.$on('show-goodies', () => {
      this.showGoodies();
    });
    bus.$on('show-goodies-test', () => {
      this.showGoodiesTest();
    });
  },
  destroyed: function () {
    bus.$off('show-goodies');
    bus.$off('show-goodies-test');
  },
  watch: {
  }


});