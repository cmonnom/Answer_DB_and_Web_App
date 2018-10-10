package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.model.extmapping.TranslocationReport;

public class TranslocationReportSummary extends Summary<TranslocationReport> {
	
	public TranslocationReportSummary() {
		super();
	}
	
	public TranslocationReportSummary(List<TranslocationReport> translocationReports, String uniqueIdField) {
		super(translocationReports, uniqueIdField);
	}

	@Override
	public void initializeHeaders() {
		Header fusionName = new Header(new String[] {"Fusion", "Name"}, "fusionName");
		fusionName.setWidth("150px");
		headers.add(fusionName);
		
		Header leftGene = new Header("Gene1", "leftGene");
		leftGene.setWidth("100px");
		headers.add(leftGene);
		
		Header lastExon = new Header(new String[] {"Last", "Exon"}, "lastExon");
		lastExon.setWidth("75px");
		headers.add(lastExon);
		
		Header rightGene = new Header("Gene2", "rightGene");
		rightGene.setWidth("100px");
		headers.add(rightGene);
		
		Header firstExon = new Header(new String[] {"First", "Exon"}, "firstExon");
		firstExon.setWidth("75px");
		headers.add(firstExon);
		
		Header comment = new Header("Comment", "comment");
		headers.add(comment);		
		
		headerOrder = headers.stream().map(aHeader -> aHeader.getValue()).collect(Collectors.toList());
		
		
	}
	
}
