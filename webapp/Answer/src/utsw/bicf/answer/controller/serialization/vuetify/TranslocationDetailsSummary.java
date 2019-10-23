package utsw.bicf.answer.controller.serialization.vuetify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.SearchItemString;
import utsw.bicf.answer.model.extmapping.Translocation;
import utsw.bicf.answer.model.hybrid.TranslocationRow;

public class TranslocationDetailsSummary {
	
	Translocation variantDetails;
	TranslocationRow item;
	Integer userId;
	Boolean isAllowed = true;
	boolean success;
	SearchItemString patientDetailsOncoTreeDiagnosis;
	

	public TranslocationDetailsSummary(Translocation variantDetails, TranslocationRow item, Integer userId, SearchItemString patientDetailsOncoTreeDiagnosis) {
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

	public Translocation getVariantDetails() {
		return variantDetails;
	}

	public void setVariantDetails(Translocation variantDetails) {
		this.variantDetails = variantDetails;
	}

	public TranslocationRow getItem() {
		return item;
	}

	public void setItem(TranslocationRow item) {
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
