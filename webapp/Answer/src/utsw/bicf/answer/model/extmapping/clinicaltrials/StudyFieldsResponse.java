package utsw.bicf.answer.model.extmapping.clinicaltrials;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StudyFieldsResponse {
	
	
	
	@JsonProperty("StudyFields")
	List<ClinicalTrial> clinicalTrials;
	

	public StudyFieldsResponse() {
		super();
	}


	public List<ClinicalTrial> getClinicalTrials() {
		return clinicalTrials;
	}


	public void setClinicalTrials(List<ClinicalTrial> clinicalTrials) {
		this.clinicalTrials = clinicalTrials;
	}


	
}
