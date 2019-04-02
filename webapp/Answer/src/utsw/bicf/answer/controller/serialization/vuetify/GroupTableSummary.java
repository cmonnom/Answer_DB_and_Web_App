package utsw.bicf.answer.controller.serialization.vuetify;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Hibernate;

import utsw.bicf.answer.model.Group;
import utsw.bicf.answer.model.hybrid.GroupTableRow;
import utsw.bicf.answer.model.hybrid.HeaderOrder;

public class GroupTableSummary extends Summary<GroupTableRow>{

	public GroupTableSummary(List<Group> groups, List<HeaderOrder> headerOrders) {
		super(createGroupTableRows(groups), "name", headerOrders);
	}
	
	
	private static List<GroupTableRow> createGroupTableRows(List<Group> groups) {
		List<GroupTableRow> groupRows = new ArrayList<GroupTableRow>();
		for (Group g : groups) {
			groupRows.add(new GroupTableRow(g));
		}
		return groupRows;
	}


	@Override
	public void initializeHeaders() {
		headers.add(new Header("Group Name", "name"));
		Header usersHeader = new Header("Users", "usersConcat");
		usersHeader.setIsSafe(true);
		headers.add(usersHeader);
		headers.add(new Header("Description", "description"));
		Header actions = new Header("Edit Group", "actions");
		actions.setButtons(true);
		headers.add(actions);
		
	}
}
