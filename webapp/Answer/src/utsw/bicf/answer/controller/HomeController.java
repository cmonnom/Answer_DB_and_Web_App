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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.vuetify.AllOrderCasesSummary;
import utsw.bicf.answer.controller.serialization.vuetify.OrderCaseAllSummary;
import utsw.bicf.answer.controller.serialization.vuetify.OrderCaseArchivedSummary;
import utsw.bicf.answer.controller.serialization.vuetify.OrderCaseFinalizedSummary;
import utsw.bicf.answer.controller.serialization.vuetify.OrderCaseForUserSummary;
import utsw.bicf.answer.controller.serialization.vuetify.Summary;
import utsw.bicf.answer.controller.serialization.vuetify.UserSearchItems;
import utsw.bicf.answer.dao.LoginDAO;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.Group;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.CaseHistory;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.extmapping.Report;
import utsw.bicf.answer.model.hybrid.HeaderOrder;
import utsw.bicf.answer.model.hybrid.OrderCaseAll;
import utsw.bicf.answer.model.hybrid.OrderCaseArchived;
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
	@Autowired
	LoginDAO loginDAO;

	@RequestMapping("/home")
	public String home(Model model, HttpSession session) throws IOException {
		model.addAttribute("urlRedirect", "home");
		ControllerUtil.setGlobalVariables(model, fileProps, otherProps);
		User user = ControllerUtil.getSessionUser(session);
		return ControllerUtil.initializeModel(model, servletContext, user, loginDAO);
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
				if (ControllerUtil.areUserAndCaseInSameGroup(user, c)) {
					caseList.add(c);
				}
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
					.filter(c -> c.getActive() != null && c.getActive())
					.map(c -> new OrderCaseAll(modelDAO, c, users, user))
					.collect(Collectors.toList());
			List<OrderCaseFinalized> casesFinalized = 
					caseList.stream()
					.filter(c -> CaseHistory.lastStepMatches(c, CaseHistory.STEP_FINALIZED)
							&& c.getActive() != null && c.getActive())
					.map(c -> new OrderCaseFinalized(modelDAO, c, users, user))
					.collect(Collectors.toList());
			List<OrderCaseArchived> casesArchived = 
					caseList.stream()
					.filter(c -> !c.getActive())
					.map(c -> new OrderCaseArchived(modelDAO, c, users, user))
					.collect(Collectors.toList());
			List<HeaderOrder> headerOrdersAll = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, "All Cases");
			OrderCaseAllSummary allSummary = new OrderCaseAllSummary(casesAll, user, headerOrdersAll);
			List<HeaderOrder> headerOrdersUser = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, "My Cases");
			OrderCaseForUserSummary forUserSummary = new OrderCaseForUserSummary(casesForUser, headerOrdersUser);
			List<HeaderOrder> headerOrdersFinalized = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, "Cases Finalized");
			OrderCaseFinalizedSummary finalizedSummary = new OrderCaseFinalizedSummary(casesFinalized, user, headerOrdersFinalized);
			List<HeaderOrder> headerOrdersArchived = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, "Cases Archived");
			OrderCaseArchivedSummary archivedSummary = new OrderCaseArchivedSummary(casesArchived, user, headerOrdersArchived);
			
			AllOrderCasesSummary summary = new AllOrderCasesSummary(allSummary, forUserSummary, finalizedSummary, archivedSummary);
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
		User currentUser = ControllerUtil.getSessionUser(session);
		List<User> users = modelDAO.getAllUsers().stream().filter(u -> u.getIndividualPermission().getCanAnnotate() != null 
				&& u.getIndividualPermission().getCanAnnotate()
				&& ControllerUtil.areUsersInSameGroup(currentUser, u)).collect(Collectors.toList());
		UserSearchItems userList = new UserSearchItems(users);
		return userList.createVuetifyObjectJSON();
		
	}
	
	@RequestMapping(value = "/assignToUser")
	@ResponseBody
	public String assignToUser(Model model, HttpSession session, HttpServletRequest request,
			@RequestParam String userIdsParam,
			@RequestParam String caseId,
			@RequestParam(defaultValue="false") Boolean receiveACopyOfEmail)
			throws Exception {
		
		RequestUtils utils = new RequestUtils(modelDAO);
		
		OrderCase orderCase = utils.getCaseSummary(caseId);
		User currentUser = ControllerUtil.getSessionUser(session);
		if (!ControllerUtil.areUserAndCaseInSameGroup(currentUser, orderCase)) {
			return ControllerUtil.returnFailedGroupCheck();
		}
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
		
		if (receiveACopyOfEmail) {
			String subject = "Confirmation of Case " + caseId + " Assigned";
			StringBuilder reason = new StringBuilder("You have assigned cases to: ");
			if (realUsers != null && !realUsers.isEmpty()) {
				reason.append(realUsers.stream().map(u -> u.getFullName()).collect(Collectors.joining(", ")));
				reason.append(". An email was sent to the people listed.");
			}
			else {
				reason.append("nobody.");
			}
			String toEmail = currentUser.getEmail();
			String message = NotificationUtils.buildStandardSelfNotificationMessage(reason.toString(), emailProps);
			boolean success = NotificationUtils.sendEmail(emailProps.getFrom(), toEmail, subject, message);
			System.out.println("An email was sent. Success:" + success);
		}
		
		return response.createObjectJSON();
		
	}
	
	/**
	 * Only admins should be able to assign a case to groups
	 * @param model
	 * @param session
	 * @param request
	 * @param groupIdsParam
	 * @param caseId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/assignToGroup")
	@ResponseBody
	public String assignToGroup(Model model, HttpSession session, HttpServletRequest request,
			@RequestParam String groupIdsParam,
			@RequestParam String caseId)
			throws Exception {
		
		RequestUtils utils = new RequestUtils(modelDAO);
		
		User currentUser = ControllerUtil.getSessionUser(session);
		if (currentUser.getIndividualPermission().getAdmin() == null ||
				!currentUser.getIndividualPermission().getAdmin()) {
			AjaxResponse response = new AjaxResponse();
			response.setSuccess(false);
			response.setIsAllowed(false);
			response.setMessage("Only admins can modify groups");
			return response.createObjectJSON();
		}
		List<Group> groups = modelDAO.getAllGroups();
		List<Group> realGroups = new ArrayList<Group>();
		String[] groupIds = groupIdsParam.split(",");
		for (String groupId : groupIds) {
			if (StringUtils.isNumeric(groupId)) {
				Integer userIdInt = Integer.parseInt(groupId);
				for (Group group : groups) {
					if (userIdInt == group.getGroupId()) {
						realGroups.add(group);
					}
				}
			}
		}
		AjaxResponse response = utils.assignCaseToGroup(realGroups, caseId);
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
				if (!ControllerUtil.areUserAndCaseInSameGroup(user, caseSummary)) {
					return ControllerUtil.initializeModelNotAllowed(model, servletContext);
				}
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
			User currentUser = ControllerUtil.getSessionUser(session);
			if (!ControllerUtil.areUserAndCaseInSameGroup(currentUser, caseSummary)) {
				return ControllerUtil.initializeModelNotAllowed(model, servletContext);
			}
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
