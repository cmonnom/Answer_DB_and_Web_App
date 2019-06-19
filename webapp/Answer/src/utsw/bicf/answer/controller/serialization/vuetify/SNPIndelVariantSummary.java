package utsw.bicf.answer.controller.serialization.vuetify;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;
import utsw.bicf.answer.controller.serialization.ToolTip;
import utsw.bicf.answer.controller.serialization.Units;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.AnnotatorSelection;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.HeaderOrder;
import utsw.bicf.answer.model.hybrid.ReportGroupForDisplay;
import utsw.bicf.answer.model.hybrid.SNPIndelVariantRow;

public class SNPIndelVariantSummary extends Summary<SNPIndelVariantRow> {
	
	public SNPIndelVariantSummary(ModelDAO modelDAO, OrderCase aCase, String uniqueIdField
			, List<ReportGroupForDisplay> reportGroups, List<HeaderOrder> headerOrders, User currentUser) {
		super(createRows(modelDAO, aCase, reportGroups, headerOrders, currentUser), uniqueIdField, headerOrders, modelDAO);
	}

	private static List<SNPIndelVariantRow> createRows(ModelDAO modelDAO, OrderCase aCase, 
			List<ReportGroupForDisplay> reportGroups, List<HeaderOrder> headerOrders, User currentUser) {
		List<SNPIndelVariantRow> rows = new ArrayList<SNPIndelVariantRow>();
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
		for (Variant variant : aCase.getVariants()) {
			
			//TODO for testing only
			addTestingAnnotators(variant);
			
			//populate selection from other annotators
			Map<Integer, AnnotatorSelection> selectionPerAnnotator = new HashMap<Integer, AnnotatorSelection>();
			if (variant.getAnnotatorSelections() != null) {
				variant.setSelected(false);
				for (Integer userId : variant.getAnnotatorSelections().keySet()) {
					boolean isSelected = variant.getAnnotatorSelections().get(userId) != null && variant.getAnnotatorSelections().get(userId);
					String date = variant.getAnnotatorDates().get(userId);
					if (isSelected) {
						if (!userId.equals(currentUser.getUserId())) { //skip current user
							for (User u : allUsers) {
								if (u.getUserId().equals(userId)) {
									AnnotatorSelection s = new AnnotatorSelection();
									s.setUserId(userId);
									s.setUserFullName(u.getFullName());
									s.setUserInitials(u.getInitials());
									s.setFirstName(u.getFirst());
									s.setLastName(u.getLast());
									s.setDate(date);
									if (s.getDate() != null) {
										OffsetDateTime dateUTCDatetime = OffsetDateTime.parse(s.getDate(), DateTimeFormatter.ISO_DATE_TIME);
										boolean ownsTheCase = aCase.getCaseOwner() != null && userId.toString().equals(aCase.getCaseOwner());
										s.setSelectedSince(buildDateSinceChip(TypeUtils.dateSince(dateUTCDatetime), ownsTheCase));
									}
									else {
										s.setSelectedSince("Unknown");
									}
									selectionPerAnnotator.put(s.getUserId(), s);
									break;
								}
//								if (u.getUserId().equals(currentUser.getUserId())) {
//									variant.setSelected(true); //select current user's variants
//								}
							}
							
						}
						else if (userId.equals(currentUser.getUserId())) {
							variant.setSelected(true); //this is the selection of the current user
						}
					}
				}
			}
			rows.add(new SNPIndelVariantRow(variant, reportGroups, aCase.getTotalCases(), selectionPerAnnotator));
		}
		return rows;
	}
	
	/**
	 * Since the column is "trusted" Vuetify will interpret the html tags
	 * This is kind of a hack but easier to manage than doing it as a special case in data-tables
	 * @param dateSince
	 * @param isReviewer 
	 * @return
	 */
	private static String buildDateSinceChip(String dateSince, Boolean isReviewer) {
		StringBuilder sb = new StringBuilder();
		String chipColor = (isReviewer != null && isReviewer) ? "green" : "grey";
		sb.append("<span tabindex='-1' class='chip chip--disabled chip--label ").append(chipColor).append(" chip--small white--text'><span class='chip__content'>")
        .append("<i aria-hidden='true' class='icon material-icons mdi mdi-checkbox-marked'></i>") 
        .append("<span class='pl-2'>").append(dateSince).append("</span></span></span>");
		return sb.toString();
	}

	private static void addTestingAnnotators(Variant variant) {
		if (variant.getMongoDBId().getOid().equals("5c47a25f5b19805aabc1d47b")
				|| variant.getMongoDBId().getOid().equals("5c47a25f5b19805aabc1d47c")) {
//			List<AnnotatorSelection> selections = new ArrayList<AnnotatorSelection>();
//				AnnotatorSelection as = new AnnotatorSelection();
//				as.setSelected(true);
//				as.setUserId(1);
				OffsetDateTime dateUTCDatetime = OffsetDateTime.now();
//				as.setDate(dateUTCDatetime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
//			selections.add(as);
				variant.getAnnotatorSelections().put(1, true);
				variant.getAnnotatorDates().put(4, dateUTCDatetime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
			
//			AnnotatorSelection as2 = new AnnotatorSelection();
//			as2.setSelected(true);
//			as2.setUserId(4);
			OffsetDateTime dateUTCDatetime2 =OffsetDateTime.parse("2019-06-03T10:15:30+01:00");
//			as2.setDate(dateUTCDatetime2.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
//			selections.add(as2);
//			variant.setAnnotatorSelections(selections);
			variant.getAnnotatorSelections().put(4, true);
			variant.getAnnotatorDates().put(4, dateUTCDatetime2.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
			
//			AnnotatorSelection as3 = new AnnotatorSelection();
//			as3.setSelected(true);
//			as3.setUserId(5);
			OffsetDateTime dateUTCDatetime3 =  OffsetDateTime.now();
//			as3.setDate(dateUTCDatetime3.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
//			selections.add(as3);
//			variant.setAnnotatorSelections(selections);
			variant.getAnnotatorSelections().put(5, true);
			variant.getAnnotatorDates().put(5, dateUTCDatetime3.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
			
		}
		if (variant.getMongoDBId().getOid().equals("5c47a25f5b19805aabc1d400")) {
//			List<AnnotatorSelection> selections = new ArrayList<AnnotatorSelection>();
//			AnnotatorSelection as4 = new AnnotatorSelection();
//			as4.setSelected(true);
//			as4.setUserId(6);
			OffsetDateTime dateUTCDatetime4 = OffsetDateTime.parse("2019-06-03T10:15:30+01:00");
//			as4.setDate(dateUTCDatetime4.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
//			selections.add(as4);
//			variant.setAnnotatorSelections(selections);
			variant.getAnnotatorSelections().put(6, true);
			variant.getAnnotatorDates().put(6, dateUTCDatetime4.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
		}
	}
	
	@Override
	public void initializeHeaders() {
		
		Map<String, AdditionalData> annotatorInitials = new HashMap<String, AdditionalData>();
		for (SNPIndelVariantRow row : items) {
			for (Integer userId : row.getSelectionPerAnnotator().keySet()) {
				//need to guarantee uniqueness of header. For now, append the userId.
				//TODO find a better way to handle same initials. 
				//Maybe scan through first to see if any initials are the same
				//and append a number to those only
				AdditionalData data = new AdditionalData();
//				data.tooltip = new ToolTip(row.getSelectionPerAnnotator().get(userId).getUserFullName());
				User u = modelDAO.getUserByUserId(userId);
				String reviewer = u.getIndividualPermission().getCanReview() != null && u.getIndividualPermission().getCanReview() ? "Case Owner " : "";
				data.tooltip = new ToolTip(reviewer + row.getSelectionPerAnnotator().get(userId).getUserFullName() + "'s selection");
				data.userId = userId;
				data.firstName = row.getSelectionPerAnnotator().get(userId).getFirstName();
				data.lastName = row.getSelectionPerAnnotator().get(userId).getLastName();
				annotatorInitials.put(row.getSelectionPerAnnotator().get(userId).getUserFullName(),data);
			}
		}
		
		for (String annotator : annotatorInitials.keySet()) {
			AdditionalData data = annotatorInitials.get(annotator);
			Header annHeader = new Header(new String[] {data.firstName, data.lastName}, "dateSince" + data.userId);
			annHeader.setIsSafe(true);
			annHeader.setMap(true);
			annHeader.setToolTip(data.tooltip);
			annHeader.setMapTo(data.userId + "");
//			annHeader.setWidth("65px");
			headers.add(annHeader);
		}
		
		Header chromPos = new Header("CHR", "chromPos");
		chromPos.setWidth("200px");
		chromPos.setAlign("right");
		chromPos.setIsSafe(true);
		headers.add(chromPos);
		Header geneVariant = new Header("Gene Variant", "geneVariant");
		geneVariant.setWidth("225px");
		geneVariant.setWidthValue(225);
		geneVariant.setIsSafe(true);
		headers.add(geneVariant);
		Header iconFlags = new Header("Flags", "iconFlags");
		iconFlags.setWidth("150px");
		iconFlags.setWidthValue(150);
		iconFlags.setIsFlag(true);
		iconFlags.setSortable(false);
		iconFlags.setAlign("left");
		iconFlags.setIsSafe(true);
		headers.add(iconFlags);
		Header exonNb = new Header("Exon #", "rank");
		exonNb.setIsSafe(true);
		headers.add(exonNb);
		Header effects = new Header("Effects", "effects");
		effects.setWidth("220px");
		effects.setWidthValue(220);
		effects.setIsSafe(true);
		headers.add(effects);
		Header ref = new Header(new String[] {"Reference", "Allele(s)"}, "reference");
		ref.setIsSafe(true);
		headers.add(ref);
		Header alt = new Header(new String[] {"Alternate", "Allele(s)"}, "alt");
		alt.setIsSafe(true);
		headers.add(alt);
		Header tumorTotalDepth = new Header(new String[] {"Tumor"," Total Depth"}, "tumorTotalDepth", Units.NB);
		tumorTotalDepth.setIsSafe(true);
		headers.add(tumorTotalDepth);
		Header taf = new Header(new String[] {"Tumor Alt", "Percent"}, "tumorAltFrequency", Units.PCT);
		taf.setWidth("100px");
		taf.setIsSafe(true);
		headers.add(taf);
//		Header tumorAltDepth = new Header(new String[] {"Tumor","Depth"}, "tumorAltDepth", Units.NB);
//		headers.add(tumorAltDepth);
		Header normalTotalDepth = new Header(new String[] {"Normal"," Total Depth"}, "normalTotalDepth", Units.NB);
		normalTotalDepth.setIsSafe(true);
		headers.add(normalTotalDepth);
		Header naf = new Header(new String[] {"Normal Alt", "Percent"}, "normalAltFrequency", Units.PCT);
		naf.setIsSafe(true);
		naf.setWidth("100px");
		headers.add(naf);
		Header delta = new Header(new String[] {"Delta", "VAF"}, "deltaTumorNormal", Units.PCT);
		delta.setIsSafe(true);
		delta.setWidth("100px");
		headers.add(delta);
//		Header normalAltDepth = new Header(new String[] {"Normal","Depth"}, "normalAltDepth", Units.NB);
//		headers.add(normalAltDepth);
		Header rnaTotalDepth = new Header(new String[] {"RNA"," Total Depth"}, "rnaTotalDepth", Units.NB);
		rnaTotalDepth.setIsSafe(true);
		headers.add(rnaTotalDepth);
		Header raf = new Header(new String[] {"RNA Alt", "Percent"}, "rnaAltFrequency", Units.PCT);
		raf.setIsSafe(true);
		raf.setWidth("100px");
		headers.add(raf);
		
		Header numCasesSeen = new Header(new String[] {"Nb Cases", "Seen"}, "numCasesSeenFormatted", Units.NB);
		numCasesSeen.setIsSafe(true);
		numCasesSeen.setWidth("50px");
		headers.add(numCasesSeen);
		
		Header numCasesInCosmic = new Header(new String[] {"Nb Cases", "In Cosmic"}, "maxCosmicPatients", Units.NB);
		numCasesInCosmic.setIsSafe(true);
		numCasesInCosmic.setWidth("50px");
		headers.add(numCasesInCosmic);
		
		Header exacAlleleFrequency = new Header(new String[] {"ExAC Allele", "Percent"}, "exacAlleleFrequency", Units.PCT);
		exacAlleleFrequency.setIsSafe(true);
		exacAlleleFrequency.setWidth("100px");
		headers.add(exacAlleleFrequency);
		
		Header somaticStatus = new Header(new String[] {"Somatic", "Status"}, "somaticStatus");
		somaticStatus.setIsSafe(true);
		somaticStatus.setWidth("100px");
		headers.add(somaticStatus);
		
		Header gnomadPopmaxAlleleFrequency = new Header(new String[] {"gnomAD Pop. Max.", "Allele Frequency"}, "gnomadPopmaxAlleleFrequency", Units.PCT);
		gnomadPopmaxAlleleFrequency.setIsSafe(true);
		gnomadPopmaxAlleleFrequency.setWidth("100px");
		headers.add(gnomadPopmaxAlleleFrequency);
		
		Header gnomadHOM = new Header(new String[] {"gnomAD ", "HOM"}, "gnomadHomozygotes", Units.NB);
		gnomadHOM.setIsSafe(true);
		gnomadHOM.setWidth("100px");
		headers.add(gnomadHOM);
		
//		Header rnaAltDepth = new Header(new String[] {"RNA","Depth"}, "rnaAltDepth", Units.NB);
//		headers.add(rnaAltDepth);
		
	}
	
	class AdditionalData {
		ToolTip tooltip;
		Integer userId;
		String firstName;
		String lastName;
		
		
	}
	
}
