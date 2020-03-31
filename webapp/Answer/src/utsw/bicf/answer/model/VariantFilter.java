package utsw.bicf.answer.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import utsw.bicf.answer.model.extmapping.Variant;

@Entity
@Table(name="variant_filter")
public class VariantFilter {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="variant_filter_id")
	Integer variantFilterId;
	
	@Column(name="field")
	String field;
	
	@ManyToMany(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE}, fetch = FetchType.EAGER)
	@JoinTable(name="variant_filter_filter_string_value",
	joinColumns=@JoinColumn(name="filter_string_value_id"),
	inverseJoinColumns=@JoinColumn(name="variant_filter_id"))
	List<FilterStringValue> stringValues;
	@Transient
	List<String> simpleStringValues;
	

	@Column(name="min_value")
	Double minValue;
	@Column(name="max_value")
	Double maxValue;
	@Column(name="value")
	String value;
	@Column(name="value_true")
	Boolean valueTrue;
	@Column(name="value_false")
	Boolean valueFalse;
	
	@Column(name="variant_type")
	String type;

	@Column(name="ui_filter_type")
	String uiFilterType;
	
	@JsonIgnore
	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE})
	@JoinColumn(name="variant_filter_list_id")
	VariantFilterList filterList;

	public VariantFilter() {
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public VariantFilter(String field, String uifilterType) {
		super();
		this.field = field;
		this.stringValues = new ArrayList<FilterStringValue>();
		if (VariantFilterList.filtersType.containsKey(field)) {
			this.type = VariantFilterList.filtersType.get(field); //TODO only cnv filters for now
			if (this.type.equals("ftl") && this.field.equals(Variant.FIELD_FTL_FILTERS)) {
				this.field = Variant.FIELD_FILTERS; //override "ftlFilters" because Mongo stores it as "filters"
			}
			if (this.type.equals("ftl") && this.field.equals(Variant.FIELD_FTL_SOMATIC_STATUS)) {
				this.field = Variant.FIELD_SOMATIC_STATUS; //override "ftlSomaticStatus" because Mongo stores it as "somaticStatus"
			}
		}
		else { //TODO for now, there are only 2 types but if we filter on translocations, we'll need to add all snp filters to  VariantFilterList.filtersType
			this.type = "snp";
		}
		this.uiFilterType = uifilterType;
	}

	public List<FilterStringValue> getStringValues() {
		return stringValues;
	}

	public void setStringValues(List<FilterStringValue> stringValues) {
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

	public Integer getVariantFilterId() {
		return variantFilterId;
	}

	public void setVariantFilterId(Integer variantFilterId) {
		this.variantFilterId = variantFilterId;
	}

	public VariantFilterList getFilterList() {
		return filterList;
	}

	public void setFilterList(VariantFilterList filterList) {
		this.filterList = filterList;
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

	public List<String> getSimpleStringValues() {
		return simpleStringValues;
	}

	public void setSimpleStringValues(List<String> simpleStringValues) {
		this.simpleStringValues = simpleStringValues;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUiFilterType() {
		return uiFilterType;
	}

	public void setUiFilterType(String uiFilterType) {
		this.uiFilterType = uiFilterType;
	}



}
