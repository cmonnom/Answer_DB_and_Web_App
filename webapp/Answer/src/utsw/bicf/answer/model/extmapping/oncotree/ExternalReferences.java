package utsw.bicf.answer.model.extmapping.oncotree;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExternalReferences {

	@JsonProperty("UMLS")
	List<String> umls;
	
	@JsonProperty("NCI")
	List<String> nci;

	public List<String> getUmls() {
		return umls;
	}

	public void setUmls(List<String> umls) {
		this.umls = umls;
	}

	public List<String> getNci() {
		return nci;
	}

	public void setNci(List<String> nci) {
		this.nci = nci;
	}
	
}
