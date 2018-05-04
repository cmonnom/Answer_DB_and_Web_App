package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Variant {
	
	String chrom;
	String geneName;
	List<String> effects;
	String notation;
	String tumorAltFrequency;
	Integer tumorAltDepth;
	Integer tumorTotalDepth;
	String normalAltFrequency;
	Integer normalAltDepth;
	Integer normalTotalDepth;
	String rnaAltFrequency;
	Integer rnaAltDepth;
	Integer rnaTotalDepth;
	Integer pos;
	List<String> callSet;
	String type;
	List<Integer> cosmicPatients;
	List<String> id; //list of external database ids (dbsnp, cosmic, etc)
	String alt;
	List<String> filters; //list of filers
	
	
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


	public List<String> getEffects() {
		return effects;
	}


	public void setEffect(List<String> effects) {
		this.effects = effects;
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


	public List<String> getCallSet() {
		return callSet;
	}


	public void setCallSet(List<String> callSet) {
		this.callSet = callSet;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public List<Integer> getCosmicPatients() {
		return cosmicPatients;
	}


	public void setCosmicPatients(List<Integer> cosmicPatients) {
		this.cosmicPatients = cosmicPatients;
	}


	public List<String> getId() {
		return id;
	}


	public void setId(List<String> id) {
		this.id = id;
	}


	public String getAlt() {
		return alt;
	}


	public void setAlt(String alt) {
		this.alt = alt;
	}


	public List<String> getFilters() {
		return filters;
	}


	public void setFilter(List<String> filters) {
		this.filters = filters;
	}


	public Integer getTumorTotalDepth() {
		return tumorTotalDepth;
	}


	public String getNormalAltFrequency() {
		return normalAltFrequency;
	}


	public Integer getNormalAltDepth() {
		return normalAltDepth;
	}


	public Integer getNormalTotalDepth() {
		return normalTotalDepth;
	}


	public String getRnaAltFrequency() {
		return rnaAltFrequency;
	}


	public Integer getRnaAltDepth() {
		return rnaAltDepth;
	}


	public Integer getRnaTotalDepth() {
		return rnaTotalDepth;
	}





	


}
