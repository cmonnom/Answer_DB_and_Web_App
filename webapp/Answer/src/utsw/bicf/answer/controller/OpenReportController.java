package utsw.bicf.answer.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.security.FileProperties;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class OpenReportController {

	static {
		PermissionUtils.addPermission(OpenReportController.class.getCanonicalName() + ".openReport",
				IndividualPermission.CAN_REVIEW);
		PermissionUtils.addPermission(OpenReportController.class.getCanonicalName() + ".openReportReadOnly",
				IndividualPermission.CAN_VIEW);
		
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;
	@Autowired
	FileProperties fileProps;

	@RequestMapping("/openReport/{caseId}")
	public String openReport(Model model, HttpSession session, @PathVariable String caseId,
			@RequestParam(defaultValue="", required=false) String reportId
			) throws IOException, UnsupportedOperationException, URISyntaxException {
		String url = "openReport/" + caseId + "?reportId=" + reportId;
		User user = (User) session.getAttribute("user");
		model.addAttribute("urlRedirect", url);
		model.addAttribute("isProduction", fileProps.getProductionEnv());
//		RequestUtils utils = new RequestUtils(modelDAO);
//		if (user != null && !ControllerUtil.isUserAssignedToCase(utils, caseId, user)) {
//			return ControllerUtil.initializeModelNotAllowed(model, servletContext);
//		}
		
		return ControllerUtil.initializeModel(model, servletContext, user);
	}
	
	@RequestMapping("/openReportReadOnly/{caseId}")
	public String openReportReadOnly(Model model, HttpSession session, @PathVariable String caseId,
			@RequestParam(defaultValue="", required=false) String reportId) throws IOException, UnsupportedOperationException, URISyntaxException {
		String url = "openReportReadOnly/" + caseId + "?reportId=" + reportId;
		User user = (User) session.getAttribute("user");
		model.addAttribute("urlRedirect", url);
		model.addAttribute("isProduction", fileProps.getProductionEnv());
		return ControllerUtil.initializeModel(model, servletContext, user);
	}
	
	//

	
}
