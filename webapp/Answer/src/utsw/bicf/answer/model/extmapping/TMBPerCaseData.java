package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TMBPerCaseData extends WhiskerPerCaseData {
	
	String caseId;
	@JsonProperty("tmb")
	Double tmbValue;
	String oncotreeDiagnosis;
	
	public TMBPerCaseData() {
		super();
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public Double getTmbValue() {
		return tmbValue;
	}

	public void setTmbValue(Double tmbValue) {
		this.tmbValue = tmbValue;
	}

	@Override
	public Double getWhiskValue() {
		return tmbValue;
	}

	public String getOncotreeDiagnosis() {
		return oncotreeDiagnosis;
	}

	public void setOncotreeDiagnosis(String oncotreeDiagnosis) {
		this.oncotreeDiagnosis = oncotreeDiagnosis;
	}
		

	@Override
	public String getLabel() {
		return oncotreeDiagnosis;
	}


}
