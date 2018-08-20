package utsw.bicf.answer.model.extmapping;

import org.apache.commons.lang3.builder.EqualsBuilder;

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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		   if (obj == this) { return true; }
		   if (obj.getClass() != getClass()) {
		     return false;
		   }
		   MongoDBId rhs = (MongoDBId) obj;
		   return new EqualsBuilder()
//		                 .appendSuper(super.equals(obj))
		                 .append(oid, rhs.oid)
		                 .isEquals();
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
