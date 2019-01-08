package utsw.bicf.answer.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.DataReportGroup;
import utsw.bicf.answer.controller.serialization.vuetify.ReportGroupTableSummary;
import utsw.bicf.answer.controller.serialization.vuetify.Summary;
import utsw.bicf.answer.controller.serialization.vuetify.UserTableSummary;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.GeneToReport;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.ReportGroup;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.hybrid.HeaderOrder;
import utsw.bicf.answer.security.FileProperties;
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

	@RequestMapping("/admin")
	public String admin(Model model, HttpSession session) throws IOException {
		model.addAttribute("urlRedirect", "admin");
		model.addAttribute("isProduction", fileProps.getProductionEnv());
		User user = (User) session.getAttribute("user");
		return ControllerUtil.initializeModel(model, servletContext, user);
	}
	
	@RequestMapping(value = "/getAllUsers")
	@ResponseBody
	public String getAllUsers(Model model, HttpSession session)
			throws Exception {

		List<User> users = modelDAO.getAllUsers();
		User user = (User) session.getAttribute("user");
		List<HeaderOrder> headerOrders = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, "Users");
		UserTableSummary summary = new UserTableSummary(users, headerOrders);
		
		return summary.createVuetifyObjectJSON();
	}
	
	@RequestMapping(value = "/getAllReportGroups")
	@ResponseBody
	public String getAllReportGroups(Model model, HttpSession session)
			throws Exception {

		List<ReportGroup> reportGroups = modelDAO.getAllReportGroups();
		User user = (User) session.getAttribute("user");
		List<HeaderOrder> headerOrders = Summary.getHeaderOrdersForUserAndTable(modelDAO, user, "Gene Sets");
		ReportGroupTableSummary summary = new ReportGroupTableSummary(reportGroups, headerOrders, user);
		
		return summary.createVuetifyObjectJSON();
	}
	
	@RequestMapping(value = "/saveUser")
	@ResponseBody
	public String saveUser(Model model, HttpSession session,
			@RequestParam(defaultValue = "") Integer userId, @RequestParam String username,
			@RequestParam String first, @RequestParam String last, @RequestParam String email,
			@RequestParam Boolean canView,
			@RequestParam Boolean canSelect, @RequestParam Boolean canAnnotate, 
			@RequestParam Boolean canAssign, @RequestParam Boolean canReview,
			@RequestParam Boolean allNotifications, @RequestParam Boolean admin)
			throws Exception {
		User user = null;
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(true);
		if (userId != null) { //edit user
			user = modelDAO.getUserByUserId(userId);
			if (user == null) {
				response.setSuccess(false);
				response.setMessage("This user does not exist");
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
		individualPermission.setCanReview(canReview);
		individualPermission.setReceiveAllNotifications(allNotifications);
		
		modelDAO.saveObject(individualPermission);
		
		modelDAO.saveObject(user);
		
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
		User user = (User) session.getAttribute("user");
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
