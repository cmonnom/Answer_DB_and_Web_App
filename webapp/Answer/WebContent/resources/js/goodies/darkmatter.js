
// ----------------------------------------
// Dark Matter
// ----------------------------------------

function BlackHole(mouseLoc, maxSize, maxParticles) {
    this.init(mouseLoc, maxSize, maxParticles);
}

BlackHole.prototype = {

    init: function (mouseLoc, maxSize, maxParticles) {
        this.alive = true;
        this.particles = [];
        this.mouseLoc = mouseLoc;
        this.alpha = 1;
        this.color = 'rgba(0,0,0,' + this.alpha + ')';
        this.size = 0;
        this.speed = 0;
        this.particlesUpdated = false;
        this.maxParticles = maxParticles; //max number of particles
        this.maxSize = maxSize; //max size of the black hole
        this.exploding = false;
        this.supernovaStarted = false;
       
        this.initInboundParticles();
    },

    draw: function (ctx) {
        this.particles = this.particles.filter((p) => p.alive);
        for (var i = 0; i < this.particles.length; i++) {
            this.particles[i].draw(ctx);
        }
        // draw the black hole
        ctx.beginPath();
        ctx.arc(this.mouseLoc.x, this.mouseLoc.y, this.size, 0, TWO_PI);
        ctx.fillStyle = this.color;
        ctx.fill();

    },

    move: function () {
        for (var i = 0; i < this.particles.length; i++) {
            this.particles[i].move();
        }
        //keep replacing dead particles
        if (!this.supernovaStarted && !this.exploding && this.particles.length < this.maxParticles) {
            this.particles.push(this.createInboundParticle());
            //increase the size of the black hole
            this.size += 1;
        }

         //supernova
        if (!this.supernovaStarted && this.size >= this.maxSize) {
            this.exploding = true;
            this.supernovaStarted = true;
            // this.size = 0;
            this.initOutboundParticles();
        }
        if (this.supernovaStarted && this.alpha > 0) {
            this.alpha *= 0.98;
            this.color = 'rgba(0,0,0,' + this.alpha + ')';
            this.size *= 1.05;
        }
        if (this.alpha < 0.001) {
            this.size = 0;
        }
        this.alive = this.particles.length > 0;
    },

    initInboundParticles: function() {
        this.particles = [];
        for (var i = 0; i < this.maxParticles; i++) {
            this.particles.push(this.createInboundParticle());
        }
    },

    initOutboundParticles: function() {
        this.particles = [];
        for (var i = 0; i < this.maxSize; i++) {
            this.particles.push(this.createOutboundParticle());
        }
    },

    createInboundParticle: function() {
        var dist = random(100, 300); //distance from the mouse
        var angle = random(0, TWO_PI);
        var x = this.mouseLoc.x + cos(angle) * dist;
        var y = this.mouseLoc.y + sin(angle) * dist;
        var speed = random(2,5);
        return new DarkParticle(x, y, speed, this.mouseLoc, angle, true);
    },

    createOutboundParticle: function() {
        var dist = random(0, this.maxSize); //distance from the mouse
        var angle = random(0, TWO_PI);
        var x = this.mouseLoc.x + cos(angle) * dist;
        var y = this.mouseLoc.y + sin(angle) * dist;
        var speed = random(1,3);
        return new DarkParticle(x, y, speed, this.mouseLoc, angle, false);
    }

};


function DarkParticle(x, y, speed, focusPoint, angle, inbound) {
    this.init(x, y, speed, focusPoint, angle, inbound);
}

DarkParticle.prototype = {

    init: function (x, y, speed, focusPoint, angle, inbound) {

        this.alive = true;
        this.focusPoint = focusPoint;
        this.angle = angle;
        this.radius = 2;
        this.drag = 0.985;
        this.color = '#000';

        this.x = x || 0.0;
        this.y = y || 0.0;

        this.speed = speed;
        this.inbound = inbound;
    },

    move: function () {
        this.speed *= this.drag;
        if (this.inbound) {
            this.x = this.x - cos(this.angle) * this.speed;
            this.y = this.y - sin(this.angle) * this.speed;
            var distFromMouse = Math.sqrt(Math.pow(this.focusPoint.y - this.y, 2) + Math.pow(this.focusPoint.x - this.x, 2));
            if (distFromMouse < 5) {
                this.speed = 0;
            }
            this.alive = this.speed > 0.25;
        }
        else {
            this.x = this.x + cos(this.angle) * this.speed;
            this.y = this.y + sin(this.angle) * this.speed;
            this.alive = this.speed > 0.025;
        }
    },

    draw: function (ctx) {
        ctx.beginPath();
        ctx.arc(this.x, this.y, this.radius, 0, TWO_PI);
        ctx.fillStyle = this.color;
        ctx.fill();

    },

};


