package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import utsw.bicf.answer.controller.serialization.ToolTip;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.model.hybrid.UserTableRow;

public class UserTableSummary extends Summary<UserTableRow>{
//	
	public UserTableSummary(List<User> users) {
		super(createUserTableRows(users), "userName");
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
		headers.add(new Header("Name", "fullName"));
		headers.add(new Header("User ID", "userName"));
		headers.add(new Header("View", "viewValue", new ToolTip("Can view a case?"), true));
		headers.add(new Header("Edit", "editValue", new ToolTip("Can edit a case?"), true));
		headers.add(new Header("Finalize", "finalizeValue", new ToolTip("Can finalize a case?"), true));
		headers.add(new Header("Admin", "adminValue", new ToolTip("Is user an admin?"), true));
		Header actions = new Header("Edit User", "actions");
		actions.setButtons(true);
		headers.add(actions);
		
		//keep in the same order
		headerOrder = headers.stream().map(header -> header.getValue()).collect(Collectors.toList());
		
	}
}
