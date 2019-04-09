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
	OrderCaseFinalizedSummary casesFinalized;
	Boolean isAllowed = true;
	Boolean success = false;
	OrderCaseArchivedSummary casesArchived;
	
	public AllOrderCasesSummary(OrderCaseAllSummary allSummary, OrderCaseForUserSummary forUserSummary, OrderCaseFinalizedSummary finalizedSummary, OrderCaseArchivedSummary archivedSummary) {
		super();
		this.casesAll = allSummary;
		this.casesForUser = forUserSummary;
		this.casesFinalized = finalizedSummary;
		this.casesArchived = archivedSummary;
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


	public OrderCaseArchivedSummary getCasesArchived() {
		return casesArchived;
	}


	public void setCasesArchived(OrderCaseArchivedSummary casesArchived) {
		this.casesArchived = casesArchived;
	}
	

}
