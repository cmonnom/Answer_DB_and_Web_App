package utsw.bicf.answer.model.extmapping.oncokb;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OncoKBTreatment {
	
	Integer priority;
	List<OncoKBDrug> drugs;
	List<String> approvedIndications;
	
	public OncoKBTreatment() {
		super();
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public List<OncoKBDrug> getDrugs() {
		return drugs;
	}

	public void setDrugs(List<OncoKBDrug> drugs) {
		this.drugs = drugs;
	}

	public List<String> getApprovedIndications() {
		return approvedIndications;
	}

	public void setApprovedIndications(List<String> approvedIndications) {
		this.approvedIndications = approvedIndications;
	}










}
