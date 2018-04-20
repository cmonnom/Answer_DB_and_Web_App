package utsw.bicf.answer.controller.serialization.vuetify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AllOrderCasesSummary {
	
	OrderCaseAvailableSummary casesAvailable;
	OrderCaseForUserSummary casesForUser;
	OrderCaseAssignedSummary casesAssigned;
	Boolean isAllowed = true;
	Boolean success = false;
	
	public AllOrderCasesSummary(OrderCaseAvailableSummary availSummary, OrderCaseForUserSummary forUserSummary,
			OrderCaseAssignedSummary assignedSummary) {
		super();
		this.casesAvailable = availSummary;
		this.casesForUser = forUserSummary;
		this.casesAssigned = assignedSummary;
	}

	public OrderCaseAvailableSummary getCasesAvailable() {
		return casesAvailable;
	}

	public void setCasesAvailable(OrderCaseAvailableSummary casesAvailable) {
		this.casesAvailable = casesAvailable;
	}

	public OrderCaseForUserSummary getCasesForUser() {
		return casesForUser;
	}

	public void setCasesForUser(OrderCaseForUserSummary casesForUser) {
		this.casesForUser = casesForUser;
	}

	public OrderCaseAssignedSummary getCasesAssigned() {
		return casesAssigned;
	}

	public void setCasesAssigned(OrderCaseAssignedSummary casesAssigned) {
		this.casesAssigned = casesAssigned;
	}
	
	public String createVuetifyObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}
	

}
