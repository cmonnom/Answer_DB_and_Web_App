var tracks = [{
    url: 'https://s3.amazonaws.com/igv.broadinstitute.org/data/hg38/gencode.v24.annotation.sorted.gtf.gz',
    indexURL: 'https://s3.amazonaws.com/igv.broadinstitute.org/data/hg38/gencode.v24.annotation.sorted.gtf.gz.tbi',
    name: 'Gencode v24',
    format: 'gtf',
    type: "annotation",
    visibilityWindow: 10000000
}];

if (normalBam) {
    tracks.push( {
        url: urlRoot + normalBam,
        indexURL: urlRoot + normalBai,
        label: normalLabel
    });
}
if (tumorBam) {
    tracks.push({
        url: urlRoot + tumorBam,
        indexURL: urlRoot + tumorBai,
        label: tumorLabel
    });
}
if (rnaBam) {
    tracks.push({
        url: urlRoot + rnaBam,
        indexURL: urlRoot + rnaBai,
        label: rnaLabel
    });
}

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
            tracks: tracks
        };
    igvDiv = document.getElementById("igv-div");
    igv.createBrowser(igvDiv, options);
});

$( window ).unload(function() {
    window.opener.bus.$emit("bam-viewer-closed", null);
});


