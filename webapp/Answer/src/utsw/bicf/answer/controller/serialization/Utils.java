package utsw.bicf.answer.controller.serialization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	@SuppressWarnings("unchecked")
	public static VariantFilterList parseFilters(String data, boolean parseToSave) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		DataFilterList filterList = mapper.readValue(data, DataFilterList.class);
		List<VariantFilter> activeFilters = new ArrayList<VariantFilter>();
		for (DataTableFilter filter : filterList.getFilters()) {
			if (filter.isBoolean() != null && filter.isBoolean()) {
				if (filter.getValueTrue() != null || filter.getValueFalse() != null) {
					VariantFilter vf = new VariantFilter(filter.getFieldName(), filter.getUiFilterType());
					if (parseToSave) { //keep normal behavior to save in local database
						if (filter.getValueTrue() != null && filter.getValueTrue()) {
							vf.setValueTrue(true);
						}
						if (filter.getValueFalse() != null && filter.getValueFalse()) {
							vf.setValueFalse(true);
						}
					}
					else { //change some boolean flags to string to query MongoDB
						if (filter.getValueTrue() != null && filter.getValueTrue()) {
							if (filter.getFieldName().equals(Variant.FIELD_FILTERS) || filter.getFieldName().equals(Variant.FIELD_FTL_FILTERS)) {
								vf.getStringValues().add(new FilterStringValue(Variant.VALUE_PASS));
								vf.setField(Variant.FIELD_FILTERS); //rename field for FTL since we had to make the distinction on the front-end but the back end has the same field name for snp and ftl
//							vf.setValueTrue(true);
							}
							if (filter.getFieldName().equals(Variant.FIELD_ANNOTATIONS)
									|| filter.getFieldName().equals(Variant.FIELD_IN_COSMIC)
									|| filter.getFieldName().equals(Variant.FIELD_HAS_REPEATS)
									|| filter.getFieldName().equals(Variant.FIELD_IN_CLINVAR)
									|| filter.getFieldName().equals(Variant.FIELD_LIKELY_ARTIFACT)
									|| filter.getFieldName().equals(Variant.FIELD_GNOMAD_LCR)) {
								vf.setValueTrue(true);
							}
						}
						if (filter.getValueFalse() != null && filter.getValueFalse()) {
							if (filter.getFieldName().equals(Variant.FIELD_FILTERS)) {
								vf.getStringValues().add(new FilterStringValue(Variant.VALUE_FAIL));
//							vf.setValueFalse(true);
							}
							if (filter.getFieldName().equals(Variant.FIELD_ANNOTATIONS)
									|| filter.getFieldName().equals(Variant.FIELD_IN_COSMIC)
									|| filter.getFieldName().equals(Variant.FIELD_HAS_REPEATS)
									|| filter.getFieldName().equals(Variant.FIELD_IN_CLINVAR)
									|| filter.getFieldName().equals(Variant.FIELD_LIKELY_ARTIFACT)
									|| filter.getFieldName().equals(Variant.FIELD_GNOMAD_LCR)) {
								vf.setValueFalse(true);
							}
						}
					}
					if (!vf.getStringValues().isEmpty() || vf.getValueTrue() != null || vf.getValueFalse() != null) {
						activeFilters.add(vf);
					}
				}
			}
			else if (filter.isCheckBox() != null && filter.isCheckBox()) {
				List<VariantFilter> existingVFs = activeFilters.stream().filter(f -> f.getField().equals(filter.getFieldName())).collect(Collectors.toList());
				VariantFilter vf = null;
				if (existingVFs != null && !existingVFs.isEmpty()) {
					vf = existingVFs.get(0); //a VariantFilter already exists (eg. with "effects"). Use that one instead of a new one.
				}
				else {
					vf = new VariantFilter(filter.getFieldName(), filter.getUiFilterType());
				}
				List<SearchItem> checkBoxes = filter.getCheckBoxes();
				for (SearchItem cb : checkBoxes) {
					if (cb.getValue() != null && (boolean) cb.getValue()) {
//						String fieldName = cb.getName().replaceAll(" ", "_").toLowerCase();
						String fieldName = Variant.CHECKBOX_FILTERS_MAP.get(cb.getName());
						if (fieldName != null) {
							vf.getStringValues().add(new FilterStringValue(fieldName));
						}
						else {
							fieldName = Variant.CHECKBOX_FTL_FILTERS_MAP.get(cb.getName());
							if (fieldName != null) {
								vf.getStringValues().add(new FilterStringValue(fieldName));
							}
						}
					}
				}
				if (!vf.getStringValues().isEmpty() && !activeFilters.contains(vf) ) {
					activeFilters.add(vf);
				}
				//if checkbox is "effect", I need to create a "impact" filter
				//Commented out because it made the UI not clear to users.
				//Now it's just a effect search, no impact
//				if (filter.getFieldName().equals(Variant.FIELD_EFFECTS) && !filter.getCheckBoxes().isEmpty()) {
//					boolean someBoxChecked = !filter.getCheckBoxes().stream().filter(c -> (Boolean) c.getValue() == true).collect(Collectors.toList()).isEmpty();
//					if (someBoxChecked && impactFilter == null) {
//						impactFilter = new VariantFilter(Variant.FIELD_IMPACT);
//						if (!impactFilter.getStringValues().stream()
//								.map(fsv -> fsv.getFilterString())
//								.collect(Collectors.toList())
//								.contains(filter.getCategory())) {
//							impactFilter.getStringValues().add(new FilterStringValue(filter.getCategory()));
//						}
//					}
//				}
			}
			else if (filter.isDate() != null && filter.isDate()) {
				//TODO
			}
			else if ((filter.isNumber() != null && filter.isNumber()) || (filter.isReverseNumber() != null && filter.isReverseNumber())) {
				if (filter.getMinValue() != null || filter.getMaxValue() != null) {
					VariantFilter vf = new VariantFilter(filter.getFieldName(), filter.getUiFilterType());
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
				VariantFilter vf = new VariantFilter(filter.getFieldName(), filter.getUiFilterType());
				String stringValue = null;
				if (filter.getValue() instanceof String) {
					stringValue = (String) filter.getValue();
				}
				else { //it's a list
					stringValue = ((List<String>) filter.getValue()).stream().collect(Collectors.joining(","));
					for (String item : (List<String>) filter.getValue()) {
						vf.getStringValues().add(new FilterStringValue(item.trim()));
					}
				}
				if (stringValue != null && !stringValue.equals("")) {
					vf.setValue(stringValue);
					activeFilters.add(vf);
				}
			}
			else if (filter.isString() != null && filter.isString()) {
				VariantFilter vf = new VariantFilter(filter.getFieldName(), filter.getUiFilterType());
				if (filter.getValue() != null) {
					List<String> items = new ArrayList<String>();
					if (filter.getValue() instanceof String) {
						String[] itemArray = ((String) filter.getValue()).split(",");
						for (int i = 0; i < itemArray.length; i++) {
							items.add(itemArray[i]);
						}
					}
					else {
						items = (List<String>) filter.getValue();
					}
					for (String item : items) {
						vf.getStringValues().add(new FilterStringValue(item.trim()));
					}
					String value = items.stream().collect(Collectors.joining(","));
					if (value != null && !value.equals("")) {
						vf.setValue(value);
						activeFilters.add(vf);
					}
				}
			}
				
		}
		VariantFilterList list = new VariantFilterList();
		activeFilters.stream().forEach(filter -> filter.setFilterList(list));
		list.setFilters(activeFilters);
		
		return list;
	}

}
