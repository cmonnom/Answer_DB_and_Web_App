package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExistingReports {
	
	List<Report> result;

	public ExistingReports() {
	}

	public List<Report> getResult() {
		return result;
	}

	public void setResult(List<Report> result) {
		this.result = result;
	}


}
