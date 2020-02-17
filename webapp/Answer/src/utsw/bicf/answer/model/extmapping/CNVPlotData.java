package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CNVPlotData {
	
	Boolean isAllowed = true;
	Boolean success = true;
	
	String caseId;
	List<CNSData> cnsData;
	List<CNRData> cnrData;
	List<BAlleleFrequencyData> bAllData;
	
	
	
	public CNVPlotData() {
		
	}



	public Boolean getIsAllowed() {
		return isAllowed;
	}



	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}



	public Boolean getSuccess() {
		return success;
	}



	public void setSuccess(Boolean success) {
		this.success = success;
	}



	public String getCaseId() {
		return caseId;
	}



	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}



	public List<CNSData> getCnsData() {
		return cnsData;
	}



	public void setCnsData(List<CNSData> cnsData) {
		this.cnsData = cnsData;
	}



	public List<CNRData> getCnrData() {
		return cnrData;
	}



	public void setCnrData(List<CNRData> cnrData) {
		this.cnrData = cnrData;
	}



	public List<BAlleleFrequencyData> getbAllData() {
		return bAllData;
	}
	
	public List<BAlleleFrequencyData> getBAllData() {
		return bAllData;
	}



	public void setbAllData(List<BAlleleFrequencyData> bAllData) {
		this.bAllData = bAllData;
	}



	public void setBAllData(List<BAlleleFrequencyData> bAllData) {
		this.bAllData = bAllData;
		
	}





}
