package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SelectedVariantIds {
	

	//Name of the organization this annotation is from (eg. UTSW)
	String selectedSNPVariantIds;
	String selectedCNVIds;
	String selectedTranslocationIds;
	
	public SelectedVariantIds() {
		
	}

	public String getSelectedSNPVariantIds() {
		return selectedSNPVariantIds;
	}

	public void setSelectedSNPVariantIds(String selectedSNPVariantIds) {
		this.selectedSNPVariantIds = selectedSNPVariantIds;
	}

	public String getSelectedCNVIds() {
		return selectedCNVIds;
	}

	public void setSelectedCNVIds(String selectedCNVIds) {
		this.selectedCNVIds = selectedCNVIds;
	}

	public String getSelectedTranslocationIds() {
		return selectedTranslocationIds;
	}

	public void setSelectedTranslocationIds(String selectedTranslocationIds) {
		this.selectedTranslocationIds = selectedTranslocationIds;
	}
	
	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}


	
}
