package utsw.bicf.answer.controller.serialization.vuetify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.model.VariantFilterList;

public class VariantFilterListSaved {
	
	VariantFilterList savedFilterSet;
	Boolean isAllowed;
	Boolean success;
	
	public VariantFilterListSaved() {
		super();
	}

	public VariantFilterList getSavedFilterSet() {
		return savedFilterSet;
	}

	public void setSavedFilterSet(VariantFilterList savedFilterSet) {
		this.savedFilterSet = savedFilterSet;
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	

}



