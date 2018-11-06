package utsw.bicf.answer.controller.serialization.vuetify;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;
import utsw.bicf.answer.controller.serialization.GeneVariantAndAnnotation;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.CNV;
import utsw.bicf.answer.model.extmapping.MongoDBId;
import utsw.bicf.answer.model.extmapping.Report;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.PatientInfo;
import utsw.bicf.answer.model.hybrid.ReportNavigationRow;

public class ReportSummary {
	
	@JsonProperty("_id")
	MongoDBId mongoDBId;
	Boolean isAllowed = true;
	PatientInfo patientInfo;
	IndicatedTherapySummary indicatedTherapySummary;
	CNVReportSummary cnvSummary;
	TranslocationReportSummary translocationSummary;
	
	String dateCreated;
	String dateModified;
	@JsonIgnore
	LocalDateTime createdLocalDateTime;
	@JsonIgnore
	LocalDateTime modifiedLocalDateTime;
	String createdSince;
	String modifiedSince;
	Integer createdBy;
	Integer modifiedBy;
	String createdByName;
	String modifiedByName;
	String caseId;
	
	String caseName;
	
	String summary;
	
	//TODO clinical trials
	ReportClinicalTrialsSummary clinicalTrialsSummary;
	
	Map<String, GeneVariantAndAnnotation> snpVariantsStrongClinicalSignificance;
	Map<String, GeneVariantAndAnnotation> snpVariantsPossibleClinicalSignificance;
	Map<String, GeneVariantAndAnnotation> snpVariantsUnknownClinicalSignificance;
	
	String reportName;
	
	List<Variant> missingTierVariants = new ArrayList<Variant>();
	List<CNV> missingTierCNVs = new ArrayList<CNV>();
	Boolean finalized;
	
	String labTestName;
	
	Set<String> snpIds = new HashSet<String>();
	Set<String> cnvIds = new HashSet<String>();
	Set<String> ftlIds = new HashSet<String>();
	
	Map<String, ReportNavigationRow> navigationRowsPerGene = new HashMap<String, ReportNavigationRow>();
	Boolean amended;
	String amendmentReason;
	Boolean addendum;
	String dateFinalized;
	String finalizedSince;
	
	
	public ReportSummary() {
		
	}
	
	public ReportSummary(Report reportDetails, boolean fullReport, User createdByUser, User modifiedByUser) {
		this.mongoDBId = reportDetails.getMongoDBId();
		this.patientInfo = reportDetails.getPatientInfo();
		
		this.dateCreated = reportDetails.getDateCreated();
		this.dateModified = reportDetails.getDateModified();
		
		OffsetDateTime createdUTCDatetime = OffsetDateTime.parse(reportDetails.getDateCreated(), DateTimeFormatter.ISO_DATE_TIME);
		this.createdLocalDateTime = createdUTCDatetime.toLocalDateTime();
		OffsetDateTime modifiedUTCDatetime = OffsetDateTime.parse(reportDetails.getDateModified(), DateTimeFormatter.ISO_DATE_TIME);
		this.modifiedLocalDateTime = modifiedUTCDatetime.toLocalDateTime();
		
		this.createdSince = TypeUtils.dateSince(createdUTCDatetime);
		this.modifiedSince = TypeUtils.dateSince(modifiedUTCDatetime);
		
		this.createdBy = reportDetails.getCreatedBy();
		this.modifiedBy = reportDetails.getModifiedBy();
		this.caseId = reportDetails.getCaseId();
		this.caseName = reportDetails.getCaseName();
		this.labTestName = reportDetails.getLabTestName();
		this.summary = reportDetails.getSummary();
		this.reportName = reportDetails.getReportName();
		this.createdByName = createdByUser.getFullName();
		this.modifiedByName = modifiedByUser.getFullName();
		
		if (fullReport) {
			this.indicatedTherapySummary = reportDetails.getIndicatedTherapies() != null ? new IndicatedTherapySummary(reportDetails.getIndicatedTherapies(), "chrom") : null;
			this.cnvSummary = reportDetails.getCnvs() != null ? new CNVReportSummary(reportDetails.getCnvs(), "gene") : null;
			this.translocationSummary = reportDetails.getTranslocations() != null ? new TranslocationReportSummary(reportDetails.getTranslocations(), "fusionName") : null;
			
			this.snpVariantsStrongClinicalSignificance = reportDetails.getSnpVariantsStrongClinicalSignificance();
			this.snpVariantsPossibleClinicalSignificance = reportDetails.getSnpVariantsPossibleClinicalSignificance();
			this.snpVariantsUnknownClinicalSignificance = reportDetails.getSnpVariantsUnknownClinicalSignificance();
			
			this.clinicalTrialsSummary = reportDetails.getClinicalTrials() != null ? new ReportClinicalTrialsSummary(reportDetails.getClinicalTrials(), "nctid") : null;
		}
	
		this.missingTierVariants = reportDetails.getMissingTierVariants();
		this.missingTierCNVs = reportDetails.getMissingTierCNVs();
		this.finalized = reportDetails.getFinalized();
		
		this.snpIds = reportDetails.getSnpIds();
		this.cnvIds = reportDetails.getCnvIds();
		this.ftlIds = reportDetails.getFtlIds();
		
		this.navigationRowsPerGene = reportDetails.getNavigationRowsPerGene();
		this.amended = reportDetails.getAmended();
		this.amendmentReason = reportDetails.getAmendmentReason();
		this.addendum = reportDetails.getAddendum();
		this.dateFinalized = reportDetails.getDateFinalized();
		if (reportDetails.getDateFinalized() != null) {
			OffsetDateTime finalizedUTCDatetime = OffsetDateTime.parse(reportDetails.getDateFinalized(), DateTimeFormatter.ISO_DATE_TIME);
			this.finalizedSince = TypeUtils.dateSince(finalizedUTCDatetime);
		}
	}
	
	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
	
	public Boolean getIsAllowed() {
		return isAllowed;
	}
	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}
	public PatientInfo getPatientInfo() {
		return patientInfo;
	}
	public void setPatientInfo(PatientInfo patientInfo) {
		this.patientInfo = patientInfo;
	}
	public IndicatedTherapySummary getIndicatedTherapySummary() {
		return indicatedTherapySummary;
	}
	public void setIndicatedTherapySummary(IndicatedTherapySummary indicatedTherapySummary) {
		this.indicatedTherapySummary = indicatedTherapySummary;
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

	public MongoDBId getMongoDBId() {
		return mongoDBId;
	}

	public void setMongoDBId(MongoDBId mongoDBId) {
		this.mongoDBId = mongoDBId;
	}

	public CNVReportSummary getCnvSummary() {
		return cnvSummary;
	}

	public void setCnvSummary(CNVReportSummary cnvSummary) {
		this.cnvSummary = cnvSummary;
	}

	public TranslocationReportSummary getTranslocationSummary() {
		return translocationSummary;
	}

	public void setTranslocationSummary(TranslocationReportSummary translocationSummary) {
		this.translocationSummary = translocationSummary;
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

	public ReportClinicalTrialsSummary getClinicalTrialsSummary() {
		return clinicalTrialsSummary;
	}

	public void setClinicalTrialsSummary(ReportClinicalTrialsSummary clinicalTrialsSummary) {
		this.clinicalTrialsSummary = clinicalTrialsSummary;
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

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	public String getModifiedByName() {
		return modifiedByName;
	}

	public void setModifiedByName(String modifiedByName) {
		this.modifiedByName = modifiedByName;
	}

	public String getCreatedSince() {
		return createdSince;
	}

	public void setCreatedSince(String createdSince) {
		this.createdSince = createdSince;
	}

	public String getModifiedSince() {
		return modifiedSince;
	}

	public void setModifiedSince(String modifiedSince) {
		this.modifiedSince = modifiedSince;
	}

	public LocalDateTime getCreatedLocalDateTime() {
		return createdLocalDateTime;
	}

	public void setCreatedLocalDateTime(LocalDateTime createdLocalDateTime) {
		this.createdLocalDateTime = createdLocalDateTime;
	}

	public LocalDateTime getModifiedLocalDateTime() {
		return modifiedLocalDateTime;
	}

	public void setModifiedLocalDateTime(LocalDateTime modifiedLocalDateTime) {
		this.modifiedLocalDateTime = modifiedLocalDateTime;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public void setDateModified(String dateModified) {
		this.dateModified = dateModified;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public String getDateModified() {
		return dateModified;
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

	public Boolean getFinalized() {
		return finalized;
	}

	public void setFinalized(Boolean finalized) {
		this.finalized = finalized;
	}

	public String getLabTestName() {
		return labTestName;
	}

	public void setLabTestName(String labTestName) {
		this.labTestName = labTestName;
	}

	public Set<String> getSnpIds() {
		return snpIds;
	}

	public void setSnpIds(Set<String> snpIds) {
		this.snpIds = snpIds;
	}

	public Set<String> getCnvIds() {
		return cnvIds;
	}

	public void setCnvIds(Set<String> cnvIds) {
		this.cnvIds = cnvIds;
	}

	public Set<String> getFtlIds() {
		return ftlIds;
	}

	public void setFtlIds(Set<String> ftlIds) {
		this.ftlIds = ftlIds;
	}

	public Map<String, ReportNavigationRow> getNavigationRowsPerGene() {
		return navigationRowsPerGene;
	}

	public void setNavigationRowsPerGene(Map<String, ReportNavigationRow> navigationRowsPerGene) {
		this.navigationRowsPerGene = navigationRowsPerGene;
	}

	public Boolean getAmended() {
		return amended;
	}

	public void setAmended(Boolean amended) {
		this.amended = amended;
	}

	public String getAmendmentReason() {
		return amendmentReason;
	}

	public void setAmendmentReason(String amendmentReason) {
		this.amendmentReason = amendmentReason;
	}

	public Boolean getAddendum() {
		return addendum;
	}

	public void setAddendum(Boolean addendum) {
		this.addendum = addendum;
	}

	public String getDateFinalized() {
		return dateFinalized;
	}

	public void setDateFinalized(String dateFinalized) {
		this.dateFinalized = dateFinalized;
	}

	public String getFinalizedSince() {
		return finalizedSince;
	}

	public void setFinalizedSince(String finalizedSince) {
		this.finalizedSince = finalizedSince;
	}


	
}
