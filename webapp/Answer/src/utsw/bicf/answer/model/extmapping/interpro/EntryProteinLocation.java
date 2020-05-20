package utsw.bicf.answer.model.extmapping.interpro;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EntryProteinLocation {
	
	@JsonProperty("fragments")
	List<Fragment> fragments = new ArrayList<Fragment>();
	@JsonProperty("model_acc")
	String modelAcc;
	
	
	public EntryProteinLocation() {
		super();
	}


	public List<Fragment> getFragments() {
		return fragments;
	}


	public void setFragments(List<Fragment> fragments) {
		this.fragments = fragments;
	}


	public String getModelAcc() {
		return modelAcc;
	}


	public void setModelAcc(String modelAcc) {
		this.modelAcc = modelAcc;
	}






}
