package utsw.bicf.answer.controller.serialization;

import org.springframework.ui.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * After a user succesfully logged in,
 * the LoginController should return a TargetPage JSON string
 * with information about if the login worked, the reason it didn't work,
 * the destination url
 * @author Guillaume
 *
 */
public class TargetPage {
	
	Boolean success;
	String reason;
	String urlRedirect;
	Boolean isAjax;
	Boolean isXss;
	Boolean isLogin;
	Object payload;
	
	public TargetPage(Boolean success, String reason, String urlRedirect, Boolean isAjax) {
		super();
		this.success = success;
		this.reason = reason;
		this.urlRedirect = urlRedirect;
		this.isAjax = isAjax;
	}
	
	public TargetPage(Model model) {
		super();
		this.success = (Boolean) model.asMap().get("isAllowed");
		Object reasonArg = model.asMap().get("reason");
		this.reason = reasonArg != null ? reasonArg.toString() : null;
		Object urlRedirectArg = model.asMap().get("urlRedirect");
		this.urlRedirect = urlRedirectArg != null ? urlRedirectArg.toString() : null;
		this.isAjax =  (Boolean) model.asMap().get("isAjax");
		this.isXss = (Boolean) model.asMap().get("isXss");
		this.isLogin = (Boolean) model.asMap().get("isLogin");
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String toJSONString() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public String getUrlRedirect() {
		return urlRedirect;
	}

	public void setUrlRedirect(String urlRedirect) {
		this.urlRedirect = urlRedirect;
	}

	public Boolean getIsAjax() {
		return isAjax;
	}

	public void setIsAjax(Boolean isAjax) {
		this.isAjax = isAjax;
	}

	public Boolean getIsXss() {
		return isXss;
	}

	public void setIsXss(Boolean isXss) {
		this.isXss = isXss;
	}

	public Boolean getIsLogin() {
		return isLogin;
	}

	public void setIsLogin(Boolean isLogin) {
		this.isLogin = isLogin;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}
	

}
