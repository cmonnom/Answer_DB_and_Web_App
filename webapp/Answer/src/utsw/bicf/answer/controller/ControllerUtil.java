package utsw.bicf.answer.controller;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import utsw.bicf.answer.model.User;

public class ControllerUtil {
	
	private static long timestamp = new Date().getTime();
	
	private static void initJSFiles(Model model, ServletContext servletContext) throws IOException {
		model.addAttribute("componentFiles", ControllerUtil.getAllComponents(servletContext));
		model.addAttribute("jsFiles", ControllerUtil.getAllJSFiles(servletContext));
	}
	
	public static String initializeModel(Model model, ServletContext servletContext, User user) throws IOException {
		initJSFiles(model, servletContext);
		if (user != null) {
			model.addAttribute("isAdmin", user.getPermission().getAdmin());
		}
		model.addAttribute("timestamp", timestamp);
		return "main-template";
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
	
	private static List<String> getAllJSFiles(ServletContext servletContext) throws IOException {
		File resourcesJs = new File( servletContext.getRealPath("/resources/js") );
		return buildFileNameList(resourcesJs);
	}
	
	private static List<String> buildFileNameList(File root) throws IOException {
		return Files.list(root.toPath())
				.map(path -> path.toFile())
				.filter(file -> !file.isDirectory() && !file.getName().equals("vue-starter.js"))
				.map(file -> file.getName())
				.collect(Collectors.toList());
	}
	
	
}
