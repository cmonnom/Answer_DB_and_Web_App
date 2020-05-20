package utsw.bicf.answer.model.extmapping.interpro;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtraFields {
	
	@JsonProperty("short_name")
	String shortName;
	
	public ExtraFields() {
		super();
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}





}
