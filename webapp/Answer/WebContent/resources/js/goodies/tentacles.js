
/**
 * Copyright (C) 2012 by Justin Windle
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

var tentacleSettings = {
  interactive: true,
  darkTheme: true,
  headRadius: 30,
  thickness: 18,
  tentacles: 40,
  friction: 0.02,
  gravity: 0.5,
  colour: { h:205, s:0.7, v:0.8 },
  length: 70,
  pulse: false,
  wind: 0.3
};

var tentacleUtils = {

  curveThroughPoints: function( points, ctx ) {

    var i, n, a, b, x, y;
    
    for ( i = 1, n = points.length - 2; i < n; i++ ) {

      a = points[i];
      b = points[i + 1];
      
      x = ( a.x + b.x ) * 0.5;
      y = ( a.y + b.y ) * 0.5;

      ctx.quadraticCurveTo( a.x, a.y, x, y );
    }

    a = points[i];
    b = points[i + 1];
    
    ctx.quadraticCurveTo( a.x, a.y, b.x, b.y );
  }
};

var tentacleNode = function( x, y ) {
  
  this.x = this.ox = x || 0.0;
  this.y = this.oy = y || 0.0;

  this.vx = 0.0;
  this.vy = 0.0;
};

var Tentacle = function( options ) {

  this.length = options.length || 10;
  this.radius = options.radius || 10;
  this.spacing = options.spacing || 20;
  this.friction = options.friction || 0.8;
  this.shade = random( 0.85, 1.1 );

  this.nodes = [];
  this.outer = [];
  this.inner = [];
  this.theta = [];

  for ( var i = 0; i < this.length; i++ ) {
    this.nodes.push( new tentacleNode() );
  }
};

Tentacle.prototype = {

  move: function( x, y, instant ) {
    
    this.nodes[0].x = x;
    this.nodes[0].y = y;

    if ( instant ) {

      var i, node;

      for ( i = 1; i < this.length; i++ ) {

        node = this.nodes[i];
        node.x = x;
        node.y = y;
      }
    }
  },

  update: function() {

    var i, n, s, c, dx, dy, da, px, py, node, prev = this.nodes[0];
    var radius = this.radius * tentacleSettings.thickness;
    var step = radius / this.length;

    for ( i = 1, j = 0; i < this.length; i++, j++ ) {

      node = this.nodes[i];

      node.x += node.vx;
      node.y += node.vy;

      dx = prev.x - node.x;
      dy = prev.y - node.y;
      da = Math.atan2( dy, dx );

      px = node.x + cos( da ) * this.spacing * tentacleSettings.length;
      py = node.y + sin( da ) * this.spacing * tentacleSettings.length;

      node.x = prev.x - ( px - node.x );
      node.y = prev.y - ( py - node.y );

      node.vx = node.x - node.ox;
      node.vy = node.y - node.oy;

      node.vx *= this.friction * (1 - tentacleSettings.friction);
      node.vy *= this.friction * (1 - tentacleSettings.friction);

      node.vx += tentacleSettings.wind;
      node.vy += tentacleSettings.gravity;

      node.ox = node.x;
      node.oy = node.y;

      s = sin( da + HALF_PI );
      c = cos( da + HALF_PI );

      this.outer[j] = {
        x: prev.x + c * radius,
        y: prev.y + s * radius
      };

      this.inner[j] = {
        x: prev.x - c * radius,
        y: prev.y - s * radius
      };

      this.theta[j] = da;

      radius -= step;

      prev = node;
    }
  },

  draw: function( ctx ) {

    var h, s, v, e;

    s = this.outer[0];
    e = this.inner[0];

    ctx.beginPath();
    ctx.moveTo( s.x, s.y );
    tentacleUtils.curveThroughPoints( this.outer, ctx );
    tentacleUtils.curveThroughPoints( this.inner.reverse(), ctx );
    ctx.lineTo( e.x, e.y );
    ctx.closePath();

    h = tentacleSettings.colour.h * this.shade;
    s = tentacleSettings.colour.s * 100 * this.shade;
    v = tentacleSettings.colour.v * 100 * this.shade;

    ctx.fillStyle = 'hsl(' + h + ',' + s + '%,' + v + '%)';
    ctx.fill();

    if ( tentacleSettings.thickness > 2 ) {

      v += tentacleSettings.darkTheme ? -10 : 10;

      ctx.strokeStyle = 'hsl(' + h + ',' + s + '%,' + v + '%)';
      ctx.lineWidth = 1;
      ctx.stroke();
    }
  }
};

