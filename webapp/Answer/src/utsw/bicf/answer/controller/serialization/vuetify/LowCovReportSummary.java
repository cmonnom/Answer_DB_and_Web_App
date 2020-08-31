package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;

import utsw.bicf.answer.model.hybrid.SampleLowCoverageFromQC;

public class LowCovReportSummary extends Summary<SampleLowCoverageFromQC> {
	
	public LowCovReportSummary() {
		super();
	}
	
	public LowCovReportSummary(List<SampleLowCoverageFromQC> lowCovs, String uniqueIdField) {
		super(lowCovs, uniqueIdField, null);
	}

	@Override
	public void initializeHeaders() {
		Header combined = new Header("Loci", "locus");
		combined.setWidth("200px");
		combined.setIsSafe(true);
		headers.add(combined);
		Header gene = new Header("Gene", "gene");
		gene.setWidth("100px");
		gene.setIsSafe(true);
		headers.add(gene);
		Header exonNb = new Header("Exon #", "exonNb");
		exonNb.setWidth("100px");
		exonNb.setIsSafe(true);
		headers.add(exonNb);
		Header min = new Header(new String[] {"Min", "Depth"}, "minDepth");
		min.setWidth("100px");
		min.setIsSafe(true);
		headers.add(min);
		Header max = new Header(new String[] {"Max", "Depth"}, "maxDepth");
		max.setWidth("100px");
		max.setIsSafe(true);
		headers.add(max);
		Header median = new Header(new String[] {"Median", "Depth"}, "medianDepth");
		median.setWidth("100px");
		median.setIsSafe(true);
		headers.add(median);
		Header avg = new Header(new String[] {"Avg", "Depth"}, "avgDepth");
		avg.setWidth("100px");
		avg.setIsSafe(true);
		headers.add(avg);
		
		
	}
	

}
