package utsw.bicf.answer.model.hybrid;

import utsw.bicf.answer.controller.serialization.GeneVariantAndAnnotation;

public class ClinicalSignificance {
	
	String geneVariant;
	String geneVariantAsKey;
	String category;
	String annotation;
	boolean readonly;
	
	
	
	public ClinicalSignificance() {
	}
	public ClinicalSignificance(String geneVariant, String category, String annotation, boolean readonly) {
		super();
		this.geneVariant = geneVariant;
		this.geneVariantAsKey = geneVariant.replaceAll("\\.", "");
		this.category = category;
		this.annotation = annotation;
		this.readonly = readonly;
	}
	public String getGeneVariant() {
		return geneVariant;
	}
	public void setGeneVariant(String geneVariant) {
		this.geneVariant = geneVariant;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getAnnotation() {
		return annotation;
	}
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	public boolean isReadonly() {
		return readonly;
	}
	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}
	public String getGeneVariantAsKey() {
		return geneVariantAsKey;
	}
	public void setGeneVariantAsKey(String geneVariantAsKey) {
		this.geneVariantAsKey = geneVariantAsKey;
	}

}
