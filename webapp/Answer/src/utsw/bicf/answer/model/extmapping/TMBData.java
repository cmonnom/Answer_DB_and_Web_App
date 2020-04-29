package utsw.bicf.answer.model.extmapping;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TMBData extends WhiskerData {
	
	String tissueGroup; //Lymphoid/Myeloid or Other Tissue
	List<WhiskerPerCaseData> tmbs = new ArrayList<WhiskerPerCaseData>();
	
	public TMBData() {
		super();
	}

	public String getTissueGroup() {
		return tissueGroup;
	}

	public void setTissueGroup(String tissueGroup) {
		this.tissueGroup = tissueGroup;
	}

	public List<WhiskerPerCaseData> getTmbs() {
		return tmbs;
	}

	public void setTmbs(List<WhiskerPerCaseData> tmbs) {
		this.tmbs = tmbs;
	}

	@Override
	public String getLabel() {
		return tissueGroup;
	}

	@Override
	public List<WhiskerPerCaseData> getPerCaseList() {
		return tmbs;
	}



	
		



}
