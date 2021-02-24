package utsw.bicf.answer.controller.serialization;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataTableFilter {

	
	
	public DataTableFilter() {
		super();
		// TODO Auto-generated constructor stub
	}

	String headerText;
	String headerTextTrue; //for booleans
	String headerTextFalse; //for booleans
	String fieldName;
	Object value;
	Boolean valueTrue; //for booleans
	Boolean valueFalse; //for booleans
	Double minValue;
	Double maxValue;
	String minDateValue;
	String maxDateValue;
	@JsonProperty("isString")
	Boolean isString;
	@JsonProperty("isNumber")
	Boolean isNumber;
	@JsonProperty("isDate")
	Boolean isDate;
	@JsonProperty("isSelect")
	Boolean isSelect;
	@JsonProperty("isBoolean")
	Boolean isBoolean;
	@JsonProperty("isCheckBox")
	Boolean isCheckBox;
	List<SearchItem> selectItems;
	List<SearchItem> checkBoxes = new ArrayList<SearchItem>();
	String tooltip;
	String category;
	
	String type;
	String uiFilterType;
	@JsonProperty("isReverseNumber")
	Boolean isReverseNumber; //flag to handles cases where we want values less than min and more than max
	
//	Some fields like gene names need to be verified against an existing list
	//button will add a button next to the filter to handle such action
	Button button;
	
	//Some checkbox filters need to be grouped in flags instead of with the other checkboxes
	String group;
	
	//Booleans need to be handled a bit differently because they can have 4 states: include/not include if true, include/not include if false, 
	//For instance Pass QC:
	//We need a flag for  "include Pass QC" or "do not include Pass QC"
	//We need a flag for  "include Fail QC" or "do not include Fail QC"
	//Use the headerTextTrue/False for the label of each state (Pass/Fail)
	//and valueTrue/valueFalse for storing the values
	
	public DataTableFilter(String headerText, String fieldName) {
		this.headerText = headerText;
		this.fieldName = fieldName;
	}
	
	public DataTableFilter(String headerTextTrue, String headerTextFalse, String fieldName) {
		this.headerTextTrue = headerTextTrue;
		this.headerTextFalse = headerTextFalse;
		this.fieldName = fieldName;
	}

	public Object getValue() {
		return value;
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

	public void setValue(Object value) {
		this.value = value;
	}

	public Boolean isString() {
		return isString;
	}

	public Boolean isNumber() {
		return isNumber;
	}
	
	public Boolean isReverseNumber() {
		return isReverseNumber;
	}

	public Boolean isDate() {
		return isDate;
	}

	public String getMinDateValue() {
		return minDateValue;
	}

	public void setMinDateValue(String minDateValue) {
		this.minDateValue = minDateValue;
	}

	public String getMaxDateValue() {
		return maxDateValue;
	}

	public void setMaxDateValue(String maxDateValue) {
		this.maxDateValue = maxDateValue;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setString(Boolean isString) {
		this.isString = isString;
	}

	public void setNumber(Boolean isNumber) {
		this.isNumber = isNumber;
	}
	
	public void setReverseNumber(Boolean isReverseNumber) {
		this.isReverseNumber = isReverseNumber;
	}

	public void setDate(Boolean isDate) {
		this.isDate = isDate;
	}

	public Boolean isSelect() {
		return isSelect;
	}

	public void setSelect(Boolean isSelect) {
		this.isSelect = isSelect;
	}

	public Boolean isBoolean() {
		return isBoolean;
	}

	public void setBoolean(Boolean isBoolean) {
		this.isBoolean = isBoolean;
	}

	public List<SearchItem> getSelectItems() {
		return selectItems;
	}

	public void setSelectItems(List<SearchItem> selectItems) {
		this.selectItems = selectItems;
	}

	public String getHeaderText() {
		return headerText;
	}

	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}

	public String getHeaderTextTrue() {
		return headerTextTrue;
	}

	public void setHeaderTextTrue(String headerTextTrue) {
		this.headerTextTrue = headerTextTrue;
	}

	public String getHeaderTextFalse() {
		return headerTextFalse;
	}

	public void setHeaderTextFalse(String headerTextFalse) {
		this.headerTextFalse = headerTextFalse;
	}

	public Boolean getValueTrue() {
		return valueTrue;
	}

	public void setValueTrue(Boolean valueTrue) {
		this.valueTrue = valueTrue;
	}

	public Boolean getValueFalse() {
		return valueFalse;
	}

	public void setValueFalse(Boolean valueFalse) {
		this.valueFalse = valueFalse;
	}

	public Boolean isCheckBox() {
		return isCheckBox;
	}

	public void setCheckBox(Boolean isCheckBox) {
		this.isCheckBox = isCheckBox;
	}

	public List<SearchItem> getCheckBoxes() {
		return checkBoxes;
	}

	public void setCheckBoxes(List<SearchItem> checkBoxes) {
		this.checkBoxes = checkBoxes;
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Button getButton() {
		return button;
	}

	public void setButton(Button button) {
		this.button = button;
	}

	public String getUiFilterType() {
		return uiFilterType;
	}

	public void setUiFilterType(String uiFilterType) {
		this.uiFilterType = uiFilterType;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	
}
