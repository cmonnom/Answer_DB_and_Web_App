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
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;

import utsw.bicf.answer.controller.serialization.vuetify.OrderCaseItems;
import utsw.bicf.answer.dao.ModelDAO;
import utsw.bicf.answer.db.api.utils.RequestUtils;
import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.extmapping.OrderCase;
import utsw.bicf.answer.security.PermissionUtils;

@Controller
@RequestMapping("/")
public class MenuController {
	
	static {
		PermissionUtils.addPermission(MenuController.class.getCanonicalName() + ".getCaseItems", IndividualPermission.CAN_VIEW);
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
			User user = (User) session.getAttribute("user");
			OrderCase[] cases = utils.getActiveCases();
			if (cases != null) {
				List<OrderCase> assignedCases = new ArrayList<OrderCase>();
				for (OrderCase c : cases) {
					if (c.getAssignedTo() != null && !c.getAssignedTo().isEmpty() && OpenCaseController.isUserAssignedToCase(utils, c.getCaseId(), user)) {
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
}
