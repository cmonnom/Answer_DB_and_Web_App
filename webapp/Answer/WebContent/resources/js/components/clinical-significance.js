Vue.component('clinical-significance', {
    props: {
        title: { default: "", type: String },
        clinicalSignificance: {default: () => [], type: Array},
        disabled: {default: true, type: Boolean},
        originalClinicalSignificance: {default: () => [], type: Array},
    },
    template: `
        <div class="text-xs-center">
            <v-card>
                <v-toolbar class="elevation-0" dense dark color="primary">
                    <v-menu offset-y offset-x class="ml-0">
                        <v-btn slot="activator" flat icon dark>
                            <v-icon color="amber accent-2">mdi-message-bulleted</v-icon>
                        </v-btn>
                        <v-list>
                            <v-list-tile avatar @click="closePanel()">
                                <v-list-tile-avatar>
                                    <v-icon>cancel</v-icon>
                                </v-list-tile-avatar>
                                <v-list-tile-content>
                                    <v-list-tile-title>Close Panel</v-list-tile-title>
                                </v-list-tile-content>
                            </v-list-tile>
                        </v-list>
                    </v-menu>
                    <v-toolbar-title>{{ title }}</v-toolbar-title>
                    <v-spacer></v-spacer>
                    <v-tooltip bottom>
                        <v-btn flat icon @click="closePanel()" slot="activator">
                            <v-icon>close</v-icon>
                        </v-btn>
                        <span>Close Panel</span>
                    </v-tooltip>
                </v-toolbar>
                <v-card-text>
                <v-container grid-list-md fluid>
                <!--
                <v-layout row wrap>
                <v-flex :class="flexClass()" v-for="item in clinicalSignificance" :key="item.label">
                    <v-layout row wrap>
                        <v-flex xs12 pb-0>
                            <span class="subheading"> {{ item.label }}: </span>     
                        </v-flex>                                               
                        <v-flex xs12 pt-0 class="caption">
                            <v-text-field :textarea="true" v-model="item.text" class="mr-2 no-height pt-1 no-label" hide-details
                            @input="handleInputChanged"
                            :disabled="disabled">
                            </v-text-field>
                        </v-flex>
                    </v-layout>  
                </v-flex>
                </v-layout> 
                -->
                <v-layout row wrap>
                <v-flex xs12 v-for="item in clinicalSignificance" :key="item.label">
                    <v-layout row wrap>
                        <v-flex xs4 pt-3>
                            <v-layout row wrap>
                            <v-flex xs12>
                            <span class="subheading"> {{ item.label }} </span>   
                            </v-flex> 
                            <v-flex xs12>
                            <v-tooltip bottom>
                            <v-btn slot="activator" icon flat color="primary" @click="resetTextField(item)">
                                <v-icon>settings_backup_restore</v-icon>
                            </v-btn>
                            <span>Cancel Changes</span>
                            </v-tooltip>
                            </v-flex>
                            </v-layout>
                        </v-flex>                                               
                        <v-flex xs8 pt-0 class="caption">
                            <v-text-field :textarea="true" v-model="item.text" class="mr-2 no-height pt-1 no-label" hide-details
                            @input="handleInputChanged"
                            :disabled="disabled">
                            </v-text-field>
                        </v-flex>
                    </v-layout>  
                </v-flex>
                </v-layout> 

                </v-container>
                </v-card-text>
            </v-card>
        </div>`
    , data() {
        return {
        }

    },
    methods: {
        closePanel() {
            this.$emit("close-panel", null);
        },
        flexClass() {
            if (this.clinicalSignificance.length && this.clinicalSignificance.length > 1) {
                return ['xs12', 'md6','lg6'];
            }
            return ['xs12'];
        },
        handleInputChanged() {
            this.$emit("clinical-significance-changed", null);
        },
        resetTextField(item) {
            for (var i = 0; i < this.originalClinicalSignificance.length; i++) {
                var originalItem = this.originalClinicalSignificance[i];
                if (originalItem.label == item.label) {
                    item.text = originalItem.text;
                }
            }
        }
    },
    created: function () {
    },
    destroyed: function () {
    },
    mounted() {
    },
    computed: {
    },
    watch: {
    }


});