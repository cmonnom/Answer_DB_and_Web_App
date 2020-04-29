package utsw.bicf.answer.model.extmapping;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FPKMData extends WhiskerData {
	
	String oncotreeCode;
	List<WhiskerPerCaseData> fpkms = new ArrayList<WhiskerPerCaseData>();
	
	public FPKMData() {
		super();
	}

	public String getOncotreeCode() {
		return oncotreeCode;
	}

	public void setOncotreeCode(String oncotreeCode) {
		this.oncotreeCode = oncotreeCode;
	}

	public List<WhiskerPerCaseData> getFpkms() {
		return fpkms;
	}

	public void setFpkms(List<WhiskerPerCaseData> fpkms) {
		this.fpkms = fpkms;
	}

	@Override
	public String getLabel() {
		return oncotreeCode;
	}

	@Override
	public List<WhiskerPerCaseData> getPerCaseList() {
		return fpkms;
	}



	
		



}
