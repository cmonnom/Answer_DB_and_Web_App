package utsw.bicf.answer.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.vuetify.UserTableSummary;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;

@Controller
@RequestMapping("/")
public class AdminController {
	
	static {
		//if no permission is added, only admins will have access to the requests
//		PermissionUtils.permissionPerUrl.put("getWorklists", new PermissionUtils(true, false, false));
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;

	@RequestMapping("/admin")
	public String admin(Model model, HttpSession session) throws IOException {
		model.addAttribute("urlRedirect", "admin");
		User user = (User) session.getAttribute("user");
		return ControllerUtil.initializeModel(model, servletContext, user);
	}
	
	@RequestMapping(value = "/getAllUsers")
	@ResponseBody
	public String getAllUsers(Model model, HttpSession session)
			throws Exception {

		List<User> users = modelDAO.getAllUsers();
		UserTableSummary summary = new UserTableSummary(users);
		
		return summary.createVuetifyObjectJSON();
	}
	
	@RequestMapping(value = "/saveUser")
	@ResponseBody
	public String saveUser(Model model, HttpSession session,
			@RequestParam(defaultValue = "") Integer userId, @RequestParam String username,
			@RequestParam String first, @RequestParam String last, @RequestParam String email,
			@RequestParam Boolean canView,
			@RequestParam Boolean canSelect, @RequestParam Boolean canAnnotate, 
			@RequestParam Boolean canAssign, @RequestParam Boolean admin)
			throws Exception {
		User user = null;
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		if (userId != null) { //edit user
			user = modelDAO.getUserByUserId(userId);
			if (user == null) {
				response.setSuccess(false);
				response.setMessage("This user does not exit");
				return response.createObjectJSON();
			}
		}
		else { //new user
			user = new User();
		}
		user.setFirst(first);
		user.setLast(last);
		user.setUsername(username);
		user.setEmail(email);
		
		IndividualPermission individualPermission = user.getIndividualPermission();
		if (individualPermission == null) {
			individualPermission = new IndividualPermission();
			user.setIndividualPermission(individualPermission);
		}
		individualPermission.setAdmin(admin);
		individualPermission.setCanAnnotate(canAnnotate);
		individualPermission.setCanAssign(canAssign);
		individualPermission.setCanSelect(canSelect);
		individualPermission.setCanView(canView);
		modelDAO.saveObject(individualPermission);
		
		modelDAO.saveObject(user);
		
		response.setSuccess(true);
		
		return response.createObjectJSON();
	}
	
}
