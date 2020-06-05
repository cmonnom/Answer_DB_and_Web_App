package utsw.bicf.answer.model.extmapping.clinicaltrials;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClinicalTrial {
	
	@JsonProperty("NCTId")
	List<String> nctId;
	@JsonProperty("BriefTitle")
	List<String> briefTitle;
	@JsonProperty("Condition")
	List<String> condition;
//	List<String> protocolNumber;
	@JsonProperty("InterventionName")
	List<String> drugNames;
	

	public ClinicalTrial() {
		super();
	}


	public List<String> getNctId() {
		return nctId;
	}


	public void setNctId(List<String> nctId) {
		this.nctId = nctId;
	}


	public List<String> getBriefTitle() {
		return briefTitle;
	}


	public void setBriefTitle(List<String> briefTitle) {
		this.briefTitle = briefTitle;
	}


	public List<String> getCondition() {
		return condition;
	}


	public void setCondition(List<String> condition) {
		this.condition = condition;
	}


	public List<String> getDrugNames() {
		return drugNames;
	}


	public void setDrugNames(List<String> drugNames) {
		this.drugNames = drugNames;
	}




	
}
