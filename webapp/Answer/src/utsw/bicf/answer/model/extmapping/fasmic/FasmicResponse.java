package utsw.bicf.answer.model.extmapping.fasmic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FasmicResponse {
	
	String gene;
	@JsonProperty("aa_change")
	String aaChange;
	@JsonProperty("final_call")
	String finalCall;
	
	public FasmicResponse() {
		super();
	}

	public String getGene() {
		return gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public String getAaChange() {
		return aaChange;
	}

	public void setAaChange(String aaChange) {
		this.aaChange = aaChange;
	}

	public String getFinalCall() {
		return finalCall;
	}

	public void setFinalCall(String finalCall) {
		this.finalCall = finalCall;
	}






}
