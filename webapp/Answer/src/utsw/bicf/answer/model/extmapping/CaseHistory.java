package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseHistory {
	
	
	String dateStatus;
	Integer step;
	
	public CaseHistory() {
		
	}

	public String getDateStatus() {
		return dateStatus;
	}

	public Integer getStep() {
		return step;
	}





}
