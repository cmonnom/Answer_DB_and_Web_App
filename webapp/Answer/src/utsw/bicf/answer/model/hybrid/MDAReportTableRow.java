package utsw.bicf.answer.model.hybrid;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;

import utsw.bicf.answer.controller.serialization.PassableValue;
import utsw.bicf.answer.reporting.finalreport.GeneVariantDetails;
import utsw.bicf.answer.reporting.finalreport.TreatmentOption;
import utsw.bicf.answer.reporting.parse.AnnotationCategory;

public class MDAReportTableRow {
	
	String uniqueIdField;
	String gene;
	String sequenceChange;
	String aberration;
	String fdaApprovedWithIndication;
	String fdaApprovedOutsideOfIndication;
	String clinicalTrials;
	String alleleFrequency;
	
	String geneDetails;
	PassableValue geneDetailsValue;
	@JsonIgnore
	GeneVariantDetails detailsForGene;
	Map<String, String> tooltips = new HashMap<String, String>();
	String mdaAnnotations;
	Boolean isSelected;

	public MDAReportTableRow(TreatmentOption option, GeneVariantDetails detailsForGene, Boolean isSelected) {
		this.gene = option.getGene();
		this.sequenceChange = option.getSequenceChange();
		this.aberration = option.getAberration();
		this.fdaApprovedWithIndication = option.getFdaApprovedWithIndication();
		this.fdaApprovedOutsideOfIndication = option.getFdaApprovedOutsideOfIndication();
		this.clinicalTrials = option.getClinicalTrials();
		this.detailsForGene = detailsForGene;
		this.buildGeneDetails();
		this.alleleFrequency = detailsForGene.getTaf();
		this.isSelected = isSelected;
		
		createOtherAnnotations();
	}

	private void buildGeneDetails() {
		StringBuilder sb = new StringBuilder();
		sb.append(gene).append(" ")
		.append(sequenceChange != null ? sequenceChange : "").append(" ")
		.append(aberration != null ? aberration : "");
		this.geneDetails = sb.toString();
		this.geneDetailsValue = new PassableValue("geneDetailsValue", geneDetails, isActionnable());
		if (detailsForGene != null && detailsForGene.getAnnotationCategories() != null) {
			StringBuilder ann = new StringBuilder();
			for (AnnotationCategory c : detailsForGene.getAnnotationCategories()) {
				if (c != null) {
					ann.append(c.toHTMLString());
				}
			}
			mdaAnnotations = ann.toString();
		}
		StringBuilder actionableInfo = new StringBuilder();
		if (detailsForGene != null && detailsForGene.getFdaWithinIndication() != null) {
			actionableInfo.append(detailsForGene.getFdaWithinIndication().stream().collect(Collectors.joining("<br>"))).append("<br>");
		}
		if (detailsForGene != null && detailsForGene.getFdaOutsideIndication() != null) {
			actionableInfo.append(detailsForGene.getFdaOutsideIndication().stream().collect(Collectors.joining("<br>"))).append("<br>");
		}
		if (detailsForGene != null && detailsForGene.getResistanceAndInteractions() != null) {
			actionableInfo.append(detailsForGene.getResistanceAndInteractions().stream().collect(Collectors.joining("<br>"))).append("<br>");
		}
		tooltips.put("geneDetailsValue", actionableInfo.toString());
		this.uniqueIdField = gene + aberration;
	}
	
	private boolean isActionnable() {
		boolean isActionable = this.detailsForGene.getActionableGene() != null && this.detailsForGene.getActionableGene().startsWith("YES");
		for (String variant : this.detailsForGene.getActionableVariants()) {
			isActionable &= variant != null && variant.startsWith("YES");
		}
		return isActionable;
				
	}
	
	private void createOtherAnnotations() {
		
	}

	public String getGene() {
		return gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
		this.buildGeneDetails();
	}

	public String getSequenceChange() {
		return sequenceChange;
	}

	public void setSequenceChange(String sequenceChange) {
		this.sequenceChange = sequenceChange;
		this.buildGeneDetails();
	}

	public String getAberration() {
		return aberration;
	}

	public void setAberration(String aberration) {
		this.aberration = aberration;
		this.buildGeneDetails();
	}

	public String getFdaApprovedWithIndication() {
		return fdaApprovedWithIndication;
	}

	public void setFdaApprovedWithIndication(String fdaApprovedWithIndication) {
		this.fdaApprovedWithIndication = fdaApprovedWithIndication;
	}

	public String getFdaApprovedOutsideOfIndication() {
		return fdaApprovedOutsideOfIndication;
	}

	public void setFdaApprovedOutsideOfIndication(String fdaApprovedOutsideOfIndication) {
		this.fdaApprovedOutsideOfIndication = fdaApprovedOutsideOfIndication;
	}

	public String getClinicalTrials() {
		return clinicalTrials;
	}

	public void setClinicalTrials(String clinicalTrials) {
		this.clinicalTrials = clinicalTrials;
	}

	public String getGeneDetails() {
		return geneDetails;
	}

	public void setGeneDetails(String geneDetails) {
		this.geneDetails = geneDetails;
	}

	public GeneVariantDetails getDetailsForGene() {
		return detailsForGene;
	}

	public void setDetailsForGene(GeneVariantDetails detailsForGene) {
		this.detailsForGene = detailsForGene;
	}

	public String getUniqueIdField() {
		return uniqueIdField;
	}

	public void setUniqueIdField(String uniqueIdField) {
		this.uniqueIdField = uniqueIdField;
	}

	public Map<String, String> getTooltips() {
		return tooltips;
	}

	public void setTooltips(Map<String, String> tooltips) {
		this.tooltips = tooltips;
	}

	public String getAlleleFrequency() {
		return alleleFrequency;
	}

	public void setAlleleFrequency(String alleleFrequency) {
		this.alleleFrequency = alleleFrequency;
	}

	public PassableValue getGeneDetailsValue() {
		return geneDetailsValue;
	}

	public void setGeneDetailsValue(PassableValue geneDetailsValue) {
		this.geneDetailsValue = geneDetailsValue;
	}

	public String getMdaAnnotations() {
		return mdaAnnotations;
	}

	public void setMdaAnnotations(String mdaAnnotations) {
		this.mdaAnnotations = mdaAnnotations;
	}

	public Boolean getIsSelected() {
		return isSelected;
	}

	public void setIsSelected(Boolean isSelected) {
		this.isSelected = isSelected;
	}


}
