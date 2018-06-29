package utsw.bicf.answer.model.extmapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AbstractReference {

	public AbstractReference() {
	}


	Boolean isAllowed = true;

	@JsonProperty("_id")
	MangoDBId mangoDBId;
	List<Annotation> utswAnnotations;
	

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


	public Boolean getIsAllowed() {
		return isAllowed;
	}


	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}


	public MangoDBId getMangoDBId() {
		return mangoDBId;
	}


	public List<Annotation> getUtswAnnotations() {
		return utswAnnotations;
	}

}
