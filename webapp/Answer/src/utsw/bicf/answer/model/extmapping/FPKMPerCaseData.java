package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FPKMPerCaseData {
	
	String caseId;
	String caseName;
	Integer fpkmValue;
	
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

	public Integer getFpkmValue() {
		return fpkmValue;
	}

	public void setFpkmValue(Integer fpkmValue) {
		this.fpkmValue = fpkmValue;
	}

	
		



}
