package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.ReportGroup;
import utsw.bicf.answer.model.hybrid.ReportGroupTableRow;

public class ReportGroupTableSummary extends Summary<ReportGroupTableRow>{
//	
	public ReportGroupTableSummary(List<ReportGroup> reportGroups) {
		super(createReportGroupTableRows(reportGroups), "groupName");
	}
	
	
	private static List<ReportGroupTableRow> createReportGroupTableRows(List<ReportGroup> reportGroups) {
		List<ReportGroupTableRow> reportGroupRows = new ArrayList<ReportGroupTableRow>();
		for (ReportGroup rg : reportGroups) {
			reportGroupRows.add(new ReportGroupTableRow(rg));
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
		Header genes = new Header("Genes", "genes");
		genes.setWidth("400px");
		genes.setWidthValue(400);
		headers.add(genes);
		Header actions = new Header(new String[] {"Edit", "Gene Set"}, "actions");
		actions.setButtons(true);
		headers.add(actions);
		
		//keep in the same order
		headerOrder = headers.stream().map(header -> header.getValue()).collect(Collectors.toList());
		
	}
}
