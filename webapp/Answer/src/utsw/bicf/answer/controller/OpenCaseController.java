package utsw.bicf.answer.controller;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.vuetify.OpenCaseSummary;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class OpenCaseController {
	
	static {
		PermissionUtils.permissionPerUrl.put("openCase", new PermissionUtils(true, false, false));
		PermissionUtils.permissionPerUrl.put("getCaseDetails", new PermissionUtils(true, false, false));
	}

	@Autowired
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;

	@RequestMapping("/openCase/{caseId}")
	public String openCase(Model model, HttpSession session, @PathVariable String caseId) throws IOException {
		model.addAttribute("urlRedirect", "openCase/" + caseId);
		User user = (User) session.getAttribute("user");
		return ControllerUtil.initializeModel(model, servletContext, user);
	}
	
	
	
	@RequestMapping(value = "/getCaseDetails")
	@ResponseBody
	public String getCaseDetails(Model model, HttpSession session, @RequestParam String caseId)
			throws Exception {
		
		User user = (User) session.getAttribute("user"); //to verify that the user is assigned to the case
		//send user to Ben's API
		RequestUtils utils = new RequestUtils(modelDAO);
		OrderCase[] cases = utils.getActiveCases();
		OrderCase detailedCase = null;
		if (cases != null) {
			for (OrderCase c : cases) {
				if (c.getCaseId().equals(caseId)) {
					detailedCase = utils.getCaseDetails(caseId);
					break; //found that the case exists
				}
			}
		}
		OpenCaseSummary summary = new OpenCaseSummary(modelDAO, detailedCase, null, "chromPos");
		return summary.createVuetifyObjectJSON();
		
	}
}
