package utsw.bicf.answer.controller.serialization.vuetify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.SearchItemString;
import utsw.bicf.answer.model.extmapping.CNV;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.CNVRow;
import utsw.bicf.answer.model.hybrid.SNPIndelVariantRow;

public class CNVDetailsSummary {
	
	CNV variantDetails;
	CNVRow item;
	Integer userId;
	Boolean isAllowed = true;
	boolean success;
	SearchItemString patientDetailsOncoTreeDiagnosis;
	

	public CNVDetailsSummary(CNV variantDetails, CNVRow item, Integer userId, SearchItemString patientDetailsOncoTreeDiagnosis) {
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

	public CNV getVariantDetails() {
		return variantDetails;
	}

	public void setVariantDetails(CNV variantDetails) {
		this.variantDetails = variantDetails;
	}

	public CNVRow getItem() {
		return item;
	}

	public void setItem(CNVRow item) {
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
