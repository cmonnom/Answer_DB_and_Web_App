package utsw.bicf.answer.model.hybrid;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import utsw.bicf.answer.model.extmapping.Annotation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportAnnotation {
	String category;
	String text;
	
	public ReportAnnotation() {
	}
	
	public ReportAnnotation(Annotation annotation) {
		this.category = annotation.getCategory();
		this.text = annotation.getText();
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
