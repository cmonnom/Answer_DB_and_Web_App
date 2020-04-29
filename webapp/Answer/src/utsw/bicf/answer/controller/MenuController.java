package utsw.bicf.answer.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.vuetify.OrderCaseItems;
import utsw.bicf.answer.controller.serialization.vuetify.UserLeaderBoardInfo;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.CaseHistory;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class MenuController {
	
	static {
		PermissionUtils.addPermission(MenuController.class.getCanonicalName() + ".getCaseItems", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(MenuController.class.getCanonicalName() + ".getCaseReportItems", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(MenuController.class.getCanonicalName() + ".getUserLeaderBoardInfo", IndividualPermission.CAN_VIEW);
		PermissionUtils.addPermission(MenuController.class.getCanonicalName() + ".isUserAssignedToCase", IndividualPermission.CAN_VIEW);
	}

	@Autowired 
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;

	@RequestMapping("/getCaseItems")
	@ResponseBody
	public String getCaseItems(Model model, HttpSession session, @RequestParam Boolean allCases) throws ClientProtocolException, URISyntaxException, IOException {
		try {
			//send user to Ben's API to retrieve all active cases
			RequestUtils utils = new RequestUtils(modelDAO);
			User user = ControllerUtil.getSessionUser(session);
			OrderCase[] cases = utils.getActiveCases();
			if (cases != null) {
				List<OrderCase> assignedCases = new ArrayList<OrderCase>();
				for (OrderCase c : cases) {
					if ((allCases && c.getActive() != null && c.getActive()) 
						|| (c.getAssignedTo() != null && !c.getAssignedTo().isEmpty() && ControllerUtil.isUserAssignedToCase(c, user)
							&& c.getActive() != null && c.getActive()
							&& !CaseHistory.lastStepMatches(c, CaseHistory.STEP_FINALIZED)
							&& !CaseHistory.lastStepMatches(c, CaseHistory.STEP_UPLOAD_TO_EPIC))) {
						assignedCases.add(c);
					}
				}
				OrderCaseItems items = new OrderCaseItems(assignedCases, user, "openCase", "openCaseReadOnly");
				return items.createVuetifyObjectJSON();
			}
			return null;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping("/isUserAssignedToCase")
	@ResponseBody
	public String isUserAssignedToCase(Model model, HttpSession session, @RequestParam String caseId) throws ClientProtocolException, URISyntaxException, IOException {
		try {
			//send user to Ben's API to retrieve all active cases
			RequestUtils utils = new RequestUtils(modelDAO);
			User user = ControllerUtil.getSessionUser(session);
			OrderCase orderCase = utils.getCaseSummary(caseId);
			AjaxResponse response = new AjaxResponse();
			response.setSuccess(false);
			response.setIsAllowed(true);
			if (orderCase != null) {
					if (ControllerUtil.isUserAssignedToCase(orderCase, user)) {
						response.setSuccess(true);
						response.setIsAllowed(true);
						response.setPayload(caseId);
					}
					else {
						response.setSuccess(true);
						response.setIsAllowed(false);
						response.setPayload(caseId);
					}
				return response.createObjectJSON();
			}
			return null;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping("/getCaseReportItems")
	@ResponseBody
	public String getCaseReportItems(Model model, HttpSession session, @RequestParam Boolean allReports) throws ClientProtocolException, URISyntaxException, IOException {
		try {
			//send user to Ben's API to retrieve all active cases
			RequestUtils utils = new RequestUtils(modelDAO);
			User user = ControllerUtil.getSessionUser(session);
			OrderCase[] cases = utils.getActiveCases(); //TODO limit to cases with reports
			if (cases != null) {
				List<OrderCase> casesWithReports = new ArrayList<OrderCase>();
				for (OrderCase c : cases) {
					if (
							(CaseHistory.lastStepMatches(c, CaseHistory.STEP_REPORTING)
									|| CaseHistory.lastStepMatches(c, CaseHistory.STEP_FINALIZED)
									|| CaseHistory.lastStepMatches(c, CaseHistory.STEP_UPLOAD_TO_EPIC))
							&& ((allReports && c.getActive() != null && c.getActive())  || ControllerUtil.isUserAssignedToCase(c, user)
							&& c.getActive() != null && c.getActive())) {
						casesWithReports.add(c);
					}
				}
				OrderCaseItems items = new OrderCaseItems(casesWithReports, user, "openReport", "openReportReadOnly");
				return items.createVuetifyObjectJSON();
			}
			return null;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Some attempt at building a leaderboard. For now the idea doesn't seem
	 * constructive enough. Don't use this method for now.
	 * @param model
	 * @param session
	 * @return
	 * @throws ClientProtocolException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	@RequestMapping("/getUserLeaderBoardInfo")
	@ResponseBody
	public String getUserLeaderBoardInfo(Model model, HttpSession session) throws ClientProtocolException, URISyntaxException, IOException {
		//send user to Ben's API to retrieve all active cases
//		RequestUtils utils = new RequestUtils(modelDAO);
		User user = ControllerUtil.getSessionUser(session);
		if (user != null) {
			UserLeaderBoardInfo lb = new UserLeaderBoardInfo(user, modelDAO);
			return lb.createObjectJSON();
		}
		return null;
	}
	
	
}
