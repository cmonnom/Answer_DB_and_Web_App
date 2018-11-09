package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.model.hybrid.PubMed;

public class PubmedSummary {
	
	List<PubMed> pubmeds;
	Boolean isAllowed = true;
	

	public PubmedSummary(List<PubMed> pubmeds) {
		super();
		this.pubmeds = pubmeds;
	}

	
	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}


	public List<PubMed> getPubmeds() {
		return pubmeds;
	}


	public void setPubmeds(List<PubMed> pubmeds) {
		this.pubmeds = pubmeds;
	}


	public Boolean getIsAllowed() {
		return isAllowed;
	}


	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}
}
