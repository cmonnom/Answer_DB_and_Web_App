package utsw.bicf.answer.controller.serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.model.FilterStringValue;
import utsw.bicf.answer.model.VariantFilter;
import utsw.bicf.answer.model.VariantFilterList;
import utsw.bicf.answer.model.extmapping.Variant;

public class Utils {
	
	/**
	 * Create a list of active filters (filters with values)
	 * to be passed to the AnswerDB API.
	 * It's up to the API to figure out which values/fields are populated
	 * @param filters
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static VariantFilterList parseFilters(String filters) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		DataFilterList filterList = mapper.readValue(filters, DataFilterList.class);
//		}
		List<VariantFilter> activeFilters = new ArrayList<VariantFilter>();
		for (DataTableFilter filter : filterList.getFilters()) {
			if (filter.isBoolean() != null && filter.isBoolean()) {
				if (filter.getValueTrue() != null || filter.getValueFalse() != null) {
					VariantFilter vf = new VariantFilter(filter.getFieldName());
					if (filter.getValueTrue() != null && filter.getValueTrue()) {
						if (filter.getFieldName().equals(Variant.FIELD_FILTERS)) {
							vf.getStringValues().add(new FilterStringValue(Variant.VALUE_PASS));
							vf.setValueTrue(true);
						}
						if (filter.getFieldName().equals(Variant.FIELD_ANNOTATIONS)) {
							vf.setValueTrue(true);
						}
					}
					if (filter.getValueFalse() != null && filter.getValueFalse()) {
						if (filter.getFieldName().equals(Variant.FIELD_FILTERS)) {
							vf.getStringValues().add(new FilterStringValue(Variant.VALUE_FAIL));
							vf.setValueFalse(true);
						}
						if (filter.getFieldName().equals(Variant.FIELD_ANNOTATIONS)) {
							vf.setValueFalse(true);
						}
					}
					if (!vf.getStringValues().isEmpty() || vf.getValueTrue() != null || vf.getValueFalse() != null) {
						activeFilters.add(vf);
					}
				}
			}
			else if (filter.isCheckBox() != null && filter.isCheckBox()) {
				VariantFilter vf = new VariantFilter(filter.getFieldName());
				List<SearchItem> checkBoxes = filter.getCheckBoxes();
				for (SearchItem cb : checkBoxes) {
					if (cb.getValue() != null && (boolean) cb.getValue()) {
						vf.getStringValues().add(new FilterStringValue(cb.getName().replaceAll(" ", "_").toLowerCase()));
					}
				}
				if (!vf.getStringValues().isEmpty() ) {
					activeFilters.add(vf);
				}
			}
			else if (filter.isDate() != null && filter.isDate()) {
				//TODO
			}
			else if (filter.isNumber() != null && filter.isNumber()) {
				if (filter.getMinValue() != null || filter.getMaxValue() != null) {
					VariantFilter vf = new VariantFilter(filter.getFieldName());
					if (filter.getMinValue() != null) {
						if (vf.getField().contains("Frequency")) { //frequencies are converted to pct. Need to revert it to ratio
							vf.setMinValue(filter.getMinValue() / 100);
						}
						else {
							vf.setMinValue(filter.getMinValue());
						}
					}
					if (filter.getMaxValue() != null) {
						if (vf.getField().contains("Frequency")) { //frequencies are converted to pct. Need to revert it to ratio
							vf.setMaxValue(filter.getMaxValue() / 100);
						}
						else {
							vf.setMaxValue(filter.getMaxValue());
						}
					}
					activeFilters.add(vf);
				}
			}
			else if (filter.isSelect() != null && filter.isSelect() && filter.getValue() != null) {
				VariantFilter vf = new VariantFilter(filter.getFieldName());
				vf.setValue(filter.getValue());
				activeFilters.add(vf);
			}
			else if (filter.isString() != null && filter.isString()) {
				VariantFilter vf = new VariantFilter(filter.getFieldName());
				vf.setValue(filter.getValue());
				activeFilters.add(vf);
			}
				
		}
		VariantFilterList list = new VariantFilterList();
		activeFilters.stream().forEach(filter -> filter.setFilterList(list));
		list.setFilters(activeFilters);
		
		return list;
	}

}
