package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommitAnnotationResponse {
	

	Boolean annotationModified;
	List<MongoDBId> newAnnotations;
	List<MongoDBId> modifiedAnnotations;
	
	public CommitAnnotationResponse() {
		
	}

	public Boolean getAnnotationModified() {
		return annotationModified;
	}

	public void setAnnotationModified(Boolean annotationModified) {
		this.annotationModified = annotationModified;
	}

	public List<MongoDBId> getNewAnnotations() {
		return newAnnotations;
	}

	public void setNewAnnotations(List<MongoDBId> newAnnotations) {
		this.newAnnotations = newAnnotations;
	}

	public List<MongoDBId> getModifiedAnnotations() {
		return modifiedAnnotations;
	}

	public void setModifiedAnnotations(List<MongoDBId> modifiedAnnotations) {
		this.modifiedAnnotations = modifiedAnnotations;
	}




	
}
