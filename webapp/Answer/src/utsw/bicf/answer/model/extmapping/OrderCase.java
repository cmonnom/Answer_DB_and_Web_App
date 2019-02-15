package utsw.bicf.answer.model.extmapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderCase {
	
	public static final String TYPE_CLINICAL = "Clinical";
	public static final String TYPE_RESEARCH = "Research";
	public static final String TYPE_CLINICAL_RESEARCH = "ClinicalResearch";
	
	private static final Map<Integer, String> stepTooltip = new HashMap<Integer, String>();
	static {
		stepTooltip.put(0, "Not assigned");
		stepTooltip.put(1, "Working on annotations");
		stepTooltip.put(2, "Variant selection under review");
		stepTooltip.put(3, "Creating the report");
		stepTooltip.put(4, "Finalized");
	}
	
	Boolean active;
	String authorizingPhysician;
	String caseId;
	String caseName;
	String dateOfBirth;
	String epicOrderDate;
	String epicOrderNumber;
	String gender;
	String icd10;
	String medicalRecordNumber;
	String normalId;
	String normalTissueType;
	String orderingPhysician;
	String patientName;
	String tumorBlock;
	String tumorCollectionDate;
	String tumorId;
	String tumorTissueType;
	String user;
	List<String> assignedTo;
	String receivedDate;
	List<Variant> variants;
	String institution;
	String normalBam;
	String tumorBam;
	String rnaBam;
	List<CNV> cnvs;
	List<Translocation> translocations;
	String oncotreeDiagnosis;
	Integer totalCases;
	List<CaseHistory> caseHistory;
	String type; //Clinical or Research or ClinicalResearch
	String clinicalStage;
	String treatmentStatus;
	String labTestName;
	Integer rawAvgDepth;
	Double rawPctOver100X;
	Integer dedupAvgDepth;
	Double dedupPctOver100X;
	@JsonProperty("tmb")
	Double tumorMutationBurden;
	String labNotes;
	
	
	public OrderCase() {
		
	}


	public Boolean getActive() {
		return active;
	}


	public void setActive(Boolean active) {
		this.active = active;
	}


	public String getAuthorizingPhysician() {
		return authorizingPhysician;
	}


	public void setAuthorizingPhysician(String authorizingPhysician) {
		this.authorizingPhysician = authorizingPhysician;
	}


	public String getCaseId() {
		return caseId;
	}


	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}


	public String getDateOfBirth() {
		return dateOfBirth;
	}


	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}


	public String getEpicOrderDate() {
		return epicOrderDate;
	}


	public void setEpicOrderDate(String epicOrderDate) {
		this.epicOrderDate = epicOrderDate;
	}


	public String getEpicOrderNumber() {
		return epicOrderNumber;
	}


	public void setEpicOrderNumber(String epicOrderNumber) {
		this.epicOrderNumber = epicOrderNumber;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getIcd10() {
		return icd10;
	}


	public void setIcd10(String icd10) {
		this.icd10 = icd10;
	}


	public String getMedicalRecordNumber() {
		return medicalRecordNumber;
	}


	public void setMedicalRecordNumber(String medicalRecordNumber) {
		this.medicalRecordNumber = medicalRecordNumber;
	}


	public String getNormalId() {
		return normalId;
	}


	public void setNormalId(String normalId) {
		this.normalId = normalId;
	}


	public String getNormalTissueType() {
		return normalTissueType;
	}


	public void setNormalTissueType(String normalTissueType) {
		this.normalTissueType = normalTissueType;
	}


	public String getOrderingPhysician() {
		return orderingPhysician;
	}


	public void setOrderingPhysician(String orderingPhysician) {
		this.orderingPhysician = orderingPhysician;
	}


	public String getPatientName() {
		return patientName;
	}


	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}


	public String getTumorBlock() {
		return tumorBlock;
	}


	public void setTumorBlock(String tumorBlock) {
		this.tumorBlock = tumorBlock;
	}


	public String getTumorCollectionDate() {
		return tumorCollectionDate;
	}


	public void setTumorCollectionDate(String tumorCollectionDate) {
		this.tumorCollectionDate = tumorCollectionDate;
	}


	public String getTumorId() {
		return tumorId;
	}


	public void setTumorId(String tumorId) {
		this.tumorId = tumorId;
	}


	public String getTumorTissueType() {
		return tumorTissueType;
	}


	public void setTumorTissueType(String tumorTissueType) {
		this.tumorTissueType = tumorTissueType;
	}


	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public List<String> getAssignedTo() {
		return assignedTo;
	}


	public void setAssignedTo(List<String> assignedTo) {
		this.assignedTo = assignedTo;
	}


	public String getCaseName() {
		return caseName;
	}


	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}


	public List<Variant> getVariants() {
		return variants;
	}


	public void setVariants(List<Variant> variants) {
		this.variants = variants;
	}


	public String getReceivedDate() {
		return receivedDate;
	}


	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}


	public String getInstitution() {
		return institution;
	}


	public void setInstitution(String institution) {
		this.institution = institution;
	}


	public String getNormalBam() {
		return normalBam;
	}


	public void setNormalBam(String normalBam) {
		this.normalBam = normalBam;
	}


	public String getTumorBam() {
		return tumorBam;
	}


	public void setTumorBam(String tumorBam) {
		this.tumorBam = tumorBam;
	}


	public String getRnaBam() {
		return rnaBam;
	}


	public void setRnaBam(String rnaBam) {
		this.rnaBam = rnaBam;
	}


	public List<CNV> getCnvs() {
		return cnvs;
	}


	public void setCnvs(List<CNV> cnvs) {
		this.cnvs = cnvs;
	}


	public List<Translocation> getTranslocations() {
		return translocations;
	}


	public void setTranslocations(List<Translocation> translocations) {
		this.translocations = translocations;
	}


	public String getOncotreeDiagnosis() {
		return oncotreeDiagnosis;
	}


	public void setOncotreeDiagnosis(String oncotreeDiagnosis) {
		this.oncotreeDiagnosis = oncotreeDiagnosis;
	}


	public Integer getTotalCases() {
		return totalCases;
	}


	public void setTotalCases(Integer totalCases) {
		this.totalCases = totalCases;
	}




	public static String getStepTooltip(int i) {
		return stepTooltip.get(i);
	}
	
	public static int getTotalSteps() {
		return stepTooltip.size();
	}


	public List<CaseHistory> getCaseHistory() {
		return caseHistory;
	}


	public void setCaseHistory(List<CaseHistory> caseHistory) {
		this.caseHistory = caseHistory;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getClinicalStage() {
		return clinicalStage;
	}


	public void setClinicalStage(String clinicalStage) {
		this.clinicalStage = clinicalStage;
	}


	public String getTreatmentStatus() {
		return treatmentStatus;
	}


	public void setTreatmentStatus(String treatmentStatus) {
		this.treatmentStatus = treatmentStatus;
	}


	public String getLabTestName() {
		return labTestName;
	}


	public void setLabTestName(String labTestName) {
		this.labTestName = labTestName;
	}


	public Integer getRawAvgDepth() {
		return rawAvgDepth;
	}


	public void setRawAvgDepth(Integer rawAvgDepth) {
		this.rawAvgDepth = rawAvgDepth;
	}


	public Double getRawPctOver100X() {
		return rawPctOver100X;
	}


	public void setRawPctOver100X(Double rawPctOver100X) {
		this.rawPctOver100X = rawPctOver100X;
	}


	public Double getTumorMutationBurden() {
		return tumorMutationBurden;
	}


	public void setTumorMutationBurden(Double tumorMutationBurden) {
		this.tumorMutationBurden = tumorMutationBurden;
	}


	public Integer getDedupAvgDepth() {
		return dedupAvgDepth;
	}


	public void setDedupAvgDepth(Integer dedupAvgDepth) {
		this.dedupAvgDepth = dedupAvgDepth;
	}


	public Double getDedupPctOver100X() {
		return dedupPctOver100X;
	}


	public void setDedupPctOver100X(Double dedupPctOver100X) {
		this.dedupPctOver100X = dedupPctOver100X;
	}


	public String getLabNotes() {
		return labNotes;
	}


	public void setLabNotes(String labNotes) {
		this.labNotes = labNotes;
	}



}
