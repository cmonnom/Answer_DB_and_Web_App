package utsw.bicf.answer.controller;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class BamViewerController {
	
	static {
		PermissionUtils.permissionPerUrl.put("bamViewer", new PermissionUtils(true, false, false));
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;

	@RequestMapping("/bamViewer")
	public String bamViewer(Model model, HttpSession session, @RequestParam String locus, 
			@RequestParam String bam,  @RequestParam String bai, @RequestParam String label) throws IOException {
		model.addAttribute("urlRedirect", "bamViewer");
		model.addAttribute("locus", locus);
		model.addAttribute("bam", bam);
		model.addAttribute("bai", bai);
		model.addAttribute("label", label);
		User user = (User) session.getAttribute("user");
		return ControllerUtil.initializeExternalModel(model, servletContext, user, "bam-viewer");
	}
	
}
