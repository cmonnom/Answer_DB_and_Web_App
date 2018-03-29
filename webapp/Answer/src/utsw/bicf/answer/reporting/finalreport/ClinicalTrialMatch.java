package utsw.bicf.answer.reporting.finalreport;

public class ClinicalTrialMatch {
	
	String description;
	String location;
	String recruiting;
	String contact;
	String urlLabel;
	String url;
	String biomarker;
	String drugs;
	
	public ClinicalTrialMatch(String description, String location, String recruiting, String contact, String urlLabel,
			String url, String biomarker, String drugs) {
		super();
		this.description = description;
		this.location = location;
		this.recruiting = recruiting;
		this.contact = contact;
		this.urlLabel = urlLabel;
		this.url = url;
		this.biomarker = biomarker;
		this.drugs = drugs;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getRecruiting() {
		return recruiting;
	}
	public void setRecruiting(String recruiting) {
		this.recruiting = recruiting;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getUrlLabel() {
		return urlLabel;
	}
	public void setUrlLabel(String urlLabel) {
		this.urlLabel = urlLabel;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getBiomarker() {
		return biomarker;
	}
	public void setBiomarker(String biomarker) {
		this.biomarker = biomarker;
	}
	public String getDrugs() {
		return drugs;
	}
	public void setDrugs(String drugs) {
		this.drugs = drugs;
	}
	
	

}
