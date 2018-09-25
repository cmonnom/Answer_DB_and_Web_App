package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TranslocationReport {
	
	Boolean isAllowed = true;
	
	@JsonProperty("_id")
	MongoDBId mongoDBId;
	String leftGene;
	String rightGene;
	String leftBreakpoint;
	String rightBreakpoint;
	String leftStrand;
	String rightStrand;
	String comment;
	
	public TranslocationReport() {
		
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}




}
