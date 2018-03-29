package utsw.bicf.answer.controller;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class HomeController {
	
	static {
		PermissionUtils.permissionPerUrl.put("home", new PermissionUtils(true, false, false));
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;

	@RequestMapping("/home")
	public String home(Model model, HttpSession session) throws IOException {
		model.addAttribute("urlRedirect", "home");
		return ControllerUtil.initializeModel(model, servletContext);
	}
}
