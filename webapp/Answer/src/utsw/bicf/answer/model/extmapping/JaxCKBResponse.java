package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JaxCKBResponse {
	

	Integer id;
	String geneName;
	String geneDesc;
	
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
	

}
