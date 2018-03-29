const Home = {
    template:
        `<div>

 Nothing here yet.
 <v-btn color="primary">Color</v-btn>

</div>`,
    data() {
        return {
            // loading: false,
            // selectedMonth: null,
            // selectedMonthFormatted: null,
            // monthVisible: false,
            // chartId: 'tat',
            // chart: null,
            // chartFontSize: 16,
            // dataConfig: {},
            // gui: {
            //     behaviors: [
            //         { id: 'DownloadSVG', enabled: 'none' },
            //         { id: 'ViewSource', enabled: 'none' },
            //         { id: 'HideGuide', enabled: 'none' },
            //         { id: 'ShowGuide', enabled: 'none' }
            //     ]
            // },
            // colors: ['#43B195', '#EA4352', '#FAE935', '#BBDC98'],
            // seriesStyle: [],
            // chartTitle: 'Turn Around Times (TAT) for Processed Orders',
            // helpVisible: false,
            // tableTitle: "Metrics for Samples Received in "


        }
    },
    methods: {
        // applySeriesStyle(series) {
        //     series.forEach((serie, index) => {
        //         serie.backgroundColor = this.seriesStyle[index].backgroundColor;
        //         serie.backgroundImage = this.seriesStyle[index].backgroundImage;
        //     });
        // },
        // getTATChartData() {
        //     axios.get(webAppRoot + "/getTATChartData", {
        //         params: {

        //         }
        //     })
        //         .then(response => {
        //             if (response.data.isAllowed) {
        //                 this.applySeriesStyle(response.data.series);
        //                 var labelLength = response.data.labels.length;
        //                 this.dataConfig = {
        //                     gui: this.gui,
        //                     graphset: [{
        //                         type: 'bar',
        //                         plot: {
        //                             alpha: 1,
        //                             tooltip: {
        //                                 visible: false
        //                             }
        //                         },
        //                         title: {
        //                             text: this.chartTitle
        //                         },
        //                         legend: {
        //                             item: {
        //                                 fontSize: this.chartFontSize
        //                             }
        //                         },
        //                         series: response.data.series,
        //                         scaleX: {
        //                             zooming: true,
        //                             zoomTo: [Math.max(labelLength - 12, 0), labelLength - 1], //zoom to the last 12 months
        //                             labels: response.data.labels,
        //                             // label: {
        //                             //     text: "Months"
        //                             // },
        //                             item: {
        //                                 //     fontAngle: -45
        //                                 flat: false,
        //                                 fontSize: this.chartFontSize
        //                             }
        //                         },
        //                         crosshairX: {
        //                             plotLabel: {
        //                                 headerText: '%data-labels',
        //                                 text: "<b style='color:%color'>%t</b> %v",
        //                                 fontSize: this.chartFontSize
        //                             },
        //                             scaleLabel: {
        //                                 fontSize: this.chartFontSize
        //                             }
        //                         },
        //                         preview: {
        //                             // adjustLayout: true,
        //                             // y: "60%"
        //                         },
        //                         "scroll-x": {

        //                         }
        //                     }]
        //                 };

        //                 zingchart.render({
        //                     id: this.chartId,
        //                     data: this.dataConfig,
        //                     height: "100%"
        //                 });
        //             }
        //             else {
        //                 this.handleDialogs(response, this.getTATChartData);
        //             }
        //         })
        //         .catch(error => {
        //             console.log(error);
        //         });
        // },
        // getMonthlySampleSummary() {
        //     this.loading = true;
        //     this.$refs.monthlySamples.startLoading();
        //     if (!this.selectedMonth) {
        //         this.selectedMonth = moment().subtract(1, 'months').format("YYYY-MM");
        //         this.selectedMonthFormatted = this.formatDate(this.selectedMonth);
        //     }
        //     axios.get(webAppRoot + "/getMonthlySampleSummary", {
        //         params: {
        //             date: this.selectedMonth
        //         }
        //     })
        //         .then(response => {
        //             if (response.data.isAllowed) {
        //                 this.$refs.monthlySamples.manualData(response);
        //             }
        //             else {
        //                 this.handleDialogs(response, this.getMonthlySampleSummary);
        //             }
        //             this.loading = false;
        //             this.$refs.monthlySamples.stopLoading();
        //         })
        //         .catch(error => {
        //             this.loading = false;
        //             this.$refs.monthlySamples.stopLoading();
        //             console.log(error);
        //         });
        // },
        // handleDialogs(response, callback) {
        //     if (response.data.isXss) {
        //         bus.$emit("xss-error", [this, response.data.reason]);
        //     }
        //     else {
        //         bus.$emit("login-needed", [this, callback])
        //     }
        // },
        // parseDate(dateString) {
        //     return moment(dateString, "MMM YYYY").format("YYYY-MM");
        // },
        // formatDate(date) {
        //     if (!date) {
        //         return null;
        //     }
        //     var formatted = moment(date, "YYYY-MM");
        //     return formatted.format("MMM YYYY");
        // },
        // formatAndRefresh(date) {
        //     this.selectedMonthFormatted = this.formatDate(date);
        //     this.getMonthlySampleSummary();
        // },
        // noFutureDate(date) {
        //     return moment(date).isBefore(moment());
        // }

    },
    mounted: function () {
        // this.getMonthlySampleSummary();
        // this.getTATChartData();
    },
    destroyed: function () {
        // zingchart.exec(this.chartId, 'destroy');
    },
    created: function () {
        // zingchart.loadModules('patterns');
        // this.seriesStyle = [
        //     {
        //         backgroundColor: this.colors[0],
        //         backgroundImage: "PATTERN_BACKWARD_DIAGONAL"
        //         // marker: {
        //         //     borderColor: this.colors[0],
        //         //     type: "triangle"
        //         // }
        //     },
        //     {
        //         backgroundColor: this.colors[1],
        //         backgroundImage: "PATTERN_DIAGONAL_BRICK"
        //     },
        //     {
        //         backgroundColor: this.colors[2],
        //         backgroundImage: "PATTERN_WEAVE"
        //     },
        //     {
        //         backgroundColor: this.colors[3],
        //         backgroundImage: "PATTERN_ZIGZAG"
        //     }];
        // //load the metrics for the month clicked on    
        // zingchart.node_click = e => { //when user clicks on a bar
        //     this.selectedMonth = this.parseDate(e["data-labels"]);
        //     this.formatAndRefresh(this.selectedMonth);
        // };
        // zingchart.bind(null, 'label_click', e => { //when user clicks on the month label
        //     this.selectedMonth = this.parseDate(e["text"]);
        //     this.formatAndRefresh(this.selectedMonth);
        // });
    },
    computed: {
        // fullSizeChart: function () {
        //     return {
        //         position: "relative",
        //         height: ((window.innerHeight - 120) * 0.65) + "px"
        //     }
        // }
    },
    watch: {
    }
};

