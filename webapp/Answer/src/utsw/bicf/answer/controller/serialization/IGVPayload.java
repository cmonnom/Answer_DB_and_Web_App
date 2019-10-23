package utsw.bicf.answer.controller.serialization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IGVPayload {
	
	String sessionType;
	String link;
	
	public IGVPayload() {
		super();
	}

	public IGVPayload(String sessionType, String link) {
		super();
		this.sessionType = sessionType;
		this.link = link;
	}

	public String getSessionType() {
		return sessionType;
	}

	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	
	
	
}
