package utsw.bicf.answer.model.extmapping;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VCFAnnotation {
	
	String allele;
	List<String> effects;
	String impact;
	String geneName;
	String geneId;
	String featureType;
	String featureId;
	String transcriptBiotype;
	String rank;
	String codingNotation;
	String proteinNotation;
	String proteinPosition;
	String cdnaPosition;
	String distanceToFeature;
	String appris;
	String tsl;
	
	public String getAllele() {
		return allele;
	}
	public List<String> getEffects() {
		return effects;
	}
	public String getImpact() {
		return impact;
	}
	public String getGeneName() {
		return geneName;
	}
	public String getGeneId() {
		return geneId;
	}
	public String getFeatureType() {
		return featureType;
	}
	public String getFeatureId() {
		return featureId;
	}
	public String getTranscriptBiotype() {
		return transcriptBiotype;
	}
	public String getRank() {
		return rank;
	}
	public String getCodingNotation() {
		return codingNotation;
	}
	public String getProteinNotation() {
		return proteinNotation;
	}
	public String getProteinPosition() {
		return proteinPosition;
	}
	public String getCdnaPosition() {
		return cdnaPosition;
	}
	public String getDistanceToFeature() {
		return distanceToFeature;
	}
	public String getAppris() {
		return appris;
	}
	public String getTsl() {
		return tsl;
	}
	
	

}
