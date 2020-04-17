package utsw.bicf.answer.model.reactome;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchResult {
	
	List<SearchEntry> entries;
	
	public SearchResult() {
		super();
	}

	public List<SearchEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<SearchEntry> entries) {
		this.entries = entries;
	}


}
