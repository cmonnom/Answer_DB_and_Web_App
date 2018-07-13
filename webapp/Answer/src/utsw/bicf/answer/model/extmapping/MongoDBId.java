package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MongoDBId {
	
	@JsonProperty("$oid")
	String oid;

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}


}
