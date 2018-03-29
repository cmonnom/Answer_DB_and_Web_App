package utsw.bicf.answer.clarity.api.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Handle responses from API calls in the form of a string.
 * Use addError to add error message to the list which will
 * be formatted in createResponse.
 * @author Guillaume
 *
 */
public class APIResponse {
	
	String message;
	List<String> errors = new ArrayList<String>();
	String results;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	
	public void addMessage(String errorMessage) {
		errors.add(errorMessage);
	}
	public String createResponse(String userAgent) {
		String lineSeparator = "<br/>";
		if (userAgent.contains("curl") || userAgent.contains("PowerShell")) { //command line instead of web browser
			lineSeparator = "\n";
		}
		StringBuilder sb = new StringBuilder(message);
		sb.append(lineSeparator);
		if (errors.isEmpty()) {
			sb.append("\n").append("Successful"); 
		}
		else {
			sb.append(errors.stream().collect(Collectors.joining(lineSeparator)));
		}
		return sb.toString();
	}
}
