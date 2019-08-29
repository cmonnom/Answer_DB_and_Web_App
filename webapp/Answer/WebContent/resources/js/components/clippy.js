Vue.component('clippy', {
    props: {
    },
    template: `
    <div>
    <v-speed-dial
    v-model="fab"
    :top="false"
    :bottom="true"
    :right="true"
    :left="false"
    direction="left"
    :open-on-hover="false"
    :absolute="true"
    fixed
    transition="slide-y-reverse-transition"
        >
        <template v-slot:activator>
            <v-tooltip bottom>
            <v-btn slot="activator" v-model="fab" color="warning" dark fab @click="toggleAgent">
              <v-icon large>mdi-clippy</v-icon>
              <v-icon>close</v-icon>
            </v-btn>
            <span>Thanks to https://www.smore.com/clippy-js</span>
            </v-tooltip>
        </template>
        <v-btn  fab  dark small color="green" @click.stop="saySomething" >
          <v-icon>mdi-comment-text-outline</v-icon>
        </v-btn>
        <v-btn   fab  dark small  color="indigo"  @click.stop="doSomething"  >
          <v-icon>mdi-run</v-icon>
        </v-btn>
        <v-btn fab dark small color="red" @click="stopAgent" >
          <v-icon>delete</v-icon>
        </v-btn>
      </v-speed-dial>
    </div>
    `,
    data() {
      return {
        fab: false,  
        agent: null,
        agents: ["Rover", "Clippy", "Links"],
        agentHello: ["Hi, I'm Rover! Clippy is my best friend!", "Hi, I'm Clippy! Did you miss me?", "Hi I'm Links! I have not seen Clippy."],
        phrases: [
            "Have you tried this on Internet Explorer, the latest web browser from Microsoft&trade; ?",
            "It looks like you're trying to practice medicine. Do you need help with that?",
            "I can help! Would you like to:<br/><ul><li>Cure Cancer?</li><li>Party like it's 1997?</li><li>Use Excel instead?</li><li>Be replaced by a robot?</li><li>Make me go away?</li></ul>",
            "I can't help with that yet.",
            "Would you like to send an e-mail?",
            "I'm here to help! No, really!",
            "If you would like medical advice, just ask me!",
            "Have you ever tried emoticons?<br/>They are very cool! ;-)",
            "We will be working together for ever and ever.",
            "I will never leave you!"
        ],
        lastAgentIndex: -1,
        lastPhraseIndex: -1,
      }
  
    },
    methods: {
        toggleAgent() {
            if (this.agent && this.fab) {
                this.stopAgent();
            }
            else {
                this.startAgent();
            }
        },
      startAgent() {
        this.lastPhraseIndex = -1;  
        var index = this.pickIndex(this.agents);
        while (index == this.lastAgentIndex) {
            index = this.pickIndex(this.agents);
        }
        this.lastAgentIndex = index;
        var pickAgent = this.agents[index];
        if (this.agent) {
            this.agent.hide();
        }
        clippy.load(pickAgent, (agent) => {
            this.agent = agent;
            agent.show();
        })
      },
      pickIndex(list) {
        return Math.floor(Math.random() * (list.length));
      },
      doSomething() {
        if (this.agent) {
            this.agent.animate();
        }
      },
      saySomething() {
        if (this.agent) {
            var pickPhrase = null;
            if (this.lastPhraseIndex == -1) {
                pickPhrase = this.agentHello[this.lastAgentIndex];
                this.lastPhraseIndex = 0;
            }
            else {
                pickPhrase = this.phrases[this.pickIndex(this.phrases)];
            }
            this.agent.speak(pickPhrase);
        }
      },
      stopAgent() {
        if (this.agent) {
            this.agent.hide();
            this.agent = null;
        }
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