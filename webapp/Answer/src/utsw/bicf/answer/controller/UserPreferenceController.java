package utsw.bicf.answer.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.vuetify.HeaderConfigSummaries;
import utsw.bicf.answer.controller.serialization.vuetify.HeaderConfigSummary;
import utsw.bicf.answer.dao.LoginDAO;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.Group;
import utsw.bicf.answer.model.HeaderConfig;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.UserPref;
import utsw.bicf.answer.model.hybrid.HeaderOrder;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.NCBIProperties;
import utsw.bicf.answer.security.OtherProperties;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class UserPreferenceController {

	static {
		PermissionUtils.addPermission(UserPreferenceController.class.getCanonicalName() + ".userPrefs",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(UserPreferenceController.class.getCanonicalName() + ".getUserPrefs",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(UserPreferenceController.class.getCanonicalName() + ".saveUserPrefs",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(UserPreferenceController.class.getCanonicalName() + ".getAdmins",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(UserPreferenceController.class.getCanonicalName() + ".getHeaderPrefs",
				IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(UserPreferenceController.class.getCanonicalName() + ".getUserGroups",
				IndividualPermission.CAN_VIEW);
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;
	@Autowired
	FileProperties fileProps;
	@Autowired
	OtherProperties otherProps;
	@Autowired
	NCBIProperties ncbiProps;
	@Autowired
	LoginDAO loginDAO;

	@RequestMapping("/userPrefs")
	public String userPrefs(Model model, HttpSession session) throws IOException, UnsupportedOperationException, URISyntaxException {
		String url = "userPrefs";
		User user = ControllerUtil.getSessionUser(session);
		model.addAttribute("urlRedirect", url);
		model.addAttribute("isProduction", fileProps.getProductionEnv());
		ControllerUtil.setGlobalVariables(model, fileProps, otherProps);
		
		return ControllerUtil.initializeModel(model, servletContext, user, loginDAO);
	}
	
	@RequestMapping(value = "/getUserPrefs", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getUserPrefs(Model model, HttpSession session) throws Exception {
		// send user to Ben's API
		User user = ControllerUtil.getSessionUser(session);
		UserPref userPref = user.getUserPref();
		if (userPref == null) {
			userPref = new UserPref();
			modelDAO.saveObject(userPref);
			user.setUserPref(userPref);
			modelDAO.saveObject(user);
		}
		userPref.setIsAllowed(true);
		userPref.setSuccess(true);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(userPref);
	}
	
	@RequestMapping(value = "/getAdmins", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getAdmins(Model model, HttpSession session) throws Exception {
		// send user to Ben's API
		List<User> adminUsers = modelDAO.getAdmins();
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		response.setSuccess(true);
		String message = adminUsers.stream().map(u -> u.getFullName()).collect(Collectors.joining(" or "));
		response.setMessage(message);
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/getUserGroups", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getUserGroups(Model model, HttpSession session) throws Exception {
		User currentUser = ControllerUtil.getSessionUser(session);
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		response.setSuccess(true);
		
		List<Group> groups = new ArrayList<Group>();
		for (Group g : currentUser.getGroups()) {
			g.setUsers(null); //to avoid lazy loading issues when creating a JSON of Group
			groups.add(g);
		}
		response.setPayload(groups);
		return response.createObjectJSON();
	}


	@RequestMapping(value = "/saveUserPrefs", produces= "application/json; charset=utf-8", method= RequestMethod.POST)
	@ResponseBody
	public String saveUserPrefs(Model model, HttpSession session,
			@RequestBody String data) throws Exception {
		// send user to Ben's API
		User user = ControllerUtil.getSessionUser(session);
		UserPref userPref = user.getUserPref();
		if (userPref == null) {
			userPref = new UserPref();
			modelDAO.saveObject(userPref);
			user.setUserPref(userPref);
			modelDAO.saveObject(user);
			userPref.setIsAllowed(true);
			userPref.setSuccess(true);
		}
		ObjectMapper mapper = new ObjectMapper();
		UserPref newUserPref = mapper.readValue(data, UserPref.class);
		if (newUserPref != null) {
			userPref.setShowGoodies(newUserPref.getShowGoodies() == null ? false : newUserPref.getShowGoodies());
		}
		modelDAO.saveObject(userPref);
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		response.setSuccess(true);
		
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/getHeaderPrefs", produces= "application/json; charset=utf-8")
	@ResponseBody
	public String getHeaderPrefs(Model model, HttpSession session)
			throws Exception {
		User user = ControllerUtil.getSessionUser(session);
		List<HeaderConfig> headerConfigs = modelDAO.getAllHeaderConfigForUser(user);
		ObjectMapper mapper = new ObjectMapper();
		List<HeaderConfigSummary> summaries = new ArrayList<HeaderConfigSummary>();
		for (HeaderConfig header : headerConfigs) {
			List<HeaderOrder> headerOrders = new ArrayList<HeaderOrder>();
			HeaderOrder[] headerOrdersArray = mapper.readValue(header.getHeaderOrder(), HeaderOrder[].class);
			for (HeaderOrder h : headerOrdersArray) {
				headerOrders.add(h);
			}
			HeaderConfigSummary summary = new HeaderConfigSummary(headerOrders, header.getTableTitle());
			summaries.add(summary);
		}
		
		return new HeaderConfigSummaries(summaries).createVuetifyObjectJSON();
	}
	
	
}
