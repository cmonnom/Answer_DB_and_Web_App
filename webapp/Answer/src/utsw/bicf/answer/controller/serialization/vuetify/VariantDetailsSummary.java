package utsw.bicf.answer.controller.serialization.vuetify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.model.extmapping.Variant;

public class VariantDetailsSummary {
	
	Variant variantDetails;
	VariantRelatedSummary relatedSummary;
	VariantVcfAnnotationSummary canonicalSummary;
	VariantVcfAnnotationSummary otherSummary;
	Boolean isAllowed = true;
	

	public VariantDetailsSummary(Variant variantDetails, VariantRelatedSummary relatedSummary, VariantVcfAnnotationSummary canonicalSummary,
			VariantVcfAnnotationSummary otherSummary) {
		super();
		this.relatedSummary = relatedSummary;
		this.variantDetails = variantDetails;
		this.canonicalSummary = canonicalSummary;
		this.otherSummary = otherSummary;
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

}
