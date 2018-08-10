package utsw.bicf.answer.controller.serialization.vuetify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.model.hybrid.OrderCaseAll;

public class AllOrderCasesSummary {
	
	OrderCaseAllSummary casesAll;
	OrderCaseForUserSummary casesForUser;
	Boolean isAllowed = true;
	Boolean success = false;
	
	public AllOrderCasesSummary(OrderCaseAllSummary allSummary, OrderCaseForUserSummary forUserSummary) {
		super();
		this.casesAll = allSummary;
		this.casesForUser = forUserSummary;
	}


	public OrderCaseForUserSummary getCasesForUser() {
		return casesForUser;
	}

	public void setCasesForUser(OrderCaseForUserSummary casesForUser) {
		this.casesForUser = casesForUser;
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


	public OrderCaseAllSummary getCasesAll() {
		return casesAll;
	}


	public void setCasesAll(OrderCaseAllSummary casesAll) {
		this.casesAll = casesAll;
	}
	

}
