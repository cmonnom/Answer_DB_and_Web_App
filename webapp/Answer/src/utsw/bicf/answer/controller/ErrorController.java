package utsw.bicf.answer.controller;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.OtherProperties;

@Controller
@RequestMapping("/")
public class ErrorController {
	
	@Autowired 
	ServletContext servletContext;
	@Autowired
	FileProperties fileProps;
	@Autowired
	OtherProperties otherProps;

	@RequestMapping("/404")
	public String error(Model model, HttpSession session) throws IOException {
		ControllerUtil.initializeModel(model, servletContext, null, null);
		if (ControllerUtil.getSessionUser(session) == null) {
			session.setAttribute("user", "error page");
		}
		ControllerUtil.setGlobalVariables(model, fileProps, otherProps);
		return "error";
	}

	

}
