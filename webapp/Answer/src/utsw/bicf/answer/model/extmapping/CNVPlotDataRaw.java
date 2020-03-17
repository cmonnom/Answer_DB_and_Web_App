package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CNVPlotDataRaw {
	
	String caseId;
	List<List<String>> cns;
	List<List<String>> cnr;
	List<BAlleleFrequencyData> ballelefreqs;
	
	
	
	public CNVPlotDataRaw() {
		
	}



	public String getCaseId() {
		return caseId;
	}



	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}



	public List<List<String>> getCns() {
		return cns;
	}



	public void setCns(List<List<String>> cns) {
		this.cns = cns;
	}



	public List<List<String>> getCnr() {
		return cnr;
	}



	public void setCnr(List<List<String>> cnr) {
		this.cnr = cnr;
	}



	public List<BAlleleFrequencyData> getBallelefreqs() {
		return ballelefreqs;
	}



	public void setBallelefreqs(List<BAlleleFrequencyData> ballelefreqs) {
		this.ballelefreqs = ballelefreqs;
	}











}
