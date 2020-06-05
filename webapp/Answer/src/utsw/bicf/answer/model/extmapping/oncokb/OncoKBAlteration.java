package utsw.bicf.answer.model.extmapping.oncokb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OncoKBAlteration {
	
	OncoKBGene gene;
	OncoKBConsequence consequence;
	String alteration;
	String name;
	
	public OncoKBAlteration() {
		super();
	}

	public OncoKBGene getGene() {
		return gene;
	}

	public void setGene(OncoKBGene gene) {
		this.gene = gene;
	}

	public OncoKBConsequence getConsequence() {
		return consequence;
	}

	public void setConsequence(OncoKBConsequence consequence) {
		this.consequence = consequence;
	}

	public String getAlteration() {
		return alteration;
	}

	public void setAlteration(String alteration) {
		this.alteration = alteration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}








}
