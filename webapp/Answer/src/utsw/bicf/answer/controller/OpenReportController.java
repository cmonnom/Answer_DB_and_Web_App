package utsw.bicf.answer.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.GeneVariantAndAnnotation;
import utsw.bicf.answer.controller.serialization.vuetify.ExistingReportsSummary;
import utsw.bicf.answer.controller.serialization.vuetify.ReportSummary;
import utsw.bicf.answer.dao.LoginDAO;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.ClinicalTest;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.Annotation;
import utsw.bicf.answer.model.extmapping.CNV;
import utsw.bicf.answer.model.extmapping.CNVReport;
import utsw.bicf.answer.model.extmapping.IndicatedTherapy;
import utsw.bicf.answer.model.extmapping.MongoDBId;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Report;
import utsw.bicf.answer.model.extmapping.TranslocationReport;
import utsw.bicf.answer.model.hybrid.ClinicalSignificance;
import utsw.bicf.answer.reporting.finalreport.FinalReportPDFTemplate;
import utsw.bicf.answer.reporting.finalreport.FinalReportTemplateConstants;
import utsw.bicf.answer.reporting.parse.BiomarkerTrialsRow;
import utsw.bicf.answer.reporting.parse.EncodingGlyphException;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.NCBIProperties;
import utsw.bicf.answer.security.OtherProperties;
import utsw.bicf.answer.security.PermissionUtils;
import utsw.bicf.answer.security.QcAPIAuthentication;

@Controller
@RequestMapping("/")
public class OpenReportController {

	static {
		PermissionUtils.addPermission(OpenReportController.class.getCanonicalName() + ".openReport",
				IndividualPermission.CAN_REVIEW);
		PermissionUtils.addPermission(OpenReportController.class.getCanonicalName() + ".openReportReadOnly",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenReportController.class.getCanonicalName() + ".getReportDetails",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenReportController.class.getCanonicalName() + ".getExistingReports",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(OpenReportController.class.getCanonicalName() + ".saveReport",
				IndividualPermission.CAN_REVIEW);
		PermissionUtils.addPermission(OpenReportController.class.getCanonicalName() + ".previewReport",
				IndividualPermission.CAN_REVIEW);
		PermissionUtils.addPermission(OpenReportController.class.getCanonicalName() + ".finalizeReport",
				IndividualPermission.CAN_REVIEW);
		PermissionUtils.addPermission(OpenReportController.class.getCanonicalName() + ".amendReport",
				IndividualPermission.CAN_REVIEW);
		PermissionUtils.addPermission(OpenReportController.class.getCanonicalName() + ".addendReport",
				IndividualPermission.CAN_REVIEW);
		PermissionUtils.addPermission(OpenReportController.class.getCanonicalName() + ".selectByPassCNVWarningAnnotation",
				IndividualPermission.CAN_REVIEW);
		PermissionUtils.addPermission(OpenReportController.class.getCanonicalName() + ".createPubMedFromId",
				IndividualPermission.CAN_REVIEW);
		PermissionUtils.addPermission(OpenReportController.class.getCanonicalName() + ".commitCNVAnnotationsBatch",
				IndividualPermission.CAN_REVIEW);
		PermissionUtils.addPermission(OpenReportController.class.getCanonicalName() + ".selectByPassCNVWarningAnnotationBatch",
				IndividualPermission.CAN_REVIEW);
//		PermissionUtils.addPermission(OpenReportController.class.getCanonicalName() + ".getPubmedDetails",
//				IndividualPermission.CAN_VIEW);
		
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
	NCBIProperties ncbiProps;
	@Autowired
	LoginDAO loginDAO;
	@Autowired
	QcAPIAuthentication qcAPI;

	@RequestMapping("/openReport/{caseId}")
	public String openReport(Model model, HttpSession session, @PathVariable String caseId,
			@RequestParam(defaultValue="", required=false) String reportId
			) throws IOException, UnsupportedOperationException, URISyntaxException {
		User user = ControllerUtil.getSessionUser(session);
		String url = "openReport/" + caseId + "?reportId=" + reportId;
		model.addAttribute("urlRedirect", url);
		RequestUtils utils = new RequestUtils(modelDAO);
		OrderCase caseSummary = utils.getCaseSummary(caseId);
		if (!ControllerUtil.areUserAndCaseInSameGroup(user, caseSummary)) {
			return ControllerUtil.initializeModelNotAllowed(model, servletContext);
		}
		if (caseSummary.getActive() == null || !caseSummary.getActive()) { //can't edit archived cases
			model.addAttribute("redirectReadOnlyUrl", url.replace("openReport/", "openReportReadOnly/"));
			return ControllerUtil.initializeModelNotAllowed(model, servletContext);
		}
		ControllerUtil.setGlobalVariables(model, fileProps, otherProps);
//		RequestUtils utils = new RequestUtils(modelDAO);
//		if (user != null && !ControllerUtil.isUserAssignedToCase(utils, caseId, user)) {
//			return ControllerUtil.initializeModelNotAllowed(model, servletContext);
//		}
		
		return ControllerUtil.initializeModel(model, servletContext, user, loginDAO);
	}
	
	@RequestMapping("/openReportReadOnly/{caseId}")
	public String openReportReadOnly(Model model, HttpSession session, @PathVariable String caseId,
			@RequestParam(defaultValue="", required=false) String reportId) throws IOException, UnsupportedOperationException, URISyntaxException {
		String url = "openReportReadOnly/" + caseId + "?reportId=" + reportId;
		User user = ControllerUtil.getSessionUser(session);
		model.addAttribute("urlRedirect", url);
		RequestUtils utils = new RequestUtils(modelDAO);
		OrderCase caseSummary = utils.getCaseSummary(caseId);
		if (!ControllerUtil.areUserAndCaseInSameGroup(user, caseSummary)) {
			return ControllerUtil.initializeModelNotAllowed(model, servletContext);
		}
		ControllerUtil.setGlobalVariables(model, fileProps, otherProps);
		return ControllerUtil.initializeModel(model, servletContext, user, loginDAO);
	}
	
	@RequestMapping(value = "/getExistingReports", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getExistingReports(Model model, HttpSession session, @RequestParam String caseId) throws Exception {

		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		User user = ControllerUtil.getSessionUser(session);
		OrderCase caseSummary = utils.getCaseSummary(caseId);
		if (!ControllerUtil.areUserAndCaseInSameGroup(user, caseSummary)) {
			return ControllerUtil.initializeModelNotAllowed(model, servletContext);
		}
		List<Report> allReports = utils.getExistingReports(caseId);
		if (allReports != null) {
			List<ReportSummary> summaries = new ArrayList<ReportSummary>();
			for (Report r : allReports) {
//				if (r.getCreatedBy() == null) {
//					r.setCreatedBy(1);
//				}
//				if (r.getModifiedBy() == null) {
//					r.setModifiedBy(1);
//				}
				User createdBy = modelDAO.getUserByUserId(r.getCreatedBy());
				User modifiedBy = modelDAO.getUserByUserId(r.getModifiedBy());
				summaries.add(new ReportSummary(r, false, createdBy, modifiedBy));
			}
			summaries.sort(new Comparator<ReportSummary>() {
				@Override
				public int compare(ReportSummary o1, ReportSummary o2) {
					return o2.getModifiedLocalDateTime().compareTo(o1.getModifiedLocalDateTime());
				}
				
			});
			return new ExistingReportsSummary(summaries).createObjectJSON();
		}
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		response.setSuccess(false);
		response.setMessage("There are no report for this case.");
		return response.createObjectJSON();
	}

	@RequestMapping(value = "/getReportDetails", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getReportDetails(Model model, HttpSession session, @RequestParam String caseId,
			@RequestParam(defaultValue="", required=false) String reportId,
			@RequestParam(defaultValue="", required=false) String test) throws Exception {

		RequestUtils utils = new RequestUtils(modelDAO, qcAPI);
		User user = ControllerUtil.getSessionUser(session);
		OrderCase caseSummary = utils.getCaseSummary(caseId);
		if (!ControllerUtil.areUserAndCaseInSameGroup(user, caseSummary)) {
			return ControllerUtil.initializeModelNotAllowed(model, servletContext);
		}
		Report reportDetails = null;
		if (reportId.equals("")) {
			reportDetails = utils.buildReportManually2(caseId, user, otherProps, ncbiProps);
		}
		else {
			reportDetails = utils.getReportDetails(reportId);
		}
		if (reportDetails != null) {
			User createdBy = modelDAO.getUserByUserId(reportDetails.getCreatedBy());
			User modifiedBy = modelDAO.getUserByUserId(reportDetails.getModifiedBy());
			ReportSummary summary = new ReportSummary(reportDetails, true, createdBy, modifiedBy);
			return summary.createObjectJSON();
		}
		return null;
	}
	
	@RequestMapping(value = "/saveReport", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String saveReport(Model model, HttpSession session, @RequestBody String data) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		AjaxResponse response = new AjaxResponse();
		if (data == null || data.equals("")) {
			response.setIsAllowed(false);
			response.setSuccess(false);
			response.setMessage("No report provided");
		}
		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		User currentUser = ControllerUtil.getSessionUser(session);
		JsonNode nodeData = mapper.readTree(data);
		ReportSummary reportSummary =  mapper.readValue(nodeData.get("report").toString(), ReportSummary.class);
		reportSummary.updateModifiedRows();
		boolean isAssigned = ControllerUtil.isUserAssignedToCase(utils, reportSummary.getCaseId(), currentUser);
		OrderCase caseSummary = utils.getCaseSummary(reportSummary.getCaseId());
		if (!ControllerUtil.areUserAndCaseInSameGroup(currentUser, caseSummary)) {
			return ControllerUtil.initializeModelNotAllowed(model, servletContext);
		}
		//check if there is an oncotree code
		if (caseSummary != null && 
				(caseSummary.getOncotreeDiagnosis() == null 
				|| caseSummary.getOncotreeDiagnosis().equals(""))) {
			response.setMessage("A report cannot be saved  without an OncoTree Diagnosis");
			return response.createObjectJSON();
		}
		boolean canProceed = isAssigned && currentUser.getIndividualPermission().getCanReview();
		response.setIsAllowed(canProceed);
		if (canProceed) {
			//handle 1st time save when no reportId
			String reportId = null;
			if(reportSummary.getMongoDBId() != null) {
				reportId = reportSummary.getMongoDBId().getOid();
			}
			List<Report> existingReports = utils.getExistingReports(reportSummary.getCaseId());
			 //check that the new report doesn't have the same name as an existing one
			if (existingReports != null && reportId == null) {
				List<String> reportNames = existingReports.stream().map(r -> r.getReportName()).collect(Collectors.toList());
				if (reportNames.contains(reportSummary.getReportName())) {
					String[] newReportNameParts = reportSummary.getReportName().split("-");
					StringBuilder newReportName = new StringBuilder();
					newReportName.append(newReportNameParts[0]);
					if (newReportNameParts.length > 1) {
						newReportName.append("-");
						newReportName.append(newReportNameParts[1]);
					}
					//append counter
					newReportName.append("-").append(existingReports.size() + 1);
					reportSummary.setReportName(newReportName.toString());
//					response.setSuccess(false);
//					response.setMessage("You cannot name a new report after an existing one.");
//					return response.createObjectJSON();
				}
			}
			Report reportToSave = null;
			if (reportId != null) {
				reportToSave = utils.getReportDetails(reportId);
				if (reportToSave == null) {
					response.setSuccess(false);
					response.setMessage("Invalid Report");
				}
				else if (!reportToSave.getReportName().equals(reportSummary.getReportName())) {
					reportSummary.setMongoDBId(null);
					reportToSave = new Report(reportSummary); //names are different, create a new report
				}
				else {
					if (reportToSave.getAddendum() == null || reportToSave.getAddendum()) {
						//nothing special, just update all fields
						//update Notes
						reportToSave.setSummary(reportSummary.getSummary());
						//update Indicated Therapies
						reportToSave.setIndicatedTherapies(reportSummary.getIndicatedTherapySummary() != null ? reportSummary.getIndicatedTherapySummary().getItems() : null);
						//update CNV
						reportToSave.setCnvs(reportSummary.getCnvSummary() != null ? reportSummary.getCnvSummary().getItems() : null);
						//update FTL
						reportToSave.setTranslocations(reportSummary.getTranslocationSummary() != null ? reportSummary.getTranslocationSummary().getItems() : null);
						//update clinical significance
						if (reportSummary.getSnpVariantsStrongClinicalSignificanceSummary() != null) {
							for (ClinicalSignificance cs : reportSummary.getSnpVariantsStrongClinicalSignificanceSummary().getItems()) {
								reportSummary.getSnpVariantsStrongClinicalSignificance().get(cs.getGeneVariantAsKey()).getAnnotationsByCategory().put(cs.getCategory(), cs.getAnnotation());
							}
							reportToSave.setSnpVariantsStrongClinicalSignificance(reportSummary.getSnpVariantsStrongClinicalSignificance());
						}
						if (reportSummary.getSnpVariantsPossibleClinicalSignificanceSummary() != null) {
							for (ClinicalSignificance cs : reportSummary.getSnpVariantsPossibleClinicalSignificanceSummary().getItems()) {
								reportSummary.getSnpVariantsPossibleClinicalSignificance().get(cs.getGeneVariantAsKey()).getAnnotationsByCategory().put(cs.getCategory(), cs.getAnnotation());
							}
							reportToSave.setSnpVariantsPossibleClinicalSignificance(reportSummary.getSnpVariantsPossibleClinicalSignificance());
						}
						if (reportSummary.getSnpVariantsUnknownClinicalSignificanceSummary() != null) {
							for (ClinicalSignificance cs : reportSummary.getSnpVariantsUnknownClinicalSignificanceSummary().getItems()) {
								reportSummary.getSnpVariantsUnknownClinicalSignificance().get(cs.getGeneVariantAsKey()).getAnnotationsByCategory().put(cs.getCategory(), cs.getAnnotation());
							}
							reportToSave.setSnpVariantsUnknownClinicalSignificance(reportSummary.getSnpVariantsUnknownClinicalSignificance());
						}
						//update clinical trials
						reportToSave.setClinicalTrials(reportSummary.getClinicalTrialsSummary() != null ? reportSummary.getClinicalTrialsSummary().getItems() : null);
					}
					else { //the report was addended. Skip existing fields
						//update Indicated Therapies
						if (reportSummary.getIndicatedTherapySummary() != null) {
							for (IndicatedTherapy t : reportSummary.getIndicatedTherapySummary().getItems()) {
								List<IndicatedTherapy> existingTherapies = reportToSave.getIndicatedTherapies().stream().filter(i -> i.getOid().equals(i.getOid()) && !i.isReadonly()).collect(Collectors.toList());
								if (existingTherapies.size() == 1) {
									existingTherapies.get(0).setIndication(t.getIndication());
								}
							}
						}
						//update CNVs
						if (reportSummary.getCnvSummary() != null) {
							for (CNVReport c : reportSummary.getCnvSummary().getItems()) {
								List<CNVReport> existingCNVs = reportToSave.getCnvs().stream().filter(cnv -> cnv.getMongoDBId().getOid().equals(c.getMongoDBId().getOid()) && !cnv.isReadonly()).collect(Collectors.toList());
								if (existingCNVs.size() == 1) {
									existingCNVs.get(0).setComment(c.getComment());
								}
							}
						}
						//update FTLs
						if (reportSummary.getTranslocationSummary() != null) {
							for (TranslocationReport t : reportSummary.getTranslocationSummary().getItems()) {
								List<TranslocationReport> existingFTLs = reportToSave.getTranslocations().stream().filter(ftl -> ftl.getMongoDBId().getOid().equals(t.getMongoDBId().getOid()) && !ftl.isReadonly()).collect(Collectors.toList());
								if (existingFTLs.size() == 1) {
									existingFTLs.get(0).setComment(t.getComment());
								}
							}
						}
						//update clinical significance
						if (reportSummary.getSnpVariantsStrongClinicalSignificance() != null) {
							for (String variant : reportSummary.getSnpVariantsStrongClinicalSignificance().keySet()) {
								GeneVariantAndAnnotation gva = reportToSave.getSnpVariantsStrongClinicalSignificance().get(variant);
								if (gva != null && !gva.isReadonly()) {
									reportToSave.getSnpVariantsStrongClinicalSignificance().put(variant, gva);
								}
							}
						}
						if (reportSummary.getSnpVariantsPossibleClinicalSignificance() != null) {
							for (String variant : reportSummary.getSnpVariantsPossibleClinicalSignificance().keySet()) {
								GeneVariantAndAnnotation gva = reportToSave.getSnpVariantsPossibleClinicalSignificance().get(variant);
								if (gva != null && !gva.isReadonly()) {
									reportToSave.getSnpVariantsPossibleClinicalSignificance().put(variant, gva);
								}
							}
						}
						if (reportSummary.getSnpVariantsUnknownClinicalSignificance() != null) {
							for (String variant : reportSummary.getSnpVariantsUnknownClinicalSignificance().keySet()) {
								GeneVariantAndAnnotation gva = reportToSave.getSnpVariantsUnknownClinicalSignificance().get(variant);
								if (gva != null && !gva.isReadonly()) {
									reportToSave.getSnpVariantsUnknownClinicalSignificance().put(variant, gva);
								}
							}
						}
						if (reportSummary.getClinicalTrialsSummary() != null) {
							for (BiomarkerTrialsRow row : reportSummary.getClinicalTrialsSummary().getItems()) {
								List<BiomarkerTrialsRow> existingRows = reportToSave.getClinicalTrials().stream().filter(t -> t.getNctid().equals(row.getNctid()) && !t.isReadonly()).collect(Collectors.toList());
								if (existingRows.size() == 1) {
									existingRows.get(0).setIsSelected(row.getIsSelected());
								}
							}
						}
						
					}
				}
			}
			else {
				reportToSave = new Report(reportSummary);
			}
			if ((reportToSave.getFinalized() == null || !reportToSave.getFinalized()) &&
					(reportToSave.getAmended() == null || !reportToSave.getAmended())) {
				reportToSave.buildSummaryTable2(); //rebuild the summary in case new rows have been added.
				utils.saveReport(response, reportToSave); //can still save if not finalized and not amended
			}
			else {
				response.setSuccess(false);
				response.setMessage("You cannot modify a finalized report. Use an amendment or an addendum");
			}
		}
		else { //cannot proceed
			response.setMessage("You are not allowed to save this report");
		}
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/previewReport", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String previewReport(Model model, HttpSession session, @RequestBody String data) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,	true);
		AjaxResponse response = new AjaxResponse();
		data = data.replaceAll("\\\\t", " ").replaceAll("\\\\n", "<br/>");
		if (data == null || data.equals("")) {
			response.setIsAllowed(false);
			response.setSuccess(false);
			response.setMessage("No report provided");
		}
		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		User currentUser = ControllerUtil.getSessionUser(session);
		JsonNode nodeData = mapper.readTree(data);
		ReportSummary reportSummary =  mapper.readValue(nodeData.get("report").toString(), ReportSummary.class);
		OrderCase caseSummary = utils.getCaseSummary(reportSummary.getCaseId());
		if (!ControllerUtil.areUserAndCaseInSameGroup(currentUser, caseSummary)) {
			return ControllerUtil.initializeModelNotAllowed(model, servletContext);
		}
		reportSummary.updateModifiedRows();
		//we might not want to restrict by assigned user
		//		boolean isAssigned = ControllerUtil.isUserAssignedToCase(utils, reportSummary.getCaseId(), currentUser);
//		boolean canProceed = isAssigned && currentUser.getIndividualPermission().getCanReview();
		boolean canProceed = (currentUser.getIndividualPermission().getAdmin() != null && currentUser.getIndividualPermission().getAdmin()) || currentUser.getIndividualPermission().getCanReview();
		response.setIsAllowed(canProceed);
		if (canProceed) {
			Report reportToPreview = new Report(reportSummary);
			User signedBy = modelDAO.getUserByUserId(reportToPreview.getModifiedBy());
			String linkName = null;
			if (reportToPreview.getFinalized() == null || !reportToPreview.getFinalized()) {
				reportToPreview.buildSummaryTable2(); //rebuild the summary in case new rows have been added.
			}
			else if (reportToPreview.getFinalized() != null && reportToPreview.getFinalized()) {
				//fetch the report from pdf/finalized directory
				linkName = FinalReportPDFTemplate.createPDFLinkWithoutReport(fileProps, reportToPreview.getMongoDBId().getOid());
			}
			if (linkName == null) { //otherwise, generate the report.
				try {
					ClinicalTest clinicalTest = modelDAO.getClinicalTest(caseSummary.getLabTestName());
					if (clinicalTest == null) {
						clinicalTest = modelDAO.getClinicalTest(FinalReportTemplateConstants.DEFAULT_TITLE);
					}
					FinalReportPDFTemplate pdfReport = new FinalReportPDFTemplate(reportToPreview, fileProps, caseSummary, otherProps, signedBy, clinicalTest);
					pdfReport.saveTemp();
					linkName = pdfReport.createPDFLink(fileProps);
					
				}catch (EncodingGlyphException e) {
					response.setSuccess(false);
					response.setMessage(e.getMessage());
				}
			}
			response.setSuccess(true);
			response.setMessage(linkName);
		}
		else {
			response.setMessage("You are not allowed to preview PDF reports.");
		}
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/finalizeReport", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String finalizeReport(Model model, HttpSession session,
			@RequestParam String reportId) throws Exception {

		RequestUtils utils = new RequestUtils(modelDAO);
		User user = ControllerUtil.getSessionUser(session);
		AjaxResponse response = new AjaxResponse();
		if (reportId.equals("")) {
			response.setSuccess(false);
			response.setIsAllowed(false);
			response.setMessage("No report id provided");
			return response.createObjectJSON();
		}
		Report reportDetails = utils.getReportDetails(reportId);
		if (reportDetails == null) {
			response.setSuccess(false);
			response.setIsAllowed(false);
			response.setMessage("Invalid report id: " + reportId);
			return response.createObjectJSON();
			
		}
		OrderCase caseSummary = utils.getCaseSummary(reportDetails.getCaseId());
		if (!ControllerUtil.areUserAndCaseInSameGroup(user, caseSummary)) {
			return ControllerUtil.initializeModelNotAllowed(model, servletContext);
		}
		//make sure the report is not finalized already
		if (reportDetails.getFinalized() != null && reportDetails.getFinalized()) {
			response.setSuccess(false);
			response.setMessage("This report has already been finalized.");
		}
		else{
			//make sure no other report was finalized for this case
			List<Report> existingReports = utils.getExistingReports(reportDetails.getCaseId());
			boolean alreadyFinalized = false; 
			for (Report r: existingReports) {
				if (r.getFinalized() != null 
						&& r.getFinalized() && (r.getAmended() == null || !r.getAmended()) //finalized report but not amended
						&& r.getMongoDBId().getOid().equals(reportDetails.getMongoDBId().getOid())) {
					//another report is already finalized
					alreadyFinalized = true;
					break;
				}
			}
			if (alreadyFinalized) {
				response.setSuccess(false);
				response.setIsAllowed(false);
				response.setMessage("Another report is already finalized. You can only have one report finalized per case.");
			}
			else {
				utils.finalizeReport(response, reportDetails);
				if (response.getSuccess()) {
					reportDetails = utils.getReportDetails(reportDetails.getMongoDBId().getOid());
					reportDetails.getIndicatedTherapies().stream().forEach(t -> t.setReadonly(true));
					reportDetails.getClinicalTrials().stream().forEach(t -> t.setReadonly(true));
					reportDetails.getSnpVariantsStrongClinicalSignificance().values().stream().forEach(v -> v.setReadonly(true));
					reportDetails.getSnpVariantsPossibleClinicalSignificance().values().stream().forEach(v -> v.setReadonly(true));
					reportDetails.getSnpVariantsUnknownClinicalSignificance().values().stream().forEach(v -> v.setReadonly(true));
					reportDetails.getCnvs().stream().forEach(c -> c.setReadonly(true));
					reportDetails.getTranslocations().stream().forEach(t -> t.setReadonly(true));
					utils.saveReport(response, reportDetails);

					//save a copy of the pdf
					User signedBy = modelDAO.getUserByUserId(reportDetails.getModifiedBy());
					ClinicalTest clinicalTest = modelDAO.getClinicalTest(caseSummary.getLabTestName());
					if (clinicalTest == null) {
						clinicalTest = modelDAO.getClinicalTest(FinalReportTemplateConstants.DEFAULT_TITLE);
					}
					String possibleDirtyData = reportDetails.createObjectJSON();
					String cleanData = possibleDirtyData.replaceAll("\\\\t", " ").replaceAll("\\\\n", "<br/>");
					ObjectMapper mapper = new ObjectMapper();
					mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,	true);
					reportDetails = mapper.readValue(cleanData, Report.class);
					FinalReportPDFTemplate pdfReport = new FinalReportPDFTemplate(reportDetails, fileProps, caseSummary, otherProps, signedBy, clinicalTest);
					pdfReport.saveFinalized();
				}
			}

		}
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/amendReport")
	@ResponseBody
	public String amendReport(Model model, HttpSession session, @RequestParam String reportId, 
			@RequestBody String reason) throws Exception {

		AjaxResponse response = new AjaxResponse();
		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		User currentUser = ControllerUtil.getSessionUser(session);
		
		boolean isAssigned = ControllerUtil.isUserAssignedToCase(utils, reportId, currentUser);
		boolean canProceed = isAssigned && currentUser.getIndividualPermission().getCanReview();
		response.setIsAllowed(canProceed);
		if (canProceed) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode nodeData = mapper.readTree(reason);
			String reasonString =  nodeData.get("reason").textValue();
			Report reportToSave = null;
			User user = ControllerUtil.getSessionUser(session);
			if (reportId != null) {
				reportToSave = utils.getReportDetails(reportId);
				if (reportToSave == null) {
					response.setSuccess(false);
					response.setMessage("Invalid Report");
				}
				else if (reportToSave != null) {
					OrderCase caseSummary = utils.getCaseSummary(reportToSave.getCaseId());
					if (!ControllerUtil.areUserAndCaseInSameGroup(user, caseSummary)) {
						return ControllerUtil.initializeModelNotAllowed(model, servletContext);
					}
				}
				if (reportToSave.getFinalized() == null || !reportToSave.getFinalized()) {
					response.setSuccess(false);
					response.setMessage("You can only amend finalized reports.");
				}
				else if (reportToSave.getAmended() != null && reportToSave.getAmended()) {
					response.setSuccess(false);
					response.setMessage("This report as already been amended.");
				}
				else if (reason == null || reason.equals("")) {
					response.setSuccess(false);
					response.setMessage("Please provide a reason for the amendment.");
				}
				else {
					reportToSave.setAmended(true);
					reportToSave.setAmendmentReason(reasonString);
					utils.saveReport(response, reportToSave);
				}
					
			}
		}
		return response.createObjectJSON();
	}

	@RequestMapping(value = "/addendReport", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String addendReport(Model model, HttpSession session, @RequestParam String reportId) throws Exception {

		AjaxResponse response = new AjaxResponse();
		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO, qcAPI);
		User currentUser = ControllerUtil.getSessionUser(session);

		boolean isAssigned = ControllerUtil.isUserAssignedToCase(utils, reportId, currentUser);
		boolean canProceed = isAssigned && currentUser.getIndividualPermission().getCanReview();
		response.setIsAllowed(canProceed);
		if (canProceed) {
			Report reportToSave = null;
			if (reportId != null) {
				reportToSave = utils.getReportDetails(reportId);
				if (reportToSave == null) {
					response.setSuccess(false);
					response.setMessage("Invalid Report");
				}
				else {
					OrderCase caseSummary = utils.getCaseSummary(reportToSave.getCaseId());
					if (!ControllerUtil.areUserAndCaseInSameGroup(currentUser, caseSummary)) {
						return ControllerUtil.initializeModelNotAllowed(model, servletContext);
					}
				}
				if (reportToSave.getFinalized() == null || !reportToSave.getFinalized()) {
					response.setSuccess(false);
					response.setMessage("You can only addend finalized reports.");
				}
				else {
					reportToSave.setMongoDBId(null); //this will create a new report
					reportToSave.setFinalized(false);
					reportToSave.setDateFinalized(null);
					reportToSave.setAddendum(true);
					Report newReport = utils.buildReportManually2(reportToSave.getCaseId(), currentUser, otherProps, ncbiProps);
					List<BiomarkerTrialsRow> existingTrials = reportToSave.getClinicalTrials();
					existingTrials.stream().forEach(t -> t.setReadonly(true));
					List<String> existingNCTIDs = existingTrials.stream().map(t -> t.getNctid()).collect(Collectors.toList());
					for (BiomarkerTrialsRow trial : newReport.getClinicalTrials()) {
						if (!existingNCTIDs.contains(trial.getNctid())) {
							existingTrials.add(trial);
						}
					}
					//TODO go through the other tables and update the variant ids
//					IndicatedTherapy
					List<IndicatedTherapy> existingTherapies = reportToSave.getIndicatedTherapies();
					existingTherapies.stream().forEach(t -> t.setReadonly(true));
					List<String> existingTherapyVariants = existingTherapies.stream().map(t -> t.getVariant()).collect(Collectors.toList());
					for (IndicatedTherapy therapy : newReport.getIndicatedTherapies()) {
						if (!existingTherapyVariants.contains(therapy.getVariant())) {
							existingTherapies.add(therapy);
							reportToSave.incrementIndicatedTherapyCount(therapy.getVariant());
							if (therapy.getType().equals("snp")) {
								reportToSave.getSnpIds().add(therapy.getOid());
							}
							else if (therapy.getType().equals("cnv")) {
								reportToSave.getCnvIds().add(therapy.getOid());
							}
							else if (therapy.getType().equals("translocation")) {
								reportToSave.getFtlIds().add(therapy.getOid());
							}
							else if (therapy.getType().equals("virus")) {
								reportToSave.getVirusIds().add(therapy.getOid());
							}
						}
					}
//					Strong significance
					Map<String, GeneVariantAndAnnotation> existingStrongByVariant = reportToSave.getSnpVariantsStrongClinicalSignificance();
					existingStrongByVariant.values().stream().forEach(t -> t.setReadonly(true));
					Set<String> existingStrongVariants = existingStrongByVariant.keySet();
					for (String strong : newReport.getSnpVariantsStrongClinicalSignificance().keySet()) {
						if (!existingStrongVariants.contains(strong)) {
							GeneVariantAndAnnotation v = newReport.getSnpVariantsStrongClinicalSignificance().get(strong);
							existingStrongByVariant.put(strong, v);
							reportToSave.incrementStrongClinicalSignificanceCount(v.getGene());
							reportToSave.getSnpIds().add(v.getOid());
						}
					}
//					Possible significance
					Map<String, GeneVariantAndAnnotation> existingPossibleByVariant = reportToSave.getSnpVariantsPossibleClinicalSignificance();
					existingPossibleByVariant.values().stream().forEach(t -> t.setReadonly(true));
					Set<String> existingPossibleVariants = existingPossibleByVariant.keySet();
					for (String possible : newReport.getSnpVariantsPossibleClinicalSignificance().keySet()) {
						if (!existingPossibleVariants.contains(possible)) {
							GeneVariantAndAnnotation v = newReport.getSnpVariantsPossibleClinicalSignificance().get(possible);
							existingPossibleByVariant.put(possible, v);
							reportToSave.incrementPossibleClinicalSignificanceCount(v.getGene());
							reportToSave.getSnpIds().add(v.getOid());
						}
					}					
//					Unknown significance
					Map<String, GeneVariantAndAnnotation> existingUnknownByVariant = reportToSave.getSnpVariantsUnknownClinicalSignificance();
					existingUnknownByVariant.values().stream().forEach(t -> t.setReadonly(true));
					Set<String> existingUnknownVariants = existingUnknownByVariant.keySet();
					for (String unknown : newReport.getSnpVariantsUnknownClinicalSignificance().keySet()) {
						if (!existingUnknownVariants.contains(unknown)) {
							GeneVariantAndAnnotation v = newReport.getSnpVariantsUnknownClinicalSignificance().get(unknown);
							existingUnknownByVariant.put(unknown, v);
							reportToSave.incrementUnknownClinicalSignificanceCount(v.getGene());
							reportToSave.getSnpIds().add(v.getOid());
						}
					}
					
					//CNV
					List<CNVReport> existingCNVs = reportToSave.getCnvs();
					existingCNVs.stream().forEach(t -> t.setReadonly(true));
					List<String> existingCNVVariants = existingCNVs.stream().map(c -> c.getMongoDBId().getOid()).collect(Collectors.toList());
					for (CNVReport cnv : newReport.getCnvs()) {
						if (!existingCNVVariants.contains(cnv.getMongoDBId().getOid())) {
							existingCNVs.add(cnv);
							reportToSave.getCnvIds().add(cnv.getMongoDBId().getOid());
						}
					}
					
					//FTL
					List<TranslocationReport> existingFTLs = reportToSave.getTranslocations();
					existingFTLs.stream().forEach(t -> t.setReadonly(true));
					List<String> existingFTLVariants = existingFTLs.stream().map(c -> c.getMongoDBId().getOid()).collect(Collectors.toList());
					for (TranslocationReport ftl : newReport.getTranslocations()) {
						if (!existingFTLVariants.contains(ftl.getMongoDBId().getOid())) {
							existingFTLs.add(ftl);
							reportToSave.getFtlIds().add(ftl.getMongoDBId().getOid());
						}
					}
					
					utils.saveReport(response, reportToSave);
				}

			}
		}
		return response.createObjectJSON();
	}
	
	/**
	 * When a user chose to bypass the CNV warning,
	 * use this method to toggle the proper annotation
	 * to go into the report
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	@RequestMapping(value = "/selectByPassCNVWarningAnnotation", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String selectByPassCNVWarningAnnotation(Model model, HttpSession session,
			@RequestParam String caseId, @RequestParam String variantId) throws ClientProtocolException, IOException, URISyntaxException {
		
		AjaxResponse response = new AjaxResponse();
		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		User currentUser = ControllerUtil.getSessionUser(session);
		OrderCase caseSummary = utils.getCaseSummary(caseId);
		boolean isAssigned = ControllerUtil.isUserAssignedToCase(caseSummary, currentUser);
		if (!ControllerUtil.areUserAndCaseInSameGroup(currentUser, caseSummary)) {
			return ControllerUtil.initializeModelNotAllowed(model, servletContext);
		}
		boolean canProceed = isAssigned && currentUser.getIndividualPermission().getCanReview();
		response.setIsAllowed(canProceed);
		if (canProceed) {
			//get variant details
			CNV cnv = utils.getCNVDetails(variantId);
			for (Annotation annotation : cnv.getReferenceCnv().getUtswAnnotations()) {
				if (annotation.getText().equals("Uncertain Clinical Significance")) {
					//add the proper annotation to annotationIdsForReporting
					List<MongoDBId> selectedAnnotations = new ArrayList<MongoDBId>();
					selectedAnnotations.add(annotation.getMongoDBId());
					cnv.setAnnotationIdsForReporting(selectedAnnotations);
					break; //only need the first auto generated annotation
				}
			}
			//save selectedAnnotations
			utils.saveSelectedAnnotations(response, cnv, "cnv", variantId);
		}
		else {
			response.setMessage("You're not assigned to this case");
		}
		return response.createObjectJSON();
	}
	
	/**
	 * When a user chose to bypass the CNV warning,
	 * use this method to toggle the proper annotation
	 * to go into the report
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	@RequestMapping(value = "/selectByPassCNVWarningAnnotationBatch", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String selectByPassCNVWarningAnnotationBatch(Model model, HttpSession session,
			@RequestParam String caseId, @RequestBody String data) throws ClientProtocolException, IOException, URISyntaxException {
		
		AjaxResponse response = new AjaxResponse();
		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		User currentUser = ControllerUtil.getSessionUser(session);
		OrderCase caseSummary = utils.getCaseSummary(caseId);
		boolean isAssigned = ControllerUtil.isUserAssignedToCase(caseSummary, currentUser);
		if (!ControllerUtil.areUserAndCaseInSameGroup(currentUser, caseSummary)) {
			return ControllerUtil.initializeModelNotAllowed(model, servletContext);
		}
		boolean canProceed = isAssigned && currentUser.getIndividualPermission().getCanReview();
		response.setIsAllowed(canProceed);
		if (canProceed) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode dataNodes = mapper.readTree(data);
			for (JsonNode annotationNode : dataNodes.get("variantIds")) {
				String variantId = annotationNode.textValue();
				CNV cnv = utils.getCNVDetails(variantId);
				for (Annotation annotation : cnv.getReferenceCnv().getUtswAnnotations()) {
					if (annotation.getText().equals("Uncertain Clinical Significance")) {
						//add the proper annotation to annotationIdsForReporting
						List<MongoDBId> selectedAnnotations = new ArrayList<MongoDBId>();
						selectedAnnotations.add(annotation.getMongoDBId());
						cnv.setAnnotationIdsForReporting(selectedAnnotations);
						break; //only need the first auto generated annotation
					}
				}
				//save selectedAnnotations
				utils.saveSelectedAnnotations(response, cnv, "cnv", variantId);
				if (!response.getSuccess()) {
					return response.createObjectJSON();
				}
			}
		}
		else {
			response.setMessage("You're not assigned to this case");
		}
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/commitCNVAnnotationsBatch", produces= "application/json; charset=utf-8", method= RequestMethod.POST)
	@ResponseBody
	public String commitCNVAnnotationsBatch(Model model, HttpSession session, @RequestBody String data,
			@RequestParam String caseId) throws Exception {
		User user = ControllerUtil.getSessionUser(session);
		RequestUtils utils = new RequestUtils(modelDAO);
		OrderCase caseSummary = utils.getCaseSummary(caseId);
		AjaxResponse response = new AjaxResponse();
		if (!caseId.equals("")) { //for annotations within a case
			if (!ControllerUtil.isUserAssignedToCase(caseSummary, user)) {
				// user is not assigned to this case
				response.setIsAllowed(false);
				response.setSuccess(false);
				response.setMessage(user.getFullName() + " is not assigned to this case");
				return response.createObjectJSON();
			}
			if (!ControllerUtil.areUserAndCaseInSameGroup(user, caseSummary)) {
				return ControllerUtil.returnFailedGroupCheck();
			}
		}
		
		response.setIsAllowed(true);

		List<Annotation> userAnnotations = new ArrayList<Annotation>();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode dataNodes = mapper.readTree(data);
		for (JsonNode annotationNode : dataNodes.get("annotations")) {
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
			if (userAnnotation.getIsVariantSpecific()) {
				userAnnotation.setVariantId(userAnnotation.getVariantId());
			}
			if (userAnnotation.getIsTumorSpecific()) {
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
			if (userAnnotation.getCnvGenes() != null && !userAnnotation.getCnvGenes().isEmpty() ) {
				Collections.sort(userAnnotation.getCnvGenes()); //sort genes alphabetically
			}
			userAnnotations.add(userAnnotation);
		}
		response = utils.commitAnnotation(response, userAnnotations);
		if (response.getSuccess()) {
			response.setPayload(userAnnotations.stream().map(a -> a.getVariantId()).collect(Collectors.toSet())); //to pass the variant ids back to the UI
		}
		return response.createObjectJSON();

	}
}
