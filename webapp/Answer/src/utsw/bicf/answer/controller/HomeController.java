package utsw.bicf.answer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
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
import utsw.bicf.answer.controller.serialization.vuetify.OrderCaseAssignedSummary;
import utsw.bicf.answer.controller.serialization.vuetify.OrderCaseAvailableSummary;
import utsw.bicf.answer.controller.serialization.vuetify.OrderCaseForUserSummary;
import utsw.bicf.answer.controller.serialization.vuetify.UserSearchItems;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.model.hybrid.OrderCaseAssigned;
import utsw.bicf.answer.model.hybrid.OrderCaseAvailable;
import utsw.bicf.answer.model.hybrid.OrderCaseForUser;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class HomeController {
	
	static {
		PermissionUtils.addPermission(HomeController.class.getCanonicalName() + ".home", new PermissionUtils(true, false, false));
		PermissionUtils.addPermission(HomeController.class.getCanonicalName() + ".getWorklists", new PermissionUtils(true, false, false));
		PermissionUtils.addPermission(HomeController.class.getCanonicalName() + ".assignToUser", new PermissionUtils(true, true, false));
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;

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
			List<OrderCaseAvailable> casesAvailable = 
					caseList.stream()
					.filter(c -> c.getAssignedTo() == null || c.getAssignedTo().isEmpty())
					.map(c -> new OrderCaseAvailable(c))
					.collect(Collectors.toList());
			List<OrderCaseForUser> casesForUser = 
					caseList.stream()
					.filter(c -> c.getAssignedTo() != null && c.getAssignedTo().contains(user.getUserId().toString()))
					.map(c -> new OrderCaseForUser(c))
					.collect(Collectors.toList());
			List<OrderCaseAssigned> casesAssigned = 
					caseList.stream()
					.filter(c -> c.getAssignedTo() != null && !c.getAssignedTo().isEmpty())
					.map(c -> new OrderCaseAssigned(c, modelDAO.getAllUsers()))
					.collect(Collectors.toList());
			
			OrderCaseAvailableSummary availSummary = new OrderCaseAvailableSummary(casesAvailable, user);
			OrderCaseForUserSummary forUserSummary = new OrderCaseForUserSummary(casesForUser);
			OrderCaseAssignedSummary assignedSummary = new OrderCaseAssignedSummary(casesAssigned, user);
			
			AllOrderCasesSummary summary = new AllOrderCasesSummary(availSummary, forUserSummary, assignedSummary);
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
	public String assignToUser(Model model, HttpSession session, @RequestParam String userIdsParam,
			@RequestParam String caseId)
			throws Exception {
		
		//send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		List<User> users = modelDAO.getAllUsers();
		List<User> realUsers = new ArrayList<User>();
		String[] userIds = userIdsParam.split("|");
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
		
		return response.createObjectJSON();
		
	}
	
}
