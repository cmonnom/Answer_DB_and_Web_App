package utsw.bicf.answer.model.extmapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
		if (utswAnnotations != null) {
			this.utswAnnotations = utswAnnotations.stream().sorted(new Comparator<Annotation>() {

				@Override
				public int compare(Annotation o1, Annotation o2) {
					if (o1.modifiedDate != null) {
						return o1.modifiedDate.compareTo(o2.modifiedDate);
					}
					return 0;
				}
			}).collect(Collectors.toList());
		}
		else {
			this.utswAnnotations = null;
		}
	}

	public void setMangoDBId(MangoDBId mangoDBId) {
		this.mangoDBId = mangoDBId;
	}

}
