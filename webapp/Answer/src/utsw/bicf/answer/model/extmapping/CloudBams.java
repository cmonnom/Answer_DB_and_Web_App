package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CloudBams {
	

	String normalBam;
	String tumorBam;
	String rnaBam;
	String normalBai;
	String tumorBai;
	String rnaBai;
	
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
	public String getNormalBai() {
		return normalBai;
	}
	public void setNormalBai(String normalBai) {
		this.normalBai = normalBai;
	}
	public String getTumorBai() {
		return tumorBai;
	}
	public void setTumorBai(String tumorBai) {
		this.tumorBai = tumorBai;
	}
	public String getRnaBai() {
		return rnaBai;
	}
	public void setRnaBai(String rnaBai) {
		this.rnaBai = rnaBai;
	}
	




	
}
