package utsw.bicf.answer.controller.serialization;

import java.util.List;

public class DataFilterList {

	List<DataTableFilter> filters;
	List<String> selectedVariantIds;

	public List<DataTableFilter> getFilters() {
		return filters;
	}


	public void setFilters(List<DataTableFilter> filters) {
		this.filters = filters;
	}


	public List<String> getSelectedVariantIds() {
		return selectedVariantIds;
	}


	public void setSelectedVariantIds(List<String> selectedVariantIds) {
		this.selectedVariantIds = selectedVariantIds;
	}
}
