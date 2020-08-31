package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnswerLowExonCoverage {
	
	Boolean success = true;
	Boolean isAllowed = true;
	String sampleLabId;
	List<SampleLowCoverageFromQC> lowCoverages = new ArrayList<SampleLowCoverageFromQC>();
	
	public AnswerLowExonCoverage() {
		super();
	}
	
	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

	public String getSampleLabId() {
		return sampleLabId;
	}

	public void setSampleLabId(String sampleLabId) {
		this.sampleLabId = sampleLabId;
	}

	public List<SampleLowCoverageFromQC> getLowCoverages() {
		return lowCoverages;
	}

	public void setLowCoverages(List<SampleLowCoverageFromQC> lowCoverages) {
		this.lowCoverages = lowCoverages;
	}

	
	

}
