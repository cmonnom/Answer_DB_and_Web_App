package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MangoDBId {
	
	@JsonProperty("$oid")
	String oid;

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}


}
