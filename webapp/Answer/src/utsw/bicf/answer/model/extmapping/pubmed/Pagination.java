package utsw.bicf.answer.model.extmapping.pubmed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pagination {
	
	@JsonProperty("MedlinePgn")
	Integer medlinePgn;
	
	
	

	public Pagination() {
	}

	public Integer getMedlinePgn() {
		return medlinePgn;
	}

	public void setMedlinePgn(Integer medlinePgn) {
		this.medlinePgn = medlinePgn;
	}




	
}
