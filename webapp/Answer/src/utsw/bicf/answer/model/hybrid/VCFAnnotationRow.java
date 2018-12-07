package utsw.bicf.answer.model.hybrid;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import utsw.bicf.answer.controller.serialization.Button;
import utsw.bicf.answer.model.extmapping.VCFAnnotation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VCFAnnotationRow {
	
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
	
	List<Button> buttons = new ArrayList<Button>();
	
	public VCFAnnotationRow(VCFAnnotation annotation, boolean actionable) {
		if (actionable) {
			buttons.add(new Button("mdi-shuffle-variant", "setDefaultTranscript", "Set this transcript to be the canonical transcript", "info"));
		}
		allele = annotation.getAllele();
		effects = annotation.getEffects();
		impact = annotation.getImpact();
		geneName = annotation.getGeneName();
		geneId = annotation.getGeneId();
		featureType = annotation.getFeatureType();
		featureId = annotation.getFeatureId();
		transcriptBiotype = annotation.getTranscriptBiotype();
		rank = annotation.getRank();
		codingNotation = annotation.getCodingNotation();
		proteinNotation = annotation.getProteinNotation();
		proteinPosition = annotation.getProteinPosition();
		cdnaPosition = annotation.getCdnaPosition();
		distanceToFeature = annotation.getDistanceToFeature();
		appris = annotation.getAppris();
		tsl = annotation.getTsl();
	}
	
	
	
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
	public List<Button> getButtons() {
		return buttons;
	}
	
	

}
