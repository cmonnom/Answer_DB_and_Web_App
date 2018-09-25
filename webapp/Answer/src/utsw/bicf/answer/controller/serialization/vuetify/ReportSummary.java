package utsw.bicf.answer.controller.serialization.vuetify;

import utsw.bicf.answer.model.hybrid.PatientInfo;

public class ReportSummary {
	
	Boolean isAllowed = true;
	PatientInfo patientInfo;
	IndicatedTherapySummary indicatedTherapySummary;
	
	String dateCreated;
	String dateModified;
	Integer createdBy;
	Integer modifiedBy;
	String caseId;
	
	public Boolean getIsAllowed() {
		return isAllowed;
	}
	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}
	
}
