package utsw.bicf.answer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.vuetify.AllOrderCasesSummary;
import utsw.bicf.answer.controller.serialization.vuetify.OrderCaseAllSummary;
import utsw.bicf.answer.controller.serialization.vuetify.OrderCaseFinalizedSummary;
import utsw.bicf.answer.controller.serialization.vuetify.OrderCaseForUserSummary;
import utsw.bicf.answer.controller.serialization.vuetify.ReportSummary;
import utsw.bicf.answer.controller.serialization.vuetify.Summary;
import utsw.bicf.answer.controller.serialization.vuetify.UserSearchItems;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.CaseHistory;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Report;
import utsw.bicf.answer.model.hybrid.HeaderOrder;
import utsw.bicf.answer.model.hybrid.OrderCaseAll;
import utsw.bicf.answer.model.hybrid.OrderCaseFinalized;
import utsw.bicf.answer.model.hybrid.OrderCaseForUser;
import utsw.bicf.answer.reporting.finalreport.FinalReportPDFTemplate;
import utsw.bicf.answer.reporting.parse.EncodingGlyphException;
import utsw.bicf.answer.security.EmailProperties;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.NotificationUtils;
import utsw.bicf.answer.security.OtherProperties;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class HomeController {
	
	static {
		PermissionUtils.addPermission(HomeController.class.getCanonicalName() + ".home", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(HomeController.class.getCanonicalName() + ".getWorklists", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(HomeController.class.getCanonicalName() + ".assignToUser", IndividualPermission.CAN_ASSIGN);
		PermissionUtils.addPermission(HomeController.class.getCanonicalName() + ".getAllUsersToAssign", IndividualPermission.CAN_ASSIGN);
		PermissionUtils.addPermission(HomeController.class.getCanonicalName() + ".createPDFReport", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(HomeController.class.getCanonicalName() + ".toggleArchivingStatusForCase", IndividualPermission.CAN_REVIEW);
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;
	@Autowired
	EmailProperties emailProps;
	@Autowired
	FileProperties fileProps;
	@Autowired
	OtherProperties otherProps;

	@RequestMapping("/home")
	public String home(Model model, HttpSession session) throws IOException {
		model.addAttribute("urlRedirect", "home");
		ControllerUtil.setGlobalVariables(model, fileProps, otherProps);
		User user = ControllerUtil.getSessionUser(session);
		return ControllerUtil.initializeModel(model, servletContext, user);
	}
	
	@RequestMapping(value = "/getWorklists")
	@ResponseBody
	public String getWorklists(Model model, HttpSession session)
			throws Exception {

		User user = ControllerUtil.getSessionUser(session);
		
		//send user to Ben's API to retrieve all active cases
		RequestUtils utils = new RequestUtils(modelDAO);
		OrderCase[] cases = utils.getActiveCases();
		List<OrderCase> caseList = new ArrayList<OrderCase>();
		
		if (cases != null) {
			List<User> users  = modelDAO.getAllUsers();
			for (OrderCase c : cases) {
				caseList.add(c);
			}
			//filter by assigned/user/available
			List<OrderCaseForUser> casesForUser = 
					caseList.stream()
					.filter(c -> c.getAssignedTo() != null && c.getAssignedTo().contains(user.getUserId().toString())
							&& c.getActive() != null && c.getActive())
					.map(c -> new OrderCaseForUser(c, user))
					.collect(Collectors.toList());
			
			List<OrderCaseAll> casesAll = 
					caseList.stream()
					.map(c -> new OrderCaseAll(modelDAO, c, users, user))
					.collect(Collectors.toList());
			List<OrderCaseFinalized> casesFinalized = 
					caseList.stream()
					.filter(c -> CaseHistory.lastStepMatches(c, CaseHistory.STEP_FINALIZED)
							&& c.getActive() != null && c.getActive())
					.map(c -> new OrderCaseFinalized(modelDAO, c, users, user))
					.collect(Collectors.toList());
			List<HeaderOrder> headerOrdersAll = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, "All Cases");
			OrderCaseAllSummary allSummary = new OrderCaseAllSummary(casesAll, user, headerOrdersAll);
			List<HeaderOrder> headerOrdersUser = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, "My Cases");
			OrderCaseForUserSummary forUserSummary = new OrderCaseForUserSummary(casesForUser, headerOrdersUser);
			List<HeaderOrder> headerOrdersFinalized = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, "Cases Finalized");
			OrderCaseFinalizedSummary finalizedSummary = new OrderCaseFinalizedSummary(casesFinalized, user, headerOrdersFinalized);
			
			AllOrderCasesSummary summary = new AllOrderCasesSummary(allSummary, forUserSummary, finalizedSummary);
			summary.setSuccess(true);
			return summary.createVuetifyObjectJSON();
		}
		AjaxResponse response = new AjaxResponse();
		response.setSuccess(false);
		response.setMessage("No cases found.");
		
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/getAllUsersToAssign")
	@ResponseBody
	public String getAllUsersToAssign(Model model, HttpSession session)
			throws Exception {
		
		List<User> users = modelDAO.getAllUsers().stream().filter(u -> u.getIndividualPermission().getCanAnnotate() != null && u.getIndividualPermission().getCanAnnotate()).collect(Collectors.toList());
		UserSearchItems userList = new UserSearchItems(users);
		return userList.createVuetifyObjectJSON();
		
	}
	
	@RequestMapping(value = "/assignToUser")
	@ResponseBody
	public String assignToUser(Model model, HttpSession session, HttpServletRequest request,
			@RequestParam String userIdsParam,
			@RequestParam String caseId)
			throws Exception {
		
		RequestUtils utils = new RequestUtils(modelDAO);
		
		OrderCase orderCase = utils.getCaseSummary(caseId);
		List<String> alreadyAssignedTo = new ArrayList<String>();
		if (orderCase != null) {
			alreadyAssignedTo = orderCase.getAssignedTo(); //to skip users already notified
		}
		List<User> users = modelDAO.getAllUsers();
		List<User> realUsers = new ArrayList<User>();
		String[] userIds = userIdsParam.split(",");
		for (String userId : userIds) {
			if (StringUtils.isNumeric(userId)) {
				Integer userIdInt = Integer.parseInt(userId);
				for (User user : users) {
					if (userIdInt == user.getUserId()) {
						realUsers.add(user);
					}
				}
					
			}
		}
		AjaxResponse response = utils.assignCaseToUser(realUsers, caseId);
		User currentUser = ControllerUtil.getSessionUser(session);
		if (response.getSuccess()) {
			for (User user : realUsers) {
				if (alreadyAssignedTo.contains(user.getUserId() + "")
						|| user.getUserId() == currentUser.getUserId()
						|| user.getIndividualPermission().getReceiveAllNotifications()) { 
					continue; //skip users that already received the email and current user
				}
				String servelt = "openCase/";
				String reason = "";
				String subject = "You have a new case: " + caseId;
				this.sendEmail(caseId, subject, user, currentUser, servelt, reason, false);
			}
		}
		
		//notify other users whose receive_all_notifications is true
		for (User aUser : users) {
			if (!aUser.equals(currentUser) 
					&& !ControllerUtil.isUserAssignedToCase(orderCase, aUser) 
					&& aUser.getIndividualPermission().getReceiveAllNotifications()) {
				String servelt = "openCaseReadOnly/";
				String reason = "You are receiving this message because your account is set to receive all notifications.<br/><br/>";
				String subject = "You have a new notification regarding case: " + caseId;
				this.sendEmail(caseId, subject, aUser, currentUser, servelt, reason, true);
			}
		}
		
		return response.createObjectJSON();
		
	}
	
	@RequestMapping(value = "/toggleArchivingStatusForCase")
	@ResponseBody
	public String toggleArchivingStatusForCase(Model model, HttpSession session, 
			@RequestParam String caseId, @RequestParam Boolean doArchive) throws Exception {

		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		User user = ControllerUtil.getSessionUser(session);
		boolean isAssigned = ControllerUtil.isUserAssignedToCase(utils, caseId, user);
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(isAssigned);
		if (isAssigned) {
			OrderCase caseSummary = utils.getCaseSummary(caseId);
			if (caseSummary != null) {
				caseSummary.setActive(!doArchive);
				OrderCase savedCaseSummary = utils.saveCaseSummary(caseId, caseSummary);
				if (savedCaseSummary != null) {
					response.setSuccess(true);
				}
				else { //something was wrong
					response.setMessage("Error: Verify that the data is valid");
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
	
	private void sendEmail(String caseId, String subject, User user, User currentUser, String servlet, String reason, boolean fromNotifications) throws IOException, InterruptedException {
		StringBuilder message = new StringBuilder()
				.append("<p>Dr. ").append(user.getLast()).append(",</p><br/>")
				.append("<b>")
				.append(currentUser.getFullName())
				.append("</b>")
				.append(" assigned ");
				if (!fromNotifications) {
					message.append(" you "); //only said "Dr. XXX assigned you" if it's not from notifications
				}
				message.append("a new case. <b>")
				.append("Case Id: ").append(caseId).append("</b><br/>")
				.append("<br/>")
				.append(reason);
				
		String toEmail = user.getEmail();
//		String toEmail = "guillaume.jimenez@utsouthwestern.edu"; //for testing to avoid sending other people emails
		String link = new StringBuilder().append(emailProps.getRootUrl()).append(servlet).append(caseId).toString();
		String fullMessage = NotificationUtils.buildStandardMessage(message.toString(), emailProps, link);
		boolean success = NotificationUtils.sendEmail(emailProps.getFrom(), toEmail, subject, fullMessage);
		System.out.println("An email was sent. Success:" + success);
	}
	
	@RequestMapping(value = "/createPDFReport", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String createPDFReport(Model model, HttpSession session, @RequestParam String reportId) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER,	true);
		AjaxResponse response = new AjaxResponse();
		if (reportId == null || reportId.equals("")) {
			response.setIsAllowed(false);
			response.setSuccess(false);
			response.setMessage("No report provided");
		}
		// send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		Report report = utils.getReportDetails(reportId);
		if (report != null) {
			String possibleDirtyData = report.createObjectJSON();
			String cleanData = possibleDirtyData.replaceAll("\\\\t", " ").replaceAll("\\\\n", "<br/>");
			report = mapper.readValue(cleanData, Report.class);
			OrderCase caseSummary = utils.getCaseSummary(report.getCaseId());
			User signedBy = modelDAO.getUserByUserId(report.getModifiedBy());
			try {
			FinalReportPDFTemplate pdfReport = new FinalReportPDFTemplate(report, fileProps, caseSummary, otherProps, signedBy);
			pdfReport.saveTemp();

			String linkName = pdfReport.createPDFLink(fileProps);
			response.setSuccess(true);
			response.setMessage(linkName);
			}catch (EncodingGlyphException e) {
				response.setSuccess(false);
				response.setMessage(e.getMessage());
			}
		}
		else {
			response.setSuccess(false);
			response.setMessage("No report provided");
		}
		return response.createObjectJSON();
	}
	
}
