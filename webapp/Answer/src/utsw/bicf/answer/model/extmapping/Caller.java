package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Caller {
	
	String callerName;
	String alt;
	Integer tumorTotalDepth;
	Float tumorAlleleFrequency;
	String tumorAlleleFrequencyFormatted;
	Integer normalTotalDepth;
	Float normalAlleleFrequency;
	String normalAlleleFrequencyFormatted;
	
	
	public Caller() {
		
	}


	public String getCallerName() {
		return callerName;
	}


	public String getAlt() {
		return alt;
	}


	public Integer getTumorTotalDepth() {
		return tumorTotalDepth;
	}


	public Float getTumorAlleleFrequency() {
		if (tumorAlleleFrequencyFormatted == null) {
			tumorAlleleFrequencyFormatted = tumorAlleleFrequency != null ? String.format("%d", (int) (tumorAlleleFrequency * 100)) : null;
		}
		return tumorAlleleFrequency;
	}


	public Integer getNormalTotalDepth() {
		return normalTotalDepth;
	}


	public Float getNormalAlleleFrequency() {
		if (normalAlleleFrequencyFormatted == null) {
			normalAlleleFrequencyFormatted = normalAlleleFrequency != null ? String.format("%d", (int) (normalAlleleFrequency * 100)) : null;
		}
		return normalAlleleFrequency;
	}


	public String getTumorAlleleFrequencyFormatted() {
		return tumorAlleleFrequencyFormatted;
	}


	public String getNormalAlleleFrequencyFormatted() {
		return normalAlleleFrequencyFormatted;
	}


}
