package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferenceVariant {

	Boolean isAllowed = true;

	@JsonProperty("_id")
	MangoDBId mangoDBId;
	String chrom;
	Integer pos;
	String alt;
	String reference;
	String type;
	@JsonProperty("utsw_annotations")
	List<Annotation> utswAnnotations;

	public ReferenceVariant() {

	}

	public String getChrom() {
		return chrom;
	}

	public void setChrom(String chrom) {
		this.chrom = chrom;
	}

	public Integer getPos() {
		return pos;
	}

	public void setPos(Integer pos) {
		this.pos = pos;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public Boolean getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}

	public MangoDBId getMangoDBId() {
		return mangoDBId;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public List<Annotation> getUtswAnnotations() {
		return utswAnnotations;
	}

	public void setUtswAnnotations(List<Annotation> utswAnnotations) {
		this.utswAnnotations = utswAnnotations;
	}

	public void setMangoDBId(MangoDBId mangoDBId) {
		this.mangoDBId = mangoDBId;
	}

}
