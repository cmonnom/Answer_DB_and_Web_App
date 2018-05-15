package utsw.bicf.answer.model;

import java.util.List;

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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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


	public String createJSON() throws JsonProcessingException {
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
