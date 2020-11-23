package utsw.bicf.answer.model.extmapping.ensembl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnsemblResponse {
	
	Response response;
	String entrezId;
	String ensemblId;
	String uniProtId;
	String hgncId;
	
	public EnsemblResponse() {
		super();
		init();
	}

	public Response getResponse() {
		return response;
	}

	public void setResponse(Response response) {
		this.response = response;
	}
	
	public void init() {
		if (response != null && response.getDocs() != null && !response.getDocs().isEmpty()) {
			EnsemblItem item = response.getDocs().get(0);
			entrezId = item.getEntrezId();
			ensemblId = item.getEnsemblGeneId();
			if (item.getUniProtIds() != null && !item.getUniProtIds().isEmpty()) {
				uniProtId = item.getUniProtIds().get(0);
			}
			hgncId = item.getHgnc();
		}
	}

	public String getEntrezId() {
		return entrezId;
	}

	public void setEntrezId(String entrezId) {
		this.entrezId = entrezId;
	}

	public String getEnsemblId() {
		return ensemblId;
	}

	public void setEnsemblId(String ensemblId) {
		this.ensemblId = ensemblId;
	}

	public String getUniProtId() {
		return uniProtId;
	}

	public void setUniProtId(String uniProtId) {
		this.uniProtId = uniProtId;
	}

	public String getHgncId() {
		return hgncId;
	}

	public void setHgncId(String hgncId) {
		this.hgncId = hgncId;
	}


}
