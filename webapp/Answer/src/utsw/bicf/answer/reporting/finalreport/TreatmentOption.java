package utsw.bicf.answer.reporting.finalreport;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utsw.bicf.answer.reporting.parse.AnnotationRow;
import utsw.bicf.answer.reporting.parse.BiomarkerTrialsRow;
import utsw.bicf.answer.reporting.parse.MDAReportTemplate;
import utsw.bicf.answer.reporting.parse.MDAReportTemplateConstants;

public class TreatmentOption {

	String gene;
	String sequenceChange;
	String aberration;
	String fdaApprovedWithIndication;
	String fdaApprovedOutsideOfIndication;
	String clinicalTrials;
	
	public TreatmentOption() {
		super();
	}

	public TreatmentOption(String gene, String sequenceChange, String aberration, String fdaApprovedWithIndication,
			String fdaApprocedOutsideOfIndication, String clinicalTrials) {
		this.gene = gene;
		this.sequenceChange = sequenceChange;
		this.aberration = aberration;
		this.fdaApprovedWithIndication = fdaApprovedWithIndication;
		this.fdaApprovedOutsideOfIndication = fdaApprocedOutsideOfIndication;
		this.clinicalTrials = clinicalTrials;
	}
	
	public static List<TreatmentOption> createFromDMAReport(MDAReportTemplate mdaReport) {
		List<TreatmentOption> options = new ArrayList<TreatmentOption>();
		for (String geneVariant : mdaReport.getAnnotationRows().keySet()) {
			AnnotationRow mdaAnnotation = mdaReport.getAnnotationRows().get(geneVariant);
			TreatmentOption option = new TreatmentOption();
			option.setGene(mdaAnnotation.getGene());
			StringBuilder withIndication = new StringBuilder();
			StringBuilder outsideOfIndication = new StringBuilder();
			for (int i = 0; i < mdaAnnotation.getActionableVariants().size(); i++) {
				String actionableFor = mdaAnnotation.getActionableFors().get(i);
				String actionableVariant = mdaAnnotation.getActionableVariants().get(i);
				String actionableGene = mdaAnnotation.getActionableGene();
				if (actionableFor.contains(MDAReportTemplateConstants.ACTIONABLE_VARIANT_RESISTANCE)) {
//					resistance.append(createStringForTherapyMatchEntry(actionableGene, actionableVariant, actionableFor));
					//TODO no indication of resistance for now?
				}
				else if (actionableVariant.contains(MDAReportTemplateConstants.ACTIONABLE_VARIANT_YES)) {
					withIndication.append(createStringForTherapyMatchEntry(actionableGene, actionableVariant, actionableFor));
				}
				else if (actionableVariant.contains(MDAReportTemplateConstants.ACTIONABLE_VARIANT_POTENTIALLY) ||
						actionableVariant.contains(MDAReportTemplateConstants.ACTIONABLE_VARIANT_NO) ||
								actionableVariant.contains(MDAReportTemplateConstants.ACTIONABLE_VARIANT_UNKNOWN)) {
					outsideOfIndication.append(createStringForTherapyMatchEntry(actionableGene, actionableVariant, actionableFor));
				}
			}
			option.setFdaApprovedWithIndication(withIndication.toString());
			option.setFdaApprovedOutsideOfIndication(outsideOfIndication.toString());
			option.setAberration(mdaAnnotation.getAlteration());
			
			//count trials
			int geneCount = 0;
			Pattern pattern = Pattern.compile(".*(" + mdaAnnotation.getGene() + ")[\\s,_].*");
			List<BiomarkerTrialsRow> allTrials = new ArrayList<BiomarkerTrialsRow>();
			allTrials.addAll(mdaReport.getSelectedBiomarkers());
			allTrials.addAll(mdaReport.getRelevantBiomarkers());
			for (BiomarkerTrialsRow trial : allTrials) {
				Matcher matcher = null;
				if (trial.getSelectedBiomarker() != null) {
					matcher = pattern.matcher(trial.getSelectedBiomarker());
				}
				else {
					matcher = pattern.matcher(trial.getRelevantBiomarker());
				}
				if (matcher.matches()) {
					geneCount++;
				}
			}
			option.setClinicalTrials(String.valueOf(geneCount));
			
			options.add(option);
		}
		return options;
	}

	private static String createStringForTherapyMatchEntry(String actionableGene, String actionableVariant, String actionableFor) {
		StringBuilder items = new StringBuilder();
		items.append("<b>Applicable Gene: </b>" + actionableGene).append(" ");
		items.append("<b>Applicable Variant: </b>" + actionableVariant).append(" ");
		items.append(actionableFor).append(" ");
		return items.toString();
	}


	public static List<TreatmentOption> createFakeTreatmentOptionsSummary() {
		List<TreatmentOption> summary = new ArrayList<TreatmentOption>();
		TreatmentOption option = new TreatmentOption("FLT3", "c.2508_2510delCAT", "p.Ile836del", "None",
				"Nintedanib,Midostaurin,Sorafenib,Sunitinib,Ponatinib and Cabozantinib", "No");
		summary.add(option);
		option = new TreatmentOption("BRAF", "c.1799T>A", "p.Val600Glu", "None",
				"Dabrafenib,Vemurafenib,Trametinib and Cobimetinib", "1");
		summary.add(option);
		option = new TreatmentOption("IDH1", "c.394C>T", "p.Arg132Cys", "None", "Azacitidine and Decitabine", "No");
		summary.add(option);
		option = new TreatmentOption("KRAS", "c.38G>A", "p.Gly13Asp", "None", "Trametinib and Cobimetinib", "1");
		summary.add(option);
		option = new TreatmentOption("MAP2K1", "c.371C>T", "p.Pro124Leu", "None", "Trametinib and Cobimetinib", "1");
		summary.add(option);
		option = new TreatmentOption("NRAS", "c.181C>A", "p.Gln61Lys", "None", "Trametinib and Cobimetinib", "1");
		summary.add(option);
		option = new TreatmentOption("JAK2", "c.1849G>T", "p.Val617Phe", "None", "Ruxolitinib", "No");
		summary.add(option);
		option = new TreatmentOption("PIK3CA", "c.3140A>G", "p.His1047Arg", "None", "None", "No");
		summary.add(option);
		option = new TreatmentOption("NOTCH1", "c.4799T>C", "p.Leu1600Pro", "None", "None", "No");
		summary.add(option);
		option = new TreatmentOption("EGFR", "c.2155G>A", "p.Gly719Ser", "None", "None", "No");
		summary.add(option);
		option = new TreatmentOption("EGFR", "c.2369C>T", "p.Thr790Met", "None", "None", "No");
		summary.add(option);

		return summary;
	}

	public String getGene() {
		return gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public String getSequenceChange() {
		return sequenceChange;
	}

	public void setSequenceChange(String sequenceChange) {
		this.sequenceChange = sequenceChange;
	}

	public String getAberration() {
		return aberration;
	}

	public void setAberration(String aberration) {
		this.aberration = aberration;
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


}
