package utsw.bicf.answer.model.extmapping;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WhiskerData {
	
	String label = null;
	List<WhiskerPerCaseData> perCaseList = new ArrayList<WhiskerPerCaseData>();
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public List<WhiskerPerCaseData> getPerCaseList() {
		return perCaseList;
	}
	public void setPerCaseList(List<WhiskerPerCaseData> perCaseList) {
		this.perCaseList = perCaseList;
	}
	public WhiskerData() {
		super();
	}
	




	
		



}
