package utsw.bicf.answer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.model.extmapping.Variant;

@Entity
@Table(name="variant_filter_list")
public class VariantFilterList {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="variant_filter_list_id")
	Integer variantFilterListId;
	
	@OneToMany(mappedBy="filterList", fetch=FetchType.EAGER, cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH, CascadeType.REMOVE})
	List<VariantFilter> filters;
	
	@JsonIgnore
	@ManyToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
	@JoinColumn(name="answer_user_id")
	User user;
	
	@Column(name="list_name")
	String listName;

	@Transient
	public static Map<String, String> filtersType = new HashMap<String, String>();
	//TODO for now, there are only 2 types but if we filter on translocations, we'll need to add all snp filters to  VariantFilterList.filtersType
	static {
		filtersType.put(Variant.FIELD_CNV_COPY_NUMBER, "cnv");
		filtersType.put(Variant.FIELD_CNV_GENE_NAME, "cnv");
		
	}

	public String createJSON() throws JsonProcessingException {
		for (VariantFilter filter : filters) {
			List<String> simpleStringValues = new ArrayList<String>();
			for (FilterStringValue v : filter.getStringValues()) {
				simpleStringValues.add(v.getFilterString());
			}
			filter.setSimpleStringValues(simpleStringValues);
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}


	public List<VariantFilter> getFilters() {
		return filters;
	}


	public void setFilters(List<VariantFilter> filters) {
		this.filters = filters;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}


	public String getListName() {
		return listName;
	}


	public void setListName(String listName) {
		this.listName = listName;
	}


	public Integer getVariantFilterListId() {
		return variantFilterListId;
	}


	public void setVariantFilterListId(Integer variantFilterListId) {
		this.variantFilterListId = variantFilterListId;
	}
}
