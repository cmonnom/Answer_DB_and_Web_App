package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CNRData {
	
	String chr;
	Long start;
	Long end;
	String gene;
	Double log2;
	Double weight;
	
	public CNRData() {
		super();
	}
	public CNRData(String chr, Long start, Long end, String gene, Double log2, Double weight) {
		super();
		this.chr = TypeUtils.formatChromosome(chr);
		this.start = start;
		this.end = end;
		this.gene = gene;
		this.log2 = log2;
		this.weight = weight;
	}
	
	public String getChr() {
		return chr;
	}
	public void setChr(String chr) {
		this.chr = chr;
	}
	public Long getStart() {
		return start;
	}
	public void setStart(Long start) {
		this.start = start;
	}
	public Long getEnd() {
		return end;
	}
	public void setEnd(Long end) {
		this.end = end;
	}
	public String getGene() {
		return gene;
	}
	public void setGene(String gene) {
		this.gene = gene;
	}
	public Double getLog2() {
		return log2;
	}
	public void setLog2(Double log2) {
		this.log2 = log2;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}

	
		



}
