package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseAnnotation {
	

	@JsonProperty("_id")
	MangoDBId mangoDBId;
	
	String caseAnnotation;
	List<String> assignedTo;
	String caseId;
	String createdDate;
	String modifiedDate;
	
	Boolean isAllowed = true;
	
	
	public CaseAnnotation() {
		
	}

	public MangoDBId getMangoDBId() {
		return mangoDBId;
	}

	public void setMangoDBId(MangoDBId mangoDBId) {
		this.mangoDBId = mangoDBId;
	}

	public String getCaseAnnotation() {
		return caseAnnotation;
	}

	public void setCaseAnnotation(String caseAnnotation) {
		this.caseAnnotation = caseAnnotation;
	}

	public List<String> getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(List<String> assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}
	


	
}
