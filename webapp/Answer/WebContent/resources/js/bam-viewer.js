document.addEventListener("DOMContentLoaded", function () {
    var igvDiv,
        options;
    options =
        {
            showNavigation: true,
            showRuler: true,
            reference: {
                id: "hg38",
                fastaURL: "https://s3.amazonaws.com/igv.broadinstitute.org/genomes/seq/hg38/hg38.fa",
                cytobandURL: "https://s3.amazonaws.com/igv.broadinstitute.org/annotations/hg38/cytoBandIdeo.txt"
            },
            locus: locus,
            tracks:
                [
                    {
                        url: 'https://s3.amazonaws.com/igv.broadinstitute.org/data/hg38/gencode.v24.annotation.sorted.gtf.gz',
                        indexURL: 'https://s3.amazonaws.com/igv.broadinstitute.org/data/hg38/gencode.v24.annotation.sorted.gtf.gz.tbi',
                        name: 'Gencode v24',
                        format: 'gtf',
                        type: "annotation",
                        visibilityWindow: 10000000
                    },
                    {
                        url: urlRoot + bam,
                        indexURL: urlRoot + bai,
                        label: label
                    },
                    {
                        url: urlRoot + "ORD534-27-3002_T_DNA_panel1385.seg",
                        label: "seg"
                    }
                ]
        };
    igvDiv = document.getElementById("igv-div");
    igv.createBrowser(igvDiv, options);
});

$( window ).unload(function() {
    window.opener.bus.$emit("bam-viewer-closed", null);
});