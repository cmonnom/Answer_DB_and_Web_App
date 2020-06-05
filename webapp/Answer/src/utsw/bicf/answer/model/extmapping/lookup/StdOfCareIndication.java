package utsw.bicf.answer.model.extmapping.lookup;

import java.util.ArrayList;
import java.util.List;

public class StdOfCareIndication {

	String drugs;
	List<String> indications = new ArrayList<String>();
	String summary;
	String pubmedUrl; 
	
	public StdOfCareIndication() {
		super();
	}

	public String getDrugs() {
		return drugs;
	}

	public void setDrugs(String drugs) {
		this.drugs = drugs;
	}


	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getPubmedUrl() {
		return pubmedUrl;
	}

	public void setPubmedUrl(String pubmedUrl) {
		this.pubmedUrl = pubmedUrl;
	}

	public List<String> getIndications() {
		return indications;
	}

	public void setIndications(List<String> indications) {
		this.indications = indications;
	}
	
}
