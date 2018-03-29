const LogOut = { template: 
    `<div>
    Logging out...
    </div>`,
    created: function() {
        window.location = "./logout";
    }};

Vue.component('logout', {
    props: {
       'url-redirect': {default: "./", type: String}
    },
    template: `<div>
    <v-layout row wrap>
    <v-flex xs6>You have been disconnected.<br/> 
        Redirecting to login page in {{ valueFormatted }}.</v-flex>
    <v-flex xs3>
        <v-progress-linear v-model="valueDeterminate"></v-progress-linear>
    </v-flex>
</v-layout>

</div>`,
    data() {
        return {
            valueDeterminate: 0,
            valueFormatted: " sec"
        }
    },
    mounted () {
        var delayInSec = 2;
        var scale = 100 / delayInSec; //transforms 100 into 50 for 2 sec delay
        var timeUnit = "secs";
        var refreshRate = 100;
        var increment = refreshRate / delayInSec / 10; //calculate the increment amount
        this.interval = setInterval(() => {

          if (this.valueDeterminate >= 100) {
            window.location = this.urlRedirect;
            return (this.valueDeterminate = 100)
          }
          if (this.valueDeterminate % scale == 0) {
              var timeLeft = (100 - this.valueDeterminate) / scale;
              if (timeLeft <= 1) {
                timeUnit = "sec";
              }
              this.valueFormatted = timeLeft + " " + timeUnit;
          }
          this.valueDeterminate += increment; 
        }, refreshRate)
      }



})