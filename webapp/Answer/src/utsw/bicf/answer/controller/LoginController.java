package utsw.bicf.answer.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.TargetPage;
import utsw.bicf.answer.controller.serialization.UserCredentials;
import utsw.bicf.answer.dao.LoginDAO;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.LoginAttempt;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.Version;
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
	@Autowired
	ModelDAO modelDAO;

	@RequestMapping("/login")
	public String login(Model model, HttpSession session) throws IOException {
		User user = ControllerUtil.getSessionUser(session);
		ControllerUtil.initializeModel(model, servletContext, user);
		if (user == null) {
			session.setAttribute("user", "login redirect");
		}
		ControllerUtil.setGlobalVariables(model, fileProps, otherProps);
		return "login";
	}

	@RequestMapping(value = "/validateUser", method = {RequestMethod.POST})
	@ResponseBody
	public String validateUser(Model model, HttpSession session, 
			@RequestBody String data) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		UserCredentials credentials = mapper.readValue(data, UserCredentials.class);
		//check if using email or username
		User user = loginDAO.getUserByUsernameOrEmail(credentials.getUsername());
				
		boolean proceed = false;
		LoginAttempt loginAttempt = null;
		String logReason = "Wrong username or password";
		loginAttempt = loginDAO.getLoginAttemptForUser(user); //user could be null. We should still record the login attempt
		if (loginAttempt == null) {
			loginAttempt = new LoginAttempt();
			loginAttempt.setCounter(0);
			loginAttempt.setUser(user);
		}
		loginAttempt.setCounter(loginAttempt.getCounter() + 1); //at this point, either it's the user's login attempt or a null user login attempt. So loginAttempt should not be null
		//check to see if user waited 10 sec after 5 attempts
		if (loginAttempt.getCounter() < 5 || loginAttempt.getLastAttemptDatetime().plusSeconds(10).isBefore(LocalDateTime.now())) {
			proceed = true;
		}
		else {
			proceed = false;
			logReason = "Too many failed logins. Please wait 10 sec.";
		}
		loginAttempt.setLastAttemptDatetime(LocalDateTime.now());
		if (proceed && user != null){
			proceed = ldapUtils.isUserValid(user.getUsername(), credentials.getPassword());
			if (proceed) {
				loginAttempt.setCounter(0);
				modelDAO.saveObject(loginAttempt);
				session.setAttribute("user", user);
				return new TargetPage(true, "login successful", (String) model.asMap().get("urlRedirect"), true).toJSONString();
			}
			else { //use a default log reason error message
				logReason = "Wrong username or password";
			}
		}
		modelDAO.saveObject(loginAttempt);
		session.removeAttribute("user");
		return new TargetPage(false, logReason, null, true).toJSONString(); //stay on the page
	}
	
	@RequestMapping(value = "/updateVersion")
	@ResponseBody
	public String updateVersion(Model model, HttpSession session) throws IOException {
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		response.setSuccess(true);
		loginDAO.resetAllVersion();
		loginDAO.updateToVersion1();
		return response.createObjectJSON();
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
		User user = ControllerUtil.getSessionUser(session);
		if (user != null) {
			return new TargetPage(true, "already logged in", "home", true).toJSONString();
		}
		return new TargetPage(false, "not logged in", "home", true).toJSONString();
	}

	@RequestMapping("/getCurrentVersion")
	@ResponseBody
	public String getCurrentVersion(Model model, HttpSession session) throws ClientProtocolException, URISyntaxException, IOException {
		Version currentVersion = modelDAO.getCurrentVersion();
		AjaxResponse response = new AjaxResponse();
		response.setPayload(currentVersion.getName());
		response.setIsAllowed(true);
		response.setSuccess(true);
		return response.createObjectJSON();
	}
	
}
