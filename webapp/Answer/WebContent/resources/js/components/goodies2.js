Vue.component('goodies2', {
  props: {
  },
  template: `
  <div class="goodies-screen" :id="containerId" :class="[opacityTransition ? 'fade-out' : '']">
  </div>
  `,
  data() {
    return {
      containerId: "goodies-panel",
      maxParticules: 280,
      colors: ['#69D2E7', '#A7DBD8', '#E0E4CC', '#F38630', '#FA6900', '#FF4E50', '#F9D423'],
      demo: {},
      choices: [{
        start: this.createParticules, //starts the animation
        end: this.clearParticules, //smooth out the ending (instead of direct destroy)
        endDelay: 1000 //how long before calling destroy
      },
      {
        start: this.createTentacles,
        end: this.clearTentacles,
        endDelay: 2000
      }],
      lastIndex: -1,
      opacityTransition: false,
    }

  },
  methods: {
    pickIndex() {
      return Math.floor(Math.random() * (this.choices.length));
    },
    //randomly select an effect here
    activateGoodies() {
      var index = this.pickIndex();
      while(index == this.lastIndex) {
        index = this.pickIndex();
      }
      this.lastIndex = index;
      var choice = this.choices[index];
      choice.start();
      if (choice.end) {
        setTimeout(choice.end, 10000);
      }
      setTimeout(this.endGoodies, 10000 + choice.endDelay);
    },
    clearParticules() {
      this.demo.mousemove = () => {
      };
    },
    clearTentacles() {
      this.opacityTransition = true;
    },
    endGoodies() {
      this.demo.destroy();
      this.$emit("end-goodies");
    },
    createParticules() {
      var particles = [];
      var pool = [];
      this.demo = Sketch.create({
        container: document.getElementById(this.containerId)
      });
      this.demo.setup = () => {

        // Set off some initial particles.
        var i, x, y;

        for (i = 0; i < 20; i++) {
          x = (this.demo.width * 0.5) + random(-100, 100);
          y = (this.demo.height * 0.5) + random(-100, 100);
          this.demo.spawn(x, y);
        }
      };

      this.demo.spawn = (x, y) => {

        if (particles.length >= this.maxParticules)
          pool.push(particles.shift());

        particle = pool.length ? pool.pop() : new Particle();
        particle.init(x, y, random(5, 40));

        particle.wander = random(0.5, 2.0);
        particle.color = random(this.colors);
        particle.drag = random(0.9, 0.99);

        theta = random(TWO_PI);
        force = random(2, 8);

        particle.vx = sin(theta) * force;
        particle.vy = cos(theta) * force;

        particles.push(particle);
      }

      this.demo.update = function () {

        var i, particle;

        for (i = particles.length - 1; i >= 0; i--) {

          particle = particles[i];

          if (particle.alive) particle.move();
          else pool.push(particles.splice(i, 1)[0]);
        }
      };

      this.demo.draw = () => {

        this.demo.globalCompositeOperation = 'lighter';

        for (var i = particles.length - 1; i >= 0; i--) {
          particles[i].draw(this.demo);
        }
      };

      this.demo.mousemove = () => {

        var particle, theta, force, touch, max, i, j, n;

        for (i = 0, n = this.demo.touches.length; i < n; i++) {

          touch = this.demo.touches[i], max = random(1, 4);
          for (j = 0; j < max; j++) this.demo.spawn(touch.x, touch.y);
        }
      };
    },
    createTentacles() {
      var ease = 0.1;
      var modified = false;
      var radius = tentacleSettings.headRadius;
      var tentacles = [];
      var center = { x: 0, y: 0 };
      var scale = window.devicePixelRatio || 1;
      this.demo = Sketch.create({
        retina: 'auto',
        container: document.getElementById(this.containerId),
        setup: function () {

          center.x = this.width / 2;
          center.y = this.height / 2;

          var tentacle;

          for (var i = 0; i < 100; i++) {

            tentacle = new Tentacle({
              length: random(10, 20),
              radius: random(0.05, 1.0),
              spacing: random(0.2, 1.0),
              friction: random(0.7, 0.88)
            });

            tentacle.move(center.x, center.y, true);
            tentacles.push(tentacle);
          }
        },

        update: function () {
          var t, cx, cy, pulse;
          t = this.millis * 0.001;
          if (tentacleSettings.pulse) {
            pulse = pow(sin(t * PI), 18);
            radius = tentacleSettings.headRadius * 0.5 + tentacleSettings.headRadius * 0.5 * pulse;
          }
          if (tentacleSettings.interactive) {
            ease += (0.7 - ease) * 0.05;
            center.x += (this.mouse.x / scale - center.x) * ease;
            center.y += (this.mouse.y / scale - center.y) * ease;
          } else {
            t = this.millis;
            cx = this.width * 0.5;
            cy = this.height * 0.5;
            center.x = cx + sin(t * 0.002) * cos(t * 0.00005) * cx * 0.5;
            center.y = cy + sin(t * 0.003) * tan(sin(t * 0.0003) * 1.15) * cy * 0.4;
          }
          var px, py, theta, tentacle;
          var step = TWO_PI / tentacleSettings.tentacles;
          for (var i = 0, n = tentacleSettings.tentacles; i < n; i++) {
            tentacle = tentacles[i];
            theta = i * step;
            px = cos(theta) * radius;
            py = sin(theta) * radius;
            tentacle.move(center.x + px, center.y + py);
            tentacle.update();
          }
        },

        draw: function () {
          var h = tentacleSettings.colour.h * 0.95;
          var s = tentacleSettings.colour.s * 100 * 0.95;
          var v = tentacleSettings.colour.v * 100 * 0.95;
          var w = v + (tentacleSettings.darkTheme ? -10 : 10);

          this.beginPath();
          this.arc(center.x, center.y, radius + tentacleSettings.thickness, 0, TWO_PI);
          this.lineWidth = tentacleSettings.headRadius * 0.3;
          this.globalAlpha = 0.2;
          this.strokeStyle = 'hsl(' + h + ',' + s + '%,' + w + '%)';
          this.stroke();

          this.globalAlpha = 1.0;

          for (var i = 0, n = tentacleSettings.tentacles; i < n; i++) {
            tentacles[i].draw(this);
          }

          this.beginPath();
          this.arc(center.x, center.y, radius + tentacleSettings.thickness, 0, TWO_PI);
          this.fillStyle = 'hsl(' + h + ',' + s + '%,' + v + '%)';
          this.fill();
        },

        // mousedown: function() {

        //   if ( demo ) {

        //     demo = false;
        //     tentacleSettings.interactive = true;
        //     interactiveGUI.updateDisplay();

        //     if ( !modified ) {
        //       tentacleSettings.length = 60;
        //       tentacleSettings.gravity = 0.1;
        //       tentacleSettings.wind = 0.0;
        //     }
        //   }
        // },

        export: function () {
          window.open(this.canvas.toDataURL(), 'tentacles', "top=20,left=20,width=" + this.width + ",height=" + this.height);
        }
      });
    }
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