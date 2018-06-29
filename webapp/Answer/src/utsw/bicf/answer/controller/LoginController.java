package utsw.bicf.answer.controller;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import utsw.bicf.answer.controller.serialization.TargetPage;
import utsw.bicf.answer.dao.LoginDAO;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.security.LDAPAuthentication;

@Controller
@RequestMapping("/")
public class LoginController {

	@Autowired
	private LoginDAO loginDAO;
	@Autowired 
	ServletContext servletContext;
	@Autowired
	LDAPAuthentication ldapUtils;

	@RequestMapping("/login")
	public String login(Model model, HttpSession session) throws IOException {
		User user = (User) session.getAttribute("user");
		ControllerUtil.initializeModel(model, servletContext, user);
		return "login";
	}

	@RequestMapping(value = "/validateUser", method = {RequestMethod.POST})
	@ResponseBody
	public String validateUser(Model model, HttpSession session, 
			@RequestParam("username") String usernameOrEmail, 
			@RequestParam("password") String password) throws JsonProcessingException {
		//check if email or username
		User user = loginDAO.getUserByUsernameOrEmail(usernameOrEmail);
				
		boolean proceed = false;
		if (user != null) {
			proceed = ldapUtils.isUserValid(user.getUsername(), password);
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
	
}
