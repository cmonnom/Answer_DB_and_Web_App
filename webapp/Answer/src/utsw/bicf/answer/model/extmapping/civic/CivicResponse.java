package utsw.bicf.answer.model.extmapping.civic;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CivicResponse {
	
	Integer id;
	String name;
	@JsonProperty("entrez_id")
	String entrezId;
	String description;
	List<CivicVariant> variants;
	
	public CivicResponse() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEntrezId() {
		return entrezId;
	}

	public void setEntrezId(String entrezId) {
		this.entrezId = entrezId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<CivicVariant> getVariants() {
		return variants;
	}

	public void setVariants(List<CivicVariant> variants) {
		this.variants = variants;
	}


}
