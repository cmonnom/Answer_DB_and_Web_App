package utsw.bicf.answer.controller;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.AnnotationSearchResult;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class AnnotationBrowserController {
	
	static {
		PermissionUtils.addPermission(AnnotationBrowserController.class.getCanonicalName() + ".annotationBrowser", new PermissionUtils(true, false, false));
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;

	@RequestMapping("/annotationBrowser")
	public String annotationBrowser(Model model, HttpSession session) throws IOException {
		model.addAttribute("urlRedirect", "annotationBrowser");
		User user = (User) session.getAttribute("user");
		return ControllerUtil.initializeModel(model, servletContext, user);
	}
	
	@RequestMapping(value = "/searchForAnnotations")
	@ResponseBody
	public String searchForAnnotations(Model model, HttpSession session, 
			@RequestParam(defaultValue = "", required = false) String gene,
			@RequestParam(defaultValue = "", required = false) String variant, 
			@RequestParam(defaultValue = "", required = false) String chrom,
			@RequestParam(defaultValue = "", required = false) String leftGene, 
			@RequestParam(defaultValue = "", required = false) String rightGene, 
			@RequestParam(defaultValue = "", required = false) String variantType) throws Exception {

		RequestUtils utils = new RequestUtils(modelDAO);
		AnnotationSearchResult result = utils.getGetAnnotationsByGeneAndVariant(gene, variant);
		return result.createVuetifyObjectJSON();

	}
	
	
}
