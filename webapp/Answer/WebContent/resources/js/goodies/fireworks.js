
// ----------------------------------------
// Fireworks
// ----------------------------------------

function Fireworks(particles, x, y, size, speed, verticalDuration) {
    this.init(particles, x, y, size, speed, verticalDuration);
}

Fireworks.prototype = {

    init: function (particles, x, y, size, speed, verticalDuration) {
        this.alive = true;
        this.particles = particles;
        this.x = x;
        this.y = y;
        this.color = '#000';
        this.size = size;
        this.speed = speed;
        this.startedTime = Date.now();
        this.particlesUpdated = false;
        this.verticalDuration = verticalDuration;
    },

    draw: function (ctx) {
        var duration = Date.now() - this.startedTime;
        if (duration < this.verticalDuration) { //vertical launch
            ctx.beginPath();
            ctx.arc(this.x, this.y, this.size, 0, TWO_PI);
            ctx.fillStyle = this.color;
            ctx.fill();
        }
        else { //explosion
            this.particles = this.particles.filter((p) => p.alive);
            for (var i = 0; i < this.particles.length; i++) {
                this.particles[i].draw(ctx);
            }
            if (this.particles.length == 0) {
                this.alive = false;
            }
        }
    },

    move: function () {
        var duration = Date.now() - this.startedTime;
        if (duration < this.verticalDuration) { //vertical launch
            this.y -= this.speed;
            this.updateColor();
        }
        else if (!this.particlesUpdated) {
            for (var i = 0; i < this.particles.length; i++) {
                var particle = this.particles[i];
                particle.x = this.x;
                particle.y = this.y;
                particle.startedTime = Date.now();
            }
            this.particlesUpdated = true;
        }
        else {
            for (var i = 0; i < this.particles.length; i++) {
                this.particles[i].move();
            }
        }
    },

    updateColor: function () {
        var hue = random(0, 360);
        this.color = "hsl(" + hue + ", 100%, 50%)";
    }

};


function FireworkParticle(x, y, radius, speed, duration) {
    this.init(x, y, radius, speed, duration);
}

FireworkParticle.prototype = {

    init: function (x, y, radius, speed, duration) {

        this.alive = true;

        this.radius = radius || 10;
        this.angle = random(0, TWO_PI);
        this.drag = 0.985;
        var hue = random(0, 360);
        this.color = "hsl(" + hue + ", 100%, 50%)";

        this.x = x || 0.0;
        this.y = y || 0.0;

        this.speed = speed;
        this.direction = random(['left', 'right']);
        this.duration = duration;
        this.startedTime = 0;
    },

    move: function () {
        this.speed *= this.drag;
        var x = cos(this.angle) * this.speed;
        if (this.direction == 'left') {
            this.x -= x;
        }
        else {
            this.x += x;
        }
        this.y -= sin(this.angle) * this.speed;
        this.updateColor();
        // this.alive = Date.now() - this.startedTime <= this.duration;
        this.alive = this.speed > 0.25;
    },

    draw: function (ctx) {
        ctx.beginPath();
        ctx.arc(this.x, this.y, this.radius, 0, TWO_PI);
        ctx.fillStyle = this.color;
        ctx.fill();

    },

    updateColor: function () {
        var hue = random(0, 360);
        this.color = "hsl(" + hue + ", 100%, 50%)";
    }
};


