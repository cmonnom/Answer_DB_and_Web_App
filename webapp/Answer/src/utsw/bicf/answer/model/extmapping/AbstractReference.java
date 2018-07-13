package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractReference {

	public AbstractReference() {
	}


	Boolean isAllowed = true;

	@JsonProperty("_id")
	MongoDBId mongoDBId;
	List<Annotation> utswAnnotations;
	

	public void setUtswAnnotations(List<Annotation> utswAnnotations) {
		this.utswAnnotations = utswAnnotations;
	}

	public void setMongoDBId(MongoDBId mongoDBId) {
		this.mongoDBId = mongoDBId;
	}


	public Boolean getIsAllowed() {
		return isAllowed;
	}


	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}


	public MongoDBId getMongoDBId() {
		return mongoDBId;
	}


	public List<Annotation> getUtswAnnotations() {
		return utswAnnotations;
	}

}
