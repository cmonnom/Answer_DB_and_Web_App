Vue.component('bam-viewer', {
	props: {
	},
	template: `<div id="igv-div"></div>`,
	data() {
		return {
			browser: null,
			locus: "",
			bam: "",
			bai: "",
			label: ""

		}

	},
	methods: {
		startIGV() {
			var div = document.getElementById("igv-div");
			var options =
				{
					showNavigation: true,
					showRuler: true,
					genome: "hg38",
					locus: this.locus,
					tracks:
						[
							// {
							//   name: "Genes",
							//   type: "annotation",
							//   format: "bed",
							//   sourceType: "file",
							//   url: "https://s3.amazonaws.com/igv.broadinstitute.org/annotations/hg19/genes/refGene.hg19.bed.gz",
							//   indexURL: "https://s3.amazonaws.com/igv.broadinstitute.org/annotations/hg19/genes/refGene.hg19.bed.gz.tbi",
							//   order: Number.MAX_VALUE,
							//   visibilityWindow: 300000000,
							//   displayMode: "EXPANDED"
							// },
							{
								// url: 'https://data.broadinstitute.org/igvdata/1KG/b37/data/HG02450/alignment/HG02450.mapped.ILLUMINA.bwa.ACB.low_coverage.20120522.bam',
								url: this.bam,
								indexURL: this.bai,
								// indexed: true,
								label: this.label
							}
						]
				};
			this.browser = igv.createBrowser(div, options);
			// this.updateBootstrapIcons();
		},
		openIGV(locus, bam, bai, label) {
			this.locus = locus;
			this.bam = bam;
			this.bai = bai;
			this.label = label;
			if (!this.browser) {
				this.startIGV();
			}
		},
		closeIGV() {
			this.browser = null;
		}
	},
	created: function () {
		// this.startIGV();
	},
	destroyed: function() {
		this.browser = null;
	},
	watch: {
	}


});