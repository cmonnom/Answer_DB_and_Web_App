package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Annotation {
	

	@JsonProperty("_id")
	MangoDBId mangoDBId;
	
	//Name of the organization this annotation is from (eg. UTSW)
	String origin;
	String text;
	String caseId;
	String geneId;
	String variantId;
	Integer userId;
	String createdDate;
	String modifiedDate;
	Boolean isVisible = true;
	Boolean markedForDeletion = false;
	List<String> pmids;
	Boolean isTumorSpecific;
	Boolean isCaseSpecific;
	Boolean isVariantSpecific;
	Boolean isGeneSpecific;
	String category;
	String fullName;
	String classification;
	String tier;
	List<String> nctids;
	
	public Annotation() {
		
	}
	
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

	public MangoDBId getMangoDBId() {
		return mangoDBId;
	}

	public void setMangoDBId(MangoDBId mangoDBId) {
		this.mangoDBId = mangoDBId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
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

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getGeneId() {
		return geneId;
	}

	public void setGeneId(String geneId) {
		this.geneId = geneId;
	}

	public String getVariantId() {
		return variantId;
	}

	public void setVariantId(String variantId) {
		this.variantId = variantId;
	}

	public List<String> getPmids() {
		return pmids;
	}

	public void setPmids(List<String> pmids) {
		this.pmids = pmids;
	}

	public Boolean getIsTumorSpecific() {
		return isTumorSpecific;
	}

	public void setIsTumorSpecific(Boolean isTumorSpecific) {
		this.isTumorSpecific = isTumorSpecific;
	}

	public Boolean getIsCaseSpecific() {
		return isCaseSpecific;
	}

	public void setIsCaseSpecific(Boolean isCaseSpecific) {
		this.isCaseSpecific = isCaseSpecific;
	}

	public Boolean getIsVariantSpecific() {
		return isVariantSpecific;
	}

	public void setIsVariantSpecific(Boolean isVariantSpecific) {
		this.isVariantSpecific = isVariantSpecific;
	}

	public Boolean getIsGeneSpecific() {
		return isGeneSpecific;
	}

	public void setIsGeneSpecific(Boolean isGeneSpecific) {
		this.isGeneSpecific = isGeneSpecific;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getTier() {
		return tier;
	}

	public void setTier(String tier) {
		this.tier = tier;
	}

	public List<String> getNctids() {
		return nctids;
	}

	public void setNtcids(List<String> nctids) {
		this.nctids = nctids;
	}


	
}
