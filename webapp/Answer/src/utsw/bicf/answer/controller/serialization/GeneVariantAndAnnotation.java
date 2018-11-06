package utsw.bicf.answer.controller.serialization;

import utsw.bicf.answer.model.extmapping.CNV;
import utsw.bicf.answer.model.extmapping.Variant;

public class GeneVariantAndAnnotation {
	
	String geneVariant;
	String annotation;
	String gene;
	String variant;
	String oid;
	boolean readonly;
	
	public GeneVariantAndAnnotation() {
	}
	
	public GeneVariantAndAnnotation(Variant v, String annotation) {
		this.geneVariant = v.getGeneName() + " " + v.getNotation();
		this.gene = v.getGeneName();
		this.variant = v.getNotation();
		this.oid = v.getMongoDBId().getOid();
		this.annotation = annotation;
	}
	
	public GeneVariantAndAnnotation(CNV v, String genes, String annotation) {
		this.geneVariant = genes;
		this.gene = genes;
		this.variant = genes;
		this.oid = v.getMongoDBId().getOid();
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
	


}
