package utsw.bicf.answer.controller;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.DataReportGroup;
import utsw.bicf.answer.controller.serialization.TargetPage;
import utsw.bicf.answer.controller.serialization.UserCredentials;
import utsw.bicf.answer.dao.LoginDAO;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.LDAPAuthentication;
import utsw.bicf.answer.security.OtherProperties;

@Controller
@RequestMapping("/")
public class LoginController {

	@Autowired
	private LoginDAO loginDAO;
	@Autowired 
	ServletContext servletContext;
	@Autowired
	LDAPAuthentication ldapUtils;
	@Autowired
	FileProperties fileProps;
	@Autowired
	OtherProperties otherProps;

	@RequestMapping("/login")
	public String login(Model model, HttpSession session) throws IOException {
		User user = (User) session.getAttribute("user");
		ControllerUtil.initializeModel(model, servletContext, user);
		ControllerUtil.setGlobalVariables(model, fileProps, otherProps);
		return "login";
	}

	@RequestMapping(value = "/validateUser", method = {RequestMethod.POST})
	@ResponseBody
	public String validateUser(Model model, HttpSession session, 
			@RequestBody String data) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		UserCredentials credentials = mapper.readValue(data, UserCredentials.class);
		//check if email or username
		User user = loginDAO.getUserByUsernameOrEmail(credentials.getUsername());
				
		boolean proceed = false;
		if (user != null) {
			proceed = ldapUtils.isUserValid(user.getUsername(), credentials.getPassword());
//			if (user.getIndividualPermission().getAdmin()) {
//				proceed = true;
//			}
		}
		
		if (proceed) {
			session.setAttribute("user", user);
			return new TargetPage(true, "login successful", (String) model.asMap().get("urlRedirect"), true).toJSONString();
			
		}
		session.removeAttribute("user");
		return new TargetPage(false, "Wrong username or password", null, true).toJSONString(); //stay on the page
	}
	
	@RequestMapping("/logout")
	public String logout(Model model, HttpSession session) {
		loginDAO.closeUserSession(session);
		model.addAttribute("isAllowed", false);
		model.addAttribute("pageTitle", "Logout");
		model.addAttribute("content", "logout");
		model.addAttribute("javascript", "logout");
		model.addAttribute("urlRedirect", "login");
		return "logout";
	}

	public static boolean isAllowed(Model model) {
		return (Boolean) model.asMap().get("isAllowed");
	}
	
	/**
	 * Only use this method on login-full-page to see if the user
	 * is already logged in and redirect to urlRedirect or home (if no urlRedirect)
	 * @param model
	 * @param session
	 * @return
	 * @throws JsonProcessingException
	 */
	@RequestMapping("/checkAlreadyLoggedIn")
	@ResponseBody
	public String checkAlreadyLoggedIn(Model model, HttpSession session) throws JsonProcessingException {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			return new TargetPage(true, "already logged in", "home", true).toJSONString();
		}
		return new TargetPage(false, "not logged in", "home", true).toJSONString();
	}

	
}
