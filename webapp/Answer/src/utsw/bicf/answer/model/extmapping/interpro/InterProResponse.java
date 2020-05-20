package utsw.bicf.answer.model.extmapping.interpro;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InterProResponse {
	
	@JsonProperty("results")
	List<Result> results;
	
	public InterProResponse() {
		super();
	}

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}



}
