

Vue.component('report-patient-details', {
    props: {

    },
    template: `<div class="text-xs-center pb-3">
    <v-card>
        <v-toolbar class="elevation-0" dense dark color="primary">
            <v-menu offset-y offset-x class="ml-0">
                <v-btn slot="activator" flat icon dark>
                    <v-icon color="amber accent-2">assignment_ind</v-icon>
                </v-btn>
                <v-list>
                    <v-list-tile avatar @click="closePatientDetails()">
                        <v-list-tile-avatar>
                            <v-icon>cancel</v-icon>
                        </v-list-tile-avatar>
                        <v-list-tile-content>
                            <v-list-tile-title>Close Patient Details</v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>
                </v-list>
            </v-menu>
            <v-toolbar-title class="ml-0">Patient Details</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-tooltip bottom>
                <v-btn flat icon @click="closePatientDetails()" slot="activator">
                    <v-icon>close</v-icon>
                </v-btn>
                <span>Close Details</span>
            </v-tooltip>
        </v-toolbar>
        <v-container grid-list-md fluid>
            <v-layout row wrap>
                <v-flex xs4 v-for="table in patientTables" :key="table.name">
                    <v-card flat>
                        <v-card-text>
                            <v-list class="dense-tiles">
                                <v-list-tile v-for="item in table.items" :key="item.label">
                                    <v-list-tile-content class="pb-2">
                                        <v-layout class="full-width " justify-space-between>
                                            <v-flex class="text-xs-left xs">
                                                <span :class="['selectable']">{{
                                                    item.label }}:</span>
                                            </v-flex>
                                            <v-flex :class="['xs','text-xs-right', '', 'blue-grey--text', 'text--lighten-1']">
                                                <span v-if="!item.value2" class="selectable">{{ item.value }}</span>    
                                                <span v-else>{{ item.value2 }}&nbsp;{{ item.value }}</span>
                                            </v-flex>
                                        </v-layout>
                                    </v-list-tile-content>
                                </v-list-tile>
                            </v-list>
                        </v-card-text>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-container>
    </v-card>
</div>
    `,
    data() {
        return {
            patientTables: []
        }
    },
    methods: {
        closePatientDetails() {
            this.$emit("close-patient-details");
        },
        extractPatientDetailsInfo(caseName, fieldToReturn) {
            var valueToReturn = null;
            for (var i = 0; i < this.patientTables.length; i++) {
                for (var j = 0; j < this.patientTables[i].items.length; j++) {
                    var item = this.patientTables[i].items[j];
                    if (caseName && item.field == "caseName") {
                        this.updateCaseName(caseName + " (" + item.value + ")")
                    }
                    else if (item.field == "dedupPctOver100X") {
                        item.value = (Math.round(parseFloat(item.value * 100)) / 100) + "%";
                    }
                    else if (item.value != "Not calculated" && (item.field == "tumorPercent" || item.field == "msi")) {
                        item.value += "%";
                    }
                    if (fieldToReturn && (item.field == fieldToReturn)) {
                        valueToReturn = item.value;
                    }

                }
            }
            return valueToReturn;
        },
        updateCaseName(fullCaseName) {
            this.$emit("update-case-name", fullCaseName);
        }
    },
    computed: {

    },
    created() {

    },
    destroyed() {
    },
    mounted() {
    },
    watch: {
    }



});

