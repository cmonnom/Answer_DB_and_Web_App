package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.DataTableFilter;

public class VariantFilterItems {
	
	Boolean isAllowed = true;
	List<DataTableFilter> filters;

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

	public String createVuetifyObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public List<DataTableFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<DataTableFilter> filters) {
		this.filters = filters;
	}
}
