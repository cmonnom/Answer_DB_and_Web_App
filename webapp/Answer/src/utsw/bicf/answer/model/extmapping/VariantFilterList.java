package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VariantFilterList {

	List<VariantFilter> filters;


	public String createJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}


	public List<VariantFilter> getFilters() {
		return filters;
	}


	public void setFilters(List<VariantFilter> filters) {
		this.filters = filters;
	}
}
