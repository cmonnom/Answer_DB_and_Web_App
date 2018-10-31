package utsw.bicf.answer.model.extmapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import utsw.bicf.answer.reporting.parse.AnnotationCategory;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IndicatedTherapy {

	String variant;
	String level;
	String indication;
	String tier;
	
	public IndicatedTherapy() {
	}
	
	public IndicatedTherapy(Annotation a, Variant v) {
		this.variant = a.getGeneId() + " " + v.getNotation();
		if (a.getTier() != null) {
			switch(a.getTier()) {
			case "1A": this.level = "FDA-Approved"; break;
			case "1B": this.level = "Strong Evidence"; break;
			case "2C": this.level = "Weak Evidence"; break;
			}
			this.tier = a.getTier();
		}
		this.indication = a.getText();
	}
	public IndicatedTherapy(Annotation a, Translocation v) {
		this.variant = a.getLeftGene() + "-" + a.getRightGene();
		if (a.getTier() != null) {
			switch(a.getTier()) {
			case "1A": this.level = "FDA-Approved"; break;
			case "1B": this.level = "Strong Evidence"; break;
			case "2C": this.level = "Weak Evidence"; break;
			}
			this.tier = a.getTier();
		}
		this.indication = a.getText();
	}
//	public IndicatedTherapy(AnnotationCategory cat, Variant v) {
//		this.variant = v.getGeneName() + " " + v.getNotation();
//		this.indication = cat.getText();
//	}
//	public IndicatedTherapy(AnnotationCategory cat, Translocation v) {
//		this.variant = v.getLeftGene() + "-" + v.getRightGene();
//		this.indication = cat.getText();
//	}


	public String getVariant() {
		return variant;
	}
	public void setVariant(String variant) {
		this.variant = variant;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getIndication() {
		return indication;
	}
	public void setIndication(String indication) {
		this.indication = indication;
	}

	public String getTier() {
		return tier;
	}

	public void setTier(String tier) {
		this.tier = tier;
	}
	
	
	
}
