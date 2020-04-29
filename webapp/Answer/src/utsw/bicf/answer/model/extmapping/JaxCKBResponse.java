package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JaxCKBResponse {
	

	Integer id;
	String geneName;
	String geneDesc;
	String text;
	@JsonProperty("gene_description")
	String geneDescription;
	@JsonProperty("gene_variant_description")
	String variantDescription;
	
	public JaxCKBResponse() {
		super();
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getGeneName() {
		return geneName;
	}
	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}
	public String getGeneDesc() {
		return geneDesc;
	}
	public void setGeneDesc(String geneDesc) {
		this.geneDesc = geneDesc;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getGeneDescription() {
		return geneDescription;
	}

	public void setGeneDescription(String geneDescription) {
		this.geneDescription = geneDescription;
	}

	public String getVariantDescription() {
		return variantDescription;
	}

	public void setVariantDescription(String variantDescription) {
		this.variantDescription = variantDescription;
	}
	

}
