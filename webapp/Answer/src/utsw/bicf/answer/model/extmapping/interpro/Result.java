package utsw.bicf.answer.model.extmapping.interpro;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Result {
	
	@JsonProperty("metadata")
	MetaData metadata;
	@JsonProperty("extra_fields")
	ExtraFields extra_fields;
	@JsonProperty("proteins")
	List<Protein> proteins;
	
	public Result() {
		super();
	}

	public MetaData getMetadata() {
		return metadata;
	}

	public void setMetadata(MetaData metadata) {
		this.metadata = metadata;
	}

	public ExtraFields getExtra_fields() {
		return extra_fields;
	}

	public void setExtra_fields(ExtraFields extra_fields) {
		this.extra_fields = extra_fields;
	}

	public List<Protein> getProteins() {
		return proteins;
	}

	public void setProteins(List<Protein> proteins) {
		this.proteins = proteins;
	}




}
