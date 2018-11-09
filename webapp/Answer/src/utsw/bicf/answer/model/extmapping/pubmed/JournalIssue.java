package utsw.bicf.answer.model.extmapping.pubmed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JournalIssue {
	
	@JsonProperty("Volume")
	Integer volume;
	@JsonProperty("Issue")
	Integer Issue;
	

	public JournalIssue() {
	}


	public Integer getVolume() {
		return volume;
	}


	public void setVolume(Integer volume) {
		this.volume = volume;
	}


	public Integer getIssue() {
		return Issue;
	}


	public void setIssue(Integer issue) {
		Issue = issue;
	}











	
}
