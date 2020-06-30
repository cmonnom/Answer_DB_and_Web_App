Vue.component('lookup-panel-plot-utils', {
    props: {
        standalone: { default: true, type: Boolean },
    },
    template: "<span></span>",
    data() {
        return {
            plotlyConfig: {
                displayModeBar: true,
                displaylogo: false,
                modeBarButtonsToRemove: ['sendDataToCloud', 'editInChartStudio', 'select2d',
                'lasso2d', 'hoverClosestCartesian', 'hoverCompareCartesian',
                'toggleSpikelines', 'autoScale2d'],
                responsive: true
            },
            cividisColors: {
                "blue75": "#38486b", //blue75
                "Deletion": "#38486b", //blue75
                "Amplification": "#a39a76", //brown50
                "SNP/Indel": "#e4cf5b", //yellow25
                "DEL": "#e4cf5b", //yellow25
            },
            cividisPaletteAlt:[
                "#002b68", //blue1
                "#908b78", //yellow1
                "#1a3a6c", //blue2
                "#ada273", //yellow2
                "#424e6b", //blue3
                "#c7b76a", //yellow3
                "#5f626e", //blue4
                "#e5cf5a", //yellow4
                "#7a7977", //blue5
                "#fde345", //yellow5
            ]
        }

    },
    methods: {
        updateBarPlot(url, params, stacked) {
            return new Promise((resolve, reject) => {
            axios.get(webAppRoot + url, {
                params: params
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        if (stacked) {
                            this.buildHorizontalStackedBarPlot(response);
                        }
                        else {
                            this.buildHorizontalBarPlot(response);
                        }
                        resolve({
                            success: true
                        });
                    }
                    else if (response.data.isAllowed && !response.data.success) {
                        resolve({
                            success: false
                        });
                    }
                    else {
                        this.handleDialogs(response.data, this.updateBarPlot.bind(null, url, params));
                    }
                })
                .catch(error => {
                    console.log(error);
                    reject({
                        success: false
                    });
                });
            });
        },
        buildHorizontalStackedBarPlot(response) {
            var chartData = response.data;
            var traces = chartData.traces;
            for (var i = 0; i < traces.length; i++) {
                traces[i].type = "bar";
                traces[i].orientation = "h";
                traces[i].text = traces[i].labels;
                // trace.text = trace.labels;
                var color = this.cividisColors[traces[i].name];
                if (!color) {
                    // color = this.cividisPaletteAlt[i % this.cividisPaletteAlt.length];
                    color = this.cividisColors.blue75;
                    //all same color, add a border
                    traces[i].marker = {
                        color: color,
                        // line: {
                        //     color: 'white',
                        //     width: 2
                        // }
                    };
                }
                else {
                    traces[i].marker = {
                        color: color
                    };
                }
            }
            var data = traces;
            var layout = {
                title: chartData.plotTitle,
                barmode: 'stack',
                hovermode: 'closest',
                font: {
                    family: 'Roboto,sans-serif',
                },
                xaxis: {automargin: true, title: this.createXAxisTitle(chartData.plotId),
                    zeroline: false,},
                yaxis: {automargin: true, 
                    title: {text: this.createYAxisTitle(chartData.plotId), standoff: 10},
                    ticks: 'outside',},
                margin: {
                    t: this.getPlotTopMargin()
                },
                height: 400,
                legend: {
                    traceorder: 'normal'
                },
            }
            this.$nextTick(() => {
                Plotly.newPlot(chartData.plotId, data, layout, this.plotlyConfig).then(
                    () => {
                        var traces = Plotly.d3.select("#" +chartData.plotId +" g.legend")
                        .selectAll("g.traces");

                        if (chartData.hideLegendMarkers) {
                            var legendMarkers = Plotly.d3.selectAll("#" +chartData.plotId + " g.legendpoints")[0];
                            for (var i = 0; i < legendMarkers.length; i++) {
                                legendMarkers[i].style.visibility = "hidden";
                            }
                        }
                        
                        traces.on("mouseover", (d, index) => { 
                            // console.log(d[0].trace.name, d[0].trace, index); 
                            // var update = { opacity: 0.1}; 
                            var otherIndices = traces[0].map( (t, i) => i).filter(i => i != index);
                            var update = { visible: "legendonly"}; 
                            Plotly.restyle(chartData.plotId, update, otherIndices);
                            // Plotly.deleteTraces(chartData.plotId, otherIndices);
                        }
                        );
                        traces.on("mouseleave", (d, index) => { 
                            // console.log(d[0].trace.name, d[0].trace, index); 
                            // var update = { opacity: 1}; 
                            var update = { visible: true}; 
                            // var otherIndices = traces.map( t, i => i).filter(i != index);
                            // Plotly.restyle(chartData.plotId, update, otherIndices);
                            Plotly.restyle(chartData.plotId, update);
                        }
                        );
                    }
                );
            });
        },
        buildHorizontalBarPlot(response) {
            var chartData = response.data;
            var trace = chartData.trace;
            trace.type = "bar";
            trace.orientation = "h";
            trace.text = trace.labels;
            trace.marker = { color: this.cividisColors.blue75};
            trace.hovertemplate = "%{text}<extra></extra>"
            var data = [trace];
            var layout = {
                title: chartData.plotTitle,
                hovermode: 'closest',
                font: {
                    family: 'Roboto,sans-serif',
                },
                xaxis: {automargin: true, title: this.createXAxisTitle(chartData.plotId)},
                yaxis: {automargin: true, ticks: 'outside', title: {text: this.createYAxisTitle(chartData.plotId), standoff: 10}},
                margin: {
                    t: this.getPlotTopMargin()
                },
                height: 350,
                colorscale: "Viridis"
            }
            this.$nextTick(() => {
                Plotly.newPlot(chartData.plotId, data, layout, this.plotlyConfig);
            });
        },
        updateLolliplotPlot(plotId, url, currentGene) {
            return new Promise((resolve, reject) => {
            axios.get(webAppRoot + url, {
                params: {
                    hugoSymbol: currentGene,
                    plotId: plotId,
                }
            })
                .then(response => {
                    if (response.data.isAllowed && response.data.success) {
                        var chartData = response.data.payload;
                        var trace = chartData.trace;
                        if (!trace) {
                            resolve({success: false});
                            return;
                        }
                        trace.type = "scattergl";
                        trace.mode = 'markers';
                        trace.marker = { size: 9, color: this.cividisColors.blue75};
                        trace.text = trace.labels;
                        trace.yaxis = "y";
                        trace.opacity = 0.8;
                        trace.hovertemplate = "%{text}<extra></extra>"
                        var data = [];
                        var shapes = [];
                        var lineTraces = trace.x.map((x,index) => ({
                            type: 'scattergl',
                            mode: "lines",
                            x: [x, x],
                            y: [0, trace.y[index]],
                            line: {
                                color:'grey',
                                width: 1
                            },
                            hoverinfo:'skip'
                        }
                            )
                        );
                        data.push(...lineTraces);
                        data.push(trace);
                        var annotations = [];
                        var y1Even = chartData.maxY * -0.1;
                        var y0Even = y1Even * 0.1;
                        var height = Math.abs(y1Even - y0Even);
                        var y1Odd = y1Even - height;
                        var y0Odd = y1Odd + height;
                        for (var i =0; i < chartData.underlineTraces[0].x.length; i++) {
                            var shape = {
                                type: 'rect',
                            xref: 'x',
                            yref: 'y',
                            x0: chartData.underlineTraces[0].start[i],
                            y0: i % 2 == 0 ? y0Even : y0Odd,
                            x1: chartData.underlineTraces[0].end[i],
                            y1: i % 2 == 0 ? y1Even : y1Odd,
                            fillcolor: '#f9e04a',
                            opacity: 0.7,
                            line: {
                                width: 0
                            },
                            };
                            shapes.push(shape);

                            let annotation = {
                                showarrow: false,
                                text: chartData.annotations[i],
                                align: "center",
                                x: chartData.underlineTraces[0].x[i],
                                xanchor: "center",
                                y: i % 2 == 0 ? y0Even : y0Odd,
                                yanchor: "top",
                                font: {
                                    // color: "white"
                                }
                            }
                            annotations.push(annotation);
                        }
                        var bottomTrace = chartData.underlineTraces[0];
                        bottomTrace.type = "scatter";
                        bottomTrace.yaxis = "y";
                        bottomTrace.y = bottomTrace.x.map((p, index) => index % 2 == 0 ? y0Even - height / 2 : y0Odd - height / 2);
                        bottomTrace.mode = "markers";
                        bottomTrace.text = bottomTrace.labels;
                        bottomTrace.hovertemplate = "%{text}<extra></extra>";
                        bottomTrace.marker = {
                            color: '#f9e04a'
                        }
                        bottomTrace.opacity = 0;
                        data.push(bottomTrace);

                        var layout = {
                            title: chartData.plotTitle,
                            hovermode: 'closest',
                            font: {
                                family: 'Roboto,sans-serif',
                            },
                            xaxis: {
                                title: this.createXAxisTitle(chartData.plotId)
                            },
                            yaxis: {
                                title: {text: this.createYAxisTitle(chartData.plotId)},
                            },
                            margin: {
                                t: this.getPlotTopMargin()
                            },
                            height: 350,
                            showlegend: false,
                            shapes: shapes,
                            annotations: annotations,
                               
                        }
                        this.$nextTick(() => {
                            Plotly.newPlot(chartData.plotId, data, layout, this.plotlyConfig);
                            //need to hide tick labels below 0. Use d3
                            var axisTickLabels = Plotly.d3.selectAll("#" + chartData.plotId + " .yaxislayer-above").selectAll('text')[0];
                            for (var i = 0; i < axisTickLabels.length; i++) {
                                if (isNaN(axisTickLabels[i].innerHTML.substring(0, 1))) {
                                    axisTickLabels[i].innerHTML = " ";
                                }
                            }
                        });
                        resolve({
                            success: true
                        });
                    }
                    else if (response.data.isAllowed && !response.data.success) {
                        resolve({
                            success: false
                        });
                    }
                    else {
                        this.handleDialogs(response.data, this.updateLolliplotPlot.bind(null, plotId, url));
                    }
                })
                .catch(error => {
                    console.log(error);
                    reject({
                        success: false
                    });
                });
            });
        },
        getPlotTopMargin() {
            return this.standalone ? 30 : null;
        },
        createXAxisTitle(plotId) {
            var genericPlotId = plotId.split("_")[0];
            switch(genericPlotId) {
                case "alterationByCancerPlot": return "Number of Variants";
                case "cancerByPercentPlot": return "Percent of cases";
                case "genieLollipopPlot": return "Amino Acid Position";
                case "mutatedGenesCancerPlot": return "Number of Variants";
                case "codonPlot": return "Number of Cases";
                case "cnvAbsPlot": return "Number of Cases";
                case "cnvPctPlot": return "Percent of Cases";
                case "fusionAbsPlot": return "Number of Cases";
                case "fusionPctPlot": return "Percent of Cases";
                case "fusionBreakpointFivePlot": return "Number of Annotated Breakpoint";
                case "fusionBreakpointThreePlot": return "Number of Annotated Breakpoint";

            }
        },
        createYAxisTitle(plotId) {
            var genericPlotId = plotId.split("_")[0];
            switch(genericPlotId) {
                case "alterationByCancerPlot": return "Cancer Type";
                case "cancerByPercentPlot": return "Cancer Type";
                case "genieLollipopPlot": return "Variant Count";
                case "mutatedGenesCancerPlot": return "Gene";
                case "codonPlot": return "Mutant AA";
                case "cnvAbsPlot": return "Cancer Type";
                case "cnvPctPlot": return "Cancer Type";
                case "fusionAbsPlot": return "Cancer Type";
                case "fusionPctPlot": return "Cancer Type";
                case "fusionBreakpointFivePlot": return "Breakpoint";
                case "fusionBreakpointThreePlot": return "Breakpoint";
            }
        },
        handleDialogs(response, callback) {
            this.$emit("handle-dialogs", [this, response, callback]);
        },
    },
    mounted() {
    },
    created: function () {
    },
    destroyed: function () {
    },
    watch: {
    }


});