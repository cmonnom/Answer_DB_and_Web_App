package utsw.bicf.answer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.vuetify.VariantsForGeneItems;
import utsw.bicf.answer.dao.LoginDAO;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.Annotation;
import utsw.bicf.answer.model.extmapping.AnnotationSearchResult;
import utsw.bicf.answer.model.extmapping.Trial;
import utsw.bicf.answer.model.extmapping.Variant;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.OtherProperties;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class AnnotationBrowserController {
	
	static {
		PermissionUtils.addPermission(AnnotationBrowserController.class.getCanonicalName() + ".annotationBrowser", IndividualPermission.CAN_ANNOTATE);
		PermissionUtils.addPermission(AnnotationBrowserController.class.getCanonicalName() + ".searchForAnnotations", IndividualPermission.CAN_ANNOTATE);
		PermissionUtils.addPermission(AnnotationBrowserController.class.getCanonicalName() + ".getAllClinicalTrials", IndividualPermission.CAN_ANNOTATE);
		PermissionUtils.addPermission(AnnotationBrowserController.class.getCanonicalName() + ".commitCaseAgnosticAnnotations", IndividualPermission.CAN_ANNOTATE);
		PermissionUtils.addPermission(AnnotationBrowserController.class.getCanonicalName() + ".getVariantsForGene", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(AnnotationBrowserController.class.getCanonicalName() + ".getAllSNPsForGene", IndividualPermission.CAN_VIEW);
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;
	@Autowired
	FileProperties fileProps;
	@Autowired
	OtherProperties otherProps;
	@Autowired
	LoginDAO loginDAO;

	@RequestMapping("/annotationBrowser")
	public String annotationBrowser(Model model, HttpSession session,
			@RequestParam(defaultValue="", required=false) String annotationId,
			@RequestParam(defaultValue="", required=false) String variantType,
			@RequestParam(defaultValue="", required=false) String edit) throws IOException {
		String url = "annotationBrowser?annotationId=" + annotationId + "%26variantType=" + variantType
				+ "%26edit=" + edit;
		model.addAttribute("urlRedirect", url);
		ControllerUtil.setGlobalVariables(model, fileProps, otherProps);
		User user = ControllerUtil.getSessionUser(session);
		return ControllerUtil.initializeModel(model, servletContext, user, loginDAO);
	}
	
//	@RequestMapping(value = "/searchForAnnotations")
//	@ResponseBody
//	public String searchForAnnotations(Model model, HttpSession session, 
//			@RequestParam(defaultValue = "", required = false) String gene,
//			@RequestParam(defaultValue = "", required = false) String variant, 
//			@RequestParam(defaultValue = "", required = false) String chrom,
//			@RequestParam(defaultValue = "", required = false) String leftGene, 
//			@RequestParam(defaultValue = "", required = false) String rightGene, 
//			@RequestParam(defaultValue = "", required = false) String variantType) throws Exception {
//
//		RequestUtils utils = new RequestUtils(modelDAO);
//		AnnotationSearchResult result = utils.getGetAnnotationsByGeneAndVariant(gene, variant);
//		if (result != null) {
//			return result.createObjectJSON();
//		}
//		AjaxResponse response = new AjaxResponse();
//		response.setIsAllowed(true);
//		response.setSuccess(false);
//		response.setMessage("No annotations found");
//
//		return response.createObjectJSON();
//	}
	
	@RequestMapping(value = "/getAllClinicalTrials")
	@ResponseBody
	public String getAllClinicalTrials(Model model, HttpSession session) throws Exception {

		RequestUtils utils = new RequestUtils(modelDAO);
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		List<Annotation> clinicalTrials = utils.getAllClinicalTrials(response);
		if (clinicalTrials != null && !clinicalTrials.isEmpty()) {
			User user = ControllerUtil.getSessionUser(session);
			for (Annotation trial : clinicalTrials) {
				Annotation.init(trial, null, modelDAO); // format dates and add missing info
				if (trial.getUserId() == null || !trial.getUserId().equals(user.getUserId())) {
					trial.setCanEdit(false); //stop user from modifying someone else's annotation
				}
			}
			clinicalTrials.sort(new Comparator<Annotation>() {
				@Override
				public int compare(Annotation o1, Annotation o2) {
					return o2.getModifiedLocalDateTime().compareTo(o1.getModifiedLocalDateTime());
				}
				
			});
			response.setPayload(clinicalTrials);
		}
		else {
			response.setSuccess(false);
			response.setMessage("No clinical trials found");
		}
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/getAllSNPsForGene")
	@ResponseBody
	public String getAllSNPsForGene(Model model, HttpSession session, @RequestParam String geneId) throws Exception {

		RequestUtils utils = new RequestUtils(modelDAO);
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		List<Annotation> snpAnnotations = utils.getAllSNPsForGene(response, geneId);
		if (response.getSuccess() && snpAnnotations != null) {
			User user = ControllerUtil.getSessionUser(session);
			for (Annotation trial : snpAnnotations) {
				Annotation.init(trial, null, modelDAO); // format dates and add missing info
				if (trial.getUserId() == null || !trial.getUserId().equals(user.getUserId())) {
					trial.setCanEdit(false); //stop user from modifying someone else's annotation
				}
			}
			snpAnnotations.sort(new Comparator<Annotation>() {
				@Override
				public int compare(Annotation o1, Annotation o2) {
					return o2.getModifiedLocalDateTime().compareTo(o1.getModifiedLocalDateTime());
				}
				
			});
			response.setPayload(snpAnnotations);
		}
		else {
			response.setSuccess(false);
		}
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/commitCaseAgnosticAnnotations", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String commitCaseAgnosticAnnotations(Model model, HttpSession session, @RequestBody String annotations) throws Exception {
		User user = ControllerUtil.getSessionUser(session);
		RequestUtils utils = new RequestUtils(modelDAO);
		AjaxResponse response = new AjaxResponse();
		
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
		response = utils.commitAnnotation(response, userAnnotations);
		if (response.getSuccess()) {
			response.setUserPrefs(user.getUserPref());
		}
		return response.createObjectJSON();

	}
	
	@RequestMapping(value = "/getVariantsForGene", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getVariantsForGene(Model model, HttpSession session, @RequestParam String geneId,
			@RequestParam String annotationId) throws Exception {
		RequestUtils utils = new RequestUtils(modelDAO);
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		List<Variant> variants = utils.getVariantsForGene(response, geneId);
		if (variants != null && !variants.isEmpty()) {
			variants.sort(new Comparator<Variant>() {
				@Override
				public int compare(Variant o1, Variant o2) {
					return o1.getNotation().compareTo(o1.getNotation());
				}
			});
			response.setPayload(new VariantsForGeneItems(variants, annotationId));
		}
		else {
			response.setSuccess(false);
			response.setMessage("No variants found for gene " + geneId);
		}
		return response.createObjectJSON();
		
	}
}
