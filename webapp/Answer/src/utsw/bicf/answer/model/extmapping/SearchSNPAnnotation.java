package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SearchSNPAnnotation {
	

	String geneSymbolOrSynonym;
	String variant;
	
	public SearchSNPAnnotation() {
		
	}

	
	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}


	public String getGeneSymbolOrSynonym() {
		return geneSymbolOrSynonym;
	}


	public void setGeneSymbolOrSynonym(String geneSymbolOrSynonym) {
		this.geneSymbolOrSynonym = geneSymbolOrSynonym;
	}


	public String getVariant() {
		return variant;
	}


	public void setVariant(String variant) {
		this.variant = variant;
	}


	
}
