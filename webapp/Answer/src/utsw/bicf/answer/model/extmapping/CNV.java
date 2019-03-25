package utsw.bicf.answer.model.extmapping;

import java.text.NumberFormat;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.reporting.parse.AnnotationRow;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CNV {
	
	public static final String BREADTH_FOCAL = "Focal";
	public static final String BREADTH_CHROM = "Chromosomal";
	
	Boolean isAllowed = true;
	
	@JsonProperty("_id")
	MongoDBId mongoDBId;
	List<String> genes;
	String chrom;
	Integer start;
	Integer end;
	String startFormatted;
	String endFormatted;
	String aberrationType;
	Integer copyNumber;
	Float score;
	String caseId;
	Boolean utswAnnotated;
	Boolean mdaAnnotated;
	Boolean selected;
	String type;
	String cytoband;
	
	List<MongoDBId> annotationIdsForReporting;
	
	AbstractReference referenceCnv;
	
	AnnotationRow mdaAnnotation;
	
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


	public MongoDBId getMongoDBId() {
		return mongoDBId;
	}


	public void setMongoDBId(MongoDBId mongoDBId) {
		this.mongoDBId = mongoDBId;
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


	public String getStartFormatted() {
		if (startFormatted == null && start != null) {
			startFormatted = NumberFormat.getInstance().format(start);
		}
		return startFormatted;
	}


	public void setStartFormatted(String startFormatted) {
		this.startFormatted = startFormatted;
	}


	public String getEndFormatted() {
		if (endFormatted == null && end != null) {
			endFormatted = NumberFormat.getInstance().format(end);
		}
		return endFormatted;
	}


	public void setEndFormatted(String endFormatted) {
		this.endFormatted = endFormatted;
	}


	public AbstractReference getReferenceCnv() {
		return referenceCnv;
	}


	public void AbstractReference(AbstractReference referenceCnv) {
		this.referenceCnv = referenceCnv;
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


	public String getCytoband() {
		return cytoband;
	}


	public void setCytoband(String cytoband) {
		this.cytoband = cytoband;
	}

	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}


	public void setAnnotationIdsForReporting(List<MongoDBId> annotationIdsForReporting) {
		this.annotationIdsForReporting = annotationIdsForReporting;
	}


	public AnnotationRow getMdaAnnotation() {
		return mdaAnnotation;
	}


	public void setMdaAnnotation(AnnotationRow mdaAnnotation) {
		this.mdaAnnotation = mdaAnnotation;
	}


	public Boolean getMdaAnnotated() {
		return mdaAnnotated;
	}


	public void setMdaAnnotated(Boolean mdaAnnotated) {
		this.mdaAnnotated = mdaAnnotated;
	}


}
