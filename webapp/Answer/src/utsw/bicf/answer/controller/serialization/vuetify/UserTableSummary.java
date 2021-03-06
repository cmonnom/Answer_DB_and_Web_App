package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.ArrayList;
import java.util.List;

import utsw.bicf.answer.controller.serialization.ToolTip;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.hybrid.HeaderOrder;
import utsw.bicf.answer.model.hybrid.UserTableRow;

public class UserTableSummary extends Summary<UserTableRow>{
//	
	public UserTableSummary(List<User> users, List<HeaderOrder> headerOrders) {
		super(createUserTableRows(users), "userName", headerOrders);
	}
	
	
	private static List<UserTableRow> createUserTableRows(List<User> users) {
		List<UserTableRow> userRows = new ArrayList<UserTableRow>();
		for (User u : users) {
			userRows.add(new UserTableRow(u));
		}
		return userRows;
	}
	
	@Override
	public void initializeHeaders() {
		Header fullName = new Header("Name", "fullName");
		fullName.setAlign("left");
		headers.add(fullName);
		headers.add(new Header("User ID", "userName"));
		Header groupsHeader = new Header("Groups", "groupsConcat");
		groupsHeader.setIsSafe(true);
		headers.add(groupsHeader);
		headers.add(new Header("View", "viewValue", new ToolTip("Can a user view cases and annotations?"), true));
		headers.add(new Header("Annotate", "annotateValue", new ToolTip("Can a user create annotations?"), true));
		headers.add(new Header("Select", "selectValue", new ToolTip("Can a user select variants for reporting?"), true));
		headers.add(new Header("Assign", "assignValue", new ToolTip("Can a user assign cases to other users?"), true));
		headers.add(new Header("Review", "reviewValue", new ToolTip("Can a user review cases?"), true));
		headers.add(new Header("Hide", "hideValue", new ToolTip("Can a user hide annotations?"), true));
		headers.add(new Header("Notifications", "notificationValue", new ToolTip("Receive all notifications?"), true));
		headers.add(new Header("Admin", "adminValue", new ToolTip("Is user an admin?"), true));
		Header actions = new Header("Edit User", "actions");
		actions.setButtons(true);
		headers.add(actions);
		
	}
}
