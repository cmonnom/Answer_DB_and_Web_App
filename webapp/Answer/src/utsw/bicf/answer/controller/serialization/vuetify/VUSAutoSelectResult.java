package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.ArrayList;
import java.util.List;

public class VUSAutoSelectResult {
	
	List<String> successOids = new ArrayList<String>();
	List<String> failedOids = new ArrayList<String>();
	List<String> untouchedOids = new ArrayList<String>();
	
	List<String> successNotation = new ArrayList<String>();
	List<String> failedNotation = new ArrayList<String>();
	List<String> untouchedNotation = new ArrayList<String>();
	public VUSAutoSelectResult() {
		super();
	}
	public List<String> getSuccessOids() {
		return successOids;
	}
	public void setSuccessOids(List<String> successOids) {
		this.successOids = successOids;
	}
	public List<String> getFailedOids() {
		return failedOids;
	}
	public void setFailedOids(List<String> failedOids) {
		this.failedOids = failedOids;
	}
	public List<String> getUntouchedOids() {
		return untouchedOids;
	}
	public void setUntouchedOids(List<String> untouchedOids) {
		this.untouchedOids = untouchedOids;
	}
	public List<String> getSuccessNotation() {
		return successNotation;
	}
	public void setSuccessNotation(List<String> successNotation) {
		this.successNotation = successNotation;
	}
	public List<String> getFailedNotation() {
		return failedNotation;
	}
	public void setFailedNotation(List<String> failedNotation) {
		this.failedNotation = failedNotation;
	}
	public List<String> getUntouchedNotation() {
		return untouchedNotation;
	}
	public void setUntouchedNotation(List<String> untouchedNotation) {
		this.untouchedNotation = untouchedNotation;
	}
	

}
