package utsw.bicf.answer.model.extmapping.ensembl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnsemblItem {
	
	String symbol;
	@JsonProperty("entrez_id")
	String entrezId;
	@JsonProperty("ensembl_gene_id")
	String ensemblGeneId;
	@JsonProperty("uniprot_ids")
	List<String> uniProtIds;
	@JsonProperty("hgnc_id")
	String hgnc;
	
	
	
	public EnsemblItem() {
		super();
	}



	public String getSymbol() {
		return symbol;
	}



	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}



	public String getEntrezId() {
		return entrezId;
	}



	public void setEntrezId(String entrezId) {
		this.entrezId = entrezId;
	}



	public String getEnsemblGeneId() {
		return ensemblGeneId;
	}



	public void setEnsemblGeneId(String ensemblGeneId) {
		this.ensemblGeneId = ensemblGeneId;
	}



	public List<String> getUniProtIds() {
		return uniProtIds;
	}



	public void setUniProtIds(List<String> uniProtIds) {
		this.uniProtIds = uniProtIds;
	}



	public String getHgnc() {
		return hgnc;
	}



	public void setHgnc(String hgnc) {
		this.hgnc = hgnc;
	}

	

}
