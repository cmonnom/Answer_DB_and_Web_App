Vue.component('draggable-headers', {
    props: {
        "headers": {default: function() {return []}},
        "header-order": {default: function() {return []}}
    },
    template:`
    <draggable :list="headerOrder" @start="draggingStarted">
    <v-chip v-for="header in headerOrder" :key="header" color="teal" text-color="white"
    :class="{'is-dragging':isDragging}">
    <v-avatar>
        <v-icon>swap_horiz</v-icon>
    </v-avatar>
    {{ getHeaderByValue(header) }}
    </v-chip>

    </draggable>
    `,
    data() {
        return {
            isDragging: false
        }
    },
    methods: {
        draggingStarted(evt) {
            var item = evt.item;
        },
        // Retrieve the human readable form by Java field name
        // Used by draggable
        getHeaderByValue(header) {
            var text = "";
            this.headers.forEach(item => {
                if (item.value === header) {
                    text = item.text;
                }
            })
            return text;
        }
    },
    computed: {
       
    }
});