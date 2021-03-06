package utsw.bicf.answer.controller.serialization.vuetify;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;
import utsw.bicf.answer.controller.serialization.HeaderAdditionalData;
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

	public static List<SNPIndelVariantRow> createRows(ModelDAO modelDAO, OrderCase aCase, 
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
			//populate selection from other annotators
			Map<Integer, AnnotatorSelection> selectionPerAnnotator = new HashMap<Integer, AnnotatorSelection>();
			if (variant.getAnnotatorSelections() != null) {
				variant.setSelected(false);
				for (Integer userId : variant.getAnnotatorSelections().keySet()) {
					boolean isSelected = variant.getAnnotatorSelections().get(userId) != null && variant.getAnnotatorSelections().get(userId);
					if (isSelected) {
						String date = variant.getAnnotatorDates().get(userId);
						SNPIndelVariantSummary.addAnotatorSelection(selectionPerAnnotator, allUsers, aCase, userId, date);
						if (userId.equals(currentUser.getUserId())) {
							variant.setSelected(true); //this is the selection of the current user
						}
					}
				}
			}
			rows.add(new SNPIndelVariantRow(variant, reportGroups, aCase.getTotalCases(), selectionPerAnnotator));
		}
		return rows;
	}
	
	@Override
	public void initializeHeaders() {
		
		Map<String, HeaderAdditionalData> annotatorInitials = new HashMap<String, HeaderAdditionalData>();
		for (SNPIndelVariantRow row : items) {
			for (Integer userId : row.getSelectionPerAnnotator().keySet()) {
				AnnotatorSelection as = row.getSelectionPerAnnotator().get(userId);
				SNPIndelVariantSummary.extractAnnotatorInitials(annotatorInitials, as, userId);
			}
		}
		SNPIndelVariantSummary.createAnnotatorHeaders(headers, annotatorInitials);
		
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
		Header highestTier = new Header(new String[] {"Highest", "Tier"}, "highestTier");
		highestTier.setAlign("right");
		highestTier.setIsSafe(true);
		headers.add(highestTier);
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

	public static void createAnnotatorHeaders(List<Header> headers, Map<String, HeaderAdditionalData> annotatorInitials) {
		for (String annotator : annotatorInitials.keySet()) {
			HeaderAdditionalData data = annotatorInitials.get(annotator);
			Header annHeader = new Header(new String[] {data.getFirstName(), data.getLastName()}, "dateSince" + data.getUserId());
			annHeader.setIsSafe(true);
			annHeader.setMap(true);
			annHeader.setToolTip(data.getTooltip());
			annHeader.setMapTo(data.getUserId() + "");
//			annHeader.setWidth("65px");
			headers.add(annHeader);
		}
	}

	public static void extractAnnotatorInitials(Map<String, HeaderAdditionalData> annotatorInitials, AnnotatorSelection as,
			Integer userId) {
		//need to guarantee uniqueness of header. For now, append the userId.
		//TODO find a better way to handle same initials. 
		//Maybe scan through first to see if any initials are the same
		//and append a number to those only
		HeaderAdditionalData data = new HeaderAdditionalData();
		data.setTooltip(new ToolTip(as.getUserFullName() + "'s selection"));
		data.setUserId(userId);
		data.setFirstName(as.getFirstName());
		data.setLastName(as.getLastName());
		annotatorInitials.put(as.getUserFullName(),data);
	}

	/**
	 * Method used by SNP, CNV and FTL to create AnnotatorSelection s if applicable
	 * @param selectionPerAnnotator
	 * @param allUsers
	 * @param aCase
	 * @param userId
	 * @param date
	 */
	public static void addAnotatorSelection(Map<Integer, AnnotatorSelection> selectionPerAnnotator, List<User> allUsers,
			OrderCase aCase, Integer userId, String date) {
		for (User u : allUsers) {
			if (u.getUserId().equals(userId)) {
				AnnotatorSelection s = new AnnotatorSelection();
				s.setUserId(userId);
				s.setUserFullName(u.getFullName());
				s.setUserInitials(u.getInitials());
				s.setFirstName(u.getFirst());
				s.setLastName(u.getLast());
				s.setDate(date);
				s.setSelected(true);
				if (s.getDate() != null) {
					OffsetDateTime dateUTCDatetime = OffsetDateTime.parse(s.getDate(), DateTimeFormatter.ISO_DATE_TIME);
					boolean ownsTheCase = aCase.getCaseOwner() != null && userId.toString().equals(aCase.getCaseOwner());
					s.setSelectedSince(TypeUtils.buildDateSinceChip(TypeUtils.dateSince(dateUTCDatetime), ownsTheCase));
				}
				else {
					s.setSelectedSince("Unknown");
				}
				selectionPerAnnotator.put(s.getUserId(), s);
				break;
			}
		}
		
	}
	
}
