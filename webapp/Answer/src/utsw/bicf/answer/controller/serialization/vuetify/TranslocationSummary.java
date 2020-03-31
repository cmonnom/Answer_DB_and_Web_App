package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utsw.bicf.answer.controller.serialization.HeaderAdditionalData;
import utsw.bicf.answer.controller.serialization.Units;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.AnnotatorSelection;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Translocation;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.HeaderOrder;
import utsw.bicf.answer.model.hybrid.TranslocationRow;

public class TranslocationSummary extends Summary<TranslocationRow> {
	
	public TranslocationSummary(ModelDAO modelDAO, OrderCase aCase, String uniqueIdField, List<HeaderOrder> ftlOrders, User currentUser) {
		super(createRows(modelDAO, aCase, currentUser), uniqueIdField, ftlOrders, modelDAO);
	}

	private static List<TranslocationRow> createRows(ModelDAO modelDAO, OrderCase aCase, User currentUser) {
		List<TranslocationRow> rows = new ArrayList<TranslocationRow>();
		List<User> allUsers = modelDAO.getAllUsers();
		//deal with duplicate initials
		List<String> uniqueColumnNames = new ArrayList<String>();
		int counter = 1;
 		for (User u : allUsers) {
			if (uniqueColumnNames.contains(u.getFullName())) {
				u.setFullName(u.getFullName() + "-" + counter);
				u.setLast(u.getLast() + "-" + counter);
				counter++;
			}
			uniqueColumnNames.add(u.getFullName());
		}
 		
		for (Translocation translocation : aCase.getTranslocations()) {
			
			//populate selection from other annotators
			Map<Integer, AnnotatorSelection> selectionPerAnnotator = new HashMap<Integer, AnnotatorSelection>();
			if (translocation.getAnnotatorSelections() != null) {
				translocation.setSelected(false);
				for (Integer userId : translocation.getAnnotatorSelections().keySet()) {
					boolean isSelected = translocation.getAnnotatorSelections().get(userId) != null && translocation.getAnnotatorSelections().get(userId);
					if (isSelected) {
						String date = translocation.getAnnotatorDates().get(userId);
						SNPIndelVariantSummary.addAnotatorSelection(selectionPerAnnotator, allUsers, aCase, userId, date);
						if (userId.equals(currentUser.getUserId())) {
							translocation.setSelected(true); //this is the selection of the current user
						}
					}
				}
			}
			
			rows.add(new TranslocationRow(translocation, selectionPerAnnotator));
		}
		return rows;
	}

	@Override
	public void initializeHeaders() {
		Map<String, HeaderAdditionalData> annotatorInitials = new HashMap<String, HeaderAdditionalData>();
		for (TranslocationRow row : items) {
			for (Integer userId : row.getSelectionPerAnnotator().keySet()) {
				AnnotatorSelection as = row.getSelectionPerAnnotator().get(userId);
				SNPIndelVariantSummary.extractAnnotatorInitials(annotatorInitials, as, userId);
			}
		}
		SNPIndelVariantSummary.createAnnotatorHeaders(headers, annotatorInitials);
		
		Header fusionName = new Header(new String[] {"Fusion", "Name"}, "fusionName");
		fusionName.setWidth("150px");
		fusionName.setIsSafe(true);
		headers.add(fusionName);
		
		Header iconFlags = new Header("Flags", "iconFlags");
		iconFlags.setWidth("100px");
		iconFlags.setIsFlag(true);
		iconFlags.setSortable(false);
		iconFlags.setIsSafe(true);
		headers.add(iconFlags);
		
		Header filters = new Header("QC Tags", "filters");
//		leftGene.setWidth("100px");
		filters.setIsSafe(true);
		headers.add(filters);
		
		Header ftlSomaticStatus = new Header(new String[] {"Somatic", "Status"}, "ftlSomaticStatus");
//		leftGene.setWidth("100px");
		ftlSomaticStatus.setIsSafe(true);
		headers.add(ftlSomaticStatus);
		
		Header leftGene = new Header(new String[] {"Left", "Gene"}, "leftGene");
//		leftGene.setWidth("100px");
		leftGene.setIsSafe(true);
		headers.add(leftGene);
		
		Header rightGene = new Header(new String[] {"Right", "Gene"}, "rightGene");
//		rightGene.setWidth("100px");
		rightGene.setIsSafe(true);
		headers.add(rightGene);
		
		Header leftExon = new Header(new String[] {"Left", "Exons"}, "leftExons");
//		leftExon.setWidth("100px");
		leftExon.setIsSafe(true);
		headers.add(leftExon);
		
		Header rightExon = new Header(new String[] {"Right", "Exons"}, "rightExons");
//		rightExon.setWidth("100px");
		rightExon.setIsSafe(true);
		headers.add(rightExon);
		
		Header chrDistance = new Header(new String[] {"Chromosome", "Distance"}, "chrDistance");
		chrDistance.setWidth("100px");
		chrDistance.setIsSafe(true);
		headers.add(chrDistance);
		
		Header chrType = new Header(new String[] {"Chromosome", "Type"}, "chrType");
		chrType.setIsSafe(true);
		headers.add(chrType);
		
		Header leftBreakpoint = new Header(new String[] {"Left", "Breakpoint"}, "leftBreakpoint");
		leftBreakpoint.setWidth("100px");
		leftBreakpoint.setIsSafe(true);
		headers.add(leftBreakpoint);
		
		Header rightBreakpoint = new Header(new String[] {"Right", "Breakpoint"}, "rightBreakpoint");
		rightBreakpoint.setWidth("100px");
		rightBreakpoint.setIsSafe(true);
		headers.add(rightBreakpoint);
		
		Header leftStrand = new Header(new String[] {"Left", "Strand"}, "leftStrand");
		leftStrand.setWidth("100px");
		leftStrand.setIsSafe(true);
		headers.add(leftStrand);
		
		Header rightStrand = new Header(new String[] {"Right", "Strand"}, "rightStrand");
		rightStrand.setWidth("100px");
		rightStrand.setIsSafe(true);
		headers.add(rightStrand);
		
		Header rnaReads = new Header(new String[] {"RNA", "Reads"}, "rnaReads");
		rnaReads.setWidth("100px");
		rnaReads.setIsSafe(true);
		headers.add(rnaReads);
		
		Header dnaReads = new Header(new String[] {"DNA", "Reads"}, "dnaReads");
//		dnaReads.setWidth("100px");
		dnaReads.setIsSafe(true);
		headers.add(dnaReads);
		
		Header normalDnaReads = new Header(new String[] {"Normal", "DNA Reads"}, Variant.FIELD_FTL_NORMAL_DNA_READS);
//		dnaReads.setWidth("100px");
		normalDnaReads.setIsSafe(true);
		headers.add(normalDnaReads);
		
		
		Header fusionType = new Header(new String[] {"Fusion", "Type"}, "fusionType");
		fusionType.setWidth("100px");
		fusionType.setIsSafe(true);
		headers.add(fusionType);
		
		Header annotations = new Header("Annotations", "annotations");
		annotations.setWidth("100px");
		annotations.setIsSafe(true);
		headers.add(annotations);
		
		Header percentSupportingReads = new Header(new String[] {"Alt", "Percent"}, "percentSupportingReads");
		percentSupportingReads.setIsSafe(true);
		percentSupportingReads.setIsHidden(true);
		percentSupportingReads.setUnit(Units.PCT);
		headers.add(percentSupportingReads);
		
	}
	
}
