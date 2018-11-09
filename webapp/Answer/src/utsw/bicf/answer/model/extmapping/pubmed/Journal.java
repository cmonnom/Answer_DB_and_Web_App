package utsw.bicf.answer.model.extmapping.pubmed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Journal {
	
	@JsonProperty("ISOAbbreviation")
	String isoAbbreviation;
	@JsonProperty("JournalIssue")
	JournalIssue journalIssue;
	

	public Journal() {
	}



	public String getIsoAbbreviation() {
		return isoAbbreviation;
	}



	public void setIsoAbbreviation(String isoAbbreviation) {
		this.isoAbbreviation = isoAbbreviation;
	}



	public JournalIssue getJournalIssue() {
		return journalIssue;
	}



	public void setJournalIssue(JournalIssue journalIssue) {
		this.journalIssue = journalIssue;
	}


	
}
