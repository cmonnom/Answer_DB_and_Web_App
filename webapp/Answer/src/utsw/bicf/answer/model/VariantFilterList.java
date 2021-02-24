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
		
		filtersType.put(Variant.FIELD_FTL_FILTERS, "ftl");
		filtersType.put(Variant.FIELD_FTL_NORMAL_DNA_READS, "ftl");
		
		filtersType.put(Variant.FIELD_CHROM, "snp");
		filtersType.put(Variant.FIELD_GENE_NAME, "snp");
		filtersType.put(Variant.FIELD_SOMATIC_STATUS, "snp");
		filtersType.put(Variant.FIELD_FTL_SOMATIC_STATUS, "ftl");
		filtersType.put(Variant.FIELD_FILTERS, "snp");
		filtersType.put(Variant.FIELD_TUMOR_ALT_FREQUENCY, "snp");
		filtersType.put(Variant.FIELD_TUMOR_TOTAL_DEPTH, "snp");
		filtersType.put(Variant.FIELD_NORMAL_ALT_FREQUENCY, "snp");
		filtersType.put(Variant.FIELD_NORMAL_TOTAL_DEPTH, "snp");
		filtersType.put(Variant.FIELD_RNA_ALT_FREQUENCY, "snp");
		filtersType.put(Variant.FIELD_RNA_TOTAL_DEPTH, "snp");
		filtersType.put(Variant.FIELD_EXAC_ALLELE_FREQUENCY, "snp");
		filtersType.put(Variant.FIELD_GNOMAD_ALLELE_FREQUENCY, "snp");
		filtersType.put(Variant.FIELD_GNOMAD_HOM, "snp");
		filtersType.put(Variant.FIELD_NUM_CASES_SEEN, "snp");
		filtersType.put(Variant.FIELD_IN_COSMIC, "snp");
		filtersType.put(Variant.FIELD_HAS_REPEATS, "snp");
		filtersType.put(Variant.FIELD_IN_CLINVAR, "snp");
		filtersType.put(Variant.FIELD_GNOMAD_LCR, "snp");
		filtersType.put(Variant.FIELD_LIKELY_ARTIFACT, "snp");
		filtersType.put(Variant.FIELD_EFFECTS, "snp");
		filtersType.put(Variant.FIELD_DISEASE_DATABASES, "snp");
		filtersType.put(Variant.FIELD_TROUBLED_REGIONS, "snp");
		
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
