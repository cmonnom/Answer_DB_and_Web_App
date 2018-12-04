package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Trial {
	

	String title;
	String phase;
	String nctId;
	String biomarker;
	String drugs;
	String contact;
	String location;
	
	Boolean isAllowed = true;
	boolean success = true;
	
	public Trial() {
		
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public String getNctId() {
		return nctId;
	}

	public void setNctId(String nctId) {
		this.nctId = nctId;
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}
	




	
}
