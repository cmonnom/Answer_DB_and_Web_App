package utsw.bicf.answer.model.extmapping.interpro;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MetaData {
	
	@JsonProperty("accession")
	String accession;
	@JsonProperty("name")
	String name;
	
	public MetaData() {
		super();
	}

	public String getAccession() {
		return accession;
	}

	public void setAccession(String accession) {
		this.accession = accession;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}






}
