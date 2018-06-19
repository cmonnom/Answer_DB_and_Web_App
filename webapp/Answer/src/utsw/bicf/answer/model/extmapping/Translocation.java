package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Translocation {
	
	Boolean isAllowed = true;
	
	@JsonProperty("_id")
	MangoDBId mangoDBId;
	String fusionName;
	String leftGene;
	String rightGene;
	String leftBreakpoint;
	String rightBreakpoint;
	String leftStrand;
	String rightStrand;
	Integer rnaReads;
	Integer dnaReads;
	String caseId;
	Boolean selected;
	Boolean utswAnnotated;
	
	public Translocation() {
		
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

	public String getFusionName() {
		return fusionName;
	}

	public void setFusionName(String fusionName) {
		this.fusionName = fusionName;
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

	public String getLeftBreakpoint() {
		return leftBreakpoint;
	}

	public void setLeftBreakpoint(String leftBreakpoint) {
		this.leftBreakpoint = leftBreakpoint;
	}

	public String getRightBreakpoint() {
		return rightBreakpoint;
	}

	public void setRightBreakpoint(String rightBreakpoint) {
		this.rightBreakpoint = rightBreakpoint;
	}

	public String getLeftStrand() {
		return leftStrand;
	}

	public void setLeftStrand(String leftStrand) {
		this.leftStrand = leftStrand;
	}

	public String getRightStrand() {
		return rightStrand;
	}

	public void setRightStrand(String rightStrand) {
		this.rightStrand = rightStrand;
	}

	public Integer getRnaReads() {
		return rnaReads;
	}

	public void setRnaReads(Integer rnaReads) {
		this.rnaReads = rnaReads;
	}

	public Integer getDnaReads() {
		return dnaReads;
	}

	public void setDnaReads(Integer dnaReads) {
		this.dnaReads = dnaReads;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	public Boolean getUtswAnnotated() {
		return utswAnnotated;
	}

	public void setUtswAnnotated(Boolean utswAnnotated) {
		this.utswAnnotated = utswAnnotated;
	}




}
