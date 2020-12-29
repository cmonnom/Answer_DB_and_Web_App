package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="genie_sample")
public class GenieSample {
	
	public GenieSample() {
		
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="genie_sample_id")
	Integer genieSampleId;
	
	@Column(name="patient_id")
	String patientId;
	
	@Column(name="sample_id")
	String sampleId;
	
	@Column(name="oncotree_code")
	String oncotreeCode;
	
	@Column(name="cancer_type")
	String cancerType;
	
	@Column(name="assay_id")
	String assayId;

	public Integer getGenieSampleId() {
		return genieSampleId;
	}

	public void setGenieSampleId(Integer genieSampleId) {
		this.genieSampleId = genieSampleId;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getSampleId() {
		return sampleId;
	}

	public void setSampleId(String sampleId) {
		this.sampleId = sampleId;
	}

	public String getOncotreeCode() {
		return oncotreeCode;
	}

	public void setOncotreeCode(String oncotreeCode) {
		this.oncotreeCode = oncotreeCode;
	}

	public String getCancerType() {
		return cancerType;
	}

	public void setCancerType(String cancerType) {
		this.cancerType = cancerType;
	}

	public String getAssayId() {
		return assayId;
	}

	public void setAssayId(String assayId) {
		this.assayId = assayId;
	}
}
