package utsw.bicf.answer.controller.serialization;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.model.UserPref;

/**
 * Helper class to return an ajax response
 * and allow errors or successes not due to login or xss issue
 * @author Guillaume
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AjaxResponse {
	
	public AjaxResponse() {
		super();
	}


	Boolean isAllowed = true;
	boolean success;
	String message;
	Boolean uiProceed = false;
	Boolean skipSnackBar = false;
	//you can pass user preferences to the Ajax response this way 
	//so it's up to date with the latest changes
	UserPref userPrefs; 
	Object payload; //any object that could be returned by MongoDB
	
	public Boolean getIsAllowed() {
		return isAllowed;
	}


	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}


	public boolean getSuccess() {
		return success;
	}


	public void setSuccess(boolean success) {
		this.success = success;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	
	
	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}


	public Boolean getUiProceed() {
		return uiProceed;
	}


	public void setUiProceed(Boolean uiProceed) {
		this.uiProceed = uiProceed;
	}


	public Boolean getSkipSnackBar() {
		return skipSnackBar;
	}


	public void setSkipSnackBar(Boolean skipSnackBar) {
		this.skipSnackBar = skipSnackBar;
	}


	public UserPref getUserPrefs() {
		return userPrefs;
	}


	public void setUserPrefs(UserPref userPrefs) {
		this.userPrefs = userPrefs;
	}


	public Object getPayload() {
		return payload;
	}


	public void setPayload(Object payload) {
		this.payload = payload;
	}


	
}
