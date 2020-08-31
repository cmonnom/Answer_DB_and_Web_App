package utsw.bicf.answer.reporting.ehr.model;

import java.util.ArrayList;
import java.util.List;

public class TempusTherapy {
	
	String drug;
	List<String> pubmedIds = new ArrayList<String>();
	TempusVariant variant;
	
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
	public TempusVariant getVariant() {
		return variant;
	}
	public void setVariant(TempusVariant variant) {
		this.variant = variant;
	}

	

}
