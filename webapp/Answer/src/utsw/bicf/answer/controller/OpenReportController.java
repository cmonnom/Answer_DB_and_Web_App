package utsw.bicf.answer.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
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
import utsw.bicf.answer.controller.serialization.vuetify.ExistingReportsSummary;
import utsw.bicf.answer.controller.serialization.vuetify.ReportSummary;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Report;
import utsw.bicf.answer.reporting.finalreport.FinalReportPDFTemplate;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.OtherProperties;
import utsw.bicf.answer.security.PermissionUtils;

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
		
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;
	@Autowired
	FileProperties fileProps;
	@Autowired
	OtherProperties otherProps;

	@RequestMapping("/openReport/{caseId}")
	public String openReport(Model model, HttpSession session, @PathVariable String caseId,
			@RequestParam(defaultValue="", required=false) String reportId
			) throws IOException, UnsupportedOperationException, URISyntaxException {
		String url = "openReport/" + caseId + "?reportId=" + reportId;
		User user = (User) session.getAttribute("user");
		model.addAttribute("urlRedirect", url);
		model.addAttribute("isProduction", fileProps.getProductionEnv());
//		RequestUtils utils = new RequestUtils(modelDAO);
//		if (user != null && !ControllerUtil.isUserAssignedToCase(utils, caseId, user)) {
//			return ControllerUtil.initializeModelNotAllowed(model, servletContext);
//		}
		
		return ControllerUtil.initializeModel(model, servletContext, user);
	}
	
	@RequestMapping("/openReportReadOnly/{caseId}")
	public String openReportReadOnly(Model model, HttpSession session, @PathVariable String caseId,
			@RequestParam(defaultValue="", required=false) String reportId) throws IOException, UnsupportedOperationException, URISyntaxException {
		String url = "openReportReadOnly/" + caseId + "?reportId=" + reportId;
		User user = (User) session.getAttribute("user");
		model.addAttribute("urlRedirect", url);
		model.addAttribute("isProduction", fileProps.getProductionEnv());
		return ControllerUtil.initializeModel(model, servletContext, user);
	}
	
	@RequestMapping(value = "/getExistingReports")
	@ResponseBody
	public String getExistingReports(Model model, HttpSession session, @RequestParam String caseId) throws Exception {

		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		List<Report> allReports = utils.getExistingReports(caseId);
		if (allReports != null) {
			List<ReportSummary> summaries = new ArrayList<ReportSummary>();
			for (Report r : allReports) {
				if (r.getCreatedBy() == null) {
					r.setCreatedBy(1);
				}
				if (r.getModifiedBy() == null) {
					r.setModifiedBy(1);
				}
				User createdBy = modelDAO.getUserByUserId(r.getCreatedBy());
				User modifiedBy = modelDAO.getUserByUserId(r.getModifiedBy());
				summaries.add(new ReportSummary(r, false, createdBy, modifiedBy));
			}
			return new ExistingReportsSummary(summaries).createObjectJSON();
		}
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		response.setSuccess(false);
		response.setMessage("There are no report for this case.");
		return response.createObjectJSON();
	}

	@RequestMapping(value = "/getReportDetails")
	@ResponseBody
	public String getReportDetails(Model model, HttpSession session, @RequestParam String caseId,
			@RequestParam(defaultValue="", required=false) String reportId) throws Exception {

		RequestUtils utils = new RequestUtils(modelDAO);
		Report reportDetails = null;
		if (reportId.equals("")) {
			reportDetails = utils.buildReportManually(caseId);
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
	
	@RequestMapping(value = "/saveReport")
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
		User currentUser = (User) session.getAttribute("user");
		JsonNode nodeData = mapper.readTree(data);
		ReportSummary reportSummary =  mapper.readValue(nodeData.get("report").toString(), ReportSummary.class);

		boolean isAssigned = ControllerUtil.isUserAssignedToCase(utils, reportSummary.getCaseId(), currentUser);
		boolean canProceed = isAssigned && currentUser.getIndividualPermission().getCanReview();
		response.setIsAllowed(canProceed);
		if (canProceed) {
			//handle 1st time save when no reportId
			String reportId = null;
			if(reportSummary.getMongoDBId() != null) {
				reportId = reportSummary.getMongoDBId().getOid();
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
					//update Notes
					reportToSave.setSummary(reportSummary.getSummary());
					//update Indicated Therapies
					reportToSave.setIndicatedTherapies(reportSummary.getIndicatedTherapySummary() != null ? reportSummary.getIndicatedTherapySummary().getItems() : null);
					//update CNV
					reportToSave.setCnvs(reportSummary.getCnvSummary() != null ? reportSummary.getCnvSummary().getItems() : null);
					//update FTL
					reportToSave.setTranslocations(reportSummary.getTranslocationSummary() != null ? reportSummary.getTranslocationSummary().getItems() : null);
					//update clinical significance
					reportToSave.setSnpVariantsStrongClinicalSignificance(reportSummary.getSnpVariantsStrongClinicalSignificance());
					reportToSave.setSnpVariantsPossibleClinicalSignificance(reportSummary.getSnpVariantsPossibleClinicalSignificance());
					reportToSave.setSnpVariantsUnknownClinicalSignificance(reportSummary.getSnpVariantsUnknownClinicalSignificance());
					//update clinical trials
					reportToSave.setClinicalTrials(reportSummary.getClinicalTrialsSummary() != null ? reportSummary.getClinicalTrialsSummary().getItems() : null);
				}
			}
			else {
				reportToSave = new Report(reportSummary);
			}
			utils.saveReport(response, reportToSave);
		}
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/previewReport")
	@ResponseBody
	public String previewReport(Model model, HttpSession session, @RequestBody String data) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		AjaxResponse response = new AjaxResponse();
		if (data == null || data.equals("")) {
			response.setIsAllowed(false);
			response.setSuccess(false);
			response.setMessage("No report provided");
		}
		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		User currentUser = (User) session.getAttribute("user");
		JsonNode nodeData = mapper.readTree(data);
		ReportSummary reportSummary =  mapper.readValue(nodeData.get("report").toString(), ReportSummary.class);
		boolean isAssigned = ControllerUtil.isUserAssignedToCase(utils, reportSummary.getCaseId(), currentUser);
		boolean canProceed = isAssigned && currentUser.getIndividualPermission().getCanReview();
		response.setIsAllowed(canProceed);
		if (canProceed) {
			OrderCase caseSummary = utils.getCaseSummary(reportSummary.getCaseId());
			Report reportToPreview = new Report(reportSummary);
			FinalReportPDFTemplate pdfReport = new FinalReportPDFTemplate(reportToPreview, fileProps, caseSummary, otherProps);
			pdfReport.saveTemp();

			String linkName = pdfReport.createPDFLink(fileProps);
			response.setSuccess(true);
			response.setMessage(linkName);
		}
		return response.createObjectJSON();
	}
	
}
