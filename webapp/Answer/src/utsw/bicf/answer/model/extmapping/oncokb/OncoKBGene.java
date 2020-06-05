package utsw.bicf.answer.model.extmapping.oncokb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OncoKBGene {
	
	Integer entrezGeneId;
	String hugoSymbol;
	String name;
	Boolean oncogene;
	Boolean tsg;
	
	public OncoKBGene() {
		super();
	}

	public Integer getEntrezGeneId() {
		return entrezGeneId;
	}

	public void setEntrezGeneId(Integer entrezGeneId) {
		this.entrezGeneId = entrezGeneId;
	}

	public String getHugoSymbol() {
		return hugoSymbol;
	}

	public void setHugoSymbol(String hugoSymbol) {
		this.hugoSymbol = hugoSymbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getOncogene() {
		return oncogene;
	}

	public void setOncogene(Boolean oncogene) {
		this.oncogene = oncogene;
	}

	public Boolean getTsg() {
		return tsg;
	}

	public void setTsg(Boolean tsg) {
		this.tsg = tsg;
	}




}
