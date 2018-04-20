package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Variant {
	
	String chrom;
	String geneName;
	String effect;
	String notation;
	String tumorAltFrequency;
	Integer pos;
	Integer tumorAltDepth;
	
	
	public Variant() {
		
	}


	public String getChrom() {
		return chrom;
	}


	public void setChrom(String chrom) {
		this.chrom = chrom;
	}


	public String getGeneName() {
		return geneName;
	}


	public void setGeneName(String geneName) {
		this.geneName = geneName;
	}


	public String getEffect() {
		return effect;
	}


	public void setEffect(String effect) {
		this.effect = effect;
	}


	public String getNotation() {
		return notation;
	}


	public void setNotation(String notation) {
		this.notation = notation;
	}


	public String getTumorAltFrequency() {
		return tumorAltFrequency;
	}


	public void setTumorAltFrequency(String tumorAltFrequency) {
		this.tumorAltFrequency = tumorAltFrequency;
	}


	public Integer getPos() {
		return pos;
	}


	public void setPos(Integer pos) {
		this.pos = pos;
	}


	public Integer getTumorAltDepth() {
		return tumorAltDepth;
	}


	public void setTumorAltDepth(Integer tumorAltDepth) {
		this.tumorAltDepth = tumorAltDepth;
	}





	


}
