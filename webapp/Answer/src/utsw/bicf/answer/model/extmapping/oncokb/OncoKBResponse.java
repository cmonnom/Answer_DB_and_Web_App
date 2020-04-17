package utsw.bicf.answer.model.extmapping.oncokb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OncoKBResponse {
	
	Integer entrezGeneId;
	String hugoSymbol;
	String desc;
	
	public OncoKBResponse() {
		super();
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
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



}
