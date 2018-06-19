package utsw.bicf.answer.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import utsw.bicf.answer.controller.serialization.DataFilterList;
import utsw.bicf.answer.controller.serialization.DataTableFilter;
import utsw.bicf.answer.controller.serialization.SearchItem;
import utsw.bicf.answer.controller.serialization.SearchItemString;
import utsw.bicf.answer.controller.serialization.Utils;
import utsw.bicf.answer.controller.serialization.vuetify.OpenCaseSummary;
import utsw.bicf.answer.controller.serialization.vuetify.VariantDetailsSummary;
import utsw.bicf.answer.controller.serialization.vuetify.VariantFilterItems;
import utsw.bicf.answer.controller.serialization.vuetify.VariantFilterListItems;
import utsw.bicf.answer.controller.serialization.vuetify.VariantFilterListSaved;
import utsw.bicf.answer.controller.serialization.vuetify.VariantVcfAnnotationSummary;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.FilterStringValue;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.VariantFilter;
import utsw.bicf.answer.model.VariantFilterList;
import utsw.bicf.answer.model.extmapping.Annotation;
import utsw.bicf.answer.model.extmapping.CaseAnnotation;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.VCFAnnotation;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.reporting.parse.ExportSelectedVariants;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class OpenCaseController {

	static {
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".openCase",
				new PermissionUtils(true, false, false));
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".getCaseDetails",
				new PermissionUtils(true, false, false));
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".getVariantFilters",
				new PermissionUtils(true, false, false));
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".getVariantDetails",
				new PermissionUtils(true, false, false));
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".saveVariantSelection",
				new PermissionUtils(true, true, false));
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".commitAnnotations",
				new PermissionUtils(true, true, false));
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".saveCurrentFilters",
				new PermissionUtils(true, false, false));
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".loadUserFilterSets",
				new PermissionUtils(true, false, false));
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".deleteFilterSet",
				new PermissionUtils(true, false, false));
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".exportSelection",
				new PermissionUtils(true, false, false));
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".saveCaseAnnotation",
				new PermissionUtils(true, true, false));
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".loadCaseAnnotation",
				new PermissionUtils(true, true, false));
		PermissionUtils.addPermission(OpenCaseController.class.getCanonicalName() + ".getCNVDetails",
				new PermissionUtils(true, true, false));

	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;
	@Autowired
	FileProperties fileProperties;

	@RequestMapping("/openCase/{caseId}")
	public String openCase(Model model, HttpSession session, @PathVariable String caseId) throws IOException {
		model.addAttribute("urlRedirect", "openCase/" + caseId);
		User user = (User) session.getAttribute("user");
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
					if (detailedCase == null || !detailedCase.getAssignedTo().contains(user.getUserId().toString())) {
						// user is not assigned to this case
						AjaxResponse response = new AjaxResponse();
						response.setIsAllowed(false);
						response.setSuccess(false);
						response.setMessage(user.getFullName() + " is not assigned to this case");
						return response.createObjectJSON();
					}
					break; // found that the case exists
				}
			}
		}
		OpenCaseSummary summary = new OpenCaseSummary(modelDAO, detailedCase, null, "oid", user);
		return summary.createVuetifyObjectJSON();

	}

	@RequestMapping(value = "/loadCaseAnnotations")
	@ResponseBody
	public String loadCaseAnnotations(Model model, HttpSession session, @RequestParam String caseId) throws Exception {

		User user = (User) session.getAttribute("user"); // to verify that the user is assigned to the case
		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		CaseAnnotation annotation = utils.getCaseAnnotation(caseId);
		if (annotation != null && annotation.getCaseId() != null) {
			if (!annotation.getAssignedTo().contains(user.getUserId().toString())) {
				AjaxResponse response = new AjaxResponse();
				response.setIsAllowed(false);
				response.setSuccess(false);
				response.setMessage(user.getFullName() + " is not assigned to this case");
				return response.createObjectJSON();
			}
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
				if (annotation != null) { // annotation should never be null. Make sure there is no funny business with user
											// id or oid
					if (!annotation.getAssignedTo().contains(user.getUserId().toString())) {
						response.setMessage(user.getFullName() + " is not assigned to this case");
						return response.createObjectJSON();
					}
					if (annotationToSave.getMangoDBId() != null && annotation.getMangoDBId().getOid() != annotationToSave.getMangoDBId().getOid()) {
						response.setMessage("Invalid annotation");
						return response.createObjectJSON();
					}
					annotation.setCaseAnnotation(annotationToSave.getCaseAnnotation());
					utils.saveCaseAnnotation(response, annotation);
					return response.createObjectJSON();
					
				}
				else { 
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
		geneFilter.setString(true);
		filters.add(geneFilter);

		DataTableFilter passQCFilter = new DataTableFilter("Pass QC", "Fail QC", Variant.FIELD_FILTERS);
		passQCFilter.setBoolean(true);
		filters.add(passQCFilter);

		DataTableFilter annotatedFilter = new DataTableFilter("Annotated", "Unknown", Variant.FIELD_ANNOTATIONS);
		annotatedFilter.setBoolean(true);
		filters.add(annotatedFilter);

		DataTableFilter tafFilter = new DataTableFilter("Tumor Alt %", Variant.FIELD_TUMOR_ALT_FREQUENCY);
		tafFilter.setNumber(true);
		filters.add(tafFilter);

		// DataTableFilter tumorDepthFilter = new DataTableFilter("Tumor Depth",
		// "tumorAltDepth");
		// tumorDepthFilter.setNumber(true);
		// filters.add(tumorDepthFilter);

		DataTableFilter tumorTotalDepthFilter = new DataTableFilter("Tumor Total Depth",
				Variant.FIELD_TUMOR_TOTAL_DEPTH);
		tumorTotalDepthFilter.setNumber(true);
		filters.add(tumorTotalDepthFilter);

		DataTableFilter nafFilter = new DataTableFilter("Normal Alt %", Variant.FIELD_NORMAL_ALT_FREQUENCY);
		nafFilter.setNumber(true);
		filters.add(nafFilter);

		// DataTableFilter normalDepthFilter = new DataTableFilter("Normal Depth",
		// "normalAltDepth");
		// normalDepthFilter.setNumber(true);
		// filters.add(normalDepthFilter);

		DataTableFilter normalTotalDepthFilter = new DataTableFilter("Normal Total Depth",
				Variant.FIELD_NORMAL_TOTAL_DEPTH);
		normalTotalDepthFilter.setNumber(true);
		filters.add(normalTotalDepthFilter);

		DataTableFilter rafFilter = new DataTableFilter("Rna Alt %", Variant.FIELD_RNA_ALT_FREQUENCY);
		rafFilter.setNumber(true);
		filters.add(rafFilter);

		// DataTableFilter rnaDepthFilter = new DataTableFilter("RNA Depth",
		// "rnaAltDepth");
		// rnaDepthFilter.setNumber(true);
		// filters.add(rnaDepthFilter);

		DataTableFilter rnaTotalDepthFilter = new DataTableFilter("RNA Total Depth", Variant.FIELD_RNA_TOTAL_DEPTH);
		rnaTotalDepthFilter.setNumber(true);
		filters.add(rnaTotalDepthFilter);

		DataTableFilter effectFilter = new DataTableFilter("Effects", Variant.FIELD_EFFECTS);
		effectFilter.setCheckBox(true);
		filters.add(effectFilter);

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
		VariantVcfAnnotationSummary summaryCanonical = null;
		VariantVcfAnnotationSummary summaryOthers = null;
		VariantDetailsSummary summary = null;
		if (variantDetails != null) {
			// populate user info to be used by the UI
			if (variantDetails.getReferenceVariant() != null
					&& variantDetails.getReferenceVariant().getUtswAnnotations() != null) {
				for (Annotation a : variantDetails.getReferenceVariant().getUtswAnnotations()) {
					User annotationUser = modelDAO.getUserByUserId(a.getUserId());
					if (annotationUser != null) {
						a.setFullName(annotationUser.getFullName());
					}
				}
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
				summary = new VariantDetailsSummary(variantDetails, summaryCanonical, summaryOthers);
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
		Variant variantDetails = utils.getVariantDetails(variantId);
		VariantDetailsSummary summary = null;
		if (variantDetails != null) {
			// populate user info to be used by the UI
			if (variantDetails.getReferenceVariant() != null
					&& variantDetails.getReferenceVariant().getUtswAnnotations() != null) {
				for (Annotation a : variantDetails.getReferenceVariant().getUtswAnnotations()) {
					User annotationUser = modelDAO.getUserByUserId(a.getUserId());
					if (annotationUser != null) {
						a.setFullName(annotationUser.getFullName());
					}
				}
			}
			summary = new VariantDetailsSummary(variantDetails, null, null);
			return summary.createVuetifyObjectJSON();
		}
		return null;

	}

	@RequestMapping(value = "/saveVariantSelection")
	@ResponseBody
	public String saveVariantSelection(Model model, HttpSession session, @RequestBody String selectedSNPVariantIds,
			@RequestBody String selectedCNVIds, @RequestBody String selectedTranslocationIds,
			@RequestParam String caseId) throws Exception {
		RequestUtils utils = new RequestUtils(modelDAO);
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
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
			filterList = Utils.parseFilters(filters);
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
				filterList = Utils.parseFilters(filters);
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
	public ResponseEntity<byte[]> exportSelection(Model model, HttpSession session, @RequestParam String caseId,
			@RequestBody String data) throws Exception {
		// AjaxResponse response = new AjaxResponse();
		// response.setIsAllowed(false);
		// response.setSuccess(false);
		ObjectMapper mapper = new ObjectMapper();
		List<String> selectedVariantIds = mapper.readValue(data, DataFilterList.class).getSelectedVariantIds();
		RequestUtils utils = new RequestUtils(modelDAO);
		OrderCase detailedCase = utils.getCaseDetails(caseId, data);
		List<Variant> selectedVariants = detailedCase.getVariants().stream()
				.filter(v -> selectedVariantIds.contains(v.getMangoDBId().getOid())).collect(Collectors.toList());
		List<Variant> selectedVariantDetails = new ArrayList<Variant>();
		for (Variant v : selectedVariants) {
			Variant variantDetails = utils.getVariantDetails(v.getMangoDBId().getOid());
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
}
