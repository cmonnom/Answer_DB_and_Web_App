package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BAlleleFrequencyData {
	
	String chrom;
	//ao is not used and sometimes is entered in Mongo as a string (see ao: "25,3")
	//so for now, let's not parse it. The issue will need to be addressed
	//the day we need that data.
//	Long ao;
	Long ro;
	Long pos;
	@JsonProperty("maf")
	Double log2;
	@JsonProperty("dp")
	Double depth;
	
	public BAlleleFrequencyData() {
		super();
	}
//	public BAlleleFrequencyData(String chr, Long pos, Long ao, Long ro, Double depth, Double log2) {
//		super();
//		this.chrom = TypeUtils.formatChromosome(chr);
//		this.pos = pos;
//		this.ao = ao;
//		this.ro = ro;
//		this.log2 = log2;
//		this.depth = depth;
//	}
	
	public BAlleleFrequencyData(String chr, Long pos, Long ro, Double depth, Double log2) {
		super();
		this.chrom = TypeUtils.formatChromosome(chr);
		this.pos = pos;
		this.ro = ro;
		this.log2 = log2;
		this.depth = depth;
	}
	
//	public Long getAo() {
//		return ao;
//	}
//	public void setAo(Long ao) {
//		this.ao = ao;
//	}
	public Long getRo() {
		return ro;
	}
	public void setRo(Long ro) {
		this.ro = ro;
	}
	public Long getPos() {
		return pos;
	}
	public void setPos(Long pos) {
		this.pos = pos;
	}
	public Double getLog2() {
		return log2;
	}
	public void setLog2(Double log2) {
		this.log2 = log2;
	}
	public Double getDepth() {
		return depth;
	}
	public void setDepth(Double depth) {
		this.depth = depth;
	}
	public String getChrom() {
		return chrom;
	}
	public void setChrom(String chrom) {
		this.chrom = chrom;
	}

	
		



}
