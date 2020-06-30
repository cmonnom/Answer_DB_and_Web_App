package utsw.bicf.answer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.DataReportGroup;
import utsw.bicf.answer.controller.serialization.UserCredentials;
import utsw.bicf.answer.controller.serialization.vuetify.GroupSearchItems;
import utsw.bicf.answer.controller.serialization.vuetify.GroupTableSummary;
import utsw.bicf.answer.controller.serialization.vuetify.ReportGroupTableSummary;
import utsw.bicf.answer.controller.serialization.vuetify.Summary;
import utsw.bicf.answer.controller.serialization.vuetify.UserSearchItems;
import utsw.bicf.answer.controller.serialization.vuetify.UserTableSummary;
import utsw.bicf.answer.dao.LoginDAO;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.AuthUtils;
import utsw.bicf.answer.model.GeneToReport;
import utsw.bicf.answer.model.Group;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.ReportGroup;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.hybrid.HeaderOrder;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.OtherProperties;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class AdminController {
	
	static {
		//if no permission is added, only admins will have access to the requests
		PermissionUtils.addPermission(AdminController.class.getCanonicalName() + ".getAllReportGroups",
				IndividualPermission.CAN_ANNOTATE);
		PermissionUtils.addPermission(AdminController.class.getCanonicalName() + ".saveUser",
				IndividualPermission.CAN_ANNOTATE);
		PermissionUtils.addPermission(AdminController.class.getCanonicalName() + ".saveReportGroup",
				IndividualPermission.CAN_ANNOTATE);
		PermissionUtils.addPermission(AdminController.class.getCanonicalName() + ".deleteReportGroup",
				IndividualPermission.CAN_ANNOTATE);
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
	LoginDAO loginDAO;
	
	@RequestMapping("/admin")
	public String admin(Model model, HttpSession session) throws IOException {
		model.addAttribute("urlRedirect", "admin");
		ControllerUtil.setGlobalVariables(model, fileProps, otherProps);
		User user = ControllerUtil.getSessionUser(session);
		return ControllerUtil.initializeModel(model, servletContext, user, loginDAO);
	}
	
	@RequestMapping(value = "/getAllUsers")
	@ResponseBody
	public String getAllUsers(Model model, HttpSession session)
			throws Exception {

		List<User> users = modelDAO.getAllUsers();
		User user = ControllerUtil.getSessionUser(session);
		List<HeaderOrder> headerOrders = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, "Users");
		UserTableSummary summary = new UserTableSummary(users, headerOrders);
		
		return summary.createVuetifyObjectJSON();
	}
	
	@RequestMapping(value = "/getAllUsersForGroups")
	@ResponseBody
	public String getAllUsersForGroups(Model model, HttpSession session)
			throws Exception {
		
		List<User> users = modelDAO.getAllUsers().stream().collect(Collectors.toList());
		UserSearchItems userList = new UserSearchItems(users);
		return userList.createVuetifyObjectJSON();
		
	}
	
	@RequestMapping(value = "/getAllGroupsForUsers")
	@ResponseBody
	public String getAllGroupsForUsers(Model model, HttpSession session)
			throws Exception {
		
		List<Group> groups = modelDAO.getAllGroups().stream().collect(Collectors.toList());
		GroupSearchItems groupList = new GroupSearchItems(groups);
		return groupList.createVuetifyObjectJSON();
		
	}
	
	@RequestMapping(value = "/getAllGroups")
	@ResponseBody
	public String getAllGroups(Model model, HttpSession session)
			throws Exception {

		List<Group> groups = modelDAO.getAllGroups();
		User user = ControllerUtil.getSessionUser(session);
		List<HeaderOrder> headerOrders = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, "Groups");
		GroupTableSummary summary = new GroupTableSummary(groups, headerOrders);
		
		return summary.createVuetifyObjectJSON();
	}
	
	@RequestMapping(value = "/getAllReportGroups")
	@ResponseBody
	public String getAllReportGroups(Model model, HttpSession session)
			throws Exception {

		List<ReportGroup> reportGroups = modelDAO.getAllReportGroups();
		User user = ControllerUtil.getSessionUser(session);
		List<HeaderOrder> headerOrders = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, "Gene Sets");
		ReportGroupTableSummary summary = new ReportGroupTableSummary(reportGroups, headerOrders, user);
		
		return summary.createVuetifyObjectJSON();
	}
	
	@RequestMapping(value = "/saveUser", method = RequestMethod.POST)
	@ResponseBody
	public String saveUser(Model model, HttpSession session, @RequestBody String data,
			@RequestParam(defaultValue = "") Integer userId, 
			@RequestParam(defaultValue = "") String groups) throws Exception {
		User user = null;
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		boolean newUser = false;
		if (userId != null) { //edit user
			user = modelDAO.getUserByUserId(userId);
			if (user == null) {
				response.setSuccess(false);
				response.setMessage("This user does not exist");
				return response.createObjectJSON();
			}
		}
		else { //new user
			newUser = true;
			user = new User();
		}
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		User userParams = mapper.readValue(data, User.class);
		IndividualPermission ipParams = userParams.getIndividualPermission();
		user.setFirst(userParams.getFirst());
		user.setLast(userParams.getLast());
		user.setUsername(userParams.getUsername());
		user.setEmail(userParams.getEmail());
		
		IndividualPermission individualPermission = user.getIndividualPermission();
		if (individualPermission == null) {
			individualPermission = new IndividualPermission();
			user.setIndividualPermission(individualPermission);
		}
		individualPermission.setAdmin(ipParams.getAdmin());
		individualPermission.setCanAnnotate(ipParams.getCanAnnotate());
		individualPermission.setCanAssign(ipParams.getCanAssign());
		individualPermission.setCanSelect(ipParams.getCanSelect());
		individualPermission.setCanView(ipParams.getCanView());
		individualPermission.setCanReview(ipParams.getCanReview());
		individualPermission.setCanHide(ipParams.getCanHide());
		individualPermission.setReceiveAllNotifications(ipParams.getReceiveAllNotifications());
		
		modelDAO.saveObject(individualPermission);
		response.setSuccess(true);
		
		List<Group> groupList = new ArrayList<Group>();
		for (String groupId : groups.split(",")) {
			if (groupId != null && !groupId.equals("")) {
				groupList.add(modelDAO.getGroupByGroupId(Integer.parseInt(groupId)));
			}
		}
		modelDAO.saveObject(user);
		user.setGroups(groupList);
		for (Group g : groupList) {
			List<User> users = g.getUsers();
			if (!users.contains(user)) {
				users.add(user);
			}
			modelDAO.saveObject(g);
		}
		//need to save before and after the user is created so that Hibernate can update Group with the new User
		modelDAO.saveObject(user);
		if (OtherProperties.AUTH_LOCAL.equals(otherProps.getAuthenticateWith())) {
			UserCredentials userCreds = new UserCredentials();
			String password = RandomStringUtils.random(255, true, true);
			userCreds.setPassword(password);
			userCreds.setUsername(user.getUserId() + ""); //update user credentials after the database set a user id
			AuthUtils utils = new AuthUtils(modelDAO, otherProps);
			if (newUser) {
				utils.addUser(response, userCreds);
			}
			else {
				utils.updateUser(response, userCreds);
			}
		}
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/saveGroup", method = RequestMethod.POST)
	@ResponseBody
	public String saveGroup(Model model, HttpSession session,
			@RequestParam(defaultValue = "") Integer groupId, @RequestParam String name,
			@RequestParam String description, @RequestParam String users)
			throws Exception {
		Group group = null;
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		if (groupId != null) { //edit group
			group = modelDAO.getGroupByGroupId(groupId);
			if (group == null) {
				response.setSuccess(false);
				response.setMessage("This group does not exist");
				return response.createObjectJSON();
			}
		}
		else { //new user
			group = new Group();
		}
		group.setName(name);
		group.setDescription(description);
		List<User> userList = new ArrayList<User>();
		for (String userId : users.split(",")) {
			if (userId != null && !userId.equals("")) {
				userList.add(modelDAO.getUserByUserId(Integer.parseInt(userId)));
			}
		}
		modelDAO.saveObject(group);
		group.setUsers(userList);
		for (User u : userList) {
			List<Group> groups = u.getGroups();
			if (!groups.contains(group)) {
				groups.add(group);
			}
			modelDAO.saveObject(u);
		}
		//need to save before and after the group is created so that Hibernate can update User with the new Group
		modelDAO.saveObject(group);
		response.setSuccess(true);
		
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/saveReportGroup")
	@ResponseBody
	public String saveReportGroup(Model model, HttpSession session,
			@RequestParam(defaultValue = "") Integer reportGroupId, @RequestBody String data)
			throws Exception {
		ReportGroup reportGroup = null;
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		User user = ControllerUtil.getSessionUser(session);
		if (reportGroupId != null) { //edit reportGroup
			reportGroup = modelDAO.getReportGroupById(reportGroupId);
			if (reportGroup == null) {
				response.setSuccess(false);
				response.setMessage("This gene set does not exist");
				return response.createObjectJSON();
			}
			if (!ControllerUtil.isOwnerOrAdmin(user, reportGroup.getCreatedBy())) {
				response.setSuccess(false);
				response.setMessage("You cannot modify someone else's gene set.");
				return response.createObjectJSON();
			}
		}
		else { //new reportGroup
			reportGroup = new ReportGroup();
			reportGroup.setCreatedBy(user);
		}
		ObjectMapper mapper = new ObjectMapper();
		DataReportGroup dataPOJO = mapper.readValue(data, DataReportGroup.class);
		reportGroup.setGroupName(dataPOJO.getGroupName());
		reportGroup.setDescription(dataPOJO.getDescription());
		reportGroup.setLink(dataPOJO.getReferenceUrl());
		String[] geneNames = dataPOJO.getGenes().toUpperCase().split("[, \r\n]");
		List<String> geneNamesClean = new ArrayList<String>();
		for (String g : geneNames) {
			g = g.trim();
			if (g.length() > 0) {
				geneNamesClean.add(g);
			}
		}
		modelDAO.saveObject(reportGroup); //if new reportGroup, this will create an id in the database
		List<GeneToReport> oldGenesToReport = reportGroup.getGenesToReport();
		List<GeneToReport> genesToReport = new ArrayList<GeneToReport>();
		if (oldGenesToReport != null) {
			for (GeneToReport g : oldGenesToReport) {
				modelDAO.deleteObject(g);
			}
		}
		
		for (String g : geneNamesClean) {
			GeneToReport gene = new GeneToReport();
			gene.setGeneName(g);
			gene.setReportGroup(reportGroup);
			modelDAO.saveObject(gene);
			genesToReport.add(gene);
		}
		reportGroup.setGenesToReport(genesToReport);
		
		modelDAO.saveObject(reportGroup);
		
		response.setSuccess(true);
		
		return response.createObjectJSON();
	}
	
	@RequestMapping(value = "/deleteReportGroup")
	@ResponseBody
	public String deleteReportGroup(Model model, HttpSession session,
			@RequestParam Integer reportGroupId)
			throws Exception {
		ReportGroup reportGroup = null;
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		if (reportGroupId != null) { //edit user
			reportGroup = modelDAO.getReportGroupById(reportGroupId);
			if (reportGroup == null) {
				response.setSuccess(false);
				response.setMessage("This gene set does not exist");
				return response.createObjectJSON();
			}
			modelDAO.deleteObject(reportGroup);
			response.setSuccess(true);
		}
		return response.createObjectJSON();
	}
}
