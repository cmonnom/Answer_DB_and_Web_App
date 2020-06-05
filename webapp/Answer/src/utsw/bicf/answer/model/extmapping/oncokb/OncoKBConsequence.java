package utsw.bicf.answer.model.extmapping.oncokb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OncoKBConsequence {
	
	String term;
	String description;
	
	public OncoKBConsequence() {
		super();
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}







}
