package utsw.bicf.answer.model.extmapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Virus {
	
	Boolean isAllowed = true;
	
	@JsonProperty("_id")
	MongoDBId mongoDBId;
	@JsonProperty("VirusName")
	String virusName;
	@JsonProperty("SampleId")
	String sampleId;
	@JsonProperty("VirusAcc")
	String virusAcc;
	@JsonProperty("VirusDescription")
	String virusDescription;
	@JsonProperty("VirusReadCount")
	Integer virusReadCount;
	Integer numCasesSeen;
	
	Boolean utswAnnotated;
	Boolean selected;
	List<MongoDBId> annotationIdsForReporting;
	String type;

	
	Map<Integer, Boolean> annotatorSelections = new HashMap<Integer, Boolean>();
	Map<Integer, String> annotatorDates = new HashMap<Integer, String>();
	
	AbstractReference referenceVirus;
	
	public Virus() {
		
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

	public Map<Integer, Boolean> getAnnotatorSelections() {
		return annotatorSelections;
	}

	public void setAnnotatorSelections(Map<Integer, Boolean> annotatorSelections) {
		this.annotatorSelections = annotatorSelections;
	}

	public Map<Integer, String> getAnnotatorDates() {
		return annotatorDates;
	}

	public void setAnnotatorDates(Map<Integer, String> annotatorDates) {
		this.annotatorDates = annotatorDates;
	}

	public List<MongoDBId> getAnnotationIdsForReporting() {
		return annotationIdsForReporting;
	}

	public void setAnnotationIdsForReporting(List<MongoDBId> annotationIdsForReporting) {
		this.annotationIdsForReporting = annotationIdsForReporting;
	}

	public String getVirusName() {
		return virusName;
	}

	public void setVirusName(String virusName) {
		this.virusName = virusName;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSampleId() {
		return sampleId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}

	public String getVirusAcc() {
		return virusAcc;
	}

	public void setVirusAcc(String virusAcc) {
		this.virusAcc = virusAcc;
	}

	public String getVirusDescription() {
		return virusDescription;
	}

	public void setVirusDescription(String virusDescription) {
		this.virusDescription = virusDescription;
	}

	public Integer getVirusReadCount() {
		return virusReadCount;
	}

	public void setVirusReadCount(Integer virusReadCount) {
		this.virusReadCount = virusReadCount;
	}

	public Integer getNumCasesSeen() {
		return numCasesSeen;
	}

	public void setNumCasesSeen(Integer numCasesSeen) {
		this.numCasesSeen = numCasesSeen;
	}

	public AbstractReference getReferenceVirus() {
		return referenceVirus;
	}

	public void setReferenceVirus(AbstractReference referenceVirus) {
		this.referenceVirus = referenceVirus;
	}







}
