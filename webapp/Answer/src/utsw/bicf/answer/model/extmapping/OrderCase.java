package utsw.bicf.answer.model.extmapping;

import java.util.ArrayList;
import java.util.Arrays;
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
	public static final List<String> TMB_CLASS_VALUES = Arrays.asList(new String[]{"High", "Medium", "Low"});
	public static final List<String> MSI_CLASS_VALUES = Arrays.asList(new String[]{"MSI", "MSS"});
	
	private static final Map<Integer, String> stepTooltipBefore = new HashMap<Integer, String>();
	private static final Map<Integer, String> stepTooltipDuring = new HashMap<Integer, String>();
	private static final Map<Integer, String> stepTooltipAfter = new HashMap<Integer, String>();
	static {
		stepTooltipBefore.put(0, "Case not assigned yet");
		stepTooltipBefore.put(1, "Waiting for annotations");
		stepTooltipBefore.put(2, "Not being reviewed yet");
		stepTooltipBefore.put(3, "Report not created yet");
		stepTooltipBefore.put(4, "Report not finalized yet");
		stepTooltipBefore.put(5, "Not uploaded to Epic yet");
		
		stepTooltipDuring.put(0, "Case assigned");
		stepTooltipDuring.put(1, "Working on annotations");
		stepTooltipDuring.put(2, "Variant selection under review");
		stepTooltipDuring.put(3, "Creating the report");
		stepTooltipDuring.put(4, "Finalized");
		stepTooltipDuring.put(5, "Uploaded to Epic");
		
		stepTooltipAfter.put(0, "Case Assigned");
		stepTooltipAfter.put(1, "Annotations done");
		stepTooltipAfter.put(2, "Variant selection reviewed");
		stepTooltipAfter.put(3, "Report created");
		stepTooltipAfter.put(4, "Finalized");
		stepTooltipAfter.put(5, "Uploaded to Epic");
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
	List<Variant> variants = new ArrayList<Variant>();
	String institution;
	String normalBam;
	String tumorBam;
	String rnaBam;
	String tumorVcf;
	List<CNV> cnvs = new ArrayList<CNV>();
	List<Translocation> translocations = new ArrayList<Translocation>();
	List<Virus> viruses = new ArrayList<Virus>();
	String oncotreeDiagnosis;
	Integer totalCases;
	List<CaseHistory> caseHistory;
	String type; //Clinical or Research or ClinicalResearch
	String clinicalStage;
	String treatmentStatus;
	@JsonProperty("testName")
	String labTestName;
	Integer rawAvgDepth;
	Double rawPctOver100X;
	Integer dedupAvgDepth;
	Double dedupPctOver100X;
	@JsonProperty("tmb")
	Double tumorMutationBurden;
	String labNotes;
	Double tumorPercent;
	List<String> groupIds;
	String storageType;
	String caseOwner;
	@JsonProperty("tmbClass")
	String tumorMutationBurdenClass;
	Float msi;
	@JsonProperty("msiClass")
	String msiClass;
	String mutationalSignatureImage;
	String mutationalSignatureLinkName;
	List<MutationalSignatureData> mutationalSignatureData;
	String tumorPanel;
	//TODO
	@JsonProperty("reportOrderNumber")
	String hl7OrderId;
	@JsonProperty("reportAccessionId")
	String hl7SampleId;
	
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




	public static String getStepTooltip(String when, int i) {
		switch(when) {
		case "before": return stepTooltipBefore.get(i); 
		case "during": return stepTooltipDuring.get(i); 
		case "after": return stepTooltipAfter.get(i);
		}
		return "";
	}
	
	public static int getTotalSteps() {
		return stepTooltipBefore.size();
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


	public Double getTumorPercent() {
		return tumorPercent;
	}


	public void setTumorPercent(Double tumorPercent) {
		this.tumorPercent = tumorPercent;
	}


	public List<String> getGroupIds() {
		return groupIds;
	}


	public void setGroupIds(List<String> groupIds) {
		this.groupIds = groupIds;
	}


	public String getTumorVcf() {
		return tumorVcf;
	}


	public void setTumorVcf(String tumorVcf) {
		this.tumorVcf = tumorVcf;
	}


	public String getStorageType() {
		if (storageType == null) {
			storageType = "local"; //temp fix. Need to populate on mongo's side
		}
		return storageType;
	}


	public void setStorageType(String storageType) {
		this.storageType = storageType;
	}


	public String getCaseOwner() {
		return caseOwner;
	}


	public void setCaseOwner(String caseOwner) {
		this.caseOwner = caseOwner;
	}


	public String getTumorMutationBurdenClass() {
		return tumorMutationBurdenClass;
	}


	public void setTumorMutationBurdenClass(String tumorMutationBurdenClass) {
		this.tumorMutationBurdenClass = tumorMutationBurdenClass;
	}


	public Float getMsi() {
		return msi;
	}


	public void setMsi(Float msi) {
		this.msi = msi;
	}


	public String getMsiClass() {
		return msiClass;
	}


	public void setMsiClass(String msiClass) {
		this.msiClass = msiClass;
	}


	public List<Virus> getViruses() {
		return viruses;
	}


	public void setViruses(List<Virus> viruses) {
		this.viruses = viruses;
	}


	public String getMutationalSignatureImage() {
		return mutationalSignatureImage;
	}


	public void setMutationalSignatureImage(String mutationalSignatureImage) {
		this.mutationalSignatureImage = mutationalSignatureImage;
	}


	public String getMutationalSignatureLinkName() {
		return mutationalSignatureLinkName;
	}


	public void setMutationalSignatureLinkName(String mutationalSignatureLinkName) {
		this.mutationalSignatureLinkName = mutationalSignatureLinkName;
	}


	public void copyAll(OrderCase orderCase) {
		this.active = orderCase.active;
		this.authorizingPhysician = orderCase.authorizingPhysician;
		this.caseId = orderCase.caseId;
		this.caseName = orderCase.caseName;
		this.dateOfBirth = orderCase.dateOfBirth;
		this.epicOrderDate = orderCase.epicOrderDate;
		this.epicOrderNumber = orderCase.epicOrderNumber;
		this.gender = orderCase.gender;
		this.icd10 = orderCase.icd10;
		this.medicalRecordNumber = orderCase.medicalRecordNumber;
		this.normalId = orderCase.normalId;
		this.normalTissueType = orderCase.normalTissueType;
		this.orderingPhysician = orderCase.orderingPhysician;
		this.patientName = orderCase.patientName;
		this.tumorBlock = orderCase.tumorBlock;
		this.tumorCollectionDate = orderCase.tumorCollectionDate;
		this.tumorId = orderCase.tumorId;
		this.tumorTissueType = orderCase.tumorTissueType;
		this.user = orderCase.user;
		this.assignedTo = orderCase.assignedTo;
		this.receivedDate = orderCase.receivedDate;
		this.variants = orderCase.variants;
		this.institution = orderCase.institution;
		this.normalBam = orderCase.normalBam;
		this.tumorBam = orderCase.tumorBam;
		this.rnaBam = orderCase.rnaBam;
		this.tumorVcf = orderCase.tumorVcf;
		this.cnvs = orderCase.cnvs;
		this.translocations = orderCase.translocations;
		this.viruses = orderCase.viruses;
		this.oncotreeDiagnosis = orderCase.oncotreeDiagnosis;
		this.totalCases = orderCase.totalCases;
		this.caseHistory = orderCase.caseHistory;
		this.type = orderCase.type;
		this.clinicalStage = orderCase.clinicalStage;
		this.treatmentStatus = orderCase.treatmentStatus;
		this.labTestName = orderCase.labTestName;
		this.rawAvgDepth = orderCase.rawAvgDepth;
		this.rawPctOver100X = orderCase.rawPctOver100X;
		this.dedupAvgDepth = orderCase.dedupAvgDepth;
		this.dedupPctOver100X = orderCase.dedupPctOver100X;
		this.tumorMutationBurden = orderCase.tumorMutationBurden;
		this.labNotes = orderCase.labNotes;
		this.tumorPercent = orderCase.tumorPercent;
		this.groupIds = orderCase.groupIds;
		this.storageType = orderCase.storageType;
		this.caseOwner = orderCase.caseOwner;
		this.tumorMutationBurdenClass = orderCase.tumorMutationBurdenClass;
		this.msi = orderCase.msi;
		this.msiClass = orderCase.msiClass;
		this.mutationalSignatureImage = orderCase.mutationalSignatureImage;
		this.mutationalSignatureLinkName = orderCase.mutationalSignatureLinkName;
		this.tumorPanel = orderCase.tumorPanel;
	}


	public List<MutationalSignatureData> getMutationalSignatureData() {
		return mutationalSignatureData;
	}


	public void setMutationalSignatureData(List<MutationalSignatureData> mutationalSignatureData) {
		this.mutationalSignatureData = mutationalSignatureData;
	}


	public String getTumorPanel() {
		return tumorPanel;
	}


	public void setTumorPanel(String tumorPanel) {
		this.tumorPanel = tumorPanel;
	}


	public String getHl7OrderId() {
		return hl7OrderId;
	}


	public void setHl7OrderId(String hl7OrderId) {
		this.hl7OrderId = hl7OrderId;
	}


	public String getHl7SampleId() {
		return hl7SampleId;
	}


	public void setHl7SampleId(String hl7SampleId) {
		this.hl7SampleId = hl7SampleId;
	}






}
