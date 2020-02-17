package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BAlleleFrequencyData {
	
	String chr;
	Long ao;
	Long ro;
	Long pos;
	Double log2;
	Double depth;
	
	public BAlleleFrequencyData() {
		super();
	}
	public BAlleleFrequencyData(String chr, Long pos, Long ao, Long ro, Double depth, Double log2) {
		super();
		this.chr = TypeUtils.formatChromosome(chr);
		this.pos = pos;
		this.ao = ao;
		this.ro = ro;
		this.log2 = log2;
		this.depth = depth;
	}
	
	public String getChr() {
		return chr;
	}
	public void setChr(String chr) {
		this.chr = chr;
	}
	public Long getAo() {
		return ao;
	}
	public void setAo(Long ao) {
		this.ao = ao;
	}
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

	
		



}
