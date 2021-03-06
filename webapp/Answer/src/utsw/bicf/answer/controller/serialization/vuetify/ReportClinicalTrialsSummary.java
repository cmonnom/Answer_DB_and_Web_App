package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.reporting.parse.BiomarkerTrialsRow;

public class ReportClinicalTrialsSummary extends Summary<BiomarkerTrialsRow> {
	
	public ReportClinicalTrialsSummary() {
		super();
	}
	
	public ReportClinicalTrialsSummary(List<BiomarkerTrialsRow> trials, String uniqueIdField) {
		super(trials, uniqueIdField, null);
		if (trials != null) {
			for (BiomarkerTrialsRow row : trials) {
				if (row.getSelectedBiomarker() != null) {
					row.setBiomarker(row.getSelectedBiomarker());
				}
				else if (row.getRelevantBiomarker() != null) {
					row.setBiomarker(row.getRelevantBiomarker());
				}
			}
		}
	}

	@Override
	public void initializeHeaders() {
		Header biomarker = new Header("Biomarker(s)", "biomarker");
		biomarker.setWidth("150px");
		biomarker.setIsSafe(false);
		headers.add(biomarker);
		
		Header addBiomarker = new Header(new String[] {"Additional Required", "Biomarker(s)"}, "additionalRequiredBiomarkers");
		addBiomarker.setWidth("150px");
		addBiomarker.setIsSafe(false);
		headers.add(addBiomarker);
		
		Header drugs = new Header("Drugs", "drugs");
		drugs.setWidth("150px");
		drugs.setIsSafe(false);
		headers.add(drugs);
		
		Header title = new Header("Title", "title");
		title.setWidth("300px");
		title.setAlign("left");
		title.setIsSafe(false);
		headers.add(title);
		
		Header nctid = new Header("NCTID", "nctid");
		nctid.setWidth("200px");
		nctid.setIsSafe(false);
		headers.add(nctid);
		
//		Header mdaccProtocol = new Header(new String[] {"MDACC", "Protocol ID"}, "mdaddProtocolId");
//		mdaccProtocol.setWidth("100px");
//		headers.add(mdaccProtocol);
		
		Header phase = new Header("Phase", "phase");
		phase.setWidth("100px");
		phase.setIsSafe(false);
		headers.add(phase);
		
		Header pi = new Header("Contact", "pi");
		pi.setWidth("100px");
		pi.setIsSafe(false);
		headers.add(pi);
		
		Header dept = new Header("Location", "dept");
		dept.setWidth("100px");
		dept.setAlign("left");
		dept.setIsSafe(false);
		headers.add(dept);
		
	}
	
}
