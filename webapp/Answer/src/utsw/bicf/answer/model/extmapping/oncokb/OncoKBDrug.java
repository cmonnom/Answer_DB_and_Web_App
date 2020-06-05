package utsw.bicf.answer.model.extmapping.oncokb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OncoKBDrug {
	
	String ncitCode;
	String drugName;
	Integer priority;
	
	public OncoKBDrug() {
		super();
	}

	public String getNcitCode() {
		return ncitCode;
	}

	public void setNcitCode(String ncitCode) {
		this.ncitCode = ncitCode;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}









}
