package utsw.bicf.answer.reporting.ehr.model;

import java.util.ArrayList;
import java.util.List;

public class HL7Therapy {
	
	String drug;
	List<String> pubmedIds = new ArrayList<String>();
	HL7Variant variant;
	String level;
	String indication;
	
	public String getDrug() {
		return drug;
	}
	public void setDrug(String drug) {
		this.drug = drug;
	}
	public List<String> getPubmedIds() {
		return pubmedIds;
	}
	public void setPubmedIds(List<String> pubmedIds) {
		this.pubmedIds = pubmedIds;
	}
	public HL7Variant getVariant() {
		return variant;
	}
	public void setVariant(HL7Variant variant) {
		this.variant = variant;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getIndication() {
		return indication;
	}
	public void setIndication(String indication) {
		this.indication = indication;
	}

	

}
