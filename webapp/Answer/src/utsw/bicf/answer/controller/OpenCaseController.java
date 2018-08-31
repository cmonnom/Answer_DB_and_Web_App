package utsw.bicf.answer.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.apache.http.client.ClientProtocolException;
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
import utsw.bicf.answer.controller.serialization.zingchart.CNVChartData;
import utsw.bicf.answer.controller.serialization.vuetify.OpenCaseSummary;
import utsw.bicf.answer.controller.serialization.vuetify.VariantDetailsSummary;
import utsw.bicf.answer.controller.serialization.vuetify.VariantFilterItems;
import utsw.bicf.answer.controller.serialization.vuetify.VariantFilterListItems;
import utsw.bicf.answer.controller.serialization.vuetify.VariantFilterListSaved;
import utsw.bicf.answer.controller.serialization.vuetify.VariantRelatedSummary;
import utsw.bicf.answer.controller.serialization.vuetify.VariantVcfAnnotationSummary;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.FilterStringValue;
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
import utsw.bicf.answer.model.extmapping.VCFAnnotation;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.model.hybrid.PatientInfo;
import utsw.bicf.answer.model.hybrid.ReportGroupForDisplay;
import utsw.bicf.answer.reporting.parse.ExportSelectedVariants;
import utsw.bicf.answer.security.EmailProperties;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.NotificationUtils;
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
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".getCNVChartData",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".verifyGeneNames",
				IndividualPermission.CAN_VIEW);
		
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;
	@Autowired
	FileProperties fileProperties;
	@Autowired
	QcAPIAuthentication qcAPI;
	@Autowired
	EmailProperties emailProps;
	@Autowired
	FileProperties fileProps;

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
		RequestUtils utils = new RequestUtils(modelDAO);
		if (user != null && !isUserAssignedToCase(utils, caseId, user)) {
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
		return ControllerUtil.initializeModel(model, servletContext, user);
	}
	
	

	@RequestMapping(value = "/getCaseDetails")
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
		
		OpenCaseSummary summary = new OpenCaseSummary(modelDAO, qcAPI, detailedCase, null, "oid", user,
				reportGroupsForDisplay);
		return summary.createVuetifyObjectJSON();

	}

	@RequestMapping(value = "/loadCaseAnnotations")
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

	@RequestMapping(value = "/saveCaseAnnotations")
	@ResponseBody
	public String saveCaseAnnotations(Model model, HttpSession session, @RequestBody String caseAnnotation,
			@RequestParam String caseId) throws Exception {

		User user = (User) session.getAttribute("user"); // to verify that the user is assigned to the case
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(false);
		response.setSuccess(false);
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

	@RequestMapping(value = "/getVariantFilters")
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

	@RequestMapping(value = "/getVariantDetails")
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
				summaryRelated = new VariantRelatedSummary(variantDetails.getRelatedVariants(), "chromPos");
			}
			List<VCFAnnotation> vcfAnnotations = variantDetails.getVcfAnnotations();
			if (!vcfAnnotations.isEmpty()) {
				List<VCFAnnotation> canonicalAnnotation = new ArrayList<VCFAnnotation>();
				canonicalAnnotation.add(vcfAnnotations.get(0));
				List<VCFAnnotation> otherAnnotations = new ArrayList<VCFAnnotation>();
				otherAnnotations.addAll(vcfAnnotations);
				otherAnnotations.remove(0);
				summaryCanonical = new VariantVcfAnnotationSummary(canonicalAnnotation, "proteinPosition");
				summaryOthers = new VariantVcfAnnotationSummary(otherAnnotations, "proteinPosition");
				summary = new VariantDetailsSummary(variantDetails, summaryRelated, summaryCanonical, summaryOthers);
			}
			return summary.createVuetifyObjectJSON();
		}
		return null;

	}

	@RequestMapping(value = "/getCNVDetails")
	@ResponseBody
	public String getCNVDetails(Model model, HttpSession session, @RequestParam String variantId) throws Exception {

		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		CNV variantDetails = utils.getCNVDetails(variantId);
		if (variantDetails != null) {
			for (Annotation a : variantDetails.getReferenceCnv().getUtswAnnotations()) {
				Annotation.init(a, variantDetails.getAnnotationIdsForReporting(), modelDAO); // format dates and add missing info
			}
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(variantDetails);
		}
		return null;

	}

	@RequestMapping(value = "/getTranslocationDetails")
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
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(variantDetails);
		}
		return null;

	}

	@RequestMapping(value = "/saveVariantSelection")
	@ResponseBody
	public String saveVariantSelection(Model model, HttpSession session, @RequestBody String data,
			@RequestParam String caseId) throws Exception {
		RequestUtils utils = new RequestUtils(modelDAO);
		AjaxResponse response = new AjaxResponse();
		
		if (!caseId.equals("")) { //for annotations within a case
			User user = (User) session.getAttribute("user");
			if (!isUserAssignedToCase(utils, caseId, user)) {
				// user is not assigned to this case
				response.setIsAllowed(false);
				response.setSuccess(false);
				response.setMessage(user.getFullName() + " is not assigned to this case");
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

	@RequestMapping(value = "/commitAnnotations")
	@ResponseBody
	public String commitAnnotations(Model model, HttpSession session, @RequestBody String annotations,
			@RequestParam String caseId, @RequestParam String geneId, @RequestParam String variantId) throws Exception {
		User user = (User) session.getAttribute("user");
		RequestUtils utils = new RequestUtils(modelDAO);
		AjaxResponse response = new AjaxResponse();
		if (!caseId.equals("")) { //for annotations within a case
			if (!isUserAssignedToCase(utils, caseId, user)) {
				// user is not assigned to this case
				response.setIsAllowed(false);
				response.setSuccess(false);
				response.setMessage(user.getFullName() + " is not assigned to this case");
				return response.createObjectJSON();
			}
		}
//	}
		
		response.setIsAllowed(true);

		List<Annotation> userAnnotations = new ArrayList<Annotation>();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode annotationNodes = mapper.readTree(annotations);
		for (JsonNode annotationNode : annotationNodes.get("annotations")) {
			Annotation userAnnotation = mapper.readValue(annotationNode.toString(), Annotation.class);
			userAnnotation.setUserId(user.getUserId());
			if (userAnnotation.getIsCaseSpecific()) {
				userAnnotation.setCaseId(caseId);
			}
			if (userAnnotation.getIsGeneSpecific()) {
				userAnnotation.setGeneId(geneId);
			}
			if (userAnnotation.getIsVariantSpecific()) {
				userAnnotation.setVariantId(variantId);
			}
			userAnnotations.add(userAnnotation);
		}
		utils.commitAnnotation(response, caseId, variantId, userAnnotations);
		return response.createObjectJSON();

	}

	@RequestMapping(value = "/saveCurrentFilters")
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

	@RequestMapping(value = "/deleteFilterSet")
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
				fileProperties);
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
	
	@RequestMapping(value = "/sendToMDA")
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
			utils.sendVariantSelectionToMDA(response, caseId, selectedSNPVariantIds, selectedCNVIds, selectedTranslocationIds);
			return response.createObjectJSON();
		}
		else {
			response.setMessage("No case found");
			return response.createObjectJSON();
		}
	}


	@RequestMapping(value = "/saveVariant")
	@ResponseBody
	public String saveVariant(Model model, HttpSession session, @RequestBody String data,
			@RequestParam String caseId, @RequestParam String variantType) throws Exception {

		User user = (User) session.getAttribute("user"); // to verify that the user is assigned to the case
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(false);
		response.setSuccess(false);
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
	
	@RequestMapping(value = "/saveSelectedAnnotationsForVariant")
	@ResponseBody
	public String saveSelectedAnnotationsForVariant(Model model, HttpSession session, @RequestBody String data,
			@RequestParam String caseId, @RequestParam String variantType) throws Exception {

		User user = (User) session.getAttribute("user"); // to verify that the user is assigned to the case
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(false);
		response.setSuccess(false);
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
	
	/**
	 * Remove heavy or transient objects from Variant
	 * to make it lighter or to avoid saving unwanted fields
	 * For instance we don't want to save the annotations selected
	 * when saving the variant tier
	 * @param variant
	 */
//	private static void stripVariant(Variant variant) {
//		variant.setReferenceVariant(null);
//		variant.setAnnotationIdsForReporting(null);
//		variant.setVcfAnnotations(null);
//		variant.setRelatedVariants(null);
//	}
//	
//	private static void stripVariantForAnnotations(Variant variant) {
//		variant.setReferenceVariant(null);
//		variant.setTier(null);
//		variant.setVcfAnnotations(null);
//		variant.setRelatedVariants(null);
//	}
//	
//	private static void stripVariantForAnnotations(CNV variant) {
//		variant.setGenes(null);
//	}
//	
//	private static void stripVariantForAnnotations(Translocation variant) {
//		variant.setReferenceTranslocation(null);
//	}
	
	public static boolean isUserAssignedToCase(RequestUtils utils, String caseId, User user) throws ClientProtocolException, IOException, URISyntaxException {
		OrderCase caseSummary = utils.getCaseSummary(caseId);
		return (caseSummary == null || caseSummary.getAssignedTo().contains(user.getUserId().toString()));
		
	}
	
	public static boolean isUserAssignedToCase(OrderCase caseSummary, User user) throws ClientProtocolException, IOException, URISyntaxException {
		return (caseSummary == null || caseSummary.getAssignedTo().contains(user.getUserId().toString()));
	}
	
	@RequestMapping(value = "/getPatientDetails")
	@ResponseBody
	public String getPatientDetails(Model model, HttpSession session, @RequestParam String caseId) throws Exception {

		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		OrderCase caseSummary = utils.getCaseSummary(caseId);
		if (caseSummary != null) {
			PatientInfo patientInfo = new PatientInfo(caseSummary, null);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(patientInfo);
		}
		return null;

	}
	
	@RequestMapping(value = "/savePatientDetails")
	@ResponseBody
	public String savePatientDetails(Model model, HttpSession session, @RequestParam String oncotreeDiagnosis,
			@RequestParam String caseId) throws Exception {

		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		User user = (User) session.getAttribute("user");
		boolean isAssigned = isUserAssignedToCase(utils, caseId, user);
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(isAssigned);
		if (isAssigned) {
			OrderCase caseSummary = utils.getCaseSummary(caseId);
			if (caseSummary != null) {
				caseSummary.setOncotreeDiagnosis(oncotreeDiagnosis);
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
	
	
	@RequestMapping(value = "/readyForReview")
	@ResponseBody
	public String readyForReview(Model model, HttpSession session, @RequestParam String caseId) throws Exception {

		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		User currentUser = (User) session.getAttribute("user");
		boolean isAssigned = isUserAssignedToCase(utils, caseId, currentUser);
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(isAssigned);
		if (isAssigned) {
			utils.caseReadyForReview(response, caseId);
			if (response.getSuccess()) {
				List<User> users = modelDAO.getAllUsers();
				OrderCase caseSummary = utils.getCaseSummary(caseId);
				for (User aUser : users) {
					if (!aUser.equals(currentUser) && isUserAssignedToCase(caseSummary, aUser) && aUser.getIndividualPermission().getCanReview()) {
						String subject = "You have a new case: " + caseId;
						StringBuilder message = new StringBuilder()
								.append("<p>Dr. ").append(aUser.getLast()).append(",</p><br/>")
								.append("<b>")
								.append(currentUser.getFullName())
								.append("</b>")
								.append(" marked a case as ready for review. ")
								.append("<b>")
								.append("Case Id: ").append(caseId).append("</b><br/>")
								.append("<br/>")
								.append("You are receiving this message because you are a reviewer on this case.<br/><br/>");								;

								String toEmail = aUser.getEmail();
//								String toEmail = "guillaume.jimenez@utsouthwestern.edu"; //for testing to avoid sending other people emails
								String link = new StringBuilder().append(emailProps.getRootUrl()).append("openCase/").append(caseId)
										.append("?showReview=true").toString();
								String fullMessage = NotificationUtils.buildStandardMessage(message.toString(), emailProps, link);
								boolean success = NotificationUtils.sendEmail(emailProps.getFrom(), toEmail, subject, fullMessage);
								System.out.println("An email was sent. Success:" + success);
					}
				}
			}
			else {
				response.setMessage("You are not allowed to edit this case");
			}
		}
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/getCNVChartData")
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
	
	@RequestMapping(value = "/verifyGeneNames")
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
	
}
