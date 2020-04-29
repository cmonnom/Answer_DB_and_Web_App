package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WhiskerPerCaseData {
	
	String caseId = null;
	String caseName = "";
	Double whiskValue = null;
	String label = null;
	
	
	public WhiskerPerCaseData() {
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
	public Double getWhiskValue() {
		return whiskValue;
	}
	public void setWhiskValue(Double whiskValue) {
		this.whiskValue = whiskValue;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	


	
		



}
