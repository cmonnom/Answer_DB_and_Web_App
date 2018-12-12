package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HeaderConfigSummaries {
	
	List<HeaderConfigSummary> summaries;
	Boolean isAllowed = true;

	public HeaderConfigSummaries() {
	}

	public HeaderConfigSummaries(List<HeaderConfigSummary> summaries) {
		this.summaries = summaries;
	}

	public String createVuetifyObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public List<HeaderConfigSummary> getSummaries() {
		return summaries;
	}

	public void setSummaries(List<HeaderConfigSummary> summaries) {
		this.summaries = summaries;
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}



}
