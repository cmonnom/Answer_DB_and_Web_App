package utsw.bicf.answer.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.http.client.ClientProtocolException;
import org.springframework.ui.Model;

import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;

public class ControllerUtil {
	
	private static long timestamp = new Date().getTime();
	
	private static void initJSFiles(Model model, ServletContext servletContext) throws IOException {
		model.addAttribute("goodiesFiles", ControllerUtil.getAllGoodies(servletContext));
		model.addAttribute("componentFiles", ControllerUtil.getAllComponents(servletContext));
		model.addAttribute("jsFiles", ControllerUtil.getAllJSFiles(servletContext));
	}
	
	public static String initializeModel(Model model, ServletContext servletContext, User user) throws IOException {
		initJSFiles(model, servletContext);
		if (user != null) {
			model.addAttribute("permissions", user.getIndividualPermission());
			model.addAttribute("userFullName", user.getFullName());
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
	
	public static String initializeModelLogin(Model model, ServletContext servletContext, Method method) throws IOException {
		model.addAttribute("isLogin", true);
		return initializeModel(model, servletContext, null);
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
	
	private static List<String> getAllGoodies(ServletContext servletContext) throws IOException {
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
	
}
