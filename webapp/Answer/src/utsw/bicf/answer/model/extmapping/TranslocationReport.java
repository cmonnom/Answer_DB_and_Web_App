package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TranslocationReport {
	
	Boolean isAllowed = true;
	
	@JsonProperty("_id")
	MongoDBId mongoDBId;
	String leftGene;
	String rightGene;
	String fusionName;
	String comment;
	String firstExon;
	String lastExon;
	boolean readonly;
	
	public TranslocationReport() {
		
	}

	public TranslocationReport(String text, Translocation t) {
		mongoDBId = t.getMongoDBId();
		this.leftGene = t.getLeftGene();
		this.rightGene = t.getRightGene();
		this.fusionName = t.getFusionName();
		this.firstExon = t.getFirstExon();
		this.lastExon = t.getLastExon();
		this.comment = text;
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

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getFusionName() {
		return fusionName;
	}

	public void setFusionName(String fusionName) {
		this.fusionName = fusionName;
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

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}




}
