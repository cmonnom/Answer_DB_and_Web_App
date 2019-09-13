package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FPKMPerCaseData {
	
	String caseId;
	String caseName = "";
	@JsonProperty("fpkm")
	Double fpkmValue;
	String oncotreeDiagnosis;
	
	public FPKMPerCaseData() {
		super();
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

	public Double getFpkmValue() {
		return fpkmValue;
	}

	public void setFpkmValue(Double fpkmValue) {
		this.fpkmValue = fpkmValue;
	}

	public String getOncotreeDiagnosis() {
		return oncotreeDiagnosis;
	}

	public void setOncotreeDiagnosis(String oncotreeDiagnosis) {
		this.oncotreeDiagnosis = oncotreeDiagnosis;
	}

	
		



}
