package utsw.bicf.answer.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.pdfbox.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.Button;
import utsw.bicf.answer.controller.serialization.DataFilterList;
import utsw.bicf.answer.controller.serialization.DataTableFilter;
import utsw.bicf.answer.controller.serialization.SearchItem;
import utsw.bicf.answer.controller.serialization.SearchItemString;
import utsw.bicf.answer.controller.serialization.Utils;
import utsw.bicf.answer.controller.serialization.vuetify.CNVChromosomeItems;
import utsw.bicf.answer.controller.serialization.vuetify.GenesInPanelItems;
import utsw.bicf.answer.controller.serialization.vuetify.OpenCaseSummary;
import utsw.bicf.answer.controller.serialization.vuetify.Summary;
import utsw.bicf.answer.controller.serialization.vuetify.VariantDetailsSummary;
import utsw.bicf.answer.controller.serialization.vuetify.VariantFilterItems;
import utsw.bicf.answer.controller.serialization.vuetify.VariantFilterListItems;
import utsw.bicf.answer.controller.serialization.vuetify.VariantFilterListSaved;
import utsw.bicf.answer.controller.serialization.vuetify.VariantRelatedSummary;
import utsw.bicf.answer.controller.serialization.vuetify.VariantVcfAnnotationSummary;
import utsw.bicf.answer.controller.serialization.zingchart.CNVChartData;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.FilterStringValue;
import utsw.bicf.answer.model.HeaderConfig;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.ReportGroup;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.VariantFilter;
import utsw.bicf.answer.model.VariantFilterList;
import utsw.bicf.answer.model.extmapping.Annotation;
import utsw.bicf.answer.model.extmapping.CNV;
import utsw.bicf.answer.model.extmapping.CNVPlotData;
import utsw.bicf.answer.model.extmapping.CaseAnnotation;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Translocation;
import utsw.bicf.answer.model.extmapping.Trial;
import utsw.bicf.answer.model.extmapping.VCFAnnotation;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.HeaderConfigData;
import utsw.bicf.answer.model.hybrid.HeaderOrder;
import utsw.bicf.answer.model.hybrid.PatientInfo;
import utsw.bicf.answer.model.hybrid.ReportGroupForDisplay;
import utsw.bicf.answer.model.hybrid.VCFAnnotationRow;
import utsw.bicf.answer.reporting.parse.ExportSelectedVariants;
import utsw.bicf.answer.security.EmailProperties;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.NotificationUtils;
import utsw.bicf.answer.security.OtherProperties;
import utsw.bicf.answer.security.PermissionUtils;
import utsw.bicf.answer.security.QcAPIAuthentication;

@Controller
@RequestMapping("/")
public class OpenCaseController {

	static {
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".openCase",
				IndividualPermission.CAN_ANNOTATE);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".openCaseReadOnly",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".getCaseDetails",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".getVariantFilters",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".getVariantDetails",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".saveVariantSelection",
				IndividualPermission.CAN_SELECT);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".commitAnnotations",
				IndividualPermission.CAN_ANNOTATE);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".saveCurrentFilters",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".loadUserFilterSets",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".deleteFilterSet",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".exportSelection",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".saveCaseAnnotations",
				IndividualPermission.CAN_ANNOTATE);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".getCNVDetails",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".getTranslocationDetails",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".loadCaseAnnotations",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".saveVariant",
				IndividualPermission.CAN_ANNOTATE);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".saveSelectedAnnotationsForVariant",
				IndividualPermission.CAN_ANNOTATE);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".sendToMDA",
				IndividualPermission.CAN_SELECT);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".getPatientDetails",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".savePatientDetails",
				IndividualPermission.CAN_ANNOTATE);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".readyForReview",
				IndividualPermission.CAN_ANNOTATE);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".readyForReport",
				IndividualPermission.CAN_REVIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".getCNVChartData",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".verifyGeneNames",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".getCNVChromList",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".getGenesInPanel",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".saveCNV",
				IndividualPermission.CAN_ANNOTATE);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".fetchNCTData",
				IndividualPermission.CAN_ANNOTATE);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".setDefaultTranscript",
				IndividualPermission.CAN_ANNOTATE);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".saveHeaderConfig",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".deleteHeaderConfig",
				IndividualPermission.CAN_VIEW);
		
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;
	@Autowired
	QcAPIAuthentication qcAPI;
	@Autowired
	EmailProperties emailProps;
	@Autowired
	FileProperties fileProps;
	@Autowired
	OtherProperties otherProps;

	@RequestMapping("/openCase/{caseId}")
	public String openCase(Model model, HttpSession session, @PathVariable String caseId,
			@RequestParam(defaultValue="", required=false) String variantId,
			@RequestParam(defaultValue="", required=false) String variantType,
			@RequestParam(defaultValue="false", required=false) Boolean showReview,
			@RequestParam(defaultValue="", required=false) String edit) throws IOException, UnsupportedOperationException, URISyntaxException {
		String url = "openCase/" + caseId + "?showReview=" + showReview
				+ "%26variantId=" + variantId + "%26variantType=" + variantType
				+ "%26edit=" + edit;
		User user = (User) session.getAttribute("user");
		model.addAttribute("urlRedirect", url);
		model.addAttribute("isProduction", fileProps.getProductionEnv());
		model.addAttribute("oncoKBGeniePortalUrl", otherProps.getOncoKBGeniePortalUrl());
		RequestUtils utils = new RequestUtils(modelDAO);
		if (user != null && !ControllerUtil.isUserAssignedToCase(utils, caseId, user)) {
			return ControllerUtil.initializeModelNotAllowed(model, servletContext);
		}
		
		return ControllerUtil.initializeModel(model, servletContext, user);
	}
	
	@RequestMapping("/openCaseReadOnly/{caseId}")
	public String openCaseReadOnly(Model model, HttpSession session, @PathVariable String caseId,
			@RequestParam(defaultValue="", required=false) String variantId,
			@RequestParam(defaultValue="", required=false) String variantType,
			@RequestParam(defaultValue="false", required=false) Boolean showReview,
			@RequestParam(defaultValue="", required=false) String edit) throws IOException, UnsupportedOperationException, URISyntaxException {
		String url = "openCaseReadOnly/" + caseId + "?showReview=" + showReview
				+ "%26variantId=" + variantId + "%26variantType=" + variantType
				+ "%26edit=" + edit;
		User user = (User) session.getAttribute("user");
		model.addAttribute("urlRedirect", url);
		model.addAttribute("isProduction", fileProps.getProductionEnv());
		model.addAttribute("oncoKBGeniePortalUrl", otherProps.getOncoKBGeniePortalUrl());
		return ControllerUtil.initializeModel(model, servletContext, user);
	}
	
	

	@RequestMapping(value = "/getCaseDetails", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getCaseDetails(Model model, HttpSession session, @RequestParam String caseId,
			@RequestBody String filters) throws Exception {

		User user = (User) session.getAttribute("user"); // to verify that the user is assigned to the case
		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		OrderCase[] cases = utils.getActiveCases();
		OrderCase detailedCase = null;
		if (cases != null) {
			for (OrderCase c : cases) {
				if (c.getCaseId().equals(caseId)) {
					detailedCase = utils.getCaseDetails(caseId, filters);
					break; // found that the case exists
				}
			}
		}
		if (detailedCase == null) { // the case does not exist
			AjaxResponse response = new AjaxResponse();
			response.setIsAllowed(false);
			response.setSuccess(false);
			response.setMessage(caseId + " does not exist.");
			return response.createObjectJSON();
		}
		List<ReportGroup> reportGroups = modelDAO.getAllReportGroups();
//		reportGroups.stream().forEach(r -> r.populateGenesToReport(modelDAO));
		List<ReportGroupForDisplay> reportGroupsForDisplay = reportGroups.stream()
				.map(r -> new ReportGroupForDisplay(r))
				.sorted()
				.collect(Collectors.toList());
		
		OpenCaseSummary summary = new OpenCaseSummary(modelDAO, qcAPI, detailedCase, "oid", user,
				reportGroupsForDisplay);
		return summary.createVuetifyObjectJSON();

	}

	@RequestMapping(value = "/loadCaseAnnotations", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String loadCaseAnnotations(Model model, HttpSession session, @RequestParam String caseId) throws Exception {

//		User user = (User) session.getAttribute("user"); // to verify that the user is assigned to the case
		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		CaseAnnotation annotation = utils.getCaseAnnotation(caseId);
		if (annotation != null && annotation.getCaseId() != null) {
//			if (!annotation.getAssignedTo().contains(user.getUserId().toString())) {
//				AjaxResponse response = new AjaxResponse();
//				response.setIsAllowed(false);
//				response.setSuccess(false);
//				response.setMessage(user.getFullName() + " is not assigned to this case");
//				return response.createObjectJSON();
//			}
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(annotation);
		}
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(false);
		response.setSuccess(false);
		response.setMessage("No annotation for case: " + caseId);
		return response.createObjectJSON();

	}

	@RequestMapping(value = "/saveCaseAnnotations", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String saveCaseAnnotations(Model model, HttpSession session, @RequestBody String caseAnnotation,
			@RequestParam String caseId, @RequestParam(defaultValue="false") Boolean skipSnackBar) throws Exception {

		User user = (User) session.getAttribute("user"); // to verify that the user is assigned to the case
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(false);
		response.setSuccess(false);
		response.setSkipSnackBar(skipSnackBar);
		RequestUtils utils = new RequestUtils(modelDAO);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode annotationNodes = mapper.readTree(caseAnnotation);
		for (JsonNode annotationNode : annotationNodes.get("annotation")) {
			CaseAnnotation annotationToSave = mapper.readValue(annotationNode.toString(), CaseAnnotation.class);
			if (annotationToSave != null) {
				CaseAnnotation annotation = utils.getCaseAnnotation(caseId);
				if (annotation != null) { // annotation should never be null. Make sure there is no funny business with
											// user
											// id or oid
					if (!annotation.getAssignedTo().contains(user.getUserId().toString())) {
						response.setMessage(user.getFullName() + " is not assigned to this case");
						return response.createObjectJSON();
					}
					if (annotationToSave.getMongoDBId() != null
							&& annotation.getMongoDBId().getOid() != annotationToSave.getMongoDBId().getOid()) {
						response.setMessage("Invalid annotation");
						return response.createObjectJSON();
					}
					annotation.setCaseAnnotation(annotationToSave.getCaseAnnotation());
					utils.saveCaseAnnotation(response, annotation);
					return response.createObjectJSON();

				} else {
					response.setMessage("Could not retrieve annotation");
					return response.createObjectJSON();

				}
			}
		}
		response.setMessage("Nothing to save");
		return response.createObjectJSON();

	}

	@RequestMapping(value = "/getVariantFilters", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getVariantFilters(Model model, HttpSession session) throws Exception {
		List<DataTableFilter> filters = new ArrayList<DataTableFilter>();

		DataTableFilter chrFilter = new DataTableFilter("Chromosome", "chrom");
		chrFilter.setType("snp");
		chrFilter.setSelect(true);
		List<SearchItem> selectItems = new ArrayList<SearchItem>();
		for (int i = 1; i <= 23; i++) {
			selectItems.add(new SearchItemString("CHR" + i, "chr" + i));
		}
		selectItems.add(new SearchItemString("CHRX", "chrX"));
		selectItems.add(new SearchItemString("CHRY", "chrY"));
		filters.add(chrFilter);
		chrFilter.setSelectItems(selectItems);

		DataTableFilter geneFilter = new DataTableFilter("Gene Name(s)", Variant.FIELD_GENE_NAME);
		geneFilter.setType("snp");
		geneFilter.setString(true);
		geneFilter.setTooltip("comma separated");
		Button geneButton = new Button("spellcheck", "verifyGeneNames", "Click here to check for untargeted genes", "primary");
		geneFilter.setButton(geneButton);
		filters.add(geneFilter);

		DataTableFilter somaticFilter = new DataTableFilter("Somatic Status", "somaticStatus");
		somaticFilter.setType("snp");
		somaticFilter.setSelect(true);
		List<SearchItem> somaticSelectItems = new ArrayList<SearchItem>();
		somaticSelectItems.add(new SearchItemString("Somatic", "Somatic"));
		somaticSelectItems.add(new SearchItemString("Germline", "Germline"));
		somaticSelectItems.add(new SearchItemString("LOH", "LOH"));
		somaticSelectItems.add(new SearchItemString("Unknown", "Unknown"));
		filters.add(somaticFilter);
		somaticFilter.setSelectItems(somaticSelectItems);

		DataTableFilter passQCFilter = new DataTableFilter("Pass QC", "Fail QC", Variant.FIELD_FILTERS);
		passQCFilter.setType("snp");
		passQCFilter.setBoolean(true);
		filters.add(passQCFilter);

		DataTableFilter annotatedFilter = new DataTableFilter("Annotated", "Not Annotated", Variant.FIELD_ANNOTATIONS);
		annotatedFilter.setType("snp");
		annotatedFilter.setBoolean(true);
		filters.add(annotatedFilter);

		DataTableFilter cosmicFilter = new DataTableFilter("In COSMIC", "Not In COSMIC", Variant.FIELD_IN_COSMIC);
		cosmicFilter.setType("snp");
		cosmicFilter.setBoolean(true);
		filters.add(cosmicFilter);
		
		DataTableFilter repeatFilter = new DataTableFilter("Has Repeats", "No Repeats", Variant.FIELD_HAS_REPEATS);
		repeatFilter.setType("snp");
		repeatFilter.setBoolean(true);
		filters.add(repeatFilter);


		DataTableFilter tafFilter = new DataTableFilter("Tumor Alt %", Variant.FIELD_TUMOR_ALT_FREQUENCY);
		tafFilter.setType("snp");
		tafFilter.setNumber(true);
		filters.add(tafFilter);

		// DataTableFilter tumorDepthFilter = new DataTableFilter("Tumor Depth",
		// "tumorAltDepth");
		// tumorDepthFilter.setNumber(true);
		// filters.add(tumorDepthFilter);

		DataTableFilter tumorTotalDepthFilter = new DataTableFilter("Tumor Total Depth",
				Variant.FIELD_TUMOR_TOTAL_DEPTH);
		tumorTotalDepthFilter.setType("snp");
		tumorTotalDepthFilter.setNumber(true);
		filters.add(tumorTotalDepthFilter);

		DataTableFilter nafFilter = new DataTableFilter("Normal Alt %", Variant.FIELD_NORMAL_ALT_FREQUENCY);
		nafFilter.setType("snp");
		nafFilter.setNumber(true);
		filters.add(nafFilter);

		// DataTableFilter normalDepthFilter = new DataTableFilter("Normal Depth",
		// "normalAltDepth");
		// normalDepthFilter.setNumber(true);
		// filters.add(normalDepthFilter);

		DataTableFilter normalTotalDepthFilter = new DataTableFilter("Normal Total Depth",
				Variant.FIELD_NORMAL_TOTAL_DEPTH);
		normalTotalDepthFilter.setType("snp");
		normalTotalDepthFilter.setNumber(true);
		filters.add(normalTotalDepthFilter);

		DataTableFilter rafFilter = new DataTableFilter("Rna Alt %", Variant.FIELD_RNA_ALT_FREQUENCY);
		rafFilter.setType("snp");
		rafFilter.setNumber(true);
		filters.add(rafFilter);

		// DataTableFilter rnaDepthFilter = new DataTableFilter("RNA Depth",
		// "rnaAltDepth");
		// rnaDepthFilter.setNumber(true);
		// filters.add(rnaDepthFilter);

		DataTableFilter rnaTotalDepthFilter = new DataTableFilter("RNA Total Depth", Variant.FIELD_RNA_TOTAL_DEPTH);
		rnaTotalDepthFilter.setType("snp");
		rnaTotalDepthFilter.setNumber(true);
		filters.add(rnaTotalDepthFilter);

		DataTableFilter exacFilter = new DataTableFilter("ExAC Allele %", Variant.FIELD_EXAC_ALLELE_FREQUENCY);
		exacFilter.setType("snp");
		exacFilter.setNumber(true);
		filters.add(exacFilter);

		DataTableFilter gnomadFilter = new DataTableFilter("gnomAD Pop. Max. Allele %",
				Variant.FIELD_GNOMAD_ALLELE_FREQUENCY);
		gnomadFilter.setType("snp");
		gnomadFilter.setNumber(true);
		filters.add(gnomadFilter);

		DataTableFilter numCasesSeenFilter = new DataTableFilter("Nb. Cases Seen", Variant.FIELD_NUM_CASES_SEEN);
		numCasesSeenFilter.setType("snp");
		numCasesSeenFilter.setNumber(true);
		filters.add(numCasesSeenFilter);

		DataTableFilter effectFilterLOF = new DataTableFilter("LOF Effects (HIGH)", Variant.FIELD_EFFECTS);
		effectFilterLOF.setType("snp");
		effectFilterLOF.setCheckBox(true);
		effectFilterLOF.setCategory("HIGH");
		filters.add(effectFilterLOF);
		
		DataTableFilter effectFilterCoding = new DataTableFilter("Coding Change Effects (MODERATE)", Variant.FIELD_EFFECTS);
		effectFilterCoding.setType("snp");
		effectFilterCoding.setCheckBox(true);
		effectFilterCoding.setCategory("MODERATE");
		filters.add(effectFilterCoding);
		
		DataTableFilter effectFilterOther = new DataTableFilter("Other Coding Effects (LOW)", Variant.FIELD_EFFECTS);
		effectFilterOther.setType("snp");
		effectFilterOther.setCheckBox(true);
		effectFilterOther.setCategory("LOW");
		filters.add(effectFilterOther);
		
		DataTableFilter effectFilterNonCoding = new DataTableFilter("Non Coding Effects (MODIFIER)", Variant.FIELD_EFFECTS);
		effectFilterNonCoding.setType("snp");
		effectFilterNonCoding.setCheckBox(true);
		effectFilterNonCoding.setCategory("MODIFIER");
		filters.add(effectFilterNonCoding);
		
		//CNV filters

		DataTableFilter cnvGeneFilter = new DataTableFilter("Gene Name(s)", Variant.FIELD_CNV_GENE_NAME);
		cnvGeneFilter.setType("cnv");
		cnvGeneFilter.setString(true);
		cnvGeneFilter.setTooltip("comma separated");
		Button cnvGeneButton = new Button("spellcheck", "verifyCNVGeneNames", "Click here to remove genes not in our panels", "primary");
		cnvGeneFilter.setButton(cnvGeneButton);
		filters.add(cnvGeneFilter);
		
		DataTableFilter cnFilter = new DataTableFilter("Copy Number", Variant.FIELD_CNV_COPY_NUMBER);
		cnFilter.setType("cnv");
		cnFilter.setReverseNumber(true);
		filters.add(cnFilter);
		

		VariantFilterItems items = new VariantFilterItems();
		items.setFilters(filters);
		return items.createVuetifyObjectJSON();

	}

	@RequestMapping(value = "/getVariantDetails", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getVariantDetails(Model model, HttpSession session, @RequestParam String variantId) throws Exception {

		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		Variant variantDetails = utils.getVariantDetails(variantId);
		VariantRelatedSummary summaryRelated = null;
		VariantVcfAnnotationSummary summaryCanonical = null;
		VariantVcfAnnotationSummary summaryOthers = null;
		VariantDetailsSummary summary = null;
		// sort annotations with the most recent first
		Comparator<Annotation> annotationComparator = new Comparator<Annotation>() {
			@Override
			public int compare(Annotation o1, Annotation o2) {
				return o2.getModifiedLocalDateTime().compareTo(o1.getModifiedLocalDateTime());
			}
		};
		if (variantDetails != null) {
			User user = (User) session.getAttribute("user");
			
			// populate user info to be used by the UI
			if (variantDetails.getReferenceVariant() != null
					&& variantDetails.getReferenceVariant().getUtswAnnotations() != null) {
				for (Annotation a : variantDetails.getReferenceVariant().getUtswAnnotations()) {
					Annotation.init(a, variantDetails.getAnnotationIdsForReporting(), modelDAO); // format dates and add missing info
				}
				// Sort annotation by last modified
				variantDetails.getReferenceVariant().setUtswAnnotations(variantDetails.getReferenceVariant()
						.getUtswAnnotations().stream().sorted(annotationComparator).collect(Collectors.toList()));
			}
			if (variantDetails.getRelatedVariants() != null && !variantDetails.getRelatedVariants().isEmpty()) {
				List<HeaderOrder> headerOrders = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, "Related Variants");
				summaryRelated = new VariantRelatedSummary(variantDetails.getRelatedVariants(), "chromPos", headerOrders);
			}
			List<VCFAnnotation> vcfAnnotations = variantDetails.getVcfAnnotations();
			if (!vcfAnnotations.isEmpty()) {
				List<VCFAnnotationRow> canonicalAnnotation = new ArrayList<VCFAnnotationRow>();
				canonicalAnnotation.add(new VCFAnnotationRow(vcfAnnotations.get(0), false));
				List<VCFAnnotationRow> otherAnnotations = new ArrayList<VCFAnnotationRow>();
				otherAnnotations.addAll(vcfAnnotations.stream().map(a -> new VCFAnnotationRow(a, true)).collect(Collectors.toList()));
				otherAnnotations.remove(0);
				List<HeaderOrder> headerOrdersCanonical = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, OpenCaseController.class.getName() + "|" + "Canonical VCF Annotations");
				summaryCanonical = new VariantVcfAnnotationSummary(canonicalAnnotation, "proteinPosition", false, headerOrdersCanonical);
				List<HeaderOrder> headerOrdersOther = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, OpenCaseController.class.getName() + "|" + "Other VCF Annotations");
				summaryOthers = new VariantVcfAnnotationSummary(otherAnnotations, "proteinPosition", true, headerOrdersOther);
				summary = new VariantDetailsSummary(variantDetails, summaryRelated, summaryCanonical, summaryOthers);
			}
			return summary.createVuetifyObjectJSON();
		}
		return null;

	}

	@RequestMapping(value = "/getCNVDetails", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getCNVDetails(Model model, HttpSession session, @RequestParam String variantId) throws Exception {

		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		CNV variantDetails = utils.getCNVDetails(variantId);
		if (variantDetails != null) {
			for (Annotation a : variantDetails.getReferenceCnv().getUtswAnnotations()) {
				Annotation.init(a, variantDetails.getAnnotationIdsForReporting(), modelDAO); // format dates and add missing info
			}
			variantDetails.getReferenceCnv().getUtswAnnotations().sort(new Comparator<Annotation>() {
				@Override
				public int compare(Annotation o1, Annotation o2) {
					return o2.getModifiedLocalDateTime().compareTo(o1.getModifiedLocalDateTime());
				}
				
			});
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(variantDetails);
		}
		return null;

	}
	
	@RequestMapping(value = "/getCNVChromList", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getCNVChromList(Model model, HttpSession session, @RequestParam String caseId) throws Exception {

		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		Set<String> selectItems = utils.getCNVChromomosomes(caseId);
		if (selectItems != null) {
			CNVChromosomeItems items = new CNVChromosomeItems(selectItems);
			return items.createVuetifyObjectJSON();
		}
		return null;
	}

	@RequestMapping(value = "/getGenesInPanel", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getGenesInPanel(Model model, HttpSession session) throws Exception {
		List<String> genesFound = modelDAO.getAllGenesInPanels();
		GenesInPanelItems genes = new GenesInPanelItems(genesFound);
		return genes.createVuetifyObjectJSON();		
	}
	
	@RequestMapping(value = "/getTranslocationDetails", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getTranslocationDetails(Model model, HttpSession session, @RequestParam String variantId)
			throws Exception {

		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		Translocation variantDetails = utils.getTranslocationDetails(variantId);
		if (variantDetails != null) {
			for (Annotation a : variantDetails.getReferenceTranslocation().getUtswAnnotations()) {
				Annotation.init(a, variantDetails.getAnnotationIdsForReporting(), modelDAO); // format dates and add missing info
			}
			variantDetails.getReferenceTranslocation().getUtswAnnotations().sort(new Comparator<Annotation>() {
				@Override
				public int compare(Annotation o1, Annotation o2) {
					return o2.getModifiedLocalDateTime().compareTo(o1.getModifiedLocalDateTime());
				}
				
			});
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(variantDetails);
		}
		return null;

	}

	@RequestMapping(value = "/saveVariantSelection", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String saveVariantSelection(Model model, HttpSession session, @RequestBody String data,
			@RequestParam String caseId, @RequestParam(defaultValue="false") Boolean closeAfter,
			 @RequestParam(defaultValue="false") Boolean skipSnackBar) throws Exception {
		RequestUtils utils = new RequestUtils(modelDAO);
		AjaxResponse response = new AjaxResponse();
		response.setSkipSnackBar(skipSnackBar);
		response.setUiProceed(closeAfter);
		
		if (!caseId.equals("")) { //for annotations within a case
			User user = (User) session.getAttribute("user");
			if (!ControllerUtil.isUserAssignedToCase(utils, caseId, user)) {
				// user is not assigned to this case
				response.setIsAllowed(false);
				response.setSuccess(false);
				response.setMessage(user.getFullName() + " is not assigned to this case");
				response.setUiProceed(closeAfter);
				return response.createObjectJSON();
			}
		}
		
		response.setIsAllowed(true);
		ObjectMapper mapper = new ObjectMapper();
		DataFilterList dataPOJO = mapper.readValue(data, DataFilterList.class);
		List<String> selectedSNPVariantIds = dataPOJO.getSelectedSNPVariantIds();
		List<String> selectedCNVIds = dataPOJO.getSelectedCNVIds();
		List<String> selectedTranslocationIds = dataPOJO.getSelectedTranslocationIds();
		utils.saveVariantSelection(response, caseId, selectedSNPVariantIds, selectedCNVIds, selectedTranslocationIds);
		return response.createObjectJSON();

	}

	@RequestMapping(value = "/commitAnnotations", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String commitAnnotations(Model model, HttpSession session, @RequestBody String annotations,
			@RequestParam String caseId, @RequestParam String geneId, @RequestParam String variantId) throws Exception {
		User user = (User) session.getAttribute("user");
		RequestUtils utils = new RequestUtils(modelDAO);
		AjaxResponse response = new AjaxResponse();
		if (!caseId.equals("")) { //for annotations within a case
			if (!ControllerUtil.isUserAssignedToCase(utils, caseId, user)) {
				// user is not assigned to this case
				response.setIsAllowed(false);
				response.setSuccess(false);
				response.setMessage(user.getFullName() + " is not assigned to this case");
				return response.createObjectJSON();
			}
		}
		
		response.setIsAllowed(true);

		List<Annotation> userAnnotations = new ArrayList<Annotation>();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode annotationNodes = mapper.readTree(annotations);
		for (JsonNode annotationNode : annotationNodes.get("annotations")) {
			Annotation userAnnotation = mapper.readValue(annotationNode.toString(), Annotation.class);
			if (userAnnotation.getUserId() != null 
					&& !userAnnotation.getUserId().equals(user.getUserId())) {
				response.setSuccess(false);
				response.setMessage("You cannot modify someone else's annotation");
				return response.createObjectJSON();
			}
			userAnnotation.setUserId(user.getUserId());
			if (userAnnotation.getIsCaseSpecific() && !caseId.equals("")) {
				userAnnotation.setCaseId(caseId);
			}
			if (userAnnotation.getIsGeneSpecific()) {
				geneId = geneId != null && geneId.equals("") ? null : geneId;
				userAnnotation.setGeneId(geneId);
			}
			if (userAnnotation.getIsVariantSpecific()) {
				userAnnotation.setVariantId(variantId);
			}
			if (userAnnotation.getIsTumorSpecific()) {
				OrderCase caseSummary = utils.getCaseSummary(caseId);
				if (caseSummary != null) {
					if (caseSummary.getOncotreeDiagnosis() != null && !caseSummary.getOncotreeDiagnosis().equals("")) {
						userAnnotation.setOncotreeDiagnosis(caseSummary.getOncotreeDiagnosis());
					}
					else {
						response.setSuccess(false);
						response.setMessage("You need to set an Oncotree Diagnosis in Patient Details");
						return response.createObjectJSON();
					}
				}
				else {
					response.setSuccess(false);
					response.setMessage("No Case with id: " + caseId);
					return response.createObjectJSON();
				}
			}
			if (userAnnotation.getTrial() != null) {
				Trial trial = userAnnotation.getTrial();
				boolean isValidTrial = trial.getNctId() != null && !trial.getNctId().equals("");
				isValidTrial &= trial.getTitle() != null && !trial.getTitle().equals("");
				isValidTrial &= trial.getPhase() != null && !trial.getPhase().equals("");
				isValidTrial &= trial.getBiomarker() != null && !trial.getBiomarker().equals("");
				isValidTrial &= trial.getDrugs() != null && !trial.getDrugs().equals("");
				isValidTrial &= trial.getContact() != null && !trial.getContact().equals("");
				isValidTrial &= trial.getLocation() != null && !trial.getLocation().equals("");
				if (!isValidTrial) {
					response.setSuccess(false);
					response.setMessage("One or more clinical trial is missing information.");
					return response.createObjectJSON();
				}
			}
			userAnnotations.add(userAnnotation);
		}
		boolean didChange = utils.commitAnnotation(response, userAnnotations);
		if (response.getSuccess()) {
			response.setMessage(didChange + ""); //ajax should check the message in commitAnnotation
			response.setUserPrefs(user.getUserPref());
			response.setPayload(variantId);
		}
		return response.createObjectJSON();

	}

	@RequestMapping(value = "/saveCurrentFilters", produces= "application/json; charset=utf-8")
	@ResponseBody
	@Transactional
	public String saveCurrentFilters(Model model, HttpSession session, @RequestBody String filters,
			@RequestParam Integer filterListId, @RequestParam String filterListName) throws Exception {
		AjaxResponse response = new AjaxResponse();
		User user = (User) session.getAttribute("user");
		VariantFilterList filterList = null;
		if (filterListId == -1) {
			// create a new one
			filterList = Utils.parseFilters(filters, true);
			filterList.setUser(user);
		} else {
			filterList = modelDAO.getSessionFactory().getCurrentSession().get(VariantFilterList.class, filterListId);
			if (filterList.getUser().getUserId() != user.getUserId()) {
				response.setIsAllowed(false);
				response.setSuccess(false);
				response.setMessage("You are not allowed to modify this filter set");
				return response.createObjectJSON();
			}
			if (filterList != null) { // update all filters by removing and replacing them
				modelDAO.deleteObject(filterList);
				filterList = Utils.parseFilters(filters, true);
				filterList.setUser(user);
				// for (VariantFilter filter : filterList.getFilters()) {
				// for (FilterStringValue v : filter.getStringValues()) {
				// modelDAO.deleteObject(v);
				// }
				// modelDAO.deleteObject(filter);
				// }
				// filterList.setFilters(newFilterList.getFilters());
			}
		}
		filterList.setListName(filterListName);
		modelDAO.saveObject(filterList);
		for (VariantFilter filter : filterList.getFilters()) {
			for (FilterStringValue v : filter.getStringValues()) {
				modelDAO.saveObject(v);
			}
			modelDAO.saveObject(filter);
		}
		modelDAO.saveObject(filterList);

		VariantFilterListSaved saveFilterSet = new VariantFilterListSaved();
		saveFilterSet.setSavedFilterSet(filterList);

		saveFilterSet.setIsAllowed(true);
		saveFilterSet.setSuccess(true);
		return saveFilterSet.createObjectJSON();

	}

	@RequestMapping(value = "/loadUserFilterSets")
	@ResponseBody
	public String loadUserFilterSets(Model model, HttpSession session) throws Exception {
		User user = (User) session.getAttribute("user");
		List<VariantFilterList> filters = modelDAO.getVariantFilterListsForUser(user);
		VariantFilterListItems items = new VariantFilterListItems(filters);

		return items.createVuetifyObjectJSON();

	}

	@RequestMapping(value = "/deleteFilterSet", produces= "application/json; charset=utf-8")
	@ResponseBody
	@Transactional
	public String deleteFilterSet(Model model, HttpSession session, @RequestParam Integer filterSetId)
			throws Exception {
		AjaxResponse response = new AjaxResponse();
		if (filterSetId < 0) {
			response.setIsAllowed(false);
			response.setSuccess(false);
			response.setMessage("Invalid filter set id");
			return response.createObjectJSON();
		}
		User user = (User) session.getAttribute("user");
		VariantFilterList filterList = modelDAO.getSessionFactory().getCurrentSession().get(VariantFilterList.class,
				filterSetId);
		if (filterList.getUser().getUserId() != user.getUserId()) {
			response.setIsAllowed(false);
			response.setSuccess(false);
			response.setMessage("You are not allowed to modify this filter set");
			return response.createObjectJSON();
		}
		if (filterList != null) { // update all filters by removing and replacing them
			modelDAO.deleteObject(filterList);
		}

		response.setIsAllowed(true);
		response.setSuccess(true);
		return response.createObjectJSON();

	}

	@RequestMapping(value = "/exportSelection")
	@ResponseBody
	public ResponseEntity<?> exportSelection(Model model, HttpSession session, @RequestParam String caseId,
			@RequestBody String data) throws Exception {
		// AjaxResponse response = new AjaxResponse();
		// response.setIsAllowed(false);
		// response.setSuccess(false);
		ObjectMapper mapper = new ObjectMapper();
		List<String> selectedVariantIds = mapper.readValue(data, DataFilterList.class).getSelectedSNPVariantIds();
		RequestUtils utils = new RequestUtils(modelDAO);
		OrderCase detailedCase = utils.getCaseDetails(caseId, data);
		List<Variant> selectedVariants = detailedCase.getVariants().stream()
				.filter(v -> selectedVariantIds.contains(v.getMongoDBId().getOid())).collect(Collectors.toList());
		List<Variant> selectedVariantDetails = new ArrayList<Variant>();
		for (Variant v : selectedVariants) {
			Variant variantDetails = utils.getVariantDetails(v.getMongoDBId().getOid());
			selectedVariantDetails.add(variantDetails);
		}
		ExportSelectedVariants export = new ExportSelectedVariants(detailedCase, selectedVariantDetails,
				fileProps);
		// response.setIsAllowed(true);
		// response.setSuccess(true);
		File excelFile = export.createExcel();
		FileInputStream fis = new FileInputStream(excelFile);
		byte[] excelContent = IOUtils.toByteArray(fis);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(
				MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		String outputFileName = caseId + "_variants.xlsx";
		headers.setContentDispositionFormData(outputFileName, outputFileName);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(excelContent, headers, HttpStatus.OK);
		fis.close();
		excelFile.delete();

		return response;

	}
	
	@RequestMapping(value = "/sendToMDA", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String sendToMDA(Model model, HttpSession session, @RequestParam String caseId,
			@RequestBody String data) throws Exception {
		// AjaxResponse response = new AjaxResponse();
		// response.setIsAllowed(false);
		// response.setSuccess(false);
		ObjectMapper mapper = new ObjectMapper();
		RequestUtils utils = new RequestUtils(modelDAO);
		OrderCase detailedCase = utils.getCaseDetails(caseId, data);
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(false);
		response.setSuccess(false);
		DataFilterList dataPOJO = mapper.readValue(data, DataFilterList.class);
		List<String> selectedSNPVariantIds = dataPOJO.getSelectedSNPVariantIds();
		List<String> selectedCNVIds = dataPOJO.getSelectedCNVIds();
		List<String> selectedTranslocationIds = dataPOJO.getSelectedTranslocationIds();

		User user = (User) session.getAttribute("user"); // to verify that the user is assigned to the case
		if (detailedCase != null) {
			if (!detailedCase.getAssignedTo().contains(user.getUserId().toString())) {
				response.setMessage("User " + user.getFullName() + " cannot edit this case.");
				return response.createObjectJSON();
			}
			response.setIsAllowed(true);
			AjaxResponse apiResponse = utils.getMocliaContent(caseId, selectedSNPVariantIds, selectedCNVIds, selectedTranslocationIds);
			response.setMessage(apiResponse.getMessage());
			response.setSuccess(apiResponse.getSuccess());
			return response.createObjectJSON();
		}
		else {
			response.setMessage("No case found");
			return response.createObjectJSON();
		}
	}


	@RequestMapping(value = "/saveVariant", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String saveVariant(Model model, HttpSession session, @RequestBody String data,
			@RequestParam String caseId, @RequestParam String variantType,
			@RequestParam(defaultValue="false") Boolean skipSnackBar) throws Exception {

		User user = (User) session.getAttribute("user"); // to verify that the user is assigned to the case
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(false);
		response.setSuccess(false);
		response.setSkipSnackBar(skipSnackBar);
		RequestUtils utils = new RequestUtils(modelDAO);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode nodeData = mapper.readTree(data);
		Object variant = null;
		if (variantType.equals("snp")) {
			variant = mapper.readValue(nodeData.get("variant").toString(), Variant.class);
		}
		else if (variantType.equals("cnv")) {
			variant = mapper.readValue(nodeData.get("variant").toString(), CNV.class);
		}
		else if (variantType.equals("translocation")) {
			variant = mapper.readValue(nodeData.get("variant").toString(), Translocation.class);
		}
		if (variant != null) {
			OrderCase orderCase = utils.getCaseDetails(caseId, null);
			if (orderCase != null) {
				if (!orderCase.getAssignedTo().contains(user.getUserId().toString())) {
					response.setMessage("User " + user.getFullName() + " cannot edit this case.");
					return response.createObjectJSON();
				}
//				stripVariant(variant);
				utils.saveVariant(response, variant, variantType);
				return response.createObjectJSON();
			}
			else {
				response.setMessage("No case found");
				return response.createObjectJSON();
			}
		}
		else { 
			response.setMessage("Nothing to save");
			return response.createObjectJSON();
		}
	}
	
	@RequestMapping(value = "/saveSelectedAnnotationsForVariant", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String saveSelectedAnnotationsForVariant(Model model, HttpSession session, @RequestBody String data,
			@RequestParam String caseId, @RequestParam String variantType,
			@RequestParam(defaultValue="false") Boolean skipSnackBar) throws Exception {

		User user = (User) session.getAttribute("user"); // to verify that the user is assigned to the case
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(false);
		response.setSuccess(false);
		response.setSkipSnackBar(skipSnackBar);
		response.setUiProceed(false);
		RequestUtils utils = new RequestUtils(modelDAO);
		ObjectMapper mapper = new ObjectMapper();
		JsonNode nodeData = mapper.readTree(data);
		
		OrderCase orderCase = utils.getCaseDetails(caseId, null);
		if (orderCase == null) {
			response.setMessage("No case found");
			return response.createObjectJSON();
		}
		if (orderCase != null && !orderCase.getAssignedTo().contains(user.getUserId().toString())) {
			response.setMessage("User " + user.getFullName() + " cannot edit this case.");
			return response.createObjectJSON();
		}
		
		if (variantType.equals("snp")) {
			Variant variant = mapper.readValue(nodeData.get("variant").toString(), Variant.class);
			if (variant != null) {
//				stripVariantForAnnotations(variant);
				variant.setType(variantType);
				utils.saveSelectedAnnotations(response, variant, variantType, variant.getMongoDBId().getOid());
				return response.createObjectJSON();
			}
			else { 
				response.setMessage("Nothing to save");
				return response.createObjectJSON();
			}
		}
		else if (variantType.equals("cnv")) {
			CNV variant = mapper.readValue(nodeData.get("variant").toString(), CNV.class);
			if (variant != null) {
//				stripVariantForAnnotations(variant);
				variant.setType(variantType);
				utils.saveSelectedAnnotations(response, variant, variantType, variant.getMongoDBId().getOid());
				return response.createObjectJSON();
			}
			else { 
				response.setMessage("Nothing to save");
				return response.createObjectJSON();
			}
		}
		else if (variantType.equals("translocation")) {
			Translocation variant = mapper.readValue(nodeData.get("variant").toString(), Translocation.class);
			if (variant != null) {
//				stripVariantForAnnotations(variant);
				variant.setType(variantType);
				utils.saveSelectedAnnotations(response, variant, variantType, variant.getMongoDBId().getOid());
				return response.createObjectJSON();
			}
			else { 
				response.setMessage("Nothing to save");
				return response.createObjectJSON();
			}
		}
		return response.createObjectJSON();
	}
	

	
	@RequestMapping(value = "/getPatientDetails", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getPatientDetails(Model model, HttpSession session, @RequestParam String caseId) throws Exception {

		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		OrderCase caseSummary = utils.getCaseSummary(caseId);
		if (caseSummary != null) {
			PatientInfo patientInfo = new PatientInfo(caseSummary);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(patientInfo);
		}
		return null;

	}
	
	@RequestMapping(value = "/savePatientDetails", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String savePatientDetails(Model model, HttpSession session, @RequestParam String oncotreeDiagnosis,
			@RequestParam String dedupAvgDepth,
			@RequestParam String dedupPctOver100X,
			@RequestParam String caseId,
			@RequestParam(defaultValue="false") Boolean skipSnackBar) throws Exception {

		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		User user = (User) session.getAttribute("user");
		boolean isAssigned = ControllerUtil.isUserAssignedToCase(utils, caseId, user);
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(isAssigned);
		response.setSkipSnackBar(skipSnackBar);
		if (isAssigned) {
			OrderCase caseSummary = utils.getCaseSummary(caseId);
			if (caseSummary != null) {
				caseSummary.setOncotreeDiagnosis(oncotreeDiagnosis);
				if (dedupAvgDepth != "") {
					try {
						int dedupAvgDepthInt = Integer.parseInt(dedupAvgDepth);
						caseSummary.setDedupAvgDepth(dedupAvgDepthInt);
					} catch (Exception e) {
						response.setSuccess(false);
						response.setMessage("Avg. Depth is not a valid integer: " + dedupAvgDepth);
					}
				}
				if (dedupPctOver100X != "") {
					try {
						double dedupPctOver100XDouble = Double.parseDouble(dedupPctOver100X);
						caseSummary.setDedupPctOver100X(dedupPctOver100XDouble);
					} catch (Exception e) {
						response.setSuccess(false);
						response.setMessage("Pct. Over 100X is not a valid number: " + dedupPctOver100X);
					}
				}
				OrderCase savedCaseSummary = utils.saveCaseSummary(caseId, caseSummary);
				if (savedCaseSummary != null) {
					response.setSuccess(true);
				}
				else { //something was wrong with saving the oncotreediagnosis
					response.setMessage("Error: Verify that the data is valid (eg. OncoTree Diagnosis exists?)");
					response.setSuccess(false);
				}
			}
			else {
				response.setSuccess(false);
				response.setMessage("The case id " + caseId + " does not exist.");
			}
		}
		else {
			response.setMessage("You are not allowed to edit this case");
		}
		return response.createObjectJSON();

	}
	
	
	@RequestMapping(value = "/readyForReview", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String readyForReview(Model model, HttpSession session, @RequestParam String caseId) throws Exception {
		
		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		User currentUser = (User) session.getAttribute("user");
		boolean isAssigned = ControllerUtil.isUserAssignedToCase(utils, caseId, currentUser);
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(isAssigned);
		if (isAssigned) {
			utils.caseReadyForReview(response, caseId);
			if (response.getSuccess()) {
				List<User> users = modelDAO.getAllUsers();
				OrderCase caseSummary = utils.getCaseSummary(caseId);
				for (User aUser : users) {
					//notify users assigned to the case
					if (!aUser.equals(currentUser) 
							&& (ControllerUtil.isUserAssignedToCase(caseSummary, aUser) 
							&& aUser.getIndividualPermission().getCanReview())) {
						String servlet = "openCase/";
						String markedAs = " marked a case as ready for review. ";
						String reason = "You are receiving this message because you are a reviewer on this case.<br/><br/>";
						String subject = "You have a new case: " + caseId;
						this.sendEmail(caseId, subject, aUser, currentUser, servlet, markedAs, reason);
					}
					//notify other users whose receive_all_notifications is true
					if (!aUser.equals(currentUser) 
							&& !ControllerUtil.isUserAssignedToCase(caseSummary, aUser) 
							&& aUser.getIndividualPermission().getReceiveAllNotifications()) {
						String servlet = "openCaseReadOnly/";
						String markedAs = " marked a case as ready for review. ";
						String reason = "You are receiving this message because your account is set to receive all notifications.<br/><br/>";
						String subject = "You have a new notification for case: " + caseId;
						this.sendEmail(caseId, subject, aUser, currentUser, servlet, markedAs, reason);
					}
				}
			}
			else {
				response.setMessage("You are not allowed to edit this case");
			}
		}
		return response.createObjectJSON();
	}
	
	private void sendEmail(String caseId, String subject, User user, User currentUser, String servlet, String markedAs, String reason) throws IOException, InterruptedException {
		StringBuilder message = new StringBuilder()
				.append("<p>Dr. ").append(user.getLast()).append(",</p><br/>")
				.append("<b>")
				.append(currentUser.getFullName())
				.append("</b>")
				.append(markedAs)
				.append("<b>")
				.append("Case Id: ").append(caseId).append("</b><br/>")
				.append("<br/>")
				.append(reason);
				
		String toEmail = user.getEmail();
//		String toEmail = "guillaume.jimenez@utsouthwestern.edu"; //for testing to avoid sending other people emails
		String link = new StringBuilder().append(emailProps.getRootUrl()).append(servlet).append(caseId)
				.append("?showReview=true").toString();
		String fullMessage = NotificationUtils.buildStandardMessage(message.toString(), emailProps, link);
		boolean success = NotificationUtils.sendEmail(emailProps.getFrom(), toEmail, subject, fullMessage);
		System.out.println("An email was sent. Success:" + success);
	}
	
	@RequestMapping(value = "/readyForReport", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String readyForReport(Model model, HttpSession session, @RequestParam String caseId) throws Exception {

		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		User currentUser = (User) session.getAttribute("user");
		boolean isAssigned = ControllerUtil.isUserAssignedToCase(utils, caseId, currentUser);
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(isAssigned);
		if (isAssigned) {
			utils.caseReadyForReport(response, caseId);
			if (!response.getSuccess()) {
				response.setMessage("You are not allowed to review this case");
			}
			else {
				List<User> users = modelDAO.getAllUsers();
				for (User aUser : users) {
					//notify other users whose receive_all_notifications is true
					if (!aUser.equals(currentUser) 
							&& aUser.getIndividualPermission().getReceiveAllNotifications()) {
						String servlet = "openReportReadOnly/";
						String markedAs = " marked a case as ready for report. ";
						String reason = "You are receiving this message because your account is set to receive all notifications.<br/><br/>";
						String subject = "You have a new notification for case: " + caseId;
						this.sendEmail(caseId, subject, aUser, currentUser, servlet, markedAs, reason);
					}
				}
			}
		}
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/getCNVChartData", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getCNVChartData(Model model, HttpSession session, @RequestParam String caseId, @RequestParam(defaultValue="all", required=false) String chrom,
			@RequestParam(defaultValue="", required=false) String genesParam) throws Exception {

		RequestUtils utils = new RequestUtils(modelDAO);
		if (chrom.equals("all")) {
			chrom = null;
			genesParam = "";//don't color genes in this view
		}
		List<String> selectedGenes = new ArrayList<String>();
		if (!genesParam.equals("")) {
			String[] selectedGenesArray = genesParam.split(",");
			for (String gene : selectedGenesArray) {
				selectedGenes.add(gene.trim());
			}
		}
		CNVPlotData cnvPlotData = utils.getCnvPlotData(caseId, chrom);
		if (cnvPlotData != null) {
			return new CNVChartData(cnvPlotData.getCnsData(), cnvPlotData.getCnrData(), selectedGenes).createObjectJSON();
		}
		else {
			AjaxResponse response = new AjaxResponse();
			response.setIsAllowed(false);
			response.setSuccess(false);
			
			return response.createObjectJSON();
		}

	}
	
	@RequestMapping(value = "/verifyGeneNames", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String verifyGeneNames(Model model, HttpSession session, @RequestParam String type, @RequestParam String genesParam) throws Exception {

		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(false);
		response.setSuccess(false);
		
		String[] genes = genesParam.split(",");
		List<String> cleanGenes = new ArrayList<String>();
		for (String gene : genes) {
			cleanGenes.add(gene.trim().toUpperCase());
		}
		
		List<String> genesFound = modelDAO.getGenesInPanels(cleanGenes);
		List<String> invalidGenes = new ArrayList<String>();
		for (String gene : cleanGenes) {
			if (!genesFound.contains(gene)) {
				invalidGenes.add(gene);
			}
		}
		if (!invalidGenes.isEmpty() ) {
			StringBuilder message = new StringBuilder("Some genes are untargeted:<br/>");
			message.append(invalidGenes.stream().collect(Collectors.joining(" "))).append("<br/><br/>");
			message.append("Click <a href='").append(fileProps.getGenePanelSearchUrl()).append("' target='blank'>").append(" here </a> to see which genes we sequence.<br/><br/>");
			response.setMessage(message.toString());
		}
		response.setIsAllowed(true);
		response.setSuccess(true);

		return response.createObjectJSON();

	}
	
	@RequestMapping(value = "/saveCNV", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String saveCNV(Model model, HttpSession session, @RequestParam String caseId,
			@RequestBody String data) throws Exception {

		RequestUtils utils = new RequestUtils(modelDAO);
		User user = (User) session.getAttribute("user"); // to verify that the user is assigned to the case
		boolean isAssigned = ControllerUtil.isUserAssignedToCase(utils, caseId, user);
		AjaxResponse response = new AjaxResponse();
		if (!isAssigned) {
			response.setIsAllowed(false);
			response.setSuccess(false);
		}
		else {
			ObjectMapper mapper = new ObjectMapper();
			CNV cnv = mapper.readValue(data, CNV.class);
			if (cnv != null) {
				cnv.setType("cnv");
				utils.saveCNV(response, cnv, caseId);
			}
			else {
				response.setMessage("Nothing to save");
			}
		}
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/fetchNCTData", produces= "application/json; charset=utf-8") 
	@ResponseBody
	public String fetchNCTData(Model model, HttpSession session, @RequestParam String nctId)
			throws Exception {

		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		AjaxResponse response = new AjaxResponse();
		Trial trial = utils.getNCTData(response, nctId);
		if (trial != null) {
			trial.setSuccess(true);
			trial.setIsAllowed(true);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(trial);
		}
		return response.createObjectJSON();

	}
	
	@RequestMapping(value = "/setDefaultTranscript", produces= "application/json; charset=utf-8") 
	@ResponseBody
	public String setDefaultTranscript(Model model, HttpSession session, @RequestParam String variantId, @RequestBody String data)
			throws Exception {

		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		AjaxResponse response = new AjaxResponse();
		utils.setDefaultTranscript(response, data, variantId);
		return response.createObjectJSON();

	}
	
	@RequestMapping(value = "/saveHeaderConfig", produces= "application/json; charset=utf-8") 
	@ResponseBody
	public String saveHeaderConfig(Model model, HttpSession session, @RequestBody String data)
			throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		HeaderConfigData headerConfig = mapper.readValue(data, HeaderConfigData.class);
		User user = (User) session.getAttribute("user");
		List<HeaderConfig> existingConfigs = modelDAO.getHeaderConfigForUserAndTable(user.getUserId(), headerConfig.getTableTitle());
		HeaderConfig uniqueConfigForTable = null;
		if (existingConfigs != null && !existingConfigs.isEmpty()) {
			uniqueConfigForTable = existingConfigs.get(0);
		}
		if (uniqueConfigForTable == null) {
			uniqueConfigForTable = new HeaderConfig();
			uniqueConfigForTable.setUser(user);
			uniqueConfigForTable.setTableTitle(headerConfig.getTableTitle());
		}
		
		String headerOrder = mapper.writeValueAsString(headerConfig.getHeaders().stream().map(h -> new HeaderOrder(h)).collect(Collectors.toList()));
		uniqueConfigForTable.setHeaderOrder(headerOrder);
		modelDAO.saveObject(uniqueConfigForTable);
		
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		response.setSuccess(true);
		return response.createObjectJSON();

	}
	
	@RequestMapping(value = "/deleteHeaderConfig", produces= "application/json; charset=utf-8") 
	@ResponseBody
	public String deleteHeaderConfig(Model model, HttpSession session, @RequestParam String tableTitle)
			throws Exception {

		AjaxResponse response = new AjaxResponse();
		User user = (User) session.getAttribute("user");
		List<HeaderConfig> existingConfigs = modelDAO.getHeaderConfigForUserAndTable(user.getUserId(), tableTitle);
		HeaderConfig uniqueConfigForTable = null;
		if (existingConfigs != null && !existingConfigs.isEmpty()) {
			uniqueConfigForTable = existingConfigs.get(0);
			modelDAO.deleteObject(uniqueConfigForTable);
		}
		
		response.setIsAllowed(true);
		response.setSuccess(true);
		return response.createObjectJSON();

	}
	
}
