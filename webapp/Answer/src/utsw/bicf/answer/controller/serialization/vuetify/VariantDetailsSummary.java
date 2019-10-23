package utsw.bicf.answer.controller.serialization.vuetify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.SearchItemString;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.SNPIndelVariantRow;

public class VariantDetailsSummary {
	
	Variant variantDetails;
	VariantRelatedSummary relatedSummary;
	CNVRelatedSummary cnvRelatedSummary;
	VariantVcfAnnotationSummary canonicalSummary;
	VariantVcfAnnotationSummary otherSummary;
	Boolean isAllowed = true;
	SNPIndelVariantRow item;
	Integer userId;
	SearchItemString patientDetailsOncoTreeDiagnosis;
	

	public VariantDetailsSummary(Variant variantDetails, SNPIndelVariantRow item, VariantRelatedSummary relatedSummary, CNVRelatedSummary cnvRelatedSummary, VariantVcfAnnotationSummary canonicalSummary,
			VariantVcfAnnotationSummary otherSummary, Integer userId, SearchItemString patientDetailsOncoTreeDiagnosis) {
		super();
		this.relatedSummary = relatedSummary;
		this.cnvRelatedSummary = cnvRelatedSummary;
		this.variantDetails = variantDetails;
		this.canonicalSummary = canonicalSummary;
		this.otherSummary = otherSummary;
		this.item = item;
		this.userId = userId;
		this.patientDetailsOncoTreeDiagnosis = patientDetailsOncoTreeDiagnosis;
	}

	public String createVuetifyObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

	public Variant getVariantDetails() {
		return variantDetails;
	}

	public void setVariantDetails(Variant variantDetails) {
		this.variantDetails = variantDetails;
	}

	public VariantVcfAnnotationSummary getCanonicalSummary() {
		return canonicalSummary;
	}

	public void setCanonicalSummary(VariantVcfAnnotationSummary canonicalSummary) {
		this.canonicalSummary = canonicalSummary;
	}

	public VariantVcfAnnotationSummary getOtherSummary() {
		return otherSummary;
	}

	public void setOtherSummary(VariantVcfAnnotationSummary otherSummary) {
		this.otherSummary = otherSummary;
	}

	public VariantRelatedSummary getRelatedSummary() {
		return relatedSummary;
	}

	public void setRelatedSummary(VariantRelatedSummary relatedSummary) {
		this.relatedSummary = relatedSummary;
	}

	public CNVRelatedSummary getCnvRelatedSummary() {
		return cnvRelatedSummary;
	}

	public void setCnvRelatedSummary(CNVRelatedSummary cnvRelatedSummary) {
		this.cnvRelatedSummary = cnvRelatedSummary;
	}

	public SNPIndelVariantRow getItem() {
		return item;
	}

	public void setItem(SNPIndelVariantRow item) {
		this.item = item;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public SearchItemString getPatientDetailsOncoTreeDiagnosis() {
		return patientDetailsOncoTreeDiagnosis;
	}

	public void setPatientDetailsOncoTreeDiagnosis(SearchItemString patientDetailsOncoTreeDiagnosis) {
		this.patientDetailsOncoTreeDiagnosis = patientDetailsOncoTreeDiagnosis;
	}

}
