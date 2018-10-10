package utsw.bicf.answer.model.extmapping;

import utsw.bicf.answer.reporting.parse.AnnotationCategory;

public class IndicatedTherapy {

	String gene;
	String variant;
	String level;
	String indication;
	
	public IndicatedTherapy() {
	}
	
	public IndicatedTherapy(Annotation a, Variant v) {
		this.gene = a.getGeneId();
		this.variant = v.getNotation();
		if (a.getTier() != null) {
			switch(a.getTier()) {
			case "1A": this.level = "FDA-Approved"; break;
			case "1B": this.level = "Strong Evidence"; break;
			case "2C": this.level = "Weak Evidence"; break;
		}
		}
		this.indication = a.getText();
	}
	public IndicatedTherapy(AnnotationCategory cat, Variant v) {
		this.gene = v.getGeneName();
		this.variant = v.getNotation();
		this.indication = cat.getText();
	}

	public String getGene() {
		return gene;
	}
	public void setGene(String gene) {
		this.gene = gene;
	}
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
	
	
	
}
