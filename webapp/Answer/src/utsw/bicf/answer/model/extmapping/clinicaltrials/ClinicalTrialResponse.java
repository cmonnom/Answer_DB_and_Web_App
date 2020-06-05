package utsw.bicf.answer.model.extmapping.clinicaltrials;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClinicalTrialResponse {
	
	@JsonProperty("StudyFieldsResponse")
	StudyFieldsResponse studyFieldsResponse;
	

	public ClinicalTrialResponse() {
		super();
	}


	public StudyFieldsResponse getStudyFieldsResponse() {
		return studyFieldsResponse;
	}


	public void setStudyFieldsResponse(StudyFieldsResponse studyFieldsResponse) {
		this.studyFieldsResponse = studyFieldsResponse;
	}


	
}
