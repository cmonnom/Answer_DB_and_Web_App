package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.model.Annotation;

public class AnnotationSummary {
	
	List<Annotation> utswAnnotations;
	List<Annotation> userAnnotations;
	Boolean isAllowed = true;
	
	public AnnotationSummary(List<Annotation> utswAnnotations, List<Annotation> userAnnotations) {
		super();
		this.utswAnnotations = utswAnnotations;
		this.userAnnotations = userAnnotations;
	}
	
	public List<Annotation> getUtswAnnotations() {
		return utswAnnotations;
	}
	public void setUtswAnnotations(List<Annotation> utswAnnotations) {
		this.utswAnnotations = utswAnnotations;
	}
	public List<Annotation> getUserAnnotations() {
		return userAnnotations;
	}
	public void setUserAnnotations(List<Annotation> userAnnotations) {
		this.userAnnotations = userAnnotations;
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

}
