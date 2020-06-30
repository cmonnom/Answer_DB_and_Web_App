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
import utsw.bicf.answer.model.extmapping.Virus;
import utsw.bicf.answer.model.hybrid.HeaderOrder;
import utsw.bicf.answer.model.hybrid.VirusRow;

public class VirusSummary extends Summary<VirusRow> {
	
	public VirusSummary(ModelDAO modelDAO, OrderCase aCase, String uniqueIdField, List<HeaderOrder> virusOrders, User currentUser) {
		super(createRows(modelDAO, aCase, currentUser), uniqueIdField, virusOrders, modelDAO);
	}

	private static List<VirusRow> createRows(ModelDAO modelDAO, OrderCase aCase, User currentUser) {
		List<VirusRow> rows = new ArrayList<VirusRow>();
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
 		
		for (Virus virus : aCase.getViruses()) {
			
			//populate selection from other annotators
			Map<Integer, AnnotatorSelection> selectionPerAnnotator = new HashMap<Integer, AnnotatorSelection>();
			if (virus.getAnnotatorSelections() != null) {
				virus.setSelected(false);
				for (Integer userId : virus.getAnnotatorSelections().keySet()) {
					boolean isSelected = virus.getAnnotatorSelections().get(userId) != null && virus.getAnnotatorSelections().get(userId);
					if (isSelected) {
						String date = virus.getAnnotatorDates().get(userId);
						SNPIndelVariantSummary.addAnotatorSelection(selectionPerAnnotator, allUsers, aCase, userId, date);
						if (userId.equals(currentUser.getUserId())) {
							virus.setSelected(true); //this is the selection of the current user
						}
					}
				}
			}
			
			rows.add(new VirusRow(virus, selectionPerAnnotator, aCase.getTotalCases()));
		}
		return rows;
	}

	@Override
	public void initializeHeaders() {
		Map<String, HeaderAdditionalData> annotatorInitials = new HashMap<String, HeaderAdditionalData>();
		for (VirusRow row : items) {
			for (Integer userId : row.getSelectionPerAnnotator().keySet()) {
				AnnotatorSelection as = row.getSelectionPerAnnotator().get(userId);
				SNPIndelVariantSummary.extractAnnotatorInitials(annotatorInitials, as, userId);
			}
		}
		SNPIndelVariantSummary.createAnnotatorHeaders(headers, annotatorInitials);
		
		Header virusName = new Header("Name", "virusName");
		virusName.setWidth("150px");
		virusName.setIsSafe(true);
		headers.add(virusName);
		
		Header virusDescription = new Header("Description", "virusDescription");
		virusDescription.setWidth("200px");
		virusDescription.setIsSafe(true);
		headers.add(virusDescription);
		
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
		
		Header virusAcc = new Header("Accession ID", "virusAcc");
		virusAcc.setIsSafe(true);
		headers.add(virusAcc);
		
		Header virusReadCount = new Header(new String[] {"Read", "Count"}, "virusReadCount");
		virusReadCount.setIsSafe(true);
		headers.add(virusReadCount);
		
		Header numCasesSeen = new Header(new String[] {"Nb Cases", "Seen"}, "numCasesSeenFormatted", Units.NB);
		numCasesSeen.setIsSafe(true);
		numCasesSeen.setWidth("50px");
		headers.add(numCasesSeen);
		
		
	}
	
}
