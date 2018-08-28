package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CNSData {
	
	String chr;
	Long start;
	Long end;
	Double log2;
	Integer cn;
	
	
	
	public CNSData() {
		super();
	}
	public CNSData(String chr, Long start, Long end, Double log2, Integer cn) {
		super();
		this.chr = TypeUtils.formatChromosome(chr);
		this.start = start;
		this.end = end;
		this.log2 = log2;
		this.cn = cn;
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
	public Double getLog2() {
		return log2;
	}
	public void setLog2(Double log2) {
		this.log2 = log2;
	}
	public Integer getCn() {
		return cn;
	}
	public void setCn(Integer cn) {
		this.cn = cn;
	}
	
		



}
