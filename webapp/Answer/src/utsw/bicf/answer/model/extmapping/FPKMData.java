package utsw.bicf.answer.model.extmapping;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FPKMData {
	
	String oncotreeCode;
	List<FPKMPerCaseData> fpkms = new ArrayList<FPKMPerCaseData>();
	
	public FPKMData() {
		super();
	}

	public String getOncotreeCode() {
		return oncotreeCode;
	}

	public void setOncotreeCode(String oncotreeCode) {
		this.oncotreeCode = oncotreeCode;
	}

	public List<FPKMPerCaseData> getFpkms() {
		return fpkms;
	}

	public void setFpkms(List<FPKMPerCaseData> fpkms) {
		this.fpkms = fpkms;
	}


	
		



}
