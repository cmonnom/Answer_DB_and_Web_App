package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import utsw.bicf.answer.reporting.parse.BiomarkerTrialsRow;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BiomarkerTrials {
	
	List<BiomarkerTrialsRow> trials;

	public BiomarkerTrials() {
	}

	public List<BiomarkerTrialsRow> getTrials() {
		return trials;
	}

	public void setTrials(List<BiomarkerTrialsRow> trials) {
		this.trials = trials;
	}


}
