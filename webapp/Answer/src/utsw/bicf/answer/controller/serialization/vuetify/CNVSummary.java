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
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.AnnotatorSelection;
import utsw.bicf.answer.model.extmapping.CNV;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.hybrid.CNVRow;
import utsw.bicf.answer.model.hybrid.HeaderOrder;

public class CNVSummary extends Summary<CNVRow> {
	
	public CNVSummary(ModelDAO modelDAO, OrderCase aCase, String uniqueIdField, 
			List<HeaderOrder> cnvOrders, User currentUser) {
		super(createRows(modelDAO, aCase, cnvOrders, currentUser), uniqueIdField, cnvOrders, modelDAO);
	}

	private static List<CNVRow> createRows(ModelDAO modelDAO, OrderCase aCase, List<HeaderOrder> cnvOrders, User currentUser) {
		List<CNVRow> rows = new ArrayList<CNVRow>();
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
		for (CNV cnv : aCase.getCnvs()) {
			//populate selection from other annotators
			Map<Integer, AnnotatorSelection> selectionPerAnnotator = new HashMap<Integer, AnnotatorSelection>();
			if (cnv.getAnnotatorSelections() != null) {
				cnv.setSelected(false);
				for (Integer userId : cnv.getAnnotatorSelections().keySet()) {
					boolean isSelected = cnv.getAnnotatorSelections().get(userId) != null && cnv.getAnnotatorSelections().get(userId);
					String date = cnv.getAnnotatorDates().get(userId);
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
										s.setSelectedSince(TypeUtils.buildDateSinceChip(TypeUtils.dateSince(dateUTCDatetime), ownsTheCase));
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
							cnv.setSelected(true); //this is the selection of the current user
						}
					}
				}
			}
			rows.add(new CNVRow(cnv, selectionPerAnnotator));
			
		}
		return rows;
	}

	@Override
	public void initializeHeaders() {
		Map<String, HeaderAdditionalData> annotatorInitials = new HashMap<String, HeaderAdditionalData>();
		for (CNVRow row : items) {
			for (Integer userId : row.getSelectionPerAnnotator().keySet()) {
				//need to guarantee uniqueness of header. For now, append the userId.
				//TODO find a better way to handle same initials. 
				//Maybe scan through first to see if any initials are the same
				//and append a number to those only
				HeaderAdditionalData data = new HeaderAdditionalData();
//				data.tooltip = new ToolTip(row.getSelectionPerAnnotator().get(userId).getUserFullName());
				User u = modelDAO.getUserByUserId(userId);
				String reviewer = u.getIndividualPermission().getCanReview() != null && u.getIndividualPermission().getCanReview() ? "Case Owner " : "";
				data.setTooltip(new ToolTip(reviewer + row.getSelectionPerAnnotator().get(userId).getUserFullName() + "'s selection"));
				data.setUserId(userId);
				data.setFirstName(row.getSelectionPerAnnotator().get(userId).getFirstName());
				data.setLastName(row.getSelectionPerAnnotator().get(userId).getLastName());
				annotatorInitials.put(row.getSelectionPerAnnotator().get(userId).getUserFullName(),data);
			}
		}
		
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
		
		Header chrom = new Header("CHR", "chrom");
		chrom.setWidth("100px");
		chrom.setIsSafe(true);
		headers.add(chrom);
		Header gene = new Header("Genes", "genes");
		gene.setWidth("200px");
		gene.setAlign("left");
		gene.setCanHighlight(true);
		gene.setIsSafe(true);
		headers.add(gene);
		Header cytoband = new Header("Cytoband", "cytoband");
		cytoband.setWidth("100px");
		cytoband.setAlign("left");
		cytoband.setIsSafe(true);
//		cytoband.setCanHighlight(true);
		headers.add(cytoband);
		Header iconFlags = new Header("Flags", "iconFlags");
		iconFlags.setWidth("100px");
		iconFlags.setIsFlag(true);
		iconFlags.setSortable(false);
		iconFlags.setAlign("left");
		iconFlags.setIsSafe(true);
		headers.add(iconFlags);
		Header start = new Header("Start", "start");
		start.setWidth("100px");
		start.setIsSafe(true);
		headers.add(start);
		Header end = new Header("End", "end");
		end.setWidth("100px");
		end.setIsSafe(true);
		headers.add(end);
		Header aberrationType = new Header(new String[] {"Aberration", "Type"}, "aberrationType");
		aberrationType.setWidth("100px");
		aberrationType.setIsSafe(true);
		headers.add(aberrationType);
		Header copyNumber = new Header(new String[] {"Copy", "Number"}, "copyNumber");
		copyNumber.setWidth("100px");
		copyNumber.setIsSafe(true);
		headers.add(copyNumber);
		Header score = new Header("Score", "score");
		score.setWidth("100px");
		score.setIsSafe(true);
		headers.add(score);
		
	}
	

}
