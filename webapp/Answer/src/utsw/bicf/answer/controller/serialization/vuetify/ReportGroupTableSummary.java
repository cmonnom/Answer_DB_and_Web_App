package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.ArrayList;
import java.util.List;

import utsw.bicf.answer.model.ReportGroup;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.hybrid.HeaderOrder;
import utsw.bicf.answer.model.hybrid.ReportGroupTableRow;

public class ReportGroupTableSummary extends Summary<ReportGroupTableRow>{
//	
	public ReportGroupTableSummary(List<ReportGroup> reportGroups, List<HeaderOrder> headerOrders, User currentUser) {
		super(createReportGroupTableRows(reportGroups, currentUser), "groupName", headerOrders);
	}
	
	
	private static List<ReportGroupTableRow> createReportGroupTableRows(List<ReportGroup> reportGroups, User currentUser) {
		List<ReportGroupTableRow> reportGroupRows = new ArrayList<ReportGroupTableRow>();
		for (ReportGroup rg : reportGroups) {
			reportGroupRows.add(new ReportGroupTableRow(rg, currentUser));
		}
		return reportGroupRows;
	}


	@Override
	public void initializeHeaders() {
		headers.add(new Header("Name", "groupName"));
		Header description = new Header("Description", "description");
		description.setWidth("150px");
		description.setWidthValue(150);
		headers.add(description);
		Header link = new Header("Reference", "referenceUrl");
		link.setIsLink(true);
		headers.add(link);
		Header createdBy = new Header("Created By", "createdBy");
		headers.add(createdBy);
		Header genes = new Header("Genes", "genes");
		genes.setWidth("400px");
		genes.setWidthValue(400);
		headers.add(genes);
		Header actions = new Header(new String[] {"Edit", "Gene Set"}, "actions");
		actions.setButtons(true);
		actions.setAlign("left");
		headers.add(actions);
		
	}
}
