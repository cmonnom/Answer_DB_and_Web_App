package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.Units;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.FinalReport;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.OpenCaseRow;
import utsw.bicf.answer.model.hybrid.PatientInfo;

public class OpenCaseSummary extends Summary<OpenCaseRow> {
	
	PatientInfo patientInfo;
	String caseId;
	String caseName;

	public OpenCaseSummary(ModelDAO modelDAO, OrderCase aCase, FinalReport finalReport, String uniqueIdField) {
		super(createRows(modelDAO, aCase), uniqueIdField);
		this.patientInfo = new PatientInfo(aCase, finalReport);
		this.caseId = aCase.getCaseId();
		this.caseName = aCase.getCaseName();
	}

	private static List<OpenCaseRow> createRows(ModelDAO modelDAO, OrderCase aCase) {
		List<OpenCaseRow> rows = new ArrayList<OpenCaseRow>();
		for (Variant variant : aCase.getVariants()) {
			rows.add(new OpenCaseRow(variant));
		}
		
		return rows;
	}

	@Override
	public void initializeHeaders() {
		Header chromPos = new Header("CHR", "chromPos");
		headers.add(chromPos);
		Header geneVariant = new Header("GeneVariant", "geneVariant");
		headers.add(geneVariant);
		Header effect = new Header("Effect", "effect");
		headers.add(effect);
		Header taf = new Header("TAF", "tumorAltFrequency", Units.PCT);
		headers.add(taf);
		Header tumorAltDepth = new Header("Depth", "tumorAltDepth", Units.BASE_PAIR);
		headers.add(tumorAltDepth);
		//keep in the same order
		headerOrder = headers.stream().map(aHeader -> aHeader.getValue()).collect(Collectors.toList());
		
		
	}

	public PatientInfo getPatientInfo() {
		return patientInfo;
	}

	public void setPatientInfo(PatientInfo patientInfo) {
		this.patientInfo = patientInfo;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getCaseName() {
		return caseName;
	}

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

}
