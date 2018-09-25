package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import utsw.bicf.answer.model.hybrid.PatientInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Report {
	
	@JsonProperty("_id")
	MongoDBId mongoDBId;
	
	boolean isLive; //if this report is made from the latest changes in the case
	PatientInfo patientInfo;
	List<IndicatedTherapy> indicatedTherapies;
	List<ClinicalTrial> clinicalTrials;
	List<Variant> snpVariantsStrongClinicalSignificance;
	List<Variant> snpVariantsUnknownClinicalSignificance;
	List<CNVReport> cnvs;
	List<TranslocationReport> fusion;
	String summary;
	List<SelectedCitation> selectedCitations;
	String dateCreated;
	String dateModified;
	Integer createdBy;
	Integer modifiedBy;
	String caseId;
	

	public MongoDBId getMongoDBId() {
		return mongoDBId;
	}
	public void setMongoDBId(MongoDBId mongoDBId) {
		this.mongoDBId = mongoDBId;
	}
	public boolean isLive() {
		return isLive;
	}
	public void setLive(boolean isLive) {
		this.isLive = isLive;
	}
	public PatientInfo getPatientInfo() {
		return patientInfo;
	}
	public void setPatientInfo(PatientInfo patientInfo) {
		this.patientInfo = patientInfo;
	}
	public List<IndicatedTherapy> getIndicatedTherapies() {
		return indicatedTherapies;
	}
	public void setIndicatedTherapies(List<IndicatedTherapy> indicatedTherapies) {
		this.indicatedTherapies = indicatedTherapies;
	}
	public List<ClinicalTrial> getClinicalTrials() {
		return clinicalTrials;
	}
	public void setClinicalTrials(List<ClinicalTrial> clinicalTrials) {
		this.clinicalTrials = clinicalTrials;
	}
	public List<Variant> getSnpVariantsStrongClinicalSignificance() {
		return snpVariantsStrongClinicalSignificance;
	}
	public void setSnpVariantsStrongClinicalSignificance(List<Variant> snpVariantsStrongClinicalSignificance) {
		this.snpVariantsStrongClinicalSignificance = snpVariantsStrongClinicalSignificance;
	}
	public List<Variant> getSnpVariantsUnknownClinicalSignificance() {
		return snpVariantsUnknownClinicalSignificance;
	}
	public void setSnpVariantsUnknownClinicalSignificance(List<Variant> snpVariantsUnknownClinicalSignificance) {
		this.snpVariantsUnknownClinicalSignificance = snpVariantsUnknownClinicalSignificance;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public List<SelectedCitation> getSelectedCitations() {
		return selectedCitations;
	}
	public void setSelectedCitations(List<SelectedCitation> selectedCitations) {
		this.selectedCitations = selectedCitations;
	}
	public List<CNVReport> getCnvs() {
		return cnvs;
	}
	public void setCnvs(List<CNVReport> cnvs) {
		this.cnvs = cnvs;
	}
	public List<TranslocationReport> getFusion() {
		return fusion;
	}
	public void setFusion(List<TranslocationReport> fusion) {
		this.fusion = fusion;
	}
	public String getDateCreated() {
		return dateCreated;
	}
	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}
	public String getDateModified() {
		return dateModified;
	}
	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
	}
	public Integer getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}
	public Integer getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(Integer modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public String getCaseId() {
		return caseId;
	}
	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}
	
	

}
