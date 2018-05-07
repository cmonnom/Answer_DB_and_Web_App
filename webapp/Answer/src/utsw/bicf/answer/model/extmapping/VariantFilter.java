package utsw.bicf.answer.model.extmapping;

import java.util.ArrayList;
import java.util.List;

public class VariantFilter {
	
	String field;
	List<String> stringValues = new ArrayList<String>();
	Double minValue;
	Double maxValue;
	String value;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public VariantFilter(String field) {
		super();
		this.field = field;
	}

	public List<String> getStringValues() {
		return stringValues;
	}

	public void setStringValues(List<String> stringValues) {
		this.stringValues = stringValues;
	}

	public Double getMinValue() {
		return minValue;
	}

	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	public Double getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
