package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseHistory {
	
	
	String time;
	Integer step;
	
	public CaseHistory() {
		
	}


	public Integer getStep() {
		return step;
	}


	public String getTime() {
		return time;
	}





}
