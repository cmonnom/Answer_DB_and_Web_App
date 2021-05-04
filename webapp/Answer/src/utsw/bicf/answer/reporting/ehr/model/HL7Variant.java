package utsw.bicf.answer.reporting.ehr.model;

import java.util.HashSet;
import java.util.List;
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
	List<String> annotations;
	String ensemblCode;
	String dbSNPId;
	String[] variantCategory;
	String cNotation;
	String pNotation;
	String dnaChangeType;
	String aaMainChangeType;
	Integer copyNumber;
	Integer structuralVariantLength;
	Integer[] structuralVariantInnerStartEnd;
	String dnaRegion;
	String ftlLeftExon;
	String ftlRightExon;
	String cytoband;
	
	String[] structuralVariantType;
	String leftGene;
	String rightGene;
	String leftDNARegion;
	String rightDNARegion;
	String leftHGNC;
	String rightHGNC;
	
	String displayName;
	
	String somaticStatus;
	
	String fusedGenes;
	
	String cosmicMVariantId;
	String cosmicVVariantId;
	String dbSNPVariantId;
	String clinvarVariantId;
	
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
	public String getLeftGene() {
		return leftGene;
	}
	public void setLeftGene(String leftGene) {
		this.leftGene = leftGene;
	}
	public String getRightGene() {
		return rightGene;
	}
	public void setRightGene(String rightGene) {
		this.rightGene = rightGene;
	}
	public String getLeftHGNC() {
		return leftHGNC;
	}
	public void setLeftHGNC(String leftHGNC) {
		this.leftHGNC = leftHGNC;
	}
	public String getRightHGNC() {
		return rightHGNC;
	}
	public void setRightHGNC(String rightHGNC) {
		this.rightHGNC = rightHGNC;
	}
	public String getLeftDNARegion() {
		return leftDNARegion;
	}
	public void setLeftDNARegion(String leftDNARegion) {
		this.leftDNARegion = leftDNARegion;
	}
	public String getRightDNARegion() {
		return rightDNARegion;
	}
	public void setRightDNARegion(String rightDNARegion) {
		this.rightDNARegion = rightDNARegion;
	}
	public String[] getStructuralVariantType() {
		return structuralVariantType;
	}
	public void setStructuralVariantType(String[] structuralVariantType) {
		this.structuralVariantType = structuralVariantType;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public List<String> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(List<String> annotations) {
		this.annotations = annotations;
	}
	public String getSomaticStatus() {
		return somaticStatus;
	}
	public void setSomaticStatus(String somaticStatus) {
		this.somaticStatus = somaticStatus;
	}
	public String getFusedGenes() {
		return fusedGenes;
	}
	public void setFusedGenes(String fusedGenes) {
		this.fusedGenes = fusedGenes;
	}
	public String getDbSNPVariantId() {
		return dbSNPVariantId;
	}
	public void setDbSNPVariantId(String dbSNPVariantId) {
		this.dbSNPVariantId = dbSNPVariantId;
	}
	public String getClinvarVariantId() {
		return clinvarVariantId;
	}
	public void setClinvarVariantId(String clinvarVariantId) {
		this.clinvarVariantId = clinvarVariantId;
	}
	public String getCosmicMVariantId() {
		return cosmicMVariantId;
	}
	public void setCosmicMVariantId(String cosmicMVariantId) {
		this.cosmicMVariantId = cosmicMVariantId;
	}
	public String getCosmicVVariantId() {
		return cosmicVVariantId;
	}
	public void setCosmicVVariantId(String cosmicVVariantId) {
		this.cosmicVVariantId = cosmicVVariantId;
	}
	public String getAaMainChangeType() {
		return aaMainChangeType;
	}
	public void setAaMainChangeType(String aaMainChangeType) {
		this.aaMainChangeType = aaMainChangeType;
	}
	
	

}
