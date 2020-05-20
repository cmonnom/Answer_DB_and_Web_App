package utsw.bicf.answer.controller.serialization.vuetify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.SearchItemString;
import utsw.bicf.answer.model.extmapping.Virus;
import utsw.bicf.answer.model.hybrid.VirusRow;

public class VirusDetailsSummary {
	
	Virus variantDetails;
	VirusRow item;
	Integer userId;
	Boolean isAllowed = true;
	boolean success;
	SearchItemString patientDetailsOncoTreeDiagnosis;

	public VirusDetailsSummary(Virus variantDetails, VirusRow item, Integer userId, SearchItemString patientDetailsOncoTreeDiagnosis) {
		super();
		this.variantDetails = variantDetails;
		this.item = item;
		this.userId = userId;
		this.patientDetailsOncoTreeDiagnosis = patientDetailsOncoTreeDiagnosis;
	}

	public String createVuetifyObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}


	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Virus getVariantDetails() {
		return variantDetails;
	}

	public void setVariantDetails(Virus variantDetails) {
		this.variantDetails = variantDetails;
	}

	public VirusRow getItem() {
		return item;
	}

	public void setItem(VirusRow item) {
		this.item = item;
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public SearchItemString getPatientDetailsOncoTreeDiagnosis() {
		return patientDetailsOncoTreeDiagnosis;
	}

	public void setPatientDetailsOncoTreeDiagnosis(SearchItemString patientDetailsOncoTreeDiagnosis) {
		this.patientDetailsOncoTreeDiagnosis = patientDetailsOncoTreeDiagnosis;
	}


}
