package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SelectedVariantIds {
	

	//Name of the organization this annotation is from (eg. UTSW)
	List<String> selectedSNPVariantIds;
	List<String> selectedCNVIds;
	List<String> selectedTranslocationIds;
	String userId;
	
	public SelectedVariantIds() {
		
	}

	public List<String> getSelectedSNPVariantIds() {
		return selectedSNPVariantIds;
	}

	public void setSelectedSNPVariantIds(List<String> selectedSNPVariantIds) {
		this.selectedSNPVariantIds = selectedSNPVariantIds;
	}

	public List<String> getSelectedCNVIds() {
		return selectedCNVIds;
	}

	public void setSelectedCNVIds(List<String> selectedCNVIds) {
		this.selectedCNVIds = selectedCNVIds;
	}

	public List<String> getSelectedTranslocationIds() {
		return selectedTranslocationIds;
	}

	public void setSelectedTranslocationIds(List<String> selectedTranslocationIds) {
		this.selectedTranslocationIds = selectedTranslocationIds;
	}
	
	public String createObjectJSON() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}


	
}
