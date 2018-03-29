package utsw.bicf.answer.controller;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import utsw.bicf.answer.controller.ControllerUtil;

@Controller
@RequestMapping("/")
public class ErrorController {
	
	@Autowired 
	ServletContext servletContext;

	@RequestMapping("/404")
	public String error(Model model, HttpSession session) throws IOException {
		ControllerUtil.initializeModel(model, servletContext);
		return "error";
	}

	

}
