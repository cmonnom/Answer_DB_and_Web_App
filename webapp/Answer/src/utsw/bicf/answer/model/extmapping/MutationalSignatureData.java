package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MutationalSignatureData {
	
	String caseId;
	Integer signature;
	String proposedEtiology;
	Float value;
	
	public MutationalSignatureData() {
		super();
	}

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public Integer getSignature() {
		return signature;
	}

	public void setSignature(Integer signature) {
		this.signature = signature;
	}

	public String getProposedEtiology() {
		return proposedEtiology;
	}

	public void setProposedEtiology(String proposedEtiology) {
		this.proposedEtiology = proposedEtiology;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}




	
		



}
