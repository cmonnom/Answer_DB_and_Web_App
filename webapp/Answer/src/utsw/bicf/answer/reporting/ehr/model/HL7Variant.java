package utsw.bicf.answer.reporting.ehr.model;

import java.util.HashSet;
import java.util.Set;

import utsw.bicf.answer.reporting.ehr.loinc.LOINCItem;

public class HL7Variant {
	
	String gene;
	String transcript;
//	String aaChange;
	String chr;
	String ref;
	String alt;
	Integer start2018v;
	Integer end2018v;
	String startEnd2020v;
	LOINCItem clinicalSignificance;
	Float allFreq;
	Integer depth;
	String hgncCode;
	String annotation;
	String ensemblCode;
	String dbSNPId;
	String[] variantCategory;
	String cNotation;
	String pNotation;
	String dnaChangeType;
	Set<String> aaChangeTypes = new HashSet<String>();
	Integer copyNumber;
	Integer structuralVariantLength;
	Integer[] structuralVariantInnerStartEnd;
	String dnaRegion;
	String ftlLeftExon;
	String ftlRightExon;
	String cytoband;
	
	
	
	public String getGene() {
		return gene;
	}
	public void setGene(String gene) {
		this.gene = gene;
	}
	public String getTranscript() {
		return transcript;
	}
	public void setTranscript(String transcript) {
		this.transcript = transcript;
	}
//	public String getAaChange() {
//		return aaChange;
//	}
//	public void setAaChange(String aaChange) {
//		this.aaChange = aaChange;
//	}
	public String getChr() {
		return chr;
	}
	public void setChr(String chr) {
		this.chr = chr;
	}
	public String getRef() {
		return ref;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getAlt() {
		return alt;
	}
	public void setAlt(String alt) {
		this.alt = alt;
	}
	public LOINCItem getClinicalSignificance() {
		return clinicalSignificance;
	}
	public void setClinicalSignificance(LOINCItem clinicalSignificance) {
		this.clinicalSignificance = clinicalSignificance;
	}
	public Float getAllFreq() {
		return allFreq;
	}
	public void setAllFreq(Float allFreq) {
		this.allFreq = allFreq;
	}
	public Integer getDepth() {
		return depth;
	}
	public void setDepth(Integer depth) {
		this.depth = depth;
	}
	public String getHgncCode() {
		return hgncCode;
	}
	public void setHgncCode(String hgncCode) {
		this.hgncCode = hgncCode;
	}
	public String getAnnotation() {
		return annotation;
	}
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	public String getEnsemblCode() {
		return ensemblCode;
	}
	public void setEnsemblCode(String ensemblCode) {
		this.ensemblCode = ensemblCode;
	}
	public String getDbSNPId() {
		return dbSNPId;
	}
	public void setDbSNPId(String dbSNPId) {
		this.dbSNPId = dbSNPId;
	}
	public String[] getVariantCategory() {
		return variantCategory;
	}
	public void setVariantCategory(String[] variantCategory) {
		this.variantCategory = variantCategory;
	}
	public String getcNotation() {
		return cNotation;
	}
	public void setcNotation(String cNotation) {
		this.cNotation = cNotation;
	}
	public String getpNotation() {
		return pNotation;
	}
	public void setpNotation(String pNotation) {
		this.pNotation = pNotation;
	}
	public String getDnaChangeType() {
		return dnaChangeType;
	}
	public void setDnaChangeType(String dnaChangeType) {
		this.dnaChangeType = dnaChangeType;
	}
	public Set<String> getAaChangeTypes() {
		return aaChangeTypes;
	}
	public void setAaChangeTypes(Set<String> aaChangeTypes) {
		this.aaChangeTypes = aaChangeTypes;
	}
	public Integer getCopyNumber() {
		return copyNumber;
	}
	public void setCopyNumber(Integer copyNumber) {
		this.copyNumber = copyNumber;
	}
	public Integer getStructuralVariantLength() {
		return structuralVariantLength;
	}
	public void setStructuralVariantLength(Integer structuralVariantLength) {
		this.structuralVariantLength = structuralVariantLength;
	}
	public Integer[] getStructuralVariantInnerStartEnd() {
		return structuralVariantInnerStartEnd;
	}
	public void setStructuralVariantInnerStartEnd(Integer[] structuralVariantInnerStartEnd) {
		this.structuralVariantInnerStartEnd = structuralVariantInnerStartEnd;
	}
	public String getDnaRegion() {
		return dnaRegion;
	}
	public void setDnaRegion(String dnaRegion) {
		this.dnaRegion = dnaRegion;
	}
	public String getFtlLeftExon() {
		return ftlLeftExon;
	}
	public void setFtlLeftExon(String ftlLeftExon) {
		this.ftlLeftExon = ftlLeftExon;
	}
	public String getFtlRightExon() {
		return ftlRightExon;
	}
	public void setFtlRightExon(String ftlRightExon) {
		this.ftlRightExon = ftlRightExon;
	}
	public String getCytoband() {
		return cytoband;
	}
	public void setCytoband(String cytoband) {
		this.cytoband = cytoband;
	}
	public String getStartEnd2020v() {
		return startEnd2020v;
	}
	public void setStartEnd2020v(String startEnd2020v) {
		this.startEnd2020v = startEnd2020v;
	}
	public Integer getStart2018v() {
		return start2018v;
	}
	public void setStart2018v(Integer start2018v) {
		this.start2018v = start2018v;
	}
	public Integer getEnd2018v() {
		return end2018v;
	}
	public void setEnd2018v(Integer end2018v) {
		this.end2018v = end2018v;
	}
	
	

}
