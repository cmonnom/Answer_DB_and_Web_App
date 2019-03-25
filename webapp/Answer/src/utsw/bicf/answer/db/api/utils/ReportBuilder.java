package utsw.bicf.answer.db.api.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import utsw.bicf.answer.controller.serialization.GeneVariantAndAnnotation;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.Annotation;
import utsw.bicf.answer.model.extmapping.CNV;
import utsw.bicf.answer.model.extmapping.CNVReport;
import utsw.bicf.answer.model.extmapping.IndicatedTherapy;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Report;
import utsw.bicf.answer.model.extmapping.Translocation;
import utsw.bicf.answer.model.extmapping.TranslocationReport;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.PatientInfo;
import utsw.bicf.answer.model.hybrid.PubMed;
import utsw.bicf.answer.reporting.finalreport.CNVClinicalSignificance;
import utsw.bicf.answer.reporting.finalreport.CNVReportWithHighestTier;
import utsw.bicf.answer.reporting.finalreport.VariantReport;
import utsw.bicf.answer.reporting.parse.BiomarkerTrialsRow;
import utsw.bicf.answer.reporting.parse.MDAReportTemplate;
import utsw.bicf.answer.security.NCBIProperties;
import utsw.bicf.answer.security.OtherProperties;

public class ReportBuilder {

	private static final List<String> STRONG_TIERS = Arrays.asList("1A", "1B");
	private static final List<String> POSSIBLE_TIERS = Arrays.asList("2C", "2D");
	public static final List<String> UNKNOWN_TIERS = Arrays.asList("3");
	public static final List<String> EXCLUDE_TIERS = Arrays.asList("4");
	private static final List<String> THERAPY_TIERS = Arrays.asList("1A", "1B", "2C");
	private static final List<String> TIER1_CLASSIFICATIONS = Arrays.asList(Variant.CATEGORY_LIKELY_PATHOGENIC,
			Variant.CATEGORY_PATHOGENIC);

	private static final String CAT_CLINICAL_TRIAL = "Clinical Trial";
	private static final String CAT_THERAPY = "Therapy";
	private static final String TIER_2D = "2D";
	

	ModelDAO modelDAO;
	String caseId;
	User user;
	OtherProperties otherProps;
	NCBIProperties ncbiProps;
	RequestUtils utils;
	Report report = new Report();
	OrderCase caseDetails;

	public ReportBuilder(RequestUtils utils, ModelDAO modelDAO, String caseId, User user, OtherProperties otherProps,
			NCBIProperties ncbiProps) {
		super();
		this.modelDAO = modelDAO;
		this.caseId = caseId;
		this.user = user;
		this.otherProps = otherProps;
		this.ncbiProps = ncbiProps;
		this.utils = utils;
	}

	/**
	 * To reset all arrays and other variables if needed
	 */
	private void init() {
	}

	public Report build() throws ClientProtocolException, IOException, URISyntaxException, UnsupportedOperationException, JAXBException, SAXException, ParserConfigurationException {
		caseDetails = utils.getCaseDetails(caseId, null);
		report.setCaseId(caseDetails.getCaseId());
		report.setCaseName(caseDetails.getCaseName());
		report.setLabTestName(caseDetails.getLabTestName());
		PatientInfo patientInfo = new PatientInfo(caseDetails);
		report.setPatientInfo(patientInfo);
		report.setReportName(caseDetails.getCaseName());
		report.setModifiedBy(user.getUserId());
		report.setCreatedBy(user.getUserId());
		report.setDateCreated(OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
		report.setDateModified(OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
		
		report.setLive(true);

		//only keep selected variants
		List<Variant> variantsSelected = caseDetails.getVariants().stream().filter(v -> isTrue(v.getSelected())).collect(Collectors.toList());
		List<CNV> cnvsSelected = caseDetails.getCnvs().stream().filter(v -> isTrue(v.getSelected())).collect(Collectors.toList());
		List<Translocation> ftlsSelected = caseDetails.getTranslocations().stream().filter(v -> isTrue(v.getSelected())).collect(Collectors.toList());
		
		//filter out unselected annotations and variants without tiered annotations
		Map<VariantReport, List<Annotation>> annotationsPerSNP = extractAnnotationsForSNPs(variantsSelected);
		Map<CNVReportWithHighestTier, List<Annotation>> annotationsPerCNV = extractAnnotationsForCNVs(cnvsSelected);
		Map<Translocation, List<Annotation>> annotationsPerFTL = extractAnnotationsForFTLs(ftlsSelected);
		
		//Now we should only be left with valid variants and annotations
		List<Annotation> allAnnotations = new ArrayList<Annotation>();
		allAnnotations.addAll(annotationsPerSNP.values().stream().flatMap(List::stream).collect(Collectors.toList()));
		allAnnotations.addAll(annotationsPerCNV.values().stream().flatMap(List::stream).collect(Collectors.toList()));
		allAnnotations.addAll(annotationsPerFTL.values().stream().flatMap(List::stream).collect(Collectors.toList()));
		//set the tier if represented in a classification of Pathogenic or Likely Pathogenic
		allAnnotations.stream().filter(a-> a.getTier() == null && a.getCategory() != null).forEach(a -> a.setTier(this.getTierFromClassification(a.getClassification())));
		report.setClinicalTrials(this.getTrials(allAnnotations));
		report.setPubmeds(this.getPubmedReferences(allAnnotations));
		
		report.setIndicatedTherapies(this.getIndicatedTherapies(annotationsPerSNP, annotationsPerCNV, annotationsPerFTL));
		report.setTranslocations(this.getFTLs(annotationsPerFTL));
		report.setCnvs(this.getCNVs(annotationsPerCNV));
		this.setClinicalSignificances(annotationsPerSNP, annotationsPerCNV);
		report.buildSummaryTable2();
		return report;
	}

	private Map<String, GeneVariantAndAnnotation> setClinicalSignificances(
			Map<VariantReport, List<Annotation>> annotationsPerSNP, Map<CNVReportWithHighestTier, List<Annotation>> annotationsPerCNV) {
		Map<String, GeneVariantAndAnnotation> annotationsStrongByVariant = new HashMap<String, GeneVariantAndAnnotation>();
		Map<String, GeneVariantAndAnnotation> annotationsPossibleByVariant = new HashMap<String, GeneVariantAndAnnotation>();
		Map<String, GeneVariantAndAnnotation> annotationsUnknownByVariant = new HashMap<String, GeneVariantAndAnnotation>();
		for (VariantReport v : annotationsPerSNP.keySet()) {
			String name = v.getVariant().getGeneName() + " " + v.getVariant().getNotation();
			List<Annotation> annotations = annotationsPerSNP.get(v);
			//At this point, the highest tier should be set on at least one card
			if (STRONG_TIERS.contains(v.getHighestAnnotationTier())) {
				List<Annotation> strongAnnotations = annotations.stream().filter(a -> this.annotationGoesInClinicalSignificanceTable(a)).collect(Collectors.toList());
				//build a map of concatenated annotations by category
				Map<String, String> strongAnnotationsByCategory = strongAnnotations.stream().collect(Collectors.groupingBy(Annotation::getCategory, Collectors.mapping(Annotation::getText,  Collectors.joining(" "))));
				annotationsStrongByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(v.getVariant(), strongAnnotationsByCategory));
			}
			else if (POSSIBLE_TIERS.contains(v.getHighestAnnotationTier())) {
				List<Annotation> possibleAnnotations = annotations.stream().filter(a -> this.annotationGoesInClinicalSignificanceTable(a)).collect(Collectors.toList());
				//build a map of concatenated annotations by category
				Map<String, String> possibleAnnotationsByCategory = possibleAnnotations.stream().collect(Collectors.groupingBy(Annotation::getCategory, Collectors.mapping(Annotation::getText,  Collectors.joining(" "))));
				annotationsPossibleByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(v.getVariant(), possibleAnnotationsByCategory));
			}
			else if (UNKNOWN_TIERS.contains(v.getHighestAnnotationTier())) {
				List<Annotation> unknownAnnotations = annotations.stream().filter(a -> this.annotationGoesInClinicalSignificanceTable(a)).collect(Collectors.toList());
				//build a map of concatenated annotations by category
				Map<String, String> unknownAnnotationsByCategory = unknownAnnotations.stream().collect(Collectors.groupingBy(Annotation::getCategory, Collectors.mapping(Annotation::getText,  Collectors.joining(" "))));
				annotationsUnknownByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(v.getVariant(), unknownAnnotationsByCategory));
			}
		}
		
		
		for (CNVReportWithHighestTier v : annotationsPerCNV.keySet()) {
			List<Annotation> annotations = annotationsPerCNV.get(v).stream().filter(a -> this.annotationGoesInClinicalSignificanceTable(a) && isStringEqual(a.getBreadth(), CNV.BREADTH_FOCAL)).collect(Collectors.toList());
			Map<String, CNVClinicalSignificance> annotationsByFocalGenesByCategory = new HashMap<String, CNVClinicalSignificance>();
			for (Annotation a : annotations) {
				String key = a.getCnvGenes().stream().collect(Collectors.joining(" "));
				if (v.getCnv().getAberrationType().equals("ITD")) {
					key += "-ITD";
				}
				CNVClinicalSignificance item = annotationsByFocalGenesByCategory.get(key);
				if (item == null) {
					item = new CNVClinicalSignificance(v, key, new ArrayList<Annotation>());
					annotationsByFocalGenesByCategory.put(key, item);
				}
				List<Annotation> annotationsForCategory = item.getAnnotations();
				annotationsForCategory.add(a);
			}
			if (STRONG_TIERS.contains(v.getHighestAnnotationTier())) {
				for (CNVClinicalSignificance item : annotationsByFocalGenesByCategory.values()) {
					annotationsStrongByVariant.put(item.getGenes().replaceAll("\\.", ""), new GeneVariantAndAnnotation(v.getCnv(), item.getGenes(), item.getAnnotationsByCategory()));
				}
			}
			if (POSSIBLE_TIERS.contains(v.getHighestAnnotationTier())) {
				for (CNVClinicalSignificance item : annotationsByFocalGenesByCategory.values()) {
					annotationsPossibleByVariant.put(item.getGenes().replaceAll("\\.", ""), new GeneVariantAndAnnotation(v.getCnv(), item.getGenes(), item.getAnnotationsByCategory()));
				}
			}
//			if (UNKNOWN_TIERS.contains(v.getHighestAnnotationTier())) {
//				for (CNVClinicalSignificance item : annotationsByFocalGenesByCategory.values()) {
//					annotationsUnknownByVariant.put(item.getGenes().replaceAll("\\.", ""), new GeneVariantAndAnnotation(v.getCnv(), item.getAnnotationsByCategory()));
//				}
//			}
		}
		
		
		report.setSnpVariantsStrongClinicalSignificance(annotationsStrongByVariant);
		report.setSnpVariantsPossibleClinicalSignificance(annotationsPossibleByVariant);
		report.setSnpVariantsUnknownClinicalSignificance(annotationsUnknownByVariant);
		return null;
	}
	
	private String getTierFromClassification(String classification) {
		if (classification != null && classification.equals(Variant.CATEGORY_LIKELY_PATHOGENIC)) {
			return "1A";
		}
		else if (classification != null && classification.equals(Variant.CATEGORY_PATHOGENIC)) {
			return "1B";
		}
		return null;
	}

	/**
	 * Finds all selected annotations for each variant
	 * and report any variant without tier or selected annotation
	 * @param variantsSelected
	 * @return a map of annotations per Variant
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	private Map<VariantReport, List<Annotation>> extractAnnotationsForSNPs(List<Variant> variantsSelected) throws ClientProtocolException, IOException, URISyntaxException {
		Map<VariantReport, List<Annotation>> annotationsPerSNP = new HashMap<VariantReport, List<Annotation>>();
		for (Variant v : variantsSelected) {
			final Variant vDetails = utils.getVariantDetails(v.getMongoDBId().getOid());
			boolean annotationsAreValid = true;
			if (!isEmptyList(vDetails.getReferenceVariant().getUtswAnnotations())) {
				vDetails.getReferenceVariant().getUtswAnnotations().stream().forEach(a -> Annotation.init(a, vDetails.getAnnotationIdsForReporting(), modelDAO));
				List<Annotation> selectedAnnotations = vDetails.getReferenceVariant().getUtswAnnotations().stream().filter(a -> a.getIsSelected()).collect(Collectors.toList());
				//set Uncategorized if needed
				selectedAnnotations.stream().forEach(a -> a.setCategory(a.getCategory() == null ? Variant.CATEGORY_UNCATEGORIZED : a.getCategory()));
				long tiersFound = selectedAnnotations.stream().filter(a -> a.getTier() != null).count();
				long moreThanTherapy = selectedAnnotations.stream().filter(a -> !a.getCategory().equals(CAT_THERAPY)).count();
				if (tiersFound == 0 || moreThanTherapy == 0) { //at least one annotation should have a tier and more than a therapy card
					annotationsAreValid = false;
				}
				else {
					String highestTier = selectedAnnotations.stream().filter(a -> a.getTier() != null).map(a -> a.getTier()).sorted().collect(Collectors.toList()).get(0);
					annotationsPerSNP.put(new VariantReport(vDetails, highestTier), selectedAnnotations);
					report.getSnpIds().add(v.getMongoDBId().getOid());
				}
			}
			else {
				annotationsAreValid = false;
			}
			if (!annotationsAreValid) { //report annotations missing a tier or variant without selected annotations
				List<Variant> missingAnnotationVariants = report.getMissingTierVariants();
				missingAnnotationVariants.add(vDetails);
				report.setMissingTierVariants(missingAnnotationVariants);
			}
		}
		return annotationsPerSNP;
	}
	
	/**
	 * Finds all selected annotations for each variant
	 * and report any variant without tier or selected annotation
	 * @param variantsSelected
	 * @return a map of annotations per Variant
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	private Map<CNVReportWithHighestTier, List<Annotation>> extractAnnotationsForCNVs(List<CNV> variantsSelected) throws ClientProtocolException, IOException, URISyntaxException {
		Map<CNVReportWithHighestTier, List<Annotation>> annotationsPerCNV = new HashMap<CNVReportWithHighestTier, List<Annotation>>();
		for (CNV v : variantsSelected) {
			final CNV vDetails = utils.getCNVDetails(v.getMongoDBId().getOid());
			boolean annotationsAreValid = true;
			if (!isEmptyList(vDetails.getReferenceCnv().getUtswAnnotations())) {
				vDetails.getReferenceCnv().getUtswAnnotations().stream().forEach(a -> Annotation.init(a, vDetails.getAnnotationIdsForReporting(), modelDAO));
				List<Annotation> selectedAnnotations = vDetails.getReferenceCnv().getUtswAnnotations().stream().filter(a -> a.getIsSelected()).collect(Collectors.toList());
				//set Uncategorized if needed
				selectedAnnotations.stream().forEach(a -> a.setCategory(a.getCategory() == null ? Variant.CATEGORY_UNCATEGORIZED : a.getCategory()));
				long tiersFound = selectedAnnotations.stream().filter(a -> a.getTier() != null).count();
				long moreThanTherapy = selectedAnnotations.stream().filter(a -> !a.getCategory().equals(CAT_THERAPY)).count();
				if (tiersFound == 0 || moreThanTherapy == 0) { //at least one annotation should have a tier
					annotationsAreValid = false;
				}
				else {
					String highestTier = selectedAnnotations.stream().filter(a -> a.getTier() != null).map(a -> a.getTier()).sorted().collect(Collectors.toList()).get(0);
					CNVReportWithHighestTier detailedCNV = new CNVReportWithHighestTier(vDetails, highestTier);
					if (detailedCNV.getBreadth() == null && selectedAnnotations.get(0).getBreadth() != null) {
						detailedCNV.setBreadth(selectedAnnotations.get(0).getBreadth());
					}
					annotationsPerCNV.put(detailedCNV, selectedAnnotations);
					report.getCnvIds().add(v.getMongoDBId().getOid());
				}
			}
			else {
				annotationsAreValid = false;
			}
			if (!annotationsAreValid) { //report annotations missing a tier or variant without selected annotations
				List<CNV> missingAnnotationCNVs = report.getMissingTierCNVs();
				missingAnnotationCNVs.add(vDetails);
				report.setMissingTierCNVs(missingAnnotationCNVs);
			}
		}
		return annotationsPerCNV;
	}
	
	/**
	 * Finds all selected annotations for each variant
	 * and report any variant without tier or selected annotation
	 * @param variantsSelected
	 * @return a map of annotations per Variant
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws ClientProtocolException 
	 */
	private Map<Translocation, List<Annotation>> extractAnnotationsForFTLs(List<Translocation> variantsSelected) throws ClientProtocolException, URISyntaxException, IOException {
		Map<Translocation, List<Annotation>> annotationsPerFTL = new HashMap<Translocation, List<Annotation>>();
		for (Translocation v : variantsSelected) {
			final Translocation vDetails = utils.getTranslocationDetails(v.getMongoDBId().getOid());
			boolean annotationsAreValid = true;
			if (!isEmptyList(vDetails.getReferenceTranslocation().getUtswAnnotations())) {
				vDetails.getReferenceTranslocation().getUtswAnnotations().stream().forEach(a -> Annotation.init(a, vDetails.getAnnotationIdsForReporting(), modelDAO));
				List<Annotation> selectedAnnotations = vDetails.getReferenceTranslocation().getUtswAnnotations().stream().filter(a -> a.getIsSelected()).collect(Collectors.toList());
				//set Uncategorized if needed
				selectedAnnotations.stream().forEach(a -> a.setCategory(a.getCategory() == null ? Variant.CATEGORY_UNCATEGORIZED : a.getCategory()));
				long tiersFound = selectedAnnotations.stream().filter(a -> a.getTier() != null).count();
				long moreThanTherapy = selectedAnnotations.stream().filter(a -> !a.getCategory().equals(CAT_THERAPY)).count();
				if (tiersFound == 0 || moreThanTherapy == 0) { //at least one annotation should have a tier
					annotationsAreValid = false;
				}
				else {
					annotationsPerFTL.put(vDetails, selectedAnnotations);
					report.getFtlIds().add(v.getMongoDBId().getOid());
				}
			}
			else {
				annotationsAreValid = false;
			}
			if (!annotationsAreValid) { //report annotations missing a tier or variant without selected annotations
				List<Translocation> missingAnnotationFTLs = report.getMissingTierFTLs();
				missingAnnotationFTLs.add(vDetails);
				report.setMissingTierFTLs(missingAnnotationFTLs);
			}
		}
		return annotationsPerFTL;
	}

	/**
	 * Fetches all trials from MDA and all selected annotations
	 * @param allAnnotations all valid annotations (selected and with at least one tiered annotation)
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws UnsupportedOperationException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	private List<BiomarkerTrialsRow> getTrials(List<Annotation> allAnnotations) throws JsonParseException, JsonMappingException,
			UnsupportedOperationException, URISyntaxException, IOException {
		List<BiomarkerTrialsRow> trials = null;
		MDAReportTemplate mdaEmail = utils.getMDATrials(caseId);
		if (mdaEmail != null) {
			trials = mdaEmail.getSelectedBiomarkers();
			if (trials != null) {
				if (mdaEmail.getSelectedAdditionalBiomarkers() != null)
					trials.addAll(mdaEmail.getSelectedAdditionalBiomarkers());
				if (mdaEmail.getRelevantBiomarkers() != null)
					trials.addAll(mdaEmail.getRelevantBiomarkers());
				if (mdaEmail.getRelevantAdditionalBiomarkers() != null)
					trials.addAll(mdaEmail.getRelevantAdditionalBiomarkers());

			} else {
				trials = new ArrayList<BiomarkerTrialsRow>();
			}
		}

		for (Annotation a : allAnnotations) {
			if (isStringEqual(a.getCategory(), CAT_CLINICAL_TRIAL))
				trials.add(new BiomarkerTrialsRow(a.getTrial()));
		}
		return trials;
	}
	

	/**
	 * Finds all Therapy cards in SNP, CNV and FTL
	 * @param annotationsPerSNP
	 * @param annotationsPerCNV
	 * @param annotationsPerFTL
	 * @return
	 */
	private List<IndicatedTherapy> getIndicatedTherapies(Map<VariantReport, List<Annotation>> annotationsPerSNP,
			Map<CNVReportWithHighestTier, List<Annotation>> annotationsPerCNV, 
			Map<Translocation, List<Annotation>> annotationsPerFTL ) {
		List<IndicatedTherapy> indicatedTherapies = new ArrayList<IndicatedTherapy>();
		for (VariantReport v : annotationsPerSNP.keySet()) {
			List<IndicatedTherapy> therapyCards = annotationsPerSNP.get(v).stream().filter(a -> annotationGoesInTherapyTable(a)).map(a -> new IndicatedTherapy(a, v.getVariant())).collect(Collectors.toList());
			indicatedTherapies.addAll(therapyCards);
		}
		for (CNVReportWithHighestTier v : annotationsPerCNV.keySet()) {
			List<IndicatedTherapy> therapyCards = annotationsPerCNV.get(v).stream().filter(a -> annotationGoesInTherapyTable(a)).map(a -> new IndicatedTherapy(a, v.getCnv())).collect(Collectors.toList());
			indicatedTherapies.addAll(therapyCards);
		}
		for (Translocation v : annotationsPerFTL.keySet()) {
			List<IndicatedTherapy> therapyCards = annotationsPerFTL.get(v).stream().filter(a -> annotationGoesInTherapyTable(a)).map(a -> new IndicatedTherapy(a, v)).collect(Collectors.toList());
			indicatedTherapies.addAll(therapyCards);
		}
		
		return indicatedTherapies;
	}
	

	
	/**
	 * Collects pubmed ids from all annotations
	 * and fetches the details from NCBI
	 * to populate title, authors etc.
	 * @param allAnnotations all valid annotations (selected and with at least one tiered annotation)
	 * @return
	 * @throws ClientProtocolException
	 * @throws UnsupportedOperationException
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	private List<PubMed> getPubmedReferences(List<Annotation> allAnnotations) throws ClientProtocolException, UnsupportedOperationException, URISyntaxException, IOException, JAXBException, SAXException, ParserConfigurationException {
		Set<String> pmIds = new HashSet<String>();
		for (Annotation a : allAnnotations) {
			if (!isEmptyList(a.getPmids())) {
				pmIds.addAll(this.trimPmIds(a.getPmids()));
			}
		}
		//convert pmids to PubMed objects
		NCBIRequestUtils utils = new NCBIRequestUtils(ncbiProps, otherProps);
		List<PubMed> pubmeds = utils.getPubmedDetails(pmIds);
		return pubmeds;
	}
	
	
	private List<TranslocationReport> getFTLs(Map<Translocation, List<Annotation>> annotationsPerFTL) {
		List<TranslocationReport> ftls = new ArrayList<TranslocationReport>();
		for (Translocation ftl : annotationsPerFTL.keySet()) {
			List<TranslocationReport> ftlCards = annotationsPerFTL.get(ftl).stream().filter(a -> annotationGoesInFTLTable(a)).map(a -> new TranslocationReport(a.getText(), ftl)).collect(Collectors.toList());
			ftls.addAll(ftlCards);
		}
		return ftls;
	}
	
	private List<CNVReport> getCNVs(Map<CNVReportWithHighestTier, List<Annotation>> annotationsPerCNV) {
		List<CNVReport> cnvs = new ArrayList<CNVReport>();
		for (CNVReportWithHighestTier cnv : annotationsPerCNV.keySet()) {
			List<CNVReport> cnvCards = annotationsPerCNV.get(cnv).stream().filter(a -> annotationGoesInCNVTable(a)).map(a -> new CNVReport(a, cnv.getCnv(), cnv.getHighestAnnotationTier(), cnv.getBreadth())).collect(Collectors.toList());
			cnvs.addAll(cnvCards);
		}
		return cnvs;
	}


	private boolean isTrue(Boolean b) {
		return b != null && b;
	}

	private boolean isEmptyList(List<?> list) {
		return list == null || list.isEmpty();
	}

	private boolean isStringEqual(String s1, String s2) {
		return s1 != null && s2 != null && s1.equals(s2);
	}
	
	private List<String> trimPmIds(List<String> pmIds) {
		return pmIds.stream().map(p -> p.trim()).collect(Collectors.toList());
	}
	
	/**
	 * Checks if an annotation should go into the Indicated Therapy table
	 * It should be selected
	 * Have a category of Therapy
	 * and not be more than 2C tier
	 * @param a
	 * @return true if the annotation should go in the table
	 */
	private boolean annotationGoesInTherapyTable(Annotation a) {
		return isStringEqual(a.getCategory(), CAT_THERAPY)
				&& a.getTier() != null && THERAPY_TIERS.contains(a.getTier());
	}
	
	/**
	 * @param a
	 * @return
	 */
//	private boolean annotationGoesInCNVTable(Annotation a) {
//		return (!isStringEqual(a.getCategory(), CAT_THERAPY) && !isStringEqual(a.getCategory(), CAT_CLINICAL_TRIAL))
//				&& ((a.getBreadth().equals("Chromosomal") || ((a.getBreadth().equals("Focal") && a.getTier() != null
//						&& UNKNOWN_TIERS.contains(a.getTier())) || a.getTier() == null)));
//	}
	private boolean annotationGoesInCNVTable(Annotation a) {
		boolean isChrom = a.getBreadth().equals(CNV.BREADTH_CHROM);
		boolean notTherapyOrTrial = !isStringEqual(a.getCategory(), CAT_THERAPY) && !isStringEqual(a.getCategory(), CAT_CLINICAL_TRIAL);
		boolean focalTier1To3OrNull = a.getBreadth().equals("Focal") && (a.getTier() == null || !EXCLUDE_TIERS.contains(a.getTier()));
		return notTherapyOrTrial && (isChrom || focalTier1To3OrNull);
	}
	
	/**
	 * @param a
	 * @return
	 */
	private boolean annotationGoesInFTLTable(Annotation a) {
		return !isStringEqual(a.getCategory(), CAT_THERAPY)
				&& !isStringEqual(a.getCategory(), CAT_CLINICAL_TRIAL);
	}
	
	/**
	 * @param a
	 * @return
	 */
	private boolean annotationGoesInClinicalSignificanceTable(Annotation a) {
		return !isStringEqual(a.getCategory(), CAT_THERAPY)
				&& !isStringEqual(a.getCategory(), CAT_CLINICAL_TRIAL);
	}

	public ModelDAO getModelDAO() {
		return modelDAO;
	}

	public String getCaseId() {
		return caseId;
	}

	public User getUser() {
		return user;
	}

	public OtherProperties getOtherProps() {
		return otherProps;
	}

	public NCBIProperties getNcbiProps() {
		return ncbiProps;
	}

	public RequestUtils getUtils() {
		return utils;
	}

	public Report getReport() {
		return report;
	}

	public OrderCase getCaseDetails() {
		return caseDetails;
	}
}
