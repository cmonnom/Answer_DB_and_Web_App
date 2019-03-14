package utsw.bicf.answer.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.http.client.ClientProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import utsw.bicf.answer.controller.serialization.AjaxResponse;
import utsw.bicf.answer.controller.serialization.vuetify.OrderCaseItems;
import utsw.bicf.answer.controller.serialization.vuetify.UserLeaderBoardInfo;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.Version;
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
	}

	@Autowired 
	ServletContext servletContext;
	@Autowired
	ModelDAO modelDAO;

	@RequestMapping("/getCaseItems")
	@ResponseBody
	public String getCaseItems(Model model, HttpSession session) throws ClientProtocolException, URISyntaxException, IOException {
		try {
			//send user to Ben's API to retrieve all active cases
			RequestUtils utils = new RequestUtils(modelDAO);
			User user = ControllerUtil.getSessionUser(session);
			OrderCase[] cases = utils.getActiveCases();
			if (cases != null) {
				List<OrderCase> assignedCases = new ArrayList<OrderCase>();
				for (OrderCase c : cases) {
					if (c.getAssignedTo() != null && !c.getAssignedTo().isEmpty() && ControllerUtil.isUserAssignedToCase(c, user)
							&& c.getActive() != null && c.getActive()) {
						assignedCases.add(c);
					}
				}
				OrderCaseItems items = new OrderCaseItems(assignedCases);
				return items.createVuetifyObjectJSON();
			}
			return null;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping("/getCaseReportItems")
	@ResponseBody
	public String getCaseReportItems(Model model, HttpSession session) throws ClientProtocolException, URISyntaxException, IOException {
		try {
			//send user to Ben's API to retrieve all active cases
			RequestUtils utils = new RequestUtils(modelDAO);
			User user = ControllerUtil.getSessionUser(session);
			OrderCase[] cases = utils.getActiveCases(); //TODO limit to cases with reports
			if (cases != null) {
				List<OrderCase> casesWithReports = new ArrayList<OrderCase>();
				for (OrderCase c : cases) {
					if (CaseHistory.lastStepMatches(c, CaseHistory.STEP_REPORTING)
							&& ControllerUtil.isUserAssignedToCase(c, user)
							&& c.getActive() != null && c.getActive()) {
						casesWithReports.add(c);
					}
				}
				OrderCaseItems items = new OrderCaseItems(casesWithReports);
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
