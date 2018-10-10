package utsw.bicf.answer.controller.serialization;

public class GeneVariantAndAnnotation {
	
	String geneVariant;
	String annotation;
	
	public GeneVariantAndAnnotation() {
	}
	
	public GeneVariantAndAnnotation(String geneVariant, String annotation) {
		this.geneVariant = geneVariant;
		this.annotation = annotation;
	}

	public String getGeneVariant() {
		return geneVariant;
	}

	public void setGeneVariant(String geneVariant) {
		this.geneVariant = geneVariant;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	


}
