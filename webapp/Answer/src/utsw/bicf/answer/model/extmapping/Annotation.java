package utsw.bicf.answer.model.extmapping;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Annotation {
	
	public Annotation() {
		
	}

	@JsonProperty("_id")
	MangoDBId mangoDBId;
	
	//Name of the organization this annotation is from (eg. UTSW)
	String origin;
	String text;
	String userId;
	String createdDate;
	String modifiedDate;
	Boolean deleted;
	Boolean isVisible = true;
	Boolean markedForDeletion = false;

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}


	public Boolean getIsVisible() {
		return isVisible;
	}

	public Boolean getMarkedForDeletion() {
		return markedForDeletion;
	}

	public Boolean getDeleted() {
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public MangoDBId getMangoDBId() {
		return mangoDBId;
	}

	public void setMangoDBId(MangoDBId mangoDBId) {
		this.mangoDBId = mangoDBId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setIsVisible(Boolean isVisible) {
		this.isVisible = isVisible;
	}

	public void setMarkedForDeletion(Boolean markedForDeletion) {
		this.markedForDeletion = markedForDeletion;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	
}
