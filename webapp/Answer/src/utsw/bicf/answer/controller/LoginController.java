package utsw.bicf.answer.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.TargetPage;
import utsw.bicf.answer.controller.serialization.UserCredentials;
import utsw.bicf.answer.dao.LoginDAO;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.AuthUtils;
import utsw.bicf.answer.model.LoginAttempt;
import utsw.bicf.answer.model.ResetToken;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.Version;
import utsw.bicf.answer.security.AzureOAuth;
import utsw.bicf.answer.security.EmailProperties;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.LDAPAuthentication;
import utsw.bicf.answer.security.NotificationUtils;
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
	@Autowired
	EmailProperties emailProps;
	@Autowired
	AzureOAuth azureAuthUtils;

	@RequestMapping("/login")
	public String login(Model model, HttpSession session) throws IOException {
		User user = ControllerUtil.getSessionUser(session);
		ControllerUtil.initializeModel(model, servletContext, user, null);
		if (user == null) {
			session.setAttribute("user", "login redirect");
		}
		ControllerUtil.setGlobalVariables(model, fileProps, otherProps);
		return "login";
	}
	
	@RequestMapping("/resetPassword")
	public String resetPassword(Model model, HttpSession session, @RequestParam String token) throws IOException {
		//if not LDAP return an error page
		if (OtherProperties.AUTH_LDAP.equals(otherProps.getAuthenticateWith())) {
			return "error";
		}
		
		ResetToken resetToken = modelDAO.getResetTokenByTokenValue(token);
		if (resetToken == null || resetToken.getUser() == null) {
			//invalid request
			return "error";
		}
		model.addAttribute("user", resetToken.getUser());
		ControllerUtil.initializeModel(model, servletContext, null, null);
		session.setAttribute("user", "reset password");
		ControllerUtil.setGlobalVariables(model, fileProps, otherProps);
		return "reset";
	}
	
	@RequestMapping(value = "/validateUser", method = {RequestMethod.POST})
	@ResponseBody
	public String validateUser(Model model, HttpSession session, 
			@RequestBody String data) throws IOException, URISyntaxException {
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
		if (proceed && user != null){
			proceed = false;
			if (OtherProperties.AUTH_AZURE_OAUTH.equals(otherProps.getAuthenticateWith())) {
					proceed = azureAuthUtils.isUserValid(otherProps, credentials.getPassword());
			}
			else if (OtherProperties.AUTH_LDAP.equals(otherProps.getAuthenticateWith())) {
				proceed = ldapUtils.isUserValid(user.getUsername(), credentials.getPassword());
			}
			else if (OtherProperties.AUTH_DEV.equals(otherProps.getAuthenticateWith())) {
				AjaxResponse response = new AjaxResponse();
				AuthUtils utils = new AuthUtils(modelDAO, otherProps);
				credentials.setUsername(user.getUserId() + ""); //switch to userId for the auth
				credentials.setPassword(credentials.getPassword());
				utils.checkDevCredentials(response, credentials);
				proceed = response.getSuccess();
			}
			else { //local auth check
				AjaxResponse response = new AjaxResponse();
				AuthUtils utils = new AuthUtils(modelDAO, otherProps);
				credentials.setUsername(user.getUserId() + ""); //switch to userId for the auth
				credentials.setPassword(credentials.getPassword());
				utils.checkUserCredentials(response, credentials);
				proceed = response.getSuccess();
			}
			if (proceed) {
				loginAttempt.setCounter(0);
				loginAttempt.setShowLastLogin(true);
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
		TargetPage page = new TargetPage(false, otherProps.getAuthMessage(), "home", true);
		page.setPayload(otherProps.getAuthenticateWith());
		return page.toJSONString();
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
	
	@RequestMapping(value = "/sendResetPasswordEmail", method = RequestMethod.POST)
	@ResponseBody
	public String sendResetPasswordEmail(Model model, HttpSession session,
			@RequestParam String email) throws IOException, InterruptedException {
		User user = modelDAO.getUserByEmail(email);
		AjaxResponse response = new AjaxResponse();
		if (OtherProperties.AUTH_LDAP.equals(otherProps.getAuthenticateWith())) {
			response.setIsAllowed(false);
			response.setSuccess(false);
			response.setMessage("Contact your LDAP administrator to change your password");
			response.createObjectJSON();
		}
		response.setIsAllowed(true);
		if (user != null) {
			//create token
			ResetToken resetToken = new ResetToken();
			resetToken.setUser(user);
			String token = RandomStringUtils.random(255, true, true);
			resetToken.setToken(token);
			resetToken.setDateCreated(LocalDateTime.now());
			modelDAO.saveObject(resetToken);
			//check if it's first time login
			LoginAttempt loginAttempt = loginDAO.getLoginAttemptForUser(user);
			boolean firstTime = loginAttempt == null;
			//TODO send email
			String subject = "You Requested a Password Reset";
			String servlet = "resetPassword?token=" + token;
			String link = new StringBuilder().append(emailProps.getRootUrl()).append(servlet).toString();
			String message = NotificationUtils.buildStandardPasswordResetMessage(user, firstTime, link, emailProps);
			boolean success = NotificationUtils.sendEmail(emailProps.getFrom(), email, subject, message);
			response.setSuccess(success);
		}
		else {
			response.setSuccess(false);
		}
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
	@ResponseBody
	public String updatePassword(Model model, HttpSession session, @RequestBody String data, @RequestParam String token) throws Exception {
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		if (OtherProperties.AUTH_LDAP.equals(otherProps.getAuthenticateWith())) {
			response.setSuccess(false);
			response.setMessage("Contact your LDAP administrator to change your password");
			return response.createObjectJSON();
		}
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		UserCredentials userCreds = mapper.readValue(data,  UserCredentials.class);
		ResetToken resetToken = modelDAO.getResetTokenByTokenAndEmail(token, userCreds.getUsername());
		
		//check that password is valid
		if (!validatePassword(userCreds.getPassword())) {
			response.setSuccess(false);
			response.setMessage("The password is not strong enough");
		}
		else if (resetToken == null) {
			response.setSuccess(false);
			response.setMessage("Invalid user or token");
		}
		else {
			//TODO send to API for storage
			User user = modelDAO.getUserByEmail(userCreds.getUsername());
			userCreds.setUsername(user.getUserId() + "");
			AuthUtils utils = new AuthUtils(modelDAO, otherProps);
			utils.updateUser(response, userCreds);
			if (response.getSuccess()) {
				modelDAO.deleteObject(resetToken);
			}
		}
		
		return response.createObjectJSON();
	}
	
	private boolean validatePassword(String password) {
		boolean isValid = password != null;
		isValid &= password.length() >= 8;
		int count = 0;
		if (password.matches(".*[A-Z]+.*")) {
			count++;
		}
		if (password.matches(".*[a-z]+.*")) {
			count++;
		}
		if (password.matches(".*[0-9]+.*")) {
			count++;
		}
		if (password.matches(".*[!@#$%^&*()+=_]+].*")) {
			count++;
		}
		isValid &= count >= 3;
		return isValid;
	}
}
