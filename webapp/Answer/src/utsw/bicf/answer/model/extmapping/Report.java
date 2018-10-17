package utsw.bicf.answer.model.extmapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.GeneVariantAndAnnotation;
import utsw.bicf.answer.controller.serialization.vuetify.ReportSummary;
import utsw.bicf.answer.model.hybrid.PatientInfo;
import utsw.bicf.answer.reporting.parse.BiomarkerTrialsRow;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Report {
	
	@JsonProperty("_id")
	MongoDBId mongoDBId;
	
	boolean isLive; //if this report is made from the latest changes in the case
	PatientInfo patientInfo;
	List<IndicatedTherapy> indicatedTherapies;
	List<BiomarkerTrialsRow> clinicalTrials;
	Map<String, GeneVariantAndAnnotation> snpVariantsStrongClinicalSignificance;
	Map<String, GeneVariantAndAnnotation> snpVariantsPossibleClinicalSignificance;
	Map<String, GeneVariantAndAnnotation> snpVariantsUnknownClinicalSignificance;
	List<CNVReport> cnvs;
	List<TranslocationReport> translocations;
	String summary;
	List<SelectedCitation> selectedCitations;
	String dateCreated;
	String dateModified;
	Integer createdBy;
	Integer modifiedBy;
	String caseId;
	String caseName;
	String reportName;
	List<Variant> missingTierVariants = new ArrayList<Variant>();
	List<CNV> missingTierCNVs = new ArrayList<CNV>();
	
	public Report() {
	}

	public Report(ReportSummary reportSummary) {
		this.mongoDBId = reportSummary.getMongoDBId();
		this.patientInfo = reportSummary.getPatientInfo();
		this.caseId = reportSummary.getCaseId();
		this.caseName = reportSummary.getCaseName();
		this.reportName = reportSummary.getReportName();
		this.createdBy = reportSummary.getCreatedBy();
		this.modifiedBy = reportSummary.getModifiedBy();
		
		this.setSummary(reportSummary.getSummary());
		//update Indicated Therapies
		if (reportSummary.getIndicatedTherapySummary() != null) {
			this.setIndicatedTherapies(reportSummary.getIndicatedTherapySummary().getItems());
		}
		//update CNV
		if (reportSummary.getCnvSummary() != null) {
			this.setCnvs(reportSummary.getCnvSummary().getItems());
		}
		//update FTL
		if (reportSummary.getTranslocationSummary() != null) {
			this.setTranslocations(reportSummary.getTranslocationSummary().getItems());
		}
		//update clinical significance
		this.setSnpVariantsStrongClinicalSignificance(reportSummary.getSnpVariantsStrongClinicalSignificance());
		this.setSnpVariantsPossibleClinicalSignificance(reportSummary.getSnpVariantsPossibleClinicalSignificance());
		this.setSnpVariantsUnknownClinicalSignificance(reportSummary.getSnpVariantsUnknownClinicalSignificance());
		
		//update clinical trials
		if (reportSummary.getClinicalTrialsSummary() != null) {
			this.setClinicalTrials(reportSummary.getClinicalTrialsSummary().getItems());
		}
	}
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
	public List<BiomarkerTrialsRow> getClinicalTrials() {
		return clinicalTrials;
	}
	public void setClinicalTrials(List<BiomarkerTrialsRow> clinicalTrials) {
		this.clinicalTrials = clinicalTrials;
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
	public Map<String, GeneVariantAndAnnotation> getSnpVariantsStrongClinicalSignificance() {
		return snpVariantsStrongClinicalSignificance;
	}
	public void setSnpVariantsStrongClinicalSignificance(Map<String, GeneVariantAndAnnotation> snpVariantsStrongClinicalSignificance) {
		this.snpVariantsStrongClinicalSignificance = snpVariantsStrongClinicalSignificance;
	}
	public Map<String, GeneVariantAndAnnotation> getSnpVariantsUnknownClinicalSignificance() {
		return snpVariantsUnknownClinicalSignificance;
	}
	public void setSnpVariantsUnknownClinicalSignificance(Map<String, GeneVariantAndAnnotation> snpVariantsUnknownClinicalSignificance) {
		this.snpVariantsUnknownClinicalSignificance = snpVariantsUnknownClinicalSignificance;
	}
	public List<TranslocationReport> getTranslocations() {
		return translocations;
	}
	public void setTranslocations(List<TranslocationReport> translocations) {
		this.translocations = translocations;
	}
	public Map<String, GeneVariantAndAnnotation> getSnpVariantsPossibleClinicalSignificance() {
		return snpVariantsPossibleClinicalSignificance;
	}
	public void setSnpVariantsPossibleClinicalSignificance(Map<String, GeneVariantAndAnnotation> snpVariantsPossibleClinicalSignificance) {
		this.snpVariantsPossibleClinicalSignificance = snpVariantsPossibleClinicalSignificance;
	}
	public String getCaseName() {
		return caseName;
	}
	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}
	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public List<Variant> getMissingTierVariants() {
		return missingTierVariants;
	}

	public void setMissingTierVariants(List<Variant> missingTierVariants) {
		this.missingTierVariants = missingTierVariants;
	}

	public List<CNV> getMissingTierCNVs() {
		return missingTierCNVs;
	}

	public void setMissingTierCNVs(List<CNV> missingTierCNVs) {
		this.missingTierCNVs = missingTierCNVs;
	}
	
	

}
