package utsw.bicf.answer.reporting.ehr.model;

import java.util.ArrayList;
import java.util.List;

public class TempusTrial {
	
	List<String> biomarkers = new ArrayList<String>();
	String nctId;
	String title;
	
	public List<String> getBiomarkers() {
		return biomarkers;
	}
	public void setBiomarkers(List<String> biomarkers) {
		this.biomarkers = biomarkers;
	}
	public String getNctId() {
		return nctId;
	}
	public void setNctId(String nctId) {
		this.nctId = nctId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	

	

}
