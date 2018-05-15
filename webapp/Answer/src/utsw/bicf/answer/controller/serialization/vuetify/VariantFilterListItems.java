package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.SearchItemInteger;
import utsw.bicf.answer.controller.serialization.SearchItems;
import utsw.bicf.answer.model.VariantFilterList;

public class VariantFilterListItems extends SearchItems {
	
	List<VariantFilterList> filters;
	
	public VariantFilterListItems(List<VariantFilterList> variantFilterLists) {
		super();
		this.items = variantFilterLists.stream()
				.map(filterSet -> new SearchItemInteger(filterSet.getListName(), filterSet.getVariantFilterListId()))
				.collect(Collectors.toList());
		this.filters = variantFilterLists;
	}

	public List<VariantFilterList> getFilters() {
		return filters;
	}

	public void setFilters(List<VariantFilterList> filters) {
		this.filters = filters;
	}

}



