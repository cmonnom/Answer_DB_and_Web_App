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
import utsw.bicf.answer.reporting.parse.BiomarkerTrialsRow;
import utsw.bicf.answer.reporting.parse.MDAReportTemplate;
import utsw.bicf.answer.security.NCBIProperties;
import utsw.bicf.answer.security.OtherProperties;

public class ReportBuilder {

	private static List<String> STRONG_TIERS = Arrays.asList("1A", "1B");
	private static List<String> POSSIBLE_TIERS = Arrays.asList("2C", "2D");
	private static List<String> UNKNOWN_TIERS = Arrays.asList("3");
	private static List<String> TIER1_CLASSIFICATIONS = Arrays.asList(Variant.CATEGORY_LIKELY_PATHOGENIC,
			Variant.CATEGORY_PATHOGENIC);

	private static String CAT_CLINICAL_TRIAL = "Clinical Trial";
	private static String CAT_THERAPY = "Therapy";
	private static String TIER_2D = "2D";
	

	ModelDAO modelDAO;
	String caseId;
	User user;
	OtherProperties otherProps;
	NCBIProperties ncbiProps;
	RequestUtils utils;
	Report report = new Report();
	OrderCase caseDetails;
	List<Variant> snps = new ArrayList<Variant>();
	List<CNV> cnvs = new ArrayList<CNV>();
	List<Translocation> ftls = new ArrayList<Translocation>();
	List<Annotation> snpAnnotations = new ArrayList<Annotation>();
	List<Annotation> cnvAnnotations = new ArrayList<Annotation>();
	List<Annotation> ftlAnnotations = new ArrayList<Annotation>();
	List<Annotation> allAnnotations = new ArrayList<Annotation>();
	Map<Variant, List<Annotation>> annotationsPerSNP = new HashMap<Variant, List<Annotation>>();
	Map<CNV, List<Annotation>> annotationsPerCNV = new HashMap<CNV, List<Annotation>>();
	Map<Translocation, List<Annotation>> annotationsPerFTL = new HashMap<Translocation, List<Annotation>>();
	List<Variant> missingTierVariants = new ArrayList<Variant>();
	List<CNV> missingTierCNVs = new ArrayList<CNV>();

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
		snps = new ArrayList<Variant>();
		cnvs = new ArrayList<CNV>();
		ftls = new ArrayList<Translocation>();
		annotationsPerSNP = new HashMap<Variant, List<Annotation>>();
		annotationsPerCNV = new HashMap<CNV, List<Annotation>>();
		annotationsPerFTL = new HashMap<Translocation, List<Annotation>>();
		snpAnnotations = new ArrayList<Annotation>();
		cnvAnnotations = new ArrayList<Annotation>();
		ftlAnnotations = new ArrayList<Annotation>();
		allAnnotations = new ArrayList<Annotation>();
	}

	public Report build() throws ClientProtocolException, IOException, URISyntaxException, UnsupportedOperationException, JAXBException, SAXException, ParserConfigurationException {
		caseDetails = utils.getCaseDetails(caseId, null);
		report.setCaseId(caseDetails.getCaseId());
		report.setCaseName(caseDetails.getCaseName());
		report.setLabTestName(caseDetails.getLabTestName());
		PatientInfo patientInfo = new PatientInfo(caseDetails);
		report.setPatientInfo(patientInfo);
		report.setReportName(caseDetails.getCaseName());

		this.fetchDetails();
		//Indicated Therapies
		report.setIndicatedTherapies(this.getIndicatedTherapies());
		//Clinical Trials
		report.setClinicalTrials(this.getTrials());
		List<Map<String, GeneVariantAndAnnotation>> clinicalSignificances = this.getClinicalSignificances();
		//Strong Clinical Significance
		report.setSnpVariantsStrongClinicalSignificance(clinicalSignificances.get(0));
		//Possible Clinical Significance
		report.setSnpVariantsPossibleClinicalSignificance(clinicalSignificances.get(1));
		//Unknown Clinical Significance
		report.setSnpVariantsUnknownClinicalSignificance(clinicalSignificances.get(2));
		
		//CNV
		report.setCnvs(this.getCNVs());
		//FTL
		report.setTranslocations(this.getFTLs());
		//Pubmed
		report.setPubmeds(this.getPubmedReferences());
		
		//Missing variants
		//TODO I could take the diff of variants in each map versus selected variants in the case
		//if the map is missing variants, it's probably because there's no annotation selected?
		report.setMissingTierVariants(this.getMissingTierVariants());
		report.setMissingTierCNVs(this.getMissingTierCNVs());
		
		//Summary table
		
		//TODO variant, cnv, ftl ids for Ben
		report.setSnpIds(this.getSNPIds());
		report.setCnvIds(this.getCNVIds());
		report.setFtlIds(this.getFTLIds());

		report.setModifiedBy(user.getUserId());
		report.setCreatedBy(user.getUserId());
		report.setDateCreated(OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
		report.setDateModified(OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
		
		report.setLive(true);
		
		return report;
	}

	
	private Set<String> getSNPIds() {
		return annotationsPerSNP.keySet().stream().map(v -> v.getMongoDBId().getOid()).collect(Collectors.toSet());
	}

	private Set<String> getCNVIds() {
		return annotationsPerCNV.keySet().stream().map(v -> v.getMongoDBId().getOid()).collect(Collectors.toSet());
	}

	private Set<String> getFTLIds() {
		return annotationsPerFTL.keySet().stream().map(v -> v.getMongoDBId().getOid()).collect(Collectors.toSet());
	}

	/**
	 * Fetches all trials from MDA and all selected annotations
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws UnsupportedOperationException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	private List<BiomarkerTrialsRow> getTrials() throws JsonParseException, JsonMappingException,
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
	
	private List<Map<String, GeneVariantAndAnnotation>> getClinicalSignificances() {
		Map<String, GeneVariantAndAnnotation> annotationsStrongByVariant = new HashMap<String, GeneVariantAndAnnotation>();
		Map<String, GeneVariantAndAnnotation> annotationsPossibleByVariant = new HashMap<String, GeneVariantAndAnnotation>();
		Map<String, GeneVariantAndAnnotation> annotationsUnknownByVariant = new HashMap<String, GeneVariantAndAnnotation>();
		List<Map<String, GeneVariantAndAnnotation>> clinicalSignificances = Arrays.asList(annotationsStrongByVariant, annotationsPossibleByVariant, annotationsUnknownByVariant);
		for (Variant v : annotationsPerSNP.keySet()) {
			List<String> tiers = new ArrayList<String>(); //to determine the highest tier for this variant
			for (Annotation a : annotationsPerSNP.get(v)) {
				tiers.add(a.getTier());
				if (a.getClassification() != null && TIER1_CLASSIFICATIONS.contains(a.getClassification())) {
					if (a.getClassification().equals(Variant.CATEGORY_PATHOGENIC)) {
						tiers.add("1A");
					}
					else if (a.getClassification().equals(Variant.CATEGORY_LIKELY_PATHOGENIC)) {
						tiers.add("1B");
					}
				}
			}
			Map<String, List<String>> strongAnnotations = new HashMap<String, List<String>>();
			Map<String, List<String>> possibleAnnotations = new HashMap<String, List<String>>();
			Map<String, List<String>> unknownAnnotations = new HashMap<String, List<String>>();
			String highestTierForVariant = null;
			tiers = tiers.stream().filter(t -> t != null).sorted().collect(Collectors.toList());
			if (!tiers.isEmpty()) {
				highestTierForVariant = tiers.get(0);
				if (STRONG_TIERS.contains(highestTierForVariant)) {
					for (Annotation a : annotationsPerSNP.get(v)) {
						String category = a.getCategory() == null ? Variant.CATEGORY_UNCATEGORIZED : a.getCategory();
						List<String> annotations = strongAnnotations.get(category);
						if (annotations == null) {
							annotations = new ArrayList<String>();
						}
						annotations.add(a.getText());
						strongAnnotations.put(category, annotations);
					}
				}
				else if (POSSIBLE_TIERS.contains(highestTierForVariant)) {
					for (Annotation a : annotationsPerSNP.get(v)) {
						String category = a.getCategory() == null ? Variant.CATEGORY_UNCATEGORIZED : a.getCategory();
						List<String> annotations = possibleAnnotations.get(category);
						if (annotations == null) {
							annotations = new ArrayList<String>();
						}
						annotations.add(a.getText());
						possibleAnnotations.put(category, annotations);
					}
				}
				else if (UNKNOWN_TIERS.contains(highestTierForVariant)) {
					for (Annotation a : annotationsPerSNP.get(v)) {
						String category = a.getCategory() == null ? Variant.CATEGORY_UNCATEGORIZED : a.getCategory();
						List<String> annotations = unknownAnnotations.get(category);
						if (annotations == null) {
							annotations = new ArrayList<String>();
						}
						annotations.add(a.getText());
						unknownAnnotations.put(category, annotations);
					}
				}
				String name = v.getGeneName() + " " + v.getNotation();
				if (!strongAnnotations.isEmpty()) {
					report.getSnpIds().add(v.getMongoDBId().getOid());
					Map<String, String> strongAnnotationsConcat = new HashMap<String, String>();
					for (String cat : strongAnnotations.keySet()) {
						strongAnnotationsConcat.put(cat, strongAnnotations.get(cat).stream().collect(Collectors.joining(" ")));
					}
					annotationsStrongByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(v, strongAnnotationsConcat));
					report.incrementStrongClinicalSignificanceCount(v.getGeneName());
				}
				if (!possibleAnnotations.isEmpty()) {
					report.getSnpIds().add(v.getMongoDBId().getOid());
					Map<String, String> possibleAnnotationsConcat = new HashMap<String, String>();
					for (String cat : possibleAnnotations.keySet()) {
						possibleAnnotationsConcat.put(cat, possibleAnnotations.get(cat).stream().collect(Collectors.joining(" ")));
					}
					annotationsPossibleByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(v, possibleAnnotationsConcat));
					report.incrementPossibleClinicalSignificanceCount(v.getGeneName());
				}
				if (!unknownAnnotations.isEmpty()) {
					report.getSnpIds().add(v.getMongoDBId().getOid());
					Map<String, String> unknownAnnotationsConcat = new HashMap<String, String>();
					for (String cat : unknownAnnotations.keySet()) {
						unknownAnnotationsConcat.put(cat, unknownAnnotations.get(cat).stream().collect(Collectors.joining(" ")));
					}
					annotationsUnknownByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(v, unknownAnnotationsConcat));
//					report.incrementUnknownClinicalSignificanceCount(v.getGeneName()); //no tier 3 in navigation table anymore
				}
			}
		}
		for (CNV v : annotationsPerCNV.keySet()) {
			boolean hasTiers = false;
			Map<String, List<Annotation>> selectedAnnotationsForVariant = new HashMap<String, List<Annotation>>();
			Map<String, List<String>> tiersByGenes = new HashMap<String, List<String>>(); //to determine the highest tier for this variant
			for (Annotation a : annotationsPerCNV.get(v)) {
				if (a.getBreadth().equals("Chromosomal") && a.getTier() != null && !a.getTier().equals("")) {
					 //chromosomal annotations don't go into the same table
					// but need to be counted anyway
					hasTiers = true;
				}
				else if (a.getBreadth().equals("Focal") && !a.getCategory().equals("Therapy")) {
					String key = a.getCnvGenes().stream().collect(Collectors.joining(" "));
					if (v.getAberrationType().equals("ITD")) {
						key += "-ITD";
					}
					List<Annotation> annotations = selectedAnnotationsForVariant.get(key);
					if (annotations == null) {
						annotations = new ArrayList<Annotation>();
					}
					annotations.add(a);
					selectedAnnotationsForVariant.put(key, annotations);
					List<String> tiers = tiersByGenes.get(key);
					if (tiers == null) {
						tiers = new ArrayList<String>();
					}
					tiers.add(a.getTier());
					if (a.getClassification() != null && TIER1_CLASSIFICATIONS.contains(a.getClassification())) {
						if (a.getClassification().equals(Variant.CATEGORY_PATHOGENIC)) {
							tiers.add("1A");
						}
						else if (a.getClassification().equals(Variant.CATEGORY_LIKELY_PATHOGENIC)) {
							tiers.add("1B");
						}
					}
					tiersByGenes.put(key, tiers);
				}
			}
			for (String genes : selectedAnnotationsForVariant.keySet()) {
				List<Annotation> annotations = selectedAnnotationsForVariant.get(genes);
				List<String> tiers = tiersByGenes.get(genes);
				Map<String, List<String>> strongAnnotations = new HashMap<String, List<String>>();
				Map<String, List<String>> possibleAnnotations = new HashMap<String, List<String>>();
//				Map<String, List<String>> unknownAnnotations = new HashMap<String, List<String>>();
				String highestTierForVariant = null;
				tiers = tiers.stream().filter(t -> t != null).sorted().collect(Collectors.toList());
				if (!tiers.isEmpty() || hasTiers) { //TODO test this
					hasTiers = true;
					highestTierForVariant = tiers.get(0);
					if (STRONG_TIERS.contains(highestTierForVariant)) {
						for (Annotation a : annotations) {
							String category = a.getCategory() == null ? Variant.CATEGORY_UNCATEGORIZED : a.getCategory();
							List<String> annotationsFormatted = strongAnnotations.get(category);
							if (annotationsFormatted == null) {
								annotationsFormatted = new ArrayList<String>();
							}
							annotationsFormatted.add(a.getText());
							strongAnnotations.put(category, annotationsFormatted);
						}
					}
					else if (POSSIBLE_TIERS.contains(highestTierForVariant)) {
						for (Annotation a : annotations) {
							String category = a.getCategory() == null ? Variant.CATEGORY_UNCATEGORIZED : a.getCategory();
							List<String> annotationsFormatted = possibleAnnotations.get(category);
							if (annotationsFormatted == null) {
								annotationsFormatted = new ArrayList<String>();
							}
							annotationsFormatted.add(a.getText());
							possibleAnnotations.put(category, annotationsFormatted);
						}
					}
				}
				String name = genes;
				if (!strongAnnotations.isEmpty()) {
					report.getCnvIds().add(v.getMongoDBId().getOid());
					Map<String, String> strongAnnotationsConcat = new HashMap<String, String>();
					for (String cat : strongAnnotations.keySet()) {
						strongAnnotationsConcat.put(cat, strongAnnotations.get(cat).stream().collect(Collectors.joining(" ")));
					}
					annotationsStrongByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(v, name, strongAnnotationsConcat));
					report.incrementStrongClinicalSignificanceCount(name);
				}
				if (!possibleAnnotations.isEmpty()) {
					report.getCnvIds().add(v.getMongoDBId().getOid());
					Map<String, String> possibleAnnotationsConcat = new HashMap<String, String>();
					for (String cat : possibleAnnotations.keySet()) {
						possibleAnnotationsConcat.put(cat, possibleAnnotations.get(cat).stream().collect(Collectors.joining(" ")));
					}
					annotationsPossibleByVariant.put(name.replaceAll("\\.", ""), new GeneVariantAndAnnotation(v, name, possibleAnnotationsConcat));
					report.incrementPossibleClinicalSignificanceCount(name);
				}
			}
		}
		return clinicalSignificances;
	}

	
	private List<CNVReport> getCNVs() {
		List<CNVReport> cnvReports = new ArrayList<CNVReport>();
		for (CNV cnv : annotationsPerCNV.keySet()) {
			StringBuilder sb = new StringBuilder();
			for (Annotation a : annotationsPerCNV.get(cnv)) {
				if (annotationGoesInCNVTable(a)) {
					sb.append(a.getText()).append(" ");
				}
			}
			cnvReports.add(new CNVReport(sb.toString(), cnv));
		}
		return cnvReports;
	}
	
	private List<TranslocationReport> getFTLs() {
		List<TranslocationReport> translocationReports = new ArrayList<TranslocationReport>();
		for (Translocation t : annotationsPerFTL.keySet()) {
			StringBuilder sb = new StringBuilder();
			for (Annotation a : annotationsPerFTL.get(t)) {
				if (annotationGoesInFTLTable(a)) {
					sb.append(a.getText()).append(" ");
				}
			}
			translocationReports.add(new TranslocationReport(sb.toString(), t));
		}
		return null;
	}

	
	/**
	 * Collects pubmed ids from all annotations
	 * and fetches the details from NCBI
	 * to populate title, authors etc.
	 * @return
	 * @throws ClientProtocolException
	 * @throws UnsupportedOperationException
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws JAXBException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	private List<PubMed> getPubmedReferences() throws ClientProtocolException, UnsupportedOperationException, URISyntaxException, IOException, JAXBException, SAXException, ParserConfigurationException {
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
	
	private List<IndicatedTherapy> getIndicatedTherapies() {
		List<IndicatedTherapy> indicatedTherapies = new ArrayList<IndicatedTherapy>();
		for (Variant v : annotationsPerSNP.keySet()) {
			for (Annotation a : annotationsPerSNP.get(v)) {
				if (this.annotationGoesInTherapyTable(a)) {
					indicatedTherapies.add(new IndicatedTherapy(a, v));
				}
			}
		}
		for (CNV v : annotationsPerCNV.keySet()) {
			for (Annotation a : annotationsPerCNV.get(v)) {
				if (this.annotationGoesInTherapyTable(a)) {
					indicatedTherapies.add(new IndicatedTherapy(a, v));
				}
			}
		}
		for (Translocation v : annotationsPerFTL.keySet()) {
			for (Annotation a : annotationsPerFTL.get(v)) {
				if (this.annotationGoesInTherapyTable(a)) {
					indicatedTherapies.add(new IndicatedTherapy(a, v));
				}
			}
		}
		return indicatedTherapies;
	}
	
	/**
	 * Some objects like variants and annotations need to be populated with
	 * additional data before being used properly.
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private void fetchDetails() throws ClientProtocolException, IOException, URISyntaxException {
		init();
		// SNP
		for (Variant v : caseDetails.getVariants()) {
			if (isTrue(v.getSelected())) {
				v = utils.getVariantDetails(v.getMongoDBId().getOid());
				snps.add(v);
				fetchSNPAnnotations(v);
			}
		}
		// CNV
		for (CNV v : caseDetails.getCnvs()) {
			if (isTrue(v.getSelected())) {
				v = utils.getCNVDetails(v.getMongoDBId().getOid());
				cnvs.add(v);
				fetchCNVAnnotations(v);
			}
		}
		// FTL
		for (Translocation v : caseDetails.getTranslocations()) {
			if (isTrue(v.getSelected())) {
				v = utils.getTranslocationDetails(v.getMongoDBId().getOid());
				ftls.add(v);
				fetchFTLAnnotations(v);
			}
		}

		allAnnotations.addAll(snpAnnotations);
		allAnnotations.addAll(cnvAnnotations);
		allAnnotations.addAll(ftlAnnotations);
	}

	private void fetchSNPAnnotations(Variant v) {
		if (v.getReferenceVariant() != null && !isEmptyList(v.getReferenceVariant().getUtswAnnotations())) {
			for (Annotation a : v.getReferenceVariant().getUtswAnnotations()) {
				Annotation.init(a, v.getAnnotationIdsForReporting(), modelDAO);
				if (isTrue(a.getIsSelected())) {
					snpAnnotations.add(a);
					List<Annotation> annotations = annotationsPerSNP.get(v);
					if (annotations == null) {
						annotations = new ArrayList<Annotation>();
					}
					annotations.add(a);
					annotationsPerSNP.put(v, annotations);
				}
			}
		}
	}

	private void fetchCNVAnnotations(CNV v) {
		if (v.getReferenceCnv() != null && !isEmptyList(v.getReferenceCnv().getUtswAnnotations())) {
			for (Annotation a : v.getReferenceCnv().getUtswAnnotations()) {
				Annotation.init(a, v.getAnnotationIdsForReporting(), modelDAO);
				if (isTrue(a.getIsSelected())) {
					cnvAnnotations.add(a);
					List<Annotation> annotations = annotationsPerCNV.get(v);
					if (annotations == null) {
						annotations = new ArrayList<Annotation>();
					}
					annotations.add(a);
					annotationsPerCNV.put(v, annotations);
				}
			}
		}
	}

	private void fetchFTLAnnotations(Translocation v) {
		if (v.getReferenceTranslocation() != null && !isEmptyList(v.getReferenceTranslocation().getUtswAnnotations())) {
			for (Annotation a : v.getReferenceTranslocation().getUtswAnnotations()) {
				Annotation.init(a, v.getAnnotationIdsForReporting(), modelDAO);
				if (isTrue(a.getIsSelected())) {
					ftlAnnotations.add(a);
					List<Annotation> annotations = annotationsPerFTL.get(v);
					if (annotations == null) {
						annotations = new ArrayList<Annotation>();
					}
					annotations.add(a);
					annotationsPerFTL.put(v, annotations);
				}
			}
		}
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
	 * and not be a 2D tier
	 * @param a
	 * @return true if the annotation should go in the table
	 */
	private boolean annotationGoesInTherapyTable(Annotation a) {
		return isStringEqual(a.getCategory(), CAT_THERAPY)
				&& !isStringEqual(a.getTier(), TIER_2D);
	}
	
	private boolean annotationGoesInCNVTable(Annotation a) {
		return !isStringEqual(a.getCategory(), CAT_THERAPY)
				&& (a.getBreadth().equals("Chromosomal")
				|| ((a.getBreadth().equals("Focal") && a.getTier() != null && UNKNOWN_TIERS.contains(a.getTier()))
						|| a.getTier() == null));
	}
	
	private boolean annotationGoesInFTLTable(Annotation a) {
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

	public List<Variant> getSnps() {
		return snps;
	}

	public List<CNV> getCnvs() {
		return cnvs;
	}

	public List<Translocation> getFtls() {
		return ftls;
	}

	public List<Annotation> getSnpAnnotations() {
		return snpAnnotations;
	}

	public List<Annotation> getCnvAnnotations() {
		return cnvAnnotations;
	}

	public List<Annotation> getFtlAnnotations() {
		return ftlAnnotations;
	}

	public List<Annotation> getAllAnnotations() {
		return allAnnotations;
	}

	public Map<Variant, List<Annotation>> getAnnotationsPerSNP() {
		return annotationsPerSNP;
	}

	public Map<CNV, List<Annotation>> getAnnotationsPerCNV() {
		return annotationsPerCNV;
	}

	public Map<Translocation, List<Annotation>> getAnnotationsPerFTL() {
		return annotationsPerFTL;
	}

	public List<Variant> getMissingTierVariants() {
		return missingTierVariants;
	}

	public List<CNV> getMissingTierCNVs() {
		return missingTierCNVs;
	}
}
