package utsw.bicf.answer.controller.serialization;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import utsw.bicf.answer.model.extmapping.CNV;
import utsw.bicf.answer.model.extmapping.Variant;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneVariantAndAnnotation {
	
	String geneVariant;
//	String annotation;
	String gene;
	String variant;
	String oid;
	boolean readonly;
	Map<String, String> annotationsByCategory;
	
	public GeneVariantAndAnnotation() {
	}
	
	public GeneVariantAndAnnotation(Variant v, Map<String, String> annotationsByCategory) {
		this.geneVariant = v.getGeneName() + " " + v.getNotation();
		this.gene = v.getGeneName();
		this.variant = v.getNotation();
		this.oid = v.getMongoDBId().getOid();
		this.annotationsByCategory = annotationsByCategory;
	}
	
	public GeneVariantAndAnnotation(CNV v, String genes, Map<String, String> annotationsByCategory) {
		this.geneVariant = genes;
		this.gene = genes;
		this.variant = genes;
		this.oid = v.getMongoDBId().getOid();
		this.annotationsByCategory = annotationsByCategory;
	}

	public String getGeneVariant() {
		return geneVariant;
	}

	public void setGeneVariant(String geneVariant) {
		this.geneVariant = geneVariant;
	}

	public String getGene() {
		return gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public Map<String, String> getAnnotationsByCategory() {
		return annotationsByCategory;
	}

	public void setAnnotationsByCategory(Map<String, String> annotationsByCategory) {
		this.annotationsByCategory = annotationsByCategory;
	}

	


}
