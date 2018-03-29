package utsw.bicf.answer.reporting.finalreport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utsw.bicf.answer.reporting.parse.AnnotationCategory;
import utsw.bicf.answer.reporting.parse.AnnotationRow;
import utsw.bicf.answer.reporting.parse.BiomarkerTrialsRow;
import utsw.bicf.answer.reporting.parse.MDAReportTemplate;
import utsw.bicf.answer.reporting.parse.MDAReportTemplateConstants;

public class GeneVariantDetails {

	String gene;
	String aberration;
	String taf;
	String coord;
	String transcriptId;
	String npNumber;
	String cnv;
	String variantQualifier;
	List<String> fdaWithinIndication;
	List<String> fdaOutsideIndication;
	AnnotationCategory treatmentApproach;
	List<String> resistanceAndInteractions;
	List<ClinicalTrialMatch> clinicalTrialMatches;
	List<AnnotationCategory> annotationCategories;
	String actionableGene;
	List<String> actionableVariants;
	
	public GeneVariantDetails() {
		super();
	}
	
//	public GeneVariantDetails(String gene, String aberration, String taf, String coord, String transcriptId, String npNumber,
//			String variantQualifier, List<String> fdaWithinIndication, List<String> fdaOutsideIndicatin, String threatmentApproach,
//			List<String> resistanceAndInteractions, List<ClinicalTrialMatch> clinicalTrialMatches, String[] information, String cnv,
//			String actionnableGene,	List<String> actionnableVariants) {
//		super();
//		this.gene = gene;
//		this.aberration = aberration;
//		this.taf = taf;
//		this.coord = coord;
//		this.transcriptId = transcriptId;
//		this.npNumber = npNumber;
//		this.variantQualifier = variantQualifier;
//		this.fdaWithinIndication = fdaWithinIndication;
//		this.fdaOutsideIndicatin = fdaOutsideIndicatin;
//		this.treatmentApproach = threatmentApproach;
//		this.resistanceAndInteractions = resistanceAndInteractions;
//		this.clinicalTrialMatches = clinicalTrialMatches;
//		this.information = information;
//		this.cnv = cnv;
//		this.actionnableGene = actionnableGene;
//		this.actionnableVariants = actionnableVariants;
//	}

	public static List<GeneVariantDetails> createFromMDAReport(MDAReportTemplate mdaReport) {
		List<GeneVariantDetails> details = new ArrayList<GeneVariantDetails>();
		for (String geneVariant : mdaReport.getAnnotationRows().keySet()) {
			AnnotationRow mdaAnnotation = mdaReport.getAnnotationRows().get(geneVariant);
			GeneVariantDetails aDetail = new GeneVariantDetails();
			aDetail.setGene(mdaAnnotation.getGene());
			aDetail.setAberration(mdaAnnotation.getAlteration());
			aDetail.setTaf(mdaAnnotation.getAllelicFrequency());
			aDetail.setVariantQualifier(mdaAnnotation.getFunctionalSignificance());
			aDetail.setTreatmentApproach(mdaAnnotation.getImplication());
			aDetail.setAnnotationCategories(mdaAnnotation.getAnnotationCategories());
			aDetail.setCnv(mdaAnnotation.getCnv());
			aDetail.setActionableGene(mdaAnnotation.getActionableGene());
			
			List<String> withIndication = new ArrayList<String>();
			List<String> outsideOfIndication = new ArrayList<String>();
			List<String> resistance = new ArrayList<String>();
			List<String> actionableVariants = new ArrayList<String>();
			for (int i = 0; i < mdaAnnotation.getActionableVariants().size(); i++) {
				String actionableFor = mdaAnnotation.getActionableFors().get(i);
				String actionableVariant = mdaAnnotation.getActionableVariants().get(i);
				actionableVariants.add(actionableVariant);
				String actionableGene = mdaAnnotation.getActionableGene();
				if (actionableFor.contains(MDAReportTemplateConstants.ACTIONABLE_VARIANT_RESISTANCE)) {
					resistance.addAll(createStringListForTherapyMatchEntry(actionableGene, actionableVariant, actionableFor));
				}
				else if (actionableVariant.contains(MDAReportTemplateConstants.ACTIONABLE_VARIANT_YES)) {
					withIndication.addAll(createStringListForTherapyMatchEntry(actionableGene, actionableVariant, actionableFor));
				}
				else if (actionableVariant.contains(MDAReportTemplateConstants.ACTIONABLE_VARIANT_POTENTIALLY) ||
						actionableVariant.contains(MDAReportTemplateConstants.ACTIONABLE_VARIANT_NO) ||
								actionableVariant.contains(MDAReportTemplateConstants.ACTIONABLE_VARIANT_UNKNOWN)) {
					outsideOfIndication.addAll(createStringListForTherapyMatchEntry(actionableGene, actionableVariant, actionableFor));
				}
			}
			aDetail.setFdaWithinIndication(withIndication);
			aDetail.setFdaOutsideIndication(outsideOfIndication);
			aDetail.setResistanceAndInteractions(resistance);
			aDetail.setActionableVariants(actionableVariants);
			
			List<ClinicalTrialMatch> trialMatches = new ArrayList<ClinicalTrialMatch>();
			Pattern pattern = Pattern.compile(".*(" + aDetail.getGene() + ")[\\s,_].*");
			for (BiomarkerTrialsRow trial : mdaReport.getSelectedBiomarkers()) {
				Matcher matcher = pattern.matcher(trial.getSelectedBiomarker());
				if (matcher.matches()) {
					ClinicalTrialMatch trialMatch = new ClinicalTrialMatch(
							trial.getTitle(), "", "", trial.getPi() + ": " + trial.getDept(), trial.getNctid(), 
							FinalReportTemplateConstants.TRIAL_URL + trial.getNctid(), "Biomarker Selected Trial",
							trial.getDrugs());
					trialMatches.add(trialMatch);
				}
			}
			for (BiomarkerTrialsRow trial : mdaReport.getRelevantBiomarkers()) {
				if (trial.getSelectedBiomarker().indexOf(aDetail.getGene() + "[ ,_]+") > -1) {
					ClinicalTrialMatch trialMatch = new ClinicalTrialMatch(
							trial.getTitle(), "", "", "", trial.getNctid(), 
							FinalReportTemplateConstants.TRIAL_URL + trial.getNctid(), "Biomarker Relevant Trial",
							trial.getDrugs());
					trialMatches.add(trialMatch);
				}
			}
			aDetail.setClinicalTrialMatches(trialMatches);
			
			details.add(aDetail);
		}
		return details;
	}
	
	/**
	 * Create the list of geneVariantDetails as a map
	 * with key being a String of gene + variant.
	 * This allows recombining TreatmentOption with GeneVariantDetails
	 * @param mdaReport
	 * @return
	 */
	public static Map<String, GeneVariantDetails> createFromMDAReportasMap(MDAReportTemplate mdaReport) {
		Map<String, GeneVariantDetails> detailsMap = new HashMap<String, GeneVariantDetails>();
		List<GeneVariantDetails> details = createFromMDAReport(mdaReport);
		for (GeneVariantDetails d : details) {
			detailsMap.put(d.getGene() + d.getAberration(), d);
		}
		return detailsMap;
	}
	
	private static List<String> createStringListForTherapyMatchEntry(String actionableGene, String actionableVariant, String actionableFor) {
		List<String> items = new ArrayList<String>();
		items.add("<b>Applicable Gene: </b>" + actionableGene);
		items.add("<b>Applicable Variant: </b>" + actionableVariant);
		items.add(actionableFor);
		return items;
	}
	

//	public static List<GeneVariantDetails> createFakeGeneDetails() {
//		List<GeneVariantDetails> details = new ArrayList<GeneVariantDetails>();
//		List<String> fdaWithin = new ArrayList<String>();
//		fdaWithin.add("No available data");
//		List<String> fdaOutside = new ArrayList<String>();
//		fdaOutside.add("Nintedanib, Midostaurin, Sorafenib, Sunitinib, Ponatinib and Cabozantinib");
//		List<String> resistance = new ArrayList<String>();
//		resistance.add("No available data");
//		GeneVariantDetails aDetail = new GeneVariantDetails("FLT3", "p.Ile836del", "5.38%", "chr13:28592634", "NM_004119",
//				"NP_004110.2", "", fdaWithin, fdaOutside,
//				"Activating alterations in FLT3 may predict sensitivity to tyrosine kinase inhibitors, including both FLT3-specific "
//						+ "and multi-kinase inhibitors (24749672, 25231999). Sunitinib, sorafenib, cabozantinib, and ponatinib are "
//						+ "inhibitors that target multiple kinases, including Flt3, and have been approved by the FDA for use in some "
//						+ "indications; these and other inhibitors are under clinical investigation in several cancer types (20212254, "
//						+ "16990784, 27060207, ASCO 2011, Abstract 6518). Second generation Flt3 inhibitors with greater specificity for "
//						+ "Flt3, such as quizartinib (AC-220), crenolanib (CP-868596), and PLX3397, are also in clinical development "
//						+ "(24883179, 24002496, 24227820, ASH 2011, Abstract 764).",
//				resistance, null,
//				new String[] {
//						"FLT3 has been reported to function as an oncogene, with activating mutations resulting in increased "
//								+ "proliferation, decreased apoptosis, and cellular transformation (11090077, 10698507, 16116483, 11290608, "
//								+ "17936561).", // separate new lines
//						"FLT3 I836del is a deletion of one amino acid that occurs in the activation loop of the Flt3 kinase domain, and is "
//								+ "one of several alterations commonly referred to as FLT3 tyrosine kinase domain (FLT3-TKD) mutations "
//								+ "(25231999). FLT3 I836del has been reported in patients with hematologic malignancies and has been shown to "
//								+ "result in constitutive activity of the Flt3 protein (12620411, 14504097, 15256420)." },
//				null, null, null);
//		details.add(aDetail);
//		List<ClinicalTrialMatch> matches = new ArrayList<ClinicalTrialMatch>();
//		matches.add(new ClinicalTrialMatch(
//				"1. A Phase II, Open-label, Study in Subjects With BRAF V600E-Mutated Rare Cancers With Several Histologies "
//						+ "to Investigate the Clinical Efficacy and Safety of the Combination Therapy of Dabrafenib and Trametinib",
//				"250 Massachusetts Ave, Cambridge, MA 02139, USA", 
//				"Recruiting", 
//				"Novartis Pharmaceuticals 1-888-669-6682 Novartis.email@novartis.com", 
//				"NCT02034110", 
//				"https://clinicaltrials.gov/ct2/show/NCT02034110", 
//				"None Available.", null));
//		
//		fdaWithin = new ArrayList<String>();
//		fdaWithin.add("No available data");
//		fdaOutside = new ArrayList<String>();
//		fdaOutside.add("Dabrafenib, Vemurafenib, Trametinib and Cobimetinib");
//		resistance = new ArrayList<String>();
//		resistance.add("No available data");
//		aDetail = new GeneVariantDetails("BRAF", "p.Val600Glu", "9.42%", "chr7:140453136", "NM_004333", "XP_005250102.1", "",
//				fdaWithin, fdaOutside,
//				"Braf signals upstream of the MAPK pathway, and BRAF amplification or activating mutations may confer "
//						+ "sensitivity to inhibitors of Braf and/or components of the MAPK pathway, including MEK (16273091). Inhibition "
//						+ "of Hsp90 leads to the degradation of oncogenic proteins, such as Braf, which suggests that Hsp90 inhibitors "
//						+ "may be particularly effective in malignancies with activating BRAF mutations, as well as malignancies where "
//						+ "proteotoxic stress is involved (19118027, 22215907).",
//						resistance,
//				matches,
//				new String[] {
//						"BRAF activating mutations or amplification have been reported to result in uncontrolled cell growth and "
//								+ "tumorigenesis (12068308, 21447722). BRAF V600E mutations, and subsequent MAPK pathway activation, have "
//								+ "been suggested to drive the pathogenesis of classical Hairy cell leukemia (HCL) (27554081, 24871132, 21663470,"
//								+ "23211289, 23349307). Several studies have reported that BRAF V600E mutations may be useful in "
//								+ "distinguishing classical Hairy cell leukemia from other B-cell lymphoproliferative disorders (23211289, "
//								+ "22072557, 21910720, 21663470).", // separate new lines
//						"BRAF V600E is a missense alteration located in the activation domain of the Braf protein (21388974, "
//								+ "26657898). This alteration has been reported as the most frequently occurring BRAF mutation in cancer, and "
//								+ "shown to lead to constitutive activation of the Braf protein and subsequent activation of the MAPK pathway; "
//								+ "BRAF V600E has also been shown to be oncogenic and lead to increased survival, proliferation, tumor "
//								+ "formation, and invasion, as compared with wild-type BRAF (17956344, 21388974)." },
//				null, null, null);
//		
//		details.add(aDetail);
//		return details;
//	}

	public String getGene() {
		return gene;
	}

	public void setGene(String gene) {
		this.gene = gene;
	}

	public String getAberration() {
		return aberration;
	}

	public void setAberration(String aberration) {
		this.aberration = aberration;
	}

	public String getTaf() {
		return taf;
	}

	public void setTaf(String taf) {
		this.taf = taf;
	}

	public String getCoord() {
		return coord;
	}

	public void setCoord(String coord) {
		this.coord = coord;
	}

	public String getTranscriptId() {
		return transcriptId;
	}

	public void setTranscriptId(String transcriptId) {
		this.transcriptId = transcriptId;
	}

	public String getNpNumber() {
		return npNumber;
	}

	public void setNpNumber(String npNumber) {
		this.npNumber = npNumber;
	}

	public String getVariantQualifier() {
		return variantQualifier;
	}

	public void setVariantQualifier(String variantQualifier) {
		this.variantQualifier = variantQualifier;
	}

	public List<String> getFdaWithinIndication() {
		return fdaWithinIndication;
	}

	public void setFdaWithinIndication(List<String> fdaWithinIndication) {
		this.fdaWithinIndication = fdaWithinIndication;
	}

	public List<String> getFdaOutsideIndication() {
		return fdaOutsideIndication;
	}

	public void setFdaOutsideIndication(List<String> fdaOutsideIndication) {
		this.fdaOutsideIndication = fdaOutsideIndication;
	}

	public AnnotationCategory getTreatmentApproach() {
		return treatmentApproach;
	}

	public void setTreatmentApproach(AnnotationCategory treatmentApproach) {
		this.treatmentApproach = treatmentApproach;
	}

	public List<String> getResistanceAndInteractions() {
		return resistanceAndInteractions;
	}

	public void setResistanceAndInteractions(List<String> resistanceAndInteractions) {
		this.resistanceAndInteractions = resistanceAndInteractions;
	}

	public List<ClinicalTrialMatch> getClinicalTrialMatches() {
		return clinicalTrialMatches;
	}

	public void setClinicalTrialMatches(List<ClinicalTrialMatch> trialMatches) {
		this.clinicalTrialMatches = trialMatches;
	}

	public String getCnv() {
		return cnv;
	}

	public void setCnv(String cnv) {
		this.cnv = cnv;
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

	public void setActionableVariant(List<String> actionableVariants) {
		this.actionableVariants = actionableVariants;
	}

	public List<AnnotationCategory> getAnnotationCategories() {
		return annotationCategories;
	}

	public void setAnnotationCategories(List<AnnotationCategory> annotationCategories) {
		this.annotationCategories = annotationCategories;
	}

	public void setFdaOutsideIndicatin(List<String> fdaOutsideIndicatin) {
		this.fdaOutsideIndication = fdaOutsideIndicatin;
	}

	public void setActionableVariants(List<String> actionnableVariants) {
		this.actionableVariants = actionnableVariants;
	}

}
