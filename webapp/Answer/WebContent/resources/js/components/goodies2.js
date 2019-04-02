Vue.component('goodies2', {
  props: {
  },
  template: `
  <div class="goodies-screen" :id="containerId" :class="[opacityTransition ? 'fade-out' : '', canClick ? 'cursor-pointer' : '']">
  </div>
  `,
  data() {
    return {
      containerId: "goodies-panel",
      maxParticules: 280,
      colors: ['#69D2E7', '#A7DBD8', '#E0E4CC', '#F38630', '#FA6900', '#FF4E50', '#F9D423'],
      demo: {},
      choices: [
          {
          start: this.createParticules, //starts the animation
          end: this.clearParticules, //smooth out the ending (instead of direct destroy)
          endDelay: 1000 //how long before calling destroy
        },
        {
          start: this.createTentacles,
          end: this.clearTentacles,
          endDelay: 2000
        },
        {
          start: this.createBubbles,
          end: this.clearBubbles,
          endDelay: 2000
        },
        {
          start: this.createSnowParticles,
          end: this.clearSnowParticles,
          endDelay: 3000
        },
        {
          start: this.createFireworks,
          end: this.clearFireworks,
          endDelay: 2000
        },
        {
          start: this.createBlackHoles,
          end: this.clearBlackHoles,
          endDelay: 2000
        }
      ],
      lastIndex: -1,
      opacityTransition: false,
      maxSnowParticles: 1000,
      stopSnowProduction: false,
      currentInterval: -1,
      snowParticles: [],
      snowOriginColor: "#AAA",
      fireworks: [],
      blackHoles: [],
      canClick: false
    }

  },
  methods: {
    pickIndex() {
      return Math.floor(Math.random() * (this.choices.length));
    },
    //randomly select an effect here
    activateGoodies() {
      var index = this.pickIndex();
      var counter = 0; //in case the random function always returns the same number
      while (index == this.lastIndex && counter < 5) {
        index = this.pickIndex();
        counter++;
      }
      this.lastIndex = index;
      var choice = this.choices[index];
      choice.start();
      if (choice.end) {
        setTimeout(choice.end, 10000);
      }
      setTimeout(this.endGoodies, 10000 + choice.endDelay);
    },
    activateGoodiesByIndex(index) {
      var counter = 0; //in case the random function always returns the same number
      this.lastIndex = index;
      var choice = this.choices[index];
      choice.start();
      if (choice.end) {
        setTimeout(choice.end, 10000);
      }
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
      this.canClick = false;
      this.$emit("end-goodies");
      this.opacityTransition = false;
      this.stopSnowProduction = false;
      this.snowParticles = [];
      this.fireworks = [];
      this.demo = null;
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

        theta = random(2 * Math.PI);
        force = random(2, 8);

        particle.vx = sin(theta) * force;
        particle.vy = cos(theta) * force;

        particles.push(particle);
      }

      this.demo.update = () => {

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
      // var scale = window.devicePixelRatio || 1;
      this.demo = Sketch.create({
        retina: 'auto',
        container: document.getElementById(this.containerId)
      });
      this.demo.setup = () => {

        center.x = this.demo.width / 2;
        center.y = this.demo.height / 2;

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
      };

      this.demo.update = () => {
        var t, cx, cy, pulse;
        t = this.millis * 0.001;
        if (tentacleSettings.pulse) {
          pulse = pow(sin(t * PI), 18);
          radius = tentacleSettings.headRadius * 0.5 + tentacleSettings.headRadius * 0.5 * pulse;
        }
        if (tentacleSettings.interactive) {
          // ease += (0.7 - ease) * 0.05;
          // center.x += (this.demo.mouse.x / scale - center.x) * ease;
          // center.y += (this.demo.mouse.y / scale - center.y) * ease;
          center.x += (this.demo.mouse.x - center.x);
          center.y += (this.demo.mouse.y - center.y);
          // console.log(this.demo.mouse.x, this.demo.mouse.y, scale, ease, center);
        } else {
          t = this.demo.millis;
          cx = this.demo.width * 0.5;
          cy = this.demo.height * 0.5;
          center.x = cx + sin(t * 0.002) * cos(t * 0.00005) * cx * 0.5;
          center.y = cy + sin(t * 0.003) * tan(sin(t * 0.0003) * 1.15) * cy * 0.4;
        }
        var px, py, theta, tentacle;
        var step = 2 * Math.PI / tentacleSettings.tentacles;
        for (var i = 0, n = tentacleSettings.tentacles; i < n; i++) {
          tentacle = tentacles[i];
          theta = i * step;
          px = cos(theta) * radius;
          py = sin(theta) * radius;
          tentacle.move(center.x + px, center.y + py);
          tentacle.update();
        }
      }


      this.demo.draw = () => {
        var h = tentacleSettings.colour.h * 0.95;
        var s = tentacleSettings.colour.s * 100 * 0.95;
        var v = tentacleSettings.colour.v * 100 * 0.95;
        var w = v + (tentacleSettings.darkTheme ? -10 : 10);

        this.demo.beginPath();
        this.demo.arc(center.x, center.y, radius + tentacleSettings.thickness, 0, 2 * Math.PI);
        this.demo.lineWidth = tentacleSettings.headRadius * 0.3;
        this.demo.globalAlpha = 0.2;
        this.demo.strokeStyle = 'hsl(' + h + ',' + s + '%,' + w + '%)';
        this.demo.stroke();

        this.demo.globalAlpha = 1.0;

        for (var i = 0, n = tentacleSettings.tentacles; i < n; i++) {
          tentacles[i].draw(this.demo);
        }

        this.demo.beginPath();
        this.demo.arc(center.x, center.y, radius + tentacleSettings.thickness, 0, 2 * Math.PI);
        this.demo.fillStyle = 'hsl(' + h + ',' + s + '%,' + v + '%)';
        this.demo.fill();
      }
    },
    createBubbles() {
      // General Variables
      var Particle, particleCount, particles, sketch, z;

      sketch = Sketch.create({
        container: document.getElementById(this.containerId)
      }
      );
      this.demo = sketch;

      particles = [];

      particleCount = 750;

      sketch.mouse.x = sketch.width / 2;

      sketch.mouse.y = sketch.height / 2;

      sketch.strokeStyle = 'hsla(200, 50%, 50%, .4)';

      sketch.globalCompositeOperation = 'lighter';


      // Particle Constructor
      Particle = function () {
        this.x = random(sketch.width);
        this.y = random(sketch.height, sketch.height * 2);
        this.vx = 0;
        this.vy = -random(1, 10) / 5;
        this.radius = this.baseRadius = 2;
        this.maxRadius = 50;
        this.threshold = 150;
        return this.hue = random(180, 240);
      };

      // Particle Prototype
      Particle.prototype = {
        update: function () {
          var dist, distx, disty, radius;
          // Determine Distance From Mouse
          distx = this.x - sketch.mouse.x;
          disty = this.y - sketch.mouse.y;
          dist = sqrt(distx * distx + disty * disty);

          // Set Radius
          if (dist < this.threshold) {
            radius = this.baseRadius + ((this.threshold - dist) / this.threshold) * this.maxRadius;
            this.radius = radius > this.maxRadius ? this.maxRadius : radius;
          } else {
            this.radius = this.baseRadius;
          }

          // Adjust Velocity
          this.vx += (random(100) - 50) / 1000;
          this.vy -= random(1, 20) / 10000;

          // Apply Velocity
          this.x += this.vx;
          this.y += this.vy;

          // Check Bounds   
          if (this.x < -this.maxRadius || this.x > sketch.width + this.maxRadius || this.y < -this.maxRadius) {
            this.x = random(sketch.width);
            this.y = random(sketch.height + this.maxRadius, sketch.height * 2);
            this.vx = 0;
            return this.vy = -random(1, 10) / 5;
          }
        },
        render: function () {
          sketch.beginPath();
          sketch.arc(this.x, this.y, this.radius, 0, TWO_PI);
          sketch.closePath();
          sketch.fillStyle = 'hsla(' + this.hue + ', 60%, 40%, .35)';
          sketch.fill();
          return sketch.stroke();
        }
      };

      // Create Particles
      z = particleCount;

      while (z--) {
        particles.push(new Particle());
      }

      // Sketch Clear
      sketch.clear = function () {
        return sketch.clearRect(0, 0, sketch.width, sketch.height);
      };


      // Sketch Update
      sketch.update = function () {
        var i, results;
        i = particles.length;
        results = [];
        while (i--) {
          results.push(particles[i].update());
        }
        return results;
      };

      // Sketch Draw
      sketch.draw = function () {
        var i, results;
        i = particles.length;
        results = [];
        while (i--) {
          results.push(particles[i].render());
        }
        return results;
      };

      // sketch.stroke();
    },
    clearBubbles() {
      this.opacityTransition = true;
    },
    createSnowParticles() {
      this.demo = Sketch.create({
        container: document.getElementById(this.containerId)
      });

      this.demo.update = () => {
        var i, particle;
        for (i = this.snowParticles.length - 1; i >= 0; i--) {

          particle = this.snowParticles[i];

          if (particle.alive) {
            particle.move(this.demo);
          }
          else {
            this.snowParticles.splice(i, 1);
          }
        }
      };

      this.demo.draw = () => {
        // this.demo.globalCompositeOperation = 'lighter';
        for (var i = this.snowParticles.length - 1; i >= 0; i--) {
          this.snowParticles[i].draw(this.demo);
        }
      };

      this.currentInterval = setInterval(this.createRandomSnowParticle, 5);
    },
    createRandomSnowParticle() {
      if (this.stopSnowProduction) {
        clearInterval(this.currentInterval);
        return;
      }
      if (this.snowParticles.length > this.maxSnowParticles) {
        return;
      }
      var x = random(0, this.demo.width);
      var y = 0;
      var speed = random(4, 8);
      var radius = random(0.2, 5);
      this.snowParticles.push(new SnowFlake(x, y, radius, speed, this.demo, this.colors, this.snowOriginColor));
    },
    clearSnowParticles() {
      this.stopSnowProduction = true;
    },
    createFireworks() {
      this.canClick = true;
      this.demo = Sketch.create({
        container: document.getElementById(this.containerId),
        retina: 'auto'
      });

      this.demo.update = () => {
        for (var i = 0; i < this.fireworks.length; i++) {
          this.fireworks[i].move();
        }
      };

      this.demo.mousedown = () => {
        this.createAFirework(true);
      }

      this.demo.draw = () => {
        this.fireworks = this.fireworks.filter((f) => f.alive);
        for (var i = this.fireworks.length - 1; i >= 0; i--) {
          this.fireworks[i].draw(this.demo);
          this.fireworks[i].move();
        }
      };
      this.currentInterval = setInterval(this.createAFirework, 500);
    },
    createAFirework(manual) {
      var particuleCount = random(20, 30);
      var particles = [];
      var particuleSize = 2;
      var fireworkSize = 3;
      var verticalDuration = random(500, 1000);
      var x = this.demo.mouse.x;
      var y = this.demo.mouse.y;
      if (!manual) {
        x = random(100, this.demo.width - 100);
        y = random(100, this.demo.height - 100);
      }
      var hueMin = random(0, 360);
      for (var i = 0; i < particuleCount; i++) {
        var speed = random(1, 4);
        var duration = random(2000, 3000);
        var hue = random(hueMin, hueMin + 50);
        particles.push(new FireworkParticle(x, y, particuleSize, speed, duration, hue));
      }
      var speed = random(2, 3);
      this.fireworks.push(new Fireworks(particles, x, y, fireworkSize, speed, verticalDuration));
    },
    clearFireworks() {
      clearInterval(this.currentInterval);
      this.opacityTransition = true;
    },
    createBlackHoles() {
      this.canClick = true;
      this.demo = Sketch.create({
          container: document.getElementById(this.containerId),
          retina: 'auto'
      });
  
      this.demo.update = () => {
          for (var i = 0; i < this.blackHoles.length; i++) {
            this.blackHoles[i].move();
            }
      }
  
      this.demo.mousedown = () => {
          var mouseLoc = {x: this.demo.mouse.x, y: this.demo.mouse.y};
          this.createABlackHole(mouseLoc);
      }
  
      this.demo.draw = () => {
        this.blackHoles = this.blackHoles.filter((b) => b.alive);
          for (var i = this.blackHoles.length - 1; i >= 0; i--) {
            this.blackHoles[i].draw(this.demo);
            this.blackHoles[i].move();
            }
      }
  },  
  createABlackHole(mouseLoc) {
      var maxSize = random(20, 100);
      var maxParticles = random(20, 40);
      this.blackHoles.push(new BlackHole(mouseLoc, maxSize, maxParticles));
  },
  clearBlackHoles() {
    clearInterval(this.currentInterval);
    this.opacityTransition = true;
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