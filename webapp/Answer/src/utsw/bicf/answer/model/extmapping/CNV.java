package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CNV {
	
	Boolean isAllowed = true;
	
	@JsonProperty("_id")
	MangoDBId mangoDBId;
	List<String> genes;
	String chrom;
	Integer start;
	Integer end;
	String aberrationType;
	Integer copyNumber;
	Float score;
	String caseId;
	Boolean utswAnnotated;
	Boolean selected;
	
	public CNV() {
		
	}


	public String getChrom() {
		return chrom;
	}


	public void setChrom(String chrom) {
		this.chrom = chrom;
	}


	public Boolean getIsAllowed() {
		return isAllowed;
	}


	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}


	public MangoDBId getMangoDBId() {
		return mangoDBId;
	}


	public void setMangoDBId(MangoDBId mangoDBId) {
		this.mangoDBId = mangoDBId;
	}


	public List<String> getGenes() {
		return genes;
	}


	public void setGenes(List<String> genes) {
		this.genes = genes;
	}


	public Integer getStart() {
		return start;
	}


	public void setStart(Integer start) {
		this.start = start;
	}


	public Integer getEnd() {
		return end;
	}


	public void setEnd(Integer end) {
		this.end = end;
	}


	public String getAberrationType() {
		return aberrationType;
	}


	public void setAberrationType(String aberrationType) {
		this.aberrationType = aberrationType;
	}


	public Integer getCopyNumber() {
		return copyNumber;
	}


	public void setCopyNumber(Integer copyNumber) {
		this.copyNumber = copyNumber;
	}


	public Float getScore() {
		return score;
	}


	public void setScore(Float score) {
		this.score = score;
	}


	public String getCaseId() {
		return caseId;
	}


	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}


	public Boolean getUtswAnnotated() {
		return utswAnnotated;
	}


	public void setUtswAnnotated(Boolean utswAnnotated) {
		this.utswAnnotated = utswAnnotated;
	}


	public Boolean getSelected() {
		return selected;
	}


	public void setSelected(Boolean selected) {
		this.selected = selected;
	}




}
