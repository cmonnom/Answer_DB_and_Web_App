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

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.vuetify.AllOrderCasesSummary;
import utsw.bicf.answer.controller.serialization.vuetify.OrderCaseAllSummary;
import utsw.bicf.answer.controller.serialization.vuetify.OrderCaseForUserSummary;
import utsw.bicf.answer.controller.serialization.vuetify.UserSearchItems;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.hybrid.OrderCaseAll;
import utsw.bicf.answer.model.hybrid.OrderCaseForUser;
import utsw.bicf.answer.security.EmailProperties;
import utsw.bicf.answer.security.NotificationUtils;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class HomeController {
	
	static {
		PermissionUtils.addPermission(HomeController.class.getCanonicalName() + ".home", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(HomeController.class.getCanonicalName() + ".getWorklists", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(HomeController.class.getCanonicalName() + ".assignToUser", IndividualPermission.CAN_ASSIGN);
		PermissionUtils.addPermission(HomeController.class.getCanonicalName() + ".getAllUsersToAssign", IndividualPermission.CAN_ASSIGN);
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;
	@Autowired
	EmailProperties emailProps;

	@RequestMapping("/home")
	public String home(Model model, HttpSession session) throws IOException {
		model.addAttribute("urlRedirect", "home");
		User user = (User) session.getAttribute("user");
		return ControllerUtil.initializeModel(model, servletContext, user);
	}
	
	@RequestMapping(value = "/getWorklists")
	@ResponseBody
	public String getWorklists(Model model, HttpSession session)
			throws Exception {

		User user = (User) session.getAttribute("user");
		
		//send user to Ben's API to retrieve all active cases
		RequestUtils utils = new RequestUtils(modelDAO);
		OrderCase[] cases = utils.getActiveCases();
		List<OrderCase> caseList = new ArrayList<OrderCase>();
		
		if (cases != null) {
			for (OrderCase c : cases) {
				caseList.add(c);
			}
			//filter by assigned/user/available
			List<OrderCaseForUser> casesForUser = 
					caseList.stream()
					.filter(c -> c.getAssignedTo() != null && c.getAssignedTo().contains(user.getUserId().toString()))
					.map(c -> new OrderCaseForUser(c))
					.collect(Collectors.toList());
			
			List<OrderCaseAll> casesAll = 
					caseList.stream()
					.map(c -> new OrderCaseAll(c, modelDAO.getAllUsers(), user))
					.collect(Collectors.toList());
			
			OrderCaseAllSummary allSummary = new OrderCaseAllSummary(casesAll, user);
			OrderCaseForUserSummary forUserSummary = new OrderCaseForUserSummary(casesForUser);
			
			AllOrderCasesSummary summary = new AllOrderCasesSummary(allSummary, forUserSummary);
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
		
		List<User> users = modelDAO.getAllUsers();
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
		if (response.getSuccess()) {
			User currentUser = (User) session.getAttribute("user");
			for (User user : realUsers) {
				if (alreadyAssignedTo.contains(user.getUserId() + "")
						|| user.getUserId() == currentUser.getUserId()) { 
					continue; //skip users that already received the email and current user
				}
				String subject = "You have a new case: " + caseId;
				StringBuilder message = new StringBuilder()
						.append("<p>Dr. ").append(user.getLast()).append(",</p><br/>")
						.append("<b>")
						.append(currentUser.getFullName())
						.append("</b>")
						.append(" assigned you a new case. ")
						.append("<b>")
						.append("Case Id: ").append(caseId).append("<br/>")
						.append("</b>");
						
				String toEmail = user.getEmail();
//				String toEmail = "guillaume.jimenez@utsouthwestern.edu"; //for testing to avoid sending other people emails
				String link = new StringBuilder().append(emailProps.getRootUrl()).append("openCase/").append(caseId).toString();
				String fullMessage = NotificationUtils.buildStandardMessage(message.toString(), emailProps, link);
				boolean success = NotificationUtils.sendEmail(emailProps.getFrom(), toEmail, subject, fullMessage);
				System.out.println("An email was sent. Success:" + success);
			}
		}
		
		return response.createObjectJSON();
		
	}
	
}
