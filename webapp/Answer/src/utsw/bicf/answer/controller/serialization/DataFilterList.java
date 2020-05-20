package utsw.bicf.answer.controller.serialization;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DataFilterList {

	public DataFilterList() {
		super();
	}


	List<DataTableFilter> filters;
	List<String> selectedSNPVariantIds;
	List<String> selectedCNVIds;
	List<String> selectedTranslocationIds;
	List<String> selectedVirusIds;

	public List<DataTableFilter> getFilters() {
		return filters;
	}


	public void setFilters(List<DataTableFilter> filters) {
		this.filters = filters;
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


	public List<String> getSelectedVirusIds() {
		return selectedVirusIds;
	}


	public void setSelectedVirusIds(List<String> selectedVirusIds) {
		this.selectedVirusIds = selectedVirusIds;
	}


}
