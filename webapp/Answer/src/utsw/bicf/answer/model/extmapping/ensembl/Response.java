package utsw.bicf.answer.model.extmapping.ensembl;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Response {
	
	List<EnsemblItem> docs;
	
	public Response() {
		super();
	}

	public List<EnsemblItem> getDocs() {
		return docs;
	}

	public void setDocs(List<EnsemblItem> docs) {
		this.docs = docs;
	}


}
