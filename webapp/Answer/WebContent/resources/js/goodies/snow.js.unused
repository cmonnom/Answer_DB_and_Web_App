   
        // ----------------------------------------
        // Snow
        // ----------------------------------------

        function SnowFlake( x, y, radius, speed, ctx, colors, originalColor  ) {
          this.init( x, y, radius, speed,  ctx, colors, originalColor  );
      }

      SnowFlake.prototype = {

          init: function( x, y, radius, speed, ctx, colors, originalColor ) {

              this.alive = true;

              this.radius = radius || 10;
              this.originalRadius = radius;
              this.color = originalColor;
              this.originalColor = originalColor;

              this.x = x || 0.0;
              this.y = y || 0.0;

              this.vx = 0.0;
              this.vy = speed;
              this.ctx = ctx;
              this.colors = colors;
          },

          move: function() {

              this.x += this.vx;
              this.y += this.vy;
              this.alive = this.y < this.ctx.height;
          },

          draw: function( ctx ) {
              var touch = this.isInCircle(ctx.mouse.x, ctx.mouse.y, 200);
              if (touch && this.color == this.originalColor) {
                  this.color = random(this.colors);
                }
              if (touch) {
                  this.radius = Math.min(this.radius * 1.05, 20);
              }
              else if (!touch) {
                  this.color = this.originalColor;
                  this.radius = Math.max(this.radius * 0.95, this.originalRadius);
              }

              ctx.beginPath();
              ctx.arc( this.x, this.y, this.radius, 0, TWO_PI );
              ctx.fillStyle = this.color;
              ctx.fill();
          },

          isInCircle: function(cx, cy, r) {
              return Math.pow(this.x - cx, 2) + Math.pow(this.y - cy, 2) <= r*r;
          }
      };

    


