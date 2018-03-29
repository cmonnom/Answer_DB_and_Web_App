package utsw.bicf.answer.reporting.finalreport;

public class Patient {
	
	String firstName;
	String lastName;
	String MRN;
	String dateOfBirth;
	String sex;
	String orderNb;
	String labAccessionNb;
	String reportAccessionNb;
	String tumorSpecimenNb;
	String germlineSpecimenNb;
	
	String orderedBy;
	String institution;
	String tumorTissue;
	String germlineTissue;
	String icd10;
	String clinicalStage;
	String treatmentStatus;
	
	String orderDate;
	String tumorCollectionDate;
	String labReceivedDate;
	String reportDate;
	String reportSignedBy;
	
	public static Patient createFakePatient() {
		Patient p = new Patient();
		p.setFirstName("AMANDA");
		p.setLastName("ZZZTEST");
		p.setMRN("72547894");
		p.setDateOfBirth("20 Dec 1967");
		p.setSex("female");
		p.setOrderNb("338809147");
		p.setLabAccessinoNb("338809147-72547894");
		p.setReportAccessionNb("338809147-v1-3");
		p.setTumorSpecimenNb("CHD14-4433");
		p.setGermlineSpecimenNb("W1043374");
		
		p.setOrderedBy("MUJEEB ABDUL BASIT");
		p.setInstitution("UTSW NGS Clinical Laboratory");
		p.setTumorTissue("Spleen");
		p.setGermlineTissue("Blood");
		p.setIcd10("Hairy cell leukemia not having achieved remission");
		p.setClinicalStage("");
		p.setTreatmentStatus("");
		
		p.setOrderDate("30 Aug 2017");
		p.setTumorCollectionDate("22 May 2014");
		p.setLabReceivedDate("31 Aug 2017");
		p.setReportDate("12 Sep 2017");
		p.setReportSignedBy("Andrew Quinn");
		
		return p;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMRN() {
		return MRN;
	}

	public void setMRN(String mRN) {
		MRN = mRN;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getOrderNb() {
		return orderNb;
	}

	public void setOrderNb(String orderNb) {
		this.orderNb = orderNb;
	}

	public String getLabAccessionNb() {
		return labAccessionNb;
	}

	public void setLabAccessinoNb(String labAccessinoNb) {
		this.labAccessionNb = labAccessinoNb;
	}

	public String getReportAccessionNb() {
		return reportAccessionNb;
	}

	public void setReportAccessionNb(String reportAccessionNb) {
		this.reportAccessionNb = reportAccessionNb;
	}

	public String getTumorSpecimenNb() {
		return tumorSpecimenNb;
	}

	public void setTumorSpecimenNb(String tumorSpecimenNb) {
		this.tumorSpecimenNb = tumorSpecimenNb;
	}

	public String getGermlineSpecimenNb() {
		return germlineSpecimenNb;
	}

	public void setGermlineSpecimenNb(String germlineSpecimen) {
		this.germlineSpecimenNb = germlineSpecimen;
	}

	public String getOrderedBy() {
		return orderedBy;
	}

	public void setOrderedBy(String orderedBy) {
		this.orderedBy = orderedBy;
	}

	public String getInstitution() {
		return institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getTumorTissue() {
		return tumorTissue;
	}

	public void setTumorTissue(String tumorTissue) {
		this.tumorTissue = tumorTissue;
	}

	public String getGermlineTissue() {
		return germlineTissue;
	}

	public void setGermlineTissue(String germlineTissue) {
		this.germlineTissue = germlineTissue;
	}

	public String getIcd10() {
		return icd10;
	}

	public void setIcd10(String icd10) {
		this.icd10 = icd10;
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

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public String getTumorCollectionDate() {
		return tumorCollectionDate;
	}

	public void setTumorCollectionDate(String tumorCollectionDate) {
		this.tumorCollectionDate = tumorCollectionDate;
	}

	public String getLabReceivedDate() {
		return labReceivedDate;
	}

	public void setLabReceivedDate(String labReceivedDate) {
		this.labReceivedDate = labReceivedDate;
	}

	public String getReportDate() {
		return reportDate;
	}

	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}

	public String getReportSignedBy() {
		return reportSignedBy;
	}

	public void setReportSignedBy(String reportSignedBy) {
		this.reportSignedBy = reportSignedBy;
	}

}
