package utsw.bicf.answer.controller.serialization.vuetify;
//package utsw.bicf.answer.controller.serialization.vuetify;
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import utsw.bicf.answer.controller.serialization.ToolTip;
//import utsw.bicf.answer.dao.ModelDAO;
//import utsw.bicf.answer.model.hybrid.LowCoverageTable;
//import utsw.bicf.answer.controller.serialization.vuetify.Header;
//import utsw.bicf.answer.controller.serialization.vuetify.Summary;
//
////JSON Object with
////headerOrder: array of the headers in the order we want them displayed
////items: array of json objects containing the data with the keys matching the header's value property
////headers: array of json objects (value, text)
//public class LowCoverageTableSummary extends Summary<LowCoverageTable>{
//	
//	public LowCoverageTableSummary(String genes, int threshold, String uniqueIdField, ModelDAO modelDAO) {
//		super(createLowCovRows(genes, threshold, modelDAO), uniqueIdField);
//	}
//	
//	private static List<LowCoverageTable> createLowCovRows(String genes, int threshold, ModelDAO modelDAO) {
//		List<LowCoverageTable> lowCovs = new ArrayList<LowCoverageTable>();
//		if (genes != null && genes.length() > 0) {
//			String[] geneList = genes.split("[^A-Za-z0-9_-]");
//			Set<String> uniqGenes = new HashSet<String>();
//			for (String gene : geneList) {
//				gene = gene.trim();
//				if (gene.length() > 0) {
//					uniqGenes.add(gene.toUpperCase());
//				}
//			}
//			lowCovs = modelDAO.getLowCoverageSummaryData(uniqGenes, threshold);
//		}
//		return lowCovs;
//	}
//
//	@Override
//	public void initializeHeaders() {
//		headers.add(new Header("Genes", "geneSymbol"));
//		headers.add(new Header("# Low Cov Exons", "exonSize", new ToolTip("Total number of low cov exons accross all samples"), false));
//		headers.add(new Header("# Low Cov Samples", "sampleSize", new ToolTip("Total number of samples with low cov exons"), false));
//		headers.add(new Header("% Low Cov Samples", "pctSampleFormatted", new ToolTip("Low cov samples VS any sample"), false));
//		
//		//keep in the same order
//		headerOrder = headers.stream().map(header -> header.getValue()).collect(Collectors.toList());
//		
//	}
//
//
//}
