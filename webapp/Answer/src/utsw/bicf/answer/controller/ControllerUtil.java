package utsw.bicf.answer.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.client.ClientProtocolException;
import org.springframework.ui.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.clarity.api.utils.TypeUtils;
import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.dao.LoginDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.Group;
import utsw.bicf.answer.model.LoginAttempt;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.OtherProperties;

public class ControllerUtil {
	
	private static long timestamp = new Date().getTime();
	
	public static User getSessionUser(HttpSession httpSession) {
		User user = null;
		if (httpSession.getAttribute("user") instanceof User) {
			user = (User) httpSession.getAttribute("user");
		}
		return user;
	}
	
	private static void initJSFiles(Model model, ServletContext servletContext) throws IOException {
		model.addAttribute("goodiesFiles", ControllerUtil.getAllGoodies(servletContext));
		model.addAttribute("componentFiles", ControllerUtil.getAllComponents(servletContext));
		model.addAttribute("jsFiles", ControllerUtil.getAllJSFiles(servletContext));
	}
	
	public static String initializeModel(Model model, ServletContext servletContext, User user, LoginDAO loginDAO) throws IOException {
		initJSFiles(model, servletContext);
		if (user != null) {
			model.addAttribute("permissions", user.getIndividualPermission());
			model.addAttribute("userFullName", user.getFullName());
			ObjectMapper mapper = new ObjectMapper();
			model.addAttribute("prefs", mapper.writeValueAsString(user.getUserPref()));
			model.addAttribute("showLastLogin", false); //this should allow to only display
			if (loginDAO != null) { //null for error pages
				LoginAttempt loginAttempt = loginDAO.getLoginAttemptForUser(user);
				model.addAttribute("showLastLogin", loginAttempt.getShowLastLogin());
				loginAttempt.setShowLastLogin(false); //reset the flag to only display it once. LoginController takes care of setting it to true
//				String lastLogin = loginAttempt.getLastAttemptDatetime().format(TypeUtils.localDateTimeFormatter);
				String lastLogin = TypeUtils.dateSince(loginAttempt.getLastAttemptDatetime());
				model.addAttribute("lastLogin", lastLogin);
				loginDAO.saveObject(loginAttempt);
			}
		}
		model.addAttribute("timestamp", timestamp);
		return "main-template";
	}
	
	public static String initializeExternalModel(Model model, ServletContext servletContext, User user, String template) throws IOException {
		if (user != null) {
			model.addAttribute("permissions", user.getIndividualPermission());
		}
		model.addAttribute("timestamp", timestamp);
		return template;
	}
	
	public static String initializeModelLogin(Model model, ServletContext servletContext, Method method, OtherProperties otherProps) throws IOException {
		model.addAttribute("isLogin", true);
		model.addAttribute("authMessage", otherProps.getAuthMessage());
		return initializeModel(model, servletContext, null, null);
//		initJSFiles(model, servletContext);
//		return "login";
	}
	
	public static String initializeModelError(Model model, ServletContext servletContext) throws IOException {
		initJSFiles(model, servletContext);
		return "error";
	}
	
	public static String initializeModelNotAllowed(Model model, ServletContext servletContext) throws IOException {
		initJSFiles(model, servletContext);
		return "not-allowed";
	}
	
	private static List<String> getAllComponents(ServletContext servletContext) throws IOException {
		File resourcesJs = new File( servletContext.getRealPath("/resources/js/components") );
		return buildFileNameList(resourcesJs);
	}
	
	public static List<String> getAllGoodies(ServletContext servletContext) throws IOException {
		File resourcesJs = new File( servletContext.getRealPath("/resources/js/goodies") );
		return buildFileNameList(resourcesJs);
	}
	
	private static List<String> getAllJSFiles(ServletContext servletContext) throws IOException {
		File resourcesJs = new File( servletContext.getRealPath("/resources/js") );
		return buildFileNameList(resourcesJs);
	}
	
	private static List<String> buildFileNameList(File root) throws IOException {
		List<String> files = new ArrayList<String>();
//		List<String> files = Files.list(root.toPath())
//				.map(path -> path.toFile())
//				.filter(file -> !file.isDirectory() && !file.getName().equals("vue-starter.js")
//						&& !file.getName().equals("bam-viewer.js"))
//				.map(file -> file.getName())
//				.collect(Collectors.toList());
		for (File file : root.listFiles()) {
			if (!file.isDirectory() 
					&& file.getName().endsWith(".js")
					&& !file.getName().equals("vue-starter.js")
					&& !file.getName().equals("cssrelpreload.js")
					&& !file.getName().equals("bam-viewer.js")) {
				files.add(file.getName());
			}
		}
		return files;
	}
	
	public static boolean isUserAssignedToCase(RequestUtils utils, String caseId, User user) throws ClientProtocolException, IOException, URISyntaxException {
		OrderCase caseSummary = utils.getCaseSummary(caseId);
		return (caseSummary == null || caseSummary.getAssignedTo().contains(user.getUserId().toString()));
		
	}
	
	public static boolean isUserAssignedToCase(OrderCase caseSummary, User user) throws ClientProtocolException, IOException, URISyntaxException {
		return (caseSummary == null || caseSummary.getAssignedTo().contains(user.getUserId().toString()));
	}
	
	/**
	 * This method can check if a user can modify an object (currentUser is the same as the owner)
	 * or she's an admin (and can override permissions to modify an object)
	 * @param currentUser
	 * @param owner
	 * @return
	 */
	public static boolean isOwnerOrAdmin(User currentUser, User owner) {
		return (currentUser.getIndividualPermission().getAdmin() != null 
				&& currentUser.getIndividualPermission().getAdmin())
				|| owner.equals(currentUser);
	}
	
	/**
	 * All or most pages might use the variables here.
	 * This is useful when individual controllers like "openCase" is 
	 * not called because the page loaded is "home" or "openReport"
	 */
	public static void setGlobalVariables(Model model, FileProperties fileProps, OtherProperties otherProps) {
		model.addAttribute("isProduction", otherProps.getProductionEnv());
		model.addAttribute("oncoKBGeniePortalUrl", otherProps.getOncoKBGeniePortalUrl());
		model.addAttribute("authMessage", otherProps.getAuthMessage());
	}
	
	/**
	 * Check if a user can access a case by comparing if a user is
	 * in the same group as a case
	 * @param user
	 * @param orderCase
	 * @return
	 */
	public static boolean areUserAndCaseInSameGroup(User user, OrderCase orderCase) {
		if (user == null || orderCase == null || user.getGroups() == null || orderCase.getGroupIds() == null) {
			return false;
		}
		if (user.getIndividualPermission().getAdmin()) {
			return true;
		}
		List<String> userGroups = user.getGroups().stream().map(g -> g.getGroupId() + "").collect(Collectors.toList());
		List<String> caseGroups = orderCase.getGroupIds();
		return !CollectionUtils.intersection(userGroups, caseGroups).isEmpty();
	}
	
	public static boolean areUsersInSameGroup(User user1, User user2) {
		if (user1 == null || user2 == null || user1.getGroups() == null || user2.getGroups() == null) {
			return false;
		}
		List<Integer> user1Groups = user1.getGroups().stream().map(g -> g.getGroupId()).collect(Collectors.toList());
		List<Integer> user2Groups = user2.getGroups().stream().map(g -> g.getGroupId()).collect(Collectors.toList());
		return !CollectionUtils.intersection(user1Groups, user2Groups).isEmpty();
	}
	
	public static String returnFailedGroupCheck() throws JsonProcessingException {
		AjaxResponse response = new AjaxResponse();
		response.setIsAllowed(false);
		response.setSuccess(false);
		response.setMessage("This case does not belong to your group.");
		return response.createObjectJSON();
	}
}
