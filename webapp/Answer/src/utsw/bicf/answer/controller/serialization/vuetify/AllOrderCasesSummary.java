package utsw.bicf.answer.controller.serialization.vuetify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Summary object for all case tables on the home page
 * @author Guillaume
 *
 */
public class AllOrderCasesSummary {
	
	OrderCaseAllSummary casesAll;
	OrderCaseForUserSummary casesForUser;
	OrderCaseForUserSummary casesForUserCompleted;
	OrderCaseFinalizedSummary casesFinalized;
	Boolean isAllowed = true;
	Boolean success = false;
	
	public AllOrderCasesSummary(OrderCaseAllSummary allSummary, OrderCaseForUserSummary forUserSummary, OrderCaseForUserSummary forUserCompletedSummary, OrderCaseFinalizedSummary finalizedSummary) {
		super();
		this.casesAll = allSummary;
		this.casesForUser = forUserSummary;
		this.casesForUserCompleted = forUserCompletedSummary;
		this.casesFinalized = finalizedSummary;
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


	public OrderCaseFinalizedSummary getCasesFinalized() {
		return casesFinalized;
	}


	public void setCasesFinalized(OrderCaseFinalizedSummary casesFinalized) {
		this.casesFinalized = casesFinalized;
	}


	public OrderCaseForUserSummary getCasesForUserCompleted() {
		return casesForUserCompleted;
	}


	public void setCasesForUserCompleted(OrderCaseForUserSummary casesForUserCompleted) {
		this.casesForUserCompleted = casesForUserCompleted;
	}
	

}
