package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Translocation {
	
	Boolean isAllowed = true;
	
	@JsonProperty("_id")
	MongoDBId mongoDBId;
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
	String type;
	String firstExon;
	String lastExon;
	
	List<MongoDBId> annotationIdsForReporting;
	
	AbstractReference referenceTranslocation;
	
	public Translocation() {
		
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

	public MongoDBId getMongoDBId() {
		return mongoDBId;
	}

	public void setMongoDBId(MongoDBId mongoDBId) {
		this.mongoDBId = mongoDBId;
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

	public AbstractReference getReferenceTranslocation() {
		return referenceTranslocation;
	}

	public void setReferenceTranslocation(AbstractReference referenceTranslocation) {
		this.referenceTranslocation = referenceTranslocation;
	}

	public List<MongoDBId> getAnnotationIdsForReporting() {
		return annotationIdsForReporting;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFirstExon() {
		return firstExon;
	}

	public void setFirstExon(String firstExon) {
		this.firstExon = firstExon;
	}

	public String getLastExon() {
		return lastExon;
	}

	public void setLastExon(String lastExon) {
		this.lastExon = lastExon;
	}



}
