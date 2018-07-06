package utsw.bicf.answer.reporting.parse;

import java.util.ArrayList;
import java.util.List;

import utsw.bicf.answer.reporting.finalreport.GeneVariantDetails;

public class AnnotationRow {
	
	public static final String HEADER_TESTED_PANEL = "Tested Panel";
	public static final String HEADER_REPORT_NB = "Report#";
	public static final String HEADER_GENE = "Gene";
	public static final String HEADER_ALTERATION = "Alteration";
	public static final String HEADER_ALLELIC_FREQUENCY = "Allelic frequency / cfDNA (%)";
	public static final String HEADER_CNV = "Copy Number / Level";
	public static final String HEADER_FUNCTIONAL_SIGNIFICANCE = "Functional Significance";
	public static final String HEADER_ANNOTATION = "Annotation";
	public static final String HEADER_ACTIONABLE_GENE = "Actionable Gene";
	public static final String HEADER_ACTIONABLE_VARIANT = "Actionable Variant";
	public static final String HEADER_ACTIONABLE_FOR = "Actionable For";

	String testedPanel;
	String reportNb;
	String gene;
	String alteration;
	String allelicFrequency;
	String cnv;
	String functionalSignificance;
	List<String> annotations;
	List<AnnotationCategory> annotationCategories = new ArrayList<AnnotationCategory>();
	String actionableGene;
	List<String> actionableVariants = new ArrayList<String>();
	List<String> actionableFors = new ArrayList<String>();
	AnnotationCategory geneFunction;
	AnnotationCategory alterationFunction;
	AnnotationCategory implication;
	AnnotationCategory tumorInfo;
	
	public AnnotationRow() {
	}

	/**
	 * Annotation is composed of 3 or 4 parts.
	 * Need to separate them by their "title"
	 * 
	 * OR that one or both of the last 2 is missing
	 */
	private void parseAnnotation() {
		for (String c : annotations) {
			String[] textItems = c.split(": ");
			String text = null;
			if (textItems.length > 1) {
				text = textItems[1];
			}
			if (c.startsWith(AnnotationCategory.BIOMARKER_SUMMARY_TITLE)) geneFunction = new AnnotationCategory(AnnotationCategory.BIOMARKER_SUMMARY_TITLE, text == null ? c.replaceFirst(AnnotationCategory.BIOMARKER_SUMMARY_TITLE + ": ", "") : text);
			else if(c.startsWith(AnnotationCategory.FUNCTIONAL_ANNOTATION_TITLE)) alterationFunction = new AnnotationCategory(AnnotationCategory.FUNCTIONAL_ANNOTATION_TITLE, text == null ? c.replaceFirst(AnnotationCategory.FUNCTIONAL_ANNOTATION_TITLE + ": ", "") : text);
			else if(c.startsWith(AnnotationCategory.POTENTIAL_THERAPEUTIC_IMPLICATIONS_TITLE)) implication = new AnnotationCategory(AnnotationCategory.POTENTIAL_THERAPEUTIC_IMPLICATIONS_TITLE,text == null ? c.replaceFirst(AnnotationCategory.POTENTIAL_THERAPEUTIC_IMPLICATIONS_TITLE + ": ", "") : text);
			else if(c.startsWith(AnnotationCategory.TUMOR_SPECIFIC_INFO_TITLE)) tumorInfo = new AnnotationCategory(AnnotationCategory.TUMOR_SPECIFIC_INFO_TITLE, text == null ? c.replaceFirst(AnnotationCategory.TUMOR_SPECIFIC_INFO_TITLE + ": ", "") : text);
		}
		
	}
	
	public String getTestedPanel() {
		return testedPanel;
	}
	public void setTestedPanel(String testedPanel) {
		this.testedPanel = testedPanel;
	}
	public String getReportNb() {
		return reportNb;
	}
	public void setReportNb(String reportNb) {
		this.reportNb = reportNb;
	}
	public String getGene() {
		return gene;
	}
	public void setGene(String gene) {
		this.gene = gene;
	}
	public String getAlteration() {
		return alteration;
	}
	public void setAlteration(String alteration) {
		this.alteration = alteration;
	}
	public String getCnv() {
		return cnv;
	}
	public void setCnv(String cnv) {
		this.cnv = cnv;
	}
	public String getFunctionalSignificance() {
		return functionalSignificance;
	}
	public void setFunctionalSignificance(String functionalSignificance) {
		this.functionalSignificance = functionalSignificance;
	}
	public List<String> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(List<String> annotations) {
		this.annotations = annotations;
		parseAnnotation();
	}
	public String getActionableGene() {
		return actionableGene;
	}
	public void setActionableGene(String actionableGene) {
		this.actionableGene = actionableGene;
	}
	public List<String> getActionableVariants() {
		return actionableVariants;
	}
	public void setActionableVariants(List<String> actionableVariants) {
		this.actionableVariants = actionableVariants;
	}
	public List<String> getActionableFors() {
		return actionableFors;
	}
	public void setActionableFors(List<String> actionableFors) {
		this.actionableFors = actionableFors;
	}
	public void prettyPrint() {
		System.out.println("Tested Panel: " + testedPanel + " MAF: " + allelicFrequency + " actionable for: " + actionableFors );
		
	}
	public String getAllelicFrequency() {
		return allelicFrequency;
	}
	public void setAllelicFrequency(String allelicFrequency) {
		this.allelicFrequency = allelicFrequency;
	}
	public AnnotationCategory getGeneFunction() {
		return geneFunction;
	}
	public void setGeneFunction(AnnotationCategory geneFunction) {
		this.geneFunction = geneFunction;
	}
	public AnnotationCategory getAlterationFunction() {
		return alterationFunction;
	}
	public void setAlterationFunction(AnnotationCategory alterationFunction) {
		this.alterationFunction = alterationFunction;
	}
	public AnnotationCategory getImplication() {
		return implication;
	}
	public void setImplication(AnnotationCategory implication) {
		this.implication = implication;
	}

	public AnnotationCategory getTumorInfo() {
		return tumorInfo;
	}

	public void setTumorInfo(AnnotationCategory tumorInfo) {
		this.tumorInfo = tumorInfo;
	}

	public List<AnnotationCategory> getAnnotationCategories() {
		annotationCategories = new ArrayList<AnnotationCategory>();
		annotationCategories.add(geneFunction);
		annotationCategories.add(alterationFunction);
		annotationCategories.add(implication);
		annotationCategories.add(tumorInfo);
		return annotationCategories;
	}

	public void setAnnotationCategories(List<AnnotationCategory> annotationCategories) {
		this.annotationCategories = annotationCategories;
	}
	
	
	
}
