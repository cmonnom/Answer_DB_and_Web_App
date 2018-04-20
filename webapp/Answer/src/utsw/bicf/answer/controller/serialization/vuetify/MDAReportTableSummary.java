//package utsw.bicf.answer.controller.serialization.vuetify;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import utsw.bicf.answer.model.OrderCase;
//import utsw.bicf.answer.model.VariantSelected;
//import utsw.bicf.answer.model.hybrid.MDAReportTableRow;
//import utsw.bicf.answer.reporting.finalreport.GeneVariantDetails;
//import utsw.bicf.answer.reporting.finalreport.TreatmentOption;
//import utsw.bicf.answer.controller.serialization.ToolTip;
//import utsw.bicf.answer.controller.serialization.vuetify.Header;
//import utsw.bicf.answer.dao.ModelDAO;
//
//public class MDAReportTableSummary extends Summary<MDAReportTableRow> {
//
//	public MDAReportTableSummary(ModelDAO modelDAO, OrderCase aCase, List<TreatmentOption> treatmentOptions, Map<String, GeneVariantDetails> geneVariantDetails, String uniqueIdField) {
//		super(createMDARows(modelDAO, aCase, treatmentOptions, geneVariantDetails), uniqueIdField);
//	}
//
//	private static List<MDAReportTableRow> createMDARows(ModelDAO modelDAO, OrderCase aCase, List<TreatmentOption> treatmentOptions, Map<String, GeneVariantDetails> geneVariantDetails) {
//		List<VariantSelected> variantsSelected = modelDAO.getAllVariantsSelectedByCase(aCase);
//		List<MDAReportTableRow> mdaRows = new ArrayList<MDAReportTableRow>();
//		for (TreatmentOption option : treatmentOptions) {
//			boolean isSelected = false;
//			GeneVariantDetails gvDetailsForThisGeneVariant = geneVariantDetails.get(option.getGene() + option.getAberration());
//			for (VariantSelected vSelected : variantsSelected) {
//				if (vSelected.getGeneAndVariant().equals(option.getGene() + option.getAberration())) {
//					isSelected = true;
//				}
//			}
//			mdaRows.add(new MDAReportTableRow(option, gvDetailsForThisGeneVariant, isSelected));
//		}
//		
//		
//		return mdaRows;
//	}
//
//	@Override
//	public void initializeHeaders() {
//		Header geneDetails = new Header("Gene Details", "geneDetailsValue");
//		geneDetails.setIsActionable(true);
//		headers.add(geneDetails);
//		Header alleleFrequency = new Header(new String[] {"Tumor", "Allele Frequency"}, "alleleFrequency");
//		alleleFrequency.setWidth("50px");
//		headers.add(alleleFrequency);
//		Header clinicalTrials = new Header(new String[] {"Clinical", "Trials"}, "clinicalTrials");
//		clinicalTrials.setWidth("50px");
//		headers.add(clinicalTrials);
//		//keep in the same order
//		headerOrder = headers.stream().map(aHeader -> aHeader.getValue()).collect(Collectors.toList());
//		
//		
//	}
//
//}
