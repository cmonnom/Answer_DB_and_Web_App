package utsw.bicf.answer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="filter_string_value")
public class FilterStringValue {
	
	public FilterStringValue() {
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="filter_string_value_id")
	Integer filterStringValueId;
	
	@Column(name="filter_string")
	String filterString;

	public FilterStringValue(String filterString) {
		super();
		this.filterString = filterString;
	}

	public Integer getFilterStringValueId() {
		return filterStringValueId;
	}

	public void setFilterStringValueId(Integer filterStringValueId) {
		this.filterStringValueId = filterStringValueId;
	}

	public String getFilterString() {
		return filterString;
	}

	public void setFilterString(String filterString) {
		this.filterString = filterString;
	}



}
