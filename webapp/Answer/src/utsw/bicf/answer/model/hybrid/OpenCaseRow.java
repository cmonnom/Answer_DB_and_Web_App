package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;

import utsw.bicf.answer.controller.serialization.Button;
import utsw.bicf.answer.model.extmapping.Variant;

public class OpenCaseRow {
	
	String chromPos;
	String geneVariant;
	String effect;
	String notation;
	String tumorAltFrequency;
	Integer tumorAltDepth;
	
	List<Button> buttons = new ArrayList<Button>();
	
	public OpenCaseRow(Variant variant) {
		this.chromPos = variant.getChrom().toUpperCase() + ":" + variant.getPos();
		this.geneVariant = variant.getGeneName() + " " + variant.getNotation();
		this.effect = variant.getEffect();
		this.notation = variant.getNotation();
		this.tumorAltFrequency = variant.getTumorAltFrequency() != null ? String.format("%.2f", Float.parseFloat(variant.getTumorAltFrequency())) : null;
		this.tumorAltDepth = variant.getTumorAltDepth();
	}


	public List<Button> getButtons() {
		return buttons;
	}




	public String getEffect() {
		return effect;
	}


	public String getNotation() {
		return notation;
	}


	public String getTumorAltFrequency() {
		return tumorAltFrequency;
	}


	public String getChromPos() {
		return chromPos;
	}

	public Integer getTumorAltDepth() {
		return tumorAltDepth;
	}


	public String getGeneVariant() {
		return geneVariant;
	}



}
