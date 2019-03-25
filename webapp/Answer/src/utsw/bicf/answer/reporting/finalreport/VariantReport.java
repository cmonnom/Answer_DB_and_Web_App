package utsw.bicf.answer.reporting.finalreport;

import utsw.bicf.answer.model.extmapping.Variant;

public class VariantReport {

	Variant variant;
	String highestAnnotationTier;

	public String getHighestAnnotationTier() {
		return highestAnnotationTier;
	}

	public void setHighestAnnotationTier(String highestAnnotationTier) {
		this.highestAnnotationTier = highestAnnotationTier;
	}

	public VariantReport(Variant variant, String highestAnnotationTier) {
		super();
		this.highestAnnotationTier = highestAnnotationTier;
		this.variant = variant;
	}

	public Variant getVariant() {
		return variant;
	}

	public void setVariant(Variant variant) {
		this.variant = variant;
	}
	
	
}
