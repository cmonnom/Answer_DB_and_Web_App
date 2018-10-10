package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExistingReportsSummary {
	
	List<ReportSummary> reports;
	Boolean isAllowed = true;
	Boolean success = true;
	
	public ExistingReportsSummary(List<ReportSummary> reports) {
		super();
		this.reports = reports;
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public List<ReportSummary> getReports() {
		return reports;
	}

	public void setReports(List<ReportSummary> reports) {
		this.reports = reports;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

}
