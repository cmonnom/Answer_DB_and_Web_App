package utsw.bicf.answer.model.extmapping.oncotree;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OncotreeTumorType {
	
	@JsonProperty("code")
	String code;
	
	@JsonProperty("name")
	String name;
	
	@JsonProperty("mainType")
	String mainType;
	
	@JsonProperty("externalReferences")
	ExternalReferences externalReferences;
	
	@JsonProperty("tissue")
	String tissue;
	
	@JsonProperty("parent")
	String parent;
	
	@JsonProperty("children")
	Map<String, OncotreeTumorType> children;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMainType() {
		return mainType;
	}

	public void setMainType(String mainType) {
		this.mainType = mainType;
	}

	public ExternalReferences getExternalReferences() {
		return externalReferences;
	}

	public void setExternalReferences(ExternalReferences externalReferences) {
		this.externalReferences = externalReferences;
	}

	public String getTissue() {
		return tissue;
	}

	public void setTissue(String tissue) {
		this.tissue = tissue;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public Map<String, OncotreeTumorType> getChildren() {
		return children;
	}

	public void setChildren(Map<String, OncotreeTumorType> children) {
		this.children = children;
	}


}
