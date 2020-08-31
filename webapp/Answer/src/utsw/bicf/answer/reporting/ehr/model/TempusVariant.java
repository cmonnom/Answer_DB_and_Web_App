package utsw.bicf.answer.reporting.ehr.model;

import utsw.bicf.answer.reporting.ehr.loinc.LOINCItem;

public class TempusVariant {
	
	String gene;
	String transcript;
	String aaChange;
	String chr;
	String ref;
	String alt;
	int start;
	LOINCItem clinicalSignificance;
	float allFreq;
	int depth;
	int hgncCode;
	String annotation;
	String ensemblCode;
	
	
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
	public String getAaChange() {
		return aaChange;
	}
	public void setAaChange(String aaChange) {
		this.aaChange = aaChange;
	}
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
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public LOINCItem getClinicalSignificance() {
		return clinicalSignificance;
	}
	public void setClinicalSignificance(LOINCItem clinicalSignificance) {
		this.clinicalSignificance = clinicalSignificance;
	}
	public float getAllFreq() {
		return allFreq;
	}
	public void setAllFreq(float allFreq) {
		this.allFreq = allFreq;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int depth) {
		this.depth = depth;
	}
	public int getHgncCode() {
		return hgncCode;
	}
	public void setHgncCode(int hgncCode) {
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
	
	

}
