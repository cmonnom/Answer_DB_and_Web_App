package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClinicalTrialXML {
	
	@JsonProperty("brief_summary")
	BriefSummary briefSummary;

	public class BriefSummary {
		String textblock;

		public String getTextblock() {
			return textblock;
		}

		public void setTextblock(String textblock) {
			this.textblock = textblock;
		}
	}

	public BriefSummary getBriefSummary() {
		return briefSummary;
	}

	public void setBriefSummary(BriefSummary briefSummary) {
		this.briefSummary = briefSummary;
	}
	
}
